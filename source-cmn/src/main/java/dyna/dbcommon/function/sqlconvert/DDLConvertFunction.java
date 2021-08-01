package dyna.dbcommon.function.sqlconvert;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public interface DDLConvertFunction
{
	String getSql(String tableName, String columnName, String columnType, DDLTypeEnum ddlTypeEnum);
}
