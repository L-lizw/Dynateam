/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CrossRequestModel
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * @author WangLHB
 * 
 */
public class CrossRequestDataModel extends CrossModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1697744876956673790L;

	@Attribute(name = "id", required = false)
	private String				id					= null;

	@Element(name = "host", required = false)
	private HostModel			hostModel			= null;

	@Element(name = "service", required = false)
	private ServiceModel		serviceModel		= null;

	@Element(name = "payload", required = false, data = true)
	private String				payLoad				= null;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @param hostModel
	 *            the hostModel to set
	 */
	public void setHostModel(HostModel hostModel)
	{
		this.hostModel = hostModel;
	}

	/**
	 * @return the hostModel
	 */
	public HostModel getHostModel()
	{
		return this.hostModel;
	}

	/**
	 * @param serviceModel
	 *            the serviceModel to set
	 */
	public void setServiceModel(ServiceModel serviceModel)
	{
		this.serviceModel = serviceModel;
	}

	/**
	 * @return the serviceModel
	 */
	public ServiceModel getServiceModel()
	{
		return this.serviceModel;
	}

	/**
	 * @param payLoad
	 *            the payLoad to set
	 */
	public void setPayLoad(String payLoad)
	{
		this.payLoad = payLoad;
	}

	/**
	 * @return the payLoad
	 */
	public String getPayLoad()
	{
		return this.payLoad;
	}

}
