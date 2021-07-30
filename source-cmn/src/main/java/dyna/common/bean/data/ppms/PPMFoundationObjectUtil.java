/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PPMFoundationObjectUtil
 * wangweixia 2013-10-21
 */
package dyna.common.bean.data.ppms;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.calculation.EarnedValueManagement;
import dyna.common.bean.data.ppms.calculation.StatusCalculated;
import dyna.common.bean.data.ppms.check.TaskCheck;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.systemenum.ppms.*;
import dyna.common.util.BooleanUtils;
import dyna.common.util.PMConstans;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangweixia
 * 
 */
public class PPMFoundationObjectUtil
{

	private FoundationObject	foundationObject		= null;

	// 常量
	private static final double	DOUBLE_DEFAULT_VALUE	= 0;
	private static final int	INT_DEFAULT_VALUE		= 0;
	// 公共字段

	// 计划开始时间
	public static final String	PLANSTARTTIME			= "PLANSTARTTIME";

	// 计划结束时间
	public static final String	PLANFINISHTIME			= "PLANFINISHTIME";

	// 实际开始时间
	public static final String	ACTUALSTARTTIME			= "ACTUALSTARTTIME";

	// 实际结束时间
	public static final String	ACTUALFINISHTIME		= "ACTUALFINISHTIME";

	// 执行人
	public static final String	EXECUTOR				= "EXECUTOR";
	// 计划周期
	public static final String	PLANNEDDURATION			= "PLANNEDDURATION";

	// 实际周期
	public static final String	ACTUALDURATION			= "ACTUALDURATION";
	// 最早开始时间
	public static final String	EARLIESTSTARTTIME		= "EARLIESTSTARTTIME";

	// 最早结束时间
	public static final String	EARLIESTFINISHTIME		= "EARLIESTFINISHTIME";

	// 最晚开始时间
	public static final String	LATESTSTARTTIME			= "LATESTSTARTTIME";

	// 最晚结束时间
	public static final String	LATESTFINISHTIME		= "LATESTFINISHTIME";
	// 完成率
	public static final String	COMPLETIONRATE			= "COMPLETIONRATE";

	// 估计工作量
	public static final String	PLANWORKLOAD			= "PLANWORKLOAD";
	// 实际工作量
	public static final String	ACTUALWORKLOAD			= "ACTUALWORKLOAD";

	// 来自模板
	public static final String	FROMTEMPLATE			= "FROMTEMPLATE";

	// 重要级别
	public static final String	IMPORTANCELEVEL			= "IMPORTANCELEVEL";
	// 准时状态
	public static final String	ONTIMESTATE				= "ONTIMESTATE";
	// 执行状态
	public static final String	EXECUTESTATUS			= "EXECUTESTATUS";
	// 是否需审批
	public static final String	NEEDAPPROVE				= "ISNEEDAPPROVE";

	/**
	 * 项目管理字段
	 */
	// 类型
	public static final String	PROJECTTYPE				= "PROJECTTYPE";
	// 项目日历
	public static final String	PROJECTCALENDAR			= "PROJECTCALENDAR";

	// 项目信息计算时间点
	public static final String	PROJECTINFOUPDATETIME	= "PROJECTINFOUPDATETIME";

	// 项目计划审批
	public static final String	PLANAPPROVAL			= "PLANAPPROVAL";

	// 关联任务
	public static final String	RELATIONTASK			= "RELATIONTASK";

	// 任务开始类型
	public static final String	TASKSTARTTYPE			= "TASKSTARTTYPE";
	// 任务执行者类型
	public static final String	TASKPERFORMERTYPE		= "TASKPERFORMERTYPE";

	// 正常
	public static final String	NORMALINI				= "NORMALINI";
	public static final String	LATEINI					= "LATEINI";

	// 正常完成
	public static final String	NORMALCOP				= "NORMALCOP";
	// 延迟完成
	public static final String	LATECOP					= "LATECOP";
	// 正常执行
	public static final String	NORMALRUN				= "NORMALRUN";
	// 延迟执行
	public static final String	LATERUN					= "LATERUN";

	/**
	 * 任务字段
	 */

	// 任务
	public static final String	TASKTYPE				= "TASKTYPE";

	// 执行角色
	public static final String	EXECUTORROLE			= "EXECUTORROLE";

	// 原始状态
	public static final String	ORIGINALSTATUS			= "ORIGINALSTATUS";

	// WBS序号
	public static final String	WBSNUMBER				= "WBSNUMBER";

	public static final String	OWNERPROJECT			= "OWNERPROJECT";
	// 父任务
	public static final String	PARENTTASK				= "PARENTTASK";

	public static final String	SEQUENCEID				= "SEQUENCEID";

	public static final String	PREDECESSORRELATION		= "PREDECESSORRELATION";

	// 子项目
	public static final String	RELATIONPROJECT			= "RELATIONPROJECT";

	// 可操作状态
	public static final String	OPERATIONSTATE			= "OPERATIONSTATE";

	// 是否存在交付项
	public static final String	HASDELIVERYITEM			= "HASDELIVERYITEM";
	/**
	 * 项目模板字段
	 */
	// 模板说明
	public static final String	REMARK					= "REMARK";
	// 是否可用
	public static final String	AVAILABLE				= "AVAILABLE";

	// 计划开始点
	public static final String	PLANSTARTPOINT			= "PLANSTARTPOINT";

	// 计划结束点
	public static final String	PLANFINISHPOINT			= "PLANFINISHPOINT";

	public static final String	EARLIESTSTARTPOINT		= "EARLIESTSTARTPOINT";

	// 最晚开始点
	public static final String	LATESTSTARTPOINT		= "LATESTSTARTPOINT";

	// 最晚结束点
	public static final String	LATESTFINISHPOINT		= "LATESTFINISHPOINT";

	/**
	 * 项目变更
	 */
	// 项目变更对象
	public static final String	CHANGE_OBJECT			= "CHANGEOBJECT";

	// 项目名称
	public static final String	PROJECTNAME				= "PROJECTNAME";

	// 项目名称(变更后)
	public static final String	NEWPROJECTNAME			= "NEWPROJECTNAME";

	// 计划开始时间(变更后)
	public static final String	NEWPLANSTARTTIME		= "NEWPLANSTARTTIME";

	// 计划结束时间(变更后)
	public static final String	NEWPLANFINISHTIME		= "NEWPLANFINISHTIME";

	// 执行人(变更后)
	public static final String	NEWEXECUTOR				= "NEWEXECUTOR";

	// 计划周期(变更后)
	public static final String	NEWPLANNEDDURATION		= "NEWPLANNEDDURATION";

	// 类型(变更后)
	public static final String	NEWPROJECTTYPE			= "NEWPROJECTTYPE";

	// 项目日历(变更后)
	public static final String	NEWPROJECTCALENDAR		= "NEWPROJECTCALENDAR";

	// 估计工作量(变更后)
	public static final String	NEWPLANWORKLOAD			= "NEWPLANWORKLOAD";

	// 来自模板(变更后)
	public static final String	NEWFROMTEMPLATE			= "NEWFROMTEMPLATE";

	// 重要级别(变更后)
	public static final String	NEWIMPORTANCELEVEL		= "NEWIMPORTANCELEVEL";

	// 项目计划审批(变更后)
	public static final String	NEWPLANAPPROVAL			= "NEWPLANAPPROVAL";

	// 任务开始类型(变更后)
	public static final String	NEWTASKSTARTTYPE		= "NEWTASKSTARTTYPE";
	// 任务执行者类型(变更后)
	public static final String	NEWTASKPERFORMERTYPE	= "NEWTASKPERFORMERTYPE";

	public WBSPrepareContain	wbsPrepareContain		= null;
	/**
	 * 任务变更
	 * 
	 * @param foundation
	 */
	public static final String	FROMTASK				= "FROMTASK";

	public static final String	OPERATOR				= "OPERATOR";

	private static final String	PVFORPARENT				= "PVFORPARENT";

	public static final String	SPI						= "SPI";

	private static final String	AC						= "AC";

	private static final String	CPI						= "CPI";

	private static final String	FINISHEDCHECKPOINT		= "FINISHEDCHECKPOINT";

	private static final String	NEXTCHECKPOINT			= "NEXTCHECKPOINT";
	private StatusCalculated	statusCalculation		= null;
	private TaskCheck			taskCheck				= null;

	public PPMFoundationObjectUtil(FoundationObject foundation)
	{
		this.foundationObject = foundation;
		statusCalculation = new StatusCalculated();
		taskCheck = new TaskCheck();
	}

	public FoundationObject getFoundation()
	{
		return this.foundationObject;
	}

	public void setFoundation(FoundationObject foundation)
	{
		if (statusCalculation == null)
		{
			statusCalculation = new StatusCalculated();
		}
		if (taskCheck == null)
		{
			taskCheck = new TaskCheck();
		}
		this.foundationObject = foundation;
	}

	/**
	 * @return the planStartTime
	 */
	public Date getPlanStartTime()
	{
		return (Date) this.foundationObject.get(PLANSTARTTIME);
	}

	/**
	 * @param planStartTime
	 *            the planStartTime to set
	 */
	public void setPlanStartTime(Date planStartTime)
	{
		this.foundationObject.put(PLANSTARTTIME, planStartTime);
	}

	/**
	 * @return the planStartPoint
	 */
	public Integer getPlanStartPoint()
	{
		Number b = (Number) this.foundationObject.get(PLANSTARTPOINT);
		return b == null ? null : b.intValue();
	}

	/**
	 * @param planStartPoint
	 *            the planStartPoint to set
	 */
	public void setPlanStartPoint(Integer planStartPoint)
	{
		this.foundationObject.put(PLANSTARTPOINT, (planStartPoint == null ? null : BigDecimal.valueOf(planStartPoint)));

	}

	/**
	 * @return the LatestFinishPoint
	 */
	public Integer getLatestFinishPoint()
	{
		Number b = (Number) this.foundationObject.get(LATESTFINISHPOINT);
		return b == null ? null : b.intValue();
	}

	/**
	 * @param LatestFinishPoint
	 *            the LatestFinishPoint to set
	 */
	public void setLatestFinishPoint(Integer latestFinishPoint)
	{
		this.foundationObject.put(LATESTFINISHPOINT, (latestFinishPoint == null ? null : BigDecimal.valueOf(latestFinishPoint)));

	}

	/**
	 * @return the latestStartPoint
	 */
	public Integer getLatestStartPoint()
	{
		Number b = (Number) this.foundationObject.get(LATESTSTARTPOINT);
		return b == null ? null : b.intValue();
	}

	/**
	 * @param latestStartPoint
	 *            the latestStartPoint to set
	 */
	public void setLatestStartPoint(Integer latestStartPoint)
	{
		this.foundationObject.put(LATESTSTARTPOINT, (latestStartPoint == null ? null : BigDecimal.valueOf(latestStartPoint)));
	}

	/**
	 * @return the planStartPoint
	 */
	public Integer getPlanFinishPoint()
	{
		Number b = (Number) this.foundationObject.get(PLANFINISHPOINT);
		return b == null ? null : b.intValue();
	}

	/**
	 * @param planStartPoint
	 *            the planFinishPoint to set
	 */
	public void setPlanFinishPoint(Integer planFinishPoint)
	{
		this.foundationObject.put(PLANFINISHPOINT, (planFinishPoint == null ? null : BigDecimal.valueOf(planFinishPoint)));

	}

	/**
	 * @return the EarliestStartPoint
	 */
	public Integer getEarliestStartPoint()
	{
		Number b = (Number) this.foundationObject.get(EARLIESTSTARTPOINT);
		return b == null ? null : b.intValue();
	}

	/**
	 * @param EarliestStartPoint
	 *            the EarliestStartPoint to set
	 */
	public void setEarliestStartPoint(Integer earliestStartPoint)
	{
		this.foundationObject.put(EARLIESTSTARTPOINT, (earliestStartPoint == null ? null : BigDecimal.valueOf(earliestStartPoint)));
		this.foundationObject.put("ISMANUALPLAN", (earliestStartPoint == null ? null : "Y"));
	}

	/**
	 * @return the planFinishTime
	 */
	public Date getPlanFinishTime()
	{
		return (Date) this.foundationObject.get(PLANFINISHTIME);
	}

	/**
	 * @param planFinishTime
	 *            the planFinishTime to set
	 */
	public void setPlanFinishTime(Date planFinishTime)
	{
		this.foundationObject.put(PLANFINISHTIME, planFinishTime);
	}

	/**
	 * @return the actualStartTime
	 */
	public Date getActualStartTime()
	{
		return (Date) this.foundationObject.get(ACTUALSTARTTIME);
	}

	/**
	 * @param actualStartTime
	 *            the actualStartTime to set
	 */
	public void setActualStartTime(Date actualStartTime)
	{
		this.foundationObject.put(ACTUALSTARTTIME, actualStartTime);
	}

	/**
	 * @return the actualFinishTime
	 */
	public Date getActualFinishTime()
	{
		return (Date) this.foundationObject.get(ACTUALFINISHTIME);
	}

	/**
	 * @param actualFinishTime
	 *            the actualFinishTime to set
	 */
	public void setActualFinishTime(Date actualFinishTime)
	{
		this.foundationObject.put(ACTUALFINISHTIME, actualFinishTime);
	}

	/**
	 * @return the completionRate
	 */
	public Double getCompletionRate()
	{
		Number b = (Number) this.foundationObject.get(COMPLETIONRATE);
		return b == null ? DOUBLE_DEFAULT_VALUE : b.doubleValue();

	}

	/**
	 * @param completionRate
	 *            the completionRate to set
	 */
	public void setCompletionRate(Double completionRate)
	{
		this.foundationObject.put(COMPLETIONRATE, completionRate == null ? null : BigDecimal.valueOf(completionRate.intValue()));
	}

	/**
	 * @return the plannedDuration
	 */
	public Integer getPlannedDuration()
	{
		Object object = this.foundationObject.get(PLANNEDDURATION);
		if (object == null)
		{
			this.foundationObject.put(PLANNEDDURATION, BigDecimal.valueOf(1));
			return 1;
		}
		if (object instanceof BigDecimal)
		{
			return ((BigDecimal) object).intValue();
		}
		else
		{
			Integer value = 1;
			try
			{
				value = Integer.parseInt(object.toString());
			}
			catch (Exception e)
			{
			}
			this.foundationObject.put(PLANNEDDURATION, BigDecimal.valueOf(value));
			return value;
		}
	}

	/**
	 * @param plannedDuration
	 *            the plannedDuration to set
	 */
	public void setPlannedDuration(Integer plannedDuration)
	{
		if (plannedDuration != null)
		{
			if (plannedDuration.intValue() != (this.getPlannedDuration().intValue()))
			{
				this.foundationObject.put(PLANNEDDURATION, (plannedDuration == null ? null : BigDecimal.valueOf(plannedDuration)));
			}
		}
	}

	/**
	 * @return the actualDuration
	 */
	public Integer getActualDuration()
	{
		Number b = (Number) this.foundationObject.get(ACTUALDURATION);
		return b == null ? INT_DEFAULT_VALUE : b.intValue();
	}

	/**
	 * @param actualDuration
	 *            the actualDuration to set
	 */
	public void setActualDuration(Integer actualDuration)
	{
		this.foundationObject.put(ACTUALDURATION, (actualDuration == null ? null : BigDecimal.valueOf(actualDuration)));
	}

	/**
	 * @return the estimatedWorkload
	 */
	public Double getPlanWorkload()
	{
		Object o = this.foundationObject.get(PLANWORKLOAD);
		if (o instanceof BigDecimal)
		{
			BigDecimal b = (BigDecimal) this.foundationObject.get(PLANWORKLOAD);
			return b == null ? null : b.doubleValue();
		}
		else if (o instanceof Double)
		{
			Double b = (Double) this.foundationObject.get(PLANWORKLOAD);
			return b;
		}
		else
		{
			return null;
		}
	}

	/**
	 * @param estimatedWorkload
	 *            the estimatedWorkload to set
	 */
	public void setPlanWorkload(Double planWorkload)
	{
		this.foundationObject.put(PLANWORKLOAD, (planWorkload == null ? null : BigDecimal.valueOf(planWorkload)));
	}

	/**
	 * @return the actualWorkload
	 */
	public Double getActualWorkload()
	{
		Number b = (Number) this.foundationObject.get(ACTUALWORKLOAD);
		return b == null ? DOUBLE_DEFAULT_VALUE : b.doubleValue();
	}

	/**
	 * @param actualWorkload
	 *            the actualWorkload to set
	 */
	public void setActualWorkload(Double actualWorkload)
	{
		this.foundationObject.put(ACTUALWORKLOAD, (actualWorkload == null ? null : BigDecimal.valueOf(actualWorkload)));
	}

	public Date getEarliestStartTime()
	{
		return (Date) this.foundationObject.get(EARLIESTSTARTTIME);
	}

	public void setEarliestStartTime(Date planStartTime)
	{
		this.foundationObject.put(EARLIESTSTARTTIME, planStartTime);
		this.foundationObject.put("ISMANUALPLAN", (planStartTime == null ? null : "Y"));
	}

	public Date getEarliestFinishTime()
	{
		return (Date) this.foundationObject.get(EARLIESTFINISHTIME);
	}

	public void setEarliestFinishTime(Date planFinishTime)
	{
		this.foundationObject.put(EARLIESTFINISHTIME, planFinishTime);
	}

	public Date getLatestStartTime()
	{
		return (Date) this.foundationObject.get(LATESTSTARTTIME);
	}

	public void setLatestStartTime(Date latestStartTime)
	{
		this.foundationObject.put(LATESTSTARTTIME, latestStartTime);
	}

	public Date getLatestFinishTime()
	{
		return (Date) this.foundationObject.get(LATESTFINISHTIME);
	}

	public void setLatestFinishTime(Date latestFinishTime)
	{
		this.foundationObject.put(LATESTFINISHTIME, latestFinishTime);
	}

	public ProjectStatusEnum getProjectStatusEnum()
	{
		String str = (String) this.foundationObject.get(EXECUTESTATUS + PMConstans.NAME);
		if (str == null)
		{
			return null;
		}
		return ProjectStatusEnum.getStatusEnum(str);
	}

	public void setProjectStatus(ProjectStatusEnum projectStatus)
	{
		if (projectStatus == null)
		{
			this.foundationObject.put(EXECUTESTATUS + PMConstans.NAME, null);
		}
		else
		{
			this.foundationObject.put(EXECUTESTATUS + PMConstans.NAME, projectStatus.getValue());
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getProjectStatus()
	{
		return (String) this.foundationObject.get(EXECUTESTATUS);
	}

	/**
	 * 
	 * @param projectStatusGuid
	 */
	public void setProjectStatus(String projectStatusGuid)
	{
		this.foundationObject.put(EXECUTESTATUS, projectStatusGuid);
	}

	public OperationStateEnum getOperationStateEnum()
	{
		String str = (String) this.foundationObject.get(OPERATIONSTATE + PMConstans.NAME);
		if (str == null)
		{
			return null;
		}
		return OperationStateEnum.getValueOf(str);
	}

	public void setOperationStateEnum(OperationStateEnum operationState)
	{
		if (operationState == null)
		{
			this.foundationObject.put(OPERATIONSTATE + PMConstans.NAME, null);
		}
		else
		{
			this.foundationObject.put(OPERATIONSTATE + PMConstans.NAME, operationState.getValue());
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getOperationState()
	{
		return (String) this.foundationObject.get(OPERATIONSTATE);
	}

	/**
	 * 
	 * @param projectStatusGuid
	 */
	public void setOperationState(String operationState)
	{
		this.foundationObject.put(OPERATIONSTATE, operationState);
	}

	public TaskStatusEnum getTaskStatusEnum()
	{
		return TaskStatusEnum.getStatusEnum(this.foundationObject.get(EXECUTESTATUS + PMConstans.NAME));
	}

	/**
	 * @param taskStatus
	 *            the taskStatus to set
	 */
	public void setTaskStatusName(String value)
	{
		this.foundationObject.put(EXECUTESTATUS + PMConstans.NAME, value);
	}

	/**
	 * @return the taskStatus
	 */
	public String getTaskStatus()
	{
		return (String) this.foundationObject.get(EXECUTESTATUS);
	}

	/**
	 * @param taskStatus
	 *            the taskStatus to set
	 */
	public void setTaskStatus(String taskStatusGuid)
	{
		this.foundationObject.put(EXECUTESTATUS, taskStatusGuid);
	}

	/**
	 * @return the ownerProject
	 */
	public ObjectGuid getProjectType()
	{
		return new ObjectGuid((String) this.foundationObject.get(PROJECTTYPE + PPMConstans.CLASS), null, (String) this.foundationObject.get(PROJECTTYPE),
				(String) this.foundationObject.get(PROJECTTYPE + PPMConstans.MASTER), null);

	}

	/**
	 * @param ownerProject
	 *            the ownerProject to set
	 */
	public void setProjectType(ObjectGuid projectProject)
	{
		if (projectProject == null)
		{
			this.foundationObject.put(PROJECTTYPE + PPMConstans.MASTER, null);
			this.foundationObject.put(PROJECTTYPE + PPMConstans.CLASS, null);
			this.foundationObject.put(PROJECTTYPE, null);
		}
		else
		{
			this.foundationObject.put(PROJECTTYPE + PPMConstans.MASTER, projectProject.getMasterGuid());
			this.foundationObject.put(PROJECTTYPE + PPMConstans.CLASS, projectProject.getClassGuid());
			this.foundationObject.put(PROJECTTYPE, projectProject.getGuid());
		}
	}

	/**
	 * @return the projectType
	 */
	public String getTaskType()
	{
		return (String) this.foundationObject.get(TASKTYPE);
	}

	/**
	 * @param projectType
	 *            the projectType to set
	 */
	public void setTaskType(String projectType)
	{
		this.foundationObject.put(TASKTYPE, projectType);
	}

	/**
	 * @param projectType
	 *            the projectType to set
	 */
	public void setTaskTypeName(String projectType)
	{
		this.foundationObject.put(TASKTYPE + PMConstans.NAME, projectType);
	}

	/**
	 * @return the TASK_TYPE
	 */
	public TaskTypeEnum getTaskTypeEnum()
	{
		return TaskTypeEnum.getStatusEnum(this.foundationObject.get(TASKTYPE + PMConstans.NAME));
	}

	/**
	 * @return
	 */
	public String getProjectCalendar()
	{
		return (String) this.foundationObject.get(PROJECTCALENDAR);
	}

	public void setProjectCalendar(String value)
	{
		this.foundationObject.put(PROJECTCALENDAR, value);
	}

	/**
	 * @return the ownerProject
	 */
	public ObjectGuid getOwnerProject()
	{
		return new ObjectGuid((String) this.foundationObject.get(OWNERPROJECT + PPMConstans.CLASS), null, (String) this.foundationObject.get(OWNERPROJECT),
				(String) this.foundationObject.get(OWNERPROJECT + PPMConstans.MASTER), null);

	}

	/**
	 * @param ownerProject
	 *            the ownerProject to set
	 */
	public void setOwnerProject(ObjectGuid ownerProject)
	{
		if (ownerProject == null)
		{
			this.foundationObject.put(OWNERPROJECT + PPMConstans.MASTER, null);
			this.foundationObject.put(OWNERPROJECT + PPMConstans.CLASS, null);
			this.foundationObject.put(OWNERPROJECT, null);
		}
		else
		{
			this.foundationObject.put(OWNERPROJECT + PPMConstans.MASTER, ownerProject.getMasterGuid());
			this.foundationObject.put(OWNERPROJECT + PPMConstans.CLASS, ownerProject.getClassGuid());
			this.foundationObject.put(OWNERPROJECT, ownerProject.getGuid());
		}

	}

	/**
	 * @return the executeRole
	 */
	public String getExecuteRole()
	{
		return (String) this.foundationObject.get(EXECUTORROLE);
	}

	/**
	 * @param executeRole
	 *            the executeRole to set
	 */
	public void setExecuteRole(String executeRole)
	{
		this.foundationObject.put(EXECUTORROLE, executeRole);
	}

	/**
	 * @return the executeRole
	 */
	public void setExecuteRoleName(String roleName)
	{
		this.foundationObject.put(EXECUTORROLE + PPMConstans.NAME, roleName);
	}

	/**
	 * @return the executeRole
	 */
	public String getExecuteRoleName()
	{
		return (String) this.foundationObject.get(EXECUTORROLE + PPMConstans.NAME);
	}

	/**
	 * @return the executor
	 */
	public String getExecutor()
	{
		return (String) this.foundationObject.get(EXECUTOR);
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutor(String executor)
	{
		this.foundationObject.put(EXECUTOR, executor);
	}

	/**
	 * @return the executor
	 */
	public String getExecutorName()
	{
		return (String) this.foundationObject.get(EXECUTOR + PPMConstans.NAME);
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setExecutorName(String executor)
	{
		this.foundationObject.put(EXECUTOR + PPMConstans.NAME, executor);
	}

	/**
	 * @return the parentTask
	 */
	public ObjectGuid getParentTask()
	{
		return new ObjectGuid((String) this.foundationObject.get(PARENTTASK + PPMConstans.CLASS), null, (String) this.foundationObject.get(PARENTTASK),
				(String) this.foundationObject.get(PARENTTASK + PPMConstans.MASTER), null);

	}

	/**
	 * @param parentTask
	 *            the parentTask to set
	 */
	public void setParentTask(ObjectGuid parentTask)
	{
		if (parentTask == null)
		{
			this.foundationObject.put(PARENTTASK + PPMConstans.MASTER, null);
			this.foundationObject.put(PARENTTASK + PPMConstans.CLASS, null);
			this.foundationObject.put(PARENTTASK, null);
		}
		else
		{
			this.foundationObject.put(PARENTTASK + PPMConstans.MASTER, parentTask.getMasterGuid());
			this.foundationObject.put(PARENTTASK + PPMConstans.CLASS, parentTask.getClassGuid());
			this.foundationObject.put(PARENTTASK, parentTask.getGuid());
		}
	}

	/**
	 * @return the originalStatus
	 */
	public String getOriginalStatus()
	{
		return (String) this.foundationObject.get(ORIGINALSTATUS);
	}

	/**
	 * @return the originalStatus
	 */
	public TaskStatusEnum getOriginalStatusEnum()
	{
		return TaskStatusEnum.valueOf((String) this.foundationObject.get(ORIGINALSTATUS));
	}

	/**
	 * @param originalStatus
	 *            the originalStatus to set
	 */
	public void setOriginalStatus(TaskStatusEnum originalStatus)
	{
		if (originalStatus == null)
		{
			this.foundationObject.put(ORIGINALSTATUS, null);
		}
		else
		{
			this.foundationObject.put(ORIGINALSTATUS, originalStatus.getValue());
		}
	}

	/**
	 * @return the FromTemplate
	 */
	public ObjectGuid getFromTemplate()
	{
		return new ObjectGuid((String) this.foundationObject.get(FROMTEMPLATE + PPMConstans.CLASS), null, (String) this.foundationObject.get(FROMTEMPLATE),
				(String) this.foundationObject.get(FROMTEMPLATE + PPMConstans.MASTER), null);

	}

	/**
	 * @param FromTemplate
	 *            the FromTemplate to set
	 */
	public void setFromTemplate(ObjectGuid fromTemplate)
	{
		if (fromTemplate == null)
		{
			this.foundationObject.put(FROMTEMPLATE + PPMConstans.MASTER, null);
			this.foundationObject.put(FROMTEMPLATE + PPMConstans.CLASS, null);
			this.foundationObject.put(FROMTEMPLATE, null);
		}
		else
		{
			this.foundationObject.put(FROMTEMPLATE + PPMConstans.MASTER, fromTemplate.getMasterGuid());
			this.foundationObject.put(FROMTEMPLATE + PPMConstans.CLASS, fromTemplate.getClassGuid());
			this.foundationObject.put(FROMTEMPLATE, fromTemplate.getGuid());
		}

	}

	public String getWBSNumber()
	{
		return (String) this.foundationObject.get(WBSNUMBER);
	}

	public void setWBSNumber(String value)
	{
		this.foundationObject.put(WBSNUMBER, value);
	}

	public int getSequence()
	{
		Number b = (Number) this.foundationObject.get(SEQUENCEID);
		return b == null ? 0 : b.intValue();
	}

	public void setSequence(int value)
	{
		this.foundationObject.put(SEQUENCEID, BigDecimal.valueOf(value));
	}

	public String getPredecessorRelation()
	{
		return (String) this.foundationObject.get(PREDECESSORRELATION);
	}

	public void setPredecessorRelation(String value)
	{
		this.foundationObject.put(PREDECESSORRELATION, value);
	}

	public boolean isExceedTask()
	{
		if (taskCheck != null)
		{
			taskCheck.setPpmUtil(this);
			return taskCheck.isExceedTask();
		}
		return false;
	}

	public boolean isCriticalTask()
	{
		if (taskCheck != null)
		{
			taskCheck.setPpmUtil(this);
			return taskCheck.isCriticalTask();
		}
		return false;
	}

	/**
	 * @return the changeObject
	 */
	public ObjectGuid getChangeObject()
	{
		return new ObjectGuid((String) this.foundationObject.get(CHANGE_OBJECT + PMConstans.CLASS), null, (String) this.foundationObject.get(CHANGE_OBJECT),
				(String) this.foundationObject.get(CHANGE_OBJECT + PMConstans.MASTER), null);
	}

	/**
	 * @param changeObject
	 *            the changeObject to set
	 */
	public void setChangeObject(ObjectGuid changeObject)
	{
		if (changeObject == null)
		{
			this.foundationObject.put(CHANGE_OBJECT + PMConstans.MASTER, null);
			this.foundationObject.put(CHANGE_OBJECT + PMConstans.CLASS, null);
			this.foundationObject.put(CHANGE_OBJECT, null);
		}
		else
		{
			this.foundationObject.put(CHANGE_OBJECT + PMConstans.MASTER, changeObject.getMasterGuid());
			this.foundationObject.put(CHANGE_OBJECT + PMConstans.CLASS, changeObject.getClassGuid());
			this.foundationObject.put(CHANGE_OBJECT, changeObject.getGuid());
		}
	}

	/**
	 * @return the changeObject
	 */
	public ObjectGuid getRelationProject()
	{
		return new ObjectGuid((String) this.foundationObject.get(RELATIONPROJECT + PMConstans.CLASS), null, (String) this.foundationObject.get(RELATIONPROJECT),
				(String) this.foundationObject.get(RELATIONPROJECT + PMConstans.MASTER), null);
	}

	/**
	 * @param changeObject
	 *            the changeObject to set
	 */
	public void setRelationProject(ObjectGuid taskObject)
	{
		if (taskObject == null)
		{
			this.foundationObject.put(RELATIONPROJECT + PMConstans.MASTER, null);
			this.foundationObject.put(RELATIONPROJECT + PMConstans.CLASS, null);
			this.foundationObject.put(RELATIONPROJECT, null);
		}
		else
		{
			this.foundationObject.put(RELATIONPROJECT + PMConstans.MASTER, taskObject.getMasterGuid());
			this.foundationObject.put(RELATIONPROJECT + PMConstans.CLASS, taskObject.getClassGuid());
			this.foundationObject.put(RELATIONPROJECT, taskObject.getGuid());
		}

	}

	/**
	 * @param changeObject
	 *            the changeObject to set
	 */
	public void setRelationProjectName(String projectName)
	{
		this.foundationObject.put(RELATIONPROJECT + "$NAME", projectName);
	}

	/**
	 * @return the changeObject
	 */
	public ObjectGuid getRelationTask()
	{
		return new ObjectGuid((String) this.foundationObject.get(RELATIONTASK + PMConstans.CLASS), null, (String) this.foundationObject.get(RELATIONTASK),
				(String) this.foundationObject.get(RELATIONTASK + PMConstans.MASTER), null);
	}

	/**
	 * @param changeObject
	 *            the changeObject to set
	 */
	public void setRelationTask(ObjectGuid taskObject)
	{
		if (taskObject == null)
		{
			this.foundationObject.put(RELATIONTASK + PMConstans.MASTER, null);
			this.foundationObject.put(RELATIONTASK + PMConstans.CLASS, null);
			this.foundationObject.put(RELATIONTASK, null);
		}
		else
		{
			this.foundationObject.put(RELATIONTASK + PMConstans.MASTER, taskObject.getMasterGuid());
			this.foundationObject.put(RELATIONTASK + PMConstans.CLASS, taskObject.getClassGuid());
			this.foundationObject.put(RELATIONTASK, taskObject.getGuid());
		}

	}

	/**
	 * @return the changeObject
	 */
	public ObjectGuid getFromTask()
	{
		return new ObjectGuid((String) this.foundationObject.get(FROMTASK + PMConstans.CLASS), null, (String) this.foundationObject.get(FROMTASK),
				(String) this.foundationObject.get(FROMTASK + PMConstans.MASTER), null);
	}

	/**
	 * @param changeObject
	 *            the changeObject to set
	 */
	public void setFromTask(ObjectGuid taskObject)
	{
		if (taskObject == null)
		{
			this.foundationObject.put(FROMTASK + PMConstans.MASTER, null);
			this.foundationObject.put(FROMTASK + PMConstans.CLASS, null);
			this.foundationObject.put(FROMTASK, null);
		}
		else
		{
			this.foundationObject.put(FROMTASK + PMConstans.MASTER, taskObject.getMasterGuid());
			this.foundationObject.put(FROMTASK + PMConstans.CLASS, taskObject.getClassGuid());
			this.foundationObject.put(FROMTASK, taskObject.getGuid());
		}

	}

	public void setProjectPlanApproval(boolean value)
	{
		this.foundationObject.put(PLANAPPROVAL, BooleanUtils.getBooleanStringYN(value));
	}

	public void setTaskStartType(String taskStartType)
	{
		this.foundationObject.put(TASKSTARTTYPE, taskStartType);
	}

	public String getTaskStartType()
	{
		return (String) this.foundationObject.get(TASKSTARTTYPE);
	}

	public TaskStartType getTaskStartTypeEnum()
	{
		String str = (String) this.foundationObject.get(TASKSTARTTYPE + PMConstans.NAME);
		if (str == null)
		{
			return null;
		}
		return TaskStartType.valueOf(str);
	}

	public void setTaskPerformerType(String taskStartType)
	{
		this.foundationObject.put(TASKPERFORMERTYPE, taskStartType);
	}

	public String getTaskPerformerType()
	{
		return (String) this.foundationObject.get(TASKPERFORMERTYPE);
	}

	public TaskPerformerType getTaskPerformerTypeEnum()
	{
		String str = (String) this.foundationObject.get(TASKPERFORMERTYPE + PMConstans.NAME);
		if (str == null)
		{
			return null;
		}
		return TaskPerformerType.valueOf(str);
	}

	public void setNewProjectPlanApproval(boolean value)
	{
		this.foundationObject.put(NEWPLANAPPROVAL, BooleanUtils.getBooleanStringYN(value));
	}

	public void setNewTaskStartType(String value)
	{
		this.foundationObject.put(NEWTASKSTARTTYPE, value);
	}

	public void setNewTaskPerformerType(String value)
	{
		this.foundationObject.put(NEWTASKPERFORMERTYPE, value);
	}

	public boolean isProjectPlanApproval()
	{
		Boolean b = BooleanUtils.getBooleanByYN((String) this.foundationObject.get(PLANAPPROVAL));
		return b == null ? false : b.booleanValue();
	}

	public OnTimeStateEnum getOnTimeStateEnum()
	{
		String str = (String) this.foundationObject.get(ONTIMESTATE + PMConstans.NAME);
		if (str == null)
		{
			return null;
		}
		return OnTimeStateEnum.getValueOf(str);
	}

	public void setOnTimeStateEnum(OnTimeStateEnum onTimeState)
	{
		if (onTimeState == null)
		{
			this.foundationObject.put(ONTIMESTATE + PMConstans.NAME, null);
		}
		else
		{
			this.foundationObject.put(ONTIMESTATE + PMConstans.NAME, onTimeState.getValue());
		}
	}

	public void setOnTimeStateTitle(String title)
	{

		this.foundationObject.put(ONTIMESTATE + PMConstans.TITLE, title);

	}

	/**
	 * 
	 * @return
	 */
	public String getOnTimeState()
	{
		return (String) this.foundationObject.get(ONTIMESTATE);
	}

	/**
	 * 
	 * @param projectStatusGuid
	 */
	public void setOnTimeState(String onTimeStateGuid)
	{
		this.foundationObject.put(ONTIMESTATE, onTimeStateGuid);
	}

	/**
	 * @return the actualWorkload
	 */
	public Double getSPI()
	{
		Number b = (Number) this.foundationObject.get(SPI);
		return b == null ? DOUBLE_DEFAULT_VALUE : b.doubleValue();
	}

	/**
	 * @param actualWorkload
	 *            the actualWorkload to set
	 */
	public void setSPI(Double spi)
	{
		if (spi == null)
		{
			this.foundationObject.put(SPI, null);
		}
		else
		{
			this.foundationObject.put(SPI, (spi == null ? null : BigDecimal.valueOf(spi)));
		}
		if (statusCalculation != null)
		{
			statusCalculation.setPpmUtil(this);
			statusCalculation.setOnTimeStateBySPI(spi);
		}
	}

	/**
	 * @return the normalRun
	 */
	public Double getNormalRun()
	{
		Number b = (Number) this.foundationObject.get(NORMALRUN);
		return b == null ? DOUBLE_DEFAULT_VALUE : b.doubleValue();
	}

	/**
	 * @param normalRun
	 *            the normalRun to set
	 */
	public void setNormalRun(Integer normalRun)
	{
		this.foundationObject.put(NORMALRUN, (normalRun == null ? null : BigDecimal.valueOf(normalRun)));
	}

	/**
	 * @return the normalCop
	 */
	public Integer getNormalCop()
	{

		Number b = (Number) this.foundationObject.get(NORMALCOP);
		return b == null ? INT_DEFAULT_VALUE : b.intValue();
	}

	/**
	 * @param normalCop
	 *            the normalCop to set
	 */
	public void setNormalCop(Integer normalCop)
	{
		this.foundationObject.put(NORMALCOP, (normalCop == null ? null : BigDecimal.valueOf(normalCop)));
	}

	/**
	 * @return the lateRun
	 */
	public Integer getLateRun()
	{
		Number b = (Number) this.foundationObject.get(LATERUN);
		return b == null ? INT_DEFAULT_VALUE : b.intValue();
	}

	/**
	 * @param lateRun
	 *            the lateRun to set
	 */
	public void setLateRun(Integer lateRun)
	{
		this.foundationObject.put(LATERUN, (lateRun == null ? null : BigDecimal.valueOf(lateRun)));
	}

	/**
	 * @return the lateCop
	 */
	public Integer getLateCop()
	{
		Number b = (Number) this.foundationObject.get(LATECOP);
		return b == null ? INT_DEFAULT_VALUE : b.intValue();
	}

	/**
	 * @param lateCop
	 *            the lateCop to set
	 */
	public void setLateCop(Integer lateCop)
	{
		this.foundationObject.put(LATECOP, (lateCop == null ? null : BigDecimal.valueOf(lateCop)));
	}

	/**
	 * @return the lateCop
	 */
	public Integer getLateIni()
	{
		Number b = (Number) this.foundationObject.get(LATEINI);
		return b == null ? INT_DEFAULT_VALUE : b.intValue();
	}

	/**
	 * @param lateCop
	 *            the lateCop to set
	 */
	public void setLateIni(Integer lateIni)
	{
		this.foundationObject.put(LATEINI, (lateIni == null ? null : BigDecimal.valueOf(lateIni)));
	}

	/**
	 * @return the normalIni
	 */
	public Integer getNormalIni()
	{
		Number b = (Number) this.foundationObject.get(NORMALINI);
		return b == null ? INT_DEFAULT_VALUE : b.intValue();
	}

	/**
	 * @param normalIni
	 *            the normalIni to set
	 */
	public void setNormalIni(Integer normalIni)
	{
		this.foundationObject.put(NORMALINI, (normalIni == null ? null : BigDecimal.valueOf(normalIni)));
	}

	/**
	 * 
	 * @param operatorGuid
	 */
	public void setOperator(String operatorGuid)
	{
		this.foundationObject.put(OPERATOR, operatorGuid);
	}

	/**
	 * 
	 * @param value
	 */
	public void setProjectName(String value)
	{
		this.foundationObject.put(PROJECTNAME, value);
	}

	/**
	 * 
	 * @param value
	 */
	public void setNewProjectName(String value)
	{
		this.foundationObject.put(NEWPROJECTNAME, value);
	}

	/**
	 * @param planStartTime
	 *            the planStartTime to set
	 */
	public void setNewPlanStartTime(Date planStartTime)
	{
		this.foundationObject.put(NEWPLANSTARTTIME, planStartTime);
	}

	/**
	 * @param planFinishTime
	 *            the planFinishTime to set
	 */
	public void setNewPlanFinishTime(Date planFinishTime)
	{
		this.foundationObject.put(NEWPLANFINISHTIME, planFinishTime);
	}

	/**
	 * @param executor
	 *            the executor to set
	 */
	public void setNewExecutor(String executor)
	{
		this.foundationObject.put(NEWEXECUTOR, executor);
	}

	/**
	 * @param plannedDuration
	 *            the plannedDuration to set
	 */
	public void setNewPlannedDuration(Integer plannedDuration)
	{
		if (plannedDuration != null)
		{
			this.foundationObject.put(NEWPLANNEDDURATION, (plannedDuration == null ? null : BigDecimal.valueOf(plannedDuration)));
		}
	}

	/**
	 * @param ownerProject
	 *            the ownerProject to set
	 */
	public void setNewProjectType(ObjectGuid projectProject)
	{
		if (projectProject == null)
		{
			this.foundationObject.put(NEWPROJECTTYPE + PPMConstans.MASTER, null);
			this.foundationObject.put(NEWPROJECTTYPE + PPMConstans.CLASS, null);
			this.foundationObject.put(NEWPROJECTTYPE, null);
		}
		else
		{
			this.foundationObject.put(NEWPROJECTTYPE + PPMConstans.MASTER, projectProject.getMasterGuid());
			this.foundationObject.put(NEWPROJECTTYPE + PPMConstans.CLASS, projectProject.getClassGuid());
			this.foundationObject.put(NEWPROJECTTYPE, projectProject.getGuid());
		}
	}

	public void setNewProjectCalendar(String value)
	{
		this.foundationObject.put(NEWPROJECTCALENDAR, value);
	}

	/**
	 * @param FromTemplate
	 *            the FromTemplate to set
	 */
	public void setNewFromTemplate(ObjectGuid fromTemplate)
	{
		if (fromTemplate == null)
		{
			this.foundationObject.put(NEWFROMTEMPLATE + PPMConstans.MASTER, null);
			this.foundationObject.put(NEWFROMTEMPLATE + PPMConstans.CLASS, null);
			this.foundationObject.put(NEWFROMTEMPLATE, null);
		}
		else
		{
			this.foundationObject.put(NEWFROMTEMPLATE + PPMConstans.MASTER, fromTemplate.getMasterGuid());
			this.foundationObject.put(NEWFROMTEMPLATE + PPMConstans.CLASS, fromTemplate.getClassGuid());
			this.foundationObject.put(NEWFROMTEMPLATE, fromTemplate.getGuid());
		}
	}

	public String getImportanceLevel()
	{
		return (String) this.foundationObject.get(IMPORTANCELEVEL);
	}

	public void setImportanceLevel(String value)
	{
		this.foundationObject.put(IMPORTANCELEVEL, value);
	}

	public void setNewImportanceLevel(String value)
	{
		this.foundationObject.put(NEWIMPORTANCELEVEL, value);
	}

	public void setNewPlanWorkload(Double planWorkload)
	{
		this.foundationObject.put(NEWPLANWORKLOAD, (planWorkload == null ? null : BigDecimal.valueOf(planWorkload)));
	}

	public void setPlanCompleteWorkload(Double planWorkload)
	{
		this.foundationObject.put(PLANWORKLOAD + "COMPLETE", (planWorkload == null ? null : BigDecimal.valueOf(planWorkload)));
	}

	public Double getPlanCompleteWorkload()
	{
		return this.foundationObject.get(PLANWORKLOAD + "COMPLETE") == null ? 0 : ((BigDecimal) this.foundationObject.get(PLANWORKLOAD + "COMPLETE")).doubleValue();
	}

	public void setHasDeliveryItem(Boolean value)
	{
		if (value == null)
		{
			this.foundationObject.put(HASDELIVERYITEM, null);
		}
		else
		{
			this.foundationObject.put(HASDELIVERYITEM, BooleanUtils.getBooleanStringYN(value));
		}
	}

	public boolean hasDeliveryItem()
	{
		Boolean b = BooleanUtils.getBooleanByYN((String) this.foundationObject.get(HASDELIVERYITEM));
		return b == null ? false : b.booleanValue();
	}

	public boolean isSubProject()
	{
		if (!StringUtils.isNullString(this.getRelationProject().getGuid()))
		{
			return true;
		}
		return false;
	}

	public void setPVForParent(Double planWorkloadT)
	{
		this.foundationObject.put(PVFORPARENT, (planWorkloadT == null ? null : BigDecimal.valueOf(planWorkloadT)));

	}

	public Double getPVForParent()
	{
		return this.foundationObject.get(PVFORPARENT) == null ? null : ((BigDecimal) this.foundationObject.get(PVFORPARENT)).doubleValue();
	}

	public void setAC(double planWorkloadT)
	{
		this.foundationObject.put(AC, BigDecimal.valueOf(planWorkloadT));
	}

	public double getAC()
	{
		return this.foundationObject.get(AC) == null ? 0 : ((BigDecimal) this.foundationObject.get(AC)).doubleValue();
	}

	public void calculateSPIAndCPI(PMCalendar workCalendar, Date now, Double workHours, boolean isLeaf)
	{
		if (this.getTaskStatusEnum() != TaskStatusEnum.SSP)
		{
			EarnedValueManagement earnedValue = new EarnedValueManagement();
			earnedValue.setPpmUtil(this);
			earnedValue.calculateSPIAndCPI(workCalendar, now, workHours, isLeaf);
		}
	}

	public void setCPI(BigDecimal cpi)
	{
		this.foundationObject.put(CPI, cpi);
	}

	public OperationStateEnum calculateTaskOperationState(PPMFoundationObjectUtil preUtil, TaskDependEnum depend)
	{
		if (statusCalculation != null)
		{
			statusCalculation.setPpmUtil(this);
			return statusCalculation.calculateTaskOperationState(preUtil, depend);
		}
		return null;
	}

	public void setFinishedCheckPoint(String name)
	{
		this.foundationObject.put(FINISHEDCHECKPOINT, name);
	}

	public void setNextCheckPoint(String name)
	{
		this.foundationObject.put(NEXTCHECKPOINT, name);
	}

	public void setWbsPrepareContain(WBSPrepareContain wbsPrepareContain)
	{
		this.wbsPrepareContain = wbsPrepareContain;
	}

	public boolean isNeedApprove()
	{
		return this.foundationObject.get(NEEDAPPROVE) == null ? false : BooleanUtils.getBooleanByYN((String) this.foundationObject.get(NEEDAPPROVE));
	}

	public void setNeedApprove(String needApprove)
	{
		this.foundationObject.put(NEEDAPPROVE, needApprove);
	}
}
