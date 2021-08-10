package dyna.app.service.brs.erpi.dataExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import dyna.app.service.brs.erpi.ERPT100TransferStub;
import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPFieldMapping;
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

public class IntegrateT100Old extends IntegrateERP<ERPT100OperationEnum>
{
	private final String				master					= "master";
	private final String				detail					= "detail";
	private final String				plugin					= "plugin";
	private final String				RESERVED_DESIGNATORS	= "bmbc010";
	private final String				bmtIndexColumn			= "bmbc009";
	private Integer						optionListSize			= 0;
	private Map<String, BOMStructure>	end2_StructureMap		= null;
	private List<FoundationObject>		objectList				= new ArrayList<FoundationObject>();

	public IntegrateT100Old(ERPTransferStub<ERPT100OperationEnum> stub, Document doc) throws Exception
	{
		super(stub, doc);
		this.xmlPath = "conf/T100conf.xml";
		this.defaultDateFormat = "yyyy/MM/dd";
		this.init();
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
			// add2StampMap(ERP_REPLACESUBSTITUTE, obj);
			BOMStructure structure = end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			if (structure != null)
			{
				String codeGuid = (String) structure.get("PartNumber");
				if (!StringUtils.isNullString(codeGuid) && StringUtils.isGuid(codeGuid))
				{
					code = stub.getStubService().getEMM().getCodeItem(codeGuid);
				}
			}
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
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
			// add2StampMap(ERP_REPLACESUBSTITUTE, obj);
			BOMStructure structure = end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			if (structure != null)
			{
				String codeGuid = (String) structure.get("PartNumber");
				if (!StringUtils.isNullString(codeGuid) && StringUtils.isGuid(codeGuid))
				{
					code = stub.getStubService().getEMM().getCodeItem(codeGuid);
				}
			}
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
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
			// CodeItemInfo code = null;
			// add2StampMap(ERP_REPLACESUBSTITUTE, obj);
			// BOMStructure structure =
			// end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			// if (structure != null)
			// {
			// String codeGuid = (String) structure.get("PartNumber");
			// if (!StringUtils.isNullString(codeGuid) && StringUtils.isGuid(codeGuid))
			// {
			// code = stub.getStubService().getEMM().getCodeItem(codeGuid);
			// }
			// }
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
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
					// if (code != null && m.containsKey("bmea004"))
					// {
					// m.put("bmea004", StringUtils.convertNULLtoString(code.getCode()));
					// }
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
			// add2StampMap(ERP_ITEM, end1Obj);
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("imaal002"))
					{// <!-- zh_tw(繁體) / zh_cn(簡體) / en_us(英文) -->
						m.put("imaal002", ((ERPT100TransferStub) stub).getLang());
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
		// bomStructureList.isEmpty() ? this.getMockObjectGuid(end1Obj) : this.bomView.getObjectGuid();
		RecordSet recordSet = new RecordSet(objGuid);
		String tableName = null;
		Map<String, String> map = null;
		if (!bomStructureList.isEmpty())
		{
			Iterator<String> tableIt = this.getTables(ERP_BOM).values().iterator();
			while (tableIt.hasNext())
			{
				tableName = tableIt.next();
				TableRecordData tableRecord = new TableRecordData();
				for (int i = 0; i < bomStructureList.size(); i++)
				{
					List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_BOM, bomStructureList.get(i).getObjectGuid().getClassName(), tableName);
					map = this.getObjectMapValue(bomStructureList.get(i), fieldList, ERP_BOM, operation);
					if ("bmba_t".equalsIgnoreCase(tableName) && bomStructureList.get(i).get("Designators") != null)
					{// <!-- 有插件位置則為Y，無則為N，程式判斷 -->
						map.put("bmba018", "Y");
					}
					tableRecord.add(map);
				}
				recordSet.put(tableName, tableRecord);
			}
		}
		else if (this.hasBOM(end1Obj))
		{
			dealEmptyBom(recordSet, end1Obj, operation);
		}
		return recordSet;
	}

	/**
	 * @return
	 */
	private Element getDocEle(List<RecordSet> valueList, String category, int totalCount, int index)
	{
		Map<String, String> tableMap = this.getTables(category);
		String headerTable = tableMap.get(this.master);
		String bodyTable = tableMap.get(this.detail);
		String pluginTable = tableMap.get(this.plugin);
		Element docEle = new Element("Document");

		if (!"M1".equalsIgnoreCase(headerTable))
		{
			docEle = getDocEle(valueList, docEle, totalCount, index, headerTable, bodyTable, pluginTable);
		}
		else
		{
			docEle = getDocEleM1(valueList, docEle, totalCount, index, headerTable, bodyTable, pluginTable);
		}

		valueList.clear();
		return docEle;
	}

	/**
	 * 
	 * @param valueList
	 * @param docEle
	 * @param totalCount
	 * @param index
	 * @param headerTable
	 *            虚拟Table M1
	 * @param bodyTable
	 * @param pluginTable
	 * @return
	 */
	private Element getDocEleM1(List<RecordSet> valueList, Element docEle, int totalCount, int index, String headerTable, String bodyTable, String pluginTable)
	{
		Element bodyEle = null;
		Element pluginEle = null;
		Iterator<RecordSet> it = valueList.iterator();
		RecordSet data = null;
		Element recordSetEle = new Element("RecordSet").setAttribute("id", String.valueOf(1));
		docEle.addContent(recordSetEle);
		Element masterEle = new Element("Master").setAttribute("name", "M1");
		recordSetEle.addContent(masterEle);
		Element recordEle = new Element("Record");
		masterEle.addContent(recordEle);
		if (!StringUtils.isNullString(bodyTable))
		{
			bodyEle = new Element("Detail").setAttribute("name", bodyTable);
			recordEle.addContent(bodyEle);
		}
		if (!StringUtils.isNullString(pluginTable))
		{
			pluginEle = new Element("Detail").setAttribute("name", pluginTable);
			recordEle.addContent(pluginEle);
		}
		while (it.hasNext())
		{
			data = it.next();
			if (!StringUtils.isNullString(headerTable))
			{
				if (!StringUtils.isNullString(bodyTable))
				{
					TableRecordData bodyData = data.getTableData(bodyTable, this.stub);
					if (bodyData != null)
					{
						for (int j = 0; j < bodyData.size(); j++)
						{
							Element recordEle_body = new Element("Record");
							bodyEle.addContent(recordEle_body);
							for (String field : bodyData.get(j).keySet())
							{
								recordEle_body.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", bodyData.get(j).get(field)));
							}
						}
					}
				}
			}
		}
		return docEle;

	}

	/**
	 * 正常格式
	 * 
	 * @param valueList
	 * @param docEle
	 * @param totalCount
	 * @param index
	 * @param headerTable
	 * @param bodyTable
	 * @param pluginTable
	 * @return
	 */
	private Element getDocEle(List<RecordSet> valueList, Element docEle, int totalCount, int index, String headerTable, String bodyTable, String pluginTable)
	{
		Iterator<RecordSet> it = valueList.iterator();
		RecordSet data = null;
		int i = 0;
		while (it.hasNext())
		{
			data = it.next();
			i++;
			Element recordSetEle = new Element("RecordSet").setAttribute("id", String.valueOf(i));
			docEle.addContent(recordSetEle);
			if (!StringUtils.isNullString(headerTable))
			{
				Element masterEle = new Element("Master").setAttribute("name", headerTable);
				recordSetEle.addContent(masterEle);
				TableRecordData headData = data.getTableData(headerTable, this.stub);
				if (headData != null)
				{
					for (int x = 0; x < headData.size(); x++)
					{
						Element recordEle = new Element("Record");
						masterEle.addContent(recordEle);
						for (String field : headData.get(x).keySet())
						{
							recordEle.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", headData.get(x).get(field)));
						}
						if (!StringUtils.isNullString(bodyTable))
						{
							Element bodyEle = new Element("Detail").setAttribute("name", bodyTable);
							TableRecordData bodyData = data.getTableData(bodyTable, this.stub);
							if (bodyData != null)
							{
								for (int j = 0; j < bodyData.size(); j++)
								{
									Element recordEle_body = new Element("Record");
									bodyEle.addContent(recordEle_body);
									for (String field : bodyData.get(j).keySet())
									{
										recordEle_body.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", bodyData.get(j).get(field)));
									}
								}
							}
							recordEle.addContent(bodyEle);
						}

						// YT要求插件位置每一个位置拆成一个Record传，即PLM的Designators字段要按,拆成多笔传输
						if (!StringUtils.isNullString(pluginTable))
						{
							Element pluginEle = new Element("Detail").setAttribute("name", pluginTable);
							TableRecordData pluginData = data.getTableData(pluginTable, this.stub);
							if (pluginData != null)
							{
								for (int k = 0; k < pluginData.size(); k++)
								{
									if (!StringUtils.isNullString(pluginData.get(k).get(RESERVED_DESIGNATORS)))
									{
										List<String> designatorList = Arrays.asList(pluginData.get(k).get(RESERVED_DESIGNATORS).split(DELIMITER_DESIGNATOR));
										for (int z = 0; z < designatorList.size(); z++)
										{
											if (StringUtils.isNullString(designatorList.get(z)) == false)
											{
												Element recordEle_plugin = new Element("Record");
												pluginEle.addContent(recordEle_plugin);
												pluginData.get(k).put(bmtIndexColumn, String.valueOf(z + 1));
												for (String field : pluginData.get(k).keySet())
												{
													if (RESERVED_DESIGNATORS.equalsIgnoreCase(field))
													{
														recordEle_plugin.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", designatorList.get(z)));
													}
													else
													{
														recordEle_plugin.addContent(new Element("Field").setAttribute("name", field).setAttribute("value",
																pluginData.get(k).get(field)));
													}
												}
											}
										}
									}
								}
							}
							recordEle.addContent(pluginEle);
						}
					}
				}
			}
		}
		return docEle;
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
		// 写Document部分
		Element docEle = this.getDocEle(this.getHeaderPackageOfData(operation.getCategory()), operation.getCategory(), totalCount, index);

		Element requestContentEle = new Element("RequestContent");
		requestContentEle.addContent(paramEle);
		requestContentEle.addContent(docEle);

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
		Element recordEle = new Element("Record").addContent(new Element("Field").setAttribute("name", "Seqkey").setAttribute("value", this.stub.jobId))
				.addContent(new Element("Field").setAttribute("name", "Seqkey_totcnt").setAttribute("value", this.optionListSize.toString()))
				.addContent(new Element("Field").setAttribute("name", "Seqkey_subcnt").setAttribute("value", String.valueOf(this.stub.seq_sub_cnt)))

				.addContent(new Element("Field").setAttribute("name", "datakey").setAttribute("value", StringUtils.convertNULLtoString(this.stub.getDataKey())))
				.addContent(new Element("Field").setAttribute("name", "datakey_totcnt").setAttribute("value", String.valueOf(totalCount)))
				.addContent(new Element("Field").setAttribute("name", "datakey_subcnt").setAttribute("value", String.valueOf(index)));
		if (parameter != null)
		{
			Map<String, String> parameterMap = parameter.getParamMap();
			for (String key : parameterMap.keySet())
			{
				recordEle.addContent(new Element("Field").setAttribute("name", key).setAttribute("value", parameterMap.get(key)));
			}
		}
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
		return "bmaa_t";
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
		setOptionListSize(tempSize);
	}

	private void setOptionListSize(Integer optionListSize)
	{
		this.optionListSize = optionListSize;
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
}
