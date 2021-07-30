/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigurableMSRImpl
 * Qiuxq 2012-4-24
 */
package dyna.common.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author Qiuxq
 *
 */
public class ConfigurableJSSImpl extends ConfigurableAdapter
{

	private Map<String, JobDefinition>	idmap	= new HashMap<String, JobDefinition>();
	private Map<String, JobDefinition>	classmap	= new HashMap<String, JobDefinition>();
	private Map<String, SchedulerDefinition>	schedulermap	= new HashMap<String, SchedulerDefinition>();


	/**
	 * @param sd
	 */
	public void addJobDefinition(JobDefinition sd)
	{
		if (!(StringUtils.isNullString(sd.getJobID())||StringUtils.isNullString(sd.getExecutorClassName())))
		{
			this.idmap.put(sd.getJobID(), sd);
			this.classmap.put(sd.getExecutorClassName(), sd);
		}
	}
	
	public List<JobDefinition> getJobDefinitionList()
	{
		return new ArrayList<JobDefinition>(idmap.values());
	}
	
	public JobDefinition getJobDefinitionByType(String jobType)
	{
		List<JobDefinition> list = this.getJobDefinitionList();
		if (!SetUtils.isNullList(list))
		{
			for (JobDefinition jobDefinition : list)
			{
				if (jobDefinition.getJobID().equals(jobType))
				{
					return jobDefinition;
				}
			}
		}
		return null;
	}
	
	public JobDefinition getJobDelfinitionWithId(String id)
	{
		return this.idmap.get(id);
	}
	
	public JobDefinition getJobDelfinitionWithClassName(String className)
	{
		return this.classmap.get(className);
	}
	
	public void addSchedulerDefinition(SchedulerDefinition sd)
	{
		if (!(StringUtils.isNullString(sd.getId())))
		{
			this.schedulermap.put(sd.getId(), sd);
		}
	}
	
	public SchedulerDefinition getSchedulerDefinition(String id)
	{
		return this.schedulermap.get(id);
	}

	public Map<String, SchedulerDefinition> getSchedulermap() {
		return schedulermap;
	}

}
