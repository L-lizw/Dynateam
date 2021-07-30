package dyna.data.cache.event;

import dyna.common.bean.data.SystemObject;

import java.util.List;

public abstract class UpdateCacheEventListener<T extends SystemObject> extends EventListener<T>
{
	private final T cacheData;

	public UpdateCacheEventListener(T cacheData)
	{
		this.cacheData = (T) cacheData.clone();
	}

	@Override
	public List<T> execute()
	{
		return this.updateCache();
	}

	public T getCacheData()
	{
		return cacheData;
	}

	public abstract List<T> updateCache();
}
