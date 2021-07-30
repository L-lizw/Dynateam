/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SyncFileServiceImpl
 * Wanglei 2010-12-3
 */
package dyna.net.syncfile;

import dyna.common.Poolable;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.net.syncfile.download.Downloader;
import dyna.net.syncfile.download.DownloaderImpl;
import dyna.net.syncfile.transfer.THCLASSIFICATIONImpl;
import dyna.net.syncfile.transfer.THICONImpl;
import dyna.net.syncfile.transfer.THOMImpl;
import dyna.net.syncfile.transfer.THREPORTImpl;
import dyna.net.syncfile.transfer.THSERVERImpl;
import dyna.net.syncfile.transfer.THUDPKGImpl;
import dyna.net.syncfile.transfer.TransferEnum;
import dyna.net.syncfile.transfer.TransferHelper;
import dyna.net.syncfile.upload.Uploader;
import dyna.net.syncfile.upload.UploaderImpl;

/**
 * 系统文件同步服务的实现
 * 
 * @author Wanglei
 * 
 */
public class SyncFileServiceImpl implements SyncFileService, Poolable
{

	private static SyncFileServer	syncFileServer	= null;

	private static String			host			= null;

	private final String			serviceUID		= StringUtils.generateRandomUID(32);

	private static synchronized void createSyncFileServer(ServiceDefinition serviceDefinition)
	{
		if (syncFileServer == null)
		{
			String initPort = serviceDefinition.getInitParameter("port");
			int port = 0;
			if (!StringUtils.isNullString(initPort))
			{
				port = Integer.valueOf(initPort);
			}
			syncFileServer = new SyncFileServer(port);
			syncFileServer.start();
			host = serviceDefinition.getDispatchIP();
			if (StringUtils.isNullString(host))
			{
				host = System.getProperty("java.rmi.server.hostname");
				if (StringUtils.isNullString(host))
				{
					host = syncFileServer.getHostAddress();
				}
			}
		}
	}

	public SyncFileServiceImpl()
	{

	}

	public SyncFileServiceImpl(ServiceDefinition serviceDefinition)
	{
		createSyncFileServer(serviceDefinition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.SyncFileService#getUploader()
	 */
	@Override
	public Uploader getUploader(TransferEnum transferEnum) throws ServiceRequestException
	{
		String session = syncFileServer.requestSession(this.getTransferHelper(transferEnum, true));
		return new UploaderImpl(host, syncFileServer.getPort(), session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.SyncFileService#getDownloader()
	 */
	@Override
	public Downloader getDownloader(TransferEnum transferEnum) throws ServiceRequestException
	{
		String session = syncFileServer.requestSession(this.getTransferHelper(transferEnum, false));
		return new DownloaderImpl(host, syncFileServer.getPort(), session);
	}

	private TransferHelper getTransferHelper(TransferEnum transferEnum, boolean isUpload)
	{
		TransferHelper helper = null;
		switch (transferEnum)
		{
		case OM:
			helper = new THOMImpl(isUpload);
			break;
		case CF:
			helper = new THCLASSIFICATIONImpl(isUpload);
			break;
		case DOWNLOAD_ICON_DS:
		case DOWNLOAD_ICON_AS:
			if (!isUpload)
			{
				helper = new THICONImpl(transferEnum);
			}
			break;
		case DOWNLOAD_UDPKG:
		case DOWNLOAD_UDCPKG:
		case DOWNLOAD_UDCADPKG:
			helper = new THUDPKGImpl(transferEnum);
			break;
		case DOWNLOAD_REPORT_DS:
			helper = new THREPORTImpl(transferEnum);
			break;
		case DOWNLOAD_SERVER:
			helper = new THSERVERImpl();
			break;
		default:
			break;
		}
		return helper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.Poolable#initObject(java.lang.Object[])
	 */
	@Override
	public void initObject(Object... initArgs) throws Exception
	{
		createSyncFileServer((ServiceDefinition) initArgs[1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.Poolable#destroyObject()
	 */
	@Override
	public void destroyObject() throws Exception
	{
		// do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.Poolable#validateObject()
	 */
	@Override
	public boolean validateObject()
	{
		// do nothing
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.Poolable#activateObject()
	 */
	@Override
	public void activateObject() throws Exception
	{
		// do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.Poolable#passivateObject()
	 */
	@Override
	public void passivateObject() throws Exception
	{
		// do nothing

	}

	@Override
	public String getObjectUID()
	{
		return this.serviceUID;
	}

}
