/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAppFactory
 * Wanglei 2010-11-11
 */
package dyna.app.service.brs.wfi.activity.app;

import java.util.HashMap;
import java.util.Map;

import dyna.app.service.brs.wfi.activity.app.impl.ActionAppImpl;
import dyna.app.service.brs.wfi.activity.app.impl.ChangPhaseAppImpl;
import dyna.app.service.brs.wfi.activity.app.impl.ChangStatusAppImpl;
import dyna.app.service.brs.wfi.activity.app.impl.LockAppImpl;
import dyna.app.service.brs.wfi.activity.app.impl.NotifyAppImpl;
import dyna.app.service.brs.wfi.activity.app.impl.UnlockAppImpl;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * @author Wanglei
 * 
 */
public class ProcAppFactory
{

	private static ProcAppFactory		factory	= null;

	private final Map<String, ProcApp>	APP_MAP	= new HashMap<String, ProcApp>();

	public static ProcAppFactory getProcAppFactory()
	{
		if (factory == null)
		{
			factory = new ProcAppFactory();
		}
		return factory;
	}

	protected ProcAppFactory()
	{
		this.addProcApp(WorkflowApplicationType.CHANGE_PHASE.name(), new ChangPhaseAppImpl());
		this.addProcApp(WorkflowApplicationType.CHANGE_STATUS.name(), new ChangStatusAppImpl());
		this.addProcApp(WorkflowApplicationType.LOCK.name(), new LockAppImpl());
		this.addProcApp(WorkflowApplicationType.UNLOCK.name(), new UnlockAppImpl());
		this.addProcApp(WorkflowApplicationType.NOTIFY.name(), new NotifyAppImpl());
		this.addProcApp(WorkflowApplicationType.ACTION.name(), new ActionAppImpl());
	}

	public ProcApp getProcApp(String appName)
	{
		return this.APP_MAP.get(appName);
	}

	public void addProcApp(String appName, ProcApp procApp)
	{
		this.APP_MAP.put(appName, procApp);
	}
}
