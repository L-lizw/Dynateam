package dyna.common.cache;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.exception.ServiceRequestException;

public abstract class AbstractCacheInfo extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private boolean				removeErrData		= false;

	public <T extends SystemObject> void register(Class<T> clz, String relationkey, AbstractCacheInfo parent, CacheRefreshListener<T> listener)
	{
		DynaObserverMediator.getInstance().register(clz, parent.getGuid(), relationkey, new DynaCacheObserver<T>(parent, listener));
	}

	public abstract void register();

	public abstract <T extends SystemObject> void addToCache(T data) throws ServiceRequestException;

	public abstract <T extends SystemObject> void removeFromCache(T data) throws ServiceRequestException;

	public abstract <T extends SystemObject> void updateToCache(T data) throws ServiceRequestException;

	public boolean isRemoveErrData()
	{
		return removeErrData;
	}

	public void setRemoveErrData(boolean removeErrData)
	{
		this.removeErrData = removeErrData;
	}
}
