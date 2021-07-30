package dyna.data.service.sync.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.dto.model.cls.ClassField;

/**
 * 从XML模型中加载的数据存储对象
 * 
 * @author patri
 *
 */
public class ModelXMLCache implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long					serialVersionUID	= 1180837158808574950L;

	/**
	 * key:interfaceName
	 */
	private Map<String, InterfaceObject>		interfaceMap		= new HashMap<String, InterfaceObject>();

	/**
	 * 接口字段
	 * key:interfaceName
	 */
	private Map<String, List<ClassField>>		interfaceFieldMap	= new HashMap<String, List<ClassField>>();

	/**
	 * 接口上配置的索引模型
	 * key:interfaceName
	 */
	private Map<String, List<TableIndexModel>>	interfaceIndexMap	= new HashMap<String, List<TableIndexModel>>();

	/**
	 * key:className
	 */
	private Map<String, ClassObject>			classObjectMap		= new HashMap<String, ClassObject>();

	/**
	 * key:className+"."+fieldName
	 */
	private Map<String, ClassField>				classFieldMap		= new HashMap<String, ClassField>();

	/**
	 * key:codeObjectName
	 */
	private Map<String, CodeObject>				codeObjectMap		= new HashMap<String, CodeObject>();

	public Map<String, InterfaceObject> getInterfaceMap()
	{
		return interfaceMap;
	}

	public void setInterfaceMap(Map<String, InterfaceObject> interfaceMap)
	{
		this.interfaceMap = interfaceMap;
	}

	public Map<String, List<ClassField>> getInterfaceFieldMap()
	{
		return interfaceFieldMap;
	}

	public void setInterfaceFieldMap(Map<String, List<ClassField>> interfaceFieldMap)
	{
		this.interfaceFieldMap = interfaceFieldMap;
	}

	public Map<String, List<TableIndexModel>> getInterfaceIndexMap()
	{
		return interfaceIndexMap;
	}

	public void setInterfaceIndexMap(Map<String, List<TableIndexModel>> interfaceIndexMap)
	{
		this.interfaceIndexMap = interfaceIndexMap;
	}

	public Map<String, ClassObject> getClassObjectMap()
	{
		return classObjectMap;
	}

	public void setClassObjectMap(Map<String, ClassObject> classObjectMap)
	{
		this.classObjectMap = classObjectMap;
	}

	public Map<String, ClassField> getClassFieldMap()
	{
		return classFieldMap;
	}

	public void setClassFieldMap(Map<String, ClassField> classFieldMap)
	{
		this.classFieldMap = classFieldMap;
	}

	public Map<String, CodeObject> getCodeObjectMap()
	{
		return codeObjectMap;
	}

	public void setCodeObjectMap(Map<String, CodeObject> codeObjectMap)
	{
		this.codeObjectMap = codeObjectMap;
	}

	/**
	 * @param classObjectName
	 * @return ClassObject
	 */
	public ClassObject getClassObject(String classObjectName)
	{
		return this.classObjectMap.get(classObjectName);
	}
}