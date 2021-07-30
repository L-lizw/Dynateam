/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.dto.model.cls;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;

@Cache
public class NumberingModelInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 5660807352929113750L;

	public static final String	CLASSFK				= "CLASSFK";

	public static final String	MANDATORY			= "MANDATORY";

	public static final String	WITHFIELD			= "WITHFIELD";

	public static final String	WITHDATE			= "WITHDATE";

	public static final String	UESEXTENDNUMBER		= "UESEXTENDNUMBER";

	public static final String	FIELDNAME			= "FIELDNAME";

	public static final String	FIELDLENGTH			= "FIELDLENGTH";

	public static final String	ISNUMBERING			= "ISNUMBERING";

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public NumberingModelInfo clone()
	{
		return (NumberingModelInfo) super.clone();
	}

	public boolean isMandatory()
	{
		if ((String) this.get(MANDATORY) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(MANDATORY));
	}

	public void setMandatory(boolean isMandatory)
	{
		this.put(MANDATORY, BooleanUtils.getBooleanStringYN(isMandatory));
	}

	public String getClassGuid()
	{
		return (String) this.get(CLASSFK);
	}

	public void setClassGuid(String classGuid)
	{
		this.put(CLASSFK, classGuid);
	}

	/**
	 * @param isSerialNumberWithField
	 *            the isSerialNumberWithField to set
	 */
	public void setSerialNumberWithField(boolean isSerialNumberWithField)
	{
		this.put(WITHFIELD, BooleanUtils.getBooleanStringYN(isSerialNumberWithField));
	}

	/**
	 * @return the isSerialNumberWithField
	 */
	public boolean isSerialNumberWithField()
	{
		if ((String) this.get(WITHFIELD) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(WITHFIELD));
	}

	/**
	 * @param isSerialNumberWithDate
	 *            the isSerialNumberWithDate to set
	 */
	public void setSerialNumberWithDate(boolean isSerialNumberWithDate)
	{
		this.put(WITHDATE, BooleanUtils.getBooleanStringYN(isSerialNumberWithDate));
	}

	/**
	 * @return the isSerialNumberWithDate
	 */
	public boolean isSerialNumberWithDate()
	{
		if ((String) this.get(WITHDATE) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(WITHDATE));
	}

	/**
	 * @param extendedserialnumber
	 *            the extendedserialnumber to set
	 */
	public void setExtendedserialnumber(boolean extendedserialnumber)
	{
		this.put(UESEXTENDNUMBER, BooleanUtils.getBooleanStringYN(extendedserialnumber));
	}

	/**
	 * @return the extendedserialnumber
	 */
	public boolean isExtendedserialnumber()
	{
		if ((String) this.get(UESEXTENDNUMBER) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(UESEXTENDNUMBER));
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.put(FIELDNAME, name);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		if (super.get(FIELDNAME) == null)
		{
			this.setName(SystemClassFieldEnum.ID.getName());
		}
		return (String) super.get(FIELDNAME);
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size)
	{
		this.put(FIELDLENGTH, size);
	}

	/**
	 * @return the size
	 */
	public String getSize()
	{
		return (String) super.get(FIELDLENGTH);
	}

	public boolean isNumbering()
	{
		if ((String) this.get(ISNUMBERING) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(ISNUMBERING));
	}

	public void setNumbering(boolean isNumbering)
	{
		this.put(ISNUMBERING, BooleanUtils.getBooleanStringYN(isNumbering));
	}
}
