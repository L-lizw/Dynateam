package dyna.app.service.brs.emm;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.data.DataServer;
import dyna.data.service.model.businessmodel.BusinessModelService;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.data.service.model.codemodel.CodeModelService;
import dyna.data.service.model.featuremodel.ClassificationFeatureService;
import dyna.data.service.model.interfacemodel.InterfaceModelService;
import dyna.data.service.model.lifecyclemodel.LifecycleModelService;
import dyna.data.service.relation.RelationService;
import dyna.data.service.sdm.SystemDataService;

public class DomainServiceStub extends AbstractServiceStub<EMMImpl>
{

	protected DomainServiceStub(ServiceContext context, EMMImpl service)
	{
		super(context, service);
	}

	public ClassModelService getClassModelService()
	{
		return DataServer.getClassModelService();
	}

	public BusinessModelService getBusinessModelService()
	{
		return DataServer.getBusinessModelService();
	}

	public CodeModelService getCodeModelService()
	{
		return DataServer.getCodeModelService();
	}

	public ClassificationFeatureService getClassificationFeatureService()
	{
		return DataServer.getClassificationFeatureService();
	}

	public InterfaceModelService getInterfaceModelService()
	{
		return DataServer.getInterfaceModelService();
	}

	public LifecycleModelService getLifecycleModelService()
	{
		return DataServer.getLifecycleModelService();
	}

	public RelationService getReleationService()
	{
		return DataServer.getRelationService();
	}

	public SystemDataService getSystemDataService()
	{
		return DataServer.getSystemDataService();
	}

}
