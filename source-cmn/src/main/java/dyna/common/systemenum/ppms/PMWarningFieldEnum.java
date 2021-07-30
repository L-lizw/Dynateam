/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * duanll Jun 1, 2012
 */
package dyna.common.systemenum.ppms;

import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.PMConstans;

/**
 * @author WangLH
 *         项目预警枚举
 * 
 */
public enum PMWarningFieldEnum
{
	SYSTEMTIME("SYSTEMTIME$", "", "ID_ENUM_PM_SYSTEMTIME"), //
	SENDER("SENDER$", "", "ID_ENUM_PM_SENDER"), //
	RECEIVER("RECEIVER$", "", "ID_ENUM_PM_RECEIVER"), //
	PROJECT_ID(SystemClassFieldEnum.ID.getName(), "PROJECT", "ID_ENUM_PM_PROJECT_ID"), //
	PROJECT_NAME(SystemClassFieldEnum.NAME.getName(), "PROJECT", "ID_ENUM_PM_PROJECT_NAME"), //
	PROJECT_STARTPLANDATE(PPMFoundationObjectUtil.PLANSTARTTIME, "PROJECT", "ID_ENUM_PM_PROJECT_STARTPLANDATE"), //
	PROJECT_FINISHPLANDATE(PPMFoundationObjectUtil.PLANFINISHTIME, "PROJECT", "ID_ENUM_PM_PROJECT_FINISHPLANDATE"), //
	PROJECT_EXECUTOR(PPMFoundationObjectUtil.EXECUTOR, "PROJECT", "ID_ENUM_PM_PROJECT_EXECUTOR"), //

	TASK_ID(SystemClassFieldEnum.ID.getName(), "TASK", "ID_ENUM_PM_TASK_ID"), //
	TASK_NAME(SystemClassFieldEnum.NAME.getName(), "TASK", "ID_ENUM_PM_TASK_NAME"), //
	TASK_STARTPLANDATE(PPMFoundationObjectUtil.PLANSTARTTIME, "TASK", "ID_ENUM_PM_TASK_STARTPLANDATE"), //
	TASK_FINISHPLANDATE(PPMFoundationObjectUtil.PLANFINISHTIME, "TASK", "ID_ENUM_PM_TASK_FINISHPLANDATE"), //
	TASK_ACTUALSTARTTIME(PPMFoundationObjectUtil.ACTUALSTARTTIME, "TASK", "ID_ENUM_PM_TASK_STARTPLANDATE"), //
	TASK_ACTUALFINISHTIME(PPMFoundationObjectUtil.ACTUALFINISHTIME, "TASK", "ID_ENUM_PM_TASK_FINISHPLANDATE"), //
	TASK_EXECUTOR(PPMFoundationObjectUtil.EXECUTOR, "TASK", "ID_ENUM_PM_TASK_EXECUTOR");//

	private String	fieldName;

	private String	type;

	private String	msrId;

	private PMWarningFieldEnum(String fieldName, String type, String msrId)
	{
		this.fieldName = fieldName;
		this.type = type;
		this.msrId = msrId;
	}

	public String getFieldName()
	{
		return this.fieldName;
	}

	public String getType()
	{
		return this.type;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	@Override
	public String toString()
	{
		if (this.type == null || "".equals(this.type))
		{
			return PMConstans.CONTENT_FIELD_SYMBO_START + this.fieldName + PMConstans.CONTENT_FIELD_SYMBO_END;
		}
		else
		{
			return PMConstans.CONTENT_FIELD_SYMBO_START + this.type + ":" + this.fieldName
					+ PMConstans.CONTENT_FIELD_SYMBO_END;
		}
	}

}
