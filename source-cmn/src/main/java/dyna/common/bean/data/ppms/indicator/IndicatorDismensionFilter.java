package dyna.common.bean.data.ppms.indicator;

import java.io.Serializable;

/**
 * 指标维度过滤明细(显示用)
 * 
 * @author duanll
 * 
 */
public class IndicatorDismensionFilter implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1285682981422665274L;

	private String	filterName	= null;

	private String	filters		= null;

	public String getFilterName()
	{
		return filterName;
	}

	public void setFilterName(String filterName)
	{
		this.filterName = filterName;
	}

	public String getFilters()
	{
		return filters;
	}

	public void setFilters(String filters)
	{
		this.filters = filters;
	}
}
