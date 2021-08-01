/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：ibatis factory
 *    创建标识：Xiasheng , 2010-3-17
 **/

package dyna.data;

import dyna.common.exception.ServiceNotFoundException;
import dyna.common.systemenum.ConnectionModeEnum;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.data.conf.SpringConfigForData;
import dyna.data.conf.XmlConfigLoaderFactory;
import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.loader.ConfigLoaderConnToDSImpl;
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
import dyna.data.service.wf.WorkFlowService;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateChangeReactorDefaultImpl;
import dyna.net.impl.DataServiceProviderFactory;
import dyna.net.security.signature.Signature;
import dyna.net.spi.DataServiceLocator;
import dyna.net.syncfile.SyncFileService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 提供数据服务
 * 
 * @author lizw
 */
public class DataServer
{
	private static DataServer				dataServer	= null;

	private DSRMIClient client = null;

	private static DataServerContext context = null;

	private static ServiceStateChangeReactor		dsscReactor	= null;

	private static ApplicationContext applicationContext = null;

	/**
	 * 数据服务器的创建
	 * @throws Exception
	 */
	public static synchronized void createDataServer() throws Exception
	{
		if (dataServer == null)
		{
			//spring容器初始化
			applicationContext = new AnnotationConfigApplicationContext(SpringConfigForData.class);
			dataServer = new DataServer();
			context = DataServer.applicationContext.getBean(DataServerContextImpl.class);
			dsscReactor = DataServer.applicationContext.getBean(ServiceStateChangeReactorDefaultImpl.class);

			//系统自己的配置文件对应加载类的初始化
			applicationContext.getBean(XmlConfigLoaderFactory.class).init();

			//数据服务器初始化
			dataServer.initialize();
		}
	}

	/**
	 *  数据服务器初始化
	 * @throws Exception
	 */
	protected void initialize() throws Exception
	{
		DataServiceLocator dataServiceLocator = getRepositoryBean(ServiceLocatorDataServerImpl.class);

		ConfigurableConnToDSImpl conToDsConfig = getRepositoryBean(ConfigLoaderConnToDSImpl.class).getConfigurable();

		if (conToDsConfig.getClientMode() == ConnectionModeEnum.BUILT_IN_SERVER)
		{
			this.context.init();
			dataServiceLocator.getServiceStateSync().setReactor(this.dsscReactor);
			DataServiceProviderFactory.createServiceProvider(dataServiceLocator);
		}
		else
		{
			this.client = new DSRMIClient(Signature.MODULE_APP_SERVER, conToDsConfig);
			this.client.setSscReactor(this.dsscReactor);
			//TODO
//			this.client.setTransactionManager(this.manager);
			this.client.open();
		}
	}

	/**
	 * 获取数据层的spring容器
	 * @return
	 */
	public static ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	/**
	 * 获取数据层的bean实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getRepositoryBean(Class<T> clazz)
	{
		T t = applicationContext.getBean(clazz);
		return t;
	}

	public static void close() throws Exception
	{
		if (dataServer.client != null)
		{
			dataServer.client.close();
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

}