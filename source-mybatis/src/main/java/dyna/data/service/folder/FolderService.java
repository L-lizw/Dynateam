package dyna.data.service.folder;

import java.util.List;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

public interface FolderService extends Service
{
	/**
	 * 创建库/公共文件夹/私有文件夹
	 * 1、如果创建公共文件夹/私有文件夹，没有创建权限，不能创建
	 * 2、创建文件夹
	 * 3、如果是创建公共库文件夹/库，将父项权限授予公共库文件夹就/库
	 * 4、如果创建库，为库创建根权限；已经有同命库，不能再创建
	 * 5、更新计算文件夹所在整个文件夹结构中的文件夹路径
	 *
	 * @param folder     文件夹信息，不能为空，必须包含文件夹类型（库、公共文件夹、私有文件夹）
	 * @param sessionId  当前操作者的sessionId，不能为空
	 * @param isCheckAcl 是否判断权限，不能为空
	 * @return 创建后的文件夹
	 * @throws ServiceRequestException
	 */
	Folder createFolder(Folder folder, String sessionId, String fixTranId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 删除文件夹
	 * 1、要删除的文件夹不存在，抛异常，不能删除
	 * 2、在需要判断权限的情况下。
	 * 如果删除的是库，判断是否有该库的管理权限，如果没有库的管理权限，不能删除；
	 * 如果删除的是公共文件夹、私有文件夹，判断文件夹的删除权限，如果没有文件夹的删除权限，不能删除。
	 * 3、如果删除的是库，库下有数据，不能删除
	 *
	 * @param folderGuid 要删除的文件夹的guid，不能为空
	 * @param sessionId  不能为空，用来判断权限
	 * @param isCheckAcl 是否判断权限，不能为空
	 * @throws ServiceRequestException
	 */
	void deleteFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 根据folderGuid获取folder
	 * 1、如果folderGuid为空，返回null
	 * 2、在需要判断权限的情况下，如果是共有文件夹和私有文件夹，没有查看权限，报没有查看权限的异常，不能查询
	 *
	 * @param folderGuid 文件夹guid，可以为空，空则返回null
	 * @param sessionId  不能为空，用来判断权限
	 * @param isCheckAcl 不能为空，是否判断权限
	 * @return 有权限的文件夹集合
	 * @throws ServiceRequestException
	 */
	Folder getFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 根据folderGuid获取folder
	 * 1、如果folderGuid为空，返回null
	 * 2、在需要判断权限的情况下，如果是共有文件夹和私有文件夹，没有查看权限，报没有查看权限的异常，不能查询
	 *
	 * @param folderGuid 文件夹guid，可以为空，空则返回null
	 * @param userGuid   不能为空，用来判断权限
	 * @param groupGuid  不能为空，用来判断权限
	 * @param roleGuid   不能为空，用来判断权限
	 * @param isCheckAcl 不能为空，是否判断权限
	 * @return 有权限的文件夹集合
	 * @throws ServiceRequestException
	 */
	Folder getFolder(String folderGuid, String userGuid, String groupGuid, String roleGuid, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 更新文件夹信息
	 * 1、在需要判断权限的情况下，判断文件夹重命名权限，如果没有文件俺家的重命名权限，不能更新
	 * 2、直接用sql更新文件夹信息
	 * 3、更新文件夹路径
	 *
	 * @param folder     要更新的文件夹的guid，不能为空
	 * @param sessionId  当前操作者的sessionId，不能为空
	 * @param isCheckAcl 是否判断权限，不能为空
	 * @return 更新后的Folder
	 * @throws ServiceRequestException
	 */
	Folder updateFolder(Folder folder, String sessionId, String fixTranId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 获取子文件夹/获取所有查看权限的子文件夹
	 *
	 * @param folderGuid 文件夹guid，不能为空
	 * @param sessionId  不能为空，用来判断权限
	 * @param isCheckAcl 是否校验权限，不能为空
	 * @return 有权限的文件夹List
	 * @throws ServiceRequestException
	 */
	List<Folder> listSubFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 取得用户的私有根目录
	 *
	 * @param userGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	Folder getRootPrivateFolderByUser(String userGuid) throws ServiceRequestException;

	/**
	 * 取得指定文件夹的所有子文件夹，不判断权限
	 *
	 * @param folderGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	List<Folder> listAllSubFolder(String folderGuid) throws ServiceRequestException;

	/**
	 * 根据实例guid查找出关联的所有文件夹
	 *
	 * @param foundationGuid 实例的guid
	 * @param isCheckAcl     预留，暂未用到
	 * @param sessionId      预留，暂未用到
	 * @return FolderList
	 * @throws ServiceRequestException
	 */
	List<Folder> listFolder(String foundationGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException;

	/**
	 * 获取指定库下主记录数
	 *
	 * @param libraryGuid 库的guid
	 * @return 库下主记录的数量
	 * @throws ServiceRequestException
	 */
	Long getMainDataNumberByLib(String libraryGuid) throws ServiceRequestException;

	/**
	 * 获取指定库下修订版本记录数
	 *
	 * @param libraryGuid 库的guid
	 * @return 库下修订版本的数量
	 * @throws ServiceRequestException
	 */
	Long getVersionDataNumberByLib(String libraryGuid) throws ServiceRequestException;

	/**
	 * 判断文件夹和实例是否有关联关系
	 *
	 * @param foundationGuid 实例的guid
	 * @param folderGuid     文件夹的guid
	 * @return 是否有关联关系
	 */
	boolean hasRelation(String foundationGuid, String folderGuid) throws ServiceRequestException;

	/**
	 * 根据当前用户信息获取有查询权限的库
	 *
	 * @param sessionId
	 * @param isCheckAcl
	 * @return Folder List
	 * @throws ServiceRequestException
	 */
	List<Folder> listLibraryByUser(String sessionId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 查出指定用户的所有的有权限的库的根文件夹。
	 *
	 * @param sessionId  当前登录用户session id。
	 * @param isCheckAcl 是否检查权限。
	 * @return
	 * @throws ServiceRequestException
	 */
	List<Folder> listRootFolder(String sessionId, boolean isCheckAcl) throws ServiceRequestException;

	/**
	 * 剪切实例，将实例从fromFolder移动到toFolder
	 * 1、fromFolder不存在，不能剪切
	 * 2、toFolder不存在，不能剪切
	 * 3、如果需要判断权限
	 * 3.1、如果没有从fromFolder删除关联关系的权限，或者没有向toFolder添加关联关系的权限，不能剪切
	 * 3.2、如果是跨库移动，没有源库的移除数据的权限，或者没有目标库的添加数据的权限，不能剪切
	 * <p>
	 * 4、移动中关于实例的处理
	 * 4.1、如果是不同用户的私有文件夹的移动，修改实例的locationUser为toFolder对应的libUser
	 * 4.2、如果是夸库移动，修改实例的locationUser为toFolder对应的libUser，修改实例的bomview、view的locationUser为toFolder对应的libUser
	 * <p>
	 * 5、移动数据
	 * 5.1、如果fromFolder不为空，先删除实例和fromFolder的关联关系，再创建实例和toFolder的关联关系
	 * 5.2、如果fromFolder为空，直接创建实例和toFolder的关联关系
	 *
	 * @param objectGuid     实例基本信息，不能为空，必须包含guid
	 * @param fromFolderGuid 源文件夹guid，可以为空
	 * @param toFolderGuid   目标文件夹guid，不能为空
	 * @param isCheckAclFrom 不能为空，是否校验权限（判断fromFolderGuid的相关权限）
	 * @param isCheckAclTo   不能为空，是否校验权限（判断toFolderGuid的相关权限）
	 * @param isMaster       公共库转移数据时，需要把master的所有数据都转移到新文件夹（O21554/A.2-删除公共库文件夹报错，但文件夹中已无对象）
	 * @param sessionId      当前操作者的sessionId，不能为空
	 * @throws ServiceRequestException
	 */
	String moveToFolder(ObjectGuid objectGuid, String fromFolderGuid, String toFolderGuid, boolean isCheckAclFrom, boolean isCheckAclTo, boolean isUpdate, boolean isMaster,
			String sessionId, String fixTranId) throws ServiceRequestException;
}
