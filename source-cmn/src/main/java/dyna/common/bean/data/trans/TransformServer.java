package dyna.common.bean.data.trans;

import dyna.common.bean.data.SystemObjectImpl;

public class TransformServer extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -276122475458760406L;

	public static String		SERVER_ID			= "SERVERID";
	public static String		TRANSFORM_TYPE		= "TRANSFORMTYPE";

	public static String		IS_DELETE			= "ISDELETE";

	public String getServerID()
	{
		return (String) this.get(SERVER_ID);
	}

	public void setServerID(String serverID)
	{
		this.put(SERVER_ID, serverID);
	}

	public String getTransformType()
	{
		return (String) this.get(TRANSFORM_TYPE);
	}

	public void setTransformType(String transformType)
	{
		this.put(TRANSFORM_TYPE, transformType);
	}

	public boolean isDelete()
	{
		if (this.get(IS_DELETE) == null)
		{
			return false;
		}
		return (Boolean) this.get(IS_DELETE);
	}

	public void setDelete(boolean isDelete)
	{
		this.put(IS_DELETE, isDelete);
	}
}
