/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ObjectContext
 * Wanglei 2011-7-12
 */
package dyna.common.context;

import dyna.common.bean.extra.PromptMessage;

/**
 * 对象上下文
 * 
 * @author Wanglei
 * 
 */
public class ObjectContext extends BaseContext
{

	private static final long	serialVersionUID	= 2361716103367015321L;

	/**
	 * 记录自动分配的编号
	 */
	public String				allocateId			= null;

	/**
	 * 提示消息
	 */
	public PromptMessage		message				= null;

	/**
	 * 脚本上下文
	 */
	public ScriptContext		scriptContext		= null;

}
