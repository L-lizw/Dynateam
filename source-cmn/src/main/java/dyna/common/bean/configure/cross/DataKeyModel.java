/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataKeyModel
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/**
 * @author WangLHB
 * 
 */
public class DataKeyModel extends CrossModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1423575437566565050L;

	@Attribute(name = "type", required = false)
	private String				type				= null;

	@ElementList(required = false, inline = true)
	private List<KeyModel>		keyList				= null;

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	public List<KeyModel> getKeyList()
	{
		return keyList;
	}

	public void setKeyList(List<KeyModel> keyList)
	{
		this.keyList = keyList;
	}
}
