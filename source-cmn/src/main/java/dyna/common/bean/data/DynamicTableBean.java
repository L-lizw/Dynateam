package dyna.common.bean.data;

public class DynamicTableBean<T extends SystemObject>
{
	private boolean		isCache		= false;

	private Class<T>	beanClass	= null;

	public boolean isCache()
	{
		return isCache;
	}

	public void setCache(boolean isCache)
	{
		this.isCache = isCache;
	}

	public Class<T> getBeanClass()
	{
		return beanClass;
	}

	public void setBeanClass(Class<T> beanClass)
	{
		this.beanClass = beanClass;
	}
}
