/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 日历中的特殊日期
 * Duanll 2012-5-7
 */
package dyna.common.bean.data.ppms;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ppms.DayOfMonthEnum;
import dyna.common.systemenum.ppms.DayOfWeekEnum;
import dyna.common.systemenum.ppms.MonthOfYearEnum;
import dyna.common.systemenum.ppms.RepeatModeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author Duanll<br>
 *         日历中的特殊日期
 *         <p>
 *         IS_NON_WORK_DAY=false,则为工作时间，工作时间范围使用PMCalendarSpecialDateTime中(BEGIN_TIME~END_TIME)的数值。
 *         <p>
 *         关于重复发生方式：
 *         <ul>
 *         每天（每隔N天） REPEAT_MODE=每天，GAP_DAY=N，<br>
 *         每周（周一到周日，CHECKBOX） REPEAT_MODE=每周，WEEKLY_DAYS设值<br>
 *         每月（日期（N）/星期（N）） REPEAT_MODE=每月，MONTHLY_DAYS非空，则为指定日期;<br>
 *         REPEAT_MODE=每月，WEEKLY_DAYS非空，则为指定星期。<br>
 *         每年（在第N月的M日） REPEAT_MODE=每年，YEARLY_MONTHS=N，MONTHLY_DAYS=M
 *         </ul>
 *         关于重复范围：
 *         <ul>
 *         BEGIN_DATE~END_DATE。
 *         </ul>
 */
public class PMCalendarSpecialDate extends SystemObjectImpl implements SystemObject
{
	private static final long				serialVersionUID	= 9091840089903186760L;

	/**
	 * 实例对象自定义编号
	 */
	public static final String				ID					= "SPECIALID";
	public static final String				NAME				= "SPECIALNAME";

	/**
	 * 重复范围：开始时间
	 * 默认值：当天
	 */
	public static final String				BEGIN_DATE			= "BEGINDATE";

	/**
	 * 重复范围：结束时间
	 * 默认值：当天
	 */
	public static final String				END_DATE			= "ENDDATE";

	/**
	 * 是否非工作日。 TRUE：非工作日；FALSE：工作日。
	 * 默认值：TRUE.
	 */
	public static final String				IS_NON_WORK_DAY		= "ISNONWORKDAY";

	/**
	 * 重复发生方式
	 * 所有值：每天、每周、每月、每年
	 * 默认值：每天
	 */
	public static final String				REPEAT_MODE			= "REPEATMODE";

	/**
	 * 每几天,每几周，每几个月，每几年
	 * 默认值：1
	 */
	public static final String				GAP_DAY				= "GAPDAY";

	/**
	 * 每周几日
	 * 所有值：星期日、星期一、星期二、星期三、星期四、星期五、星期六
	 */
	public static final String				WEEKLY_DAYS			= "WEEKLYDAYS";

	/**
	 * 每月几日
	 * 所有值1-31
	 */
	public static final String				MONTHLY_DAYS		= "MONTHLYDAYS";

	/**
	 * 每年的几月
	 * 所有值 1-12
	 */
	public static final String				YEARLY_MONTHS		= "YEARLYMONTHS";

	/**
	 * 日历唯一标示符 PMCalendar.guid
	 */
	public static final String				CALENDAR_GUID		= "CALENDARGUID";

	// 日历中特殊日期的特殊时间段，只有选择是工作时间才有
	private List<PMCalendarSpecialDateTime>	specialDateTimeList	= null;

	public PMCalendarSpecialDate()
	{
		this.setIsNonWorkDay(true);
		this.setRepeatMode(RepeatModeEnum.EVERYDAY);
		this.setGapDay(0);
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return (String) super.get(ID);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		super.put(ID, id);
	}

	/**
	 * @return the beginDate
	 */
	public Date getBeginDate()
	{
		return (Date) super.get(BEGIN_DATE);
	}

	/**
	 * @param beginDate
	 *            the beginDate to set
	 */
	public void setBeginDate(Date beginDate)
	{
		super.put(BEGIN_DATE, beginDate);
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate()
	{
		return (Date) super.get(END_DATE);
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate)
	{
		super.put(END_DATE, endDate);
	}

	/**
	 * @return the isNonWorkDay
	 */
	public Boolean getIsNonWorkDay()
	{
		return BooleanUtils.getBooleanBy10((String) super.get(IS_NON_WORK_DAY));
	}

	/**
	 * @param isNonWorkDay
	 *            the isNonWorkDay to set
	 */
	public void setIsNonWorkDay(Boolean isNonWorkDay)
	{
		super.put(IS_NON_WORK_DAY, BooleanUtils.getBooleanString10(isNonWorkDay));
	}

	/**
	 * @return the repeatMode
	 */
	public RepeatModeEnum getRepeatMode()
	{
		BigDecimal repeatMode = super.get(REPEAT_MODE) == null ? null : new BigDecimal(super.get(REPEAT_MODE).toString());
		if (repeatMode == null)
		{
			return null;
		}
		return RepeatModeEnum.typeValueOf(repeatMode.intValue());
	}

	/**
	 * @param repeatMode
	 *            the repeatMode to set
	 */
	public void setRepeatMode(RepeatModeEnum repeatMode)
	{
		if (repeatMode == null)
		{
			return;
		}
		super.put(REPEAT_MODE, BigDecimal.valueOf(repeatMode.getMode()));
	}

	/**
	 * @return the gapDay
	 */
	public Integer getGapDay()
	{
		BigDecimal gapDay =  super.get(GAP_DAY) == null ? null : new BigDecimal(super.get(GAP_DAY).toString());
		if (gapDay == null)
		{
			return null;
		}
		return gapDay.intValue();
	}

	/**
	 * @param gapDay
	 *            the gapDay to set
	 */
	public void setGapDay(Integer gapDay)
	{
		if (gapDay == null)
		{
			return;
		}
		super.put(GAP_DAY, BigDecimal.valueOf(gapDay));
	}

	/**
	 * @return the weeklyDays
	 */
	public DayOfWeekEnum[] getWeeklyDays()
	{
		String days = (String) super.get(WEEKLY_DAYS);
		if (StringUtils.isNullString(days))
		{
			return null;
		}

		String[] dayArray = days.split(",");
		DayOfWeekEnum weeklyDays[] = new DayOfWeekEnum[dayArray.length];
		for (int i = 0; i < dayArray.length; i++)
		{
			weeklyDays[i] = DayOfWeekEnum.typeValueOf(Integer.valueOf(dayArray[i]));
		}
		return weeklyDays;
	}

	/**
	 * @param weeklyDays
	 *            the weeklyDays to set
	 */
	public void setWeeklyDays(DayOfWeekEnum[] weeklyDays)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < weeklyDays.length; i++)
		{
			if (builder.length() != 0)
			{
				builder.append(",");
			}
			builder.append(weeklyDays[i].getDay());
		}
		super.put(WEEKLY_DAYS, builder.toString());
	}

	/**
	 * @return the monthlyDays
	 */
	public DayOfMonthEnum getMonthlyDays()
	{
		String monthlyDays = (String) super.get(MONTHLY_DAYS);
		if (monthlyDays == null)
		{
			return null;
		}
		return DayOfMonthEnum.typeValueOf(Integer.valueOf(monthlyDays));
	}

	/**
	 * @param monthlyDays
	 *            the monthlyDays to set
	 */
	public void setMonthlyDays(DayOfMonthEnum monthlyDays)
	{
		if (monthlyDays == null)
		{
			return;
		}
		super.put(MONTHLY_DAYS, String.valueOf(monthlyDays.getDay()));
	}

	/**
	 * @return the monthlyDays
	 */
	public MonthOfYearEnum getYearlyMonths()
	{
		String yearlyMonths = (String) super.get(YEARLY_MONTHS);
		if (yearlyMonths == null)
		{
			return null;
		}
		return MonthOfYearEnum.typeValueOf(Integer.valueOf(yearlyMonths));
	}

	/**
	 * @param monthlyDays
	 *            the monthlyDays to set
	 */
	public void setYearlyMonths(MonthOfYearEnum yearlyMonths)
	{
		if (yearlyMonths == null)
		{
			return;
		}
		super.put(YEARLY_MONTHS, String.valueOf(yearlyMonths.getMonth()));
	}

	/**
	 * @return the calendarGuid
	 */
	public String getCalendarGuid()
	{
		return (String) super.get(CALENDAR_GUID);
	}

	/**
	 * @param calendarGuid
	 *            the calendarGuid to set
	 */
	public void setCalendarGuid(String calendarGuid)
	{
		super.put(CALENDAR_GUID, calendarGuid);
	}

	/**
	 * @return the specialDateTimeList
	 */
	public List<PMCalendarSpecialDateTime> getSpecialDateTimeList()
	{
		return this.specialDateTimeList;
	}

	/**
	 * @param specialDateTimeList
	 *            the specialDateTimeList to set
	 */
	public void setSpecialDateTimeList(List<PMCalendarSpecialDateTime> specialDateTimeList)
	{
		this.specialDateTimeList = specialDateTimeList;
	}

	/**
	 * @param calendar
	 * @param date
	 * @return
	 */
	public boolean isSpecialDay(Calendar calendar, Date date)
	{
		boolean returnValue = false;
		int gapDay = 1;
		calendar.setTime(this.getBeginDate());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.setBeginDate(calendar.getTime());
		calendar.setTime(this.getEndDate());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.setEndDate(calendar.getTime());
		if (date.getTime()-this.getBeginDate().getTime()< 0
				|| date.getTime() - this.getEndDate().getTime() - 24 * 3600 * 1000 >= 0)
		{
			returnValue = false;
		}
		else if (this.getRepeatMode() == null || this.getGapDay() == null)
		{
			returnValue = false;
		}
		else
		{
			gapDay = this.getGapDay() + 1;
			if (this.getRepeatMode() == RepeatModeEnum.EVERYDAY)
			{
				long diffDay = this.getDifferenceDay(calendar, date);
				if (diffDay % gapDay == 0)
				{
					returnValue = true;
				}
			}
			else if (this.getRepeatMode() == RepeatModeEnum.EVERYWEEK)
			{
				if (this.getWeeklyDays() != null)
				{
					long diffWeek = this.getDifferenceWeek(calendar, date);
					if (diffWeek % gapDay == 0)
					{
						calendar.setTime(date);
						int curDay = calendar.get(Calendar.DAY_OF_WEEK);
						DayOfWeekEnum xDayOfWeekEnum = DayOfWeekEnum.typeValueOf(curDay);
						for (DayOfWeekEnum yDayOfWeekEnum : this.getWeeklyDays())
						{
							if (yDayOfWeekEnum.equals(xDayOfWeekEnum))
							{
								returnValue = true;
								break;
							}
						}
					}
				}
			}
			else if (this.getRepeatMode() == RepeatModeEnum.EVERYMONTH)
			{
				if (this.getMonthlyDays() != null)
				{
					long diffMonth = this.getDifferenceMonth(calendar, date);
					if (diffMonth % gapDay == 0)
					{
						calendar.setTime(date);
						if (calendar.get(Calendar.DAY_OF_MONTH) == this.getMonthlyDays().getDay())
						{
							returnValue = true;
						}
					}
				}
			}
			else if (this.getRepeatMode() == RepeatModeEnum.EVERYYEAR)
			{
				if (this.getYearlyMonths() != null && this.getMonthlyDays() != null)
				{
					long diffWeek = this.getDifferenceWeek(calendar, date);
					if (diffWeek % gapDay == 0)
					{
						calendar.setTime(date);
						if (calendar.get(Calendar.MONTH) == this.getYearlyMonths().getMonth()
								&& calendar.get(Calendar.DAY_OF_MONTH) == this.getMonthlyDays().getDay())
						{
							returnValue = true;
						}
					}
				}
			}
		}
		return returnValue;
	}

	public long getDifferenceDay(Calendar calendar, Date date)
	{
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, 0);
		calendar.add(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		calendar.setTime(this.getBeginDate());
		calendar.add(Calendar.HOUR_OF_DAY, 0);
		calendar.add(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date rdate = calendar.getTime();
		return (date.getTime() - rdate.getTime()) / (24 * 3600 * 1000);
	}

	public long getDifferenceWeek(Calendar calendar, Date date)
	{
		calendar.setTime(this.getBeginDate());
		return (this.getDifferenceDay(calendar, date) + calendar.get(Calendar.DAY_OF_WEEK) - calendar
				.getFirstDayOfWeek()) / 7;
	}

	public long getDifferenceMonth(Calendar calendar, Date date)
	{
		calendar.setTime(date);
		int year1 = calendar.get(Calendar.YEAR);
		int month1 = calendar.get(Calendar.MONTH);
		calendar.setTime(this.getBeginDate());
		int year2 = calendar.get(Calendar.YEAR);
		int month2 = calendar.get(Calendar.MONTH);
		return (year1 - year2) * 12 + (month1 - month2);
	}

	public long getDifferenceYear(Calendar calendar, Date date)
	{
		calendar.setTime(date);
		int year1 = calendar.get(Calendar.YEAR);
		calendar.setTime(this.getBeginDate());
		int year2 = calendar.get(Calendar.YEAR);
		return year1 - year2;
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME,name);
	}
	
}
