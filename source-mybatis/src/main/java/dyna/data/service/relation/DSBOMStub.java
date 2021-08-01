/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理BOM
 * JiangHL 2011-5-10
 */
package dyna.data.service.relation;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.dto.Session;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.SystemDataService;
import dyna.dbcommon.exception.DynaDataExceptionAll;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理BOM
 * 
 * @author JiangHL
 */
public class DSBOMStub extends DSAbstractServiceStub<RelationServiceImpl>
{
	protected DSBOMStub(DataServerContext context, RelationServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 精确非精确转换
	 * 
	 * @param bomView
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	protected void convertPrecise(BOMView bomView, String bomTemplateGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String returnValue = this.convertPrecise(bomView, bomTemplateGuid, bomView.isPrecise(), session.getUserGuid(), sessionId);

		if (!StringUtils.isNullString(returnValue) && returnValue.equals("rls"))
		{
			throw new DynaDataExceptionAll("convertPrecise error : status is in RLS、OBS or checkoutuser is not self . ", null, DataExceptionEnum.DS_CHANGEPRECISE_ERROR);
		}
	}

	public List<FoundationObject> listEnd2OfView(ObjectGuid viewObjectGuid, ObjectGuid end2ObjectGuid, String bomTemplateGuid) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = DataServer.getRelationService().getBOMTemplateInfo(bomTemplateGuid);
		Map<String, Object> filter = new HashMap<>();
		filter.put("END2MASTERGUID", end2ObjectGuid.getMasterGuid());
		filter.put("VIEWFK", viewObjectGuid.getGuid());
		filter.put("VIEWTABLE", DataServer.getDSCommonService().getTableName(viewObjectGuid.getClassGuid()));
		filter.put("STRUCTABLE", DataServer.getDSCommonService().getTableName(bomTemplate.getStructureClassGuid()));
		return DataServer.getSystemDataService().query(FoundationObject.class, filter, "listSameEnd2MasterInBOM");
	}

	@SuppressWarnings("unchecked")
	protected String convertPrecise(BOMView bomView, String bomTemplateGuid, boolean isPrecise, String userGuid, String sessionId) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = DataServer.getRelationService().getBOMTemplateInfo(bomTemplateGuid);
		String tableName = DataServer.getDSCommonService().getTableName(bomView.getObjectGuid().getClassGuid());

		Map<String, Object> param = new HashMap<>();
		param.put("fieldlist", "GUID,STATUS,CHECKOUTUSER");
		param.put("table", tableName);
		param.put("GUID", bomView.getObjectGuid().getGuid());
		try
		{
			Map<String, Object> result = (Map<String, Object>) this.dynaObjectMapper.select(param);
			if (!SetUtils.isNullMap(result))
			{
				String status = (String) result.get("STATUS");
				if ("RLS".equals(status) || "OBS".equals(status))
				{
					return "rls";
				}

				String checkoutUser = (String) result.get("CHECKOUTUSER");
				if (!userGuid.equals(checkoutUser))
				{
					return "rls";
				}

				boolean isInRunningWorkflow = this.isInRunningWorkflow(bomView.getEnd1ObjectGuid().getGuid());
				if (isInRunningWorkflow)
				{
					return "rls";
				}

				if (isPrecise)
				{
					param.clear();
					param.put("STRUCTABLE", DataServer.getDSCommonService().getTableName(bomTemplate.getStructureClassGuid()));
					param.put("VIEWGUID", bomView.getObjectGuid().getGuid());
					// 所有物料类的数据必须放在一个表中
					param.put("END2TABLE", DataServer.getDSCommonService().getTableName(bomView.getObjectGuid().getClassGuid()));
					this.bomObjectMapper.setFixedRevisionEnd2(param);
				}
				else
				{
					String structureTable = DataServer.getDSCommonService().getTableName(bomTemplate.getStructureClassGuid());
					this.bomObjectMapper.setLatestEnd2(structureTable,bomView.getObjectGuid().getGuid());
				}

				return "";
			}
			return "rls";
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query error, tableName is " + tableName, e, DataExceptionEnum.DS_UPDATE_DATA_EXCEPTION);
		}
	}

	/**
	 * 更新UHasBOM标记
	 *
	 * @param end1Object
	 * @param bomTemplate
	 * @param exceptionParameter
	 * @param fixTranId
	 * @throws ServiceRequestException
	 */
	public void updateUHasBOM(ObjectGuid end1Object, String bomTemplateGuid, String exceptionParameter, String fixTranId) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = DataServer.getRelationService().getBOMTemplateInfo(bomTemplateGuid);
		SystemDataService sds = DataServer.getSystemDataService();
		CodeItem yCodeItem = DataServer.getCodeModelService().getCodeItem("UHasBOM", "Y");
		CodeItem dCodeItem = DataServer.getCodeModelService().getCodeItem("UHasBOM", "D");

		Map<String, Object> param = new HashMap<>();
		param.put("FIELDS", "md_id id$, uhasbom");
		param.put("TABLENAME", DataServer.getDSCommonService().getTableName(end1Object.getClassGuid()));
		param.put("WHERESQL", "guid='" + end1Object.getGuid() + "'");
		FoundationObject foundationObject = sds.queryObject(FoundationObject.class, param, "selectOneShortHalf");
		if (foundationObject != null)
		{
			boolean uhasBOM = this.uhasBOM(foundationObject.getId(), end1Object, bomTemplate);
			if (uhasBOM && !"Y".equals(foundationObject.get("UHasBOM")))
			{
				// 当前有BOM但是BOM标记不为Y，需要更新为Y
				this.updateUHasBOM(foundationObject.getId(), end1Object.getGuid(), yCodeItem.getGuid(), DataServer.getDSCommonService().getTableName(end1Object.getClassGuid()));
			}
			else if (!uhasBOM && "Y".equals(foundationObject.get("UHasBOM")))
			{
				// 当前没有BOM但是BOM标记为Y，需要更新为D
				this.updateUHasBOM(foundationObject.getId(), end1Object.getGuid(), dCodeItem.getGuid(), DataServer.getDSCommonService().getTableName(end1Object.getClassGuid()));
			}
		}
	}

	private void updateUHasBOM(String id, String foundationObjectGuid, String uhasbom, String tableName)
	{
		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("END1TABLENAME", tableName);
			param.put("UHASBOM", uhasbom);
			param.put("GUID", foundationObjectGuid);
			this.bomObjectMapper.updateUHasBOM(param);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("SQLException uhasBOM() Id =" + id, e, DataExceptionEnum.SDS_UPDATE, id);
		}
	}

	// 判断当前对象是否有bom
	private boolean uhasBOM(String id, ObjectGuid end1Object, BOMTemplateInfo bomTemplate)
	{
		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("VIEWTABLENAME", DataServer.getDSCommonService().getTableName(bomTemplate.getViewClassGuid()));
			param.put("STRUCTURETABLENAME", DataServer.getDSCommonService().getTableName(bomTemplate.getStructureClassGuid()));
			param.put("END1GUID", end1Object.getGuid());
			String uhasBOM = (String) this.bomObjectMapper.isEND1HaveBOM(param);
			return "Y".equals(uhasBOM);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("SQLException uhasBOM() Id =" + id, e, DataExceptionEnum.SDS_UPDATE, id);
		}
	}

	private boolean isInRunningWorkflow(String instanceGuid) throws SQLException
	{
//		String result = (String) this.bomObjectMapper.isInRunningWF(instanceGuid);
//		return StringUtils.isNullString(result) ? true : BooleanUtils.getBooleanByYN(result);
		return false;
	}
}
