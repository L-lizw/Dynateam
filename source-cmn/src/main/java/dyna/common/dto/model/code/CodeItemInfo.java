/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeItemInfo
 * caogc 2010-9-19
 */
package dyna.common.dto.model.code;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author caogc
 * 
 */
@Cache
public class CodeItemInfo extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -740223844971555613L;

	public static final String	MASTERGUID			= "MASTERGUID";

	public static final String	ITEMNAME			= "ITEMNAME";

	public static final String	TITLE				= "TITLE";

	public static final String	PARENTGUID			= "PARENTGUID";

	public static final String	VALUENUMBER			= "VALUENUMBER";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	// codeitem中一属性

	public static final String	CODE				= "CODE";

	public static final String	VALUESTRING			= "VALUESTRING";

	public static final String	TYPE				= "TYPE";

	public static final String	SHOWTYPE			= "SHOWTYPE";

	/**
	 * 缩略图
	 */

	public static final String	ICONPATH			= "ICONPATH";

	/**
	 * 符号图
	 */

	public static final String	SYMBOLPATH			= "SYMBOLPATH";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	SHOWPREVIEW			= "SHOWPREVIEW";

	public static final String	UNIQUEFIELDS		= "UNIQUEFIELDS";

	public static final String	ISCLASSIFICATION	= "ISCLASSIFICATION";

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(ITEMNAME);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(ITEMNAME, name);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	/**
	 * @return the title
	 */
	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return (String) this.get(PARENTGUID);
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.put(PARENTGUID, parentGuid);
	}

	public String getValueNumber()
	{
		return this.get(VALUENUMBER) == null ? null : this.get(VALUENUMBER).toString();
	}

	public void setValueNumber(String valueNumber)
	{
		this.put(VALUENUMBER, valueNumber == null ? null : new BigDecimal(valueNumber));
	}

	public String getValueString()
	{
		return (String) this.get(VALUESTRING);
	}

	public void setValueString(String valueString)
	{
		this.put(VALUESTRING, valueString);
	}

	/**
	 * @return the codeGuid
	 */
	public String getCodeGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	/**
	 * @param codeGuid
	 *            the codeGuid to set
	 */
	public void setCodeGuid(String codeGuid)
	{
		this.put(MASTERGUID, codeGuid);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return (String) this.get(CODE);
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code)
	{
		this.put(CODE, code);
	}

	public String getType()
	{
		return (String) this.get(TYPE);
	}

	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	public String getShowType()
	{
		return (String) this.get(SHOWTYPE);
	}

	public void setShowType(String showType)
	{
		this.put(SHOWTYPE, showType);
	}

	public String getIconPath()
	{
		return (String) this.get(ICONPATH);
	}

	public void setIconPath(String iconPath)
	{
		this.put(ICONPATH, iconPath);
	}

	public String getSymbolPath()
	{
		return (String) this.get(SYMBOLPATH);
	}

	public void setSymbolPath(String symbolPath)
	{
		this.put(SYMBOLPATH, symbolPath);
	}

	public Integer getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	public Boolean isShowPreview()
	{
		return this.get(SHOWPREVIEW) == null ? true : BooleanUtils.getBooleanByYN((String) this.get(SHOWPREVIEW));
	}

	public void setShowPreview(Boolean showPreview)
	{
		this.put(SHOWPREVIEW, BooleanUtils.getBooleanStringYN(showPreview));
	}

	public String getUniqueFields()
	{
		return (String) this.get(UNIQUEFIELDS);
	}

	public void setUniqueFields(String uniqueFields)
	{
		this.put(UNIQUEFIELDS, uniqueFields);
	}

	public boolean isClassification()
	{
		return this.get(ISCLASSIFICATION) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISCLASSIFICATION));
	}

	public void setClassification(boolean isClassification)
	{
		this.put(ISCLASSIFICATION, BooleanUtils.getBooleanStringYN(isClassification));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "{Name: " + this.getName() + ", Guid: " + this.getGuid() + "}";
	}

	@Override
	public CodeItemInfo clone()
	{
		return (CodeItemInfo) super.clone();
	}
}
