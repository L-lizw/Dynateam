package dyna.common.bean.data.ppms.instancedomain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.systemenum.DomainSyncModeEnum;
import dyna.common.util.SetUtils;

public class InstanceDomainUpdateBean implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long				serialVersionUID	= 6361695941468418581L;

	private FoundationObject				rootObject			= null;

	private Map<String, String>				newGuidMap			= new HashMap<String, String>();

	private final Set<String>				loadSignSet			= new HashSet<String>();

	private final Map<String, DynaObject>	instanceMap			= new HashMap<String, DynaObject>();

	private final Map<String, List<String>>	loadMap				= new HashMap<String, List<String>>();

	private final Map<String, List<String>>	deleteMap			= new HashMap<String, List<String>>();

	private DomainSyncModeEnum				mode				= null;

	public InstanceDomainUpdateBean(FoundationObject rootObject)
	{
		this.rootObject = rootObject;
	}

	/**
	 * @return the rootObjectGuid
	 */
	public FoundationObject getRootObject()
	{
		return this.rootObject;
	}

	public void setRootObject(FoundationObject object)
	{
		this.rootObject = object;
	}

	/**
	 * @return the newGuidMap
	 */
	public Map<String, String> getNewGuidMap()
	{
		return this.newGuidMap;
	}

	/**
	 * @param newGuidMap
	 *            the newGuidMap to set
	 */
	public void setNewGuidMap(Map<String, String> newGuidMap)
	{
		this.newGuidMap = newGuidMap;
	}

	/**
	 * @return the loadSignSet
	 */
	public Set<String> getLoadSignSet()
	{
		return this.loadSignSet;
	}

	/**
	 * @return the loadMap
	 */
	public List<DynaObject> getLoadMap(String key)
	{
		List<String> list = this.loadMap.get(key);
		if (SetUtils.isNullList(list))
		{
			return null;
		}
		List<DynaObject> returnList = new ArrayList<DynaObject>();
		{
			for (String guid : list)
			{
				returnList.add(this.instanceMap.get(guid));
			}
		}
		return returnList;
	}

	/**
	 * @param loadMap
	 *            the loadMap to set
	 */
	public void setLoadMap(String key, List<? extends DynaObject> list)
	{
		if (!SetUtils.isNullList(list))
		{
			this.loadSignSet.add(key);
			List<String> returnList = new ArrayList<String>();
			for (DynaObject object : list)
			{
				String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
				returnList.add(guid);
				this.instanceMap.put(guid, object);
			}
			this.loadMap.put(key, returnList);
		}
	}

	/**
	 * @return the deleteMap
	 */
	public List<DynaObject> getDeleteMap(String key)
	{
		List<String> list = this.deleteMap.get(key);
		if (SetUtils.isNullList(list))
		{
			return null;
		}
		List<DynaObject> returnList = new ArrayList<DynaObject>();
		{
			for (String guid : list)
			{
				returnList.add(this.instanceMap.get(guid));
			}
		}
		return returnList;
	}

	/**
	 * @param deleteMap
	 *            the deleteMap to set
	 */
	public void setDeleteMap(String key, List<DynaObject> list)
	{
		if (!SetUtils.isNullList(list))
		{
			this.loadSignSet.add(key);
			List<String> returnList = new ArrayList<String>();
			for (DynaObject object : list)
			{
				String guid = (String) (object.get("GUID") == null ? object.get("GUID$") : object.get("GUID"));
				returnList.add(guid);
				this.instanceMap.put(guid, object);
			}

			this.deleteMap.put(key, returnList);

		}
	}

	/**
	 * @return the mode
	 */
	public DomainSyncModeEnum getMode()
	{
		return this.mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(DomainSyncModeEnum mode)
	{
		this.mode = mode;
	}

}
