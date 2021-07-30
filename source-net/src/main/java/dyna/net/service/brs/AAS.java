/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AAS Authorization and Authentication Service 授权与认证服务
 * Wanglei 2010-4-16
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.aas.UserAgent;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.UserWorkFolderTypeEnum;
import dyna.net.service.Service;

/**
 * Authorization and Authentication Service 授权与认证服务
 * 
 * @author Wanglei
 * 
 */
public interface AAS extends Service
{
	/**
	 * (重新)激活组<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param groupId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、group不存在异常及应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public void activeGroup(String groupId) throws ServiceRequestException;

	/**
	 * (重新)激活角色<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param roleId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及role不存在异常
	 */
	public void activeRole(String roleId) throws ServiceRequestException;

	/**
	 * (重新)激活用户<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param userId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及user不存在异常
	 */
	public void activeUser(String userId) throws ServiceRequestException;

	/**
	 * 分配角色到组<br>
	 * 系统管理员(组)有权限
	 * 
	 * @param roleGuid
	 * @param groupGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及role或者group不存在异常
	 */
	public void assignRoleToGroup(String roleGuid, String groupGuid) throws ServiceRequestException;

	/**
	 * 分配用户到角色组<br>
	 * 系统管理员(组)有权限
	 * 
	 * @param userGuid
	 * @param roleInGroupGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、user\role或者group不存在异常及应用服务器分配失败异常
	 */
	public void assignUserToRoleInGroup(String userGuid, String roleInGroupGuid) throws ServiceRequestException;

	/**
	 * 分配用户到角色组<br>
	 * 如果roleId的角色不属于groupId的组, 则默认将该角色分配到该组中.<br>
	 * 系统管理员(组)有权限
	 * 
	 * @param userGuid
	 * @param roleGuid
	 * @param groupGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void assignUserToRoleInGroup(String userGuid, String roleGuid, String groupGuid) throws ServiceRequestException;

	/**
	 * 通过组Guid取得Group
	 * 
	 * @param groupGuid
	 * @return Group
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Group getGroup(String groupGuid) throws ServiceRequestException;

	/**
	 * 通过组ID取得Group
	 * 
	 * @param groupId
	 * @return Group
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Group getGroupById(String groupId) throws ServiceRequestException;

	/**
	 * 根据groupGuid及roleGuid获取RoleInGroup实例
	 * 
	 * @param groupGuid
	 * @param roleGuid
	 * @return RIG
	 * @throws ServiceRequestException
	 */
	public RIG getRIGByGroupAndRole(String groupGuid, String roleGuid) throws ServiceRequestException;

	/**
	 * 通过角色guid获取角色对象
	 * 
	 * @param roleGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Role getRole(String roleGuid) throws ServiceRequestException;

	/**
	 * 通过角色Id获取角色对象
	 * 
	 * @param roleId
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Role getRoleById(String roleId) throws ServiceRequestException;

	/**
	 * 取得组织架构的根Group
	 * 
	 * @return ROOT-Group
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Group getRootGroup() throws ServiceRequestException;

	/**
	 * 查询group的上级group
	 * 
	 * @param groupGuid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Group getSuperGroup(String groupGuid) throws ServiceRequestException;

	/**
	 * 通过用户Guid取得用户
	 * 
	 * @param userGuid
	 * @return User
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public User getUser(String userGuid) throws ServiceRequestException;

	/**
	 * 通过用户ID取得用户
	 * 
	 * @param userId
	 * @return User
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public User getUserById(String userId) throws ServiceRequestException;

	/**
	 * 查询所有的系统组
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Group> listAllGroup() throws ServiceRequestException;

	/**
	 * 查询所有的系统角色
	 * sortField 排序字段
	 * isASC 是否升序
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Role> listAllRole(String sortField, boolean isASC) throws ServiceRequestException;

	/**
	 * 查询组织下面的所有角色
	 * 
	 * @param groupId
	 *            组id
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Role> listAllRoleByGroupId(String groupId) throws ServiceRequestException;

	/**
	 * 查询子group信息,包括已废弃和激活状态<br>
	 * 
	 * 可根据guid 或者id 来查询, 如果两个参数都提供, 则根据guid查询.<br>
	 * 
	 * cascade参数指定是否只查询所有层子组
	 * 
	 * @param groupGuid
	 *            group guid
	 * @param groupId
	 *            group id
	 * @param cascade
	 *            true - 查询所有子组信息, 包含子组的子组; false - 只查询单层子组
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器的 参数为空的异常
	 */
	public List<Group> listAllSubGroup(String groupGuid, String groupId, boolean cascade) throws ServiceRequestException;

	/**
	 * 取得所有用户列表。
	 * sortField 排序字段
	 * isASC 是否升序
	 * 
	 * @return 用户列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<User> listAllUser(String sortField, boolean isASC) throws ServiceRequestException;

	/**
	 * 查询组角色下的所有用户
	 * 
	 * @param groupId
	 *            组groupId
	 * @param roleId
	 *            角色roleId
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<User> listAllUserByRoleInGroup(String groupId, String roleId) throws ServiceRequestException;

	/**
	 * 查询系统组
	 * 
	 * @param hasRoot
	 *            true 包含根，false 不包含根节点
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Group> listGroup(boolean hasRoot) throws ServiceRequestException;

	/**
	 * 通过用户ID，取得用户组列表。
	 * 
	 * @return 用户组列表。
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Group> listGroupByUser(String userId) throws ServiceRequestException;

	/**
	 * 通过用户ID，取得用户组列表。不包含ROOT组,仅在登录使用
	 * 
	 * @return 用户组列表。
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Group> listGroupByUserForLogin(String userId) throws ServiceRequestException;

	/**
	 * 获取用户的组与角色
	 * 
	 * @param userId
	 *            用户的Id
	 * @return 用户的RIG
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<RIG> listRIGOfUser(String userId) throws ServiceRequestException;

	/**
	 * 获取用户的组与角色,不包含ROOT组,仅在登录使用
	 * 
	 * @param userId
	 *            用户的Id
	 * @return 用户的RIG
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<RIG> listRIGOfUserForLogin(String userId) throws ServiceRequestException;

	/**
	 * 查询系统角色
	 * sortField 排序字段
	 * isASC 是否升序
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Role> listRole(String sortField, boolean isASC) throws ServiceRequestException;

	/**
	 * 查询组织下面的角色
	 * 
	 * @param groupId
	 *            组Id
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Role> listRoleByGroupId(String groupId) throws ServiceRequestException;

	/**
	 * 查询组织下面的角色
	 * 
	 * @param groupGuid
	 *            组Guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Role> listRoleByGroup(String groupGuid) throws ServiceRequestException;

	/**
	 * 查询指定用户对应的指定组织下面的所有角色
	 * 
	 * @param userId
	 *            用户Id
	 * @param groupId
	 *            组Id
	 * 
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<Role> listRoleByUserInGroup(String userId, String groupId) throws ServiceRequestException;

	/**
	 * 查询所有角色组
	 * 
	 * @return 系统定义的角色组列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<RIG> listRoleInGroup() throws ServiceRequestException;

	/**
	 * 查询子group信息<br>
	 * 
	 * 可根据guid 或者id 来查询, 如果两个参数都提供, 则根据guid查询.<br>
	 * 
	 * cascade参数指定是否只查询所有层子组
	 * 
	 * @param groupGuid
	 *            group guid
	 * @param groupId
	 *            group id
	 * @param cascade
	 *            true - 查询所有子组信息, 包含子组的子组; false - 只查询单层子组
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器的 参数为空的异常
	 */
	public List<Group> listSubGroup(String groupGuid, String groupId, boolean cascade) throws ServiceRequestException;

	/**
	 * 查询group的所有上级group
	 * 
	 * @param groupGuid
	 * @param groupId
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)及应用服务器的 参数为空的异常
	 */
	public List<Group> listSuperGroup(String groupGuid, String groupId) throws ServiceRequestException;

	/**
	 * 取得用户列表。
	 * sortField 排序字段
	 * isASC 是否升序
	 * 
	 * @return 用户列表
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<User> listUser(String sortField, boolean isASC) throws ServiceRequestException;

	/**
	 * 查询组角色下的用户
	 * 
	 * @param roleInGroupGuid
	 *            组角色guid
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<User> listUserByRoleInGroup(String roleInGroupGuid) throws ServiceRequestException;

	/**
	 * 查询组角色下的用户
	 * 
	 * @param groupId
	 *            组groupId
	 * @param roleId
	 *            角色roleId
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public List<User> listUserByRoleInGroup(String groupId, String roleId) throws ServiceRequestException;

	/**
	 * 根据Group的主键获取该Group下的所有未废弃的用户
	 * 
	 * @param groupGuid
	 * @return 用户列表
	 * @throws ServiceRequestException
	 */
	public List<User> listUserInGroup(String groupGuid) throws ServiceRequestException;

	/**
	 * 根据Group的主键获取该Group下的以及子Group下所有未废弃的用户
	 * 
	 * @param groupGuid
	 * @return 用户列表
	 * @throws ServiceRequestException
	 */
	public List<User> listUserInGroupAndSubGroup(String groupGuid) throws ServiceRequestException;

	/**
	 * 用户登录系统
	 * 
	 * @param userID
	 *            用户id
	 * @param groupID
	 *            登录系统所使用的组id
	 * @param roleID
	 *            登录系统所使用的角色id
	 * @param password
	 *            登录系统所需的用户密码
	 * @param lang
	 *            登录者选择的语言
	 * @return 用户登录系统获得的证书
	 * @throws ServiceRequestException
	 *             服务请求异常
	 */
	public String login(String userID, String groupID, String roleID, String password, String hostName, LanguageEnum lang) throws ServiceRequestException;

	/**
	 * 用户登录系统
	 * 
	 * @param userID
	 *            用户id
	 * @param groupID
	 *            登录系统所使用的组id
	 * @param roleID
	 *            登录系统所使用的角色id
	 * @param password
	 *            登录系统所需的用户密码
	 * @param appType
	 *            应用类型, 可为空
	 * @param lang
	 *            登录者选择的语言
	 * @return 用户登录系统获得的证书
	 * @throws ServiceRequestException
	 *             服务请求异常
	 */
	public String login(String userID, String groupID, String roleID, String password, String hostName, ApplicationTypeEnum appType, LanguageEnum lang)
			throws ServiceRequestException;

	/**
	 * 用户登录系统
	 * 
	 * @param userID
	 *            用户id
	 * @param groupID
	 *            登录系统所使用的组id
	 * @param roleID
	 *            登录系统所使用的角色id
	 * @param password
	 *            登录系统所需的用户密码
	 * @param ip
	 *            登陆者ip, 可为空
	 * @param appType
	 *            应用类型, 可为空
	 * @param lang
	 *            登录者选择的语言
	 * @return 用户登录系统获得的证书
	 * @throws ServiceRequestException
	 *             服务请求异常
	 */
	public String login(String userID, String groupID, String roleID, String password, String ip, String hostName, ApplicationTypeEnum appType, LanguageEnum lang)
			throws ServiceRequestException;

	/**
	 * 用户登出系统. 登出用户为调用此服务的用户.
	 * 
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public void logout() throws ServiceRequestException;

	// /**
	// * 用户登出系统. 登出用户由所传参数决定.
	// *
	// * @param userID
	// * 用户id
	// * @param groupID
	// * 登录系统所使用的组id
	// * @param roleID
	// * 登录系统所使用的roleID
	// * @throws ServiceRequestException
	// * 服务请求异常
	// */
	// public void logout(String userID, String groupID, String roleID) throws ServiceRequestException;

	// /**
	// * 用户登出系统. 登出用户由所传参数决定.
	// *
	// * @param userID
	// * 用户id
	// * @param groupID
	// * 登录系统所使用的组id
	// * @param roleID
	// * 登录系统所使用的roleID
	// * @param appType
	// * 登录系统的应用类型
	// * @throws ServiceRequestException
	// * 服务请求异常
	// */
	// public void logout(String userID, String groupID, String roleID, ApplicationTypeEnum appType)
	// throws ServiceRequestException;

	/**
	 * 废除组<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param groupId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器(ID_APP_SERVER_EXCEPTION)及group不存在异常
	 */
	public void obsoleteGroup(String groupId) throws ServiceRequestException;

	/**
	 * 废除角色<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param roleId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器的role不存在异常
	 */
	public void obsoleteRole(String roleId) throws ServiceRequestException;

	/**
	 * 废除用户<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param userId
	 * @return User
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器的user不存在异常
	 */
	public User obsoleteUser(String userId) throws ServiceRequestException;

	/**
	 * 重设用户密码, 将密码重设为系统默认密码
	 * 
	 * @param userId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器的user不存在异常
	 */
	public void resetUserPassword(String userId) throws ServiceRequestException;

	/**
	 * 取消groupId的组内角色roleId<br>
	 * 系统管理员(组)有权限
	 * 
	 * @param roleId
	 * @param groupId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器操作失败异常
	 */
	public void revokeRoleFromGroup(String roleId, String groupId) throws ServiceRequestException;

	/**
	 * 取消取消userId的用户在roleInGroupGuid的组角色<br>
	 * 系统管理员(组)有权限
	 * 
	 * @param userId
	 * @param roleInGroupGuid
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器操作失败异常
	 */
	public void revokeUserFromRoleInGroup(String userId, String roleInGroupGuid) throws ServiceRequestException;

	/**
	 * 取消userId的用户在groupId的组内的roleId角色<br>
	 * 系统管理员(组)有权限
	 * 
	 * @param userId
	 * @param roleId
	 * @param groupId
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器操作失败异常
	 */
	public void revokeUserFromRoleInGroup(String userId, String roleId, String groupId) throws ServiceRequestException;

	/**
	 * 新建或修改组信息<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param group
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Group saveGroup(Group group) throws ServiceRequestException;

	/**
	 * 新建或修改角色信息<br>
	 * 仅系统管理员(组)有权限
	 * 
	 * @param role
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public Role saveRole(Role role) throws ServiceRequestException;

	/**
	 * 新建或修改用户信息<br>
	 * 系统管理员(组)和用户本人有权限
	 * 
	 * 如果是创建用户 会同时给用户创建一个私有文件夹的根目录和订阅夹的根目录
	 * 
	 * @param user
	 * @return
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器异常(ID_APP_SERVER_EXCEPTION)
	 */
	public User saveUser(User user) throws ServiceRequestException;

	/**
	 * 修改用户密码<br>
	 * 用户本人有权限
	 * 
	 * @param userId
	 *            用户id
	 * @param oldPwd
	 *            用户旧密码
	 * @param newPwd
	 *            用户新密码
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)、应用服务器旧密码无效异常
	 */
	public void updateUserPassword(String userId, String oldPwd, String newPwd) throws ServiceRequestException;

	/**
	 * 取得指定角色的所有RIG list
	 * 
	 * @param roleGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RIG> listRIGByRoleGuid(String roleGuid) throws ServiceRequestException;

	/**
	 * 取得指定RIG
	 * 
	 * @param rigGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public RIG getRIG(String rigGuid) throws ServiceRequestException;

	/**
	 * 判断用户是否在这个组中
	 * 
	 * @param groupGuid
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isUserInGroup(String groupGuid, String userGuid) throws ServiceRequestException;

	/**
	 * 判断用户是否在这个Role in Group中
	 * 
	 * @param roleInGroupGuid
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isUserInRIG(String roleInGroupGuid, String userGuid) throws ServiceRequestException;

	/**
	 * 判断用户是否在这个Role中
	 * 
	 * @param roleGuid
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isUserInRole(String roleGuid, String userGuid) throws ServiceRequestException;

	/**
	 * 通过用户Guid取得用户,根据权限判断是否掩盖详细信息
	 * 管理员与当前登录者将返回详细信息，否则某些字段返回XXXX
	 * 
	 * @param userGuid
	 * @return User
	 * @throws ServiceRequestException
	 *             数据库操作异常，详见数据层异常枚举(DataExceptionEnum)
	 */
	public User getMaskUser(String userGuid) throws ServiceRequestException;

	// /**
	// * 比较服务端客户端版本
	// *
	// *
	// * @throws ServiceRequestException
	// */
	// public void getServiceVersion(String versionInfo) throws ServiceRequestException;

	/**
	 * 验证客户端版本是否正确
	 * 
	 * @param clientVer
	 * @param clientType
	 *            client/service/codemanager
	 * @param isDebug
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean checkClientVersion(String clientVer, String clientType, boolean isDebug) throws ServiceRequestException;

	/**
	 * 失效用户代理设置（更新或者新建）
	 * 
	 * @param agent
	 */
	public UserAgent obsoleteUserAgent(UserAgent agent) throws ServiceRequestException;

	/**
	 * 生效用户代理设置（更新或者新建）
	 * 
	 * @param agent
	 */
	public UserAgent activeUserAgent(UserAgent agent) throws ServiceRequestException;

	/**
	 * 取得用户代理设置
	 * 
	 * @param agent
	 * @return
	 */
	public UserAgent getAgentByPrincipal(String principalGuid) throws ServiceRequestException;

	/**
	 * 根据被代理人取得生效的其代理人信息
	 * 
	 * @param principalGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public UserAgent getValidAgentByPrincipal(String principalGuid) throws ServiceRequestException;

	/**
	 * 取得代理人列表
	 * 
	 * @param agent
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<UserAgent> listUserAgent(UserAgent agent) throws ServiceRequestException;

	/**
	 * 判断代理人和被代理人之间是否存在有效的代理关系
	 * 
	 * @param agentGuid
	 * @param principalGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isAgent(String agentGuid, String principalGuid) throws ServiceRequestException;

	/**
	 * 根据GUID取得对应的用户或者组或者组角色的类型以及名称等信息
	 * 
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	public User getUserWitType(String guid) throws ServiceRequestException;

	/**
	 * 根据指定的用户和工作目录类型，取得工作目录
	 * 
	 * @param userGuid
	 * @param workFolderTypeEnum
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getUserWorkFolder(String userGuid, UserWorkFolderTypeEnum workFolderTypeEnum) throws ServiceRequestException;

	/**
	 * 保存用户工作目录
	 * 
	 * @param userGuid
	 * @param workFolderTypeEnum
	 * @param folderPath
	 * @throws ServiceRequestException
	 */
	public void saveUserWorkFolder(String userGuid, UserWorkFolderTypeEnum workFolderTypeEnum, String folderPath) throws ServiceRequestException;

	/**
	 * 查询已有用户登录
	 * 
	 * @param userID
	 *            用户id
	 * @param password
	 *            登录系统所需的用户密码
	 * @param ip
	 *            登陆者ip, 可为空
	 * @param appType
	 *            应用类型, 可为空
	 * @return 用户登录系统获得的证书
	 * @throws ServiceRequestException
	 *             服务请求异常
	 */
	public String lookupSession(String userID, String password, String ip, String hostName, ApplicationTypeEnum appType) throws ServiceRequestException;

	/**
	 * 查询已有用户登录
	 * 
	 * @param userID
	 * @param password
	 * @param isEncryptPwd
	 *            密码已加密
	 * @param ip
	 * @param hostName
	 * @param appType
	 * @return
	 * @throws ServiceRequestException
	 */
	public String lookupSession(String userID, String password, boolean isEncryptPwd, String ip, String hostName, ApplicationTypeEnum appType) throws ServiceRequestException;

	/**
	 * 查询移动用户的PLM帐户
	 * 
	 * @param userID
	 *            用户id
	 * @param password
	 *            登录系统所需的用户密码
	 * @param ip
	 *            登陆者ip, 可为空
	 * @param appType
	 *            应用类型, 可为空
	 * @return 用户登录系统获得的证书
	 * @throws ServiceRequestException
	 *             服务请求异常
	 */
	public String getPLMUserByMobile(String mobileUserID) throws ServiceRequestException;

	/**
	 * 根据传递的sessionId，判断报表用户是否能成功登陆
	 * 
	 * @param sessionId
	 * @param userId
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean reportUserAuthentication(String sessionId, String userId) throws ServiceRequestException;

	public List<Group> lisgGroupByBM(String bmguid) throws ServiceRequestException;

	/**
	 * 判断Group是不是另一个Group的下层Group
	 * 
	 * @param groupGuid
	 * @param parentGroupGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public boolean isChildGroup(String groupGuid, String parentGroupGuid) throws ServiceRequestException;
}
