package dyna.dbcommon.function.columntype;

import dyna.common.systemenum.FieldTypeEnum;

public class OracleColumnTypeFunction implements ColumnTypeFunction
{
	@Override
	public String getColumnType(FieldTypeEnum fieldTypeEnum, String fieldSize)
	{
		switch (fieldTypeEnum)
		{
		case CODE:
		case FOLDER:
		case OBJECT:
		case CODEREF:
		case CLASSIFICATION:
			return "CHAR(32)";
		case DATE:
		case DATETIME:
			return "DATE";
		case FLOAT:
		case INTEGER:
			return "NUMBER";
		case BOOLEAN:
			return "CHAR(1)";
		case MULTICODE:
			return "VARCHAR2(4000)";
		default:
			return "VARCHAR2(" + fieldSize + ")";
		}
	}
}
