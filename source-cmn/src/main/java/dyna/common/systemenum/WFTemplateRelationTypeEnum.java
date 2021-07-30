/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WFTemplateRelationTypeEnum
 * caogc 2011-12-08
 */
package dyna.common.systemenum;

/**
 * WFTemplateRelationTypeEnum 工作流关系模板类型枚举
 * 
 * @author caogc
 * 
 */
public enum WFTemplateRelationTypeEnum
{
	// 1、bom；2、普通关系；3、结构关系；4、工厂别；5，扩展关联；6、明细
	NORMAL("ID_SYS_TEMPLATE_RELATION", "0"), // 普通关系模板
	TREE("ID_SYS_TEMPLATE_STRUCTURE", "1"), // 结构关系模板
	FACTORY("ID_SYS_TEMPLATE_FACTORY", "2"), // 工厂别模板
	EXTENDED("ID_SYS_TEMPLATE_EXTENDED", "3"), // 扩展关联模板
	DETAIL("ID_SYS_TEMPLATE_DETAIL", "4"), // 明细模板

	BOM("ID_SYS_TEMPLATE_BOM", "9"); // BOM模板
	private String	msrId	= null;

	private String	type	= null;

	private WFTemplateRelationTypeEnum(String msrId, String type)
	{
		this.msrId = msrId;
		this.type = type;
	}

	public String getMsrId()
	{
		return this.msrId;
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

	public static WFTemplateRelationTypeEnum typeValueOf(String type)
	{
		for (WFTemplateRelationTypeEnum relationTypeEnum : WFTemplateRelationTypeEnum.values())
		{
			if (relationTypeEnum.toString().equals(type))
			{
				return relationTypeEnum;
			}
		}
		return null;
	}

}
