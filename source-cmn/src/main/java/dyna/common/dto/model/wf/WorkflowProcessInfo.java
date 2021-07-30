/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessDefInfo
 * Wanglei 2010-11-3
 */
package dyna.common.dto.model.wf;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.WorkflowType;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
@Cache
public class WorkflowProcessInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -7446298478270942777L;

	public static final String	WFNAME				= "WFNAME";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	TITLE				= "TITLE";

	private WorkflowType		type				= null;

	public String getTitle(LanguageEnum language)
	{
		return StringUtils.getMsrTitle(this.getTitle(), language.getType());
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setWFName(String wfName)
	{
		this.put(WFNAME, wfName);
	}

	public String getWFName()
	{
		return (String) this.get(WFNAME);
	}

	@Override
	public void setName(String name)
	{
		this.setWFName(name);
	}

	@Override
	public String getName()
	{
		return this.getWFName();
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(WorkflowType type)
	{
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public WorkflowType getType()
	{
		return this.type;
	}
}
