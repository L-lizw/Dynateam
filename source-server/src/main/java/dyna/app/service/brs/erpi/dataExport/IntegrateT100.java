package dyna.app.service.brs.erpi.dataExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import dyna.app.service.brs.erpi.ERPT100TransferStub;
import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dblink.DBLinkStub;
import dyna.app.service.brs.erpi.dblink.TableRecord;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.bean.erp.ERPFieldMappingTypeEnum;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPT100OperationEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

public class IntegrateT100 extends IntegrateERP<ERPT100OperationEnum>
{
	private final String				master					= "master";
	private final String				detail					= "detail";
	private final String				plugin					= "plugin";
	private final String				RESERVED_DESIGNATORS	= "bmbc010";
	private final String				bmtIndexColumn			= "bmbc009";
	public final String					FORMID_TYPE_BOM			= "1";									// 有抛BOM数据
	public final String					FORMID_TYPE_S			= "2";									// 没有抛BOM，只抛了局部替代
	public final String					FORMID_TYPE_R			= "3";									// 没有抛BOM，只抛了局部取代

	private Map<String, BOMStructure>	end2_StructureMap		= null;
	private List<FoundationObject>		objectList				= new ArrayList<FoundationObject>();

	private int							addCount				= 0;
	private int							updateCount				= 0;

	private DBLinkStub					dbLinkStub;

	public IntegrateT100(ERPTransferStub<ERPT100OperationEnum> stub, Document doc, DBLinkStub dbLinkStub) throws Exception
	{
		super(stub, doc);
		this.xmlPath = "conf/T100conf.xml";
		this.defaultDateFormat = "yyyy/MM/dd";
		this.dbLinkStub = dbLinkStub;
		this.init();
	}

	/**
	 * 根据操作类型传输数据到中间库
	 * 对每个operation操作一次
	 * 
	 * @param operation
	 * @throws ServiceRequestException
	 */
	public void exportDataIntoTempTable(ERPT100OperationEnum operation, int operationIndex) throws ServiceRequestException
	{
		List<TableRecord> listTableRecord = new ArrayList<TableRecord>();
		Iterator<RecordSet> it = this.getDataMap().get(operation.getCategory()).iterator();
		List<RecordSet> subList = new ArrayList<RecordSet>();
		while (it.hasNext())
		{
			RecordSet rs = it.next();
			subList.add(rs);
		}
		if (!SetUtils.isNullList(subList))
		{
			for (RecordSet recordSet : subList)
			{
				Iterator<String> recordIt = recordSet.keySet().iterator();
				while (recordIt.hasNext())
				{
					String tableName = recordIt.next();
					TableRecordData recordData = recordSet.getTableData(tableName, this.stub);
					TableRecord tableRecord = new TableRecord(tableName, recordData);
					tableRecord.setErpFieldMappingList(this.erpFieldMappingContainer.get(tableName));
					this.setDefaultFieldValue(tableName, tableRecord, operationIndex);
					listTableRecord.add(tableRecord);
				}
			}
		}
		this.dbLinkStub.batchInsertData(listTableRecord);
	}

	public void exportLogDataIntoTable(List<ERPT100OperationEnum> operationList) throws ServiceRequestException
	{
		List<TableRecord> listTableRecord = new ArrayList<TableRecord>();
		listTableRecord.add(this.getGeneralTaskData(operationList));
		this.dbLinkStub.batchInsertData(listTableRecord);
	}

	private void setDefaultFieldValue(String tableName, TableRecord tableRecord, int operationIndex)
	{
		String column = null;
		List<ERPFieldMapping> fieldList = this.erpFieldMappingContainer.get(tableName);
		if (!SetUtils.isNullList(fieldList))
		{
			for (ERPFieldMapping field : fieldList)
			{
				if ("seqkey_subcnt".equalsIgnoreCase(field.getERPField()))
				{
					column = "seqkey_subcnt";
					break;
				}
			}
		}
		LinkedList<Map<String, String>> recordDataList = tableRecord.getRecordDataList();
		if (!SetUtils.isNullList(recordDataList))
		{
			for (Map<String, String> recordData : recordDataList)
			{
				if (!StringUtils.isNullString(column))
				{
					recordData.put(column, String.valueOf(operationIndex + 1));
				}
				recordData.put("plm_pk", DateFormat.getUniqueSysDateTimeStamp());
				this.sleep();
			}
		}
	}

	private void sleep()
	{
		try
		{
			Thread.sleep(1);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取任务总表数据
	 * 
	 * @param taskType
	 *            任务类型
	 * @return
	 */
	private TableRecord getGeneralTaskData(List<ERPT100OperationEnum> operationList)
	{
		TableRecord tableRecord = new TableRecord("plm_logm_t");
		LinkedList<Map<String, String>> recordDataList = new LinkedList<Map<String, String>>();
		Map<String, String> taskDataMap = new LinkedHashMap<String, String>();
		taskDataMap.put("logm_pk", DateFormat.getUniqueSysDateTimeStamp()); // 主件
		taskDataMap.put("entid", this.stub.job.getFieldf()); // 集團代碼
		taskDataMap.put("tran_time", DateFormat.getUniqueSysDateTimeStamp()); // PLM資料拋轉時間
		taskDataMap.put("Seqkey", this.stub.job.getFieldh()); // 任务号
		taskDataMap.put("Seqkey_totcnt", String.valueOf(operationList.size()));// 隊列傳輸總筆數
		taskDataMap.put("formid", this.getFormid(operationList));
		for (int i = 1; i <= operationList.size(); i++)
		{
			taskDataMap.put("data_type" + i, this.getOperationKey(operationList.get(i - 1)));
		}
		taskDataMap.put("transfer_type", "1");
		recordDataList.add(taskDataMap);
		tableRecord.setRecordDataList(recordDataList);
		List<ERPFieldMapping> fieldList = new ArrayList<ERPFieldMapping>();
		fieldList.add(this.getFieldMapping("logm_pk", ERPFieldMappingTypeEnum.DATE, DateFormat.PTN_TIMESTAMP));
		fieldList.add(this.getFieldMapping("entid", null, null));
		fieldList.add(this.getFieldMapping("tran_time", ERPFieldMappingTypeEnum.DATE, DateFormat.PTN_TIMESTAMP));
		fieldList.add(this.getFieldMapping("Seqkey", null, null));
		fieldList.add(this.getFieldMapping("Seqkey_totcnt", null, null));
		fieldList.add(this.getFieldMapping("formid", null, null));
		fieldList.add(this.getFieldMapping("data_type1", null, null));
		fieldList.add(this.getFieldMapping("data_type2", null, null));
		fieldList.add(this.getFieldMapping("data_type3", null, null));
		fieldList.add(this.getFieldMapping("data_type4", null, null));
		fieldList.add(this.getFieldMapping("transfer_type", null, null));
		tableRecord.setErpFieldMappingList(fieldList);
		return tableRecord;
	}

	private String getOperationKey(ERPT100OperationEnum operation)
	{
		if (operation == ERPT100OperationEnum.ItemMasterDataCreate)
		{
			return "imaa";
		}
		if (operation == ERPT100OperationEnum.PLMBOMDataCreate || operation == ERPT100OperationEnum.PLMConfigBOMDataCreate)
		{
			return "bmaa";
		}
		if (operation == ERPT100OperationEnum.CreateLocalSubstitute || operation == ERPT100OperationEnum.CreateLocalReplace
				|| operation == ERPT100OperationEnum.CreateGlobalSubtstitute)
		{
			return "bmea";
		}
		if (operation == ERPT100OperationEnum.ItemApprovalDataCreate)
		{
			return "bmif";
		}
		return null;
	}

	private String getFormid(List<ERPT100OperationEnum> operationList)
	{
		ERPParameter parameter = null;
		if (operationList.contains(ERPT100OperationEnum.PLMBOMDataCreate))
		{
			parameter = this.getParameter(ERPT100OperationEnum.PLMBOMDataCreate.getCategory());
		}
		else if (operationList.contains(ERPT100OperationEnum.CreateGlobalSubtstitute))
		{
			parameter = this.getParameter(ERPT100OperationEnum.CreateGlobalSubtstitute.getCategory());
		}
		else if (operationList.contains(ERPT100OperationEnum.CreateLocalReplace))
		{
			parameter = this.getParameter(ERPT100OperationEnum.CreateLocalReplace.getCategory());
		}
		else if (operationList.contains(ERPT100OperationEnum.CreateLocalSubstitute))
		{
			parameter = this.getParameter(ERPT100OperationEnum.CreateLocalSubstitute.getCategory());
		}
		if (parameter != null)
		{
			return parameter.getParamMap().get("formid");
		}
		return null;
	}

	/**
	 * 创建ERPFieldMapping
	 * 
	 * @param erpField
	 * @return
	 */
	private ERPFieldMapping getFieldMapping(String erpField, ERPFieldMappingTypeEnum type, String format)
	{
		ERPFieldMapping mapping = new ERPFieldMapping();
		mapping.setERPField(erpField);
		mapping.setDateFormat(format);
		if (type != null)
		{
			mapping.setDataType(type);
		}
		return mapping;
	}

	@Override
	protected void getRSData(FoundationObject end1FoundationObj, List<BOMStructure> bOMStructureList, List<FoundationObject> end2FoundationObjList, BOMTemplateInfo bomTemplate,
			ERPT100OperationEnum operation) throws ServiceRequestException
	{
		end2_StructureMap = getStructureByEnd2Map(bOMStructureList, end2FoundationObjList);
		super.getRSData(end1FoundationObj, bOMStructureList, end2FoundationObjList, bomTemplate, operation);
	}

	@Override
	public RecordSet getEachLocalS(FoundationObject obj, ERPT100OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			CodeItemInfo code = null;
			BOMStructure structure = end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			if (structure != null)
			{
				String codeGuid = (String) structure.get("PartNumber");
				if (!StringUtils.isNullString(codeGuid) && StringUtils.isGuid(codeGuid))
				{
					code = stub.getStubService().getEMM().getCodeItem(codeGuid);
				}
			}
			String className = obj.getObjectGuid().getClassName();
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_REPLACESUBSTITUTE, className, tableName);
				this.erpFieldMappingContainer.put(tableName, className, fieldList);
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("bmea007"))
					{
						m.put("bmea007", "2");// <!--1.取代/2.替代-->
					}
					if (m.containsKey("acttype") && isEarlierThanNow(obj))
					{
						m.put("acttype", "D");
					}
					if (code != null && m.containsKey("bmea004"))
					{
						m.put("bmea004", StringUtils.convertNULLtoString(code.getCode()));
					}
					if (m.containsKey("seqkey_subcnt"))
					{
						m.put("seqkey_subcnt", String.valueOf(this.stub.currentOpeIdx + 1));
					}
				}
			}
		}
		String key = StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSType)) + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.Scope));
		rs.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER));
		rs.setItemKey(StringUtils.generateMd5(key));
		return rs;
	}

	@Override
	public RecordSet getEachLocalR(FoundationObject obj, ERPT100OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalR(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			CodeItemInfo code = null;
			BOMStructure structure = end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			if (structure != null)
			{
				String codeGuid = (String) structure.get("PartNumber");
				if (!StringUtils.isNullString(codeGuid) && StringUtils.isGuid(codeGuid))
				{
					code = stub.getStubService().getEMM().getCodeItem(codeGuid);
				}
			}
			String className = obj.getObjectGuid().getClassName();
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_REPLACESUBSTITUTE, className, tableName);
				this.erpFieldMappingContainer.put(tableName, className, fieldList);
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("bmea007"))
					{
						m.put("bmea007", "1");// <!--1.取代/2.替代-->
					}
					if (m.containsKey("acttype") && isEarlierThanNow(obj))
					{
						m.put("acttype", "D");
					}
					if (code != null && m.containsKey("bmea004"))
					{
						m.put("bmea004", StringUtils.convertNULLtoString(code.getCode()));
					}
					if (m.containsKey("seqkey_subcnt"))
					{
						m.put("seqkey_subcnt", String.valueOf(this.stub.currentOpeIdx + 1));
					}
				}
			}
		}
		String key = StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSType)) + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.Scope));
		rs.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER));
		rs.setItemKey(StringUtils.generateMd5(key));
		return rs;
	}

	@Override
	public RecordSet getEachGlobalS(FoundationObject obj, ERPT100OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachGlobalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			String className = obj.getObjectGuid().getClassName();
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_REPLACESUBSTITUTE, className, tableName);
				this.erpFieldMappingContainer.put(tableName, className, fieldList);
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("bmea007"))
					{
						m.put("bmea007", "2");// <!--1.取代/2.替代-->
					}
					if (m.containsKey("acttype") && isEarlierThanNow(obj))
					{
						m.put("acttype", "D");
					}
					if (m.containsKey("seqkey_subcnt"))
					{
						m.put("seqkey_subcnt", String.valueOf(this.stub.currentOpeIdx + 1));
					}
				}
			}
		}
		String key = StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSType)) + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.Scope));
		rs.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER));
		rs.setItemKey(StringUtils.generateMd5(key));
		return rs;
	}

	@Override
	protected RecordSet getEachItemData(FoundationObject end1Obj, ERPT100OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachItemData(end1Obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			String className = end1Obj.getObjectGuid().getClassName();
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_ITEM, className, tableName);
				this.erpFieldMappingContainer.put(tableName, className, fieldList);
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("imaal002"))
					{// <!-- zh_tw(繁體) / zh_cn(簡體) / en_us(英文) -->
						m.put("imaal002", ((ERPT100TransferStub) stub).getLang());
					}
					if (m.containsKey("seqkey_subcnt"))
					{
						m.put("seqkey_subcnt", String.valueOf(this.stub.currentOpeIdx + 1));
					}
				}
			}
		}
		return rs;
	}

	@Override
	protected RecordSet getEachBOMData(FoundationObject end1Obj, List<BOMStructure> bomStructureList, BOMStructure parentBOMStructure, ERPT100OperationEnum operation)
			throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(this.bomView, ERP_BOM, operation))
		{
			return null;
		}
		ObjectGuid objGuid = end1Obj.getObjectGuid();
		RecordSet recordSet = new RecordSet(objGuid);
		String tableName = null;
		Map<String, String> map = null;
		if (!SetUtils.isNullList(bomStructureList))
		{
			Map<String, String> tableMap = this.getTables(ERP_BOM);
			if (!SetUtils.isNullMap(tableMap))
			{
				Iterator<String> tableIt = tableMap.keySet().iterator();
				while (tableIt.hasNext())
				{
					String tableId = tableIt.next();
					tableName = tableMap.get(tableId);
					TableRecordData tableRecord = new TableRecordData();
					for (int i = 0; i < bomStructureList.size(); i++)
					{
						// 单头只取一条数据
						if (master.equalsIgnoreCase(tableId) && i > 0)
						{
							break;
						}
						BOMStructure bomStructure = bomStructureList.get(i);
						String className = bomStructureList.get(i).getObjectGuid().getClassName();
						List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_BOM, className, tableName);
						this.erpFieldMappingContainer.put(tableName, className, fieldList);
						map = this.getObjectMapValue(bomStructureList.get(i), fieldList, ERP_BOM, operation);
						if (plugin.equals(tableId) && bomStructure.get("Designators") != null)
						{
							String designator = (String) bomStructure.get("Designators");
							List<String> designatorList = Arrays.asList(designator.split(DELIMITER_DESIGNATOR));
							for (int j = 0; j < designatorList.size(); j++)
							{
								if (!StringUtils.isNullString(designatorList.get(j)))
								{
									Map<String, String> pluginMap = new HashMap<>(map);
									pluginMap.put(this.bmtIndexColumn, String.valueOf(j + 1));
									pluginMap.put(this.RESERVED_DESIGNATORS, designatorList.get(j));
									tableRecord.add(pluginMap);
								}
							}
						}
						else if (master.equalsIgnoreCase(tableId))
						{
							// <!-- 有插件位置則為Y，無則為N，程式判斷 -->
							if (bomStructure.get("Designators") != null)
							{
								map.put("bmat008", "Y");
							}
							tableRecord.add(map);
						}
						else if (detail.equalsIgnoreCase(tableId))
						{
							tableRecord.add(map);
						}
					}
					recordSet.put(tableName, tableRecord);
				}
			}
		}
		// 若料件曾经挂载过BOM，则生成空BOM单头
		else if (this.hasBOM(end1Obj))
		{
			dealEmptyBom(recordSet, end1Obj, operation);
		}
		return recordSet;
	}

	/**
	 * @param object
	 * @param actionSchema
	 * @param totalCount
	 * @param index
	 * @return
	 * @throws Exception
	 */
	@Override
	public Document getCreateDataXML(ERPT100OperationEnum operation, int totalCount, int index) throws Exception
	{
		// 写Parameter部分
		Element paramEle = this.getParamEle(this.getParameter(operation.getCategory()), operation, totalCount, index);

		Element requestContentEle = new Element("RequestContent");
		requestContentEle.addContent(paramEle);

		// 增加Request的根节点
		Element requestEle = new Element("Request");
		requestEle.addContent(requestContentEle);
		return new Document(requestEle);
	}

	/**
	 * @param erpFactory
	 * @param actionSchema
	 * @param totalvalue
	 * @param index
	 * @param jobId
	 * @return
	 */
	private Element getParamEle(ERPParameter parameter, ERPT100OperationEnum operation, int totalCount, int index)
	{
		Element recordEle = new Element("Record").addContent(this.stub.jobId);
		Element paramEle = new Element("Parameter").addContent(recordEle);
		return paramEle;
	}

	@Override
	public String getOperationId(ERPT100OperationEnum operation)
	{
		return operation.getId();
	}

	@Override
	public String getOperationCategory(ERPT100OperationEnum operation)
	{
		return operation.getCategory();
	}

	@Override
	public List<ERPT100OperationEnum> getOperationList(boolean isMergeByCategory) throws ServiceRequestException
	{
		List<String> operations = this.stub.schema.getOperationList();
		List<String> categoryList = new ArrayList<String>();
		List<ERPT100OperationEnum> operationList = new ArrayList<ERPT100OperationEnum>();
		for (int i = 0; i < operations.size(); i++)
		{
			String id = operations.get(i);
			Map<String, String> attrMap = this.stub.getStubService().getOperationAttribute(ERPServerType.ERPT100, id);
			ERPT100OperationEnum e = ERPT100OperationEnum.getById(id);
			e.setCategory(attrMap.get("category"));
			e.setName(attrMap.get("name"));
			if (isMergeByCategory)
			{
				if (!categoryList.contains(e.getCategory()))
				{
					operationList.add(e);
					categoryList.add(e.getCategory());
				}
			}
			else
			{
				operationList.add(e);
			}
		}
		return operationList;
	}

	@Override
	public String getOperationCrossServiceName(ERPT100OperationEnum operation)
	{
		final String methodName = operation.getWs();
		return Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
	}

	@Override
	public String getOperationName(ERPT100OperationEnum operation)
	{
		return operation.getName();
	}

	@Override
	protected String getBOMHeaderTableName()
	{
		return this.getTables(ERP_BOM).get(master);
	}

	@Override
	protected List<ERPFieldMapping> getFieldFromMap(String category, String className, String tableName) throws ServiceRequestException
	{
		if ("M1".equalsIgnoreCase(tableName))// T100中间文件虚拟表M1，其下没有任何字段mapping
		{
			return null;
		}
		return super.getFieldFromMap(category, className, tableName);
	}

	/**
	 * 缓存end2——BOMstructure，然后根据取替代的ComponentItem获得对应的BOMStructure
	 * 
	 * @param bOMStructureList
	 * @param end2FoundationObjList
	 * @return
	 */
	private Map<String, BOMStructure> getStructureByEnd2Map(List<BOMStructure> bOMStructureList, List<FoundationObject> end2FoundationObjList)
	{
		if (!SetUtils.isNullList(bOMStructureList) && !SetUtils.isNullList(end2FoundationObjList))
		{
			Map<String, BOMStructure> result = new HashMap<String, BOMStructure>();
			for (FoundationObject end2 : end2FoundationObjList)
			{
				for (BOMStructure structure : bOMStructureList)
				{
					if (end2.getGuid().equals(structure.getEnd2ObjectGuid().getGuid()))
					{
						result.put(end2.getGuid(), structure);
						break;
					}
				}
			}
			return result;
		}
		return null;
	}

	protected boolean isEarlierThanNow(FoundationObject fo)
	{
		Date invalid = null;
		if (fo != null)
		{
			if (fo.get(ReplaceSubstituteConstants.InvalidDate) != null)
			{
				invalid = (Date) fo.get(ReplaceSubstituteConstants.InvalidDate);
				if (invalid != null && !(DateFormat.compareDate(invalid, new Date()) > 0))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获得传数据的operation数量</br>
	 * compareWithDB()可能使某个operation不传数据，需在compareWithDB后调用
	 * 
	 * @throws ServiceRequestException
	 */
	public void fillOptionListSize() throws ServiceRequestException
	{
		List<ERPT100OperationEnum> operationList = this.getOperationList(true);
		int tempSize = operationList.size();
		for (int k = 0; k < operationList.size(); k++)
		{
			ERPT100OperationEnum operation = operationList.get(k);
			String category = this.getOperationCategory(operation);
			if (StringUtils.isNullString(category))
			{
				throw new IllegalArgumentException("no category attribute for " + operationList.get(k).name() + " in " + this.xmlPath);
			}
			if (ERP_ITEM.equals(category))
			{
				if (this.dataMap.get(ERP_ITEM) == null || this.dataMap.get(ERP_ITEM).isEmpty())
				{
					tempSize = tempSize - 1;
				}
			}
			else if (ERP_BOM.equals(category))
			{
				if (this.dataMap.get(ERP_BOM) == null || this.dataMap.get(ERP_BOM).isEmpty())
				{
					tempSize = tempSize - 1;
				}
			}
			else if (ERP_REPLACESUBSTITUTE.equals(category))
			{
				if (this.dataMap.get(ERP_REPLACESUBSTITUTE) == null || this.dataMap.get(ERP_REPLACESUBSTITUTE).isEmpty())
				{
					tempSize = tempSize - 1;
				}
			}
			else if (ERP_MA.equals(category))
			{
				if (this.dataMap.get(ERP_MA) == null || this.dataMap.get(ERP_MA).isEmpty())
				{
					tempSize = tempSize - 1;
				}
			}
		}
	}

	protected void setActtype(RecordSet set, boolean isAdd)
	{
		Iterator<String> tableIt = set.keySet().iterator();
		while (tableIt.hasNext())
		{
			String tableName = tableIt.next();
			TableRecordData tableRecord = set.get(tableName);
			for (Map<String, String> m : tableRecord)
			{
				if (m.containsKey("acttype"))
				{
					if (!StringUtils.isNullString(m.get("acttype")))
					{
						continue;
					}
					if (isAdd)
					{
						m.put("acttype", "A");
					}
					else
					{
						m.put("acttype", "U");
					}
				}
			}
		}
	}

	@Override
	public List<String> getReturnList(List<FoundationObject> list, List<String> factoryId, ERPServiceConfig serviceConfig, List<String> contentList) throws Exception
	{
		List<String> result = new ArrayList<String>();
		List<String> tableNameList = new ArrayList<String>();
		Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
		this.stub.factory = factoryId.get(0);
		objectList.addAll(list);
		// List<ERPT100OperationEnum> operations = getOperationList(false);
		for (String content : contentList)
		{
			Map<String, String> map = this.stub.getStubService().getERPStub().getContentAttribute(ERPServerType.valueOf(serviceConfig.getERPServerSelected()), content);
			String tablename = map.get("tablename");
			if (!tableNameList.contains(tablename))
			{
				tableNameList.add(tablename);
			}
		}
		paraMap.put("tableName", tableNameList);
		String originalXML = getPriceXML(list, factoryId, paraMap);
		String returnXML = ((ERPT100TransferStub) this.stub).callWS(originalXML, ERPT100OperationEnum.GetSupplyDemandData);
		result.add(returnXML);
		return result;
	}

	@Override
	public List<FoundationObject> getReturnList(List<String> returnXML, List<String> contentList) throws Exception
	{
		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		String xml = returnXML.get(0);
		List<FoundationObject> list = this.getReturnList(xml, contentList);
		List<FoundationObject> notExistList = this.mergeList(this.objectList, list);
		if (!SetUtils.isNullList(notExistList))
		{
			for (FoundationObject obj : notExistList)
			{
				FoundationObject fo = new FoundationObjectImpl();
				fo.put("PlantCode", this.stub.factory);
				fo.put("Flag", BooleanUtils.getBooleanStringYN(false));
				fo.put("FULLNAME$", obj.getFullName());
				resultList.add(fo);
			}
		}
		resultList.addAll(list);
		return resultList;
	}

	@SuppressWarnings("unchecked")
	private List<FoundationObject> getReturnList(String returnXML, List<String> contentList) throws Exception
	{
		List<FoundationObject> list = new ArrayList<FoundationObject>();
		Document doc = XMLUtil.convertString2XML(returnXML);
		Element root = doc.getRootElement();
		Element executionContent = root.getChild("Execution");
		Element statusField = executionContent.getChild("Status");
		String code = statusField.getAttributeValue("code");
		if (!"0".equals(code))
		{
			String errMsg = statusField.getAttributeValue("description");
			throw new Exception(errMsg);
		}

		Element responseContent = root.getChild("ResponseContent");
		Element documentEle = responseContent.getChild("Document");
		List<Element> recordSetList = documentEle.getChildren("RecordSet");
		if (!SetUtils.isNullList(recordSetList))
		{
			for (Element recordSetEle : recordSetList)
			{
				Element master = recordSetEle.getChild("Master");
				String id = master.getAttributeValue("name");
				FoundationObject fo = new FoundationObjectImpl();
				fo.put(SystemClassFieldEnum.ID.getName(), id);
				List<Element> detailList = recordSetEle.getChild("Master").getChild("Record").getChildren("Detail");
				for (Element detail : detailList)
				{
					List<Element> fieldList = detail.getChild("Record").getChildren("Field");
					for (Element field : fieldList)
					{
						String key = field.getAttributeValue("name");
						if (contentList.contains(key))
						{
							fo.put(key, field.getAttributeValue("value"));
						}
					}
				}

				List<FoundationObject> foList = this.stub.getStubService().getERPStub().getItem(id);
				if (SetUtils.isNullList(foList))
				{
					throw new ServiceRequestException("ID_DS_NO_DATA", null);
				}
				fo.put("FULLNAME$", foList.get(0).getFullName());
				fo.put("Flag", BooleanUtils.getBooleanStringYN(true));
				fo.put("PlantCode", this.stub.factory);
				list.add(fo);
			}
		}
		return list;
	}

	private List<FoundationObject> mergeList(List<FoundationObject> origList, List<FoundationObject> destList)
	{
		if (SetUtils.isNullList(origList))
		{
			return new ArrayList<FoundationObject>();
		}

		if (SetUtils.isNullList(destList))
		{
			return origList;
		}

		List<FoundationObject> tmpList = new ArrayList<FoundationObject>();
		for (FoundationObject fo : destList)
		{
			if (!StringUtils.isNullString(fo.getId()))
			{
				tmpList.add(fo);
			}
		}

		List<FoundationObject> resultList = new ArrayList<FoundationObject>();
		for (FoundationObject orig : origList)
		{
			boolean isContains = false;
			for (FoundationObject dest : tmpList)
			{
				if (dest.getId().equals(orig.getId()))
				{
					isContains = true;
					break;
				}
			}
			if (!isContains)
			{
				resultList.add(orig);
			}
		}
		return resultList;
	}

	@Override
	protected String getPriceXML(List<FoundationObject> list, List<String> factoryId, Map<String, List<String>> map) throws Exception
	{
		String[] allTableName = { "imaatmp1", "imaatmp2", "imaatmp3", "imaatmp4", "imaatmp5", "imaatmp6", "imaatmp7", "imai_t", "imae_t", "imaf_t", "imag_t" };
		List<String> nList = new ArrayList<String>();
		nList.addAll(Arrays.asList(allTableName));
		StringBuffer buffer = new StringBuffer();
		String originalXML = null;
		List<String> tableNameList = map.get("tableName");
		buffer.append("<Request>\n");
		buffer.append("<RequestContent>\n");
		buffer.append("<Parameter>\n");
		buffer.append("<Record>\n");
		buffer.append("<Field name=\"condition\" value=\"AND imaa001 in (\'");
		for (int i = 0; i < list.size(); i++)
		{
			FoundationObject object = list.get(i);
			buffer.append(object.getId());
			buffer.append("\'");
			if (i != list.size() - 1)
			{
				buffer.append(",\'");
			}
		}
		buffer.append(")\"/>\n");
		for (int i = 0; i < nList.size(); i++)
		{
			String tableName = nList.get(i);
			buffer.append(" <Field name=\"").append(tableName).append("\" value=\"").append(tableNameList.contains(tableName) ? "Y" : "N").append("\"/>\n");
		}
		buffer.append("</Record>\n");
		buffer.append("</Parameter>\n");
		buffer.append("</RequestContent>\n");
		buffer.append("</Request>\n");
		originalXML = buffer.toString();
		return originalXML;
	}

	public int getUpdateCount()
	{
		return updateCount;
	}

	public int getAddCount()
	{
		return addCount;
	}
}
