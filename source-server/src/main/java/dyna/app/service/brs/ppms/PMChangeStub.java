/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 变更管理
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.ppms.*;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.systemenum.ppms.MessageTypeEnum;
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangLHB
 *         变更管理
 * 
 */
public class PMChangeStub extends AbstractServiceStub<PPMSImpl>
{

	protected PMChangeStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	protected BOInfo getPMChangeBoInfo() throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = this.stubService.getEMM().listAllSubClassInfoOnlyLeaf(ModelInterfaceEnum.IPMChangeRequest, "");
		BOInfo boInfo = null;
		if (!SetUtils.isNullList(classInfoList))
		{
			ClassInfo classInfo = classInfoList.get(0);
			boInfo = this.stubService.getEMM().getCurrentBoInfoByClassName(classInfo.getName());
		}

		if (boInfo == null)
		{
			throw new ServiceRequestException("ID_APP_PM_CHANGE_REQUEST_BOINFO_NOT_FOUND", "not found project change request boinfo");
		}

		return boInfo;

	}

	protected BOInfo getPMTaskChangeBoInfo() throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = this.stubService.getEMM().listAllSubClassInfoOnlyLeaf(ModelInterfaceEnum.IPMChangeTask, "");
		BOInfo boInfo = null;
		if (!SetUtils.isNullList(classInfoList))
		{
			ClassInfo classInfo = classInfoList.get(0);
			boInfo = this.stubService.getEMM().getCurrentBoInfoByClassName(classInfo.getName());
		}

		if (boInfo == null)
		{
			throw new ServiceRequestException("ID_APP_PM_CHANGE_TASKINFO_BOINFO_NOT_FOUND", "not found task change record boinfo");
		}

		return boInfo;
	}

	public List<FoundationObject> listChangeRequest(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		SearchCondition searchCondition = null;

		searchCondition = SearchConditionFactory.createSearchCondition4Class(this.getPMChangeBoInfo().getClassName(), null, false);
		List<ClassField> fieldList = this.stubService.getEMM().listFieldOfClass(this.getPMChangeBoInfo().getClassName());
		for (ClassField filed : fieldList)
		{
			searchCondition.addResultField(filed.getName());
		}
		searchCondition.addFilter("ChangeObject", projectObjectGuid, OperateSignEnum.EQUALS);
		searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		searchCondition.addOrder(SystemClassFieldEnum.CREATETIME, false);
		List<FoundationObject> list = boas.getFoundationStub().listObject(searchCondition, false);
		return list;
	}

	public FoundationObject createChangeRequest(FoundationObject foundation) throws ServiceRequestException
	{
		BOInfo boInfo = this.getPMTaskChangeBoInfo();

		ObjectGuid projectObjectGuid = this.getProjectObjectGuid(foundation);
		FoundationObject project = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(project);
		boolean pmChangApp = false;
		// 项目经理才可以变更项目
		if (this.stubService.getUserSignature().getUserGuid().equals(util.getExecutor()) == false)
		{
			throw new ServiceRequestException("ID_APP_PM_PROJECT_CHANGE_NOT_PM", "only PM can Chang Project");
		}

		if (util.getProjectStatusEnum() == ProjectStatusEnum.SSP || util.getProjectStatusEnum() == ProjectStatusEnum.COP)
		{
			// 中止，结束，废弃 的项目不可以变更
			throw new ServiceRequestException("ID_APP_PM_PROJECT_STATUS_COP_SSP", "Project is Finish");
		}
		if (project.getStatus() != SystemStatusEnum.RELEASE)
		{
			// 项目计划未发布或启动
			throw new ServiceRequestException("ID_APP_PM_PROJECT_CHANGE_STATUS_COP_SSP", "Project not be started");
		}

		String sessionId = this.stubService.getSignature().getCredential();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			foundation = this.stubService.getBOAS().createObject(foundation);
			InstanceService ds = DataServer.getInstanceService();
			if (pmChangApp == false)
			{
				// 设置变更请求为ECP状态
				ds.changeStatus(foundation.getStatus(), SystemStatusEnum.ECP, foundation.getObjectGuid(), false, sessionId);

				// 设置项目为ECP状态
				ds.changeStatus(project.getStatus(), SystemStatusEnum.ECP, project.getObjectGuid(), false, sessionId);
			}
			// 处理团队
			Map<String, String> roleGuidMap = this.copyTeam(foundation, project);
			// 处理任务
			List<FoundationObject> taskList = this.stubService.getWBSStub().listAllSubTask(projectObjectGuid, true);
			Map<String, ObjectGuid> guidMap = new HashMap<String, ObjectGuid>();
			guidMap.put(projectObjectGuid.getGuid(), foundation.getObjectGuid());
			if (taskList != null)
			{
				for (int i = 0; i < taskList.size(); i++)
				{
					FoundationObject task = taskList.get(i);
					util.setFoundation(task);
					if (pmChangApp == false)
					{
						if (!(util.getTaskStatusEnum() == TaskStatusEnum.SSP || util.getTaskStatusEnum() == TaskStatusEnum.COP))
						{
							// 设置项目为ECP状态
							ds.changeStatus(task.getStatus(), SystemStatusEnum.ECP, task.getObjectGuid(), false, sessionId);
						}
					}
					ObjectGuid oguid = task.getObjectGuid();

					ObjectGuid taskOguid = new ObjectGuid();
					taskOguid.setClassGuid(boInfo.getClassGuid());
					taskOguid.setClassName(boInfo.getClassName());

					task.setObjectGuid(taskOguid);
					task.setStatus(SystemStatusEnum.WIP);
					task.setOwnerGroupGuid(null);
					task.setOwnerGroupGuid(null);

					util.setFoundation(task);

					util.setOwnerProject(foundation.getObjectGuid());
					util.setParentTask(guidMap.get(util.getParentTask().getGuid()));
					util.setFromTask(oguid); // 设置来源任务，以便记录变更标志
					util.setExecuteRole(roleGuidMap.get(util.getExecuteRole()));
					task = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(task, null, false, false);
					guidMap.put(oguid.getGuid(), task.getObjectGuid());
				}
			}
			// 处理任务交付项，资源，前置任务
			// 处理里程碑
			this.copyMilestone(foundation, project, guidMap);
			this.copyPreTask(foundation, project, guidMap);
			this.copyTaskResource(foundation, project, guidMap, roleGuidMap);
			this.copyTaskDeliverableItem(foundation, project, guidMap);
			RelationTemplateInfo templeteObj = this.stubService.getEMM().getRelationTemplateByName(projectObjectGuid, BuiltinRelationNameEnum.PM_CHANGE.toString());
			if (templeteObj != null)
			{
				StructureObject structureObject = this.stubService.getBOAS().newStructureObject(null, templeteObj.getStructureClassName());
				this.stubService.getBOAS().link(projectObjectGuid, foundation.getObjectGuid(), structureObject, BuiltinRelationNameEnum.PM_CHANGE.toString());
			}

			// 项目变更通知
			this.stubService.getNoticeStub().sendMail(project, null, MessageTypeEnum.PROJECTCHANGENOTIFY);

//			DataServer.getTransactionManager().commitTransaction();
			return foundation;
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else if (e instanceof DynaDataException)
			{
				throw ServiceRequestException.createByDynaDataException((DynaDataException) e);
			}
			else
			{
				throw ServiceRequestException.createByException("", e);
			}
		}
	}

	private void copyTaskDeliverableItem(FoundationObject foundation, FoundationObject project, Map<String, ObjectGuid> guidMap) throws ServiceRequestException
	{
		List<DeliverableItem> listAllDeliveryByProject = this.stubService.getDeliveryStub().listDeliveryItemByProject(project.getObjectGuid().getGuid());
		if (SetUtils.isNullList(listAllDeliveryByProject) == false)
		{
			for (DeliverableItem item : listAllDeliveryByProject)
			{
				if (guidMap.get(item.getTaskGuid()) != null)
				{
					item.setGuid(null);
					item.setProjectGuid(guidMap.get(item.getProjectGuid()).getGuid());
					item.setTaskGuid(guidMap.get(item.getTaskGuid()).getGuid());
					this.stubService.getDeliveryStub().saveDeliveryItem(item);
				}
			}
		}
	}

	private void copyTaskResource(FoundationObject foundation, FoundationObject project, Map<String, ObjectGuid> guidMap, Map<String, String> roleGuidMap)
			throws ServiceRequestException
	{
		List<ProjectRole> listProjectRole = this.stubService.getRoleStub().listProjectRoleByProjectGuid(project.getObjectGuid().getGuid());

		if (!SetUtils.isNullList(listProjectRole))
		{
			for (ProjectRole role : listProjectRole)
			{
				List<TaskMember> listTaskMember = this.stubService.getTaskMemberStub().listTaskMemberByRole(role.getGuid());
				if (listTaskMember != null)
				{
					for (TaskMember member : listTaskMember)
					{
						if (guidMap.get(member.getTaskObjectGuid().getGuid()) != null)
						{
							member.setGuid(null);
							member.setProjectRole(roleGuidMap.get(member.getProjectRole()));
							member.setTaskObjectGuid(guidMap.get(member.getTaskObjectGuid().getGuid()));
							this.stubService.getTaskMemberStub().saveTaskMember(member);
						}
					}
				}

			}
		}

	}

	private void copyPreTask(FoundationObject foundation, FoundationObject project, Map<String, ObjectGuid> guidMap) throws ServiceRequestException
	{
		List<TaskRelation> dependList = this.stubService.getTaskStub().listAllTaskRelation(project.getObjectGuid());
		if (SetUtils.isNullList(dependList) == false)
		{
			for (TaskRelation item : dependList)
			{
				if (guidMap.get(item.getTaskObjectGuid().getGuid()) != null && guidMap.get(item.getPreTaskObjectGuid().getGuid()) != null)
				{
					item.setGuid(null);
					item.setProjectObjectGuid(guidMap.get(item.getProjectObjectGuid().getGuid()));
					item.settaskObjectGuid(guidMap.get(item.getTaskObjectGuid().getGuid()));
					item.setpreTaskObjectGuid(guidMap.get(item.getPreTaskObjectGuid().getGuid()));
					this.stubService.getTaskStub().saveTaskRelation(item);
				}
			}
		}
	}

	private void copyMilestone(FoundationObject foundation, FoundationObject project, Map<String, ObjectGuid> guidMap) throws ServiceRequestException
	{
		List<CheckpointConfig> projectMileStone = this.stubService.getBaseConfigStub().listCheckpointConfigByMileStoneGuid(project.getObjectGuid().getGuid());
		if (SetUtils.isNullList(projectMileStone) == false)
		{
			for (CheckpointConfig item : projectMileStone)
			{
				item.setGuid(null);
				item.setTypeGuid(guidMap.get(item.getTypeGuid()).getGuid());
				if (guidMap.get(item.getRelatedTaskObject().getGuid()) != null)
				{
					item.setRelatedTaskObject(guidMap.get(item.getRelatedTaskObject().getGuid()));
				}
				this.stubService.getBaseConfigStub().saveCheckpointConfig(item);
			}
		}
	}

	private Map<String, String> copyTeam(FoundationObject foundation, FoundationObject project) throws ServiceRequestException
	{
		List<ProjectRole> listProjectRole = this.stubService.getRoleStub().listProjectRoleByProjectGuid(project.getObjectGuid().getGuid());
		Map<String, String> returnMap = new HashMap<String, String>();
		if (SetUtils.isNullList(listProjectRole) == false)
		{
			for (ProjectRole item : listProjectRole)
			{
				ProjectRole item1 = (ProjectRole) item.clone();
				String guid = item1.getGuid();
				item1.setGuid(null);
				item1.setTypeGuid(foundation.getObjectGuid().getGuid());
				item1 = this.stubService.getRoleStub().saveProjectRole(item1, false);
				returnMap.put(guid, item1.getGuid());
			}
		}
		if (!SetUtils.isNullList(listProjectRole))
		{
			for (ProjectRole role : listProjectRole)
			{
				List<RoleMembers> listRoleMembers = this.stubService.getRoleStub().listRoleMembers(role.getGuid());
				if (!SetUtils.isNullList(listRoleMembers))
				{
					for (RoleMembers member : listRoleMembers)
					{
						member.setGuid(null);
						member.setProjectRoleGuid(returnMap.get(member.getProjectRoleGuid()));
						this.stubService.getRoleStub().saveProjectRoleUser(member);
					}
				}
			}
		}
		return returnMap;
	}

	public void completeChangeRequest(FoundationObject foundation) throws ServiceRequestException
	{
		this.completeChangeRequest(foundation, false);
	}

	public void completeChangeRequest(FoundationObject foundation, boolean isProc) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		List<FoundationObject> taskList = null;
		FoundationObject project = null;
		try
		{
			ObjectGuid projectObjectGuid = this.getProjectObjectGuid(foundation);

			project = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);
			PPMFoundationObjectUtil putil = new PPMFoundationObjectUtil(project);
			if (isProc == false)
			{
				// 项目经理才可以变更项目
				if (this.stubService.getUserSignature().getUserGuid().equals(putil.getExecutor()) == false)
				{
					throw new ServiceRequestException("ID_APP_PM_PROJECT_CHANGE_NOT_PM", "only PM can Chang Project");
				}

				if ((project.getStatus() != SystemStatusEnum.ECP)) // 判断项目是不是变更中；
				{
					throw new ServiceRequestException("ID_APP_PM_PROJECT_CHANGE_STATUS_NOT_ECP", "Project Status is Not ECP Status");
				}
			}
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			InstanceService ds = DataServer.getInstanceService();
			// 修改项目的状态
			if (project.getStatus() != SystemStatusEnum.RELEASE)
			{
				ds.changeStatus(project.getStatus(), SystemStatusEnum.RELEASE, project.getObjectGuid(), false, sessionId);
			}
			taskList = this.stubService.getWBSStub().listAllSubTask(projectObjectGuid, true);
			// 修改任务的状态
			if (taskList != null)
			{
				PPMFoundationObjectUtil taskUtil = new PPMFoundationObjectUtil(null);
				for (int i = 0; i < taskList.size(); i++)
				{
					FoundationObject task = taskList.get(i);
					if (task.getStatus() == SystemStatusEnum.ECP || task.getStatus() == SystemStatusEnum.WIP) // 判断任务是不是变更中；
					{
						ds.changeStatus(task.getStatus(), SystemStatusEnum.RELEASE, task.getObjectGuid(), false, sessionId);
					}

					taskUtil.setFoundation(task);
					if (taskUtil.getTaskTypeEnum() == TaskTypeEnum.GENERAL || taskUtil.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
					{
						if (!StringUtils.isGuid(taskUtil.getExecutor()))
						{
							throw new ServiceRequestException("ID_APP_PM_START_PROJECT_TASK_EXECUTOR", "task executor is null", null, taskUtil.getFoundation().getId());
						}
					}
				}
			}
			//

			PPMFoundationObjectUtil cutil = new PPMFoundationObjectUtil(foundation);
			// cutil.setNewProjectChangeApproval(putil.isProjectChangeApproval());
			cutil.setNewProjectPlanApproval(putil.isProjectPlanApproval());
			cutil.setNewTaskStartType(putil.getTaskStartType());
			cutil.setNewTaskPerformerType(putil.getTaskPerformerType());
			cutil.setNewImportanceLevel(putil.getImportanceLevel());
			cutil.setNewPlanWorkload(putil.getPlanWorkload());
			cutil.setNewFromTemplate(putil.getFromTemplate());
			cutil.setNewProjectCalendar(putil.getProjectCalendar());
			cutil.setNewProjectType(putil.getProjectType());
			cutil.setNewPlannedDuration(putil.getPlannedDuration());
			cutil.setNewExecutor(putil.getExecutor());
			cutil.setNewPlanFinishTime(putil.getPlanFinishTime());

			cutil.setNewPlanStartTime(putil.getPlanStartTime());
			cutil.setNewProjectName(project.getName());

			((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(foundation, false, false, false, false, null, true, false, false, true);
			// 不要流程审批时，修改变更请求的状态
			if (foundation.getStatus() != SystemStatusEnum.RELEASE)
			{
				ds.changeStatus(foundation.getStatus(), SystemStatusEnum.RELEASE, foundation.getObjectGuid(), false, sessionId);
			}

			this.stubService.getNoticeStub().sendMail(project, null, MessageTypeEnum.PROJECTCHANGEAFTERNOTIFY);

//			DataServer.getTransactionManager().commitTransaction();

		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else if (e instanceof DynaDataException)
			{
				throw ServiceRequestException.createByDynaDataException((DynaDataException) e);
			}
			else
			{
				throw ServiceRequestException.createByException("", e);
			}
		}
		project = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(project.getObjectGuid(), false);
		this.stubService.getProjectStub().calculateProjectInfo(project);
		this.stubService.getProjectStub().checkSummery(project);
	}

	private ObjectGuid getProjectObjectGuid(FoundationObject foundation) throws ServiceRequestException
	{
		ObjectGuid projectObjectGuid = new ObjectGuid();
		projectObjectGuid.setClassGuid((String) foundation.get("ChangeObject" + PMConstans.CLASS));
		projectObjectGuid.setMasterGuid((String) foundation.get("ChangeObject" + PMConstans.MASTER));
		projectObjectGuid.setGuid((String) foundation.get("ChangeObject"));
		projectObjectGuid.setClassName(this.stubService.getEMM().getClassByGuid(projectObjectGuid.getClassGuid()).getName());
		return projectObjectGuid;
	}
}
