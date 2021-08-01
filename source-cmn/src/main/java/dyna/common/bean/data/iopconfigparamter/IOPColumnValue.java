package dyna.common.bean.data.iopconfigparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.configparamter.IOPColumnValueMapper;
import dyna.common.util.BooleanUtils;

import java.util.Date;

@EntryMapper(IOPColumnValueMapper.class)
public class IOPColumnValue extends SystemObjectImpl implements SystemObject
{

	private static final long serialVersionUID = 3995590265843503378L;

	public static final String VALUE           = "VARVALUE";
	public static final String NAME            = "VARNAME";                // 属性值
	public static final String TITLE           = "TITLE";                // 标题
	public static final String SEQUENCE        = "DATASEQ";
	public static final String MASTERGUID      = "MASTERGUID";
	public static final String RELEASETIME     = "RELEASETIME";
	public static final String OBSOLETETIME    = "OBSOLETETIME";
	public static final String HASNEXTREVISION = "HASNEXTREVISION";

	public String getColumnName()
	{
		return (String) this.get(NAME);
	}

	public void setColumnName(String name)
	{
		this.put(NAME, name);
	}

	public String getColumnValue()
	{
		return (String) this.get(VALUE);
	}

	public void setColumnValue(String value)
	{
		this.put(VALUE, value);
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

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
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

	public String getColumnTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setColumnTitle(String title)
	{
		this.put(TITLE, title);
	}
}
