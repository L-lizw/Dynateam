package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public abstract class AbstractCrossRequestModel extends CrossModel
{
	@Attribute(name = "id", required = false)
	private String				id					= null;

	@Attribute(name = "type", required = false)
	private String				type				= null;

	@Attribute(name = "key", required = false)
	private String				key					= null;

	@Element(name = "host", required = false)
	private HostModel			hostModel			= null;

	@Element(name = "service", required = false)
	private ServiceModel		serviceModel		= null;

	@Element(name = "datakey", required = false)
	private DataKeyModel		dataKeyModel		= null;
	
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public DataKeyModel getDataKeyModel()
	{
		return dataKeyModel;
	}

	public void setDataKeyModel(DataKeyModel dataKeyModel)
	{
		this.dataKeyModel = dataKeyModel;
	}
	public abstract String getXMLWithoutCrossInfo();
}
