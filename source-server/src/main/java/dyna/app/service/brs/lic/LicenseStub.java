/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LicenseStub
 * Wanglei 2011-9-19
 */
package dyna.app.service.brs.lic;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.Version;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;

/**
 * 有license信息相关的分支
 * 
 * @author Wanglei
 * 
 */
public class LicenseStub extends AbstractServiceStub<LICImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected LicenseStub(ServiceContext context, LICImpl service)
	{
		super(context, service);
	}

	protected int[] getLicenseOccupiedNode() throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getLicenseDaemon().getLicenseInUse(this.stubService);
	}

	protected int[] getLicenseNode() throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getLicenseDaemon().getLicenseNode();
	}

	protected String getLicenseModules() throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getLicenseDaemon().getLicenseModules();
	}

	protected long[] getLicensePeriod() throws ServiceRequestException
	{
		return this.serviceContext.getServerContext().getLicenseDaemon().getLicensePeriod();
	}

	protected String getVersionInfo() throws ServiceRequestException
	{
		return Version.getVersionInfo();
	}

	/**
	 * @return
	 */
	public String getSystemIdentification()
	{
		String id=this.serviceContext.getServerContext().getLicenseDaemon().getSystemIdentification();
		boolean isVM=this.serviceContext.getServerContext().getLicenseDaemon().isVM();
		if (StringUtils.isNullString(id))
		{
			if (isVM)
			{
				return "Unable to connect to Guard Service";
			}
			else
			{
				return id;
			}
		}
		else if (isVM)
		{
			return id +" (Guard Service Id)";
		}
		else
		{
			return id +" (Mac Address)";
		}
	}
}
