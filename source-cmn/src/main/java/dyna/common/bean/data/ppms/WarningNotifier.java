/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WarningNotifier
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ppms.WarningNotifiterEnum;

/**
 * 预警通知人员
 * 
 * @author wangweixia
 * 
 */
public class WarningNotifier extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 2505662070539320221L;
	// 预警guid
	public static final String	WARNINGGUID			= "WARNINGGUID";

	// 类型
	public static final String	NOTIFIERTYPE		= "NOTIFIERTYPE";

	// 类型值:当类型非项目角色/group/rig/user时，类型值为空
	public static final String	NOTIFIERGUID		= "NOTIFIERGUID";

	/**
	 * @return the warningguid
	 */
	public String getWarningGuid()
	{
		return (String) super.get(WARNINGGUID);
	}

	public void setWarningGuid(String warningGuid)
	{
		super.put(WARNINGGUID, warningGuid);
	}

	/**
	 * @return the notifiertype
	 */
	public WarningNotifiterEnum getNotifierType()
	{
		return WarningNotifiterEnum.valueOf((String) super.get(NOTIFIERTYPE));
	}

	public void setNotifierType(WarningNotifiterEnum notifierType)
	{
		super.put(NOTIFIERTYPE, notifierType.name());
	}

	/**
	 * @return the notifierguid
	 */
	public String getNotifierGuid()
	{
		return (String) super.get(NOTIFIERGUID);
	}

	public void setNotifierGuid(String notifierGuid)
	{
		super.put(NOTIFIERGUID, notifierGuid);
	}

	@Override
	public String getName()
	{
		return (String) super.get("NOTIFIER");
	}

	@Override
	public void setName(String value)
	{
		super.put("NOTIFIER", value);
	}

}
