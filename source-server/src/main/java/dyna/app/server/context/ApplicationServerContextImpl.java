/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerContext
 * Wanglei 2010-3-26
 */
package dyna.app.server.context;

import dyna.app.core.i18n.NLSManager;
import dyna.app.core.lic.License;
import dyna.app.core.lic.LicenseDaemon;
import dyna.app.core.lic.LicenseException;
import dyna.app.core.lic.LicenseManager;
import dyna.app.core.pool.PoolManager;
import dyna.app.core.sch.SchedulerManager;
import dyna.app.core.sch.SchedulerQueuedTaskImpl;
import dyna.app.core.sch.SchedulerTimerTaskImpl;
import dyna.app.core.track.TrackerManager;
import dyna.app.net.impl.ServiceDispatcherImpl;
import dyna.app.net.impl.server.ServiceLocatorServerImpl;
import dyna.app.server.GenericServer;
import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.ConfigurableServerImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.conf.loader.ConfigLoaderServerImpl;
import dyna.common.context.AbstractSvContext;
import dyna.common.dto.Session;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.ConnectionMode;
import dyna.common.systemenum.ConnectionModeEnum;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.connection.ConnectionManager;
import dyna.net.dispatcher.DispatcherRemoteInvocationExecutor;
import dyna.net.dispatcher.ServiceDispatcher;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateManager;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.SignatureFactory;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.LIC;
import dyna.net.syncfile.download.Downloader;
import dyna.net.syncfile.transfer.TransferEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.*;

/**
 * 服务器上下文的实现
 * 
 * @author Wanglei
 * 
 */
@Service
public class ApplicationServerContextImpl extends AbstractSvContext implements ApplicationServerContext
{
	private static final long					serialVersionUID	= -5782779160183354988L;

	private static final Map<String, Long>		SESSION_UDT_MAP		= new HashMap<String, Long>();

	private ConfigurableServerImpl				svConfig			= null;

	private UserSignature						sysIntSignature		= null;
	@Autowired
	private PoolManager							poolManager			= null;
	@Autowired
	private CredentialManager					credentialManager	;
	@Autowired
	private ConnectionManager					connectionManager	= null;
	@Autowired
	private ServiceStateManager					serviceStateManager	= null;
	@Autowired
	private SchedulerManager					schedulerManager	= null;
	@Autowired
	private TrackerManager						trackerManager		= null;
	@Autowired
	private NLSManager							nlsManager			= null;
	private LicenseDaemon						licenseDaemon		= null;
	@Autowired
	private ServiceStateChangeReactor			sscReactor			= null;

	private boolean								isDebugMode			= false;

	private final List<RemoteInvocation>		invocationList		= new ArrayList<RemoteInvocation>();
	private final List<String>					invocationIPList	= new ArrayList<String>();
	private final List<Date>					invocationDateList	= new ArrayList<Date>();

	public ApplicationServerContextImpl()
	{
		super("DynaTeam Server Context");
	}

	@Override
	public void init() throws Exception
	{
		// configuration loader
		ConfigLoaderServerImpl configLoader = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4Server();
		this.svConfig = configLoader.getConfigurable();

		// load config from file
		DynaLogger.info("<" + this.svConfig.getServerId() + ": " + this.svConfig.getServerDescription() + ">");

		// license manager initialize.
		LicenseManager licenseManager = new LicenseManager();

		licenseManager.testLicenseValid(this.svConfig.getServerIP());

		this.initLicenseDaemon(licenseManager);

		// add queued task scheduler
		int threadPoolCount = ((this.svConfig.getThreadPoolCount() == null || this.svConfig.getThreadPoolCount().intValue() < 2) ? 16
				: this.svConfig.getThreadPoolCount().intValue());
		this.schedulerManager.addScheduler(SchedulerManager.MULTI_THREAD_QUEUED_TASK, new SchedulerQueuedTaskImpl(threadPoolCount));
		int scheduledThreadPoolCount = ((this.svConfig.getScheduledThreadPoolCount() == null || this.svConfig.getScheduledThreadPoolCount().intValue() < 1) ? 5
				: this.svConfig.getScheduledThreadPoolCount().intValue());
		this.schedulerManager.addScheduler(SchedulerManager.SCHEDULED_TASK, new SchedulerTimerTaskImpl(scheduledThreadPoolCount));
		
		Session ssn = DataServer.getDSCommonService().getSystemInternal();
		if (ssn.getLanguageEnum() != this.svConfig.getLanguage())
		{
			ssn.setLanguageEnum(this.svConfig.getLanguage());
			DataServer.getSystemDataService().save(ssn);
		}

		this.sysIntSignature = (UserSignature) SignatureFactory.createSignature(ssn.getUserId(), ssn.getUserName(), ssn.getUserGuid(), //
				ssn.getLoginGroupId(), ssn.getLoginGroupName(), ssn.getLoginGroupGuid(), //
				ssn.getLoginRoleId(), ssn.getLoginRoleName(), ssn.getLoginRoleGuid(), //
				ssn.getIpAddress(), ssn.getAppType(), ssn.getLanguageEnum(), ssn.getBizModelGuid(), ssn.getBizModelName());
		this.credentialManager.bind(ssn.getGuid(), this.sysIntSignature);

		// service dispatcher initialize
		this.createServiceDispatcher();

		this.nlsManager.loadStringRepository();


		if (this.isDebugMode)
		{
			getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(new RMIInvokeCheck(this), 1000, 300 * 1000);
		}
		DynaLogger.info("Server initialized successfully.");
	}

	/**
	 * remote service initialize, to dispatch service
	 */
	private void createServiceDispatcher()
	{
		if (this.svConfig.getServiceDispatchPort() == null)
		{
			return;
		}

		RmiServiceExporter exporter = new RmiServiceExporter();
		try
		{
			exporter.setAlwaysCreateRegistry(true);
			exporter.setServiceInterface(ServiceDispatcher.class);
			exporter.setServiceName("serviceDispatcher");
			exporter.setService(new ServiceDispatcherImpl(this));
			String serverIP = this.svConfig.getServerIP();
			if (!StringUtils.isNullString(serverIP))
			{
				// exporter.setRegistryHost(serverIP);
				System.setProperty("java.rmi.server.hostname", serverIP);
			}
			exporter.setRegistryPort(this.svConfig.getRmiRegistryPort());
			exporter.setServicePort(this.svConfig.getServiceDispatchPort());
			exporter.setRemoteInvocationExecutor(new DispatcherRemoteInvocationExecutor());

			exporter.afterPropertiesSet();
		}
		catch (RemoteException e)
		{
			DynaLogger.warn("failed to dispatch service, turn into pojo mode.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getSystemInternalSessionId()
	 */
	@Override
	public UserSignature getSystemInternalSignature()
	{
		return this.sysIntSignature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getPoolManager()
	 */
	@Override
	public PoolManager getPoolManager()
	{
		return this.poolManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getCredentialManager()
	 */
	@Override
	public CredentialManager getCredentialManager()
	{
		return this.credentialManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getServerConfig()
	 */
	@Override
	public ConfigurableServerImpl getServerConfig()
	{
		return this.svConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getConnectionManager()
	 */
	@Override
	public ConnectionManager getConnectionManager()
	{
		return this.connectionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.context.AbstractContext#setServiceState(dyna.common.systemenum.ServiceStateEnum)
	 */
	@Override
	public synchronized ServiceStateEnum setServiceState(ServiceStateEnum serviceState)
	{
		ServiceStateEnum newState = super.setServiceState(serviceState);
		if (newState != serviceState)
		{
			return newState;
		}

		if (newState == ServiceStateEnum.WAITING)
		{
			try
			{
				DynaLogger.info("start to refresh cache...");

				newState = super.setServiceState(ServiceStateEnum.SYNCHRONIZE);

				ConfigurableConnToDSImpl connToDS = DataServer.getRepositoryBean(ConfigLoaderFactory.class).getLoader4ConnToDS().getConfigurable();
				File iconFile = new File(EnvUtils.getRootPath() + "tmp/icon.zip");
				FileUtils.makeDirectories(iconFile.getAbsolutePath(), true);
				if (connToDS.getClientMode() == ConnectionModeEnum.DISTRIBUTED)
				{
					Downloader downloader = DataServer.getSyncFileService().getDownloader(TransferEnum.DOWNLOAD_ICON_DS);
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
				if (connToDS.getClientMode() == ConnectionModeEnum.DISTRIBUTED)
				{
					Downloader downloader = DataServer.getSyncFileService().getDownloader(TransferEnum.DOWNLOAD_REPORT_DS);
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

				DynaLogger.info("end refresh cache.");
			}
			catch (Exception e)
			{
				DynaLogger.error("error occurs during refresh cache.", e);
				return ServiceStateEnum.INVALID;
			}
		}

		if (newState != ServiceStateEnum.NORMAL)
		{
			this.getServiceStateManager().setServiceState(newState);
		}

		return newState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getServiceStateManager()
	 */
	@Override
	public ServiceStateManager getServiceStateManager()
	{
		return this.serviceStateManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#setSscReactor(dyna.net.dispatcher.sync.ServiceStateChangeReactor)
	 */
	@Override
	public void setSscReactor(ServiceStateChangeReactor sscReactor)
	{
		this.sscReactor = sscReactor;
	}

	@Override
	public ServiceStateChangeReactor getSscReactor()
	{
		return this.sscReactor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getLicenseDaemon()
	 */
	@Override
	public LicenseDaemon getLicenseDaemon()
	{
		return this.licenseDaemon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getScheduleManager()
	 */
	@Override
	public SchedulerManager getSchedulerManager()
	{
		return this.schedulerManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getTrackerManager()
	 */
	@Override
	public TrackerManager getTrackerManager()
	{
		return this.trackerManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getNLSManager()
	 */
	@Override
	public NLSManager getNLSManager()
	{
		return this.nlsManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#getSessionUpdateTime(java.lang.String)
	 */
	@Override
	public boolean shouldUpdateSessionTime(String sessionId, long updateTime)
	{
		Long ret = SESSION_UDT_MAP.get(sessionId);
		if (ret == null || updateTime - ret > 120000)
		{
			SESSION_UDT_MAP.put(sessionId, updateTime);
			return ret == null ? false : true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#removeSessionUpdateTime(java.lang.String)
	 */
	@Override
	public void removeSessionUpdateTime(String sessionId)
	{
		SESSION_UDT_MAP.remove(sessionId);
	}

	private void initLicenseDaemon(final LicenseManager licenseManager)
	{
		this.licenseDaemon = new LicenseDaemon()
		{

			@Override
			public boolean hasLicence(String moduleName)
			{
				String aProduct = "DT-" + moduleName.toUpperCase();
				String pCode = licenseManager.getProductCode(aProduct);

				if (StringUtils.isNullString(pCode))
				{
					return false;
				}

				String mCode = licenseManager.getModuleCode(aProduct);
				String vCode = licenseManager.getVersionCode(aProduct);

				List<License> licTable = licenseManager.getLicenseTable();
				int i = this.findLicense(licTable, pCode, mCode, vCode);
				if (i >= 0)
				{
					return true;
				}

				return false;
			}

			private synchronized int findLicense(String moduleName)
			{
				String aProduct = "DT-" + moduleName.toUpperCase();
				String pCode = licenseManager.getProductCode(aProduct);

				if (StringUtils.isNullString(pCode))
				{
					return -1;
				}

				String mCode = licenseManager.getModuleCode(aProduct);
				String vCode = licenseManager.getVersionCode(aProduct);

				List<License> licTable = licenseManager.getLicenseTable();
				return this.findLicense(licTable, pCode, mCode, vCode);

			}

			private synchronized int findLicense(List<License> licTable, String productCode, String moduleCode, String versionCode)
			{
				for (int i = 0; i < licTable.size(); i++)
				{
					if (licTable.get(i).testFindSlot(productCode, moduleCode, versionCode))
					{
						return i;
					}
				}
				return -1;
			}

			@Override
			public synchronized boolean requestLicense(LIC lic, String moduleName) throws LicenseException
			{
				if ("WEB".equalsIgnoreCase(moduleName))
				{
					if (!this.hasLicence(moduleName))
					{
						throw new LicenseException("No license grant for " + moduleName);
					}

					if (!this.resetLicense(lic))
					{
						throw new LicenseException("request license failed: " + moduleName);
					}

					List<License> licTable = licenseManager.getLicenseTable();
					License license = licTable.get(findLicense(moduleName));
					if (!license.increaseUse())
					{
						throw new LicenseException("Maximum user limited by license, max: " + license.getAvailable() + ", in use: " + license.getInUse());
					}

				}
				else
				{
					moduleName = "BASE";
					if (!this.hasLicence(moduleName))
					{
						throw new LicenseException("No license grant for " + moduleName);
					}

					if (!this.resetLicense(lic))
					{
						throw new LicenseException("request license failed: " + moduleName);
					}

					List<License> licTable = licenseManager.getLicenseTable();
					int webindex = findLicense("WEB");
					License weblicense = null;
					if (webindex > -1)
					{
						weblicense = licTable.get(webindex);
					}
					int webwxindex = findLicense("WEBWX");
					License webwxlicense = null;
					if (webwxindex > -1)
					{
						webwxlicense = licTable.get(webwxindex);
					}
					for (License license : licTable)
					{
						if (license != weblicense && license != webwxlicense)
						{
							if (!license.increaseUse())
							{
								throw new LicenseException("Maximum user limited by license, max: " + license.getAvailable() + ", in use: " + license.getInUse());
							}
						}
					}
				}
				return true;
			}

			@Override
			public synchronized void releaseLicense(String moduleName)
			{
				List<License> licTable = licenseManager.getLicenseTable();
				int webindex = findLicense("WEB");
				License weblicense = null;
				if (webindex > -1)
				{
					weblicense = licTable.get(webindex);
				}
				int webwxindex = findLicense("WEBWX");
				License webwxlicense = null;
				if (webwxindex > -1)
				{
					webwxlicense = licTable.get(webwxindex);
				}
				if ("WEB".equalsIgnoreCase(moduleName))
				{
					if (weblicense != null)
					{
						weblicense.decreaseUse();
					}
				}
				else
				{
					for (License license : licTable)
					{
						if (license != weblicense && license != webwxlicense)
						{
							license.decreaseUse();
						}
					}
				}
			}

			@Override
			public int[] getLicenseInUse(LIC lic) throws ServiceRequestException
			{
				this.resetLicense(lic);
				List<License> licTable = licenseManager.getLicenseTable();
				if (SetUtils.isNullList(licTable))
				{
					return new int[] { 0, 0 };
				}
				else
				{
					int webindex = findLicense("WEB");
					if (webindex > -1)
					{
						return new int[] { licTable.get(0).getInUse(), licTable.get(webindex).getInUse() };
					}
					else
					{
						return new int[] { licTable.get(0).getInUse(), 0 };
					}
				}
			}

			@Override
			public int[] getLicenseNode() throws ServiceRequestException
			{
				List<License> licTable = licenseManager.getLicenseTable();
				if (SetUtils.isNullList(licTable))
				{
					return new int[] { 0, 0 };
				}
				else
				{
					int webindex = findLicense("WEB");
					if (webindex > -1)
					{
						return new int[] { licTable.get(0).getAvailable(), licTable.get(webindex).getAvailable() };
					}
					else
					{
						return new int[] { licTable.get(0).getAvailable(), 0 };
					}
				}
			}

			@Override
			public String getLicenseModules() throws ServiceRequestException
			{
				return licenseManager.getProductList();
			}

			@Override
			public synchronized boolean resetLicense(LIC lic)
			{
				try
				{
					List<Session> occupant = lic.listLicensedOccupant();
					List<License> licTable = licenseManager.getLicenseTable();
					if (SetUtils.isNullList(occupant))
					{
						for (License license : licTable)
						{
							license.resetInUse(0);
						}
					}
					else
					{
						int size = 0;
						int websize = 0;
						for (Session oSession : occupant)
						{
							if (oSession.getAppType() == ApplicationTypeEnum.WEB)
							{
								websize++;
							}
							else
							{
								size++;
							}
						}
						int webindex = findLicense("WEB");
						License weblicense = null;
						if (webindex > -1)
						{
							weblicense = licTable.get(webindex);
						}
						int webwxindex = findLicense("WEBWX");
						License webwxlicense = null;
						if (webwxindex > -1)
						{
							webwxlicense = licTable.get(webwxindex);
						}
						for (License license : licTable)
						{
							if (weblicense == license)
							{
								license.resetInUse(websize);
							}
							else if (webwxlicense != license)
							{
								license.resetInUse(size);
							}

						}

					}
					return true;
				}
				catch (ServiceRequestException e)
				{
					DynaLogger.error(e.getMessage(), e);
				}
				return false;
			}

			@Override
			public long[] getLicensePeriod()
			{
				if (SetUtils.isNullList(licenseManager.getLicenseTable()))
				{
					return null;
				}
				else
				{
					License license = licenseManager.getLicenseTable().get(0);
					return new long[] { license.getStartDate(), license.getEndDate() };
				}
			}

			@Override
			public String getSystemIdentification()
			{
				return licenseManager.getSystemIdentification();
			}

			@Override
			public boolean isVM()
			{
				return licenseManager.isVM();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#isDebugMode()
	 */
	@Override
	public boolean isDebugMode()
	{
		return this.isDebugMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContext#setDebugMode(boolean)
	 */
	@Override
	public void setDebugMode(boolean value)
	{
		this.isDebugMode = value;
	}

	@Override
	public void startRMIRemoteInvocation(RemoteInvocation invocation, String ip)
	{
		synchronized (invocationList)
		{
			invocationList.add(invocation);
			invocationIPList.add(ip);
			invocationDateList.add(new Date());
		}

	}

	@Override
	public void finishRMIRemoteInvocation(RemoteInvocation invocation)
	{
		synchronized (invocationList)
		{
			int i = invocationList.indexOf(invocation);
			if (i > -1)
			{
				invocationList.remove(i);
				invocationIPList.remove(i);
				invocationDateList.remove(i);
			}
		}

	}

	@Override
	public List<List<Object>> listRMIRemoteInvocation()
	{
		List<List<Object>> list = new ArrayList<List<Object>>();
		synchronized (invocationList)
		{
			long x = System.currentTimeMillis() - 300000;
			for (int n = invocationDateList.size() - 1; n > -1; n--)
			{
				Date d = invocationDateList.get(n);
				if (d.getTime() < x)
				{
					List<Object> row = new ArrayList<Object>();
					list.add(row);
					row.add(invocationList.get(n));
					row.add(invocationIPList.get(n));
					row.add(invocationDateList.get(n));
					invocationList.remove(n);
					invocationIPList.remove(n);
					invocationDateList.remove(n);
				}
			}
		}
		return list;
	}

}
