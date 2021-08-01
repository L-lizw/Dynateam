package dyna.common.bean.data.ppms.indicator;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.IndicatorAnalysisValMapper;

import java.math.BigDecimal;

/**
 * 指标分析结果
 * 
 * @author duanll
 * 
 */
@EntryMapper(IndicatorAnalysisValMapper.class)
public class IndicatorAnalysisVal extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1988314951497312610L;

	public static final String	ID					= "INDICATORID";

	public static final String	NAME				= "INDICATORNAME";

	public static final String	YEAR				= "INDICATORYEAR";

	public static final String	MONTH				= "INDICATORMONTH";

	public static final String	RESULT				= "RESVAL";

	// 分析维度1
	public static final String	DISMENSION1			= "DISMENSION1";

	// 分析维度2
	public static final String	DISMENSION2			= "DISMENSION2";

	// 分析维度3
	public static final String	DISMENSION3			= "DISMENSION3";

	// 分析维度4
	public static final String	DISMENSION4			= "DISMENSION4";

	// 分析维度5
	public static final String	DISMENSION5			= "DISMENSION5";

	// 分析维度6
	public static final String	DISMENSION6			= "DISMENSION6";

	// 分析维度7
	public static final String	DISMENSION7			= "DISMENSION7";

	// 分析维度8
	public static final String	DISMENSION8			= "DISMENSION8";

	// 分析维度9
	public static final String	DISMENSION9			= "DISMENSION9";

	// 分析维度10
	public static final String	DISMENSION10		= "DISMENSION10";

	public Integer getYear()
	{
		return ((Number) this.get(YEAR)).intValue();
	}

	public void setYear(Integer year)
	{
		this.put(YEAR, new BigDecimal(year));
	}

	public Integer getMonth()
	{
		return ((Number) this.get(MONTH)).intValue();
	}

	public void setMonth(Integer month)
	{
		this.put(MONTH, new BigDecimal(month));
	}

	public String getId()
	{
		return (String) this.get(ID);
	}

	public void setId(String id)
	{
		this.put(ID, id);
	}

	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public Double getResult()
	{
		Object result = this.get(RESULT);
		if (result == null)
		{
			return 0D;
		}
		return ((Number) result).doubleValue();
	}

	public void setResult(Double result)
	{
		if (result != null)
		{
			this.put(RESULT, BigDecimal.valueOf(result));
		}
		else
		{
			this.put(RESULT, BigDecimal.valueOf(0d));
		}
	}

	public String getDismension1()
	{
		return (String) this.get(DISMENSION1);
	}

	public void setDismension1(String dismension1)
	{
		this.put(DISMENSION1, dismension1);
	}

	public String getDismension2()
	{
		return (String) this.get(DISMENSION2);
	}

	public void setDismension2(String dismension2)
	{
		this.put(DISMENSION2, dismension2);
	}

	public String getDismension3()
	{
		return (String) this.get(DISMENSION3);
	}

	public void setDismension3(String dismension3)
	{
		this.put(DISMENSION3, dismension3);
	}

	public String getDismension4()
	{
		return (String) this.get(DISMENSION4);
	}

	public void setDismension4(String dismension4)
	{
		this.put(DISMENSION4, dismension4);
	}

	public String getDismension5()
	{
		return (String) this.get(DISMENSION5);
	}

	public void setDismension5(String dismension5)
	{
		this.put(DISMENSION5, dismension5);
	}

	public String getDismension6()
	{
		return (String) this.get(DISMENSION6);
	}

	public void setDismension6(String dismension6)
	{
		this.put(DISMENSION6, dismension6);
	}

	public String getDismension7()
	{
		return (String) this.get(DISMENSION7);
	}

	public void setDismension7(String dismension7)
	{
		this.put(DISMENSION7, dismension7);
	}

	public String getDismension8()
	{
		return (String) this.get(DISMENSION8);
	}

	public void setDismension8(String dismension8)
	{
		this.put(DISMENSION8, dismension8);
	}

	public String getDismension9()
	{
		return (String) this.get(DISMENSION9);
	}

	public void setDismension9(String dismension9)
	{
		this.put(DISMENSION9, dismension9);
	}

	public String getDismension10()
	{
		return (String) this.get(DISMENSION10);
	}

	public void setDismension10(String dismension10)
	{
		this.put(DISMENSION10, dismension10);
	}
}
