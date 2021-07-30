/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowLifecyclePhase
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 工作流生命周期阶段
 * 
 * @author Jiagang
 * 
 */
@Cache
public class WorkflowLifecyclePhaseInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 8791583082599496861L;

	public static final String	MAWFFK				= "MAWFFK";

	public static final String	LCMASTERGUID		= "LCMASTERGUID";

	public static final String	LCPHASEGUID			= "LCPHASEGUID";

	public static final String	SEQUENCE			= "DATASEQ";

	private String				lifecycleName		= null;

	private String				phaseName			= null;

	/**
	 * @return the lifecycleName
	 */
	public String getLifecycleName()
	{
		return this.lifecycleName;
	}

	/**
	 * @param lifecycleName
	 *            the lifecycleName to set
	 */
	public void setLifecycleName(String lifecycleName)
	{
		this.lifecycleName = lifecycleName;
	}

	/**
	 * @return the phaseName
	 */
	public String getPhaseName()
	{
		return this.phaseName;
	}

	/**
	 * @param phaseName
	 *            the phaseName to set
	 */
	public void setPhaseName(String phaseName)
	{
		this.phaseName = phaseName;
	}

	public void setMAWFfk(String mawffk)
	{
		this.put(MAWFFK, mawffk);
	}

	public String getMAWFfk()
	{
		return (String) this.get(MAWFFK);
	}

	public void setLCMasterGuid(String lcMasterGuid)
	{
		this.put(LCMASTERGUID, lcMasterGuid);
	}

	public String getLCMasterGuid()
	{
		return (String) this.get(LCMASTERGUID);
	}

	public void setLCPhaseGuid(String lcPhaseGuid)
	{
		this.put(LCPHASEGUID, lcPhaseGuid);
	}

	public String getLCPhaseGuid()
	{
		return (String) this.get(LCPHASEGUID);
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
