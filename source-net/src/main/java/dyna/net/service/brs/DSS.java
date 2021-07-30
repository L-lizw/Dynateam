/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DSS Distributed Storage Service
 * Wanglei 2010-8-31
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.serv.DSStorage;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.FileType;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AuthorityEnum;
import dyna.net.service.Service;

/**
 * Distributed Storage Service
 * 
 * @author Wanglei
 * 
 */
public interface DSS extends Service
{

	/**
	 * 设置主文件
	 * 
	 * @param fileGuid
	 * @throws ServiceRequestException
	 */
	public void setAsPrimaryFile(String fileGuid) throws ServiceRequestException;

	/**
	 * 获取文件对象信息
	 * 
	 * @param fileGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo getFile(String fileGuid) throws ServiceRequestException;

	/**
	 * 根据文件传输master guid 查找文件传输列表
	 * 
	 * @param fileTransMasterGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileTrans> listFileTransDetail(String fileTransMasterGuid) throws ServiceRequestException;

	/**
	 * 查找传输文件列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileTrans> listFileTrans(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 将文件srcFile复制到destFile
	 * 
	 * @param destFileGuid
	 *            目标文件
	 * @param srcFileGuid
	 *            源文件
	 * @throws ServiceRequestException
	 */
	public void copyFile(String destFileGuid, String srcFileGuid) throws ServiceRequestException;

	/**
	 * 将源文件srcFileGuids复制到实例对象destObjectGuid
	 * 
	 * @param destObjectGuid
	 *            目标实例对象
	 * @param srcFileGuids
	 *            源文件(s)
	 * @throws ServiceRequestException
	 */
	public void copyFile(ObjectGuid destObjectGuid, String... srcFileGuids) throws ServiceRequestException;

	/**
	 * 将实例对象srcObjectGuid下的文件复制到实例对象destObjectGuid
	 * 
	 * @param destObjectGuid
	 *            目标对象
	 * @param srcObjectGuid
	 *            源对象
	 * @throws ServiceRequestException
	 */
	public void copyFile(ObjectGuid destObjectGuid, ObjectGuid srcObjectGuid) throws ServiceRequestException;

	/**
	 * 根据用户id获取文件传输对象
	 * 
	 * @param userId
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans getDSServerUser(String userId) throws ServiceRequestException;

	/**
	 * 获取文件传输的对象
	 * 
	 * @param fileTransGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans getFileTrans(String fileTransGuid) throws ServiceRequestException;

	/**
	 * 查询业务对象(最新版序的)文件列表
	 * 
	 * @param objectGuid
	 * @param searchCondition
	 *            查询条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, SearchCondition searchCondition)
			throws ServiceRequestException;

	/**
	 * 查询业务对象(指定版序的)文件列表
	 * 
	 * @param objectGuid
	 * @param iterationId
	 *            对象的版序
	 * @param searchCondition
	 *            查询条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, int iterationId, SearchCondition searchCondition)
			throws ServiceRequestException;
	/**
	 * 查询业务对象(指定版序的)文件列表，根据文件类型
	 * 
	 * @param objectGuid
	 * @param iterationId
	 *            对象的版序
	 * @param searchCondition
	 *            查询条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listFileByFileType(ObjectGuid objectGuid, int iterationId, SearchCondition searchCondition)
			throws ServiceRequestException;

	/**
	 * 查询业务对象(指定版序的)文件列表, 用于查看流程中指定意见的文件列表
	 * 
	 * @param procGuid
	 *            流程的guid
	 * @param actrtGuid
	 *            活动节点gud
	 * @param startNumber
	 *            开始层次
	 * @param createUserGuid
	 *            上传者
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listProcessFile(String procRtGuid, String actrtGuid, int startNumber, String createUserGuid)
			throws ServiceRequestException;

	/**
	 * 查询业务对象(指定版序的)文件列表, 用于查看流程附件中的文件列表
	 * 
	 * @param objectGuid
	 *            业务对象的guid
	 * @param procGuid
	 *            流程的guid
	 * @param iterationId
	 *            对象的版序
	 * @param searchCondition
	 *            查询条件
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, String procGuid, int iterationId,
			SearchCondition searchCondition) throws ServiceRequestException;

	// /**
	// * 查询流程文件
	// *
	// * @param procRtGuid
	// * 流程Guid
	// * @return
	// * @throws ServiceRequestException
	// */
	// public List<DSSFileInfo> listFile(String procRtGuid) throws ServiceRequestException;

	/**
	 * 查询数据库表记录关联的所有文件信息
	 * 
	 * @param tabName
	 *            数据库表名
	 * @param fkGuid
	 *            记录guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileInfo> listFile(String tabName, String fkGuid) throws ServiceRequestException;

	/**
	 * 下载文件
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @return 文件传输对象
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadFile(String fileGuid) throws ServiceRequestException;

	/**
	 * 下载文件
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @param authorityEnum
	 *            需要判断的权限，只有 DOWNLOADFILE，VIEWFILE 两个值可以，不能传其他值
	 *            管理员不判断权限
	 * @return 文件传输对象
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadFile(String fileGuid, AuthorityEnum authorityEnum) throws ServiceRequestException;

	// /**
	// * 下载文件：无权限判断
	// *
	// * @param fileGuid
	// * @return
	// * @throws ServiceRequestException
	// */
	// public DSSFileTrans downloadFileNoAuthority(String fileGuid) throws ServiceRequestException;

	/**
	 * 下载文件(文件对应的实例在流程中)
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @param procGuid
	 *            流程实例guid
	 * @return 文件传输对象
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadFile(String fileGuid, String procGuid) throws ServiceRequestException;

	/**
	 * 批量下载文件
	 * 不判断下载权限
	 * 
	 * @param fileGuidList
	 *            文件guid列表
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans batchDownloadFile(List<String> fileGuidList) throws ServiceRequestException;
	
	/**
	 * 批量下载文件  多文件服务器
	 * 不判断下载权限
	 * 
	 * @param fileGuidList
	 *            文件guid列表
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSSFileTrans> batchlisDownloadFile(List<String> fileGuidList) throws ServiceRequestException;

	/**
	 * 上传文件
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @param filePath
	 *            物理文件的本地路径
	 * @return 文件传输对象
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans uploadFile(String fileGuid, String filePath) throws ServiceRequestException;

	/**
	 * 批量上传文件
	 * 
	 * @param fileGuid
	 * @param filePathList
	 *            物理文件的本地路径列表
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans batchUploadFile(List<String> fileGuid, List<String> filePathList)
			throws ServiceRequestException;

	/**
	 * 完成文件上传<br>
	 * 此方法由系统内部的文件传输模块自动调用, 任何个人均无权限调用.
	 * 
	 * @param fileTransGuid
	 *            文件guid
	 * @throws ServiceRequestException
	 */
	public void fileUploaded(String fileTransGuid, long size, String md5) throws ServiceRequestException;

	/**
	 * 完成文件删除<br>
	 * 此方法由系统内部的文件传输模块自动调用, 任何个人均无权限调用.
	 * 
	 * @param fileTransGuid
	 * @throws ServiceRequestException
	 */
	public void fileDeleted(String fileTransGuid) throws ServiceRequestException;

	/**
	 * 添加文件附加到业务对象
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo attachFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 添加文件附加到流程上
	 * 
	 * @param procGuid
	 *            流程guid
	 * @param actGuid
	 *            活动guid
	 * @param startNumber
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo attachFile(String procGuid, String actGuid, int startNumber, DSSFileInfo file)
			throws ServiceRequestException;

	/**
	 * 添加文件到系统内任意数据库表中的记录关联
	 * 
	 * @param tabName
	 *            数据库表名
	 * @param fkGuid
	 *            该数据库表记录的主键(GUID)
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileInfo attachFile4Tab(String tabName, String fkGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 删除文件
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @throws ServiceRequestException
	 */
	public void detachFile(String fileGuid) throws ServiceRequestException;

	/**
	 * 删除数据库表记录关联的文件
	 * 
	 * @param fileGuid
	 * @throws ServiceRequestException
	 */
	public void detachFile4Tab(String fileGuid) throws ServiceRequestException;

	/**
	 * 删除数据库表记录关联的所有文件
	 * 
	 * @param tabName
	 *            数据库表名
	 * @param fkGuid
	 *            该数据库表记录的主键(GUID)
	 * @throws ServiceRequestException
	 */
	public void detachFile4Tab(String tabName, String fkGuid) throws ServiceRequestException;

	/**
	 * 
	 * @param groupGuid
	 * @param groupId
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSStorage getStorageForGroup(String groupGuid, String groupId) throws ServiceRequestException;

	/**
	 * 获取存储空间
	 * 
	 * @param storageId
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSStorage getStorage(String storageId) throws ServiceRequestException;

	/**
	 * 查询存储空间列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<DSStorage> listStorage() throws ServiceRequestException;

	/**
	 * 根据文件后缀名获取文件类型guid
	 * 
	 * @param fileTypeGuidOrIdOrExt
	 *            文件类型guid或者id或者后缀名
	 * @return 文件类型guid
	 * @throws ServiceRequestException
	 */
	public FileType getFileType(String fileTypeGuidOrIdOrExt) throws ServiceRequestException;

	/**
	 * 修改文件为相应的类型
	 * 
	 * @param fileGuid
	 *            文件guid
	 * @param fileTypeGuidOrIdOrExt
	 *            文件类型guid或者id或者后缀名
	 * @throws ServiceRequestException
	 */
	public void setAsFileType(String fileGuid, String fileTypeGuidOrIdOrExt) throws ServiceRequestException;

	/**
	 * 查询指定后缀名的文件在系统中所注册的文件类型
	 * 
	 * @param extension
	 *            文件后缀名
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FileType> listFileTypeByExtension(String extension) throws ServiceRequestException;

	/**
	 * 查询在系统中所有注册的文件类型
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<FileType> listFileType() throws ServiceRequestException;

	/**
	 * 下载对象预览类型的文件
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param iterationId
	 *            实例版序
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException;

	/**
	 * 上传对象预览类型文件, 目前仅支持图片格式的文件(.gif, .jpg, .jpeg, .bmp, .png)
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans uploadPreviewFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 上传对象缩略图文件, 目前仅支持图片格式的文件(.gif, .jpg, .jpeg, .bmp, .png)
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param file
	 *            文件信息
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans uploadIconFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException;

	/**
	 * 下载对象缩略图文件
	 * 
	 * @param objectGuid
	 *            对象guid
	 * @param iterationId
	 *            实例版序
	 * @return
	 * @throws ServiceRequestException
	 */
	public DSSFileTrans downloadIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException;

	/**
	 * 删除图标文件
	 * 
	 * @param objectGuid
	 * @param iterationId
	 * @throws ServiceRequestException
	 */
	public void detachIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException;

	/**
	 * 删除预览文件
	 * 
	 * @param objectGuid
	 * @param iterationId
	 * @throws ServiceRequestException
	 */
	public void detachPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException;

	/**
	 * 删除文件,删除任务单号中给定名字的所有未上传文件的文件对象
	 * CAD需求
	 * 
	 * @param fileTransGuid
	 *            任务单号
	 * @param fileNameList
	 *            要删除的文件名列表
	 * @throws ServiceRequestException
	 */
	public void detachFile(String fileTransGuid, List<String> fileNameList) throws ServiceRequestException;

	/**
	 * 是否存在缩略图
	 * 
	 * @param objectGuid
	 * @param iterationId
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException;

	/**
	 * 是否存在预览图
	 * 
	 * @param objectGuid
	 * @param iterationId
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException;

	/**
	 * 更新下载客户端文件保存的路径
	 * 
	 * @param fileGuid
	 * @param fileClientPath
	 * @throws ServiceRequestException
	 */
	public void setLocalFilePath(String fileGuid, String fileClientPath) throws ServiceRequestException;

	/**
	 * 检验所有的文件是否上传成功
	 * 
	 * @param transFileGuid
	 * @return
	 */
	public boolean checkUploadFiles(String transFileGuid) throws ServiceRequestException;

	/**
	 * 检查并删除无效文件信息
	 * 通过任务列表检查bi_file中的文件是否上传成功，如没上传，则删除此文件信息
	 * 
	 * @param transFileGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<String> checkAndDeleteFiles(String transFileGuid, boolean isDelete) throws ServiceRequestException;

	/**
	 * 是否有附件的下载权限，若无权限，则抛出异常
	 * 
	 * @param fileGuid
	 * @throws ServiceRequestException
	 */
	public void hasDownLoadFileAuthority(String fileGuid) throws ServiceRequestException;

}
