package dyna.data.sqlbuilder;

import dyna.common.Criterion;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionImpl;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.Session;
import dyna.common.dto.aas.User;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.AdvancedQueryTypeEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.ppms.SearchCategoryEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.data.service.model.codemodel.CodeModelService;
import dyna.dbcommon.util.DSCommonUtil;

import java.util.*;

public class AdvanceQuerySqlBuilder
{
	public static DynamicSelectParamData buildInstanceSearchParamData(SearchConditionImpl condition, String sessionId, Map<String, String> classificationFieldMap)
			throws ServiceRequestException
	{
		ClassModelService classModelService = DataServer.getClassModelService();
		DSCommonService commonService = DataServer.getDSCommonService();
		CodeModelService codeModelService = DataServer.getCodeModelService();

		DynamicSelectParamData paramData = new DynamicSelectParamData();

		String className = condition.getObjectGuid().getClassName();
		String tableName = commonService.getTableName(className);

		Map<String, String> relationTableMap = new HashMap<>();
		List<String> relationTableList = new ArrayList<>();

		String classificationSelect = null;
		String otherSelect = null;
		String mainTableAlias = "f";

		// mian join
		if (AdvancedQueryTypeEnum.CLASSIFICATION.equals(condition.getSearchType()))
		{
			String classificationItemGuid = condition.getClassification();
			// 是否主分类，如果查询的是主分类，则一定是有分类数据的，当查询次分类时，分类数据可以不存在
			boolean isMaster = false;
			if (className != null)
			{
				ClassObject classObject = classModelService.getClassObject(className);
				isMaster = DataServer.getClassificationFeatureService().isMasterClassification(classObject.getGuid(), classificationItemGuid);
			}

			String masterClassification = codeModelService.getCodeItemByGuid(condition.getClassification()).getCodeGuid();
			CodeObject codeObject = codeModelService.getCodeObjectByGuid(masterClassification);
			if (!StringUtils.isNullString(codeObject.getBaseTableName()))
			{
				relationTableList.add(codeObject.getRevisionTableName() + " cf$");
				if (isMaster)
				{
					relationTableMap.put(codeObject.getRevisionTableName() + " cf$", "f.guid = cf$.foundationfk(+)");
				}
				else
				{
					relationTableMap.put(codeObject.getRevisionTableName() + " cf$", "f.guid = cf$.foundationfk");
				}
				classificationSelect = MutiTableSelectUtil.getClassificationSqlSelect("cf$", condition, sessionId, classificationFieldMap, className);
			}
		}
		if (condition.getSearchCategory() != null && (condition.getSearchCategory().equals(SearchCategoryEnum.WORKLOAD_PARTICIPATE_TASK)
				|| (condition.getSearchCategory().equals(SearchCategoryEnum.WORKLOAD_PARTICIPATE_WORKITEM))))
		{
			if (StringUtils.isGuid(condition.getUserGuidForPPM()))
			{
				otherSelect = ",tr.resourcerate as resourcerate$$";
				relationTableList.add(" ppm_taskresource tr ");
				relationTableMap.put(" ppm_taskresource tr ", mainTableAlias + ".GUID=" + "tr.taskguid(+)" + " and tr.userguid = '" + condition.getUserGuidForPPM() + "'");
			}
		}
		String selectString = MutiTableSelectUtil.getFoundationSqlSelect(mainTableAlias, null, relationTableList, relationTableMap, condition, sessionId, className);

		if (!StringUtils.isNullString(classificationSelect))
		{
			selectString = selectString + "," + classificationSelect;
		}
		if (!StringUtils.isNullString(otherSelect))
		{
			selectString = selectString + otherSelect;
		}
		List<SqlParamData> paramList = new LinkedList<>();
		String sqlWhere = buildSqlWhere(className, mainTableAlias, "cf$", relationTableList, relationTableMap, condition, sessionId, paramList);

		// List<SqlParamData> paramList = buildSqlParamDataList(condition, commonService.getSession(sessionId));

		String orderByString = CommonSqlBuilder.getSqlOrderBy(mainTableAlias, "cf$", condition, false);

		paramData.setTableName(tableName + " f");
		paramData.setCurrentPage(condition.getPageNum());
		paramData.setRowsPerPage(condition.getPageSize());
		paramData.setFieldSql(selectString);
		paramData.setWhereSql(sqlWhere);
		paramData.setOrderSql(orderByString);
		paramData.setJoinTableList(relationTableList);
		paramData.setJoinTableMap(relationTableMap);
		paramData.setWhereParamList(paramList);

		return paramData;
	}

	private static String buildSqlWhere(String className, String mainTableAlias, String classiFicationAlias, List<String> relationTableList, Map<String, String> relationTableMap,
			SearchConditionImpl searchCondition, String sessionId, List<SqlParamData> paramList) throws ServiceRequestException
	{
		DSCommonService commonService = DataServer.getDSCommonService();

		String conditionWhere = MutiTableCriterionUtil.getSqlWhere(className, mainTableAlias, classiFicationAlias, relationTableList, relationTableMap, searchCondition, sessionId,
				paramList);
		StringBuffer whereClauseSb = new StringBuffer();
		whereClauseSb.append(conditionWhere);
		// 只查废弃的记录，只查没被废弃的记录，没有查所有记录的情况
		if (!searchCondition.isIncludeOBS())
		{
			whereClauseSb.append(" AND ").append("(" + mainTableAlias + ".obsoletetime is null or " + mainTableAlias + ".obsoletetime > ? )");
			paramList.add(new SqlParamData("obsoletetime", new Date(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));
		}

		// 是否只按照所属组查询
		Criterion criterion = new Criterion(null, null, null);
		if (searchCondition.isOwnerGroupOnly())
		{
			if (whereClauseSb.length() > 0)
			{
				whereClauseSb.append(" AND ");
			}
			criterion.setOperateSignEnum(OperateSignEnum.EQUALS);
			DataServicUtil.transOperateGroup(whereClauseSb, criterion, commonService.getSession(sessionId).getLoginGroupGuid(), mainTableAlias + ".OWNERGROUP", paramList);
		}

		// 是否只按照所属用户查询
		if (searchCondition.isOwnerOnly())
		{
			if (whereClauseSb.length() > 0)
			{
				whereClauseSb.append(" AND ");
			}
			criterion.setOperateSignEnum(OperateSignEnum.EQUALS);
			DataServicUtil.transOperateUser(whereClauseSb, criterion, commonService.getSession(sessionId).getUserGuid(), mainTableAlias + ".OWNERUSER", paramList);
		}
		// 是否只查我检出的数据
		if (searchCondition.isCheckOutOnly())
		{
			if (whereClauseSb.length() > 0)
			{
				whereClauseSb.append(" AND ");
			}
			criterion.setOperateSignEnum(OperateSignEnum.EQUALS);
			DataServicUtil.transOperateUser(whereClauseSb, criterion, commonService.getSession(sessionId).getUserGuid(), mainTableAlias + ".CHECKOUTUSER", paramList);
		}

		String wherePMSql = addPMWhereSql(mainTableAlias, searchCondition, sessionId, paramList);
		if (!StringUtils.isNull(wherePMSql))
		{
			if (wherePMSql.length() == 0)
			{
				return whereClauseSb.toString();
			}
			else
			{
				return whereClauseSb.toString() + " and " + wherePMSql;
			}
		}

		return null;
	}

	/*
	 * private static List<SqlParamData> buildSqlParamDataList(SearchCondition searchCondition, Session session) throws
	 * ServiceRequestException
	 * {
	 * List<SqlParamData> list = MutiTableCriterionUtil.buildCriterionParamDataList(searchCondition, session);
	 * // 是否只按照所属组查询
	 * Criterion criterion = new Criterion(null, null, null);
	 * if (searchCondition.isOwnerGroupOnly())
	 * {
	 * SqlParamData paramData = new SqlParamData();
	 * paramData.setParamName(criterion.getKey());
	 * paramData.setJavaType(String.class);
	 * paramData.setVal(session.getLoginGroupGuid());
	 * 
	 * list.add(paramData);
	 * }
	 * 
	 * // 是否只按照所属用户查询
	 * if (searchCondition.isOwnerOnly())
	 * {
	 * SqlParamData paramData = new SqlParamData();
	 * paramData.setParamName(criterion.getKey());
	 * paramData.setJavaType(String.class);
	 * paramData.setVal(session.getUserGuid());
	 * 
	 * list.add(paramData);
	 * }
	 * // 是否只查我检出的数据
	 * if (searchCondition.isCheckOutOnly())
	 * {
	 * SqlParamData paramData = new SqlParamData();
	 * paramData.setParamName(criterion.getKey());
	 * paramData.setJavaType(String.class);
	 * paramData.setVal(session.getUserGuid());
	 * 
	 * list.add(paramData);
	 * }
	 * return list;
	 * }
	 */
	private static String addPMWhereSql(String mainTableAlias, SearchCondition condition, String sessionId, List<SqlParamData> paramList) throws ServiceRequestException
	{
		DSCommonService commonService = DataServer.getDSCommonService();
		CodeModelService codeModelService = DataServer.getCodeModelService();

		Session session = commonService.getSession(sessionId);
		String userGuid = session.getUserGuid();

		if (PPMSQueryUtil.isPMQueryEnum(condition))
		{
			SearchCategoryEnum sce = condition.getSearchCategory();
			String realUserGuid = userGuid;
			if (StringUtils.isGuid(condition.getUserGuidForPPM()))
			{
				realUserGuid = condition.getUserGuidForPPM();
			}

			StringBuilder sqlWherePM = new StringBuilder();
			if (sce == SearchCategoryEnum.PROJ_ALL_UP_TO_NOW || sce == SearchCategoryEnum.PROJ_INIT || sce == SearchCategoryEnum.PROJ_IN_PROCESS
					|| sce == SearchCategoryEnum.PROJ_COMPLETE || sce == SearchCategoryEnum.PROJ_SUSPEND || sce == SearchCategoryEnum.PROJ_STOP
					|| sce == SearchCategoryEnum.PROJ_OBSERVE || sce == SearchCategoryEnum.PROJ_DEFAULT)
			{
				User u = PPMSQueryUtil.isSysObserver(userGuid);
				if (u == null)
				{
					PPMSQueryUtil.changeToPM(mainTableAlias, sce, sqlWherePM, realUserGuid, condition.getManageGroup(), codeModelService, paramList);
				}
			}
			else
			{
				PPMSQueryUtil.changeToPM(mainTableAlias, sce, sqlWherePM, realUserGuid, condition.getManageGroup(), codeModelService, paramList);
			}
			return sqlWherePM.toString();
		}
		return "";
	}

}
