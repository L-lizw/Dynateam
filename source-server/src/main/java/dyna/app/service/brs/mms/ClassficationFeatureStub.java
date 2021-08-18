package dyna.app.service.brs.mms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ClassficationFeatureStub extends AbstractServiceStub<MMSImpl>
{

	protected List<ClassficationFeature> listAllClassficationFeature()
	{
		List<ClassficationFeature> classficationFeatureList = DataServer.getClassificationFeatureService().listAllClassficationFeature();
		return classficationFeatureList;
	}

	public void resetoMdelMasterFeature() throws ServiceRequestException
	{
		ClassInfo info = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IFoundation, null);
		if (StringUtils.isNullString(info.getClassification()))
		{

		}
		this.stubService.getEMM().listSubClass(null, info.getGuid(), false, null);
	}

	protected void saveNormalClassficationFeature(String classGuid, List<ClassficationFeature> classficationFeatures) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		SystemDataService sds = DataServer.getSystemDataService();
		List<ClassficationFeature> allFeatureList = this.listAllClassficationFeature();
		Map<String, ClassInfo> superClassMap = this.getSuperClassMap(classGuid);
		Map<String, ClassInfo> subClassMap = this.getAllSubClassMap(classGuid);
		Map<String, ClassficationFeature> saveFeatureMap = new HashMap<>();
		if (classficationFeatures != null)
		{
			for (ClassficationFeature feature : classficationFeatures)
			{
				if (!feature.isMaster())
				{
					saveFeatureMap.put(feature.getClassificationfk(), feature);
				}
			}
		}

		if (allFeatureList != null)
		{
			for (ClassficationFeature feature : allFeatureList)
			{
				if (saveFeatureMap.containsKey(feature.getClassificationfk()))
				{
					if (superClassMap.containsKey(feature.getClassGuid()) || classGuid.equals(feature.getClassGuid()))
					{
						ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(feature.getClassGuid());
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

						throw new ServiceRequestException("ID_APP_CLASS_SUPER_MAPPING_EXIST", "super class has exist ", null, classInfo.getName(),
								StringUtils.getMsrTitle(feature.getTitle(), languageEnum.getType()));
					}
					else if (subClassMap.containsKey(feature.getClassGuid()) || classGuid.equals(feature.getClassGuid()))
					{
						ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(feature.getClassGuid());
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

						throw new ServiceRequestException("ID_APP_CLASS_SUB_MAPPING_EXIST", "sub class has exist ", null, classInfo.getName(),
								StringUtils.getMsrTitle(feature.getTitle(), languageEnum.getType()));
					}
				}
			}
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			for (ClassficationFeature classficationFeature : classficationFeatures)
			{
				if (!classficationFeature.isMaster())
				{
					if (StringUtils.isNullString(classficationFeature.getGuid()))
					{
						classficationFeature.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					}
					classficationFeature.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
					sds.save(classficationFeature);
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteNormalClassficationFeature(String classguid, List<String> classficationFeatureGuids) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			for (String classficationFeatureGuid : classficationFeatureGuids)
			{
				ClassficationFeature classficationFeature = DataServer.getClassificationFeatureService().getClassficationFeature(classficationFeatureGuid);
				if (classficationFeature != null && classficationFeature.isInherit())
				{
					LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
					throw new ServiceRequestException("ID_APP_CLASS_MAPPING_INHERIT_SAVE", "not save inherit mapping", null,
							StringUtils.getMsrTitle(classficationFeature.getTitle(), languageEnum.getType()));
				}

				filter.put(ClassficationFeature.GUID, classficationFeatureGuid);
				sds.deleteFromCache(ClassficationFeature.class, new FieldValueEqualsFilter<ClassficationFeature>(ClassficationFeature.GUID, classficationFeatureGuid));
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteClassificationFeatureItem(String classificationregularguid) throws ServiceRequestException
	{
		DataServer.getClassificationFeatureService().deleteClassificationFeatureItem(classificationregularguid);
	}

	protected String saveClassificationFeatureItem(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		try
		{
			if (StringUtils.isNullString(classficationFeatureItem.getClassificationFeatureGuid()))
			{
				throw new ServiceRequestException("ID_APP_CLASSIFICATION_EXCEPTIONG", "mapping exception");
			}

			checkFatureItem(classficationFeatureItem);
			if (StringUtils.isNullString(classficationFeatureItem.getGuid()))
			{
				classficationFeatureItem.setCreateUser(operatorGuid);
			}
			classficationFeatureItem.setUpdateUser(operatorGuid);
			return DataServer.getClassificationFeatureService().saveClassificationFeatureItem(classficationFeatureItem);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	private void checkFatureItem(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException
	{
		checkOnlyDefineInMaster(classficationFeatureItem);
		List<ClassficationFeatureItemInfo> checkScopeItemList = this.getCheckScope(classficationFeatureItem);
		checkFeatureItemFiedExit(classficationFeatureItem, checkScopeItemList);
		Map<String, ClassficationFeatureItem> itemMap = getFieldItemMap(classficationFeatureItem, checkScopeItemList);
		checkNumberFieldCircular(classficationFeatureItem, itemMap);
	}

	private void checkOnlyDefineInMaster(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException
	{
		// 字段unique 只能在主分类上定义
		// 编号只能在主分类上定义
		if (classficationFeatureItem.isNumbering() || SystemClassFieldEnum.REPEAT.getName().equalsIgnoreCase(classficationFeatureItem.getFieldName())
				|| SystemClassFieldEnum.UNIQUES.getName().equalsIgnoreCase(classficationFeatureItem.getFieldName()))
		{
			ClassficationFeature queryObject = DataServer.getClassificationFeatureService().getClassficationFeature(classficationFeatureItem.getClassificationFeatureGuid());
			if (queryObject != null)
			{
				if (!queryObject.isMaster())
				{
					if (classficationFeatureItem.isNumbering())
					{
						throw new ServiceRequestException("ID_APP_CLASSIFICATION_NUMBERING", "numbering filed can only be defined inmaster classificaiton ");
					}
					else if (SystemClassFieldEnum.REPEAT.getName().equalsIgnoreCase(classficationFeatureItem.getFieldName()))
					{
						throw new ServiceRequestException("ID_APP_CLASSIFICATION_REPEAT", "object repeat field");
					}
				}
			}
		}
	}

	private List<ClassficationFeatureItemInfo> getCheckScope(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException
	{
		List<ClassficationFeatureItemInfo> allItemList = DataServer.getClassificationFeatureService().listAllClassficationFeatureItemInfo();
		// 检查class的所有父级,子级，自身与其他Code的分类映射
		Set<String> otherFeatureSet = this.getCheckOtherFeatureList(classficationFeatureItem);
		// 相同Feature的检查codeitem的所有父级和所有子级,自身
		Set<String> sameFeatureCodeItemSet = this.getCheckCodeItemGuidSet(classficationFeatureItem.getClassificationItemGuid());
		List<ClassficationFeatureItemInfo> checkList = new ArrayList<>();
		for (ClassficationFeatureItemInfo item : allItemList)
		{
			if (item.getClassificationFeatureGuid().equals(classficationFeatureItem.getClassificationFeatureGuid()))
			{
				if (sameFeatureCodeItemSet != null && sameFeatureCodeItemSet.contains(item.getClassificationItemGuid()))
				{
					checkList.add(item);
				}
			}
			else if (otherFeatureSet != null && otherFeatureSet.contains(item.getClassificationFeatureGuid()))
			{
				checkList.add(item);
			}
		}
		return checkList;
	}

	private Set<String> getCheckOtherFeatureList(ClassficationFeatureItem classficationFeatureItem) throws ServiceRequestException
	{
		Set<String> returnSet = new HashSet<String>();
		ClassficationFeature feature = DataServer.getClassificationFeatureService().getClassficationFeature(classficationFeatureItem.getClassificationFeatureGuid());
		if (feature != null)
		{
			Set<String> sameFeatureClassSet = this.getCheckClassMap(feature.getClassGuid()).keySet();
			List<ClassficationFeature> list = this.listAllClassficationFeature();
			if (list != null)
			{
				for (ClassficationFeature temp : list)
				{
					if (sameFeatureClassSet.contains(temp.getClassGuid()))
					{
						if (temp.getGuid().equals(feature.getGuid()) == false)
						{
							returnSet.add(temp.getGuid());
						}
					}
				}
			}
		}
		return returnSet;
	}

	private void checkFeatureItemFiedExit(ClassficationFeatureItem classficationFeatureItem, List<ClassficationFeatureItemInfo> checkItemList) throws ServiceRequestException
	{
		if (checkItemList != null)
		{
			for (ClassficationFeatureItemInfo item : checkItemList)
			{
				if (classficationFeatureItem.getFieldName().equals(item.getFieldName()) && !item.getGuid().equals(classficationFeatureItem.getInfo().getGuid()))
				{
					CodeItemInfo classification = this.stubService.getEMM().getCodeItem(item.getClassificationItemGuid());
					String classificationTitle = "";
					if (classification != null)
					{
						classificationTitle = classification.getTitle(this.stubService.getUserSignature().getLanguageEnum());
						classificationTitle = StringUtils.convertNULLtoString(classificationTitle) + "[" + StringUtils.convertNULLtoString(classification.getCode()) + "]";
					}
					throw new ServiceRequestException("ID_APP_CLASSIFICATION_NUMBERING_FIELDNAME_EXIST", "field has exist", null, classificationTitle);
				}
			}
		}
	}

	private Map<String, ClassficationFeatureItem> getFieldItemMap(ClassficationFeatureItem classficationFeatureItem, List<ClassficationFeatureItemInfo> checkItemList)
			throws ServiceRequestException
	{
		Map<String, ClassficationFeatureItem> returnMap = new HashMap<>();
		if (classficationFeatureItem.getFieldList() != null)
		{
			for (ClassificationNumberField classificationNumberField : classficationFeatureItem.getFieldList())
			{
				if (classificationNumberField.getClassField() != null)
				{
					if (classificationNumberField.isFormClass() && classificationNumberField.getClassField().getType() == FieldTypeEnum.STRING)
					{
						returnMap.put(classificationNumberField.getFieldName(), null);
					}
				}
			}
		}
		if (checkItemList != null)
		{
			for (ClassficationFeatureItemInfo item : checkItemList)
			{
				if (returnMap.containsKey(item.getFieldName()))
				{
					returnMap.put(item.getFieldName(), this.getClassficationFeatureItem(item.getGuid()));
				}
			}
		}
		return returnMap;
	}

	private void checkNumberFieldCircular(ClassficationFeatureItem classficationFeatureItem, Map<String, ClassficationFeatureItem> itemMap) throws ServiceRequestException
	{
		if (classficationFeatureItem.getFieldList() != null)
		{
			for (ClassificationNumberField classificationNumberField : classficationFeatureItem.getFieldList())
			{
				if (classificationNumberField.getClassField() != null)
				{
					if (classificationNumberField.isFormClass() && classificationNumberField.getClassField().getType() == FieldTypeEnum.STRING
							&& itemMap.get(classificationNumberField.getFieldName()) != null)
					{
						if (!SetUtils.isNullList(itemMap.get(classificationNumberField.getFieldName()).getFieldList()))
						{
							for (ClassificationNumberField field : itemMap.get(classificationNumberField.getFieldName()).getFieldList())
							{
								if (classficationFeatureItem.getFieldName().equalsIgnoreCase(field.getFieldName()))
								{
									throw new ServiceRequestException("ID_APP_CLASSIFICATION_CONNECTBY_FIELD", "Circular definition", null,
											classificationNumberField.getFieldName());
								}
							}
						}
					}
				}
			}
		}
	}

	protected ClassficationFeatureItem getClassficationFeatureItem(String itemGuid) throws ServiceRequestException
	{
		return DataServer.getClassificationFeatureService().getClassficationFeatureItem(itemGuid);
	}

	private Set<String> getCheckCodeItemGuidSet(String guid) throws ServiceRequestException
	{
		Set<String> returnSet = new HashSet<String>();
		returnSet.add(guid);
		List<CodeItemInfo> list = this.stubService.getEMM().listAllSubCodeItemInfoByDatail(guid);
		if (list != null)
		{
			for (CodeItemInfo info : list)
			{
				returnSet.add(info.getGuid());
			}
		}
		list = this.stubService.getEMM().listAllSuperCodeItemInfo(guid);
		if (list != null)
		{
			for (CodeItemInfo info : list)
			{
				returnSet.add(info.getGuid());
			}
		}
		return returnSet;
	}

	private Map<String, ClassInfo> getAllSubClassMap(String guid) throws ServiceRequestException
	{
		Map<String, ClassInfo> returnMap = new HashMap<>();
		List<ClassInfo> list = this.stubService.getEMM().listSubClass(null, guid, true, null);
		if (list != null)
		{
			for (ClassInfo info : list)
			{
				returnMap.put(info.getGuid(), info);
			}
		}
		return returnMap;
	}

	private Map<String, ClassInfo> getSuperClassMap(String guid) throws ServiceRequestException
	{
		Map<String, ClassInfo> returnMap = new HashMap<>();
		List<ClassInfo> list = this.stubService.getEMM().listAllSuperClass(null, guid);
		if (list != null)
		{
			for (ClassInfo info : list)
			{
				returnMap.put(info.getGuid(), info);
			}
		}
		return returnMap;
	}

	private Map<String, ClassInfo> getCheckClassMap(String guid) throws ServiceRequestException
	{
		Map<String, ClassInfo> returnMap = new HashMap<>();
		returnMap.put(guid, this.stubService.getEMM().getClassByGuid(guid));
		returnMap.putAll(this.getSuperClassMap(guid));
		returnMap.putAll(this.getAllSubClassMap(guid));
		return returnMap;
	}

	protected void saveClassificationNumberField(String classificationFeatureItemGuid, List<ClassificationNumberField> classificationNumberFieldList) throws ServiceRequestException
	{
		try
		{
			if (StringUtils.isNullString(classificationFeatureItemGuid))
			{
				throw new ServiceRequestException("ID_APP_CLASSIFICATION_EXCEPTIONG", "mapping exception");
			}

			DataServer.getClassificationFeatureService().saveClassificationNumberField(classificationFeatureItemGuid, classificationNumberFieldList);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

}
