package dyna.common.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.ppms.indicator.IndicatorAnalysisVal;
import dyna.common.bean.data.ppms.indicator.IndicatorConstants;
import dyna.common.systemenum.ppms.IndicatorTimeRangeEnum;

public class IndicatorUtil
{
	/**
	 * 取得相同周期的数据
	 * 
	 * @param date
	 *            yyyyMM
	 * @param list
	 * @param timeRange
	 * @return
	 */
	public static double getValue(String date, List<IndicatorAnalysisVal> list, IndicatorTimeRangeEnum timeRange)
	{
		if (!SetUtils.isNullList(list))
		{
			for (IndicatorAnalysisVal v : list)
			{
				if (isSameTimeRange(v.getYear() + StringUtils.lpad(String.valueOf(v.getMonth()), 2, '0'), date, timeRange))
				{
					return v.getResult();
				}
			}
		}
		return 0;
	}

	public static boolean isWithDismension(Map<String, List<IndicatorAnalysisVal>> valWithDismensionMap)
	{
		return !SetUtils.isNullMap(valWithDismensionMap) && !valWithDismensionMap.containsKey(IndicatorConstants.DISMENSION_WHEN_NULL);
	}

	/**
	 * 往前移动时间
	 * 
	 * @param c
	 * @param timeRange
	 * @param amount
	 */
	public static void moveTimeForward(Calendar c, IndicatorTimeRangeEnum timeRange, int amount)
	{
		switch (timeRange)
		{
		case YEAR:
			String firstStr = DateFormat.getFirstDayOfYear(c.getTime());
			c.setTime(DateFormat.parse(firstStr));
			c.add(Calendar.YEAR, -1 * (amount - 1));
			break;
		case HALF_YEAR:
			// 首先移动到当前半年的第一天
			firstStr = DateFormat.getFirstDayOfHalfYear(c.getTime());
			c.setTime(DateFormat.parse(firstStr));
			// 然后异动到当前半年*6月
			c.add(Calendar.MONTH, -1 * (amount - 1) * 6);
			break;
		case QUARTER:
			// 首先移动到当前季度的第一天
			firstStr = DateFormat.getFirstDayOfQuarterYear(c.getTime());
			c.setTime(DateFormat.parse(firstStr));
			// 然后移动到除当前季度剩余季度*3月
			c.add(Calendar.MONTH, -1 * (amount - 1) * 3);
			break;
		default:
			c.add(Calendar.MONTH, -1 * (amount - 1));
		}
	}

	/**
	 * 取得横轴时间刻度
	 * 
	 * @param baseTime
	 * @param timeRange
	 * @return
	 */
	public static List<String> getDomainMarkList(Date baseTime, IndicatorTimeRangeEnum timeRange)
	{
		List<String> marks = new ArrayList<String>();
		List<Integer> dateList = getMonthDateList(baseTime, timeRange, false);
		for (Integer date : dateList)
		{
			if (timeRange == IndicatorTimeRangeEnum.MONTH)
			{
				marks.add(String.valueOf(date));
			}
			else if (timeRange == IndicatorTimeRangeEnum.QUARTER)
			{
				String quarter = DateFormat.getFirstDayOfQuarterYear(DateFormat.parse(String.valueOf(date), "yyyyMM"));
				Date quarterDate = DateFormat.parse(quarter);
				String quarter_ = DateFormat.format(quarterDate, "yyyyMM");
				if (!marks.contains(quarter_))
				{
					marks.add(quarter_);
				}
			}
			else if (timeRange == IndicatorTimeRangeEnum.HALF_YEAR)
			{
				String halfYear = DateFormat.getFirstDayOfHalfYear(DateFormat.parse(String.valueOf(date), "yyyyMM"));
				Date halfYearDate = DateFormat.parse(halfYear);
				String halfYear_ = DateFormat.format(halfYearDate, "yyyyMM");
				if (!marks.contains(halfYear_))
				{
					marks.add(halfYear_);
				}
			}
			else if (timeRange == IndicatorTimeRangeEnum.YEAR)
			{
				String year = String.valueOf(date).substring(0, 4) + "01";
				if (!marks.contains(year))
				{
					marks.add(year);
				}
			}
			else
			{
				continue;
			}
		}
		return marks;
	}

	/**
	 * 是否是同一个周期范围内
	 * 
	 * @param date1
	 *            yyyyMM
	 * @param date2
	 *            yyyyMM
	 * @param timeRange
	 * @return
	 */
	public static boolean isSameTimeRange(String date1, String date2, IndicatorTimeRangeEnum timeRange)
	{
		if (timeRange == null)
		{
			return false;
		}
		if (timeRange == IndicatorTimeRangeEnum.MONTH)
		{
			return date1.equals(date2);
		}
		else if (timeRange == IndicatorTimeRangeEnum.QUARTER)
		{
			String quarter1 = DateFormat.getFirstDayOfQuarterYear(DateFormat.parse(date1, "yyyyMM"));
			String quarter2 = DateFormat.getFirstDayOfQuarterYear(DateFormat.parse(date2, "yyyyMM"));
			return quarter1.equals(quarter2);
		}
		else if (timeRange == IndicatorTimeRangeEnum.HALF_YEAR)
		{
			String halfYear1 = DateFormat.getFirstDayOfHalfYear(DateFormat.parse(date1, "yyyyMM"));
			String halfYear2 = DateFormat.getFirstDayOfHalfYear(DateFormat.parse(date2, "yyyyMM"));
			return halfYear1.equals(halfYear2);
		}
		else if (timeRange == IndicatorTimeRangeEnum.YEAR)
		{
			String year1 = date1.substring(0, 4);
			String year2 = date2.substring(0, 4);
			return year1.equals(year2);
		}
		return false;
	}

	// 取得指定日期范围内的所有日期，格式yyyyMM
	public static List<Integer> getMonthDateList(Date baseTime, IndicatorTimeRangeEnum timeRange, boolean isOnlyCurrent)
	{
		baseTime = getBaseTimeByRange(baseTime, timeRange);
		List<Integer> dateList = new ArrayList<Integer>();

		Calendar c = Calendar.getInstance();
		c.setTime(baseTime);
		int limit = isOnlyCurrent ? 1 : getLimitNumberOfMonth(timeRange);
		IndicatorUtil.moveTimeForward(c, timeRange, limit);

		Date startTime = c.getTime();
		while (Integer.valueOf(DateFormat.format(startTime, "yyyyMM")) <= Integer.valueOf(DateFormat.format(baseTime, "yyyyMM")))
		{
			dateList.add(Integer.valueOf(DateFormat.format(startTime, "yyyyMM")));

			c.setTime(startTime);
			c.add(Calendar.MONTH, 1);
			startTime = c.getTime();
		}

		Collections.sort(dateList);
		return dateList;
	}

	private static Date getBaseTimeByRange(Date baseTime, IndicatorTimeRangeEnum timeRange)
	{
		if (timeRange == IndicatorTimeRangeEnum.QUARTER)
		{
			return DateFormat.parse(DateFormat.getLastDayOfQuarterYear(baseTime));
		}
		if (timeRange == IndicatorTimeRangeEnum.HALF_YEAR)
		{
			return DateFormat.parse(DateFormat.getLastDayOfHalfYear(baseTime));
		}
		if (timeRange == IndicatorTimeRangeEnum.YEAR)
		{
			return DateFormat.parse(DateFormat.getLastDayOfYear(baseTime));
		}
		return baseTime;
	}

	public static int getLimitNumberOfMonth(IndicatorTimeRangeEnum timeRange)
	{
		switch (timeRange)
		{
		case YEAR:
			return IndicatorConstants.LIMIT_NUMBER_OF_YEAR;
		case HALF_YEAR:
			return IndicatorConstants.LIMIT_NUMBER_OF_HALFYEAR;
		case QUARTER:
			return IndicatorConstants.LIMIT_NUMBER_OF_QUARTER;
		}
		return IndicatorConstants.LIMIT_NUMBER_OF_MONTH;
	}
}
