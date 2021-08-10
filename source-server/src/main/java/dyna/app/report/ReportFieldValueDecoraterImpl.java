/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaValueDecoraterImpl 获取报表字段值接口实现
 * cuilei 2012-2-8
 */
package dyna.app.report;

import net.sf.jasperreports.engine.JRField;
import dyna.common.bean.data.DynaObject;

/**
 * 获取报表字段值接口实现
 * 
 * @author cuilei
 * 
 */
public class ReportFieldValueDecoraterImpl implements ReportFieldValueDecorater
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see jasper.DynaValueDecorater#getFieldValue(net.sf.jasperreports.engine.JRField, jasper.DetailColumnInfo,
	 * dyna.common.bean.data.DynaObject)
	 */
	@Override
	public Object getFieldValue(JRField field, DetailColumnInfo column, DynaObject object)
	{
		if (!field.getName().equals(column.getPropertyName()))
		{
			return object.get(field.getName().replace("#", "$"));
		}
		return object.get(field.getName());
	}

}
