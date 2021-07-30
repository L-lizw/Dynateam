/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessPerformer 流程活动执行人指定
 * Wanglei 2010-11-3
 */
package dyna.common.bean.extra;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.wf.Performer;

/**
 * 流程活动执行人指定
 * 
 * @author Wanglei
 * 
 */
public class ProcessSetting implements Serializable
{

	private static final long									serialVersionUID	= -3203389693369792907L;

	/**
	 * 流程描述
	 */
	private String												procRtDesc			= null;

	/**
	 * activityName(actName) - <performerGuid, Performer>
	 */
	private final Map<String, Map<String, Performer>>	actPerformerMap		= new HashMap<String, Map<String, Performer>>();

	/**
	 * activityGuid - deadline
	 */
	private final Map<String, Date>								deadlineMap			= new HashMap<String, Date>();

	/**
	 * instaceGuid - proctrackattach
	 */
	// private final Map<String, String> attachOpinionMap = new HashMap<String, String>();

	/**
	 * instanceguid - objectguid insert
	 */
	// private final Map<String, ObjectGuid> attachMap = new HashMap<String, ObjectGuid>();
	//
	// /**
	// * instanceguid - objectguid old
	// */
	// private final Map<String, ObjectGuid> oldAttachMap = new HashMap<String, ObjectGuid>();
	//
	// /**
	// * instanceguid - objectguid
	// */
	// private final Map<String, ObjectGuid> removeAttachMap = new HashMap<String, ObjectGuid>();

	/**
	 * activityRuntimeGuid - userguid
	 */
	private final Map<String, String>							removePerformerMap	= new HashMap<String, String>();

	// /**
	// * workflow file
	// */
	// private List<DSSFileInfo> listProcFile = new ArrayList<DSSFileInfo>();
	//
	// /**
	// * instanceGuid - bomTempNames
	// */
	// private Map<String, Set<String>> bomTempNameMap = new HashMap<String, Set<String>>();
	//
	// /**
	// * instanceGuid - relationTempNames
	// */
	// private Map<String, Set<String>> relationTempNameMap = new HashMap<String, Set<String>>();

	// private String parentProcGuid = null;
	//
	// private String parentActGuid = null;

	// resumeflow use
	// private ObjectGuid bomObjectGuid = null;

	protected Map<String, Performer> getPerformerMap(String actName)
	{
		Map<String, Performer> perfMap = this.actPerformerMap.get(actName);
		if (perfMap == null)
		{
			perfMap = new LinkedHashMap<String, Performer>();
			this.actPerformerMap.put(actName, perfMap);
		}
		return perfMap;
	}

	/**
	 * @return the procRtDesc
	 */
	public String getProcRtDesc()
	{
		return this.procRtDesc;
	}

	/**
	 * @param procRtDesc
	 *            the procRtDesc to set
	 */
	public void setProcRtDesc(String procRtDesc)
	{
		this.procRtDesc = procRtDesc;
	}

	/**
	 * 获取附件新增列表
	 * 
	 * @return
	 */
	// public List<ObjectGuid> getAttachList()
	// {
	// return Collections.unmodifiableList(new ArrayList<ObjectGuid>(this.attachMap.values()));
	// }

	/**
	 * 重启流程时
	 * 获取流程中(上次流程未改变的)附件列表
	 * 
	 * @return
	 */
	// public List<ObjectGuid> getOldAttachList()
	// {
	// return Collections.unmodifiableList(new ArrayList<ObjectGuid>(this.oldAttachMap.values()));
	// }

	/**
	 * 添加流程附件
	 * 
	 * @param objectGuid
	 */
	// public void addAttach(ObjectGuid objectGuid)
	// {
	// this.attachMap.put(objectGuid.getGuid(), objectGuid);
	// }

	/**
	 * 添加流程附件
	 * 
	 * @param objectGuid
	 */
	// public void addAttach(List<ObjectGuid> listAttachObjectGuid)
	// {
	// if (!SetUtils.isNullList(listAttachObjectGuid))
	// {
	// for (ObjectGuid objectGuid : listAttachObjectGuid)
	// {
	// this.attachMap.put(objectGuid.getGuid(), objectGuid);
	// }
	// }
	// }

	/**
	 * 添加流程中原有附件
	 * 
	 * @param objectGuid
	 */
	// public void addOldAttach(ObjectGuid objectGuid)
	// {
	// this.oldAttachMap.put(objectGuid.getGuid(), objectGuid);
	// }

	/**
	 * 添加流程文件
	 * 
	 * @param objectGuid
	 */
	public void addProcFile(List<DSSFileInfo> listProcFile)
	{
		listProcFile.addAll(listProcFile);
	}

	//
	// /**
	// * 获取流程文件
	// *
	// * @param objectGuid
	// */
	// public List<DSSFileInfo> listProcFile()
	// {
	// return this.listProcFile;
	// }
	//
	// public void setProcFile(List<DSSFileInfo> listProcFile)
	// {
	// this.listProcFile = listProcFile;
	// }

	/**
	 * 添加流程附件意见
	 * 
	 * @param objectGuid
	 */
	// public void addAttachOpinion(String guid, String comments)
	// {
	// this.attachOpinionMap.put(guid, comments);
	// }
	//
	// /**
	// * @return the attachOpinionMap
	// */
	// public String getAttachOpinion(String guid)
	// {
	// return this.attachOpinionMap.get(guid);
	// }
	//
	// public Map<String, String> getAttachOpinionMap()
	// {
	// return this.attachOpinionMap;
	// }

	/**
	 * 删除流程附件
	 * 
	 * @param objectGuid
	 */
	// public void removeAttach(ObjectGuid objectGuid)
	// {
	// this.attachMap.remove(objectGuid.getGuid());
	// }

	/**
	 * 清除附件map数据
	 */
	// public void clearAttachMap()
	// {
	// this.attachMap.clear();
	// }

	/**
	 * 获取需要删除的附件列表
	 * 
	 * @return
	 */
	// public List<ObjectGuid> getRemoveAttachList()
	// {
	// return Collections.unmodifiableList(new ArrayList<ObjectGuid>(this.removeAttachMap.values()));
	// }

	// /**
	// * 获取截止时间Entry集合
	// *
	// * @return
	// */
	// public Set<Entry<String, Date>> getDeadlineEntries()
	// {
	// return this.deadlineMap.entrySet();
	// }

	/**
	 * 设定活动截止时间
	 * 
	 * @param actName
	 * @param deadline
	 */
	public void setDeadline(String actName, Date deadline)
	{
		this.deadlineMap.put(actName, deadline);
	}

	public void setDeadline(Map<String, Date> deadlineMap)
	{
		this.deadlineMap.putAll(deadlineMap);
	}

	//
	public Date getDeadline(String actName)
	{
		return this.deadlineMap.get(actName);
	}

	/**
	 * 获取指定活动执行人的集合
	 * 
	 * @param actName
	 *            活动名称
	 * @return
	 */
	public Map<String, Performer> getPerformerViewMap(String actName)
	{
		return Collections.unmodifiableMap(this.getPerformerMap(actName));
	}

	/**
	 * 添加执行人到指定活动
	 * 
	 * @param actNameGuid
	 *            创建阶段：活动名称
	 *            重启阶段：活动guid
	 * @param perfGuid
	 *            执行人guid
	 * @param perfType
	 *            执行人类型
	 */
	public void addPerformer(String actNameGuid, String perfGuid, Performer perfType)
	{
		this.getPerformerMap(actNameGuid).put(perfGuid, perfType);
	}

	/**
	 * 删除指定活动的执行人
	 * 
	 * @param actName
	 *            活动名称
	 * @param perfGuid
	 *            执行人guid
	 */
	public void removePerformer(String actName, String perfGuid)
	{
		this.getPerformerMap(actName).remove(perfGuid);
	}

	/**
	 * 删除活动的所有执行人
	 * 
	 * @param actName
	 *            活动名称
	 */
	public void removePerformer(String actName)
	{
		this.actPerformerMap.remove(actName);
	}

	/**
	 * 获取需要删除活动执行人的集合
	 * 
	 * @return
	 */
	public Set<Entry<String, String>> getRemovePerformerEntries()
	{
		return this.removePerformerMap.entrySet();
	}

	/**
	 * 获取需要添加活动执行人的集合
	 * 
	 * @return
	 */
	public Set<Entry<String, Map<String, Performer>>> getAddPerformerEntries()
	{
		return this.actPerformerMap.entrySet();
	}

	/**
	 * 从运行时活动中删除执行人
	 * 
	 * @param actRtGuid
	 * @param perfGuid
	 */
	public void removePerformerFromAcitityRumtime(String actRtGuid, String perfGuid)
	{
		this.removePerformerMap.put(actRtGuid, perfGuid);
	}

	/**
	 * 从运行时流程中删除附件
	 * 
	 * @param objectGuid
	 */
	// public void removeAttachFromProcessRuntime(ObjectGuid objectGuid)
	// {
	// this.removeAttachMap.put(objectGuid.getGuid(), objectGuid);
	// }

	/**
	 * 清空deadlineMap actPerformerMap removePerformerMap
	 */
	public void clearDeadlinePerformer()
	{
		this.deadlineMap.clear();
		this.actPerformerMap.clear();
		this.removePerformerMap.clear();
	}

	/**
	 * @return the deadlineMap
	 */
	public Map<String, Date> getDeadlineMap()
	{
		return this.deadlineMap;
	}

	/**
	 * @return the bomTempNames
	 */
	// public Set<String> getBomTempNames(String instanceGuid)
	// {
	// return this.bomTempNameMap.get(instanceGuid);
	// }
	//
	// /**
	// * @return the relationTempNames
	// */
	// public Set<String> getRelationTempNames(String instanceGuid)
	// {
	// return this.relationTempNameMap.get(instanceGuid);
	// }
	//
	// public void setBOMTempMap(Map<String, Set<String>> bomTempNameMap)
	// {
	// this.bomTempNameMap = bomTempNameMap;
	// }
	//
	// public void setRelaTempMap(Map<String, Set<String>> relaTempNameMap)
	// {
	// this.relationTempNameMap = relaTempNameMap;
	// }
	//
	// /**
	// * @return the bomTempNameMap
	// */
	// public Map<String, Set<String>> getBomTempNameMap()
	// {
	// return this.bomTempNameMap;
	// }
	//
	// /**
	// * @return the relationTempNameMap
	// */
	// public Map<String, Set<String>> getRelationTempNameMap()
	// {
	// return this.relationTempNameMap;
	// }

	/**
	 * @return the parentProcGuid
	 */
	// public String getParentProcGuid()
	// {
	// return this.parentProcGuid;
	// }
	//
	// /**
	// * @return the parentActGuid
	// */
	// public String getParentActGuid()
	// {
	// return this.parentActGuid;
	// }
	//
	// /**
	// * @param parentProcGuid
	// * the parentProcGuid to set
	// */
	// public void setParentProcGuid(String parentProcGuid)
	// {
	// this.parentProcGuid = parentProcGuid;
	// }
	//
	// /**
	// * @param parentActGuid
	// * the parentActGuid to set
	// */
	// public void setParentActGuid(String parentActGuid)
	// {
	// this.parentActGuid = parentActGuid;
	// }

	/**
	 * @return the bomViewName
	 */
	// public String getBomViewName()
	// {
	// return this.bomViewName;
	// }
	//
	// /**
	// * @param bomViewName
	// * the bomViewName to set
	// */
	// public void setBomViewName(String bomViewName)
	// {
	// this.bomViewName = bomViewName;
	// }
	//
	// /**
	// * @return the bomObjectGuid
	// */
	// public ObjectGuid getBomObjectGuid()
	// {
	// return this.bomObjectGuid;
	// }
	//
	// /**
	// * @param bomObjectGuid
	// * the bomObjectGuid to set
	// */
	// public void setBomObjectGuid(ObjectGuid bomObjectGuid)
	// {
	// this.bomObjectGuid = bomObjectGuid;
	// }
	//
	// /**
	// * @return the isAllBOM
	// */
	// public boolean isAllBOM()
	// {
	// return this.isAllBOM;
	// }
	//
	// /**
	// * @param isAllBOM
	// * the isAllBOM to set
	// */
	// public void setAllBOM(boolean isAllBOM)
	// {
	// this.isAllBOM = isAllBOM;
	// }
	//
	// /**
	// * @return the allLevel
	// */
	// public boolean isAllLevel()
	// {
	// return this.allLevel;
	// }
	//
	// /**
	// * @param allLevel
	// * the allLevel to set
	// */
	// public void setAllLevel(boolean allLevel)
	// {
	// this.allLevel = allLevel;
	// }
	//
	// /**
	// * @return the end2Precise
	// */
	// public boolean isEnd2Precise()
	// {
	// return this.end2Precise;
	// }
	//
	// /**
	// * @param end2Precise
	// * the end2Precise to set
	// */
	// public void setEnd2Precise(boolean end2Precise)
	// {
	// this.end2Precise = end2Precise;
	// }
	//
	// /**
	// * @return the end2NonPrecise
	// */
	// public boolean isEnd2NonPrecise()
	// {
	// return this.end2NonPrecise;
	// }
	//
	// /**
	// * @param end2NonPrecise
	// * the end2NonPrecise to set
	// */
	// public void setEnd2NonPrecise(boolean end2NonPrecise)
	// {
	// this.end2NonPrecise = end2NonPrecise;
	// }

}
