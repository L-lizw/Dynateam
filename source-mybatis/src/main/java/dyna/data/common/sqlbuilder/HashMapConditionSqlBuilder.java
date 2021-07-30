package dyna.data.common.sqlbuilder;

import dyna.common.Criterion;
import dyna.common.SearchCondition;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.NumberUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HashMapConditionSqlBuilder
{
	public static DynamicSelectParamData buildSelectParamData(String tableName, SearchCondition searchCondition)
	{
		DynamicSelectParamData selectParamData = new DynamicSelectParamData();
		selectParamData.setTableName(tableName);
		if (searchCondition != null)
		{
			selectParamData.setCurrentPage(searchCondition.getPageNum());
			selectParamData.setRowsPerPage(searchCondition.getPageSize());
			selectParamData.setFieldSql(getFieldSql(searchCondition));
			selectParamData.setWhereSql(getSqlWhere(searchCondition));
			selectParamData.setWhereParamList(listSqlParamData(searchCondition));
		}

		return selectParamData;
	}

	protected static String getFieldSql(SearchCondition searchCondition)
	{
		StringBuilder builder = new StringBuilder();
		if (!SetUtils.isNullList(searchCondition.getResultFieldList()))
		{
			searchCondition.getResultFieldList().forEach(field -> {
				if (builder.length() > 0)
				{
					builder.append(",");
				}
				builder.append(field);
			});
			return builder.toString();
		}
		else
		{
			return "GUID";
		}
	}

	/**
	 * 根据searchCondition 解析出where条件
	 *
	 * @param searchCondition
	 * @return
	 */
	protected static String getSqlWhere(SearchCondition searchCondition)
	{
		if (searchCondition == null)
		{
			return null;
		}

		StringBuffer whereClauseSb = new StringBuffer();

		boolean isFirstCriterion = true;
		String preKey = null;
		whereClauseSb.append("(");
		for (int i = 0, j = (searchCondition.getCriterionList() == null) ? 0 : (searchCondition.getCriterionList().size()); i < j; i++)
		{
			Criterion criterion = searchCondition.getCriterionList().get(i);
			String key = criterion.getKey();
			if (key.equals(Criterion.GROUP_START))
			{
				Criterion criterionNext = searchCondition.getCriterionList().get(i + 1);
				if (criterionNext.getKey().equalsIgnoreCase(Criterion.GROUP_END))
				{
					continue;
				}
				if (!isFirstCriterion && (!preKey.equals(Criterion.GROUP_START) && !preKey.equals(Criterion.CON_AND) && !preKey.equals(Criterion.CON_OR)))
				{
					whereClauseSb.append(" ").append(criterion.getConjunction()).append(" ");
				}

				whereClauseSb.append("(");
				isFirstCriterion = false;
				preKey = key;
				continue;
			}
			else if (key.equals(Criterion.GROUP_END))
			{
				if (i == 0)
				{
					continue;
				}
				Criterion criterionPre = searchCondition.getCriterionList().get(i - 1);
				if (criterionPre.getKey().equalsIgnoreCase(Criterion.GROUP_START))
				{
					continue;
				}
				whereClauseSb.append(")");
				isFirstCriterion = false;
				preKey = key;
				continue;
			}

			if (whereClauseSb.length() > 1 && whereClauseSb.charAt(whereClauseSb.length() - 1) != '(')
			{
				whereClauseSb.append(" ").append(criterion.getConjunction()).append(" ");
			}
			whereClauseSb.append(buildFilter(criterion, searchCondition.caseSensitive()));

			isFirstCriterion = false;
			preKey = key;
		}
		preKey = null;

		if (whereClauseSb.length() == 1 && whereClauseSb.charAt(0) == '(')
		{
			whereClauseSb.append("1=1 ");
		}
		whereClauseSb.append(")");

		return whereClauseSb.toString();
	}

	private static String buildFilter(Criterion criterion, boolean isCaseSensitive)
	{
		Object criterionValue = criterion.getValue();
		String key = criterion.getKey();
		if (isCaseSensitive && criterionValue instanceof String)
		{
			key = "upper(" + key + ")";
		}
		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign == OperateSignEnum.NOTNULL)
		{
			return key + " IS NOT NULL ";
		}
		else if (operateSign == OperateSignEnum.ISNULL)
		{
			return key + " IS NULL ";
		}
		else if (operateSign == OperateSignEnum.TRUE || operateSign == OperateSignEnum.YES)
		{
			return key + "='Y' ";
		}
		else if (operateSign == OperateSignEnum.FALSE || operateSign == OperateSignEnum.NO)
		{
			return "(" + key + " IS NULL OR " + key + "='N') ";
		}
		else if (operateSign == OperateSignEnum.CONTAIN)
		{
			return key + " like '%?%'";
		}
		else if (operateSign == OperateSignEnum.NOTCONTAIN)
		{
			return key + " not like '%?%'";
		}
		else if (operateSign == OperateSignEnum.STARTWITH)
		{
			return key + " not like '?%'";
		}
		else if (operateSign == OperateSignEnum.ENDWITH)
		{
			return key + " not like '%?'";
		}
		else if (operateSign == OperateSignEnum.BIGGER && NumberUtils.isNumeric(criterion.toString()))
		{
			return key + ">" + Integer.parseInt(criterion.toString());
		}
		else if (operateSign == OperateSignEnum.BIGGEROREQUAL && NumberUtils.isNumeric(criterion.toString()))
		{
			return key + ">=" + Integer.parseInt(criterion.toString());
		}
		else if (operateSign == OperateSignEnum.SMALLER && NumberUtils.isNumeric(criterion.toString()))
		{
			return key + "<" + Integer.parseInt(criterion.toString());
		}
		else if (operateSign == OperateSignEnum.SMALLEROREQUAL && NumberUtils.isNumeric(criterion.toString()))
		{
			return key + "<=" + Integer.parseInt(criterion.toString());
		}
		return "";
	}

	private static List<SqlParamData> listSqlParamData(SearchCondition searchCondition)
	{
		if (searchCondition == null)
		{
			return null;
		}

		List<SqlParamData> list = new ArrayList<>();
		for (int i = 0, j = (searchCondition.getCriterionList() == null) ? 0 : (searchCondition.getCriterionList().size()); i < j; i++)
		{
			Criterion criterion = searchCondition.getCriterionList().get(i);
			list.add(buildSqlParamData(criterion, searchCondition.caseSensitive()));
		}
		return list;
	}

	private static SqlParamData buildSqlParamData(Criterion criterion, boolean isCaseSensitive)
	{
		Object criterionValue = criterion.getValue();
		String key = criterion.getKey();
		if (isCaseSensitive && criterionValue instanceof String)
		{
			key = "upper(" + key + ")";
			criterionValue = StringUtils.convertNULLtoString(criterionValue).toUpperCase();
		}

		String fieldtype = criterion.getFieldtype();
		Class<?> javaType = String.class;
		if (FieldTypeEnum.typeof(fieldtype) == FieldTypeEnum.DATETIME || FieldTypeEnum.typeof(fieldtype) == FieldTypeEnum.DATE)
		{
			javaType = Date.class;
		}
		else if (FieldTypeEnum.typeof(fieldtype) == FieldTypeEnum.INTEGER || FieldTypeEnum.typeof(fieldtype) == FieldTypeEnum.FLOAT)
		{
			javaType = BigDecimal.class;
		}

		SqlParamData paramData = new SqlParamData(key, criterionValue, javaType);

		OperateSignEnum operateSign = criterion.getOperateSignEnum();

		if (operateSign == OperateSignEnum.CONTAIN || operateSign == OperateSignEnum.NOTCONTAIN || operateSign == OperateSignEnum.STARTWITH
				|| operateSign == OperateSignEnum.ENDWITH)
		{
			return paramData;
		}
		return null;
	}
}
