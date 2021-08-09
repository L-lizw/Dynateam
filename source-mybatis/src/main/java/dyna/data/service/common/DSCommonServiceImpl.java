package dyna.data.service.common;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.Queue;
import dyna.common.dto.Session;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ObjectFieldTypeEnum;
import dyna.common.systemenum.PMSearchTypeEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

import java.util.ArrayList;
import java.util.List;

public class DSCommonServiceImpl extends DataRuleService implements DSCommonService
{
	private DSCommonStub commonStub = null;

	public DSCommonServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public DSCommonStub getDSCommonStub()
	{
		return this.commonStub;
	}

	@Override
	public String getTableName(String classNameOrGuid) throws ServiceRequestException
	{
		return this.getDSCommonStub().getTableName(classNameOrGuid);
	}

	@Override
	public String getTableName(String classGuidOrName, String fieldName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getTableName(classGuidOrName, fieldName);
	}

	@Override
	public String getRealBaseTableName(String classGuidOrName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getRealBaseTableName(classGuidOrName);
	}

	@Override
	public String getTableIndex(String classGuidOrName, String fieldName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getTableIndex(classGuidOrName, fieldName);
	}

	@Override
	public String getFieldColumn(String mainTableAlias, String fieldName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getFieldColumn(mainTableAlias, fieldName);
	}

	@Override
	public Session getSession(String guid) throws ServiceRequestException
	{
		return this.getDSCommonStub().getSession(guid);
	}

	@Override
	public Session getSystemInternal() throws ServiceRequestException
	{
		return this.getDSCommonStub().getSystemInternal();
	}

	@Override
	public String updateSession(String guid) throws ServiceRequestException
	{
		return this.getDSCommonStub().updateSession(guid);
	}

	@Override
	public void updateSessionActiveTime(String guid) throws ServiceRequestException
	{
		this.getDSCommonStub().updateSession(guid);
	}

	@Override
	public ObjectFieldTypeEnum getObjectFieldTypeOfField(ClassField field, String bizModelName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getObjectFieldTypeOfField(field, bizModelName);
	}

	@Override
	public ObjectFieldTypeEnum getObjectFieldTypeOfField(String className, String fieldName, String bizModelName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getObjectFieldTypeOfField(className, fieldName, bizModelName);
	}

	@Override
	public List<String> getAllFieldsFromSC(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getDSCommonStub().getAllFieldsFromSC(searchCondition);
	}

	@Override
	public List<FoundationObject> executeQuery(DynamicSelectParamData paramData)
	{
		return this.getDSCommonStub().executeQuery(paramData);
	}

	@Override
	public String getMasterTableName(String classGuidOrName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getMasterTableName(classGuidOrName);
	}

	@Override
	public List<String> getAllSubClass(String className) throws ServiceRequestException
	{
		List<String> classNameList = new ArrayList<>();
		this.getDSCommonStub().getAllSubClass(className, classNameList);

		return classNameList;
	}

	@Override
	public int getRecordCount(DynamicSelectParamData paramData)
	{
		return this.getDSCommonStub().getRecordCount(paramData);
	}

	@Override
	public void decorateClass(SearchCondition searchCondition) throws ServiceRequestException
	{
		this.getDSCommonStub().decorateClass(searchCondition);
	}

	@Override
	public FieldTypeEnum getFieldTypeByName(String codeName) throws ServiceRequestException
	{
		return this.getDSCommonStub().getFieldTypeByName(codeName);
	}

	@Override
	public String saveQueue(Queue queue) throws ServiceRequestException
	{
		return this.getDSCommonStub().saveQueue(queue);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.data.orm.DataService#isSearchNameUnique(java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public boolean isSearchNameUnique(String name, boolean isPublic, PMSearchTypeEnum pmTypeEnum, String userGuid) throws ServiceRequestException
	{
		return this.getDSCommonStub().isSearchNameUnique(name, isPublic, pmTypeEnum, userGuid);
	}

	@Override
	public void deleteMail(String userGuid, int messageDay, boolean isWorkflow) throws ServiceRequestException
	{
		this.getDSCommonStub().deleteMail(userGuid, messageDay, isWorkflow);
	}

	@Override
	public String getDataServerConfRootPath()
	{
		return this.getDSCommonStub().getDataServerConfRootPath();
	}
}
