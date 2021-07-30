package dyna.common.sqlbuilder.plmdynamic;

public class SqlParamData
{
	private String paramName;
	private Object val;
	private Class<?> javaType;

	public SqlParamData()
	{
	}

	public SqlParamData(String paramName, Object val, Class<?> javaType)
	{
		this.paramName = paramName;
		this.val = val;
		this.javaType = javaType;
	}

	public String getParamName()
	{
		return this.paramName;
	}

	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}

	public Object getVal()
	{
		return this.val;
	}

	public void setVal(Object val)
	{
		this.val = val;
	}

	public Class<?> getJavaType()
	{
		return this.javaType;
	}

	public void setJavaType(Class<?> javaType)
	{
		this.javaType = javaType;
	}
}