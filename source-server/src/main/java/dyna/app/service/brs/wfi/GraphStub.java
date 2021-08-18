/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 流程图操作分支
 * zhanghj 2010-12-23
 */
package dyna.app.service.brs.wfi;

import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.GraphRuntimeActivity;
import dyna.common.dto.wf.GraphRuntimeTransition;
import dyna.common.dto.wf.TransRestriction;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowRouteModeType;
import dyna.common.systemenum.WorkflowRouteType;
import dyna.common.util.SetUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 流程图操作分支
 * 
 * @author zhanghj
 * 
 */
@Component
public class GraphStub extends AbstractServiceStub<WFIImpl>
{

	protected List<Object> listAllGraphActivity(String procRtGuid) throws ServiceRequestException
	{
		List<GraphRuntimeActivity> listAllGraphActivivty = new ArrayList<GraphRuntimeActivity>();
		Map<String, GraphRuntimeActivity> graphActivityMap = new LinkedHashMap<String, GraphRuntimeActivity>();
		Map<Integer, Integer> levelNumber = new HashMap<Integer, Integer>();
		ActivityRuntime beginActivity = this.stubService.getActivityRuntimeStub().getBeginActivityRuntime(procRtGuid);
		Integer maxActivityNumSameLevel = 1;
		if (beginActivity != null)
		{
			GraphRuntimeActivity gra = new GraphRuntimeActivity();
			gra.setData(beginActivity);
			gra.setLevel(1);
			gra.setSameLevelNum(1);
			listAllGraphActivivty.add(gra);
			graphActivityMap.put(beginActivity.getGuid(), gra);
			levelNumber.put(gra.getLevel(), gra.getSameLevelNum());
			this.recuSearchActivity(gra, listAllGraphActivivty, graphActivityMap, levelNumber, true);
		}
		List<GraphRuntimeActivity> graphActivitys = new ArrayList<GraphRuntimeActivity>();
		for (GraphRuntimeActivity gra : listAllGraphActivivty)
		{
			gra.setSameLevelNum(levelNumber.get(gra.getLevel()));
			graphActivitys.add(gra);
			if (maxActivityNumSameLevel < levelNumber.get(gra.getLevel()))
			{
				maxActivityNumSameLevel = levelNumber.get(gra.getLevel());
			}
		}
		List<Object> listGraphInfo = new ArrayList<Object>();
		listGraphInfo.add(graphActivitys);
		listGraphInfo.add(levelNumber);
		listGraphInfo.add(maxActivityNumSameLevel);
		return listGraphInfo;
	}

	private void recuSearchActivity(GraphRuntimeActivity activity, List<GraphRuntimeActivity> listGraphRuntimeActivity,
			Map<String, GraphRuntimeActivity> graphRuntimeActivityMap, Map<Integer, Integer> levelNumber, boolean isLast)
			throws ServiceRequestException
	{
		List<ActivityRuntime> listNextActivity = null;
		listNextActivity = this.stubService.getActivityRuntimeStub().listAcceptedNotApplicationActivityRuntime(
				activity.getData().getGuid());
		if (!SetUtils.isNullList(listNextActivity))
		{
			for (ActivityRuntime activityRuntime : listNextActivity)
			{
				GraphRuntimeActivity tempActivity = graphRuntimeActivityMap.get(activityRuntime.getName());
				if (tempActivity == null)
				{
					tempActivity = this.loadGraphRuntimeActivity(activity, activityRuntime, listGraphRuntimeActivity,
							graphRuntimeActivityMap, levelNumber);
					graphRuntimeActivityMap.put(activityRuntime.getName(), tempActivity);
					this.recuSearchActivity(tempActivity, listGraphRuntimeActivity, graphRuntimeActivityMap,
							levelNumber, isLast);

				}
				else if (tempActivity.getLevel() < activity.getLevel() + 1)
				{
					Integer number = levelNumber.get(tempActivity.getLevel());
					if (number == null || number <= 0)
					{

					}
					else
					{
						number--;
						levelNumber.put(tempActivity.getLevel(), number);
					}
					tempActivity.setLevel(activity.getLevel() + 1);
					number = levelNumber.get(tempActivity.getLevel());
					if (number == null || number <= 0)
					{
						number = 1;
						levelNumber.put(tempActivity.getLevel(), number);
					}
					else
					{
						number++;
						levelNumber.put(tempActivity.getLevel(), number);
					}
					this.recuSearchActivity(tempActivity, listGraphRuntimeActivity, graphRuntimeActivityMap,
							levelNumber, isLast);
				}
			}
		}
	}

	private GraphRuntimeActivity loadGraphRuntimeActivity(GraphRuntimeActivity activity, ActivityRuntime wfa,
			List<GraphRuntimeActivity> listGraphRuntimeActivity,
			Map<String, GraphRuntimeActivity> GraphRuntimeActivityMap, Map<Integer, Integer> levelNumber)
			throws ServiceRequestException
	{
		GraphRuntimeActivity graphActivity = new GraphRuntimeActivity();
		if (wfa.getActType() == WorkflowActivityType.ROUTE)
		{
			TransRestriction restriction = this.stubService.getRoutRestrictionStub().getRoutRestriction(wfa.getGuid());
			if (restriction.getRestrictionType() == WorkflowRouteType.JOIN)
			{
				graphActivity.setRouteJoin(true);
			}
			if (restriction.getConnectionType() == WorkflowRouteModeType.XOR)
			{
				graphActivity.setOrAnd(true);
			}
		}
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
		listGraphRuntimeActivity.add(graphActivity);
		GraphRuntimeActivityMap.put(wfa.getName(), graphActivity);
		return graphActivity;
	}

	/**
	 * 工作流程活动变迁,不包括application
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<GraphRuntimeTransition> listGraphTransition(String procRtGuid) throws ServiceRequestException
	{
		List<GraphRuntimeTransition> listGraphTransition = new ArrayList<GraphRuntimeTransition>();
		ActivityRuntime beginActivity = this.stubService.getActivityRuntimeStub().getBeginActivityRuntime(procRtGuid);
		this.recuLoadGraphTransition(procRtGuid, beginActivity, listGraphTransition, beginActivity);
		return listGraphTransition;
	}

	/**
	 * 递归装载GraphTransition
	 */
	private void recuLoadGraphTransition(String procRtGuid, ActivityRuntime currentActivity,
			List<GraphRuntimeTransition> listGraphTransition, ActivityRuntime fromActivity)
			throws ServiceRequestException
	{
		if (currentActivity.getActType() != WorkflowActivityType.APPLICATION)
		{
			fromActivity = currentActivity;
		}
		List<ActivityRuntime> nextActivitys = this.stubService.getActivityRuntimeStub()
				.listAcceptedNotApplicationActivityRuntime(currentActivity.getGuid());
		GraphRuntimeTransition gt = null;
		if (!SetUtils.isNullList(nextActivitys))
		{
			for (ActivityRuntime activityRuntime : nextActivitys)
			{
				if (activityRuntime.getActType() != WorkflowActivityType.APPLICATION)
				{
					gt = new GraphRuntimeTransition();
					gt.setProcGuid(procRtGuid);
					gt.setFromActivityGuid(fromActivity.getGuid());
					gt.setToActivityGuid(activityRuntime.getGuid());
					listGraphTransition.add(gt);
				}
				this.recuLoadGraphTransition(procRtGuid, activityRuntime, listGraphTransition, fromActivity);
			}
		}
	}
}
