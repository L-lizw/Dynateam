/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AccessTypeEnum
 * Wanglei 2010-7-30
 */
package dyna.common.systemenum;

/**
 * @author Wanglei
 * 
 */
public enum AccessTypeEnum
{

	USER(1), //
	OWNER(2), //
	RIG(3), //
	ROLE(4), //
	GROUP(5), //
	OTHERS(6);//

	private int	precedence	= 0;

	private AccessTypeEnum(int precedence)
	{
		this.precedence = precedence;
	}

	public int getPrecedence()
	{
		return this.precedence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.name();
	}

	public static AccessTypeEnum precedenceTypeValueOf(int precedence)
	{
		if (precedence == 0)
		{
			return null;
		}
		for (AccessTypeEnum precedenceType : AccessTypeEnum.values())
		{
			if (precedence == precedenceType.getPrecedence())
			{
				return precedenceType;
			}
		}
		return null;
	}
}
