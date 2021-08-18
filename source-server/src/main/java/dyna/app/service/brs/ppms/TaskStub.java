/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理任务实例
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.*;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.*;

/**
 * @author WangLHB
 *         任务实例
 */
@Component
public class TaskStub extends AbstractServiceStub<PPMSImpl>
{

	protected List<FoundationObject> listTask(SearchCondition searchCondition) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		if (searchCondition != null)
		{
			ObjectGuid searchObject = searchCondition.getObjectGuid();
			if (searchObject != null && !StringUtils.isNullString(searchObject.getClassName()))
			{
				this.stubService.getBaseConfigStub().putAllClassFieldToSearch(searchObject.getClassName(), searchCondition);
			}
			return boas.getFoundationStub().listObject(searchCondition, false);
		}
		return null;
	}

	protected void deleteAllTask(ObjectGuid ownerProjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("PROJECTGUID", ownerProjectGuid.getGuid());
		paramMap.put("TASKBASETABLENAME", this.getTaskBaseTableName());
		sds.delete(FoundationObject.class, paramMap, "deleteAllProjectRole");
		sds.delete(FoundationObject.class, paramMap, "deleteAllTaskDI");
		sds.delete(FoundationObject.class, paramMap, "deleteAllTask_I");
		sds.delete(FoundationObject.class, paramMap, "deleteAllTask_M");
		sds.delete(FoundationObject.class, paramMap, "deleteAllTask");
	}

	protected void clearRelationProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("PROJECTGUID", projectObjectGuid.getGuid());
		paramMap.put("TASKBASETABLENAME", this.getTaskBaseTableName());
		sds.update(FoundationObject.class, paramMap, "clearRelationProject");
	}

	private String getTaskBaseTableName() throws ServiceRequestException
	{
		ClassInfo taskClass = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMTask, null);
		String tableName = DataServer.getDSCommonService().getTableName(taskClass.getName());
		return StringUtils.isNullString(tableName) ? null : tableName.subSequence(0, tableName.indexOf("_")).toString();
	}

	protected List<FoundationObject> listSubTask(ObjectGuid parentObjectGuid, boolean hasAllField) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		SearchCondition searchCondition = null;
		BOInfo boinfo = this.stubService.getPMTaskBoInfo((!this.stubService.getProjectStub().isProject(parentObjectGuid)));
		searchCondition = SearchConditionFactory.createSearchCondition4Class(boinfo.getClassName(), null, false);
		if (hasAllField)
		{
			List<ClassField> fieldList = this.stubService.getEMM().listFieldOfClass(boinfo.getClassName());
			for (ClassField filed : fieldList)
			{
				searchCondition.addResultField(filed.getName());
			}
		}
		searchCondition.addFilter(PPMFoundationObjectUtil.PARENTTASK, parentObjectGuid, OperateSignEnum.EQUALS);
		searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
		searchCondition.addOrder(SystemClassFieldEnum.ID, true);
		List<FoundationObject> list = boas.getFoundationStub().listObject(searchCondition, false);
		return list;
	}

	protected FoundationObject suspendTask(FoundationObject foundation) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);

		CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.SSP);
		util.setTaskStatus(codeItem.getGuid());

		FoundationObject project = this.stubService.getBOAS().getObject(util.getOwnerProject());
		if (project != null)
		{
			PPMFoundationObjectUtil projectUtil = new PPMFoundationObjectUtil(project);
			// 只能项目经理取消任务
			if (!this.stubService.getOperatorGuid().equalsIgnoreCase(projectUtil.getExecutor()))
			{
				throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
			}
		}

		if (util.getActualStartTime() != null)
		{
			util.setActualFinishTime(new Date());
			PPMFoundationObjectUtil utilProj = new PPMFoundationObjectUtil(project);
			String projectCalendar = utilProj.getProjectCalendar();
			PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
			if (workCalendar != null)
			{
				int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
				util.setActualDuration(actualDuration);
			}
		}

		this.stubService.getTaskStub().changeTaskSuspendProject(project, foundation, codeItem.getGuid());

		FoundationObject retFoundation = this.stubService.getProjectStub().saveObject(foundation, true, true);

		// 创建任务停止记录
		UpdateTaskStatus updateRecord = new UpdateTaskStatus();
		updateRecord.setTaskObjectGuid(retFoundation.getObjectGuid());
		updateRecord.setProgressRate(StringUtils.convertNULLtoString(util.getCompletionRate()));
		updateRecord.setStatus(ProgressRateEnum.CANCEL);
		this.saveUpdateTaskStatus(updateRecord, false);

		this.updateOperateStatus(project);
		if (!project.getObjectGuid().getGuid().equalsIgnoreCase(util.getParentTask().getGuid()))
		{
			boolean isCompleteParent = true;
			PPMFoundationObjectUtil subUtil = new PPMFoundationObjectUtil(foundation);
			List<FoundationObject> listSubTask = this.listSubTask(util.getParentTask(), true);
			if (!SetUtils.isNullList(listSubTask))
			{
				for (FoundationObject foundtaion : listSubTask)
				{
					subUtil.setFoundation(foundtaion);
					if (!(subUtil.getTaskStatusEnum() == TaskStatusEnum.COP || subUtil.getTaskStatusEnum() == TaskStatusEnum.SSP))
					{
						isCompleteParent = false;
					}
				}
			}

			if (isCompleteParent)
			{
				FoundationObject parentTask = this.stubService.getParentTask(foundation.getObjectGuid());
				this.completeTask(parentTask, project);
			}
		}

		util.setFoundation(foundation);
		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
		{
			this.stubService.getNoticeStub().sendMail(project, retFoundation, MessageTypeEnum.TASKENDNOTIFY);
		}
		return retFoundation;

	}

	protected FoundationObject startTask(FoundationObject task, FoundationObject project) throws ServiceRequestException
	{

		if (task.isCheckOut())
		{
			throw new ServiceRequestException("ID_APP_PM_STATUS_CHECK_IN", "please check in");
		}

		PPMFoundationObjectUtil projectUtil = new PPMFoundationObjectUtil(project);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);

		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();

		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY && StringUtils.isNullString(util.getExecutor()))
		{
			throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_IS_NULL", "task executor is null");
		}
		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
		{
			if (util.getTaskPerformerTypeEnum() == TaskPerformerType.Owner && !this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
			{
				throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
			}
			else if (util.getTaskPerformerTypeEnum() == TaskPerformerType.Superior)
			{
				if (!this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
				{
					if (!projectUtil.getExecutor().equalsIgnoreCase(this.stubService.getOperatorGuid()))
					{
						throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
					}
				}
			}
		}

		if (util.getTaskStatusEnum().equals(TaskStatusEnum.APP))
		{
			throw new ServiceRequestException("ID_TASK_STATUS_APP", "not operate");
		}

		if (projectUtil.getProjectStatusEnum() != ProjectStatusEnum.RUN)
		{
			// 项目没有运行，任务不可以启动
			throw new ServiceRequestException("ID_APP_PM_PROJECT_START_NOTRUN", "project task isnot running");
		}

		// 相关任务是否开始与结束
		List<TaskRelation> preTaskList = this.listPreTask(task.getObjectGuid());
		if (SetUtils.isNullList(preTaskList) == false)
		{
			for (TaskRelation struUtil : preTaskList)
			{
				PPMFoundationObjectUtil preTaskUtil = new PPMFoundationObjectUtil(null);
				if (TaskDependEnum.START_START == TaskDependEnum.getEnum(struUtil.getDependType()))
				{
					FoundationObject prefoundation = boas.getFoundationStub().getObject(struUtil.getPreTaskObjectGuid(), false);
					preTaskUtil.setFoundation(prefoundation);
					if (preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.INI)
					{
						throw new ServiceRequestException("ID_APP_PM_TASK_START_DEPEND_NOTRUN", "depend task isnot running", null, util.getWBSNumber() + "-"
								+ util.getFoundation().getName(), preTaskUtil.getWBSNumber() + "-" + preTaskUtil.getFoundation().getName());
					}
				}
				else if (TaskDependEnum.FINISH_START == TaskDependEnum.getEnum(struUtil.getDependType()))
				{
					FoundationObject prefoundation = boas.getFoundationStub().getObject(struUtil.getPreTaskObjectGuid(), false);
					preTaskUtil.setFoundation(prefoundation);
					if (preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.INI || preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.RUN
							|| preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.PUS || preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.APP)
					{
						throw new ServiceRequestException("ID_APP_PM_TASK_START_DEPEND_NOTFINISH", "depend task isnot finished", null, util.getWBSNumber() + "-"
								+ util.getFoundation().getName(), preTaskUtil.getWBSNumber() + "-" + preTaskUtil.getFoundation().getName());
					}
				}
			}
		}

		if (util.getTaskStatusEnum() == TaskStatusEnum.INI)
		{
			if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY && !StringUtils.isGuid(util.getExecutor()))
			{
				throw new ServiceRequestException("ID_APP_PM_START_PROJECT_TASK_EXECUTOR", "task executor is null", null, util.getFoundation().getId());
			}
		}

		CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.RUN);
		util.setTaskStatus(codeItem.getGuid());
		if (util.getActualStartTime() == null)
		{
			util.setActualStartTime(new Date());
		}
		FoundationObject retFoundation = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			this.stubService.getTaskStub().changeTaskStartProject(project, task, codeItem.getGuid(), false);

			retFoundation = this.stubService.getProjectStub().saveObject(task, false, true);

			// 创建任务启动记录
			UpdateTaskStatus updateRecord = new UpdateTaskStatus();
			updateRecord.setTaskObjectGuid(retFoundation.getObjectGuid());
			updateRecord.setProgressRate(StringUtils.convertNULLtoString(util.getCompletionRate()));
			updateRecord.setStatus(ProgressRateEnum.START);
			this.saveUpdateTaskStatus(updateRecord, false);

			if (task.getStatus() != SystemStatusEnum.RELEASE)
			{
				((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(retFoundation.getObjectGuid(), SystemStatusEnum.WIP, SystemStatusEnum.RELEASE, false, false);
			}

			FoundationObject parentTask = this.stubService.getParentTask(task.getObjectGuid());
			PPMFoundationObjectUtil parentUtil = new PPMFoundationObjectUtil(parentTask);
			if (parentUtil.getTaskStatusEnum() != TaskStatusEnum.RUN)
			{
				this.startTask(parentTask, project);
			}

			// 根据启动方式，执行任务,取得后置任务
			util.setFoundation(project);
			if (util.getTaskStartTypeEnum() == TaskStartType.FrontDrive)
			{
				List<TaskRelation> listTaskForDependRelationObject = this.listPostTask(task.getObjectGuid());
				if (!SetUtils.isNullList(listTaskForDependRelationObject))
				{
					for (TaskRelation relation : listTaskForDependRelationObject)
					{
						if (relation.getDependTypeEnum() == TaskDependEnum.START_START)
						{
							FoundationObject forPretask = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(relation.getTaskObjectGuid(), false);
							if (allHasStartedPreTask(project, forPretask, (String) task.get("WBSNumber")))
							{
								this.startTask(forPretask, project);
							}
						}
					}
				}
			}
			this.updateOperateStatus(project);
			retFoundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(task.getObjectGuid(), false);

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

		return retFoundation;
	}

	protected FoundationObject pauseTask(FoundationObject task, FoundationObject project) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		PPMFoundationObjectUtil projectUtil = new PPMFoundationObjectUtil(project);
		if (util.getTaskStatusEnum() != TaskStatusEnum.RUN)
		{
			throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_NOT_START", "project task isnot running");
		}

		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY && StringUtils.isNullString(util.getExecutor()))
		{
			throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_IS_NULL", "task executor is null");
		}
		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
		{
			if (!this.stubService.getOperatorGuid().equalsIgnoreCase(projectUtil.getExecutor()))
			{
				throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
			}
			else if (util.getTaskPerformerTypeEnum() == TaskPerformerType.Superior)
			{
				if (!this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
				{
					if (!projectUtil.getExecutor().equalsIgnoreCase(this.stubService.getOperatorGuid()))
					{
						throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
					}
				}
			}
		}

		CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.PUS);

		this.changeTaskPauseProject(project, task, codeItem.getGuid());

		util.setTaskStatus(codeItem.getGuid());

		FoundationObject retFoundation = this.stubService.getProjectStub().saveObject(task, true, true);

		// 创建任务暂停记录
		UpdateTaskStatus updateRecord = new UpdateTaskStatus();
		updateRecord.setTaskObjectGuid(retFoundation.getObjectGuid());
		updateRecord.setProgressRate(StringUtils.convertNULLtoString(util.getCompletionRate()));
		updateRecord.setStatus(ProgressRateEnum.PAUSE);
		this.saveUpdateTaskStatus(updateRecord, false);

		this.updateOperateStatus(project);

		util.setFoundation(task);
		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
		{
			this.stubService.getNoticeStub().sendMail(project, retFoundation, MessageTypeEnum.TASKPAUSENOTIFY);
		}
		return retFoundation;

	}

	protected FoundationObject completeTask(FoundationObject task, FoundationObject project) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		PPMFoundationObjectUtil projectUtil = new PPMFoundationObjectUtil(project);
		// 如果是处于审批状态，不处理直接进入 改状态 并开启后面相关的任务
		// 如果不是审批，那就是启动状态，进行完成的判断
		if (util.getTaskStatusEnum().equals(TaskStatusEnum.APP))
		{
			throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_APP", "project task is app");
		}
		else
		{
			if (util.getTaskStatusEnum() != TaskStatusEnum.RUN)
			{
				throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_IS_NOT_START", "project task isnot running");
			}
			if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY && StringUtils.isNullString(util.getExecutor()))
			{
				throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_IS_NULL", "task executor is null");
			}
			if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
			{
				if (util.getTaskPerformerTypeEnum() == TaskPerformerType.Superior && !this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
				{
					throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
				}
				else if (util.getTaskPerformerTypeEnum() == TaskPerformerType.Superior)
				{
					if (!this.stubService.getOperatorGuid().equalsIgnoreCase(util.getExecutor()))
					{
						if (!projectUtil.getExecutor().equalsIgnoreCase(this.stubService.getOperatorGuid()))
						{
							throw new ServiceRequestException("ID_APP_PM_TASK_EXECUTOR_OPERATE", "not operate");
						}
					}
				}
			}

			// 检查任务能否完成
			if (util.getFoundation() != null)
			{
				this.checkTaskCanComplete(util);
			}
		}
		// 如果是需要审批 并且状态不是审批，则改变状态，发邮件
		// 如果是审批 改变状态
		if (util.isNeedApprove() && !(util.getTaskStatusEnum().equals(TaskStatusEnum.APP)) && util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
		{
			CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.APP);
			util.setTaskStatus(codeItem.getGuid());
			LaborHourConfig workTimeConfig = this.stubService.getBaseConfigStub().getWorkTimeConfig();
			Double planWorkload = util.getPlanWorkload() == null ? util.getPlannedDuration() * workTimeConfig.getStandardhour() : util.getPlanWorkload();
			util.setActualWorkload(planWorkload);
			util.setActualFinishTime(new Date());
			String projectCalendar = projectUtil.getProjectCalendar();
			PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
			if (workCalendar != null)
			{
				int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
				util.setActualDuration(actualDuration);
			}
			util.setCompletionRate(100d);
			util.setOperationState(null);

			FoundationObject retFoundation = this.stubService.getProjectStub().saveObject(task, true, true);

			// 创建任务完成记录
			UpdateTaskStatus updateRecord = new UpdateTaskStatus();
			updateRecord.setTaskObjectGuid(retFoundation.getObjectGuid());
			updateRecord.setProgressRate(StringUtils.convertNULLtoString(util.getCompletionRate()));
			updateRecord.setStatus(ProgressRateEnum.FINISH);
			this.saveUpdateTaskStatus(updateRecord, false);

			String proGuid = (String) task.get("OWNERPROJECT");
			String proClassGuid = (String) task.get("OWNERPROJECT$CLASS");
			FoundationObject fou = null;
			if (!StringUtils.isNullString(proGuid))
			{
				fou = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(proClassGuid, null, proGuid, null));
			}
			this.stubService.getNoticeStub().sendMail(fou, task, MessageTypeEnum.TASKAPPNOTIFY);
			return retFoundation;
		}
		else
		{
			CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.COP);
			util.setTaskStatus(codeItem.getGuid());
			LaborHourConfig workTimeConfig = this.stubService.getBaseConfigStub().getWorkTimeConfig();
			Double planWorkload = util.getPlanWorkload() == null ? util.getPlannedDuration() * workTimeConfig.getStandardhour() : util.getPlanWorkload();
			util.setActualWorkload(planWorkload);
			if (!util.getTaskStatusEnum().equals(TaskStatusEnum.APP))
			{
				util.setActualFinishTime(new Date());
			}
			String projectCalendar = projectUtil.getProjectCalendar();
			PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
			if (workCalendar != null)
			{
				int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
				util.setActualDuration(actualDuration);
			}
			util.setCompletionRate(100d);

			FoundationObject retFoundation = this.stubService.getProjectStub().saveObject(task, true, true);

			// 创建任务完成记录
			UpdateTaskStatus updateRecord = new UpdateTaskStatus();
			updateRecord.setTaskObjectGuid(retFoundation.getObjectGuid());
			updateRecord.setProgressRate(StringUtils.convertNULLtoString(util.getCompletionRate()));
			updateRecord.setStatus(ProgressRateEnum.FINISH);
			this.saveUpdateTaskStatus(updateRecord, false);

			if (!project.getObjectGuid().getGuid().equalsIgnoreCase(util.getParentTask().getGuid()))
			{
				List<FoundationObject> listSubTask = this.stubService.listSubTask(util.getParentTask());
				if (!SetUtils.isNullList(listSubTask))
				{
					boolean isAllComplete = true;
					for (FoundationObject foundation : listSubTask)
					{
						util.setFoundation(foundation);

						if (!(util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP))
						{
							isAllComplete = false;
							break;
						}
					}
					if (isAllComplete)
					{
						FoundationObject parentTask = this.stubService.getParentTask(task.getObjectGuid());
						this.completeTask(parentTask, project);
					}
				}
			}

			// 根据启动方式，执行任务
			util.setFoundation(project);
			List<TaskRelation> listPostTask = this.listPostTask(task.getObjectGuid());
			if (util.getTaskStartTypeEnum() == TaskStartType.FrontDrive)
			{
				if (!SetUtils.isNullList(listPostTask))
				{
					for (TaskRelation relation : listPostTask)
					{
						if (relation.getDependTypeEnum() == TaskDependEnum.FINISH_START)
						{
							FoundationObject forPretask = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(relation.getTaskObjectGuid(), false);
							if (!this.haveOtherNotFinishedPreTask(project, forPretask, (String) task.get("WBSNumber")))
							{
								this.startTask(forPretask, project);
							}
						}
					}
				}
			}

			this.updateOperateStatus(project);
			util.setFoundation(task);
			if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
			{
				this.stubService.getNoticeStub().sendMail(project, retFoundation, MessageTypeEnum.TASKCOMPLETENOTIFY);
			}

			if (!SetUtils.isNullList(listPostTask))
			{
				for (TaskRelation relation : listPostTask)
				{
					FoundationObject forPretask = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(relation.getTaskObjectGuid(), false);

					this.stubService.getNoticeStub().sendMail(project, forPretask, MessageTypeEnum.PRETASKCOMPLETENOTIFY, task);
				}
			}

			// 项目可完成通知
			if (project.getObjectGuid().getGuid().equalsIgnoreCase(util.getParentTask().getGuid()))
			{
				List<FoundationObject> listSubTask = this.stubService.listSubTask(util.getParentTask());
				if (!SetUtils.isNullList(listSubTask))
				{
					boolean isAllComplete = true;
					for (FoundationObject foundation : listSubTask)
					{
						util.setFoundation(foundation);

						if (!(util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP))
						{
							isAllComplete = false;
							break;
						}
					}
					if (isAllComplete)
					{
						this.stubService.getNoticeStub().sendMail(project, null, MessageTypeEnum.PROJECTCANCOMPLETENOTIFY);

					}
				}
			}

			return retFoundation;
		}
	}

	private boolean haveOtherNotFinishedPreTask(FoundationObject project, FoundationObject task, String excludeWbsNumber) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		String predecessorRelation = util.getPredecessorRelation();
		if (StringUtils.isNullString(predecessorRelation))
		{
			return false;
		}
		String[] relations = predecessorRelation.split(",");
		for (String relation : relations)
		{
			if (excludeWbsNumber.equals(relation))
			{
				continue;
			}
			FoundationObject task_ = this.stubService.getTaskFoundationObjectByWBSNumber(project.getObjectGuid(), relation);
			if (task_ != null)
			{
				util.setFoundation(task_);
				if (util.getProjectStatusEnum() != ProjectStatusEnum.COP && util.getProjectStatusEnum() != ProjectStatusEnum.SSP)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断任务能否完成
	 * 
	 * @param util
	 * @throws ServiceRequestException
	 */
	protected void checkTaskCanComplete(PPMFoundationObjectUtil util) throws ServiceRequestException
	{
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();
		// 检查子任务状态
		List<FoundationObject> list = this.stubService.listSubTask(util.getFoundation().getObjectGuid());
		if (!SetUtils.isNullList(list))
		{
			PPMFoundationObjectUtil utilEnd2 = new PPMFoundationObjectUtil(util.getFoundation());
			for (FoundationObject end2 : list)
			{
				utilEnd2.setFoundation(end2);

				if (!(utilEnd2.getTaskStatusEnum().equals(TaskStatusEnum.COP) || utilEnd2.getTaskStatusEnum().equals(TaskStatusEnum.SSP)))
				{
					// 第一层任务存在除完成，废弃，中断外的状态，任务不可以被完成
					throw new ServiceRequestException("ID_APP_PM_TASK_COMPLETE_TASKSTATUS_OTHERS", "task not be Complete");
				}
			}
		}
		// 相关任务是否开始与结束,摘要任务不受前置任务控制
		if (SetUtils.isNullList(list))
		{
			List<TaskRelation> preTaskList = this.listPreTask(util.getFoundation().getObjectGuid());
			if (SetUtils.isNullList(preTaskList) == false)
			{
				for (TaskRelation struUtil : preTaskList)
				{
					PPMFoundationObjectUtil preTaskUtil = new PPMFoundationObjectUtil(null);
					if (TaskDependEnum.START_FINISH.equals(struUtil.getDependTypeEnum()))
					{
						FoundationObject prefoundation = boas.getFoundationStub().getObject(struUtil.getPreTaskObjectGuid(), false);
						preTaskUtil.setFoundation(prefoundation);
						if (preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.INI || util.getTaskStatusEnum() == TaskStatusEnum.SSP)
						{
							throw new ServiceRequestException("ID_APP_PM_TASK_START_DEPEND_NOTRUN", "depend task isnot running", null, util.getWBSNumber() + "-"
									+ util.getFoundation().getName(), preTaskUtil.getWBSNumber() + "-" + preTaskUtil.getFoundation().getName());
						}
					}
					else if (TaskDependEnum.FINISH_FINISH.equals(struUtil.getDependTypeEnum()))
					{
						FoundationObject prefoundation = boas.getFoundationStub().getObject(struUtil.getPreTaskObjectGuid(), false);
						preTaskUtil.setFoundation(prefoundation);
						if (preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.INI || preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.SSP
								|| preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.RUN || preTaskUtil.getTaskStatusEnum() == TaskStatusEnum.PUS)
						{
							throw new ServiceRequestException("ID_APP_PM_TASK_START_DEPEND_NOTFINISH", "depend task isnot finished", null, util.getWBSNumber() + "-"
									+ util.getFoundation().getName(), preTaskUtil.getWBSNumber() + "-" + preTaskUtil.getFoundation().getName());
						}
					}
				}
			}
		}
		// 检查交付物
		List<DeliverableItem> deliveryItemList = this.stubService.listDeliveryItem(util.getFoundation().getGuid());
		if (!SetUtils.isNullList(deliveryItemList))
		{
			for (DeliverableItem deliveryObj : deliveryItemList)
			{
				// 判断是否需要与是否要发布
				if (deliveryObj.isNeed() || deliveryObj.isRelease())
				{
					List<Deliverable> deliveryList = this.stubService.listDeliveryByItem(deliveryObj.getGuid());
					if (deliveryObj.isNeed())
					{
						if (SetUtils.isNullList(deliveryList))
						{
							throw new ServiceRequestException("ID_APP_PM_TASK_FINISH_DELIVERY_MONDATORY", "task need delivery Object");
						}
					}

					if (deliveryObj.isRelease())
					{
						if (!SetUtils.isNullList(deliveryList))
						{
							for (Deliverable deliverable : deliveryList)
							{
								FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(deliverable.getInstanceObjectGuid(), false);
								if (object.getStatus() != SystemStatusEnum.RELEASE)
								{
									throw new ServiceRequestException("ID_APP_PM_TASK_FINISH_DELIVERY_RELEASE", "delivery is not release");
								}

							}
						}
					}
				}
				// 判断分类是否正确
				if (!StringUtils.isNullString(deliveryObj.getClassification()))
				{
					List<Deliverable> deliveryList = this.stubService.listDeliveryByItem(deliveryObj.getGuid());
					if (!SetUtils.isNullList(deliveryList))
					{
						List<CodeItemInfo> codeItemList = this.stubService.getEMM().listAllSubCodeItemInfoByDatailContain(deliveryObj.getClassification());
						List<String> codeItemGuidList = new ArrayList<String>();
						if (!SetUtils.isNullList(codeItemList))
						{
							for (CodeItemInfo info : codeItemList)
							{
								codeItemGuidList.add(info.getGuid());
							}
						}
						for (Deliverable obj : deliveryList)
						{
							FoundationObject objectByGuid = this.stubService.getBOAS().getObjectByGuid(
									new ObjectGuid(obj.getInstanceClassGuid(), null, obj.getInstanceGuid(), null));
							if (SetUtils.isNullList(codeItemList))
							{
								if (objectByGuid != null && !deliveryObj.getClassification().equals(objectByGuid.getClassificationGuid()))
								{
									throw new ServiceRequestException("ID_APP_PM_TASK_FINISH_DELIVERY_CLASSIFICATION", "delivery classification ");
								}
							}
							else
							{
								if (objectByGuid != null && !codeItemGuidList.contains(objectByGuid.getClassificationGuid()))
								{
									throw new ServiceRequestException("ID_APP_PM_TASK_FINISH_DELIVERY_CLASSIFICATION", "delivery classification ");
								}
							}
						}
					}
				}

			}

		}
		// 检查子项目是否完成
		if (util.getRelationProject() != null && StringUtils.isGuid(util.getRelationProject().getGuid()))
		{
			FoundationObject childProject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(util.getRelationProject(), false);
			if (childProject != null)
			{
				PPMFoundationObjectUtil childUtil = new PPMFoundationObjectUtil(childProject);
				if (childUtil.getTaskStatusEnum() != TaskStatusEnum.COP && childUtil.getTaskStatusEnum() != TaskStatusEnum.SSP)
				{
					throw new ServiceRequestException("ID_CLIENT_PM_CT_PROJECT_RELATION_NOT_COMPLETE", "relation project " + childProject.getId() + " is not complete ", null,
							childProject.getId());
				}
			}
		}
	}

	protected void deleteTask(FoundationObject taskFoundationObject) throws ServiceRequestException
	{
		// 删除对象，删除关系 ,删除相同模板名下的end2(模板中设置成级联删除)
		// 并关联删除项目角色，角色与用户关系（数据库中关联删除）
		List<FoundationObject> childList = this.listSubTask(taskFoundationObject.getObjectGuid(), false);
		if (!SetUtils.isNullList(childList))
		{
			for (int i = childList.size() - 1; i >= 0; i--)
			{
				this.deleteTask(childList.get(i));
			}
		}
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(taskFoundationObject);

		if (util.getActualStartTime() != null)
		{
			throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_IS_STATED", "Task is Started");
		}
		((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteObject(taskFoundationObject, false);

		List<TaskRelation> postTaskList = this.listPostTask(taskFoundationObject.getObjectGuid());
		if (!SetUtils.isNullList(postTaskList))
		{
			for (TaskRelation relation : postTaskList)
			{
				if (relation != null)
				{
					this.deleteTaskRelation(relation.getGuid());
				}
			}
		}
		List<TaskRelation> preTaskList = this.listPreTask(taskFoundationObject.getObjectGuid());
		if (!SetUtils.isNullList(preTaskList))
		{
			for (TaskRelation relation : preTaskList)
			{
				if (relation != null)
				{
					this.deleteTaskRelation(relation.getGuid());
				}
			}
		}
	}

	/**
	 * 取得任务的前置任务
	 * 
	 * @param taskObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TaskRelation> listPreTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(TaskRelation.TASKGUID, taskObjectGuid.getGuid());
			return sds.query(TaskRelation.class, searchConditionMap);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<TaskRelation> listPostTask(ObjectGuid preTaskObjectGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(TaskRelation.PRETASKGUID, preTaskObjectGuid.getGuid());
			return sds.query(TaskRelation.class, searchConditionMap);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected void deleteTaskRelation(String relationGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, relationGuid);
			sds.delete(TaskRelation.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 变更所有任务的状态
	 * 变更为暂停状态
	 * 
	 * @param foundation
	 * @param pauseStatusGuid
	 *            暂停状态codeguid
	 * @throws ServiceRequestException
	 */
	protected void changeTaskPauseProject(FoundationObject prjFoundation, FoundationObject foundation, String pauseStatusGuid) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);
		List<FoundationObject> list = this.stubService.listSubTask(foundation.getObjectGuid());
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject end2 : list)
			{

				util.setFoundation(end2);
				if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP)
				{
					// 只有运行状态可以暂停
					continue;
				}

				if (util.getTaskStatusEnum() == TaskStatusEnum.PUS)
				{
				}
				else
				{
					util.setOriginalStatus(util.getTaskStatusEnum());
					this.changeTaskPauseProject(prjFoundation, end2, pauseStatusGuid);
				}

				util.setTaskStatus(pauseStatusGuid);

				this.stubService.getProjectStub().saveObject(end2, false, true);

				this.stubService.getNoticeStub().sendMail(prjFoundation, end2, MessageTypeEnum.TASKPAUSENOTIFY);
			}
		}
	}

	/**
	 * 变更所有任务的状态
	 * 变更为立项或暂停前状态
	 * 
	 * @param foundation
	 *            * @param cctStatusGuid 立项状态codeguid
	 * @throws ServiceRequestException
	 */
	protected void changeTaskStartProject(FoundationObject prjFoundation, FoundationObject foundation, String cctStatusGuid, boolean isProject) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);
		List<FoundationObject> list = this.stubService.listSubTask(foundation.getObjectGuid());
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject end2 : list)
			{
				util.setFoundation(end2);
				if (util.getTaskStatusEnum() == TaskStatusEnum.SSP || util.getTaskStatusEnum() == TaskStatusEnum.RUN || util.getTaskStatusEnum() == TaskStatusEnum.COP)
				{
					// 中止，结束，废弃，运行中，状态不可以启动
					continue;
				}

				if (StringUtils.isNullString(util.getOriginalStatus())) // 没有备份状态
				{
					continue;
				}
				CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(util.getOriginalStatusEnum());
				util.setTaskStatus(codeItem.getGuid());
				if (util.getOriginalStatusEnum() == TaskStatusEnum.PUS)
				{
					util.setOriginalStatus(null);
					this.stubService.getProjectStub().saveObject(end2, false, true);
					// 项目启动时，要更新任务的系统状态为发布
					if (isProject && foundation.getStatus() != SystemStatusEnum.RELEASE)
					{
						String sessionId = this.stubService.getSignature().getCredential();
						DataServer.getInstanceService().changeStatus(SystemStatusEnum.WIP, SystemStatusEnum.RELEASE, end2.getObjectGuid(), false, sessionId);
					}
				}
				else
				{
					util.setOriginalStatus(null);
					this.stubService.getProjectStub().saveObject(end2, false, true);
					// 项目启动时，要更新任务的系统状态为发布
					if (isProject && foundation.getStatus() != SystemStatusEnum.RELEASE)
					{
						String sessionId = this.stubService.getSignature().getCredential();
						DataServer.getInstanceService().changeStatus(SystemStatusEnum.WIP, SystemStatusEnum.RELEASE, end2.getObjectGuid(), false, sessionId);
					}

					this.changeTaskStartProject(prjFoundation, end2, cctStatusGuid, isProject);
				}

				this.stubService.getNoticeStub().sendMail(prjFoundation, end2, MessageTypeEnum.TASKASSIGNNOTIFY);
			}
		}
	}

	/**
	 * 变更所有任务的状态
	 * 变更为中止状态
	 * 
	 * @param foundation
	 * @param sspStatusGuid
	 *            中止状态
	 * @throws ServiceRequestException
	 */
	protected void changeTaskSuspendProject(FoundationObject prjFoundation, FoundationObject foundation, String sspStatusGuid) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);
		List<FoundationObject> list = this.stubService.listSubTask(foundation.getObjectGuid());
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject end2 : list)
			{
				util.setFoundation(end2);
				if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP)

				{
					// 创建，立项,废弃，结束状态不可以中止
					continue;
				}

				util.setTaskStatus(sspStatusGuid);
				if (util.getActualStartTime() != null)
				{
					util.setActualFinishTime(new Date());
					PPMFoundationObjectUtil utilProj = new PPMFoundationObjectUtil(prjFoundation);
					String projectCalendar = utilProj.getProjectCalendar();
					PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
					if (workCalendar != null)
					{
						int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
						util.setActualDuration(actualDuration);
					}
				}

				this.changeTaskSuspendProject(prjFoundation, end2, sspStatusGuid);

				this.stubService.getProjectStub().saveObject(end2, false, true);

				this.stubService.getNoticeStub().sendMail(prjFoundation, end2, MessageTypeEnum.TASKENDNOTIFY);

			}
		}
	}

	protected BOInfo getPMTaskBoInfo(boolean isTaskTemplate) throws ServiceRequestException
	{
		List<BOInfo> boInfoList = null;
		if (isTaskTemplate)
		{
			boInfoList = this.stubService.getEMM().listBizObjectByInterface(ModelInterfaceEnum.IPMTaskTemplate);
		}
		else
		{
			boInfoList = this.stubService.getEMM().listBizObjectByInterface(ModelInterfaceEnum.IPMTask);
		}

		if (!SetUtils.isNullList(boInfoList))
		{
			return boInfoList.get(0);
		}
		else
		{
			throw new ServiceRequestException("ID_APP_PM_TASK_BOINFO_NOT_FOUND", "not found task boinfo");
		}
	}

	/**
	 * 通过任务Guid获取任务的更新状态
	 * 
	 * @param TaskGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<UpdateTaskStatus> listUpdateTaskStatus(String taskGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(UpdateTaskStatus.TASKGUID, taskGuid);
		try
		{
			List<UpdateTaskStatus> taskUpdateList = sds.query(UpdateTaskStatus.class, filter);
			if (!SetUtils.isNullList(taskUpdateList))
			{
				for (UpdateTaskStatus taskStatus : taskUpdateList)
				{
					taskStatus.setForwardUserList(this.listForwardUser(taskStatus.getGuid()));
					if (!StringUtils.isNullString(taskStatus.getUpdateUserGuid()))
					{
						User user = this.stubService.getAAS().getUser(taskStatus.getUpdateUserGuid());
						if (user != null)
						{
							taskStatus.put(UpdateTaskStatus.UPDATE_USER_NAME, user.getUserName());
						}
					}
					if (!StringUtils.isNullString(taskStatus.getCreateUserGuid()))
					{
						User user = this.stubService.getAAS().getUser(taskStatus.getCreateUserGuid());
						if (user != null)
						{
							taskStatus.put(UpdateTaskStatus.CREATE_USER_NAME, user.getUserName());
						}
					}
				}
			}
			return taskUpdateList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected UpdateTaskStatus saveUpdateTaskStatus(UpdateTaskStatus taskStatus, boolean checkSuperiorACL) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();

		FoundationObject object = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskStatus.getTaskObjectGuid(), false);
		if (object == null)
		{
			return null;
		}

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(object);
		if (!operatorGuid.equals(util.getExecutor()) && checkSuperiorACL)
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(object.getObjectGuid().getClassGuid());
			FoundationObject project = null;
			if (classInfo.hasInterface(ModelInterfaceEnum.IPMWorkItem))
			{
				project = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(util.getRelationProject(), false);
			}
			else if (classInfo.hasInterface(ModelInterfaceEnum.IPMTask))
			{
				project = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(util.getOwnerProject(), false);
			}
			util.setFoundation(project);

			if (project != null && !(util.getTaskPerformerTypeEnum() == TaskPerformerType.Superior && operatorGuid.equals(util.getExecutor())))
			{
				throw new ServiceRequestException("ID_PM_NOT_UPDATE_TASK_STATUS", "not update task status");
			}
		}

		taskStatus.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		taskStatus.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			taskStatus.setGuid("");
			String guid = sds.save(taskStatus);
			if (StringUtils.isGuid(guid))
			{
				taskStatus.setGuid(guid);
			}
			// 取得更新进度,重新保存到任务中去
			this.updateTaskCompletionRate(taskStatus.getTaskObjectGuid(), taskStatus.getProgressRate());

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
		FoundationObject foun = this.stubService.getBOAS().getObjectByGuid(taskStatus.getTaskObjectGuid());
		if (foun != null)
		{
			if (foun.getObjectGuid().getClassGuid() != null)
			{
				ClassInfo classinfo = this.stubService.getEMM().getClassByGuid(foun.getObjectGuid().getClassGuid());
				if (classinfo != null)
				{
					object.put("COMPLETIONRATE", taskStatus.getProgressRate());
					object.put("UPDATECONTENT", taskStatus.getUpdateContent());
					FoundationObject fou = null;
					if (classinfo.hasInterface(ModelInterfaceEnum.IPMWorkItem))
					{
						String proGuid = (String) object.get("RelationProject");
						String proClassGuid = (String) object.get("RelationProject$Class");
						if (!StringUtils.isNullString(proGuid))
						{
							fou = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(proClassGuid, null, proGuid, null));
						}
						this.stubService.getNoticeStub().sendMail(fou, object, MessageTypeEnum.WORKITEMUPDATENOTIFY);
					}
					else if (classinfo.hasInterface(ModelInterfaceEnum.IPMTask))
					{
						String proGuid = (String) object.get("OWNERPROJECT");
						String proClassGuid = (String) object.get("OWNERPROJECT$CLASS");
						if (!StringUtils.isNullString(proGuid))
						{
							fou = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(proClassGuid, null, proGuid, null));
						}
						this.stubService.getNoticeStub().sendMail(fou, object, MessageTypeEnum.TASKUPDATENOTIFY);
					}
				}
			}
		}
		return this.getUpdateTaskStatus(taskStatus.getGuid());

	}

	/**
	 * 取得更新进度,重新保存到任务中去
	 * 
	 * @param taskObjectGuid
	 * @param progressRate
	 * @throws ServiceRequestException
	 */
	private void updateTaskCompletionRate(ObjectGuid taskObjectGuid, String progressRate) throws ServiceRequestException
	{
		if (taskObjectGuid != null)
		{
			FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(foundation);
			if (foundation != null)
			{
				LaborHourConfig workTimeConfig = this.stubService.getBaseConfigStub().getWorkTimeConfig();
				Double planWorkload = util.getPlanWorkload() == null ? util.getPlannedDuration() * workTimeConfig.getStandardhour() : util.getPlanWorkload();

				NumberFormat formater = NumberFormat.getInstance();
				formater.setGroupingUsed(false);
				formater.setMaximumFractionDigits(2);

				util.setCompletionRate(Double.parseDouble(formater.format(Double.parseDouble(progressRate))));
				util.setActualWorkload(planWorkload * Float.parseFloat(progressRate) / 100);
				PMCalendar workCalendar = null;
				if (!StringUtils.isNullString(util.getProjectCalendar()))
				{
					workCalendar = this.stubService.getCalendarStub().getWorkCalendar(util.getProjectCalendar());
				}
				else
				{
					workCalendar = this.stubService.getCalendarStub().getStandardWorkCalendar();
				}
				List<CodeItemInfo> onTimeStatusList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.ONTIMESTATE.getValue());
				util.setWbsPrepareContain(new WBSPrepareContain(null, null, null, null, null, onTimeStatusList));
				util.calculateSPIAndCPI(workCalendar, new Date(), workTimeConfig.getStandardhour(), true);

				this.stubService.getProjectStub().saveObject(util.getFoundation(), false, true);

			}
		}
	}

	/**
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	private UpdateTaskStatus getUpdateTaskStatus(String updateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, updateGuid);
		try
		{
			List<UpdateTaskStatus> updateTask = sds.query(UpdateTaskStatus.class, filter);
			if (!SetUtils.isNullList(updateTask))
			{
				return updateTask.get(0);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return null;
	}

	/**
	 * @param updateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> listForwardUser(String updateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("UPDATETASKSTATUSGUID", updateGuid);
		List<User> value = new ArrayList<User>();
		try
		{
			List<UpdateTaskStatus> forwardUserList = sds.query(UpdateTaskStatus.class, filter, "selectForwardUser");
			if (!SetUtils.isNullList(forwardUserList))
			{
				for (UpdateTaskStatus updateTaskStatus : forwardUserList)
				{
					if (updateTaskStatus != null)
					{
						User user = new User();
						if (updateTaskStatus.get("FORWARDUSER") != null && updateTaskStatus.get("FORWARDUSER") != "")
						{
							user.setGuid((String) updateTaskStatus.get("FORWARDUSER"));
						}
						if (updateTaskStatus.get("ID") != null && updateTaskStatus.get("ID") != "")
						{
							user.put("ID", updateTaskStatus.get("ID"));
						}
						if (updateTaskStatus.get("NAME") != null && updateTaskStatus.get("NAME") != "")
						{
							user.put("NAME", updateTaskStatus.get("NAME"));
						}
						value.add(user);
					}
				}
			}
			return value;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 通过更新guid取得所有相关备注，
	 * 
	 * @param updateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<UpdateRemark> listRemarkByUpdateTaskStatusGuid(String updateGuid) throws ServiceRequestException
	{
		return this.stubService.getWorkItemStub().listRemarkByUpdateGuid(updateGuid);
	}

	protected UpdateRemark saveUpdateTaskStatusRemark(String updateTaskStatusGuid, UpdateRemark remark) throws ServiceRequestException
	{
		return this.stubService.getWorkItemStub().saveRemark(updateTaskStatusGuid, remark);
	}

	protected void deleteUpdateTaskStatusRemark(String remarkGuid) throws ServiceRequestException
	{
		this.stubService.getWorkItemStub().deleteRemark(remarkGuid);
	}

	public TaskRelation saveTaskRelation(TaskRelation taskRelation) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		taskRelation.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		if (!StringUtils.isGuid(taskRelation.getGuid()))
		{
			taskRelation.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}
		try
		{
			String guid = sds.save(taskRelation);
			if (StringUtils.isGuid(guid))
			{
				taskRelation.setGuid(guid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return taskRelation;
	}

	/**
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TaskRelation> listAllTaskRelation(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put(TaskRelation.PROJECT, projectObjectGuid.getGuid());
			return sds.query(TaskRelation.class, searchConditionMap);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void updateOperateStatus(FoundationObject project) throws ServiceRequestException
	{
		List<FoundationObject> allTaskList = this.stubService.getWBSStub().listAllSubTask(project.getObjectGuid(), true);
		Map<String, FoundationObject> foundationMap = new HashMap<String, FoundationObject>();

		if (!SetUtils.isNullList(allTaskList))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
			PPMFoundationObjectUtil preUtil = new PPMFoundationObjectUtil(null);
			for (FoundationObject foundation : allTaskList)
			{
				foundationMap.put(foundation.getObjectGuid().getGuid(), foundation);
			}
			for (FoundationObject foundation : allTaskList)
			{
				util.setFoundation(foundation);
				OperationStateEnum operation = null;
				List<TaskRelation> preTaskList = this.listPreTask(foundation.getObjectGuid());

				if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
				{
					util.setOperationState(null);
					util.setOperationStateEnum(null);
					continue;
				}

				if (!SetUtils.isNullList(preTaskList))
				{
					for (TaskRelation relation : preTaskList)
					{
						FoundationObject preObject = foundationMap.get(relation.getPreTaskObjectGuid().getGuid());
						preUtil.setFoundation(preObject);
						operation = util.calculateTaskOperationState(preUtil, relation.getDependTypeEnum());
						if (operation == OperationStateEnum.CANNOTCOMPLETE || operation == OperationStateEnum.CANNOTSTART)
						{
							break;
						}
					}
				}
				else
				{
					if (util.getTaskStatusEnum() == TaskStatusEnum.INI)
					{
						operation = OperationStateEnum.START;
					}
					else if (util.getTaskStatusEnum() == TaskStatusEnum.RUN)
					{
						operation = OperationStateEnum.COMPLETE;
					}
				}

				if (util.getOperationStateEnum() != operation)
				{
					util.setOperationStateEnum(operation);
					if (operation == null)
					{
						util.setOperationState(null);
					}
					else
					{
						CodeItemInfo operationStatusCodeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.OPERATIONSTATE.getValue(),
								util.getOperationStateEnum().getValue());
						if (operationStatusCodeItem != null)
						{
							util.setOperationState(operationStatusCodeItem.getGuid());
						}
					}
					this.stubService.saveObject(util.getFoundation(), false, false);
				}
			}
		}
	}

	protected FoundationObject completeTask(FoundationObject task, FoundationObject project, UpdateTaskStatus approve) throws ServiceRequestException
	{

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		if (!util.getTaskStatusEnum().equals(TaskStatusEnum.APP))
		{
			throw new ServiceRequestException("ID_APP_PPM_TASK_STATUS_NOT_APP", "project task is not app");
		}

		ProgressRateEnum status = approve.getStatus();
		if (status.equals(ProgressRateEnum.APPISCANCEL))
		{
			CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.RUN);
			util.setTaskStatus(codeItem.getGuid());

			util.setActualWorkload(null);
			if (util.getTaskStatusEnum().equals(TaskStatusEnum.APP))
			{
				util.setActualFinishTime(null);
			}
			util.setActualWorkload(null);
			util.setCompletionRate(null);
			util.setActualDuration(null);
			CodeItemInfo OperationState = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.COP);

			util.setOperationState(OperationState.getGuid());

			FoundationObject retFoundation = this.stubService.getProjectStub().saveObject(task, true, true);

			// 创建任务完成记录
			approve.setTaskObjectGuid(retFoundation.getObjectGuid());
			approve.setProgressRate(StringUtils.convertNULLtoString(util.getCompletionRate()));
			approve.setStatus(ProgressRateEnum.START);

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			// 取得更新进度,重新保存到任务中去
			if (!StringUtils.isNullString(approve.getProgressRate()))
			{
				this.updateTaskCompletionRate(approve.getTaskObjectGuid(), approve.getProgressRate());
			}

//			DataServer.getTransactionManager().commitTransaction();

			String proGuid = (String) task.get("OWNERPROJECT");
			String proClassGuid = (String) task.get("OWNERPROJECT$CLASS");
			FoundationObject fou = null;
			if (!StringUtils.isNullString(proGuid))
			{
				fou = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(proClassGuid, null, proGuid, null));
			}
			this.stubService.getNoticeStub().sendMail(fou, task, MessageTypeEnum.TASKCANCELNOTIFY);

			return retFoundation;
		}
		PPMFoundationObjectUtil projectUtil = new PPMFoundationObjectUtil(project);
		CodeItemInfo codeItem = this.stubService.getWBSStub().getCodeItemByEnum(TaskStatusEnum.COP);
		util.setTaskStatus(codeItem.getGuid());
		LaborHourConfig workTimeConfig = this.stubService.getBaseConfigStub().getWorkTimeConfig();
		Double planWorkload = util.getPlanWorkload() == null ? util.getPlannedDuration() * workTimeConfig.getStandardhour() : util.getPlanWorkload();
		util.setActualWorkload(planWorkload);
		if (!util.getTaskStatusEnum().equals(TaskStatusEnum.APP))
		{
			util.setActualFinishTime(new Date());
		}
		String projectCalendar = projectUtil.getProjectCalendar();
		PMCalendar workCalendar = this.stubService.getCalendarStub().getWorkCalendar(projectCalendar);
		if (workCalendar != null)
		{
			int actualDuration = workCalendar.getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
			util.setActualDuration(actualDuration);
		}
		util.setCompletionRate(100d);

		FoundationObject retFoundation = this.stubService.getProjectStub().saveObject(task, true, true);
		if (!project.getObjectGuid().getGuid().equalsIgnoreCase(util.getParentTask().getGuid()))
		{
			List<FoundationObject> listSubTask = this.stubService.listSubTask(util.getParentTask());
			if (!SetUtils.isNullList(listSubTask))
			{
				boolean isAllComplete = true;
				for (FoundationObject foundation : listSubTask)
				{
					util.setFoundation(foundation);

					if (!(util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP))
					{
						isAllComplete = false;
						break;
					}

				}
				if (isAllComplete)
				{
					FoundationObject parentTask = this.stubService.getParentTask(task.getObjectGuid());
					this.completeTask(parentTask, project);
				}
			}
		}

		// 根据启动方式，执行任务
		util.setFoundation(project);
		List<TaskRelation> listPostTask = this.listPostTask(task.getObjectGuid());
		if (util.getTaskStartTypeEnum() == TaskStartType.FrontDrive)
		{
			if (!SetUtils.isNullList(listPostTask))
			{
				for (TaskRelation relation : listPostTask)
				{
					if (relation.getDependTypeEnum() == TaskDependEnum.FINISH_START)
					{
						FoundationObject forPretask = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(relation.getTaskObjectGuid(), false);
						if (!this.haveOtherNotFinishedPreTask(project, forPretask, (String) task.get("WBSNumber")))
						{
							this.startTask(forPretask, project);
						}
					}

				}
			}
		}

		this.updateOperateStatus(project);
		util.setFoundation(task);
		if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
		{
			this.stubService.getNoticeStub().sendMail(project, retFoundation, MessageTypeEnum.TASKAPPCOLNOTIFY);
		}

		if (!SetUtils.isNullList(listPostTask))
		{
			for (TaskRelation relation : listPostTask)
			{
				FoundationObject forPretask = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(relation.getTaskObjectGuid(), false);

				this.stubService.getNoticeStub().sendMail(project, forPretask, MessageTypeEnum.PRETASKCOMPLETENOTIFY, task);
			}
		}

		// 项目可完成通知
		if (project.getObjectGuid().getGuid().equalsIgnoreCase(util.getParentTask().getGuid()))
		{
			List<FoundationObject> listSubTask = this.stubService.listSubTask(util.getParentTask());
			if (!SetUtils.isNullList(listSubTask))
			{
				boolean isAllComplete = true;
				for (FoundationObject foundation : listSubTask)
				{
					util.setFoundation(foundation);

					if (!(util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP))
					{
						isAllComplete = false;
						break;
					}

				}
				if (isAllComplete)
				{
					this.stubService.getNoticeStub().sendMail(project, null, MessageTypeEnum.PROJECTCANCOMPLETENOTIFY);

				}
			}
		}

		return retFoundation;
	}

	private boolean allHasStartedPreTask(FoundationObject project, FoundationObject task, String excludeWbsNumber) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		String predecessorRelation = util.getPredecessorRelation();
		if (StringUtils.isNullString(predecessorRelation))
		{
			return true;
		}
		String[] relations = predecessorRelation.split(",");
		for (String relation : relations)
		{
			if (excludeWbsNumber.equals(relation))
			{
				continue;
			}
			if (relation.endsWith("SS"))
			{
				relation = relation.substring(0, relation.length() - 2);
			}
			FoundationObject task_ = this.stubService.getTaskFoundationObjectByWBSNumber(project.getObjectGuid(), relation);
			if (task_ != null)
			{
				util.setFoundation(task_);
				if (util.getProjectStatusEnum() == ProjectStatusEnum.INI)
				{
					return false;
				}
			}
		}
		return true;
	}

}
