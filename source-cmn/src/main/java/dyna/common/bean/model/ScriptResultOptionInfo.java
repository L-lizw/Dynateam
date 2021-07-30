/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptResultOptionInfo
 * Wanglei 2011-7-19
 */
package dyna.common.bean.model;

import java.io.Serializable;

/**
 * 脚本执行结果选项提示信息
 * 
 * @author Wanglei
 * 
 */
public class ScriptResultOptionInfo implements Serializable
{

	public static enum MessageTypeEnum
	{
		INFO, WARNING, ERROR, QUESTION
	}

	private static final long		serialVersionUID	= -4297195497504280468L;

	private MessageTypeEnum			msgType				= MessageTypeEnum.INFO;
	private String					title				= null;
	private String					message				= null;
	private ScriptResultTypeEnum	resultType			= null;
	private String[]				options				= null;
	private ScriptCallbackEnum[]	callbackEnums		= null;

	/**
	 * 脚本执行结果选项提示信息
	 * 
	 * @param title
	 *            标题
	 * @param message
	 *            提示的信息
	 * @param resultType
	 *            选项类型
	 */
	public ScriptResultOptionInfo(String title, String message, ScriptResultTypeEnum resultType, String[] options,
			ScriptCallbackEnum... callbackEnums)
	{
		this.title = title;
		this.message = message;
		this.resultType = resultType;
		this.callbackEnums = callbackEnums;
		this.options = options;
		this.init();
	}

	private void init()
	{
		switch (this.resultType)
		{
		case OK_INFO:
			this.msgType = MessageTypeEnum.INFO;
			break;

		case OK_WARNNING:
			this.msgType = MessageTypeEnum.WARNING;
			break;

		case OK_ERROR:
			this.msgType = MessageTypeEnum.ERROR;
			break;
		default:
			break;
		}
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * @return the resultType
	 */
	public ScriptResultTypeEnum getResultType()
	{
		return this.resultType;
	}

	/**
	 * @return the options
	 */
	public String[] getOptions()
	{
		return this.options;
	}

	/**
	 * @return the msgType
	 */
	public MessageTypeEnum getMsgType()
	{
		return this.msgType;
	}

	public void setMsgType(MessageTypeEnum msgType)
	{
		this.msgType = msgType;
	}

	/**
	 * 根据所选项返回callback类型
	 * 
	 * @param option
	 * @return
	 */
	public ScriptCallbackEnum getScriptCallbackEnumForOption(int option)
	{
		if (this.callbackEnums == null || option < 0 || option > this.callbackEnums.length - 1)
		{
			return null;
		}
		return this.callbackEnums[option];
	}

	/**
	 * 获取选项 数目
	 */
	public int getOptionCount()
	{
		return this.options == null ? 0 : this.options.length;
	}
}
