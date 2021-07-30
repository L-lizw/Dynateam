/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigRuleBOLM  '规则'的配置
 * caogc 2010-11-12
 */
package dyna.common.dto;

import java.util.Date;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * ConfigRuleBOLM 规则配置
 * 包括：生效规则、失效规则、入库规则
 * 
 * @author caogc
 * 
 */
public class ConfigRuleBOLM extends SystemObjectImpl implements SystemObject
{

	private static final long			serialVersionUID		= -5168560756324214395L;
	public static final String			GUID					= "GUID";
	public static final String			BOGUID					= "BOGUID";
	// 20130115变更，7.默认值为新建即生效
	// public static final String EFFECTIVE_RULE = "EFFECTIVERULE";
	// public static final String OBSOLETE_RULE = "OBSOLETERULE";
	// 20130115变更，7.默认值为生效即入库
	// public static final String COMMIT_RULE = "COMMITRULE";
	public static final String			IS_FROM_PARENT			= "ISFROMPARENT";

	// 0、只能有一个修订版；1、可以有多个修订版
	// public static final String MULTI_RVERSION = "MULTIRVERSION";

	private List<ConfigRuleBOPhaseSet>	boPhaseSetList			= null;

	/**
	 * 1:修订后生命周期阶段“返回到第一阶段”; 0:保持上一个阶段不变
	 */
	public static final String			ISREVISETOFIRSTPHRASE	= "ISREVISETOFIRSTPHRASE";

	/**
	 * @return the isFromParent
	 */
	public boolean isFromParent()
	{
		if (this.get(IS_FROM_PARENT) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanBy10((String) this.get(IS_FROM_PARENT));
	}

	/**
	 * @param isFromParent
	 *            the isFromParent to set
	 */
	public void setIsFromParent(boolean isFromParent)
	{
		this.put(IS_FROM_PARENT, BooleanUtils.getBooleanString10(isFromParent));
	}

	// /**
	// * @return the commitRule
	// */
	// public CommitRuleEnum getCommitRule()
	// {
	// if (this.get(COMMIT_RULE) == null)
	// {
	// // return null;
	// return CommitRuleEnum.EFFECTIVECOMMIT;
	// }
	// return CommitRuleEnum.typeValueOf((String) this.get(COMMIT_RULE));
	// }
	//
	// /**
	// * @param commitRule
	// * the commitRule to set
	// */
	// public void setCommitRule(CommitRuleEnum commitRule)
	// {
	// this.put(COMMIT_RULE, commitRule.toString());
	// }

	// /**
	// * @return the obsoleteRule
	// */
	// public ObsoleteRuleEnum getObsoleteRule()
	// {
	// if (this.get(OBSOLETE_RULE) == null)
	// {
	// return null;
	// }
	// return ObsoleteRuleEnum.typeValueOf((String) this.get(OBSOLETE_RULE));
	// }
	//
	// /**
	// * @param obsoleteRule
	// * the obsoleteRule to set
	// */
	// public void setObsoleteRule(ObsoleteRuleEnum obsoleteRule)
	// {
	// this.put(OBSOLETE_RULE, obsoleteRule.toString());
	// }

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the effectiveRule
	 */
	// public EffectiveRuleEnum getEffectiveRule()
	// {
	// if (this.get(EFFECTIVE_RULE) == null)
	// {
	// // return null;
	// return EffectiveRuleEnum.CREATE;
	// }
	// return EffectiveRuleEnum.typeValueOf((String) this.get(EFFECTIVE_RULE));
	// }

	/**
	 * @return the boguid
	 */
	public String getBOGuid()
	{
		return (String) this.get(BOGUID);
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
	 * @param effectiveRule
	 *            the effectiveRule to set
	 */
	// public void setEffectiveRule(EffectiveRuleEnum effectiveRule)
	// {
	// this.put(EFFECTIVE_RULE, effectiveRule.toString());
	// }

	/**
	 * @param boGuid
	 *            the boGuid to set
	 */
	public void setBoGuid(String boGuid)
	{
		this.put(BOGUID, boGuid);
	}

	/**
	 * @return the boPhaseSetList
	 */
	public List<ConfigRuleBOPhaseSet> getBoPhaseSetList()
	{
		return this.boPhaseSetList;
	}

	/**
	 * @param boPhaseSetList
	 *            the boPhaseSetList to set
	 */
	public void setBoPhaseSetList(List<ConfigRuleBOPhaseSet> boPhaseSetList)
	{
		this.boPhaseSetList = boPhaseSetList;
	}

	/**
	 * @param multiRevision
	 *            the multiRevision to set
	 */
	// public void setMultiRevision(boolean multiRevision)
	// {
	// this.put(MULTI_RVERSION, multiRevision);
	// }
	//
	// /**
	// * @return the multiRevision
	// */
	// public boolean isMultiRevision()
	// {
	// return "1".equals(this.get(MULTI_RVERSION));
	// }

	/**
	 * @return the isReviseToFirstPhrase
	 */
	public boolean isReviseToFirstPhrase()
	{
		return StringUtils.isNullString((String) this.get(ISREVISETOFIRSTPHRASE)) ? true : BooleanUtils.getBooleanBy10((String) this.get(ISREVISETOFIRSTPHRASE));
	}

	/**
	 * @param isReviseToFirstPhrase
	 *            the isReviseToFirstPhrase to set
	 */
	public void setReviseToFirstPhrase(boolean isReviseToFirstPhrase)
	{
		this.put(ISREVISETOFIRSTPHRASE, BooleanUtils.getBooleanString10(isReviseToFirstPhrase));
	}
}
