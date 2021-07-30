package dyna.data.cache.event;

import dyna.common.bean.data.SystemObject;

import java.util.List;

public abstract class RefreshCacheEventListener extends EventListener<SystemObject>
{
	@Override
	public List<SystemObject> execute()
	{
		return this.refreshCache();
	}

	public abstract List<SystemObject> refreshCache();
}
