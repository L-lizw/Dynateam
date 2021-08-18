/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InstEventExecuteStub 与对象实例相关的脚本执行操作
 * Wanglei 2011-3-28
 */
package dyna.app.service.brs.eoss;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.ScriptResultTypeEnum;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.EventTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 与对象实例相关的脚本执行操作
 * 
 * @author Wanglei
 * 
 */
@Component
public class InstEventExecuteStub extends AbstractScriptServiceStub
{
	protected ScriptEvalResult executeScript(InputObject inputObject, EventTypeEnum eventType)
			throws ServiceRequestException
	{
		return this.executeScript(inputObject, eventType, false);
	}

	protected ScriptEvalResult executeScript(InputObject inputObject, EventTypeEnum eventType, boolean isFromUI)
			throws ServiceRequestException
	{
		ScriptEvalResult retObject = null;
		try
		{
			ObjectGuid objectGuid = inputObject.getObjectGuid();

			Script script = null;
			String className = objectGuid.getClassName();
			script = this.stubService.getEventScript(className, eventType);

			if (script == null)
			{
				return null;
			}

			if (isFromUI || (!isFromUI && script.getScriptResultType() == ScriptResultTypeEnum.NONE))
			{
				retObject = super.executeScript(className, script, inputObject);
			}
		}
		catch (Exception e)
		{
			// after event do not throw execption
			if (!(eventType == EventTypeEnum.ADD_AFTER //
					|| eventType == EventTypeEnum.CHECKIN_AFTER//
					|| eventType == EventTypeEnum.CHECKOUT_AFTER //
					|| eventType == EventTypeEnum.DELETE_AFTER//
					|| eventType == EventTypeEnum.EFFECT_AFTER //
					|| eventType == EventTypeEnum.UPDATE_AFTER //
					|| eventType == EventTypeEnum.OBS_AFTER//
					|| eventType == EventTypeEnum.REVISE_AFTER //
			|| eventType == EventTypeEnum.SUBMITTOLIB_AFTER))
			{
				throw new ServiceRequestException("ID_APP_INST_EVENT_EXCEPTION", e.getMessage(), e);
			}
		}
		return retObject;
	}

	protected ScriptEvalResult executeAddBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.ADD_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.ADD_BEFORE, true, "ID_APP_BLOCK_ADD_BEFORE",
				"add.before script refuse object creation");
		return result;
	}

	protected ScriptEvalResult executeAddAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.ADD_AFTER);
		this.handleScriptResult(result, EventTypeEnum.ADD_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeUpdateBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.UPDATE_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.UPDATE_BEFORE, true, "ID_APP_BLOCK_UPDATE_BEFORE",
				"update.before script refuse object update");
		return result;
	}

	protected ScriptEvalResult executeUpdateAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.UPDATE_AFTER);
		this.handleScriptResult(result, EventTypeEnum.UPDATE_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeDeleteBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.DELETE_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.DELETE_BEFORE, true, "ID_APP_BLOCK_DEL_BEFORE",
				"delete.before script refuse object delete");
		return result;
	}

	protected ScriptEvalResult executeDeleteAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.DELETE_AFTER);
		this.handleScriptResult(result, EventTypeEnum.DELETE_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeReviseBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.REVISE_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.REVISE_BEFORE, true, "ID_APP_BLOCK_REVISE_BEFORE",
				"wip.before script refuse object revise");
		return result;
	}

	protected ScriptEvalResult executeReviseAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.REVISE_AFTER);
		this.handleScriptResult(result, EventTypeEnum.REVISE_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeObsoleteBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.OBS_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.OBS_BEFORE, true, "ID_APP_BLOCK_OBS_BEFORE",
				"obs.before script refuse object obsolete");
		return result;
	}

	protected ScriptEvalResult executeObsoleteAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.OBS_AFTER);
		this.handleScriptResult(result, EventTypeEnum.OBS_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeEffectBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.EFFECT_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.EFFECT_BEFORE, true, "ID_APP_BLOCK_EFFECT_BEFORE",
				"effect.before script refuse object effect");
		return result;
	}

	protected ScriptEvalResult executeEffectAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.EFFECT_AFTER);
		this.handleScriptResult(result, EventTypeEnum.EFFECT_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeCheckInBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.CHECKIN_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.CHECKIN_BEFORE, true, "ID_APP_BLOCK_CHECKIN_BEFORE",
				"checkin.before script refuse object checkin");
		return result;
	}

	protected ScriptEvalResult executeCheckInAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.CHECKIN_AFTER);
		this.handleScriptResult(result, EventTypeEnum.CHECKIN_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeCheckOutBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.CHECKOUT_BEFORE);
		this.handleScriptResult(result, EventTypeEnum.CHECKOUT_BEFORE, true, "ID_APP_BLOCK_CHECKOUT_BEFORE",
				"checkout.before script refuse object checkout");
		return result;
	}

	protected ScriptEvalResult executeCheckOutAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(foundationObject, EventTypeEnum.CHECKOUT_AFTER);
		this.handleScriptResult(result, EventTypeEnum.CHECKOUT_AFTER, false, null, null);
		return result;
	}

	protected ScriptEvalResult executeSubmitToLibBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.SUBMITTOLIB_AFTER);
		this.handleScriptResult(result, EventTypeEnum.SUBMITTOLIB_AFTER, true, "ID_APP_BLOCK_SUMBITTOLIB_BEFORE",
				"submittolib.before script refuse object submit to lib");
		return result;
	}

	protected ScriptEvalResult executeSubmitToLibAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		ScriptEvalResult result = this.executeScript(inputObject, EventTypeEnum.SUBMITTOLIB_AFTER);
		this.handleScriptResult(result, EventTypeEnum.SUBMITTOLIB_AFTER, false, null, null);
		return result;
	}

	protected void handleScriptResult(ScriptEvalResult result, EventTypeEnum eventType, boolean assertReturnObject,
			String assertFailMsrId, String assertFailMsg) throws ServiceRequestException
	{
		if (result == null)
		{
			return;
		}

		// after event do not throw execption
		if (eventType == EventTypeEnum.ADD_AFTER //
				|| eventType == EventTypeEnum.CHECKIN_AFTER//
				|| eventType == EventTypeEnum.CHECKOUT_AFTER //
				|| eventType == EventTypeEnum.DELETE_AFTER//
				|| eventType == EventTypeEnum.EFFECT_AFTER //
				|| eventType == EventTypeEnum.UPDATE_AFTER //
				|| eventType == EventTypeEnum.OBS_AFTER//
				|| eventType == EventTypeEnum.REVISE_AFTER //
				|| eventType == EventTypeEnum.SUBMITTOLIB_AFTER)
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
