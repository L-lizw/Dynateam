package dyna.common.bean.data.ppms.indicator.chart;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class IndicatorViewRow extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long			serialVersionUID		= 4869895172942119395L;

	private List<IndicatorViewChartSet>	indicatorChartSetList	= new ArrayList<IndicatorViewChartSet>();

	public List<IndicatorViewChartSet> getIndicatorChartSetList()
	{
		return indicatorChartSetList;
	}

	public void setIndicatorChartSetList(List<IndicatorViewChartSet> indicatorChartSetList)
	{
		this.indicatorChartSetList = indicatorChartSetList;
	}

	public void addChartSet(IndicatorViewChartSet chartSet)
	{
		this.indicatorChartSetList.add(chartSet);
	}
}
