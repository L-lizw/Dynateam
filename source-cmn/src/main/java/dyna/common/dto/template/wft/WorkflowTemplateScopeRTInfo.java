/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateScopeBo
 * WangLHB Jun 25, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.wft.WorkflowTemplateScopeRTInfoMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
@Cache
@EntryMapper(WorkflowTemplateScopeRTInfoMapper.class)
public class WorkflowTemplateScopeRTInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID		= -7890082245736879998L;

	public static final String	WFTEMPLATEFK			= "WFTEMPLATEFK";

	public static final String	RELATION_TEMPLATE_ID	= "TEMPLATEID";

	public static final String	TEMPLATE_TITLE			= "TEMPLATETITLE";

	/**
	 * @return the wftemplatefk
	 */
	public String getWFTemplatefk()
	{
		return (String) this.get(WFTEMPLATEFK);
	}

	/**
	 * @param wftemplatefk
	 *            the wftemplatefk to set
	 */
	public void setWFTemplatefk(String wfTemplatefk)
	{
		this.put(WFTEMPLATEFK, wfTemplatefk);
	}

	/**
	 * @return the TemplateId
	 */
	public String getTemplateID()
	{
		return (String) this.get(RELATION_TEMPLATE_ID);
	}

	/**
	 * @param templateId
	 *            the templateId to set
	 */
	public void setTemplateID(String templateId)
	{
		this.put(RELATION_TEMPLATE_ID, templateId);
	}

	/**
	 * @return the TemplateTitle
	 */
	public String getTemplateTitle()
	{
		return (String) this.get(TEMPLATE_TITLE);
	}

	/**
	 * @return the templateTitle by language
	 */
	public String getRelationTitle(LanguageEnum lang)
	{
		String boTitle2 = this.getTemplateTitle();
		return StringUtils.getMsrTitle(boTitle2, lang.getType());
	}

	/**
	 * @param templateTitle
	 *            the templateTitle to set
	 */
	public void setTemplateTitle(String templateTitle)
	{
		this.put(TEMPLATE_TITLE, templateTitle);
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(WFTEMPLATEFK);
	}
}
