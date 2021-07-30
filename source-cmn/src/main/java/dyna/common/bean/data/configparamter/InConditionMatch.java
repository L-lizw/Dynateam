package dyna.common.bean.data.configparamter;

import java.util.Arrays;
import java.util.List;

import dyna.common.util.StringUtils;

public class InConditionMatch implements ConditionMatch{

	@Override
	public boolean match(String value1, String value2) 
	{
		
		if (StringUtils.isNullString(value1)||StringUtils.isNullString(value2))
		{
			return false;
		}
		else //if (("^" + value2 + "^").contains("^" + value1 + "^"))
		{
			List<String> list=Arrays.asList(value2.split("\\^"));
			if (list.contains(value1))
			{
				return true;
			}
		}
		return false;
	}

}
