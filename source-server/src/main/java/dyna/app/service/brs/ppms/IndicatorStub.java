/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CalendarStub
 * WangLHB May 7, 2012
 */
package dyna.app.service.brs.ppms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ppms.indicator.DefineIndicator;
import dyna.common.bean.data.ppms.indicator.FunctionBean;
import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;
import dyna.common.bean.data.ppms.indicator.IndicatorConstants;
import dyna.common.bean.data.ppms.indicator.IndicatorDismension;
import dyna.common.bean.data.ppms.indicator.IndicatorDismensionFilter;
import dyna.common.bean.data.ppms.indicator.function.AbstractFunction;
import dyna.common.bean.data.ppms.indicator.function.FunctionFactory;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.IndicatorTimeRangeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.IndicatorUtil;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * 指标数相关方法
 * 
 * @author duanll
 * 
 */
public class IndicatorStub extends AbstractServiceStub<PPMSImpl>
{
	/**
	 * @param context
	 * @param service
	 */
	protected IndicatorStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	protected Map<String, List<IndicatorAnalysisVal>> listAnalysisValBeforeBaseDate(String indicatorId, Date baseTime, IndicatorTimeRangeEnum timeRange)
			throws ServiceRequestException
	{
		DefineIndicator indicator = this.stubService.getIndicatorConfigStub().getDefinedIndicatorConfig(indicatorId);
		if (indicator == null || SetUtils.isNullList(indicator.getParams()))
		{
			throw new ServiceRequestException("ID_APP_CHAR_DEFINE_ID_NOT_EXIST", "indicator " + indicatorId + " is not defined.", null, indicatorId);
		}

		List<List<IndicatorAnalysisVal>> dataList = new ArrayList<List<IndicatorAnalysisVal>>();
		for (Map<String, String> param : indicator.getParams())
		{
			String indicatorValId = (String) param.values().toArray()[0];

			List<IndicatorAnalysisVal> tmpList = this.listIndicatorValBeforeBaseDate(indicatorValId, baseTime, timeRange);
			if (tmpList == null)
			{
				tmpList = new ArrayList<IndicatorAnalysisVal>();
			}
			dataList.add(tmpList);
		}

		return this.buildAnalysisVal(indicator, dataList, timeRange);
	}

	/**
	 * 取得当月指标
	 * 
	 * @param indicatorSetGuid
	 * @param baseTime
	 * @param timeRange
	 * @return
	 * @throws ServiceRequestException
	 */
	protected Map<String, List<IndicatorAnalysisVal>> listAnalysisVal(String indicatorId, Date baseTime, IndicatorTimeRangeEnum timeRange) throws ServiceRequestException
	{
		DefineIndicator indicator = this.stubService.getIndicatorConfigStub().getDefinedIndicatorConfig(indicatorId);

		List<List<IndicatorAnalysisVal>> dataList = new ArrayList<List<IndicatorAnalysisVal>>();
		for (Map<String, String> param : indicator.getParams())
		{
			String indicatorValId = (String) param.values().toArray()[0];

			List<IndicatorAnalysisVal> tmpList = this.listIndicatorVal(indicatorValId, baseTime, timeRange);
			if (tmpList == null)
			{
				tmpList = new ArrayList<IndicatorAnalysisVal>();
			}
			dataList.add(tmpList);
		}

		return this.buildAnalysisVal(indicator, dataList, timeRange);
	}

	/**
	 * 根据分子和分母计算指标
	 * 
	 * @param numeratorList
	 * @param denominatorList
	 * @param dateList
	 * @param timeRange
	 * @param isAccumulate
	 * @return
	 */
	private Map<String, List<IndicatorAnalysisVal>> buildAnalysisVal(DefineIndicator indicator, List<List<IndicatorAnalysisVal>> dataList, IndicatorTimeRangeEnum timeRange)
	{
		Map<String, List<IndicatorAnalysisVal>> retMap = new HashMap<String, List<IndicatorAnalysisVal>>();

		// 按照维度进行过滤，并分组
		List<Map<String, List<IndicatorAnalysisVal>>> list = this.groupByDismension(indicator.getDismension(), dataList, timeRange);
		if (SetUtils.isNullList(list))
		{
			return null;
		}

		// 合并维度
		List<String> dismensionList = this.mergeDismension(list);
		if (!SetUtils.isNullList(dismensionList))
		{
			for (String dismension : dismensionList)
			{
				List<List<IndicatorAnalysisVal>> tmpList = new ArrayList<List<IndicatorAnalysisVal>>();
				for (int i = 0; i < list.size(); i++)
				{
					tmpList.add(this.getDataOfDismensionList(dismension, list.get(i)));
				}
				retMap.put(dismension, this.calculate(indicator, tmpList, dismension));
			}
		}
		else
		{
			// 无维度
			if (!SetUtils.isNullList(list))
			{
				List<List<IndicatorAnalysisVal>> tmpList = new ArrayList<List<IndicatorAnalysisVal>>();
				for (int i = 0; i < list.size(); i++)
				{
					tmpList.add(this.getDataOfDismensionList(IndicatorConstants.DISMENSION_WHEN_NULL, list.get(i)));
				}
				retMap.put(IndicatorConstants.DISMENSION_WHEN_NULL, this.calculate(indicator, tmpList, IndicatorConstants.DISMENSION_WHEN_NULL));
			}
			else
			{
				retMap.put(IndicatorConstants.DISMENSION_WHEN_NULL, new ArrayList<IndicatorAnalysisVal>());
			}
		}

		return retMap;
	}

	private List<IndicatorAnalysisVal> getDataOfDismensionList(String dismension, Map<String, List<IndicatorAnalysisVal>> dataWithDismensionMap)
	{
		List<IndicatorAnalysisVal> valOfDismensionList = null;
		if (dataWithDismensionMap.containsKey(IndicatorConstants.DISMENSION_WHEN_NULL))
		{
			// 无维度
			valOfDismensionList = dataWithDismensionMap.get(IndicatorConstants.DISMENSION_WHEN_NULL);
		}
		else
		{
			// 有维度
			valOfDismensionList = dataWithDismensionMap.get(dismension);
		}

		if (valOfDismensionList == null)
		{
			valOfDismensionList = new ArrayList<IndicatorAnalysisVal>();
		}

		return valOfDismensionList;
	}

	private List<Map<String, List<IndicatorAnalysisVal>>> groupByDismension(IndicatorDismension filterDismension, List<List<IndicatorAnalysisVal>> dataList,
			IndicatorTimeRangeEnum timeRange)
	{
		List<Map<String, List<IndicatorAnalysisVal>>> list = new ArrayList<Map<String, List<IndicatorAnalysisVal>>>();
		if (!SetUtils.isNullList(dataList))
		{
			for (List<IndicatorAnalysisVal> tmpList : dataList)
			{
				// 取得指定维度（经过过滤后）的数据集
				Map<String, List<IndicatorAnalysisVal>> map = this.buildAnalysisValForDismensionAndDate(filterDismension, tmpList, timeRange);
				if (!SetUtils.isNullMap(map))
				{
					list.add(map);
				}
			}
		}

		return list;
	}

	private List<IndicatorAnalysisVal> calculate(DefineIndicator indicator, List<List<IndicatorAnalysisVal>> list, String dismension)
	{
		List<String> dateList = new ArrayList<String>();
		if (!SetUtils.isNullList(list))
		{
			// 取得所有的日期
			for (List<IndicatorAnalysisVal> paramDataList : list)
			{
				for (IndicatorAnalysisVal monthData : paramDataList)
				{
					String key = monthData.getYear() + StringUtils.lpad(String.valueOf(monthData.getMonth()), 2, '0');
					if (!dateList.contains(key))
					{
						dateList.add(key);
					}
				}
			}
		}

		List<IndicatorAnalysisVal> dataList = new ArrayList<IndicatorAnalysisVal>();
		Calendar c = Calendar.getInstance();
		// 取得指定日期每个参数数据集的数据
		for (String date : dateList)
		{
			c.setTime(DateFormat.parse(date + "01", "yyyyMMdd"));

			IndicatorAnalysisVal result = new IndicatorAnalysisVal();
			result.setYear(c.get(Calendar.YEAR));
			result.setMonth(c.get(Calendar.MONTH) + 1);
			this.resetDismension(result, dismension);

			List<IndicatorAnalysisVal> monthDataList = new ArrayList<IndicatorAnalysisVal>();
			for (List<IndicatorAnalysisVal> paramDataList : list)
			{
				IndicatorAnalysisVal val = this.getMonthData(date, paramDataList);
				monthDataList.add(val);
			}
			result.setResult(this.calculate(indicator.getFunction(), monthDataList));
			dataList.add(result);
		}
		return dataList;
	}

	private Double calculate(FunctionBean functionBean, List<IndicatorAnalysisVal> monthDataList)
	{
		if (functionBean == null)
		{
			return SetUtils.isNullList(monthDataList) ? 0d : monthDataList.get(0).getResult();
		}
		AbstractFunction function = FunctionFactory.createFunction(functionBean.getClassName(), monthDataList);
		if (function == null)
		{
			return 0d;
		}
		return function.calculate();
	}

	/**
	 * 从参数的数据集中，取得相同年月的数据
	 * 
	 * @param date
	 * @param monthDataList
	 * @return
	 */
	private IndicatorAnalysisVal getMonthData(String date, List<IndicatorAnalysisVal> monthDataList)
	{
		if (!SetUtils.isNullList(monthDataList))
		{
			for (IndicatorAnalysisVal val : monthDataList)
			{
				String key = val.getYear() + StringUtils.lpad(String.valueOf(val.getMonth()), 2, '0');
				if (date.equals(key))
				{
					return val;
				}
			}
		}
		return null;
	}

	private List<IndicatorAnalysisVal> listIndicatorValBeforeBaseDate(String indicatorValId, Date baseTime, IndicatorTimeRangeEnum timeRange)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		List<Integer> monthDateList = IndicatorUtil.getMonthDateList(baseTime, timeRange, false);

		IndicatorAnalysisVal params = new IndicatorAnalysisVal();
		String fromStr = String.valueOf(monthDateList.get(0));
		params.put("FROMYEAR", fromStr.substring(0,4));
		params.put("FROMMONTH", fromStr.substring(5));
		String toStr = String.valueOf(monthDateList.get(monthDateList.size() - 1));
		params.put("TOYEAR", toStr.substring(0,4));
		params.put("TOMONTH", toStr.substring(5));
		params.setId(indicatorValId);
		List<IndicatorAnalysisVal> list = sds.query(IndicatorAnalysisVal.class, params);
		return list;
	}

	private List<IndicatorAnalysisVal> listIndicatorVal(String indicatorValId, Date baseTime, IndicatorTimeRangeEnum timeRange)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Calendar c = Calendar.getInstance();
		c.setTime(baseTime);

		IndicatorAnalysisVal params = new IndicatorAnalysisVal();
		params.put("YEAR", c.get(Calendar.YEAR));
		if (timeRange == IndicatorTimeRangeEnum.MONTH)
		{
			params.put("MONTH", c.get(Calendar.MONTH) + 1);
		}
		else if (timeRange == IndicatorTimeRangeEnum.QUARTER)
		{
			Date from = DateFormat.parse(DateFormat.getFirstDayOfQuarterYear(baseTime));
			Date to = DateFormat.parse(DateFormat.getLastDayOfQuarterYear(baseTime));
			String fromStr = DateFormat.format(from, "yyyyMM");
			params.put("FROMYEAR", fromStr.substring(0,4));
			params.put("FROMMONTH", fromStr.substring(5));
			String toStr = DateFormat.format(to, "yyyyMM");
			params.put("TOYEAR", toStr.substring(0,4));
			params.put("TOMONTH", toStr.substring(5));
		}
		else if (timeRange == IndicatorTimeRangeEnum.HALF_YEAR)
		{
			Date from = DateFormat.parse(DateFormat.getFirstDayOfHalfYear(baseTime));
			Date to = DateFormat.parse(DateFormat.getLastDayOfHalfYear(baseTime));
			String fromStr = DateFormat.format(from, "yyyyMM");
			params.put("FROMYEAR", fromStr.substring(0,4));
			params.put("FROMMONTH", fromStr.substring(5));
			String toStr = DateFormat.format(to, "yyyyMM");
			params.put("TOYEAR", toStr.substring(0,4));
			params.put("TOMONTH", toStr.substring(5));
		}
		params.setId(indicatorValId);

		return sds.query(IndicatorAnalysisVal.class, params);
	}

	/**
	 * 取得所有维度
	 * 维度只能有三种情况：
	 * 1、都没有维度
	 * 2、都有维度，并且维度相同
	 * 3、部分有维度，部分没有维度，有维度的维度相同
	 * 
	 * @param list
	 * @return
	 */
	private List<String> mergeDismension(List<Map<String, List<IndicatorAnalysisVal>>> list)
	{
		Set<String> set = new HashSet<String>();
		if (!SetUtils.isNullList(list))
		{
			for (Map<String, List<IndicatorAnalysisVal>> map : list)
			{
				if (!SetUtils.isNullSet(map.keySet()))
				{
					set.addAll(map.keySet());
				}
			}
		}
		set.remove(IndicatorConstants.DISMENSION_WHEN_NULL);
		return new ArrayList<String>(set);
	}

	/**
	 * 根据维度和给定的日期（年月）列表，重构分析结果集合
	 * 对于每一个维度，如果给定日期没有值，则会被构造一个空值
	 * 
	 * @param list
	 * @param dateList
	 * @return
	 */
	private Map<String, List<IndicatorAnalysisVal>> buildAnalysisValForDismensionAndDate(IndicatorDismension filterDismension, List<IndicatorAnalysisVal> list,
			IndicatorTimeRangeEnum timeRange)
	{
		if (SetUtils.isNullList(list))
		{
			return null;
		}

		List<IndicatorAnalysisVal> dataList = new ArrayList<IndicatorAnalysisVal>();
		if (filterDismension == null)
		{
			// 没有设定过滤维度，合并所有数据
			dataList.addAll(this.mergeByTime(list));
		}
		else
		{
			// 根据设定的过滤维度，对数据进行合并
			List<IndicatorDismensionFilter> filters = filterDismension.getFilters();
			if (!SetUtils.isNullList(filters))
			{
				for (IndicatorDismensionFilter filter : filters)
				{
					List<IndicatorAnalysisVal> tmpList = this.filterByDismension(filter, list);
					if (!SetUtils.isNullList(tmpList))
					{
						dataList.addAll(tmpList);
					}
				}
			}
		}

		if (SetUtils.isNullList(dataList))
		{
			return null;
		}

		Map<String, List<IndicatorAnalysisVal>> map = new HashMap<String, List<IndicatorAnalysisVal>>();
		if (!SetUtils.isNullList(dataList))
		{
			// 按维度分组
			for (IndicatorAnalysisVal analysisVal : dataList)
			{
				String dismension = this.getDismension(analysisVal, IndicatorConstants.DISMENSION_WHEN_NULL);
				if (!map.containsKey(dismension))
				{
					map.put(dismension, new ArrayList<IndicatorAnalysisVal>());
				}
				map.get(dismension).add(analysisVal);
			}
		}

		// 是否累计
		boolean isAccumulate = this.stubService.getIndicatorConfigStub().isAccumulate(dataList.get(0).getId());

		// 对月度数据进行合并计算
		if (!SetUtils.isNullMap(map))
		{
			for (String dismension : map.keySet())
			{
				List<IndicatorAnalysisVal> tmpList = map.get(dismension);
				List<IndicatorAnalysisVal> newList = this.mergeByTimeRange(tmpList, timeRange, isAccumulate);
				map.put(dismension, newList);
			}
		}

		return map;
	}

	/**
	 * 相同年月的数据进行合并
	 * 
	 * @param list
	 * @return
	 */
	private List<IndicatorAnalysisVal> mergeByTime(List<IndicatorAnalysisVal> list)
	{
		Map<String, IndicatorAnalysisVal> valMap = new HashMap<String, IndicatorAnalysisVal>();
		for (IndicatorAnalysisVal v : list)
		{
			String key = v.getYear() + StringUtils.lpad(String.valueOf(v.getMonth()), 2, '0');
			if (!valMap.containsKey(key))
			{
				this.resetDismension(v, null);
				valMap.put(key, v);
			}
			else
			{
				valMap.get(key).setResult(new BigDecimal(String.valueOf(valMap.get(key).getResult())).add(new BigDecimal(String.valueOf(v.getResult()))).doubleValue());
			}
		}

		return new ArrayList<IndicatorAnalysisVal>(valMap.values());
	}

	/**
	 * 过滤合并结果集
	 * 
	 * @param filter
	 * @param list
	 * @return
	 */
	private List<IndicatorAnalysisVal> filterByDismension(IndicatorDismensionFilter filter, List<IndicatorAnalysisVal> list)
	{
		String filters = filter.getFilters();
		if (StringUtils.isNullString(filter.getFilters()))
		{
			return list;
		}

		Map<String, IndicatorAnalysisVal> valMap = new HashMap<String, IndicatorAnalysisVal>();
		for (IndicatorAnalysisVal v : list)
		{
			String dismension = this.getDismension(v, IndicatorConstants.DISMENSION_WHEN_NULL);
			String[] filterArr = filters.split(";");

			boolean isMatch = true;
			// 有一个未匹配，则该记录不匹配
			for (String filterVal : filterArr)
			{
				if (!(";" + dismension + ";").contains(";" + filterVal + ";"))
				{
					isMatch = false;
				}
			}

			if (isMatch)
			{
				String key = v.getYear() + StringUtils.lpad(String.valueOf(v.getMonth()), 2, '0');
				if (!valMap.containsKey(key))
				{
					this.resetDismension(v, filters);
					valMap.put(key, v);
				}
				else
				{
					valMap.get(key).setResult(new BigDecimal(String.valueOf(valMap.get(key).getResult())).add(new BigDecimal(String.valueOf(v.getResult()))).doubleValue());
				}
			}
		}

		return new ArrayList<IndicatorAnalysisVal>(valMap.values());
	}

	private void resetDismension(IndicatorAnalysisVal v, String dismension)
	{
		if (v != null)
		{
			for (int i = 1; i <= 10; i++)
			{
				v.clear("DISMENSION" + i);
			}
			if (!StringUtils.isNullString(dismension))
			{
				String[] tmpArr = dismension.split(";");
				for (int i = 0; i < tmpArr.length; i++)
				{
					v.put("DISMENSION" + (i + 1), tmpArr[i]);
				}
			}
		}
	}

	// 根据时间周期，合并计算
	private List<IndicatorAnalysisVal> mergeByTimeRange(List<IndicatorAnalysisVal> list, IndicatorTimeRangeEnum timeRange, boolean isAccumulate)
	{
		Map<String, List<IndicatorAnalysisVal>> tmpMap = new HashMap<String, List<IndicatorAnalysisVal>>();
		if (SetUtils.isNullList(list))
		{
			return list;
		}
		if (timeRange == IndicatorTimeRangeEnum.MONTH)
		{
			return list;
		}
		else if (timeRange == IndicatorTimeRangeEnum.YEAR)
		{
			for (IndicatorAnalysisVal v : list)
			{
				String date = DateFormat.getLastDayOfYear(DateFormat.parse(v.getYear() + StringUtils.lpad(String.valueOf(v.getMonth()), 2, '0'), "yyyyMM"));
				if (!tmpMap.containsKey(date))
				{
					tmpMap.put(date, new ArrayList<IndicatorAnalysisVal>());
				}
				tmpMap.get(date).add(v);
			}
		}
		else if (timeRange == IndicatorTimeRangeEnum.HALF_YEAR)
		{
			for (IndicatorAnalysisVal v : list)
			{
				String date = DateFormat.getLastDayOfHalfYear(DateFormat.parse(v.getYear() + StringUtils.lpad(String.valueOf(v.getMonth()), 2, '0'), "yyyyMM"));
				if (!tmpMap.containsKey(date))
				{
					tmpMap.put(date, new ArrayList<IndicatorAnalysisVal>());
				}
				tmpMap.get(date).add(v);
			}
		}
		else if (timeRange == IndicatorTimeRangeEnum.QUARTER)
		{
			for (IndicatorAnalysisVal v : list)
			{
				String date = DateFormat.getLastDayOfQuarterYear(DateFormat.parse(v.getYear() + StringUtils.lpad(String.valueOf(v.getMonth()), 2, '0'), "yyyyMM"));
				if (!tmpMap.containsKey(date))
				{
					tmpMap.put(date, new ArrayList<IndicatorAnalysisVal>());
				}
				tmpMap.get(date).add(v);
			}
		}

		List<IndicatorAnalysisVal> finalList = new ArrayList<IndicatorAnalysisVal>();
		if (!SetUtils.isNullMap(tmpMap))
		{
			for (String date : tmpMap.keySet())
			{
				List<IndicatorAnalysisVal> tmpList = tmpMap.get(date);
				if (!SetUtils.isNullList(tmpList))
				{
					// 累计
					if (isAccumulate)
					{
						IndicatorAnalysisVal v = tmpList.get(0);
						for (int i = 1; i < tmpList.size(); i++)
						{
							IndicatorAnalysisVal tmpV = tmpList.get(i);
							v.setResult(new BigDecimal(String.valueOf(v.getResult())).add(new BigDecimal(String.valueOf(tmpV.getResult()))).doubleValue());
						}
						finalList.add(v);
					}
					else
					{
						// 取月最大的一条记录作为计算结果
						IndicatorAnalysisVal val = this.getValOfMaxMonth(tmpList);
						finalList.add(val);
					}
				}
			}
		}
		Collections.sort(finalList, new Comparator<IndicatorAnalysisVal>() {

			@Override
			public int compare(IndicatorAnalysisVal o1, IndicatorAnalysisVal o2)
			{
				if (o1.getYear().equals(o2.getYear()))
				{
					return o1.getMonth().compareTo(o2.getMonth());
				}
				return o1.getYear().compareTo(o2.getYear());
			}
		});
		return finalList;
	}

	private IndicatorAnalysisVal getValOfMaxMonth(List<IndicatorAnalysisVal> list)
	{
		if (!SetUtils.isNullList(list))
		{
			int month = list.get(0).getMonth();
			IndicatorAnalysisVal val = list.get(0);
			for (int i = 1; i < list.size(); i++)
			{
				if (list.get(i).getMonth() > month)
				{
					month = list.get(i).getMonth();
					val = list.get(i);
				}
			}
			return val;
		}
		return null;
	}

	private String getDismension(IndicatorAnalysisVal analysisVal, String defaultWhenNull)
	{
		StringBuffer buffer = new StringBuffer();
		if (!StringUtils.isNullString(analysisVal.getDismension1()))
		{
			buffer.append(analysisVal.getDismension1());
			if (!StringUtils.isNullString(analysisVal.getDismension2()))
			{
				buffer.append(";");
				buffer.append(analysisVal.getDismension2());
				if (!StringUtils.isNullString(analysisVal.getDismension3()))
				{
					buffer.append(";");
					buffer.append(analysisVal.getDismension3());
					if (!StringUtils.isNullString(analysisVal.getDismension4()))
					{
						buffer.append(";");
						buffer.append(analysisVal.getDismension4());
						if (!StringUtils.isNullString(analysisVal.getDismension5()))
						{
							buffer.append(";");
							buffer.append(analysisVal.getDismension5());
							if (!StringUtils.isNullString(analysisVal.getDismension6()))
							{
								buffer.append(";");
								buffer.append(analysisVal.getDismension6());
								if (!StringUtils.isNullString(analysisVal.getDismension7()))
								{
									buffer.append(";");
									buffer.append(analysisVal.getDismension7());
									if (!StringUtils.isNullString(analysisVal.getDismension8()))
									{
										buffer.append(";");
										buffer.append(analysisVal.getDismension8());
										if (!StringUtils.isNullString(analysisVal.getDismension9()))
										{
											buffer.append(";");
											buffer.append(analysisVal.getDismension9());
											if (!StringUtils.isNullString(analysisVal.getDismension10()))
											{
												buffer.append(";");
												buffer.append(analysisVal.getDismension10());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else
		{
			analysisVal.setDismension1(defaultWhenNull);
			return defaultWhenNull;
		}

		return buffer.toString();
	}
}
