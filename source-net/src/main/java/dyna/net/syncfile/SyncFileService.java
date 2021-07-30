/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SyncFileService
 * Wanglei 2010-12-3
 */
package dyna.net.syncfile;

import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;
import dyna.net.syncfile.download.Downloader;
import dyna.net.syncfile.transfer.TransferEnum;
import dyna.net.syncfile.upload.Uploader;

/**
 * 系统文件同步服务
 * 
 * @author Wanglei
 * 
 */
public interface SyncFileService extends Service
{

	/**
	 * 获取上传文件实例
	 * @param transferEnum TODO
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public Uploader getUploader(TransferEnum transferEnum) throws ServiceRequestException;

	/**
	 * 获取下载文件的实例
	 * @param transferEnum TODO
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public Downloader getDownloader(TransferEnum transferEnum) throws ServiceRequestException;
}
