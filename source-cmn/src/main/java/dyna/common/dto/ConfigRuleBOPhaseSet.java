/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigRuleBOEctemplate  处于当前生命周期的数据能否修订
 * jianghl 2012-2-13
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ConfigRuleBOPhaseSetMapper;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * ConfigRuleBORevision 处于当前生命周期的数据能否修订
 * 
 * @author jianghl
 * 
 */
@EntryMapper(ConfigRuleBOPhaseSetMapper.class)
public class ConfigRuleBOPhaseSet extends SystemObjectImpl implements SystemObject
{

	private static final long				serialVersionUID			= -5168560756324214395L;
	public static final String				GUID						= "GUID";
	public static final String				BOLM_GUID					= "BOLMGUID";
	public static final String				PHASE_GUID					= "LCPHASEGUID";
	public static final String				IS_REVISION					= "ISREVISION";

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the BOLMGuid
	 */
	public String getBOLMGuid()
	{
		return (String) this.get(BOLM_GUID);
	}

	/**
	 * @param CreateTime
	 *            the CreateTime to set
	 */
	public void setCreateTime(Date createTime)
	{
		this.put(CREATE_TIME, createTime);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @param bolmGuid
	 *            the bolmGuid to set
	 */
	public void setBOLMGuid(String bolmGuid)
	{
		this.put(BOLM_GUID, bolmGuid);
	}

	/**
	 * @return the PhaseGuid
	 */
	public String getPhaseGuid()
	{
		return (String) this.get(PHASE_GUID);
	}

	/**
	 * @param PhaseGuid
	 *            the PhaseGuid to set
	 */
	public void setPhaseGuid(String phaseGuid)
	{
		this.put(PHASE_GUID, phaseGuid);
	}

	/**
	 * @return the isRevision
	 */
	public boolean isRevision()
	{
		return StringUtils.isNullString((String) this.get(IS_REVISION)) ? false : BooleanUtils.getBooleanBy10((String) this.get(IS_REVISION));
	}

	/**
	 * @param isRevision
	 *            the isRevision to set
	 */
	public void setRevision(boolean isRevision)
	{
		this.put(IS_REVISION, BooleanUtils.getBooleanString10(isRevision));
	}


}
