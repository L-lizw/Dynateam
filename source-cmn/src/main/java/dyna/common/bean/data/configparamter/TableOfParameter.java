package dyna.common.bean.data.configparamter;

import java.util.Map;

import dyna.common.bean.data.SystemObject;

/**
 * 参数(P表)
 * 
 * @author wwx
 * 
 */
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
