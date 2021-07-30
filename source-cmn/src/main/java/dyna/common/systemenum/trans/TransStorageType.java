package dyna.common.systemenum.trans;

public enum TransStorageType
{
	PREVIEW("ID_CLIENT_TRANS_STOR_PREVIEW"), //
	OBJECT("ID_CLIENT_TRANS_STOR_OBJECT"), //
	ATTACHMENT("ID_CLIENT_TRANS_STOR_ATTACHMENT");//

	private String	msrId	= null;

	private TransStorageType(String msrId)
	{

		this.msrId = msrId;
	}

	public static TransStorageType typeValueOf(String type)
	{
		if (type != null)
		{
			for (TransStorageType dayOfWeekEnum : TransStorageType.values())
			{
				if (type.equalsIgnoreCase(dayOfWeekEnum.name()))
				{
					return dayOfWeekEnum;
				}
			}
		}

		return null;
	}

	public String getMsrId()
	{
		return this.msrId;
	}
}
