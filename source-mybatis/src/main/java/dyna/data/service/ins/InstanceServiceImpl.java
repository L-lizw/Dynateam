package dyna.data.service.ins;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InstanceServiceImpl extends DataRuleService implements InstanceService
{
	private DSIterationStub			iterationStub;
	private DSCheckOutStub			checkOutStub;
	private DSInstanceUpdateStub	foundationStub;
	private NewRevisionRuleStub		revisionRuleStub;
	private DSCancelCheckoutStub	cancelCheckout;
	private DSCheckInStub			checkInStub;
	private DSInstanceGetStub		instanceGetStub;
	private DSQuickQueryStub		quickQueryStub;
	private DSConditionQueryStub	conditionQuery;

	public InstanceServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	protected DSIterationStub getIterationStub()
	{
		return this.iterationStub;
	}

	protected DSCheckOutStub getCheckoutStub()
	{
		return this.checkOutStub;
	}

	public DSInstanceUpdateStub getFoundationStub()
	{
		return this.foundationStub;
	}

	public NewRevisionRuleStub getRevisionRuleStub()
	{
		return this.revisionRuleStub;
	}

	public DSCheckInStub getCheckInStub()
	{
		return this.checkInStub;
	}

	public DSCancelCheckoutStub getCancelCheckoutStub()
	{
		return this.cancelCheckout;
	}

	protected DSInstanceGetStub getInstanceGetStub()
	{
		return this.instanceGetStub;
	}

	protected DSQuickQueryStub getQuickQueryStub()
	{
		return this.quickQueryStub;
	}

	protected DSConditionQueryStub getConditionQueryStub()
	{
		return this.conditionQuery;
	}

	@Override
	public List<FoundationObject> listIteration(ObjectGuid objectGuid, Integer iterationId, SearchCondition searchCondition, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException
	{
		return this.getIterationStub().listIteration(objectGuid, iterationId, searchCondition, isCheckAcl, sessionId);
	}

	@Override
	public FoundationObject getFoundationObject(ObjectGuid objectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceGetStub().get(objectGuid, isCheckAcl, sessionId);
	}

	@Override
	public FoundationObject getSystemFieldInfo(String guid, String classGuidOrClassName, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceGetStub().getSystemFieldInfo(guid, classGuidOrClassName, isCheckAcl, sessionId);
	}

	@Override
	public FoundationObject getWipSystemFieldInfoByMaster(String masterGuid, String classGuidOrClassName, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceGetStub().getWipSystemFieldInfoByMaster(masterGuid, classGuidOrClassName, isCheckAcl, sessionId);
	}

	@Override
	public FoundationObject getSystemFieldInfoByMasterContext(String masterGuid, String classGuidOrClassName, DataRule dataRule, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException
	{
		return this.getInstanceGetStub().getSystemFieldInfoByMasterContext(masterGuid, classGuidOrClassName, dataRule, isCheckAcl, sessionId);
	}

	@Override
	public List<FoundationObject> query(SearchCondition searchCondition, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		return this.getConditionQueryStub().query(searchCondition, false, isCheckAcl, sessionId);
	}

	@Override
	public List<FoundationObject> queryByGuidList(String className, List<String> guidList, String sessionId) throws ServiceRequestException
	{
		return this.getConditionQueryStub().query(className, guidList, sessionId);
	}

	@Override
	public List<FoundationObject> querySubscription(SearchCondition searchCondition, boolean isCheckAcl, String sessionId, String subscriptionFolderGuid)
			throws ServiceRequestException
	{
		return null;
		// return this.subscriptionUnit.querySubscription(searchCondition, subscriptionFolderGuid, isCheckAcl,
		// sessionId);
	}

	@Override
	public FoundationObject queryForClassification(String foundationObjectGuid, String iterationId, String itemGuid, String sessionId) throws ServiceRequestException
	{
		return this.getInstanceGetStub().getClassification(foundationObjectGuid, iterationId, itemGuid, sessionId);
	}

	@Override
	public FoundationObject getMaster(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getInstanceGetStub().getMaster(objectGuid);
	}

	@Override
	public boolean hasMutliWorkVersion(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getInstanceGetStub().hasMutliWorkVersion(objectGuid);
	}

	@Override
	public List<FoundationObject> quickQuery(String searchKey, int rowCntPerPate, int pageNum, boolean caseSensitive, boolean isEquals, boolean isOnlyId,
			List<String> boNameList, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		return this.getQuickQueryStub().quickQuery(searchKey, rowCntPerPate, pageNum, caseSensitive, isEquals, isOnlyId, boNameList, isCheckAcl, sessionId);
	}

	@Override
	public FoundationObject queryByTime(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		return this.getQuickQueryStub().queryByTime(objectGuid, ruleTime);
	}

	@Override
	public void cancelCheckout(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getCancelCheckoutStub().cancelCheckout(foundationObject, isCheckAcl, sessionId, fixTranId);
	}

	@Override
	public FoundationObject checkin(FoundationObject foundationObject, boolean isOwnerOnly, String sessionId, String fixTranId) throws ServiceRequestException
	{
		return this.getCheckInStub().checkin(foundationObject, isOwnerOnly, sessionId, fixTranId);
	}

	@Override
	public FoundationObject checkout(FoundationObject foundationObject, String checkOutUser, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		return this.getCheckoutStub().checkout(foundationObject, checkOutUser, isCheckAcl, sessionId, fixTranId);
	}

	@Override
	public FoundationObject create(FoundationObject foundationObject, String originalFoundationGuid, boolean isCheckAcl, String sessionId, String fixTranId)
			throws ServiceRequestException
	{
		return this.getFoundationStub().create(foundationObject, originalFoundationGuid, isCheckAcl, sessionId, fixTranId, false);
	}

	@Override
	public void delete(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().delete(foundationObject, isCheckAcl, sessionId, fixTranId);
	}

	@Override
	public FoundationObject transferCheckout(FoundationObject foundationObject, String toUserGuid, boolean isCheckAcl, boolean isOwnerOnly, String sessionId, String fixTranId)
			throws ServiceRequestException
	{
		return this.getFoundationStub().transferCheckout(foundationObject, toUserGuid, isCheckAcl, isOwnerOnly, sessionId, fixTranId);
	}

	@Override
	public void rollbackIteration(ObjectGuid objectGuid, Integer iterationId, boolean isOwnerOnly, String sessionId) throws ServiceRequestException
	{
		this.getIterationStub().rollbackIteration(objectGuid, iterationId, isOwnerOnly, sessionId);
	}

	@Override
	public void cancelObsolete(String guid, String classGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		this.getFoundationStub().cancelObsolete(guid, classGuid, isCheckAcl, sessionId);
	}

	@Override
	public void obsolete(ObjectGuid foundationObjectGuid, boolean isFromWF, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().obsolete(foundationObjectGuid, isFromWF, isCheckAcl, sessionId, fixTranId);
	}

	@Override
	public void changeStatus(SystemStatusEnum from, SystemStatusEnum to, ObjectGuid foundationObject, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		this.getFoundationStub().changeStatus(from, to, foundationObject, isCheckAcl, sessionId);
	}

	@Override
	public void save(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, String revisionId, Date updatetime, LifecyclePhaseInfo fromLifeCyclePhase,
			LifecyclePhaseInfo toLifeCyclePhase, String masterName, SystemStatusEnum status, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().save(objectGuid, ownerUserGuid, ownerGroupGuid, revisionId, updatetime, fromLifeCyclePhase, toLifeCyclePhase, masterName, status, isCheckAcl,
				sessionId, fixTranId);
	}

	@Override
	public FoundationObject save(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId, boolean isCheckCheckout) throws ServiceRequestException
	{
		return this.getFoundationStub().save(foundationObject, isCheckAcl, sessionId, fixTranId, false, isCheckCheckout, true);
	}

	@Override
	public void saveOwner(String foundationGuid, String classGuid, String ownerUserGuid, String ownerGroupGuid, String sessionId) throws ServiceRequestException
	{
		this.getFoundationStub().saveOwner(foundationGuid, classGuid, ownerUserGuid, ownerGroupGuid, sessionId);
	}

	@Override
	public void deleteMaster(String masterGuid, String classGuid, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().deleteMaster(masterGuid, classGuid, isCheckAcl, sessionId, fixTranId);
	}

	@Override
	public String getNewRevisionId(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getRevisionRuleStub().getNextRevisionId(objectGuid.getGuid(), objectGuid.getClassName());
	}

	@Override
	public String getFirstRevisionId(int startRevisionIdSequence) throws ServiceRequestException
	{
		return this.getRevisionRuleStub().getNextRevisionId(startRevisionIdSequence);
	}

	@Override
	public FoundationObject saveForIgnoreUpdateTime(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		return this.getFoundationStub().save(foundationObject, isCheckAcl, sessionId, fixTranId, false, false, false);
	}

	@Override
	public void release(ObjectGuid foundationObjectGuid, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().release(foundationObjectGuid, sessionId, fixTranId);
	}

	@Override
	public void copyFileOnlyRecord(FoundationObject destObject, FoundationObject srcObject, String sessionId, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().copyFileOnlyRecord(destObject, srcObject, sessionId, fixTranId);
	}

	@Override
	public void changePhase(String foundationGuid, String foundationClassGuid, String lifecyclePhaseGuid, boolean isCheckAuth, String fixTranId) throws ServiceRequestException
	{
		this.getFoundationStub().changePhase(foundationGuid, foundationClassGuid, lifecyclePhaseGuid, isCheckAuth, fixTranId);
	}

	/**
	 * 保存实例的file信息
	 *
	 * @param guid
	 *            要更新的revision的guid，不能为空
	 * @param classGuid
	 * @param sessionId
	 * @param fileGuid
	 *            文件的guid
	 * @param fileName
	 *            文件的name
	 * @param fileType
	 *            文件的type
	 * @param MD5
	 * @throws ServiceRequestException
	 */
	@Override
	public void saveFile(String guid, String classGuid, String sessionId, String fileGuid, String fileName, String fileType, String MD5) throws ServiceRequestException
	{
		this.getFoundationStub().saveFile(guid, classGuid, sessionId, fileGuid, fileName, fileType, MD5);
	}

	/**
	 * 删除对象相关数据，包括：1、0表以外的其他表
	 * 2、作为end2时的结构数据
	 * 3、作为end1时的关系数据，包括关系master数据，包含0表在内的所有关系数据，以及结构数据
	 *
	 * @param objectGuid
	 * @param exceptionParameter
	 * @param fixTranId
	 * @throws ServiceRequestException
	 */
	@Override
	public void deleteReferenceData(ObjectGuid objectGuid, String sessionId) throws ServiceRequestException
	{
		this.getFoundationStub().deleteReferenceData(objectGuid, sessionId);
	}

	/**
	 * 设置是否已导入至ERP
	 *
	 * @param guid
	 *            实例的guid
	 * @param className
	 * @param isExportToERP
	 *            是否已导入至ERP
	 * @param isCheckAcl
	 *            预留，暂未用到
	 * @param sessionId
	 *            预留，暂未用到
	 * @throws DynaDataException
	 */
	@Override
	public void setIsExportToERP(String guid, String className, boolean isExportToERP, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		this.getFoundationStub().setIsExportToERP(guid, className, isExportToERP, isCheckAcl, sessionId);
	}
}
