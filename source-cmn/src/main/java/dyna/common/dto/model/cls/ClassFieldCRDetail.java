package dyna.common.dto.model.cls;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

@Cache
public class ClassFieldCRDetail extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -385575520245702860L;

	public static final String	FIELDFK				= "FIELDFK";

	public static final String	CODE				= "CODE";

	public static final String	VALUE				= "CRVALUE";

	public String getFieldFK()
	{
		return (String) this.get(FIELDFK);
	}

	public void setFieldFK(String fieldfk)
	{
		this.put(FIELDFK, fieldfk);
	}

	public String getCode()
	{
		return (String) this.get(CODE);
	}

	public void setCode(String code)
	{
		this.put(CODE, code);
	}

	public String getValue()
	{
		return (String) this.get(VALUE);
	}

	public void setValue(String value)
	{
		this.put(VALUE, value);
	}
}