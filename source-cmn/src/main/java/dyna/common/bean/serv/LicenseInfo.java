package dyna.common.bean.serv;

import dyna.common.bean.data.SystemObjectImpl;

public class LicenseInfo extends SystemObjectImpl
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4134450969000774472L;

	public static final String	TITLE				= "TITLE";
	public static final String	INCLUDE				= "INCLUDE";
	public static final String	NODE				= "NODE";
	public static final String	INUSE				= "INUSE";

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public void setInclude(String module)
	{
		this.put(INCLUDE, module);
	}

	public void setNode(int num)
	{
		this.put(NODE, num);
	}

	public void setInUse(int num)
	{
		this.put(INUSE, num);
	}

}
