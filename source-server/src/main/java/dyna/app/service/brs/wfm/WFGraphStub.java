/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ActivitiyStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.wfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.wf.WorkflowActivity;
import dyna.common.bean.model.wf.WorkflowProcess;
import dyna.common.bean.model.wf.WorkflowRouteActivity;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.wf.GraphActivity;
import dyna.common.dto.wf.GraphTransition;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowRouteType;
import dyna.common.util.SetUtils;

/**
 * @author Wanglei
 * 
 */
public class WFGraphStub extends AbstractServiceStub<WFMImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected WFGraphStub(ServiceContext context, WFMImpl service)
	{
		super(context, service);
	}

	protected List<GraphTransition> listGraphTransition(String procName) throws ServiceRequestException
	{
		WorkflowProcess process = this.stubService.getProcessStub().getProcess(procName);
		WorkflowActivity beginActivity = process.getBeginActivity();

		List<GraphTransition> listGraphTransition = new ArrayList<GraphTransition>();
		this.recuLoadGraphTransition(process, beginActivity, listGraphTransition, beginActivity);

		return listGraphTransition;
	}

	/**
	 * 递归装载GraphTransition
	 */
	private void recuLoadGraphTransition(WorkflowProcess process, WorkflowActivity currentActivity, List<GraphTransition> listGraphTransition, WorkflowActivity fromActivity)
	{
		if (WorkflowActivityType.getEnum(currentActivity.getType())!= null && WorkflowActivityType.getEnum(currentActivity.getType()) != WorkflowActivityType.APPLICATION)
		{
			fromActivity = currentActivity;
		}
		List<WorkflowActivity> nextActivitys = this.stubService.getActivitiyStub().getNextWorkflowActivityListWithoutReject(process, currentActivity.getName());
		GraphTransition gt = null;
		if (!SetUtils.isNullList(nextActivitys))
		{
			for (WorkflowActivity wfa : nextActivitys)
			{
				WorkflowActivityType type = WorkflowActivityType.getEnum(wfa.getType());
				if (type != null && type != WorkflowActivityType.APPLICATION)
				{
					gt = new GraphTransition();
					gt.setProcGuid(process.getName());
					gt.setFromActivityGuid(fromActivity.getName());
					gt.setToActivityGuid(wfa.getName());
					listGraphTransition.add(gt);
				}
				this.recuLoadGraphTransition(process, wfa, listGraphTransition, fromActivity);
			}
		}
	}

	protected List<WorkflowActivity> listAcceptaleFromActivityNotApplication(WorkflowProcess process, String actName) throws ServiceRequestException
	{
		WorkflowActivity currentActivity = process.getActivityByName(actName);

		List<WorkflowActivity> listActivityNotApplication = new ArrayList<WorkflowActivity>();
		this.recuLoadActivity(process, currentActivity, listActivityNotApplication);

		return listActivityNotApplication;
	}

	protected List<WorkflowActivityInfo> listAcceptaleFromActivityNotApplication(String procName, String actName) throws ServiceRequestException
	{
		WorkflowProcess process = this.stubService.getProcessStub().getProcess(procName);

		List<WorkflowActivity> wfActivityList = this.listAcceptaleFromActivityNotApplication(process, actName);
		List<WorkflowActivityInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(wfActivityList))
		{
			wfActivityList.forEach(wfActivity -> {
				resultList.add(wfActivity.getWorkflowActivityInfo());
			});
		}
		return resultList;
	}

	/**
	 * 递归装载activity
	 * 
	 * @param process
	 * @param activity
	 * @param listActivityNotApplication
	 */
	private void recuLoadActivity(WorkflowProcess process, WorkflowActivity activity, List<WorkflowActivity> listActivityNotApplication)
	{
		List<WorkflowActivity> nextWorkflowActivity = this.stubService.getActivitiyStub().getNextWorkflowActivityListWithoutReject(process, activity.getName());
		if (!SetUtils.isNullList(nextWorkflowActivity))
		{
			for (WorkflowActivity wfa : nextWorkflowActivity)
			{
				WorkflowActivityType type = WorkflowActivityType.getEnum(wfa.getType());
				if (type != null && type != WorkflowActivityType.APPLICATION)
				{
					listActivityNotApplication.add(wfa);
				}
				else
				{
					this.recuLoadActivity(process, wfa, listActivityNotApplication);
				}
			}
		}
	}

	protected List<Object> listActivityGraphInfo(String procName) throws ServiceRequestException
	{
		// 获取流程模型开始节点
		String beginActivityName = WorkflowActivityType.BEGIN.toString();
		WorkflowProcess process = this.stubService.getProcessStub().getProcess(procName);
		WorkflowActivity beginActivity = process.getActivityByName(beginActivityName);
		List<GraphActivity> listGraphActivity = new ArrayList<GraphActivity>();
		Map<Integer, Integer> levelNumber = new HashMap<Integer, Integer>();
		Map<String, GraphActivity> graphActivityMap = new LinkedHashMap<String, GraphActivity>();
		GraphActivity graphActivity = new GraphActivity();
		Integer maxActivityNumSameLevel = 1;
		if (beginActivity != null)
		{
			graphActivity.setData(beginActivity);
			graphActivity.setLevel(1);
			listGraphActivity.add(graphActivity);
			graphActivityMap.put(beginActivityName, graphActivity);
			levelNumber.put(graphActivity.getLevel(), 1);
			maxActivityNumSameLevel = this.recuSearchActivity(graphActivity, listGraphActivity, graphActivityMap, process, levelNumber, true, maxActivityNumSameLevel);
		}
		List<GraphActivity> graphActivitys = new ArrayList<GraphActivity>();
		for (GraphActivity ga : listGraphActivity)
		{
			ga.setSameLevelNum(levelNumber.get(ga.getLevel()));
			graphActivitys.add(ga);
		}
		List<Object> listGraphInfo = new ArrayList<Object>();
		listGraphInfo.add(graphActivitys);
		listGraphInfo.add(levelNumber);
		listGraphInfo.add(maxActivityNumSameLevel);
		return listGraphInfo;
	}

	private Integer recuSearchActivity(GraphActivity activity, List<GraphActivity> listGraphActivity, Map<String, GraphActivity> graphActivityMap, WorkflowProcess process,
			Map<Integer, Integer> levelNumber, boolean isLast, Integer maxActivityNumSameLevel) throws ServiceRequestException
	{

		List<WorkflowActivity> listNextActivity = null;
		GraphActivity graphActivity = null;
		int splitNum = -1;

		listNextActivity = this.listAcceptaleFromActivityNotApplication(process, activity.getData().getName());
		if (!SetUtils.isNullList(listNextActivity))
		{
			if (activity.getData() instanceof WorkflowRouteActivity && ((WorkflowRouteActivity) activity.getData()).getRouteType() == WorkflowRouteType.SPLIT)
			{
				splitNum = listNextActivity.size();
				if (splitNum > maxActivityNumSameLevel)
				{
					maxActivityNumSameLevel = splitNum;
				}
				isLast = false;
			}
			for (WorkflowActivity wfa : listNextActivity)
			{
				if (splitNum == 1)
				{
					isLast = true;
				}
				if (wfa instanceof WorkflowRouteActivity && ((WorkflowRouteActivity) wfa).getRouteType() == WorkflowRouteType.JOIN)
				{
					GraphActivity tempActivity = graphActivityMap.get(wfa.getName());
					if (tempActivity != null)
					{
						if (tempActivity.getLevel() < activity.getLevel() + 1)
						{
							tempActivity.setLevel(activity.getLevel() + 1);
							graphActivityMap.put(wfa.getName(), tempActivity);
							listGraphActivity = (List<GraphActivity>) graphActivityMap.values();
						}
					}
					else
					{
						tempActivity = this.loadGraphActivity(activity, wfa, listGraphActivity, graphActivityMap, levelNumber);
					}
					if (isLast)
					{
						maxActivityNumSameLevel = this.recuSearchActivity(tempActivity, listGraphActivity, graphActivityMap, process, levelNumber, isLast, maxActivityNumSameLevel);

					}
				}
				else
				{
					graphActivity = this.loadGraphActivity(activity, wfa, listGraphActivity, graphActivityMap, levelNumber);
					maxActivityNumSameLevel = this.recuSearchActivity(graphActivity, listGraphActivity, graphActivityMap, process, levelNumber, isLast, maxActivityNumSameLevel);
				}
				splitNum--;
			}
		}
		return maxActivityNumSameLevel;
	}

	private GraphActivity loadGraphActivity(GraphActivity activity, WorkflowActivity wfa, List<GraphActivity> listGraphActivity, Map<String, GraphActivity> graphActivityMap,
			Map<Integer, Integer> levelNumber)
	{
		GraphActivity graphActivity = new GraphActivity();
		graphActivity.setData(wfa);
		graphActivity.setLevel(activity.getLevel() + 1);
		graphActivity.setParent(activity);
		Integer number = levelNumber.get(graphActivity.getLevel());
		if (number == null || number <= 0)
		{
			number = 1;
			levelNumber.put(graphActivity.getLevel(), number);
		}
		else
		{
			number++;
			levelNumber.put(graphActivity.getLevel(), number);
		}
		listGraphActivity.add(graphActivity);
		graphActivityMap.put(wfa.getName(), graphActivity);
		return graphActivity;
	}

}
