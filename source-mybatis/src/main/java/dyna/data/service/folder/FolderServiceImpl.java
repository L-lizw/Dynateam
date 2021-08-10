package dyna.data.service.folder;

import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Folder;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.DataRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl extends DataRuleService implements FolderService
{
	@Autowired
	private DSFolderStub			folderStub;
	@Autowired
	private DSFolderRelationStub	folderRelationStub;

	protected DSFolderStub getFolderStub()
	{
		return this.folderStub;
	}

	protected DSFolderRelationStub getFolderRelationStub()
	{
		return this.folderRelationStub;
	}

	@Override
	public Folder createFolder(Folder folder, String sessionId, String fixTranId, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().createFolder(folder, sessionId, fixTranId, isCheckAcl);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#deleteFolder(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, boolean)
	 */
	@Override
	public void deleteFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		this.getFolderStub().deleteFolder(folderGuid, sessionId, isCheckAcl);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#getFolder(java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public Folder getFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().getFolder(folderGuid, sessionId, isCheckAcl);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#getFolder(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public Folder getFolder(String folderGuid, String userGuid, String groupGuid, String roleGuid, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().getFolder(folderGuid, userGuid, groupGuid, roleGuid, isCheckAcl);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#updateFolder(dyna.common.bean.data.system.Folder, java.lang.String,
	 * java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public Folder updateFolder(Folder folder, String sessionId, String fixTranId, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().updateFolder(folder, sessionId, fixTranId, isCheckAcl);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#listSubFolder(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, boolean)
	 */
	@Override
	public List<Folder> listSubFolder(String folderGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().listSubFolder(folderGuid, sessionId, isCheckAcl);
	}

	@Override
	public Folder getRootPrivateFolderByUser(String userGuid) throws ServiceRequestException
	{
		return this.getFolderStub().getRootPrivateFolderByUser(userGuid);
	}

	@Override
	public List<Folder> listAllSubFolder(String folderGuid) throws DynaDataException
	{
		return this.getFolderStub().listAllSubFolder(folderGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#listFolder(java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public List<Folder> listFolder(String foundationGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		return this.getFolderRelationStub().listFolder(foundationGuid, isCheckAcl, sessionId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#getMainDataNumberByLib(java.lang.String)
	 */
	@Override
	public Long getMainDataNumberByLib(String libraryGuid) throws ServiceRequestException
	{
		return this.getFolderStub().getDataNumberByLib(libraryGuid, true);
	}

	@Override
	public Long getVersionDataNumberByLib(String libraryGuid) throws ServiceRequestException
	{
		return this.getFolderStub().getDataNumberByLib(libraryGuid, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#hasRelation(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean hasRelation(String foundationGuid, String folderGuid) throws DynaDataException
	{
		return this.getFolderStub().hasRelation(foundationGuid, folderGuid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#listLibraryByUser(java.lang.String, boolean)
	 */
	@Override
	public List<Folder> listLibraryByUser(String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().listLibraryByUser(sessionId, isCheckAcl);
	}

	/**
	 * 查出指定用户的所有的有权限的库的根文件夹。
	 *
	 * @param sessionId
	 *            当前登录用户session id。
	 * @param isCheckAcl
	 *            是否检查权限。
	 * @return
	 * @throws DynaDataException
	 */
	@Override
	public List<Folder> listRootFolder(String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		return this.getFolderStub().listRootFolder(sessionId, isCheckAcl);
	}

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
	 * @param objectGuid
	 *            实例基本信息，不能为空，必须包含guid
	 * @param fromFolderGuid
	 *            源文件夹guid，可以为空
	 * @param toFolderGuid
	 *            目标文件夹guid，不能为空
	 * @param isCheckAclFrom
	 *            不能为空，是否校验权限（判断fromFolderGuid的相关权限）
	 * @param isCheckAclTo
	 *            不能为空，是否校验权限（判断toFolderGuid的相关权限）
	 * @param isUpdate
	 * @param isMaster
	 *            公共库转移数据时，需要把master的所有数据都转移到新文件夹（O21554/A.2-删除公共库文件夹报错，但文件夹中已无对象）
	 * @param sessionId
	 *            当前操作者的sessionId，不能为空
	 * @param fixTranId
	 * @throws ServiceRequestException
	 */
	@Override
	public String moveToFolder(ObjectGuid objectGuid, String fromFolderGuid, String toFolderGuid, boolean isCheckAclFrom, boolean isCheckAclTo, boolean isUpdate, boolean isMaster,
			String sessionId, String fixTranId) throws ServiceRequestException
	{
		return this.getFolderRelationStub().moveToFolder(objectGuid, fromFolderGuid, toFolderGuid, isCheckAclFrom, isCheckAclTo, isUpdate, isMaster, sessionId, fixTranId);
	}
}
