package dyna.common.systemenum;

import dyna.common.util.StringUtils;

public enum JoinTypeEnum
{
	AND("ID_SYS_JOINTYPE_AND"), OR("ID_SYS_JOINTYPE_OR");

	private String	messageId;

	private JoinTypeEnum(String messageId)
	{
		this.messageId = messageId;
	}

	public static JoinTypeEnum typeof(String joinType)
	{
		if (StringUtils.isNullString(joinType))
		{
			return null;
		}
		for (JoinTypeEnum joinTypeEnum : JoinTypeEnum.values())
		{
			if (joinType.toUpperCase().equals(joinTypeEnum.name()))
			{
				return joinTypeEnum;
			}
		}
		return null;
	}

	public String getMessageId()
	{
		return this.messageId;
	}
}
