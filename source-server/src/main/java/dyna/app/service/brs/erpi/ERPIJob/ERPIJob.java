/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPIJob
 * WangLHB Nov 15, 2011
 */
package dyna.app.service.brs.erpi.ERPIJob;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.das.jss.JSSImpl;
import dyna.app.service.das.jss.JobExecutor;
import dyna.app.service.das.jss.JobResult;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.Queue;
import dyna.common.dto.aas.User;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.ERPI;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.MSRM;

/**
 * @author WangLHB
 *         因队列优先级，增加ERPpriorityJob.java，内容与本类一致，如需修改本类则ERPpriorityJob也需一并修改
 */
public class ERPIJob implements JobExecutor
{

	private EMM	emm;

	/*
	 * 修改后的perform (non-Javadoc)
	 * 
	 * @see dyna.app.service.das.jss.JobExecutor#perform(dyna.app.service.das.jss .JSSImpl,
	 * dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public JobResult perform(JSSImpl jss, Queue queuejob) throws Exception
	{
		String mail = null;
		String itemNo = null;
		LanguageEnum lang = null;
		boolean success = false;
		boolean isMerge = false;
		BooleanResult result = null;

		ERPI erpi = jss.getRefService(ERPI.class);
		SMS sms = jss.getRefService(SMS.class);
		AAS aas = jss.getRefService(AAS.class);
		emm = jss.getRefService(EMM.class);
		DynaLogger.info("*****Start ERP Job*****" + "\n" + "Job id:" + queuejob.getFieldh());

		isMerge = "true".equalsIgnoreCase(queuejob.getFieldo());
		lang = LanguageEnum.getById(queuejob.getFieldg());

		String jobId = queuejob.getFieldh();
		String erpFactory = queuejob.getFieldf();
		String serviceGuid = queuejob.getFieldc();
		String schemaName = queuejob.getFieldd();
		ERPServiceConfig serviceConfig = erpi.getERPServiceConfigbyServiceGuid(serviceGuid);
		serviceConfig.setErpServerAddress("http://" + serviceConfig.getERPServerIP() + ":" + serviceConfig.getERPServerPort() + "/" + serviceConfig.getERPServerName());

		User user = ((AASImpl) aas).getUser(queuejob.getCreateUserGuid());
		String userId = "";
		if (user != null)
		{
			userId = user.getUserId();
		}
		DynaLogger.debug(userId);

		try
		{
			if (isMerge)
			{
				StringBuffer itemName = new StringBuffer();
				String[] guidAray = queuejob.getFielda().split(";");
				String[] classguidArray = queuejob.getFieldb().split(";");
				List<ObjectGuid> objectGuids = new ArrayList<ObjectGuid>();
				if (guidAray != null && guidAray.length > 0)
				{
					for (int i = 0; i < guidAray.length; i++)
					{
						String guid = guidAray[i];
						String classGuid = classguidArray[i];
						if (!StringUtils.isNullString(guid) && !StringUtils.isNullString(classGuid))
						{
							ObjectGuid itemObjectGuid = new ObjectGuid(classGuid, null, guid, null);
							FoundationObject foundationObject = ((BOASImpl) jss.getRefService(BOAS.class)).getFoundationStub().getObject(itemObjectGuid, false);
							if (foundationObject == null)
							{
								throw new NullPointerException(jss.getRefService(MSRM.class).getMSRString("ID_APP_INTEGRATEYF_ERP_MESSAGE_NO_DATA", lang.toString()));
							}
							objectGuids.add(foundationObject.getObjectGuid());
							if (i == guidAray.length - 1)
							{
								itemName.append(foundationObject.getFullName());
							}
							else
							{
								itemName.append(foundationObject.getFullName() + ";" + "\n");
							}
						}
					}
					itemNo = itemName.toString();
					result = erpi.exportMergeData2ERP(objectGuids, queuejob, userId, serviceConfig);
				}
			}
			else
			{
				String guid = queuejob.getFielda();
				String classGuid = queuejob.getFieldb();
				ObjectGuid itemObjectGuid = new ObjectGuid(classGuid, null, guid, null);

				FoundationObject foundationObject = ((BOASImpl) jss.getRefService(BOAS.class)).getFoundationStub().getObject(itemObjectGuid, false);
				DynaLogger.debug(foundationObject);
				if (foundationObject == null)
				{
					throw new NullPointerException(jss.getRefService(MSRM.class).getMSRString("ID_APP_INTEGRATEYF_ERP_MESSAGE_NO_DATA", lang.toString()));
				}
				itemNo = foundationObject.getFullName();
				if (isHasBathRS(itemObjectGuid))
				{
					result = this.exportBatchRS(foundationObject, lang, jss, queuejob, serviceConfig, userId);
				}
				else
				{
					result = erpi.exportData2ERP(itemObjectGuid, queuejob, userId, serviceConfig);
				}
			}
			DynaLogger.debug(result);
			if (result == null)
			{
				throw new NullPointerException(ERPTransferStub.NO_DATA_RECEIVED);
			}
			success = result.getFlag();
			mail = result.getDetail();

			// 易拓在抛转数据时，是先把所有的xml抛转过去，队列执行成功，状态变更为ERP执行中。
			// ERP接收到所有的xml之后才开始执行。此时不需要发送邮件通知。
			boolean noExecuteFlg = false;
			String erpServerType = serviceConfig.getERPServerSelected();
			if (ERPServerType.ERPTIPTOP.name().equals(erpServerType) || ERPServerType.ERPT100.name().equals(erpServerType) || ERPServerType.ERPT100DB.name().equals(erpServerType))
			{
				if (!result.isDataEmpty())
				{
					noExecuteFlg = true;
				}
			}

			// 格式化通知内容
			mail = this.formatMailContent(erpFactory, itemNo, schemaName, mail, success, jobId);
			DynaLogger.debug(mail);
			// 结束后通知
			String userSelf = user.getGuid();
			if (success)
			{
				if (noExecuteFlg)
				{
					return JobResult.erpExecuting(null, false);
				}
				else
				{
					List<String> listNoticeUsers = erpi.listEndNoticeUsersByServGuid(serviceGuid);
					// 如果listNoticeUsers不包含创建者，则把创建者加入到通知列表中
					if (!listNoticeUsers.contains(userSelf))
					{
						listNoticeUsers.add(userSelf);
					}
					sms.sendMailToUsers(queuejob.getName(), mail, MailCategoryEnum.INFO, null, listNoticeUsers, MailMessageType.ERPNOTIFY);
					return JobResult.succeed(mail, false);
				}
			}
			else
			{
				List<String> failedNoticeUsersList = erpi.listNotifyUsersWhenFailed(serviceGuid);
				// 如果failedNoticeUsersList不包含创建者，则把创建者加入到通知列表中
				if (!failedNoticeUsersList.contains(userSelf))
				{
					failedNoticeUsersList.add(userSelf);
				}
				sms.sendMailToUsers(queuejob.getName(), mail, MailCategoryEnum.ERROR, null, failedNoticeUsersList, MailMessageType.ERPNOTIFY);
				// 创建者已经发送邮件了，因此不需要再额外通知
				return JobResult.failed(mail, false);
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error(e.getMessage(), e);
			mail = "PLM:    " + this.getExceptionStringWithCause(e, jss.getRefService(MSRM.class), lang.toString());
			// 格式化通知内容
			mail = this.formatMailContent(erpFactory, itemNo, schemaName, mail, success, jobId);
			List<String> failedNoticeUsersList = erpi.listNotifyUsersWhenFailed(serviceGuid);
			if (!failedNoticeUsersList.contains(user.getGuid()))
			{
				failedNoticeUsersList.add(user.getGuid());
			}
			sms.sendMailToUsers(queuejob.getName(), mail, MailCategoryEnum.ERROR, null, failedNoticeUsersList, MailMessageType.ERPNOTIFY);
			throw new Exception(mail, e);
		}
		finally
		{
			DynaLogger.info("ERPIJob.perform() end");
		}
	}

	private BooleanResult exportBatchRS(FoundationObject foundationObject, LanguageEnum lang, JSSImpl jss, Queue queuejob, ERPServiceConfig serviceConfig, String userId)
			throws Exception
	{
		if (foundationObject != null && foundationObject.getStatus().equals(SystemStatusEnum.RELEASE))
		{
			List<ObjectGuid> objectGuids = new ArrayList<ObjectGuid>();
			BOAS boas = jss.getRefService(BOAS.class);
			ERPI erpi = jss.getRefService(ERPI.class);
			RelationTemplateInfo template = emm.getRelationTemplateByName(foundationObject.getObjectGuid(), ReplaceSubstituteConstants.BATCHRS_ITEM);
			ViewObject view = boas.getRelationByEND1(foundationObject.getObjectGuid(), template.getName());
			if (view != null)
			{
				List<FoundationObject> masterItemList = boas.listFoundationObjectOfRelation(view.getObjectGuid(), null, null, null, true);
				if (!SetUtils.isNullList(masterItemList))
				{
					for (FoundationObject fo : masterItemList)
					{
						objectGuids.add(fo.getObjectGuid());
					}
					return erpi.exportMergeData2ERP(objectGuids, queuejob, userId, serviceConfig);
				}
				else
				{
					throw new ServiceRequestException("No Data to be sent");
				}
			}
			else
			{
				throw new ServiceRequestException("No Data to be sent");
			}
		}
		else
		{
			String message = jss.getRefService(MSRM.class).getMSRString("ID_APP_ERPI_ERPIJOB_BRS_NOTREALSE", lang.toString());
			throw new ServiceRequestException(message);
		}
	}

	private String getExceptionStringWithCause(Throwable e, MSRM msrm, String locale)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getExceptionMsg(e, msrm, locale));
		Throwable t = e;
		while ((t = t.getCause()) != null)
		{
			sb.append("\r\n\tCaused by: " + this.getExceptionMsg(t, msrm, locale));
		}
		return sb.toString();
	}

	private String getExceptionMsg(Throwable e, MSRM msrm, String locale)
	{
		if (e instanceof ServiceRequestException)
		{
			ServiceRequestException sre = (ServiceRequestException) e;
			String msg = msrm.getMSRString(sre.getMsrId(), locale);
			if (!StringUtils.isNullString(msg))
			{
				return MessageFormat.format(msg, sre.getArgs());
			}
		}
		if (StringUtils.isNullString(e.getMessage()))
		{
			return e.getClass().getCanonicalName();
		}
		else
		{
			return e.getMessage();
		}
	}

	/**
	 * 格式：
	 * Succeed/Fail + 工厂 +编号+方案+详细信息
	 * 
	 * @param factory
	 * @param itemNo
	 * @param schema
	 * @param result
	 * @param success
	 * @return
	 */
	private String formatMailContent(String factory, String itemNo, String schema, String result, boolean success, String jobId)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(factory).append(": ").append(itemNo).append(": ").append(schema).append(". ").append(System.getProperty("line.separator"))
				.append(System.getProperty("line.separator")).append("JobID: ").append(jobId).append(System.getProperty("line.separator")).append(result);
		String mail = null;
		if (success)
		{
			mail = "Succeed: " + strBuilder.toString();
		}
		else
		{
			mail = "Fail: " + strBuilder.toString();
		}

		// 如果mail长度超过一定字符，则截断
		final int maxLength = 2000;
		if (mail.length() > maxLength)
		{
			mail = mail.substring(0, maxLength);
		}
		return mail;
	}

	@Override
	public JobResult serverPerformFail(JSSImpl jss, Queue job) throws Exception
	{
		String schemaName = job.getFieldd();
		String itemNo = null;
		String jobId = job.getFieldh();
		String guid = job.getFielda();
		String classGuid = job.getFieldb();
		ObjectGuid itemObjectGuid = new ObjectGuid(classGuid, null, guid, null);
		LanguageEnum lang = LanguageEnum.getById(job.getFieldg());
		String erpFactory = job.getFieldf();
		String mail = null;
		ERPI erpi = jss.getRefService(ERPI.class);
		SMS sms = jss.getRefService(SMS.class);
		AAS aas = jss.getRefService(AAS.class);
		User user = ((AASImpl) aas).getUser(job.getCreateUserGuid());
		String serviceGuid = job.getFieldc();
		FoundationObject foundationObject = ((BOASImpl) jss.getRefService(BOAS.class)).getFoundationStub().getObject(itemObjectGuid, false);
		DynaLogger.debug(foundationObject);
		itemNo = foundationObject.getFullName();
		mail = jss.getRefService(MSRM.class).getMSRString("ID_APP_JSS_JOB_FAIL_SERVER_RESTART", lang.toString());
		// 格式化通知内容
		mail = this.formatMailContent(erpFactory, itemNo, schemaName, mail, false, jobId);
		DynaLogger.debug(mail);
		// 结束后通知
		String userSelf = user.getGuid();

		List<String> failedNoticeUsersList = erpi.listNotifyUsersWhenFailed(serviceGuid);
		// 如果failedNoticeUsersList不包含创建者，则把创建者加入到通知列表中
		if (!failedNoticeUsersList.contains(userSelf))
		{
			failedNoticeUsersList.add(userSelf);
		}
		sms.sendMailToUsers(job.getName(), mail, MailCategoryEnum.ERROR, null, failedNoticeUsersList, MailMessageType.ERPNOTIFY);
		// 创建者已经发送邮件了，因此不需要再额外通知
		return JobResult.failed(mail, false);
	}

	/**
	 * 是否为批量取替代申请单
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean isHasBathRS(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid != null)
		{
			ClassInfo classInfo = this.emm.getClassByGuid(objectGuid.getClassGuid());
			if (classInfo != null && classInfo.hasInterface(ModelInterfaceEnum.IRSApplyForm))
			{
				return true;
			}
		}
		return false;
	}
}
