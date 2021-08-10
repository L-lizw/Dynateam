/**
 * TIPTOPServiceGateWayBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.ytIntegrateService;

public class TIPTOPServiceGateWayBindingStub extends org.apache.axis.client.Stub implements
		TIPTOPServiceGateWayPortType
{
	private final java.util.Vector						cachedSerClasses		= new java.util.Vector();
	private final java.util.Vector						cachedSerQNames			= new java.util.Vector();
	private final java.util.Vector						cachedSerFactories		= new java.util.Vector();
	private final java.util.Vector						cachedDeserFactories	= new java.util.Vector();

	static org.apache.axis.description.OperationDesc[]	_operations;

	static
	{
		_operations = new org.apache.axis.description.OperationDesc[151];
		_initOperationDesc1();
		_initOperationDesc2();
		_initOperationDesc3();
		_initOperationDesc4();
		_initOperationDesc5();
		_initOperationDesc6();
		_initOperationDesc7();
		_initOperationDesc8();
		_initOperationDesc9();
		_initOperationDesc10();
		_initOperationDesc11();
		_initOperationDesc12();
		_initOperationDesc13();
		_initOperationDesc14();
		_initOperationDesc15();
		_initOperationDesc16();
	}

	private static void _initOperationDesc1()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustomerAccAmtData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustomerAccAmtDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCustomerAccAmtDataRequest_GetCustomerAccAmtDataRequest"),
				GetCustomerAccAmtDataRequest_GetCustomerAccAmtDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse"));
		oper.setReturnClass(GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerAccAmtDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustClassificationData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustClassificationDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCustClassificationDataRequest_GetCustClassificationDataRequest"),
				GetCustClassificationDataRequest_GetCustClassificationDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustClassificationDataResponse_GetCustClassificationDataResponse"));
		oper.setReturnClass(GetCustClassificationDataResponse_GetCustClassificationDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustClassificationDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetInvoiceTypeList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetInvoiceTypeListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetInvoiceTypeListRequest_GetInvoiceTypeListRequest"),
				GetInvoiceTypeListRequest_GetInvoiceTypeListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInvoiceTypeListResponse_GetInvoiceTypeListResponse"));
		oper.setReturnClass(GetInvoiceTypeListResponse_GetInvoiceTypeListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInvoiceTypeListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetTradeTermData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetTradeTermDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetTradeTermDataRequest_GetTradeTermDataRequest"),
				GetTradeTermDataRequest_GetTradeTermDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTradeTermDataResponse_GetTradeTermDataResponse"));
		oper.setReturnClass(GetTradeTermDataResponse_GetTradeTermDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTradeTermDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetDataCount");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetDataCountRequest"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetDataCountRequest_GetDataCountRequest"), GetDataCountRequest_GetDataCountRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDataCountResponse_GetDataCountResponse"));
		oper.setReturnClass(GetDataCountResponse_GetDataCountResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDataCountResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("SyncAccountData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "SyncAccountDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"SyncAccountDataRequest_SyncAccountDataRequest"),
				SyncAccountDataRequest_SyncAccountDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"SyncAccountDataResponse_SyncAccountDataResponse"));
		oper.setReturnClass(SyncAccountDataResponse_SyncAccountDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"SyncAccountDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreatePLMTempTableData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreatePLMTempTableDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest"),
				CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse"));
		oper.setReturnClass(CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMTempTableDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[6] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("DeletePLMTempTableData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "DeletePLMTempTableDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest"),
				DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse"));
		oper.setReturnClass(DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"DeletePLMTempTableDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[7] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPLMTempTableDataStatus");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPLMTempTableDataStatusRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest"),
				GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse"));
		oper.setReturnClass(GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPLMTempTableDataStatusResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[8] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetAreaData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAreaDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAreaDataRequest_GetAreaDataRequest"),
				GetAreaDataRequest_GetAreaDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaDataResponse_GetAreaDataResponse"));
		oper.setReturnClass(GetAreaDataResponse_GetAreaDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[9] = oper;

	}

	private static void _initOperationDesc2()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetAreaList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAreaListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAreaListRequest_GetAreaListRequest"),
				GetAreaListRequest_GetAreaListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaListResponse_GetAreaListResponse"));
		oper.setReturnClass(GetAreaListResponse_GetAreaListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[10] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetAxmDocument");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAxmDocumentRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetAxmDocumentRequest_GetAxmDocumentRequest"),
				GetAxmDocumentRequest_GetAxmDocumentRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAxmDocumentResponse_GetAxmDocumentResponse"));
		oper.setReturnClass(GetAxmDocumentResponse_GetAxmDocumentResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAxmDocumentResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[11] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPurchaseStockInQty");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPurchaseStockInQtyRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPurchaseStockInQtyRequest_GetPurchaseStockInQtyRequest"),
				GetPurchaseStockInQtyRequest_GetPurchaseStockInQtyRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse"));
		oper.setReturnClass(GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockInQtyResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[12] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetBasicCodeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetBasicCodeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetBasicCodeDataRequest_GetBasicCodeDataRequest"),
				GetBasicCodeDataRequest_GetBasicCodeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBasicCodeDataResponse_GetBasicCodeDataResponse"));
		oper.setReturnClass(GetBasicCodeDataResponse_GetBasicCodeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBasicCodeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[13] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetComponentrepsubData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetComponentrepsubDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetComponentrepsubDataRequest_GetComponentrepsubDataRequest"),
				GetComponentrepsubDataRequest_GetComponentrepsubDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetComponentrepsubDataResponse_GetComponentrepsubDataResponse"));
		oper.setReturnClass(GetComponentrepsubDataResponse_GetComponentrepsubDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetComponentrepsubDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[14] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCostGroupData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCostGroupDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCostGroupDataRequest_GetCostGroupDataRequest"),
				GetCostGroupDataRequest_GetCostGroupDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCostGroupDataResponse_GetCostGroupDataResponse"));
		oper.setReturnClass(GetCostGroupDataResponse_GetCostGroupDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCostGroupDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[15] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCountryData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCountryDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCountryDataRequest_GetCountryDataRequest"),
				GetCountryDataRequest_GetCountryDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryDataResponse_GetCountryDataResponse"));
		oper.setReturnClass(GetCountryDataResponse_GetCountryDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[16] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCountryList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCountryListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCountryListRequest_GetCountryListRequest"),
				GetCountryListRequest_GetCountryListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryListResponse_GetCountryListResponse"));
		oper.setReturnClass(GetCountryListResponse_GetCountryListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[17] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCurrencyData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCurrencyDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCurrencyDataRequest_GetCurrencyDataRequest"),
				GetCurrencyDataRequest_GetCurrencyDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyDataResponse_GetCurrencyDataResponse"));
		oper.setReturnClass(GetCurrencyDataResponse_GetCurrencyDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[18] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCurrencyList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCurrencyListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCurrencyListRequest_GetCurrencyListRequest"),
				GetCurrencyListRequest_GetCurrencyListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyListResponse_GetCurrencyListResponse"));
		oper.setReturnClass(GetCurrencyListResponse_GetCurrencyListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[19] = oper;

	}

	private static void _initOperationDesc3()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustomerData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustomerDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCustomerDataRequest_GetCustomerDataRequest"),
				GetCustomerDataRequest_GetCustomerDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerDataResponse_GetCustomerDataResponse"));
		oper.setReturnClass(GetCustomerDataResponse_GetCustomerDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[20] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustomerProductData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustomerProductDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCustomerProductDataRequest_GetCustomerProductDataRequest"),
				GetCustomerProductDataRequest_GetCustomerProductDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerProductDataResponse_GetCustomerProductDataResponse"));
		oper.setReturnClass(GetCustomerProductDataResponse_GetCustomerProductDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerProductDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[21] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetDepartmentData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetDepartmentDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetDepartmentDataRequest_GetDepartmentDataRequest"),
				GetDepartmentDataRequest_GetDepartmentDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentDataResponse_GetDepartmentDataResponse"));
		oper.setReturnClass(GetDepartmentDataResponse_GetDepartmentDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[22] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetDepartmentList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetDepartmentListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetDepartmentListRequest_GetDepartmentListRequest"),
				GetDepartmentListRequest_GetDepartmentListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentListResponse_GetDepartmentListResponse"));
		oper.setReturnClass(GetDepartmentListResponse_GetDepartmentListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[23] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPOReceivingOutData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPOReceivingOutDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPOReceivingOutDataRequest_GetPOReceivingOutDataRequest"),
				GetPOReceivingOutDataRequest_GetPOReceivingOutDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse"));
		oper.setReturnClass(GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingOutDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[24] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetEmployeeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetEmployeeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetEmployeeDataRequest_GetEmployeeDataRequest"),
				GetEmployeeDataRequest_GetEmployeeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeDataResponse_GetEmployeeDataResponse"));
		oper.setReturnClass(GetEmployeeDataResponse_GetEmployeeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[25] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetEmployeeList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetEmployeeListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetEmployeeListRequest_GetEmployeeListRequest"),
				GetEmployeeListRequest_GetEmployeeListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeListResponse_GetEmployeeListResponse"));
		oper.setReturnClass(GetEmployeeListResponse_GetEmployeeListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[26] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetInspectionData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetInspectionDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetInspectionDataRequest_GetInspectionDataRequest"),
				GetInspectionDataRequest_GetInspectionDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInspectionDataResponse_GetInspectionDataResponse"));
		oper.setReturnClass(GetInspectionDataResponse_GetInspectionDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInspectionDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[27] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreatePurchaseStockOut");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreatePurchaseStockOutRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreatePurchaseStockOutRequest_CreatePurchaseStockOutRequest"),
				CreatePurchaseStockOutRequest_CreatePurchaseStockOutRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse"));
		oper.setReturnClass(CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockOutResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[28] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetLocationData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetLocationDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetLocationDataRequest_GetLocationDataRequest"),
				GetLocationDataRequest_GetLocationDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLocationDataResponse_GetLocationDataResponse"));
		oper.setReturnClass(GetLocationDataResponse_GetLocationDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLocationDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[29] = oper;

	}

	private static void _initOperationDesc4()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMonthList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMonthListRequest"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetMonthListRequest_GetMonthListRequest"), GetMonthListRequest_GetMonthListRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMonthListResponse_GetMonthListResponse"));
		oper.setReturnClass(GetMonthListResponse_GetMonthListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMonthListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[30] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetOverdueAmtDetailData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetOverdueAmtDetailDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetOverdueAmtDetailDataRequest_GetOverdueAmtDetailDataRequest"),
				GetOverdueAmtDetailDataRequest_GetOverdueAmtDetailDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse"));
		oper.setReturnClass(GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtDetailDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[31] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetOverdueAmtRankingData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetOverdueAmtRankingDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetOverdueAmtRankingDataRequest_GetOverdueAmtRankingDataRequest"),
				GetOverdueAmtRankingDataRequest_GetOverdueAmtRankingDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse"));
		oper.setReturnClass(GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtRankingDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[32] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetProdClassList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetProdClassListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetProdClassListRequest_GetProdClassListRequest"),
				GetProdClassListRequest_GetProdClassListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdClassListResponse_GetProdClassListResponse"));
		oper.setReturnClass(GetProdClassListResponse_GetProdClassListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdClassListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[33] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetProductClassData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetProductClassDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetProductClassDataRequest_GetProductClassDataRequest"),
				GetProductClassDataRequest_GetProductClassDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProductClassDataResponse_GetProductClassDataResponse"));
		oper.setReturnClass(GetProductClassDataResponse_GetProductClassDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProductClassDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[34] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSOInfoData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSOInfoDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSOInfoDataRequest_GetSOInfoDataRequest"), GetSOInfoDataRequest_GetSOInfoDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDataResponse_GetSOInfoDataResponse"));
		oper.setReturnClass(GetSOInfoDataResponse_GetSOInfoDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[35] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSOInfoDetailData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSOInfoDetailDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSOInfoDetailDataRequest_GetSOInfoDetailDataRequest"),
				GetSOInfoDetailDataRequest_GetSOInfoDetailDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse"));
		oper.setReturnClass(GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDetailDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[36] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSalesDetailData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSalesDetailDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSalesDetailDataRequest_GetSalesDetailDataRequest"),
				GetSalesDetailDataRequest_GetSalesDetailDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDetailDataResponse_GetSalesDetailDataResponse"));
		oper.setReturnClass(GetSalesDetailDataResponse_GetSalesDetailDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDetailDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[37] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSalesStatisticsData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSalesStatisticsDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSalesStatisticsDataRequest_GetSalesStatisticsDataRequest"),
				GetSalesStatisticsDataRequest_GetSalesStatisticsDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse"));
		oper.setReturnClass(GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesStatisticsDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[38] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSupplierData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSupplierDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSupplierDataRequest_GetSupplierDataRequest"),
				GetSupplierDataRequest_GetSupplierDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierDataResponse_GetSupplierDataResponse"));
		oper.setReturnClass(GetSupplierDataResponse_GetSupplierDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[39] = oper;

	}

	private static void _initOperationDesc5()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSupplierItemData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSupplierItemDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSupplierItemDataRequest_GetSupplierItemDataRequest"),
				GetSupplierItemDataRequest_GetSupplierItemDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierItemDataResponse_GetSupplierItemDataResponse"));
		oper.setReturnClass(GetSupplierItemDataResponse_GetSupplierItemDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierItemDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[40] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetWarehouseData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetWarehouseDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetWarehouseDataRequest_GetWarehouseDataRequest"),
				GetWarehouseDataRequest_GetWarehouseDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWarehouseDataResponse_GetWarehouseDataResponse"));
		oper.setReturnClass(GetWarehouseDataResponse_GetWarehouseDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWarehouseDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[41] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetItemData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemDataRequest_GetItemDataRequest"),
				GetItemDataRequest_GetItemDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemDataResponse_GetItemDataResponse"));
		oper.setReturnClass(GetItemDataResponse_GetItemDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[42] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetBOMData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetBOMDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetBOMDataRequest_GetBOMDataRequest"),
				GetBOMDataRequest_GetBOMDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBOMDataResponse_GetBOMDataResponse"));
		oper.setReturnClass(GetBOMDataResponse_GetBOMDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBOMDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[43] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetDocumentNumber");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetDocumentNumberRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetDocumentNumberRequest_GetDocumentNumberRequest"),
				GetDocumentNumberRequest_GetDocumentNumberRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDocumentNumberResponse_GetDocumentNumberResponse"));
		oper.setReturnClass(GetDocumentNumberResponse_GetDocumentNumberResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDocumentNumberResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[44] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateQuotationData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateQuotationDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateQuotationDataRequest_CreateQuotationDataRequest"),
				CreateQuotationDataRequest_CreateQuotationDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateQuotationDataResponse_CreateQuotationDataResponse"));
		oper.setReturnClass(CreateQuotationDataResponse_CreateQuotationDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateQuotationDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[45] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetStockData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetStockDataRequest"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetStockDataRequest_GetStockDataRequest"), GetStockDataRequest_GetStockDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetStockDataResponse_GetStockDataResponse"));
		oper.setReturnClass(GetStockDataResponse_GetStockDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetStockDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[46] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetReceivingQty");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetReceivingQtyRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetReceivingQtyRequest_GetReceivingQtyRequest"),
				GetReceivingQtyRequest_GetReceivingQtyRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReceivingQtyResponse_GetReceivingQtyResponse"));
		oper.setReturnClass(GetReceivingQtyResponse_GetReceivingQtyResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReceivingQtyResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[47] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPOData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPODataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPODataRequest_GetPODataRequest"),
				GetPODataRequest_GetPODataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPODataResponse_GetPODataResponse"));
		oper.setReturnClass(GetPODataResponse_GetPODataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPODataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[48] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMFGDocument");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMFGDocumentRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetMFGDocumentRequest_GetMFGDocumentRequest"),
				GetMFGDocumentRequest_GetMFGDocumentRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGDocumentResponse_GetMFGDocumentResponse"));
		oper.setReturnClass(GetMFGDocumentResponse_GetMFGDocumentResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGDocumentResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[49] = oper;

	}

	private static void _initOperationDesc6()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreatePOReceivingData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreatePOReceivingDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreatePOReceivingDataRequest_CreatePOReceivingDataRequest"),
				CreatePOReceivingDataRequest_CreatePOReceivingDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePOReceivingDataResponse_CreatePOReceivingDataResponse"));
		oper.setReturnClass(CreatePOReceivingDataResponse_CreatePOReceivingDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePOReceivingDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[50] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateIssueReturnData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateIssueReturnDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateIssueReturnDataRequest_CreateIssueReturnDataRequest"),
				CreateIssueReturnDataRequest_CreateIssueReturnDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateIssueReturnDataResponse_CreateIssueReturnDataResponse"));
		oper.setReturnClass(CreateIssueReturnDataResponse_CreateIssueReturnDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateIssueReturnDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[51] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPOReceivingInData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPOReceivingInDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPOReceivingInDataRequest_GetPOReceivingInDataRequest"),
				GetPOReceivingInDataRequest_GetPOReceivingInDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingInDataResponse_GetPOReceivingInDataResponse"));
		oper.setReturnClass(GetPOReceivingInDataResponse_GetPOReceivingInDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingInDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[52] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateStockInData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateStockInDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateStockInDataRequest_CreateStockInDataRequest"),
				CreateStockInDataRequest_CreateStockInDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockInDataResponse_CreateStockInDataResponse"));
		oper.setReturnClass(CreateStockInDataResponse_CreateStockInDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockInDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[53] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetAccountSubjectData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAccountSubjectDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetAccountSubjectDataRequest_GetAccountSubjectDataRequest"),
				GetAccountSubjectDataRequest_GetAccountSubjectDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountSubjectDataResponse_GetAccountSubjectDataResponse"));
		oper.setReturnClass(GetAccountSubjectDataResponse_GetAccountSubjectDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountSubjectDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[54] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreatePurchaseStockIn");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreatePurchaseStockInRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreatePurchaseStockInRequest_CreatePurchaseStockInRequest"),
				CreatePurchaseStockInRequest_CreatePurchaseStockInRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockInResponse_CreatePurchaseStockInResponse"));
		oper.setReturnClass(CreatePurchaseStockInResponse_CreatePurchaseStockInResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockInResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[55] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPurchaseStockOutQty");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPurchaseStockOutQtyRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPurchaseStockOutQtyRequest_GetPurchaseStockOutQtyRequest"),
				GetPurchaseStockOutQtyRequest_GetPurchaseStockOutQtyRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse"));
		oper.setReturnClass(GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockOutQtyResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[56] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateTransferNote");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateTransferNoteRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateTransferNoteRequest_CreateTransferNoteRequest"),
				CreateTransferNoteRequest_CreateTransferNoteRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateTransferNoteResponse_CreateTransferNoteResponse"));
		oper.setReturnClass(CreateTransferNoteResponse_CreateTransferNoteResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateTransferNoteResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[57] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetQtyConversion");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetQtyConversionRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetQtyConversionRequest_GetQtyConversionRequest"),
				GetQtyConversionRequest_GetQtyConversionRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQtyConversionResponse_GetQtyConversionResponse"));
		oper.setReturnClass(GetQtyConversionResponse_GetQtyConversionResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQtyConversionResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[58] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetShippingNoticeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetShippingNoticeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetShippingNoticeDataRequest_GetShippingNoticeDataRequest"),
				GetShippingNoticeDataRequest_GetShippingNoticeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingNoticeDataResponse_GetShippingNoticeDataResponse"));
		oper.setReturnClass(GetShippingNoticeDataResponse_GetShippingNoticeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingNoticeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[59] = oper;

	}

	private static void _initOperationDesc7()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSalesDocument");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSalesDocumentRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetSalesDocumentRequest_GetSalesDocumentRequest"),
				GetSalesDocumentRequest_GetSalesDocumentRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDocumentResponse_GetSalesDocumentResponse"));
		oper.setReturnClass(GetSalesDocumentResponse_GetSalesDocumentResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDocumentResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[60] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetShippingOrderData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetShippingOrderDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetShippingOrderDataRequest_GetShippingOrderDataRequest"),
				GetShippingOrderDataRequest_GetShippingOrderDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingOrderDataResponse_GetShippingOrderDataResponse"));
		oper.setReturnClass(GetShippingOrderDataResponse_GetShippingOrderDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingOrderDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[61] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetFQCData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetFQCDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetFQCDataRequest_GetFQCDataRequest"),
				GetFQCDataRequest_GetFQCDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetFQCDataResponse_GetFQCDataResponse"));
		oper.setReturnClass(GetFQCDataResponse_GetFQCDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetFQCDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[62] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetWOData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetWODataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetWODataRequest_GetWODataRequest"),
				GetWODataRequest_GetWODataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWODataResponse_GetWODataResponse"));
		oper.setReturnClass(GetWODataResponse_GetWODataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWODataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[63] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetWOStockQty");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetWOStockQtyRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetWOStockQtyRequest_GetWOStockQtyRequest"), GetWOStockQtyRequest_GetWOStockQtyRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOStockQtyResponse_GetWOStockQtyResponse"));
		oper.setReturnClass(GetWOStockQtyResponse_GetWOStockQtyResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOStockQtyResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[64] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateWOStockinData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateWOStockinDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateWOStockinDataRequest_CreateWOStockinDataRequest"),
				CreateWOStockinDataRequest_CreateWOStockinDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOStockinDataResponse_CreateWOStockinDataResponse"));
		oper.setReturnClass(CreateWOStockinDataResponse_CreateWOStockinDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOStockinDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[65] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetWOIssueData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetWOIssueDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetWOIssueDataRequest_GetWOIssueDataRequest"),
				GetWOIssueDataRequest_GetWOIssueDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOIssueDataResponse_GetWOIssueDataResponse"));
		oper.setReturnClass(GetWOIssueDataResponse_GetWOIssueDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOIssueDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[66] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("UpdateWOIssueData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "UpdateWOIssueDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"UpdateWOIssueDataRequest_UpdateWOIssueDataRequest"),
				UpdateWOIssueDataRequest_UpdateWOIssueDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateWOIssueDataResponse_UpdateWOIssueDataResponse"));
		oper.setReturnClass(UpdateWOIssueDataResponse_UpdateWOIssueDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateWOIssueDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[67] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateShippingOrder");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateShippingOrderRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateShippingOrderRequest_CreateShippingOrderRequest"),
				CreateShippingOrderRequest_CreateShippingOrderRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrderResponse_CreateShippingOrderResponse"));
		oper.setReturnClass(CreateShippingOrderResponse_CreateShippingOrderResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrderResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[68] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetReasonCode");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetReasonCodeRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetReasonCodeRequest_GetReasonCodeRequest"), GetReasonCodeRequest_GetReasonCodeRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReasonCodeResponse_GetReasonCodeResponse"));
		oper.setReturnClass(GetReasonCodeResponse_GetReasonCodeResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReasonCodeResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[69] = oper;

	}

	private static void _initOperationDesc8()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetLabelTypeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetLabelTypeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetLabelTypeDataRequest_GetLabelTypeDataRequest"),
				GetLabelTypeDataRequest_GetLabelTypeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLabelTypeDataResponse_GetLabelTypeDataResponse"));
		oper.setReturnClass(GetLabelTypeDataResponse_GetLabelTypeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLabelTypeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[70] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCountingLabelData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCountingLabelDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCountingLabelDataRequest_GetCountingLabelDataRequest"),
				GetCountingLabelDataRequest_GetCountingLabelDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountingLabelDataResponse_GetCountingLabelDataResponse"));
		oper.setReturnClass(GetCountingLabelDataResponse_GetCountingLabelDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountingLabelDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[71] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("UpdateCountingLabelData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "UpdateCountingLabelDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"UpdateCountingLabelDataRequest_UpdateCountingLabelDataRequest"),
				UpdateCountingLabelDataRequest_UpdateCountingLabelDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse"));
		oper.setReturnClass(UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateCountingLabelDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[72] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateMISCIssueData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateMISCIssueDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateMISCIssueDataRequest_CreateMISCIssueDataRequest"),
				CreateMISCIssueDataRequest_CreateMISCIssueDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateMISCIssueDataResponse_CreateMISCIssueDataResponse"));
		oper.setReturnClass(CreateMISCIssueDataResponse_CreateMISCIssueDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateMISCIssueDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[73] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CheckExecAuthorization");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CheckExecAuthorizationRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CheckExecAuthorizationRequest_CheckExecAuthorizationRequest"),
				CheckExecAuthorizationRequest_CheckExecAuthorizationRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckExecAuthorizationResponse_CheckExecAuthorizationResponse"));
		oper.setReturnClass(CheckExecAuthorizationResponse_CheckExecAuthorizationResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckExecAuthorizationResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[74] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateStockData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateStockDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateStockDataRequest_CreateStockDataRequest"),
				CreateStockDataRequest_CreateStockDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockDataResponse_CreateStockDataResponse"));
		oper.setReturnClass(CreateStockDataResponse_CreateStockDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[75] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("EboGetCustData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "EboGetCustDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"EboGetCustDataRequest_EboGetCustDataRequest"),
				EboGetCustDataRequest_EboGetCustDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetCustDataResponse_EboGetCustDataResponse"));
		oper.setReturnClass(EboGetCustDataResponse_EboGetCustDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetCustDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[76] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("EboGetProdData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "EboGetProdDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"EboGetProdDataRequest_EboGetProdDataRequest"),
				EboGetProdDataRequest_EboGetProdDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetProdDataResponse_EboGetProdDataResponse"));
		oper.setReturnClass(EboGetProdDataResponse_EboGetProdDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetProdDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[77] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("EboGetOrderData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "EboGetOrderDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"EboGetOrderDataRequest_EboGetOrderDataRequest"),
				EboGetOrderDataRequest_EboGetOrderDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetOrderDataResponse_EboGetOrderDataResponse"));
		oper.setReturnClass(EboGetOrderDataResponse_EboGetOrderDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetOrderDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[78] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("RunCommand");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "RunCommandRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "RunCommandRequest_RunCommandRequest"),
				RunCommandRequest_RunCommandRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RunCommandResponse_RunCommandResponse"));
		oper.setReturnClass(RunCommandResponse_RunCommandResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RunCommandResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[79] = oper;

	}

	private static void _initOperationDesc9()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CheckApsExecution");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CheckApsExecutionRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CheckApsExecutionRequest_CheckApsExecutionRequest"),
				CheckApsExecutionRequest_CheckApsExecutionRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckApsExecutionResponse_CheckApsExecutionResponse"));
		oper.setReturnClass(CheckApsExecutionResponse_CheckApsExecutionResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckApsExecutionResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[80] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetOrganizationList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetOrganizationListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetOrganizationListRequest_GetOrganizationListRequest"),
				GetOrganizationListRequest_GetOrganizationListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOrganizationListResponse_GetOrganizationListResponse"));
		oper.setReturnClass(GetOrganizationListResponse_GetOrganizationListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOrganizationListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[81] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetUserToken");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetUserTokenRequest"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetUserTokenRequest_GetUserTokenRequest"), GetUserTokenRequest_GetUserTokenRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserTokenResponse_GetUserTokenResponse"));
		oper.setReturnClass(GetUserTokenResponse_GetUserTokenResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserTokenResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[82] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CheckUserAuth");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CheckUserAuthRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CheckUserAuthRequest_CheckUserAuthRequest"), CheckUserAuthRequest_CheckUserAuthRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckUserAuthResponse_CheckUserAuthResponse"));
		oper.setReturnClass(CheckUserAuthResponse_CheckUserAuthResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckUserAuthResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[83] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMenuData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMenuDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMenuDataRequest_GetMenuDataRequest"),
				GetMenuDataRequest_GetMenuDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMenuDataResponse_GetMenuDataResponse"));
		oper.setReturnClass(GetMenuDataResponse_GetMenuDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMenuDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[84] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateVendorData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateVendorDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateVendorDataRequest_CreateVendorDataRequest"),
				CreateVendorDataRequest_CreateVendorDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVendorDataResponse_CreateVendorDataResponse"));
		oper.setReturnClass(CreateVendorDataResponse_CreateVendorDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVendorDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[85] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateBOMMasterData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateBOMMasterDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateBOMMasterDataRequest_CreateBOMMasterDataRequest"),
				CreateBOMMasterDataRequest_CreateBOMMasterDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMMasterDataResponse_CreateBOMMasterDataResponse"));
		oper.setReturnClass(CreateBOMMasterDataResponse_CreateBOMMasterDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMMasterDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[86] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateBOMDetailData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateBOMDetailDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateBOMDetailDataRequest_CreateBOMDetailDataRequest"),
				CreateBOMDetailDataRequest_CreateBOMDetailDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDetailDataResponse_CreateBOMDetailDataResponse"));
		oper.setReturnClass(CreateBOMDetailDataResponse_CreateBOMDetailDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDetailDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[87] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateVoucherData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateVoucherDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateVoucherDataRequest_CreateVoucherDataRequest"),
				CreateVoucherDataRequest_CreateVoucherDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVoucherDataResponse_CreateVoucherDataResponse"));
		oper.setReturnClass(CreateVoucherDataResponse_CreateVoucherDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVoucherDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[88] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetAccountData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAccountDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetAccountDataRequest_GetAccountDataRequest"),
				GetAccountDataRequest_GetAccountDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountDataResponse_GetAccountDataResponse"));
		oper.setReturnClass(GetAccountDataResponse_GetAccountDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[89] = oper;

	}

	private static void _initOperationDesc10()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateCustomerData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateCustomerDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateCustomerDataRequest_CreateCustomerDataRequest"),
				CreateCustomerDataRequest_CreateCustomerDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerDataResponse_CreateCustomerDataResponse"));
		oper.setReturnClass(CreateCustomerDataResponse_CreateCustomerDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[90] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateItemMasterData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateItemMasterDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateItemMasterDataRequest_CreateItemMasterDataRequest"),
				CreateItemMasterDataRequest_CreateItemMasterDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemMasterDataResponse_CreateItemMasterDataResponse"));
		oper.setReturnClass(CreateItemMasterDataResponse_CreateItemMasterDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemMasterDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[91] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateEmployeeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateEmployeeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateEmployeeDataRequest_CreateEmployeeDataRequest"),
				CreateEmployeeDataRequest_CreateEmployeeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateEmployeeDataResponse_CreateEmployeeDataResponse"));
		oper.setReturnClass(CreateEmployeeDataResponse_CreateEmployeeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateEmployeeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[92] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateAddressData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateAddressDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateAddressDataRequest_CreateAddressDataRequest"),
				CreateAddressDataRequest_CreateAddressDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateAddressDataResponse_CreateAddressDataResponse"));
		oper.setReturnClass(CreateAddressDataResponse_CreateAddressDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateAddressDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[93] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("TIPTOPGateWay");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "TIPTOPGateWayRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"TIPTOPGateWayRequest_TIPTOPGateWayRequest"), TIPTOPGateWayRequest_TIPTOPGateWayRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"TIPTOPGateWayResponse_TIPTOPGateWayResponse"));
		oper.setReturnClass(TIPTOPGateWayResponse_TIPTOPGateWayResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"TIPTOPGateWayResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[94] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateBillingAP");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateBillingAPRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateBillingAPRequest_CreateBillingAPRequest"),
				CreateBillingAPRequest_CreateBillingAPRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBillingAPResponse_CreateBillingAPResponse"));
		oper.setReturnClass(CreateBillingAPResponse_CreateBillingAPResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBillingAPResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[95] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateCustomerOtheraddressData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateCustomerOtheraddressDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateCustomerOtheraddressDataRequest_CreateCustomerOtheraddressDataRequest"),
				CreateCustomerOtheraddressDataRequest_CreateCustomerOtheraddressDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse"));
		oper.setReturnClass(CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerOtheraddressDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[96] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreatePotentialCustomerData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreatePotentialCustomerDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreatePotentialCustomerDataRequest_CreatePotentialCustomerDataRequest"),
				CreatePotentialCustomerDataRequest_CreatePotentialCustomerDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse"));
		oper.setReturnClass(CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePotentialCustomerDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[97] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustomerContactData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustomerContactDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCustomerContactDataRequest_GetCustomerContactDataRequest"),
				GetCustomerContactDataRequest_GetCustomerContactDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerContactDataResponse_GetCustomerContactDataResponse"));
		oper.setReturnClass(GetCustomerContactDataResponse_GetCustomerContactDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerContactDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[98] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustomerOtheraddressData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustomerOtheraddressDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCustomerOtheraddressDataRequest_GetCustomerOtheraddressDataRequest"),
				GetCustomerOtheraddressDataRequest_GetCustomerOtheraddressDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse"));
		oper.setReturnClass(GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerOtheraddressDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[99] = oper;

	}

	private static void _initOperationDesc11()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetItemStockList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemStockListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetItemStockListRequest_GetItemStockListRequest"),
				GetItemStockListRequest_GetItemStockListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemStockListResponse_GetItemStockListResponse"));
		oper.setReturnClass(GetItemStockListResponse_GetItemStockListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemStockListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[100] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMFGSettingSmaData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMFGSettingSmaDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetMFGSettingSmaDataRequest_GetMFGSettingSmaDataRequest"),
				GetMFGSettingSmaDataRequest_GetMFGSettingSmaDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse"));
		oper.setReturnClass(GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGSettingSmaDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[101] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPackingMethodData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPackingMethodDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPackingMethodDataRequest_GetPackingMethodDataRequest"),
				GetPackingMethodDataRequest_GetPackingMethodDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPackingMethodDataResponse_GetPackingMethodDataResponse"));
		oper.setReturnClass(GetPackingMethodDataResponse_GetPackingMethodDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPackingMethodDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[102] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPotentialCustomerData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPotentialCustomerDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPotentialCustomerDataRequest_GetPotentialCustomerDataRequest"),
				GetPotentialCustomerDataRequest_GetPotentialCustomerDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse"));
		oper.setReturnClass(GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPotentialCustomerDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[103] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetTableAmendmentData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetTableAmendmentDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetTableAmendmentDataRequest_GetTableAmendmentDataRequest"),
				GetTableAmendmentDataRequest_GetTableAmendmentDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTableAmendmentDataResponse_GetTableAmendmentDataResponse"));
		oper.setReturnClass(GetTableAmendmentDataResponse_GetTableAmendmentDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTableAmendmentDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[104] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetTaxTypeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetTaxTypeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetTaxTypeDataRequest_GetTaxTypeDataRequest"),
				GetTaxTypeDataRequest_GetTaxTypeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTaxTypeDataResponse_GetTaxTypeDataResponse"));
		oper.setReturnClass(GetTaxTypeDataResponse_GetTaxTypeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTaxTypeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[105] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetUnitConversionData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetUnitConversionDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetUnitConversionDataRequest_GetUnitConversionDataRequest"),
				GetUnitConversionDataRequest_GetUnitConversionDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitConversionDataResponse_GetUnitConversionDataResponse"));
		oper.setReturnClass(GetUnitConversionDataResponse_GetUnitConversionDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitConversionDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[106] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetUnitData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetUnitDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetUnitDataRequest_GetUnitDataRequest"),
				GetUnitDataRequest_GetUnitDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitDataResponse_GetUnitDataResponse"));
		oper.setReturnClass(GetUnitDataResponse_GetUnitDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[107] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetReportData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetReportDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetReportDataRequest_GetReportDataRequest"), GetReportDataRequest_GetReportDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReportDataResponse_GetReportDataResponse"));
		oper.setReturnClass(GetReportDataResponse_GetReportDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReportDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[108] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CRMGetCustomerData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CRMGetCustomerDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CRMGetCustomerDataRequest_CRMGetCustomerDataRequest"),
				CRMGetCustomerDataRequest_CRMGetCustomerDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CRMGetCustomerDataResponse_CRMGetCustomerDataResponse"));
		oper.setReturnClass(CRMGetCustomerDataResponse_CRMGetCustomerDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CRMGetCustomerDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[109] = oper;

	}

	private static void _initOperationDesc12()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateCustomerContactData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateCustomerContactDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateCustomerContactDataRequest_CreateCustomerContactDataRequest"),
				CreateCustomerContactDataRequest_CreateCustomerContactDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerContactDataResponse_CreateCustomerContactDataResponse"));
		oper.setReturnClass(CreateCustomerContactDataResponse_CreateCustomerContactDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerContactDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[110] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateDepartmentData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateDepartmentDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateDepartmentDataRequest_CreateDepartmentDataRequest"),
				CreateDepartmentDataRequest_CreateDepartmentDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateDepartmentDataResponse_CreateDepartmentDataResponse"));
		oper.setReturnClass(CreateDepartmentDataResponse_CreateDepartmentDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateDepartmentDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[111] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetAccountTypeData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetAccountTypeDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetAccountTypeDataRequest_GetAccountTypeDataRequest"),
				GetAccountTypeDataRequest_GetAccountTypeDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountTypeDataResponse_GetAccountTypeDataResponse"));
		oper.setReturnClass(GetAccountTypeDataResponse_GetAccountTypeDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountTypeDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[112] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetTransactionCategory");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetTransactionCategoryRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetTransactionCategoryRequest_GetTransactionCategoryRequest"),
				GetTransactionCategoryRequest_GetTransactionCategoryRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTransactionCategoryResponse_GetTransactionCategoryResponse"));
		oper.setReturnClass(GetTransactionCategoryResponse_GetTransactionCategoryResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTransactionCategoryResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[113] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetVoucherDocumentData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetVoucherDocumentDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetVoucherDocumentDataRequest_GetVoucherDocumentDataRequest"),
				GetVoucherDocumentDataRequest_GetVoucherDocumentDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse"));
		oper.setReturnClass(GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetVoucherDocumentDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[114] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("RollbackVoucherData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "RollbackVoucherDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"RollbackVoucherDataRequest_RollbackVoucherDataRequest"),
				RollbackVoucherDataRequest_RollbackVoucherDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RollbackVoucherDataResponse_RollbackVoucherDataResponse"));
		oper.setReturnClass(RollbackVoucherDataResponse_RollbackVoucherDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RollbackVoucherDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[115] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCardDetailData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCardDetailDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCardDetailDataRequest_GetCardDetailDataRequest"),
				GetCardDetailDataRequest_GetCardDetailDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCardDetailDataResponse_GetCardDetailDataResponse"));
		oper.setReturnClass(GetCardDetailDataResponse_GetCardDetailDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCardDetailDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[116] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetOnlineUser");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetOnlineUserRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetOnlineUserRequest_GetOnlineUserRequest"), GetOnlineUserRequest_GetOnlineUserRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOnlineUserResponse_GetOnlineUserResponse"));
		oper.setReturnClass(GetOnlineUserResponse_GetOnlineUserResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOnlineUserResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[117] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetProdInfo");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetProdInfoRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetProdInfoRequest_GetProdInfoRequest"),
				GetProdInfoRequest_GetProdInfoRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdInfoResponse_GetProdInfoResponse"));
		oper.setReturnClass(GetProdInfoResponse_GetProdInfoResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdInfoResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[118] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMemberData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMemberDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetMemberDataRequest_GetMemberDataRequest"), GetMemberDataRequest_GetMemberDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberDataResponse_GetMemberDataResponse"));
		oper.setReturnClass(GetMemberDataResponse_GetMemberDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[119] = oper;

	}

	private static void _initOperationDesc13()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMachineData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMachineDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetMachineDataRequest_GetMachineDataRequest"),
				GetMachineDataRequest_GetMachineDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMachineDataResponse_GetMachineDataResponse"));
		oper.setReturnClass(GetMachineDataResponse_GetMachineDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMachineDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[120] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetProdRoutingData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetProdRoutingDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetProdRoutingDataRequest_GetProdRoutingDataRequest"),
				GetProdRoutingDataRequest_GetProdRoutingDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdRoutingDataResponse_GetProdRoutingDataResponse"));
		oper.setReturnClass(GetProdRoutingDataResponse_GetProdRoutingDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdRoutingDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[121] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetWorkstationData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetWorkstationDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetWorkstationDataRequest_GetWorkstationDataRequest"),
				GetWorkstationDataRequest_GetWorkstationDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWorkstationDataResponse_GetWorkstationDataResponse"));
		oper.setReturnClass(GetWorkstationDataResponse_GetWorkstationDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWorkstationDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[122] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateRepSubPBOMData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateRepSubPBOMDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateRepSubPBOMDataRequest_CreateRepSubPBOMDataRequest"),
				CreateRepSubPBOMDataRequest_CreateRepSubPBOMDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse"));
		oper.setReturnClass(CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateRepSubPBOMDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[123] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetBrandData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetBrandDataRequest"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetBrandDataRequest_GetBrandDataRequest"), GetBrandDataRequest_GetBrandDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBrandDataResponse_GetBrandDataResponse"));
		oper.setReturnClass(GetBrandDataResponse_GetBrandDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBrandDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[124] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateItemApprovalData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateItemApprovalDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateItemApprovalDataRequest_CreateItemApprovalDataRequest"),
				CreateItemApprovalDataRequest_CreateItemApprovalDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemApprovalDataResponse_CreateItemApprovalDataResponse"));
		oper.setReturnClass(CreateItemApprovalDataResponse_CreateItemApprovalDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemApprovalDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[125] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetItemOtherGroupData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemOtherGroupDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetItemOtherGroupDataRequest_GetItemOtherGroupDataRequest"),
				GetItemOtherGroupDataRequest_GetItemOtherGroupDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse"));
		oper.setReturnClass(GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemOtherGroupDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[126] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateSupplierItemData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateSupplierItemDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateSupplierItemDataRequest_CreateSupplierItemDataRequest"),
				CreateSupplierItemDataRequest_CreateSupplierItemDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateSupplierItemDataResponse_CreateSupplierItemDataResponse"));
		oper.setReturnClass(CreateSupplierItemDataResponse_CreateSupplierItemDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateSupplierItemDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[127] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateWOWorkReportData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateWOWorkReportDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateWOWorkReportDataRequest_CreateWOWorkReportDataRequest"),
				CreateWOWorkReportDataRequest_CreateWOWorkReportDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse"));
		oper.setReturnClass(CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOWorkReportDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[128] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateBOMData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateBOMDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateBOMDataRequest_CreateBOMDataRequest"), CreateBOMDataRequest_CreateBOMDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDataResponse_CreateBOMDataResponse"));
		oper.setReturnClass(CreateBOMDataResponse_CreateBOMDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[129] = oper;

	}

	private static void _initOperationDesc14()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateShippingOrdersWithoutOrders");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateShippingOrdersWithoutOrdersRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateShippingOrdersWithoutOrdersRequest_CreateShippingOrdersWithoutOrdersRequest"),
				CreateShippingOrdersWithoutOrdersRequest_CreateShippingOrdersWithoutOrdersRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse"));
		oper.setReturnClass(CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrdersWithoutOrdersResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[130] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetItemGroupData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemGroupDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetItemGroupDataRequest_GetItemGroupDataRequest"),
				GetItemGroupDataRequest_GetItemGroupDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemGroupDataResponse_GetItemGroupDataResponse"));
		oper.setReturnClass(GetItemGroupDataResponse_GetItemGroupDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemGroupDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[131] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetProdState");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetProdStateRequest"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetProdStateRequest_GetProdStateRequest"), GetProdStateRequest_GetProdStateRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdStateResponse_GetProdStateResponse"));
		oper.setReturnClass(GetProdStateResponse_GetProdStateResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdStateResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[132] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetPaymentTermsData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetPaymentTermsDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetPaymentTermsDataRequest_GetPaymentTermsDataRequest"),
				GetPaymentTermsDataRequest_GetPaymentTermsDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPaymentTermsDataResponse_GetPaymentTermsDataResponse"));
		oper.setReturnClass(GetPaymentTermsDataResponse_GetPaymentTermsDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPaymentTermsDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[133] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSSOKey");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSSOKeyRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSSOKeyRequest_GetSSOKeyRequest"),
				GetSSOKeyRequest_GetSSOKeyRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSSOKeyResponse_GetSSOKeyResponse"));
		oper.setReturnClass(GetSSOKeyResponse_GetSSOKeyResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSSOKeyResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[134] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreateECNData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreateECNDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreateECNDataRequest_CreateECNDataRequest"), CreateECNDataRequest_CreateECNDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateECNDataResponse_CreateECNDataResponse"));
		oper.setReturnClass(CreateECNDataResponse_CreateECNDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateECNDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[135] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("CreatePLMBOMData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "CreatePLMBOMDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"CreatePLMBOMDataRequest_CreatePLMBOMDataRequest"),
				CreatePLMBOMDataRequest_CreatePLMBOMDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMBOMDataResponse_CreatePLMBOMDataResponse"));
		oper.setReturnClass(CreatePLMBOMDataResponse_CreatePLMBOMDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMBOMDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[136] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetUserDefOrg");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetUserDefOrgRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetUserDefOrgRequest_GetUserDefOrgRequest"), GetUserDefOrgRequest_GetUserDefOrgRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserDefOrgResponse_GetUserDefOrgResponse"));
		oper.setReturnClass(GetUserDefOrgResponse_GetUserDefOrgResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserDefOrgResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[137] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetMemberCardData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetMemberCardDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetMemberCardDataRequest_GetMemberCardDataRequest"),
				GetMemberCardDataRequest_GetMemberCardDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberCardDataResponse_GetMemberCardDataResponse"));
		oper.setReturnClass(GetMemberCardDataResponse_GetMemberCardDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberCardDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[138] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("UpdatePaymentInfo");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "UpdatePaymentInfoRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"UpdatePaymentInfoRequest_UpdatePaymentInfoRequest"),
				UpdatePaymentInfoRequest_UpdatePaymentInfoRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdatePaymentInfoResponse_UpdatePaymentInfoResponse"));
		oper.setReturnClass(UpdatePaymentInfoResponse_UpdatePaymentInfoResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdatePaymentInfoResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[139] = oper;

	}

	private static void _initOperationDesc15()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetShappingData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetShappingDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetShappingDataRequest_GetShappingDataRequest"),
				GetShappingDataRequest_GetShappingDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShappingDataResponse_GetShappingDataResponse"));
		oper.setReturnClass(GetShappingDataResponse_GetShappingDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShappingDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[140] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCustList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCustListRequest_GetCustListRequest"),
				GetCustListRequest_GetCustListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustListResponse_GetCustListResponse"));
		oper.setReturnClass(GetCustListResponse_GetCustListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[141] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetQuotationData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetQuotationDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetQuotationDataRequest_GetQuotationDataRequest"),
				GetQuotationDataRequest_GetQuotationDataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQuotationDataResponse_GetQuotationDataResponse"));
		oper.setReturnClass(GetQuotationDataResponse_GetQuotationDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQuotationDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[142] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetSOData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSODataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetSODataRequest_GetSODataRequest"),
				GetSODataRequest_GetSODataRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSODataResponse_GetSODataResponse"));
		oper.setReturnClass(GetSODataResponse_GetSODataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSODataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[143] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetCouponData");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetCouponDataRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"GetCouponDataRequest_GetCouponDataRequest"), GetCouponDataRequest_GetCouponDataRequest.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCouponDataResponse_GetCouponDataResponse"));
		oper.setReturnClass(GetCouponDataResponse_GetCouponDataResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCouponDataResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[144] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("GetItemList");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemListRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "GetItemListRequest_GetItemListRequest"),
				GetItemListRequest_GetItemListRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemListResponse_GetItemListResponse"));
		oper.setReturnClass(GetItemListResponse_GetItemListResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemListResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[145] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("UpdateMemberPoint");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName(
				"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay", "UpdateMemberPointRequest"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
						"UpdateMemberPointRequest_UpdateMemberPointRequest"),
				UpdateMemberPointRequest_UpdateMemberPointRequest.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateMemberPointResponse_UpdateMemberPointResponse"));
		oper.setReturnClass(UpdateMemberPointResponse_UpdateMemberPointResponse.class);
		oper.setReturnQName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateMemberPointResponse"));
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[146] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("invokeSrv");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "response"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[147] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("callbackSrv");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "response"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[148] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("syncProd");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "response"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[149] = oper;

	}

	private static void _initOperationDesc16()
	{
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("invokeMdm");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "request"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		oper.setReturnClass(java.lang.String.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "response"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.LITERAL);
		_operations[150] = oper;

	}

	public TIPTOPServiceGateWayBindingStub() throws org.apache.axis.AxisFault
	{
		this(null);
	}

	public TIPTOPServiceGateWayBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault
	{
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public TIPTOPServiceGateWayBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault
	{
		if (service == null)
		{
			super.service = new org.apache.axis.client.Service();
		}
		else
		{
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		addBindings0();
		addBindings1();
		addBindings2();
	}

	private void addBindings0()
	{
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckApsExecutionRequest_CheckApsExecutionRequest");
		cachedSerQNames.add(qName);
		cls = CheckApsExecutionRequest_CheckApsExecutionRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckApsExecutionResponse_CheckApsExecutionResponse");
		cachedSerQNames.add(qName);
		cls = CheckApsExecutionResponse_CheckApsExecutionResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckExecAuthorizationRequest_CheckExecAuthorizationRequest");
		cachedSerQNames.add(qName);
		cls = CheckExecAuthorizationRequest_CheckExecAuthorizationRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckExecAuthorizationResponse_CheckExecAuthorizationResponse");
		cachedSerQNames.add(qName);
		cls = CheckExecAuthorizationResponse_CheckExecAuthorizationResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckUserAuthRequest_CheckUserAuthRequest");
		cachedSerQNames.add(qName);
		cls = CheckUserAuthRequest_CheckUserAuthRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CheckUserAuthResponse_CheckUserAuthResponse");
		cachedSerQNames.add(qName);
		cls = CheckUserAuthResponse_CheckUserAuthResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateAddressDataRequest_CreateAddressDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateAddressDataRequest_CreateAddressDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateAddressDataResponse_CreateAddressDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateAddressDataResponse_CreateAddressDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBillingAPRequest_CreateBillingAPRequest");
		cachedSerQNames.add(qName);
		cls = CreateBillingAPRequest_CreateBillingAPRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBillingAPResponse_CreateBillingAPResponse");
		cachedSerQNames.add(qName);
		cls = CreateBillingAPResponse_CreateBillingAPResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDataRequest_CreateBOMDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateBOMDataRequest_CreateBOMDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDataResponse_CreateBOMDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateBOMDataResponse_CreateBOMDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDetailDataRequest_CreateBOMDetailDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateBOMDetailDataRequest_CreateBOMDetailDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMDetailDataResponse_CreateBOMDetailDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateBOMDetailDataResponse_CreateBOMDetailDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMMasterDataRequest_CreateBOMMasterDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateBOMMasterDataRequest_CreateBOMMasterDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateBOMMasterDataResponse_CreateBOMMasterDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateBOMMasterDataResponse_CreateBOMMasterDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerContactDataRequest_CreateCustomerContactDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateCustomerContactDataRequest_CreateCustomerContactDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerContactDataResponse_CreateCustomerContactDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateCustomerContactDataResponse_CreateCustomerContactDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerDataRequest_CreateCustomerDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateCustomerDataRequest_CreateCustomerDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerDataResponse_CreateCustomerDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateCustomerDataResponse_CreateCustomerDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerOtheraddressDataRequest_CreateCustomerOtheraddressDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateCustomerOtheraddressDataRequest_CreateCustomerOtheraddressDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateDepartmentDataRequest_CreateDepartmentDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateDepartmentDataRequest_CreateDepartmentDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateDepartmentDataResponse_CreateDepartmentDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateDepartmentDataResponse_CreateDepartmentDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateECNDataRequest_CreateECNDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateECNDataRequest_CreateECNDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateECNDataResponse_CreateECNDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateECNDataResponse_CreateECNDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateEmployeeDataRequest_CreateEmployeeDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateEmployeeDataRequest_CreateEmployeeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateEmployeeDataResponse_CreateEmployeeDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateEmployeeDataResponse_CreateEmployeeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateIssueReturnDataRequest_CreateIssueReturnDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateIssueReturnDataRequest_CreateIssueReturnDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateIssueReturnDataResponse_CreateIssueReturnDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateIssueReturnDataResponse_CreateIssueReturnDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemApprovalDataRequest_CreateItemApprovalDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateItemApprovalDataRequest_CreateItemApprovalDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemApprovalDataResponse_CreateItemApprovalDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateItemApprovalDataResponse_CreateItemApprovalDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemMasterDataRequest_CreateItemMasterDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateItemMasterDataRequest_CreateItemMasterDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateItemMasterDataResponse_CreateItemMasterDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateItemMasterDataResponse_CreateItemMasterDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateMISCIssueDataRequest_CreateMISCIssueDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateMISCIssueDataRequest_CreateMISCIssueDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateMISCIssueDataResponse_CreateMISCIssueDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateMISCIssueDataResponse_CreateMISCIssueDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMBOMDataRequest_CreatePLMBOMDataRequest");
		cachedSerQNames.add(qName);
		cls = CreatePLMBOMDataRequest_CreatePLMBOMDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMBOMDataResponse_CreatePLMBOMDataResponse");
		cachedSerQNames.add(qName);
		cls = CreatePLMBOMDataResponse_CreatePLMBOMDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest");
		cachedSerQNames.add(qName);
		cls = CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse");
		cachedSerQNames.add(qName);
		cls = CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePOReceivingDataRequest_CreatePOReceivingDataRequest");
		cachedSerQNames.add(qName);
		cls = CreatePOReceivingDataRequest_CreatePOReceivingDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePOReceivingDataResponse_CreatePOReceivingDataResponse");
		cachedSerQNames.add(qName);
		cls = CreatePOReceivingDataResponse_CreatePOReceivingDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePotentialCustomerDataRequest_CreatePotentialCustomerDataRequest");
		cachedSerQNames.add(qName);
		cls = CreatePotentialCustomerDataRequest_CreatePotentialCustomerDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse");
		cachedSerQNames.add(qName);
		cls = CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockInRequest_CreatePurchaseStockInRequest");
		cachedSerQNames.add(qName);
		cls = CreatePurchaseStockInRequest_CreatePurchaseStockInRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockInResponse_CreatePurchaseStockInResponse");
		cachedSerQNames.add(qName);
		cls = CreatePurchaseStockInResponse_CreatePurchaseStockInResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockOutRequest_CreatePurchaseStockOutRequest");
		cachedSerQNames.add(qName);
		cls = CreatePurchaseStockOutRequest_CreatePurchaseStockOutRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse");
		cachedSerQNames.add(qName);
		cls = CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateQuotationDataRequest_CreateQuotationDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateQuotationDataRequest_CreateQuotationDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateQuotationDataResponse_CreateQuotationDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateQuotationDataResponse_CreateQuotationDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateRepSubPBOMDataRequest_CreateRepSubPBOMDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateRepSubPBOMDataRequest_CreateRepSubPBOMDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrderRequest_CreateShippingOrderRequest");
		cachedSerQNames.add(qName);
		cls = CreateShippingOrderRequest_CreateShippingOrderRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrderResponse_CreateShippingOrderResponse");
		cachedSerQNames.add(qName);
		cls = CreateShippingOrderResponse_CreateShippingOrderResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrdersWithoutOrdersRequest_CreateShippingOrdersWithoutOrdersRequest");
		cachedSerQNames.add(qName);
		cls = CreateShippingOrdersWithoutOrdersRequest_CreateShippingOrdersWithoutOrdersRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse");
		cachedSerQNames.add(qName);
		cls = CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockDataRequest_CreateStockDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateStockDataRequest_CreateStockDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockDataResponse_CreateStockDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateStockDataResponse_CreateStockDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockInDataRequest_CreateStockInDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateStockInDataRequest_CreateStockInDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateStockInDataResponse_CreateStockInDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateStockInDataResponse_CreateStockInDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateSupplierItemDataRequest_CreateSupplierItemDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateSupplierItemDataRequest_CreateSupplierItemDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateSupplierItemDataResponse_CreateSupplierItemDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateSupplierItemDataResponse_CreateSupplierItemDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateTransferNoteRequest_CreateTransferNoteRequest");
		cachedSerQNames.add(qName);
		cls = CreateTransferNoteRequest_CreateTransferNoteRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateTransferNoteResponse_CreateTransferNoteResponse");
		cachedSerQNames.add(qName);
		cls = CreateTransferNoteResponse_CreateTransferNoteResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVendorDataRequest_CreateVendorDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateVendorDataRequest_CreateVendorDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVendorDataResponse_CreateVendorDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateVendorDataResponse_CreateVendorDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVoucherDataRequest_CreateVoucherDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateVoucherDataRequest_CreateVoucherDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateVoucherDataResponse_CreateVoucherDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateVoucherDataResponse_CreateVoucherDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOStockinDataRequest_CreateWOStockinDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateWOStockinDataRequest_CreateWOStockinDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOStockinDataResponse_CreateWOStockinDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateWOStockinDataResponse_CreateWOStockinDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOWorkReportDataRequest_CreateWOWorkReportDataRequest");
		cachedSerQNames.add(qName);
		cls = CreateWOWorkReportDataRequest_CreateWOWorkReportDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse");
		cachedSerQNames.add(qName);
		cls = CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CRMGetCustomerDataRequest_CRMGetCustomerDataRequest");
		cachedSerQNames.add(qName);
		cls = CRMGetCustomerDataRequest_CRMGetCustomerDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"CRMGetCustomerDataResponse_CRMGetCustomerDataResponse");
		cachedSerQNames.add(qName);
		cls = CRMGetCustomerDataResponse_CRMGetCustomerDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest");
		cachedSerQNames.add(qName);
		cls = DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse");
		cachedSerQNames.add(qName);
		cls = DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetCustDataRequest_EboGetCustDataRequest");
		cachedSerQNames.add(qName);
		cls = EboGetCustDataRequest_EboGetCustDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetCustDataResponse_EboGetCustDataResponse");
		cachedSerQNames.add(qName);
		cls = EboGetCustDataResponse_EboGetCustDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetOrderDataRequest_EboGetOrderDataRequest");
		cachedSerQNames.add(qName);
		cls = EboGetOrderDataRequest_EboGetOrderDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetOrderDataResponse_EboGetOrderDataResponse");
		cachedSerQNames.add(qName);
		cls = EboGetOrderDataResponse_EboGetOrderDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetProdDataRequest_EboGetProdDataRequest");
		cachedSerQNames.add(qName);
		cls = EboGetProdDataRequest_EboGetProdDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"EboGetProdDataResponse_EboGetProdDataResponse");
		cachedSerQNames.add(qName);
		cls = EboGetProdDataResponse_EboGetProdDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountDataRequest_GetAccountDataRequest");
		cachedSerQNames.add(qName);
		cls = GetAccountDataRequest_GetAccountDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountDataResponse_GetAccountDataResponse");
		cachedSerQNames.add(qName);
		cls = GetAccountDataResponse_GetAccountDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountSubjectDataRequest_GetAccountSubjectDataRequest");
		cachedSerQNames.add(qName);
		cls = GetAccountSubjectDataRequest_GetAccountSubjectDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountSubjectDataResponse_GetAccountSubjectDataResponse");
		cachedSerQNames.add(qName);
		cls = GetAccountSubjectDataResponse_GetAccountSubjectDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountTypeDataRequest_GetAccountTypeDataRequest");
		cachedSerQNames.add(qName);
		cls = GetAccountTypeDataRequest_GetAccountTypeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAccountTypeDataResponse_GetAccountTypeDataResponse");
		cachedSerQNames.add(qName);
		cls = GetAccountTypeDataResponse_GetAccountTypeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaDataRequest_GetAreaDataRequest");
		cachedSerQNames.add(qName);
		cls = GetAreaDataRequest_GetAreaDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaDataResponse_GetAreaDataResponse");
		cachedSerQNames.add(qName);
		cls = GetAreaDataResponse_GetAreaDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaListRequest_GetAreaListRequest");
		cachedSerQNames.add(qName);
		cls = GetAreaListRequest_GetAreaListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAreaListResponse_GetAreaListResponse");
		cachedSerQNames.add(qName);
		cls = GetAreaListResponse_GetAreaListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAxmDocumentRequest_GetAxmDocumentRequest");
		cachedSerQNames.add(qName);
		cls = GetAxmDocumentRequest_GetAxmDocumentRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetAxmDocumentResponse_GetAxmDocumentResponse");
		cachedSerQNames.add(qName);
		cls = GetAxmDocumentResponse_GetAxmDocumentResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBasicCodeDataRequest_GetBasicCodeDataRequest");
		cachedSerQNames.add(qName);
		cls = GetBasicCodeDataRequest_GetBasicCodeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBasicCodeDataResponse_GetBasicCodeDataResponse");
		cachedSerQNames.add(qName);
		cls = GetBasicCodeDataResponse_GetBasicCodeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBOMDataRequest_GetBOMDataRequest");
		cachedSerQNames.add(qName);
		cls = GetBOMDataRequest_GetBOMDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBOMDataResponse_GetBOMDataResponse");
		cachedSerQNames.add(qName);
		cls = GetBOMDataResponse_GetBOMDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBrandDataRequest_GetBrandDataRequest");
		cachedSerQNames.add(qName);
		cls = GetBrandDataRequest_GetBrandDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetBrandDataResponse_GetBrandDataResponse");
		cachedSerQNames.add(qName);
		cls = GetBrandDataResponse_GetBrandDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	private void addBindings1()
	{
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCardDetailDataRequest_GetCardDetailDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCardDetailDataRequest_GetCardDetailDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCardDetailDataResponse_GetCardDetailDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCardDetailDataResponse_GetCardDetailDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetComponentrepsubDataRequest_GetComponentrepsubDataRequest");
		cachedSerQNames.add(qName);
		cls = GetComponentrepsubDataRequest_GetComponentrepsubDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetComponentrepsubDataResponse_GetComponentrepsubDataResponse");
		cachedSerQNames.add(qName);
		cls = GetComponentrepsubDataResponse_GetComponentrepsubDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCostGroupDataRequest_GetCostGroupDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCostGroupDataRequest_GetCostGroupDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCostGroupDataResponse_GetCostGroupDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCostGroupDataResponse_GetCostGroupDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountingLabelDataRequest_GetCountingLabelDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCountingLabelDataRequest_GetCountingLabelDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountingLabelDataResponse_GetCountingLabelDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCountingLabelDataResponse_GetCountingLabelDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryDataRequest_GetCountryDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCountryDataRequest_GetCountryDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryDataResponse_GetCountryDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCountryDataResponse_GetCountryDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryListRequest_GetCountryListRequest");
		cachedSerQNames.add(qName);
		cls = GetCountryListRequest_GetCountryListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCountryListResponse_GetCountryListResponse");
		cachedSerQNames.add(qName);
		cls = GetCountryListResponse_GetCountryListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCouponDataRequest_GetCouponDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCouponDataRequest_GetCouponDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCouponDataResponse_GetCouponDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCouponDataResponse_GetCouponDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyDataRequest_GetCurrencyDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCurrencyDataRequest_GetCurrencyDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyDataResponse_GetCurrencyDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCurrencyDataResponse_GetCurrencyDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyListRequest_GetCurrencyListRequest");
		cachedSerQNames.add(qName);
		cls = GetCurrencyListRequest_GetCurrencyListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCurrencyListResponse_GetCurrencyListResponse");
		cachedSerQNames.add(qName);
		cls = GetCurrencyListResponse_GetCurrencyListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustClassificationDataRequest_GetCustClassificationDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCustClassificationDataRequest_GetCustClassificationDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustClassificationDataResponse_GetCustClassificationDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCustClassificationDataResponse_GetCustClassificationDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustListRequest_GetCustListRequest");
		cachedSerQNames.add(qName);
		cls = GetCustListRequest_GetCustListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustListResponse_GetCustListResponse");
		cachedSerQNames.add(qName);
		cls = GetCustListResponse_GetCustListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerAccAmtDataRequest_GetCustomerAccAmtDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCustomerAccAmtDataRequest_GetCustomerAccAmtDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerContactDataRequest_GetCustomerContactDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCustomerContactDataRequest_GetCustomerContactDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerContactDataResponse_GetCustomerContactDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCustomerContactDataResponse_GetCustomerContactDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerDataRequest_GetCustomerDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCustomerDataRequest_GetCustomerDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerDataResponse_GetCustomerDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCustomerDataResponse_GetCustomerDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerOtheraddressDataRequest_GetCustomerOtheraddressDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCustomerOtheraddressDataRequest_GetCustomerOtheraddressDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerProductDataRequest_GetCustomerProductDataRequest");
		cachedSerQNames.add(qName);
		cls = GetCustomerProductDataRequest_GetCustomerProductDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetCustomerProductDataResponse_GetCustomerProductDataResponse");
		cachedSerQNames.add(qName);
		cls = GetCustomerProductDataResponse_GetCustomerProductDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDataCountRequest_GetDataCountRequest");
		cachedSerQNames.add(qName);
		cls = GetDataCountRequest_GetDataCountRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDataCountResponse_GetDataCountResponse");
		cachedSerQNames.add(qName);
		cls = GetDataCountResponse_GetDataCountResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentDataRequest_GetDepartmentDataRequest");
		cachedSerQNames.add(qName);
		cls = GetDepartmentDataRequest_GetDepartmentDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentDataResponse_GetDepartmentDataResponse");
		cachedSerQNames.add(qName);
		cls = GetDepartmentDataResponse_GetDepartmentDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentListRequest_GetDepartmentListRequest");
		cachedSerQNames.add(qName);
		cls = GetDepartmentListRequest_GetDepartmentListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDepartmentListResponse_GetDepartmentListResponse");
		cachedSerQNames.add(qName);
		cls = GetDepartmentListResponse_GetDepartmentListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDocumentNumberRequest_GetDocumentNumberRequest");
		cachedSerQNames.add(qName);
		cls = GetDocumentNumberRequest_GetDocumentNumberRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetDocumentNumberResponse_GetDocumentNumberResponse");
		cachedSerQNames.add(qName);
		cls = GetDocumentNumberResponse_GetDocumentNumberResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeDataRequest_GetEmployeeDataRequest");
		cachedSerQNames.add(qName);
		cls = GetEmployeeDataRequest_GetEmployeeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeDataResponse_GetEmployeeDataResponse");
		cachedSerQNames.add(qName);
		cls = GetEmployeeDataResponse_GetEmployeeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeListRequest_GetEmployeeListRequest");
		cachedSerQNames.add(qName);
		cls = GetEmployeeListRequest_GetEmployeeListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetEmployeeListResponse_GetEmployeeListResponse");
		cachedSerQNames.add(qName);
		cls = GetEmployeeListResponse_GetEmployeeListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetFQCDataRequest_GetFQCDataRequest");
		cachedSerQNames.add(qName);
		cls = GetFQCDataRequest_GetFQCDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetFQCDataResponse_GetFQCDataResponse");
		cachedSerQNames.add(qName);
		cls = GetFQCDataResponse_GetFQCDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInspectionDataRequest_GetInspectionDataRequest");
		cachedSerQNames.add(qName);
		cls = GetInspectionDataRequest_GetInspectionDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInspectionDataResponse_GetInspectionDataResponse");
		cachedSerQNames.add(qName);
		cls = GetInspectionDataResponse_GetInspectionDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInvoiceTypeListRequest_GetInvoiceTypeListRequest");
		cachedSerQNames.add(qName);
		cls = GetInvoiceTypeListRequest_GetInvoiceTypeListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetInvoiceTypeListResponse_GetInvoiceTypeListResponse");
		cachedSerQNames.add(qName);
		cls = GetInvoiceTypeListResponse_GetInvoiceTypeListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemDataRequest_GetItemDataRequest");
		cachedSerQNames.add(qName);
		cls = GetItemDataRequest_GetItemDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemDataResponse_GetItemDataResponse");
		cachedSerQNames.add(qName);
		cls = GetItemDataResponse_GetItemDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemGroupDataRequest_GetItemGroupDataRequest");
		cachedSerQNames.add(qName);
		cls = GetItemGroupDataRequest_GetItemGroupDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemGroupDataResponse_GetItemGroupDataResponse");
		cachedSerQNames.add(qName);
		cls = GetItemGroupDataResponse_GetItemGroupDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemListRequest_GetItemListRequest");
		cachedSerQNames.add(qName);
		cls = GetItemListRequest_GetItemListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemListResponse_GetItemListResponse");
		cachedSerQNames.add(qName);
		cls = GetItemListResponse_GetItemListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemOtherGroupDataRequest_GetItemOtherGroupDataRequest");
		cachedSerQNames.add(qName);
		cls = GetItemOtherGroupDataRequest_GetItemOtherGroupDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse");
		cachedSerQNames.add(qName);
		cls = GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemStockListRequest_GetItemStockListRequest");
		cachedSerQNames.add(qName);
		cls = GetItemStockListRequest_GetItemStockListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetItemStockListResponse_GetItemStockListResponse");
		cachedSerQNames.add(qName);
		cls = GetItemStockListResponse_GetItemStockListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLabelTypeDataRequest_GetLabelTypeDataRequest");
		cachedSerQNames.add(qName);
		cls = GetLabelTypeDataRequest_GetLabelTypeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLabelTypeDataResponse_GetLabelTypeDataResponse");
		cachedSerQNames.add(qName);
		cls = GetLabelTypeDataResponse_GetLabelTypeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLocationDataRequest_GetLocationDataRequest");
		cachedSerQNames.add(qName);
		cls = GetLocationDataRequest_GetLocationDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetLocationDataResponse_GetLocationDataResponse");
		cachedSerQNames.add(qName);
		cls = GetLocationDataResponse_GetLocationDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMachineDataRequest_GetMachineDataRequest");
		cachedSerQNames.add(qName);
		cls = GetMachineDataRequest_GetMachineDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMachineDataResponse_GetMachineDataResponse");
		cachedSerQNames.add(qName);
		cls = GetMachineDataResponse_GetMachineDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberCardDataRequest_GetMemberCardDataRequest");
		cachedSerQNames.add(qName);
		cls = GetMemberCardDataRequest_GetMemberCardDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberCardDataResponse_GetMemberCardDataResponse");
		cachedSerQNames.add(qName);
		cls = GetMemberCardDataResponse_GetMemberCardDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberDataRequest_GetMemberDataRequest");
		cachedSerQNames.add(qName);
		cls = GetMemberDataRequest_GetMemberDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMemberDataResponse_GetMemberDataResponse");
		cachedSerQNames.add(qName);
		cls = GetMemberDataResponse_GetMemberDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMenuDataRequest_GetMenuDataRequest");
		cachedSerQNames.add(qName);
		cls = GetMenuDataRequest_GetMenuDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMenuDataResponse_GetMenuDataResponse");
		cachedSerQNames.add(qName);
		cls = GetMenuDataResponse_GetMenuDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGDocumentRequest_GetMFGDocumentRequest");
		cachedSerQNames.add(qName);
		cls = GetMFGDocumentRequest_GetMFGDocumentRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGDocumentResponse_GetMFGDocumentResponse");
		cachedSerQNames.add(qName);
		cls = GetMFGDocumentResponse_GetMFGDocumentResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGSettingSmaDataRequest_GetMFGSettingSmaDataRequest");
		cachedSerQNames.add(qName);
		cls = GetMFGSettingSmaDataRequest_GetMFGSettingSmaDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse");
		cachedSerQNames.add(qName);
		cls = GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMonthListRequest_GetMonthListRequest");
		cachedSerQNames.add(qName);
		cls = GetMonthListRequest_GetMonthListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetMonthListResponse_GetMonthListResponse");
		cachedSerQNames.add(qName);
		cls = GetMonthListResponse_GetMonthListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOnlineUserRequest_GetOnlineUserRequest");
		cachedSerQNames.add(qName);
		cls = GetOnlineUserRequest_GetOnlineUserRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOnlineUserResponse_GetOnlineUserResponse");
		cachedSerQNames.add(qName);
		cls = GetOnlineUserResponse_GetOnlineUserResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOrganizationListRequest_GetOrganizationListRequest");
		cachedSerQNames.add(qName);
		cls = GetOrganizationListRequest_GetOrganizationListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOrganizationListResponse_GetOrganizationListResponse");
		cachedSerQNames.add(qName);
		cls = GetOrganizationListResponse_GetOrganizationListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtDetailDataRequest_GetOverdueAmtDetailDataRequest");
		cachedSerQNames.add(qName);
		cls = GetOverdueAmtDetailDataRequest_GetOverdueAmtDetailDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse");
		cachedSerQNames.add(qName);
		cls = GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtRankingDataRequest_GetOverdueAmtRankingDataRequest");
		cachedSerQNames.add(qName);
		cls = GetOverdueAmtRankingDataRequest_GetOverdueAmtRankingDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse");
		cachedSerQNames.add(qName);
		cls = GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPackingMethodDataRequest_GetPackingMethodDataRequest");
		cachedSerQNames.add(qName);
		cls = GetPackingMethodDataRequest_GetPackingMethodDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPackingMethodDataResponse_GetPackingMethodDataResponse");
		cachedSerQNames.add(qName);
		cls = GetPackingMethodDataResponse_GetPackingMethodDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPaymentTermsDataRequest_GetPaymentTermsDataRequest");
		cachedSerQNames.add(qName);
		cls = GetPaymentTermsDataRequest_GetPaymentTermsDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPaymentTermsDataResponse_GetPaymentTermsDataResponse");
		cachedSerQNames.add(qName);
		cls = GetPaymentTermsDataResponse_GetPaymentTermsDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest");
		cachedSerQNames.add(qName);
		cls = GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse");
		cachedSerQNames.add(qName);
		cls = GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPODataRequest_GetPODataRequest");
		cachedSerQNames.add(qName);
		cls = GetPODataRequest_GetPODataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPODataResponse_GetPODataResponse");
		cachedSerQNames.add(qName);
		cls = GetPODataResponse_GetPODataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingInDataRequest_GetPOReceivingInDataRequest");
		cachedSerQNames.add(qName);
		cls = GetPOReceivingInDataRequest_GetPOReceivingInDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingInDataResponse_GetPOReceivingInDataResponse");
		cachedSerQNames.add(qName);
		cls = GetPOReceivingInDataResponse_GetPOReceivingInDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingOutDataRequest_GetPOReceivingOutDataRequest");
		cachedSerQNames.add(qName);
		cls = GetPOReceivingOutDataRequest_GetPOReceivingOutDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse");
		cachedSerQNames.add(qName);
		cls = GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPotentialCustomerDataRequest_GetPotentialCustomerDataRequest");
		cachedSerQNames.add(qName);
		cls = GetPotentialCustomerDataRequest_GetPotentialCustomerDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse");
		cachedSerQNames.add(qName);
		cls = GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	private void addBindings2()
	{
		java.lang.Class cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdClassListRequest_GetProdClassListRequest");
		cachedSerQNames.add(qName);
		cls = GetProdClassListRequest_GetProdClassListRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdClassListResponse_GetProdClassListResponse");
		cachedSerQNames.add(qName);
		cls = GetProdClassListResponse_GetProdClassListResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdInfoRequest_GetProdInfoRequest");
		cachedSerQNames.add(qName);
		cls = GetProdInfoRequest_GetProdInfoRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdInfoResponse_GetProdInfoResponse");
		cachedSerQNames.add(qName);
		cls = GetProdInfoResponse_GetProdInfoResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdRoutingDataRequest_GetProdRoutingDataRequest");
		cachedSerQNames.add(qName);
		cls = GetProdRoutingDataRequest_GetProdRoutingDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdRoutingDataResponse_GetProdRoutingDataResponse");
		cachedSerQNames.add(qName);
		cls = GetProdRoutingDataResponse_GetProdRoutingDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdStateRequest_GetProdStateRequest");
		cachedSerQNames.add(qName);
		cls = GetProdStateRequest_GetProdStateRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProdStateResponse_GetProdStateResponse");
		cachedSerQNames.add(qName);
		cls = GetProdStateResponse_GetProdStateResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProductClassDataRequest_GetProductClassDataRequest");
		cachedSerQNames.add(qName);
		cls = GetProductClassDataRequest_GetProductClassDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetProductClassDataResponse_GetProductClassDataResponse");
		cachedSerQNames.add(qName);
		cls = GetProductClassDataResponse_GetProductClassDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockInQtyRequest_GetPurchaseStockInQtyRequest");
		cachedSerQNames.add(qName);
		cls = GetPurchaseStockInQtyRequest_GetPurchaseStockInQtyRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse");
		cachedSerQNames.add(qName);
		cls = GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockOutQtyRequest_GetPurchaseStockOutQtyRequest");
		cachedSerQNames.add(qName);
		cls = GetPurchaseStockOutQtyRequest_GetPurchaseStockOutQtyRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse");
		cachedSerQNames.add(qName);
		cls = GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQtyConversionRequest_GetQtyConversionRequest");
		cachedSerQNames.add(qName);
		cls = GetQtyConversionRequest_GetQtyConversionRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQtyConversionResponse_GetQtyConversionResponse");
		cachedSerQNames.add(qName);
		cls = GetQtyConversionResponse_GetQtyConversionResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQuotationDataRequest_GetQuotationDataRequest");
		cachedSerQNames.add(qName);
		cls = GetQuotationDataRequest_GetQuotationDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetQuotationDataResponse_GetQuotationDataResponse");
		cachedSerQNames.add(qName);
		cls = GetQuotationDataResponse_GetQuotationDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReasonCodeRequest_GetReasonCodeRequest");
		cachedSerQNames.add(qName);
		cls = GetReasonCodeRequest_GetReasonCodeRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReasonCodeResponse_GetReasonCodeResponse");
		cachedSerQNames.add(qName);
		cls = GetReasonCodeResponse_GetReasonCodeResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReceivingQtyRequest_GetReceivingQtyRequest");
		cachedSerQNames.add(qName);
		cls = GetReceivingQtyRequest_GetReceivingQtyRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReceivingQtyResponse_GetReceivingQtyResponse");
		cachedSerQNames.add(qName);
		cls = GetReceivingQtyResponse_GetReceivingQtyResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReportDataRequest_GetReportDataRequest");
		cachedSerQNames.add(qName);
		cls = GetReportDataRequest_GetReportDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetReportDataResponse_GetReportDataResponse");
		cachedSerQNames.add(qName);
		cls = GetReportDataResponse_GetReportDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDetailDataRequest_GetSalesDetailDataRequest");
		cachedSerQNames.add(qName);
		cls = GetSalesDetailDataRequest_GetSalesDetailDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDetailDataResponse_GetSalesDetailDataResponse");
		cachedSerQNames.add(qName);
		cls = GetSalesDetailDataResponse_GetSalesDetailDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDocumentRequest_GetSalesDocumentRequest");
		cachedSerQNames.add(qName);
		cls = GetSalesDocumentRequest_GetSalesDocumentRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesDocumentResponse_GetSalesDocumentResponse");
		cachedSerQNames.add(qName);
		cls = GetSalesDocumentResponse_GetSalesDocumentResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesStatisticsDataRequest_GetSalesStatisticsDataRequest");
		cachedSerQNames.add(qName);
		cls = GetSalesStatisticsDataRequest_GetSalesStatisticsDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse");
		cachedSerQNames.add(qName);
		cls = GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShappingDataRequest_GetShappingDataRequest");
		cachedSerQNames.add(qName);
		cls = GetShappingDataRequest_GetShappingDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShappingDataResponse_GetShappingDataResponse");
		cachedSerQNames.add(qName);
		cls = GetShappingDataResponse_GetShappingDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingNoticeDataRequest_GetShippingNoticeDataRequest");
		cachedSerQNames.add(qName);
		cls = GetShippingNoticeDataRequest_GetShippingNoticeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingNoticeDataResponse_GetShippingNoticeDataResponse");
		cachedSerQNames.add(qName);
		cls = GetShippingNoticeDataResponse_GetShippingNoticeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingOrderDataRequest_GetShippingOrderDataRequest");
		cachedSerQNames.add(qName);
		cls = GetShippingOrderDataRequest_GetShippingOrderDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetShippingOrderDataResponse_GetShippingOrderDataResponse");
		cachedSerQNames.add(qName);
		cls = GetShippingOrderDataResponse_GetShippingOrderDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSODataRequest_GetSODataRequest");
		cachedSerQNames.add(qName);
		cls = GetSODataRequest_GetSODataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSODataResponse_GetSODataResponse");
		cachedSerQNames.add(qName);
		cls = GetSODataResponse_GetSODataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDataRequest_GetSOInfoDataRequest");
		cachedSerQNames.add(qName);
		cls = GetSOInfoDataRequest_GetSOInfoDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDataResponse_GetSOInfoDataResponse");
		cachedSerQNames.add(qName);
		cls = GetSOInfoDataResponse_GetSOInfoDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDetailDataRequest_GetSOInfoDetailDataRequest");
		cachedSerQNames.add(qName);
		cls = GetSOInfoDetailDataRequest_GetSOInfoDetailDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse");
		cachedSerQNames.add(qName);
		cls = GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSSOKeyRequest_GetSSOKeyRequest");
		cachedSerQNames.add(qName);
		cls = GetSSOKeyRequest_GetSSOKeyRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSSOKeyResponse_GetSSOKeyResponse");
		cachedSerQNames.add(qName);
		cls = GetSSOKeyResponse_GetSSOKeyResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetStockDataRequest_GetStockDataRequest");
		cachedSerQNames.add(qName);
		cls = GetStockDataRequest_GetStockDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetStockDataResponse_GetStockDataResponse");
		cachedSerQNames.add(qName);
		cls = GetStockDataResponse_GetStockDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierDataRequest_GetSupplierDataRequest");
		cachedSerQNames.add(qName);
		cls = GetSupplierDataRequest_GetSupplierDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierDataResponse_GetSupplierDataResponse");
		cachedSerQNames.add(qName);
		cls = GetSupplierDataResponse_GetSupplierDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierItemDataRequest_GetSupplierItemDataRequest");
		cachedSerQNames.add(qName);
		cls = GetSupplierItemDataRequest_GetSupplierItemDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetSupplierItemDataResponse_GetSupplierItemDataResponse");
		cachedSerQNames.add(qName);
		cls = GetSupplierItemDataResponse_GetSupplierItemDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTableAmendmentDataRequest_GetTableAmendmentDataRequest");
		cachedSerQNames.add(qName);
		cls = GetTableAmendmentDataRequest_GetTableAmendmentDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTableAmendmentDataResponse_GetTableAmendmentDataResponse");
		cachedSerQNames.add(qName);
		cls = GetTableAmendmentDataResponse_GetTableAmendmentDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTaxTypeDataRequest_GetTaxTypeDataRequest");
		cachedSerQNames.add(qName);
		cls = GetTaxTypeDataRequest_GetTaxTypeDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTaxTypeDataResponse_GetTaxTypeDataResponse");
		cachedSerQNames.add(qName);
		cls = GetTaxTypeDataResponse_GetTaxTypeDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTradeTermDataRequest_GetTradeTermDataRequest");
		cachedSerQNames.add(qName);
		cls = GetTradeTermDataRequest_GetTradeTermDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTradeTermDataResponse_GetTradeTermDataResponse");
		cachedSerQNames.add(qName);
		cls = GetTradeTermDataResponse_GetTradeTermDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTransactionCategoryRequest_GetTransactionCategoryRequest");
		cachedSerQNames.add(qName);
		cls = GetTransactionCategoryRequest_GetTransactionCategoryRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetTransactionCategoryResponse_GetTransactionCategoryResponse");
		cachedSerQNames.add(qName);
		cls = GetTransactionCategoryResponse_GetTransactionCategoryResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitConversionDataRequest_GetUnitConversionDataRequest");
		cachedSerQNames.add(qName);
		cls = GetUnitConversionDataRequest_GetUnitConversionDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitConversionDataResponse_GetUnitConversionDataResponse");
		cachedSerQNames.add(qName);
		cls = GetUnitConversionDataResponse_GetUnitConversionDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitDataRequest_GetUnitDataRequest");
		cachedSerQNames.add(qName);
		cls = GetUnitDataRequest_GetUnitDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUnitDataResponse_GetUnitDataResponse");
		cachedSerQNames.add(qName);
		cls = GetUnitDataResponse_GetUnitDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserDefOrgRequest_GetUserDefOrgRequest");
		cachedSerQNames.add(qName);
		cls = GetUserDefOrgRequest_GetUserDefOrgRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserDefOrgResponse_GetUserDefOrgResponse");
		cachedSerQNames.add(qName);
		cls = GetUserDefOrgResponse_GetUserDefOrgResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserTokenRequest_GetUserTokenRequest");
		cachedSerQNames.add(qName);
		cls = GetUserTokenRequest_GetUserTokenRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetUserTokenResponse_GetUserTokenResponse");
		cachedSerQNames.add(qName);
		cls = GetUserTokenResponse_GetUserTokenResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetVoucherDocumentDataRequest_GetVoucherDocumentDataRequest");
		cachedSerQNames.add(qName);
		cls = GetVoucherDocumentDataRequest_GetVoucherDocumentDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse");
		cachedSerQNames.add(qName);
		cls = GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWarehouseDataRequest_GetWarehouseDataRequest");
		cachedSerQNames.add(qName);
		cls = GetWarehouseDataRequest_GetWarehouseDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWarehouseDataResponse_GetWarehouseDataResponse");
		cachedSerQNames.add(qName);
		cls = GetWarehouseDataResponse_GetWarehouseDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWODataRequest_GetWODataRequest");
		cachedSerQNames.add(qName);
		cls = GetWODataRequest_GetWODataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWODataResponse_GetWODataResponse");
		cachedSerQNames.add(qName);
		cls = GetWODataResponse_GetWODataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOIssueDataRequest_GetWOIssueDataRequest");
		cachedSerQNames.add(qName);
		cls = GetWOIssueDataRequest_GetWOIssueDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOIssueDataResponse_GetWOIssueDataResponse");
		cachedSerQNames.add(qName);
		cls = GetWOIssueDataResponse_GetWOIssueDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWorkstationDataRequest_GetWorkstationDataRequest");
		cachedSerQNames.add(qName);
		cls = GetWorkstationDataRequest_GetWorkstationDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWorkstationDataResponse_GetWorkstationDataResponse");
		cachedSerQNames.add(qName);
		cls = GetWorkstationDataResponse_GetWorkstationDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOStockQtyRequest_GetWOStockQtyRequest");
		cachedSerQNames.add(qName);
		cls = GetWOStockQtyRequest_GetWOStockQtyRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"GetWOStockQtyResponse_GetWOStockQtyResponse");
		cachedSerQNames.add(qName);
		cls = GetWOStockQtyResponse_GetWOStockQtyResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RollbackVoucherDataRequest_RollbackVoucherDataRequest");
		cachedSerQNames.add(qName);
		cls = RollbackVoucherDataRequest_RollbackVoucherDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RollbackVoucherDataResponse_RollbackVoucherDataResponse");
		cachedSerQNames.add(qName);
		cls = RollbackVoucherDataResponse_RollbackVoucherDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RunCommandRequest_RunCommandRequest");
		cachedSerQNames.add(qName);
		cls = RunCommandRequest_RunCommandRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"RunCommandResponse_RunCommandResponse");
		cachedSerQNames.add(qName);
		cls = RunCommandResponse_RunCommandResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"SyncAccountDataRequest_SyncAccountDataRequest");
		cachedSerQNames.add(qName);
		cls = SyncAccountDataRequest_SyncAccountDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"SyncAccountDataResponse_SyncAccountDataResponse");
		cachedSerQNames.add(qName);
		cls = SyncAccountDataResponse_SyncAccountDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"TIPTOPGateWayRequest_TIPTOPGateWayRequest");
		cachedSerQNames.add(qName);
		cls = TIPTOPGateWayRequest_TIPTOPGateWayRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"TIPTOPGateWayResponse_TIPTOPGateWayResponse");
		cachedSerQNames.add(qName);
		cls = TIPTOPGateWayResponse_TIPTOPGateWayResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateCountingLabelDataRequest_UpdateCountingLabelDataRequest");
		cachedSerQNames.add(qName);
		cls = UpdateCountingLabelDataRequest_UpdateCountingLabelDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse");
		cachedSerQNames.add(qName);
		cls = UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateMemberPointRequest_UpdateMemberPointRequest");
		cachedSerQNames.add(qName);
		cls = UpdateMemberPointRequest_UpdateMemberPointRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateMemberPointResponse_UpdateMemberPointResponse");
		cachedSerQNames.add(qName);
		cls = UpdateMemberPointResponse_UpdateMemberPointResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdatePaymentInfoRequest_UpdatePaymentInfoRequest");
		cachedSerQNames.add(qName);
		cls = UpdatePaymentInfoRequest_UpdatePaymentInfoRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdatePaymentInfoResponse_UpdatePaymentInfoResponse");
		cachedSerQNames.add(qName);
		cls = UpdatePaymentInfoResponse_UpdatePaymentInfoResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateWOIssueDataRequest_UpdateWOIssueDataRequest");
		cachedSerQNames.add(qName);
		cls = UpdateWOIssueDataRequest_UpdateWOIssueDataRequest.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"UpdateWOIssueDataResponse_UpdateWOIssueDataResponse");
		cachedSerQNames.add(qName);
		cls = UpdateWOIssueDataResponse_UpdateWOIssueDataResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException
	{
		try
		{
			org.apache.axis.client.Call _call = super._createCall();
			if (super.maintainSessionSet)
			{
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null)
			{
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null)
			{
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null)
			{
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null)
			{
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null)
			{
				_call.setPortName(super.cachedPortName);
			}
			java.util.Enumeration keys = super.cachedProperties.keys();
			while (keys.hasMoreElements())
			{
				java.lang.String key = (java.lang.String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized (this)
			{
				if (firstCall())
				{
					// must set encoding style before registering serializers
					_call.setEncodingStyle(null);
					for (int i = 0; i < cachedSerFactories.size(); ++i)
					{
						java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
						javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
						java.lang.Object x = cachedSerFactories.get(i);
						if (x instanceof Class)
						{
							java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
							java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
						else if (x instanceof javax.xml.rpc.encoding.SerializerFactory)
						{
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
									.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
									.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		}
		catch (java.lang.Throwable _t)
		{
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse getCustomerAccAmtData(
			GetCustomerAccAmtDataRequest_GetCustomerAccAmtDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustomerAccAmtData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCustClassificationDataResponse_GetCustClassificationDataResponse getCustClassificationData(
			GetCustClassificationDataRequest_GetCustClassificationDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustClassificationData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustClassificationDataResponse_GetCustClassificationDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustClassificationDataResponse_GetCustClassificationDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetCustClassificationDataResponse_GetCustClassificationDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetInvoiceTypeListResponse_GetInvoiceTypeListResponse getInvoiceTypeList(
			GetInvoiceTypeListRequest_GetInvoiceTypeListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetInvoiceTypeList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetInvoiceTypeListResponse_GetInvoiceTypeListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetInvoiceTypeListResponse_GetInvoiceTypeListResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetInvoiceTypeListResponse_GetInvoiceTypeListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetTradeTermDataResponse_GetTradeTermDataResponse getTradeTermData(
			GetTradeTermDataRequest_GetTradeTermDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetTradeTermData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetTradeTermDataResponse_GetTradeTermDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetTradeTermDataResponse_GetTradeTermDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetTradeTermDataResponse_GetTradeTermDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetDataCountResponse_GetDataCountResponse getDataCount(GetDataCountRequest_GetDataCountRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetDataCount"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetDataCountResponse_GetDataCountResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetDataCountResponse_GetDataCountResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetDataCountResponse_GetDataCountResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public SyncAccountDataResponse_SyncAccountDataResponse syncAccountData(
			SyncAccountDataRequest_SyncAccountDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "SyncAccountData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (SyncAccountDataResponse_SyncAccountDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (SyncAccountDataResponse_SyncAccountDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, SyncAccountDataResponse_SyncAccountDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse createPLMTempTableData(
			CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreatePLMTempTableData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse deletePLMTempTableData(
			DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[7]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "DeletePLMTempTableData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse getPLMTempTableDataStatus(
			GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[8]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPLMTempTableDataStatus"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetAreaDataResponse_GetAreaDataResponse getAreaData(GetAreaDataRequest_GetAreaDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[9]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetAreaData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetAreaDataResponse_GetAreaDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetAreaDataResponse_GetAreaDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetAreaDataResponse_GetAreaDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetAreaListResponse_GetAreaListResponse getAreaList(GetAreaListRequest_GetAreaListRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[10]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetAreaList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetAreaListResponse_GetAreaListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetAreaListResponse_GetAreaListResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetAreaListResponse_GetAreaListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetAxmDocumentResponse_GetAxmDocumentResponse getAxmDocument(
			GetAxmDocumentRequest_GetAxmDocumentRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[11]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetAxmDocument"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetAxmDocumentResponse_GetAxmDocumentResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetAxmDocumentResponse_GetAxmDocumentResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetAxmDocumentResponse_GetAxmDocumentResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse getPurchaseStockInQty(
			GetPurchaseStockInQtyRequest_GetPurchaseStockInQtyRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[12]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPurchaseStockInQty"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetBasicCodeDataResponse_GetBasicCodeDataResponse getBasicCodeData(
			GetBasicCodeDataRequest_GetBasicCodeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[13]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetBasicCodeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetBasicCodeDataResponse_GetBasicCodeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetBasicCodeDataResponse_GetBasicCodeDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetBasicCodeDataResponse_GetBasicCodeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetComponentrepsubDataResponse_GetComponentrepsubDataResponse getComponentrepsubData(
			GetComponentrepsubDataRequest_GetComponentrepsubDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[14]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetComponentrepsubData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetComponentrepsubDataResponse_GetComponentrepsubDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetComponentrepsubDataResponse_GetComponentrepsubDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetComponentrepsubDataResponse_GetComponentrepsubDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCostGroupDataResponse_GetCostGroupDataResponse getCostGroupData(
			GetCostGroupDataRequest_GetCostGroupDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[15]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCostGroupData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCostGroupDataResponse_GetCostGroupDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCostGroupDataResponse_GetCostGroupDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetCostGroupDataResponse_GetCostGroupDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCountryDataResponse_GetCountryDataResponse getCountryData(
			GetCountryDataRequest_GetCountryDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[16]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCountryData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCountryDataResponse_GetCountryDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCountryDataResponse_GetCountryDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetCountryDataResponse_GetCountryDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCountryListResponse_GetCountryListResponse getCountryList(
			GetCountryListRequest_GetCountryListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[17]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCountryList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCountryListResponse_GetCountryListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCountryListResponse_GetCountryListResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetCountryListResponse_GetCountryListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCurrencyDataResponse_GetCurrencyDataResponse getCurrencyData(
			GetCurrencyDataRequest_GetCurrencyDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[18]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCurrencyData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCurrencyDataResponse_GetCurrencyDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCurrencyDataResponse_GetCurrencyDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetCurrencyDataResponse_GetCurrencyDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCurrencyListResponse_GetCurrencyListResponse getCurrencyList(
			GetCurrencyListRequest_GetCurrencyListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[19]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCurrencyList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCurrencyListResponse_GetCurrencyListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCurrencyListResponse_GetCurrencyListResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetCurrencyListResponse_GetCurrencyListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCustomerDataResponse_GetCustomerDataResponse getCustomerData(
			GetCustomerDataRequest_GetCustomerDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[20]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustomerData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustomerDataResponse_GetCustomerDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustomerDataResponse_GetCustomerDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetCustomerDataResponse_GetCustomerDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCustomerProductDataResponse_GetCustomerProductDataResponse getCustomerProductData(
			GetCustomerProductDataRequest_GetCustomerProductDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[21]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustomerProductData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustomerProductDataResponse_GetCustomerProductDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustomerProductDataResponse_GetCustomerProductDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetCustomerProductDataResponse_GetCustomerProductDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetDepartmentDataResponse_GetDepartmentDataResponse getDepartmentData(
			GetDepartmentDataRequest_GetDepartmentDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[22]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetDepartmentData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetDepartmentDataResponse_GetDepartmentDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetDepartmentDataResponse_GetDepartmentDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetDepartmentDataResponse_GetDepartmentDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetDepartmentListResponse_GetDepartmentListResponse getDepartmentList(
			GetDepartmentListRequest_GetDepartmentListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[23]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetDepartmentList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetDepartmentListResponse_GetDepartmentListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetDepartmentListResponse_GetDepartmentListResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetDepartmentListResponse_GetDepartmentListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse getPOReceivingOutData(
			GetPOReceivingOutDataRequest_GetPOReceivingOutDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[24]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPOReceivingOutData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetEmployeeDataResponse_GetEmployeeDataResponse getEmployeeData(
			GetEmployeeDataRequest_GetEmployeeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[25]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetEmployeeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetEmployeeDataResponse_GetEmployeeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetEmployeeDataResponse_GetEmployeeDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetEmployeeDataResponse_GetEmployeeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetEmployeeListResponse_GetEmployeeListResponse getEmployeeList(
			GetEmployeeListRequest_GetEmployeeListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[26]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetEmployeeList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetEmployeeListResponse_GetEmployeeListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetEmployeeListResponse_GetEmployeeListResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetEmployeeListResponse_GetEmployeeListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetInspectionDataResponse_GetInspectionDataResponse getInspectionData(
			GetInspectionDataRequest_GetInspectionDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[27]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetInspectionData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetInspectionDataResponse_GetInspectionDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetInspectionDataResponse_GetInspectionDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetInspectionDataResponse_GetInspectionDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse createPurchaseStockOut(
			CreatePurchaseStockOutRequest_CreatePurchaseStockOutRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[28]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreatePurchaseStockOut"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetLocationDataResponse_GetLocationDataResponse getLocationData(
			GetLocationDataRequest_GetLocationDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[29]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetLocationData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetLocationDataResponse_GetLocationDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetLocationDataResponse_GetLocationDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetLocationDataResponse_GetLocationDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMonthListResponse_GetMonthListResponse getMonthList(GetMonthListRequest_GetMonthListRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[30]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMonthList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMonthListResponse_GetMonthListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMonthListResponse_GetMonthListResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetMonthListResponse_GetMonthListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse getOverdueAmtDetailData(
			GetOverdueAmtDetailDataRequest_GetOverdueAmtDetailDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[31]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetOverdueAmtDetailData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse getOverdueAmtRankingData(
			GetOverdueAmtRankingDataRequest_GetOverdueAmtRankingDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[32]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetOverdueAmtRankingData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetProdClassListResponse_GetProdClassListResponse getProdClassList(
			GetProdClassListRequest_GetProdClassListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[33]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetProdClassList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetProdClassListResponse_GetProdClassListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetProdClassListResponse_GetProdClassListResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetProdClassListResponse_GetProdClassListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetProductClassDataResponse_GetProductClassDataResponse getProductClassData(
			GetProductClassDataRequest_GetProductClassDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[34]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetProductClassData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetProductClassDataResponse_GetProductClassDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetProductClassDataResponse_GetProductClassDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetProductClassDataResponse_GetProductClassDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSOInfoDataResponse_GetSOInfoDataResponse getSOInfoData(
			GetSOInfoDataRequest_GetSOInfoDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[35]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSOInfoData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSOInfoDataResponse_GetSOInfoDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSOInfoDataResponse_GetSOInfoDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetSOInfoDataResponse_GetSOInfoDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse getSOInfoDetailData(
			GetSOInfoDetailDataRequest_GetSOInfoDetailDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[36]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSOInfoDetailData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSalesDetailDataResponse_GetSalesDetailDataResponse getSalesDetailData(
			GetSalesDetailDataRequest_GetSalesDetailDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[37]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSalesDetailData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSalesDetailDataResponse_GetSalesDetailDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSalesDetailDataResponse_GetSalesDetailDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetSalesDetailDataResponse_GetSalesDetailDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse getSalesStatisticsData(
			GetSalesStatisticsDataRequest_GetSalesStatisticsDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[38]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSalesStatisticsData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSupplierDataResponse_GetSupplierDataResponse getSupplierData(
			GetSupplierDataRequest_GetSupplierDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[39]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSupplierData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSupplierDataResponse_GetSupplierDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSupplierDataResponse_GetSupplierDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetSupplierDataResponse_GetSupplierDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSupplierItemDataResponse_GetSupplierItemDataResponse getSupplierItemData(
			GetSupplierItemDataRequest_GetSupplierItemDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[40]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSupplierItemData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSupplierItemDataResponse_GetSupplierItemDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSupplierItemDataResponse_GetSupplierItemDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetSupplierItemDataResponse_GetSupplierItemDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetWarehouseDataResponse_GetWarehouseDataResponse getWarehouseData(
			GetWarehouseDataRequest_GetWarehouseDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[41]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetWarehouseData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetWarehouseDataResponse_GetWarehouseDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetWarehouseDataResponse_GetWarehouseDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetWarehouseDataResponse_GetWarehouseDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetItemDataResponse_GetItemDataResponse getItemData(GetItemDataRequest_GetItemDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[42]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetItemData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetItemDataResponse_GetItemDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetItemDataResponse_GetItemDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetItemDataResponse_GetItemDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetBOMDataResponse_GetBOMDataResponse getBOMData(GetBOMDataRequest_GetBOMDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[43]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetBOMData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetBOMDataResponse_GetBOMDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetBOMDataResponse_GetBOMDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetBOMDataResponse_GetBOMDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetDocumentNumberResponse_GetDocumentNumberResponse getDocumentNumber(
			GetDocumentNumberRequest_GetDocumentNumberRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[44]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetDocumentNumber"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetDocumentNumberResponse_GetDocumentNumberResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetDocumentNumberResponse_GetDocumentNumberResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetDocumentNumberResponse_GetDocumentNumberResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateQuotationDataResponse_CreateQuotationDataResponse createQuotationData(
			CreateQuotationDataRequest_CreateQuotationDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[45]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateQuotationData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateQuotationDataResponse_CreateQuotationDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateQuotationDataResponse_CreateQuotationDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateQuotationDataResponse_CreateQuotationDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetStockDataResponse_GetStockDataResponse getStockData(GetStockDataRequest_GetStockDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[46]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetStockData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetStockDataResponse_GetStockDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetStockDataResponse_GetStockDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetStockDataResponse_GetStockDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetReceivingQtyResponse_GetReceivingQtyResponse getReceivingQty(
			GetReceivingQtyRequest_GetReceivingQtyRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[47]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetReceivingQty"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetReceivingQtyResponse_GetReceivingQtyResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetReceivingQtyResponse_GetReceivingQtyResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetReceivingQtyResponse_GetReceivingQtyResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPODataResponse_GetPODataResponse getPOData(GetPODataRequest_GetPODataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[48]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPOData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPODataResponse_GetPODataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPODataResponse_GetPODataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetPODataResponse_GetPODataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMFGDocumentResponse_GetMFGDocumentResponse getMFGDocument(
			GetMFGDocumentRequest_GetMFGDocumentRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[49]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMFGDocument"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMFGDocumentResponse_GetMFGDocumentResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMFGDocumentResponse_GetMFGDocumentResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetMFGDocumentResponse_GetMFGDocumentResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreatePOReceivingDataResponse_CreatePOReceivingDataResponse createPOReceivingData(
			CreatePOReceivingDataRequest_CreatePOReceivingDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[50]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreatePOReceivingData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreatePOReceivingDataResponse_CreatePOReceivingDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreatePOReceivingDataResponse_CreatePOReceivingDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreatePOReceivingDataResponse_CreatePOReceivingDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateIssueReturnDataResponse_CreateIssueReturnDataResponse createIssueReturnData(
			CreateIssueReturnDataRequest_CreateIssueReturnDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[51]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateIssueReturnData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateIssueReturnDataResponse_CreateIssueReturnDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateIssueReturnDataResponse_CreateIssueReturnDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateIssueReturnDataResponse_CreateIssueReturnDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPOReceivingInDataResponse_GetPOReceivingInDataResponse getPOReceivingInData(
			GetPOReceivingInDataRequest_GetPOReceivingInDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[52]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPOReceivingInData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPOReceivingInDataResponse_GetPOReceivingInDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPOReceivingInDataResponse_GetPOReceivingInDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPOReceivingInDataResponse_GetPOReceivingInDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateStockInDataResponse_CreateStockInDataResponse createStockInData(
			CreateStockInDataRequest_CreateStockInDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[53]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateStockInData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateStockInDataResponse_CreateStockInDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateStockInDataResponse_CreateStockInDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateStockInDataResponse_CreateStockInDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetAccountSubjectDataResponse_GetAccountSubjectDataResponse getAccountSubjectData(
			GetAccountSubjectDataRequest_GetAccountSubjectDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[54]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetAccountSubjectData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetAccountSubjectDataResponse_GetAccountSubjectDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetAccountSubjectDataResponse_GetAccountSubjectDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetAccountSubjectDataResponse_GetAccountSubjectDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreatePurchaseStockInResponse_CreatePurchaseStockInResponse createPurchaseStockIn(
			CreatePurchaseStockInRequest_CreatePurchaseStockInRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[55]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreatePurchaseStockIn"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreatePurchaseStockInResponse_CreatePurchaseStockInResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreatePurchaseStockInResponse_CreatePurchaseStockInResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreatePurchaseStockInResponse_CreatePurchaseStockInResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse getPurchaseStockOutQty(
			GetPurchaseStockOutQtyRequest_GetPurchaseStockOutQtyRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[56]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPurchaseStockOutQty"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateTransferNoteResponse_CreateTransferNoteResponse createTransferNote(
			CreateTransferNoteRequest_CreateTransferNoteRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[57]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateTransferNote"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateTransferNoteResponse_CreateTransferNoteResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateTransferNoteResponse_CreateTransferNoteResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateTransferNoteResponse_CreateTransferNoteResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetQtyConversionResponse_GetQtyConversionResponse getQtyConversion(
			GetQtyConversionRequest_GetQtyConversionRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[58]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetQtyConversion"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetQtyConversionResponse_GetQtyConversionResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetQtyConversionResponse_GetQtyConversionResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetQtyConversionResponse_GetQtyConversionResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetShippingNoticeDataResponse_GetShippingNoticeDataResponse getShippingNoticeData(
			GetShippingNoticeDataRequest_GetShippingNoticeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[59]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetShippingNoticeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetShippingNoticeDataResponse_GetShippingNoticeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetShippingNoticeDataResponse_GetShippingNoticeDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetShippingNoticeDataResponse_GetShippingNoticeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSalesDocumentResponse_GetSalesDocumentResponse getSalesDocument(
			GetSalesDocumentRequest_GetSalesDocumentRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[60]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSalesDocument"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSalesDocumentResponse_GetSalesDocumentResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSalesDocumentResponse_GetSalesDocumentResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetSalesDocumentResponse_GetSalesDocumentResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetShippingOrderDataResponse_GetShippingOrderDataResponse getShippingOrderData(
			GetShippingOrderDataRequest_GetShippingOrderDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[61]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetShippingOrderData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetShippingOrderDataResponse_GetShippingOrderDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetShippingOrderDataResponse_GetShippingOrderDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetShippingOrderDataResponse_GetShippingOrderDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetFQCDataResponse_GetFQCDataResponse getFQCData(GetFQCDataRequest_GetFQCDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[62]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetFQCData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetFQCDataResponse_GetFQCDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetFQCDataResponse_GetFQCDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetFQCDataResponse_GetFQCDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetWODataResponse_GetWODataResponse getWOData(GetWODataRequest_GetWODataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[63]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetWOData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetWODataResponse_GetWODataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetWODataResponse_GetWODataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetWODataResponse_GetWODataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetWOStockQtyResponse_GetWOStockQtyResponse getWOStockQty(
			GetWOStockQtyRequest_GetWOStockQtyRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[64]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetWOStockQty"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetWOStockQtyResponse_GetWOStockQtyResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetWOStockQtyResponse_GetWOStockQtyResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetWOStockQtyResponse_GetWOStockQtyResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateWOStockinDataResponse_CreateWOStockinDataResponse createWOStockinData(
			CreateWOStockinDataRequest_CreateWOStockinDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[65]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateWOStockinData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateWOStockinDataResponse_CreateWOStockinDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateWOStockinDataResponse_CreateWOStockinDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateWOStockinDataResponse_CreateWOStockinDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetWOIssueDataResponse_GetWOIssueDataResponse getWOIssueData(
			GetWOIssueDataRequest_GetWOIssueDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[66]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetWOIssueData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetWOIssueDataResponse_GetWOIssueDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetWOIssueDataResponse_GetWOIssueDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetWOIssueDataResponse_GetWOIssueDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public UpdateWOIssueDataResponse_UpdateWOIssueDataResponse updateWOIssueData(
			UpdateWOIssueDataRequest_UpdateWOIssueDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[67]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "UpdateWOIssueData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (UpdateWOIssueDataResponse_UpdateWOIssueDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (UpdateWOIssueDataResponse_UpdateWOIssueDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, UpdateWOIssueDataResponse_UpdateWOIssueDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateShippingOrderResponse_CreateShippingOrderResponse createShippingOrder(
			CreateShippingOrderRequest_CreateShippingOrderRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[68]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateShippingOrder"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateShippingOrderResponse_CreateShippingOrderResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateShippingOrderResponse_CreateShippingOrderResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateShippingOrderResponse_CreateShippingOrderResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetReasonCodeResponse_GetReasonCodeResponse getReasonCode(
			GetReasonCodeRequest_GetReasonCodeRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[69]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetReasonCode"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetReasonCodeResponse_GetReasonCodeResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetReasonCodeResponse_GetReasonCodeResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetReasonCodeResponse_GetReasonCodeResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetLabelTypeDataResponse_GetLabelTypeDataResponse getLabelTypeData(
			GetLabelTypeDataRequest_GetLabelTypeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[70]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetLabelTypeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetLabelTypeDataResponse_GetLabelTypeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetLabelTypeDataResponse_GetLabelTypeDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetLabelTypeDataResponse_GetLabelTypeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCountingLabelDataResponse_GetCountingLabelDataResponse getCountingLabelData(
			GetCountingLabelDataRequest_GetCountingLabelDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[71]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCountingLabelData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCountingLabelDataResponse_GetCountingLabelDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCountingLabelDataResponse_GetCountingLabelDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetCountingLabelDataResponse_GetCountingLabelDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse updateCountingLabelData(
			UpdateCountingLabelDataRequest_UpdateCountingLabelDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[72]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "UpdateCountingLabelData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateMISCIssueDataResponse_CreateMISCIssueDataResponse createMISCIssueData(
			CreateMISCIssueDataRequest_CreateMISCIssueDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[73]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateMISCIssueData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateMISCIssueDataResponse_CreateMISCIssueDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateMISCIssueDataResponse_CreateMISCIssueDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateMISCIssueDataResponse_CreateMISCIssueDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CheckExecAuthorizationResponse_CheckExecAuthorizationResponse checkExecAuthorization(
			CheckExecAuthorizationRequest_CheckExecAuthorizationRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[74]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CheckExecAuthorization"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CheckExecAuthorizationResponse_CheckExecAuthorizationResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CheckExecAuthorizationResponse_CheckExecAuthorizationResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CheckExecAuthorizationResponse_CheckExecAuthorizationResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateStockDataResponse_CreateStockDataResponse createStockData(
			CreateStockDataRequest_CreateStockDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[75]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateStockData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateStockDataResponse_CreateStockDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateStockDataResponse_CreateStockDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, CreateStockDataResponse_CreateStockDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public EboGetCustDataResponse_EboGetCustDataResponse eboGetCustData(
			EboGetCustDataRequest_EboGetCustDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[76]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "EboGetCustData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (EboGetCustDataResponse_EboGetCustDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (EboGetCustDataResponse_EboGetCustDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, EboGetCustDataResponse_EboGetCustDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public EboGetProdDataResponse_EboGetProdDataResponse eboGetProdData(
			EboGetProdDataRequest_EboGetProdDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[77]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "EboGetProdData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (EboGetProdDataResponse_EboGetProdDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (EboGetProdDataResponse_EboGetProdDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, EboGetProdDataResponse_EboGetProdDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public EboGetOrderDataResponse_EboGetOrderDataResponse eboGetOrderData(
			EboGetOrderDataRequest_EboGetOrderDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[78]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "EboGetOrderData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (EboGetOrderDataResponse_EboGetOrderDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (EboGetOrderDataResponse_EboGetOrderDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, EboGetOrderDataResponse_EboGetOrderDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public RunCommandResponse_RunCommandResponse runCommand(RunCommandRequest_RunCommandRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[79]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "RunCommand"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (RunCommandResponse_RunCommandResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (RunCommandResponse_RunCommandResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							RunCommandResponse_RunCommandResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CheckApsExecutionResponse_CheckApsExecutionResponse checkApsExecution(
			CheckApsExecutionRequest_CheckApsExecutionRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[80]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CheckApsExecution"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CheckApsExecutionResponse_CheckApsExecutionResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CheckApsExecutionResponse_CheckApsExecutionResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CheckApsExecutionResponse_CheckApsExecutionResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetOrganizationListResponse_GetOrganizationListResponse getOrganizationList(
			GetOrganizationListRequest_GetOrganizationListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[81]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetOrganizationList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetOrganizationListResponse_GetOrganizationListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetOrganizationListResponse_GetOrganizationListResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetOrganizationListResponse_GetOrganizationListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetUserTokenResponse_GetUserTokenResponse getUserToken(GetUserTokenRequest_GetUserTokenRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[82]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetUserToken"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetUserTokenResponse_GetUserTokenResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetUserTokenResponse_GetUserTokenResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetUserTokenResponse_GetUserTokenResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CheckUserAuthResponse_CheckUserAuthResponse checkUserAuth(
			CheckUserAuthRequest_CheckUserAuthRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[83]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CheckUserAuth"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CheckUserAuthResponse_CheckUserAuthResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CheckUserAuthResponse_CheckUserAuthResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							CheckUserAuthResponse_CheckUserAuthResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMenuDataResponse_GetMenuDataResponse getMenuData(GetMenuDataRequest_GetMenuDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[84]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMenuData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMenuDataResponse_GetMenuDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMenuDataResponse_GetMenuDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetMenuDataResponse_GetMenuDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateVendorDataResponse_CreateVendorDataResponse createVendorData(
			CreateVendorDataRequest_CreateVendorDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[85]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateVendorData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateVendorDataResponse_CreateVendorDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateVendorDataResponse_CreateVendorDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, CreateVendorDataResponse_CreateVendorDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateBOMMasterDataResponse_CreateBOMMasterDataResponse createBOMMasterData(
			CreateBOMMasterDataRequest_CreateBOMMasterDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[86]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateBOMMasterData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateBOMMasterDataResponse_CreateBOMMasterDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateBOMMasterDataResponse_CreateBOMMasterDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateBOMMasterDataResponse_CreateBOMMasterDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateBOMDetailDataResponse_CreateBOMDetailDataResponse createBOMDetailData(
			CreateBOMDetailDataRequest_CreateBOMDetailDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[87]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateBOMDetailData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateBOMDetailDataResponse_CreateBOMDetailDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateBOMDetailDataResponse_CreateBOMDetailDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateBOMDetailDataResponse_CreateBOMDetailDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateVoucherDataResponse_CreateVoucherDataResponse createVoucherData(
			CreateVoucherDataRequest_CreateVoucherDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[88]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateVoucherData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateVoucherDataResponse_CreateVoucherDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateVoucherDataResponse_CreateVoucherDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateVoucherDataResponse_CreateVoucherDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetAccountDataResponse_GetAccountDataResponse getAccountData(
			GetAccountDataRequest_GetAccountDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[89]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetAccountData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetAccountDataResponse_GetAccountDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetAccountDataResponse_GetAccountDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetAccountDataResponse_GetAccountDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateCustomerDataResponse_CreateCustomerDataResponse createCustomerData(
			CreateCustomerDataRequest_CreateCustomerDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[90]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateCustomerData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateCustomerDataResponse_CreateCustomerDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateCustomerDataResponse_CreateCustomerDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateCustomerDataResponse_CreateCustomerDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateItemMasterDataResponse_CreateItemMasterDataResponse createItemMasterData(
			CreateItemMasterDataRequest_CreateItemMasterDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[91]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateItemMasterData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateItemMasterDataResponse_CreateItemMasterDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateItemMasterDataResponse_CreateItemMasterDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateItemMasterDataResponse_CreateItemMasterDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateEmployeeDataResponse_CreateEmployeeDataResponse createEmployeeData(
			CreateEmployeeDataRequest_CreateEmployeeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[92]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateEmployeeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateEmployeeDataResponse_CreateEmployeeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateEmployeeDataResponse_CreateEmployeeDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateEmployeeDataResponse_CreateEmployeeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateAddressDataResponse_CreateAddressDataResponse createAddressData(
			CreateAddressDataRequest_CreateAddressDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[93]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateAddressData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateAddressDataResponse_CreateAddressDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateAddressDataResponse_CreateAddressDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateAddressDataResponse_CreateAddressDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public TIPTOPGateWayResponse_TIPTOPGateWayResponse TIPTOPGateWay(
			TIPTOPGateWayRequest_TIPTOPGateWayRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[94]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "TIPTOPGateWay"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (TIPTOPGateWayResponse_TIPTOPGateWayResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (TIPTOPGateWayResponse_TIPTOPGateWayResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							TIPTOPGateWayResponse_TIPTOPGateWayResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateBillingAPResponse_CreateBillingAPResponse createBillingAP(
			CreateBillingAPRequest_CreateBillingAPRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[95]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateBillingAP"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateBillingAPResponse_CreateBillingAPResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateBillingAPResponse_CreateBillingAPResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, CreateBillingAPResponse_CreateBillingAPResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse createCustomerOtheraddressData(
			CreateCustomerOtheraddressDataRequest_CreateCustomerOtheraddressDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[96]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateCustomerOtheraddressData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse createPotentialCustomerData(
			CreatePotentialCustomerDataRequest_CreatePotentialCustomerDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[97]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreatePotentialCustomerData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCustomerContactDataResponse_GetCustomerContactDataResponse getCustomerContactData(
			GetCustomerContactDataRequest_GetCustomerContactDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[98]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustomerContactData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustomerContactDataResponse_GetCustomerContactDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustomerContactDataResponse_GetCustomerContactDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetCustomerContactDataResponse_GetCustomerContactDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse getCustomerOtheraddressData(
			GetCustomerOtheraddressDataRequest_GetCustomerOtheraddressDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[99]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustomerOtheraddressData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetItemStockListResponse_GetItemStockListResponse getItemStockList(
			GetItemStockListRequest_GetItemStockListRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[100]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetItemStockList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetItemStockListResponse_GetItemStockListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetItemStockListResponse_GetItemStockListResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetItemStockListResponse_GetItemStockListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse getMFGSettingSmaData(
			GetMFGSettingSmaDataRequest_GetMFGSettingSmaDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[101]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMFGSettingSmaData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPackingMethodDataResponse_GetPackingMethodDataResponse getPackingMethodData(
			GetPackingMethodDataRequest_GetPackingMethodDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[102]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPackingMethodData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPackingMethodDataResponse_GetPackingMethodDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPackingMethodDataResponse_GetPackingMethodDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPackingMethodDataResponse_GetPackingMethodDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse getPotentialCustomerData(
			GetPotentialCustomerDataRequest_GetPotentialCustomerDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[103]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPotentialCustomerData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetTableAmendmentDataResponse_GetTableAmendmentDataResponse getTableAmendmentData(
			GetTableAmendmentDataRequest_GetTableAmendmentDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[104]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetTableAmendmentData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetTableAmendmentDataResponse_GetTableAmendmentDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetTableAmendmentDataResponse_GetTableAmendmentDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetTableAmendmentDataResponse_GetTableAmendmentDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetTaxTypeDataResponse_GetTaxTypeDataResponse getTaxTypeData(
			GetTaxTypeDataRequest_GetTaxTypeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[105]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetTaxTypeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetTaxTypeDataResponse_GetTaxTypeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetTaxTypeDataResponse_GetTaxTypeDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetTaxTypeDataResponse_GetTaxTypeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetUnitConversionDataResponse_GetUnitConversionDataResponse getUnitConversionData(
			GetUnitConversionDataRequest_GetUnitConversionDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[106]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetUnitConversionData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetUnitConversionDataResponse_GetUnitConversionDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetUnitConversionDataResponse_GetUnitConversionDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetUnitConversionDataResponse_GetUnitConversionDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetUnitDataResponse_GetUnitDataResponse getUnitData(GetUnitDataRequest_GetUnitDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[107]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetUnitData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetUnitDataResponse_GetUnitDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetUnitDataResponse_GetUnitDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetUnitDataResponse_GetUnitDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetReportDataResponse_GetReportDataResponse getReportData(
			GetReportDataRequest_GetReportDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[108]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetReportData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetReportDataResponse_GetReportDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetReportDataResponse_GetReportDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetReportDataResponse_GetReportDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CRMGetCustomerDataResponse_CRMGetCustomerDataResponse CRMGetCustomerData(
			CRMGetCustomerDataRequest_CRMGetCustomerDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[109]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CRMGetCustomerData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CRMGetCustomerDataResponse_CRMGetCustomerDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CRMGetCustomerDataResponse_CRMGetCustomerDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CRMGetCustomerDataResponse_CRMGetCustomerDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateCustomerContactDataResponse_CreateCustomerContactDataResponse createCustomerContactData(
			CreateCustomerContactDataRequest_CreateCustomerContactDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[110]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateCustomerContactData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateCustomerContactDataResponse_CreateCustomerContactDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateCustomerContactDataResponse_CreateCustomerContactDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateCustomerContactDataResponse_CreateCustomerContactDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateDepartmentDataResponse_CreateDepartmentDataResponse createDepartmentData(
			CreateDepartmentDataRequest_CreateDepartmentDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[111]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateDepartmentData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateDepartmentDataResponse_CreateDepartmentDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateDepartmentDataResponse_CreateDepartmentDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateDepartmentDataResponse_CreateDepartmentDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetAccountTypeDataResponse_GetAccountTypeDataResponse getAccountTypeData(
			GetAccountTypeDataRequest_GetAccountTypeDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[112]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetAccountTypeData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetAccountTypeDataResponse_GetAccountTypeDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetAccountTypeDataResponse_GetAccountTypeDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetAccountTypeDataResponse_GetAccountTypeDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetTransactionCategoryResponse_GetTransactionCategoryResponse getTransactionCategory(
			GetTransactionCategoryRequest_GetTransactionCategoryRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[113]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetTransactionCategory"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetTransactionCategoryResponse_GetTransactionCategoryResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetTransactionCategoryResponse_GetTransactionCategoryResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetTransactionCategoryResponse_GetTransactionCategoryResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse getVoucherDocumentData(
			GetVoucherDocumentDataRequest_GetVoucherDocumentDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[114]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetVoucherDocumentData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public RollbackVoucherDataResponse_RollbackVoucherDataResponse rollbackVoucherData(
			RollbackVoucherDataRequest_RollbackVoucherDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[115]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "RollbackVoucherData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (RollbackVoucherDataResponse_RollbackVoucherDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (RollbackVoucherDataResponse_RollbackVoucherDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, RollbackVoucherDataResponse_RollbackVoucherDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCardDetailDataResponse_GetCardDetailDataResponse getCardDetailData(
			GetCardDetailDataRequest_GetCardDetailDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[116]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCardDetailData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCardDetailDataResponse_GetCardDetailDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCardDetailDataResponse_GetCardDetailDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetCardDetailDataResponse_GetCardDetailDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetOnlineUserResponse_GetOnlineUserResponse getOnlineUser(
			GetOnlineUserRequest_GetOnlineUserRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[117]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetOnlineUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetOnlineUserResponse_GetOnlineUserResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetOnlineUserResponse_GetOnlineUserResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetOnlineUserResponse_GetOnlineUserResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetProdInfoResponse_GetProdInfoResponse getProdInfo(GetProdInfoRequest_GetProdInfoRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[118]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetProdInfo"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetProdInfoResponse_GetProdInfoResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetProdInfoResponse_GetProdInfoResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetProdInfoResponse_GetProdInfoResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMemberDataResponse_GetMemberDataResponse getMemberData(
			GetMemberDataRequest_GetMemberDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[119]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMemberData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMemberDataResponse_GetMemberDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMemberDataResponse_GetMemberDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetMemberDataResponse_GetMemberDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMachineDataResponse_GetMachineDataResponse getMachineData(
			GetMachineDataRequest_GetMachineDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[120]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMachineData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMachineDataResponse_GetMachineDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMachineDataResponse_GetMachineDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetMachineDataResponse_GetMachineDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetProdRoutingDataResponse_GetProdRoutingDataResponse getProdRoutingData(
			GetProdRoutingDataRequest_GetProdRoutingDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[121]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetProdRoutingData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetProdRoutingDataResponse_GetProdRoutingDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetProdRoutingDataResponse_GetProdRoutingDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetProdRoutingDataResponse_GetProdRoutingDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetWorkstationDataResponse_GetWorkstationDataResponse getWorkstationData(
			GetWorkstationDataRequest_GetWorkstationDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[122]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetWorkstationData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetWorkstationDataResponse_GetWorkstationDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetWorkstationDataResponse_GetWorkstationDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetWorkstationDataResponse_GetWorkstationDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse createRepSubPBOMData(
			CreateRepSubPBOMDataRequest_CreateRepSubPBOMDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[123]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateRepSubPBOMData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetBrandDataResponse_GetBrandDataResponse getBrandData(GetBrandDataRequest_GetBrandDataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[124]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetBrandData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetBrandDataResponse_GetBrandDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetBrandDataResponse_GetBrandDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetBrandDataResponse_GetBrandDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateItemApprovalDataResponse_CreateItemApprovalDataResponse createItemApprovalData(
			CreateItemApprovalDataRequest_CreateItemApprovalDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[125]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateItemApprovalData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateItemApprovalDataResponse_CreateItemApprovalDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateItemApprovalDataResponse_CreateItemApprovalDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateItemApprovalDataResponse_CreateItemApprovalDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse getItemOtherGroupData(
			GetItemOtherGroupDataRequest_GetItemOtherGroupDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[126]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetItemOtherGroupData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateSupplierItemDataResponse_CreateSupplierItemDataResponse createSupplierItemData(
			CreateSupplierItemDataRequest_CreateSupplierItemDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[127]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateSupplierItemData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateSupplierItemDataResponse_CreateSupplierItemDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateSupplierItemDataResponse_CreateSupplierItemDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateSupplierItemDataResponse_CreateSupplierItemDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse createWOWorkReportData(
			CreateWOWorkReportDataRequest_CreateWOWorkReportDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[128]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateWOWorkReportData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateBOMDataResponse_CreateBOMDataResponse createBOMData(
			CreateBOMDataRequest_CreateBOMDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[129]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateBOMData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateBOMDataResponse_CreateBOMDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateBOMDataResponse_CreateBOMDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							CreateBOMDataResponse_CreateBOMDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse createShippingOrdersWithoutOrders(
			CreateShippingOrdersWithoutOrdersRequest_CreateShippingOrdersWithoutOrdersRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[130]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateShippingOrdersWithoutOrders"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse) org.apache.axis.utils.JavaUtils
							.convert(
									_resp,
									CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetItemGroupDataResponse_GetItemGroupDataResponse getItemGroupData(
			GetItemGroupDataRequest_GetItemGroupDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[131]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetItemGroupData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetItemGroupDataResponse_GetItemGroupDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetItemGroupDataResponse_GetItemGroupDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetItemGroupDataResponse_GetItemGroupDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetProdStateResponse_GetProdStateResponse getProdState(GetProdStateRequest_GetProdStateRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[132]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetProdState"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetProdStateResponse_GetProdStateResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetProdStateResponse_GetProdStateResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetProdStateResponse_GetProdStateResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetPaymentTermsDataResponse_GetPaymentTermsDataResponse getPaymentTermsData(
			GetPaymentTermsDataRequest_GetPaymentTermsDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[133]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetPaymentTermsData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetPaymentTermsDataResponse_GetPaymentTermsDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetPaymentTermsDataResponse_GetPaymentTermsDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetPaymentTermsDataResponse_GetPaymentTermsDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSSOKeyResponse_GetSSOKeyResponse getSSOKey(GetSSOKeyRequest_GetSSOKeyRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[134]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSSOKey"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSSOKeyResponse_GetSSOKeyResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSSOKeyResponse_GetSSOKeyResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetSSOKeyResponse_GetSSOKeyResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreateECNDataResponse_CreateECNDataResponse createECNData(
			CreateECNDataRequest_CreateECNDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[135]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreateECNData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreateECNDataResponse_CreateECNDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreateECNDataResponse_CreateECNDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							CreateECNDataResponse_CreateECNDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public CreatePLMBOMDataResponse_CreatePLMBOMDataResponse createPLMBOMData(
			CreatePLMBOMDataRequest_CreatePLMBOMDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[136]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "CreatePLMBOMData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (CreatePLMBOMDataResponse_CreatePLMBOMDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (CreatePLMBOMDataResponse_CreatePLMBOMDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, CreatePLMBOMDataResponse_CreatePLMBOMDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetUserDefOrgResponse_GetUserDefOrgResponse getUserDefOrg(
			GetUserDefOrgRequest_GetUserDefOrgRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[137]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetUserDefOrg"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetUserDefOrgResponse_GetUserDefOrgResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetUserDefOrgResponse_GetUserDefOrgResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetUserDefOrgResponse_GetUserDefOrgResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetMemberCardDataResponse_GetMemberCardDataResponse getMemberCardData(
			GetMemberCardDataRequest_GetMemberCardDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[138]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetMemberCardData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetMemberCardDataResponse_GetMemberCardDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetMemberCardDataResponse_GetMemberCardDataResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, GetMemberCardDataResponse_GetMemberCardDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public UpdatePaymentInfoResponse_UpdatePaymentInfoResponse updatePaymentInfo(
			UpdatePaymentInfoRequest_UpdatePaymentInfoRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[139]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "UpdatePaymentInfo"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (UpdatePaymentInfoResponse_UpdatePaymentInfoResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (UpdatePaymentInfoResponse_UpdatePaymentInfoResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, UpdatePaymentInfoResponse_UpdatePaymentInfoResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetShappingDataResponse_GetShappingDataResponse getShappingData(
			GetShappingDataRequest_GetShappingDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[140]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetShappingData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetShappingDataResponse_GetShappingDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetShappingDataResponse_GetShappingDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetShappingDataResponse_GetShappingDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCustListResponse_GetCustListResponse getCustList(GetCustListRequest_GetCustListRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[141]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCustList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCustListResponse_GetCustListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCustListResponse_GetCustListResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetCustListResponse_GetCustListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetQuotationDataResponse_GetQuotationDataResponse getQuotationData(
			GetQuotationDataRequest_GetQuotationDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[142]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetQuotationData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetQuotationDataResponse_GetQuotationDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetQuotationDataResponse_GetQuotationDataResponse) org.apache.axis.utils.JavaUtils.convert(
							_resp, GetQuotationDataResponse_GetQuotationDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetSODataResponse_GetSODataResponse getSOData(GetSODataRequest_GetSODataRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[143]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetSOData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetSODataResponse_GetSODataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetSODataResponse_GetSODataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetSODataResponse_GetSODataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetCouponDataResponse_GetCouponDataResponse getCouponData(
			GetCouponDataRequest_GetCouponDataRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[144]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetCouponData"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetCouponDataResponse_GetCouponDataResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetCouponDataResponse_GetCouponDataResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetCouponDataResponse_GetCouponDataResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public GetItemListResponse_GetItemListResponse getItemList(GetItemListRequest_GetItemListRequest parameters)
			throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[145]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "GetItemList"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (GetItemListResponse_GetItemListResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (GetItemListResponse_GetItemListResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
							GetItemListResponse_GetItemListResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	public UpdateMemberPointResponse_UpdateMemberPointResponse updateMemberPoint(
			UpdateMemberPointRequest_UpdateMemberPointRequest parameters) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[146]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("", "UpdateMemberPoint"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (UpdateMemberPointResponse_UpdateMemberPointResponse) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (UpdateMemberPointResponse_UpdateMemberPointResponse) org.apache.axis.utils.JavaUtils
							.convert(_resp, UpdateMemberPointResponse_UpdateMemberPointResponse.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	@Override
	public java.lang.String invokeSrv(java.lang.String request) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[147]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"invokeSrv"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (java.lang.String) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	@Override
	public java.lang.String callbackSrv(java.lang.String request) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[148]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"callbackSrv"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (java.lang.String) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	@Override
	public java.lang.String syncProd(java.lang.String request) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[149]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"syncProd"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (java.lang.String) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

	@Override
	public java.lang.String invokeMdm(java.lang.String request) throws java.rmi.RemoteException
	{
		if (super.cachedEndpoint == null)
		{
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[150]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay",
				"invokeMdm"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try
		{
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] { request });

			if (_resp instanceof java.rmi.RemoteException)
			{
				throw (java.rmi.RemoteException) _resp;
			}
			else
			{
				extractAttachments(_call);
				try
				{
					return (java.lang.String) _resp;
				}
				catch (java.lang.Exception _exception)
				{
					return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
				}
			}
		}
		catch (org.apache.axis.AxisFault axisFaultException)
		{
			throw axisFaultException;
		}
	}

}
