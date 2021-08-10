/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataSourceProvider 获取报表数据源
 * cuilei 2012-2-7
 */
package dyna.app.report;

import java.util.List;
import java.util.Map;

import dyna.common.bean.data.DynaObject;
import dyna.common.exception.ServiceRequestException;

/**
 * 获取报表数据源
 * 
 * @author cuilei
 * 
 */
public interface ReportDataProvider<T extends DynaObject>
{
	/**
	 * 获取报表数据列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<T> getDataList() throws ServiceRequestException;

	/**
	 * 设置报表需要的参数
	 * 
	 * @param map
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<String, Object> getHeaderParameter() throws ServiceRequestException;

	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter() throws ServiceRequestException;

	public GenericReportParams getParams();

	public void setDataList(List<T> dataList);

	public Class<T> getResultClass();

}
