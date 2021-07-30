/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: THelperIconImpl
 * Wanglei 2010-12-9
 */
package dyna.net.syncfile.transfer;

import java.io.File;

import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * 传输图标文件
 * 
 * @author Wanglei
 * 
 */
@Deprecated
public class THICONImpl extends AbstractTransferHelper
{

	private final static String	FILE_NAME		= "icon.zip";

	private final static String	FILE_PATH		= EnvUtils.getRootPath() + "conf/icon/";

	private final static String	FILE_PATH_AS	= EnvUtils.getRootPath() + "tmp/";

	private TransferEnum		transferEnum	= TransferEnum.DOWNLOAD_ICON_DS;

	/**
	 * @param
	 */
	public THICONImpl(TransferEnum transferEnum)
	{
		super(FILE_PATH, FILE_NAME, false);
		this.transferEnum = transferEnum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.AbstractTransferHelper#onStart()
	 */
	@Override
	public void onStart() throws Exception
	{
		if (this.transferEnum == TransferEnum.DOWNLOAD_ICON_DS)
		{
			super.onStart();

			// can only download zip file
			//TODO lizw
//			FileUtils.compress(new File[] { new File(FILE_PATH), new File(EnvUtils.getRootPath() + "conf/cf/icon") },
//					this.file, true);
		}
		else
		{
			this.file = new File(FILE_PATH_AS + FILE_NAME);
			//TODO lizw
//			FileUtils.compress(new File[] { new File(FILE_PATH), new File(EnvUtils.getRootPath() + "conf/cf/icon") },
//					this.file, true);
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
		// super.onFinish();
		// if (this.isUpload())
		// {
		// // unzip file
		// FileUtils.decompress(this.file.getAbsolutePath(), new File(FILE_PATH).getParentFile());
		// }
		// else
		// {
		// // delete file
		// this.file.delete();
		// }

		if (this.transferEnum == TransferEnum.DOWNLOAD_ICON_DS)
		{
			this.file.delete();
		}
	}

}
