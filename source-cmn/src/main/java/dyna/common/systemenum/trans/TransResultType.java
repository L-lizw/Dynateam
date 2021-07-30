package dyna.common.systemenum.trans;

public enum TransResultType
{
	PDF3D("ID_CLIENT_TRANSFORM_TYPE_PDF3D"), //
	PDF("ID_CLIENT_TRANSFORM_TYPE_PDF");//
	// STAMP("ID_CLIENT_TRANSFORM_TYPE_STAMP");

	private String	msrId	= null;

	private TransResultType(String msrId)
	{

		this.msrId = msrId;
	}

	public static TransResultType typeValueOf(String type)
	{
		if (type != null)
		{
			for (TransResultType dayOfWeekEnum : TransResultType.values())
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
