/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ActivityRuntime 工作流程活动
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ActRuntimeModeEnum;
import dyna.common.systemenum.DecisionEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.PerformerTypeEnum;
import dyna.common.systemenum.SubProcessTypeEnum;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * 工作流程活动
 * 
 * @author Wanglei
 * 
 */
public class ActivityRuntime extends SystemObjectImpl implements SystemObject
{
	private List<Performer>		performerList		= null;
	private static final long	serialVersionUID	= 1L;

	public static final String	TITLE				= "TITLE";
	public static final String	DESCRIPTION			= "DESCRIPTION";
	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	ACT_MODE			= "ACTMODE";
	public static final String	ACT_TYPE			= "ACTTYPE";
	public static final String	DEADLINE			= "DEADLINE";
	public static final String	APP_NAME			= "APPNAME";
	public static final String	IS_FINISH			= "ISFINISH";
	public static final String	FINISH_TIME			= "FINISHTIME";
	public static final String	IS_BLOCK			= "ISBLOCK";
	public static final String	MIN_SUBPRO			= "MINSUBPRO";
	public static final String	MAX_SUBPRO			= "MAXSUBPRO";
	public static final String	GATE				= "GATE";
	public static final String	SUB_PROC_NAME		= "SUBPROCNAME";
	public static final String	COMMENTS			= "COMMENTS";
	public static final String	PERF_NAME			= "PERFNAME";
	public static final String	DECIDE				= "DECIDE";
	public static final String	CHANGEPHASE_STATUS	= "CHANGEPHASESTATUS";

	public static final String	STARTTIME			= "STARTTIME";

	public static final String	START_NUMBER		= "STARTNUMBER";

	public static final String	PERF_TYPE			= "PERFTYPE";
	public static final String	PERF_GUID			= "PERFGUID";

	// 应用层处理
	public static final String	SEQUENCE			= "DATASEQ";
	public static final String	IS_HISTORY			= "ISHISTORY";
	public static final String	ACTRTNAME			= "ACTRTNAME";

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) super.get(TITLE), lang.getType());
	}

	@Override
	public String getName()
	{
		return (String) super.get(ACTRTNAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(ACTRTNAME, name);
	}

	public String getActrtName()
	{
		return this.getName();
	}

	public void setActrtName(String actrtName)
	{
		this.setName(actrtName);
	}

	public String getTitle()
	{
		return (String) super.get(TITLE);
	}

	public void setTitle(String title)
	{
		super.put(TITLE, title);
	}

	public String getComments()
	{
		return (String) super.get(COMMENTS);
	}

	public void setDecide(DecisionEnum decision)
	{
		if (decision == null)
		{
			this.put(DECIDE, null);
		}
		else
		{
			this.put(DECIDE, decision.toString());
		}
	}

	public DecisionEnum getDecide()
	{
		if (this.get(DECIDE) == null)
		{
			if (this.isFinished())
			{
				return DecisionEnum.ACCEPT;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return DecisionEnum.valueOf((String) this.get(DECIDE));
		}
	}

	public String getPerfName()
	{
		return (String) super.get(PERF_NAME);
	}

	public Integer getGate()
	{
		return super.get(GATE) == null ? -1 : ((Number) super.get(GATE)).intValue();
	}

	public void setGate(Integer gate)
	{
		super.put(GATE, BigDecimal.valueOf(gate));
	}

	public ActRuntimeModeEnum getActMode()
	{
		return ActRuntimeModeEnum.valueOf((String) this.get(ACT_MODE));
	}

	public void setActMode(ActRuntimeModeEnum mode)
	{
		this.put(ACT_MODE, mode.name());
	}

	public WorkflowActivityType getActType()
	{
		return WorkflowActivityType.valueOf((String) this.get(ACT_TYPE));
	}

	public void setActType(WorkflowActivityType type)
	{
		this.put(ACT_TYPE, type.name());
	}

	// public ChangePhaseActivetyType getChangePhaseStatus()
	// {
	// if (this.get(CHANGEPHASE_STATUS) == null)
	// {
	// return null;
	// }
	// return ChangePhaseActivetyType.valueOf((String) this.get(CHANGEPHASE_STATUS));
	// }

	// public void setChangePhaseStatus(ChangePhaseActivetyType changePhaseType)
	// {
	// this.put(CHANGEPHASE_STATUS, changePhaseType.name());
	// }

	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String desc)
	{
		super.put(DESCRIPTION, desc);
	}

	public String getSubProcName()
	{
		return (String) super.get(SUB_PROC_NAME);
	}

	public void setSubProcName(String subProcName)
	{
		super.put(SUB_PROC_NAME, subProcName);
	}

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public Boolean isFinished()
	{
		return BooleanUtils.getBooleanByYN((String) super.get(IS_FINISH));
	}

	public void setFinished(Boolean isFinished)
	{
		super.put(IS_FINISH, BooleanUtils.getBooleanStringYN(isFinished));
	}

	// public Boolean isAllPerformer()
	// {
	// // 0 or 1 and
	// if (super.get(IS_ALL_PERFORMER) == null)
	// {
	// return false;
	// }
	// return BooleanUtils.getBooleanBy10((String) super.get(IS_ALL_PERFORMER));
	// }
	//
	// public void setAllPerformer(Boolean isAllPerformer)
	// {
	// super.put(IS_ALL_PERFORMER, BooleanUtils.getBooleanString10(isAllPerformer));
	// }

	public SubProcessTypeEnum getIsBlock()
	{
		return SubProcessTypeEnum.valueOf((String) this.get(IS_BLOCK));
	}

	public void setBlock(SubProcessTypeEnum isBlock)
	{
		this.put(IS_BLOCK, isBlock.name());
	}

	public Date getFinishTime()
	{
		return (Date) super.get(FINISH_TIME);
	}

	public void setFinishTime(Date finishTime)
	{
		super.put(FINISH_TIME, finishTime);
	}

	public Date getDeadline()
	{
		return (Date) super.get(DEADLINE);
	}

	public void setDeadline(Date deadline)
	{
		super.put(DEADLINE, deadline);
	}

	public String getApplicationName()
	{
		return (String) this.get(APP_NAME);
	}

	public void setApplicationName(String appId)
	{
		this.put(APP_NAME, appId);
	}

	public Integer getMinSubPro()
	{
		return super.get(MIN_SUBPRO) == null ? 1 : ((Number) super.get(MIN_SUBPRO)).intValue();
	}

	public void setMinSubPro(Integer minSubPro)
	{
		super.put(MIN_SUBPRO, BigDecimal.valueOf(minSubPro));
	}

	public Integer getMaxSubPro()
	{
		return super.get(MAX_SUBPRO) == null ? 1 : ((Number) super.get(MAX_SUBPRO)).intValue();
	}

	public void setMaxSubPro(Integer maxSubPro)
	{
		super.put(MAX_SUBPRO, BigDecimal.valueOf(maxSubPro));
	}

	public Date getStartTime()
	{
		return (Date) this.get(STARTTIME);
	}

	public void setStartTime(Date startTime)
	{
		this.put(STARTTIME, startTime);
	}

	public void setStartNumber(int startNumber)
	{
		super.put(START_NUMBER, new BigDecimal(startNumber));
	}

	public Integer getStartNumber()
	{
		if (super.get(START_NUMBER) == null)
		{
			return 0;
		}
		return ((Number) super.get(START_NUMBER)).intValue();
	}

	public void setSequence(int sequence)
	{
		super.put(SEQUENCE, sequence);
	}

	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	public List<Performer> getPerformerList()
	{
		return this.performerList;
	}

	public void setPerformerList(List<Performer> performerList)
	{
		this.performerList = performerList;
	}

	public void setHistory(boolean isHistory)
	{
		this.put(IS_HISTORY, isHistory);
	}

	public boolean isHistory()
	{
		if (super.get(IS_HISTORY) == null)
		{
			return false;
		}

		return (Boolean) super.get(IS_HISTORY);
	}

	public PerformerTypeEnum getPerformerType()
	{
		if (this.get(PERF_TYPE) == null)
		{
			return PerformerTypeEnum.USER;
		}
		return PerformerTypeEnum.valueOf((String) this.get(PERF_TYPE));
	}

	public void setPerformerType(PerformerTypeEnum type)
	{
		this.put(PERF_TYPE, type.name());
	}

	public String getPerformerGuid()
	{
		return (String) super.get(PERF_GUID);
	}

	public void setPerformerGuid(String guid)
	{
		super.put(PERF_GUID, guid);
	}
}
