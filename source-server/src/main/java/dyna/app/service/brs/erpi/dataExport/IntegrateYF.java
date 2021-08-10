package dyna.app.service.brs.erpi.dataExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

import dyna.app.service.brs.erpi.ERPYFTransferStub;
import dyna.app.service.brs.erpi.cross.util.XMLUtil;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPYFOperationEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * 
 * @author chega
 */

public class IntegrateYF extends IntegrateERP<ERPYFOperationEnum>
{
	private final String					PLMSystemName	= "PDM";
	private final String					ObjectID		= "ObjectID";
	private final String					separator		= "|";
	private final String					filePath;
	private final String					sendFilePath;
	public final String						SetData			= "SetData";
	public final String						GetTemplate		= "GetTemplate";
	public final String						GetObjectList	= "GetObjectList";
	public final String						GetData			= "GetData";
	public final String						GetCalculate	= "GetCalculate";
	public final String						Help			= "Help";
	public final String						GetJobStatus	= "GetJobStatus";
	// YF在生成xml文件的时候需要拼接列，因此在生成数据时候就把相应的category和FieldList存储起来
	private final Map<String, String>	categoryFieldMap;
	private boolean							deleteTempFile	= true;

	public IntegrateYF(ERPYFTransferStub stub, Document document) throws Exception
	{
		super(stub, document);
		this.xmlPath = "conf" + File.separator + "yfconf.xml";
		this.init();
		this.defaultDateFormat = "yyyy-MM-dd";
		this.filePath = this.getParameter(null).getParamMap().get("filePath");
		this.sendFilePath = this.getParameter(null).getParamMap().get("sendFilePath");
		this.deleteTempFile = "true".equalsIgnoreCase(this.getParameter(null).getParamMap().get("deleteTempFile"));
		this.categoryFieldMap = new HashMap<String, String>();
	}

	@Override
	protected RecordSet getEachBOMData(FoundationObject end1Obj, List<BOMStructure> bomStructureList, BOMStructure parentBOMStructure, ERPYFOperationEnum operation)
			throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(this.bomView, ERP_BOM, operation))
		{
			return null;
		}
		if (bomStructureList.isEmpty() && !this.isContinEmptyBOMXML())
		{
			if (!this.isPrintEmptyBOMXML)
			{
				return RecordSet.emptySet(this.getMockObjectGuid(null));
			}
		}
		ObjectGuid objGuid = bomStructureList.isEmpty() ? this.getMockObjectGuid(end1Obj) : this.bomView.getObjectGuid();
		RecordSet recordSet = new RecordSet(objGuid);
		String tableName = null;
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
					tableRecord.add(this.getObjectMapValue(bomStructureList.get(i), fieldList, ERP_BOM, operation));
				}
				recordSet.put(tableName, tableRecord);
			}
		}
		else
		{
			boolean isEmptyBOM = false;
			isEmptyBOM = this.hasBOM(end1Obj);
			if (isEmptyBOM)
			{
				Iterator<String> tableIt = this.getTables(ERP_BOM).values().iterator();
				while (tableIt.hasNext())
				{
					tableName = tableIt.next();
					this.getFieldFromMap(ERP_BOM, this.getArbitraryBOMClassName(), tableName);
				}

				tableName = this.getBOMHeaderTableName();
				TableRecordData tableRecord = new TableRecordData();
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_BOM, this.getArbitraryBOMClassName(), tableName);
				// fieldList = this.removeEnd1Prefix(fieldList);
				tableRecord.add(this.getObjectMapValue(end1Obj, fieldList, ERP_BOM, operation, isEmptyBOM));
				recordSet.put(tableName, tableRecord);
			}
		}
		return recordSet;

	}

	private Map<String, String> getObjectMapValue(SystemObject baseObj, List<ERPFieldMapping> fieldList, String category, ERPYFOperationEnum operation,
			boolean isEmptyBOM) throws ServiceRequestException
	{
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (isEmptyBOM)
		{
			if (fieldList == null)
			{
				return returnMap;
			}
			for (int j = 0; j < fieldList.size(); j++)
			{
				ERPFieldMapping field = fieldList.get(j);
				String dataValue = this.getFieldValue4YFEmptyBOM(baseObj, field, category, operation);
				// this.checkFieldValue(dataValue, field, baseObj);空BOM时不检查数据合法性
				returnMap.put(field.getERPField(), dataValue);
			}
		}
		else
		{
			return super.getObjectMapValue(baseObj, fieldList, category, operation);
		}
		return returnMap;
	}

	private String getFieldValue4YFEmptyBOM(SystemObject baseObj, ERPFieldMapping field, String category, ERPYFOperationEnum operation) throws ServiceRequestException
	{
		String resultValue = "";
		if(!field.isShowDefault())
		{
			if (!StringUtils.isNullString(field.getPLMField()))
			{
				List<JointField> fieldList = JointField.getFiels(field.getPLMField());
				for (JointField jointField : fieldList)
				{
					SystemObject targetObj = baseObj;
					Object dataValue = null;
					if (this.isBuildinField(jointField.getFieldName()))
					{
						dataValue = this.getBuildinFieldValue(jointField.getName(), field, category, baseObj, operation);
					}
					else
					{
						if (jointField.isEnd1Field())
						{
							String PLMField = jointField.getName().replaceAll("(?i)End1\\$", "");
							dataValue = targetObj.get(PLMField);
						}
						else
						{
							dataValue = "";
						}
					}
					resultValue += (dataValue == null ? "" : dataValue.toString());
				}
			}
			if (StringUtils.isNullString(resultValue))
			{
				resultValue = field.getDefaultValue();
			}
		}
		resultValue = this.formatValue(resultValue, field);
		return resultValue;
	}

	@Override
	protected List<ERPFieldMapping> getFieldFromMap(String category, String className, String tableName) throws ServiceRequestException
	{
		List<ERPFieldMapping> fieldList = super.getFieldFromMap(category, className, tableName);
		if (this.categoryFieldMap.get(category) == null)
		{
			this.categoryFieldMap.put(category, this.getFieldString(fieldList));
		}
		return fieldList;
	}

	/**
	 * {@inheritDoc}<br/>
	 * YF中要求父类和子类配置的字段数必须相等
	 * 
	 * @throws ServiceRequestException
	 */
	@Override
	protected void afterProcessDataList() throws ServiceRequestException
	{
		super.afterProcessDataList();
		this.checkFieldAndValueCountEquals();
	}

	/**
	 * 易飞是通过中间文件来传的，没有分批概念
	 */
	@Override
	public int getBatchSize()
	{
		return 0;
	}

	/**
	 * 易飞是通过中间文件来传的，没有分批概念
	 */
	@Override
	public int getBOMBatchSize()
	{
		return 0;
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
					if (rsList.get(i).get(tableName).get(0).keySet().size() != rsList.get(0).get(tableName).get(0).keySet().size())
					{
						throw new IllegalArgumentException("all classes with category: " + category + " must have same amount of TableField");
					}
				}
			}
		}
	}

	@Override
	protected boolean shuldSortFields()
	{
		return true;
	}

	@Override
	public Document getCreateDataXML(ERPYFOperationEnum operation, int totalCount, int index) throws Exception
	{
		String tableName = this.getTableNameById(operation.getCategory(), this.ObjectID);
		if (null == this.categoryFieldMap.get(operation.getCategory()))
		{
			throw new ServiceRequestException("ID_APP_EXP_NODATA", null, null, null);
		}
		Element dataSetEle = new Element("DataSet").setAttribute("Field", this.categoryFieldMap.get(operation.getCategory()));
		Element serviceEle = new Element("Service").setAttribute("Name", this.SetData).addContent(new Element("ServiceId").setText(this.stub.getDataKey()))
				.addContent(new Element("ObjectID").setText(tableName))
				.addContent(new Element("Operate").setText(this.getParameter(operation.getCategory()).getParamMap().get("strategy")))
				.addContent(new Element("Data").addContent(dataSetEle));
		List<RecordSet> valueList = this.getHeaderPackageOfData(operation.getCategory());
		for (int i = 0; i < valueList.size(); i++)
		{
			TableRecordData mapList = valueList.get(i).getTableData(tableName, this.stub);
			if (mapList != null)
			{
				for (int j = 0; j < mapList.size(); j++)
				{
					dataSetEle.addContent(new Element("Row").setAttribute("Data", this.getCreateDataCompoundString(mapList.get(j))));
				}
			}
		}

		final String fileName = this.stub.getXMLPackNo();
		File file = new File(this.getAbsoluteFilePath(fileName));
		BufferedWriter bw = null;
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "utf-8"));
			bw.write(XMLUtil.convertXML2String(new Document(serviceEle), true, true));
			bw.flush();
		}
		finally
		{
			if (bw != null)
			{
				try
				{
					bw.close();
				}
				catch (IOException e)
				{
					DynaLogger.error(e);
				}
			}
		}

		return this.getNotifyXML(operation, this.SetData, fileName);
	}

	private Document getNotifyXML(ERPYFOperationEnum operation, String serviceName, String fileName) throws ServiceRequestException
	{
		Document doc = new Document();
		DocType docType = new DocType("SC");
		docType.setInternalSubset("<!ENTITY stdd \"&amp;stdd;\">");
		doc.addContent(docType);
		Element stdEle = new Element("STD_IN").setAttribute("Origin", this.PLMSystemName).addContent(new Element("Factory").setText(this.stub.factory))
				.addContent(new Element("BuildXMLByMultiResource").setText("Y")).addContent(new Element("WaitingForResult").setText("N"))
				.addContent(new Element("User").setText("DS"))
				.addContent(new Element("Service").setAttribute("_NodeType", "File").setAttribute("FileName", this.sendFilePath + File.separator + fileName + ".xml"));
		doc.addContent(stdEle);
		return doc;
	}

	public String getAbsoluteFilePath(String fileName)
	{
		return this.filePath + File.separator + fileName + ".xml";
	}

	public boolean shouldDeleteTempFile()
	{
		return this.deleteTempFile;
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

	private String getFieldString(List<ERPFieldMapping> fieldList)
	{
		StringBuilder sb = new StringBuilder();
		for (ERPFieldMapping field : fieldList)
		{
			sb.append(this.separator + field.getERPField());
		}
		return sb.toString().substring(1);
	}

	@Override
	public String getOperationId(ERPYFOperationEnum operation)
	{
		return operation.getId();
	}

	@Override
	public String getOperationCategory(ERPYFOperationEnum operation)
	{
		return operation.getCategory();
	}

	@Override
	public List<ERPYFOperationEnum> getOperationList(boolean isMergeByCategory) throws ServiceRequestException
	{
		List<String> operations = this.stub.schema.getOperationList();
		List<String> categoryList = new ArrayList<String>();
		List<ERPYFOperationEnum> operationList = new ArrayList<ERPYFOperationEnum>();
		for (int i = 0; i < operations.size(); i++)
		{
			String id = operations.get(i);
			Map<String, String> attrMap = this.stub.getStubService().getOperationAttribute(ERPServerType.ERPYF, id);
			ERPYFOperationEnum e = ERPYFOperationEnum.getById(id);
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
	public String getOperationCrossServiceName(ERPYFOperationEnum operation)
	{
		return operation.getWs();
	}

	@Override
	public String getOperationName(ERPYFOperationEnum operation)
	{
		return operation.getName();
	}

	@Override
	protected String getBOMHeaderTableName()
	{
		return "PBOM";
	};

	/**
	 * no package concept in YF, all data are in one package.
	 */
	@Override
	public int getXMLPackageCount(ERPYFOperationEnum operation)
	{
		return (SetUtils.isNullList(this.dataMap.get(operation.getCategory()))) ? 0 : 1;
	}
}
