package dyna.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
@SuppressWarnings("rawtypes")
public class DynaObserverMediator
{
	// 指定类的缓存刷新器
	private Map<Class<?>, Map<String, DynaCacheObserver>>	mediatorMap			= new HashMap<Class<?>, Map<String, DynaCacheObserver>>();

	// 指定类是通过哪一个属性和父对象guid进行关联的
	private Map<Class<?>, List<String>>						classRelationKeyMap	= new HashMap<Class<?>, List<String>>();

	private static volatile DynaObserverMediator			mediator			= new DynaObserverMediator();

	private DynaObserverMediator()
	{
		this.init();
	}

	/**
	 * 应用启动时初始化
	 */
	public void init()
	{
	}

	public static DynaObserverMediator getInstance()
	{
		return mediator;
	}

	/**
	 * Bean注册，一个类数据刷新时，需要对关联父对象进行刷新，需要指定关联key，以及父对象guid
	 * 
	 * @param <T>
	 * @param clazz
	 * @param parentGuid
	 * @param relationKey
	 * @param observer
	 * @return
	 */
	public synchronized <T extends SystemObject> boolean register(Class<T> clazz, String parentGuid, String relationKey, DynaCacheObserver observer)
	{
		Map<String, DynaCacheObserver> observerMap = mediatorMap.get(clazz);
		if (observerMap == null)
		{
			observerMap = new HashMap<String, DynaCacheObserver>();
		}
		if (!observerMap.containsKey(parentGuid))
		{
			observerMap.put(parentGuid, observer);
			mediatorMap.put(clazz, observerMap);
		}

		List<String> relationKeyList = classRelationKeyMap.get(clazz);
		if (relationKeyList == null)
		{
			classRelationKeyMap.put(clazz, new ArrayList<String>());
		}
		if (!classRelationKeyMap.get(clazz).contains(relationKey))
		{
			classRelationKeyMap.get(clazz).add(relationKey);
		}

		return true;
	}

	/**
	 * 当父对象只有一个对象时，使用该方法进行注册
	 * 
	 * @param <T>
	 * @param clazz
	 * @param observer
	 * @return
	 */
	public synchronized <T extends SystemObject> boolean register(Class<T> clazz, DynaCacheObserver observer)
	{
		Map<String, DynaCacheObserver> observerMap = mediatorMap.get(clazz);
		if (observerMap == null)
		{
			observerMap = new HashMap<String, DynaCacheObserver>();
		}
		observerMap.put("EMPTY", observer);
		mediatorMap.put(clazz, observerMap);
		return true;
	}

	public synchronized <T extends SystemObject> boolean unregister(Class<T> clazz, String parentGuid, DynaCacheObserver observer)
	{
		Map<String, DynaCacheObserver> observerMap = mediatorMap.get(clazz);
		if (observerMap == null)
		{
			return false;
		}
		observerMap.remove(parentGuid);
		return true;
	}

	public synchronized <T extends SystemObject> boolean unregister(Class<T> clazz, DynaCacheObserver observer)
	{
		Map<String, DynaCacheObserver> observerMap = mediatorMap.get(clazz);
		if (observerMap == null)
		{
			return false;
		}
		observerMap.remove("EMPTY");
		return true;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends SystemObject> boolean notifyAll(T dynaObject, String type)
	{
		Map<String, DynaCacheObserver> observerMap = mediatorMap.get(dynaObject.getClass());
		if (observerMap != null)
		{
			List<String> relationKeyList = classRelationKeyMap.get(dynaObject.getClass());
			if (!SetUtils.isNullList(relationKeyList))
			{
				for (String relationKey : relationKeyList)
				{
					String parentGuid = (String) dynaObject.get(relationKey);
					if (StringUtils.isGuid(parentGuid))
					{
						DynaCacheObserver observer = observerMap.get(parentGuid);
						AbstractCacheInfo parent = observer.getParent();
						if (parent != null && parent.getGuid().equals(dynaObject.get(relationKey)))
						{
							try
							{
								observer.getListener().notifyListener(parent, dynaObject, type);
							}
							catch (ServiceRequestException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
			else
			{
				DynaCacheObserver observer = observerMap.get("EMPTY");
				if (observer != null)
				{
					AbstractCacheInfo parent = observer.getParent();
					if (parent != null)
					{
						try
						{
							observer.getListener().notifyListener(parent, dynaObject, type);
						}
						catch (ServiceRequestException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		return true;
	}
}
