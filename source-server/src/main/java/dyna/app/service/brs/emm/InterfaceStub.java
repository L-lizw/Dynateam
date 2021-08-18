package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class InterfaceStub extends AbstractServiceStub<EMMImpl>
{

	protected InterfaceObject getInterfaceObjectByName(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getInterfaceModelService().getInterface(interfaceEnum);
	}

	protected List<InterfaceObject> listInterfaceObject() throws ServiceRequestException
	{
		List<InterfaceObject> resultList = new ArrayList<InterfaceObject>();
		Map<String, InterfaceObject> allInterfaceMap = this.stubService.getDomainServiceStub().getInterfaceModelService().getInterfaceMap();
		if (!SetUtils.isNullMap(allInterfaceMap))
		{
			resultList.addAll(allInterfaceMap.values());
		}
		return resultList;
	}

	protected List<ClassField> listClassFieldOfInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getInterfaceModelService().listClassFieldOfInterface(interfaceEnum);
	}

	protected List<ClassField> listClassFieldByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getInterfaceModelService().listClassFieldOfInterface(interfaceEnum);
	}

}
