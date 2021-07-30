/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TransferHelper
 * Wanglei 2010-12-9
 */
package dyna.net.syncfile.transfer;

import java.io.File;

/**
 * 文件传输帮助接口
 * 
 * @author Wanglei
 * 
 */
public interface TransferHelper
{

	/**
	 * 上传/下载
	 * 
	 * @return
	 */
	public boolean isUpload();

	/**
	 * 传输的文件
	 * 
	 * @return
	 */
	public File getFile();

	/**
	 * 传输开始之前执行的操作
	 */
	public void onStart() throws Exception;

	/**
	 * 传输结束之后执行的操作
	 */
	public void onFinish() throws Exception;
}
