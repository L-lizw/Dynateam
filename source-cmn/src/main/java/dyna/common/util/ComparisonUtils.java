/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Comparison
 * Wanglei 2011-11-16
 */
package dyna.common.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

/**
 * 对象比较工具类
 * 
 * @author Wanglei
 * 
 */
public class ComparisonUtils
{

	private static final Comparator<Object> COMPARATOR = new testValueComparator();

	/**
	 * 比较对象是否相等, 此处对BigDecimal, Float, Double类型的数据进行了特殊处理
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean assertEqual(Object o1, Object o2)
	{
		return assertEqual(o1, o2, COMPARATOR);
	}

	/**
	 * 比较对象是否相等, 如为指定comparator, 则效果与Object.equals相同
	 * 
	 * @param o1
	 * @param o2
	 * @param comparator
	 * @return
	 */
	public static <T extends Object> boolean assertEqual(T o1, T o2, Comparator<T> comparator)
	{
		if (comparator != null)
		{
			return comparator.compare(o1, o2) == 0 ? true : false;
		}

		if (o1 == null && o2 == null)
		{
			return true;
		}
		else if ((o1 == null && o2 != null) || (o1 != null && o2 == null))
		{
			return false;
		}
		else
		{
			return o1.equals(o2);
		}
	}

}

class testValueComparator implements Comparator<Object>
{

	private static final double	EXP		= 10E-10;
	private static final double	NEXP	= -1 * (10E-10);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
		{
			return 0;
		}
		else if (o1 == null && o2 != null)
		{
			return -1;
		}
		else if (o1 != null && o2 == null)
		{
			return 1;
		}
		// qiuxq Date Type Field Get back Timestamp
		if (o1 instanceof java.sql.Timestamp)
		{
			o1 = new Date(((java.sql.Timestamp) o1).getTime());
		}

		if (o2 instanceof java.sql.Timestamp)
		{
			o2 = new Date(((java.sql.Timestamp) o2).getTime());
		}

		Object t1 = o1;
		Object t2 = o2;
		if (o1 instanceof Float)
		{
			t1 = BigDecimal.valueOf((Float) o1);
		}
		else if (o1 instanceof Double)
		{
			t1 = BigDecimal.valueOf((Double) o1);
		}
		if (o2 instanceof Float)
		{
			t2 = BigDecimal.valueOf((Float) o2);
		}
		else if (o2 instanceof Double)
		{
			t2 = BigDecimal.valueOf((Double) o2);
		}

		if (t1 instanceof BigDecimal)
		{
			double f1 = ((Number) t1).doubleValue();
			double f2 = 0;
			if (o2 instanceof String)
			{
				f2 = Double.parseDouble(t2.toString());
			}
			else
			{
				f2 = ((Number) t2).doubleValue();
			}
			double f3 = f1 - f2;
			double abs = Math.abs(f3);
			return ((abs > NEXP) && (abs < EXP)) ? 0 : (f3 > 0 ? 1 : -1);
		}
		if (!o1.getClass().equals(o2.getClass()))
		{
			return 1;
		}

		return o1.equals(o2) ? 0 : 1;
	}
}
