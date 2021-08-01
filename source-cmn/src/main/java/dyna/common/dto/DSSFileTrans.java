/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DSSFileTrans
 * Wanglei 2010-10-18
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.DSSFileTransMapper;
import dyna.common.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Wanglei
 *
 */
@EntryMapper(DSSFileTransMapper.class)
public class DSSFileTrans extends SystemObjectImpl implements SystemObject
{

	public static enum FileTransTypeEnum
	{
		DNL, UPL, DEL, CPY;
	}

	private static final long	serialVersionUID	= 1574966016567083870L;

	public static String		SERVER_IP			= "SERVERIP";
	public static String		SERVER_PORT			= "SERVERPORT";
	public static String		USER_ID				= "USERNAME";
	public static String		USER_PWD			= "PASSWORD";
	public static String		TYPE				= "CMDTYPE";
	public static String		FILE_GUID			= "FILEFK";
	public static String		MASTER_GUID			= "MASTERFK";

	public static String		PARAM_FILE			= "PARAMFILE";
	public static String		REAL_PATH			= "REALPATH";
	public static String		FILE_NAME			= "FILENAME";
	public static String		REAL_FILE_NAME		= "REALFILE";
	public static String		RELATIVE_PATH		= "RELATIVEPATH";

	public static String		STORAGE_ID			= "STORAGEID";
	public static String		SITE_ID				= "SITEID";

	public String getServerIP()
	{
		return (String) this.get(SERVER_IP);
	}

	public void setServerIP(String serverIP)
	{
		this.put(SERVER_IP, serverIP);
	}

	public int getServerPort()
	{
		return this.get(SERVER_PORT) == null ? 0 : ((Number) this.get(SERVER_PORT)).intValue();
	}

	public void setServerPort(int serverPort)
	{
		this.put(SERVER_PORT, serverPort);
	}

	public String getMasterFK()
	{
		return (String) this.get(MASTER_GUID);
	}

	public void setMasterFK(String masterFk)
	{
		this.put(MASTER_GUID, masterFk);
	}

	public String getStorageId()
	{
		return (String) this.get(STORAGE_ID);
	}

	public void setStorageId(String storageId)
	{
		this.put(STORAGE_ID, storageId);
	}

	public String getSiteId()
	{
		return (String) this.get(SITE_ID);
	}

	public void setSiteId(String siteId)
	{
		this.put(SITE_ID, siteId);
	}

	protected String appendSlash(String path)
	{
		if (path.charAt(path.length() - 1) != '/')
		{
			return path + '/';
		}
		else
		{
			return path;
		}
	}

	public String getFileMasterURI()
	{
		StringBuffer tempURI = new StringBuffer();
		tempURI.append(this.getUserId());
		tempURI.append(":");
		tempURI.append(this.getUserPassword());
		tempURI.append("@");
		tempURI.append(this.getServerIP());
		tempURI.append(":");
		tempURI.append(this.getServerPort());
		tempURI.append("/");
		tempURI.append(this.getMasterFK());
		return tempURI.toString();
	}

	public String getFileURI()
	{
		StringBuffer tempURI = new StringBuffer();
		tempURI.append(this.getServerIP());
		tempURI.append(":");
		tempURI.append(this.getServerPort());
		tempURI.append("/");
		tempURI.append(this.appendSlash(this.getGuid()));
		tempURI.append(this.getFileName());
		if (this.getTransType() == FileTransTypeEnum.UPL)
		{
			tempURI.append(":");
			tempURI.append(this.getParamFile());
		}
		else if (this.getTransType() == FileTransTypeEnum.DNL)
		{
			tempURI.append(":");
			tempURI.append(StringUtils.convertNULLtoString(this.getRelativePath()));
		}
		return tempURI.toString();
	}

	public String getFileFullURI()
	{
		StringBuffer tempURI = new StringBuffer();
		tempURI.append(this.getUserId());
		tempURI.append(":");
		tempURI.append(this.getUserPassword());
		tempURI.append("@");
		tempURI.append(this.getServerIP());
		tempURI.append(":");
		tempURI.append(this.getServerPort());
		tempURI.append("/");
		tempURI.append(this.appendSlash(this.getGuid()));
		tempURI.append(this.getFileName());
		if (this.getTransType() == FileTransTypeEnum.UPL)
		{
			tempURI.append(":");
			tempURI.append(this.getParamFile());
		}
		else if (this.getTransType() == FileTransTypeEnum.DNL)
		{
			tempURI.append(":");
			tempURI.append(StringUtils.convertNULLtoString(this.getRelativePath()));
		}
		return tempURI.toString();
	}

	public String getDownloadFileEncodeURL()
	{
		StringBuffer tempURI = new StringBuffer();
		tempURI.append(this.getUserId());
		tempURI.append(":");
		tempURI.append(this.getUserPassword());
		tempURI.append("@");
		tempURI.append(this.getServerIP());
		tempURI.append(":");
		tempURI.append(this.getServerPort());
		tempURI.append("/");
		tempURI.append(this.appendSlash(this.getGuid()));

		String pathEncode = this.getFileName();
		try
		{
			pathEncode = URLEncoder.encode(pathEncode, "UTF-8");
			pathEncode = pathEncode.replace("\\+", "%20");
		}
		catch (UnsupportedEncodingException e)
		{
		}

		tempURI.append(pathEncode);
		return tempURI.toString();
	}

	public void setTransType(FileTransTypeEnum type)
	{
		this.put(TYPE, type.name());
	}

	public FileTransTypeEnum getTransType()
	{
		return this.get(TYPE) == null ? null : FileTransTypeEnum.valueOf((String) this.get(TYPE));
	}

	public String getParamFile()
	{
		return (String) this.get(PARAM_FILE);
	}

	public void setParamFile(String path)
	{
		this.put(PARAM_FILE, path);
	}

	public String getFileGuid()
	{
		return (String) this.get(FILE_GUID);
	}

	public void setFileGuid(String fileGuid)
	{
		this.put(FILE_GUID, fileGuid);
	}

	public String getRealFileName()
	{
		return (String) this.get(REAL_FILE_NAME);
	}

	public void setRealFileName(String fileName)
	{
		this.put(REAL_FILE_NAME, fileName);
	}

	public String getFileName()
	{
		return (String) this.get(FILE_NAME);
	}

	public void setFileName(String fileName)
	{
		this.put(FILE_NAME, fileName);
	}

	public String getRealPath()
	{
		return (String) this.get(REAL_PATH);
	}

	public void setRealPath(String path)
	{
		this.put(REAL_PATH, path);
	}

	public String getVirtualPath()
	{
		return (String) this.get(GUID);
	}

	public String getUserPassword()
	{
		return (String) this.get(USER_PWD);
	}

	public void setUserPassword(String pwd)
	{
		this.put(USER_PWD, pwd);
	}

	public String getUserId()
	{
		return (String) this.get(USER_ID);
	}

	public void setUserId(String id)
	{
		this.put(USER_ID, id);
	}

	public String getRelativePath()
	{
		return (String) this.get(RELATIVE_PATH);
	}

}
