package dyna.dbcommon.function.sqlconvert;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class SqlServerDDLConvert implements DDLConvertFunction
{
	@Override
	public String getSql(String tableName, String columnName, String columnType, DDLTypeEnum ddlTypeEnum)
	{
		if (ddlTypeEnum == DDLTypeEnum.ALTER_COLUMN)
		{
			return "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " " + columnType;
		}
		return null;
	}
}
