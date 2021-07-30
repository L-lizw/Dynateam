/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptContext
 * Wanglei 2011-7-12
 */
package dyna.common.context;

import dyna.common.bean.model.Script;
import dyna.common.dto.ScriptEvalCallBackInputSet;

/**
 * 脚本上下文
 * 
 * @author Wanglei
 * 
 */
public class ScriptContext extends BaseContext
{

	private static final long			serialVersionUID			= 990249082491655588L;

	/**
	 * 执行脚本时, 是否需要返回脚本的执行结果
	 */
	public boolean						isReturnScriptEval			= true;

	/**
	 * 脚本名称
	 */
	public String						scriptName					= null;

	/**
	 * 脚本片段
	 */
	public int							segment						= -1;

	/**
	 * 反馈到客户端所显示对话框的标题
	 */
	public String						dialogTitle					= Script.DEFAULT_TITLE;

	/**
	 * 反馈到客户端所显示对话框的消息
	 */
	public String						dialogMessage				= null;

	/**
	 * callback需要的自定义参数
	 */
	private Object[]					callbackArguments			= null;

	/**
	 * 脚本打开前台画面编辑器的参数设置
	 */
	public ScriptEvalCallBackInputSet	scriptEvalCallBackInputSet	= null;

	/**
	 * 设置自定义callback所需的参数
	 * 
	 * @param args
	 */
	public void setCallbackArguments(Object... args)
	{
		this.callbackArguments = args;
	}

	/**
	 * 获取自定义callback所需的参数
	 * 
	 * @return
	 */
	public Object[] getCallbackArguments()
	{
		return this.callbackArguments;
	}
}
