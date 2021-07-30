package dyna.common.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;

public abstract class AppServerCacheInfo extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long								serialVersionUID	= 1L;

	protected ThreadLocal<List<HashMap<String, Object>>>	todoItemList		= null;

	public AppServerCacheInfo()
	{
		this.todoItemList = new ThreadLocal<List<HashMap<String, Object>>>();
	}

	public <T extends SystemObject> void registerToDoItem(T data, String type)
	{
		List<HashMap<String, Object>> todoList = this.todoItemList.get();
		if (todoList == null)
		{
			todoList = new LinkedList<HashMap<String, Object>>();
			this.todoItemList.set(todoList);
		}
		HashMap<String, Object> todoItem = new HashMap<String, Object>();
		todoItem.put(type, data);
		todoList.add(todoItem);
	}

	/**
	 * 添加待刷新项
	 * 
	 * @param data
	 * @param type
	 */
	public <T extends SystemObject> void registerToDoItemList(List<T> dataList, String type)
	{
		if (!SetUtils.isNullList(dataList))
		{
			for (T data : dataList)
			{
				this.registerToDoItem(data, type);
			}
		}
	}

	/**
	 * 执行缓存刷新
	 * 
	 * @throws ServiceRequestException
	 */
	public <T extends SystemObject> void executeToDoItem() throws ServiceRequestException
	{
		List<HashMap<String, Object>> todoList = this.todoItemList.get();

		if (todoList != null)
		{
			for (Map<String, Object> todoItem : todoList)
			{
				Map.Entry<String, Object> entry = todoItem.entrySet().iterator().next();
				String type = entry.getKey();
				Object data = entry.getValue();

				if (CacheConstants.CHANGE_TYPE.DELETE.equals(type))
				{
					this.removeFromCache(data);
				}
				else if (CacheConstants.CHANGE_TYPE.INSERT.equals(type))
				{
					this.addToCache(data);
				}
				else if (CacheConstants.CHANGE_TYPE.UPDATE.equals(type))
				{
					this.updateToCache(data);
				}
			}
			todoList.clear();
		}

	}

	public void rollBack()
	{
		List<HashMap<String, Object>> todoList = this.todoItemList.get();
		if (todoList != null)
		{
			todoList.clear();
		}
	}

	protected abstract void addToCache(Object data) throws ServiceRequestException;

	protected abstract void removeFromCache(Object data) throws ServiceRequestException;

	protected abstract void updateToCache(Object data) throws ServiceRequestException;

}
