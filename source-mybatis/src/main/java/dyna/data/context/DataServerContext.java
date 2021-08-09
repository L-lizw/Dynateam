/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServerContext
 * Wanglei 2010-12-13
 */
package dyna.data.context;

import dyna.common.bean.data.DynamicTableBean;
import dyna.common.bean.data.SystemObject;
import dyna.common.context.SvContext;
import dyna.data.cache.CacheManagerDelegate;
import dyna.net.connection.ConnectionManager;
import dyna.net.dispatcher.sync.ServiceStateManager;
import dyna.net.security.CredentialManager;
import dyna.net.service.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author Wanglei
 * 
 */
public interface DataServerContext extends SvContext
{

	/**
	 * 获取客户连接管理器
	 * 
	 * @return
	 */
	public ConnectionManager getConnectionManager();

	/**
	 * 获取服务状态管理器
	 * 
	 * @return
	 */
	public ServiceStateManager getServiceStateManager();

	/**
	 * 获取凭证管理器
	 * 
	 * @return
	 */
	public CredentialManager getCredentialManager();

	/**
	 * 获取 mybatis sqlSessionFactory
	 * 
	 * @return
	 */
	public SqlSessionFactory getSqlSessionFactory();

	/**
	 * 获取模型配置文件路径
	 * 
	 * @return
	 */
	public String getModelConfigPath();

	/**
	 * 获取classification模型配置文件路径
	 * 
	 * @return
	 */
	public String getClassificationConfigPath();

	/**
	 * 获取内部服务方法
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 */
	public <T extends Service> T getInternalService(Class<T> serviceClass);

	/**
	 * 添加内部服务, 存在则替换, 否则新增
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @param service
	 */
	public void putInternalService(Class<?> serviceClass, Object service);

	/**
	 * 取得通过包扫描得到的缓存类信息
	 * 
	 * @param <T>
	 * @return
	 */
	public <T extends SystemObject> Map<String, DynamicTableBean<T>> getDynaimcTableBeanMap();

	/**
	 * 获取entry对应的接口mapper
	 * @return
	 */
	public Map<Class,Class> getEntryMapperMap();

	/**
	 * 缓存管理
	 * 
	 * @param <T>
	 * @return
	 */
	public <T extends SystemObject> CacheManagerDelegate<T> getCacheManager();

	/**
	 * 获取数据层springContext
	 * @return
	 */
	public ApplicationContext       getApplicationContext();
}
