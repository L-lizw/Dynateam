/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Guid的包装，包括实例和类的guid等数据
 * sam Jun 24, 2010
 */
package dyna.common.bean.data;

import java.io.Serializable;

import dyna.common.bean.data.input.AbstractInputObject;

/**
 * 存放instance guid, classguid, classname, masterguid, folderguid, bizObjectName等<br>
 * 
 * toString: classguid$classname$guid$masterguid$folderguid$bizObjectName
 */
public class ObjectGuid extends AbstractInputObject implements InputObject, Serializable
{
	private static final long	serialVersionUID	= 681590046356649948L;

	private String				guid				= null;
	private String				commitFolderGuid	= null;
	private String				classGuid			= null;
	private String				className			= null;
	private String				masterGuid			= null;
	private boolean				isMaster			= false;
	private boolean				hasAuth				= true;
	private String				bizObjectGuid		= null;

	public ObjectGuid()
	{
		// do nothing
	}

	public ObjectGuid(ObjectGuid srcObjectGuid)
	{
		this(srcObjectGuid.getClassGuid(), //
				srcObjectGuid.getClassName(), //
				srcObjectGuid.getGuid(), //
				srcObjectGuid.getMasterGuid(), //
				srcObjectGuid.isMaster(), //
				srcObjectGuid.getBizObjectGuid(), //
				srcObjectGuid.getCommitFolderGuid());//
		this.setScriptContext(srcObjectGuid.getScriptContext());
	}

	public ObjectGuid(String className, String commitFolderGuid)
	{
		this.className = className;
		this.commitFolderGuid = commitFolderGuid;
	}

	public ObjectGuid(String className, String guid, String commitFolderGuid)
	{
		this.className = className;
		this.guid = guid;
		this.commitFolderGuid = commitFolderGuid;
	}

	public ObjectGuid(String classGuid, String className, String guid, String commitFolderGuid)
	{
		this.classGuid = classGuid;
		this.className = className;
		this.guid = guid;
		this.commitFolderGuid = commitFolderGuid;
	}

	public ObjectGuid(String classGuid, String className, String guid, String masterGuid, boolean isMaster, String commitFolderGuid)
	{
		this.classGuid = classGuid;
		this.className = className;
		this.guid = guid;
		this.masterGuid = masterGuid;
		this.isMaster = isMaster;
		this.commitFolderGuid = commitFolderGuid;
	}

	public ObjectGuid(String classGuid, String className, String guid, String masterGuid, boolean isMaster, String bizObjectGuid, String commitFolderGuid)
	{
		this.classGuid = classGuid;
		this.className = className;
		this.guid = guid;
		this.masterGuid = masterGuid;
		this.isMaster = isMaster;
		this.bizObjectGuid = bizObjectGuid;
		this.commitFolderGuid = commitFolderGuid;
	}

	public ObjectGuid(String classGuid, String className, String guid, String masterGuid, String bizObjectGuid, String commitFolderGuid)
	{
		this.classGuid = classGuid;
		this.className = className;
		this.guid = guid;
		this.masterGuid = masterGuid;
		this.bizObjectGuid = bizObjectGuid;
		this.commitFolderGuid = commitFolderGuid;
	}

	public ObjectGuid(String classGuid, String className, String guid, String masterGuid, String commitFolderGuid)
	{
		this.classGuid = classGuid;
		this.className = className;
		this.guid = guid;
		this.masterGuid = masterGuid;
		this.commitFolderGuid = commitFolderGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.InputObject#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		ObjectGuid og = null;
		if (!(obj instanceof ObjectGuid))
		{
			return false;
		}
		og = (ObjectGuid) obj;

		if (this.classGuid != null && this.classGuid.equals(og.getClassGuid()) && this.guid != null && this.guid.equals(og.getGuid()) || this.classGuid == null
				&& og.getClassGuid() == null && this.guid == null && og.getGuid() == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @return the bizObjectGuid
	 */
	public String getBizObjectGuid()
	{
		return this.bizObjectGuid;
	}

	/**
	 * @return the classGuid
	 */
	public String getClassGuid()
	{
		return this.classGuid;
	}

	/**
	 * @return the className
	 */
	public String getClassName()
	{
		if (this.className == null)
		{
			return null;
		}
		else
		{
			return this.className;
		}
	}

	public String getCommitFolderGuid()
	{
		return this.commitFolderGuid;
	}

	/**
	 * @return the guid
	 */
	public String getGuid()
	{
		return this.guid;
	}

	public String getMasterGuid()
	{
		return this.masterGuid;
	}

	/**
	 * @return the folderGuid
	 */
	public boolean isMaster()
	{
		return this.isMaster;
	}

	/**
	 * @param bizObjectGuid
	 *            the bizObjectGuid to set
	 */
	public void setBizObjectGuid(String bizObjectGuid)
	{
		this.bizObjectGuid = bizObjectGuid;
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		this.classGuid = classGuid;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * @param commitFolderGuid
	 *            the commitFolderGuid to set
	 */
	public void setCommitFolderGuid(String commitFolderGuid)
	{
		this.commitFolderGuid = commitFolderGuid;
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	/**
	 * @param isMaster
	 *            the isMaster to set
	 */
	public void setIsMaster(boolean isMaster)
	{
		this.isMaster = isMaster;
	}

	public void setMasterGuid(String masterGuid)
	{
		this.masterGuid = masterGuid;
	}

	/**
	 * @param hasAuth
	 *            the hasAuth to set
	 */
	public void setHasAuth(boolean hasAuth)
	{
		this.hasAuth = hasAuth;
	}

	public boolean hasAuth()
	{
		return this.hasAuth;
	}

	/**
	 * classGuid$className$guid$masterGuid$commitFolderGuid
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.classGuid + "$" + this.className + "$" + this.guid + "$" + this.masterGuid + "$" + this.isMaster + "$" + this.bizObjectGuid + "$" + this.commitFolderGuid;
	}

}
