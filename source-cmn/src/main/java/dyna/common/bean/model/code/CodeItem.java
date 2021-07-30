/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeItemInfo
 * caogc 2010-9-19
 */
package dyna.common.bean.model.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.model.ui.ClassificationUIObject;
import dyna.common.bean.model.ui.UIIcon;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.ClassificationUIField;
import dyna.common.dto.model.ui.UIField;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * @author caogc
 */
public class CodeItem extends SystemObjectImpl implements SystemObject
{

	private static final long				serialVersionUID			= -740223844971555613L;

	private final CodeItemInfo				info;

	private List<CodeItem>					codeDetailList				= null;

	private List<ClassificationField>		classificationFieldList		= null;

	private List<ClassificationUIObject>	classificationUIObjectList	= null;

	private UIIcon							uiIcon						= null;

	private CodeItem						parent						= null;

	private String							masterName					= null;

	private String							parentName					= null;

	public CodeItem()
	{
		this.info = new CodeItemInfo();
	}

	public CodeItem(CodeItemInfo info)
	{
		this.info = info;
	}

	public CodeItemInfo getInfo()
	{
		return info;
	}

	@Override
	public String getGuid()
	{
		return this.info.getGuid();
	}

	@Override
	public void setGuid(String guid)
	{
		this.info.setGuid(guid);
	}

	public String getMasterGuid()
	{
		return this.info.getMasterGuid();
	}

	public void setMasterGuid(String masterGuid)
	{
		this.info.setMasterGuid(masterGuid);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.info.getName();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	public String getTitle()
	{
		return this.info.getTitle();
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.info.setTitle(title);
	}

	/**
	 * @return the title
	 */
	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	/**
	 * @return the parentGuid
	 */
	public String getParentGuid()
	{
		return this.info.getParentGuid();
	}

	/**
	 * @param parentGuid
	 *            the parentGuid to set
	 */
	public void setParentGuid(String parentGuid)
	{
		this.info.setParentGuid(parentGuid);
	}

	public String getValueNumber()
	{
		return this.info.getValueNumber();
	}

	public void setValueNumber(String valueNumber)
	{
		this.info.setValueNumber(valueNumber);
	}

	public String getValueString()
	{
		return this.info.getValueString();
	}

	public void setValueString(String valueString)
	{
		this.info.setValueString(valueString);
	}

	/**
	 * @return the codeGuid
	 */
	public String getCodeGuid()
	{
		return this.info.getCodeGuid();
	}

	/**
	 * @param codeGuid
	 *            the codeGuid to set
	 */
	public void setCodeGuid(String codeGuid)
	{
		this.info.setCodeGuid(codeGuid);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.info.setDescription(description);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.info.getDescription();
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return this.info.getCode();
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code)
	{
		this.info.setCode(code);
	}

	public String getType()
	{
		return this.info.getType();
	}

	public void setType(String type)
	{
		this.info.setType(type);
	}

	public String getShowType()
	{
		return this.info.getShowType();
	}

	public void setShowType(String showType)
	{
		this.info.setShowType(showType);
	}

	public String getIconPath()
	{
		return this.info.getIconPath();
	}

	public void setIconPath(String iconPath)
	{
		this.info.setIconPath(iconPath);
	}

	public String getSymbolPath()
	{
		return this.info.getSymbolPath();
	}

	public void setSymbolPath(String symbolPath)
	{
		this.info.setSymbolPath(symbolPath);
	}

	public int getSequence()
	{
		return this.info.getSequence();
	}

	public void setSequence(int sequence)
	{
		this.info.setSequence(sequence);
	}

	public Boolean isShowPreview()
	{
		return this.info.isShowPreview();
	}

	public void setShowPreview(Boolean showPreview)
	{
		this.info.setShowPreview(showPreview);
	}

	public String getUniqueFields()
	{
		return this.info.getUniqueFields();
	}

	public void setUniqueFields(String uniqueFields)
	{
		this.info.setUniqueFields(uniqueFields);
	}

	public boolean isClassification()
	{
		return this.info.isClassification();
	}

	public void setClassification(boolean isClassification)
	{
		this.info.setClassification(isClassification);
	}

	public void addField(ClassificationField field)
	{
		if (this.classificationFieldList == null)
		{
			this.classificationFieldList = new ArrayList<ClassificationField>();
		}
		this.classificationFieldList.add(field);
	}

	public void removeField(ClassificationField field)
	{
		if (this.classificationFieldList != null)
		{
			for (ClassificationField field_ : this.classificationFieldList)
			{
				if (field_.getFieldName().equals(field.getFieldName()))
				{
					this.classificationFieldList.remove(field_);
					break;
				}
			}
		}
	}

	public List<ClassField> getFieldList()
	{
		if (!SetUtils.isNullList(this.getClassificationFieldList()))
		{
			List<ClassField> fieldList = new ArrayList<ClassField>();
			for (ClassificationField clfField : this.getClassificationFieldList())
			{
				ClassField field = new ClassField();
				field.putAll(clfField);
				field.setCoderefField(field.getCoderefField());
				fieldList.add(field);
			}
			return fieldList;
		}
		return null;
	}

	public void setClassificationFieldList(List<ClassificationField> fieldList)
	{
		this.classificationFieldList = fieldList;
	}

	public List<ClassificationField> getClassificationFieldList()
	{
		return this.classificationFieldList;
	}

	/**
	 * 根据字段名取得当前分类对象的字段对象
	 *
	 * @param fieldName
	 * @return field
	 */
	public ClassField getField(String fieldName)
	{
		if (this.classificationFieldList == null)
		{
			return null;
		}

		for (ClassificationField field : this.classificationFieldList)
		{
			if (fieldName.equalsIgnoreCase(field.getName()))
			{
				ClassField classField = new ClassField();
				classField.putAll(field);
				return classField;
			}
		}

		return null;
	}

	public ClassificationField getClassificationField(String fieldName)
	{
		if (this.classificationFieldList == null)
		{
			return null;
		}

		for (ClassificationField field : this.classificationFieldList)
		{
			if (fieldName.equalsIgnoreCase(field.getName()))
			{
				return field;
			}
		}

		return null;
	}

	public ClassificationUIObject getClassificationUIObject(UITypeEnum UIType, boolean onlyVisible)
	{
		for (ClassificationUIObject uiObject : this.getClassificationUIObjectList())
		{
			if (onlyVisible && !uiObject.isVisible())
			{
				continue;
			}

			if (UIType.toString().equals(uiObject.getType()))
			{
				return uiObject;
			}
		}

		return null;
	}

	/**
	 * @param objectName
	 * @return UIObject
	 */
	public ClassificationUIObject getClassificationUIObject(String objectName)
	{
		if (this.getClassificationUIObjectList() == null)
		{
			return null;
		}

		for (ClassificationUIObject uiObject : this.getClassificationUIObjectList())
		{
			if (objectName.equalsIgnoreCase(uiObject.getUIName()))
			{
				return uiObject;
			}
		}

		return null;
	}

	public void addUIObject(ClassificationUIObject uiObject)
	{
		if (this.getClassificationUIObjectList() == null)
		{
			this.setClassificationUIObjectList(new ArrayList<ClassificationUIObject>());
		}

		this.getClassificationUIObjectList().add(uiObject);
		Collections.sort(this.getClassificationUIObjectList(), new CFUIObjectSeqCompare());
	}

	public void removeUIObject(ClassificationUIObject uiObject)
	{
		if (this.classificationUIObjectList != null)
		{
			Iterator<ClassificationUIObject> it = this.classificationUIObjectList.iterator();
			while (it.hasNext())
			{
				if (it.next().getInfo().getGuid().equals(uiObject.getInfo().getGuid()))
				{
					it.remove();
					break;
				}
			}
		}
	}

	public List<UIObject> getUiObjectList()
	{
		if (this.getClassificationUIObjectList() == null)
		{
			return null;
		}
		List<UIObject> uiObjectList = new ArrayList<UIObject>();
		for (ClassificationUIObject classificationUIObject : this.getClassificationUIObjectList())
		{
			UIObject uiObject = new UIObject();
			uiObject.putAll(classificationUIObject);

			if (classificationUIObject.getUiFieldList() != null)
			{
				for (ClassificationUIField classificationUIField : classificationUIObject.getUiFieldList())
				{
					UIField uiField = new UIField();
					uiField.putAll(classificationUIField);
					uiObject.addField(uiField);
				}
			}

			uiObjectList.add(uiObject);
		}
		return uiObjectList;
	}

	public UIObject getUiObject(String uiGuid)
	{
		if (this.getClassificationUIObjectList() == null)
		{
			return null;
		}
		for (ClassificationUIObject classificationUIObject : this.getClassificationUIObjectList())
		{
			if (StringUtils.isGuid(uiGuid) && uiGuid.equals(classificationUIObject.getInfo().getGuid()))
			{
				UIObject uiObject = new UIObject();
				uiObject.putAll(classificationUIObject);

				if (classificationUIObject.getUiFieldList() != null)
				{
					for (ClassificationUIField classificationUIField : classificationUIObject.getUiFieldList())
					{
						UIField uiField = new UIField();
						uiField.putAll(classificationUIField);
						uiField.setUIGuid(classificationUIField.getUIGuid());
						uiField.setFieldGuid(classificationUIField.getFieldGuid());
						if (!uiField.isSeparator())
						{
							ClassField field = this.getField(uiField.getName());
							if (field != null)
							{
								uiField.setType(field.getType());
								uiObject.addField(uiField);
							}
						}
						else
						{
							uiObject.addField(uiField);
						}
					}
				}
				return uiObject;
			}
		}
		return null;
	}

	public UIIcon getUiIcon()
	{
		return uiIcon;
	}

	public void setUiIcon(UIIcon uiIcon)
	{
		this.uiIcon = uiIcon;
	}

	public List<ClassificationUIObject> getClassificationUIObjectList()
	{
		return classificationUIObjectList;
	}

	public void setClassificationUIObjectList(List<ClassificationUIObject> classificationUIObjectList)
	{
		this.classificationUIObjectList = classificationUIObjectList;
		if (classificationUIObjectList != null)
		{
			Collections.sort(classificationUIObjectList, new CFUIObjectSeqCompare());
		}
	}

	public void setCodeDetailList(List<CodeItem> codeDetailList)
	{
		this.codeDetailList = codeDetailList;
		if (codeDetailList != null)
		{
			Collections.sort(codeDetailList, new CodeItemSeqCompare());
		}
	}

	public List<CodeItem> getCodeDetailList()
	{
		if (this.codeDetailList == null)
		{
			this.codeDetailList = new ArrayList<CodeItem>();
		}
		return this.codeDetailList;
	}

	public CodeItem getParent()
	{
		return parent;
	}

	public void setParent(CodeItem parent)
	{
		this.parent = parent;
	}

	public String getParentName()
	{
		return this.parentName;
	}

	public String getMasterName()
	{
		return masterName;
	}

	public void setMasterName(String masterName)
	{
		this.masterName = masterName;
	}

	/**
	 * 在分类对象中添加子分类对象
	 *
	 * @param child
	 */
	public void addChild(CodeItem child)
	{
		if (this.codeDetailList == null)
		{
			this.codeDetailList = new ArrayList<CodeItem>();
		}
		this.codeDetailList.add(child);
		Collections.sort(codeDetailList, new CodeItemSeqCompare());
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
	public CodeItem clone()
	{
		return (CodeItem) super.clone();
	}

	public void setParentName(String parentName)
	{
		this.parentName = parentName;
	}
}
