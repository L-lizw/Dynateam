/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FilterBuilder 过滤器构建中心
 * Wanglei 2010-9-8
 */
package dyna.app.service.helper;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

import dyna.common.Criterion;
import dyna.common.SearchCondition;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * 过滤器构建中心, 为system data service 的搜索提供filter
 * 
 * @author Wanglei
 * 
 */
public class FilterBuilder
{

	/**
	 * 拼装searchCondition的检索条件,分页及排序 到Map中
	 * 
	 * @param searchCondition
	 * @return filter
	 */
	public static Map<String, Object> buildFilterBySearchCondition(SearchCondition searchCondition)
	{
		return buildFilterBySearchCondition(searchCondition, null);
	}

	/**
	 * 拼装searchCondition的检索条件,分页及排序 到Map中
	 * 
	 * @param searchCondition
	 * @param orderByTable
	 *            排序字段所属的数据表
	 * @return
	 */
	public static Map<String, Object> buildFilterBySearchCondition(SearchCondition searchCondition,
			String orderByTable)
	{
		Map<String, Object> filter = buildFilterJustCondition(searchCondition);

		// pagination
		// boolean pageForward = searchCondition.getPageForward();
		// int rowIdx = searchCondition.getPageStartRowIdx();
		int rowNum = searchCondition.getPageNum();
		int pageSize = searchCondition.getPageSize();
		// if (!pageForward)
		// {
		// rowIdx -= pageSize + 1;
		// }

		filter.put("CURRENTPAGE", rowNum);
		filter.put("ROWSPERPAGE", pageSize);

		// 拼装排序
		if (!SetUtils.isNullList(searchCondition.getOrderMapList()))
		{
			String orderKey = null;
			Boolean isAsc = false;
			String orderby = "order by ";

			String orderByPrefix = "";
			if (!StringUtils.isNullString(orderByTable))
			{
				orderByPrefix = orderByTable + ".";
			}

			for (Map<String, Boolean> order : searchCondition.getOrderMapList())
			{
				for (Iterator<String> iterator = order.keySet().iterator(); iterator.hasNext();)
				{
					orderKey = iterator.next();
					isAsc = order.get(orderKey);
					orderby += " " + orderByPrefix + trimDollar(orderKey) + " " + (isAsc ? " asc " : " desc ");
				}
			}

			filter.put(Constants.ORDER_BY, orderby);
		}

		return filter;
	}

	/**
	 * 拼装searchCondition的检索条件到Map中
	 * 
	 * @param searchCondition
	 * @return filter
	 */
	public static Map<String, Object> buildFilterJustCondition(SearchCondition searchCondition)
	{
		Map<String, Object> filter = new HashMap<String, Object>();

		// 拼装检索条件
		if (!SetUtils.isNullList(searchCondition.getCriterionList()))
		{
			String key = null;
			Object value = null;
			for (Criterion criterion : searchCondition.getCriterionList())
			{
				value = criterion.getValue();
				if (value instanceof String)
				{
					value = StringUtils.convertNULL((String) value);
				}
				key = trimDollar(criterion.getKey());
				filter.put(key, value);
			}
		}

		// if (searchCondition.isIncludeOBS()/* .isOBSOnly() */)
		// {
		// filter.put("STATUS", "OBS");
		// }

		filter.put("ISLATESTONLY", BooleanUtils.getBooleanStringYN(searchCondition.isLatestOnly()));

		return filter;
	}

	public static String trimDollar(String str)
	{
		String retString = str;
		if (str.lastIndexOf('$') == str.length() - 1)
		{
			retString = str.substring(0, str.length() - 1);
		}
		return retString;
	}
}
