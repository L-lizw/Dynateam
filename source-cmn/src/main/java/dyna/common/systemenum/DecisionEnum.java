/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DecisionEnum
 * Wanglei 2010-11-3
 */
package dyna.common.systemenum;

/**
 * @author Wanglei
 * 
 */
public enum DecisionEnum
{

	/**
	 * 同意
	 */
	ACCEPT("ID_SYS_ENUM_ACCEPT"),

	/**
	 * 不同意
	 */
	REJECT("ID_SYS_ENUM_REJECT"),

	/**
	 * 跳过
	 */
	SKIP("ID_SYS_ENUM_SKIP");
	private String	messageId	= null;

	/**
	 * 
	 */
	private DecisionEnum(String messageId)
	{
		this.messageId = messageId;
	}

	public String getMessageId()
	{
		return this.messageId;
	}
}
