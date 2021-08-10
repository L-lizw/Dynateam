package dyna.app.service.brs.wfi.activity.app;

import java.util.HashMap;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.application.ApplyActivityApplication;
import dyna.app.service.brs.wfi.activity.app.application.BeginActivityApplication;
import dyna.app.service.brs.wfi.activity.app.application.EndActivityApplication;
import dyna.app.service.brs.wfi.activity.app.application.ManualActivityApplication;
import dyna.app.service.brs.wfi.activity.app.application.NotifyActivityApplication;
import dyna.app.service.brs.wfi.activity.app.application.RouteActivityApplication;
import dyna.app.service.brs.wfi.activity.app.application.SubProcessActivityApplication;
import dyna.common.systemenum.WorkflowActivityType;

public class ActivityRuntimeAppFactory extends AbstractServiceStub<WFIImpl>
{
	private final Map<WorkflowActivityType, ActivityRuntimeApplication> APP_MAP = new HashMap<WorkflowActivityType, ActivityRuntimeApplication>();

	public ActivityRuntimeAppFactory(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.addActivityApplication(WorkflowActivityType.END, new EndActivityApplication(service));
		this.addActivityApplication(WorkflowActivityType.BEGIN, new BeginActivityApplication(service));
		this.addActivityApplication(WorkflowActivityType.APPLICATION, new ApplyActivityApplication(service));
		this.addActivityApplication(WorkflowActivityType.MANUAL, new ManualActivityApplication(service));
		this.addActivityApplication(WorkflowActivityType.NOTIFY, new NotifyActivityApplication(service));
		this.addActivityApplication(WorkflowActivityType.ROUTE, new RouteActivityApplication(service));
		this.addActivityApplication(WorkflowActivityType.SUB_PROCESS, new SubProcessActivityApplication(service));
	}

	public ActivityRuntimeApplication getActivityApplication(WorkflowActivityType type)
	{
		return this.APP_MAP.get(type);
	}

	public void addActivityApplication(WorkflowActivityType type, ActivityRuntimeApplication actrtApp)
	{
		this.APP_MAP.put(type, actrtApp);
	}
}
