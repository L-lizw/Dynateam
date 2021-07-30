package dyna.common.bean.data.configparamter;

public enum ConditionRelationEnum
{
	// SCOPE("0", "APP_CONDITIONRELATIONENUM_SCOPE"), // 范围
	CHOICE("1", "APP_CONDITIONRELATIONENUM_CHOICE"), // 选择
	EQUAL("2", "APP_CONDITIONRELATIONENUM_EQUAL"), // 等于
	MORETHAN("3", "APP_CONDITIONRELATIONENUM_MORETHAN"), // 大于
	LESSTHAN("4", "APP_CONDITIONRELATIONENUM_LESSTHAN"), // 小于
	UNEQUAL("5", "APP_CONDITIONRELATIONENUM_UNEQUAL"), // 不等于
	;
	private String	id		= null;
	private String	msrId	= null;

	ConditionRelationEnum(String id, String msrId)
	{
		this.id = id;
		this.msrId = msrId;
	}

	public static ConditionRelationEnum getConditionRelationById(String id)
	{
		for (ConditionRelationEnum relation : ConditionRelationEnum.values())
		{
			if (id.equalsIgnoreCase(relation.getId()))
			{
				return relation;
			}
		}
		return null;
	}

	public String getId()
	{
		return id;
	}

	public String getMsrId()
	{
		return msrId;
	}

	public static ConditionRelationEnum getEnumWithid(String id)
	{
		for (ConditionRelationEnum value : ConditionRelationEnum.values())
		{
			if (value.getId().equals(id))
			{
				return value;
			}
		}
		return null;
	}
}
