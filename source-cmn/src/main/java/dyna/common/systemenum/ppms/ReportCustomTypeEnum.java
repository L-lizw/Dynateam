package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

public enum ReportCustomTypeEnum
{
	// 默认报表
	DEFAULT("0", "ID_CLIENT_REPORT_TITLE_DEFAULT"),
	// 自定义报表
	CUSTOM("1", "ID_CLIENT_REPORT_TITLE_CUSTOM"), ;

	private String	messageId;

	private String	type;

	private ReportCustomTypeEnum(String type, String messageId)
	{
		this.type = type;
		this.messageId = messageId;
	}

	public String getMessageId()
	{
		return this.messageId;
	}

	public String getType()
	{
		return this.type;
	}

	public static ReportCustomTypeEnum typeOf(String type)
	{
		if (!StringUtils.isNullString(type))
		{
			for (ReportCustomTypeEnum templateEnum : ReportCustomTypeEnum.values())
			{
				if (type.equals(templateEnum.getType()))
				{
					return templateEnum;
				}
			}
		}
		return ReportCustomTypeEnum.DEFAULT;
	}
}
