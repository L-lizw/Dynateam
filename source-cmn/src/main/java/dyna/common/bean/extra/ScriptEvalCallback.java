/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptEvalCallback
 * Wanglei 2011-7-15
 */
package dyna.common.bean.extra;


/**
 * @author Wanglei
 *
 */
public interface ScriptEvalCallback
{

	/**
	 * 执行从脚本执行结果反馈的callback
	 * 
	 * @param inputObject
	 * @return
	 */
	public Object execute(ScriptEvalResult evalResult);
}
