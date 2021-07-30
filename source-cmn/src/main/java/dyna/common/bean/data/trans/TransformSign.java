package dyna.common.bean.data.trans;

import java.util.List;

import dyna.common.bean.data.SystemObjectImpl;

public class TransformSign extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 6564841658649248759L;

	public static String				SIGN_NAME			= "SOLUTIONNAME";

	public static String				IS_DELETE			= "ISDELETE";

	private List<TransformSignParam>	paramList			= null;

	public String getSignName()
	{
		return (String) this.get(SIGN_NAME);
	}

	public void setSignName(String signName)
	{
		this.put(SIGN_NAME, signName);
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

	public List<TransformSignParam> getParamList()
	{
		return this.paramList;
	}

	public void setParamList(List<TransformSignParam> paramList)
	{
		this.paramList = paramList;
	}

	@Override
	public String getName()
	{
		return (String) super.get(SIGN_NAME);
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub
		super.put(SIGN_NAME,name);
	}
	
}
