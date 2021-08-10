/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EventAppFactory
 * WangLH 2012-06-26
 */
package dyna.app.service.brs.ppms.app;

import java.util.HashMap;
import java.util.Map;

import dyna.common.systemenum.ppms.WarningEventEnum;

/**
 * @author WangLHB
 * 
 */
public class EventAppFactory
{

	private static EventAppFactory		factory	= null;

	private final Map<String, EventApp>	APP_MAP	= new HashMap<String, EventApp>();

	public static EventAppFactory getEventAppFactory()
	{
		if (factory == null)
		{
			factory = new EventAppFactory();
		}
		return factory;
	}

	protected EventAppFactory()
	{
		this.addEventApp(WarningEventEnum.P_COMPLETE_DELAY.name(), new PCompleteDelayImpl());
		this.addEventApp(WarningEventEnum.P_START_DELAY.name(), new PStartDelayImpl());
		this.addEventApp(WarningEventEnum.P_START_BEFORE.name(), new PStartBeforeImpl());
		this.addEventApp(WarningEventEnum.P_COMPLETE_DELAY_PERCENT.name(), new PCompleteDelayPercentImpl());
		this.addEventApp(WarningEventEnum.P_PROGRESS_RISK.name(), new PprogressRiskImpl());

		this.addEventApp(WarningEventEnum.T_COMPLETE_DELAY.name(), new TCompleteDelayImpl());
		this.addEventApp(WarningEventEnum.T_START_DELAY.name(), new TStartDelayImpl());
		this.addEventApp(WarningEventEnum.T_START_BEFORE.name(), new TStartBeforeImpl());
		this.addEventApp(WarningEventEnum.T_COMPLETE_DELAY_PERCENT.name(), new TCompleteDelayPercentImpl());
		this.addEventApp(WarningEventEnum.T_PROGRESS_RISK.name(), new TprogressRiskImpl());
		this.addEventApp(WarningEventEnum.T_COMPLETE_BEFORE.name(), new TCompleteBeforeImpl());

	}

	public EventApp getEventApp(String appName)
	{
		return this.APP_MAP.get(appName);
	}

	public void addEventApp(String appName, EventApp EventApp)
	{
		this.APP_MAP.put(appName, EventApp);
	}
}
