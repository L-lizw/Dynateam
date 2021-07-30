package dyna.common.bean.data.ppms.indicator.chart.style;

/**
 * 环形图样式
 * 
 * @author Administrator
 * 
 */
public class RingChartStyle extends ChartStyle
{
	private int		centerTextFontSize	= 25;

	private boolean	isCenterTextBold	= true;

	public int getCenterTextFontSize()
	{
		return centerTextFontSize;
	}

	public void setCenterTextFontSize(int centerTextFontSize)
	{
		this.centerTextFontSize = centerTextFontSize;
	}

	public boolean isCenterTextBold()
	{
		return isCenterTextBold;
	}

	public void setCenterTextBold(boolean isCenterTextBold)
	{
		this.isCenterTextBold = isCenterTextBold;
	}
}
