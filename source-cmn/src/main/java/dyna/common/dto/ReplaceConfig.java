package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class ReplaceConfig extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 1L;
	public static final String	QASSOCIATEDTYPE		= "QASSOCIATEDTYPE";
	public static final String	ISQREVISED			= "ISQREVISED";
	public static final String	ISQRELEASE			= "ISQRELEASE";
	public static final String	TASSOCIATEDTYPE		= "TASSOCIATEDTYPE";
	public static final String	ISTREVISED			= "ISTREVISED";
	public static final String	ISTRELEASE			= "ISTRELEASE";

	public void setQASSOCIATEDTYPE(Object qAssociatedType)
	{
		this.put(QASSOCIATEDTYPE, qAssociatedType);
	}

	public void setISQREVISED(Object isQRevised)
	{
		this.put(ISQREVISED, isQRevised);
	}

	public void setISQRELEASE(Object isQRelease)
	{
		this.put(ISQRELEASE, isQRelease);
	}

	public void setTASSOCIATEDTYPE(Object tAssociatedType)
	{
		this.put(TASSOCIATEDTYPE, tAssociatedType);
	}

	public void setISTREVISED(Object isTRevised)
	{
		this.put(ISTREVISED, isTRevised);
	}

	public void setISTRELEASE(Object isTRelease)
	{
		this.put(ISTRELEASE, isTRelease);
	}

	public String getQASSOCIATEDTYPE()
	{
		return (String) this.get(QASSOCIATEDTYPE);
	}

	public String getISQREVISED()
	{
		return (String) this.get(ISQREVISED);
	}

	public String getISQRELEASE()
	{
		return (String) this.get(ISQRELEASE);
	}

	public String getTASSOCIATEDTYPE()
	{
		return (String) this.get(TASSOCIATEDTYPE);
	}

	public String getISTREVISED()
	{
		return (String) this.get(ISTREVISED);
	}

	public String getISTRELEASE()
	{
		return (String) this.get(ISTRELEASE);
	}

}
