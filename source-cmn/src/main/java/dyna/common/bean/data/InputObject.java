/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InputObject
 * Wanglei 2011-7-13
 */
package dyna.common.bean.data;

import java.io.Serializable;

import dyna.common.context.ScriptContext;

/**
 * 脚本执行的输入对象
 * 
 * @author Wanglei
 * 
 */
public interface InputObject extends Serializable
{
	/**
	 * get script context
	 * 
	 * @return
	 */
	public ScriptContext getScriptContext();

	/**
	 * set script context
	 * 
	 * @param context
	 */
	public void setScriptContext(ScriptContext context);

	/**
	 * 执行实例事件脚本时, 返回实例的objectguid
	 * 
	 * @return
	 */
	public ObjectGuid getObjectGuid();
}
