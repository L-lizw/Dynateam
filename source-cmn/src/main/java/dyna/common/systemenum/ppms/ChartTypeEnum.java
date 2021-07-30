package dyna.common.systemenum.ppms;

public enum ChartTypeEnum
{
	BAR("ID_CLIENT_CHART_BAR"), // 柱形图
	LINE("ID_CLIENT_CHART_LINE"), // 折线图
	PIE("ID_CLIENT_CHART_PIE"), // 饼图
	RING("ID_CLIENT_CHART_RING"), // 环图
	LINE_BAR("ID_CLIENT_CHART_LINE_BAR"), // 折线-柱形复合图
	DAIL("ID_CLIENT_CHART_DAIL"), // 数字表盘
	;

	private String	msgId;

	private ChartTypeEnum(String msgId)
	{
		this.msgId = msgId;
	}

	public String getMsgId()
	{
		return this.msgId;
	}
}
