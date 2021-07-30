/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.bean.model.cls;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.cls.NumberingModelInfo;
import dyna.common.util.CloneUtils;

public class NumberingModel extends SystemObjectImpl implements SystemObject
{
	private static final long			serialVersionUID	= 5660807352929113750L;

	public static String				ID_NUMBERING_SIZE	= "0";

	private final NumberingModelInfo	modelInfo;

	private List<NumberingObject>		numberingObjectList	= null;

	private boolean						isInherited			= false;

	public NumberingModel()
	{
		this.modelInfo = new NumberingModelInfo();
	}

	public NumberingModel(NumberingModelInfo modelInfo)
	{
		this.modelInfo = modelInfo;
	}

	public NumberingModelInfo getModelInfo()
	{
		return modelInfo;
	}

	@Override
	public String getGuid()
	{
		// TODO Auto-generated method stub
		return this.modelInfo.getGuid();
	}

	/**
	 * @return the NumberingObjectList
	 */
	public List<NumberingObject> getNumberingObjectList()
	{
		return this.numberingObjectList;
	}

	/**
	 * @param NumberingObjectList
	 *            the NumberingObjectList to set
	 */
	public void setNumberingObjectList(List<NumberingObject> numberingObjectList)
	{
		this.numberingObjectList = numberingObjectList;
	}

	/**
	 * @param numberingObject
	 *            the numberingObject to add
	 */
	public void addNumberingObject(NumberingObject numberingObject)
	{
		if (this.numberingObjectList == null)
		{
			this.numberingObjectList = new ArrayList<NumberingObject>();
		}
		if (numberingObject == null)
		{
			return;
		}
		this.numberingObjectList.add(numberingObject);
	}

	/**
	 * @return the isInherited
	 */
	public boolean isInherited()
	{
		return this.isInherited;
	}

	/**
	 * @param isInherited
	 *            the isInherited to set
	 */
	public void setInherited(boolean isInherited)
	{
		this.isInherited = isInherited;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public NumberingModel clone()
	{
		return CloneUtils.clone(this);
	}

	public boolean isMandatory()
	{
		return this.modelInfo.isMandatory();
	}

	public void setMandatory(boolean isMandatory)
	{
		this.modelInfo.setMandatory(isMandatory);
	}

	/**
	 * @param isSerialNumberWithField
	 *            the isSerialNumberWithField to set
	 */
	public void setSerialNumberWithField(boolean isSerialNumberWithField)
	{
		this.modelInfo.setSerialNumberWithField(isSerialNumberWithField);
	}

	/**
	 * @return the isSerialNumberWithField
	 */
	public boolean isSerialNumberWithField()
	{
		return this.modelInfo.isSerialNumberWithField();
	}

	/**
	 * @param isSerialNumberWithDate
	 *            the isSerialNumberWithDate to set
	 */
	public void setSerialNumberWithDate(boolean isSerialNumberWithDate)
	{
		this.modelInfo.setSerialNumberWithDate(isSerialNumberWithDate);
	}

	/**
	 * @return the isSerialNumberWithDate
	 */
	public boolean isSerialNumberWithDate()
	{
		return this.modelInfo.isSerialNumberWithDate();
	}

	/**
	 * @param extendedserialnumber
	 *            the extendedserialnumber to set
	 */
	public void setExtendedserialnumber(boolean extendedserialnumber)
	{
		this.modelInfo.setExtendedserialnumber(extendedserialnumber);
	}

	/**
	 * @return the extendedserialnumber
	 */
	public boolean isExtendedserialnumber()
	{
		return this.modelInfo.isExtendedserialnumber();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.modelInfo.setName(name);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.modelInfo.getName();
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size)
	{
		this.modelInfo.setSize(size);
	}

	/**
	 * @return the size
	 */
	public String getSize()
	{
		return this.modelInfo.getSize();
	}

	public boolean isNumbering()
	{
		return this.modelInfo.isNumbering();
	}

	public void setNumbering(boolean isNumbering)
	{
		this.modelInfo.setNumbering(isNumbering);
	}

	public String getClassGuid()
	{
		return this.modelInfo.getClassGuid();
	}

	public void setClassGuid(String classGuid)
	{
		this.modelInfo.setClassGuid(classGuid);
	}
}
