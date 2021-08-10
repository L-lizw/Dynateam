/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 工作项
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.*;
import dyna.common.bean.data.ppms.wbs.WBSPrepareContain;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ppms.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.Signature;
import dyna.net.service.brs.AAS;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangLHB
 *         工作项处理
 * 
 */
public class WorkItemStub extends AbstractServiceStub<PPMSImpl>
{

	protected WorkItemStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);

	}

	protected WorkItemAuthEnum getWorkItemAuth(ObjectGuid objectGuid, boolean isFile) throws ServiceRequestException
	{
		FoundationObject workItemObject = this.stubService.getBOAS().getObject(objectGuid);
		if (workItemObject == null)
		{
			return WorkItemAuthEnum.ONLYVIEW;
		}

		boolean isCompleted = this.isCompleted(workItemObject);
		boolean isAdmin = this.isAdmin();
		boolean isProjectManager = this.isProjectManager(workItemObject);
		boolean isExecutor = isExecutor(workItemObject);

		if (!isFile)
		{
			if (!isCompleted)
			{
				if (isAdmin || isProjectManager || isExecutor)
				{
					return WorkItemAuthEnum.ALL;
				}
				else
				{
					WorkItemStateEnum statusEnum = this.getWorkItemStateEnum(workItemObject);
					String createUserGuid = workItemObject.getCreateUserGuid();
					if ((WorkItemStateEnum.NOTASSIGNED == statusEnum && this.stubService.getUserSignature().getUserGuid().equals(createUserGuid)) || isExecutor || isProjectManager)
					{
						return WorkItemAuthEnum.ALL;
					}
				}
			}
			return WorkItemAuthEnum.ONLYVIEW;
		}

		if (isAdmin || isProjectManager)
		{
			return WorkItemAuthEnum.ALL;
		}

		if (isCompleted)
		{
			if (isExecutor)
			{
				return WorkItemAuthEnum.ONLYUPLOAD;
			}
			return WorkItemAuthEnum.ONLYVIEW;
		}
		else
		{
			// 只有在“未指派”的时候且是创建者时可编辑
			WorkItemStateEnum statusEnum = this.getWorkItemStateEnum(workItemObject);
			String createUserGuid = workItemObject.getCreateUserGuid();
			if ((WorkItemStateEnum.NOTASSIGNED == statusEnum && this.stubService.getUserSignature().getUserGuid().equals(createUserGuid)) || isExecutor || isProjectManager)
			{
				return WorkItemAuthEnum.ALL;
			}

			return WorkItemAuthEnum.ONLYVIEW;
		}
	}

	protected BOInfo getWorkItemBoInfo() throws ServiceRequestException
	{
		List<BOInfo> boInfoList = null;
		boInfoList = this.stubService.getEMM().listBizObjectByInterface(ModelInterfaceEnum.IPMWorkItem);

		if (!SetUtils.isNullList(boInfoList))
		{
			return boInfoList.get(0);
		}
		else
		{
			throw new ServiceRequestException("ID_APP_PM_WORKITEM_BOINFO_NOT_FOUND", "not found workitem boinfo");

		}
	}

	/**
	 * 获取工作项
	 * 
	 * @param searchCondition
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<FoundationObject> listWorkItem(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<FoundationObject> objectList = null;
		BOASImpl boas = (BOASImpl) this.stubService.getBOAS();

		if (searchCondition != null)
		{
			ObjectGuid searchObject = searchCondition.getObjectGuid();
			if (searchObject != null && !StringUtils.isNullString(searchObject.getClassName()))
			{
				this.stubService.getBaseConfigStub().putAllClassFieldToSearch(searchObject.getClassName(), searchCondition);
			}
			objectList = boas.getFoundationStub().listObject(searchCondition, false);
		}
		return objectList;
	}

	/**
	 * 取消工作项,创建者有权限,且状态为“未启动”、“暂停”、“进行中”
	 * 
	 * @param projectObjectGuid
	 * @throws ServiceRequestException
	 */
	public FoundationObject cancelWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		try
		{
			FoundationObject workItem = this.stubService.getBOAS().getObject(workItemObjectGuid);
			if (workItem != null)
			{
				String createUserguid = workItem.getCreateUserGuid();
				String operationUser = this.stubService.getOperatorGuid();
				if (operationUser.equals(createUserguid))
				{
					WorkItemStateEnum workStatusEnum = this.getWorkItemStatusEnum(workItem);
					if (workStatusEnum != null)
					{
						if (workStatusEnum.equals(WorkItemStateEnum.NOTSTART) || workStatusEnum.equals(WorkItemStateEnum.PAUSE)
								|| workStatusEnum.equals(WorkItemStateEnum.INPROGRESS))
						{
							// 重新保存工作项，将其状态置为已撤销
							CodeItemInfo cancelInfo = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.WORKITEMSTATE.getValue(), WorkItemStateEnum.CANCEL.getValue());
							if (cancelInfo != null)
							{
								workItem.put(CodeNameEnum.EXECUTESTATUS.getValue(), cancelInfo.getGuid());
								if (workItem.get(PPMFoundationObjectUtil.ACTUALFINISHTIME) == null)
								{
									workItem.put(PPMFoundationObjectUtil.ACTUALFINISHTIME, new Date());
								}

								workItem = this.saveObjectNotCheckAuthority(workItem);

								UpdateTaskStatus updateRecord = new UpdateTaskStatus();
								updateRecord.setTaskObjectGuid(workItem.getObjectGuid());
								Object completeRate = workItem.get(PPMFoundationObjectUtil.COMPLETIONRATE) == null ? 0 : workItem.get(PPMFoundationObjectUtil.COMPLETIONRATE);
								updateRecord.setProgressRate(StringUtils.convertNULLtoString(completeRate));
								updateRecord.setStatus(ProgressRateEnum.CANCEL);
								this.stubService.getTaskStub().saveUpdateTaskStatus(updateRecord, false);

								// 给取消工作项后的人发邮件
								this.stubService.getNoticeStub().sendMail(null, workItem, MessageTypeEnum.WORKITEMCANCELNOTIFY);

							}
						}
					}
				}
			}
			return workItem;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 保存对象,不判断权限
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveObjectNotCheckAuthority(FoundationObject foundationObject) throws ServiceRequestException
	{
		return ((BOASImpl) (this.stubService.getBOAS())).getFSaverStub().saveObject(foundationObject, true, false, false, null);
	}

	/**
	 * 通过更新guid取得所有相关备注，
	 * 
	 * @param updateGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<UpdateRemark> listRemarkByUpdateGuid(String updateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(UpdateRemark.UPDATEWORKITEMTASKGUID, updateGuid);
		try
		{
			List<UpdateRemark> remarks = sds.query(UpdateRemark.class, filter);
			if (!SetUtils.isNullList(remarks))
			{
				for (UpdateRemark taskStatus : remarks)
				{
					if (!StringUtils.isNullString(taskStatus.getUpdateUserGuid()))
					{
						User user = this.stubService.getAAS().getUser(taskStatus.getUpdateUserGuid());
						if (user != null)
						{
							taskStatus.put(UpdateTaskStatus.UPDATE_USER_NAME, user.getUserName());
						}
					}
					if (!StringUtils.isNullString(taskStatus.getCreateUserGuid()))
					{
						User user = this.stubService.getAAS().getUser(taskStatus.getCreateUserGuid());
						if (user != null)
						{
							taskStatus.put(UpdateTaskStatus.CREATE_USER_NAME, user.getUserName());
						}
					}
				}

			}
			return remarks;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected UpdateRemark saveRemark(String updateGuid, UpdateRemark remark) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		remark.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		remark.setUpdateWorkitemGuid(updateGuid);
		if (!StringUtils.isGuid(remark.getGuid()))
		{
			remark.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}
		try
		{
			String guid = sds.save(remark);
			if (StringUtils.isGuid(guid))
			{
				remark.setGuid(guid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return this.getRemark(remark.getGuid());

	}

	/**
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	private UpdateRemark getRemark(String remarkGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, remarkGuid);
		try
		{
			List<UpdateRemark> updateRemark = sds.query(UpdateRemark.class, filter);
			if (!SetUtils.isNullList(updateRemark))
			{
				return updateRemark.get(0);
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return null;
	}

	protected void deleteRemark(String remarkGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, remarkGuid);
			sds.delete(UpdateRemark.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 启动工作项:责任人有权限,且状态为"未启动"或者"暂停"
	 * 
	 * @param workItemObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject startWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		FoundationObject foun = this.stubService.getBOAS().getObject(workItemObjectGuid);
		if (foun != null)
		{
			String resUserguid = (String) foun.get(PPMFoundationObjectUtil.EXECUTOR);
			if (StringUtils.isGuid(resUserguid))
			{
				String operation = this.stubService.getOperatorGuid();
				if (operation.equals(resUserguid))
				{
					WorkItemStateEnum statusEnum = this.getWorkItemStatusEnum(foun);

					if (statusEnum != null && (statusEnum.equals(WorkItemStateEnum.NOTSTART) || statusEnum.equals(WorkItemStateEnum.PAUSE)))
					{
						CodeItemInfo completeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.WORKITEMSTATE.getValue(), WorkItemStateEnum.INPROGRESS.getValue());
						if (completeItem != null)
						{
							foun.put(PPMFoundationObjectUtil.EXECUTESTATUS, completeItem.getGuid());

							if (foun.get(PPMFoundationObjectUtil.ACTUALSTARTTIME) == null)
							{
								foun.put(PPMFoundationObjectUtil.ACTUALSTARTTIME, new Date());
							}

							foun = this.saveObjectNotCheckAuthority(foun);

							// 创建任务启动记录
							UpdateTaskStatus updateRecord = new UpdateTaskStatus();
							updateRecord.setTaskObjectGuid(foun.getObjectGuid());
							Object completeRate = foun.get(PPMFoundationObjectUtil.COMPLETIONRATE) == null ? 0 : foun.get(PPMFoundationObjectUtil.COMPLETIONRATE);
							updateRecord.setProgressRate(StringUtils.convertNULLtoString(completeRate));
							updateRecord.setStatus(ProgressRateEnum.START);
							this.stubService.getTaskStub().saveUpdateTaskStatus(updateRecord, false);

						}
					}
				}
			}
		}
		return foun;

	}

	/**
	 * 工作项完成:责任人有权限,且状态是"进行中"
	 * 
	 * @param workItemObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject completeWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		FoundationObject foun = this.stubService.getBOAS().getObject(workItemObjectGuid);
		if (foun != null)
		{
			String resUserguid = (String) foun.get(PPMFoundationObjectUtil.EXECUTOR);
			if (StringUtils.isGuid(resUserguid))
			{
				String operation = this.stubService.getOperatorGuid();
				if (operation.equals(resUserguid))
				{
					WorkItemStateEnum statusEnum = this.getWorkItemStatusEnum(foun);

					if (statusEnum != null && statusEnum.equals(WorkItemStateEnum.INPROGRESS))
					{
						CodeItemInfo completeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.WORKITEMSTATE.getValue(), WorkItemStateEnum.COMPLETED.getValue());
						if (completeItem != null)
						{
							foun.put(PPMFoundationObjectUtil.EXECUTESTATUS, completeItem.getGuid());
							foun.put(PPMFoundationObjectUtil.COMPLETIONRATE, 100);

							if (foun.get(PPMFoundationObjectUtil.ACTUALFINISHTIME) == null)
							{
								foun.put(PPMFoundationObjectUtil.ACTUALFINISHTIME, new Date());
							}

							foun = this.saveObjectNotCheckAuthority(foun);

							// 创建完成记录
							UpdateTaskStatus updateRecord = new UpdateTaskStatus();
							updateRecord.setTaskObjectGuid(foun.getObjectGuid());
							Object completeRate = foun.get(PPMFoundationObjectUtil.COMPLETIONRATE) == null ? 0 : foun.get(PPMFoundationObjectUtil.COMPLETIONRATE);
							updateRecord.setProgressRate(StringUtils.convertNULLtoString(completeRate));
							updateRecord.setStatus(ProgressRateEnum.FINISH);
							this.stubService.getTaskStub().saveUpdateTaskStatus(updateRecord, false);
						}
					}
				}
			}
		}
		return foun;
	}

	/**
	 * @param foun
	 * @return
	 * @throws ServiceRequestException
	 */
	private WorkItemStateEnum getWorkItemStatusEnum(FoundationObject foun) throws ServiceRequestException
	{
		WorkItemStateEnum statusEnum = null;
		String status = (String) foun.get(PPMFoundationObjectUtil.EXECUTESTATUS);
		if (StringUtils.isGuid(status))
		{
			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(status);
			if (codeItem != null)
			{
				statusEnum = WorkItemStateEnum.getValueOf(codeItem.getName());
			}
		}
		return statusEnum;
	}

	/**
	 * 暂停工作项:责任人有权,且状态为"运行中"
	 * 
	 * @param workItemObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject pauseWorkItem(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		FoundationObject foun = this.stubService.getBOAS().getObject(workItemObjectGuid);
		if (foun != null)
		{
			String resUserguid = (String) foun.get(PPMFoundationObjectUtil.EXECUTOR);
			if (StringUtils.isGuid(resUserguid))
			{
				String operation = this.stubService.getOperatorGuid();
				if (operation.equals(resUserguid))
				{
					WorkItemStateEnum statusEnum = this.getWorkItemStatusEnum(foun);

					if (statusEnum != null && statusEnum.equals(WorkItemStateEnum.INPROGRESS))
					{
						CodeItemInfo completeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.WORKITEMSTATE.getValue(), WorkItemStateEnum.PAUSE.getValue());
						if (completeItem != null)
						{
							foun.put(PPMFoundationObjectUtil.EXECUTESTATUS, completeItem.getGuid());
							foun = this.saveObjectNotCheckAuthority(foun);

							// 创建暂停记录
							UpdateTaskStatus updateRecord = new UpdateTaskStatus();
							updateRecord.setTaskObjectGuid(foun.getObjectGuid());
							Object completeRate = foun.get(PPMFoundationObjectUtil.COMPLETIONRATE) == null ? 0 : foun.get(PPMFoundationObjectUtil.COMPLETIONRATE);
							updateRecord.setProgressRate(StringUtils.convertNULLtoString(completeRate));
							updateRecord.setStatus(ProgressRateEnum.PAUSE);
							this.stubService.getTaskStub().saveUpdateTaskStatus(updateRecord, false);
						}
					}
				}
			}
		}
		return foun;
	}

	/**
	 * @param workItemFou
	 * @param listTaskMember
	 * @param isCommit
	 * @return
	 * @throws ServiceRequestException
	 */
	public FoundationObject saveWorkItem(FoundationObject workItemFou, List<TaskMember> listTaskMember, boolean isCommit) throws ServiceRequestException
	{
		String createUserguid = workItemFou.getCreateUserGuid();
		String operation = this.stubService.getOperatorGuid();
		if (createUserguid == null || operation.equals(createUserguid))
		{
			// 状态改成"未启动",且发邮件
			WorkItemStateEnum status = this.getWorkItemStatusEnum(workItemFou);
			CodeItemInfo completeItem = null;
			if (isCommit)
			{
				if (status == null || (status != null && status.equals(WorkItemStateEnum.NOTASSIGNED)))
				{
					completeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.WORKITEMSTATE.getValue(), WorkItemStateEnum.NOTSTART.getValue());
				}
			}
			else
			{
				if (status == null || (status != null && status.equals(WorkItemStateEnum.NOTASSIGNED)))
				{
					completeItem = this.stubService.getEMM().getCodeItemByName(CodeNameEnum.WORKITEMSTATE.getValue(), WorkItemStateEnum.NOTASSIGNED.getValue());
				}
			}
			if (completeItem != null)
			{
				workItemFou.put(PPMFoundationObjectUtil.EXECUTESTATUS, completeItem.getGuid());
			}

			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(workItemFou);
			if (util.getPlanStartTime() != null && util.getPlanFinishTime() != null && util.getPlanStartTime().compareTo(util.getPlanFinishTime()) > 0)
			{
				throw new ServiceRequestException("ID_APP_PM_START_TIME_TREATER_END", "The start time is greater than the end");
			}

			workItemFou.setCreateUserGuid(operation);
			try
			{
//				DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

				PPMFoundationObjectUtil workItemUtil = new PPMFoundationObjectUtil(workItemFou);
				this.calculateSPIAndDuration(workItemUtil);
				if (workItemFou.getObjectGuid().getGuid() == null)
				{
					workItemFou = this.stubService.getBOAS().createObject(workItemUtil.getFoundation());
				}
				else
				{
					workItemFou = this.saveObjectNotCheckAuthority(workItemUtil.getFoundation());
				}

				this.saveWorkItemResourceUser(workItemFou, listTaskMember);
				if (isCommit)
				{
					// 发邮件
					this.stubService.getNoticeStub().sendMail(null, workItemFou, MessageTypeEnum.WORKITEMNOTIFY);
				}
//				DataServer.getTransactionManager().commitTransaction();
			}
			catch (DynaDataException e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
			catch (Exception e)
			{
//				DataServer.getTransactionManager().rollbackTransaction();
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
		return workItemFou;
	}

	/**
	 * 计算工作量时：不考虑关联项目设置的日历，只用标准日历
	 * 计算工作量和spi--
	 * 
	 * @param workItemUtil
	 * @throws ServiceRequestException
	 */
	public void calculateSPIAndDuration(PPMFoundationObjectUtil workItemUtil) throws ServiceRequestException
	{
		if (workItemUtil != null && workItemUtil.getPlanStartTime() != null && workItemUtil.getPlanFinishTime() != null)
		{
			PMCalendar calendar = this.stubService.getCalendarStub().getStandardWorkCalendar();
			if (calendar != null)
			{
				// 计算工作量
				workItemUtil.setPlannedDuration(calendar.getDurationDay(workItemUtil.getPlanStartTime(), workItemUtil.getPlanFinishTime()) + 1);
				List<CodeItemInfo> onTimeStatusList = this.stubService.getEMM().listAllCodeItemInfoByMaster(null, CodeNameEnum.ONTIMESTATE.getValue());
				workItemUtil.setWbsPrepareContain(new WBSPrepareContain(null, null, null, null, null, onTimeStatusList));
				workItemUtil.calculateSPIAndCPI(calendar, new Date(), this.stubService.getWorkTimeConfig().getStandardhour(), true);
			}

		}

	}

	/**
	 * 保存工作项资源人
	 * 
	 * @param workItemFou
	 * @param listTaskMember
	 * @throws ServiceRequestException
	 */
	private void saveWorkItemResourceUser(FoundationObject workItemFou, List<TaskMember> listTaskMember) throws ServiceRequestException
	{
		this.stubService.getTaskMemberStub().saveBatchTaskMember(workItemFou.getObjectGuid(), listTaskMember);
	}

	/**
	 * @param listObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public void deleteWorkItem(ObjectGuid objectGuid) throws ServiceRequestException
	{
		FoundationObject foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);
		if (foundationObject != null)
		{
			String operationUser = this.stubService.getOperatorGuid();
			if (!operationUser.equals(foundationObject.getCreateUserGuid()))
			{
				throw new ServiceRequestException("ID_APP_PPM_WORKITEMSTUB_DELETE_USERERROR", "the user is not the creater user!", null);
			}
			WorkItemStateEnum states = this.getWorkItemStatusEnum(foundationObject);
			if (!WorkItemStateEnum.NOTASSIGNED.equals(states))
			{
				throw new ServiceRequestException("ID_APP_PPM_WORKITEMSTUB_DELETE_STATESERROR", "the states is not notassigin!", null);
			}
			// 先删除工作项的资源
			((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteObject(foundationObject, false);
		}
	}

	private boolean isExecutor(FoundationObject workItem) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(workItem);
		String executor = util.getExecutor();
		if (StringUtils.isGuid(executor) && executor.equals(this.stubService.getUserSignature().getUserGuid()))
		{
			return true;
		}
		return false;
	}

	private boolean isProjectManager(FoundationObject workItem) throws ServiceRequestException
	{
		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(workItem);
		FoundationObject ownerProject = null;
		if (util.getRelationProject() != null && StringUtils.isGuid(util.getRelationProject().getGuid()))
		{
			ownerProject = this.stubService.getBOAS().getObject(util.getRelationProject());
		}
		if (ownerProject != null)
		{
			util = new PPMFoundationObjectUtil(ownerProject);
			String projectManager = util.getExecutor();
			if (StringUtils.isGuid(projectManager) && projectManager.equals(this.stubService.getUserSignature().getUserGuid()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isAdmin() throws ServiceRequestException
	{
		if (Signature.SYSTEM_INTERNAL_USERID.equals(this.stubService.getUserSignature().getUserId()))
		{
			return false;
		}

		try
		{
			Group group = this.stubService.getServiceInstance(AAS.class, this.stubService.getUserSignature().getCredential())
					.getGroup(this.stubService.getUserSignature().getLoginGroupGuid());
			if (group != null && group.isAdminGroup())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			return false;
		}

		return false;
	}

	private WorkItemStateEnum getWorkItemStateEnum(FoundationObject workItem) throws ServiceRequestException
	{
		String status = (String) workItem.get(PPMFoundationObjectUtil.EXECUTESTATUS);
		if (StringUtils.isGuid(status))
		{
			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(status);
			if (codeItem != null)
			{
				return WorkItemStateEnum.getValueOf(codeItem.getName());
			}
		}
		return null;
	}

	private boolean isCompleted(FoundationObject workItem) throws ServiceRequestException
	{
		WorkItemStateEnum statusEnum = this.getWorkItemStateEnum(workItem);
		if (statusEnum == null)
		{
			return false;
		}
		return WorkItemStateEnum.COMPLETED == statusEnum;
	}
}
