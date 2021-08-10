package dyna.data.service.tool;

import dyna.common.bean.data.FoundationObject;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.DataRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DSToolServiceImpl extends DataRuleService implements DSToolService
{
	@Autowired
	private DSToolStub toolStub;

	protected DSToolStub getDSToolStub()
	{
		return this.toolStub;
	}

	@Override
	public void clearErrDataInMast(String className) throws ServiceRequestException
	{
		this.getDSToolStub().clearErrDataInMast(className);
	}

	@Override
	public void changeFoundationStatus(Map<String, Object> parameterMap) throws ServiceRequestException
	{
		this.getDSToolStub().changeFoundationStatus(parameterMap);
	}

	@Override
	public void refreshMergeFieldValue(FoundationObject foundationObject, String fieldName) throws ServiceRequestException
	{
		this.getDSToolStub().refreshMergeFieldValue(foundationObject, fieldName);
	}
}
