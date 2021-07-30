/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EOSS 企业对象脚本服务
 * Wanglei 2011-3-25
 */
package dyna.net.service.brs;

import javax.script.ScriptException;

import dyna.common.bean.data.InputObject;
import dyna.common.bean.data.input.InputObjectBOMViewActionImpl;
import dyna.common.bean.data.input.InputObjectListActionImpl;
import dyna.common.bean.data.input.InputObjectWrokflowActionImpl;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.EventTypeEnum;
import dyna.net.service.Service;

/**
 * Enterprise Object Script Service 企业对象脚本服务
 * 
 * @author Wanglei
 * 
 */
public interface EOSS extends Service
{

	/**
	 * 获取类的事件脚本
	 * 
	 * @param className
	 *            类名
	 * @param type
	 *            脚本类型
	 * @param segments
	 *            TODO
	 * @return
	 * @throws ServiceRequestException
	 */
	public Script getEventScript(String className, EventTypeEnum type, int... segments) throws ServiceRequestException;

	/**
	 * 获取类的动作脚本
	 * 
	 * @param className
	 *            类名
	 * @param scriptName
	 *            脚本名
	 * @param segments
	 *            TODO
	 * @return
	 * @throws ServiceRequestException
	 */
	public Script getActionScript(String className, String scriptName, int... segments) throws ServiceRequestException;

	/**
	 * 执行实例对象的指定动作, 如果脚本包含几个片段, 则自动从第一个片段开始执行
	 * 
	 * @param inputObject
	 *            实例对象
	 * @param scriptName
	 *            脚本名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeActionOnInstance(InputObject inputObject, String scriptName) throws ServiceRequestException;

	/**
	 * 执行bomview的动作脚本, 如果脚本包含几个片段, 则自动从第一个片段开始执行
	 * 
	 * @param inputObject
	 * @param scriptName
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeActionOnBOMView(InputObjectBOMViewActionImpl inputObject, String scriptName) throws ServiceRequestException;

	/**
	 * 执行实例对象的指定动作, 如果脚本包含几个片段, 则自动从第一个片段开始执行
	 * 
	 * @param inputObject
	 *            实例对象
	 * @param scriptName
	 *            脚本名称
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeActionOnList(InputObjectListActionImpl inputObject, String scriptName) throws ServiceRequestException;

	/**
	 * 执行实例add.before事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeAddBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例add.after事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeAddAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例update.before事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeUpdateBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例update.after事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeUpdateAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例delete.before事件
	 * 
	 * @param foundationObject
	 *            实例
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeDeleteBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例delete.after事件
	 * 
	 * @param foundationObject
	 *            实例
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeDeleteAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例revise.before事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeReviseBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行实例revise.after事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeReviseAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行obs.before事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeObsoleteBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行obs.after事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeObsoleteAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行effect.before事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeEffectBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行effect.after事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeEffectAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行checkin.before事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeCheckInBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行checkin.after事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeCheckInAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行checkout.before事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeCheckOutBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行checkout.after事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeCheckOutAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行submittolib.before事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeSubmitToLibBeforeEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 执行submittolib.after事件
	 * 
	 * @param foundationObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeSubmitToLibAfterEvent(InputObject foundationObject) throws ServiceRequestException;

	/**
	 * 从UI执行脚本
	 * 
	 * @param inputObject
	 * @param eventType
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeScriptFromUI(InputObject inputObject, EventTypeEnum eventType) throws ServiceRequestException;

	/**
	 * 取得工作流脚本
	 * 
	 * @param workflowName
	 * @param activityName
	 * @param segments
	 * @return
	 * @throws ServiceRequestException
	 */
	public Script getWorkflowActionScript(String workflowName, String activityName, int... segments) throws ServiceRequestException;

	/**
	 * 判断工作流脚本是否必须从UI执行
	 * 
	 * @param workflowName
	 * @param activityName
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isMustExecuteWorkflowActionFromUI(String workflowName, String activityName) throws ServiceRequestException;

	/**
	 * 执行工作流脚本
	 * 
	 * @param inputObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeWorkflowAction(InputObjectWrokflowActionImpl inputObject) throws ServiceRequestException;

	/**
	 * 执行工作流脚本，仅从ui执行
	 * 
	 * @param inputObject
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeWorkflowActionFromUI(InputObjectWrokflowActionImpl inputObject) throws ServiceRequestException;

	/**
	 * 执行实例add.before事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeWorkflowAddBeforeEvent(InputObject inputObject) throws ServiceRequestException;

	/**
	 * 执行实例add.after事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeWorkflowAddAfterEvent(InputObject inputObject) throws ServiceRequestException;

	/**
	 * 执行实例start.before事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeWorkflowStartBeforeEvent(InputObject inputObject) throws ServiceRequestException;

	/**
	 * 执行实例start.after事件
	 * 
	 * @param foundationObject
	 *            实例对象
	 * @return
	 * @throws ServiceRequestException
	 */
	public ScriptEvalResult executeWorkflowStartAfterEvent(InputObject inputObject) throws ServiceRequestException;

	/**
	 * 获取类的事件脚本
	 * 
	 * @param className
	 *            类名
	 * @param type
	 *            脚本类型
	 * @param segments
	 *            TODO
	 * @return
	 * @throws ServiceRequestException
	 */
	public Script getWorkflowEventScript(String workflowName, EventTypeEnum type, int... segments) throws ServiceRequestException;

	public Double calculate(String str) throws ServiceRequestException;

	/**
	 * @param str
	 * @param scale
	 * @return
	 * @throws ScriptException
	 */
	public Double calculate(String str, Integer scale) throws ServiceRequestException;

	/**
	 * @param str
	 * @param scale
	 * @return
	 * @throws ScriptException
	 */
	public String getScriptContent(String fileName) throws ServiceRequestException;

	/**
	 * @param str
	 * @param scale
	 * @return
	 * @throws ScriptException
	 */
	public void saveScriptContent(String fileName, String content) throws ServiceRequestException;

}
