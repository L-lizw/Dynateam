/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GroupStub
 * Wanglei 2010-7-27
 */
package dyna.app.service.brs.aas;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.aas.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.AAS;
import org.acegisecurity.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

/**
 * 与group相关的操作分支
 *
 * @author Wanglei
 */
public class GroupStub extends AbstractServiceStub<AASImpl>
{
	/**
	 * @param context
	 * @param service
	 */
	public GroupStub(ServiceContext context, AASImpl service)
	{
		super(context, service);
	}

	protected void activeGroup(String groupId) throws ServiceRequestException
	{
		Group group = this.getGroupById(groupId);
		if (group == null)
		{
			throw new ServiceRequestException(null, "group " + groupId + " not found.");
		}

		List<Group> groupList = null;

		groupList = this.listSuperGroupNotActivited(group.getGuid(), groupId);

		String operatorGuid = this.stubService.getOperatorGuid();

		group.setActive(true);
		group.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(groupList))
		{
			for (Group supGroup : groupList)
			{
				this.activeGroup(supGroup.getGroupId());
			}
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			sds.save(group);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
		finally
		{
		}
	}

	protected Group getGroup(String groupGuid) throws ServiceRequestException
	{
		return DataServer.getSystemDataService().get(Group.class, groupGuid);
	}

	protected Group getGroupById(String groupId) throws ServiceRequestException
	{
		List<Group> groupList = DataServer.getSystemDataService().listFromCache(Group.class, new FieldValueEqualsFilter<Group>("GROUPID", groupId));
		if (!SetUtils.isNullList(groupList))
		{
			return groupList.get(0);
		}
		return null;
	}

	protected Group getRootGroup() throws ServiceRequestException
	{
		List<Group> groupList = DataServer.getSystemDataService().listFromCache(Group.class, null);
		for (Group group : groupList)
		{
			if (!StringUtils.isGuid(group.getParentGuid()))
			{
				return group;
			}
		}

		return null;
	}

	protected Group getSuperGroup(String groupGuid) throws ServiceRequestException
	{
		Group group = this.getGroup(groupGuid);
		if (StringUtils.isGuid(group.getParentGuid()))
		{
			Group superGroup = this.getGroup(group.getParentGuid());
			return superGroup;
		}

		return null;
	}

	protected List<Group> listAllGroup() throws ServiceRequestException
	{
		Group root = this.getGroupById("ROOT");
		root.put("LEVEL", new BigDecimal("1"));
		root.put("HIERARCHY", "/ROOT");

		List<Group> groupList = new ArrayList<Group>();
		groupList.add(root);

		this.buildHierarchy(root, groupList);

		Collections.sort(groupList, new GroupComparator("HIERARCHY", true));
		return groupList;
	}

	protected List<Group> listGroup(boolean hasRoot) throws ServiceRequestException
	{
		List<Group> groupList = new ArrayList<Group>(this.listAllGroup());
		Iterator<Group> it = groupList.iterator();
		while (it.hasNext())
		{
			Group group = it.next();
			if (!group.isActive())
			{
				it.remove();
			}
			if (!hasRoot && "ROOT".equals(group.getGroupId()))
			{
				it.remove();
			}
		}
		return groupList;
	}

	protected List<Group> listGroupByUser(String userId) throws ServiceRequestException
	{
		User user = this.stubService.getUserById(userId);
		if (user == null || !user.isActive())
		{
			return null;
		}

		List<Group> groupList = new ArrayList<Group>();
		List<URIG> urigList = this.stubService.getOrgStub().listUserRoleInGroupByUser(userId);
		if (!SetUtils.isNullList(urigList))
		{
			for (URIG urig : urigList)
			{
				RIG rig = this.stubService.getRIG(urig.getRoleGroupGuid());
				if (rig != null)
				{
					Group group = this.getGroup(rig.getGroupGuid());
					if (group != null && group.isActive())
					{
						if (!groupList.contains(group))
						{
							groupList.add(group);
						}
					}
				}
			}
		}
		return groupList;
	}

	protected List<Group> listSubGroup(String groupGuid, String groupId, boolean cascade, boolean isAll) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(groupGuid) && StringUtils.isNullString(groupId))
		{
			throw new ServiceRequestException("you should specify guid or id of group at least.");
		}

		if (StringUtils.isNullString(groupId))
		{
			Group group = this.getGroup(groupGuid);
			if (group != null)
			{
				groupId = group.getGroupId();
			}
		}

		if (StringUtils.isNullString(groupId))
		{
			return null;
		}

		if (!cascade)
		{
			return this.listSubGroup(groupId, isAll);
		}

		return this.listSubGroupHierarchy(groupId, isAll);
	}

	private List<Group> listSubGroupHierarchy(String groupId, boolean isAll) throws ServiceRequestException
	{
		Group group = this.getGroupById(groupId);
		if (group == null)
		{
			return null;
		}

		group.put("LEVEL", new BigDecimal("1"));
		group.put("HIERARCHY", "/" + group.getGroupId());

		List<Group> groupList = new ArrayList<Group>();
		this.buildHierarchy(group, groupList);

		if (!SetUtils.isNullList(groupList))
		{
			Iterator<Group> it = groupList.iterator();
			while (it.hasNext())
			{
				Group group_ = it.next();
				if (!isAll && !group_.isActive())
				{
					it.remove();
				}
			}
		}

		if (!SetUtils.isNullList(groupList))
		{
			Collections.sort(groupList, new GroupComparator("HIERARCHY", true));
			return groupList;
		}

		return null;
	}

	private List<Group> listSubGroup(String groupId, boolean isAll) throws ServiceRequestException
	{
		Group parentGroup = this.getGroupById(groupId);
		List<Group> subGroupList = new ArrayList<Group>();

		List<Group> groupList = DataServer.getSystemDataService().listFromCache(Group.class, null);
		for (Group group : groupList)
		{
			if (StringUtils.isGuid(group.getParentGuid()) && group.getParentGuid().equals(parentGroup.getGuid()))
			{
				if (isAll || group.isActive())
				{
					subGroupList.add(group);
				}
			}
		}

		if (SetUtils.isNullList(subGroupList))
		{
			return null;
		}

		Collections.sort(subGroupList, new GroupComparator(null, true));
		return subGroupList;
	}

	/**
	 * 取得指定组的父亲组
	 *
	 * @param groupGuid
	 * @param groupId
	 * @param dataType  数据类型，null：不限制；1：生效；2：失效
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Group> listSuperGroup(String groupGuid, String groupId, String dataType) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(groupGuid) && StringUtils.isNullString(groupId))
		{
			throw new ServiceRequestException("you should specify guid or id of group at least.");
		}

		Group group = null;
		if (StringUtils.isGuid(groupGuid))
		{
			group = this.getGroup(groupGuid);
		}
		else
		{
			group = this.getGroupById(groupId);
		}

		if (group == null)
		{
			return null;
		}

		group.put("LEVEL", new BigDecimal("1"));
		group.put("HIERARCHY", "/" + group.getGroupId());

		List<Group> groupList = new ArrayList<Group>();
		this.buildHierarchySuper(group, groupList);

		if (!SetUtils.isNullList(groupList))
		{
			Iterator<Group> it = groupList.iterator();
			while (it.hasNext())
			{
				Group group_ = it.next();
				if (!StringUtils.isNullString(dataType))
				{
					if ("1".equals(dataType) && !group_.isActive())
					{
						it.remove();
					}
					else if ("2".equals(dataType) && group_.isActive())
					{
						it.remove();
					}
				}
			}
		}

		if (!SetUtils.isNullList(groupList))
		{
			Collections.sort(groupList, new GroupComparator("HIERARCHY", true));
		}
		return groupList;
	}

	protected List<Group> listAllSuperGroup(String groupGuid, String groupId) throws ServiceRequestException
	{
		return this.listSuperGroup(groupGuid, groupId, null);
	}

	protected List<Group> listSuperGroup(String groupGuid, String groupId) throws ServiceRequestException
	{
		return this.listSuperGroup(groupGuid, groupId, "1");
	}

	private List<Group> listSuperGroupNotActivited(String groupGuid, String groupId) throws ServiceRequestException
	{
		return this.listSuperGroup(groupGuid, groupId, "2");
	}

	protected void obsoleteGroup(String groupId) throws ServiceRequestException
	{
		Group group = this.getGroupById(groupId);
		if (group == null)
		{
			throw new ServiceRequestException(null, "group " + groupId + " not found.");
		}

		List<Group> groupList = null;

		groupList = this.listSubGroup(group.getGuid(), groupId, true, false);

		String operatorGuid = this.stubService.getOperatorGuid();

		group.setActive(false);
		group.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		SystemDataService sds = DataServer.getSystemDataService();

		if (!SetUtils.isNullList(groupList))
		{
			for (Group subGroup : groupList)
			{
				this.obsoleteGroup(subGroup.getGroupId());
			}
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			sds.save(group);

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}
	}

	protected Group saveGroup(Group group) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		boolean isCreate = false;

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(group);

		// 新增组时，需要保存组的父子结构
		// 更新组时，不涉及道父子结构的变化，不需要处理
		boolean needSaveGroupTreeRelation = false;
		Group retGroup = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			String groupGuid = group.getGuid();

			String operatorGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			group.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (!StringUtils.isGuid(groupGuid))
			{
				isCreate = true;
				group.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			Group oldGroup = this.getGroupById(group.getGroupId());
			if (oldGroup != null && !oldGroup.getGuid().equals(groupGuid))
			{
				throw new ServiceRequestException("ID_DS_UNIQUE_ID", "id is uniqu");
			}

			String ret = sds.save(group);
			if (isCreate)
			{
				groupGuid = ret;
				needSaveGroupTreeRelation = true;
			}

			// 新建组成功后给组分派两个角色(manager与receiver)
			if (isCreate)
			{
				Role role = this.stubService.getRoleById("manager");
				if (role != null)
				{
					this.stubService.assignRoleToGroup(role.getGuid(), groupGuid);
				}

				role = this.stubService.getRoleById("receiver");
				if (role != null)
				{
					this.stubService.assignRoleToGroup(role.getGuid(), groupGuid);
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
			retGroup = this.getGroup(groupGuid);
			needSaveGroupTreeRelation = true;
			return retGroup;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e, isCreate ? group.getFullname() : group.getOriginalFullname());
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
			if (needSaveGroupTreeRelation)
			{
				this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
						.addTask(new SaveGroupTreeScheduledTask(serviceContext, retGroup, stubService.getUserSignature()));
			}
		}
	}

	private void listAllSuperGroup(String groupGuid, List<Group> superGroupList)
	{
		Group group = DataServer.getSystemDataService().get(Group.class, groupGuid);
		if (group != null)
		{
			superGroupList.add(group);
			String superGroupGuid = group.getParentGuid();
			if (StringUtils.isGuid(superGroupGuid))
			{
				this.listAllSuperGroup(superGroupGuid, superGroupList);
			}
		}
	}

	private void buildHierarchy(Group group, List<Group> groupList) throws ServiceRequestException
	{
		List<Group> subGroupList = this.listSubGroup(group.getGroupId(), true);
		if (!SetUtils.isNullList(subGroupList))
		{
			for (Group subGroup : subGroupList)
			{
				BigDecimal level = ((BigDecimal) group.get("LEVEL")).add(new BigDecimal("1"));
				subGroup.put("LEVEL", level);
				subGroup.put("HIERARCHY", (String) group.get("HIERARCHY") + "/" + subGroup.getGroupId());

				groupList.add(subGroup);
				this.buildHierarchy(subGroup, groupList);
			}
		}
	}

	private void buildHierarchySuper(Group group, List<Group> groupList)
	{
		if (!StringUtils.isGuid(group.getParentGuid()))
		{
			return;
		}

		Group superGroup = DataServer.getSystemDataService().get(Group.class, group.getParentGuid());
		if (superGroup == null)
		{
			return;
		}

		BigDecimal level = ((BigDecimal) group.get("LEVEL")).add(new BigDecimal("1"));
		superGroup.put("LEVEL", level);
		superGroup.put("HIERARCHY", (String) group.get("HIERARCHY") + "/" + superGroup.getGroupId());
		groupList.add(superGroup);

		this.buildHierarchySuper(superGroup, groupList);
	}

	protected boolean isChildGroup(String groupGuid, String parentGroupGuid) throws ServiceRequestException
	{
		if (StringUtils.isNullString(groupGuid) || !StringUtils.isGuid(parentGroupGuid))
		{
			return false;
		}

		if (groupGuid.equals(parentGroupGuid))
		{
			return true;
		}

		List<Group> children = stubService.listSubGroup(parentGroupGuid, null, true);

		if (SetUtils.isNullList(children))
		{
			return false;
		}

		for (Group child : children)
		{
			if (child.getGuid().equals(groupGuid))
			{
				return true;
			}
		}

		return false;
	}

	class GroupComparator implements Comparator<Group>
	{
		private String  sortField = null;
		private boolean isASC     = true;

		public GroupComparator(String sortField, boolean isASC)
		{
			this.sortField = StringUtils.isNullString(sortField) ? "ID" : sortField;
			this.isASC = isASC;
		}

		@Override
		public int compare(Group o1, Group o2)
		{
			if (o1.get(sortField) == null && o2.get(sortField) == null)
			{
				return 0;
			}

			if (isASC)
			{
				if (o1.get(sortField) instanceof Date || o2.get(sortField) instanceof Date)
				{
					if (o1.get(sortField) == null)
					{
						return 1;
					}
					else if (o2.get(sortField) == null)
					{
						return -1;
					}
					else
					{
						return ((Date) o1.get(sortField)).compareTo((Date) o2.get(sortField));
					}
				}
				else
				{
					String v1 = StringUtils.convertNULLtoString(o1.get(sortField));
					String v2 = StringUtils.convertNULLtoString(o2.get(sortField));
					return v1.compareTo(v2);
				}
			}
			else
			{
				if (o1.get(sortField) instanceof Date || o2.get(sortField) instanceof Date)
				{
					if (o1.get(sortField) == null)
					{
						return -1;
					}
					else if (o2.get(sortField) == null)
					{
						return 1;
					}
					else
					{
						return ((Date) o2.get(sortField)).compareTo((Date) o1.get(sortField));
					}
				}
				else
				{
					String v1 = StringUtils.convertNULLtoString(o1.get(sortField));
					String v2 = StringUtils.convertNULLtoString(o2.get(sortField));
					return v2.compareTo(v1);
				}
			}
		}
	}

	public List<Group> listGroupByBM(String bmguid)
	{
		return DataServer.getSystemDataService().listFromCache(Group.class, new FieldValueEqualsFilter<Group>("BMGUID", bmguid));
	}
}

class SaveGroupTreeScheduledTask extends AbstractScheduledTask
{
	private ServiceContext serviceContext = null;
	private Group          group          = null;
	private AAS            aas            = null;
	private UserSignature  userSignature;

	public SaveGroupTreeScheduledTask(ServiceContext serviceContext, Group group, UserSignature userSignature)
	{
		this.serviceContext = serviceContext;
		this.group = group;
		this.userSignature = userSignature;
		this.setPriority(998);
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]SaveGroupTreeScheduledTask , Scheduled Task Start...");
		try
		{
			this.aas = this.serviceContext.allocatService(AAS.class);

			TreeDataRelation relation = new TreeDataRelation();
			relation.setDataGuid(group.getGuid());
			relation.setSubDataGuid(group.getGuid());
			relation.setDataType(TreeDataRelation.DATATYPE_GROUP);
			DataServer.getSystemDataService().delete(TreeDataRelation.class,relation,"deleteBy");

			relation.setCreateUserGuid(this.userSignature.getUserGuid());
			DataServer.getSystemDataService().save(relation);

			List<Group> allSubGroupList = this.aas.listAllSubGroup(group.getGuid(), null, true);
			if (!SetUtils.isNullList(allSubGroupList))
			{
				for (Group subGroup : allSubGroupList)
				{
					relation = new TreeDataRelation();
					relation.setDataGuid(group.getGuid());
					relation.setSubDataGuid(subGroup.getGuid());
					relation.setDataType(TreeDataRelation.DATATYPE_GROUP);
					relation.setCreateUserGuid(this.userSignature.getUserGuid());
					DataServer.getSystemDataService().save(relation);
				}
			}

			List<Group> allParentGroupList = ((AASImpl) this.aas).getGroupStub().listSuperGroup(group.getGuid(), null, null);
			if (!SetUtils.isNullList(allParentGroupList))
			{
				for (Group superGroup : allParentGroupList)
				{
					relation = new TreeDataRelation();
					relation.setDataGuid(superGroup.getGuid());
					relation.setSubDataGuid(group.getGuid());
					relation.setDataType(TreeDataRelation.DATATYPE_GROUP);
					relation.setCreateUserGuid(this.userSignature.getUserGuid());
					DataServer.getSystemDataService().save(relation);
				}
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error("run send mail:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.aas != null)
			{
				this.serviceContext.releaseService(this.aas);
			}
		}
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]SaveGroupTreeScheduledTask , Scheduled Task End...");
	}

}
