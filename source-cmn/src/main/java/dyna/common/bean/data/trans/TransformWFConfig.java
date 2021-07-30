package dyna.common.bean.data.trans;

import dyna.common.bean.data.SystemObjectImpl;

public class TransformWFConfig extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID		= 2564958930201934572L;

	public static String		TRANSFORM_CONFIG_GUID	= "TRANSFORMCONFIGGUID";

	public static String		WORKFLOW_NAME			= "WORKFLOWNAME";

	public static String		WORKFLOW_TITLE			= "WORKFLOWTITLE";

	public static String		ACTIVITY_NAME			= "ACTIVITYNAME";

	public static String		ACTIVITY_TITLE			= "ACTIVITYTITLE";

	public static String		SIGNATURE_SOLUTION		= "SOLUTION";

	public static String		SIGNATURE_SOLUTIONNAME	= "SOLUTIONNAME";

	public static String		IS_DELETE				= "ISDELETE";

	public String getTransformConfigGuid()
	{
		return (String) this.get(TRANSFORM_CONFIG_GUID);
	}

	public void setTransformConfigGuid(String transformConfigGuid)
	{
		this.put(TRANSFORM_CONFIG_GUID, transformConfigGuid);
	}

	public String getSignatureSolution()
	{
		return (String) this.get(SIGNATURE_SOLUTION);
	}

	public void setSignatureSolution(String solution)
	{
		this.put(SIGNATURE_SOLUTION, solution);
	}

	public String getWorkflowName()
	{
		return (String) this.get(WORKFLOW_NAME);
	}

	public void setWorkflowName(String workflowName)
	{
		this.put(WORKFLOW_NAME, workflowName);
	}

	public String getActivityName()
	{
		return (String) this.get(ACTIVITY_NAME);
	}

	public void setActivityName(String activityName)
	{
		this.put(ACTIVITY_NAME, activityName);
	}

	public boolean isDelete()
	{
		if (this.get(IS_DELETE) == null)
		{
			return false;
		}
		return (Boolean) this.get(IS_DELETE);
	}

	public void setDelete(boolean isDelete)
	{
		this.put(IS_DELETE, isDelete);
	}

}
