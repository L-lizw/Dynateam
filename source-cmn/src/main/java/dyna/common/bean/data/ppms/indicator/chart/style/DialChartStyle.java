package dyna.common.bean.data.ppms.indicator.chart.style;

public class DialChartStyle extends ChartStyle
{
	// 最大刻度(表示最大刻度值，只有在图表为仪表盘类型，且值类型为数值类型时才有效。根据设定的最大刻度，把表盘分隔为10个区间)
	// 当数值包含小数点时，数值类型为Float，否则为Integer
	private String	maxScale			= "100";

	// 数值框内字体大小
	private int		dialValFontSize		= 10;

	// 仪表盘刻度半径
	private double	tickRadius			= 0.8D;

	// 仪表盘数值刻度距离线条刻度的距离
	private double	tickLabelOffset		= 0.14999999999999999D;

	// 仪表盘数值刻度的字体大小
	private int		tickLabelFontSize	= 10;

	// 仪表盘刻度线条颜色(主线)
	private String	majorTickPaintRGB	= "0,0,255";

	// 仪表盘刻度线条颜色(非主线)
	private String	minorTickPaintRGB	= "0,0,255";

	public String getMaxScale()
	{
		return maxScale;
	}

	public void setMaxScale(String maxScale)
	{
		this.maxScale = maxScale;
	}

	public int getDialValFontSize()
	{
		return dialValFontSize;
	}

	public void setDialValFontSize(int dialValFontSize)
	{
		this.dialValFontSize = dialValFontSize;
	}

	public double getTickRadius()
	{
		return tickRadius;
	}

	public void setTickRadius(double tickRadius)
	{
		this.tickRadius = tickRadius;
	}

	public double getTickLabelOffset()
	{
		return tickLabelOffset;
	}

	public void setTickLabelOffset(double tickLabelOffset)
	{
		this.tickLabelOffset = tickLabelOffset;
	}

	public int getTickLabelFontSize()
	{
		return tickLabelFontSize;
	}

	public void setTickLabelFontSize(int tickLabelFontSize)
	{
		this.tickLabelFontSize = tickLabelFontSize;
	}

	public String getMajorTickPaintRGB()
	{
		return majorTickPaintRGB;
	}

	public void setMajorTickPaintRGB(String majorTickPaintRGB)
	{
		this.majorTickPaintRGB = majorTickPaintRGB;
	}

	public String getMinorTickPaintRGB()
	{
		return minorTickPaintRGB;
	}

	public void setMinorTickPaintRGB(String minorTickPaintRGB)
	{
		this.minorTickPaintRGB = minorTickPaintRGB;
	}
}
