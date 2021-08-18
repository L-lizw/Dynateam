package dyna.app.service.brs.mms;

import dyna.app.service.BusinessRuleService;
import dyna.common.bean.configure.ProjectModel;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.bean.model.lf.Lifecycle;
import dyna.common.bean.model.ui.UIIcon;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.bean.model.wf.WorkflowProcess;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.model.wf.WorkflowProcessInfo;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.service.brs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Service
public class MMSImpl extends BusinessRuleService implements MMS
{
	@Autowired
	private ModelManageStub				modelManageStub				= null;
	@Autowired
	private CodeManageStub				codeManageStub				= null;
	@Autowired
	private UIManageStub				uiManageStub				= null;
	@Autowired
	private ModelDeployStub				modelDeployStub				= null;
	@Autowired
	private SystemObjectManagerStub		soManagerStub				= null;
	@Autowired
	private BMBOManagerStub				bmboManagerStub				= null;
	@Autowired
	private LifecycleManagerStub		lifecycleManagerStub		= null;
	@Autowired
	private WorkflowProcessManagerStub	wfProcessManagerStub		= null;
	@Autowired
	private ClassficationFeatureStub	classficationFeatureStub	= null;
	@Autowired
	private RelationTemplateStub		relationTemplateStub		= null;
	@Autowired
	private BOMTemplateStub				bomTemplateStub				= null;

	public ModelManageStub getModelManageStub()
	{
		return this.modelManageStub;
	}

	public ModelDeployStub getModelDeployStub()
	{
		return this.modelDeployStub;
	}

	public UIManageStub getUIManageStub()
	{
		return this.uiManageStub;
	}

	public SystemObjectManagerStub getSoManagerStub()
	{
		return this.soManagerStub;
	}

	public BMBOManagerStub getBMBOManagerStub()
	{
		return this.bmboManagerStub;
	}

	public CodeManageStub getCodeManagerStub()
	{
		return this.codeManageStub;
	}

	public LifecycleManagerStub getLifecycleManagerStub()
	{
		return this.lifecycleManagerStub;
	}

	public WorkflowProcessManagerStub getWfProcessManagerStub()
	{
		return this.wfProcessManagerStub;
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

	public ClassficationFeatureStub getClassficationFeatureStub()
	{
		return this.classficationFeatureStub;
	}

	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		if (this.getSignature() != null && this.getSignature() instanceof ModuleSignature
				&& ("getCurrentSyncModel".equals(method.getName()) || "makeModelFile".equals(method.getName())))
		{
			return;
		}
		super.authorize(method, args);
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

	protected synchronized WFM getWFM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	@Override
	public void createCodeItem(CodeItemInfo codeItemInfo) throws ServiceRequestException
	{
		this.getCodeManagerStub().createCodeItem(codeItemInfo, codeItemInfo.getCodeGuid(), codeItemInfo.getParentGuid(), false);
	}

	@Override
	public void createCodeObject(CodeObjectInfo codeObjectInfo) throws ServiceRequestException
	{
		this.getCodeManagerStub().createCodeObject(codeObjectInfo, false);
	}

	@Override
	public <T extends SystemObject> T saveModel(T model) throws ServiceRequestException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveClassObject(ClassInfo classInfo) throws ServiceRequestException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void saveClassField(ClassField classField) throws ServiceRequestException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void saveUIObject(UIObject uiObject) throws ServiceRequestException
	{
		this.getUIManageStub().saveUIObject(uiObject);
	}

	@Override
	public void saveUIField(UIField uiField) throws ServiceRequestException
	{
		this.getUIManageStub().saveUIField(uiField);
	}

	@Override
	public void saveUIAction(UIAction uiAction) throws ServiceRequestException
	{
		this.getUIManageStub().saveUIAction(uiAction);
	}

	@Override
	public void deleteUIField(String uiFieldGuid) throws ServiceRequestException
	{
		this.getUIManageStub().deleteUIField(uiFieldGuid);
	}

	@Override
	public void deleteUIAction(String uiActionGuid) throws ServiceRequestException
	{
		this.getUIManageStub().deleteUIAction(uiActionGuid);
	}

	@Override
	public void deleteUIObject(String uiGuid) throws ServiceRequestException
	{
		this.getUIManageStub().deleteUIObject(uiGuid);
	}

	@Override
	public ProjectModel getCurrentSyncModel() throws ServiceRequestException
	{
		return this.getModelManageStub().getCurrentSyncModel();
	}

	@Override
	public ProjectModel makeModelFile() throws ServiceRequestException
	{
		return this.getModelManageStub().makeModelFile();
	}

	@Override
	public ProjectModel deploy(ProjectModel projectModel) throws ServiceRequestException
	{
		return this.getModelDeployStub().deploy(projectModel);
	}

	@Override
	public boolean isModelSync(ProjectModel projectModel) throws ServiceRequestException
	{
		return this.getModelDeployStub().isModelSync(projectModel);
	}

	@Override
	public boolean isDeployLock() throws ServiceRequestException
	{
		return this.getModelDeployStub().isDeployLock();
	}

	@Override
	public void saveNormalClassficationFeature(String classguid, List<ClassficationFeature> classficationFeatures) throws ServiceRequestException
	{
		this.getClassficationFeatureStub().saveNormalClassficationFeature(classguid, classficationFeatures);
	}

	@Override
	public void deleteNormalClassficationFeature(String classguid, List<String> classficationFeatureGuids) throws ServiceRequestException
	{
		this.getClassficationFeatureStub().deleteNormalClassficationFeature(classguid, classficationFeatureGuids);
	}

	@Override
	public String saveClassificationFeatureItem(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException
	{
		return this.getClassficationFeatureStub().saveClassificationFeatureItem(classficationFeatureItem);
	}

	@Override
	public void saveClassificationNumberField(String classificationFeatureItemGuid, List<ClassificationNumberField> classificationNumberFieldList) throws ServiceRequestException
	{
		this.getClassficationFeatureStub().saveClassificationNumberField(classificationFeatureItemGuid, classificationNumberFieldList);
	}

	@Override
	public void deleteClassificationFeatureItem(String classificationregularguid) throws ServiceRequestException
	{
		this.getClassficationFeatureStub().deleteClassificationFeatureItem(classificationregularguid);
	}

	@Override
	public void deleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		this.getBomTemplateStub().deleteBOMTemplate(bomTemplateGuid);
	}

	@Override
	public void deleteBOMTemplateByName(String templateName) throws ServiceRequestException
	{
		this.getBomTemplateStub().deleteBOMTemplateByName(templateName);
	}

	@Override
	public void obsoleteBOMTemplateByName(String templateName) throws ServiceRequestException
	{
		this.getBomTemplateStub().obsoleteBOMTemplateByName(templateName);
	}

	@Override
	public void reUseBOMTemplateByName(String templateName) throws ServiceRequestException
	{
		this.getBomTemplateStub().reUseBOMTemplateByName(templateName);
	}

	@Override
	public void obsoleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		this.getBomTemplateStub().obsoleteBOMTemplate(bomTemplateGuid);
	}

	@Override
	public void reUseBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		this.getBomTemplateStub().reUseBOMTemplate(bomTemplateGuid);
	}

	@Override
	public BOMTemplate saveBOMTemplate(BOMTemplate bomTemplate) throws ServiceRequestException
	{
		return this.getBomTemplateStub().saveBOMTemplate(bomTemplate);
	}

	@Override
	public void deleteRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		this.getRelationTemplateStub().deleteRelationTemplate(relationTemplateGuid);
	}

	@Override
	public void obsoleteRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		this.getRelationTemplateStub().obsoleteRelationTemplate(relationTemplateGuid);
	}

	@Override
	public void deleteRelationTemplateByName(String templateName) throws ServiceRequestException
	{
		this.getRelationTemplateStub().deleteRelationTemplateByName(templateName);
	}

	@Override
	public void obsoleteRelationTemplateByName(String templateName) throws ServiceRequestException
	{
		this.getRelationTemplateStub().obsoleteRelationTemplateByName(templateName);
	}

	@Override
	public void reUseRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		this.getRelationTemplateStub().reUseRelationTemplate(relationTemplateGuid);
	}

	@Override
	public void reUseRelationTemplateByName(String templateName) throws ServiceRequestException
	{
		this.getRelationTemplateStub().reUseRelationTemplateByName(templateName);
	}

	@Override
	public RelationTemplate saveRelationTemplate(RelationTemplate relationTemplate) throws ServiceRequestException
	{
		return this.getRelationTemplateStub().saveRelationTemplate(relationTemplate);
	}

	@Override
	public void deleteBMInfo(String bmInfoguid) throws ServiceRequestException
	{
		this.getBMBOManagerStub().deleteBMInfo(bmInfoguid);
	}

	@Override
	public void deleteBOInfo(String boInfoguid) throws ServiceRequestException
	{
		this.getBMBOManagerStub().deleteBOInfo(boInfoguid);
	}

	@Override
	public String saveSystemObject(SystemObject systemObject) throws ServiceRequestException
	{
		return this.getSoManagerStub().saveSystemObject(systemObject);
	}

	@Override
	public void saveCodeItem(CodeItemInfo codeItemInfo) throws ServiceRequestException
	{
		this.getSoManagerStub().saveSystemObject(codeItemInfo);
	}

	@Override
	public void saveCodeObject(CodeObjectInfo codeObjectInfo) throws ServiceRequestException
	{
		this.getSoManagerStub().saveSystemObject(codeObjectInfo);
	}

	@Override
	public void deleteCodeObject(String codeObjectguid) throws ServiceRequestException
	{
		this.getCodeManagerStub().deleteCodeObject(codeObjectguid);
	}

	@Override
	public void deleteCodeItem(String codeItemguid) throws ServiceRequestException
	{
		this.getCodeManagerStub().deleteCodeItem(codeItemguid);
	}

	@Override
	public List<UIObjectInfo> copy4CreateUIObject(List<UIObjectInfo> sourceUIobjectList, String toClassguid, boolean isCut) throws ServiceRequestException
	{
		return this.getUIManageStub().copy4CreateUIObjct(sourceUIobjectList, toClassguid, isCut);
	}

	@Override
	public List<BMInfo> copy4CreateBusinessModle(List<BMInfo> sourceBMInfoList) throws ServiceRequestException
	{
		return this.getBMBOManagerStub().copy4CreateBusinessModel(sourceBMInfoList);
	}

	@Override
	public void copy4CreateBusinessObject(List<BOInfo> sourceBOInfoList, String bmguid, String parentBOguid) throws ServiceRequestException
	{
		this.getBMBOManagerStub().copy4CreateBusinessObject(sourceBOInfoList, bmguid, parentBOguid);
	}

	@Override
	public List<CodeObjectInfo> copy4CreateCodeObject(List<CodeObjectInfo> sourceCodeObjectInfoList, boolean isCut) throws ServiceRequestException
	{
		return this.getCodeManagerStub().copy4CreateCodeObject(sourceCodeObjectInfoList, isCut);
	}

	@Override
	public void copy4CreateCodeItem(List<CodeItemInfo> sourceCodeItemInfoList, String codeObjectguid, String parentCodeItemguid) throws ServiceRequestException
	{
		this.getCodeManagerStub().copy4createCodeItem(sourceCodeItemInfoList, codeObjectguid, parentCodeItemguid);
	}

	@Override
	public List<LifecycleInfo> copy4CreateLifeCycle(List<LifecycleInfo> sourceLifecycleInfoList) throws ServiceRequestException
	{
		return this.getLifecycleManagerStub().copy4CreateLifecycle(sourceLifecycleInfoList);
	}

	@Override
	public List<WorkflowProcessInfo> copy4CreateWorkflowProcess(List<WorkflowProcessInfo> sourceWfProcessInfoList) throws ServiceRequestException
	{
		return this.getWfProcessManagerStub().copy4CreateWorkflowProcess(sourceWfProcessInfoList);
	}

	@Override
	public void createBusinessModel(BMInfo bmInfo) throws ServiceRequestException
	{
		this.getBMBOManagerStub().createnewBusinessModel(bmInfo);
	}

	@Override
	public BOInfo createBusinessObject(BOInfo boInfo) throws ServiceRequestException
	{
		return this.getBMBOManagerStub().createBusinessObject(boInfo);
	}

	@Override
	public void deleteLifecycle(String lifecycleGuid) throws ServiceRequestException
	{
		this.getLifecycleManagerStub().deleteLifecycle(lifecycleGuid);
	}

	@Override
	public void deleteWorkflowProcess(String procGuid) throws ServiceRequestException
	{
		this.getWfProcessManagerStub().deleteWorkflowProcess(procGuid);
	}

	@Override
	public UIObjectInfo editUIField(UIObjectInfo uiInfo, List<UIField> uiFieldList) throws ServiceRequestException
	{
		return this.getUIManageStub().editUIField(uiInfo, uiFieldList);
	}

	@Override
	public void editUIObject(List<UIObjectInfo> uiObjectInfoList) throws ServiceRequestException
	{
		this.getUIManageStub().editUIObject(uiObjectInfoList);
	}

	@Override
	public void editCodeObject(CodeObject codeObject) throws ServiceRequestException
	{
		this.getCodeManagerStub().editCodeObject(codeObject);
	}

	@Override
	public void editNumberingModel(String classGuid, List<NumberingModel> numberingModelList, boolean isNumbering) throws ServiceRequestException
	{
		this.getModelManageStub().editNumberingModel(classGuid, numberingModelList, isNumbering);
	}

	@Override
	public Lifecycle editLifecycle(Lifecycle lifecycle) throws ServiceRequestException
	{
		return this.getLifecycleManagerStub().editLifecycle(lifecycle);
	}

	@Override
	public WorkflowProcess editWorkflowProcess(WorkflowProcess workflowProcess) throws ServiceRequestException
	{
		return this.getWfProcessManagerStub().editWorkflowProcess(workflowProcess);
	}

	@Override
	public ClassificationUIInfo editClassificationUIField(ClassificationUIInfo uiInfo, List<UIField> cfUIfieldList) throws ServiceRequestException
	{
		return this.getUIManageStub().editCFUIField(uiInfo, cfUIfieldList);
	}

	@Override
	public void editClassificationUI(List<ClassificationUIInfo> cfUIInfoList) throws ServiceRequestException
	{
		this.getUIManageStub().editClassificationUI(cfUIInfoList);
	}

	@Override
	public void editClassAction(String classGuid, List<ClassAction> actionList, boolean isAction) throws ServiceRequestException
	{
		this.getModelManageStub().editClassAction(classGuid, actionList, isAction);
	}

	@Override
	public List<BMInfo> editBMInfo(List<BMInfo> bmList, boolean isUp) throws ServiceRequestException
	{
		return this.getBMBOManagerStub().moveBmInfo(bmList, isUp);
	}

	@Override
	public List<BOInfo> editBOInfo(List<BOInfo> boList, boolean isUp) throws ServiceRequestException
	{
		return this.getBMBOManagerStub().moveBOInfo(boList, isUp);
	}

	@Override
	public void checkAndReBuildBusinessModel() throws ServiceRequestException
	{
		this.getBMBOManagerStub().checkAndReBuildBusinessModel();
	}

	@Override
	public void editIcon(UIIcon uiIcon) throws ServiceRequestException
	{
		this.getModelManageStub().editIcon(uiIcon);
	}

	@Override
	public void updateSerialNumber(FoundationObject foundationObject) throws ServiceRequestException
	{
		this.getSoManagerStub().updateSerialNumber(foundationObject);
	}

	@Override
	public void deployClassification(List<Map<String, String>> dataSource, String sessionId) throws ServiceRequestException
	{
		this.getModelDeployStub().deployClassification(dataSource, sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.MMS#saveIcon(java.lang.String, byte[])
	 */
	@Override
	public void saveIcon(String filename, byte[] imageData) throws ServiceRequestException
	{
		this.getModelManageStub().saveIcon(filename, imageData);
	}
}
