/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOASImpl
 * Wanglei 2010-7-8
 */
package dyna.app.service.brs.boas;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;

import dyna.app.core.track.annotation.Tracked;
import dyna.app.core.track.impl.TRFoundationImpl;
import dyna.app.core.track.impl.TRSearchConditionImpl;
import dyna.app.service.BusinessRuleService;
import dyna.app.service.brs.boas.tracked.TROpenObjectImpl;
import dyna.app.service.brs.boas.tracked.TRSaveRelationByTemplateImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.extra.OpenInstanceModel;
import dyna.common.dto.BIViewHis;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DataRule;
import dyna.common.dto.Folder;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.ACL;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.BRM;
import dyna.net.service.brs.CAD;
import dyna.net.service.brs.DCR;
import dyna.net.service.brs.DSS;
import dyna.net.service.brs.EDAP;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.EOSS;
import dyna.net.service.brs.FTS;
import dyna.net.service.brs.LIC;
import dyna.net.service.brs.POS;
import dyna.net.service.brs.PPMS;
import dyna.net.service.brs.SLC;
import dyna.net.service.brs.SMS;
import dyna.net.service.brs.WFI;
import dyna.net.service.das.MSRM;

/**
 * Business Object Access Service implementation
 * 
 * @author Wanglei
 * 
 */
public class BOASImpl extends BusinessRuleService implements BOAS
{
	private static boolean			initialized				= false;

	private CheckInStub				checkInStub				= null;
	private CheckOutStub			checkOutStub			= null;
	private CancelCheckOutStub		cancelCheckOutStub		= null;
	private TransferCheckOutStub	transferCheckOutStub	= null;

	private FoundationStub			foundationStub			= null;
	private FFolderStub				fFolderStub				= null;
	private FRevisionStub			fRevisionStub			= null;
	private FSaverStub				fSaverStub				= null;
	private FUIStub					fUIStub					= null;
	private FUpdaterStub			fUpdaterStub			= null;
	private IterationStub			iterationStub			= null;

	private RelationStub			relationStub			= null;
	private RelationLinkStub		relationLinkStub		= null;
	private RelationUnlinkStub		relationUnlinkStub		= null;
	private StructureStub			structureStub			= null;
	private ClassificationStub		classificationStub		= null;
	private RouteStub				routeStub				= null;

	@Deprecated
	@Override
	public String allocateUniqueId(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getFSaverStub().allocateUniqueId(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(dyna.net.security.signature.Signature)
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		super.authorize(method, args);
	}

	@Override
	public void batchLink(ObjectGuid viewObjectGuid, List<ObjectGuid> end2FoundationObjectGuidList, List<StructureObject> structureObjectList) throws ServiceRequestException
	{
		this.getRelationLinkStub().batchLink(viewObjectGuid, end2FoundationObjectGuidList, structureObjectList, null);
	}

	protected synchronized ACL getACL() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(ACL.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized WFI getWFI() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized FTS getFTS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(FTS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized PPMS getPPMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(PPMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized DCR getDCR() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DCR.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized SMS getSMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(SMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized LIC getLIC() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(LIC.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

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

	protected synchronized BRM getBRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized DSS getDSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(DSS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
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

	protected synchronized CAD getCAD() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(CAD.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized EMM getEMM() throws ServiceRequestException
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

	protected synchronized EOSS getEOSS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EOSS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public CancelCheckOutStub getCancelCheckOutStub()
	{
		if (this.cancelCheckOutStub == null)
		{
			this.cancelCheckOutStub = new CancelCheckOutStub(this.serviceContext, this);
		}
		return this.cancelCheckOutStub;
	}

	public ClassificationStub getClassificationStub()
	{
		if (this.classificationStub == null)
		{
			this.classificationStub = new ClassificationStub(this.serviceContext, this);
		}
		return this.classificationStub;
	}

	public CheckInStub getCheckInStub()
	{
		if (this.checkInStub == null)
		{
			this.checkInStub = new CheckInStub(this.serviceContext, this);
		}
		return this.checkInStub;
	}

	public CheckOutStub getCheckOutStub()
	{
		if (this.checkOutStub == null)
		{
			this.checkOutStub = new CheckOutStub(this.serviceContext, this);
		}
		return this.checkOutStub;
	}

	private RouteStub getRouteStub()
	{
		if (routeStub == null)
		{
			routeStub = new RouteStub(serviceContext, this);
		}
		return routeStub;
	}

	/**
	 * @return the fFolderStub
	 */
	public FFolderStub getFFolderStub()
	{
		if (this.fFolderStub == null)
		{
			this.fFolderStub = new FFolderStub(this.serviceContext, this);
		}

		return this.fFolderStub;
	}

	/**
	 * @return the foundationStub
	 */
	public FoundationStub getFoundationStub()
	{
		if (this.foundationStub == null)
		{
			this.foundationStub = new FoundationStub(this.serviceContext, this);
		}

		return this.foundationStub;
	}

	/**
	 * @return the fRevisionStub
	 */
	public FRevisionStub getFRevisionStub()
	{
		if (this.fRevisionStub == null)
		{
			this.fRevisionStub = new FRevisionStub(this.serviceContext, this);
		}
		return this.fRevisionStub;
	}

	/**
	 * @return the fSaverStub
	 */
	public FSaverStub getFSaverStub()
	{
		if (this.fSaverStub == null)
		{
			this.fSaverStub = new FSaverStub(this.serviceContext, this);
		}
		return this.fSaverStub;
	}

	/**
	 * @return the fUIStub
	 */
	public FUIStub getFUIStub()
	{
		if (this.fUIStub == null)
		{
			this.fUIStub = new FUIStub(this.serviceContext, this);
		}
		return this.fUIStub;
	}

	/**
	 * @return the fUpdaterStub
	 */
	public FUpdaterStub getFUpdaterStub()
	{
		if (this.fUpdaterStub == null)
		{
			this.fUpdaterStub = new FUpdaterStub(this.serviceContext, this);
		}
		return this.fUpdaterStub;
	}

	@Override
	public void batchUnlink(List<StructureObject> structureObjectList) throws ServiceRequestException
	{
		this.getRelationUnlinkStub().batchUnlink(structureObjectList);
	}

	@Tracked(description = TrackedDesc.CHECK_OUT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject checkOut(FoundationObject foundationObject) throws ServiceRequestException
	{
		String checkOutUserGuid = this.getOperatorGuid();
		return this.getCheckOutStub().checkOut(foundationObject, checkOutUserGuid, Constants.isSupervisor(true, this));
	}

	@Tracked(description = TrackedDesc.CREATE_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject createObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		// String operatorGuid = this.getUserSignature().getUserGuid();
		// String groupGuid = this.getUserSignature().getLoginGroupGuid();
		// return this.getFSaverStub().createObject(foundationObject, null, groupGuid, operatorGuid,
		// Constants.isSupervisor(true, this), true);

		return this.getFSaverStub().createObject(foundationObject, true);
	}

	@Tracked(description = TrackedDesc.DEL_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public void deleteObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		this.getFoundationStub().deleteObject(foundationObject);
	}

	@Tracked(description = TrackedDesc.DEL_RELATION, renderer = TRFoundationImpl.class)
	@Override
	public void deleteRelation(ViewObject relation) throws ServiceRequestException
	{
		this.getRelationStub().deleteRelation(relation);
	}

	@Override
	public String getFoundationObjectViewValue(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getFoundationObjectViewValue(objectGuid);
	}

	@Override
	public String getInitRevisionId(int startRevisionIdSequence) throws ServiceRequestException
	{
		return this.getFRevisionStub().getInitRevisionId(startRevisionIdSequence);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#getObject(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.system.Folder)
	 */
	@Override
	public FoundationObject getObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getObject(objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#getObject(dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Override
	public FoundationObject getObject(ObjectGuid objectGuid, String sharedFolderGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getObject(objectGuid, sharedFolderGuid);
	}

	@Override
	public FoundationObject getObjectByGuid(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getObjectByGuid(objectGuid, Constants.isSupervisor(true, this));
	}

	@Override
	public FoundationObject getObjectOfProcrtByGuid(String guid, String classGuid, String procrtGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getObjectByGuid(new ObjectGuid(classGuid, null, guid, null), procrtGuid == null ? Constants.isSupervisor(true, this) : false);
	}

	protected synchronized POS getPOS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(POS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	@Override
	public ViewObject getRelation(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getRelationStub().getRelation(objectGuid);
	}

	/**
	 * @return the relationLinkStub
	 */
	public RelationLinkStub getRelationLinkStub()
	{
		if (this.relationLinkStub == null)
		{
			this.relationLinkStub = new RelationLinkStub(this.serviceContext, this);
		}
		return this.relationLinkStub;
	}

	/**
	 * @return the relationStub
	 */
	public RelationStub getRelationStub()
	{
		if (this.relationStub == null)
		{
			this.relationStub = new RelationStub(this.serviceContext, this);
		}
		return this.relationStub;
	}

	/**
	 * @return the iterationStub
	 */
	public IterationStub getIterationStub()
	{
		if (this.iterationStub == null)
		{
			this.iterationStub = new IterationStub(this.serviceContext, this);
		}
		return this.iterationStub;
	}

	/**
	 * @return the relationUnlinkStub
	 */
	public RelationUnlinkStub getRelationUnlinkStub()
	{
		if (this.relationUnlinkStub == null)
		{
			this.relationUnlinkStub = new RelationUnlinkStub(this.serviceContext, this);
		}
		return this.relationUnlinkStub;
	}

	protected synchronized SLC getSLC() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(SLC.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/**
	 * @return the structureStub
	 */
	public StructureStub getStructureStub()
	{
		if (this.structureStub == null)
		{
			this.structureStub = new StructureStub(this.serviceContext, this);
		}
		return this.structureStub;
	}

	public TransferCheckOutStub getTransferCheckOutStub()
	{
		if (this.transferCheckOutStub == null)
		{
			this.transferCheckOutStub = new TransferCheckOutStub(this.serviceContext, this);
		}
		return this.transferCheckOutStub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	protected void init()
	{
		if (initialized)
		{
			return;
		}

		initialized = true;
	}

	// @Tracked(description = TrackedDesc.LINK_RELATION, renderer = TRViewLinkImpl.class)
	@Override
	public StructureObject link(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject) throws ServiceRequestException
	{
		StructureObject object = this.getRelationLinkStub().link(viewObjectGuid, end2FoundationObjectGuid, null, structureObject);
		// ViewObject view = this.getRelation(viewObjectGuid);
		// FoundationObject end1 = this.getObject(view.getEnd1ObjectGuid());
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#listByClass(java.lang.String, java.util.List, java.util.Map, java.util.Map)
	 */
	@Tracked(description = TrackedDesc.QUERY_OBJECT, renderer = TRSearchConditionImpl.class)
	@Override
	public List<FoundationObject> listObject(SearchCondition condition) throws ServiceRequestException
	{
		return this.getFoundationStub().listObject(condition);
	}

	@Override
	public List<StructureObject> listObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
			throws ServiceRequestException
	{
		return this.getRelationStub().listObjectOfRelation(viewObject, searchCondition, end2SearchCondition, dataRule);
	}

	@Override
	public List<FoundationObject> listObjectRevisionHistory(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFRevisionStub().listObjectRevisionHistory(objectGuid);
	}

	@Override
	public List<ViewObject> listRelation(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getRelationStub().listRelation(end1ObjectGuid);
	}

	@Override
	public List<FoundationObject> listWhereReferenced(ObjectGuid end2ObjectGuid, String viewName, SearchCondition end1SearchCondition, DataRule dataRule)
			throws ServiceRequestException
	{
		return this.getRelationStub().listWhereReferenced(end2ObjectGuid, viewName, end1SearchCondition, dataRule, Constants.isSupervisor(true, this));
	}

	@Override
	public FoundationObject newFoundationObject(String classGuid, String className) throws ServiceRequestException
	{
		return this.getFoundationStub().newFoundationObject(classGuid, className, (String) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#newStructureObject(java.lang.String, java.lang.String)
	 */
	@Override
	public StructureObject newStructureObject(String classGuid, String className) throws ServiceRequestException
	{
		return this.getStructureStub().newStructureObject(classGuid, className);
	}

	@Tracked(description = TrackedDesc.VIEW_OBJECT, renderer = TROpenObjectImpl.class)
	@Override
	public FoundationObject openObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFUIStub().openObject(objectGuid, true);
	}

	@Override
	public FoundationObject prepareRevision(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFRevisionStub().prepareRevision(objectGuid);
	}

	@Tracked(description = TrackedDesc.SAVE_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getFSaverStub().saveObject(foundationObject, true, Constants.isSupervisor(true, this), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#saveObject(dyna.common.bean.data.FoundationObject,
	 * dyna.common.bean.data.StructureObject)
	 */
	@Override
	public FoundationObject saveObject(FoundationObject foundationObject, RelationTemplateTypeEnum structureModel) throws ServiceRequestException
	{
		return this.getFSaverStub().saveObject(foundationObject, structureModel, Constants.isSupervisor(true, this), null);
	}

	@Tracked(description = TrackedDesc.SAVE_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public void saveObjectOnly(FoundationObject foundationObject) throws ServiceRequestException
	{
		this.getFSaverStub().saveObject(foundationObject, false, Constants.isSupervisor(true, this), null);
	}

	@Tracked(description = TrackedDesc.SAVE_RELATION, renderer = TRFoundationImpl.class)
	@Override
	public ViewObject saveRelation(ViewObject relation) throws ServiceRequestException
	{
		return this.getRelationStub().saveRelation(relation, Constants.isSupervisor(true, this), null);
	}

	@Tracked(description = TrackedDesc.SAVE_RELATION, renderer = TRSaveRelationByTemplateImpl.class)
	@Override
	public ViewObject saveRelationByTemplate(String relationTemplateGuid, ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getRelationStub().saveRelationByTemplate(relationTemplateGuid, end1ObjectGuid, Constants.isSupervisor(true, this), null);
	}

	@Override
	public StructureObject saveStructure(StructureObject structureObject) throws ServiceRequestException
	{
		return this.getStructureStub().saveStructure(structureObject);
	}

	@Override
	public void saveStructureBatch(ObjectGuid end1ObjectGuid, String name, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList)
			throws ServiceRequestException
	{
		ViewObject viewObj = this.getRelationByEND1(end1ObjectGuid, name);
		RelationTemplateInfo relationTemplate = this.getEMM().getRelationTemplateByName(end1ObjectGuid, name);
		if (viewObj == null)
		{
			if (relationTemplate == null)
			{
				throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + name, null, name);
			}
			else
			{
				viewObj = this.getRelationStub().saveRelationByTemplate(relationTemplate.getGuid(), end1ObjectGuid, false, null);
			}
		}
		this.getStructureStub().saveStructureBatch(viewObj.getObjectGuid(), linkList, unlinkList, updateList, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#saveStructure4Detail(dyna.common.bean.data.ObjectGuid, java.util.List,
	 * java.util.List, java.util.List)
	 */
	@Override
	public void saveStructureBatch(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList)
			throws ServiceRequestException
	{
		this.getStructureStub().saveStructureBatch(viewObjectGuid, linkList, unlinkList, updateList, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#saveStructure4Detail(dyna.common.bean.data.ObjectGuid, java.util.List,
	 * java.util.List, java.util.List)
	 */
	@Override
	public void saveStructure4Detail(ObjectGuid viewObjectGuid, List<FoundationObject> linkList, List<FoundationObject> unlinkList, List<FoundationObject> updateList)
			throws ServiceRequestException
	{
		this.getStructureStub().saveStructure4Detail(viewObjectGuid, linkList, unlinkList, updateList, null);
	}

	@Override
	public void unlink(ObjectGuid viewObjectGuid, ObjectGuid end2FoundationObjectGuid) throws ServiceRequestException
	{
		this.getRelationUnlinkStub().unlink(viewObjectGuid, end2FoundationObjectGuid, true);
	}

	@Override
	public void unlink(StructureObject structureObject) throws ServiceRequestException
	{
		structureObject.put(ViewObject.END1, structureObject.get("END1$"));
		this.getRelationUnlinkStub().unlink(structureObject);
		ObjectGuid end1ObjectGuid = structureObject.getEnd1ObjectGuid();
		if (!StringUtils.isGuid(end1ObjectGuid.getGuid()))
		{
			FoundationObject end1 = this.getObject(structureObject.getEnd1ObjectGuid());
			end1ObjectGuid = end1.getObjectGuid();
		}
	}

	// @Tracked(description = TrackedDesc.UPDATE_OWNER)
	@Override
	public FoundationObject updateOwnerUser(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime, boolean containBomView)
			throws ServiceRequestException
	{
		return this.getFUpdaterStub().updateOwnerUser(objectGuid, ownerUserGuid, ownerGroupGuid, updateTime, Constants.isSupervisor(true, this), containBomView);
	}

	// @Tracked(description = TrackedDesc.UPDATE_OWNER)
	@Override
	public FoundationObject updateOwnerUserInLib(ObjectGuid objectGuid, String ownerUserGuid, String ownerGroupGuid, Date updateTime) throws ServiceRequestException
	{
		return this.getFUpdaterStub().updateOwnerUserInLib(objectGuid, ownerUserGuid, ownerGroupGuid, updateTime);
	}

	// @Tracked(description = TrackedDesc.UPDATE_REV)
	// @Override
	// public FoundationObject updateRevisionId(ObjectGuid objectGuid, String revisionId, Date updateTime)
	// throws ServiceRequestException
	// {
	// return this.getFUpdaterStub().updateRevisionId(objectGuid, revisionId, updateTime);
	// }

	@Override
	public ViewObject getRelationByEND1(ObjectGuid end1ObjectGuid, String name) throws ServiceRequestException
	{
		return this.getRelationStub().getRelationByEND1(end1ObjectGuid, name);
	}

	// @Tracked(description = TrackedDesc.LINK_RELATION, renderer = TREnd1LinkImpl.class)
	@Override
	public StructureObject link(ObjectGuid end1Object, ObjectGuid end2Object, StructureObject structureObject, String viewName) throws ServiceRequestException
	{
		return this.getRelationLinkStub().link(end1Object, end2Object, structureObject, viewName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#getFoundationPreviewInfo(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public String getFoundationPreviewInfo(ObjectGuid objectGuid, LanguageEnum lang) throws ServiceRequestException
	{
		return this.getFUIStub().getFoundationPreviewInfo(objectGuid);
	}

	@Override
	public StructureObject getStructureObject(ObjectGuid structureObjectGuid, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getStructureStub().getStructureObject(structureObjectGuid, searchCondition, Constants.isSupervisor(true, this), null);
	}

	@Override
	public StructureObject getStructureObject(ObjectGuid structureObjectGuid, SearchCondition searchCondition, DataRule dataRule) throws ServiceRequestException
	{
		return this.getStructureStub().getStructureObject(structureObjectGuid, searchCondition, Constants.isSupervisor(true, this), dataRule);
	}

	@Override
	public List<FoundationObject> listObjectIteration(ObjectGuid objectGuid, Integer iterationId) throws ServiceRequestException
	{
		return this.getIterationStub().listObjectIteration(objectGuid, iterationId, Constants.isSupervisor(true, this));
	}

	// @Tracked(description = TrackedDesc.ROLLBACK_ITR)
	@Override
	public void rollbackObjectIteration(ObjectGuid objectGuid, Integer iterationId) throws ServiceRequestException
	{
		this.getIterationStub().rollbackObjectIteration(objectGuid, iterationId);
	}

	@Override
	public List<StructureObject> listObjectOfRelation(ObjectGuid end1ObjectGuid, String viewName, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException
	{
		return this.getRelationStub().listObjectOfRelation(end1ObjectGuid, viewName, searchCondition, end2SearchCondition, dataRule, Constants.isSupervisor(true, this));
	}

	@Tracked(description = TrackedDesc.QUERY_OBJECT, renderer = TRSearchConditionImpl.class)
	@Override
	public List<FoundationObject> listObjectBySearch(SearchCondition condition, String searchGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().listObjectBySearch(condition, searchGuid);
	}

	@Tracked(description = TrackedDesc.CREATE_OBJ_BY_TPL, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject createDocumentByTemplate(FoundationObject docFoundationObject, ObjectGuid tmpObjectGuid, boolean isCheckOut) throws ServiceRequestException
	{
		return this.getFSaverStub().createDocumentByTemplate(docFoundationObject, tmpObjectGuid, isCheckOut);
	}

	@Tracked(description = TrackedDesc.CREATE_OBJ_BY_FILE, renderer = TRFoundationImpl.class)
	@Override
	public List<DynaObject> createDocumentByFile(FoundationObject docFoundationObject, List<DSSFileInfo> fileInfoList, boolean isCheckOut) throws ServiceRequestException
	{
		return this.getFSaverStub().createDocumentByFile(docFoundationObject, fileInfoList, isCheckOut);
	}

	@Override
	public FoundationObject newFoundationObject(String classGuid, String className, ObjectGuid templateObjectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().newFoundationObject(classGuid, className, templateObjectGuid);
	}

	@Tracked(description = TrackedDesc.DEL_OBJECT)
	@Override
	public void deleteFoundationObject(String foundationObjectGuid, String classGuid) throws ServiceRequestException
	{
		this.getFoundationStub().deleteFoundationObject(foundationObjectGuid, classGuid, Constants.isSupervisor(true, this));
	}

	// @Tracked(description = TrackedDesc.CANCEL_CHECK_OUT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject cancelCheckOut(FoundationObject foundationObject, boolean isDealBom) throws ServiceRequestException
	{
		return this.getCancelCheckOutStub().cancelCheckOut(foundationObject, isDealBom);
	}

	@Tracked(description = TrackedDesc.CHECK_IN, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject checkIn(FoundationObject foundationObject, boolean isDealBom) throws ServiceRequestException
	{
		return this.getCheckInStub().checkIn(foundationObject, isDealBom, Constants.isSupervisor(true, this));
	}

	@Tracked(description = TrackedDesc.TRANS_CHECK_OUT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject transferCheckout(FoundationObject foundationObject, String toUserGuid, String locale, boolean isDealBom) throws ServiceRequestException
	{
		return this.getTransferCheckOutStub().transferCheckout(foundationObject, toUserGuid, locale, Constants.isSupervisor(true, this), isDealBom);
	}

	@Tracked(description = TrackedDesc.REVISE_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject createRevision(FoundationObject foundationObject, boolean isContainBom) throws ServiceRequestException
	{
		String operatorGuid = ((UserSignature) this.getSignature()).getUserGuid();
		String groupGuid = ((UserSignature) this.getSignature()).getLoginGroupGuid();

		return this.getFSaverStub().saveAsObject(foundationObject, groupGuid, operatorGuid, false, false, Constants.isSupervisor(true, this), isContainBom, false, false, true);
		// return this.getFRevisionStub().createRevision(operatorGuid, groupGuid, foundationObject, isContainBom);
	}

	@Tracked(description = TrackedDesc.REVISE_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject createRevisionAndCheckOut(FoundationObject foundationObject, boolean isContainBom) throws ServiceRequestException
	{
		String operatorGuid = ((UserSignature) this.getSignature()).getUserGuid();
		String groupGuid = ((UserSignature) this.getSignature()).getLoginGroupGuid();

		FoundationObject saveAsObject = this.getFSaverStub().saveAsObject(foundationObject, groupGuid, operatorGuid, false, false, Constants.isSupervisor(true, this), isContainBom,
				false, false, true);
		// FoundationObject saveAsObject = this.getFRevisionStub().createRevision(operatorGuid, groupGuid,
		// foundationObject, isContainBom);

		ClassInfo classInfo = this.getEMM().getClassByGuid(saveAsObject.getObjectGuid().getClassGuid());
		// 打开取替代对象时，不记录历史
		if (classInfo != null && !classInfo.hasInterface(ModelInterfaceEnum.IReplaceSubstitute))
		{
			BIViewHis biViewHis = new BIViewHis();
			biViewHis.setInstanceGuid(saveAsObject.getObjectGuid().getGuid());
			biViewHis.setInstanceClassGuid(saveAsObject.getObjectGuid().getClassGuid());
			biViewHis.setInstanceBOGuid(saveAsObject.getObjectGuid().getBizObjectGuid());
			biViewHis.put(BIViewHis.CREATE_USER, this.getOperatorGuid());

			this.getPOS().saveBIViewHis(biViewHis);
		}

		try
		{
			if (!saveAsObject.isCheckOut() && saveAsObject.isCommited())
			{
				saveAsObject = this.checkOut(saveAsObject);
			}
		}
		catch (ServiceRequestException e)
		{
			saveAsObject.put(FSaverStub.CHECKOUT_ERROR_ID, "ID_APP_CREATE_CHECKOUT_FAIL");
			// throw new ServiceRequestException("ID_APP_CREATE_CHECKOUT_FAIL",
			// "create successful , checkout error");
		}
		return saveAsObject;
	}

	@Tracked(description = TrackedDesc.OBS_OBJECT)
	@Deprecated
	@Override
	public void obsoleteObject(ObjectGuid objectGuid, Date obsoleteTime) throws ServiceRequestException
	{
		this.getFoundationStub().obsoleteObject(objectGuid, obsoleteTime, Constants.isSupervisor(true, this));
	}

	@Tracked(description = TrackedDesc.SAVE_AS_NEW, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject saveAsObject(FoundationObject foundationObject, boolean isContainBom, boolean isContainPartReplace, boolean isContainGlobalReplce)
			throws ServiceRequestException
	{
		String operatorGuid = ((UserSignature) this.getSignature()).getUserGuid();
		String groupGuid = ((UserSignature) this.getSignature()).getLoginGroupGuid();

		return this.getFSaverStub().saveAsObject(foundationObject, groupGuid, operatorGuid, true, false, Constants.isSupervisor(true, this), isContainBom, isContainPartReplace,
				isContainGlobalReplce, false);
	}

	@Tracked(description = TrackedDesc.SAVE_AS_NEW, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject saveAsObjectAndCheckOut(FoundationObject foundationObject, boolean isContainBom, boolean isContainPartReplace, boolean isContainGlobalReplce)
			throws ServiceRequestException
	{

		String operatorGuid = ((UserSignature) this.getSignature()).getUserGuid();
		String groupGuid = ((UserSignature) this.getSignature()).getLoginGroupGuid();

		FoundationObject saveAsObject = this.getFSaverStub().saveAsObject(foundationObject, groupGuid, operatorGuid, true, false, Constants.isSupervisor(true, this), isContainBom,
				isContainPartReplace, isContainGlobalReplce, true);

		ClassInfo classInfo = this.getEMM().getClassByGuid(saveAsObject.getObjectGuid().getClassGuid());
		// 打开取替代对象时，不记录历史
		if (classInfo != null && !classInfo.hasInterface(ModelInterfaceEnum.IReplaceSubstitute))
		{
			BIViewHis biViewHis = new BIViewHis();
			biViewHis.setInstanceGuid(saveAsObject.getObjectGuid().getGuid());
			biViewHis.setInstanceClassGuid(saveAsObject.getObjectGuid().getClassGuid());
			biViewHis.setInstanceBOGuid(saveAsObject.getObjectGuid().getBizObjectGuid());
			biViewHis.put(BIViewHis.CREATE_USER, this.getOperatorGuid());

			this.getPOS().saveBIViewHis(biViewHis);
		}

		// 检出
		try
		{
			if (!saveAsObject.isCheckOut() && saveAsObject.isCommited())
			{
				saveAsObject = this.checkOut(saveAsObject);
			}
		}
		catch (ServiceRequestException e)
		{
			saveAsObject.put(FSaverStub.CHECKOUT_ERROR_ID, "ID_APP_CREATE_CHECKOUT_FAIL");
			// throw new ServiceRequestException("ID_APP_CREATE_CHECKOUT_FAIL",
			// "create successful , checkout error");
		}

		return saveAsObject;
	}

	@Override
	public void cancelObsolete(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getFoundationStub().cancelObsolete(objectGuid, Constants.isSupervisor(true, this));
	}

	@Override
	public void setIsExportToERP(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getFSaverStub().setIsExportToERP(objectGuid);
	}

	@Deprecated
	@Override
	public String allocateUniqueAlterId(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getFSaverStub().allocateUniqueAlterId(foundationObject);
	}

	@Deprecated
	@Override
	public String allocateUniqueName(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getFSaverStub().allocateUniqueName(foundationObject);
	}

	@Override
	public void moveToFolder(ObjectGuid objectGuid, String fromFolderGuid, String toFolderGuid) throws ServiceRequestException
	{
		this.getFFolderStub().moveToFolder(objectGuid, fromFolderGuid, toFolderGuid, Constants.isSupervisor(true, this));
	}

	@Override
	public List<Folder> listFolder(ObjectGuid foundationObjectGuid) throws ServiceRequestException
	{
		return this.getFFolderStub().listFolder(foundationObjectGuid);
	}

	@Override
	public List<FoundationObject> listFoundationObjectOfRelation(ObjectGuid viewObject, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule,
			boolean isCheckAuthority) throws ServiceRequestException
	{
		return this.getRelationStub().listFoundationObjectOfRelation(viewObject, searchCondition, end2SearchCondition, dataRule, isCheckAuthority);
	}

	@Override
	public FoundationObject getObject4ObjectField(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getObject4ObjectField(objectGuid);
	}

	@Override
	public List<FoundationObject> getObject4ObjectField(List<ObjectGuid> objectGuidList) throws ServiceRequestException
	{
		return this.getFoundationStub().getObject4ObjectField(objectGuidList);
	}

	@Override
	public FoundationObject getMaster(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getMaster(objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#unlink(dyna.common.bean.data.ObjectGuid, java.lang.String,
	 * dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void unlink(ObjectGuid end1ObjectGuid, String templateName, ObjectGuid end2FoundationObjectGuid) throws ServiceRequestException
	{
		this.getRelationUnlinkStub().unlink(end1ObjectGuid, templateName, end2FoundationObjectGuid, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#listRelationTemplateName4Builtin(java.lang.String)
	 */
	// @Override
	// public List<String> listRelationTemplateName4Builtin(String end1ClassName) throws ServiceRequestException
	// {
	// return this.getRelationTemplateStub().listRelationTemplateName4Builtin(end1ClassName);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#listRelationWithOutBuiltIn(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<ViewObject> listRelationWithOutBuiltIn(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getRelationStub().listRelation(end1ObjectGuid, true, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#isCreateCommitByClassName(java.lang.String)
	 */
	// @Override
	// @Deprecated
	// public boolean isCreateCommitByClassName(String className) throws ServiceRequestException
	// {
	// return this.getFoundationStub().isCreateCommitByClassName(className);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#deleteMaster(java.lang.String)
	 */
	@Override
	public void deleteMaster(String masterGuid, String classGuid) throws ServiceRequestException
	{
		this.getFoundationStub().deleteMaster(masterGuid, classGuid, true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#unlinkAndDeleteEnd2(dyna.common.bean.data.StructureObject)
	 */
	@Override
	public void unlinkAndDeleteEnd2(StructureObject structureObject) throws ServiceRequestException
	{
		this.getRelationUnlinkStub().unlinkAndDeleteEnd2(structureObject, true);

	}

	// @Override
	// public boolean hasBuiltinRelationTemplate(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	// {
	// return this.getRelationTemplateStub().hasBuiltinRelationTemplate(end1ObjectGuid);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#deleteRelationAndEnd2(dyna.common.bean.data.foundation.ViewObject)
	 */
	@Tracked(description = TrackedDesc.DEL_RELATION, renderer = TRFoundationImpl.class)
	@Override
	public void deleteRelationAndEnd2(ViewObject relation) throws ServiceRequestException
	{
		this.getRelationStub().deleteRelationAndEnd2(relation);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#createAndCheckOutObject(dyna.common.bean.data.FoundationObject)
	 */
	@Tracked(description = TrackedDesc.CREATE_OBJECT, renderer = TRFoundationImpl.class)
	@Override
	public FoundationObject createAndCheckOutObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		String operatorGuid = this.getUserSignature().getUserGuid();
		String groupGuid = this.getUserSignature().getLoginGroupGuid();
		return this.getFSaverStub().createAndCheckOutObject(foundationObject, null, groupGuid, operatorGuid, Constants.isSupervisor(true, this), true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#openInstance(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public OpenInstanceModel openInstance(ObjectGuid objectGuid, boolean isView, String processRuntimeGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().openInstance(objectGuid, isView, processRuntimeGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#listObjectRevisionHistoryForMaster(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<FoundationObject> listObjectRevisionHistoryForMaster(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFRevisionStub().listObjectRevisionHistoryForMaster(objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#changePrimaryObject(dyna.common.bean.data.StructureObject)
	 */
	@Override
	public void changePrimaryObject(ObjectGuid end1ObjectGuid, String viewName, StructureObject structureObject) throws ServiceRequestException
	{
		this.getStructureStub().changePrimaryObject(end1ObjectGuid, viewName, structureObject, true);

	}

	@Override
	public void stopUsingObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getFoundationStub().stopUsingObject(objectGuid, true, null);
	}

	@Override
	public void startUsingObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getFoundationStub().startUsingObject(objectGuid, true);
	}

	@Override
	public FoundationObject newClassificationFoundation(String classificationGuid, FoundationObject oriClassificationFoundation) throws ServiceRequestException
	{
		return this.getFoundationStub().newFoundationObject(FoundationObject.class, classificationGuid, null, true, oriClassificationFoundation);
	}

	@Override
	public String checkFoundationRepeat(FoundationObject foundation, boolean isSaveAs) throws ServiceRequestException
	{
		return this.getFoundationStub().checkFoundationRepeat(foundation, isSaveAs);
	}

	@Override
	public FoundationObject newFoundationObject(String classGuid, String className, String classificationGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().newFoundationObject(classGuid, className, classificationGuid);
	}

	@Override
	public List<FoundationObject> listFoundationObjectOfRelation4Detail(ObjectGuid viewObject, SearchCondition structureSearchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule) throws ServiceRequestException
	{
		return this.getRelationStub().listFoundationObjectOfRelation4Detail(viewObject, structureSearchCondition, end2SearchCondition, dataRule, false);
	}

	@Override
	public void editRelation(ObjectGuid end1ObjectGuid, String templateName, List<ObjectGuid> end2FoundationObjectGuidList, List<StructureObject> structureObjectList,
			String procRtGuid, boolean isReplace) throws ServiceRequestException
	{
		this.getRelationLinkStub().editRelation(end1ObjectGuid, templateName, end2FoundationObjectGuidList, structureObjectList, procRtGuid, isReplace);
	}

	@Override
	public void deleteReference(ObjectGuid objectGuid, String exceptionParameter) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();
		ds.deleteReferenceData(objectGuid, this.getUserSignature().getUserGuid());
	}

	@Override
	public List<FoundationObject> quickQuery(String searchKey, int rowCntPerPate, int pageNum, boolean caseSensitive, boolean isEquals, boolean isOnlyId, List<String> boNameList)
			throws ServiceRequestException
	{
		return this.getFoundationStub().quickQuery(searchKey, rowCntPerPate, pageNum, caseSensitive, isEquals, isOnlyId, boNameList);
	}

	@Override
	public void refreshMergeFieldValue(String className, String fieldName) throws ServiceRequestException
	{
		this.getFoundationStub().refreshMergeFieldValue(className, fieldName);
	}

	@Override
	public void refreshMergeFieldValue(String className, Set<String> fieldNameSet, String classificationGuid) throws ServiceRequestException
	{
		this.getFoundationStub().refreshMergeFieldValue(className, fieldNameSet, classificationGuid);
	}

	@Override
	public void canStop(FoundationObject foundationObject, String procrtGuid) throws ServiceRequestException
	{
		this.getFoundationStub().canStop(foundationObject, procrtGuid);
	}

	@Override
	public FoundationObject getObjectNotCheckAuthor(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getFoundationStub().getObject(objectGuid, false);
	}

	@Override
	public void release(ObjectGuid objectGuid) throws ServiceRequestException
	{
		this.getFoundationStub().release(objectGuid);
	}

	@Override
	public void createByTemplate(ObjectGuid end1ObjectGuid, ObjectGuid templateObjectGuid, String templateName) throws ServiceRequestException
	{
		this.getRouteStub().createByTemplate(end1ObjectGuid, templateObjectGuid, templateName);
	}

	@Override
	public void updateHasEnd2Flg(ObjectGuid end1ObjectGuid, String relationTemplateGuid) throws ServiceRequestException
	{
	}
}