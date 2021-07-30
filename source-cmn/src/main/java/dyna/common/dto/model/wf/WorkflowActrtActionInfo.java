/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.dto.model.wf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.AbstractScript;
import dyna.common.bean.model.Script;
import dyna.common.systemenum.ScriptTypeEnum;
import dyna.common.util.SetUtils;

@Cache
public class WorkflowActrtActionInfo extends AbstractScript implements SystemObject
{
	private static final long	serialVersionUID	= -3699501028572128906L;

	public final static String	WFACTIVITYGUID		= "ACTFK";

	private boolean				isInherited			= false;

	private String				script				= null;

	private boolean				isBuiltin			= false;

	public WorkflowActrtActionInfo()
	{
		this.setScriptType(ScriptTypeEnum.WFACTACTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public WorkflowActrtActionInfo clone()
	{
		WorkflowActrtActionInfo info = (WorkflowActrtActionInfo) super.clone();
		List<Script> children = new ArrayList<Script>();
		List<Script> childList = this.getChildren();
		if (!SetUtils.isNullList(childList))
		{
			for (Script script : childList)
			{
				children.add(((WorkflowActrtActionInfo) script).clone());
			}
		}
		info.setChildren(children);
		return info;
	}

	/**
	 * @return the script
	 */
	@Override
	@Deprecated
	public String getScript()
	{
		return this.script;
	}

	/**
	 * @return the isBuiltin
	 */
	@Override
	public boolean isBuiltin()
	{
		return this.isBuiltin;
	}

	/**
	 * @return the isInherited
	 */
	@Override
	public boolean isInherited()
	{
		return this.isInherited;
	}

	/**
	 * @param isBuiltin
	 *            the isBuiltin to set
	 */
	@Override
	public void setBuiltin(boolean isBuiltin)
	{
		this.isBuiltin = isBuiltin;
	}

	/**
	 * @param isInherited
	 *            the isInherited to set
	 */
	@Override
	public void setInherited(boolean isInherited)
	{
		this.isInherited = isInherited;
	}

	/**
	 * @param script
	 *            the script to set
	 */
	@Override
	public void setScript(String script)
	{
		this.script = script;
	}

	public String getWfActivityGuid()
	{
		return (String) super.get(WFACTIVITYGUID);
	}

	public void setWfActivityGuid(String wfActrtguid)
	{
		super.put(WFACTIVITYGUID, wfActrtguid);
	}

	public void removeAction(WorkflowActrtActionInfo actionInfo)
	{
		if (!SetUtils.isNullList(getChildren()))
		{
			Iterator<Script> it = getChildren().iterator();
			while (it.hasNext())
			{
				WorkflowActrtActionInfo info = (WorkflowActrtActionInfo) it.next();
				if (info.getGuid().equals(actionInfo.getGuid()))
				{
					it.remove();
				}
			}
		}
	}
}
