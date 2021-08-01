/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActClassRelation
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.wft.WorkflowTemplateActClassUIInfoMapper;
import dyna.common.util.BooleanUtils;

/**
 * 工作流模板活动节点class UI Bean
 * 
 * @author WangLHB
 * 
 */
@Cache
@EntryMapper(WorkflowTemplateActClassUIInfoMapper.class)
public class WorkflowTemplateActClassUIInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2833440885506515220L;

	// public static final String GUID = "GUID";
	public static final String	TEMACTBOGUID		= "TEMACTBOGUID";
	public static final String	BOUINAME			= "BOUINAME";
	public static final String	UITITLE				= "UITITLE";

	public static final String	EDIT				= "EDITUINAME";
	public static final String	VIEW				= "SELECTUINAME";

	/**
	 * @return the temActBOGuid
	 */
	public String getTemActBOGuid()
	{
		return (String) this.get(TEMACTBOGUID);
	}

	/**
	 * @param temactBOGuid
	 *            the temactBOGuid to set
	 */
	public void setTemActBOGuid(String temactBOGuid)
	{
		this.put(TEMACTBOGUID, temactBOGuid);
	}

	/**
	 * @return the boUIName
	 */
	public String getBOUIName()
	{
		return (String) this.get(BOUINAME);
	}

	/**
	 * @param boUIName
	 *            the boUIName to set
	 */
	public void setBOUIName(String boUIName)
	{
		this.put(BOUINAME, boUIName);
	}

	/**
	 * @return the uiTitle
	 */
	public String getUITitle()
	{
		return (String) this.get(UITITLE);
	}

	/**
	 * @param uiTitle
	 *            the uiTitle to set
	 */
	public void setUITitle(String uiTitle)
	{
		this.put(UITITLE, uiTitle);
	}

	/**
	 * @return the isView
	 */
	public boolean isView()
	{
		if (this.get(VIEW) == null)
		{
			return true;
		}
		else
		{
			return BooleanUtils.getBooleanBy10((String) this.get(VIEW));
		}
	}

	/**
	 * @param isView
	 *            the isView to set
	 */
	public void setView(boolean isView)
	{
		this.put(VIEW, BooleanUtils.getBooleanString10(isView));
	}

	/**
	 * @return the isEdit
	 */
	public boolean isEdit()
	{
		if (this.get(EDIT) == null)
		{
			return false;
		}
		else
		{
			return BooleanUtils.getBooleanBy10((String) this.get(EDIT));
		}

	}

	/**
	 * @param isEdit
	 *            the isEdit to set
	 */
	public void setEdit(boolean isEdit)
	{
		this.put(EDIT, BooleanUtils.getBooleanString10(isEdit));
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMACTBOGUID);

	}

	@Override
	public WorkflowTemplateActClassUIInfo clone()
	{
		return (WorkflowTemplateActClassUIInfo) super.clone();
	}
}
