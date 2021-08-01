package dyna.data.sqlbuilder;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.dbcommon.util.DSCommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassInstanceGetSqlBuilder
{
	public static DynamicSelectParamData buildInstanceSearchParamData(ObjectGuid objectGuid, String sessionId) throws ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();

		DynamicSelectParamData paramData = new DynamicSelectParamData();
		String className = objectGuid.getClassName();
		ClassObject classInfo;
		if (StringUtils.isNull(className))
		{
			classInfo = classModelService.getClassObjectByGuid(objectGuid.getClassGuid());
			className = classInfo.getName();
		}
		else
		{
			classInfo = classModelService.getClassObject(className);
		}
		String tableName = commonService.getTableName(classInfo.getName());

		Map<String, String> relationTableMap = new HashMap<>();
		List<String> relationTableList = new ArrayList<>();
		String selectSql = MutiTableSelectUtil.getSqlSelectByFeildList(sessionId, className, "f", null, classInfo.getFieldList(), relationTableList, relationTableMap);
		String whereSql = "f.guid=?";
		List<SqlParamData> paramList = new ArrayList<>();
		paramList.add(new SqlParamData("guid", objectGuid.getGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		paramData.setTableName(tableName + " f");
		paramData.setFieldSql(selectSql);
		paramData.setWhereSql(whereSql);
		paramData.setWhereParamList(paramList);
		paramData.setJoinTableList(relationTableList);
		paramData.setJoinTableMap(relationTableMap);
		return paramData;
	}
}
