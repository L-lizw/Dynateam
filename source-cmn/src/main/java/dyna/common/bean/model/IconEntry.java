/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: IconEntry
 * WangLHB Aug 31, 2011
 */
package dyna.common.bean.model;

import java.io.Serializable;

/**
 * @author Jiagang
 * 
 */
public class IconEntry implements Cloneable, Serializable
{

	private static final long	serialVersionUID	= -5854200516729883906L;

	private String				classGuid			= null;
	
	private String				actionGuid			= null;
	
	private String				actionName			= null;

	private String				imagePath;

	private String				imagePath32;

	private String				thumbnailPath		= null;

	private String				symbolPath			= null;

	private Boolean				showPreview			= null;

	public IconEntry(String classGuid, String path, String path32, Boolean showpreview)
	{
		this.classGuid = classGuid;
		this.imagePath = path;
		this.imagePath32 = path32;
		this.showPreview = showpreview;
	}

	/**
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath()
	{
		return this.imagePath;
	}

	/**
	 * @param imagePath32
	 *            the imagePath32 to set
	 */
	public void setImagePath32(String imagePath32)
	{
		this.imagePath32 = imagePath32;
	}

	/**
	 * @return the imagePath32
	 */
	public String getImagePath32()
	{
		return this.imagePath32;
	}

	/**
	 * @return the showPreview
	 */
	public boolean isShowPreview()
	{
		return this.showPreview;
	}

	/**
	 * @param showPreview
	 *            the showPreview to set
	 */
	public void setShowPreview(boolean showPreview)
	{
		this.showPreview = showPreview;
	}

	public String getThumbnailPath()
	{
		return this.thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath)
	{
		this.thumbnailPath = thumbnailPath;
	}

	public String getSymbolPath()
	{
		return this.symbolPath;
	}

	public void setSymbolPath(String symbolPath)
	{
		this.symbolPath = symbolPath;
	}

	public String getClassGuid()
	{
		return classGuid;
	}

	public void setClassGuid(String classGuid)
	{
		this.classGuid = classGuid;
	}

	public Boolean getShowPreview()
	{
		return showPreview;
	}

	public void setShowPreview(Boolean showPreview)
	{
		this.showPreview = showPreview;
	}

	public String getActionGuid()
	{
		return actionGuid;
	}

	public void setActionGuid(String actionGuid)
	{
		this.actionGuid = actionGuid;
	}

	public String getActionName()
	{
		return actionName;
	}

	public void setActionName(String actionName)
	{
		this.actionName = actionName;
	}

}