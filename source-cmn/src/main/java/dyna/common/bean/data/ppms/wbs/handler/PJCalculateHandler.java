package dyna.common.bean.data.ppms.wbs.handler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.LaborHourConfig;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainHandel;
import dyna.common.bean.data.ppms.wbs.AbstractWBSDriver;
import dyna.common.bean.data.ppms.wbs.TaskCompareByWBSCode;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.DataTypeEnum;
import dyna.common.systemenum.ppms.OperationStateEnum;
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskStartType;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class PJCalculateHandler extends InstanceDomainHandel<AbstractWBSDriver> implements CalculateHandler
{

	public PJCalculateHandler(AbstractWBSDriver bean)
	{
		super(bean);
	}

	public WBSPrepareContain getPrepareContain()
	{
		return this.getDomainBean().prepareContain;
	}

	@Override
	public void updateWBScode(FoundationObject task)
	{
		List<FoundationObject> list = this.getDomainBean().queryHandler.getChildList(task.getObjectGuid());
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		TaskTypeEnum type = this.getPrepareContain().getTaskTypeFromCode(util.getTaskType());
		if (SetUtils.isNullList(list) == false)
		{
			if (TaskTypeEnum.GENERAL == type)
			{
				util.setTaskType(this.getPrepareContain().getTaskTypeGuid(TaskTypeEnum.SUMMARY));
				util.getFoundation().put(PPMFoundationObjectUtil.TASKTYPE + "$TITLE", this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.SUMMARY).getTitle());
				util.getFoundation().put(PPMFoundationObjectUtil.TASKTYPE + "$NAME", this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.SUMMARY).getCode());
			}
			String parentWBSCode = StringUtils.convertNULLtoString(util.getWBSNumber());
			for (int i = 0; i < list.size(); i++)
			{
				FoundationObject obj = list.get(i);
				util.setFoundation(obj);
				util.setSequence(i + 1);
				if (type == null || this.getDomainBean().getCheckHandler().isSubProject(task))
				{
					util.setWBSNumber(String.valueOf(i + 1));
				}
				else
				{
					if (StringUtils.isNullString(parentWBSCode))
					{
						util.setWBSNumber(String.valueOf(i + 1));
					}
					else
					{
						util.setWBSNumber(parentWBSCode + "." + String.valueOf(i + 1));
					}

				}
				util.setParentTask(new ObjectGuid(task.getObjectGuid()));
				this.updateWBScode(obj);
			}
		}
		else
		{
			if (TaskTypeEnum.SUMMARY == type)
			{
				util.setTaskType(this.getPrepareContain().getTaskTypeGuid(TaskTypeEnum.GENERAL));
				util.getFoundation().put(PPMFoundationObjectUtil.TASKTYPE + "$TITLE", this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL).getTitle());
				util.getFoundation().put(PPMFoundationObjectUtil.TASKTYPE + "$NAME", this.getPrepareContain().getTaskTypeCodeItem(TaskTypeEnum.GENERAL).getCode());
			}

		}
	}

	@Override
	public void updateTaskPreInfo(FoundationObject task)
	{
		List<TaskRelation> allTaskDependList = this.getDomainBean().queryHandler.getPreTaskList(task.getObjectGuid());
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		String predecessorRelation = "";
		if (!SetUtils.isNullList(allTaskDependList))
		{
			int i = 0;
			for (TaskRelation relation : allTaskDependList)
			{
				if (this.getDomainBean().checkHandler.isValid(util.getFoundation(), true))
				{
					FoundationObject preFoundationObject = this.getDomainBean().queryHandler.getObject(relation.getPreTaskObjectGuid());
					util.setFoundation(preFoundationObject);

					if (i != 0)
					{
						predecessorRelation = predecessorRelation + ",";
					}
					predecessorRelation = predecessorRelation + util.getWBSNumber();
					if (relation.getDelayTime() == 0)
					{
						if (relation.getDependTypeEnum() != TaskDependEnum.FINISH_START)
						{
							predecessorRelation = predecessorRelation + relation.getDependType();
						}
					}
					else
					{
						predecessorRelation = predecessorRelation + relation.getDependType() + relation.getDelayTime();
					}
					i++;
				}
			}
		}
		util.setFoundation(task);
		util.setPredecessorRelation(predecessorRelation);
	}

	@Override
	public void updateTaskPreInfo()
	{
		List<TaskRelation> allTaskDependList = this.getDomainBean().queryHandler.listAllObject(TaskRelation.class, DataTypeEnum.DEPEND);
		List<String> calculSet = new ArrayList<String>();
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);

		if (!SetUtils.isNullList(allTaskDependList))
		{
			for (TaskRelation relation : allTaskDependList)
			{
				String predecessorRelation = "";
				FoundationObject foundationObject = this.getDomainBean().queryHandler.getObject(relation.getTaskObjectGuid());
				util.setFoundation(foundationObject);

				if (calculSet.contains(relation.getTaskObjectGuid().getGuid()))
				{
					predecessorRelation = util.getPredecessorRelation() + ",";
				}

				if (this.getDomainBean().checkHandler.isValid(util.getFoundation(), true))
				{
					FoundationObject preFoundationObject = this.getDomainBean().queryHandler.getObject(relation.getPreTaskObjectGuid());
					util.setFoundation(preFoundationObject);
					predecessorRelation = predecessorRelation + util.getWBSNumber();
					if (relation.getDelayTime() == 0)
					{
						if (relation.getDependTypeEnum() != TaskDependEnum.FINISH_START)
						{
							predecessorRelation = predecessorRelation + relation.getDependType();
						}
					}
					else
					{
						predecessorRelation = predecessorRelation + relation.getDependType() + relation.getDelayTime();
					}
				}
				util.setFoundation(foundationObject);
				util.setPredecessorRelation(predecessorRelation);
				if (!calculSet.contains(relation.getTaskObjectGuid().getGuid()))
				{
					calculSet.add(relation.getTaskObjectGuid().getGuid());
				}
			}
		}
		List<FoundationObject> allTaskList = this.getDomainBean().queryHandler.listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		if (allTaskList != null)
		{
			for (FoundationObject foundaiton : allTaskList)
			{
				if (!calculSet.contains(foundaiton.getObjectGuid().getGuid()))
				{
					util.setFoundation(foundaiton);
					util.setPredecessorRelation(null);
				}
			}
		}

	}

	@Override
	public void calculatePlanStartTime(FoundationObject currentTask, Stack<String> taskStack) throws ServiceRequestException
	{
		this.calculatePlanStartTime(this.getDomainBean().getRootObject(), taskStack, new PPMFoundationObjectUtil(null), null);
	}

	/**
	 * 
	 * @param currentTask
	 * @param taskStack
	 * @param util
	 * @param loopStack
	 * @throws ServiceRequestException
	 */
	public void calculatePlanStartTime(FoundationObject currentTask, Stack<String> taskStack, PPMFoundationObjectUtil util, Stack<String> loopStack) throws ServiceRequestException
	{
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

		PMCalendar projectCalendar = this.getPrepareContain().getProjectCalendar();
		util.setFoundation(currentTask);
		Date oldStart = util.getPlanStartTime();
		Date oldFinish = util.getPlanFinishTime();
		FoundationObject parentTask = this.getDomainBean().queryHandler.getParentTask(currentTask);
		Date est = null;
		util.setFoundation(currentTask);
		if (parentTask == null)
		{
			est = util.getPlanStartTime();
		}
		else
		{
			if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY && util.getEarliestStartTime() != null)
			{
				est = util.getEarliestStartTime();
			}
			util.setFoundation(parentTask);
			if (est == null || est.getTime() < util.getPlanStartTime().getTime())
			{
				est = util.getPlanStartTime();
			}
		}

		util.setFoundation(parentTask);
		if (parentTask != null && util.getPlanStartTime() != null && (est == null || util.getPlanStartTime().getTime() - est.getTime() > 0)
				&& parentTask == this.getDomainBean().queryHandler.getRootObject())
		{
			est = util.getPlanStartTime();
		}

		if (est == null)
		{
			FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
			util.setFoundation(project);
			est = util.getPlanStartTime();
		}

		if (est == null)
		{
			return;
		}
		util.setFoundation(currentTask);
		loopStack.add(currentTask.getObjectGuid().getGuid());
		Date eft = null;
		eft = projectCalendar.addDay(est, util.getPlannedDuration() - 1);

		// 计算依赖任务
		List<TaskRelation> preTaskList = this.getDomainBean().queryHandler.getPreTaskList(currentTask.getObjectGuid());
		if (!SetUtils.isNullList(preTaskList))
		{
			util.setFoundation(currentTask);

			Date tmpPreDate = null;

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
					if (util.getPlanStartTime() != null)
					{
						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.addDay(util.getPlanStartTime(), relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.decDay(util.getPlanStartTime(), 0 - relation.getDelayTime());
						}
						if (est == null || tmpPreDate.getTime() - est.getTime() > 0)
						{
							est = tmpPreDate;
						}
					}

					break;
				case START_FINISH:
					if (util.getPlanStartTime() != null)
					{
						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.addDay(util.getPlanStartTime(), relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.decDay(util.getPlanStartTime(), 0 - relation.getDelayTime());
						}

						if (eft == null || tmpPreDate.getTime() - eft.getTime() > 0)
						{
							eft = tmpPreDate;
						}
					}
					break;

				case FINISH_START:
					if (util.getPlanFinishTime() != null)
					{
						// 结束-开始 关系，开始日加一天
						tmpPreDate = projectCalendar.addDay(util.getPlanFinishTime(), 1);

						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.addDay(tmpPreDate, relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.decDay(tmpPreDate, 0 - relation.getDelayTime());
						}

						if (est == null || tmpPreDate.compareTo(est) > 0)
						{
							est = tmpPreDate;
						}
					}
					break;
				case FINISH_FINISH:
					if (util.getPlanFinishTime() != null)
					{
						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.addDay(util.getPlanFinishTime(), relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.decDay(util.getPlanFinishTime(), 0 - relation.getDelayTime());
						}

						if (eft == null || tmpPreDate.getTime() - eft.getTime() > 0)
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

		Date eftTem = projectCalendar.addDay(est, util.getPlannedDuration() - 1);
		if (eft == null || eftTem.getTime() - eft.getTime() > 0)
		{
			eft = eftTem;
		}
		else
		{
			est = projectCalendar.decDay(eft, util.getPlannedDuration() - 1);
		}

		util.setPlanStartTime(est);
		util.setPlanFinishTime(eft);

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
			Date cest = null;
			Date ceft = null;
			for (int i = 0; i < childList.size(); i++)
			{
				FoundationObject obj = childList.get(i);
				util.setFoundation(obj);

				if (cest == null)
				{
					cest = util.getPlanStartTime();
					ceft = util.getPlanFinishTime();
				}

				if (util.getPlanStartTime() != null && util.getPlanStartTime().getTime() - cest.getTime() < 0)
				{
					cest = util.getPlanStartTime();
				}
				if (util.getPlanFinishTime() != null && util.getPlanFinishTime().getTime() - ceft.getTime() > 0)
				{
					ceft = util.getPlanFinishTime();
				}
			}
			est = cest;
			eft = ceft;
		}
		int durationDay = projectCalendar.getDurationDay(est, eft) + 1;

		est = projectCalendar.getNextWorkingDate(est);
		eft = projectCalendar.getNextWorkingDate(eft);

		util.setFoundation(currentTask);
		util.setPlannedDuration(durationDay);
		util.setPlanStartTime(est);
		util.setPlanFinishTime(eft);
		taskStack.add(currentTask.getObjectGuid().getGuid());
		List<TaskRelation> fDependList = this.getDomainBean().queryHandler.getPostTaskList(currentTask.getObjectGuid());
		if (SetUtils.isNullList(fDependList) == false)
		{
			for (TaskRelation xRelationObj : fDependList)
			{
				if (taskStack.contains(xRelationObj.getTaskObjectGuid().getGuid()))
				{
					if (oldStart == null || oldFinish == null || oldStart.getTime() != est.getTime() || oldFinish.getTime() != eft.getTime())
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
				if (oldStart == null || oldFinish == null || oldStart.getTime() != est.getTime() || oldFinish.getTime() != eft.getTime())
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
		this.calculateLasterTime(currentTask, taskStack, null);
	}

	public void calculateLasterTime(FoundationObject currentTask, Stack<String> taskStack, Stack<String> loopStack) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(currentTask);
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
		Date current_lst = null;
		Date current_lft = util.getLatestFinishTime();

		Date oldStart = util.getLatestStartTime();
		Date oldFinish = util.getLatestFinishTime();

		FoundationObject parentTask = this.getDomainBean().queryHandler.getParentTask(currentTask);
		if (parentTask == null)
		{
			current_lft = util.getPlanFinishTime();
		}
		else
		{
			util.setFoundation(parentTask);
			if (util.getLatestFinishTime() != null)
			{
				current_lft = util.getLatestFinishTime();
			}
		}
		util.setFoundation(parentTask);
		if (parentTask != null && util.getLatestFinishTime() != null && (current_lft == null || util.getLatestFinishTime().getTime() - current_lft.getTime() > 0)
				&& parentTask == this.getDomainBean().queryHandler.getRootObject())
		{
			current_lft = util.getLatestFinishTime();
		}

		if (current_lft == null)
		{
			FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
			util.setFoundation(project);
			current_lft = util.getLatestFinishTime();
		}
		if (current_lft == null)
		{
			return;
		}
		util.setFoundation(currentTask);
		loopStack.add(currentTask.getObjectGuid().getGuid());

		PMCalendar projectCalendar = this.getPrepareContain().getProjectCalendar();

		// 计算依赖任务
		List<TaskRelation> postTaskList = this.getDomainBean().queryHandler.getPostTaskList(currentTask.getObjectGuid());
		if (!SetUtils.isNullList(postTaskList))
		{
			util.setFoundation(currentTask);

			Date tmpPreDate = null;

			for (TaskRelation relation : postTaskList)
			{
				this.calculateLasterTime(this.getDomainBean().queryHandler.getObject(relation.getTaskObjectGuid()), taskStack, loopStack);
			}
			for (TaskRelation relation : postTaskList)
			{
				TaskDependEnum xTaskDependEnum = this.getPrepareContain().getDependTypeFromCode(relation.getDependType());

				util.setFoundation(this.getDomainBean().queryHandler.getObject(relation.getTaskObjectGuid()));
				tmpPreDate = null;
				switch (xTaskDependEnum)
				{
				case START_START:
					if (util.getLatestStartTime() != null)
					{
						tmpPreDate = projectCalendar.getNextWorkingDate(util.getLatestStartTime());

						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.decDay(tmpPreDate, relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.addDay(tmpPreDate, relation.getDelayTime());
						}
						if (current_lst == null || tmpPreDate.getTime() < current_lst.getTime())
						{
							current_lst = tmpPreDate;
						}
					}

					break;
				case START_FINISH:
					if (util.getLatestFinishTime() != null)
					{
						tmpPreDate = projectCalendar.getNextWorkingDate(util.getLatestFinishTime());

						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.decDay(tmpPreDate, relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.addDay(tmpPreDate, relation.getDelayTime());
						}

						if (current_lst == null || tmpPreDate.getTime() - current_lst.getTime() < 0)
						{
							current_lst = tmpPreDate;
						}
					}
					break;

				case FINISH_START:
					if (util.getLatestStartTime() != null)
					{
						tmpPreDate = projectCalendar.getNextWorkingDate(util.getLatestStartTime());

						if (relation.getDelayTime() >= 0)
						{
							tmpPreDate = projectCalendar.decDay(tmpPreDate, relation.getDelayTime() + 1);
						}
						else
						{
							tmpPreDate = projectCalendar.addDay(tmpPreDate, relation.getDelayTime() + 1);
						}

						if (current_lft == null || tmpPreDate.getTime() - current_lft.getTime() < 0)
						{
							current_lft = tmpPreDate;
						}
					}
					break;
				case FINISH_FINISH:
					if (util.getLatestFinishTime() != null)
					{
						tmpPreDate = projectCalendar.getNextWorkingDate(util.getLatestFinishTime());

						if (relation.getDelayTime() > 0)
						{
							tmpPreDate = projectCalendar.decDay(tmpPreDate, relation.getDelayTime());
						}
						else
						{
							tmpPreDate = projectCalendar.addDay(tmpPreDate, relation.getDelayTime());
						}

						if (current_lft == null || tmpPreDate.getTime() - current_lft.getTime() < 0)
						{
							current_lft = tmpPreDate;
						}
					}

					break;
				default:
					break;
				}
			}
		}

		util.setFoundation(currentTask);
		if (current_lst != null)
		{
			Date eftTem = projectCalendar.addDay(current_lst, util.getPlannedDuration() - 1);
			if (current_lft == null || eftTem.getTime() - current_lft.getTime() < 0)
			{
				current_lft = eftTem;
			}
		}
		current_lst = projectCalendar.decDay(current_lft, util.getPlannedDuration() - 1);
		util.setLatestStartTime(current_lst);
		util.setLatestFinishTime(current_lft);

		List<FoundationObject> childList = this.getDomainBean().queryHandler.getChildList(currentTask.getObjectGuid());
		// 修改子任务
		if (!SetUtils.isNullList(childList))
		{
			for (int i = childList.size() - 1; i > -1; i--)
			{
				FoundationObject obj = childList.get(i);
				taskStack.remove(obj.getObjectGuid().getGuid());
				this.calculateLasterTime(obj, taskStack, loopStack);
			}
		}

		if (!SetUtils.isNullList(childList))
		{
			Date child_est = null;
			Date child_eft = null;
			for (int i = childList.size() - 1; i > -1; i--)
			{
				FoundationObject obj = childList.get(i);
				util.setFoundation(obj);

				if (child_est == null)
				{
					child_est = util.getLatestStartTime();
					child_eft = util.getLatestFinishTime();
				}

				if (util.getLatestStartTime() != null && util.getLatestStartTime().getTime() - child_est.getTime() < 0)
				{
					child_est = util.getLatestStartTime();
				}
				if (util.getLatestFinishTime() != null && util.getLatestFinishTime().getTime() - child_eft.getTime() > 0)
				{
					child_eft = util.getLatestFinishTime();
				}

			}
			current_lst = child_est;
			current_lft = child_eft;
		}

		if (null != current_lst && null != current_lft)
		{
			current_lst = projectCalendar.getNextWorkingDate(current_lst);
			current_lft = projectCalendar.getNextWorkingDate(current_lft);
			util.setFoundation(currentTask);
			util.setLatestStartTime(current_lst);
			util.setLatestFinishTime(current_lft);
			taskStack.add(currentTask.getObjectGuid().getGuid());
		}

		// 修改依赖任务
		List<TaskRelation> preDependList = this.getDomainBean().queryHandler.getPreTaskList(currentTask.getObjectGuid());

		if (SetUtils.isNullList(preDependList) == false)
		{
			for (TaskRelation xRelationObj : preDependList)
			{
				if (taskStack.contains(xRelationObj.getPreTaskObjectGuid().getGuid()))
				{
					if (oldStart == null || oldFinish == null || oldStart.getTime() != current_lst.getTime() || oldFinish.getTime() != current_lft.getTime())
					{
						taskStack.remove(xRelationObj.getPreTaskObjectGuid().getGuid());
						this.calculateLasterTime(this.getDomainBean().queryHandler.getObject(xRelationObj.getPreTaskObjectGuid()), taskStack, loopStack);
					}
				}
				else
				{
					this.calculateLasterTime(this.getDomainBean().queryHandler.getObject(xRelationObj.getPreTaskObjectGuid()), taskStack, loopStack);
				}
			}
		}

		// 修改父任务
		if (parentTask != null)
		{
			if (taskStack.contains(parentTask.getObjectGuid().getGuid()))
			{
				taskStack.remove(parentTask.getObjectGuid().getGuid());
				this.calculateLasterTime(parentTask, taskStack, loopStack);
			}
			else
			{
				this.calculateLasterTime(parentTask, taskStack, loopStack);
			}
		}
		loopStack.remove(currentTask.getObjectGuid().getGuid());
	}

	@Override
	public void calculateProjectInfo(FoundationObject project, FoundationObject task, Date now, Map<String, Integer> countMap) throws ServiceRequestException
	{

		PMCalendar workCalendar = this.getDomainBean().prepareContain.getProjectCalendar();
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		if (this.getDomainBean().getCheckHandler().isValid(task, true) == false)
		{
			return;
		}
		if (countMap == null)
		{
			countMap = new HashMap<String, Integer>();
			countMap.put(PPMFoundationObjectUtil.LATEINI, 0);
			countMap.put(PPMFoundationObjectUtil.LATERUN, 0);
			countMap.put(PPMFoundationObjectUtil.LATECOP, 0);
			countMap.put(PPMFoundationObjectUtil.NORMALINI, 0);
			countMap.put(PPMFoundationObjectUtil.NORMALRUN, 0);
			countMap.put(PPMFoundationObjectUtil.NORMALCOP, 0);
		}

		double totalPlanCompleteWorkload = 0;
		double totalAC = 0;
		double spi = 0;

		// 摘要任务
		List<FoundationObject> childList = this.getDomainBean().queryHandler.getChildList(task.getObjectGuid());
		if (!SetUtils.isNullList(childList))
		{
			PPMFoundationObjectUtil childUtil = new PPMFoundationObjectUtil(null);
			for (FoundationObject child : childList)
			{
				childUtil.setFoundation(child);
				if (childUtil.getTaskStatus() == null || childUtil.getTaskType() == null)
				{
					continue;
				}
				if (this.getDomainBean().getCheckHandler().isValid(child, true))
				{
					this.calculateProjectInfo(project, child, now, countMap);
					totalPlanCompleteWorkload = totalPlanCompleteWorkload + childUtil.getPlanCompleteWorkload();
					totalAC = totalAC + childUtil.getAC();
				}
			}
			util.setPlanCompleteWorkload(totalPlanCompleteWorkload);
			util.setAC(totalAC);
		}
		util.setWbsPrepareContain(this.getPrepareContain());
		util.calculateSPIAndCPI(workCalendar, now, this.getPrepareContain().getWorkHourConfig().getStandardhour(), SetUtils.isNullList(childList));
		spi = util.getSPI();
		if (util.getTaskStatusEnum() == TaskStatusEnum.INI)
		{
			// 未启动
			if (project != task)
			{
				if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
				{
					if (util.getPlanStartTime() != null && now.getTime() - util.getPlanStartTime().getTime() > 0)
					{
						countMap.put(PPMFoundationObjectUtil.LATEINI, (countMap.get(PPMFoundationObjectUtil.LATEINI)) + 1);
					}
					else
					{
						countMap.put(PPMFoundationObjectUtil.NORMALINI, (countMap.get(PPMFoundationObjectUtil.NORMALINI)) + 1);
					}
				}
			}
		}
		else
		{
			if (spi >= 1)
			{
				// 正常状态
				if (project != task)
				{
					if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
					{
						if (util.getTaskStatusEnum() == TaskStatusEnum.RUN || util.getTaskStatusEnum() == TaskStatusEnum.PUS)
						{
							countMap.put(PPMFoundationObjectUtil.NORMALRUN, (countMap.get(PPMFoundationObjectUtil.NORMALRUN)) + 1);

						}
						else if (util.getTaskStatusEnum() == TaskStatusEnum.COP)
						{
							countMap.put(PPMFoundationObjectUtil.NORMALCOP, (countMap.get(PPMFoundationObjectUtil.NORMALCOP)) + 1);

						}
					}
				}
			}
			else if (spi < 1 && now.getTime() - util.getPlanFinishTime().getTime() > 0)
			{
				// 延迟
				if (project != task)
				{
					if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
					{
						if (util.getTaskStatusEnum() == TaskStatusEnum.COP)
						{
							countMap.put(PPMFoundationObjectUtil.LATECOP, (countMap.get(PPMFoundationObjectUtil.LATECOP)) + 1);

						}
						else if (util.getTaskStatusEnum() == TaskStatusEnum.RUN || util.getTaskStatusEnum() == TaskStatusEnum.PUS)
						{
							countMap.put(PPMFoundationObjectUtil.LATERUN, (countMap.get(PPMFoundationObjectUtil.LATERUN)) + 1);
						}
					}
				}
			}
			else if (spi < 1)
			{
				// 潜在进度风险
				if (util.getTaskStatusEnum() == TaskStatusEnum.RUN || util.getTaskStatusEnum() == TaskStatusEnum.PUS)
				{
					// 潜在风险任务算正常
					if (project != task)
					{
						if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
						{
							countMap.put(PPMFoundationObjectUtil.NORMALRUN, (countMap.get(PPMFoundationObjectUtil.NORMALRUN)) + 1);
						}
					}
				}
				else if (util.getTaskStatusEnum() == TaskStatusEnum.COP)
				{
					if (project != task)
					{
						if (util.getTaskTypeEnum() != TaskTypeEnum.SUMMARY)
						{
							countMap.put(PPMFoundationObjectUtil.LATECOP, (countMap.get(PPMFoundationObjectUtil.LATECOP)) + 1);
						}
					}
				}
			}
		}

		// 设置项目信息
		if (project == task)
		{
			util.setFoundation(project);

			util.setLateCop(countMap.get(PPMFoundationObjectUtil.LATECOP));
			util.setLateRun(countMap.get(PPMFoundationObjectUtil.LATERUN));
			util.setLateIni(countMap.get(PPMFoundationObjectUtil.LATEINI));

			util.setNormalCop(countMap.get(PPMFoundationObjectUtil.NORMALCOP));
			util.setNormalRun(countMap.get(PPMFoundationObjectUtil.NORMALRUN));
			util.setNormalIni(countMap.get(PPMFoundationObjectUtil.NORMALINI));

		}
	}

	@Override
	public void calculateMilestone() throws ServiceRequestException
	{
		LaborHourConfig workHourConfig = this.getPrepareContain().getWorkHourConfig();
		List<CheckpointConfig> checkPointConfigList = this.getDomainBean().getQueryHandler().getCheckPointConfigList();
		if (SetUtils.isNullList(checkPointConfigList))
		{
			return;
		}

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
		List<FoundationObject> listAllObject = this.getDomainBean().getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		if (listAllObject != null)
		{
			Collections.sort(listAllObject, new TaskCompareByWBSCode());
		}
		int i = 0;

		Date epst = null;
		Date lpft = null;
		Date laft = null;
		Double totlePlanWorkload = 0D;
		Double totleActualWorkload = 0D;

		if (!SetUtils.isNullList(listAllObject))
		{
			for (FoundationObject foundation : listAllObject)
			{
				util.setFoundation(foundation);

				if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
				{
					continue;
				}

				if (checkPointConfigList.size() <= i)
				{
					break;
				}

				if (epst == null || epst.getTime() - util.getPlanStartTime().getTime() > 0)
				{
					epst = util.getPlanStartTime();
				}

				if (lpft == null || lpft.getTime() - util.getPlanFinishTime().getTime() < 0)
				{
					lpft = util.getPlanFinishTime();
				}

				if (laft == null || util.getActualFinishTime() == null || laft.getTime() - util.getActualFinishTime().getTime() < 0)
				{
					laft = util.getActualFinishTime();
				}

				if (util.getPlanWorkload() == null)
				{
					totlePlanWorkload = totlePlanWorkload + util.getPlannedDuration() * workHourConfig.getStandardhour();
				}
				else
				{
					totlePlanWorkload = totlePlanWorkload + util.getPlanWorkload() * workHourConfig.getStandardhour();
				}
				if (util.getTaskStatusEnum() == TaskStatusEnum.COP || util.getTaskStatusEnum() == TaskStatusEnum.SSP)
				{
					totleActualWorkload = totlePlanWorkload;
				}
				else
				{
					totleActualWorkload = (util.getActualWorkload() == null ? totleActualWorkload : util.getActualWorkload() + totleActualWorkload);
				}

				if (util.getTaskTypeEnum() == TaskTypeEnum.MILESTONE)
				{
					CheckpointConfig config = checkPointConfigList.get(i++);
					config.setRelatedTaskObject(foundation.getObjectGuid());

					config.setActualFinishTime(laft);
					config.setPlanFinishTime(lpft);
					if (totlePlanWorkload != 0)
					{
						DecimalFormat df = new DecimalFormat("#.00");
						config.setCompletionRate(new Double(df.format(totleActualWorkload / totlePlanWorkload * 100)));
					}
					else
					{
						config.setCompletionRate(0.0);
					}
					epst = null;
					lpft = null;
					totlePlanWorkload = 0D;
					totleActualWorkload = 0D;
				}

			}
		}
		util.setFoundation(this.getDomainBean().getRootObject());
		for (i = 0; i < checkPointConfigList.size(); i++)
		{
			CheckpointConfig config = checkPointConfigList.get(i);
			if (config.getActualFinishTime() != null)
			{
				util.setFinishedCheckPoint(config.getName());
			}
			else
			{
				util.setNextCheckPoint(config.getName());
				break;
			}
		}
	}

	@Override
	public void change(Date startDate, PMCalendar projectCalendar, String changeType) throws ServiceRequestException
	{
		FoundationObject project = this.getDomainBean().queryHandler.getRootObject();
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(project);
		Date projectStartTime = util.getPlanStartTime();
		if (PMConstans.COPY_TYPE_P2P.equalsIgnoreCase(changeType) && startDate != null)
		{
			util.setPlanStartTime(startDate);
			Date finishDate = null;
			finishDate = projectCalendar.addDay(startDate, util.getPlannedDuration() - 1);
			util.setPlanFinishTime(finishDate);
		}

		List<FoundationObject> allTaskList = this.getDomainBean().queryHandler.listAllObject(FoundationObject.class, DataTypeEnum.TASK);
		if (!SetUtils.isNullList(allTaskList))
		{
			Date start = null;
			Date finish = null;
			for (FoundationObject foundation : allTaskList)
			{
				util.setFoundation(foundation);
				if (PMConstans.COPY_TYPE_P2T.equalsIgnoreCase(changeType))
				{
					long d = (util.getPlanStartTime().getTime() - projectStartTime.getTime()) / (3600 * 24 * 1000);
					util.setPlanStartPoint((int) d);
					util.setPlanFinishPoint((int) d + util.getPlannedDuration() - 1);
					util.setPlanStartTime(null);
					util.setPlanFinishTime(null);
					util.setEarliestStartTime(null);
					util.setEarliestFinishTime(null);
					util.setPlanFinishTime(null);
				}
				else if (PMConstans.COPY_TYPE_P2P.equalsIgnoreCase(changeType))
				{
					int d = 0;
					if (util.getPlanStartTime().compareTo(projectStartTime) > 0)
					{
						d = projectCalendar.getDurationDay(projectStartTime, util.getPlanStartTime());
					}
					else
					{
						d = projectCalendar.getDurationDay(util.getPlanStartTime(), projectStartTime);
					}
					start = projectCalendar.addDay(startDate, d);
					start = projectCalendar.getNextWorkingDate(start);
					util.setPlanStartTime(start);
					finish = projectCalendar.addDay(start, util.getPlannedDuration() - 1);
					finish = projectCalendar.getNextWorkingDate(finish);
					util.setPlanFinishTime(finish);
					util.setEarliestFinishTime(null);

				}
			}
		}

	}

	@Override
	public void updatePlanWorkload()
	{
		Double updatePlanWorkload = this.updatePlanWorkload(this.getDomainBean().getRootObject());

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(this.getDomainBean().getRootObject());
		util.setPlanWorkload(updatePlanWorkload);
	}

	protected Double updatePlanWorkload(FoundationObject task)
	{
		List<FoundationObject> list = this.getDomainBean().getQueryHandler().getChildList(task.getObjectGuid());

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		Double pworkload = 0D;
		Double aworkload = 0D;
		if (SetUtils.isNullList(list) == false)
		{

			for (int i = 0; i < list.size(); i++)
			{
				FoundationObject obj = list.get(i);
				util.setFoundation(obj);
				if (this.getDomainBean().getCheckHandler().isValid(obj, true))
				{
					pworkload = pworkload + this.updatePlanWorkload(obj);
					aworkload = aworkload + util.getActualWorkload();
				}
			}
			// 摘要任务
			util.setFoundation(task);
			util.setPlanWorkload(pworkload);
			util.setActualWorkload(aworkload);
			if (((task == this.getDomainBean().getRootObject() && util.getProjectStatusEnum() != ProjectStatusEnum.INI) || //
					(task != this.getDomainBean().getRootObject() && util.getTaskStatusEnum() != TaskStatusEnum.INI)) && pworkload > 0)
			{
				DecimalFormat df = new DecimalFormat("#.00");
				util.setCompletionRate(new Double(df.format(aworkload * 100 / pworkload)));
			}
		}
		else
		{
			// 非摘要任务
			pworkload = util.getPlanWorkload() == null ? util.getPlannedDuration() * this.getPrepareContain().getWorkHourConfig().getStandardhour() : util.getPlanWorkload();
			aworkload = util.getActualWorkload() == null ? 0 : util.getActualWorkload();
		}

		if (util.getActualStartTime() != null && util.getActualFinishTime() != null)
		{
			int tempd = 0;
			tempd = this.getPrepareContain().getProjectCalendar().getDurationDay(util.getActualStartTime(), util.getActualFinishTime()) + 1;
			util.setActualDuration(tempd);
		}
		// 重新计算实际工作量
		if (!(util.getCompletionRate() == null || util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY || util.getFoundation() == this.getDomainBean().getRootObject()))
		{
			NumberFormat formater = NumberFormat.getInstance();
			formater.setGroupingUsed(false);
			formater.setMaximumFractionDigits(2);
			util.setActualWorkload(new Double(formater.format(pworkload * (util.getCompletionRate() / 100))));
		}

		if (util.getTaskStatusEnum() == TaskStatusEnum.SSP)
		{
			pworkload = aworkload;
		}
		return pworkload;
	}

	protected void updateOperateStatus(FoundationObject task, TaskStartType startType)
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		PPMFoundationObjectUtil preUtil = new PPMFoundationObjectUtil(null);
		OperationStateEnum operation = null;
		List<TaskRelation> preTaskList = this.getDomainBean().getQueryHandler().getPreTaskList(task.getObjectGuid());

		if (util.getTaskTypeEnum() == TaskTypeEnum.SUMMARY)
		{
			util.setOperationState(null);
			util.setOperationStateEnum(null);
			return;
		}
		// 手动启动
		// 是否可启动 ，不可启动>可启动
		if (!SetUtils.isNullList(preTaskList))
		{
			for (TaskRelation relation : preTaskList)
			{
				FoundationObject preObject = this.getDomainBean().getQueryHandler().getObject(relation.getPreTaskObjectGuid());
				preUtil.setFoundation(preObject);
				if (preObject != null)
				{
					operation = util.calculateTaskOperationState(preUtil, relation.getDependTypeEnum());

					if (operation == OperationStateEnum.CANNOTCOMPLETE || operation == OperationStateEnum.CANNOTSTART)
					{
						break;
					}
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

		util.setOperationStateEnum(operation);
		if (operation == null)
		{
			util.setOperationState(null);
		}
		else
		{
			CodeItemInfo operationStatusCodeItem = this.getPrepareContain().getOperationStatusCodeItem(operation);
			if (operationStatusCodeItem != null)
			{
				util.setOperationState(operationStatusCodeItem.getGuid());
			}
		}
	}

	@Override
	public void updateOperateStatus() throws ServiceRequestException
	{
		FoundationObject project = this.getDomainBean().getRootObject();
		PPMFoundationObjectUtil projectUtil = new PPMFoundationObjectUtil(project);

		List<FoundationObject> allTaskList = this.getDomainBean().getQueryHandler().listAllObject(FoundationObject.class, DataTypeEnum.TASK);

		if (!SetUtils.isNullList(allTaskList))
		{
			for (FoundationObject foundation : allTaskList)
			{
				if (foundation == null)
				{
					continue;
				}
				this.updateOperateStatus(foundation, projectUtil.getTaskStartTypeEnum());
			}
		}
	}
}
