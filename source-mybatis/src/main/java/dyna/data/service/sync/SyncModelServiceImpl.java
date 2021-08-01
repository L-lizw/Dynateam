package dyna.data.service.sync;

import dyna.common.bean.configure.ProjectModel;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;
import dyna.dbcommon.function.DatabaseFunctionFactory;

import java.util.List;
import java.util.Map;

public class SyncModelServiceImpl extends DataRuleService implements SyncModelService
{
	private SDSModelStub		modelStub;

	private CodeXMLLoadStub		codeXMLLoadStub;

	private ClassXMLLoadStub	classXMLLoadStub;

	private static boolean		initialized	= false;

	public SyncModelServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public SDSModelStub getModelStub()
	{
		if (this.modelStub == null)
		{
			this.modelStub = new SDSModelStub(this.serviceContext, this);
		}
		return modelStub;
	}

	public CodeXMLLoadStub getClassificationModelLoadStub()
	{
		if (this.codeXMLLoadStub == null)
		{
			this.codeXMLLoadStub = new CodeXMLLoadStub(this.serviceContext, this);
		}
		return codeXMLLoadStub;
	}

	public ClassXMLLoadStub getClassModelLoadStub()
	{
		if (this.classXMLLoadStub == null)
		{
			this.classXMLLoadStub = new ClassXMLLoadStub(this.serviceContext, this);
		}
		return classXMLLoadStub;
	}

	@Override
	protected void init()
	{
		if (initialized)
		{
			return;
		}

		// this.getModelStub().deleteModelerSession();

		initialized = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#isModelSync(dyna.common.bean.configure.ProjectModel)
	 */
	@Override
	public boolean isModelSync(ProjectModel projectModel) throws DynaDataException
	{
		return this.getModelStub().isModelSync(projectModel);
	}

	@Override
	public String getCurrentLoginUser()
	{
		return this.getModelStub().getCurrentLoginUser();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#getCurrentSyncModel()
	 */
	@Override
	public ProjectModel getCurrentSyncModel() throws DynaDataException
	{
		return this.getModelStub().getSyncInfo();
	}

	/**
	 * 从数据库中生成模型文件
	 */
	@Override
	public ProjectModel makeModelFile(boolean hasClassificationLicense)
	{
		this.getModelStub().makeModelFile(hasClassificationLicense);
		return this.getModelStub().getSyncInfo();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.SystemDataService#isDeployLock()
	 */
	@Override
	public boolean isDeployLock(String sessionId) throws DynaDataException
	{
		return this.getModelStub().isDeployLock(sessionId);
	}

	@Override
	public ProjectModel deploy(String sessionId, ProjectModel projectModel, boolean hasClassificationLicense) throws Exception
	{
		return this.getModelStub().deploy(sessionId, projectModel, hasClassificationLicense);
	}

	@Override
	public void deployClassificationField(List<Map<String, String>> dataSource,String sessionId) throws ServiceRequestException
	{
		this.getModelStub().deployClassificationField(dataSource,sessionId);
		
	}

	@Override
	public String getColumnDBType(FieldTypeEnum fieldTypeEnum, String fieldSize)
	{
		return DatabaseFunctionFactory.getColumnTypeFunction().getColumnType(fieldTypeEnum, fieldSize);
	}
}
