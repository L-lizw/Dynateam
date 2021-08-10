/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TrackerAdoption
 * Wanglei 2011-11-10
 */
package dyna.app.core.track;

/**
 * 将日志存入数据库
 * 
 * @author Wanglei
 * 
 */
public interface TrackerPersistence
{
	/**
	 * 存储
	 * 
	 * @param tracker
	 *            日志信息对象
	 */
	public void persist(Tracker tracker) throws Exception;
}
