package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.ClassificationUIInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ClassificationStub extends AbstractServiceStub<EMMImpl>
{
	public static final String	FOUNDATION_GUID		= "FOUNDATIONGUID";
	public static final String	CLASSIFICATION_FK	= "CFITEMFK";

	protected List<UIField> listUIFieldByUIGuid(String uiGuid) throws ServiceRequestException
	{
		List<UIField> fieldList = this.stubService.getDomainServiceStub().getCodeModelService().listUIField(uiGuid);
		return fieldList;
	}

	protected ClassificationUIInfo getCFUIObject(String classificationGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		List<ClassificationUIInfo> uiList = this.stubService.getDomainServiceStub().getCodeModelService().listAllUIObject(classificationGuid);
		if (!SetUtils.isNullList(uiList))
		{
			for (ClassificationUIInfo uiObject : uiList)
			{
				if (uiObject.isVisible())
				{
					if (uiType.toString().equals(uiObject.getType()))
					{
						return uiObject;
					}
				}
			}
		}
		return null;
	}

	protected List<UIField> listCFUIField(String classificationGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		ClassificationUIInfo cfuiObject = this.getCFUIObject(classificationGuid, uiType);
		if (cfuiObject != null)
		{
			return this.listUIFieldByUIGuid(cfuiObject.getGuid());
		}

		return null;
	}

	protected List<ClassificationUIInfo> listCFUIObjectInfo(String codeItemguid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getCodeModelService().listAllUIObject(codeItemguid);
	}

	public List<ClassField> listClassificationField(String classificationItemGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getCodeModelService().listField(classificationItemGuid);
	}

	public ClassField getClassificationField(String classificationItemGuid, String fieldName) throws ServiceRequestException
	{
		List<ClassField> listClassificationField = this.listClassificationField(classificationItemGuid);
		if (!SetUtils.isNullList(listClassificationField))
		{
			for (ClassField field : listClassificationField)
			{
				if (field.getName().equalsIgnoreCase(fieldName))
				{
					return field;
				}
			}
		}
		return null;
	}

	protected List<ClassficationFeature> listClassficationFeature(String classguid) throws ServiceRequestException
	{
		List<ClassficationFeature> featureList = this.stubService.getDomainServiceStub().getClassificationFeatureService().listClassficationFeatureBySubClass(classguid);
		if (!SetUtils.isNullList(featureList))
		{
			featureList.forEach(feature -> {
				feature.setInherit(!classguid.equals(feature.getClassGuid()));
			});
		}
		return featureList;
	}

	protected List<ClassficationFeature> listClassficationFeatureByClassification(String classificGuid) throws ServiceRequestException
	{
		SystemDataService sds = this.stubService.getDomainServiceStub().getSystemDataService();
		List<ClassficationFeature> featureList = sds.listFromCache(ClassficationFeature.class, new FieldValueEqualsFilter<ClassficationFeature>("CLASSIFICATIONFK", classificGuid));
		return featureList;
	}

	/**
	 * 取得对象对应的uiobject 包含父级
	 * 
	 * @param objectGuid
	 * @param uiType
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<UIObjectInfo> listCFUIObjectByObjecGuid(ObjectGuid objectGuid, UITypeEnum uiType) throws ServiceRequestException
	{
		boolean hasLicence = this.stubService.getLIC().hasLicence(ApplicationTypeEnum.CLS.name());
		if (!hasLicence)
		{
			return null;
		}

		List<UIObjectInfo> uiObjectList = new ArrayList<>();
		List<ClassficationFeature> listClassficationFeature = this.listClassficationFeature(objectGuid.getClassGuid());
		if (!SetUtils.isNullList(listClassficationFeature))
		{
			UIObjectInfo uiObject;
			for (ClassficationFeature feature : listClassficationFeature)
			{
				// 主分类没有属性，不需要显示页签
				CodeObjectInfo codeInfo = this.stubService.getCode(feature.getClassificationfk());
				if (codeInfo != null && codeInfo.isHasFields())
				{
					uiObject = new UIObjectInfo();
					uiObject.setFromClassification(true);
					uiObject.setName(feature.getClassificationName());
					uiObject.setTitle(feature.getTitle());
					uiObject.setMaster(feature.isMaster());
					uiObjectList.add(uiObject);
				}
			}
			uiObjectList.sort(Comparator.comparing(UIObjectInfo::getName));
		}
		return uiObjectList;
	}

	protected ClassficationFeatureItem getClassficationFeatureItem(String itemGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getClassificationFeatureService().getClassficationFeatureItem(itemGuid);
	}

	public List<ClassificationNumberField> listClassificationNumberField(String classificationnumberregular) throws ServiceRequestException
	{
		ClassficationFeatureItem classficationFeatureItem = this.stubService.getDomainServiceStub().getClassificationFeatureService()
				.getClassficationFeatureItem(classificationnumberregular);
		if (classficationFeatureItem == null)
		{
			return null;
		}
		else
		{
			return classficationFeatureItem.getFieldList();
		}
	}

	protected List<ClassficationFeatureItemInfo> listClassficationFeatureItem(String classiFeatrueguid, String classificationItemGuid) throws ServiceRequestException
	{
		return listAllSuperClassificationFeatureItem(classiFeatrueguid, classificationItemGuid);
	}

	private List<ClassficationFeatureItemInfo> listAllSuperClassificationFeatureItem(String classiFeatrueguid, String classificationItemGuid) throws ServiceRequestException
	{
		Set<String> returnSet = this.stubService.getCodeStub().getAllSuperCodeItemGuidSet(classificationItemGuid);
		List<ClassficationFeatureItemInfo> allItemList = DataServer.getClassificationFeatureService().listAllClassficationFeatureItemInfo();
		List<ClassficationFeatureItemInfo> checkList = new ArrayList<>();
		for (ClassficationFeatureItemInfo item : allItemList)
		{
			if (item.getClassificationFeatureGuid().equals(classiFeatrueguid))
			{
				if (returnSet != null && returnSet.contains(item.getClassificationItemGuid()))
				{
					checkList.add(item);
					if (!item.getClassificationItemGuid().equals(classificationItemGuid))
					{
						item.setInherit(true);
					}
				}
			}
		}
		return checkList;
	}

	public List<ClassficationFeatureItem> listAllSuperClassficationFeatureItem(String classGuid, List<String> classificationItemGuidList) throws ServiceRequestException
	{
		List<ClassficationFeatureItem> finalList = new ArrayList<>();
		if (!SetUtils.isNullList(classificationItemGuidList))
		{
			Set<String> codeItemSuperMap = new HashSet<>();
			for (String codeItemGuid : classificationItemGuidList)
			{
				codeItemSuperMap.addAll(this.stubService.getCodeStub().getAllSuperCodeItemGuidSet(codeItemGuid));
			}
			Set<String> allSuperClassSet = this.stubService.getClassStub().getAllSuperClassGuidSet(classGuid);
			List<ClassficationFeatureItemInfo> allItemList = DataServer.getClassificationFeatureService().listAllClassficationFeatureItemInfo();
			for (ClassficationFeatureItemInfo item : allItemList)
			{
				ClassficationFeature feature = this.stubService.getDomainServiceStub().getClassificationFeatureService()
						.getClassficationFeature(item.getClassificationFeatureGuid());

				if (feature != null)
				{
					String codeName = this.stubService.getCode(feature.getClassificationfk()).getName();
					if (allSuperClassSet.contains(feature.getClassGuid()))
					{
						if (codeItemSuperMap.contains(item.getClassificationItemGuid()))
						{
							ClassficationFeatureItem classficationFeatureItem = this.getClassficationFeatureItem(item.getGuid());
							classficationFeatureItem.getInfo().put("CLASSIFICATIONGROUPNAME", codeName);
							finalList.add(classficationFeatureItem);
							if (!classificationItemGuidList.contains(item.getClassificationItemGuid()))
							{
								classficationFeatureItem.setInherit(true);
							}
						}
					}
				}
			}
			if (!SetUtils.isNullList(finalList))
			{
				finalList.stream().sorted(Comparator.comparing(ClassficationFeatureItem::getName));
			}
		}
		return finalList;
	}

}
