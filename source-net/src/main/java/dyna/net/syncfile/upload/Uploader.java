/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Uploader
 * Wanglei 2010-12-3
 */
package dyna.net.syncfile.upload;

import java.io.InputStream;
import java.io.Serializable;

import dyna.common.exception.ServiceRequestException;
import dyna.common.util.CopyStreamListener;

/**
 * 文件上传器
 * 
 * @author Wanglei
 * 
 */
public interface Uploader extends Serializable
{
	/**
	 * 执行上传操作
	 * 
	 * @param input
	 *            本地输入流
	 * @param size
	 *            输入流的大小
	 * @throws ServiceRequestException
	 */
	public void upload(InputStream input, long size) throws ServiceRequestException;

	/**
	 * 执行上传操作
	 * 
	 * @param input
	 *            本地输入流
	 * @param size
	 *            输入流的大小
	 * @param listener
	 *            流传输监听器
	 * @throws ServiceRequestException
	 */
	public void upload(InputStream input, long size, CopyStreamListener listener) throws ServiceRequestException;
}
