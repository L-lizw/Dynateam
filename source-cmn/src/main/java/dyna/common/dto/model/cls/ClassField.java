/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.dto.model.cls;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.model.ReferenceCode;
import dyna.common.dtomapper.model.cls.ClassFieldMapper;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Cache
@EntryMapper(ClassFieldMapper.class)
public class ClassField extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -9065074751977527996L;

	/**
	 * 数据库字符串默认长度
	 */
	public static final int		defaultCharLength	= 128;

	public static final String	CLASSGUID			= "CLASSGUID";

	public static final String	FIELDNAME			= "FIELDNAME";

	public static final String	COLUMNNAME			= "COLUMNNAME";

	public static final String	ISBUILTIN			= "ISBUILTIN";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	TYPEVALUE			= "TYPEVALUE";

	public static final String	VALIDITYREGEX		= "VALIDITYREGEX";

	public static final String	FIELDSIZE			= "FIELDSIZE";

	public static final String	FIELDDEFAULT		= "FIELDDEFAULT";

	public static final String	MANDATORY			= "MANDATORY";

	public static final String	TYPE				= "FIELDTYPE";

	public static final String	INHERITED			= "INHERITED";

	public static final String	CODEREFFIELD		= "CODEREFFIELD";

	public static final String	ROLLBACK			= "ISROLLBACK";

	public static final String	TITLE				= "TITLE";

	public static final String	ISSYSTEM			= "ISSYSTEM";

	public static final String	TABLEINDEX			= "TABLEINDEX";

	public static final String	PUBLICFIELDINERP	= "PUBLICFIELDINERP";

	public static final String	VALUESCOPE			= "VALUESCOPE";

	private List<String>		relationFieldList	= null;

	private List<ReferenceCode>	referenceCodeList	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ClassField clone()
	{
		return (ClassField) super.clone();
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.getFieldName();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.setFieldName(name);
	}

	/**
	 * language
	 * 
	 * @return the title
	 */
	public String getTitle(LanguageEnum language)
	{
		String[] titles = StringUtils.splitString(this.getTitle());
		if (titles == null)
		{
			return null;
		}

		if (language.getType() < titles.length)
		{
			return titles[language.getType()];
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return the publicFieldInERP
	 */
	@Attribute(name = "publicfieldinerp", required = false)
	public String getPublicFieldInERP()
	{
		return (String) this.get(PUBLICFIELDINERP);
	}

	/**
	 * @param publicFieldInERP
	 *            the publicFieldInERP to set
	 */
	@Attribute(name = "publicfieldinerp", required = false)
	public void setPublicFieldInERP(String publicFieldInERP)
	{
		this.put(PUBLICFIELDINERP, publicFieldInERP);
	}

	public String getClassGuid()
	{
		return (String) super.get(CLASSGUID);
	}

	public void setClassGuid(String classGuid)
	{
		this.put(CLASSGUID, classGuid);
	}

	@Attribute(name = "name", required = false)
	public String getFieldName()
	{
		return (String) super.get(FIELDNAME);
	}

	@Attribute(name = "name", required = false)
	public void setFieldName(String fieldName)
	{
		this.put(FIELDNAME, fieldName);
	}

	public String getColumnName()
	{
		return (String) super.get(COLUMNNAME);
	}

	public void setColumnName(String columnName)
	{
		this.put(COLUMNNAME, columnName);
	}

	public boolean isBuiltin()
	{
		if ((String) super.get(ISBUILTIN) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) super.get(ISBUILTIN));
	}

	public void setBuiltin(boolean isBuiltin)
	{
		this.put(ISBUILTIN, BooleanUtils.getBooleanStringYN(isBuiltin));
	}

	@Attribute(name = "description", required = false)
	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	@Attribute(name = "description", required = false)
	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	@Attribute(name = "typevalue", required = false)
	public String getTypeValue()
	{
		return (String) super.get(TYPEVALUE);
	}

	@Attribute(name = "typevalue", required = false)
	public void setTypeValue(String typeValue)
	{
		this.put(TYPEVALUE, typeValue);
	}

	@Attribute(name = "validity", required = false)
	public String getValidityRegex()
	{
		return (String) super.get(VALIDITYREGEX);
	}

	@Attribute(name = "validity", required = false)
	public void setValidityRegex(String validityRegex)
	{
		this.put(VALIDITYREGEX, validityRegex);
	}

	@Attribute(name = "size", required = false)
	public String getFieldSize()
	{
		return (String) super.get(FIELDSIZE);
	}

	@Attribute(name = "size", required = false)
	public void setFieldSize(String fieldSize)
	{
		this.put(FIELDSIZE, fieldSize);
	}

	@Attribute(name = "defaultvalue", required = false)
	public String getDefaultValue()
	{
		return (String) super.get(FIELDDEFAULT);
	}

	@Attribute(name = "defaultvalue", required = false)
	public void setDefaultValue(String fieldDefault)
	{
		this.put(FIELDDEFAULT, fieldDefault);
	}

	@Attribute(name = "mandatory", required = false)
	public boolean isMandatory()
	{
		if ((String) super.get(MANDATORY) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) super.get(MANDATORY));
	}

	@Attribute(name = "mandatory", required = false)
	public void setMandatory(boolean isMandatory)
	{
		this.put(MANDATORY, BooleanUtils.getBooleanStringYN(isMandatory));
	}

	public boolean isInherited()
	{
		if ((String) super.get(INHERITED) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) super.get(INHERITED));
	}

	public void setInherited(boolean inherited)
	{
		this.put(INHERITED, BooleanUtils.getBooleanStringYN(inherited));
	}

	@Attribute(name = "system", required = false)
	public boolean isSystem()
	{
		if ((String) super.get(ISSYSTEM) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) super.get(ISSYSTEM));
	}

	@Attribute(name = "system", required = false)
	public void setSystem(boolean isSystem)
	{
		this.put(ISSYSTEM, BooleanUtils.getBooleanStringYN(isSystem));
	}

	@Attribute(name = "rollback", required = false)
	public boolean isRollback()
	{
		if ((String) super.get(ROLLBACK) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) super.get(ROLLBACK));
	}

	@Attribute(name = "rollback", required = false)
	public void setRollback(boolean isRollback)
	{
		this.put(ROLLBACK, BooleanUtils.getBooleanStringYN(isRollback));
	}

	@Attribute(name = "title", required = false)
	public String getTitle()
	{
		return (String) super.get(TITLE);
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

	@Attribute(name = "relationfield", required = false)
	public String getCoderefField()
	{
		return (String) super.get(CODEREFFIELD);
	}

	@Attribute(name = "relationfield", required = false)
	public void setCoderefField(String coderefField)
	{
		this.put(CODEREFFIELD, coderefField);
		if (!StringUtils.isNullString(coderefField))
		{
			String[] relationfields = StringUtils.splitString(coderefField);
			this.setRelationFieldList(new ArrayList<String>((Arrays.asList(relationfields))));
		}
	}

	public String getTableIndex()
	{
		return (String) super.get(TABLEINDEX);
	}

	public void setTableIndex(String tableIndex)
	{
		this.put(TABLEINDEX, tableIndex);
	}

	public String getValueScope()
	{
		return (String) super.get(VALUESCOPE);
	}

	public void setValueScope(String valueScope)
	{
		this.put(VALUESCOPE, valueScope);
	}

	public List<String> getRelationFieldList()
	{
		return relationFieldList;
	}

	public void setRelationFieldList(List<String> relationFieldList)
	{
		this.relationFieldList = relationFieldList;
	}

	@ElementList(entry = "refcode", required = false, inline = true)
	public List<ReferenceCode> getReferenceCodeList()
	{
		return referenceCodeList;
	}

	@ElementList(entry = "refcode", required = false, inline = true)
	public void setReferenceCodeList(List<ReferenceCode> referenceCodeList)
	{
		this.referenceCodeList = referenceCodeList;
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
		if (!SetUtils.isNullList(referenceCodeList))
		{
			for (ReferenceCode referenceCode : referenceCodeList)
			{
				String returnValue = referenceCode.getRefCodeByField(refFieldValueList);
				if (!StringUtils.isNullString(returnValue))
				{
					return returnValue;
				}
			}
		}
		return null;

	}
}
