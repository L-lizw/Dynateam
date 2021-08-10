/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActClass
 * WangLHB Jan 6, 2012
 */
package dyna.common.dto.template.wft;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.template.wft.WorkflowTemplateActClassInfoMapper;

/**
 * 工作流模板活动节点class设置 Bean
 * 
 * @author WangLHB
 * 
 */
@Cache
@EntryMapper(WorkflowTemplateActClassInfoMapper.class)
public class WorkflowTemplateActClassInfo extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long						serialVersionUID		= -7130352152192777523L;

	// public static final String GUID = "GUID";
	public static final String						TEMPLATEACTRTGUID		= "TEMPLATEACTRTGUID";
	public static final String						BOGUID					= "BOGUID";
	public static final String						HASEFFECTIVETIME		= "HASEFFECTIVETIME";
	public static final String						HASOBSOLETETIME			= "HASOBSOLETETIME";
	public static final String						HASSCRIPT				= "HASSCRIPT";
	public static final String						EDITFILE				= "EDITFILE";
	public static final String						VIEWFILE				= "SELECTFILE";

	// 1：has； 0：not
	public static final String						CLASS_TYPE_HAS			= "1";
	public static final String						CLASS_TYPE_NOT			= "0";

	/**
	 * @return the templateActrtGuid
	 */
	public String getTemplateActrtGuid()
	{
		return (String) this.get(TEMPLATEACTRTGUID);
	}

	/**
	 * @param templateActrtGuid
	 *            the templateActrtGuid to set
	 */
	public void setTemplateActrtGuid(String templateActrtGuid)
	{
		this.put(TEMPLATEACTRTGUID, templateActrtGuid);
	}

	/**
	 * @return the boGuid
	 */
	public String getBOGuid()
	{
		return (String) this.get(BOGUID);
	}

	/**
	 * @param boGuid
	 *            the classGuid to set
	 */
	public void setBOGuid(String boGuid)
	{
		this.put(BOGUID, boGuid);
	}

	/**
	 * @param editFile
	 *            the editFile to set
	 */
	public void setEditFile(boolean editFile)
	{
		this.put(EDITFILE, editFile == true ? CLASS_TYPE_HAS : CLASS_TYPE_NOT);
	}

	/**
	 * @return the editFile
	 */
	public boolean hasEditFile()
	{
		return CLASS_TYPE_HAS.equals(this.get(EDITFILE)) ? true : false;
	}

	/**
	 * @param viewFile
	 *            the viewFile to set
	 */
	public void setViewFile(boolean viewFile)
	{

		this.put(VIEWFILE, viewFile == true ? CLASS_TYPE_HAS : CLASS_TYPE_NOT);
	}

	/**
	 * @return the viewFile
	 */
	public boolean hasViewFile()
	{
		if (this.get(VIEWFILE) == null)
		{
			return true;
		}
		else
		{
			return CLASS_TYPE_HAS.equals(this.get(VIEWFILE));
		}

	}

	/**
	 * @return the hasEffectiveTime
	 */
	public boolean hasEffectiveTime()
	{
		return CLASS_TYPE_HAS.equals(this.get(HASEFFECTIVETIME)) ? true : false;
	}

	/**
	 * @param hasEffectiveTime
	 *            the hasEffectiveTime to set
	 */
	public void setEffectiveTime(boolean hasEffectiveTime)
	{
		this.put(HASEFFECTIVETIME, hasEffectiveTime == true ? CLASS_TYPE_HAS : CLASS_TYPE_NOT);
	}

	/**
	 * @return the hasObsoleteTime
	 */
	public boolean hasObsoleteTime()
	{
		return CLASS_TYPE_HAS.equals(this.get(HASOBSOLETETIME)) ? true : false;
	}

	/**
	 * @param hasObsoleteTime
	 *            the hasObsoleteTime to set
	 */
	public void setObsoleteTime(boolean hasObsoleteTime)
	{
		this.put(HASOBSOLETETIME, hasObsoleteTime == true ? CLASS_TYPE_HAS : CLASS_TYPE_NOT);
	}

	/**
	 * @return the hasScript
	 */
	public boolean hasScript()
	{
		return CLASS_TYPE_HAS.equals(this.get(HASSCRIPT)) ? true : false;
	}

	/**
	 * @param hasScript
	 *            the hasScript to set
	 */
	public void setScript(boolean hasScript)
	{
		this.put(HASSCRIPT, hasScript == true ? CLASS_TYPE_HAS : CLASS_TYPE_NOT);
	}

	public boolean hasPropertyModify()
	{
		return this.hasEffectiveTime() || this.hasEditFile() || this.hasViewFile() || this.hasObsoleteTime() || this.hasScript();
	}

	public void clearForCreate()
	{
		this.clear(GUID);
		this.clear(TEMPLATEACTRTGUID);
	}

	@Override
	public WorkflowTemplateActClassInfo clone()
	{
		WorkflowTemplateActClassInfo actClass = (WorkflowTemplateActClassInfo) super.clone();
		return actClass;
	}
}
