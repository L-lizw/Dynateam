/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoundationObjectReportDataProviderImpl
 * cuilei 2012-3-22
 */
package dyna.app.service.brs.srs;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.report.DetailColumnInfo;
import dyna.app.report.GenericReportParams;
import dyna.app.report.GenericReportUtil;
import dyna.app.report.ParameterColumnInfo;
import dyna.app.report.ReportDataProvider;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author cuilei
 * 
 */
public class PreFoundationObjectReportDataProviderImpl implements ReportDataProvider<FoundationObject>
{
	private Class<FoundationObject>	resultClass	= FoundationObject.class;
	private List<FoundationObject>	list		= null;
	private GenericReportParams		params		= null;
	private boolean					refreshData	= true;
	private List<FoundationObject>	dataList	= new ArrayList<FoundationObject>();

	public PreFoundationObjectReportDataProviderImpl(List<FoundationObject> list, GenericReportParams params)
	{
		this.list = list;
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
				boolean isReSearch = false;
				FoundationObjectImpl fo = (FoundationObjectImpl) this.list.get(i);
				try
				{
					fo = (FoundationObjectImpl) params.getBOAS().getObject(this.getObjectGuid(fo));
					isReSearch = true;
				}
				catch (Exception e)
				{
				}

				String val = null;
				FoundationObject foundationObject = new FoundationObjectImpl();
				List<DetailColumnInfo> detailColumnsList = params.getDetailColumnList();
				for (DetailColumnInfo column : detailColumnsList)
				{
					String fieldName = column.getPropertyName().replace("#", "$");
					if (isReSearch)
					{
						if ("BOTITLE$".equals(fieldName) && fo.get("BOTITLE$") != null && !fo.get("BOTITLE$").toString().contains("["))
						{
							val = "[" + StringUtils.getMsrTitle((String) fo.get("BOTITLE$"), params.getLang().getType()) + "]";
						}
						else if (fieldName.toString().endsWith("$TITLE"))
						{
							String realProperty = fieldName.toString().substring(0, fieldName.toString().length() - 6);
							val = reportUtil.getFieldValue(fo, realProperty, column.getPropertiesMap());
						}
						else
						{
							val = reportUtil.getFieldValue(fo, fieldName, column.getPropertiesMap());
						}
					}
					else
					{
						val = this.getValWithoutSearch(fo, fieldName);
					}

					if (StringUtils.isNullString(val))
					{
						val = reportUtil.getFieldValue(fo, fieldName + "$", column.getPropertiesMap());
					}
					foundationObject.put(column.getPropertyName(), val);
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
			boolean isReSearch = false;
			FoundationObjectImpl f = (FoundationObjectImpl) this.list.get(0);
			try
			{
				f = (FoundationObjectImpl) params.getBOAS().getObject(this.getObjectGuid(f));
				isReSearch = true;
			}
			catch (Exception e)
			{
			}

			List<ParameterColumnInfo> headerColumnsList = params.getHeaderColumnList();
			for (ParameterColumnInfo column : headerColumnsList)
			{
				String val = null;
				String fieldName = column.getParameterName().replace("#", "$");
				if (isReSearch)
				{
					if ("BOTITLE$".equals(fieldName) && !f.get("BOTITLE$").toString().contains("["))
					{
						val = "[" + StringUtils.getMsrTitle((String) f.get("BOTITLE$"), params.getLang().getType()) + "]";
					}
					else if (fieldName.toString().endsWith("$TITLE"))
					{
						String realProperty = fieldName.toString().substring(0, fieldName.toString().length() - 6);
						val = reportUtil.getFieldValue(f, realProperty, column.getPropertiesMap());
					}
					else
					{
						val = reportUtil.getFieldValue(f, fieldName, column.getPropertiesMap());
					}
				}
				else
				{
					val = this.getValWithoutSearch(f, fieldName);
				}

				if (StringUtils.isNullString(val))
				{
					val = reportUtil.getFieldValue(f, fieldName + "$", column.getPropertiesMap());
				}
				map.put(column.getParameterName(), val);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter() throws ServiceRequestException
	{
		return null;
	}

	private String getValWithoutSearch(FoundationObject f, String fieldName)
	{
		if (f.get(fieldName) == null)
		{
			return null;
		}

		Object o = f.get(fieldName);
		String val = o.toString();
		if (o instanceof String && (fieldName.endsWith("TIME") || fieldName.endsWith("TIME$")))
		{
			val = (String) f.get(fieldName);
			try
			{
				val = DateFormat.format(DateFormat.parse(val), DateFormat.PTN_YMDHMS);
			}
			catch (Exception e)
			{
			}
		}
		else if (o instanceof Timestamp)
		{
			Timestamp date = (Timestamp) f.get(fieldName);
			val = DateFormat.format(date, DateFormat.PTN_YMDHMS);
		}
		else if (o instanceof Date)
		{
			Date date = (Date) f.get(fieldName);
			val = DateFormat.format(date, DateFormat.PTN_YMDHMS);
		}

		return val;
	}

	private ObjectGuid getObjectGuid(FoundationObject fo)
	{
		ObjectGuid objectGuid = fo.getObjectGuid();
		if (objectGuid == null)
		{
			objectGuid = new ObjectGuid();
		}

		String classGuid = objectGuid.getClassGuid();
		if (!StringUtils.isGuid(classGuid))
		{
			classGuid = (String) fo.get("CLASSGUID");
		}

		String className = objectGuid.getClassName();
		if (StringUtils.isNullString(className))
		{
			className = (String) fo.get("CLASSNAME");
		}

		String guid = objectGuid.getGuid();
		if (!StringUtils.isGuid(guid))
		{
			guid = (String) fo.get("GUID");
		}

		String masterGuid = objectGuid.getMasterGuid();
		if (!StringUtils.isGuid(masterGuid))
		{
			masterGuid = (String) fo.get("MASTERGUID");
		}

		boolean isMaster = objectGuid.isMaster();

		String bizObjectGuid = objectGuid.getBizObjectGuid();
		if (!StringUtils.isGuid(bizObjectGuid))
		{
			bizObjectGuid = (String) fo.get("BOGUID");
		}

		String commitFolderGuid = objectGuid.getCommitFolderGuid();
		if (!StringUtils.isGuid(commitFolderGuid))
		{
			commitFolderGuid = (String) fo.get("COMMITFOLDER");
		}

		objectGuid.setBizObjectGuid(bizObjectGuid);
		objectGuid.setClassGuid(classGuid);
		objectGuid.setClassName(className);
		objectGuid.setCommitFolderGuid(commitFolderGuid);
		objectGuid.setGuid(guid);
		objectGuid.setIsMaster(isMaster);
		objectGuid.setMasterGuid(masterGuid);

		return objectGuid;
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
