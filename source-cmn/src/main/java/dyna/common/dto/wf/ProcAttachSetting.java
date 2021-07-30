/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttachTemplate
 * zhanghj 2011-4-16
 */
package dyna.common.dto.wf;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 附件设置
 * 
 * @author zhanghj
 * @author Wanglei updated at 2012-2-24
 * 
 */
public class ProcAttachSetting extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 8590310283645872195L;
	public static final String	PROCRT_GUID			= "PROCRTGUID";

	private List<String>		boNameList			= null;

	private List<WFRelationSet>	relationSetList		= null;

	// 需要计算的附件
	public static final String	ATTACH_GUID			= "ATTACHGUID";
	
	public static final String	ATTACH_CLASSGUID	= "ATTACHCLASSGUID";



	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String procRtGuid)
	{
		super.put(PROCRT_GUID, procRtGuid);
	}

	public String getAttachGuid()
	{
		return (String) super.get(ATTACH_GUID);
	}

	public void setAttachGuid(String attachGuid)
	{
		super.put(ATTACH_GUID, attachGuid);
	}
	
	public String getAttachClassGuid()
	{
		return (String) super.get(ATTACH_CLASSGUID);
	}

	public void setAttacClasshGuid(String attachClassGuid)
	{
		super.put(ATTACH_CLASSGUID, attachClassGuid);
	}

	//
	// public String getViewName()
	// {
	// return (String) super.get(VIEW_NAME);
	// }
	//
	// public void setViewName(String viewName)
	// {
	// super.put(VIEW_NAME, viewName);
	// }
	//
	// public LevelType getLevelType()
	// {
	// String type = (String) super.get(LEVEL_TYPE);
	// if (type == null)
	// {
	// return null;
	// }
	// int idx = Integer.valueOf(type) - 1;
	// return LevelType.values()[idx];
	// }
	//
	// public void setLevelType(LevelType levelType)
	// {
	// super.put(LEVEL_TYPE, String.valueOf(levelType.ordinal() + 1));
	// }
	//
	// public BomType getBomType()
	// {
	// String type = (String) super.get(BOM_TYPE);
	// if (type == null)
	// {
	// return null;
	// }
	// int idx = Integer.valueOf(type) - 1;
	// return BomType.values()[idx];
	// }
	//
	// public void setBomType(BomType bomType)
	// {
	// super.put(BOM_TYPE, String.valueOf(bomType.ordinal() + 1));
	// }
	//
	// public String getInstatnceGuid()
	// {
	// return (String) super.get(INSTANCE_GUID);
	// }
	//
	// public void setInstatnceGuid(String guid)
	// {
	// super.put(INSTANCE_GUID, guid);
	// }

	public List<WFRelationSet> getRelationSetList()
	{
		return this.relationSetList;
	}

	public void setRelationSetList(List<WFRelationSet> relationSetList)
	{
		this.relationSetList = relationSetList;
	}

	public List<String> getBoNameList()
	{
		return this.boNameList;
	}

	public void setBoNameList(List<String> boNameList)
	{
		this.boNameList = boNameList;
	}

}
