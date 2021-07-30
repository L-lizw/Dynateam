/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RuleAuthEnum
 * zhanghj 2010-11-26
 */
package dyna.common.systemenum;


/**
 * @author zhanghj
 *
 */
public enum OrgTreeSearchTypeEnum
{
	ALL("ID_WEB_ORGTREE_SEARCHTYPE_ALL", 0), // 所有
	GROUP("ID_WEB_ORGTREE_SEARCHTYPE_GROUP", 4), // 组
	ROLE("ID_WEB_ORGTREE_SEARCHTYPE_ROLE", 5), // 角色
	USER("ID_WEB_ORGTREE_SEARCHTYPE_USER", 6), // 用户
	GROUPROLE("ID_WEB_ORGTREE_SEARCHTYPE_GROUPANDROLE", 1), // 组和角色
	GROUPUSER("ID_WEB_ORGTREE_SEARCHTYPE_GROUPANDUSER", 2), // 组和用户
	ROLEUSER("ID_WEB_ORGTREE_SEARCHTYPE_ROLEANDUSER", 3); // 角色和用户

	private final String	type;
	private final int		index;

	private OrgTreeSearchTypeEnum(String type, int index)
	{
		this.type = type;
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex()
	{
		return this.index;
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
