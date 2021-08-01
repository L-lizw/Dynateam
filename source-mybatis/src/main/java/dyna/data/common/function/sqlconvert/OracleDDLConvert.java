package dyna.data.common.function.sqlconvert;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class OracleDDLConvert implements DDLConvertFunction
{
	@Override
	public String getSql(String tableName, String columnName, String columnType, DDLTypeEnum ddlTypeEnum)
	{
		if (ddlTypeEnum == DDLTypeEnum.ALTER_COLUMN)
		{
			return "ALTER TABLE " + tableName + " MODIFY " + columnName + " " + columnType;
		}
		return null;
	}
}
