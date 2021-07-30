package dyna.common.bean.data.ppms.instancedomain;

import java.util.List;

import dyna.common.bean.data.DynaObject;

public class InstanceDomainHandel<T extends InstanceDomainBean>
{
	private T	bean	= null;

	public InstanceDomainHandel(T bean)
	{
		this.bean=bean;
	}
	
	/**
	 * @return the bean
	 */
	public T getDomainBean()
	{
		return bean;
	}

	protected void addToDomain(String key, DynaObject object)
	{
		this.getDomainBean().addToDomain(key, object);
	}

	protected void deleteFromDomain(String key, List<? extends DynaObject> list)
	{
		this.getDomainBean().deleteFromDomain(key, list);
	}

	protected void deleteFromDomain(String key, DynaObject object)
	{
		this.getDomainBean().deleteFromDomain(key, object);
	}

	protected void addToDomain(String key, List<? extends DynaObject> list)
	{
		this.getDomainBean().addToDomain(key, list);
	}

	protected void updateSequenceToDomain(String key, List<? extends DynaObject> list)
	{
		this.getDomainBean().updateSequenceToDomain(key, list);
	}
	
}
