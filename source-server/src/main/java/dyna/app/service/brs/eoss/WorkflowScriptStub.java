/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassScriptStub 与模型类相关的脚本执行操作
 * Wanglei 2011-3-25
 */
package dyna.app.service.brs.eoss;

import dyna.app.service.brs.wfm.WFMImpl;
import dyna.common.bean.data.InputObject;
import dyna.common.bean.data.input.InputObjectWrokflowActionImpl;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.ScriptResultTypeEnum;
import dyna.common.context.ScriptContext;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.EventTypeEnum;
import dyna.common.util.SetUtils;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;

/**
 * 与模型类相关的脚本执行操作
 * 
 * @author Wanglei
 * 
 */
@Component
public class WorkflowScriptStub extends AbstractScriptServiceStub
{

	protected Script getWorkflowActionScript(String workflowName, String activityName, int... segments) throws ServiceRequestException
	{
		Script script = ((WFMImpl) this.stubService.getWFM()).getWfScriptStub().getActionScript(workflowName, activityName);
		return this.getScriptSegment(script, segments);
	}

	protected Script getWorkflowEventScript(String workflowName, EventTypeEnum type, int... segments) throws ServiceRequestException
	{
		Script script = ((WFMImpl) this.stubService.getWFM()).getWfScriptStub().getEventScript(workflowName, type);
		return this.getScriptSegment(script, segments);
	}

	private ScriptEvalResult executeAction(InputObject object, String workflowName, String activityName, boolean isFromUI) throws ServiceRequestException
	{
		Script script = null;
		ScriptContext scriptContext = object.getScriptContext();
		ScriptEvalResult xScriptEvalResult = null;
		if (scriptContext != null)
		{
			String scriptName1 = scriptContext.scriptName + "." + scriptContext.segment;
			script = ((WFMImpl) this.stubService.getWFM()).getWfScriptStub().getScript(scriptName1);
		}
		else
		{
			script = this.stubService.getWorkflowActionScript(workflowName, activityName);
		}

		if (script == null)
		{
			return null;
		}

		try
		{
			if (isFromUI || (!isFromUI && script.getScriptResultType() == ScriptResultTypeEnum.NONE))
			{
				xScriptEvalResult = this.executeScript(workflowName + "_" + activityName, script, object);
			}

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

	protected ScriptEvalResult executeWorkflowAction(InputObjectWrokflowActionImpl inputObject, boolean isFromUI) throws ServiceRequestException
	{
		return this.executeAction(inputObject, inputObject.getProcessName(), inputObject.getActrtName(), isFromUI);
	}

	protected boolean isMustExecuteWorkflowActionFromUI(String workflowName, String activityName) throws ServiceRequestException
	{
		Script script = this.getWorkflowActionScript(workflowName, activityName);
		if (script != null)
		{
			if (script.getScriptResultType() != ScriptResultTypeEnum.NONE)
			{
				return true;
			}
		}
		return false;
	}

	private Script getScriptSegment(Script script, int... segments)
	{
		if (script == null || segments == null || segments.length == 0)
		{
			return script;
		}

		List<Script> children = script.getChildren();
		if (SetUtils.isNullList(children))
		{
			return null;
		}

		if (segments[0] < 0 || segments[0] >= children.size())
		{
			return null;
		}

		int[] nextSeg = Arrays.copyOfRange(segments, 1, segments.length);
		return this.getScriptSegment(children.get(segments[0]), nextSeg);
	}
}
