/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateEnum
 * Wanglei 2011-1-17
 */
package dyna.common.systemenum;

/**
 * 服务状态枚举
 * 
 * @author Wanglei
 * 
 */
public enum ServiceStateEnum
{

	/**
	 * 服务无效
	 */
	INVALID,

	/**
	 * 正常提供服务
	 */
	NORMAL,

	/**
	 * 服务暂停, 同步进行中
	 */
	WAITING,

	/**
	 * 服务暂停, 需要重新同步
	 */
	SYNCHRONIZE;
}
