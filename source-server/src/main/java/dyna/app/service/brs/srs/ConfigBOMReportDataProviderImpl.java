/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoundationObjectReportDataProviderImpl
 * cuilei 2012-3-22
 */
package dyna.app.service.brs.srs;

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
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.StructureObjectImpl;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author cuilei
 * 
 */
public class ConfigBOMReportDataProviderImpl implements ReportDataProvider<StructureObject>
{
	private Class<StructureObject>	resultClass			= StructureObject.class;
	private FoundationObject		foundationObject	= null;
	private FoundationObject		draw				= null;
	private GenericReportParams		params				= null;
	private SearchCondition			sc					= null;
	private int						level				= -1;
	private boolean					refreshData			= true;
	private List<StructureObject>	dataList			= new ArrayList<StructureObject>();

	public ConfigBOMReportDataProviderImpl(FoundationObject foundationObject, FoundationObject draw, SearchCondition sc, GenericReportParams params)
	{
		this.foundationObject = foundationObject;
		this.draw = draw;
		this.params = params;
		this.sc = sc;

		String level_ = (String) this.params.getOtherParams().get("level");
		this.level = level_ == null ? -1 : Integer.valueOf(level_);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#getDataList()
	 */
	@Override
	public List<StructureObject> getDataList() throws ServiceRequestException
	{
		if (refreshData)
		{
			List<StructureObject> bomList = new ArrayList<StructureObject>();

			ClassInfo classInfo = this.params.getEMM().getClassByGuid(this.foundationObject.getObjectGuid().getClassGuid());
			if (classInfo.hasInterface(ModelInterfaceEnum.IOrderContract))
			{
				ViewObject viewObject = this.params.getBOAS().getRelationByEND1(this.foundationObject.getObjectGuid(),
						ConfigParameterConstants.CONFIG_PARAMETER_RESULTRELATION_TEMPLATE_NAME);
				if (viewObject == null)
				{
					return null;
				}

				List<StructureObject> tmpList = this.params.getBOAS().listObjectOfRelation(viewObject.getObjectGuid(), null, this.sc, null);
				if (!SetUtils.isNullList(tmpList))
				{
					int sequence = 1;
					for (StructureObject structureObject : tmpList)
					{
						structureObject.put("DATASEQ", String.valueOf(sequence++));
						bomList.add(structureObject);

						FoundationObject item = structureObject.getEnd2UIObject();
						FoundationObject draw = this.params.getCPB().getDrawInstanceByItem(item, viewObject.getCreateTime());
						List<StructureObject> strucList_ = this.getStructureList(item, draw, structureObject.getSequence());
						if (!SetUtils.isNullList(strucList_))
						{
							bomList.addAll(strucList_);
						}
					}
				}
			}
			else if (this.draw != null)
			{
				bomList = this.getStructureList(this.foundationObject, this.draw, null);
			}

			dataList.clear();
			if (!SetUtils.isNullList(bomList))
			{
				GenericReportUtil util = new GenericReportUtil(this.params);
				for (StructureObject structureObject : bomList)
				{
					Map<String, Object> strucMap = new HashMap<String, Object>();
					strucMap.putAll((StructureObjectImpl) structureObject);

					StructureObject struc = new StructureObjectImpl();

					FoundationObject end2 = structureObject.getEnd2UIObject();
					FoundationObject end1 = (FoundationObject) structureObject.get("END1_UI_OBJECT");
					if (!SetUtils.isNullList(this.params.getDetailColumnList()))
					{
						for (DetailColumnInfo columnInfo : this.params.getDetailColumnList())
						{
							String fieldName = columnInfo.getPropertyName().replace("#", "$");
							if (fieldName.startsWith("END1$"))
							{
								if (!end1.getObjectGuid().hasAuth() && !columnInfo.getPropertyName().equalsIgnoreCase("END1$ID$")
										&& !columnInfo.getPropertyName().equalsIgnoreCase("END1$NAME$"))
								{
									struc.put(columnInfo.getPropertyName(), "-");
								}
								else
								{
									fieldName = columnInfo.getPropertyName().substring(5);
									struc.put(columnInfo.getPropertyName(), util.getFieldValue((FoundationObjectImpl) end1, fieldName, columnInfo.getPropertiesMap()));
								}
							}
							else if (fieldName.startsWith("STRUCTURE$"))
							{
								if (!structureObject.getObjectGuid().hasAuth())
								{
									struc.put(columnInfo.getPropertyName(), "-");
								}
								else
								{
									fieldName = columnInfo.getPropertyName().substring(10);
									struc.put(columnInfo.getPropertyName(),
											util.getFieldValue(strucMap, structureObject.getObjectGuid().getClassName(), fieldName, columnInfo.getPropertiesMap()));
								}
							}
							else
							{
								if (!end2.getObjectGuid().hasAuth())
								{
									struc.put(columnInfo.getPropertyName(), "-");
								}
								else
								{
									if (fieldName.startsWith("END2$"))
									{
										fieldName = columnInfo.getPropertyName().substring(5);
										struc.put(columnInfo.getPropertyName(), util.getFieldValue((FoundationObjectImpl) end2, fieldName, columnInfo.getPropertiesMap()));
									}
									else
									{
										struc.put(columnInfo.getPropertyName(), util.getFieldValue((FoundationObjectImpl) end2, fieldName, columnInfo.getPropertiesMap()));
									}
								}
							}
						}
					}

					dataList.add(struc);
				}
			}
		}
		return dataList;
	}

	private List<StructureObject> getStructureList(FoundationObject item, FoundationObject draw, String sequence) throws ServiceRequestException
	{
		if (draw == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "draw is not exist.");
		}

		DataRule dataRule = new DataRule();
		dataRule.setSystemStatus(draw.getStatus());
		if (!draw.isLatestRevision())
		{
			dataRule.setLocateTime((Date) draw.get(SystemClassFieldEnum.NEXTREVISIONRLSTIME.getName()));
		}

		List<StructureObject> bomList = new ArrayList<StructureObject>();

		List<StructureObject> structureList = this.params.getCPB().driveResult4Order(item, draw, dataRule, sc);
		if (!SetUtils.isNullList(structureList))
		{
			int tmpSequence = 0;
			for (StructureObject structureObject : structureList)
			{
				tmpSequence++;
				structureObject.put("DATASEQ", StringUtils.isNullString(sequence) ? String.valueOf(tmpSequence) : sequence + "." + tmpSequence);
				structureObject.put("END1_UI_OBJECT", item);
				bomList.add(structureObject);

				if (this.level >= 2 || this.level == -1)
				{
					this.listItemForConfig(structureObject.getEnd2UIObject(), dataRule, sc, bomList, (String) structureObject.get("DATASEQ"));
				}
			}
		}

		return bomList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#getHeaderParameter(java.util.Map)
	 */
	@Override
	public Map<String, Object> getHeaderParameter() throws ServiceRequestException
	{
		GenericReportUtil util = new GenericReportUtil(this.params);

		Map<String, Object> header = new HashMap<String, Object>();
		List<ParameterColumnInfo> headerColumnList = this.params.getHeaderColumnList();

		if (!SetUtils.isNullList(headerColumnList))
		{
			for (ParameterColumnInfo headerColumn : headerColumnList)
			{
				String fieldName = headerColumn.getParameterName().replace("#", "$");
				header.put(headerColumn.getParameterName(), util.getFieldValue((FoundationObjectImpl) this.foundationObject, fieldName, headerColumn.getPropertiesMap()));
			}
		}
		return header;
	}

	private void listItemForConfig(FoundationObject item, DataRule dataRule, SearchCondition itemSearchCondition, List<StructureObject> bomList, String parentSequence)
			throws ServiceRequestException
	{
		// 一个点表示两层，依次类推
		if (this.level != -1 && parentSequence.length() - (parentSequence.replace(".", "")).length() + 1 >= this.level)
		{
			return;
		}

		FoundationObject draw = this.params.getCPB().getDrawInstanceByItem(item, dataRule.getLocateTime());

		List<StructureObject> structureList = this.params.getCPB().driveResult4Order(item, draw, dataRule, itemSearchCondition);
		if (!SetUtils.isNullList(structureList))
		{
			int sequence = 0;
			for (StructureObject structureObject : structureList)
			{
				sequence++;
				structureObject.put("DATASEQ", parentSequence + "." + sequence);
				structureObject.put("END1_UI_OBJECT", item);
				bomList.add(structureObject);

				FoundationObject end2 = structureObject.getEnd2UIObject();
				this.listItemForConfig(end2, dataRule, itemSearchCondition, bomList, (String) structureObject.get("DATASEQ"));
			}
		}
	}

	@Override
	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter() throws ServiceRequestException
	{
		return null;
	}

	@Override
	public GenericReportParams getParams()
	{
		return params;
	}

	@Override
	public void setDataList(List<StructureObject> dataList)
	{
		this.dataList = dataList;
		this.refreshData = false;
	}

	@Override
	public Class<StructureObject> getResultClass()
	{
		return this.resultClass;
	}

}
