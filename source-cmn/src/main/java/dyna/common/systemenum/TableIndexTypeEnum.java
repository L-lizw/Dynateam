package dyna.common.systemenum;

public enum TableIndexTypeEnum
{
	INDEX("IX"), FOREIGNKEY("FK");
	
	private String prefix;
	
	private TableIndexTypeEnum(String prefix)
	{
		this.prefix = prefix;
	}

	public static TableIndexTypeEnum typeValueOf(String name)
	{
		TableIndexTypeEnum[] types = TableIndexTypeEnum.values();
		for (TableIndexTypeEnum type : types)
		{
			if (type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		return null;
	}
	
	public String getPrefixByType()
	{
		return this.prefix;
	}
}
