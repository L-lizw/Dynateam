/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.bean.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.systemenum.ModelInterfaceEnum;

@Root(name = "class-object")
public class ClassObjectXML implements Cloneable, Serializable
{
	private static final long			serialVersionUID	= 3783060613022909967L;

	private ClassInfo					info				= new ClassInfo();

	@ElementList(name = "fields", entry = "field", required = false)
	private List<ClassField>			fieldList			= null;

	private List<ClassObjectXML>	childList			= null;

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
	public void addChild(ClassObjectXML child)
	{
		if (this.childList == null)
		{
			this.childList = new ArrayList<ClassObjectXML>();
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
		}
	}

	/**
	 * @param interface
	 *            the interface to add
	 */
	public void addInterface(String interfaceName)
	{
		this.getInfo().addInterface(interfaceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ClassObjectXML clone()
	{
		try
		{
			return (ClassObjectXML) super.clone();
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
		return this.getInfo().getBaseClass();
	}

	/**
	 * @return the childList
	 */
	public List<ClassObjectXML> getChildList()
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

	/**
	 * @return the icon
	 */
	public String getIcon()
	{
		return this.getInfo().getIcon();
	}

	/**
	 * @return the icon32
	 */
	public String getIcon32()
	{
		return this.getInfo().getIcon32();
	}

	/**
	 * @return the instanceString
	 */
	@Attribute(name = "instancestring", required = false)
	public String getInstanceString()
	{
		return this.getInfo().getInstanceString();
	}

	/**
	 * @return the interfaceList
	 */
	public List<ModelInterfaceEnum> getInterfaceList()
	{
		return this.getInfo().getInterfaceList();
	}

	/**
	 * @return the lifecycle
	 */
	public String getLifecycle()
	{
		return this.getInfo().getLifecycle();
	}
	
	/**
	 * @return the lifecycle
	 */
	@Attribute(name = "lifecycle", required = false)
	public String getLifecycleName()
	{
		return this.getInfo().getLifecycleName();
	}
	
	@Attribute(name = "lifecycle", required = false)
	public void setLifecycleName(String lifecycleName)
	{
		this.getInfo().setLifecycleName(lifecycleName);
	}
	
	/**
	 * @return the name
	 */
	@Attribute(name = "name", required = false)
	public String getName()
	{
		return this.getInfo().getName();
	}

	/**
	 * @return the superclass
	 */
	@Attribute(name = "superclass", required = false)
	public String getSuperclass()
	{
		return this.getInfo().getSuperclass();
	}

	public boolean hasInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getInfo().hasInterface(interfaceEnum);
	}

	/**
	 * @return the isInstantiable
	 */
	@Attribute(name = "abstract", required = false)
	public boolean isAbstract()
	{
		return this.getInfo().isAbstract();
	}

	/**
	 * @return the isBuiltin
	 */
	public boolean isBuiltin()
	{
		return this.getInfo().isBuiltin();
	}

	/**
	 * @return the isFinal
	 */
	@Attribute(name = "final", required = false)
	public boolean isFinal()
	{
		return this.getInfo().isFinal();
	}

	/**
	 * @param isAbstract
	 *            the isAbstract to set
	 */
	@Attribute(name = "abstract", required = false)
	public void setAbstract(boolean isAbstract)
	{
		this.getInfo().setAbstract(isAbstract);
	}

	/**
	 * @param isCreateTable
	 *            the isCreateTable to set
	 */
	@Attribute(name = "createtable", required = false)
	public void setCreateTable(boolean isCreateTable)
	{
		this.getInfo().setCreateTable(isCreateTable);
	}

	@Attribute(name = "createtable", required = false)
	public boolean isCreateTable()
	{
		return this.getInfo().isCreateTable();
	}

	/**
	 * @param isCreateTable
	 *            the isCreateTable to set
	 */
	@Attribute(name = "idunique", required = false)
	public void setIdUnique(boolean isIdUnique)
	{
		this.getInfo().setIdUnique(isIdUnique);
	}

	@Attribute(name = "idunique", required = false)
	public boolean isIdUnique()
	{
		return this.getInfo().isIdUnique();
	}

	/**
	 * @param baseClass
	 *            the baseClass to set
	 */
	public void setBaseClass(String baseClass)
	{
		this.getInfo().setBaseClass(baseClass);
	}

	/**
	 * @param isBuiltin
	 *            the isBuiltin to set
	 */
	public void setBuiltin(boolean isBuiltin)
	{
		this.getInfo().setBuiltin(isBuiltin);
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
	@Attribute(name = "final", required = false)
	public void setFinal(boolean isFinal)
	{
		this.getInfo().setFinal(isFinal);
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon)
	{
		this.getInfo().setIcon(icon);
	}

	/**
	 * @param icon32
	 *            the icon32 to set
	 */
	public void setIcon32(String icon32)
	{
		this.getInfo().setIcon32(icon32);
	}

	/**
	 * @param instanceString
	 *            the instanceString to set
	 */
	@Attribute(name = "instancestring", required = false)
	public void setInstanceString(String instanceString)
	{
		this.getInfo().setInstanceString(instanceString);
	}

	/**
	 * @param interfaceList
	 *            the interfaceList to set
	 */
	public void setInterfaceList(List<ModelInterfaceEnum> interfaceList)
	{
		this.getInfo().setInterfaceList(interfaceList);
	}

	/**
	 * @param lifecycle
	 *            the lifecycle to set
	 */
	public void setLifecycle(String lifecycle)
	{
		this.getInfo().setLifecycle(lifecycle);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Attribute(name = "name", required = false)
	public void setName(String name)
	{
		this.getInfo().setName(name);
	}

	/**
	 * @param superclass
	 *            the superclass to set
	 */
	@Attribute(name = "superclass", required = false)
	public void setSuperclass(String superclass)
	{
		this.getInfo().setSuperclass(superclass);
	}

	@Attribute(name = "classification", required = false)
	public String getClassification()
	{
		return this.getInfo().getClassification();
	}

	/**
	 * @param classificationGroup
	 *            the classificationGroup to set
	 */
	@Attribute(name = "classification", required = false)
	public void setClassification(String classificationGroup)
	{
		this.getInfo().setClassification(classificationGroup);
	}

	// public boolean isPersistent()
	// {
	// return this.info.isPersistent();
	// }
	//
	// public void setPersistent(boolean isPersistent)
	// {
	// this.info.setPersistent(isPersistent);
	// }

	@Attribute(name = "description", required = false)
	public void setDescription(String description)
	{
		this.getInfo().setDescription(description);
	}

	@Attribute(name = "description", required = false)
	public String getDescription()
	{
		return this.getInfo().getDescription();
	}

	// to xml string
	public String toXMLString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Attribute(name = "iterationlimit", required = false)
	public void setIterationLimit(String iterationLimit)
	{
		this.getInfo().setIterationLimit(iterationLimit);
	}

	@Attribute(name = "iterationlimit", required = false)
	public String getIterationLimit()
	{
		return this.getInfo().getIterationLimit();
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	@Attribute(name = "interface", required = false)
	public void setInterfaces(String interfaces)
	{
		this.getInfo().setInterfaces(interfaces);
	}

	/**
	 * @return the interfaces
	 */
	@Attribute(name = "interface", required = false)
	public String getInterfaces()
	{
		return this.getInfo().getInterfaces();
	}

	/**
	 * @param system
	 *            the system to set
	 */
	@Attribute(name = "system", required = false)
	public void setSystem(boolean system)
	{
		this.getInfo().setSystem(system);
	}

	/**
	 * @return the system
	 */
	@Attribute(name = "system", required = false)
	public boolean isSystem()
	{
		return this.getInfo().isSystem();
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(ClassInfo info)
	{
		this.info = info;
	}
}
