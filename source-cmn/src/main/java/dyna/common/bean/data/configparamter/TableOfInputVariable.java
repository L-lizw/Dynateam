package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.configparamter.TableOfInputVariableMapper;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 输入变量表
 *
 * @author wwx
 */
@EntryMapper(TableOfInputVariableMapper.class)
public class TableOfInputVariable extends ConfigTableBase implements SystemObject
{
	private static final long                         serialVersionUID = 5474372883389913067L;
	public static final  String                       DESCRIPTION      = "DESCRIPTION";
	public static final  String                       VALUETYPE        = "VALUETYPE";
	public static final  String                       RANGE            = "VALUERANGE";        // 拼接值
	public static final  String                       SN               = "SN";
	public static final  String                       NAME             = "VARNAME";
	public               List<TableOfDefineCondition> listValue        = null;
	public static final  String                       VALUEDESCRIPTION = "valuedescription";    // 拼接描述

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

	public String getSN()
	{
		return (String) this.get(SN);
	}

	public void setSN(String sn)
	{
		this.put(SN, sn);
	}

	public String getValueDescription()
	{
		return (String) this.get(VALUEDESCRIPTION);
	}

	public void setValueDescription(String valuedescription)
	{
		this.put(VALUEDESCRIPTION, valuedescription);
	}

	public InputVariableValueSelectEnum getValueType()
	{
		String valueType = (String) this.get(VALUETYPE);
		return valueType == null ? InputVariableValueSelectEnum.Select : InputVariableValueSelectEnum.getInputVariableValueSelect(valueType);
	}

	public void setValueType(InputVariableValueSelectEnum valueTypeEnum)
	{
		this.put(VALUETYPE, valueTypeEnum == null ? InputVariableValueSelectEnum.Input.getValue() : valueTypeEnum.getValue());
	}

	public String getRange()
	{
		return (String) this.get(RANGE);
	}

	public void setRange(String range)
	{
		this.put(RANGE, range);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfInputVariable result = new TableOfInputVariable();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}

	public void setListValue(List<TableOfDefineCondition> listValue)
	{
		if (!SetUtils.isNullList(listValue))
		{
			StringBuffer value = new StringBuffer();
			StringBuffer desc = new StringBuffer();
			for (TableOfDefineCondition condition : listValue)
			{
				if (!StringUtils.isNullString(condition.getDefinitionName()))
				{
					if (value.length() > 0)
					{
						value.append(ConfigParameterConstants.UNIQUE_SPLIT_CHAR);
						desc.append(ConfigParameterConstants.UNIQUE_SPLIT_CHAR);
					}
					value.append(condition.getDefinitionName());
					desc.append(StringUtils.convertNULLtoString(condition.getDefinitionValue()));
				}
			}
			this.put(RANGE, StringUtils.convertNULL(value.toString()));
			this.put(VALUEDESCRIPTION, StringUtils.convertNULL(desc.toString()));
		}
		this.listValue = listValue;
	}

	public List<TableOfDefineCondition> getListValue()
	{
		this.listValue = new ArrayList<TableOfDefineCondition>();
		if (!StringUtils.isNullString((String) this.get(RANGE)))
		{
			String[] values = StringUtils.splitStringWithDelimiterHavEnd(ConfigParameterConstants.UNIQUE_SPLIT_CHAR, (String) this.get(RANGE));
			String[] descs = StringUtils.splitStringWithDelimiterHavEnd(ConfigParameterConstants.UNIQUE_SPLIT_CHAR, (String) this.get(VALUEDESCRIPTION));
			if (values != null && values.length > 0)
			{
				int j = 1;
				for (int i = 0; i < values.length; i++)
				{
					TableOfDefineCondition condition = new TableOfDefineCondition();
					if (!StringUtils.isNullString(values[i]))
					{
						String vv = values[i];
						condition.setDefinitionName(vv);
						condition.put("index", j);
						if (descs != null && descs.length > i)
						{
							String de = descs[i];
							condition.setDefinitionValue(de);
						}
						listValue.add(condition);
						j++;
					}
				}
			}
		}
		return listValue;
	}
}
