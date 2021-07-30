/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActAdvnoticeper
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.util.StringUtils;

/**
 * 工作流模板活动节点执行人bean
 * 
 * @author WangLHB
 * 
 */
@Cache
public class WorkflowTemplateActPerformerInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID			= -4431941455176130385L;

	public static final String	TEMPLATEGUID				= "TEMPLATEGUID";
	public static final String	TEMPLATEACTRTGUID			= "TEMPLATEACTRTGUID";
	public static final String	PERFTYPE					= "PERFTYPE";
	public static final String	PERFGUID					= "PERFGUID";
	public static final String	NOTICETYPE					= "NOTICETYPE";

	public static final String	PERFGROUPGUID				= "PERFGROUPGUID";
	public static final String	PERFROLEGUID				= "PERFROLEGUID";
	public static final String	NAME						= "PERNAME";

	// wf_template_actrt_advnoticeper
	public static final String	NOTICETYPE_ACTRTADV			= "ACTRTADV";

	// wf_template_actrt_defnoticeper
	public static final String	NOTICETYPE_ACTRTDEF			= "ACTRTDEF";

	// wf_template_actrt_finnoticeper
	public static final String	NOTICETYPE_ACTRTFIN			= "ACTRTFIN";

	// wf_template_observer
	public static final String	NOTICETYPE_OBSERVER			= "OBSERVER";

	// wf_template_originator
	public static final String	NOTICETYPE_ORIGINATOR		= "ORIGINATOR";

	// WF_TEMPLATE_OPENNOTICEPER
	public static final String	NOTICETYPE_OPENNOTICE		= "OPENNOTICE";

	// wf_template_closenoticeper
	public static final String	NOTICETYPE_CLOSENOTICE		= "CLOSENOTICE";

	// wf_template_actrt_execscope
	public static final String	NOTICETYPE_ACTRTEXECSCOPE	= "ACTRTEXECSCOPE";

	// wf_template_actrt_executor
	public static final String	NOTICETYPE_ACTRTEXECUTOR	= "ACTRTEXECUTOR";

	// bi_config_erp_acl_noticer
	public static final String	NOTICETYPE_ERPNOTICER		= "ERPNOTICER";

	// bi_config_erp_acl_user
	public static final String	NOTICETYPE_ERPUSER			= "ERPUSER";

	// bi_config_erp_acl_noticer_fail
	public static final String	NOTICETYPE_ERPNOTICERFAIL	= "ERPNOTICERFAIL";

	// wf_template_actrt&&wf_template_actrt_execscope
	// 是否是执行人
	public static final String	PERSCOPETYPE				= "PERSCOPETYPE";

	// 1：指定执行人；2：范围 default：1
	public static final String	PERSCOPE_TYPE_SPECIFIE		= "1";

	/**
	 * @return the templateGuid
	 */
	public String getTemplateGuid()
	{
		return (String) this.get(TEMPLATEGUID);
	}

	/**
	 * @param templateGuid
	 *            the templateGuid to set
	 */
	public void setTemplateGuid(String templateGuid)
	{
		this.put(TEMPLATEGUID, templateGuid);
	}

	/**
	 * @return the templateActrtGuid
	 */
	public String getTemplateActrtGuid()
	{
		return (String) this.get(TEMPLATEACTRTGUID);
	}

	/**
	 * @param templateActrtGuid
	 *            the templateActrtGuid to set
	 */
	public void setTemplateActrtGuid(String templateActrtGuid)
	{
		this.put(TEMPLATEACTRTGUID, templateActrtGuid);
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
	 * @return the noticeType
	 */
	public String getNoticeType()
	{
		return (String) this.get(NOTICETYPE);
	}

	/**
	 * @param noticeType
	 *            the noticeType to set
	 */
	public void setNoticeType(String noticeType)
	{
		this.put(NOTICETYPE, noticeType);
	}

	/**
	 * @return the perGroup
	 */
	public String getPerfGroupGuid()
	{
		return (String) this.get(PERFGROUPGUID);
	}

	/**
	 * @return the perscopeType
	 */
	public String getPerscopeType()
	{
		return StringUtils.isNullString((String) this.get(PERSCOPETYPE)) ? PERSCOPE_TYPE_SPECIFIE : (String) this.get(PERSCOPETYPE);
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

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMPLATEGUID);
		this.clear(TEMPLATEACTRTGUID);
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

	@Override
	public WorkflowTemplateActPerformerInfo clone()
	{
		return (WorkflowTemplateActPerformerInfo) super.clone();
	}
}
