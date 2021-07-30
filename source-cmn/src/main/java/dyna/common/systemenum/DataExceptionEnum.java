/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 数据层exception
 * JianghL，2010-9-30
 */
package dyna.common.systemenum;

/**
 * 数据层异常枚举
 *
 * @author JiangHL
 */
public enum DataExceptionEnum
{
	/*
	 * for DataService
	 */
	/**
	 * 初始化构造函数异常
	 */
	DS_INIT("1", "ID_DS_INIT"), //

	/**
	 * 找不到指定的字段
	 */
	DS_NO_FIELD("2", "ID_DS_NO_FIELD"), //

	/**
	 * 没有检出权限
	 */
	DS_NO_CHECKOUT_AUTH("3", "ID_DS_NO_CHECKOUT_AUTH"), //

	/**
	 * 取消检出异常,当前数据未被检出或已经被修改过或已经被删除。
	 */
	DS_CANCEL_CHECKOUT_DATA_LOST("4", "ID_DS_CANCEL_CHECKOUT_DATA_LOST"), //

	/**
	 * 取消检出异常
	 */
	DS_CANCEL_CHECKOUT("5", "ID_DS_CANCEL_CHECKOUT"), //

	/**
	 * 没有检入权限
	 */
	DS_NO_CHECKIN_AUTH("6", "ID_DS_NO_CHECKIN_AUTH"), //

	/**
	 * 检入异常,当前数据未被检出或已经被修改或已经被删除。
	 */
	DS_CHECKIN_DATA_LOST("7", "ID_DS_CHECKIN_DATA_LOST"), //

	/**
	 * 检入异常
	 */
	DS_CHECKIN("8", "ID_DS_CHECKIN"), //

	/**
	 * 检出异常,当前数据未被检入或已经被修改或已经被删除。
	 */
	DS_CHECKOUT_DATA_LOST("9", "ID_DS_CHECKOUT_DATA_LOST"), //

	/**
	 * 检出异常
	 */
	DS_CHECKOUT("10", "ID_DS_CHECKOUT"), //

	/**
	 * 数据提交异常
	 */
	DS_COMMIT_TRANSACTION("11", "ID_DS_COMMIT_TRANSACTION"), //

	/**
	 * 没有创建权限
	 */
	DS_NO_CREATE_AUTH("12", "ID_DS_NO_CREATE_AUTH"), //

	/**
	 * 创建foundation异常
	 */
	DS_CREATE_FOUNDATION("13", "ID_DS_CREATE_FOUNDATION"), //

	/**
	 * 创建structureObject异常
	 */
	DS_CREATE_STRUCTURE("14", "ID_DS_CREATE_STRUCTURE"), //

	/**
	 * 没有删除权限
	 */
	DS_NO_DELETE_AUTH("15", "ID_DS_NO_DELETE_AUTH"), //

	/**
	 * 最后一条关联关系
	 */
	DS_LAST_REFERENCE("142", "ID_DS_LAST_REFERENCE"), //

	/**
	 * 删除viewObject异常,当前对象被修改或已经被删除
	 */
	DS_DELETE_VIEWOBJECT_DATA_LOST("16", "ID_DS_DELETE_VIEWOBJECT_DATA_LOST"), //

	/**
	 * 删除foundation异常,当前对象被修改或已经被删除或不是WIP状态,或对象已经被检出
	 */
	DS_DELETE_FOUNDATION_DATA_LOST("17", "ID_DS_DELETE_FOUNDATION_DATA_LOST"), //

	/**
	 * 删除view时异常
	 */
	DS_DELETE_VIEW_DATA_LOST("143", "ID_DS_DELETE_VIEW_DATA_LOST"), //

	/**
	 * 删除foundation或viewObject异常
	 */
	DS_DELETE_FOUNDATION("18", "ID_DS_DELETE_FOUNDATION"), //

	/**
	 * 删除structure异常
	 */
	DS_DELETE_STRCTURE("19", "ID_DS_DELETE_STRCTURE"), //

	/**
	 * 移交检出异常,当前数据没有被检出或已经被修改或已经被删除
	 */
	DS_TRANS_CHECKOUT_DATA_LOST("20", "ID_DS_TRANS_CHECKOUT_DATA_LOST"), //

	/**
	 * 移交权限异常
	 */
	DS_ONLY_MYSELF_TRANS("21", "ID_DS_ONLY_MYSELF_TRANS"), //

	/**
	 * 移交权限异常
	 */
	DS_TRANS_CHECKOUT("22", "ID_DS_TRANS_CHECKOUT"), //

	/**
	 * 执行存储过程时异常
	 */
	DS_EXECUTE_FUNCTION("23", "ID_DS_EXECUTE_FUNCTION"), //

	/**
	 * 用存储过程获取BOMStructure时异常
	 */
	DS_EXECUTE_SELECT_BOM_STRUCTURE("24", "ID_DS_EXECUTE_SELECT_BOM_STRUCTURE"), //

	/**
	 * 用存储过程获取FoundationObject时异常
	 */
	DS_EXECUTE_SELECT_FUNCTION("25", "ID_DS_EXECUTE_SELECT_FUNCTION"), //

	/**
	 * 用存储过程获取ViewObject时异常
	 */
	DS_EXECUTE_SELECT_VIEW_OBJECT("26", "ID_DS_EXECUTE_SELECT_VIEW_OBJECT"), //

	/**
	 * 用存储过程获取StructureObject时异常
	 */
	DS_EXECUTE_SELECT_STRUCTURE("27", "ID_DS_EXECUTE_SELECT_STRUCTURE"), //

	/**
	 * 找不到session异常
	 */
	DS_NOT_FOUND_SESSION("28", "ID_DS_NOT_FOUND_SESSION"), //

	/**
	 * 删除foundation时异常
	 */
	DS_UPDATE_LAST_REVISION("29", "ID_DS_UPDATE_LAST_REVISION"), //

	/**
	 * 根据view和end2查找STRUCTURE时异常
	 */
	DS_SELECT_STRUCTUREOBJECT_WITH_VIEW_END2("30", "ID_DS_SELECT_STRUCTUREOBJECT_WITH_VIEW_END2"), //

	/**
	 * 没有读权限
	 */
	DS_NO_READ_AUTH("31", "ID_DS_NO_READ_AUTH"), //

	/**
	 * 根据view和end2查找STRUCTURE时异常
	 */
	DS_SELECT_BOMSTRUCTURE_WITH_VIEW_END2("32", "ID_DS_SELECT_BOMSTRUCTURE_WITH_VIEW_END2"), //

	/**
	 * 移动foundation到其他文件夹时异常
	 */
	DS_MOVE_TO_FOLDER("33", "ID_DS_MOVE_TO_FOLDER"), //

	/**
	 * 没有obsolete权限
	 */
	DS_NO_OBSOLETE_AUTH("34", "ID_DS_NO_OBSOLETE_AUTH"), //

	/**
	 * obsolete异常,当前数据已经被废除或数据不存在
	 */
	DS_OBSOLETE_DATA_LOST("35", "ID_DS_OBSOLETE_DATA_LOST"), //

	/**
	 * obsolete时异常
	 */
	DS_OBSOLETE("36", "ID_DS_OBSOLETE"), //

	/**
	 * searchCondition条件为空
	 */
	DS_SEARCHCONDITION_IS_NULL("37", "ID_DS_SEARCHCONDITION_IS_NULL"), //

	/**
	 * searchCondition中的criterionlist为空
	 */
	DS_CRITERIONLIST_IS_NULL("38", "ID_DS_CRITERIONLIST_IS_NULL"), //

	/**
	 * rollbackTransaction时异常
	 */
	DS_ROLLBACK_TRANSACTION("39", "ID_DS_ROLLBACK_TRANSACTION"), //

	/**
	 * 保存foundation的revision时异常,当前数据已经被修改或已经被删除
	 */
	DS_SAVE_FOUNDATION_REVISION("40", "ID_DS_SAVE_FOUNDATION_REVISION"), //

	/**
	 * 保存foundation的revision_gen时异常,当前数据已经被修改或已经被删除
	 */
	DS_SAVE_FOUNDATION_REVISION_GEN("41", "ID_DS_SAVE_FOUNDATION_REVISION_GEN"), //

	/**
	 * 保存foundation时异常
	 */
	DS_SAVE_FOUNDATION("42", "ID_DS_SAVE_FOUNDATION"), //

	/**
	 * 实际值太大
	 */
	DS_VALUE_TOO_LARGE("129", "ID_DS_VALUE_TOO_LARGE"), //

	/**
	 * 获取唯一id异常
	 */
	DS_ALLOCATEUNIQUEID("130", "ID_DS_ALLOCATEUNIQUEID"), //

	/**
	 * 只能删除最新版
	 */
	DS_DELETE_LASTER_REVISION_ONLY("131", "ID_DS_DELETE_LASTER_REVISION_ONLY"), //

	/**
	 * 字段重复
	 */
	DS_UNIQUE("133", "ID_DS_UNIQUE"), //

	/**
	 * 只能有一个修订版
	 */
	DS_ONLY_ONE_REVISION("134", "ID_DS_ONLY_ONE_REVISION"), //

	/**
	 * 保存foundation的特殊字段时异常
	 */
	DS_SAVE_FOUNDATION_PARAM("43", "ID_DS_SAVE_FOUNDATION_PARAM"), //

	/**
	 * 保存foundation的特殊字段时异常
	 */
	DS_SAVE_FOUNDATION_SPECIAL_DATA_LOST("44", "ID_DS_SAVE_FOUNDATION_SPECIAL_DATA_LOST"), //

	/**
	 * 保存foundation的特殊字段时异常
	 */
	DS_SAVE_FOUNDATION_SPECIAL("45", "ID_DS_SAVE_FOUNDATION_SPECIAL"), //

	/**
	 * 没有权限
	 */
	DS_NO_AUTH("46", "ID_DS_NO_AUTH"), //

	/**
	 * 保存StructureObject或StructureObject_gen时异常
	 */
	DS_DATA_LOST("47", "ID_DS_DATA_LOST"), //

	/**
	 * 保存StructureObject时异常
	 */
	DS_SAVE_STRUCTURE("48", "ID_DS_SAVE_STRUCTURE"), //

	/**
	 * 创建foundation更新BI_NUMBER_TRANS（流水码）时异常
	 */
	DS_UPDATE_SERIES_CREATE("49", "ID_DS_UPDATE_SERIES"), //

	/**
	 * 启动新事务异常
	 */
	DS_START_TRANSACTION("50", "ID_DS_START_TRANSACTION"), //

	/**
	 * 不能移交检出别人的数据
	 */
	DS_TRANS_CHECKOUT_ERROR_1("51", "ID_DS_TRANS_CHECKOUT_ERROR_1"), //

	/**
	 * 接收人没有检出权限
	 */
	DS_TRANS_CHECKOUT_NO_AUTH_2("141", "ID_DS_TRANS_CHECKOUT_NO_AUTH_2"), //

	/**
	 * 删除BOMStructure时异常
	 */
	DS_DELETE_BOMSTRUCTURE("52", "ID_DS_DELETE_BOMSTRUCTURE"), //

	/**
	 * 保存foundation的id时传入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_ID_PARAM("53", "ID_DS_SAVE_FOUNDATION_ID_PARAM"), //

	/**
	 * 保存foundation的LifeCyclePhase时传入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_LIFEPHASE_PARAM("54", "ID_DS_SAVE_FOUNDATION_LIFEPHASE_PARAM"), //

	/**
	 * 保存foundation的OwnerGroup时传入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_OWNERGROUP_PARAM("55", "ID_DS_SAVE_FOUNDATION_OWNERGROUP_PARAM"), //

	/**
	 * 保存foundation的OwnerUser时传入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_OWNERUSER_PARAM("56", "ID_DS_SAVE_FOUNDATION_OWNERUSER_PARAM"), //

	/**
	 * 保存foundation的RevisionId时传入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_RID_PARAM("57", "ID_DS_SAVE_FOUNDATION_RID_PARAM"), //

	/**
	 * 保存foundation的Status时传入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_STATUS_PARAM("58", "ID_DS_SAVE_FOUNDATION_STATUS_PARAM"), //

	/**
	 * 删除foundation更新BI_NUMBER_TRANS（流水码）时异常
	 */
	DS_UPDATE_SERIES_DELETE("80", "ID_DS_UPDATE_SERIES_DELETE"), //

	/**
	 * 要删除的foundation已经被删除
	 */
	DS_DELETE_FOUNDATION_NO_DATA("81", "ID_DS_DELETE_FOUNDATION_NO_DATA"), //

	/**
	 * 移交检出异常,当前数据没有被检出或已经被修改或已经被删除或不允许非检出者移交此数据
	 */
	DS_TRANS_CHECKOUT_IS_OWNER_ONLY("82", "ID_DS_TRANS_CHECKOUT_IS_OWNER_ONLY"), //

	/**
	 * 更改foundation的特殊字段时异常,当前对象已经被修改或已经被删除
	 */
	DS_SAVE_FOUNDATION_PARAM_DATA_LOST("83", "ID_DS_SAVE_FOUNDATION_PARAM_DATA_LOST"), //

	/**
	 * 更改foundation的状态时异常,当前对象的状态已经被改变
	 */
	DS_SAVE_FOUNDATION_PARAM_STATUS("84", "ID_DS_SAVE_FOUNDATION_PARAM_STATUS"), //

	/**
	 * 更改foundation的生命周期状态时异常,当前对象的生命周期状态已经被改变
	 */
	DS_SAVE_FOUNDATION_PARAM_LIFECYCLEPHASE("85", "ID_DS_SAVE_FOUNDATION_PARAM_LIFECYCLEPHASE"), //

	/**
	 * 保存foundation的特殊字段时，输入参数不符合规范
	 */
	DS_SAVE_FOUNDATION_PARAM_INPUT("86", "ID_DS_SAVE_FOUNDATION_PARAM"), //

	/**
	 * 保存structureObject时异常,当前数据已经被修改或已经被删除
	 */
	DS_SAVE_STRUCTURE_REVISION("87", "ID_DS_SAVE_STRUCTURE_REVISION"), //

	/**
	 * 保存structureObject的gen时异常,当前数据已经被修改或已经被删除
	 */
	DS_SAVE_STRUCTURE_REVISION_GEN("88", "ID_DS_SAVE_STRUCTURE_REVISION_GEN"), //

	/**
	 * 用存储过程获取bomView时异常
	 */
	DS_EXECUTE_SELECT_BOM_VIEW("89", "ID_DS_EXECUTE_SELECT_BOM_VIEW"), //

	/**
	 * 检入异常,当前数据未被检出或已经被修改或已经被删除或只允许检出者检入。
	 */
	DS_CHECKIN_DATA_LOST_ISOWNERONLY("97", "ID_DS_CHECKIN_DATA_LOST_ISOWNERONLY"), //

	/**
	 * 根据BOMViewGuid查找STRUCTURE时异常
	 */
	DS_SELECT_STRUCTUREOBJECT_WITH_BOMVIEWGUID("98", "ID_DS_SELECT_STRUCTUREOBJECT_WITH_BOMVIEWGUID"), //

	/**
	 * 创建structure对象时违反唯一约束条件
	 */
	DS_CREATE_STRUCTURE_UNIQUE("100", "ID_DS_CREATE_STRUCTURE_UNIQUE"), //

	/**
	 * 保存foundation对象时违反唯一约束条件
	 */
	DS_SAVE_FO_UNIQUE("101", "ID_DS_SAVE_FO_UNIQUE"), //

	/**
	 * 保存structure对象时违反唯一约束条件
	 */
	DS_SAVE_STRUCTURE_UNIQUE("102", "ID_DS_FOUNDATION_STRUCTURE_UNIQUE"), //

	/**
	 * 将数据移动到文件夹时源数据不存在
	 */
	DS_MOVETOFOLDER_NO_DATE("103", "ID_DS_MOVETOFOLDER_NO_DATE"), //

	/**
	 * 没有新建修订版权限
	 */
	DS_NO_REVISE_AUTH("104", "ID_DS_NO_REVISE_AUTH"), //

	/**
	 * 没有查询到数据
	 */
	DS_SEARCHCONDITION_NO_RESULT("105", "ID_DS_SEARCHCONDITION_NO_RESULT"), //

	/**
	 * 没有查询权限
	 */
	DS_SEARCHCONDITION_NO_READ_AUTH("106", "ID_DS_SEARCHCONDITION_NO_READ_AUTH"), //

	/**
	 * SQL查询中没有指定查找的字段
	 */
	DS_NO_SELECT_FIELD("107", "ID_DS_NO_SELECT_FIELD"), //

	/**
	 * relation重复
	 */
	DS_UNIQUE_RELATION("108", "ID_DS_UNIQUE_RELATION"), //

	/**
	 * 不能关联自己
	 */
	DS_RELATION_SELF("109", "ID_DS_RELATION_SELF"), //

	/**
	 * 循环关联
	 */
	DS_CONNECT_BY_ERROR("128", "ID_DS_CONNECT_BY_ERROR"), //

	/**
	 * 根据item查找product异常
	 */
	DS_SELECT_ITEM_BYPRODUCT("110", "ID_DS_SELECT_ITEM_BYPRODUCT"), //

	/**
	 * ec保存变更的字段时异常
	 */
	DS_SAVEECOBJECT("111", "ID_DS_SAVEECOBJECT"), //

	/**
	 * ec获取变更的字段时异常
	 */
	DS_SHOWECOBJECT("112", "ID_DS_SHOWECOBJECT"), //

	/**
	 * ec保存受影响的对象时异常
	 */
	DS_SAVEECEFFECTEDOBJECT("113", "ID_DS_SAVEECEFFECTEDOBJECT"), //

	/**
	 * 剪切BOM是异常
	 */
	DS_CUTBOM("114", "ID_DS_CUTBOM"), //

	/**
	 * 替换BOM时异常,只有view的检出者可以进行操作
	 */
	DS_REPLACEBOM("115", "ID_DS_REPLACEBOM"), //

	/**
	 * 创建ec时异常
	 */
	DS_CREATE_ECFOUNDATION("116", "ID_DS_CREATE_ECFOUNDATION"), //

	/**
	 * 保存受影响对象异常
	 */
	DS_SAVEEFFECTEDPRODUCT("117", "ID_DS_SAVEEFFECTEDPRODUCT"), //

	/**
	 * 查询受影响对象列表时异常
	 */
	DS_LISTECEFFECTEDOBJECT("118", "ID_DS_LISTECEFFECTEDOBJECT"), //

	/**
	 * 查询bomstructure异常
	 */
	DS_LISTBOM("119", "ID_DS_LISTBOM"), //

	/**
	 * SDS中提交事务异常
	 */
	SDS_COMMIT_TRANSACTION("59", "ID_SDS_COMMIT_TRANSACTION"), //

	/**
	 * SDS中按照GUID删除对象时异常
	 */
	SDS_DELETE_GUID("60", "ID_SDS_DELETE_GUID"), //

	/**
	 * 获取ma_class时异常
	 */
	SDS_GET_MA_CLASS("61", "ID_SDS_GET_MA_CLASS"), //

	/**
	 * SDS中rollbackTransaction异常
	 */
	SDS_ROLLBACK_TRANSACTION("62", "ID_SDS_ROLLBACK_TRANSACTION"), //

	/**
	 * SDS中保存时异常
	 */
	SDS_SAVE("63", "ID_SDS_SAVE"), //

	/**
	 * SDS中查询异常
	 */
	SDS_SELECT("64", "ID_SDS_SELECT"), //

	/**
	 * SDS中startTransaction异常
	 */
	SDS_START_TRANSACTION("65", "ID_SDS_START_TRANSACTION"), //

	/**
	 * SDS中更新异常
	 */
	SDS_UPDATE("66", "ID_SDS_UPDATE"), //

	/**
	 * SDS中调用存储过程时异常
	 */
	SDS_EXE_FUNCTION("67", "ID_SDS_EXE_FUNCTION"), //

	/**
	 * 调用存储过程查询ACL时异常
	 */
	SDS_EXE_FUNCTION_FORACL("68", "ID_SDS_EXE_FUNCTION_FORACL"), //

	/**
	 * 调用存储过程重新刷新ACL时异常
	 */
	SDS_REFRESH_ACL("69", "ID_SDS_REFRESH_ACL"), //

	/**
	 * SDS中创建时异常
	 */
	SDS_INSERT("90", "ID_SDS_INSERT"), //

	/**
	 * SDS中调用存储过程更新数据异常
	 */
	SDS_EXE_FUNCTION_UPDATE("91", "ID_SDS_EXE_FUNCTION_UPDATE"), //

	/**
	 * SDS中按照传进来的参数删除对象时异常
	 */
	SDS_DELETE_PARAM("95", "ID_SDS_DELETE_PARAM"), //

	/**
	 * SDS中按照传进来的参数和sqlStatementId删除对象时异常
	 */
	SDS_DELETE_SQLSTATEMENTID("96", "ID_SDS_DELETE_SQLSTATEMENTID"), //

	/*
	 * for CacheService
	 */

	/**
	 * cacheClassGuidMap时异常
	 */
	CACHE_CLASS_GUID("70", "ID_CACHE_CLASS_GUID"), //

	/**
	 * cacheSystemStatusMap时异常
	 */
	CACHE_SYSTEM_STATUS("71", "ID_CACHE_SYSTEM_STATUS"), //

	/**
	 * cacheBomViewType时异常
	 */
	CACHE_BOM_VIEW("72", "ID_CACHE_BOM_VIEW"), //

	/**
	 * cacheAccessCondition时异常
	 */
	CACHE_ACCES_CONDITION("73", "ID_CACHE_ACCES_CONDITION"), //

	/**
	 * cacheBizObject时异常
	 */
	CACHE_BIZ_OBJECT("74", "ID_CACHE_BIZ_OBJECT"), //

	/**
	 * cacheBizModel时异常
	 */
	CACHE_BIZ_MODEL("75", "ID_CACHE_BIZ_MODEL"), //

	/**
	 * cacheBM2G时异常
	 */
	CACHE_BM2G("76", "ID_CACHE_BM2G"), //

	/**
	 * cacheClassField时异常
	 */
	CACHE_CLASS_FIELD("77", "ID_CACHE_CLASS_FIELD"), //

	/**
	 * cacheCodeItem时异常
	 */
	CACHE_CODE_ITEM("78", "ID_CACHE_CODE_ITEM"), //

	/**
	 * cacheCode时异常
	 */
	CACHE_CODE("79", "ID_CACHE_CODE"), //

	/**
	 * 缓存生命周期detail时异常
	 */
	CACHE_CACHELIFECYCLEITEM("120", "ID_CACHE_CACHELIFECYCLEITEM"), //

	/**
	 * 缓存生命周期master时异常
	 */
	CACHE_CACHELIFECYCLE("121", "ID_CACHE_CACHELIFECYCLE"), //

	/**
	 * 查询已发布数据时异常
	 */
	DS_SELECT_RELEASED("122", "ID_DS_SELECT_RELEASED"), //

	/**
	 * 查询废弃数据时异常
	 */
	DS_SELECT_OBSOLECT("123", "ID_DS_SELECT_OBSOLECT"), //

	/**
	 * 只能修改WIP状态的数据
	 */
	DS_WIP_ONLY("124", "ID_DS_WIP_ONLY"), //

	/**
	 * 没有取消检出的权限
	 */
	DS_NO_CANCELCHECKOUT_AUTH("144", "ID_DS_NO_CANCELCHECKOUT_AUTH"), //

	/**
	 * 目标文件夹只允许添加发布版数据，且数据不是发布版
	 */
	// MOVE_REL_FOR_REV_FOL_RLSONLY("145", "ID_MOVE_REL_FOR_REV_FOL_RLSONLY"), //

	/**
	 * 目标文件夹已经有相同的快捷方式
	 */
	// MOVE_REL_FOR_REV_FOL_EXISTS("146", "ID_MOVE_REL_FOR_REV_FOL_EXISTS"), //

	/**
	 * 没有创建文件夹的权限
	 */
	DS_CREATEFOLDER_NOAUTH("147", "ID_DS_CREATEFOLDER_NOAUTH"), //

	/**
	 * 没有删除文件夹的权限
	 */
	DS_DELETEFOLDER_NOAUTH("148", "ID_DS_DELETEFOLDER_NOAUTH"), //

	/**
	 * 删除文件夹异常
	 * 文件夹不存在，或文件夹已经被其他对象使用
	 */
	DS_DELETEFOLDER_ERROR("149", "ID_DS_DELETEFOLDER_ERROR"), //

	/**
	 * 没有读取文件夹的权限
	 */
	DS_READFOLDER_NOAUTH("150", "ID_DS_READFOLDER_NOAUTH"), //

	/**
	 * 没有找到library
	 */
	DS_READFOLDER_NODATA("151", "ID_DS_READFOLDER_NODATA"), //

	/**
	 * 没有重命名文件夹的权限
	 */
	DS_UPDATEFOLDER_NOAUTH("152", "ID_DS_UPDATEFOLDER_NOAUTH"), //

	/**
	 * 更新文件夹失败
	 */
	DS_UPDATEFOLDER_ERROR("153", "ID_DS_UPDATEFOLDER_ERROR"), //

	/**
	 * 获取library异常
	 */
	DS_LISTLIBRAIRE_ERROR("154", "ID_DS_LISTLIBRAIRE_ERROR"), //

	/**
	 * 没有提升的权限
	 */
	DS_NO_PROMOTE_AUTH("155", "ID_DS_NO_PROMOTE_AUTH"), //

	/**
	 * 没有降级的权限
	 */
	DS_NO_DEMOTE_AUTH("156", "ID_DS_NO_DEMOTE_AUTH"), //

	/**
	 * 初始化导入数据服务异常
	 */
	IMPORTDATASERVER_INIT_DS("157", "ID_IMPORTDATASERVER_INIT_DS"), //

	/**
	 * 源文件夹不存在该数据
	 */
	FROM_FOLDER_NO_DATA("157", "ID_FROM_FOLDER_NO_DATA"), //

	/**
	 * 传入参数错误
	 */
	DS_INPUT_PARAM_ERROR("158", "ID_DS_INPUT_PARAM_ERROR"), //

	/**
	 * 属性不能为空
	 */
	DS_KEY_VALUE_ERROR("160", "ID_DS_KEY_VALUE_ERROR"), //

	/**
	 * 文件夹不能为空
	 */
	DS_FOLDER_IS_NULL("161", "ID_DS_FOLDER_IS_NULL"), //

	/**
	 * 为revision和folder创建关系时异常
	 */
	CREATE_REL_FOR_REV_FOL("125", "ID_CREATE_REL_FOR_REV_FOL"), //

	/**
	 * 修改revision和folder的关系时异常
	 */
	MOVE_REL_FOR_REV_FOL("126", "ID_MOVE_REL_FOR_REV_FOL"), //

	/**
	 * 数据不存在或者已经被删除
	 */
	DS_NO_DATA("135", "ID_DS_NO_DATA"), //

	/**
	 * 文件夹下只能存放已发布数据
	 */
	RELEASE_ONLY("136", "ID_RELEASE_ONLY"), //

	/**
	 * 没有添加关联关系的权限
	 */
	DS_NO_ADD_PREFERENCE_AUTHORITY("137", "ID_DS_NO_ADD_PREFERENCE_AUTHORITY"), //

	/**
	 * 没有删除关联关系的权限
	 */
	DS_NO_DEL_PREFERENCE_AUTHORITY("138", "ID_DS_NO_DEL_PREFERENCE_AUTHORITY"), //

	/**
	 * 获取有add preference权限的文件夹列表异常
	 */
	DS_GET_VISIBLE_FOLDERS("139", "ID_DS_GET_VISIBLE_FOLDERS"), //

	/**
	 * 获取流水号问题
	 */
	DS_GET_NUMBER_ERROR("140", "ID_DS_GET_NUMBER_ERROR"), //

	/**
	 * 编码管理获取流水号问题
	 */
	DS_GET_CODE_NUMBER_ERROR("282", "ID_DS_GET_CODE_NUMBER_ERROR"),

	/**
	 * 不能获取创建权限
	 */
	CAN_NOT_GET_CREATE_AUTHORITY("127", "ID_GET_CREATE_AUTHORITY_ONLY"), //

	/*
	 * for DataServer
	 */

	/**
	 * DATASERVER初始化CacheService异常
	 */
	DATASERVER_INIT_CS("92", "ID_DATASERVER_INIT_CS"), //

	/**
	 * DATASERVER初始化DataService异常
	 */
	DATASERVER_INIT_DS("93", "ID_DATASERVER_INIT_DS"), //

	/**
	 * DATASERVER初始化SystemDataService异常
	 */
	DATASERVER_INIT_SDS("94", "ID_DATASERVER_INIT_SDS"), //

	/**
	 * DATASERVER初始化TransactionService异常
	 */
	DATASERVER_INIT_TS("95", "ID_DATASERVER_INIT_TS"), //
	/**
	 * 违反系统唯一约束条件，不同版本的数据不能有相同版本号
	 */
	DS_CREATE_SYS_UK("162", "ID_DS_CREATE_SYS_UK"), //

	/**
	 * 文件夹不存在
	 */
	FOLDER_NOT_EXITSTS("163", "ID_ORIGINAL_FOLDER_NOT_EXITSTS"), //

	/**
	 * 关联关系已经不存在
	 */
	DS_REF_NOT_EXISTS("164", "ID_DS_REF_NOT_EXISTS"), //

	/**
	 * 获取精确上层异常
	 */
	LIST_WHERE_PRECISE_USED("165", "ID_LIST_WHERE_PRECISE_USED"), //

	/**
	 * 只有view的检出者能够操作
	 */
	DS_VIEWCHECKOUTUSER_ONLY("166", "ID_DS_VIEWCHECKOUTUSER_ONLY"), //

	/**
	 * 创建文件夹异常
	 */
	DS_CREATE_FOLDER("167", "ID_DS_CREATE_FOLDER"), //

	/**
	 * 违反唯一约束限制
	 */
	UNIQUE_CONSTRAINT_VIOLATED("168", "ID_UNIQUE_CONSTRAINT_VIOLATED"), //

	/**
	 * 属性不能为空
	 */
	FIELD_IS_NULL("169", "ID_FIELD_IS_NULL"), //

	/**
	 * 获取文件夹的权限列表时异常
	 */
	LIST_FOLDER_AUTH("170", "ID_LIST_FOLDER_AUTH"), //

	/**
	 * 获取单个bomstructure时异常
	 */
	DS_QUERY_BOMSTRUCTURE("171", "ID_DS_QUERY_BOMSTRUCTURE"), //

	/**
	 * 获取iteration异常
	 */
	DS_LIST_ITERATION("172", "ID_DS_LIST_ITERATION"), //

	/**
	 * 保存实例下的文件异常
	 */
	SAVE_FILE_ERROR("173", "ID_SAVE_FILE_ERROR"), //

	/**
	 * 只允许自己回滚
	 */
	ROLLBACK_OWNER_ONLY("174", "ID_ROLLBACK_OWNER_ONLY"), //

	/**
	 * 没有更改所有者权限
	 */
	DS_NO_AUTH_CHANGOWNER("175", "ID_DS_NO_AUTH_CHANGOWNER"), //

	/**
	 * 没有更改ID或者AlterID的权限
	 */
	DS_NO_AUTH_CHANGID("176", "ID_DS_NO_AUTH_CHANGID"), //

	/**
	 * 没有更改主名称权限
	 */
	DS_NO_AUTH_CHANGEMASTERNAME("177", "ID_DS_NO_AUTH_CHANGEMASTERNAME"), //

	/**
	 * 工作流中的数据
	 */
	DS_WF_DATA("178", "ID_DS_WF_DATA"), //

	/**
	 * linkbom,精确bom end2的guid不能为空
	 */
	DS_LINKBOM_1("179", "ID_DS_LINKBOM_1"), //

	/**
	 * linkbom,非精确bom end2的guid不能有值
	 */
	DS_LINKBOM_2("180", "ID_DS_LINKBOM_2"), //

	/**
	 * view不存在
	 */
	DS_VIEW_NOT_EXISTS("181", "ID_DS_VIEW_NOT_EXISTS"), //

	/**
	 * 不能从个人文件夹粘贴对象到公共库文件夹
	 */
	DS_CANOT_ADD_TO_PUBLICFOLDER("182", "DS_CANOT_ADD_TO_PUBLICFOLDER"), //

	/**
	 * 工作流附件被检出
	 */
	DS_WF_INSTANCE_ISCHECKOUT("183", "DS_WF_INSTANCE_ISCHECKOUT"), //

	/**
	 * 工作流附件不符合生命周期
	 */
	DS_WF_INSTANCE_ISOUTLIFECYCLEPHASE("184", "DS_WF_INSTANCE_ISOUTLIFECYCLEPHASE"), //

	/**
	 * 工作流附件被锁
	 */
	DS_WF_INSTANCE_ISLOCKED("185", "DS_WF_INSTANCE_ISLOCKED"), //

	/**
	 * 工作流附件没有权限
	 */
	DS_WF_INSTANCE_ISNOAUTH("186", "DS_WF_INSTANCE_ISNOAUTH"), //

	/**
	 * ID重复,有流水码的id重复
	 */
	DS_UNIQUE_ID("132", "ID_DS_UNIQUE_ID"), //

	/**
	 * ID重复,没有流水码的id重复
	 */
	DS_UNIQUE_ID_NO_SERIES("187", "ID_DS_UNIQUE_ID_NO_SERIES"), //

	/**
	 * 流水码用完了
	 */
	DS_NO_SERIES("188", "ID_DS_UNIQUE_ID_NO_NUMBERINGOBJECTLIST"),

	/**
	 * 普通structureobject结构，同一个end1下end2相同
	 */
	DS_END2_UNIQUE("189", "ID_DS_END2_UNIQUE"),

	/**
	 * 修改master name出错，可能数据在工作流中，或数据已经不存在
	 */
	DS_CHANGEMASTERNAME_ERROR("190", "ID_DS_CHANGEMASTERNAME_ERROR"),

	/**
	 * 修改id出错，可能数据在工作流中，或者数据已经被删除
	 */
	DS_CHANGID_ERROR("191", "ID_DS_CHANGID_ERROR"),

	/**
	 * 保存预定义搜索时异常
	 */
	DS_SAVE_PRESEARCH("192", "ID_DS_SAVE_PRESEARCH"),

	/**
	 * 删除预定义搜索时异常
	 */
	DS_DELETE_PRESEARCH("193", "ID_DS_DELETE_PRESEARCH"),
	/**
	 * 查询预定义搜索时异常
	 */
	DS_LIST_PRESEARCH("194", "ID_DS_LIST_PRESEARCH"),

	/**
	 * 存储过程的传入参数和实际参数不一致
	 */
	DS_PROCEDUREPARA_NOT_SAME("195", "ID_DS_PROCEDUREPARA_NOT_SAME"),

	/**
	 * 存储过程不存在
	 */
	DS_PROCEDURE_NOT_EXIST("196", "ID_DS_PROCEDURE_NOT_EXIST"),

	/**
	 * 保存ecflag异常
	 */
	SAVE_ECFLAG_ERROR("197", "ID_SAVE_FILE_ERROR"), //

	/**
	 * 预定义搜索不存在
	 */
	DS_PRESEARCH_NOT_EXIST("198", "ID_DS_PRESEARCH_NOT_EXIST"),

	/**
	 * 关闭ecn异常
	 */
	DS_CLOSED_ECN_ERROR("199", "ID_DS_CLOSED_ECN_ERROR"),

	/**
	 * 没有更新权限
	 */
	DS_NO_UPDATE_AUTH("200", "ID_DS_NO_UPDATE_AUTH"),

	/**
	 * 有字段实际值大于允许的精度
	 */
	DS_INT_FIELD_TOO_LARGE("201", "ID_DS_INT_FIELD_TOO_LARGE"),

	/**
	 * 用存储过程获取BOInfo时异常
	 */
	DS_EXECUTE_SELECT_BOINFO("202", "ID_DS_EXECUTE_SELECT_BOINFO"),

	/**
	 * 用存储过程获取CodeItemInfo时异常
	 */
	DS_EXECUTE_SELECT_CODEITEMINFO("203", "ID_DS_EXECUTE_SELECT_CODEITEMINFO"),

	/**
	 * 没有生效权限
	 */
	DS_NO_EFFECT_AUTH("204", "ID_DS_NO_EFFECT_AUTH"), //

	/**
	 * 生效异常,当前数据已经生效或者数据不存在
	 */
	DS_EFFECT_DATA_LOST("205", "ID_DS_EFFECT_DATA_LOST"), //

	/**
	 * 生效异常
	 */
	DS_EFFECT("206", "ID_DS_EFFECT"), //

	/**
	 * 入库异常
	 */
	DS_COMMIT("207", "ID_DS_COMMIT"), //

	/**
	 * 取消废弃异常
	 */
	DS_CANCELOBSOLETE("208", "ID_DS_CANCELOBSOLETE"), //

	/**
	 * 设置是否导入ERP异常
	 */
	DS_ISEXPORTTOERP("209", "ID_DS_ISEXPORTTOERP"), //

	/**
	 * 库处于卸载状态
	 */
	DS_ISNOTVALID("210", "ID_DS_ISNOTVALID"), //

	/**
	 * 用户组和库没有关联
	 */
	DS_GROUP_NOTRELATION_LIBRARY("211", "ID_DS_GROUP_NOTRELATION_LIBRARY"), //

	/**
	 * 不符合编码长度
	 */
	DS_NUMBER_LENGTH("212", "ID_DS_NUMBER_LENGTH"), //

	/**
	 * 替换时出现循环
	 */
	DS_REPLACE_CIRCLE_ERROR("213", "ID_DS_REPLACE_CIRCLE_ERROR"), //

	/**
	 * 保存PR状态异常
	 */
	SAVE_PRSTATUS_ERROR("214", "ID_SAVE_PRSTATUS_ERROR"), //

	/**
	 * 获取系统时间异常
	 */
	DS_GET_SYSDATE_ERROR("215", "ID_DS_GET_SYSDATE_ERROR"), //

	/**
	 * 修改规则时执行操作异常
	 */
	DS_CHANGERULE_OPER_ERROR("216", "ID_DS_CHANGERULE_OPER_ERROR"), //

	/**
	 * 修改状态出错，可能数据已经不存在
	 */
	DS_CHANGESTATUS_ERROR("217", "ID_DS_CHANGESTATUS_ERROR"),

	/**
	 * 没有库移除数据的权限
	 */
	DS_NO_LIB_DEL_PREFERENCE_AUTH("218", "ID_DS_NO_LIB_DEL_PREFERENCE_AUTH"), //

	/**
	 * 没有库添加数据的权限
	 */
	DS_NO_LIB_ADD_PREFERENCE_AUTH("219", "ID_DS_NO_LIB_ADD_PREFERENCE_AUTH"), //

	/**
	 * 库中存在数据，不能删除
	 */
	DS_DELETE_LIB_ERROR("220", "ID_DS_DELETE_LIB_ERROR"), //

	/**
	 * 运行入库规则异常
	 */
	DS_RUN_COMMITRULE_ERROR("221", "ID_DS_RUN_COMMITRULE_ERROR"), //

	/**
	 * 删除数据和个人文件夹的关联关系异常
	 */
	DS_DELETE_PRIVATE_REFERENCE("222", "ID_DS_DELETE_PRIVATE_REFERENCE"), //

	/**
	 * 私人数据不能检出
	 */
	DS_CHECKOUT_PRIVATE_DATA("223", "ID_DS_CHECKOUT_PRIVATE_DATA"), //

	/**
	 * 数据处于非检出状态
	 */
	DS_NON_CHECKOUT("224", "ID_DS_NON_CHECKOUT"), //

	/**
	 * 源文件夹不存在
	 */
	DS_NON_FROM_FOLDER("225", "ID_DS_NON_FROM_FOLDER"), //

	/**
	 * 目标文件夹不存在
	 */
	DS_NON_TO_FOLDER("226", "ID_DS_NON_TO_FOLDER"), //

	/**
	 * 数据已发布，不能检出
	 */
	DS_DATA_IS_RLS("227", "ID_DS_DATA_IS_RLS"), //

	/**
	 * 修改master alterid出错，可能数据已经不存在
	 */
	DS_CHANGEMASTERALTERID_ERROR("228", "ID_DS_CHANGEMASTERALTERID_ERROR"),

	/**
	 * BOM精确性转换出错，当前BOM处于预发布、发布、废弃状态或者非本人检出
	 */
	DS_CHANGEPRECISE_ERROR("229", "ID_DS_CHANGEPRECISE_ERROR"),

	/**
	 * 只有系统管理员能删除库
	 */
	DS_DELETE_LIB_NOT_ADMIN("230", "ID_DS_DELETE_LIB_NOT_ADMIN"),

	/**
	 * 查询master异常
	 */
	DS_QUERY_MASTER_ERROR("231", "ID_DS_QUERY_MASTER_ERROR"),

	/**
	 * 库不存在或者已被删除
	 */
	LIB_IS_NULL("232", "ID_LIB_IS_NULL"), //

	/**
	 * 获取共享文件夹权限异常
	 */
	DS_GET_SHAREFOLDER_AUTH_ERROR("233", "ID_DS_GET_SHAREFOLDER_AUTH_ERROR"), //

	/**
	 * 库不能同名
	 */
	DS_LIB_NAME_DUPLICATE("234", "ID_DS_DS_LIB_NAME_DUPLICATE"), //

	/**
	 * 数据处于非crt、wip、ecp状态不能检出
	 */
	DS_CHECKOUT_STATUS_ERROR("235", "ID_DS_CHECKOUT_STATUS_ERROR"), //

	/**
	 * 检出异常，数据已经被修改
	 */
	DS_CHECKOUT_ERROR_DATAMODIFIED("236", "ID_DS_CHECKOUT_ERROR_DATAMODIFIED"), //

	/**
	 * 检出异常，数据已经被检出
	 */
	DS_CHECKOUT_ERROR_DATACHECKOUTED("237", "ID_DS_CHECKOUT_ERROR_DATACHECKOUTED"), //

	/**
	 * 创建流程时异常
	 */
	DS_CREATE_WF_ERROR("238", "ID_DS_CREATE_WF_ERROR"), //

	/**
	 * 修改owner异常，当前对象已经被删除
	 */
	DS_MOFIFY_OWNER_ERROR_DELETE("239", "ID_DS_MOFIFY_OWNER_ERROR_DELETE"), //

	/**
	 * 修改owner异常
	 */
	DS_MOFIFY_OWNER_ERROR("240", "ID_DS_MOFIFY_OWNER_ERROR"), //

	/**
	 * 删除master异常，有版本处于检出、预发布、发布、或者废弃状态
	 */
	DS_DELETE_MASTER_ERROR_STATUS("241", "ID_DS_DELETE_MASTER_ERROR_STATUS"), //

	/**
	 * 删除master异常，当前用户对某一版本没有权限
	 */
	DS_DELETE_MASTER_ERROR_AUTH("242", "ID_DS_DELETE_MASTER_ERROR_AUTH"), //

	/**
	 * 删除master异常，数据处于工作流中或者不存在
	 */
	DS_DELETE_MASTER_ERROR_WF_DELETED("243", "ID_DS_DELETE_MASTER_ERROR_WF_DELETED"), //

	/**
	 * 删除master异常
	 */
	DS_DELETE_MASTER_ERROR("244", "ID_DS_DELETE_MASTER_ERROR"), //

	/**
	 * 删除库异常，库被作为组的默认库
	 */
	DS_DELETE_LIB_ERROR_DEFAULTLIB("245", "ID_DS_DELETE_LIB_ERROR_DEFAULTLIB"), //

	/**
	 * 修订异常，当前实例所处的生命周期不允许修订操作
	 */
	DS_REVISE_ERROR_LIFECYCLEPHASE("246", "ID_DS_REVISE_ERROR_LIFECYCLEPHASE"), //

	/**
	 * 修订异常，不允许创建多个工作版
	 */
	DS_REVISE_ERROR_MANYWIP("247", "ID_DS_REVISE_ERROR_MANYWIP"), //

	/**
	 * 没有添加end2权限
	 */
	DS_NO_LINK_AUTH("248", "ID_DS_NO_LINK_AUTH"), //

	/**
	 * 没有移除end2权限
	 */
	DS_NO_UNLINK_AUTH("249", "ID_DS_NO_UNLINK_AUTH"), //

	/**
	 * 没有编辑结构权限
	 */
	DS_NO_EDITLINK_AUTH("250", "ID_DS_NO_EDITLINK_AUTH"), //

	/**
	 * 工作流添加附件异常
	 */
	DS_WF_ATTACH_ERROR("251", "ID_DS_WF_ATTACH_ERROR"), //

	/**
	 * 工作流删除附件异常
	 */
	DS_WF_DELATTACH_ERROR("252", "ID_DS_WF_DELATTACH_ERROR"), //

	/**
	 * 用存储过程获取ProcAttach时异常
	 */
	DS_EXECUTE_SELECT_PROCATTACH("253", "ID_DS_EXECUTE_SELECT_PROCATTACH"), //

	/**
	 * 工作流查看附件异常
	 */
	DS_WF_ATTACH_READ_ERROR("254", "ID_DS_WF_ATTACH_READ_ERROR"), //

	/**
	 * 判断权限异常，当前类的生命周期不存在
	 */
	DS_AUTH_ERROR_LIFECYCLEPHASE("255", "ID_DS_AUTH_ERROR_LIFECYCLEPHASE"), //

	/**
	 * 启动工作流失败，没有有效的附件
	 */
	DS_WF_WITHOUTINSTANCE("256", "ID_DS_WF_WITHOUTINSTANCE"), //

	/**
	 * 存储过程异常，代码20001，包含数字之外的字符
	 */
	DS_PROCEDURE_20001("257", "ID_DS_PROCEDURE_20001"), //

	/**
	 * 存储过程异常，代码20002，只能为0~9或者A~Z的字符
	 */
	DS_PROCEDURE_20002("258", "ID_DS_PROCEDURE_20002"), //

	/**
	 * 存储过程异常，代码20005，没有数据
	 */
	DS_PROCEDURE_20005("259", "ID_DS_PROCEDURE_20005"), //

	/**
	 * 存储过程异常，代码20006，检出失败
	 */
	DS_PROCEDURE_20006("260", "ID_DS_PROCEDURE_20006"), //

	/**
	 * 存储过程异常，代码20007，不能撤销流程
	 */
	DS_PROCEDURE_20007("261", "ID_DS_PROCEDURE_20007"), //

	/**
	 * 存储过程异常，代码20010，入库规则不存在
	 */
	DS_PROCEDURE_20010("262", "ID_DS_PROCEDURE_20010"), //

	/**
	 * 删除foundation异常,当前对象已经被检出
	 */
	DS_DELETE_FOUNDATION_CHECKOUT("263", "ID_DS_DELETE_FOUNDATION_CHECKOUT"), //

	/**
	 * 删除structure异常，数据已经被删除、对应的view处于工作流或者非本人检出
	 */
	DS_DELETE_STRCTURE_NODATA("264", "ID_DS_DELETE_STRCTURE_NODATA"), //

	/**
	 * BOMStructure添加end2异常，根据配置不允许添加相同子阶主物料
	 */
	DS_BOMSTRUCTURE_END2_REPEAT("265", "ID_DS_BOMSTRUCTURE_END2_REPEAT"), //

	/**
	 * 模型中object类型的字段没有类型值（后跟两个参数，1、类名；2、字段名）
	 */
	DS_MODEL_OBJECT_TYPEVALUE_ERROR("266", "ID_DS_MODEL_OBJECT_TYPEVALUE_ERROR"), //

	/**
	 * 存储过程异常，代码20011，版本号已用完
	 */
	DS_PROCEDURE_20011("267", "ID_DS_PROCEDURE_20011"), //

	/**
	 * 关闭数据库连接失败
	 */
	DS_CLOSE_CONNECTION_FAILURE("268", "ID_DS_CLOSE_CONNECTION_FAILURE"), //

	/**
	 * 查询数据时异常
	 */
	DS_QUERY_DATA_EXCEPTION("269", "ID_DS_QUERY_DATA_EXCEPTION"), //

	/**
	 * 更新数据时异常
	 */
	DS_UPDATE_DATA_EXCEPTION("270", "ID_DS_UPDATE_DATA_EXCEPTION"), //

	/**
	 * 无效数字
	 */
	DS_INVALID_NUMBER("271", "ID_DS_INVALID_NUMBER"), //

	/**
	 * 数字或值转换错误
	 */
	DS_DATABASE_ERROR_6502("272", "ID_DS_DATABASE_ERROR_6502"), //

	/**
	 * 参数的数量或类型错误
	 */
	DS_DATABASE_ERROR_6550("273", "ID_DS_DATABASE_ERROR_6550"), //

	/**
	 * 查询项目管理权限异常
	 */
	DS_AUTH_ERROR_PM_OPERATE("274", "ID_DS_AUTH_ERROR_PM_OPERATE"),

	/**
	 * 改变实例状态异常，数据已不存在
	 */
	DS_CHANGE_FO_STATUS("275", "ID_DS_CHANGE_FO_STATUS"),

	/**
	 * 获取数据系统权限异常
	 */
	DS_GET_DATA_AUTH_ERROR("276", "ID_DS_GET_DATA_AUTH_ERROR"),

	/**
	 * 获取公共搜索权限异常
	 */
	DS_GET_PUBLICSEARCH_AUTH_ERROR("277", "ID_DS_GET_PUBLICSEARCH_AUTH_ERROR"),

	/**
	 * 输入的值过长
	 */
	DS_DATA_TOO_LONG("278", "ID_DS_DATA_TOO_LONG"), //

	/**
	 * 改变实例状态异常，改变的状态不在允许的范围内
	 */
	DS_CHANGE_FO_STATUS_NOT_ACCEPT("279", "ID_DS_CHANGE_FO_STATUS_NOT_ACCEPT"), //

	/**
	 * 存储过程异常，代码20012，EC过了阶段不能回滚
	 */
	DS_PROCEDURE_20012("280", "ID_DS_PROCEDURE_20012"), //

	/**
	 * 子件已删除/不存在
	 */
	DS_COMPONENT_NOT_EXIST("281", "ID_DS_COMPONENT_NOT_EXIST"),

	/**
	 * 发布异常,当前数据已经发布或者数据不存在
	 */
	DS_RELEASE_DATA_LOST("282", "ID_DS_RELEASE_DATA_LOST"), //

	/**
	 * 发布异常
	 */
	DS_RELEASE("283", "ID_DS_RELEASE"), //

	/**
	 * 超出db的column长度
	 */
	DS_VALUE_OUT_OF_DBRANGE("284", "ID_DS_VALUE_OUT_OF_DBRANGE"), //

	/**
	 * 查询实例分类数据异常
	 */
	DS_LIST_CLASSIFICATION("285", "ID_DS_LIST_CLASSIFICATION"),

	/**
	 * 要查询的分类不存在。
	 */
	DS_NOTEXISTS_CLASSIFICATIONITEM("286", "ID_DS_NOTEXISTS_CLASSIFICATIONITEM"),

	/**
	 * 状态变更异常，数据已被他人修改或数据已被删除。
	 */
	DS_CHANGESTATUS("287", "ID_DS_CHANGESTATUS"), //

	/**
	 * 循环关联
	 */
	DS_CONNECT_BY_ERROR_END1_END2("288", "ID_DS_CONNECT_BY_ERROR_END1_END2"),

	/**
	 * 修订或者另存时，保存BOM或者关系失败
	 */
	DS_COPY_BOM_OR_RELATION_ERROR("289", "ID_DS_COPY_BOM_OR_RELATION_ERROR"),

	/**
	 * 一个任务下不允许定义完全相同的交付项：对象类型、分类、是否发布、是否必须都是相同的
	 */
	DS_DELIVERABLEITEM_MULTI_ERROR("290", "ID_DS_DELIVERABLEITEM_MULTI_ERROR"),

	/**
	 * 一个交付项下不允许提交相同的交付物
	 */
	DS_DELIVERABLE_MULTI_ERROR("291", "ID_DS_DELIVERABLE_MULTI_ERROR"),

	DS_UNIQUE_VIOLATE("292", "ID_DS_UNIQUE_VIOLATE"),

	/**
	 * 查询数据时异常
	 */
	DS_TABLENAME_NULL_EXCEPTION("293", "ID_DS_TABLENAME_NULL_EXCEPTION"), //

	/**
	 * 接收人没有查看权限
	 */
	DS_TRANS_SEL_NO_AUTH_2("141", "ID_DS_TRANS_SEL_NO_AUTH_2"), //

	/**
	 * 配置表被已他人解锁
	 */
	DS_CONFIG_TABLE_LOCKED_BY_OTHERS("294", "ID_DS_CONFIG_TABLE_LOCKED_BY_OTHERS"), //

	/**
	 * 不能解锁他人锁定的对象
	 */
	DS_CONFIG_TABLE_CANNOT_UNLOCK_OTHERS_TAB("295", "ID_DS_CONFIG_TABLE_CANNOT_UNLOCK_OTHERS_TAB"), //

	/**
	 * 配置表被锁定，请先解锁
	 */
	DS_CONFIG_TABLE_ISLOCKED_WHEN_CHANGE_STATUS("296", "ID_DS_CONFIG_TABLE_ISLOCKED_WHEN_CHANGE_STATUS"), //

	/**
	 * 字段在数据库中不存在
	 */
	DS_MODEL_TABLENAME_IS_NULL("295", "ID_DS_MODEL_TABLENAME_IS_NULL"), //

	/**
	 * DATASERVER初始化WorkFlowService异常
	 */
	WORKFLOWSERVER_INIT_WF("298", "ID_DATASERVER_INIT_WFS"), //

	/**
	 * 模型部署异常
	 */
	DS_MODEL_DEPLOY("299", "ID_DS_MODEL_DEPLOY"), //

	/**
	 * 仍未定义的异常
	 */
	UNKNOWN("0", "UNKNOWN");//

	private       String id    = null;
	private       String msrId = null;
	private final String smemo = null;

	public String getSmemo()
	{
		return this.smemo;
	}

	private DataExceptionEnum(String id, String msrId)
	{
		this.id = id;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	@Override
	public String toString()
	{
		return this.id;
	}

	public static DataExceptionEnum getDataExceptionEnum(String id)
	{
		for (DataExceptionEnum dataExceptionEnum : DataExceptionEnum.values())
		{
			if (dataExceptionEnum.id.equals(id))
			{
				return dataExceptionEnum;
			}
		}
		return UNKNOWN;
	}

}
