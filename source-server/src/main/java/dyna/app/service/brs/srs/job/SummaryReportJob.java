/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SummaryReportJob
 * cuilei 2012-9-3
 */
package dyna.app.service.brs.srs.job;

import java.util.List;

import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.das.jss.JSSImpl;
import dyna.app.service.das.jss.JobExecutor;
import dyna.app.service.das.jss.JobResult;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Queue;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.SMS;
import dyna.net.service.brs.SRS;
import dyna.net.service.das.MSRM;
import dyna.net.spi.ServiceProvider;

/**
 * @author cuilei
 * 
 */
public class SummaryReportJob implements JobExecutor
{

	private SRS	srs	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.das.jss.JobExecutor#perform(dyna.app.service.das.jss.JSSImpl,
	 * dyna.common.bean.data.system.Queue)
	 */
	@Override
	public JobResult perform(JSSImpl jss, Queue job) throws Exception
	{
		// TODO Auto-generated method stub
		String[] login = job.getFieldb().split(SRS.JOIN_CHAR);

		String session = ((AASImpl) jss.getAAS()).getLoginStub().login(login[0], login[1], login[2], LanguageEnum.getById(login[3]));

		ServiceProvider serviceProvider = ServiceProviderFactory.getServiceProvider();
		this.srs = serviceProvider.getServiceInstance(SRS.class, session);

		// SearchCondition searchCondition = null;

		ObjectGuid productObjectGuid = this.srs.getObjectGuidByStr(job.getFielda());
		if (productObjectGuid == null)
		{
			return JobResult.failed("class can not be null!");
		}

		// SearchCondition searchConditionItem = SearchConditionFactory.createSearchCondition(productObjectGuid, null,
		// false);
		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(job.getFieldk(), null, false);

		String classguidkey = job.getFieldl();
		String classificationkey = job.getFieldn();
		if (classguidkey != null)
		{
			searchCondition.addFilter(classguidkey, job.getFieldm(), OperateSignEnum.EQUALS);
		}
		if (classificationkey != null)
		{
			searchCondition.addFilter(classificationkey, job.getFieldo(), OperateSignEnum.YES);
		}

		List<String> boGuidList = this.srs.rebuildStrToList(job.getFieldc());
		if (boGuidList != null && boGuidList.size() > 0)
		{
			searchCondition.setBOGuidList(boGuidList);
		}

		List<String> classificationGuidList = this.srs.rebuildStrToList(job.getFieldd());
		if (classificationGuidList != null && classificationGuidList.size() > 0)
		{
			searchCondition.setClassificationList(classificationGuidList);
		}

		searchCondition.setSearchValue(job.getFielde());

		if (null != job.getFieldf())
		{
			String[] orderMaps = job.getFieldf().split(SRS.JOIN_CHAR);
			for (String orderMap : orderMaps)
			{
				String[] maps = orderMap.substring(1, orderMap.length() - 1).split("=");
				searchCondition.addOrder(maps[0].trim(), maps[1].trim().equals("true") ? true : false);
			}
		}

		String[] pages = job.getFieldh().split(SRS.JOIN_CHAR);
		searchCondition.setPageNum(Integer.valueOf(pages[0]).intValue());
		searchCondition.setPageSize(Integer.valueOf(pages[1]).intValue());

		String[] reports = job.getFieldi().split(SRS.JOIN_CHAR);
		ReportTypeEnum exportFileType = ReportTypeEnum.valueOf(reports[0]);
		String bomReportTemplateName = reports[1];
		String reportName = reports[2];
		String pagesize = reports[3];
		String reportpath = reports[4];

		String relationTemplateName = job.getFieldj();

		try
		{
			this.srs.reportGenericProductSummaryObject(productObjectGuid, searchCondition, relationTemplateName, exportFileType, bomReportTemplateName, reportName, pagesize,
					reportpath, job.getGuid());
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
		String[] login = job.getFielda().split("-");
		LanguageEnum lang = LanguageEnum.getById(login[3]);

		String session = ((AASImpl) jss.getAAS()).getLoginStub().login(login[0], login[1], login[2], lang);

		ServiceProvider serviceProvider = ServiceProviderFactory.getServiceProvider();
		SMS sms = serviceProvider.getServiceInstance(SMS.class, session);
		MSRM msrm = serviceProvider.getServiceInstance(MSRM.class, session);
		BOAS boas = serviceProvider.getServiceInstance(BOAS.class, session);

		ObjectGuid productObjectGuid = this.srs.getObjectGuidByStr(job.getFielda());
		FoundationObject foundation = boas.getObject(productObjectGuid);

		String[] reports = job.getFieldi().split("-");
		String reportName = reports[2];

		String message = msrm.getMSRString("ID_APP_JSS_JOB_FAIL_SERVER_RESTART", lang.toString());
		String errMsg = foundation.getFullName() + "_" + reportName + " | " + msrm.getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());

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
