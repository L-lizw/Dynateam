package dyna.common.dto.cfm;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.cfm.ClassficationFeatureMapper;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

@Cache
@EntryMapper(ClassficationFeatureMapper.class)
public class ClassficationFeature extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID		= 1L;
	public final static String	CLASSGUID				= "CLASSGUID";
	public final static String	CLASSIFICATIONFK		= "CLASSIFICATIONFK";
	public final static String	ISMASTER				= "ISMASTER";
	public final static String	ISREVISIONRELATED		= "ISREVISIONRELATED";
	public final static String	CLASSFIFCATION_TITLE	= "CLASSFIFCATIONTITLE";
	public final static String	CLASSFIFCATION_NAME		= "CLASSFIFCATIONNAME";

	private final static String	ISINHERIT				= "ISINHERIT";

	public String getClassGuid()
	{
		return (String) super.get(CLASSGUID);
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		super.put(CLASSGUID, classGuid);
	}

	public String getTitle()
	{
		return (String) super.get(CLASSFIFCATION_TITLE);
	}

	/**
	 * @param classGuid
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		super.put(CLASSFIFCATION_TITLE, title);
	}

	public void setClassificationName(String classificationName)
	{
		super.put(CLASSFIFCATION_NAME, classificationName);
	}

	public String getClassificationName()
	{
		return (String) super.get(CLASSFIFCATION_NAME);
	}

	public String getClassificationfk()
	{
		return (String) super.get(CLASSIFICATIONFK);
	}

	public void setClassificationfk(String classificationfk)
	{
		super.put(CLASSIFICATIONFK, classificationfk);
	}

	public boolean isMaster()
	{
		if (StringUtils.isNullString((String) super.get(ISMASTER)))
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) super.get(ISMASTER));

	}

	public void setIsmaster(boolean ismaster)
	{
		super.put(ISMASTER, BooleanUtils.getBooleanStringYN(ismaster));
	}

	public String getIsrevisionrelated()
	{
		return (String) super.get(ISREVISIONRELATED);
	}

	public void setIsrevisionrelated(String isrevisionrelated)
	{
		super.put(ISREVISIONRELATED, isrevisionrelated);
	}

	public boolean isInherit()
	{
		if (this.get(ISINHERIT) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(ISINHERIT));
	}

	public void setInherit(boolean isInherit)
	{
		this.put(ISINHERIT, BooleanUtils.getBooleanStringYN(isInherit));
	}
}
