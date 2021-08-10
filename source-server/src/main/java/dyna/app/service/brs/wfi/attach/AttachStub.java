/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 附件处理分支
 * Wanglei 2010-11-10
 */
package dyna.app.service.brs.wfi.attach;

import dyna.app.core.sch.Scheduler;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.erpi.ERPIImpl;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfm.WFMImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ProAttachAnalysisUtil;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ShortObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.wf.WorkflowActivity;
import dyna.common.bean.model.wf.WorkflowChangePhaseActivity;
import dyna.common.bean.model.wf.WorkflowChangeStatusActivity;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.dto.FileType;
import dyna.common.dto.Session;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowActrtStatusInfo;
import dyna.common.dto.model.wf.WorkflowLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowPhaseChangeInfo;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActCompanyInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeBoInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeRTInfo;
import dyna.common.dto.wf.*;
import dyna.common.dto.wf.ProcAttach.AttachmentType;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.*;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.EMM;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 附件处理分支
 * 
 * @author Wanglei
 * 
 */
public class AttachStub extends AbstractServiceStub<WFIImpl>
{
	private AttachDBStub dbStub = null;

	/**
	 * @param context
	 * @param service
	 */
	public AttachStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.dbStub = new AttachDBStub(context, service);
	}

	/**
	 * 附件改变阶段
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void changeAttachPhase(ActivityRuntime activity) throws ServiceRequestException
	{

		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());

		List<WorkflowPhaseChangeInfo> listPhaseChange = null;
		WorkflowActivity changePhaseActivities = ((WFMImpl) this.stubService.getWFM()).getActivitiyStub().listActivityByApplicationType(processRuntime.getName(),
				activity.getName(), WorkflowApplicationType.CHANGE_PHASE);

		if (changePhaseActivities != null)
		{
			WorkflowChangePhaseActivity changePhaseActivity = (WorkflowChangePhaseActivity) changePhaseActivities;
			listPhaseChange = changePhaseActivity.getPhaseChangeList();
		}

		Map<String, WorkflowPhaseChangeInfo> statusChangeMap = new HashMap<String, WorkflowPhaseChangeInfo>();
		if (!SetUtils.isNullList(listPhaseChange))
		{
			for (WorkflowPhaseChangeInfo sc : listPhaseChange)
			{
				statusChangeMap.put(sc.getLifecycle() + ":" + sc.getFromPhase(), sc);
			}
		}
		else
		{
			return;
		}

		List<ProcAttach> listProcAttach = this.listProcAttach(activity.getProcessRuntimeGuid());
		if (!SetUtils.isNullList(listProcAttach))
		{
			for (ProcAttach attach : listProcAttach)
			{

				LifecyclePhaseInfo lifecyclePhaseInfo = this.stubService.getEMM().getLifecyclePhaseInfo(attach.getLifecyclePhase());
				LifecycleInfo lifecycleInfo = this.stubService.getEMM().getLifecycleInfoByGuid(lifecyclePhaseInfo.getMasterfk());
				// 判断是否可以跳转
				if (statusChangeMap.containsKey(lifecycleInfo.getName() + ":" + lifecyclePhaseInfo.getName()))
				{
					// 调用通用状态变更方法
					WorkflowPhaseChangeInfo workflowStatusChange = statusChangeMap.get(lifecycleInfo.getName() + ":" + lifecyclePhaseInfo.getName());
					LifecyclePhaseInfo lifecyclePhaseInfoTo = this.stubService.getEMM().getLifecyclePhaseInfo(lifecycleInfo.getName(), workflowStatusChange.getToPhase());
					if (lifecyclePhaseInfoTo != null && !lifecyclePhaseInfoTo.getGuid().equalsIgnoreCase(attach.getLifecyclePhase()))
					{
						this.changePhase(attach.getInstanceGuid(), attach.getInstanceClassGuid(), lifecyclePhaseInfoTo.getGuid());
					}
				}
				ObjectGuid mainObjectGuid = new ObjectGuid();
				mainObjectGuid.setClassGuid(attach.getInstanceClassGuid());
				mainObjectGuid.setGuid(attach.getInstanceGuid());
				List<ViewObject> viewList = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listRelation(mainObjectGuid, false, true);
				if (!SetUtils.isNullList(viewList))
				{
					for (ViewObject obj : viewList)
					{
						lifecyclePhaseInfo = this.stubService.getEMM().getLifecyclePhaseInfo(obj.getLifecyclePhaseGuid());
						lifecycleInfo = this.stubService.getEMM().getLifecycleInfoByGuid(lifecyclePhaseInfo.getMasterfk());
						// 判断是否可以跳转
						if (statusChangeMap.containsKey(lifecycleInfo.getName() + ":" + lifecyclePhaseInfo.getName()))
						{
							// 调用通用状态变更方法
							WorkflowPhaseChangeInfo workflowStatusChange = statusChangeMap.get(lifecycleInfo.getName() + ":" + lifecyclePhaseInfo.getName());
							LifecyclePhaseInfo lifecyclePhaseInfoTo = this.stubService.getEMM().getLifecyclePhaseInfo(lifecycleInfo.getName(), workflowStatusChange.getToPhase());
							if (lifecyclePhaseInfoTo != null && !lifecyclePhaseInfoTo.getGuid().equalsIgnoreCase(attach.getLifecyclePhase()))
							{
								this.changePhase(obj.getObjectGuid().getGuid(), obj.getObjectGuid().getClassGuid(), lifecyclePhaseInfoTo.getGuid());
							}
						}
					}
				}
				List<BOMView> bomviewList = ((BOMSImpl) this.stubService.getBOMS()).getBomViewStub().listBOMView(mainObjectGuid, false);
				if (!SetUtils.isNullList(bomviewList))
				{
					for (BOMView obj : bomviewList)
					{
						lifecyclePhaseInfo = this.stubService.getEMM().getLifecyclePhaseInfo(obj.getLifecyclePhaseGuid());
						lifecycleInfo = this.stubService.getEMM().getLifecycleInfoByGuid(lifecyclePhaseInfo.getMasterfk());
						// 判断是否可以跳转
						if (statusChangeMap.containsKey(lifecycleInfo.getName() + ":" + lifecyclePhaseInfo.getName()))
						{
							// 调用通用状态变更方法
							WorkflowPhaseChangeInfo workflowStatusChange = statusChangeMap.get(lifecycleInfo.getName() + ":" + lifecyclePhaseInfo.getName());
							LifecyclePhaseInfo lifecyclePhaseInfoTo = this.stubService.getEMM().getLifecyclePhaseInfo(lifecycleInfo.getName(), workflowStatusChange.getToPhase());
							if (lifecyclePhaseInfoTo != null && !lifecyclePhaseInfoTo.getGuid().equalsIgnoreCase(attach.getLifecyclePhase()))
							{
								this.changePhase(obj.getObjectGuid().getGuid(), obj.getObjectGuid().getClassGuid(), lifecyclePhaseInfoTo.getGuid());
							}
						}
					}
				}

			}

		}
	}

	private void changePhase(String instanceGuid, String instanceClassGuid, String lifecyclePhaseGuid) throws ServiceRequestException
	{
		DataServer.getInstanceService().changePhase(instanceGuid, instanceClassGuid, lifecyclePhaseGuid, false, this.stubService.getFixedTransactionId());

		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setGuid(instanceGuid);
		objectGuid.setClassGuid(instanceClassGuid);
		objectGuid.setClassName(this.stubService.getEMM().getClassByGuid(instanceClassGuid).getName());
		List<ViewObject> viewList = this.stubService.getBOAS().listRelation(objectGuid);
		if (!SetUtils.isNullList(viewList))
		{
			for (ViewObject viewObject : viewList)
			{
				DataServer.getInstanceService().changePhase(viewObject.getGuid(), viewObject.getObjectGuid().getClassGuid(), lifecyclePhaseGuid, false,
						this.stubService.getFixedTransactionId());
			}
		}
	}

	/**
	 * 附件改变状态
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void rollbackAttachStatus(String procrtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listProcAttach = this.listProcAttach(procrtGuid);
		if (!SetUtils.isNullList(listProcAttach))
		{
			for (ProcAttach attach : listProcAttach)
			{
				// 判断是否可以跳转
				SystemStatusEnum fromStatusEnum = SystemStatusEnum.getStatusEnum(attach.getStatus());

				SystemStatusEnum toStatusEnum = SystemStatusEnum.getStatusEnum(attach.getInstanceStatusBackup());

				DataServer.getInstanceService().changePhase(attach.getInstanceGuid(), attach.getInstanceClassGuid(), attach.getInstanceLifcpBackup(), false,
						this.stubService.getFixedTransactionId());
				ObjectGuid objectGuid = new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);
				((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(objectGuid, fromStatusEnum, toStatusEnum, true, false);
				changeViewStatus(attach);
			}
		}
	}

	/**
	 * 通过流程关联附件，修改对应的关联关系状态
	 */
	public void changeViewStatus(ProcAttach attach) throws ServiceRequestException
	{
		List<ViewObject> viewObjectList = new ArrayList<ViewObject>();
		ObjectGuid objectGuid = new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);
		FoundationObject foun = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);
		if (foun == null)
		{
			return;
		}
		BOMView bomView = this.stubService.getBOMS().getBOMViewByEND1(foun.getObjectGuid(), "BOM");
		if (bomView != null)
		{
			SystemStatusEnum fromStatus = SystemStatusEnum.getStatusEnum(bomView.getStatus().toString());
			SystemStatusEnum toStatus = SystemStatusEnum.getStatusEnum(attach.getInstanceStatusBackup());
			DataServer.getInstanceService().changePhase(bomView.getGuid(), bomView.getObjectGuid().getClassGuid(), attach.getInstanceLifcpBackup(), false,
					this.stubService.getFixedTransactionId());
			ObjectGuid bomOjbectGuid = new ObjectGuid(bomView.getObjectGuid().getClassGuid(), null, bomView.getGuid(), null);
			((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(bomOjbectGuid, fromStatus, toStatus, true, false);
		}
		List<RelationTemplateInfo> list = this.stubService.getEMM().listRelationTemplate(foun.getObjectGuid(), true);
		if (!SetUtils.isNullList(list))
		{
			for (RelationTemplateInfo relTemp : list)
			{
				ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(foun.getObjectGuid(), relTemp.getName());
				if (viewObject != null)
				{
					viewObjectList.add(viewObject);
				}
			}
		}
		if (!SetUtils.isNullList(viewObjectList))
		{
			for (ViewObject viewObject : viewObjectList)
			{
				SystemStatusEnum fromStatus = SystemStatusEnum.getStatusEnum(viewObject.getStatus().toString());
				SystemStatusEnum toStatus = SystemStatusEnum.getStatusEnum(attach.getInstanceStatusBackup());
				DataServer.getInstanceService().changePhase(viewObject.getGuid(), viewObject.getObjectGuid().getClassGuid(), attach.getInstanceLifcpBackup(), false,
						this.stubService.getFixedTransactionId());
				ObjectGuid OjbectGuid = new ObjectGuid(viewObject.getObjectGuid().getClassGuid(), null, viewObject.getGuid(), null);
				((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(OjbectGuid, fromStatus, toStatus, true, false);
			}
		}
	}

	/**
	 * 附件改变状态
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void changeAttachStatus(ActivityRuntime activity) throws ServiceRequestException
	{
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());

		List<WorkflowActrtStatusInfo> listStatusChange = null;
		WorkflowActivity changeStatusActivities = ((WFMImpl) this.stubService.getWFM()).getActivitiyStub().listActivityByApplicationType(processRuntime.getName(),
				activity.getName(), WorkflowApplicationType.CHANGE_STATUS);
		if (changeStatusActivities != null)
		{
			WorkflowChangeStatusActivity changeStatusActivity = (WorkflowChangeStatusActivity) changeStatusActivities;
			listStatusChange = changeStatusActivity.getStatusChangeList();
		}
		Map<String, WorkflowActrtStatusInfo> statusChangeMap = new HashMap<String, WorkflowActrtStatusInfo>();
		if (!SetUtils.isNullList(listStatusChange))
		{
			for (WorkflowActrtStatusInfo sc : listStatusChange)
			{
				statusChangeMap.put(sc.getFromStatus(), sc);
			}
		}
		else
		{
			return;
		}

		List<ProcAttach> listProcAttach = this.listProcAttach(activity.getProcessRuntimeGuid());
		if (!SetUtils.isNullList(listProcAttach))
		{
			for (ProcAttach attach : listProcAttach)
			{
				if (statusChangeMap.containsKey(attach.getStatus()))
				{
					SystemStatusEnum fromStatusEnum = SystemStatusEnum.getStatusEnum(attach.getStatus());

					WorkflowActrtStatusInfo workflowStatusChange = statusChangeMap.get(attach.getStatus());
					SystemStatusEnum toStatusEnum = SystemStatusEnum.getStatusEnum(workflowStatusChange.getToStatus());
					ObjectGuid objectGuid = new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);
					ClassStub.decorateObjectGuid(objectGuid, this.stubService);

					if (fromStatusEnum == SystemStatusEnum.RELEASE && toStatusEnum == SystemStatusEnum.OBSOLETE)
					{
						try
						{
							((BOASImpl) this.stubService.getBOAS()).getFoundationStub().stopUsingObject(objectGuid, false, activity.getProcessRuntimeGuid());
						}
						catch (ServiceRequestException e)
						{
							DynaLogger.error(e.getMsrId());
						}
						catch (Exception e1)
						{

						}
					}
					else if (fromStatusEnum == SystemStatusEnum.OBSOLETE && toStatusEnum == SystemStatusEnum.RELEASE)
					{
						try
						{
							((BOASImpl) this.stubService.getBOAS()).getFoundationStub().startUsingObject(objectGuid, false);
						}
						catch (ServiceRequestException e)
						{
							DynaLogger.error(e.getMsrId());
						}
						catch (Exception e1)
						{

						}
					}
					else
					{
						((BOASImpl) this.stubService.getBOAS()).getFSaverStub().changeStatus(objectGuid, fromStatusEnum, toStatusEnum, true, false);
					}
				}
			}
		}

	}

	/**
	 * 根据流程节点配置erp模板信息，创建抛转队列
	 * 
	 * @param procRtGuid
	 * @param actRtGuid
	 * @throws ServiceRequestException
	 */
	public void excuteERPWorkflow(String procRtGuid, String actRtGuid) throws ServiceRequestException
	{
		boolean isExportAll = false;
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
		ActivityRuntime activityRuntime = this.stubService.getActivityRuntime(actRtGuid);
		WorkflowTemplateAct workflowTemplateActSetInfo = this.stubService.getTemplateStub().getWorkflowTemplateActSet(processRuntime.getWFTemplateGuid(),
				activityRuntime.getName());
		if (workflowTemplateActSetInfo == null)
		{
			return;
		}

		List<WorkflowTemplateActCompanyInfo> actCompanyList = workflowTemplateActSetInfo.getActCompanyList();
		List<String> factoryList = new ArrayList<String>();
		if (!SetUtils.isNullList(actCompanyList))
		{
			for (WorkflowTemplateActCompanyInfo company : actCompanyList)
			{
				factoryList.add(company.getCompanyName());
			}
		}
		WorkflowTemplateActInfo workflowTemplateActDto = workflowTemplateActSetInfo.getWorkflowTemplateActInfo();
		if (workflowTemplateActDto == null)
		{
			return;
		}
		String erpTemplateGuid = workflowTemplateActDto.getErpTemplateGuid();
		if (StringUtils.isNullString(erpTemplateGuid))
		{
			return;
		}
		List<ObjectGuid> objecgGuidList = new ArrayList<>();

		isExportAll = workflowTemplateActDto.isExportAll();
		List<ProcAttach> attachmentList = this.stubService.listProcAttach(procRtGuid);
		for (ProcAttach obj : attachmentList)
		{
			if (!isExportAll)
			{
				if (obj.isMain())
				{// 仅抛主审批对象
					objecgGuidList.add(new ObjectGuid(obj.getInstanceClassGuid(), null, obj.getInstanceGuid(), null));
				}
			}
			else
			{// 抛全部对象
				objecgGuidList.add(new ObjectGuid(obj.getInstanceClassGuid(), null, obj.getInstanceGuid(), null));
			}
		}

		try
		{// 抛全部对象即合并队列（isExportAll=isMerge）
			((ERPIImpl) this.stubService.getERP()).getERPStub().createERPJob(objecgGuidList, factoryList, null, erpTemplateGuid, null, true, isExportAll, false);
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.error("[AttachStub#createERPJob]", e);
			throw e;
		}

	}

	/**
	 * 取得流程附件,包含无效附件
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> listProcAttachContainInValid(String procRtGuid) throws ServiceRequestException
	{

		try
		{
			Map<String, Object> filter = new HashMap<>();
			filter.put(ProcAttach.PROCRT_GUID, procRtGuid);
			filter.put("INSTANCETYPES", "'1','3'");

			List<ProcAttach> listProcAttach = this.listProcAttach(filter);

			String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			this.decorate(listProcAttach, currentBizModelGuid);

			return listProcAttach;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<ProcAttach> listSubProcAttach(String procRtGuid, String workflowTemplateGuid) throws ServiceRequestException
	{
		List<ProcAttach> listProcAttach = this.listProcAttach(procRtGuid);

		if (!SetUtils.isNullList(listProcAttach))
		{
			WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(workflowTemplateGuid);
			String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			boolean isAllInvalid = false;
			if (!(currentBizModelGuid.equals(workflowTemplate.getWorkflowTemplateInfo().getBMGuid())
					|| BOMTemplateInfo.ALL.equals(workflowTemplate.getWorkflowTemplateInfo().getBMGuid())))
			{
				isAllInvalid = true;
			}
			List<String> scopeClassGuidList = this.getScropClassGuidList(workflowTemplate);

			for (ProcAttach procAttach : listProcAttach)
			{
				procAttach.setInvalid(true);
				if (isAllInvalid)
				{
					continue;
				}

				if (scopeClassGuidList.contains(procAttach.getInstanceClassGuid()))
				{
					procAttach.setInvalid(false);
				}
			}
		}

		return listProcAttach;

	}

	/**
	 * 取得流程附件
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> listProcAttach(String procRtGuid) throws ServiceRequestException
	{
		try
		{
			Map<String, Object> filter = new HashMap<>();
			filter.put(ProcAttach.PROCRT_GUID, procRtGuid);
			filter.put(ProcAttach.IS_INVALID, BooleanUtils.getBooleanStringYN(false));
			filter.put("INSTANCETYPES", "'1','3'");

			List<ProcAttach> listProcAttach = this.listProcAttach(filter);

			String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			this.decorate(listProcAttach, currentBizModelGuid);

			return listProcAttach;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<ProcAttach> listInValidAttach(String procRtGuid) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<>();
		filter.put(ProcAttach.PROCRT_GUID, procRtGuid);
		filter.put(ProcAttach.IS_INVALID, "Y");
		return this.listProcAttach(filter);

	}

	protected List<ProcAttach> refreshWFAttach(String procRtGuid) throws ServiceRequestException
	{
		String sessionId = this.stubService.getSignature().getCredential();
		List<ProcAttach> procAttachList = this.listInValidAttach(procRtGuid);

		List<FoundationObject> tmpList = new ArrayList<>();
		Map<String, List<String>> tmpMap = new HashMap<>();
		if (!SetUtils.isNullList(procAttachList))
		{
			for (ProcAttach procAttach : procAttachList)
			{
				String instanceClassGuid = procAttach.getInstanceClassGuid();
				tmpMap.computeIfAbsent(instanceClassGuid, k -> new ArrayList<>());
				tmpMap.get(instanceClassGuid).add(procAttach.getInstanceGuid());
			}
		}
		for (String classGuid : tmpMap.keySet())
		{
			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);
			tmpList.addAll(DataServer.getInstanceService().queryByGuidList(classObject.getName(), tmpMap.get(classGuid), sessionId));
		}
		if (!SetUtils.isNullList(procAttachList))
		{
			for (ProcAttach procAttach : procAttachList)
			{
				for (FoundationObject fo : tmpList)
				{
					if (procAttach.getInstanceGuid().equals(fo.getGuid()))
					{
						procAttach.putAll((FoundationObjectImpl) fo);
					}
				}
			}
		}

		return procAttachList;
	}

	public List<ProcAttach> verifyValidAttach(String procRtGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		try
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
			if (processRuntime == null)
			{
				return null;
			}

			if (processRuntime.getStatus() == ProcessStatusEnum.CANCEL || processRuntime.getStatus() == ProcessStatusEnum.CLOSED
					|| processRuntime.getStatus() == ProcessStatusEnum.OBSOLETE || processRuntime.getStatus() == ProcessStatusEnum.RUNNING)
			{
				return null;
			}

			String credential = this.stubService.getSignature().getCredential();

			this.deleteUnExistsAttach(procRtGuid);

			List<ProcAttach> returnAttachList = this.listProcAttachContainInValid(procRtGuid);
			if (!SetUtils.isNullList(returnAttachList))
			{
				for (ProcAttach attach : returnAttachList)
				{
					this.refreshProcrtAttach(credential, this.stubService.getFixedTransactionId(), attach, processRuntime.getName(),
							Constants.isSupervisor(isCheckAcl, this.stubService));
				}
			}

			List<ProcAttach> inValidAttachList = this.refreshWFAttach(procRtGuid);

			if (inValidAttachList != null)
			{
				String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
				this.decorate(inValidAttachList, currentBizModelGuid);
			}
			return inValidAttachList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void calculateAttach(String procRtGuid, ProcAttachSetting settings, boolean isCheckAcl) throws ServiceRequestException
	{
		try
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
			if (processRuntime != null && !(processRuntime.getStatus() == ProcessStatusEnum.CREATED || processRuntime.getStatus() == ProcessStatusEnum.ONHOLD))
			{
				throw new ServiceRequestException("ID_APP_WORKFLOW_RUNNING_NOT_CALCULATE", "workflow's status is not created");
			}

			if (!SetUtils.isNullList(settings.getRelationSetList()))
			{
				ObjectGuid mainObjectGuid = new ObjectGuid();
				mainObjectGuid.setClassGuid(settings.getAttachClassGuid());
				mainObjectGuid.setGuid(settings.getAttachGuid());
				List<ObjectGuid> iputlist = new LinkedList<>();
				iputlist.add(mainObjectGuid);

				Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				if (schedule == null)
				{
					this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
					schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
				}
				String credential = this.stubService.getUserSignature().getCredential();
				ProAttachAnalysisUtil util = new ProAttachAnalysisUtil(credential, this.stubService.getEMM(), this.stubService.getBOMS(), this.stubService.getBOAS(), schedule);
				WorkflowTemplate workflowTemplate = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(processRuntime.getWFTemplateGuid());
				List<WorkflowTemplateScopeRTInfo> listScopeRT = workflowTemplate.getListScopeRT();
				List<FoundationObject> list = util.calculateAttach(iputlist, settings, listScopeRT, Constants.isSupervisor(isCheckAcl, this.stubService));
				// List<FoundationObject> list = new LinkedList<>();
				// Set<String> distinctSet = new HashSet<>();
				// distinctSet.add(settings.getAttachGuid());
				// WFRelationSet bomSet = null;
				// WFRelationSet assoSet = null;
				// for (WFRelationSet tempSet : settings.getRelationSetList())
				// {
				// if (tempSet.isBOM())
				// {
				// bomSet = tempSet;
				// }
				// else
				// {
				// assoSet = tempSet;
				// }
				// }
				// List<BOMTemplateInfo> bomTempLateList =
				// this.stubService.getEMM().listBOMTemplateByEND1(mainObjectGuid);
				// if (!SetUtils.isNullList(bomTempLateList))
				// {
				// this.calculateBOMAttach(bomSet, list, mainObjectGuid, bomTempLateList, distinctSet, isCheckAcl);
				// }
				//
				// this.calculateRelAttach(assoSet, list, mainObjectGuid, processRuntime.getWFTemplateGuid(),
				// distinctSet, isCheckAcl);
				//
				this.addAttachment(procRtGuid, isCheckAcl, list, settings.getBoNameList(), false);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<BOInfo> calculateEnableAttachBO(String procRtGuid, ObjectGuid attachInstanceObjectGuid, List<WFRelationSet> relationSetList) throws ServiceRequestException
	{
		List<BOInfo> returnList = new ArrayList<>();
		List<BOInfo> masterBOList = new ArrayList<>();

		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);
		WorkflowTemplate workflowTemplateInfo = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(processRuntime.getWFTemplateGuid());

		// 后期需处理缓存同步问题（boInfo删除的同时，这里的缓存应该也查不到）
		List<String> copeClassGuidList = getScropClassGuidList(workflowTemplateInfo);

		if (SetUtils.isNullList(copeClassGuidList))
		{
			return returnList;
		}

		if (this.stubService.getEMM().getClassByGuid(attachInstanceObjectGuid.getClassGuid()).hasInterface(ModelInterfaceEnum.IBOMView))
		{
			List<BOInfo> startBOList = this.getStartBOList(attachInstanceObjectGuid);
			for (BOInfo boInfo : startBOList)
			{
				List<BOInfo> listAllSubBOInfo = this.stubService.getEMM().listAllSubBOInfoContain(boInfo.getName());
				if (!SetUtils.isNullList(listAllSubBOInfo))
				{
					for (BOInfo boInfo2 : listAllSubBOInfo)
					{
						if (boInfo2.isAbstract())
						{
							continue;
						}
						if (copeClassGuidList.contains(boInfo2.getClassGuid()) && !returnList.contains(boInfo2))
						{
							returnList.add(boInfo2);
						}
					}
				}
			}
		}
		else
		{
			BOInfo currentBizObject = this.stubService.getEMM().getCurrentBizObject(attachInstanceObjectGuid.getClassGuid());
			List<BOInfo> listAllSubBOInfo = this.stubService.getEMM().listAllSubBOInfoContain(currentBizObject.getName());
			if (!SetUtils.isNullList(listAllSubBOInfo))
			{
				for (BOInfo boInfo : listAllSubBOInfo)
				{
					if (copeClassGuidList.contains(boInfo.getClassGuid()) && !returnList.contains(boInfo))
					{
						if (boInfo.isAbstract())
						{
							continue;
						}

						returnList.add(boInfo);
					}
				}
			}
			masterBOList.addAll(returnList);
		}

		List<RelationTemplateInfo> rtList = new ArrayList<>();

		for (WFRelationSet set : relationSetList)
		{
			if (set.isBOM())
			{
				List<BOMTemplateInfo> listBOMTemplateByName = this.stubService.getEMM().listBOMTemplateByEND1(attachInstanceObjectGuid);

				set.setBomTemplateList(listBOMTemplateByName);
			}
			else
			{
				List<WorkflowTemplateScopeRTInfo> listScopeRT = workflowTemplateInfo.getListScopeRT();
				if (!SetUtils.isNullList(listScopeRT))
				{
					for (WorkflowTemplateScopeRTInfo rt : listScopeRT)
					{
						RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(rt.getTemplateID());
						if (relationTemplate != null)
						{
							rtList.add(relationTemplate);
						}
					}
				}
				set.setRelationTemplateList(rtList);
			}
		}

		int startIndex = returnList.size();
		for (int i = 0; i < returnList.size(); i++)
		{
			List<BOInfo> list = this.getRelationBoInProcess(returnList.get(i), i < startIndex, relationSetList);
			if (list != null)
			{
				for (BOInfo boinfo : list)
				{
					List<BOInfo> listAllSubBOInfo = this.stubService.getEMM().listAllSubBOInfoContain(boinfo.getName());
					for (BOInfo subBOInfo : listAllSubBOInfo)
					{
						if (subBOInfo.isAbstract())
						{
							continue;
						}

						if (returnList.contains(subBOInfo) == false)
						{
							if (copeClassGuidList.contains(subBOInfo.getClassGuid()))
							{
								returnList.add(subBOInfo);
							}
						}

						if (masterBOList.contains(subBOInfo))
						{
							masterBOList.remove(subBOInfo);
						}

					}
				}
			}
		}
		returnList.removeAll(masterBOList);
		return returnList;
	}

	private List<String> getScropClassGuidList(WorkflowTemplate workflowTemplateInfo) throws ServiceRequestException
	{
		List<String> copeClassGuidList = new ArrayList<>();
		if (workflowTemplateInfo.getScopeBOMap() != null)
		{
			for (Entry<String, WorkflowTemplateScopeBoInfo> dto : workflowTemplateInfo.getScopeBOMap().entrySet())
			{
				if (!StringUtils.isNullString(dto.getValue().getBOName()))
				{
					BOInfo boinfo = this.stubService.getEMM().getCurrentBoInfoByName(dto.getValue().getBOName(), false);
					copeClassGuidList.add(boinfo.getClassGuid());
					List<ClassInfo> subclassList = this.stubService.getEMM().listSubClass(boinfo.getClassName(), boinfo.getClassGuid(), true, null);
					if (subclassList != null)
					{
						for (ClassInfo subinfo : subclassList)
						{
							copeClassGuidList.add(subinfo.getGuid());
						}
					}
				}
			}
		}
		return copeClassGuidList;
	}

	private List<BOInfo> getRelationBoInProcess(BOInfo boInfo, boolean isFirst, List<WFRelationSet> relationSetList) throws ServiceRequestException
	{
		EMM emm = this.stubService.getEMM();
		List<BOInfo> returnList = new ArrayList<BOInfo>();
		List<ClassInfo> listAllSuperClass = emm.listAllSuperClass(null, boInfo.getClassGuid());
		ClassInfo classByGuid = this.stubService.getEMM().getClassByGuid(boInfo.getClassGuid());
		listAllSuperClass.add(classByGuid);
		for (WFRelationSet rt : relationSetList)
		{
			if (WFRelationSet.FIRST.equals(rt.getStrategy()))
			{
				if (isFirst == false)
				{
					continue;
				}
			}
			if (WFRelationSet.ONLYBOMVIEW.equals(rt.getStrategy()))
			{
				continue;
			}
			if (rt.isBOM())
			{
				if (SetUtils.isNullList(rt.getBomTemplateList()) == false)
				{
					for (BOMTemplateInfo bomTemplate : rt.getBomTemplateList())
					{
						BOInfo end1bo = null;

						try
						{
							end1bo = emm.getCurrentBoInfoByName(bomTemplate.getEnd1BoName(), true);
						}
						catch (Exception e)
						{
							continue;
						}
						ClassInfo end1ClassInfo = emm.getClassByGuid(end1bo.getClassGuid());
						if (listAllSuperClass.contains(end1ClassInfo))
						{
							List<BOMTemplateEnd2> bomTemplateEnd2List = DataServer.getRelationService().listBOMTemplateEnd2(bomTemplate.getGuid());
							if (SetUtils.isNullList(bomTemplateEnd2List) == false)
							{
								for (BOMTemplateEnd2 bomTemplateEnd2 : bomTemplateEnd2List)
								{
									try
									{
										returnList.add(emm.getCurrentBoInfoByName(bomTemplateEnd2.getEnd2BoName(), true));
									}
									catch (Exception e)
									{
									}
								}
							}
							// rt.getBomTemplateList().remove(bomTemplate);
							// break;
						}
					}
				}
			}
			else
			{
				if (SetUtils.isNullList(rt.getRelationTemplateList()) == false)
				{
					for (RelationTemplateInfo relationTemplate : rt.getRelationTemplateList())
					{
						BOInfo end1bo = null;
						try
						{
							end1bo = emm.getCurrentBoInfoByName(relationTemplate.getEnd1BoName(), true);
						}
						catch (Exception e)
						{
							continue;
						}
						ClassInfo end1ClassInfo = emm.getClassByGuid(end1bo.getClassGuid());
						if (listAllSuperClass.contains(end1ClassInfo))
						{
							List<RelationTemplateEnd2> relationTemplateEnd2List = DataServer.getRelationService().listRelationTemplateEnd2(relationTemplate.getGuid());
							if (SetUtils.isNullList(relationTemplateEnd2List) == false)
							{
								for (RelationTemplateEnd2 relationTemplateEnd2 : relationTemplateEnd2List)
								{
									try
									{
										returnList.add(emm.getCurrentBoInfoByName(relationTemplateEnd2.getEnd2BoName(), true));
									}
									catch (Exception e)
									{
									}
								}
							}
							// rt.getRelationTemplateList().remove(relationTemplate);
							// break;
						}
					}
				}
			}
		}
		return returnList;
	}

	private List<BOInfo> getStartBOList(ObjectGuid attachInstanceObjectGuid) throws ServiceRequestException
	{
		String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		BOMView bomView = ((BOMSImpl) this.stubService.getBOMS()).getBomViewStub().getBOMView(attachInstanceObjectGuid, false);
		BOMTemplateInfo bomTemplate = this.stubService.getEMM().getBOMTemplateById(bomView.getTemplateID());
		return this.getBOMEnd2BOInfo(bomTemplate, bmGuid);
	}

	private List<BOInfo> getBOMEnd2BOInfo(BOMTemplateInfo bomTemplate, String bmGuid) throws ServiceRequestException
	{
		List<BOInfo> returnList = new ArrayList<BOInfo>();
		if (bomTemplate != null)
		{
			List<BOMTemplateEnd2> bomTemplateEnd2List = DataServer.getRelationService().listBOMTemplateEnd2(bomTemplate.getGuid());
			if (!SetUtils.isNullList(bomTemplateEnd2List))
			{
				for (BOMTemplateEnd2 end2 : bomTemplateEnd2List)
				{
					BOInfo boInfo = null;
					try
					{
						boInfo = this.stubService.getEMM().getCurrentBoInfoByName(end2.getEnd2BoName(), true);
					}
					catch (Exception e)
					{
						continue;
					}

					if (boInfo != null)
					{
						returnList.add(boInfo);
					}
				}
			}
		}
		return returnList;
	}

	public List<ProcAttach> addAttachment(String procRtGuid, boolean isCheckAcl, ProcAttach... settings) throws ServiceRequestException
	{
		List<ProcAttach> existAttachList = new ArrayList<>();

		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);

		WorkflowTemplate workflowTemplateInfo = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(processRuntime.getWFTemplateGuid());
		List<String> scropClassGuidList = this.getScropClassGuidList(workflowTemplateInfo);

		try
		{
			if (settings != null)
			{
				for (ProcAttach attach : settings)
				{
					ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(attach.getInstanceClassGuid());
					if (classInfo.hasInterface(ModelInterfaceEnum.IBOMView) || classInfo.hasInterface(ModelInterfaceEnum.IViewObject))
					{
						existAttachList.add(attach);
						continue;
					}
					if (!scropClassGuidList.contains(attach.getInstanceClassGuid()))
					{
						existAttachList.add(attach);
						continue;
					}

					attach.setProcessRuntimeGuid(procRtGuid);
					attach.setMain(true);
					attach.setInvalid(false);
					attach.setCalculated(false);
					attach.setCreateUserGuid(this.stubService.getOperatorGuid());
					attach.setUpdateUserGuid(this.stubService.getOperatorGuid());
					if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IBOMView))
					{
						attach.setAttachmentType(AttachmentType.BOM);
					}
					else if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IViewObject))
					{
						attach.setAttachmentType(AttachmentType.RELATION);
					}
					else
					{
						attach.setAttachmentType(AttachmentType.INSTANCE);
					}

					String instanceClassName = this.stubService.getEMM().getClassByGuid(attach.getInstanceClassGuid()).getName();

					ObjectGuid objectGuid = new ObjectGuid();
					objectGuid.setGuid(attach.getInstanceGuid());
					objectGuid.setClassGuid(attach.getInstanceClassGuid());
					objectGuid.setClassName(instanceClassName);

					Map<String, Object> filter = new HashMap<>();
					filter.put(ProcAttach.INSTANCE_GUID, attach.getInstanceGuid());
					filter.put(ProcAttach.CLASS_GUID, attach.getInstanceClassGuid());
					filter.put(ProcAttach.PROCRT_GUID, procRtGuid);

					List<ProcAttach> query = this.listProcAttach(filter);
					if (!SetUtils.isNullList(query))
					{
						attach = query.get(0);
						if (!attach.isInvalid())
						{
							existAttachList.add(attach);
						}
					}

					FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
					attach = this.dbStub.addAttachment(attach, instance.getIterationId());

					String credential = this.stubService.getSignature().getCredential();

					// 更新附件状态
					this.refreshProcrtAttach(credential, this.stubService.getFixedTransactionId(), attach, workflowTemplateInfo.getWorkflowTemplateInfo().getWFName(),
							Constants.isSupervisor(isCheckAcl, this.stubService));

				}
			}

			return existAttachList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
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

	protected void refreshProcrtAttach(String sessionId, String fixTranId, ProcAttach attach, String wfName, boolean isCheckAcl) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
//			DataServer.getTransactionManager().startTransaction(fixTranId);

			ObjectGuid mainAttachObjectGuid = new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);
			ClassStub.decorateObjectGuid(mainAttachObjectGuid, stubService);

			FoundationObject mainAttachObject = this.stubService.getBOAS().getObject(mainAttachObjectGuid);

			String invalidReasion = null;

			// 检查权限（默认2无权限）
			if (isCheckAcl)
			{
				String aclIsValid = "2";
				Session session = DataServer.getSystemDataService().get(Session.class, sessionId);
				String allAuthority = DataServer.getAclService().getAuthority(mainAttachObjectGuid.getGuid(), mainAttachObjectGuid.getClassGuid(), session.getUserGuid(),
						session.getLoginGroupGuid(), session.getLoginRoleGuid());
				if (!StringUtils.isNullString(allAuthority))
				{
					aclIsValid = allAuthority.split(";")[0];
				}

				if (Integer.parseInt(aclIsValid) == 2)
				{
					invalidReasion = "6";
				}
			}

			if (invalidReasion == null)
			{
				// 判断附件生命周器是否合法
				boolean lifecycleValid = false;
				List<WorkflowLifecyclePhaseInfo> allPhaseInfoList = this.stubService.getWFM().listLifecyclePhaseInfo(null, wfName);
				if (!SetUtils.isNullList(allPhaseInfoList))
				{
					for (WorkflowLifecyclePhaseInfo phaseInfo : allPhaseInfoList)
					{
						if (phaseInfo.getLCPhaseGuid().equals(mainAttachObject.getLifecyclePhaseGuid()))
						{
							lifecycleValid = true;
							break;
						}
					}
				}
				if (!lifecycleValid)
				{
					invalidReasion = "1";
				}
			}

			// 是否检出
			if (invalidReasion == null && mainAttachObject.isCheckOut())
			{
				invalidReasion = "3";
			}

			// 判断工作流程锁定对象
			if (invalidReasion == null)
			{
				boolean lockIsvalid = this.checkLock(attach.getInstanceGuid(), attach.getProcessRuntimeGuid());
				if (!lockIsvalid)
				{
					invalidReasion = "4";
				}
			}

			boolean isInvalid = invalidReasion != null;

			String iterationId = isInvalid ? null : mainAttachObject.getIterationId().toString();
			attach.setIterationId(iterationId);
			attach.setInvalid(isInvalid);
			attach.setInvalidReasion(invalidReasion);
			sds.update(ProcAttach.class, attach, "update");

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			DataServer.getTransactionManager().rollbackTransaction();
			throw new DynaDataExceptionAll("workflow add main attach failed:" + e.getMessage(), e, DataExceptionEnum.DS_EXECUTE_FUNCTION);
		}

	}

	private boolean checkLock(String instanceGuid, String procrtGuid) throws SQLException
	{
		List<String> allProcrtGuidList = getParentProcessRuntime(procrtGuid);
		Map<String, Object> param = new HashMap<>();
		param.put(ProcLockObject.INSTANCE_GUID, instanceGuid);
		List<ProcLockObject> proLockList = DataServer.getSystemDataService().query(ProcLockObject.class, param);
		if (!SetUtils.isNullList(proLockList))
		{
			for (ProcLockObject procLockObject : proLockList)
			{
				String processRuntimeGuid = procLockObject.getProcessRuntimeGuid();
				if (!StringUtils.isNullString(processRuntimeGuid) && !allProcrtGuidList.contains(processRuntimeGuid))
				{
					return false;
				}
			}
		}
		return true;
	}

	private List<String> getParentProcessRuntime(String processRuntimeGuid) throws SQLException
	{
		List<String> allProcrtGuidList = new ArrayList<>();
		allProcrtGuidList.add(processRuntimeGuid);
		Map<String, Object> filter = new HashMap<>();
		filter.put(ProcessRuntime.GUID, processRuntimeGuid);
		ProcessRuntime processRuntime = DataServer.getSystemDataService().queryObject(ProcessRuntime.class, filter);
		if (processRuntime != null && processRuntime.getParentGuid() != null)
		{
			List<String> parentProcrtGuidList = getParentProcessRuntime(processRuntime.getParentGuid());
			if (!SetUtils.isNullList(parentProcrtGuidList))
			{
				allProcrtGuidList.addAll(parentProcrtGuidList);
			}
		}
		return allProcrtGuidList;
	}

	protected void addAttachment(String procRtGuid, boolean isCheckAcl, List<FoundationObject> objList, List<String> boGuidList, boolean isMain) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);

		WorkflowTemplate workflowTemplateInfo = this.stubService.getWfTemplateCacheStub().getWorkflowTemplate(processRuntime.getWFTemplateGuid());
		List<String> scropClassGuidList = this.getScropClassGuidList(workflowTemplateInfo);
		try
		{
			if (objList != null)
			{
				List<ProcAttach> procAttachList = this.listProcAttachContainInValid(procRtGuid);
				Map<String, ProcAttach> procAttachMap = new HashMap<>();
				if (procAttachList != null)
				{
					for (ProcAttach attach : procAttachList)
					{
						procAttachMap.put(attach.getInstanceGuid(), attach);
					}
				}
				for (FoundationObject obj : objList)
				{
					ProcAttach attach = new ProcAttach();
					if (!procAttachMap.containsKey(obj.getObjectGuid().getGuid()))
					{
						ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(obj.getObjectGuid().getClassGuid());
						if (classInfo.hasInterface(ModelInterfaceEnum.IBOMView) || classInfo.hasInterface(ModelInterfaceEnum.IViewObject))
						{
							continue;
						}
						if (!scropClassGuidList.contains(obj.getObjectGuid().getClassGuid()))
						{
							continue;
						}
						attach.setProcessRuntimeGuid(procRtGuid);
						attach.setMain(isMain);
						attach.setInvalid(false);
						attach.setCalculated(!isMain);
						attach.setCreateUserGuid(this.stubService.getOperatorGuid());
						attach.setUpdateUserGuid(this.stubService.getOperatorGuid());
						attach.setInstanceClassGuid(obj.getObjectGuid().getClassGuid());
						attach.setInstanceGuid(obj.getObjectGuid().getGuid());
						attach.put("ITERATIONID", String.valueOf(obj.getIterationId()));
						if (classInfo.hasInterface(ModelInterfaceEnum.IBOMView))
						{
							attach.setAttachmentType(AttachmentType.BOM);
						}
						else if (classInfo.hasInterface(ModelInterfaceEnum.IViewObject))
						{
							attach.setAttachmentType(AttachmentType.RELATION);
						}
						else
						{
							attach.setAttachmentType(AttachmentType.INSTANCE);
						}
						sds.save(attach);

						String credential = this.stubService.getSignature().getCredential();
						ObjectGuid mainAttachObjectGuid = new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null);
						ClassStub.decorateObjectGuid(mainAttachObjectGuid, stubService);

						// 更新附件状态
						this.refreshProcrtAttach(credential, this.stubService.getFixedTransactionId(), attach, workflowTemplateInfo.getWorkflowTemplateInfo().getWFName(),
								Constants.isSupervisor(isCheckAcl, this.stubService));
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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

	public void removeAttachment(String procRtGuid, List<String> procAttachGuidList, boolean isCheckAcl) throws ServiceRequestException
	{
		if (SetUtils.isNullList(procAttachGuidList))
		{
			return;
		}
		Map<String, Object> filter = new HashMap<>();
		filter.put(ProcAttach.PROCRT_GUID, procRtGuid);

		this.dbStub.removeAttachment(filter, procAttachGuidList, isCheckAcl);
	}

	private void decorate(List<ProcAttach> procAttachList, String bmGuid) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(procAttachList))
		{
			for (ProcAttach attach : procAttachList)
			{
				String classGuid = attach.getInstanceClassGuid();
				if (StringUtils.isNullString(classGuid))
				{
					continue;
				}

				String classificationGuid = attach.getClassification();

				BOInfo bizObject = this.stubService.getEMM().getBizObject(bmGuid, classGuid, classificationGuid == null ? "" : classificationGuid);

				if (bizObject != null)
				{
					attach.setBoTitle(bizObject.getTitle());
				}

				if (StringUtils.isGuid(classificationGuid))
				{
					CodeItemInfo classification = this.stubService.getEMM().getCodeItem(classificationGuid);
					if (classification != null)
					{
						attach.setClassificationTitle(classification.getTitle());
					}
				}
				if (StringUtils.isGuid(attach.getLifecyclePhase()))
				{
					LifecyclePhaseInfo info = this.stubService.getEMM().getLifecyclePhaseInfo(attach.getLifecyclePhase());
					if (info != null)
					{
						attach.put(SystemClassFieldEnum.LCPHASE.getName() + "TITLE", info.getTitle());
					}
				}

				if (StringUtils.isNullString(attach.getFileGuid()))
				{
					String type = attach.getFileType();
					if (!StringUtils.isNullString(type))
					{
						FileType fileType = this.stubService.getDSS().getFileType(type);
						attach.clear(ProcAttach.FILE_ICON16);
						attach.put(ProcAttach.FILE_ICON16, fileType.getIcon16());
						attach.clear(ProcAttach.FILE_ICON32);
						attach.put(ProcAttach.FILE_ICON32, fileType.getIcon32());
					}
				}

				// bomview,view中fullname 显示为模板tilte
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);
				// 图标
				if (classInfo != null)
				{
					attach.clear(ShortObject.BO_ICON);
					attach.put(ShortObject.BO_ICON, classInfo.getIcon());

					attach.clear("ICON32$");
					attach.put("ICON32$", classInfo.getIcon32());
					attach.put("CLASSNAME$", classInfo.getName());
				}
			}
		}
	}

	public void deleteUnExistsAttach(String procRtGuid) throws ServiceRequestException
	{
		DataServer.getWorkFlowService().deleteUnExistsAttach(procRtGuid);
	}

	public void removeECPAttachment(String procRtGuid) throws ServiceRequestException
	{
		List<ProcAttach> listProcAttach = this.stubService.listProcAttach(procRtGuid);
		List<String> listECPAppachGuid = new ArrayList<>();
		StringBuilder s = new StringBuilder();
		if (!SetUtils.isNullList(listProcAttach))
		{
			for (ProcAttach procAttach : listProcAttach)
			{
				FoundationObject object = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(procAttach.getInstanceClassGuid(), null, procAttach.getInstanceGuid(), null));
				if (procAttach.getStatus().equals(SystemStatusEnum.ECP.toString()) || (object != null && object.getECFlag() != null))
				{
					listECPAppachGuid.add(procAttach.getInstanceGuid());
					s.append("[").append(object.getFullName()).append("]");
				}
			}
			if (!StringUtils.isNullString(s.toString()))
			{
				throw new ServiceRequestException("ID_APP_UECS_APPACHMENT_IN_ECP_STATUS", "appachment is in EC Lock", null, s.toString());
			}
		}
	}

	protected List<ProcAttach> listProcAttach(Map<String, Object> filter) throws ServiceRequestException
	{
		List<ProcAttach> resultList = new ArrayList<>();
		String procrtGuid = (String) filter.get(ProcAttach.PROCRT_GUID);
		String instanceGuid = (String) filter.get(ProcAttach.INSTANCE_GUID);
		String instanceClassGuid = (String) filter.get(ProcAttach.CLASS_GUID);
		if (StringUtils.isGuid(instanceGuid) && StringUtils.isGuid(instanceClassGuid))
		{
			filter.put("tablename", DataServer.getDSCommonService().getTableName(instanceClassGuid));

			resultList = this.dbStub.listProcAttach(filter, "selectWithClass");

		}
		else if (StringUtils.isGuid(procrtGuid))
		{
			List<ProcAttach> list = this.dbStub.listProcAttach(filter, "selectClassOfInstance");
			if (!SetUtils.isNullList(list))
			{
				for (ProcAttach proc : list)
				{
					instanceClassGuid = proc.getInstanceClassGuid();
					if (!StringUtils.isGuid(instanceClassGuid))
					{
						continue;
					}
					filter.put(ProcAttach.CLASS_GUID, instanceClassGuid);
					filter.put("tablename", DataServer.getDSCommonService().getTableName(instanceClassGuid));

					List<ProcAttach> tempList = this.dbStub.listProcAttach(filter, null);
					if (!SetUtils.isNullList(tempList))
					{
						resultList.addAll(tempList);
					}
				}
			}
		}

		if (!SetUtils.isNullList(resultList))
		{
			this.decorateFullNameOfProcAttach(resultList);
			resultList.sort(Comparator.comparing(ProcAttach::getFullName));
		}
		return resultList;
	}

	private void decorateFullNameOfProcAttach(List<ProcAttach> procAttachList)
	{
		if (!SetUtils.isNullList(procAttachList))
		{
			for (ProcAttach procAttach : procAttachList)
			{
				procAttach.reLoadFullName();
				try
				{
					User ownerUser = this.stubService.getAAS().getUser(procAttach.getOwnerUser());
					if (ownerUser != null)
					{
						procAttach.setOwnerUserName(ownerUser.getName());
					}
				}
				catch (ServiceRequestException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
