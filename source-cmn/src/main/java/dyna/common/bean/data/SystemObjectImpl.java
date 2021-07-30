/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: General Object bean
 * xiasheng Apr 22, 2010
 */
package dyna.common.bean.data;

import java.util.Date;
import java.util.Iterator;

/**
 * @author xiasheng
 *
 */
public class SystemObjectImpl extends DynaObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 8887765836699603321L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		throw new UnsupportedOperationException("Invalid method on SystemObject, use getGuid() instead.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#resetObjectGuid()
	 */
	@Override
	public ObjectGuid resetObjectGuid()
	{
		throw new UnsupportedOperationException("Invalid method on SystemObject, use setGuid() instead.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#setObjectGuid(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void setObjectGuid(ObjectGuid objectGuid)
	{
		throw new UnsupportedOperationException("Invalid method on SystemObject, use setGuid() instead.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.SystemObject#removeNoChanged()
	 */
	@Override
	public void removeNoChanged()
	{
		synchronized (this)
		{
			String field = null;
			for (Iterator<String> iter = this.keySet().iterator(); iter.hasNext();)
			{
				field = iter.next();
				if (this.isChanged(field) || GUID.equals(field))
				{
					continue;
				}
				this.remove(field);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObject#getGuid()
	 */
	@Override
	public String getGuid()
	{
		return (String) super.get(GUID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.SystemObject#setGuid(java.lang.String)
	 */
	@Override
	public void setGuid(String guid)
	{
		super.put(GUID, guid);
	}

	public String getName()
	{
		return (String) super.get(NAME);
	}

	public void setName(String name)
	{
		super.put(NAME, name);
	}

	@Override
	public void setUpdateUserGuid(String updateUserGuid)
	{
		this.put(UPDATE_USER_GUID, updateUserGuid);
	}

	@Override
	public void setCreateUserGuid(String createUserGuid)
	{
		this.put(CREATE_USER_GUID, createUserGuid);
	}

	@Override
	public String getCreateUserGuid()
	{
		return (String) this.get(CREATE_USER_GUID);
	}

	@Override
	public String getUpdateUserGuid()
	{
		return (String) this.get(UPDATE_USER_GUID);
	}

	@Override
	public Date getCreateTime()
	{
		return (Date) this.get(CREATE_TIME);
	}

	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get(UPDATE_TIME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#getCreateUser()
	 */
	@Override
	public String getCreateUser()
	{
		return (String) this.get(CREATE_USER_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#getUpdateUser()
	 */
	@Override
	public String getUpdateUser()
	{
		return (String) this.get(UPDATE_USER_NAME);
	}
}
