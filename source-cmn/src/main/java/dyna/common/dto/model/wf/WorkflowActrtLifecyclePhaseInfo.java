package dyna.common.dto.model.wf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.wf.WorkflowActrtLifecyclePhaseInfoMapper;

import java.math.BigDecimal;

@Cache
@EntryMapper(WorkflowActrtLifecyclePhaseInfoMapper.class)
public class WorkflowActrtLifecyclePhaseInfo extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3392079065980971300L;

	public static final String	MAWFACTFK			= "MAWFACTFK";

	public static final String	LCMASTERGUID		= "LCMASTERGUID";

	public static final String	FROMLCPHASEGUID		= "FROMLCPHASEGUID";

	public static final String	TOLCPHASEGUID		= "TOLCPHASEGUID";

	public static final String	SEQUENCE			= "DATASEQ";

	public String getMAWFActfk()
	{
		return (String) this.get(MAWFACTFK);
	}

	public void setMAWFActfk(String mawfactfk)
	{
		this.put(MAWFACTFK, mawfactfk);
	}

	public String getLCMasterGuid()
	{
		return (String) this.get(LCMASTERGUID);
	}

	public void setLCMasterGuid(String lcMasterGuid)
	{
		this.put(LCMASTERGUID, lcMasterGuid);
	}

	public String getFromLCPhaseGuid()
	{
		return (String) this.get(FROMLCPHASEGUID);
	}

	public void setFromLCPhaseGuid(String fromLCPhaseGuid)
	{
		this.put(FROMLCPHASEGUID, fromLCPhaseGuid);
	}

	public String getToLCPhaseGuid()
	{
		return (String) this.get(TOLCPHASEGUID);
	}

	public void setToLCPhaseGuid(String toLCPhaseGuid)
	{
		this.put(TOLCPHASEGUID, toLCPhaseGuid);
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
