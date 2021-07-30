/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttachTemplate
 * zhanghj 2011-4-16
 */
package dyna.common.dto.wf;

import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.PerformerTypeEnum;

/**
 * 审批模板明细
 * 
 * @author zhanghj
 * 
 */
public class ApproveTemplateDetail extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;
	public static final String	MASTER_FK			= "MASTERFK";
	public static final String	ACTRT_NAME			= "ACTRTNAME";
	public static final String	PERF_GUID			= "PERFGUID";
	public static final String	PERF_NAME			= "PERFNAME";
	public static final String	PERF_TYPE			= "PERFTYPE";
	public static final String	DEADLINE			= "DEADLINE";

	public static final String	PERFGROUPGUID		= "PERFGROUPGUID";
	public static final String	PERFROLEGUID		= "PERFROLEGUID";

	public String getTemplateGuid()
	{
		return (String) super.get(MASTER_FK);
	}

	public void setTemplateGuid(String templateGuid)
	{
		super.put(MASTER_FK, templateGuid);
	}

	public String getActrtName()
	{
		return (String) super.get(ACTRT_NAME);
	}

	public void setActrtName(String actrtName)
	{
		super.put(ACTRT_NAME, actrtName);
	}

	public String getPerfGuid()
	{
		return (String) super.get(PERF_GUID);
	}

	public void setPerfGuid(String perfGuid)
	{
		super.put(PERF_GUID, perfGuid);
	}

	public String getPerfName()
	{
		return (String) super.get(PERF_NAME);
	}
	
	public void setPerfName(String perfName)
	{
		super.put(PERF_NAME, perfName);
	}

	public PerformerTypeEnum getPerformerType()
	{
		return PerformerTypeEnum.valueOf((String) this.get(PERF_TYPE));
	}

	public void setPerformerType(PerformerTypeEnum type)
	{
		this.put(PERF_TYPE, type.name());
	}

	public Date getDeadline()
	{
		return (Date) super.get(DEADLINE);
	}

	public void setDeadline(Date deadline)
	{
		super.put(DEADLINE, deadline);
	}

	/**
	 * @return the perGroup
	 */
	public String getPerfGroupGuid()
	{
		return (String) this.get(PERFGROUPGUID);
	}

	/**
	 * @param perGroup
	 *            the perGroup to set
	 */
	public void setPerfGroupGuid(String perfGroup)
	{
		this.put(PERFGROUPGUID, perfGroup);
	}

	/**
	 * @return the perRole
	 */
	public String getPerfRoleGuid()
	{
		return (String) this.get(PERFROLEGUID);
	}

	/**
	 * @param perRole
	 *            the perRole to set
	 */
	public void setPerfRoleGuid(String perfRole)
	{
		this.put(PERFROLEGUID, perfRole);
	}
}
