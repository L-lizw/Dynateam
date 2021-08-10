package dyna.app.server.context;

import java.util.Date;
import java.util.List;

import org.acegisecurity.Authentication;
import org.springframework.remoting.support.RemoteInvocation;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.common.log.DynaLogger;
import dyna.common.util.DateFormat;

public class RMIInvokeCheck extends AbstractScheduledTask
{
	private ApplicationServerContext sc = null;

	public RMIInvokeCheck(ApplicationServerContext sc)
	{
		this.sc = sc;
	}

	@Override
	public void run()
	{
		List<List<Object>> list = sc.listRMIRemoteInvocation();
		for (List<Object> row : list)
		{
			RemoteInvocation invocation = (RemoteInvocation) row.get(0);
			Authentication authentication = (Authentication) invocation.getAttribute("authentication");
			DynaLogger.info("App Method Invoke Loop");
			DynaLogger.info("APP Start:" + DateFormat.format((Date) row.get(2), "HH:mm:ss,SSS") + "\tcredential:" + authentication.getCredentials() + "\tRMI clientIP:" + row.get(1)
					+ "\tmethod:" + invocation.getMethodName());
			if (invocation.getArguments() != null)
			{
				for (Object obj : invocation.getArguments())
				{
					DynaLogger.info("Argument:" + obj);
				}
			}
		}
	}

}
