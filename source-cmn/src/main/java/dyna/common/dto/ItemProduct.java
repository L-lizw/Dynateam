/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ItemProduct
 * caogc 2010-11-02
 */
package dyna.common.dto;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author caogc
 * 
 */
public class ItemProduct extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 3279514362902686911L;

	public static final String	GUID				= "GUID";
	public static final String	ITEM_GUID			= "ITEMGUID";
	public static final String	ITEM_CLASS_GUID		= "ITEMCLASSGUID";
	public static final String	ITEM_MASTER_GUID	= "ITEMMASTERGUID";
	public static final String	PRODUCT_GUID		= "PRODUCTGUID";
	public static final String	PRODUCT_CLASS_GUID	= "PRODUCTCLASSGUID";
	public static final String	PRODUCT_MASTER_GUID	= "PRODUCTMASTERGUID";
	// public static final String IS_PRECISE = "ISPRECISE";
	public static final String	PRODUCT_TITLE		= "PRODUCTTITLE";
	public static final String	ITEM_TITLE			= "ITEMTITLE";
	public static final String	ITEM_PRODUCT_GUID	= "ItemProductGuid";

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the itemClassGuid
	 */
	public String getItemClassGuid()
	{
		return (String) this.get(ITEM_CLASS_GUID);
	}

	/**
	 * @return the itemGuid
	 */
	public String getItemGuid()
	{
		return (String) this.get(ITEM_GUID);
	}

	/**
	 * @return the itemMasterGuid
	 */
	public String getItemMasterGuid()
	{
		return (String) this.get(ITEM_MASTER_GUID);
	}

	/**
	 * @return the itemTitle
	 */
	public String getItemTitle()
	{
		return (String) this.get(ITEM_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		if (this.objectGuid == null)
		{
			this.objectGuid = new ObjectGuid(null, this.getGuid(), null);
		}
		return this.objectGuid;
	}

	/**
	 * @return the productClassGuid
	 */
	public String getProductClassGuid()
	{
		return (String) this.get(PRODUCT_CLASS_GUID);
	}

	/**
	 * @return the productGuid
	 */
	public String getProductGuid()
	{
		return (String) this.get(PRODUCT_GUID);
	}

	/**
	 * @return the productMasterGuid
	 */
	public String getProductMasterGuid()
	{
		return (String) this.get(PRODUCT_MASTER_GUID);
	}

	/**
	 * @return the productTitle
	 */
	public String getProductTitle()
	{
		return (String) this.get(PRODUCT_TITLE);
	}

	// /**
	// * @return the isPrecise
	// */
	// public boolean isPrecise()
	// {
	// return BooleanUtils.getBooleanByYN((String) this.get(IS_PRECISE));
	// }

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	// /**
	// * @param isPrecise
	// * the isPrecise to set
	// */
	// public void setIsPrecise(boolean isPrecise)
	// {
	// this.put(IS_PRECISE, BooleanUtils.getBooleanStringYN(isPrecise));
	// }

	/**
	 * @param itemClassGuid
	 *            the itemClassGuid to set
	 */
	public void setItemClassGuid(String itemClassGuid)
	{
		this.put(ITEM_CLASS_GUID, itemClassGuid);
	}

	/**
	 * @param itemGuid
	 *            the itemGuid to set
	 */
	public void setItemGuid(String itemGuid)
	{
		this.put(ITEM_GUID, itemGuid);
	}

	/**
	 * @param itemMasterGuid
	 *            the itemMasterGuid to set
	 */
	public void setItemMasterGuid(String itemMasterGuid)
	{
		this.put(ITEM_MASTER_GUID, itemMasterGuid);
	}

	/**
	 * @param itemTitle
	 *            the itemTitle to set
	 */
	public void setItemTitle(String itemTitle)
	{
		this.put(ITEM_TITLE, itemTitle);
	}

	/**
	 * @param productClassGuid
	 *            the productClassGuid to set
	 */
	public void setProductClassGuid(String productClassGuid)
	{
		this.put(PRODUCT_CLASS_GUID, productClassGuid);
	}

	/**
	 * @param productGuid
	 *            the productGuid to set
	 */
	public void setProductGuid(String productGuid)
	{
		this.put(PRODUCT_GUID, productGuid);
	}

	/**
	 * @param productMasterGuid
	 *            the productMasterGuid to set
	 */
	public void setProductMasterGuid(String productMasterGuid)
	{
		this.put(PRODUCT_MASTER_GUID, productMasterGuid);
	}

	/**
	 * @param productTitle
	 *            the productTitle to set
	 */
	public void setProductTitle(String productTitle)
	{
		this.put(PRODUCT_TITLE, productTitle);

	}
}
