/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Queue 队列
 * zhanghw 2012-04-24
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.SaLicenseOccupiedMapper;
import dyna.common.util.DateFormat;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Queue 队列
 * 
 * @author caogc
 * 
 */
@EntryMapper(SaLicenseOccupiedMapper.class)
public class SaLicenseOccupied extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -9137180160333458537L;

	public static String		GUID				= "GUID";
	// 类型
	public static String		TYPE				= "SESSIONTYPE";
	// 时间：格式YYYYMMDD
	public static String		TIME				= "LOGTIME";
	// 当前小时
	public static String		CURRENT_HOUR		= "CURRENTHOUR";

	public static String		H1					= "H1";
	public static String		H2					= "H2";
	public static String		H3					= "H3";
	public static String		H4					= "H4";
	public static String		H5					= "H5";
	public static String		H6					= "H6";
	public static String		H7					= "H7";
	public static String		H8					= "H8";
	public static String		H9					= "H9";
	public static String		H10					= "H10";
	public static String		H11					= "H11";
	public static String		H12					= "H12";
	public static String		H13					= "H13";
	public static String		H14					= "H14";
	public static String		H15					= "H15";
	public static String		H16					= "H16";
	public static String		H17					= "H17";
	public static String		H18					= "H18";
	public static String		H19					= "H19";
	public static String		H20					= "H20";
	public static String		H21					= "H21";
	public static String		H22					= "H22";
	public static String		H23					= "H23";
	public static String		H24					= "H24";

	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	public String getType()
	{
		return (String) this.get(TYPE);
	}

	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	public String getTime()
	{
		return (String) this.get(TIME);
	}

	public void setTime(String time)
	{
		this.put(TIME, time);
	}

	public int getCurrentHour()
	{
		Calendar c = Calendar.getInstance();
		c.setTime(DateFormat.getSysDate());
		return this.get(TIME) == null ? c.get(Calendar.HOUR_OF_DAY) : ((Number) this.get(TIME)).intValue();
	}

	public void setCurrentHour(int currentHour)
	{
		this.put(TIME, new BigDecimal(String.valueOf(currentHour)));
	}

	public int getH1()
	{
		return this.get(H1) == null ? 0 : ((Number) this.get(H1)).intValue();
	}

	public void setH1(int h1)
	{
		this.put(H1, new BigDecimal(String.valueOf(h1)));
	}

	public int getH2()
	{
		return this.get(H2) == null ? 0 : ((Number) this.get(H2)).intValue();
	}

	public void setH2(int h2)
	{
		this.put(H2, new BigDecimal(String.valueOf(h2)));
	}

	public int getH3()
	{
		return this.get(H3) == null ? 0 : ((Number) this.get(H3)).intValue();
	}

	public void setH3(int h3)
	{
		this.put(H3, new BigDecimal(String.valueOf(h3)));
	}

	public int getH4()
	{
		return this.get(H4) == null ? 0 : ((Number) this.get(H4)).intValue();
	}

	public void setH4(int h4)
	{
		this.put(H4, new BigDecimal(String.valueOf(h4)));
	}

	public int getH5()
	{
		return this.get(H5) == null ? 0 : ((Number) this.get(H5)).intValue();
	}

	public void setH5(int h5)
	{
		this.put(H5, new BigDecimal(String.valueOf(h5)));
	}

	public int getH6()
	{
		return this.get(H6) == null ? 0 : ((Number) this.get(H6)).intValue();
	}

	public void setH6(int h6)
	{
		this.put(H6, new BigDecimal(String.valueOf(h6)));
	}

	public int getH7()
	{
		return this.get(H7) == null ? 0 : ((Number) this.get(H7)).intValue();
	}

	public void setH7(int h7)
	{
		this.put(H7, new BigDecimal(String.valueOf(h7)));
	}

	public int getH8()
	{
		return this.get(H8) == null ? 0 : ((Number) this.get(H8)).intValue();
	}

	public void setH8(int h8)
	{
		this.put(H8, new BigDecimal(String.valueOf(h8)));
	}

	public int getH9()
	{
		return this.get(H9) == null ? 0 : ((Number) this.get(H9)).intValue();
	}

	public void setH9(int h9)
	{
		this.put(H9, new BigDecimal(String.valueOf(h9)));
	}

	public int getH10()
	{
		return this.get(H10) == null ? 0 : ((Number) this.get(H10)).intValue();
	}

	public void setH10(int h10)
	{
		this.put(H10, new BigDecimal(String.valueOf(h10)));
	}

	public int getH11()
	{
		return this.get(H11) == null ? 0 : ((Number) this.get(H11)).intValue();
	}

	public void setH11(int h11)
	{
		this.put(H11, new BigDecimal(String.valueOf(h11)));
	}

	public int getH12()
	{
		return this.get(H12) == null ? 0 : ((Number) this.get(H12)).intValue();
	}

	public void setH12(int h12)
	{
		this.put(H12, new BigDecimal(String.valueOf(h12)));
	}

	public int getH13()
	{
		return this.get(H13) == null ? 0 : ((Number) this.get(H13)).intValue();
	}

	public void setH13(int h13)
	{
		this.put(H13, new BigDecimal(String.valueOf(h13)));
	}

	public int getH14()
	{
		return this.get(H14) == null ? 0 : ((Number) this.get(H14)).intValue();
	}

	public void setH14(int h14)
	{
		this.put(H14, new BigDecimal(String.valueOf(h14)));
	}

	public int getH15()
	{
		return this.get(H15) == null ? 0 : ((Number) this.get(H15)).intValue();
	}

	public void setH15(int h15)
	{
		this.put(H15, new BigDecimal(String.valueOf(h15)));
	}

	public int getH16()
	{
		return this.get(H16) == null ? 0 : ((Number) this.get(H16)).intValue();
	}

	public void setH16(int h16)
	{
		this.put(H16, new BigDecimal(String.valueOf(h16)));
	}

	public int getH17()
	{
		return this.get(H17) == null ? 0 : ((Number) this.get(H17)).intValue();
	}

	public void setH17(int h17)
	{
		this.put(H17, new BigDecimal(String.valueOf(h17)));
	}

	public int getH18()
	{
		return this.get(H18) == null ? 0 : ((Number) this.get(H18)).intValue();
	}

	public void setH18(int h18)
	{
		this.put(H18, new BigDecimal(String.valueOf(h18)));
	}

	public int getH19()
	{
		return this.get(H19) == null ? 0 : ((Number) this.get(H19)).intValue();
	}

	public void setH19(int h19)
	{
		this.put(H19, new BigDecimal(String.valueOf(h19)));
	}

	public int getH20()
	{
		return this.get(H20) == null ? 0 : ((Number) this.get(H20)).intValue();
	}

	public void setH20(int h20)
	{
		this.put(H20, new BigDecimal(String.valueOf(h20)));
	}

	public int getH21()
	{
		return this.get(H21) == null ? 0 : ((Number) this.get(H21)).intValue();
	}

	public void setH21(int h21)
	{
		this.put(H21, new BigDecimal(String.valueOf(h21)));
	}

	public int getH22()
	{
		return this.get(H22) == null ? 0 : ((Number) this.get(H22)).intValue();
	}

	public void setH22(int h22)
	{
		this.put(H22, new BigDecimal(String.valueOf(h22)));
	}

	public int getH23()
	{
		return this.get(H23) == null ? 0 : ((Number) this.get(H23)).intValue();
	}

	public void setH23(int h23)
	{
		this.put(H23, new BigDecimal(String.valueOf(h23)));
	}

	public int getH24()
	{
		return this.get(H24) == null ? 0 : ((Number) this.get(H24)).intValue();
	}

	public void setH24(int h24)
	{
		this.put(H24, new BigDecimal(String.valueOf(h24)));
	}

	public void setValue(int hours, int count)
	{
		String fieldName = "H" + String.valueOf(hours + 1);
		if (this.get(fieldName) == null)
		{
			this.put(fieldName, count);
		}
		else if (count > ((Number) this.get(fieldName)).intValue())
		{
			this.put(fieldName, count);
		}
	}
}
