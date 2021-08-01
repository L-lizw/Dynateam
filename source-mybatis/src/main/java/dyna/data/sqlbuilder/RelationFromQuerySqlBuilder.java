package dyna.data.sqlbuilder;

import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.DataRule;
import dyna.common.dto.Session;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.data.service.model.interfacemodel.InterfaceModelService;
import dyna.dbcommon.util.DSCommonUtil;

import java.util.*;

public class RelationFromQuerySqlBuilder
{
	public static DynamicSelectParamData buildStructureInfoSearchParamDataByEnd1(String sessionId, String templateId, String viewClassNameOrGuid, String struClassGuid,
			String end1Guid) throws ServiceNotFoundException, ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		DynamicSelectParamData paramData = new DynamicSelectParamData();

		String struTableName = commonService.getTableName(struClassGuid);
		String ViewTableName = commonService.getTableName(viewClassNameOrGuid);
		Map<String, String> relationTableMap = new HashMap<>();
		List<String> relationTableList = new ArrayList<>();
		List<SqlParamData> paramList = new ArrayList<>();

		paramData.setTableName(ViewTableName + " v");
		paramData.setJoinTableList(relationTableList);
		paramData.setJoinTableMap(relationTableMap);
		paramData.setWhereParamList(paramList);

		relationTableList.add(struTableName + " s ");
		relationTableMap.put(struTableName + " s ", "v.guid = s.viewfk");

		ClassObject classObject = classModelService.getClassObjectByGuid(struClassGuid);
		String struSelect = MutiTableSelectUtil.getSqlSelectByFeildList(sessionId, classObject.getName(), "s", null, classObject.getFieldList(), relationTableList,
				relationTableMap);
		paramData.setFieldSql(struSelect);

		String whereSql = "v.end1 = ? and v.templateid = ? ";
		paramList.add(new SqlParamData("end1", end1Guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		paramList.add(new SqlParamData("templateid", templateId, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		paramData.setWhereSql(whereSql);
		return paramData;
	}

	public static DynamicSelectParamData buildStructureInfoSearchParamDataByEnd1Master(String sessionId, String templateId, String viewClassNameOrGuid, String struClassGuid,
			String end1MasterGuid) throws ServiceNotFoundException, ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		DynamicSelectParamData paramData = new DynamicSelectParamData();

		String struTableName = commonService.getTableName(struClassGuid);
		String ViewTableName = commonService.getTableName(viewClassNameOrGuid);
		Map<String, String> relationTableMap = new HashMap<>();
		List<String> relationTableList = new ArrayList<>();
		List<SqlParamData> paramList = new ArrayList<>();

		paramData.setTableName(ViewTableName + " v");
		paramData.setJoinTableList(relationTableList);
		paramData.setJoinTableMap(relationTableMap);
		paramData.setWhereParamList(paramList);

		relationTableList.add(struTableName + " s ");
		relationTableMap.put(struTableName + " s ", "v.guid = s.viewfk");

		ClassObject classObject = classModelService.getClassObjectByGuid(struClassGuid);
		String struSelect = MutiTableSelectUtil.getSqlSelectByFeildList(sessionId, classObject.getName(), "s", null, classObject.getFieldList(), relationTableList,
				relationTableMap);
		paramData.setFieldSql(struSelect);

		String whereSql = "v.end1 = ? and v.templateid = ? ";
		paramList.add(new SqlParamData("end1$master", end1MasterGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		paramList.add(new SqlParamData("templateid", templateId, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		paramData.setWhereSql(whereSql);
		return paramData;
	}

	public static DynamicSelectParamData buildStructureInfoSearchParamData(String sessionId, String viewClassNameOrGuid, String struClassGuid, String viewGuid)
			throws ServiceNotFoundException, ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		DynamicSelectParamData paramData = new DynamicSelectParamData();

		String struTableName = commonService.getTableName(struClassGuid);

		Map<String, String> relationTableMap = new HashMap<>();
		List<String> relationTableList = new ArrayList<>();
		List<SqlParamData> paramList = new ArrayList<>();

		paramData.setTableName(struTableName + " s ");
		paramData.setJoinTableList(relationTableList);
		paramData.setJoinTableMap(relationTableMap);
		paramData.setWhereParamList(paramList);

		ClassObject classObject = classModelService.getClassObjectByGuid(struClassGuid);
		String struSelect = MutiTableSelectUtil.getSqlSelectByFeildList(sessionId, classObject.getName(), "s", null, classObject.getFieldList(), relationTableList,
				relationTableMap);
		paramData.setFieldSql(struSelect);

		String whereSql = "s.viewfk = ?";
		paramList.add(new SqlParamData("viewfk", viewGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		paramData.setWhereSql(whereSql);
		return paramData;
	}

	public static DynamicSelectParamData buildInstanceSearchParamData(String sessionId, ObjectGuid viewObjectGuid, RelationTemplate temp, SearchCondition searchCondition,
			ClassObject end2Class, List<String> end2FieldNameList, DataRule datarule) throws ServiceRequestException
	{
		try
		{
			DSCommonService commonService = DataServer.getDSCommonService();
			Session session = commonService.getSession(sessionId);
			DynamicSelectParamData paramData = new DynamicSelectParamData();

			String struClassname = temp.getStructureClassName();
			String struTableName = commonService.getTableName(struClassname);
			String end2tableName = commonService.getTableName(end2Class.getName());

			Map<String, String> relationTableMap = new HashMap<>();
			List<String> relationTableList = new ArrayList<>();
			List<SqlParamData> paramList = new ArrayList<>();

			paramData.setTableName(struTableName + " s ");
			paramData.setJoinTableList(relationTableList);
			paramData.setJoinTableMap(relationTableMap);
			paramData.setWhereParamList(paramList);

			relationTableList.add(end2tableName + " f ");
			if ("2".equalsIgnoreCase(temp.getEnd2Type()))
			{
				relationTableMap.put(end2tableName + " f ", "s.end2 = f.guid");
			}
			else
			{
				if (datarule == null || datarule.getSystemStatus() == SystemStatusEnum.WIP)
				{
					relationTableMap.put(end2tableName + " f ", "s.end2$master =f.masterfk and f.latestrevision like '%m%'");
				}
				else if (datarule.getLocateTime() == null)
				{
					relationTableMap.put(end2tableName + " f ",
							"s.end2$master =f.masterfk and ((f.releasetime is null and f.revisionidsequence = 0) or (f.latestrevision like '%r%'))");
				}
				else
				{
					relationTableMap.put(end2tableName + " f ", "s.end2$master =f.masterfk and ((f.releasetime is null and f.revisionidsequence = 0) "
							+ "or (f.releasetime is not null and f.releasetime <= ? and (f.nextrevisionrlstime is null or f.nextrevisionrlstime > ?)))");
					paramList.add(new SqlParamData("releasetime", datarule.getLocateTime(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATE)));
					paramList.add(new SqlParamData("releasetime", datarule.getLocateTime(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATE)));
				}
			}

			String struSelect = MutiTableSelectUtil.getFoundationSqlSelect("s", null, relationTableList, relationTableMap, searchCondition, sessionId, struClassname);
			String end2Select = getEnd2SqlSelect(sessionId, "f", end2Class, end2FieldNameList, relationTableList, relationTableMap);
			if (!StringUtils.isNullString(end2Select))
			{
				struSelect = struSelect + "," + end2Select;
			}
			paramData.setFieldSql(struSelect);
			String whereSql = MutiTableCriterionUtil.getSqlWhere(struClassname, "s", null, relationTableList, relationTableMap, searchCondition, sessionId, paramList);
			whereSql = whereSql + " and s.viewfk = ?";
			paramList.add(new SqlParamData("viewfk", viewObjectGuid.getGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			paramData.setWhereSql(whereSql);
			return paramData;
		}
		catch (ServiceNotFoundException e)
		{
			e.printStackTrace();
			throw new ServiceRequestException("", null, e);
		}
	}

	private static String getEnd2SqlSelect(String sessionId, String mainTableAlias, ClassObject end2Class, List<String> end2FieldNameList, List<String> relationTableList,
			Map<String, String> relationTableMap) throws ServiceNotFoundException, ServiceRequestException
	{
		InterfaceModelService interfaceModelService = DataServer.getInterfaceModelService();

		List<ClassField> fieldList = new ArrayList<>(end2FieldNameList.size());
		Set<String> fieldNameSet = new HashSet<>();
		// TODO duanll 这里没有赋值
		List<ModelInterfaceEnum> interfaceList = end2Class.getInterfaceList();
		if (interfaceList != null)
		{
			for (ModelInterfaceEnum modelInterfaceEnum : interfaceList)
			{
				List<ClassField> listClassFieldInInterface = interfaceModelService.listClassFieldOfInterface(modelInterfaceEnum);
				if (listClassFieldInInterface != null)
				{
					for (ClassField classField : listClassFieldInInterface)
					{
						if (!fieldNameSet.contains(classField.getName().toUpperCase()))
						{
							fieldNameSet.add(classField.getName().toUpperCase());
							fieldList.add(classField);
						}
					}
				}
			}
		}
		if (!SetUtils.isNullList(end2FieldNameList))
		{
			for (String fieldName : end2FieldNameList)
			{
				ClassField field = end2Class.getField(fieldName);
				if (field != null)
				{
					if (!fieldNameSet.contains(field.getName().toUpperCase()))
					{
						fieldNameSet.add(field.getName().toUpperCase());
						fieldList.add(field);
					}
				}
			}
		}
		if (!SetUtils.isNullList(fieldList))
		{
			return MutiTableSelectUtil.getSqlSelectByFeildList(sessionId, end2Class.getName(), mainTableAlias, ViewObject.PREFIX_END2, fieldList, relationTableList,
					relationTableMap);
		}
		return null;
	}

}
