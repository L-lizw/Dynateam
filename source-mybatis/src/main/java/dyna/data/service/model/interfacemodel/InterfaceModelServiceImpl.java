package dyna.data.service.model.interfacemodel;

import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;
import dyna.data.service.sync.bean.TableIndexModel;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class InterfaceModelServiceImpl extends DataRuleService implements InterfaceModelService
{
	private InterfaceModelServiceStub modelStub;

	public InterfaceModelServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public InterfaceModelServiceStub getModelStub()
	{
		return this.modelStub;
	}

	@Override
	protected void init()
	{
		this.getModelStub().loadModel();
	}

	@Override
	public void reloadModel() throws ServiceRequestException
	{
		this.getModelStub().loadModel();
	}

	@Override
	public Map<String, InterfaceObject> getInterfaceMap()
	{
		return this.getModelStub().getInterfaceObjectMap();
	}

	@Override
	public InterfaceObject getInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getModelStub().getInterface(interfaceEnum);
	}

	@Override
	public List<InterfaceObject> listSubInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getModelStub().listSubInterface(interfaceEnum);
	}

	@Override
	public List<ClassField> listClassFieldOfInterface(ModelInterfaceEnum interfaceEnum)
	{
		return this.getModelStub().listClassFieldOfInterface(interfaceEnum.name());
	}

	@Override
	public Map<String, List<ClassField>> getInterfaceFieldMap()
	{
		return this.getModelStub().getInterfaceFieldMap();
	}

	@Override
	public Map<String, List<TableIndexModel>> getInterfaceIndexMap()
	{
		return this.getModelStub().getInterfaceIndexMap();
	}
}
