/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InputObjectListActionImpl
 * Wanglei 2012-4-1
 */
package dyna.common.bean.data.input;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.dto.wf.ProcAttach;

/**
 * Search Result动作脚本的输入对象
 * 
 * @author Qiuxq
 * 
 */
public class InputObjectWrokflowEventImpl extends AbstractInputObject
{

	private static final long	serialVersionUID	= 5165617970259613697L;

	private String				processGuid			= null;

	public ProcAttach[] getAttachSettings()
	{
		return this.attachSettings;
	}

	public void setAttachSettings(ProcAttach[] attachSettings)
	{
		this.attachSettings = attachSettings;
	}

	public WorkflowTemplate getWorkflowTemplateInfo()
	{
		return this.workflowTemplateInfo;
	}

	public void setWorkflowTemplateInfo(WorkflowTemplate workflowTemplateInfo)
	{
		this.workflowTemplateInfo = workflowTemplateInfo;
	}

	public String getParentProcGuid()
	{
		return this.parentProcGuid;
	}

	public void setParentProcGuid(String parentProcGuid)
	{
		this.parentProcGuid = parentProcGuid;
	}

	public String getParentActGuid()
	{
		return this.parentActGuid;
	}

	public void setParentActGuid(String parentActGuid)
	{
		this.parentActGuid = parentActGuid;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	private String				processName				= null;

	private ProcAttach[]		attachSettings			= null;

	private WorkflowTemplate	workflowTemplateInfo	= null;

	private String				parentProcGuid			= null;

	private String				parentActGuid			= null;

	private String				description				= null;

	/**
	 * @return the processName
	 */
	public String getProcessName()
	{
		return this.processName;
	}

	/**
	 * 构造方法
	 * 
	 * @param className
	 *            Search Result的类的Name
	 * @param selectedBOMStructures
	 *            Search Result选择的对象
	 */
	public InputObjectWrokflowEventImpl(String processGuid, String processName)
	{
		this.processName = processName;
		this.processGuid = processGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.InputObject#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the processGuid
	 */
	public String getProcessGuid()
	{
		return this.processGuid;
	}

}
