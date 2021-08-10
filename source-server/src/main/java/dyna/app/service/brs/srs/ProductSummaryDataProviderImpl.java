/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProductSummaryDataProviderImpl
 * cuilei 2012-9-4
 */
package dyna.app.service.brs.srs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dyna.app.report.GenericReportParams;
import dyna.app.report.GenericReportUtil;
import dyna.app.report.ParameterColumnInfo;
import dyna.app.report.ReportDataProvider;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;

/**
 * @author cuilei
 * 
 */
public class ProductSummaryDataProviderImpl implements ReportDataProvider<FoundationObject>
{
	private Class<FoundationObject>	resultClass				= FoundationObject.class;
	private SearchCondition			searchCondition			= null;
	private ObjectGuid				productObjectGuid		= null;
	private GenericReportParams		params					= null;
	private String					relationTemplateName	= null;
	private boolean					refreshData				= true;
	private List<FoundationObject>	dataList				= new ArrayList<FoundationObject>();

	public ProductSummaryDataProviderImpl(SearchCondition searchCondition, String relationTemplateName, ObjectGuid productObjectGuid, GenericReportParams params)
	{
		this.searchCondition = searchCondition;
		this.productObjectGuid = productObjectGuid;
		this.relationTemplateName = relationTemplateName;
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#getDataList()
	 */
	@Override
	public List<FoundationObject> getDataList() throws ServiceRequestException
	{
		if (refreshData)
		{
			if (params == null)
			{
				return new ArrayList<FoundationObject>();
			}

			try
			{
				List<FoundationObject> list = this.params.getPMS().listProductSummaryObject(productObjectGuid, searchCondition, this.relationTemplateName);
				if (list.size() == 0 || list == null)
				{
					list.add(new FoundationObjectImpl());
					return list;
				}

				dataList.clear();

				int i = 1;
				for (FoundationObject foundation : list)
				{
					FoundationObjectImpl data = (FoundationObjectImpl) this.params.getBOAS().getObject(foundation.getObjectGuid());
					data.put("NUMBER", i);
					dataList.add(data);
					i++;
				}
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
		}
		return dataList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#getHeaderParameter()
	 */
	@Override
	public Map<String, Object> getHeaderParameter() throws ServiceRequestException
	{
		if (params == null)
		{
			return new HashMap<String, Object>();
		}

		GenericReportUtil reportUtil = new GenericReportUtil(params);

		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			FoundationObject headObject = this.params.getBOAS().getObject(productObjectGuid);
			for (ParameterColumnInfo fieldInfo : this.params.getHeaderColumnList())
			{
				map.put(fieldInfo.getParameterName(),
						reportUtil.getFieldValue((FoundationObjectImpl) headObject, fieldInfo.getParameterName().replace("#", "$"), fieldInfo.getPropertiesMap()));
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}

		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.app.report.ReportDataProvider#getWBSAndDeliverablesReportHeaderParameter
	 * ()
	 */
	@Override
	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter()
	{
		return null;
	}

	@Override
	public GenericReportParams getParams()
	{
		return params;
	}

	@Override
	public void setDataList(List<FoundationObject> dataList)
	{
		this.dataList = dataList;
		this.refreshData = false;
	}

	@Override
	public Class<FoundationObject> getResultClass()
	{
		return this.resultClass;
	}

}
