/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: KVObject
 * Wanglei 2010-4-16
 */
package dyna.common.conf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dyna.common.Configurable;
import dyna.common.util.NumberUtils;

/**
 * @author Wanglei
 *
 */
public class ConfigurableKVElementImpl extends ConfigurableAdapter implements Configurable
{
	private static final String		EL_ATTRIBUTE	= "<ATTRIBUTE>:";

	private static final String		EL_TEXT			= "<TEXT>:";

	private static final String		EL_TOTAL		= "<TOTAL>:";

	protected Map<String, String>	kvMap			= new HashMap<String, String>();

	public void setAttributeValue(String path, String value)
	{
		this.setValue(EL_ATTRIBUTE, path, value);
	}

	public String getAttributeValue(String path)
	{
		return this.getValue(EL_ATTRIBUTE, path);
	}

	public void setElementValue(String path, String value)
	{
		this.setValue(EL_TEXT, path, value);
	}

	/**
	 * 根据元素在xml中的path查询元素的TEXT信息
	 * 如该元素在同结构内重复出现, 则默认返回第一个出现的元素的TEXT信息
	 * 
	 * @param path
	 * @return
	 */
	public String getElementValue(String path)
	{
		return this.getValue(EL_TEXT, path);
	}

	public String getElementValue()
	{
		return this.getValue(EL_TEXT, "");
	}

	public String getElementValue(String path, int index)
	{
		int size = this.getSize(path);
		if (index < 1 || index > size)
		{
			index = 0;
		}
		String suffix = index == 0 ? "" : ".<" + String.valueOf(index) + ">";
		return this.getValue(EL_TEXT, path + suffix);
	}

	public boolean hasAttribute(String path)
	{
		return this.hasPathLike(EL_ATTRIBUTE + path);
	}

	public boolean hasElement(String path)
	{
		return this.hasPathLike(EL_TEXT + path);
	}

	private boolean hasPathLike(String path)
	{
		for (Iterator<String> iter = this.kvMap.keySet().iterator(); iter.hasNext();)
		{
			String key=iter.next();
			if (key.equals(path)||key.startsWith(path+"."))
			{
				return true;
			}
		}
		return false;
	}

	public int getSize(String path)
	{
		int size = NumberUtils.intOf(this.getValue(EL_TOTAL, path), 0);
		if (size == 0)
		{
			if (this.hasAttribute(path) || this.hasElement(path))
			{
				size = 1;
			}
			else
			{
				size = 0;
			}
		}
		return size;
	}

	public void setSize(String path, int size)
	{
		this.setValue(EL_TOTAL, path, String.valueOf(size));
	}

	public Iterator<ConfigurableKVElementImpl> iterator(String path)
	{
		List<ConfigurableKVElementImpl> retList = new LinkedList<ConfigurableKVElementImpl>();

		String suffix = null;
		int size = this.getSize(path);
		for (int i = 0; i < size; i++)
		{

			if (i == 0)
			{
				suffix = "";
			}
			else
			{
				suffix = ".<" + i + ">";
			}

			retList.add(this.getElementMapLikeKey(path + suffix));
		}

		return retList.iterator();
	}

	private ConfigurableKVElementImpl getElementMapLikeKey(String path)
	{
		String newPath = null;
		String mapKey = null;
		String type = null;
		int offset = 0;
		int fromIndex = 0;
		boolean isFirst = !path.endsWith(">");

		ConfigurableKVElementImpl kvElement = new ConfigurableKVElementImpl();
		for (Iterator<String> iter = this.kvMap.keySet().iterator(); iter.hasNext();)
		{
			mapKey = iter.next();
			if (mapKey.equals(EL_TOTAL + path))
			{
				continue;
			}

			offset = mapKey.indexOf(':') + 1;
			type = mapKey.substring(0, offset);
			fromIndex = mapKey.indexOf(path, offset) + path.length() + 1;

			if (fromIndex >= mapKey.length())
			{
				newPath = "";
			}
			else
			{
				newPath = mapKey.substring(fromIndex);
			}

			if (isFirst)
			{
				if (mapKey.startsWith(path, offset) && !mapKey.startsWith(path + ".<", offset))
				{
					kvElement.setValue(type, newPath, this.kvMap.get(mapKey));
				}
			}
			else
			{
				if (mapKey.startsWith(path, offset))
				{
					kvElement.setValue(type, newPath, this.kvMap.get(mapKey));
				}
			}
		}
		return kvElement;
	}

	private void setValue(String type, String path, String value)
	{
		this.kvMap.put(type + path, value);
	}

	private String getValue(String type, String path)
	{
		path = path.replace(".<0>.", ".");
		String value = this.kvMap.get(type + path);
		if (value == null || value.trim().isEmpty())
		{
			return null;
		}
		else
		{
			return value;
		}
	}

	public void clear()
	{
		this.kvMap.clear();
	}

	@Override
	public String toString()
	{
		return this.kvMap.toString();
	}

}
