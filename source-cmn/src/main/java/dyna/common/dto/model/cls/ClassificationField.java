/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassificationField
 * duanll 2014-1-14
 */
package dyna.common.dto.model.cls;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.cls.ClassificationFieldMapper;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.util.BooleanUtils;
import org.simpleframework.xml.Attribute;

/**
 * @author duanll
 */
@Cache
@EntryMapper(ClassificationFieldMapper.class)
public class ClassificationField extends SystemObjectImpl implements SystemObject
{

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static final String	FIELDNAME			= "FIELDNAME";

	public static final String	CLASSIFICATIONFK	= "CLASSIFICATIONFK";

	public static final String	COLUMNNAME			= "COLUMNNAME";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	TITLE				= "TITLE";

	public static final String	TYPE				= "FIELDTYPE";

	public static final String	TYPEVALUE			= "TYPEVALUE";

	public static final String	VALIDITYREGEX		= "VALIDITYREGEX";

	public static final String	FIELDSIZE			= "FIELDSIZE";

	public static final String	DEFAULTVALUE		= "FIELDDEFAULT";

	public static final String	MANDATORY			= "MANDATORY";

	public static final String	INHERITED			= "INHERITED";

	public static final String	PUBLICFIELDINERP	= "PUBLICFIELDINERP";

	@Attribute(name = "name", required = false)
	public String getFieldName()
	{
		return (String) this.get(FIELDNAME);
	}

	@Attribute(name = "name", required = false)
	public void setFieldName(String fieldName)
	{
		this.put(FIELDNAME, fieldName);
	}

	@Override
	public String getName()
	{
		return this.getFieldName();
	}

	@Override
	public void setName(String name)
	{
		this.setFieldName(name);
	}

	@Attribute(name = "description", required = false)
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	@Attribute(name = "description", required = false)
	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	@Attribute(name = "title", required = false)
	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	@Attribute(name = "title", required = false)
	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	@Attribute(name = "type", required = false)
	public FieldTypeEnum getType()
	{
		return super.get(TYPE) == null ? FieldTypeEnum.STRING : FieldTypeEnum.typeof((String) super.get(TYPE));
	}

	@Attribute(name = "type", required = false)
	public void setType(FieldTypeEnum type)
	{
		this.put(TYPE, type.toString());
	}

	@Attribute(name = "typevalue", required = false)
	public String getTypeValue()
	{
		return (String) this.get(TYPEVALUE);
	}

	@Attribute(name = "typevalue", required = false)
	public void setTypeValue(String typeValue)
	{
		this.put(TYPEVALUE, typeValue);
	}

	@Attribute(name = "defaultvalue", required = false)
	public String getDefaultValue()
	{
		return (String) this.get(DEFAULTVALUE);
	}

	@Attribute(name = "defaultvalue", required = false)
	public void setDefaultValue(String defaultValue)
	{
		this.put(DEFAULTVALUE, defaultValue);
	}

	@Attribute(name = "publicfieldinerp", required = false)
	public String getPublicFieldInERP()
	{
		return (String) this.get(PUBLICFIELDINERP);
	}

	@Attribute(name = "publicfieldinerp", required = false)
	public void setPublicFieldInERP(String publicFieldInERP)
	{
		this.put(PUBLICFIELDINERP, publicFieldInERP);
	}

	public boolean isInherited()
	{
		return this.get(INHERITED) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(INHERITED));
	}

	public void setInherited(boolean inherited)
	{
		this.put(INHERITED, BooleanUtils.getBooleanStringYN(inherited));
	}

	@Attribute(name = "mandatory", required = false)
	public boolean isMandatory()
	{
		return this.get(MANDATORY) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(MANDATORY));
	}

	@Attribute(name = "mandatory", required = false)
	public void setMandatory(boolean mandatory)
	{
		this.put(MANDATORY, BooleanUtils.getBooleanStringYN(mandatory));
	}

	@Attribute(name = "size", required = false)
	public String getFieldSize()
	{
		return (String) this.get(FIELDSIZE);
	}

	@Attribute(name = "size", required = false)
	public void setFieldSize(String fieldSize)
	{
		this.put(FIELDSIZE, fieldSize);
	}

	@Attribute(name = "validity", required = false)
	public String getValidityRegex()
	{
		return (String) this.get(VALIDITYREGEX);
	}

	@Attribute(name = "validity", required = false)
	public void setValidityRegex(String validityRegex)
	{
		this.put(VALIDITYREGEX, validityRegex);
	}

	public String getColumnName()
	{
		return (String) this.get(COLUMNNAME);
	}

	public void setColumnName(String columnName)
	{
		this.put(COLUMNNAME, columnName);
	}

	public String getClassificationGuid()
	{
		return (String) this.get(CLASSIFICATIONFK);
	}

	public void setClassificationGuid(String classificationGuid)
	{
		this.put(CLASSIFICATIONFK, classificationGuid);
	}
}
