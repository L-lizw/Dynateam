package dyna.data.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import dyna.data.cache.controller.CacheController;
import dyna.data.service.sdm.BeanFilter;
import dyna.common.bean.data.SystemObject;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;

public class DynaCacheModel<T extends SystemObject>
{
	private String				id;

	private long				lastFlush;
	private long				flushInterval;
	private static final long	NO_FLUSH_INTERVAL	= -99999;
	private static final int	MAX_OBJECT_LOG_SIZE	= 200;

	private CacheController<T> controller;

	public DynaCacheModel(String cacheId)
	{
		flushInterval = NO_FLUSH_INTERVAL;
		this.id = cacheId;
	}

	public void flush()
	{
		synchronized (this)
		{
			controller.flush(this);
			lastFlush = System.currentTimeMillis();
			if (DynaLogger.isDebugEnabled())
			{
				log("flushed", false, null);
			}
		}
	}

	// public T refresh(T data)
	// {
	// synchronized (this)
	// {
	// T value = controller.fresh(this, data);
	// if (DynaLogger.isDebugEnabled())
	// {
	// log("fresh object", true, value);
	// }
	// return value;
	// }
	// }

	public void cacheAll(List<T> dataList)
	{
		synchronized (this)
		{
			if (!SetUtils.isNullList(dataList))
			{
				Map<String, T> dataMap = new HashMap<>();
				dataList.forEach(data -> {
					dataMap.put(data.getGuid(), data);
				});
				controller.cacheAll(this, dataMap);
			}
			if (DynaLogger.isDebugEnabled())
			{
				log("load all data for ", true, this.id);
			}
		}
	}

	public T getObject(String guid)
	{
		T value = null;
		synchronized (this)
		{
			if (flushInterval != NO_FLUSH_INTERVAL && System.currentTimeMillis() - lastFlush > flushInterval)
			{
				flush();
			}

			value = controller.getObject(this, guid);
			if (DynaLogger.isDebugEnabled())
			{
				if (value != null)
				{
					log("retrieved object", true, value);
				}
				else
				{
					log("cache miss", false, null);
				}
			}
		}
		return value;
	}

	public T getObject(BeanFilter<T> beanFilter)
	{
		List<T> dataList = this.listObject(beanFilter);
		return SetUtils.isNullList(dataList) ? null : dataList.get(0);
	}

	public List<T> listObject(BeanFilter<T> beanFilter)
	{
		return controller.listObject(this, beanFilter);
	}

	public T putObject(String key, T value)
	{
		synchronized (this)
		{
			T ret = controller.putObject(this, value);
			if (DynaLogger.isDebugEnabled())
			{
				log("stored object", true, value);
			}
			return ret;
		}
	}

	public T removeObject(String guid)
	{
		synchronized (this)
		{
			return (T) controller.removeObject(this, guid);
		}
	}

	public List<T> removeObject(BeanFilter<T> beanFilter)
	{
		List<T> dataList = this.listObject(beanFilter);
		if (!SetUtils.isNullList(dataList))
		{
			dataList.forEach(data -> removeObject(data.getGuid()));
		}
		return dataList;
	}

	protected void log(String action, boolean addValue, Object cacheValue)
	{
		StringBuilder output = new StringBuilder("Cache '");
		output.append(getId());
		output.append("': ");
		output.append(action);
		if (addValue)
		{
			String cacheObjectStr = (cacheValue == null ? "null" : cacheValue.toString());
			output.append(" '");
			if (cacheObjectStr.length() < MAX_OBJECT_LOG_SIZE)
			{
				output.append(cacheObjectStr);
			}
			else
			{
				output.append(cacheObjectStr, 1, MAX_OBJECT_LOG_SIZE);
				output.append("...");
			}
			output.append("'");
		}
		DynaLogger.debug(output.toString());
	}

	public void setFlushInterval(long flushInterval)
	{
		this.flushInterval = flushInterval;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public CacheController<T> getController()
	{
		return controller;
	}

	public void setController(CacheController<T> controller)
	{
		this.controller = controller;
	}
}
