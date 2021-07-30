package dyna.common.bean.data.configparamter;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

public class ConfigBase extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6650610345610027117L;

	/**
	 * 数据表是否被锁定
	 */
	public static final String	TABLEISLOCK			= "TABLEISLOCK";
	/**
	 * 锁定时间
	 */
	public static final String	TABLELOCKTIME		= "TABLELOCKTIME";

	public static final String	MASTERGUID			= "MASTERGUID";
	public static final String	SEQUENCE			= "DATASEQ";
	public static final String	RELEASETIME			= "RELEASETIME";
	public static final String	OBSOLETETIME		= "OBSOLETETIME";
	public static final String	HASNEXTREVISION		= "HASNEXTREVISION";

	// 当固定列的值发生变更，需要新增，但是动态列没有改变时，动态列的数据可以复用，所以动态列和固定列之间不能通过固定列的行guid来关联，因此引入该字段
	// 当固定列的值发生改变，需要新增时，新增的数据UNIQUEVALUE不变，以达到复用动态列数据的目的
	public static final String	UNIQUEVALUE			= "UNIQUEVALUE";

	// 单个拆分最大字节数
	public static final int		MAX_BYTE			= 4000;
	// 字符编码
	public static final String	CHARSET				= "UTF-8";

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, sequence);
	}

	public Date getReleaseTime()
	{
		return (Date) this.get(RELEASETIME);
	}

	public void setReleaseTime(Date date)
	{
		this.put(RELEASETIME, date);
	}

	public Date getObsoleteTime()
	{
		return (Date) this.get(OBSOLETETIME);
	}

	public void setObsoleteTime(Date obsoletTime)
	{
		this.put(OBSOLETETIME, obsoletTime);
	}

	public boolean isHasNextRevision()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(HASNEXTREVISION));
		return ret == null ? false : ret.booleanValue();
	}

	public void setHasNextRevision(boolean isHasNext)
	{
		this.put(HASNEXTREVISION, BooleanUtils.getBooleanStringYN(isHasNext));
	}

	public void setUniqueValue(String uniqueValue)
	{
		this.put(UNIQUEVALUE, uniqueValue);
	}

	public String getUniqueValue()
	{
		return (String) this.get(UNIQUEVALUE);
	}

	/**
	 * 合并拆分字段(fieldName = fieldName1 + fieldName2 + ...)
	 * 
	 * @param obj
	 * @param fieldName
	 * @param max
	 */
	public static <T extends SystemObject> void composeToOneField(T obj, String fieldName, Integer max)
	{
		StringBuilder finalValue = new StringBuilder();
		for (int i = 1; i <= max; i++)
		{
			String value = (String) obj.get(fieldName + i);
			if (!StringUtils.isNullString(value))
			{
				finalValue.append(value);
			}
		}
		obj.put(fieldName, finalValue.toString());
	}

	/**
	 * 拆分字段(fieldName -> fieldName1 + fieldName2 + ...)
	 * 
	 * @param obj
	 * @param fieldName
	 * @param max
	 * @throws ServiceRequestException
	 */
	public static <T extends SystemObject> void assignmentToOtherField(T obj, String fieldName, Integer max) throws ServiceRequestException
	{
		int i = 1;
		String lastStr = StringUtils.convertNULLtoString(obj.get(fieldName));
		try
		{
			while (i <= max)
			{
				String subStr = null;
				if (!StringUtils.isNullString(lastStr))
				{
					if (lastStr.getBytes(CHARSET).length <= MAX_BYTE)
					{
						subStr = lastStr;
						lastStr = null;
					}
					else
					{
						int start = 1334;
						int end = lastStr.length() > MAX_BYTE ? MAX_BYTE : lastStr.length();
						if (lastStr.substring(0, start).getBytes(CHARSET).length > MAX_BYTE)
						{
							subStr = lastStr.substring(0, start - 1);
							lastStr = lastStr.substring(start - 1);
						}
						else
						{
							int current = 0;
							while (true)
							{
								current = start + (end - start) / 2;
								if (start == current)
								{
									if (lastStr.substring(0, end).getBytes(CHARSET).length <= MAX_BYTE)
										current = end;
									break;
								}

								if (lastStr.substring(0, current).getBytes(CHARSET).length > MAX_BYTE)
									end = current;
								else
									start = current;
							}
							subStr = lastStr.substring(0, current);
							lastStr = lastStr.substring(current);
						}
					}
				}
				obj.put(fieldName + i, subStr);
				i++;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServiceRequestException("ID_APP_UECNECOSTUB_STRING_EXCHANGEERROR", "exchangeError", null, lastStr);
		}
		if (!StringUtils.isNullString(lastStr))
		{
			throw new ServiceRequestException("ID_DS_DATA_TOO_LONG", "value is too long, can't save");
		}
	}

}
