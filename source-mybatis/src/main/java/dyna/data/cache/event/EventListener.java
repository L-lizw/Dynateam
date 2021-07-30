package dyna.data.cache.event;

import java.util.List;

import dyna.common.bean.data.SystemObject;

public abstract class EventListener<T extends SystemObject>
{
	/**
	 * 监听实际执行方法
	 */
	public abstract List<T> execute();
}
