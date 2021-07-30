package dyna.common.bean.data.ppms.wbs.handler;

import java.math.BigDecimal;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.ppms.DataTypeEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;

public class PJTUpdateHandler extends PJUpdateHandler
{
	public PJTUpdateHandler(AbstractWBSDriver bean)
	{
		super(bean);
	}

	@Override
	public void addTask(FoundationObject parent, FoundationObject foundationObject, int index) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(parent);
		if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE || this.getDomainBean().getCheckHandler().isSubProject(parent))
		{
			throw new ServiceRequestException("ID_PM_NOT_ADD_TASK", "not add task");
		}

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
		if (pmObj.getPlanStartPoint() == null)
		{
			pmObj.setPlanStartPoint(parentU.getPlanStartPoint());
		}
		if (pmObj.getPlannedDuration() == null)
		{
			pmObj.setPlannedDuration(1);
		}
		if (pmObj.getPlanStartPoint() != null)
		{
			pmObj.setPlanFinishPoint(pmObj.getPlanStartPoint() + pmObj.getPlannedDuration() - 1);
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
		pmObj.setCompletionRate(null);
		CodeItemInfo taskStatusCodeItem = this.getPrepareContain().getTaskStatusCodeItem(TaskStatusEnum.INI);
		pmObj.setTaskStatus(taskStatusCodeItem.getGuid());
		pmObj.setTaskStatusName(taskStatusCodeItem.getName());
		foundationObject.put(PPMFoundationObjectUtil.EXECUTESTATUS + "$TITLE", taskStatusCodeItem.getTitle());

		CodeItemInfo taskTypeCodeItem = this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL);
		foundationObject.put(PPMFoundationObjectUtil.TASKTYPE, taskTypeCodeItem.getGuid());
		foundationObject.put(PPMFoundationObjectUtil.TASKTYPE + PMConstans.NAME, taskTypeCodeItem.getCode());
		foundationObject.put(PPMFoundationObjectUtil.TASKTYPE + "$TITLE", taskTypeCodeItem.getTitle());

		foundationObject.setStatus(SystemStatusEnum.WIP);
		pmObj.setParentTask(parent.getObjectGuid());
		pmObj.setOwnerProject(this.getDomainBean().getRootObject().getObjectGuid());
		pmObj.setPredecessorRelation(null);
		pmObj.setHasDeliveryItem(null);
		this.getDomainBean().getCalculateHandler().updateWBScode(parent);
		this.getDomainBean().getCalculateHandler().updateTaskPreInfo();
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
	}

	@Override
	protected void updatePreparePlanStartTime(PPMFoundationObjectUtil task, Object object) throws ServiceRequestException
	{
		Number planStartPoint = null;
		if (object instanceof String)
		{
			planStartPoint = new BigDecimal((String) object);
		}
		else
		{
			planStartPoint = (Number) object;
		}
		if (planStartPoint != null && (task.getPlanStartPoint() == null || task.getPlanStartPoint() != planStartPoint.doubleValue()))
		{
			if (object != this.getDomainBean().getRootObject())
			{
				task.setEarliestStartPoint(planStartPoint.intValue());
			}
			task.setPlanStartPoint(planStartPoint.intValue());
			task.setPlanFinishPoint(planStartPoint.intValue() + task.getPlannedDuration());
		}
		else if (planStartPoint == null)
		{
			if (task.getEarliestStartTime() != null)
			{
				task.setEarliestStartPoint(null);
			}
		}
	}

	/**
	 * @param planFinishTime
	 *            the planStartTime to set
	 */
	@Override
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
			return;
		}

		int oldPlanDuration = task.getPlannedDuration();

		if (plannedDuration != oldPlanDuration)
		{
			if (!(task.getTaskTypeEnum() == TaskTypeEnum.MILESTONE || task.getTaskTypeEnum() == TaskTypeEnum.GENERAL
					|| this.getDomainBean().getCheckHandler().isSubProject(task.getFoundation())) && this.getDomainBean().queryHandler.getRootObject() != task.getFoundation())
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_SUMMARY_TASK_DURATION_NOT_CHANGE", "Duration of Summary Task can't change", null, task.getWBSNumber());
			}
			if (plannedDuration <= 0)
			{
				throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_DURATION_IS_NULL", "Duration of Task is Setting Null", null, task.getWBSNumber());
			}

			task.setPlannedDuration(plannedDuration);

			int planFinishTime = task.getPlanStartPoint() + plannedDuration - 1;
			task.setPlanFinishPoint(planFinishTime);
		}
	}

	/**
	 * @param preparePlanFinishTime
	 * @throws Exception
	 */
	@Override
	protected void updatePreparePlanFinishTime(PPMFoundationObjectUtil task, Object object) throws ServiceRequestException
	{
		Number planFinishTime = null;
		if (object instanceof String)
		{
			planFinishTime = new BigDecimal((String) object);
		}
		else
		{
			planFinishTime = (Number) object;
		}
		if (planFinishTime != null && (task.getPlanFinishPoint() == null || task.getPlanFinishPoint() != planFinishTime.doubleValue()))
		{
			if (SetUtils.isNullList(this.getDomainBean().getQueryHandler().getChildList(task.getFoundation().getObjectGuid())))
			{
				if (planFinishTime != null && task.getPlanFinishPoint() != null)
				{
					if (this.getPrepareContain().getProjectCalendar() == null)
					{
						throw new ServiceRequestException("ID_PPM_WBS_PLAN_TASK_NO_EXECUTE_ROLE", "Task isn't setting Role", null, task.getWBSNumber());
					}

					int hour = planFinishTime.intValue() - task.getPlanStartPoint() + 1;
					if (hour > 0)
					{
						task.setPlannedDuration(hour);
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
	public void reschedule(FoundationObject task, Object planStartTime1, Object planFinishTime1, double percent, int duration) throws ServiceRequestException
	{
		Integer planStartTime = (Integer) planStartTime1;
		Integer planFinishTime = (Integer) planFinishTime1;

		if (percent != 0)
		{
			percent = percent / 100;
		}

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		if (planStartTime != null && planFinishTime != null)
		{
			duration = planFinishTime - planStartTime;// projectCalendar.getDurationDay(planStartTime, planFinishTime);
			util.setPlanStartPoint(planStartTime);
		}

		if (duration != 0)
		{
			percent = duration / util.getPlannedDuration();
		}

		if (percent != 0)
		{
			this.reCalculateDuration(task, percent);
			this.getDomainBean().getCalculateHandler().updatePlanWorkload();
		}
	}
}
