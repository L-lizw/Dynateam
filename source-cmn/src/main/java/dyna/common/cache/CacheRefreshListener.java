package dyna.common.cache;

import dyna.common.bean.data.SystemObject;
import dyna.common.exception.ServiceRequestException;

public class CacheRefreshListener<T extends SystemObject>
{
	public void notifyListener(AbstractCacheInfo parent, T data, String type) throws ServiceRequestException
	{
		if (CacheConstants.CHANGE_TYPE.DELETE.equals(type))
		{
			parent.removeFromCache(data);
		}
		else if (CacheConstants.CHANGE_TYPE.INSERT.equals(type))
		{
			parent.addToCache(data);
		}
		else if (CacheConstants.CHANGE_TYPE.UPDATE.equals(type))
		{
			parent.updateToCache(data);
		}
	}
}
