package dyna.common.dto.template.wft;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.model.wf.template.WorkflowTemplate;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.systemenum.ProcessStatusEnum;

public class WorkflowActivityRuntimeData extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long		serialVersionUID					= -6177552909576222896L;

	private ProcessRuntime			processRuntime						= null;

	private ActivityRuntime			currentActivityRuntime				= null;

	private WorkflowTemplate	workflowTemplate				= null;

	private List<ActivityRuntime>	currentActvityList					= null;

	private List<ActivityRuntime>	rejectedDestinedActivityRuntimeList	= null;

	private boolean					isMySelf							= false;

	private boolean					isAgent								= false;

	private boolean					isCreateOrCurrentPerformer			= false;

	private boolean					isProcessCreator					= false;

	private boolean					isSubFlow							= false;

	private String					subProcname							= null;

	public String getProcessDescription()
	{
		return this.processRuntime.getDescription();
	}

	public String getProcessParentGuid()
	{
		return this.processRuntime.getParentGuid();
	}

	public ProcessStatusEnum getProcessStatus()
	{
		return this.processRuntime.getStatus();
	}

	public boolean isPrcessOnHold()
	{
		return ProcessStatusEnum.ONHOLD == this.getProcessStatus();
	}

	public boolean isFCreate()
	{
		return ProcessStatusEnum.CREATED == this.getProcessStatus();
	}

	public boolean isCreate()
	{
		return this.isPrcessOnHold() || this.isFCreate();
	}

	public ProcessRuntime getProcessRuntime()
	{
		return processRuntime;
	}

	public void setProcessRuntime(ProcessRuntime processRuntime)
	{
		this.processRuntime = processRuntime;
	}

	public ActivityRuntime getCurrentActivityRuntime()
	{
		return currentActivityRuntime;
	}

	public void setCurrentActivityRuntime(ActivityRuntime currentActivityRuntime)
	{
		this.currentActivityRuntime = currentActivityRuntime;
	}

	public boolean isProcessCreator()
	{
		return isProcessCreator;
	}

	public void setProcessCreator(boolean isProcessCreator)
	{
		this.isProcessCreator = isProcessCreator;
	}

	public WorkflowTemplate getWorkflowTemplate()
	{
		return workflowTemplate == null ? new WorkflowTemplate() : workflowTemplate;
	}

	public void setWorkflowTemplate(WorkflowTemplate workflowTemplate)
	{
		this.workflowTemplate = workflowTemplate;
	}

	public List<ActivityRuntime> getCurrentActvityList()
	{
		return currentActvityList;
	}

	public void setCurrentActvityList(List<ActivityRuntime> currentActvityList)
	{
		this.currentActvityList = currentActvityList;
	}

	public List<ActivityRuntime> getRejectedDestinedActivityRuntimeList()
	{
		return rejectedDestinedActivityRuntimeList;
	}

	public void setRejectedDestinedActivityRuntimeList(List<ActivityRuntime> rejectedDestinedActivityRuntimeList)
	{
		this.rejectedDestinedActivityRuntimeList = rejectedDestinedActivityRuntimeList;
	}

	public boolean isMySelf()
	{
		return isMySelf;
	}

	public void setMySelf(boolean isMySelf)
	{
		this.isMySelf = isMySelf;
	}

	public boolean isAgent()
	{
		return isAgent;
	}

	public void setAgent(boolean isAgent)
	{
		this.isAgent = isAgent;
	}

	public boolean isCreateOrCurrentPerformer()
	{
		return isCreateOrCurrentPerformer;
	}

	public void setCreateOrCurrentPerformer(boolean isCreateOrCurrentPerformer)
	{
		this.isCreateOrCurrentPerformer = isCreateOrCurrentPerformer;
	}

	public boolean isSubFlow()
	{
		return isSubFlow;
	}

	public void setSubFlow(boolean isSubFlow)
	{
		this.isSubFlow = isSubFlow;
	}

	public String getSubProcname()
	{
		return subProcname;
	}

	public void setSubProcname(String subProcname)
	{
		this.subProcname = subProcname;
	}
}
