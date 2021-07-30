/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EmailServer
 * WangLHB Feb 22, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

/**
 * @author WangLHB
 * 
 */
public class WorkFlowPerference extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public static final String	IS_TRACK_OPEN		= "ISTRACKOPEN";
	public static final String	IS_ATTACH_OPEN		= "ISATTACHOPEN";
	public static final String	IS_ACTIVITY_OPEN	= "ISACTIVITYOPEN";

	/**
	 * @return the isTrackOpen
	 */
	public boolean isTrackOpen()
	{
		if (this.get(IS_TRACK_OPEN) == null)
		{
			return true;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_TRACK_OPEN));
	}

	public void setTrackOpen(boolean isTrackOpen)
	{
		this.put(IS_TRACK_OPEN, BooleanUtils.getBooleanStringYN(isTrackOpen));
	}

	/**
	 * @return the isAttachOpen
	 */
	public boolean isAttachOpen()
	{
		if (this.get(IS_ATTACH_OPEN) == null)
		{
			return true;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_ATTACH_OPEN));
	}

	public void setAttachOpen(boolean isAttachOpen)
	{
		this.put(IS_ATTACH_OPEN, BooleanUtils.getBooleanStringYN(isAttachOpen));
	}

	/**
	 * @return the isAttachOpen
	 */
	public boolean isActivityOpen()
	{
		if (this.get(IS_ACTIVITY_OPEN) == null)
		{
			return true;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_ACTIVITY_OPEN));
	}

	public void setActivityOpen(boolean isActivityOpen)
	{
		this.put(IS_ACTIVITY_OPEN, BooleanUtils.getBooleanStringYN(isActivityOpen));
	}
}
