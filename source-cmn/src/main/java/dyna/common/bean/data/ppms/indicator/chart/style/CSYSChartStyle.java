package dyna.common.bean.data.ppms.indicator.chart.style;

/**
 * 坐标系图表样式
 * 
 * @author Administrator
 * 
 */
public class CSYSChartStyle extends ChartStyle
{
	// 横轴刻度字体大小
	private int	domainTickLabelFontSize	= 12;

	// 纵轴标签字体大小
	private int	rangeLabelFontSize		= 16;

	// 纵轴标签字体大小
	private int	rangeTickLabelFontSize	= 12;

	public int getDomainTickLabelFontSize()
	{
		return domainTickLabelFontSize;
	}

	public void setDomainTickLabelFontSize(int domainTickLabelFontSize)
	{
		this.domainTickLabelFontSize = domainTickLabelFontSize;
	}

	public int getRangeLabelFontSize()
	{
		return rangeLabelFontSize;
	}

	public void setRangeLabelFontSize(int rangeLabelFontSize)
	{
		this.rangeLabelFontSize = rangeLabelFontSize;
	}

	public int getRangeTickLabelFontSize()
	{
		return rangeTickLabelFontSize;
	}

	public void setRangeTickLabelFontSize(int rangeTickLabelFontSize)
	{
		this.rangeTickLabelFontSize = rangeTickLabelFontSize;
	}
}
