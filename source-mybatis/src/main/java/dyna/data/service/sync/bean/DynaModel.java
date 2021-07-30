package dyna.data.service.sync.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

import dyna.common.bean.model.cls.ClassObject;
import dyna.common.util.StringUtils;

public class DynaModel implements Cloneable, Serializable
{
	/**
	 *
	 */
	private static final long						serialVersionUID		= -27063582538205428L;

	private Map<String /* name */, ClassObject>		classObjectMap			= new HashMap<>();

	private Map<String /* guid */, ClassObject>		classObjectWithGuidMap	= new HashMap<>();

	private Map<String /* name */, CodeModelInfo>   codeItemMap             = new HashMap<>();

	private Map<String /* name */, CodeModelInfo>   classificationItemMap   = new HashMap<>();

	/**
	 * @return the classObjectMap
	 */
	public Map<String, ClassObject> getClassObjectMap()
	{
		return this.classObjectMap;
	}

	/**
	 * @param classObjectMap
	 *            the classObjectMap to set
	 */
	public void setClassObjectMap(Map<String, ClassObject> classObjectMap)
	{
		this.classObjectMap = classObjectMap;
	}

	/**
	 * @param classObjectName
	 * @return ClassObject
	 */
	public ClassObject getClassObject(String classObjectName)
	{
		return this.classObjectMap.get(classObjectName);
	}

	public ClassObject getClassObjectByGuid(String classGuid)
	{
		return this.classObjectWithGuidMap.get(classGuid);
	}

	public ClassObject getClassObject(String objectTypeValue, String businessName)
	{
		if (!StringUtils.isNullString(objectTypeValue))
		{
			return this.getClassObject(objectTypeValue);
		}
		return null;
	}

	public Map<String, CodeModelInfo> getCodeItemMap()
	{
		return codeItemMap;
	}

	public void setCodeItemMap(Map<String, CodeModelInfo> codeItemMap)
	{
		this.codeItemMap = codeItemMap;
	}

	public Map<String, CodeModelInfo> getClassificationItemMap()
	{
		return classificationItemMap;
	}

	public void setClassificationItemMap(Map<String, CodeModelInfo> classificationItemMap)
	{
		this.classificationItemMap = classificationItemMap;
	}

	/**
	 * 取得根节点的分类对象，该节点是虚拟的，不可变更的。
	 *
	 * @param classificationName
	 * @return
	 */
	public CodeModelInfo getRootCodeObject()
	{
		return this.classificationItemMap.get("CodeModel");
	}

	// to xml string
	public String toXMLString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DynaModel clone()
	{
		try
		{
			return (DynaModel) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
