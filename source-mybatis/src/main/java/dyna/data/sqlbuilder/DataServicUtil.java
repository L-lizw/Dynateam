/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TransOperater
 * JiangHL 2010-9-9
 */
package dyna.data.sqlbuilder;

import dyna.common.Criterion;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.dto.TreeDataRelation;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.StringUtils;
import dyna.data.service.model.classmodel.ClassModelServiceImpl;
import dyna.data.service.model.codemodel.CodeModelService;
import dyna.dbcommon.util.DSCommonUtil;
import dyna.dbcommon.util.UpperCaseMap;

import java.util.Date;
import java.util.List;

/**
 * @author JiangHL
 */
public class DataServicUtil
{
	public final static String	CRT	= " (status ='CRT') ";
	public final static String	WIP	= " (status ='WIP') ";
	public final static String	PRE	= " (status ='PRE') ";
	public final static String	RLS	= " (status ='RLS') ";
	public final static String	ECP	= " (status ='ECP') ";
	public final static String	OBS	= " (status ='OBS') ";

	/**
	 * @param whereClauseSb
	 * @param fieldType
	 * @param criterionValue
	 * @param paramList
	 */
	private static void changeToDateHaveElse(StringBuffer whereClauseSb, FieldTypeEnum fieldType, Object criterionValue, String Operate, boolean caseSensitive, String column,
			List<SqlParamData> paramList)
	{
		if (fieldType.equals(FieldTypeEnum.DATE))
		{
			whereClauseSb.append(column);
			whereClauseSb.append(Operate + "?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(fieldType)));
		}
		else if (fieldType.equals(FieldTypeEnum.DATETIME))
		{
			whereClauseSb.append(column);
			whereClauseSb.append(Operate + "?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(fieldType)));
		}
		else
		{
			if (!caseSensitive)
			{
				if ("=".equals(Operate))
				{
					if (StringUtils.isGuid(criterionValue.toString()))
					{
						whereClauseSb.append(column + Operate + "?");
					}
					else
					{
						whereClauseSb.append("upper(" + column + ")" + Operate + "upper(?)");
					}
				}
				else
				{
					whereClauseSb.append("(" + column + " is null or upper(" + column + ")" + Operate + "upper(?))");
				}
			}
			else
			{
				if ("=".equals(Operate))
				{
					whereClauseSb.append(column + Operate + "?");
				}
				else
				{
					whereClauseSb.append("(" + column + " is null or " + column + Operate + "?)");
				}
			}
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(fieldType)));
		}
	}

	/**
	 * @param whereClauseSb
	 * @param fieldType
	 * @param criterionValue
	 * @param caseSensitive
	 * @param paramList
	 */
	public static void changeToStatus(StringBuffer whereClauseSb, FieldTypeEnum fieldType, Object criterionValue, boolean isEquals, boolean caseSensitive,
			List<SqlParamData> paramList)
	{
		whereClauseSb.delete(0, whereClauseSb.length());

		if (String.valueOf(criterionValue).equalsIgnoreCase(SystemStatusEnum.WIP.toString()))
		{
			// 是WIP状态
			if (isEquals)
			{
				whereClauseSb.append("(" + DataServicUtil.WIP + ")");
			}
			else
			{
				whereClauseSb.append(
						"(" + DataServicUtil.CRT + " or " + DataServicUtil.PRE + " or " + DataServicUtil.RLS + " or " + DataServicUtil.ECP + " or " + DataServicUtil.OBS + ")");
			}

		}
		else if (String.valueOf(criterionValue).equalsIgnoreCase(SystemStatusEnum.PRE.toString()))
		{
			if (isEquals)
			{
				whereClauseSb.append("(" + DataServicUtil.PRE + ")");
			}
			else
			{
				whereClauseSb.append(
						"(" + DataServicUtil.CRT + " or " + DataServicUtil.WIP + " or " + DataServicUtil.RLS + " or " + DataServicUtil.ECP + " or " + DataServicUtil.WIP + ")");
			}

		}
		else if (String.valueOf(criterionValue).equalsIgnoreCase(SystemStatusEnum.RELEASE.toString()))
		{
			if (isEquals)
			{
				whereClauseSb.append("(" + DataServicUtil.RLS + ")");
			}
			else
			{
				whereClauseSb.append(
						"(" + DataServicUtil.CRT + " or " + DataServicUtil.PRE + " or " + DataServicUtil.OBS + " or " + DataServicUtil.ECP + " or " + DataServicUtil.WIP + ")");
			}

		}
		else if (String.valueOf(criterionValue).equalsIgnoreCase(SystemStatusEnum.ECP.toString()))
		{
			if (isEquals)
			{
				whereClauseSb.append("(" + DataServicUtil.ECP + ")");
			}
			else
			{
				whereClauseSb.append(
						"(" + DataServicUtil.CRT + " or " + DataServicUtil.PRE + " or " + DataServicUtil.OBS + " or " + DataServicUtil.RLS + " or " + DataServicUtil.WIP + ")");
			}

		}
		else if (String.valueOf(criterionValue).equalsIgnoreCase(SystemStatusEnum.OBSOLETE.toString()))
		{
			if (isEquals)
			{
				whereClauseSb.append("(" + DataServicUtil.OBS + ")");
			}
			else
			{
				whereClauseSb.append("(obsoletetime is null or obsoletetime > ? )");
				paramList.add(new SqlParamData("obsoletetime", new Date(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));
			}
		}

	}

	/**
	 * 查询管理的ec
	 *
	 * @param whereClauseSb
	 */
	public static void changeToECAdmin(UpperCaseMap classBaseTableMap, ClassModelServiceImpl classModelServiceImpl, StringBuffer whereClauseSb, Criterion criterion,
			String userGuid)
	{
	}

	// 发起的EC
	public static void changeToECInitiate(UpperCaseMap classBaseTableMap, ClassModelServiceImpl classModelServiceImpl, StringBuffer whereClauseSb, Criterion criterion,
			String userGuid)
	{
	}

	/**
	 * 按进度查询ec
	 *
	 * @param whereClauseSb
	 */
	public static void changeToECSchedule(String userGuid, UpperCaseMap classBaseTableMap, ClassModelServiceImpl classModelServiceImpl, StringBuffer whereClauseSb,
			Criterion criterion)
	{
	}

	public static String getGuidByCodeName(String codeName, String codeMasterName, CodeModelService codeService)
	{
		CodeItem codeItem = codeService.getCodeItem(codeMasterName, codeName);
		if (codeItem != null)
		{
			return codeItem.getGuid();
		}
		return null;
	}

	/**
	 * 转换GUID
	 *
	 * @param whereClauseSb
	 * @param fieldType
	 * @param criterion
	 * @param criterionValue
	 * @param caseSensitive
	 *            是否大小写敏感
	 * @param paramList
	 * @return
	 */
	public static void transOperateGuid(StringBuffer whereClauseSb, FieldTypeEnum fieldType, Criterion criterion, String column, Object criterionValue, boolean caseSensitive,
			List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.EQUALS) || operateSign.equals(OperateSignEnum.TRUE) || operateSign.equals(OperateSignEnum.YES))
		{
			whereClauseSb.delete(0, whereClauseSb.length());
			whereClauseSb.append(" (" + column + " = ?)");
		}
		else
		{
			whereClauseSb.delete(0, whereClauseSb.length());
			whereClauseSb.append(column + " != ?");
		}
		paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(fieldType)));
	}

	public static void transOperateMasterfk(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, boolean caseSensitive, List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.EQUALS) || operateSign.equals(OperateSignEnum.TRUE) || operateSign.equals(OperateSignEnum.YES))
		{
			whereClauseSb.delete(0, whereClauseSb.length());
			whereClauseSb.append(" (MASTERFK = ?)");
		}
		else
		{
			whereClauseSb.delete(0, whereClauseSb.length());
			whereClauseSb.append("MASTERFK != ?");
		}
		paramList.add(new SqlParamData("MASTERFK", criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
	}

	/**
	 * @param whereClauseSb
	 * @param criterion
	 * @param criterionValue
	 * @param column
	 * @param paramList
	 */
	public static void transOperateMultCode(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, String column, List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column + " is null");
		}
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column + " is not null");
		}
		else if (operateSign.equals(OperateSignEnum.NO))
		{
			whereClauseSb.append("(" + column + " is null or " + column + " not like ?)");
			paramList.add(new SqlParamData(column, "%" + criterionValue + "%", DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
		else if (operateSign.equals(OperateSignEnum.YES))
		{
			whereClauseSb.append(column + " like ? ");
			paramList.add(new SqlParamData(column, "%" + criterionValue + "%", DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
	}

	/**
	 * @param whereClauseSb
	 * @param criterion
	 * @param criterionValue
	 * @param column
	 * @param paramList
	 */
	public static void transOperateUser(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, String column, List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column + " is null");
		}
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column + " is not null");
		}
		else
		{
			if (operateSign.equals(OperateSignEnum.NO))
			{
				whereClauseSb.append("(" + column + " is null or " + column + " != ?')");
			}
			else
			{
				whereClauseSb.append(column + " = ?");
			}
			paramList.add(new SqlParamData("loginUser", criterionValue, String.class));
		}
	}

	/**
	 * @param whereClauseSb
	 * @param criterion
	 * @param criterionValue
	 * @param column
	 * @param paramList
	 */
	public static void transOperateGroup(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, String column, List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column + " is null");
		}
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column + " is not null");
		}
		else
		{
			if (operateSign.equals(OperateSignEnum.NO))
			{
				whereClauseSb.append("(" + column + " is null or " + column + " not in ");
				whereClauseSb.append("(select x.guid from sa_group_relation where x.guid=?)");
			}
			else
			{
				whereClauseSb.append(column + " in ");
				whereClauseSb.append("(select x.guid from sa_group_relation where x.guid=?)");
			}
			paramList.add(new SqlParamData("loginGroup", criterionValue, String.class));
		}
	}

	/**
	 * @param whereClauseSb
	 * @param criterion
	 * @param criterionValue
	 * @param column
	 * @param paramList
	 */
	public static void transOperateObject(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, String column, List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column + " is null");
		}
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column + " is not null");
		}
		else
		{
			ObjectGuid objectGuid = (ObjectGuid) criterionValue;

			if (operateSign.equals(OperateSignEnum.NO))
			{
				if (objectGuid.isMaster())
				{
					whereClauseSb.append("(" + column + "$master is null or " + column + "$master != ?)");
					paramList.add(new SqlParamData(column, objectGuid.getMasterGuid(), String.class));
				}
				else
				{
					whereClauseSb.append("(" + column + " is null or " + column + " != ?)");
					paramList.add(new SqlParamData(column, objectGuid.getGuid(), String.class));
				}
			}
			else
			{
				if (objectGuid.isMaster())
				{
					whereClauseSb.append(column + "$master = ?");
					paramList.add(new SqlParamData(column, objectGuid.getMasterGuid(), String.class));
				}
				else
				{
					whereClauseSb.append(column + " = ?");
					paramList.add(new SqlParamData(column, objectGuid.getGuid(), String.class));
				}
			}
		}
	}

	/**
	 * @param whereClauseSb
	 * @param criterion
	 * @param criterionValue
	 * @param column
	 * @param paramList
	 */
	public static void transOperateClassification(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, String column, List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column + " is null");
		}
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column + " is not null");
		}
		else if (operateSign.equals(OperateSignEnum.NO))
		{
			whereClauseSb.append("(");
			whereClauseSb.append(column);
			whereClauseSb.append(" is null or not exists (select 1 from (");
			whereClauseSb.append(" select m.subdataguid guid from ma_treedata_relation m where m.datatype = '").append(TreeDataRelation.DATATYPE_CODEITEM);
			whereClauseSb.append("' and m.dataguid = ?");
			whereClauseSb.append(") o where o.guid = ").append(column).append("))");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
		else if (operateSign.equals(OperateSignEnum.YES))
		{
			whereClauseSb.append("exists (select 1 from (");
			whereClauseSb.append("select m.subdataguid guid from ma_treedata_relation m where m.datatype = '").append(TreeDataRelation.DATATYPE_CODEITEM);
			whereClauseSb.append("' and m.dataguid = ? ");
			whereClauseSb.append(") o where o.guid = ").append(column).append(")");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
	}

	/**
	 * @param whereClauseSb
	 * @param column
	 * @param isMaster
	 *            是否主分类
	 * @param paramList
	 */
	public static void transOperateClassification(StringBuffer whereClauseSb, OperateSignEnum operateSign, String classificationItemGuid, String column, boolean isMaster,
			List<SqlParamData> paramList)
	{
		if (isMaster)
		{
			whereClauseSb.append("exists ( ");
			whereClauseSb.append("select 1 from ( ");
			whereClauseSb.append("select m.subdataguid guid from ma_treedata_relation m where m.datatype = '").append(TreeDataRelation.DATATYPE_CODEITEM);
			whereClauseSb.append("' and m.dataguid = ? ");
			whereClauseSb.append(") o ");
			whereClauseSb.append("where o.guid = ").append(column).append(") ");
		}
		else
		{
			whereClauseSb.append("exists (select 1 from (");
			whereClauseSb.append("select m.subdataguid guid from ma_treedata_relation m where m.datatype = '").append(TreeDataRelation.DATATYPE_CODEITEM);
			whereClauseSb.append("' and m.dataguid = ? ");
			whereClauseSb.append(" ) o ");
			whereClauseSb.append("where o.guid = cf$.classificationitemguid)");
		}
		paramList.add(new SqlParamData(column, classificationItemGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
	}

	public static void transOperateCode(StringBuffer whereClauseSb, Criterion criterion, Object criterionValue, String column, CodeModelService codeModelService,
			List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column + " is null");
		}
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column + " is not null");
		}

		else if (operateSign.equals(OperateSignEnum.NO))
		{
			whereClauseSb.append("(").append(column);
			whereClauseSb.append(" is null or not exists (select 1 from (");
			whereClauseSb.append("select m.subdataguid guid from ma_treedata_relation m where m.datatype = '").append(TreeDataRelation.DATATYPE_CODEITEM);
			whereClauseSb.append("' and m.dataguid = ? ");
			whereClauseSb.append(") o where o.guid = ").append(column).append("))");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
		else if (operateSign.equals(OperateSignEnum.YES))
		{
			whereClauseSb.append("exists (select 1 from (");
			whereClauseSb.append("select m.subdataguid guid from ma_treedata_relation m where m.datatype = '").append(TreeDataRelation.DATATYPE_CODEITEM);
			whereClauseSb.append("' and m.dataguid = ? ");
			whereClauseSb.append(") o where o.guid = ").append(column).append(")");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
		else if (operateSign.equals(OperateSignEnum.NOTEQUALS))
		{
			whereClauseSb.append(column + " <> ?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
		else if (operateSign.equals(OperateSignEnum.EQUALS))
		{
			whereClauseSb.append(column + " = ?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.CODE)));
		}
	}

	/**
	 * 转换状态
	 *
	 * @param whereClauseSb
	 * @param fieldType
	 * @param criterion
	 * @param criterionValue
	 * @param caseSensitive
	 *            是否大小写敏感
	 * @param paramList
	 * @return
	 */
	public static void transOperateStatus(StringBuffer whereClauseSb, FieldTypeEnum fieldType, Criterion criterion, Object criterionValue, boolean caseSensitive,
			List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign.equals(OperateSignEnum.EQUALS) || operateSign.equals(OperateSignEnum.TRUE) || operateSign.equals(OperateSignEnum.YES))
		{
			changeToStatus(whereClauseSb, fieldType, criterionValue, true, caseSensitive, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.NOTEQUALS) || operateSign.equals(OperateSignEnum.FALSE) || operateSign.equals(OperateSignEnum.NO))
		{
			changeToStatus(whereClauseSb, fieldType, criterionValue, false, caseSensitive, paramList);
		}
	}

	/**
	 * 转换enum操作符，并拼接SQL
	 *
	 * @param whereClauseSb
	 * @param fieldType
	 * @param criterion
	 * @param criterionValue
	 * @param caseSensitive
	 *            是否大小写敏感
	 * @param paramList
	 * @return
	 */
	public static void transOperate(StringBuffer whereClauseSb, FieldTypeEnum fieldType, Criterion criterion, Object criterionValue, boolean caseSensitive, String column,
			List<SqlParamData> paramList)
	{
		OperateSignEnum operateSign = criterion.getOperateSignEnum();
		/*
		 * String criterionValueEscape = "";
		 * 
		 * if (fieldType.equals(FieldTypeEnum.STRING))
		 * {
		 * if (criterion.getOperateSignEnum() != OperateSignEnum.EQUALS && (criterionValue.toString().contains("%") ||
		 * criterionValue.toString().contains("_")))
		 * {
		 * criterionValue = criterionValue.toString().replace(DSAbstractServiceStub.ESCAPE_CHAR,
		 * DSAbstractServiceStub.ESCAPE_CHAR + DSAbstractServiceStub.ESCAPE_CHAR)
		 * .replace("%", DSAbstractServiceStub.ESCAPE_CHAR + "%").replace("_", DSAbstractServiceStub.ESCAPE_CHAR + "_");
		 * criterionValueEscape = " ESCAPE '" + "/" + "'";
		 * }
		 * }
		 */
		if (fieldType.equals(FieldTypeEnum.STRING) && criterionValue.toString().contains("*") && !operateSign.equals(OperateSignEnum.EQUALS)
				&& !operateSign.equals(OperateSignEnum.YES))
		{
			criterionValue = criterionValue.toString().replace("*", "%");
			criterionValue = criterionValue.toString().replace("?", "_");
		}
		/*
		 * if (fieldType.equals(FieldTypeEnum.STRING) && criterionValue.toString().contains("?") &&
		 * !operateSign.equals(OperateSignEnum.EQUALS)
		 * && !operateSign.equals(OperateSignEnum.YES))
		 * {
		 * criterionValue = criterionValue.toString().replace("?", "_");
		 * }
		 */
		// STRING
		if (operateSign.equals(OperateSignEnum.STARTWITH) && (fieldType.equals(FieldTypeEnum.STRING)))
		{
			if (!caseSensitive)
			{
				whereClauseSb.append("upper(" + column + ") like upper(? )");
			}
			else
			{
				whereClauseSb.append(column + " like ? ");
			}
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		}
		else if (operateSign.equals(OperateSignEnum.ENDWITH) && (fieldType.equals(FieldTypeEnum.STRING)))
		{
			if (!caseSensitive)
			{
				whereClauseSb.append("upper(" + column + ") like upper(?)");
			}
			else
			{
				whereClauseSb.append(column + " like  ? ");
			}
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		}
		else if (operateSign.equals(OperateSignEnum.CONTAIN) && (fieldType.equals(FieldTypeEnum.STRING)))
		{
			if (!caseSensitive)
			{
				whereClauseSb.append("upper(" + column + ") like upper(?)");
			}
			else
			{
				whereClauseSb.append(column + " like ?");
			}
			paramList.add(new SqlParamData(column, "%" + criterionValue + "%", DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		}
		else if (operateSign.equals(OperateSignEnum.NOTCONTAIN) && (fieldType.equals(FieldTypeEnum.STRING)))
		{
			if (!caseSensitive)
			{
				whereClauseSb.append("(" + column + " is null or " + "upper(" + column + ") not like upper(?)" + ")");
			}
			else
			{
				whereClauseSb.append("(" + column + " is null or " + column + " not like ?" + ")");
			}
			paramList.add(new SqlParamData(column, "%" + criterionValue + "%", DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		}
		else if (operateSign.equals(OperateSignEnum.LIKE_REGEX) && (fieldType.equals(FieldTypeEnum.STRING)))
		{
			if (!caseSensitive)
			{
				whereClauseSb.append("upper(" + column + ") like upper(?)");
			}
			else
			{
				whereClauseSb.append(column + " like ?");
			}
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		}

		// FLOAT INTEGER
		else if (operateSign.equals(OperateSignEnum.BIGGER) && (fieldType.equals(FieldTypeEnum.FLOAT) || fieldType.equals(FieldTypeEnum.INTEGER)))
		{
			whereClauseSb.append(column).append(">?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.INTEGER)));
		}
		else if (operateSign.equals(OperateSignEnum.BIGGEROREQUAL) && (fieldType.equals(FieldTypeEnum.FLOAT) || fieldType.equals(FieldTypeEnum.INTEGER)))
		{
			whereClauseSb.append(column).append(">=?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.INTEGER)));
		}
		else if (operateSign.equals(OperateSignEnum.SMALLER) && (fieldType.equals(FieldTypeEnum.FLOAT) || fieldType.equals(FieldTypeEnum.INTEGER)))
		{
			whereClauseSb.append(column).append("<?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.INTEGER)));
		}
		else if (operateSign.equals(OperateSignEnum.SMALLEROREQUAL) && (fieldType.equals(FieldTypeEnum.FLOAT) || fieldType.equals(FieldTypeEnum.INTEGER)))
		{
			whereClauseSb.append(column).append("<=?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.INTEGER)));
		}
		else if (operateSign.equals(OperateSignEnum.EQUALS) && (fieldType.equals(FieldTypeEnum.FLOAT) || fieldType.equals(FieldTypeEnum.INTEGER)))
		{
			whereClauseSb.append(column).append("=?");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.INTEGER)));
		}
		else if (operateSign.equals(OperateSignEnum.NOTEQUALS) && (fieldType.equals(FieldTypeEnum.FLOAT) || fieldType.equals(FieldTypeEnum.INTEGER)))
		{
			whereClauseSb.append("(");
			whereClauseSb.append(column).append(" is null ");
			whereClauseSb.append(" or ");
			whereClauseSb.append(column).append("<>?");
			whereClauseSb.append(")");
			paramList.add(new SqlParamData(column, criterionValue, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.INTEGER)));
		}

		// DATE DATETIME
		else if (operateSign.equals(OperateSignEnum.EARLIER) && (fieldType.equals(FieldTypeEnum.DATE) || fieldType.equals(FieldTypeEnum.DATETIME)))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, "<", caseSensitive, column, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.LATER) && (fieldType.equals(FieldTypeEnum.DATE) || fieldType.equals(FieldTypeEnum.DATETIME)))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, ">", caseSensitive, column, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.NOTEARLIER) && (fieldType.equals(FieldTypeEnum.DATE) || fieldType.equals(FieldTypeEnum.DATETIME)))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, ">=", caseSensitive, column, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.NOTLATER) && (fieldType.equals(FieldTypeEnum.DATE) || fieldType.equals(FieldTypeEnum.DATETIME)))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, "<=", caseSensitive, column, paramList);
		}

		// BOOLEAN STRING
		else if (operateSign.equals(OperateSignEnum.FALSE) && (fieldType.equals(FieldTypeEnum.BOOLEAN) || fieldType.equals(FieldTypeEnum.STRING)))
		{
			whereClauseSb.append(column).append("='N'");
		}
		else if (operateSign.equals(OperateSignEnum.TRUE) && (fieldType.equals(FieldTypeEnum.BOOLEAN) || fieldType.equals(FieldTypeEnum.STRING)))
		{
			whereClauseSb.append(column).append("='Y'");
		}

		else if (operateSign.equals(OperateSignEnum.YES))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, "=", caseSensitive, column, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.NO))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, "!=", caseSensitive, column, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.NOTEQUALS))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, "!=", caseSensitive, column, paramList);
		}
		else if (operateSign.equals(OperateSignEnum.EQUALS))
		{
			changeToDateHaveElse(whereClauseSb, fieldType, criterionValue, "=", caseSensitive, column, paramList);
		}

		// others
		else if (operateSign.equals(OperateSignEnum.NOTNULL))
		{
			whereClauseSb.append(column).append(" IS NOT NULL");
		}
		else if (operateSign.equals(OperateSignEnum.HAVE))
		{
			whereClauseSb.append(column).append(" IS NOT NULL");
		}
		else if (operateSign.equals(OperateSignEnum.NOTHAVE))
		{
			whereClauseSb.append(column).append(" IS NULL");
		}
		else if (operateSign.equals(OperateSignEnum.ISNULL))
		{
			whereClauseSb.append(column).append(" IS NULL");
		}
		else
		{
			whereClauseSb.append(column).append(" IS NULL");
		}
	}
}
