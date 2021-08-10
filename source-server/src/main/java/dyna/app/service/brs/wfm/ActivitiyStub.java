/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ActivitiyStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.wfm;

import java.util.ArrayList;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.wf.WorkflowActionActivity;
import dyna.common.bean.model.wf.WorkflowActivity;
import dyna.common.bean.model.wf.WorkflowChangePhaseActivity;
import dyna.common.bean.model.wf.WorkflowChangeStatusActivity;
import dyna.common.bean.model.wf.WorkflowProcess;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowActrtActionInfo;
import dyna.common.dto.model.wf.WorkflowActrtLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowActrtStatusInfo;
import dyna.common.dto.model.wf.WorkflowPhaseChangeInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.dto.model.wf.WorkflowTransitionInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;
import dyna.common.systemenum.WorkflowTransitionConditionType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.common.exception.DynaDataSqlException;

/**
 * @author Wanglei
 * 
 */
public class ActivitiyStub extends AbstractServiceStub<WFMImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected ActivitiyStub(ServiceContext context, WFMImpl service)
	{
		super(context, service);
	}

	protected WorkflowProcess getWorkflowProcessByName(String processName)
	{
		return this.stubService.getWfModelCacheStub().getWorkflowProcessByName(processName);
	}

	protected WorkflowProcess getWorkflowProcess(String processGuid)
	{
		return this.stubService.getWfModelCacheStub().getWorkflowProcess(processGuid);
	}

	protected WorkflowActivity getWorkflowActivityByName(String procName, String actName) throws ServiceRequestException
	{
		WorkflowProcess workflowProcess = this.getWorkflowProcessByName(procName);
		return workflowProcess.getWorkflowActivity(actName);
	}

	protected WorkflowActivityInfo getWorkflowActivityInfoByName(String procName, String actName) throws ServiceRequestException
	{

		WorkflowActivity wfActivity = this.getWorkflowActivityByName(procName, actName);
		return wfActivity == null ? null : wfActivity.getWorkflowActivityInfo();
	}

	protected WorkflowActivityInfo getWorkflowActivityInfo(String procModelGuid, String guid) throws ServiceRequestException
	{
		WorkflowProcess workflowProcess = this.stubService.getWfModelCacheStub().getWorkflowProcess(procModelGuid);

		WorkflowActivity wfActivity = workflowProcess.getActivity(guid);

		return wfActivity == null ? null : wfActivity.getWorkflowActivityInfo();
	}

	protected WorkflowActivityInfo getWorkflowBeginActivityInfoByName(String procName) throws ServiceRequestException
	{
		WorkflowProcess workflowProcess = this.getWorkflowProcessByName(procName);
		WorkflowActivity beginActivity = workflowProcess.getBeginActivity();
		return beginActivity == null ? null : beginActivity.getWorkflowActivityInfo();
	}

	protected List<WorkflowActivityInfo> listAcceptaleFromActivity(String procName, String actName) throws ServiceRequestException
	{
		WorkflowProcess process = this.getWorkflowProcessByName(procName);
		List<WorkflowActivity> workflowActivityList = process.getNextAcceptableActivityList(actName);
		List<WorkflowActivityInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(workflowActivityList))
		{
			workflowActivityList.forEach(workflowActivity -> {
				resultList.add(workflowActivity.getWorkflowActivityInfo());
			});
		}
		return resultList;
	}

	protected List<WorkflowActivityInfo> listRejectableFromActivity(String procName, String actName) throws ServiceRequestException
	{
		WorkflowProcess process = this.getWorkflowProcessByName(procName);
		List<WorkflowActivity> wfActivityList = process.getNextRejectableActivityList(actName);
		List<WorkflowActivityInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(wfActivityList))
		{
			wfActivityList.forEach(wfActivity -> {
				resultList.add(wfActivity.getWorkflowActivityInfo());
			});
		}
		return resultList;
	}

	public List<WorkflowActivity> listActivity(String procName)
	{
		if (!StringUtils.isNullString(procName))
		{
			WorkflowProcess process = this.getWorkflowProcessByName(procName);
			return process.getActivityList();
		}
		return null;
	}

	protected List<WorkflowActivityInfo> listActivityInfo(String guid, String procName) throws ServiceRequestException
	{
		if (guid != null)
		{
			WorkflowProcessInfo wfProcess = this.stubService.getProcessModelInfoByGuid(guid);
			if (wfProcess != null)
			{
				procName = wfProcess.getName();
			}
		}
		if (procName == null)
		{
			return null;
		}
		List<WorkflowActivity> wfActivityList = this.listActivity(procName);
		List<WorkflowActivityInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(wfActivityList))
		{
			wfActivityList.forEach(wfActivity -> {
				resultList.add(wfActivity.getWorkflowActivityInfo());
			});
		}
		return resultList;
	}

	protected List<WorkflowActrtActionInfo> listActrtAction(String processguid, String activityguid) throws ServiceRequestException
	{
		WorkflowProcess process = this.getWorkflowProcess(processguid);
		WorkflowActivity activity = process.getActivity(activityguid);
		if (activity != null && activity instanceof WorkflowActionActivity)
		{
			return ((WorkflowActionActivity) activity).getActionList();
		}
		return null;
	}

	protected List<WorkflowActrtStatusInfo> listStatusChange(String procName, String activityName) throws ServiceRequestException
	{
		List<WorkflowActrtStatusInfo> listStatusChange = null;

		WorkflowActivity changeStatusActivity = this.listActivityByApplicationType(procName, activityName, WorkflowApplicationType.CHANGE_STATUS);
		if (changeStatusActivity != null && changeStatusActivity instanceof WorkflowChangeStatusActivity)
		{
			listStatusChange = ((WorkflowChangeStatusActivity) changeStatusActivity).getStatusChangeList();
		}
		return listStatusChange;
	}

	protected List<WorkflowPhaseChangeInfo> listPhaseChange(String processguid, String activityguid) throws ServiceRequestException
	{
		WorkflowProcess process = this.getWorkflowProcess(processguid);
		WorkflowActivity activity = process.getActivity(activityguid);
		if (activity != null && activity instanceof WorkflowChangePhaseActivity)
		{
			return ((WorkflowChangePhaseActivity) activity).getPhaseChangeList();
		}
		return null;
	}

	protected List<WorkflowActrtLifecyclePhaseInfo> listActrtPhaseChange(String processguid, String activityguid) throws ServiceRequestException
	{
		WorkflowProcess process = this.getWorkflowProcess(processguid);
		WorkflowActivity activity = process.getActivity(activityguid);
		if (activity != null && activity instanceof WorkflowChangePhaseActivity)
		{
			return ((WorkflowChangePhaseActivity) activity).getActrtLifecyclePhaseList();
		}
		return null;
	}

	protected List<WorkflowActrtStatusInfo> listStatusChang(String processguid, String activityguid)
	{
		WorkflowProcess process = this.getWorkflowProcess(processguid);
		WorkflowActivity activity = process.getActivity(activityguid);
		if (activity != null && activity instanceof WorkflowChangeStatusActivity)
		{
			return ((WorkflowChangeStatusActivity) activity).getStatusChangeList();
		}

		return null;
	}

	protected List<WorkflowPhaseChangeInfo> listPhaseChangeByName(String procName, String activityName) throws ServiceRequestException
	{
		List<WorkflowPhaseChangeInfo> listPhaseChange = null;
		WorkflowActivity changePhaseActivities = this.listActivityByApplicationType(procName, activityName, WorkflowApplicationType.CHANGE_PHASE);

		if (changePhaseActivities != null)
		{
			WorkflowChangePhaseActivity changePhaseActivity = (WorkflowChangePhaseActivity) changePhaseActivities;
			listPhaseChange = changePhaseActivity.getPhaseChangeList();
		}

		return listPhaseChange;
	}

	protected List<WorkflowActivityInfo> listSortedPerformableActivity(String procName) throws ServiceRequestException
	{
		List<WorkflowActivity> wfActivityList = this.stubService.getProcessStub().getSortGateList(procName);
		List<WorkflowActivityInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(wfActivityList))
		{
			wfActivityList.forEach(wfActivity -> {
				resultList.add(wfActivity.getWorkflowActivityInfo());
			});
		}
		return resultList;
	}

	protected List<WorkflowActivityInfo> listPerformableActivity(String procName) throws ServiceRequestException
	{
		WorkflowProcess process = this.stubService.getProcessStub().getProcess(procName);
		List<WorkflowActivity> activityList = process.getActivityList();
		if (SetUtils.isNullList(activityList))
		{
			return null;
		}

		List<WorkflowActivityInfo> resultList = new ArrayList<WorkflowActivityInfo>();
		for (WorkflowActivity activity : activityList)
		{
			switch (WorkflowActivityType.getEnum(activity.getType()))
			{
			case MANUAL:
			case NOTIFY:
				resultList.add(activity.getWorkflowActivityInfo());
				break;

			default:
				break;
			}
		}
		return resultList;
	}

	protected List<WorkflowActivity> getNextWorkflowActivityListWithoutReject(WorkflowProcess process, String activityName)
	{
		try
		{
			if (activityName != null && process != null && process.listAllTransition() != null)
			{
				List<WorkflowTransitionInfo> transitionList = process.listAllTransition();
				List<WorkflowActivity> resultList = new ArrayList<>();
				for (WorkflowTransitionInfo transition : transitionList)
				{
					if (WorkflowTransitionConditionType.REJECT == transition.getTransType())
					{
						continue;
					}
					String fromActivityGuid = transition.getActFromGuid();
					String fromActivityName = "";
					if (StringUtils.isGuid(fromActivityGuid))
					{
						WorkflowActivityInfo fromActivityInfo = this.stubService.getWorkflowActivityInfo(transition.getWorkflowGuid(), transition.getActFromGuid());
						fromActivityName = fromActivityInfo == null ? null : fromActivityInfo.getName();
					}

					if (activityName.equalsIgnoreCase(fromActivityName))
					{
						WorkflowActivity nextActivity = null;
						String toActivityGuid = transition.getActToGuid();
						if (StringUtils.isGuid(toActivityGuid))
						{
							nextActivity = this.stubService.getWfModelCacheStub().getWorkflowActivityByGuid(transition.getWorkflowGuid(), transition.getActToGuid());
						}
						if (nextActivity != null)
						{
							resultList.add(nextActivity);
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
		catch (Exception e)
		{
			throw new DynaDataSqlException("Workflow Process Modle read error", e);
		}
	}

	public WorkflowActivity listActivityByApplicationType(String processName, String activityName, WorkflowApplicationType wfApplicationType)
	{
		try
		{
			WorkflowProcess process = this.getWorkflowProcessByName(processName);
			List<WorkflowActivity> activityList = process.listActivityByType(wfApplicationType);
			if (!SetUtils.isNullList(activityList))
			{
				for (WorkflowActivity activity : activityList)
				{
					if (activity.getWorkflowActivityInfo().getName().equals(activityName))
					{
						return activity;
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataSqlException("Workflow Process Modle read error", e);
		}

		return null;
	}

}
