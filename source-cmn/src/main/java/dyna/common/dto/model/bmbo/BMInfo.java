/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BMInfo
 * Wanglei 2010-9-2
 */
package dyna.common.dto.model.bmbo;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.bmbo.BMInfoMapper;
import dyna.common.systemenum.BusinessModelType;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;

/**
 * @author Wanglei
 * 
 */
@Cache
@EntryMapper(BMInfoMapper.class)
public class BMInfo extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 7454704314111283772L;

	public static final String	SHARE_MODEL			= "ALL";

	public static final String	BMNAME				= "BMNAME";

	public static final String	TITLE				= "TITLE";

	public static final String	MODELTYPE			= "MODELTYPE";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(BMNAME);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(BMNAME, name);
	}

	/**
	 * @return the title by language
	 */
	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return (String) this.get(TITLE);
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

	public void setModelType(String modelType)
	{
		this.put(MODELTYPE, modelType);
	}

	public String getModelType()
	{
		return (String) this.get(MODELTYPE);
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	public boolean isShareMode()
	{
		return BusinessModelType.SHARED_MODEL.getName().equalsIgnoreCase(this.getName());
	}

	@Override
	public BMInfo clone()
	{
		return (BMInfo) super.clone();
	}
}
