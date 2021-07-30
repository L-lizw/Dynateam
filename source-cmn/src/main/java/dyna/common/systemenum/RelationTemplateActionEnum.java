/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplateActionEnum
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * RelationTemplateActionEnum 关系模板事件类型枚举
 * 
 * @author caogc
 * 
 */
public enum RelationTemplateActionEnum
{
	NONE("0"), LINK("1"), COPYLINK("2"), REVISELINK("3"), CHECKIN("1"), CHECKOUT("1"), TRASCHKOUT("1"), CANCELCHKOUT(
			"1"), CHANGELOCATION("1"), DELETE("1"), RELEASE("1"), OBSOLETE("1"), SUBMITPROCESS("1"), MASTER("1"), REVISION(
			"2"), MASTER_REVISION("3"), ADDTOPRODUCT("1");

	private String	type	= null;

	private RelationTemplateActionEnum(String type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.type;
	}

}
