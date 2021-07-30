package dyna.common.bean.data.ppms.wbs.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Stack;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.WarningNotifier;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainBean;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainHandel;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainUpdateBean;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.bean.data.ppms.wbs.ProjectWBSDriver;
import dyna.common.bean.data.ppms.wbs.TaskCompareByWBSCode;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DomainSyncModeEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.DataTypeEnum;
import dyna.common.systemenum.ppms.OnTimeStateEnum;
import dyna.common.systemenum.ppms.PMAuthorityEnum;
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.systemenum.ppms.WBSOperateEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class PJUpdateHandler extends InstanceDomainHandel<AbstractWBSDriver> implements UpdateHandler
{
	public PJUpdateHandler(AbstractWBSDriver bean)
	{
		super(bean);
	}

	public WBSPrepareContain getPrepareContain()
	{
		return this.getDomainBean().prepareContain;
	}

	@Override
	public void addTask(FoundationObject parent, FoundationObject foundationObject, int index) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(parent);
		if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE || this.getDomainBean().getCheckHandler().isSubProject(parent))
		{
			throw new ServiceRequestException("ID_PM_NOT_ADD_TASK", "not add task");
		}

		this.getDomainBean().getCheckHandler().hasPMAuthority(parent, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());
		List<FoundationObject> list = this.getDomainBean().queryHandler.getChildList(parent.getObjectGuid());

		if (list == null)
		{
			util.setFoundation(foundationObject);
			util.setSequence(0);
		}
		else
		{
			if (index < 0 || index >= list.size())
			{
				list.add(foundationObject);
			}
			else
			{
				list.add(index, foundationObject);
			}
			int i = 0;
			for (FoundationObject object : list)
			{
				util.setFoundation(object);
				util.setSequence(i++);
			}
		}

		this.addToDomain(DataTypeEnum.TASK.name(), foundationObject);

		PPMFoundationObjectUtil parentU = new PPMFoundationObjectUtil(parent);
		PPMFoundationObjectUtil pmObj = new PPMFoundationObjectUtil(foundationObject);
		if (pmObj.getPlanStartTime() == null)
		{
			pmObj.setPlanStartTime(parentU.getPlanStartTime());
		}
		if (pmObj.getPlannedDuration() == null)
		{
			pmObj.setPlannedDuration(1);
		}
		if (pmObj.getPlanStartTime() != null)
		{
			pmObj.setPlanFinishTime(this.getPrepareContain().getProjectCalendar().addDay(pmObj.getPlanStartTime(), pmObj.getPlannedDuration() - 1));
		}
		if (!this.getDomainBean().getRootObject().getObjectGuid().getGuid().equals(pmObj.getOwnerProject().getGuid()) && pmObj.getOwnerProject().getGuid() != null)
		{
			pmObj.setPlanWorkload(null);
			pmObj.setExecutor(null);
			pmObj.setExecuteRole(null);
			pmObj.setExecutorName(null);
			pmObj.setExecuteRoleName(null);
		}

		pmObj.setEarliestStartTime(null);
		pmObj.setEarliestFinishTime(null);
		pmObj.setEarliestFinishTime(null);
		pmObj.setActualFinishTime(null);
		pmObj.setActualStartTime(null);
		pmObj.setHasDeliveryItem(null);
		pmObj.setCompletionRate(null);
		foundationObject.setStatus(SystemStatusEnum.WIP);
		foundationObject.setLifecyclePhaseGuid(null);

		CodeItemInfo onTimeCodeItem = this.getPrepareContain().getOnTimeStateFromEnum(OnTimeStateEnum.NOTSTARTED);
		if (onTimeCodeItem != null)
		{
			pmObj.setOnTimeState(onTimeCodeItem.getGuid());
			pmObj.setOnTimeStateEnum(OnTimeStateEnum.NOTSTARTED);
		}

		CodeItemInfo taskStatusCodeItem = this.getPrepareContain().getTaskStatusCodeItem(TaskStatusEnum.INI);
		pmObj.setTaskStatus(taskStatusCodeItem.getGuid());
		pmObj.setTaskStatusName(taskStatusCodeItem.getName());
		foundationObject.put(PPMFoundationObjectUtil.EXECUTESTATUS + "$TITLE", taskStatusCodeItem.getTitle());

		foundationObject.setStatus(SystemStatusEnum.WIP);
		pmObj.setParentTask(parent.getObjectGuid());
		pmObj.setOwnerProject(this.getDomainBean().getRootObject().getObjectGuid());
		pmObj.setPredecessorRelation(null);
		parentU.setHasDeliveryItem(null);
		pmObj.setHasDeliveryItem(null);
		if (parent != this.getDomainBean().getRootObject())
		{
			List<TaskMember> taskResourcelist = this.getDomainBean().getQueryHandler().getTaskMemberList(parent.getObjectGuid().getGuid());
			if (SetUtils.isNullList(taskResourcelist) == false)
			{
				for (TaskMember member : taskResourcelist)
				{
					this.removeTaskMember(member);
				}
			}
			this.updateExcutorRole(parentU, null, false);

			List<DeliverableItem> taskDeliverableItemList = this.getDomainBean().getQueryHandler().getDeliverableItemList(parent.getObjectGuid().getGuid());
			if (SetUtils.isNullList(taskDeliverableItemList) == false)
			{
				for (DeliverableItem item : taskDeliverableItemList)
				{
					this.removeDeliverableItem(item);
				}
			}
		}

		CodeItemInfo taskTypeCodeItem = this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL);
		foundationObject.put(PPMFoundationObjectUtil.TASKTYPE, taskTypeCodeItem.getGuid());
		foundationObject.put(PPMFoundationObjectUtil.TASKTYPE + PMConstans.NAME, taskTypeCodeItem.getCode());
		foundationObject.put(PPMFoundationObjectUtil.TASKTYPE + "$TITLE", taskTypeCodeItem.getTitle());

		this.getDomainBean().getCalculateHandler().updateWBScode(parent);
		this.getDomainBean().getCalculateHandler().updateTaskPreInfo();
	}

	@Override
	public void deleteTask(FoundationObject foundationObject) throws ServiceRequestException
	{
		this.getDomainBean().getCheckHandler().hasPMAuthority(foundationObject, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
		if (util.getTaskStatusEnum() != TaskStatusEnum.INI)
		{
			throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_RUN", "Task Status is RUN.Does not Need Modify");
		}
		FoundationObject parentTask = this.getDomainBean().getQueryHandler().getParentTask(foundationObject);
		List<FoundationObject> childList = this.getDomainBean().getQueryHandler().getAllChildList(foundationObject.getObjectGuid());
		childList.add(foundationObject);

		for (FoundationObject o : childList)
		{
			// 删除依赖
			List<TaskRelation> postTaskList = this.getDomainBean().getQueryHandler().getPostTaskList(o.getObjectGuid());
			if (!SetUtils.isNullList(postTaskList))
			{
				this.deleteFromDomain(DataTypeEnum.DEPEND.name(), postTaskList);
			}

			List<TaskRelation> preTaskList = this.getDomainBean().getQueryHandler().getPreTaskList(o.getObjectGuid());
			if (!SetUtils.isNullList(preTaskList))
			{
				this.deleteFromDomain(DataTypeEnum.DEPEND.name(), preTaskList);
			}

			// 删除交付项
			List<DeliverableItem> deliverableItemList = this.getDomainBean().getQueryHandler().getDeliverableItemList(o.getGuid());
			if (!SetUtils.isNullList(deliverableItemList))
			{
				for (DeliverableItem item : deliverableItemList)
				{
					this.removeDeliverableItem(item);
				}
			}

			// 删除资源
			List<TaskMember> taskMemberList = this.getDomainBean().getQueryHandler().getTaskMemberList(o.getGuid());
			if (!SetUtils.isNullList(taskMemberList))
			{
				for (TaskMember item : taskMemberList)
				{
					this.removeTaskMember(item);
				}
			}

		}

		this.deleteFromDomain(DataTypeEnum.TASK.name(), childList);

		util.setFoundation(foundationObject);
		childList = this.getDomainBean().getQueryHandler().getChildList(util.getParentTask());
		if (SetUtils.isNullList(childList))
		{
			FoundationObject object = this.getDomainBean().getQueryHandler().getObject(util.getParentTask());
			util.setFoundation(object);
			CodeItemInfo taskTypeCodeItem = this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL);
			util.setTaskType(taskTypeCodeItem.getGuid());
			util.setTaskTypeName(taskTypeCodeItem.getName());
			util.setPlannedDuration(null);
			util.setPlanWorkload(null);
		}

		if (parentTask == null)
		{
			parentTask = this.getDomainBean().getRootObject();
		}

		this.relateTaskMilestone();
		this.getDomainBean().getCalculateHandler().updateWBScode(parentTask);
		this.getDomainBean().getCalculateHandler().updateTaskPreInfo();
	}

	@Override
	public void addPreTask(TaskRelation taskRelation) throws ServiceRequestException
	{
		this.addPreTask(taskRelation, true);
	}

	@Override
	public void addPreTask(TaskRelation taskRelation, boolean checkLoop) throws ServiceRequestException
	{
		FoundationObject object = this.getDomainBean().getQueryHandler().getObject(taskRelation.getTaskObjectGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(object, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		List<TaskRelation> preTaskList = this.getDomainBean().queryHandler.getPreTaskList(taskRelation.getTaskObjectGuid());
		if (!SetUtils.isNullList(preTaskList))
		{
			for (TaskRelation oldRelation : preTaskList)
			{
				if (taskRelation.getPreTaskObjectGuid().getGuid().equalsIgnoreCase(oldRelation.getPreTaskObjectGuid().getGuid()))
				{
					oldRelation.setDelayTime(taskRelation.getDelayTime());
					oldRelation.setDependType(taskRelation.getDependType());
					FoundationObject task = this.getDomainBean().getQueryHandler().getObject(taskRelation.getTaskObjectGuid());
					this.getDomainBean().getCalculateHandler().updateTaskPreInfo(task);
					return;
				}
			}
		}

		taskRelation.setProjectObjectGuid(this.getDomainBean().getRootObject().getObjectGuid());
		FoundationObject task = this.getDomainBean().getQueryHandler().getObject(taskRelation.getTaskObjectGuid());

		try
		{
			this.addToDomain(DataTypeEnum.DEPEND.name(), taskRelation);
			if (checkLoop)
			{
				this.getDomainBean().getCheckHandler().checkSummayDepend(task);
				Stack<String> taskStack = new Stack<String>();
				this.getDomainBean().getCheckHandler().checkDependLoop(taskRelation.getTaskObjectGuid(), taskRelation.getPreTaskObjectGuid(), taskStack, false);
				taskStack.clear();
			}
			this.getDomainBean().getCalculateHandler().updateTaskPreInfo(task);

		}
		catch (ServiceRequestException e)
		{
			this.deleteFromDomain(DataTypeEnum.DEPEND.name(), taskRelation);
			throw e;
		}
	}

	@Override
	public void removePreTask(TaskRelation taskRelation) throws ServiceRequestException
	{
		FoundationObject object = this.getDomainBean().queryHandler.getObject(taskRelation.getTaskObjectGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(object, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		if (StringUtils.isNullString(taskRelation.getTaskObjectGuid().getGuid()))
		{
			return;
		}
		this.deleteFromDomain(DataTypeEnum.DEPEND.name(), taskRelation);
		this.getDomainBean().calculateHandler.updateTaskPreInfo(object);

	}

	@Override
	public void exchangePosition(FoundationObject preObject, FoundationObject nextObject) throws ServiceRequestException
	{
		this.getDomainBean().getCheckHandler().hasPMAuthority(preObject, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(nextObject, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		FoundationObject parent1 = this.getDomainBean().getQueryHandler().getParentTask(preObject);
		FoundationObject parent2 = this.getDomainBean().getQueryHandler().getParentTask(nextObject);
		if (parent1 == parent2)
		{
			List<FoundationObject> list = this.getDomainBean().getQueryHandler().getChildList(parent1.getObjectGuid());
			if (SetUtils.isNullList(list) == false)
			{
				int index1 = list.indexOf(preObject);
				int index2 = list.indexOf(nextObject);
				if (index1 > -1 && index2 > -1)
				{
					list.set(index1, nextObject);
					list.set(index2, preObject);

					int i = 0;
					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
					for (FoundationObject object : list)
					{
						util.setFoundation(object);
						util.setSequence(i++);
					}

					List<TaskRelation> prelist = this.getDomainBean().queryHandler.getPreTaskList(preObject.getObjectGuid());
					if (prelist != null)
					{
						for (TaskRelation stru : prelist)
						{
							this.getDomainBean().getUpdateHandler().removePreTask(stru);
						}
					}

					prelist = this.getDomainBean().queryHandler.getPostTaskList(preObject.getObjectGuid());
					if (prelist != null)
					{
						for (TaskRelation stru : prelist)
						{
							this.getDomainBean().getUpdateHandler().removePreTask(stru);
						}
					}

					this.getDomainBean().getCalculateHandler().updateWBScode(parent1);
				}
			}
		}
	}

	@Override
	public void degrade(FoundationObject preObject) throws ServiceRequestException
	{
		FoundationObject pparent = this.getDomainBean().getQueryHandler().getParentTask(preObject);
		PPMFoundationObjectUtil parentU = new PPMFoundationObjectUtil(pparent);

		List<FoundationObject> list = this.getDomainBean().getQueryHandler().getChildList(pparent.getObjectGuid());
		if (SetUtils.isNullList(list) == false)
		{
			int index = list.indexOf(preObject);
			if (index == 0)
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_IS_FIRST", "Task is first,can't move up  or degrade");
			}
			else
			{
				FoundationObject parent = list.get(index - 1);
				this.getDomainBean().getCheckHandler().hasPMAuthority(parent, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());
				parentU.setFoundation(parent);
				if (parentU.getTaskTypeEnum() == TaskTypeEnum.MILESTONE || this.getDomainBean().getCheckHandler().isSubProject(parent))
				{
					throw new ServiceRequestException("ID_PM_NOT_DEGRADE_TASK", "parent task is milestone or subproject");
				}
				try
				{
					list.remove(index);
					list = this.getDomainBean().getQueryHandler().getChildList(parent.getObjectGuid());
					parentU.setFoundation(preObject);
					parentU.setParentTask(parent.getObjectGuid());
					if (!SetUtils.isNullList(list))
					{
						int lastSequenceId = Integer.parseInt(list.get(list.size() - 1).get(PPMFoundationObjectUtil.SEQUENCEID).toString());
						parentU.setSequence(lastSequenceId + 1);
					}
					List<TaskRelation> plist = this.getDomainBean().queryHandler.getPreTaskList(preObject.getObjectGuid());
					if (plist != null)
					{
						for (TaskRelation stru : plist)
						{
							this.getDomainBean().getUpdateHandler().removePreTask(stru);
						}
					}

					plist = this.getDomainBean().queryHandler.getPostTaskList(preObject.getObjectGuid());
					if (plist != null)
					{
						for (TaskRelation stru : plist)
						{
							this.getDomainBean().getUpdateHandler().removePreTask(stru);
						}
					}

					parentU.setFoundation(parent);

					List<TaskMember> taskResourcelist = this.getDomainBean().getQueryHandler().getTaskMemberList(parent.getObjectGuid().getGuid());
					if (SetUtils.isNullList(taskResourcelist) == false)
					{
						for (TaskMember member : taskResourcelist)
						{
							this.removeTaskMember(member);
						}
					}
					this.updateExcutorRole(parentU, null, false);
					List<DeliverableItem> devItemlist = this.getDomainBean().getQueryHandler().getDeliverableItemList(parent.getObjectGuid().getGuid());
					if (SetUtils.isNullList(devItemlist) == false)
					{
						for (DeliverableItem item : devItemlist)
						{
							this.removeDeliverableItem(item);
						}
					}
					this.getDomainBean().calculateHandler.updateWBScode(pparent);
					parentU.setHasDeliveryItem(null);
				}
				catch (ServiceRequestException e)
				{
					parentU.setFoundation(preObject);
					parentU.setParentTask(pparent.getObjectGuid());
					list.remove(preObject);
					list = this.getDomainBean().queryHandler.getChildList(pparent.getObjectGuid());
					list.add(index, preObject);
					throw e;
				}
			}
		}
		else
		{
			throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_IS_ROOT", "Task is Root,can't upgrade or degrade");
		}
	}

	@Override
	public void removeRoleMember(RoleMembers roleMember) throws ServiceRequestException
	{

		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());
		ProjectRole projectRole = this.getDomainBean().getInstanceObject(ProjectRole.class, roleMember.getProjectRoleGuid());
		if (projectRole.isManger())
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(this.getDomainBean().getRootObject());
			if (roleMember.getUserGuid().equals(util.getExecutor()))
			{
				throw new ServiceRequestException("ID_APP_REMOVE_PM_ERROR", "please modify executor of project instance");
			}
		}
		List<TaskMember> listAllObject = this.getDomainBean().getQueryHandler().listAllObject(TaskMember.class, DataTypeEnum.RESOURCE);
		if (!SetUtils.isNullList(listAllObject))
		{
			for (TaskMember member : listAllObject)
			{
				if (member.getUserGuid() != null && member.getUserGuid().equals(roleMember.getUserGuid()) && member.getProjectRole() != null
						&& member.getProjectRole().equals(roleMember.getProjectRoleGuid()))
				{

					FoundationObject object = this.getDomainBean().getQueryHandler().getObject(member.getTaskObjectGuid());
					if (!this.getDomainBean().getCheckHandler().hasPMAuthorityNoException(object, PMAuthorityEnum.TASK_TEAMRESOURCES, this.getDomainBean().getOperatorGuid()))
					{
						throw new ServiceRequestException("ID_CLIENT_PM_FRAMEWORK_NO_DELETE_AUTH", "no authority to remove member ");
					}
				}
			}
		}
		if (!SetUtils.isNullList(listAllObject))
		{
			for (TaskMember member : listAllObject)
			{
				if (member.getUserGuid() != null && member.getUserGuid().equals(roleMember.getUserGuid()) && member.getProjectRole() != null
						&& member.getProjectRole().equals(roleMember.getProjectRoleGuid()))
				{
					this.removeTaskMember(member);
				}
			}
		}
		// 取得该角色下所有的成员
		this.deleteFromDomain(DataTypeEnum.TEAM.name(), roleMember);

	}

	// 任务资源相关方法
	@Override
	public void addTaskMember(ObjectGuid taskObjectGuid, TaskMember taskMember) throws ServiceRequestException
	{
		FoundationObject taskFoundationObject = this.getDomainBean().queryHandler.getObject(new ObjectGuid(taskObjectGuid.getClassGuid(), null, taskObjectGuid.getGuid(), null));
		this.getDomainBean().getCheckHandler().hasPMAuthority(taskFoundationObject, PMAuthorityEnum.TASK_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());
		taskMember.setTaskObjectGuid(taskObjectGuid);
		if (taskFoundationObject != null)
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(taskFoundationObject);
			if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
			{
				throw new ServiceRequestException("ID_APP_SUMMARY_RESOURCES_ADD", "summary task do not add resources");
			}
		}

		if (taskMember.isPersonInCharge())
		{
			TaskMember masterTaskMember = this.getDomainBean().queryHandler.getMasterTaskMember(taskObjectGuid.getGuid());
			if (masterTaskMember != null)
			{
				masterTaskMember.setPersonInCharge(false);
			}
		}

		this.addToDomain(DataTypeEnum.RESOURCE.name(), taskMember);
		if (taskMember.isPersonInCharge())
		{
			FoundationObject object = this.getDomainBean().queryHandler.getObject(taskObjectGuid);
			if (object != null)
			{
				PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
				util.setExecutor(taskMember.getUserGuid());
				util.setExecutorName(taskMember.getUserName());
				util.setExecuteRole(taskMember.getProjectRole());
				util.setExecuteRoleName(taskMember.getProjectRoleName());

			}
		}

	}

	@Override
	public void removeProjectRole(ProjectRole projectRole) throws ServiceRequestException
	{

		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());
		if (projectRole.isManger())
		{
			throw new ServiceRequestException("ID_PM_REMOVE_PROJECT_MANAGER_ROLE", "not delete PM role");
		}
		if (PMConstans.PROJECT_OBSERVER_ROLE.equals(projectRole.getRoleId()))
		{
			throw new ServiceRequestException("ID_PM_REMOVE_PROJECT_RECEIVER_ROLE", "not delete receiver role");
		}
		// 取得该角色下所有的成员
		List<RoleMembers> listRoleMembers = this.getDomainBean().queryHandler.getRoleMemberList(projectRole.getGuid());
		// 无成员,直接删除角色
		if (SetUtils.isNullList(listRoleMembers))
		{
			this.deleteFromDomain(DataTypeEnum.ROLE.name(), projectRole);
			FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
			List<FoundationObject> list = this.getDomainBean().queryHandler.getAllChildList(project.getObjectGuid());
			if (!SetUtils.isNullList(list))
			{
				for (FoundationObject task : list)
				{
					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
					if (projectRole.getGuid().equals(util.getExecuteRole()))
					{
						util.setExecuteRole(null);
						util.setExecuteRoleName(null);
					}
				}
			}
		}
		else
		{
			throw new ServiceRequestException("ID_PM_REMOVE_PROJECT_ROLE_ERROR", "not delete role");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.client.projectmanage.util.WBSModel#upgrade(dyna.common.bean.data.ppm.AbstractPMFoundationObject)
	 */
	@Override
	public void upgrade(FoundationObject preObject) throws ServiceRequestException
	{
		FoundationObject parent = this.getDomainBean().queryHandler.getParentTask(preObject);
		this.getDomainBean().getCheckHandler().hasPMAuthority(parent, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		List<FoundationObject> list = this.getDomainBean().queryHandler.getChildList(parent.getObjectGuid());
		if (SetUtils.isNullList(list) == false)
		{
			FoundationObject pparent = this.getDomainBean().queryHandler.getParentTask(parent);

			List<FoundationObject> plist = this.getDomainBean().queryHandler.getChildList(pparent.getObjectGuid());
			if (SetUtils.isNullList(plist) == false)
			{
				int index = list.indexOf(preObject);
				int index_p = plist.indexOf(parent);
				try
				{
					list.remove(index);
					plist.add(index_p + 1, preObject);
					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
					util.setFoundation(preObject);
					util.setParentTask(pparent.getObjectGuid());

					int i = 0;
					for (FoundationObject object : list)
					{
						util.setFoundation(object);
						util.setSequence(i++);
					}
					i = 0;
					for (FoundationObject object : plist)
					{
						util.setFoundation(object);
						util.setSequence(i++);
					}

					List<TaskRelation> prelist = this.getDomainBean().queryHandler.getPreTaskList(preObject.getObjectGuid());
					if (prelist != null)
					{
						for (TaskRelation stru : prelist)
						{
							this.getDomainBean().getUpdateHandler().removePreTask(stru);
						}
					}

					prelist = this.getDomainBean().queryHandler.getPostTaskList(preObject.getObjectGuid());
					if (prelist != null)
					{
						for (TaskRelation stru : prelist)
						{
							this.getDomainBean().getUpdateHandler().removePreTask(stru);
						}
					}

					if (SetUtils.isNullList(list))
					{
						util.setFoundation(parent);
						CodeItemInfo taskTypeCodeItem = this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL);
						util.setTaskType(taskTypeCodeItem.getGuid());
						util.setTaskTypeName(taskTypeCodeItem.getName());
						util.setPlanWorkload(null);
					}

					this.getDomainBean().calculateHandler.updateWBScode(pparent);
					this.getDomainBean().calculateHandler.updateTaskPreInfo();

				}
				catch (ServiceRequestException e)
				{
					list.add(index, preObject);
					plist.remove(preObject);

					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
					util.setFoundation(preObject);
					util.setParentTask(parent.getObjectGuid());

					int i = 0;
					for (FoundationObject object : list)
					{
						util.setFoundation(object);
						util.setSequence(i++);
					}
					i = 0;
					for (FoundationObject object : plist)
					{
						util.setFoundation(object);
						util.setSequence(i++);
					}
					throw e;
				}
			}
			else
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_CHILD_LEVEL_FIRST", "level of Task is 1,can't upgrade");
			}
		}
		else
		{
			throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_IS_ROOT", "Task is Root,can't upgrade or degrade");
		}
	}

	@Override
	public void reschedule(FoundationObject task, Object planStartTime1, Object planFinishTime1, double percent, int duration) throws ServiceRequestException
	{
		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		Date planStartTime = (Date) planStartTime1;
		Date planFinishTime = (Date) planFinishTime1;
		if (percent != 0)
		{
			percent = percent / 100;
		}

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		if (planStartTime != null && planFinishTime != null)
		{
			PMCalendar projectCalendar = this.getPrepareContain().getProjectCalendar();
			duration = projectCalendar.getDurationDay(planStartTime, planFinishTime) + 1;
			util.setPlanStartTime(planStartTime);

		}

		if (duration != 0)
		{
			percent = 1.0 * duration / util.getPlannedDuration();
		}

		if (percent != 0)
		{
			this.reCalculateDuration(task, percent);
			this.getDomainBean().calculateHandler.calculatePlanStartTime(task, null);
			this.getDomainBean().getCalculateHandler().updatePlanWorkload();
		}
	}

	protected void reCalculateDuration(FoundationObject task, Double percent)
	{
		if (task == null)
		{
			return;
		}
		List<FoundationObject> childList = this.getDomainBean().queryHandler.getChildList(task.getObjectGuid());
		if (!SetUtils.isNullList(childList))
		{
			for (FoundationObject foundation : childList)
			{
				this.reCalculateDuration(foundation, percent);
			}
		}
		else
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
			if (util.getTaskStatusEnum() == TaskStatusEnum.INI || util.getProjectStatusEnum() == ProjectStatusEnum.INI)
			{
				int plannedDuration = util.getPlannedDuration();
				plannedDuration = (int) Math.round(plannedDuration * percent);
				util.setPlannedDuration(plannedDuration);
			}
		}

	}

	@Override
	public void replacePerformer(RoleMembers oriRoleMemberP, RoleMembers newRoleMemberP) throws ServiceRequestException
	{
		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());

		String oriUserGuid = oriRoleMemberP.getUserGuid();
		String oriRoleGuid = oriRoleMemberP.getProjectRoleGuid();
		String oriRoleName = oriRoleMemberP.getRoleName();
		String newUserGuid = newRoleMemberP.getUserGuid();
		String newRoleGuid = newRoleMemberP.getProjectRoleGuid();
		User newUser = newRoleMemberP.getUser();

		if (newUserGuid == null || oriUserGuid == null || oriRoleGuid == null)
		{
			return;
		}

		RoleMembers newRoleMember = null;

		// 新用户不存在于原团队中
		if (StringUtils.isNullString(newRoleGuid))
		{
			// 新人+旧角色
			RoleMembers member = new RoleMembers();
			member.setProjectRoleGuid(oriRoleGuid);
			member.setRoleName(oriRoleName);

			member.setUserGuid(newUserGuid);
			member.setUser(newUser);

			this.addToDomain(DataTypeEnum.TEAM.name(), member);
			newRoleMember = member;
		}
		else
		{
			newRoleMember = newRoleMemberP;
		}

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
		List<FoundationObject> allTaskList = this.getDomainBean().queryHandler.listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		if (!SetUtils.isNullList(allTaskList))
		{
			for (FoundationObject foundation : allTaskList)
			{
				util.setFoundation(foundation);
				if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP || util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
				{
					continue;
				}

				// 修改任务
				if (oriUserGuid.equalsIgnoreCase(util.getExecutor()))
				{
					util.setExecutor(newRoleMember.getUserGuid());
					util.setExecutorName(newRoleMember.getUser().getName());
					util.setExecuteRole(newRoleMember.getProjectRoleGuid());
					util.setExecuteRoleName(newRoleMember.getRoleName());
				}
				else
				{
					continue;
				}

				// 删除原资源
				TaskMember taskMemberByUser = this.getDomainBean().queryHandler.getTaskMemberByUser(foundation.getGuid(), oriUserGuid);
				if (taskMemberByUser != null)
				{
					this.deleteFromDomain(DataTypeEnum.RESOURCE.name(), taskMemberByUser);
				}

				TaskMember newTaskMemberByUser = this.getDomainBean().queryHandler.getTaskMemberByUser(foundation.getGuid(), newUserGuid);
				if (newTaskMemberByUser != null)
				{
					// 修改资源
					newTaskMemberByUser.setPersonInCharge(true);
				}
				else
				{
					// 新增新资源
					TaskMember taskMember = new TaskMember();
					taskMember.setUserGuid(newRoleMember.getUserGuid());
					taskMember.setUserName(newUser.getName());
					taskMember.setPersonInCharge(true);
					taskMember.setProjectRole(newRoleMember.getProjectRoleGuid());
					taskMember.setProjectRoleName(newRoleMember.getRoleName());
					taskMember.setTaskObjectGuid(foundation.getObjectGuid());
					taskMember.setRate(100D);
					this.getDomainBean().updateHandler.addTaskMember(foundation.getObjectGuid(), taskMember);
				}
			}
		}

	}

	/**
	 * 更新执行人按钮
	 * 
	 * @throws ServiceRequestException
	 */
	@Override
	public void updateTaskExcutor() throws ServiceRequestException
	{

		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		List<FoundationObject> allTaskList = this.getDomainBean().queryHandler.listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		if (allTaskList != null)
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
			for (FoundationObject foundation : allTaskList)
			{

				util.setFoundation(foundation);
				// 摘要任务不能设置执行人
				if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
				{
					continue;
				}
				if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP || util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
				{
					continue;
				}
				String executeRole = util.getExecuteRole();
				if (!StringUtils.isNullString(executeRole))
				{
					List<RoleMembers> listRoleMember = this.getDomainBean().queryHandler.getRoleMemberList(executeRole);
					if (!SetUtils.isNullList(listRoleMember))
					{
						User user = listRoleMember.get(0).getUser();
						util.setExecutor(user.getGuid());
						util.setExecutorName(user.getName());

						List<TaskMember> listTaskMember = this.getDomainBean().queryHandler.getTaskMemberList(foundation.getObjectGuid().getGuid());
						if (listTaskMember != null)
						{
							boolean isResourceExist = false;
							for (TaskMember taskMember : listTaskMember)
							{
								if (user.getGuid().equals(taskMember.getUserGuid()))
								{
									isResourceExist = true;
									taskMember.setOperate(WBSOperateEnum.UPDATE);
									taskMember.setPersonInCharge(true);
								}
								else
								{
									if (taskMember.isPersonInCharge())
									{
										taskMember.setOperate(WBSOperateEnum.UPDATE);
										taskMember.setPersonInCharge(false);
									}
								}
							}

							if (!isResourceExist)
							{
								TaskMember taskMember = new TaskMember();
								taskMember.setTaskObjectGuid(foundation.getObjectGuid());
								taskMember.setPersonInCharge(true);
								taskMember.setProjectRole(executeRole);
								taskMember.setProjectRoleName(util.getExecuteRoleName());
								taskMember.setUserGuid(user.getGuid());
								taskMember.setUserName(user.getName());
								taskMember.setRate(100D);
								this.getDomainBean().updateHandler.addTaskMember(foundation.getObjectGuid(), taskMember);
							}
						}
					}
					else
					{
						util.setExecutor(null);
						util.setExecutorName(null);
					}
				}
			}
		}
	}

	@Override
	public void modifyPreTask(TaskRelation xTaskRelation, FoundationObject end2) throws ServiceRequestException
	{
		FoundationObject task = this.getDomainBean().getQueryHandler().getObject(xTaskRelation.getTaskObjectGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(task, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		this.getDomainBean().getCheckHandler().checkDependLoop(xTaskRelation.getTaskObjectGuid(), end2.getObjectGuid(), null, false);

		xTaskRelation.setpreTaskObjectGuid(end2.getObjectGuid());

		this.getDomainBean().getCalculateHandler().updateTaskPreInfo(task);
	}

	@Override
	public void modifyPreTaskType(TaskRelation xTaskRelation, String type) throws ServiceRequestException
	{
		FoundationObject task = this.getDomainBean().getQueryHandler().getObject(xTaskRelation.getTaskObjectGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(task, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		if (StringUtils.isNullString(type))
		{
			throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DEPEND_TYPE_IS_NULL", "Type of Task Depend Relation is Setting Null");
		}
		if (type.equals(xTaskRelation.getDependType()) == false)
		{
			xTaskRelation.setDependType(type);
		}
		this.getDomainBean().getCalculateHandler().updateTaskPreInfo(task);

	}

	@Override
	public void modifyPreTaskDelayTime(TaskRelation xTaskRelation, int value) throws ServiceRequestException
	{
		FoundationObject task = this.getDomainBean().getQueryHandler().getObject(xTaskRelation.getTaskObjectGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(task, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		if (value < 0)
		{
			throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DELAY_IS_NULL", "Delay of Task Depend Rleation is Setting Null or <0");
		}
		if (value != xTaskRelation.getDelayTime())
		{
			xTaskRelation.setDelayTime(value);
		}
		this.getDomainBean().getCalculateHandler().updateTaskPreInfo(task);
	}

	@Override
	public void setPrepareFieldValue(FoundationObject fo, String fieldName, Object value) throws ServiceRequestException
	{

		if (this.getDomainBean().getCheckHandler().isEditable(fo, fieldName))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(fo);
			if (PPMFoundationObjectUtil.PLANSTARTTIME.equalsIgnoreCase(fieldName))
			{
				this.updatePreparePlanStartTime(util, value);
			}
			else if (PPMFoundationObjectUtil.PLANFINISHTIME.equalsIgnoreCase(fieldName))
			{
				this.updatePreparePlanFinishTime(util, value);
			}
			else if (PPMFoundationObjectUtil.PLANNEDDURATION.equalsIgnoreCase(fieldName))
			{
				this.updatePlannedDuration(util, value);
			}
			else if (PPMFoundationObjectUtil.PLANWORKLOAD.equalsIgnoreCase(fieldName))
			{
				if (value == null)
				{
					util.setPlanWorkload(null);
				}
				else if (!StringUtils.isNullString(value.toString()))
				{
					util.setPlanWorkload(Double.valueOf(value.toString()));
				}
				else
				{
					util.setPlanWorkload(null);
				}
			}
			else if (PPMFoundationObjectUtil.EXECUTORROLE.equalsIgnoreCase(fieldName))
			{
				this.updateExcutorRole(util, value, true);
			}
			else if (PPMFoundationObjectUtil.EXECUTOR.equalsIgnoreCase(fieldName))
			{
				if (this.getDomainBean().getRootObject() == fo)
				{
					this.updateProjectPerformer(fo, (String) value);
				}
				else
				{
					this.updateTaskPerformer(fo, (String) value);
				}

			}
			else if (PPMFoundationObjectUtil.PREDECESSORRELATION.equalsIgnoreCase(fieldName))
			{
				List<TaskRelation> taskRelations = this.updatePreparePreTask(util, value);
				List<TaskRelation> preTaskList = this.getDomainBean().getQueryHandler().getPreTaskList(fo.getObjectGuid());
				if (!SetUtils.isNullList(preTaskList))
				{
					for (TaskRelation relation : preTaskList)
					{
						this.removePreTask(relation);
					}
				}

				if (!SetUtils.isNullList(taskRelations))
				{
					for (TaskRelation taskRelation : taskRelations)
					{
						this.addPreTask(taskRelation);
					}
				}
				this.getDomainBean().getCalculateHandler().updateTaskPreInfo(util.getFoundation());
			}
			else if (PPMFoundationObjectUtil.PROJECTTYPE.equalsIgnoreCase(fieldName))
			{
				this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

				if (util.getProjectType().getGuid() != null)
				{
					this.updateMilestone((List<CheckpointConfig>) value);
				}
				else
				{
					this.updateMilestone(null);
				}
			}
			else if ("ISMANUALPLAN".equalsIgnoreCase(fieldName))
			{
				this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

				if ("N".equalsIgnoreCase((String) value))
				{
					this.setPrepareFieldValue(fo, PPMFoundationObjectUtil.PLANSTARTTIME, null);
					this.setPrepareFieldValue(fo, PPMFoundationObjectUtil.PLANSTARTPOINT, null);
				}
			}
			else if (value instanceof ObjectGuid)
			{
				ObjectGuid objectGuid = (ObjectGuid) value;
				fo.put(fieldName, objectGuid.getGuid());
				fo.put(fieldName + "$CLASS", objectGuid.getClassGuid());
				fo.put(fieldName + "$MASTER", objectGuid.getMasterGuid());
			}
			else
			{
				fo.put(fieldName, value);
			}
		}
	}

	private void updateProjectPerformer(FoundationObject fo, String value) throws ServiceRequestException
	{

		if (StringUtils.isGuid(value) == false)
		{
			throw new ServiceRequestException("ID_PPM_PROJECT_EXECUTOR_IS_NOT_NULL", "project instance excutor is not null");
		}
		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());
		List<RoleMembers> list = this.getDomainBean().getRealationInfo(RoleMembers.class, DataTypeEnum.TEAM.name());
		User user = null;
		boolean isExist = false;
		ProjectRole pmrole = null;
		if (!SetUtils.isNullList(list))
		{
			for (RoleMembers member : list)
			{
				if (value.equalsIgnoreCase(member.getUserGuid()))
				{
					user = member.getUser();
				}
				ProjectRole role = this.getDomainBean().getInstanceObject(ProjectRole.class, member.getProjectRoleGuid());
				if (role.isManger())
				{
					pmrole = role;
					if (value.equalsIgnoreCase(member.getUserGuid()))
					{
						isExist = true;
						break;
					}
				}
			}
		}

		if (!isExist)
		{

			RoleMembers newMember = new RoleMembers();
			newMember.setProjectRoleGuid(pmrole.getGuid());
			newMember.setRoleName(pmrole.getRoleName());
			newMember.setUserGuid(value);
			newMember.setUser(user);

			newMember.setProjectRoleGuid(pmrole.getGuid());
			newMember.setRoleName(pmrole.getRoleName());
			this.addToDomain(DataTypeEnum.TEAM.name(), newMember);
		}

		fo.put(PPMFoundationObjectUtil.EXECUTOR, value);
	}

	/**
	 * @param planFinishTime
	 *            the planStartTime to set
	 */
	protected void updatePlannedDuration(PPMFoundationObjectUtil task, Object value) throws ServiceRequestException
	{
		int plannedDuration = 0;
		try
		{
			if (value instanceof Double)
			{
				plannedDuration = (int) Math.floor((Double) value);
			}
			else if (value instanceof BigDecimal)
			{
				plannedDuration = (int) Math.floor(((BigDecimal) value).doubleValue());
			}
			else
			{
				plannedDuration = (int) Math.floor(Double.valueOf((String) value));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		int oldPlanDuration = task.getPlannedDuration();

		if (plannedDuration != oldPlanDuration)
		{
			if (!(task.getTaskTypeEnum() == TaskTypeEnum.MILESTONE || task.getTaskTypeEnum() == TaskTypeEnum.GENERAL || this.getDomainBean().getCheckHandler()
					.isSubProject(task.getFoundation()))
					&& this.getDomainBean().queryHandler.getRootObject() != task.getFoundation())
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_SUMMARY_TASK_DURATION_NOT_CHANGE", "Duration of Summary Task can't change", null, task.getWBSNumber());
			}
			if (plannedDuration <= 0)
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DURATION_IS_NULL", "Duration of Task is Setting Null", null, task.getWBSNumber());
			}

			task.setPlannedDuration(plannedDuration);

			Date planFinishTime = this.getPrepareContain().getProjectCalendar().addDay(task.getPlanStartTime(), plannedDuration - 1);
			task.setPlanFinishTime(planFinishTime);
		}
	}

	protected void updateTaskPerformer(FoundationObject task, String userGuid) throws ServiceRequestException
	{
		PPMFoundationObjectUtil taskUtil = new PPMFoundationObjectUtil(task);

		if (StringUtils.isNullString(userGuid))
		{
			taskUtil.setExecutor(null);
			taskUtil.setExecutorName(null);
			List<TaskMember> listTaskMember = this.getDomainBean().getQueryHandler().getTaskMemberList(task.getObjectGuid().getGuid());
			for (TaskMember taskMember : listTaskMember)
			{
				taskMember.setPersonInCharge(false);
			}
		}
		else
		{

			if (taskUtil.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
			{
				throw new ServiceRequestException("ID_PPM_WBS_SUMMARY_EXECUTOR", "summary task do not set executor", null, taskUtil.getWBSNumber());
			}

			if (task != this.getDomainBean().getRootObject())
			{ // 任务
				taskUtil.setExecutor(userGuid);
				List<TaskMember> listTaskMember = this.getDomainBean().getQueryHandler().getTaskMemberList(task.getObjectGuid().getGuid());
				boolean isExist = false;
				TaskMember newMember = null;
				for (TaskMember taskMember : listTaskMember)
				{
					if (taskMember != null && taskMember.getUserGuid() != null && taskMember.getUserGuid().equalsIgnoreCase(userGuid))
					{
						isExist = true;
						newMember = taskMember;
						newMember.setPersonInCharge(true);
					}
					else
					{
						taskMember.setPersonInCharge(false);
					}
				}

				if (!isExist)
				{

					newMember = new TaskMember();

					newMember.setUserGuid(taskUtil.getExecutor());
					newMember.setUserName(taskUtil.getExecutorName());
					newMember.setProjectRole(taskUtil.getExecuteRole());
					newMember.setProjectRoleName(taskUtil.getExecuteRoleName());
					newMember.setTaskObjectGuid(task.getObjectGuid());
					newMember.setPersonInCharge(true);
					newMember.setRate(100D);

					this.addTaskMember(task.getObjectGuid(), newMember);
				}
			}
		}
	}

	/**
	 * @param planFinishTime
	 *            the planStartTime to set
	 */
	protected void updateExcutorRole(PPMFoundationObjectUtil task, Object value, boolean isCheckAuthority) throws ServiceRequestException
	{
		if (isCheckAuthority)
		{
			this.getDomainBean().getCheckHandler().hasPMAuthority(task.getFoundation(), PMAuthorityEnum.TASK_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());

		}
		if (StringUtils.isNullString((String) value))
		{
			task.setExecuteRole(null);
			task.setExecuteRoleName(null);
			this.updateTaskPerformer(task.getFoundation(), null);
		}
		else
		{
			if (task.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
			{
				throw new ServiceRequestException("ID_PPM_WBS_SUMMARY_EXECUTOR_ROLE", "summary task do not set executor role", null, task.getWBSNumber());
			}
			boolean isUpdate = true;
			if (value.equals(task.getExecuteRole()))
			{
				isUpdate = false;
			}

			task.setExecuteRole((String) value);
			if (isUpdate)
			{
				this.updateTaskPerformer(task.getFoundation(), null);
			}
		}
	}

	protected void updatePreparePlanStartTime(PPMFoundationObjectUtil task, Object object) throws ServiceRequestException
	{
		Date planStartTime = (Date) object;
		Date oldPlanStartTime = task.getPlanStartTime();

		if (planStartTime != null && (oldPlanStartTime == null || oldPlanStartTime.compareTo(planStartTime) != 0))
		{
			if (planStartTime != null)
			{
				Calendar xCalendar = Calendar.getInstance();
				xCalendar.setTime(planStartTime);
				xCalendar.set(Calendar.HOUR_OF_DAY, 0);
				xCalendar.set(Calendar.MINUTE, 0);
				xCalendar.set(Calendar.SECOND, 0);
				xCalendar.set(Calendar.MILLISECOND, 0);
				planStartTime = xCalendar.getTime();
			}

			planStartTime = this.getPrepareContain().getProjectCalendar().getNextWorkingDate(planStartTime);
			task.setPlanStartTime(planStartTime);
			Date planFinishTime = this.getPrepareContain().getProjectCalendar().addDay(task.getPlanStartTime(), task.getPlannedDuration() - 1);
			task.setPlanFinishTime(planFinishTime);
			if (task.getFoundation() != this.getDomainBean().getRootObject())
			{
				task.setEarliestStartTime(planStartTime);
			}
		}
		else if (planStartTime == null)
		{
			if (task.getEarliestStartTime() != null)
			{
				task.setEarliestStartTime(planStartTime);

			}
		}
	}

	protected List<TaskRelation> updatePreparePreTask(PPMFoundationObjectUtil task, Object value) throws ServiceRequestException
	{
		List<TaskRelation> relationList = new ArrayList<TaskRelation>();
		String relationStrs = (String) value;
		if (relationStrs == null)
		{
			return relationList;
		}

		String[] splitrelations = StringUtils.splitStringWithDelimiter(",", relationStrs);
		if (splitrelations == null)
		{
			return relationList;
		}

		TaskRelation taskRelation = null;
		for (String relation : splitrelations)
		{
			if (relation == null)
			{
				continue;
			}

			taskRelation = new TaskRelation();

			taskRelation.settaskObjectGuid(task.getFoundation().getObjectGuid());
			taskRelation.setProjectObjectGuid(task.getOwnerProject());
			if (relation.contains("SS") || relation.contains("SF") || relation.contains("FS") || relation.contains("FF"))
			{

				if (relation.contains("SS"))
				{
					taskRelation.setDependType(TaskDependEnum.START_START.getValue());
				}
				else if (relation.contains("SF"))
				{
					taskRelation.setDependType(TaskDependEnum.START_FINISH.getValue());
				}
				else if (relation.contains("FS"))
				{
					taskRelation.setDependType(TaskDependEnum.FINISH_START.getValue());
				}
				else if (relation.contains("FF"))
				{
					taskRelation.setDependType(TaskDependEnum.FINISH_FINISH.getValue());
				}

				String[] split = StringUtils.splitStringWithDelimiter(taskRelation.getDependType(), relation);

				FoundationObject taskFoundationObject = this.getDomainBean().queryHandler.getObject(split[0]);
				if (taskFoundationObject == null)
				{
					// error
					continue;
				}
				taskRelation.setpreTaskObjectGuid(taskFoundationObject.getObjectGuid());

				if (split.length == 2)
				{
					try
					{
						Integer valueOf = Integer.valueOf(split[1]);
						taskRelation.setDelayTime(valueOf);
					}
					catch (Exception e)
					{
						// error
						continue;
					}

				}

			}
			else
			{
				taskRelation.setDependType(TaskDependEnum.FINISH_START.getValue());
				taskRelation.setDelayTime(0);
				FoundationObject taskFoundationObject = this.getDomainBean().queryHandler.getObject(relation);
				if (taskFoundationObject == null)
				{
					// error
					continue;
				}

				taskRelation.setpreTaskObjectGuid(taskFoundationObject.getObjectGuid());
			}

			relationList.add(taskRelation);
		}

		return relationList;
	}

	/**
	 * @param preparePlanFinishTime
	 * @throws Exception
	 */
	protected void updatePreparePlanFinishTime(PPMFoundationObjectUtil task, Object object) throws ServiceRequestException
	{
		Date planFinishTime = (Date) object;
		Date oldPlanFinishTime = task.getPlanFinishTime();

		if (planFinishTime != null && (oldPlanFinishTime == null || oldPlanFinishTime.compareTo(planFinishTime) != 0))
		{
			if (task.getTaskTypeEnum() == TaskTypeEnum.MILESTONE || task.getTaskTypeEnum() == TaskTypeEnum.GENERAL
					|| this.getDomainBean().getCheckHandler().isSubProject(task.getFoundation()) || task.getFoundation() == this.getDomainBean().queryHandler.getRootObject())
			{
				if (planFinishTime != null && oldPlanFinishTime != null)
				{
					if (this.getPrepareContain().getProjectCalendar() == null)
					{
						throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_NO_EXECUTE_ROLE", "Task isn't setting Role", null, task.getWBSNumber());
					}
					planFinishTime = this.getPrepareContain().getProjectCalendar().getNextWorkingDate(planFinishTime);
					int hour = this.getPrepareContain().getProjectCalendar().getDurationDay(task.getPlanStartTime(), planFinishTime) + 1;
					if (hour > 0)
					{
						task.setPlannedDuration(hour);
						task.setPlanFinishTime(planFinishTime);
					}
					else
					{
						throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_PLAN_TIME", "plan finish time can't < plan start time", null, task.getWBSNumber());
					}
				}
			}
			else
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_IS_SUMMARY", "Task Type is Summary", null, task.getWBSNumber());
			}
		}
	}

	@Override
	public InstanceDomainBean copyInstanceDomain(FoundationObject toProject, String classGuid, String copyType, boolean isCopyM, List<CheckpointConfig> checkpointConfigList)
	{
		PPMFoundationObjectUtil taskUtil = new PPMFoundationObjectUtil(toProject);
		String newManagerUser = taskUtil.getExecutor();
		taskUtil = new PPMFoundationObjectUtil(this.getDomainBean().getRootObject());
		String oldManagerUser = taskUtil.getExecutor();
		InstanceDomainUpdateBean updateBean = new InstanceDomainUpdateBean(toProject);
		updateBean.setMode(DomainSyncModeEnum.REPACLEALL);
		ProjectWBSDriver driver = new ProjectWBSDriver(new FoundationObjectImpl(), newManagerUser);
		((InstanceDomainBean) driver).syncInstanceDomain(updateBean);

		// 复制团队
		List<ProjectRole> list = this.getDomainBean().getQueryHandler().listAllObject(ProjectRole.class, DataTypeEnum.ROLE);

		ProjectRole mrole = null;
		Map<String, String> projectRoleMap = new HashMap<String, String>();
		if (SetUtils.isNullList(list) == false)
		{
			for (ProjectRole pmRole : list)
			{
				ProjectRole clone = (ProjectRole) pmRole.clone();
				clone.setGuid(null);
				clone.setTypeGuid(toProject.getObjectGuid().getGuid());
				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.ROLE.name(), clone);
				projectRoleMap.put(pmRole.getGuid(), clone.getGuid());
				if (clone.isManger() && mrole == null)
				{
					mrole = clone;
				}
			}
		}
		if (mrole == null)
		{
			mrole = new ProjectRole();
			mrole.setRoleId(PMConstans.PROJECT_MANAGER_ROLE);
			mrole.setRoleName(PMConstans.PROJECT_MANAGER_ROLE);
			mrole.setTypeGuid(toProject.getObjectGuid().getGuid());
			((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.ROLE.name(), mrole);
		}

		if (PMConstans.COPY_TYPE_P2P.equalsIgnoreCase(copyType))
		{
			boolean isExistuser = false;
			boolean isNewManagerExistuser = false;
			List<RoleMembers> roleMembersList = this.getDomainBean().getQueryHandler().listAllObject(RoleMembers.class, DataTypeEnum.TEAM);
			if (SetUtils.isNullList(roleMembersList) == false)
			{
				for (RoleMembers roleMembers : roleMembersList)
				{
					RoleMembers clone = (RoleMembers) roleMembers.clone();
					clone.setGuid(null);
					String newRoleGuid = projectRoleMap.get(roleMembers.getProjectRoleGuid());
					clone.setProjectRoleGuid(newRoleGuid);
					if (mrole.getGuid().equals(newRoleGuid) && StringUtils.isNullString(oldManagerUser) == false)
					{
						if (oldManagerUser.equals(clone.getUserGuid()))
						{
							clone.setUserGuid(newManagerUser);
							isExistuser = true;
							if (isNewManagerExistuser)
							{
								continue;
							}
							else
							{
								isNewManagerExistuser = true;
								((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.TEAM.name(), clone);
								continue;
							}
						}
					}

					if (!StringUtils.isNullString(newManagerUser) && mrole.getGuid().equals(newRoleGuid) && newManagerUser.equals(clone.getUserGuid()))
					{
						if (isNewManagerExistuser)
						{
							continue;
						}
						else
						{
							isNewManagerExistuser = true;
						}
					}

					((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.TEAM.name(), clone);
				}
			}
			if (isExistuser == false)
			{
				RoleMembers clone = new RoleMembers();
				clone.setProjectRoleGuid(mrole.getGuid());
				clone.setUserGuid(newManagerUser);
				clone.setSequence(0);
				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.TEAM.name(), clone);
			}
		}
		else
		{
			RoleMembers clone = new RoleMembers();
			clone.setProjectRoleGuid(mrole.getGuid());
			clone.setUserGuid(newManagerUser);
			clone.setSequence(0);
			((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.TEAM.name(), clone);
		}

		// 复制任务
		List<FoundationObject> taskList = this.getDomainBean().getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		Map<String, ObjectGuid> taskGuidMapping = new HashMap<String, ObjectGuid>();
		taskGuidMapping.put(this.getDomainBean().getRootObject().getGuid(), toProject.getObjectGuid());
		if (!SetUtils.isNullList(taskList))
		{
			for (FoundationObject foundation : taskList)
			{
				FoundationObject clone = (FoundationObject) foundation.clone();
				ObjectGuid newObjectGuid = new ObjectGuid(clone.getObjectGuid());

				taskUtil.setFoundation(clone);
				if (StringUtils.isNullString(taskUtil.getParentTask().getGuid()))
				{
					continue;
				}

				taskUtil.setOwnerProject(toProject.getObjectGuid());

				taskUtil.setRelationProject(null);
				taskUtil.setOperationState(null);
				taskUtil.setOperationStateEnum(null);
				taskUtil.setFromTemplate(null);
				taskUtil.setOnTimeState(null);
				taskUtil.setOnTimeStateEnum(null);
				taskUtil.setActualWorkload(null);
				taskUtil.setCompletionRate(null);
				taskUtil.setSPI(null);
				taskUtil.setActualDuration(null);
				taskUtil.setActualFinishTime(null);
				taskUtil.setActualStartTime(null);
				taskUtil.setTaskStatus(null);
				taskUtil.setOriginalStatus(null);
				if (!PMConstans.COPY_TYPE_P2P.equalsIgnoreCase(copyType))
				{
					taskUtil.setExecutor(null);
					taskUtil.setExecutorName(null);
				}

				newObjectGuid.setGuid(null);
				newObjectGuid.setMasterGuid(null);
				clone.setObjectGuid(newObjectGuid);
				clone.setStatus(SystemStatusEnum.WIP);
				clone.setLifecyclePhaseGuid(null);
				newObjectGuid.setClassGuid(classGuid);
				newObjectGuid.setClassName(null);
				newObjectGuid.setBizObjectGuid(null);
				newObjectGuid.setCommitFolderGuid(null);
				newObjectGuid.setIsMaster(false);
				newObjectGuid.setMasterGuid(null);
				clone.setObjectGuid(newObjectGuid);

				CodeItemInfo subCodeItem = this.getPrepareContain().getTaskStatusCodeItem(TaskStatusEnum.INI);
				taskUtil.setTaskStatus(subCodeItem.getGuid());

				String newRoleGuid = projectRoleMap.get(taskUtil.getExecuteRole());
				taskUtil.setExecuteRole(newRoleGuid);

				if (mrole.getGuid().equals(newRoleGuid) && StringUtils.isNullString(oldManagerUser) == false)
				{
					if (oldManagerUser.equals(taskUtil.getExecutor()))
					{
						taskUtil.setExecutor(newManagerUser);
					}
				}
				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.TASK.name(), clone);
				taskGuidMapping.put(foundation.getGuid(), clone.getObjectGuid());
			}

			taskList = driver.getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);
			if (!SetUtils.isNullList(taskList))
			{
				for (FoundationObject foundation : taskList)
				{
					taskUtil.setFoundation(foundation);
					taskUtil.setParentTask(taskGuidMapping.get(taskUtil.getParentTask().getGuid()));
				}
			}
		}

		// 复制依赖
		List<TaskRelation> taskRelationList = this.getDomainBean().getQueryHandler().listAllObject(TaskRelation.class, DataTypeEnum.DEPEND);
		if (!SetUtils.isNullList(taskRelationList))
		{
			for (TaskRelation taskRelation : taskRelationList)
			{
				TaskRelation clone = (TaskRelation) taskRelation.clone();
				clone.setGuid(null);
				clone.setProjectObjectGuid(toProject.getObjectGuid());
				ObjectGuid objectGuid = taskGuidMapping.get(taskRelation.getTaskObjectGuid().getGuid());
				if (objectGuid == null)
				{
					continue;
				}
				clone.settaskObjectGuid(objectGuid);

				objectGuid = taskGuidMapping.get(taskRelation.getPreTaskObjectGuid().getGuid());
				if (objectGuid == null)
				{
					continue;
				}

				clone.setpreTaskObjectGuid(objectGuid);
				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.DEPEND.name(), clone);
			}
		}
		// 复制资源
		if (PMConstans.COPY_TYPE_P2P.equalsIgnoreCase(copyType))
		{
			List<TaskMember> listTaskMember = this.getDomainBean().getQueryHandler().listAllObject(TaskMember.class, DataTypeEnum.RESOURCE);
			if (!SetUtils.isNullList(listTaskMember))
			{
				for (TaskMember taskMember : listTaskMember)
				{
					TaskMember clone = (TaskMember) taskMember.clone();
					clone.setGuid(null);

					String newRoleGuid = projectRoleMap.get(taskMember.getProjectRole());
					if (StringUtils.isNullString(newRoleGuid))
					{
						continue;
					}

					clone.setProjectRole(newRoleGuid);
					if (mrole.getGuid().equals(newRoleGuid) && StringUtils.isNullString(oldManagerUser) == false)
					{
						if (oldManagerUser.equals(clone.getUserGuid()))
						{
							clone.setUserGuid(newManagerUser);
						}
					}
					ObjectGuid toTaskObjectGuid = taskGuidMapping.get(taskMember.getTaskObjectGuid().getGuid());
					clone.setTaskObjectGuid(toTaskObjectGuid);
					((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.RESOURCE.name(), clone);

				}
			}
		}

		// 复制里程碑
		if (isCopyM)
		{
			checkpointConfigList = this.getDomainBean().getQueryHandler().listAllObject(CheckpointConfig.class, DataTypeEnum.MILESTONE);
		}

		if (!SetUtils.isNullList(checkpointConfigList))
		{
			for (CheckpointConfig checkpointConfig : checkpointConfigList)
			{
				CheckpointConfig clone = (CheckpointConfig) checkpointConfig.clone();
				clone.setGuid(null);
				clone.setType("2");
				clone.setTypeGuid(toProject.getGuid());
				ObjectGuid toTaskObjectGuid = taskGuidMapping.get(clone.getRelatedTaskObject().getGuid());
				clone.setRelatedTaskObject(toTaskObjectGuid);

				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.MILESTONE.name(), clone);

			}
		}

		// 复制交付项
		List<DeliverableItem> itemList = this.getDomainBean().getQueryHandler().listAllObject(DeliverableItem.class, DataTypeEnum.DEVELIVE);

		if (!SetUtils.isNullList(itemList))
		{
			for (DeliverableItem item : itemList)
			{
				DeliverableItem clone = (DeliverableItem) item.clone();
				clone.setProjectGuid(toProject.getGuid());
				ObjectGuid objectGuid = taskGuidMapping.get(item.getTaskGuid());
				if (objectGuid == null || objectGuid.getGuid() == null)
				{
					continue;
				}

				clone.setTaskGuid(objectGuid.getGuid());
				clone.setGuid(null);
				if (StringUtils.isNullString(oldManagerUser) == false)
				{
					if (oldManagerUser.equals(clone.getCreateUserGuid()))
					{
						clone.setCreateUserGuid(newManagerUser);
					}
				}
				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.DEVELIVE.name(), clone);
			}
		}

		// 复制预警
		List<EarlyWarning> listAllObject = this.getDomainBean().getQueryHandler().listAllObject(EarlyWarning.class, DataTypeEnum.WARN);
		if (!SetUtils.isNullList(listAllObject))
		{
			for (EarlyWarning earlyWarning : listAllObject)
			{
				EarlyWarning clone = (EarlyWarning) earlyWarning.clone();
				clone.setProjectGuid(toProject.getGuid());

				clone.setGuid(null);
				if (StringUtils.isNullString(oldManagerUser) == false)
				{
					if (oldManagerUser.equals(clone.getCreateUserGuid()))
					{
						clone.setCreateUserGuid(newManagerUser);
					}
				}

				if (!SetUtils.isNullList(clone.getNoticeMemberList()))
				{
					for (WarningNotifier o : clone.getNoticeMemberList())
					{
						if (StringUtils.isNullString(oldManagerUser) == false)
						{
							if (oldManagerUser.equals(o.getNotifierGuid()))
							{
								o.setNotifierGuid(newManagerUser);
							}
						}
					}
				}

				if (!SetUtils.isNullList(clone.getApplyToTaskList()))
				{
					for (ObjectGuid o : clone.getApplyToTaskList())
					{
						ObjectGuid objectGuid = taskGuidMapping.get(o.getGuid());
						if (objectGuid == null || objectGuid.getGuid() == null)
						{
							continue;
						}

						o.setGuid(objectGuid.getGuid());
						o.setClassGuid(objectGuid.getClassGuid());
					}
				}

				((PJUpdateHandler) driver.getUpdateHandler()).addToDomain(DataTypeEnum.WARN.name(), clone);

			}
		}

		return driver;

	}

	@Override
	public void addDeliverableItem(ObjectGuid taskObjectGuid, DeliverableItem item) throws ServiceRequestException
	{
		FoundationObject object = this.getDomainBean().getQueryHandler().getObject(taskObjectGuid);

		this.getDomainBean().getCheckHandler().hasPMAuthority(object, PMAuthorityEnum.TASK_MANAGEDELIVERY, this.getDomainBean().getOperatorGuid());

		item.setTaskGuid(taskObjectGuid.getGuid());
		item.setProjectGuid(this.getDomainBean().getRootObject().getObjectGuid().getGuid());
		this.addToDomain(DataTypeEnum.DEVELIVE.name(), item);

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
		if (!util.hasDeliveryItem())
		{
			util.setHasDeliveryItem(true);
		}
	}

	@Override
	public void removeDeliverableItem(DeliverableItem item) throws ServiceRequestException
	{

		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setGuid(item.getTaskGuid());
		FoundationObject object = this.getDomainBean().getQueryHandler().getObject(objectGuid);
		this.getDomainBean().getCheckHandler().hasPMAuthority(object, PMAuthorityEnum.TASK_MANAGEDELIVERY, this.getDomainBean().getOperatorGuid());
		this.deleteFromDomain(DataTypeEnum.DEVELIVE.name(), item);

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
		List<DeliverableItem> deliverableItemList = this.getDomainBean().getQueryHandler().getDeliverableItemList(item.getTaskGuid());
		if (SetUtils.isNullList(deliverableItemList))
		{
			util.setHasDeliveryItem(null);
		}
		else
		{
			util.setHasDeliveryItem(true);
		}

	}

	@Override
	public void addProjectRole(ProjectRole projectRole) throws ServiceRequestException
	{

		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());

		projectRole.setTypeGuid(this.getDomainBean().getRootObject().getGuid());
		if (this.getDomainBean().getRootObject().getObjectGuid() != null)
		{
			projectRole.setTypeClassGuid(this.getDomainBean().getRootObject().getObjectGuid().getClassGuid());
		}
		this.addToDomain(DataTypeEnum.ROLE.name(), projectRole);
	}

	@Override
	public void addRoleMember(ProjectRole projectRole, RoleMembers roleMember) throws ServiceRequestException
	{
		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());

		roleMember.setProjectRoleGuid(projectRole.getGuid());
		roleMember.setRoleName(projectRole.getRoleName());
		this.addToDomain(DataTypeEnum.TEAM.name(), roleMember);
	}

	@Override
	public void removeTaskMember(TaskMember taskMember) throws ServiceRequestException
	{
		FoundationObject object = this.getDomainBean().getQueryHandler().getObject(taskMember.getTaskObjectGuid());

		this.getDomainBean().getCheckHandler().hasPMAuthority(object, PMAuthorityEnum.TASK_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());

		this.deleteFromDomain(DataTypeEnum.RESOURCE.name(), taskMember);
		String taskGuid = taskMember.getTaskObjectGuid().getGuid();

		if (taskMember.isPersonInCharge())
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
			util.setExecutor(null);
			util.setExecutorName(null);
		}

		List<TaskMember> taskMemberList = this.getDomainBean().getQueryHandler().getTaskMemberList(taskGuid);
		if (!SetUtils.isNullList(taskMemberList))
		{
			int i = 0;
			for (TaskMember member : taskMemberList)
			{
				member.setSequence(i++);
			}
		}
	}

	@Override
	public void removeCheckPointConfig(CheckpointConfig checkPoint)
	{
		this.deleteFromDomain(DataTypeEnum.MILESTONE.name(), checkPoint);
	}

	@Override
	public void addCheckPointConfig(CheckpointConfig checkPoint)
	{
		checkPoint.setGuid(null);
		checkPoint.setType("2");
		checkPoint.setTypeGuid(this.getDomainBean().getRootObject().getObjectGuid().getGuid());
		this.addToDomain(DataTypeEnum.MILESTONE.name(), checkPoint);
	}

	/**
	 * @param planFinishTime
	 *            the planStartTime to set
	 */
	protected void updateMilestone(List<CheckpointConfig> newCheckPointConfigList) throws ServiceRequestException
	{
		List<CheckpointConfig> checkPointConfigList = this.getDomainBean().getQueryHandler().getCheckPointConfigList();
		if (!SetUtils.isNullList(checkPointConfigList))
		{
			for (CheckpointConfig checkPoint : checkPointConfigList)
			{
				this.removeCheckPointConfig(checkPoint);
			}
		}

		if (!SetUtils.isNullList(newCheckPointConfigList))
		{
			for (CheckpointConfig checkPoint : newCheckPointConfigList)
			{
				this.addCheckPointConfig(checkPoint);
			}
		}

		this.relateTaskMilestone();

	}

	@Override
	public void setTaskMilestone(FoundationObject object) throws ServiceRequestException
	{
		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
		if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
		{
			throw new ServiceRequestException("ID_CLIENT_PM_MILESTONE_IS_SET", "task is milestone");
		}
		if (util.getTaskTypeEnum() != TaskTypeEnum.GENERAL)
		{
			throw new ServiceRequestException("ID_PM_MILESTONE_SET_ERROR", "set milestone error");
		}
		List<FoundationObject> listAllObject = this.getDomainBean().getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		Collections.sort(listAllObject, new TaskCompareByWBSCode());
		int i = 0;
		for (FoundationObject foundation : listAllObject)
		{
			util.setFoundation(foundation);

			if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
			{
				i++;
			}
		}
		List<CheckpointConfig> checkPointConfigList = this.getDomainBean().getQueryHandler().getCheckPointConfigList();
		if (checkPointConfigList == null)
		{
			throw new ServiceRequestException("ID_PM_NO_MILESTONE_CHECKPOINT", "no milestone Checkpoint");
		}

		if (i >= checkPointConfigList.size())
		{
			throw new ServiceRequestException("ID_PM_MILESTONE_SET_FINISH", "milestone Checkpoint set finish");
		}
		util.setFoundation(object);
		if (util.getTaskTypeEnum() == TaskTypeEnum.GENERAL)
		{
			CodeItemInfo taskTypeCodeItem = this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.MILESTONE);
			util.setTaskType(taskTypeCodeItem.getGuid());
			util.setTaskTypeName(taskTypeCodeItem.getName());
		}

		this.relateTaskMilestone();

	}

	@Override
	public void cancelTaskMilestone(FoundationObject object) throws ServiceRequestException
	{

		this.getDomainBean().getCheckHandler().hasPMAuthority(this.getDomainBean().getRootObject(), PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
		if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
		{
			CodeItemInfo taskTypeCodeItem = this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL);
			util.setTaskType(taskTypeCodeItem.getGuid());
			util.setTaskTypeName(taskTypeCodeItem.getName());
		}

		this.relateTaskMilestone();
	}

	private void relateTaskMilestone()
	{

		List<CheckpointConfig> checkPointConfigList = this.getDomainBean().getQueryHandler().getCheckPointConfigList();
		if (checkPointConfigList == null)
		{
			return;
		}

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
		List<FoundationObject> listAllObject = this.getDomainBean().getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		int i = 0;
		if (!SetUtils.isNullList(listAllObject))
		{
			Collections.sort(listAllObject, new TaskCompareByWBSCode());

			for (FoundationObject foundation : listAllObject)
			{
				util.setFoundation(foundation);

				if (checkPointConfigList.size() <= i)
				{
					util.setFoundation(foundation);

					if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
					{
						util.setTaskType(this.getPrepareContain().getTaskTypeGuid(TaskTypeEnum.GENERAL));
						util.setTaskTypeName(TaskTypeEnum.GENERAL.getValue());
					}
				}

				if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
				{
					CheckpointConfig checkpointConfig = checkPointConfigList.get(i++);
					checkpointConfig.setRelatedTaskObject(foundation.getObjectGuid());
				}

			}
		}

		if (!SetUtils.isNullList(checkPointConfigList))
		{
			for (; checkPointConfigList.size() > i; i++)
			{
				checkPointConfigList.get(i).setRelatedTaskObject(null);
			}
		}
		else
		{
			if (!SetUtils.isNullList(listAllObject))
			{
				for (FoundationObject foundation : listAllObject)
				{
					util.setFoundation(foundation);

					if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
					{
						util.setTaskType(this.getPrepareContain().getTaskTypeGuid(TaskTypeEnum.GENERAL));
						util.setTaskTypeName(TaskTypeEnum.GENERAL.getValue());
					}

				}
			}
		}
	}

	@Override
	public void updatePersonInCharge(TaskMember taskMember) throws ServiceRequestException
	{
		FoundationObject object = this.getDomainBean().getQueryHandler().getObject(taskMember.getTaskObjectGuid());
		this.getDomainBean().getCheckHandler().hasPMAuthority(object, PMAuthorityEnum.TASK_TEAMRESOURCES, this.getDomainBean().getOperatorGuid());
		taskMember.setPersonInCharge(true);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
		if (object != null)
		{
			util.setFoundation(object);

			util.setExecutor(taskMember.getUserGuid());
			util.setExecutorName(taskMember.getUserName());
			util.setExecuteRole(taskMember.getProjectRole());
			util.setExecuteRoleName(taskMember.getProjectRoleName());
		}

		List<TaskMember> listAllObject = this.getDomainBean().getQueryHandler().getTaskMemberList(taskMember.getTaskObjectGuid().getGuid());
		if (!SetUtils.isNullList(listAllObject))
		{
			for (TaskMember member : listAllObject)
			{
				if (taskMember != member && member.isPersonInCharge())
				{
					member.setPersonInCharge(false);
				}
			}
		}
	}

	@Override
	public void conversionManager(User user, Group group) throws ServiceRequestException
	{
		FoundationObject fo = this.getDomainBean().getRootObject();
		if (fo != null && user != null)
		{
			String userGuid = user.getGuid();
			List<RoleMembers> list = this.getDomainBean().getRealationInfo(RoleMembers.class, DataTypeEnum.TEAM.name());
			ProjectRole pmrole = null;
			List<RoleMembers> duser = null;// 需删除的User
			boolean isExist = false;
			if (!SetUtils.isNullList(list))
			{
				duser = new ArrayList<RoleMembers>();
				for (RoleMembers member : list)
				{
					ProjectRole role = this.getDomainBean().getInstanceObject(ProjectRole.class, member.getProjectRoleGuid());
					if (role.isManger())
					{
						pmrole = role;
						if (!userGuid.equalsIgnoreCase(member.getUserGuid()))
						{
							duser.add(member);
						}
						else
						{
							isExist = true;
						}
					}
				}
			}
			if (!SetUtils.isNullList(duser))
			{
				// 删除原来的User
				this.deleteFromDomain(DataTypeEnum.TEAM.name(), duser);
			}
			if (!isExist)
			{
				// 新建后来的User
				RoleMembers newMember = new RoleMembers();
				newMember.setProjectRoleGuid(pmrole.getGuid());
				newMember.setRoleName(pmrole.getRoleName());
				newMember.setUserGuid(userGuid);
				newMember.setUser(user);
				newMember.setProjectRoleGuid(pmrole.getGuid());
				newMember.setRoleName(pmrole.getRoleName());
				this.addToDomain(DataTypeEnum.TEAM.name(), newMember);
			}
			fo.put(PPMFoundationObjectUtil.EXECUTOR, userGuid);
			fo.setOwnerUserGuid(userGuid);
			fo.setOwnerGroupGuid(group.getGuid());
		}
	}
}
