package dyna.common.systemenum.coding;

import dyna.common.util.StringUtils;

public enum CFMCodeRuleEnum
{
	FIXED("fixed", "ID_SYS_CFMCODERULE_FIXED"), // 固定
	SERIAL("Serial", "ID_SYS_CFMCODERULE_SERIAL"), // 流水号
	DATE("date", "ID_SYS_CFMCODERULE_DATE"), // 日期
	DATETIME("datetime", "ID_SYS_CFMCODERULE_DATETIME"), // 时间
	FIELD("field", "ID_SYS_CFMCODERULE_FIELD"); // 选值

	private final String	type;
	private final String	msrId;

	private CFMCodeRuleEnum(String type, String msrId)
	{
		this.type = type;
		this.msrId = msrId;
	}

	public String getType()
	{
		return this.type;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	public static CFMCodeRuleEnum typeValueOf(String type)
	{

		if (StringUtils.isNullString(type))
		{
			return null;
		}
		for (CFMCodeRuleEnum cfmCodeRuleEnum : CFMCodeRuleEnum.values())
		{
			if (type.equalsIgnoreCase(cfmCodeRuleEnum.getType()))
			{
				return cfmCodeRuleEnum;
			}
		}

		return null;
	}
}
