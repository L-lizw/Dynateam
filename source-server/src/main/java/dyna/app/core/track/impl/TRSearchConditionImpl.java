/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TRSearchConditionImpl
 * Wanglei 2011-11-14
 */
package dyna.app.core.track.impl;

import dyna.app.core.track.Tracker;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 *
 */
public class TRSearchConditionImpl extends DefaultTrackerRendererImpl
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.impl.DefaultTrackerRendererImpl#getHandledObject(dyna.app.core.track.Tracker)
	 */
	@Override
	public String getHandledObject(Tracker tracker)
	{
		return this.renderSearchCondition(this.getSearchCondition(tracker));
	}

	protected SearchCondition getSearchCondition(Tracker tracker)
	{
		Object[] params = tracker.getParameters();
		if (params == null || params.length == 0)
		{
			return null;
		}

		for (Object object : params)
		{
			if (object instanceof SearchCondition)
			{
				return (SearchCondition) object;
			}
		}
		return null;
	}

	protected String renderSearchCondition(SearchCondition searchCondition)
	{
		if (searchCondition == null)
		{
			return null;
		}
		String retStr = "";
		ObjectGuid objectGuid = searchCondition.getObjectGuid();
		if (objectGuid != null && !StringUtils.isNullString(objectGuid.getClassName()))
		{
			retStr += objectGuid.getClassName();
		}
		Folder folder = searchCondition.getFolder();
		if (folder != null)
		{
			if (!StringUtils.isNullString(retStr))
			{
				retStr += "|";
			}
			if (folder.getLevel() == 1)
			{
				retStr = folder.getType().name() + ": " + folder.getName();
			}
			else
			{
				retStr = folder.getType().name() + ": " + folder.getHierarchy();
			}
		}

		return StringUtils.isNullString(retStr) ? searchCondition.toString() : retStr;
	}
}
