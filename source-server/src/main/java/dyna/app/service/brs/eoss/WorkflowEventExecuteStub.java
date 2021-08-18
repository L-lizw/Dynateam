/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InstEventExecuteStub 与对象实例相关的脚本执行操作
 * Wanglei 2011-3-28
 */
package dyna.app.service.brs.eoss;

import dyna.app.service.brs.wfm.WFMImpl;
import dyna.common.bean.data.InputObject;
import dyna.common.bean.data.input.InputObjectWrokflowEventImpl;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.context.ScriptContext;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.EventTypeEnum;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 与对象实例相关的脚本执行操作
 * 
 * @author Wanglei
 * 
 */
@Component
public class WorkflowEventExecuteStub extends AbstractScriptServiceStub
{

	protected ScriptEvalResult executeScript(InputObject inputObject, EventTypeEnum eventType, boolean isFromUI) throws ServiceRequestException
	{
		ScriptEvalResult retObject = null;
		try
		{
			String processName = ((InputObjectWrokflowEventImpl) inputObject).getProcessName();
			if (StringUtils.isNullString(processName))
			{
				return null;
			}
			ScriptContext scriptContext = inputObject.getScriptContext();
			Script script = null;
			if (scriptContext != null)
			{
				String scriptName = scriptContext.scriptName + "." + scriptContext.segment;
				script = ((WFMImpl) this.stubService.getWFM()).getWfScriptStub().getScript(scriptName);
			}
			else
			{
				script = this.stubService.getWorkflowEventScript(processName, eventType);
			}

			if (script == null)
			{
				return null;
			}

			// if (script.getScriptResultType() == ScriptResultTypeEnum.NONE)
			// {
			retObject = super.executeScript(processName, script, inputObject);
			// }
		}
		catch (Exception e)
		{
			// after event do not throw execption
			if (!(eventType == EventTypeEnum.ADD_AFTER //
					|| eventType == EventTypeEnum.START_AFTER))
			{
				throw new ServiceRequestException("ID_APP_INST_EVENT_EXCEPTION", e.getMessage(), e);
			}
		}
		return retObject;
	}

	protected ScriptEvalResult executeAddBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.ADD_BEFORE, false);
		this.handleScriptResult(result, EventTypeEnum.ADD_BEFORE, true, "ID_APP_BLOCK_ADD_BEFORE", "add.before script refuse object creation");
		return result;
	}

	protected ScriptEvalResult executeAddAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.ADD_AFTER, false);
		this.handleScriptResult(result, EventTypeEnum.ADD_BEFORE, true, "ID_APP_BLOCK_ADD_AFTER", "add.after script refuse object creation");
		return result;
	}

	protected ScriptEvalResult executeStartBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.START_BEFORE, false);
		this.handleScriptResult(result, EventTypeEnum.START_BEFORE, true, "ID_APP_BLOCK_START_BEFORE", "start.before script refuse object creation");
		return result;
	}

	protected ScriptEvalResult executeStartAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.START_AFTER, false);
		this.handleScriptResult(result, EventTypeEnum.START_AFTER, true, "ID_APP_BLOCK_START_AFTER", "start.after script refuse object creation");
		return result;
	}

	protected void handleScriptResult(ScriptEvalResult result, EventTypeEnum eventType, boolean assertReturnObject, String assertFailMsrId, String assertFailMsg)
			throws ServiceRequestException
	{
		if (result == null)
		{
			return;
		}

		// after event do not throw execption
		if (eventType == EventTypeEnum.ADD_AFTER //
				|| eventType == EventTypeEnum.START_AFTER)
		{
			return;
		}

		if (assertReturnObject)
		{
			if (result.returnObject != null)
			{
				if (result.returnObject instanceof ServiceRequestException)
				{
					throw (ServiceRequestException) result.returnObject;
				}
				else if (result.returnObject instanceof Exception)
				{
					throw ServiceRequestException.createByException("", (Exception) result.returnObject);
				}
				else
				{
					if (result.returnObject instanceof Boolean && ((Boolean) result.returnObject))
					{
						// do nothing
					}
					else
					{
						throw new ServiceRequestException(assertFailMsrId, assertFailMsg);
					}
				}
			}
		}

		// if (result.inputObject.getScriptContext().isReturnScriptEval && //
		// (result.optionInfo != null && result.optionInfo.getResultType() != ScriptResultTypeEnum.NONE || //
		// // result.callbackEnum == ScriptCallbackEnum.FRONT_CUSTOM || //
		// result.callbackEnum == ScriptCallbackEnum.FRONT_DISPLAY_INSTANCE))
		// {
		// throw new ServiceRequestException(assertFailMsrId, assertFailMsg, new ScriptEvalResultWrap(result,
		// eventType));
		// }
	}
}
