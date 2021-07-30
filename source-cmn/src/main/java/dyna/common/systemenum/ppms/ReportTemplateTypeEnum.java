package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

public enum ReportTemplateTypeEnum
{
	// 普通报表
	NOR_REPORT("0", "ID_CLIENT_REPORT_TEMPLATE_TYPE_NOR"),
	// 聚合报表
	JH_REPORT("1", "ID_CLIENT_REPORT_TEMPLATE_TYPE_JH"),
	// 决策报表
	JC_REPORT("2", "ID_CLIENT_REPORT_TEMPLATE_TYPE_JC"), ;

	private String	messageId;

	private String	type;

	private ReportTemplateTypeEnum(String type, String messageId)
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

	public static ReportTemplateTypeEnum typeOf(String type)
	{
		if (!StringUtils.isNullString(type))
		{
			for (ReportTemplateTypeEnum templateEnum : ReportTemplateTypeEnum.values())
			{
				if (type.equals(templateEnum.getType()))
				{
					return templateEnum;
				}
			}
		}
		return ReportTemplateTypeEnum.NOR_REPORT;
	}
}
