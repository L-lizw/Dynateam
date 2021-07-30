/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowEvent
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

import java.util.Iterator;

import dyna.common.annotation.Cache;
import dyna.common.bean.model.AbstractScript;
import dyna.common.bean.model.EventScript;
import dyna.common.bean.model.Script;
import dyna.common.systemenum.ScriptTypeEnum;
import dyna.common.util.SetUtils;

/**
 * 工作流事件
 * 
 * @author Jiagang
 * 
 */
@Cache
public class WorkflowEventInfo extends AbstractScript implements EventScript
{
	private static final long	serialVersionUID	= -1394049540333838443L;

	public static final String	WFFK				= "WFFK";

	public static final String	ACTIONNAME			= "EVENTNAME";

	public WorkflowEventInfo()
	{
		this.setScriptType(ScriptTypeEnum.WFEVENT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.common.bean.model.Script#getName()
	 */
	@Override
	public String getName()
	{
		return (String) this.get(ACTIONNAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(ACTIONNAME, name);
	}

	public String getWffk()
	{
		return (String) this.get(WFFK);
	}

	public void setWffk(String wffk)
	{
		this.put(WFFK, wffk);
	}

	@Override
	public WorkflowEventInfo clone()
	{
		return (WorkflowEventInfo) super.clone();
	}

	public void removeChild(WorkflowEventInfo childInfo)
	{
		if (!SetUtils.isNullList(getChildren()))
		{
			Iterator<Script> it = this.getChildren().iterator();
			while (it.hasNext())
			{
				WorkflowEventInfo info = (WorkflowEventInfo) it.next();
				if (info.getGuid().equals(childInfo.getGuid()))
				{
					it.remove();
				}
			}
		}
	}
}
