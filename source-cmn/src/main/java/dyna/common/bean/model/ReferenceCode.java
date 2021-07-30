/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RefCode
 * WangLHB Jun 26, 2011
 */
package dyna.common.bean.model;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
public class ReferenceCode implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -1532954215626144265L;

	@ElementList(entry = "reffield", required = false, inline = true)
	private List<ReferenceField>	refFieldList		= null;

	@Attribute(name = "name", required = false)
	private String					codeName			= null;

	private int						hashCode			= 0;

	/**
	 * @param refFieldList
	 *            the refFieldList to set
	 */
	public void setRefFieldList(List<ReferenceField> refFieldList)
	{
		this.refFieldList = refFieldList;
	}

	/**
	 * @return the refFieldList
	 */
	public List<ReferenceField> getRefFieldList()
	{
		return this.refFieldList;
	}

	/**
	 * @param hashCode
	 *            the hashCode to set
	 */
	public void setHashCode(int hashCode)
	{
		this.hashCode = hashCode;
	}

	/**
	 * @return the hashCode
	 */
	public int getHashCode()
	{
		return this.hashCode;
	}

	/**
	 * @param codeName
	 *            the codeName to set
	 */
	public void setCodeName(String codeName)
	{
		this.codeName = codeName;
	}

	/**
	 * @return the codeName
	 */
	public String getCodeName()
	{
		return this.codeName;
	}
	
	/**
	 * 根据字段的名字及相关字段的值的codeItem的GUID获取对应的Code
	 * 
	 * @param refFieldValueList
	 *            该字段相关的字段 对应的值的codeItem的Guid,如果为该字段对应的值空的话 要传"",并且按照顺序传值
	 * 
	 * @return CodeName
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public String getRefCodeByField(List<String> refFieldValueList)
	{
		boolean isMatch = true;
		List<ReferenceField> referenceFieldList = getRefFieldList();
		if (!SetUtils.isNullList(referenceFieldList))
		{
			if (refFieldValueList.size() == referenceFieldList.size())
			{
				for (int i = 0; i < referenceFieldList.size(); i++)
				{
					if (referenceFieldList.get(i) == null)
					{
						continue;
					}

					String codeItemGuid = refFieldValueList.get(i);
					ReferenceField referenceField = referenceFieldList.get(i);

					if (codeItemGuid == null || !codeItemGuid.equals(StringUtils.convertNULLtoString(referenceField.getCodeItemGuid())))
					{
						isMatch = false;
						break;
					}
				}
			}
			else
			{
				isMatch = false;
			}
			if (isMatch)
			{
				return getCodeName();
			}
		}
		return null;
	}

}
