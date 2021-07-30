/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Downloader
 * Wanglei 2010-12-8
 */
package dyna.net.syncfile.download;

import java.io.OutputStream;
import java.io.Serializable;

import dyna.common.exception.ServiceRequestException;
import dyna.common.util.CopyStreamListener;

/**
 * 文件下载接口
 * 
 * @author Wanglei
 * 
 */
public interface Downloader extends Serializable
{

	/**
	 * 执行下载操作
	 * 
	 * @param output
	 *            本地输出流
	 * @throws ServiceRequestException
	 */
	public void download(OutputStream output) throws ServiceRequestException;

	/**
	 * 执行下载操作
	 * 
	 * @param output
	 *            本地输出流
	 * @param listener
	 *            流传输监听器
	 * @throws ServiceRequestException
	 */
	public void download(OutputStream output, CopyStreamListener listener) throws ServiceRequestException;
}
