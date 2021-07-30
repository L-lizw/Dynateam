package dyna.common.bean.data.configparamter;

import java.math.BigDecimal;

import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;

public  class CompareConditionMatch implements ConditionMatch{
	private ConditionRelationEnum type=ConditionRelationEnum.EQUAL;
	
	public CompareConditionMatch(ConditionRelationEnum type) 
	{
		this.type = type;
	}

	public double compare(String value1, String value2)
	{
		if (StringUtils.isNullString(value1))
		{
			if (StringUtils.isNullString(value2))
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else if (StringUtils.isNullString(value2))
		{
			return 1;
		}
		else if (NumberUtils.isNumeric(value1) && NumberUtils.isNumeric(value2))
		{
			double doubleValue1 = new BigDecimal(value1).doubleValue();
			double doubleValue2 = new BigDecimal(value2).doubleValue();
			return (doubleValue1-doubleValue2);
		}
		else
		{
			return value1.compareTo(value2);
		}
	}

	@Override
	public boolean match(String value1, String value2) {
		if (type==ConditionRelationEnum.EQUAL)
		{
			return (this.compare(value1, value2)==0);
		}
		else if (type==ConditionRelationEnum.UNEQUAL)
		{
			return (this.compare(value1, value2)!=0);
		}
		else if (type==ConditionRelationEnum.LESSTHAN)
		{
			return (this.compare(value1, value2)<=0);
		}
		else if (type==ConditionRelationEnum.MORETHAN)
		{
			return (this.compare(value1, value2)>=0);
		}
		return false;
	}
	
	
}
