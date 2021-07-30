package dyna.common.dto.erp;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class CrossParamList extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8194005850651278179L;

	// 参数名
	public static final String	PARAM_NAME			= "PARAM_NAME";

	// 参数值
	public static final String	PARAM_VALUE			= "PARAM_VALUE";
	
	public void setParamName(String paramName)
	{
		put(PARAM_NAME, paramName);
	}

	public String getParamName()
	{
		return (String) this.get(PARAM_NAME);
	}
	
	public void setParamValue(String paramValue)
	{
		put(PARAM_VALUE, paramValue);
	}

	public String getParamValue()
	{
		return (String) this.get(PARAM_VALUE);
	}
}
