package dyna.common.systemenum;

public enum FileTransferResultEnum
{
	SUCCESS(1),

	NO_AUTH(2),

	MULTI_EXISTS_FO(3),

	MULTI_EXISTS_LOCAL(4),

	OTHERS(5);

	private int	type;

	private FileTransferResultEnum(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return this.type;
	}

	public static FileTransferResultEnum typeOf(int type)
	{
		FileTransferResultEnum[] typeEnums = FileTransferResultEnum.values();
		for (FileTransferResultEnum typeEnum : typeEnums)
		{
			if (typeEnum.getType() == type)
			{
				return typeEnum;
			}
		}
		return null;
	}
}
