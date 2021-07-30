package dyna.common.bean.data.ppms.wbs.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainHandel;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.bean.data.ppms.wbs.AuthorityCheck;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.DataTypeEnum;
import dyna.common.systemenum.ppms.PMAuthorityEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.SetUtils;

public class PJCheckHandler extends InstanceDomainHandel<AbstractWBSDriver> implements CheckHandler
{
	AuthorityCheck authorityCheck = null;

	public PJCheckHandler(AbstractWBSDriver bean)
	{
		super(bean);

		this.authorityCheck = new AuthorityCheck() {
			@Override
			protected List<TaskMember> listTaskMember(ObjectGuid taskObjectGuid)
			{
				return PJCheckHandler.this.getDomainBean().getQueryHandler().getTaskMemberList(taskObjectGuid.getGuid());
			}

			@Override
			protected FoundationObject getProject(ObjectGuid objectGuid)
			{
				return PJCheckHandler.this.getDomainBean().getRootObject();
			}

			@Override
			protected List<ProjectRole> listProjectRoleByUser(ObjectGuid projectObjectGuid, String operatorGuid)
			{
				List<RoleMembers> listAllObject = PJCheckHandler.this.getDomainBean().getQueryHandler().listAllObject(RoleMembers.class, DataTypeEnum.TEAM);
				List<ProjectRole> roleList = new ArrayList<ProjectRole>();
				if (!SetUtils.isNullList(listAllObject))
				{
					for (RoleMembers member : listAllObject)
					{
						if (operatorGuid.equals(member.getUserGuid()))
						{
							roleList.add(PJCheckHandler.this.getDomainBean().getInstanceObject(ProjectRole.class, member.getProjectRoleGuid()));
						}
					}
				}
				return roleList;
			}
		};
	}

	@Override
	public boolean isValid(FoundationObject task, boolean containCOP)
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		TaskStatusEnum status = this.getDomainBean().prepareContain.getTaskStatusFromCode(util.getTaskStatus());
		if (status == TaskStatusEnum.SSP)
		{
			return false;
		}
		if (!containCOP && status == TaskStatusEnum.COP)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isEditable(FoundationObject task) throws ServiceRequestException
	{
		FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
		if (project.getStatus() == SystemStatusEnum.RELEASE)
		{
			throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_RUN", "project is release,not modify");
		}
		if (project.getStatus() == SystemStatusEnum.PRE)
		{
			throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_PRE", "project is in Workflow,not modify");
		}
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP)
		{
			throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_COP", "task is finished");
		}
		this.getDomainBean().getCheckHandler().hasPMAuthority(task, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());
		return true;
	}

	@Override
	public boolean isEditable(FoundationObject task, String name) throws ServiceRequestException
	{
		FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
		if (project.getStatus() == SystemStatusEnum.RELEASE || project.getStatus() == SystemStatusEnum.PRE)
		{
			return false;
		}
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP)
		{
			return false;
		}
		this.getDomainBean().getCheckHandler().hasPMAuthority(task, PMAuthorityEnum.PROJECT_WBS, this.getDomainBean().getOperatorGuid());

		if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY
				&& (PPMFoundationObjectUtil.EXECUTOR.equalsIgnoreCase(name) || PPMFoundationObjectUtil.EXECUTORROLE.equalsIgnoreCase(name)//
						|| PPMFoundationObjectUtil.PLANSTARTTIME.equalsIgnoreCase(name)//
						|| PPMFoundationObjectUtil.PLANFINISHTIME.equalsIgnoreCase(name)//
						|| PPMFoundationObjectUtil.PLANNEDDURATION.equalsIgnoreCase(name) //
						|| PPMFoundationObjectUtil.PLANWORKLOAD.equalsIgnoreCase(name)))
		{
			return false;
		}
		if (util.getTaskStatusEnum() == TaskStatusEnum.RUN || util.getTaskStatusEnum() == TaskStatusEnum.PUS)
		{
			if (PPMFoundationObjectUtil.PLANSTARTTIME.equalsIgnoreCase(name))//
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void checkSummayDepend(FoundationObject task) throws ServiceRequestException
	{
		if (SetUtils.isNullList(this.getDomainBean().queryHandler.getChildList(task.getObjectGuid())) == false)
		{
			List<TaskRelation> uDependList = this.getDomainBean().queryHandler.getPostTaskList(task.getObjectGuid());
			PPMFoundationObjectUtil outil = new PPMFoundationObjectUtil(task);
			if (SetUtils.isNullList(uDependList) == false)
			{
				for (TaskRelation xTaskRelation : uDependList)
				{
					TaskDependEnum xTaskDependEnum = this.getDomainBean().prepareContain.getDependTypeFromCode(xTaskRelation.getDependType());
					if (TaskDependEnum.FINISH_FINISH.equals(xTaskDependEnum))
					{
						throw new ServiceRequestException("ID_PPM_WBS_PLAN_SUMMARY_TASK_DEPEND_FINISH", "Depend Type Of Summary Task is Not FF or SF", null, outil.getWBSNumber());
					}
					else if (TaskDependEnum.START_FINISH.equals(xTaskDependEnum))
					{
						throw new ServiceRequestException("ID_PPM_WBS_PLAN_SUMMARY_TASK_DEPEND_FINISH", "Depend Type Of Summary Task is Not FF or SF", null, outil.getWBSNumber());
					}
				}
			}
		}
	}

	@Override
	public void checkDependLoop(ObjectGuid task, ObjectGuid preTask, Stack<String> taskStack, boolean isParent) throws ServiceRequestException
	{

		if (taskStack == null)
		{
			taskStack = new Stack<String>();
		}

		if (taskStack.search(preTask.getGuid()) == -1)
		{
			taskStack.push(preTask.getGuid());
		}
		else
		{
			return;
		}

		FoundationObject pmFoundationObject = this.getDomainBean().queryHandler.getObject(preTask);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(pmFoundationObject);
		if (preTask.getGuid().equalsIgnoreCase(task.getGuid()))
		{
			throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DEPEND_LOOP", "Depend Loop" + util.getWBSNumber(), null, util.getWBSNumber());
		}

		List<TaskRelation> preTaskList = this.getDomainBean().queryHandler.getPreTaskList(preTask);
		if (!SetUtils.isNullList(preTaskList))
		{
			for (TaskRelation relation : preTaskList)
			{
				if (relation.getPreTaskObjectGuid().getGuid().equalsIgnoreCase(task.getGuid()))
				{
					throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DEPEND_LOOP", "Depend Loop" + util.getWBSNumber(), null, util.getWBSNumber());
				}
				this.checkDependLoop(task, relation.getPreTaskObjectGuid(), taskStack, false);
			}
		}

		if (!isParent)
		{
			List<FoundationObject> childList = this.getDomainBean().queryHandler.getChildList(preTask.getObjectGuid());
			if (!SetUtils.isNullList(childList))
			{
				for (FoundationObject foundation : childList)
				{
					if (foundation.getObjectGuid().getGuid().equalsIgnoreCase(task.getGuid()))
					{
						throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DEPEND_LOOP", "Depend Loop" + util.getWBSNumber(), null, util.getWBSNumber());
					}
					this.checkDependLoop(task, foundation.getObjectGuid(), taskStack, false);
				}
			}
		}

		ObjectGuid parentTask = util.getParentTask();
		if (parentTask != null && parentTask.getGuid() != null)
		{
			if (parentTask.getGuid().equalsIgnoreCase(task.getGuid()))
			{
				util.setFoundation(this.getDomainBean().queryHandler.getObject(parentTask));
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DEPEND_LOOP", "Depend Loop" + util.getWBSNumber(), null, util.getWBSNumber());
			}
			else
			{
				this.checkDependLoop(task, parentTask, taskStack, true);
			}
		}
	}

	@Override
	public void hasPMAuthority(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, String operatorGuid) throws ServiceRequestException
	{
		this.authorityCheck.hasPMAuthority(foundationObject, authorityEnum, operatorGuid);
	}

	@Override
	public boolean hasPMAuthorityNoException(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, String operatorGuid)
	{
		return this.authorityCheck.hasPMAuthorityNoException(foundationObject, authorityEnum, operatorGuid);
	}

	@Override
	public boolean isSubProject(FoundationObject task)
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		util.setFoundation(task);
		if (util.isSubProject())
		{
			return true;
		}
		return false;
	}
}
