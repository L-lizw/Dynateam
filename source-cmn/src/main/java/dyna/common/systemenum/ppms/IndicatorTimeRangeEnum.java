package dyna.common.systemenum.ppms;

public enum IndicatorTimeRangeEnum
{
	MONTH("MONTH"), // 月
	QUARTER("QUARTER"), // 季度
	HALF_YEAR("HALFYEAR"), // 半年
	YEAR("YEAR"), // 年
	;

	private String	id;

	private IndicatorTimeRangeEnum(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return this.id;
	}

	public static IndicatorTimeRangeEnum typevalueof(String val)
	{
		for (IndicatorTimeRangeEnum timeRange : IndicatorTimeRangeEnum.values())
		{
			if (timeRange.getId().equals(val))
			{
				return timeRange;
			}
		}
		return null;
	}
}
