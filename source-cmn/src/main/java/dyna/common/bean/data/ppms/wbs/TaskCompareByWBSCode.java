package dyna.common.bean.data.ppms.wbs;

import java.util.Comparator;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.util.StringUtils;

public class TaskCompareByWBSCode implements Comparator<DynaObject>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(DynaObject o1, DynaObject o2)
	{
		if (o1 == null)
		{
			if (o2 == null)
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else if (o2 == null)
		{
			return 1;
		}
		else
		{
			PPMFoundationObjectUtil task1 = new PPMFoundationObjectUtil((FoundationObject) o1);
			PPMFoundationObjectUtil task2 = new PPMFoundationObjectUtil((FoundationObject) o2);
			if (StringUtils.isNullString(task1.getParentTask().getGuid()) == false
					&& task1.getParentTask().equals(task2.getParentTask()))
			{
				return task1.getSequence() - task2.getSequence();
			}
			else
			{
				int i = this.compareWBS(task1.getWBSNumber(), task2.getWBSNumber());
				return i;
			}
		}
	}

	public int compareWBS(String wbsCode1, String wbsCode2)
	{
		if (wbsCode1 == null)
		{
			if (wbsCode2 == null)
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else if (wbsCode2 == null)
		{
			return 1;
		}
		else
		{
			String[] strL1 = wbsCode1.trim().split("\\.");
			String[] strL2 = wbsCode2.trim().split("\\.");
			if (strL1.length <= strL2.length)
			{
				for (int i = 0; i < strL1.length; i++)
				{
					if (strL1[i] == null)
					{
						if (strL2[i] != null)
						{
							return -1;
						}
					}
					else if (strL2[i] == null)
					{
						return 1;
					}
					else
					{
						if (strL1[i].equals(strL2[i]) == false)
						{
							int i1 = 0;
							int i2 = 0;
							try
							{
								i1 = Integer.parseInt(strL1[i]);
								i2 = Integer.parseInt(strL2[i]);
								return (i1 - i2);
							}
							catch (Exception e)
							{
								return strL1[i].compareTo(strL2[i]);
							}
						}
					}
				}
				return strL1.length - strL2.length;
			}
			else
			{
				for (int i = 0; i < strL2.length; i++)
				{
					if (strL1[i] == null)
					{
						if (strL2[i] != null)
						{
							return -1;
						}
					}
					else if (strL2[i] == null)
					{
						return 1;
					}
					else
					{
						if (strL1[i].equals(strL2[i]) == false)
						{
							int i1 = 0;
							int i2 = 0;
							try
							{
								i1 = Integer.parseInt(strL1[i]);
								i2 = Integer.parseInt(strL2[i]);
								return (i1 - i2);
							}
							catch (Exception e)
							{
								return strL1[i].compareTo(strL2[i]);
							}
						}
					}
				}
				return 1;
			}
		}
	}
}
