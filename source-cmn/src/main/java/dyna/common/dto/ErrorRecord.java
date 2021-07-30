package dyna.common.dto;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.DynaObjectImpl;

/**
 * 主要用于存放从中间库
 * 查询出来的错误记录信息
 * @author dingmx
 *
 */
public class ErrorRecord extends DynaObjectImpl implements DynaObject
{
	private static final long	serialVersionUID	= -6497690492782841055L;

	/**
	 * 任务号
	 */
	private static final String				JOB_ID				    = "jobId";

	/**
	 * 任务类型
	 */
	private static final String				TASK_TYPE			    = "taskType";

	/**
	 * 企业编号
	 */
	private static final String				COMPANY_ID			    = "companyId";

	/**
	 * T100中间表
	 */
	private static final String				TEMP_TABLE			    = "tempTable";

	/**
	 * 主件料号
	 */
	private static final String				MASTER_ITEM_ID		    = "masterItemId";

	/**
	 * 元件料号
	 */
	private static final String				COMPONENT_ITEM_ID		= "componentItemId";

	/**
	 * 错误描述
	 */
	private static final String				ERROR_DESCRIPTION	    = "errorDescription";

	public String getJobId()
	{
		return (String) this.get(JOB_ID);
	}

	public void setJobId(String jobId)
	{
		this.put(JOB_ID, jobId);
	}

	public String getTaskType()
	{
		return (String) this.get(TASK_TYPE);
	}

	public void setTaskType(String taskType)
	{
		this.put(TASK_TYPE, taskType);
	}

	public String getCompanyId()
	{
		return (String) this.get(COMPANY_ID);
	}

	public void setCompanyId(String companyId)
	{
		this.put(COMPANY_ID, companyId);
	}

	public String getTempTable()
	{
		return (String) this.get(TEMP_TABLE);
	}

	public void setTempTable(String tempTable)
	{
		this.put(TEMP_TABLE, tempTable);
	}

	public String getMasterItemId()
	{
		return (String) this.get(MASTER_ITEM_ID);
	}

	public void setMasterItemId(String masterItemId)
	{
		this.put(MASTER_ITEM_ID, masterItemId);
	}

	public String getComponentItemId()
	{
		return (String) this.get(COMPONENT_ITEM_ID);
	}

	public void setComponentItemId(String componentItemId)
	{
		this.put(COMPONENT_ITEM_ID, componentItemId);
	}

	public String getErrorDescription()
	{
		return (String) this.get(ERROR_DESCRIPTION);
	}

	public void setErrorDescription(String errorDescription)
	{
		this.put(ERROR_DESCRIPTION, errorDescription);
	}
}
