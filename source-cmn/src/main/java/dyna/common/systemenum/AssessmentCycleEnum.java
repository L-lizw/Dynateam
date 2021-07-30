package dyna.common.systemenum;

public enum AssessmentCycleEnum
{
	YEAR("ID_SYS_TIME_RANGE_YEAR"), HALFYEAR("ID_SYS_TIME_RANGE_HALFYEAR"), QUARTER("ID_SYS_TIME_RANGE_QUARTER");

	private String	msgId;

	private AssessmentCycleEnum(String msgId)
	{
		this.msgId = msgId;
	}

	public String getMsgId()
	{
		return this.msgId;
	}
}
