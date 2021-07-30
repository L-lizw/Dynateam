/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.dto.model.cls;

import java.util.ArrayList;
import java.util.List;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

@Cache
public class ClassInfo extends SystemObjectImpl implements SystemObject
{
	private static final long			serialVersionUID	= 3783060613022909967L;

	public static final String			GUID				= "GUID";

	public static final String			NAME				= "CLASSNAME";

	public static final String			SUPERCLASSGUID		= "SUPERCLASSGUID";

	public static final String			BASECLASS			= "BASECLASS";

	public static final String			BASETABLENAME		= "BASETABLENAME";

	public static final String			NOQUERYABLE			= "NONQUERYABLE";

	public static final String			ISABSTRACT			= "ABSTRACT";

	public static final String			ISFINAL				= "FINAL";

	public static final String			ICON				= "ICONPATH";

	public static final String			ICON32				= "ICONPATH32";

	public static final String			INSTANCESTRING		= "INSTANCESTRING";

	public static final String			LIFECYCLE			= "LIFECYCLE";

	public static final String			ISBUILTIN			= "ISBUILTIN";

	public static final String			CLASSIFICATIONGROUP	= "CLASSIFICATION";

	public static final String			DESCRIPTION			= "DESCRIPTION";

	public static final String			ITERATIONLIMIT		= "ITERATIONLIMIT";

	public static final String			INTERFACES			= "INTERFACES";

	public static final String			SUPERINTERFACE		= "SUPERINTERFACE";

	public static final String			SYSTEM				= "SYSTEM";

	public static final String			SHOWPREVIEW			= "SHOWPREVIEW";

	public static final String			ISCREATETABLE		= "ISCREATETABLE";

	// 不需要保存在数据库中
	public static final String			ISCHECKUNIQUE		= "ISCHECKUNIQUE";

	public static final String			ISIDUNIQUE			= "ISIDUNIQUE";

	public static final String			REALBASETABLENAME	= "REALBASETABLENAME";

	public static final String			SUPERCLASS			= "SUPERCLASS";

	private List<ModelInterfaceEnum>	interfaceList		= new ArrayList<ModelInterfaceEnum>();

	/**
	 * @return the guid
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	/**
	 * @return the name
	 */
	public String getFullName()
	{
		if (StringUtils.isNullString(this.getDescription()))
		{
			return this.getName();
		}
		return this.getName() + "[" + this.getDescription() + "]";
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	/**
	 * @return the superclass
	 */
	public String getSuperClassGuid()
	{
		return (String) this.get(SUPERCLASSGUID);
	}

	/**
	 * @param superclass
	 *            the superclass to set
	 */
	public void setSuperClassGuid(String superClassGuid)
	{
		this.put(SUPERCLASSGUID, superClassGuid);
	}

	/**
	 * @return the superclass
	 */
	public String getSuperclass()
	{
		return (String) this.get(SUPERCLASS);
	}

	/**
	 * @param superclass
	 *            the superclass to set
	 */
	public void setSuperclass(String superclass)
	{
		this.put(SUPERCLASS, superclass);
	}

	/**
	 * @return the baseClass
	 */
	public String getBaseClass()
	{
		return (String) this.get(BASECLASS);
	}

	/**
	 * @param baseClass
	 *            the baseClass to set
	 */
	public void setBaseClass(String baseClass)
	{
		this.put(BASECLASS, baseClass);
	}

	public String getBasetableName()
	{
		return (String) this.get(BASETABLENAME);
	}

	public void setBasetableName(String basetableName)
	{
		this.put(BASETABLENAME, basetableName);
	}

	/**
	 * @return the isAbstract
	 */
	public boolean isNoQueryable()
	{
		return this.get(NOQUERYABLE) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(NOQUERYABLE));
	}

	/**
	 * @param isAbstract
	 *            the isAbstract to set
	 */
	public void setNoQueryable(boolean isNoQueryable)
	{
		this.put(NOQUERYABLE, BooleanUtils.getBooleanStringYN(isNoQueryable));
	}

	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract()
	{
		return this.get(ISABSTRACT) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISABSTRACT));
	}

	/**
	 * @param isAbstract
	 *            the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract)
	{
		this.put(ISABSTRACT, BooleanUtils.getBooleanStringYN(isAbstract));
	}

	/**
	 * @return the isFinal
	 */
	public boolean isFinal()
	{
		return this.get(ISFINAL) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISFINAL));
	}

	/**
	 * @param isFinal
	 *            the isFinal to set
	 */
	public void setFinal(boolean isFinal)
	{
		this.put(ISFINAL, BooleanUtils.getBooleanStringYN(isFinal));
	}

	/**
	 * @return the icon
	 */
	public String getIcon()
	{
		return (String) this.get(ICON);
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon)
	{
		this.put(ICON, icon);
	}

	/**
	 * @return the icon32
	 */
	public String getIcon32()
	{
		return (String) this.get(ICON32);
	}

	/**
	 * @param icon32
	 *            the icon32 to set
	 */
	public void setIcon32(String icon32)
	{
		this.put(ICON32, icon32);
	}

	/**
	 * @return the instanceString
	 */
	public String getInstanceString()
	{
		return (String) this.get(INSTANCESTRING);
	}

	/**
	 * @param instanceString
	 *            the instanceString to set
	 */
	public void setInstanceString(String instanceString)
	{
		this.put(INSTANCESTRING, instanceString);
	}

	/**
	 * @return the lifecycle
	 */
	public String getLifecycle()
	{
		return (String) this.get(LIFECYCLE);
	}

	/**
	 * @param lifecycle
	 *            the lifecycle to set
	 */
	public void setLifecycle(String lifecycle)
	{
		this.put(LIFECYCLE, lifecycle);
	}

	/**
	 * @return the lifecycleName
	 */
	public String getLifecycleName()
	{
		return (String) this.get(LIFECYCLE + "NAME");
	}

	/**
	 * @param lifecycleName
	 *            the lifecycleName to set
	 */
	public void setLifecycleName(String lifecycleName)
	{
		this.put(LIFECYCLE + "NAME", lifecycleName);
	}

	/**
	 * @return the isBuiltin
	 */
	public boolean isBuiltin()
	{
		return this.get(ISBUILTIN) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISBUILTIN));
	}

	/**
	 * @param isBuiltin
	 *            the isBuiltin to set
	 */
	public void setBuiltin(boolean isBuiltin)
	{
		this.put(ISBUILTIN, BooleanUtils.getBooleanStringYN(isBuiltin));
	}

	public boolean hasInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getInterfaceList().contains(interfaceEnum);
	}

	/**
	 * @return the interfaceList
	 */
	public List<ModelInterfaceEnum> getInterfaceList()
	{
		return this.interfaceList;
	}

	/**
	 * @param interfaceList
	 *            the interfaceList to set
	 */
	public void setInterfaceList(List<ModelInterfaceEnum> interfaceList)
	{
		if (interfaceList != null)
		{
			this.interfaceList = interfaceList;
		}
		else
		{
			this.interfaceList = new ArrayList<ModelInterfaceEnum>();
		}
	}

	/**
	 * @param interface
	 *            the interface to add
	 */
	public void addInterface(String interfaceName)
	{
		try
		{
			ModelInterfaceEnum interfaceEnum = ModelInterfaceEnum.valueOf(interfaceName);
			if (interfaceEnum != null)
			{
				this.addInterface(interfaceEnum);
			}
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * @param interface
	 *            the interface to add
	 */
	private void addInterface(ModelInterfaceEnum interfaceEnum)
	{
		if (this.interfaceList.contains(interfaceEnum))
		{
			return;
		}
		this.interfaceList.add(interfaceEnum);
		if (interfaceEnum.getSuperInterfaces() != null)
		{
			for (ModelInterfaceEnum superInterface : interfaceEnum.getSuperInterfaces())
			{
				this.addInterface(superInterface);
			}
		}
	}

	/**
	 * @return the classificationGroup
	 */
	public String getClassification()
	{
		return (String) this.get(CLASSIFICATIONGROUP);
	}

	/**
	 * @param classificationGroup
	 *            the classificationGroup to set
	 */
	public void setClassification(String classificationGroup)
	{
		this.put(CLASSIFICATIONGROUP, classificationGroup);
	}

	/**
	 * @return the classificationGroupName
	 */
	public String getClassificationName()
	{
		return (String) this.get(CLASSIFICATIONGROUP + "NAME");
	}

	/**
	 * @param classificationGroupName
	 *            the classificationGroupName to set
	 */
	public void setClassificationName(String classificationGroupName)
	{
		this.put(CLASSIFICATIONGROUP + "NAME", classificationGroupName);
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
	 * @param iterationLimit
	 *            the iterationLimit to set
	 */
	public void setIterationLimit(String iterationLimit)
	{
		this.put(ITERATIONLIMIT, iterationLimit);
	}

	/**
	 * @return the iterationLimit
	 */
	public String getIterationLimit()
	{
		return (String) this.get(ITERATIONLIMIT);
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	public void setInterfaces(String interfaces)
	{
		this.put(INTERFACES, interfaces);
	}

	/**
	 * @return the interfaces
	 */
	public String getInterfaces()
	{
		return (String) this.get(INTERFACES);
	}

	/**
	 * @param system
	 *            the system to set
	 */
	public void setSystem(boolean system)
	{
		this.put(SYSTEM, BooleanUtils.getBooleanStringYN(system));
	}

	/**
	 * @return the system
	 */
	public boolean isSystem()
	{
		return this.get(SYSTEM) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(SYSTEM));
	}

	/**
	 * @return the showPreview
	 */
	public boolean isShowPreview()
	{
		return this.get(SHOWPREVIEW) == null ? true : BooleanUtils.getBooleanByYN((String) this.get(SHOWPREVIEW));
	}

	/**
	 * @param showPreview
	 *            the showPreview to set
	 */
	public void setShowPreview(boolean showPreview)
	{
		this.put(SHOWPREVIEW, BooleanUtils.getBooleanStringYN(showPreview));
	}

	/**
	 * @return the isCreateTable
	 */
	public boolean isCreateTable()
	{
		return this.get(ISCREATETABLE) == null ? true : BooleanUtils.getBooleanByYN((String) this.get(ISCREATETABLE));
	}

	/**
	 * @param isCreateTable
	 *            the isCreateTable to set
	 */
	public void setCreateTable(boolean isCreateTable)
	{
		this.put(ISCREATETABLE, BooleanUtils.getBooleanStringYN(isCreateTable));
	}

	public String getSuperInterface()
	{
		return (String) this.get(SUPERINTERFACE);
	}

	public void setSuperInterface(String superInterface)
	{
		this.put(SUPERINTERFACE, superInterface);
	}

	public boolean isCheckUnique()
	{
		return this.get(ISCHECKUNIQUE) == null ? true : BooleanUtils.getBooleanByYN((String) this.get(ISCHECKUNIQUE));
	}

	public void setCheckUnique(boolean isCheckUnique)
	{
		this.put(ISCHECKUNIQUE, BooleanUtils.getBooleanStringYN(isCheckUnique));
	}

	public boolean isIdUnique()
	{
		return this.get(ISIDUNIQUE) == null ? true : BooleanUtils.getBooleanByYN((String) this.get(ISIDUNIQUE));
	}

	public void setIdUnique(boolean isIdUnique)
	{
		this.put(ISIDUNIQUE, BooleanUtils.getBooleanStringYN(isIdUnique));
	}

	public String getRealBaseTableName()
	{
		return (String) this.get(REALBASETABLENAME);
	}

	public void setRealBaseTableName(String realBaseTableName)
	{
		this.put(REALBASETABLENAME, realBaseTableName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ClassInfo clone()
	{
		return (ClassInfo) super.clone();
	}
}
