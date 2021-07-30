/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractInputObject
 * Wanglei 2011-11-28
 */
package dyna.common.bean.data.input;

import dyna.common.bean.data.InputObject;
import dyna.common.context.ScriptContext;

/**
 * @author Wanglei
 *
 */
public abstract class AbstractInputObject implements InputObject
{

	private static final long	serialVersionUID	= -5932364665300271205L;

	private ScriptContext		scriptContext		= null;

	/* (non-Javadoc)
	 * @see dyna.common.bean.data.InputObject#getScriptContext()
	 */
	@Override
	public ScriptContext getScriptContext()
	{
		return this.scriptContext;
	}

	/* (non-Javadoc)
	 * @see dyna.common.bean.data.InputObject#setScriptContext(dyna.common.context.ScriptContext)
	 */
	@Override
	public void setScriptContext(ScriptContext context)
	{
		this.scriptContext = context;
	}


}
