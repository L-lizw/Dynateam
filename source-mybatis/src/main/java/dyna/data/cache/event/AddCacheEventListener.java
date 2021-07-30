package dyna.data.cache.event;

import dyna.common.bean.data.SystemObject;
import dyna.common.exception.ServiceRequestException;

import java.util.List;

/**
 * @Description: 添加数据到缓存中事件监听
 * 当需要缓存的对象有新增数据时，需要在新增数据之后，添加该监听，以便当事务提交或者回滚时，同步更新缓存
 * 当更新操作在事务中时，不支持嵌套事务的缓存数据回滚
 * @author: duanll
 * @date: 2020年3月26日
 */
public abstract class AddCacheEventListener<T extends SystemObject> extends EventListener<T>
{
	private final T cacheData;

	public AddCacheEventListener(T data)
	{
		this.cacheData = data == null ? null : (T) data.clone();
	}

	@Override
	public List<T> execute()
	{
		try
		{
			return this.addToCache();
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public T getCacheData()
	{
		return this.cacheData;
	}

	/**
	 * 把数据添加到cache中
	 *
	 * @throws ServiceRequestException
	 */
	public abstract List<T> addToCache() throws ServiceRequestException;
}
