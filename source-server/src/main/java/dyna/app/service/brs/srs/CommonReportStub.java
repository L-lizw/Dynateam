/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CommonReportStub
 * Wanglei 2011-12-21
 */
package dyna.app.service.brs.srs;

import java.util.Map;

import dyna.app.report.DynaReportBuilderFactory;
import dyna.app.report.GenericDynaReportBuilder;
import dyna.app.report.ReportConfiguration;
import dyna.app.report.ReportDataProvider;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.DynaObject;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.customization.report.ReportBuilder;

/**
 * @author Wanglei
 *
 */
public class CommonReportStub extends AbstractServiceStub<SRSImpl>
{

	public static final String REPORT_PACKAGE_BASE = "dyna.customization.report";

	/**
	 * @param context
	 * @param service
	 */
	protected CommonReportStub(ServiceContext context, SRSImpl service)
	{
		super(context, service);
	}

	/**
	 * 获取报表创建器
	 * 
	 * @param classGuid
	 *            类guid
	 * @param className
	 *            类名
	 * @param bomScriptFileName
	 *            报表UI名
	 * @return
	 */
	protected ReportBuilder getReportBuilder(String classGuid, String className, String bomScriptFileName) throws ServiceRequestException
	{
		String clasPackage = className;
		if (StringUtils.isNullString(clasPackage))
		{
			ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);
			clasPackage = classInfo.getName();
		}
		String reportBuilderClassName = REPORT_PACKAGE_BASE + "." + clasPackage + "." + bomScriptFileName;
		try
		{
			@SuppressWarnings("rawtypes")
			Class reportBuilderClass = null;
			try
			{
				reportBuilderClass = Class.forName(reportBuilderClassName);
			}
			catch (ClassNotFoundException e)
			{
				throw new ServiceRequestException("ID_APP_INVALID_REPORT_BUILDER", e.getMessage(), e);
			}

			if (!ReportBuilder.class.isAssignableFrom(reportBuilderClass))
			{
				throw new ServiceRequestException("ID_APP_INVALID_REPORT_BUILDER", "invalid report builder: " + reportBuilderClassName);
			}

			return (ReportBuilder) reportBuilderClass.newInstance();
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("ID_APP_REPORT_BUILDER_INIT_FAILED", e.getLocalizedMessage(), e);
		}
	}

	protected Map<String, Object> buildReport(ReportBuilder builder, Object... params) throws ServiceRequestException
	{
		builder.setReportParameters(params);
		Map<String, Object> data = null;
		try
		{
			builder.setCreatorId(this.stubService.getUserSignature().getUserId());
			data = builder.build(this.stubService);
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}

			throw new ServiceRequestException("ID_APP_BOM_REPORT_ERROR", e.getLocalizedMessage(), e);
		}

		if (builder.isNotifyCreator())
		{
			builder.notifyCreator(this.stubService);
		}
		return data;
	}

	protected <T extends DynaObject> void buildGenericReport(ReportConfiguration configuration, ReportDataProvider<T> provider) throws ServiceRequestException
	{
		GenericDynaReportBuilder reportBuilder = DynaReportBuilderFactory.createGenericDynaReportBuilder();
		try
		{
			reportBuilder.generateReport(configuration, provider, null);
		}
		catch (Exception e)
		{
			DynaLogger.error(e.getMessage(), e);
			throw new ServiceRequestException("ID_APP_GENERIC_REPORT_FAILED", "report error: " + e.getMessage());
		}
	}
}
