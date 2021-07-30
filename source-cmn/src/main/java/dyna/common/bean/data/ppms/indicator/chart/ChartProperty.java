package dyna.common.bean.data.ppms.indicator.chart;

import dyna.common.bean.data.ppms.indicator.chart.style.ChartStyle;

public class ChartProperty
{
	// 标题
	private String		title;
	// X轴标签
	private String		domainAxisLabel;
	// Y轴标签
	private String		rangeAxisLabel;
	// 数据源
	private Object		dataset;
	// 样式表
	private ChartStyle	chartStyle;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDomainAxisLabel()
	{
		return domainAxisLabel;
	}

	public void setDomainAxisLabel(String domainAxisLabel)
	{
		this.domainAxisLabel = domainAxisLabel;
	}

	public String getRangeAxisLabel()
	{
		return rangeAxisLabel;
	}

	public void setRangeAxisLabel(String rangeAxisLabel)
	{
		this.rangeAxisLabel = rangeAxisLabel;
	}

	public Object getDataset()
	{
		return dataset;
	}

	public void setDataset(Object dataset)
	{
		this.dataset = dataset;
	}

	public ChartStyle getChartStyle()
	{
		return chartStyle;
	}

	public void setChartStyle(ChartStyle chartStyle)
	{
		this.chartStyle = chartStyle;
	}
}
