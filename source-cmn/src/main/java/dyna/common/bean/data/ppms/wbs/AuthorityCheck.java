package dyna.common.bean.data.ppms.wbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.PMAuthorityEnum;
import dyna.common.systemenum.ppms.ProjectOrTaskEnum;
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.RoleTypeEnum;
import dyna.common.systemenum.ppms.TaskPerformerType;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class AuthorityCheck
{
	private static final Map<RoleTypeEnum, List<String>>	projectMap	= new HashMap<RoleTypeEnum, List<String>>();
	private static final Map<RoleTypeEnum, List<String>>	taskMap		= new HashMap<RoleTypeEnum, List<String>>();
	private static boolean									isInit		= false;

	public AuthorityCheck()
	{
		if (!isInit)
		{
			authorityLoad();
		}
	}

	protected static void authorityLoad()
	{

		// 项目管理员有所有权限
		List<String> prj = new ArrayList<String>();
		for (PMAuthorityEnum authEnum : PMAuthorityEnum.values())
		{
			if (authEnum.getType() == ProjectOrTaskEnum.PROJECT || authEnum.getType() == ProjectOrTaskEnum.ALL)
			{
				prj.add(authEnum.getValue());
			}
		}

		projectMap.put(RoleTypeEnum.MANAGER, prj);
		projectMap.put(RoleTypeEnum.PARENTCHARGE, prj);

		// 任务管理员有所有权限
		List<String> task = new ArrayList<String>();
		for (PMAuthorityEnum authEnum : PMAuthorityEnum.values())
		{
			if (authEnum.getType() == ProjectOrTaskEnum.TASK || authEnum.getType() == ProjectOrTaskEnum.ALL)
			{
				task.add(authEnum.getValue());
			}
		}
		taskMap.put(RoleTypeEnum.PARENTCHARGE, task);

		task = new ArrayList<String>();

		task.add(PMAuthorityEnum.START.getValue());
		task.add(PMAuthorityEnum.COMPLETE.getValue());
		task.add(PMAuthorityEnum.SUSPEND.getValue());
		task.add(PMAuthorityEnum.PAUSE.getValue());
		task.add(PMAuthorityEnum.PROJECT_WBS.getValue());
		task.add(PMAuthorityEnum.TASK_TEAMRESOURCES.getValue());
		task.add(PMAuthorityEnum.TASK_MANAGEDELIVERY.getValue());
		task.add(PMAuthorityEnum.TASK_CHANGETOPROJECT.getValue());
		task.add(PMAuthorityEnum.PROJECT_WBS.getValue());
		taskMap.put(RoleTypeEnum.MANAGER, task);

		task = new ArrayList<String>();
		taskMap.put(RoleTypeEnum.PARTICIPATE, task);

		task = new ArrayList<String>();
		task.add(PMAuthorityEnum.TASK_MANAGEDELIVERY.getValue());
		task.add(PMAuthorityEnum.TASK_CHANGETOPROJECT.getValue());
		task.add(PMAuthorityEnum.COMPLETE.getValue());
		task.add(PMAuthorityEnum.START.getValue());
		taskMap.put(RoleTypeEnum.CHARGE, task);

		isInit = true;
	}

	private static boolean hasAuthority(boolean isProject, RoleTypeEnum roleTypeEnum, PMAuthorityEnum AuthorityType)
	{
		if (isProject)
		{
			List<String> list = projectMap.get(roleTypeEnum);
			if (!SetUtils.isNullList(list))
			{
				return list.contains(AuthorityType.getValue());
			}
		}
		else
		{
			List<String> list = taskMap.get(roleTypeEnum);
			if (!SetUtils.isNullList(list))
			{
				return list.contains(AuthorityType.getValue());
			}
		}
		return false;
	}

	public boolean hasPMAuthority(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, String operatorGuid) throws ServiceRequestException
	{
		return this.hasPMAuthority(foundationObject, authorityEnum, true, operatorGuid);
	}

	public boolean hasPMAuthorityNoException(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, String operatorGuid)
	{
		try
		{
			return this.hasPMAuthority(foundationObject, authorityEnum, false, operatorGuid);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	protected boolean checkProjectStatus(FoundationObject foundationObject, PMAuthorityEnum authorityEnum) throws ServiceRequestException
	{

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
		// 标准逻辑
		if (util.getProjectStatusEnum() == ProjectStatusEnum.SSP || util.getProjectStatusEnum() == ProjectStatusEnum.COP)
		{
			throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_LAST", "Project Status is SSP,COP or OBS.Does not Modify");
		}
		if (authorityEnum == PMAuthorityEnum.START)
		{
			if (util.getProjectStatusEnum() == ProjectStatusEnum.RUN)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_RUN", "Project Status is RUN.Does not Need Modify");
			}
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_ECP", "Project Status is ECP.Please Finish Chang");
			}
		}
		else if (authorityEnum == PMAuthorityEnum.PAUSE)
		{
			if (util.getProjectStatusEnum() == ProjectStatusEnum.PUS)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_PAUSE", "Project Status is PAUSE.Does not Need Modify");
			}
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_ECP", "Project Status is ECP.Please Finish Chang");
			}
			if (util.getProjectStatusEnum() != ProjectStatusEnum.RUN)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_NOT_START", "Project Status isn't Start.Does not Need Modify");
			}
		}
		else if (authorityEnum == PMAuthorityEnum.SUSPEND)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_ECP", "Project Status is ECP.Please Finish Chang");
			}
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_WIP", "Project Status is WIP.");
			}
		}
		else if (authorityEnum == PMAuthorityEnum.COMPLETE)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_ECP", "Project Status is ECP.Please Finish Chang");
			}

			// 暂停,运行中的项目可以完成
			if (!(util.getProjectStatusEnum() == ProjectStatusEnum.RUN || util.getProjectStatusEnum() == ProjectStatusEnum.PUS))
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_NOT_START", "Project Status isn't Start.Does not Need Modify");
			}
		}
		else if (authorityEnum == PMAuthorityEnum.PROJECT_TEAMRESOURCES || authorityEnum == PMAuthorityEnum.PROJECT_WBS)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_RELEASE", "Project Status is Release.Please Start Chang");
			}
			if (foundationObject.getStatus() == SystemStatusEnum.PRE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_PRE", "Project Status is in workflow.");
			}
		}

		// PLM自定义逻辑
		if (authorityEnum == PMAuthorityEnum.START)
		{
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				if (util.isProjectPlanApproval())
				{
					throw new ServiceRequestException("ID_APP_PPM_PROJRCT_PLAN_IS_NEED_APPROVAL", "Project Status is wip.Please start plan Approval workflow");
				}
			}
		}
		return true;
	}

	protected boolean checkTaskStatus(FoundationObject foundationObject, PMAuthorityEnum authorityEnum) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);

		if (util.getTaskStatusEnum() == TaskStatusEnum.SSP || util.getTaskStatusEnum() == TaskStatusEnum.COP)
		{
			throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_LAST", "Task Status is SSP,COP or OBS.Does not Modify");
		}
		if (authorityEnum == PMAuthorityEnum.START)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_ECP", "Task Status is ECP.Please Finish Chang");
			}
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_NOT_START", "Project Status is WIP.");
			}
			if (util.getTaskStatusEnum() == TaskStatusEnum.RUN)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_RUN", "Task Status is RUN.Does not Need Modify");
			}
		}
		else if (authorityEnum == PMAuthorityEnum.PAUSE)
		{
			if (util.getTaskStatusEnum() == TaskStatusEnum.PUS)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_PAUSE", "Task Status is PAUSE.Does not Need Modify");
			}
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_ECP", "Task Status is ECP.Please Finish Chang");
			}
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_NOT_PAUSE", "Project Status is WIP.");
			}
			if (util.getTaskStatusEnum() != TaskStatusEnum.RUN)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_NOT_START", "Task Status isn't Start.Does not Need Modify");
			}
		}
		else if (authorityEnum == PMAuthorityEnum.SUSPEND)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_ECP", "Task Status is ECP.Please Finish Chang");
			}
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_WIP", "Project Status is WIP.");
			}
			// if (util.getTaskStatusEnum() != TaskStatusEnum.RUN)
			// {
			// throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_NOT_START",
			// "Task Status isn't Start.Does not Need Modify");
			// }
		}
		else if (authorityEnum == PMAuthorityEnum.COMPLETE)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.ECP)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_ECP", "Task Status is ECP.Please Finish Chang");
			}
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_NOT_COMPLETE", "Project Status is WIP.");
			}
			// 运行中的任务可以完成
			if (!(util.getTaskStatusEnum() == TaskStatusEnum.RUN))
			{
				if(util.getTaskStatusEnum() == TaskStatusEnum.APP)
				{
					
				}
				else
				{
					throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_NOT_START", "Task Status isn't Start.Does not Need Modify");
				}
			}
		}
		else if (authorityEnum == PMAuthorityEnum.TASK_MANAGEDELIVERY)
		{

		}
		else if (authorityEnum == PMAuthorityEnum.PROJECT_WBS || authorityEnum == PMAuthorityEnum.TASK_TEAMRESOURCES)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_RELEASE", "Project Status is Release.Please Start Chang");
			}
		}
		// PLM自定义逻辑
		else if (authorityEnum == PMAuthorityEnum.TASK_CHANGETOPROJECT)
		{
			if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
			{
				throw new ServiceRequestException("ID_APP_PPM_PROJRCT_STATUS_IS_NOT_CHANGE", "Project Status is WIP.");
			}
			if (util.getTaskStatusEnum() == TaskStatusEnum.PUS)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_PUS", "Task Status isn't Start.Does not Need Modify");
			}
		}
		// PLM自定义逻辑
		if (authorityEnum == PMAuthorityEnum.START || authorityEnum == PMAuthorityEnum.PAUSE || authorityEnum == PMAuthorityEnum.COMPLETE
				|| authorityEnum == PMAuthorityEnum.SUSPEND || authorityEnum == PMAuthorityEnum.TASK_TEAMRESOURCES)
		{
			if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_TYPE_IS_SUMMARY", "Summary Task is Auto Execute.Does not Need Modify");
			}
		}
		return true;
	}

	private boolean hasPMAuthority(FoundationObject foundationObject, PMAuthorityEnum authorityEnum, boolean isThrowException, String operatorGuid) throws ServiceRequestException
	{

 		if (!this.isCheck(foundationObject, operatorGuid))
		{
			return true;
		}
		boolean isProject = false;
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
		// 项目与任务判断
		if (util.getOwnerProject() != null && StringUtils.isNullString(util.getOwnerProject().getGuid()))
		{
			isProject = true;
			this.checkProjectStatus(foundationObject, authorityEnum);
		}
		else
		{
			isProject = false;
			this.checkTaskStatus(foundationObject, authorityEnum);
		}

		// 角色判断
		List<RoleTypeEnum> roleTypeList = this.listRoleType(isProject, foundationObject, operatorGuid);

		// 判断权限
		boolean hasAuthority = false;

		for (RoleTypeEnum roleType : roleTypeList)
		{

			hasAuthority = hasAuthority(isProject, roleType, authorityEnum);
			if (hasAuthority)
			{
				break;
			}
		}
		// PLM自定义逻辑
		if (hasAuthority && isProject == false)
		{
			if (authorityEnum == PMAuthorityEnum.START || authorityEnum == PMAuthorityEnum.COMPLETE || authorityEnum == PMAuthorityEnum.TASK_CHANGETOPROJECT)
			{
				if (roleTypeList.contains(RoleTypeEnum.CHARGE) == false)
				{
					util.setFoundation(this.getProject(util.getOwnerProject()));
					if (util.getTaskPerformerTypeEnum() == TaskPerformerType.Owner)
					{
						hasAuthority = false;
					}
				}
			}
			else if (authorityEnum == PMAuthorityEnum.TASK_MANAGEDELIVERY)
			{
				if (roleTypeList.contains(RoleTypeEnum.MANAGER) == false)
				{
					if (foundationObject.getStatus() != SystemStatusEnum.RELEASE)
					{
						hasAuthority = false;
					}
				}
			}
		}

		if (!hasAuthority && isThrowException)
		{
			throw new ServiceRequestException("ID_APP_PM_AUTHORITY_NO_PERMISSION", "has no authority");
		}
		return hasAuthority;
	}

	private List<RoleTypeEnum> listRoleType(boolean isProject, FoundationObject foundationObject, String operatorGuid) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundationObject);
		// 角色判断
		List<RoleTypeEnum> roleTypeList = new ArrayList<RoleTypeEnum>();
		if (isProject)
		{
			List<ProjectRole> roleList = this.listProjectRoleByUser(foundationObject.getObjectGuid(), operatorGuid);
			if (!SetUtils.isNullList(roleList))
			{
				for (ProjectRole role : roleList)
				{
					if (PMConstans.PROJECT_MANAGER_ROLE.equals(role.getRoleId()))
					{
						if (operatorGuid.equalsIgnoreCase(util.getExecutor()))
						{
							roleTypeList.add(RoleTypeEnum.MANAGER);
							roleTypeList.add(RoleTypeEnum.PARENTCHARGE);
						}
					}
					else if (PMConstans.PROJECT_OBSERVER_ROLE.equals(role.getRoleId()))
					{
						roleTypeList.add(RoleTypeEnum.OBSERVER);
					}
					else
					{
						roleTypeList.add(RoleTypeEnum.PARTICIPATE);
					}
				}
			}
		}
		else
		{
			ObjectGuid ownerProject = util.getOwnerProject();

			// 当前任务的项目经理是否为操作者

			FoundationObject prjFoundation = this.getProject(ownerProject);

			if (prjFoundation != null)
			{
				util.setFoundation(prjFoundation);

				if (operatorGuid.equalsIgnoreCase(util.getExecutor()))
				{
					roleTypeList.add(RoleTypeEnum.MANAGER);
					roleTypeList.add(RoleTypeEnum.PARENTCHARGE);
				}
			}

			util.setFoundation(foundationObject);
			if (operatorGuid.equalsIgnoreCase(util.getExecutor()))
			{
				roleTypeList.add(RoleTypeEnum.CHARGE);
			}
			List<TaskMember> listTaskMember = this.listTaskMember(foundationObject.getObjectGuid());
			if (!SetUtils.isNullList(listTaskMember))
			{
				for (TaskMember taskMember : listTaskMember)
				{
					if (taskMember.getUserGuid().equals(operatorGuid))
					{
						roleTypeList.add(RoleTypeEnum.PARTICIPATE);
						break;
					}
				}
			}
		}

		return roleTypeList;
	}

	protected List<TaskMember> listTaskMember(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		return null;
	}

	protected FoundationObject getProject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return null;
	}

	protected List<ProjectRole> listProjectRoleByUser(ObjectGuid projectObjectGuid, String operatorGuid) throws ServiceRequestException
	{
		return null;
	}

	protected boolean isCheck(FoundationObject project, String operatorGuid) throws ServiceRequestException
	{
		return true;
	}
}
