/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceStructure
 * wangweixia 2012-7-16
 */
package dyna.common.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.systemenum.ReplaceStatusEnum;

/**
 * 取替代结构
 * 
 * @author Wangweixia
 * 
 */
public class ReplaceSubstituteConstants
{
	public static final String	REPLACE_CLASS_NAME				= "Substitute";
	/*
	 * 取替代对象字段
	 */
	// 主件的ObjectGuid
	public static final String	MasterItem						= "MasterItem";
	// 元件的ObjectGuid
	public static final String	ComponentItem					= "ComponentItem";
	// 替代件的ObjectGuid
	public static final String	RSItem							= "RSItem";
	// 序号
	public static final String	RSNumber						= "RSNumber";
	// 元件用量
	public static final String	ComponentUsage					= "ComponentUsage";
	// 类型 ReplaceTypeEnum
	public static final String	RSType							= "RSType";
	// 类型 ReplaceRangeEnum
	public static final String	RSRange							= "RSRange";
	// 范围ReplaceRangeEnum
	public static final String	Scope							= "Scope";
	// 取替代BOM模板名称
	public static final String	BOMViewDisplay					= "BOMVIEWDISPLAY";
	// BOM模板Name
	public static final String	BOMViewDisplayName				= "BOMVIEWDISPLAY$NAME";
	// 生效日期
	public static final String	EffectiveDate					= "EffectiveDate";
	// 失效日期
	public static final String	InvalidDate						= "InvalidDate";
	// CLASS子串
	public static final String	ClassGuid						= "$CLASS";
	// CLASSNAME子串
	public static final String	CLASSNAME						= "$CLASSNAME";
	// MASTER子串
	public static final String	MASTER							= "$MASTER";

	public static final String	isHasReplace					= "ISHASREPLACE";
	// 记录元件位置的GUID
	public static final String	BOMKey							= "BOMKey";
	// 记录元件的位置
	public static final String	Sequence						= "DATASEQ";
	/**
	 * 标记当前正在“使用”的取替代，这里的“使用”包含了生效、尚未生效和失效等所有的状态
	 * 区分使用与不使用取决于取替代主件物料的版本：主件物料为最新版本，取替代就是正在“使用”；主件物料为历史版本，那么取替代就是“非使用”
	 * 在取替代搜索不设置主件条件时，通过该字段可过滤掉历史取替代数据
	 */
	public static final String	IN_USE							= "InUse";
	// 取替代对象的状态:SystemStatusEnum
	public static final String	status							= "status$";
	// 取替代创建时比较的基准日期，为空时取替代生效和失效日期比较sysdate，替代申请单放替代申请单创建时的日期
	public static final String	COMPARE_DATE					= "COMPARE_DATE";
	/*
	 * 取替代表单、结构字段
	 */
	// 表单类型
	public static final String	FORM_TYPE						= "FormType";
	// 错误详情
	public static final String	EXCEPTION_DETAIL				= "ExceptionDetail";
	// 流程结果
	public static final String	PROCESS_RESULT					= "ProcessResult";
	// BOM序号
	public static final String	BOM_SEQUENCE					= "BOMSequence";
	// 元件
	public static final String	STRUCTURE_COMPONENT_ITEM		= "STRUCTURE$COMPONENTITEM";
	// 替代件
	public static final String	STRUCTURE_RS_ITEM				= "STRUCTURE$RSITEM";
	// 取替代对象
	public static final String	REPLACE_DATA					= "ReplaceData";

	/**
	 * 定义搜索用到的某些Key
	 */
	// 取替代对象标识
	public static final String	searchObject					= "M";
	// 主件对象标识
	public static final String	searchMasterItem				= "MI";
	// 元件对象标识
	public static final String	searchComponentItem				= "CI";
	// 替代件对象标识
	public static final String	searchRSItem					= "RI";
	// Id
	public static final String	searchID						= "ID$";
	// alterId
	public static final String	searchAlterId					= "ALTERID$";
	// name
	public static final String	searchName						= "NAME$";
	// revisionId
	public static final String	searchRevisionId				= "REVISIONID$";
	// iterationId
	public static final String	searchIterationId				= "ITERATIONID$";
	// classification
	public static final String	searchClassification			= "CLASSIFICATION$";
	// fileName
	public static final String	searchFilename					= "FILENAME";
	// 取替代对象的ObjectGuid
	public static final String	replaceObjectGuid				= "REPLACEOBJECTGUID";

	// guid
	public static final String	guid							= "GUID";
	// masterGuid
	public static final String	masterGuid						= "MASTERFK";
	// classGuid
	public static final String	classGuid						= "CLASSGUID";

	/**
	 * 除预定义搜索界面用到的某些Key
	 */
	// 替代件Id
	public static final String	RSItemId						= "RSITEMID";
	// 替代件AlterId
	public static final String	RSItemAlterId					= "RSITEMALTERID";
	// 替代件Name
	public static final String	RSItemName						= "RSITEMNAME";
	// 替代件RevisionId
	public static final String	RSItemRevisionId				= "RSITEMREVISIONID";
	// 替代件IterationId
	public static final String	RSItemIterationId				= "RSITEMITERATIONID";
	// 替代件Classfication
	public static final String	RSItemClassification			= "RSITEMCLASSIFICATION";
	// 替代件fileName
	public static final String	RSItemFilename					= "RSITEMFILENAME";
	// 替代件fullName
	public static final String	RSItemFullname					= "RSITEM$NAME";

	// 编号
	public static final String	RSID							= "@ID";
	// 名称
	public static final String	NAME							= "@NAME";
	// 规格
	public static final String	SPECIFICATION					= "@SPECIFICATION";
	// 版本
	public static final String	REVISION						= "@REVISIONID";

	// 批量取替代与主件的关系名称
	@Deprecated
	public static final String	BatchRS_Master					= "BatchRS-Master";

	// 取替代申请单与物料的关系名称
	public static final String	BATCHRS_ITEM					= "BatchRS-Item";

	// 批量取替代新增申请的关联关系模板
	@Deprecated
	public static final String	BatchRS_RSRelationship			= "BatchRS-RSRelationship";

	// 批量取替代废弃申请的关联关系模板
	public static final String	BatchRS_RSRelationship_Obsolete	= "BatchRS-RSRelationship-Obsolete";

	// 批量取替代申请类型
	public static final String	TYPE_ADD						= "ADD";
	public static final String	TYPE_ALTER						= "ALTER";
	public static final String	TYPE_OBSOLETE					= "OBSOLETE";

	public static List<FoundationObject> getReplaceDataByStatus(List<FoundationObject> listFoundationObject, ReplaceStatusEnum status)
	{
		List<FoundationObject> listReplaceData = new ArrayList<FoundationObject>();
		if (!SetUtils.isNullList(listFoundationObject))
		{
			for (FoundationObject foundationObject : listFoundationObject)
			{
				if (status == getReplaceDataStatus(foundationObject))
				{
					listReplaceData.add(foundationObject);
				}
			}
		}
		return listReplaceData;
	}

	/**
	 * 判断取替代数据的状态
	 * 
	 * @param replaceData
	 * @param status
	 * @return
	 */
	public static ReplaceStatusEnum getReplaceDataStatus(FoundationObject replaceData)
	{
		Date effectiveDate = (Date) replaceData.get(ReplaceSubstituteConstants.EffectiveDate);
		Date invalidDate = (Date) replaceData.get(ReplaceSubstituteConstants.InvalidDate);

		/*
		 * 未激活
		 * 1记录“生效时间”为空的
		 * 2记录“生效时间”有值，且晚于系统当前日期
		 * 3除去生效日期=失效日期=当前日期
		 */
		if (replaceData.get(ReplaceSubstituteConstants.EffectiveDate) == null
				|| DateFormat.compareDate((Date) replaceData.get(ReplaceSubstituteConstants.EffectiveDate), DateFormat.getSysDate()) > 0)
		{
			if (!(invalidDate != null && effectiveDate != null && DateFormat.compareDate(effectiveDate, invalidDate) == 0
					&& DateFormat.compareDate(invalidDate, DateFormat.getSysDate()) == 0))
			{
				return ReplaceStatusEnum.UNACTIVATED;
			}
		}

		/*
		 * 生效：
		 * 1 记录“生效时间”有值，“失效时间”为空，且“生效时间”早于系统当前日期
		 * 2 记录“生效时间”有值，且“生效时间”早于系统当前日期，“失效时间”有值，且“失效时间”晚于等于系统当前日期
		 */
		if (effectiveDate != null)
		{
			if ((invalidDate == null && DateFormat.compareDate(effectiveDate, DateFormat.getSysDate()) <= 0) || (invalidDate != null
					&& DateFormat.compareDate(effectiveDate, DateFormat.getSysDate()) <= 0 && DateFormat.compareDate(invalidDate, DateFormat.getSysDate()) >= 0))
			{
				return ReplaceStatusEnum.EFFECTIVE;
			}
		}

		/*
		 * 失效
		 * 1记录“失效时间”有值，且早于系统当前日期
		 * 2生效日期=失效日期=当前日期
		 */
		if ((invalidDate != null && DateFormat.compareDate(invalidDate, DateFormat.getSysDate()) < 0) || (invalidDate != null && effectiveDate != null
				&& DateFormat.compareDate(effectiveDate, invalidDate) == 0 && DateFormat.compareDate(invalidDate, DateFormat.getSysDate()) == 0))
		{
			return ReplaceStatusEnum.EXPIRE;
		}

		return null;
	}

}
