package dyna.common.dto.model.wf;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

@Cache
public class WorkflowActrtStatusInfo extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 174032713514281633L;

	public static final String	MAWFACTFK			= "MAWFACTFK";

	public static final String	FROMSTATUS			= "FROMSTATUS";

	public static final String	TOSTATUS			= "TOSTATUS";

	public static final String	SEQUENCE			= "DATASEQ";

	public String getMAWFActfk()
	{
		return (String) this.get(MAWFACTFK);
	}

	public void setMAWFActfk(String mawfactfk)
	{
		this.put(MAWFACTFK, mawfactfk);
	}

	public String getFromStatus()
	{
		return (String) this.get(FROMSTATUS);
	}

	public void setFromStatus(String fromStatus)
	{
		this.put(FROMSTATUS, fromStatus);
	}

	public String getToStatus()
	{
		return (String) this.get(TOSTATUS);
	}

	public void setToStatus(String toStatus)
	{
		this.put(TOSTATUS, toStatus);
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

}
