package dyna.app.service.brs.erpi.dataExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.erpi.ERPT100DBTransferStub;
import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.app.service.brs.erpi.dblink.DBLinkStub;
import dyna.app.service.brs.erpi.dblink.TableRecord;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPT100DBOperationEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SearchRevisionTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;

public class IntegrateT100DB extends IntegrateERP<ERPT100DBOperationEnum>
{
	private final String						master				= "master";
	private final String						detail				= "detail";
	private final String						plugin				= "plugin";
	@SuppressWarnings("unused")
	private Integer								optionListSize		= 0;
	private Map<String, BOMStructure>			end2_StructureMap	= null;
	private List<FoundationObject>				objectList			= new ArrayList<FoundationObject>();

	private final String						CLASSIFICATION		= "Classification";

	private final String						CLASSIFICATION_ITEM	= "ClassificationItem";
	private final String						FIELD				= "field";
	private final String						CODEITEM			= "codeitem";

	private final String						TYPE_CLASS			= "class";
	private final String						TYPE_CODE			= "code";

	private Document							document			= null;

	private int									updateCount			= 0;

	private int									addCount			= 0;

	private DBLinkStub							dbLinkStub;

	private Map<String, List<ERPFieldMapping>>	cfTableFieldMap		= new HashMap<String, List<ERPFieldMapping>>();

	public IntegrateT100DB(ERPTransferStub<ERPT100DBOperationEnum> stub, Document doc, DBLinkStub dbLinkStub) throws Exception
	{
		super(stub, doc);
		this.xmlPath = "conf/T100_DBconf.xml";
		this.defaultDateFormat = "yyyy/MM/dd";
		this.document = doc;
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
	public void exportDataIntoTempTable(ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		List<TableRecord> listTableRecord = new ArrayList<TableRecord>();
		listTableRecord.add(this.getGeneralTaskData(operation.getWs()));
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
					TableRecordData recordData = recordSet.getTableData(tableName, stub);
					TableRecord tableRecord = new TableRecord(tableName, recordData);
					tableRecord.setErpFieldMappingList(erpFieldMappingContainer.get(tableName));
					listTableRecord.add(tableRecord);
				}
			}
		}
		dbLinkStub.batchInsertData(listTableRecord);
	}

	/**
	 * 获取任务总表数据
	 * 
	 * @param taskType
	 *            任务类型
	 * @return
	 */
	private TableRecord getGeneralTaskData(String taskType)
	{
		TableRecord tableRecord = new TableRecord("plm_tassuc_t");
		LinkedList<Map<String, String>> recordDataList = new LinkedList<Map<String, String>>();
		Map<String, String> taskDataMap = new LinkedHashMap<String, String>();
		taskDataMap.put("plm_pk", this.stub.job.getFieldh()); // 任务号
		taskDataMap.put("tass001", taskType); // 任务类型
		taskDataMap.put("tass002", this.stub.job.getFieldf()); // 企业编号
		taskDataMap.put("tass003", this.stub.job.getCreateUser());// PLM的登入者账号
		taskDataMap.put("tass004", DateFormat.formatYMDHMS(this.stub.job.getCreateTime()));// PLM数据抛转时间
		taskDataMap.put("tass005", "N");// 资料状态
		recordDataList.add(taskDataMap);
		tableRecord.setRecordDataList(recordDataList);
		List<ERPFieldMapping> fieldList = new ArrayList<ERPFieldMapping>();
		fieldList.add(this.getFieldMapping("plm_pk"));
		fieldList.add(this.getFieldMapping("tass001"));
		fieldList.add(this.getFieldMapping("tass002"));
		fieldList.add(this.getFieldMapping("tass003"));
		fieldList.add(this.getFieldMapping("tass004"));
		fieldList.add(this.getFieldMapping("tass005"));
		tableRecord.setErpFieldMappingList(fieldList);
		return tableRecord;
	}

	/**
	 * 创建ERPFieldMapping
	 * 
	 * @param erpField
	 * @return
	 */
	protected ERPFieldMapping getFieldMapping(String erpField)
	{
		ERPFieldMapping mapping = new ERPFieldMapping();
		mapping.setERPField(erpField);
		mapping.setDateFormat(this.defaultDateFormat);
		return mapping;
	}

	/**
	 * 从中间表获取数据保存到PLM系统
	 * 对于object类型的创建对象
	 * 对于code类型的创建code
	 * 
	 * @param category
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> saveDataFromTempTable(String category, String operation) throws ServiceRequestException
	{
		if (this.document != null)
		{
			List<Element> langEleList = this.document.getRootElement().getChild("category").getChild(category).getChildren("lang");
			if (!SetUtils.isNullList(langEleList))
			{
				for (Element langEle : langEleList)
				{
					String langName = langEle.getAttributeValue("name");
					String type = this.getCategoryType(category);
					Element classElem = null;
					if (TYPE_CLASS.equals(type))
					{
						classElem = langEle.getChild("class");
					}
					else if (TYPE_CODE.equals(type))
					{
						classElem = langEle.getChild("code");
					}
					else
					{
						throw new ServiceRequestException("category" + category + "type only support class and code!");
					}
					if (classElem != null)
					{
						String className = classElem.getAttributeValue("name");
						Iterator<String> tableIt = this.getTables(category).values().iterator();
						while (tableIt.hasNext())
						{
							String tableName = tableIt.next();
							List<ERPFieldMapping> erpFieldMapList = getFieldFromMap(category, className, tableName, langName, type);
							if (!SetUtils.isNullList(erpFieldMapList))
							{
								TableRecord tableRecord = new TableRecord(tableName);
								tableRecord.setErpFieldMappingList(erpFieldMapList);
								Map<String, String> conditionMap = new HashMap<String, String>();
								if (ERPT100DBOperationEnum.GetSupplier.getId().equals(operation))
								{
									conditionMap.put("pmaa003", langName);
								}
								else if (ERPT100DBOperationEnum.GetProCategory.getId().equals(operation))
								{
									conditionMap.put("rtax002", langName);
								}
								else if (ERPT100DBOperationEnum.GetItemGroup.getId().equals(operation))
								{
									conditionMap.put("oocq003", langName);
								}
								else if (ERPT100DBOperationEnum.GetJobNumber.getId().equals(operation))
								{
									conditionMap.put("oocr003", langName);
								}
								tableRecord.setConditionMap(conditionMap);
								LinkedList<Map<String, String>> dataList = null;
								try
								{
									((ERPT100DBTransferStub) this.stub).connectDbLink();
									dataList = dbLinkStub.searchDataFromTempTable(tableRecord);
								}
								catch (Exception e)
								{
									throw new ServiceRequestException("search data from ERP error!" + e.getMessage());
								}
								finally
								{
									((ERPT100DBTransferStub) this.stub).closeDblink();
								}

								if (!SetUtils.isNullList(dataList))
								{
									this.saveData(className, type, dataList, erpFieldMapList);
								}
							}
						}
					}
				}
			}
		}
		List<Integer> countList = new ArrayList<Integer>();
		countList.add(this.getAddCount());
		countList.add(this.getUpdateCount());
		return countList;
	}

	/**
	 * 
	 * @param className
	 * @param type
	 * @param dataList
	 * @param erpFieldMapList
	 * @throws ServiceRequestException
	 */
	private void saveData(String className, String type, LinkedList<Map<String, String>> dataList, List<ERPFieldMapping> erpFieldMapList) throws ServiceRequestException
	{
		if (TYPE_CLASS.equals(type))
		{
			this.saveClassData(className, dataList, erpFieldMapList);
		}
		else if (TYPE_CODE.equals(type))
		{
			this.saveCodeData(className, dataList, erpFieldMapList);
		}
	}

	/**
	 * 保存class数据
	 * 
	 * @param className
	 * @param dataList
	 * @param erpFieldMapList
	 * @throws ServiceRequestException
	 */
	private void saveClassData(String className, LinkedList<Map<String, String>> dataList, List<ERPFieldMapping> erpFieldMapList) throws ServiceRequestException
	{
		ERPFieldMapping idFieldMapping = this.getPLMIdField(erpFieldMapList);
		if (idFieldMapping != null)
		{
			for (Map<String, String> dataMap : dataList)
			{
				String dataId = dataMap.get(idFieldMapping.getERPField());
				SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(className, null, true);
				searchCondition.addFilter("ID$", dataId, OperateSignEnum.EQUALS);
				searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISLATESTONLY);
				List<FoundationObject> listObject = this.stub.getStubService().getBOAS().listObject(searchCondition);
				if (SetUtils.isNullList(listObject))
				{
					FoundationObject prepFoundationObj = this.stub.getStubService().getBOAS().newFoundationObject(null, className);
					if (!SetUtils.isNullList(erpFieldMapList))
					{
						for (ERPFieldMapping field : erpFieldMapList)
						{
							if (!StringUtils.isNullString(field.getPLMField()))
							{
								prepFoundationObj.put(field.getPLMField(), dataMap.get(field.getERPField()));
							}
						}
						this.stub.getStubService().getBOAS().createObject(prepFoundationObj);
						this.addCount++;
					}
				}
				else
				{
					FoundationObject foundationObj = this.stub.getStubService().getBOAS().getObject(listObject.get(0).getObjectGuid());
					if (!SetUtils.isNullList(erpFieldMapList) && foundationObj != null)
					{
						for (ERPFieldMapping field : erpFieldMapList)
						{
							if (!StringUtils.isNullString(field.getPLMField()))
							{
								foundationObj.put(field.getPLMField(), dataMap.get(field.getERPField()));
							}
						}
						((BOASImpl) this.stub.getStubService().getBOAS()).getFSaverStub().saveObject(foundationObj, false, true, false, null);
						this.updateCount++;
					}
				}
			}
		}
		else
		{
			throw new ServiceRequestException("could not get" + className + "ID$ column!");
		}
	}

	/**
	 * 保存code数据
	 * 
	 * @param className
	 * @param dataList
	 * @param erpFieldMapList
	 * @throws ServiceRequestException
	 */
	private void saveCodeData(String codeName, LinkedList<Map<String, String>> dataList, List<ERPFieldMapping> erpFieldMapList) throws ServiceRequestException
	{
		if (!StringUtils.isNullString(codeName))
		{
			CodeObjectInfo codeObjectInfo = this.stub.getStubService().getEMM().getCodeByName(codeName);
			if (codeObjectInfo != null && StringUtils.isGuid(codeObjectInfo.getGuid()))
			{
				ERPFieldMapping codeItemNameField = this.getPLMCodeItemName(erpFieldMapList);
				if (codeItemNameField != null)
				{
					for (Map<String, String> dataMap : dataList)
					{
						String codeItemName = dataMap.get(codeItemNameField.getERPField());
						CodeItemInfo codeDetailInfo = null;
						try
						{
							codeDetailInfo = this.getCodeDetailByName(codeObjectInfo.getGuid(), codeItemName);
						}
						catch (ServiceRequestException e)
						{
						}
						if (codeDetailInfo != null)
						{
							CodeItemInfo codeDetail = (CodeItemInfo) codeDetailInfo.clone();
							codeDetail.put("ISCLASSIFICATION", "N");
							if (!SetUtils.isNullList(erpFieldMapList))
							{
								String[] title = new String[3];
								for (ERPFieldMapping field : erpFieldMapList)
								{
									String PLMField = this.getPLMField(field.getPLMField());
									if (!StringUtils.isNullString(PLMField))
									{
										if ("Title_cn".equalsIgnoreCase(PLMField))
										{
											title[LanguageEnum.ZH_CN.getType()] = dataMap.get(field.getERPField());
										}
										else if ("Title_tw".equalsIgnoreCase(PLMField))
										{
											title[LanguageEnum.ZH_TW.getType()] = dataMap.get(field.getERPField());
										}
										else if ("Title_en".equalsIgnoreCase(PLMField))
										{
											title[LanguageEnum.EN.getType()] = dataMap.get(field.getERPField());
										}
										else
										{
											codeDetail.put(PLMField, dataMap.get(field.getERPField()));
										}
									}
								}
								codeDetail = setCodeTitle(codeDetail, title);
								this.stub.getStubService().getMMS().saveCodeItem(codeDetail);
								this.updateCount++;
							}
						}
						else
						{
							CodeItemInfo codeDetail = new CodeItemInfo();
							codeDetail.setMasterGuid(codeObjectInfo.getGuid());
							codeDetail.put("ISCLASSIFICATION", "N");
							if (!SetUtils.isNullList(erpFieldMapList))
							{
								String[] title = new String[3];
								for (ERPFieldMapping field : erpFieldMapList)
								{
									String PLMField = this.getPLMField(field.getPLMField());
									if (!StringUtils.isNullString(PLMField))
									{
										if ("Title_cn".equalsIgnoreCase(PLMField))
										{
											title[LanguageEnum.ZH_CN.getType()] = dataMap.get(field.getERPField());
										}
										else if ("Title_tw".equalsIgnoreCase(PLMField))
										{
											title[LanguageEnum.ZH_TW.getType()] = dataMap.get(field.getERPField());
										}
										else if ("Title_en".equalsIgnoreCase(PLMField))
										{
											title[LanguageEnum.EN.getType()] = dataMap.get(field.getERPField());
										}
										else
										{
											codeDetail.put(PLMField, dataMap.get(field.getERPField()));
										}
									}
								}
								if (codeDetail.getSequence() == null)
								{
									codeDetail.setSequence(getCodeItemMaxSequence(codeObjectInfo.getGuid()) + 1);
								}
								codeDetail = setCodeTitle(codeDetail, title);
								this.stub.getStubService().getMMS().createCodeItem(codeDetail);
								this.addCount++;
							}
						}
					}
				}
				else
				{
					throw new ServiceRequestException("could not get" + codeName + "Codeitem|Name column!");
				}
			}
			else
			{
				throw new ServiceRequestException("code:" + codeName + "is not exists in current model!");
			}
		}
	}

	private CodeItemInfo setCodeTitle(CodeItemInfo codeDetail, String[] title)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < title.length; i++)
		{
			buffer.append(StringUtils.convertNULLtoString(title[i]));
			if (i < title.length - 1)
			{
				buffer.append(";");
			}
		}
		codeDetail.setTitle(buffer.toString());
		return codeDetail;
	}

	/**
	 * 
	 * @param guid
	 * @param codeItemName
	 * @return
	 * @throws ServiceRequestException
	 */
	private CodeItemInfo getCodeDetailByName(String codeGuid, String codeItemName) throws ServiceRequestException
	{
		CodeItemInfo codeDeatil = null;
		if (StringUtils.isGuid(codeGuid) && !StringUtils.isNullString(codeItemName))
		{
			List<CodeItemInfo> codeDetailList = this.getCodeDetailList(codeGuid);
			if (!SetUtils.isNullList(codeDetailList))
			{
				for (int i = 0; i < codeDetailList.size(); i++)
				{
					if (codeItemName.equals(codeDetailList.get(i).getName()))
					{
						codeDeatil = codeDetailList.get(i);
						break;
					}
				}
			}
		}
		return codeDeatil;
	}

	private List<CodeItemInfo> getCodeDetailList(String codeGuid) throws ServiceRequestException
	{
		List<CodeItemInfo> codeDetailList = new ArrayList<>();
		CodeObjectInfo code = this.stub.getStubService().getEMM().getCode(codeGuid);
		CodeObjectInfo codeObject = this.stub.getStubService().getEMM().getCodeByName(code.getName());
		if (codeObject != null)
		{
			List<CodeItemInfo> codeItemList = DataServer.getCodeModelService().listDetailCodeItemInfo(codeObject.getGuid(), null);
			if (codeItemList != null)
			{
				codeItemList.stream().map(item -> codeDetailList.add(item));
			}
		}
		return codeDetailList;
	}

	/**
	 * 获取PLMField
	 * 主要对于进行修饰的PLM字段
	 * 进行去修饰CodeItem
	 * 
	 * @param PLMField
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getPLMField(String PLMField) throws ServiceRequestException
	{
		String PLMFieldName = null;
		if (!StringUtils.isNullString(PLMField) && PLMField.indexOf("Codeitem|") > -1)
		{
			PLMFieldName = PLMField.substring("Codeitem|".length(), PLMField.length());
		}
		if (StringUtils.isNullString(PLMFieldName))
		{
			throw new ServiceRequestException("PLMField:" + PLMField + " Configuration error!");
		}
		return PLMFieldName;
	}

	/**
	 * 获取codeItem的sequence
	 * 
	 * @param codeGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private int getCodeItemMaxSequence(String codeGuid) throws ServiceRequestException
	{
		int maxSequence = 0;
		if (!StringUtils.isNullString(codeGuid))
		{
			List<CodeItemInfo> codeDetailList = this.getCodeDetailList(codeGuid);
			if (!SetUtils.isNullList(codeDetailList))
			{
				for (CodeItemInfo codeDetail : codeDetailList)
				{
					if (codeDetail.getSequence() != null)
					{
						int sequence = codeDetail.getSequence();
						if (sequence > maxSequence)
						{
							maxSequence = sequence;
						}
					}
				}
			}
		}
		return maxSequence;
	}

	/**
	 * 获取CodeItem|Name列
	 * 
	 * @param erpFieldMapList
	 * @return
	 */
	private ERPFieldMapping getPLMCodeItemName(List<ERPFieldMapping> erpFieldMapList)
	{
		ERPFieldMapping CodeItemNameField = null;
		if (!SetUtils.isNullList(erpFieldMapList))
		{
			for (int i = 0; i < erpFieldMapList.size(); i++)
			{
				if ("Codeitem|Name".equals(erpFieldMapList.get(i).getPLMField()))
				{
					CodeItemNameField = erpFieldMapList.get(i);
					break;
				}
			}
		}
		return CodeItemNameField;
	}

	/**
	 * 当type为class时需获取ID$这一列
	 * 如果没有则报错
	 * 
	 * @param erpFieldMapList
	 * @return
	 */
	private ERPFieldMapping getPLMIdField(List<ERPFieldMapping> erpFieldMapList)
	{
		ERPFieldMapping idFieldMapping = null;
		if (!SetUtils.isNullList(erpFieldMapList))
		{
			for (int i = 0; i < erpFieldMapList.size(); i++)
			{
				if ("ID$".equals(erpFieldMapList.get(i).getPLMField()))
				{
					idFieldMapping = erpFieldMapList.get(i);
					break;
				}
			}
		}
		return idFieldMapping;
	}

	/**
	 * 获取category的type
	 * 主要用于供应商等支持class和code类型的传入
	 * 
	 * @param category
	 *            类名
	 * @return 类型type
	 */
	@SuppressWarnings("unchecked")
	public String getCategoryType(String category)
	{
		if (this.document != null)
		{
			Element langElem = this.document.getRootElement().getChild("category").getChild(category).getChild("lang");
			if (langElem != null)
			{
				List<Element> listElement = langElem.getChildren();
				if (!SetUtils.isNullList(listElement))
				{
					String type = listElement.get(0).getName();
					if (TYPE_CLASS.equals(type))
					{
						return TYPE_CLASS;
					}
					else if (TYPE_CODE.equals(type))
					{
						return TYPE_CODE;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void getRSData(FoundationObject end1FoundationObj, List<BOMStructure> bOMStructureList, List<FoundationObject> end2FoundationObjList, BOMTemplateInfo bomTemplate,
			ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		end2_StructureMap = getStructureByEnd2Map(bOMStructureList, end2FoundationObjList);
		super.getRSData(end1FoundationObj, bOMStructureList, end2FoundationObjList, bomTemplate, operation);
	}

	@Override
	public RecordSet getEachLocalS(FoundationObject obj, ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			BOMStructure structure = end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					// 部位编号
					if (m.containsKey("bmet004") && structure != null)
					{
						String value = StringUtils.convertNULLtoString(structure.get("PartNumber"));
						if (StringUtils.isGuid(value))
						{
							CodeItemInfo code = stub.getStubService().getEMM().getCodeItem(value);
							if (code != null)
							{
								value = code.getCode();
							}
						}
						m.put("bmet004", value);
					}
					// 作业编号
					if (m.containsKey("bmet005") && structure != null)
					{
						String jobGuid = (String) structure.get("JobNumber");
						String jobClassguid = (String) structure.get("JobNumber$CLASS");
						if (StringUtils.isGuid(jobGuid))
						{
							if (StringUtils.isGuid(jobClassguid))
							{
								ObjectGuid objectGuid = new ObjectGuid(jobClassguid, null, jobGuid, null);
								FoundationObject jobFoundation = this.stub.getStubService().getBOAS().getObjectByGuid(objectGuid);
								if (jobFoundation != null)
								{
									m.put("bmet005", jobFoundation.getId());
								}
							}
							else
							{
								CodeItemInfo code = stub.getStubService().getEMM().getCodeItem(jobGuid);
								if (code != null)
								{
									m.put("bmet005", code.getCode());
								}
							}
						}
					}
					// 取替代类型
					if (m.containsKey("bmet006"))
					{
						m.put("bmet006", "2");// <!--1.取代/2.替代-->
					}
					// 元件底数
					if (m.containsKey("bmet009") && structure != null && structure.get("Base") != null)
					{
						m.put("bmet009", StringUtils.convertNULLtoString(structure.get("Base")));
					}
					// 替代方式
					if (m.containsKey("bmet010"))
					{
						m.put("bmet010", "1");// <!--1.部分取替代,2.完全取替代-->
					}
					// 異動碼類型
					if (m.containsKey("bmet013") && isEarlierThanNow(obj))
					{
						m.put("bmet013", "D");
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
	public RecordSet getEachLocalR(FoundationObject obj, ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalR(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			BOMStructure structure = end2_StructureMap.get(obj.get(ReplaceSubstituteConstants.ComponentItem.toUpperCase()));
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					// 部位编号
					if (m.containsKey("bmet004") && structure != null)
					{
						String value = StringUtils.convertNULLtoString(structure.get("PartNumber"));
						if (StringUtils.isGuid(value))
						{
							CodeItemInfo code = stub.getStubService().getEMM().getCodeItem(value);
							if (code != null)
							{
								value = code.getCode();
							}
						}
						m.put("bmet004", value);
					}
					// 作业编号
					if (m.containsKey("bmet005") && structure != null)
					{
						String jobGuid = (String) structure.get("JobNumber");
						String jobClassguid = (String) structure.get("JobNumber$CLASS");
						if (StringUtils.isGuid(jobGuid))
						{
							if (StringUtils.isGuid(jobClassguid))
							{
								ObjectGuid objectGuid = new ObjectGuid(jobClassguid, null, jobGuid, null);
								FoundationObject jobFoundation = this.stub.getStubService().getBOAS().getObjectByGuid(objectGuid);
								if (jobFoundation != null)
								{
									m.put("bmet005", jobFoundation.getId());
								}
							}
							else
							{
								CodeItemInfo code = stub.getStubService().getEMM().getCodeItem(jobGuid);
								if (code != null)
								{
									m.put("bmet005", code.getCode());
								}
							}
						}
					}
					// 取替代类型
					if (m.containsKey("bmet006"))
					{
						m.put("bmet006", "1");// <!--1.取代/2.替代-->
					}
					// 元件底数
					if (m.containsKey("bmet009") && structure != null && structure.get("Base") != null)
					{
						m.put("bmet009", StringUtils.convertNULLtoString(structure.get("Base")));
					}
					// 替代方式
					if (m.containsKey("bmet010"))
					{
						m.put("bmet010", "1");// <!--1.部分取替代,2.完全取替代-->
					}
					// 異動碼類型
					if (m.containsKey("bmet013") && isEarlierThanNow(obj))
					{
						m.put("bmet013", "D");
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
	public RecordSet getEachGlobalS(FoundationObject obj, ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachGlobalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					// 取替代类型
					if (m.containsKey("bmet006"))
					{
						m.put("bmet006", "2");// <!--1.取代/2.替代-->
					}
					if (m.containsKey("bmet010"))
					{
						m.put("bmet010", "2");// <!--1.取代/2.替代-->
					}
					// 異動碼類型
					if (m.containsKey("bmet013") && isEarlierThanNow(obj))
					{
						m.put("bmet013", "D");
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
	protected RecordSet getEachItemData(FoundationObject end1Obj, ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(end1Obj, ERP_ITEM, operation))
		{
			return null;
		}
		RecordSet recordSet = new RecordSet(end1Obj.getObjectGuid());
		Map<String, String> tableMap = this.getTables(ERP_ITEM);
		if (!SetUtils.isNullMap(tableMap))
		{
			Iterator<String> tableIt = tableMap.keySet().iterator();
			String tableName = null;
			String tableId = null;
			// add2StampMap(ERP_ITEM, end1Obj);
			String className = end1Obj.getObjectGuid().getClassName();
			while (tableIt.hasNext())
			{
				tableId = tableIt.next();
				tableName = tableMap.get(tableId);
				// fieldList： 获得一个table下的所有字段匹配
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_ITEM, className, tableName);
				this.erpFieldMappingContainer.put(tableName, className, fieldList);
				if (this.stub.schema.isExpandClassification())
				{
					if (end1Obj.getClassificationGuid() != null)
					{
						CodeItemInfo codeItem = this.stub.getStubService().getEMM().getCodeItem(end1Obj.getClassificationGuid());
						if (detail.equalsIgnoreCase(tableId))
						{
							List<UIField> tempList = this.stub.getStubService().getEMM().listCFUIField(end1Obj.getClassificationGuid(), UITypeEnum.FORM);
							Map<String, ClassField> classifiFieldMap = getCFFieldMap(end1Obj.getClassificationGuid());
							if (SetUtils.isNullList(tempList) || SetUtils.isNullMap(classifiFieldMap))
							{
								continue;
							}
							else
							{
								TableRecordData tableRecord = new TableRecordData();
								for (int i = 0; i < tempList.size(); i++)
								{
									ClassField classField = classifiFieldMap.get(tempList.get(i).getName());
									tableRecord.add(this.getItemCFInfoMapValue(end1Obj, codeItem, classField, fieldList, operation));
								}
								recordSet.put(tableName, tableRecord);
							}
						}
						else
						{
							TableRecordData tableRecord = new TableRecordData(this.getItemInfoMapValue(end1Obj, fieldList, codeItem, operation));
							recordSet.put(tableName, tableRecord);
						}
					}
					else
					{
						if (detail.equalsIgnoreCase(tableId))
						{
							continue;
						}
						TableRecordData tableRecord = new TableRecordData(this.getItemInfoMapValue(end1Obj, fieldList, null, operation));
						recordSet.put(tableName, tableRecord);
					}
				}
				else
				{
					if (detail.equalsIgnoreCase(tableId))
					{
						continue;
					}
					TableRecordData tableRecord = new TableRecordData(this.getObjectMapValue(end1Obj, fieldList, ERP_ITEM, operation));
					recordSet.put(tableName, tableRecord);
				}
			}
		}
		return recordSet;
	}

	/**
	 * 将分类上所有字段取出来放在map中
	 * 方便以后取出来使用
	 * 
	 * @param classificationGuid
	 *            分类guid
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, ClassField> getCFFieldMap(String classificationGuid) throws ServiceRequestException
	{
		Map<String, ClassField> classFieldMap = new HashMap<String, ClassField>();
		if (StringUtils.isGuid(classificationGuid))
		{
			List<ClassField> listClassField = this.stub.getStubService().getEMM().listClassificationField(classificationGuid);
			if (!SetUtils.isNullList(listClassField))
			{
				for (ClassField classField : listClassField)
				{
					classFieldMap.put(classField.getName(), classField);
				}
			}
		}
		return classFieldMap;
	}

	/**
	 * 获取Item detail表上所有信息
	 * 分类信息
	 * 
	 * @param end1Obj
	 *            baseObject
	 * @param info
	 *            codeitemInfo
	 * @param uiField
	 *            字段
	 * @param fieldList
	 * @param classifiFieldMap
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getItemCFInfoMapValue(FoundationObject end1Obj, CodeItemInfo codeItem, ClassField classField, List<ERPFieldMapping> fieldList,
			ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (fieldList == null)
		{
			return returnMap;
		}
		for (int j = 0; j < fieldList.size(); j++)
		{
			ERPFieldMapping field = fieldList.get(j);
			String dataValue = null;
			if (isCFKeyField(field))
			{
				dataValue = this.getCFFieldValue(end1Obj, field, codeItem, classField, operation);
			}
			else
			{
				dataValue = this.getFieldValue(end1Obj, field, ERP_ITEM, operation);
			}
			this.checkFieldValue(dataValue, field, end1Obj);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

	/**
	 * 获取item detail表上自定义字段的值
	 * 如 Field|name CodeItem|name
	 * 
	 * @param end1Obj
	 * @param field
	 * @param info
	 * @param operation
	 * @param uiField
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getCFFieldValue(FoundationObject end1Obj, ERPFieldMapping field, CodeItemInfo info, ClassField classField, ERPT100DBOperationEnum operation)
			throws ServiceRequestException
	{
		String resultValue = "";
		if (!StringUtils.isNullString(field.getPLMField()) && classField != null)
		{
			List<JointField> fieldList = JointField.getFiels(field.getPLMField());
			FoundationObject targetObject = getCFTarget(end1Obj, info);
			for (JointField jointField : fieldList)
			{
				Object dataValue = null;
				String fieldName = jointField.getClearFieldName();
				String delegateFieldName = jointField.getDelegateFieldName();
				if (FIELD.equalsIgnoreCase(fieldName))
				{
					if ("name".equalsIgnoreCase(delegateFieldName))
					{
						dataValue = classField.getName();
					}
					else if ("title".equalsIgnoreCase(delegateFieldName))
					{
						dataValue = classField.getTitle(this.stub.lang);
					}
					else if ("description".equalsIgnoreCase(delegateFieldName))
					{
						dataValue = classField.getDescription();
					}
				}
				else if (CODEITEM.equalsIgnoreCase(fieldName))
				{
					if (classField.getType().equals(FieldTypeEnum.CODE))
					{
						String codeItemGuid = targetObject == null ? null : targetObject.get(classField.getName()) == null ? "" : (String) targetObject.get(classField.getName());
						if (StringUtils.isGuid(codeItemGuid))
						{
							CodeItemInfo codeItem = this.stub.getStubService().getEMM().getCodeItem(codeItemGuid);
							if (codeItem != null)
							{
								if ("name".equalsIgnoreCase(delegateFieldName))
								{
									dataValue = codeItem.getName();
								}
								else if ("code".equalsIgnoreCase(delegateFieldName))
								{
									dataValue = codeItem.getCode();
								}
							}
						}
					}
					else if (classField.getType().equals(FieldTypeEnum.OBJECT))
					{
						String guid = targetObject == null ? null : (String) targetObject.get(classField.getName());
						String classGuid = targetObject == null ? null : (String) targetObject.get(fieldName + "$CLASS");
						if (guid != null && classGuid != null)
						{
							ObjectGuid objectGuidFieldObject = new ObjectGuid();
							objectGuidFieldObject.setGuid(guid);
							objectGuidFieldObject.setClassGuid(classGuid);
							FoundationObject foundationObjectByField = (FoundationObject) this.getObject(objectGuidFieldObject);
							if (foundationObjectByField != null)
							{
								if (StringUtils.isNullString(delegateFieldName))
								{// 如果没有delegateFieldName则默认取ID$字段
									delegateFieldName = "ID$";
								}
								// 不能简单的认为delegateFieldName就是这个对象的普通字段，可能delegateFieldName也是这个对象的复杂字段
								ERPFieldMapping fieldMapping = new ERPFieldMapping();
								fieldMapping.setPLMField(delegateFieldName);
								dataValue = this.getFieldValue(foundationObjectByField, fieldMapping, null, operation);
							}
						}
					}
					else
					{
						Object object = targetObject == null ? null : targetObject.get(classField.getName());
						dataValue = object == null ? null : object.toString();
						if (classField.getType().equals(FieldTypeEnum.DATE) && dataValue != null)
						{
							dataValue = DateFormat.formatYMD(DateFormat.parse(dataValue.toString()));
						}
					}
				}
				resultValue += (dataValue == null ? "" : dataValue.toString());
			}
		}
		if (StringUtils.isNullString(resultValue))
		{
			resultValue = field.getDefaultValue();
		}
		resultValue = this.formatValue(resultValue, field);
		return resultValue;
	}

	/**
	 * 获得分类数据
	 * 
	 * @param baseObj
	 * @param info
	 * @return
	 */
	private FoundationObject getCFTarget(FoundationObject baseObj, CodeItemInfo info)
	{
		FoundationObject result = null;
		if (baseObj != null && !SetUtils.isNullList(baseObj.restoreAllClassification(false)))
		{
			for (FoundationObject object : baseObj.restoreAllClassification(false))
			{
				if (object.getClassificationGuid() != null && info.getGuid().equals(object.getClassificationGuid()))
				{
					result = object;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 获取Item master基础信息
	 * 
	 * @param end1Obj
	 * @param fieldList
	 * @param isHasClassification
	 * @param operation
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getItemInfoMapValue(FoundationObject end1Obj, List<ERPFieldMapping> fieldList, CodeItemInfo codeItem, ERPT100DBOperationEnum operation)
			throws ServiceRequestException
	{
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (fieldList == null)
		{
			return returnMap;
		}
		for (int j = 0; j < fieldList.size(); j++)
		{
			ERPFieldMapping field = fieldList.get(j);
			String dataValue = null;
			if (isCFKeyField(field))
			{
				dataValue = this.getCFAttributeValue(end1Obj, field, codeItem);
			}
			else
			{
				dataValue = this.getFieldValue(end1Obj, field, ERP_ITEM, operation);
			}
			this.checkFieldValue(dataValue, field, end1Obj);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

	/**
	 * 获取classification和classification_item的属性信息
	 * 当集成分类特征时，取类似Classification|Name+ClassificationItem|Name
	 * 
	 * @param end1Obj
	 * @param field
	 * @param codeItem
	 *            ClassificationItem
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getCFAttributeValue(FoundationObject end1Obj, ERPFieldMapping field, CodeItemInfo codeItem) throws ServiceRequestException
	{
		String resultValue = "";
		if (!StringUtils.isNullString(field.getPLMField()))
		{
			List<JointField> fieldList = JointField.getFiels(field.getPLMField());
			for (JointField jointField : fieldList)
			{
				Object dataValue = null;
				String fieldName = jointField.getClearFieldName();
				String delegateFieldName = jointField.getDelegateFieldName();
				if (CLASSIFICATION.equalsIgnoreCase(fieldName))
				{
					if (codeItem != null && StringUtils.isGuid(codeItem.getCodeGuid()))
					{
						CodeObjectInfo codeObject = this.stub.getStubService().getEMM().getCode(codeItem.getCodeGuid());
						if (codeObject != null)
						{
							if ("name".equalsIgnoreCase(delegateFieldName))
							{
								dataValue = codeObject.getName();
							}
							else if ("title".equalsIgnoreCase(delegateFieldName))
							{
								dataValue = codeObject.getTitle(this.stub.lang);
							}
							else if ("description".equalsIgnoreCase(delegateFieldName))
							{
								dataValue = codeObject.getDescription();
							}
						}
					}
				}
				else if (CLASSIFICATION_ITEM.equalsIgnoreCase(fieldName))
				{
					if (codeItem != null)
					{
						if ("name".equalsIgnoreCase(delegateFieldName))
						{
							dataValue = codeItem.getName();
						}
						else if ("title".equalsIgnoreCase(delegateFieldName))
						{
							dataValue = codeItem.getTitle(this.stub.lang);
						}
						else if ("description".equalsIgnoreCase(delegateFieldName))
						{
							dataValue = codeItem.getDescription();
						}
						else if ("code".equalsIgnoreCase(fieldName))
						{
							dataValue = codeItem.getCode();
						}
						else if ("fullname".equalsIgnoreCase(fieldName))
						{
							dataValue = this.reNameCode(codeItem);
						}
					}
				}
				resultValue += (dataValue == null ? "" : dataValue.toString());
			}
		}
		if (StringUtils.isNullString(resultValue))
		{
			resultValue = field.getDefaultValue();
		}
		resultValue = this.formatValue(resultValue, field);
		return resultValue;
	}

	/**
	 * 是否是关键字字段Classification和ClassificationItem
	 * codeItem和Field
	 * 
	 * @param field
	 *            字段
	 * @return
	 */
	private boolean isCFKeyField(ERPFieldMapping field)
	{
		if (!StringUtils.isNullString(field.getPLMField()))
		{
			List<JointField> jointFieldList = JointField.getFiels(field.getPLMField());
			for (JointField jointField : jointFieldList)
			{
				String fieldName = jointField.getClearFieldName();
				if (CLASSIFICATION.equalsIgnoreCase(fieldName) || CLASSIFICATION_ITEM.equalsIgnoreCase(fieldName) || FIELD.equalsIgnoreCase(fieldName)
						|| CODEITEM.equalsIgnoreCase(fieldName))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected RecordSet getEachBOMData(FoundationObject end1Obj, List<BOMStructure> bomStructureList, BOMStructure parentBOMStructure, ERPT100DBOperationEnum operation)
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
									Map<String, String> pluginMap = new HashMap<String, String>(map);
									pluginMap.put("bmbt005", String.valueOf(j + 1));
									pluginMap.put("bmbt006", designatorList.get(j));
									tableRecord.add(pluginMap);
								}
							}
						}
						else if (master.equalsIgnoreCase(tableId))
						{// <!-- 有插件位置則為Y，無則為N，程式判斷 -->
							if (bomStructure.get("Designators") != null)
							{
								map.put("bmat008", "Y");
							}
							tableRecord.add(map);
						}
					}
					recordSet.put(tableName, tableRecord);
				}
			}
		}
		/*
		 * else if (this.hasBOM(end1Obj))
		 * {
		 * Iterator<String> tableIt = this.getTables(ERP_BOM).values().iterator();
		 * while (tableIt.hasNext())
		 * {
		 * tableName = tableIt.next();
		 * this.getFieldFromMap(ERP_BOM, this.getArbitraryBOMClassName(), tableName);
		 * }
		 * 
		 * tableName = this.getBOMHeaderTableName();
		 * TableRecordData tableRecord = new TableRecordData();
		 * ArrayList<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_BOM, this.getArbitraryBOMClassName(),
		 * tableName);
		 * this.mapERPFieldMapping.put(tableName, fieldList);
		 * fieldList = this.removeEnd1Prefix(fieldList);
		 * tableRecord.add(this.getObjectMapValue(end1Obj, fieldList, ERP_BOM, operation));
		 * recordSet.put(tableName, tableRecord);
		 * }
		 */
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
	public Document getCreateDataXML(ERPT100DBOperationEnum operation, int totalCount, int index) throws Exception
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
	private Element getParamEle(ERPParameter parameter, ERPT100DBOperationEnum operation, int totalCount, int index)
	{
		Element recordEle = new Element("Record").addContent(this.stub.jobId);
		Element paramEle = new Element("Parameter").addContent(recordEle);
		return paramEle;
	}

	@Override
	public String getOperationId(ERPT100DBOperationEnum operation)
	{
		return operation.getId();
	}

	@Override
	public String getOperationCategory(ERPT100DBOperationEnum operation)
	{
		return operation.getCategory();
	}

	@Override
	public List<ERPT100DBOperationEnum> getOperationList(boolean isMergeByCategory) throws ServiceRequestException
	{
		List<String> operations = this.stub.schema.getOperationList();
		List<String> categoryList = new ArrayList<String>();
		List<ERPT100DBOperationEnum> operationList = new ArrayList<ERPT100DBOperationEnum>();
		for (int i = 0; i < operations.size(); i++)
		{
			String id = operations.get(i);
			Map<String, String> attrMap = this.stub.getStubService().getOperationAttribute(ERPServerType.ERPT100DB, id);
			ERPT100DBOperationEnum e = ERPT100DBOperationEnum.getById(id);
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
	public String getOperationCrossServiceName(ERPT100DBOperationEnum operation)
	{
		final String methodName = operation.getWs();
		return Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
	}

	@Override
	public String getOperationName(ERPT100DBOperationEnum operation)
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
		List<ERPT100DBOperationEnum> operationList = this.getOperationList(true);
		int tempSize = operationList.size();
		for (int k = 0; k < operationList.size(); k++)
		{
			ERPT100DBOperationEnum operation = operationList.get(k);
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
		// List<ERPT100DBOperationEnum> operations = getOperationList(false);
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
		String returnXML = ((ERPT100DBTransferStub) this.stub).callWS(originalXML, ERPT100DBOperationEnum.GetSupplyDemandData);
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

	protected void getCFData(List<CodeItemInfo> codeItemList, ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		RecordSet recordSet = new RecordSet();
		if (!SetUtils.isNullList(codeItemList))
		{
			TableRecordData masterTableRecord = null;
			TableRecordData detailTableRecord = null;
			TableRecordData pluginTableRecord = null;
			Map<String, String> tableMap = this.getTables(ERP_CF);
			if (!SetUtils.isNullMap(tableMap))
			{
				for (CodeItemInfo codeItemInfo : codeItemList)
				{
					Iterator<String> tableIt = tableMap.keySet().iterator();
					// 记录code类型字段
					List<ClassField> codeFieldList = null;
					while (tableIt.hasNext())
					{
						String tableId = tableIt.next();
						// fieldList： 获得一个table下的所有字段匹配
						List<ERPFieldMapping> fieldList = this.getCFFieldFromMap(ERP_CF, tableMap.get(tableId));
						this.erpFieldMappingContainer.put(tableMap.get(tableId), null, fieldList);
						if (master.equals(tableId))
						{
							if (masterTableRecord == null)
							{
								masterTableRecord = new TableRecordData();
							}
							masterTableRecord.add(this.getCFMapValue(codeItemInfo, fieldList, null, null, operation));
						}
						if (detail.equals(tableId))
						{
							List<UIField> uiFieldList = this.stub.getStubService().getEMM().listCFUIField(codeItemInfo.getGuid(), UITypeEnum.FORM);
							Map<String, ClassField> cfFieldMap = getCFFieldMap(codeItemInfo.getGuid());
							if (SetUtils.isNullList(uiFieldList) || SetUtils.isNullMap(cfFieldMap))
							{
								continue;
							}
							else
							{
								if (detailTableRecord == null)
								{
									detailTableRecord = new TableRecordData();
								}
								codeFieldList = new ArrayList<ClassField>();
								for (int i = 0; i < uiFieldList.size(); i++)
								{
									ClassField classField = cfFieldMap.get(uiFieldList.get(i).getName());
									if (classField != null)
									{
										if (FieldTypeEnum.CODE.equals(classField.getType()))
										{
											codeFieldList.add(classField);
										}
										detailTableRecord.add(this.getCFMapValue(codeItemInfo, fieldList, classField, null, operation));
									}
								}
							}
						}
						if (plugin.equals(tableId) && !SetUtils.isNullList(codeFieldList))
						{
							if (pluginTableRecord == null)
							{
								pluginTableRecord = new TableRecordData();
							}
							for (int i = 0; i < codeFieldList.size(); i++)
							{
								ClassField classField = codeFieldList.get(i);
								String typeValue = classField.getTypeValue();
								if (!StringUtils.isNullString(typeValue))
								{
									CodeObjectInfo codeObjctInfo = this.stub.getStubService().getEMM().getCodeByName(typeValue);
									pluginTableRecord.add(this.getCFMapValue(codeItemInfo, fieldList, classField, codeObjctInfo, operation));
								}
							}
						}
					}
				}

				if (!StringUtils.isNullString(tableMap.get(master)) && masterTableRecord != null)
				{
					recordSet.put(tableMap.get(master), masterTableRecord);
				}
				if (!StringUtils.isNullString(tableMap.get(detail)) && detailTableRecord != null)
				{
					recordSet.put(tableMap.get(detail), detailTableRecord);
				}
				if (!StringUtils.isNullString(tableMap.get(plugin)) && pluginTableRecord != null)
				{
					recordSet.put(tableMap.get(plugin), pluginTableRecord);
				}
			}
		}
		this.addToDataMap(ERP_CF, recordSet);
	}

	/**
	 * 抛分类时获取分类信息
	 * 
	 * @param codeItemInfo
	 * @param fieldList
	 * @param operation
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getCFMapValue(CodeItemInfo codeItemInfo, List<ERPFieldMapping> fieldList, ClassField classField, CodeObjectInfo codeObjctInfo,
			ERPT100DBOperationEnum operation) throws ServiceRequestException
	{
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (fieldList == null)
		{
			return returnMap;
		}
		SystemObject tempObject = new SystemObjectImpl();
		tempObject.put("FULLNAME$", "CLASSIFICATION");
		for (int j = 0; j < fieldList.size(); j++)
		{
			ERPFieldMapping field = fieldList.get(j);
			String dataValue = null;
			dataValue = this.getCFFieldValue(codeItemInfo, field, classField, codeObjctInfo, operation);
			this.checkFieldValue(dataValue, field, tempObject);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

	/**
	 * 获取分类字段value
	 * 
	 * @param codeItemInfo
	 * @param field2
	 * @param operation
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getCFFieldValue(CodeItemInfo codeItemInfo, ERPFieldMapping field, ClassField classField, CodeObjectInfo codeObjctInfo, ERPT100DBOperationEnum operation)
			throws ServiceRequestException
	{
		String resultValue = "";
		if (!StringUtils.isNullString(field.getPLMField()))
		{
			List<JointField> fieldList = JointField.getFiels(field.getPLMField());
			for (JointField jointField : fieldList)
			{
				Object dataValue = null;
				String fieldName = jointField.getClearFieldName();
				String delegateFieldName = jointField.getDelegateFieldName();
				if (this.isBuildinField(jointField.getFieldName()))
				{
					dataValue = this.getBuildinFieldValue(jointField.getName(), field, ERP_CF, null, operation);
				}
				else
				{
					if (CLASSIFICATION.equalsIgnoreCase(fieldName))
					{
						CodeObjectInfo codeInfo = this.stub.getStubService().getEMM().getCode(codeItemInfo.getCodeGuid());
						dataValue = this.getCodeAttributeValue(codeInfo, delegateFieldName);
					}
					else if (this.CLASSIFICATION_ITEM.equalsIgnoreCase(fieldName))
					{
						dataValue = this.getCodeItemAttributeValue(codeItemInfo, delegateFieldName);
					}
					else if (this.FIELD.equalsIgnoreCase(fieldName))
					{
						dataValue = this.getFieldAttributeValue(classField, delegateFieldName);
					}
					else if (this.CODEITEM.equalsIgnoreCase(fieldName))
					{
						dataValue = this.getCodeAttributeValue(codeObjctInfo, delegateFieldName);
					}
				}
				resultValue += (dataValue == null ? "" : dataValue.toString());
			}
		}
		if (StringUtils.isNullString(resultValue))
		{
			resultValue = field.getDefaultValue();
		}
		resultValue = this.formatValue(resultValue, field);
		resultValue = this.upperCaseSpecificField(resultValue, field);
		return resultValue;
	}

	/**
	 * 将某些特殊字段转化为大写
	 * 
	 * @param resultValue
	 * @param field2
	 * @return
	 */
	private String upperCaseSpecificField(String resultValue, ERPFieldMapping field)
	{
		if (!StringUtils.isNullString(resultValue))
		{
			if (field != null && !StringUtils.isNullString(field.getERPField()))
			{
				String erpField = field.getERPField();
				if ("imea001".equals(erpField) || "imeb001".equals(erpField) || "imeb002".equals(erpField) || "imec001".equals(erpField) || "imec002".equals(erpField)
						|| "imec003".equals(erpField))
				{
					resultValue = resultValue.toUpperCase();
				}
			}
		}
		return resultValue;
	}

	/**
	 * 获取分类字段的属性值
	 * 
	 * @param classField
	 * @param delegateFieldName
	 * @return
	 */
	private Object getFieldAttributeValue(ClassField classField, String delegateFieldName)
	{
		Object dataValue = null;
		if ("name".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = classField.getName();
		}
		else if ("title".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = classField.getTitle();
		}
		else if ("description".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = classField.getDescription();
		}
		else if ("Title_cn".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = classField.getTitle(LanguageEnum.ZH_CN);
		}
		else if ("Title_tw".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = classField.getTitle(LanguageEnum.ZH_TW);
		}
		else if ("Title_en".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = classField.getTitle(LanguageEnum.EN);
		}
		else if ("scope".equalsIgnoreCase(delegateFieldName))
		{
			FieldTypeEnum type = classField.getType();
			if (FieldTypeEnum.STRING.equals(type) || FieldTypeEnum.BOOLEAN.equals(type) || FieldTypeEnum.DATE.equals(type) || FieldTypeEnum.DATETIME.equals(type)
					|| FieldTypeEnum.OBJECT.equals(type))
			{
				dataValue = "1";
			}
			else if (FieldTypeEnum.CODE.equals(type))
			{
				dataValue = "2";
			}
			else if (FieldTypeEnum.INTEGER.equals(type) || FieldTypeEnum.FLOAT.equals(type))
			{
				dataValue = "3";
			}
		}
		else if ("type".equalsIgnoreCase(delegateFieldName))
		{
			FieldTypeEnum type = classField.getType();
			if (FieldTypeEnum.CODE.equals(type) || FieldTypeEnum.STRING.equals(type) || FieldTypeEnum.BOOLEAN.equals(type) || FieldTypeEnum.DATE.equals(type)
					|| FieldTypeEnum.DATETIME.equals(type) || FieldTypeEnum.OBJECT.equals(type))
			{
				dataValue = "1";
			}
			else if (FieldTypeEnum.FLOAT.equals(type))
			{
				dataValue = "2";
			}
			else if (FieldTypeEnum.INTEGER.equals(type))
			{
				dataValue = "3";
			}

		}
		else if ("size".equalsIgnoreCase(delegateFieldName))
		{
			FieldTypeEnum type = classField.getType();
			if (FieldTypeEnum.STRING.equals(type) || FieldTypeEnum.INTEGER.equals(type) || FieldTypeEnum.FLOAT.equals(type))
			{
				String rawSize = classField.getFieldSize();
				if (!StringUtils.isNullString(rawSize))
				{
					dataValue = rawSize.split(",")[0];
				}
			}
			else if (FieldTypeEnum.DATE.equals(type))
			{
				dataValue = "10";
			}
			else if (FieldTypeEnum.DATETIME.equals(type))
			{
				dataValue = "19";
			}
			else if (FieldTypeEnum.OBJECT.equals(type))
			{
				dataValue = "128";
			}
			else if (FieldTypeEnum.BOOLEAN.equals(type))
			{
				dataValue = "1";
			}
		}
		return dataValue;
	}

	/**
	 * 获取codeItem属性值
	 * 
	 * @param codeItemInfo
	 * @param delegateFieldName
	 * @return
	 * @throws ServiceRequestException
	 */
	private Object getCodeItemAttributeValue(CodeItemInfo codeItemInfo, String delegateFieldName) throws ServiceRequestException
	{
		Object dataValue = null;
		if ("name".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getName();
		}
		else if ("code".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getCode();
		}
		else if ("title".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getTitle();
		}
		else if ("description".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getDescription();
		}
		else if ("Title_cn".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getTitle(LanguageEnum.ZH_CN);
		}
		else if ("Title_tw".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getTitle(LanguageEnum.ZH_TW);
		}
		else if ("Title_en".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeItemInfo.getTitle(LanguageEnum.EN);
		}
		else if ("fullname".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = reNameCode(codeItemInfo);
		}
		return dataValue;
	}

	/**
	 * 获取code的属性值
	 * 
	 * @param codeObjectInfo
	 * @param delegateFieldName
	 * @return
	 */
	private Object getCodeAttributeValue(CodeObjectInfo codeObjectInfo, String delegateFieldName)
	{
		Object dataValue = null;
		if ("name".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeObjectInfo.getName();
		}
		else if ("title".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeObjectInfo.getTitle();
		}
		else if ("description".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeObjectInfo.getDescription();
		}
		else if ("Title_cn".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeObjectInfo.getTitle(LanguageEnum.ZH_CN);
		}
		else if ("Title_tw".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeObjectInfo.getTitle(LanguageEnum.ZH_TW);
		}
		else if ("Title_en".equalsIgnoreCase(delegateFieldName))
		{
			dataValue = codeObjectInfo.getTitle(LanguageEnum.EN);
		}
		return dataValue;
	}

	@Override
	protected void generateAllData(List<String> codeGuidList) throws ServiceRequestException
	{

		CodeItemInfo codeItem = null;
		List<CodeItemInfo> codeItemList = new ArrayList<CodeItemInfo>();
		for (String codeguid : codeGuidList)
		{
			codeItem = this.stub.getStubService().getEMM().getCodeItem(codeguid);
			if (codeItem != null)
			{
				codeItemList.add(codeItem);
			}
		}
		List<ERPT100DBOperationEnum> operationList = this.getOperationList(false);
		for (int k = 0; k < operationList.size(); k++)
		{
			ERPT100DBOperationEnum operation = operationList.get(k);
			String category = this.getOperationCategory(operation);
			if (StringUtils.isNullString(category))
			{
				throw new IllegalArgumentException("no category attribute for " + operationList.get(k).name() + " in " + this.xmlPath);
			}
			// try
			// {
			this.getCFData(codeItemList, operation);
			// }
			// catch (InvalidDataException e)
			// {
			// this.addDataError(e);
			// if (!this.isTraverseAllInstanceIgnoringError())
			// {
			// return;
			// }
			// }
		}
	}

	/**
	 * 获取ERPFieldMapping
	 * 
	 * @param category
	 *            种类
	 * @param className
	 *            类名(code名)
	 * @param tableName
	 *            表名
	 * @param lang
	 *            多语言
	 * @return
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("unchecked")
	protected List<ERPFieldMapping> getFieldFromMap(String category, String className, String tableName, String lang, String type) throws ServiceRequestException
	{
		List<ERPFieldMapping> erpFieldMapList = null;
		if (!StringUtils.isNullString(category) && !StringUtils.isNullString(className) && !StringUtils.isNullString(tableName) && !StringUtils.isNullString(lang))
		{
			erpFieldMapList = new ArrayList<ERPFieldMapping>();
			Element cateElement = this.doc.getRootElement().getChild("category").getChild(category);
			if (cateElement != null)
			{
				Attribute attribute = new Attribute("name", lang);
				Element langElem = this.getChildElementWithAttr(cateElement, "lang", attribute);
				if (langElem != null)
				{
					attribute = new Attribute("name", className);
					Element classElem = null;
					if ("class".equals(type))
					{
						classElem = this.getChildElementWithAttr(langElem, "class", attribute);
					}
					else if ("code".equals(type))
					{
						classElem = this.getChildElementWithAttr(langElem, "code", attribute);
					}
					if (classElem != null)
					{
						attribute = new Attribute("name", tableName);
						Element tableElem = this.getChildElementWithAttr(classElem, "table", attribute);
						if (tableElem != null)
						{
							List<Element> mappingList = tableElem.getChildren("mapping");
							if (!SetUtils.isNullList(mappingList))
							{
								for (Element mappingEle : mappingList)
								{
									Map<String, String> paramMap = this.getMapByElement(mappingEle);
									if (!SetUtils.isNullMap(paramMap))
									{
										erpFieldMapList.add(this.getFieldMapping(paramMap));
									}
								}
							}
						}
					}
				}
			}
		}
		return erpFieldMapList;
	}

	/**
	 * 获取父节点下具有attribute属性的节点
	 * 
	 * @param parentElem
	 * @param attribute
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Element getChildElementWithAttr(Element parentElem, String name, Attribute attribute)
	{
		Element childElem = null;
		if (parentElem != null && attribute != null)
		{
			List<Element> elementList = parentElem.getChildren(name);
			if (!SetUtils.isNullList(elementList))
			{
				for (Element element : elementList)
				{
					String attributeName = attribute.getName();
					String attributeValue = attribute.getValue();
					if (!StringUtils.isNullString(attributeName) && !StringUtils.isNullString(attributeValue))
					{
						if (attributeValue.equalsIgnoreCase(element.getAttributeValue(attributeName)))
						{
							childElem = element;
							break;
						}
					}
				}
			}
		}
		return childElem;
	}

	/**
	 * 根据表名获取ERPFieldMapping
	 * 
	 * @param category
	 *            类别，这里为ERP_CF
	 * @param tableName
	 *            表名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<ERPFieldMapping> getCFFieldFromMap(String category, String tableName)
	{
		if (SetUtils.isNullMap(cfTableFieldMap) && SetUtils.isNullList(cfTableFieldMap.get(tableName)))
		{
			Element cateElem = this.doc.getRootElement().getChild("category").getChild(category);
			if (cateElem != null)
			{
				Iterator<Element> tableIt = cateElem.getChildren("table").iterator();
				Element tableEle = null;
				while (tableIt.hasNext())
				{
					tableEle = tableIt.next();
					String tableName1 = tableEle.getAttributeValue("name");
					Iterator<Element> fieldIt = tableEle.getChildren("mapping").iterator();
					Element mappingEle = null;
					while (fieldIt.hasNext())
					{
						mappingEle = fieldIt.next();
						if (cfTableFieldMap.get(tableName1) == null)
						{
							cfTableFieldMap.put(tableName1, new ArrayList<ERPFieldMapping>());
						}
						Map<String, String> paramMap = this.getMapByElement(mappingEle);
						if (!this.existSameField(cfTableFieldMap.get(tableName1), paramMap.get("ERPField")))
						{
							cfTableFieldMap.get(tableName1).add(this.getFieldMapping(paramMap));
						}
					}
					mappingEle = null;
					fieldIt = null;
				}
			}
		}
		return cfTableFieldMap.get(tableName);
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
