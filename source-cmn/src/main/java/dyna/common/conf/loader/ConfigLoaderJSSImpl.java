/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoaderMSRImpl
 * Qiuxq 2012-4-24
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableJSSImpl;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.JobDefinition;
import dyna.common.conf.SchedulerDefinition;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;

import java.util.Iterator;

/**
 * @author Qiuxq
 *
 */
public class ConfigLoaderJSSImpl extends AbstractConfigLoader<ConfigurableJSSImpl>
{

	private ConfigurableJSSImpl	conf			= null;

	private String	confDirectory	= EnvUtils.getConfRootPath() + "conf/JobQueueConfig.xml";

	protected ConfigLoaderJSSImpl()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoaderDefaultImpl#load()
	 */
	@Override
	public synchronized void load(String xmlFilePath)
	{
		this.conf = new ConfigurableJSSImpl();
		this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));

		ConfigurableKVElementImpl kvElement = super.loadDefault();

	
		Iterator<ConfigurableKVElementImpl> elementIterator = kvElement.iterator("JobQueueConfig.jobtype.type");
		ConfigurableKVElementImpl element = null;
		while (elementIterator.hasNext())
		{
			element = elementIterator.next();

			JobDefinition sd = new JobDefinition();
			sd.setJobID(element.getElementValue("id"));
			sd.setJobName(element.getElementValue("name"));
			sd.setMsrId(element.getElementValue("msrID"));
			sd.setExecutorClassName(element.getElementValue("executorclass"));
			sd.setSchedulerID(element.getElementValue("SchedulerID"));
			if (!StringUtils.isNullString(element.getElementValue("priority")))
			{
				sd.setPriority(Integer.parseInt(element.getElementValue("priority")));
			}
			try
			{
				sd.setTimeOut(Integer.parseInt(element.getElementValue("TimeOut")));
			}
			catch(Exception e)
			{
				
			}
			this.conf.addJobDefinition(sd);
		}
		elementIterator = kvElement.iterator("JobQueueConfig.SchedulerList.Scheduler");
		while (elementIterator.hasNext())
		{
			element = elementIterator.next();

			SchedulerDefinition sd = new SchedulerDefinition();
			sd.setId(element.getElementValue("id"));
			try
			{
				sd.setThreadCount(Integer.valueOf(element.getElementValue("thread")));
			}
			catch(Exception e)
			{
				
			}
			this.conf.addSchedulerDefinition(sd);
		}
		this.conf.configured();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public void load()
	{
		 this.load(this.confDirectory);
	}

	@Override public ConfigurableJSSImpl getConfigurable()
	{
		return conf;
	}

}
