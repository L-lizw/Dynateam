/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TrackerManager
 * Wanglei 2011-11-9
 */
package dyna.app.core.track;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户操作日志管理器
 * 
 * @author Wanglei
 * 
 */
@Component
public class TrackerManager
{

	private static final Map<Method, TrackerBuilder>	buildersMap	= new HashMap<Method, TrackerBuilder>();

	/**
	 * 根据调用的方法获取日志构建器
	 * 
	 * @param method
	 *            所调用的方法
	 * @return
	 */
	public TrackerBuilder getTrackerBuilder(Method method)
	{
		return buildersMap.get(method);
	}

	/**
	 * 将方法绑定日志构建器, 若构建器已经存在, 则覆盖原构建器
	 * 
	 * @param method
	 * @param builder
	 */
	public void bindTrackerBuilder(Method method, TrackerBuilder builder)
	{
		buildersMap.put(method, builder);
	}

	/**
	 * 解除指定方法与构建器的绑定
	 * 
	 * @param method
	 */
	public void unbindTrackerBuilder(Method method)
	{
		buildersMap.remove(method);
	}

}
