package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.configparamter.DynamicOfMultiVariableMapper;
import dyna.common.exception.ServiceRequestException;

import java.util.Map;

/**
 * 多变量列
 * 
 * @author wwx
 */
@EntryMapper(DynamicOfMultiVariableMapper.class)
public class DynamicOfMultiVariable extends ConfigBase implements SystemObject
{
	private static final long	serialVersionUID	= -8380474666256640100L;
	// 列顺序
	public static final String	COLSEQUENCE			= "COLSEQUENCE";
	// 变量名称
	public static final String	NAME				= "VARNAME";
	// 值
	public static final String	VALUE				= "VARVALUE";
	// 条件值
	public static final String	CONDITIONJSON		= "CONDITIONJSON";
	// 最大拆分数
	public static final Integer	MAX_SEGMENT			= 8;

	public String getColSequence()
	{
		return (String) this.get(COLSEQUENCE);
	}

	public void setColSequence(String colsequence)
	{
		this.put(COLSEQUENCE, colsequence);
	}

	public String getValue()
	{
		return (String) this.get(VALUE);
	}

	public void setValue(String value)
	{
		this.put(VALUE, value);
	}

	public String getConditionJson() throws ServiceRequestException
	{
		composeToOneField(this, CONDITIONJSON, MAX_SEGMENT);
		return (String) this.get(CONDITIONJSON);
	}

	public void setConditionJson(String conditionJson) throws ServiceRequestException
	{
		this.put(CONDITIONJSON, conditionJson);
		assignmentToOtherField(this, CONDITIONJSON, MAX_SEGMENT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		DynamicOfMultiVariable result = new DynamicOfMultiVariable();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}
}
