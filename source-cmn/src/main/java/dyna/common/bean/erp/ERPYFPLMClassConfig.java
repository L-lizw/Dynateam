/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYFPLMClassConfig
 * wangweixia 2012-3-12
 */
package dyna.common.bean.erp;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.erp.ERPBOConfig;
import dyna.common.dto.template.wft.WorkflowTemplateActPerformerInfo;

/**
 * ERPYF集成PLM类权限配置
 * 
 * @author wangweixia
 * 
 */
public class ERPYFPLMClassConfig extends SystemObjectImpl implements SystemObject
{

	private static final long					serialVersionUID			= -1245701888017912162L;

	// 所有对象可以做集成的类信息
	public List<ERPBOConfig>					boinfoList					= null;
	// 成功集成后需通知的人员
	public List<WorkflowTemplateActPerformerInfo>	endNoticePersonList			= null;
	// 可以使用集成按钮的人员
	public List<WorkflowTemplateActPerformerInfo>	canUseTemplatePersonList	= null;

	// 失败集成后需通知的人员
	public List<WorkflowTemplateActPerformerInfo>	failedendNoticePersonList	= null;

	// 多公司别
	List<String>								moreCompanyList				= null;

	/**
	 * @return the moreCompanyList
	 */
	public List<String> getMoreCompanyList()
	{
		return moreCompanyList;
	}

	/**
	 * @param moreCompanyList
	 *            the moreCompanyList to set
	 */
	public void setMoreCompanyList(List<String> moreCompanyList)
	{
		this.moreCompanyList = moreCompanyList;
	}

	/**
	 * @return the boinfoList
	 */
	public List<ERPBOConfig> getBoinfoList()
	{
		return boinfoList;
	}

	/**
	 * @param boinfoList
	 *            the boinfoList to set
	 */
	public void setBoinfoList(List<ERPBOConfig> boinfoList)
	{
		this.boinfoList = boinfoList;
	}

	/**
	 * @return the endNoticePersonList
	 */
	public List<WorkflowTemplateActPerformerInfo> getEndNoticePersonList()
	{
		return endNoticePersonList;
	}

	/**
	 * @param endNoticePersonList
	 *            the endNoticePersonList to set
	 */
	public void setEndNoticePersonList(List<WorkflowTemplateActPerformerInfo> endNoticePersonList)
	{
		this.endNoticePersonList = endNoticePersonList;
	}

	/**
	 * @return the canUseTemplatePersonList
	 */
	public List<WorkflowTemplateActPerformerInfo> getCanUseTemplatePersonList()
	{
		return canUseTemplatePersonList;
	}

	/**
	 * @param canUseTemplatePersonList
	 *            the canUseTemplatePersonList to set
	 */
	public void setCanUseTemplatePersonList(List<WorkflowTemplateActPerformerInfo> canUseTemplatePersonList)
	{
		this.canUseTemplatePersonList = canUseTemplatePersonList;
	}

	/**
	 * @return the endNoticePersonList
	 */
	public List<WorkflowTemplateActPerformerInfo> getFaildEndNoticePersonList()
	{
		return failedendNoticePersonList;
	}

	/**
	 * @param endNoticePersonList
	 *            the endNoticePersonList to set
	 */
	public void setFaildEndNoticePersonList(List<WorkflowTemplateActPerformerInfo> faildendNoticePersonList)
	{
		failedendNoticePersonList = faildendNoticePersonList;
	}

}
