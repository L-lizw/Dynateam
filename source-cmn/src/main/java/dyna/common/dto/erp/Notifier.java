/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActAdvnoticeper
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.erp;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.PerformerTypeEnum;

/**
 * 通知者
 * 
 * @author WangLHB
 * 
 */
public class Notifier extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4431941455176130385L;

	public static final String	PERFTYPE			= "PERFTYPE";
	public static final String	PERFGUID			= "PERFGUID";

	public static final String	PERFGROUPGUID		= "PERFGROUPGUID";
	public static final String	PERFROLEGUID		= "PERFROLEGUID";

	/**
	 * @return the perType
	 */
	public PerformerTypeEnum getPerfType()
	{
		try
		{
			return PerformerTypeEnum.valueOf((String) this.get(PERFTYPE));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * @param perfType
	 *            the perfType to set
	 */
	public void setPerfType(PerformerTypeEnum perfType)
	{
		if (perfType == null)
		{
			this.put(PERFTYPE, "");
		}
		else
		{
			this.put(PERFTYPE, perfType.toString());
		}
	}

	/**
	 * @return the perfGuid
	 */
	public String getPerfGuid()
	{
		return (String) this.get(PERFGUID);
	}

	/**
	 * @param perfGuid
	 *            the perfGuid to set
	 */
	public void setPerfGuid(String perfGuid)
	{
		this.put(PERFGUID, perfGuid);
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
	 * @param perfRole
	 *            the perfRole to set
	 */
	public void setPerfRoleGuid(String perfRole)
	{
		this.put(PERFROLEGUID, perfRole);
	}
}
