/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericServerImpl
 * Wanglei 2010-3-29
 */
package dyna.app.server;

import dyna.app.net.impl.server.ServiceLocatorServerImpl;
import dyna.app.server.context.ApplicationServerContext;
import dyna.app.server.context.ServerContextListener;
import dyna.app.server.context.ServiceContext;
import dyna.app.server.context.ServiceContextImpl;
import dyna.common.Version;
import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ConnectionModeEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.data.DataServer;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.syncfile.download.Downloader;
import dyna.net.syncfile.transfer.TransferEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Wanglei
 */
@Service public class ServerImpl implements Server
{
	@Autowired private ApplicationServerContext serverContext = null;
	@Autowired private ServiceContext           scContext     = null;
	@Autowired
	private ServiceLocatorServerImpl serviceLocator = null;

	private final List<ServerContextListener> serverCtxListeners = new ArrayList<ServerContextListener>();


	protected boolean isStart = false;

	public ServerImpl() throws Exception
	{
		super();
	}

	public void initialize() throws Exception
	{
		boolean isDistDSConn = false;
		ConfigurableConnToDSImpl connToDS = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4ConnToDS().getConfigurable();
		if (connToDS.getClientMode() == ConnectionModeEnum.DISTRIBUTED)
		{
			isDistDSConn = true;
		}
		else
		{
			DataServer.createDataServer();
		}

		DynaLogger.info("***************************************************");
		DynaLogger.info("************* " + Version.getProductName() + " " + Version.getVersionInfo() + "**********");
		DynaLogger.info("************* " + Version.getCopyRight() + " ****************");
		DynaLogger.info("***************************************************");
		DynaLogger.info("Starting Server[" + this.getClass().getSimpleName() + "]");

		DynaLogger.print("\tConnecting to DataServer[" + connToDS.getClientMode().name() + (!isDistDSConn ?
				"" :
				":" + connToDS.getLookupServiceHost() + ":" + connToDS.getLookupServicePort()) + "]...");
		File iconFile = new File(EnvUtils.getRootPath() + "tmp/icon.zip");
		FileUtils.makeDirectories(iconFile.getAbsolutePath(), true);
		Downloader downloader = null;
		if (isDistDSConn)
		{
			DataServer.createDataServer();
			downloader = DataServer.getSyncFileService().getDownloader(TransferEnum.DOWNLOAD_ICON_DS);
		}
		DynaLogger.println("[OK]");

		DynaLogger.print("\tSynchronizing with DataServer...");
		if (downloader != null)
		{
			FileOutputStream out = null;
			try
			{
				out = new FileOutputStream(iconFile);
				downloader.download(out);
			}
			finally
			{
				if (out != null)
				{
					out.close();
				}
			}
		}
		else
		{
			FileUtils.compress(new File(EnvUtils.getRootPath() + "/conf/icon"), iconFile);
		}
		File reportFile = new File(EnvUtils.getRootPath() + "tmp/report.zip");
		FileUtils.makeDirectories(reportFile.getAbsolutePath(), true);
		downloader = null;
		if (isDistDSConn)
		{
			downloader = DataServer.getSyncFileService().getDownloader(TransferEnum.DOWNLOAD_REPORT_DS);
			FileOutputStream out = null;
			try
			{
				out = new FileOutputStream(reportFile);
				downloader.download(out);
			}
			finally
			{
				if (out != null)
				{
					out.close();
				}
			}
			if (reportFile.length() > 0)
			{
				File tempDir = FileUtils.newFileEscape(EnvUtils.getRootPath() + "tmp/report");
				FileUtils.decompress(EnvUtils.getRootPath() + "tmp/report.zip", tempDir);

				// 删除下载的压缩文件
				FileUtils.deleteFile(EnvUtils.getRootPath() + "tmp/report.zip");

				// 删除原报表文件目录，并创建新的文件目录
				String modelReportPath = EnvUtils.getRootPath() + "/conf/report";
				File modelReportDir = FileUtils.newFileEscape(modelReportPath);
				FileUtils.deleteFile(modelReportDir);
				if (FileUtils.makeDirectories(modelReportPath, false))
				{
					// 将解压的临时文件夹中文件目录复制到报表文件目录
					FileUtils.copyFile(FileUtils.newFileEscape(EnvUtils.getRootPath() + "tmp/report/report"), modelReportDir, false);
				}

				// 删除临时文件夹
				FileUtils.deleteFile(tempDir);
			}
			else
			{
				FileUtils.deleteFile(EnvUtils.getRootPath() + "tmp/report.zip");
			}
		}
		DynaLogger.println("[OK]");

		DynaLogger.info("Server initialize...");
		this.serverContext.init();
		this.scContext.init();

		// service provider initialize
		this.createServiceProvider();

		// do server context listener
		for (ServerContextListener listener : this.serverCtxListeners)
		{
			listener.contextInitialized();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{

			@Override public void run()
			{
				try
				{
					ServerImpl.this.shutdown();
				}
				catch (Exception e)
				{
					DynaLogger.info("Failed to shutdown server: ", e.fillInStackTrace());
					System.exit(-1);
				}
			}
		}));
	}

	private void createServiceProvider()
	{
		try
		{
			ServiceProviderFactory.getServiceProvider();
		}
		catch (IllegalStateException e)
		{
			this.serviceLocator.getServiceStateSync().setReactor(this.serverContext.getSscReactor());
			((ServiceContextImpl) this.scContext).getServiceContextObservable().addObserver(this.serviceLocator);
			ServiceProviderFactory.createServiceProvider(this.serviceLocator);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.server.Server#start()
	 */
	@Override public void start() throws Exception
	{
		this.isStart = true;
		DynaLogger.info("Server started.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.server.Server#shutdown()
	 */
	@Override public void shutdown() throws Exception
	{
		DynaLogger.info("Ready to shutdown Server...");
		this.stop();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.server.Server#stop()
	 */
	@Override public void stop() throws Exception
	{
		DynaLogger.info("Stop Server[" + this.getClass().getSimpleName() + "]");
		this.isStart = false;
		this.serverContext.getSchedulerManager().shutdownAll();
		DataServer.close();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.server.Server#isAlive()
	 */
	@Override public boolean isAlive()
	{
		return this.isStart;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * dyna.app.server.context.ServerContext#addServerContextListener(dyna.app.server.context.ServerContextListener)
	 */
	@Override
	public void addServerContextListener(ServerContextListener listener)
	{
		this.serverCtxListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * dyna.app.server.context.ServerContext#removeServerContextListener(dyna.app.server.context.ServerContextListener)
	 */
	@Override
	public void removeServerContextListener(ServerContextListener listener)
	{
		this.serverCtxListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.server.context.ServerContext#getServerContextListeners()
	 */
	@Override
	public List<ServerContextListener> getServerContextListeners()
	{
		return Collections.unmodifiableList(this.serverCtxListeners);
	}

}
