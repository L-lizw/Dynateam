/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeInfo
 * caogc 2010-9-19
 */
package dyna.common.dto.model.code;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.code.CodeObjectInfoMapper;
import dyna.common.systemenum.CodeDisplayEnum;
import dyna.common.systemenum.CodeTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author caogc
 *
 */
@Cache
@EntryMapper(CodeObjectInfoMapper.class)
public class CodeObjectInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID			= -740223844971555613L;

	public static final String	MULTI_CODE_DELIMITER		= "\r\n";

	public static final String	MULTI_CODE_DELIMITER_GUID	= ";";

	public static final String	OTHER_GUID					= "################################";

	public static final String	PREFIX_TABLENAME			= "CF_";

	public static final String	SUBFIX_TABLENAME			= "_I";

	public static final String	CODENAME					= "CODENAME";

	public static final String	TITLE						= "TITLE";

	public static final String	DESCRIPTION					= "DESCRIPTION";

	public static final String	TYPE						= "CODETYPE";

	public static final String	SHOWTYPE					= "SHOWTYPE";

	public static final String	ISCLASSIFICATION			= "ISCLASSIFICATION";

	public static final String	BASETABLENAME				= "BASETABLENAME";

	public static final String	TYPEENUM					= "TYPEENUM";

	public static final String	SHOWTYPEENUM				= "SHOWTYPEENUM";

	/**
	 * 当组码类型为普通组码时，为false，当组码类型为分类时，如果所有的分类子项都没有字段，则为false，否则为true
	 */
	private boolean				hasFields					= false;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CodeObjectInfo clone()
	{
		return (CodeObjectInfo) super.clone();
	}

	@Override
	public String getName()
	{
		return (String) this.get(CODENAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(CODENAME, name);
	}

	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	/**
	 * @return the type
	 */
	public CodeTypeEnum getType()
	{
		return CodeTypeEnum.getEnum((String) this.get(TYPE));
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(CodeTypeEnum type)
	{
		this.put(TYPE, type.getValue());
		this.put(TYPEENUM, type);
	}

	/**
	 * @return the isClassification
	 */
	public boolean isClassification()
	{
		return this.get(ISCLASSIFICATION) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISCLASSIFICATION));
	}

	/**
	 * @param isClassification
	 *            the isClassification to set
	 */
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

	/**
	 * @param showType
	 *            the showType to set
	 */
	public void setShowType(CodeDisplayEnum showType)
	{
		this.put(SHOWTYPE, showType.getValue());
		this.put(SHOWTYPEENUM, showType);
	}

	/**
	 * @return the showType
	 */
	public CodeDisplayEnum getShowType()
	{
		return CodeDisplayEnum.getEnum((String) this.get(SHOWTYPE));
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

	public String getBaseTableName()
	{
		return (String) this.get(BASETABLENAME);
	}

	public void setBaseTableName(String baseTableName)
	{
		this.put(BASETABLENAME, baseTableName);
	}

	public String getRevisionTableName()
	{
		return StringUtils.isNullString(this.getBaseTableName()) ? null : PREFIX_TABLENAME + this.getBaseTableName();
	}

	public String getIterationTableName()
	{
		return StringUtils.isNullString(this.getBaseTableName()) ? null : PREFIX_TABLENAME + this.getBaseTableName() + SUBFIX_TABLENAME;
	}

	public boolean isHasFields()
	{
		return hasFields;
	}

	public void setHasFields(boolean hasFields)
	{
		this.hasFields = hasFields;
	}
}
