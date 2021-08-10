/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: IntegrateWF
 * wangweixia 2012-4-10
 */
package dyna.app.service.brs.erpi.dataExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.erpi.ERPWFTransferStub;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.dto.erp.tmptab.ERPtempData;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ERPSMOperationEnum;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.ERPWFOperationEnum;
import dyna.common.systemenum.JobStatus;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ReplaceRangeEnum;
import dyna.common.systemenum.ReplaceTypeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.BRM;

/**
 * @author wangweixia
 * 
 */
public class IntegrateWF extends IntegrateERP<ERPWFOperationEnum>
{
	private final String	formItem	= "FormItem";
	private final String	formHead	= "FormHead";
	private final String	formBody	= "FormBody";
	private final String	subBody		= "SubBody";
	public final String		WFUserId	= "admin";

	/**
	 * @param context
	 * @param service
	 * @throws ServiceRequestException
	 * @throws IOException
	 * @throws JDOMException
	 */
	public IntegrateWF(ERPWFTransferStub stub, Document document) throws Exception
	{
		super(stub, document);
		this.xmlPath = "conf/wfconf.xml";
		this.defaultDateFormat = "yyyyMMdd";
		this.init();
	}

	/**
	 * @param object
	 * @param actionSchema
	 * @param totalCount
	 * @param index
	 * @return
	 * @throws ServiceRequestException
	 */
	@Override
	public Document getCreateDataXML(ERPWFOperationEnum operation, int totalCount, int index) throws Exception
	{
		Element stdEle = new Element("STD_IN");
		stdEle.addContent(this.getMsgHeadEleList(operation)).addContent(
				this.getDataEle(this.getHeaderPackageOfData(operation.getCategory()), operation.getCategory(), totalCount, index));
		return new Document(stdEle);
	}

	/**
	 * 生成XML文件中的Data标签部分
	 * 
	 * @param valueList
	 * @param category
	 * @param totalCount
	 * @param index
	 * @return
	 * @throws ServiceRequestException
	 */
	private Element getDataEle(List<RecordSet> valueList, String category, int totalCount, int index)
	{
		Map<String, String> tableMap = this.getTables(category);
		String itemTable = tableMap.get(this.formItem);
		String headTable = tableMap.get(this.formHead);
		String bodyTable = tableMap.get(this.formBody);
		String subTable = tableMap.get(this.subBody);
		Element itemEle = null;
		Element headEle = null;
		Element bodyEle = null;
		// 一个FormBody下面可以有多个subBody，因此这里不需要定义subEle
		// Element subEle = null;
		if (!StringUtils.isNullString(itemTable))
		{
			itemEle = new Element(this.formItem).addContent(new Element("TableName").setText(itemTable));
		}
		if (!StringUtils.isNullString(headTable))
		{
			headEle = new Element(this.formHead).addContent(new Element("TableName").setText(headTable));
		}
		if (!StringUtils.isNullString(bodyTable))
		{
			bodyEle = new Element(this.formBody).addContent(new Element("TableName").setText(bodyTable));
		}
		/*
		 * if (!StringUtils.isNullString(subTable)) { subEle = new
		 * Element(this.subBody).addContent(new
		 * Element("TableName").setText(subTable)); }
		 */
		boolean skipECN = false;
		Element dataEle = new Element("Data");
		Iterator<RecordSet> it = valueList.iterator();
		RecordSet recordset = null;
		while (it.hasNext())
		{
			recordset = it.next();
			if (itemEle != null)
			{
				TableRecordData itemData = recordset.getTableData(itemTable, this.stub);
				if (itemData != null)
				{
					for (int x = 0; x < itemData.size(); x++)
					{
						Element recordEle = new Element("RecordList");
						itemEle.addContent(recordEle);
						for (String field : itemData.get(x).keySet())
						{
							recordEle.addContent(new Element(field).setText(itemData.get(x).get(field)));
						}
					}
				}
			}
			if (!skipECN && headEle != null)
			{
				TableRecordData headData = recordset.getTableData(headTable, this.stub);
				if (headData != null)
				{
					for (int x = 0; x < headData.size(); x++)
					{
						Element recordEle = new Element("RecordList");
						headEle.addContent(recordEle);
						for (String field : headData.get(x).keySet())
						{
							recordEle.addContent(new Element(field).setText(headData.get(x).get(field)));
						}
					}
				}
				skipECN = category.equals(ERP_BOM);
			}

			if (bodyEle != null)
			{
				TableRecordData bodyData = recordset.getTableData(bodyTable, this.stub);
				if (bodyData != null)
				{
					for (int x = 0; x < bodyData.size(); x++)
					{
						Element recordEle = new Element("RecordList");
						bodyEle.addContent(recordEle);
						for (String field : bodyData.get(x).keySet())
						{
							recordEle.addContent(new Element(field).setText(bodyData.get(x).get(field)));
						}
						// subEle在bodyEle的recordList里面
						if (!StringUtils.isNullString(subTable))
						{
							TableRecordData subData = recordset.getTableData(subTable, this.stub);
							if (subData != null)
							{
								Element subEle = new Element(this.subBody).addContent(new Element("TableName").setText(subTable));
								recordEle.addContent(subEle);
								for (int z = 0; z < subData.size(); z++)
								{
									Element subRecordEle = new Element("RecordList");
									subEle.addContent(subRecordEle);
									for (String field : subData.get(z).keySet())
									{
										subRecordEle.addContent(new Element(field).setText(subData.get(z).get(field)));
									}
								}
							}
						}
					}
				}
			}
		}
		if (itemEle != null)
		{
			dataEle.addContent(itemEle);
		}
		if (headEle != null)
		{
			dataEle.addContent(headEle);
		}
		if (bodyEle != null)
		{
			dataEle.addContent(bodyEle);
		}
		valueList.clear();
		return dataEle;
	}

	private List<Element> getMsgHeadEleList(ERPWFOperationEnum operation)
	{
		ERPParameter param = this.getParameter(null);
		List<Element> eleList = new ArrayList<Element>();
		eleList.add(new Element("Companyid").setText(this.stub.factory));
		String WFUser = param.getParamMap().get("user");
		if (StringUtils.isNullString(WFUser))
		{
			WFUser = this.WFUserId;
		}
		eleList.add(new Element("Userid").setText(WFUser));
		eleList.add(new Element("DoAction").setText(operation.getNo()));
		eleList.add(new Element("DoActive").setText(param.getParamMap().get("doActive")));
		eleList.add(new Element("Docase").setText(param.getParamMap().get("doCase")));
		return eleList;
	}

	private TableRecordData getLeafItemForBOM(BOMStructure struct, String category, ERPWFOperationEnum operation) throws ServiceRequestException
	{
		TableRecordData data = null;
		Iterator<String> tableIt = this.getTables(category).values().iterator();
		String tableName = null;
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			if (tableName.equals(this.getTableNameById(category, this.formItem)))
			{
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(category, struct.getObjectGuid().getClassName(), tableName);
				// filedList是end1的列，这里要转换成end2的列
				data = new TableRecordData(this.getObjectMapValue(struct, this.convertEnd1ToEnd2Field(fieldList), category, operation));
				break;
			}
		}
		return data;
	}

	/**
	 * 这个方法只返回叶子结点的BOM的信息，即FormBody标签中的内容
	 * 
	 * @param end1Obj
	 * @param category
	 * @return
	 * @throws ServiceRequestException
	 */
	private TableRecordData getLeafBOMForBOM(BOMStructure struct, String category, ERPWFOperationEnum operation) throws ServiceRequestException
	{
		TableRecordData data = null;
		Iterator<String> tableIt = this.getTables(category).values().iterator();
		String tableName = null;
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			if (this.isContinEmptyBOMXML() && tableName.equals(this.getTableNameById(category, this.formBody)))
			{
				List<ERPFieldMapping> fieldList = this.getFieldFromMap(category, struct.getObjectGuid().getClassName(), tableName);
				// filedList是end1的列，这里要转换成end2的列
				data = new TableRecordData(this.getObjectMapValue(struct, this.convertEnd1ToEnd2Field(fieldList), category, operation));
				break;
			}
		}
		return data;
	}

	@Override
	protected int getRecordSetSizePerXMLPackage(String category)
	{
		if (category.equals(ERP_REPLACESUBSTITUTE))
		{
			return 1;
		}
		else
		{
			return super.getRecordSetSizePerXMLPackage(category);
		}
	}

	/**
	 * {@inheritDoc}
	 * <P/>
	 * WF提供的xml格式中，把局部取替代按照head-body(1-N)的关系，因此需要提供重载方法，并不能直接使用父类中方法
	 * <P>
	 * YT的xml是把每笔取替代做为一个RecordSet, 这种做法会产生主件和元件数据冗余问题
	 * 
	 * @see getEachLocalS4WF
	 */
	@Override
	@Deprecated
	public RecordSet getEachLocalS(FoundationObject obj, ERPWFOperationEnum operation) throws ServiceRequestException
	{
		throw new IllegalStateException("this method is not compatible with WFERP, please refers to 'getEachLocalS4WF' method");
	}

	/**
	 * @param end1Obj
	 * @param bomStructure
	 * @param end2List
	 * @param bomTemplate
	 * @param category
	 * @param lang
	 * @param jobId
	 * @param userId
	 * @return
	 * @throws ServiceRequestException
	 */
	private RecordSet getEachLocalS4WF(FoundationObject end1Obj, BOMStructure bomStructure, FoundationObject end2Obj, BOMTemplateInfo bomTemplate, ERPWFOperationEnum operation)
			throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(end1Obj, ERP_REPLACESUBSTITUTE, operation))
		{
			return null;
		}
		RecordSet data = new RecordSet(bomStructure.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
		String tableName = null;
		List<FoundationObject> substituteList = this.stub
				.getStubService()
				.getBRM()
				.listReplaceDataByRang(end1Obj.getObjectGuid(), end2Obj.getObjectGuid(), ReplaceRangeEnum.PART, ReplaceTypeEnum.TIDAI, bomTemplate.getName(),
						bomStructure.getBOMKey(), true);
		if (this.shouldAddRSEnd2Item())
		{
			if (substituteList != null && substituteList.size() != 0)
			{
				for (int j = 0; j < substituteList.size(); j++)
				{
					this.addRSEnd2FoundationObject(substituteList.get(j), operation);
				}
			}
		}
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			if (!SetUtils.isNullList(substituteList))
			{
				TableRecordData tableRecord = new TableRecordData();
				for (int j = 0; j < substituteList.size(); j++)
				{
					FoundationObject object = substituteList.get(j);
					if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
					{
//						add2StampMap(ERP_REPLACESUBSTITUTE, object);
						ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
						Map<String, String> map = this.getObjectMapValue(substituteList.get(j),
								this.getFieldFromMap(ERP_REPLACESUBSTITUTE, substituteList.get(j).getObjectGuid().getClassName(), tableName), ERP_REPLACESUBSTITUTE, operation);
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
						{
							map.put("MA003", "3");
						}
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formBody)))
						{
							map.put("MB003", "3");
						}
						tableRecord.add(map);
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
						{
							break;
						}
					}
				}
				data.put(tableName, tableRecord);
			}
		}
		return data;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see getEachLocalR
	 * @see getEachGlobalS4WF
	 */
	@Override
	@Deprecated
	public RecordSet getEachGlobalS(FoundationObject obj, ERPWFOperationEnum operation) throws ServiceRequestException
	{
		throw new IllegalStateException("this method is not compatible with WFERP, please refers to 'getEachGlobalS4WR' method");
	}

	private RecordSet getEachGlobalS4WF(FoundationObject end1Obj, BOMTemplateInfo bomTemplate, ERPWFOperationEnum operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(end1Obj, ERP_REPLACESUBSTITUTE, operation))
		{
			return null;
		}
		RecordSet data = new RecordSet(end1Obj.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
		String tableName = null;
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			TableRecordData tableRecord = new TableRecordData();
			List<FoundationObject> substituteList = this.stub.getStubService().getBRM()
					.listReplaceDataByRang(null, end1Obj.getObjectGuid(), ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI, null, null, true);
			if (substituteList != null && substituteList.size() != 0)
			{
				for (int j = 0; j < substituteList.size(); j++)
				{
					FoundationObject object = substituteList.get(j);
					if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
					{
//						add2StampMap(ERP_REPLACESUBSTITUTE, object);
						ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
						if (this.shouldAddRSEnd2Item())
						{
							this.addRSEnd2FoundationObject(substituteList.get(j), operation);
						}
						Map<String, String> map = this.getObjectMapValue(substituteList.get(j),
								this.getFieldFromMap(ERP_REPLACESUBSTITUTE, substituteList.get(j).getObjectGuid().getClassName(), tableName), ERP_REPLACESUBSTITUTE, operation);
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
						{
							map.put("MA003", "3");
						}
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formBody)))
						{
							map.put("MB003", "3");
						}
						tableRecord.add(map);
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
						{
							break;
						}
					}
				}
			}
			data.put(tableName, tableRecord);
		}
		return data;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see getEachLocalS
	 * @see getEachLocalR4WF
	 */
	@Override
	@Deprecated
	public RecordSet getEachLocalR(FoundationObject obj, ERPWFOperationEnum operation) throws ServiceRequestException
	{
		throw new IllegalStateException("this method is not compatible with WFERP, please refers to 'getEachLocalR4WF' method");
	}

	private RecordSet getEachLocalR4WF(FoundationObject end1Obj, BOMStructure bomStructure, FoundationObject end2Obj, BOMTemplateInfo bomTemplate, ERPWFOperationEnum operation)
			throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(end1Obj, ERP_REPLACESUBSTITUTE, operation))
		{
			return null;
		}
		RecordSet data = new RecordSet(bomStructure.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
		String tableName = null;
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			List<FoundationObject> substituteList = this.stub
					.getStubService()
					.getBRM()
					.listReplaceDataByRang(end1Obj.getObjectGuid(), end2Obj.getObjectGuid(), ReplaceRangeEnum.PART, ReplaceTypeEnum.QUDAI, bomTemplate.getName(),
							bomStructure.getBOMKey(), true);
			TableRecordData tableRecord = new TableRecordData();
			if (substituteList != null && substituteList.size() != 0)
			{
				for (int j = 0; j < substituteList.size(); j++)
				{
					FoundationObject object = substituteList.get(j);
					if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
					{
//						add2StampMap(ERP_REPLACESUBSTITUTE, object);
						ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
						if (this.shouldAddRSEnd2Item())
						{
							this.addRSEnd2FoundationObject(substituteList.get(j), operation);
						}
						Map<String, String> map = this.getObjectMapValue(substituteList.get(j),
								this.getFieldFromMap(ERP_REPLACESUBSTITUTE, substituteList.get(j).getObjectGuid().getClassName(), tableName), ERP_REPLACESUBSTITUTE, operation);
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
						{
							map.put("MA003", "2");
						}
						if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formBody)))
						{
							map.put("MB003", "2");
						}
						tableRecord.add(map);
					}
				}
			}
			data.put(tableName, tableRecord);
		}
		return data;
	}

	// /**
	// * {@inheritDoc}
	// *
	// * @see getEachLocalR
	// * @see getEachGlobalR4WF
	// */
	// @Override
	// @Deprecated
	// public RecordSet getEachGlobalR(FoundationObject obj, ERPWFOperationEnum operation) throws
	// ServiceRequestException
	// {
	// throw new
	// IllegalStateException("this method is not compatible with WFERP, please refers to 'getEachGlobalR' method");
	// }

	// private RecordSet getEachGlobalR4WF(FoundationObject end1Obj, FoundationObject end2, BOMTemplate bomTemplate,
	// ERPWFOperationEnum operation) throws ServiceRequestException
	// {
	// if (this.checkObjectReturningSkip(end1Obj, ERP_REPLACESUBSTITUTE, operation))
	// {
	// return null;
	// }
	// RecordSet data = new RecordSet(end2.getObjectGuid());
	// Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
	// String tableName = null;
	// while (tableIt.hasNext())
	// {
	// tableName = tableIt.next();
	// TableRecordData tableRecord = new TableRecordData();
	// List<FoundationObject> substituteList = this.stub.getStubService().getBRM()
	// .listReplaceDataByRang(null, end2.getObjectGuid(), ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.QUDAI,
	// bomTemplate.getId(), true);
	// if (substituteList != null && substituteList.size() != 0)
	// {
	// for (int j = 0; j < substituteList.size(); j++)
	// {
	// ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
	// if (this.shouldAddRSEnd2Item())
	// {
	// this.addRSEnd2FoundationObject(substituteList.get(j), operation);
	// }
	// Map<String, String> map = this.getObjectMapValue(substituteList.get(j),
	// this.getFieldFromMap(ERP_REPLACESUBSTITUTE, substituteList.get(j).getObjectGuid().getClassName(), tableName),
	// ERP_REPLACESUBSTITUTE, operation);
	// if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
	// {
	// map.put("MA003", "2");
	// }
	// if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formBody)))
	// {
	// map.put("MB003", "2");
	// }
	// tableRecord.add(map);
	// if (tableName.equals(this.getTableNameById(ERP_REPLACESUBSTITUTE, this.formHead)))
	// {
	// break;
	// }
	// }
	// }
	// data.put(tableName, tableRecord);
	// }
	// return data;
	// }

	@Override
	protected void getBOMData(FoundationObject end1Obj, List<BOMStructure> BOMStructureList, BOMStructure parentBOMStructure, ERPWFOperationEnum operation)
			throws ServiceRequestException
	{
		// WF在传BOM的时候要求把物料的简明信息也传过去，放在FormItem标签下
//		add2StampMap(ERP_BOM, end1Obj);
		RecordSet rs = this.getEachBOMData(end1Obj, BOMStructureList, parentBOMStructure, operation);
		if (null == rs)
		{
			return;
		}
		// 删除父件的料件信息(父件做为它的父件的子件取一次料件信息，因此在它做为父件时，不需要取料件信息)
		// 目前生成的xml文件中，同一个子件的料件信息可能有重复的，如C既是A的子件，又是B的子件，在取A、B的数据时，会把C的料件信息
		// 一起生成，由于A-C， B-C是在两个不同的RecordSet，因此没办法去重
		if (parentBOMStructure != null)
		{
			rs.getTableData(this.getTables(ERP_BOM).get(this.formItem), stub).clear();
		}
		// 如果有end1有BOM结构，将end2的料件信息追加到父阶的料件TableRecord中，否则ERP会在构成BOM的时候找不到end2料件
		if (!BOMStructureList.isEmpty())
		{
			for (int i = 0; i < BOMStructureList.size(); i++)
			{
				rs.getTableData(this.getTables(ERP_BOM).get(this.formItem), stub).add(this.getLeafItemForBOM(BOMStructureList.get(i), ERP_BOM, operation).getFirst());
			}
			this.addToDataMap(ERP_BOM, rs);
		}
		else
		{
			this.addToDataMap(ERP_ITEM, this.getEachItemData(end1Obj, ERPWFOperationEnum.PLMToDGBOM));
		}

		// 如果没有子件，并且不是传单阶BOM，并且ERP允许接收空BOM的xml文件，则输出空的BOM xml文件(这个一般用于删除ERP BOM，视ERP需求而定)
		if (BOMStructureList.isEmpty() && this.stub.schema.isExpandBOM() && this.isContinEmptyBOMXML())
		{
			RecordSet bomRS = new RecordSet(this.getMockObjectGuid(end1Obj));
			bomRS.put(this.getTables(ERP_BOM).get(this.formBody), this.getLeafBOMForBOM(parentBOMStructure, ERP_BOM, operation));
			this.addToDataMap(ERP_BOM, bomRS);
		}
	}

	@Override
	protected boolean isContinEmptyBOMXML()
	{
		return false;
	}

	@Override
	protected void getRSData(FoundationObject end1FoundationObj, List<BOMStructure> bOMStructureList, List<FoundationObject> end2FoundationObjList, BOMTemplateInfo bomTemplate,
			ERPWFOperationEnum operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(end1FoundationObj, ERP_REPLACESUBSTITUTE, operation))
		{
			return;
		}
		// 局部替代
		if (operation == ERPWFOperationEnum.PLMTOBOMI02)
		{
			for (int i = 0; i < end2FoundationObjList.size(); i++)
			{
				this.addToDataMap(ERP_REPLACESUBSTITUTE, this.getEachLocalS4WF(end1FoundationObj, bOMStructureList.get(i), end2FoundationObjList.get(i), bomTemplate, operation));
			}
		}
		// 局部取代
		else if (operation == ERPWFOperationEnum.PLMTOBOMI01)
		{
			for (int i = 0; i < end2FoundationObjList.size(); i++)
			{
				this.addToDataMap(ERP_REPLACESUBSTITUTE, this.getEachLocalR4WF(end1FoundationObj, bOMStructureList.get(i), end2FoundationObjList.get(i), bomTemplate, operation));
			}
		}
		// 全局替代
		else if (operation == ERPWFOperationEnum.PLMTOBOMI04)
		{
			// for (FoundationObject end2 : end2FoundationObjList)
			// {
			this.addToDataMap(ERP_REPLACESUBSTITUTE, this.getEachGlobalS4WF(end1FoundationObj, bomTemplate, operation));
			// }
		}
		// // 全局取代
		// else if (operation == ERPWFOperationEnum.PLMTOBOMI03)
		// {
		// for (FoundationObject end2 : end2FoundationObjList)
		// {
		// this.addToDataMap(ERP_REPLACESUBSTITUTE, this.getEachGlobalR4WF(end1FoundationObj, end2, bomTemplate,
		// operation));
		// }
		// }
	}

	@Override
	public String getOperationId(ERPWFOperationEnum operation)
	{
		return operation.getId();
	}

	@Override
	public String getOperationCategory(ERPWFOperationEnum operation)
	{
		return operation.getCategory();
	}

	@Override
	public List<ERPWFOperationEnum> getOperationList(boolean isMergeByCategory) throws ServiceRequestException
	{
		List<String> operations = this.stub.schema.getOperationList();
		List<String> categoryList = new ArrayList<String>();
		List<ERPWFOperationEnum> operationList = new ArrayList<ERPWFOperationEnum>();
		for (int i = 0; i < operations.size(); i++)
		{
			String id = operations.get(i);
			Map<String, String> attrMap = this.stub.getStubService().getOperationAttribute(ERPServerType.ERPWORKFLOW, id);
			ERPWFOperationEnum e = ERPWFOperationEnum.getById(id);
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
	public String getOperationCrossServiceName(ERPWFOperationEnum operation)
	{
		return operation.getWs();
	}

	@Override
	public String getOperationName(ERPWFOperationEnum operation)
	{
		return operation.getName();
	}

	@Override
	protected String getBOMHeaderTableName()
	{
		return "INVMB";
	};

	@Override
	public int getXMLPackageCount(ERPWFOperationEnum operation)
	{
		final String category = this.getOperationCategory(operation);
		if (category.equals(IntegrateERP.ERP_REPLACESUBSTITUTE))
		{
			if (SetUtils.isNullList(this.getDataMap().get(this.getOperationCategory(operation))))
			{
				return -1;
			}
			return this.getDataMap().get(this.getOperationCategory(operation)).size();
		}
		return super.getXMLPackageCount(operation);
	}

	@Override
	public String getReturnList(List<FoundationObject> list, String factory, ERPWFOperationEnum operation) throws Exception
	{
		String originalXML = null;
		this.stub.jobId = String.valueOf(System.nanoTime());
		if (operation.equals(ERPWFOperationEnum.GETPRICE) || operation.equals(ERPWFOperationEnum.GETCOST))
		{
			originalXML = this.getPriceOriginalXML(list, factory, operation);
		}
		else if (operation.equals(ERPWFOperationEnum.GETQUANTITY))
		{
			originalXML = this.getQuantityOriginalXML(list, factory, operation);
		}
		return this.stub.callWS(originalXML, operation);
	}

	/**
	 * 取价格成本（SM中取数量和取价格成本是2个服务，格式不同）
	 * （取数据时：仅取价格成本时支持单工厂多物料；与取数量混用时拆分成单工厂单物料）
	 * 
	 * @param list
	 * @param factoryId
	 * @param operation
	 * @return
	 * @throws Exception
	 */
	private String getPriceOriginalXML(List<FoundationObject> list, String factoryId, ERPWFOperationEnum operation) throws Exception
	{
		ERPParameter param = this.getParameter(null);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
		buffer.append("<STD_IN>");
		buffer.append("<Product>PLM</Product>");
		buffer.append("<Companyid>" + factoryId + "</Companyid>");
		buffer.append("<Userid>" + param.getParamMap().get("user") + "</Userid>");
		buffer.append("<DoAction>" + operation.getNo() + "</DoAction>");
		buffer.append("<Docase>" + param.getParamMap().get("doCase") + "</Docase>");
		buffer.append("<Data>		<FormHead>			<TableName>INVMB</TableName>");
		if (!SetUtils.isNullList(list))
		{
			for (FoundationObject object : list)
			{
				buffer.append("<RecordList>			<MB001>" + object.getId() + "</MB001>	");
				buffer.append("<MB046> MB046</MB046><MB047> MB047</MB047> <MB051> MB051</MB051><MB057>MB057</MB057><MB058>MB058</MB058>  <MB059>MB059</MB059> <MB060>MB060</MB060></RecordList>");
			}
		}
		buffer.append("</FormHead></Data></STD_IN>");
		return buffer.toString();
	}

	/**
	 * 取数量
	 * 
	 * @param fo
	 * @param factory
	 * @param operation
	 * @return
	 */
	private String getQuantityOriginalXML(List<FoundationObject> fo, String factory, ERPWFOperationEnum operation)
	{
		Date date = new Date();
		ERPParameter param = this.getParameter(null);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version='1.0' encoding='UTF-8'?>");
		buffer.append("<STD_IN>");
		buffer.append("<Product>PLM</Product>");
		buffer.append("<Companyid>" + factory + "</Companyid> ");
		buffer.append("<Userid>" + param.getParamMap().get("user") + "</Userid>");
		buffer.append("<DoAction>" + operation.getNo() + "</DoAction>");
		buffer.append("<Docase>" + param.getParamMap().get("doCase") + "</Docase>");
		buffer.append("	<Data>		<FormHead>			<TableName>INVQ01</TableName>");
		buffer.append("<RecordList>");
		buffer.append("<MQRY1>" + fo.get(0).getId() + "</MQRY1>");
		buffer.append("<MQRY2></MQRY2>");
		buffer.append("<MQRY3>" + DateFormat.format(date, "yyyyMMdd") + "</MQRY3>");
		buffer.append("</RecordList>");
		buffer.append("</FormHead></Data></STD_IN>");
		return buffer.toString();
	}

	@Override
	public List<RecordSet> dealReplaceSubstituteData(List<RecordSet> recordSets,boolean isExportAll) throws ServiceRequestException
	{
		List<RecordSet> result = new ArrayList<RecordSet>();
		Map<String, List<RecordSet>> groupedRecords = groupRSRecords(recordSets);
		ReplaceTypeEnum type = getRStype();
		if (!SetUtils.isNullMap(groupedRecords))
		{// 遍历每组主元件对应的取替代对象，有一个修改则传整组取替代对象
			for (String key : groupedRecords.keySet())
			{
				List<RecordSet> recordList = groupedRecords.get(key);
				if (!SetUtils.isNullList(recordList))
				{
					boolean isSend = false;
					for (RecordSet recordSet : recordList)
					{
						List<FoundationObject> substituteList = getSubstituteListByRecord(recordSet, type);
						if (!SetUtils.isNullList(substituteList))
						{
							for(FoundationObject object:substituteList)
							{
								String itemkey = object.getObjectGuid().getMasterGuid();
								String md5 = StringUtils.generateMd5(new RecordSet(object.getObjectGuid()).toString());
								ERPtempData temp = new ERPtempData();
								temp.setITEMGUID(itemkey);
								temp.setSTAMP(md5);
								temp.setJOBGUID(stub.jobGUID);
								temp.setCATEGORY(ERP_ITEM);
								temp.put("TABLE", stub.tempTableName);
								try
								{
									this.stub.getStubService().insertTempData(temp);
									setActtype(recordSet, true);
									isSend = true;
								}
								catch (Exception e)
								{
									setActtype(recordSet, false);
									temp = this.getERPTempData(ERP_ITEM, itemkey);
									if (temp.getJOBSTATUS() == -1)
									{
										isSend = true;
										temp.setJOBGUID(stub.jobGUID);
										temp.put("TABLE", stub.tempTableName);
										this.stub.getStubService().updateTempData(temp);
									}
									else if (temp.getJOBSTATUS() == JobStatus.SUCCESSFUL.getValue())
									{
										if (md5.equalsIgnoreCase(temp.getSTAMP()))
										{

										}
										else
										{
											isSend = true;
											temp.setSTAMP(md5);
											temp.setJOBGUID(stub.jobGUID);
											temp.put("TABLE", stub.tempTableName);
											this.stub.getStubService().updateTempData(temp);
										}
									}
									else if (temp.getJOBSTATUS() == JobStatus.FAILED.getValue())
									{
										isSend = true;
										temp.setJOBGUID(stub.jobGUID);
										temp.put("TABLE", stub.tempTableName);
										this.stub.getStubService().updateTempData(temp);
									}
									else
									{
										isSend = true;
										if (!md5.equalsIgnoreCase(temp.getSTAMP()))
										{
											temp.setJOBGUID(stub.jobGUID);
											temp.put("TABLE", stub.tempTableName);
											this.stub.getStubService().updateTempData(temp);
										}
									}
								}
							}
						}
						
					}
					if (isSend || isExportAll)
					{
						result.addAll(recordList);
					}
				}
			}
		}
		return result;

	}

	private List<FoundationObject> getSubstituteListByRecord(RecordSet set, ReplaceTypeEnum type) throws ServiceRequestException
	{
		List<FoundationObject> result = new ArrayList<FoundationObject>();
		List<FoundationObject> list = new ArrayList<FoundationObject>();
		BRM brm = stub.getStubService().getBRM();
		if (set != null)
		{
			DynaObject object = getObjectByObjGuid(set.getObjectGuid());
			if (object != null)
			{
				if (object instanceof BOMStructure)
				{
					BOMView view = stub.getStubService().getBOMS().getBOMView(((BOMStructure) object).getViewObjectGuid());
					BOMTemplateInfo bomTemplate = stub.getStubService().getEMM().getBOMTemplateById(view.getTemplateID());
					if (((BOMStructure) object).getEnd1ObjectGuid() != null && ((BOMStructure) object).getEnd2ObjectGuid() != null)
					{

						list = brm.listReplaceDataByRang(((BOMStructure) object).getEnd1ObjectGuid(), ((BOMStructure) object).getEnd2ObjectGuid(), ReplaceRangeEnum.PART, type,
								bomTemplate.getName(), ((BOMStructure) object).getBOMKey(), true);
					}
				}
				else if (object instanceof FoundationObject)
				{
					list = brm.listReplaceDataByRang(null, object.getObjectGuid(), ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI, null, null, true);
				}
				if (!SetUtils.isNullList(list))
				{
					for (FoundationObject fo : list)
					{
						if (fo.get(ReplaceSubstituteConstants.EffectiveDate) != null)
						{
							result.add(fo);
						}
					}
				}
			}
		}
		return result;
	}

	private DynaObject getObjectByObjGuid(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid == null)
		{
			return null;
		}
		String classGuid = objectGuid.getClassGuid();
		ClassInfo info = this.stub.getStubService().getEMM().getClassByGuid(classGuid);
		if (info.hasInterface(ModelInterfaceEnum.IItem))
		{
			return this.stub.getStubService().getBOAS().getObject(objectGuid);
		}
		else if (info.hasInterface(ModelInterfaceEnum.IBOMStructure))
		{
			SearchCondition condition = SearchConditionFactory.createSearchConditionForStructure(objectGuid.getClassName());
			return this.stub.getStubService().getBOMS().getBOM(objectGuid, condition, null);
		}
		return null;
	}

	private ReplaceTypeEnum getRStype()
	{
		if (this.stub.schema != null)
		{
			List<String> operationList = this.stub.schema.getOperationList();
			if (operationList.contains(ERPSMOperationEnum.PLMTOBOMI01.getId()))
			{
				return ReplaceTypeEnum.QUDAI;
			}
			else if (operationList.contains(ERPSMOperationEnum.PLMTOBOMI02.getId()))
			{
				return ReplaceTypeEnum.TIDAI;
			}
		}
		return null;
	}
}
