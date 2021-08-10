/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Tracked
 * Wanglei 2011-11-11
 */
package dyna.app.core.track.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.TrackerRenderer;

/**
 * 标注服务api中的方法是否需要系统日志
 * 
 * @author Wanglei
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Tracked
{
	/**
	 * 定义操作内容, 支持多语言描述, 如未找到多语言, 则使用输入的字符串
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * define renderer for where
	 * 
	 * @return
	 */
	Class<? extends TrackerRenderer> renderer() default TrackerRenderer.class;

	/**
	 * define the way of track's persistence
	 * 
	 * @return
	 */
	Class<? extends TrackerPersistence> persistence() default TrackerPersistence.class;
}
