package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.configparamter.TableOfMarkMapper;

import java.util.Map;

/**
 * 标记表(Mak表)
 * 
 * @author wwx
 * 
 */
@EntryMapper(TableOfMarkMapper.class)
public class TableOfMark extends ConfigTableBase implements SystemObject
{
	private static final long	serialVersionUID	= -6499805383983920552L;
	public static final String	MAK					= "MAK";
	public static final String	VALUE				= "PARAMVALUE";

	public String getMak()
	{
		return (String) this.get(MAK);
	}

	public void setMak(String mak)
	{
		this.put(MAK, mak);
	}

	public String getValue()
	{
		return (String) this.get(VALUE);
	}

	public void setValue(String value)
	{
		this.put(VALUE, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfMark result = new TableOfMark();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}
}
