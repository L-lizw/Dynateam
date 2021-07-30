package dyna.common.systemenum;

public enum ScriptTypeEnum
{
	CLASSACTION("2"), //
	CLASSEVENT("1"), //
	WFACTACTION("4"), //
	WFEVENT("3"),//
	;

	private String type;

	ScriptTypeEnum(String type)
	{
		this.type = type;
	}

	public static ScriptTypeEnum typeOf(String type)
	{
		ScriptTypeEnum[] values = ScriptTypeEnum.values();
		for (ScriptTypeEnum value : values)
		{
			if (value.getType().equals(type))
			{
				return value;
			}
		}
		return null;
	}

	public String getType()
	{
		return this.type;
	}
}
