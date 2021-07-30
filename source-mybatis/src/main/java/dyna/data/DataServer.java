/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：ibatis factory
 *    创建标识：Xiasheng , 2010-3-17
 **/

package dyna.data;

import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.ConfigurableDataServerImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.systemenum.ConnectionMode;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.connection.DSRMIClient;
import dyna.data.context.DataServerContext;
import dyna.data.context.DataServerContextImpl;
import dyna.data.dispatch.ServiceLocatorDataServerImpl;
import dyna.data.service.acl.AclService;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.config.ConfigManagerService;
import dyna.data.service.ec.ECService;
import dyna.data.service.folder.FolderService;
import dyna.data.service.ins.InstanceService;
import dyna.data.service.model.businessmodel.BusinessModelService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.data.service.model.codemodel.CodeModelService;
import dyna.data.service.model.featuremodel.ClassificationFeatureService;
import dyna.data.service.model.interfacemodel.InterfaceModelService;
import dyna.data.service.model.lifecyclemodel.LifecycleModelService;
import dyna.data.service.relation.RelationService;
import dyna.data.service.sdm.SystemDataService;
import dyna.data.service.sync.SyncModelService;
import dyna.data.service.tool.DSToolService;
import dyna.data.service.transaction.DBTransactionService;
import dyna.data.service.transaction.DataServerTransactionManager;
import dyna.data.service.transaction.DataServerTransactionManagerImpl;
import dyna.data.service.wf.WorkFlowService;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateChangeReactorDefaultImpl;
import dyna.net.impl.DataServiceProviderFactory;
import dyna.net.security.signature.Signature;
import dyna.net.spi.DataServiceLocator;
import dyna.net.syncfile.SyncFileService;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

/**
 * 提供数据服务
 * 
 * @author xiasheng
 */
public class DataServer
{
	private static DataServer				dataServer	= null;

	private DSRMIClient client = null;

	private DataServerContext context = null;

	private ServiceStateChangeReactor		dsscReactor	= null;

	private DataServerTransactionManager manager = null;

	private static ApplicationContext applicationContext = null;

	public static synchronized void createDataServer(ServiceStateChangeReactor sscReactor) throws Exception
	{

		if (dataServer == null)
		{
			dataServer = new DataServer(sscReactor);
			dataServer.initialize();
//			applicationContext = new AnnotationConfigApplicationContext(SpringConfigForData.class);
		}
	}

	public static void createDataServer() throws Exception
	{
		createDataServer(new ServiceStateChangeReactorDefaultImpl());
	}

	public static ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	public static void close() throws Exception
	{
		if (dataServer.client != null)
		{
			dataServer.client.close();
		}
	}

	protected DataServer(ServiceStateChangeReactor dsscReactor)
	{
		try
		{
			this.context = new DataServerContextImpl();
			this.dsscReactor = dsscReactor;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	protected void initialize() throws Exception
	{
		DataServiceLocator dataServiceLocator = null;

		ConfigurableConnToDSImpl conToDsConfig = ConfigLoaderFactory.getLoader4ConnToDS().load();
		this.manager = new DataServerTransactionManagerImpl();

		if (conToDsConfig.getClientMode() == ConnectionMode.BUILT_IN_SERVER)
		{
			ConfigurableDataServerImpl dsConfig = ConfigLoaderFactory.getLoader4DataServer().load();
			this.context.init();

			dataServiceLocator = new ServiceLocatorDataServerImpl(this.context, dsConfig);
			dataServiceLocator.getServiceStateSync().setReactor(this.dsscReactor);

			DataServiceProviderFactory.createServiceProvider(dataServiceLocator);
		}
		else
		{
			this.client = new DSRMIClient(Signature.MODULE_APP_SERVER, conToDsConfig);
			this.client.setSscReactor(this.dsscReactor);
			this.client.setTransactionManager(this.manager);
			this.client.open();
		}
	}

	public static WorkFlowService getWorkFlowService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(WorkFlowService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	/**
	 * 获取SystemDataService服务
	 * 
	 * @return SystemDataService实例
	 */
	public static SystemDataService getSystemDataService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(SystemDataService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing System Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_SDS);
		}
	}

	public static SyncFileService getSyncFileService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(SyncFileService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing SyncFileService Error: ", null, DataExceptionEnum.DATASERVER_INIT_SDS);
		}
	}

	public static SyncModelService getSyncModelService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(SyncModelService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing SyncFileService Error: ", null, DataExceptionEnum.DATASERVER_INIT_SDS);
		}
	}

	/**
	 * 获取TrasactionService服务,服务器内部使用
	 * 
	 * @return TrasactionService
	 */
	public static DBTransactionService getTransactionService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(DBTransactionService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing SyncFileService Error: ", null, DataExceptionEnum.DATASERVER_INIT_TS);
		}
	}

	public static AclService getAclService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(AclService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static InstanceService getInstanceService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(InstanceService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}
	}

	public static DSToolService getToolService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(DSToolService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static ECService getECService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(ECService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static RelationService getRelationService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(RelationService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static ConfigManagerService getConfigManagerService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(ConfigManagerService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static ClassModelService getClassModelService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(ClassModelService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static BusinessModelService getBusinessModelService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(BusinessModelService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static InterfaceModelService getInterfaceModelService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(InterfaceModelService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static LifecycleModelService getLifecycleModelService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(LifecycleModelService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static CodeModelService getCodeModelService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(CodeModelService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static FolderService getFolderService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(FolderService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	/**
	 * 获取DataService服务和SystemDataService的服务器
	 * 
	 * @return DataService实例
	 */
	public static DSCommonService getDSCommonService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(DSCommonService.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	public static ClassificationFeatureService getClassificationFeatureService()
	{
		try
		{
			return DataServiceProviderFactory.getServiceProvider().getServiceInstance(ClassificationFeatureService.class);
		}
		catch (ServiceNotFoundException e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("Initializing Data Server Error: ", null, DataExceptionEnum.DATASERVER_INIT_DS);
		}

	}

	/**
	 * 获取TrasactionService服务,服务器内部使用
	 * 
	 * @return TrasactionService
	 */
	public static DataServerTransactionManager getTransactionManager()
	{
		if (dataServer == null)
		{
			return null;
		}
		else
		{
			return dataServer.manager;
		}
	}
}