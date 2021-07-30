/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Enterprise Directory Access Protocol 企业级目录访问协议服务
 * Wanglei 2010-8-13
 */
package dyna.net.service.brs;

import dyna.common.dto.Folder;
import dyna.common.dto.acl.SaAclFolderLibConf;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

import java.util.List;

/**
 * Enterprise Directory Access Protocol 企业级目录访问协议服务
 * 
 * @author Wanglei
 * 
 */
public interface EDAP extends Service
{

	/**
	 * 删除指定文件夹<br>
	 * 
	 * 判断登录用户所在的组的角色有没有删除权限<br>
	 * 
	 * 如果没有权限则异常信息提示不能 删除该文件夹<br>
	 * 
	 * 判断还有没有对象在该文件夹或者下层文件夹中<br>
	 * 
	 * 如果有则异常信息提示不能 删除该文件夹
	 * 
	 * @param folderGuid
	 *            要删除的文件夹的guid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及文件夹删除失败异常(ID_APP_FOLDER_DELETE_FAIL)
	 */
	public void deleteFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 根据folder guid 获取folder信息<br>
	 * 
	 * 包含私有Folder,library及library_folder等三种类型的Folder<br>
	 * 
	 * 其中对library及library_folder类型的Folder会判断登录用户所在的组的角色的查看权限
	 * 
	 * @param folderGuid
	 * @return Folder对象
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Folder getFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 获取登录用户个人文件夹的根目录
	 * 
	 * @return Folder
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Folder getRootMyFolder() throws ServiceRequestException;

	/**
	 * 获取指定用户组对应的工作库的根文件夹
	 * 
	 * @return Folder
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Folder getDefaultRootFolderByUserGroup(String groupGuid) throws ServiceRequestException;

	/**
	 * 获取指定用户的个人文件夹的根目录<br>
	 * 
	 * 参数中的userGuid和userId二者传其一即可，如果二者都传值已userGuid为准
	 * 
	 * @param userGuid
	 *            要获取根目录的用户Guid
	 * @param userId
	 *            要获取根目录的用户ID
	 * @return Folder对象
	 * @throws ServiceRequestException
	 */
	public Folder getRootPrivateFolderByUser(String userGuid, String userId) throws ServiceRequestException;

	/**
	 * 根据文件夹Guid获取对应的"是否只保存发布版信息的配置"
	 * 
	 * @param folderGuid
	 * @return SaAclFolderLibConf
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public SaAclFolderLibConf getSaAclFolderLibConf(String folderGuid) throws ServiceRequestException;

	/**
	 * 获取所有库文件集，不包含库root<br>
	 * 
	 * @return Folder列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Folder> listLibrary() throws ServiceRequestException;

	/**
	 * 查询下一层子文件夹,只查询一层<br>
	 * 判断登录用户所在的组的角色的查看权限<br>
	 * 参数folderGuid可以是私有文件夹的guid也可以是library的guid也可以是lib_folder的guid
	 * 
	 * @param folderGuid
	 * @return Folder列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Folder> listSubFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 查询下一层子文件夹,只查询一层<br>
	 * 不判断权限<br>
	 * 参数folderGuid可以是私有文件夹的guid也可以是library的guid也可以是lib_folder的guid
	 * 
	 * @param folderGuid
	 * @return Folder列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Folder> listSubFolderNoCheckACL(String folderGuid) throws ServiceRequestException;

	/**
	 * 新建/更新文件夹<br>
	 * 
	 * 新建时会判断登录用户所在的组的角色对于父文件夹的创建权限<br>
	 * 更新时会判断登录用户所在的组的角色对于当前文件夹的重命名权限
	 * 
	 * @param folder
	 *            要创建或者更新的文件夹对象
	 * @return 新的文件夹
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Folder saveFolder(Folder folder) throws ServiceRequestException;

	/**
	 * 保存"是否只保存发布版信息的配置",包括创建和更新
	 * 
	 * @param saAclFolderLibConf
	 * @return 保存后的SaAclFolderLibConf
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public SaAclFolderLibConf saveSaAclFolderLibConf(SaAclFolderLibConf saAclFolderLibConf)
			throws ServiceRequestException;

	/**
	 * 根据登录用户所在的组获取所有关联的库列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Folder> listLibraryByUserGroup() throws ServiceRequestException;

	/**
	 * 挂载卸载指定库的方法
	 * 
	 * @param libraryGuid
	 * @param isValid
	 * 
	 * @return Folder
	 * 
	 * @throws ServiceRequestException
	 */
	public Folder setLibraryValid(String libraryGuid, boolean isValid) throws ServiceRequestException;

	/**
	 * 根据登录用户所在组 获取该用户对应的默认库
	 * 
	 * @return 库
	 * @throws ServiceRequestException
	 */
	public Folder getDefaultLibraryByUserGroup() throws ServiceRequestException;

	/**
	 * 获取指定库内主记录数
	 * 
	 * @param libraryGuid
	 * @return 主记录数
	 * @throws ServiceRequestException
	 */
	public Long getMainDataNumberByLib(String libraryGuid) throws ServiceRequestException;

	/**
	 * 获取指定库内修订版本记录数
	 * 
	 * @param libraryGuid
	 * @return 修订版本记录数
	 * @throws ServiceRequestException
	 */
	public Long getVersionDataNumberByLib(String libraryGuid) throws ServiceRequestException;

	/**
	 * 取得当前用户所有有权限的库的根文件夹
	 * 
	 * @return 根文件夹
	 * @throws ServiceRequestException
	 */
	public List<Folder> listAllRootFolderHasAcl() throws ServiceRequestException;

}
