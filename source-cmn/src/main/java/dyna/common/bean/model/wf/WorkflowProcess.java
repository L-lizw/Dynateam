/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessDefInfo
 * Wanglei 2010-11-3
 */
package dyna.common.bean.model.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.wf.WorkflowEventInfo;
import dyna.common.dto.model.wf.WorkflowLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.dto.model.wf.WorkflowTransitionInfo;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;
import dyna.common.systemenum.WorkflowTransitionConditionType;
import dyna.common.util.CloneUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public class WorkflowProcess extends SystemObjectImpl implements SystemObject
{
	private static final long										serialVersionUID		= -7446298478270942777L;

	private WorkflowProcessInfo										workflowProcessInfo		= null;

	// name-WorkflowActivity
	private Map<String, WorkflowActivity>							allActivityMap			= null;
	// Activityguid-ActivityName
	private Map<String, String>										activity_guid_name		= new HashMap<String, String>();

	private Map<WorkflowApplicationType, List<WorkflowActivity>>	appActivityMap			= null;

	private List<WorkflowLifecyclePhaseInfo>						lifecyclePhaseList		= null;

	private Map<String, WorkflowTransitionInfo>						allTransitionMap		= null;

	private Map<String, String>										transition_guid_name	= new HashMap<String, String>();

	private List<WorkflowEventInfo>									eventList				= null;

	// 编辑时使用
	private List<WorkflowActivity>									editList				= new ArrayList<WorkflowActivity>();

	private List<WorkflowTransitionInfo>							editTransitionList		= new ArrayList<WorkflowTransitionInfo>();

	public WorkflowProcess()
	{
		this.workflowProcessInfo = new WorkflowProcessInfo();
	}

	public WorkflowProcess(WorkflowProcessInfo workflowProcessInfo)
	{
		this.workflowProcessInfo = workflowProcessInfo;
	}

	public WorkflowProcessInfo getWorkflowProcessInfo()
	{
		return workflowProcessInfo;
	}

	public void setWorkflowProcessInfo(WorkflowProcessInfo workflowProcessDto)
	{
		this.workflowProcessInfo = workflowProcessDto;
	}

	public List<WorkflowActivity> getEditList()
	{
		return editList;
	}

	public void setEditList(List<WorkflowActivity> editList)
	{
		this.editList = editList;
	}

	@Override
	public String getGuid()
	{
		// TODO Auto-generated method stub
		return this.workflowProcessInfo.getGuid();
	}

	public List<WorkflowTransitionInfo> getEditTransitionList()
	{
		return editTransitionList;
	}

	public void setEditTransitionList(List<WorkflowTransitionInfo> editTransitionList)
	{
		this.editTransitionList = editTransitionList;
	}

	@Override
	public void setGuid(String guid)
	{
		// TODO Auto-generated method stub
		this.workflowProcessInfo.setGuid(guid);
	}

	public Map<String, String> getActivity_guid_name()
	{
		return activity_guid_name;
	}

	public void setActivity_guid_name(Map<String, String> activity_guid_name)
	{
		this.activity_guid_name = activity_guid_name;
	}

	public Map<String, String> getTransition_guid_name()
	{
		return transition_guid_name;
	}

	public void setTransition_guid_name(Map<String, String> transition_guid_name)
	{
		this.transition_guid_name = transition_guid_name;
	}

	/**
	 * @param actionName
	 * @return action
	 */
	public WorkflowEventInfo getEvent(String actionName)
	{
		if (this.eventList == null)
		{
			return null;
		}

		for (WorkflowEventInfo action : this.eventList)
		{
			if (actionName.equalsIgnoreCase(action.getName()))
			{
				return action;
			}
		}

		return null;
	}

	/**
	 * @param action
	 *            the action to add
	 */
	public void addEvent(WorkflowEventInfo action)
	{
		if (this.eventList == null)
		{
			this.eventList = new ArrayList<WorkflowEventInfo>();
		}
		if (!this.eventList.contains(action) && this.getEvent(action.getName()) == null)
		{
			this.eventList.add(action);
		}
	}

	public void removeEvent(WorkflowEventInfo eventInfo)
	{
		if (!SetUtils.isNullList(eventList))
		{
			Iterator<WorkflowEventInfo> it = this.eventList.iterator();
			while (it.hasNext())
			{
				WorkflowEventInfo event = it.next();
				if (event.getGuid().equals(eventInfo.getGuid()))
				{
					it.remove();
					break;
				}
			}
		}
	}

	public WorkflowActivity getActivityByName(String activityName)
	{
		if (this.allActivityMap == null)
		{
			return null;
		}
		return this.allActivityMap.get(activityName);
	}

	public WorkflowActivity getActivity(String activityguid)
	{
		if (this.allActivityMap == null)
		{
			return null;
		}
		String name = this.activity_guid_name.get(activityguid);
		return name == null ? null : this.allActivityMap.get(name);
	}

	/**
	 * @return the activityList
	 */
	public List<WorkflowActivity> getActivityList()
	{
		List<WorkflowActivity> activityList = new ArrayList<WorkflowActivity>();
		if (!SetUtils.isNullMap(allActivityMap))
		{
			activityList.addAll(allActivityMap.values());
		}
		return activityList;
	}

	/**
	 * @return the activityList
	 */
	public Map<String, WorkflowActivity> getActivityMap()
	{
		if (this.allActivityMap == null)
		{
			this.allActivityMap = new HashMap<String, WorkflowActivity>();
		}
		return allActivityMap;
	}

	public void addActivity(WorkflowActivity activity)
	{
		if (this.allActivityMap == null)
		{
			this.allActivityMap = new HashMap<String, WorkflowActivity>();
		}
		this.allActivityMap.put(activity.getName(), activity);
		if (activity.getGuid() != null)
		{
			this.activity_guid_name.put(activity.getGuid(), activity.getName());

		}
	}

	public void setActivityList(List<WorkflowActivity> allActivityList)
	{
		if (SetUtils.isNullList(allActivityList))
		{
			this.allActivityMap = new HashMap<String, WorkflowActivity>();
		}
		if (this.allActivityMap == null)
		{
			this.allActivityMap = new HashMap<String, WorkflowActivity>();
		}
		this.allActivityMap.clear();
		this.activity_guid_name.clear();
		for (WorkflowActivity wfActivity : allActivityList)
		{
			this.allActivityMap.put(wfActivity.getName(), wfActivity);
			if (wfActivity.getGuid() != null)
			{
				this.activity_guid_name.put(wfActivity.getGuid(), wfActivity.getName());
			}
		}
	}

	/**
	 * 根据节点应用类型分类的节点集合
	 * 
	 * @return
	 */
	public Map<WorkflowApplicationType, List<WorkflowActivity>> getAppActivityMap()
	{
		return this.appActivityMap;
	}

	public void setActivityMap(Map<String, WorkflowActivity> allActivityMap)
	{
		this.allActivityMap = allActivityMap;
		this.activity_guid_name.clear();
		if (!SetUtils.isNullMap(allActivityMap))
		{
			for (Map.Entry<String, WorkflowActivity> entry : allActivityMap.entrySet())
			{
				this.activity_guid_name.put(entry.getValue().getGuid(), entry.getKey());
			}
		}
	}

	/**
	 * 根据应用类型直接查找所有符合的活动节点
	 * 
	 * @param applicationType
	 * @return
	 */
	public List<WorkflowActivity> listActivityByType(WorkflowApplicationType applicationType)
	{
		if (appActivityMap == null)
		{
			return null;
		}
		return this.appActivityMap.get(applicationType);
	}

	public void addActivityByType(WorkflowActivity wfActivity, WorkflowApplicationType applicationType)
	{
		if (this.appActivityMap == null)
		{
			this.appActivityMap = new HashMap<WorkflowApplicationType, List<WorkflowActivity>>();
		}
		List<WorkflowActivity> wfActivityList = this.appActivityMap.get(applicationType);
		if (wfActivityList == null)
		{
			wfActivityList = new ArrayList<WorkflowActivity>();
			this.appActivityMap.put(applicationType, wfActivityList);
		}
		wfActivityList.add(wfActivity);
	}

	/**
	 * @return the lifecyclePhaseList
	 */
	public List<WorkflowLifecyclePhaseInfo> getLifecyclePhaseList()
	{
		if(this.lifecyclePhaseList == null)
		{
			this.lifecyclePhaseList = new ArrayList<WorkflowLifecyclePhaseInfo>();
		}
		return this.lifecyclePhaseList;
	}

	/**
	 * @param lifecyclePhaseList
	 *            the lifecyclePhaseList to set
	 */
	public void setLifecyclePhaseList(List<WorkflowLifecyclePhaseInfo> lifecyclePhaseList)
	{
		this.lifecyclePhaseList = lifecyclePhaseList;
	}

	/**
	 * @param transitionList
	 *            the transitionList to set
	 */
	public void setTransitionMap(Map<String, WorkflowTransitionInfo> allTransition)
	{
		this.allTransitionMap = allTransition;
		this.transition_guid_name.clear();
		if (!SetUtils.isNullMap(allTransition))
		{
			for (Map.Entry<String, WorkflowTransitionInfo> entry : allTransition.entrySet())
			{
				this.transition_guid_name.put(entry.getValue().getGuid(), entry.getKey());
			}
		}
	}

	/**
	 * @return the transitionList
	 */
	public WorkflowTransitionInfo getTransition(String transitionGuid)
	{
		if (allTransitionMap == null)
		{
			return null;
		}
		String name = this.transition_guid_name.get(transitionGuid);
		return name == null ? null : this.allTransitionMap.get(name.toUpperCase());
	}

	public WorkflowTransitionInfo getTransitionByName(String transitionName)
	{
		if (allTransitionMap == null)
		{
			return null;
		}
		return this.allTransitionMap.get(transitionName);
	}

	public Map<String, WorkflowTransitionInfo> getAllTransition()
	{
		if (this.allTransitionMap == null)
		{
			this.allTransitionMap = new HashMap<String, WorkflowTransitionInfo>();
		}
		return this.allTransitionMap;
	}

	public List<WorkflowTransitionInfo> listAllTransition()
	{
		List<WorkflowTransitionInfo> allTransition = new ArrayList<WorkflowTransitionInfo>();
		if (!SetUtils.isNullMap(allTransitionMap))
		{
			allTransition.addAll(allTransitionMap.values());
		}
		return allTransition;
	}

	/**
	 * 
	 * 
	 * @param activityName
	 * @return
	 */
	public WorkflowActivity getWorkflowActivity(String activityName)
	{
		if (this.getActivityList() != null)
		{
			List<WorkflowActivity> activityList = this.getActivityList();
			for (WorkflowActivity activity : activityList)
			{
				if (activityName.equalsIgnoreCase(activity.getName()))
				{
					return activity;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public WorkflowBeginActivity getBeginActivity()
	{
		return (WorkflowBeginActivity) this.getWorkflowActivity(WorkflowActivityType.BEGIN.toString());
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public WorkflowEndActivity getEndActivity()
	{
		return (WorkflowEndActivity) this.getWorkflowActivity(WorkflowActivityType.END.toString());
	}

	/**
	 * 
	 * 
	 * @param activityName
	 * @return
	 */
	public List<WorkflowActivity> getNextWorkflowActivityList(String activityName)
	{
		List<WorkflowTransitionInfo> transitionList = this.getWorkflowTransitionFromActivity(activityName);
		if (SetUtils.isNullList(transitionList))
		{
			return null;
		}

		List<WorkflowActivity> resultList = new ArrayList<WorkflowActivity>();

		for (WorkflowTransitionInfo transition : transitionList)
		{
			String nextActivityGuid = transition.getActToGuid();
			if (StringUtils.isGuid(nextActivityGuid))
			{
				String nextActivityName = activity_guid_name.get(nextActivityGuid);
				WorkflowActivity nextActivity = nextActivityName == null ? null : allActivityMap.get(nextActivityName);
				if (nextActivity != null)
				{
					resultList.add(nextActivity);
				}
			}

		}

		return resultList;
	}

	/**
	 * 
	 * 
	 * @param activityName
	 * @return
	 */
	public List<WorkflowTransitionInfo> getWorkflowTransitionFromActivity(String activityName)
	{
		List<WorkflowTransitionInfo> transitionList = this.listAllTransition();
		if (!SetUtils.isNullList(transitionList))
		{
			List<WorkflowTransitionInfo> resultList = new ArrayList<WorkflowTransitionInfo>();
			for (WorkflowTransitionInfo transition : transitionList)
			{
				String fromActivityGuid = transition.getActFromGuid();
				if (StringUtils.isGuid(fromActivityGuid))
				{
					String fromActivityName = activity_guid_name.get(fromActivityGuid);
					if (activityName.equalsIgnoreCase(fromActivityName))
					{
						resultList.add(transition);
					}
				}

			}

			if (resultList.isEmpty())
			{
				return null;
			}
			else
			{
				return resultList;
			}
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param actName
	 * @return
	 */
	public List<WorkflowActivity> getNextAcceptableActivityList(String actName)
	{
		List<WorkflowActivity> nextActs = new ArrayList<WorkflowActivity>();

		this.retrieveNextPerformable(nextActs, actName, WorkflowTransitionConditionType.ACCEPT);

		return nextActs;
	}

	/**
	 * 
	 * 
	 * @param actName
	 * @return
	 */
	public List<WorkflowActivity> getNextRejectableActivityList(String actName)
	{
		List<WorkflowActivity> nextActs = new ArrayList<WorkflowActivity>();

		this.retrieveNextPerformable(nextActs, actName, WorkflowTransitionConditionType.REJECT);

		return nextActs;
	}

	private void retrieveNextPerformable(List<WorkflowActivity> nextActs, String curActName, WorkflowTransitionConditionType condType)
	{
		List<WorkflowTransitionInfo> transList = this.getWorkflowTransitionFromActivity(curActName);
		if (SetUtils.isNullList(transList))
		{
			return;
		}

		List<WorkflowActivity> actList = new ArrayList<WorkflowActivity>();

		WorkflowActivity act = null;
		WorkflowTransitionConditionType transCondType = null;
		for (WorkflowTransitionInfo transition : transList)
		{
			transCondType = transition.getTransType();
			if (transCondType == condType || transCondType == WorkflowTransitionConditionType.REGULAR)
			{
				String toActivityGuid = transition.getActToGuid();
				if (StringUtils.isGuid(toActivityGuid))
				{
					String toActivityName = activity_guid_name.get(toActivityGuid);
					act = allActivityMap.get(toActivityName);
				}

				if (act != null)
				{
					actList.add(act);
				}
			}
		}

		if (SetUtils.isNullList(actList))
		{
			return;
		}

		WorkflowActivityType type = null;
		for (WorkflowActivity activity : actList)
		{
			type = WorkflowActivityType.getEnum(activity.getType());
			if (type == WorkflowActivityType.MANUAL || type == WorkflowActivityType.NOTIFY)
			{
				if (!nextActs.contains(activity))
				{
					nextActs.add(activity);
				}
			}
			else
			{
				this.retrieveNextPerformable(nextActs, activity.getName(), condType);
			}
		}
	}

	public List<WorkflowEventInfo> getEventList()
	{
		return this.eventList;
	}

	public void setEventList(List<WorkflowEventInfo> eventList)
	{
		this.eventList = eventList;
	}

	public void sortWorkflowActivityGate()
	{
		WorkflowActivity activity = this.getBeginActivity();
		sortWorkflowActivityGate(activity, 0);
	}

	private void sortWorkflowActivityGate(WorkflowActivity activity, int i)
	{
		List<WorkflowTransitionInfo> transitionList = this.getWorkflowTransitionFromActivity(activity.getName());
		if (transitionList != null)
		{
			for (WorkflowTransitionInfo transition : transitionList)
			{
				WorkflowTransitionConditionType transCondType = transition.getTransType();
				if (transCondType == WorkflowTransitionConditionType.ACCEPT || transCondType == WorkflowTransitionConditionType.REGULAR)
				{
					String toActivityGuid = transition.getActToGuid();
					WorkflowActivity nextActivity = null;
					if (StringUtils.isGuid(toActivityGuid))
					{
						String toActivityName = activity_guid_name.get(toActivityGuid);
						nextActivity = allActivityMap.get(toActivityName);
					}

					if (WorkflowActivityType.getEnum(nextActivity.getType()) == WorkflowActivityType.MANUAL
							|| WorkflowActivityType.getEnum(nextActivity.getType()) == WorkflowActivityType.NOTIFY
							|| WorkflowActivityType.getEnum(nextActivity.getType()) == WorkflowActivityType.SUB_PROCESS)
					{
						nextActivity.getWorkflowActivityInfo().setGate(i + 10);
						sortWorkflowActivityGate(nextActivity, i + 10);
					}
					else
					{
						sortWorkflowActivityGate(nextActivity, i);
					}
				}
			}
		}
	}

	@Override
	public WorkflowProcess clone()
	{
		// TODO Auto-generated method stub
		return CloneUtils.clone(this);
	}
}
