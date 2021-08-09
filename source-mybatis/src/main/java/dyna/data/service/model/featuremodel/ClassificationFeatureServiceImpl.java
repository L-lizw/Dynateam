package dyna.data.service.model.featuremodel;

import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

import java.util.List;

public class ClassificationFeatureServiceImpl extends DataRuleService implements ClassificationFeatureService
{
	private ClassificationFeatureStub featureStub;

	public ClassificationFeatureServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public ClassificationFeatureStub getFeatureStub()
	{
		return this.featureStub;
	}

	@Override
	public void checkAndClearClassificationFeature(String userGuid) throws ServiceRequestException
	{
		this.getFeatureStub().checkAndClearClassificationFeature(userGuid);
	}

	@Override
	public ClassficationFeature getClassficationFeature(String featureGuid)
	{
		return this.getFeatureStub().getClassficationFeature(featureGuid);
	}

	@Override
	public List<ClassficationFeature> listAllClassficationFeature()
	{
		return this.getFeatureStub().listAllClassficationFeature();
	}

	@Override
	public List<ClassficationFeature> listClassficationFeatureBySubClass(String classguid)
	{
		return this.getFeatureStub().listClassficationFeatureBySubClass(classguid);
	}

	@Override
	public List<ClassficationFeature> listAllSubClassficationFeature(String classGuid)
	{
		return this.getFeatureStub().listAllSubClassficationFeature(classGuid);
	}

	@Override
	public boolean isMasterClassification(String classGuid, String classificationItemGuid) throws ServiceRequestException
	{
		return this.getFeatureStub().isMasterClassification(classGuid, classificationItemGuid);
	}

	@Override
	public List<ClassficationFeatureItemInfo> listAllSuperClassificationFeatureItem(String featureGuid, String classificationItemGuid) throws ServiceRequestException
	{
		return this.getFeatureStub().listAllSuperClassificationFeatureItem(featureGuid, classificationItemGuid);
	}

	@Override
	public List<ClassficationFeatureItemInfo> listAllSubClassificationFeatureItem(String featureGuid, String classificationItemGuid) throws ServiceRequestException
	{
		return this.getFeatureStub().listAllSubClassificationFeatureItem(featureGuid, classificationItemGuid);
	}

	@Override
	public ClassficationFeatureItem getClassficationFeatureItem(String featureItemGuid) throws ServiceRequestException
	{
		return this.getFeatureStub().getClassificationFeatureItem(featureItemGuid);
	}

	@Override
	public String saveClassificationFeatureItem(ClassficationFeatureItem classificationFeatureItem) throws DynaDataException, ServiceRequestException
	{
		return this.getFeatureStub().saveClassificationFeatureItem(classificationFeatureItem);
	}

	@Override
	public void saveClassificationNumberField(String classificationFeatureItemGuid, List<ClassificationNumberField> classificationNumberFieldList)
			throws DynaDataException, ServiceRequestException
	{
		this.getFeatureStub().saveClassificationNumberField(classificationFeatureItemGuid, classificationNumberFieldList);
	}

	@Override
	public void deleteClassificationFeatureItem(String featureItemGuid) throws DynaDataException, ServiceRequestException
	{
		this.getFeatureStub().delClassificationFeatureItem(featureItemGuid);
	}

	@Override
	public List<ClassficationFeatureItemInfo> listAllClassficationFeatureItemInfo()
	{
		return this.getFeatureStub().listAllClassficationFeatureItem();
	}
}
