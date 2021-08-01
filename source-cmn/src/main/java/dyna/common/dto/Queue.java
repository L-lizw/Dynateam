/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Queue 队列
 * zhanghw 2012-04-24
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.QueueMapper;
import dyna.common.systemenum.JobGroupEnum;
import dyna.common.systemenum.JobStatus;

import java.math.BigDecimal;

/**
 * Queue 队列
 * 
 * @author caogc
 * 
 */
@EntryMapper(QueueMapper.class)
public class Queue extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -9137180160333458537L;

	public static String		GUID				= "GUID";
	// 编号
	public static String		ID					= "JOBID";
	// 名称
	public static String		NAME				= "JOBNAME";
	// 执行人
	public static String		OWNERUSER_GUID		= "OWNERUSERGUID";
	// 执行人
	public static String		OWNERUSER_NAME		= "OWNERUSERNAME";
	// 状态
	public static String		JOB_STATUS			= "JOBSTATUS";
	// 对应的CLASS
	public static String		EXECUTOR_CLASS		= "EXECUTORCLASS";
	// 是否单线程
	public static String		IS_SINGLETH_READ	= "ISSINGLETHREAD";
	// 队列任务类别
	public static String		TYPE				= "JOBTYPE";
	// 优先级
	public static String		PRIORITY			= "PRIORITY";
	// 返回结果
	public static String		RESULT				= "EXECUTRESULT";
	// 描述
	public static String		DESCRIPTION			= "DESCRIPTION";

	public static String		FIELDA				= "FIELDA";
	public static String		FIELDB				= "FIELDB";
	public static String		FIELDC				= "FIELDC";
	public static String		FIELDD				= "FIELDD";
	public static String		FIELDE				= "FIELDE";
	public static String		FIELDF				= "FIELDF";
	public static String		FIELDG				= "FIELDG";
	public static String		FIELDH				= "FIELDH";
	public static String		FIELDI				= "FIELDI";
	public static String		FIELDJ				= "FIELDJ";
	public static String		FIELDK				= "FIELDK";
	public static String		FIELDL				= "FIELDL";
	public static String		FIELDM				= "FIELDM";
	public static String		FIELDN				= "FIELDN";
	public static String		FIELDO				= "FIELDO";

	public static String		SERVER_ID			= "SERVERID";
	public static String		JOBGROUP			= "JOBGROUP";
	public static String		FILEGUID			= "FILEGUID";

	public static String		CURRENTUSERGUID		= "CURRENTUSERGUID";

	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	public String getId()
	{
		return (String) this.get(ID);
	}

	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	public String getOwneruserGuid()
	{
		return (String) this.get(OWNERUSER_GUID);
	}

	public String getOwneruserName()
	{
		return (String) this.get(OWNERUSER_NAME);
	}

	public JobStatus getJobStatus()
	{
		Number status = (Number) this.get(JOB_STATUS);
		return JobStatus.getStatus(Integer.valueOf(status.intValue()));
	}

	public String getExecutorClass()
	{
		return (String) this.get(EXECUTOR_CLASS);
	}

	public String getIsSinglethRead()
	{
		return (String) this.get(IS_SINGLETH_READ);
	}

	public String getType()
	{
		return (String) this.get(TYPE);
	}

	public Integer getPriority()
	{
		Object priority = this.get(PRIORITY);

		return priority == null ? null : ((Number) priority).intValue();
	}

	public String getResult()
	{
		return (String) this.get(RESULT);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public String getFielda()
	{
		return (String) this.get(FIELDA);
	}

	public String getFieldb()
	{
		return (String) this.get(FIELDB);
	}

	public String getFieldc()
	{
		return (String) this.get(FIELDC);
	}

	public String getFieldd()
	{
		return (String) this.get(FIELDD);
	}

	public String getFielde()
	{
		return (String) this.get(FIELDE);
	}

	public String getFieldf()
	{
		return (String) this.get(FIELDF);
	}

	public String getFieldg()
	{
		return (String) this.get(FIELDG);
	}

	public String getFieldh()
	{
		return (String) this.get(FIELDH);
	}

	public String getFieldi()
	{
		return (String) this.get(FIELDI);
	}

	public String getFieldj()
	{
		return (String) this.get(FIELDJ);
	}

	public String getFieldk()
	{
		return (String) this.get(FIELDK);
	}

	public String getFieldl()
	{
		return (String) this.get(FIELDL);
	}

	public String getFieldm()
	{
		return (String) this.get(FIELDM);
	}

	public String getFieldn()
	{
		return (String) this.get(FIELDN);
	}

	public String getFieldo()
	{
		return (String) this.get(FIELDO);
	}

	public void setId(String id)
	{
		this.put(ID, id);
	}

	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public void setOwneruserGuid(String owneruserGuid)
	{
		this.put(OWNERUSER_GUID, owneruserGuid);
	}

	public void setOwneruserName(String owneruserName)
	{
		this.put(OWNERUSER_NAME, owneruserName);
	}

	public void setJobStatus(JobStatus jobStatus)
	{
		this.put(JOB_STATUS, jobStatus.getValue());
	}

	public void setExecutorClass(String executorClass)
	{
		this.put(EXECUTOR_CLASS, executorClass);
	}

	public void setIsSinglethRead(String isSinglethRead)
	{
		this.put(IS_SINGLETH_READ, isSinglethRead);
	}

	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	public void setPriority(Integer priority)
	{
		this.put(PRIORITY, new BigDecimal(String.valueOf(priority)));
	}

	public void setResult(String result)
	{
		this.put(RESULT, result);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public void setFielda(String fielda)
	{
		this.put(FIELDA, fielda);
	}

	public void setFieldb(String fieldb)
	{
		this.put(FIELDB, fieldb);
	}

	public void setFieldc(String fieldc)
	{
		this.put(FIELDC, fieldc);
	}

	public void setFieldd(String fieldd)
	{
		this.put(FIELDD, fieldd);
	}

	public void setFielde(String fielde)
	{
		this.put(FIELDE, fielde);
	}

	public void setFieldf(String fieldf)
	{
		this.put(FIELDF, fieldf);
	}

	public void setFieldg(String fieldg)
	{
		this.put(FIELDG, fieldg);
	}

	public void setFieldh(String fieldh)
	{
		this.put(FIELDH, fieldh);
	}

	public void setFieldi(String fieldi)
	{
		this.put(FIELDI, fieldi);
	}

	public void setFieldj(String fieldj)
	{
		this.put(FIELDJ, fieldj);
	}

	public void setFieldk(String fieldk)
	{
		this.put(FIELDK, fieldk);
	}

	public void setFieldl(String fieldl)
	{
		this.put(FIELDL, fieldl);
	}

	public void setFieldm(String fieldm)
	{
		this.put(FIELDM, fieldm);
	}

	public void setFieldn(String fieldn)
	{
		this.put(FIELDN, fieldn);
	}

	public void setFieldo(String fieldo)
	{
		this.put(FIELDO, fieldo);
	}

	public String getServerID()
	{
		return (String) this.get(SERVER_ID);
	}

	public void setServerID(String serverID)
	{
		this.put(SERVER_ID, serverID);
	}

	public JobGroupEnum getJobGroup()
	{
		return JobGroupEnum.typeValueOf((String) this.get(JOBGROUP));
	}

	public void setJobGroup(JobGroupEnum jobGroup)
	{
		if (jobGroup == null)
		{
			this.put(JOBGROUP, JobGroupEnum.OTHER.getGroup());
		}
		else
		{
			this.put(JOBGROUP, jobGroup.getGroup());
		}
	}

	public void setFileGuid(String fileGuid)
	{
		this.put(FILEGUID, fileGuid);
	}

	public String getFileGuid()
	{
		return (String) this.get(FILEGUID);
	}
}
