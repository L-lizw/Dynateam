package dyna.data.common.function.typename;

public class MySQLTypeNameConvert implements TypeNameConvertFunction
{
	@Override
	public String getTypeName(String typeName)
	{
		return typeName.toLowerCase();
	}

	@Override
	public String getSchemaName(String userName)
	{
		return userName;
	}

}
