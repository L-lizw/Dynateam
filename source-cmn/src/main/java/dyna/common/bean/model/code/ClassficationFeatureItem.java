package dyna.common.bean.model.code;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class ClassficationFeatureItem implements Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 1L;

	// 生成编码时使用
	private transient ThreadLocal<Boolean>	hasAllocate			= new ThreadLocal<Boolean>();
	private transient ThreadLocal<String>	itemFieldValue		= new ThreadLocal<String>();
	private transient ThreadLocal<Boolean>	needAllocate		= new ThreadLocal<Boolean>();

	private ClassficationFeatureItemInfo	info				= null;

	private List<ClassificationNumberField>	fieldList			= null;

	public ClassficationFeatureItem()
	{
		this.info = new ClassficationFeatureItemInfo();
	}

	public ClassficationFeatureItem(ClassficationFeatureItemInfo info, List<ClassificationNumberField> fieldList)
	{
		this.info = info;
		this.fieldList = fieldList;
	}

	public ClassficationFeatureItemInfo getInfo()
	{
		return this.info;
	}

	public void setInfo(ClassficationFeatureItemInfo info)
	{
		this.info = info;
	}

	public void clearAllocate()
	{
		this.hasAllocate.set(false);
		this.itemFieldValue.set(null);
		this.needAllocate.set(false);
		if (fieldList != null)
		{
			for (ClassificationNumberField item : fieldList)
			{
				item.clearAllocate();
			}
		}
	}

	public boolean isHasAllocate()
	{
		Boolean boolean1 = this.hasAllocate.get();
		return boolean1 == null ? false : boolean1;
	}

	public void setHasAllocate(boolean hasAllocate)
	{
		this.hasAllocate.set(hasAllocate);
	}

	public String getFieldValue()
	{
		return this.itemFieldValue.get();
	}

	public void setFieldValue(String fieldValue)
	{
		this.itemFieldValue.set(fieldValue);
	}

	public boolean isNeedAllocate()
	{
		Boolean boolean1 = this.needAllocate.get();
		return boolean1 == null ? false : boolean1;
	}

	public void setNeedAllocate(boolean needAllocate)
	{
		this.needAllocate.set(needAllocate);
	}

	public List<ClassificationNumberField> getFieldList()
	{
		return this.fieldList == null ? null : new ArrayList<ClassificationNumberField>(this.fieldList);
	}

	public void setFieldList(List<ClassificationNumberField> fieldList)
	{
		this.fieldList = fieldList;
	}

	/**
	 * 格式化码段,如:[型号]-[安全线]-[尺寸]
	 * 
	 * @param mappingList
	 * @return
	 */
	public static String formatSegmentStyle(List<ClassificationNumberField> fieldList)
	{
		StringBuffer sb = new StringBuffer();
		if (!SetUtils.isNullList(fieldList))
		{
			for (ClassificationNumberField mapping : fieldList)
			{
				if (mapping == null)
				{
					continue;
				}

				sb.append(StringUtils.convertNULLtoString(mapping.getPrefix()));
				sb.append("[");
				sb.append(StringUtils.convertNULLtoString(mapping.getFieldName()));
				sb.append("]");
				sb.append(StringUtils.convertNULLtoString(mapping.getSuffix()));

			}
		}

		return sb.toString();
	}

	/**
	 * 格式化显示样式,如:XXXX-XX-XX
	 * 
	 * @param mappingList
	 * @return
	 */
	public static String formatDisplayStyle(List<ClassificationNumberField> fieldList)
	{
		StringBuffer sb = new StringBuffer();
		if (!SetUtils.isNullList(fieldList))
		{
			for (ClassificationNumberField mapping : fieldList)
			{
				if (mapping == null)
				{
					continue;
				}
				sb.append(StringUtils.convertNULLtoString(mapping.getPrefix()));
				sb.append("[");
				if (CFMCodeRuleEnum.DATE.getType().equals(mapping.getType().getType()) && mapping.getFieldlength() == 0)
				{
					String value = mapping.getTypeValue();
					if (!StringUtils.isNullString(value))
					{
						sb.append(StringUtils.makeFixedLengthString('X', value.length()));
					}
				}
				else
				{
					sb.append(StringUtils.makeFixedLengthString('X', mapping.getFieldlength()));
				}
				sb.append("]");
				sb.append(StringUtils.convertNULLtoString(mapping.getSuffix()));

			}
		}

		return sb.toString();
	}

	public String getClassificationItemGuid()
	{
		return this.info.getClassificationItemGuid();
	}

	public void setClassificationItemGuid(String classificationItemGuid)
	{
		this.info.setClassificationItemGuid(classificationItemGuid);
	}

	public void setFieldName(String name)
	{
		this.info.setFieldName(name);
	}

	public String getFieldName()
	{
		return this.info.getFieldName();
	}

	public String getClassificationFeatureGuid()
	{
		return this.info.getClassificationFeatureGuid();
	}

	public void setClassificationFeatureGuid(String classclassificationGuid)
	{
		this.info.setClassificationFeatureGuid(classclassificationGuid);
	}

	public void setNumbering(boolean numbering)
	{
		this.info.setNumbering(numbering);
	}

	public boolean isNumbering()
	{
		return this.info.isNumbering();
	}

	public String getFormat()
	{
		return this.info.getFormat();
	}

	public void setFormat(String format)
	{
		this.info.setFormat(format);
	}

	public void setStyle(String style)
	{
		this.info.setStyle(style);
	}

	public String getStyle()
	{
		return this.info.getStyle();
	}

	public boolean isInherit()
	{
		return this.info.isInherit();
	}

	public void setInherit(boolean isInherit)
	{
		this.info.setInherit(isInherit);
	}

	public void setName(String name)
	{
		this.info.setName(name);
	}

	public String getName()
	{
		return this.info.getName();
	}

	public void setClassGuid(String guid)
	{
		this.info.setClassGuid(guid);
	}

	public String getClassGuid()
	{
		return this.info.getClassGuid();
	}

	public String getGuid()
	{
		return this.info.getGuid();
	}

	public void setCreateUser(String operatorGuid)
	{
		this.info.setCreateUserGuid(operatorGuid);
		refreshManagerFiel();
	}

	public void setUpdateUser(String operatorGuid)
	{
		this.info.setUpdateUserGuid(operatorGuid);
		refreshManagerFiel();
	}

	private void refreshManagerFiel()
	{
		if (this.fieldList != null)
		{
			for (ClassificationNumberField cnf : this.fieldList)
			{
				if (StringUtils.isNullString(cnf.getCreateUserGuid()))
				{
					cnf.setCreateUserGuid(this.info.getCreateUserGuid());
				}
				if (StringUtils.isNullString(cnf.getUpdateUserGuid()))
				{
					cnf.setCreateUserGuid(this.info.getUpdateUserGuid());
				}
			}
		}

	}

}
