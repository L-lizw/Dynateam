/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SessionActiveTime
 * Wanglei 2012-12-20
 */
package dyna.app.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiuxq
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SessionActiveTime
{
	boolean isUpdate() default true;
}
