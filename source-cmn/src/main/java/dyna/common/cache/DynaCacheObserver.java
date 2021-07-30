package dyna.common.cache;

import dyna.common.bean.data.SystemObject;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class DynaCacheObserver<T extends SystemObject>
{
	private AbstractCacheInfo		parent;

	private CacheRefreshListener<T>	listener;

	public DynaCacheObserver(AbstractCacheInfo parent, CacheRefreshListener<T> listener)
	{
		this.parent = parent;
		this.listener = listener;
	}

	public CacheRefreshListener<T> getListener()
	{
		return this.listener;
	}

	public AbstractCacheInfo getParent()
	{
		return this.parent;
	}
}
