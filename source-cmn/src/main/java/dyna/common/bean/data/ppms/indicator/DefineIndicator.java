package dyna.common.bean.data.ppms.indicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 指标
 * 
 * @author Administrator
 * 
 */
public class DefineIndicator implements Serializable
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 2481423725145497722L;

	private String						id					= null;

	private String						name				= null;

	private FunctionBean				function			= null;

	private IndicatorDismension			dismension			= null;

	private List<Map<String, String>>	params				= null;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public FunctionBean getFunction()
	{
		return function;
	}

	public void setFunction(FunctionBean function)
	{
		this.function = function;
	}

	public IndicatorDismension getDismension()
	{
		return dismension;
	}

	public void setDismension(IndicatorDismension dismension)
	{
		this.dismension = dismension;
	}

	public List<Map<String, String>> getParams()
	{
		return params == null ? new ArrayList<Map<String, String>>() : params;
	}
	
	public void setParams(List<Map<String, String>> params)
	{
		this.params = params;
	}
}
