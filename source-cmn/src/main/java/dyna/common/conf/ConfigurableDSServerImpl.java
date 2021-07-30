/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaServerConfig
 * Wanglei 2010-3-30
 */
package dyna.common.conf;


/**
 * 读取服务器配置信息, 并保存, 供服务器获取初始化参数使用.
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableDSServerImpl extends ConfigurableAdapter
{

	private String	rootDir	= null;
	private String	address	= null;
	private int		port	= 21;
	private String	pasvPorts	= null;
	private String	pasvExtAddress	= null;
	/**
	 * @return the rootDir
	 */
	public String getRootDir()
	{
		return this.rootDir;
	}

	/**
	 * @param rootDir
	 *            the rootDir to set
	 */
	public void setRootDir(String rootDir)
	{
		this.rootDir = rootDir;
	}

	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return this.address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return this.port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	public String getPasvPorts()
	{
		return pasvPorts;
	}

	public void setPasvPorts(String pasvPorts)
	{
		this.pasvPorts = pasvPorts;
	}

	public String getPassiveExternalAddress() {
		return pasvExtAddress;
	}
	
	public void setPassiveExternalAddress(String address)
	{
		this.pasvExtAddress = address;
	}



}
