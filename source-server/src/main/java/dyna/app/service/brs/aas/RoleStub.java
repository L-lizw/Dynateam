/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RoleStub
 * Wanglei 2010-7-27
 */
package dyna.app.service.brs.aas;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;

import java.util.*;

/**
 * 与角色相关的操作分支
 * 
 * @author Wanglei
 * 
 */
public class RoleStub extends AbstractServiceStub<AASImpl>
{
	/**
	 * @param context
	 * @param service
	 */
	public RoleStub(ServiceContext context, AASImpl service)
	{
		super(context, service);
	}

	protected void activeRole(String roleId) throws ServiceRequestException
	{
		Role role = this.getRoleById(roleId);
		if (role == null)
		{
			throw new ServiceRequestException(null, "role " + roleId + " not found.");
		}

		String operatorGuid = this.stubService.getOperatorGuid();

		role.setActive(true);
		role.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			SystemDataService sds = DataServer.getSystemDataService();
			sds.save(role);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e, role.getFullname());
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
	}

	protected Role getRole(String roleGuid) throws ServiceRequestException
	{
		return DataServer.getSystemDataService().get(Role.class, roleGuid);
	}

	protected Role getRoleById(String roleId) throws ServiceRequestException
	{
		List<Role> roleList = DataServer.getSystemDataService().listFromCache(Role.class, new FieldValueEqualsFilter<Role>("ROLEID", roleId));
		if (!SetUtils.isNullList(roleList))
		{
			return roleList.get(0);
		}
		return null;
	}

	protected List<Role> listAllRole(String sortField, final boolean isASC) throws ServiceRequestException
	{
		List<Role> roleList = DataServer.getSystemDataService().listFromCache(Role.class, null);

		Collections.sort(roleList, new RoleComparator(sortField, isASC));
		return roleList;
	}

	protected List<Role> listAllRoleByGroupId(String groupId) throws ServiceRequestException
	{
		List<Role> roleList = new ArrayList<Role>();
		Group group = this.stubService.getGroupById(groupId);
		if (group != null)
		{
			List<RIG> rigList = this.stubService.getOrgStub().listRIGByGroup(group.getGuid());
			if (!SetUtils.isNullList(rigList))
			{
				for (RIG rig : rigList)
				{
					String roleGuid = rig.getRoleGuid();
					Role role = this.stubService.getRole(roleGuid);
					if (role != null)
					{
						role.put(Role.ROLE_IN_GROUP_GUID, rig.getGuid());
						roleList.add(role);
					}
				}
			}
		}

		return roleList;
	}

	protected List<Role> listRole(String sortField, boolean isASC) throws ServiceRequestException
	{
		List<Role> roleList = this.listAllRole(sortField, isASC);
		if (!SetUtils.isNullList(roleList))
		{
			Iterator<Role> iterator = roleList.iterator();
			while (iterator.hasNext())
			{
				Role role = iterator.next();
				if (!role.isActive())
				{
					iterator.remove();
				}
			}
		}

		return roleList;
	}

	protected List<Role> listRoleByGroupId(String groupId) throws ServiceRequestException
	{
		List<Role> roleList = this.listAllRoleByGroupId(groupId);
		if (!SetUtils.isNullList(roleList))
		{
			Iterator<Role> iterator = roleList.iterator();
			while (iterator.hasNext())
			{
				Role role = iterator.next();
				if (!role.isActive())
				{
					iterator.remove();
				}
			}
		}

		return roleList;
	}

	protected List<Role> listRoleByGroup(String groupGuid) throws ServiceRequestException
	{
		Group group = this.stubService.getGroup(groupGuid);
		if (group != null)
		{
			return this.listRoleByGroupId(group.getGroupId());
		}
		return null;
	}

	protected List<Role> listRoleByUserInGroup(String userId, String groupId) throws ServiceRequestException
	{
		List<Role> roleList = new ArrayList<Role>();
		User user = this.stubService.getUserById(userId);
		Group group = this.stubService.getGroupById(groupId);
		if (user == null || group == null || !user.isActive() || !group.isActive())
		{
			return null;
		}

		List<RIG> rigList = this.stubService.getUserStub().listRIGOfUser(userId, false);
		if (!SetUtils.isNullList(rigList))
		{
			for (RIG rig : rigList)
			{
				Role role = this.getRole(rig.getRoleGuid());
				if (role.isActive())
				{
					roleList.add(role);
				}
			}
		}

		Collections.sort(roleList, new RoleComparator(null, true));

		return roleList;
	}

	protected void obsoleteRole(String roleId) throws ServiceRequestException
	{
		Role role = this.getRoleById(roleId);
		if (role == null)
		{
			throw new ServiceRequestException(null, "role " + roleId + " not found.");
		}
		if ("ADMINISTRATOR".equalsIgnoreCase(roleId) || "receiver".equalsIgnoreCase(roleId) || "manager".equalsIgnoreCase(roleId))
		{
			throw new ServiceRequestException("ID_APP_ROLESTUB_ROLE_NOT_OBSOLETE", "role can't obsolete", null, roleId);
		}
		String operatorGuid = this.stubService.getOperatorGuid();

		role.setActive(false);
		role.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			SystemDataService sds = DataServer.getSystemDataService();
			sds.save(role);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e, role.getFullname());
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
	}

	protected Role saveRole(Role role) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		boolean isCreate = false;

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(role);

		try
		{
			Role retRole = null;

			String roleGuid = role.getGuid();

			String operatorGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			role.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (!StringUtils.isGuid(roleGuid))
			{
				isCreate = true;
				role.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			Role oldRole = this.getRoleById(role.getRoleId());
			if (oldRole != null && !oldRole.getGuid().equals(roleGuid))
			{
				throw new ServiceRequestException("ID_DS_UNIQUE_ID", "id is uniqu");
			}

			String ret = sds.save(role);

			if (isCreate)
			{
				roleGuid = ret;
			}

			retRole = this.getRole(roleGuid);

			return retRole;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e, isCreate ? role.getFullname() : role.getOriginalFullname());
		}
	}

	class RoleComparator implements Comparator<Role>
	{
		private String	sortField	= null;
		private boolean	isASC		= true;

		public RoleComparator(String sortField, boolean isASC)
		{
			this.sortField = StringUtils.isNullString(sortField) ? "ID" : sortField;
			this.isASC = isASC;
		}

		@Override
		public int compare(Role o1, Role o2)
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
}
