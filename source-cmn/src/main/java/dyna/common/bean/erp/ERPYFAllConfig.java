/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYFAllConfig
 * wangweixia 2012-3-12
 */
package dyna.common.bean.erp;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.erp.ERPServiceConfig;

/**
 * @author wangweixia
 * 
 */
public class ERPYFAllConfig extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 7288221409952971479L;
	// ERPServiceConfig对象
	public ERPServiceConfig		erpServiceConfig	= null;
	// ERPYFPLMClassConfig对象
	public ERPYFPLMClassConfig	erpYFPLMClassConfig	= null;

	/**
	 * @return the erpServiceConfig
	 */
	public ERPServiceConfig getErpServiceConfig()
	{
		return erpServiceConfig;
	}

	/**
	 * @param erpServiceConfig
	 *            the erpServiceConfig to set
	 */
	public void setErpServiceConfig(ERPServiceConfig erpServiceConfig)
	{
		this.erpServiceConfig = erpServiceConfig;
	}

	/**
	 * @return the erpYFPLMClassConfig
	 */
	public ERPYFPLMClassConfig getErpYFPLMClassConfig()
	{
		return erpYFPLMClassConfig;
	}

	/**
	 * @param erpYFPLMClassConfig
	 *            the erpYFPLMClassConfig to set
	 */
	public void setErpYFPLMClassConfig(ERPYFPLMClassConfig erpYFPLMClassConfig)
	{
		this.erpYFPLMClassConfig = erpYFPLMClassConfig;
	}

}
