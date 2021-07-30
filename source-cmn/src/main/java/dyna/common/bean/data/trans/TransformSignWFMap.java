package dyna.common.bean.data.trans;

import dyna.common.bean.data.SystemObjectImpl;

public class TransformSignWFMap extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6564841658649248759L;

	public static String		SIGN_PARAM_GUID		= "SIGNPARAMGUID";

	public static String		WORKFLOW_NAME		= "WORKFLOWNAME";

	public static String		ACTIVITY_NAME		= "ACTIVITYNAME";

	public static String		WORKFLOW_TITLE		= "WORKFLOWTITLE";

	public static String		ACTIVITY_TITLE		= "ACTIVITYTITLE";

	public static String		IS_DELETE			= "ISDELETE";

	public String getSignParamGuid()
	{
		return (String) this.get(SIGN_PARAM_GUID);
	}

	public void setSignParamGuid(String signParamGuid)
	{
		this.put(SIGN_PARAM_GUID, signParamGuid);
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
