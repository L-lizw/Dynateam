/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FBTS
 * wangweixia 2012-9-6
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.FileOpenConfig;
import dyna.common.dto.FileOpenItem;
import dyna.common.dto.FileOpenSubject;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

/**
 * File Browser Tools Service 文件浏览工具服务
 * 
 * @author wangweixia
 * 
 */
public interface FBTS extends Service
{
	/**
	 * 批量保存文件打开类型设置：包括FileOpenConfig新增，更新，删除
	 * 
	 * @param addFileOpenConfigList
	 * @param updateFileOpenConfigList
	 * @param deleteFileOpenConfigList
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public void batchSaveFileOpenConfig(List<FileOpenConfig> addFileOpenConfigList, List<FileOpenConfig> updateFileOpenConfigList, List<FileOpenConfig> deleteFileOpenConfigList)
			throws ServiceRequestException;

	/**
	 * 获取所有的文件打开类型配置
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FileOpenConfig> listFileOpenConfig() throws ServiceRequestException;

	/**
	 * 根据Guid文件打开类型配置
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FileOpenConfig getFileOpenConfigByGuid(String guid) throws ServiceRequestException;

	/**
	 * FileOpenItem与FileOpenSubject的添加/更新的操作<br>
	 * 
	 * @param fileOpenSubject
	 *            添加/更新
	 * @param fileOpenItemList
	 *            添加/更新
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public void saveSubjectAndItem(FileOpenSubject fileOpenSubject, List<FileOpenItem> fileOpenItemList) throws ServiceRequestException;

	/**
	 * 删除权限配置
	 * 
	 * @param fileOpenSubject
	 */
	public void deleteFileOpenSubject(String subjectGuid) throws ServiceRequestException;

	/**
	 * 获取系统中第一层FileOpenSubject的列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FileOpenSubject> listRootFileOpenSubject() throws ServiceRequestException;

	/**
	 * 根据FileOpenSubject的guid查询下层FileOpenSubject的列表
	 * 
	 * @param FileOpenSubject
	 * @param isCascade
	 *            only false
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FileOpenSubject> listSubFileOpenSubject(String FileOpenSubjectGuid, boolean isCascade) throws ServiceRequestException;

	/**
	 * 根据FileOpenSubject的guid 查询FileOpenItem控制列表
	 * 
	 * @param aclSubjectGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FileOpenItem> listFileOpenItemBySubject(String FileOpenSubjectGuid) throws ServiceRequestException;

	/**
	 * 通过文件后缀获取可使用的文件类型配置
	 * 
	 * @param suffix
	 *            文件后缀
	 * @return FileOpenConfig
	 */
	public List<FileOpenConfig> listFileOpenConfigBySuffix(FoundationObject foundationObject, String suffix) throws ServiceRequestException;

	/**
	 * 通过选取的文件配置和后缀获取需要下载的相关文件
	 * 
	 * @param foundationObject
	 *            实例的foundationObject
	 * @param guid
	 *            所选的FileOpenConfig的guid
	 * @param DSSFileInfo
	 *            需下载的文件信息
	 * @return List
	 */
	public List<DSSFileTrans> listDSSFileTransByConfigGuid(FoundationObject foundationObject, String guid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 根据选取文件的Guid，取得其文件所有批注文件的下载信息
	 * 
	 * @param guid
	 *            file的guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileTrans> listBIFileByFileGuid(String guid) throws ServiceRequestException;

	/**
	 * 文件比较工具是否激活
	 * 
	 * @return boolean
	 */
	public boolean isFileCompareEnabled() throws ServiceRequestException;

	/**
	 * 获取文件比较工具配置
	 * 
	 * @return FileOpenConfig
	 */
	public FileOpenConfig getFileCompareConfig() throws ServiceRequestException;

	/**
	 * 通过选取的文件配置和文件获取WEB型浏览工具的浏览参数信息
	 * 
	 * @param foundationObject
	 *            实例的foundationObject
	 * @param guid
	 *            所选的FileOpenConfig的guid
	 * @param file
	 *            需下载的文件信息
	 * @return String
	 * @throws ServiceRequestException
	 */
	public String getWebFileViewInfo(FoundationObject foundationObject, String guid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 通过选取的文件配置和文件获取WEB型浏览工具的文件比较参数信息
	 * 
	 * @param guid
	 *            所选的FileOpenConfig的guid
	 * @param fileObjectGuid1
	 *            比较实例1的ObjectGuid
	 * @param file1
	 *            比较实例1的文件信息
	 * @param fileObjectGuid2
	 *            比较实例2的ObjectGuid
	 * @param file2
	 *            比较实例2的文件信息
	 * @return String
	 * @throws ServiceRequestException
	 */
	public String getWebFileCompareInfo(String guid, ObjectGuid fileObjectGuid1, DSSFileInfo file1, ObjectGuid fileObjectGuid2, DSSFileInfo file2) throws ServiceRequestException;

}
