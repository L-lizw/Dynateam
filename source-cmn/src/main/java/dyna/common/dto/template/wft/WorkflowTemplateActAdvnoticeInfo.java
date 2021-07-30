/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActAdvnotice
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 	工作流模板活动节点通知人设置Bean
 * 
 * @author WangLHB
 * 
 */
@Cache
public class WorkflowTemplateActAdvnoticeInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2171920597237057284L;

	// public static final String GUID = "GUID";
	public static final String	TEMPLATEACTRTGUID	= "TEMPLATEACTRTGUID";

	public static final String	TEMPLATEGUID		= "TEMPLATEGUID";

	// 流程发起人
	public static final String	HASORGANIGER		= "HASORGANIGER";

	// 上一活动
	public static final String	HASLASTEXECUTOR		= "HASLASTEXECUTOR";
	// 下一活动
	public static final String	HASNEXTEXECUTOR		= "HASNEXTEXECUTOR";
	// 所有活动
	public static final String	HASALLEXECUTOR		= "HASALLEXECUTOR";

	// 上级
	public static final String	HASLEADER			= "HASLEADER";

	public static final String	NOTICETYPE			= "NOTICETYPE";

	// wf_template_actrt_advnotice
	public static final String	NOTICETYPE_ACTRTADV	= "ACTRTADV";

	// wf_template_actrt_defnotice
	public static final String	NOTICETYPE_ACTRTDEF	= "ACTRTDEF";

	// wf_template_actrt_finnotice
	public static final String	NOTICETYPE_ACTRTFIN	= "ACTRTFIN";

	// wf_template_opennotice
	public static final String	NOTICETYPE_OPEN		= "OPEN";

	// wf_template_opennotice
	public static final String	NOTICETYPE_CLOSE	= "CLOSE";

	// 1：has； 0：not
	public static final String	ADVNOTICE_TYPE_HAS	= "1";
	public static final String	ADVNOTICE_TYPE_NOT	= "0";

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
	 * @return the hasOrganiger
	 */
	public boolean hasOrganiger()
	{
		return ADVNOTICE_TYPE_HAS.equals(this.get(HASORGANIGER)) ? true : false;
	}

	/**
	 * @param hasOrganiger
	 *            the hasOrganiger to set
	 */
	public void setOrganiger(boolean hasOrganiger)
	{
		this.put(HASORGANIGER, hasOrganiger == true ? ADVNOTICE_TYPE_HAS : ADVNOTICE_TYPE_NOT);
	}

	/**
	 * @return the hasLastExecutor
	 */
	public boolean hasLastExecutor()
	{
		return ADVNOTICE_TYPE_HAS.equals(this.get(HASLASTEXECUTOR)) ? true : false;
	}

	/**
	 * @param hasLastExecutor
	 *            the hasLastExecutor to set
	 */
	public void setLastExecutor(boolean hasLastExecutor)
	{
		this.put(HASLASTEXECUTOR, hasLastExecutor == true ? ADVNOTICE_TYPE_HAS : ADVNOTICE_TYPE_NOT);
	}

	/**
	 * @return the hasNextExecutor
	 */
	public boolean hasNextExecutor()
	{
		return ADVNOTICE_TYPE_HAS.equals(this.get(HASNEXTEXECUTOR)) ? true : false;
	}

	/**
	 * @param hasNextExecutor
	 *            the hasNextExecutor to set
	 */
	public void setNextExecutor(boolean hasNextExecutor)
	{
		this.put(HASNEXTEXECUTOR, hasNextExecutor == true ? ADVNOTICE_TYPE_HAS : ADVNOTICE_TYPE_NOT);
	}

	/**
	 * @return the hasAllExecutor
	 */
	public boolean hasAllExecutor()
	{
		return ADVNOTICE_TYPE_HAS.equals(this.get(HASALLEXECUTOR)) ? true : false;
	}

	/**
	 * @param hasAllExecutor
	 *            the hasAllExecutor to set
	 */
	public void setAllExecutor(boolean hasAllExecutor)
	{
		this.put(HASALLEXECUTOR, hasAllExecutor == true ? ADVNOTICE_TYPE_HAS : ADVNOTICE_TYPE_NOT);
	}

	/**
	 * @return the hasLeader
	 */
	public boolean hasLeader()
	{
		return ADVNOTICE_TYPE_HAS.equals(this.get(HASLEADER)) ? true : false;
	}

	/**
	 * @param hasLeader
	 *            the hasLeader to set
	 */
	public void setLeader(boolean hasLeader)
	{
		this.put(HASLEADER, hasLeader == true ? ADVNOTICE_TYPE_HAS : ADVNOTICE_TYPE_NOT);
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMPLATEGUID);
		this.clear(TEMPLATEACTRTGUID);
	}

	@Override
	public WorkflowTemplateActAdvnoticeInfo clone()
	{
		return (WorkflowTemplateActAdvnoticeInfo) super.clone();
	}
}
