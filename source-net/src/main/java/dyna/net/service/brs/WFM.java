/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WFI Workflow Interpreter 工作流解析器
 * Wanglei 2010-11-4
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.SystemWorkflowActivity;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowActrtActionInfo;
import dyna.common.dto.model.wf.WorkflowActrtLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowActrtStatusInfo;
import dyna.common.dto.model.wf.WorkflowEventInfo;
import dyna.common.dto.model.wf.WorkflowLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowPhaseChangeInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.dto.model.wf.WorkflowTransitionInfo;
import dyna.common.dto.template.wft.WorkflowTemplateInfo;
import dyna.common.dto.wf.ApproveTemplate;
import dyna.common.dto.wf.ApproveTemplateDetail;
import dyna.common.dto.wf.GraphTransition;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * Workflow Model 工作流模型
 * 
 * @author Wanglei
 * 
 */
public interface WFM extends Service
{

	/**
	 * 批量保存流程模板
	 * 
	 * @param addApproveTemplateList
	 *            新增列表
	 * @param updateApproveTemplateList
	 *            修改列表
	 * @throws ServiceRequestException
	 */
	public void batchSaveApproveTemplate(List<ApproveTemplate> addApproveTemplateList, List<ApproveTemplate> updateApproveTemplateList) throws ServiceRequestException;

	/**
	 * 批量删除流程模板
	 * 
	 * @throws ServiceRequestException
	 */
	public void batchDeleteApproveTemplate(List<ApproveTemplate> deleteApproveTemlateList) throws ServiceRequestException;

	/**
	 * 判断执行人模板是否能被应用到工作流中
	 * 
	 * @param processGuid
	 *            流程GUID
	 * @param executorTemplateGuid
	 *            执行人模板GUID
	 * @return boolean 是否能应用到工作流
	 * @throws ServiceRequestException
	 */
	public boolean canApplyExecutorTemplate(String processGuid, String executorTemplateGuid) throws ServiceRequestException;

	/**
	 * 获取流程定义的基本信息
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowProcessInfo getProcessModelInfo(String procName) throws ServiceRequestException;

	/**
	 * 获取流程定义的基本信息
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowProcessInfo getProcessModelInfoByGuid(String procModelGuid) throws ServiceRequestException;

	/**
	 * 获取流程内指定活动的基本信息
	 * 
	 * @param procName
	 * @param actName
	 * @return 模板活动对象
	 * @throws ServiceRequestException
	 */
	public WorkflowActivityInfo getWorkflowActivityInfo(String procModelGuid, String wfActivityGuid) throws ServiceRequestException;

	/**
	 * 获取流程内指定活动的基本信息
	 * 
	 * @param procName
	 * @param actName
	 * @return 模板活动对象
	 * @throws ServiceRequestException
	 */
	public WorkflowActivityInfo getWorkflowActivityInfoByName(String procName, String actName) throws ServiceRequestException;

	/**
	 * 获取流程开始活动
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowActivityInfo getWorkflowBeginActivityInfoByName(String procName) throws ServiceRequestException;

	/**
	 * 获取所有定义的工作流模型基本信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowProcessInfo> listAllWorkflowInfo() throws ServiceRequestException;

	/**
	 * 列出所有的流程模型名称
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listAllWorkflowName() throws ServiceRequestException;

	/**
	 * 查询可到达指定活动的可执行活动
	 * 
	 * @param procName
	 * @param actName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActivityInfo> listRejectableFromActivity(String procName, String actName) throws ServiceRequestException;

	/**
	 * 查询指定活动的后续可执行活动
	 * 
	 * @param procName
	 * @param actName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActivityInfo> listAcceptaleFromActivity(String procName, String actName) throws ServiceRequestException;

	/**
	 * 查询指定活动的后续不是APPLICATION的可执行活动
	 * 
	 * @param procName
	 * @param actName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActivityInfo> listAcceptaleFromActivityNotApplication(String procName, String actName) throws ServiceRequestException;

	/**
	 * 查询流程中所有活动
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActivityInfo> listAllActivityInfo(String procGuid, String procName) throws ServiceRequestException;

	/**
	 * 查询画流程图所需的信息
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Object> listActivityGraphInfo(String procName) throws ServiceRequestException;

	/**
	 * 列出工作流模型的所有生命周期阶段
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowLifecyclePhaseInfo> listLifecyclePhaseInfo(String procguid, String procName) throws ServiceRequestException;

	/**
	 * 查询流程中所有变迁
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowTransitionInfo> listTransitionInfo(String procGuid, String procName) throws ServiceRequestException;

	/**
	 * 查询模型阶段转换设置信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowPhaseChangeInfo> listPhaseChangeInfo(String processguid, String activityguid) throws ServiceRequestException;

	/**
	 * 查询活动节点阶段转换设置信息(简单bean)
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActrtLifecyclePhaseInfo> listActrtPhaseChangeInfo(String processguid, String activityguid) throws ServiceRequestException;

	/**
	 * 查询活动模型阶段转换设置信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActrtStatusInfo> listStatusChangeInfo(String processguid, String activityguid) throws ServiceRequestException;

	/**
	 * 查询活动节点动作事件设置信息
	 * 
	 * @param processGuid
	 * @param activityGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActrtActionInfo> listActrtAction(String processGuid, String activityGuid) throws ServiceRequestException;

	/**
	 * 查询流程中人为活动的所有变迁
	 * 包括begin,end,route,manual,subprocess
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<GraphTransition> listGraphTransition(String procName) throws ServiceRequestException;

	/**
	 * 查询流程中可执行的活动定义
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActivityInfo> listPerformableActivity(String procName) throws ServiceRequestException;

	/**
	 * 得到指定工作流排序后的关卡列表（人为活动和通知活动）
	 * 列表中对象为WorkflowActivity
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowActivityInfo> listSortedPerformableActivity(String procName) throws ServiceRequestException;

	/**
	 * 取得可用的工作流模板信息
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowTemplateInfo> listRunnableProcessTemplate(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 取得可用的工作流模板信息
	 * 
	 * @param wfName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowTemplateInfo> listRunnableProcessTemplate(String wfName) throws ServiceRequestException;

	/**
	 * 查询流程模板
	 * 
	 * @param procName
	 * @param perfGuid
	 * @return 模板列表
	 * @throws ServiceRequestException
	 */
	public List<ApproveTemplate> listProcessTemplate(String procName, String perfGuid) throws ServiceRequestException;

	/**
	 * 查询流程模板明细
	 * 
	 * @param templateGuid
	 *            模板guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ApproveTemplateDetail> listProcessTemplateDetail(String templateGuid) throws ServiceRequestException;

	/**
	 * 查询流程模板明细
	 * 
	 * @param templateGuid
	 *            模板guid
	 * @param actName
	 *            活动名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ApproveTemplateDetail> listProcessTemplateDetailByActivityName(String templateGuid, String actName) throws ServiceRequestException;

	/**
	 * 查詢流程所有的人为活动列表
	 * 
	 * @param procName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<SystemWorkflowActivity> listManualActivity(String procName) throws ServiceRequestException;

	/**
	 * 查询能使用的指定工作流的class列表
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ClassInfo> listClassByProcName(String procName) throws ServiceRequestException;

	/**
	 * 查询能使用的指定工作流的BOInfo列表
	 * 
	 * @param procName
	 *            流程名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> listBOInfoByProcName(String procName, String bmGuid) throws ServiceRequestException;

	/**
	 * 查詢工作流模型所有的事件
	 * 
	 * @param procName
	 * @param bmGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowEventInfo> listEventInfo(String procGuid, String procName) throws ServiceRequestException;

	/**
	 * 得到指定的生命周期和指定阶段的WorkflowProcessInfo列表
	 * 
	 * @param LifecycleName
	 * @param phaseName
	 * @return
	 */
	public List<WorkflowProcessInfo> getPhaseProcessList(String lifecycleName, String phaseName) throws ServiceRequestException;

}
