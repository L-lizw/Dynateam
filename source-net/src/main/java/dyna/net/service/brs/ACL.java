/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACL 权限访问列表服务
 * Wanglei 2010-7-30
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.acl.ACLFunctionItem;
import dyna.common.dto.acl.ACLFunctionObject;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.acl.PublicSearchACLItem;
import dyna.common.dto.acl.ShareFolderACLItem;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.ModulEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.net.service.Service;

/**
 * Access Control List Service 权限访问列表服务<br>
 * <strong>注意: 此服务用于系统的管理与维护, 因此如无特别说明, 服务中提供的所有方法, 均需要管理组中的用户才能访问!</strong>
 * 
 * @author Wanglei
 * 
 */
public interface ACL extends Service
{

	/**
	 * 批量处理ACL的操作，包括ACLItem与ACLSubject的添加/更新/删除等的操作<br>
	 * 
	 * 执行顺序如下：<br>
	 * <ol>
	 * <li>删除ACLItem</li>
	 * <li>更新ACLItem</li>
	 * <li>删除ACLSubject</li>
	 * <li>新增ACLSubject</li>
	 * <li>重置ACLItem对应的subjectGuid</li>
	 * <li>新增ACLItem</li>
	 * <li>更新ACLItem</li>
	 * <li>更新ACLSubject</li>
	 * </ol>
	 * 
	 * @param addAclItemList
	 * @param addAclSubjectList
	 * @param updateAclItemList
	 * @param updateAclSubjectList
	 * @param deleteAclItemList
	 * @param deleteAclSubjectList
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public void batchDealWithACL(List<ACLItem> addAclItemList, List<ACLSubject> addAclSubjectList, List<ACLItem> updateAclItemList, List<ACLSubject> updateAclSubjectList,
			List<ACLItem> deleteAclItemList, List<ACLSubject> deleteAclSubjectList) throws ServiceRequestException;
	
	/**
	 * 批量处理ACL的操作，包括ACLFunctionItem与ACLFunctionObject的添加/更新/删除等的操作<br>
	 * @param addAclItemList
	 * @param addAclSubjectList
	 * @param updateAclItemList
	 * @param updateAclSubjectList
	 * @param deleteAclItemList
	 * @param deleteAclSubjectList
	 * @throws ServiceRequestException
	 */
	public void batchDealWithFunctionACL(List<ACLFunctionItem> addAclItemList, List<ACLFunctionObject> addAclSubjectList, List<ACLFunctionItem> updateAclItemList,
			List<ACLFunctionObject> updateAclSubjectList, List<ACLFunctionItem> deleteAclItemList, List<ACLFunctionObject> deleteAclSubjectList) throws ServiceRequestException;

	/**
	 * 批量处理ACL的操作，包括ACLItem与ACLSubject的添加/更新/删除等的操作(该方法仅供库管理使用)
	 * 
	 * 执行顺序如下：<br>
	 * <ol>
	 * <li>删除ACLItem</li>
	 * <li>更新ACLItem</li>
	 * <li>删除ACLSubject</li>
	 * <li>新增ACLSubject</li>
	 * <li>重置ACLItem对应的subjectGuid</li>
	 * <li>新增ACLItem</li>
	 * <li>更新ACLItem</li>
	 * <li>更新ACLSubject</li>
	 * </ol>
	 * 
	 * @param addAclItemList
	 * @param addAclSubjectList
	 * @param updateAclItemList
	 * @param updateAclSubjectList
	 * @param deleteAclItemList
	 * @param deleteAclSubjectList
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public void batchDealWithACL4Lib(List<ACLItem> addAclItemList, List<ACLSubject> addAclSubjectList, List<ACLItem> updateAclItemList, List<ACLSubject> updateAclSubjectList,
			List<ACLItem> deleteAclItemList, List<ACLSubject> deleteAclSubjectList) throws ServiceRequestException;

	/**
	 * 根据folderGuid 获取 登录用户的文件夹ACL条目对象<br>
	 * 
	 * <b>如果调用者为管理员组成员 那么返回的所有权限都是true</b>
	 * 
	 * @param folderGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FolderACLItem getACLFolderItemByFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 根据folderGuid 获取 文件夹ACL条目对象
	 * 
	 * @param folderGuid
	 * @param accessTypeEnum
	 *            权限授予者类型
	 * @param objectGuid
	 *            权限授予者guid
	 *            当权限授予者类型为others，owneruser时传值为空
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FolderACLItem getACLFolderItemByFolder(String folderGuid, AccessTypeEnum accessTypeEnum, String objectGuid) throws ServiceRequestException;

	/**
	 * 根据guid 获取 ACL条目对象
	 * 
	 * @param aclItemGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ACLItem getACLItem(String aclItemGuid) throws ServiceRequestException;

	/**
	 * 根据guid 获取 ACL条目对象<该方法仅供库管理使用>
	 * 
	 * @param aclItemGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public FolderACLItem getACLItem4Lib(String aclItemGuid) throws ServiceRequestException;

	/**
	 * 计算用户userId在组groupId中的roleId下, 对实例对象FoundationObject的权限<br>
	 * Note. 普通用户只能查看自己的权限信息
	 * 
	 * <b>groupId为管理员组 那么返回的所有权限都是true</b>
	 * 
	 * @param objectGuid
	 *            实例对象guid
	 * @param userId
	 *            用户id
	 * @param groupId
	 *            组id
	 * @param roleId
	 *            角色id
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及user或group未找到异常
	 */
	public ACLItem getACLItemForObjectByUser(ObjectGuid objectGuid, String userId, String groupId, String roleId) throws ServiceRequestException;
	
	/**
	 * 计算当前调用者对此功能的权限
	 * @param position
	 * @param functionName
	 * @param userId
	 * @param roleId
	 * @param groupId
	 * @return
	 * @throws ServiceRequestException
	 */
	public PermissibleEnum getFunctionPermissionByUser(ModulEnum position,String functionName,String userGuid,String roleGuid,String groupGuid) throws ServiceRequestException;

	/**
	 * 计算当前调用者 对实例对象FoundationObject的权限<br>
	 * 
	 * <b>如果调用者为管理员组成员 那么返回的所有权限都是true</b>
	 * 
	 * @param objectGuid
	 *            实例对象guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及user或group未找到异常
	 */
	public ACLItem getACLItemForObject(ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 根据guid 获取 ACL主体对象
	 * 
	 * @param aclSubjectGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ACLSubject getACLSubject(String aclSubjectGuid) throws ServiceRequestException;

	/**
	 * 根据guid 获取 ACL主体对象<该方法仅供库管理使用>
	 * 
	 * @param aclSubjectGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public ACLSubject getACLSubject4Lib(String aclSubjectGuid) throws ServiceRequestException;

	/**
	 * 根据folderGuid, 查询文件夹权限列表
	 * 
	 * @param folderGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FolderACLItem> listACLFolderItemByFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 根据folderGuid(库的guid), 查询库权限列表
	 * 
	 * @param folderGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FolderACLItem> listACLLibItemByLib(String folderGuid) throws ServiceRequestException;

	/**
	 * 根据folderGuid, 查询文件夹权限列表(在保存数据之前临时查看保存后的效果)
	 * 
	 * @param folderGuid
	 * @param isExdtend
	 *            是否继承
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<FolderACLItem> listACLFolderItemByFolderTemporary(String folderGuid, boolean isExdtend) throws ServiceRequestException;

	/**
	 * 根据权限控制类别, 查询权限控制列表
	 * 
	 * @param aclSubjectGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLItem> listACLItemBySubject(String aclSubjectGuid) throws ServiceRequestException;
	
	/**
	 * 根据权限控制类别, 查询权限控制列表
	 * 
	 * @param aclSubjectGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLFunctionItem> listACLFunctionItemByFunctionObject(String aclFunctionObjectGuid) throws ServiceRequestException;

	/**
	 * 根据权限控制类别, 查询权限控制列表<该方法仅供库管理使用>
	 * 
	 * @param aclSubjectGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLItem> listACLItemBySubject4Lib(String aclSubjectGuid) throws ServiceRequestException;

	// /**
	// * 查看用户userId在组groupId中, 对实例对象objectGuid的权限列表<br>
	// * Note. 普通用户只能查看自己的权限信息
	// *
	// * @param objectGuid
	// * @return 权限条目列表, 其中最后一条为通过对条目进行计算后的权限结果.
	// * @throws ServiceRequestException
	// * 数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	// * 及user或group未找到异常
	// */
	// public List<ACLItem> listACLItemForObjectByUser(ObjectGuid objectGuid, String userId, String groupId, String
	// roleId)
	// throws ServiceRequestException;

	/**
	 * 根据folderGuid, 查询共享文件权限列表
	 * 
	 * @param folderGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ShareFolderACLItem> listACLSharedFolderItemByFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 根据公共库的guid查询第一层ACLSubject的列表
	 * 
	 * @param libraryGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLSubject> listRootACLSubjectByLIB(String libraryGuid) throws ServiceRequestException;
	
	/**
	 * 查询第一层功能权限的列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLFunctionObject> listRootACLFunctionObject() throws ServiceRequestException;

	/**
	 * 根据公共库的guid查询第一层ACLSubject的列表
	 * 
	 * @param libraryGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLSubject> listRootACLSubjectByLib4Lib(String libraryGuid) throws ServiceRequestException;

	/**
	 * 根据ACLSubject的guid查询下层ACLSubject的列表
	 * 
	 * @param aclSubjectGuid
	 * @param isCascade
	 *            是否包含所有子层 true 包含 false 只查找当前子层
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLSubject> listSubACLSubject(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException;
	
	/**
	 * 根据ACLFunctionObject的guid查询下层ACLFunctionObject的列表
	 * 
	 * @param aclSubjectGuid
	 * @param isCascade
	 *            是否包含所有子层 true 包含 false 只查找当前子层
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLFunctionObject> listSubACLFunctionObject(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException;

	/**
	 * 根据ACLSubject的guid查询下层ACLSubject的列表<该方法仅供库管理使用>
	 * 
	 * @param aclSubjectGuid
	 * @param isCascade
	 *            是否包含所有子层 true 包含 false 只查找当前子层
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<ACLSubject> listSubACLSubject4Lib(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException;

	/**
	 * 保存文件夹的ACL条目
	 * 
	 * @param saveACLItemList
	 *            修改为的权限列表(删除的权限不要传)
	 * @param folderGuid
	 *            文件夹guid
	 * @param isExtend
	 *            是否继承父文件夹权限
	 * @param isMandatoryWriteOver
	 *            是否强制覆盖继承子文件夹权限
	 * @param isMandatoryAll
	 *            是否强制覆盖所有子文件夹权限
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及type属于USER/RIG/ROLE/GROUP之一缺少value异常
	 */
	public void batchDealFolderACLItem(List<FolderACLItem> saveACLItemList, String folderGuid, boolean isExtend, boolean isMandatoryWriteOver, boolean isMandatoryAll)
			throws ServiceRequestException;

	/**
	 * 保存库的ACL条目<br>
	 * 先删除后添加
	 * 
	 * @param saveACLItemList
	 *            修改为的权限列表(删除的权限不要传),如果参数为空 那么直接删除该库的所有权限
	 * 
	 * @param folderGuid
	 *            文件夹guid
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及type属于USER/RIG/ROLE/GROUP之一缺少value异常
	 */
	public void batchDealLibACLItem(List<FolderACLItem> saveACLItemList, String folderGuid) throws ServiceRequestException;

	/**
	 * 保存共享文件夹的ACL条目<br>
	 * 先删除后添加
	 * 
	 * @param saveACLItemList
	 *            修改为的权限列表(删除的权限不要传),如果参数为空 那么直接删除该库的所有权限
	 * 
	 * @param folderGuid
	 *            文件夹guid
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及type属于USER/RIG/ROLE/GROUP之一缺少value异常
	 */
	public void batchDealSharedFolderACLItem(List<ShareFolderACLItem> saveACLItemList, String folderGuid) throws ServiceRequestException;

	/**
	 * 判断文件夹/库的指定类别的权限
	 * 
	 * @param folderGuid
	 * @param folderAuthorityEnum
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasFolderAuthority(String folderGuid, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid)
			throws ServiceRequestException;

	/**
	 * 根据guid 删除 "PUBLIC检索"ACL条目对象
	 * 
	 * @param publicSearchGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void deletePublicSearchACLItem(String publicSearchGuid) throws ServiceRequestException;

	/**
	 * 根据publicSearchGuid, 查询"PUBLIC检索"权限列表
	 * 
	 * @param publicSearchGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<PublicSearchACLItem> listPublicSearchACLItemByPreSearchGuid(String publicSearchGuid) throws ServiceRequestException;

	/**
	 * 创建/更新"PUBLIC检索"的ACL条目<br>
	 * 
	 * 采用先根据publicSearchGuid删 acl集合 然后添加加的方式处理
	 * 
	 * @param aclItemList
	 * @param publicSearchGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、type属于USER/RIG/ROLE/GROUP之一缺少value异常
	 */
	public void savePublicSearchACLItem(List<PublicSearchACLItem> aclItemList, String publicSearchGuid) throws ServiceRequestException;

	/**
	 * 根据BO,用户，库判断是否有创建权限
	 * 
	 * @param boName
	 * @return Y/N
	 */
	public boolean hasFoundationCreateACL(String boName) throws ServiceRequestException;

	/**
	 * 判断实例下的文件是否有下载权限
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean hasFileDownloadACL(ObjectGuid objectGuid) throws ServiceRequestException;
}
