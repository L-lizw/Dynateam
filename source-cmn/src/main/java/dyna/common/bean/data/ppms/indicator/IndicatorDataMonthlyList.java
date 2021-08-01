package dyna.common.bean.data.ppms.indicator;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.IndicatorDataMonthlyListMapper;

/**
 * 按月收集数据
 * 
 * @author Administrator
 * 
 */
@EntryMapper(IndicatorDataMonthlyListMapper.class)
public class IndicatorDataMonthlyList extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 174195789294471205L;

	public static final String	YEAR				= "INDICATORYEAR";

	public static final String	MONTH				= "INDICATORMONTH";

	// 产品类别
	public static final String	DATASOURCENAME		= "DATASOURCENAME";

	public static final String	INSTANCECLASSGUID	= "INSTANCECLASSGUID";

	public static final String	INSTANCEGUID		= "INSTANCEGUID";

	// 数据类型 INSTANCE:实例; PROCRT:流程; ATTACH:流程附件
	public static final String	DATATYPE			= "INSTANCETYPE";

	public String getYear()
	{
		return (String) this.get(YEAR);
	}

	public void setYear(String year)
	{
		this.put(YEAR, year);
	}

	public String getMonth()
	{
		return (String) this.get(MONTH);
	}

	public void setMonth(String month)
	{
		this.put(MONTH, month);
	}

	public String getDataSourceName()
	{
		return (String) this.get(DATASOURCENAME);
	}

	public void setDataSourceName(String dataSourceName)
	{
		this.put(DATASOURCENAME, dataSourceName);
	}

	public String getInstanceClassGuid()
	{
		return (String) this.get(INSTANCECLASSGUID);
	}

	public void setInstanceClassGuid(String instanceClassGuid)
	{
		this.put(INSTANCECLASSGUID, instanceClassGuid);
	}

	public String getInstanceGuid()
	{
		return (String) this.get(INSTANCEGUID);
	}

	public void setInstanceGuid(String instanceGuid)
	{
		this.put(INSTANCEGUID, instanceGuid);
	}

	public String getDataType()
	{
		return (String) this.get(DATATYPE);
	}

	public void setDataType(String dataType)
	{
		this.put(DATATYPE, dataType);
	}
}
