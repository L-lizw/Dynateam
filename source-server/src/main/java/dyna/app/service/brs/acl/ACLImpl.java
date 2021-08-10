/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLImpl Access Control List Service implementation
 * Wanglei 2010-7-30
 */
package dyna.app.service.brs.acl;

import java.lang.reflect.Method;
import java.util.List;

import dyna.app.service.BusinessRuleService;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.aas.Group;
import dyna.common.dto.acl.ACLFunctionItem;
import dyna.common.dto.acl.ACLFunctionObject;
import dyna.common.dto.acl.ACLItem;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.dto.acl.FolderACLItem;
import dyna.common.dto.acl.PublicSearchACLItem;
import dyna.common.dto.acl.ShareFolderACLItem;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.AccessTypeEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.ModulEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.SetUtils;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.ACL;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EDAP;
import dyna.net.service.brs.EMM;

/**
 * Access Control List Service implementation
 * 
 * @author Wanglei
 * 
 */
public class ACLImpl extends BusinessRuleService implements ACL
{
	private static boolean initialized = false;

	private static synchronized void syncInit()
	{
		// initialize only once.
		if (initialized)
		{
			return;
		}

		initialized = true;

		ACLSubjectStub.init();
	}

	private ACLStub					aclStub					= null;
	private ACLSubjectStub			aclSubjectStub			= null;
	private ACLItemStub				aclItemStub				= null;
	private FolderACLStub			folderACLStub			= null;
	private SharedFolderACLStub		sharedFolderACLStub		= null;

	private PublicSearchACLStub		publicSearchACLStub		= null;
	private FoundationACLStub		foundationACLStub		= null;

	private ACLFunctionObjectStub	aclFunctionObjectStub	= null;
	private ACLFunctionItemStub		aclFunctionItemStub		= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	protected void init()
	{
		syncInit();
	}

	/**
	 * 检查调用者是否属于管理组
	 * 
	 * @param signature
	 * @throws AuthorizeException
	 */
	private void administrativeAuthorize(UserSignature signature) throws AuthorizeException
	{
		String groupGuid = signature.getLoginGroupGuid();
		try
		{
			Group group = this.getAAS().getGroup(groupGuid);
			if (group == null || !group.isAdminGroup())
			{
				throw new AuthorizeException("accessible for administrative group only");
			}
		}
		catch (ServiceRequestException e)
		{
			throw new AuthorizeException("accessible for administrative group only", e.fillInStackTrace());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		super.authorize(method, args);

		if (!(this.getSignature() instanceof UserSignature))
		{
			throw new AuthorizeException("permission denied");
		}

		UserSignature signature = (UserSignature) this.getSignature();

		String methodName = method.getName();
		String userId = null;
		if ("listACLItemForObjectByUser".equals(methodName) || "getACLItemForObjectByUser".equals(methodName))
		{
			userId = (String) args[1];
			try
			{
				this.singleAuthorize(signature, userId);
				return;
			}
			catch (AuthorizeException e)
			{
			}
		}

		if ("batchDealWithACL".equals(methodName) || "getACLItem".equals(methodName) || "getACLSubject".equals(methodName) || "listACLItemBySubject".equals(methodName)
				|| "listRootACLSubjectByLib".equals(methodName) || "listSubACLSubject".equals(methodName))
		{
			this.administrativeAuthorize(signature);
			return;
		}
	}

	/**
	 * @return the aclStub
	 */
	protected ACLStub getAclStub()
	{
		if (this.aclStub == null)
		{
			this.aclStub = new ACLStub(this.serviceContext, this);
		}
		return this.aclStub;
	}

	/**
	 * @return the aclItemStub
	 */
	protected ACLItemStub getAclItemStub()
	{
		if (this.aclItemStub == null)
		{
			this.aclItemStub = new ACLItemStub(this.serviceContext, this);
		}
		return this.aclItemStub;
	}

	/**
	 * @return the aclSubjectStub
	 */
	protected ACLSubjectStub getAclSubjectStub()
	{
		if (this.aclSubjectStub == null)
		{
			this.aclSubjectStub = new ACLSubjectStub(this.serviceContext, this);
		}
		return this.aclSubjectStub;
	}

	/**
	 * @return the aclStub
	 */
	protected FoundationACLStub getFoundationACLStub()
	{
		if (this.foundationACLStub == null)
		{
			this.foundationACLStub = new FoundationACLStub(this.serviceContext, this);
		}
		return this.foundationACLStub;
	}

	/**
	 * @return the aclItemStub
	 */
	protected ACLFunctionItemStub getAclFunctionItemStub()
	{
		if (this.aclFunctionItemStub == null)
		{
			this.aclFunctionItemStub = new ACLFunctionItemStub(this.serviceContext, this);
		}
		return this.aclFunctionItemStub;
	}

	/**
	 * @return the aclSubjectStub
	 */
	protected ACLFunctionObjectStub getAclFunctionObjectStub()
	{
		if (this.aclFunctionObjectStub == null)
		{
			this.aclFunctionObjectStub = new ACLFunctionObjectStub(this.serviceContext, this);
		}
		return this.aclFunctionObjectStub;
	}

	/**
	 * @return the publicSearchACLStub
	 */
	protected PublicSearchACLStub getPublicSearchACLStub()
	{
		if (this.publicSearchACLStub == null)
		{
			this.publicSearchACLStub = new PublicSearchACLStub(this.serviceContext, this);
		}
		return this.publicSearchACLStub;
	}

	/**
	 * @return the folderACLStub
	 */
	public FolderACLStub getFolderACLStub()
	{
		if (this.folderACLStub == null)
		{
			this.folderACLStub = new FolderACLStub(this.serviceContext, this);
		}
		return this.folderACLStub;
	}

	/**
	 * @return the sharedFolderACLStub
	 */
	public SharedFolderACLStub getSharedFolderACLStub()
	{
		if (this.sharedFolderACLStub == null)
		{
			this.sharedFolderACLStub = new SharedFolderACLStub(this.serviceContext, this);
		}
		return this.sharedFolderACLStub;
	}

	protected synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	protected synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);

		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	protected synchronized EDAP getEDAP() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EDAP.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);

		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	@Override
	public void batchDealWithACL(List<ACLItem> addAclItemList, List<ACLSubject> addAclSubjectList, List<ACLItem> updateAclItemList, List<ACLSubject> updateAclSubjectList,
			List<ACLItem> deleteAclItemList, List<ACLSubject> deleteAclSubjectList) throws ServiceRequestException
	{
		this.getAclStub().batchDealWithACL(addAclItemList, addAclSubjectList, updateAclItemList, updateAclSubjectList, deleteAclItemList, deleteAclSubjectList);
	}

	@Override
	public FolderACLItem getACLFolderItemByFolder(String folderGuid) throws ServiceRequestException
	{
		return this.getFolderACLStub().getACLFolderItemByFolder(folderGuid);
	}

	@Override
	public FolderACLItem getACLFolderItemByFolder(String folderGuid, AccessTypeEnum accessTypeEnum, String objectGuid) throws ServiceRequestException
	{
		return this.getFolderACLStub().getACLFolderItemByFolder(folderGuid, accessTypeEnum, objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#getACLItem(java.lang.String)
	 */
	@Override
	public ACLItem getACLItem(String aclItemGuid) throws ServiceRequestException
	{
		return (ACLItem) this.getAclItemStub().getACLItem(ACLItem.class, aclItemGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#getACLItemForObjectByUser(dyna.common.bean.data.ObjectGuid, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ACLItem getACLItemForObjectByUser(ObjectGuid objectGuid, String userId, String groupId, String roleId) throws ServiceRequestException
	{
		return this.getAclItemStub().getACLItemForObjectByUser(objectGuid, userId, groupId, roleId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#getACLItemForObject(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public ACLItem getACLItemForObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		UserSignature userSignature = this.getUserSignature();
		return this.getAclItemStub().getACLItemForObjectByUser(objectGuid, userSignature.getUserId(), userSignature.getLoginGroupId(), userSignature.getLoginRoleId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#getACLSubject(java.lang.String)
	 */
	@Override
	public ACLSubject getACLSubject(String aclSubjectGuid) throws ServiceRequestException
	{
		return this.getAclSubjectStub().getACLSubject(aclSubjectGuid);
	}

	@Override
	public List<FolderACLItem> listACLFolderItemByFolder(String folderGuid) throws ServiceRequestException
	{
		this.getFolderACLStub().hasUserAuthorize4Lib(folderGuid);
		return this.getFolderACLStub().listACLFolderItemByFolder(folderGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#listACLItem()
	 */
	@Override
	public List<ACLItem> listACLItemBySubject(String aclSubjectGuid) throws ServiceRequestException
	{
		return this.getAclItemStub().listACLItemBySubject(aclSubjectGuid);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see dyna.net.service.brs.ACL#listACLItemForObjectByUser(dyna.common.bean.data.ObjectGuid, java.lang.String,
	// * java.lang.String)
	// */
	// @Override
	// public List<ACLItem> listACLItemForObjectByUser(ObjectGuid objectGuid, String userId, String groupId, String
	// roleId)
	// throws ServiceRequestException
	// {
	// return this.getAclItemStub().listACLItemForObjectByUser(objectGuid, userId, groupId, roleId);
	// }

	@Override
	public List<ShareFolderACLItem> listACLSharedFolderItemByFolder(String folderGuid) throws ServiceRequestException
	{
		return this.getSharedFolderACLStub().listACLSharedFolderItemByFolder(folderGuid);
	}

	/**
	 * 检查调用者是否用户本人
	 * 
	 * @param signature
	 * @param userId
	 * @throws AuthorizeException
	 */
	private void singleAuthorize(UserSignature signature, String userId) throws AuthorizeException
	{
		if (!signature.getUserId().equals(userId))
		{
			throw new AuthorizeException("accessible for administrative group or user itself");
		}
	}

	@Override
	public List<ACLSubject> listRootACLSubjectByLIB(String libraryGuid) throws ServiceRequestException
	{
		this.getFolderACLStub().hasUserAuthorize4Lib(libraryGuid);
		return this.getAclSubjectStub().listRootACLSubjectByLIB(libraryGuid);
	}

	@Override
	public List<ACLSubject> listSubACLSubject(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException
	{
		return this.getAclSubjectStub().listSubACLSubject(aclSubjectGuid, isCascade);
	}

	@Override
	public void batchDealFolderACLItem(List<FolderACLItem> saveACLItemList, String folderGuid, boolean isExtend, boolean isMandatoryWriteOver, boolean isMandatoryAll)
			throws ServiceRequestException
	{
		this.getFolderACLStub().batchDealFolderACLItem(saveACLItemList, folderGuid, isExtend, isMandatoryWriteOver, isMandatoryAll);
	}

	@Override
	public List<FolderACLItem> listACLFolderItemByFolderTemporary(String folderGuid, boolean isExdtend) throws ServiceRequestException
	{
		this.getFolderACLStub().hasUserAuthorize4Lib(folderGuid);
		return this.getFolderACLStub().listACLFolderItemByFolderTemporary(folderGuid, isExdtend);
	}

	@Override
	public void batchDealLibACLItem(List<FolderACLItem> saveACLItemList, String folderGuid) throws ServiceRequestException
	{
		this.getFolderACLStub().hasUserAuthorize4Lib(folderGuid);
		this.getFolderACLStub().batchDealLibACLItem(saveACLItemList, folderGuid);
	}

	@Override
	public List<FolderACLItem> listACLLibItemByLib(String folderGuid) throws ServiceRequestException
	{
		this.getFolderACLStub().hasUserAuthorize4Lib(folderGuid);
		return this.getFolderACLStub().listACLLibItemByLib(folderGuid);
	}

	@Override
	public boolean hasFolderAuthority(String folderGuid, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		return this.getFolderACLStub().hasFolderAuthority(folderGuid, folderAuthorityEnum, userGuid, groupGuid, roleGuid);
	}

	@Override
	public void batchDealWithACL4Lib(List<ACLItem> addAclItemList, List<ACLSubject> addAclSubjectList, List<ACLItem> updateAclItemList, List<ACLSubject> updateAclSubjectList,
			List<ACLItem> deleteAclItemList, List<ACLSubject> deleteAclSubjectList) throws ServiceRequestException
	{
		this.getAclStub().batchDealWithACL(addAclItemList, addAclSubjectList, updateAclItemList, updateAclSubjectList, deleteAclItemList, deleteAclSubjectList);
	}

	@Override
	public FolderACLItem getACLItem4Lib(String aclItemGuid) throws ServiceRequestException
	{
		FolderACLItem acl = (FolderACLItem) this.getAclItemStub().getACLItem(FolderACLItem.class, aclItemGuid);
		if (acl != null)
		{
			this.getFolderACLStub().hasUserAuthorize4Lib(acl.getFolderGuid());
		}
		return acl;
	}

	@Override
	public ACLSubject getACLSubject4Lib(String aclSubjectGuid) throws ServiceRequestException
	{
		ACLSubject acl = this.getAclSubjectStub().getACLSubject(aclSubjectGuid);
		if (acl != null)
		{
			this.getFolderACLStub().hasUserAuthorize4Lib(acl.getLibraryGuid());
		}
		return acl;
	}

	@Override
	public List<ACLItem> listACLItemBySubject4Lib(String aclSubjectGuid) throws ServiceRequestException
	{
		List<ACLItem> aclList = this.getAclItemStub().listACLItemBySubject(aclSubjectGuid);
		return aclList;
	}

	@Override
	public List<ACLSubject> listRootACLSubjectByLib4Lib(String libraryGuid) throws ServiceRequestException
	{
		this.getFolderACLStub().hasUserAuthorize4Lib(libraryGuid);
		return this.getAclSubjectStub().listRootACLSubjectByLIB(libraryGuid);
	}

	@Override
	public List<ACLSubject> listSubACLSubject4Lib(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException
	{
		List<ACLSubject> aclList = this.getAclSubjectStub().listSubACLSubject(aclSubjectGuid, isCascade);
		if (!SetUtils.isNullList(aclList))
		{
			this.getFolderACLStub().hasUserAuthorize4Lib(aclList.get(0).getLibraryGuid());
		}
		return aclList;
	}

	@Override
	public void batchDealSharedFolderACLItem(List<ShareFolderACLItem> saveACLItemList, String folderGuid) throws ServiceRequestException
	{
		this.getSharedFolderACLStub().batchDealSharedFolderACLItem(saveACLItemList, folderGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#deletePublicSearchACLItem(java.lang.String)
	 */
	@Override
	public void deletePublicSearchACLItem(String publicSearchGuid) throws ServiceRequestException
	{
		this.getPublicSearchACLStub().deletePublicSearchACLItem(publicSearchGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#listPublicSearchACLItemByPreSearchGuid(java.lang.String)
	 */
	@Override
	public List<PublicSearchACLItem> listPublicSearchACLItemByPreSearchGuid(String publicSearchGuid) throws ServiceRequestException

	{
		return this.getPublicSearchACLStub().listPublicSearchACLItemByPreSearchGuid(publicSearchGuid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#savePublicSearchACLItem(java.util.List, java.lang.String)
	 */
	@Override
	public void savePublicSearchACLItem(List<PublicSearchACLItem> aclItemList, String publicSearchGuid) throws ServiceRequestException
	{
		this.getPublicSearchACLStub().savePublicSearchACLItem(aclItemList, publicSearchGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.ACL#isFoundationCreateACL(java.lang.String)
	 */
	@Override
	public boolean hasFoundationCreateACL(String boName) throws ServiceRequestException
	{
		return this.getFoundationACLStub().hasFoundationCreateACL(boName);
	}

	@Override
	public boolean hasFileDownloadACL(ObjectGuid objectGuid) throws ServiceRequestException
	{

		return this.getFoundationACLStub().hasFileDownloadACL(objectGuid);
	}
	
	@Override
	public List<ACLFunctionItem> listACLFunctionItemByFunctionObject(String aclFunctionObjectGuid) throws ServiceRequestException
	{
		return this.getAclFunctionItemStub().listACLFunctionItemByFunctionObject(aclFunctionObjectGuid);
	}
	
	@Override
	public List<ACLFunctionObject> listRootACLFunctionObject() throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ACLFunctionObject> listSubACLFunctionObject(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException
	{
		return this.getAclFunctionObjectStub().listSubACLFunctionObject(aclSubjectGuid);
	}
	
	@Override
	public void batchDealWithFunctionACL(List<ACLFunctionItem> addAclItemList, List<ACLFunctionObject> addAclSubjectList, List<ACLFunctionItem> updateAclItemList,
			List<ACLFunctionObject> updateAclSubjectList, List<ACLFunctionItem> deleteAclItemList, List<ACLFunctionObject> deleteAclSubjectList) throws ServiceRequestException
	{
		this.getAclFunctionObjectStub().batchDealWithACL(addAclItemList, addAclSubjectList, updateAclItemList, updateAclSubjectList, deleteAclItemList, deleteAclSubjectList);
	}

	@Override
	public PermissibleEnum getFunctionPermissionByUser(ModulEnum position, String functionName, String userGuid, String roleGuid, String groupGuid) throws ServiceRequestException
	{
		return this.getAclFunctionItemStub().getFunctionPermissionByUser(position, functionName, userGuid, roleGuid, groupGuid);
	}
}
