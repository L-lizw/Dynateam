/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InputObjectSubmitLibEventImpl
 * Wanglei 2011-11-28
 */
package dyna.common.bean.data.input;

import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;

/**
 * 入库事件的脚本输入对象
 * 
 * @author Wanglei
 * 
 */
public class InputObjectSubmitLibEventImpl extends AbstractInputObject
{
	private static final long	serialVersionUID	= -5770665188586852006L;

	private FoundationObject	foundationObject	= null;
	private String				fromLibFolderGuid	= null;
	private List<String>		toLibFolderGuidList	= null;

	/**
	 * @param foundationObject
	 * @param fromLibFolderGuid
	 * @param toLibFolderGuidList
	 */
	public InputObjectSubmitLibEventImpl(FoundationObject foundationObject, String fromLibFolderGuid, List<String> toLibFolderGuidList)
	{
		super();
		this.foundationObject = foundationObject;
		this.fromLibFolderGuid = fromLibFolderGuid;
		this.toLibFolderGuidList = toLibFolderGuidList;
	}

	/**
	 * @return the foundationObject
	 */
	public FoundationObject getFoundationObject()
	{
		return this.foundationObject;
	}

	/**
	 * @return the fromLibFolderGuid
	 */
	public String getFromLibFolderGuid()
	{
		return this.fromLibFolderGuid;
	}

	/**
	 * @return the toLibFolderGuidList
	 */
	public List<String> getToLibFolderGuidList()
	{
		return this.toLibFolderGuidList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.InputObject#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		return this.foundationObject.getObjectGuid();
	}

}
