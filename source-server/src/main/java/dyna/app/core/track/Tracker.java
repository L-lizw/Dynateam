/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Tracker
 * Wanglei 2011-11-9
 */
package dyna.app.core.track;

import java.lang.reflect.Method;

import dyna.app.server.context.ServiceContext;
import dyna.net.security.signature.Signature;

/**
 * 操作日志<br>
 * 记录 谁, 什么时间, 在那里, 操作哪个对象, 做了什么, 结果如何
 * 
 * @author Wanglei
 * 
 */
public interface Tracker
{

	/**
	 * 
	 * @return
	 */
	public ServiceContext getServiceContext();

	/**
	 * 获取日志对应的会话信息
	 * 
	 * @return
	 */
	public Signature getSignature();

	/**
	 * 获取日志所调用方法的参数
	 * 
	 * @return
	 */
	public Object[] getParameters();

	/**
	 * 获取日志所调用的方法
	 * 
	 * @return
	 */
	public Method getMethod();

	/**
	 * 获取日志所调用方法执行返回的结果
	 * 
	 * @return 正确的结果或者产生的异常
	 */
	public Object getResult();

	/**
	 * 操作渲染器
	 * 
	 * @return
	 */
	public TrackerRenderer getTrackerRenderer();

	/**
	 * 自定义日志持久化机制
	 * 
	 * @return
	 */
	public TrackerPersistence getPersistence();

	/**
	 * 将日志存入数据库, 如自定义日志持久化机制存在, 则使用该机制存储.<br>
	 * 用户无需重写此方法
	 */
	public void persist() throws Exception;

	/**
	 * 将日志写入远程服务器
	 */
	public void getSyslogString() throws Exception;

}
