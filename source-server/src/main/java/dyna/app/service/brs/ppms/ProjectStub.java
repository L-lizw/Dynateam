/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理项目实例
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.*;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainBean;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainUpdateBean;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.bean.data.ppms.wbs.WBSDriver;
import dyna.common.dto.Folder;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.systemenum.ppms.*;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.PPMS;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author WangLHB
 *         项目的查询，新建
 */
@Component
public class ProjectStub extends AbstractServiceStub<PPMSImpl>
{

	/**
	 * @param foundationObject
	 * @param isTemplate
	 * @param isStartDate
	 * @param plannedDuration
	 *            工期至少有1天，所以初始值应该为1
	 * @throws ServiceRequestException
	 */
	public void initNewFoundation(FoundationObject foundationObject, boolean isTemplate, boolean isStartDate, int plannedDuration) throws ServiceRequestException
	{
		PPMFoundationObjectUtil targetObj = new PPMFoundationObjectUtil(foundationObject);
		targetObj.setActualFinishTime(null);
		targetObj.setActualStartTime(null);
		targetObj.setActualWorkload(0D);
		targetObj.setActualDuration(0);
		targetObj.setCompletionRate(null);
		targetObj.setOriginalStatus(null);
		CodeItemInfo onTimeStateItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.ONTIMESTATE.getValue(), OnTimeStateEnum.NOTSTARTED.getValue());
		targetObj.setOnTimeState(onTimeStateItem.getGuid());
		targetObj.setOnTimeStateEnum(OnTimeStateEnum.NOTSTARTED);
		if (StringUtils.isNullString(targetObj.getExecutor()))
		{
			targetObj.setExecutor(this.stubService.getOperatorGuid());
		}

		CodeItemInfo subCodeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.EXECUTESTATUS.getValue(), ProjectStatusEnum.INI.getValue());
		targetObj.setProjectStatus(subCodeItem.getGuid());
		foundationObject.setOwnerUserGuid(this.stubService.getUserSignature().getUserGuid());
		foundationObject.setOwnerGroupGuid(this.stubService.getUserSignature().getLoginGroupGuid());
		foundationObject.setStatus(SystemStatusEnum.WIP);

		PMCalendar projectCalendar = this.getProjectCalendar(targetObj.getProjectCalendar());

		targetObj.setProjectCalendar(projectCalendar.getGuid());

		if (!isTemplate)
		{
			if (isStartDate)
			{
				Date date = new Date();
				if (targetObj.getPlanStartTime() != null)
				{
					date = targetObj.getPlanStartTime();
				}
				Calendar xCalendar = this.getCalendarByDate(date);
				targetObj.setPlanStartTime(xCalendar.getTime());
				Date addDay = projectCalendar.addDay(targetObj.getPlanStartTime(), plannedDuration - 1);

				xCalendar = this.getCalendarByDate(addDay);
				targetObj.setPlanFinishTime(xCalendar.getTime());
			}
			else
			{
				Date date = new Date();
				if (targetObj.getPlanFinishTime() != null)
				{
					date = targetObj.getPlanFinishTime();
				}
				Calendar xCalendar = this.getCalendarByDate(date);
				targetObj.setPlanFinishTime(xCalendar.getTime());

				Date addDay = projectCalendar.decDay(targetObj.getPlanFinishTime(), plannedDuration - 1);
				xCalendar = this.getCalendarByDate(addDay);
				targetObj.setPlanStartTime(xCalendar.getTime());

			}
		}
	}

	/**
	 * 取得项目日历
	 * 如果项目未设置日历,则使用标准日历,否则使用所设置的日历
	 *
	 * @param projectCalendar
	 * @return
	 * @throws ServiceRequestException
	 */
	private PMCalendar getProjectCalendar(String projectCalendarGuid) throws ServiceRequestException
	{
		PMCalendar projectCalendar = null;
		if (StringUtils.isNullString(projectCalendarGuid))
		{
			projectCalendar = this.stubService.getCalendarStub().getStandardWorkCalendar();
		}
		else
		{
			projectCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendarGuid);
		}
		return projectCalendar;
	}

	private Calendar getCalendarByDate(Date date)
	{
		Calendar xCalendar = Calendar.getInstance();
		xCalendar.setTime(date);
		xCalendar.set(Calendar.HOUR_OF_DAY, 0);
		xCalendar.set(Calendar.MINUTE, 0);
		xCalendar.set(Calendar.SECOND, 0);
		xCalendar.set(Calendar.MILLISECOND, 0);
		return xCalendar;
	}

	protected void copyProjectField(FoundationObject source, FoundationObject target)
	{
		PPMFoundationObjectUtil templateObj = new PPMFoundationObjectUtil(source);
		PPMFoundationObjectUtil targetObj = new PPMFoundationObjectUtil(target);

		// id
		if (StringUtils.isNullString(target.getId()))
		{
			try
			{
				ClassStub.decorateObjectGuid(target.getObjectGuid(), this.stubService);

				String targetClassName = target.getObjectGuid().getClassName();
				ClassInfo targetClassInfo = this.stubService.getEMM().getClassByName(targetClassName);
				if (targetClassInfo.hasInterface(ModelInterfaceEnum.IPMProjectTemplate))
				{
					target.setId(source.getId());
				}
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
		}

		// 项目名称
		if (StringUtils.isNullString(target.getName()))
		{
			target.setName(source.getName());
		}

		// 项目类型
		if (targetObj.getProjectType() == null || targetObj.getProjectType().getGuid() == null)
		{
			targetObj.setProjectType(templateObj.getProjectType());
		}

		// 项目日历
		if (targetObj.getProjectCalendar() == null)
		{
			targetObj.setProjectCalendar(templateObj.getProjectCalendar());
		}

		targetObj.setProjectPlanApproval(templateObj.isProjectPlanApproval());
		targetObj.setTaskStartType(templateObj.getTaskStartType());
		targetObj.setTaskPerformerType(templateObj.getTaskPerformerType());
		targetObj.setPlanWorkload(templateObj.getPlanWorkload());
		if (targetObj.getImportanceLevel() == null)
		{
			targetObj.setImportanceLevel(templateObj.getImportanceLevel());
		}

	}

	public FoundationObject createProjectFromTemplate(FoundationObject toProject, ObjectGuid templateProjectObjectGuid) throws ServiceRequestException
	{
		// 复制wbs
		FoundationObject foundationObjectTemplate = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(templateProjectObjectGuid, false);
		if (foundationObjectTemplate == null)
		{
			throw new ServiceRequestException("ID_PROJECT_TEMPLATE_NOT_EXIST", "Project Template isn't Exist");
		}

		PPMFoundationObjectUtil targetObj = new PPMFoundationObjectUtil(toProject);
		PPMFoundationObjectUtil templateObj = new PPMFoundationObjectUtil(foundationObjectTemplate);

		boolean isStart = true;
		Date startDate = null;
		PMCalendar projectCalendar = this.getProjectCalendar(targetObj.getProjectCalendar());

		this.copyProjectField(foundationObjectTemplate, toProject);

		if (targetObj.getPlanFinishTime() != null)
		{
			startDate = projectCalendar.decDay(targetObj.getPlanFinishTime(), templateObj.getPlannedDuration() - 1);
			targetObj.setPlanStartTime(startDate);
		}
		else
		{
			startDate = targetObj.getPlanStartTime();
		}

		targetObj.setFoundation(foundationObjectTemplate);
		this.initNewFoundation(toProject, false, isStart, templateObj.getPlannedDuration());

		targetObj.setFoundation(toProject);

		if (startDate == null)
		{
			startDate = targetObj.getPlanStartTime();
		}

		if (startDate != null)
		{
			startDate = DateFormat.parse(DateFormat.formatYMD(startDate));
		}

		BOInfo boInfo = this.stubService.getPMTaskBoInfo(false);
		if (boInfo == null)
		{
			throw new ServiceRequestException("ID_PPM_NO_TASK_CLASS", "Task Class isn't");
		}

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(templateProjectObjectGuid.getClassGuid());
		String copyType = PMConstans.COPY_TYPE_P2P;
		if (classInfo.hasInterface(ModelInterfaceEnum.IPMProjectTemplate))
		{
			copyType = PMConstans.COPY_TYPE_T2P;
		}

		toProject.setId(StringUtils.convertNULLtoString(toProject.getId()));
		toProject = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(toProject, true);

		WBSDriver driveWbsContain = this.stubService.getWBSStub().driveWbsContain(foundationObjectTemplate, startDate, projectCalendar, toProject, copyType);

		PPMFoundationObjectUtil oldUtil = new PPMFoundationObjectUtil(driveWbsContain.getQueryHandler().getRootObject());
		targetObj.setFoundation(toProject);
		targetObj.setPlanWorkload(oldUtil.getPlanWorkload());
		targetObj.setPlannedDuration(oldUtil.getPlannedDuration());
		targetObj.setPlanStartTime(oldUtil.getPlanStartTime());
		targetObj.setPlanFinishTime(oldUtil.getPlanFinishTime());

		this.saveObject(toProject, false, false);

		boolean isCopyM = true;
		List<CheckpointConfig> listCheckpointConfigByMileStoneGuid = null;
		if (targetObj.getProjectType().getGuid() != null && !targetObj.getProjectType().getGuid().equals(oldUtil.getProjectType().getGuid()))
		{
			isCopyM = false;
			if (!isCopyM && targetObj.getProjectType().getGuid() != null)
			{
				listCheckpointConfigByMileStoneGuid = this.stubService.getBaseConfigStub().listCheckpointConfigByMileStoneGuid(targetObj.getProjectType().getGuid());
			}
		}

		InstanceDomainBean copyInstanceDomain = driveWbsContain.getUpdateHandler().copyInstanceDomain(toProject, boInfo.getClassGuid(), copyType, isCopyM,
				listCheckpointConfigByMileStoneGuid);
		this.stubService.getWBSStub().saveInstanceDomain(copyInstanceDomain.getInstanceDomainUpdateBean());

		AbstractWBSDriver driver = this.stubService.getWBSStub().createWBSDriver(copyInstanceDomain.getInstanceDomainUpdateBean().getRootObject(),
				this.stubService.getOperatorGuid());
		driver.getCalculateHandler().calculatePlanStartTime(driver.getRootObject(), null);
		this.stubService.getWBSStub().saveInstanceDomain(driver.getInstanceDomainUpdateBean());

		return toProject;

	}

	public FoundationObject createTemplateFromProject(FoundationObject foundationObject, ObjectGuid templateProjectObjectGuid) throws ServiceRequestException
	{
		// 复制wbs
		FoundationObject foundationObjectTemplate = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(templateProjectObjectGuid, false);
		if (foundationObjectTemplate == null)
		{
			throw new ServiceRequestException("ID_PROJECT_TEMPLATE_NOT_EXIST", "Project Template isn't Exist");
		}

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(templateProjectObjectGuid.getClassGuid());
		String copyType = PMConstans.COPY_TYPE_P2T;
		if (classInfo.hasInterface(ModelInterfaceEnum.IPMProjectTemplate))
		{
			copyType = PMConstans.COPY_TYPE_T2T;
		}
		PPMFoundationObjectUtil targetObj = new PPMFoundationObjectUtil(foundationObject);

		PMCalendar projectCalendar = this.getProjectCalendar(targetObj.getProjectCalendar());
		targetObj.setFoundation(foundationObjectTemplate);
		this.initNewFoundation(foundationObject, true, true, targetObj.getPlannedDuration());
		this.copyProjectField(foundationObjectTemplate, foundationObject);
		BOInfo boInfo = this.stubService.getPMTaskBoInfo(true);
		if (boInfo == null)
		{
			throw new ServiceRequestException("ID_PPM_NO_TASK_CLASS", "Task Class isn't");
		}

		foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(foundationObject, true);

		WBSDriver driveWbsContain = this.stubService.getWBSStub().driveWbsContain(foundationObjectTemplate, null, projectCalendar, foundationObject, copyType);

		PPMFoundationObjectUtil oldUtil = new PPMFoundationObjectUtil(driveWbsContain.getQueryHandler().getRootObject());
		targetObj.setFoundation(foundationObject);

		// 判断是否复制里程碑
		boolean isCopyM = true;
		List<CheckpointConfig> listCheckpointConfigByMileStoneGuid = null;
		if (oldUtil.getProjectType().getGuid() != null && !oldUtil.getProjectType().getGuid().equals(targetObj.getProjectType().getGuid()))
		{
			isCopyM = false;
			if (!isCopyM && targetObj.getProjectType().getGuid() != null)
			{
				listCheckpointConfigByMileStoneGuid = this.stubService.getBaseConfigStub().listCheckpointConfigByMileStoneGuid(targetObj.getProjectType().getGuid());
			}
		}
		InstanceDomainBean copyInstanceDomain = driveWbsContain.getUpdateHandler().copyInstanceDomain(foundationObject, boInfo.getClassGuid(), copyType, isCopyM,
				listCheckpointConfigByMileStoneGuid);
		this.stubService.getWBSStub().saveInstanceDomain(copyInstanceDomain.getInstanceDomainUpdateBean());

		return foundationObject;

	}

	/**
	 * 创建项目或模板
	 * 从模板创建项目，从项目创建模板，从模板创建模板
	 *
	 * @param pmFoundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject createProject(FoundationObject pmFoundationObject) throws ServiceRequestException
	{
		FoundationObject project = null;

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			ClassStub.decorateObjectGuid(pmFoundationObject.getObjectGuid(), this.stubService);

			String className = pmFoundationObject.getObjectGuid().getClassName();
			ClassInfo classInfo = this.stubService.getEMM().getClassByName(className);
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(pmFoundationObject);
			// 创建模板
			if (classInfo.hasInterface(ModelInterfaceEnum.IPMProjectTemplate))
			{
				// 直接创建模板
				if (StringUtils.isNullString(util.getFromTemplate().getGuid()))
				{
					boolean isStart = true;
					if (util.getPlanFinishTime() != null)
					{
						isStart = false;
					}
					this.initNewFoundation(pmFoundationObject, true, isStart, 1);
					project = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(pmFoundationObject, true);

					// 根据项目类型复制项目角色
					ObjectGuid projectType = util.getProjectType();
					if (!StringUtils.isNullString(projectType.getGuid()))
					{
						this.stubService.getBaseConfigStub().copyCheckpointConfigByMileStone(projectType.getGuid(), project.getObjectGuid().getGuid(), "2");
					}

					this.createBasePMRole(project);
				}
				else
				{
					// 项目创建模板，从模板创建模板
					project = this.createTemplateFromProject(pmFoundationObject, util.getFromTemplate());
				}
			}
			else if (classInfo.hasInterface(ModelInterfaceEnum.IPMProject))
			{
				// 直接创建项目
				if (StringUtils.isNullString(util.getFromTemplate().getGuid()))
				{
					boolean isStart = true;
					if (util.getPlanFinishTime() != null)
					{
						isStart = false;
					}
					// 创建项目
					this.initNewFoundation(pmFoundationObject, false, isStart, util.getPlannedDuration());
					project = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(pmFoundationObject, true);

					ObjectGuid projectType = util.getProjectType();
					if (!StringUtils.isNullString(projectType.getGuid()))
					{
						this.stubService.getBaseConfigStub().copyCheckpointConfigByMileStone(projectType.getGuid(), project.getObjectGuid().getGuid(), "2");
					}

					this.createBasePMRole(project);
				}
				else
				{
					// 从模板创建项目
					project = this.createProjectFromTemplate(pmFoundationObject, util.getFromTemplate());
				}
			}

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
		return project;
	}

	private void createBasePMRole(FoundationObject project) throws ServiceRequestException
	{
		try
		{
			ProjectRole managerRole = new ProjectRole();
			managerRole.setRoleId(PMConstans.PROJECT_MANAGER_ROLE);
			managerRole.setRoleName(this.stubService.getMSRM().getMSRString("ID_APP_PM_MANAGER_ROLE_NAME", this.stubService.getUserSignature().getLanguageEnum().toString()));
			managerRole.setTypeGuid(project.getObjectGuid().getGuid());
			managerRole.setTypeClassGuid(project.getObjectGuid().getClassGuid());
			managerRole = this.stubService.getRoleStub().saveProjectRole(managerRole, false);
			List<String> userGuidList = new ArrayList<String>();
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(project);
			if (!StringUtils.isNullString(util.getExecutor()))
			{
				userGuidList.add(util.getExecutor());
			}
			else
			{
				userGuidList.add(this.stubService.getOperatorGuid());
			}
			this.stubService.getRoleStub().saveUserInProjectRole(managerRole.getGuid(), userGuidList);

			ProjectRole obsoleteRole = new ProjectRole();
			obsoleteRole.setRoleId(PMConstans.PROJECT_OBSERVER_ROLE);
			obsoleteRole.setRoleName(this.stubService.getMSRM().getMSRString("ID_APP_PM_RECEIVER_ROLE_NAME", this.stubService.getUserSignature().getLanguageEnum().toString()));
			obsoleteRole.setTypeGuid(project.getObjectGuid().getGuid());
			this.stubService.getRoleStub().saveProjectRole(obsoleteRole, false);
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	protected void deleteProject(FoundationObject pmFoundationObject) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			this.stubService.getBOAS().deleteObject(pmFoundationObject);
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
			ClassStub.decorateObjectGuid(pmFoundationObject.getObjectGuid(), this.stubService);
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new DeleteScheduledTask(serviceContext, pmFoundationObject.getObjectGuid()));
		}
	}

	public FoundationObject startProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);
		if (util.isProjectPlanApproval() && foundation.getStatus() != SystemStatusEnum.RELEASE)
		{
			throw new ServiceRequestException("ID_APP_PM_PROJECT_PLAN_NOT_APPROVAL", "project hasn't been approvaled");
		}
		// 1.判断是否有启动权限
		this.stubService.getPMAuthorityStub().hasPMAuthority(foundation, PMAuthorityEnum.START);

		if (!this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
		{
			throw new ServiceRequestException("ID_APP_PM_PROJECT_EXECUTOR_NOT_ACL", "project executor is null");
		}

		if (foundation.getStatus() != SystemStatusEnum.RELEASE)
		{
			// 2. 创建状态只否需要工作流审批才能启动
			if (util.isProjectPlanApproval())
			{
				throw new ServiceRequestException("ID_APP_PM_PROJECT_START_STATUS_CCT", "Project start is need WFApproved");
			}
		}

		CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(ProjectStatusEnum.RUN);

		util.setProjectStatus(codeItem.getGuid());
		if (util.getActualStartTime() == null)
		{
			util.setActualStartTime(new Date());
		}
		// 计划开始/结束时间
		if (util.getPlanFinishTime() == null || util.getPlanStartTime() == null)
		{
			throw new ServiceRequestException("ID_APP_PM_START_PLANTIME_ISNULL", "plan start time or plan finish time is not be started");
		}
		List<FoundationObject> listAllSubTask = this.stubService.getWBSStub().listAllSubTask(foundation.getObjectGuid(), true);
		if (!SetUtils.isNullList(listAllSubTask))
		{
			PPMFoundationObjectUtil taskUtil = new PPMFoundationObjectUtil(null);
			for (FoundationObject task : listAllSubTask)
			{
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
		// 变更所有任务的状态
		codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.RUN);
		this.stubService.getTaskStub().changeTaskStartProject(foundation, foundation, codeItem.getGuid(), true);

		this.saveObject(foundation, false, true);
		if (foundation.getStatus() != SystemStatusEnum.RELEASE)
		{
			String sessionId = this.stubService.getSignature().getCredential();
			DataServer.getInstanceService().changeStatus(SystemStatusEnum.WIP, SystemStatusEnum.RELEASE, projectObjectGuid, false, sessionId);
			if (!SetUtils.isNullList(listAllSubTask))
			{
				for (FoundationObject task : listAllSubTask)
				{
					if (task.getStatus() != SystemStatusEnum.RELEASE && task.getStatus() != SystemStatusEnum.OBSOLETE)
					{
						DataServer.getInstanceService().changeStatus(task.getStatus(), SystemStatusEnum.RELEASE, task.getObjectGuid(), false, sessionId);
					}
				}
			}
		}

		FoundationObject retFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);

		this.stubService.getTaskStub().updateOperateStatus(foundation);
		// 通知--项目启动通知
		this.stubService.getNoticeStub().sendMail(retFoundation, null, MessageTypeEnum.PROJECTSTARTNOTIFY);

		// 通知--任务分配通知
		this.sendTaskAssignMail(retFoundation);

		this.calculateProjectInfo(foundation);

		return retFoundation;
	}

	/**
	 * 通知--任务分配通知
	 *
	 * @param retFoundation
	 * @throws ServiceRequestException
	 */
	private void sendTaskAssignMail(FoundationObject retFoundation) throws ServiceRequestException
	{
		if (retFoundation != null)
		{
			List<ClassInfo> taskClassList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IPMTask);
			ClassInfo taskClassInfo = taskClassList.get(0);
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(taskClassInfo.getName(), null, true);
			searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
			searchCondition.addFilter(PPMFoundationObjectUtil.OWNERPROJECT, retFoundation.getObjectGuid(), OperateSignEnum.EQUALS);
			searchCondition.startGroup();
			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.TASKTYPE.getValue(), TaskTypeEnum.GENERAL.getValue());
			if (codeItem != null)
			{
				searchCondition.addFilterWithOR(PPMFoundationObjectUtil.TASKTYPE, codeItem.getGuid(), OperateSignEnum.YES);
			}
			CodeItemInfo codeItem1 = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.TASKTYPE.getValue(), TaskTypeEnum.MILESTONE.getValue());
			if (codeItem != null)
			{
				searchCondition.addFilterWithOR(PPMFoundationObjectUtil.TASKTYPE, codeItem1.getGuid(), OperateSignEnum.YES);
			}
			searchCondition.endGroup();
			searchCondition.addResultField(PPMFoundationObjectUtil.EXECUTOR);

			List<FoundationObject> taskObjectList = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
			if (!SetUtils.isNullList(taskObjectList))
			{
				PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
				for (FoundationObject foun : taskObjectList)
				{
					util.setFoundation(foun);
					if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
					{
						this.stubService.getNoticeStub().sendMail(retFoundation, foun, MessageTypeEnum.TASKASSIGNNOTIFY);
					}
				}
			}
		}

	}

	public FoundationObject completeProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);

		// 1.判断是否有完成权限
		this.stubService.getPMAuthorityStub().hasPMAuthority(foundation, PMAuthorityEnum.COMPLETE);

		List<FoundationObject> list = this.stubService.listSubTask(projectObjectGuid);
		if (!SetUtils.isNullList(list))
		{
			PPMFoundationObjectUtil utilEnd2 = new PPMFoundationObjectUtil(foundation);
			for (FoundationObject end2 : list)
			{
				utilEnd2.setFoundation(end2);

				if (!(utilEnd2.getTaskStatusEnum() == TaskStatusEnum.COP || utilEnd2.getTaskStatusEnum() == TaskStatusEnum.SSP))
				{
					// 第一层任务存在除完成，废弃，中断外的状态，项目不可以被完成
					throw new ServiceRequestException("ID_APP_PM_PROJECT_COMPLETE_TASKSTATUS_OTHERS", "project not be Complete");
				}
			}
		}

		CodeItemInfo codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(ProjectStatusEnum.COP);
		util.setProjectStatus(codeItem.getGuid());

		LaborHourConfig workTimeConfig = this.stubService.getBaseConfigStub().getWorkTimeConfig();
		Double planWorkload = util.getPlanWorkload() == null ? util.getPlannedDuration() * workTimeConfig.getStandardhour() : util.getPlanWorkload();
		util.setActualWorkload(planWorkload);
		util.setCompletionRate(100d);
		util.setActualFinishTime(new Date());

		String projectCalendar = util.getProjectCalendar();
		PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
		if (workCalendar != null)
		{
			int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
			util.setActualDuration(actualDuration);
		}

		FoundationObject retFoundation = this.saveObject(foundation, true, true);

		this.stubService.getTaskStub().updateOperateStatus(foundation);

		this.stubService.getNoticeStub().sendMail(retFoundation, null, MessageTypeEnum.PROJECTCOMPLETENOTIFY);

		this.calculateProjectInfo(foundation);

		return retFoundation;
	}

	public FoundationObject pauseProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);

		// 1.判断是否有暂停权限
		this.stubService.getPMAuthorityStub().hasPMAuthority(foundation, PMAuthorityEnum.PAUSE);

		CodeItemInfo codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(ProjectStatusEnum.PUS);
		util.setProjectStatus(codeItem.getGuid());

		codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(TaskStatusEnum.PUS);
		this.stubService.getTaskStub().changeTaskPauseProject(foundation, foundation, codeItem.getGuid());

		FoundationObject retFoundation = this.saveObject(foundation, true, true);
		this.stubService.getTaskStub().updateOperateStatus(foundation);
		this.stubService.getNoticeStub().sendMail(retFoundation, null, MessageTypeEnum.PROJECTPAUSENOTIFY);

		return retFoundation;
	}

	protected FoundationObject suspendProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);

		// 1.判断是否有中止权限
		this.stubService.getPMAuthorityStub().hasPMAuthority(foundation, PMAuthorityEnum.SUSPEND);

		CodeItemInfo codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(ProjectStatusEnum.SSP);
		util.setProjectStatus(codeItem.getGuid());

		codeItem = this.stubService.getBaseConfigStub().getCodeItemByEnum(TaskStatusEnum.SSP);
		this.stubService.getTaskStub().changeTaskSuspendProject(foundation, foundation, codeItem.getGuid());

		if (util.getActualStartTime() != null)
		{
			util.setActualFinishTime(new Date());
			String projectCalendar = util.getProjectCalendar();
			PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
			if (workCalendar != null)
			{
				int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
				util.setActualDuration(actualDuration);
			}
		}

		FoundationObject retFoundation = this.saveObject(foundation, true, true);

		this.stubService.getNoticeStub().sendMail(retFoundation, null, MessageTypeEnum.PROJECTENDNOTIFY);

		this.stubService.getTaskStub().updateOperateStatus(foundation);

		this.calculateProjectInfo(foundation);

		return retFoundation;
	}

	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isUpdateTime) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(foundationObject, hasReturn, false, false, false, null, false, false, isUpdateTime, true);
	}

	protected BOInfo getPMProjectTemplateBoInfo() throws ServiceRequestException
	{
		List<BOInfo> boInfoList = null;

		boInfoList = this.stubService.getEMM().listBizObjectByInterface(ModelInterfaceEnum.IPMProjectTemplate);

		if (!SetUtils.isNullList(boInfoList))
		{
			return boInfoList.get(0);
		}
		else
		{
			throw new ServiceRequestException("ID_APP_PM_PROJECT_BOINFO_NOT_FOUND", "not found project boinfo");

		}
	}

	public boolean isProject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getObjectGuid().getClassGuid());
		if (classInfo.hasInterface(ModelInterfaceEnum.IPMTask) || classInfo.getInterfaceList().contains(ModelInterfaceEnum.IPMProject))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listProject(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.listProject(searchCondition, false);
	}

	/**
	 * @param searchCondition
	 * @param isCheckAth
	 *            是否检查权限
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> listProject(SearchCondition condition, boolean isCheckAuth) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			if (SearchCategoryEnum.PROJ_OBSERVE == condition.getSearchCategory())
			{
				Role role = this.stubService.getAAS().getRoleById("receiver");
				Group group = this.stubService.getAAS().getGroupById("receiver");
				RIG rig = this.stubService.getAAS().getRIGByGroupAndRole(group.getGuid(), role.getGuid());
				if (this.stubService.getAAS().isUserInRIG(rig.getGuid(), this.stubService.getUserSignature().getUserGuid()))
				{
					condition.setSearchCategory(null);
				}
			}

			BOInfo pmProjectBoInfo = this.stubService.getPMProjectBoInfo();
			List<ClassField> listFiled = this.stubService.getEMM().listFieldOfClass(pmProjectBoInfo.getClassName());
			if (!SetUtils.isNullList(listFiled) && listFiled.size() > 0)
			{
				for (ClassField field : listFiled)
				{
					condition.addResultField(field.getName());
				}
			}
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(condition.getObjectGuid(), this.stubService);

			List<FoundationObject> results = ds.query(condition, Constants.isSupervisor(isCheckAuth, this.stubService), sessionId);

			if (SetUtils.isNullList(results))
			{
				return results;
			}

			EMM emm = this.stubService.getEMM();

			Set<String> fieldNames = emm.getObjectFieldNamesInSC(condition);
			Set<String> fieldCodeNames = emm.getCodeFieldNamesInSC(condition);
			Folder folder = condition.getFolder();

			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			for (FoundationObject fObject : results)
			{
				DecoratorFactory.decorateFoundationObject(fieldNames, fObject, emm, bmGuid, folder);
				DecoratorFactory.decorateFoundationObjectCode(fieldCodeNames, fObject, emm, bmGuid);
			}
			DecoratorFactory.decorateFoundationObject(fieldNames, results, emm, sessionId);
			return results;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	protected BOInfo getPMProjectBoInfo() throws ServiceRequestException
	{
		List<BOInfo> boInfoList = this.stubService.getEMM().listBizObjectByInterface(ModelInterfaceEnum.IPMProject);
		BOInfo boInfo = null;
		if (!SetUtils.isNullList(boInfoList))
		{
			boInfo = boInfoList.get(0);
			boolean isBreak = false;
			while (!isBreak)
			{

				if (StringUtils.isNullString(boInfo.getParentBOGuid()))
				{
					isBreak = true;
				}
				else
				{
					BOInfo paretnBoInfo = this.stubService.getEMM().getCurrentBizObjectByGuid(boInfo.getParentBOGuid());
					ClassInfo parentClassInfo = null;
					if (paretnBoInfo.getType() == BusinessModelTypeEnum.CLASS)
					{
						parentClassInfo = this.stubService.getEMM().getClassByGuid(paretnBoInfo.getClassGuid());
					}
					if (parentClassInfo != null && parentClassInfo.hasInterface(ModelInterfaceEnum.IPMProject))
					{
						boInfo = paretnBoInfo;
					}
					else
					{
						isBreak = true;
					}

				}
			}
		}

		if (boInfo == null)
		{
			throw new ServiceRequestException("ID_APP_PM_PROJECT_BOINFO_NOT_FOUND", "not found project boinfo");
		}

		return boInfo;
	}

	public FoundationObject createSubProject(FoundationObject task, FoundationObject project) throws ServiceRequestException
	{
		this.stubService.getPMAuthorityStub().hasPMAuthority(task, PMAuthorityEnum.TASK_CHANGETOPROJECT);

		PPMFoundationObjectUtil taskUtil = new PPMFoundationObjectUtil(task);
		FoundationObject result = null;

		Integer plannedDuration = taskUtil.getPlannedDuration();
		if (plannedDuration == null)
		{
			plannedDuration = 0;
		}

		PPMFoundationObjectUtil pUtil = new PPMFoundationObjectUtil(project);
		pUtil.setPlannedDuration(plannedDuration);
		pUtil.setRelationTask(task.getObjectGuid());

		result = this.stubService.createProject(project);

		if (result != null)
		{
			taskUtil.setRelationProject(result.getObjectGuid());
			((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(task, false, false, false, false, null, false, false, false, true);
		}

		return result;
	}

	public FoundationObject changeToSubProject(FoundationObject task) throws ServiceRequestException
	{
		this.stubService.getPMAuthorityStub().hasPMAuthority(task, PMAuthorityEnum.TASK_CHANGETOPROJECT);

		BOInfo boinfo = this.stubService.getPMProjectBoInfo();
		FoundationObject newFoundationObject = this.stubService.getBOAS().newFoundationObject(boinfo.getClassGuid(), null);
		PPMFoundationObjectUtil taskUtil = new PPMFoundationObjectUtil(task);
		FoundationObject result = null;

		if (taskUtil.getTaskTypeEnum() == TaskTypeEnum.SUMMARY || taskUtil.isSubProject() || taskUtil.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
		{
			throw new ServiceRequestException("ID_APP_PM_NOT_CHANGETO_SUBPRJ", "not change to subprj");
		}

		FoundationObject project = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskUtil.getOwnerProject(), false);
		String id = StringUtils.convertNULLtoString(project.getId()) + "-" + StringUtils.convertNULLtoString(task.getId());
		int i = 0;
		String newID = id;
		while (i < 10)
		{

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(project.getObjectGuid().getClassName(), null, false);
			if (i != 0)
			{
				newID = id + "(" + i + ")";
			}
			searchCondition.addFilter(SystemClassFieldEnum.ID.getName(), newID, OperateSignEnum.EQUALS);

			searchCondition.setPageSize(10);
			List<FoundationObject> listObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
			if (SetUtils.isNullList(listObject))
			{
				i = 10;
			}
			i++;

		}

		newFoundationObject.setName(task.getName());

		PPMFoundationObjectUtil pUtil = new PPMFoundationObjectUtil(newFoundationObject);
		pUtil.getFoundation().setId(newID);
		pUtil.setPlanStartTime(taskUtil.getPlanStartTime());
		pUtil.setPlanFinishTime(taskUtil.getPlanFinishTime());
		pUtil.setPlannedDuration(taskUtil.getPlannedDuration());
		pUtil.setExecutor(taskUtil.getExecutor());
		pUtil.setRelationTask(task.getObjectGuid());

		result = this.stubService.createProject(newFoundationObject);

		if (result != null)
		{
			taskUtil.setRelationProject(result.getObjectGuid());
			result = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(task, false, false, false, false, null, false, false, false, true);
		}
		return result;
	}

	public List<FoundationObject> listProjectFramework(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<FoundationObject> objectList = null;
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		if (searchCondition != null)
		{
			ObjectGuid searchObject = searchCondition.getObjectGuid();
			if (searchObject != null && !StringUtils.isNullString(searchObject.getClassName()))
			{
				this.stubService.getBaseConfigStub().putAllClassFieldToSearch(searchObject.getClassName(), searchCondition);
			}
			objectList = boas.getFoundationStub().listObject(searchCondition, false);
		}
		return objectList;
	}

	public void remindUpdateSchedule(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject project = this.stubService.getBOAS().getObject(projectObjectGuid);
		List<FoundationObject> allTaskList = this.stubService.getWBSStub().listAllSubTask(projectObjectGuid, true);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(project);
		if (util.getProjectStatusEnum() == ProjectStatusEnum.INI)
		{
			throw new ServiceRequestException("ID_PM_PROJECT_NOT_START", "project has not start");
		}

		if (!this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
		{
			throw new ServiceRequestException("ID_PM_PROJECT_REMIND_SCHEDULE_MANAGER", "not project manager");
		}

		LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
		String msrString = this.stubService.getMSRM().getMSRString("ID_PM_REMIND_UPDATE_SCHEDULE_PROJECTEXECUTOR", languageEnum.toString());
		String contentProject = MessageFormat.format(msrString, util.getExecutorName());
		if (!SetUtils.isNullList(allTaskList))
		{

			Map<String, List<FoundationObject>> userTaskMap = new HashMap<String, List<FoundationObject>>();
			for (FoundationObject foundation : allTaskList)
			{
				if (project.getObjectGuid().getGuid().equalsIgnoreCase(foundation.getGuid()))
				{
					continue;
				}

				util.setFoundation(foundation);

				if (StringUtils.isNullString(util.getExecutor()))
				{
					continue;
				}

				if (util.getSPI() == null || util.getSPI() >= 1)
				{
					continue;
				}

				List<FoundationObject> taskList = userTaskMap.get(util.getExecutor());
				if (taskList == null)
				{
					taskList = new ArrayList<FoundationObject>();
					userTaskMap.put(util.getExecutor(), taskList);
				}
				taskList.add(foundation);
			}
			String subject = "";
			subject = this.stubService.getMSRM().getMSRString("ID_APP_PM_REMIND_UPDATE_SCHEDULE", languageEnum.toString());
			if (subject == null)
			{
				subject = "ID_APP_PM_REMIND_UPDATE_SCHEDULE";
			}

			String contentEexecutor = "";

			String userGuid = null;

			List<ObjectGuid> attachList = new ArrayList<ObjectGuid>();
			for (List<FoundationObject> taskList : userTaskMap.values())
			{
				String contentTaskList = "";
				if (taskList != null)
				{
					String taskExecutor = this.stubService.getMSRM().getMSRString("ID_PM_REMIND_UPDATE_SCHEDULE_EXECUTOR", languageEnum.toString());

					String taskContent = this.stubService.getMSRM().getMSRString("ID_PM_REMIND_UPDATE_SCHEDULE_TASK", languageEnum.toString());

					attachList.clear();
					for (FoundationObject foundation : taskList)
					{
						attachList.add(foundation.getObjectGuid());
						util.setFoundation(foundation);

						if (util.getTaskStatusEnum() != null
								&& ((util.getTaskStatusEnum() == TaskStatusEnum.INI && DateFormat.compareDate(util.getPlanStartTime(), DateFormat.getSysDate()) <= 0)
										|| (util.getTaskStatusEnum() == TaskStatusEnum.RUN && util.getSPI() < 1)))
						{
							contentEexecutor = MessageFormat.format(taskExecutor, util.getExecutorName());

							String taskStatus = this.stubService.getMSRM().getMSRString(util.getTaskStatusEnum().getMsrId(), languageEnum.toString());

							contentTaskList = contentTaskList + MessageFormat.format(taskContent, util.getWBSNumber()//
									, StringUtils.convertNULLtoString(foundation.getName())//
									, StringUtils.convertNULLtoString(DateFormat.formatYMD(util.getPlanStartTime()))//
									, StringUtils.convertNULLtoString(DateFormat.formatYMD(util.getPlanFinishTime()))//
									, util.getPlannedDuration() //
									, StringUtils.convertNULLtoString(taskStatus));
							contentTaskList = contentTaskList + "\r\n";
							userGuid = util.getExecutor();
						}
					}
				}
				String name = this.stubService.getMSRM().getMSRString("ID_CLIENT_PM_PROJECT", languageEnum.toString());
				this.stubService.getSMS().sendMailToUser(subject,
						contentEexecutor + "\n" + "[" + name + "] " + project.getFullName() + "\n" + contentTaskList + "\n" + contentProject, MailCategoryEnum.INFO, attachList,
						userGuid, MailMessageType.TASKNOTIFY);

			}

		}
	}

	public InstanceDomainUpdateBean calculateProjectInfo(FoundationObject project) throws ServiceRequestException
	{
		if (SystemStatusEnum.RELEASE != project.getStatus())
		{
			return null;
		}
		AbstractWBSDriver driver = this.stubService.getWBSStub().createWBSDriver(project, this.stubService.getOperatorGuid());
		FoundationObject rootObject = driver.getQueryHandler().getRootObject();
		driver.getCalculateHandler().updatePlanWorkload();
		Date current = new Date();
		Calendar xCalendar = this.getCalendarByDate(current);
		driver.getCalculateHandler().calculateProjectInfo(rootObject, rootObject, xCalendar.getTime(), null);
		driver.getCalculateHandler().calculateMilestone();
		driver.getCalculateHandler().updateOperateStatus();
		return this.stubService.getWBSStub().saveInstanceDomain(driver.getInstanceDomainUpdateBean());

	}

	/**
	 * 检查project中是否有可完成的摘要任务，如果可完成，就完成该摘要任务
	 *
	 * @param project
	 */
	public void checkSummery(FoundationObject project)
	{
		try
		{
			Map<String, List<FoundationObject>> alTaskMap = this.getAllSubTask(project.getObjectGuid(), true);
			List<FoundationObject> listChildSumTask = alTaskMap.get(project.getObjectGuid().getGuid());
			if (!SetUtils.isNullList(listChildSumTask))
			{
				for (FoundationObject sumTask : listChildSumTask)
				{
					this.doCompleteTask(sumTask, project, alTaskMap);
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 检查摘要任务是否可完成，可完成就自动完成该摘要任务
	 *
	 * @param sumTask
	 * @param project
	 * @param Map
	 */
	private void doCompleteTask(FoundationObject sumTask, FoundationObject project, Map<String, List<FoundationObject>> Map)
	{
		try
		{
			List<FoundationObject> listChildTask = Map.get(sumTask.getObjectGuid().getGuid());
			List<FoundationObject> listRunSumTask = new ArrayList<FoundationObject>();
			boolean isComplete = true;
			if (!SetUtils.isNullList(listChildTask))
			{
				for (FoundationObject obj : listChildTask)
				{
					PPMFoundationObjectUtil subUtil = new PPMFoundationObjectUtil(obj);
					if (subUtil.getTaskStatusEnum().equals(TaskStatusEnum.RUN))
					{
						isComplete = false;
						if (subUtil.getTaskTypeEnum().equals(TaskTypeEnum.SUMMARY))
						{
							listRunSumTask.add(obj);
						}
					}
				}
				if (isComplete)
				{
					// 完成该摘要任务
					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(sumTask);
					if (util.getTaskStatusEnum().equals(TaskStatusEnum.RUN))
					{
						this.stubService.getTaskStub().completeTask(sumTask, project);
					}
				}
				else if (!SetUtils.isNullList(listRunSumTask))
				{
					for (FoundationObject fou : listRunSumTask)
					{
						this.doCompleteTask(fou, project, Map);
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 得到parentObjectGuid下面所有的子任务，并且以父任务的guid作为key，子任务的list作为value保存在Map上面
	 *
	 * @param parentObjectGuid
	 * @param hasAllField
	 * @return
	 * @throws ServiceRequestException
	 */
	protected Map<String, List<FoundationObject>> getAllSubTask(ObjectGuid parentObjectGuid, boolean hasAllField) throws ServiceRequestException
	{
		List<FoundationObject> taskList = this.stubService.getWBSStub().listAllSubTask(parentObjectGuid, true);
		Map<String, List<FoundationObject>> Map = new HashMap<String, List<FoundationObject>>();
		if (!SetUtils.isNullList(taskList))
		{
			for (FoundationObject obj : taskList)
			{
				PPMFoundationObjectUtil subUtil = new PPMFoundationObjectUtil(obj);
				if (subUtil.getParentTask() != null && !StringUtils.isNullString(subUtil.getParentTask().getGuid()))
				{
					if (Map.get(subUtil.getParentTask().getGuid()) == null)
					{
						List<FoundationObject> tempList = new ArrayList<FoundationObject>();
						tempList.add(obj);
						Map.put(subUtil.getParentTask().getGuid(), tempList);
					}
					else
					{
						List<FoundationObject> tempList = Map.get(subUtil.getParentTask().getGuid());
						tempList.add(obj);
					}
				}
			}
		}
		return Map;
	}

	/**
	 * 通过流程修改项目和任务的状态
	 *
	 * @param projectObjectGuid
	 */
	public void changeProjectAndTaskStatusByWF(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		try
		{
			String sessionId = this.stubService.getSignature().getCredential();
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			InstanceService ds = DataServer.getInstanceService();
			if (projectObjectGuid != null)
			{
				FoundationObject projectFoun = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);
				if (projectFoun != null && projectFoun.getStatus() != SystemStatusEnum.RELEASE)
				{
					ds.changeStatus(projectFoun.getStatus(), SystemStatusEnum.RELEASE, projectFoun.getObjectGuid(), false, sessionId);
					List<FoundationObject> taskList = this.stubService.getWBSStub().listAllSubTask(projectObjectGuid, false);
					if (!SetUtils.isNullList(taskList))
					{
						for (FoundationObject taskFoun : taskList)
						{
							if (taskFoun != null && taskFoun.getStatus() != SystemStatusEnum.RELEASE)
							{
								ds.changeStatus(taskFoun.getStatus(), SystemStatusEnum.RELEASE, taskFoun.getObjectGuid(), false, sessionId);
							}
						}
					}
				}
			}
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
	}

	public FoundationObject effectProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.updateProjectTemplateAvailable(foundationObject, true);
	}

	public FoundationObject obsoleteProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.updateProjectTemplateAvailable(foundationObject, false);
	}

	/**
	 * 更新项目模板是否启用
	 *
	 * @param foundationObject
	 * @param isAvailable
	 * @return
	 * @throws ServiceRequestException
	 */
	private FoundationObject updateProjectTemplateAvailable(FoundationObject foundationObject, boolean isAvailable) throws ServiceRequestException
	{
		FoundationObject object = this.stubService.getBOAS().getObject(foundationObject.getObjectGuid());
		if (object != null)
		{
			// 1.判断是否有权限
			this.stubService.getPMAuthorityStub().hasPMAuthority(object, PMAuthorityEnum.PROJECT_WBS);
			object.put(PPMFoundationObjectUtil.AVAILABLE, BooleanUtils.getBooleanStringYN(isAvailable));
			return this.saveObject(object, true, false);
		}
		return null;
	}

	public void deleteProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException
	{
		FoundationObject object = this.stubService.getBOAS().getObject(foundationObject.getObjectGuid());
		if (object != null)
		{
			this.stubService.getPMAuthorityStub().hasPMAuthority(object, PMAuthorityEnum.PROJECT_WBS);
			this.stubService.getBOAS().deleteObject(object);
		}
	}
}

class DeleteScheduledTask extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;
	private ObjectGuid		objectGuid		= null;
	private PPMS			ppms			= null;

	public DeleteScheduledTask(ServiceContext serviceContext, ObjectGuid objectGuid)
	{
		this.serviceContext = serviceContext;
		this.objectGuid = objectGuid;
		this.setPriority(980);
	}

	@Override
	public void run()
	{
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]DeletetScheduledTask , Scheduled Task Start...");
		try
		{
			this.ppms = this.serviceContext.allocatService(PPMS.class);
			this.ppms.deleteAllTask(objectGuid);
			// 如果改项目被任务关联，需要清空任务的关联项目的值
			this.ppms.clearRelationProject(objectGuid);
		}
		catch (Throwable e)
		{
			DynaLogger.error("run delete all task:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.ppms != null)
			{
				this.serviceContext.releaseService(this.ppms);
			}
		}
		DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]DeletetScheduledTask , Scheduled Task End...");
	}
}