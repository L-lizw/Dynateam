/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MapJRDataSource 解析并绑定报表数据源
 * navy 2012-1-12
 */
package dyna.app.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.DynaObject;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * 解析并绑定报表数据源
 * 
 * @author cuilei
 * 
 */
public class GenericDynaDataSource<T extends DynaObject> implements JRDataSource
{
	private List<T>								reportDataCache	= null;
	private int									dataSize		= 0;
	protected ReportDataProvider<T>				provider		= null;
	private final Map<String, DetailColumnInfo>	mapColumn		= new HashMap<String, DetailColumnInfo>();
	private int									index			= -1;

	public GenericDynaDataSource(ReportDataProvider<T> provider, List<DetailColumnInfo> detailFields) throws Exception
	{
		this.provider = provider;
		for (DetailColumnInfo column : detailFields)
		{
			// this.mapColumn.put(column.getPropertyName(), column);
			/**
			 * 修改3
			 */
			String[] columns = column.getPropertyName().split(",");
			for (String col : columns)
			{
				this.mapColumn.put(col, column);
			}
		}
		this.initReportData();
	}

	protected void initReportData() throws ServiceRequestException
	{
		this.reportDataCache = this.provider.getDataList();
		this.dataSize = SetUtils.isNullList(this.reportDataCache) ? 0 : this.reportDataCache.size();
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException
	{
		try
		{
			if (this.dataSize == 0)
			{
				return null;
			}

			Object value = null;
			String fieldName = field.getName();
			DetailColumnInfo column = this.mapColumn.get(fieldName);

			// $转义,将#替换为$
			if (null == column)
			{
				column = this.mapColumn.get(fieldName.replace("#", "$"));
			}

			value = column.getValueDecorater().getFieldValue(field, column, this.reportDataCache.get(this.index));
			return value;
		}
		catch (Exception e)
		{
			String message = "get field value error!fieldname = " + field.getName() + (e.getMessage() == null ? "" : ",") + e.getMessage();
			JRException jre = new JRException(message, e);
			throw jre;
		}
	}

	@Override
	public boolean next() throws JRException
	{
		this.index++;
		return (this.index < this.dataSize);
	}

	public int getDataListSize()
	{
		return this.dataSize;
	}

	public List<T> getReportDataCache()
	{
		return reportDataCache;
	}

}
