package dyna.data.service.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.dtomapper.cpb.IOPColumnTitleMapper;
import dyna.common.dtomapper.configparamter.*;
import dyna.common.dtomapper.cpb.DynamicOfColumnMapper;
import dyna.data.service.relation.RelationServiceImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.bmbo.BusinessObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.Session;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigManagerStub extends DSAbstractServiceStub<ConfigManagerServiceImpl>
{
	@Autowired
	private IOPColumnTitleMapper            iopColumnTitleMapper;
	@Autowired
	private IOPColumnValueMapper            iopColumnValueMapper;
	@Autowired
	private DynamicOfMultiVariableMapper    dynamicOfMultiVariableMapper;
	@Autowired
	private TableOfExpressionMapper         tableOfExpressionMapper;
	@Autowired
	private TableOfGroupMapper              tableOfGroupMapper;
	@Autowired
	private TableOfListMapper               tableOfListMapper;
	@Autowired
	private TableOfMarkMapper               tableOfMarkMapper;
	@Autowired
	private TableOfRegionMapper             tableOfRegionMapper;
	@Autowired
	private TableOfParameterMapper          tableOfParameterMapper;
	@Autowired
	private TableOfInputVariableMapper      tableOfInputVariableMapper;
	@Autowired
	private DynamicColumnTitleMapper        dynamicColumnTitleMapper;
	@Autowired
	private DynamicOfColumnMapper           dynamicOfColumnMapper;

	protected ConfigManagerStub(DataServerContext context, ConfigManagerServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 当订单合同改变时,需要刷新订单明细的所属订单合同字段
	 *
	 * @param contractObjectGuid
	 * @param relationTemplateGuid
	 * @param sessionId
	 * @throws DynaDataException
	 */
	protected void changeOwnerContractOfContent(ObjectGuid contractObjectGuid, String relationTemplateGuid, String sessionId) throws ServiceRequestException
	{
		RelationTemplate template = ((RelationServiceImpl) DataServer.getRelationService()).getRelationTemplateStub().getRelationTemplate(relationTemplateGuid);
		String viewTable = DataServer.getDSCommonService().getTableName(template.getViewClassGuid());
		String strucTable = DataServer.getDSCommonService().getTableName(template.getStructureClassGuid());
		List<RelationTemplateEnd2> templateEnd2List = template.getRelationTemplateEnd2List();
		if (SetUtils.isNullList(templateEnd2List))
		{
			return;
		}

		String bmName = DataServer.getDSCommonService().getSession(sessionId).getBizModelName();

		List<String> end2TableList = new ArrayList<>();
		for (RelationTemplateEnd2 templateEnd2 : templateEnd2List)
		{
			String end2BoName = templateEnd2.getEnd2BoName();
			BusinessObject bo = DataServer.getBusinessModelService().getBusinessObjectByName(bmName, end2BoName);
			String end2ClassGuid = null;
			if (bo == null)
			{
				ClassObject classObject = DataServer.getClassModelService().getClassObject(end2BoName);
				if (classObject != null)
				{
					end2ClassGuid = classObject.getGuid();
				}
			}
			else
			{
				end2ClassGuid = bo.getClassGuid();
			}
			if (StringUtils.isGuid(end2ClassGuid))
			{
				continue;
			}

			String end2Table = DataServer.getDSCommonService().getTableName(end2ClassGuid);
			if (!StringUtils.isNullString(end2Table) && !end2TableList.contains(end2Table))
			{
				end2TableList.add(end2Table);
			}
		}

		Map<String, Object> param = new HashMap<>();
		param.put("VIEWTABLE", viewTable);
		param.put("STRUCTABLE", strucTable);
		param.put("TEMPLATEID", template.getId());
		param.put("OWNERCONTRACT", contractObjectGuid.getGuid());
		param.put("OWNERCONTRACTMASTER", contractObjectGuid.getMasterGuid());
		param.put("OWNERCONTRACTCLASS", contractObjectGuid.getClassGuid());

		try
		{
			if (!SetUtils.isNullList(end2TableList))
			{
				for (String end2Table : end2TableList)
				{
					param.put("END2TABLE", end2Table);
					this.relationobjectMapper.updateOwnerContract(param);
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("changeOwnerContractOfContent error.", e, DataExceptionEnum.DS_SAVE_FOUNDATION);
		}
	}

	protected void clearItemOfContent(ObjectGuid contractObjectGuid, ObjectGuid itemObjectGuid, String relationTemplateGuid, String sessionId) throws ServiceRequestException
	{
		RelationTemplate template = ((RelationServiceImpl) DataServer.getRelationService()).getRelationTemplateStub().getRelationTemplate(relationTemplateGuid);
		List<RelationTemplateEnd2> templateEnd2List = template.getRelationTemplateEnd2List();
		if (SetUtils.isNullList(templateEnd2List))
		{
			return;
		}

		String bmName = DataServer.getDSCommonService().getSession(sessionId).getBizModelName();

		List<String> end2TableList = new ArrayList<>();
		for (RelationTemplateEnd2 templateEnd2 : templateEnd2List)
		{
			String end2BoName = templateEnd2.getEnd2BoName();
			BusinessObject bo = DataServer.getBusinessModelService().getBusinessObjectByName(bmName, end2BoName);
			String end2ClassGuid = null;
			if (bo == null)
			{
				ClassObject classObject = DataServer.getClassModelService().getClassObject(end2BoName);
				if (classObject != null)
				{
					end2ClassGuid = classObject.getGuid();
				}
			}
			else
			{
				end2ClassGuid = bo.getClassGuid();
			}
			if (!StringUtils.isGuid(end2ClassGuid))
			{
				continue;
			}

			String end2Table = DataServer.getDSCommonService().getTableName(end2ClassGuid);
			if (!StringUtils.isNullString(end2Table) && !end2TableList.contains(end2Table))
			{
				end2TableList.add(end2Table);
			}
		}

		Map<String, Object> param = new HashMap<>();
		param.put("OWNERCONTRACT", contractObjectGuid.getGuid());
		param.put("ORDERITEM", itemObjectGuid.getGuid());

		try
		{
			if (!SetUtils.isNullList(end2TableList))
			{
				for (String end2Table : end2TableList)
				{
					param.put("END2TABLE", end2Table);
					this.relationobjectMapper.clearItemOfContent(param);
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("clearItemOfContent error.", e, DataExceptionEnum.DS_SAVE_FOUNDATION);
		}
	}

	protected void releaseConfigTable(String masterGuid, String foundationId, ModelInterfaceEnum interfaceEnum, String sessionId) throws ServiceRequestException
	{
		try
		{
			Session session = DataServer.getDSCommonService().getSession(sessionId);
			String userGuid = session.getUserGuid();

			if (ModelInterfaceEnum.IManufacturingRule.equals(interfaceEnum))
			{
				this.releaseConfigTableBy("MA_CONFIG_TABLE_INFO", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_L_GROUP", masterGuid, userGuid);
				this.releaseConfigTableCustColumnBy("MA_CONFIG_TABLE_CUSTM_L", "MA_CONFIG_TABLE_L_GROUP", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_G", masterGuid, userGuid);
				this.releaseConfigTableCustColumnBy("MA_CONFIG_TABLE_CUSTM_G", "MA_CONFIG_TABLE_G", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_F", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_AERQ", masterGuid, userGuid);
				this.releaseConfigTableCustColumnBy("MA_CONFIG_TABLE_CUSTM_AERQ", "MA_CONFIG_TABLE_AERQ", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_P", masterGuid, userGuid);
				this.releaseConfigTableCustColumnBy("MA_CONFIG_TABLE_CUSTM_P", "MA_CONFIG_TABLE_P", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_MAK", masterGuid, userGuid);
				this.releaseConfigTableBy("MA_CONFIG_TABLE_INPUTVARIABLE", masterGuid, userGuid);
				this.releaseTableOfM(masterGuid, userGuid);
			}
			else if (ModelInterfaceEnum.IOption.equals(interfaceEnum))
			{
				this.releaseIOPConfigTable(masterGuid, userGuid);
			}
		}
		catch (SQLException e)
		{
			String exceptionMsg = "releaseConfigTable() Id =" + foundationId;
			if (foundationId == null)
			{
				exceptionMsg = "releaseConfigTable() masterGuid =" + masterGuid;
			}
			throw new DynaDataExceptionSQL(exceptionMsg, e, DataExceptionEnum.DS_RELEASE);
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("releaseConfigTable() Id =" + foundationId, e, DataExceptionEnum.DS_RELEASE);
		}
	}

	private void releaseIOPConfigTable(String masterGuid, String userGuid) throws SQLException
	{
		Map<String, Object> param = new HashMap<>();
		param.put("CURRENTTIME", DateFormat.getSysDate());
		param.put("MASTERGUID", masterGuid);
		param.put("UPDATEUSERGUID", userGuid);

		// 所有已发布但是下一个版本数据为Y，且有效的数据失效
		this.iopColumnTitleMapper.obsolete(param);
		// 所有未发布的数据发布
		this.iopColumnTitleMapper.release(param);

		// 所有已发布但是下一个版本数据为Y，且有效的数据失效
		this.iopColumnValueMapper.obsoleteCustColumn(param);
		// 所有未发布的数据发布
		this.iopColumnValueMapper.releaseCustColumn(param);

	}

	private void releaseTableOfM(String masterGuid, String userGuid) throws SQLException
	{
		Map<String, Object> param = new HashMap<>();
		param.put("CURRENTTIME", DateFormat.getSysDate());
		param.put("MASTERGUID", masterGuid);
		param.put("UPDATEUSERGUID", userGuid);

		// 所有已发布但是下一个版本数据为Y，且有效的数据失效
		this.dynamicOfMultiVariableMapper.obsoleteCustColumn(param);
		// 所有未发布的数据发布
		this.dynamicOfMultiVariableMapper.releaseCustColumn(param);

	}

	protected void deleteConfigTableData(String masterGuid, boolean isMaster, ModelInterfaceEnum interfaceEnum, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		Map<String, Object> param = new HashMap<>();
		param.put("MASTERGUID", masterGuid);
		param.put("UPDATEUSERGUID", session.getUserGuid());
		param.put("CURRENTTIME", DateFormat.getSysDate());
		try
		{
			if (isMaster)
			{
				if (ModelInterfaceEnum.IManufacturingRule.equals(interfaceEnum))
				{
					this.tableOfExpressionMapper.deleteByMaster(masterGuid);

					this.tableOfGroupMapper.deleteByMaster(masterGuid);
					this.tableOfGroupMapper.deleteCustByMaster(masterGuid);

					this.tableOfInputVariableMapper.deleteByMaster(masterGuid);

					this.tableOfListMapper.deleteByMaster(masterGuid);
					this.tableOfListMapper.deleteCustByMaster(masterGuid);

					this.tableOfMarkMapper.deleteByMaster(masterGuid);

					this.tableOfParameterMapper.deleteByMaster(masterGuid);
					this.tableOfParameterMapper.deleteCustByMaster(masterGuid);

					this.tableOfRegionMapper.deleteByMaster(masterGuid,null);
					this.tableOfRegionMapper.deleteCustByMaster(masterGuid,null);

					this.dynamicColumnTitleMapper.deleteByMaster(masterGuid);

					this.dynamicOfMultiVariableMapper.deleteByMaster(masterGuid);
				}
				else if (ModelInterfaceEnum.IOption.equals(interfaceEnum))
				{
					this.iopColumnValueMapper.deleteByMaster(masterGuid);
					this.iopColumnTitleMapper.deleteByMaster(masterGuid);
				}
			}
			else
			{
				if (ModelInterfaceEnum.IManufacturingRule.equals(interfaceEnum))
				{
					this.tableOfExpressionMapper.deleteWIP(masterGuid);

					this.tableOfGroupMapper.deleteWIP(masterGuid);
					this.tableOfGroupMapper.deleteCustWIP(masterGuid);

					this.tableOfInputVariableMapper.deleteWIP(masterGuid);

					this.tableOfListMapper.deleteWIP(masterGuid);
					this.tableOfListMapper.deleteCustWIP(masterGuid);

					this.tableOfMarkMapper.deleteWIP(masterGuid);

					this.tableOfParameterMapper.deleteWIP(masterGuid);
					this.tableOfParameterMapper.deleteCustWIP(masterGuid);

					this.tableOfRegionMapper.deleteWIP(masterGuid);
					this.tableOfRegionMapper.deleteCustWIP(masterGuid);

					this.dynamicColumnTitleMapper.deleteWIP(masterGuid);
					this.dynamicOfMultiVariableMapper.deleteWIP(masterGuid);

					this.tableOfExpressionMapper.clearWIP(param);

					this.tableOfGroupMapper.clearWIP(param);
					this.tableOfGroupMapper.clearCustWIP(param);

					this.tableOfInputVariableMapper.clearWIP(param);

					this.tableOfListMapper.clearWIP(param);
					this.tableOfListMapper.clearCustWIP(param);

					this.tableOfMarkMapper.clearWIP(param);

					this.tableOfParameterMapper.clearWIP(param);
					this.tableOfParameterMapper.clearCustWIP(param);

					this.tableOfRegionMapper.clearWIP(param);
					this.tableOfRegionMapper.clearCustWIP(param);

					this.dynamicColumnTitleMapper.clearWIP(param);
					this.dynamicOfMultiVariableMapper.clearWIP(param);
				}
				else if (ModelInterfaceEnum.IOption.equals(interfaceEnum))
				{
					this.iopColumnValueMapper.deleteWIP(masterGuid);
					this.iopColumnTitleMapper.deleteWIP(masterGuid);

					this.iopColumnValueMapper.clearWIP(param);
					this.iopColumnTitleMapper.clearWIP(param);

				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteConfigTableData() masterGuid =" + masterGuid, e, DataExceptionEnum.DS_RELEASE);
		}
	}

	private void releaseConfigTableBy(String tableName, String masterGuid, String userGuid) throws SQLException
	{
		Map<String, Object> param = new HashMap<>();
		param.put("TABLENAME", tableName);
		param.put("CURRENTTIME", DateFormat.getSysDate());
		param.put("MASTERGUID", masterGuid);
		param.put("UPDATEUSERGUID", userGuid);

		// 所有已发布但是下一个版本数据为Y，且有效的数据失效
		this.dynamicColumnTitleMapper.obsolete(param);
		// 所有未发布的数据发布
		this.dynamicColumnTitleMapper.release(param);
	}

	private void releaseConfigTableCustColumnBy(String tableName, String masterTableName, String masterGuid, String userGuid) throws SQLException
	{
		Map<String, Object> param = new HashMap<>();
		param.put("TABLENAME", tableName);
		param.put("MASTERTABLENAME", masterTableName);
		param.put("CURRENTTIME", DateFormat.getSysDate());
		param.put("MASTERGUID", masterGuid);
		param.put("UPDATEUSERGUID", userGuid);

		// 所有已发布但是下一个版本数据为Y，且有效的数据失效
		this.dynamicOfColumnMapper.obsoleteCustColumn(param);
		// 所有未发布的数据发布
		this.dynamicOfColumnMapper.releaseCustColumn(param);
	}
}
