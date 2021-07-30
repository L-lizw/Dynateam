/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: THOMImpl
 * Wanglei 2010-12-9
 */
package dyna.net.syncfile.transfer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * 传输服务器程序文件
 * 
 * @author Wanglei
 * 
 */
public class THSERVERImpl extends AbstractTransferHelper
{
	private final static String			FILE_NAME		= "serverupdate.zip";
	private final static List<String>	removeFileList	= Arrays.asList("om", "om_bak", "client.xml", "crossconf.xml", "dsconn.xml", "dsserver.xml", "license.lic",
																"license.properties", "server.xml", "syncServerConfig.xml");
	private String						FILE_PATH		= EnvUtils.getConfRootPath();

	/**
	 * @param
	 */
	public THSERVERImpl()
	{
		super(EnvUtils.getConfRootPath() + "tmp/server/", FILE_NAME, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.AbstractTransferHelper#onStart()
	 */
	@Override
	public void onStart() throws Exception
	{
		super.onStart();
		File[] files = (new File(this.FILE_PATH + "conf")).listFiles();
		List<File> fileList = new ArrayList<File>();
		List<String> baseDirList = new ArrayList<String>();
		for (File file : files)
		{
			if (!removeFileList.contains(file.getName().toLowerCase()))
			{
				fileList.add(file);
				baseDirList.add("conf/");
			}
		}

		files = (new File(this.FILE_PATH)).listFiles();
		for (File file : files)
		{
			if (file.isDirectory() && !"conf".equalsIgnoreCase(file.getName().toLowerCase()) && !"tmp".equalsIgnoreCase(file.getName().toLowerCase())
					&& !"log".equalsIgnoreCase(file.getName().toLowerCase()) && !"jre".equalsIgnoreCase(file.getName().toLowerCase())
					&& !file.getName().toLowerCase().contains("jdk"))
			{
				fileList.add(file);
				baseDirList.add("");
			}
		}
		File[] updatefiles = new File[fileList.size()];
		updatefiles = fileList.toArray(updatefiles);
		String[] baseDirs = new String[fileList.size()];
		baseDirs = baseDirList.toArray(baseDirs);
		//TODO lizw
//		FileUtils.compress(baseDirs, updatefiles, this.file, true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.AbstractTransferHelper#onFinish()
	 */
	@Override
	public void onFinish() throws Exception
	{
		super.onFinish();
		// delete file
		this.file.delete();
	}

}
