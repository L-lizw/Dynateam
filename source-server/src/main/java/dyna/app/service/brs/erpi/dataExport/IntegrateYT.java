/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: IntegrateYT
 * wangweixia 2012-5-19
 */
package dyna.app.service.brs.erpi.dataExport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import dyna.app.service.brs.erpi.ERPYTTransferStub;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPYTOperationEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.security.signature.Signature;

/**
 * @author wangweixia
 * @author chega
 */
public class IntegrateYT extends IntegrateERP<ERPYTOperationEnum>
{
	private final String			master			= "master";
	private final String			detail			= "detail";
	private final String			plugin			= "plugin";
	private final String			bmtIndexColumn	= "bmt05";
	private Integer					optionListSize	= 0;
	private List<FoundationObject>	objectList		= null;

	/**
	 * @param context
	 * @param service
	 * @throws Exception
	 */
	public IntegrateYT(ERPYTTransferStub stub, Document document) throws Exception
	{
		super(stub, document);
		this.xmlPath = "conf/ytconf.xml";
		this.defaultDateFormat = "yyyy/MM/dd";
		this.init();
	}

	@Override
	public RecordSet getEachLocalS(FoundationObject obj, ERPYTOperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalS(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("bmd02"))
					{
						m.put("bmd02", "2");
					}
					if ("bmd_file".equalsIgnoreCase(tableName))
					{
						if (isEarlierThanNow(obj))
						{
							m.put("acttype", "DEL");
						}
					}
				}
			}
		}
		return rs;
	}

	@Override
	public RecordSet getEachLocalR(FoundationObject obj, ERPYTOperationEnum operation) throws ServiceRequestException
	{
		RecordSet rs = super.getEachLocalR(obj, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if (m.containsKey("bmd02"))
					{
						m.put("bmd02", "1");
					}
					if ("bmd_file".equalsIgnoreCase(tableName))
					{
						if (isEarlierThanNow(obj))
						{
							m.put("acttype", "DEL");
						}
					}
				}
			}
		}
		return rs;
	}

	@Override
	public RecordSet getEachGlobalS(FoundationObject obj, ERPYTOperationEnum operation) throws ServiceRequestException
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
					if (m.containsKey("bmd02"))
					{
						m.put("bmd02", "2");
					}
					if ("bmd_file".equalsIgnoreCase(tableName))
					{
						if (isEarlierThanNow(obj))
						{
							m.put("acttype", "DEL");
						}
					}
				}
			}
		}
		return rs;
	}

	//
	// @Override
	// public RecordSet getEachGlobalR(FoundationObject obj, ERPYTOperationEnum operation) throws
	// ServiceRequestException
	// {
	// RecordSet rs = super.getEachGlobalR(obj, operation);
	// if (rs != null && !SetUtils.isNullSet(rs.keySet()))
	// {
	// Iterator<String> tableIt = rs.keySet().iterator();
	// while (tableIt.hasNext())
	// {
	// String tableName = tableIt.next();
	// TableRecordData tableRecord = rs.get(tableName);
	// for (Map<String, String> m : tableRecord)
	// {
	// if (m.containsKey("bmd02"))
	// {
	// m.put("bmd02", "1");
	// }
	// if ("bmd_file".equalsIgnoreCase(tableName))
	// {
	// if (isEarlierThanNow(obj))
	// {
	// m.put("acttype", "DEL");
	// }
	// }
	// }
	// }
	// }
	// return rs;
	// }

	public Element getAccessEle() throws UnknownHostException
	{
		ERPParameter param = this.getParameter(null);
		Element access = new Element("Access");
		access.addContent(new Element("Authentication").setAttribute("user", param.getParamMap().get("user")).setAttribute("password", param.getParamMap().get("password")))
				.addContent(
						new Element("Connection").setAttribute("application", param.getParamMap().get("application")).setAttribute("source",
								InetAddress.getLocalHost().getHostAddress())).addContent(new Element("Organization").setAttribute("name", this.stub.factory))
				.addContent(new Element("Locale").setAttribute("language", param.getParamMap().get("language")));
		return access;
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
					}
				}
			}
			if (!StringUtils.isNullString(bodyTable))
			{
				Element bodyEle = new Element("Detail").setAttribute("name", bodyTable);
				recordSetEle.addContent(bodyEle);
				TableRecordData bodyData = data.getTableData(bodyTable, this.stub);
				if (bodyData != null)
				{
					for (int x = 0; x < bodyData.size(); x++)
					{
						Element recordEle = new Element("Record");
						bodyEle.addContent(recordEle);
						for (String field : bodyData.get(x).keySet())
						{
							recordEle.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", bodyData.get(x).get(field)));
						}
					}
				}
			}

			// YT要求插件位置每一个位置拆成一个Record传，即PLM的Designators字段要按,拆成多笔传输
			if (!StringUtils.isNullString(pluginTable))
			{
				Element pluginEle = new Element("Detail").setAttribute("name", pluginTable);
				recordSetEle.addContent(pluginEle);
				TableRecordData pluginData = data.getTableData(pluginTable, this.stub);
				if (pluginData != null)
				{
					for (int x = 0; x < pluginData.size(); x++)
					{
						if (!StringUtils.isNullString(pluginData.get(x).get(RESERVED_DESIGNATORS)))
						{
							List<String> designatorList = Arrays.asList(pluginData.get(x).get(RESERVED_DESIGNATORS).split(DELIMITER_DESIGNATOR));
							for (int z = 0; z < designatorList.size(); z++)
							{
								if (StringUtils.isNullString(designatorList.get(z)) == false)
								{
									Element recordEle = new Element("Record");
									pluginEle.addContent(recordEle);
									pluginData.get(x).put(bmtIndexColumn, String.valueOf(z + 1));
									for (String field : pluginData.get(x).keySet())
									{
										if (RESERVED_DESIGNATORS.equalsIgnoreCase(field))
										{
											recordEle.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", designatorList.get(z)));
										}
										else
										{
											recordEle.addContent(new Element("Field").setAttribute("name", field).setAttribute("value", pluginData.get(x).get(field)));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		valueList.clear();
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
	public Document getCreateDataXML(ERPYTOperationEnum operation, int totalCount, int index) throws Exception
	{
		// 写Access部分
		Element accessEle = this.getAccessEle();

		// 写Parameter部分
		Element paramEle = this.getParamEle(this.getParameter(operation.getCategory()), operation, totalCount, index);
		// 写Document部分
		Element docEle = this.getDocEle(this.getHeaderPackageOfData(operation.getCategory()), operation.getCategory(), totalCount, index);

		Element requestContentEle = new Element("RequestContent");
		requestContentEle.addContent(paramEle);
		requestContentEle.addContent(docEle);
		requestContentEle.addContent(this.getParamGroupEle());

		// 增加Request的根节点
		Element requestEle = new Element("Request");
		requestEle.addContent(accessEle);
		requestEle.addContent(requestContentEle);
		return new Document(requestEle);
	}

	/**
	 * @return
	 */
	private Element getParamGroupEle()
	{
		Element recordEle = new Element("Record");

		Element seqkeyFieldEle = new Element("Field");
		seqkeyFieldEle.setAttribute("name", "seqkey");
		seqkeyFieldEle.setAttribute("value", this.stub.jobId);

		Element seqTotCntFieldEle = new Element("Field");
		seqTotCntFieldEle.setAttribute("name", "seq_tot_cnt");
		seqTotCntFieldEle.setAttribute("value", this.optionListSize.toString());

		Element seqsubCntFieldEle = new Element("Field");
		seqsubCntFieldEle.setAttribute("name", "seq_sub_cnt");
		seqsubCntFieldEle.setAttribute("value", String.valueOf(this.stub.seq_sub_cnt));

		recordEle.addContent(seqkeyFieldEle);
		recordEle.addContent(seqTotCntFieldEle);
		recordEle.addContent(seqsubCntFieldEle);

		Element paramEle = new Element("ParameterGroup").addContent(recordEle);
		return paramEle;
	}

	/**
	 * @param erpFactory
	 * @param actionSchema
	 * @param totalvalue
	 * @param index
	 * @param jobId
	 * @return
	 */
	private Element getParamEle(ERPParameter parameter, ERPYTOperationEnum operation, int totalCount, int index)
	{
		Element recordEle = new Element("Record")
				.addContent(new Element("Field").setAttribute("name", "ws_name").setAttribute("value", StringUtils.convertNULLtoString(operation.getWs())))
				.addContent(new Element("Field").setAttribute("name", "datakey").setAttribute("value", StringUtils.convertNULLtoString(this.stub.getDataKey())))
				.addContent(new Element("Field").setAttribute("name", "tot_cnt").setAttribute("value", totalCount == index ? String.valueOf(totalCount) : ""))
				.addContent(new Element("Field").setAttribute("name", "sub_cnt").setAttribute("value", String.valueOf(index)));
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
	public String getOperationId(ERPYTOperationEnum operation)
	{
		return operation.getId();
	}

	@Override
	public String getOperationCategory(ERPYTOperationEnum operation)
	{
		return operation.getCategory();
	}

	@Override
	public List<ERPYTOperationEnum> getOperationList(boolean isMergeByCategory) throws ServiceRequestException
	{
		List<String> operations = this.stub.schema.getOperationList();
		List<String> categoryList = new ArrayList<String>();
		List<ERPYTOperationEnum> operationList = new ArrayList<ERPYTOperationEnum>();
		for (int i = 0; i < operations.size(); i++)
		{
			String id = operations.get(i);
			Map<String, String> attrMap = this.stub.getStubService().getOperationAttribute(ERPServerType.ERPTIPTOP, id);
			ERPYTOperationEnum e = ERPYTOperationEnum.getById(id);
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
	public String getOperationCrossServiceName(ERPYTOperationEnum operation)
	{
		final String methodName = operation.getMethod();
		return Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
	}

	@Override
	public String getOperationName(ERPYTOperationEnum operation)
	{
		return operation.getName();
	}

	@Override
	protected String getBOMHeaderTableName()
	{
		return "bma_file";
	}

	@Override
	public List<String> getReturnList(List<FoundationObject> list, List<String> factoryIdList, ERPServiceConfig serviceConfig, List<String> contentList) throws Exception
	{
		if (!SetUtils.isNullList(list))
		{
			objectList = new ArrayList<FoundationObject>();
			for (FoundationObject fo : list)
			{
				objectList.add(fo);
			}
		}

		Map<String, List<String>> paraMap = new HashMap<String, List<String>>();
		List<ERPYTOperationEnum> operations = null;
		ERPYTOperationEnum priceOperation = null;
		String originalXML = null;
		String returnXML = null;
		operations = getOperationList(false);
		if (!SetUtils.isNullList(operations))
		{
			for (int i = 0; i < operations.size(); i++)
			{
				ERPYTOperationEnum operation = operations.get(i);
				String category = this.getOperationCategory(operation);
				if (category.equals("Price") || category.equals("Cost") || category.equals("Quantity"))
				{
					priceOperation = operation;
					break;
				}
			}
			if (priceOperation != null)
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

				// 不同的工厂各生成一个xml文件，所以需要记录工厂索引。
				int factoryIdIndex = 0;
				List<String> resultXMLList = new ArrayList<String>();
				for (String factoryId : factoryIdList)
				{
					factoryIdIndex++;

					List<String> factoryIdTmpList = new ArrayList<String>();
					factoryIdTmpList.add(factoryId);

					originalXML = this.getPriceXML(list, factoryIdTmpList, paraMap);
					returnXML = ((ERPYTTransferStub) this.stub).callWS(originalXML, priceOperation, factoryIdIndex);
					returnXML = this.addFactoryIDToXML(returnXML, factoryId);
					resultXMLList.add(returnXML);
				}

				return resultXMLList;
			}
		}
		return null;
	}

	private String addFactoryIDToXML(String xmlContent, String factoryId) throws JDOMException, IOException
	{
		Document doc = XMLUtil.convertString2XML(xmlContent);
		Element root = doc.getRootElement();
		root.addContent(new Element("factoryid").setText(factoryId));
		return XMLUtil.convertXML2String(doc);
	}

	@Override
	protected String getPriceXML(List<FoundationObject> list, List<String> factoryIdList, Map<String, List<String>> map) throws Exception
	{
		String ipAddress = Signature.LOCAL_IP;
		try
		{
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
		}

		String factoryId = SetUtils.isNullList(factoryIdList) ? "" : factoryIdList.get(0);

		ERPParameter param = this.getParameter(null);

		StringBuffer result = new StringBuffer();
		result.append("<Request>");
		result.append("     <Access>");
		result.append("         <Authentication user=\"tiptop\" password=\" \" />");
		result.append("         <Connection application=\"PLM\" source=\"" + ipAddress + "\" />");
		result.append("         <Organization name=\"" + factoryId + "\" />");
		result.append("         <Locale language=\"" + param.getParamMap().get("language") + "\" />");
		result.append("     </Access>");
		result.append("     <RequestContent>");
		result.append("         <Parameter>");
		result.append("             <Record>");
		if (list == null || list.size() == 0)
		{
			throw new NullPointerException(this.stub.getStubService().getMSRM()
					.getMSRString("ID_CLIENT_NO_DATA", this.stub.getStubService().getUserSignature().getLanguageEnum().toString()));
		}
		if (list.size() == 1)
		{
			result.append("                 <Field name=\"condition\" value=\"ima01 = '" + list.get(0).getId() + "'\" />");
		}
		else
		{
			result.append("                 <Field name=\"condition\" value = \"ima01 in(");
			for (int i = 0; i < list.size(); i++)
			{
				if (i != 0)
				{
					result.append(",");
				}
				result.append("'");
				result.append(list.get(i).getId());
				result.append("'");
			}
			result.append(")\" />");
		}
		if (!SetUtils.isNullMap(map))
		{
			for (Iterator<List<String>> it = map.values().iterator(); it.hasNext();)
			{
				List<String> fieldNameList = it.next();
				for (String fieldName : fieldNameList)
				{
					result.append("     <Field name=\"" + fieldName + "\" />");
				}
			}
		}
		result.append("             </Record>");
		result.append("         </Parameter>");
		result.append("     </RequestContent>");
		result.append("</Request>");

		return result.toString();
	}

	@Override
	public List<FoundationObject> getReturnList(List<String> returnXMLList, List<String> contentList) throws Exception
	{
		List<FoundationObject> foList = new ArrayList<FoundationObject>();
		for (String returnXML : returnXMLList)
		{
			List<FoundationObject> list = this.getReturnList(returnXML);
			List<FoundationObject> notExistList = this.mergeList(this.objectList, list);
			if (!SetUtils.isNullList(notExistList))
			{
				for (FoundationObject obj : notExistList)
				{
					FoundationObject fo = new FoundationObjectImpl();
					fo.put("PlantCode", this.getFactoryIdFromXML(returnXML));
					fo.put("Flag", BooleanUtils.getBooleanStringYN(false));
					fo.put("FULLNAME$", obj.getFullName());
					foList.add(fo);
				}
			}
			foList.addAll(list);
		}
		this.objectList.clear();

		return foList;
	}

	private String getFactoryIdFromXML(String xml) throws JDOMException, IOException
	{
		Document doc = XMLUtil.convertString2XML(xml);
		Element root = doc.getRootElement();
		Element factoryEle = root.getChild("factoryid");

		return factoryEle == null ? null : factoryEle.getText();
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

	@SuppressWarnings("unchecked")
	private List<FoundationObject> getReturnList(String returnXML) throws Exception
	{
		List<FoundationObject> list = new ArrayList<FoundationObject>();
		Document doc = XMLUtil.convertString2XML(returnXML);
		Element root = doc.getRootElement();
		Element factoryEle = root.getChild("factoryid");
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
				Element recordEle = recordSetEle.getChild("Master").getChild("Record");
				List<Element> fieldEleList = recordEle.getChildren("Field");
				FoundationObject fo = new FoundationObjectImpl();
				if (!SetUtils.isNullList(fieldEleList))
				{
					for (Element fieldEle : fieldEleList)
					{
						fo.put(fieldEle.getAttributeValue("name"), fieldEle.getAttributeValue("value"));
					}
				}

				String id = (String) fo.get("ima01");
				fo.put(SystemClassFieldEnum.ID.getName(), id);
				if (StringUtils.isNullString(id))
				{
					throw new ServiceRequestException("ID_APP_VALUE_IS_EMPTY", "ima01");
				}

				List<FoundationObject> foList = this.stub.getStubService().getERPStub().getItem(id);
				if (SetUtils.isNullList(foList))
				{
					throw new ServiceRequestException("ID_DS_NO_DATA", null);
				}
				fo.put("FULLNAME$", foList.get(0).getFullName());
				fo.put("Flag", BooleanUtils.getBooleanStringYN(true));
				fo.put("PlantCode", factoryEle.getText());
				list.add(fo);
			}
		}
		return list;
	}

	@Override
	protected RecordSet getEachBOMData(FoundationObject end1Obj, List<BOMStructure> bomStructureList, BOMStructure parentBOMStructure, ERPYTOperationEnum operation)
			throws ServiceRequestException
	{
		/**
		 * 如果end1之前有过BOM结构，但是后来被删除了，有时需要告诉ERP，也同步删除该BOM结构
		 */
		isPrintEmptyBOMXML = true;
		RecordSet rs = super.getEachBOMData(end1Obj, bomStructureList, parentBOMStructure, operation);
		if (rs != null && !SetUtils.isNullSet(rs.keySet()))
		{
			Iterator<String> tableIt = rs.keySet().iterator();
			while (tableIt.hasNext())
			{
				String tableName = tableIt.next();
				TableRecordData tableRecord = rs.get(tableName);
				for (Map<String, String> m : tableRecord)
				{
					if ("bma_file".equalsIgnoreCase(tableName))
					{
						if (this.hasBOM(end1Obj))
						{
							m.put("acttype", "DEL");
						}
					}
				}
			}
		}
		return rs;
	}

	//
	// /**
	// * 若数据库中的字段“UHASBOM”值为“Y”且BOMView被删除或BOMView中没有子阶
	// *
	// * @param end1
	// * @param bomStructureList
	// * @return
	// */
	// protected boolean isSetBOMdel(FoundationObject end1, List<BOMStructure> bomStructureList)
	// {
	// boolean isDel = false;
	// if (end1 != null)
	// {
	// if (this.hasBOM(end1) && (this.bomView == null || SetUtils.isNullList(bomStructureList)))
	// {
	// return true;
	// }
	// }
	// return isDel;
	// }

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

	public void fillOptionListSize() throws ServiceRequestException
	{
		List<ERPYTOperationEnum> operationList = this.getOperationList(true);
		int tempSize = operationList.size();
		for (int k = 0; k < operationList.size(); k++)
		{
			ERPYTOperationEnum operation = operationList.get(k);
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
}
