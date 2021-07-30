/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: THOMImpl
 * Wanglei 2010-12-9
 */
package dyna.net.syncfile.transfer;

import java.io.File;

import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * 传输模型文件
 * 
 * @author Wanglei
 * 
 */
public class THCLASSIFICATIONImpl extends AbstractTransferHelper
{
	private final static String	FILE_NAME	= "classification.zip";

	private String				FILE_PATH	= EnvUtils.getConfRootPath();

	/**
	 * @param isUpload
	 */
	public THCLASSIFICATIONImpl(boolean isUpload)
	{
		super(isUpload ? EnvUtils.getConfRootPath() + "conf/cf/" : EnvUtils.getConfRootPath() + "tmp/cf/", FILE_NAME,
				isUpload);

		if (isUpload)
		{
			this.FILE_PATH = this.FILE_PATH + "conf/cf";
		}
		else
		{
			this.FILE_PATH = this.FILE_PATH + "tmp/";
		}
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

		if (!this.isUpload())// download
		{
			// zip file
			//TODO lizw
//			FileUtils.compress(new File[] { new File(this.FILE_PATH + "cf") }, this.file, false);
		}

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
		if (this.isUpload())
		{
			// File destFile = new File(this.FILE_PATH_BAK);
			// unzip file
			//TODO lizw
//			FileUtils.decompress(this.file.getAbsolutePath(), new File(this.FILE_PATH));
		}
		else
		{
			// delete file
			this.file.delete();
		}
	}

}
