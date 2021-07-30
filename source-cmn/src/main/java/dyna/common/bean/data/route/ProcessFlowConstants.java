package dyna.common.bean.data.route;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.SetUtils;

public class ProcessFlowConstants
{
	/**
	 * 计算公式库-参数关联模板
	 */
	public static final String	CALCULATION_PARAM_RELATION_TEMPLATE_NAME	= "CalculationParam";

	/**
	 * 材料-工艺流程
	 */
	public static final String	ITEM_PROCESS_FLOW_RELATION_TEMPLATE_NAME	= "itemRelatedjtgxlc";

	/**
	 * 工艺流程库-工序库
	 */
	public static final String	PROCESS_FLOW_PROCESS_RELATION_TEMPLATE_NAME	= "jtgylcRelatedjtgx";

	/**
	 * 工序-工艺参数
	 */
	public static final String	PROCESS_PARAM_RELATION_TEMPLATE_NAME		= "jtgxRelatedcs";

	/**
	 * 工序库-辅料清单
	 */
	public static final String	PROCESS_ITEM_RELATION_TEMPLATE_NAME			= "jtgxRelatedfl";

	/**
	 * 工序库-文档
	 */
	public static final String	PROCESS_DOC_RELATION_TEMPLATE_NAME			= "jtgxRelatedDoc";

	/**
	 * 计算公式库-公式参数对象，固定，用在计算公式库-公式参数关联关系中
	 */
	public static final String	CALCULATION_PARAM_OBJ_ID					= "CAL_PARAM_001";

	/**
	 * 计算公式参数表,参数变量
	 */
	public static final String	CALCULATION_PARAM_CS_VAR					= "cs";

	/**
	 * 计算公式参数表,参数名称
	 */
	public static final String	CALCULATION_PARAM_CS_NAME					= "csmc";

	/**
	 * 计算公式参数表,参数说明
	 */
	public static final String	CALCULATION_PARAM_CS_DESC					= "cssm";

	/**
	 * 工序-工艺参数结构类/工序辅料清单结构类,参数值键值对
	 */
	public static final String	CALCULATION_PARAM_CS_VAL					= "csval";

	/**
	 * 工艺参数库上/工序辅料清单结构类,计算公式字段
	 */
	public static final String	CALCULATION_FORMULA							= "gs";

	/**
	 * 计算公式库,计算公式字段
	 */
	public static final String	CALCULATION_LIBRARY_FORMULA					= "NAME$";

	/**
	 * 工序辅料清单结构类上的用量字段
	 */
	public static final String	PROCESS_ITEM_LIST_QUANTITY					= "yl";

	/**
	 * 工序-工艺参数结构类上的值字段
	 */
	public static final String	PROCESS_PARAM_VALUE							= "z";

	/**
	 * 工序库类上的物料编码字段
	 */
	public static final String	PROCESS_ITEM								= "itemobj";

	/**
	 * 清除模板对象一些属性,用来创建新对象使用
	 * 
	 * @param foundationObject
	 * @return
	 */
	public static FoundationObject clearForCreateObj(FoundationObject foundationObject)
	{
		List<String> fieldNeedClearList = getFieldNeedClearList();
		if (!SetUtils.isNullList(fieldNeedClearList))
		{
			for (String fieldName : fieldNeedClearList)
			{
				foundationObject.clear(fieldName);
			}
		}
		foundationObject.put(SystemClassFieldEnum.STATUS.getName(), SystemStatusEnum.WIP);

		return foundationObject;
	}

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

		return fieldList;
	}
}
