/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScriptCallbackEnum
 * Wanglei 2011-7-15
 */
package dyna.common.bean.model;

/**
 * @author Wanglei
 * 
 */
@Deprecated
public enum ScriptCallbackEnum
{
	/**
	 * 脚本返回true
	 */
	NONE,

	// /**
	// * 返回false
	// */
	// SCRIPT_CANCEL,
	//
	// /**
	// * 返回true
	// */
	// SCRIPT_CONTINUE,
	//
	// /**
	// * 自定义脚本
	// */
	// SCRIPT_CUSTOM,

	/**
	 * 在客户端展现实例画面
	 */
	FRONT_DISPLAY_INSTANCE,

	// /**
	// * 在客户端执行调用
	// */
	// FRONT_CUSTOM
}
