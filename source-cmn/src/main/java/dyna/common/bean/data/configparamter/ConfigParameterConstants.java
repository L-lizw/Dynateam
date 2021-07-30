package dyna.common.bean.data.configparamter;

import java.util.ArrayList;
import java.util.List;

import dyna.common.systemenum.SystemClassFieldEnum;

public class ConfigParameterConstants
{
	// 材料明细模板名称
	public static final String	CONFIG_PARAMETER_RELATION_TEMPLATE_NAME			= "CPMaterialDetail";

	// 订单明细
	public static final String	CONFIG_PARAMETER_ORDELDETAIL_TEMPLATE_NAME		= "CPDrivenOrder";

	// 驱动结果明细--订单BOM
	public static final String	CONFIG_PARAMETER_RESULTRELATION_TEMPLATE_NAME	= "CPDrivenResult";

	// 图纸和物料关联关系
	public static final String	CONFIG_PARAMETER_ITEM_CAD2D_TEMPLATE_NAME		= "ITEM-CAD2D$";

	public static final String	CONFIG_PARAMETER_ITEM_CAD3D_TEMPLATE_NAME		= "ITEM-CAD3D$";

	// 默认的驱动测试条件名称
	public static final String	CONFIG_PARAMETER_TEST_CONDITION_NAME			= "DEFAULT";

	// G番号使用的G号字段最大数目
	public static final Integer	CONFIG_PARAMETER_MAX_G_FIELD_COUNT				= 99;

	// 件号
	public static final String	PARTNUMBER										= "PartNumber";
	// L番号
	public static final String	LNUMBER											= "LNumber";
	// 把L番号去掉L00保存在物料上
	public static final String	LNUMBERWITHOUTL00								= "LNumberWithoutL00";
	// 配置参数
	public static final String	CONFIGPARAMETER									= "ConfigParameter";
	// 参数描述
	public static final String	PARAMETERDESC									= "ParameterDESC";
	// 转换类
	public static final String	MATCHEDCLASS									= "MatchedClass";
	// 计算日志
	public static final String	LOGS											= "LOGS";
	// 错误日志
	public static final String	ERRLOGS											= "ERRLOGS";
	// 唯一号
	public static final String	UNIQUENO										= "UniqueNo";
	// 新对象上的原始对象OBJECT
	public static final String	ORIGOBJ											= "ORIGDRAW";
	// 来源对象版本
	public static final String	MAK												= "MAK";
	public static final String	QUANTITY										= "QUANTITY";
	public static final String	SPECIFICATION									= "Specification";
	public static final String	TEST_LOG_CONDITION_SPLIT_CHAR					= "|";
	// 无图件占位图纸对象编号
	public static final String	NO_DRAWING_ITEM_ID								= "#W";
	// 客供料占位图纸对象编号前缀
	public static final String	SUPPLIED_MATERIALS_ITEM_ID_PREFIX				= "#";
	// 默认的客供料占位图纸对象编号
	public static final String	SUPPLIED_MATERIALS_ITEM_ID						= "#A";

	// 无图件名称
	public static final String	CPNAME											= "CPNAME";
	// 唯一号中图号,件号,选配,配置参数之间的连接字符,手工创建的对象,唯一号默认为^^
	public static final String	UNIQUE_SPLIT_CHAR								= "^";
	// 创建物料对象时,如果不是通过驱动得到的物料,唯一值默认为^^
	public static final String	DEFAULT_UNIQUE									= "^^";
	// 是否是无图件
	public static final String	IS_NO_DRAWING_ITEM								= "ISNODRAWINGITEM";
	// 订单物料
	public static final String	ORDERITEM										= "ORDERITEM";
	// 物料是否是新建的
	public static final String	IS_NEW_ITEM										= "ISNEWITEM";
	// 订单明细所属订单合同
	public static final String	OWNER_CONTRACT									= "OWNERCONTRACT";
	// 订单明细是否可以被驱动
	public static final String	IS_CONTENT_CAN_BE_DRIVE							= "ISCANBEDRIVEN";
	// 物料分类
	public static final String	ITEM_CLASSIFICATION								= "ItemClassification";
	// 订单BOM上生成该订单物料的订单明细对象
	public static final String	STRUC_ORDER_DETAIL								= "ORDERDETAIL";
	
	public static List<String> getFieldNeedClearList()
	{
		List<String> fieldList = new ArrayList<String>();

		fieldList.add(SystemClassFieldEnum.GUID.getName());
		fieldList.add(SystemClassFieldEnum.UPDATETIME.getName());
		fieldList.add(SystemClassFieldEnum.ID.getName());
		fieldList.add("FULLNAME$");
		fieldList.add(SystemClassFieldEnum.UPDATEUSER.getName());
		fieldList.add(SystemClassFieldEnum.CREATETIME.getName());
		fieldList.add(SystemClassFieldEnum.CREATEUSER.getName());
		fieldList.add(SystemClassFieldEnum.MASTERFK.getName());
		fieldList.add(SystemClassFieldEnum.UNIQUES.getName());
		fieldList.add(SystemClassFieldEnum.FILEGUID.getName());
		fieldList.add(SystemClassFieldEnum.FILENAME.getName());
		fieldList.add(SystemClassFieldEnum.FILETYPE.getName());
		fieldList.add(SystemClassFieldEnum.CLASSGUID.getName());
		fieldList.add(SystemClassFieldEnum.BOGUID.getName());
		fieldList.add(SystemClassFieldEnum.CHECKOUTTIME.getName());
		fieldList.add(SystemClassFieldEnum.CHECKOUTUSER.getName());
		fieldList.add(SystemClassFieldEnum.CLASSIFICATION.getName());
		fieldList.add(SystemClassFieldEnum.ECFLAG.getName());
		fieldList.add(SystemClassFieldEnum.FILEGUID.getName());
		fieldList.add(SystemClassFieldEnum.FILENAME.getName());
		fieldList.add(SystemClassFieldEnum.FILETYPE.getName());
		fieldList.add(SystemClassFieldEnum.ISCHECKOUT.getName());
		fieldList.add(SystemClassFieldEnum.ITERATIONID.getName());
		fieldList.add(SystemClassFieldEnum.LATESTREVISION.getName());
		fieldList.add(SystemClassFieldEnum.LCPHASE.getName());
		fieldList.add(SystemClassFieldEnum.NEXTREVISIONRLSTIME.getName());
		fieldList.add(SystemClassFieldEnum.RELEASETIME.getName());
		fieldList.add(SystemClassFieldEnum.REVISIONID.getName());
		fieldList.add(SystemClassFieldEnum.REVISIONIDSEQUENCE.getName());
		fieldList.add(SystemClassFieldEnum.REPEAT.getName());
		fieldList.add(SystemClassFieldEnum.ISEXPORTTOERP.getName());
		fieldList.add("ItemClassification");

		return fieldList;
	}
}
