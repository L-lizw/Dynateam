package dyna.common.bean.data.configparamter;

import dyna.common.util.StringUtils;

/**
 * 
 * 材料明细页签位置
 * 
 */
public enum DetailPositionEnum
{
	Up("UP"), Down("DOWN"), Left("LEFT"), Right("Right");

	private String	value	= null;

	private DetailPositionEnum(String value)
	{
		this.value = value;
	}

	public static DetailPositionEnum getEnumByValue(String va)
	{
		if (!StringUtils.isNullString(va))
		{
			for (DetailPositionEnum position : DetailPositionEnum.values())
			{
				if (position.value.equalsIgnoreCase(va))
				{
					return position;
				}
			}
		}
		return null;
	}
}
