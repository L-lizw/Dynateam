/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceStructure
 * wangweixia 2012-7-16
 */
package dyna.common.util;

/**
 * 工程变更静态常量
 * 
 * @author wangweixia
 * 
 */
public class UpdatedECSConstants
{
	// 申请人
	public static final String	Applier					= "Applier";
	// 父级ECP
	public static final String	ParentECP				= "ParentECP";
	// 主题
	public static final String	Subject					= "Subject";
	// 分析资源类型
	public static final String	AnaysisSourceType		= "AnaysisSourceType";
	// 变更对象
	public static final String	ChangeItem				= "ChangeItem";
	// ECO
	public static final String	ECO						= "ECO";

	// ECN
	public static final String	ECN						= "ECN";

	// ECR
	public static final String	ECR						= "ECR";
	// 一般变更和批量变更
	public static final String	ECType					= "ECType";
	// 修改类型:修改属性，修改文件，修改关联关系，其他
	public static final String	ContentType				= "ContentType";
	// 变更内容
	public static final String	ModifyContent			= "ModifyContent";
	// 描述
	public static final String	Description				= "Description";
	// ECP
	public static final String	ECP						= "ECP";
	// 修改类型:批量新增，批量修改，批量删除，批量取代
	public static final String	ModifyType				= "ModifyType";
	// BOM模板
	public static final String	BOMTemplate				= "BOMTemplate";
	// 取代方式:自然取代，强制取代
	public static final String	Replacepolicy			= "Replacepolicy";
	// 取代对象
	public static final String	TargetItem				= "TargetItem";
	// 其他BOM结构属性
	public static final String	BOMInfo					= "BOMInfo";
	// BOMInfo1
	public static final String	BOMInfo1				= "BOMInfo1";
	public static final String	BOMInfo2				= "BOMInfo2";
	// ECO上下级关系
	public static final String	ParentECO				= "ParentECO";
	// 执行者
	public static final String	Performer				= "Performer";
	// 解决对象
	public static final String	SolveItem				= "SolveItem";
	// 解决对象
	public static final String	ECTargetItem			= "ECTargetItem";
	// 变更子对象
	public static final String	SubChangeBO				= "SubChangeBO";

	// 变更类型
	public static final String	ChangeType				= "ChangeType";
	// 关系/BOM模板
	public static final String	Template				= "Template";
	// 变更方式:新增，删除，修改，取代
	public static final String	ActionType				= "ActionType";
	// 变更内容
	public static final String	Attribute				= "Attribute";
	// 变更值
	public static final String	Value1					= "Value1";

	public static final String	Value2					= "Value2";
	public static final String	Value3					= "Value3";

	// ecp是否有效
	public static final String	valid					= "valid";

	// CLASS子串
	public static final String	ClassGuid				= "$CLASS";
	// CLASSNAME子串
	public static final String	CLASSNAME				= "$CLASSNAME";
	// MASTER子串
	public static final String	MASTER					= "$MASTER";

	// guid
	public static final String	guid					= "GUID";
	// guid
	public static final String	guid$					= "GUID$";
	// masterGuid
	public static final String	masterGuid				= "MASTERFK";
	// classGuid
	public static final String	classGuid				= "CLASSGUID";

	// eci属性变更,变更内容
	public static final String	eciContent				= "ECICONTENT";
	// eci属性变更,变更内容，变更前
	public static final String	eciEditBefore			= "ECIEDITBEFORE";
	// eci属性变更,变更内容，变更后
	public static final String	eciEditAfter			= "ECIEDITAFTER";
	// eci的actiontype的code对应的enum
	public static final String	eciActionTypeEnum		= "ECIACTIONTYPEENUM";
	// eco是否完成
	public static final String	isCompleted				= "isCompleted";
	// ecn是否与eco一起启动
	public static final String	isAssoicatedECO			= "isAssoicatedECO";

	// 固定的流程常量名字
	public static final String	wfECNECOReview			= "ECNECOReview";
	// 固定的流程常量名字
	public static final String	wfECNECOCombineRelease	= "ECNECOCombineRelease";
	// 固定的流程常量名字
	public static final String	wfECORelease			= "ECORelease";
	// 固定的流程常量名字
	public static final String	wfECNRelease			= "ECNRelease";

	// ecpContent
	public static final String	ECP_CONTENT				= "ECPCONTENT";

	// ecoContent
	public static final String	ECO_CONTENT				= "ECOCONTENT";

	// eci
	public static final String	ECI						= "ECI";

	/**
	 * 2014-2-27日讨论新增
	 * 
	 */
	// 各种关系模板名称
	// ecn-eco:ECN-ECO$,
	public static final String	ECN_ECO$				= "ECN-ECO$";
	// ecr-ecn：ECN -ECR$,
	public static final String	ECN_ECR$				= "ECN-ECR$";
	// Ecr-ecp：ECR-ECP$,
	public static final String	ECR_ECP$				= "ECR-ECP$";
	// ecp-eco：ECP-ECO$,
	public static final String	ECP_ECO$				= "ECP-ECO$";
	// ecp-ecpcontent：ECP-ECPCONTENT$,
	public static final String	ECP_ECPCONTENT$			= "ECP-ECPCONTENT$";
	// ecp-变更前对象:ECP-CHANGEITEMBEFORE$,
	public static final String	ECP_CHANGEITEMBEFORE$	= "ECP-CHANGEITEMBEFORE$";
	// ecp-变更对象:ECP-CHANGEITEM$,
	public static final String	ECP_CHANGEITEM$			= "ECP_CHANGEITEM$";
	// eco-ecocontent:ECO-ECOCONTENT$,
	public static final String	ECO_ECOCONTENT$			= "ECO-ECOCONTENT$";
	// eco-变更前对象:ECO-CHANGEITEMBEFORE$,
	public static final String	ECO_CHANGEITEMBEFORE$	= "ECO-CHANGEITEMBEFORE$";
	// eco-变更后对象:ECO-CHANGEITEMAFTER$,
	public static final String	ECO_CHANGEITEMAFTER$	= "ECO-CHANGEITEMAFTER$";
	// eco-变更对象:ECO-CHANGEITEM$,
	public static final String	ECO_CHANGEITEM$			= "ECO_CHANGEITEM$";
	// eco-eci:ECO-ECI$
	public static final String	ECO_ECI$				= "ECO-ECI$";

	// eco-变更前对象的关系结构上，增加3个字段
	public static final String	ECChangeRecord1			= "ECChangeRecord1";
	public static final String	ECChangeRecord2			= "ECChangeRecord2";
	public static final String	ECChangeRecord3			= "ECChangeRecord3";

	// 取代率
	public static final String	replaceRate				= "replaceRate";
	// 顺序
	public static final String	Sequence				= "DATASEQ";
	// 数量
	public static final String	Quantity				= "Quantity";
}
