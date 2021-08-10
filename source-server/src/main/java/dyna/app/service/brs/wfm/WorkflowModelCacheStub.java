package dyna.app.service.brs.wfm;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.EventScript;
import dyna.common.bean.model.Implementation;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.wf.*;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.cache.AppServerCacheInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.wf.*;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataSqlException;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WorkflowModelCacheStub extends AbstractServiceStub<WFMImpl>
{

	private static final String							SCRIPT_FOLDER			= EnvUtils.getConfRootPath() + "/conf/script/";

	// 脚本名称-脚本对象
	private static final Map<String, Script>			SCRIPT_MAP				= Collections.synchronizedMap(new HashMap<>());
	// 脚本名-内容
	private static final Map<String, String>			SCRIPT_CONTENT_MAP		= Collections.synchronizedMap(new HashMap<>());
	// 流程模型名-模型
	private static final Map<String, WorkflowProcess>	PROCESS_NAME_MAP		= Collections.synchronizedMap(new HashMap<>());
	// 流程模型guid-name
	private static final Map<String, String>			GUID_NAME_PROCESS		= Collections.synchronizedMap(new HashMap<>());
	// 模型节点guid-模型name
	private static final Map<String, String>			ACTIVITY_PROCESS_MAP	= Collections.synchronizedMap(new HashMap<>());

	private AppServerCacheInfo							cacheInfo				= null;

	/**
	 * @param context
	 * @param service
	 */
	protected WorkflowModelCacheStub(ServiceContext context, WFMImpl service)
	{
		super(context, service);
		this.cacheInfo = new WorkflowModelCacheInfo();
	}

	public void loadModel()
	{
		List<WorkflowProcessInfo> prcessInfoList = DataServer.getSystemDataService().listFromCache(WorkflowProcessInfo.class, null);
		if (!SetUtils.isNullList(prcessInfoList))
		{
			prcessInfoList.forEach(processInfo -> {
				WorkflowProcess process = new WorkflowProcess();
				process.setWorkflowProcessInfo(processInfo);
				GUID_NAME_PROCESS.put(processInfo.getGuid(), processInfo.getName());
				PROCESS_NAME_MAP.put(processInfo.getName(), process);

				try
				{
					this.makeWorkflowProcess(process);
				}
				catch (ServiceRequestException e)
				{
					e.printStackTrace();
				}
			});
		}
	}

	protected WorkflowProcess getWorkflowProcessByName(String wfModelName)
	{
		return PROCESS_NAME_MAP.get(wfModelName).clone();
	}

	protected WorkflowProcess getWorkflowProcess(String wfModelGuid)
	{
		String wfname = GUID_NAME_PROCESS.get(wfModelGuid);
		return this.getWorkflowProcessByName(wfname);
	}

	protected WorkflowActivity getWorkflowActivity(String processName, String activityName)
	{
		WorkflowProcess workflowProcess = getWorkflowProcessByName(processName);
		return workflowProcess == null ? null : workflowProcess.getActivityByName(activityName);
	}

	protected WorkflowActivity getWorkflowActivityByGuid(String processGuid, String activityGuid)
	{
		WorkflowProcess workflowProcess = getWorkflowProcess(processGuid);
		return workflowProcess == null ? null : workflowProcess.getActivity(activityGuid);
	}

	protected WorkflowTransitionInfo getWorkflowTransitionInfo(String processName, String transitionName)
	{
		WorkflowProcess workflowProcess = getWorkflowProcessByName(processName);
		return workflowProcess.getTransitionByName(transitionName);
	}

	protected List<WorkflowProcess> listAllWorkflowProcess()
	{
		List<WorkflowProcess> list = new ArrayList<>();
		PROCESS_NAME_MAP.forEach((name, info) -> list.add(info.clone()));
		return list;
	}

	protected List<WorkflowActivity> listActivityByApplicationType(String processName, String activityName, WorkflowApplicationType wfApplicationType)
	{
		try
		{
			WorkflowProcess process = getWorkflowProcessByName(processName);
			return process.listActivityByType(wfApplicationType);
		}
		catch (Exception e)
		{
			throw new DynaDataSqlException("Workflow Process Modle read error", e);
		}
	}

	protected Script getScript(String scriptName)
	{
		Script script = SCRIPT_MAP.get(scriptName);
		if (script != null && script instanceof WorkflowActrtActionInfo)
		{
			return ((WorkflowActrtActionInfo) script).clone();
		}
		else if (script != null && script instanceof WorkflowEventInfo)
		{
			return ((WorkflowEventInfo) script).clone();
		}
		return null;
	}

	protected String getScriptContent(String processName, String activityName, Script script, boolean update)
	{
		File scriptFile = new File(SCRIPT_FOLDER + script.getScriptFileName());
		if (!scriptFile.exists())
		{
			return null;
		}

		String content = null;
		if (!SCRIPT_CONTENT_MAP.containsKey(script.getScriptFileName()) || script.getLastModified() != scriptFile.lastModified())
		{
			try
			{
				content = FileUtils.readFromFile(scriptFile);
				SCRIPT_CONTENT_MAP.put(script.getScriptFileName(), content);

				script.setLastModified(scriptFile.lastModified());

				if (update)
				{
					Script newScript = null;
					if (!StringUtils.isNullString(activityName))
					{
						newScript = this.getActionScript(processName, activityName, script.getName());
					}
					else if (script instanceof EventScript)
					{
						newScript = this.getEventScript(processName, script.getEventType());
					}

					if (newScript != null)
					{
						newScript.setLastModified(scriptFile.lastModified());
					}
				}
			}
			catch (Exception e)
			{
				DynaLogger.error("read script error: " + e.getMessage());
			}
		}
		else
		{
			content = SCRIPT_CONTENT_MAP.get(script.getScriptFileName());
		}

		return content;
	}

	protected Script getActionScript(String processName, String activityName, String scriptName)
	{
		WorkflowActionActivity actionActivity = this.getWorkflowActionActivity(processName, activityName);
		if (actionActivity != null)
		{
			WorkflowActrtActionInfo actionInfo = actionActivity.getAction(scriptName);
			return actionInfo == null ? null : actionInfo.clone();
		}
		return null;
	}

	protected Script getActionScript(String processName, String activityName)
	{
		WorkflowActionActivity actionActivity = this.getWorkflowActionActivity(processName, activityName);
		if (actionActivity != null)
		{
			WorkflowActrtActionInfo actionInfo = actionActivity.getAction();
			return actionInfo == null ? null : actionInfo.clone();
		}
		return null;
	}

	protected Script getEventScript(String processName, EventTypeEnum eventType)
	{
		WorkflowProcess workflowProcess = PROCESS_NAME_MAP.get(processName);
		if (workflowProcess != null)
		{
			WorkflowEventInfo event = workflowProcess.getEvent(eventType.toString());
			return event == null ? null : event.clone();
		}
		return null;
	}

	private WorkflowActionActivity getWorkflowActionActivity(String processName, String activityName)
	{
		WorkflowProcess workflowProcess = PROCESS_NAME_MAP.get(processName);
		if (workflowProcess != null)
		{
			if (!SetUtils.isNullList(workflowProcess.getActivityList()))
			{
				for (WorkflowActivity activity : workflowProcess.getActivityList())
				{
					if (activity instanceof WorkflowActionActivity && activity.getName().equalsIgnoreCase(activityName))
					{
						return (WorkflowActionActivity) activity;
					}
				}
			}
		}
		return null;
	}

	private void makeWorkflowProcess(WorkflowProcess workflowProcess) throws ServiceRequestException
	{
		workflowProcess.setActivityMap(this.listWorkflowActivity(workflowProcess));
		workflowProcess.setLifecyclePhaseList(this.listLifecyclePhase(workflowProcess));
		workflowProcess.setTransitionMap(this.listTransition(workflowProcess));
		workflowProcess.setEventList(this.makeWorkflowEventDto(workflowProcess));
	}

	private List<WorkflowLifecyclePhaseInfo> listLifecyclePhase(WorkflowProcess workflowProcess) throws ServiceRequestException
	{
		List<WorkflowLifecyclePhaseInfo> lifecyclePhaseDtoList = DataServer.getSystemDataService().listFromCache(WorkflowLifecyclePhaseInfo.class,
				new FieldValueEqualsFilter<WorkflowLifecyclePhaseInfo>(WorkflowLifecyclePhaseInfo.MAWFFK, workflowProcess.getGuid()));

		List<WorkflowLifecyclePhaseInfo> lifecyclePhaseList = new ArrayList<>();
		if (!SetUtils.isNullList(lifecyclePhaseDtoList))
		{
			for (WorkflowLifecyclePhaseInfo WorkflowLifecyclePhaseInfo : lifecyclePhaseDtoList)
			{
				this.makeWorkflowLifecyclePhaseInfo(WorkflowLifecyclePhaseInfo);
				lifecyclePhaseList.add(WorkflowLifecyclePhaseInfo);
			}
		}

		return lifecyclePhaseList;
	}

	private void makeWorkflowLifecyclePhaseInfo(WorkflowLifecyclePhaseInfo WorkflowLifecyclePhaseInfo)
	{
		String lcmasterGuid = WorkflowLifecyclePhaseInfo.getLCMasterGuid();
		LifecycleInfo lcm = DataServer.getSystemDataService().get(LifecycleInfo.class, lcmasterGuid);
		if (lcm != null)
		{
			WorkflowLifecyclePhaseInfo.setLifecycleName(lcm.getName());
		}
		String lcPhaseGuid = WorkflowLifecyclePhaseInfo.getLCPhaseGuid();
		LifecyclePhaseInfo phase = DataServer.getSystemDataService().get(LifecyclePhaseInfo.class, lcPhaseGuid);
		if (phase != null)
		{
			WorkflowLifecyclePhaseInfo.setPhaseName(phase.getName());
		}
	}

	private Map<String, WorkflowTransitionInfo> listTransition(WorkflowProcess workflowProcess) throws ServiceRequestException
	{
		Map<String, WorkflowTransitionInfo> allTransition = new HashMap<>();

		List<WorkflowTransitionInfo> queryForDtoList = DataServer.getSystemDataService().listFromCache(WorkflowTransitionInfo.class,
				new FieldValueEqualsFilter<>(WorkflowTransitionInfo.WFFK, workflowProcess.getGuid()));
		if (!SetUtils.isNullList(queryForDtoList))
		{
			for (WorkflowTransitionInfo workflowTransitionInfo : queryForDtoList)
			{
				makeWFTransitionInfo(workflowTransitionInfo);
				allTransition.put(workflowTransitionInfo.getName(), workflowTransitionInfo);
			}
		}

		return allTransition;
	}

	private void makeWFTransitionInfo(WorkflowTransitionInfo workflowTransitionInfo)
	{
		String processName = GUID_NAME_PROCESS.get(workflowTransitionInfo.getWorkflowGuid());
		WorkflowProcess process = PROCESS_NAME_MAP.get(processName);

		WorkflowActivity fromActivity = process.getActivity(workflowTransitionInfo.getActFromGuid());
		WorkflowActivity toActivity = process.getActivity(workflowTransitionInfo.getActToGuid());
		if (fromActivity != null)
		{
			workflowTransitionInfo.setActrtFromName(fromActivity.getWorkflowActivityInfo().getName());
		}
		if (toActivity != null)
		{
			workflowTransitionInfo.setActrtToName(toActivity.getWorkflowActivityInfo().getName());
		}
	}

	private Map<String, WorkflowActivity> listWorkflowActivity(WorkflowProcess workflowProcess) throws ServiceRequestException
	{
		Map<String, WorkflowActivity> workflowActivityMap = new HashMap<String, WorkflowActivity>();
		Map<String, String> guid_name_map = new HashMap<String, String>();
		List<WorkflowActivityInfo> workflowActivityDtoList = DataServer.getSystemDataService().listFromCache(WorkflowActivityInfo.class,
				new FieldValueEqualsFilter<>(WorkflowActivityInfo.MAWFFK, workflowProcess.getGuid()));
		if (!SetUtils.isNullList(workflowActivityDtoList))
		{
			for (WorkflowActivityInfo activityDto : workflowActivityDtoList)
			{
				WorkflowActivity workflowActivity = this.makeWorkflowActivity(workflowProcess, activityDto);
				workflowActivityMap.put(workflowActivity.getName(), workflowActivity);
				guid_name_map.put(workflowActivity.getGuid(), workflowActivity.getName());
				ACTIVITY_PROCESS_MAP.put(activityDto.getGuid(), workflowProcess.getWorkflowProcessInfo().getName());
			}
		}
		if (workflowProcess.getActivity_guid_name() == null)
		{
			workflowProcess.setActivity_guid_name(new HashMap<String, String>());
		}
		workflowProcess.getActivity_guid_name().putAll(guid_name_map);

		return workflowActivityMap;
	}

	private WorkflowActivity makeWorkflowActivity(WorkflowProcess workflowProcess, WorkflowActivityInfo activity) throws ServiceRequestException
	{
		WorkflowActivity active = null;
		WorkflowActivityType activeType = activity.getType() == null ? null : WorkflowActivityType.getEnum(activity.getType());
		WorkflowApplicationType applicationType = null;

		if (activeType == null || activeType == WorkflowActivityType.NOTIFY)
		{
			activeType = WorkflowActivityType.APPLICATION;
			applicationType = activity.getType() == null ? null : WorkflowApplicationType.getEnum(activity.getType());
			if (applicationType == null)
			{
				applicationType = WorkflowApplicationType.CHANGE_PHASE;
			}
		}

		Implementation implementation = null;

		switch (activeType)
		{
		case BEGIN:
			active = new WorkflowBeginActivity();
			break;
		case END:
			active = new WorkflowEndActivity();
			break;
		case MANUAL:
			WorkflowManualActivity manualActivity = new WorkflowManualActivity();
			implementation = new Implementation(activeType.toString());
			manualActivity.setProcessMode(activity.getProcessMode());
			active = manualActivity;
			break;
		case ROUTE:
			WorkflowRouteActivity workflowRouteActivity = new WorkflowRouteActivity();

			workflowRouteActivity.setRouteType(activity.getSubType() == null ? null : WorkflowRouteType.valueOf(activity.getSubType().toUpperCase()));
			workflowRouteActivity.setRouteMode(activity.getRouteModel() == null ? null : WorkflowRouteModeType.valueOf(activity.getRouteModel().toUpperCase()));
			workflowRouteActivity.setTransitionRefList(this.makeWorkflowRouteTransition(activity));
			active = workflowRouteActivity;
			break;
		case SUB_PROCESS:
			WorkflowSubProcessActivity subProcessActivity = new WorkflowSubProcessActivity();
			subProcessActivity.getWorkflowActivityInfo().setStartMax(activity.getStartMax());
			subProcessActivity.getWorkflowActivityInfo().setStartMin(activity.getStartMin());
			subProcessActivity.getWorkflowActivityInfo().setSubType(activity.getSubType());
			subProcessActivity.setSubProcessName(activity.getSubWFName());

			implementation = new Implementation(activeType.toString());

			active = subProcessActivity;
			break;
		case NOTIFY:
		case APPLICATION:

			if (applicationType == null)
			{
				applicationType = WorkflowApplicationType.CHANGE_PHASE;
			}
			implementation = new Implementation(activeType.toString(), applicationType.toString());
			switch (applicationType)
			{
			case LOCK:
				active = new WorkflowLockActivity();
				active.getWorkflowActivityInfo().setImplementation(new Implementation(activeType.toString(), applicationType.toString()));
				break;
			case UNLOCK:
				active = new WorkflowUnlockActivity();
				break;
			case NOTIFY:
				active = new WorkflowNotifyActivity();
				active.getWorkflowActivityInfo().setImplementation(new Implementation(activeType.toString(), applicationType.toString()));
				break;
			case ACTION:
				WorkflowActionActivity actionActive = new WorkflowActionActivity();
				actionActive.getWorkflowActivityInfo().setImplementation(new Implementation(activeType.toString(), applicationType.toString()));
				actionActive.setActionList(this.makeWorkflowActivityAction(activity, workflowProcess.getWorkflowProcessInfo().getName()));
				active = actionActive;
				break;
			case CHANGE_PHASE:
				WorkflowChangePhaseActivity changeActive = new WorkflowChangePhaseActivity();

				List<WorkflowActrtLifecyclePhaseInfo> activityLifecyclePhaseList = DataServer.getSystemDataService().listFromCache(WorkflowActrtLifecyclePhaseInfo.class,
						new FieldValueEqualsFilter<>(WorkflowActrtLifecyclePhaseInfo.MAWFACTFK, activity.getGuid()));

				changeActive.setActrtLifecyclePhaseList(activityLifecyclePhaseList);
				if (!SetUtils.isNullList(activityLifecyclePhaseList))
				{
					for (WorkflowActrtLifecyclePhaseInfo actrtLfcPhase : activityLifecyclePhaseList)
					{
						changeActive.getPhaseChangeList().add(this.makeWorkflowPhaseChangeDto(actrtLfcPhase));
					}
				}

				active = changeActive;
				break;
			case CHANGE_STATUS:
				WorkflowChangeStatusActivity statusChangeActive = new WorkflowChangeStatusActivity();

				statusChangeActive.setStatusChangeList(this.makeWorkflowStatusChangeDto(activity));
				active = statusChangeActive;
				break;
			default:
				break;
			}

			break;
		default:
			break;
		}

		active.getWorkflowActivityInfo().putAll(activity);
		active.getWorkflowActivityInfo().setDescription(activity.getDescription());
		active.getWorkflowActivityInfo().setGate(activity.getGate());
		active.setName(activity.getName());
		active.getWorkflowActivityInfo().setPosition(activity.getPosition());
		active.getWorkflowActivityInfo().setTitle(activity.getTitle());

		active.getWorkflowActivityInfo().setImplementation(implementation);

		workflowProcess.addActivityByType(active, applicationType);

		return active;
	}

	private List<WorkflowActrtActionInfo> makeWorkflowActivityAction(WorkflowActivityInfo activity, String processName) throws ServiceRequestException
	{
		List<WorkflowActrtActionInfo> workflowActionList = new ArrayList<>();

		UpperKeyMap filterMap = new UpperKeyMap();
		filterMap.put("ACTFK", activity.getGuid());
		List<WorkflowActrtActionInfo> actionList = DataServer.getSystemDataService().listFromCache(WorkflowActrtActionInfo.class, new FieldValueEqualsFilter<>(filterMap));
		if (actionList != null)
		{
			actionList = actionList.stream().filter(action -> action.getScriptType() == ScriptTypeEnum.WFACTACTION).collect(Collectors.toList());
		}
		Map<String, WorkflowActrtActionInfo> workflowActionMap = new HashMap<>();
		List<WorkflowActrtActionInfo> classScriptList = new ArrayList<>();
		if (actionList != null)
		{
			for (WorkflowActrtActionInfo action : actionList)
			{
				action.setBuiltin(false);
				action.setClassName(processName + "_" + activity.getName());

				workflowActionMap.put(action.getGuid(), action);
				classScriptList.add(action);
			}
		}

		for (WorkflowActrtActionInfo action : classScriptList)
		{
			if (StringUtils.isNullString(action.getParentGuid()))
			{
				workflowActionList.add(action);
			}
			else
			{
				WorkflowActrtActionInfo parentClassAction = workflowActionMap.get(action.getParentGuid());
				if (parentClassAction != null)
				{
					parentClassAction.addScript(action);
				}
			}
		}

		for (WorkflowActrtActionInfo action : classScriptList)
		{
			SCRIPT_MAP.put(action.getSequenceFullName(), action.clone());
		}
		return workflowActionList;
	}

	private List<String> makeWorkflowRouteTransition(WorkflowActivityInfo activity) throws ServiceRequestException
	{
		List<String> routeTransitionNameList = new ArrayList<>();
		List<WorkflowTransitionInfo> routeTransitionList = DataServer.getSystemDataService().listFromCache(WorkflowTransitionInfo.class,
				new FieldValueEqualsFilter<WorkflowTransitionInfo>(WorkflowTransitionInfo.ACTFROMGUID, activity.getGuid()));
		if (routeTransitionList != null)
		{
			for (WorkflowTransitionInfo routeTrans : routeTransitionList)
			{
				routeTransitionNameList.add(routeTrans.getName());
			}
		}
		return routeTransitionNameList;
	}

	private WorkflowPhaseChangeInfo makeWorkflowPhaseChangeDto(WorkflowActrtLifecyclePhaseInfo actrtLfcPhase) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		WorkflowPhaseChangeInfo WorkflowPhaseChangeDto = new WorkflowPhaseChangeInfo();
		String lcmasterGuid = actrtLfcPhase.getLCMasterGuid();
		LifecycleInfo lcm = sds.get(LifecycleInfo.class, lcmasterGuid);
		if (lcm != null)
		{
			WorkflowPhaseChangeDto.setLifecycle(lcm.getName());
		}
		String fromLcPhaseGuid = actrtLfcPhase.getFromLCPhaseGuid();
		LifecyclePhaseInfo phaseFrom = sds.get(LifecyclePhaseInfo.class, fromLcPhaseGuid);
		if (phaseFrom != null)
		{
			WorkflowPhaseChangeDto.setFromPhase(phaseFrom.getName());
		}
		String toLcPhaseGuid = actrtLfcPhase.getToLCPhaseGuid();
		LifecyclePhaseInfo phaseTo = sds.get(LifecyclePhaseInfo.class, toLcPhaseGuid);
		if (phaseTo != null)
		{
			WorkflowPhaseChangeDto.setToPhase(phaseTo.getName());
		}

		return WorkflowPhaseChangeDto;
	}

	private List<WorkflowActrtStatusInfo> makeWorkflowStatusChangeDto(WorkflowActivityInfo activity) throws ServiceRequestException
	{
		List<WorkflowActrtStatusInfo> queryForList = DataServer.getSystemDataService().listFromCache(WorkflowActrtStatusInfo.class,
				new FieldValueEqualsFilter<>(WorkflowActrtStatusInfo.MAWFACTFK, activity.getGuid()));

		return queryForList;
	}

	private List<WorkflowEventInfo> makeWorkflowEventDto(WorkflowProcess workflowProcess) throws ServiceRequestException
	{
		List<WorkflowEventInfo> workflowActionList = new ArrayList<>();

		UpperKeyMap param = new UpperKeyMap();
		param.put("WFFK", workflowProcess.getGuid());
		param.put("TYPE", "3");
		List<WorkflowEventInfo> WorkflowEventDtoList = DataServer.getSystemDataService().listFromCache(WorkflowEventInfo.class,
				new FieldValueEqualsFilter<WorkflowEventInfo>(param));
		if (WorkflowEventDtoList != null)
		{
			WorkflowEventDtoList = WorkflowEventDtoList.stream().filter(event -> event.getScriptType() == ScriptTypeEnum.WFEVENT).collect(Collectors.toList());
		}

		Map<String, WorkflowEventInfo> workflowActionMap = new HashMap<>();
		List<WorkflowEventInfo> classScriptList = new ArrayList<>();
		if (WorkflowEventDtoList != null)
		{
			for (WorkflowEventInfo event : WorkflowEventDtoList)
			{
				workflowActionMap.put(event.getGuid(), event);
				classScriptList.add(event);
			}
		}

		for (WorkflowEventInfo action : classScriptList)
		{
			if (StringUtils.isNullString(action.getParentGuid()))
			{
				workflowActionList.add(action);
			}
			else
			{
				WorkflowEventInfo parentClassAction = workflowActionMap.get(action.getParentGuid());
				if (parentClassAction != null)
				{
					parentClassAction.addScript(action);
				}
			}
		}

		for (WorkflowEventInfo action : classScriptList)
		{
			SCRIPT_MAP.put(action.getSequenceFullName(), action.clone());
		}
		return workflowActionList;
	}

	class WorkflowModelCacheInfo extends AppServerCacheInfo
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5569238942718054241L;

		@Override
		public void addToCache(Object data) throws ServiceRequestException
		{

			if (data instanceof WorkflowProcessInfo)
			{
				WorkflowProcessInfo info = (WorkflowProcessInfo) data;
				WorkflowProcess process = new WorkflowProcess(info);
				PROCESS_NAME_MAP.put(info.getName(), process);
				GUID_NAME_PROCESS.put(info.getGuid(), info.getName());
			}
			else if (data instanceof WorkflowActivityInfo)
			{
				WorkflowActivityInfo info = (WorkflowActivityInfo) data;
				String processGuid = info.getWorkflowGuid();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				PROCESS_NAME_MAP.get(processName).addActivity(makeWorkflowActivity(PROCESS_NAME_MAP.get(processName), info));
				ACTIVITY_PROCESS_MAP.put(info.getGuid(), processName);

			}
			else if (data instanceof WorkflowLifecyclePhaseInfo)
			{
				WorkflowLifecyclePhaseInfo info = (WorkflowLifecyclePhaseInfo) data;
				String processGuid = info.getMAWFfk();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				makeWorkflowLifecyclePhaseInfo(info);
				PROCESS_NAME_MAP.get(processName).getLifecyclePhaseList().add(info);
			}
			else if (data instanceof WorkflowTransitionInfo)
			{
				WorkflowTransitionInfo info = (WorkflowTransitionInfo) data;
				makeWFTransitionInfo(info);
				String processGuid = info.getWorkflowGuid();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				PROCESS_NAME_MAP.get(processName).getAllTransition().put(info.getName(), info);
				PROCESS_NAME_MAP.get(processName).getTransition_guid_name().put(info.getGuid(), info.getName());
			}
			else if (data instanceof WorkflowEventInfo)
			{
				WorkflowEventInfo info = (WorkflowEventInfo) data;
				String processGuid = info.getWffk();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				String parentGuid = info.getParentGuid();
				if (StringUtils.isGuid(parentGuid))
				{
					List<WorkflowEventInfo> eventList = PROCESS_NAME_MAP.get(processName).getEventList();
					if (!SetUtils.isNullList(eventList))
					{
						for (WorkflowEventInfo event : eventList)
						{
							if (event.getGuid().equals(parentGuid))
							{
								event.addScript(info);
								break;
							}
						}
					}
				}
				else
				{
					PROCESS_NAME_MAP.get(processName).addEvent(info);
				}

			}
			else if (data instanceof WorkflowActrtLifecyclePhaseInfo)
			{
				// WorkflowActrtLifecyclePhaseInfo info = (WorkflowActrtLifecyclePhaseInfo) data;
				// String actrtGuid = info.getMAWFActfk();
				// String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				// WorkflowChangePhaseActivity activity = (WorkflowChangePhaseActivity)
				// PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid);
				// activity.getActrtLifecyclePhaseList().add(info);
				// activity.getPhaseChangeList().add(makeWorkflowPhaseChangeDto(info));

			}
			else if (data instanceof WorkflowActrtStatusInfo)
			{
				// WorkflowActrtStatusInfo info = (WorkflowActrtStatusInfo) data;
				// String actrtGuid = info.getMAWFActfk();
				// String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				// ((WorkflowChangeStatusActivity)
				// PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid)).getStatusChangeList().add(info);
			}
			else if (data instanceof WorkflowActrtActionInfo)
			{
				// WorkflowActrtActionInfo info = (WorkflowActrtActionInfo) data;
				// String actrtGuid = info.getWfActivityGuid();
				// String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				// String parentGuid = info.getParentGuid();
				// if (StringUtils.isGuid(parentGuid))
				// {
				// List<WorkflowActrtActionInfo> eventList = ((WorkflowActionActivity)
				// PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid)).getActionList();
				// if (!SetUtils.isNullList(eventList))
				// {
				// for (WorkflowActrtActionInfo event : eventList)
				// {
				// if (event.getGuid().equals(parentGuid))
				// {
				// event.addScript(info);
				// break;
				// }
				// }
				// }
				// }
				// else
				// {
				// ((WorkflowActionActivity)
				// PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid)).getActionList().add(info);
				// }
			}
		}

		@Override
		public void removeFromCache(Object data) throws ServiceRequestException
		{
			if (data instanceof WorkflowProcessInfo)
			{
				WorkflowProcessInfo info = (WorkflowProcessInfo) data;
				PROCESS_NAME_MAP.remove(info.getName());
				GUID_NAME_PROCESS.remove(info.getGuid());
			}
			else if (data instanceof WorkflowActivityInfo)
			{
				WorkflowActivityInfo info = (WorkflowActivityInfo) data;
				String processGuid = info.getWorkflowGuid();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				PROCESS_NAME_MAP.get(processName).getActivityMap().remove(info.getName());
				PROCESS_NAME_MAP.get(processName).getActivity_guid_name().remove(info.getGuid());
				ACTIVITY_PROCESS_MAP.remove(info.getGuid(), processName);

				WorkflowApplicationType applicationType = info.getType() == null ? null : WorkflowApplicationType.getEnum(info.getType());
				if (applicationType != null)
				{
					List<WorkflowActivity> appActivityList = PROCESS_NAME_MAP.get(processName).getAppActivityMap().get(applicationType);
					Iterator<WorkflowActivity> it = appActivityList.iterator();
					while (it.hasNext())
					{
						WorkflowActivity activity = it.next();
						{
							if (activity.getGuid().equals(info.getGuid()))
							{
								it.remove();
								// break;
							}
						}
					}
				}
			}
			else if (data instanceof WorkflowLifecyclePhaseInfo)
			{
				WorkflowLifecyclePhaseInfo info = (WorkflowLifecyclePhaseInfo) data;
				String processGuid = info.getMAWFfk();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				makeWorkflowLifecyclePhaseInfo(info);
				PROCESS_NAME_MAP.get(processName).getLifecyclePhaseList().remove(info);
			}
			else if (data instanceof WorkflowTransitionInfo)
			{
				WorkflowTransitionInfo info = (WorkflowTransitionInfo) data;
				String processGuid = info.getWorkflowGuid();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				PROCESS_NAME_MAP.get(processName).getAllTransition().remove(info.getName());
				PROCESS_NAME_MAP.get(processName).getTransition_guid_name().remove(info.getGuid());

			}
			else if (data instanceof WorkflowEventInfo)
			{
				WorkflowEventInfo info = (WorkflowEventInfo) data;
				String processGuid = info.getWffk();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				String parentGuid = info.getParentGuid();
				if (StringUtils.isGuid(parentGuid))
				{
					List<WorkflowEventInfo> eventList = PROCESS_NAME_MAP.get(processName).getEventList();
					if (!SetUtils.isNullList(eventList))
					{
						for (WorkflowEventInfo event : eventList)
						{
							if (event.getGuid().equals(parentGuid))
							{
								event.removeChild(info);
								break;
							}
						}
					}
				}
				else
				{
					PROCESS_NAME_MAP.get(processName).removeEvent(info);
				}
			}
			else if (data instanceof WorkflowActrtLifecyclePhaseInfo)
			{
				WorkflowActrtLifecyclePhaseInfo info = (WorkflowActrtLifecyclePhaseInfo) data;
				String actrtGuid = info.getMAWFActfk();
				String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				((WorkflowChangePhaseActivity) PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid)).getActrtLifecyclePhaseList().remove(info);
				((WorkflowChangePhaseActivity) PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid)).getPhaseChangeList().remove(makeWorkflowPhaseChangeDto(info));
			}
			else if (data instanceof WorkflowActrtStatusInfo)
			{
				WorkflowActrtStatusInfo info = (WorkflowActrtStatusInfo) data;
				String actrtGuid = info.getMAWFActfk();
				String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				((WorkflowChangeStatusActivity) PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid)).getStatusChangeList().remove(info);

			}
			else if (data instanceof WorkflowActrtActionInfo)
			{
				WorkflowActrtActionInfo info = (WorkflowActrtActionInfo) data;
				String actrtGuid = info.getWfActivityGuid();
				String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				String parentGuid = info.getParentGuid();
				WorkflowActionActivity activity = (WorkflowActionActivity) PROCESS_NAME_MAP.get(processName).getActivity(actrtGuid);
				if (StringUtils.isGuid(parentGuid))
				{
					List<WorkflowActrtActionInfo> actionList = activity.getActionList();
					if (!SetUtils.isNullList(actionList))
					{
						Iterator<WorkflowActrtActionInfo> it = actionList.iterator();
						while (it.hasNext())
						{
							WorkflowActrtActionInfo parent = it.next();
							if (parent.getGuid().equals(parentGuid))
							{
								parent.removeAction(info);
								break;
							}
						}
					}
				}
				else
				{
					activity.removeAction(info);
				}
			}
		}

		@Override
		public void updateToCache(Object data) throws ServiceRequestException
		{
			if (data instanceof WorkflowProcessInfo)
			{
				WorkflowProcessInfo info = (WorkflowProcessInfo) data;
				PROCESS_NAME_MAP.get(info.getName()).getWorkflowProcessInfo().putAll(info);
			}
			else if (data instanceof WorkflowActivityInfo)
			{
				WorkflowActivityInfo info = (WorkflowActivityInfo) data;
				String processGuid = info.getWorkflowGuid();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				PROCESS_NAME_MAP.get(processName).getActivity(info.getGuid()).getWorkflowActivityInfo().putAll(info);

				WorkflowApplicationType applicationType = info.getType() == null ? null : WorkflowApplicationType.getEnum(info.getType());
				if (applicationType != null)
				{
					List<WorkflowActivity> appActivityList = PROCESS_NAME_MAP.get(processName).getAppActivityMap().get(applicationType);
					for (WorkflowActivity activity : appActivityList)
					{
						if (activity.getGuid().equals(info.getGuid()))
						{
							activity.putAll(info);
							break;
						}
					}
				}
			}
			else if (data instanceof WorkflowLifecyclePhaseInfo)
			{
				WorkflowLifecyclePhaseInfo info = (WorkflowLifecyclePhaseInfo) data;
				String processGuid = info.getMAWFfk();
				makeWorkflowLifecyclePhaseInfo(info);
				String processName = GUID_NAME_PROCESS.get(processGuid);
				List<WorkflowLifecyclePhaseInfo> phaseList = PROCESS_NAME_MAP.get(processName).getLifecyclePhaseList();
				for (WorkflowLifecyclePhaseInfo phaseInfo : phaseList)
				{
					if (phaseInfo.getGuid().equals(info.getGuid()))
					{
						phaseInfo.putAll(info);
						phaseInfo.setLifecycleName(info.getLifecycleName());
						phaseInfo.setPhaseName(info.getPhaseName());
						break;
					}
				}
			}
			else if (data instanceof WorkflowTransitionInfo)
			{
				WorkflowTransitionInfo info = (WorkflowTransitionInfo) data;
				makeWFTransitionInfo(info);
				String processGuid = info.getWorkflowGuid();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				PROCESS_NAME_MAP.get(processName).getAllTransition().get(info.getName()).putAll(info);
			}
			else if (data instanceof WorkflowEventInfo)
			{
				WorkflowEventInfo info = (WorkflowEventInfo) data;
				String processGuid = info.getWffk();
				String processName = GUID_NAME_PROCESS.get(processGuid);
				List<WorkflowEventInfo> eventList = PROCESS_NAME_MAP.get(processName).getEventList();
				if (!SetUtils.isNullList(eventList))
				{
					for (WorkflowEventInfo eventInfo : eventList)
					{
						if (eventInfo.getGuid().equals(info.getGuid()))
						{
							eventInfo.putAll(info);
							break;
						}
					}
				}
			}
			else if (data instanceof WorkflowActrtLifecyclePhaseInfo)
			{
				WorkflowActrtLifecyclePhaseInfo info = (WorkflowActrtLifecyclePhaseInfo) data;
				String actrtGuid = info.getMAWFActfk();
				String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				List<WorkflowActrtLifecyclePhaseInfo> phaseInfoList = ((WorkflowChangePhaseActivity) PROCESS_NAME_MAP.get(processName).getActivityMap().get(info.getName()))
						.getActrtLifecyclePhaseList();
				for (WorkflowActrtLifecyclePhaseInfo phaseInfo : phaseInfoList)
				{
					if (phaseInfo.getGuid().equals(info.getGuid()))
					{
						phaseInfo.putAll(info);
						break;
					}
				}

			}
			else if (data instanceof WorkflowActrtStatusInfo)
			{
				WorkflowActrtStatusInfo info = (WorkflowActrtStatusInfo) data;
				String actrtGuid = info.getMAWFActfk();
				String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				List<WorkflowActrtStatusInfo> statusList = ((WorkflowChangeStatusActivity) PROCESS_NAME_MAP.get(processName).getActivityMap().get(info.getName()))
						.getStatusChangeList();
				for (WorkflowActrtStatusInfo statusInfo : statusList)
				{
					if (statusInfo.getGuid().equals(info.getGuid()))
					{
						statusInfo.putAll(info);
						break;
					}
				}
			}
			else if (data instanceof WorkflowActrtActionInfo)
			{
				WorkflowActrtActionInfo info = (WorkflowActrtActionInfo) data;
				String actrtGuid = info.getWfActivityGuid();
				String processName = ACTIVITY_PROCESS_MAP.get(actrtGuid);
				List<WorkflowActrtActionInfo> actionInfoList = ((WorkflowActionActivity) PROCESS_NAME_MAP.get(processName).getActivityMap().get(info.getName())).getActionList();
				for (WorkflowActrtActionInfo actionInfo : actionInfoList)
				{
					if (actionInfo.getGuid().equals(info.getGuid()))
					{
						actionInfo.putAll(info);
						break;
					}
				}
			}
		}
	}

	public AppServerCacheInfo getWorkflowModelCacheInfo()
	{
		return this.cacheInfo;
	}
}
