package dyna.common.dto.erp.tmptab;

import dyna.common.bean.data.SystemObjectImpl;

public class ERPTempTableInfo extends SystemObjectImpl
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 212919966547588329L;
	private String				ERPTYPE				= "ERPTYPE";
	private String				FACTORY				= "FACTORY";
	private String				SERVERIP			= "SERVERIP";
	private String				BASETABLENAME		= "BASETABLENAME";

	public String getERPTYPE()
	{
		return (String) this.get(ERPTYPE);
	}

	public void setERPTYPE(String erptype)
	{
		this.put(ERPTYPE, erptype);
	}

	public String getFACTORY()
	{
		return (String) this.get(FACTORY);
	}

	public void setFACTORY(String factory)
	{
		this.put(FACTORY, factory);
	}

	public String getSERVERIP()
	{
		return (String) this.get(SERVERIP);
	}

	public void setSERVERIP(String serverip)
	{
		this.put(SERVERIP, serverip);
	}

	public String getBASETABLENAME()
	{
		return (String) this.get(BASETABLENAME);
	}

	public void setBASETABLENAME(String basetablename)
	{
		this.put(BASETABLENAME, basetablename);
	}

}
