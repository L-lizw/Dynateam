/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptEvalResultWrap
 * Wanglei 2011-7-19
 */
package dyna.common.exception;

import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.systemenum.EventTypeEnum;

/**
 * 脚本执行结果反馈封装
 * @author Wanglei
 *
 */
public class ScriptEvalResultWrap extends Exception
{
	private static final long	serialVersionUID	= 6674547705230686441L;

	private ScriptEvalResult	result				= null;
	private EventTypeEnum		eventType			= null;

	public ScriptEvalResultWrap(ScriptEvalResult result)
	{
		this(result, null);
	}

	public ScriptEvalResultWrap(ScriptEvalResult result, EventTypeEnum eventType)
	{
		super();
		this.result = result;
		this.eventType = eventType;
	}

	public ScriptEvalResult getScriptEvalResult()
	{
		return this.result;
	}

	public EventTypeEnum getEventType()
	{
		return this.eventType;
	}
}
