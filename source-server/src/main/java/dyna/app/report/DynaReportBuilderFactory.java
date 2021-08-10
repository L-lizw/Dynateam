/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaReportFactory 报表构建器工厂
 * Wanglei 2012-2-8
 */
package dyna.app.report;

/**
 * 报表构建器工厂
 * 
 * @author Wanglei
 * 
 */
public class DynaReportBuilderFactory
{

	/**
	 * 创建通用报表构建器
	 * 
	 * @return
	 */
	public static GenericDynaReportBuilder createGenericDynaReportBuilder()
	{
		return new GenericDynaReportBuilderImpl();
	}
}
