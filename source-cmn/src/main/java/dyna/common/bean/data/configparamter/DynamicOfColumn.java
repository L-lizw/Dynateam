package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.cpb.DynamicOfColumnMapper;

import java.util.Map;

/**
 * 动态列
 * 
 * @author wwx
 * 
 */
@EntryMapper(DynamicOfColumnMapper.class)
public class DynamicOfColumn extends ConfigBase implements SystemObject
{
	private static final long	serialVersionUID	= -8380474666256640100L;
	// 固定表的Guid
	public static final String	MASTERFK			= "MASTERFK";
	// 列名（取columntitle的UNIQUEVALUE）
	public static final String	TITLEGUID			= "TITLEGUID";
	// 值
	public static final String	VALUE				= "ITEMVALUE";

	public String getMasterFK()
	{
		return (String) this.get(MASTERFK);
	}

	public void setMasterFK(String masterfk)
	{
		this.put(MASTERFK, masterfk);
	}

	public String getValue()
	{
		return (String) this.get(VALUE);
	}

	public void setValue(String value)
	{
		this.put(VALUE, value);
	}

	public String getTitleGuid()
	{
		return (String) this.get(TITLEGUID);
	}

	public void setTitleGuid(String titleGuid)
	{
		this.put(TITLEGUID, titleGuid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		DynamicOfColumn result = new DynamicOfColumn();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}
}
