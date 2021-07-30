/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptExecutor 脚本执行器
 * Wanglei 2011-3-25
 */
package dyna.net.script;

import javax.script.Bindings;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;

/**
 * 脚本执行器
 * 
 * @author Wanglei
 * 
 */
public interface ScriptExecutor
{

	/**
	 * get Bindings which put inputObject into
	 * 
	 * @param inputObject
	 * @return
	 */
	public Bindings getBindings(InputObject inputObject, Script script);

	/**
	 * execute script beyond bindings
	 * 
	 * @param script
	 *            脚本对象
	 * @param scriptContent
	 *            脚本内容
	 * @param bindings
	 *            环境
	 * @return
	 * @throws Exception
	 */
	public ScriptEvalResult execute(Script script, String scriptContent, Bindings bindings)
			throws Exception;
}
