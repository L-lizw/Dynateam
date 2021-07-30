/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaObjectI
 * xiasheng May 4, 2010
 */
package dyna.common.bean.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author xiasheng
 *
 */
public interface DynaObject extends Cloneable, Serializable
{

	public Object clone();

	public abstract void clear(String key);

	public abstract Object get(String field);

	public abstract Date getCreateTime();

	public abstract String getCreateUser();

	public abstract String getCreateUserGuid();

	public abstract ObjectGuid getObjectGuid();

	/**
	 * @param field
	 * @return the original value associated with field
	 */
	public abstract Object getOriginalValue(String field);

	public int getRowCount();

	public abstract long getRowIndex();

	public abstract Date getUpdateTime();

	public abstract String getUpdateUser();

	public abstract String getUpdateUserGuid();


	/**
	 * @return the object value is changed or not
	 */
	public abstract boolean isChanged();

	public abstract boolean isChanged(String field);

	/**
	 * @return the object is valid or not
	 */
	public abstract boolean isValid();

	public abstract Object put(String field, Object value);

	/**
	 * 重新获取ObjectGuid
	 * 
	 * @return ObjectGuid
	 */
	public abstract ObjectGuid resetObjectGuid();

	public void restoreOriginalValues();
	
	public void putOriginalValueMap(Map<String, Object> originalMap);

	public abstract void setCreateUserGuid(String createUserGuid);

	public abstract void setObjectGuid(ObjectGuid objectGuid);

	public abstract void setUpdateUserGuid(String updateUserGuid);
	
	public void sync(DynaObject object);
	
	public void addObserver(DynaObserver o);

	public void deleteObserver(DynaObserver o);

	public void syncValue(DynaObject object);

}