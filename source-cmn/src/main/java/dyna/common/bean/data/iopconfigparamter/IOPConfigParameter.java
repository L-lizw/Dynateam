package dyna.common.bean.data.iopconfigparamter;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class IOPConfigParameter extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 7896742695625365271L;

	public static final String	MASTERGUID			= "MASTERGUID";
	public static final String	SEQUENCE			= "DATASEQ";
	public List<IOPColumnValue>	valueList			= null;
	public static final String	TITLES				= "TITLES";

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public List<IOPColumnValue> getValueList()
	{
		return valueList;
	}

	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, sequence);
	}

	public void setValueList(List<IOPColumnValue> valueList)
	{
		this.valueList = valueList;
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}
}
