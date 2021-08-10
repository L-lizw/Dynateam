/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PreSearchRepeortJob
 * cuilei 2012-6-26
 */
package dyna.app.service.brs.srs.job;

import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.das.jss.JSSImpl;
import dyna.app.service.das.jss.JobExecutor;
import dyna.app.service.das.jss.JobResult;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.DataRule;
import dyna.common.dto.Queue;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.CPB;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.SMS;
import dyna.net.service.brs.SRS;
import dyna.net.service.das.MSRM;
import dyna.net.spi.ServiceProvider;

/**
 * @author cuilei
 * 
 */
public class ConfigBOMRepeortJob implements JobExecutor
{
	private SRS srs = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.das.jss.JobExecutor#perform(dyna.app.service.das.jss.JSSImpl,
	 * dyna.common.bean.data.system.Queue)
	 */
	@Override
	public JobResult perform(JSSImpl jss, Queue job) throws Exception
	{
		String[] login = job.getFieldd().split(SRS.JOIN_CHAR);

		String session = ((AASImpl) jss.getAAS()).getLoginStub().login(login[0], login[1], login[2], LanguageEnum.getById(login[3]));

		ServiceProvider serviceProvider = ServiceProviderFactory.getServiceProvider();
		this.srs = serviceProvider.getServiceInstance(SRS.class, session);
		BOAS boas = serviceProvider.getServiceInstance(BOAS.class, session);
		CPB cpb = serviceProvider.getServiceInstance(CPB.class, session);
		EMM emm = serviceProvider.getServiceInstance(EMM.class, session);

		ObjectGuid objectGuid = this.srs.getObjectGuidByStr(job.getFielda());
		ClassInfo classInfo = emm.getClassByGuid(objectGuid.getClassGuid());
		FoundationObject instance = boas.getObject(objectGuid);

		FoundationObject draw = null;

		// 在物料上导出
		if (classInfo.hasInterface(ModelInterfaceEnum.IItem))
		{
			// 图纸不为空
			if (!StringUtils.isNullString(job.getFieldg()))
			{
				ObjectGuid drawObjectGuid = this.srs.getObjectGuidByStr(job.getFieldg());
				draw = boas.getObject(drawObjectGuid);
			}
			// 图纸为空,根据物料取图纸
			else
			{
				DataRule dataRule = new DataRule();
				if (!StringUtils.isNullString(job.getFieldh()))
				{
					dataRule.setSystemStatus(SystemStatusEnum.getStatusEnum(job.getFieldh()));
				}
				if (!StringUtils.isNullString(job.getFieldi()))
				{
					dataRule.setLocateTime(DateFormat.parse(job.getFieldi(), DateFormat.PTN_YMD));
				}

				draw = cpb.getDrawInstanceByItem(instance, dataRule.getLocateTime());
			}
		}
		// 在订单上导出
		else
		{
			classInfo = emm.getFirstLevelClassByItem();
		}

		ReportTypeEnum reportType = ReportTypeEnum.valueOf(job.getFieldc());

		String resultFieldNameStr = job.getFielde();
		String resultUINameStr = job.getFieldf();

		SearchCondition sc = SearchConditionFactory.createSearchCondition4Class(classInfo.getName(), null, true);
		if (!StringUtils.isNullString(resultUINameStr))
		{
			String[] uiNameArr = resultUINameStr.split(",");
			for (String uiName : uiNameArr)
			{
				sc.addResultUIObjectName(uiName);
			}
		}
		if (!StringUtils.isNullString(resultFieldNameStr))
		{
			String[] uiFieldNameArr = resultFieldNameStr.split(",");
			for (String uiFieldName : uiFieldNameArr)
			{
				sc.addResultField(uiFieldName);
			}
		}

		String level = job.getFieldb();

		try
		{
			srs.reportConfigBOM(instance, draw, reportType, sc, job.getGuid(), level == null ? -1 : Integer.valueOf(level));
		}
		finally
		{
			AAS aas = serviceProvider.getServiceInstance(AAS.class, session);
			aas.logout();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.das.jss.JobExecutor#serverPerformFail(dyna.app.service.das.jss.JSSImpl,
	 * dyna.common.bean.data.system.Queue)
	 */
	@Override
	public JobResult serverPerformFail(JSSImpl jss, Queue job) throws Exception
	{
		String[] login = job.getFieldd().split("-");
		LanguageEnum lang = LanguageEnum.getById(login[3]);
		String session = ((AASImpl) jss.getAAS()).getLoginStub().login(login[0], login[1], login[2], lang);

		ServiceProvider serviceProvider = ServiceProviderFactory.getServiceProvider();
		SMS sms = serviceProvider.getServiceInstance(SMS.class, session);
		MSRM msrm = serviceProvider.getServiceInstance(MSRM.class, session);
		BOAS boas = serviceProvider.getServiceInstance(BOAS.class, session);

		ObjectGuid itemObjectGuid = this.srs.getObjectGuidByStr(job.getFielda());
		FoundationObject instance = boas.getObject(itemObjectGuid);

		String message = msrm.getMSRString("ID_APP_JSS_JOB_FAIL_SERVER_RESTART", lang.toString());
		String errMsg = (instance != null ? instance.getFullName() : "") + " " + msrm.getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());

		try
		{
			sms.sendMail4Report(message, errMsg, MailCategoryEnum.ERROR, login[0], null);
		}
		finally
		{
			AAS aas = serviceProvider.getServiceInstance(AAS.class, session);
			aas.logout();
		}

		return JobResult.failed(message);
	}
}
