package dyna.data.cache.controller;

import dyna.data.service.sdm.BeanFilter;
import dyna.common.bean.data.DynaObjectImpl;
import dyna.common.bean.data.SystemObject;
import dyna.common.util.SetUtils;
import dyna.data.cache.DynaCacheModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynaCacheController<T extends SystemObject> implements CacheController<T>
{
	private final Map<String, Map<String, T>> cache = new HashMap<>();

	@Override
	public void flush(DynaCacheModel<T> cacheModel)
	{
		cache.remove(cacheModel.getId());
	}

	// @Override
	// public T fresh(DynaCacheModel<T> cacheModel, T object)
	// {
	// cache.get(cacheModel.getId()).put(object.getGuid(), object);
	// return object;
	// }

	@Override
	public void cacheAll(DynaCacheModel<T> cacheModel, Map<String, T> dataMap)
	{
		this.flush(cacheModel);
		if (!SetUtils.isNullMap(dataMap))
		{
			cache.put(cacheModel.getId(), dataMap);
		}
		else
		{
			cache.put(cacheModel.getId(), new HashMap<String, T>());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject(DynaCacheModel<T> cacheModel, String guid)
	{
		Map<String, T> map = cache.get(cacheModel.getId());
		if (map == null)
		{
			return null;
		}
		return map.get(guid) == null ? null : (T) map.get(guid).clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> listObject(DynaCacheModel<T> cacheModel, BeanFilter<T> filter)
	{
		List<T> beanList = new ArrayList<T>();
		if (filter == null)
		{
			if (!SetUtils.isNullMap(this.cache.get(cacheModel.getId())))
			{
				for (Map.Entry<String, T> entry : this.cache.get(cacheModel.getId()).entrySet())
				{
					T value = entry.getValue();
					if (value != null)
					{
						beanList.add((T) value.clone());
					}
				}
			}
			return beanList;
		}
		else
		{
			if (!SetUtils.isNullMap(this.cache.get(cacheModel.getId())))
			{
				for (Map.Entry<String, T> entry : this.cache.get(cacheModel.getId()).entrySet())
				{
					T value = entry.getValue();
					if (filter.match(value))
					{
						beanList.add((T) value.clone());
					}
				}
			}
			return beanList;
		}
	}

	@Override
	public T removeObject(DynaCacheModel<T> cacheModel, String guid)
	{
		Map<String, T> map = cache.get(cacheModel.getId());
		if (map == null)
		{
			return null;
		}
		T data = map.get(guid);
		cache.get(cacheModel.getId()).remove(guid);
		return data;
	}

	@Override
	public T putObject(DynaCacheModel<T> cacheModel, T object)
	{
		Map<String, T> data = cache.get(cacheModel.getId());
		if (data == null)
		{
			data = new HashMap<String, T>();
			cache.put(cacheModel.getId(), data);
		}
		if (data.get(object.getGuid()) == null)
		{
			data.put(object.getGuid(), object);
		}
		else
		{
			((DynaObjectImpl) data.get(object.getGuid())).putAll((DynaObjectImpl) object);
		}
		cache.put(cacheModel.getId(), data);
		return object;
	}
}
