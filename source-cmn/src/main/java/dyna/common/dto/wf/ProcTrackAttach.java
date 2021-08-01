/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcTrackAttach
 * zhanghj 2011-3-30
 */
package dyna.common.dto.wf;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.wf.ProcTrackAttachMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * 
 * 附件意见表
 * 
 * @author zhanghj
 * 
 */
@EntryMapper(ProcTrackAttachMapper.class)
public class ProcTrackAttach extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 1L;

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	ACTRT_GUID			= "ACTRTGUID";
	public static final String	ACTRT_TITLE			= "ACTRTTITLE";
	public static final String	COMMENTS			= "COMMENTS";
	public static final String	FINISH_TIME			= "FINISHTIME";
	public static final String	PERF_GUID			= "PERFGUID";
	public static final String	CREATE_USERNAME		= "CREATEUSERNAME";
	public static final String	ATTACH_GUID			= "ATTACHGUID";
	public static final String	QUOTE_COMMENTS		= "QUOTECOMMENTS";

	public String getAttachGuid()
	{
		return (String) super.get(ATTACH_GUID);
	}

	public void setAttachGuid(String guid)
	{
		super.put(ATTACH_GUID, guid);
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) super.get(ACTRT_TITLE), lang.getType());
	}

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public String getActRuntimeTitle()
	{
		return (String) super.get(ACTRT_TITLE);
	}

	public void setActRuntimeTitle(String actrtTitle)
	{
		super.put(ACTRT_TITLE, actrtTitle);
	}

	public String getActRuntimeGuid()
	{
		return (String) super.get(ACTRT_GUID);
	}

	public void setActRuntimeGuid(String guid)
	{
		super.put(ACTRT_GUID, guid);
	}

	public String getPerformerGuid()
	{
		return (String) super.get(PERF_GUID);
	}

	public void setPerformerGuid(String guid)
	{
		super.put(PERF_GUID, guid);
	}

	public String getCreateUserName()
	{
		return (String) this.get(CREATE_USERNAME);
	}

	public void setCreateUserName(String createUserName)
	{
		super.put(CREATE_USER_NAME, createUserName);
	}

	public String getComments()
	{
		return (String) super.get(COMMENTS);
	}

	public void setComments(String comments)
	{
		super.put(COMMENTS, comments);
	}

	public Date getFinishTime()
	{
		return (Date) this.get(FINISH_TIME);
	}

	public static String getCreateUsername()
	{
		return CREATE_USERNAME;
	}

	public String getQuoteComments()
	{
		return (String) super.get(QUOTE_COMMENTS);
	}

	public void setQuoteComments(String quoteComments)
	{
		super.put(QUOTE_COMMENTS, quoteComments);
	}
}
