package dyna.common.bean.data.configparamter;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.dtomapper.configparamter.TableOfParameterMapper;

import java.util.Map;

/**
 * 参数(P表)
 * 
 * @author wwx
 * 
 */
@EntryMapper(TableOfParameterMapper.class)
public class TableOfParameter extends ConfigTableBase implements SystemObject
{
	private static final long	serialVersionUID	= 198901519599475733L;
	public static final String	GNUMBER				= "GNUMBER";

	public String getGNumber()
	{
		return (String) this.get(GNUMBER);
	}

	public void setGNumber(String gNumber)
	{
		this.put(GNUMBER, gNumber);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		TableOfParameter result = new TableOfParameter();
		result.putAll((Map<String, Object>) super.clone());
		result.putOriginalValueMap((Map<String, Object>) result);
		return result;
	}
}
