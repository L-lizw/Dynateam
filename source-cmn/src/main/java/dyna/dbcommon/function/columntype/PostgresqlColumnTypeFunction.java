package dyna.dbcommon.function.columntype;

import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.util.StringUtils;

public class PostgresqlColumnTypeFunction implements ColumnTypeFunction
{
	@Override
	public String getColumnType(FieldTypeEnum fieldTypeEnum, String fieldSize)
	{
		if (fieldTypeEnum == FieldTypeEnum.CLASSIFICATION || fieldTypeEnum == FieldTypeEnum.CODE || fieldTypeEnum == FieldTypeEnum.CODEREF || fieldTypeEnum == FieldTypeEnum.FOLDER
				|| fieldTypeEnum == FieldTypeEnum.OBJECT)
		{
			return "char(32)";
		}
		if (fieldTypeEnum == FieldTypeEnum.BOOLEAN)
		{
			return "char(1)";
		}
		if (fieldTypeEnum == FieldTypeEnum.DATE || fieldTypeEnum == FieldTypeEnum.DATETIME)
		{
			return "timestamp";
		}
		if (fieldTypeEnum == FieldTypeEnum.FLOAT || fieldTypeEnum == FieldTypeEnum.INTEGER)
		{
			return "numeric";
		}
		if (StringUtils.isNullString(fieldSize))
		{
			fieldSize = "128";
		}
		if (fieldTypeEnum == FieldTypeEnum.MULTICODE)
		{
			fieldSize = "4000";
		}
		return "varchar(" + fieldSize + ")";
	}
}
