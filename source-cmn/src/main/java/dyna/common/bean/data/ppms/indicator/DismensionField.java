package dyna.common.bean.data.ppms.indicator;

import java.io.Serializable;

/**
 * 维度字段
 * 
 * @author duanll
 * 
 */
public class DismensionField implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1366886957282035482L;

	private String	fieldName;

	private String	description;

	private String	testName;

	private boolean	isFunction;

	public String getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String gettestName()
	{
		return testName;
	}

	public void settestName(String testName)
	{
		this.testName = testName;
	}

	public boolean isFunction()
	{
		return isFunction;
	}

	public void setFunction(boolean isFunction)
	{
		this.isFunction = isFunction;
	}
}