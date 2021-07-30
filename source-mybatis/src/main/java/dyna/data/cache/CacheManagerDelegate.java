package dyna.data.cache;

import dyna.data.cache.controller.DynaCacheController;
import dyna.data.cache.event.AddCacheEventListener;
import dyna.data.cache.event.EventListener;
import dyna.data.cache.event.RemoveCacheEventListener;
import dyna.data.cache.event.UpdateCacheEventListener;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataNormalException;
import dyna.data.context.DataServerContext;
import dyna.common.bean.data.DynamicTableBean;
import dyna.common.bean.data.SystemObject;
import dyna.common.cache.CacheConstants;
import dyna.common.cache.DynaObserverMediator;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManagerDelegate<T extends SystemObject>
{
	private final Map<String, DynaCacheModel<T>>      cacheModels;
	private final Map<String, DynamicTableBean<T>>    tableBeanMap;
	private final DynaCacheController<T>              controller;
	private       SqlSessionFactory                   sqlSessionFactory;
	private       DataServerContext                   context;
	private       ThreadLocal<List<EventListener<T>>> executeListeners = null;

	public CacheManagerDelegate(DataServerContext context)
	{
		this.executeListeners = new ThreadLocal<>();
		this.cacheModels = new HashMap<>();
		this.tableBeanMap = context.getDynaimcTableBeanMap();
		this.context = context;
		this.controller = new DynaCacheController<T>();
	}

	public void init() throws ServiceRequestException
	{
		this.sqlSessionFactory = context.getSqlSessionFactory();
		this.addModels();
	}

	public void addModels()
	{
		if (!SetUtils.isNullMap(tableBeanMap))
		{
			for (String clzName : tableBeanMap.keySet())
			{
				DynamicTableBean<T> tableBean = tableBeanMap.get(clzName);
				if (tableBean.isCache())
				{
					DynaCacheModel<T> cacheModel = new DynaCacheModel<T>(clzName);
					cacheModel.setController(this.controller);
					this.addCacheModel(cacheModel);
					new BatchLoadToCacheEventListener(clzName).execute();
				}
			}
		}
	}

	private void addCacheModel(DynaCacheModel<T> cacheModel)
	{
		cacheModels.put(cacheModel.getId(), cacheModel);
	}

	public DynaCacheModel<T> getCacheModel(String id)
	{
		DynaCacheModel<T> cacheModel = cacheModels.get(id);
		if (cacheModel == null)
		{
			throw new DynaDataNormalException("There is no cache model named " + id, null);
		}
		return cacheModel;
	}

	public void addExecuteListener(EventListener<T> listener)
	{
		List<EventListener<T>> eventListeners = this.executeListeners.get();
		if (eventListeners == null)
		{
			eventListeners = new ArrayList<>();
			this.executeListeners.set(eventListeners);
		}
		eventListeners.add(listener);
	}

	public void removeExecuteListener(EventListener<T> listener)
	{
		List<EventListener<T>> eventListeners = this.executeListeners.get();
		if (!SetUtils.isNullList(eventListeners))
		{
			eventListeners.remove(listener);
		}
	}

	public void notifyListeners()
	{
		List<EventListener<T>> eventListeners = this.executeListeners.get();
		if (!SetUtils.isNullList(eventListeners))
		{
			for (EventListener<T> listener : eventListeners)
			{
				List<T> dataList = listener.execute();
				if (listener instanceof RemoveCacheEventListener)
				{
					if (!SetUtils.isNullList(dataList))
					{
						for (T data : dataList)
						{
							DynaObserverMediator.getInstance().notifyAll(data, CacheConstants.CHANGE_TYPE.DELETE);
						}
					}
				}
				else if (listener instanceof AddCacheEventListener)
				{
					if (!SetUtils.isNullList(dataList))
					{
						for (T data : dataList)
						{
							DynaObserverMediator.getInstance().notifyAll(data, CacheConstants.CHANGE_TYPE.INSERT);
						}
					}
				}
				else if (listener instanceof UpdateCacheEventListener)
				{
					if (!SetUtils.isNullList(dataList))
					{
						for (T data : dataList)
						{
							DynaObserverMediator.getInstance().notifyAll(data, CacheConstants.CHANGE_TYPE.UPDATE);
						}
					}
				}
			}
		}
		this.clearEventListener();
	}

	public void clearEventListener()
	{
		List<EventListener<T>> eventListeners = this.executeListeners.get();
		if (!SetUtils.isNullList(eventListeners))
		{
			eventListeners.clear();
		}
	}

	private class BatchLoadToCacheEventListener extends AddCacheEventListener<T>
	{
		private String clzName = null;

		public BatchLoadToCacheEventListener(String clzName)
		{
			super(null);
			this.clzName = clzName;
		}

		@Override
		public List<T> addToCache() throws ServiceRequestException
		{
			return this.addAllToCache();
		}

		@SuppressWarnings("unchecked")
		private List<T> addAllToCache() throws ServiceRequestException
		{
			DynamicTableBean<T> dynamicTableBean = tableBeanMap.get(this.clzName);
			if (dynamicTableBean == null || !dynamicTableBean.isCache())
			{
				return null;
			}

			String sqlStatementId = dynamicTableBean.getBeanClass().getName() + "." + "selectForLoad";
			List<T> dataList = null;
			SqlSession sqlSession = null;
			try
			{
				sqlSession = sqlSessionFactory.openSession();
				dataList = sqlSession.selectList(sqlStatementId);
			}
			catch (Exception e)
			{
				throw new DynaDataExceptionAll("select() selectStatement = " + sqlStatementId, e, DataExceptionEnum.SDS_SELECT);
			}
			finally
			{
				sqlSession.close();
			}
			cacheModels.get(dynamicTableBean.getBeanClass().getName()).cacheAll(dataList);
			return dataList;
		}
	}
}
