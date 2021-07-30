/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UIIcon
 * WangLHB Aug 31, 2011
 */
package dyna.common.bean.model.ui;

import java.io.Serializable;
import java.util.List;

import dyna.common.bean.model.IconEntry;

/**
 * @author WangLHB
 * 
 */
public class UIIcon implements Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7625651487629300398L;

	private IconEntry			classIcon			= null;

	private List<IconEntry>		actionIcons			= null;

	/**
	 * @param classIcon
	 *            the classIcon to set
	 */
	public void setClassIcon(IconEntry classIcon)
	{
		this.classIcon = classIcon;
	}

	/**
	 * @return the classIcon
	 */
	public IconEntry getClassIcon()
	{
		return this.classIcon;
	}

	/**
	 * @param actionIcons
	 *            the actionIcons to set
	 */
	public void setActionIcons(List<IconEntry> actionIcons)
	{
		this.actionIcons = actionIcons;
	}

	/**
	 * @return the actionIcons
	 */
	public List<IconEntry> getActionIcons()
	{
		return this.actionIcons;
	}

}
