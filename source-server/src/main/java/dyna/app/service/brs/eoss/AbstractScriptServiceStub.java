/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractScriptServiceStub 脚本服务分支的虚类
 * Wanglei 2011-3-28
 */
package dyna.app.service.brs.eoss;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.InputObject;
import dyna.common.bean.extra.ScriptEvalResult;
import dyna.common.bean.model.Script;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.DateFormat;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;
import dyna.net.script.ScriptExecutors;

/**
 * 脚本服务分支的虚类
 * 
 * @author Wanglei
 * 
 */
public abstract class AbstractScriptServiceStub extends AbstractServiceStub<EOSSImpl>
{
	private static final Map<String, Long>		scriptUpdateMap		= new HashMap<String, Long>();
	private static final Map<String, String>	scriptContentMap	= new HashMap<String, String>();

	/**
	 * @param context
	 * @param service
	 */
	public AbstractScriptServiceStub(ServiceContext context, EOSSImpl service)
	{
		super(context, service);
	}

	/**
	 * 获取脚本异常信息
	 * 
	 * @param scriptName
	 * @param message
	 * @return
	 */
	protected String getScriptErrorMessage(String scriptName, String message)
	{
		return MessageFormat.format("Error occurs invoking script: {0}, error: {1}", scriptName, message);
	}

	/**
	 * 获取脚本内容
	 * 
	 * @param script
	 * @return
	 */
	protected String getScriptContent(String className, Script script)
	{
		return StringUtils.convertNULLtoString(getScriptContent(script.getScriptFileName()));
	}

	/**
	 * 执行脚本
	 * 
	 * @param script
	 * @param inputObject
	 *            输入到脚本执行环境中的变量
	 * @return 脚本的执行结果
	 * @throws Exception
	 */
	protected ScriptEvalResult executeScript(String className, Script script, InputObject inputObject) throws Exception
	{
		if (script == null)
		{
			return null;
		}

		ScriptEvalResult retObject = null;
		String scriptName = script.getName();
		try
		{
			String content = this.getScriptContent(className, script);

			retObject = ScriptExecutors.executeScriptAtServer(script, content, inputObject, this.stubService, this.stubService.getUserSignature().getCredential());
		}
		catch (Exception e)
		{
			DynaLogger.error(this.getScriptErrorMessage(scriptName, e.getMessage()));
			throw e;
		}
		return retObject;
	}

	protected String getScriptContent(String fileName)
	{
		String scriptFileName = EnvUtils.getConfRootPath() + "/conf/script/" + fileName;

		File scriptFile = new File(scriptFileName);
		if (!scriptFile.exists())
		{
			return null;
		}
		String content = null;
		if (scriptUpdateMap.containsKey(fileName) == false || scriptUpdateMap.get(fileName) != scriptFile.lastModified())
		{
			try
			{
				content = FileUtils.readFromFile(scriptFile);
				scriptContentMap.put(fileName, content);

				scriptUpdateMap.put(fileName, scriptFile.lastModified());
			}
			catch (Exception e)
			{
				DynaLogger.error("read script error: " + e.getMessage());
			}
		}
		else
		{
			content = scriptContentMap.get(fileName);
		}

		return content;
	}

	public void saveScriptContent(String fileName, String content) throws ServiceRequestException
	{
		String scriptFileName = EnvUtils.getConfRootPath() + "/conf/script/" + fileName;
		File scriptFile = new File(scriptFileName);
		try
		{
			if (scriptFile.exists())
			{
				scriptFile.renameTo(new File(scriptFileName + "." + DateFormat.format(new Date(), "yyyyMMddHHmmss.SSS")));
				scriptFile = new File(scriptFileName);
			}
			FileUtils.writeToFile(scriptFile, content);

			scriptContentMap.put(fileName, content);

			scriptFile = new File(scriptFileName);

			scriptUpdateMap.put(fileName, scriptFile.lastModified());
		}
		catch (Exception e)
		{
			DynaLogger.error("write script error: " + e.getMessage());
		}
	}
}
