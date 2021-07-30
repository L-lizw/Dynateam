/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理 日历
 * Duanll 2012-5-7
 */
package dyna.common.bean.data.ppms;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ppms.DayOfWeekEnum;
import dyna.common.systemenum.ppms.MonthOfYearEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.PMConstans;
import dyna.common.util.StringUtils;

/**
 * @author Duanll
 *         项目管理 日历
 */
public class PMCalendar extends SystemObjectImpl implements SystemObject
{
	private static final long			serialVersionUID	= -6611794139250699053L;

	/**
	 * 实例对象自定义编号
	 */
	public static final String			ID					= "CALENDARID";

	public static final String			NAME				= "CALENDARNAME";

	/**
	 * 每周开始
	 * 所有值：星期日、星期一、星期二、星期三、星期四、星期五、星期六
	 * 默认值：星期日
	 */
	public static final String			BOFWEEK				= "BOFWEEK";

	/**
	 * 默认开始时间
	 * 默认值：8:00:00
	 */
	public static final String			BEGIN_TIME			= "BEGINTIME";

	/**
	 * 默认结束时间
	 * 默认值：17:00:00
	 */
	public static final String			END_TIME			= "ENDTIME";

	/**
	 * 每日工时
	 * 默认值：8.00
	 */
	public static final String			DAILY_WORK_HOURS	= "DAILYWORKHOURS";

	/**
	 * 每周工时
	 * 默认值：40.00
	 */
	public static final String			WEEKLY_WORK_HOURS	= "WEEKLYWORKHOURS";

	/**
	 * 每月工作日
	 * 默认值：20
	 */
	public static final String			MONTHLY_WORK_DAYS	= "MONTHLYWORKDAYS";

	/**
	 * 财政年度以开始年度编号
	 * 默认值：false:非选中。 true：选中。
	 */
	public static final String			CODE_BY_START_YEAR	= "CODEBYSTARTYEAR";

	/**
	 * 财年开始月
	 * 所有值：1、2、3、4、5、6、7、8、9、10、11、12
	 */
	public static final String			FISCAL_YEAR			= "FISCALYEAR";

	/**
	 * 默认非工作日
	 * 所有值：星期日、星期一、星期二、星期三、星期四、星期五、星期六
	 * 默认值：星期日、星期六
	 */
	public static final String			NON_WORK_DAYS		= "NONWORKDAYS";

	/**
	 * 废弃状态。 true:有效。 false:废弃。
	 */
	public static final String			IS_VALID			= "ISVALID";

	// 日历中的特殊日期
	private List<PMCalendarSpecialDate>	specialDateList		= null;

	private Calendar					curCalendar			= null;

	/**
	 * 对应项目的ObejctGuid
	 */
	public static final String			PROJECT_OBJECTGUID	= "PROJECT";

	public PMCalendar()
	{
		this.setBofweek(DayOfWeekEnum.SUNDAY);
		this.setFiscalYear(MonthOfYearEnum.JANUARY);
		this.setCodeByStartYear(false);
		this.setMonthlyWorkDays(20);
		this.setDailyWorkHours(8.00F);
		this.setWeeklyWorkHours(40.00F);
		this.setNonWorkDays(new DayOfWeekEnum[] { DayOfWeekEnum.SUNDAY, DayOfWeekEnum.SATURDAY });

		this.setBeginTime("08:00");
		this.setEndTime("17:00");
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
	 * @return the name
	 */
	public String getName()
	{
		return (String) super.get(NAME);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		super.put(NAME, name);
	}

	/**
	 * @return the bofweek
	 */
	public DayOfWeekEnum getBofweek()
	{
		BigDecimal bofweek = super.get(BOFWEEK) == null ? null:new BigDecimal(super.get(BOFWEEK).toString()) ;
		if (bofweek == null)
		{
			return null;
		}
		return DayOfWeekEnum.typeValueOf(bofweek.intValue());
	}

	/**
	 * @param bofweek
	 *            the bofweek to set
	 */
	public void setBofweek(DayOfWeekEnum bofweek)
	{
		if (bofweek == null)
		{
			return;
		}
		super.put(BOFWEEK, BigDecimal.valueOf(bofweek.getDay()));
	}

	/**
	 * @return the beginTime
	 */
	public String getBeginTime()
	{
		return (String) super.get(BEGIN_TIME);
	}

	/**
	 * @param beginTime
	 *            the beginTime to set
	 */
	public void setBeginTime(String beginTime)
	{
		super.put(BEGIN_TIME, beginTime);
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime()
	{
		return (String) super.get(END_TIME);
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime)
	{
		super.put(END_TIME, endTime);
	}

	/**
	 * @return the dailyWorkHours
	 */
	public Double getDailyWorkHours()
	{
		BigDecimal dailyWorkHours = super.get(DAILY_WORK_HOURS) == null ? null:new BigDecimal(super.get(DAILY_WORK_HOURS).toString());
		if (dailyWorkHours == null)
		{
			return 8.00d;
		}
		return dailyWorkHours.doubleValue();
	}

	/**
	 * @param dailyWorkHours
	 *            the dailyWorkHours to set
	 */
	public void setDailyWorkHours(Float dailyWorkHours)
	{
		if (dailyWorkHours == null)
		{
			return;
		}
		super.put(DAILY_WORK_HOURS, BigDecimal.valueOf(dailyWorkHours));
	}

	/**
	 * @return the weeklyWorkHours
	 */
	public Double getWeeklyWorkHours()
	{
		BigDecimal weeklyWorkHours = super.get(WEEKLY_WORK_HOURS) == null ? null:new BigDecimal(super.get(WEEKLY_WORK_HOURS).toString());
		if (weeklyWorkHours == null)
		{
			return null;
		}
		return weeklyWorkHours.doubleValue();
	}

	/**
	 * @param weeklyWorkHours
	 *            the weeklyWorkHours to set
	 */
	public void setWeeklyWorkHours(Float weeklyWorkHours)
	{
		if (weeklyWorkHours == null)
		{
			return;
		}
		super.put(WEEKLY_WORK_HOURS, BigDecimal.valueOf(weeklyWorkHours));
	}

	/**
	 * @return the monthlyWorkDays
	 */
	public Integer getMonthlyWorkDays()
	{
		BigDecimal monthlyWorkDays = super.get(MONTHLY_WORK_DAYS) == null ? null:new BigDecimal(super.get(MONTHLY_WORK_DAYS).toString());;
		if (monthlyWorkDays == null)
		{
			return null;
		}
		return monthlyWorkDays.intValue();
	}

	/**
	 * @param monthlyWorkDays
	 *            the monthlyWorkDays to set
	 */
	public void setMonthlyWorkDays(Integer monthlyWorkDays)
	{
		if (monthlyWorkDays == null)
		{
			return;
		}
		super.put(MONTHLY_WORK_DAYS, BigDecimal.valueOf(monthlyWorkDays));
	}

	/**
	 * @param codeByStartYear
	 *            the codeByStartYear to set
	 */
	public void setCodeByStartYear(Boolean codeByStartYear)
	{
		super.put(CODE_BY_START_YEAR, BooleanUtils.getBooleanString10(codeByStartYear));
	}

	/**
	 * @return the codeByStartYear
	 */
	public Boolean getCodeByStartYear()
	{
		return BooleanUtils.getBooleanBy10((String) super.get(CODE_BY_START_YEAR));
	}

	/**
	 * @return the fiscalYear
	 */
	public MonthOfYearEnum getFiscalYear()
	{
		BigDecimal fiscalYear = super.get(FISCAL_YEAR) == null ? null:new BigDecimal(super.get(FISCAL_YEAR).toString());
		if (fiscalYear == null)
		{
			return null;
		}
		return MonthOfYearEnum.typeValueOf(fiscalYear.intValue());
	}

	/**
	 * @param fiscalYear
	 *            the fiscalYear to set
	 */
	public void setFiscalYear(MonthOfYearEnum fiscalYear)
	{
		if (fiscalYear == null)
		{
			return;
		}
		super.put(FISCAL_YEAR, BigDecimal.valueOf(fiscalYear.getMonth()));
	}

	/**
	 * @return the nonWorkDays
	 */
	public DayOfWeekEnum[] getNonWorkDays()
	{
		String days = (String) super.get(NON_WORK_DAYS);
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
	 * @param nonWorkDays
	 *            the nonWorkDays to set
	 */
	public void setNonWorkDays(DayOfWeekEnum nonWorkDays[])
	{
		if (nonWorkDays == null)
		{
			return;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < nonWorkDays.length; i++)
		{
			if (builder.length() != 0)
			{
				builder.append(",");
			}
			builder.append(nonWorkDays[i].getDay());
		}
		super.put(NON_WORK_DAYS, builder.toString());
	}

	/**
	 * @return the specialDateList
	 */
	public List<PMCalendarSpecialDate> getSpecialDateList()
	{
		return this.specialDateList;
	}

	/**
	 * @param specialDateList
	 *            the specialDateList to set
	 */
	public void setSpecialDateList(List<PMCalendarSpecialDate> specialDateList)
	{
		this.specialDateList = specialDateList;
	}

	public Date addDay(Date date, double day)
	{
		double i = 0;
		while (i < day)
		{
			date = this.getNextWorkingDate(date);
			this.getCalendar().setTime(date);
			this.getCalendar().add(Calendar.DATE, 1);
			date = this.getCalendar().getTime();
			i++;
		}
		date = this.getNextWorkingDate(date);
		return date;
	}

	public Date getNextWorkingDate(Date date)
	{
		Date rdate = null;
		while (true)
		{
			boolean iswork = !this.isRest(date);
			PMCalendarSpecialDate xPMCalendarSpecialDate = this.getDetail(date);
			if (xPMCalendarSpecialDate == null)
			{
				if (iswork)
				{
					rdate = date;
				}
			}
			else
			{
				if (xPMCalendarSpecialDate.getIsNonWorkDay() == false)
				{
					rdate = date;
				}
			}

			if (rdate == null)
			{
				this.getCalendar().setTime(date);
				this.getCalendar().add(Calendar.DATE, 1);
				this.getCalendar().set(Calendar.HOUR_OF_DAY, 0);
				this.getCalendar().set(Calendar.MINUTE, 0);
				this.getCalendar().set(Calendar.SECOND, 0);
				this.getCalendar().set(Calendar.MILLISECOND, 0);
				date = this.getCalendar().getTime();
			}
			else
			{
				this.getCalendar().setTime(date);
				this.getCalendar().set(Calendar.HOUR_OF_DAY, 0);
				this.getCalendar().set(Calendar.MINUTE, 0);
				this.getCalendar().set(Calendar.SECOND, 0);
				this.getCalendar().set(Calendar.MILLISECOND, 0);
				date = this.getCalendar().getTime();
				break;
			}
		}
		return rdate;
	}

	public Date decDay(Date date, double day)
	{
		double i = 0;
		while (i < day)
		{
			date = this.getPreviousWorkingDate(date);
			this.getCalendar().setTime(date);
			this.getCalendar().add(Calendar.DATE, -1);
			date = this.getCalendar().getTime();
			i++;
		}
		date = this.getPreviousWorkingDate(date);
		return date;
	}

	/**
	 * @param date
	 * @return
	 */
	public Date getPreviousWorkingDate(Date date)
	{
		Date rdate = null;
		while (true)
		{
			boolean iswork = !this.isRest(date);
			PMCalendarSpecialDate xPMCalendarSpecialDate = this.getDetail(date);
			if (xPMCalendarSpecialDate == null)
			{
				if (iswork)
				{
					rdate = date;
				}
			}
			else
			{
				if (xPMCalendarSpecialDate.getIsNonWorkDay() == false)
				{
					rdate = date;
				}
			}

			if (rdate == null)
			{
				this.getCalendar().setTime(date);
				this.getCalendar().add(Calendar.DATE, -1);
				this.getCalendar().set(Calendar.HOUR_OF_DAY, 0);
				this.getCalendar().set(Calendar.MINUTE, 0);
				this.getCalendar().set(Calendar.SECOND, 0);

				// this.getCalendar().set(Calendar.HOUR_OF_DAY, 23);
				// this.getCalendar().set(Calendar.MINUTE, 59);
				// this.getCalendar().set(Calendar.SECOND, 59);
				this.getCalendar().set(Calendar.MILLISECOND, 0);
				date = this.getCalendar().getTime();
			}
			else
			{
				this.getCalendar().setTime(date);
				this.getCalendar().set(Calendar.HOUR_OF_DAY, 0);
				this.getCalendar().set(Calendar.MINUTE, 0);
				this.getCalendar().set(Calendar.SECOND, 0);
				this.getCalendar().set(Calendar.MILLISECOND, 0);
				date = this.getCalendar().getTime();
				break;
			}
		}
		return rdate;
	}

	public Calendar getCalendar()
	{
		if (this.curCalendar == null)
		{
			this.curCalendar = Calendar.getInstance();
			if (this.getBofweek() != null)
			{
				this.curCalendar.setFirstDayOfWeek(this.getBofweek().getDay());
			}
		}
		return this.curCalendar;
	}

	public boolean isRest(Date date)
	{
		boolean returnValue = false;
		Calendar calendar = this.getCalendar();
		calendar.setTime(date);
		int curDay = calendar.get(Calendar.DAY_OF_WEEK);
		DayOfWeekEnum xDayOfWeekEnum = DayOfWeekEnum.typeValueOf(curDay);
		if (this.getNonWorkDays() != null)
		{
			for (DayOfWeekEnum yDayOfWeekEnum : this.getNonWorkDays())
			{
				if (yDayOfWeekEnum.equals(xDayOfWeekEnum))
				{
					returnValue = true;
					break;
				}
			}
		}
		return returnValue;
	}
	
	public boolean isRestWithSpecial(Date date)
	{
		boolean returnValue = this.isRest(date);
		PMCalendarSpecialDate xPMCalendarSpecialDate = this.getDetail(date);
		if (xPMCalendarSpecialDate != null)
		{
			returnValue=xPMCalendarSpecialDate.getIsNonWorkDay();
		}
		return returnValue;
	}

	public PMCalendarSpecialDate getDetail(Date date)
	{
		if (this.specialDateList != null)
		{
			for (PMCalendarSpecialDate xPMCalendarSpecialDate : this.specialDateList)
			{
				if (xPMCalendarSpecialDate.isSpecialDay(this.getCalendar(), date))
				{
					return xPMCalendarSpecialDate;
				}
			}
		}
		return null;
	}

	public void setValid(boolean isValid)
	{
		this.put(IS_VALID, BooleanUtils.getBooleanString10(isValid));
	}

	@Override
	public boolean isValid()
	{
		if (this.get(IS_VALID) == null)
		{
			return true;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(IS_VALID));
	}

	private int getDurationDayInternal(Date preparePlanStartTime, Date preparePlanFinishTime)
	{
		int value = 0;
		this.getCalendar().setTime(preparePlanFinishTime);
		int year = this.getCalendar().get(Calendar.YEAR);
		int month = this.getCalendar().get(Calendar.MONTH) + 1;
		int day = this.getCalendar().get(Calendar.DATE);
		this.getCalendar().setTime(preparePlanStartTime);
		this.getCalendar().set(Calendar.HOUR_OF_DAY, 0);
		this.getCalendar().set(Calendar.MINUTE, 0);
		this.getCalendar().set(Calendar.SECOND, 0);
		this.getCalendar().set(Calendar.MILLISECOND, 0);
		while (true)
		{
			preparePlanStartTime = this.getCalendar().getTime();
			if (year == this.getCalendar().get(Calendar.YEAR) && month == this.getCalendar().get(Calendar.MONTH) + 1
					&& day == this.getCalendar().get(Calendar.DATE))
			{

				break;
			}
			else
			{
				boolean iswork = !this.isRest(preparePlanStartTime);
				PMCalendarSpecialDate xPMCalendarSpecialDate = this.getDetail(preparePlanStartTime);
				if (xPMCalendarSpecialDate == null)
				{
					if (iswork)
					{
						value = value + 1;
					}
				}
				else
				{
					if (xPMCalendarSpecialDate.getIsNonWorkDay() == false)
					{
						value = value + 1;
					}
				}
			}
			this.getCalendar().setTime(preparePlanStartTime);
			this.getCalendar().add(Calendar.DATE, 1);
			this.getCalendar().set(Calendar.HOUR_OF_DAY, 0);
			this.getCalendar().set(Calendar.MINUTE, 0);
			this.getCalendar().set(Calendar.SECOND, 0);
			this.getCalendar().set(Calendar.MILLISECOND, 0);
		}
		return value;
	}

	/**
	 * @param preparePlanStartTime
	 * @param preparePlanFinishTime
	 * @return
	 */
	public int getDurationDay(Date preparePlanStartTime, Date preparePlanFinishTime)
	{
		if (preparePlanStartTime.getTime()-preparePlanFinishTime.getTime() <= 0)
		{
			return this.getDurationDayInternal(preparePlanStartTime, preparePlanFinishTime);
		}
		else
		{
			return 0 - this.getDurationDayInternal(preparePlanFinishTime, preparePlanStartTime);
		}
	}

	/**
	 * @return the grantedrole
	 */
	public ObjectGuid getProject()
	{
		return new ObjectGuid((String) this.get(PROJECT_OBJECTGUID + PMConstans.CLASS), null,
				(String) this.get(PROJECT_OBJECTGUID), (String) this.get(PROJECT_OBJECTGUID + PMConstans.MASTER), null);
	}

	/**
	 * @param grantedrole
	 *            the grantedrole to set
	 */
	public void setProject(ObjectGuid projectObjectGuid)
	{
		this.put(PROJECT_OBJECTGUID + PMConstans.MASTER, projectObjectGuid.getMasterGuid());
		this.put(PROJECT_OBJECTGUID + PMConstans.CLASS, projectObjectGuid.getClassGuid());
		this.put(PROJECT_OBJECTGUID, projectObjectGuid.getGuid());
	}
}
