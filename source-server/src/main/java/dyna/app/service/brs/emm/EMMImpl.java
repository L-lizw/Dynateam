/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EMMImpl
 * Wanglei 2010-7-8
 */
package dyna.app.service.brs.emm;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.BusinessRuleService;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecycleGate;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.LIC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Enterprise Model Management implementation
 * 
 * @author Wanglei
 * 
 */
@Service
public class EMMImpl extends BusinessRuleService implements EMM
{

	private static boolean			initialized				= false;
	@Autowired
	private DomainServiceStub		domainServiceStub		= null;
	@Autowired
	private CodeStub				codeStub				= null;
	@Autowired
	private BMStub					bmStub					= null;
	@Autowired
	private ClassStub				classStub				= null;
	@Autowired
	private LCStub					lcStub					= null;
	@Autowired
	private UIStub					uiStub					= null;
	@Autowired
	private ClassificationStub		classificationStub		= null;
	@Autowired
	private RelationTemplateStub	relationTemplateStub	= null;
	@Autowired
	private BOMTemplateStub			bomTemplateStub			= null;
	@Autowired
	private InterfaceStub			interfaceStub			= null;
	@Autowired
	private ViewModelStub			viewModelStub			= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(dyna.net.security.signature.Signature)
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		if (this.getSignature() == null)
		{
			super.authorize(method, args);
		}
	}

	@Override
	public void init()
	{
		try
		{
			this.syncInit(this.serviceContext);
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}

	public void refreshCache(boolean hasLicence) throws ServiceRequestException
	{
		this.getCodeStub().init(hasLicence);
	}

	private synchronized void syncInit(ServiceContext serviceContext) throws ServiceRequestException
	{
		if (initialized)
		{
			return;
		}
		initialized = true;

		this.refreshCache(serviceContext.getServerContext().getLicenseDaemon().hasLicence(ApplicationTypeEnum.CLS.name()));
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

	public DomainServiceStub getDomainServiceStub()
	{
		return this.domainServiceStub;
	}

	public InterfaceStub getInterfaceStub()
	{
		return this.interfaceStub;
	}

	public CodeStub getCodeStub()
	{
		return this.codeStub;
	}

	public ClassificationStub getClassificationStub()
	{
		return this.classificationStub;
	}

	/**
	 * @return the relationTemplateStub
	 */
	public RelationTemplateStub getRelationTemplateStub()
	{
		return this.relationTemplateStub;
	}

	/**
	 * @return the bomTemplateStub
	 */
	protected BOMTemplateStub getBomTemplateStub()
	{
		return this.bomTemplateStub;
	}

	public BMStub getBMStub()
	{
		return this.bmStub;
	}

	public ClassStub getClassStub()
	{
		return this.classStub;
	}

	public LCStub getLCStub()
	{
		return this.lcStub;
	}

	public UIStub getUIStub()
	{
		return this.uiStub;
	}

	public ViewModelStub getViewModelStub()
	{
		return this.viewModelStub;
	}

	@Override
	public CodeObjectInfo getCode(String codeGuid) throws ServiceRequestException
	{
		return this.getCodeStub().getCodeInfo(codeGuid);
	}

	@Override
	public CodeObjectInfo getCodeByName(String codeName) throws ServiceRequestException
	{
		return this.getCodeStub().getCodeByName(codeName);
	}

	@Override
	public CodeItemInfo getCodeItem(String codeItemGuid) throws ServiceRequestException
	{
		return this.getCodeStub().getCodeItemInfo(codeItemGuid);
	}

	@Override
	public CodeItemInfo getCodeItemByName(String codeName, String codeItemName) throws ServiceRequestException
	{
		return this.getCodeStub().getCodeItemInfoByName(codeName, codeItemName);
	}

	@Override
	public List<CodeObjectInfo> listCodeInfo() throws ServiceRequestException
	{
		return this.getCodeStub().listCode();
	}

	@Override
	public List<CodeItemInfo> listAllCodeItemInfoByMaster(String codeGuid, String codeName) throws ServiceRequestException
	{
		return this.getCodeStub().listAllCodeItem(codeGuid, codeName);
	}

	@Override
	public List<CodeItemInfo> listSubCodeItemForMaster(String codeGuid, String codeName) throws ServiceRequestException
	{
		return this.getCodeStub().listSubCodeItemForMaster(codeGuid, codeName);
	}

	@Override
	public List<CodeItemInfo> listSubCodeItemForDetail(String codeItemGuid) throws ServiceRequestException
	{
		return this.getCodeStub().listSubCodeItemForDetail(codeItemGuid);
	}

	@Override
	public List<CodeItemInfo> listAllSubCodeItemInfoByDatail(String codeItemGuid) throws ServiceRequestException
	{
		return this.getCodeStub().listAllSubCodeItemInfoByDatail(codeItemGuid, false);
	}

	@Override
	public List<CodeItemInfo> listAllSubCodeItemInfoByDatailContain(String codeItemGuid) throws ServiceRequestException
	{
		return this.getCodeStub().listAllSubCodeItemInfoByDatail(codeItemGuid, true);
	}

	@Override
	public List<CodeItemInfo> listLeafCodeItemInfoByMaster(String codeGuid, String codeName) throws ServiceRequestException
	{
		return this.getCodeStub().listLeafCodeItemInfoByMaster(codeGuid, codeName);
	}

	@Override
	public List<CodeItemInfo> listLeafCodeItemInfoByDatail(String codeItemGuid) throws ServiceRequestException
	{
		return this.getCodeStub().listLeafCodeItemInfoByDatail(codeItemGuid);
	}

	@Override
	public List<CodeItemInfo> listAllSuperCodeItemInfo(String codeItemGuid) throws ServiceRequestException
	{
		return this.getCodeStub().listAllSuperCodeItemInfo(codeItemGuid);
	}

	@Override
	public ClassificationUIInfo getCFUIObject(String classificationGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		return this.getClassificationStub().getCFUIObject(classificationGuid, uiType);
	}

	@Override
	public List<UIField> listCFUIField(String uiGuid) throws ServiceRequestException
	{
		return this.getClassificationStub().listUIFieldByUIGuid(uiGuid);
	}

	@Override
	public List<UIField> listCFUIField(String classificationGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		return this.getClassificationStub().listCFUIField(classificationGuid, uiType);
	}

	@Override
	public List<UIObjectInfo> listCFUIObjectByObjecGuid(ObjectGuid objectGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		return this.getClassificationStub().listCFUIObjectByObjecGuid(objectGuid, uiType);
	}

	@Override
	public List<ClassField> listClassificationField(String classificationItemGuid) throws ServiceRequestException
	{
		return this.getClassificationStub().listClassificationField(classificationItemGuid);
	}

	@Override
	public ClassField getClassificationField(String classificationItemGuid, String fieldName) throws ServiceRequestException
	{
		return this.getClassificationStub().getClassificationField(classificationItemGuid, fieldName);
	}

	@Override
	public List<ClassficationFeature> listClassficationFeature(String classguid) throws ServiceRequestException
	{
		return this.getClassificationStub().listClassficationFeature(classguid);
	}

	@Override
	public List<ClassficationFeatureItemInfo> listClassficationFeatureItem(String classiFeatrueguid, String classificationItemGuid) throws ServiceRequestException
	{
		return this.getClassificationStub().listClassficationFeatureItem(classiFeatrueguid, classificationItemGuid);
	}

	@Override
	public List<ClassificationNumberField> listClassificationNumberField(String classificationnumberregular) throws ServiceRequestException
	{
		return this.getClassificationStub().listClassificationNumberField(classificationnumberregular);
	}

	@Override
	public List<BOInfo> listBOInfoByClassficationGuid(String classficationguid) throws ServiceRequestException
	{
		return this.getViewModelStub().listBOInfoByClassficationGuid(classficationguid);
	}

	@Override
	public BOInfo getCurrentBizObject(String classGuid) throws ServiceRequestException
	{
		String bmGuid = ((UserSignature) this.getSignature()).getLoginGroupBMGuid();
		BOInfo bizObject = this.getBMStub().getBizObject(bmGuid, classGuid, null);
		if (bizObject == null)
		{
			throw new ServiceRequestException("not found biz object by " + classGuid);
		}
		return bizObject;
	}

	@Override
	public BOInfo getBizObject(String bmGuid, String classGuid, String classificationGuid) throws ServiceRequestException
	{
		return this.getBMStub().getBizObject(bmGuid, classGuid, classificationGuid);
	}

	@Override
	public BOInfo getBizObject(String bmGuid, String boGuid) throws ServiceRequestException
	{
		return this.getBMStub().getBizObject(bmGuid, boGuid);
	}

	@Override
	public BOInfo getBoInfoByNameAndBM(String bmGuid, String boInfoName) throws ServiceRequestException
	{
		return this.getBMStub().getBoInfoByNameAndBM(bmGuid, boInfoName, true);
	}

	@Override
	public BOInfo getCurrentBizObjectByGuid(String boGuid) throws ServiceRequestException
	{
		String bmGuid = ((UserSignature) this.getSignature()).getLoginGroupBMGuid();
		return this.getBMStub().getBizObject(bmGuid, boGuid);
	}

	@Override
	public BOInfo getCurrentBoInfoByName(String boInfoName, boolean isThrowException) throws ServiceRequestException
	{
		return this.getBMStub().getCurrentBoInfoByName(boInfoName, isThrowException);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#getClassNameByGuid(java.lang.String)
	 */
	@Override
	public ClassInfo getClassByGuid(String classGuid) throws ServiceRequestException
	{
		return this.getClassStub().getClassByGuid(classGuid);
	}

	@Override
	public ClassInfo getClassByName(String classObjectName) throws ServiceRequestException
	{
		return this.getClassStub().getClassByName(classObjectName);
	}

	@Override
	public Set<String> getCodeFieldNamesInSC(SearchCondition condition) throws ServiceRequestException
	{
		return this.getClassStub().getObjectFieldNamesInSC(condition, false);
	}

	@Override
	public Set<String> getCodeFieldNames(String className, List<String> resultFields) throws ServiceRequestException
	{
		return this.getClassStub().getObjectFieldNamesInSC(className, null, resultFields, false);
	}

	@Override
	public BMInfo getCurrentBizModel() throws ServiceRequestException
	{
		return this.getBMStub().getCurrentBizModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#getClassFieldByName(java.lang.String, java.lang.String)
	 */
	@Override
	public ClassField getFieldByName(String classObjectName, String fieldName, boolean isThrowException) throws ServiceRequestException
	{
		return this.getClassStub().getFieldByName(classObjectName, fieldName, isThrowException);
	}

	@Override
	public UIField getUIFieldByName(String classObjectName, String fieldName) throws ServiceRequestException
	{
		return this.getUIStub().getUIFieldByName(classObjectName, fieldName);
	}

	@Override
	public LifecyclePhaseInfo getFirstLifecyclePhaseInfoByClassName(String className) throws ServiceRequestException
	{
		return this.getLCStub().getFirstLifecyclePhaseInfoByClassName(className);
	}

	@Override
	public UIObjectInfo getUIObjectInCurrentBizModel(String classObjectName, UITypeEnum type) throws ServiceRequestException
	{
		return this.getUIStub().getUIObjectByBizModel(classObjectName, type);
	}

	@Override
	public LifecycleInfo getLifecycleInfoByGuid(String lifecycleInfoGuid) throws ServiceRequestException
	{
		return this.getLCStub().getLifecycleInfoByGuid(lifecycleInfoGuid);
	}

	@Override
	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecyclePhaseInfoGuid) throws ServiceRequestException
	{
		return this.getLCStub().getLifecyclePhaseInfo(lifecyclePhaseInfoGuid);
	}

	@Override
	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecycleInfoName, String lifecyclePhaseInfoName) throws ServiceRequestException
	{
		return this.getLCStub().getLifecyclePhaseInfo(lifecycleInfoName, lifecyclePhaseInfoName);
	}

	@Override
	public NumberingModel lookupNumberingModel(String classGuid, String className, String classFieldName) throws ServiceRequestException
	{
		return this.getClassStub().lookupNumberingModel(classGuid, className, classFieldName);
	}

	@Override
	public NumberingModel lookupNumberingModel(String classGuid, String className, String classFieldName, boolean isContainSynthesis) throws ServiceRequestException
	{
		return this.getClassStub().lookupNumberingModel(classGuid, className, classFieldName, isContainSynthesis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#getObjectFieldNamesInSC(dyna.common.SearchCondition)
	 */
	@Override
	public Set<String> getObjectFieldNamesInSC(SearchCondition condition) throws ServiceRequestException
	{
		return this.getClassStub().getObjectFieldNamesInSC(condition, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#getObjectFieldNamesInSC(dyna.common.SearchCondition)
	 */
	@Override
	public Set<String> getObjectFieldNames(String className, List<String> resultFields) throws ServiceRequestException
	{
		return this.getClassStub().getObjectFieldNamesInSC(className, null, resultFields, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public boolean isSuperClass(String classNameSub, String classNameSuper) throws ServiceRequestException
	{
		return this.getClassStub().isSuperClass(classNameSub, classNameSuper);
	}

	@Override
	public List<BOInfo> listAllSubBOInfo(String boName) throws ServiceRequestException
	{
		return this.getBMStub().listAllSubBOInfo(boName, false);
	}

	@Override
	public List<BOInfo> listAllSubBOInfoContain(String boName) throws ServiceRequestException
	{
		return this.getBMStub().listAllSubBOInfo(boName, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listAllSubBOInfoContain(java.lang.String, java.lang.String)
	 */
	@Override
	public List<BOInfo> listAllSubBOInfoContain(String boName, String bmGuid) throws ServiceRequestException
	{
		return this.getBMStub().listAllSubBOInfo(boName, bmGuid, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listAllSuperClass(java.lang.String)
	 */
	@Override
	public List<ClassInfo> listAllSuperClass(String className, String classGuid) throws ServiceRequestException
	{
		return this.getClassStub().listAllSuperClass(className, classGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listBizModelName()
	 */
	@Override
	public List<BMInfo> listBizModel() throws ServiceRequestException
	{
		return this.getBMStub().listBizModel();
	}

	@Override
	public List<BOInfo> listBizObjectByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		BMInfo currentBizModel = this.getCurrentBizModel();
		if (currentBizModel == null)
		{
			return null;
		}
		String bmGuid = currentBizModel.getGuid();
		return this.getBMStub().listBOInfoByInterface(interfaceEnum, bmGuid);
	}

	@Override
	public List<BOInfo> listBizObjectByInterface(ModelInterfaceEnum interfaceEnum, String modelGuid) throws ServiceRequestException
	{
		if (modelGuid != null)
		{
			return this.getBMStub().listBOInfoByInterface(interfaceEnum, modelGuid);
		}
		else
		{
			List<BMInfo> bmInfoList = this.listBizModel();
			if (SetUtils.isNullList(bmInfoList))
			{
				return null;
			}
			List<BOInfo> boInfoList = new ArrayList<>();
			List<String> classNameList = new ArrayList<>();
			for (BMInfo bmInfo : bmInfoList)
			{
				boInfoList.addAll(this.getBMStub().listBOInfoByInterfaceAndBM(classNameList, interfaceEnum, bmInfo.getGuid()));
			}
			return boInfoList;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listBizObjectOfModel(java.lang.String)
	 */
	@Override
	public List<BOInfo> listBizObjectOfModelNonContainOthers(String modelName) throws ServiceRequestException
	{
		return this.getBMStub().listBizObjectOfModelNonContainOthers(modelName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listBizObjectOfModel(java.lang.String)
	 */
	@Override
	public List<BOInfo> listBizObjectOfModelNonContainOthersByBMGuid(String modelGuid) throws ServiceRequestException
	{
		String modelName = this.getBMStub().getBizModelNameByGuid(modelGuid);
		if (modelName == null)
		{
			return null;
		}
		return this.getBMStub().listBizObjectOfModelNonContainOthers(modelName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listBizObjectOfModel(java.lang.String)
	 */
	@Override
	public List<BOInfo> listBizObjectOfModel(String modelName) throws ServiceRequestException
	{
		return this.getBMStub().listBizObjectOfModel(modelName);
	}

	@Override
	public List<BOInfo> listBizObjectOfModelByBMGuid(String modelGuid) throws ServiceRequestException
	{
		String modelName = this.getBMStub().getBizModelNameByGuid(modelGuid);

		if (modelName == null)
		{
			return null;
		}
		return this.getBMStub().listBizObjectOfModel(modelName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listAllClass()
	 */
	@Override
	public List<ClassInfo> listClassInfo() throws ServiceRequestException
	{
		return this.getClassStub().listAllClass(null);
	}

	@Override
	public List<ClassInfo> listClassByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.getClassStub().listClassByInterface(interfaceEnum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listClassFieldOfClass(java.lang.String)
	 */
	@Override
	public List<ClassField> listFieldOfClass(String className) throws ServiceRequestException
	{
		return this.getClassStub().listFieldOfClass(className);
	}

	@Override
	public List<LifecycleInfo> listLifeCycleInfo() throws ServiceRequestException
	{
		return this.getLCStub().listLifeCycleInfo();
	}

	@Override
	public List<LifecyclePhaseInfo> listLifeCyclePhase(String lifeCycleName) throws ServiceRequestException
	{
		return this.getLCStub().listLifeCyclePhase(lifeCycleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listUIFieldByUIObject(java.lang.String, java.lang.String)
	 */
	@Override
	public List<UIField> listUIFieldByUIObject(String classObjectName, String uiObjectName) throws ServiceRequestException
	{
		return this.getUIStub().listUIFieldByUIObject(classObjectName, uiObjectName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listFormUIField(java.lang.String)
	 */
	@Override
	public List<UIField> listFormUIField(String className) throws ServiceRequestException
	{
		return this.getUIStub().listFormUIField(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listListUIField(java.lang.String)
	 */
	@Override
	public List<UIField> listListUIField(String className) throws ServiceRequestException
	{
		return this.getUIStub().listListUIField(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listReportUIField(java.lang.String)
	 */
	@Override
	public List<UIField> listReportUIField(String className) throws ServiceRequestException
	{
		return this.getUIStub().listReportUIField(className);
	}

	@Override
	public List<ClassField> listClassFieldByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.getInterfaceStub().listClassFieldByInterface(interfaceEnum);
	}

	@Override
	public BMInfo getBizModel(String bmGuid) throws ServiceRequestException
	{
		return this.getBMStub().getBizModel(bmGuid);
	}

	@Override
	public List<BOInfo> listSharedBizObject() throws ServiceRequestException
	{
		return this.getBMStub().listSharedBizObject();
	}

	@Override
	public BMInfo getSharedBizModel() throws ServiceRequestException
	{
		return this.getBMStub().getSharedBizModel();
	}

	@Override
	public boolean hasNumberingModelByField(String classGuid, String className, String classFieldName) throws ServiceRequestException
	{
		NumberingModel numberingModel = this.lookupNumberingModel(classGuid, className, classFieldName);

		if (numberingModel == null)
		{
			return false;
		}

		List<NumberingObject> numberingObjectInfoList = numberingModel.getNumberingObjectList();
		return !SetUtils.isNullList(numberingObjectInfoList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listSectionUIField(java.lang.String)
	 */
	@Override
	public List<UIField> listSectionUIField(String className) throws ServiceRequestException
	{
		return this.getUIStub().listSectionUIField(className);
	}

	@Override
	public List<BOInfo> listSubBOInfo(String boName) throws ServiceRequestException
	{
		return this.getBMStub().listSubBOInfo(boName);
	}

	@Override
	public Date getSystemDate()
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.getTime();
	}

	@Override
	public List<UIObjectInfo> listUIObjectInBizModel(String bmGuid, String classObjectName, UITypeEnum uiType, boolean isOnlyVisible) throws ServiceRequestException
	{
		return this.getUIStub().listUIObjectByBizModel(classObjectName, bmGuid, uiType, isOnlyVisible);
	}

	@Override
	public List<UIObjectInfo> listUIObjectInCurrentBizModel(String classObjectName, UITypeEnum uiType, boolean isOnlyVisible) throws ServiceRequestException
	{
		String bmGuid = this.getCurrentBizModel().getGuid();
		return this.getUIStub().listUIObjectByBizModel(classObjectName, bmGuid, uiType, isOnlyVisible);
	}

	@Override
	public List<UIAction> listUIAction(String uiGuid) throws ServiceRequestException
	{
		return this.getUIStub().listUIAction(uiGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listSubClass(java.lang.String, java.lang.String, boolean,
	 * dyna.common.systemenum.ModelInterfaceEnum)
	 */
	@Override
	public List<ClassInfo> listSubClass(String className, String classGuid, boolean cascade, ModelInterfaceEnum removeInterface) throws ServiceRequestException
	{
		return this.getClassStub().listSubClass(className, classGuid, cascade, null, removeInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listClass(dyna.common.systemenum.ModelInterfaceEnum)
	 */
	@Override
	public List<ClassInfo> listClassOutInterface(ModelInterfaceEnum removeInterface) throws ServiceRequestException
	{
		return this.getClassStub().listAllClass(removeInterface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#getUIObjectByName(java.lang.String, java.lang.String)
	 */
	@Override
	public UIObjectInfo getUIObjectByName(String className, String uiName) throws ServiceRequestException
	{
		return this.getUIStub().getUIObjectByName(className, uiName);
	}

	@Override
	public BOInfo getCurrentBoInfoByClassName(String className) throws ServiceRequestException
	{
		return this.getBMStub().getCurrentBoInfoByClassName(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.EMM#listALLFormListUIObjectInBizModel(java.lang.String)
	 */
	@Override
	public List<UIObjectInfo> listALLFormListUIObjectInBizModel(String classObjectName) throws ServiceRequestException
	{
		return this.getUIStub().listALLFormListUIObjectInBizModel(classObjectName);
	}

	@Override
	public List<ClassInfo> listAllSubClassInfoOnlyLeaf(ModelInterfaceEnum interfaceEnum, String className) throws ServiceRequestException
	{
		return this.getClassStub().listAllSubClassInfoOnlyLeaf(interfaceEnum, className);
	}

	@Override
	public ClassInfo getFirstLevelClassByItem() throws ServiceRequestException
	{
		return this.getClassStub().getFirstLevelClassByInterface(ModelInterfaceEnum.IItem, null);
	}

	@Override
	public ClassInfo getFirstLevelClassByInterface(ModelInterfaceEnum interfaceType, List<String> excludeList) throws ServiceRequestException
	{
		return this.getClassStub().getFirstLevelClassByInterface(interfaceType, excludeList);
	}

	@Override
	public List<UIField> listUIFieldByUIGuid(String uiGuid) throws ServiceRequestException
	{
		return this.getUIStub().listUIField(uiGuid);
	}

	@Override
	public RelationTemplateInfo getRelationTemplateByName(ObjectGuid objectGuid, String templateName) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().getRelationTemplateByName(objectGuid, templateName);
	}

	@Override
	public RelationTemplate getRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		RelationTemplate template = this.getRelationTemplateStub().getRelationTemplate(relationTemplateGuid);
		return template;
	}

	@Override
	public RelationTemplateInfo getRelationTemplateById(String id) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().getRelationTemplateById(id);
	}

	@Override
	public List<RelationTemplateInfo> listRelationTemplateByName(String templateName, boolean containObs) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().listRelationTemplateByName(templateName, containObs);
	}

	@Override
	public List<RelationTemplateInfo> listRelationTemplate(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().listRelationTemplate(objectGuid, "1");
	}
	
	@Override
	public List<RelationTemplateInfo> listRelationTemplate4Opposite(ObjectGuid end2ObjectGuid) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().listRelationTemplate4Opposite(end2ObjectGuid);
	}

	@Override
	public List<RelationTemplateInfo> listRelationTemplate(ObjectGuid objectGuid, boolean isContainBuiltin) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().listRelationTemplate(objectGuid, isContainBuiltin ? "1" : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#listRelationTemplate4Builtin(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<RelationTemplateInfo> listRelationTemplate4Builtin(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<RelationTemplateInfo> templateList = this.getRelationTemplateStub().listRelationTemplate4Builtin(end1ObjectGuid);
		return templateList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BOAS#listRelationTemplateName()
	 */
	@Override
	public List<String> listRelationTemplateName(boolean containObs) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().listRelationTemplateName(null, containObs);
	}

	@Override
	public List<RelationTemplateInfo> listEnableRelationTemplate4End1BO(String bmGuid, List<String> end1boGuidList, boolean isContainObsolete) throws ServiceRequestException
	{
		List<RelationTemplateInfo> templateList = this.getRelationTemplateStub().listEnableRelationTemplate4End1BO(bmGuid, end1boGuidList, isContainObsolete);
		return templateList;
	}

	@Override
	public List<RelationTemplateInfo> listRelationTemplateByEND2(ObjectGuid objectGuid) throws ServiceRequestException
	{
		List<RelationTemplateInfo> templateList = this.getRelationTemplateStub().listRelationTemplateByEND2(objectGuid);
		return templateList;
	}

	@Override
	public BOMTemplate getBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		return this.getBomTemplateStub().getBOMTemplate(bomTemplateGuid);
	}

	@Override
	public BOMTemplateInfo getBOMTemplateById(String id) throws ServiceRequestException
	{
		return this.getBomTemplateStub().getBOMTemplateInfoById(id);
	}

	@Override
	public BOMTemplateInfo getBOMTemplateByName(ObjectGuid objectGuid, String templateName) throws ServiceRequestException
	{
		return this.getBomTemplateStub().getBOMTemplateInfoByName(objectGuid, templateName);
	}

	@Override
	public List<BOMTemplateInfo> listBOMTemplate() throws ServiceRequestException
	{
		return this.getBomTemplateStub().listAllBOMTemplate(false);
	}

	@Override
	public List<BOMTemplateInfo> listBOMTemplateByEND1(ObjectGuid objectGuid) throws ServiceRequestException
	{
		String bmGuid = this.getCurrentBizModel().getGuid();
		return this.getBomTemplateStub().listBOMTemplateInfoByEND1(objectGuid, bmGuid);
	}

	@Override
	public List<BOMTemplateInfo> listBOMTemplateByEND1(ObjectGuid objectGuid, String bmGuid) throws ServiceRequestException
	{
		return this.getBomTemplateStub().listBOMTemplateInfoByEND1(objectGuid, bmGuid);
	}

	@Override
	public List<BOMTemplateInfo> listBOMTemplateByName(String templateName, boolean containObs) throws ServiceRequestException
	{
		return this.getBomTemplateStub().listBOMTemplateInfoByName(templateName, containObs);
	}

	@Override
	public List<String> listBOMTemplateName(boolean containObs) throws ServiceRequestException
	{
		return this.getBomTemplateStub().listAllBOMTemplateName(containObs);
	}

	@Override
	public List<BOMTemplateInfo> listBOMTemplateByEND2(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return this.getBomTemplateStub().listBOMTemplateInfoByEND2(objectGuid);
	}

	@Override
	public List<BOMTemplateInfo> listAllBOMTemplateByEnd1BO(String boName, boolean isContainInValid) throws ServiceRequestException
	{
		return this.getBomTemplateStub().listAllBOMTemplateInfoByEnd1BO(boName, isContainInValid);
	}

	@Override
	public List<String> listBOMReportTemplateName() throws ServiceRequestException
	{
		return this.getBomTemplateStub().listBOMReportTemplateName();
	}

	@Override
	public List<LifecycleGate> listLifecyclePhaseGate(String lifecyclePhaseGuid) throws ServiceRequestException
	{
		return null;
	}

	@Override
	public List<ClassificationUIInfo> listCFUIObject(String classificationGuid) throws ServiceRequestException
	{
		return this.getClassificationStub().listCFUIObjectInfo(classificationGuid);
	}

	@Override
	public InterfaceObject getInterfaceObjectByName(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.getInterfaceStub().getInterfaceObjectByName(interfaceEnum);
	}

	@Override
	public List<UIObjectInfo> listAllUIObject(String classObjectName) throws ServiceRequestException
	{
		return this.getUIStub().listUiObjectInfo(null, classObjectName);
	}

	@Override
	public List<BOInfo> listRootBizObject(String modelGuid) throws ServiceRequestException
	{
		return this.getBMStub().listRootBizObjectByGuid(modelGuid);
	}

	@Override
	public List<BOInfo> listSubBOInfoByBM(String modelGuid, String boGuid) throws ServiceRequestException
	{
		return this.getBMStub().listSubBOInfoByBM(modelGuid, boGuid);
	}

	@Override
	public List<UIObjectInfo> listUIObjectInfo(String classguid, String className) throws ServiceRequestException
	{
		return this.getUIStub().listUiObjectInfo(classguid, className);
	}

	@Override
	public List<ClassAction> listClassAction(String classguid) throws ServiceRequestException
	{
		return this.getClassStub().listClassAction(classguid);
	}

	@Override
	public List<NumberingModel> listNumberingModel(String classGuid, String className, boolean isNumbering) throws ServiceRequestException
	{
		return this.getClassStub().listNumberingModel(classGuid, className, isNumbering);
	}

	@Override
	public ClassInfo getClassInfoByBOGuid(String boGuid) throws ServiceRequestException
	{
		return this.getViewModelStub().getClassInfoByBOGuid(boGuid);
	}

	@Override
	public LifecycleInfo getLifecycleInfoByBOGuid(String boGuid) throws ServiceRequestException
	{
		return this.getViewModelStub().getLifecycleInfoByBOGuid(boGuid);
	}

	@Override
	public List<BOInfo> listRelationEnd2BoInfo(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException
	{
		return this.getViewModelStub().listRelationEnd2BoInfo(relationTemplateGuid, isBOM, isTree);
	}

	@Override
	public List<BOInfo> listEnd2LeafBoInfoByTemplateGuid(String guid, boolean isBOM) throws ServiceRequestException
	{
		return this.getViewModelStub().listEnd2LeafBoInfoByTemplateGuid(guid, isBOM);
	}

	@Override
	public List<UIField> listAssoUITableColumn(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException
	{
		return this.getViewModelStub().listAssoUITableColumn(relationTemplateGuid, isBOM, isTree);
	}

	@Override
	public SearchCondition createAssoSearchCondition(String strcutureClassName) throws ServiceRequestException
	{
		return this.getUIStub().createAssoSearchCondition(strcutureClassName);
	}

	@Override
	public SearchCondition createBOSearchCondition(String className) throws ServiceRequestException
	{
		return this.getUIStub().createBOSearchCondition(className);
	}

	@Override
	public SearchCondition createClassificationSearchCondition(String className, String itemName) throws ServiceRequestException
	{
		return this.getViewModelStub().createClassificationSearchCondition(className, itemName);
	}

	@Override
	public SearchCondition createAssoEnd2SearchCondition(String relationTemplateGuid, boolean isBOM, boolean isTree) throws ServiceRequestException
	{
		return this.getViewModelStub().createAssoEnd2SearchCondition(relationTemplateGuid, isBOM, isTree);
	}

}
