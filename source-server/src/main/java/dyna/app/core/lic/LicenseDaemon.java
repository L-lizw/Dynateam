/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LicenseDaemon license守护
 * Wanglei 2011-4-20
 */
package dyna.app.core.lic;

import dyna.common.exception.ServiceRequestException;
import dyna.net.service.brs.LIC;

/**
 * license守护
 * 
 * @author Wanglei
 * 
 */
public interface LicenseDaemon
{
	/**
	 * 返回license有效期
	 * 
	 * @return
	 */
	public long[] getLicensePeriod();

	/**
	 * 获取被占用的license数
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int[] getLicenseInUse(LIC lic) throws ServiceRequestException;

	/**
	 * 获取license的节点数
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public int[] getLicenseNode() throws ServiceRequestException;

	/**
	 * 查找license中所有可用模块的列表<br>
	 * 模块名称以逗号","分隔
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getLicenseModules() throws ServiceRequestException;

	/**
	 * 判断是否有指定产品模块的授权
	 * 
	 * @param moduleName
	 *            产品模块
	 * @return
	 */
	public boolean hasLicence(String moduleName);

	/**
	 * 根据登录用户重置license占用情况
	 * 
	 * @param lic
	 * @return
	 */
	public boolean resetLicense(LIC lic);

	/**
	 * 请求license
	 * 
	 * @return
	 */
	public boolean requestLicense(LIC lic,String moduleName) throws LicenseException;

	/**
	 * 释放license
	 */
	public void releaseLicense(String moduleName);

	/**
	 * 返回授权ID信息
	 * 
	 * @return
	 */
	public String getSystemIdentification();
	/**
	 * 返回是不是运行在虚拟机中
	 * 
	 * @return
	 */
	public boolean isVM();

}
