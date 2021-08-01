package dyna.dbcommon.util;

import dyna.common.systemenum.FieldTypeEnum;

import java.math.BigDecimal;
import java.sql.Date;

public class DSCommonUtil
{
	public static Class<?> getJavaTypeOfField(FieldTypeEnum fieldType)
	{
		switch (fieldType)
		{
		case DATE:
		case DATETIME:
			return Date.class;
		case FLOAT:
		case INTEGER:
			return BigDecimal.class;
		default:
			return String.class;
		}
	}

	public static String getTableAlias(String mainTableAlias, String tableName)
	{
		if (!isSystemTable(tableName))
		{
			return mainTableAlias + tableName.substring(tableName.length() - 1);
		}
		return mainTableAlias;
	}

	public static boolean isSystemTable(String tableName)
	{
		return tableName.endsWith("_0");
	}
}
