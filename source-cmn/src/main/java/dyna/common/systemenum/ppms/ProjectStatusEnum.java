/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 项目管理中项目状态枚举
 * 
 * @author WangLHB
 * 
 */
public enum ProjectStatusEnum
{
	INI("INI", "ID_PRJ_STATUS_INITIALIZATION"), // 已创建
	// CCT("CCT", "ID_PRJ_STATUS_CONCREATE"), // 立项
	RUN("RUN", "ID_PRJ_STATUS_RUNNING"), // 运行中
	SSP("SSP", "ID_PRJ_STATUS_SUSPEND"), // 中止
	COP("COP", "ID_PRJ_STATUS_COMPLETE"), // 结束
	APP("APP", "ID_PRJ_STATUS_APP"),//审批
	PUS("PUS", "ID_PRJ_STATUS_PAUSE");// 暂停

	private String	value	= null;
	private String	msrId	= null;

	ProjectStatusEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	public static ProjectStatusEnum getStatusEnum(Object status)
	{
		try
		{
			if (status == null)
			{
				return null;
			}
			return ProjectStatusEnum.valueOf(status.toString());
		}
		catch (Exception e)
		{
		}
		return null;
	}

	public static ProjectStatusEnum[] getValidStatusEnum()
	{
		return new ProjectStatusEnum[] { INI, RUN, SSP, COP, PUS };
	}
}
