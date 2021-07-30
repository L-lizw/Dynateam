/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptResultTypeEnum
 * Wanglei 2011-7-14
 */
package dyna.common.bean.model;

/**
 * 脚本结果类型
 * 
 * @author Wanglei
 * 
 */
public enum ScriptResultTypeEnum
{
	/**
	 * 默认,无需前台处理返回的结果
	 */
	NONE,

	/**
	 * 提示消息,显示ok按钮
	 */
	OK_INFO,

	/**
	 * 提示警告消息,显示ok按钮
	 */
	OK_WARNNING,

	/**
	 * 提示错误消息,显示ok按钮
	 */
	OK_ERROR,

	/**
	 * 显示消息,并给出yes, no按钮
	 */
	YES_NO,

	/**
	 * 显示消息,并给出yes, no, cancel按钮
	 */
	YES_NO_CANCEL,

	/**
	 * 显示消息,并给出ok, cancel按钮
	 */
	OK_CANCEL,

	// /**
	// * 在客户端展现实例画面
	// */
	// FRONT_DISPLAY_INSTANCE,

	/**
	 * 在客户端执行调用
	 */
	CUSTOM_FRONT,

	/**
	 * 自定义选项
	 */
	CUSTOM_OPTION
}
