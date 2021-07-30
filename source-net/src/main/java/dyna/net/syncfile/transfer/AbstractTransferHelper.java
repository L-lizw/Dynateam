/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractTransferHelper
 * Wanglei 2010-12-9
 */
package dyna.net.syncfile.transfer;

import java.io.File;
import java.util.Date;

import dyna.common.util.DateFormat;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * @author Wanglei
 * 
 */
public abstract class AbstractTransferHelper implements TransferHelper
{

	private boolean		isUpload		= false;

	protected File		file			= null;

	protected String	tempFolderPath	= EnvUtils.getConfRootPath() + "tmp/";

	protected String	srcFilePath		= null;

	protected String	fileName		= null;

	protected AbstractTransferHelper(String srcFilePath, String fileName, boolean isUpload)
	{
		this.srcFilePath = srcFilePath;
		this.fileName = fileName;
		this.isUpload = isUpload;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.TransferHelper#isUpload()
	 */
	@Override
	public boolean isUpload()
	{
		return this.isUpload;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.TransferHelper#getFile()
	 */
	@Override
	public File getFile()
	{
		return this.file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.TransferHelper#onStart()
	 */
	@Override
	public void onStart() throws Exception
	{
		String fileName = this.tempFolderPath + (this.isUpload() ? "upload/" : "download/") + this.fileName;
		String rename = this.fileName + "." + DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP);

		FileUtils.makeDirectories(fileName, true);

		Boolean suc = FileUtils.rename(fileName, rename);
		if (suc != null && !suc.booleanValue())
		{
			throw new RuntimeException("failed to rename file: " + rename);
		}

		this.file = new File(fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.TransferHelper#onFinish()
	 */
	@Override
	public void onFinish() throws Exception
	{
		if (this.isUpload())
		{
			// backup file
			File file = new File(this.srcFilePath);
			String rename = file.getName() + DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP);

			Boolean suc = FileUtils.rename(this.srcFilePath, rename);
			if (suc != null && !suc.booleanValue())
			{
				throw new RuntimeException("failed to rename file: " + rename);
			}
			File destFile = new File(EnvUtils.getConfRootPath() + "conf/om_bak");

			if (!destFile.exists())
			{
				boolean mkdirs = destFile.mkdirs();
				if (mkdirs == false)
				{
					throw new RuntimeException("failed to create dir : " + destFile.getAbsolutePath());
				}
			}

			FileUtils.copyFile(new File(EnvUtils.getConfRootPath() + "conf/" + rename), destFile, true);
			FileUtils.deleteFile(new File(EnvUtils.getConfRootPath() + "conf/" + rename));
		}
	}

}
