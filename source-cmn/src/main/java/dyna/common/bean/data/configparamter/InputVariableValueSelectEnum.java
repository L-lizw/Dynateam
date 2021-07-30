package dyna.common.bean.data.configparamter;

import dyna.common.util.StringUtils;

public enum InputVariableValueSelectEnum
{
	Input("Input", "ID_APP_INPUTVARIABLEVALUESELECTENUM_INPUT"), // 输入
	Select("Select", "ID_APP_INPUTVARIABLEVALUESELECTENUM_SELECT");// 选择

	private String	value	= null;
	private String	msId	= null;
	private InputVariableValueSelectEnum(String value, String msId)
	{
		this.value = value;
		this.msId = msId;
	}

	public static InputVariableValueSelectEnum getInputVariableValueSelect(String vv)
	{
		if (!StringUtils.isNullString(vv))
		{
			for (InputVariableValueSelectEnum sEnum : InputVariableValueSelectEnum.values())
			{
				if (vv.equalsIgnoreCase(sEnum.getValue()))
				{
					return sEnum;
				}
			}
		}
		return null;
	}

	public String getValue()
	{
		return value;
	}

	public String getMsId()
	{
		return msId;
	}
}
