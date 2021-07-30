/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericDataServer 数据服务器主入口
 * Wanglei 2010-11-30
 */
package dyna.data;

import dyna.common.log.DynaLogger;

/**
 * 数据服务器主入口
 * 
 * @author Wanglei
 * 
 */
public class GenericDataServer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DynaLogger.setDataLog();
			DataServer.createDataServer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
