/**
 * chega
 * 2013-1-6下午4:02:02
 */
package dyna.app.service.brs.erpi.dataExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;

import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPE10OperationEnum;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public class IntegrateE10 extends IntegrateERP<ERPE10OperationEnum>
{
	/**
	 * 分隔符
	 */
	private final String												separator			= "|";
	/**
	 * 外层map键值是category,内层map键值是tableName, 值是这个table下的所有拼接字符串
	 */
	private Map<String, Map<String, String>>							categoryFieldMap;
	/**
	 * 业务主键，外层Map的key值是category，内层Map的key值是tableName,value是primaryKey
	 */
	private Map<String, Map<String, String>>							primaryKeyMap;

	private String														tokenId				= null;
	// 外层Map的key值是category，中层Map的key值是tableName，内层Map的key值是ERPField
	private Map<String, List<Map<String, List<Map<String, String>>>>>	CFItemMap			= null;

	private final String												CLASSIFICATION		= "Classification";

	private final String												CLASSIFICATION_ITEM	= "ClassificationItem";
	// private final String FIELD = "field";
	// private final String CODEITEM = "codeitem";

	// private Map<UIField, ClassField> ui_classFieldMap = null;

	public IntegrateE10(ERPTransferStub<ERPE10OperationEnum> stub, Document document) throws Exception
	{
		super(stub, document);
		this.xmlPath = "conf/e10conf.xml";
		this.init();
		this.tokenId = this.stub.getUnusualParameter("TokenId");
		this.defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
		this.categoryFieldMap = new HashMap<String, Map<String, String>>();
		this.primaryKeyMap = new HashMap<String, Map<String, String>>();
		this.CFItemMap = new HashMap<String, List<Map<String, List<Map<String, String>>>>>();
		// this.ui_classFieldMap = new HashMap<UIField, ClassField>();
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Document getCreateDataXML(ERPE10OperationEnum operation, int totalCount, int index) throws Exception
	{
		if (!this.checkStrategy(operation.getCategory()))
		{
			throw new IllegalArgumentException(
					"category and strategy are not matched.(" + operation.getCategory() + ":" + this.getParameter(operation.getCategory()).getParamMap().get("strategy") + ")");
		}
		ERPParameter param = this.getParameter(null);
		List<RecordSet> valueList = this.getHeaderPackageOfData(operation.getCategory());
		Element parameterObjectEle = new Element("ParameterObject");
		Element tablesEle = new Element("Tables");
		parameterObjectEle.addContent(new Element("Type").setText("String")).addContent(new Element("Value").addContent(new Element("TransferData")
				.addContent(new Element("Action").setText(this.getParameter(operation.getCategory()).getParamMap().get("strategy"))).addContent(tablesEle)));
		Element requestContent = new Element("RequestContent").addContent(new Element("Username").setText(param.getParamMap().get("user")))
				.addContent(new Element("TokenId").setText(this.tokenId)).addContent(new Element("Language").setText(param.getParamMap().get("language")))
				.addContent(new Element("ParameterObjects").addContent(parameterObjectEle)
						.addContent(new Element("ParameterObject").addContent(new Element("Type").setText("String")).addContent(new Element("Value").setText("XML-PDM"))));

		Map<String, String> tableMap = this.getTables(operation.getCategory());
		Iterator<String> tableIt = tableMap.values().iterator();
		while (tableIt.hasNext())
		{
			String tableName = tableIt.next();
			Element rowsEle = new Element("Rows");
			if ("ITEM_FEATURE_VALUE".equalsIgnoreCase(tableName))
			{
				if (this.stub.schema.isExpandClassification() && !this.itemCFInfoMap.isEmpty())
				{
					Iterator<Map<String, String>> cfIt = this.itemCFInfoMap.values().iterator();
					while (cfIt.hasNext())
					{
						Map<String, String> itemCF = cfIt.next();
						rowsEle.addContent(new Element("Row").setText(this.getCreateDataCompoundString(itemCF)));
					}
				}
			}
			else
			{
				for (int i = 0; i < valueList.size(); i++)
				{
					TableRecordData tableRecordData = valueList.get(i).getTableData(tableName, this.stub);
					for (int j = 0; j < tableRecordData.size(); j++)
					{
						rowsEle.addContent(new Element("Row").setText(this.getCreateDataCompoundString(tableRecordData.get(j))));
					}
				}
			}
			tablesEle.addContent(new Element("Table").addContent(new Element("Name").setText(tableName))
					.addContent(new Element("Field").setText(this.categoryFieldMap.get(operation.getCategory()).get(tableName))).addContent(rowsEle));
			// 如果这个tableName下没有数据，则不传这个table的内容
			// if (rowsEle.getChild("Row") == null)
			// {
			// continue;
			// }
		}
		// clear temporary valueList for GC
		valueList.clear();
		return new Document(requestContent);
	}

	@Override
	public Document getCreateCFDataXML(ERPE10OperationEnum operation, int count, int j)
	{
		if (!this.checkStrategy(operation.getCategory()))
		{
			throw new IllegalArgumentException(
					"category and strategy are not matched.(" + operation.getCategory() + ":" + this.getParameter(operation.getCategory()).getParamMap().get("strategy") + ")");
		}
		List<Map<String, List<Map<String, String>>>> codeList = null;
		ERPParameter param = this.getParameter(null);
		Element parameterObjectEle = new Element("ParameterObject");
		Element tablesEle = new Element("Tables");
		parameterObjectEle.addContent(new Element("Type").setText("String")).addContent(new Element("Value").addContent(new Element("TransferData")
				.addContent(new Element("Action").setText(this.getParameter(operation.getCategory()).getParamMap().get("strategy"))).addContent(tablesEle)));
		Element requestContent = new Element("RequestContent").addContent(new Element("Username").setText(param.getParamMap().get("user")))
				.addContent(new Element("TokenId").setText(this.tokenId)).addContent(new Element("Language").setText(param.getParamMap().get("language")))
				.addContent(new Element("ParameterObjects").addContent(parameterObjectEle)
						.addContent(new Element("ParameterObject").addContent(new Element("Type").setText("String")).addContent(new Element("Value").setText("XML-PDM"))));

		Map<String, String> tableMap = this.getTables(operation.getCategory());
		if (count == 1)
		{
			codeList = this.CFItemMap.get(operation.getCategory());
		}
		else
		{
			codeList = getPackageData(operation.getCategory(), j, count);
		}
		Iterator<String> tableIt = tableMap.values().iterator();
		while (tableIt.hasNext())
		{
			String tableName = tableIt.next();
			Element rowsEle = new Element("Rows");
			if (!SetUtils.isNullList(codeList))
			{
				for (int i = 0; i < codeList.size(); i++)
				{
					List<Map<String, String>> rowList = codeList.get(i).get(tableName);
					if (!SetUtils.isNullList(rowList))
					{
						this.replaceBuildinField(rowList, this.stub);
						for (Map<String, String> map : rowList)
						{
							rowsEle.addContent(new Element("Row").setText(this.getCreateDataCompoundString(map)));
						}
					}
				}
				tablesEle.addContent(new Element("Table").addContent(new Element("Name").setText(tableName))
						.addContent(new Element("Field").setText(this.categoryFieldMap.get(operation.getCategory()).get(tableName))).addContent(rowsEle));
			}
		}
		return new Document(requestContent);
	}

	private List<Map<String, List<Map<String, String>>>> getPackageData(String category, int j, int count)
	{
		int batchSize = this.getBatchSize();
		int start = (j - 1) * batchSize;
		int end = start + batchSize;
		List<Map<String, List<Map<String, String>>>> result = new ArrayList<Map<String, List<Map<String, String>>>>();
		List<Map<String, List<Map<String, String>>>> codeList = this.CFItemMap.get(category);
		if (j > count || this.CFItemMap.get(category).size() > batchSize * count)
		{
			throw new IllegalStateException("out of data size(CFItemMap) in category: " + category);
		}
		if (j == count)
		{
			end = this.CFItemMap.get(category).size();
		}
		if (!SetUtils.isNullList(codeList))
		{
			for (int i = start; i < end; i++)
			{
				result.add(codeList.get(i));
			}
		}
		return result;
	}

	private void replaceBuildinField(List<Map<String, String>> record, ERPTransferStub stub)
	{

		if (stub != null && stub.currentOpeIdx != -1 && stub.currentXMLPackIdx != 0)
		{
			for (Map<String, String> map : record)
			{
				Iterator<String> keyIt = map.keySet().iterator();
				String key = null;
				while (keyIt.hasNext())
				{
					key = keyIt.next();
					if (map.get(key).contains(IntegrateERP.RESERVED_DATAKEY))
					{
						map.put(key, map.get(key).replace(IntegrateERP.RESERVED_DATAKEY, stub.getDataKey()));
					}
					if (map.get(key).contains(IntegrateERP.RESERVED_XMLPACKAGENO))
					{
						map.put(key, map.get(key).replace(IntegrateERP.RESERVED_XMLPACKAGENO, stub.getXMLPackNo()));
					}
				}
			}
		}
	}

	/**
	 * 目前E10要求PLM这边的策略限制死
	 * 
	 * @param category
	 * @return
	 */
	private boolean checkStrategy(String category)
	{
		if (ERP_ITEM.equals(category) || ERP_CODEITEM.equals(category) || ERP_FIELDGROUP.equals(category))
		{
			return "Adjust".equalsIgnoreCase(this.getParameter(category).getParamMap().get("strategy"));
		}
		if (ERP_BOM.equals(category))
		{
			return "InsertOnly".equalsIgnoreCase(this.getParameter(category).getParamMap().get("strategy"));
		}
		if (ERP_REPLACESUBSTITUTE.equals(category))
		{
			return "Sync".equalsIgnoreCase(this.getParameter(category).getParamMap().get("strategy"));
		}
		return false;
	}

	@Override
	protected List<ERPFieldMapping> getFieldFromMap(String category, String className, String tableName) throws ServiceRequestException
	{
		List<ERPFieldMapping> fieldList = super.getFieldFromMap(category, className, tableName);
		// 把拼接后的列名放入categoryFieldMap
		if (this.categoryFieldMap.get(category) == null)
		{
			Map<String, String> tableMap = new HashMap<String, String>();
			tableMap.put(tableName, this.getFieldString(fieldList));
			this.categoryFieldMap.put(category, tableMap);
		}
		else if (this.categoryFieldMap.get(category).get(tableName) == null)
		{
			this.categoryFieldMap.get(category).put(tableName, this.getFieldString(fieldList));
		}
		// 把primaryKey放入primaryKeyMap
		if (this.primaryKeyMap.get(category) == null)
		{
			this.primaryKeyMap.put(category, new HashMap<String, String>());
		}
		if (this.primaryKeyMap.get(category).get(tableName) == null)
		{
			Iterator<Element> tableIt = this.doc.getRootElement().getChild("category").getChild(category).getChild("tables").getChildren("table").iterator();
			while (tableIt.hasNext())
			{
				Element tableEle = tableIt.next();
				if (tableEle.getAttributeValue("name").equals(tableName))
				{
					this.primaryKeyMap.get(category).put(tableName, tableEle.getAttributeValue("primaryKey"));
					break;
				}
			}
		}
		return fieldList;
	}

	@Override
	protected List<ERPFieldMapping> getFieldFromMap(String category, String tableName) throws ServiceRequestException
	{
		List<ERPFieldMapping> fieldList = super.getFieldFromMap(category, tableName);
		// 把拼接后的列名放入categoryFieldMap
		if (this.categoryFieldMap.get(category) == null)
		{
			Map<String, String> tableMap = new HashMap<String, String>();
			tableMap.put(tableName, this.getFieldString(fieldList));
			this.categoryFieldMap.put(category, tableMap);
		}
		else if (this.categoryFieldMap.get(category).get(tableName) == null)
		{
			this.categoryFieldMap.get(category).put(tableName, this.getFieldString(fieldList));
		}
		// 把primaryKey放入primaryKeyMap
		if (this.primaryKeyMap.get(category) == null)
		{
			this.primaryKeyMap.put(category, new HashMap<String, String>());
		}
		if (this.primaryKeyMap.get(category).get(tableName) == null)
		{
			Iterator<Element> tableIt = this.doc.getRootElement().getChild("category").getChild(category).getChild("tables").getChildren("table").iterator();
			while (tableIt.hasNext())
			{
				Element tableEle = tableIt.next();
				if (tableEle.getAttributeValue("name").equals(tableName))
				{
					this.primaryKeyMap.get(category).put(tableName, tableEle.getAttributeValue("primaryKey"));
					break;
				}
			}
		}
		return fieldList;
	}

	private String getFieldString(List<ERPFieldMapping> fieldList)
	{
		StringBuilder sb = new StringBuilder();
		for (ERPFieldMapping field : fieldList)
		{
			sb.append(this.separator + field.getERPField());
		}
		sb.trimToSize();
		return sb.length() > 1 ? sb.toString().substring(1) : "";
	}

	private String getCreateDataCompoundString(Map<String, String> mapData)
	{
		StringBuilder sb = new StringBuilder();
		for (String s : mapData.values())
		{
			sb.append(this.separator + s);
		}
		return sb.toString().substring(1);
	}

	public void updateTokenId(String tokenId) throws IOException
	{
		// 如果当前的tokenId为或者E10传回的tokenId发生变化
		if (this.tokenId == null || !this.tokenId.equals(tokenId))
		{
			this.tokenId = tokenId;
			this.stub.setUnusualParameter("TokenId", tokenId);
		}
	}

	@Override
	protected boolean shuldSortFields()
	{
		return true;
	}

	/**
	 * {@inheritDoc} <br/>
	 * E10要求验证每个table中的主键值不能重复<br/>
	 * 验证是否所有类的字段配置都一样多
	 * 
	 * @throws ServiceRequestException
	 */
	@Override
	protected void afterProcessDataList() throws ServiceRequestException
	{
		super.afterProcessDataList();
		this.checkFieldAndValueCountEquals();
	}

	@Override
	public String getOperationId(ERPE10OperationEnum operation)
	{
		return operation.getId();
	}

	@Override
	public String getOperationCategory(ERPE10OperationEnum operation)
	{
		return operation.getCategory();
	}

	@Override
	public List<ERPE10OperationEnum> getOperationList(boolean isMergeByCategory) throws ServiceRequestException
	{
		List<String> operations = this.stub.schema.getOperationList();
		List<String> categoryList = new ArrayList<String>();
		List<ERPE10OperationEnum> operationList = new ArrayList<ERPE10OperationEnum>();
		for (int i = 0; i < operations.size(); i++)
		{
			String id = operations.get(i);
			Map<String, String> attrMap = this.stub.getStubService().getOperationAttribute(ERPServerType.ERPE10, id);
			ERPE10OperationEnum e = ERPE10OperationEnum.getById(id);
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
	protected boolean addToDataMap(String category, RecordSet recordSet)
	{
		if (this.checkPrimaryKey(category, recordSet))
		{
			return super.addToDataMap(category, recordSet);
		}
		return false;
	}

	private void checkFieldAndValueCountEquals()
	{
		Iterator<String> categoryIt = this.dataMap.keySet().iterator();
		while (categoryIt.hasNext())
		{
			String category = categoryIt.next();
			List<RecordSet> rsList = this.dataMap.get(category);
			for (int i = 1; i < rsList.size(); i++)
			{
				Iterator<String> tableIt = rsList.get(0).keySet().iterator();
				while (tableIt.hasNext())
				{
					String tableName = tableIt.next();
					if (SetUtils.isNullList(rsList.get(i).get(tableName)))
					{
						continue;
					}
					if (rsList.get(i).get(tableName).get(0).keySet().size() != rsList.get(0).get(tableName).get(0).keySet().size())
					{
						throw new IllegalArgumentException("all classes with category: " + category + " must have same amount of TableField");
					}
				}
			}
		}
	}

	@Override
	public int getXMLPackageCount(ERPE10OperationEnum operation)
	{
		int batchsize = this.getBatchSize();
		if (IntegrateERP.ERP_CODEITEM.equalsIgnoreCase(operation.getCategory()) || IntegrateERP.ERP_FIELDGROUP.equalsIgnoreCase(operation.getCategory()))
		{
			if (this.CFItemMap.get(operation.getCategory()) != null)
			{
				int size = this.CFItemMap.get(operation.getCategory()).size();
				if (size > 0)
				{
					if (batchsize > 0)
					{
						return size % batchsize == 0 ? size / batchsize : size / batchsize + 1;
					}
					else
					{
						return 1;
					}
				}
				return -1;
			}
			return -1;
		}
		else
		{
			return super.getXMLPackageCount(operation);
		}
	}

	/**
	 * <b>rs</b>是否满足primaryKey条件 <br/>
	 * 如果不重复返回true
	 * 
	 * @param category
	 * @param rs
	 * @return
	 */
	private boolean checkPrimaryKey(String category, RecordSet rs)
	{
		List<RecordSet> rsList = this.dataMap.get(category);
		if (rsList == null || SetUtils.isNullMap(rs))
		{
			return true;
		}
		Iterator<RecordSet> rsIt = rsList.iterator();
		while (rsIt.hasNext())
		{
			RecordSet existRS = rsIt.next();
			Iterator<String> tableIt = this.getTables(category).values().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData list = rs.get(tableName);
				if (SetUtils.isNullList(list) == false)
				{
					for (int i = list.size() - 1; i > -1; i--)
					{
						if (existRS.getTableData(tableName, null).exist(list.get(i)))
						{
							list.remove(i);
						}
					}
				}
			}
		}

		// // 只判断其中一个表即可
		// if (tableIt.hasNext())
		// {
		// String tableName = tableIt.next();
		// String primaryKey = this.primaryKeyMap.get(category).get(tableName);
		// String primaryValue = rs.getTableData(tableName, null).getCompoundFieldValue(primaryKey);
		// if (!StringUtils.isNullString(primaryKey))
		// {
		// Iterator<RecordSet> rsIt = rsList.iterator();
		// while (rsIt.hasNext())
		// {
		// RecordSet existRS = rsIt.next();
		// if (existRS.getTableData(tableName, null).getCompoundFieldValue(primaryKey).equals(primaryValue))
		// {
		// try
		// {
		// SystemObject existObj = this.getObject(existRS.getObjectGuid());
		// SystemObject foundationObj = this.getObject(rs.getObjectGuid());
		// if (existObj.getGuid().equals(foundationObj.getGuid()))
		// {
		// this.addDataWarn(this.getFoundationObjectDesp(foundationObj) + " and " +
		// this.getFoundationObjectDesp(existObj)
		// + " are same, only one was sent to ERP");
		// }
		// else
		// {
		// this.addDataWarn(this.getFoundationObjectDesp(foundationObj) + " and " +
		// this.getFoundationObjectDesp(existObj) + " has same primary key: "
		// + primaryKey + " in table: " + tableName + ", " + this.getFoundationObjectDesp(foundationObj) +
		// " is skipped");
		// }
		// }
		// catch (ServiceRequestException e)
		// {
		// DynaLogger.error(this.getClass().getCanonicalName() + "#checkPrimaryKey " + e.getMessage());
		// }
		// return false;
		// }
		// }
		// }
		// }
		return true;
	}

	@Override
	public List<String> getReturnList(List<FoundationObject> list, List<String> factoryId, ERPServiceConfig serviceConfig, List<String> contentList) throws Exception
	{
		Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
		List<ERPE10OperationEnum> operations = null;
		String getPriceORQuantity = null;
		ERPE10OperationEnum priceOperation = null;
		String originalXML = null;
		List<String> returnXMLList = new ArrayList<String>();
		operations = getOperationList(false);
		if (!SetUtils.isNullList(operations))
		{
			for (int i = 0; i < operations.size(); i++)
			{
				ERPE10OperationEnum operation = operations.get(i);
				String category = this.getOperationCategory(operation);
				if (category.equals("Price") || category.equals("Cost") || category.equals("Quantity"))
				{
					getPriceORQuantity = "Price";
					priceOperation = operation;
				}
			}
			if (getPriceORQuantity != null)
			{
				if (!SetUtils.isNullList(contentList))
				{
					for (String content : contentList)
					{
						Map<String, String> map;
						map = this.stub.getStubService().getERPStub().getContentAttribute(ERPServerType.valueOf(serviceConfig.getERPServerSelected()), content);
						if (paraMap.containsKey(map.get("operation")))
						{
							paraMap.get(map.get("operation")).add(content);
						}
						else
						{
							paraMap.put(map.get("operation"), new ArrayList<String>());
							paraMap.get(map.get("operation")).add(content);
						}
					}
				}
				originalXML = this.getPriceXML(list, factoryId, paraMap);
				String returnXML = this.stub.callWS(originalXML, priceOperation);
				returnXMLList.add(returnXML);
				return returnXMLList;
			}
		}
		return null;

	}

	@Override
	protected String getPriceXML(List<FoundationObject> list, List<String> factoryId, Map<String, List<String>> map)
	{
		StringBuffer result = new StringBuffer();
		ERPParameter param = this.getParameter(null);
		List<String> priceList = new ArrayList<String>();
		List<String> quantityList = new ArrayList<String>();
		if (!SetUtils.isNullList(map.get("getPrice")))
		{
			priceList.addAll(map.get("getPrice"));
		}
		if (!SetUtils.isNullList(map.get("getCost")))
		{
			priceList.addAll(map.get("getCost"));
		}
		if (!SetUtils.isNullList(map.get("getQuantity")))
		{
			quantityList.addAll(map.get("getQuantity"));
		}
		result.append("<RequestContent>");
		result.append("<Username>" + param.getParamMap().get("user") + "</Username>");
		result.append("<TokenId>" + this.stub.getUnusualParameter("TokenId") + "</TokenId>");
		result.append("<Language>" + param.getParamMap().get("language") + "</Language>");
		result.append("<ParameterObjects><ParameterObject><Type>String</Type><Value><Record><TimeFile ParaEndDate= \"\" ParaEndDate_add=\"\"/>");
		if (!SetUtils.isNullList(factoryId))
		{
			for (String string : factoryId)
			{
				result.append("<FilterItem ParaPlantCode =\"" + string + "\" />");
			}
		}
		else
		{
			result.append("<FilterItem ParaPlantCode =\"\" />");
		}
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject fo : list)
			{
				result.append("<Field ParaItemCode = \"" + this.filter(fo.getId()) + "\" />");
			}
		}
		else
		{
			result.append("<Field ParaItemCode = \"\" />");
		}
		if (!SetUtils.isNullList(quantityList))
		{
			for (String string : quantityList)
			{
				result.append("<FilterQty ParaQTYType =\"" + string + "\" />");
			}
		}
		else
		{
			result.append("<FilterQty ParaQTYType =\"\" />");
		}
		if (!SetUtils.isNullList(priceList))
		{
			for (String string : priceList)
			{
				result.append("<FilterPrice ParaPriceType =\"" + string + "\" />");
			}
		}
		else
		{
			result.append("<FilterPrice ParaPriceType =\"\" />");
		}
		result.append("</Record></Value></ParameterObject></ParameterObjects></RequestContent>");
		return result.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.erpi.dataExport.IntegrateERP#getReturnList(java.lang.String, java.util.List)
	 */
	@Override
	public List<FoundationObject> getReturnList(List<String> returnXML, List<String> contentList) throws Exception
	{
		List<FoundationObject> list = new ArrayList<FoundationObject>();
		String returnString = returnXML.get(0);
		Document doc = XMLUtil.convertString2XML(returnString);
		Element root = doc.getRootElement();
		Element responseContent = root.getChild("ResponseContent");

		List<Element> recordList = responseContent.getChild("Document").getChildren("Record");
		if (!SetUtils.isNullList(recordList))
		{
			for (Element e : recordList)
			{
				Element field = e.getChild("Field");
				String id = field.getAttributeValue("ItemCode");
				if (!SetUtils.isNullList(this.stub.getStubService().getERPStub().getItem(id)))
				{
					FoundationObject fo = this.stub.getStubService().getERPStub().getItem(id).get(0);
					fo.put("PlantCode", field.getAttribute("PlantCode").getValue());
					fo.put("Flag", field.getAttribute("Flag").getValue());
					for (String name : contentList)
					{
						fo.put(name, field.getAttribute(name).getValue());
					}
					list.add(fo);
				}
			}
		}
		return list;
	}

	@Override
	public String getOperationCrossServiceName(ERPE10OperationEnum operation)
	{
		return operation.getWs();
	}

	@Override
	public String getOperationName(ERPE10OperationEnum operation)
	{
		return operation.getName();
	}

	@Override
	protected String getBOMHeaderTableName()
	{
		return "BOM";
	};

	@Override
	protected RecordSet getEachItemData(FoundationObject end1Obj, ERPE10OperationEnum operation) throws ServiceRequestException
	{
		EMM emm = this.stub.getStubService().getEMM();
		if (this.checkObjectReturningSkip(end1Obj, ERP_ITEM, operation))
		{
			return null;
		}
		RecordSet recordSet = new RecordSet(end1Obj.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_ITEM).values().iterator();
		String tableName = null;
		// add2StampMap(ERP_ITEM, end1Obj);
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			// fieldList： 获得一个table下的所有字段匹配
			List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_ITEM, end1Obj.getObjectGuid().getClassName(), tableName);
			if (tableName.equalsIgnoreCase("ITEM_FEATURE_VALUE"))
			{
				if (this.stub.schema.isExpandClassification())
				{
					if (end1Obj.getClassificationGuid() == null)
					{
						continue;
					}
					else
					{
						CodeItemInfo info = emm.getCodeItem(end1Obj.getClassificationGuid());
						List<UIField> tempList = emm.listCFUIField(end1Obj.getClassificationGuid(), UITypeEnum.FORM);
						if (SetUtils.isNullList(tempList))
						{
							continue;
						}
						else
						{
							List<UIField> uifieldList = sortList(tempList);
							List<ClassField> fields = emm.listClassificationField(end1Obj.getClassificationGuid());
							Map<String, ClassField> classFieldMap = new HashMap<String, ClassField>();
							if (!SetUtils.isNullList(fields))
							{
								for (ClassField field : fields)
								{
									classFieldMap.put(field.getName(), field);
								}
							}
							// List<String> mergeFieldNameList = this.getMergeField(getCodeItemList(info));
							for (int i = 0; i < uifieldList.size(); i++)
							{
								UIField uiField = uifieldList.get(i);
								DynaLogger.debug("(" + uifieldList.get(i).getRowAmount() + "," + uifieldList.get(i).getColumnAmount() + ")");
								String size = String.valueOf(this.itemCFInfoMap.size());
								this.itemCFInfoMap.put(size, getItemCFInfoMapValue(end1Obj, info, uiField, classFieldMap.get(uiField.getName()), fieldList));
							}
							// recordSet.get("GI").get(0).put("FEATURE_GROUP_CODE", this.reNameCode(info));
						}
					}
				}
			}
			else
			{
				if (this.stub.schema.isExpandClassification())
				{
					if (end1Obj.getClassificationGuid() != null)
					{
						CodeItemInfo codeItem = this.stub.getStubService().getEMM().getCodeItem(end1Obj.getClassificationGuid());
						TableRecordData tableRecord = new TableRecordData(this.getItemInfoMapValue(end1Obj, fieldList, codeItem, operation));
						recordSet.put(tableName, tableRecord);
					}
					else
					{
						TableRecordData tableRecord = new TableRecordData(this.getObjectMapValue(end1Obj, fieldList, ERP_ITEM, operation));
						recordSet.put(tableName, tableRecord);
					}
				}
				else
				{
					TableRecordData tableRecord = new TableRecordData(this.getObjectMapValue(end1Obj, fieldList, ERP_ITEM, operation));
					recordSet.put(tableName, tableRecord);
				}
			}
		}
		return recordSet;

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
	private Map<String, String> getItemInfoMapValue(FoundationObject end1Obj, List<ERPFieldMapping> fieldList, CodeItemInfo codeItem, ERPE10OperationEnum operation)
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
				dataValue = this.getCFAttributeValue(field, codeItem);
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
	private String getCFAttributeValue(ERPFieldMapping field, CodeItemInfo codeItem) throws ServiceRequestException
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
				if (CLASSIFICATION.equalsIgnoreCase(fieldName) || CLASSIFICATION_ITEM.equalsIgnoreCase(fieldName))
				{
					return true;
				}
			}
		}
		return false;
	}

	private List<UIField> sortList(List<UIField> tempList)
	{
		List<UIField> result = new ArrayList<UIField>();
		List<UIField> temp = null;
		int maxRow = 0;
		int maxColumn = 0;
		maxRow = getMaxSize(tempList, true);
		for (int row = 0; row <= maxRow; row++)
		{
			temp = new ArrayList<UIField>();
			for (int j = 0; j < tempList.size(); j++)
			{
				if (tempList.get(j).getRowAmount() == row)
				{
					temp.add(tempList.get(j));
				}
			}
			if (!SetUtils.isNullList(temp))
			{
				maxColumn = getMaxSize(temp, false);
				for (int column = 0; column <= maxColumn; column++)
				{
					for (int k = 0; k < temp.size(); k++)
					{
						if (temp.get(k).getColumnAmount() == column)
						{
							result.add(temp.get(k));
						}
					}
				}
			}
		}
		return result;
	}

	private int getMaxSize(List<UIField> tempList, boolean isRow)
	{
		int max = 0;
		for (UIField field : tempList)
		{
			if (isRow)
			{
				if (field.getRowAmount() > max)
				{
					max = field.getRowAmount();
				}
			}
			else
			{
				if (field.getColumnAmount() > max)
				{
					max = field.getColumnAmount();
				}
			}
		}
		return max;
	}

	private List<CodeItemInfo> getCodeItemList(CodeItemInfo code) throws ServiceRequestException
	{
		List<CodeItemInfo> temp = null;
		List<CodeItemInfo> result = new ArrayList<CodeItemInfo>();
		EMM emm = this.stub.getStubService().getEMM();
		if (code.getCodeGuid() != null)
		{
			CodeObjectInfo codeObjectInfo = emm.getCode(code.getCodeGuid());
			if (codeObjectInfo != null)
			{
				temp = emm.listAllCodeItemInfoByMaster(codeObjectInfo.getGuid(), codeObjectInfo.getName());
				if (!SetUtils.isNullList(temp))
				{
					for (CodeItemInfo codeItem : temp)
					{
						List<UIField> uiList = emm.listCFUIField(codeItem.getGuid(), UITypeEnum.FORM);
						if (!SetUtils.isNullList(uiList))
						{
							result.add(codeItem);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 获得Item的分类信息
	 * 
	 * @param baseObj
	 * @param info
	 * @param uiField
	 * @param ui
	 * @param fieldList
	 * @param mergeFieldNameList
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getItemCFInfoMapValue(FoundationObject baseObj, CodeItemInfo info, UIField ui, ClassField classField, List<ERPFieldMapping> fieldList)
			throws ServiceRequestException
	{
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (fieldList == null)
		{
			return returnMap;
		}
		FoundationObject targetObject = getCFTarget(baseObj, info);
		for (int j = 0; j < fieldList.size(); j++)
		{
			ERPFieldMapping field = fieldList.get(j);
			String dataValue = null;
			if (this.isCFKeyField(field))
			{
				dataValue = this.getCFAttributeValue(field, info);
			}
			else if ("Field|Name".equalsIgnoreCase(field.getPLMField()))
			{
				dataValue = getFeatureName(classField);
			}
			else if ("Codeitem|Name".equalsIgnoreCase(field.getPLMField()))
			{
				if (ui.getType().equals(FieldTypeEnum.CODE))
				{
					dataValue = targetObject == null ? null : (String) targetObject.get(ui.getName());
					if (dataValue != null && dataValue.length() > 0)
					{
						CodeItemInfo code = this.stub.getStubService().getEMM().getCodeItem(dataValue);
						if (code != null)
						{
							dataValue = code.getName();
						}
						else
						{
							dataValue = null;
						}
					}
				}
				else if (ui.getType().equals(FieldTypeEnum.OBJECT))
				{
					dataValue = targetObject == null ? null : (String) targetObject.get(ui.getName());
					if (dataValue != null && dataValue.length() > 0)
					{
						String classKey = ui.getName();
						if (classKey.endsWith("$"))
						{
							classKey = classKey + "Class";
						}
						else
						{
							classKey = classKey + "$Class";
						}
						FoundationObject fo = this.stub.getStubService().getBOAS().getObjectByGuid(new ObjectGuid((String) targetObject.get(classKey), null, dataValue, null));
						if (fo != null)
						{
							dataValue = fo.getId();
						}
						else
						{
							dataValue = null;
						}
					}
				}
				else
				{
					Object object = targetObject == null ? null : targetObject.get(ui.getName());
					dataValue = object == null ? null : object.toString();
					if (ui.getType().equals(FieldTypeEnum.DATE) && dataValue != null)
					{
						dataValue = DateFormat.formatYMD(DateFormat.parse(dataValue));
					}
				}
			}
			else if ("Codeitem|Title".equalsIgnoreCase(field.getPLMField()))
			{
				if (ui.getType().equals(FieldTypeEnum.CODE))
				{
					dataValue = targetObject == null ? null : (String) targetObject.get(ui.getName());
					if (StringUtils.isGuid(dataValue))
					{
						CodeItemInfo code = this.stub.getStubService().getEMM().getCodeItem(dataValue);
						if (code != null)
						{
							dataValue = code.getTitle(this.stub.lang);
						}
						else
						{
							dataValue = null;
						}
					}
				}
				else if (ui.getType().equals(FieldTypeEnum.OBJECT))
				{
					dataValue = targetObject == null ? null : (String) targetObject.get(ui.getName());
					if (dataValue != null && dataValue.length() > 0)
					{
						String classKey = ui.getName();
						if (classKey.endsWith("$"))
						{
							classKey = classKey + "Class";
						}
						else
						{
							classKey = classKey + "$Class";
						}
						FoundationObject fo = this.stub.getStubService().getBOAS().getObjectByGuid(new ObjectGuid((String) targetObject.get(classKey), null, dataValue, null));
						if (fo != null)
						{
							dataValue = fo.getName();
						}
						else
						{
							dataValue = null;
						}
					}
				}
				else
				{
					Object object = targetObject == null ? null : targetObject.get(ui.getName());
					dataValue = object == null ? null : object.toString();
					if (ui.getType().equals(FieldTypeEnum.DATE) && dataValue != null)
					{
						dataValue = DateFormat.formatYMD(DateFormat.parse(dataValue));
					}

				}
			}
			else if ("ID$".equalsIgnoreCase(field.getPLMField()))
			{
				dataValue = baseObj.getId();
			}
			else
			{
				dataValue = field.getDefaultValue();
			}

			dataValue = this.formatValue(dataValue, field);
			this.checkFieldValue(dataValue, field, baseObj);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

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

	@Override
	protected RecordSet getEachLocalS(FoundationObject obj, ERPE10OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String table = tableIt.next();
				TableRecordData record = rs.get(table);
				for (Map<String, String> m : record)
				{
					if (m.containsKey("REPLACE_TYPE"))
					{
						m.put("REPLACE_TYPE", "2");
						return rs;
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
	protected RecordSet getEachGlobalS(FoundationObject obj, ERPE10OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachGlobalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String table = tableIt.next();
				TableRecordData record = rs.get(table);
				for (Map<String, String> m : record)
				{
					if (m.containsKey("REPLACE_TYPE"))
					{
						m.put("REPLACE_TYPE", "2");
						return rs;
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
	protected RecordSet getEachLocalR(FoundationObject obj, ERPE10OperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalR(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String table = tableIt.next();
				TableRecordData record = rs.get(table);
				for (Map<String, String> m : record)
				{
					if (m.containsKey("REPLACE_TYPE"))
					{
						m.put("REPLACE_TYPE", "1");
						return rs;
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

	// @Override
	// protected RecordSet getEachGlobalR(FoundationObject obj, ERPE10OperationEnum operation) throws
	// ServiceRequestException
	// {
	// RecordSet rs = super.getEachGlobalR(obj, operation);
	// if (rs != null && !SetUtils.isNullSet(rs.keySet()))
	// {
	// Iterator<String> tableIt = rs.keySet().iterator();
	// while (tableIt.hasNext())
	// {
	// String table = tableIt.next();
	// TableRecordData record = rs.get(table);
	// for (Map<String, String> m : record)
	// {
	// if (m.containsKey("REPLACE_TYPE"))
	// {
	// m.put("REPLACE_TYPE", "1");
	// return rs;
	// }
	// }
	// }
	// }
	// return rs;
	// }

	@Override
	protected void getCFGroupData(List<CodeItemInfo> codeItemList, ERPE10OperationEnum operation) throws ServiceRequestException
	{
		EMM emm = this.stub.getStubService().getEMM();
		String tableName = null;
		List<Map<String, List<Map<String, String>>>> categoryList = new ArrayList<Map<String, List<Map<String, String>>>>();
		Map<String, List<Map<String, String>>> codeMap = null;
		List<Map<String, String>> bodyList = null;
		for (CodeItemInfo code : codeItemList)
		{
			codeMap = new HashMap<String, List<Map<String, String>>>();
			Iterator<String> tableIt = this.getTables(ERP_FIELDGROUP).values().iterator();
			while (tableIt.hasNext())
			{
				bodyList = new ArrayList<Map<String, String>>();
				tableName = tableIt.next();
				// fieldList： 获得一个table下的所有字段匹配
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_FIELDGROUP, tableName);

				if (tableName.equalsIgnoreCase("FEATURE_GROUP"))
				{
					bodyList.add(getEachCFItem(code, fieldList, operation));
					codeMap.put(tableName, bodyList);
				}
				else
				{
					List<UIField> uiList = emm.listCFUIField(code.getGuid(), UITypeEnum.FORM);
					List<ClassField> fields = emm.listClassificationField(code.getGuid());
					Map<String, ClassField> classFieldMap = new HashMap<String, ClassField>();
					if (!SetUtils.isNullList(fields))
					{
						for (ClassField field : fields)
						{
							classFieldMap.put(field.getName(), field);
						}
					}
					for (UIField ui : uiList)
					{
						bodyList.add(getEachCFField(code, classFieldMap.get(ui.getName()), fieldList, operation));
					}
					codeMap.put(tableName, bodyList);
				}
			}
			categoryList.add(codeMap);
		}
		this.CFItemMap.put(operation.getCategory(), categoryList);
	}

	@Override
	protected void getCFfiedData(List<CodeItemInfo> codeItemList, ERPE10OperationEnum operation) throws ServiceRequestException
	{
		EMM emm = this.stub.getStubService().getEMM();
		String tableName = null;
		Map<String, List<CodeItemInfo>> mergeField_CodeItemListMap = this.getMergeField_CodeItemListMap(codeItemList);
		List<Map<String, List<Map<String, String>>>> categoryList = new ArrayList<Map<String, List<Map<String, String>>>>();
		Map<String, List<Map<String, String>>> codeMap = null;
		List<Map<String, String>> bodyList = null;
		List<String> headTempList = new ArrayList<String>();
		List<String> bodyTempList = new ArrayList<String>();
		for (CodeItemInfo code : codeItemList)
		{
			List<UIField> uiFieldList = emm.listCFUIField(code.getGuid(), UITypeEnum.FORM);
			List<ClassField> fields = emm.listClassificationField(code.getGuid());
			Map<String, ClassField> classFieldMap = new HashMap<String, ClassField>();
			if (!SetUtils.isNullList(fields))
			{
				for (ClassField field : fields)
				{
					classFieldMap.put(field.getName(), field);
				}
			}
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField field : uiFieldList)
				{
					// 分类属性的备注栏位信息不抛转到ERP
					if (field.getName().toUpperCase().startsWith("SEPARATOR"))
					{
						continue;
					}
					codeMap = new HashMap<String, List<Map<String, String>>>();
					ClassField classfield = classFieldMap.get(field.getName());
					String name = getFeatureName(classfield);
					Iterator<String> tableIt = this.getTables(ERP_CODEITEM).values().iterator();
					while (tableIt.hasNext())
					{
						bodyList = new ArrayList<Map<String, String>>();
						tableName = tableIt.next();
						// fieldList： 获得一个table下的所有字段匹配
						List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_CODEITEM, tableName);
						if (tableName.equalsIgnoreCase("FEATURE"))
						{
							if (headTempList.contains(name))
							{
								continue;
							}
							headTempList.add(name);
							bodyList.add(getEachCFFileld(code, field, fieldList, operation, classfield, mergeField_CodeItemListMap));
							codeMap.put(tableName, bodyList);
						}
						else
						{
							if (FieldTypeEnum.CODE.equals(field.getType()))
							{
								if (classfield != null)
								{
									List<CodeItemInfo> iteminfoList = null;
									if (bodyTempList.contains(name))
									{
										continue;
									}
									bodyTempList.add(name);
									iteminfoList = mergeField_CodeItemListMap.get(name);
									if (!SetUtils.isNullList(iteminfoList))
									{
										for (CodeItemInfo codeItem : iteminfoList)
										{
											bodyList.add(getEachCFFileld_D(code, classfield, codeItem, fieldList, operation));
										}
										codeMap.put(tableName, bodyList);
									}
								}
							}
						}
					}
					if (!codeMap.isEmpty())
					{
						categoryList.add(codeMap);
					}
				}
			}
		}
		this.CFItemMap.put(operation.getCategory(), categoryList);
	}

	/**
	 * 获得code类型字段的具体信息
	 * 
	 * @param code
	 * @param classfield
	 * @param field
	 * @param fieldList
	 * @param operation
	 * @param mergeFieldList
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getEachCFFileld_D(CodeItemInfo code, ClassField classField, CodeItemInfo codeitem, List<ERPFieldMapping> fieldList,
			ERPE10OperationEnum operation) throws ServiceRequestException
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
			if (isBuildinField(field.getPLMField()))
			{
				dataValue = this.getBuildinFieldValue(field.getPLMField(), field, null, null, operation);
			}
			else
			{
				if ("Field|Name".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = this.getFeatureName(classField);
				}
				else if ("Codeitem|Name".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = codeitem.getName();
				}
				else if ("Codeitem|Title".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = codeitem.getTitle(this.stub.lang);
				}
				else
				{
					dataValue = field.getDefaultValue();
				}
			}
			dataValue = this.formatValue(dataValue, field);
			checkFieldValue4Code(dataValue, field, code);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;

	}

	/**
	 * 获得每个字段的详细信息
	 * 
	 * @param code
	 * 
	 * @param field
	 * @param fieldList
	 * @param operation
	 * @param mergeField_CodeItemListMap
	 * @param mergeFieldList
	 * @param mergeField_CodeItemListMap
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getEachCFFileld(CodeItemInfo code, UIField uifield, List<ERPFieldMapping> fieldList, ERPE10OperationEnum operation, ClassField classField,
			Map<String, List<CodeItemInfo>> mergeField_CodeItemListMap) throws ServiceRequestException
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
			if (isBuildinField(field.getPLMField()))
			{
				dataValue = this.getBuildinFieldValue(field.getPLMField(), field, null, null, operation);
			}
			else
			{
				if ("Field|Name".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = getFeatureName(classField);
				}
				else if ("Field|Title".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = uifield.getTitle(this.stub.lang);
				}
				else if ("DATA_SIZE".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = this.getfieldSize(code, uifield, classField, mergeField_CodeItemListMap);
				}
				else if ("USE_METHOD".equalsIgnoreCase(field.getPLMField()))
				{
					if (uifield.getType().equals(FieldTypeEnum.CODE))
					{
						dataValue = "1";
					}
					else
					{
						dataValue = "3";
					}
				}
				else
				{
					dataValue = field.getDefaultValue();
				}
			}
			dataValue = this.formatValue(dataValue, field);
			checkFieldValue4Code(dataValue, field, code);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;

	}

	/**
	 * 根据字段类型，获取字段的长度
	 * 
	 * @param code
	 * 
	 * @param uifield
	 * @param mergeField_CodeItemListMap
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getfieldSize(CodeItemInfo code, UIField uifield, ClassField classField, Map<String, List<CodeItemInfo>> mergeField_CodeItemListMap)
			throws ServiceRequestException
	{
		FieldTypeEnum type = uifield.getType();
		EMM emm = this.stub.getStubService().getEMM();
		List<ClassField> classFieldList = null;
		int size = 0;
		switch (type)
		{
		case OBJECT:
			size = 256;
			break;
		case DATETIME:
			size = 20;
			break;
		case DATE:
			size = 10;
			break;
		case STRING:
		case INTEGER:
			classFieldList = emm.listClassificationField(code.getGuid());
			if (!SetUtils.isNullList(classFieldList))
			{
				for (ClassField field : classFieldList)
				{
					if (field.getName().equalsIgnoreCase(uifield.getName()))
					{
						return field.getFieldSize() == null ? String.valueOf(ClassField.defaultCharLength) : field.getFieldSize();
					}
				}
			}
			break;
		case FLOAT:
			String floatSize = "";
			classFieldList = emm.listClassificationField(code.getGuid());
			if (!SetUtils.isNullList(classFieldList))
			{
				for (ClassField field : classFieldList)
				{
					if (field.getName().equalsIgnoreCase(uifield.getName()))
					{
						floatSize = field.getFieldSize() == null ? String.valueOf(ClassField.defaultCharLength) : field.getFieldSize();
						break;
					}
				}
				String[] array = floatSize.split(",");
				if (array.length > 0)
				{
					return array[0];
				}
			}
			break;
		case BOOLEAN:
			size = 1;
			break;
		case CODE:
			size = getcodeSize(emm, code, classField, mergeField_CodeItemListMap);
		}
		return String.valueOf(size);
	}

	private int getcodeSize(EMM emm, CodeItemInfo code, ClassField classfield, Map<String, List<CodeItemInfo>> mergeField_CodeItemListMap) throws ServiceRequestException
	{
		int maxSize = 0;
		List<CodeItemInfo> itemList = null;
		if (classfield != null)
		{
			if (mergeField_CodeItemListMap == null)
			{
				CodeObjectInfo object = emm.getCodeByName(classfield.getTypeValue());
				if (object != null)
				{
					itemList = emm.listAllCodeItemInfoByMaster(object.getGuid(), object.getName());
				}
			}
			else
			{
				itemList = mergeField_CodeItemListMap.get(this.getFeatureName(classfield));
			}
			if (!SetUtils.isNullList(itemList))
			{
				for (CodeItemInfo item : itemList)
				{
					int codelength = StringUtils.convertNULLtoString(item.getCode()).length();
					int titlelength = StringUtils.convertNULLtoString(item.getTitle(this.stub.lang)).length();
					maxSize = maxSize > codelength ? maxSize : codelength;
					maxSize = maxSize > titlelength ? maxSize : titlelength;
				}
			}

		}
		return maxSize;
	}

	/**
	 * 获得每个codeItem的formUI上的每个字段基本信息
	 * 
	 * @param code
	 * @param classField
	 * @param fieldList
	 * @param operation
	 * @param mergeField
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getEachCFField(CodeItemInfo code, ClassField classField, List<ERPFieldMapping> fieldList, ERPE10OperationEnum operation)
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
			if (isBuildinField(field.getPLMField()))
			{
				dataValue = this.getBuildinFieldValue(field.getPLMField(), field, null, null, operation);
			}
			else
			{
				if (this.isCFKeyField(field))
				{
					dataValue = this.getCFAttributeValue(field, code);
				}
				else if ("Field|Name".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = getFeatureName(classField);
				}
				else
				{
					dataValue = field.getDefaultValue();
				}
			}
			dataValue = this.formatValue(dataValue, field);
			checkFieldValue4Code(dataValue, field, code);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

	/**
	 * 获得每个codeitem的信息
	 * 
	 * @param code
	 * @param fieldList
	 * @param operation
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, String> getEachCFItem(CodeItemInfo code, List<ERPFieldMapping> fieldList, ERPE10OperationEnum operation) throws ServiceRequestException
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
			if (isBuildinField(field.getPLMField()))
			{
				dataValue = this.getBuildinFieldValue(field.getPLMField(), field, null, null, operation);
			}
			else
			{
				if (this.isCFKeyField(field))
				{
					dataValue = this.getCFAttributeValue(field, code);
				}
				else if ("Classification|Title".equalsIgnoreCase(field.getPLMField()))
				{
					dataValue = code.getTitle(this.stub.lang);
				}
				else
				{
					dataValue = field.getDefaultValue();
				}
			}
			dataValue = this.formatValue(dataValue, field);
			checkFieldValue4Code(dataValue, field, code);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

	// /**
	// * 获得需要合并后的字段名（描述中未去除[]）
	// *
	// * @param codeItemList
	// * @return
	// * @throws ServiceRequestException
	// */
	// private List<String> getMergeField(List<CodeItemInfo> codeItemList) throws ServiceRequestException
	// {
	// List<String> result = new ArrayList<String>();
	// Map<String, List<UIField>> tempMap = getMergeFieldTempMap(codeItemList);
	// if (!tempMap.isEmpty())
	// {
	// Iterator<String> iterator = tempMap.keySet().iterator();
	// while (iterator.hasNext())
	// {
	// String key = iterator.next();
	// if (tempMap.get(key).size() > 1)
	// {
	// result.add(key);
	// }
	// }
	// }
	// return result;
	// }

	// /**
	// * 获得Map<field.Name+field.description,List<UIField>>
	// *
	// * @param codeItemList
	// * @return
	// * @throws ServiceRequestException
	// */
	// private Map<String, List<UIField>> getMergeFieldTempMap(List<CodeItemInfo> codeItemList) throws
	// ServiceRequestException
	// {
	// EMM emm = this.stub.getStubService().getEMM();
	// Map<String, List<UIField>> tempMap = new HashMap<String, List<UIField>>();
	// Map<CodeItemInfo, List<UIField>> codeUIFieldMap = getCodeFiledMap(codeItemList);
	// if (SetUtils.isNullList(codeItemList))
	// {
	// return null;
	// }
	// for (CodeItemInfo code : codeItemList)
	// {
	// List<UIField> uiFieldList = codeUIFieldMap.get(code);// 从缓存的code类型uifields中取值
	// if (!SetUtils.isNullList(uiFieldList))
	// {
	// for (UIField field : uiFieldList)
	// {
	// List<ClassField> fields = emm.listClassificationField(code.getGuid());
	// if (!SetUtils.isNullList(fields))
	// {
	// for (ClassField classfield : fields)
	// {
	// if (classfield.getName().equalsIgnoreCase(field.getName())) // 获得uifield对应的classfield
	// {
	// // 缓存code类型的uifield与对应的classfield
	// if (this.ui_classFieldMap.get(field) == null)
	// {
	// this.ui_classFieldMap.put(field, classfield);
	// }
	// if (classfield.getDescription() != null && classfield.getDescription().contains("[") &&
	// classfield.getDescription().contains("]"))
	// {
	// String key = classfield.getName() + classfield.getDescription();
	// if (tempMap.get(key) != null)
	// {
	// tempMap.get(key).add(field);
	// }
	// else
	// {
	// List<UIField> tempList = new ArrayList<UIField>();
	// tempList.add(field);
	// tempMap.put(key, tempList);
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// return tempMap;
	// }

	/**
	 * 缓存合并字段对应的codeItemList
	 * 
	 * @param codeItemList
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<String, List<CodeItemInfo>> getMergeField_CodeItemListMap(List<CodeItemInfo> codeItemList) throws ServiceRequestException
	{
		Map<String, List<CodeItemInfo>> result = new HashMap<String, List<CodeItemInfo>>();
		Map<String, List<String>> nameTempMap = new HashMap<String, List<String>>();
		EMM emm = this.stub.getStubService().getEMM();

		for (CodeItemInfo code : codeItemList)
		{
			List<UIField> uiFieldList = emm.listCFUIField(code.getGuid(), UITypeEnum.FORM);
			List<ClassField> fields = emm.listClassificationField(code.getGuid());
			Map<String, ClassField> classFieldMap = new HashMap<String, ClassField>();
			if (!SetUtils.isNullList(fields))
			{
				for (ClassField field : fields)
				{
					if (FieldTypeEnum.CODE == field.getType())
					{
						classFieldMap.put(field.getName(), field);
					}
				}
			}
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField field : uiFieldList)
				{
					if (classFieldMap.containsKey(field.getName()))
					{
						ClassField classfield = classFieldMap.get(field.getName());
						String featureName = getFeatureName(classfield);
						List<String> tempNameList = null;
						if (!SetUtils.isNullList(nameTempMap.get(featureName)))
						{
							tempNameList = nameTempMap.get(featureName);
						}
						else
						{
							tempNameList = new ArrayList<String>();
						}
						List<CodeItemInfo> iteminfoList = null;
						if (!SetUtils.isNullList(result.get(featureName)))
						{
							iteminfoList = result.get(featureName);
						}
						else
						{
							iteminfoList = new ArrayList<CodeItemInfo>();
						}
						if (classfield != null && classfield.getTypeValue() != null)
						{
							CodeObjectInfo item = emm.getCodeByName(classfield.getTypeValue());
							if (item != null)
							{
								List<CodeItemInfo> tempList = emm.listAllCodeItemInfoByMaster(item.getGuid(), item.getName());
								if (!SetUtils.isNullList(tempList))
								{
									for (CodeItemInfo codeItem : tempList)
									{
										if (!tempNameList.contains(codeItem.getName()))
										{
											iteminfoList.add(codeItem);
											tempNameList.add(codeItem.getName());
										}
									}
								}
							}
						}
						if (!SetUtils.isNullList(iteminfoList))
						{
							result.put(featureName, iteminfoList);
						}
						if (!SetUtils.isNullList(tempNameList))
						{
							nameTempMap.put(featureName, tempNameList);
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 以codeitem为key，缓存对应的code类型uifields
	 * 
	 * @param codeItemList
	 * @return
	 * @throws ServiceRequestException
	 */
	private Map<CodeItemInfo, List<UIField>> getCodeFiledMap(List<CodeItemInfo> codeItemList) throws ServiceRequestException
	{
		Map<CodeItemInfo, List<UIField>> codeUIFieldMap = new HashMap<CodeItemInfo, List<UIField>>();
		EMM emm = this.stub.getStubService().getEMM();
		if (!SetUtils.isNullList(codeItemList))
		{
			for (CodeItemInfo code : codeItemList)// 以codeitem为key，缓存对应的uifields
			{
				List<UIField> codeFieldList = new ArrayList<UIField>();
				List<UIField> list = new ArrayList<UIField>();
				list = emm.listCFUIField(code.getGuid(), UITypeEnum.FORM);
				for (UIField field : list)
				{
					if (field.getType().equals(FieldTypeEnum.CODE))
					{
						codeFieldList.add(field);
					}
				}
				if (!SetUtils.isNullList(codeFieldList))
				{
					codeUIFieldMap.put(code, codeFieldList);// 以codeitem为key，缓存对应的code类型uifields
				}
			}
		}
		return codeUIFieldMap;
	}

	/**
	 * 获取字段描述[]中的值
	 * 
	 * @param param
	 * @return
	 */
	private String getDescriptionContent(String param)
	{
		String result = null;
		result = param.substring(param.indexOf("[") + 1, param.indexOf("]"));
		return result;
	}

	/**
	 * 检查数据合法性
	 * 
	 * @param dataValue
	 * @param field
	 * @return
	 */
	protected void checkFieldValue4Code(String dataValue, ERPFieldMapping field, CodeItemInfo object)
	{
		if (dataValue == null)
		{
			dataValue = "";
		}
		boolean isMandatory = field.isMandatory();
		String ERPField = field.getERPField();
		int maxLength = field.getMaxLength();
		if ("".equals(dataValue))
		{
			if (isMandatory)
			{
				throw new InvalidDataException("ERPField: " + ERPField + " is mandatory, but the value is null in " + this.getFoundationObjectDesp4Code(object));
			}
			else
			{
				return;
			}
		}
		if (!field.getDataType().validate(dataValue, field, this.getBooleanCombinationReg()))
		{
			throw new InvalidDataException("dataType validate error. data: " + dataValue + ", type: " + field.getDataType().getType());
		}
		if ((maxLength != 0) && (dataValue.length() > maxLength))
		{
			throw new InvalidDataException(
					"ERPField: " + ERPField + "'s maxLength is " + maxLength + ", but the value is " + dataValue + " in " + this.getFoundationObjectDesp4Code(object));
		}
	}

	public String getFoundationObjectDesp4Code(CodeItemInfo object)
	{
		return (String) object.getCode() + "[" + StringUtils.convertNULLtoString(object.getDescription()) + "]";
	}

	private String getFeatureName(ClassField classfield)
	{
		if (classfield == null)
		{
			return null;
		}
		return StringUtils.isNullString(classfield.getPublicFieldInERP()) ? classfield.getName() : classfield.getPublicFieldInERP();
	}

}
