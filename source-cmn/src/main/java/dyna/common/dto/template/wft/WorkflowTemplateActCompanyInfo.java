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
 * 工作流模板活动节点通知人Bean
 * 
 * @author WangLHB
 * 
 */
@Cache
public class WorkflowTemplateActCompanyInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2171920597237057284L;

	// public static final String GUID = "GUID";
	public static final String	TEMPLATEACTRTGUID	= "TEMPLATEACTRTGUID";

	public static final String	COMPANYNAME			= "COMPANYNAME";

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
	 * @return the companyName
	 */
	public String getCompanyName()
	{
		return (String) this.get(COMPANYNAME);
	}

	/**
	 * @param companyName
	 *            the companyName to set
	 */
	public void setCompanyName(String companyName)
	{
		this.put(COMPANYNAME, companyName);
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMPLATEACTRTGUID);

	}

	@Override
	public WorkflowTemplateActCompanyInfo clone()
	{
		return (WorkflowTemplateActCompanyInfo) super.clone();
	}
}
