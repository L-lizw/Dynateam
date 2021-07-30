package dyna.common.bean.data.ppms.indicator;

import java.io.Serializable;
import java.util.List;

/**
 * 指标维度过滤(显示用)
 * 
 * @author duanll
 * 
 */
public class IndicatorDismension implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2560767478635365252L;

	private String							dismensionName	= null;

	private List<IndicatorDismensionFilter>	filters			= null;

	public String getDismensionName()
	{
		return dismensionName;
	}

	public void setDismensionName(String dismensionName)
	{
		this.dismensionName = dismensionName;
	}

	public List<IndicatorDismensionFilter> getFilters()
	{
		return filters;
	}

	public void setFilters(List<IndicatorDismensionFilter> filters)
	{
		this.filters = filters;
	}
}
