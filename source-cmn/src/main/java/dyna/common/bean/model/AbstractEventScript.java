/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractEventScript
 * Wanglei 2011-7-13
 */
package dyna.common.bean.model;

import dyna.common.systemenum.EventTypeEnum;

/**
 * @author Wanglei
 *
 */
public abstract class AbstractEventScript extends AbstractScript implements EventScript
{

	private static final long	serialVersionUID	= -6756842252500277434L;

	private EventTypeEnum		eventType			= null;

	/* (non-Javadoc)
	 * @see dyna.common.bean.model.EventScript#getEventType()
	 */
	@Override
	public EventTypeEnum getEventType()
	{
		return this.eventType;
	}

	public void setEventType(EventTypeEnum eventType)
	{
		this.eventType = eventType;
	}

}
