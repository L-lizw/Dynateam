/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateInfo
 * WangLHB Jan 9, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
@Cache
public class WorkflowTemplateInfo extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 904737217884318579L;

	public static final String	ID					= "WFTID";
	public static final String	NAME				= "WFTNAME";
	public static final String	WFNAME				= "WFNAME";
	public static final String	TITLE				= "TITLE";
	public static final String	IS_VALID			= "ISVALID";
	public static final String	BMGUID				= "BMGUID";
	private static final String	REQUIRED_EXECUTOR	= "REQUIREDEXECUTOR";

	public void setRequiredExecutor(boolean isRequiredExecutor)
	{
		this.put(REQUIRED_EXECUTOR, BooleanUtils.getBooleanStringYN(isRequiredExecutor));
	}

	public boolean isRequiredExecutor()
	{
		if (StringUtils.isNullString((String) this.get(REQUIRED_EXECUTOR)))
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(REQUIRED_EXECUTOR));
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return (String) this.get(ID);
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.put(ID, id);
	}

	/**
	 * @return the BMGuid
	 */
	public String getBMGuid()
	{
		return (String) this.get(BMGUID);
	}

	/**
	 * @param BMGuid
	 *            the BMGuid to set
	 */
	public void setBMGuid(String BMGuid)
	{
		this.put(BMGUID, BMGuid);
	}

	/**
	 * @return the wfName
	 */
	public String getWFName()
	{
		return (String) this.get(WFNAME);
	}

	/**
	 * @param wfName
	 *            the wfName to set
	 */
	public void setWFName(String wfName)
	{
		this.put(WFNAME, wfName);
	}

	public void setValid(boolean isValid)
	{
		this.put(IS_VALID, BooleanUtils.getBooleanString10(isValid));
	}

	@Override
	public boolean isValid()
	{
		if (this.get(IS_VALID) == null)
		{
			return true;
		}

		return BooleanUtils.getBooleanBy10((String) this.get(IS_VALID));
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	/**
	 * @return the title
	 */
	public String getTitle(LanguageEnum lang)
	{
		String title = this.getTitle();
		return StringUtils.getMsrTitle(title, lang.getType());
	}

	public void clearForCreate()
	{
		this.clear(GUID);
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
	public WorkflowTemplateInfo clone()
	{
		WorkflowTemplateInfo templateInfo = (WorkflowTemplateInfo) super.clone();
		templateInfo.putAll(this);
		return templateInfo;
	}
}
