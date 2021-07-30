package dyna.common.bean.data.trans;

import java.math.BigDecimal;
import java.util.Date;

import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.JobStatus;
import dyna.common.util.BooleanUtils;

public class TransformQueue extends SystemObjectImpl
{

	private static final long	serialVersionUID				= -9137180160333458537L;

	// 编号
	public static String		ID								= "MD_ID";
	public static String		NAME							= "MD_NAME";
	// 执行人
	public static String		OWNERUSER_GUID					= "OWNERUSERGUID";
	// 执行人
	public static String		OWNERUSER_NAME					= "OWNERUSERNAME";
	// 状态
	public static String		JOB_STATUS						= "JOBSTATUS";
	// 对应的CLASS
	public static String		EXECUTOR_CLASS					= "EXECUTORCLASS";
	// 是否单线程
	public static String		IS_SINGLETH_READ				= "ISSINGLETHREAD";
	// 优先级
	public static String		PRIORITY						= "PRIORITY";
	// 返回结果
	public static String		RESULT							= "RESULT";
	// 描述
	public static String		DESCRIPTION						= "DESCRIPTION";

	// 转换配置guid
	public static String		TRANSFORM_CONFIG_GUID			= "TRANSFORMCONFIGGUID";

	// 转换类型
	public static String		TRANSFORM_TYPE					= "TRANSFORMTYPE";

	// 转换实例
	public static String		TRANSFORM_INSTANCE_GUID			= "REVISIONGUID";

	// 转换实例类
	public static String		TRANSFORM_INSTANCE_CLASSGUID	= "REVISIONCLASSGUID";

	// 转换文件guid
	public static String		FILE_GUID						= "FILEGUID";

	public static String		FILE_NAME						= "FILENAME";

	public static String		FILE_TYPE						= "FILETYPE";

	public static String		MASTERFK						= "MASTERFK";

	public static String		MASTERFK_CREATETIME				= "MASTERCREATETIME";

	public static String		FILE_MD5						= "MD5";

	public static String		SIGN_GUID						= "SIGNGUID";

	public static String		DSS_ID							= "DSSID";

	public static String		NEED_TRANSFORM					= "NEEDTRANSFORM";

	public static String		TARGETREVISIONGUID				= "TARGETREVISIONGUID";

	public static String		TARGETREVISIONCLASSGUID			= "TARGETREVISIONCLASSGUID";

	public TransformQueue()
	{
		this.setJobStatus(JobStatus.WAITING.getValue() + "");
	}

	public String getId()
	{
		return (String) this.get(ID);
	}

	public void setId(String id)
	{
		this.put(ID, id);
	}

	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public String getOwneruserGuid()
	{
		return (String) this.get(OWNERUSER_GUID);
	}

	public void setOwneruserGuid(String owneruserGuid)
	{
		this.put(OWNERUSER_GUID, owneruserGuid);
	}

	public String getOwneruserName()
	{
		return (String) this.get(OWNERUSER_NAME);
	}

	public void setOwneruserName(String owneruserName)
	{
		this.put(OWNERUSER_NAME, owneruserName);
	}

	public JobStatus getJobStatus()
	{
		// return (String) this.get(JOB_STATUS);
		String status = (String) this.get(JOB_STATUS);
		return JobStatus.getStatus(Integer.valueOf(status));
	}

	public void setJobStatus(String jobStatus)
	{
		this.put(JOB_STATUS, jobStatus);
	}

	public String getExecutorClass()
	{
		return (String) this.get(EXECUTOR_CLASS);
	}

	public void setExecutorClass(String executorClass)
	{
		this.put(EXECUTOR_CLASS, executorClass);
	}

	public String getIsSinglethRead()
	{
		return (String) this.get(IS_SINGLETH_READ);
	}

	public void setIsSinglethRead(String isSinglethRead)
	{
		this.put(IS_SINGLETH_READ, isSinglethRead);
	}

	public String getPriority()
	{
		return this.get(PRIORITY) == null ? null : this.get(PRIORITY).toString();
	}

	public void setPriority(String priority)
	{
		this.put(PRIORITY, priority == null ? null : new BigDecimal(priority));
	}

	public String getResult()
	{
		return (String) this.get(RESULT);
	}

	public void setResult(String result)
	{
		this.put(RESULT, result);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public String getTransformConfigGuid()
	{
		return (String) this.get(TRANSFORM_CONFIG_GUID);
	}

	public void setTransformConfigGuid(String transformConfigGuid)
	{
		this.put(TRANSFORM_CONFIG_GUID, transformConfigGuid);
	}

	public String getTransformType()
	{
		return (String) this.get(TRANSFORM_TYPE);
	}

	public void setTransformType(String type)
	{
		this.put(TRANSFORM_TYPE, type);
	}

	public String getTransformInstanceGuid()
	{
		return (String) this.get(TRANSFORM_INSTANCE_GUID);
	}

	public void setTransformInstanceGuid(String transformInstanceGuid)
	{
		this.put(TRANSFORM_INSTANCE_GUID, transformInstanceGuid);
	}

	public String getTransformInstanceClassGuid()
	{
		return (String) this.get(TRANSFORM_INSTANCE_CLASSGUID);
	}

	public void setTransformInstanceClassGuid(String classGuid)
	{
		this.put(TRANSFORM_INSTANCE_CLASSGUID, classGuid);
	}

	public String getFileGuid()
	{
		return (String) this.get(FILE_GUID);
	}

	public void setFileGuid(String transformFile)
	{
		this.put(FILE_GUID, transformFile);
	}

	public String getFileType()
	{
		return (String) this.get(FILE_TYPE);
	}

	public void setFileType(String fileType)
	{
		this.put(FILE_TYPE, fileType);
	}

	public String getFileName()
	{
		return (String) this.get(FILE_NAME);
	}

	public void setFileName(String fileName)
	{
		this.put(FILE_NAME, fileName);
	}

	public String getMasterFK()
	{
		return (String) this.get(MASTERFK);
	}

	public void setMasterFK(String masterFK)
	{
		this.put(MASTERFK, masterFK);
	}

	public Date getMasterCreateTime()
	{
		return (Date) this.get(MASTERFK_CREATETIME);
	}

	public void setMasterCreateTime(Date date)
	{
		this.put(MASTERFK_CREATETIME, date);
	}

	public String getFileMD5()
	{
		return (String) this.get(FILE_MD5);
	}

	public void setFileMD5(String MD5)
	{
		this.put(FILE_MD5, MD5);
	}

	public String getSignGuid()
	{
		return (String) this.get(SIGN_GUID);
	}

	public void setSignGuid(String signGuid)
	{
		this.put(SIGN_GUID, signGuid);
	}

	public String getDssID()
	{
		return (String) this.get(DSS_ID);
	}

	public void setDssID(String dssID)
	{
		this.put(DSS_ID, dssID);
	}

	public boolean needTransform()
	{
		if (this.get(NEED_TRANSFORM) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(NEED_TRANSFORM));
	}

	public void setNeedTransform(boolean needTransform)
	{
		this.put(NEED_TRANSFORM, BooleanUtils.getBooleanStringYN(needTransform));
	}
}
