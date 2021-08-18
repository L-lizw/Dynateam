package dyna.app.service.brs.cpb;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.configparamter.*;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ConfigParameterTableType;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.*;
import dyna.data.DataServer;
import dyna.data.service.relation.RelationService;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConfigQueryStub extends AbstractServiceStub<CPBImpl>
{

	/**
	 * 通过表类型取得动态列头
	 * 
	 * @param masterGuid
	 * @param tableTypeEnum
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<DynamicColumnTitle> listColumnTitles(ObjectGuid objectGuid, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		return this.listColumnTitles(instance, tableTypeEnum, ruleTime);
	}

	protected List<DynamicColumnTitle> listColumnTitles(FoundationObject instance, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		DynamicColumnTitle columnTitle = new DynamicColumnTitle();
		columnTitle.setMasterGuid(instance.getObjectGuid().getMasterGuid());
		if (tableTypeEnum != null)
		{
			columnTitle.setTableType(tableTypeEnum);
		}
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}
		columnTitle.setReleaseTime(ruleTime);
		if (instance.isLatestRevision())
		{
			columnTitle.setReleaseTime(null);
		}

		if (tableTypeEnum != null)
		{
			if (columnTitle.getTableType() == ConfigParameterTableType.G || columnTitle.getTableType() == ConfigParameterTableType.P)
			{
				columnTitle.put("ORDERBYSEQUENCE", "Y");
			}
		}

		return sds.query(DynamicColumnTitle.class, columnTitle, "selectCustTtileOfTable");
	}

	/**
	 * 取得所有L号（只包含L号，不包含图面变量）
	 * 
	 * @param ruleTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfList> listAllList(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		return this.listAllList(objectGuid, ruleTime, null);
	}

	/**
	 * 取得默认L番号L00的数据
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected TableOfList getDefaultL00Number(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		List<TableOfList> list = this.listAllList(objectGuid, ruleTime, "L00");
		if (!SetUtils.isNullList(list))
		{
			TableOfList defaultTableOfList = list.get(0);

			List<DynamicOfColumn> columnList = this.listAllVariableOfLTable(objectGuid, ruleTime, "L00");
			defaultTableOfList.setColumns(columnList);
			return defaultTableOfList;
		}

		return null;
	}

	/**
	 * 取得指定分组的所有L番号
	 * 
	 * @param groupGuidList
	 *            GroupOfList的uniquevalue列表
	 * @param tableTypeEnum
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfList> listAllListOfGroup(List<String> groupGuidList, Date end1ReleaseTime) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		TableOfList tableOfList = new TableOfList();
		tableOfList.put("GROUPGUIDLIST", groupGuidList);
		if (end1ReleaseTime != null)
		{
			end1ReleaseTime = DateFormat.getDateOfEnd(end1ReleaseTime, DateFormat.PTN_YMDHMS);
		}
		tableOfList.setReleaseTime(end1ReleaseTime);

		return sds.query(TableOfList.class, tableOfList, "selectAllList");
	}

	/**
	 * 取得所有L表数据
	 * 
	 * @param masterGuid
	 * @param tableTypeEnum
	 *            La或者Lb
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfList> listTableOfListData(ObjectGuid objectGuid, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);

		if (tableTypeEnum != null)
		{
			searchConditionMap.put("TABLETYPE", tableTypeEnum.toString());
		}
		List<TableOfList> lNumberOfGroupList = sds.query(TableOfList.class, searchConditionMap, "selectAllList");
		if (!SetUtils.isNullList(lNumberOfGroupList))
		{
			List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfList");
			if (!SetUtils.isNullList(columnList))
			{
				Map<String, String> titleMap = this.getTitleGuidMap(instance, tableTypeEnum, true, ruleTime);
				for (TableOfList lNumberOfGroup : lNumberOfGroupList)
				{
					for (DynamicOfColumn column : columnList)
					{
						column.setName(titleMap.get(column.getTitleGuid()));
						if (lNumberOfGroup.getUniqueValue().equals(column.getMasterFK()))
						{
							lNumberOfGroup.addColumn(column);
						}
					}
				}
			}
		}

		return lNumberOfGroupList;
	}

	/**
	 * 取得G表数据
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfGroup> listTableOfGroup(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		return this.listTableOfGroup(instance, ruleTime);
	}

	/**
	 * 取得G表数据
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfGroup> listTableOfGroup(FoundationObject instance, Date ruleTime) throws ServiceRequestException
	{
		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);
		if (ruleTime != null)
		{
			Date releaseTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
			searchConditionMap.put("RELEASETIME", releaseTime);
		}

		// 取得所有的G番号
		SystemDataService sds = DataServer.getSystemDataService();
		List<TableOfGroup> gNumberList = sds.query(TableOfGroup.class, searchConditionMap, "selectGNumberList");
		if (!SetUtils.isNullList(gNumberList))
		{
			List<String> gNumberGuidList = new ArrayList<String>();
			for (TableOfGroup gNumber : gNumberList)
			{
				gNumberGuidList.add(gNumber.getUniqueValue());
			}

			// 取得G番号的自定义字段列
			searchConditionMap.put("GNUMBERLIST", gNumberGuidList);
			List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfG");

			if (!SetUtils.isNullList(columnList))
			{
				Map<String, String> titleMap = this.getTitleGuidMap(instance, ConfigParameterTableType.G, true, ruleTime);
				for (TableOfGroup gNumber : gNumberList)
				{
					for (DynamicOfColumn column : columnList)
					{
						column.setName(titleMap.get(column.getTitleGuid()));
						if (gNumber.getUniqueValue().equals(column.getMasterFK()))
						{
							gNumber.addColumn(column);
						}
					}
				}
			}
		}

		return gNumberList;
	}

	/**
	 * 取得A-E，R,Q表数据
	 * 
	 * @param masterGuid
	 * @param tableTypeEnum
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfRegion> listTableOfRegion(ObjectGuid objectGuid, ConfigParameterTableType tableTypeEnum, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}

		SystemDataService sds = DataServer.getSystemDataService();

		// 取得基本数据（固定列）
		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);
		if (tableTypeEnum != null)
		{
			searchConditionMap.put("TABLETYPE", tableTypeEnum.name());
		}
		List<TableOfRegion> regionList = sds.query(TableOfRegion.class, searchConditionMap);
		if (SetUtils.isNullList(regionList))
		{
			return null;
		}

		List<String> regionGuidList = new ArrayList<String>();
		for (TableOfRegion region : regionList)
		{
			regionGuidList.add(region.getUniqueValue());
		}
		searchConditionMap.put("RNUMBERLIST", regionGuidList);

		// 根据行Guid取得动态列
		List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfRegion");
		if (!SetUtils.isNullList(columnList))
		{
			Map<String, String> titleMap = this.getTitleGuidMap(instance, tableTypeEnum, true, ruleTime);
			for (TableOfRegion region : regionList)
			{
				for (DynamicOfColumn column : columnList)
				{
					column.setName(titleMap.get(column.getTitleGuid()));
					if (region.getUniqueValue().equals(column.getMasterFK()))
					{
						region.addColumn(column);
					}
				}
			}
		}

		return regionList;
	}

	/**
	 * 取得F表的数据
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfExpression> listTableOfExpression(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);
		if (ruleTime != null)
		{
			Date releaseTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
			searchConditionMap.put("RELEASETIME", releaseTime);
		}

		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(TableOfExpression.class, searchConditionMap);
	}

	/**
	 * 取得P表的数据
	 * 
	 * @param masterGuid
	 * @param gNumber
	 * @param end1ReleaseTime
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfParameter> listTableOfParameter(ObjectGuid objectGuid, String gNumber, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}
		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);
		if (!StringUtils.isNullString(gNumber))
		{
			searchConditionMap.put("GNUMBER", gNumber);
		}

		SystemDataService sds = DataServer.getSystemDataService();
		List<TableOfParameter> pNumberList = sds.query(TableOfParameter.class, searchConditionMap);

		if (!SetUtils.isNullList(pNumberList))
		{
			List<String> pNumberGuidList = new ArrayList<String>();
			for (TableOfParameter param : pNumberList)
			{
				pNumberGuidList.add(param.getUniqueValue());
			}

			searchConditionMap.put("PNUMBERLIST", pNumberGuidList);
			List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfP");

			if (!SetUtils.isNullList(columnList))
			{
				Map<String, String> titleMap = this.getTitleGuidMap(instance, ConfigParameterTableType.P, true, ruleTime);
				for (TableOfParameter pNumber : pNumberList)
				{
					for (DynamicOfColumn column : columnList)
					{
						column.setName(titleMap.get(column.getTitleGuid()));
						if (pNumber.getUniqueValue().equals(column.getMasterFK()))
						{
							pNumber.addColumn(column);
						}
					}
				}
			}
		}
		return pNumberList;
	}

	/**
	 * 取得动态column的titleguid对应的title，或者title对应的titleguid
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @param tableType
	 * @return
	 * @throws ServiceRequestException
	 */
	Map<String, String> getTitleGuidMap(FoundationObject instance, ConfigParameterTableType tableType, boolean isGuidKey, Date ruleTime) throws ServiceRequestException
	{
		Map<String, String> titleMap = new HashMap<String, String>();
		List<DynamicColumnTitle> columnTitles = this.stubService.getConfigQueryStub().listColumnTitles(instance, tableType, ruleTime);
		if (!SetUtils.isNullList(columnTitles))
		{
			for (DynamicColumnTitle title : columnTitles)
			{
				if (isGuidKey)
				{
					titleMap.put(title.getUniqueValue(), title.getTitle());
				}
				else
				{
					titleMap.put(title.getTitle(), title.getUniqueValue());
				}
			}
		}

		return titleMap;
	}

	protected List<TableOfInputVariable> listTableOfInputVariable(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}
		searchConditionMap.put("RELEASETIME", ruleTime);

		SystemDataService sds = DataServer.getSystemDataService();
		List<TableOfInputVariable> listVariable = sds.query(TableOfInputVariable.class, searchConditionMap);
		if (!SetUtils.isNullList(listVariable))
		{
			int i = 1;
			for (TableOfInputVariable variable : listVariable)
			{
				variable.put("index", i);
				i++;
			}
		}
		return listVariable;
	}

	protected List<TableOfMark> listTableOfMarkData(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);

		SystemDataService sds = DataServer.getSystemDataService();
		return sds.query(TableOfMark.class, searchConditionMap);
	}

	protected String getFieldNameByGNumber(ObjectGuid end1ObjectGuid, DataRule dateRule, String gNumber)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", end1ObjectGuid.getMasterGuid());
		Date releaseTime = dateRule.getLocateTime();
		searchConditionMap.put("RELEASETIME", releaseTime);
		if (releaseTime != null)
		{
			releaseTime = DateFormat.getDateOfEnd(releaseTime, DateFormat.PTN_YMDHMS);
			searchConditionMap.put("RELEASETIME", releaseTime);
		}
		searchConditionMap.put("GNUMBER", gNumber);
		TableOfGroup tableOfGroup = sds.queryObject(TableOfGroup.class, searchConditionMap, "selectGNumberList");
		return tableOfGroup == null ? null : tableOfGroup.getClassFieldName();
	}

	protected List<StructureObject> listStructureObject(ObjectGuid end1ObjectGuid, String viewName, DataRule dataRule, SearchCondition strucSearchCondition,
			SearchCondition end2SearchCondition) throws ServiceRequestException
	{
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, viewName);
		if (relationTemplate == null)
		{
			return null;
		}

		ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(end1ObjectGuid, viewName);
		if (viewObject == null)
		{
			return null;
		}

		if (strucSearchCondition == null)
		{
			strucSearchCondition = SearchConditionFactory.createSearchConditionForStructure(relationTemplate.getStructureClassName());
		}

		if (end2SearchCondition == null)
		{
			end2SearchCondition = this.stubService.getEMM().createAssoEnd2SearchCondition(relationTemplate.getGuid(), true, true);
		}

		List<FoundationObject> end2List = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(viewObject.getObjectGuid(), strucSearchCondition,
				end2SearchCondition, dataRule, true, true);

		List<StructureObject> resList = new ArrayList<StructureObject>();
		if (!SetUtils.isNullList(end2List))
		{
			for (FoundationObject end2 : end2List)
			{
				StructureObject struc = (StructureObject) end2.get(StructureObject.STRUCTURE_CLASS_NAME);
				end2.clear(StructureObject.STRUCTURE_CLASS_NAME);
				struc.put(StructureObject.END2_UI_OBJECT, end2);
				resList.add(struc);
			}
		}

		return resList;
	}

	protected List<StructureObject> listStructureObject(ObjectGuid end1ObjectGuid, DataRule dataRule, List<String> gNumberList, SearchCondition strucSearchCondition,
			SearchCondition end2SearchCondition) throws ServiceRequestException
	{
		ViewObject viewObject = this.stubService.getBOAS().getRelationByEND1(end1ObjectGuid, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
		if (viewObject == null)
		{
			return null;
		}

		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid,
				ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
		if (relationTemplate == null)
		{
			return null;
		}

		if (strucSearchCondition == null)
		{
			strucSearchCondition = SearchConditionFactory.createSearchConditionForStructure(relationTemplate.getStructureClassName());
		}

		if (end2SearchCondition == null)
		{
			List<RelationTemplateEnd2> templateEnd2List = DataServer.getRelationService().listRelationTemplateEnd2(relationTemplate.getGuid());
			List<String> limitClassNameList = new ArrayList<String>();
			if (!SetUtils.isNullList(templateEnd2List))
			{
				for (RelationTemplateEnd2 templateEnd2 : templateEnd2List)
				{
					limitClassNameList.add(this.stubService.getEMM().getCurrentBoInfoByName(templateEnd2.getEnd2BoName(), true).getClassName());
				}
			}
			else
			{
				return null;
			}
			end2SearchCondition = SearchConditionFactory.createSearchCondition4MulitClassSearch(limitClassNameList);
		}

		ObjectGuid end2ObjectGuid = end2SearchCondition.getObjectGuid();
		ClassStub.decorateObjectGuid(end2ObjectGuid, this.stubService);

		if (gNumberList == null)
		{
			gNumberList = new ArrayList<String>();
			List<TableOfGroup> gDataList = this.stubService.listTableOfGroup(end1ObjectGuid, dataRule.getLocateTime());
			if (!SetUtils.isNullList(gDataList))
			{
				for (TableOfGroup gData : gDataList)
				{
					if (!gNumberList.contains(gData.getGNumber()))
					{
						gNumberList.add(gData.getGNumber());
					}
				}
			}
		}

		Map<String, String> tempMap = new HashMap<String, String>();
		for (String gNumber : gNumberList)
		{
			String resultField = this.getFieldNameByGNumber(end1ObjectGuid, dataRule, gNumber);
			tempMap.put(resultField, gNumber);
			if (!StringUtils.isNullString(resultField))
			{
				strucSearchCondition.addResultField(resultField);
			}
		}
		strucSearchCondition.addResultField(ConfigParameterConstants.PARTNUMBER);
		strucSearchCondition.addResultField(ConfigParameterConstants.LNUMBER);
		strucSearchCondition.addResultField(ConfigParameterConstants.CONFIGPARAMETER);
		strucSearchCondition.addResultField(ConfigParameterConstants.CPNAME);
		end2SearchCondition.addResultField(ConfigParameterConstants.MATCHEDCLASS);
		end2SearchCondition.addResultField(ConfigParameterConstants.UNIQUENO);
		end2SearchCondition.addResultField(ConfigParameterConstants.ITEM_CLASSIFICATION);
		List<FoundationObject> end2List = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().listFoundationObjectOfRelation(viewObject.getObjectGuid(), strucSearchCondition,
				end2SearchCondition, dataRule, true, true);

		List<StructureObject> resList = new ArrayList<StructureObject>();
		if (!SetUtils.isNullList(end2List))
		{
			for (FoundationObject end2 : end2List)
			{
				StructureObject struc = (StructureObject) end2.get(StructureObject.STRUCTURE_CLASS_NAME);
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				List<String> tmpList = new ArrayList<String>();
				for (String fieldName : tempMap.keySet())
				{
					if (struc.get(fieldName) != null && !fieldName.equals(tempMap.get(fieldName)))
					{
						tmpMap.put(tempMap.get(fieldName), struc.get(fieldName));
						tmpList.add(fieldName);
					}
				}

				if (!SetUtils.isNullList(tmpList))
				{
					for (String fieldName : tmpList)
					{
						struc.clear(fieldName);
					}
				}
				if (!SetUtils.isNullMap(tmpMap))
				{
					for (String key : tmpMap.keySet())
					{
						struc.put(key, tmpMap.get(key));
					}
				}

				end2.clear(StructureObject.STRUCTURE_CLASS_NAME);
				struc.put(StructureObject.END2_UI_OBJECT, end2);
				resList.add(struc);
			}
		}
		return resList;
	}

	/**
	 * 取得指定L番号的数据，不包含自定义列的数据
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @param lNumber
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<TableOfList> listAllList(ObjectGuid objectGuid, Date ruleTime, String lNumber) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("MASTERGUID", objectGuid.getMasterGuid());
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}
		param.put("RELEASETIME", ruleTime);
		if (lNumber != null)
		{
			param.put("LNUMBER", lNumber);
		}
		return sds.query(TableOfList.class, param, "selectAllList");
	}

	/**
	 * 根据输入的L番号序列，取得所有L番号对应的图面变量的值
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @param lNumbers
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<DynamicOfColumn> listAllVariableOfLTable(ObjectGuid objectGuid, Date ruleTime, String lNumbers) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		List<String> lNumberList = ConfigUtil.transferLNumberStrToList(lNumbers);
		if (SetUtils.isNullList(lNumberList))
		{
			return null;
		}

		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("RELEASETIME", ruleTime);
		if (ruleTime != null)
		{
			Date releaseTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
			searchConditionMap.put("RELEASETIME", releaseTime);
		}
		searchConditionMap.put("MASTERGUID", objectGuid.getMasterGuid());
		searchConditionMap.put("LNUMBERLIST", lNumberList);

		return sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfLNumber");
	}

	protected List<FoundationObject> listFoundationObjectOfRelationWithoutDecorator(ObjectGuid viewObjectGuid, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, boolean isCheckAuth) throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();
		String sessionId = this.stubService.getSignature().getCredential();
		try
		{
			SearchCondition tempSSC = searchCondition;
			String strucClass = null;
			ViewObject viewObject = this.stubService.getBOAS().getRelation(viewObjectGuid);
			String templateID = viewObject.getTemplateID();
			RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(templateID);

			if (template == null)
			{
				return null;
			}
			strucClass = template.getStructureClassName();
			if (searchCondition == null)
			{
				tempSSC = SearchConditionFactory.createSearchConditionForStructure(strucClass);
			}
			else
			{
				tempSSC.getObjectGuid().setClassName(strucClass);
			}

			if (StructureObject.CAD_STRUCTURE_CLASS_NAME.equals(strucClass))
			{
				tempSSC.addResultField(StructureObject.FIELD_NAME_QUANTITY);
				tempSSC.addResultField(StructureObject.FIELD_NAME_BOMRELATED);
			}
			this.setEnd2UIToStructureSearchCondition(end2SearchCondition, tempSSC);
			dataRule = this.checkTemplateEnd2Type(viewObject, template, dataRule);
			List<StructureObject> resultList = ds.listObjectOfRelation(viewObjectGuid, template.getGuid(), dataRule, Constants.isSupervisor(isCheckAuth, this.stubService),
					sessionId, tempSSC);
			if (SetUtils.isNullList(resultList))
			{
				return null;
			}

			List<FoundationObject> retList = new ArrayList<FoundationObject>();
			this.decoratorResult(resultList, end2SearchCondition, tempSSC, sessionId, retList, null, true);
			return retList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	/**
	 * 将endUI上的字段加上前缀放入structureSearchCondition
	 * 
	 * @param end2SearchCondition
	 * @param structureSearchCondition
	 * @throws ServiceRequestException
	 */
	private void setEnd2UIToStructureSearchCondition(SearchCondition end2SearchCondition, SearchCondition structureSearchCondition) throws ServiceRequestException
	{
		List<String> end2FieldList = new ArrayList<String>();
		if (structureSearchCondition != null)
		{
			if (end2SearchCondition != null)
			{
				List<String> listField = end2SearchCondition.getResultFieldList();
				if (!SetUtils.isNullList(listField))
				{
					for (String name : listField)
					{
						end2FieldList.add(name);
					}
				}
				List<String> list = end2SearchCondition.listResultUINameList();
				ObjectGuid end2ObjectGuid = end2SearchCondition.getObjectGuid();
				if (!SetUtils.isNullList(list) && end2ObjectGuid != null && end2ObjectGuid.getClassName() != null)
				{
					for (String name : list)
					{
						List<UIField> flist = this.stubService.getEMM().listUIFieldByUIObject(end2ObjectGuid.getClassName(), name);
						if (!SetUtils.isNullList(flist))
						{
							for (UIField ff : flist)
							{
								if (!end2FieldList.contains(ff.getName()))
								{
									end2FieldList.add(ff.getName());
								}
							}
						}
					}
				}

				if (end2SearchCondition.getObjectGuid() != null && !StringUtils.isNullString(end2SearchCondition.getObjectGuid().getClassName()))
				{
					ClassInfo classInfo = this.stubService.getEMM().getClassByName(end2SearchCondition.getObjectGuid().getClassName());
					List<ModelInterfaceEnum> interfaceList = classInfo.getInterfaceList();
					if (!SetUtils.isNullList(interfaceList))
					{
						for (ModelInterfaceEnum interfaceEnum : interfaceList)
						{
							if (interfaceEnum == ModelInterfaceEnum.IFoundation)
							{
								continue;
							}
							List<ClassField> fieldList = this.stubService.getEMM().listClassFieldByInterface(interfaceEnum);
							if (!SetUtils.isNullList(fieldList))
							{
								for (ClassField field : fieldList)
								{
									if (!end2FieldList.contains(field.getName()))
									{
										end2FieldList.add(field.getName());
									}
								}
							}
						}
					}
				}
			}

			if (!SetUtils.isNullList(end2FieldList))
			{
				for (String fieldName : end2FieldList)
				{
					structureSearchCondition.addResultField(ViewObject.PREFIX_END2 + fieldName);
				}
			}
		}
	}

	/**
	 * 检查模板上end2Type
	 * 
	 * @param viewObject
	 * @param template
	 * @return
	 */
	private DataRule checkTemplateEnd2Type(FoundationObject objectData, RelationTemplateInfo template, DataRule dataRule)
	{
		if ("3".equalsIgnoreCase(template.getEnd2Type()) && objectData != null && template != null)
		{
			dataRule = new DataRule();
			if (SystemStatusEnum.RELEASE.equals(objectData.getStatus()) && objectData.getReleaseTime() != null)
			{
				dataRule.setSystemStatus(SystemStatusEnum.RELEASE);
				Date date = new Date(objectData.getReleaseTime().getTime() + (24 * 60 * 60 * 1000));
				dataRule.setLocateTime(date);
			}
		}
		return dataRule;
	}

	/**
	 * 装饰结果
	 * 
	 * @param foList
	 * @param end2SearchCondition
	 * @param soList
	 * @param structureSearchCondition
	 * @param retList
	 * @throws ServiceRequestException
	 * @throws DecorateException
	 */
	private void decoratorResult(List<StructureObject> resultList, SearchCondition end2SearchCondition, SearchCondition structureSearchCondition, String sessionId,
			List<FoundationObject> retList, List<StructureObject> sretList, boolean isFoundation) throws ServiceRequestException, DecorateException
	{
		if (!SetUtils.isNullList(resultList))
		{
			List<FoundationObject> foList = new LinkedList<FoundationObject>();
			List<StructureObject> soList = new LinkedList<StructureObject>();
			for (int i = 0; i < resultList.size(); i++)
			{
				StructureObject structureObject = resultList.get(i);
				ClassStub.decorateObjectGuid(structureObject.getEnd2ObjectGuid(), this.stubService);
				FoundationObject foundationObject = (FoundationObject) structureObject.get(ViewObject.PREFIX_END2);

				structureObject.put("FULLNAME$", foundationObject.getFullName());

				foList.add(foundationObject);
				soList.add(structureObject);
				structureObject.clear(ViewObject.PREFIX_END2);
			}

			for (int i = 0; i < foList.size(); i++)
			{
				FoundationObject foundationObject = foList.get(i);
				StructureObject structureObject = soList.get(i);
				if (isFoundation)
				{
					foundationObject.put(StructureObject.STRUCTURE_CLASS_NAME, structureObject);
					retList.add(foundationObject);
				}
				else
				{
					structureObject.put(StructureObject.END2_UI_OBJECT, foundationObject);
					sretList.add(structureObject);
				}
			}
		}
	}

	public List<TableOfMultiCondition> listTableOfMultiVariable(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(objectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + objectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
		searchConditionMap.put("RELEASETIME", ruleTime);
		if (ruleTime != null)
		{
			Date releaseTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
			searchConditionMap.put("RELEASETIME", releaseTime);
		}

		// 取得所有的M表数据
		SystemDataService sds = DataServer.getSystemDataService();
		List<DynamicOfMultiVariable> list = sds.query(DynamicOfMultiVariable.class, searchConditionMap, "listMultiVariable");
		if (!SetUtils.isNullList(list))
		{
			Map<Integer, TableOfMultiCondition> tempMap = new LinkedHashMap<Integer, TableOfMultiCondition>();
			int i = 1;
			for (DynamicOfMultiVariable multiVariable : list)
			{
				int sequence = multiVariable.getSequence();
				if (tempMap.get(sequence) == null)
				{
					TableOfMultiCondition condition = new TableOfMultiCondition();
					List<DynamicOfMultiVariable> listColumns = new ArrayList<DynamicOfMultiVariable>();
					listColumns.add(multiVariable);
					condition.setColumns(listColumns);
					List<TableOfDefineCondition> listCondition = new ArrayList<TableOfDefineCondition>();
					if (!StringUtils.isNullString(multiVariable.getConditionJson()))
					{
						listCondition = JsonUtils.listObjectByJsonStr(multiVariable.getConditionJson(), TableOfDefineCondition.class);
						if (!SetUtils.isNullList(listCondition))
						{
							condition.setConditions(listCondition);
							listCondition = new ArrayList<TableOfDefineCondition>();
							int j = 1;
							for (int g = 0; g < condition.getConditions().size(); g++)
							{
								Map<String, Object> cc = condition.getConditions().get(g);
								TableOfDefineCondition c1 = new TableOfDefineCondition();
								c1.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								c1.putAll(cc);
								c1.put("index", j);
								c1.putOriginalValueMap(cc);
								listCondition.add(c1);
								j++;
							}
							condition.setConditions(listCondition);
						}
					}
					condition.setHasGuid(true);
					condition.setIndex(i);
					condition.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
					tempMap.put(sequence, condition);
					i++;
				}
				else
				{
					TableOfMultiCondition condition = tempMap.get(sequence);
					if (condition.getColumns() != null)
					{
						condition.getColumns().add(multiVariable);
					}
				}
			}
			if (!SetUtils.isNullMap(tempMap))
			{
				List<TableOfMultiCondition> value = new ArrayList<TableOfMultiCondition>();
				value.addAll(tempMap.values());
				return value;
			}
		}

		return null;
	}
}
