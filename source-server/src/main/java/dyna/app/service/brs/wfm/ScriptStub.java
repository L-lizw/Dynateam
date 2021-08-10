package dyna.app.service.brs.wfm;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.Script;
import dyna.common.systemenum.EventTypeEnum;

public class ScriptStub extends AbstractServiceStub<WFMImpl>
{

	protected ScriptStub(ServiceContext context, WFMImpl service)
	{
		super(context, service);
		// TODO Auto-generated constructor stub
	}

	public Script getScript(String scriptName)
	{
		return this.stubService.getWfModelCacheStub().getScript(scriptName);
	}

	public String getScriptContent(String processName, String activityName, Script script, boolean update)
	{
		return this.stubService.getWfModelCacheStub().getScriptContent(processName, activityName, script, update);
	}

	public Script getActionScript(String processName, String activityName, String scriptName)
	{
		return this.stubService.getWfModelCacheStub().getActionScript(processName, activityName, scriptName);
	}

	public Script getActionScript(String processName, String activityName)
	{
		return this.stubService.getWfModelCacheStub().getActionScript(processName, activityName);
	}

	public Script getEventScript(String processName, EventTypeEnum eventType)
	{
		return this.stubService.getWfModelCacheStub().getEventScript(processName, eventType);
	}

}
