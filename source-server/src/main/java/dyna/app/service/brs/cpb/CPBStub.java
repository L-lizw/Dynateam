package dyna.app.service.brs.cpb;

import dyna.app.core.sch.Scheduler;
import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.core.track.impl.TRViewLinkImpl;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.helper.CheckConnectUtil;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.configparamter.*;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ConfigParameterTableType;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.JsonUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.service.ins.InstanceService;
import dyna.data.service.relation.RelationService;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CPBStub extends AbstractServiceStub<CPBImpl>
{
	private static TrackerBuilder trackerBuilder = null;


	@SuppressWarnings("unchecked")
	protected List<TableOfGroup> saveTableOfGroup(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfGroup> groupList, List<DynamicColumnTitle> columnTitleList,
			List<TableOfGroup> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 整列删除时，不做单元格保存处理
			List<String> exceptColumnTitleList = new ArrayList<String>();
			if (!SetUtils.isNullList(deleteColumnTitleList))
			{
				for (DynamicColumnTitle title : deleteColumnTitleList)
				{
					exceptColumnTitleList.add(title.getTitle());
				}
			}

			this.saveTableData(end1ObjectGuid, columnTitleList, null, ConfigParameterTableType.G);

			List<TableOfGroup> finalList = this.saveTableData(end1ObjectGuid, groupList, exceptColumnTitleList, ConfigParameterTableType.G);

			this.deleteColumnTitle(deleteColumnTitleList, true);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());

			this.isDuplicateSN(end1ObjectGuid.getMasterGuid(), ruleTime, ConfigParameterTableType.G);

//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(ConfigParameterTableType.G, e);
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

	@SuppressWarnings("unchecked")
	protected List<TableOfList> saveTableOfList(ObjectGuid end1ObjectGuid, Date ruleTime, ConfigParameterTableType tableType, List<TableOfList> tableOfListList,
			List<DynamicColumnTitle> columnTitleList, List<TableOfList> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 整列删除时，不做单元格保存处理
			List<String> exceptColumnTitleList = new ArrayList<String>();
			if (!SetUtils.isNullList(deleteColumnTitleList))
			{
				for (DynamicColumnTitle title : deleteColumnTitleList)
				{
					exceptColumnTitleList.add(title.getTitle());
				}
			}

			this.saveTableData(end1ObjectGuid, columnTitleList, null, tableType);

			List<TableOfList> finalList = this.saveTableData(end1ObjectGuid, tableOfListList, exceptColumnTitleList, tableType);

			this.deleteColumnTitle(deleteColumnTitleList, true);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());

			// 检查数据是否正确
			this.checkLTableData(end1ObjectGuid, ruleTime, tableType);

//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(tableType, e);
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

	@SuppressWarnings("unchecked")
	protected List<TableOfExpression> saveTableOfExpression(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfExpression> tableOfExpressionList,
			List<TableOfExpression> deleteLineList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			List<TableOfExpression> finalList = this.saveTableData(end1ObjectGuid, tableOfExpressionList, null, ConfigParameterTableType.F);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());
//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(ConfigParameterTableType.F, e);
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

	@SuppressWarnings("unchecked")
	protected List<TableOfInputVariable> saveTableOfInputVariable(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfInputVariable> tableOfInputVariableList,
			List<TableOfInputVariable> deleteLineList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			List<TableOfInputVariable> finalList = this.saveTableData(end1ObjectGuid, tableOfInputVariableList, null, ConfigParameterTableType.INPT);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());

			this.isDuplicateSN(end1ObjectGuid.getMasterGuid(), ruleTime, ConfigParameterTableType.INPT);

//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(ConfigParameterTableType.INPT, e);
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

	@SuppressWarnings("unchecked")
	protected List<TableOfMark> saveTableOfMark(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfMark> tableOfMarkList, List<TableOfMark> deleteLineList)
			throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			List<TableOfMark> finalList = this.saveTableData(end1ObjectGuid, tableOfMarkList, null, ConfigParameterTableType.MAK);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());

//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(ConfigParameterTableType.MAK, e);
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

	@SuppressWarnings("unchecked")
	protected List<TableOfParameter> saveTableOfParameter(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfParameter> tableOfParameterList,
			List<DynamicColumnTitle> columnTitleList, List<TableOfParameter> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 整列删除时，不做单元格保存处理
			List<String> exceptColumnTitleList = new ArrayList<String>();
			if (!SetUtils.isNullList(deleteColumnTitleList))
			{
				for (DynamicColumnTitle title : deleteColumnTitleList)
				{
					exceptColumnTitleList.add(title.getTitle());
				}
			}

			this.saveTableData(end1ObjectGuid, columnTitleList, null, ConfigParameterTableType.P);

			List<TableOfParameter> finalList = this.saveTableData(end1ObjectGuid, tableOfParameterList, exceptColumnTitleList, ConfigParameterTableType.P);

			this.deleteColumnTitle(deleteColumnTitleList, true);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());

//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(ConfigParameterTableType.P, e);
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

	@SuppressWarnings("unchecked")
	protected List<TableOfRegion> saveTableOfRegion(ObjectGuid end1ObjectGuid, Date ruleTime, ConfigParameterTableType tableType, List<TableOfRegion> tableOfRegionList,
			List<DynamicColumnTitle> columnTitleList, List<TableOfRegion> deleteLineList, List<DynamicColumnTitle> deleteColumnTitleList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			// 整列删除时，不做单元格保存处理
			List<String> exceptColumnTitleList = new ArrayList<String>();
			if (!SetUtils.isNullList(deleteColumnTitleList))
			{
				for (DynamicColumnTitle title : deleteColumnTitleList)
				{
					exceptColumnTitleList.add(title.getTitle());
				}
			}

			this.saveTableData(end1ObjectGuid, columnTitleList, null, tableType);

			List<TableOfRegion> finalList = this.saveTableData(end1ObjectGuid, tableOfRegionList, exceptColumnTitleList, tableType);

			this.deleteColumnTitle(deleteColumnTitleList, true);

			this.deleteLineData(deleteLineList, end1ObjectGuid.getMasterGuid());

//			DataServer.getTransactionManager().commitTransaction();

			return finalList;
		}
		catch (DynaDataException e)
		{
			this.rebuildExceptionMsg(tableType, e);
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

	// 配置数据初始化
	protected ConfigVariable buildConfigVariable(FoundationObject instance, Date ruleTime) throws ServiceRequestException
	{
		ConfigVariable configVariable = new ConfigVariable();
		this.initConfigVariable(instance, ruleTime, configVariable);
		return configVariable;
	}

	// 配置数据初始化
	protected ConfigCalculateVar buildConfigCalculateVar(FoundationObject instance, Date ruleTime) throws ServiceRequestException
	{
		ConfigCalculateVar configVariable = new ConfigCalculateVar();
		this.initConfigVariable(instance, ruleTime, configVariable);
		return configVariable;
	}

	protected void initConfigVariable(FoundationObject instance, Date ruleTime, ConfigVariable configVariable) throws ServiceRequestException
	{
		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		ObjectGuid objectGuid = instance.getObjectGuid();

		List<DynamicColumnTitle> titles = this.stubService.listColumnTitles(objectGuid, null, ruleTime);

		List<TableOfList> tableOfListDataList = this.stubService.listTableOfListData(objectGuid, null, ruleTime);
		List<TableOfGroup> gDataList = this.stubService.listTableOfGroup(objectGuid, ruleTime);
		List<TableOfExpression> fDataList = this.stubService.listTableOfExpression(objectGuid, ruleTime);
		List<TableOfInputVariable> inptDataList = this.stubService.listTableOfInputVariable(objectGuid, ruleTime);
		List<TableOfParameter> pDataList = this.stubService.listTableOfParameter(objectGuid, null, ruleTime);
		List<TableOfRegion> aerqDataList = this.stubService.listTableOfRegion(objectGuid, null, ruleTime);
		List<TableOfMark> markList = this.stubService.listTableOfMarkData(objectGuid, ruleTime);
		List<TableOfMultiCondition> multiRegionList = this.stubService.listTableOfMultiVariable(objectGuid, ruleTime);

		configVariable.setGroupList(gDataList);
		configVariable.setListOfList(tableOfListDataList);
		configVariable.setExpressionList(fDataList);
		configVariable.setInptVarList(inptDataList);
		configVariable.setMarkList(markList);
		configVariable.setRegionList(aerqDataList);
		configVariable.setParameterList(pDataList);
		configVariable.setTitleList(titles);
		configVariable.setMultiRegionList(multiRegionList);
		configVariable.prepare();
	}

	protected ConfigVariable getInputVars(FoundationObject instance, Date ruleTime) throws ServiceRequestException
	{
		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		ObjectGuid objectGuid = instance.getObjectGuid();

		List<DynamicColumnTitle> titles = this.stubService.listColumnTitles(objectGuid, null, ruleTime);
		List<TableOfInputVariable> inptDataList = this.stubService.listTableOfInputVariable(objectGuid, ruleTime);
		List<TableOfParameter> pDataList = this.stubService.listTableOfParameter(objectGuid, null, ruleTime);

		ConfigVariable configVariable = new ConfigVariable();
		configVariable.setInptVarList(inptDataList);
		configVariable.setParameterList(pDataList);
		configVariable.setTitleList(titles);
		configVariable.prepare();

		return configVariable;
	}

	protected ConfigVariable buildOtherConfigVariable(FoundationObject instance, Date ruleTime, ConfigVariable inputConfigVariable) throws ServiceRequestException
	{
		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		ObjectGuid objectGuid = instance.getObjectGuid();

		List<TableOfList> tableOfListDataList = this.stubService.listTableOfListData(objectGuid, null, ruleTime);
		List<TableOfGroup> gDataList = this.stubService.listTableOfGroup(objectGuid, ruleTime);
		List<TableOfExpression> fDataList = this.stubService.listTableOfExpression(objectGuid, ruleTime);
		List<TableOfRegion> aerqDataList = this.stubService.listTableOfRegion(objectGuid, null, ruleTime);
		List<TableOfMark> markList = this.stubService.listTableOfMarkData(objectGuid, ruleTime);

		ConfigVariable configVariable = new ConfigVariable();
		configVariable.setGroupList(gDataList);
		configVariable.setListOfList(tableOfListDataList);
		configVariable.setExpressionList(fDataList);
		configVariable.setInptVarList(inputConfigVariable.getInptVarList());
		configVariable.setMarkList(markList);
		configVariable.setRegionList(aerqDataList);
		configVariable.setParameterList(inputConfigVariable.getParameterList());
		configVariable.setTitleList(inputConfigVariable.getTitleList());
		configVariable.prepare();

		return configVariable;
	}

	/**
	 * 保存表数据
	 * 
	 * @param masterGuid
	 * @param dataList
	 * @param exceptColumnTitleList
	 * @param tableType
	 * @return
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings({ "rawtypes" })
	private List saveTableData(ObjectGuid end1ObjectGuid, List dataList, List<String> exceptColumnTitleList, ConfigParameterTableType tableType) throws ServiceRequestException
	{
		return this.saveTableData(end1ObjectGuid, null, dataList, exceptColumnTitleList, tableType);
	}

	/**
	 * 保存表数据
	 * 
	 * @param masterGuid
	 * @param end1ReleaseTime
	 * @param dataList
	 * @param exceptColumnTitleList
	 * @param tableType
	 * @return
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List saveTableData(ObjectGuid end1ObjectGuid, Date ruleTime, List dataList, List<String> exceptColumnTitleList, ConfigParameterTableType tableType)
			throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}

		List resultList = new ArrayList();
		if (!SetUtils.isNullList(dataList))
		{
			List<Map<String, Object>> insertList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> updateNextRevisionList = new ArrayList<Map<String, Object>>();

			Map<String, String> titleMap = null;
			List<String> fieldNameList = null;
			for (Object obj : dataList)
			{
				if (obj instanceof DynamicColumnTitle)
				{
					DynamicColumnTitle title = (DynamicColumnTitle) obj;
					DynamicColumnTitle copy = new DynamicColumnTitle();
					if (title.getGuid() == null)
					{
						title.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						title.setTableType(tableType);
						title.setMasterGuid(end1ObjectGuid.getMasterGuid());
						insertList.add(title);

						copy.sync(title);
					}
					else
					{
						copy.sync(title);
						// if (title.isChanged())
						{
							if (title.getReleaseTime() == null)
							{
								updateList.add(title);
							}
							else
							{
								DynamicColumnTitle newTitle = (DynamicColumnTitle) title.clone();
								newTitle.clear(DynamicColumnTitle.GUID);
								newTitle.clear(DynamicColumnTitle.RELEASETIME);
								newTitle.clear(DynamicColumnTitle.HASNEXTREVISION);
								newTitle.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(newTitle);

								copy.sync(newTitle);

								title.setHasNextRevision(true);
								updateNextRevisionList.add(title);
							}
						}
					}
					resultList.add(copy);
				}
				else if (ConfigParameterTableType.F == tableType)
				{
					TableOfExpression expression = (TableOfExpression) obj;
					TableOfExpression copy = new TableOfExpression();
					if (expression.getGuid() == null)
					{
						expression.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						expression.setMasterGuid(end1ObjectGuid.getMasterGuid());
						insertList.add(expression);

						copy.sync(expression);
					}
					else
					{
						copy.sync(expression);
						// if (expression.isChanged())
						{
							if (expression.getReleaseTime() == null)
							{
								updateList.add(expression);
							}
							else
							{
								TableOfExpression newExpression = (TableOfExpression) expression.clone();
								newExpression.clear(TableOfExpression.GUID);
								newExpression.clear(TableOfExpression.RELEASETIME);
								newExpression.clear(TableOfExpression.HASNEXTREVISION);
								newExpression.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(newExpression);

								copy.sync(newExpression);

								expression.setHasNextRevision(true);
								updateNextRevisionList.add(expression);
							}
						}
					}
					resultList.add(copy);
				}
				else if (ConfigParameterTableType.INPT == tableType)
				{
					TableOfInputVariable inpt = (TableOfInputVariable) obj;
					TableOfInputVariable copy = new TableOfInputVariable();
					if (inpt.getGuid() == null)
					{
						inpt.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						inpt.setMasterGuid(end1ObjectGuid.getMasterGuid());
						insertList.add(inpt);

						copy.sync(inpt);
					}
					else
					{
						copy.sync(inpt);
						// if (inpt.isChanged())
						{
							if (inpt.getReleaseTime() == null)
							{
								updateList.add(inpt);
							}
							else
							{
								TableOfInputVariable newInpt = (TableOfInputVariable) inpt.clone();
								newInpt.clear(TableOfInputVariable.GUID);
								newInpt.clear(TableOfInputVariable.RELEASETIME);
								newInpt.clear(TableOfInputVariable.HASNEXTREVISION);
								newInpt.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(newInpt);

								copy.sync(newInpt);

								inpt.setHasNextRevision(true);
								updateNextRevisionList.add(inpt);
							}
						}
					}
					resultList.add(copy);
				}
				else if (ConfigParameterTableType.MAK == tableType)
				{
					TableOfMark mark = (TableOfMark) obj;
					TableOfMark copy = new TableOfMark();
					if (mark.getGuid() == null)
					{
						mark.setMasterGuid(end1ObjectGuid.getMasterGuid());
						mark.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						insertList.add(mark);

						copy.sync(mark);
					}
					else
					{
						copy.sync(mark);
						// if (mark.isChanged())
						{
							if (mark.getReleaseTime() == null)
							{
								updateList.add(mark);
							}
							else
							{
								TableOfMark newMark = (TableOfMark) mark.clone();
								newMark.clear(TableOfMark.GUID);
								newMark.clear(TableOfMark.RELEASETIME);
								newMark.clear(TableOfMark.HASNEXTREVISION);
								newMark.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(newMark);

								copy.sync(newMark);

								mark.setHasNextRevision(true);
								updateNextRevisionList.add(mark);
							}
						}
					}
					resultList.add(copy);
				}
				else if (ConfigParameterTableType.G == tableType)
				{
					TableOfGroup group = (TableOfGroup) obj;
					TableOfGroup newGroup = new TableOfGroup();

					String masterfk = group.getUniqueValue();
					if (group.getGuid() == null)
					{
						if (StringUtils.isNullString(masterfk))
						{
							masterfk = StringUtils.generateRandomUID(32).toUpperCase();
						}
						group.setMasterGuid(end1ObjectGuid.getMasterGuid());
						group.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						group.setUniqueValue(masterfk);

						// 先取得已有的G番号使用的字段
						if (fieldNameList == null)
						{
							fieldNameList = this.listFieldNameAlreadyUsedByGNumber(instance, ruleTime);
						}

						// 取一个未使用过的G字段
						String fieldName = null;
						for (int i = 1; i <= ConfigParameterConstants.CONFIG_PARAMETER_MAX_G_FIELD_COUNT; i++)
						{
							fieldName = "G" + StringUtils.lpad(String.valueOf(i), 2, '0');
							if (!SetUtils.isNullList(fieldNameList) && fieldNameList.contains(fieldName))
							{
								continue;
							}
							fieldNameList.add(fieldName);
							break;
						}

						if (StringUtils.isNullString(fieldName))
						{
							continue;
						}
						ClassField classField = this.getGField(end1ObjectGuid, fieldName);
						if (classField == null)
						{
							continue;
						}

						group.setClassFieldName(fieldName);
						group.setClassFieldGuid(classField.getGuid());

						insertList.add(group);

						newGroup.sync(group);
					}
					else
					{
						newGroup.sync(group);
						// if (group.isChanged())
						{
							if (group.getReleaseTime() == null)
							{
								updateList.add(group);
							}
							else
							{
								TableOfGroup tempGroup = (TableOfGroup) group.clone();
								tempGroup.clear(TableOfGroup.RELEASETIME);
								tempGroup.clear(TableOfGroup.HASNEXTREVISION);
								tempGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(tempGroup);

								newGroup.sync(tempGroup);

								group.setHasNextRevision(true);
								updateNextRevisionList.add(group);
							}
						}
					}
					resultList.add(newGroup);

					if (!SetUtils.isNullList(group.getColumns()))
					{
						if (SetUtils.isNullMap(titleMap))
						{
							titleMap = this.stubService.getConfigQueryStub().getTitleGuidMap(instance, tableType, false, ruleTime);
						}
						List<DynamicOfColumn> columnList = this.saveColumn(end1ObjectGuid.getMasterGuid(), group.getColumns(), masterfk, ConfigParameterTableType.G,
								exceptColumnTitleList, titleMap);
						if (!SetUtils.isNullList(columnList))
						{
							newGroup.setColumns(columnList);
						}
					}
				}
				else if (ConfigParameterTableType.La == tableType || ConfigParameterTableType.Lb == tableType)
				{
					TableOfList tableOfList = (TableOfList) obj;
					TableOfList newList = new TableOfList();

					// 分组和L番号的关系发生改变或者新增
					String masterfk = tableOfList.getUniqueValue();
					if (tableOfList.getGuid() == null)
					{
						tableOfList.setMasterGuid(end1ObjectGuid.getMasterGuid());
						if (StringUtils.isNullString(masterfk))
						{
							masterfk = StringUtils.generateRandomUID(32).toUpperCase();
						}
						tableOfList.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						tableOfList.setUniqueValue(masterfk);
						tableOfList.setTableType(tableType);
						insertList.add(tableOfList);

						newList.sync(tableOfList);
					}
					else
					{
						newList.sync(tableOfList);
						// if (tableOfList.isChanged())
						{
							if (tableOfList.getReleaseTime() == null)
							{
								updateList.add(tableOfList);
							}
							else
							{
								TableOfList newTableOfList = (TableOfList) tableOfList.clone();
								newTableOfList.clear(TableOfList.RELEASETIME);
								newTableOfList.clear(TableOfList.HASNEXTREVISION);
								newTableOfList.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(newTableOfList);

								newList.sync(newTableOfList);

								tableOfList.setHasNextRevision(true);
								updateNextRevisionList.add(tableOfList);
							}
						}
					}
					resultList.add(newList);

					if (!SetUtils.isNullList(tableOfList.getColumns()))
					{
						if (SetUtils.isNullMap(titleMap))
						{
							titleMap = this.stubService.getConfigQueryStub().getTitleGuidMap(instance, tableType, false, ruleTime);
						}
						List<DynamicOfColumn> columnList = this.saveColumn(end1ObjectGuid.getMasterGuid(), tableOfList.getColumns(), masterfk, ConfigParameterTableType.La,
								exceptColumnTitleList, titleMap);
						if (!SetUtils.isNullList(columnList))
						{
							newList.setColumns(columnList);
						}
					}
				}
				else if (ConfigParameterTableType.A == tableType || ConfigParameterTableType.B == tableType || ConfigParameterTableType.C == tableType
						|| ConfigParameterTableType.D == tableType || ConfigParameterTableType.E == tableType || ConfigParameterTableType.R == tableType
						|| ConfigParameterTableType.Q == tableType)
				{
					TableOfRegion region = (TableOfRegion) obj;
					TableOfRegion newRegion = new TableOfRegion();

					String masterfk = region.getUniqueValue();
					if (region.getGuid() == null)
					{
						region.setMasterGuid(end1ObjectGuid.getMasterGuid());
						if (StringUtils.isNullString(masterfk))
						{
							masterfk = StringUtils.generateRandomUID(32).toUpperCase();
						}
						region.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						region.setUniqueValue(masterfk);
						region.setTableType(tableType);
						insertList.add(region);

						newRegion.sync(region);
					}
					else
					{
						newRegion.sync(region);
						// if (region.isChanged())
						{
							if (region.getReleaseTime() == null)
							{
								updateList.add(region);
							}
							else
							{
								TableOfRegion tempRegion = (TableOfRegion) region.clone();
								tempRegion.clear(TableOfParameter.RELEASETIME);
								tempRegion.clear(TableOfParameter.HASNEXTREVISION);
								tempRegion.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(tempRegion);

								newRegion.sync(tempRegion);

								region.setHasNextRevision(true);
								updateNextRevisionList.add(region);
							}
						}
					}
					resultList.add(newRegion);

					if (!SetUtils.isNullList(region.getColumns()))
					{
						if (SetUtils.isNullMap(titleMap))
						{
							titleMap = this.stubService.getConfigQueryStub().getTitleGuidMap(instance, tableType, false, ruleTime);
						}
						List<DynamicOfColumn> columnList = this.saveColumn(end1ObjectGuid.getMasterGuid(), region.getColumns(), masterfk, ConfigParameterTableType.A,
								exceptColumnTitleList, titleMap);
						if (!SetUtils.isNullList(columnList))
						{
							newRegion.setColumns(columnList);
						}
					}
				}
				else if (ConfigParameterTableType.P == tableType)
				{
					TableOfParameter parameter = (TableOfParameter) obj;
					TableOfParameter newParameter = new TableOfParameter();

					String masterfk = parameter.getUniqueValue();
					if (parameter.getGuid() == null)
					{
						parameter.setMasterGuid(end1ObjectGuid.getMasterGuid());
						if (StringUtils.isNullString(masterfk))
						{
							masterfk = StringUtils.generateRandomUID(32).toUpperCase();
						}
						parameter.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
						parameter.setUniqueValue(masterfk);
						insertList.add(parameter);

						newParameter.sync(parameter);
					}
					else
					{
						newParameter.sync(parameter);
						// if (parameter.isChanged())
						{
							if (parameter.getReleaseTime() == null)
							{
								updateList.add(parameter);
							}
							else
							{
								TableOfParameter tempParameter = (TableOfParameter) parameter.clone();
								tempParameter.clear(TableOfParameter.RELEASETIME);
								tempParameter.clear(TableOfParameter.HASNEXTREVISION);
								tempParameter.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								insertList.add(tempParameter);

								newParameter.sync(tempParameter);

								parameter.setHasNextRevision(true);
								updateNextRevisionList.add(parameter);
							}
						}
					}
					resultList.add(newParameter);

					if (!SetUtils.isNullList(parameter.getColumns()))
					{
						if (SetUtils.isNullMap(titleMap))
						{
							titleMap = this.stubService.getConfigQueryStub().getTitleGuidMap(instance, tableType, false, ruleTime);
						}
						List<DynamicOfColumn> dynamicColList = this.saveColumn(end1ObjectGuid.getMasterGuid(), parameter.getColumns(), masterfk, ConfigParameterTableType.P,
								exceptColumnTitleList, titleMap);
						if (!SetUtils.isNullList(dynamicColList))
						{
							newParameter.setColumns(dynamicColList);
						}
					}
				}
				else
				{
					break;
				}
			}

			SystemDataService sds = DataServer.getSystemDataService();
			if (!SetUtils.isNullList(insertList))
			{
				for (Map<String, Object> param : insertList)
				{
					param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
					param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				}
				if (dataList.get(0) instanceof DynamicColumnTitle)
				{
					sds.insertBatch(DynamicColumnTitle.class, insertList, "inserBatchList");
				}
				else if (ConfigParameterTableType.F == tableType)
				{
					sds.insertBatch(TableOfExpression.class, insertList, "inserBatchList");
				}
				else if (ConfigParameterTableType.INPT == tableType)
				{
					sds.insertBatch(TableOfInputVariable.class, insertList, "inserBatchList");
				}
				else if (ConfigParameterTableType.MAK == tableType)
				{
					sds.insertBatch(TableOfMark.class, insertList, "inserBatchList");
				}
				else if (tableType == ConfigParameterTableType.G)
				{
					sds.insertBatch(TableOfGroup.class, insertList, "inserBatchList");
				}
				else if (tableType == ConfigParameterTableType.La || tableType == ConfigParameterTableType.Lb)
				{
					sds.insertBatch(TableOfList.class, insertList, "inserBatchList");
				}
				else if (tableType == ConfigParameterTableType.A || tableType == ConfigParameterTableType.B || tableType == ConfigParameterTableType.C
						|| tableType == ConfigParameterTableType.D || tableType == ConfigParameterTableType.E || tableType == ConfigParameterTableType.R
						|| tableType == ConfigParameterTableType.Q)
				{
					sds.insertBatch(TableOfRegion.class, insertList, "inserBatchList");
				}
				else if (tableType == ConfigParameterTableType.P)
				{
					sds.insertBatch(TableOfParameter.class, insertList, "inserBatchList");
				}
			}
			if (!SetUtils.isNullList(updateList))
			{
				for (Map<String, Object> param : updateList)
				{
					param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
					param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				}
				if (dataList.get(0) instanceof DynamicColumnTitle)
				{
					sds.updateBatch(DynamicColumnTitle.class, updateList, "updateBatchList");
				}
				else if (ConfigParameterTableType.F == tableType)
				{
					sds.updateBatch(TableOfExpression.class, updateList, "updateBatchList");
				}
				else if (ConfigParameterTableType.INPT == tableType)
				{
					sds.updateBatch(TableOfInputVariable.class, updateList, "updateBatchList");
				}
				else if (ConfigParameterTableType.MAK == tableType)
				{
					sds.updateBatch(TableOfMark.class, updateList, "updateBatchList");
				}
				else if (tableType == ConfigParameterTableType.G)
				{
					sds.updateBatch(TableOfGroup.class, updateList, "updateBatchList");
				}
				else if (tableType == ConfigParameterTableType.La || tableType == ConfigParameterTableType.Lb)
				{
					sds.updateBatch(TableOfList.class, updateList, "updateBatchList");
				}
				else if (tableType == ConfigParameterTableType.A || tableType == ConfigParameterTableType.B || tableType == ConfigParameterTableType.C
						|| tableType == ConfigParameterTableType.D || tableType == ConfigParameterTableType.E || tableType == ConfigParameterTableType.R
						|| tableType == ConfigParameterTableType.Q)
				{
					sds.updateBatch(TableOfRegion.class, updateList, "updateBatchList");
				}
				else if (dataList.get(0) instanceof TableOfParameter)
				{
					sds.updateBatch(TableOfParameter.class, updateList, "updateBatchList");
				}
			}
			if (!SetUtils.isNullList(updateNextRevisionList))
			{
				for (Map<String, Object> param : updateNextRevisionList)
				{
					param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
					param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				}
				if (dataList.get(0) instanceof DynamicColumnTitle)
				{
					sds.updateBatch(DynamicColumnTitle.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (ConfigParameterTableType.F == tableType)
				{
					sds.updateBatch(TableOfExpression.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (ConfigParameterTableType.INPT == tableType)
				{
					sds.updateBatch(TableOfInputVariable.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (ConfigParameterTableType.MAK == tableType)
				{
					sds.updateBatch(TableOfMark.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (tableType == ConfigParameterTableType.G)
				{
					sds.updateBatch(TableOfGroup.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (tableType == ConfigParameterTableType.La || tableType == ConfigParameterTableType.Lb)
				{
					sds.updateBatch(TableOfList.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (tableType == ConfigParameterTableType.A || tableType == ConfigParameterTableType.B || tableType == ConfigParameterTableType.C
						|| tableType == ConfigParameterTableType.D || tableType == ConfigParameterTableType.E || tableType == ConfigParameterTableType.R
						|| tableType == ConfigParameterTableType.Q)
				{
					sds.updateBatch(TableOfRegion.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
				else if (tableType == ConfigParameterTableType.P)
				{
					sds.updateBatch(TableOfParameter.class, updateNextRevisionList, "updateNextRevisionBatchList");
				}
			}
		}
		return resultList;
	}

	private List<String> listFieldNameAlreadyUsedByGNumber(FoundationObject instance, Date ruleTime) throws ServiceRequestException
	{
		List<String> fieldNameList = new ArrayList<String>();
		List<TableOfGroup> groupList = this.stubService.getConfigQueryStub().listTableOfGroup(instance, ruleTime);
		if (!SetUtils.isNullList(groupList))
		{
			for (TableOfGroup g : groupList)
			{
				fieldNameList.add(g.getClassFieldName());
			}
		}
		return fieldNameList;
	}

	private ClassField getGField(ObjectGuid end1ObjectGuid, String fieldName) throws ServiceRequestException
	{
		RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
		return this.stubService.getEMM().getFieldByName(template.getStructureClassName(), fieldName, true);
	}

	/**
	 * 按行删除数据
	 * 
	 * @param data
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("rawtypes")
	private void deleteLineData(List dataList, String masterGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
		if (!SetUtils.isNullList(dataList))
		{
			for (Object data : dataList)
			{
				ConfigBase data_ = (ConfigBase) data;
				if (!StringUtils.isGuid(data_.getGuid()))
				{
					return;
				}

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("GUID", data_.getGuid());
				param.put("MASTERGUID", data_.getUniqueValue());
				param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				param.put("ITEMMASTERGUID", data_.getMasterGuid());
				paramList.add(param);
			}
			Object data = dataList.get(0);
			if (data instanceof TableOfGroup)
			{
				sds.deleteBatch(TableOfGroup.class, paramList, "deleteLine");
				sds.updateBatch(TableOfGroup.class, paramList, "updateNextRevisionBatchList");
				sds.deleteBatch(TableOfGroup.class, paramList, "deleteCustLine");
				sds.updateBatch(TableOfGroup.class, paramList, "updateCustNextRevisionBatchList");
			}
			if (data instanceof TableOfList)
			{
				sds.deleteBatch(TableOfList.class, paramList, "deleteLine");
				sds.updateBatch(TableOfList.class, paramList, "updateNextRevisionBatchList");
				sds.deleteBatch(TableOfList.class, paramList, "deleteCustLine");
				sds.updateBatch(TableOfList.class, paramList, "updateCustNextRevisionBatchList");
			}
			if (data instanceof TableOfExpression)
			{
				sds.deleteBatch(TableOfExpression.class, paramList, "deleteLine");
				sds.updateBatch(TableOfExpression.class, paramList, "updateNextRevisionBatchList");
			}
			if (data instanceof TableOfInputVariable)
			{
				sds.deleteBatch(TableOfInputVariable.class, paramList, "deleteLine");
				sds.updateBatch(TableOfInputVariable.class, paramList, "updateNextRevisionBatchList");
			}
			if (data instanceof TableOfMark)
			{
				sds.deleteBatch(TableOfMark.class, paramList, "deleteLine");
				sds.updateBatch(TableOfMark.class, paramList, "updateNextRevisionBatchList");
			}
			if (data instanceof TableOfParameter)
			{
				sds.deleteBatch(TableOfParameter.class, paramList, "deleteLine");
				sds.updateBatch(TableOfParameter.class, paramList, "updateNextRevisionBatchList");
				sds.deleteBatch(TableOfParameter.class, paramList, "deleteCustLine");
				sds.updateBatch(TableOfParameter.class, paramList, "updateCustNextRevisionBatchList");
			}
			else if (data instanceof TableOfRegion)
			{
				sds.deleteBatch(TableOfRegion.class, paramList, "deleteLine");
				sds.updateBatch(TableOfRegion.class, paramList, "updateNextRevisionBatchList");
				sds.deleteBatch(TableOfRegion.class, paramList, "deleteCustLine");
				sds.updateBatch(TableOfRegion.class, paramList, "updateCustNextRevisionBatchList");
			}
		}
	}

	/**
	 * 批量删除结构
	 * 
	 * @param viewObjectGuid
	 * @throws ServiceRequestException
	 */
	protected void deleteRelation(ObjectGuid viewObjectGuid) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			ViewObject view = this.stubService.getBOAS().getRelation(viewObjectGuid);
			if (view != null)
			{
				RelationTemplateInfo template = this.stubService.getEMM().getRelationTemplateById(view.getTemplateID());
				if (template != null)
				{
					RelationService ds = DataServer.getRelationService();
					ds.deleteAllStruc(template.getStructureClassName(), null, view.getObjectGuid(), Constants.isSupervisor(true, this.stubService),
							this.stubService.getUserSignature().getCredential());
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

	// 删除指定列
	private void deleteColumnTitle(List<DynamicColumnTitle> deleteColumnTitleList, boolean isContainData) throws ServiceRequestException
	{
		String masterGuid = null;
		ConfigParameterTableType tableType = null;
		SystemDataService sds = DataServer.getSystemDataService();
		if (!SetUtils.isNullList(deleteColumnTitleList))
		{
			List<String> obsoletGuidList = new ArrayList<String>();
			List<String> deleteGuidList = new ArrayList<String>();
			List<String> columnNameList = new ArrayList<String>();

			Map<String, Object> paramMap = new HashMap<String, Object>();
			for (DynamicColumnTitle columnTitle : deleteColumnTitleList)
			{
				if (masterGuid == null)
				{
					masterGuid = columnTitle.getMasterGuid();
				}
				if (tableType == null)
				{
					tableType = columnTitle.getTableType();
				}

				columnNameList.add(columnTitle.getUniqueValue());
				if (columnTitle.getReleaseTime() == null)
				{
					deleteGuidList.add(columnTitle.getGuid());
				}
				else
				{
					obsoletGuidList.add(columnTitle.getGuid());
				}
			}

			// 删除新加的未发布的列
			if (!SetUtils.isNullList(deleteGuidList))
			{
				paramMap.put("TITLEGUIDLIST", deleteGuidList);
				sds.delete(DynamicColumnTitle.class, paramMap, "delete");
			}

			// 删除已发布的列（废弃）
			if (!SetUtils.isNullList(obsoletGuidList))
			{
				paramMap.put("TITLEGUIDLIST", obsoletGuidList);
				paramMap.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				sds.update(DynamicColumnTitle.class, paramMap, "obsoleteOnly");
			}

			if (!isContainData)
			{
				return;
			}

			// 删除列数据
			if (tableType == ConfigParameterTableType.G)
			{
				// 删除未发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				sds.delete(TableOfGroup.class, paramMap, "deleteColumnByTitle");

				// 废弃已发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				paramMap.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				sds.update(TableOfGroup.class, paramMap, "obsoleteColumnByTitle");
			}
			else if (tableType == ConfigParameterTableType.La || tableType == ConfigParameterTableType.Lb)
			{
				// 删除未发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				sds.delete(TableOfList.class, paramMap, "deleteColumnByTitle");

				// 废弃已发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				paramMap.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				sds.update(TableOfList.class, paramMap, "obsoleteColumnByTitle");
			}
			else if (tableType == ConfigParameterTableType.A || tableType == ConfigParameterTableType.B || tableType == ConfigParameterTableType.C
					|| tableType == ConfigParameterTableType.D || tableType == ConfigParameterTableType.E || tableType == ConfigParameterTableType.R
					|| tableType == ConfigParameterTableType.Q)
			{
				// 删除未发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				sds.delete(TableOfRegion.class, paramMap, "deleteColumnByTitle");

				// 废弃已发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				paramMap.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				sds.update(TableOfRegion.class, paramMap, "obsoleteColumnByTitle");
			}
			else if (tableType == ConfigParameterTableType.P)
			{
				// 删除未发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				sds.delete(TableOfParameter.class, paramMap, "deleteColumnByTitle");

				// 废弃已发布的字段
				paramMap.clear();
				paramMap.put("TITLELIST", columnNameList);
				paramMap.put("MASTERGUID", masterGuid);
				paramMap.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
				sds.update(TableOfParameter.class, paramMap, "obsoleteColumnByTitle");
			}
		}
	}

	/**
	 * 批量更新自定义字段
	 * 
	 * @param columns
	 * @param masterfk
	 * @param tableType
	 */
	private List<DynamicOfColumn> saveColumn(String masterGuid, List<DynamicOfColumn> columns, String masterfk, ConfigParameterTableType tableType,
			List<String> exceptColumnTitleList, Map<String, String> titleMap)
	{
		SystemDataService sds = DataServer.getSystemDataService();

		List<DynamicOfColumn> insertList = new ArrayList<DynamicOfColumn>();
		List<String> deleteList = new ArrayList<String>();
		List<DynamicOfColumn> updateList = new ArrayList<DynamicOfColumn>();
		List<String> updateNextRevisionList = new ArrayList<String>();
		List<DynamicOfColumn> resultList = new ArrayList<DynamicOfColumn>();

		if (!SetUtils.isNullList(columns))
		{
			for (DynamicOfColumn column : columns)
			{
				column.setTitleGuid(titleMap.get(column.getName()));
				if (!SetUtils.isNullList(exceptColumnTitleList) && exceptColumnTitleList.contains(column.getName()))
				{
					continue;
				}

				if (column.getGuid() == null)
				{
					column.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
					column.setMasterFK(masterfk);
					column.put("MASTERGUID", masterGuid);
					insertList.add(column);

					DynamicOfColumn copy = new DynamicOfColumn();
					copy.sync(column);
					resultList.add(copy);
				}
				else
				{
					if (column.isChanged(DynamicOfColumn.VALUE))
					{
						if (column.getReleaseTime() == null)
						{
							// 未发布时修改的数据，值为空时，直接删除原数据
							if (StringUtils.isNullString(column.getValue()))
							{
								deleteList.add(column.getGuid());
							}
							else
							{
								updateList.add(column);

								DynamicOfColumn copy = new DynamicOfColumn();
								copy.sync(column);
								resultList.add(copy);
							}
						}
						else
						{
							DynamicOfColumn newColumn = (DynamicOfColumn) column.clone();
							newColumn.clear(TableOfMark.RELEASETIME);
							newColumn.clear(TableOfMark.HASNEXTREVISION);
							newColumn.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							newColumn.put("MASTERGUID", masterGuid);
							insertList.add(newColumn);

							DynamicOfColumn copy = new DynamicOfColumn();
							copy.sync(newColumn);
							resultList.add(copy);

							column.setHasNextRevision(true);
							updateNextRevisionList.add(column.getGuid());
						}
					}
				}
			}

			if (!SetUtils.isNullList(insertList))
			{
				for (Map<String, Object> param : insertList)
				{
					param.put("TABLENAME", this.getCustTableName(tableType));
				}
				sds.insertBatch(DynamicOfColumn.class, insertList, "insertCustColumn");
			}
			if (!SetUtils.isNullList(updateList))
			{
				for (Map<String, Object> param : updateList)
				{
					param.put("TABLENAME", this.getCustTableName(tableType));
				}
				sds.updateBatch(DynamicOfColumn.class, updateList, "updateCustColumn");
			}

			if (!SetUtils.isNullList(updateNextRevisionList))
			{
				List<Map<String, Object>> paramList = new LinkedList<>();
				for (String guid : updateNextRevisionList)
				{
					Map<String, Object> param = new HashMap<>();
					param.put("TABLENAME", this.getCustTableName(tableType));
					param.put("GUID", guid);
					paramList.add(param);
				}
				sds.updateBatch(DynamicOfColumn.class, paramList, "updateCustColumnHasNextRevision");
			}
			if (!SetUtils.isNullList(deleteList))
			{
				List<Map<String, Object>> paramList = new LinkedList<>();
				for (String guid : deleteList)
				{
					Map<String, Object> param = new HashMap<>();
					param.put("TABLENAME", this.getCustTableName(tableType));
					param.put("GUID", guid);
					paramList.add(param);
				}
				sds.deleteBatch(DynamicOfColumn.class, paramList, "deleteCustColumn");
			}
		}
		return resultList;
	}

	private String getCustTableName(ConfigParameterTableType tableType)
	{
		if (tableType == ConfigParameterTableType.La || tableType == ConfigParameterTableType.Lb)
		{
			return "MA_CONFIG_TABLE_CUSTM_L";
		}
		else if (tableType == ConfigParameterTableType.G)
		{
			return "MA_CONFIG_TABLE_CUSTM_G";
		}
		else if (tableType == ConfigParameterTableType.P)
		{
			return "MA_CONFIG_TABLE_CUSTM_P";
		}
		else if (tableType == ConfigParameterTableType.A || tableType == ConfigParameterTableType.B || tableType == ConfigParameterTableType.C
				|| tableType == ConfigParameterTableType.D || tableType == ConfigParameterTableType.E || tableType == ConfigParameterTableType.R
				|| tableType == ConfigParameterTableType.Q)
		{
			return "MA_CONFIG_TABLE_CUSTM_AERQ";
		}
		return null;
	}

	private String getMessage(String id, Object... agrs) throws ServiceRequestException
	{
		return this.stubService.getMSRM().getMSRString(id, this.stubService.getUserSignature().getLanguageEnum().getId(), agrs);
	}

	private void rebuildExceptionMsg(ConfigParameterTableType tableType, DynaDataException e) throws ServiceRequestException
	{
		if (e.getDataExceptionEnum() == DataExceptionEnum.DS_VALUE_TOO_LARGE)
		{
			Object[] newArgs = new Object[3];
			if (e.getArgs() != null && e.getArgs().length == 3)
			{
				newArgs[0] = this.getTitleByColumn(tableType, (String) e.getArgs()[0]);
				newArgs[1] = e.getArgs()[1];
				if (e.getArgs()[2] != null && ((String) (e.getArgs()[2])).contains("ORA-06512") && ((String) (e.getArgs()[2])).contains("\n"))
				{
					String[] tmp = ((String) (e.getArgs()[2])).split("\n");
					newArgs[2] = tmp[0];
				}
				else
				{
					newArgs[2] = e.getArgs()[2];
				}
			}
			e.setArgs(newArgs);
		}
	}

	/**
	 * 可接受表类型:G,L,A,B,C,D,E,R,Q,F,INPT,P,MAK,DETAIL
	 * 
	 * @param destObjectGuid
	 * @param origObjectGuid
	 * @param ruleTime
	 * @param tableTypeList
	 * @throws ServiceRequestException
	 */
	protected void copyConfigData(ObjectGuid destObjectGuid, ObjectGuid origObjectGuid, Date ruleTime, List<ConfigParameterTableType> tableTypeList) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(origObjectGuid.getMasterGuid()) || !StringUtils.isGuid(destObjectGuid.getMasterGuid()))
		{
			return;
		}
		if (SetUtils.isNullList(tableTypeList))
		{
			return;
		}

		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(origObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + origObjectGuid.getGuid() + "'");
		}

		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}

		RelationService ds = DataServer.getRelationService();
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			List<DynamicColumnTitle> custTitleList = new LinkedList<>();
			for (ConfigParameterTableType tableType : tableTypeList)
			{
				if (tableType == ConfigParameterTableType.G)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(TableOfGroup.class, searchConditionMap, "deleteByMaster");
					sds.delete(TableOfGroup.class, searchConditionMap, "deleteCustByMaster");
					sds.delete(TableOfGroup.class, searchConditionMap, "deleteInfoByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfGroup> gNumberList = sds.query(TableOfGroup.class, searchConditionMap, "selectGNumberList");
					if (!SetUtils.isNullList(gNumberList))
					{
						for (TableOfGroup tableOfGroup : gNumberList)
						{
							tableOfGroup.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfGroup.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfGroup.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfGroup.class, gNumberList, "inserBatchList");

						searchConditionMap.put("TABLETYPE", "'G'");
						List<DynamicColumnTitle> tempList = sds.query(DynamicColumnTitle.class, searchConditionMap, "selectCustTtileOfTable");
						if (!SetUtils.isNullList(tempList))
						{
							for (DynamicColumnTitle titleOfGroup : tempList)
							{
								titleOfGroup.setMasterGuid(destObjectGuid.getMasterGuid());
								titleOfGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								titleOfGroup.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								titleOfGroup.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								custTitleList.add(titleOfGroup);
							}
						}
						List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfG");
						if (!SetUtils.isNullList(columnList))
						{
							for (DynamicOfColumn varOfGroup : columnList)
							{
								varOfGroup.setMasterGuid(destObjectGuid.getMasterGuid());
								varOfGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								varOfGroup.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfGroup.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfGroup.put("TABLENAME", this.getCustTableName(tableType));
							}
							sds.insertBatch(DynamicOfColumn.class, columnList, "inserBatchList");
						}
					}
				}
				else if (tableType == ConfigParameterTableType.L)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(TableOfList.class, searchConditionMap, "deleteByMaster");
					sds.delete(TableOfList.class, searchConditionMap, "deleteCustByMaster");
					sds.delete(TableOfList.class, searchConditionMap, "deleteInfoByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfList> lNumberList = sds.query(TableOfList.class, searchConditionMap, "selectAllList");
					if (!SetUtils.isNullList(lNumberList))
					{
						for (TableOfList tableOfList : lNumberList)
						{
							tableOfList.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfList.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfList.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfList.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfList.class, lNumberList, "inserBatchList");

						searchConditionMap.put("TABLETYPE", "'L'");
						List<DynamicColumnTitle> tempList = sds.query(DynamicColumnTitle.class, searchConditionMap, "selectCustTtileOfTable");
						if (!SetUtils.isNullList(tempList))
						{
							for (DynamicColumnTitle titleOfGroup : tempList)
							{
								titleOfGroup.setMasterGuid(destObjectGuid.getMasterGuid());
								titleOfGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								titleOfGroup.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								titleOfGroup.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								custTitleList.add(titleOfGroup);
							}
						}

						List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfList");
						if (!SetUtils.isNullList(columnList))
						{
							for (DynamicOfColumn varOfList : columnList)
							{
								varOfList.setMasterGuid(destObjectGuid.getMasterGuid());
								varOfList.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								varOfList.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfList.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfList.put("TABLENAME", this.getCustTableName(tableType));
							}
							sds.insertBatch(DynamicOfColumn.class, columnList, "inserBatchList");
						}
					}
				}
				else if (tableType == ConfigParameterTableType.A_E || tableType == ConfigParameterTableType.R_Q)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					searchConditionMap.put("TABLETYPELIST", "'A','B','C','D','E'");
					searchConditionMap.put("TABLETYPE", "'A','B','C','D','E'");
					if (tableType == ConfigParameterTableType.R_Q)
					{
						searchConditionMap.put("TABLETYPELIST", "'R','Q'");
						searchConditionMap.put("TABLETYPE", "'R','Q'");

					}
					sds.delete(TableOfRegion.class, searchConditionMap, "deleteByMaster");
					sds.delete(TableOfRegion.class, searchConditionMap, "deleteCustByMaster");
					sds.delete(TableOfRegion.class, searchConditionMap, "deleteInfoByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfRegion> aeList = sds.query(TableOfRegion.class, searchConditionMap);
					if (!SetUtils.isNullList(aeList))
					{
						for (TableOfRegion tableOfRegion : aeList)
						{
							tableOfRegion.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfRegion.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfRegion.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfRegion.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfRegion.class, aeList, "inserBatchList");

						List<DynamicColumnTitle> tempList = sds.query(DynamicColumnTitle.class, searchConditionMap, "selectCustTtileOfTable");
						if (!SetUtils.isNullList(tempList))
						{
							for (DynamicColumnTitle titleOfGroup : tempList)
							{
								titleOfGroup.setMasterGuid(destObjectGuid.getMasterGuid());
								titleOfGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								titleOfGroup.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								titleOfGroup.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								custTitleList.add(titleOfGroup);
							}
						}

						List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfRegion");
						if (!SetUtils.isNullList(columnList))
						{
							for (DynamicOfColumn varOfList : columnList)
							{
								varOfList.setMasterGuid(destObjectGuid.getMasterGuid());
								varOfList.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								varOfList.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfList.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfList.put("TABLENAME", this.getCustTableName(tableType));
							}
							sds.insertBatch(DynamicOfColumn.class, columnList, "inserBatchList");
						}
					}
				}
				else if (tableType == ConfigParameterTableType.INPT)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(TableOfInputVariable.class, searchConditionMap, "deleteByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfInputVariable> inputVarList = sds.query(TableOfInputVariable.class, searchConditionMap);
					if (!SetUtils.isNullList(inputVarList))
					{
						for (TableOfInputVariable tableOfInpt : inputVarList)
						{
							tableOfInpt.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfInpt.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfInpt.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfInpt.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfInputVariable.class, inputVarList, "inserBatchList");
					}
				}
				else if (tableType == ConfigParameterTableType.P)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(TableOfParameter.class, searchConditionMap, "deleteByMaster");
					sds.delete(TableOfParameter.class, searchConditionMap, "deleteCustByMaster");
					sds.delete(TableOfParameter.class, searchConditionMap, "deleteInfoByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfParameter> pTableList = sds.query(TableOfParameter.class, searchConditionMap);
					if (!SetUtils.isNullList(pTableList))
					{
						for (TableOfParameter tableOfParam : pTableList)
						{
							tableOfParam.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfParam.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfParam.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfParam.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfParameter.class, pTableList, "inserBatchList");

						searchConditionMap.put("TABLETYPE", "'P'");
						List<DynamicColumnTitle> tempList = sds.query(DynamicColumnTitle.class, searchConditionMap, "selectCustTtileOfTable");
						if (!SetUtils.isNullList(tempList))
						{
							for (DynamicColumnTitle titleOfGroup : tempList)
							{
								titleOfGroup.setMasterGuid(destObjectGuid.getMasterGuid());
								titleOfGroup.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								titleOfGroup.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								titleOfGroup.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								custTitleList.add(titleOfGroup);
							}
						}

						List<DynamicOfColumn> columnList = sds.query(DynamicOfColumn.class, searchConditionMap, "listAllVirableOfP");
						if (!SetUtils.isNullList(columnList))
						{
							for (DynamicOfColumn varOfList : columnList)
							{
								varOfList.setMasterGuid(destObjectGuid.getMasterGuid());
								varOfList.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
								varOfList.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfList.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
								varOfList.put("TABLENAME", this.getCustTableName(tableType));
							}
							sds.insertBatch(DynamicOfColumn.class, columnList, "inserBatchList");
						}
					}
				}
				else if (tableType == ConfigParameterTableType.MAK)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(TableOfMark.class, searchConditionMap, "deleteByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfMark> markVarList = sds.query(TableOfMark.class, searchConditionMap);
					if (!SetUtils.isNullList(markVarList))
					{
						for (TableOfMark tableOfMark : markVarList)
						{
							tableOfMark.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfMark.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfMark.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfMark.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfMark.class, markVarList, "inserBatchList");
					}
				}
				else if (tableType == ConfigParameterTableType.F)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(TableOfExpression.class, searchConditionMap, "deleteByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<TableOfExpression> expTableList = sds.query(TableOfExpression.class, searchConditionMap);
					if (!SetUtils.isNullList(expTableList))
					{
						for (TableOfExpression tableOfExp : expTableList)
						{
							tableOfExp.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfExp.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfExp.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfExp.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(TableOfExpression.class, expTableList, "inserBatchList");
					}
				}
				else if (tableType == ConfigParameterTableType.M)
				{
					Map<String, Object> searchConditionMap = new HashMap<String, Object>();
					// 删除目标对象单变量表
					searchConditionMap.put("MASTERGUID", destObjectGuid.getMasterGuid());
					sds.delete(DynamicOfMultiVariable.class, searchConditionMap, "deleteByMaster");

					searchConditionMap.put("MASTERGUID", instance.getObjectGuid().getMasterGuid());
					List<DynamicOfMultiVariable> mutiConList = sds.query(DynamicOfMultiVariable.class, searchConditionMap, "listMultiVariable");
					if (!SetUtils.isNullList(mutiConList))
					{
						for (DynamicOfMultiVariable tableOfMutiCon : mutiConList)
						{
							tableOfMutiCon.setMasterGuid(destObjectGuid.getMasterGuid());
							tableOfMutiCon.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
							tableOfMutiCon.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
							tableOfMutiCon.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
						}
						sds.insertBatch(DynamicOfMultiVariable.class, mutiConList, "inserBatchList");
					}
				}
				else if (tableType == ConfigParameterTableType.DETAIL)
				{
					RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(destObjectGuid,
							ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
					if (relationTemplate == null)
					{
						throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME,
								null, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
					}

					// 目标对象已有明细结构，删除原有明细结构
					ViewObject destViewObject = this.stubService.getBOAS().getRelationByEND1(destObjectGuid, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
					if (destViewObject != null)
					{
						this.stubService.deleteRelation(destViewObject.getObjectGuid());
					}

					// 源对象没有明细结构，退出
					ViewObject origViewObject = this.stubService.getBOAS().getRelationByEND1(origObjectGuid, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
					if (origViewObject != null)
					{

						destViewObject = this.stubService.getBOAS().saveRelationByTemplate(relationTemplate.getGuid(), destObjectGuid);

						// 复制
						Map<String, Object> specialField = new HashMap<String, Object>();
						specialField.put(BOMStructure.RSFLAG, "N");
						specialField.put(BOMStructure.BOMKEY, UUID.randomUUID().toString().replace("-", ""));
						ds.copyBomOrRelation(destViewObject.getObjectGuid().getClassGuid(), origViewObject.getObjectGuid().getGuid(), destViewObject.getObjectGuid().getGuid(),
								relationTemplate.getStructureClassGuid(), "5", specialField, this.stubService.getUserSignature().getCredential(),
								this.stubService.getFixedTransactionId());
						// 检查结构是否出现循环
						Scheduler schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
						if (schedule == null)
						{
							this.serviceContext.getServerContext().getSchedulerManager().createScheduler("SelectConnect", 8);
							schedule = this.serviceContext.getServerContext().getSchedulerManager().getScheduler("SelectConnect");
						}
						CheckConnectUtil util = new CheckConnectUtil(this.stubService.getEMM(), schedule, this.stubService.getUserSignature().getCredential(),
								ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, false);
						if (util.checkConntc(destObjectGuid))
						{
							throw new DynaDataExceptionAll("connect by error ", null, DataExceptionEnum.DS_CONNECT_BY_ERROR);
						}
					}
				}
			}
			sds.insertBatch(DynamicColumnTitle.class, custTitleList, "inserBatchList");
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

	/**
	 * 任何一张配置表,存在已发布的配置数据,则不允许拷贝
	 * 
	 * @param masterGuid
	 * @return
	 */
	protected boolean isConfigTableCanBeCopy(String masterGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", masterGuid);
		DynamicColumnTitle title = sds.queryObject(DynamicColumnTitle.class, searchConditionMap, "haveRLSData");
		if (title != null && "Y".equals(title.get("HAVERLS")))
		{
			return false;
		}
		TableOfGroup group = sds.queryObject(TableOfGroup.class, searchConditionMap, "haveRLSData");
		if (group != null && "Y".equals(group.get("HAVERLS")))
		{
			return false;
		}
		TableOfList tList = sds.queryObject(TableOfList.class, searchConditionMap, "haveRLSData");
		if (tList != null && "Y".equals(tList.get("HAVERLS")))
		{
			return false;
		}
		TableOfRegion region = sds.queryObject(TableOfRegion.class, searchConditionMap, "haveRLSData");
		if (region != null && "Y".equals(region.get("HAVERLS")))
		{
			return false;
		}
		TableOfParameter parmeter = sds.queryObject(TableOfParameter.class, searchConditionMap, "haveRLSData");
		if (parmeter != null && "Y".equals(parmeter.get("HAVERLS")))
		{
			return false;
		}
		TableOfInputVariable inpt = sds.queryObject(TableOfInputVariable.class, searchConditionMap, "haveRLSData");
		if (inpt != null && "Y".equals(inpt.get("HAVERLS")))
		{
			return false;
		}
		TableOfExpression expression = sds.queryObject(TableOfExpression.class, searchConditionMap, "haveRLSData");
		if (expression != null && "Y".equals(expression.get("HAVERLS")))
		{
			return false;
		}
		TableOfMark mark = sds.queryObject(TableOfMark.class, searchConditionMap, "haveRLSData");
		if (mark != null && "Y".equals(mark.get("HAVERLS")))
		{
			return false;
		}
		return true;
	}

	private String getTitleByColumn(ConfigParameterTableType tableType, String columnName) throws ServiceRequestException
	{
		String locale = this.stubService.getUserSignature().getLanguageEnum().getId();
		if (ConfigParameterTableType.A == tableType || ConfigParameterTableType.B == tableType || ConfigParameterTableType.C == tableType || ConfigParameterTableType.D == tableType
				|| ConfigParameterTableType.E == tableType)
		{
			if (TableOfRegion.LOWERLIMIT1.equals(columnName))
			{
				return ">=" + this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AE_LOWERLIMIT1", locale);
			}
			else if (TableOfRegion.UPPERLIMIT1.equals(columnName))
			{
				return "<" + this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AE_UPPERLIMIT1", locale);
			}
			else if (TableOfRegion.VARIABLE1.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AERQ_VARIABLE", locale);
			}
		}
		else if (ConfigParameterTableType.R == tableType || ConfigParameterTableType.Q == tableType)
		{
			if (TableOfRegion.LOWERLIMIT1.equals(columnName))
			{
				return ">=" + this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AE_LOWERLIMIT1", locale) + "1";
			}
			else if (TableOfRegion.UPPERLIMIT1.equals(columnName))
			{
				return "<" + this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AE_UPPERLIMIT1", locale) + "1";
			}
			else if (TableOfRegion.VARIABLE1.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AERQ_VARIABLE", locale) + "1";
			}
			else if (TableOfRegion.LOWERLIMIT2.equals(columnName))
			{
				return ">=" + this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AE_LOWERLIMIT1", locale) + "2";
			}
			else if (TableOfRegion.UPPERLIMIT2.equals(columnName))
			{
				return "<" + this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AE_UPPERLIMIT1", locale) + "2";
			}
			else if (TableOfRegion.VARIABLE2.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AERQ_VARIABLE", locale) + "2";
			}
		}
		else if (ConfigParameterTableType.G == tableType)
		{
			if (TableOfGroup.GNUMBER.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_G_GNUMBER", locale);
			}
		}
		else if (ConfigParameterTableType.La == tableType || ConfigParameterTableType.Lb == tableType)
		{
			if (TableOfList.GROUPNAME.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_L_GROUPVALUE", locale);
			}
			else if (TableOfList.DESCRIPTION.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_L_DESCRIPTION", locale);
			}
			else if (TableOfList.LNUMBER.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_L_LNUMBER", locale);
			}
		}
		else if (ConfigParameterTableType.F == tableType)
		{
			if (TableOfExpression.DRAWVARIABLE.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_F_DRAWVARIABLE", locale);
			}
			else if (TableOfExpression.FORMULA.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_F_FORMULA", locale);
			}
			else if (TableOfExpression.VARIABLEINFORMULA.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_AERQ_VARIABLE", locale);
			}
		}
		else if (ConfigParameterTableType.P == tableType)
		{
			if (TableOfParameter.GNUMBER.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_G_GNUMBER", locale);
			}
		}
		else if (ConfigParameterTableType.MAK == tableType)
		{
			if (TableOfMark.MAK.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_MAK_NAME", locale);
			}
			else if (TableOfMark.VALUE.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_MAK_DESCRIPTION", locale);
			}
		}
		else if (ConfigParameterTableType.INPT == tableType)
		{
			if (TableOfInputVariable.NAME.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_INPT_NAME", locale);
			}
			else if (TableOfInputVariable.DESCRIPTION.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_INPT_DESCRIPTION", locale);
			}
			else if (TableOfInputVariable.VALUETYPE.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_INPT_VALUETYPE", locale);
			}
			else if (TableOfInputVariable.RANGE.equals(columnName))
			{
				return this.stubService.getMSRM().getMSRString("ID_CLIENT_INSTANCE_CONFIG_INPT_RANGE", locale);
			}
		}
		return columnName;
	}

	/**
	 * 检查G表,L表和输入变量表的SN是否有重复
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void isDuplicateSN(String masterGuid, Date releaseTime, ConfigParameterTableType tableType) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		TableOfGroup searchConditionMap = new TableOfGroup();
		searchConditionMap.setMasterGuid(masterGuid);
		if (releaseTime != null)
		{
			releaseTime = DateFormat.getDateOfEnd(releaseTime, DateFormat.PTN_YMDHMS);
		}
		searchConditionMap.setReleaseTime(releaseTime);

		Class clz = null;
		if (tableType == ConfigParameterTableType.G)
		{
			clz = TableOfGroup.class;
		}
		else if (tableType == ConfigParameterTableType.La || tableType == ConfigParameterTableType.Lb)
		{
			clz = TableOfList.class;
		}
		else if (tableType == ConfigParameterTableType.INPT)
		{
			clz = TableOfInputVariable.class;
		}
		else
		{
			return;
		}

		SystemObjectImpl result = (SystemObjectImpl) sds.queryObject(clz, searchConditionMap, "isDuplicateSN");
		if (result != null && "Y".equals(result.get("FLG")))
		{
			throw new ServiceRequestException("ID_APP_CONFIG_SN_DUPLICATE_SET", "duplicate settings for SN.", null);
		}
	}

	public List<BOMStructure> listBOM(FoundationObject item, SearchCondition searchCondition, DataRule dataRule, String origGNumber) throws ServiceRequestException
	{
		List<BOMStructure> reList = null;
		try
		{
			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(item.getObjectGuid(), this.stubService);

			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(item.getObjectGuid(),
					ConfigParameterConstants.CONFIG_PARAMETER_RESULTRELATION_TEMPLATE_NAME);
			if (relationTemplate == null)
			{
				return null;
			}

			if (searchCondition == null)
			{
				List<UIObjectInfo> uiObjectList = this.stubService.getEMM().listALLFormListUIObjectInBizModel(relationTemplate.getStructureClassName());
				searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(relationTemplate.getStructureClassName(), uiObjectList);
			}

			if (dataRule == null)
			{
				dataRule = new DataRule();
				dataRule.setLocateTime(item.getReleaseTime());
				dataRule.setSystemStatus(item.getStatus());
			}
			if (item.isLatestRevision())
			{
				dataRule.setLocateTime(null);
			}

			FoundationObject draw = this.stubService.getOrderConfigureStub().getDrawInstanceByItem(item, dataRule.getLocateTime());

			List<StructureObject> strucList = this.stubService.driveResult4Order(item, draw, dataRule, null);
			if (SetUtils.isNullList(strucList))
			{
				return null;
			}

			int sequence = 1;
			reList = new ArrayList<BOMStructure>();
			for (StructureObject struc : strucList)
			{
				BOMStructure bomStructure = new BOMStructure(struc.getObjectGuid().getClassName());
				bomStructure.sync(struc);
				bomStructure.resetEnd2ObjectGuid();
				bomStructure.resetViewObjectGuid();
				bomStructure.setEnd1ObjectGuid(item.getObjectGuid());
				bomStructure.put("BOMKey", UUID.randomUUID().toString().replace("-", ""));
				bomStructure.setSequence(String.valueOf(sequence++));
				reList.add(bomStructure);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (ServiceRequestException e)
		{
			throw e;
		}

		return reList;
	}

	private void checkLTableData(ObjectGuid end1ObjectGuid, Date ruleTime, ConfigParameterTableType tableType) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		// La表和Lb表的L番号不能互相重复(除了L00，L00在La表和Lb表都存在)
		Map<String, Object> searchConditionMap = new HashMap<String, Object>();
		searchConditionMap.put("MASTERGUID", end1ObjectGuid.getMasterGuid());
		if (ruleTime != null)
		{
			ruleTime = DateFormat.getDateOfEnd(ruleTime, DateFormat.PTN_YMDHMS);
		}
		searchConditionMap.put("RELEASETIME", ruleTime);
		searchConditionMap.put("TABLETYPE", tableType.name());
		List<TableOfList> lNumberList_ = sds.query(TableOfList.class, searchConditionMap, "listDuplicateLNumber");
		if (!SetUtils.isNullList(lNumberList_))
		{
			String exceptionMsg = null;
			for (TableOfList lData : lNumberList_)
			{
				if (exceptionMsg != null)
				{
					exceptionMsg = exceptionMsg + ",";
				}
				exceptionMsg = StringUtils.convertNULLtoString(exceptionMsg) + lData.getLNumber();
			}

			String laTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LA");
			String lbTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LB");
			throw new ServiceRequestException("ID_APP_CONFIG_LNUMBER_DUPLICATE_SET", "duplicate settings for LNUMBER.", null, laTableTitle, lbTableTitle, exceptionMsg);
		}

		// La表或者Lb表最多只能有一个L00存在
		searchConditionMap.put("ISONLYL00", "Y");
		lNumberList_ = sds.query(TableOfList.class, searchConditionMap, "listDuplicateLNumber");
		if (!SetUtils.isNullList(lNumberList_))
		{
			String exceptionMsg = null;
			for (TableOfList lData : lNumberList_)
			{
				if (exceptionMsg != null)
				{
					exceptionMsg = exceptionMsg + ",";
				}
				exceptionMsg = StringUtils.convertNULLtoString(exceptionMsg) + lData.getLNumber();
			}

			String laTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LA");
			String lbTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LB");
			throw new ServiceRequestException("ID_APP_CONFIG_LNUMBER_DUPLICATE_SET", "duplicate settings for LNUMBER.", null, laTableTitle, lbTableTitle, exceptionMsg);
		}

		// La表和Lb表的图面变量不能重复
		List<DynamicColumnTitle> titles = this.stubService.listColumnTitles(end1ObjectGuid, null, ruleTime);
		if (!SetUtils.isNullList(titles))
		{
			List<String> variableList = new ArrayList<String>();
			for (DynamicColumnTitle title : titles)
			{
				if (!variableList.contains(title.getTitle()))
				{
					variableList.add(title.getTitle());
				}
				else
				{
					String laTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LA");
					String lbTableTitle = this.getMessage("CONFIGPARAMETERTABLETYPE_TABLE_LB");
					throw new ServiceRequestException("ID_APP_CONFIG_VARIABLE_DUPLICATE_SET", "variable is duplicate.", null, laTableTitle, lbTableTitle, title.getTitle());
				}
			}
		}

		this.isDuplicateSN(end1ObjectGuid.getMasterGuid(), ruleTime, tableType);
	}

	protected void deleteAllConfig(ObjectGuid objectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			TableOfGroup tableOfGroup = new TableOfGroup();
			tableOfGroup.setMasterGuid(objectGuid.getMasterGuid());
			sds.delete(TableOfGroup.class, tableOfGroup, "deleteByMaster");
			sds.delete(TableOfGroup.class, tableOfGroup, "deleteCustByMaster");
			sds.delete(TableOfGroup.class, tableOfGroup, "deleteInfoByMaster");

			TableOfList tableOfList = new TableOfList();
			tableOfList.setMasterGuid(objectGuid.getMasterGuid());
			sds.delete(TableOfList.class, tableOfList, "deleteByMaster");

			TableOfRegion tableOfRegion = new TableOfRegion();
			tableOfRegion.setMasterGuid(objectGuid.getMasterGuid());
			tableOfRegion.put("TABLETYPELIST", "'A','B','C','D','E', 'R', 'Q'");
			sds.delete(TableOfRegion.class, tableOfRegion, "deleteByMaster");
			sds.delete(TableOfRegion.class, tableOfRegion, "deleteCustByMaster");
			sds.delete(TableOfRegion.class, tableOfRegion, "deleteCustInfoByMaster");

			TableOfInputVariable tableOfInpt = new TableOfInputVariable();
			tableOfInpt.setMasterGuid(objectGuid.getMasterGuid());
			sds.delete(TableOfInputVariable.class, tableOfInpt, "deleteByMaster");

			TableOfParameter tableOfParameter = new TableOfParameter();
			tableOfParameter.setMasterGuid(objectGuid.getMasterGuid());
			sds.delete(TableOfParameter.class, tableOfParameter, "deleteByMaster");
			sds.delete(TableOfParameter.class, tableOfParameter, "deleteCustByMaster");
			sds.delete(TableOfParameter.class, tableOfParameter, "deleteInfoByMaster");

			TableOfMark tableOfMAK = new TableOfMark();
			tableOfMAK.setMasterGuid(objectGuid.getMasterGuid());
			sds.delete(TableOfMark.class, tableOfMAK, "deleteByMaster");

			TableOfExpression tableOfExpression = new TableOfExpression();
			tableOfExpression.setMasterGuid(objectGuid.getMasterGuid());
			sds.delete(TableOfExpression.class, tableOfExpression, "deleteByMaster");

			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(objectGuid,
					ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
			if (relationTemplate == null)
			{
				throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, null,
						ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
			}

			// 目标对象已有明细结构，删除原有明细结构
			ViewObject destViewObject = this.stubService.getBOAS().getRelationByEND1(objectGuid, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
			if (destViewObject != null)
			{
				this.stubService.deleteRelation(destViewObject.getObjectGuid());
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

	protected String getClassFieldOfG(FoundationObject instance, List<String> fieldNameList, Date ruleTime) throws ServiceRequestException
	{
		// 先取得已有的G番号使用的字段
		if (fieldNameList == null)
		{
			fieldNameList = this.listFieldNameAlreadyUsedByGNumber(instance, ruleTime);
		}

		// 取一个未使用过的G字段
		String fieldName = null;
		for (int i = 1; i <= ConfigParameterConstants.CONFIG_PARAMETER_MAX_G_FIELD_COUNT; i++)
		{
			fieldName = "G" + StringUtils.lpad(String.valueOf(i), 2, '0');
			if (!SetUtils.isNullList(fieldNameList) && fieldNameList.contains(fieldName))
			{
				continue;
			}
			fieldNameList.add(fieldName);
			break;
		}
		return fieldName;
	}

	/**
	 * 取得驱动生成物料的原始图纸
	 * 
	 * @param item
	 * @param dataRule
	 * @return
	 * @throws ServiceRequestException
	 */
	protected FoundationObject getDrawing(ObjectGuid objectGuid, Date ruleTime) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);

		InstanceService ds = DataServer.getInstanceService();
		return ds.queryByTime(objectGuid, ruleTime);
	}

	protected StructureObject link(ViewObject viewObject, FoundationObject end1FoundationObject, ObjectGuid end2FoundationObjectGuid, StructureObject structureObject,
			boolean isCheckACL, String procrtGuid) throws ServiceRequestException
	{
		RelationService ds = DataServer.getRelationService();
		ServiceRequestException returnObj = null;
		try
		{
			if (viewObject == null)
			{
				throw new ServiceRequestException("ID_APP_VEIW_NOT_FOUND", "view not found");
			}

			if ((SystemStatusEnum.OBSOLETE.equals(viewObject.getStatus()) || SystemStatusEnum.PRE.equals(viewObject.getStatus())))
			{
				throw new ServiceRequestException("ID_APP_VEIW_STATUS_NOT_UPDATE", "view not update");
			}

			if (end1FoundationObject != null && end1FoundationObject.getStatus() == SystemStatusEnum.OBSOLETE)
			{
				throw new ServiceRequestException("ID_APP_OBJECT_OBSOLETED", "instace has benn obsoleted", null, end1FoundationObject.getFullName());
			}

			structureObject.setViewObjectGuid(viewObject.getObjectGuid());
			structureObject.setEnd2ObjectGuid(end2FoundationObjectGuid);

			RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateById(viewObject.getTemplateID());
			if (relationTemplate != null)
			{
				if (!relationTemplate.isIncorporatedMaster())
				{
					// 不允许有相同的end2
					String sessionId = this.stubService.getUserSignature().getCredential();
					List<String> end2MasterGuidList = new ArrayList<String>();
					if (!relationTemplate.isIncorporatedMaster())
					{
						List<StructureObject> bomList = DataServer.getRelationService().listSimpleStructureOfRelation(viewObject.getObjectGuid(),
								relationTemplate.getViewClassGuid(), relationTemplate.getStructureClassGuid(), sessionId);
						if (bomList != null)
						{
							for (StructureObject stru : bomList)
							{
								end2MasterGuidList.add(stru.getEnd2ObjectGuid().getMasterGuid());
							}
							if (end2MasterGuidList.contains(structureObject.getEnd2ObjectGuid().getGuid()))
							{
								FoundationObject objectByGuid = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(structureObject.getEnd2ObjectGuid(),
										false);

								throw new ServiceRequestException("ID_APP_BOMEDIT_CANT_CONECT_BOMVIEW", "end2 is not relation", null, objectByGuid.getFullName());
							}
						}
					}

				}
				if (relationTemplate.getMaxQuantity() != 0)
				{
					int maxQuantity = ds.getEnd2CountOfView(viewObject.getObjectGuid().getGuid(), relationTemplate.getGuid());
					if (maxQuantity >= relationTemplate.getMaxQuantity())
					{
						throw new ServiceRequestException("ID_APP_RELATION_TEMPLATE_MAXQUANTITY", "more than max quantity");
					}
				}
			}

			return ((BOASImpl) this.stubService.getBOAS()).getRelationLinkStub().linkInner(viewObject, end1FoundationObject, end2FoundationObjectGuid, structureObject, true, null);

		}
		catch (DynaDataException e)
		{
			returnObj = ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			throw returnObj;
		}
		catch (ServiceRequestException e)
		{
			returnObj = e;
			throw returnObj;
		}
		finally
		{
			Object[] args = new Object[] { viewObject == null ? null : viewObject.getObjectContext(), end2FoundationObjectGuid };
			this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler()
					.addTask(new ScheduledTaskTrackImpl(this.getTrackerBuilder(), this.stubService.getSignature(), null, args, returnObj));
		}
	}

	protected ViewObject getViewObject(ObjectGuid end1ObjectGuid, String viewName, boolean isAutoCreate) throws ServiceRequestException
	{
		ViewObject viewObject = null;
		RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(end1ObjectGuid, viewName);

		if (relationTemplate == null)
		{
			throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE", "no relation template:" + viewName, null, viewName);
		}
		else if (isAutoCreate)
		{
			viewObject = ((BOASImpl) this.stubService.getBOAS()).getRelationStub().saveRelationByTemplate(relationTemplate.getGuid(), end1ObjectGuid, true, null);
		}
		return viewObject;
	}

	private TrackerBuilder getTrackerBuilder()
	{
		if (trackerBuilder == null)
		{
			trackerBuilder = new DefaultTrackerBuilderImpl(this.serviceContext);

			trackerBuilder.setTrackerRendererClass(TRViewLinkImpl.class, TrackedDesc.LINK_RELATION);
			trackerBuilder.setPersistenceClass(TrackerPersistence.class);
		}
		return trackerBuilder;
	}

	public List<TableOfMultiCondition> saveTableOfMultiVariable(ObjectGuid end1ObjectGuid, Date ruleTime, List<TableOfMultiCondition> tableOfMultiConditionList,
			List<TableOfMultiCondition> deleteLineList) throws ServiceRequestException
	{
		FoundationObject instance = this.stubService.getBOAS().getObjectByGuid(end1ObjectGuid);
		if (instance == null)
		{
			throw new ServiceRequestException("ID_DS_NO_DATA", "contract is not exist, guid='" + end1ObjectGuid.getGuid() + "'");
		}
		if (instance.isLatestRevision())
		{
			ruleTime = null;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			List<DynamicOfMultiVariable> deleteList = new ArrayList<DynamicOfMultiVariable>();
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("CREATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
			param.put("UPDATEUSERGUID", this.stubService.getUserSignature().getUserGuid());
			if (!SetUtils.isNullList(tableOfMultiConditionList))
			{
				List<DynamicOfMultiVariable> updateList = new ArrayList<DynamicOfMultiVariable>();
				List<DynamicOfMultiVariable> insertList = new ArrayList<DynamicOfMultiVariable>();
				List<DynamicOfMultiVariable> updateNextList = new ArrayList<DynamicOfMultiVariable>();
				for (int i = 0; i < tableOfMultiConditionList.size(); i++)
				{
					TableOfMultiCondition condition = tableOfMultiConditionList.get(i);
					if (!SetUtils.isNullList(condition.getColumns()))
					{
						String conditions = null;
						if (!SetUtils.isNullList(condition.getConditions()))
						{
							List<TableOfDefineCondition> listConditions = this.clearOtherFields(condition.getConditions());
							conditions = JsonUtils.writeJsonStr(listConditions);
						}
						for (int j = 0; j < condition.getColumns().size(); j++)
						{
							DynamicOfMultiVariable variable = condition.getColumns().get(j);
							variable.setMasterGuid(instance.getObjectGuid().getMasterGuid());
							if (!StringUtils.isNullString(variable.getName()))
							{
								variable.setColSequence(j + "");
								variable.setSequence(i);
								variable.setConditionJson(conditions);
								if (StringUtils.isGuid(variable.getGuid()))
								{
									if (variable.getReleaseTime() == null)
									{
										updateList.add(variable);
									}
									else
									{
										DynamicOfMultiVariable tempVariable = (DynamicOfMultiVariable) variable.clone();
										tempVariable.clear(DynamicOfMultiVariable.RELEASETIME);
										tempVariable.clear(DynamicOfMultiVariable.HASNEXTREVISION);
										tempVariable.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
										insertList.add(tempVariable);
										variable.setHasNextRevision(true);
										updateNextList.add(variable);
									}
								}
								else
								{
									variable.setMasterGuid(end1ObjectGuid.getMasterGuid());
									variable.setGuid(StringUtils.generateRandomUID(32).toUpperCase());
									insertList.add(variable);
								}
							}
							else
							{
								if (StringUtils.isGuid(variable.getGuid()))
								{
									deleteList.add(variable);
								}
							}
						}
					}
				}

				if (!SetUtils.isNullList(insertList))
				{
					param.put("DATALIST", insertList);
					sds.update(DynamicOfMultiVariable.class, param, "inserBatchList");
				}
				if (!SetUtils.isNullList(updateList))
				{
					int size = updateList.size();
					while (size > 0)
					{
						if (size <= 50)
						{
							param.put("DATALIST", updateList);
							sds.update(DynamicOfMultiVariable.class, param, "updateBatchList");
							size = 0;
						}
						else
						{
							List<DynamicOfMultiVariable> temp = new ArrayList<DynamicOfMultiVariable>();
							temp = updateList.subList(0, 50);
							updateList = updateList.subList(50, size);
							size = size - 50;
							param.put("DATALIST", temp);
							sds.update(DynamicOfMultiVariable.class, param, "updateBatchList");
						}
					}
				}
				if (!SetUtils.isNullList(updateNextList))
				{
					StringBuffer sqlBuffer = new StringBuffer();
					if (!SetUtils.isNullList(updateNextList))
					{
						for (Map<String, Object> map : updateNextList)
						{
							if (sqlBuffer.length() > 0)
							{
								sqlBuffer.append(",");
							}
							sqlBuffer.append("'").append((String) map.get("GUID")).append("'");
						}
					}
					param.put("DATALIST", sqlBuffer);
					sds.update(DynamicOfMultiVariable.class, param, "updateNextRevisionBatchList");
				}
			}
			if (!SetUtils.isNullList(deleteLineList))
			{
				for (TableOfMultiCondition dd : deleteLineList)
				{
					List<DynamicOfMultiVariable> listcolumns = dd.getColumns();
					if (!SetUtils.isNullList(listcolumns))
					{
						for (DynamicOfMultiVariable vv : listcolumns)
						{
							if (StringUtils.isGuid(vv.getGuid()))
							{
								vv.setMasterGuid(instance.getObjectGuid().getMasterGuid());
								deleteList.add(vv);
							}
						}
					}
				}
			}
			if (!SetUtils.isNullList(deleteList))
			{
				param.put("DATALIST", deleteList);
				sds.update(DynamicOfMultiVariable.class, param, "deleteLine");
			}
//			DataServer.getTransactionManager().commitTransaction();

			return null;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			this.rebuildExceptionMsg(ConfigParameterTableType.M, e);
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

	private List<TableOfDefineCondition> clearOtherFields(List<TableOfDefineCondition> conditions)
	{
		List<TableOfDefineCondition> value = new ArrayList<TableOfDefineCondition>();
		for (TableOfDefineCondition defineCondition : conditions)
		{
			if (!StringUtils.isNullString(defineCondition.getDefinitionName()))
			{
				TableOfDefineCondition cc = new TableOfDefineCondition();
				cc.setDefinitionName(defineCondition.getDefinitionName());
				cc.setDefinitionValue(defineCondition.getDefinitionValue());
				cc.setRelationCondition(defineCondition.getRelationCondition());
				value.add(cc);
			}
		}
		return value;
	}

	protected void deleteConfigByParam(ObjectGuid objectGuid, List<ConfigParameterTableType> tableTypes) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			if (!SetUtils.isNullList(tableTypes))
			{
				for (ConfigParameterTableType tableType : tableTypes)
				{
					if (ConfigParameterTableType.G.equals(tableType))
					{
						TableOfGroup tableOfGroup = new TableOfGroup();
						tableOfGroup.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(TableOfGroup.class, tableOfGroup, "deleteByMaster");
						sds.delete(TableOfGroup.class, tableOfGroup, "deleteCustByMaster");
						sds.delete(TableOfGroup.class, tableOfGroup, "deleteInfoByMaster");
					}
					else if (ConfigParameterTableType.L.equals(tableType))
					{
						TableOfList tableOfList = new TableOfList();
						tableOfList.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(TableOfList.class, tableOfList, "deleteByMaster");
					}
					else if (ConfigParameterTableType.A_E.equals(tableType))
					{
						TableOfRegion tableOfRegion = new TableOfRegion();
						tableOfRegion.setMasterGuid(objectGuid.getMasterGuid());
						tableOfRegion.put("TABLETYPELIST", "'A','B','C','D','E'");
						sds.delete(TableOfRegion.class, tableOfRegion, "deleteByMaster");
						sds.delete(TableOfRegion.class, tableOfRegion, "deleteCustByMaster");
						sds.delete(TableOfRegion.class, tableOfRegion, "deleteCustInfoByMaster");
					}
					else if (ConfigParameterTableType.R_Q.equals(tableType))
					{
						TableOfRegion tableOfRegion = new TableOfRegion();
						tableOfRegion.setMasterGuid(objectGuid.getMasterGuid());
						tableOfRegion.put("TABLETYPELIST", "'R','Q'");
						sds.delete(TableOfRegion.class, tableOfRegion, "deleteByMaster");
						sds.delete(TableOfRegion.class, tableOfRegion, "deleteCustByMaster");
						sds.delete(TableOfRegion.class, tableOfRegion, "deleteCustInfoByMaster");
					}
					else if (ConfigParameterTableType.F.equals(tableType))
					{
						TableOfExpression tableOfExpression = new TableOfExpression();
						tableOfExpression.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(TableOfExpression.class, tableOfExpression, "deleteByMaster");
					}
					else if (ConfigParameterTableType.P.equals(tableType))
					{
						TableOfParameter tableOfParameter = new TableOfParameter();
						tableOfParameter.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(TableOfParameter.class, tableOfParameter, "deleteByMaster");
						sds.delete(TableOfParameter.class, tableOfParameter, "deleteCustByMaster");
						sds.delete(TableOfParameter.class, tableOfParameter, "deleteInfoByMaster");
					}
					else if (ConfigParameterTableType.MAK.equals(tableType))
					{
						TableOfMark tableOfMAK = new TableOfMark();
						tableOfMAK.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(TableOfMark.class, tableOfMAK, "deleteByMaster");
					}
					else if (ConfigParameterTableType.INPT.equals(tableType))
					{
						TableOfInputVariable tableOfInpt = new TableOfInputVariable();
						tableOfInpt.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(TableOfInputVariable.class, tableOfInpt, "deleteByMaster");
					}
					else if (ConfigParameterTableType.M.equals(tableType))
					{
						TableOfMultiCondition tableOfMultiCondition = new TableOfMultiCondition();
						tableOfMultiCondition.setMasterGuid(objectGuid.getMasterGuid());
						sds.delete(DynamicOfMultiVariable.class, tableOfMultiCondition, "deleteByMaster");
					}
					else if (ConfigParameterTableType.DETAIL.equals(tableType))
					{
						RelationTemplateInfo relationTemplate = this.stubService.getEMM().getRelationTemplateByName(objectGuid,
								ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
						if (relationTemplate == null)
						{
							throw new ServiceRequestException("ID_APP_NO_RELATION_TEMPLATE",
									"no relation template:" + ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME, null,
									ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
						}
						// 目标对象已有明细结构，删除原有明细结构
						ViewObject destViewObject = this.stubService.getBOAS().getRelationByEND1(objectGuid, ConfigParameterConstants.CONFIG_PARAMETER_RELATION_TEMPLATE_NAME);
						if (destViewObject != null)
						{
							this.stubService.deleteRelation(destViewObject.getObjectGuid());
						}
					}
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

}