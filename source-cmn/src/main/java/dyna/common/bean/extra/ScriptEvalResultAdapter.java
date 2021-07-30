/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptEvalResultAdapter
 * Wanglei 2011-7-18
 */
package dyna.common.bean.extra;

/**
 * 脚本结果适配器
 * 
 * @author Wanglei
 * 
 */
public interface ScriptEvalResultAdapter
{

	/**
	 * 输入脚本执行结果, 产生相应的反馈
	 * 
	 * @param evalResult
	 * @return 用户所选的项
	 */
	public int adapt(ScriptEvalResult evalResult);
}
