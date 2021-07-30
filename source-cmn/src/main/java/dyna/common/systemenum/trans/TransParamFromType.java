package dyna.common.systemenum.trans;

public enum TransParamFromType
{
	SEAL("ID_CLIENT_TRANS_PARAM_SEAL"), WF("ID_CLIENT_TRANS_PARAM_WF"), OBJECT("ID_CLIENT_TRANS_PARAM_OBJECT");

	private String	msrId	= null;

	private TransParamFromType(String msrId)
	{

		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public static TransParamFromType typeValueOf(String type)
	{
		if (type != null)
		{
			for (TransParamFromType dayOfWeekEnum : TransParamFromType.values())
			{
				if (type.equalsIgnoreCase(dayOfWeekEnum.name()))
				{
					return dayOfWeekEnum;
				}
			}
		}

		return null;
	}
}
