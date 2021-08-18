/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassScriptStub 与模型类相关的脚本执行操作
 * Wanglei 2011-3-25
 */
package dyna.app.service.brs.eoss;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.Script;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.EventTypeEnum;
import dyna.common.util.SetUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 与模型类相关的脚本执行操作
 * 
 * @author Wanglei
 * 
 */
@Component
public class ClassScriptStub extends AbstractServiceStub<EOSSImpl>
{

	protected Script getActionScript(String className, String scriptName, int... segments) throws ServiceRequestException
	{
		Script script = null;
		// TODO duanllDataServer.getClassModelService().getActionScript(className, scriptName);
		return this.getScriptSegment(script, segments);
	}

	protected Script getEventScript(String className, EventTypeEnum type, int... segments) throws ServiceRequestException
	{
		Script script = null;
		// TODO duanll DataServer.getClassModelService().getEventScript(className, type);
		return this.getScriptSegment(script, segments);
	}

	protected Script getEventScript(String scriptName) throws ServiceRequestException
	{
		// TODO duanll return DataServer.getClassModelService().getScript(scriptName);
		return null;
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
