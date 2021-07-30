/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowBeginActivity
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

/**
 * 工作流流程生效活动
 * 
 * @author WangLHB
 * 
 */
public class WorkflowEffectiveActivityInfo extends WorkflowActivityInfo
{
	private static final long serialVersionUID = -2083152007495205681L;

	public WorkflowEffectiveActivityInfo()
	{
		// this.setType(WorkflowActivityType.EFFECTIVE);
	}

	@Override
	public WorkflowEffectiveActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowEffectiveActivityInfo) super.clone();
	}
}
