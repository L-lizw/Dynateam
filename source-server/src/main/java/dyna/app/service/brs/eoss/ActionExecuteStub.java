/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ActionExecuteStub 与Action相关的脚本执行操作
 * Wanglei 2011-3-28
 */
package dyna.app.service.brs.eoss;

import javax.script.ScriptException;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.emm.ClassStub;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.InputObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.input.InputObjectBOMViewActionImpl;
import dyna.common.bean.data.input.InputObjectListActionImpl;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.context.ScriptContext;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;

/**
 * 与Action相关的脚本执行操作
 * 
 * @author Wanglei
 * 
 */
public class ActionExecuteStub extends AbstractScriptServiceStub
{

	/**
	 * @param context
	 * @param service
	 */
	public ActionExecuteStub(ServiceContext context, EOSSImpl service)
	{
		super(context, service);
	}

	private ScriptEvalResult executeAction(InputObject object, String className, String scriptName) throws ServiceRequestException
	{
		Script script = null;
		ScriptContext scriptContext = object.getScriptContext();
		ScriptEvalResult xScriptEvalResult = null;
		if (scriptContext != null)
		{
			String scriptName1 = scriptContext.scriptName + "." + scriptContext.segment;
			// TODO duanll
			// script = DataServer.getClassModelService().getScript(scriptName1);
		}
		else
		{
			script = this.stubService.getActionScript(className, scriptName);
		}

		if (script == null)
		{
			return null;
		}

		try
		{
			xScriptEvalResult = this.executeScript(className, script, object);
		}
		catch (ScriptException e)
		{
			throw new ServiceRequestException("ID_APP_ACTION_EXCEPTION", e.getMessage(), e);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("ID_APP_ACTION_EXCEPTION", e.getMessage(), e);
		}
		if (xScriptEvalResult != null && xScriptEvalResult.returnObject instanceof ServiceRequestException)
		{
			throw (ServiceRequestException) xScriptEvalResult.returnObject;
		}
		return xScriptEvalResult;
	}

	protected ScriptEvalResult executeActionOnInstance(FoundationObject object, String scriptName) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(object.getObjectGuid(), this.stubService);

		String className = object.getObjectGuid().getClassName();

		return this.executeAction(object, className, scriptName);
	}

	protected ScriptEvalResult executeActionOnBOMView(InputObjectBOMViewActionImpl inputObject, String scriptName) throws ServiceRequestException
	{
		ObjectGuid objectGuid = inputObject.getObjectGuid();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		String className = objectGuid.getClassName();

		return this.executeAction(inputObject, className, scriptName);
	}

	/**
	 * @param inputObject
	 * @param scriptName
	 * @return
	 */
	public ScriptEvalResult executeActionOnList(InputObjectListActionImpl inputObject, String scriptName) throws ServiceRequestException
	{
		return this.executeAction(inputObject, inputObject.getClassName(), scriptName);
	}

}
