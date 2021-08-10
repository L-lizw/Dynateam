package dyna.app.service.brs.erpi.ERPIJob;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.das.jss.JSSImpl;
import dyna.app.service.das.jss.JobExecutor;
import dyna.app.service.das.jss.JobResult;
import dyna.common.bean.data.DynaObject;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.Queue;
import dyna.common.dto.aas.User;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.ERPI;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.MSRM;

public class CFERPIJob implements JobExecutor
{
	private List<String>	codeGuidList	= new ArrayList<String>();
	private String			codeTitle		= null;
	private List<String>	noticeUsers		= new ArrayList<String>();
	private EMM				emm;

	@Override
	public JobResult perform(JSSImpl jss, Queue queuejob) throws Exception
	{
		ERPI erpi = jss.getRefService(ERPI.class);
		SMS sms = jss.getRefService(SMS.class);
		emm = jss.getRefService(EMM.class);
		Object sObject = null;
		DynaObject selectionObject = null;
		boolean isRoot = false;
		LanguageEnum lang = null;
		DynaLogger.info("*****Start CF2ERP Job*****" + "\n" + "Job id:" + queuejob.getFieldh());
		String mail = null;
		String serviceGuid = queuejob.getFieldb();
		String schemaName = queuejob.getFieldd();
		ERPServiceConfig serviceConfig = erpi.getERPServiceConfigbyServiceGuid(serviceGuid);
		serviceConfig.setErpServerAddress("http://" + serviceConfig.getERPServerIP() + ":" + serviceConfig.getERPServerPort() + "/" + serviceConfig.getERPServerName());
		serviceConfig.setName(schemaName);
		serviceConfig.setSchemaName(schemaName);
		String erpType = serviceConfig.getERPServerSelected();
		lang = LanguageEnum.getById(queuejob.getFieldc());
		if ("1".equalsIgnoreCase(queuejob.getFieldg()))
		{
			isRoot = true;
		}
		codeTitle = queuejob.getFieldf();
		AAS aas = jss.getRefService(AAS.class);
		User user = ((AASImpl) aas).getUser(queuejob.getCreateUserGuid());
		String userId = "";
		if (user != null)
		{
			userId = user.getUserId();
			noticeUsers.add(user.getGuid());
		}
		DynaLogger.debug(userId);
		boolean success = false;

		String jobId = queuejob.getFieldh();
		try
		{
			if (isRoot)
			{
				sObject = emm.getCode(queuejob.getFielda());
			}
			else
			{
				sObject = emm.getCodeItem(queuejob.getFielda());
			}
			selectionObject = getSelectionObject(sObject);
			getCodeList(selectionObject, erpType);
			BooleanResult result = erpi.exportCF2ERP(codeGuidList, queuejob, userId, serviceConfig);

			DynaLogger.debug(result);
			if (result == null)
			{
				throw new NullPointerException(ERPTransferStub.NO_DATA_RECEIVED);
			}
			boolean noExecuteFlg = false;
			String erpServerType = serviceConfig.getERPServerSelected();
			if (ERPServerType.ERPT100DB.name().equals(erpServerType))
			{
				if (!result.isDataEmpty())
				{
					noExecuteFlg = true;
				}
			}
			success = result.getFlag();
			mail = result.getDetail();

			// 格式化通知内容
			mail = this.formatMailContent(schemaName, mail, success, jobId);
			DynaLogger.debug(mail);
			// 结束后通知
			if (success)
			{
				if (noExecuteFlg)
				{
					return JobResult.erpExecuting(null, false);
				}
				else
				{
					sms.sendMailToUsers(queuejob.getName(), mail, MailCategoryEnum.INFO, null, noticeUsers, MailMessageType.ERPNOTIFY);
					// 创建者已经发送邮件了，因此不需要再额外通知
					return JobResult.succeed(mail, false);
				}
			}
			else
			{
				sms.sendMailToUsers(queuejob.getName(), mail, MailCategoryEnum.ERROR, null, noticeUsers, MailMessageType.ERPNOTIFY);
				// 创建者已经发送邮件了，因此不需要再额外通知
				return JobResult.failed(mail, false);
			}
		}
		catch (Throwable e)
		{
			DynaLogger.error(e.getMessage(), e);
			mail = this.getExceptionStringWithCause(e, jss.getRefService(MSRM.class), lang.toString());
			// 格式化通知内容
			mail = this.formatMailContent(schemaName, mail, success, jobId);
			sms.sendMailToUsers(queuejob.getName(), mail, MailCategoryEnum.ERROR, null, noticeUsers, MailMessageType.ERPNOTIFY);
			throw new Exception(mail, e);
		}
		finally
		{
			DynaLogger.info("CFERPIJob.perform() end");
		}
	}

	@Override
	public JobResult serverPerformFail(JSSImpl jss, Queue job) throws Exception
	{
		String schemaName = job.getFieldc();
		String jobId = job.getFieldh();
		LanguageEnum lang = LanguageEnum.getById(job.getFieldd());
		String mail = null;
		SMS sms = jss.getRefService(SMS.class);
		mail = jss.getRefService(MSRM.class).getMSRString("ID_APP_JSS_JOB_FAIL_SERVER_RESTART", lang.toString());
		// 格式化通知内容
		mail = this.formatMailContent(schemaName, mail, false, jobId);
		DynaLogger.debug(mail);
		// 结束后通知
		sms.sendMailToUsers(job.getName(), mail, MailCategoryEnum.ERROR, null, noticeUsers, MailMessageType.ERPNOTIFY);
		// 创建者已经发送邮件了，因此不需要再额外通知
		return JobResult.failed(mail, false);

	}

	/**
	 * mail信息
	 * 格式：
	 * Succeed/Fail + code +方案+详细信息
	 * 
	 * @param schema
	 * @param result
	 * @param success
	 * @param jobId
	 * @return
	 */
	private String formatMailContent(String schema, String result, boolean success, String jobId)
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(codeTitle).append(": ").append(schema).append(". ").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
				.append("JobID: ").append(jobId).append(System.getProperty("line.separator")).append(result);
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
	 * 获得满足抛转条件的分类
	 * 
	 * @param selectionObject
	 * @param erpType
	 * @throws ServiceRequestException
	 */
	private void getCodeList(DynaObject selectionObject, String erpType) throws ServiceRequestException
	{
		List<CodeItemInfo> codedetailList = null;
		if (selectionObject == null)
		{
			throw new ServiceRequestException("selectionObject is null");
		}
		if (selectionObject instanceof CodeObjectInfo)// 判断是否为根节点
		{
			CodeObjectInfo codeObjectInfo = (CodeObjectInfo) selectionObject;
			codedetailList = DataServer.getCodeModelService().listDetailCodeItemInfo(codeObjectInfo.getGuid(), null);
		}
		else if (selectionObject instanceof CodeItemInfo)
		{
			CodeItemInfo codeItemInfo = (CodeItemInfo) selectionObject;
			codedetailList = DataServer.getCodeModelService().listDetailCodeItemInfo(null, codeItemInfo.getGuid());
		}
		if (SetUtils.isNullList(codedetailList))
		{
			if (ERPServerType.ERPT100DB.toString().equals(erpType))
			{
				this.codeGuidList.add(((CodeItemInfo) selectionObject).getGuid());
			}
			else
			{
				if (selectionObject instanceof CodeItemInfo && canExport(((CodeItemInfo) selectionObject).getGuid()))
				{
					this.codeGuidList.add(((CodeItemInfo) selectionObject).getGuid());
				}
			}
		}
		else
		{
			for (CodeItemInfo detail : codedetailList)
			{
				getCodeList(detail, erpType);
			}
		}
	}

	/**
	 * 判断该分类在分类建模器中有定义Form类型的UI（该UI中有字段存在）
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	private boolean canExport(String guid) throws ServiceRequestException
	{
		List<UIField> uiList = null;
		uiList = emm.listCFUIField(guid, UITypeEnum.FORM);
		DynaLogger.debug(uiList);
		return !SetUtils.isNullList(uiList);
	}

	private DynaObject getSelectionObject(Object sObject) throws ServiceRequestException
	{
		DynaObject result;
		CodeObjectInfo code;
		CodeObjectInfo sMaster = null;
		Map<String, CodeItemInfo> map = new HashMap<>();
		if (sObject instanceof CodeObjectInfo)
		{
			code = (CodeObjectInfo) sObject;
		}
		else
		{
			code = emm.getCode(((CodeItemInfo) sObject).getCodeGuid());
		}
		List<CodeObjectInfo> masterList = emm.listCodeInfo();
		for (CodeObjectInfo master : masterList)
		{
			if (master.getGuid().equalsIgnoreCase(code.getGuid()))
			{
				sMaster = master;
				break;
			}
		}
		if (sObject instanceof CodeObjectInfo)
		{
			result = sMaster;
		}
		else
		{
			CodeObjectInfo codeObject = this.emm.getCodeByName(sMaster.getName());
			List<CodeItemInfo> detailList = DataServer.getCodeModelService().listDetailCodeItemInfo(codeObject.getGuid(), null);
			getCodeDetail(detailList, sObject, map);
			result = map.get("result");
		}
		return result;
	}

	private void getCodeDetail(List<CodeItemInfo> codeDetailList, Object sObject, Map<String, CodeItemInfo> map)
	{
		for (CodeItemInfo detail : codeDetailList)
		{
			if (map.isEmpty())
			{
				if (detail.getGuid().equalsIgnoreCase(((CodeItemInfo) sObject).getGuid()))
				{
					map.put("result", detail);
					break;
				}
				List<CodeItemInfo> codeDetailList_ = null;
				try
				{
					codeDetailList_ = DataServer.getCodeModelService().listDetailCodeItemInfo(null, detail.getGuid());
				}
				catch (ServiceRequestException e)
				{
					e.printStackTrace();
				}
				if (!SetUtils.isNullList(codeDetailList_))
				{
					getCodeDetail(codeDetailList_, sObject, map);
				}
			}
			else
			{
				break;
			}
		}
	}

}
