/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMReportStub
 * Wanglei 2011-12-21
 */
package dyna.app.service.brs.srs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.report.DetailColumnInfo;
import dyna.app.report.GenericDynaReportBuilder;
import dyna.app.report.GenericDynaReportBuilderImpl;
import dyna.app.report.GenericReportParams;
import dyna.app.report.GenericReportUtil;
import dyna.app.report.ParameterColumnInfo;
import dyna.app.report.ReportConfiguration;
import dyna.app.report.ReportDataProvider;
import dyna.app.report.ReportFieldValueDecorater;
import dyna.app.report.ResolveReportTemplateField;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.SearchCondition;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.customization.report.ReportBuilder;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.util.JRFontNotFoundException;

/**
 * @author Wanglei
 * 
 */
public class BOMReportStub extends AbstractServiceStub<SRSImpl>
{
	private SRSImpl	service	= null;

	/**
	 * @param context
	 * @param service
	 * @throws ServiceRequestException
	 */
	protected BOMReportStub(ServiceContext context, SRSImpl service)
	{
		super(context, service);
		this.service = service;
	}

	protected Map<String, Object> reportBOM(ObjectGuid bomViewObjectGuid, int level, SearchCondition bomSearchCondition, String bomScriptFileName, ReportTypeEnum exportFileType,
			String bomReportName, String exportType, String levelStyle, String groupStyle) throws ServiceRequestException
	{
		ReportBuilder reportBuilder = this.stubService.getCommonReportStub()
				.getReportBuilder(bomViewObjectGuid.getClassGuid(), bomViewObjectGuid.getClassName(), bomScriptFileName);
		return this.stubService.getCommonReportStub().buildReport(reportBuilder, bomViewObjectGuid, level, bomSearchCondition, exportFileType, bomReportName, exportType,
				levelStyle, groupStyle);
	}

	protected File reportGenericBOM(ObjectGuid bomViewObjectGuid, DataRule dataRule, int level, SearchCondition bomSearchCondition, ReportTypeEnum exportFileType,
			LanguageEnum lang, String bomReportName, String exportType, String levelStyle, String groupStyle, String bomReportTemplateName, String isExportAllLevel,
			List<String> summaryFiledName, String pagesize, String reportpath, List<String> classGuids, boolean isContainRepf) throws ServiceRequestException
	{
		FoundationObject foundation = this.stubService.getBOAS().getObject(bomViewObjectGuid);
		ReportConfiguration configuration = new ReportConfiguration();
		configuration.setExportFileType(exportFileType);
		configuration.setExportToFilePath(GenericReportUtil.getFile(foundation.getId() + "_" + bomReportName, exportFileType));
		if (!StringUtils.isNullString(pagesize))
		{
			configuration.setPageCount(Integer.valueOf(pagesize));
		}

		String templateName = bomReportTemplateName.replace(".jrxml", "");
		File templateFile = null;
		if (!StringUtils.isNullString(exportType))
		{
			if (exportType.equals("bomtree"))
			{
				templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/bomReport/" + bomReportTemplateName);
			}
			else if (exportType.equals("bomlist"))
			{
				templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/bomReport/" + bomReportTemplateName);
			}
			else if (exportType.equals("group"))
			{
				templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/bomReport/" + bomReportTemplateName);
			}
		}
		else if (!StringUtils.isNullString(isExportAllLevel) || !SetUtils.isNullList(summaryFiledName))
		{
			templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/bomSummaryReport/" + bomReportTemplateName);
		}
		else
		{
			templateFile = new File(EnvUtils.getConfRootPath() + "conf/comment/report/bomReport/" + bomReportTemplateName);
		}

		ResolveReportTemplateField resolveReportTemplateField = new ResolveReportTemplateField();
		List<DetailColumnInfo> columnList = resolveReportTemplateField.getReportTemplateField(templateFile);
		List<ParameterColumnInfo> parameters = resolveReportTemplateField.getReportTemplateParameters(templateFile);

		Map<String, Object> otherParams = new HashMap<String, Object>();
		otherParams.put("level", level);
		otherParams.put("isExportAllLevel", isExportAllLevel);
		otherParams.put("summaryFiledName", summaryFiledName);
		otherParams.put("exportType", exportType);
		otherParams.put("levelStyle", levelStyle);
		otherParams.put("classGuids", classGuids);
		otherParams.put("isContainRepf", BooleanUtils.getBooleanStringYN(isContainRepf));

		GenericReportParams params = service.createGenericReportParamsWithService();
		params.setLang(lang);
		params.setUiObject(null);
		params.setHeaderColumnList(parameters);
		params.setDetailColumnList(columnList);
		params.setOtherParams(otherParams);

		configuration.setDetailColumnInfoList(columnList);
		ReportDataProvider<BOMStructure> provider = new ReportDataProviderGenericBOMImpl(bomViewObjectGuid, dataRule, bomSearchCondition, params);

		try
		{
			if (!SetUtils.isNullList(summaryFiledName))
			{
				configuration.setGroupFields(summaryFiledName);
			}
			genericBOM(exportType, groupStyle, provider, lang, configuration, templateName, summaryFiledName);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			DynaLogger.error(e);
			if (e instanceof JRFontNotFoundException)
			{
				String message = this.service.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR_FONT", lang.toString());
				e = new Exception(message, e);

				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR_FONT", "," + e.getMessage());
			}
			else if (e.getMessage().contains("Error evaluating expression"))
			{
				String message = e.getMessage();
				message = this.service.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString()) + "("
						+ e.getMessage().substring(message.indexOf("{") + 1, message.length() - 1) + ")";
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
			else if (e.getCause() instanceof FileNotFoundException)
			{
				String message = this.service.getMSRM().getMSRString("ID_APP_NOTFOUND_REPORT_TEMPLATE", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_NOTFOUND_REPORT_TEMPLATE", "," + e.getMessage());
			}
			else if (e.toString().contains("jasperreports"))
			{
				String message = this.service.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_TEMPLATE_ERROR", "," + e.getMessage());
			}
			else
			{
				String message = this.service.getMSRM().getMSRString("ID_APP_EXPORT_REPORT_ERROR", lang.toString());
				e = new Exception(message, e);
				throw new ServiceRequestException("ID_APP_EXPORT_REPORT_ERROR", "," + e.getMessage());
			}
		}

		return configuration.getExportToFilePath();
	}

	public void genericBOM(String exportType, String groupStyle, ReportDataProvider<BOMStructure> provider, LanguageEnum lang, ReportConfiguration configuration,
			String templateName, List<String> summaryFiledName) throws Exception
	{
		GenericDynaReportBuilder reportBuilder = new GenericDynaReportBuilderImpl();
		String newReportTemplateName = "";
		// if (templateName.equals("bom_report"))
		if (!StringUtils.isNullString(exportType))
		{
			if (exportType.equals("group"))
			{
				// 按照bo分组到报表数据
				if (groupStyle.equals("1"))
				{
					// newReportTemplateName = getReportTemplateNameBylang(lang,
					// templateName + "_bo_group_template");
					newReportTemplateName = getReportTemplateNameBylang(lang, templateName + "_template");
					List<String> list = new ArrayList<String>();
					list.add("END2.BOTITLE#");
					configuration.setGroupFields(list);
					reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
							provider.getHeaderParameter());
				}
				// 按照classification分组到报表数据
				else if (groupStyle.equals("2"))
				{
					// newReportTemplateName = getReportTemplateNameBylang(lang,
					// templateName
					// + "_classification_group_template");
					newReportTemplateName = getReportTemplateNameBylang(lang, templateName + "_template");
					List<String> list = new ArrayList<String>();
					list.add("END2.CLASSIFICATION#TITLE");
					configuration.setGroupFields(list);
					reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
							provider.getHeaderParameter());
				}
				// 按照bo、classification分组到报表数据
				else
				{
					// newReportTemplateName = getReportTemplateNameBylang(lang,
					// templateName
					// + "_bo_classification_group_template");
					newReportTemplateName = getReportTemplateNameBylang(lang, templateName + "_template");
					List<String> list = new ArrayList<String>();
					list.add("END2.BOTITLE#");
					list.add("END2.CLASSIFICATION#TITLE");
					configuration.setGroupFields(list);
					reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
							provider.getHeaderParameter());
				}

			}
			else
			{
				configuration.setGroupFields(summaryFiledName);
				newReportTemplateName = getReportTemplateNameBylang(lang, templateName + "_template");
				reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
						provider.getHeaderParameter());
			}
		}
		else
		{
			newReportTemplateName = getReportTemplateNameBylang(lang, templateName + "_template");
			configuration.setGroupFields(summaryFiledName);
			reportBuilder.personalizedReport(new File(EnvUtils.getConfRootPath() + "conf/comment/report/" + newReportTemplateName), provider, configuration,
					provider.getHeaderParameter());
		}

	}

	@SuppressWarnings("unused")
	private DetailColumnInfo createDetailColumnInfo(String title, String propertyName, Class<?> typeClass)
	{
		DetailColumnInfo ret = new DetailColumnInfo(title, typeClass, propertyName);
		ret.setValueDecorater(new ReportFieldValueDecorater() {

			@Override
			public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
			{

				if (!field.getName().equals(column.getPropertyName()))
				{
					return (object.get(field.getName().replace("#", "$")) == null ? "" : object.get(field.getName().replace("#", "$")));
				}
				return (object.get(field.getName()) == null ? "" : object.get(field.getName()));
			}
		});
		return ret;
	}

	/**
	 * 设置导出报表文件后缀
	 * 
	 * @param exportFileType
	 * @return
	 */
	// private String getExportFile(ReportTypeEnum exportFileType)
	// {
	// if (exportFileType.equals(ReportTypeEnum.PDF))
	// {
	// return ".pdf";
	// }
	// else if (exportFileType.equals(ReportTypeEnum.EXCEL))
	// {
	// return ".xls";
	// }
	// else if (exportFileType.equals(ReportTypeEnum.CSV))
	// {
	// return ".csv";
	// }
	// else if (exportFileType.equals(ReportTypeEnum.HTML))
	// {
	// return ".html";
	// }
	// else if (exportFileType.equals(ReportTypeEnum.WORD))
	// {
	// return ".doc";
	// }
	// return ".xls";
	// }

	private String getReportTemplateNameBylang(LanguageEnum lang, String reportTemplateName)
	{

		String newReportTemplateName = "";
		if (lang.equals(LanguageEnum.ZH_CN))
		{
			newReportTemplateName = reportTemplateName.concat("_zh_cn.jrxml");
		}
		else if (lang.equals(LanguageEnum.ZH_TW))
		{
			newReportTemplateName = reportTemplateName.concat("_zh_tw.jrxml");
		}
		else if (lang.equals(LanguageEnum.EN))
		{
			newReportTemplateName = reportTemplateName.concat("_us_en.jrxml");
		}
		return newReportTemplateName;
	}
}