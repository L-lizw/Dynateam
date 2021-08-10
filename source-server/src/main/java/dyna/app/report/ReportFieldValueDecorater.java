/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaValueDecorater 获取报表字段值接口
 * cuilei 2012-2-8
 */
package dyna.app.report;

import net.sf.jasperreports.engine.JRField;
import dyna.common.bean.data.DynaObject;

/**
 * 获取报表字段值接口
 * 
 * @author cuilei
 * 
 */
public interface ReportFieldValueDecorater
{
	/**
	 * 获取报表字段的值
	 * 
	 * @param field
	 * @param column
	 * @param object
	 * @return
	 */
	public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object);
}
