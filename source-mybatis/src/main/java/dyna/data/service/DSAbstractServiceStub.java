/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStub 服务分支
 * Wanglei 2011-3-24
 */
package dyna.data.service;

import dyna.common.dtomapper.BomObjectMapper;
import dyna.common.dtomapper.DynaObjectMapper;
import dyna.common.dtomapper.RelationobjectMapper;
import dyna.common.dtomapper.model.cls.ClassificationModelMapper;
import dyna.data.SqlExecuteInvocationHandler;
import dyna.data.cache.CacheManagerDelegate;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 服务分支
 *
 * @author Wanglei
 */
public abstract class DSAbstractServiceStub<T extends DataRuleService> extends DataAbstractServiceStub<T>
{
	public static final String ESCAPE_CHAR = "/";

	protected static final boolean ISCHECKACL = true;

	private static SqlExecuteInvocationHandler executeHandler = null;

	@Autowired protected DynaObjectMapper          dynaObjectMapper;
	@Autowired protected RelationobjectMapper      relationobjectMapper;
	@Autowired protected BomObjectMapper           bomObjectMapper;
	@Autowired protected ClassificationModelMapper classificationModelMapper;
	@Autowired protected CacheManagerDelegate      cacheManagerDelegate;

	protected final SqlSessionFactory sqlSessionFactory = null;

	protected DSAbstractServiceStub()
	{
		super();
		if (executeHandler == null)
		{
			executeHandler = new SqlExecuteInvocationHandler();
		}
		//TODO
//		executeHandler.setExecuteClient(serviceContext.getSqlSessionFactory());
//		this.sqlSessionFactory = (SqlSessionFactory) Proxy
//				.newProxyInstance(serviceContext.getSqlSessionFactory().getClass().getClassLoader(), serviceContext.getSqlSessionFactory().getClass().getInterfaces(),
//						executeHandler);
	}
}
