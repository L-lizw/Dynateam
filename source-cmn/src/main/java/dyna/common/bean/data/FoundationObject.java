/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoundationObject
 * xiasheng May 7, 2010
 */
package dyna.common.bean.data;

import java.util.Date;
import java.util.List;

import dyna.common.bean.extra.PromptMessage;

/**
 * @author xiasheng
 * 
 */
public interface FoundationObject extends ShortObject, InputObject
{

	public static final String	ALLOCATE_ID				= "ALLOCATEID";
	public static final String	MESSAGE					= "MESSAGE";
	public static final String	FOUNDATION_CLASSNAME	= "FoundationObject";
	public static final String	DYNAOBJECT_CLASSNAME	= "DynaObject";

	public abstract Date getCheckedOutTime();

	public abstract Date getObsoleteTime();

	public abstract String getOwnerGroup();

	public abstract String getOwnerGroupGuid();

	public abstract PromptMessage getPromptMessage();

	public abstract Date getReleaseTime();

	public abstract void setOwnerGroupGuid(String ownerGroupGuid);

	public abstract void setPromptMessage(PromptMessage promptMessage);

	/**
	 * 私有文件夹的GUID
	 * 
	 * @return
	 */
	public String getPrivateFolderGuid();

	/**
	 * 私有文件夹的GUID
	 * 
	 * @param privateFolderGuid
	 */
	public void setPrivateFolderGuid(String privateFolderGuid);

	/**
	 * 私有文件夹的path
	 * 
	 * @return
	 */
	public String getPrivateFolderPath();

	/**
	 * 私有文件夹的path
	 * 
	 * @param privateFolderPath
	 */
	public void setPrivateFolderPath(String privateFolderPath);

	/**
	 * 检出路径
	 * 
	 * @return
	 */
	public String getCheckOutPath();

	/**
	 * 检出路径
	 * 
	 * @param checkoutpath
	 */
	public void setCheckOutPath(String checkoutpath);

	/**
	 * 库
	 * 
	 * @return
	 */
	public String getLocationlib();

	/**
	 * 库
	 * 
	 * @param checkoutpath
	 */
	public void setLocationlib(String locationlib);

	/**
	 * 库名称
	 * 
	 * @return
	 */
	public String getLocationlibName();

	/**
	 * 库名称
	 * 
	 * @param checkoutpath
	 */
	public void setLocationlibName(String locationlibName);

	/**
	 * 废弃者
	 * 
	 * @return
	 */
	public String getObsoleteUser();

	/**
	 * 废弃者GUID
	 * 
	 * @return
	 */
	public String getObsoleteUserGuid();

	/**
	 * 废弃者GUID
	 * 
	 * @param obsoleteUserGuid
	 */
	public void setObsoleteUserGuid(String obsoleteUserGuid);

	/**
	 * 是否导入ERP
	 * 
	 * @return
	 */
	public boolean isExportToERP();

	/**
	 * 导入ERP设置
	 * 
	 * @param isExportToERP
	 */
	public void setExportToERP(boolean isExportToERP);

	// /**
	// * 是否是master脚本
	// *
	// * @return true:是master在执行脚本；false:不是master在执行脚本
	// */
	// public boolean isMasterScript();
	//
	// /**
	// * 设置是否master脚本
	// *
	// * @param isMasterScript
	// */
	// public void setMasterScript(boolean isMasterScript);

	/**
	 * 所在库的GUID
	 * 
	 * @return
	 */
	public String getCommitFolderGuid();

	/**
	 * 所在库的GUID
	 * 
	 * @param commitFolderGuid
	 */
	public void setCommitFolderGuid(String commitFolderGuid);

	/**
	 * 库路径GUID
	 * 
	 * @return
	 */
	public String getCommitPath();

	/**
	 * 库路径GUID
	 * 
	 * @param commitPath
	 */
	public void setCommitPath(String commitPath);

	/**
	 * 库UserGUID
	 * 
	 * @return
	 */
	public String getLocationUser();

	/**
	 * 库UserGUID
	 * 
	 * @param locationUser
	 */
	public void setLocationUser(String locationUser);

	/**
	 * ECFLAG
	 * 
	 * @return
	 */
	public ObjectGuid getECFlag();

	/**
	 * ECFLAG
	 * 
	 * @param ecFlag
	 */
	public void setECFlag(ObjectGuid ecFlag);

	/**
	 * 是否已经入库
	 * 
	 * @return
	 */
	public boolean isCommited();

	/**
	 * 设置是否需要流程确认生效
	 * 
	 * @param isNeedFlowConfirmEffective
	 */
	public void setNeedFlowConfirmEffective(boolean isNeedFlowConfirmEffective);

	/**
	 * 是否需要流程确认生效
	 * 
	 * @return
	 */
	public boolean isNeedFlowConfirmEffective();

	/**
	 * 设置通用件
	 * 
	 * @param configInstanceItem
	 */
	public void setGenericItem(ObjectGuid genericItem);

	/**
	 * 取得通用件
	 */
	public ObjectGuid getGenericItem();

	/**
	 * 设置族表实例
	 * 
	 * @param configInstance
	 */
	public void setConfigInstance(String configInstance);

	/**
	 * 取得族表实例名称
	 * 
	 * @return
	 */
	public String getConfigInstance();

	/**
	 * 当前BO对象作为交付的对象时，是否为已公开状态,ProjectReference
	 * 
	 * @return
	 */
	public ObjectGuid getProjectReference();

	/**
	 * 当前BO对象作为交付的对象时，是否为已公开状态,ProjectReference
	 * 
	 * @return
	 */
	public void setProjectReference(ObjectGuid projectReference);

	/**
	 * 是否为最新版本,不区分发布与工作版
	 * 根据LATESTREVISION$字段判断是否为最新版
	 * 字符意义：
	 * "m" 为最新版
	 * "w" 为最新工作版
	 * "r" 为最新发布版
	 */
	public boolean isLatestRevision();

	/**
	 * 把分布于实例中的 classification 字段以classificationname + "." + fieldName的形式取得classification信息
	 * 返回classification foundation,并删除分布于实例中的分类字段
	 * 
	 * @param classificationName
	 * @return
	 */
	public FoundationObject restoreClasssification(String classificationName);

	/**
	 * 清除实例foundation中的所有classification信息
	 * 
	 * @param classificationName
	 */
	public void clearClasssification(String classificationName);

	/**
	 * 把foundation classificationmap中的所有信息混合分布到foundation中
	 */
	public void mixAllClassification();

	/**
	 * 取得所有分布于foundation中的 classification信息
	 */
	public List<FoundationObject> restoreAllClassification(boolean isDecompose);

	/**
	 * classification信息添加到实例中
	 * 
	 * @param classificationFoundation
	 * @param isMix
	 *            true:classification字段分布于实例中
	 *            false:classificationFoundation 存放去classificationmap中
	 */
	public void addClassification(FoundationObject classificationFoundation, boolean isMix);

}