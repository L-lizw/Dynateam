/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPWFMoreCompanies
 * wangweixia 2012-4-5
 */
package dyna.common.dto.erp;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 用于ERP中取多公司资料的配置
 * <br/>目前易拓和Workflow中的运营中心是保存在数据库中的，易飞的公司别是保存在yfconf.xml中
 * @author wangweixia
 * 
 */

public class ERPMoreCompanies extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 2635748039455400888L;
	private static final String	GUID				= "GUID";
	private static final String	TEMPLATEGUID		= "TEMPLATEGUID";
	// 公司别代号
	private static final String	COMPANYDH			= "COMPANYDH";
	// 公司别的简称
	private static final String	COMPANYJC			= "COMPANYJC";

	// 标识位：是WorkFlow erp还是易拓ERP
	private static final String	ERPTYPEFLAG			= "ERPTYPEFLAG";

	@Override
	public String getGuid()
	{
		return (String) get(GUID);
	}

	@Override
	public void setGuid(String guid)
	{
		put(GUID, guid);
	}

	public String getTemplateGuid()
	{
		return (String) get(TEMPLATEGUID);
	}

	public void setTemplateGuid(String templateGuid)
	{
		put(TEMPLATEGUID, templateGuid);
	}

	/**
	 * @return the companydh
	 */
	public String getCompanydh()
	{
		return (String) get(COMPANYDH);
	}

	public void setCompanydh(String companydh)
	{
		put(COMPANYDH, companydh);
	}

	/**
	 * @return the companyjc
	 */
	public String getCompanyjc()
	{
		return (String) get(COMPANYJC);
	}

	public void setCompanyjc(String companyjc)
	{
		put(COMPANYJC, companyjc);
	}

	public String getERPTypeFlag()
	{
		return (String) get(ERPTYPEFLAG);
	}

	public void setERPTypeFlag(String flag)
	{
		put(ERPTYPEFLAG, flag);
	}
}
