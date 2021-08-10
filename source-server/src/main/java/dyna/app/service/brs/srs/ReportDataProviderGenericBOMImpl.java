/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReportDataProviderGenericBOMImpl
 * Wanglei 2012-2-9
 */
package dyna.app.service.brs.srs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ReplaceRangeEnum;
import dyna.common.systemenum.ReplaceStatusEnum;
import dyna.common.systemenum.ReplaceTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public class ReportDataProviderGenericBOMImpl implements ReportDataProvider<BOMStructure>
{
	private Class<BOMStructure>	resultClass			= BOMStructure.class;
	private ObjectGuid			bomViewObjectGuid	= null;
	private int					level				= 1;
	private SearchCondition		bomSearchCondition	= null;
	private LanguageEnum		lang				= null;
	private String				exportType			= null;
	private String				levelStyle			= null;
	private String				isExportAllLevel	= null;
	private List<String>		summaryFiledName	= null;
	private List<String>		classGuids			= null;
	private boolean				hasSequence			= false;
	private GenericReportParams	params				= null;
	private Boolean				isContainRepf		= null;
	private DataRule			dataRule			= null;
	private boolean				refreshData			= true;
	private List<BOMStructure>	dataList			= new ArrayList<BOMStructure>();

	@SuppressWarnings("unchecked")
	public ReportDataProviderGenericBOMImpl(ObjectGuid bomViewObjectGuid, DataRule dataRule, SearchCondition bomSearchCondition, GenericReportParams params)
	{
		Map<String, Object> otherParams = params.getOtherParams();

		this.bomViewObjectGuid = bomViewObjectGuid;
		this.level = (Integer) otherParams.get("level");
		this.bomSearchCondition = bomSearchCondition;
		this.exportType = (String) otherParams.get("exportType");
		this.levelStyle = (String) otherParams.get("levelStyle");
		this.isExportAllLevel = (String) otherParams.get("isExportAllLevel");
		this.summaryFiledName = (List<String>) otherParams.get("summaryFiledName");
		this.classGuids = (List<String>) otherParams.get("classGuids");
		this.isContainRepf = BooleanUtils.getBooleanByYN((String) otherParams.get("isContainRepf"));
		this.params = params;
		this.lang = params.getLang();
		this.dataRule = dataRule;

		hasSequence = true;

		// for (DetailColumnInfo column : this.columnInfo)
		// {
		// if (column.getPropertyName().equals("STRUCTURE.LAYER"))
		// {
		// flagA = false;
		// }
		// if (column.getPropertyName().equals("STRUCTURE.DESIGNATORS"))
		// {
		// flagB = false;
		// }
		// if (column.getPropertyName().equals("NUMBER"))
		// {
		// hasSequence = true;
		// }
		// }
	}

	private void getRecursionEnd2(List<BOMStructure> listFinalBOMStructure, Map<String, List<BOMStructure>> bomMap, BOMStructure bomStructure, String codePath, String constr,
			int i, String flag)
	{
		String[] temp = null;
		if (codePath != null && !"".equals(codePath))
		{
			// 导出bomtree结构如下：10,10.10,10.10.20...
			if (this.levelStyle.equals("1"))
			{
				temp = bomStructure.getSequence().replace(".", "-").split("-");
				int size = temp.length;
				codePath = codePath + "." + temp[size - 1];
			}
			// 导出bomtree结构如下：.10,..10,...20 ...
			else if (this.levelStyle.equals("2"))
			{
				constr = constr + ".";

				temp = bomStructure.getSequence().replace(".", "-").split("-");
				int size = temp.length;
				codePath = constr + temp[size - 1];
			}
			else
			{
				codePath = bomStructure.getSequence();
			}
		}
		else
		{
			if (this.levelStyle.equals("2"))
			{
				codePath = constr + bomStructure.getSequence();
			}
			else
			{
				codePath = bomStructure.getSequence();
			}

		}
		if (listFinalBOMStructure.contains(bomStructure))
		{
			BOMStructure structure = new BOMStructure();
			structure.putAll(bomStructure);
			structure.setSequence(codePath);
			if (flag.equals("1"))
			{
				structure.put("flag", "1");
			}
			listFinalBOMStructure.add(structure);
		}
		else
		{
			listFinalBOMStructure.add(bomStructure);
			if (flag.equals("1"))
			{
				bomStructure.put("flag", "1");
			}
			bomStructure.setSequence(codePath);
		}
		if (i + 2 > this.level && this.level != -1)
		{
			return;
		}
		String end1Guid = bomStructure.getEnd2ObjectGuid().getGuid();
		List<BOMStructure> bomlist = bomMap.get(end1Guid);
		if (!SetUtils.isNullList(bomlist))
		{
			for (BOMStructure tmpBomStructure : bomlist)
			{
				if (end1Guid.equals(tmpBomStructure.getEnd1ObjectGuid().getGuid()))
				{
					this.getRecursionEnd2(listFinalBOMStructure, bomMap, tmpBomStructure, codePath, constr, i + 1, "-1");
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#getDataList()
	 */
	@Override
	public List<BOMStructure> getDataList()
	{
		if (refreshData)
		{
			List<BOMStructure> listFinalBOMStructure = new ArrayList<BOMStructure>();
			List<BOMStructure> finalList = new ArrayList<BOMStructure>();
			try
			{
				// 导出bomtree类型的报表数据
				if (!StringUtils.isNullString(exportType))
				{
					BOMView bomViewObject = this.params.getBOMS().getBOMView(this.bomViewObjectGuid);
					if (bomViewObject == null)
					{
						return null;
					}
					BOMTemplateInfo bomTemplate = this.params.getEMM().getBOMTemplateById((String) bomViewObject.get(ViewObject.TEMPLATE_ID));
					if (bomTemplate == null)
					{
						return null;
					}


					if (exportType.equals("bomtree"))
					{
						Map<String, List<BOMStructure>> bomMap = this.params.getBOMS().listBOMForTree(this.bomViewObjectGuid, this.level, this.bomSearchCondition, null, this.dataRule);
						List<BOMStructure> listBOMStructure=null;
						if (SetUtils.isNullMap(bomMap))
						{
							listBOMStructure = new ArrayList<BOMStructure>();
							listBOMStructure.add(new BOMStructure());
							return listBOMStructure;
						}
						listBOMStructure = bomMap.get(bomViewObject.getEnd1ObjectGuid().getGuid());
						if (!SetUtils.isNullList(listBOMStructure))
						{
							for (BOMStructure tmpBomStructure : listBOMStructure)
							{
								this.getRecursionEnd2(listFinalBOMStructure, bomMap, tmpBomStructure, null, ".", 0, "-1");
							}
						}

						List<BOMStructure> list = returnFinalBOMStructureList(listFinalBOMStructure);
						finalList.addAll(list);
					}
					else if (exportType.equals("bomlist") || isExportAllLevel.equals("1"))
					{
						List<BOMStructure> bomstrList = this.params.getBOMS().listBOM(this.bomViewObjectGuid, this.bomSearchCondition, null, null);
						List<UIField> listUIField = new ArrayList<UIField>();
						try
						{
							BOMView bomview = this.params.getBOMS().getBOMView(this.bomViewObjectGuid);
							List<BOMView> bomviewList = new ArrayList<BOMView>();
							bomviewList.add(bomview);
							List<FoundationObject> foList = this.params.getBOMS().batchListEnd1OfBOMView(bomviewList);
							List<BOMTemplateInfo> bomTempList = this.params.getEMM().listBOMTemplateByEND1(foList.get(0).getObjectGuid());
							String strClassName = null;
							if (!SetUtils.isNullList(bomTempList))
							{
								strClassName = bomTempList.get(0).getStructureClassName();
								listUIField = this.params.getEMM().listListUIField(strClassName);
							}

						}
						catch (ServiceRequestException e)
						{
							e.printStackTrace();
						}

						List<BOMStructure> lists = new ArrayList<BOMStructure>();
						if (!SetUtils.isNullList(listUIField) && !SetUtils.isNullList(bomstrList))
						{
							for (BOMStructure bomStructure : bomstrList)
							{
								for (UIField uiField : listUIField)
								{
									if (!uiField.getName().equalsIgnoreCase(BOMStructure.QUANTITY) && !uiField.getName().equalsIgnoreCase(BOMStructure.UOM))
									{
										if (uiField.getType() == FieldTypeEnum.CODE || uiField.getType() == FieldTypeEnum.CODEREF || uiField.getType() == FieldTypeEnum.MULTICODE)
										{
											bomStructure.put(uiField.getName() + "$NAME", null);
											bomStructure.put(uiField.getName() + "$TITLE", null);
										}
										bomStructure.put(uiField.getName(), null);
									}
								}
								lists.add(bomStructure);
							}
						}
						List<BOMStructure> list = returnFinalBOMStructureList(lists);
						finalList.addAll(list);
					}
					// 按照分组导报表数据
					else if (exportType.equals("group"))
					{
						this.levelStyle = "0";
						Map<String, List<BOMStructure>> bomMap = this.params.getBOMS().listBOMForTree(this.bomViewObjectGuid, this.level, this.bomSearchCondition, null,
								this.dataRule);
						List<BOMStructure> listBOMStructure = null;
						if (SetUtils.isNullMap(bomMap))
						{
							listBOMStructure = new ArrayList<BOMStructure>();
							listBOMStructure.add(new BOMStructure());
							return listBOMStructure;
						}
						listBOMStructure = bomMap.get(bomViewObject.getEnd1ObjectGuid().getGuid());
						if (!SetUtils.isNullList(listBOMStructure))
						{
							for (BOMStructure tmpBomStructure : listBOMStructure)
							{
								this.getRecursionEnd2(listFinalBOMStructure, bomMap, tmpBomStructure, null, ".", 0, "-1");
							}
						}

						List<BOMStructure> list = returnFinalBOMStructureList(listFinalBOMStructure);
						finalList.addAll(list);
					}
				}
				else if ("1".equals(isExportAllLevel))
				{
					List<BOMStructure> list = returnFinalBOMStructureList(this.params.getBOMS().listBOM(this.bomViewObjectGuid, this.bomSearchCondition, null, null));
					finalList.addAll(list);
				}
				else
				{
					this.levelStyle = "0";
					this.level = -1;
					List<BOMStructure> list = returnFinalBOMStructureList(this.params.getBOMS().listBOMForList(this.bomViewObjectGuid, this.bomSearchCondition, null, null));
					finalList.addAll(list);
				}
				if (isContainRepf == null || isContainRepf == false)
				{
					return finalList;
				}

				dataList.clear();
				GenericReportUtil reportUtil = new GenericReportUtil(this.params);
				for (BOMStructure bomStructure : finalList)
				{
					dataList.add(bomStructure);

					List<FoundationObject> replaceDataList = listReplaceData(bomStructure);
					if (replaceDataList != null && replaceDataList.size() > 0)
					{
						bomStructure.put("ReplaceRT", "*");

						for (FoundationObject fo : replaceDataList)
						{
							String rsType = (String) fo.get(ReplaceSubstituteConstants.RSType);
							String rang = (String) fo.get(ReplaceSubstituteConstants.Scope);
							String replaceRTString = this.getReplaceRTString(rang, rsType);
							if (StringUtils.isNullString(replaceRTString))
							{
								continue;
							}

							// 未激活和已失效的取替代关系不导出
							if (ReplaceSubstituteConstants.getReplaceDataStatus(fo) == ReplaceStatusEnum.UNACTIVATED
									|| ReplaceSubstituteConstants.getReplaceDataStatus(fo) == ReplaceStatusEnum.EXPIRE)
							{
								continue;
							}

							BOMStructure tempStructure = new BOMStructure();
							tempStructure.put("ReplaceRT", replaceRTString);

							String rsItemGuid = (String) fo.get(ReplaceSubstituteConstants.RSItem);
							String rsItemClassGuid = (String) fo.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.ClassGuid);
							FoundationObject rsItem = this.params.getBOAS().getObjectByGuid(new ObjectGuid(rsItemClassGuid, null, rsItemGuid, null));
							rsItem = this.params.getBOAS().getObject(rsItem.getObjectGuid());

							for (String fieldName : getEnd2FieldNameList())
							{

								tempStructure.put("End2." + fieldName, reportUtil.getFieldValue((FoundationObjectImpl) rsItem, fieldName, null));
							}
							dataList.add(tempStructure);
						}
					}
				}
			}
			catch (Exception e)
			{
				DynaLogger.error(e,e);
			}
		}
		return dataList;
	}

	private String getReplaceRTString(String rangGuid, String rsTypeGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(rsTypeGuid))
		{
			return "";
		}

		CodeItemInfo rsTypeItem = this.params.getEMM().getCodeItem(rsTypeGuid);
		if (rsTypeItem == null)
		{
			return "";
		}

		ReplaceTypeEnum type = ReplaceTypeEnum.typeValueOf(rsTypeItem.getName());

		if (!StringUtils.isGuid(rangGuid))
		{
			return "";
		}

		CodeItemInfo rangItem = this.params.getEMM().getCodeItem(rangGuid);
		if (rangItem == null)
		{
			return "";
		}

		ReplaceRangeEnum rang = ReplaceRangeEnum.typeValueOf(rangItem.getName());

		String messageId = null;
		if (rang == ReplaceRangeEnum.GLOBAL && type == ReplaceTypeEnum.TIDAI)
		{
			messageId = "ID_BOM_REPORT_GLOBAL_TIDAI";
		}
		if (rang == ReplaceRangeEnum.PART && type == ReplaceTypeEnum.QUDAI)
		{
			messageId = "ID_BOM_REPORT_PART_QUDAI";
		}
		if (rang == ReplaceRangeEnum.PART && type == ReplaceTypeEnum.TIDAI)
		{
			messageId = "ID_BOM_REPORT_PART_TIDAI";
		}
		return this.params.getMSRM().getMSRString(messageId, this.lang.toString());
	}

	private List<FoundationObject> listReplaceData(BOMStructure bomStructure) throws ServiceRequestException
	{
		BOMView bomView = this.params.getBOMS().getBOMView(this.bomViewObjectGuid);
		// String end1Guid = (String) bomStructure.get("END1.GUID$");
		// String end2Guid = (String) bomStructure.get("END2.GUID$");
		FoundationObject end1Object = this.params.getBOAS().getObjectByGuid(bomStructure.getEnd1ObjectGuid());
		FoundationObject end2Object = this.params.getBOAS().getObjectByGuid(bomStructure.getEnd2ObjectGuid());
		List<FoundationObject> partReplaceDataList = this.params.getBRM().listReplaceDataByRang(end1Object.getObjectGuid(), end2Object.getObjectGuid(), ReplaceRangeEnum.PART,
				null, bomView.getName(), bomStructure.getBOMKey(), true);
		List<FoundationObject> globalReplaceDataList = this.params.getBRM()
				.listReplaceDataByRang(null, end2Object.getObjectGuid(), ReplaceRangeEnum.GLOBAL, null, null, null, true);

		List<FoundationObject> list = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(partReplaceDataList))
		{
			list.addAll(partReplaceDataList);
		}
		if (!SetUtils.isNullList(globalReplaceDataList))
		{
			list.addAll(globalReplaceDataList);
		}

		return list;
	}

	private List<String> getEnd2FieldNameList()
	{
		List<String> list = new ArrayList<String>();
		List<DetailColumnInfo> detailColumnList = this.params.getDetailColumnList();
		if (detailColumnList != null)
		{
			for (DetailColumnInfo detailColumn : detailColumnList)
			{
				String fieldName = detailColumn.getPropertyName();
				if (fieldName.startsWith("END2."))
				{
					String realFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
					list.add(realFieldName);
				}
			}
		}

		return list;
	}


	@SuppressWarnings("rawtypes")
	private List<BOMStructure> returnFinalBOMStructureList(List<BOMStructure> listBOMStructure) throws ServiceRequestException
	{
		if (null == listBOMStructure)
		{
			listBOMStructure = new ArrayList<BOMStructure>();
			listBOMStructure.add(new BOMStructure());
			return listBOMStructure;
		}

		// ---------------
		List<BOMStructure> finalBOMStructureList = new ArrayList<BOMStructure>();
		List<String> numbers = new ArrayList<String>();
		List<String> noExportList = new ArrayList<String>();
		GenericReportUtil reportUtil = new GenericReportUtil(this.params);

		for (BOMStructure structure : listBOMStructure)
		{
			BOMStructure finalstructure = new BOMStructure();
			FoundationObjectImpl end1 = null;
			FoundationObjectImpl end2 = null;

			if (null == structure.getEnd1ObjectGuid() || null == structure.getEnd1ObjectGuid().getGuid())
			{
				end1 = null;
			}
			else
			{
				end1 = (FoundationObjectImpl) this.params.getBOAS().getObject(structure.getEnd1ObjectGuid());
				finalstructure.setEnd1ObjectGuid(structure.getEnd1ObjectGuid());
			}
			if (null == structure.getEnd2ObjectGuid() || null == structure.getEnd2ObjectGuid().getGuid())
			{
				end2 = null;
			}
			else
			{
				end2 = (FoundationObjectImpl) this.params.getBOAS().getObject(structure.getEnd2ObjectGuid());
				finalstructure.setEnd2ObjectGuid(structure.getEnd2ObjectGuid());
			}

			if (null != this.classGuids)
			{
				if (!StringUtils.isNullString(exportType) && exportType.equals("bomtree"))
				{
					/*
					 * if (null != end2 && this.classGuids.contains(end2.get("BOGUID$").toString()))
					 * {
					 */
					if (null != structure.get("flag") && structure.get("flag").toString().equals("1"))
					{
						numbers.add(structure.getSequence());
					}
					else
					{
						if (null != end1 && this.classGuids.contains(end1.get("BOGUID$").toString()))
						{
							if (end2 != null)
							{
								noExportList.add(end2.getGuid());
							}
							continue;
						}
						else if (end1 != null && noExportList.contains(end1.getGuid()))
						{
							if (end2 != null)
							{
								noExportList.add(end2.getGuid());
							}
							continue;
						}
						else
						{
							numbers.add(structure.getSequence());
						}
					}
					// }
				}
				else if (null != end2 && this.classGuids.contains(end2.get("BOGUID$").toString()))
				{
					numbers.add(structure.getSequence());
					continue;
				}
			}

			Iterator structureIter = structure.entrySet().iterator();
			while (structureIter.hasNext())
			{
				Map.Entry entry = (Map.Entry) structureIter.next();
				if (entry.getKey().equals("QUANTITY"))
				{
					BigDecimal quantity = new BigDecimal(entry.getValue().toString());
					String value = entry.getValue() == null ? "1.00" : quantity.toString();
					finalstructure.put("Structure." + entry.getKey(), Double.parseDouble(value));
				}
				else if (entry.getKey().toString().contains("TITLE"))
				{
					if (null == entry.getValue())
					{
						finalstructure.put("Structure." + entry.getKey(), "--");
					}
					else
					{
						finalstructure.put("Structure." + entry.getKey(), StringUtils.getMsrTitle((String) entry.getValue(), lang.getType()));
					}
				}
				else
				{
					finalstructure.put("Structure." + entry.getKey(), entry.getValue());
				}
			}

			List<DetailColumnInfo> detailColumnsList = params.getDetailColumnList();
			String className = structure.getObjectGuid().getClassName();
			for (DetailColumnInfo fieldInfo : detailColumnsList)
			{
				String column = fieldInfo.getPropertyName();
				if (column.toUpperCase().startsWith("STRUCTURE."))
				{
					String realColumn = (column.substring(10, column.length())).replace("#", "$");
					finalstructure.put(column, reportUtil.getFieldValue(structure, className, realColumn, fieldInfo.getPropertiesMap()));
				}
			}

			if (null != end1)
			{
				Iterator end1Iter = end1.entrySet().iterator();
				while (end1Iter.hasNext())
				{
					Map.Entry entry = (Map.Entry) end1Iter.next();
					if (entry.getKey().toString().contains("TITLE"))
					{
						if (null == entry.getValue())
						{
							finalstructure.put("End1." + entry.getKey(), "--");
						}
						else
						{
							finalstructure.put("End1." + entry.getKey(), StringUtils.getMsrTitle((String) entry.getValue(), lang.getType()));
						}
					}
					else
					{
						finalstructure.put("End1." + entry.getKey(), entry.getValue());
					}
				}

				for (DetailColumnInfo fieldInfo : detailColumnsList)
				{
					String column = fieldInfo.getPropertyName();
					if (column.toUpperCase().startsWith("END1."))
					{
						String realColumn = (column.substring(5, column.length())).replace("#", "$");
						finalstructure.put(column, reportUtil.getFieldValue(end1, realColumn, fieldInfo.getPropertiesMap()));
					}
				}
			}

			if (null != end2)
			{
				Iterator end2Iter = end2.entrySet().iterator();
				String uomName = "";
				String uomTitle = "";
				while (end2Iter.hasNext())
				{
					Map.Entry entry = (Map.Entry) end2Iter.next();
					if (entry.getKey().equals("BOTITLE$") || entry.getKey().equals("CLASSIFICATION$TITLE") || entry.getKey().toString().contains("TITLE"))
					{
						if (null == entry.getValue())
						{
							finalstructure.put("End2." + entry.getKey(), "--");
						}
						else
						{
							finalstructure.put("End2." + entry.getKey(), StringUtils.getMsrTitle((String) entry.getValue(), lang.getType()));
						}
					}
					else if (entry.getKey().equals("UOM$NAME"))
					{
						uomName = (null == entry.getValue()) ? "" : "[" + entry.getValue().toString() + "]";
					}
					else if (entry.getKey().equals("UOM$TITLE"))
					{
						uomTitle = (null == entry.getValue()) ? "" : StringUtils.getMsrTitle((String) entry.getValue(), lang.getType());
					}
					else if (entry.getKey().toString().contains("TITLE"))
					{
						if (null == entry.getValue())
						{
							finalstructure.put("End2." + entry.getKey(), "--");
						}
						else
						{
							finalstructure.put("End2." + entry.getKey(), StringUtils.getMsrTitle((String) entry.getValue(), lang.getType()));
						}
					}
					else
					{
						finalstructure.put("End2." + entry.getKey(), entry.getValue());
						if (entry.getKey().equals("REVISIONID$"))
						{
							if (null == finalstructure.get("STRUCTURE.REVISIONID$"))
							{
								finalstructure.put("STRUCTURE.REVISIONID$", entry.getValue());
							}
						}

					}
					for (String sf : summaryFiledName)
					{
						if (sf.replace(".", ",").split(",").length == 2)
						{
							sf = sf.replace(".", ",").split(",")[1].replace("#", "$");
						}
						else
						{
							sf = sf.replace("#", "$");
						}

						if (null == end2.get(sf))
						{
							finalstructure.put("End2." + sf, "--");
						}
					}
				}

				for (DetailColumnInfo fieldInfo : detailColumnsList)
				{
					String column = fieldInfo.getPropertyName();
					if (column.toUpperCase().startsWith("END2."))
					{
						String realColumn = (column.substring(5, column.length())).replace("#", "$");
						finalstructure.put(column, reportUtil.getFieldValue(end2, realColumn, fieldInfo.getPropertiesMap()));
					}
				}

				finalstructure.put("End2.UOM", uomTitle + uomName);
				finalstructure.put("NUMBER", finalstructure.get("STRUCTURE.SEQUENCE"));
			}

			finalBOMStructureList.add(finalstructure);
		}

		if (!StringUtils.isNullString(exportType) && exportType.equals("bomtree") && this.classGuids.size() > 0)
		{
			List<BOMStructure> tempList = new ArrayList<BOMStructure>();
			for (String number : numbers)
			{
				for (BOMStructure s : finalBOMStructureList)
				{
					if ((s.get("STRUCTURE.SEQUENCE").toString().equals(number) || number.startsWith(s.get("STRUCTURE.SEQUENCE").toString())) && !tempList.contains(s))
					{
						tempList.add(s);
					}
				}
			}
			finalBOMStructureList = tempList;
		}

		if (!StringUtils.isNullString(exportType) && (exportType.equals("bomlist") || exportType.equals("group")) || isExportAllLevel.equals("1")
				|| !StringUtils.isNullString(isExportAllLevel) || !SetUtils.isNullList(summaryFiledName))
		{
			List<BOMStructure> tempList = new ArrayList<BOMStructure>();
			if (!StringUtils.isNullString(exportType) && (exportType.equals("bomtree")))
			{
				tempList = finalBOMStructureList;
			}
			else
			{
				for (BOMStructure s : finalBOMStructureList)
				{
					if (!this.classGuids.contains(s.get("END2.BOGUID$").toString()))
					{
						tempList.add(s);
					}
				}
			}
			finalBOMStructureList = tempList;
		}

		List<BOMStructure> templist = new ArrayList<BOMStructure>();
		templist.addAll(finalBOMStructureList);
//		if (bomMap != null)
//		{
//			if (!StringUtils.isNullString(isExportAllLevel) && isExportAllLevel.equals("1"))
//			{
//				for (BOMStructure s : finalBOMStructureList)
//				{
//					List<BOMStructure> list = bomMap.get(s.get("STRUCTURE.END2$"));
//					if (!SetUtils.isNullList(list))
//					{
//						templist.remove(s);
//					}
//				}
//			}
//		}

		finalBOMStructureList = templist;
		int i = 1;
		if (!StringUtils.isNullString(exportType) && !exportType.equals("bomtree") || StringUtils.isNullString(exportType))
		{
			for (BOMStructure finalstructure : finalBOMStructureList)
			{
				finalstructure.put("NUMBER", i);
				finalstructure.put("STRUCTURE.SEQUENCE", i);
				i++;
			}
		}

		if (finalBOMStructureList.size() == 0)
		{
			finalBOMStructureList.add(new BOMStructure());
		}
		return finalBOMStructureList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.report.ReportDataProvider#setMap(java.util.Map)
	 */
	@Override
	public Map<String, Object> getHeaderParameter()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			BOMView bomView = this.params.getBOMS().getBOMView(bomViewObjectGuid);
			FoundationObject headObject = this.params.getBOAS().getObject(bomView.getEnd1ObjectGuid());

			GenericReportUtil reportUtil = new GenericReportUtil(this.params);
			for (ParameterColumnInfo fieldInfo : this.params.getHeaderColumnList())
			{
				String name = fieldInfo.getParameterName();
				if (name.equals("pageSize"))
				{
					continue;
				}
				map.put(name, reportUtil.getFieldValue((FoundationObjectImpl) headObject, name.replace("#", "$"), fieldInfo.getPropertiesMap()));
			}
			map.put("id", headObject.getId());
			map.put("name", headObject.getName() == null ? "" : headObject.getName());
			map.put("rev", headObject.getRevisionId());
			map.put("type", StringUtils.getMsrTitle(headObject.getClassification(), lang.getType()));
			map.put("specification", headObject.get("Specification"));
			map.put("bo", StringUtils.getMsrTitle((String) headObject.get("BOTITLE$"), lang.getType()));
			map.put("exportType", exportType);

		}
		catch (ServiceRequestException e)
		{
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
	public void setDataList(List<BOMStructure> dataList)
	{
		this.dataList = dataList;
		this.refreshData = false;
	}

	@Override
	public Class<BOMStructure> getResultClass()
	{
		return this.resultClass;
	}

}
