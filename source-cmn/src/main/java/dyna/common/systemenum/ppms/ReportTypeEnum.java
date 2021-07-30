package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

public enum ReportTypeEnum
{
	// 项目报表
	PROJECT_LIST("0", "ID_CLIENT_REPORT_TITLE_PROJ_LIST"),
	// 项目经理统计报表
	MANAGER("1", "ID_CLIENT_REPORT_TITLE_PROJ_MANAGER"),
	// 部门项目统计报表
	DEPT_PROJ("2", "ID_CLIENT_REPORT_TITLE_PROJ_DEPT"),
	// 部门统计报表
	DEPT("3", "ID_CLIENT_REPORT_TITLE_DEPT"),
	// 资源报表
	RESOURCE("4", "ID_CLIENT_REPORT_TITLE_PROJ_RESOURCE"),
	// 里程碑报表
	MILESTONE("5", "ID_CLIENT_REPORT_TITLE_PROJ_MILESTONE"), ;

	private String	messageId;

	private String	type;

	private ReportTypeEnum(String type, String messageId)
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

	public static ReportTypeEnum typeOf(String type)
	{
		if (!StringUtils.isNullString(type))
		{
			for (ReportTypeEnum templateEnum : ReportTypeEnum.values())
			{
				if (type.equals(templateEnum.getType()))
				{
					return templateEnum;
				}
			}
		}
		return ReportTypeEnum.PROJECT_LIST;
	}
}
