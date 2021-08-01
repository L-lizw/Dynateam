package dyna.data.common.function.typename;

public class OracleTypeNameConvert implements TypeNameConvertFunction
{
	@Override
	public String getTypeName(String typeName)
	{
		return typeName.toUpperCase();
	}

	@Override
	public String getSchemaName(String userName)
	{
		return userName;
	}

}
