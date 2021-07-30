/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: the top object definition
 * xiasheng Apr 22, 2010
 */
package dyna.common.bean.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.ComparisonUtils;

/**
 * @author xiasheng
 * 
 */
public class DynaObjectImpl extends HashMap<String, Object> implements DynaObject
{
	private static final long				serialVersionUID	= 4369095091268417114L;

	protected Map<String, Object>			originalMap			= null;					// save old values of changed
																						// fields

	protected ObjectGuid					objectGuid			= null;

	private transient Vector<DynaObserver>	obs					= null;

	public DynaObjectImpl()
	{
		this.obs = new Vector<DynaObserver>();
		this.originalMap = new HashMap<String, Object>();
	}

	@Override
	public void clear(String key)
	{
		super.remove(key.toUpperCase());
		this.originalMap.remove(key.toUpperCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObjectI#get(java.lang.Object)
	 */
	@Override
	public Object get(String field)
	{
		Object obj = super.get(field.toUpperCase());
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof Long || obj instanceof Integer || obj instanceof Float)
		{
			return new BigDecimal(obj.toString());
		}
		return super.get(field.toUpperCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getCreateTime()
	 */
	@Override
	public Date getCreateTime()
	{
		return (Date) this.get(SystemClassFieldEnum.CREATETIME.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getCreateUser()
	 */
	@Override
	public String getCreateUser()
	{
		return (String) this.get(SystemClassFieldEnum.CREATEUSER.getName() + "NAME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getCreateUserGuid()
	 */
	@Override
	public String getCreateUserGuid()
	{
		return (String) this.get(SystemClassFieldEnum.CREATEUSER.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObject#getGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		if (this.objectGuid == null)
		{
			Boolean isMaster = BooleanUtils.getBooleanByYN((String) this.get("ISMASTER"));
			this.objectGuid = new ObjectGuid(//
					(String) this.get("CLASSGUID$"), //
					(String) this.get("CLASSNAME$"), //
					(String) this.get("GUID$"), //
					(String) this.get("MASTERFK$"), //
					isMaster == null ? false : isMaster, //
					(String) this.get("BOGUID$"), (String) this.get(SystemClassFieldEnum.COMMITFOLDER.getName()));
		}

		return this.objectGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getOriginalValue(java.lang.Object)
	 */
	@Override
	public Object getOriginalValue(String field)
	{
		Object obj = this.getOriginalMap().get(field.toUpperCase());
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof Long || obj instanceof Integer || obj instanceof Float)
		{
			return new BigDecimal(obj.toString());
		}
		return this.getOriginalMap().get(field.toUpperCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getRowCount()
	 */
	@Override
	public int getRowCount()
	{
		Object rowCount = this.get("ROWCOUNT$");
		if (rowCount == null)
		{
			return -1;
		}
		else if (rowCount instanceof Integer)
		{
			return (Integer) rowCount;
		}
		else if (rowCount instanceof Long)
		{
			return ((Long) rowCount).intValue();
		}
		else
		{
			return ((Number) rowCount).intValue();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getRowIndex()
	 */
	@Override
	public long getRowIndex()
	{
		if (this.get("ROWINDEX$") == null)
		{
			return -1;
		}
		else
		{
			return ((Number) this.get("ROWINDEX$")).longValue();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getUpdateTime()
	 */
	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get(SystemClassFieldEnum.UPDATETIME.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getUpdateUser()
	 */
	@Override
	public String getUpdateUser()
	{
		return (String) this.get(SystemClassFieldEnum.UPDATEUSER.getName() + "NAME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#getUpdateUserGuid()
	 */
	@Override
	public String getUpdateUserGuid()
	{
		return (String) this.get(SystemClassFieldEnum.UPDATEUSER.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#isChanged()
	 */
	@Override
	public boolean isChanged()
	{
		for (Iterator<String> ir = this.getOriginalMap().keySet().iterator(); ir.hasNext();)
		{
			String field = ir.next();
			if (this.isChanged(field))
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#isChanged(java.lang.String)
	 */
	@Override
	public boolean isChanged(String field)
	{
		field = field.toUpperCase();

		synchronized (this.getOriginalMap())
		{
			if (this.get(field) == null)
			{
				if (this.getOriginalMap().get(field) == null)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return !ComparisonUtils.assertEqual(this.get(field), this.getOriginalValue(field));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObjectI#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object put(String field, Object value)
	{
		field = field.toUpperCase();

		if (value != null && value instanceof String)
		{
			value = ((String) value).trim();
		}
		else if (value != null && (value instanceof Long || value instanceof Integer || value instanceof Float || value instanceof Double))
		{
			if (value instanceof Integer)
			{
				value = new BigDecimal((Integer) value);
			}
			if (value instanceof Long)
			{
				value = new BigDecimal((Long) value);
			}
			if (value instanceof Float)
			{
				value = new BigDecimal((Float) value);
			}
			if (value instanceof Double)
			{
				value = new BigDecimal((Double) value);
			}
		}

		if (!this.containsKey(field))
		{ // initialize map
			super.put(field, value);
			if (this.getOriginalMap().containsKey(field))
			{
				if (this.isChanged(field))
				{
					this.notifyObservers(field);
					this.notifyObservers();
				}
			}
			else
			{
				synchronized (this.getOriginalMap())
				{
					this.getOriginalMap().put(field, value);
				}
				if (value != null)
				{
					this.notifyObservers(field);
					this.notifyObservers();
				}
			}
			return null;
		}
		else
		{
			Object oldValue = super.put(field, value);
			if (oldValue == null)
			{
				if (value != null)
				{
					this.notifyObservers(field);
					this.notifyObservers();
				}
			}
			else if (!oldValue.equals(value))
			{
				this.notifyObservers(field);
				this.notifyObservers();
			}
			return oldValue;
		}
	}

	@Override
	public ObjectGuid resetObjectGuid()
	{
		boolean hasAuth = true;
		if (this.objectGuid != null)
		{
			hasAuth = this.objectGuid.hasAuth();
		}

		this.objectGuid = null;
		ObjectGuid objectGuid = this.getObjectGuid();
		objectGuid.setHasAuth(hasAuth);
		return objectGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObject#restoreOriginalValues()
	 */
	@Override
	public void restoreOriginalValues()
	{
		synchronized (this.getOriginalMap())
		{
			String field = null;
			for (Iterator<String> iterator = this.getOriginalMap().keySet().iterator(); iterator.hasNext();)
			{
				field = iterator.next();
				super.put(field, this.getOriginalMap().get(field));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#setCreateUserGuid(java.lang.String)
	 */
	@Override
	public void setCreateUserGuid(String createUserGuid)
	{
		this.put(SystemClassFieldEnum.CREATEUSER.getName(), createUserGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObject#setGuid()
	 */
	@Override
	public void setObjectGuid(ObjectGuid objectGuid)
	{
		this.objectGuid = objectGuid;
		if (objectGuid == null)
		{
			objectGuid = new ObjectGuid();
		}

		this.put("GUID$", objectGuid.getGuid());
		this.put("CLASSNAME$", objectGuid.getClassName());
		this.put("CLASSGUID$", objectGuid.getClassGuid());
		this.put("MASTERFK$", objectGuid.getMasterGuid());
		this.put("BOGUID$", objectGuid.getBizObjectGuid());
		this.put(SystemClassFieldEnum.COMMITFOLDER.getName(), objectGuid.getCommitFolderGuid());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.DynaObject#setUpdateUserGuid(java.lang.String)
	 */
	@Override
	public void setUpdateUserGuid(String updateUserGuid)
	{
		this.put(SystemClassFieldEnum.UPDATEUSER.getName(), updateUserGuid);
	}

	/**
	 * @param originalMap
	 *            the originalMap to set
	 */
	@Override
	public void putOriginalValueMap(Map<String, Object> originalMap)
	{
		if (originalMap == null)
		{
			this.originalMap = new HashMap<String, Object>();
		}
		originalMap.putAll(originalMap);
	}

	/**
	 * @return the originalMap
	 */
	public Map<String, Object> getOriginalMap()
	{
		return this.originalMap;
	}

	@Override
	public void sync(DynaObject object)
	{
		if (object != this)
		{
			this.objectGuid = null;
			this.clear();
			this.putAll((DynaObjectImpl) object);
			this.getOriginalMap().clear();
			this.getOriginalMap().putAll(this);
		}
	}

	@Override
	public synchronized void addObserver(DynaObserver o)
	{
		if (o == null)
		{
			throw new NullPointerException();
		}
		if (this.obs == null)
		{
			this.obs = new Vector<DynaObserver>();
		}
		if (!this.obs.contains(o))
		{
			this.obs.addElement(o);
		}
	}

	@Override
	public synchronized void deleteObserver(DynaObserver o)
	{
		if (this.obs == null)
		{
			this.obs = new Vector<DynaObserver>();
		}
		this.obs.removeElement(o);
	}

	public void notifyObservers()
	{
		this.notifyObservers(null);
	}

	public void notifyObservers(Object arg)
	{
		if (this.obs == null)
		{
			this.obs = new Vector<DynaObserver>();
		}
		Object[] arrLocal = this.obs.toArray();
		synchronized (this)
		{
			for (int i = arrLocal.length - 1; i >= 0; i--)
			{
				((DynaObserver) arrLocal[i]).update(this, arg);
			}
		}
	}

	public synchronized void deleteObservers()
	{
		if (this.obs == null)
		{
			this.obs = new Vector<DynaObserver>();
		}
		this.obs.removeAllElements();
	}

	public synchronized int countObservers()
	{
		if (this.obs == null)
		{
			this.obs = new Vector<DynaObserver>();
		}
		return this.obs.size();
	}

	@Override
	public void syncValue(DynaObject object)
	{
		if (object != this)
		{
			this.objectGuid = null;
			this.clear();
			this.putAll((DynaObjectImpl) object);
		}

	}
}
