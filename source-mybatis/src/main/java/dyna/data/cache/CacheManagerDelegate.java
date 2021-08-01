package dyna.data.cache;

import dyna.common.bean.data.DynamicTableBean;
import dyna.common.bean.data.SystemObject;
import dyna.common.cache.CacheConstants;
import dyna.common.cache.DynaObserverMediator;
import dyna.common.dtomapper.DynaCacheMapper;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.cache.controller.DynaCacheController;
import dyna.data.cache.event.AddCacheEventListener;
import dyna.data.cache.event.EventListener;
import dyna.data.cache.event.RemoveCacheEventListener;
import dyna.data.cache.event.UpdateCacheEventListener;
import dyna.data.context.DataServerContext;
import dyna.data.context.DataServerContextImpl;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataNormalException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CacheManagerDelegate<T extends SystemObject>
{
	private final Map<String, DynaCacheModel<T>>      cacheModels;
	private       Map<String, DynamicTableBean<T>>    tableBeanMap = null;
	private       Map<Class,Class>                    entryToDaoMapper = null;
	private final DynaCacheController<T>              controller;
	private       ThreadLocal<List<EventListener<T>>> executeListeners = null;
	@Autowired
	private       SqlSessionFactory                   sqlSessionFactory;

	public CacheManagerDelegate()
	{
		this.executeListeners = new ThreadLocal<>();
		this.cacheModels = new HashMap<>();
		this.controller = new DynaCacheController<T>();
	}

	public void init() throws ServiceRequestException
	{
		DataServerContext context = DataServer.getApplicationContext().getBean(DataServerContextImpl.class);
		this.tableBeanMap = context.getDynaimcTableBeanMap();
		this.entryToDaoMapper = context.getEntryMapperMap();
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

			List<T> dataList = null;
			try
			{
				Class<? extends DynaCacheMapper> mapperClass = entryToDaoMapper.get(Class.forName(this.clzName));
				dataList = DataServer.getRepositoryBean(mapperClass).selectForLoad();
			}
			catch (Exception e)
			{
				throw new DynaDataExceptionAll("select() selectStatement = " + this.clzName + "selectForLoad", e, DataExceptionEnum.SDS_SELECT);
			}
			cacheModels.get(dynamicTableBean.getBeanClass().getName()).cacheAll(dataList);
			return dataList;
		}
	}
}
