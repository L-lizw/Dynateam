/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActAdvnoticeper
 * WangLHB Jan 6, 2012
 */
package dyna.common.bean.data.trans;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.PerformerTypeEnum;

/**
 * 工作流模板活动节点执行人bean
 * 
 * @author WangLHB
 * 
 */
public class TransformManualPerformer extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID		= 4505266119270964540L;
	public static String		TRANSFORM_CONFIG_GUID	= "TRANSFORMCONFIGGUID";
	public static String		STATUSTYPE				= "STATUSTYPE";

	public static final String	PERFTYPE				= "PERFTYPE";
	public static final String	PERFGUID				= "PERFGUID";

	public static final String	PERFGROUPGUID			= "PERFGROUPGUID";
	public static final String	PERFROLEGUID			= "PERFROLEGUID";

	public static final String	NAME					= "NAME";

	public String getTransformConfigGuid()
	{
		return (String) this.get(TRANSFORM_CONFIG_GUID);
	}

	public void setTransformConfigGuid(String transformConfigGuid)
	{
		this.put(TRANSFORM_CONFIG_GUID, transformConfigGuid);
	}

	/**
	 * @return the templateActrtGuid
	 */
	public String getStatusType()
	{
		return (String) this.get(STATUSTYPE);
	}

	public void setStatusType(String statusType)
	{
		this.put(STATUSTYPE, statusType);
	}

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

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}

}
