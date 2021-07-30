/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptExecutors 脚本执行工厂方法集合
 * Wanglei 2011-3-25
 */
package dyna.net.script;

import javax.script.Bindings;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.systemenum.ScriptFileType;
import dyna.net.service.Service;

/**
 * 脚本执行工厂方法集合
 * 
 * @author Wanglei
 * 
 */
public class ScriptExecutors
{

	private static ScriptExecutor	jsExecutor	= new ScriptExecutorJSImpl();

	public static ScriptEvalResult executeScriptAtServer(Script script, String scriptContent, InputObject inputObject,
			Service provider, String sessionId)
					throws Exception
					{
		ScriptEvalResult ret = null;
		Bindings bindings = null;
		try{
			ScriptExecutor executor = getScriptExecutor(script.getScriptFileType());

			bindings = executor.getBindings(inputObject, script);
			bindings.put("provider", provider);
			bindings.put("sid", sessionId);

			ret = executor.execute(script, scriptContent, bindings);
		}
		finally
		{
			if (bindings != null)
			{
				bindings.clear();
			}
			bindings = null;
		}
		return ret;
					}

	private static ScriptExecutor getScriptExecutor(ScriptFileType type)
	{
		ScriptExecutor executor = null;
		switch (type)
		{
		case JAVASCRIPT:
			executor = jsExecutor;
			break;

		default:
			break;
		}
		return executor;
	}

}
