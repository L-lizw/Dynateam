/**
 * Copyright(C) DCIS 版权所有。
 * 功能描述：data service for system beans
 * 创建标识：Xiasheng , 2010-4-22
 **/

package dyna.data.service.sdm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.configure.ProjectModel;
import dyna.common.bean.data.DynamicTableBean;
import dyna.common.bean.data.SystemObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.cache.CacheManagerDelegate;
import dyna.data.cache.event.AddCacheEventListener;
import dyna.data.cache.event.RemoveCacheEventListener;
import dyna.data.cache.event.UpdateCacheEventListener;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataNormalException;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

/**
 * 系统数据服务SystemDataService的实现
 *
 * @author xiasheng
 */
public class SystemDataServiceImpl extends DataRuleService implements SystemDataService
{
	private static boolean								initialized	= false;

	private CacheManagerDelegate<SystemObject>			cacheManager;
	private SDSOperateDataStub							operateDataStub;
	private SDSCommonStub								commonStub;

	private Map<String, DynamicTableBean<SystemObject>>	tableBeanMap;

	private Map<Class,Class>                            entryMapperMap;

	public SystemDataServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	@Override
	protected void init()
	{
		if (initialized)
		{
			return;
		}

		this.cacheManager = this.serviceContext.getCacheManager();
		try
		{
			this.cacheManager.init();
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}

		initialized = true;
	}

	public CacheManagerDelegate<SystemObject> getCacheDelegate()
	{
		if (this.cacheManager == null)
		{
			this.cacheManager = this.serviceContext.getCacheManager();
		}
		return this.cacheManager;
	}

	public Map<String, DynamicTableBean<SystemObject>> getTableBeanMap()
	{
		if (this.tableBeanMap == null)
		{
			this.tableBeanMap = this.serviceContext.getDynaimcTableBeanMap();
		}
		return this.tableBeanMap;
	}

	public Map<Class, Class> getEntryMapperMap()
	{
		if (this.entryMapperMap == null)
		{
			this.entryMapperMap = this.serviceContext.getEntryMapperMap();
		}
		return this.entryMapperMap;
	}

	public SDSOperateDataStub getOperateDataStub()
	{
		if (this.operateDataStub == null)
		{
			this.operateDataStub = new SDSOperateDataStub(this.serviceContext, this);
		}
		return operateDataStub;
	}

	public SDSCommonStub getCommonStub()
	{
		if (this.commonStub == null)
		{
			this.commonStub = new SDSCommonStub(this.serviceContext, this);
		}
		return commonStub;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#delete(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends SystemObject> boolean delete(Class<T> systemClass, String guid) throws DynaDataException
	{
		boolean result = this.getOperateDataStub().delete(systemClass, guid);
		if (this.isBeanCache(systemClass) && result)
		{
			this.addRemoveCacheEventListener(systemClass, guid);
		}
		return result;
	}

	@Override
	public <T extends SystemObject> int deleteBy(Class<T> systemClass, Map<String, Object> paramMap)
	{
		int result = this.getOperateDataStub().delete(systemClass, paramMap);

		UpperKeyMap map = new UpperKeyMap();
		map.putAll(paramMap);
		if (this.isBeanCache(systemClass) && result != 0)
		{
			this.addRemoveCacheEventListener(systemClass, (FieldValueEqualsFilter<SystemObject>) new FieldValueEqualsFilter<T>(map));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#delete(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T extends SystemObject> int delete(Class<T> systemClass, Map<String, Object> paramMap) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : delete: " + systemClass.getName() + ",parameter=" + paramMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().delete(systemClass, paramMap);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#delete(java.lang.Class, java.util.Map, java.lang.String)
	 */
	@Override
	public <T extends SystemObject> int delete(Class<T> systemClass, Map<String, Object> paramMap, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : delete: " + systemClass.getName() + ",parameter=" + paramMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().delete(systemClass, paramMap, sqlStatementId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#delete(T)
	 */
	@Override
	public <T extends SystemObject> boolean delete(T systemObject) throws DynaDataException
	{
		boolean result = this.getOperateDataStub().delete(systemObject);
		// 数据库已经没有的数据缓存中同步也不会有，无需删除
		if (this.isBeanCache(systemObject.getClass()) && result)
		{
			this.addRemoveCacheEventListener(systemObject.getClass(), systemObject.getGuid());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#deploy(java.lang.String)
	 */
	@Override
	public ProjectModel deploy(String sessionId, ProjectModel projectModel) throws SQLException
	{
		// TODO
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#query(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : query: " + systemClass.getName() + ",parameter=" + searchConditionMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().query(systemClass, searchConditionMap);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#query(java.lang.Class, java.util.Map, java.lang.String)
	 */
	@Override
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : query: " + systemClass.getName() + ",parameter=" + searchConditionMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().query(systemClass, searchConditionMap, sqlStatementId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#query(java.lang.Class, java.util.Map, java.lang.String, boolean)
	 */
	@Override
	public <T extends SystemObject> List<T> query(Class<T> systemClass, Map<String, Object> searchConditionMap, String orderby, boolean isAscOrder) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : query: " + systemClass.getName() + ",parameter=" + searchConditionMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().query(systemClass, searchConditionMap, orderby, isAscOrder);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#queryObject(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T extends SystemObject> T queryObject(Class<T> systemClass, Map<String, Object> searchConditionMap) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : queryObject: " + systemClass.getName() + ",parameter=" + searchConditionMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().queryObject(systemClass, searchConditionMap);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#queryObject(java.lang.Class, java.util.Map, java.lang.String)
	 */
	@Override
	public <T extends SystemObject> T queryObject(Class<T> systemClass, Map<String, Object> searchConditionMap, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : queryObject: " + systemClass.getName() + ",parameter=" + searchConditionMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().queryObject(systemClass, searchConditionMap, sqlStatementId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#save(dyna.common.bean.data.SystemObject)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SystemObject> String save(T systemObject) throws DynaDataException
	{
		String ret = this.getOperateDataStub().save(systemObject);
		if (StringUtils.isGuid(ret))
		{
			systemObject.setGuid(ret);
		}

		if (this.isBeanCache(systemObject.getClass()))
		{
			T data = (T) this.getDirectly(systemObject.getClass(), systemObject.getGuid());
			this.addRefreshCacheEventListener(systemObject.getClass(), data, StringUtils.isGuid(ret));
		}

		return ret;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#save(dyna.common.bean.data.SystemObject, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SystemObject> String save(T systemObject, String sqlStatementId) throws DynaDataException
	{
		String save = this.getOperateDataStub().save(systemObject, sqlStatementId);
		if (StringUtils.isGuid(save))
		{
			systemObject.setGuid(save);
		}

		if (this.isBeanCache(systemObject.getClass()))
		{
			T data = (T) this.getDirectly(systemObject.getClass(), systemObject.getGuid());
			this.addRefreshCacheEventListener(systemObject.getClass(), data, StringUtils.isGuid(save));
		}

		return save;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#save(dyna.common.bean.data.SystemObject, java.lang.String)
	 */
	@Override
	public String save(Map<String, Object> parameterMap, String sqlStatementId, boolean isSaveDynamic) throws DynaDataException
	{
		return this.getOperateDataStub().save(parameterMap, sqlStatementId, isSaveDynamic);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#save(dyna.common.bean.data.SystemObject, java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends SystemObject> String save(T systemObject, String insertId, String updateId) throws DynaDataException
	{
		String save = this.getOperateDataStub().save(systemObject, insertId, updateId);
		if (StringUtils.isGuid(save))
		{
			systemObject.setGuid(save);
		}

		if (this.isBeanCache(systemObject.getClass()))
		{
			T data = (T) this.getDirectly(systemObject.getClass(), systemObject.getGuid());
			this.addRefreshCacheEventListener(systemObject.getClass(), data, StringUtils.isGuid(save));
		}
		return save;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#update(java.lang.Class, java.util.Map, java.lang.String)
	 */
	@Override
	public <T extends SystemObject> int update(Class<T> systemClass, Map<String, Object> paramMap, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : queryObject: " + systemClass.getName() + ",parameter=" + paramMap + ",has Data Cache", null);
		}
		return this.getOperateDataStub().update(systemClass, paramMap, sqlStatementId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SystemObject> String save(T systemObject, boolean isUpdate) throws DynaDataException
	{
		if (!isUpdate)
		{
			systemObject.clear(SystemObject.GUID);
		}

		String save = this.getOperateDataStub().save(systemObject, null, isUpdate);
		if (StringUtils.isGuid(save))
		{
			systemObject.setGuid(save);
		}

		if (this.isBeanCache(systemObject.getClass()))
		{
			T data = (T) this.getOperateDataStub().getObject(systemObject.getClass(), systemObject.getGuid(), null);
			this.addRefreshCacheEventListener(systemObject.getClass(), data, StringUtils.isGuid(save));
		}
		return save;
	}

	@Override
	public List<String> executeQueryBySql(String sql) throws DynaDataException
	{
		return this.getCommonStub().executeQueryBySql(sql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SystemObject> T get(Class<T> class1, String guid)
	{
		if (!this.isBeanCache(class1))
		{
			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put(SystemObject.GUID, guid);
			return this.getOperateDataStub().queryObject(class1, searchConditionMap);
		}
		else
		{
			return (T) this.getCacheDelegate().getCacheModel(class1.getName()).getObject(guid);
		}
	}

	/**
	 * 直接从数据库查询，不查询缓存数据
	 *
	 * @param <T>
	 * @param class1
	 * @param guid
	 * @return
	 */
	public <T extends SystemObject> T getDirectly(Class<T> class1, String guid)
	{
		if (!this.isBeanCache(class1))
		{
			Map<String, Object> searchConditionMap = new HashMap<>();
			searchConditionMap.put(SystemObject.GUID, guid);
			return this.getOperateDataStub().queryObject(class1, searchConditionMap);
		}
		return this.getOperateDataStub().getObject(class1, guid, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SystemObject> T findInCache(Class<T> class1, BeanFilter<T> filter)
	{
		if (!this.isBeanCache(class1))
		{
			throw new DynaDataNormalException("Illegally called on method: listFromCache: " + class1.getName(), null);
		}
		return (T) this.getCacheDelegate().getCacheModel(class1.getName()).getObject((BeanFilter<SystemObject>) filter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SystemObject> List<T> listFromCache(Class<T> class1, BeanFilter<T> filter)
	{
		if (!this.isBeanCache(class1))
		{
			throw new DynaDataNormalException("Illegally called on method: listFromCache: " + class1.getName(), null);
		}
		return (List<T>) this.getCacheDelegate().getCacheModel(class1.getName()).listObject((BeanFilter<SystemObject>) filter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SystemObject> T getObject(Class<T> systemClass, String guid) throws DynaDataException
	{
		if (!this.isBeanCache(systemClass))
		{
			Map<String, Object> filter = new HashMap<>();
			filter.put("GUID", guid);
			return this.queryObject(systemClass, filter);
		}
		else
		{
			return (T) this.getCacheDelegate().getCacheModel(systemClass.getName()).getObject(guid);
		}
	}

	@Override
	public <T extends SystemObject> T getObjectDirectly(Class<T> systemClass, String guid, String sqlId) throws DynaDataException
	{
		return this.getOperateDataStub().getObject(systemClass, guid, sqlId);
	}

	@Override
	public <T extends SystemObject> List<T> listObjectDirectly(Class<T> systemClass) throws DynaDataException
	{
		return this.getOperateDataStub().query(systemClass, null, "selectForLoad");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SystemObject> void reloadAllCache(Class<T> class1) throws DynaDataException
	{
		if (!this.isBeanCache(class1))
		{
			throw new DynaDataNormalException("Illegally called on method: reloadAllCache: " + class1.getName(), null);
		}

		List<T> dataList = null;
		try
		{
			dataList = DataServer.getSystemDataService().listObjectDirectly(class1);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("select() selectStatement = " + class1.getName() + ".selectForLoad", e, DataExceptionEnum.SDS_SELECT);
		}
		this.getCacheDelegate().getCacheModel(class1.getName()).cacheAll((List<SystemObject>) dataList);
	}

	@Override
	public <T extends SystemObject> void deleteFromCache(Class<T> class1, BeanFilter<T> filter) throws DynaDataException
	{
		List<T> delList = this.listFromCache(class1, filter);
		if (!SetUtils.isNullList(delList))
		{
			for (T t : delList)
			{
				this.getOperateDataStub().delete(class1, t.getGuid());
			}

			this.addRemoveCacheEventListener(class1, (BeanFilter<SystemObject>) filter);
		}
	}

	public boolean isBeanCache(Class<?> clz)
	{
		return this.getTableBeanMap().containsKey(clz.getName());
	}

	private <T extends SystemObject> void addRefreshCacheEventListener(Class<?> clz, T data, boolean isNew)
	{
		if (isNew)
		{
			this.getCacheDelegate().addExecuteListener(new AddCacheEventListener<SystemObject>(data)
			{
				@Override
				public List<SystemObject> addToCache() throws ServiceRequestException
				{
					SystemObject cacheData = getCacheData();
					SystemObject refresh = getCacheDelegate().getCacheModel(clz.getName()).putObject(cacheData.getGuid(), cacheData);
					List<SystemObject> dataList = new ArrayList<>();
					dataList.add(refresh);
					return dataList;
				}
			});
		}
		else
		{
			this.getCacheDelegate().addExecuteListener(new UpdateCacheEventListener<SystemObject>(data)
			{
				@Override
				public List<SystemObject> updateCache()
				{
					SystemObject cacheData = getCacheData();
					SystemObject refresh = getCacheDelegate().getCacheModel(clz.getName()).putObject(cacheData.getGuid(), cacheData);
					List<SystemObject> dataList = new ArrayList<>();
					dataList.add(refresh);
					return dataList;
				}
			});
		}
		this.notifyRefreshCacheListeners();
	}

	private <T extends SystemObject> void addRemoveCacheEventListener(Class<?> clz, String guid)
	{
		this.getCacheDelegate().addExecuteListener(new RemoveCacheEventListener<SystemObject>(guid)
		{
			@Override
			public List<SystemObject> removeCache()
			{
				SystemObject remove = getCacheDelegate().getCacheModel(clz.getName()).removeObject(getRemoveGuid());
				List<SystemObject> dataList = new ArrayList<>();
				dataList.add(remove);
				return dataList;
			}
		});
		this.notifyRefreshCacheListeners();
	}

	private <T extends SystemObject> void addRemoveCacheEventListener(Class<?> clz, BeanFilter<SystemObject> filter)
	{
		this.getCacheDelegate().addExecuteListener(new RemoveCacheEventListener<SystemObject>(filter)
		{
			@Override
			public List<SystemObject> removeCache()
			{
				return getCacheDelegate().getCacheModel(clz.getName()).removeObject(getBeanFilter());
			}
		});
		this.notifyRefreshCacheListeners();
	}

	@Override
	public void notifyRefreshCacheListeners()
	{
		// 当操作不在事务中时，马上执行listener，否则在事务结束时通知
		if (DataServer.getTransactionManager().getCountOfNotCommitTranscation() == 0)
		{
			DynaLogger.debug("Notify CacheRefreshListener.");
			this.getCacheDelegate().notifyListeners();
		}
	}

	@Override
	public void clearRefreshCacheListeners()
	{
		DynaLogger.debug("Clear CacheRefreshListener.");
		this.getCacheDelegate().clearEventListener();
	}

	@Override
	public <T extends SystemObject> int insertBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : queryObject: " + systemClass.getName() + ",parameter=" + paramList + ",has Data Cache", null);
		}
		return this.getOperateDataStub().insertBatch(systemClass, paramList, sqlStatementId);
	}

	@Override
	public <T extends SystemObject> int updateBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : queryObject: " + systemClass.getName() + ",parameter=" + paramList + ",has Data Cache", null);
		}
		return this.getOperateDataStub().updateBatch(systemClass, paramList, sqlStatementId);
	}

	@Override
	public <T extends SystemObject> int deleteBatch(Class<T> systemClass, List<? extends Map<String, Object>> paramList, String sqlStatementId) throws DynaDataException
	{
		if (this.isBeanCache(systemClass))
		{
			throw new DynaDataNormalException("Illegally called on method : queryObject: " + systemClass.getName() + ",parameter=" + paramList + ",has Data Cache", null);
		}
		return this.getOperateDataStub().deleteBatch(systemClass, paramList, sqlStatementId);
	}
}
