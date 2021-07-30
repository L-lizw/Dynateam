/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOInfo
 * Wanglei 2010-9-2
 */
package dyna.common.dto.model.bmbo;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.BusinessModelTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
@Cache
public class BOInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 4181949505516193453L;

	public static final String	BONAME				= "BONAME";

	public static final String	TITLE				= "TITLE";

	public static final String	CLASSGUID			= "CLASSGUID";

	public static final String	CLASSNAME			= "CLASSNAME";

	// 该字段目前不赋值
	public static final String	CLASSIFICATIONGUID	= "CLASSIFICATIONGUID";

	public static final String	CLASSIFICATIONNAME	= "CLASSIFICATIONNAME";

	public static final String	MODELTYPE			= "MODELTYPE";

	public static final String	TYPE				= "BOTYPE";

	public static final String	SHOWCLASSIFICATION	= "SHOWCLASSIFICATION";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	ICONPATH			= "ICONPATH";

	public static final String	ICONPATH32			= "ICONPATH32";

	public static final String	ISABSTRACT			= "ISABSTRACT";

	public static final String	ISCREATETABLE		= "ISCREATETABLE";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	PARENTBOGUID		= "PARENTBOGUID";

	public static final String	BMGUID				= "BMGUID";

	private String				bmName				= null;

	private boolean				isShared			= false;

	private String				parent				= null;

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.getBOName();
	}

	public String getBOName()
	{
		return (String) this.get(BONAME);
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	/**
	 * @return the title by language
	 */
	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	/**
	 * @return BusinessModelTypeEnum类型枚举
	 */
	public BusinessModelTypeEnum getType()
	{
		String type = (String) this.get(TYPE);
		return BusinessModelTypeEnum.getEnum(type);
	}

	public String getModelType()
	{
		return (String) this.get(MODELTYPE);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.setBOName(name);
	}

	public void setBOName(String name)
	{
		this.put(BONAME, name);
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(BusinessModelTypeEnum type)
	{
		this.put(TYPE, type.getValue());
	}

	public void setModelType(String modelType)
	{
		this.put(MODELTYPE, modelType);
	}

	/**
	 * @return the classGuid
	 */
	public String getClassGuid()
	{
		return (String) this.get(CLASSGUID);
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		this.put(CLASSGUID, classGuid);
	}

	/**
	 * @return the classificationGuid
	 */
	public String getClassificationGuid()
	{
		return (String) this.get(CLASSIFICATIONGUID);
	}

	/**
	 * @param classificationGuid
	 *            the classificationGuid to set
	 */
	public void setClassificationGuid(String classificationGuid)
	{
		this.put(CLASSIFICATIONGUID, classificationGuid);
	}

	/**
	 * @param showClassification
	 *            the showClassification to set
	 */
	public void setShowClassification(boolean showClassification)
	{
		this.put(SHOWCLASSIFICATION, BooleanUtils.getBooleanStringYN(showClassification));
	}

	/**
	 * @return the showClassification
	 */
	public boolean isShowClassification()
	{
		return this.get(SHOWCLASSIFICATION) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(SHOWCLASSIFICATION));
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

	public String getClassificationName()
	{
		return (String) this.get(CLASSIFICATIONNAME);
	}

	public void setClassificationName(String classificationName)
	{
		this.put(CLASSIFICATIONNAME, classificationName);
	}

	public boolean isShared()
	{
		return isShared;
	}

	public void setShared(boolean isShared)
	{
		this.isShared = isShared;
	}

	/**
	 * @return the classGuid
	 */
	public String getClassName()
	{
		return (String) this.get(CLASSNAME);
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassName(String className)
	{
		this.put(CLASSNAME, className);
	}

	public String getBmName()
	{
		return bmName;
	}

	public void setBmName(String bmName)
	{
		this.bmName = bmName;
	}

	public boolean isAbstract()
	{
		return this.get(ISABSTRACT) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISABSTRACT));
	}

	public void setAbstract(boolean isAbstract)
	{
		this.put(ISABSTRACT, BooleanUtils.getBooleanStringYN(isAbstract));
	}

	public void setCreateTable(boolean isCreateTable)
	{
		this.put(ISCREATETABLE, BooleanUtils.getBooleanStringYN(isCreateTable));
	}

	public boolean isCreateTable()
	{
		return this.get(ISCREATETABLE) == null ? false : BooleanUtils.getBooleanByYN((String) this.get(ISCREATETABLE));
	}

	public String getIcon()
	{
		if (this.getType() == BusinessModelTypeEnum.PACKAGE)
		{
			return "package.png";
		}
		else if (StringUtils.isNullString((String) this.get(ICONPATH)))
		{
			return "object.png";
		}
		else
		{
			return (String) this.get(ICONPATH);
		}
	}

	public String getIcon32()
	{
		if (this.getType() == BusinessModelTypeEnum.PACKAGE)
		{
			return "package.png";
		}
		else if (StringUtils.isNullString((String) this.get(ICONPATH32)))
		{
			return "object.png";
		}
		else
		{
			return (String) this.get(ICONPATH32);
		}
	}

	public String getBMGuid()
	{
		return (String) this.get(BMGUID);
	}

	public void setBMGuid(String bmGuid)
	{
		this.put(BMGUID, bmGuid);
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	public String getParentBOGuid()
	{
		return (String) this.get(PARENTBOGUID);
	}

	public void setParentBOGuid(String parentBOGuid)
	{
		this.put(PARENTBOGUID, parentBOGuid);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof BOInfo))
		{
			return false;
		}

		// fix 类型为Package的BOInfo,Guid为null,用name判断是否相等
		// return (((BOInfo) obj).getGuid()).equals(this.getGuid());
		BOInfo boInfo = (BOInfo) obj;
		if (boInfo.getGuid() != null)
		{
			return boInfo.getGuid().equals(this.getGuid());
		}
		else if (boInfo.getName() != null)
		{
			return boInfo.getName().equals(this.getName());
		}
		return false;
	}

	public String getParent()
	{
		return parent;
	}

	public void setParent(String parent)
	{
		this.parent = parent;
	}

	@Override
	public BOInfo clone()
	{
		return (BOInfo) super.clone();
	}
}
