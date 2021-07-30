package dyna.common.bean.data.ppms.instancedomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.DynaObserver;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.DomainSyncModeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class InstanceDomainBean implements Cloneable, Serializable, DynaObserver
{

	/**
	 * 
	 */
	private static final long				serialVersionUID	= -107171041820202560L;

	private FoundationObject				rootObject			= null;

	private final Set<String>				loadSignSet			= new HashSet<String>();

	private final Map<String, List<String>>	loadMap				= new HashMap<String, List<String>>();

	private final Map<String, List<String>>	deleteMap			= new HashMap<String, List<String>>();

	private final Map<String, DynaObject>	instanceMap			= new HashMap<String, DynaObject>();

	private int								i					= 0;

	private List<Object[]>					notifyList			= new ArrayList<Object[]>();

	@SuppressWarnings("rawtypes")
	private transient Vector				obs					= null;

	private boolean							isChange			= false;

	public InstanceDomainBean(FoundationObject rootObject)
	{
		this.rootObject = rootObject;
		this.rootObject.addObserver(this);
		this.instanceMap.put(rootObject.getObjectGuid().getGuid(), rootObject);
		this.obs = new Vector();
	}

	/**
	 * @return the rootObjectGuid
	 */
	public FoundationObject getRootObject()
	{
		return this.rootObject;
	}

	/**
	 * @return the loadSignSet
	 */
	public Set<String> getLoadSignSet()
	{
		return this.loadSignSet;
	}

	/**
	 * @return the deleteMap
	 */
	@SuppressWarnings("unchecked")
	protected <T extends DynaObject> List<T> getDeleteList(Class<T> beanClass, String key)
	{
		List<String> list = this.deleteMap.get(key);
		if (SetUtils.isNullList(list))
		{
			return null;
		}
		List<T> returnList = new ArrayList<T>();
		{
			for (String guid : list)
			{
				returnList.add((T) this.instanceMap.get(guid));
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	public <T extends DynaObject> List<T> getRealationInfo(Class<T> beanClass, String key)
	{
		List<String> list = this.loadMap.get(key);
		if (SetUtils.isNullList(list))
		{
			return null;
		}
		List<T> returnList = new ArrayList<T>();
		{
			for (String guid : list)
			{
				returnList.add((T) this.instanceMap.get(guid));
			}
		}
		return returnList;
	}

	private void removeInstance(String key, DynaObject object)
	{
		List<String> oldList = this.loadMap.get(key);
		if (SetUtils.isNullList(oldList))
		{
			return;
		}
		String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
		if (oldList.contains(guid))
		{
			oldList.remove(guid);
			this.instanceMap.get(guid).deleteObserver(this);
			if (StringUtils.isGuid(guid))
			{
				List<String> deleteList = this.deleteMap.get(key);
				if (deleteList == null)
				{
					deleteList = new ArrayList<String>();
					this.deleteMap.put(key, deleteList);
				}
				deleteList.add(guid);
				isChange=true;
			}
			else
			{
				this.instanceMap.remove(guid);
			}
		}
	}

	private void removeInstanceWithGuid(String key, String guid)
	{
		List<String> oldList = this.loadMap.get(key);
		if (SetUtils.isNullList(oldList))
		{
			return;
		}
		if (oldList.contains(guid))
		{
			oldList.remove(guid);
			this.instanceMap.get(guid).deleteObserver(this);
			if (StringUtils.isGuid(guid))
			{
				List<String> deleteList = this.deleteMap.get(key);
				if (deleteList == null)
				{
					deleteList = new ArrayList<String>();
					this.deleteMap.put(key, deleteList);
				}
				deleteList.add(guid);
				isChange=true;
			}
			else
			{
				this.instanceMap.remove(guid);
			}
		}
	}

	protected void deleteFromDomainWithGuid(String key, List<String> list)
	{
		if (SetUtils.isNullList(list))
		{
			return;
		}

		for (String object : list)
		{
			this.removeInstanceWithGuid(key, object);
		}
		this.notifyObservers(key);
	}

	protected void deleteFromDomainWithGuid(String key, String guid)
	{
		if (guid == null)
		{
			return;
		}
		this.removeInstanceWithGuid(key, guid);
		this.notifyObservers(key);

	}

	protected final void deleteFromDomain(String key, List<? extends DynaObject> list)
	{
		if (SetUtils.isNullList(list))
		{
			return;
		}
		for (DynaObject object : list)
		{
			this.removeInstance(key, object);
		}
		this.notifyObservers(key);
	}

	protected final void deleteFromDomain(String key, DynaObject object)
	{
		if (object == null)
		{
			return;
		}
		this.removeInstance(key, object);
		this.notifyObservers(key);
	}

	protected final void addToDomain(String key, List<? extends DynaObject> list)
	{
		if (SetUtils.isNullList(list))
		{
			return;
		}
		for (DynaObject object : list)
		{
			this.addInstance(key, object);
		}
		this.notifyObservers(key);

	}

	protected final void updateSequenceToDomain(String key, List<? extends DynaObject> list)
	{
		if (SetUtils.isNullList(list))
		{
			return;
		}
		List<String> oldGuidList = this.loadMap.get(key);
		if (SetUtils.isNullList(oldGuidList))
		{
			return;
		}
		if (list.size() != oldGuidList.size())
		{
			return;
		}
		List<String> guidList = new ArrayList<String>(list.size());
		for (DynaObject object : list)
		{
			String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
			if (oldGuidList.contains(guid))
			{
				if (guidList.contains(guid) == false)
				{
					guidList.add(guid);
				}
			}
			else
			{
				return;
			}
		}
		if (guidList.size() != oldGuidList.size())
		{
			return;
		}
		if (guidList.containsAll(oldGuidList))
		{
			oldGuidList.clear();
			oldGuidList.addAll(guidList);
			this.notifyObservers(key);
		}
	}

	private void addInstance(String key, DynaObject object)
	{
		if (object instanceof SystemObjectImpl)
		{
			if (object.get("GUID") == null)
			{
				object.put("GUID", "TEMPGUID$" + object.hashCode() + "$" + System.currentTimeMillis());
			}
			else
			{
				if (this.deleteMap.get(key) != null)
				{
					this.deleteMap.get(key).remove(object.get("GUID"));
				}
			}
		}
		else
		{
			if (object.get("GUID$") == null)
			{
				ObjectGuid objectGuid = object.getObjectGuid();
				objectGuid.setGuid("TEMPGUID$" + object.hashCode() + "$" + System.currentTimeMillis());
				object.setObjectGuid(objectGuid);
			}
			else
			{
				if (this.deleteMap.get(key) != null)
				{
					this.deleteMap.get(key).remove(object.get("GUID$"));
				}
			}
		}
		String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
		List<String> instanceList = this.loadMap.get(key);
		if (instanceList != null && instanceList.contains(guid))
		{
			return;
		}
		if (SetUtils.isNullList(instanceList))
		{
			instanceList = new ArrayList<String>();
			this.loadMap.put(key, instanceList);
		}
		instanceList.add(guid);
		this.instanceMap.put(guid, object);
		object.addObserver(this);
		isChange=true;
	}

	protected final void addToDomain(String key, DynaObject object)
	{
		if (object == null)
		{
			return;
		}
		this.addInstance(key, object);
		this.notifyObservers(key);
	}

	public InstanceDomainUpdateBean getInstanceDomainUpdateBean()
	{
		InstanceDomainUpdateBean bean = new InstanceDomainUpdateBean(this.rootObject);
		bean.setRootObject(this.rootObject);
		for (String key : this.deleteMap.keySet())
		{
			List<DynaObject> list = this.getDeleteList(DynaObject.class, key);
			if (!SetUtils.isNullList(list))
			{
				bean.setDeleteMap(key, list);
			}
		}
		for (String key : this.loadMap.keySet())
		{
			List<DynaObject> list = this.getRealationInfo(DynaObject.class, key);
			if (!SetUtils.isNullList(list))
			{
				for (int i = list.size() - 1; i > -1; i--)
				{
					String guid = (String) (list.get(i).get("GUID") == null ? list.get(i).get("GUID$") : list.get(i).get("GUID"));
					if (StringUtils.isGuid(guid) && list.get(i).isChanged() == false)
					{
						list.remove(i);
					}
				}
				if (!SetUtils.isNullList(list))
				{
					bean.setLoadMap(key, list);
				}
			}
		}
		bean.setMode(DomainSyncModeEnum.EDITSAVE);
		return bean;
	}

	public void syncInstanceDomain(InstanceDomainUpdateBean bean)
	{
		DomainSyncModeEnum mode = bean.getMode();
		for (String key : this.deleteMap.keySet())
		{
			List<String> list = this.deleteMap.get(key);
			if (!SetUtils.isNullList(list))
			{
				for (String guid : list)
				{
					this.instanceMap.remove(guid);
				}
			}
		}
		this.deleteMap.clear();

		switch (mode)
		{
		case EDITSAVE:
			this.syncInstanceDomainEdit(bean);
			break;
		case REPACLEALL:
			this.syncInstanceDomainAll(bean);
			break;
		case CHANGE:
			this.syncInstanceDomainUpdate(bean);
			break;
		}
	}

	private void syncInstanceDomainEdit(InstanceDomainUpdateBean bean)
	{
		Set<String> signSet = bean.getLoadSignSet();
		if (bean.getRootObject() != null)
		{
			this.instanceMap.get(this.rootObject.getGuid()).syncValue(bean.getRootObject());
		}
		if (signSet != null)
		{
			for (String key : signSet)
			{
				if (bean.getDeleteMap(key) != null)
				{
					this.deleteFromDomain(key, bean.getDeleteMap(key));
				}
				List<DynaObject> list = bean.getLoadMap(key);
				if (list != null)
				{
					for (DynaObject object : list)
					{
						String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
						if (this.loadMap.get(key) == null)
						{
							if (StringUtils.isGuid(guid) == false)
							{
								this.addToDomain(key, object);
							}
							else
							{
								this.deleteFromDomainWithGuid(key, guid); // exception
							}
						}
						else
						{
							if (StringUtils.isGuid(guid) == false)
							{
								this.addToDomain(key, object);
							}
							else if (this.instanceMap.containsKey(guid) && this.loadMap.get(key).contains(guid))
							{
								this.instanceMap.get(guid).syncValue(object);
							}
							else
							{
								this.deleteFromDomainWithGuid(key, guid); // exception
							}
						}
					}
				}
			}
		}
	}

	private void syncInstanceDomainUpdate(InstanceDomainUpdateBean bean)
	{
		Set<String> signSet = bean.getLoadSignSet();
		Map<String, String> newGuidMap = bean.getNewGuidMap();
		newGuidMap = this.swapMap(newGuidMap);
		if (bean.getRootObject() != null)
		{
			this.instanceMap.get(this.rootObject.getGuid()).sync(bean.getRootObject());
		}
		if (signSet != null)
		{
			for (String key : signSet)
			{
				if (bean.getDeleteMap(key) != null)
				{
					this.deleteFromDomain(key, bean.getDeleteMap(key));
				}
				List<DynaObject> list = bean.getLoadMap(key);
				if (!SetUtils.isNullList(list))
				{
					for (DynaObject object : list)
					{
						String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
						String oldGuid = newGuidMap.get(guid);
						if (this.loadMap.get(key) == null)
						{
							// exception
							if (oldGuid != null)
							{
								this.deleteFromDomainWithGuid(key, oldGuid);
							}
							else
							{
								this.deleteFromDomainWithGuid(key, guid);
							}
						}
						else
						{
							if (oldGuid != null)
							{
								if (this.loadMap.get(key).contains(oldGuid) && this.instanceMap.containsKey(oldGuid))
								{
									this.instanceMap.get(oldGuid).sync(object);
									this.instanceMap.put(guid, this.instanceMap.get(oldGuid));
									this.instanceMap.remove(oldGuid);
									int i = this.loadMap.get(key).indexOf(oldGuid);
									this.loadMap.get(key).set(i, guid);
								}
								else
								{
									this.deleteFromDomainWithGuid(key, oldGuid);// exception
								}
							}
							else
							{
								if (this.instanceMap.containsKey(guid) && this.loadMap.get(key).contains(guid))
								{
									this.instanceMap.get(guid).sync(object);
								}
								else
								{
									this.removeInstanceWithGuid(key, guid);// exception
								}
							}
						}
					}
				}
			}
		}
		isChange=false;
	}

	private Map<String, String> swapMap(Map<String, String> newGuidMap)
	{
		Map<String, String> guidMap = new HashMap<String, String>();
		if (newGuidMap != null)
		{
			for (String key : newGuidMap.keySet())
			{
				if (StringUtils.isNullString(newGuidMap.get(key)) == false)
				{
					guidMap.put(newGuidMap.get(key), key);
				}
			}
		}
		return guidMap;
	}

	private void syncInstanceDomainAll(InstanceDomainUpdateBean bean)
	{
		Set<String> signSet = bean.getLoadSignSet();
		this.loadSignSet.addAll(signSet);
		Map<String, String> newGuidMap = bean.getNewGuidMap();
		newGuidMap = this.swapMap(newGuidMap);
		if (bean.getRootObject() != null)
		{
			this.instanceMap.get(this.rootObject.getGuid()).sync(bean.getRootObject());
		}
		if (signSet != null)
		{
			for (String key : signSet)
			{
				List<DynaObject> list = bean.getLoadMap(key);
				List<DynaObject> oldlist = this.getRealationInfo(DynaObject.class, key);
				if (!SetUtils.isNullList(oldlist))
				{
					for (DynaObject object : oldlist)
					{
						if (isExist(object, list) == false)
						{
							this.deleteFromDomain(key, object);
						}
					}
				}
				if (bean.getDeleteMap(key) != null)
				{
					this.deleteFromDomain(key, bean.getDeleteMap(key));
				}
				if (!SetUtils.isNullList(list))
				{
					if (this.loadMap.get(key) == null)
					{
						for (DynaObject object : list)
						{
							String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
							String oldGuid = newGuidMap.get(guid);
							if (oldGuid != null)
							{
								// exception
							}
							this.addInstance(key, object);
						}
					}
					else
					{
						for (DynaObject object : list)
						{
							String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
							String oldGuid = newGuidMap.get(guid);

							if (oldGuid != null)
							{
								if (this.loadMap.get(key).contains(oldGuid))
								{
									this.instanceMap.get(oldGuid).sync(object);
									this.instanceMap.put(guid, this.instanceMap.get(oldGuid));
									this.instanceMap.remove(oldGuid);
									int i = this.loadMap.get(key).indexOf(oldGuid);
									this.loadMap.get(key).set(i, guid);
								}
								else
								{
									this.deleteFromDomainWithGuid(key, oldGuid);// exception
								}
							}
							else
							{
								if (this.instanceMap.containsKey(guid))
								{
									if (this.loadMap.get(key).contains(guid))
									{
										this.instanceMap.get(guid).sync(object);
									}
									else
									{
										this.deleteFromDomainWithGuid(key, oldGuid);// exception
									}
								}
								else
								{
									this.addInstance(key, object);
								}
							}
						}
					}
				}
			}
		}
		isChange=false;
	}

	private boolean isExist(DynaObject object, List<DynaObject> list)
	{
		if (!SetUtils.isNullList(list))
		{
			for (DynaObject nobject : list)
			{
				String guid1 = (String) (((DynaObject) nobject).get("GUID") == null ? ((DynaObject) nobject).get("GUID$") : ((DynaObject) nobject).get("GUID"));
				String guid2 = (String) (((DynaObject) object).get("GUID") == null ? ((DynaObject) object).get("GUID$") : ((DynaObject) object).get("GUID"));

				if (guid2.equals(guid1))
				{
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends DynaObject> T getInstanceObject(Class<T> beanClass, String key)
	{
		return (T) this.instanceMap.get(key);
	}

	@Override
	public void update(Object o, Object arg)
	{
		for (Object[] notify : this.notifyList)
		{
			if (notify[0] == o)
			{
				if (arg == null)
				{
					if (notify[1] == null)
					{
						return;
					}
				}
				else if (arg.equals(notify[1]))
				{
					return;
				}
			}
		}
		if(isChange==false)
		{
			isChange=((DynaObject) o).isChanged();
		}
		this.notifyList.add(new Object[] { o, arg });
		if (o != this)
		{
			String guid = (String) (((DynaObject) o).get("GUID") == null ? ((DynaObject) o).get("GUID$") : ((DynaObject) o).get("GUID"));
			if (this.loadMap != null)
			{
				for (String itrkey : this.loadMap.keySet())
				{
					if (this.loadMap.get(itrkey) != null && this.loadMap.get(itrkey).contains(guid))
					{
						for (Object[] notify : this.notifyList)
						{
							if (notify[0] == this && itrkey.equals(notify[1]))
							{
								return;
							}
						}
						this.notifyList.add(new Object[] { this, itrkey });
						return;
					}
				}
			}
		}
	}

	public void notifyObservers()
	{
		this.notifyObservers(null);
	}

	public void notifyObservers(Object arg)
	{
		this.update(this, arg);
	}

	public void closeNotifyObservers()
	{
		this.i++;
	}

	public void openNotifyObservers()
	{
		this.i--;
		if (this.i == 0)
		{
			this.clearNotifyObservers();
		}
	}

	public void clearNotifyObservers()
	{
		if (i == 0)
		{
			Object[] arrLocal;

			synchronized (this)
			{
				if (this.notifyList.size() == 0)
				{
					return;
				}
				arrLocal = this.obs.toArray();
				List<Object[]> notifyList2 = this.notifyList;
				this.notifyList = new ArrayList<Object[]>();

				for (int i = arrLocal.length - 1; i >= 0; i--)
				{
					for (Object[] notify : notifyList2)
					{
						try
						{
							((DynaObserver) arrLocal[i]).update(notify[0], notify[1]);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				this.notifyList.clear();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void addObserver(DynaObserver o)
	{
		if (o == null)
		{
			throw new NullPointerException();
		}
		if (!this.obs.contains(o))
		{
			this.obs.addElement(o);
		}
	}

	public synchronized void deleteObserver(DynaObserver o)
	{
		this.obs.removeElement(o);
	}

	public synchronized void deleteObservers()
	{
		this.obs.removeAllElements();
	}

	public synchronized int countObservers()
	{
		return this.obs.size();
	}

	public boolean isNeedSave()
	{
		return isChange;
	}
}
