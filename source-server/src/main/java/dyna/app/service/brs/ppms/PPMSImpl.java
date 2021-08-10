/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: POSImpl Personal Object Service的实现类
 * wanglhb 2013-10-21
 */
package dyna.app.service.brs.ppms;

import dyna.app.service.BusinessRuleService;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.ppms.timertask.TimeTaskServerContextListener;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.*;
import dyna.common.bean.data.ppms.indicator.DefineIndicator;
import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;
import dyna.common.bean.data.ppms.indicator.chart.IndicatorView;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainUpdateBean;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.IndicatorTimeRangeEnum;
import dyna.common.systemenum.ppms.PMAuthorityEnum;
import dyna.common.systemenum.ppms.WorkItemAuthEnum;
import dyna.net.service.brs.*;
import dyna.net.service.das.MSRM;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * PPMS PROJECT PROCESS MANAGER SERVICE 项目过程管理
 * 
 * @author WangLH
 * 
 */
public class PPMSImpl extends BusinessRuleService implements PPMS
{
	private BaseConfigStub		baseConfigStub		= null;
	private DeliveryStub		deliveryStub		= null;
	private ProjectStub			projectStub			= null;
	private TaskStub			taskStub			= null;
	private RoleStub			roleStub			= null;
	private WorkItemStub		workItemStub		= null;
	private TaskMemberStub		taskMemberStub		= null;
	private PMAuthorityStub		pmAuthorityStub		= null;
	private PMReportStub		pmReportStub		= null;	// add by fanjq
	private NoticeStub			noticeStub			= null;
	private IndicatorStub		indicatorStub		= null;
	private IndicatorConfigStub	indicatorConfigStub	= null;

	private WBSStub				wbsStub				= null;

	private CalendarStub		calendarStub		= null;

	private PMChangeStub		pmChangeStub		= null;

	private WarningNoticeStub	warningNoticeStub	= null;

	private static boolean		isInit				= false;

	@Override
	protected void init()
	{
		if (isInit)
		{
			return;
		}
		isInit = true;

		IndicatorConfigStub.init();

		this.server.addServerContextListener(new TimeTaskServerContextListener());

	}

	public synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized SMS getSMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(SMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized DSS getDSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DSS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public WarningNoticeStub getWarningNoticeStub()
	{
		if (this.warningNoticeStub == null)
		{
			this.warningNoticeStub = new WarningNoticeStub(this.serviceContext, this);
		}
		return this.warningNoticeStub;
	}

	protected PMAuthorityStub getPMAuthorityStub()
	{
		if (this.pmAuthorityStub == null)
		{
			this.pmAuthorityStub = new PMAuthorityStub(this.serviceContext, this);
		}
		return this.pmAuthorityStub;
	}

	protected BaseConfigStub getBaseConfigStub()
	{
		if (this.baseConfigStub == null)
		{
			this.baseConfigStub = new BaseConfigStub(this.serviceContext, this);
		}
		return this.baseConfigStub;
	}

	public TaskMemberStub getTaskMemberStub()
	{
		if (this.taskMemberStub == null)
		{
			this.taskMemberStub = new TaskMemberStub(this.serviceContext, this);
		}
		return this.taskMemberStub;
	}

	public NoticeStub getNoticeStub()
	{
		if (this.noticeStub == null)
		{
			this.noticeStub = new NoticeStub(this.serviceContext, this);
		}
		return this.noticeStub;
	}

	// add by fanjq
	protected PMReportStub getPMReportStub()
	{
		if (this.pmReportStub == null)
		{
			this.pmReportStub = new PMReportStub(this.serviceContext, this);
		}
		return this.pmReportStub;
	}

	public WBSStub getWBSStub()
	{
		if (this.wbsStub == null)
		{
			this.wbsStub = new WBSStub(this.serviceContext, this);
		}
		return this.wbsStub;
	}

	protected CalendarStub getCalendarStub()
	{
		if (this.calendarStub == null)
		{
			this.calendarStub = new CalendarStub(this.serviceContext, this);
		}
		return this.calendarStub;
	}

	public WorkItemStub getWorkItemStub()
	{
		if (this.workItemStub == null)
		{
			this.workItemStub = new WorkItemStub(this.serviceContext, this);
		}
		return this.workItemStub;
	}

	public RoleStub getRoleStub()
	{
		if (this.roleStub == null)
		{
			this.roleStub = new RoleStub(this.serviceContext, this);
		}
		return this.roleStub;
	}

	public TaskStub getTaskStub()
	{
		if (this.taskStub == null)
		{
			this.taskStub = new TaskStub(this.serviceContext, this);
		}
		return this.taskStub;
	}

	public ProjectStub getProjectStub()
	{
		if (this.projectStub == null)
		{
			this.projectStub = new ProjectStub(this.serviceContext, this);
		}
		return this.projectStub;
	}

	public PMChangeStub getPMChangeStub()
	{
		if (this.pmChangeStub == null)
		{
			this.pmChangeStub = new PMChangeStub(this.serviceContext, this);
		}
		return this.pmChangeStub;
	}

	protected DeliveryStub getDeliveryStub()
	{
		if (this.deliveryStub == null)
		{
			this.deliveryStub = new DeliveryStub(this.serviceContext, this);
		}
		return this.deliveryStub;
	}

	public IndicatorStub getIndicatorStub()
	{
		if (this.indicatorStub == null)
		{
			this.indicatorStub = new IndicatorStub(this.serviceContext, this);
		}
		return this.indicatorStub;
	}

	public IndicatorConfigStub getIndicatorConfigStub()
	{
		if (this.indicatorConfigStub == null)
		{
			this.indicatorConfigStub = new IndicatorConfigStub(this.serviceContext, this);
		}
		return this.indicatorConfigStub;
	}

	public synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized WFI getWFI() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	@Override
	public List<FoundationObject> listProjectType() throws ServiceRequestException
	{
		return this.getBaseConfigStub().listProjectType();
	}

	@Override
	public FoundationObject saveProjectType(FoundationObject projectType) throws ServiceRequestException
	{
		return this.getBaseConfigStub().saveProjectType(projectType);
	}

	@Override
	public FoundationObject getProjectType(String projectTypeGuid, String classGuid) throws ServiceRequestException
	{
		return this.getBaseConfigStub().getProjectType(projectTypeGuid, classGuid);
	}

	@Override
	public void delProjectType(ObjectGuid projectTypeObjectGuid) throws ServiceRequestException
	{
		this.getBaseConfigStub().delProjectType(projectTypeObjectGuid);
	}

	@Override
	public ProjectRole getProjectTypeRole(String projectRoleGuid) throws ServiceRequestException
	{
		return this.getBaseConfigStub().getProjectTypeRole(projectRoleGuid);
	}

	@Override
	public LaborHourConfig getWorkTimeConfig() throws ServiceRequestException
	{
		return this.getBaseConfigStub().getWorkTimeConfig();
	}

	@Override
	public LaborHourConfig saveWorkTimeConfig(LaborHourConfig laborHour) throws ServiceRequestException
	{
		return this.getBaseConfigStub().saveWorkTimeConfig(laborHour);
	}

	@Override
	public List<MessageRule> listProjectNotifyRule() throws ServiceRequestException
	{
		return this.getNoticeStub().listProjectNotifyRule();
	}

	@Override
	public List<MessageRule> saveProjectNotifyRule(List<MessageRule> messageRule) throws ServiceRequestException
	{
		return this.getNoticeStub().saveProjectNotifyRule(messageRule);

	}

	@Override
	public MessageRule getProjectNotifyRule(String messagetype) throws ServiceRequestException
	{
		return this.getNoticeStub().getProjectNotifyRule(messagetype);
	}

	@Override
	public FoundationObject createProject(FoundationObject pmFoundationObject) throws ServiceRequestException
	{
		return this.getProjectStub().createProject(pmFoundationObject);
	}

	@Override
	public void deleteProject(FoundationObject pmFoundationObject) throws ServiceRequestException
	{
		this.getProjectStub().deleteProject(pmFoundationObject);

	}

	@Override
	public void deleteAllTask(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		this.getTaskStub().deleteAllTask(projectObjectGuid);
	}

	@Override
	public void clearRelationProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		this.getTaskStub().clearRelationProject(projectObjectGuid);
	}

	@Override
	public List<FoundationObject> listProject(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getProjectStub().listProject(searchCondition);

	}

	@Override
	public List<FoundationObject> listTask(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getTaskStub().listTask(searchCondition);
	}

	@Override
	public List<Deliverable> listAllDeliveryByProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		return this.getDeliveryStub().listAllDeliveryByProject(projectObjectGuid);
	}

	@Override
	public List<RoleMembers> listUserInProjectRole(String projectRoleGuid) throws ServiceRequestException
	{
		return this.getRoleStub().listUserInProjectRole(projectRoleGuid);
	}

	@Override
	public List<RoleMembers> listUserInProject(String projectGuid) throws ServiceRequestException
	{
		return this.getRoleStub().listUserInProject(projectGuid);
	}

	@Override
	public void deleteProjectRole(String projectRoleGuid) throws ServiceRequestException
	{
		this.getRoleStub().deleteProjectRole(projectRoleGuid);
	}

	@Override
	public List<RoleMembers> saveUserInProjectRole(String pmRoleGuid, List<String> userGuidList) throws ServiceRequestException
	{
		return this.getRoleStub().saveUserInProjectRole(pmRoleGuid, userGuidList);

	}

	@Override
	public List<DeliverableItem> listDeliveryItem(String taskGuid) throws ServiceRequestException
	{
		return this.getDeliveryStub().listDeliveryItem(taskGuid);

	}

	@Override
	public List<Deliverable> listDeliveryByItem(String deliveryItemGuid) throws ServiceRequestException
	{
		return this.getDeliveryStub().listDeliveryByItem(deliveryItemGuid);

	}

	@Override
	public DeliverableItem saveDeliveryItem(DeliverableItem deliveryItem) throws ServiceRequestException
	{
		return this.getDeliveryStub().saveDeliveryItem(deliveryItem);

	}

	@Override
	public void commitDelivery(Deliverable delivery) throws ServiceRequestException
	{
		this.getDeliveryStub().commitDelivery(delivery);

	}

	@Override
	public void deleteDelivery(String deliveryGuid) throws ServiceRequestException
	{
		this.getDeliveryStub().deleteDelivery(deliveryGuid);

	}

	@Override
	public FoundationObject createChangeRequest(FoundationObject foundation) throws ServiceRequestException
	{
		return this.getPMChangeStub().createChangeRequest(foundation);

	}

	@Override
	public void completeChangeRequest(FoundationObject foundation) throws ServiceRequestException
	{
		this.getPMChangeStub().completeChangeRequest(foundation);

	}

	@Override
	public List<FoundationObject> listChangeRequest(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		return this.getPMChangeStub().listChangeRequest(projectObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listRemarkByUpdateGuid(java.lang.String)
	 */
	@Override
	public List<UpdateRemark> listRemarkByUpdateGuid(String updateGuid) throws ServiceRequestException
	{
		return this.getWorkItemStub().listRemarkByUpdateGuid(updateGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#saveRemark(java.lang.String, dyna.common.bean.data.ppms.UpdateRemark)
	 */
	@Override
	public UpdateRemark saveRemark(String updateGuid, UpdateRemark remark) throws ServiceRequestException
	{
		return this.getWorkItemStub().saveRemark(updateGuid, remark);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#deleteRemark(java.lang.String)
	 */
	@Override
	public void deleteRemark(String remarkGuid) throws ServiceRequestException
	{
		this.getWorkItemStub().deleteRemark(remarkGuid);

	}

	@Override
	public List<CheckpointConfig> listCheckpointConfigByTypeGuid(String typeGuid) throws ServiceRequestException
	{
		return this.getBaseConfigStub().listCheckpointConfigByMileStoneGuid(typeGuid);
	}

	@Override
	public CheckpointConfig saveCheckpointConfig(CheckpointConfig checkpoint) throws ServiceRequestException
	{
		return this.getBaseConfigStub().saveCheckpointConfig(checkpoint);

	}

	@Override
	public void delCheckpointConfig(String checkpointGuid) throws ServiceRequestException
	{
		this.getBaseConfigStub().delCheckpointConfig(checkpointGuid);

	}

	@Override
	public FoundationObject startProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			value = this.getProjectStub().startProject(projectObjectGuid);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;
	}

	@Override
	public FoundationObject completeProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			value = this.getProjectStub().completeProject(projectObjectGuid);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;
	}

	@Override
	public FoundationObject pauseProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			value = this.getProjectStub().pauseProject(projectObjectGuid);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#deleteTask(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public void deleteTask(FoundationObject taskFoundationObject) throws ServiceRequestException
	{
		this.getTaskStub().deleteTask(taskFoundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#suspendTask(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject suspendTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			// 1.判断是否有完成权限
			FoundationObject foundation = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);
			this.getPMAuthorityStub().hasPMAuthority(foundation, PMAuthorityEnum.SUSPEND);

			value = this.getTaskStub().suspendTask(foundation);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#startTask(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject startTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{

		FoundationObject task = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
		FoundationObject project = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(util.getOwnerProject(), false);
		// 1.判断是否有完成权限
		this.getPMAuthorityStub().hasPMAuthority(task, PMAuthorityEnum.START);

		return this.getTaskStub().startTask(task, project);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#pauseTask(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject pauseTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			FoundationObject task = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);
			// 1.判断是否有完成权限
			this.getPMAuthorityStub().hasPMAuthority(task, PMAuthorityEnum.PAUSE);

			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);
			FoundationObject project = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(util.getOwnerProject(), false);

			value = this.getTaskStub().pauseTask(task, project);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#completeTask(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject completeTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			FoundationObject task = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);

			// 1.判断是否有完成权限
			this.getPMAuthorityStub().hasPMAuthority(task, PMAuthorityEnum.COMPLETE);

			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);

			FoundationObject project = this.getBOAS().getObject(util.getOwnerProject());
			value = this.getTaskStub().completeTask(task, project);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;
	}

	@Override
	public FoundationObject getParentTask(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{

		return this.getWBSStub().getParentTask(projectObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#listSubTask(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<FoundationObject> listSubTask(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		return this.getTaskStub().listSubTask(projectObjectGuid, true);
	}

	@Override
	public BOInfo getPMTaskBoInfo(boolean isTaskTemplate) throws ServiceRequestException
	{
		return this.getTaskStub().getPMTaskBoInfo(isTaskTemplate);
	}

	@Override
	public PMCalendar saveCalendar(PMCalendar pmCalendar) throws ServiceRequestException
	{
		return this.getCalendarStub().saveCalendar(pmCalendar);
	}

	@Override
	public PMCalendar getWorkCalendar(String calendarGuid) throws ServiceRequestException
	{
		return this.getCalendarStub().getWorkCalendar(calendarGuid);
	}

	@Override
	public PMCalendar getWorkCalendarBaseInfo(String calendarGuid) throws ServiceRequestException
	{
		return this.getCalendarStub().getWorkCalendarBaseInfo(calendarGuid);
	}

	@Override
	public PMCalendar getWorkCalendarById(String calendarId) throws ServiceRequestException
	{
		return this.getCalendarStub().getCalendarById(calendarId);
	}

	@Override
	public void obsoleteCalendar(String calendarGuid) throws ServiceRequestException
	{
		this.getCalendarStub().obsoleteCalendar(calendarGuid);

	}

	@Override
	public PMCalendar saveASCalendar(String oriCalendarGuid, PMCalendar pmCalendar) throws ServiceRequestException
	{
		return this.getCalendarStub().saveASCalendar(oriCalendarGuid, pmCalendar);

	}

	@Override
	public List<PMCalendar> listCalendar() throws ServiceRequestException
	{

		return this.getCalendarStub().listCalendarBaseInfo(false);
	}

	@Override
	public List<PMCalendar> listCalendarContainObsolete() throws ServiceRequestException
	{
		return this.getCalendarStub().listCalendarBaseInfo(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listUpdateTaskStatus(java.lang.String)
	 */
	@Override
	public List<UpdateTaskStatus> listUpdateTaskStatus(String taskGuid) throws ServiceRequestException
	{
		return this.getTaskStub().listUpdateTaskStatus(taskGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#saveUpdateTaskStatus(dyna.common.bean.data.ppms.UpdateTaskStatus)
	 */
	@Override
	public UpdateTaskStatus saveUpdateTaskStatus(UpdateTaskStatus taskStatus) throws ServiceRequestException
	{
		return this.getTaskStub().saveUpdateTaskStatus(taskStatus, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listRemarkByUpdateTaskStatusGuid(java.lang.String)
	 */
	@Override
	public List<UpdateRemark> listRemarkByUpdateTaskStatusGuid(String updateGuid) throws ServiceRequestException
	{
		return this.getTaskStub().listRemarkByUpdateTaskStatusGuid(updateGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#saveUpdateTaskStatusRemark(java.lang.String,
	 * dyna.common.bean.data.ppms.UpdateRemark)
	 */
	@Override
	public UpdateRemark saveUpdateTaskStatusRemark(String updateTaskStatusGuid, UpdateRemark remark) throws ServiceRequestException
	{
		return this.getTaskStub().saveUpdateTaskStatusRemark(updateTaskStatusGuid, remark);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#deleteUpdateTaskStatusRemark(java.lang.String)
	 */
	@Override
	public void deleteUpdateTaskStatusRemark(String remarkGuid) throws ServiceRequestException
	{
		this.getTaskStub().deleteUpdateTaskStatusRemark(remarkGuid);

	}

	@Override
	public FoundationObject suspendProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		return this.getProjectStub().suspendProject(projectObjectGuid);
	}

	/**
	 * 发送项目预警通知
	 * 
	 * @throws ServiceRequestException
	 * 
	 */
	@Override
	public void dispatchProjectWarningRule() throws ServiceRequestException
	{
		this.getWarningNoticeStub().dispatchProjectWarningRule();

	}

	/**
	 * 通过任务ObjectGuid取得任务上的所有的成员
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	@Override
	public List<TaskMember> listTaskMember(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		return this.getTaskMemberStub().listTaskMember(taskObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listProjectRoleByProjectGuid(java.lang.String)
	 */
	@Override
	public List<ProjectRole> listProjectRoleByProjectGuid(String projectGuid) throws ServiceRequestException
	{
		return this.getRoleStub().listProjectRoleByProjectGuid(projectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#saveEarlyWarning(dyna.common.bean.data.ppms.EarlyWarning)
	 */
	@Override
	public EarlyWarning saveEarlyWarning(EarlyWarning earlyWarning) throws ServiceRequestException
	{
		return this.getWarningNoticeStub().saveEarlyWarning(earlyWarning);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#deleteEarlyWarning(java.lang.String)
	 */
	@Override
	public void deleteEarlyWarning(String warningGuid) throws ServiceRequestException
	{
		this.getWarningNoticeStub().deleteEarlyWarning(warningGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#getEarlyWarningByGuid(java.lang.String)
	 */
	@Override
	public EarlyWarning getEarlyWarningByGuid(String warningGuid) throws ServiceRequestException
	{
		return this.getWarningNoticeStub().getEarlyWarningByGuid(warningGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listEarlyWarning(java.lang.String)
	 */
	@Override
	public List<EarlyWarning> listEarlyWarning(String projectGuid) throws ServiceRequestException
	{
		return this.getWarningNoticeStub().listEarlyWarning(projectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#startEarlyWarning(java.lang.String)
	 */
	@Override
	public EarlyWarning startEarlyWarning(String warningGuid) throws ServiceRequestException
	{
		return this.getWarningNoticeStub().startEarlyWarning(warningGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#stopEarlyWarning(java.lang.String)
	 */
	@Override
	public EarlyWarning stopEarlyWarning(String warningGuid) throws ServiceRequestException
	{
		return this.getWarningNoticeStub().stopEarlyWarning(warningGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listStartEarlyWarning(java.lang.String)
	 */
	@Override
	public List<EarlyWarning> listStartEarlyWarning(String projectGuid) throws ServiceRequestException
	{
		return this.getWarningNoticeStub().listStartEarlyWarning(projectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#getPMChangeBoInfo()
	 */
	@Override
	public BOInfo getPMChangeBoInfo() throws ServiceRequestException
	{
		return this.getPMChangeStub().getPMChangeBoInfo();
	}

	@Override
	public List<Deliverable> listAllDeliveryByTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		return this.getDeliveryStub().listAllDeliveryByTask(taskObjectGuid);
	}

	@Override
	public void deleteDeliveryItem(String guid) throws ServiceRequestException
	{
		this.getDeliveryStub().deleteDeliveryItem(guid);
	}

	@Override
	public ProjectRole getProjectRole(String roleGuid) throws ServiceRequestException
	{

		return this.getRoleStub().getProjectRole(roleGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#getPMProjectTemplateBoInfo()
	 */
	@Override
	public BOInfo getPMProjectTemplateBoInfo() throws ServiceRequestException
	{
		return this.getProjectStub().getPMProjectTemplateBoInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPM#getPMProjectBoInfo(boolean)
	 */
	@Override
	public BOInfo getPMProjectBoInfo() throws ServiceRequestException
	{
		return this.getProjectStub().getPMProjectBoInfo();
	}

	@Override
	public BOInfo getPMTaskChangeBoInfo() throws ServiceRequestException
	{
		return this.getPMChangeStub().getPMTaskChangeBoInfo();
	}

	@Override
	public FoundationObject changeToSubProject(FoundationObject task) throws ServiceRequestException
	{

		return this.getProjectStub().changeToSubProject(task);
	}

	@Override
	public FoundationObject createSubProject(FoundationObject task, FoundationObject project) throws ServiceRequestException
	{
		return this.getProjectStub().createSubProject(task, project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#getWorkItemBoinfo()
	 */
	@Override
	public BOInfo getWorkItemBoinfo() throws ServiceRequestException
	{
		return this.getWorkItemStub().getWorkItemBoInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#listWorkItem(dyna.common.SearchCondition)
	 */
	@Override
	public List<FoundationObject> listWorkItem(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getWorkItemStub().listWorkItem(searchCondition);
	}

	/* begin by fanjq for project report */

	@Override
	public List<RptProject> listRptProject(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptProject(map);
	}

	@Override
	public List<RptDeptStat> listRptDeptStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptDeptStat(map);
	}

	@Override
	public List<RptManagerStat> listRptManagerStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptManagerStat(map);
	}

	@Override
	public List<RptMilestone> listRptMilestone(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptMilestone(map);
	}

	@Override
	public List<RptDeliverable> listRptDeliverable(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptDeliverable(map);
	}

	@Override
	public List<RptTask> listRptTask(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptTask(map);
	}

	@Override
	public List<RptTaskExecutorStat> listRptTaskExecutorStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptTaskExecutorStat(map);
	}

	@Override
	public List<RptTaskDeptStat> listRptTaskDeptStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptTaskDeptStat(map);
	}

	@Override
	public List<RptTaskExecStateStat> listRptTaskExecStateStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptTaskExecStateStat(map);
	}

	/* end by fanjq for project report */
	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#startWorkItem(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject startWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		return this.getWorkItemStub().startWorkItem(workItemObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#completeWorkItem(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject completeWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		return this.getWorkItemStub().completeWorkItem(workItemObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#pauseWorkItem(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject pauseWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		return this.getWorkItemStub().pauseWorkItem(workItemObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#cancelWorkItem(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject cancelWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		return this.getWorkItemStub().cancelWorkItem(workItemObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#saveWorkItem(dyna.common.bean.data.FoundationObject, java.util.List, boolean)
	 */
	@Override
	public FoundationObject saveWorkItem(FoundationObject workItemFou, List<TaskMember> listTaskMember, boolean isCommit) throws ServiceRequestException
	{
		return this.getWorkItemStub().saveWorkItem(workItemFou, listTaskMember, isCommit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#deleteWorkItem(java.util.List)
	 */
	@Override
	public void deleteWorkItem(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getWorkItemStub().deleteWorkItem(objectGuid);
	}

	@Override
	public FoundationObject effectProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getProjectStub().effectProjectTemplate(foundationObject);

	}

	@Override
	public FoundationObject obsoleteProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getProjectStub().obsoleteProjectTemplate(foundationObject);
	}

	@Override
	public void deleteProjectTemplate(FoundationObject foundationObject) throws ServiceRequestException
	{
		this.getProjectStub().deleteProjectTemplate(foundationObject);
	}

	@Override
	public FoundationObject saveObject(FoundationObject foundationObject, boolean hasReturn, boolean isUpdateTime) throws ServiceRequestException
	{
		return this.getProjectStub().saveObject(foundationObject, hasReturn, isUpdateTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#getTaskFoundationObjectByWBSNumber(dyna.common.bean.data.FoundationObject,
	 * java.lang.String)
	 */
	@Override
	public FoundationObject getTaskFoundationObjectByWBSNumber(ObjectGuid projectObjectGuid, String wbsNumber) throws ServiceRequestException
	{
		return this.getWBSStub().getTaskFoundationObjectByWBSNumber(projectObjectGuid, wbsNumber);
	}

	@Override
	public TaskRelation saveTaskRelation(TaskRelation taskRelation) throws ServiceRequestException
	{
		return this.getTaskStub().saveTaskRelation(taskRelation);
	}

	@Override
	public void remindUpdateSchedule(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		this.getProjectStub().remindUpdateSchedule(projectObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.PPMS#saveTaskMember(dyna.common.bean.data.ObjectGuid, java.util.List)
	 */
	@Override
	public void saveBatchTaskMember(ObjectGuid taskObjectGuid, List<TaskMember> listMember) throws ServiceRequestException
	{
		this.getTaskMemberStub().saveBatchTaskMember(taskObjectGuid, listMember);

	}

	@Override
	public InstanceDomainUpdateBean saveInstanceDomain(InstanceDomainUpdateBean updateBean) throws ServiceRequestException
	{
		return this.getWBSStub().saveInstanceDomain(updateBean);
	}

	@Override
	public InstanceDomainUpdateBean getInstanceDomain(ObjectGuid rootObjectGuid) throws ServiceRequestException
	{

		return this.getWBSStub().getInstanceDomain(rootObjectGuid);
	}

	@Override
	public InstanceDomainUpdateBean calculateProjectInfo(FoundationObject project) throws ServiceRequestException
	{
		return this.getProjectStub().calculateProjectInfo(project);

	}

	@Override
	public WBSPrepareContain getWBSPrepareContain(String calendarGuid) throws ServiceRequestException
	{
		return this.getWBSStub().getWBSPrepareContain(calendarGuid);
	}

	@Override
	public WorkItemAuthEnum getWorkItemAuth(ObjectGuid objectGuid, boolean isFile) throws ServiceRequestException
	{
		return this.getWorkItemStub().getWorkItemAuth(objectGuid, isFile);
	}

	@Override
	public List<RptWorkItem> listRptWorkItem(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptWorkItem(map);
	}

	@Override
	public List<RptWorkItemExecStateStat> listRptWorkItemExecStateStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptWorkItemExecStateStat(map);
	}

	@Override
	public List<RptWorkItemDeptStat> listRptWorkItemDeptStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptWorkItemDeptStat(map);
	}

	@Override
	public List<RptWorkItemExecutorStat> listRptWorkItemExecutorStat(Map<String, Object> map) throws ServiceRequestException
	{
		return this.getPMReportStub().listRptWorkItemExecutorStat(map);
	}

	@Override
	public List<FoundationObject> listProjectFramework(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getProjectStub().listProjectFramework(searchCondition);
	}

	@Override
	public List<DefineIndicator> listIndicatorDefineFromConfig() throws ServiceRequestException
	{
		return this.getIndicatorConfigStub().listDefinedIndicatorConfig();
	}

	@Override
	public List<IndicatorView> listIndicatorViewSet() throws ServiceRequestException
	{
		return this.getIndicatorConfigStub().listIndicatorViewSet();
	}

	@Override
	public Map<String, List<IndicatorAnalysisVal>> listAnalysisValBeforeBaseDate(String indicatorId, Date baseTime, IndicatorTimeRangeEnum timeRange) throws ServiceRequestException
	{
		return this.getIndicatorStub().listAnalysisValBeforeBaseDate(indicatorId, baseTime, timeRange);
	}

	@Override
	public Map<String, List<IndicatorAnalysisVal>> listAnalysisValByTime(String indicatorId, Date baseTime, IndicatorTimeRangeEnum timeRange) throws ServiceRequestException
	{
		return this.getIndicatorStub().listAnalysisVal(indicatorId, baseTime, timeRange);
	}

	@Override
	public FoundationObject completeTask(ObjectGuid taskObjectGuid, UpdateTaskStatus approve) throws ServiceRequestException
	{

		FoundationObject value = null;
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.getFixedTransactionId());
			FoundationObject task = ((BOASImpl) this.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);

			// 1.判断是否有完成权限
			this.getPMAuthorityStub().hasPMAuthority(task, PMAuthorityEnum.COMPLETE);

			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(task);

			FoundationObject project = this.getBOAS().getObject(util.getOwnerProject());
			value = this.getTaskStub().completeTask(task, project, approve);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
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
		return value;
	}
}