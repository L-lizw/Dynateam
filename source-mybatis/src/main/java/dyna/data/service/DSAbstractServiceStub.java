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
import dyna.data.context.DataServerContext;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * 服务分支
 *
 * @author Wanglei
 */
public abstract class DSAbstractServiceStub<T extends DataRuleService> extends DataAbstractServiceStub<T>
{
	protected static final boolean ISCHECKACL = true;

	@Autowired
	protected   DynaObjectMapper            dynaObjectMapper;
	@Autowired
	protected   RelationobjectMapper        relationobjectMapper;
	@Autowired
	protected   BomObjectMapper             bomObjectMapper;
	@Autowired
	protected   ClassificationModelMapper   classificationModelMapper;

	public static final String ESCAPE_CHAR = "/";

	private static SqlExecuteInvocationHandler executeHandler = null;

	protected final SqlSessionFactory sqlSessionFactory;

	protected SqlSession sqlSession = null;

	protected final CacheManagerDelegate cacheManagerDelegate;

	protected DSAbstractServiceStub(DataServerContext context, T service)
	{
		super(context, service);
		this.cacheManagerDelegate = context.getCacheManager();
		if (executeHandler == null)
		{
			executeHandler = new SqlExecuteInvocationHandler();
		}
		executeHandler.setExecuteClient(context.getSqlSessionFactory());
		this.sqlSessionFactory = (SqlSessionFactory) Proxy
				.newProxyInstance(context.getSqlSessionFactory().getClass().getClassLoader(), context.getSqlSessionFactory().getClass().getInterfaces(), executeHandler);
	}
}
