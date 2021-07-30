package dyna.data.service.sdm;

import java.io.Serializable;

import dyna.common.bean.data.SystemObject;

public interface BeanFilter<E extends SystemObject> extends Serializable, Cloneable
{
	boolean match(E o);

	BeanFilter<E> clone();
}
