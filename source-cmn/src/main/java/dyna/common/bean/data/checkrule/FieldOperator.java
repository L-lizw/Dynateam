package dyna.common.bean.data.checkrule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.systemenum.OperateSignEnum;

//TODO 字段操作符映射
public class FieldOperator
{
	public static Map<Enum, List<OperateSignEnum>>	map;

	static
	{
		map = new HashMap<Enum, List<OperateSignEnum>>();
		List<OperateSignEnum> fieldOperatorList = new ArrayList<OperateSignEnum>();
	}

	public static List<OperateSignEnum> get(Enum customEnum)
	{
		return map.get(customEnum);
	}

}
