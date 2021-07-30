/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptEvalResult
 * Wanglei 2011-7-12
 */
package dyna.common.bean.extra;

import java.io.Serializable;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.model.ScriptCallbackEnum;
import dyna.common.bean.model.ScriptResultOptionInfo;
import dyna.common.dto.ScriptEvalCallBackInputSet;

/**
 * 脚本执行结果
 * 
 * @author Wanglei
 * 
 */
public class ScriptEvalResult implements Serializable
{

	private static final long			serialVersionUID			= 1363420457238118871L;

	/**
	 * 执行脚本的输入参数
	 */
	public InputObject					inputObject					= null;

	/**
	 * 执行脚本, 返回用于决定是否继续后续步骤的值
	 */
	public Object						returnObject				= null;

	/**
	 * 脚本名称
	 */
	public String						scriptName					= null;

	/**
	 * 脚本反馈类型
	 */
	public ScriptCallbackEnum			callbackEnum				= null;

	/**
	 * 脚本执行结果的选项提示信息
	 */
	public ScriptResultOptionInfo		optionInfo					= null;

	/**
	 * 脚本打开前台画面编辑器的参数设置
	 */
	public ScriptEvalCallBackInputSet	scriptEvalCallBackInputSet	= null;

}
