/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServerContextImpl
 * Wanglei 2010-12-13
 */
package dyna.data.context;

import dyna.common.Version;
import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.DynamicTableBean;
import dyna.common.bean.data.SystemObject;
import dyna.common.context.AbstractSvContext;
import dyna.common.dtomapper.DynaObjectMapper;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.common.util.AnnotationUtil;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.cache.CacheManagerDelegate;
import dyna.dbcommon.function.DatabaseFunctionFactory;
import dyna.net.connection.ConnectionManager;
import dyna.net.dispatcher.sync.ServiceStateManager;
import dyna.net.security.CredentialManager;
import dyna.net.service.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Wanglei
 */
@Repository @SuppressWarnings({ "rawtypes" })
public class DataServerContextImpl extends AbstractSvContext implements DataServerContext
{
	/**
	 *
	 */
	private static final long serialVersionUID = -992145370055197280L;

	private static final String SCAN_BEAN_ANNOTATION_PACKAGE = "dyna.common.dto";

	private static final String MODEL_CONFIG_PATH = EnvUtils.getConfRootPath() + "conf/om";

	private static final String              CLASSIFICATION_CONFIG_PATH = EnvUtils.getConfRootPath() + "conf/cf";

	private   Map<String, DynamicTableBean> dynamicTableBeanMap  = null;

	private   Map<Class, Class> entryToDaoMapper = null;

	@Autowired
	private   DynaObjectMapper    dynaObjectMapper           = null;
	@Autowired
	private   CredentialManager   credentialManager          = null;
	@Autowired
	private   ConnectionManager   connectionManager          = null;
	@Autowired
	private   ServiceStateManager serviceStateManager        = null;
	@Autowired
	private SqlSessionFactory     sqlSessionFactory = null;
	@Autowired
	private CacheManagerDelegate          cacheManagerDelegate = null;

	public DataServerContextImpl() throws IOException
	{
		super("DataServer");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.common.context.AbstractContext#init()
	 */
	@Override public void init() throws Exception
	{
		DynaLogger.info("***************************************************");
		DynaLogger.info("************* " + Version.getProductName() + " " + Version.getVersionInfo() + "**********");
		DynaLogger.info("************* " + Version.getCopyRight() + " ****************");
		DynaLogger.info("***************************************************");
		DynaLogger.info("Data Server initialize...");
		DynaLogger.print("\tConnecting to Database...");
		// mapper initialize.
		String configFile = "dm/mybatis-config.xml";
		try
		{
			dynaObjectMapper.pingQuery();
		}
		catch (Exception e)
		{
			DynaLogger.println("[FAILED]" + e.getMessage());
			throw new Exception(e.getMessage());
		}

		DatabaseFunctionFactory.databaseType = this.sqlSessionFactory.getConfiguration().getDatabaseId();
		DynaLogger.println("[OK]");
		this.loadTableData();

		this.loadEntryDaoInfo();
	}

	@Override public SqlSessionFactory getSqlSessionFactory()
	{
		return this.sqlSessionFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#getCredentialManager()
	 */
	@Override public CredentialManager getCredentialManager()
	{
		return this.credentialManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#getModelConfigPath()
	 */
	@Override public String getModelConfigPath()
	{
		return MODEL_CONFIG_PATH;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#getModelConfigPath()
	 */
	@Override public String getClassificationConfigPath()
	{
		return CLASSIFICATION_CONFIG_PATH;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#getInternalService(java.lang.Class)
	 */
	@SuppressWarnings("unchecked") @Override public <T extends Service> T getInternalService(Class<T> serviceClass)
	{
		return (T) this.getAttribute(serviceClass.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#addInternalService(java.lang.Class, dyna.net.service.Service)
	 */
	@Override public void setInternalService(Class<?> serviceClass, Object service)
	{
		this.setAttribute(serviceClass.getName(), service);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#getConnectionManager()
	 */
	@Override public ConnectionManager getConnectionManager()
	{
		return this.connectionManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.context.DataServerContext#getServiceStateManager()
	 */
	@Override public ServiceStateManager getServiceStateManager()
	{
		return this.serviceStateManager;
	}

	@Override public ApplicationContext getApplicationContext()
	{
		return DataServer.getApplicationContext();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.common.context.AbstractContext#setServiceState(dyna.common.systemenum.ServiceStateEnum)
	 */
	@Override public synchronized ServiceStateEnum setServiceState(ServiceStateEnum serviceState)
	{
		ServiceStateEnum newState = super.setServiceState(serviceState);

		if (newState != ServiceStateEnum.NORMAL)
		{
			this.getServiceStateManager().setServiceState(newState);
		}

		return newState;
	}

	@SuppressWarnings({ "unchecked" }) private void loadTableData()
	{
		dynamicTableBeanMap = new HashMap<>();
		Set<Class<?>> set = AnnotationUtil.scanAnnotation(SCAN_BEAN_ANNOTATION_PACKAGE, Cache.class);
		if (!SetUtils.isNullSet(set))
		{
			for (Class<?> clz : set)
			{
				Cache cacheAnnotation = clz.getAnnotation(Cache.class);
				if (cacheAnnotation != null)
				{
					DynamicTableBean tableBean = new DynamicTableBean();
					tableBean.setCache(true);
					tableBean.setBeanClass(clz);
					dynamicTableBeanMap.put(clz.getName(), tableBean);
				}

			}
		}
	}

	private void loadEntryDaoInfo()
	{
		entryToDaoMapper = new HashMap<>();
		Set<Class<?>> set = AnnotationUtil.scanAnnotation("dyna.common", EntryMapper.class);
		if (!SetUtils.isNullSet(set))
		{
			for (Class<?> entryClass : set)
			{
				EntryMapper mapperClass = entryClass.getAnnotation(EntryMapper.class);
				if (mapperClass != null)
				{
					entryToDaoMapper.put(entryClass, mapperClass.value());
				}
			}
		}
	}

	@SuppressWarnings("unchecked") @Override public Map<String, DynamicTableBean> getDynaimcTableBeanMap()
	{
		return this.dynamicTableBeanMap;
	}

	@Override public Map<Class, Class> getEntryMapperMap()
	{
		return this.entryToDaoMapper;
	}

	@SuppressWarnings("unchecked") @Override public <T extends SystemObject> CacheManagerDelegate<T> getCacheManager()
	{
		return this.cacheManagerDelegate;
	}
}
