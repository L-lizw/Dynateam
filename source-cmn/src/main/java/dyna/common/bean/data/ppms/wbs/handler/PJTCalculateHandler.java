package dyna.common.bean.data.ppms.wbs.handler;

import java.util.Date;
import java.util.List;
import java.util.Stack;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.DataTypeEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.SetUtils;

public class PJTCalculateHandler extends PJCalculateHandler
{
	public PJTCalculateHandler(AbstractWBSDriver bean)
	{
		super(bean);
	}

	@Override
	public void calculatePlanStartTime(FoundationObject currentTask, Stack<String> taskStack) throws ServiceRequestException
	{
		this.calculatePlanStartTime(this.getDomainBean().getRootObject(), taskStack, new PPMFoundationObjectUtil(null), null);
	}

	public void calculatePlanStartTime(FoundationObject currentTask, Stack<String> taskStack, PPMFoundationObjectUtil util, Stack<String> loopStack) throws ServiceRequestException
	{
		util.setFoundation(currentTask);
		if (currentTask == null)
		{
			return;
		}
		if (!this.getDomainBean().checkHandler.isValid(currentTask, false))
		{
			return;
		}
		if (taskStack == null)
		{
			taskStack = new Stack<String>();
		}
		if (loopStack == null)
		{
			loopStack = new Stack<String>();
		}
		if (taskStack.search(currentTask.getObjectGuid().getGuid()) > -1)
		{
			return;
		}
		if (loopStack.search(currentTask.getObjectGuid().getGuid()) > -1)
		{
			return;
		}
		FoundationObject parentTask = this.getDomainBean().queryHandler.getParentTask(currentTask);
		Integer oldStart = util.getPlanStartPoint();
		Integer oldFinish = util.getPlanFinishPoint();
		Integer est = null;
		util.setFoundation(currentTask);
		if (parentTask == null)
		{
			est = util.getPlanStartPoint();
		}
		else
		{
			if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY && util.getEarliestStartTime() != null)
			{
				est = util.getEarliestStartPoint();
			}
			util.setFoundation(parentTask);
			if (est == null || est.intValue() < util.getPlanStartPoint().intValue())
			{
				est = util.getPlanStartPoint();
			}
		}

		util.setFoundation(parentTask);
		if (parentTask != null && util.getPlanStartTime() != null && (est == null || util.getPlanStartPoint().intValue() - est.intValue() > 0)
				&& parentTask == this.getDomainBean().queryHandler.getRootObject())
		{
			est = util.getPlanStartPoint();
		}

		if (est == null)
		{
			FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
			util.setFoundation(project);
			est = util.getPlanStartPoint();
		}

		if (est == null)
		{
			return;
		}
		util.setFoundation(currentTask);
		loopStack.add(currentTask.getObjectGuid().getGuid());
		Integer eft = null;
		eft = est + util.getPlannedDuration() - 1;

		// 计算依赖任务
		List<TaskRelation> preTaskList = this.getDomainBean().queryHandler.getPreTaskList(currentTask.getObjectGuid());
		if (!SetUtils.isNullList(preTaskList))
		{
			util.setFoundation(currentTask);

			Integer tmpPreDate = null;
			for (TaskRelation relation : preTaskList)
			{
				this.calculatePlanStartTime(this.getDomainBean().queryHandler.getObject(relation.getPreTaskObjectGuid()), taskStack, util, loopStack);
			}
			for (TaskRelation relation : preTaskList)
			{
				TaskDependEnum xTaskDependEnum = this.getPrepareContain().getDependTypeFromCode(relation.getDependType());
				util.setFoundation(this.getDomainBean().queryHandler.getObject(relation.getPreTaskObjectGuid()));
				tmpPreDate = null;
				switch (xTaskDependEnum)
				{
				case START_START:
					if (util.getPlanStartPoint() != null)
					{
						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = util.getPlanStartPoint() + relation.getDelayTime();
						}
						else
						{
							tmpPreDate = util.getPlanStartPoint() - relation.getDelayTime();
						}
						if (est == null || tmpPreDate.intValue() - est.intValue() > 0)
						{
							est = tmpPreDate;
						}
					}

					break;
				case START_FINISH:
					if (util.getPlanStartPoint() != null)
					{
						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = util.getPlanStartPoint() + relation.getDelayTime();
						}
						else
						{
							tmpPreDate = util.getPlanStartPoint() - relation.getDelayTime();
						}

						if (eft == null || tmpPreDate.intValue() - eft.intValue() > 0)
						{
							eft = tmpPreDate;
						}
					}
					break;

				case FINISH_START:
					if (util.getPlanFinishPoint() != null)
					{
						// 结束-开始 关系，开始日加一天
						tmpPreDate = util.getPlanFinishPoint() + 1;

						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = tmpPreDate + relation.getDelayTime();
						}
						else
						{
							tmpPreDate = tmpPreDate - relation.getDelayTime();
						}

						if (est == null || tmpPreDate.compareTo(est) > 0)
						{
							est = tmpPreDate;
						}
					}
					break;
				case FINISH_FINISH:
					if (util.getPlanFinishPoint() != null)
					{
						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = util.getPlanFinishPoint() + relation.getDelayTime();
						}
						else
						{
							tmpPreDate = util.getPlanFinishPoint() - relation.getDelayTime();
						}

						if (eft == null || tmpPreDate.intValue() - eft.intValue() > 0)
						{
							eft = tmpPreDate;
						}
					}

					break;
				default:
					break;
				}
			}
		}

		util.setFoundation(currentTask);

		Integer eftTem = est + util.getPlannedDuration() - 1;
		if (eft == null || eftTem.intValue() - eft.intValue() > 0)
		{
			eft = eftTem;
		}
		else
		{
			est = eft - util.getPlannedDuration() + 1;
		}

		util.setPlanStartPoint(est);
		util.setPlanFinishPoint(eft);

		List<FoundationObject> childList = this.getDomainBean().queryHandler.getChildList(currentTask.getObjectGuid());
		// 修改子任务

		if (!SetUtils.isNullList(childList))
		{
			for (int i = 0; i < childList.size(); i++)
			{
				FoundationObject obj = childList.get(i);
				taskStack.remove(obj.getObjectGuid().getGuid());
				this.calculatePlanStartTime(obj, taskStack, util, loopStack);
			}
		}

		if (!SetUtils.isNullList(childList))
		{
			Integer cest = null;
			Integer ceft = null;
			for (int i = 0; i < childList.size(); i++)
			{
				FoundationObject obj = childList.get(i);
				util.setFoundation(obj);

				if (cest == null)
				{
					cest = util.getPlanStartPoint();
					ceft = util.getPlanFinishPoint();
				}

				if (util.getPlanStartPoint() != null && util.getPlanStartPoint() - cest < 0)
				{
					cest = util.getPlanStartPoint();
				}
				if (util.getPlanFinishPoint() != null && util.getPlanFinishPoint() - ceft > 0)
				{
					ceft = util.getPlanFinishPoint();
				}
			}
			est = cest;
			eft = ceft;
		}
		int durationDay = eft - est + 1;

		util.setFoundation(currentTask);
		util.setPlannedDuration(durationDay);
		util.setPlanStartPoint(est);
		util.setPlanFinishPoint(eft);
		taskStack.add(currentTask.getObjectGuid().getGuid());
		// 修改依赖任务
		List<TaskRelation> fDependList = this.getDomainBean().queryHandler.getPostTaskList(currentTask.getObjectGuid());

		if (SetUtils.isNullList(fDependList) == false)
		{
			for (TaskRelation xRelationObj : fDependList)
			{
				if (taskStack.contains(xRelationObj.getTaskObjectGuid().getGuid()))
				{
					if (oldStart == null || oldFinish == null || oldStart.intValue() != est.intValue() || oldFinish.intValue() != eft.intValue())
					{
						taskStack.remove(xRelationObj.getTaskObjectGuid().getGuid());
						this.calculatePlanStartTime(this.getDomainBean().queryHandler.getObject(xRelationObj.getTaskObjectGuid()), taskStack, util, loopStack);
					}
				}
				else
				{
					this.calculatePlanStartTime(this.getDomainBean().queryHandler.getObject(xRelationObj.getTaskObjectGuid()), taskStack, util, loopStack);
				}
			}
		}
		// 修改父任务
		if (parentTask != null)
		{
			if (taskStack.contains(parentTask.getObjectGuid().getGuid()))
			{
				if (oldStart == null || oldFinish == null || oldStart.intValue() != est.intValue() || oldFinish.intValue() != eft.intValue())
				{
					taskStack.remove(parentTask.getObjectGuid().getGuid());
					this.calculatePlanStartTime(parentTask, taskStack, util, loopStack);
				}
			}
			else
			{
				this.calculatePlanStartTime(parentTask, taskStack, util, loopStack);
			}
		}
		loopStack.remove(currentTask.getObjectGuid().getGuid());
	}

	@Override
	public void calculateLasterTime(FoundationObject currentTask, Stack<String> taskStack) throws ServiceRequestException
	{

	}

	@Override
	public void change(Date startDate, PMCalendar targetPMCalendar, String changeType) throws ServiceRequestException

	{
		FoundationObject project = this.getDomainBean().getQueryHandler().getRootObject();
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(project);
		PMCalendar projectCalendar = this.getPrepareContain().getProjectCalendar();

		Date finishDate = null;
		finishDate = projectCalendar.addDay(startDate, util.getPlannedDuration() - 1);

		util.setPlanStartTime(startDate);
		util.setPlanFinishTime(finishDate);
		Date addDay = null;
		List<FoundationObject> allTaskList = this.getDomainBean().getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		if (!SetUtils.isNullList(allTaskList))
		{
			for (FoundationObject foundation : allTaskList)
			{
				util.setFoundation(foundation);
				addDay = projectCalendar.addDay(startDate, util.getPlanStartPoint());
				addDay = projectCalendar.getNextWorkingDate(addDay);
				util.setPlanStartTime(addDay);
				addDay = projectCalendar.addDay(addDay, util.getPlannedDuration() - 1);
				addDay = projectCalendar.getNextWorkingDate(addDay);
				util.setPlanFinishTime(addDay);
			}
		}

	}
}
