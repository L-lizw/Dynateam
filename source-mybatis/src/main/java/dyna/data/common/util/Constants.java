/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: aaa
 * jianghongliang 2011-1-14
 */
package dyna.data.common.util;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.systemenum.SystemClassFieldEnum;

/**
 * @author jianghongliang
 *
 */
/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 常量类
 * caogc 2010-8-31
 */

public class Constants
{
	/**
	 * 是否需要权限判断的标识符,主要在RelationStub中使用
	 */
	public static final boolean	IS_NEED_AUTHORITY		= false;

	/**
	 * 表示SQL排序的静态常量
	 */
	public static final String	ORDER_BY				= "ORDERBY";

	/**
	 * isOwnerOnly为true只允许检出者操作，false允许其他人操作,主要在CheckStub中使用
	 */
	public static final boolean	IS_OWN_ONLY				= false;

	/**
	 * 表示编码规则中YEAR的所取位数为2
	 */
	public static final String	NUMBERING_YEAR_TWO		= "2";

	/**
	 * 表示编码规则中YEAR的所取位数为4
	 */
	public static final String	NUMBERING_YEAR_FOUR		= "4";

	/**
	 * 表示编码规则中MONTH或者DAY的所取位数为1位时前面补0
	 */
	public static final String	NUMBERING_MONTH_DAY_TWO	= "2";

	public static final String	ESCAPE_CHAR				= "/";

	/**
	 * 当查询bom或者关系结构时，字段可用的前缀列表
	 *
	 * @return
	 */
	public static List<String> getPrefixForListStructure()
	{
		List<String> prefixList = new ArrayList<>();
		prefixList.add(ViewObject.PREFIX_END2);
		// prefixList.add(ViewObject.PREFIX_END1);

		return prefixList;
	}

	/**
	 * 部分需要用end2的值显示在结构上的字段
	 *
	 * @return
	 */
	public static List<String> getDefaultFieldListForStructure()
	{
		List<String> defaultFieldList = new ArrayList<>();
		defaultFieldList.add(SystemClassFieldEnum.ID.getName());
		defaultFieldList.add(SystemClassFieldEnum.NAME.getName());
		defaultFieldList.add(SystemClassFieldEnum.REVISIONID.getName());
		defaultFieldList.add(SystemClassFieldEnum.STATUS.getName());

		return defaultFieldList;
	}
}
