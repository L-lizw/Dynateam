package dyna.common.util;

import java.util.Comparator;
import java.util.Date;

import dyna.common.bean.data.SystemObject;

public class ObjectComparator implements Comparator<SystemObject>
{
	private String	sortField	= null;
	private boolean	isASC		= true;

	public ObjectComparator(String sortField, boolean isASC)
	{
		this.sortField = StringUtils.isNullString(sortField) ? "ID" : sortField;
		this.isASC = isASC;
	}

	@Override
	public int compare(SystemObject o1, SystemObject o2)
	{
		if (o1.get(sortField) == null && o2.get(sortField) == null)
		{
			return 0;
		}

		if (isASC)
		{
			if (o1.get(sortField) instanceof Date || o2.get(sortField) instanceof Date)
			{
				if (o1.get(sortField) == null)
				{
					return 1;
				}
				else if (o2.get(sortField) == null)
				{
					return -1;
				}
				else
				{
					return ((Date) o1.get(sortField)).compareTo((Date) o2.get(sortField));
				}
			}
			else
			{
				String v1 = StringUtils.convertNULLtoString(o1.get(sortField));
				String v2 = StringUtils.convertNULLtoString(o2.get(sortField));
				return v1.compareTo(v2);
			}
		}
		else
		{
			if (o1.get(sortField) instanceof Date || o2.get(sortField) instanceof Date)
			{
				if (o1.get(sortField) == null)
				{
					return -1;
				}
				else if (o2.get(sortField) == null)
				{
					return 1;
				}
				else
				{
					return ((Date) o2.get(sortField)).compareTo((Date) o1.get(sortField));
				}
			}
			else
			{
				String v1 = StringUtils.convertNULLtoString(o1.get(sortField));
				String v2 = StringUtils.convertNULLtoString(o2.get(sortField));
				return v2.compareTo(v1);
			}
		}
	}
}
