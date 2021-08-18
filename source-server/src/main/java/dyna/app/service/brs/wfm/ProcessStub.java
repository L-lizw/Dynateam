/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ActivitiyStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.wfm;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.wf.*;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.dto.SystemWorkflowActivity;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowEventInfo;
import dyna.common.dto.model.wf.WorkflowLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.dto.template.wft.WorkflowTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeBoInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataSqlException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class ProcessStub extends AbstractServiceStub<WFMImpl>
{

	public WorkflowProcess getProcess(String procName) throws ServiceRequestException
	{
		WorkflowProcess process = this.stubService.getWfModelCacheStub().getWorkflowProcessByName(procName);
		return process;
	}

	public WorkflowProcessInfo getProcessInfo(String procName) throws ServiceRequestException
	{
		WorkflowProcess process = this.getProcess(procName);

		return process == null ? null : process.getWorkflowProcessInfo();
	}

	protected WorkflowProcessInfo getProcessInfoByGuid(String procGuid) throws ServiceRequestException
	{
		WorkflowProcess process = this.stubService.getWfModelCacheStub().getWorkflowProcess(procGuid);

		return process == null ? null : process.getWorkflowProcessInfo();
	}

	protected List<WorkflowProcess> listAllWorkflowProcess() throws ServiceRequestException
	{
		List<WorkflowProcess> processList = this.stubService.getWfModelCacheStub().listAllWorkflowProcess();
		return processList;
	}

	protected List<WorkflowProcessInfo> listAllWorkflowProcessInfo() throws ServiceRequestException
	{
		List<WorkflowProcess> processList = this.listAllWorkflowProcess();
		List<WorkflowProcessInfo> resultList = new ArrayList<>();
		if (!SetUtils.isNullList(processList))
		{
			processList.forEach(wfProcess -> {
				resultList.add(wfProcess.getWorkflowProcessInfo());
			});
		}
		return resultList;
	}

	protected List<String> listAllWorkflowName() throws ServiceRequestException
	{
		List<String> workflowProcessNameList = new ArrayList<>();
		List<WorkflowProcess> wProcesses = this.listAllWorkflowProcess();
		if (SetUtils.isNullList(wProcesses))
		{
			return workflowProcessNameList;
		}
		for (WorkflowProcess process : wProcesses)
		{
			workflowProcessNameList.add(process.getWorkflowProcessInfo().getName());
		}
		return workflowProcessNameList;
	}

	/**
	 * 取得人为和通知节点
	 * 
	 * @param processName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<SystemWorkflowActivity> listManualAndNotifyActivity(String processName) throws ServiceRequestException
	{
		WorkflowProcess workflowProcess = this.stubService.getWfModelCacheStub().getWorkflowProcessByName(processName);
		List<SystemWorkflowActivity> manualActivityList = new ArrayList<>();
		if (workflowProcess.getActivityList() != null)
		{
			for (WorkflowActivity activity : workflowProcess.getActivityList())
			{
				WorkflowActivityInfo WorkflowActivityDto = activity.getWorkflowActivityInfo();
				if (WorkflowActivityType.getEnum(activity.getType()) == WorkflowActivityType.MANUAL
						|| WorkflowActivityType.getEnum(activity.getType()) == WorkflowActivityType.NOTIFY)
				{
					SystemWorkflowActivity systemWorkflowActivity = new SystemWorkflowActivity();
					systemWorkflowActivity.setName(WorkflowActivityDto.getName());
					systemWorkflowActivity.setDescription(WorkflowActivityDto.getDescription());
					systemWorkflowActivity.setActivityType(WorkflowActivityType.getEnum(activity.getType()));
					systemWorkflowActivity.setGate(WorkflowActivityDto.getGate());
					manualActivityList.add(systemWorkflowActivity);

				}

				if (WorkflowActivityType.getEnum(activity.getType()) == WorkflowActivityType.APPLICATION)
				{
					if (((WorkflowApplicationActivity) activity).getWorkflowActivityInfo().getApplicationType() == WorkflowApplicationType.CHANGE_STATUS)
					{
						SystemWorkflowActivity systemWorkflowActivity = new SystemWorkflowActivity();
						systemWorkflowActivity.setName(WorkflowActivityDto.getName());
						systemWorkflowActivity.setDescription(WorkflowActivityDto.getDescription());
						systemWorkflowActivity.setActivityType(WorkflowActivityType.getEnum(activity.getType()));
						systemWorkflowActivity.setGate(WorkflowActivityDto.getGate());
						manualActivityList.add(systemWorkflowActivity);
					}
				}

			}
		}
		manualActivityList.sort(Comparator.comparingInt(SystemWorkflowActivity::getGate));

		return manualActivityList;
	}

	/**
	 * 取得能使用当前工作流的、继承了IFoundaion、
	 * 没有继承IBOMview接口、没有继承IviewObject接口的非抽象class。
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ClassInfo> listClassByProcName(String procName) throws ServiceRequestException
	{
		List<String> lifecycleNameList = this.listLifecycleNameOfWfProcess(procName);
		List<ClassInfo> classInfoList = new ArrayList<>();
		List<ClassInfo> classList = DataServer.getClassModelService().listAllClass();
		for (ClassInfo classInfo : classList)
		{
			if (lifecycleNameList.contains(classInfo.getLifecycleName()))
			{
				if (classInfo.hasInterface(ModelInterfaceEnum.IFoundation)//
						&& !classInfo.hasInterface(ModelInterfaceEnum.IUser)//
						&& !classInfo.hasInterface(ModelInterfaceEnum.IGroup)//
						&& !classInfo.hasInterface(ModelInterfaceEnum.IBOMView)//
						&& !classInfo.hasInterface(ModelInterfaceEnum.IViewObject)//
						// 2012-10-29项目管理变更，增加接口IPMRole，IPMCalendar
						&& !classInfo.hasInterface(ModelInterfaceEnum.IPMRole)//
						&& !classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar)//
						&& !classInfo.isAbstract())
				{
					classInfoList.add(classInfo);
				}
			}
		}

		return classInfoList;
	}

	protected List<WorkflowEventInfo> listWorkflowEventInfo(String procGuid, String procName)
	{
		WorkflowProcess workflowProcess = null;
		List<WorkflowEventInfo> wfEventList = null;
		if (StringUtils.isGuid(procGuid))
		{
			workflowProcess = this.stubService.getWfModelCacheStub().getWorkflowProcess(procGuid);
		}
		else if (StringUtils.isNullString(procName))
		{
			workflowProcess = this.stubService.getWfModelCacheStub().getWorkflowProcessByName(procName);
		}
		if (workflowProcess != null)
		{
			wfEventList = workflowProcess.getEventList();
		}
		return wfEventList;
	}

	/**
	 * 取得能使用当前工作流的、继承了IFoundaion、
	 * 没有继承IBOMview接口、没有继承IviewObject接口的非抽象bo。
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<BOInfo> listBOInfoByProcName(String procName, String bmGuid) throws ServiceRequestException
	{
		List<String> lifecycleNameList = this.listLifecycleNameOfWfProcess(procName);

		if (BMInfo.SHARE_MODEL.equalsIgnoreCase(bmGuid))
		{
			BMInfo businessModel = this.stubService.getEMM().getSharedBizModel();
			if (businessModel != null)
			{
				bmGuid = businessModel.getGuid();
			}
		}

		List<BOInfo> boInfoList = new ArrayList<>();
		List<ClassInfo> classList = DataServer.getClassModelService().listAllClass();
		for (ClassInfo classObject : classList)
		{
			if (lifecycleNameList.contains(classObject.getLifecycleName()))
			{
				if (classObject.hasInterface(ModelInterfaceEnum.IFoundation) && !classObject.hasInterface(ModelInterfaceEnum.IUser)
						&& !classObject.hasInterface(ModelInterfaceEnum.IGroup) && !classObject.hasInterface(ModelInterfaceEnum.IViewObject)
						&& !classObject.hasInterface(ModelInterfaceEnum.IPMRole)//
						&& !classObject.hasInterface(ModelInterfaceEnum.IPMCalendar)//
				)
				{
					BOInfo bizObject = ((EMMImpl) this.stubService.getEMM()).getBMStub().getBizObject(bmGuid, classObject.getGuid(), null);
					if (bizObject != null)
					{
						boInfoList.add(bizObject);
					}
				}
			}
		}

		return boInfoList;
	}

	protected List<WorkflowLifecyclePhaseInfo> listLifecyclePhaseInfo(String procguid, String procName)
	{
		WorkflowProcess workflowProcess = null;
		List<WorkflowLifecyclePhaseInfo> lifecyclePhaseList = null;
		if (StringUtils.isGuid(procguid))
		{
			workflowProcess = this.stubService.getWfModelCacheStub().getWorkflowProcess(procguid);
		}
		else if (!StringUtils.isNullString(procName))
		{
			workflowProcess = this.stubService.getWfModelCacheStub().getWorkflowProcessByName(procName);
		}
		if (workflowProcess != null)
		{
			lifecyclePhaseList = workflowProcess.getLifecyclePhaseList();
		}
		return lifecyclePhaseList;

	}

	private List<String> listLifecycleNameOfWfProcess(String procName)
	{
		List<WorkflowLifecyclePhaseInfo> lifecyclePhaseList = this.listLifecyclePhaseInfo(null, procName);
		List<String> lifecycleNameList = new ArrayList<>();
		if (!SetUtils.isNullList(lifecyclePhaseList))
		{
			for (WorkflowLifecyclePhaseInfo phase : lifecyclePhaseList)
			{
				if (!lifecycleNameList.contains(phase.getLifecycleName()))
				{
					lifecycleNameList.add(phase.getLifecycleName());
				}
			}
		}
		return lifecycleNameList;
	}

	private List<WorkflowTemplate> listWorkflowTemplateInfoByUser(String userGuid) throws ServiceRequestException
	{
		if (StringUtils.isNullString(userGuid))
		{
			return null;
		}

		List<WorkflowTemplate> templateList = ((WFIImpl) this.stubService.getWFI()).getWfTemplateCacheStub().listAllWorkflowTemplateInfo();
		Iterator<WorkflowTemplate> it = templateList.iterator();
		while (it.hasNext())
		{
			WorkflowTemplate worflowTemplate = it.next();
			if (!worflowTemplate.getWorkflowTemplateInfo().isValid())
			{
				it.remove();
			}
			else
			{
				List<WorkflowTemplateActPerformerInfo> useList = worflowTemplate.getUseList();
				if (!SetUtils.isNullList(useList))
				{
					boolean isFind = false;
					for (WorkflowTemplateActPerformerInfo performer : useList)
					{
						// 用户可用模版
						if (performer.getPerfType() == PerformerTypeEnum.USER && userGuid.equals(performer.getPerfGuid()))
						{
							isFind = true;
							break;
						}
						else if (performer.getPerfType() == PerformerTypeEnum.GROUP)
						{
							String groupGuid = performer.getPerfGuid();
							if (this.userInGroup(userGuid, groupGuid))
							{
								isFind = true;
								break;
							}
						}
						else if (performer.getPerfType() == PerformerTypeEnum.RIG)
						{
							String rigGuid = performer.getPerfGuid();
							if (this.userInRIG(userGuid, rigGuid))
							{
								isFind = true;
								break;
							}
						}
						else if (performer.getPerfType() == PerformerTypeEnum.ROLE)
						{
							String roleGuid = performer.getPerfGuid();
							if (this.userInRole(userGuid, roleGuid))
							{
								isFind = true;
								break;
							}
						}
					}
					if (!isFind)
					{
						it.remove();
					}
				}
			}
		}

		return templateList;
	}

	/**
	 * 用户是否属于指定的组
	 * 
	 * @param userGuid
	 * @param roleGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean userInRole(String userGuid, String roleGuid) throws ServiceRequestException
	{
		List<User> userList = ((AASImpl) this.stubService.getAAS()).getUserStub().listUserInRole(roleGuid);
		if (!SetUtils.isNullList(userList))
		{
			for (User user_ : userList)
			{
				if (user_.getGuid().equals(userGuid))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 用户是否在指定的组织结构内
	 * 
	 * @param userGuid
	 * @param rigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean userInRIG(String userGuid, String rigGuid) throws ServiceRequestException
	{
		List<User> userList = this.stubService.getAAS().listUserByRoleInGroup(rigGuid);
		if (!SetUtils.isNullList(userList))
		{
			for (User user_ : userList)
			{
				if (user_.getGuid().equals(userGuid))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 用户是否属于指定角色
	 * 
	 * @param userGuid
	 * @param groupGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean userInGroup(String userGuid, String groupGuid) throws ServiceRequestException
	{
		List<User> userList = this.stubService.getAAS().listUserInGroup(groupGuid);
		if (!SetUtils.isNullList(userList))
		{
			for (User user_ : userList)
			{
				if (user_.getGuid().equals(userGuid))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected List<WorkflowTemplateInfo> listSubRunnableProcessTemplate(String workflowName) throws ServiceRequestException
	{
		List<WorkflowTemplateInfo> resultList = new ArrayList<WorkflowTemplateInfo>();

		List<WorkflowTemplate> allWorkflowTemplateInfoList = this.listWorkflowTemplateInfoByUser(this.stubService.getUserSignature().getUserGuid());
		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		if (!SetUtils.isNullList(allWorkflowTemplateInfoList))
		{
			for (WorkflowTemplate template : allWorkflowTemplateInfoList)
			{
				WorkflowTemplateInfo info = template.getWorkflowTemplateInfo();
				if (currentBizModelGuid.equals(info.getBMGuid()) || BOMTemplateInfo.ALL.equals(info.getBMGuid()))
				{

					if (workflowName != null && workflowName.equalsIgnoreCase(info.getWFName()))
					{
						resultList.add(template.getWorkflowTemplateInfo());
					}
				}
			}
		}

		return resultList;
	}

	protected List<WorkflowTemplateInfo> listRunnableProcessTemplate(ObjectGuid objectGuid) throws ServiceRequestException
	{
		String boGuid = null;
		if (objectGuid.getClassGuid() != null)
		{
			BOInfo bizObject = this.stubService.getEMM().getCurrentBizObject(objectGuid.getClassGuid());
			boGuid = bizObject.getGuid();
		}
		else if (StringUtils.isGuid(objectGuid.getBizObjectGuid()))
		{
			boGuid = objectGuid.getBizObjectGuid();
		}

		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		List<WorkflowTemplate> allWorkflowTemplateInfoList = this.listWorkflowTemplateInfoByUser(this.stubService.getUserSignature().getUserGuid());

		List<WorkflowTemplateInfo> resultList = new ArrayList<WorkflowTemplateInfo>();
		if (!SetUtils.isNullList(allWorkflowTemplateInfoList))
		{
			for (WorkflowTemplate template : allWorkflowTemplateInfoList)
			{
				boolean hasBO = false;
				WorkflowTemplateInfo info = template.getWorkflowTemplateInfo();
				if (currentBizModelGuid.equals(info.getBMGuid()))
				{
					if (SetUtils.isNullList(template.getListScopeBO()))
					{
						continue;
					}
					else
					{
						for (WorkflowTemplateScopeBoInfo scopeBO : template.getListScopeBO())
						{
							if (scopeBO.getBOGuid().equals(boGuid) && scopeBO.canLaunch())
							{
								hasBO = true;
								break;
							}
						}

					}

				}
				else if (BOMTemplateInfo.ALL.equals(info.getBMGuid()))
				{
					BMInfo bmInfo = this.stubService.getEMM().getSharedBizModel();
					if (SetUtils.isNullList(template.getListScopeBO()))
					{
						continue;
					}
					else
					{
						for (WorkflowTemplateScopeBoInfo scopeBO : template.getListScopeBO())
						{
							BOInfo boInfo = this.stubService.getEMM().getBizObject(bmInfo.getGuid(), scopeBO.getBOGuid());
							if ((scopeBO.getBOGuid().equals(boGuid) || boInfo != null && boInfo.getClassGuid().equals(objectGuid.getClassGuid())) && scopeBO.canLaunch())
							{
								hasBO = true;
								break;
							}
						}

					}
				}

				if (hasBO)
				{
					resultList.add(template.getWorkflowTemplateInfo());
				}
			}
		}

		return resultList;
	}

	private static List<WorkflowActivity> getSortActivityByGate(WorkflowProcess process)
	{
		if (process.getActivityList() != null)
		{
			List<WorkflowActivity> activityList = new ArrayList<>(process.getActivityList());
			activityList.sort(Comparator.comparingInt(WorkflowActivity::getGate));
			return activityList;
		}
		return null;
	}

	public List<WorkflowActivity> getSortGateList(String processName)
	{
		try
		{
			WorkflowProcess process = this.stubService.getWfModelCacheStub().getWorkflowProcessByName(processName);
			List<WorkflowActivity> activityList = getSortActivityByGate(process);
			if (activityList != null && !activityList.isEmpty())
			{
				List<WorkflowActivity> resultList = new ArrayList<>(activityList.size());
				for (WorkflowActivity activity : activityList)
				{
					if (activity instanceof WorkflowManualActivity || activity instanceof WorkflowNotifyActivity || activity instanceof WorkflowSubProcessActivity)
					{
						resultList.add(activity);
					}
				}
				return resultList;
			}
			return null;
		}
		catch (Exception e)
		{
			throw new DynaDataSqlException("Workflow Process Modle read error", e);
		}
	}

	/**
	 * 得到指定的生命周期和指定阶段的WorkflowProcess列表
	 * 
	 * @param LifecycleName
	 * @param phaseName
	 * @return
	 */
	public List<WorkflowProcessInfo> getPhaseProcessList(String lifecycleName, String phaseName) throws ServiceRequestException
	{
		List<WorkflowProcess> workflowProcessList = this.stubService.getWfModelCacheStub().listAllWorkflowProcess();
		List<WorkflowProcessInfo> resultList = new ArrayList<WorkflowProcessInfo>();
		if (workflowProcessList == null)
		{
			return resultList;
		}

		for (WorkflowProcess process : workflowProcessList)
		{
			if (SetUtils.isNullList(process.getLifecyclePhaseList()))
			{
				continue;
			}

			for (WorkflowLifecyclePhaseInfo lifecyclePhase : process.getLifecyclePhaseList())
			{
				if (lifecyclePhase.getLifecycleName() != null && lifecyclePhase.getLifecycleName().equalsIgnoreCase(lifecycleName) && lifecyclePhase.getPhaseName() != null
						&& lifecyclePhase.getPhaseName().equalsIgnoreCase(phaseName))
				{
					resultList.add(process.getWorkflowProcessInfo());
				}
			}
		}

		return resultList;
	}
}
