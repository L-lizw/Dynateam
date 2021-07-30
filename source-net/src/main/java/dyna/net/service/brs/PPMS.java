/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PPMS PROJECT PROCESS MANAGER SERVICE 项目过程管理
 * WagnLHB 2013-10-14
 */
package dyna.net.service.brs;

import java.util.Date;
import java.util.Map;
import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.Deliverable;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.bean.data.ppms.LaborHourConfig;
import dyna.common.bean.data.ppms.MessageRule;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.RptDeliverable;
import dyna.common.bean.data.ppms.RptDeptStat;
import dyna.common.bean.data.ppms.RptManagerStat;
import dyna.common.bean.data.ppms.RptMilestone;
import dyna.common.bean.data.ppms.RptProject;
import dyna.common.bean.data.ppms.RptTask;
import dyna.common.bean.data.ppms.RptTaskDeptStat;
import dyna.common.bean.data.ppms.RptTaskExecStateStat;
import dyna.common.bean.data.ppms.RptTaskExecutorStat;
import dyna.common.bean.data.ppms.RptWorkItem;
import dyna.common.bean.data.ppms.RptWorkItemDeptStat;
import dyna.common.bean.data.ppms.RptWorkItemExecStateStat;
import dyna.common.bean.data.ppms.RptWorkItemExecutorStat;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.UpdateRemark;
import dyna.common.bean.data.ppms.UpdateTaskStatus;
import dyna.common.bean.data.ppms.indicator.DefineIndicator;
import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;
import dyna.common.bean.data.ppms.indicator.chart.IndicatorView;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainUpdateBean;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.IndicatorTimeRangeEnum;
import dyna.common.systemenum.ppms.WorkItemAuthEnum;
import dyna.net.service.Service;

/**
 * PPMS PROJECT PROCESS MANAGER SERVICE 项目过程管理
 * 
 * @author WagnLHB
 * 
 */
public interface PPMS extends Service
{

	/**
	 * 取得项目管理系统配置中的项目配置的项目类型列表
	 * 
	 * @return 项目类型
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listProjectType() throws ServiceRequestException;

	/**
	 * 新建、保存项目管理系统配置中的项目配置的项目类型
	 * 
	 * @param projectType
	 * @return 项目类型
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveProjectType(FoundationObject projectType) throws ServiceRequestException;

	/**
	 * 取得项目管理系统配置中的项目配置的项目类型
	 * 
	 * @param projectTypeGuid
	 * @return 项目类型
	 * @throws ServiceRequestException
	 */
	public FoundationObject getProjectType(String projectTypeGuid, String classGuid) throws ServiceRequestException;

	/**
	 * 删除项目管理系统配置中的项目配置的项目类型
	 * 同时删除对应的里程碑
	 * 
	 * @param projectTypeGuid
	 * @throws ServiceRequestException
	 */
	public void delProjectType(ObjectGuid projectTypeObjectGuid) throws ServiceRequestException;

	/**
	 * 取得项目类型角色
	 * 
	 * @param projectRoleGuid
	 * @return 项目类型角色
	 * @throws ServiceRequestException
	 */
	public ProjectRole getProjectTypeRole(String projectRoleGuid) throws ServiceRequestException;

	/**
	 * 通过类型guid取得关卡
	 * 
	 * @param typeGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<CheckpointConfig> listCheckpointConfigByTypeGuid(String typeGuid) throws ServiceRequestException;

	/**
	 * 保存关卡(系统关卡)
	 * 
	 * @param checkpoint
	 * @return
	 * @throws ServiceRequestException
	 */
	public CheckpointConfig saveCheckpointConfig(CheckpointConfig checkpoint) throws ServiceRequestException;

	/**
	 * 通过里程碑关卡guid删除关卡
	 * 
	 * @param checkpointGuid
	 * @throws ServiceRequestException
	 */
	public void delCheckpointConfig(String checkpointGuid) throws ServiceRequestException;

	/**
	 * 取得工时参数
	 * 
	 * @return 工时参数
	 * @throws ServiceRequestException
	 */
	public LaborHourConfig getWorkTimeConfig() throws ServiceRequestException;

	/**
	 * 保存工时参数
	 * 
	 * @param laborHour
	 * @return 工时参数
	 * @throws ServiceRequestException
	 */
	public LaborHourConfig saveWorkTimeConfig(LaborHourConfig laborHour) throws ServiceRequestException;

	/**
	 * 取得项目通知规则
	 * 
	 * @throws ServiceRequestException
	 */
	public List<MessageRule> listProjectNotifyRule() throws ServiceRequestException;

	/**
	 * 保存项目通知规则
	 * 
	 * @throws ServiceRequestException
	 */
	public List<MessageRule> saveProjectNotifyRule(List<MessageRule> messageRule) throws ServiceRequestException;

	/**
	 * 取得项目通知规则
	 * 
	 * @throws ServiceRequestException
	 */
	public MessageRule getProjectNotifyRule(String messagetype) throws ServiceRequestException;

	/**
	 * 项目管理主页
	 */
	/**
	 * 创建项目
	 * 
	 * @param pmFoundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject createProject(FoundationObject pmFoundationObject) throws ServiceRequestException;

	/**
	 * 删除项目
	 * 
	 * @param pmFoundationObject
	 * @throws ServiceRequestException
	 */
	public void deleteProject(FoundationObject pmFoundationObject) throws ServiceRequestException;

	public void deleteAllTask(ObjectGuid projectObjectGuid) throws ServiceRequestException;
	
	/**
	 * 清空任务上的关联项目字段
	 * 
	 * @param projectObjectGuid
	 * @throws ServiceRequestException
	 */
	public void clearRelationProject(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	/**
	 * 查询我的项目列表
	 * 
	 * 
	 * 
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listProject(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 查询任务列表
	 * 
	 * 
	 * 
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listTask(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 获取工作项BO
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public BOInfo getWorkItemBoinfo() throws ServiceRequestException;

	/**
	 * 根据搜索条件搜索工作项
	 * 
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listWorkItem(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 启动工作项
	 * 
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject startWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException;

	/**
	 * 完成工作项
	 * 
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject completeWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException;

	/**
	 * 暂停工作项
	 * 
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject pauseWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException;

	/**
	 * 取消工作项
	 * 
	 * @param projectObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject cancelWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException;

	/**
	 * 保存工作项
	 * 
	 * @param workItemFou
	 *            工作项实例
	 * @param listTaskMember
	 *            资源对象
	 * @param isCommit
	 *            是否提交:true:保存并提交;false:保存不提交
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveWorkItem(FoundationObject workItemFou, List<TaskMember> listTaskMember, boolean isCommit) throws ServiceRequestException;

	/**
	 * 批量删除工作项，符合条件删除，不符合的返回异常
	 * 
	 * @param listObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public void deleteWorkItem(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 通过更新状态guid取得对应的备注
	 * 
	 * @param updateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UpdateRemark> listRemarkByUpdateGuid(String updateGuid) throws ServiceRequestException;

	/**
	 * 保存备注
	 * 
	 * @param updateGuid
	 *            更新状态guid
	 * @param remark
	 *            备注对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public UpdateRemark saveRemark(String updateGuid, UpdateRemark remark) throws ServiceRequestException;

	/**
	 * 删除备注
	 * 
	 * @param remarkGuid
	 * @throws ServiceRequestException
	 */
	public void deleteRemark(String remarkGuid) throws ServiceRequestException;

	public FoundationObject startProject(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	public FoundationObject completeProject(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	public FoundationObject pauseProject(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	public FoundationObject suspendProject(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	public void deleteTask(FoundationObject taskFoundationObject) throws ServiceRequestException;

	public FoundationObject suspendTask(ObjectGuid taskObjectGuid) throws ServiceRequestException;

	public FoundationObject startTask(ObjectGuid taskObjectGuid) throws ServiceRequestException;

	public FoundationObject pauseTask(ObjectGuid taskObjectGuid) throws ServiceRequestException;

	public FoundationObject completeTask(ObjectGuid taskObjectGuid) throws ServiceRequestException;

	/**
	 * 取得所有交付物
	 * 
	 * @param projectGuid
	 * @throws ServiceRequestException
	 */
	public List<Deliverable> listAllDeliveryByProject(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	/**
	 * 取得所有交付物
	 * 
	 * @param projectGuid
	 * @throws ServiceRequestException
	 */
	public List<Deliverable> listAllDeliveryByTask(ObjectGuid taskObjectGuid) throws ServiceRequestException;

	/**
	 * 取得项目角色中的用户列表
	 * 
	 * @param 项目角色guid
	 * 
	 * @return 角色人员列表
	 * @throws ServiceRequestException
	 */
	public List<RoleMembers> listUserInProjectRole(String projectRoleGuid) throws ServiceRequestException;

	public List<RoleMembers> listUserInProject(String projectGuid) throws ServiceRequestException;

	/**
	 * 删除项目角色
	 * 
	 * @param 项目角色
	 * 
	 * @throws ServiceRequestException
	 */
	public void deleteProjectRole(String projectRoleGuid) throws ServiceRequestException;

	public ProjectRole getProjectRole(String roleGuid) throws ServiceRequestException;;

	/**
	 * 
	 * @throws ServiceRequestException
	 */
	public List<RoleMembers> saveUserInProjectRole(String pmRoleGuid, List<String> userGuidList) throws ServiceRequestException;

	/**
	 * 获取任务资源
	 * 
	 * @param 任务ObjectGuid
	 * @return 任务资源
	 * @throws ServiceRequestException
	 */
	public List<TaskMember> listTaskMember(ObjectGuid taskObjectGuid) throws ServiceRequestException;

	/**
	 * 取得任务下的所有的交付项
	 * 
	 * @return
	 * 
	 * @throws ServiceRequestException
	 */
	public List<DeliverableItem> listDeliveryItem(String taskGuid) throws ServiceRequestException;

	/**
	 * 根据任务交付项取得交付物
	 * 
	 * @return
	 * 
	 * @throws ServiceRequestException
	 */
	public List<Deliverable> listDeliveryByItem(String deliveryItemGuid) throws ServiceRequestException;

	/**
	 * 保存交付项
	 * 
	 * @param deliveryItem
	 * @return 交付项
	 * @throws ServiceRequestException
	 */
	public DeliverableItem saveDeliveryItem(DeliverableItem deliveryItem) throws ServiceRequestException;

	/**
	 * 删除交付项
	 * 
	 * @param deliveryItem
	 * @throws ServiceRequestException
	 */
	public void deleteDeliveryItem(String guid) throws ServiceRequestException;

	/**
	 * 提交交付物
	 * 
	 * @param delivery
	 * @throws ServiceRequestException
	 */
	public void commitDelivery(Deliverable delivery) throws ServiceRequestException;

	/**
	 * 删除交付物
	 * 
	 * @param deliveryGuid
	 * @throws ServiceRequestException
	 */
	public void deleteDelivery(String deliveryGuid) throws ServiceRequestException;

	/**
	 * 查找项目变更单
	 * 
	 * @param projectObjecgGuid
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listChangeRequest(ObjectGuid projectObjecgGuid) throws ServiceRequestException;

	/**
	 * 取得项目变更请求BOInfo
	 * 
	 * @return 项目变更请求BOInfo
	 * @throws ServiceRequestException
	 */
	public BOInfo getPMChangeBoInfo() throws ServiceRequestException;

	/**
	 * 取得任务变更记录BOInfo
	 * 
	 * @return 任务变更记录BOInfo
	 * @throws ServiceRequestException
	 */
	public BOInfo getPMTaskChangeBoInfo() throws ServiceRequestException;

	/**
	 * 创建变更请求对象，开始项目变更
	 * 
	 * @param 项目变更请求
	 * @throws ServiceRequestException
	 */
	public FoundationObject createChangeRequest(FoundationObject foundation) throws ServiceRequestException;

	/**
	 * 完成变更
	 * 
	 * @param 项目变更请求
	 * @throws ServiceRequestException
	 */
	public void completeChangeRequest(FoundationObject foundation) throws ServiceRequestException;

	public FoundationObject getParentTask(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	public List<FoundationObject> listSubTask(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	public BOInfo getPMTaskBoInfo(boolean isTaskTemplate) throws ServiceRequestException;

	/**********************************************
	 * 任务状态更新
	 ********************************************** 
	 */
	/**
	 * 通过任务guid取得所有的状态更新历史
	 * 
	 * @param TaskGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UpdateTaskStatus> listUpdateTaskStatus(String taskGuid) throws ServiceRequestException;

	/**
	 * 保存任务更新状态
	 * 
	 * @param taskStatus
	 * @return
	 * @throws ServiceRequestException
	 */
	public UpdateTaskStatus saveUpdateTaskStatus(UpdateTaskStatus taskStatus) throws ServiceRequestException;

	/**
	 * 通过更新状态guid取得备注
	 * 
	 * @param updateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UpdateRemark> listRemarkByUpdateTaskStatusGuid(String updateGuid) throws ServiceRequestException;

	/**
	 * 保存任务更新的备注
	 * 
	 * @param updateTaskStatusGuid
	 *            状态更新对象Guid
	 * @param remark
	 *            备注对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public UpdateRemark saveUpdateTaskStatusRemark(String updateTaskStatusGuid, UpdateRemark remark) throws ServiceRequestException;

	/**
	 * 通过任务更新备注Guid删除备注对象
	 * 
	 * @param remarkGuid
	 * @throws ServiceRequestException
	 */
	public void deleteUpdateTaskStatusRemark(String remarkGuid) throws ServiceRequestException;

	/**
	 * 保存日历
	 * 1.日历对象已经创建，
	 * 日历例外日期对象中如果guid已经存在，则保存
	 * 如果guid不存在，则新建。
	 * 
	 * 2.关联日历对象，日历例外日期对象
	 * 
	 * @param pmCalendar
	 * @return 保存后的日历对象
	 * @throws ServiceRequestException
	 */
	public PMCalendar saveCalendar(PMCalendar pmCalendar) throws ServiceRequestException;

	/**
	 * 获取取工作日历
	 * 
	 * @param calendarGuid
	 * @return 日历对象
	 * @throws ServiceRequestException
	 */

	public PMCalendar getWorkCalendar(String calendarGuid) throws ServiceRequestException;

	/**
	 * 取得日历
	 * 
	 * @param calendarGuid
	 * @return 日历对象
	 * @throws ServiceRequestException
	 */

	public PMCalendar getWorkCalendarBaseInfo(String calendarGuid) throws ServiceRequestException;
	
	/**
	 * 根据日历编号取得日历对象
	 * 
	 * @param calendarId
	 * @return
	 * @throws ServiceRequestException
	 */
	public PMCalendar getWorkCalendarById(String calendarId) throws ServiceRequestException;

	/**
	 * 废弃日历对象和其下的例外日期对象
	 * 
	 * @param calendarGuid
	 *            日历对象guid
	 * @throws ServiceRequestException
	 */
	public void obsoleteCalendar(String calendarGuid) throws ServiceRequestException;

	/**
	 * 另存日历对象，包括复制例外日期对象
	 * 
	 * @param oriCalendarGuid
	 * @param pmCalendar
	 * @return 另存后的日历对象
	 * @throws ServiceRequestException
	 */
	public PMCalendar saveASCalendar(String oriCalendarGuid, PMCalendar pmCalendar) throws ServiceRequestException;

	/**
	 * 取得公有的日历对象
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<PMCalendar> listCalendar() throws ServiceRequestException;

	/**
	 * 取得公有的日历，包含废弃
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<PMCalendar> listCalendarContainObsolete() throws ServiceRequestException;

	/**
	 * 保存预警
	 * 
	 * @param earlyWarning
	 * @return
	 * @throws ServiceRequestException
	 */
	public EarlyWarning saveEarlyWarning(EarlyWarning earlyWarning) throws ServiceRequestException;

	/**
	 * 删除预警
	 * 
	 * @param warningGuid
	 * @throws ServiceRequestException
	 */
	public void deleteEarlyWarning(String warningGuid) throws ServiceRequestException;

	/**
	 * 取得预警
	 * 
	 * @param warningGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public EarlyWarning getEarlyWarningByGuid(String warningGuid) throws ServiceRequestException;

	/**
	 * 通过项目或这项目模板的Guid取得所有的预警对象
	 * 
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<EarlyWarning> listEarlyWarning(String projectGuid) throws ServiceRequestException;

	/**
	 * 启用预警
	 * 
	 * @param warningGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public EarlyWarning startEarlyWarning(String warningGuid) throws ServiceRequestException;

	/**
	 * 停用预警
	 * 
	 * @param warningGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public EarlyWarning stopEarlyWarning(String warningGuid) throws ServiceRequestException;

	/**
	 * 通过项目guid取得所有启动的预警
	 * 
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<EarlyWarning> listStartEarlyWarning(String projectGuid) throws ServiceRequestException;

	/**
	 * 
	 * 根据项目模板或者项目guid取得角色
	 * 
	 * @param projectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ProjectRole> listProjectRoleByProjectGuid(String projectGuid) throws ServiceRequestException;

	public BOInfo getPMProjectTemplateBoInfo() throws ServiceRequestException;

	public BOInfo getPMProjectBoInfo() throws ServiceRequestException;

	public FoundationObject changeToSubProject(FoundationObject task) throws ServiceRequestException;

	public FoundationObject createSubProject(FoundationObject task, FoundationObject project) throws ServiceRequestException;

	/**
	 * 启用项目模板
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject effectProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 停用项目模板
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject obsoleteProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 删除项目模板
	 * 
	 * @param foundationObject
	 * @throws ServiceRequestException
	 */
	public void deleteProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException;

	/**
	 * 列出所有的项目
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptProject> listRptProject(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按组统计的项目
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptDeptStat> listRptDeptStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按项目经理统计的项目
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptManagerStat> listRptManagerStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出里程碑
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptMilestone> listRptMilestone(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出交付物
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptDeliverable> listRptDeliverable(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出任务
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptTask> listRptTask(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按任务执行者统计的任务
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptTaskExecutorStat> listRptTaskExecutorStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按部门统计的任务
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptTaskDeptStat> listRptTaskDeptStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按执行状态统计的结果
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptTaskExecStateStat> listRptTaskExecStateStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 保存对象
	 * 
	 * @param foundationObject
	 * @param hasReturn
	 * @param isUpdateTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isUpdateTime) throws ServiceRequestException;

	/**
	 * 通过wbs号取得该项目中对应的任务
	 * 
	 * @param projectFoundationObject
	 * @param wbsNumber
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject getTaskFoundationObjectByWBSNumber(ObjectGuid projectObjectGuid, String wbsNumber) throws ServiceRequestException;

	/**
	 * 增加依赖
	 * 
	 * @param taskRelation
	 * @return
	 * @throws ServiceRequestException
	 */
	public TaskRelation saveTaskRelation(TaskRelation taskRelation) throws ServiceRequestException;

	public void remindUpdateSchedule(ObjectGuid projectObjectGuid) throws ServiceRequestException;

	/**
	 * 批量保存任务或者工作项资源
	 * 
	 * @param taskObjectGuid
	 * @param listMember
	 * @throws ServiceRequestException
	 */
	public void saveBatchTaskMember(ObjectGuid taskObjectGuid, List<TaskMember> listMember) throws ServiceRequestException;

	public void dispatchProjectWarningRule() throws ServiceRequestException;

	/**
	 * 保存项目，包括任务，交付项，预警等
	 * 
	 * @param updateBean
	 * @return
	 * @throws ServiceRequestException
	 */
	public InstanceDomainUpdateBean saveInstanceDomain(InstanceDomainUpdateBean updateBean) throws ServiceRequestException;

	/**
	 * 取得项目实例，任务，交付项，预警等所有信息
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public InstanceDomainUpdateBean getInstanceDomain(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 计算项目中的延期，正常，超期任务、设置里程碑等项目信息
	 * 
	 * @param project
	 * @return
	 * @throws ServiceRequestException
	 */
	public InstanceDomainUpdateBean calculateProjectInfo(FoundationObject project) throws ServiceRequestException;

	/**
	 * 取得wbs计算时固定code、日历、工作时间配置
	 * 
	 * @param calendarGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public WBSPrepareContain getWBSPrepareContain(String calendarGuid) throws ServiceRequestException;

	public WorkItemAuthEnum getWorkItemAuth(ObjectGuid objectGuid, boolean isFile) throws ServiceRequestException;

	/**
	 * 列出任务
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptWorkItem> listRptWorkItem(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按执行状态统计的工作项
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptWorkItemExecStateStat> listRptWorkItemExecStateStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按部门统计的工作项
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptWorkItemDeptStat> listRptWorkItemDeptStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 列出按任务执行者统计的工作项
	 * 
	 * @param map
	 *            筛选条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptWorkItemExecutorStat> listRptWorkItemExecutorStat(Map<String, Object> map) throws ServiceRequestException;

	/**
	 * 根据条件搜索项目模板
	 * 
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FoundationObject> listProjectFramework(SearchCondition searchCondition) throws ServiceRequestException;
	
	/**
	 * 从配置文件中读取定义的指标值
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DefineIndicator> listIndicatorDefineFromConfig() throws ServiceRequestException;

	/**
	 * 读取设置的所有指标
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<IndicatorView> listIndicatorViewSet() throws ServiceRequestException;

	/**
	 * 取得指定指标值的分析结果，以维度分组
	 * 
	 * @param indicatorSetGuid
	 *            指标设定guid
	 * @param baseTime
	 *            基准时间
	 * @param timeDismension
	 *            时间维度
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<String, List<IndicatorAnalysisVal>> listAnalysisValBeforeBaseDate(String indicatorId, Date baseTime, IndicatorTimeRangeEnum timeRange)
			throws ServiceRequestException;

	/**
	 * 取得指定日期（日期所属年月）的指标值，以维度分组
	 * 
	 * @param indicatorSetGuid
	 * @param baseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<String, List<IndicatorAnalysisVal>> listAnalysisValByTime(String indicatorId, Date baseTime, IndicatorTimeRangeEnum timeRange) throws ServiceRequestException;
	
	/**
	 * 任务从审批到完成
	 * 
	 * @param taskObjectGuid 任务GUID 
	 * @param approve 审批意见
	 * 
	 */
	public FoundationObject completeTask(ObjectGuid taskObjectGuid,UpdateTaskStatus approve) throws ServiceRequestException;
	
}
