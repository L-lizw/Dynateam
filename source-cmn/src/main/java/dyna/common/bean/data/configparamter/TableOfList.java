package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.configparamter.TableOfListMapper;
import dyna.common.systemenum.ConfigParameterTableType;

import java.util.Map;

/**
 * 列表(La和Lb)
 * 
 * @author wwx
 * 
 */
@EntryMapper(TableOfListMapper.class)
public class TableOfList extends ConfigTableBase implements SystemObject
{
	private static final long	serialVersionUID	= -8178312263272838430L;

	public static final String	GROUPNAME			= "GROUPNAME";

	public static final String	LNUMBER				= "LNUMBER";

	public static final String	SN					= "SN";

	public static final String	TABLETYPE			= "TABLETYPE";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public String getGroupName()
	{
		return (String) this.get(GROUPNAME);
	}

	public void setGroupName(String groupName)
	{
		this.put(GROUPNAME, groupName);
	}

	public String getLNumber()
	{
		return (String) this.get(LNUMBER);
	}

	public void setLNumber(String lNumber)
	{
		this.put(LNUMBER, lNumber);
	}

	public String getSN()
	{
		return (String) this.get(SN);
	}

	public void setSN(String sn)
	{
		this.put(SN, sn);
	}

	@Override
	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	@Override
	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, sequence);
	}

	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public ConfigParameterTableType getTableType()
	{
		return ConfigParameterTableType.valueOf((String) this.get(TABLETYPE));
	}

	public void setTableType(ConfigParameterTableType tableTypeEnum)
	{
		this.put(TABLETYPE, tableTypeEnum == null ? null : tableTypeEnum.name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfList result = new TableOfList();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap(result);
		return result;
	}
}
