/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClientMode
 * Wanglei 2010-11-29
 */
package dyna.common.systemenum;

/**
 * define client mode.
 * 
 * @author Wanglei
 * 
 */
public enum ConnectionMode
{
	/**
	 * all client, server together
	 */
	ALL_IN_ONE("all-in-one"), //

	/**
	 * client and server together
	 */
	BUILT_IN_SERVER("built-in-server"), //

	/**
	 * client, server, dsserver, dataserver separated.
	 */
	DISTRIBUTED("distributed");

	private String	name	= null;

	private ConnectionMode(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public static ConnectionMode getClientMode(String name)
	{
		if (ALL_IN_ONE.getName().equalsIgnoreCase(name))
		{
			return ALL_IN_ONE;
		}
		else if (BUILT_IN_SERVER.getName().equalsIgnoreCase(name))
		{
			return BUILT_IN_SERVER;
		}
		else if (DISTRIBUTED.getName().equalsIgnoreCase(name))
		{
			return DISTRIBUTED;
		}
		else
		{
			return null;
		}
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
