/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ECSImpl
 * caogc 2010-11-02
 */
package dyna.app.service.brs.uecs;

import dyna.app.service.BusinessRuleService;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.UpdatedECSConstants;
import dyna.net.service.brs.*;
import dyna.net.service.das.MSRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Engineering Change Service Implement工程变更服务的实现类
 * 
 * @author caogc
 * 
 */
@Service
public class UECSImpl extends BusinessRuleService implements UECS
{
	@Autowired
	private UECRECPStub		uecrStub		;
	@Autowired
	private UECSStub		uecsStub		;
	@Autowired
	private UECNECOStub		uEcnEcoStub		;
	@Autowired
	private UECIStub		uEciStub		;
	@Autowired
	private UECQueryStub	uECQueryStub	;
	private static boolean	initialized		= false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public void init()
	{
		if (initialized == false)
		{
			this.setChangeItemValueScope();
			initialized = true;
		}
	}

	/**
	 * 重新设置批量ECO/ECP的changeItem 的valuescope值
	 */
	private void setChangeItemValueScope()
	{
		try
		{
			List<ClassInfo> listClassInfo = this.getEMM().listClassByInterface(ModelInterfaceEnum.IBatchForEc);
			if (!SetUtils.isNullList(listClassInfo))
			{
				for (ClassInfo classinfo : listClassInfo)
				{
					ClassField classField = this.getEMM().getFieldByName(classinfo.getName(), UpdatedECSConstants.ChangeItem, true);
					if (classField != null)
					{
						classField.setValueScope(ModelInterfaceEnum.IItem.name());
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * @return the ecAffectedProductStub
	 */
	public UECRECPStub getUECRECPStub()
	{
		return this.uecrStub;
	}

	/**
	 * @return the ecAffectedProductStub
	 */
	public UECNECOStub getUECNECOStub()
	{
		return this.uEcnEcoStub;
	}

	/**
	 * @return the ecAffectedProductStub
	 */
	protected UECIStub getUECIStub()
	{
		return this.uEciStub;
	}

	/**
	 * @return the ecAffectedProductStub
	 */
	protected UECQueryStub getUECQueryStub()
	{
		return this.uECQueryStub;
	}

	public UECSStub getUECSStub()
	{
		return this.uecsStub;
	}

	public synchronized BOAS getBOAS() throws ServiceRequestException
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

	public synchronized EDAP getEDAP() throws ServiceRequestException
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

	public synchronized DSS getDSS() throws ServiceRequestException
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

	public synchronized BRM getBRM() throws ServiceRequestException
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

	public synchronized WFI getWFI() throws ServiceRequestException
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

	public synchronized AAS getAAS() throws ServiceRequestException
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

	public synchronized SMS getSMS() throws ServiceRequestException
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

	public synchronized MSRM getMSRM() throws ServiceRequestException
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

	public synchronized BOMS getBOMS() throws ServiceRequestException
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

	public List<FoundationObject> getECPByECRTree(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPByECRTree(end1ObjectGuid);
	}

	public List<FoundationObject> getECPByECRTreeInECNPage(ObjectGuid ecrObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPByECRTreeInECNPage(ecrObjectGuid, ecnObjectGuid);
	}

	public List<FoundationObject> getECPByECRAll(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPByECRAll(end1ObjectGuid);
	}

	public List<FoundationObject> getECPByECRAllInECNPage(ObjectGuid ecrObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPByECRAllInECNPage(ecrObjectGuid, ecnObjectGuid);
	}

	public List<FoundationObject> getECOByECNTree(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECOByECNTree(end1ObjectGuid);
	}

	public List<FoundationObject> getECOByECNAll(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECOByECNAll(end1ObjectGuid);
	}

	public List<FoundationObject> getECRByECN(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECRByECN(end1ObjectGuid);
	}

	public List<FoundationObject> getECOByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECOByECP(end1ObjectGuid);
	}

	public List<FoundationObject> getECPCONTENTByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPCONTENTByECP(end1ObjectGuid);
	}

	public List<FoundationObject> getECPCONTENTByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPCONTENTByECO(end1ObjectGuid);
	}

	public List<FoundationObject> getWhereUsedECNByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getWhereUsedECNByECO(end1ObjectGuid);
	}

	public List<FoundationObject> getWhereUsedECRByECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getWhereUsedECRByECP(end1ObjectGuid);
	}

	public List<FoundationObject> getFormECIByECO(ObjectGuid ChangeItemObjectGuid, ObjectGuid SolveObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getFormECIByECO(ChangeItemObjectGuid, SolveObjectGuid, ecoObjectGuid);
	}

	public List<FoundationObject> getRelationECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName, ObjectGuid ecoObjectGuid)
			throws ServiceRequestException
	{
		return this.getUECQueryStub().getRelationECIByECO(changeItemObjectGuid, solveObjectGuid, relationTemplateName, ecoObjectGuid);
	}

	public List<FoundationObject> getBomECIByECO(ObjectGuid changeItemObjectGuid, ObjectGuid solveObjectGuid, String relationTemplateName, ObjectGuid ecoObjectGuid)
			throws ServiceRequestException
	{
		return this.getUECQueryStub().getBomECIByECO(changeItemObjectGuid, solveObjectGuid, relationTemplateName, ecoObjectGuid);
	}

	public List<FoundationObject> getContentECIByECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getOtherECIByECO(end1ObjectGuid);
	}

	public FoundationObject getBatchECPFoundation(ObjectGuid ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getBatchECPorECOFoundation(ObjectGuid);
	}

	public FoundationObject getBatchECOFoundation(ObjectGuid ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getBatchECPorECOFoundation(ObjectGuid);
	}

	public List<StructureObject> getEciBYBatchECP(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getChangeItemBeforeBYJustBatchECP(end1ObjectGuid);
	}

	public List<StructureObject> getEciBYBatchECO(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getChangeItemBeforeBYJustBatchECO(end1ObjectGuid);
	}

	public void completeEco(ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		this.getUECIStub().completeEco(ecoObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#saveECP(dyna.common.bean.data.FoundationObject, dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject saveECP(FoundationObject ecpfoundation, ObjectGuid ecrObject, ObjectGuid parentECP, String proGuid) throws ServiceRequestException
	{
		return this.getUECRECPStub().saveECP(ecpfoundation, ecrObject, parentECP, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#saveECO(dyna.common.bean.data.FoundationObject, dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public FoundationObject saveECO(FoundationObject ecofoundation, ObjectGuid ecnObject, ObjectGuid parentECO, String proGuid) throws ServiceRequestException
	{
		return this.getUECNECOStub().saveECO(ecofoundation, ecnObject, parentECO, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#linkECR_ECP(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void linkECR_ECP(ObjectGuid ecrObjectGuid, ObjectGuid ecpObjectGuid, String proGuid) throws ServiceRequestException
	{
		this.getUECSStub().link(ecrObjectGuid, ecpObjectGuid, null, proGuid, UpdatedECSConstants.ECR_ECP$);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#linkECN_ECO(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void linkECN_ECO(ObjectGuid ecnObjectGuid, ObjectGuid ecoObjectGuid, String proGuid) throws ServiceRequestException
	{
		this.getUECSStub().link(ecnObjectGuid, ecoObjectGuid, null, proGuid, UpdatedECSConstants.ECN_ECO$);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#linkECN_ECR(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void linkECN_ECR(ObjectGuid ecnObjectGuid, ObjectGuid ecrObjectGuid, String proGuid) throws ServiceRequestException
	{
		this.getUECSStub().link(ecnObjectGuid, ecrObjectGuid, null, proGuid, UpdatedECSConstants.ECN_ECR$);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#linkECP_ECO(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void linkECP_ECO(ObjectGuid ecpObjectGuid, ObjectGuid ecoObjectGuid, String proGuid) throws ServiceRequestException
	{
		this.getUECSStub().link(ecpObjectGuid, ecoObjectGuid, null, proGuid, UpdatedECSConstants.ECP_ECO$);
	}

	@Override
	public List<FoundationObject> listChildECPbyParentObjectGuid(ObjectGuid ecpObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().listChildbyParentObjectGuid(ecpObjectGuid, UpdatedECSConstants.ParentECP);
	}

	public List<FoundationObject> getECPChildbyParentECPInECNPage(ObjectGuid ecpObjectGuid, ObjectGuid ecnObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().getECPChildbyParentECPInECNPage(ecpObjectGuid, ecnObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#listChildECObyParentObjectGuid(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<FoundationObject> listChildECObyParentObjectGuid(ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		return this.getUECQueryStub().listChildbyParentObjectGuid(ecoObjectGuid, UpdatedECSConstants.ParentECO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#addECP_Content(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void addECRelation(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, String relationTempalte, String proGuid) throws ServiceRequestException
	{
		this.getUECRECPStub().addECRelation(end1ObjectGuid, end2ObjectGuid, relationTempalte, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#deleteECP(dyna.common.bean.data.ObjectGuid, java.util.List)
	 */
	@Override
	public void deleteECP(ObjectGuid ecrObjectGuid, List<ObjectGuid> listEcpObjectGuid, boolean deleteAll) throws ServiceRequestException
	{
		this.getUECRECPStub().deleteECPorECO(listEcpObjectGuid, deleteAll, ModelInterfaceEnum.IECPM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#deleteECO(dyna.common.bean.data.ObjectGuid, java.util.List)
	 */
	@Override
	public void deleteECO(ObjectGuid ecnObjectGuid, List<ObjectGuid> listEcoObjectGuid, boolean deleteAll) throws ServiceRequestException
	{
		this.getUECRECPStub().deleteECPorECO(listEcoObjectGuid, deleteAll, ModelInterfaceEnum.IECOM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#updateContent(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public FoundationObject saveNotCheckout(FoundationObject contentFoundation) throws ServiceRequestException
	{
		return this.getUECRECPStub().saveNotCheckout(contentFoundation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#linkECO_ECI(dyna.common.bean.data.ObjectGuid, dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void linkECO_ECI(ObjectGuid ecoObjectGuid, ObjectGuid eciObjectGuid, String proGuid) throws ServiceRequestException
	{
		this.getUECNECOStub().linkECO_ECI(ecoObjectGuid, eciObjectGuid, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#saveECO_ECI_ChangeItem(dyna.common.bean.data.FoundationObject, java.util.List,
	 * java.util.List, java.util.List)
	 */
	@Override
	public FoundationObject saveECO_ECI_ChangeItem(FoundationObject ecoFoundationObject, List<StructureObject> structureObjectList, List<StructureObject> deleteList, String proGuid)
			throws ServiceRequestException
	{
		return this.getUECNECOStub().saveECO_ECI_ChangeItem(ecoFoundationObject, structureObjectList, deleteList, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#exchangeECP_ECO(dyna.common.bean.data.ObjectGuid, java.util.List)
	 */
	@Override
	public Map<String, ServiceRequestException> exchangeECP_ECO(ObjectGuid ecnObjectGuid, List<ObjectGuid> ecpObjectGuidList, String proGuid) throws ServiceRequestException
	{
		return this.getUECNECOStub().exchangeECP_ECO(ecnObjectGuid, ecpObjectGuidList, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#saveECP_ECI_ChangeItem(dyna.common.bean.data.FoundationObject, java.util.List,
	 * java.util.List, java.util.List)
	 */
	@Override
	public FoundationObject saveECP_ECI_ChangeItem(FoundationObject ecpFoundationObject, List<StructureObject> structureObjectList, List<StructureObject> deleteList, String proGuid)
			throws ServiceRequestException
	{
		return this.getUECRECPStub().saveECP_ECI_ChangeItem(ecpFoundationObject, structureObjectList, deleteList, proGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#listStructureObject(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Override
	public List<BOMStructure> listStructureObject(ObjectGuid end1ObjectGuid, ObjectGuid end2ObjectGuid, String viewName) throws ServiceRequestException
	{
		return this.getUECSStub().listStructureObject(end1ObjectGuid, end2ObjectGuid, viewName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#startECO(java.util.List)
	 */
	@Override
	public void startECO(ObjectGuid ecnObjectGuid, ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		this.getUECNECOStub().startECO(ecnObjectGuid, ecoObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#isUsedByOtherECO(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public boolean isUsedByOtherECO(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getUECNECOStub().isUsedByOtherNormalECO(objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#deleteNotCheckOut(dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Override
	public void deleteNotCheckOut(ObjectGuid objectGuid, String tagString) throws ServiceRequestException
	{
		FoundationObject foundation = this.getBOAS().getObject(objectGuid);
		this.getUECSStub().deleteObject(foundation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#cancelECN_ECO(java.util.List)
	 */
	@Override
	public void cancelECN_ECO(ObjectGuid cancelValue) throws ServiceRequestException
	{
		this.getUECNECOStub().cancelECN_ECO(cancelValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#isCanCompleteECO(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public boolean checkECOCanBeStarted(ObjectGuid objectGuid) throws ServiceRequestException
	{

		return this.getUECNECOStub().checkECOCanBeStarted(objectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#cancelECR(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void cancelECR(ObjectGuid ecrObjectGuid) throws ServiceRequestException
	{
		this.getUECRECPStub().cancelECR(ecrObjectGuid);
	}

	public void sendMailtoPerformerByScript(String procRtGuid) throws ServiceRequestException
	{
		this.getUECSStub().sendMailtoPerformerByScript(procRtGuid);
	}

	public void unlockFoundation(String procRtGuid) throws ServiceRequestException
	{
		this.getUECSStub().unlockFoundation(procRtGuid);
	}

	public void recoveryEco(ObjectGuid ecoObjectGuid) throws ServiceRequestException
	{
		this.getUECIStub().recoveryEco(ecoObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.UECS#listMyExchangeTask(java.util.Map)
	 */
	@Override
	public List<FoundationObject> listMyExchangeTask(SearchCondition searchCondition) throws ServiceRequestException
	{
		return null;
	}
}
