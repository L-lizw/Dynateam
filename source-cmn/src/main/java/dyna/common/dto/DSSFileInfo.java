/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileInfo 文件信息bean
 * Wanglei 2010-9-1
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.DSSFileInfoMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 文件信息bean
 * 
 * @author Wanglei
 * 
 */
@EntryMapper(DSSFileInfoMapper.class)
public class DSSFileInfo extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= -3500487957392681556L;

	public static final String	IS_PRIMARY				= "ISPRIMARY";
	public static final String	IS_UPLOAD				= "ISUPLOAD";
	public static final String	FILE_SIZE				= "FILESIZE";
	public static final String	FILE_PATH				= "FILEPATH";
	public static final String	FILE_TYPE				= "FILETYPE";
	public static final String	EXTENTION_NAME			= "EXTENTIONNAME";
	public static final String	DESCRIPTION				= "DESCRIPTION";
	public static final String	SITE_ID					= "SITEID";
	public static final String	STORAGE_ID				= "STORAGEID";
	public static final String	RELATIVE_PATH			= "RELATIVEPATH";
	public static final String	LAST_ACCESS_TIME		= "LASTACCESSTIME";
	public static final String	LAST_ACCESS_USERGUID	= "LASTACCESSUSERGUID";
	public static final String	LAST_ACCESS_USERNAME	= "LASTACCESSUSERNAME";
	// public static final String STORAGE_HOSTNAME = "HOSTNAME"; // read only
	// public static final String STORAGE_PATH = "STORAGEPATH"; // read only
	public static final String	CLASS_GUID				= "CLASSGUID";
	public static final String	REVISION_GUID			= "REVISIONGUID";
	public static final String	ITERATION_ID			= "ITERATIONID";

	public static final String	PROCESS_GUID			= "PROCESSGUID";
	public static final String	ACTIVITY_GUID			= "ACTIVITYGUID";
	public static final String	ACTIVITY_TITLE			= "ACTIVITYTITLE";
	public static final String	START_NUMBER			= "STARTNUMBER";

	public static final String	MD5						= "MD5";

	public static final String	ECA_STATUS				= "ECASTATUS";

	public static final String	TAB_NAME				= "TABNAME";
	public static final String	TABFK_GUID				= "TABFKGUID";
	// 文件下载时保存在本地的路径
	public static final String	LOCAL_FILE_PATH			= "LOCALFILEPATH";

	public String getProcessGuid()
	{
		return (String) this.get(PROCESS_GUID);
	}

	public void setProcessGuid(String procGuid)
	{
		this.put(PROCESS_GUID, procGuid);
	}

	public String getActivityGuid()
	{
		return (String) this.get(ACTIVITY_GUID);
	}

	public void setActivityGuid(String actGuid)
	{
		this.put(ACTIVITY_GUID, actGuid);
	}

	public String getActivityTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(ACTIVITY_TITLE), lang.getType());
	}

	public void setActivityTitle(String actTitle)
	{
		this.put(ACTIVITY_TITLE, actTitle);
	}

	public String getClassGuid()
	{
		return (String) this.get(CLASS_GUID);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public String getExtentionName()
	{
		return (String) this.get(EXTENTION_NAME);
	}

	public String getFilePath()
	{
		return (String) this.get(FILE_PATH);
	}

	public long getFileSize()
	{
		return ((Number) this.get(FILE_SIZE)).longValue();
	}

	public String getFileType()
	{
		return (String) this.get(FILE_TYPE);
	}

	public String getId()
	{
		return (String) this.get("DSSFILENAME");
	}

	public Integer getIterationId()
	{
		Number iid = (Number) this.get(ITERATION_ID);
		return iid.intValue();
	}

	public String getMD5()
	{
		return (String) this.get(MD5);
	}

	@Override
	public String getName()
	{
		return (String) this.get("FILENAME");
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

	public String getRevisionGuid()
	{
		return (String) this.get(REVISION_GUID);
	}

	public String getSiteId()
	{
		return (String) this.get(SITE_ID);
	}

	public String getStorageId()
	{
		return (String) this.get(STORAGE_ID);
	}

	public boolean isPrimary()
	{
		Boolean isPrimary = BooleanUtils.getBooleanByYN((String) this.get(IS_PRIMARY));
		return isPrimary == null ? false : isPrimary.booleanValue();
	}

	public boolean isUploaded()
	{
		Boolean isUploaded = BooleanUtils.getBooleanByYN((String) this.get(IS_UPLOAD));
		return isUploaded == null ? false : isUploaded.booleanValue();
	}

	public void setClassGuid(String classGuid)
	{
		this.put(CLASS_GUID, classGuid);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public void setExtentionName(String extentionName)
	{
		this.put(EXTENTION_NAME, extentionName);
	}

	public void setFilePath(String filePath)
	{
		this.put(FILE_PATH, filePath);
	}

	public void setFileSize(long size)
	{
		this.put(FILE_SIZE, BigDecimal.valueOf(size));
	}

	public void setFileType(String fileType)
	{
		this.put(FILE_TYPE, fileType);
	}

	public void setId(String id)
	{
		this.put("DSSFILENAME", id);
	}

	public void setIterationId(Integer iterationId)
	{
		if (iterationId != null)
		{
			this.put(ITERATION_ID, new BigDecimal(iterationId));
		}
		else
		{
			this.put(ITERATION_ID, null);
		}

	}

	public void setMD5(String md5)
	{
		this.put(MD5, md5);
	}

	@Override
	public void setName(String name)
	{
		this.put("FILENAME", name);
	}

	public void setPrimary(Boolean isPrimary)
	{
		this.put(IS_PRIMARY, BooleanUtils.getBooleanStringYN(isPrimary));
	}

	public void setRevision(String revisionGuid)
	{
		this.put(REVISION_GUID, revisionGuid);
	}

	public void setSiteId(String siteId)
	{
		this.put(SITE_ID, siteId);
	}

	public void setStorageId(String storageId)
	{
		this.put(STORAGE_ID, storageId);
	}

	public void setUploaded(Boolean isUploaded)
	{
		this.put(IS_UPLOAD, BooleanUtils.getBooleanStringYN(isUploaded));
	}

	public void setRelativePath(String relativePath)
	{
		this.put(RELATIVE_PATH, relativePath);
	}

	public String getRelativePath()
	{
		return (String) this.get(RELATIVE_PATH);
	}

	public String getIcon16()
	{
		return (String) super.get(FileType.ICON16);
	}

	public String getIcon32()
	{
		return (String) super.get(FileType.ICON32);
	}

	public Date getLastAccessTime()
	{
		return (Date) super.get(LAST_ACCESS_TIME);
	}

	public String getLastAccessUser()
	{
		return (String) super.get(LAST_ACCESS_USERGUID);
	}

	public void setLastAccessUser(String userGuid)
	{
		this.put(LAST_ACCESS_USERGUID, userGuid);
	}

	public String getLastAccessUserName()
	{
		return (String) super.get(LAST_ACCESS_USERNAME);
	}

	public void setTabName(String tabName)
	{
		this.put(TAB_NAME, tabName);
	}

	public String getTabName()
	{
		return (String) super.get(TAB_NAME);
	}

	public void setTabFkGuid(String fkGuid)
	{
		this.put(TABFK_GUID, fkGuid);
	}

	public String getTabFkGuid()
	{
		return (String) super.get(TABFK_GUID);
	}

	public void setStartNumber(int startNumber)
	{
		super.put(START_NUMBER, new BigDecimal(startNumber));
	}

	public Integer getStartNumber()
	{
		if (super.get(START_NUMBER) == null)
		{
			return 0;
		}
		return ((Number) super.get(START_NUMBER)).intValue();
	}

	public String getLocalFilePath()
	{
		return (String) super.get(LOCAL_FILE_PATH);
	}

	public void setLocalFilePath(String localFilePath)
	{
		super.put(LOCAL_FILE_PATH, localFilePath);
	}
}
