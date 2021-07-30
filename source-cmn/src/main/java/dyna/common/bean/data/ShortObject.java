/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AssistantObject DynaObject的辅助类
 * Wanglei 2010-9-1
 */
package dyna.common.bean.data;

import dyna.common.systemenum.LocationTypeEnum;
import dyna.common.systemenum.SystemStatusEnum;

/**
 * DynaObject的辅助类, 将一些公共字段放置于此类中 <br>
 * FoundationObject的简装类，主要用于映射MyFolder中的对象及checkOut等对应的相关对象
 * 
 * @author Wanglei
 * 
 */
public interface ShortObject extends DynaObject, SystemObject
{
	public static final String	BO_ICON			= "ICON$";
	public static final String	FILE_ICON16		= "FILEICON16$";
	public static final String	FILE_ICON32		= "FILEICON32$";
	public static final String	IS_SHORTCUT_KEY	= "ISSHORTCUT";

	/**
	 * 备选编号
	 * 
	 * @return
	 */
	public String getAlterId();

	/**
	 * 检出者
	 * 
	 * @return "编号-名称"
	 */
	public String getCheckedOutUser();

	/**
	 * 检出者
	 * 
	 * @return GUID
	 */
	public String getCheckedOutUserGuid();

	/**
	 * 分类(多语言)
	 * 
	 * @return 多语言";;"
	 */
	public String getClassification();

	/**
	 * 分类ITEM名
	 * 
	 * @return
	 */
	public String getClassificationName();

	/**
	 * 分类
	 * 
	 * @return GUID
	 */
	public String getClassificationGuid();

	/**
	 * 对象全称
	 * 
	 * @return "编号/版本.版序-名称"
	 */
	public String getFullName();

	/**
	 * 显示的图标
	 * 
	 * @return
	 */
	public String getIcon();

	/**
	 * 编号
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 版序
	 * 
	 * @return
	 */
	public Integer getIterationId();

	/**
	 * 生命周期阶段(多语言)
	 * 
	 * @return 多语言";;"
	 */
	public String getLifecyclePhase();

	/**
	 * 生命周期阶段
	 * 
	 * @return GUID
	 */
	public String getLifecyclePhaseGuid();

	/**
	 * 对象所存位置
	 * 
	 * @return
	 */
	public String getLocation();

	/**
	 * 位置类型(私有/公有)
	 * 
	 * @return
	 */
	public LocationTypeEnum getLocationType();

	/**
	 * 名称
	 */
	@Override
	public String getName();

	/**
	 * 所有者
	 * 
	 * @return "编号-名称"
	 */
	public String getOwnerUser();

	/**
	 * 所有者
	 * 
	 * @return GUID
	 */
	public String getOwnerUserGuid();

	/**
	 * 版本
	 * 
	 * @return
	 */
	public String getRevisionId();

	/**
	 * 状态ID枚举
	 * 
	 * @return
	 */
	public SystemStatusEnum getStatus();

	/**
	 * 判断是否有文件附件
	 * 
	 * @return
	 */
	public boolean hasFile();

	/**
	 * 文件名
	 * 
	 * @return
	 */
	public String getFileName();

	/**
	 * 文件guid
	 * 
	 * @return
	 */
	public String getFileGuid();

	/**
	 * 文件类型
	 * 
	 * @return
	 */
	public String getFileType();

	/**
	 * 是否检出
	 * 
	 * @return
	 */
	public boolean isCheckOut();

	/**
	 * 是否废弃
	 * 
	 * @return
	 */
	public boolean isObsolete();

	/**
	 * 是否快捷方式
	 * 
	 * @return
	 */
	public boolean isShortcut();

	/**
	 * 设置编号
	 * 
	 * @param alterId
	 */
	public void setAlterId(String alterId);

	/**
	 * 设置分类名称
	 * 
	 * @param clsf
	 *            多语言(";;")
	 */
	public void setClassification(String clsfTitle);

	/**
	 * 
	 * @param clsf
	 */
	public void setClassificationName(String clsfName);

	/**
	 * 设置分类guid
	 * 
	 * @param classficationGuid
	 *            guid
	 */
	public void setClassificationGuid(String classficationGuid);

	/**
	 * 设置编号
	 * 
	 * @param id
	 */
	public void setId(String id);

	/**
	 * 设置生命周期阶段名称
	 * 
	 * @param phaseTitle
	 *            多语言(";;")
	 */
	public void setLifecyclePhase(String phaseTitle);

	/**
	 * 设置生命周期阶段 GUID
	 * 
	 * @param phaseGuid
	 *            guid
	 */
	public void setLifecyclePhaseGuid(String phaseGuid);

	/**
	 * 设置存储位置
	 * 
	 * @param location
	 */
	public void setLocation(String location);

	/**
	 * 设置存储位置类型
	 * 
	 * @param locationType
	 */
	public void setLocationType(LocationTypeEnum locationType);

	/**
	 * 设置对象名称
	 */
	@Override
	public void setName(String name);

	/**
	 * 设置所有者guid
	 * 
	 * @param ownerUserGuid
	 */
	public void setOwnerUserGuid(String ownerUserGuid);

	/**
	 * 设置版本号
	 * 
	 * @param revisionId
	 */
	public void setRevisionId(String revisionId);

	/**
	 * 设置状态
	 * 
	 * @param statusGuid
	 */
	public void setStatus(SystemStatusEnum systemStatusEnum);

	/**
	 * 设置是否快捷方式
	 * 
	 * @param isShortcut
	 */
	public void setShortcut(boolean isShortcut);

	/**
	 * 获取主文件16x16的图标
	 * 
	 * @return
	 */
	public String getFileIcon16();

	/**
	 * 获取主文件32x32的图标
	 * 
	 * @return
	 */
	public String getFileIcon32();

	/**
	 * 取得分类group标题
	 * 
	 * @return
	 */
	public String getClassificationGroupTitle();

	/**
	 * 取得分类group名
	 * 
	 * @return
	 */
	public String getClassificationGroupName();

	/**
	 * 取得分类group guid
	 * 
	 * @return
	 */
	public String getClassificationGroup();

	/**
	 * 取得分类group标题
	 * 
	 * @return
	 */
	public void setClassificationGroupTitle(String classificationGroupTitle);

	/**
	 * 取得分类group名
	 * 
	 * @return
	 */
	public void setClassificationGroupName(String classificationGroupName);

	/**
	 * 取得分类group guid
	 * 
	 * @return
	 */
	public void setClassificationGroup(String classificationGroupGuid);

	/**
	 * 设置唯一性字段
	 * 
	 * @param unique
	 */
	public void setUnique(String unique);

	/**
	 * 设置唯一性字段
	 * 
	 * @param unique
	 */
	public String getUnique();

	/**
	 * 设置重复性字段
	 * 
	 * @param repeat
	 */
	public void setRepeat(String unique);

	/**
	 * 取得重复性字段
	 * 
	 * @param repeat
	 */
	public String getRepeat();

	/**
	 * 设置MD5字段
	 * 
	 * @param repeat
	 */
	public void setMD5(String md5);

	/**
	 * 取得MD5字段
	 * 
	 * @param repeat
	 */
	public String getMD5();

}
