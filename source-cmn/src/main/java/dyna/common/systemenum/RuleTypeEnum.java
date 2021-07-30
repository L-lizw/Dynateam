package dyna.common.systemenum;

public enum RuleTypeEnum
{
	BOM("BOM"), // BOM
	RELATION("ID_SYS_DATACHECK_RULETYPE_RELATION"), // 关联关系
	WF("ID_SYS_DATACHECK_RULETYPE_WF"), // 流程
	OBJECTFIELD("ID_SYS_DATACHECK_RULETYPE_OBJFIELD"), // Object字段
	ERP("ERP")// ERP抛转
	;

	private String	msrId;

	private RuleTypeEnum(String msrId)
	{
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	@Override
	public String toString()
	{
		return msrId;
	}
}
