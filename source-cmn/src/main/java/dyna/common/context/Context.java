/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaContext
 * Wanglei 2010-3-26
 */
package dyna.common.context;

import java.io.Serializable;

/**
 * 上下文接口, 提供给需要提供上下文的应用对象
 * 
 * @author Wanglei
 * 
 */
public interface Context extends Serializable
{
	public Object getAttribute(String key);

	public void setAttribute(String key, Object value);

	public Object removeAttribute(String key);
}
