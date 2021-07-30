package dyna.common.bean.data.configparamter;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class OrderSearchClass extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 1L;
	public static final String	BMGUID				= "BMGUID";
	public static final String	BOINFONAME			= "BOINFONAME";

	public String getBMGuid()
	{
		return (String) this.get(BMGUID);
	}

	public void setBMGuid(String bmGuid)
	{
		this.put(BMGUID, bmGuid);
	}

	public String getBoInfoName()
	{
		return (String) this.get(BOINFONAME);
	}

	public void setBoInfoName(String boInfoName)
	{
		this.put(BOINFONAME, boInfoName);
	}
}
