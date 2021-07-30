/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoaderMSRImpl
 * Wanglei 2010-7-1
 */
package dyna.common.conf.loader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.ConfigurableMSRImpl;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * @author Wanglei
 *
 */
public class ConfigLoaderMSRImpl extends AbstractConfigLoader<ConfigurableMSRImpl>
{

	private ConfigurableMSRImpl	conf			= null;

	private String	confDirectory	= EnvUtils.getConfRootPath() + "conf/";

	protected ConfigLoaderMSRImpl()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoaderDefaultImpl#load()
	 */
	@Override
	public synchronized ConfigurableMSRImpl load(String xmlFilePath)
	{
		if (this.conf != null)
		{
			return this.conf;
		}

		this.conf = new ConfigurableMSRImpl();
		File confDir = FileUtils.newFileEscape(xmlFilePath);
		List<File> msrFileList=new ArrayList<File>();
		this.genMsrFileList(confDir,msrFileList);
		String fileName = null;
		String loc = null;
		for (int i = 0; i < msrFileList.size(); i++)
		{
			fileName = msrFileList.get(i).getName();

			loc = fileName.substring(0, fileName.lastIndexOf('.'));
			loc = loc.substring(loc.lastIndexOf('.') + 1);

			this.setConfigFile(msrFileList.get(i));

			super.loadDefault();

			ConfigurableKVElementImpl msr = null;
			for (Iterator<ConfigurableKVElementImpl> iter = this.kvElement.iterator("msrs.msr"); iter.hasNext();)
			{
				msr = iter.next();
				this.conf.putValue(msr.getElementValue("id"), msr.getElementValue("msg"), loc);
			}

			this.kvElement.clear();
		}
		this.conf.configured();
		return this.conf;
	}

	private void genMsrFileList(File confDir, List<File> msrFileList)
	{
		File[] files = confDir.listFiles();
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile())
				{
					String fileName = files[i].getName();
					if (fileName.startsWith("message") && fileName.endsWith(".xml"))
					{
						msrFileList.add(files[i]);
					}
				}
			}
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					this.genMsrFileList(files[i], msrFileList);
				}
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public ConfigurableMSRImpl load()
	{
		return this.load(this.confDirectory);
	}

}
