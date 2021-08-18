/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EOSSImpl 企业对象脚本服务实现
 * Wanglei 2011-3-25
 */
package dyna.app.service.brs.eoss;

import dyna.app.service.BusinessRuleService;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.InputObject;
import dyna.common.bean.data.input.InputObjectBOMViewActionImpl;
import dyna.common.bean.data.input.InputObjectListActionImpl;
import dyna.common.bean.data.input.InputObjectWrokflowActionImpl;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.EventTypeEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EOSS;
import dyna.net.service.brs.WFI;
import dyna.net.service.brs.WFM;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;

/**
 * Enterprise Object Script Service 企业对象脚本服务实现
 * 
 * @author Wanglei
 * 
 */
public class EOSSImpl extends BusinessRuleService implements EOSS
{

	private ClassScriptStub				classScriptStub				= null;
	private ActionExecuteStub			actionExecuteStub			= null;
	private InstEventExecuteStub		instEventExecuteStub		= null;
	private WorkflowScriptStub			workflowScriptStub			= null;
	private WorkflowEventExecuteStub	workflowEventExecuteStub	= null;
	private static ScriptEngine			jse							= new ScriptEngineManager().getEngineByName("JavaScript");

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public void init()
	{
		// do nothing
	}

	protected ClassScriptStub getClassScriptStub()
	{
		return this.classScriptStub;
	}

	protected WorkflowScriptStub getWorkflowScriptStub()
	{
		return this.workflowScriptStub;
	}

	protected ActionExecuteStub getActionExecuteStub()
	{
		return this.actionExecuteStub;
	}

	protected InstEventExecuteStub getInstEventExecuteStub()
	{
		return this.instEventExecuteStub;
	}

	protected WorkflowEventExecuteStub getWorkflowEventExecuteStub()
	{
		return this.workflowEventExecuteStub;
	}

	protected synchronized WFM getWFM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized WFI getWFI() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#getEventScript(java.lang.String, dyna.common.systemenum.EventTypeEnum)
	 */
	@Override
	public Script getEventScript(String className, EventTypeEnum type, int... segments) throws ServiceRequestException
	{
		return this.getClassScriptStub().getEventScript(className, type, segments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#getEventScript(java.lang.String, dyna.common.systemenum.EventTypeEnum)
	 */
	@Override
	public Script getWorkflowEventScript(String workflowName, EventTypeEnum type, int... segments) throws ServiceRequestException
	{
		return this.getWorkflowScriptStub().getWorkflowEventScript(workflowName, type, segments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#getActionScript(java.lang.String, java.lang.String)
	 */
	@Override
	public Script getActionScript(String className, String scriptName, int... segments) throws ServiceRequestException
	{
		return this.getClassScriptStub().getActionScript(className, scriptName, segments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#getActionScript(java.lang.String, java.lang.String)
	 */
	@Override
	public Script getWorkflowActionScript(String workflowName, String activityName, int... segments) throws ServiceRequestException
	{
		return this.getWorkflowScriptStub().getWorkflowActionScript(workflowName, activityName, segments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeActionOnInstance(dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Override
	public ScriptEvalResult executeActionOnInstance(InputObject inputObject, String scriptName) throws ServiceRequestException
	{
		return this.getActionExecuteStub().executeActionOnInstance((FoundationObject) inputObject, scriptName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeActionOnBOMView(dyna.common.bean.data.input.InputObjectBOMViewActionImpl,
	 * java.lang.String)
	 */
	@Override
	public ScriptEvalResult executeActionOnBOMView(InputObjectBOMViewActionImpl inputObject, String scriptName) throws ServiceRequestException
	{
		return this.getActionExecuteStub().executeActionOnBOMView(inputObject, scriptName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeAddBeforeEvent(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public ScriptEvalResult executeAddBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeAddBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeAddAfterEvent(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public ScriptEvalResult executeAddAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeAddAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeUpdateBeforeEvent(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public ScriptEvalResult executeUpdateBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeUpdateBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeUpdateAfterEvent(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public ScriptEvalResult executeUpdateAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeUpdateAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeDeleteBeforeEvent(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public ScriptEvalResult executeDeleteBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeDeleteBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeDeleteAfterEvent(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public ScriptEvalResult executeDeleteAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeDeleteAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeReviseBeforeEvent(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public ScriptEvalResult executeReviseBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeReviseBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeReviseAfterEvent(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public ScriptEvalResult executeReviseAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeReviseAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeObsoleteBeforeEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeObsoleteBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeObsoleteBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeObsoleteAfterEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeObsoleteAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeObsoleteAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeEffectBeforeEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeEffectBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeEffectBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeEffectAfterEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeEffectAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeEffectAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeCheckInBeforeEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeCheckInBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeCheckInBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeCheckInAfterEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeCheckInAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeCheckInAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeCheckOutBeforeEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeCheckOutBeforeEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeCheckOutBeforeEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeCheckOutAfterEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeCheckOutAfterEvent(InputObject foundationObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeCheckOutAfterEvent(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeSubmitToLibBeforeEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeSubmitToLibBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeSubmitToLibBeforeEvent(inputObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeSubmitToLibAfterEvent(dyna.common.bean.data.InputObject)
	 */
	@Override
	public ScriptEvalResult executeSubmitToLibAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeSubmitToLibAfterEvent(inputObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeActionOnList(dyna.common.bean.data.input.InputObjectListActionImpl,
	 * java.lang.String)
	 */
	@Override
	public ScriptEvalResult executeActionOnList(InputObjectListActionImpl inputObject, String scriptName) throws ServiceRequestException
	{
		return this.getActionExecuteStub().executeActionOnList(inputObject, scriptName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeScript(dyna.common.bean.data.InputObject,
	 * dyna.common.systemenum.EventTypeEnum)
	 */
	@Override
	public ScriptEvalResult executeScriptFromUI(InputObject inputObject, EventTypeEnum eventType) throws ServiceRequestException
	{
		return this.getInstEventExecuteStub().executeScript(inputObject, eventType, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeScript(dyna.common.bean.data.InputObject,
	 * dyna.common.systemenum.EventTypeEnum)
	 */
	@Override
	public ScriptEvalResult executeWorkflowAction(InputObjectWrokflowActionImpl inputObject) throws ServiceRequestException
	{
		return this.getWorkflowScriptStub().executeWorkflowAction(inputObject, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#executeScript(dyna.common.bean.data.InputObject,
	 * dyna.common.systemenum.EventTypeEnum)
	 */
	@Override
	public ScriptEvalResult executeWorkflowActionFromUI(InputObjectWrokflowActionImpl inputObject) throws ServiceRequestException
	{
		return this.getWorkflowScriptStub().executeWorkflowAction(inputObject, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EOSS#isExecuteWorkflowActionFromUI(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isMustExecuteWorkflowActionFromUI(String workflowName, String activityName) throws ServiceRequestException
	{
		return this.getWorkflowScriptStub().isMustExecuteWorkflowActionFromUI(workflowName, activityName);
	}

	@Override
	public ScriptEvalResult executeWorkflowAddBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{

		return this.getWorkflowEventExecuteStub().executeAddBeforeEvent(inputObject);
	}

	@Override
	public ScriptEvalResult executeWorkflowAddAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		return this.getWorkflowEventExecuteStub().executeAddAfterEvent(inputObject);

	}

	@Override
	public ScriptEvalResult executeWorkflowStartBeforeEvent(InputObject inputObject) throws ServiceRequestException
	{
		return this.getWorkflowEventExecuteStub().executeStartBeforeEvent(inputObject);

	}

	@Override
	public ScriptEvalResult executeWorkflowStartAfterEvent(InputObject inputObject) throws ServiceRequestException
	{
		return this.getWorkflowEventExecuteStub().executeStartAfterEvent(inputObject);
	}

	@Override
	public Double calculate(String str) throws ServiceRequestException
	{
		return calculate(str, null);
	}

	@Override
	public Double calculate(String str, Integer scale) throws ServiceRequestException
	{
		Object obj = null;
		try
		{
			obj = jse.eval(str);
		}
		catch (ScriptException e)
		{
			throw new ServiceRequestException("ID_APP_INST_EVENT_EXCEPTION_WHEN_CALCULATE", "calculate failed, formula meybe wrong.", e);
		}
		BigDecimal d = new BigDecimal(obj.toString());
		scale = scale == null ? 9 : scale;
		return d.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	public String getScriptContent(String fileName) throws ServiceRequestException
	{
		return StringUtils.convertNULLtoString(this.getActionExecuteStub().getScriptContent(fileName));
	}

	@Override
	public void saveScriptContent(String fileName, String content) throws ServiceRequestException
	{
		this.getActionExecuteStub().saveScriptContent(fileName, content);

	}
}
