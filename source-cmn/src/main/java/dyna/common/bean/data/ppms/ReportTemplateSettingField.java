package dyna.common.bean.data.ppms;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

/**
 * 报表模板自定义显示字段
 * 
 * @author duanll
 * 
 */
public class ReportTemplateSettingField extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7461337550636810809L;

	public static final String	MASTERGUID			= "MASTERGUID";

	public static final String	FIELDNAME			= "FIELDNAME";

	public static final String	ISSHOW				= "ISSHOW";

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

	public String getFieldName()
	{
		return (String) this.get(FIELDNAME);
	}

	public void setFieldName(String fieldName)
	{
		this.put(FIELDNAME, fieldName);
	}

	public boolean isShow()
	{
		return this.get(FIELDNAME) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISSHOW));
	}

	public void setShow(boolean isShow)
	{
		this.put(ISSHOW, BooleanUtils.getBooleanStringYN(isShow));
	}
}
