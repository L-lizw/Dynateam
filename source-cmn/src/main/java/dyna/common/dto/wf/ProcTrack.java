/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcTrack 工作流程活动意见追溯
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.wf.ProcTrackCommMapper;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * 工作流程活动意见追溯
 * 
 * @author Wanglei
 * 
 */
@EntryMapper(ProcTrackCommMapper.class)
public class ProcTrack extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;
	public static final String	PROCTRACKTABLE		= "wf_track";

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	ACTRT_GUID			= "ACTRTGUID";
	public static final String	ACTRT_TITLE			= "ACTRTTITLE";
	public static final String	DEADLINE			= "DEADLINE";
	public static final String	DECIDE				= "DECIDE";
	public static final String	COMMENTS			= "COMMENTS";
	public static final String	FINISH_TIME			= "FINISHTIME";
	public static final String	PERF_GUID			= "PERFGUID";
	public static final String	PERF_NAME			= "PERFNAME";
	public static final String	CREATE_USERNAME		= "CREATEUSERNAME";

	public static final String	START_NUMBER		= "STARTNUMBER";
	public static final String	HAS_FIEL			= "HASFILE";
	public static final String	AGENTGUID			= "AGENTGUID";
	public static final String	AGENTNAME			= "AGENTNAME";

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) super.get(ACTRT_TITLE), lang.getType());
	}

	public DecisionEnum getDecide()
	{
		return DecisionEnum.valueOf((String) this.get(DECIDE));
	}

	public void setDecide(DecisionEnum decide)
	{
		this.put(DECIDE, decide.name());
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

	public Date getDeadline()
	{
		return (Date) super.get(DEADLINE);
	}

	public void setDeadline(Date deadline)
	{
		super.put(DEADLINE, deadline);
	}

	public String getPerformerGuid()
	{
		return (String) super.get(PERF_GUID);
	}

	public void setPerformerGuid(String guid)
	{
		super.put(PERF_GUID, guid);
	}

	public String getPerformerName()
	{
		return (String) super.get(PERF_NAME);
	}

	public void setPerformerName(String name)
	{
		super.put(PERF_NAME, name);
	}

	public String getCreateUsernName()
	{
		return (String) this.get(CREATE_USERNAME);
	}

	public String getComments()
	{
		return (String) super.get(COMMENTS);
	}

	public void setComments(String comments)
	{
		super.put(COMMENTS, comments);
	}

	public String getAgent()
	{
		return (String) super.get(AGENTGUID);
	}

	public void setAgent(String agentGuid)
	{
		super.put(AGENTGUID, agentGuid);
	}

	public String getAgentName()
	{
		return (String) super.get(AGENTNAME);
	}

	public void setAgentName(String agentName)
	{
		super.put(AGENTNAME, agentName);
	}

	public Date getFinishTime()
	{
		return (Date) this.get(FINISH_TIME);
	}

	public void setStartNumber(int startNumber)
	{
		super.put(START_NUMBER, startNumber);
	}

	public int getStartNumber()
	{
		if (super.get(START_NUMBER) == null)
		{
			return 0;
		}

		return ((Number) super.get(START_NUMBER)).intValue();
	}

	public boolean hasFile()
	{
		if (StringUtils.isNullString((String) this.get(HAS_FIEL)))
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(HAS_FIEL));
	}
}
