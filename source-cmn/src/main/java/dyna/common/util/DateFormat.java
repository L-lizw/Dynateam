/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: this is utility class for common date format.
 * Xiasheng, 2010-04-21
 */

package dyna.common.util;

import java.text.Collator;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 与日期相关的操作
 * 
 * @author Wanglei
 * 
 */
public class DateFormat
{
	public static final String								PTN_YMD			= "yyyy-MM-dd";
	public static final String								PTN_YMDHM		= "yyyy-MM-dd HH:mm";
	public static final String								PTN_YMDHMS		= "yyyy-MM-dd HH:mm:ss";
	public static final String								PTN_HMS			= "HH:mm:ss[yyyy-MM-dd]";
	public static final String								PTN_TIMESTAMP	= "yyyyMMddHHmmssSSS";
	private static final AtomicLong							LAST_TIME_MS	= new AtomicLong();

	private static final ThreadLocal<Map<String, Format>>	FORMATER_LOCAL	= new ThreadLocal<Map<String, Format>>()
																			{
																				@Override
																				protected Map<String, Format> initialValue()
																				{
																					return new HashMap<String, Format>();
																				}
																			};

	static
	{
		FORMATER_LOCAL.get().put(PTN_YMD, new SimpleDateFormat(PTN_YMD));
		FORMATER_LOCAL.get().put(PTN_YMDHM, new SimpleDateFormat(PTN_YMDHM));
		FORMATER_LOCAL.get().put(PTN_YMDHMS, new SimpleDateFormat(PTN_YMDHMS));
		FORMATER_LOCAL.get().put(PTN_HMS, new SimpleDateFormat(PTN_HMS));
		FORMATER_LOCAL.get().put(PTN_TIMESTAMP, new SimpleDateFormat(PTN_TIMESTAMP));
	}

	private static Format getFormat(String pattern)
	{
		Format format = FORMATER_LOCAL.get().get(pattern);
		if (format == null)
		{
			format = new SimpleDateFormat(pattern);
			FORMATER_LOCAL.get().put(pattern, format);
		}
		return format;
	}

	public static String format(Date date, String pattern)
	{
		if (date == null)
		{
			return null;
		}
		return getFormat(pattern).format(date);
	}

	public static SimpleDateFormat getSDFYMD()
	{
		return (SimpleDateFormat) getFormat(PTN_YMD);
	}

	public static SimpleDateFormat getSDFHMS()
	{
		return (SimpleDateFormat) getFormat(PTN_HMS);
	}

	public static SimpleDateFormat getSDFYMDHM()
	{
		return (SimpleDateFormat) getFormat(PTN_YMDHM);
	}

	public static SimpleDateFormat getSDFYMDHMS()
	{
		return (SimpleDateFormat) getFormat(PTN_YMDHMS);
	}

	public static SimpleDateFormat getSDFTimeStamp()
	{
		return (SimpleDateFormat) getFormat(PTN_TIMESTAMP);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static Date getSysDate()
	{
		return new Date();
	}

	/**
	 * 获取当前日期附近的日期
	 * 
	 * @param amount
	 *            amount>0时，当前日期的后几天
	 *            amount<0时，当前日期的前几天
	 * @return
	 */
	public static Date getSysDate(int amount)
	{
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, amount);
		return calendar.getTime();
	}

	/**
	 * 将日期格式化为yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYMD(Date date)
	{
		if (date != null)
		{
			return getSDFYMD().format(date);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 将日期格式化为yyyy-MM-dd HH-mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYMDHM(Date date)
	{
		if (date != null)
		{
			return getSDFYMDHM().format(date);
		}
		else
		{
			return null;
		}
	}

	/**
	 * 将日期格式化为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYMDHMS(Date date)
	{
		if (date != null)
		{
			return getSDFYMDHMS().format(date);
		}
		else
		{
			return null;
		}
	}

	/**
	 * this method can parse dateStr pattern by
	 * "yyyy-MM-dd" and "yyyy-MM-dd HH:mm:ss"
	 * to Date Object.
	 * 
	 * @param dateStr
	 * @return Date if pattern matches. Otherwise null.
	 */
	public static Date parse(String dateStr)
	{
		Date retDate = null;
		try
		{
			retDate = getSDFYMD().parse(dateStr);
		}
		catch (ParseException e)
		{
			try
			{
				retDate = getSDFYMDHMS().parse(dateStr);
			}
			catch (ParseException e1)
			{
				return null;
			}
		}
		return retDate;
	}

	/**
	 * 按照指定的格式pattern将指定的日期字符串转换为对应的日期对象
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static Date parse(String dateString, String pattern)
	{
		try
		{
			return ((SimpleDateFormat) getFormat(pattern)).parse(dateString);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 获得当前日期与本周日相差的天数
	 * 
	 * @return int
	 */
	private static int getMondayPlus(Date today)
	{
		Calendar cd = Calendar.getInstance();
		cd.setTime(today);

		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
		if (dayOfWeek == 1)
		{
			return 0;
		}
		else
		{
			return 1 - dayOfWeek;
		}
	}

	/**
	 * 获取本周的第一天
	 * 
	 * @return
	 */
	public static String getMondayOFWeek(Date today)
	{
		int mondayPlus = getMondayPlus(today);
		// GregorianCalendar currentDate = new GregorianCalendar();

		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(today);

		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();

		String df = formatYMD(monday);
		return df;
	}

	/**
	 * 获得本周的周日的日期
	 * 
	 * @return
	 */
	public static String getSundayOFWeek(Date today)
	{
		int mondayPlus = getMondayPlus(today);

		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(today);
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		Date date = currentDate.getTime();
		String df = formatYMD(date);
		return df;
	}

	/**
	 * 获取当前月的1号
	 * 
	 * @return
	 */
	public static String getFirstDayOfMonth(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取当前年的1号
	 * 
	 * @return
	 */
	public static String getFirstDayOfYear(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.MONTH, Calendar.JANUARY);// 设置为1月
		lastDate.set(Calendar.DATE, 1);// 设为1月的1号
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取当前半年的第一天
	 * 
	 * @return
	 */
	public static String getFirstDayOfHalfYear(Date today)
	{
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);
		int month = lastDate.get(Calendar.MONTH);
		if (month > 5)
		{
			// 后半年
			lastDate.set(Calendar.MONTH, Calendar.JULY);// 设置为7月
			return getFirstDayOfMonth(lastDate.getTime());
		}
		else
		{
			// 前半年
			return getFirstDayOfYear(today);
		}
	}

	/**
	 * 取得当前日期所属季度
	 * 
	 * @param today
	 * @return
	 */
	public static String getQuarterOfYear(Date today)
	{
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);
		int month = lastDate.get(Calendar.MONTH);
		switch (month)
		{
		case 0:
		case 1:
		case 2:
			return "1";
		case 3:
		case 4:
		case 5:
			return "2";
		case 6:
		case 7:
		case 8:
			return "3";
		default:
			return "4";
		}
	}

	/**
	 * 获取当前季度的第一天
	 * 
	 * @return
	 */
	public static String getFirstDayOfQuarterYear(Date today)
	{
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);
		int month = lastDate.get(Calendar.MONTH);
		switch (month)
		{
		case 0:
		case 1:
		case 2:
			return getFirstDayOfYear(today);
		case 3:
		case 4:
		case 5:
			lastDate.set(Calendar.MONTH, Calendar.APRIL);
			return getFirstDayOfMonth(lastDate.getTime());
		case 6:
		case 7:
		case 8:
			lastDate.set(Calendar.MONTH, Calendar.JULY);
			return getFirstDayOfMonth(lastDate.getTime());
		default:
			lastDate.set(Calendar.MONTH, Calendar.OCTOBER);
			return getFirstDayOfMonth(lastDate.getTime());
		}
	}

	/**
	 * 获取当前季度的最后一天
	 * 
	 * @return
	 */
	public static String getLastDayOfQuarterYear(Date today)
	{
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);
		int month = lastDate.get(Calendar.MONTH);
		switch (month)
		{
		case 0:
		case 1:
		case 2:
			lastDate.set(Calendar.MONTH, Calendar.MARCH);
			return getLastDayOfMonth(lastDate.getTime());
		case 3:
		case 4:
		case 5:
			lastDate.set(Calendar.MONTH, Calendar.JUNE);
			return getLastDayOfMonth(lastDate.getTime());
		case 6:
		case 7:
		case 8:
			lastDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
			return getLastDayOfMonth(lastDate.getTime());
		default:
			lastDate.set(Calendar.MONTH, Calendar.DECEMBER);
			return getLastDayOfMonth(lastDate.getTime());
		}
	}

	/**
	 * 获取当前月的最后一天
	 * 
	 * @return
	 */
	public static String getLastDayOfMonth(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取当前年的最后一天
	 * 
	 * @return
	 */
	public static String getLastDayOfYear(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.MONTH, Calendar.JANUARY);// 设置为1月
		lastDate.set(Calendar.DATE, 1);// 设为1月的1号
		lastDate.add(Calendar.YEAR, 1);// 加一年，变为下年的1月1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为当年最后一天
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取当前半年的最后一天
	 * 
	 * @return
	 */
	public static String getLastDayOfHalfYear(Date today)
	{
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);
		int month = lastDate.get(Calendar.MONTH);
		if (month > 5)
		{
			// 当年最后一天
			return getLastDayOfYear(today);
		}
		else
		{
			lastDate.set(Calendar.MONTH, Calendar.JUNE);// 设置为6月
			return getLastDayOfMonth(lastDate.getTime());
		}
	}

	/**
	 * 获取上周的第一天
	 * 
	 * @return
	 */
	public static String getMondayOFLastWeek(Date today)
	{
		int mondayPlus = getMondayPlus(today);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(today);
		currentDate.add(GregorianCalendar.DATE, mondayPlus - 7);
		Date monday = currentDate.getTime();

		String df = formatYMD(monday);
		return df;
	}

	/**
	 * 获得上周的周日的日期
	 * 
	 * @return
	 */
	public static String getSundayOFLastWeek(Date today)
	{
		int mondayPlus = getMondayPlus(today);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(today);
		currentDate.add(GregorianCalendar.DATE, mondayPlus - 1);
		Date date = currentDate.getTime();
		String df = formatYMD(date);
		return df;
	}

	/**
	 * 获取下周的第一天
	 * 
	 * @return
	 */
	public static String getMondayOFNextWeek(Date today)
	{
		int mondayPlus = getMondayPlus(today);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(today);
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		Date monday = currentDate.getTime();

		String df = formatYMD(monday);
		return df;
	}

	/**
	 * 获得下周的周日的日期
	 * 
	 * @return
	 */
	public static String getSundayOFNextWeek(Date today)
	{
		int mondayPlus = getMondayPlus(today);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(today);
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 13);
		Date date = currentDate.getTime();
		String df = formatYMD(date);
		return df;
	}

	/**
	 * 获取上月的1号
	 * 
	 * @return
	 */
	public static String getFirstDayOfLastMonth(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, -1);// 减去一个月，变为上月的1号
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取上月的最后一天
	 * 
	 * @return
	 */
	public static String getLastDayOfLastMonth(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为上个月最后一天
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取下月的1号
	 * 
	 * @return
	 */
	public static String getFirstDayOfNextMonth(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 1);// 加上一个月，变为下月的1号
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取下月的最后一天
	 * 
	 * @return
	 */
	public static String getLastDayOfLastNextMonth(Date today)
	{
		String str = "";
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(today);

		lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
		lastDate.add(Calendar.MONTH, 2);// 加上两个月，变为下下月的1号
		lastDate.add(Calendar.DATE, -1);// 减去一天，变为下个月最后一天
		str = formatYMD(lastDate.getTime());
		return str;
	}

	/**
	 * 获取日期中的天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDate(Date date)
	{
		if (date == null)
		{
			return 0;
		}
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		return lastDate.get(Calendar.DATE);
	}

	/**
	 * 获取日期中的分
	 * 
	 * @param date
	 * @return
	 */
	public static int getMinutes(Date date)
	{
		if (date == null)
		{
			return 0;
		}
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		return lastDate.get(Calendar.MINUTE);
	}

	/**
	 * 获取日期中的小时数
	 * 
	 * @param date
	 * @return
	 */
	public static int getHours(Date date)
	{
		if (date == null)
		{
			return 0;
		}
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(date);
		return lastDate.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 比较两个日期的大小（只比较年月日，不比较时分秒）
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return 大于0：前者大于后者;0：二者相等;小于0：后者大于前者
	 */
	public static int compareDate(Date fromDate, Date toDate)
	{
		if (fromDate == null && toDate != null)
		{
			return -1;
		}
		else if (fromDate == null && toDate == null)
		{
			return 0;
		}
		else if (fromDate != null && toDate == null)
		{
			return 1;
		}
		else if (fromDate != null && toDate != null)
		{
			String fromDateStr = formatYMD(fromDate);
			String toDateStr = formatYMD(toDate);

			return Collator.getInstance().compare(fromDateStr, toDateStr);
		}
		return 0;
	}

	public static int compareDate(Date fromDate, Date toDate, String pattern)
	{
		if (fromDate == null && toDate != null)
		{
			return -1;
		}
		else if (fromDate == null && toDate == null)
		{
			return 0;
		}
		else if (fromDate != null && toDate == null)
		{
			return 1;
		}
		else if (fromDate != null && toDate != null)
		{
			String fromDateStr = format(fromDate, pattern);
			String toDateStr = format(toDate, pattern);

			return Collator.getInstance().compare(fromDateStr, toDateStr);
		}
		return 0;
	}

	/**
	 * 取得两个日期的间隔天数
	 * 计算方法为toDate-fromDate
	 * 
	 * @param toDate
	 * @param fromDate
	 * @return 间隔天数
	 */
	@SuppressWarnings("deprecation")
	public static long getDifferenceDay(Date toDate, Date fromDate)
	{
		if (fromDate == null)
		{
			return 1;
		}
		if (toDate == null)
		{
			return -1;
		}

		Calendar fromCal = Calendar.getInstance();

		fromCal.set(fromDate.getYear(), fromDate.getMonth(), fromDate.getDate());
		Calendar toCal = Calendar.getInstance();
		toCal.set(toDate.getYear(), toDate.getMonth(), toDate.getDate());

		long t = toCal.getTimeInMillis() - fromCal.getTimeInMillis();
		int x = 1;
		if (t <= 0)
		{
			x = -1;
		}
		double d = (Double.valueOf(Math.abs(t))) / (24 * 3600 * 1000);

		return (long) Math.floor(d + 0.5f) * x;

	}

	public static long getDifferenceHour(Date toDate, Date fromDate)
	{
		long times = toDate.getTime() - fromDate.getTime();
		return (long) Math.floor(Double.valueOf(times) / (60 * 60 * 1000));
	}

	/**
	 * 获取指定日期时间对应的毫秒数
	 * 
	 * @param date
	 * @return
	 */
	public static long getTimeMillis(Date date)
	{
		return date.getTime();
	}

	/**
	 * 根据指定开始日期以及间隔计算出延迟时间
	 * 
	 * @param targetDate
	 * @param period
	 * @return
	 */
	public static long getDelay(Date targetDate, long period)
	{
		return getDelay(DateFormat.getTimeMillis(targetDate), period);
	}

	/**
	 * 根据指定开始日期以及间隔计算出延迟时间
	 * 
	 * @param targetMillis
	 * @param period
	 * @return
	 */
	public static long getDelay(long targetMillis, long period)
	{
		long delay = targetMillis - System.currentTimeMillis();
		if (period > 0)
		{
			while (delay < 0)
			{
				delay += period;
			}
		}
		return delay;
	}

	public static Date formatStringToDateYMD(String date) throws ParseException
	{
		return getSDFYMD().parse(date);
	}

	/**
	 * 获取唯一时间戳，多线程有效
	 * 
	 * @return
	 */
	public static String getUniqueTimeMills()
	{
		long now = System.currentTimeMillis();
		while (true)
		{
			long lastTime = LAST_TIME_MS.get();
			if (lastTime >= now)
			{
				now = lastTime + 1;
			}
			if (LAST_TIME_MS.compareAndSet(lastTime, now))
			{
				return String.valueOf(now);
			}
		}
	}

	/**
	 * 获取当前日期唯一时间戳，多线程有效，格式：yyyyMMddHHmmssSSS
	 * 
	 * @return
	 */
	public static String getUniqueSysDateTimeStamp()
	{
		Date date = new Date(Long.valueOf(getUniqueTimeMills()));
		return getSDFTimeStamp().format(date);
	}

	/**
	 * 返回指定日期，格式的零点(传入的日期必须是Date或者日期格式的字符串)
	 * 
	 * @param sourceDate
	 * @param pattern
	 * @return
	 */
	public static Date getDateOfBegin(Object sourceData, String pattern)
	{
		if (sourceData == null || !(sourceData instanceof Date || sourceData instanceof String))
		{
			return null;
		}
		Date sourceDate = null;
		try
		{
			if (sourceData instanceof Date)
			{
				sourceDate = (Date) sourceData;
			}
			else if (sourceData instanceof String)
			{
				sourceDate = getSDFYMD().parse((String) sourceData);
			}
			SimpleDateFormat dateFormat = ((SimpleDateFormat) getFormat(pattern));
			String dateString = getSDFYMD().format(sourceDate);

			Date date = new Date(getSDFYMD().parse(dateString).getTime());
			return dateFormat.parse(dateFormat.format(date));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 返回指定日期，格式下一天的零点
	 * 
	 * @param sourceDate
	 * @param pattern
	 * @return
	 */
	public static Date getDateOfEnd(Object sourceData, String pattern)
	{
		if (sourceData == null || !(sourceData instanceof Date || sourceData instanceof String))
		{
			return null;
		}

		try
		{
			Date sourceDate = null;
			if (sourceData instanceof Date)
			{
				sourceDate = (Date) sourceData;
			}
			else if (sourceData instanceof String)
			{
				sourceDate = getSDFYMD().parse((String) sourceData);
			}
			Calendar cd = Calendar.getInstance();
			cd.setTime(sourceDate);
			cd.add(Calendar.DATE, 1);// 增加一天
			return getDateOfBegin(cd.getTime(), pattern);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
