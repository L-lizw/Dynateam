/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BaseContext
 * Wanglei 2011-7-12
 */
package dyna.common.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用上下文实现
 * 
 * @author Wanglei
 * 
 */
public class BaseContext implements Context
{
	private static final long	serialVersionUID	= -7945564785841734757L;

	private Map<String, Object>	attributes			= null;

	/* (non-Javadoc)
	 * @see dyna.common.context.Context#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String key)
	{
		if (this.attributes == null)
		{
			return null;
		}
		return this.attributes.get(key);
	}

	/* (non-Javadoc)
	 * @see dyna.common.context.Context#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized void setAttribute(String key, Object value)
	{
		if (this.attributes == null)
		{
			this.attributes = Collections.synchronizedMap(new HashMap<String, Object>());
		}
		this.attributes.put(key, value);
	}

	/* (non-Javadoc)
	 * @see dyna.common.context.Context#removeAttribute(java.lang.String)
	 */
	@Override
	public Object removeAttribute(String key)
	{
		if (this.attributes == null)
		{
			return null;
		}
		return this.attributes.remove(key);
	}

}
