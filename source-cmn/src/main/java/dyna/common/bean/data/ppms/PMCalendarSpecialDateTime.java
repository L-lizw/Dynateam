/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 日历中特殊日期的特殊时间段
 * Duanll 2012-5-7
 */
package dyna.common.bean.data.ppms;

import java.util.Calendar;
import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author Duanll
 *         日历中特殊日期的特殊时间段，只有选择是工作时间才有
 */
public class PMCalendarSpecialDateTime extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 5202866751611114386L;

	/**
	 * 实例对象自定义编号
	 */
	public static final String	ID					= "SPECIALID";
	public static final String	NAME				= "SPECIALNAME";
	/**
	 * 开始时间
	 * 默认时间：当天
	 */
	public static final String	BEGIN_TIME			= "BEGINTIME";

	/**
	 * 结束时间
	 * 默认时间：当天
	 */
	public static final String	END_TIME			= "ENDTIME";

	/**
	 * 日历中的特殊日期唯一标示符
	 */
	public static final String	SPECIAL_DATE_GUID	= "SPECIALDATEGUID";

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
	 * @return the specialDateGuid
	 */
	public String getSpecialDateGuid()
	{
		return (String) super.get(SPECIAL_DATE_GUID);
	}

	/**
	 * @param specialDateGuid
	 *            the specialDateGuid to set
	 */
	public void setSpecialDateGuid(String specialDateGuid)
	{
		super.put(SPECIAL_DATE_GUID, specialDateGuid);
	}

	public double getDailyWorkHours()
	{
		return this.getFinishTimeHour() - this.getStartTimeHour()
				+ (this.getFinishTimeMinute() - this.getStartTimeMinute()) / 60;
	}

	public Date getNextWorkTime(Calendar calendar, Date date)
	{
		calendar.setTime(date);
		if (this.getStartTimeHour() > calendar.get(Calendar.HOUR_OF_DAY))
		{
			calendar.set(Calendar.HOUR_OF_DAY, this.getStartTimeHour());
			calendar.set(Calendar.MINUTE, this.getStartTimeMinute());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		else if (this.getStartTimeHour() == calendar.get(Calendar.HOUR_OF_DAY))
		{
			if (this.getStartTimeMinute() > calendar.get(Calendar.MINUTE))
			{
				calendar.set(Calendar.MINUTE, this.getStartTimeMinute());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
		}

		if (this.getFinishTimeHour() < calendar.get(Calendar.HOUR_OF_DAY))
		{
			return null;
		}
		else if (this.getFinishTimeHour() == calendar.get(Calendar.HOUR_OF_DAY))
		{
			if (this.getFinishTimeMinute() <= calendar.get(Calendar.MINUTE))
			{
				return null;
			}
		}
		return calendar.getTime();
	}

	/**
	 * @param date
	 * @return
	 */
	public Date getPreviousWorkTime(Calendar calendar, Date date)
	{
		calendar.setTime(date);
		if (this.getStartTimeHour() > calendar.get(Calendar.HOUR_OF_DAY))
		{
			return null;
		}
		else if (this.getStartTimeHour() == calendar.get(Calendar.HOUR_OF_DAY))
		{
			if (this.getStartTimeMinute() >= calendar.get(Calendar.MINUTE))
			{
				return null;
			}
		}

		if (this.getFinishTimeHour() < calendar.get(Calendar.HOUR_OF_DAY))
		{
			calendar.set(Calendar.HOUR_OF_DAY, this.getFinishTimeHour());
			calendar.set(Calendar.MINUTE, this.getFinishTimeMinute());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		else if (this.getFinishTimeHour() == calendar.get(Calendar.HOUR_OF_DAY))
		{
			if (this.getFinishTimeMinute() <= calendar.get(Calendar.MINUTE))
			{
				calendar.set(Calendar.MINUTE, this.getFinishTimeMinute());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
		}
		return calendar.getTime();
	}

	/**
	 * @return
	 */
	public int getFinishTimeMinute()
	{
		String finishTime = this.getEndTime();
		return Integer.parseInt(finishTime.substring(finishTime.indexOf(':') + 1));
	}

	/**
	 * @return
	 */
	public int getFinishTimeHour()
	{
		String finishTime = this.getEndTime();
		return Integer.parseInt(finishTime.substring(0, finishTime.indexOf(':')));
	}

	/**
	 * @return
	 */
	public int getStartTimeMinute()
	{
		String startTime = this.getBeginTime();
		return Integer.parseInt(startTime.substring(startTime.indexOf(':') + 1));
	}

	/**
	 * @return
	 */
	public int getStartTimeHour()
	{
		String startTime = this.getBeginTime();
		return Integer.parseInt(startTime.substring(0, startTime.indexOf(':')));
	}

	/**
	 * @param calendar
	 * @param date
	 * @return
	 */
	public double getCurDayWorkTime(Calendar calendar, Date date)
	{
		calendar.setTime(date);

		if (this.getFinishTimeHour() < calendar.get(Calendar.HOUR_OF_DAY))
		{
			return this.getDailyWorkHours();
		}
		else if (this.getFinishTimeHour() == calendar.get(Calendar.HOUR_OF_DAY))
		{
			if (this.getFinishTimeMinute() <= calendar.get(Calendar.MINUTE))
			{
				return this.getDailyWorkHours();
			}
		}

		if (this.getStartTimeHour() > calendar.get(Calendar.HOUR_OF_DAY))
		{
			return 0;
		}
		else if (this.getStartTimeHour() == calendar.get(Calendar.HOUR_OF_DAY))
		{
			if (this.getStartTimeMinute() > calendar.get(Calendar.MINUTE))
			{
				return 0;
			}
		}
		double x = calendar.get(Calendar.HOUR_OF_DAY) - this.getStartTimeHour()
				+ ((calendar.get(Calendar.MINUTE) - this.getStartTimeMinute()) / 60d);

		return x;
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
