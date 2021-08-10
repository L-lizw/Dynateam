/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 活动操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.activity;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ActivityRuntimeAppFactory;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.input.InputObjectWrokflowEventImpl;
import dyna.common.bean.extra.ProcessSetting;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.dto.aas.User;
import dyna.common.dto.template.wft.WorkflowActivityRuntimeData;
import dyna.common.dto.wf.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.text.MessageFormat;
import java.util.*;

/**
 * 活动操作分支
 * 
 * @author Wanglei
 * 
 */
public class ActivityRuntimeStub extends AbstractServiceStub<WFIImpl>
{
	private ActivityRuntimeAppFactory	applicationFactory	= null;
	private ActivityRuntimeDBStub		dbStub				= null;

	/**
	 * @param context
	 * @param service
	 */
	public ActivityRuntimeStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.applicationFactory = new ActivityRuntimeAppFactory(context, service);
		this.dbStub = new ActivityRuntimeDBStub(context, service);
	}

	public ActivityRuntime performActivityRuntime(String actRtGuid, String comment, DecisionEnum decide, String rejectToActRtGuid, String performerGuid,
			ProcessSetting processSetting, boolean isAutoblockPerform) throws ServiceRequestException
	{
		// O12096/A.3-一个执行人拒绝到开始，节点的其他执行人的通知一直为未读状态
		ActivityRuntime activity = this.dbStub.getActivityRuntime(actRtGuid);
		if (StringUtils.isNullString(performerGuid))
		{
			performerGuid = this.stubService.getOperatorGuid();
		}
		List<User> users = this.stubService.listNotFinishPerformer(actRtGuid);
		if (!SetUtils.isNullList(users) && users.size() > 1 && !isAutoblockPerform)
		{
			WorkflowTemplateAct workflowTemplateAct = this.stubService.getActivityRuntimeStub().getWorkflowTemplateActSingle(activity.getGuid());
			// 节点为OR时
			if (workflowTemplateAct.getWorkflowTemplateActInfo() != null && workflowTemplateAct.getWorkflowTemplateActInfo().getExecutionType().equals("0"))
			{
				// 拒绝
				if (decide.equals(DecisionEnum.REJECT))
				{
					List<String> tempList = new ArrayList<String>();
					LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
					User operatorUser = this.stubService.getAAS().getUser(performerGuid);
					for (User user : users)
					{
						if (!user.getGuid().equals(performerGuid))
						{
							DecisionEnum decide1 = DecisionEnum.REJECT;
							String procRtGuid = activity.getProcessRuntimeGuid();
							String content = this.stubService.getMSRM().getMSRString("ID_WF_ATTACH_ALREAD_DEAL", languageEnum.toString());
							content = MessageFormat.format(content, operatorUser.getUserName());
							this.doTrack(procRtGuid, actRtGuid, user.getGuid(), decide1, content, activity.getStartNumber(), tempList);
						}
					}
				}
			}
		}
		return this.doPerformActivityRuntime(actRtGuid, comment, decide, rejectToActRtGuid, performerGuid, processSetting, isAutoblockPerform);
	}

	protected synchronized ActivityRuntime doPerformActivityRuntime(String actRtGuid, String comment, DecisionEnum decide, String rejectToActRtGuid, String performerGuid,
			ProcessSetting processSetting, boolean isAutoblockPerform) throws ServiceRequestException
	{
		ActivityRuntime activity = this.dbStub.getActivityRuntime(actRtGuid);
		if (activity == null)
		{
			throw new ServiceRequestException("ID_APP_WF_GETACTIVITY_ERROR", "activity is null");
		}

		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());
		if (processRuntime != null && (processRuntime.getStatus() == ProcessStatusEnum.CLOSED || processRuntime.getStatus() == ProcessStatusEnum.OBSOLETE
				|| processRuntime.getStatus() == ProcessStatusEnum.CANCEL))
		{
			throw new ServiceRequestException("ID_APP_WF_NOT_RUNNING", "process is not running");
		}

		String procRtGuid = activity.getProcessRuntimeGuid();

		ActRuntimeModeEnum actMode = activity.getActMode();
		WorkflowActivityType actType = activity.getActType();

		// finished or bypass activity should be ignored.
		if (actMode == ActRuntimeModeEnum.BYPASS || actMode == ActRuntimeModeEnum.FINISH || actType != WorkflowActivityType.BEGIN && actMode == ActRuntimeModeEnum.NORMAL)
		{
			throw new ServiceRequestException("ID_APP_WF_ACTIVITYISFINISHORBYPASS", "invalid activity mode, expected: " + ActRuntimeModeEnum.CURRENT);
		}

		// DCR规则检查
		List<ProcAttach> attachList = this.stubService.getAttachStub().listProcAttach(procRtGuid);
		this.stubService.getDCR().check(procRtGuid, processRuntime.getWFTemplateName(), activity.getName(), attachList);

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			if (StringUtils.isNullString(performerGuid))
			{
				performerGuid = this.stubService.getOperatorGuid();
			}

			this.stubService.getPerformerStub().savePerformerAndDeadLine(procRtGuid, processSetting);

			this.stubService.getPerformerStub().checkPerform(activity, rejectToActRtGuid, false);

			this.checkCanPerform(activity, performerGuid, isAutoblockPerform);

			if (actType == WorkflowActivityType.BEGIN)
			{
				InputObjectWrokflowEventImpl inputObject = new InputObjectWrokflowEventImpl(procRtGuid, processRuntime.getName());
				this.stubService.getEOSS().executeWorkflowStartBeforeEvent(inputObject);
			}
			// do track comment

			List<String> tempList = new ArrayList<String>();
			this.doTrack(procRtGuid, actRtGuid, performerGuid, decide, comment, activity.getStartNumber(), tempList);

			boolean isExecute = this.applicationFactory.getActivityApplication(activity.getActType()).performActivityRuntime(activity, decide);

			if (isExecute || decide == null)
			{
				// finish activity
				if (decide == null)
				{
					decide = DecisionEnum.ACCEPT;
				}
				this.finishActivity(activity, decide);

				if (actType == WorkflowActivityType.BEGIN)
				{
					InputObjectWrokflowEventImpl inputObject = new InputObjectWrokflowEventImpl(procRtGuid, processRuntime.getName());
					this.stubService.getEOSS().executeWorkflowStartAfterEvent(inputObject);
				}

				// process next activities
				if (DecisionEnum.ACCEPT == decide || DecisionEnum.SKIP == decide)
				{
					if (DecisionEnum.SKIP == decide)
					{
						this.dbStub.setAsByPassActivity(activity);
					}
					ActivityRuntime actionActivity = this.fireNextAcceptActivity(activity);
					if (actionActivity != null)
					{
//						DataServer.getTransactionManager().commitTransaction();
						return actionActivity;
					}

				}
				else if (decide == DecisionEnum.REJECT)
				{
					ActivityRuntime actionActivity = this.fireRejectActivity(activity, rejectToActRtGuid);
					if (actionActivity != null)
					{
//						DataServer.getTransactionManager().commitTransaction();
						return actionActivity;
					}

				}
			}
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
			throw ServiceRequestException.createByException("ID_APP_WF_UNKNOW_ERROR", e);
		}
		return null;
	}

	/**
	 * 1,检查子流程完成数
	 * 2,检查是否有未完成的子流程
	 * 3,检查当前活动是否已经完成
	 * 4,检查执行人
	 * 
	 * @param activity
	 * @param performerGuid
	 * @param isAutoblockPerform
	 * @throws ServiceRequestException
	 */
	private void checkCanPerform(ActivityRuntime activity, String performerGuid, boolean isAutoblockPerform) throws ServiceRequestException
	{
		// do performed
		if (activity.getActType() != WorkflowActivityType.BEGIN)
		{
			if (activity.getActType() == WorkflowActivityType.SUB_PROCESS && activity.getIsBlock() == SubProcessTypeEnum.AUTOBLOCK && !isAutoblockPerform)
			{
				if (activity.getMinSubPro() != null && activity.getMinSubPro() > 0)
				{
					throw new ServiceRequestException("ID_APP_WF_PERFORMER_ACTIVITY_AUTOBLOCK", "workflow activity is autoblock.");
				}
			}
			else if (activity.getActType() == WorkflowActivityType.SUB_PROCESS && activity.getIsBlock() == SubProcessTypeEnum.BLOCK)
			{
				int allSubFinished = this.stubService.getProcessRuntimeStub().isAllSubFinished(activity.getProcessRuntimeGuid(), activity);
				if (allSubFinished == 1)
				{
					throw new ServiceRequestException("ID_APP_WF_PERFORMER_ACTIVITY_BLOCK", "workflow activity is block.");
				}
				else if (allSubFinished == 2)
				{
					throw new ServiceRequestException("ID_APP_WF_PERFORMER_ACTIVITY_SUBWF_NOFINISHED", "sub workflow has been not finished");
				}
			}

			boolean isPerformed = this.stubService.getTrackStub().isPerformed(activity.getGuid(), performerGuid, activity.getStartNumber());
			if (isPerformed)
			{
				throw new ServiceRequestException("ID_CLIENT_WF_ACTRT_ALREADY_FINISH", "actrt already finished");
			}

			boolean isSuccess = this.stubService.getPerformerStub().performed(activity.getProcessRuntimeGuid(), activity.getGuid(), performerGuid);

			if (!isSuccess)
			{
				throw new ServiceRequestException("ID_APP_WF_PERFORMER_ERROR", "performer is error");
			}
		}
	}

	public void finishActivity(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		try
		{
			this.dbStub.updateActrtStartNumber(activity);
			WorkflowActivityType type = activity.getActType();

			this.applicationFactory.getActivityApplication(type).finishActivity(activity, decide);

			if (decide == DecisionEnum.ACCEPT)
			{
				this.stubService.getFTS().createTransformQueue4WF(activity);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public ActivityRuntime fireNextActivity(ActivityRuntime activity, DecisionEnum decision, String rejectToActRtGuid) throws ServiceRequestException
	{

		if (decision == DecisionEnum.REJECT)
		{
			return this.fireRejectActivity(activity, rejectToActRtGuid);
		}
		else
		{
			return this.fireNextAcceptActivity(activity);
		}
	}

	public ActivityRuntime fireNextAcceptActivity(ActivityRuntime activity) throws ServiceRequestException
	{
		String actRtGuid = activity.getGuid();
		ActivityRuntime returnActrt = null;

		this.dbStub.updatenNextActrtStartTime(actRtGuid);

		List<ActivityRuntime> actList = this.dbStub.listAcceptableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(actList))
		{
			return null;
		}

		WorkflowActivityType actType = null;

		for (ActivityRuntime nextActRt : actList)
		{
			actType = nextActRt.getActType();

			returnActrt = this.applicationFactory.getActivityApplication(actType).fireNextAcceptActivity(nextActRt);
		}

		return returnActrt;
	}

	/**
	 * 执行拒绝操作之后, 修改相关活动上下文
	 * 
	 * @param wfeImpl
	 * @param activity
	 * @param rejectToActRtGuid
	 * @param operatorGuid
	 * @param credential
	 * @throws ServiceRequestException
	 */
	private ActivityRuntime fireRejectActivity(ActivityRuntime activity, String rejectToActRtGuid) throws ServiceRequestException
	{
		ActivityRuntime result = null;
		List<ActivityRuntime> rejectedPath = this.listRejectedPath(activity, rejectToActRtGuid);
		for (ActivityRuntime rejAct : rejectedPath)
		{
			result = this.applicationFactory.getActivityApplication(rejAct.getActType()).fireRejectActivity(rejAct);
		}

		return result;
	}

	/**
	 * 拒绝到指定活动之后, 将该指定活动之后执行的操作重置
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	public void resetRelatedActivityRuntime(ActivityRuntime activity) throws ServiceRequestException
	{
		String actRtGuid = activity.getGuid();
		List<ActivityRuntime> actList = this.dbStub.listAcceptableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(actList))
		{
			return;
		}

		for (ActivityRuntime act : actList)
		{
			this.resetActivityRuntime(act);

			WorkflowActivityType actType = act.getActType();
			switch (actType)
			{
			case MANUAL:
			case NOTIFY:
				this.stubService.getPerformerStub().resetPerformersOfActivityRutime(act.getGuid());
				break;

			case SUB_PROCESS:
				this.stubService.getPerformerStub().resetPerformersOfActivityRutime(act.getGuid());
				this.resetSubWorkflow(act);
				break;
			default:
				break;
			}

			// nested
			this.resetRelatedActivityRuntime(act);
		}
	}

	public void resetSubWorkflow(ActivityRuntime activity) throws ServiceRequestException
	{
		List<ProcessRuntime> listSubProcess = null;

		listSubProcess = this.stubService.getProcessRuntimeStub().listSubProcessRuntime(activity.getProcessRuntimeGuid());
		if (!SetUtils.isNullList(listSubProcess))
		{
			for (ProcessRuntime processRuntime : listSubProcess)
			{
				// 废弃子流程
				this.stubService.getProcessRuntimeStub().updateProcessStatus(processRuntime.getGuid(), ProcessStatusEnum.OBSOLETE);
			}
		}

	}

	/**
	 * 重置活动为normal状态
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	public void resetActivityRuntime(ActivityRuntime activity) throws ServiceRequestException
	{
		try
		{
			if (activity.getActMode() != ActRuntimeModeEnum.BYPASS)
			{
				activity.setActMode(ActRuntimeModeEnum.NORMAL);
			}
			this.dbStub.resetActivityRuntime(activity);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void resetActivitiesInProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<ActivityRuntime> list = this.stubService.getActivityRuntimeStub().listActivityInProcessRuntime(procRtGuid);
			for (ActivityRuntime activity : list)
			{
				ActRuntimeModeEnum actMode = activity.getActMode();
				if (actMode != ActRuntimeModeEnum.BYPASS)
				{
					activity.setActMode(ActRuntimeModeEnum.NORMAL);
				}

				activity.setFinished(false);

				sds.save(activity);
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 查询从当前活动拒绝到指定人为活动之间的活动列表
	 * 
	 * @param activity
	 * @param rejectToActRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listRejectedPath(ActivityRuntime activity, String rejectToActRtGuid) throws ServiceRequestException
	{
		List<ActivityRuntime> rejectedPath = new ArrayList<ActivityRuntime>();

		String actRtGuid = activity.getGuid();
		List<ActivityRuntime> rejectedList = this.dbStub.listRejectableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(rejectedList))
		{
			return rejectedPath;
		}

		for (ActivityRuntime rejAct : rejectedList)
		{
			rejectedPath.addAll(this.lookupRejectedPath(rejAct, rejectToActRtGuid));
			if (!SetUtils.isNullList(rejectedPath))
			{
				break;
			}
		}

		return rejectedPath;
	}

	private List<ActivityRuntime> lookupRejectedPath(ActivityRuntime activity, String rejectToActRtGuid) throws ServiceRequestException
	{
		List<ActivityRuntime> rejectedPath = new ArrayList<ActivityRuntime>();
		rejectedPath.add(activity);

		String actRtGuid = activity.getGuid();
		if (rejectToActRtGuid.equals(actRtGuid))
		{
			return rejectedPath;
		}

		WorkflowActivityType type = activity.getActType();
		ActRuntimeModeEnum actMode = activity.getActMode();
		if (type == WorkflowActivityType.MANUAL && actMode != ActRuntimeModeEnum.BYPASS || type == WorkflowActivityType.BEGIN || type == WorkflowActivityType.END)
		{
			rejectedPath.clear();
			return rejectedPath;
		}

		List<ActivityRuntime> rejectedList = this.dbStub.listAcceptableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(rejectedList))
		{
			rejectedPath.clear();
			return rejectedPath;
		}

		List<ActivityRuntime> tempList = null;
		for (ActivityRuntime rejAct : rejectedList)
		{
			tempList = this.lookupRejectedPath(rejAct, rejectToActRtGuid);
			if (!SetUtils.isNullList(tempList))
			{
				rejectedPath.addAll(tempList);
				return rejectedPath;
			}
		}

		// this path is not correct
		if (!rejectedPath.isEmpty())
		{
			rejectedPath.clear();
		}

		return rejectedPath;
	}

	public List<ActivityRuntime> listAcceptedNotApplicationActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		List<ActivityRuntime> rejectActList = new ArrayList<ActivityRuntime>();

		List<ActivityRuntime> rejectedList = this.dbStub.listAcceptableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(rejectedList))
		{
			return rejectActList;
		}

		for (ActivityRuntime rejAct : rejectedList)
		{
			this.lookupAcceptedNotApplicationActivityRuntime(rejectActList, rejAct);
		}
		return rejectActList;
	}

	private void lookupAcceptedNotApplicationActivityRuntime(List<ActivityRuntime> destList, ActivityRuntime actRt) throws ServiceRequestException
	{
		String actRtGuid = actRt.getGuid();
		WorkflowActivityType actType = actRt.getActType();
		if (actType != WorkflowActivityType.APPLICATION)// && actType != WorkflowActivityType.EFFECTIVE)
		{
			destList.add(actRt);
			return;
		}

		List<ActivityRuntime> actList = this.listAcceptableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(actList))
		{
			return;
		}

		for (ActivityRuntime nextAct : actList)
		{
			this.lookupAcceptedNotApplicationActivityRuntime(destList, nextAct);
		}
	}

	public List<ActivityRuntime> listAcceptableToActivityRuntime(String actRtGuid) throws ServiceRequestException
	{

		return this.dbStub.listAcceptableToActivityRuntime(actRtGuid);
	}

	public List<ActivityRuntime> listActivityInProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			List<ActivityRuntime> list = this.dbStub.listActivityInProcessRuntime(procRtGuid);
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void setAsCurrentActivity(ActivityRuntime activity) throws ServiceRequestException
	{
		try
		{
			String actRtGuid = activity.getGuid();

			List<Performer> listPerformer = this.stubService.getPerformerStub().listPerformer(actRtGuid);
			this.stubService.getNoticeStub().sendPerformMail(listPerformer, activity, MailMessageType.WORKFLOWAPPROVED);
			this.dbStub.setAsCurrentActivity(activity);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<ActivityRuntime> listRejectedDestinedActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		List<ActivityRuntime> rejectActList = new ArrayList<ActivityRuntime>();

		List<ActivityRuntime> rejectedList = this.dbStub.listRejectableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(rejectedList))
		{
			return rejectActList;
		}

		for (ActivityRuntime rejAct : rejectedList)
		{
			this.lookupRejectedDestinedActivityRuntime(rejectActList, rejAct);
		}
		return rejectActList;
	}

	private void lookupRejectedDestinedActivityRuntime(List<ActivityRuntime> destList, ActivityRuntime actRt) throws ServiceRequestException
	{
		String actRtGuid = actRt.getGuid();
		WorkflowActivityType actType = actRt.getActType();
		ActRuntimeModeEnum actMode = actRt.getActMode();
		if (actType == WorkflowActivityType.BEGIN || actType == WorkflowActivityType.END || actType == WorkflowActivityType.MANUAL || actType == WorkflowActivityType.SUB_PROCESS)
		{
			if (actMode != ActRuntimeModeEnum.BYPASS)
			{
				destList.add(actRt);
			}
			return;
		}

		List<ActivityRuntime> actList = this.listAcceptableFromActivityRuntime(actRtGuid);
		if (SetUtils.isNullList(actList))
		{
			return;
		}

		for (ActivityRuntime nextAct : actList)
		{
			this.lookupRejectedDestinedActivityRuntime(destList, nextAct);
		}
	}

	public WorkflowTemplateAct getWorkflowTemplateActSingle(String actrtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		WorkflowTemplateAct workflowTemplateAct = null;
		try
		{
			ActivityRuntime actrt = this.dbStub.getActivityRuntime(actrtGuid);
			if (actrt != null)
			{
				Map<String, Object> filter = new HashMap<String, Object>();
				filter.put("GUID", actrt.getProcessRuntimeGuid());
				ProcessRuntime procrt = sds.queryObject(ProcessRuntime.class, filter);
				if (procrt != null)
				{
					workflowTemplateAct = this.stubService.getTemplateStub().getWorkflowTemplateActSetInfoSingle(procrt.getWFTemplateGuid(), actrt.getActrtName());
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return workflowTemplateAct == null ? new WorkflowTemplateAct() : workflowTemplateAct;
	}

	protected void computeDateLine(ActivityRuntime currentActrt, Date preTime, String wfTemplateGuid) throws ServiceRequestException
	{
		Date dateLine = null;
		if (preTime != null)
		{
			WorkflowTemplateAct workflowTemplateActSingle = this.stubService.getTemplateStub().getWorkflowTemplateActSetInfoSingle(wfTemplateGuid, currentActrt.getName());
			// WorkflowTemplateAct workflowTemplateActSingle =
			// this.getWorkflowTemplateActSingle(currentActrt.getGuid());

			if (workflowTemplateActSingle != null && workflowTemplateActSingle.getWorkflowTemplateActInfo() != null)
			{
				if (!"0".equals(workflowTemplateActSingle.getWorkflowTemplateActInfo().getSchemePeriods()))
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(preTime);
					calendar.add(Calendar.DATE, Integer.valueOf(workflowTemplateActSingle.getWorkflowTemplateActInfo().getSchemePeriods()));

					dateLine = calendar.getTime();
				}
				currentActrt.setDeadline(dateLine);
			}
		}
	}

	public List<ActivityRuntime> listHistoryActivityRuntimeAndPerformer(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			List<ActivityRuntime> listSortedPerformableActivityRuntime = this.stubService.listSortedPerformableActivityRuntime(procRtGuid);
			List<ProcTrack> listComment = this.stubService.listComment(procRtGuid);
			Map<String, ActivityRuntime> actMap = new HashMap<String, ActivityRuntime>();
			List<ActivityRuntime> historyActrt = new ArrayList<ActivityRuntime>();
			List<int[]> trackPathList = new ArrayList<int[]>();
			List<List<ProcTrack>> trackList = new ArrayList<List<ProcTrack>>();
			int cStartgate = -1;
			int finishgate = -1;
			if (!SetUtils.isNullList(listSortedPerformableActivityRuntime))
			{
				int no = 1;
				cStartgate = listSortedPerformableActivityRuntime.get(listSortedPerformableActivityRuntime.size() - 1).getGate();
				finishgate = cStartgate;
				for (ActivityRuntime actrt : listSortedPerformableActivityRuntime)
				{
					List<Performer> listPerformer = this.stubService.listPerformer(actrt.getGuid());
					actrt.setPerformerList(listPerformer);
					actrt.setSequence(no);
					actMap.put(actrt.getGuid(), actrt);
					no++;
					if (actrt.isFinished() == false)
					{
						if (cStartgate == finishgate)
						{
							cStartgate = actrt.getGate();
						}
					}
				}
			}

			if (!SetUtils.isNullList(listComment))
			{
				Collections.sort(listComment, new ProcTrackComparator(actMap));
				int startGate = 0;
				int[] temp = new int[2];
				temp[0] = -1;
				temp[1] = -1;
				trackPathList.add(temp);
				List<ProcTrack> tempList = new ArrayList<ProcTrack>();
				trackList.add(tempList);
				for (ProcTrack track : listComment)
				{
					int gate = actMap.get(track.getActRuntimeGuid()).getGate();
					if (gate < startGate)
					{
						temp = new int[2];
						temp[0] = actMap.get(track.getActRuntimeGuid()).getGate();
						trackPathList.add(temp);
						tempList = new ArrayList<ProcTrack>();
						trackList.add(tempList);
					}
					tempList.add(track);
					temp[1] = actMap.get(track.getActRuntimeGuid()).getGate();
					startGate = gate;
				}
				if (temp[1] > cStartgate)
				{
					temp = new int[2];
					temp[0] = cStartgate;
					temp[1] = finishgate;
					trackPathList.add(temp);
					tempList = new ArrayList<ProcTrack>();
					trackList.add(tempList);
				}
				else
				{
					temp[1] = finishgate;
				}
				if (!SetUtils.isNullList(listSortedPerformableActivityRuntime))
				{
					for (int i = 0; i < trackPathList.size(); i++)
					{
						temp = trackPathList.get(i);
						for (int j = 0; j < listSortedPerformableActivityRuntime.size(); j++)
						{
							ActivityRuntime actrt = listSortedPerformableActivityRuntime.get(j);
							if (actrt.getGate() >= temp[0] && actrt.getGate() <= temp[1])
							{
								List<ProcTrack> actrtTrackList = this.getActivityCommentList(actrt, trackList.get(i));
								if (!SetUtils.isNullList(actrtTrackList))
								{
									ProcTrack track = actrtTrackList.get(actrtTrackList.size() - 1);
									ActivityRuntime clone = (ActivityRuntime) actrt.clone();
									clone.setStartNumber(track.getStartNumber());
									clone.setFinishTime(track.getFinishTime());
									clone.setDecide(track.getDecide());
									clone.setHistory(true);
									clone.setSequence(actrt.getSequence());
									clone.setPerformerList(actrt.getPerformerList());
									historyActrt.add(clone);
									if (actrt.isFinished() == false && clone.getStartNumber() == actrt.getStartNumber())
									{
										clone.setHistory(false);
										clone.setFinished(false);
										clone.setFinishTime(null);
									}
								}
								else if (actrt.getActType() == WorkflowActivityType.NOTIFY)
								{
									ActivityRuntime clone = (ActivityRuntime) actrt.clone();
									clone.setStartNumber(temp[0]);
									clone.setHistory(clone.isFinished());
									clone.setSequence(actrt.getSequence());
									clone.setPerformerList(actrt.getPerformerList());
									historyActrt.add(clone);
								}
								else if (i == trackPathList.size() - 1)
								{
									historyActrt.add(actrt);
									if (actrt.isFinished())
									{
										actrt.setHistory(true);
									}
								}
							}
						}
					}
				}
				return historyActrt;
			}
			else
			{
				return listSortedPerformableActivityRuntime;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<ProcTrack> getActivityCommentList(ActivityRuntime actrt, List<ProcTrack> listComment)
	{
		if (!SetUtils.isNullList(listComment))
		{
			List<ProcTrack> returnList = new ArrayList<ProcTrack>();
			for (ProcTrack track : listComment)
			{
				if (track.getActRuntimeGuid().equalsIgnoreCase(actrt.getGuid()))
				{
					returnList.add(track);
				}
			}
			return returnList;
		}
		return null;
	}

	private void doTrack(String procRtGuid, String actRtGuid, String performerGuid, DecisionEnum decide, String comment, int startNumber, List<String> alreadyTrackList)
			throws ServiceRequestException
	{
		ProcTrack track = new ProcTrack();
		track.setProcessRuntimeGuid(procRtGuid);
		track.setActRuntimeGuid(actRtGuid);
		track.setDecide(decide);
		track.setComments(comment);
		track.setStartNumber(startNumber);

		// 如果代理人和被代理人都是流程执行人，则需要代理人和被代理人一同审批。
		List<String> principalList = this.stubService.getPrincipalsOfAgent(performerGuid, actRtGuid);
		if (!SetUtils.isNullList(principalList))
		{
			// 如果被代理人没有审批，则代理人审批
			for (String principal : principalList)
			{
				boolean isAlreadyTracked = this.stubService.getTrackStub().isPerformed(actRtGuid, principal, startNumber);
				if (isAlreadyTracked)
				{
					alreadyTrackList.add(principal);
				}
				if (!alreadyTrackList.contains(principal))
				{
					alreadyTrackList.add(principal);
					track.setPerformerGuid(principal);
					track.setAgent(performerGuid);
					this.stubService.getTrackStub().doTrack(track);
				}
			}

			// 当前执行人需要审批但是未审批
			List<User> performerList = this.stubService.getPerformerStub().listActrtPerformer(actRtGuid);
			for (User performer : performerList)
			{
				if (performer.getGuid().equals(performerGuid))
				{
					boolean isPerformed = this.stubService.getTrackStub().isPerformed(actRtGuid, performerGuid, startNumber);
					if (!isPerformed)
					{
						track.setPerformerGuid(performerGuid);
						track.setAgent(null);
						this.stubService.getTrackStub().doTrack(track);
					}
				}
			}
		}
		else
		{
			ActivityRuntime actrt = this.getActivityRuntime(actRtGuid);
			boolean isAgent = this.stubService.getAAS().isAgent(performerGuid, actrt.getCreateUserGuid());
			if ("BEGIN".equals(actrt.getName()) && isAgent)
			{
				track.setPerformerGuid(actrt.getCreateUserGuid());
				track.setAgent(performerGuid);
			}
			else
			{
				track.setPerformerGuid(performerGuid);
				track.setAgent(null);
			}
			this.stubService.getTrackStub().doTrack(track);

		}
	}

	public WorkflowActivityRuntimeData getWorkflowRuntimeData(String procRtGuid, String actRuntimeGuid) throws ServiceRequestException
	{
		WorkflowActivityRuntimeData workflowProp = new WorkflowActivityRuntimeData();

		// 取得流程信息
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
		workflowProp.setProcessRuntime(processRuntime);

		// 取得工作流模板主信息
		WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(processRuntime.getWFTemplateGuid());
		workflowProp.setWorkflowTemplate(workflowTemplate);

		// 活动节点列表
		List<ActivityRuntime> currentActvityList = this.stubService.listCurrentActivityRuntime(procRtGuid);
		workflowProp.setCurrentActvityList(currentActvityList);

		// 取得流程要处理的活动
		this.getCurrentActivityRuntime(currentActvityList, workflowProp, actRuntimeGuid);
		this.loadProcInfo(workflowProp);

		return workflowProp;
	}

	public WorkflowActivityRuntimeData getWorkflowRuntimeData(ObjectGuid objectGuid) throws ServiceRequestException
	{
		FoundationObject foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(objectGuid, false);
		if (!foundationObject.isLatestRevision() || foundationObject.getStatus() == SystemStatusEnum.RELEASE || foundationObject.isCheckOut())
		{
			return null;
		}
		WorkflowActivityRuntimeData workflowProp = new WorkflowActivityRuntimeData();
		// 取得对象正在处理中的最新流程
		ProcessRuntime processRuntime = this.stubService.getInstanceLatestProcessRuntime(objectGuid.getGuid());
		if (processRuntime != null)
		{
			workflowProp.setProcessRuntime(processRuntime);

			// 取得工作流模板主信息
			WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(processRuntime.getWFTemplateGuid());
			workflowProp.setWorkflowTemplate(workflowTemplate);

			// 取得流程的当前活动节点
			List<ActivityRuntime> currentActvityList = this.stubService.listCurrentActivityRuntime(processRuntime.getGuid());
			if (!SetUtils.isNullList(currentActvityList))
			{
				workflowProp.setCurrentActvityList(currentActvityList);

				// 取得流程要处理的活动
				this.getCurrentActivityRuntime(currentActvityList, workflowProp, null);
				this.loadProcInfo(workflowProp);
			}
		}

		return workflowProp;
	}

	private void getCurrentActivityRuntime(List<ActivityRuntime> currentActvityList, WorkflowActivityRuntimeData workflowProp, String actRuntimeGuid) throws ServiceRequestException
	{
		ActivityRuntime currentActivityRuntime = null;
		String currentUserGuid = this.stubService.getUserSignature().getUserGuid();
		List<ActivityRuntime> runTimeList = new ArrayList<ActivityRuntime>();
		if (!SetUtils.isNullList(currentActvityList))
		{
			boolean isFindActivity = false;
			String guid = null;
			for (ActivityRuntime activityRuntime : currentActvityList)
			{
				List<User> listPerFormer = this.stubService.listNotFinishPerformer(activityRuntime.getGuid());
				if (!SetUtils.isNullList(listPerFormer))
				{
					for (User user : listPerFormer)
					{
						boolean isAgent = false;
						boolean isMySelf = false;
						if (currentUserGuid.equalsIgnoreCase(user.getGuid()))
						{
							isMySelf = true;
							workflowProp.setMySelf(isMySelf);
						}
						else
						{
							isAgent = isAgent(currentUserGuid, user.getGuid());
							if (isAgent)
							{
								workflowProp.setAgent(isAgent);
							}
						}
						if (isMySelf || isAgent)
						{
							workflowProp.setAgent(true);
							guid = activityRuntime.getGuid();
							if (StringUtils.isGuid(actRuntimeGuid) && actRuntimeGuid.equalsIgnoreCase(guid))
							{
								isFindActivity = true;
								break;
							}
							else
							{
								runTimeList.add(activityRuntime);
							}
						}
					}
				}
				if (isFindActivity)
				{
					currentActivityRuntime = activityRuntime;
					break;
				}
			}
			if (currentActivityRuntime == null)
			{
				if (!SetUtils.isNullList(runTimeList))
				{
					ActivityRuntime activityRuntime = runTimeList.get(0);
					currentActivityRuntime = activityRuntime;
				}
			}
		}
		workflowProp.setCurrentActivityRuntime(currentActivityRuntime);
	}

	private void loadProcInfo(WorkflowActivityRuntimeData workflowProp)
	{
		try
		{
			boolean isCurrentPerformer = false;
			ProcessRuntime processRuntime = workflowProp.getProcessRuntime();
			String currentUserGuid = this.stubService.getUserSignature().getUserGuid();
			if (workflowProp.isCreate())
			{
				if (currentUserGuid.equals(processRuntime.getCreateUserGuid()))
				{
					workflowProp.setProcessCreator(true);
				}
				else if (isAgent(currentUserGuid, processRuntime.getCreateUserGuid()))
				{
					workflowProp.setAgent(true);
				}
			}
			else if (workflowProp.getProcessStatus() == ProcessStatusEnum.RUNNING && workflowProp.getCurrentActivityRuntime() != null)
			{
				if (workflowProp.getCurrentActivityRuntime().getActType() == WorkflowActivityType.SUB_PROCESS)
				{
					String subProcname = workflowProp.getCurrentActivityRuntime().getSubProcName();
					workflowProp.setSubProcname(subProcname);
					workflowProp.setSubFlow(true);
				}

				List<ActivityRuntime> activityRuntimeList = this.stubService.listRejectedDestinedActivityRuntime(workflowProp.getCurrentActivityRuntime().getGuid());
				if (!SetUtils.isNullList(activityRuntimeList))
				{
					workflowProp.setRejectedDestinedActivityRuntimeList(activityRuntimeList);
				}
				if (workflowProp.isMySelf() || workflowProp.isAgent())
				{
					isCurrentPerformer = true;
				}

			}
			workflowProp.setCreateOrCurrentPerformer(isCurrentPerformer || workflowProp.isProcessCreator());
		}
		catch (ServiceRequestException e)
		{
		}
	}

	private boolean isAgent(String agentGuid, String principalGuid) throws ServiceRequestException
	{
		return this.stubService.getAAS().isAgent(agentGuid, principalGuid);
	}

	public ActivityRuntime getActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return this.dbStub.getActivityRuntime(actRtGuid);
	}

	public ActivityRuntime getBeginActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return this.dbStub.getBeginActivityRuntime(procRtGuid);
	}

	public List<ActivityRuntime> listSortedPerformableActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return this.dbStub.listSortedPerformableActivityRuntime(procRtGuid);
	}

	public List<ActivityRuntime> listRejectableFromActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return this.dbStub.listRejectableFromActivityRuntime(actRtGuid);
	}

	public List<ActivityRuntime> listCurrentActivityRuntime(String procRtGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return this.dbStub.listCurrentActivityRuntime(procRtGuid);
	}

	public List<ActivityRuntime> listAcceptableFromActivityRuntime(String actRtGuid) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return this.dbStub.listAcceptableFromActivityRuntime(actRtGuid);
	}

}
