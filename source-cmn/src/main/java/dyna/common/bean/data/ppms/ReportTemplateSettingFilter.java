package dyna.common.bean.data.ppms;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.FieldTypeEnum;

/**
 * 报表模板过滤属性
 * 
 * @author duanll
 * 
 */
public class ReportTemplateSettingFilter extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7162528267282073358L;

	//
	public static final String	MASTERGUID			= "MASTERGUID";

	// 属性名称
	public static final String	test_KEY		= "KEY";

	// 属性值
	public static final String	test_VAL		= "VAL";

	// 属性类型
	public static final String	test_TYPE		= "TYPE";

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

	public String gettestKey()
	{
		return (String) this.get(test_KEY);
	}

	public void settestKey(String testKey)
	{
		this.put(test_KEY, testKey);
	}

	public Object gettestVal()
	{
		return this.get(test_VAL);
	}

	public void settestVal(Object testVal)
	{
		this.put(test_VAL, testVal);
	}

	public String gettestType()
	{
		return this.get(test_TYPE) == null ? FieldTypeEnum.STRING.toString() : (String) this.get(test_TYPE);
	}

	public void settestType(String testType)
	{
		this.put(test_TYPE, testType);
	}
}
