package dyna.data.cache.event;

import dyna.data.service.sdm.BeanFilter;
import dyna.common.bean.data.SystemObject;

import java.util.List;

public abstract class RemoveCacheEventListener<T extends SystemObject> extends EventListener<T>
{
	private BeanFilter<T> filter;

	private String guid;

	public RemoveCacheEventListener(BeanFilter<T> filter)
	{
		this.filter = filter.clone();
	}

	public RemoveCacheEventListener(String guid)
	{
		this.guid = guid;
	}

	@Override
	public List<T> execute()
	{
		return this.removeCache();
	}

	public BeanFilter<T> getBeanFilter()
	{
		return this.filter;
	}

	public String getRemoveGuid()
	{
		return this.guid;
	}

	public abstract List<T> removeCache();
}
