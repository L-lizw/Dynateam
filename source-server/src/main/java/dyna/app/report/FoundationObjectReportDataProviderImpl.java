/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoundationObjectReportDataProviderImpl
 * cuilei 2012-3-22
 */
package dyna.app.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author cuilei
 * 
 */
public class FoundationObjectReportDataProviderImpl implements ReportDataProvider<FoundationObject>
{

	private Class<FoundationObject>	resultClass		= FoundationObject.class;
	private List<FoundationObject>	list			= null;
	@SuppressWarnings("unused")
	private SearchCondition			reportcondition	= null;
	private FoundationObject		foundation		= null;
	private GenericReportParams		params			= null;
	private boolean					refreshData		= true;
	private List<FoundationObject>	dataList		= new ArrayList<FoundationObject>();

	public FoundationObjectReportDataProviderImpl(List<FoundationObject> list, SearchCondition reportcondition, GenericReportParams params)
	{
		this.list = list;
		this.reportcondition = reportcondition;
		this.params = params;
	}

	public FoundationObjectReportDataProviderImpl(FoundationObject foundation, List<FoundationObject> list, GenericReportParams params)
	{
		this.list = list;
		this.foundation = foundation;
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
			if (SetUtils.isNullList(this.list))
			{
				this.list.add(new FoundationObjectImpl());
			}

			GenericReportUtil reportUtil = new GenericReportUtil(this.params);

			dataList.clear();
			for (int i = 0; i < this.list.size(); i++)
			{
				FoundationObjectImpl fo = (FoundationObjectImpl) this.list.get(i);
				if (!fo.getObjectGuid().hasAuth())
				{
					continue;
				}

				List<FoundationObject> clfList = fo.restoreAllClassification(false);

				FoundationObject foundationObject = new FoundationObjectImpl();
				List<DetailColumnInfo> detailColumnsList = params.getDetailColumnList();
				for (DetailColumnInfo column : detailColumnsList)
				{
					String fieldName = column.getPropertyName().replace("#", "$").toUpperCase();
					if (fieldName.startsWith("CF$"))
					{
						String classificationMasterName = fieldName.substring(3, fieldName.indexOf("$", 3));
						for (FoundationObject clf : clfList)
						{
							String masterName = this.getClassificationMasterName(clf);
							if (classificationMasterName.equalsIgnoreCase(masterName))
							{
								String classificationItemGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATION.getName());
								String realFieldName = fieldName.substring(fieldName.indexOf("$", 3) + 1);
								foundationObject.put(column.getPropertyName(),
										reportUtil.getFieldValue((FoundationObjectImpl) clf, classificationItemGuid, realFieldName, true, column.getPropertiesMap()));

								break;
							}
						}
					}
					else
					{
						if ("BOTITLE$".equals(fieldName) && fo.get("BOTITLE$") != null && !fo.get("BOTITLE$").toString().contains("["))
						{
							foundationObject.put(column.getPropertyName(), "[" + StringUtils.getMsrTitle((String) fo.get("BOTITLE$"), params.getLang().getType()) + "]");
						}
						else if (fieldName.toString().endsWith("$TITLE"))
						{
							String realProperty = fieldName.toString().substring(0, fieldName.toString().length() - 6);
							foundationObject.put(column.getPropertyName(), reportUtil.getFieldValue(fo, realProperty, column.getPropertiesMap()));
						}
						else
						{
							foundationObject.put(column.getPropertyName(), reportUtil.getFieldValue(fo, fieldName, column.getPropertiesMap()) == null ? fo.get(fieldName)
									: reportUtil.getFieldValue(fo, fieldName, column.getPropertiesMap()));
						}
					}
				}
				dataList.add(foundationObject);
			}
		}
		return dataList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#getHeaderParameter(java.util.Map)
	 */
	@Override
	public Map<String, Object> getHeaderParameter() throws ServiceRequestException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reportcondition", "");

		GenericReportUtil reportUtil = new GenericReportUtil(this.params);

		if (this.list.size() == 1)
		{
			FoundationObjectImpl f = (FoundationObjectImpl) this.list.get(0);
			f = (FoundationObjectImpl) params.getBOAS().getObject(f.getObjectGuid());
			List<FoundationObject> clfList = f.restoreAllClassification(false);

			List<ParameterColumnInfo> headerColumnsList = params.getHeaderColumnList();
			for (ParameterColumnInfo column : headerColumnsList)
			{
				String fieldName = column.getParameterName().replace("#", "$");
				if ("reportTitle".equalsIgnoreCase(fieldName) || "reportcondition".equalsIgnoreCase(fieldName) || "pageSize".equalsIgnoreCase(fieldName))
				{
					continue;
				}
				if (fieldName.startsWith("CF$"))
				{
					String classificationMasterName = fieldName.substring(3, fieldName.indexOf("$", 3));
					for (FoundationObject clf : clfList)
					{
						String masterName = this.getClassificationMasterName(clf);
						if (classificationMasterName.equalsIgnoreCase(masterName))
						{
							String classificationItemGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATION.getName());
							String realFieldName = fieldName.substring(fieldName.indexOf("$", 3) + 1);
							map.put(column.getParameterName(),
									reportUtil.getFieldValue((FoundationObjectImpl) clf, classificationItemGuid, realFieldName, true, column.getPropertiesMap()));

							break;
						}
					}
				}
				else
				{
					if ("BOTITLE$".equals(fieldName) && !f.get("BOTITLE$").toString().contains("["))
					{
						map.put(column.getParameterName(), "[" + StringUtils.getMsrTitle((String) f.get("BOTITLE$"), params.getLang().getType()) + "]");
					}
					else if (fieldName.toString().endsWith("$TITLE"))
					{
						String realProperty = fieldName.toString().substring(0, fieldName.toString().length() - 6);
						map.put(column.getParameterName(), reportUtil.getFieldValue(f, realProperty, column.getPropertiesMap()));
					}
					else
					{
						map.put(column.getParameterName(), reportUtil.getFieldValue(f, fieldName, column.getPropertiesMap()));
					}
				}
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter() throws ServiceRequestException
	{
		Map<String, Object> map = new HashMap<String, Object>();

		FoundationObjectImpl f = (FoundationObjectImpl) this.foundation;
		GenericReportUtil reportUtil = new GenericReportUtil(this.params);

		List<ParameterColumnInfo> headerColumnsList = params.getHeaderColumnList();
		for (ParameterColumnInfo column : headerColumnsList)
		{
			String realColumn = column.getParameterName().replace("#", "$");
			if ("BOTITLE$".equals(realColumn) && !f.get("BOTITLE$").toString().contains("["))
			{
				map.put(column.getParameterName(), "[" + StringUtils.getMsrTitle((String) f.get("BOTITLE$"), params.getLang().getType()) + "]");
			}
			else if (realColumn.toString().endsWith("$TITLE"))
			{
				String realProperty = realColumn.toString().substring(0, realColumn.toString().length() - 6);
				map.put(column.getParameterName(), reportUtil.getFieldValue(f, realProperty, column.getPropertiesMap()));
			}
			else
			{
				map.put(column.getParameterName(), reportUtil.getFieldValue(f, realColumn, column.getPropertiesMap()) == null);
			}
		}

		return map;
	}

	private String getClassificationMasterName(FoundationObject clf) throws ServiceRequestException
	{
		String classificationGroupName = (String) clf.get("CLASSIFICATIONGROUP$NAME");
		String classificationItemGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATION.getName());

		if (classificationGroupName == null)
		{
			String classificationGroupGuid = (String) clf.get(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName());
			if (StringUtils.isGuid(classificationGroupGuid))
			{
				CodeObjectInfo codeObjectInfo = this.params.getEMM().getCode(classificationGroupGuid);
				if (codeObjectInfo != null)
				{
					classificationGroupName = codeObjectInfo.getName();
				}
			}
			else
			{
				if (StringUtils.isGuid(classificationItemGuid))
				{
					CodeItemInfo codeItemInfo = this.params.getEMM().getCodeItem(classificationItemGuid);
					if (codeItemInfo != null)
					{
						CodeObjectInfo code = this.params.getEMM().getCode(codeItemInfo.getCodeGuid());
						if (code != null)
						{
							classificationGroupName = code.getName();
						}
					}
				}
			}
		}

		return classificationGroupName;
	}

	public void setDataList(List<FoundationObject> dataList)
	{
		this.dataList = dataList;
		this.refreshData = false;
	}

	@Override
	public GenericReportParams getParams()
	{
		return params;
	}

	@Override
	public Class<FoundationObject> getResultClass()
	{
		return this.resultClass;
	}

}
