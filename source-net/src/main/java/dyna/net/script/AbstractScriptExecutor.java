/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractScriptExecutor 脚本执行器虚类
 * Wanglei 2011-3-25
 */
package dyna.net.script;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.model.Script;
import dyna.common.context.ScriptContext;

/**
 * 脚本执行器虚类, 所有脚本执行器的实现均需继承此类
 * 
 * @author Wanglei
 * 
 */
public abstract class AbstractScriptExecutor implements ScriptExecutor
{
	private ScriptEngineManager	engineManager	= null;
	protected ScriptEngine			engine			= null;

	protected AbstractScriptExecutor(ClassLoader classLoader, String scriptExtension)
	{
		if (classLoader == null)
		{
			this.engineManager = new ScriptEngineManager();
		}
		else
		{
			this.engineManager = new ScriptEngineManager(classLoader);
		}

		this.getScriptEngineByExtension(scriptExtension);
	}

	protected ScriptEngine getScriptEngineByExtension(String extension)
	{
		if (this.engine == null)
		{
			this.engine = this.engineManager.getEngineByExtension(extension);
		}
		return this.engine;
	}

	protected ScriptEngine getScriptEngineByName(String shortName)
	{
		if (this.engine == null)
		{
			this.engine = this.engineManager.getEngineByName(shortName);
		}
		return this.engine;
	}

	@Override
	public Bindings getBindings(InputObject inputObject, Script script)
	{
		Bindings bindings = new SimpleBindings();
		bindings.put("inputObject", inputObject);

		ScriptContext context = null;
		if (inputObject != null && inputObject.getScriptContext() != null)
		{
			context = inputObject.getScriptContext();
		}

		if (context == null)
		{
			context = new ScriptContext();
		}
		context.scriptName = script.getSequenceFullName();// .getFullName();

		bindings.put("scriptcontext", context);

		return bindings;

	}
}
