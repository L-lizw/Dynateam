package dyna.data.service.sdm;

import java.math.BigDecimal;
import java.util.Map;

import dyna.common.bean.data.SystemObject;
import dyna.common.util.CloneUtils;
import dyna.common.bean.xml.UpperKeyMap;

public class FieldValueEqualsFilter<E extends SystemObject> implements BeanFilter<E>
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2473289712618436549L;

	private String              fieldName = "";
	private Object              value     = null;
	public Map<String, Object> filterMap = null;

	public FieldValueEqualsFilter(String fieldName, Object value)
	{
		this.fieldName = fieldName;
		this.value = value;
	}

	public FieldValueEqualsFilter(UpperKeyMap filterMap)
	{
		this.filterMap = filterMap;
	}

	@Override
	public boolean match(E o)
	{
		if (this.filterMap == null)
		{
			if (value == null)
			{
				return o.get(fieldName) == null;
			}
			else if (o != null)
			{
				if (value instanceof Number && o.get(fieldName) != null && o.get(fieldName) instanceof Number)
				{
					Object val1 = new BigDecimal(value.toString());
					Object val2 = new BigDecimal(o.get(fieldName).toString());
					return val1.equals(val2);
				}
				return value.equals(o.get(fieldName));
			}
			else
			{
				return false;
			}
		}
		else
		{
			for (String fieldName_ : this.filterMap.keySet())
			{
				boolean isMatch = false;
				Object val_ = this.filterMap.get(fieldName_);
				if (val_ == null)
				{
					isMatch = o.get(fieldName_) == null;
				}
				else
				{
					if (val_ instanceof Number && o.get(fieldName_) != null && o.get(fieldName_) instanceof Number)
					{
						Object val1 = new BigDecimal(val_.toString());
						Object val2 = new BigDecimal(o.get(fieldName_).toString());
						isMatch = val1.equals(val2);
					}
					else
					{
						isMatch = val_.equals(o.get(fieldName_));
					}
				}

				if (!isMatch)
				{
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public BeanFilter<E> clone()
	{
		return CloneUtils.clone(this);
	}

}
