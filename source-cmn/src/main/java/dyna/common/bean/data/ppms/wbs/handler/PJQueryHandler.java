package dyna.common.bean.data.ppms.wbs.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainHandel;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.systemenum.ppms.DataTypeEnum;
import dyna.common.util.SetUtils;

public class PJQueryHandler extends InstanceDomainHandel<AbstractWBSDriver> implements QueryHandler
{
	public PJQueryHandler(AbstractWBSDriver bean)
	{
		super(bean);
	}

	@Override
	public FoundationObject getRootObject()
	{
		return this.getDomainBean().getRootObject();
	}

	@Override
	public FoundationObject getObject(String wbsCode)
	{
		List<FoundationObject> all = this.listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		PPMFoundationObjectUtil task = new PPMFoundationObjectUtil(null);
		if (!SetUtils.isNullList(all))
		{
			for (FoundationObject foundation : all)
			{
				task.setFoundation(foundation);
				if (task.getWBSNumber() != null && task.getWBSNumber().equals(wbsCode))
				{
					return foundation;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getPMFoundationObject(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject getObject(ObjectGuid objectGuid)
	{
		if (objectGuid == null)
		{
			return null;
		}
		else
		{
			return this.getDomainBean().getInstanceObject(FoundationObject.class, objectGuid.getGuid());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getChildList(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<FoundationObject> getChildList(ObjectGuid objectGuid)
	{
		if (objectGuid == null)
		{
			return null;
		}
		else
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
			List<FoundationObject> all = this.listAllObject(FoundationObject.class, DataTypeEnum.TASK);
			if (SetUtils.isNullList(all) == false)
			{
				List<FoundationObject> child = new ArrayList<FoundationObject>();
				for (FoundationObject foundation : all)
				{
					util.setFoundation(foundation);
					if (util.getParentTask() != null && objectGuid.getGuid().equals(util.getParentTask().getGuid()))
					{
						child.add(foundation);
					}
				}

				Collections.sort(child, new Comparator<FoundationObject>()
				{
					@Override
					public int compare(FoundationObject o1, FoundationObject o2)
					{
						if (o1.get(PPMFoundationObjectUtil.SEQUENCEID) == null)
						{
							return 0;
						}

						if (o2.get(PPMFoundationObjectUtil.SEQUENCEID) == null)
						{
							return 0;
						}

						return ((Number) o1.get(PPMFoundationObjectUtil.SEQUENCEID)).intValue() - ((Number) o2.get(PPMFoundationObjectUtil.SEQUENCEID)).intValue();
					};
				});

				return child;
			}
		}
		return null;
	}

	/*
	 * 查找所有前置任务
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getDependTaskList(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<TaskRelation> getPreTaskList(ObjectGuid objGuid)
	{
		if (objGuid == null)
		{
			return null;
		}
		else
		{
			List<TaskRelation> list = new ArrayList<TaskRelation>();
			List<TaskRelation> all = this.listAllObject(TaskRelation.class, DataTypeEnum.DEPEND);
			if (!SetUtils.isNullList(all))
			{
				for (TaskRelation xRelationObj : all)
				{
					if (objGuid.getGuid().equals(xRelationObj.getTaskObjectGuid().getGuid()))
					{
						list.add(xRelationObj);
					}
				}
			}
			return list;
		}
	}

	/*
	 * 
	 * 查找所有后置任务
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getForDependTaskList(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<TaskRelation> getPostTaskList(ObjectGuid objGuid)
	{
		if (objGuid == null)
		{
			return null;
		}
		else
		{
			List<TaskRelation> list = new ArrayList<TaskRelation>();
			List<TaskRelation> all = this.listAllObject(TaskRelation.class, DataTypeEnum.DEPEND);
			if (!SetUtils.isNullList(all))
			{
				for (TaskRelation xRelationObj : all)
				{
					if (objGuid.getGuid().equals(xRelationObj.getPreTaskObjectGuid().getGuid()))
					{
						list.add(xRelationObj);
					}
				}
			}
			return list;
		}
	}

	@Override
	public List<RoleMembers> getRoleMemberList(String projectRoleGuid)
	{
		List<RoleMembers> all = this.listAllObject(RoleMembers.class, DataTypeEnum.TEAM);
		List<RoleMembers> memberList = new ArrayList<RoleMembers>();
		if (!SetUtils.isNullList(all))
		{
			for (RoleMembers member : all)
			{
				if (projectRoleGuid.equalsIgnoreCase(member.getProjectRoleGuid()))
				{
					memberList.add(member);
				}
			}
			Collections.sort(memberList, new Comparator<RoleMembers>()
			{

				@Override
				public int compare(RoleMembers o1, RoleMembers o2)
				{
					if (o1.getSequence() == null || o2.getSequence() == null)
					{
						return 0;
					}

					return o1.getSequence() - o2.getSequence();
				}

			});
		}
		return memberList;
	}

	@Override
	public List<TaskMember> getTaskMemberList(String taskGuid)
	{
		List<TaskMember> all = this.listAllObject(TaskMember.class, DataTypeEnum.RESOURCE);
		List<TaskMember> memberList = new ArrayList<TaskMember>();
		if (!SetUtils.isNullList(all))
		{
			for (TaskMember member : all)
			{
				if (taskGuid.equalsIgnoreCase(member.getTaskObjectGuid().getGuid()))
				{
					memberList.add(member);
				}
			}
		}

		Collections.sort(memberList, new Comparator<TaskMember>()
		{

			@Override
			public int compare(TaskMember o1, TaskMember o2)
			{
				return o1.getSequence() - o2.getSequence();
			}
		});
		return memberList;
	}

	@Override
	public List<DeliverableItem> getDeliverableItemList(String taskGuid)
	{
		List<DeliverableItem> all = this.listAllObject(DeliverableItem.class, DataTypeEnum.DEVELIVE);
		List<DeliverableItem> memberList = new ArrayList<DeliverableItem>();
		if (!SetUtils.isNullList(all))
		{
			for (DeliverableItem member : all)
			{
				if (taskGuid.equalsIgnoreCase(member.getTaskGuid()))
				{
					memberList.add(member);
				}
			}
		}
		return memberList;
	}

	/**
	 * @param obj
	 * @return
	 */
	@Override
	public FoundationObject getParentTask(FoundationObject obj)
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(obj);
		return this.getObject(util.getParentTask());
	}

	@Override
	public <T extends DynaObject> List<T> listAllObject(Class<T> clazz, DataTypeEnum typeEnum)
	{
		return this.getDomainBean().getRealationInfo(clazz, typeEnum.name());
	}

	@Override
	public TaskMember getTaskMemberByUser(String taskGuid, String userGuid)
	{
		List<TaskMember> taskMemberList = this.getTaskMemberList(taskGuid);
		if (!SetUtils.isNullList(taskMemberList))
		{
			for (TaskMember member : taskMemberList)
			{
				if (member.getUserGuid().equalsIgnoreCase(userGuid))
				{
					return member;
				}
			}
		}

		return null;
	}

	@Override
	public TaskMember getMasterTaskMember(String taskGuid)
	{
		List<TaskMember> taskMemberList = this.getTaskMemberList(taskGuid);
		if (!SetUtils.isNullList(taskMemberList))
		{
			for (TaskMember member : taskMemberList)
			{
				if (member.isPersonInCharge())
				{
					return member;
				}
			}
		}
		return null;
	}

	@Override
	public List<CheckpointConfig> getCheckPointConfigList()
	{
		List<CheckpointConfig> listAllObject = this.listAllObject(CheckpointConfig.class, DataTypeEnum.MILESTONE);
		if (listAllObject != null)
		{
			Collections.sort(listAllObject, new Comparator<CheckpointConfig>()
			{

				@Override
				public int compare(CheckpointConfig o1, CheckpointConfig o2)
				{
					if (o1.getSequence() == null)
					{
						return 0;
					}
					if (o2.getSequence() == null)
					{
						return 0;
					}
					return Integer.valueOf(o1.getSequence()) - Integer.valueOf(o2.getSequence());
				}
			});
		}
		return listAllObject;
	}

	@Override
	public List<FoundationObject> getAllChildList(ObjectGuid objectGuid)
	{
		List<FoundationObject> list = new ArrayList<FoundationObject>();
		FoundationObject object = this.getObject(objectGuid);
		if (object != null)
		{
			list.add(object);
		}
		for (int i = 0; i < list.size(); i++)
		{
			List<FoundationObject> childList = this.getChildList(list.get(i).getObjectGuid());
			if (childList != null)
			{
				list.addAll(childList);
			}
		}
		if (object != null)
		{
			list.remove(object);
		}
		return list;
	}
}
