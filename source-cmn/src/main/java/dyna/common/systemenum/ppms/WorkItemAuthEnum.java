package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

public enum WorkItemAuthEnum
{
	// 仅查看
	ONLYVIEW("1"),

	// 仅上传
	ONLYUPLOAD("2"),

	// 所有的文件操作权限或者所有关联权限
	ALL("3"), ;

	private String	type;

	private WorkItemAuthEnum(String type)
	{
		this.type = type;
	}

	public WorkItemAuthEnum typeof(String type)
	{
		if (StringUtils.isNullString(type))
		{
			return null;
		}
		for (WorkItemAuthEnum authType : WorkItemAuthEnum.values())
		{
			if (type.equals(authType.getType()))
			{
				return authType;
			}
		}
		return null;
	}

	public String getType()
	{
		return this.type;
	}
}
