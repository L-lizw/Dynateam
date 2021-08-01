/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActivity
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.wft.WorkflowTemplateActInfoMapper;
import dyna.common.systemenum.OverTimeActionEnum;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;

/**
 * 工作流模板活动节点Bean
 * 
 * @author WangLHB
 * 
 */
@Cache
@EntryMapper(WorkflowTemplateActInfoMapper.class)
public class WorkflowTemplateActInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID		= -863915324012839699L;
	private static final String	NAME					= "ACTRTNAME";

	// public static final String GUID = "GUID";
	public static final String	TEMPLATEGUID			= "TEMPLATEGUID";
	public static final String	ACTRTNAME				= "ACTRTNAME";
	// 是否是执行人
	public static final String	PERSCOPETYPE			= "PERSCOPETYPE";

	// 活动类型
	public static final String	EXECUTIONTYPE			= "EXECUTIONTYPE";
	public static final String	ISPASSED				= "ISPASSED";
	// 计划周期
	public static final String	SCHEMEPERIODS			= "SCHEMEPERIODS";

	// 超期动作
	public static final String	OVERTIMEACTION			= "OVERTIMEACTION";

	// 最大执行人数
	public static final String	MAXIMUMEXECUTORS		= "MAXIMUMEXECUTORS";

	// 通过百分比
	public static final String	PASSAGEPERCENT			= "PASSAGEPERCENT";

	public static final String	DAYSBEFORECLOSETIME		= "DAYSBEFORECLOSETIME";
	public static final String	DAYSAFTERCLOSETIME		= "DAYSAFTERCLOSETIME";

	public static final String	ERPTEMPLATEGUID			= "ERPTEMPLATEGUID";

	public static final String	ERPTEMPLATENAME			= "ERPTEMPLATENAME";
	// 是否抛转全部（流程对象抛转范围）
	public static final String	ISEXPORTALL				= "ISEXPORTALL";

	// 1：完成；2：等待；3：自动跳过 default： 2
	// public static final String OVER_TIME_ACTION_COMPLETE = "1";
	// public static final String OVER_TIME_ACTION_WAIT = "2";
	// public static final String OVER_TIME_ACTION_SKIP = "3";

	// 1：可以被跳过；0：不可以被跳过 default ：0
	public static final String	IS_PASSED_CAN			= "1";
	public static final String	IS_PASSED_NOT			= "0";

	// 1：AND； 0：OR default：1
	public static final String	EXECUTION_TYPE_AND		= "1";
	public static final String	EXECUTION_TYPE_OR		= "0";

	// 1：指定执行人；2：范围 default：1
	public static final String	PERSCOPE_TYPE_SPECIFIE	= "1";
	public static final String	PERSCOPE_TYPE_SCOPE		= "2";

	public static final String	PASSAGE_PERCENT			= "100";

	public WorkflowTemplateActInfo()
	{
		this.setPassagePercent("100");
	}

	/**
	 * @return the templateGuid
	 */
	public String getTemplateGuid()
	{
		return (String) this.get(TEMPLATEGUID);
	}

	/**
	 * @param templateGuid
	 *            the templateGuid to set
	 */
	public void setTemplateGuid(String templateGuid)
	{
		this.put(TEMPLATEGUID, templateGuid);
	}

	/**
	 * @return the actrtName
	 */
	public String getActrtName()
	{
		return (String) this.get(ACTRTNAME);
	}

	/**
	 * @param actrtName
	 *            the actrtName to set
	 */
	public void setActrtName(String actrtName)
	{
		this.put(ACTRTNAME, actrtName);
	}

	/**
	 * @return the perscopeType
	 */
	public String getPerscopeType()
	{
		return StringUtils.isNullString((String) this.get(PERSCOPETYPE)) ? PERSCOPE_TYPE_SPECIFIE : (String) this.get(PERSCOPETYPE);
	}

	/**
	 * @param perscopeType
	 *            the perscopeType to set
	 */
	public void setPerscopeType(String perscopeType)
	{
		this.put(PERSCOPETYPE, perscopeType);
	}

	/**
	 * @return the executionType
	 */
	public String getExecutionType()
	{
		return StringUtils.isNullString((String) this.get(PERSCOPETYPE)) ? EXECUTION_TYPE_AND : (String) this.get(EXECUTIONTYPE);
	}

	/**
	 * @param executionType
	 *            the executionType to set
	 */
	public void setExecutionType(String executionType)
	{
		this.put(EXECUTIONTYPE, executionType);
	}

	/**
	 * @return the isPassed
	 */
	public boolean isPassed()
	{
		return IS_PASSED_CAN.equals(this.get(ISPASSED)) ? true : false;
	}

	/**
	 * @param isPassed
	 *            the isPassed to set
	 */
	public void setPassed(boolean isPassed)
	{
		this.put(ISPASSED, isPassed == true ? IS_PASSED_CAN : IS_PASSED_NOT);
	}

	/**
	 * @return the schemePeriods
	 */
	public String getSchemePeriods()
	{
		return StringUtils.isNull(this.get(SCHEMEPERIODS)) ? "0" : (new BigDecimal(this.get(SCHEMEPERIODS).toString())).toString();

	}

	/**
	 * @param schemePeriods
	 *            the schemePeriods to set
	 */
	public void setSchemePeriods(String schemePeriods)
	{
		this.put(SCHEMEPERIODS, StringUtils.isNullString(schemePeriods) ? null : new BigDecimal(schemePeriods));
	}

	/**
	 * @return the overTimeAction
	 */
	public String getOverTimeAction()
	{
		return StringUtils.isNullString((String) this.get(OVERTIMEACTION)) ? OverTimeActionEnum.WAIT.toString() : (String) this.get(OVERTIMEACTION);

	}

	/**
	 * @param overTimeAction
	 *            the overTimeAction to set
	 */
	public void setOverTimeAction(String overTimeAction)
	{
		this.put(OVERTIMEACTION, overTimeAction);
	}

	/**
	 * @return the maximumExecutors
	 */
	public String getMaximumExecutors()
	{
		return StringUtils.isNull(this.get(MAXIMUMEXECUTORS)) ? "0" : (new BigDecimal(this.get(MAXIMUMEXECUTORS).toString()).toString());

	}

	/**
	 * @param maximumExecutors
	 *            the maximumExecutors to set
	 */
	public void setMaximumExecutors(String maximumExecutors)
	{
		this.put(MAXIMUMEXECUTORS, StringUtils.isNullString(maximumExecutors) ? null : new BigDecimal(maximumExecutors));
	}

	/**
	 * @return the passagePercent
	 */
	public String getPassagePercent()
	{
		return StringUtils.isNull(this.get(PASSAGEPERCENT)) ? null : (new BigDecimal(this.get(PASSAGEPERCENT).toString()).toString());

	}

	/**
	 * @param passagePercent
	 *            the passagePercent to set
	 */
	public void setPassagePercent(String passagePercent)
	{
		this.put(PASSAGEPERCENT, StringUtils.isNullString(passagePercent) ? null : new BigDecimal(passagePercent));
	}

	/**
	 * @return the daysBeforeCloseTime
	 */
	public String getDaysBeforeCloseTime()
	{
		return StringUtils.isNull(this.get(DAYSBEFORECLOSETIME)) ? null : (new BigDecimal(this.get(DAYSBEFORECLOSETIME).toString()).toString());
	}

	/**
	 * @param daysBeforeCloseTime
	 *            the daysBeforeCloseTime to set
	 */
	public void setDaysBeforeCloseTime(String daysBeforeCloseTime)
	{
		this.put(DAYSBEFORECLOSETIME, StringUtils.isNullString(daysBeforeCloseTime) ? null : new BigDecimal(daysBeforeCloseTime));
	}

	/**
	 * @return the daysAfterCloseTime
	 */
	public String getDaysAfterCloseTime()
	{
		return StringUtils.isNull(this.get(DAYSAFTERCLOSETIME)) ? null : (new BigDecimal(this.get(DAYSAFTERCLOSETIME).toString()).toString());

	}

	/**
	 * @param daysAfterCloseTime
	 *            the daysAfterCloseTime to set
	 */
	public void setDaysAfterCloseTime(String daysAfterCloseTime)
	{
		this.put(DAYSAFTERCLOSETIME, StringUtils.isNullString(daysAfterCloseTime) ? null : new BigDecimal(daysAfterCloseTime));
	}

	/**
	 * @return the erpTemplateGuid
	 */
	public String getErpTemplateGuid()
	{
		return (String) this.get(ERPTEMPLATEGUID);
	}

	/**
	 * @param erpTemplateGuid
	 *            the erpTemplateGuid to set
	 */
	public void setErpTemplateGuid(String erpTemplateGuid)
	{
		this.put(ERPTEMPLATEGUID, erpTemplateGuid);
	}

	/**
	 * @return the erpTemplateName
	 */
	public String getErpTemplateName()
	{
		return (String) this.get(ERPTEMPLATENAME);
	}

	/**
	 * @param erpTemplateName
	 *            the erpTemplateName to set
	 */
	public void setErpTemplateName(String erpTemplateName)
	{
		this.put(ERPTEMPLATENAME, erpTemplateName);
	}

	/**
	 * @return the isExportAll
	 */
	public boolean isExportAll()
	{
		return "1".equals(this.get(ISEXPORTALL)) ? true : false;
	}

	/**
	 * @param isExportAll
	 *            the isExportAll to set
	 */
	public void setExportAll(boolean isExportAll)
	{
		this.put(ISEXPORTALL, isExportAll == true ? "1" : "0");
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMPLATEGUID);

	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}

	@Override
	public WorkflowTemplateActInfo clone()
	{
		WorkflowTemplateActInfo actrt = (WorkflowTemplateActInfo) super.clone();
		return actrt;
	}
}
