package dyna.common.bean.erp;

import java.io.Serializable;

import dyna.common.systemenum.ERPServerType;

/**
 * 封装查询Schema的参数类
 * 
 * @author chega
 * 
 */
public class ERPQuerySchemaParameterObject implements Serializable
{
	private static final long	serialVersionUID	= 8834172737155762480L;

	private ERPServerType		ERPType;
	private String				schemaName;
	private boolean				isEC;
	private String				ECChangeType;
	private boolean				bomChange;

	public ERPServerType getERPType()
	{
		return ERPType;
	}

	public void setERPType(ERPServerType eRPType)
	{
		ERPType = eRPType;
	}

	public String getSchemaName()
	{
		return schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	public boolean isEC()
	{
		return isEC;
	}

	public void setEC(boolean isEC)
	{
		this.isEC = isEC;
	}

	public String getECChangeType()
	{
		return ECChangeType;
	}

	public void setECChangeType(String eCChangeType)
	{
		ECChangeType = eCChangeType;
	}

	public boolean isBomChange()
	{
		return bomChange;
	}

	public void setBomChange(boolean bomChange)
	{
		this.bomChange = bomChange;
	}
}
