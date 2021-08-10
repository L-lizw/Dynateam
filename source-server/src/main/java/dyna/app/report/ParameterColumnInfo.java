package dyna.app.report;

import net.sf.jasperreports.engine.JRPropertiesMap;

public class ParameterColumnInfo
{
	// 参数名
	private String			parameterName	= null;

	// 参数值类型
	private Class<?>		clz				= null;

	// 参数描述
	private String			description		= null;

	// 参数属性集合
	private JRPropertiesMap	propertiesMap	= null;

	// 是否是模板内置参数
	private boolean			isSystemDefined	= false;

	public ParameterColumnInfo(String parameterName, Class<?> clz, String description, JRPropertiesMap propertiesMap)
	{
		this.parameterName = parameterName;
		this.clz = clz;
		this.description = description;
		this.propertiesMap = propertiesMap;
	}

	public ParameterColumnInfo(String parameterName, Class<?> clz, String description, boolean isSystemDefined, JRPropertiesMap propertiesMap)
	{
		this.parameterName = parameterName;
		this.clz = clz;
		this.description = description;
		this.isSystemDefined = isSystemDefined;
		this.propertiesMap = propertiesMap;
	}

	public String getParameterName()
	{
		return parameterName;
	}

	public void setParameterName(String parameterName)
	{
		this.parameterName = parameterName;
	}

	public Class<?> getClz()
	{
		return clz;
	}

	public void setClz(Class<?> clz)
	{
		this.clz = clz;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isSystemDefined()
	{
		return isSystemDefined;
	}

	public void setSystemDefined(boolean isSystemDefined)
	{
		this.isSystemDefined = isSystemDefined;
	}

	public JRPropertiesMap getPropertiesMap()
	{
		return propertiesMap;
	}

	public void setPropertiesMap(JRPropertiesMap propertiesMap)
	{
		this.propertiesMap = propertiesMap;
	}
}
