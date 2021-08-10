package dyna.app.service.brs.srs;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dyna.app.report.GenericReportParams;
import dyna.app.report.ParameterColumnInfo;
import dyna.app.report.ReportDataProvider;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.StructureObjectImpl;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.uecs.UECActionTypeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.common.util.UpdatedECSConstants;

public class ECReportProviderImpl implements ReportDataProvider<FoundationObject>
{
	private Class<FoundationObject>		resultClass				= FoundationObject.class;
	private ObjectGuid					ecnObjectGuid			= null;
	private List<FoundationObjectImpl>	ecoList					= null;
	private GenericReportParams			params					= null;
	private Map<String, List<String>>	subReportColumnMap		= null;
	private boolean						refreshData				= true;
	private List<FoundationObject>		dataList				= new ArrayList<FoundationObject>();

	// 非系统字段，但是含有$符号
	private static final String[]		SPECIAL_FIELD_NAME_LIST	= { "FULLNAME$" };

	// {模板对象名/系统bean属性名/当批量变更批量添加时，为了便于报表便于理解，变更对象设置为修改后，修改前设为空}
	private static final String[][]		SPECIAL_PARAM_NAME_LIST	= { { "ECBEFOREITEM", "CHANGEITEM", "TARGETITEM" }, { "ECAFTERITEM", "TARGETITEM", "CHANGEITEM" } };

	@SuppressWarnings("unchecked")
	public ECReportProviderImpl(ObjectGuid ecnObjectGuid, List<FoundationObjectImpl> ecoList, GenericReportParams params)
	{
		this.ecnObjectGuid = ecnObjectGuid;
		this.ecoList = ecoList;
		this.params = params;
		this.subReportColumnMap = (Map<String, List<String>>) this.params.getOtherParams().get("templateColumns");
	}

	@Override
	/**
	 * ECO和解决对象通过关系模板来关联
	 */
	public List<FoundationObject> getDataList() throws ServiceRequestException
	{
		if (refreshData)
		{
			this.dataList.clear();
			this.dataList = getECDataList();
		}
		return dataList;
	}

	@Override
	public Map<String, Object> getHeaderParameter() throws ServiceRequestException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		FoundationObjectImpl ecn = null;
		if (this.ecnObjectGuid != null)
		{
			ecn = (FoundationObjectImpl) this.params.getBOAS().getObject(this.ecnObjectGuid);
		}

		try
		{
			List<ParameterColumnInfo> parameters = this.params.getHeaderColumnList();
			for (ParameterColumnInfo fieldInfo : parameters)
			{
				String name = fieldInfo.getParameterName();
				String fieldName = name.replace("#", "$");
				if (fieldName.indexOf(".") != -1)
				{
					fieldName = fieldName.substring(fieldName.indexOf(".") + 1);
				}

				// 如果是ECN上的属性
				if (name.startsWith("ECN."))
				{
					map.put(name, this.getFieldVal(ecn, fieldName));
				}
				else if (name.equalsIgnoreCase("ECOLIST"))
				{
					List<FoundationObject> list = new ArrayList<FoundationObject>();
					List<String> fieldList = ((Map<String, List<String>>) this.params.getOtherParams().get("templateColumns")).get("ECOLIST");
					for (FoundationObjectImpl eco : this.ecoList)
					{
						FoundationObjectImpl eco_ = new FoundationObjectImpl();
						if (!SetUtils.isNullList(fieldList))
						{
							for (String field : fieldList)
							{
								eco_.put(field, this.getFieldVal(eco, field));
							}
						}
						list.add(eco_);
					}
					map.put(name, list);
				}
				else if (name.equalsIgnoreCase("ECILIST"))
				{
					List<FoundationObject> list = new ArrayList<FoundationObject>();
					for (FoundationObjectImpl eco : this.ecoList)
					{
						Boolean isBatchEC = false;
						isBatchEC = this.params.getEMM().getClassByGuid((String) eco.get("CLASSGUID$")).hasInterface(ModelInterfaceEnum.IBatchForEc);

						if (isBatchEC != null && !isBatchEC)
						{
							// 普通ECO
							List<FoundationObject> normalList = this.getNormalECDataList(eco);
							if (!SetUtils.isNullList(normalList))
							{
								list.addAll(normalList);
							}
						}
						else if (isBatchEC != null && isBatchEC)
						{
							// 批量ECO
							List<FoundationObject> batchList = this.getBatchECDataList(eco);
							if (!SetUtils.isNullList(batchList))
							{
								list.addAll(batchList);
							}
						}
					}
					map.put("ECILIST", list);
				}
				// 设置子报表模板路径
				else if (("REPORTTEMPLATEPATH").equals(name))
				{
					map.put(name, this.params.getOtherParams().get("REPORTTEMPLATEPATH"));
				}
				else
				{
					map.put(name, StringUtils.EMPTY_STRING);
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
			throw new ServiceRequestException("ECDataProviderImpl.getHeaderParameter() get parameter value error", "," + e.getMessage());
		}

		return map;
	}

	@Override
	public Map<String, Object> getWBSAndDeliverablesReportHeaderParameter()
	{
		return null;
	}

	/**
	 * 因为detail中只有子报表的原因,如果不添加一个空的detail数据,会导致子报表不显示
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> getECDataList() throws ServiceRequestException
	{
		List<FoundationObject> list = new ArrayList<FoundationObject>();

		FoundationObject foundationObject = new FoundationObjectImpl();

		list.add(foundationObject);

		return list;
	}

	private List<FoundationObject> getNormalECDataList(FoundationObjectImpl eco) throws ServiceRequestException
	{
		List<FoundationObject> list = new ArrayList<FoundationObject>();
		List<FoundationObject> relations = this.getECOItemRelation(eco);
		if (!SetUtils.isNullList(relations))
		{
			List<FoundationObjectImpl> propertyList = this.getPropertyChange(relations, eco);
			if (!SetUtils.isNullList(propertyList))
			{
				list.addAll(propertyList);
			}
			List<FoundationObjectImpl> bomList = this.getBOMChange(relations, eco);
			if (!SetUtils.isNullList(bomList))
			{
				list.addAll(bomList);
			}
			List<FoundationObjectImpl> relationList = this.getRelationChange(relations, eco);
			if (!SetUtils.isNullList(relationList))
			{
				list.addAll(relationList);
			}
			List<FoundationObjectImpl> otherList = this.getOthersChange(eco);
			if (!SetUtils.isNullList(otherList))
			{
				list.addAll(otherList);
			}
		}

		return list;
	}

	/**
	 * ECO和解决对象之前通过关系关联。
	 * 取得解决对象。
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObject> getECOItemRelation(FoundationObject eco) throws ServiceRequestException
	{
		// 解决对象的关系
		ViewObject resolvedObjectView = this.params.getBOAS().getRelationByEND1(eco.getObjectGuid(), UpdatedECSConstants.ECO_CHANGEITEMAFTER$);
		if (resolvedObjectView == null)
		{
			return new ArrayList<FoundationObject>();
		}

		// 取得所有的解决对象,普通变更只会有一个解决对象
		// 获取已知viewobject对象，取得对象上关联的所有结构，即解决对象
		List<FoundationObject> relationList = ((BOASImpl) this.params.getBOAS()).getRelationStub().listFoundationObjectOfRelation(resolvedObjectView.getObjectGuid(), null, null,
				null, false);

		return relationList;
	}

	/**
	 * 查询eci属性变更部分的信息
	 * 
	 * @param relationList
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObjectImpl> getPropertyChange(List<FoundationObject> relationList, FoundationObjectImpl eco) throws ServiceRequestException
	{
		if (relationList == null || relationList.size() == 0)
		{
			return new ArrayList<FoundationObjectImpl>();
		}

		ObjectGuid relationObjectGuid = relationList.get(0).getObjectGuid();

		// ECIEDITAFTER, ECICONTENT, ECIEDITBEFORE
		// 查询eci属性变更部分的信息
		List<FoundationObject> eciList = this.params.getUECS().getFormECIByECO(this.getChangedItem(eco), relationObjectGuid, eco.getObjectGuid());
		if (eciList == null)
		{
			return new ArrayList<FoundationObjectImpl>();
		}

		List<String> propertyReportColumns = this.subReportColumnMap.get("ECILIST");
		List<String> structureColumns = new ArrayList<String>();
		if (propertyReportColumns != null)
		{
			for (FoundationObject foun : eciList)
			{
				for (String column : propertyReportColumns)
				{
					if (column.startsWith("ECO."))
					{
						String fieldName = (column.replace("#", "$")).substring(4);
						String val = this.getFieldVal(eco, fieldName);
						foun.put(column, val);
					}
					else if (column.equalsIgnoreCase("ISBATCH"))
					{
						foun.put(column, false);
					}
					else if (column.startsWith("ECI."))
					{
						column = column.substring(4);
						// 若模板中的field在对象中不存在，说明该field有可能含有其他属性，如#TITLE，#NAME等，
						// 把该field添加到对象中，以便在后续处理中，取得其对应的具体属性。
						if (foun.get(column) == null)
						{
							foun.put(column, null);
						}
					}
					else
					{
						structureColumns.add(column);
					}
				}
			}
		}

		return this.addPrefixToListData(eciList, "ECI", structureColumns);
	}

	private List<FoundationObjectImpl> getBOMChange(List<FoundationObject> relationList, FoundationObjectImpl eco) throws ServiceRequestException
	{
		if (relationList == null || relationList.size() == 0)
		{
			return new ArrayList<FoundationObjectImpl>();
		}

		ObjectGuid relationObjectGuid = relationList.get(0).getObjectGuid();

		List<FoundationObject> bomChangedList = new ArrayList<FoundationObject>();
		List<BOMTemplateInfo> allBOMTemplates = this.params.getEMM().listBOMTemplateByEND1(this.getChangedItem(eco));
		if (allBOMTemplates != null)
		{
			for (BOMTemplateInfo bomTemplate : allBOMTemplates)
			{
				// 变更方式：ActionType， BOM对象map：UpdatedECSConstants.Value
				List<FoundationObject> bomECIList = this.params.getUECS().getBomECIByECO(this.getChangedItem(eco), relationObjectGuid, bomTemplate.getName(), eco.getObjectGuid());
				if (bomECIList != null)
				{
					for (FoundationObject bomECI : bomECIList)
					{
						bomECI.put("BOMORRelationTemplateGuid", bomTemplate.getGuid());
						bomChangedList.add(bomECI);
					}
				}
			}
		}

		// 数量为空时，给默认值为1
		for (FoundationObject fo : bomChangedList)
		{
			Object quantity = fo.get("QUANTITY");
			Object quantityOld = fo.get("QUANTITY_OLD");
			fo.put("QUANTITY", quantity == null || StringUtils.EMPTY_STRING.equals(quantity.toString()) ? "1.0" : quantity.toString());
			fo.put("QUANTITY_OLD", quantityOld == null || StringUtils.EMPTY_STRING.equals(quantityOld.toString()) ? "1.0" : quantityOld.toString());
		}

		// 把模板中的field添加到对象中
		List<String> bomReportColumns = this.subReportColumnMap.get("ECILIST");
		List<String> structureColumns = new ArrayList<String>();
		if (bomReportColumns != null)
		{
			for (FoundationObject foun : bomChangedList)
			{
				for (String column : bomReportColumns)
				{
					if (column.startsWith("ECO."))
					{
						String fieldName = (column.replace("#", "$")).substring(4);
						String val = this.getFieldVal(eco, fieldName);
						foun.put(column, val);
					}
					else if (column.equalsIgnoreCase("ISBATCH"))
					{
						foun.put(column, false);
					}
					else if (column.startsWith("ECI."))
					{
						column = column.substring(4);
						// 若模板中的field在对象中不存在，说明该field有可能含有其他属性，如#TITLE，#NAME等，
						// 把该field添加到对象中，以便在后续处理中，取得其对应的具体属性。
						if (foun.get(column) == null)
						{
							foun.put(column, null);
						}
					}
					else
					{
						structureColumns.add(column);
					}
				}
			}
		}

		return this.addPrefixToListData(bomChangedList, "ECI", structureColumns);
	}

	private List<FoundationObjectImpl> getRelationChange(List<FoundationObject> relationList, FoundationObjectImpl eco) throws ServiceRequestException
	{
		if (relationList == null || relationList.size() == 0)
		{
			return new ArrayList<FoundationObjectImpl>();
		}

		ObjectGuid relationObjectGuid = relationList.get(0).getObjectGuid();

		// 取得普通关联关系模板
		List<RelationTemplateInfo> normalRelationTemplates = this.params.getEMM().listRelationTemplate(this.getChangedItem(eco));
		// 取得内置关系模板
		List<RelationTemplateInfo> builtinTemplateList = this.params.getEMM().listRelationTemplate4Builtin(this.getChangedItem(eco));

		List<RelationTemplateInfo> allRelationTemplates = new ArrayList<RelationTemplateInfo>();
		if (normalRelationTemplates != null)
		{
			allRelationTemplates.addAll(normalRelationTemplates);
		}
		if (builtinTemplateList != null)
		{
			allRelationTemplates.addAll(builtinTemplateList);
		}

		List<FoundationObject> relationChangedList = new ArrayList<FoundationObject>();
		if (allRelationTemplates != null)
		{
			for (RelationTemplateInfo relationTemplate : allRelationTemplates)
			{
				// 变更方式：ActionType， BOM对象map：UpdatedECSConstants.Value
				List<FoundationObject> relationECIList = this.params.getUECS().getRelationECIByECO(this.getChangedItem(eco), relationObjectGuid, relationTemplate.getName(),
						eco.getObjectGuid());
				if (relationECIList != null && !relationECIList.isEmpty())
				{
					for (FoundationObject fo : relationECIList)
					{
						fo.put("BOMORRelationTemplateGuid", relationTemplate.getGuid());
						relationChangedList.add(fo);
					}
				}
			}
		}

		List<String> relationReportColumns = this.subReportColumnMap.get("ECILIST");
		List<String> structureColumns = new ArrayList<String>();

		if (relationReportColumns != null)
		{
			for (FoundationObject foun : relationChangedList)
			{
				for (String column : relationReportColumns)
				{
					if (column.startsWith("ECO."))
					{
						String fieldName = (column.replace("#", "$")).substring(4);
						String val = this.getFieldVal(eco, fieldName);
						foun.put(column, val);
					}
					else if (column.equalsIgnoreCase("ISBATCH"))
					{
						foun.put(column, false);
					}
					else if (column.startsWith("ECI."))
					{
						column = column.substring(4);
						// 若模板中的field在对象中不存在，说明该field有可能含有其他属性，如#TITLE，#NAME等，
						// 把该field添加到对象中，以便在后续处理中，取得其对应的具体属性。
						if (foun.get(column) == null)
						{
							foun.put(column, null);
						}
					}
					else if (!structureColumns.contains(column))
					{
						structureColumns.add(column);
					}
				}
			}
		}

		return this.addPrefixToListData(relationChangedList, "ECI", structureColumns);
	}

	private List<FoundationObjectImpl> getOthersChange(FoundationObjectImpl eco) throws ServiceRequestException
	{
		// 其他变更 变更对象：ECISUBJECT、变更内容：ECICONTENT、变更前：ECIEDITBEFORE、变更后：ECIEDITAFTER、文件：FILEGUID
		List<FoundationObject> contentChangedList = this.params.getUECS().getContentECIByECO(eco.getObjectGuid());
		List<FoundationObjectImpl> list = this.addPrefixToListData(contentChangedList, "ECI", null);
		List<String> otherReportColumns = this.subReportColumnMap.get("ECILIST");
		for (FoundationObject fou : list)
		{
			for (String column : otherReportColumns)
			{
				if (column.startsWith("ECO"))
				{
					String fieldName = (column.replace("#", "$")).substring(4);
					fou.put(column, this.getFieldVal(eco, fieldName));
				}
			}
		}
		return list;
	}

	/**
	 * 给list的所有属性，添加前缀
	 * 
	 * @param origList
	 * @param prefix
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<FoundationObjectImpl> addPrefixToListData(List<FoundationObject> origList, String prefix, List<String> structureColumns) throws ServiceRequestException
	{
		List<FoundationObjectImpl> destList = new ArrayList<FoundationObjectImpl>();
		if (origList != null)
		{
			for (FoundationObject o : origList)
			{
				FoundationObjectImpl obj = (FoundationObjectImpl) o;
				Set<Entry<String, Object>> set = obj.entrySet();
				Iterator<Entry<String, Object>> it = set.iterator();
				FoundationObjectImpl foundObj = new FoundationObjectImpl();
				while (it.hasNext())
				{
					Entry<String, Object> entry = it.next();
					if (entry.getKey().startsWith("ECO."))
					{
						foundObj.put(entry.getKey(), this.getFieldVal((FoundationObjectImpl) o, entry.getKey()));
					}
					else
					{
						if ("VALUE".equals(entry.getKey()))
						{
							// 在value属性上，保存了所有结构信息，因此需要把结构上的信息取出，保存在实例对象上。
							foundObj.putAll(this.buildStructure((FoundationObjectImpl) o, entry.getKey(), structureColumns));
						}
						else
						{
							foundObj.put(prefix + "." + entry.getKey(), this.getFieldVal((FoundationObjectImpl) o, entry.getKey()));
						}
					}
				}
				destList.add(foundObj);
			}
		}

		return destList;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> buildStructure(FoundationObjectImpl foundationObject, String key, List<String> structureColumns) throws ServiceRequestException
	{
		Map<String, Object> map = new HashMap<String, Object>();

		if (foundationObject.get(key) == null)
		{
			return map;
		}

		Map<String, Object> structureMap = new HashMap<String, Object>();
		if (foundationObject.get(key) instanceof BOMStructure)
		{
			structureMap = (BOMStructure) foundationObject.get(key);
		}
		else if (foundationObject.get(key) instanceof StructureObjectImpl)
		{
			structureMap = (StructureObjectImpl) foundationObject.get(key);
		}
		else if (foundationObject.get(key) instanceof Map)
		{
			structureMap = (Map<String, Object>) foundationObject.get(key);
		}

		if (structureMap == null)
		{
			return map;
		}

		String templateGuid = (String) foundationObject.get("BOMORRelationTemplateGuid");
		ClassInfo classInfo = this.getClassInfoByTemplate(templateGuid);

		for (String property : structureColumns)
		{
			if (classInfo != null)
			{
				String val = this.getFieldVal(structureMap, classInfo.getGuid(), property);
				map.put(property, val);
			}
			else
			{
				map.put(property, structureMap.get(property));
			}
		}

		String actionType = this.getFieldVal(foundationObject, "ACTIONTYPE$NAME");
		this.buildEND1END2Object(actionType, map);

		return map;
	}

	/**
	 * 在实例上保存属性值时，通过“属性_OLD”保存变更前的值，通过“属性”保存变更后的值。
	 * 为了在报表中，便于理解对值进行特殊处理。
	 * 当变更方式为修改或者删除时，只有变更前，没有变更后。
	 * 当变更方式为添加时，只有变更后。
	 * 当变更方式为替换时，即有变更前，也有变更后。
	 * 
	 * @param actionType
	 * @param map
	 * @throws ServiceRequestException
	 */
	private void buildEND1END2Object(String actionType, Map<String, Object> map) throws ServiceRequestException
	{
		if (UECActionTypeEnum.Del.name().equals(actionType))
		{
			Map<String, Object> end1 = this.buildEND1ObjectToMap(map, "END1", true);
			Map<String, Object> end2 = this.buildEND2ObjectToMap(map, "END2", true);
			map.putAll(end1);
			map.putAll(end2);
		}
		else if (UECActionTypeEnum.Add.name().equals(actionType))
		{
			Map<String, Object> end1 = this.buildEND1ObjectToMap(map, "END1", false);
			Map<String, Object> end2 = this.buildEND2ObjectToMap(map, "END2", false);
			map.putAll(end1);
			map.putAll(end2);
		}
		else
		{
			Map<String, Object> end1 = this.buildEND1ObjectToMap(map, "END1", false);
			Map<String, Object> end2 = this.buildEND2ObjectToMap(map, "END2", false);
			map.putAll(end1);
			map.putAll(end2);

			end1 = this.buildEND1ObjectToMap(map, "END1", true);
			end2 = this.buildEND2ObjectToMap(map, "END2", true);
			map.putAll(end1);
			map.putAll(end2);
		}
	}

	private ObjectGuid getChangedItem(FoundationObject eco)
	{
		// 从ECO中取得变更对象
		ObjectGuid changedItem = new ObjectGuid();
		changedItem.setGuid(eco.get("CHANGEITEM").toString());
		changedItem.setClassGuid(eco.get("CHANGEITEM$CLASS").toString());
		changedItem.setMasterGuid(eco.get("CHANGEITEM$MASTER").toString());

		return changedItem;
	}

	/**
	 * 根据指定类，判断指定字段的类型，从制定对象中取得其值。
	 * 
	 * @param paramObject
	 * @param classGuid
	 * @param paramFieldName
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getFieldVal(Map<String, Object> paramObject, String classGuid, String paramFieldName) throws ServiceRequestException
	{
		if (paramObject == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		paramFieldName = paramFieldName.toUpperCase();
		if (classGuid == null)
		{
			return StringUtils.convertNULLtoString(paramObject.get(paramFieldName));
		}

		String fieldNameMayOld = paramFieldName;

		// 字段名字的形式为：F, 普通字段
		// F$, 普通系统字段
		// X$F, 对象类型的普通字段或者对象类型的系统字段$对象上的普通字段
		// X$F$,对象类型的普通字段或者对象类型的系统字段$对象上的普通系统字段
		// X$X$F，X$X$F$等
		// 以上X或者X$为对象，F为字段

		// 变更前的字段为：字段_OLD形式。因此如果字段包含_OLD，则字段为变更前的字段。
		// 形式如下：F_OLD，普通字段
		// F$_OLD，普通系统字段
		// X_OLD$F或者X$_OLD$F, 对象类型的普通字段或者对象类型的系统字段$对象上的普通字段
		// X_OLD$F$或者X$_OLD$F$,对象类型的普通字段或者对象类型的系统字段$对象上的普通系统字段
		// X_OLD$X$F，X_OLD$X$F$，X$_OLD$X$F，X$_OLD$X$F$等
		// 以上X和X_OLD或者X$和X$_OLD为对象，F为字段

		// 报表模板上对于系统字段变更前为X或者X$，变更后统一使用X_OLD或者X_OLD$
		List<String> tmpList = Arrays.asList(SPECIAL_FIELD_NAME_LIST);
		// 如果字段中包含$符号，则截取字段中的开始位置到$符号的位置，即为对象X或者属性F
		if (!tmpList.contains(paramFieldName) && paramFieldName.indexOf("$") != -1)
		{
			fieldNameMayOld = paramFieldName.substring(0, paramFieldName.indexOf("$"));
		}

		// 判断该字段的类型时，需要把_OLD删除，用原始字段判断，但是从对象中取得该字段的值时，需要用实际字段。

		// 此处的realFieldName的值，即为第一个$之前的部分（如果是系统字段，则包含$）
		boolean isOldVal = false;
		if ((fieldNameMayOld.toLowerCase()).endsWith("_old"))
		{
			isOldVal = true;
		}

		String oldSubfix = isOldVal ? "_OLD" : "";

		ClassField field = null;
		String origFieldName = fieldNameMayOld;
		try
		{
			ClassInfo classInfo = this.params.getEMM().getClassByGuid(classGuid);
			if (classInfo == null)
			{
				return StringUtils.convertNULLtoString(paramObject.get(paramFieldName));
			}
			if (fieldNameMayOld.endsWith("_OLD"))
			{
				// 把_OLD删除，即得到原始字段
				origFieldName = fieldNameMayOld.substring(0, fieldNameMayOld.length() - 4);
			}

			// 如果字段是系统字段，则需要再补回$符号
			if (paramFieldName.contains("BOTITLE"))
			{
				origFieldName = origFieldName + "$";
			}

			field = this.params.getEMM().getFieldByName(classInfo.getName(), origFieldName, true);
			if (field.isSystem())
			{
				origFieldName = origFieldName + "$";
			}

			// 代码中对于变更前的字段形式为：END2$_OLD, 但是模板中形式为END2_OLD$，因此需要把模板中的字段修改为系统中的形式，以便从对象中取值。
			fieldNameMayOld = origFieldName + oldSubfix;
		}
		catch (Exception e)
		{
			String message = "field is not exists! field = " + paramFieldName;
			DynaLogger.debug(message);

			// 非模型上的字段，若值为String类型，则直接返回其值，否则，返回空。
			return this.getStringValueOfField(paramObject, paramFieldName);
		}

		if (FieldTypeEnum.DATE.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = DATE");
			return this.getTimeVal(paramObject, fieldNameMayOld, DateFormat.PTN_YMD);
		}
		else if (FieldTypeEnum.DATETIME.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = DATE");
			return this.getTimeVal(paramObject, fieldNameMayOld, DateFormat.PTN_YMDHMS);
		}
		else if (FieldTypeEnum.OBJECT.equals(field.getType()))
		{
			// 如果字段是变更前字段，则由_OLD后缀，否则，没有后缀。
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = OBJECT");

			// 若对象类型的字段，其guid不存在，则无法取得该对象，所以从对象中直接返回参数字段的其值。
			String guid = (String) paramObject.get(fieldNameMayOld);
			if (!StringUtils.isGuid(guid))
			{
				return StringUtils.convertNULLtoString(paramObject.get(paramFieldName));
			}

			// Object中的特殊字段类型（非FoundationObject类型）判断
			ClassInfo classInfo = this.params.getEMM().getClassByName(field.getTypeValue());
			if (classInfo != null)
			{
				if (classInfo.hasInterface(ModelInterfaceEnum.IUser) || classInfo.hasInterface(ModelInterfaceEnum.IGroup) || classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar)
						|| classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
				{
					return this.getSpecialObjectName(classInfo, paramObject, field);
				}
			}
			if (field.isSystem())
			{
				// 系统中的OWNERUSER$和OWNERGROUP$的特殊处理
				// 其变更前的值为：OWNERUSER$NAME_OLD、OWNERGROUP$NAME_OLD，变更后的值为：OWNERUSER$NAME、OWNERGROUP$NAME
				if (origFieldName.endsWith("USER$") || origFieldName.endsWith("GROUP$"))
				{
					return StringUtils.convertNULLtoString(paramObject.get(origFieldName + "NAME" + oldSubfix));
				}

			}

			// 原始字段的classguidkey和masterguiddkey
			String classguidKey = (origFieldName.endsWith("$") ? origFieldName + "CLASS" : origFieldName + "$CLASS");
			String masterguiddKey = (origFieldName.endsWith("$") ? origFieldName + "MASTER" : origFieldName + "$MASTER");

			// 变更前的key为X$CLASS、X$MASTER
			// 变更后的key为X$CLASS_OLD、X$MASTER_OLD
			FoundationObjectImpl founObj = this.getObjectByPropertyKey(guid, (String) paramObject.get(classguidKey + oldSubfix),
					(String) paramObject.get(masterguiddKey + oldSubfix));
			if (founObj == null)
			{
				// 部分数据变更前的key为X$CLASSGUID、X$MASTERGUID
				// 变更后的key为X$CLASSGUID_OLD、X$MASTERGUID_OLD
				founObj = this.getObjectByPropertyKey(guid, (String) paramObject.get(classguidKey + "GUID" + oldSubfix),
						(String) paramObject.get(masterguiddKey + "GUID" + oldSubfix));
				if (founObj == null)
				{
					if (founObj == null)
					{
						// 部分数据变更前的key为X$CLASSGUID、X$MASTERFK
						// 变更后的key为X$CLASSGUID_OLD、X$MASTERFK_OLD
						founObj = this.getObjectByPropertyKey(guid, (String) paramObject.get(classguidKey + "GUID" + oldSubfix),
								(String) paramObject.get(masterguiddKey + "FK" + oldSubfix));
						if (founObj == null)
						{
							// 如果Object类型的字段，没有取到对应的Object对象，则直接从参数对象中取得参数字段的值。
							return StringUtils.convertNULLtoString(paramObject.get(paramFieldName));
						}
					}
				}
			}

			// 若参数字段直接是对象字段，没有指定属性，则返回对象的fullname。
			// 如：END2$
			if (fieldNameMayOld.equals(paramFieldName))
			{
				return StringUtils.convertNULLtoString(founObj.get("FULLNAME$"));
			}

			// 从参数字段中截取从第一个$符号之后的部分，即为Object类型字段的属性
			String property = paramFieldName.substring(paramFieldName.indexOf("$") + 1);
			if (property.equalsIgnoreCase("BOTITLE$"))
			{
				return StringUtils.getMsrTitle((String) founObj.get(property), this.params.getLang().getType());
			}
			if (!StringUtils.isNullString(property))
			{
				// 递归查询
				String val = this.getFieldVal(founObj, property);
				if (StringUtils.isNullString(val))
				{
					// 个别情况下，对象存储Object类型的数据时，其上的系统字段属性可能会缺少$，如：NAME
					// 对象中存储的是X$NAME，但NAME是系统字段，从X上查询时，需要使用NAME$
					val = this.getFieldVal(founObj, property + "$");
					if (StringUtils.isNullString(val))
					{
						// 没查询到值，则直接从参数对象返回参数字段对应的值
						return StringUtils.convertNULLtoString(paramObject.get(paramFieldName));
					}
					return StringUtils.convertNULLtoString(val);
				}
				return StringUtils.convertNULLtoString(val);
			}
			return this.getStringValueOfField(founObj, property);
		}
		else if (FieldTypeEnum.CODE.equals(field.getType()) || FieldTypeEnum.CODEREF.equals(field.getType()) || FieldTypeEnum.CLASSIFICATION.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = " + field.getType());

			String codeGuid = (String) paramObject.get(fieldNameMayOld);
			CodeItemInfo codeItemInfo = this.getCodeItemByGuid(codeGuid);
			if (codeItemInfo == null)
			{
				return StringUtils.EMPTY_STRING;
			}

			if (paramFieldName.endsWith("$NAME"))
			{
				return StringUtils.convertNULLtoString(codeItemInfo.getName());
			}

			return this.getTitle(codeItemInfo.getTitle());
		}
		else if (FieldTypeEnum.INTEGER.equals(field.getType()) || FieldTypeEnum.FLOAT.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = " + field.getType());
			if (paramObject.get(fieldNameMayOld) == null)
			{
				return StringUtils.EMPTY_STRING;
			}
			Object o = paramObject.get(fieldNameMayOld);
			if (o == null)
			{
				return StringUtils.EMPTY_STRING;
			}

			if (o instanceof BigDecimal)
			{
				BigDecimal decimal = (BigDecimal) paramObject.get(fieldNameMayOld);
				return StringUtils.convertNULLtoString(decimal.toString());
			}
			else
			{
				return StringUtils.convertNULLtoString(String.valueOf(o));
			}
		}
		else if (FieldTypeEnum.BOOLEAN.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = Boolean");
			if (paramObject.get(fieldNameMayOld) == null)
			{
				return "N";
			}

			return StringUtils.convertNULLtoString(paramObject.get(fieldNameMayOld));
		}
		else if (FieldTypeEnum.MULTICODE.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = MULTICODE");
			if (paramObject.get(fieldNameMayOld) == null)
			{
				return StringUtils.EMPTY_STRING;
			}

			StringBuffer multicode = new StringBuffer();
			// MULTICODE类型的字段，其在数据库中的格式是使用[;]连接的guid，返回值也是用[;]连接
			String[] codeGuids = ((String) paramObject.get(fieldNameMayOld)).split(";");
			for (String codeGuid : codeGuids)
			{
				CodeItemInfo codeItemInfo = this.getCodeItemByGuid(codeGuid);
				if (codeItemInfo == null)
				{
					return StringUtils.EMPTY_STRING;
				}

				if (multicode.length() != 0)
				{
					multicode.append(";");
				}

				if (paramFieldName.endsWith("$NAME"))
				{
					multicode.append(StringUtils.convertNULLtoString(codeItemInfo.getName()));
				}
				else
				{
					multicode.append(this.getTitle(codeItemInfo.getTitle()));
				}
			}
			return multicode.toString();
		}
		else if (FieldTypeEnum.STATUS.equals(field.getType()))
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = Status");
			// 状态类型特殊处理
			String status = (String) paramObject.get(fieldNameMayOld);
			SystemStatusEnum statusEnmu = SystemStatusEnum.getStatusEnum(status);
			return StringUtils.convertNULLtoString(this.params.getMSRM().getMSRString(statusEnmu.getMsrId(), this.params.getLang().toString()));
		}
		else
		{
			DynaLogger.debug("get field value! fieldname = " + fieldNameMayOld + ", field type = String");
			// 生命周期特殊处理
			if ("LIFECYCLEPHASE$".equals(fieldNameMayOld.toUpperCase()) || "LIFECYCLEPHASE$_OLD".equals(fieldNameMayOld.toUpperCase()))
			{
				String lifecyclephase = (String) paramObject.get(fieldNameMayOld);
				if (StringUtils.isGuid(lifecyclephase))
				{
					LifecyclePhaseInfo lifecyclePhaseInfo = this.params.getEMM().getLifecyclePhaseInfo(lifecyclephase);
					if (lifecyclePhaseInfo != null)
					{
						if (paramFieldName.endsWith("$TITLE"))
						{
							return this.getTitle(lifecyclePhaseInfo.getTitle());
						}
						return StringUtils.convertNULLtoString(lifecyclePhaseInfo.getName());
					}
				}
			}
			return StringUtils.convertNULLtoString(paramObject.get(paramFieldName));
		}
	}

	private String getFieldVal(FoundationObjectImpl object, String fieldName) throws ServiceRequestException
	{
		if (object == null)
		{
			return StringUtils.EMPTY_STRING;
		}
		return this.getFieldVal(object, object.getObjectGuid().getClassGuid(), fieldName);
	}

	/**
	 * @param foundation
	 * @param fieldName
	 * @return
	 */
	private String getStringValueOfField(Map<String, Object> foundation, String fieldName)
	{
		if (foundation == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		if (foundation.get(fieldName) instanceof String)
		{
			return StringUtils.convertNULLtoString(foundation.get(fieldName));
		}
		return StringUtils.EMPTY_STRING;
	}

	private String getSpecialObjectName(ClassInfo classInfo, Map<String, Object> obj, ClassField field) throws ServiceRequestException
	{
		if (classInfo == null || obj == null || field == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		String guid = (String) obj.get(field.getName().toUpperCase());
		if (!StringUtils.isGuid(guid))
		{
			return StringUtils.EMPTY_STRING;
		}

		SystemObjectImpl object = null;
		if (classInfo.hasInterface(ModelInterfaceEnum.IGroup))
		{
			object = this.params.getAAS().getGroup(guid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IUser))
		{
			object = this.params.getAAS().getUser(guid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
		{
			object = this.params.getPPMS().getProjectRole(guid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			object = this.params.getPPMS().getWorkCalendar(guid);
		}

		return object == null ? StringUtils.EMPTY_STRING : object.getName();
	}

	private FoundationObjectImpl getObjectByPropertyKey(String propertyGuid, String propertyClassGuid, String propertyMasterGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(propertyGuid) || !StringUtils.isGuid(propertyClassGuid) || !StringUtils.isGuid(propertyMasterGuid))
		{
			return null;
		}

		ObjectGuid oGuid = new ObjectGuid();
		oGuid.setGuid(propertyGuid);
		oGuid.setClassGuid(propertyClassGuid);
		oGuid.setMasterGuid(propertyMasterGuid);

		return (FoundationObjectImpl) this.params.getBOAS().getObject(oGuid);
	}

	private CodeItemInfo getCodeItemByGuid(String codeGuid) throws ServiceRequestException
	{
		return this.params.getEMM().getCodeItem(codeGuid);
	}

	/**
	 * 从map对象中，取得指定key对应的值。
	 * 若要取得变更前的值，则key为：属性+“_OLD”
	 * 
	 * @param map
	 * @param objectKey
	 * @param isOld
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, Object> buildEND1ObjectToMap(Map<String, Object> map, String objectKey, boolean isOld) throws ServiceRequestException
	{
		if (map == null || StringUtils.isNullString(objectKey))
		{
			return new HashMap<String, Object>();
		}

		String objectGuid = (String) map.get(objectKey.toUpperCase() + (isOld ? "_OLD" : StringUtils.EMPTY_STRING));
		String object$classguid = (String) map.get(objectKey.toUpperCase() + "$CLASS" + (isOld ? "_OLD" : StringUtils.EMPTY_STRING));
		String object$masterguid = (String) map.get(objectKey.toUpperCase() + "$MASTER" + (isOld ? "_OLD" : StringUtils.EMPTY_STRING));

		return this.buildObjectToMap(objectGuid, object$classguid, object$masterguid, objectKey);
	}

	/**
	 * 从map对象中，取得指定key对应的值。
	 * 若要取得变更前的值，则key为：属性+“_OLD”
	 * end2的属性比end1的属性多了一个$
	 * 
	 * @param map
	 * @param objectKey
	 * @param isOld
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, Object> buildEND2ObjectToMap(Map<String, Object> map, String objectKey, boolean isOld) throws ServiceRequestException
	{
		if (map == null || StringUtils.isNullString(objectKey))
		{
			return new HashMap<String, Object>();
		}

		String objectGuid = (String) map.get(objectKey.toUpperCase() + "$" + (isOld ? "_OLD" : StringUtils.EMPTY_STRING));
		String object$classguid = (String) map.get(objectKey.toUpperCase() + "$CLASSGUID" + (isOld ? "_OLD" : StringUtils.EMPTY_STRING));
		String object$masterguid = (String) map.get(objectKey.toUpperCase() + "$MASTERFK" + (isOld ? "_OLD" : StringUtils.EMPTY_STRING));

		String reportObjKey = objectKey.toUpperCase() + (isOld ? "_OLD" : StringUtils.EMPTY_STRING);

		return this.buildObjectToMap(objectGuid, object$classguid, object$masterguid, reportObjKey);
	}

	/**
	 * 重构数据的存储方法。
	 * key为：对象$属性，value为原value。
	 * 
	 * @param guid
	 * @param classGuid
	 * @param masterGuid
	 * @param objectKey
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, Object> buildObjectToMap(String guid, String classGuid, String masterGuid, String objectKey) throws ServiceRequestException
	{
		FoundationObjectImpl object = this.getObjectByPropertyKey(guid, classGuid, masterGuid);
		if (object == null)
		{
			return new HashMap<String, Object>();
		}

		Map<String, Object> map = new HashMap<String, Object>();

		Set<Entry<String, Object>> set = object.entrySet();
		Iterator<Entry<String, Object>> it = set.iterator();
		while (it.hasNext())
		{
			Entry<String, Object> entry = it.next();

			String key = objectKey.toUpperCase() + "$" + entry.getKey();
			map.put(key, this.getFieldVal(object, entry.getKey()));
		}
		return map;
	}

	/**
	 * 批量变更
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("unchecked")
	private List<FoundationObject> getBatchECDataList(FoundationObjectImpl eco) throws ServiceRequestException
	{
		List<StructureObject> structureObjectList = this.params.getUECS().getEciBYBatchECO(eco.getObjectGuid());

		String changeItemGuid = (String) eco.get(UpdatedECSConstants.ChangeItem.toUpperCase());
		String changeItemMasterGuid = (String) eco.get(UpdatedECSConstants.ChangeItem.toUpperCase() + "$MASTER");
		String changeItemClassGuid = (String) eco.get(UpdatedECSConstants.ChangeItem.toUpperCase() + "$CLASS");
		FoundationObjectImpl changeItemClass = this.getObjectByPropertyKey(changeItemGuid, changeItemClassGuid, changeItemMasterGuid);

		String bomTemplateName = (String) eco.get(UpdatedECSConstants.BOMTemplate.toUpperCase());

		BOMTemplateInfo bomTemplate = null;
		if (changeItemClass != null)
		{
			bomTemplate = this.params.getEMM().getBOMTemplateByName(changeItemClass.getObjectGuid(), bomTemplateName);
		}

		ClassInfo classInfo = null;
		if (bomTemplate != null)
		{
			classInfo = this.getClassInfoByTemplate(bomTemplate.getGuid());
		}

		List<FoundationObject> eciList = new ArrayList<FoundationObject>();
		if (structureObjectList != null)
		{
			for (StructureObject structureObject : structureObjectList)
			{
				FoundationObjectImpl foundationObject = new FoundationObjectImpl();

				// 解决对象
				String guid = (String) structureObject.get("ECTARGETITEM");
				String classguid = (String) structureObject.get("ECTARGETITEM$CLASS");
				String masterguid = (String) structureObject.get("ECTARGETITEM$MASTER");

				// 父阶对象:END1、变更对象:END2$
				Map<String, Object> strucMap = (Map<String, Object>) structureObject.get("ECCHANGERECORD1");
				Map<String, Object> end1 = this.buildEND1ObjectToMap(strucMap, "END1", false);

				Iterator<Entry<String, Object>> it = end1.entrySet().iterator();
				while (it.hasNext())
				{
					Entry<String, Object> entry = it.next();
					// KEY已经包含了“END1$”，因此前缀不需要添加
					foundationObject.put("ECBEFORE-" + entry.getKey(), entry.getValue() == null ? StringUtils.EMPTY_STRING : entry.getValue());
				}

				// 解决对象
				end1 = this.buildObjectToMap(guid, classguid, masterguid, "END1");
				it = end1.entrySet().iterator();
				while (it.hasNext())
				{
					Entry<String, Object> entry = it.next();
					// KEY已经包含了“END1$”，因此前缀不需要添加
					foundationObject.put("ECAFTER-" + entry.getKey(), entry.getValue() == null ? StringUtils.EMPTY_STRING : entry.getValue());
				}

				// 把结构上的字段添加到datasource中
				List<String> columns = this.subReportColumnMap.get("ECILIST");
				for (String column : columns)
				{
					if (column.equalsIgnoreCase("ISBATCH"))
					{
						foundationObject.put(column, true);
					}
					else if (column.startsWith("ECO."))
					{
						String fieldName = (column.replace("#", "$")).substring(4);
						String val = this.getFieldVal(eco, fieldName);
						// 为了模板便于理解，所以对个别字段进行特殊处理，没有使用模型的字段，而是使用了自定义的便于理解的字段。
						// 所以此处把自定义的字段转为模型上的字段去取值。
						// 变更对象为变更前的对象，解决对象为变更后的对象。
						for (String[] params : SPECIAL_PARAM_NAME_LIST)
						{
							if (fieldName.startsWith(params[0]))
							{
								String fieldNameOfObject = fieldName.substring(fieldName.indexOf("$"));
								String property = params[1];
								String modifyType = this.getFieldVal(eco, "MODIFYTYPE$NAME");
								// 当批量变更批量添加时，为了便于报表便于理解，变更后对象设为变更对象（实际存储的值为解决对象），所以需要特殊处理。变更前的对象为空。
								if ("BatchAdd".equals(modifyType))
								{
									property = params[2];
								}

								val = this.getFieldVal(eco, property + fieldNameOfObject);
								foundationObject.put(column, val);
							}
						}

						foundationObject.put(column, val);
					}
					else if (column.equals("END2$FULLNAME$") && "Replace".equalsIgnoreCase((String) structureObject.get("ACTIONTYPE$NAME")))
					{
						foundationObject.put(column, eco.get("TARGETITEM$NAME"));
					}
					else if (!column.startsWith("ECBEFORE-") && !column.startsWith("ECAFTER-"))
					{
						if (classInfo == null)
						{
							foundationObject.put(column, strucMap.get(column) == null ? StringUtils.EMPTY_STRING : strucMap.get(column));
						}
						else
						{
							foundationObject.put(column, this.getFieldVal(strucMap, classInfo.getGuid(), column));
						}
					}
				}

				// 数量为空时，给默认值为1
				Object quantity = foundationObject.get("QUANTITY");
				Object quantityOld = foundationObject.get("QUANTITY_OLD");
				foundationObject.put("QUANTITY", quantity == null || StringUtils.EMPTY_STRING.equals(quantity.toString()) ? "1.0" : quantity.toString());
				foundationObject.put("QUANTITY_OLD", quantityOld == null || StringUtils.EMPTY_STRING.equals(quantityOld.toString()) ? "1.0" : quantityOld.toString());

				eciList.add(foundationObject);
			}
		}

		return eciList;
	}

	private ClassInfo getClassInfoByTemplate(String templateGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = null;
		if (StringUtils.isGuid(templateGuid))
		{
			BOMTemplate bomTemplate = this.params.getEMM().getBOMTemplate(templateGuid);
			if (bomTemplate != null)
			{
				if (!StringUtils.isGuid(bomTemplate.getStructureClassGuid()))
				{
					if (!StringUtils.isNullString(bomTemplate.getStructureClassName()))
					{
						classInfo = this.params.getEMM().getClassByName(bomTemplate.getStructureClassName());
					}
				}
				else
				{
					classInfo = this.params.getEMM().getClassByGuid(bomTemplate.getStructureClassGuid());
				}
			}
			else
			{
				RelationTemplate relationTemplate = this.params.getEMM().getRelationTemplate(templateGuid);
				if (relationTemplate != null)
				{
					if (!StringUtils.isGuid(relationTemplate.getStructureClassGuid()))
					{
						if (!StringUtils.isNullString(relationTemplate.getStructureClassName()))
						{
							classInfo = this.params.getEMM().getClassByName(relationTemplate.getStructureClassName());
						}
					}
					else
					{
						classInfo = this.params.getEMM().getClassByGuid(relationTemplate.getStructureClassGuid());
					}
				}
			}
		}

		return classInfo;
	}

	/**
	 * 取得多语言title
	 * 
	 * @param o
	 * @param param
	 * @param type
	 * @return
	 */
	private String getTitle(String title)
	{
		if (StringUtils.isNullString(title))
		{
			return StringUtils.EMPTY_STRING;
		}

		return StringUtils.convertNULLtoString(StringUtils.getMsrTitle(title, this.params.getLang().getType()));
	}

	/**
	 * 取出时间
	 * 
	 * @param o
	 * @param param
	 * @return
	 */
	private String getTimeVal(Map<String, Object> o, String fieldName, String pattern)
	{
		Calendar calendar = Calendar.getInstance();
		Object time = o.get(fieldName);
		if (time == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		if (time instanceof Long)
		{
			calendar.setTimeInMillis((Long) time);
		}
		else if (time instanceof Timestamp)
		{
			calendar.setTimeInMillis(((Timestamp) o.get(fieldName)).getTime());
		}
		else
		{
			return StringUtils.convertNULLtoString(time.toString());
		}

		return StringUtils.convertNULLtoString(DateFormat.format(calendar.getTime(), pattern));
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
