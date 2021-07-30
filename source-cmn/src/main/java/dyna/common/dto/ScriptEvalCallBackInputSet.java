/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 执行脚本打开编辑器参数类
 * Liuzt 2012-8-1
 */
package dyna.common.dto;

import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author Liuzt
 * 
 */
public class ScriptEvalCallBackInputSet extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -7228159410436591827L;

	// 是否是打开模式对话框，true：模式对话框，false：编辑器
	private boolean				isDialog			= false;
	// 编辑器ID
	private String				inputID				= null;
	// 编辑器传值
	private Map<String, Object>	inputArgs			= null;

	public ScriptEvalCallBackInputSet()
	{
		super();
	}

	public ScriptEvalCallBackInputSet(boolean isDialog, String inputID, Map<String, Object> inputArgs)
	{
		super();
		this.isDialog = isDialog;
		this.inputID = inputID;
		this.inputArgs = inputArgs;
	}

	/**
	 * @return the isDialog
	 */
	public boolean isDialog()
	{
		return this.isDialog;
	}

	/**
	 * @param isDialog
	 *            the isDialog to set
	 */
	public void setDialog(boolean isDialog)
	{
		this.isDialog = isDialog;
	}

	/**
	 * @return the inputID
	 */
	public String getInputID()
	{
		return this.inputID;
	}

	/**
	 * @param inputID
	 *            the inputID to set
	 */
	public void setInputID(String inputID)
	{
		this.inputID = inputID;
	}

	/**
	 * @return the inputArgs
	 */
	public Map<String, Object> getInputArgs()
	{
		return this.inputArgs;
	}

	/**
	 * @param inputArgs
	 *            the inputArgs to set
	 */
	public void setInputArgs(Map<String, Object> inputArgs)
	{
		this.inputArgs = inputArgs;
	}
}
