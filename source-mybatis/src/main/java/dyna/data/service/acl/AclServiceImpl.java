package dyna.data.service.acl;

import java.util.List;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.conf.ServiceDefinition;
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
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

public class AclServiceImpl extends DataRuleService implements AclService
{
	private InstanceAuthStub		instanceAuthStub		= null;

	private FolderAuthStub			folderAuthStub			= null;

	private PublicSearchAuthStub	publicSearchAuthStub	= null;

	private AuthCommonStub			authCommonStub			= null;

	public AclServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public InstanceAuthStub getInstanceAuthStub()
	{
		if (this.instanceAuthStub == null)
		{
			this.instanceAuthStub = new InstanceAuthStub(this.serviceContext, this);
		}
		return this.instanceAuthStub;
	}

	public AuthCommonStub getAuthCommonStub()
	{
		if (this.authCommonStub == null)
		{
			this.authCommonStub = new AuthCommonStub(this.serviceContext, this);
		}
		return this.authCommonStub;
	}

	public FolderAuthStub getFolderAuthStub()
	{
		if (this.folderAuthStub == null)
		{
			this.folderAuthStub = new FolderAuthStub(this.serviceContext, this);
		}
		return this.folderAuthStub;
	}

	public PublicSearchAuthStub getPublicSearchAuthStub()
	{
		if (this.publicSearchAuthStub == null)
		{
			this.publicSearchAuthStub = new PublicSearchAuthStub(this.serviceContext, this);
		}
		return this.publicSearchAuthStub;
	}

	@Override
	public boolean hasAuthority(ObjectGuid objectGuid, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().hasAuthority(objectGuid, authorityEnum, sessionId);
	}

	@Override
	public boolean hasAuthority(FoundationObject foundationObject, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().hasAuthority(foundationObject, authorityEnum, sessionId);
	}

	@Override
	public boolean hasAuthority(String foundationGuid, String className, AuthorityEnum authorityEnum, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().hasAuthority(foundationGuid, className, authorityEnum, sessionId);
	}

	@Override
	public boolean hasFolderAuthority(String folderGuid, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		return this.getFolderAuthStub().hasFolderAuthority(folderGuid, folderAuthorityEnum, userGuid, groupGuid, roleGuid);
	}

	@Override
	public List<FolderACLItem> listFolderAuth(String folderGuid) throws ServiceRequestException
	{
		return this.getFolderAuthStub().listFolderAuth(folderGuid);
	}

	@Override
	public List<FolderACLItem> listChangingLibFolderAuth(String folderGuid, boolean isextend) throws ServiceRequestException
	{
		return this.getFolderAuthStub().listChangingLibFolderAuth(folderGuid, isextend);
	}

	@Override
	public SaAclFolderLibConf getSaAclFolderLibConf(String folderGuid) throws DynaDataException
	{
		return this.getFolderAuthStub().getSaAclFolderLibConf(folderGuid);
	}

	@Override
	public boolean hasFolderAuthority(Folder folder, FolderAuthorityEnum folderAuthorityEnum, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		return this.hasFolderAuthority(folder.getGuid(), folderAuthorityEnum, userGuid, groupGuid, roleGuid);
	}

	@Override
	public FolderACLItem getAuthorityFolder(String folderGuid, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		return this.getFolderAuthStub().getFolderAuthority(folderGuid, userGuid, groupGuid, roleGuid);
	}

	@Override
	public String getCreateAuthority(FoundationObject foundationObject, String folderGuid, String sessionId, boolean isCreateAuth, boolean isRefAuth) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().getCreateAuthority(foundationObject, folderGuid, sessionId, isCreateAuth, isRefAuth);
	}

	@Override
	public String getTransferCheckoutAuthority(String toUserGuid, String instanceGuid, String className) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().getTransferCheckoutAuthority(toUserGuid, instanceGuid, className);
	}

	@Override
	public String getDelAuthority(ObjectGuid objectGuid, String folderGuid, String sessionId, boolean isDelRef) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().getDelAuthority(objectGuid, folderGuid, sessionId, isDelRef);
	}

	@Override
	public String getAuthority(String foundationGuid, String className, String userGuid, String groupGuid, String roleGuid) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().getAuthority(foundationGuid, className, userGuid, groupGuid, roleGuid);
	}

	@Override
	public boolean hasCreateAuthorityForClass(String classGuid, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceAuthStub().hasCreateAuthorityForClass(classGuid, sessionId);
	}

	@Override
	public boolean hasAuthorityForPublicSearch(String publicSearchGuid, PublicSearchAuthorityEnum publicSearchAuthorityEnum, String sessionId) throws ServiceRequestException
	{
		return this.getPublicSearchAuthStub().hasAuthorityForPublicSearch(publicSearchGuid, publicSearchAuthorityEnum, sessionId);
	}

	@Override
	public boolean hasAuthorityForSharedFolder(String sharedFolderGuid, ShareFolderAuthorityEnum shareFolderAuthorityEnum, String sessionId)
	{
		return true;
	}

	@Override
	public Map<String, ACLSubject> listAllSubjectWithTree()
	{
		return this.getInstanceAuthStub().listAllSubjectWithTree();
	}

	@Override
	public void loadACLTreeToCache() throws ServiceRequestException
	{
		this.getInstanceAuthStub().loadACLTreeOfLibraryMap();
	}
}
