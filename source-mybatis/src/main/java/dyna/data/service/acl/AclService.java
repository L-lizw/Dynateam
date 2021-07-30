package dyna.data.service.acl;

import java.util.List;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.acl.SaAclFolderLibConf;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.PublicSearchAuthorityEnum;
import dyna.common.systemenum.ShareFolderAuthorityEnum;
import dyna.net.service.Service;

public interface AclService extends Service
{
	/**
	 * 判断是否有除了创建权限外的某种权限
	 * 1、根据sessionId获得Session
	 * 2、获取Session中的user对应的所有权限字符串
	 * 如果没有对应的权限，返回false
	 * 3、根据传进来的参数authorityEnum，获得对应权限字符
	 * 4、比较权限字符，等于2返回false，不等于2返回true
	 * 
	 * @param objectGuid
	 *            实例基本信息，不能为空，必须包含guid
	 * @param authorityEnum
	 *            权限枚举，不能为空
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @return
	 *         是否有权限
	 * @throws DynaDataException
	 */
	public boolean hasAuthority(ObjectGuid objectGuid, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException;

	public boolean hasAuthority(FoundationObject foundationObject, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException;

	/**
	 * 根据guid判断权限
	 * 
	 * @param foundationGuid
	 * @param className
	 * @param authorityEnum
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public boolean hasAuthority(String foundationGuid, String className, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException;

	/**
	 * 根据guid获取foundation的所有权限
	 * 根据系统版本规则配置如果允许查看其他用户检出的数据，同时数据已经检出那么获取的是检出后数据的权限
	 * 
	 * @param foundationGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @return 以分号（;）分隔的0/1/2组合字符串 0有权限、1有授予权限、2没有权限
	 *         顺序为：查询；新建；修订；删除；生效；废弃；发布；检出；取消组检出；更改所有者；
	 *         更改编号；更改主名称；导出；导入；预览文件；查看文件；下载文件；上传文件；管理(编辑)文件；删除文件；
	 *         添加子阶；编辑结构；移除子阶
	 * @throws DynaDataException
	 */
	public String getAuthority(String foundationGuid, String className, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException;

	/**
	 * 判断文件夹权限
	 * 1、根据sessionId获得Session
	 * 2、获取Session中的user对应的所有权限字符串
	 * 如果没有对应的权限，返回false
	 * 3、根据传进来的参数folderAuthorityEnum，获得对应权限字符
	 * 4、比较权限字符，等于2返回false，不等于2返回true
	 * 
	 * @param folderGuid
	 *            文件夹guid，不能为空
	 * @param folderAuthorityEnum
	 *            文件夹权限枚举，不能为空
	 * @param userGuid
	 *            用户guid，不能为空
	 * @param groupGuid
	 *            用户对应组guid，不能为空
	 * @param roleGuid
	 *            用户对应角色guid，不能为空
	 * @return
	 *         是否有权限
	 */
	public boolean hasFolderAuthority(String folderGuid, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid)
			throws ServiceRequestException;

	/**
	 * 判断文件夹权限
	 * 1、根据sessionId获得Session
	 * 2、获取Session中的user对应的所有权限字符串
	 * 如果没有对应的权限，返回false
	 * 3、根据传进来的参数folderAuthorityEnum，获得对应权限字符
	 * 4、比较权限字符，等于2返回false，不等于2返回true
	 * 
	 * @param folderGuid
	 *            文件夹guid，不能为空
	 * @param folderAuthorityEnum
	 *            文件夹权限枚举，不能为空
	 * @param userGuid
	 *            用户guid，不能为空
	 * @param groupGuid
	 *            用户对应组guid，不能为空
	 * @param roleGuid
	 *            用户对应角色guid，不能为空
	 * @return
	 *         是否有权限
	 */
	public boolean hasFolderAuthority(Folder folder, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException;

	/**
	 * 获得文件夹的权限
	 * 
	 * @param folderGuid
	 * @return
	 * @throws DynaDataException
	 */
	public List<FolderACLItem> listFolderAuth(String folderGuid) throws ServiceRequestException;

	/**
	 * 获得文件夹的改变后的权限集合
	 * 
	 * @param folderGuid
	 *            folder的guid，不能为空
	 * @param isextend
	 *            是否计算继承权限
	 * @return
	 *         ACLItem列表
	 * @throws DynaDataException
	 */
	List<FolderACLItem> listChangingLibFolderAuth(String folderGuid, boolean isextend) throws ServiceRequestException;

	SaAclFolderLibConf getSaAclFolderLibConf(String folderGuid) throws DynaDataException;

	/**
	 * 根据文件夹的guid获取文件夹的所有权限
	 * 
	 * @param folderGuid
	 * @param userGuid
	 * @param groupGuid
	 * @param roleGuid
	 * @return ACLItem.OPER_MANAGE 管理
	 *         ACLItem.OPER_READ 查询库数据
	 *         ACLItem.OPER_CREATE 新建文件夹
	 *         ACLItem.OPER_DELETE 删除库
	 *         ACLItem.OPER_RENAME 重命名库
	 *         ACLItem.OPER_ADDREF 添加对象
	 *         ACLItem.OPER_DELREF 移除对象
	 * @throws DynaDataException
	 */
	public FolderACLItem getAuthorityFolder(String folderGuid, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException;

	/**
	 * 获取创建实例或者实例和文件夹关联关系的权限
	 * isCreateAuth为true时，需要获取foundation的以下信息：
	 * foundationObject.getLocationlib(),//
	 * foundationObject.getOwnerUserGuid(),//
	 * foundationObject.getOwnerGroupGuid(),//
	 * foundationObject.getObjectGuid().getClassGuid(),//
	 * foundationObject.getClassificationGuid(),//
	 * foundationObject.getStatus().toString(),//
	 * foundationObject.getLifecyclePhaseGuid(),//
	 * foundationObject.getRevisionId()//
	 * 
	 * @param foundationObject
	 * @param folderGuid
	 * @param sessionId
	 * @param isCreateAuth
	 *            是否创建实例
	 * @param isRefAuth
	 *            是否创建关联关系
	 * @return 0有权限、2没有权限
	 * @throws DynaDataException
	 */
	public String getCreateAuthority(FoundationObject foundationObject, String folderGuid, String sessionId, boolean isCreateAuth, boolean isRefAuth)
			throws ServiceRequestException;

	/**
	 * 获取user的检出权限（移交检出时用）
	 * 根据系统版本规则配置如果允许查看其他用户检出的数据，同时数据已经检出那么获取的是检出后数据的权限
	 * 
	 * @param toUserGuid
	 * @param instanceGuid
	 * @return 0有权限、1有授予权限、2没有权限
	 * @throws DynaDataException
	 */
	public String getTransferCheckoutAuthority(String toUserGuid, String instanceGuid, String className) throws ServiceRequestException;

	/**
	 * 获取删除数据或者删除关联关系的权限
	 * 如果是删除数据，
	 * 需要根据系统版本规则配置，如果允许查看其他用户检出的数据，同时数据已经检出那么获取的是检出后数据的权限
	 * 
	 * @param objectGuid
	 * @param folderGuid
	 * @param sessionId
	 * @param isDelRef
	 *            true:删除关联关系（此时folderGuid不能为空）；false：删除实例
	 * @return 0有权限、1有授予权限、2没有权限
	 * @throws DynaDataException
	 */
	public String getDelAuthority(ObjectGuid objectGuid, String folderGuid, String sessionId, boolean isDelRef) throws ServiceRequestException;

	/**
	 * 是否有创建权限
	 * 1、获取当前用户对应的默认库
	 * 2、在此库下判断用户对此类在状态为CRT、分类为空、生命周期为空、最新版状态为 master下的最新版的情况下的创建权限
	 * 
	 * @param classGuid
	 *            类guid，不能为空
	 * @param sessionId
	 *            会话ID，不能为空
	 * @return
	 * @throws DynaDataException
	 */
	public boolean hasCreateAuthorityForClass(String classGuid, String sessionId) throws ServiceRequestException;

	/**
	 * 判断是否有某种public搜索权限
	 * 1、根据sessionId获得Session
	 * 2、获取Session中的user对应的所有public搜索权限字符串
	 * 如果没有对应的权限，返回false
	 * 3、根据传进来的参数preSearchAuthorityEnum，获得对应权限字符
	 * 4、比较权限字符，等于2返回false，不等于2返回true
	 * public搜索有三种权限：查看、修改、删除
	 * 
	 * @param publicSearchGuid
	 *            不能为空
	 * @param publicSearchAuthorityEnum
	 *            权限枚举，不能为空
	 * @param sessionId
	 *            不能为空
	 * @return
	 *         是否有权限
	 * @throws DynaDataException
	 */
	public boolean hasAuthorityForPublicSearch(String publicSearchGuid, PublicSearchAuthorityEnum publicSearchAuthorityEnum, String sessionId) throws ServiceRequestException;

	/**
	 * 查询用户对共享文件夹是否有某种权限
	 * 
	 * @param sharedFolderGuid
	 * @param shareFolderAuthorityEnum
	 *            权限类型
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public boolean hasAuthorityForSharedFolder(String sharedFolderGuid, ShareFolderAuthorityEnum shareFolderAuthorityEnum, String sessionId) throws DynaDataException;

	/**
	 * 按照树形结构返回权限
	 * 
	 * @return key:公共库 value：root权限
	 * @throws DynaDataException
	 */
	public Map<String, ACLSubject> listAllSubjectWithTree() throws DynaDataException;

	/**
	 * 把权限树加载到内存中
	 * 
	 * @throws DynaDataException
	 */
	public void loadACLTreeToCache() throws ServiceRequestException;
}
