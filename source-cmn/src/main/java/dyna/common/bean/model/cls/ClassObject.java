/**
 * Copyright(C) DCIS 版权所有。
 * 功能描述：data common object definitions
 * 创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.bean.model.cls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Complete;
import org.simpleframework.xml.core.Persist;

import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.bean.xml.ClassObjectXML;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

@Root(name = "object-models")
public class ClassObject implements Cloneable, Serializable
{
	private static final long				serialVersionUID	= 3783060613022909967L;

	private final ClassInfo					info;

	private List<ClassField>				fieldList			= null;

	private List<Integer>					tableIndexList		= null;

	private List<NumberingModel>			numberingModelList	= null;

	private List<ClassficationFeatureItem>	featureItemList		= null;

	private List<ClassObject>				childList			= null;

	private List<UIObject>					uiObjectList		= null;

	private List<ClassAction>				actionList			= null;

	/**
	 * 必填字段列表
	 */
	private List<ClassField>				mandatoryFieldList	= null;

	@ElementList(name = "class-model", entry = "class-object", required = false)
	private List<ClassObjectXML>			classObjectXML		= null;

	@SuppressWarnings("unused")
	@Persist
	private void prepare()
	{
		ClassObjectXML classObjectXML = new ClassObjectXML();
		classObjectXML.setFieldList(this.getFieldList());
		classObjectXML.setInfo(this.getInfo());

		this.classObjectXML = new ArrayList<ClassObjectXML>();
		this.classObjectXML.add(classObjectXML);
	}

	@SuppressWarnings("unused")
	@Complete
	private void release()
	{
		this.classObjectXML = null;
	}

	public ClassObject()
	{
		this.info = new ClassInfo();
	}

	public ClassObject(ClassInfo info)
	{
		if (info == null)
		{
			this.info = new ClassInfo();
		}
		else
		{
			this.info = info;
		}
	}

	/**
	 * @return the info
	 */
	public ClassInfo getInfo()
	{
		return this.info;
	}

	/**
	 * @param child
	 *            the child to add
	 */
	public void addChild(ClassObject child)
	{
		if (this.childList == null)
		{
			this.childList = new ArrayList<ClassObject>();
		}

		this.childList.add(child);
	}

	/**
	 * @param field
	 *            the field to add
	 */
	public void addField(ClassField field)
	{
		if (this.fieldList == null)
		{
			this.fieldList = new ArrayList<ClassField>();
		}

		if (!this.fieldList.contains(field) && this.getField(field.getName()) == null)
		{
			this.fieldList.add(field);

			if (field.isMandatory())
			{
				this.addMandatoryField(field);
			}
		}
	}

	/**
	 * @param field
	 *            the field to add
	 */
	public void removeField(ClassField field)
	{
		if (this.fieldList != null)
		{
			this.fieldList.remove(field);
		}

		if (this.mandatoryFieldList != null)
		{
			this.mandatoryFieldList.remove(field);
		}
	}

	public List<Integer> getTableIndexList()
	{
		return tableIndexList;
	}

	public void setTableIndexList(List<Integer> tableIndexList)
	{
		this.tableIndexList = tableIndexList;
	}

	/**
	 * @param interface
	 *            the interface to add
	 */
	public void addInterface(String interfaceName)
	{
		this.info.addInterface(interfaceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ClassObject clone()
	{
		try
		{
			return (ClassObject) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

	/**
	 * @return the baseClass
	 */
	public String getBaseClass()
	{
		return this.info.getBaseClass();
	}

	/**
	 * @return the childList
	 */
	public List<ClassObject> getChildList()
	{
		return this.childList;
	}

	/**
	 * @param fieldName
	 * @return field
	 */
	public ClassField getField(String fieldName)
	{
		if (this.fieldList == null)
		{
			return null;
		}

		for (ClassField field : this.fieldList)
		{
			if (fieldName.equalsIgnoreCase(field.getName()))
			{
				return field;
			}
		}

		return null;
	}

	/**
	 * @return the fieldList
	 */
	public List<ClassField> getFieldList()
	{
		if (this.fieldList != null)
		{
			return Collections.unmodifiableList(this.fieldList);
		}
		else
		{
			return null;
		}
	}

	public List<ClassField> getFieldList(boolean isNotContainSystemField)
	{
		List<ClassField> allFieldList = this.getFieldList();
		if (!isNotContainSystemField)
		{
			return allFieldList;
		}

		List<ClassField> list = new ArrayList<ClassField>();
		for (ClassField classField : allFieldList)
		{
			if (!classField.isSystem())
			{
				list.add(classField);
			}
		}

		return Collections.unmodifiableList(list);
	}

	/**
	 * @return the icon
	 */
	public String getIcon()
	{
		return this.info.getIcon();
	}

	/**
	 * @return the icon32
	 */
	public String getIcon32()
	{
		return this.info.getIcon32();
	}

	/**
	 * @return the basetableName
	 */
	public String getBasetableName()
	{
		return this.info.getBasetableName();
	}

	/**
	 * @param basetableName
	 *            the basetableName to set
	 */
	public void setBasetableName(String basetableName)
	{
		this.info.setBasetableName(basetableName);
	}

	/**
	 * @return the instanceString
	 */
	public String getInstanceString()
	{
		return this.info.getInstanceString();
	}

	/**
	 * @return the interfaceList
	 */
	public List<ModelInterfaceEnum> getInterfaceList()
	{
		return this.info.getInterfaceList();
	}

	/**
	 * @return the lifecycle
	 */
	public String getLifecycle()
	{
		return this.info.getLifecycle();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.info.getName();
	}

	public String getSuperInterface()
	{
		return this.info.getSuperInterface();
	}

	public void setSuperInterface(String superInterface)
	{
		this.info.setSuperInterface(superInterface);
	}

	public void setNoQueryable(boolean isNoQueryable)
	{
		this.info.setNoQueryable(isNoQueryable);
	}

	public boolean isNoQueryable()
	{
		return this.info.isNoQueryable();
	}

	/**
	 * @return the superclass
	 */
	public String getSuperclass()
	{
		return this.info.getSuperclass();
	}

	public String getSuperClassGuid()
	{
		return this.info.getSuperClassGuid();
	}

	public void setSuperClassGuid(String superClassGuid)
	{
		this.info.setSuperClassGuid(superClassGuid);
	}

	public boolean hasInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.info.hasInterface(interfaceEnum);
	}

	/**
	 * @return the isInstantiable
	 */
	public boolean isAbstract()
	{
		return this.info.isAbstract();
	}

	public boolean isCreateTable()
	{
		return this.info.isCreateTable();
	}

	public void setCreateTable(boolean isCreateTable)
	{
		this.info.setCreateTable(isCreateTable);
	}

	public boolean isIdUnique()
	{
		return this.info.isIdUnique();
	}

	public void setIdUnique(boolean isIdUnique)
	{
		this.info.setIdUnique(isIdUnique);
	}

	public boolean isCheckUnique()
	{
		return this.info.isCheckUnique();
	}

	public void setCheckUnique(boolean isCheckUnique)
	{
		this.info.setCheckUnique(isCheckUnique);
	}

	/**
	 * @return the isBuiltin
	 */
	public boolean isBuiltin()
	{
		return this.info.isBuiltin();
	}

	/**
	 * @return the isFinal
	 */
	public boolean isFinal()
	{
		return this.info.isFinal();
	}

	/**
	 * @param isAbstract
	 *            the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract)
	{
		this.info.setAbstract(isAbstract);
	}

	/**
	 * @param baseClass
	 *            the baseClass to set
	 */
	public void setBaseClass(String baseClass)
	{
		this.info.setBaseClass(baseClass);
	}

	/**
	 * @param isBuiltin
	 *            the isBuiltin to set
	 */
	public void setBuiltin(boolean isBuiltin)
	{
		this.info.setBuiltin(isBuiltin);
	}

	/**
	 * @param fieldList
	 *            the fieldList to set
	 */
	public void setFieldList(List<ClassField> fieldList)
	{
		this.fieldList = fieldList;
	}

	/**
	 * @param isFinal
	 *            the isFinal to set
	 */
	public void setFinal(boolean isFinal)
	{
		this.info.setFinal(isFinal);
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon)
	{
		this.info.setIcon(icon);
	}

	/**
	 * @param icon32
	 *            the icon32 to set
	 */
	public void setIcon32(String icon32)
	{
		this.info.setIcon32(icon32);
	}

	/**
	 * @param showPreview
	 *            the icon to set
	 */
	public boolean isShowPreview()
	{
		return this.info.isShowPreview();
	}

	/**
	 * @param showPreview
	 *            the icon to set
	 */
	public void setShowPreview(boolean showPreview)
	{
		this.info.setShowPreview(showPreview);
	}

	/**
	 * @param instanceString
	 *            the instanceString to set
	 */
	public void setInstanceString(String instanceString)
	{
		this.info.setInstanceString(instanceString);
	}

	/**
	 * @param interfaceList
	 *            the interfaceList to set
	 */
	public void setInterfaceList(List<ModelInterfaceEnum> interfaceList)
	{
		this.info.setInterfaceList(interfaceList);
	}

	/**
	 * @param lifecycle
	 *            the lifecycle to set
	 */
	public void setLifecycle(String lifecycle)
	{
		this.info.setLifecycle(lifecycle);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.info.setName(name);
	}

	/**
	 * @param superclass
	 *            the superclass to set
	 */
	public void setSuperclass(String superclass)
	{
		this.info.setSuperclass(superclass);
	}

	public String getClassification()
	{
		return this.info.getClassification();
	}

	/**
	 * @param classificationGroup
	 *            the classificationGroup to set
	 */
	public void setClassification(String classificationGroup)
	{
		this.info.setClassification(classificationGroup);
	}

	public void setDescription(String description)
	{
		this.info.setDescription(description);
	}

	public String getDescription()
	{
		return this.info.getDescription();
	}

	// to xml string
	public String toXMLString()
	{
		return null;
	}

	/**
	 * 添加一个必填字段
	 * 
	 * @param classField
	 */
	public void addMandatoryField(ClassField classField)
	{
		if (this.mandatoryFieldList == null)
		{
			this.mandatoryFieldList = new ArrayList<ClassField>();
		}
		this.mandatoryFieldList.add(classField);
	}

	public List<UIObject> getUiObjectList()
	{
		return uiObjectList;
	}

	public void setUiObjectList(List<UIObject> uiObjectList)
	{
		this.uiObjectList = uiObjectList;
	}

	public void addUIObject(UIObject uiObject)
	{
		if (this.uiObjectList == null)
		{
			this.uiObjectList = new ArrayList<UIObject>();
		}
		this.uiObjectList.add(uiObject);
	}

	public UIObject getUIObject(String uiObjectName)
	{
		if (this.uiObjectList != null)
		{
			for (UIObject uiObject : this.uiObjectList)
			{
				if (uiObjectName.equals(uiObject.getName()))
				{
					return uiObject;
				}
			}
		}
		return null;
	}

	public List<ClassAction> getClassActionList()
	{
		return actionList;
	}

	public void setClassActionList(List<ClassAction> actionList)
	{
		this.actionList = actionList;
	}

	public void addClassAction(ClassAction action)
	{
		if (this.actionList == null)
		{
			this.actionList = new ArrayList<ClassAction>();
		}
		this.actionList.add(action);
	}

	/**
	 * 得到必填字段列表
	 * 
	 * @return the mandatoryFieldList
	 */
	public List<ClassField> getMandatoryFieldList()
	{
		return this.mandatoryFieldList;
	}

	public void setIterationLimit(String iterationLimit)
	{
		this.info.setIterationLimit(iterationLimit);
	}

	public String getIterationLimit()
	{
		return this.info.getIterationLimit();
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	public void setInterfaces(String interfaces)
	{
		this.info.setInterfaces(interfaces);
	}

	/**
	 * @return the interfaces
	 */
	public String getInterfaces()
	{
		return this.info.getInterfaces();
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid)
	{
		this.info.setGuid(guid);
	}

	public String getGuid()
	{
		return this.info.getGuid();
	}

	/**
	 * @param numberingModelList
	 *            the numberingModelList to set
	 */
	public void setNumberingModelList(List<NumberingModel> numberingModelList)
	{
		this.numberingModelList = numberingModelList;
	}

	/**
	 * @return the numberingModelList
	 */
	public List<NumberingModel> getNumberingModelList()
	{
		if (this.numberingModelList == null)
		{
			this.numberingModelList = new ArrayList<NumberingModel>();
		}
		return this.numberingModelList;
	}

	public NumberingModel getNumberingModel(String fieldName)
	{
		if (StringUtils.isNullString(fieldName))
		{
			return null;
		}
		if (this.numberingModelList != null)
		{
			for (NumberingModel numberingModel : this.numberingModelList)
			{
				if (fieldName.equalsIgnoreCase(numberingModel.getName()))
				{
					return numberingModel;
				}
			}
		}
		return null;
	}

	public void makeFeatureItem()
	{
		this.featureItemList = new ArrayList<ClassficationFeatureItem>();
		ClassficationFeatureItem item = null;
		if (!SetUtils.isNullList(this.numberingModelList))
		{
			for (NumberingModel model : this.numberingModelList)
			{
				if (model.isNumbering())
				{
					continue;
				}

				item = new ClassficationFeatureItem();
				item.setNumbering(model.isNumbering());
				item.setName(model.getName());
				item.setFieldName(model.getName());
				item.setClassGuid(this.info.getGuid());
				this.featureItemList.add(item);

				if (!SetUtils.isNullList(model.getNumberingObjectList()))
				{
					List<ClassificationNumberField> fieldList = new ArrayList<ClassificationNumberField>();
					item.setFieldList(fieldList);
					ClassificationNumberField field = null;
					for (NumberingObject numbering : model.getNumberingObjectList())
					{
						field = new ClassificationNumberField();
						field.setName(numbering.getName());
						if (StringUtils.isNullString(numbering.getFieldLength()))
						{
							field.put(ClassificationNumberField.FIELDLENGTH, null);
						}
						else
						{
							field.setFieldlength(Integer.valueOf(numbering.getFieldLength()));
						}

						field.setPrefix(numbering.getPrefix());
						field.setSuffix(numbering.getSuffix());
						field.setControlledNumberFieldGuid(numbering.getSerialField());
						field.setGuid(numbering.getGuid());
						field.setType(CFMCodeRuleEnum.typeValueOf(numbering.getFieldType()));
						field.setTypeValue(numbering.getTypevalue());

						if (numbering.getFieldTypeEnum() == CFMCodeRuleEnum.FIELD)
						{
							field.setFormClass(true);
							field.setFieldName(field.getName());
						}
						else
						{
							field.setFormClass(false);
						}
						fieldList.add(field);
					}
				}
			}
		}

	}

	public List<ClassficationFeatureItem> getFeatureItemList()
	{
		return this.featureItemList;
	}

	public void setFeatureItemList(List<ClassficationFeatureItem> featureItemList)
	{
		this.featureItemList = featureItemList;
	}
}
