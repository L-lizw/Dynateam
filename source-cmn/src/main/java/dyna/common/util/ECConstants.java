/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 工程变更常量类
 * caogc 2011-4-03
 */
package dyna.common.util;

/**
 * ECConstants 工程变更常量类
 * 
 * 用于记录工程变更中用到的所有常量
 * 
 * @author caogc
 * 
 */
public class ECConstants
{

	/**
	 * 表示在ECR,ECO,ECN等类中所对应的用于关联EC类的Object类型的字段名字的静态常量 在IECR,IECO,IECN接口中需要定义该字段
	 */
	public static final String	EC					= "EC";

	/**
	 * 表示在EC类中所对应的用于关联EC模板Guid的字段名字的静态常量 在IEC接口中需要定义该字段
	 */
	public static final String	EC_TEMPLATE			= "ECTTemplate";

	/**
	 * 表示在ECO及ECT类中所对应的用于关联ECN类的Object类型的字段名字的静态常量 在IECO及IECT接口中需要定义该字段
	 */
	public static final String	ECN					= "ECN";

	/**
	 * 表示在Foundation实例中用于记录该实例是否作为问题对象在变更中的字段名字的静态常量<br>
	 * 该字段是Object类型的字段，用于存储是被哪个EC锁定的，如果为空说明没有被锁定
	 */
	public static final String	EC_FLAG				= "ECFLAG$";

	/**
	 * 表示在ECR,ECO所对应类中用于记录计划完成时间的字段名字的静态常量 IECR,IECO,IECT,IECA接口中内置该字段
	 */
	public static final String	PLAN_TIME			= "PlanTime";

	/**
	 * 表示在EC对应类中用于记录变更所处的状态的字段名字的静态常量
	 */
	public static final String	EC_STATUS			= "ECStatus";

	/**
	 * 表示在PR(问题报告)对应类中用于记录该问题报告所处的状态的字段名字的静态常量
	 * 
	 * 问题报告包含三种状态（初始状态，变更中，关闭），该字段值为空时为初始状态
	 * 
	 */
	public static final String	PR_STATUS			= "PRStatus";

	/**
	 * 表示在ECT所对应类中用于记录"完成时间"的字段名字的静态常量 IECT,IECA接口中内置该字段
	 */
	public static final String	COMPLETE_TIME		= "CompleteTime";

	/**
	 * 表示在ECT所对应类中用于记录"负责人"的字段名字的静态常量 IECT接口中内置该字段
	 */
	public static final String	RESPONSIBLE			= "Responsible";

	/**
	 * 表示在ECT所对应类中用于记录"处理人"的字段名字的静态常量 IECT、IECA接口中内置该字段
	 */
	public static final String	EXECUTOR			= "Executor";

	/**
	 * 表示在ECT所对应类中用于记录"强制"的字段名字的静态常量 IECT接口中内置该字段
	 */
	public static final String	MANDATORY			= "IsMandatory";

	/**
	 * 表示在ECT所对应类中用于记录"是否完成"的字段名字的静态常量 IECT接口中内置该字段
	 */
	public static final String	IS_COMPLETE			= "IsCompleted";

	/**
	 * 表示在ECN所对应类中用于记录"是否关闭"的字段名字的静态常量 IECN接口中内置该字段
	 */
	public static final String	IS_CLOSED			= "IsClosed";

	/**
	 * 表示在EC及ECN所对应类中用于记录"关闭时间"的字段名字的静态常量 IEC,IECN接口中内置该字段
	 */
	public static final String	CLOSE_TIME			= "CloseTime";

	/**
	 * 前台要求根据ec取出ECR的时候 要把ECR实例所在的PHASE是否大于等于模板上配置的ECR的PHASE存入返回实例中<br>
	 * 如果大于等于那么返回true否则返回false
	 * 
	 */
	public static final String	ECR_PHASE			= "ECRPHASE";

	/**
	 * 前台要求根据ec取出ECR的时候 要把ECR实例所在的ECR PLAN PHASE是否大于等于模板上配置的ECR的PLAN
	 * PHASE存入返回实例中<br>
	 * 如果大于等于那么返回true否则返回false
	 * 
	 */
	public static final String	ECR_PLAN_PHASE		= "ECRPLANPHASE";

	/**
	 * 前台要求根据ec取出ECO的时候 要把ECO实例所在的PHASE是否大于等于模板上配置的ECO的PHASE存入返回实例中<br>
	 * 如果大于等于那么返回true否则返回false
	 * 
	 */
	public static final String	ECO_PHASE			= "ECOPHASE";

	/**
	 * 前台要求根据ec取出ECN的时候 要把ECN实例所在的PHASE是否大于等于模板上配置的ECN的PHASE存入返回实例中<br>
	 * 如果大于等于那么返回true否则返回false
	 * 
	 */
	public static final String	ECN_PHASE			= "ECNPHASE";

	/**
	 * ECA上使用 用以记录要变更的问题对象 IECA,IPR接口上有该内置字段
	 * 
	 */
	public static final String	PI					= "PI";

	/**
	 * 用以记录父ECA对应的对象的GUID
	 * 
	 */
	public static final String	PARENT				= "Parent";

	/**
	 * IECA上增加Priority字段（Integer），代表ECA的优先级，默认为1，该值越大，优先级越高
	 * 
	 */
	public static final String	PRIORITY			= "Priority";

	/**
	 * ECA上用以记录ECA对应的ECR实例的GUID
	 * 
	 */
	public static final String	ECR					= "ECR";

	/**
	 * ECA上用以记录ECA对应的ECO实例的GUID
	 * 
	 */
	public static final String	ECO					= "ECO";

	/**
	 * 用以记录ECA的变更类别
	 * 
	 */
	public static final String	CHANGETYPE			= "Changetype";

	/**
	 * 用以记录ECA的变更内容
	 * 
	 */
	public static final String	CONTENT				= "Content";

	/**
	 * 用以记录ECA的解决方案对应的对象的GUID
	 * 
	 */
	public static final String	SI					= "SI";

	/**
	 * 修改BOM时，用以记录原始BOMView ObjectGuid
	 * 
	 */
	public static final String	ORIGINALBOM			= "OriginalBOM";

	/**
	 * ECA上用以记录ECA所对应的状态的字段
	 * 
	 */
	public static final String	ECASTATUS			= "ECAstatus";

	/**
	 * ECA上问题报告所对应的字段 用以记录所对应的问题报告实例的GUID
	 * 
	 */
	public static final String	PR					= "PR";

	/**
	 * 用以记录ECA上 变更类别为BOM时的NAME
	 * 
	 */
	public static final String	ECA_BOM				= "ECABOM";

	/**
	 * 用以记录ECA变更类别为替换时 在ECR阶段选择的替换件 如果该字段有值那么在ECO阶段不可修改"替换件"
	 * 
	 */
	public static final String	ECA_REPLACE_ITEM	= "ECAReplaceItem";

	/**
	 * IECR接口上的内置字段的名字
	 * 
	 */
	public static final String	EC_ADMINISTRATOR	= "ECadministrator";

	/**
	 * 
	 * 用来记录ECA是否能够分解
	 */
	public static final String	ECA_UPDATE_STATUS	= "ECAUPDATESTATUS";

	/**
	 * 
	 * IECT接口上的内置字段【描述】
	 */
	public static final String	ECT_DESCRIPTION		= "DESCRIPTION";

}
