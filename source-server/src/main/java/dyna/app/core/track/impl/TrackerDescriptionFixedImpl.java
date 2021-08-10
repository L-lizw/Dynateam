/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TrackerDescriptionFixedImpl
 * Wanglei 2011-11-11
 */
package dyna.app.core.track.impl;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.TrackerDescription;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 *
 */
public class TrackerDescriptionFixedImpl implements TrackerDescription
{

	private String	jobDesc	= null;

	protected TrackerDescriptionFixedImpl(String desc)
	{
		this.setJobDesc(desc);
	}

	public void setJobDesc(String desc)
	{
		this.jobDesc = desc;
	}

	@Override
	public String getDescription(Tracker tracker)
	{
		return StringUtils.convertNULLtoString(this.jobDesc);
		// if (this.jobDesc == null)
		// {
		// return "";
		// }
		// Signature signature = tracker.getSignature();
		// if (signature == null || signature instanceof ModuleSignature)
		// {
		// return this.jobDesc;
		// }
		//
		// UserSignature us = (UserSignature) signature;
		// LanguageEnum lang = us.getLanguageEnum();
		// String msr = tracker.getServiceContext().getServerContext().getNLSManager().getMSRString(this.jobDesc,
		// lang.getId());
		// return msr == null ? this.jobDesc : msr;
	}

}
