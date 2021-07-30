package dyna.common.bean.data.configparamter;

import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.systemenum.ConfigParameterTableType;

/**
 * 范围(A-E表和 R-Q表)
 * 
 * @author wwx
 * 
 */
public class TableOfRegion extends ConfigTableBase implements SystemObject
{
	private static final long	serialVersionUID	= 198901519599475733L;
	public static final String	TABLETYPE			= "TABLETYPE";
	public static final String	VARIABLE1			= "VARIABLE1";
	public static final String	LOWERLIMIT1			= "LOWERLIMIT1";
	public static final String	UPPERLIMIT1			= "UPPERLIMIT1";
	public static final String	VARIABLE2			= "VARIABLE2";
	public static final String	LOWERLIMIT2			= "LOWERLIMIT2";
	public static final String	UPPERLIMIT2			= "UPPERLIMIT2";

	public ConfigParameterTableType getTableType()
	{
		return ConfigParameterTableType.valueOf((String) this.get(TABLETYPE));
	}

	public void setTableType(ConfigParameterTableType tableTypeEnum)
	{
		this.put(TABLETYPE, tableTypeEnum == null ? null : tableTypeEnum.name());
	}

	public String getVariable1()
	{
		return (String) this.get(VARIABLE1);
	}

	public void setVariable1(String variable1)
	{
		this.put(VARIABLE1, variable1);
	}

	public String getLowerLimit1()
	{
		return (String) this.get(LOWERLIMIT1);
	}

	public void setLowerLimit1(String lowerLimit1)
	{
		this.put(LOWERLIMIT1, lowerLimit1);
	}

	public String getUpperLimit1()
	{
		return (String) this.get(UPPERLIMIT1);
	}

	public void setUpperLimit1(String lowerLimit1)
	{
		this.put(UPPERLIMIT1, lowerLimit1);
	}

	public String getVariable2()
	{
		return (String) this.get(VARIABLE2);
	}

	public void setVariable2(String variable2)
	{
		this.put(VARIABLE2, variable2);
	}

	public String getLowerLimit2()
	{
		return (String) this.get(LOWERLIMIT2);
	}

	public void setLowerLimit2(String lowerLimit2)
	{
		this.put(LOWERLIMIT2, lowerLimit2);
	}

	public String getUpperLimit2()
	{
		return (String) this.get(UPPERLIMIT2);
	}

	public void setUpperLimit2(String lowerLimit2)
	{
		this.put(UPPERLIMIT2, lowerLimit2);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfRegion result = new TableOfRegion();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}

	public String getVarValue(String varName) 
	{
		if (this.getColumns()!=null)
		{
			for (DynamicOfColumn column:this.getColumns())
			{
				if (varName.equalsIgnoreCase(column.getName()))
				{
					return column.getValue();
				}
			}
		}
		return null;
	}
}
