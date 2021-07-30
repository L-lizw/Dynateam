/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ShortCut
 * caogc 2010-08-25
 */
package dyna.common.dto;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;

/**
 * Favorite中存放的对象
 * 
 * @author caogc
 * 
 */
public class ShortCut extends FoundationObjectImpl implements FoundationObject
{

	private static final long	serialVersionUID	= -6595101405378999362L;

	public static final String	FAVORITE_GUID		= "FAVORITEGUID";
	public static final String	INSTANCE_GUID		= "INSTANCEGUID";
	public static final String	INSTANCE_CLASS_GUID	= "INSTANCECLASSGUID";

	public static final String	CREATE_USER			= "CREATEUSER";

	/**
	 * @return the FavoriteGuid
	 */
	public String getFavoriteGuid()
	{
		return (String) this.get(FAVORITE_GUID);
	}

	@Override
	public String getGuid()
	{
		return (String) super.get("GUID");
	}

	/**
	 * @return the instanceClassGuid
	 */
	public String getInstanceClassGuid()
	{
		return (String) this.get(INSTANCE_CLASS_GUID);
	}

	/**
	 * @return the instanceGuid
	 */
	public String getInstanceGuid()
	{
		return (String) this.get(INSTANCE_GUID);
	}

	/**
	 * @param FavoriteGuid
	 *            the FavoriteGuid to set
	 */
	public void setFavoriteGuid(String favoriteGuid)
	{
		this.put(FAVORITE_GUID, favoriteGuid);
	}

	/**
	 * @param Guid
	 *            the Guid to set
	 */
	@Override
	public void setGuid(String guid)
	{
		super.put(GUID, guid);
	}

	/**
	 * @param instanceClassGuid
	 *            the instanceClassGuid to set
	 */
	public void setInstanceClassGuid(String instanceClassGuid)
	{
		this.put(INSTANCE_CLASS_GUID, instanceClassGuid);
	}

	/**
	 * @param instanceGuid
	 *            the instanceGuid to set
	 */
	public void setInstanceGuid(String instanceGuid)
	{
		this.put(INSTANCE_GUID, instanceGuid);
	}

}
