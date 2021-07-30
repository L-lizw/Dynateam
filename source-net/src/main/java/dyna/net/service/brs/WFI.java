/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WFM Workflow Management 工作流管理
 * Wanglei 2010-11-3
 */
package dyna.net.service.brs;

import java.util.Map;
import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.extra.ProcessSetting;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.bean.model.wf.template.WorkflowTemplateAct;
import dyna.common.bean.model.wf.template.WorkflowTemplateVo;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.wft.WorkflowActivityRuntimeData;
import dyna.common.dto.template.wft.WorkflowTemplateActAdvnoticeInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassRelationInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActClassUIInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActCompanyInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActInfo;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;
import dyna.common.dto.template.wft.WorkflowTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeBoInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeRTInfo;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.GraphRuntimeTransition;
import dyna.common.dto.wf.Performer;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.dto.wf.ProcAttachSetting;
import dyna.common.dto.wf.ProcTrack;
import dyna.common.dto.wf.ProcTrackAttach;
import dyna.common.dto.wf.ProcTrackComm;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.dto.wf.WFRelationSet;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.net.service.Service;

/**
 * Workflow Instance 工作流程实例
 * 
 * @author Wanglei
 * 
 */
public interface WFI extends Service
{
	public static final String	SAVE_ATTACH_ERROR			= "SAVEATTACHERROR";
	public static final String	CHANGEPHASE_ERROR			= "CHANGEPHASEERROR";
	public static final String	RELEASE_PROCESS_MODEL_NAME	= "RELEASE";

	/**
	 * 判断指定实例是否在运行的流程中
	 * 
	 * @param objectGuid
	 * @return 运行的流程guid
	 * @throws ServiceRequestException
	 */
	public String isInRunningProcess(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 查询实例对象执行过的流程
	 * 
	 * @param objectGuid
	 *            对象的guid
	 * @param searchCondition
	 *            查询条件, 用于排序和分页
	 * @return 流程实例列表
	 * @throws ServiceRequestException
	 */
	public List<ProcessRuntime> listProcessRuntimeOfObject(ObjectGuid objectGuid, SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 查询实例对象执行过的流程
	 * 
	 * @param searchCondition
	 *            查询条件
	 * @return 流程实例列表
	 * @throws ServiceRequestException
	 */
	public List<ProcessRuntime> listProcessRuntime(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 查询实例对象执行过的流程
	 * 
	 * @param paramMap
	 *            查询条件
	 * @return 流程实例列表
	 * @throws ServiceRequestException
	 */
	public List<ProcessRuntime> listProcessRuntime(Map<String, Object> paramMap) throws ServiceRequestException;

	/**
	 * 取得一次审批通过的流程
	 * 
	 * @param paramMap
	 *            查询条件
	 * @return 流程实例列表
	 * @throws ServiceRequestException
	 */
	public List<ProcessRuntime> listFristPassApprovalProcessRuntime(Map<String, Object> paramMap) throws ServiceRequestException;

	/**
	 * 查询运行时活动
	 * 
	 * @param actRtGuid
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime getActivityRuntime(String actRtGuid) throws ServiceRequestException;

	/**
	 * 查询开始活动
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime getBeginActivityRuntime(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询流程，当前的活动
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listCurrentActivityRuntime(String procRtGuid) throws ServiceRequestException;

	/**
	 * 获取流程实例
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProcessRuntime getProcessRuntime(String procRtGuid) throws ServiceRequestException;

	/**
	 * 获取实例当前正在处理的最新流程
	 * 
	 * @param instanceGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProcessRuntime getInstanceLatestProcessRuntime(String instanceGuid) throws ServiceRequestException;

	/**
	 * 创建流程实例
	 * 
	 * @param wfTemplateGuid
	 *            流程模板guid
	 * @param parentProcGuid
	 *            父流程guid(创建子流程时)
	 * @param parentActGuid
	 *            父流程活动节点的guid(创建子流程时)
	 * @param description
	 *            流程描述
	 * @param attachSettings
	 *            附件信息
	 * @return 创建的流程实例
	 * @throws ServiceRequestException
	 */
	public ProcessRuntime createProcess(String wfTemplateGuid, String parentProcGuid, String parentActGuid, String description, ProcAttach... procAttach)
			throws ServiceRequestException;

	/**
	 * 修改创建状态的流程信息, 非创建状态的流程不可修改
	 * 
	 * @param settings
	 *            流程相关信息,请参考<code>dyna.common.bean.extra.ProcessSetting</code>的内容
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProcessRuntime saveProcess(String procRtGuid, ProcessSetting settings) throws ServiceRequestException;

	/**
	 * 添加附件到流程
	 * 
	 * @param procRtGuid
	 *            流程guid
	 * @param attachSettings
	 *            附件的设置信息
	 * @return 返回添加不成功的附件
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> addAttachment(String procRtGuid, ProcAttach... procAttach) throws ServiceRequestException;

	/**
	 * 删除流程中的附件
	 * 
	 * @param procRtGuid
	 *            流程guid
	 * @param procAttachGuidList
	 *            附件的设置信息
	 * @throws ServiceRequestException
	 */
	public void removeAttachment(String procRtGuid, List<String> procAttachGuidList) throws ServiceRequestException;

	/**
	 * 发布流程 （仅用于发布附件）
	 * 
	 * @param attachObjectGuidList
	 *            附件的objectguid的集合
	 * @throws ServiceRequestException
	 */
	public void releaseProcess(List<ObjectGuid> attachObjectGuidList) throws ServiceRequestException;

	/**
	 * 重新启动流程
	 * 
	 * @param procRtGuid
	 * @param comments
	 * @param settings
	 * 
	 *            操作步骤：
	 *            1.删除流程所有实例和关系
	 *            2.保存流程实例和关系
	 *            3.修改截止时间
	 *            4.删除流程执行人
	 *            5.保存流程执行人
	 *            6.执行流程
	 * @return
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime resumeProcess(String procRtGuid, String comments, ProcessSetting settings) throws ServiceRequestException;

	/**
	 * 执行活动
	 * 
	 * @param actRtGuid
	 *            活动guid
	 * @param comment
	 *            意见
	 * @param decide
	 *            决定
	 * @param rejectToActRtGuid
	 *            拒绝到达的活动guid
	 * @param processSetting
	 *            修改后的執行人
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime performActivityRuntime(String actRtGuid, String comment, DecisionEnum decide, String rejectToActRtGuid, ProcessSetting processSetting)
			throws ServiceRequestException;

	/**
	 * 查询指定活动执行'REJECT'之后将进入的活动
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listRejectableFromActivityRuntime(String actRtGuid) throws ServiceRequestException;

	/**
	 * 查询指定活动执行'ACCEPT'之后将进入的活动
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listAcceptableFromActivityRuntime(String actRtGuid) throws ServiceRequestException;

	/**
	 * 查询指定父流程的所有子流程
	 * 
	 * @param parentProcGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcessRuntime> listSubProcessRuntime(String parentProcGuid) throws ServiceRequestException;

	/**
	 * 查询出所有的画流程图对象的信息
	 * 
	 * @param procRtGuid
	 * @return 两个object对象
	 *         1.所有的GraphRuntimeActivity
	 *         2.GraphRuntimeActivity层级信息
	 * @throws ServiceRequestException
	 */
	public List<Object> listActivityGraphInfo(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询所有画流程图线的信息
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<GraphRuntimeTransition> listGraphTransition(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询活动执行人
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Performer> listPerformer(String actRtGuid) throws ServiceRequestException;

	/**
	 * 活动执行人中查询未执行，此活动的执行人
	 * 前提：此活动未完成，如果完成，返回为空
	 * 
	 * @param actRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> listNotFinishPerformer(String actRtGuid) throws ServiceRequestException;

	/**
	 * 查询附件意见
	 * 
	 * @param procRtGuid
	 * @param actRtGuid
	 * @param attachGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcTrackAttach> listProcAttachComment(String procRtGuid, String actRtGuid, String attachGuid) throws ServiceRequestException;

	/**
	 * 根据附件关联设置计算附件
	 * 
	 * @param procRtGuid
	 * @param settings
	 * @throws ServiceRequestException
	 */
	public void calculateAttach(String procRtGuid, ProcAttachSetting settings) throws ServiceRequestException;

	/**
	 * 
	 * @param procRtGuid
	 * @param mainProcAttachGuid
	 * @param relationSetList
	 * @throws ServiceRequestException
	 */
	public List<BOInfo> calculateEnableAttachBO(String procRtGuid, ObjectGuid attachInstanceObjectGuid, List<WFRelationSet> relationSetList) throws ServiceRequestException;

	/**
	 * 根据附件ObjectGuid查询指定流程附件
	 * 
	 * @param objectGuid
	 *            附件的objectGuid
	 * @param processGuid
	 *            流程guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getAttachment(ObjectGuid objectGuid, String processGuid) throws ServiceRequestException;

	/**
	 * 根据附件ObjectGuid查询指定流程附件(主附件为BOM)
	 * 
	 * @param objectGuid
	 *            附件的objectGuid
	 * @param processGuid
	 *            流程guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOMView getBOMView(ObjectGuid objectGuid, String processGuid) throws ServiceRequestException;

	/**
	 * 查询流程主附件定义列表
	 * 
	 * @param procRtGuid
	 *            流程guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> listProcAttach(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询流程意见列表
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	@Deprecated
	public List<ProcTrack> listComment(String procRtGuid) throws ServiceRequestException;

	/**
	 * 检查对象是否流程被锁定
	 * 
	 * @param attachment
	 * @return 锁定该对象的流程guid
	 * @throws ServiceRequestException
	 */
	public String isLock(ObjectGuid attachment) throws ServiceRequestException;

	/**
	 * 得到指定工作流排序后的关卡列表（人为活动和通知活动）
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listSortedPerformableActivityRuntime(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询活动"REJECT"后到达的可执行(MANUAL/BEGIN/END)活动
	 * 
	 * @param actRtGuid
	 *            活动guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listRejectedDestinedActivityRuntime(String actRtGuid) throws ServiceRequestException;

	/**
	 * 检查是否流程活动的执行者
	 * 
	 * @param procRtGuid
	 *            流程运行时的guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isPerformerOfProcessRuntime(String procRtGuid) throws ServiceRequestException;

	/**
	 * 保存常用语
	 * 
	 * @param trackComm
	 * @return
	 * @throws ServiceRequestException
	 */
	public ProcTrackComm saveTrackComm(ProcTrackComm trackComm) throws ServiceRequestException;

	/**
	 * 删除常用语
	 * 
	 * @param trackCommGuid
	 * @throws ServiceRequestException
	 */
	public void deleteTrackComm(String trackCommGuid) throws ServiceRequestException;

	/**
	 * 查询所有的常用语
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcTrackComm> listProcTrackComm() throws ServiceRequestException;

	/**
	 * 取子流程数
	 * 
	 * @param parentProcGuid
	 * @param parentRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public Integer getSubFlowSize(String parentProcGuid, String parentRtGuid) throws ServiceRequestException;

	/**
	 * 撤销流程
	 * 
	 * 先将流程 ，状态改为cancel
	 * 后将流程所有附件，还原到进入流程的状态
	 * 
	 * @param procRtGuid
	 * @param actrtGuid
	 * @throws ServiceRequestException
	 */
	public void recallProcessRuntime(String procRtGuid, String actrtGuid) throws ServiceRequestException;

	/**
	 * 添加附件意见
	 * 
	 * @param procRtGuid
	 * @param actRtGuid
	 * @param attchGuid
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void addAttachOpinion(String procRtGuid, String actRtGuid, String attchGuid, ProcTrackAttach attachOpinion) throws ServiceRequestException;

	/**
	 * 修改附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void updateAttachOpinion(ProcTrackAttach attachOpinion) throws ServiceRequestException;

	/**
	 * 保存工作流模板，包含创建（guid为空）
	 * 
	 * @param workflowTemplate
	 * @return
	 */
	public WorkflowTemplateVo saveWorkflowTemplateInDetail(WorkflowTemplateVo workflowTemplateVo) throws ServiceRequestException;

	/**
	 * 取得工作流模板所有信息
	 * 
	 * @param workflowTemplateGuid
	 * @return
	 */
	public WorkflowTemplateVo getWorkflowTemplateDetail(String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 通过工作流模板ID取得工作流模板所有信息
	 * 
	 * @param workflowTemplateId
	 * @return
	 */
	public WorkflowTemplateVo getWorkflowTemplateInDetailById(String workflowTemplateId) throws ServiceRequestException;

	/**
	 * 取得工作流模板基本信息
	 * 
	 * @param workflowTemplateGuid
	 * @return WorkflowTemplateInfo
	 */
	public WorkflowTemplateInfo getWorkflowTemplateInfo(String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 取得工作流模型详细信息
	 * 
	 * @param workflowTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowTemplate getWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 取得工作流模板中活动节点的基本设置信息
	 * 
	 * @param workflowTemplateGuid
	 * @param actrtName
	 * @return
	 */
	public WorkflowTemplateActInfo getWorkflowTemplateActSetInfo(String workflowTemplateGuid, String actrtName) throws ServiceRequestException;

	/**
	 * 取得工作流模板中活动节点的详细设置信息
	 * 
	 * @param workflowTemplateGuid
	 * @param actrtName
	 * @return
	 */
	public WorkflowTemplateAct getWorklfowTemplateActSet(String workflowTemplateGuid, String actrtName) throws ServiceRequestException;

	/**
	 * 列出所有执行人，包括 指定执行人，执行人范围，完成通知人，超时截止日期通知人明细设置，日期通知人设置
	 * 
	 * @param wfTemplateActGuid
	 * @return
	 */
	public List<WorkflowTemplateActPerformerInfo> listAllPerformerOfAct(String wfTemplateActGuid) throws ServiceRequestException;

	/**
	 * 列出所有执行人，包括 观察人，使用人，启动通知人，结束通知人
	 * 
	 * @param wfTemplateActGuid
	 * @return
	 */
	public List<WorkflowTemplateActPerformerInfo> listAllPerformerOfTemplate(String wfTemplateGuid) throws ServiceRequestException;

	/**
	 * 工作流模板节点指定类型的执行人：
	 * 
	 * @param noticeType
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECUTOR->指定执行人
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTEXECSCOPE->范围执行人
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTFIN-》完成通知人
	 *            WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF-》超时通知人
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_ACTRTADV-》将要截至通知人
	 * @return
	 */
	public List<WorkflowTemplateActPerformerInfo> listPerFormerOfActByType(String wfTemplateActGuid, String noticeType) throws ServiceRequestException;

	/**
	 * 获取工作流模板指定类型的执行人：
	 * 
	 * @param noticeType
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_OBSERVER->观察人
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_ORIGINATOR->使用人
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_OPENNOTICE-》启动通知人
	 *            WorkflowTemplateActPerformerInfo.NOTICETYPE_CLOSENOTICE-》结束通知人
	 * @return
	 */
	public List<WorkflowTemplateActPerformerInfo> listPerFormerOfTemplateByType(String wfTemplateGuid, String noticeType) throws ServiceRequestException;

	/**
	 * 获取工作流模板节点的：完成通知设置,超时截止日期通知人明细设置,将截止日期通知人设置
	 * 
	 * @param wfTemplateActGuid
	 * @param noticeType
	 * @return
	 */
	public Map<String, WorkflowTemplateActAdvnoticeInfo> getAllAdvnoticeSetInfoOfAct(String wfTemplateActGuid) throws ServiceRequestException;

	/**
	 * 获取工作流模板的：开始流程通知设置，结束流程通知设置
	 * 
	 * @param wfTemplateActGuid
	 * @param noticeType
	 * @return
	 */
	public Map<String, WorkflowTemplateActAdvnoticeInfo> getAllAdvnoticeSetInfoOfTemplate(String wfTemplateGuid) throws ServiceRequestException;

	/**
	 * 获取工作流模板节点的通知設置
	 * 
	 * @param wfTemplateActGuid
	 * @param noticeType
	 *            WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTFIN->完成通知设置
	 *            WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTDEF->超时截止日期通知人明细设置
	 *            WorkflowTemplateActAdvnoticeInfo.NOTICETYPE_ACTRTADV->将截止日期通知人设置
	 * @return
	 */
	public WorkflowTemplateActAdvnoticeInfo getAdvnoticeSetInfoByType(String wfTemplateActGuid, String noticeType) throws ServiceRequestException;

	/**
	 * 列出可发起此模板的bo
	 * 
	 * @param wfTemplate
	 * @return
	 */
	public List<WorkflowTemplateScopeBoInfo> listScopeBoCanLaunchOfTemplate(String wfTemplateguid) throws ServiceRequestException;

	/**
	 * 列出可用此模板的bo
	 * 
	 * @param wfTemplate
	 * @return
	 */
	public List<WorkflowTemplateScopeBoInfo> listScopeBoOfTemplate(String wfTemplateguid) throws ServiceRequestException;

	/**
	 * 列出模板的关联审批关系
	 * 
	 * @param wfTemplateguid
	 * @return
	 */
	public List<WorkflowTemplateScopeRTInfo> listScoperRelationTemplateOfTemplate(String wfTemplateguid) throws ServiceRequestException;

	/**
	 * 获取工作流模板的节点的erp工厂设置
	 * 
	 * @param wfTemplateActGuid
	 * @return
	 */
	public List<WorkflowTemplateActCompanyInfo> listWorkflowTemplateActCompanyInfoOfAct(String wfTemplateActGuid) throws ServiceRequestException;

	/**
	 * 根据guid获取工作流模板节点对应业务的设置信息
	 * 
	 * @return
	 */
	public WorkflowTemplateActClassInfo getWorkflowTemplateActClassInfo(String actClassguid) throws ServiceRequestException;

	/**
	 * 工作流模板节点class对应的可修改ui
	 * 
	 * @return
	 */
	public List<WorkflowTemplateActClassUIInfo> listUIInfoOfWfActClass(String actClassguid) throws ServiceRequestException;

	/**
	 * 工作流模板节点class对应的可修改的关联关系
	 * 
	 * @return
	 */
	public List<WorkflowTemplateActClassRelationInfo> listRelationInfoOfWfActClass(String actClassguid) throws ServiceRequestException;

	/**
	 * 取得工作流模板中活动节点的class基本设置信息
	 * classguid,classname,二者设其一
	 * 
	 * @param workflowTemplateGuid
	 *            工作流模板guid
	 * @param workflowActrtName
	 *            工作流节点name
	 * @param classGuid
	 * @param className
	 * @param isCurrentBM
	 * @param templateBMGuid
	 * @return
	 */
	public WorkflowTemplateActClassInfo getWorkflowTemplateActClassSetInfo(String workflowTemplateGuid, String workflowActrtName, String classGuid, String className,
			boolean isCurrentBM, String templateBMGuid) throws ServiceRequestException;

	/**
	 * 取得工作流模板中特定活动节点已经设置的class基本信息
	 * 
	 * @param workflowTemplateGuid
	 *            工作流模板guid
	 * @param actrtName
	 *            活动名称
	 * @return
	 */
	public List<WorkflowTemplateActClassInfo> listWorkflowTemplateActClassSetInfoSingle(String workflowTemplateGuid, String actrtName) throws ServiceRequestException;

	/**
	 * 通过流程名称取得工作流模板列表,列表均爲模板基本信息
	 * 
	 * @param workFlowName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowTemplateInfo> listWorkflowTemplateInfoByWFNameWithOutObserver(String workFlowName) throws ServiceRequestException;

	/**
	 * 通过模板ID取得工作流模板基本信息
	 * 
	 * @param workflowTemplateId
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowTemplateInfo getWorkflowTemplateInfoById(String workflowTemplateInfoId) throws ServiceRequestException;

	/**
	 * 取得已在工作流模板中定义的所有的工作流名称
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listWorkflowName() throws ServiceRequestException;

	/**
	 * 取得已在工作流模板中定义的所有的工作流名称,包含废弃
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> listWorkflowNameContainObsolete() throws ServiceRequestException;

	/**
	 * 根据类型获取已知end1ObjectGuid对象上关联的所有结构
	 * 不判断权限
	 * 
	 * @param end1ObjectGuid
	 *            end1对象的ObjectGuid
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的StructureObject对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<StructureObject> listObjectOfRelation(ObjectGuid end1ObjectGuid, String viewName, SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 根据关联关系的objectGuid获取Relation对象
	 * 不判断权限
	 * 
	 * @param objectGuid
	 *            关联关系的objectGuid对象
	 * @return ViewObject
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public ViewObject getRelation(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 查询产品中Summary数据列表
	 * 不判断权限
	 * 
	 * @param productObjectGuid
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listProductSummaryObject(ObjectGuid productObjectGuid, SearchCondition searchCondition, String relationTemplateName)
			throws ServiceRequestException;

	/**
	 * 上传对象预览类型文件, 目前仅支持图片格式的文件(.gif, .jpg, .jpeg, .bmp, .png)
	 * 流程中不判断权限
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans uploadPreviewFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 添加文件附加到业务对象
	 * 流程中不判断权限
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo attachFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 获取end1对象的上的所有BOM视图
	 * 流程中不判断权限
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @return BOMView列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<BOMView> listBOMView(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 获取已知view对象上关联的所有结构
	 * 流程中不判断权限
	 * 
	 * @param viewObject
	 *            view对象的ObjectGuid
	 * @param searchCondition
	 *            这个是装饰字段用的 主要用于装饰返回结果对象中的Code类型和Object类型的字段<br>
	 *            可以传空 也可以用createSearchConditionForStructure生成
	 * @return 关联的StructureObject对象列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<StructureObject> listObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 获取指定end1对象的所有的Relation,不包含内置关系
	 * 流程中不判断权限
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @return Relation列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<ViewObject> listRelationWithOutBuiltIn(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 获取指定end1对象的所有的Relation,
	 * 流程中不判断权限
	 * 
	 * @param end1ObjectGuid
	 *            主件(END1) objectguid
	 * @return Relation列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及装饰异常(DecorateException)
	 */
	public List<ViewObject> listRelation(ObjectGuid end1ObjectGuid) throws ServiceRequestException;

	/**
	 * 根据关联关系的objectGuid及关联关系的名字或者对应关系模板的名字获取ViewObject对象
	 * 流程中不判断权限
	 * 
	 * @param end1ObjectGuid
	 *            关联关系的ObjectGuid对象
	 * @param name
	 *            ViewObject的名字或者关系模板的名字
	 * 
	 * @return ViewObject
	 * @throws ServiceRequestException
	 */
	public ViewObject getRelationByEND1(ObjectGuid end1ObjectGuid, String name) throws ServiceRequestException;

	/**
	 * 将一个对象end2关联到指定view对象上 <br>
	 * 实际是创建structureObject对象，该对象上有end2和view的关系的结构信息
	 * 流程中不判断权限
	 * 
	 * @param viewObjectGuid
	 *            view对象的ObjectGuid
	 * @param end2FoundationObjectGuid
	 *            要关联的对象的ObjectGuid
	 * @param structureObject
	 *            结构关系的对象
	 * @param procRtGuid
	 *            流程guid
	 * @return 创建出来的关联结构关系
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public StructureObject link(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject, String procRtGuid) throws ServiceRequestException;

	/**
	 * 更新明细列表<br>
	 * 流程中不能删除关联, 不判断权限.
	 * 
	 * @param viewObjectGuid
	 *            关系视图 objectguid
	 * @param linkList
	 *            需要新增的end2列表, 其中StructureObject字段存储的是structure信息
	 * @param unlinkList
	 *            需要删除的end2列表
	 * @param updateList
	 *            需要更新的end2列表
	 * @param procRtGuid
	 *            流程guid
	 * @throws ServiceRequestException
	 */
	public void saveStructure4Detail(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList,
			String procRtGuid) throws ServiceRequestException;

	/**
	 * 更新对象 <br>
	 * 判断有没有必填字段没填值 如果存在 直接抛异常信息<br>
	 * 其中ID，生命周期阶段，masterName，拥有者等信息不可用此方法修改
	 * 
	 * @param foundationObject
	 *            要更新的对象
	 * @param procRtGuid
	 *            流程guid 在流程中SystemStatusEnum.PRE 为可编辑
	 * @return 更新以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject saveObject(FoundationObject foundationObject, String procRtGuid) throws ServiceRequestException;

	/**
	 * 更新对象, 修改的fo为明细, 部分/工厂关系的end2则不需要检出检入操作
	 * 否则等同于方法<code>saveObject(FoundationObject)</code><br>
	 * 
	 * @param foundationObject
	 *            要更新的对象
	 * @param structureModel
	 *            RelationTemplateTypeEnum 类型
	 * @param procRtGuid
	 *            流程guid 在流程中SystemStatusEnum.PRE 为可编辑
	 * @return 更新以后的对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FoundationObject saveObject(FoundationObject foundationObject, RelationTemplateTypeEnum structureModel, String procRtGuid) throws ServiceRequestException;

	/**
	 * 添加/更新一个关联关系对象<br>
	 * 更新时：只有WIP状态的关系才能被更新<br>
	 * 创建时如果不是用模板创建那么ID为VIEW_APPOINTED_ID = "32AA50EA06FA4D4096E454A063BAA2AF";<br>
	 * 如果是用模板创建 那么ID及NAME取自模板的ID及NAME
	 * 
	 * @param relation
	 *            关联关系的对象
	 * @param procRtGuid
	 *            流程guid
	 * @return 刚添加/更新的Relation对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ViewObject saveRelation(ViewObject relation, String procRtGuid) throws ServiceRequestException;

	/**
	 * 根据模板创建 一个关联关系对象<br>
	 * 一个模板在一个对象上只能创建一个relation,如果重复创建报异常<br>
	 * 创建relation的时候会把模板的ID与NAME赋值给relation做ID和NAME
	 * 
	 * @param relationTemplateGuid
	 *            关联关系模板的guid
	 * @param end1ObjectGuid
	 *            要关联的对象的ObjectGuid
	 * @param procRtGuid
	 *            流程guid
	 * @return 刚创建的Relation对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及重复创建异常(ID_APP_MULTI_CREATE_RELATION)
	 */
	public ViewObject saveRelationByTemplate(String relationTemplateGuid, ObjectGuid end1ObjectGuid, String procRtGuid) throws ServiceRequestException;

	/**
	 * 通过流程名称取得工作流模板列表,不包含观察人信息等，
	 * 仅有工作流模板基本信息,包含废弃
	 * 
	 * @param workFlowName
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<WorkflowTemplateInfo> listWorkflowTemplateInfoByWFNameContainObsolete(String workFlowName) throws ServiceRequestException;

	/**
	 * 到得模板后，去掉其所有的guid(用相同信息创建使用)
	 * 因为缓存中同一个对象，所有得到后 执行clearForCreate();方法 自行清空
	 * 
	 * @param workflowTemplateGuid
	 *            原工作流模板
	 * @return 创建后的工作流模板
	 * @throws ServiceRequestException
	 */
	public WorkflowTemplateVo copyWorkflowTemplateWithOutGuid(String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 废弃工作流模板
	 * 
	 * @param workflowTemplateGuid
	 * @throws ServiceRequestException
	 */
	public void obsoleteWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 通过end1,template与end2对象的所有关联关系
	 * 不判断权限
	 * 
	 * @param end1ObjectGuid
	 *            end1对象的ObjectGuid
	 * @param templateName
	 *            模板名
	 * @param end2FoundationObjectGuid
	 *            要解除的对象的ObjectGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlink(StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 从view对象上解除与end2对象的所有关联关系
	 * 不判断权限
	 * 
	 * @param viewObjectGuid
	 *            view对象的ObjectGuid
	 * @param end2FoundationObjectGuid
	 *            要解除的对象的ObjectGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlink(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, String relationTemplateName) throws ServiceRequestException;

	/**
	 * 替换item关联的CAD, 如替换前未有关联, 则建立关联关系<br>
	 * 其中CAD是end1,item从cadFrom的end1上解除关系 然后再关联到cadTo对应的CAD上
	 * 工作流中不判断权限
	 * 
	 * @param cadFrom
	 * @param cadTo
	 * @param itemObjectGuid
	 * @param templateName
	 * @throws ServiceRequestException
	 */
	public void replaceCADByItem(ObjectGuid cadFrom, ObjectGuid cadTo, ObjectGuid itemObjectGuid, String templateName) throws ServiceRequestException;

	/**
	 * 关联物料与图纸对象
	 * 先取得genericItem,再关联，不判断权限
	 * 
	 * @param modelObjectGuid
	 * @param itemObjectGuid
	 * @param templateName
	 * @param isCheckAcl
	 * @throws ServiceRequestException
	 */
	public void linkGenericItemCAD(ObjectGuid modelObjectGuid, ObjectGuid itemObjectGuid, String templateName, String procRtGuid) throws ServiceRequestException;

	/**
	 * 解除物料与CAD的关系
	 * 工作流中不判断权限
	 * 
	 * @param itemObjectGuid
	 *            end1对象的ObjectGuid
	 * @param templateName
	 *            模板名
	 * @param cadObjectGuid
	 *            要解除的对象的ObjectGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void unlinkCAD(ObjectGuid itemObjectGuid, String templateName, ObjectGuid cadObjectGuid) throws ServiceRequestException;

	/**
	 * 根据structure解除关联关系,并且删除end2
	 * 不判断权限
	 * 
	 * @param structureObject
	 * @throws ServiceRequestException
	 */
	public void unlinkAndDeleteEnd2(StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 执行下一个节点
	 * （主要应用于有UI的动作活动节点之后调用）
	 * 
	 * @param activity
	 *            当前节点，一般为action activity
	 * @param decision
	 * @param rejectToActRtGuid
	 * @return 存在UI的Action Activity
	 * @throws ServiceRequestException
	 */
	public ActivityRuntime fireNextActivity(ActivityRuntime activity, DecisionEnum decision, String rejectToActRtGuid) throws ServiceRequestException;

	/**
	 * 删除文件,在工作流中強制刪除
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @throws ServiceRequestException
	 */
	public void detachFile(String fileGuid) throws ServiceRequestException;

	/**
	 * 判断当前用户在当前流程节点中是否能有郊审批
	 * 
	 * @param procRtGuid
	 * @param actRtGuid
	 * @return 是否是有效审批节点
	 */
	public boolean isEffectiveApprovalActivity(String procRtGuid, String actRtGuid) throws ServiceRequestException;

	/**
	 * 取得按顺序所有活动节点，根据活动意见还原历史活动
	 * 并包含每个活动的执行人列表
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ActivityRuntime> listHistoryActivityRuntimeAndPerformer(String procRtGuid) throws ServiceRequestException;

	/**
	 * 取得活动节点的意见，并通过活动开始次数判断同一节点不同时间（有拒绝的情况 ）的意见
	 * 
	 * @param actrtGuid
	 * @param startNumber
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcTrack> listActivityComment(String actrtGuid, String startNumber) throws ServiceRequestException;

	/**
	 * 验证流程中的附件是否有效，返回无效的附件列表
	 * 
	 * @param procRtGuid
	 * @param isCheckAcl
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> verifyValidAttach(String procRtGuid) throws ServiceRequestException;

	/**
	 * 流程中更改附件主对象
	 * 
	 * @param end1ObjectGuid
	 * @param viewName
	 * @param structureObject
	 * @throws ServiceRequestException
	 */
	public void changePrimaryObject(ObjectGuid end1ObjectGuid, String viewName, StructureObject structureObject) throws ServiceRequestException;

	/**
	 * 返回无效附件
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> listInValidAttach(String procRtGuid) throws ServiceRequestException;

	/**
	 * 取得流程附件,包含无效附件
	 * 
	 * @param procRtGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> listProcAttachContainInValid(String procRtGuid) throws ServiceRequestException;

	/**
	 * 保存执行人
	 * 
	 * @param procRtGuid
	 * @param settings
	 * @throws ServiceRequestException
	 */
	public void savePerformer(String procRtGuid, ProcessSetting settings) throws ServiceRequestException;

	/**
	 * 添加执行人
	 * 
	 * @param procRtGuid
	 * @param settings
	 * @throws ServiceRequestException
	 */
	public void addPerformer(String procRtGuid, ProcessSetting settings) throws ServiceRequestException;

	/**
	 * 检验执行人是否必填
	 * 
	 * @param actrtGuid
	 * @param rejectToActRtGuid
	 * @param isAllActivity
	 *            是否验证所有活动结点。启动流程时，模板中设置必须要有执行人时，则为true,否则为false
	 * @throws ServiceRequestException
	 */
	public void checkPerform(String actrtGuid, String rejectToActRtGuid, boolean isAllActivity) throws ServiceRequestException;

	/**
	 * 删除附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	public void deleteAttachOpinion(String attachOpinion) throws ServiceRequestException;

	/**
	 * 取得子流程附件，通过invalid 标识，是否适用此子流程(过滤boinfo)
	 * 
	 * @param procRtGuid
	 * @param workflowTemplateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcAttach> listSubProcAttach(String procRtGuid, String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 删除流程
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void deleteProcess(ProcessRuntime processRuntime) throws ServiceRequestException;

	public FoundationObject openObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 移除状态为变更中的附件
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void removeECPAttachment(String procRtGuid) throws ServiceRequestException;

	/**
	 * 废弃组、角色、用户
	 * 
	 * @param guid
	 * @param perfTypeEnum
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProcessRuntime> queryActrtProcess(String guid, PerformerTypeEnum perfTypeEnum) throws ServiceRequestException;
	
	/**
	 * 查询用户是否是执行人
	 * 
	 */
	public List<Performer> queryPerformer(String guid) throws ServiceRequestException;

	/**
	 * 根据用户guid和流程活动节点查询出所有的被代理人
	 * 
	 * @param agentGuid
	 * @param actrtGuid
	 * @return
	 */
	public List<String> getPrincipalsOfAgent(String agentGuid, String actrtGuid) throws ServiceRequestException;

	/**
	 * 根据用户guid和流程活动节点查询出所有未处理的被代理人
	 * 
	 * @param agentGuid
	 * @param actrtGuid
	 * @return
	 */
	public List<String> getNotFinishedPrincipalsOfAgent(String agentGuid, String actrtGuid) throws ServiceRequestException;

	/**
	 * 判断当前用户是否是当前活动节点的执行人的代理人
	 * 
	 * @param agentGuid
	 * @param actrtGuid
	 * @return
	 */
	public boolean isAgentPerformerOfActrt(String agentGuid, String actrtGuid) throws ServiceRequestException;

	/**
	 * 发送邮件
	 * 
	 * @param listPerformer
	 * @param activity
	 * @param messageType
	 * @throws ServiceRequestException
	 */
	public void sendPerformMail(List<Performer> listPerformer, ActivityRuntime activity, MailMessageType messageType) throws ServiceRequestException;

	/**
	 * 取得工作流当前活动节点相关的数据
	 * 
	 * @param procRtGuid
	 * @param actRuntimeGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowActivityRuntimeData getWorkflowRuntimeData(String procRtGuid, String actRuntimeGuid) throws ServiceRequestException;

	/**
	 * 取得当前对象正在处理中的活动节点
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public WorkflowActivityRuntimeData getWorkflowRuntimeData(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 重新启用废弃的工作流模板
	 * 
	 * @param workflowTemplateGuid
	 * @throws ServiceRequestException
	 */
	public WorkflowTemplate reUseWorkflowTemplate(String workflowTemplateGuid) throws ServiceRequestException;

	/**
	 * 是否是当前流程的执行人
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public boolean isCurrentActivityApprover(String procRtGuid, String actrtName) throws ServiceRequestException;

}
