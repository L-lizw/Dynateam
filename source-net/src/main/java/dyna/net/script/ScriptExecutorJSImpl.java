/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptExecutorJSImpl javascript脚本执行器
 * Wanglei 2011-3-25
 */
package dyna.net.script;

import java.util.List;

import javax.script.Bindings;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.ScriptCallbackEnum;
import dyna.common.bean.model.ScriptResultOptionInfo;
import dyna.common.bean.model.ScriptResultOptionInfo.MessageTypeEnum;
import dyna.common.bean.model.ScriptResultTypeEnum;
import dyna.common.context.ScriptContext;
import dyna.common.systemenum.MessageIconEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * javascript脚本执行器
 * 
 * @author Wanglei
 * 
 */
public class ScriptExecutorJSImpl extends AbstractScriptExecutor
{

	/**
	 * @param classLoader
	 */
	public ScriptExecutorJSImpl()
	{
		super(null, "js");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.script.ScriptExecutor#execute(java.lang.Object, java.lang.String)
	 */
	@Override
	public ScriptEvalResult execute(Script script, String scriptContent, Bindings bindings) throws Exception
	{
		if (this.engine == null)
		{
			throw new IllegalStateException("Script Engine is not available");
		}

		if (StringUtils.isNullString(scriptContent))
		{
			return null;
		}

		String scriptName = script.getName().replace('.', '_');

		StringBuffer scriptBuf = new StringBuffer();
		scriptBuf.append("function f_");
		scriptBuf.append(scriptName);
		scriptBuf.append("(){");
		scriptBuf.append(scriptContent);
		scriptBuf.append("}f_");
		scriptBuf.append(scriptName);
		scriptBuf.append("();");

		ScriptContext context = (ScriptContext) bindings.get("scriptcontext");

		ScriptEvalResult evalResult = new ScriptEvalResult();
		evalResult.inputObject = (InputObject) bindings.get("inputObject");
		evalResult.inputObject.setScriptContext(context);

		Object result = this.engine.eval(scriptBuf.toString(), bindings);

		evalResult.returnObject = result;// != null && result instanceof Boolean ? (Boolean) result : true;
		evalResult.scriptName = scriptName;
		evalResult.callbackEnum = script.getScriptCallbackEnum();
		evalResult.scriptEvalCallBackInputSet = context.scriptEvalCallBackInputSet;

		String title = context.dialogTitle;
		String message = context.dialogMessage;
		if (script.getScriptResultType().equals(ScriptResultTypeEnum.CUSTOM_OPTION))
		{
			if (script.isFixedMessage())
			{
				message = script.getMessage();
			}
		}

		ScriptResultTypeEnum resultType = script.getScriptResultType();

		String[] options = script.getOptions();
		ScriptCallbackEnum[] callbackEnums = null;
		List<Script> children = script.getChildren();
		if (!SetUtils.isNullList(children))
		{
			int size = children.size();
			options = new String[size];
			callbackEnums = new ScriptCallbackEnum[size];
			for (int i = 0; i < size; i++)
			{
				options[i] = children.get(i).getTitle();
				callbackEnums[i] = children.get(i).getScriptCallbackEnum();
			}
		}

		if (options != null && options.length != 0)
		{
			evalResult.optionInfo = new ScriptResultOptionInfo(title, message, resultType, options, callbackEnums);
			if (resultType.equals(ScriptResultTypeEnum.CUSTOM_OPTION))
			{
				if (MessageIconEnum.ERROR.equals(script.getMessageIcon()))
				{
					evalResult.optionInfo.setMsgType(MessageTypeEnum.ERROR);
				}
				else if (MessageIconEnum.QUESTION.equals(script.getMessageIcon()))
				{
					evalResult.optionInfo.setMsgType(MessageTypeEnum.QUESTION);
				}
			}
		}

		return evalResult;
	}

}
