package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class ModelSync extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7839993552695770276L;

	public static final String	SYNCNAME			= "MODELNAME";
	public static final String	DESCRIPTION			= "DESCRIPTION";
	public static final String	VERSION				= "VERSION";
	public static final String	OMGUID				= "OMGUID";
	public static final String	SYNCIP				= "SYNCIP";
	public static final String	REMARKS				= "REMARKS";
	public static final String	OMNAME				= "OMNAME";

	public String getSyncName()
	{
		return (String) this.get(SYNCNAME);
	}

	public void setSyncName(String syncName)
	{
		this.put(SYNCNAME, syncName);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public String getOmName()
	{
		return (String) this.get(OMNAME);
	}

	public void setOmName(String omName)
	{
		this.put(OMNAME, omName);
	}

	public String getOmGuid()
	{
		return (String) this.get(OMGUID);
	}

	public void setOmGuid(String omGuid)
	{
		this.put(OMGUID, omGuid);
	}

	public String getVersion()
	{
		return (String) this.get(VERSION);
	}

	public void setVersion(String version)
	{
		this.put(VERSION, version);
	}

	public String getRemarks()
	{
		return (String) this.get(REMARKS);
	}

	public void setRemarks(String remarks)
	{
		this.put(REMARKS, remarks);
	}

	public String getSyncIP()
	{
		return (String) this.get(SYNCIP);
	}

	public void setSyncIP(String syncIp)
	{
		this.put(SYNCIP, syncIp);
	}
}
