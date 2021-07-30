/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Transition 工作流程活动变迁
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * 工作流程活动执行人
 * 
 * @author Wanglei
 * 
 */
public class Performer extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	PROCRT_TITLE		= "PROCRTTITLE";
	public static final String	ACTRT_GUID			= "ACTRTGUID";
	public static final String	ACTRT_TITLE			= "ACTRTTITLE";
	public static final String	PERF_GUID			= "PERFGUID";
	public static final String	PERF_NAME			= "PERFNAME";

	public static final String	PERF_TYPE			= "PERFTYPE";
	public static final String	ACTUAL_PERF_GUID	= "ACTUALPERFGUID";
	public static final String	ACTUAL_PERF_NAME	= "ACTUALPERFNAME";
	public static final String	VIEW_TIME			= "VIEWTIME";
	public static final String	IS_DELETE			= "ISDELETE";
	public static final String	DELETE_TIME			= "DELETETIME";
	public static final String	IS_FINISH			= "ISFINISH";
	public static final String	FINISH_TIME			= "FINISHTIME";

	public static final String	PERF_GROUP_GUID		= "PERFGROUPGUID";
	public static final String	PERF_ROLE_GUID		= "PERFROLEGUID";
	public static final String	IS_RECHECKER		= "ISRECHECKER";//是否是加签人
	
	public Performer()
	{
		this.setFinished(false);
		this.setDeleted(false);
	}

	public String getProcessRuntimeTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(PROCRT_TITLE), lang.getType());
	}

	public String getActivityRuntimeTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(ACTRT_TITLE), lang.getType());
	}

	public String getActualPerformerNAME()
	{
		return (String) this.get(ACTUAL_PERF_NAME);
	}

	public void setActualPerformerNAME(String actualPerformerName)
	{
		this.put(ACTUAL_PERF_NAME, actualPerformerName);
	}

	public String getActualPerformerGuid()
	{
		return (String) this.get(ACTUAL_PERF_GUID);
	}

	public void setActualPerformerGuid(String guid)
	{
		this.put(ACTUAL_PERF_GUID, guid);
	}

	public Boolean isDeleted()
	{
		return BooleanUtils.getBooleanByYN((String) super.get(IS_DELETE));
	}

	public void setDeleted(Boolean isDeleted)
	{
		super.put(IS_DELETE, BooleanUtils.getBooleanStringYN(isDeleted));
		this.put(DELETE_TIME, null);
	}

	public Boolean isFinished()
	{
		return BooleanUtils.getBooleanByYN((String) super.get(IS_FINISH));
	}

	public void setFinished(Boolean isFinished)
	{
		super.put(IS_FINISH, BooleanUtils.getBooleanStringYN(isFinished));
		this.put(FINISH_TIME, null);
	}

	public Date getDeleteTime()
	{
		return (Date) super.get(DELETE_TIME);
	}

	public Date getFinishTime()
	{
		return (Date) this.get(FINISH_TIME);
	}

	public Date getViewTime()
	{
		return (Date) this.get(VIEW_TIME);
	}

	public void clearViewTime()
	{
		this.put(VIEW_TIME, null);
	}

	public PerformerTypeEnum getPerformerType()
	{
		if (this.get(PERF_TYPE) == null)
		{
			return PerformerTypeEnum.USER;
		}
		return PerformerTypeEnum.valueOf((String) this.get(PERF_TYPE));
	}

	public void setPerformerType(PerformerTypeEnum type)
	{
		this.put(PERF_TYPE, type.name());
	}

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public String getActRuntimeGuid()
	{
		return (String) super.get(ACTRT_GUID);
	}

	public void setActRuntimeGuid(String guid)
	{
		super.put(ACTRT_GUID, guid);
	}

	public String getPerformerName()
	{
		return (String) super.get(PERF_NAME);
	}

	public void setPerformerName(String PerformerName)
	{
		this.put(PERF_NAME, PerformerName);
	}

	public String getPerformerGuid()
	{
		return (String) super.get(PERF_GUID);
	}

	public void setPerformerGuid(String guid)
	{
		super.put(PERF_GUID, guid);
	}

	/**
	 * @return the perGroup
	 */
	public String getPerGroupGuid()
	{
		return (String) this.get(PERF_GROUP_GUID);
	}

	/**
	 * @param perGroup
	 *            the perGroup to set
	 */
	public void setPerGroupGuid(String perGroup)
	{
		this.put(PERF_GROUP_GUID, perGroup);
	}

	/**
	 * @return the perRole
	 */
	public String getPerRoleGuid()
	{
		return (String) this.get(PERF_ROLE_GUID);
	}

	/**
	 * @param perRole
	 *            the perRole to set
	 */
	public void setPerRoleGuid(String perRole)
	{
		this.put(PERF_ROLE_GUID, perRole);
	}

	public Boolean isRechecker()
	{
		return BooleanUtils.getBooleanByYN((String) super.get(IS_RECHECKER));
	}

	public void setRechecker(Boolean isRechecker)
	{
		super.put(IS_RECHECKER, BooleanUtils.getBooleanStringYN(isRechecker));
	}
	
}
