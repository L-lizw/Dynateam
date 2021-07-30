/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SysTrack
 * Wanglei 2011-11-14
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObjectImpl;

/**
 * 系统操作日志
 * 
 * @author Wanglei
 * 
 */
public class SysTrack extends SystemObjectImpl
{
	private static final long	serialVersionUID	= -3715859366839286488L;

	public static final String	SID					= "SID";
	public static final String	BY_WHOM				= "BYWHOM";
	public static final String	AT_WHERE			= "ATWHERE";
	public static final String	DO_WHAT				= "DOWHAT";
	public static final String	TG_OBJECT			= "TGOBJECT";
	public static final String	RESULT				= "RESULT";

	public static final String	SEARCH_FROM			= "CREATETIMESTART";
	public static final String	SEARCH_TO			= "CREATETIMEEND";
}
