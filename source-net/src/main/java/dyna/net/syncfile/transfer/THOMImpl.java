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
public class THOMImpl extends AbstractTransferHelper
{
	private final static String	FILE_NAME		= "om.zip";

	private String				FILE_PATH		= EnvUtils.getConfRootPath();

	private final String		FILE_PATH_BAK	= EnvUtils.getConfRootPath() + "conf/om_bak";

	/**
	 * @param isUpload
	 */
	public THOMImpl(boolean isUpload)
	{
		super(isUpload ? EnvUtils.getConfRootPath() + "conf/om/" : EnvUtils.getConfRootPath() + "tmp/om/", FILE_NAME, isUpload);

		if (isUpload)
		{
			this.FILE_PATH = this.FILE_PATH + "conf/";
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
			String iconDir = EnvUtils.getConfRootPath() + "conf/";
			// zip file
			//TODO lizw
//			FileUtils.compress(new File[] { new File(this.FILE_PATH + "om") }, this.file, true);
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
			File destFile = new File(this.FILE_PATH_BAK);
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
