/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 流程对象操作分支
 * Wanglei 2010-11-5
 */
package dyna.app.service.brs.wfi.processruntime;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfm.WFMImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.input.InputObjectWrokflowEventImpl;
import dyna.common.bean.extra.ProcessSetting;
import dyna.common.bean.model.wf.*;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.bean.model.wf.template.WorkflowTemplateVo;
import dyna.common.dto.aas.*;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowSubProcessActivityInfo;
import dyna.common.dto.model.wf.WorkflowTransitionInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.template.wft.WorkflowTemplateInfo;
import dyna.common.dto.wf.*;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 流程对象操作分支
 *
 * @author Wanglei
 */
@Component
public class ProcessRuntimeStub extends AbstractServiceStub<WFIImpl>
{
	@Autowired
	private ProcessRuntimeDBStub dbStub = null;

	public synchronized ProcessRuntime createProcess(String wfTemplateGuid, String parentProcGuid, String parentActGuid, String description, boolean isCheckAcl,
			ProcAttach... attachSettings) throws ServiceRequestException
	{
		if (attachSettings == null || attachSettings.length == 0)
		{
			throw new ServiceRequestException("ID_APP_WF_NOATTACH", "no attachment.");
		}

		WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(wfTemplateGuid);
		if (workflowTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_WF_NO_TEMPLATE", "no found template.");
		}

		String wfModelName = workflowTemplate.getWorkflowTemplateInfo().getWFName();

		// create process.......
		WorkflowProcess processModelInfo = ((WFMImpl) this.stubService.getWFM()).getProcessStub().getProcess(wfModelName);

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, String> activityTempMap = new HashMap<String, String>();
		Map<String, String> transitionTempMap = new HashMap<String, String>();

		List<WorkflowRouteActivity> routeActList = new ArrayList<WorkflowRouteActivity>();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 创建流程
			ProcAttach defaultAttach = attachSettings[0];
			ProcessRuntime procRt = this.createProcessRuntime(workflowTemplate, parentProcGuid, parentActGuid, description, defaultAttach);

			this.doAddBeforeEvent4Create(procRt, workflowTemplate, attachSettings);

			String procRtGuid = sds.save(procRt);

			// 所有活动模型信息
			List<WorkflowActivity> activityList = processModelInfo.getActivityList();

			// 活动变迁模型信息
			List<WorkflowTransitionInfo> transitionList = processModelInfo.listAllTransition();

			// 获取活动节点路线
			Map<String, WorkflowActivity> name_ActivityMap = new HashMap<String, WorkflowActivity>();
			Map<String, List<String>> route = new LinkedHashMap<String, List<String>>();

			WorkflowActivity beginActivity = this.getActivityRoute(activityList, name_ActivityMap, route, transitionList);

			// 活动节点截至日期
			Map<String, Date> currentDeadline = new HashMap<String, Date>();

			this.getActivityDateLine(route, name_ActivityMap, currentDeadline, beginActivity, wfTemplateGuid);

			// 创建活动
			this.createActivity(procRtGuid, route, name_ActivityMap, currentDeadline, activityTempMap, routeActList);

			// 创建活动变迁
			this.createTransition(procRtGuid, transitionList, activityTempMap, transitionTempMap);

			// 创建活动变迁约束
			this.createTransRestriction(procRtGuid, routeActList, activityTempMap);

			// 添加主附件
			this.stubService.getAttachStub().addAttachment(procRtGuid, isCheckAcl, attachSettings);

			InputObjectWrokflowEventImpl inputObject = new InputObjectWrokflowEventImpl(procRtGuid, wfModelName);
			this.stubService.getEOSS().executeWorkflowAddAfterEvent(inputObject);

//			DataServer.getTransactionManager().commitTransaction();

			procRt = this.getProcessRuntime(procRtGuid);
			LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

			String title = StringUtils.isNullString(procRt.getDescription()) ? StringUtils.convertNULLtoString(procRt.getDescription())
					: procRt.getDescription();

			ActivityRuntime beginActivityRuntime = this.stubService.getActivityRuntimeStub().getBeginActivityRuntime(procRtGuid);
			String actTitle = null;
			if (beginActivityRuntime != null)
			{
				actTitle = beginActivityRuntime.getTitle(languageEnum);
			}
			actTitle = actTitle == null ? "" : actTitle;
			String contents = actTitle + "("
					+ (StringUtils.isNullString(procRt.getWFTemplateTitle(languageEnum)) ? actTitle : procRt.getWFTemplateTitle(languageEnum)) + ")";
			
			this.stubService.getSMS().getMailSentStub().sendMail4WorkFlow(Arrays.asList(procRt.getCreateUserGuid()), procRt.getCreateUserGuid(), procRtGuid, beginActivityRuntime.getGuid(), contents,
					title, MailMessageType.WORKFLOWAPPROVED, MailCategoryEnum.INFO, beginActivityRuntime.getStartNumber());
			
			return procRt;
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
		finally
		{
			activityTempMap.clear();
			activityTempMap = null;
			transitionTempMap.clear();
			transitionTempMap = null;
			routeActList.clear();
			routeActList = null;
		}
	}

	/**
	 * 创建流程实例
	 *
	 * @param wfTemplateInfo
	 * @param parentProcGuid
	 * @param parentActGuid
	 * @param description
	 * @return
	 * @throws ServiceRequestException
	 */
	private ProcessRuntime createProcessRuntime(WorkflowTemplate wfTemplateInfo, String parentProcGuid, String parentActGuid, String description, ProcAttach defaultAttach)
			throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();

		ProcessRuntime procRt = new ProcessRuntime();
		procRt.setName(wfTemplateInfo.getWorkflowTemplateInfo().getWFName());
		procRt.setTitle(wfTemplateInfo.getWorkflowTemplateInfo().getTitle());
		procRt.setDescription(description);
		procRt.setStatus(ProcessStatusEnum.CREATED);
		procRt.setCreateUserGuid(operatorGuid);
		procRt.setUpdateUserGuid(operatorGuid);
		procRt.setWFTemplateGuid(wfTemplateInfo.getWorkflowTemplateInfo().getGuid());
		procRt.setFromInstance(defaultAttach.getInstanceGuid() + "$" + defaultAttach.getInstanceClassGuid());
		// subflow
		boolean isSubflow = this.checkSubflow(parentActGuid, parentProcGuid);
		if (isSubflow)
		{
			procRt.setActrtGuid(parentActGuid);
			procRt.setParentGuid(parentProcGuid);
		}

		return procRt;
	}

	/**
	 * 如果当前流程为子流程时，检查是否能继续创建子流程
	 *
	 * @param parentActGuid
	 * @param parentProcGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean checkSubflow(String parentActGuid, String parentProcGuid) throws ServiceRequestException
	{
		boolean isSubflow = false;
		if (!StringUtils.isNullString(parentActGuid) && !StringUtils.isNullString(parentProcGuid))
		{
			ActivityRuntime parentActRt = this.stubService.getActivityRuntime(parentActGuid);

			if (parentActRt != null)
			{
				int maxSize = parentActRt.getMaxSubPro();
				int nowSize = this.getSubFlowSize(parentProcGuid, parentActGuid);

				if (nowSize >= maxSize)
				{
					throw new ServiceRequestException("ID_APP_WF_SUBFLOWSIZE_ERROR", "subflow is full");
				}
				isSubflow = true;
			}
		}
		return isSubflow;
	}

	/**
	 * 执行addBefore脚本
	 *
	 * @param procRt
	 * @param workflowTemplate
	 * @param attachSettings
	 * @throws ServiceRequestException
	 */
	private void doAddBeforeEvent4Create(ProcessRuntime procRt, WorkflowTemplate workflowTemplate, ProcAttach... attachSettings) throws ServiceRequestException
	{
		InputObjectWrokflowEventImpl inputObject = new InputObjectWrokflowEventImpl(null, workflowTemplate.getWorkflowTemplateInfo().getWFName());
		inputObject.setAttachSettings(attachSettings);
		inputObject.setDescription(procRt.getDescription());
		inputObject.setParentActGuid(procRt.getActrtGuid());
		inputObject.setParentProcGuid(procRt.getParentGuid());
		inputObject.setWorkflowTemplateInfo(workflowTemplate);
		this.stubService.getEOSS().executeWorkflowAddBeforeEvent(inputObject);
	}

	/**
	 * 创建流程活动
	 *
	 * @param procRtGuid
	 * @param route
	 * @param name_ActivityMap
	 * @param currentDeadline
	 * @param activityTempMap
	 * @param routeActList
	 * @throws ServiceRequestException
	 */
	private void createActivity(String procRtGuid, Map<String, List<String>> route, Map<String, WorkflowActivity> name_ActivityMap, Map<String, Date> currentDeadline,
			Map<String, String> activityTempMap, List<WorkflowRouteActivity> routeActList) throws ServiceRequestException
	{
		if (!SetUtils.isNullMap(route))
		{
			String actName = null;
			WorkflowActivityType actType = null;
			String operatorGuid = this.stubService.getOperatorGuid();
			SystemDataService sds = DataServer.getSystemDataService();

			for (String activityName : route.keySet())
			{
				WorkflowActivity activity = name_ActivityMap.get(activityName);
				WorkflowActivityInfo activityDto = activity.getWorkflowActivityInfo();
				actName = activity.getName();
				actType = WorkflowActivityType.getEnum(activity.getType());
				actType = actType == null ? WorkflowActivityType.APPLICATION : actType;
				ActivityRuntime actRt = new ActivityRuntime();
				actRt.setProcessRuntimeGuid(procRtGuid);
				actRt.setGate(activityDto.getGate());
				actRt.setName(actName);
				actRt.setDescription(activityDto.getDescription());
				actRt.setTitle(activityDto.getTitle());
				actRt.setActType(actType);
				actRt.setStartNumber(0);

				if (actType == WorkflowActivityType.SUB_PROCESS)
				{
					actRt.setBlock(SubProcessTypeEnum.valueOf(((WorkflowSubProcessActivityInfo) activityDto).getSubType().toUpperCase()));
					actRt.setMaxSubPro(((WorkflowSubProcessActivityInfo) activityDto).getStartMax());
					actRt.setMinSubPro(((WorkflowSubProcessActivityInfo) activityDto).getStartMin());
					actRt.setSubProcName(((WorkflowSubProcessActivity) activity).getSubProcessName());
				}
				// set mode
				actRt.setActMode(ActRuntimeModeEnum.NORMAL);
				if (actType == WorkflowActivityType.ROUTE)
				{
					routeActList.add((WorkflowRouteActivity) activity);
				}
				// set application name
				if (activity instanceof WorkflowApplicationActivity)
				{
					actRt.setApplicationName(((WorkflowApplicationActivity) activity).getWorkflowActivityInfo().getApplicationType().name());
				}
				actRt.setCreateUserGuid(operatorGuid);
				actRt.setUpdateUserGuid(operatorGuid);

				if (WorkflowActivityType.getEnum(activity.getType()) == WorkflowActivityType.MANUAL)
				{
					if (DateFormat.compareDate(currentDeadline.get(activity.getName()), new Date()) != 0)
					{
						actRt.setDeadline(currentDeadline.get(activity.getName()));
					}
				}

				String actRtGuid = sds.save(actRt);
				activityTempMap.put(actName, actRtGuid);
			}
		}

	}

	/**
	 * 创建活动变迁
	 *
	 * @param procRtGuid
	 * @param transitionList
	 * @param activityTempMap
	 * @param transitionTempMap
	 * @throws ServiceRequestException
	 */
	private void createTransition(String procRtGuid, List<WorkflowTransitionInfo> transitionList, Map<String, String> activityTempMap, Map<String, String> transitionTempMap)
			throws ServiceRequestException
	{
		if (!SetUtils.isNullList(transitionList))
		{
			String transGuid = null;
			SystemDataService sds = DataServer.getSystemDataService();
			String operatorGuid = this.stubService.getOperatorGuid();
			for (WorkflowTransitionInfo transition : transitionList)
			{
				Transition trans = new Transition();
				trans.setProcessRuntimeGuid(procRtGuid);
				trans.setName(transition.getName());
				trans.setFromActGuid(activityTempMap.get(transition.getActrtFromName()));
				trans.setToActGuid(activityTempMap.get(transition.getActrtToName()));

				trans.setCreateUserGuid(operatorGuid);
				trans.setUpdateUserGuid(operatorGuid);

				transGuid = sds.save(trans);

				transitionTempMap.put(transition.getName(), transGuid);

				// transition condition
				TransCondition tc = new TransCondition();
				tc.setTransGuid(transGuid);
				tc.setConditionType(transition.getTransType());
				tc.setCreateUserGuid(operatorGuid);
				tc.setUpdateUserGuid(operatorGuid);

				sds.save(tc);
			}
		}
	}

	/**
	 * 创建活动变迁约束
	 *
	 * @throws ServiceRequestException
	 */
	private void createTransRestriction(String procRtGuid, List<WorkflowRouteActivity> routeActList, Map<String, String> activityTempMap) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(routeActList))
		{
			String operatorGuid = this.stubService.getOperatorGuid();
			SystemDataService sds = DataServer.getSystemDataService();
			for (WorkflowRouteActivity activity : routeActList)
			{
				TransRestriction transRst = new TransRestriction();
				transRst.setProcessRuntimeGuid(procRtGuid);
				transRst.setActRuntimeGuid(activityTempMap.get(activity.getName()));
				// transRst.setTransGuid(transitionTempMap.get(transName));
				transRst.setRestrictionType(activity.getRouteType());
				transRst.setConnectionType(activity.getRouteMode());
				transRst.setCreateUserGuid(operatorGuid);
				transRst.setUpdateUserGuid(operatorGuid);

				sds.save(transRst);
			}
		}

	}

	/**
	 * 取得当前结点的截止日期
	 * 若有两个或者两个以上的父节点，则取时间较后的一个结点时间计算当前结点的时间
	 *
	 * @param route
	 * @param nameActivity
	 * @param parentDeadline
	 * @param beginActivity
	 * @param wfTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private void getActivityDateLine(Map<String, List<String>> route, Map<String, WorkflowActivity> nameActivity, Map<String, Date> activityDeadline,
			WorkflowActivity beginActivity, String wfTemplateGuid) throws ServiceRequestException
	{
		Map<String, List<String>> childParentMap = new HashMap<String, List<String>>();
		Map<String, List<String>> mapTemp = new LinkedHashMap<String, List<String>>();
		for (String name : route.keySet())
		{
			List<String> children = route.get(name);
			for (String child : children)
			{
				List<String> listP = mapTemp.get(child);
				if (!SetUtils.isNullList(listP))
				{
					listP.add(name);
				}
				else
				{
					listP = new ArrayList<String>();
					listP.add(name);
					mapTemp.put(child, listP);
				}
			}
		}
		this.getManulParent(nameActivity, mapTemp, childParentMap);
		for (String name : route.keySet())
		{
			if (name.equalsIgnoreCase(WorkflowActivityType.BEGIN.name()))
			{
				activityDeadline.put(name, new Date());
			}
			List<String> children = route.get(name);
			for (String child : children)
			{
				if (child.equalsIgnoreCase(WorkflowActivityType.BEGIN.name()) || child.equalsIgnoreCase(WorkflowActivityType.END.name()))
				{
					continue;
				}
				else
				{
					if (childParentMap.get(child) != null)
					{
						List<String> listP = childParentMap.get(child);
						// 选出父节点中最晚的时间
						Date date = activityDeadline.get(listP.get(0));
						if (date == null)
						{
							continue;
						}
						for (String parent : listP)
						{
							Date d1 = activityDeadline.get(parent);
							if (d1 == null)
							{
								break;
							}
							if (DateFormat.compareDate(d1, date) > 0)
							{
								date = d1;
							}
						}
						Date diedLine = this.computeCurrentDateLine(date, child, wfTemplateGuid);
						if (diedLine == null)
						{
							diedLine = new Date();
						}
						activityDeadline.put(child, diedLine);
					}
				}
			}
		}
	}

	/**
	 * 计算当前结点的计划开始时间
	 *
	 * @param preTime
	 * @param activityName
	 * @param wfTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private Date computeCurrentDateLine(Date preTime, String activityName, String wfTemplateGuid) throws ServiceRequestException
	{
		Date dateLine = null;
		if (preTime != null)
		{
			WorkflowTemplateAct workflowTemplateActSingle = this.stubService.getTemplateStub().getWorkflowTemplateActSetInfoSingle(wfTemplateGuid, activityName);
			if (workflowTemplateActSingle != null && workflowTemplateActSingle.getWorkflowTemplateActInfo() != null)
			{
				WorkflowTemplateActInfo workflowTemplateActDto = workflowTemplateActSingle.getWorkflowTemplateActInfo();
				if (!"0".equals(workflowTemplateActDto.getSchemePeriods()))
				{
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(preTime);
					calendar.add(Calendar.DATE, Integer.valueOf(workflowTemplateActDto.getSchemePeriods()));
					dateLine = calendar.getTime();
				}
			}
		}
		return dateLine;
	}

	/**
	 * 取得子结点对应的父节点(结点类型为MANUL或者BEGIN)
	 *
	 * @param nameActivity
	 * @param childParentMap
	 * @param childParentMap2
	 */
	private void getManulParent(Map<String, WorkflowActivity> nameActivity, Map<String, List<String>> mapTemp, Map<String, List<String>> childParentMap)
	{
		for (String child : mapTemp.keySet())
		{
			if (!nameActivity.get(child).getType().equals(WorkflowActivityType.MANUAL))
			{
				continue;
			}
			List<String> parent = new ArrayList<String>();
			this.getParentByChild(child, nameActivity, mapTemp, parent);
			if (!SetUtils.isNullList(parent))
			{
				childParentMap.put(child, parent);
			}
		}
	}

	/**
	 * @param child
	 * @param nameActivity
	 * @param mapTemp
	 * @param parent
	 */
	private void getParentByChild(String child, Map<String, WorkflowActivity> nameActivity, Map<String, List<String>> mapTemp, List<String> parent)
	{
		if (!parent.contains(child) && !nameActivity.get(child).getType().equals(WorkflowActivityType.BEGIN))
		{
			List<String> listP = mapTemp.get(child);
			if (!SetUtils.isNullList(listP))
			{
				for (String p : listP)
				{
					WorkflowActivity pActivity = nameActivity.get(p);
					if (pActivity.getType().equals(WorkflowActivityType.MANUAL) || pActivity.getType().equals(WorkflowActivityType.BEGIN))
					{
						parent.add(p);
					}
					else if (pActivity.getType().equals(WorkflowActivityType.END))
					{
						continue;
					}
					else
					{
						this.getParentByChild(p, nameActivity, mapTemp, parent);
					}
				}
			}
		}
	}

	/**
	 * 获取活动节点路线
	 *
	 * @param nameActivity
	 * @param activityList
	 * @param route
	 * @return
	 * @throws ServiceRequestException
	 */
	private WorkflowActivity getActivityRoute(List<WorkflowActivity> activityList, Map<String, WorkflowActivity> nameActivity, Map<String, List<String>> route,
			List<WorkflowTransitionInfo> listTrans) throws ServiceRequestException
	{
		WorkflowActivity beginActivity = null;
		if (!SetUtils.isNullList(activityList))
		{
			for (WorkflowActivity activity : activityList)
			{
				if (WorkflowActivityType.getEnum(activity.getType()) == WorkflowActivityType.BEGIN)
				{
					beginActivity = activity;
				}
				nameActivity.put(activity.getName(), activity);
			}
		}
		if (!SetUtils.isNullList(listTrans))
		{
			for (WorkflowTransitionInfo tran : listTrans)
			{
				WorkflowActivityInfo fromActivityInfo = this.stubService.getWFM().getWorkflowActivityInfo(tran.getWorkflowGuid(), tran.getActFromGuid());
				WorkflowActivityInfo toActivityInfo = this.stubService.getWFM().getWorkflowActivityInfo(tran.getWorkflowGuid(), tran.getActToGuid());

				if (fromActivityInfo != null && fromActivityInfo.getName().equalsIgnoreCase(beginActivity.getName()))
				{
					List<String> list = route.get(beginActivity.getName());
					// TODO duanll
					if (toActivityInfo != null && !SetUtils.isNullList(list) && !list.contains(toActivityInfo.getName()))
					{
						list.add(toActivityInfo.getName());
					}
					else if (toActivityInfo != null && toActivityInfo.getName() != null)
					{
						list = new ArrayList<String>();
						list.add(toActivityInfo.getName());
						route.put(fromActivityInfo.getName(), list);
					}
				}
			}
			Map<String, Integer> temp = new HashMap<String, Integer>();
			this.getRouteByBeginActivity(listTrans, route, beginActivity.getName(), temp);
		}
		return beginActivity;
	}

	/**
	 * 通过第一个结点找到接下来的路径
	 *
	 * @param listTrans
	 * @param route
	 * @param startActivity
	 * @param temp
	 * @throws ServiceRequestException
	 */
	private void getRouteByBeginActivity(List<WorkflowTransitionInfo> listTrans, Map<String, List<String>> route, String startActivity, Map<String, Integer> temp)
			throws ServiceRequestException
	{
		temp.put(startActivity, temp.get(startActivity) == null ? 1 : (temp.get(startActivity) + 1));
		if (!SetUtils.isNullMap(route) && route.get(startActivity) != null && temp.get(startActivity) == 1)
		{
			List<String> listValue = route.get(startActivity);
			for (String name : listValue)
			{
				if (name.equalsIgnoreCase(WorkflowActivityType.BEGIN.name()))
				{
					continue;
				}
				if (name.equalsIgnoreCase(WorkflowActivityType.END.name()))
				{
					List<String> listEnd = new ArrayList<String>();
					listEnd.add(name);
					route.put(name, listEnd);
					continue;
				}
				for (WorkflowTransitionInfo tran : listTrans)
				{
					String fromActivityGuid = tran.getActFromGuid();
					String toActivityGuid = tran.getActToGuid();
					WorkflowActivityInfo fromActivityInfo = null;
					WorkflowActivityInfo toActivityInfo = null;
					if (StringUtils.isGuid(fromActivityGuid))
					{
						fromActivityInfo = this.stubService.getWFM().getWorkflowActivityInfo(tran.getWorkflowGuid(), fromActivityGuid);
					}

					if (StringUtils.isGuid(toActivityGuid))
					{
						toActivityInfo = this.stubService.getWFM().getWorkflowActivityInfo(tran.getWorkflowGuid(), toActivityGuid);
					}
					if (fromActivityInfo != null && toActivityInfo != null && fromActivityInfo.getName().equalsIgnoreCase(name))
					{
						List<String> list = route.get(name);
						// TODO duanll
						if (!SetUtils.isNullList(list) && !list.contains(toActivityInfo.getName()))
						{
							list.add(toActivityInfo.getName());
						}
						else
						{
							list = new ArrayList<String>();
							list.add(toActivityInfo.getName());
							route.put(fromActivityInfo.getName(), list);
						}
					}
				}
				this.getRouteByBeginActivity(listTrans, route, name, temp);
			}
		}
	}

	public ProcessRuntime saveProcess(String procRtGuid, ProcessSetting settings) throws ServiceRequestException
	{
		if (settings == null)
		{
			return null;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		ProcessRuntime processRuntime = this.getProcessRuntime(procRtGuid);
		try
		{

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 删除所有执行人记录
			this.stubService.getPerformerStub().deleteAllPerformer(procRtGuid);
			// 删除实际执行人记录
			this.stubService.getPerformerStub().deleteActualPerformer(procRtGuid);

			String actRtGuid = null;
			ActivityRuntime actRt = null;
			Entry<String, Performer> perfEntry = null;
			int perfSize = 0;
			Set<Entry<String, Map<String, Performer>>> entries = settings.getAddPerformerEntries();
			for (Entry<String, Map<String, Performer>> entry : entries)
			{
				perfSize = 0;
				actRtGuid = entry.getKey();
				for (Iterator<Entry<String, Performer>> iterator = entry.getValue().entrySet().iterator(); iterator.hasNext();)
				{
					perfEntry = iterator.next();

					Performer perf = perfEntry.getValue();// new Performer();
					if (perf == null)
					{
						continue;
					}

					perf.setProcessRuntimeGuid(procRtGuid);
					perf.setActRuntimeGuid(actRtGuid);
					perf.setCreateUserGuid(operatorGuid);
					perf.setUpdateUserGuid(operatorGuid);

					sds.save(perf);

					List<User> listUser = this.stubService.getPerformerStub().listUserInRoleGroup(perf.getPerformerType(), perfEntry.getKey());
					if (listUser != null)
					{
						perfSize += listUser.size();
					}
				}

				actRt = this.stubService.getActivityRuntimeStub().getActivityRuntime(actRtGuid);

				if (actRt.getActType() == WorkflowActivityType.MANUAL || actRt.getActType() == WorkflowActivityType.NOTIFY)
				{
					WorkflowTemplateAct workflowTemplateAct = this.stubService.getTemplateStub().getWorkflowTemplateActSetInfoSingle(processRuntime.getWFTemplateGuid(),
							actRt.getName());
					String maximumExecutors = workflowTemplateAct == null ? null : workflowTemplateAct.getWorkflowTemplateActInfo().getMaximumExecutors();
					int maxPerf = maximumExecutors == null ? 0 : Integer.valueOf(maximumExecutors);
					if (maxPerf != 0 && perfSize > maxPerf)
					{
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
						throw new ServiceRequestException("ID_APP_WF_PERFORMER_MAXIMUM_OVER", "Ecutors Maximumex Over", null, actRt.getTitle(languageEnum));
					}
				}
			}

			Iterator<Entry<String, Date>> iterator = settings.getDeadlineMap().entrySet().iterator();
			while (iterator.hasNext())
			{
				Entry<String, Date> deadline = iterator.next();
				actRt = this.stubService.getActivityRuntimeStub().getActivityRuntime(deadline.getKey());
				actRt.setDeadline(deadline.getValue());
				sds.save(actRt);
			}

			if (!StringUtils.isNullString(settings.getProcRtDesc()))
			{
				Map<String, Object> filter = new HashMap<>();
				filter.put(SystemObject.GUID, procRtGuid);
				filter.put(ProcessRuntime.DESCRIPTION, settings.getProcRtDesc());
				this.updateProcess(filter);
				processRuntime = this.getProcessRuntime(procRtGuid);
			}
//			DataServer.getTransactionManager().commitTransaction();

			return processRuntime;
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
			throw ServiceRequestException.createByException("", e);
		}
	}

	public synchronized ActivityRuntime resumeProcess(String procRtGuid, String comments, boolean isCheckAcl, ProcessSetting settings) throws ServiceRequestException
	{
		ActivityRuntime beginAct = this.stubService.getActivityRuntimeStub().getBeginActivityRuntime(procRtGuid);
		// ProcessRuntime procRuntime = this.stubService.getProcessRuntime(procRtGuid);
		if (beginAct == null || beginAct.isFinished())
		{
			throw new ServiceRequestException("ID_WEB_WFRESUME_FAILED", "resumeProcess failed");
		}

		String operatorGuid = this.stubService.getOperatorGuid();
		boolean isAgent = this.stubService.getAAS().isAgent(operatorGuid, beginAct.getCreateUserGuid());
		if (!operatorGuid.equals(beginAct.getCreateUserGuid()) && !isAgent)
		{
			throw new ServiceRequestException("ID_WEB_WFRESUME_DENIED", "resumeProcess failed, need creator: " + beginAct.getCreateUserGuid());
		}

		ProcessRuntime processRuntime2 = this.stubService.getProcessRuntime(procRtGuid);
		// subflow
		this.checkSubflow(processRuntime2.getActrtGuid(), processRuntime2.getParentGuid());

		List<ProcAttach> attachList = this.stubService.getAttachStub().listProcAttach(procRtGuid);
		if (SetUtils.isNullList(attachList))
		{
			throw new ServiceRequestException("ID_APP_WF_NOATTACH", "no attachment.");
		}

		// DCR规则检查
		this.stubService.getDCR().check(procRtGuid, processRuntime2.getWFTemplateName(), beginAct.getName(), attachList);

		// set process environment
		if (settings == null)
		{
			settings = new ProcessSetting();
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 判断状态，并改变表中“是否有效”字段
			this.stubService.getAttachStub().verifyValidAttach(procRtGuid, isCheckAcl);
			// 删除已经不存在的对象附件
			this.stubService.getAttachStub().deleteUnExistsAttach(procRtGuid);
			// 判断是否无有效附件
			List<ProcAttach> listProcAttach = this.stubService.listProcAttach(procRtGuid);
			if (SetUtils.isNullList(listProcAttach))
			{
				throw new ServiceRequestException("ID_APP_WORKFLOW_NO_EFFECTIVE_ACCESSORIES", "no effective attach");
			}

			// 先清除相关信息
			this.restartProcrt(procRtGuid);

			ProcessRuntime processRuntime = this.saveProcess(procRtGuid, settings);

			WorkflowTemplateVo workflowTemplate = this.stubService.getWorkflowTemplateDetail(processRuntime.getWFTemplateGuid());
			if (workflowTemplate != null && workflowTemplate.getTemplate().getWorkflowTemplateInfo().isRequiredExecutor())
			{
				this.stubService.getPerformerStub().checkPerform(beginAct, null, true);
			}

			// perform begin activity
			ActivityRuntime performActivityRuntime = this.stubService.getActivityRuntimeStub().performActivityRuntime(beginAct.getGuid(), comments, DecisionEnum.ACCEPT, null, null,
					null, false);

//			DataServer.getTransactionManager().commitTransaction();

			return performActivityRuntime;
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
			throw ServiceRequestException.createByException("", e);
		}
	}

	private void restartProcrt(String procRtGuid) throws ServiceRequestException
	{
		this.dbStub.restartProcrt(procRtGuid);
	}

	public void releaseProcess(List<ObjectGuid> attachObjectGuidList) throws ServiceRequestException
	{
		String credential = this.stubService.getUserSignature().getCredential();
		if (!SetUtils.isNullList(attachObjectGuidList))
		{
			for (ObjectGuid objectGuid : attachObjectGuidList)
			{
				try
				{
					DataServer.getInstanceService().release(objectGuid, credential, this.stubService.getFixedTransactionId());
				}
				catch (Exception e)
				{
					DynaLogger.error(e.getMessage());
				}
			}
		}
	}

	/**
	 * 根据guid查询流程实例
	 *
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProcessRuntime getProcessRuntime(String procRtGuid) throws ServiceRequestException
	{
		ProcessRuntime process = this.dbStub.getProcessRuntime(procRtGuid);
		if (process != null)
		{
			this.decorateProcessRuntime(process);
		}
		return process;
	}

	/**
	 * 流程执行，更新流程信息
	 *
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void runProcess(String procRtGuid) throws ServiceRequestException
	{
		Map<String, Object> searchConditionMap = new HashMap<>();
		searchConditionMap.put(SystemObject.GUID, procRtGuid);
		searchConditionMap.put(ProcessRuntime.STATUS, ProcessStatusEnum.RUNNING);
		searchConditionMap.put("ISSTART", "Y");

		this.updateProcess(searchConditionMap);
		this.stubService.getNoticeStub().processStartUpNotice(procRtGuid);
	}

	public void holdonProcess(String procRtGuid) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			this.updateProcessStatus(procRtGuid, ProcessStatusEnum.ONHOLD);

			this.stubService.getActivityRuntimeStub().resetActivitiesInProcessRuntime(procRtGuid);

			this.stubService.getPerformerStub().resetPerformersOfProcessRuntime(procRtGuid);

			// 结束工作流时回滚数据的状态 20140512变更
			this.stubService.getAttachStub().rollbackAttachStatus(procRtGuid);

			// notify process creator case reject to begin
			ProcessRuntime processRuntime = this.getProcessRuntime(procRtGuid);
			if (processRuntime != null)
			{
				String receiverGuid = processRuntime.getCreateUserGuid();

				LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

				String title = StringUtils.isNullString(processRuntime.getDescription()) ? StringUtils.convertNULLtoString(processRuntime.getDescription())
						: processRuntime.getDescription();

				ActivityRuntime beginActivityRuntime = this.stubService.getActivityRuntimeStub().getBeginActivityRuntime(procRtGuid);
				String actTitle = null;
				if (beginActivityRuntime != null)
				{
					actTitle = beginActivityRuntime.getTitle(languageEnum);
				}
				actTitle = actTitle == null ? "" : actTitle;
				String contents = actTitle + "("
						+ (StringUtils.isNullString(processRuntime.getWFTemplateTitle(languageEnum)) ? actTitle : processRuntime.getWFTemplateTitle(languageEnum)) + ")";

				this.stubService.getSMS().getMailSentStub().sendMail4WorkFlow(Arrays.asList(receiverGuid), receiverGuid, procRtGuid, beginActivityRuntime.getGuid(), contents,
						title, MailMessageType.WORKFLOWAPPROVED, MailCategoryEnum.INFO, beginActivityRuntime.getStartNumber());

			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestException.createByException("ID_APP_PROCESS_HOLD_ON_ERROR", e);
		}
	}

	public void recallProcess(String procRtGuid, String actrtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		// 关闭，废弃，取消状态的流程不能撤消
		ProcessRuntime processRt = this.getProcessRuntime(procRtGuid);
		if (processRt != null && (processRt.getStatus() == ProcessStatusEnum.CLOSED || processRt.getStatus() == ProcessStatusEnum.CANCEL
				|| processRt.getStatus() == ProcessStatusEnum.OBSOLETE || processRt.getStatus() == ProcessStatusEnum.ONHOLD))
		{
			throw new ServiceRequestException("ID_APP_WF_PROCESS_STATUS_NOT_RECALL", "process has closed, can not recall the process");
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 判断能否撤销
			List<ProcessRuntime> procList = new ArrayList<ProcessRuntime>();
			procList.add(processRt);
			this.listAllSubProcess(procList, procRtGuid);
			for (ProcessRuntime proc : procList)
			{
				ActivityRuntime activityRuntime = new ActivityRuntime();
				activityRuntime.setProcessRuntimeGuid(proc.getGuid());
				List<ActivityRuntime> listActivity = sds.query(ActivityRuntime.class, activityRuntime, "changephasestatus");
				if (!SetUtils.isNullList(listActivity))
				{
					throw new ServiceRequestException("ID_APP_WF_CANNTREPEALPROCESS", "can not recall the process");
				}
			}

			List<ProcessRuntime> processRtList = this.listSubProcessRuntime(procRtGuid);

			// repeal subProcess
			if (!SetUtils.isNullList(processRtList))
			{
				for (ProcessRuntime processRuntime : processRtList)
				{
					// 结束工作流时回滚数据的状态 20140512变更
					this.stubService.getAttachStub().rollbackAttachStatus(processRuntime.getGuid());

					this.stubService.getLockStub().unlock(processRuntime.getGuid());

					Map<String, Object> searchConditionMap = new HashMap<>();
					searchConditionMap.put(SystemObject.GUID, processRuntime.getGuid());
					searchConditionMap.put(ProcessRuntime.STATUS, ProcessStatusEnum.CANCEL);
					searchConditionMap.put(ProcessRuntime.IS_DELETE, "Y");
					this.updateProcess(searchConditionMap);
				}
			}

			// repeal parentProcess
			// 结束工作流时回滚数据的状态 20140512变更
			this.stubService.getAttachStub().rollbackAttachStatus(procRtGuid);

			this.stubService.getLockStub().unlock(procRtGuid);

			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put(SystemObject.GUID, procRtGuid);
			searchConditionMap.put(ProcessRuntime.STATUS, ProcessStatusEnum.CANCEL);
			searchConditionMap.put(ProcessRuntime.IS_DELETE, "Y");
			this.updateProcess(searchConditionMap);

			ActivityRuntime activityRuntime = this.stubService.getActivityRuntime(actrtGuid);
			if (activityRuntime != null)
			{
				ActivityRuntime actrt = new ActivityRuntime();
				// activityRuntime.setActMode(ActRuntimeModeEnum.CANCEL);
				// activityRuntime.setFinished(false);
				actrt.setActMode(ActRuntimeModeEnum.CANCEL);
				actrt.setFinished(false);
				actrt.setProcessRuntimeGuid(activityRuntime.getProcessRuntimeGuid());
				sds.save(actrt, "updateActivity");
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
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void listAllSubProcess(List<ProcessRuntime> subProcList, String processGuid) throws ServiceRequestException
	{
		List<ProcessRuntime> processRuntimeList = this.listSubProcessRuntime(processGuid);
		if (!SetUtils.isNullList(processRuntimeList))
		{
			for (ProcessRuntime proc : processRuntimeList)
			{
				this.listAllSubProcess(subProcList, proc.getGuid());
			}
		}
	}

	public void closeProcess(String procRtGuid) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put(SystemObject.GUID, procRtGuid);
			searchConditionMap.put(ProcessRuntime.STATUS, ProcessStatusEnum.CLOSED);
			searchConditionMap.put(ProcessRuntime.IS_FINISH, "Y");

			this.updateProcess(searchConditionMap);
			this.stubService.getNoticeStub().processCompleteNotice(procRtGuid);
			// 结束工作流时去掉回滚数据的状态，状态完全由流程控制 20140512变更

			// get process
			ProcessRuntime procRt = this.getProcessRuntime(procRtGuid);
			String parentProcGuid = procRt.getParentGuid();
			String parentActGuid = procRt.getActrtGuid();
			// get activity
			ActivityRuntime parentActRt = null;
			if (!StringUtils.isNullString(parentProcGuid) && !StringUtils.isNullString(parentActGuid))
			{
				// get activity
				parentActRt = this.stubService.getActivityRuntime(parentActGuid);
			}

			// 阻塞自动
			if (parentActRt != null && SubProcessTypeEnum.AUTOBLOCK.equals(parentActRt.getIsBlock()))
			{
				int allSubFinish = this.isAllSubFinished(parentProcGuid, parentActRt);
				// finish current activity
				if (allSubFinish == 0)
				{
					String operatorGuid = this.stubService.getOperatorGuid();
					this.stubService.getActivityRuntimeStub().performActivityRuntime(parentActGuid, null, DecisionEnum.ACCEPT, null, operatorGuid, null, true);
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
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 判断子流程是否完成，子流程数是否大于最小子流程数
	 *
	 * @param parentProcessRtGuid
	 * @param parentActRt
	 * @return int
	 *         0: 完成
	 *         1: 子流程数小于最小子流程数
	 *         2: 子流程有末完成
	 * @throws ServiceRequestException
	 */
	public int isAllSubFinished(String parentProcessRtGuid, ActivityRuntime parentActRt) throws ServiceRequestException
	{
		int minSize = parentActRt.getMinSubPro();
		int nowSize = this.getSubFlowSize(parentProcessRtGuid, parentActRt.getGuid());
		// int allSubFinish = 0;

		if (minSize > nowSize)
		{
			// 子流程数小于最小子流程数
			return 1;
		}

		List<ProcessRuntime> listProcRt = this.listSubProcessRuntime(parentProcessRtGuid);
		if (!SetUtils.isNullList(listProcRt))
		{
			for (ProcessRuntime proc : listProcRt)
			{
				if (!(proc.isFinished() //
						|| proc.getStatus() == ProcessStatusEnum.CANCEL || proc.getStatus() == ProcessStatusEnum.OBSOLETE || proc.getStatus() == ProcessStatusEnum.ONHOLD
						|| proc.getStatus() == ProcessStatusEnum.CREATED))

				{
					// 子流程有末完成
					return 2;

				}
			}
		}

		return 0;
	}

	public void deleteProcess(ProcessRuntime processRuntime) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			DataServer.getWorkFlowService().deleteProcess(processRuntime);

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
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 更新流程状态
	 *
	 * @param procRtGuid
	 * @param status
	 * @throws ServiceRequestException
	 */
	public void updateProcessStatus(String procRtGuid, ProcessStatusEnum status) throws ServiceRequestException
	{
		Map<String, Object> searchConditionMap = new HashMap<>();
		searchConditionMap.put(SystemObject.GUID, procRtGuid);
		searchConditionMap.put(ProcessRuntime.STATUS, status);
		this.updateProcess(searchConditionMap);
	}

	/**
	 * 根据条件更新流程实例
	 *
	 * @param valueMap
	 * @throws ServiceRequestException
	 */
	protected void updateProcess(Map<String, Object> valueMap) throws ServiceRequestException
	{
		this.dbStub.updateProcess(valueMap);
	}

	public List<ProcessRuntime> listProcessRuntimeOfObject(ObjectGuid objectGuid, SearchCondition searchCondition) throws ServiceRequestException
	{

		List<ProcessRuntime> resultList = this.dbStub.listProcessRuntime(objectGuid, searchCondition);

		this.decorateProcessRuntime(resultList);

		return resultList;
	}

	public List<ProcessRuntime> listProcessRuntime(SearchCondition searchCondition) throws ServiceRequestException
	{

		List<ProcessRuntime> resultList = this.dbStub.listProcessRuntime(null, searchCondition);

		this.decorateProcessRuntime(resultList);

		return resultList;
	}

	public List<ProcessRuntime> queryActrtProcess(String guid, PerformerTypeEnum perfTypeEnum) throws ServiceRequestException
	{
		List<String> perfGuidList = this.getAllAASList(guid, perfTypeEnum);
		String guids = perfGuidList.stream().collect(Collectors.joining("','", "'", "'"));

		ProcessRuntime param = new ProcessRuntime();
		param.put("GUIDLIST", guids);

		List<ProcessRuntime> processList = this.dbStub.listProcessRuntime(param, "selectAllNotFinishProc");
		if (!SetUtils.isNullList(processList))
		{
			for (ProcessRuntime process : processList)
			{
				DynaLogger.info("find running workflow. description='" + process.getDescription() + "'");
			}
		}
		return processList;
	}

	/**
	 * 取得指定对象的所有父阶对象GUID
	 * 
	 * @param guid
	 * @param perfTypeEnum
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<String> getAllAASList(String guid, PerformerTypeEnum perfTypeEnum) throws ServiceRequestException
	{
		List<String> guidList = new ArrayList<String>();
		List<String> tempList = null;
		switch (perfTypeEnum)
		{
		// 废弃用户
		case USER:
			// User不能在未结束的流程中
			guidList.add(guid);
			// user所属的组以及父组不能在未结束的流程中
			tempList = this.listAllGroupOfUser(guid);
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			// user所属的所有组角色不能在未结束的流程中
			tempList = this.listAllRIGOfUser(guid);
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			break;
		// 移除组角色
		case RIG:
			// 组角色不能在未结束的流程中
			guidList.add(guid);
			// 组角色的所有用户都不能在未结束的流程中
			List<User> userList = this.stubService.getAAS().listUserByRoleInGroup(guid);
			if (!SetUtils.isNullList(userList))
			{
				tempList = userList.stream().map(User::getGuid).collect(Collectors.toList());
			}
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			// 组角色的所有组及其父组都不能在未结束的流程中
			List<String> superGroupGuidList = this.listAllSuperGroupByRIG(guid);
			if (!SetUtils.isNullList(superGroupGuidList))
			{
				guidList.addAll(superGroupGuidList);
			}
			break;
		case ROLE:
			// 角色所属的所有组角色
			List<RIG> rigList = this.stubService.getAAS().listRIGByRoleGuid(guid);
			if (!SetUtils.isNullList(rigList))
			{
				rigList.forEach(rig -> {
					try
					{
						// 组角色不能在未结束的流程中
						guidList.add(rig.getGuid());
						// 组角色的所有用户都不能在未结束的流程中
						List<User> userList_ = this.stubService.getAAS().listUserByRoleInGroup(rig.getGuid());
						if (!SetUtils.isNullList(userList_))
						{
							List<String> tempList_ = userList_.stream().map(User::getGuid).collect(Collectors.toList());
							guidList.addAll(tempList_);
						}
						// 组角色的所有组及其父组都不能在未结束的流程中
						List<String> tempList_ = this.listAllSuperGroupByRIG(rig.getGuid());
						if (!SetUtils.isNullList(tempList_))
						{
							guidList.addAll(tempList_);
						}
					}
					catch (ServiceRequestException e)
					{
						e.printStackTrace();
					}
				});
			}
			break;
		case GROUP:
			// 组不能在未结束的流程中
			guidList.add(guid);
			// 组下的所有用户都不能在未结束的流程中
			tempList = this.listAllUserInGroup(guid);
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			// 组的所有子组不能在未结束的流程中
			tempList = this.listAllSubGroup(guid);
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			// 组及子组的所有组角色不能在未结束的流程中
			tempList = this.listAllRIGByGroup(guid, tempList);
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			// 组的所有父组不能在未结束的流程中
			tempList = this.listAllSuperGroup(guid);
			if (!SetUtils.isNullList(tempList))
			{
				guidList.addAll(tempList);
			}
			break;
		default:
			break;
		}
		return guidList;
	}

	private List<String> listAllSuperGroup(String groupGuid) throws ServiceRequestException
	{
		List<String> groupGuidList = new ArrayList<String>();
		while (StringUtils.isGuid(groupGuid))
		{
			groupGuidList.add(groupGuid);
			Group group = this.stubService.getAAS().getGroup(groupGuid);
			groupGuid = group.getParentGuid();
		}
		return groupGuidList;
	}

	private List<String> listAllSuperGroupByRIG(String rigGuid) throws ServiceRequestException
	{
		RIG rig = this.stubService.getAAS().getRIG(rigGuid);
		String groupGuid = rig.getGroupGuid();
		return this.listAllSuperGroup(groupGuid);
	}

	private List<String> listAllSubGroup(String groupGuid) throws ServiceRequestException
	{
		List<Group> groupList = this.stubService.getAAS().listSubGroup(groupGuid, null, true);
		if (!SetUtils.isNullList(groupList))
		{
			return groupList.stream().map(Group::getGuid).collect(Collectors.toList());
		}
		return null;
	}

	private List<String> listAllUserInGroup(String groupGuid) throws ServiceRequestException
	{
		List<User> userList = this.stubService.getAAS().listUserInGroupAndSubGroup(groupGuid);
		if (!SetUtils.isNullList(userList))
		{
			return userList.stream().map(User::getGuid).collect(Collectors.toList());
		}
		return null;
	}

	private List<String> listAllRIGByGroup(String groupGuid, List<String> subGroupGuidList) throws ServiceRequestException
	{
		List<String> groupGuidList = new ArrayList<String>();
		groupGuidList.add(groupGuid);
		if (!SetUtils.isNullList(subGroupGuidList))
		{
			groupGuidList.addAll(subGroupGuidList);
		}

		return this.listAllRIGByGroup(groupGuidList);
	}

	private List<String> listAllRIGByGroup(List<String> groupGuidList)
	{
		List<String> rigGuidList = new ArrayList<String>();
		if (!SetUtils.isNullList(groupGuidList))
		{
			groupGuidList.forEach(groupGuid -> {
				// 所有角色
				try
				{
					List<Role> roleList = this.stubService.getAAS().listRoleByGroup(groupGuid);
					if (!SetUtils.isNullList(roleList))
					{
						roleList.forEach(role -> {
							try
							{
								RIG rig = this.stubService.getAAS().getRIGByGroupAndRole(groupGuid, role.getGuid());
								rigGuidList.add(rig.getGuid());
							}
							catch (ServiceRequestException e)
							{
								e.printStackTrace();
							}
						});
					}
				}
				catch (ServiceRequestException e)
				{
					e.printStackTrace();
				}
			});
		}
		return rigGuidList;
	}

	private List<String> listAllRIGOfUser(String userGuid) throws ServiceRequestException
	{
		User user = this.stubService.getAAS().getUser(userGuid);
		List<RIG> rigList = this.stubService.getAAS().listRIGOfUser(user.getUserId());
		if (!SetUtils.isNullList(rigList))
		{
			return rigList.stream().map(RIG::getGuid).distinct().collect(Collectors.toList());
		}
		return null;
	}

	private List<String> listAllGroupOfUser(String userGuid) throws ServiceRequestException
	{
		List<String> guidList = new ArrayList<String>();
		User user = this.stubService.getAAS().getUser(userGuid);
		List<RIG> rigList = this.stubService.getAAS().listRIGOfUser(user.getUserId());
		if (!SetUtils.isNullList(rigList))
		{
			List<String> groupGuidList = rigList.stream().map(RIG::getGroupGuid).distinct().collect(Collectors.toList());
			if (!SetUtils.isNullList(groupGuidList))
			{
				groupGuidList.forEach(groupGuid -> {
					while (StringUtils.isGuid(groupGuid))
					{
						if (!guidList.contains(groupGuid))
						{
							guidList.add(groupGuid);
						}
						try
						{
							Group group = this.stubService.getAAS().getGroup(groupGuid);
							if (group != null)
							{
								groupGuid = group.getParentGuid();
							}
						}
						catch (ServiceRequestException e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		}
		return guidList;
	}

	public List<ProcessRuntime> listProcessRuntime(Map<String, Object> paramMap) throws ServiceRequestException
	{
		try
		{
			boolean hasCondition = this.dealStartAndEndTime(paramMap);
			if (!hasCondition)
			{
				return new ArrayList<ProcessRuntime>();
			}
			Date startTime = (Date) paramMap.get("STARTTIME");
			Date endTime = (Date) paramMap.get("ENDTIME");

			List<ProcessRuntime> tempList = this.dbStub.listProcessRuntime(paramMap, "selectFinishBy");
			List<ProcessRuntime> resultList = new ArrayList<ProcessRuntime>();
			if (!SetUtils.isNullList(tempList))
			{
				for (ProcessRuntime process : tempList)
				{
					Date finishTime = DateFormat.formatStringToDateYMD(DateFormat.formatYMD(process.getFinishTime()));
					Date deleteTime = DateFormat.formatStringToDateYMD(DateFormat.formatYMD(process.getDeleteTime()));

					if ((startTime.compareTo(finishTime) <= 0 && endTime.compareTo(finishTime) >= 0)
							|| (startTime.compareTo(deleteTime) <= 0 && endTime.compareTo(deleteTime) >= 0))
					{
						resultList.add(process);
					}
				}
			}
			this.decorateProcessRuntime(resultList);

			return resultList;
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<ProcessRuntime>();
	}

	/**
	 * 批量处理
	 *
	 * @param retList
	 * @throws ServiceRequestException
	 */
	private void decorateProcessRuntime(List<ProcessRuntime> retList) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(retList))
		{
			for (ProcessRuntime process : retList)
			{
				this.decorateProcessRuntime(process);
			}
		}
	}

	/**
	 * 单个处理
	 *
	 * @param process
	 * @throws ServiceRequestException
	 */
	private void decorateProcessRuntime(ProcessRuntime process) throws ServiceRequestException
	{
		String createUserGuid = process.getCreateUserGuid();
		String updateUserGuid = process.getUpdateUserGuid();
		String wftemplateguid = process.getWFTemplateGuid();
		WorkflowTemplateInfo wfTemplateInfo = this.stubService.getWorkflowTemplateInfo(wftemplateguid);
		User createUser = this.stubService.getAAS().getUser(createUserGuid);
		User updateUser = this.stubService.getAAS().getUser(updateUserGuid);
		process.put("CREATEUSERNAME", createUser != null ? createUser.getUserName() : null);
		process.put("UPDATEUSERNAME", updateUser != null ? updateUser.getUserName() : null);
		process.put("TEMPLATENAME", wfTemplateInfo != null ? wfTemplateInfo.getName() : null);
		process.put("WFTEMPLATETITLE", wfTemplateInfo != null ? wfTemplateInfo.getTitle() : null);
	}

	public List<ProcessRuntime> listFristPassApprovalProcessRuntime(Map<String, Object> paramMap) throws ServiceRequestException
	{
		try
		{
			boolean hasCondition = this.dealStartAndEndTime(paramMap);
			if (!hasCondition)
			{
				return new ArrayList<ProcessRuntime>();
			}

			Date startTime = (Date) paramMap.get("STARTTIME");
			Date endTime = (Date) paramMap.get("ENDTIME");

			List<ProcessRuntime> retList = this.dbStub.listProcessRuntime(paramMap, "selectFirstPassApproval");
			List<ProcessRuntime> resultList = new ArrayList<ProcessRuntime>();
			if (!SetUtils.isNullList(retList))
			{
				for (ProcessRuntime process : retList)
				{
					Date finishTime = DateFormat.formatStringToDateYMD(DateFormat.formatYMD(process.getFinishTime()));
					if (startTime.compareTo(finishTime) <= 0 && endTime.compareTo(finishTime) >= 0)
					{
						resultList.add(process);
					}
				}
			}
			return resultList;
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<ProcessRuntime>();
	}

	private boolean dealStartAndEndTime(Map<String, Object> paramMap)
	{
		Object startTime_old = paramMap.get("STARTTIME");
		Object endTime_old = paramMap.get("UPDATEUSERNAME");
		if (startTime_old == null || endTime_old == null)
		{
			return false;

		}
		try
		{
			Date startTime = null;
			Date endTime = null;
			if (startTime_old instanceof Date)
			{
				startTime = DateFormat.formatStringToDateYMD(DateFormat.formatYMD((Date) startTime_old));
			}
			else
			{
				startTime = DateFormat.formatStringToDateYMD((String) startTime_old);
			}

			if (endTime_old instanceof Date)
			{
				endTime = DateFormat.formatStringToDateYMD(DateFormat.formatYMD((Date) endTime_old));
			}
			else
			{
				endTime = DateFormat.formatStringToDateYMD((String) endTime_old);
			}
			paramMap.put("STARTTIME", startTime);
			paramMap.put("ENDTIME", endTime);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public List<ProcessRuntime> listSubProcessRuntime(String parentProcGuid) throws ServiceRequestException
	{

		Map<String, Object> searchConditionMap = new HashMap<>();
		searchConditionMap.put(ProcessRuntime.PARENT_GUID, parentProcGuid);

		List<ProcessRuntime> procList = this.dbStub.listProcessRuntime(searchConditionMap, "select");
		return procList;
	}

	public int getSubFlowSize(String parentRtGuid, String parentActrtGuid) throws ServiceRequestException
	{
		int size = 0;
		ProcessRuntime procRt = this.dbStub.getSubProcessRuntime(parentRtGuid, parentActrtGuid);
		if (procRt != null)
		{
			size = ((Number) procRt.get(ProcessRuntime.CNT_SONS)).intValue();
		}

		return size;
	}

	public ProcessRuntime getInstanceLatestProcessRuntime(String instanceGuid) throws ServiceRequestException
	{
		try
		{
			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put(ProcAttach.INSTANCE_GUID, instanceGuid);

			List<ProcessRuntime> midList = this.dbStub.listProcessRuntime(searchConditionMap, "selectCurrentByObject4WF");

			if (SetUtils.isNullList(midList))
			{
				return null;
			}

			ProcessRuntime process = midList.get(0);
			ProcessRuntime midProcess = this.dbStub.getProcessRuntime(searchConditionMap, "selectCurrentByObject");
			process.setActrtTitle(midProcess != null ? midProcess.getActrtTitle() : null);
			if (process != null)
			{
				this.decorateProcessRuntime(process);
			}

			return process;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void runNoticeAction() throws ServiceRequestException
	{
		this.noticeAdvActrtPerf();
		this.noticeDefActrtPerf();
	}

	/**
	 * 将到期工作流活动通知
	 *
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("deprecation")
	public void noticeAdvActrtPerf() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<ActivityRuntime> defActrtList = new ArrayList<>();
		int currentTime = new Date().getDate();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<>();

			List<ActivityRuntime> resultList = sds.query(ActivityRuntime.class, searchConditionMap, "selectAdvActrt");

			ActivityRuntime runtime = sds.queryObject(ActivityRuntime.class, null, "selectCloseTimeAdvActrt");
			if (runtime != null)
			{
				String closeTimeOfDay = (String) runtime.get("DAYSBEFORECLOSETIME");
				if (!SetUtils.isNullList(resultList) && !StringUtils.isNullString(closeTimeOfDay))
				{
					for (ActivityRuntime activity : resultList)
					{
						int day = activity.getDeadline().getDate();
						int daysbeforeclosetime = Integer.parseInt(closeTimeOfDay);
						if (day - daysbeforeclosetime == currentTime)
						{
							defActrtList.add(activity);
						}
					}
				}
			}
			if (!SetUtils.isNullList(defActrtList))
			{
				for (ActivityRuntime actrt : defActrtList)
				{
					this.stubService.getNoticeStub().activiteAdvNotice(actrt);
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 超期工作流活动通知
	 *
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("deprecation")
	public void noticeDefActrtPerf() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<ActivityRuntime> defActrtList = new ArrayList<>();
		int currentTime = new Date().getDate();
		try
		{
			Map<String, Object> searchConditionMap = new HashMap<>();
			List<ActivityRuntime> resultList = sds.query(ActivityRuntime.class, searchConditionMap, "selectDefActrt");
			ActivityRuntime runtime = sds.queryObject(ActivityRuntime.class, null, "selectCloseTimeAdvActrt");
			if (runtime != null)
			{
				String closeTimeOfDay = (String) runtime.get("DAYSBEFORECLOSETIME");
				if (!SetUtils.isNullList(resultList) && !StringUtils.isNullString(closeTimeOfDay))
				{
					for (ActivityRuntime activity : resultList)
					{
						int day = activity.getDeadline().getDate();
						int daysafterclosetime = Integer.parseInt(closeTimeOfDay);
						if (day + daysafterclosetime == currentTime)
						{
							defActrtList.add(activity);
						}
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		if (!SetUtils.isNullList(defActrtList))
		{

			for (ActivityRuntime actrt : defActrtList)
			{
				this.stubService.getNoticeStub().activiteDefNotice(actrt);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void runOverTimeAction() throws ServiceRequestException
	{
		String ACTRTGUID = "ACTRTGUID";
		SystemDataService sds = DataServer.getSystemDataService();
		List<WorkflowTemplateAct> workflowActivityList = new ArrayList<WorkflowTemplateAct>();
		Date currentTime = DateFormat.parse(DateFormat.formatYMD(new Date()), DateFormat.PTN_YMD);
		try
		{
			Map<String, Object> searchConditionMap = new HashMap<>();

			List<ActivityRuntime> resultList = sds.query(ActivityRuntime.class, searchConditionMap, "selectAllDeferredActrt");
			List<ActivityRuntime> actrtList = new ArrayList<>();
			if (!SetUtils.isNullList(resultList))
			{
				for (ActivityRuntime activity : resultList)
				{
					if (activity.getDeadline() != null)
					{
						Date deadLine = new Date(DateFormat.formatYMD(activity.getDeadline()));
						if (currentTime.compareTo(deadLine) >= 0)
						{
							actrtList.add(activity);
						}
					}
				}
			}

			if (!SetUtils.isNullList(actrtList))
			{
				for (ActivityRuntime actrt : actrtList)
				{
					String actrtName = actrt.getActrtName();
					String wfTemplateGuid = (String) actrt.get("WFTEMPLATEGUID");

					WorkflowTemplateAct wfTemplate = this.stubService.getTemplateStub().getWorkflowTemplateActSet(wfTemplateGuid, actrtName);
					if (wfTemplate != null)
					{
						workflowActivityList.add(wfTemplate);
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		if (workflowActivityList != null)
		{
			for (WorkflowTemplateAct templateAct : workflowActivityList)
			{
				String overTimeAction = templateAct.getWorkflowTemplateActInfo().getOverTimeAction();
				String actRtGuid = (String) templateAct.getWorkflowTemplateActInfo().get(ACTRTGUID);

				OverTimeActionEnum overTimeActionEnum = OverTimeActionEnum.getEnum(overTimeAction);
				switch (overTimeActionEnum)
				{
				case COMPLETE:
					this.autoPerform(actRtGuid, DecisionEnum.ACCEPT);
					break;
				case SKIP:
					this.autoPerform(actRtGuid, DecisionEnum.SKIP);
					break;
				case WAIT:
					break;
				default:
					break;
				}
			}
		}

	}

	public void runOverTimeAgent() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<UserAgent> userAgentList = null;
		try
		{
			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put("ISOVERTIME", "Y");

			userAgentList = sds.query(UserAgent.class, searchConditionMap);

			if (!SetUtils.isNullList(userAgentList))
			{
				for (UserAgent agent : userAgentList)
				{
					this.stubService.getAAS().obsoleteUserAgent(agent);
					String mailContent = this.formatMailContent(agent);
					try
					{
						if (mailContent != null)
						{
							this.stubService.getSMS().sendMailToUser(this.formatMsg("ID_CLIENT_AGENT_INVALID_MAIL_SEND_SUBJECT", this.stubService.getUserSignature().getUserName()),
									mailContent, MailCategoryEnum.INFO, null, agent.getAgentGuid(), MailMessageType.AGENTNOTIFY);
						}
					}
					catch (ServiceRequestException e)
					{
						this.stubService.getSMS().sendMailToUser(this.formatMsg("ID_CLIENT_AGENT_MAIL_SEND_FAILED", null),
								e.getMessage() == null ? e.getCause().getMessage() : e.getMessage(), MailCategoryEnum.INFO, null, agent.getPrincipalGuid(),
								MailMessageType.AGENTNOTIFY);
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private String formatMsg(String msgId, String val) throws ServiceRequestException
	{
		String msg = this.stubService.getMSRM().getMSRString(msgId, this.stubService.getUserSignature().getLanguageEnum().toString());
		if (StringUtils.isNullString(val))
		{
			return msg;
		}
		return MessageFormat.format(msg, val);
	}

	private String formatMailContent(UserAgent agent)
	{
		if (agent != null)
		{
			String fromUser = agent.getPrincipalName();
			return MessageFormat.format("ID_CLIENT_AGENT_MAIL_CONTENT_INV", fromUser);
		}
		return null;
	}

	private void autoPerform(String actRtGuid, DecisionEnum decise) throws ServiceRequestException
	{
		ActivityRuntime activityRuntime = this.stubService.getActivityRuntime(actRtGuid);

		List<User> listNotFinishPerformer = this.stubService.listNotFinishPerformer(actRtGuid);
		if (!SetUtils.isNullList(listNotFinishPerformer))
		{
			for (User user : listNotFinishPerformer)
			{
				if (activityRuntime != null && !activityRuntime.isFinished())
				{
					this.stubService.getActivityRuntimeStub().performActivityRuntime(actRtGuid, null, decise, null, user.getGuid(), null, false);
				}
				else
				{
					break;
				}
			}

		}
		else
		{
			this.stubService.getActivityRuntimeStub().performActivityRuntime(actRtGuid, null, decise, null, null, null, false);
		}
	}

	public String isInRunningProcess(ObjectGuid objectGuid) throws ServiceRequestException
	{
		List<ProcessRuntime> procRtList = this.listProcessRuntimeOfObject(objectGuid, null);
		if (!SetUtils.isNullList(procRtList))
		{
			for (ProcessRuntime procRt : procRtList)
			{
				if (procRt.getStatus() == ProcessStatusEnum.RUNNING)
				{
					return procRt.getGuid();
				}
			}
		}
		return null;
	}

	public boolean isEffectiveApprovalProcess(String procRtGuid, String actRtGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(actRtGuid))
		{
			ActivityRuntime activityRuntime = this.stubService.getActivityRuntime(actRtGuid);

			if (activityRuntime != null && activityRuntime.getActType() == WorkflowActivityType.NOTIFY)
			{
				return true;
			}
		}

		List<ActivityRuntime> listCurrentActivityRuntime = this.stubService.listCurrentActivityRuntime(procRtGuid);
		String operatorGuid = this.stubService.getOperatorGuid();

		if (!SetUtils.isNullList(listCurrentActivityRuntime))
		{
			for (ActivityRuntime actrt : listCurrentActivityRuntime)
			{
				List<User> listNotFinishPerformer = this.stubService.listNotFinishPerformer(actrt.getGuid());
				if (!SetUtils.isNullList(listNotFinishPerformer))
				{
					for (User user : listNotFinishPerformer)
					{
						boolean isAgent = this.stubService.getAAS().isAgent(operatorGuid, user.getGuid());
						if (operatorGuid.equalsIgnoreCase(user.getGuid()) || isAgent)
						{
							return true;
						}
					}
				}
			}
		}

		if (SetUtils.isNullList(listCurrentActivityRuntime))
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
			if (processRuntime != null && processRuntime.getStatus() == ProcessStatusEnum.ONHOLD)
			{
				ActivityRuntime beginActivityRuntime = this.stubService.getBeginActivityRuntime(procRtGuid);
				boolean isAgent = this.stubService.getAAS().isAgent(operatorGuid, beginActivityRuntime.getCreateUserGuid());
				if (operatorGuid.equals(beginActivityRuntime.getCreateUserGuid()) || isAgent)
				{
					return true;
				}
			}

		}

		return false;
	}
}