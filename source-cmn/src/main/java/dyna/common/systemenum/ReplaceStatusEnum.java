package dyna.common.systemenum;

import dyna.common.util.StringUtils;

public enum ReplaceStatusEnum
{
	EFFECTIVE("Effective"), UNACTIVATED("Unactivated"), EXPIRE("Expire");

	private String	desc;

	private ReplaceStatusEnum(String desc)
	{
		this.desc = desc;
	}

	public String getDesc()
	{
		return this.desc;
	}

	@Override
	public String toString()
	{
		return this.desc;
	}

	public ReplaceStatusEnum getEnum(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (ReplaceStatusEnum replaceStatus : ReplaceStatusEnum.values())
		{
			if (value.equalsIgnoreCase(replaceStatus.getDesc()))
			{
				return replaceStatus;
			}
		}
		return null;
	}

}
