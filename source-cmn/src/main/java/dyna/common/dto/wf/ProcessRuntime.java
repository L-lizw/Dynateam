/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessRuntime 工作流程
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.wf.ProcessRuntimeMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ProcessStatusEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * 工作流程
 *
 * @author Wanglei
 *
 */
@EntryMapper(ProcessRuntimeMapper.class)
public class ProcessRuntime extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;
	public static final String	NAME				= "PROCRTNAME";
	public static final String	TITLE				= "TITLE";
	public static final String	ACTRTTITLE			= "ACTRTTITLE";
	public static final String	DESCRIPTION			= "DESCRIPTION";
	public static final String	STATUS				= "STATUS";
	public static final String	PROCDEF_GUID		= "PROCDEFGUID";
	public static final String	IS_DELETE			= "ISDELETE";
	public static final String	DELETE_TIME			= "DELETETIME";
	public static final String	IS_FINISH			= "ISFINISH";
	public static final String	FINISH_TIME			= "FINISHTIME";
	public static final String	PARENT_GUID			= "PARENTGUID";
	public static final String	ACTRT_GUID			= "ACTRTGUID";
	public static final String	CREATE_USERNAME		= "CREATEUSERNAME";
	public static final String	CNT_SONS			= "CNTSONS";
	// 在哪个对象上发起的流程,GUID+"$"+CLASSGUID
	public static final String	FROMINSTANCE		= "FROMINSTANCE";

	public static final String	WFTEMPLATEGUID		= "WFTEMPLATEGUID";
	public static final String	WFTEMPLATENAME		= "TEMPLATENAME";
	public static final String	WFTEMPLATETITLE		= "WFTEMPLATETITLE";

	public String getCreateUserName()
	{
		return (String) super.get(CREATE_USERNAME);
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) super.get(TITLE), lang.getType());
	}

	public String getTitle()
	{
		return (String) super.get(TITLE);
	}

	public void setTitle(String title)
	{
		super.put(TITLE, title);
	}

	public String getActrtTitle()
	{
		return (String) super.get(ACTRTTITLE);
	}

	public void setActrtTitle(String actrtTitle)
	{
		super.put(ACTRTTITLE, actrtTitle);
	}

	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String desc)
	{
		super.put(DESCRIPTION, desc);
	}

	public ProcessStatusEnum getStatus()
	{
		return ProcessStatusEnum.valueOf((String) super.get(STATUS));
	}

	public void setStatus(ProcessStatusEnum status)
	{
		super.put(STATUS, status.name());
	}

	public String getProcessDefGuid()
	{
		return (String) super.get(PROCDEF_GUID);
	}

	public void setProcessDefGuid(String guid)
	{
		super.put(PROCDEF_GUID, guid);
	}

	public String getFromInstance()
	{
		return (String) super.get(FROMINSTANCE);
	}

	public void setFromInstance(String fromInstance)
	{
		super.put(FROMINSTANCE, fromInstance);
	}

	public Boolean isDeleted()
	{
		return BooleanUtils.getBooleanByYN((String) super.get(IS_DELETE));
	}

	public void setDeleted(Boolean isDeleted)
	{
		super.put(IS_DELETE, BooleanUtils.getBooleanStringYN(isDeleted));
	}

	public Boolean isFinished()
	{
		if (super.get(IS_FINISH) != null)
		{
			return BooleanUtils.getBooleanByYN((String) super.get(IS_FINISH));
		}
		else
		{
			return Boolean.FALSE;
		}
	}

	public void setFinished(Boolean isFinished)
	{
		super.put(IS_FINISH, BooleanUtils.getBooleanStringYN(isFinished));
	}

	public Date getDeleteTime()
	{
		return (Date) super.get(DELETE_TIME);
	}

	public Date getFinishTime()
	{
		return (Date) super.get(FINISH_TIME);
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return (String) super.get(PARENT_GUID);
	}

	public void setParentGuid(String parentGuid)
	{
		super.put(PARENT_GUID, parentGuid);
	}

	/**
	 * @return the parentGuid
	 */
	public String getActrtGuid()
	{
		return (String) super.get(ACTRT_GUID);
	}

	public void setActrtGuid(String actrtGuid)
	{
		super.put(ACTRT_GUID, actrtGuid);
	}

	/**
	 * @return the wfTemplateGuid
	 */
	public String getWFTemplateGuid()
	{
		return (String) super.get(WFTEMPLATEGUID);
	}

	public void setWFTemplateGuid(String wfTemplateGuid)
	{
		super.put(WFTEMPLATEGUID, wfTemplateGuid);
	}

	/**
	 * @return the wfTemplateName
	 */
	public String getWFTemplateName()
	{
		return (String) super.get(WFTEMPLATENAME);
	}

	public void setWFTemplateName(String wfTemplateName)
	{
		super.put(WFTEMPLATENAME, wfTemplateName);
	}

	public String getWFTemplateTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) super.get(WFTEMPLATETITLE), lang.getType());
	}

	public String getWFTemplateTitle()
	{
		return (String) super.get(WFTEMPLATETITLE);
	}

	public void setWFTemplateTitle(String title)
	{
		super.put(WFTEMPLATETITLE, title);
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME,name);
	}


}
