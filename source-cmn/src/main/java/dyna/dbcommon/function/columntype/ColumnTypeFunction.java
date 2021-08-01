package dyna.dbcommon.function.columntype;

import dyna.common.systemenum.FieldTypeEnum;

public interface ColumnTypeFunction
{
	String getColumnType(FieldTypeEnum fieldTypeEnum, String fieldSize);
}
