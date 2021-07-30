package dyna.common.bean.data.ppms.indicator.chart;

import java.util.HashMap;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ppms.ChartTypeEnum;
import dyna.common.systemenum.ppms.ChartValueTypeEnum;

public class IndicatorViewChartSet extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3078276628313258957L;

	// 指标编号
	public static final String	INDICATORID			= "INDICATORID";

	// 指标描述
	public static final String	INDICATORNAME		= "INDICATORNAME";

	// 图表类型
	public static final String	CHARTTYPE			= "CHARTTYPE";

	// 数值类型：一般数值；比率
	public static final String	VALUETYPE			= "VALUETYPE";

	// 样式集合
	private Map<String, String>	styleMap			= new HashMap<String, String>();

	public void setIndicatorId(String indicatorId)
	{
		this.put(INDICATORID, indicatorId);
	}

	public String getIndicatorId()
	{
		return (String) this.get(INDICATORID);
	}

	public void setIndicatorName(String indicatorName)
	{
		this.put(INDICATORNAME, indicatorName);
	}

	public String getIndicatorName()
	{
		return (String) this.get(INDICATORNAME);
	}

	public void setChartType(ChartTypeEnum chartType)
	{
		this.put(CHARTTYPE, chartType == null ? ChartTypeEnum.LINE.name() : chartType.name());
	}

	public ChartTypeEnum getChartType()
	{
		if (this.get(CHARTTYPE) == null)
		{
			return ChartTypeEnum.LINE;
		}
		return ChartTypeEnum.valueOf((String) this.get(CHARTTYPE));
	}

	public void setValueType(ChartValueTypeEnum valueType)
	{
		this.put(VALUETYPE, valueType == null ? ChartValueTypeEnum.NUMBER.name() : valueType.name());
	}

	public ChartValueTypeEnum getValueType()
	{
		if (this.get(VALUETYPE) == null)
		{
			return ChartValueTypeEnum.NUMBER;
		}
		return ChartValueTypeEnum.valueOf((String) this.get(VALUETYPE));
	}

	public Map<String, String> getStyleMap()
	{
		return styleMap;
	}

	public void setStyleMap(Map<String, String> styleMap)
	{
		this.styleMap = styleMap;
	}
}
