package dyna.common.bean.data.ppms.indicator.chart.style;

public class ChartStyle
{
	// 是否显示图例
	private boolean	isLegendVisible	= true;

	// 百分比显示
	private boolean	isPercentage	= false;

	// 图例字体大小
	private int		legendFontSize	= 12;

	public boolean isLegendVisible()
	{
		return isLegendVisible;
	}

	public void setLegendVisible(boolean isLegendVisible)
	{
		this.isLegendVisible = isLegendVisible;
	}

	public boolean isPercentage()
	{
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage)
	{
		this.isPercentage = isPercentage;
	}

	public int getLegendFontSize()
	{
		return legendFontSize;
	}

	public void setLegendFontSize(int legendFontSize)
	{
		this.legendFontSize = legendFontSize;
	}
}
