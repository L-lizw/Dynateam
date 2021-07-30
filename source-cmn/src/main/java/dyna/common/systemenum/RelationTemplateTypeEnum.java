/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplateTypeEnum
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * RelationTemplateTypeEnum 关系模板类型枚举
 * 普通关系 or 树形关系
 * 
 * @author caogc
 * 
 */
public enum RelationTemplateTypeEnum
{
	NORMAL("ID_SYS_TEMPLATE_RELATION", "0"), // 普通关系模板
	TREE("ID_SYS_TEMPLATE_STRUCTURE", "1"), // 结构关系模板
	FACTORY("ID_SYS_TEMPLATE_FACTORY", "2"), // 工厂别模板

	// 依赖模板
	EXTENDED("ID_SYS_TEMPLATE_EXTENDED", "3"), // 扩展关联模板
	DETAIL("ID_SYS_TEMPLATE_DETAIL", "4");// 明细模板

	private String	msrId	= null;

	private String	type	= null;

	private RelationTemplateTypeEnum(String msrId, String type)
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

}
