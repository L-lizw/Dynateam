/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataService服务的实现
 * XiaSheng 2010-3-17
 * JinagHL 2011-5-5
 */
package dyna.data.service.wf;

import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

import java.util.List;

/**
 * DataService服务的实现
 * 
 * @author XiaSheng
 */
public class WorkFlowServiceImpl extends DataRuleService implements WorkFlowService
{
	private WorkflowStub			workflowStub			= null;

	public WorkFlowServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	protected WorkflowStub getWorkflowStub()
	{
		if (this.workflowStub == null)
		{
			this.workflowStub = new WorkflowStub(this.serviceContext, this);
		}
		return this.workflowStub;
	}

	@Override
	public void deleteUnExistsAttach(String procRtGuid)
	{
		this.getWorkflowStub().deleteUnExistsAttach(procRtGuid);
	}

	@Override
	public List<ProcAttach> listRevisionInWF(String masterGuid, String classGuid)
	{
		return this.getWorkflowStub().listAllRevisionInWF(masterGuid, classGuid);
	}

	@Override
	public ActivityRuntime getActivityRuntime(String actRtGuid)
	{
		return this.getWorkflowStub().getActivityRuntime(actRtGuid);
	}

	@Override
	public ActivityRuntime getBeginActivityRuntime(String procRtGuid)
	{
		return this.getWorkflowStub().getActivityRuntimeByType(procRtGuid, WorkflowActivityType.BEGIN);
	}

	@Override
	public ActivityRuntime getEndActivityRuntime(String procRtGuid)
	{
		return this.getWorkflowStub().getActivityRuntimeByType(procRtGuid, WorkflowActivityType.END);
	}

	@Override
	public void deleteProcess(ProcessRuntime processRuntime) throws DynaDataException
	{
		this.getWorkflowStub().deleteProcess(processRuntime);
	}
}
