/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PMConstans
 * WangLHB May 15, 2012
 */
package dyna.common.util;

/**
 * 项目管理定数
 * 
 * @author WangLHB
 * 
 */
public class PMConstans
{
	/**
	 * ObjectGuid字段 master, classguid,的后缀，如字段名$MASTER,字段名$CLASS
	 */
	public static final String	MASTER						= "$MASTER";
	public static final String	CLASS						= "$CLASS";
	public static final String	NAME						= "$NAME";

	public static final String	TITLE						= "$TITLE";
	// 项目经理角色
	public static final String	PROJECT_MANAGER_ROLE		= "ManagerRole";

	// 观察者
	public static final String	PROJECT_OBSERVER_ROLE		= "receiver";

	// 总成型角色
	// public static final String PROJECT_ROLLUP_ROLE = "RollUpRole";

	public static final String	CALENDAR_STANDARD_ID		= "Standard$";
	public static final String	CALENDAR_STANDARD_NAME		= "Standard";

	// 日历定义
	public static final double	DEFALUT_WORK_HOUR			= 8;

	// 最大里程碑数量
	public static final int		MAX_MILESTONE_COUNT			= 10;

	public static final String	CONTENT_FIELD_SYMBO_START	= "#{";

	public static final String	CONTENT_FIELD_SYMBO_END		= "}#";

	// 最大spi
	public static final double	DEFALUT_SPI					= 2;

	public static String		COPY_TYPE_T2P				= "T2P";
	public static String		COPY_TYPE_T2T				= "T2T";
	public static String		COPY_TYPE_P2T				= "P2T";
	public static String		COPY_TYPE_P2P				= "P2P";

	public static String		RECEIVER_NOTICE_STRING		= "#{RECEIVER$}#";
	public static String		SENDER_NOTICE_STRING		= "#{SENDER$}#";
	public static String		PERCENT_NOTICE_STRING		= "#{PERCENT$}#";
	public static String		PRETASK_NOTICE_STRING		= "#{PRETASKNAME$}#";
}
