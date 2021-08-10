/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CustomJasperReport 自定义报表生成器
 * cuilei 2012-2-7
 */
package dyna.app.report;

import java.io.File;
import java.util.List;
import java.util.Map;

import dyna.app.service.brs.srs.ECReportProviderImpl;
import dyna.common.bean.data.DynaObject;

/**
 * 报表生成器
 * 
 * @author cuilei
 * 
 */
public interface GenericDynaReportBuilder
{
	/**
	 * 自定义生成（通用）报表模板
	 * 
	 * @param configuration
	 *            报表配置
	 * @param provider
	 *            报表数据提供者
	 * @param reportTemplateFile
	 *            报表模板
	 * @throws Exception
	 *             报表生成过程中产生的异常
	 */
	public <T extends DynaObject> void generateReport(ReportConfiguration configuration, ReportDataProvider<T> provider, File reportTemplateFile) throws Exception;

	/**
	 * 定制化报表
	 * 
	 * @param <T>
	 * @param reportTemplateFile
	 *            模板路径
	 * @param provider
	 *            数据源
	 * @param configuration
	 *            报表导出条件
	 * @throws Exception
	 */
	public <T extends DynaObject> void personalizedReport(File reportTemplateFile, ReportDataProvider<T> provider, ReportConfiguration configuration,
			Map<String, Object> headerParameters) throws Exception;

	@SuppressWarnings("rawtypes")
	public void personalizedReportVO(File reportTemplateFile, ReportDataProvider provider, ReportConfiguration configuration, Map<String, Object> headerParameters, List dataList)
			throws Exception;

	public <T extends DynaObject> void personalizedReportByEC(File templateFile, List<Map<String, Object>> provider, ReportConfiguration configuration,
			Map<String, Object> headerParameters) throws Exception;
}
