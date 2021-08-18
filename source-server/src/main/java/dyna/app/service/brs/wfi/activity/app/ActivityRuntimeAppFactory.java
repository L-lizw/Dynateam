package dyna.app.service.brs.wfi.activity.app;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.application.*;
import dyna.common.systemenum.WorkflowActivityType;

import java.util.HashMap;
import java.util.Map;

public class ActivityRuntimeAppFactory extends AbstractServiceStub<WFIImpl>
{
	private final Map<WorkflowActivityType, ActivityRuntimeApplication> APP_MAP = new HashMap<WorkflowActivityType, ActivityRuntimeApplication>();

	public ActivityRuntimeAppFactory()
	{
		this.addActivityApplication(WorkflowActivityType.END, new EndActivityApplication());
		this.addActivityApplication(WorkflowActivityType.BEGIN, new BeginActivityApplication());
		this.addActivityApplication(WorkflowActivityType.APPLICATION, new ApplyActivityApplication());
		this.addActivityApplication(WorkflowActivityType.MANUAL, new ManualActivityApplication());
		this.addActivityApplication(WorkflowActivityType.NOTIFY, new NotifyActivityApplication());
		this.addActivityApplication(WorkflowActivityType.ROUTE, new RouteActivityApplication());
		this.addActivityApplication(WorkflowActivityType.SUB_PROCESS, new SubProcessActivityApplication());
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
