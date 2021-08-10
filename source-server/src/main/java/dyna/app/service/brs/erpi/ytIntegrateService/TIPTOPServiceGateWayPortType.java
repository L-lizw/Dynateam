/**
 * TIPTOPServiceGateWayPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.app.service.brs.erpi.ytIntegrateService;

public interface TIPTOPServiceGateWayPortType extends java.rmi.Remote
{
	public GetCustomerAccAmtDataResponse_GetCustomerAccAmtDataResponse getCustomerAccAmtData(
			GetCustomerAccAmtDataRequest_GetCustomerAccAmtDataRequest parameters) throws java.rmi.RemoteException;

	public GetCustClassificationDataResponse_GetCustClassificationDataResponse getCustClassificationData(
			GetCustClassificationDataRequest_GetCustClassificationDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetInvoiceTypeListResponse_GetInvoiceTypeListResponse getInvoiceTypeList(
			GetInvoiceTypeListRequest_GetInvoiceTypeListRequest parameters) throws java.rmi.RemoteException;

	public GetTradeTermDataResponse_GetTradeTermDataResponse getTradeTermData(
			GetTradeTermDataRequest_GetTradeTermDataRequest parameters) throws java.rmi.RemoteException;

	public GetDataCountResponse_GetDataCountResponse getDataCount(GetDataCountRequest_GetDataCountRequest parameters)
			throws java.rmi.RemoteException;

	public SyncAccountDataResponse_SyncAccountDataResponse syncAccountData(
			SyncAccountDataRequest_SyncAccountDataRequest parameters) throws java.rmi.RemoteException;

	public CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse createPLMTempTableData(
			CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest parameters) throws java.rmi.RemoteException;

	public DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse deletePLMTempTableData(
			DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest parameters) throws java.rmi.RemoteException;

	public GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse getPLMTempTableDataStatus(
			GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest parameters)
			throws java.rmi.RemoteException;

	public GetAreaDataResponse_GetAreaDataResponse getAreaData(GetAreaDataRequest_GetAreaDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetAreaListResponse_GetAreaListResponse getAreaList(GetAreaListRequest_GetAreaListRequest parameters)
			throws java.rmi.RemoteException;

	public GetAxmDocumentResponse_GetAxmDocumentResponse getAxmDocument(
			GetAxmDocumentRequest_GetAxmDocumentRequest parameters) throws java.rmi.RemoteException;

	public GetPurchaseStockInQtyResponse_GetPurchaseStockInQtyResponse getPurchaseStockInQty(
			GetPurchaseStockInQtyRequest_GetPurchaseStockInQtyRequest parameters) throws java.rmi.RemoteException;

	public GetBasicCodeDataResponse_GetBasicCodeDataResponse getBasicCodeData(
			GetBasicCodeDataRequest_GetBasicCodeDataRequest parameters) throws java.rmi.RemoteException;

	public GetComponentrepsubDataResponse_GetComponentrepsubDataResponse getComponentrepsubData(
			GetComponentrepsubDataRequest_GetComponentrepsubDataRequest parameters) throws java.rmi.RemoteException;

	public GetCostGroupDataResponse_GetCostGroupDataResponse getCostGroupData(
			GetCostGroupDataRequest_GetCostGroupDataRequest parameters) throws java.rmi.RemoteException;

	public GetCountryDataResponse_GetCountryDataResponse getCountryData(
			GetCountryDataRequest_GetCountryDataRequest parameters) throws java.rmi.RemoteException;

	public GetCountryListResponse_GetCountryListResponse getCountryList(
			GetCountryListRequest_GetCountryListRequest parameters) throws java.rmi.RemoteException;

	public GetCurrencyDataResponse_GetCurrencyDataResponse getCurrencyData(
			GetCurrencyDataRequest_GetCurrencyDataRequest parameters) throws java.rmi.RemoteException;

	public GetCurrencyListResponse_GetCurrencyListResponse getCurrencyList(
			GetCurrencyListRequest_GetCurrencyListRequest parameters) throws java.rmi.RemoteException;

	public GetCustomerDataResponse_GetCustomerDataResponse getCustomerData(
			GetCustomerDataRequest_GetCustomerDataRequest parameters) throws java.rmi.RemoteException;

	public GetCustomerProductDataResponse_GetCustomerProductDataResponse getCustomerProductData(
			GetCustomerProductDataRequest_GetCustomerProductDataRequest parameters) throws java.rmi.RemoteException;

	public GetDepartmentDataResponse_GetDepartmentDataResponse getDepartmentData(
			GetDepartmentDataRequest_GetDepartmentDataRequest parameters) throws java.rmi.RemoteException;

	public GetDepartmentListResponse_GetDepartmentListResponse getDepartmentList(
			GetDepartmentListRequest_GetDepartmentListRequest parameters) throws java.rmi.RemoteException;

	public GetPOReceivingOutDataResponse_GetPOReceivingOutDataResponse getPOReceivingOutData(
			GetPOReceivingOutDataRequest_GetPOReceivingOutDataRequest parameters) throws java.rmi.RemoteException;

	public GetEmployeeDataResponse_GetEmployeeDataResponse getEmployeeData(
			GetEmployeeDataRequest_GetEmployeeDataRequest parameters) throws java.rmi.RemoteException;

	public GetEmployeeListResponse_GetEmployeeListResponse getEmployeeList(
			GetEmployeeListRequest_GetEmployeeListRequest parameters) throws java.rmi.RemoteException;

	public GetInspectionDataResponse_GetInspectionDataResponse getInspectionData(
			GetInspectionDataRequest_GetInspectionDataRequest parameters) throws java.rmi.RemoteException;

	public CreatePurchaseStockOutResponse_CreatePurchaseStockOutResponse createPurchaseStockOut(
			CreatePurchaseStockOutRequest_CreatePurchaseStockOutRequest parameters) throws java.rmi.RemoteException;

	public GetLocationDataResponse_GetLocationDataResponse getLocationData(
			GetLocationDataRequest_GetLocationDataRequest parameters) throws java.rmi.RemoteException;

	public GetMonthListResponse_GetMonthListResponse getMonthList(GetMonthListRequest_GetMonthListRequest parameters)
			throws java.rmi.RemoteException;

	public GetOverdueAmtDetailDataResponse_GetOverdueAmtDetailDataResponse getOverdueAmtDetailData(
			GetOverdueAmtDetailDataRequest_GetOverdueAmtDetailDataRequest parameters) throws java.rmi.RemoteException;

	public GetOverdueAmtRankingDataResponse_GetOverdueAmtRankingDataResponse getOverdueAmtRankingData(
			GetOverdueAmtRankingDataRequest_GetOverdueAmtRankingDataRequest parameters) throws java.rmi.RemoteException;

	public GetProdClassListResponse_GetProdClassListResponse getProdClassList(
			GetProdClassListRequest_GetProdClassListRequest parameters) throws java.rmi.RemoteException;

	public GetProductClassDataResponse_GetProductClassDataResponse getProductClassData(
			GetProductClassDataRequest_GetProductClassDataRequest parameters) throws java.rmi.RemoteException;

	public GetSOInfoDataResponse_GetSOInfoDataResponse getSOInfoData(
			GetSOInfoDataRequest_GetSOInfoDataRequest parameters) throws java.rmi.RemoteException;

	public GetSOInfoDetailDataResponse_GetSOInfoDetailDataResponse getSOInfoDetailData(
			GetSOInfoDetailDataRequest_GetSOInfoDetailDataRequest parameters) throws java.rmi.RemoteException;

	public GetSalesDetailDataResponse_GetSalesDetailDataResponse getSalesDetailData(
			GetSalesDetailDataRequest_GetSalesDetailDataRequest parameters) throws java.rmi.RemoteException;

	public GetSalesStatisticsDataResponse_GetSalesStatisticsDataResponse getSalesStatisticsData(
			GetSalesStatisticsDataRequest_GetSalesStatisticsDataRequest parameters) throws java.rmi.RemoteException;

	public GetSupplierDataResponse_GetSupplierDataResponse getSupplierData(
			GetSupplierDataRequest_GetSupplierDataRequest parameters) throws java.rmi.RemoteException;

	public GetSupplierItemDataResponse_GetSupplierItemDataResponse getSupplierItemData(
			GetSupplierItemDataRequest_GetSupplierItemDataRequest parameters) throws java.rmi.RemoteException;

	public GetWarehouseDataResponse_GetWarehouseDataResponse getWarehouseData(
			GetWarehouseDataRequest_GetWarehouseDataRequest parameters) throws java.rmi.RemoteException;

	public GetItemDataResponse_GetItemDataResponse getItemData(GetItemDataRequest_GetItemDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetBOMDataResponse_GetBOMDataResponse getBOMData(GetBOMDataRequest_GetBOMDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetDocumentNumberResponse_GetDocumentNumberResponse getDocumentNumber(
			GetDocumentNumberRequest_GetDocumentNumberRequest parameters) throws java.rmi.RemoteException;

	public CreateQuotationDataResponse_CreateQuotationDataResponse createQuotationData(
			CreateQuotationDataRequest_CreateQuotationDataRequest parameters) throws java.rmi.RemoteException;

	public GetStockDataResponse_GetStockDataResponse getStockData(GetStockDataRequest_GetStockDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetReceivingQtyResponse_GetReceivingQtyResponse getReceivingQty(
			GetReceivingQtyRequest_GetReceivingQtyRequest parameters) throws java.rmi.RemoteException;

	public GetPODataResponse_GetPODataResponse getPOData(GetPODataRequest_GetPODataRequest parameters)
			throws java.rmi.RemoteException;

	public GetMFGDocumentResponse_GetMFGDocumentResponse getMFGDocument(
			GetMFGDocumentRequest_GetMFGDocumentRequest parameters) throws java.rmi.RemoteException;

	public CreatePOReceivingDataResponse_CreatePOReceivingDataResponse createPOReceivingData(
			CreatePOReceivingDataRequest_CreatePOReceivingDataRequest parameters) throws java.rmi.RemoteException;

	public CreateIssueReturnDataResponse_CreateIssueReturnDataResponse createIssueReturnData(
			CreateIssueReturnDataRequest_CreateIssueReturnDataRequest parameters) throws java.rmi.RemoteException;

	public GetPOReceivingInDataResponse_GetPOReceivingInDataResponse getPOReceivingInData(
			GetPOReceivingInDataRequest_GetPOReceivingInDataRequest parameters) throws java.rmi.RemoteException;

	public CreateStockInDataResponse_CreateStockInDataResponse createStockInData(
			CreateStockInDataRequest_CreateStockInDataRequest parameters) throws java.rmi.RemoteException;

	public GetAccountSubjectDataResponse_GetAccountSubjectDataResponse getAccountSubjectData(
			GetAccountSubjectDataRequest_GetAccountSubjectDataRequest parameters) throws java.rmi.RemoteException;

	public CreatePurchaseStockInResponse_CreatePurchaseStockInResponse createPurchaseStockIn(
			CreatePurchaseStockInRequest_CreatePurchaseStockInRequest parameters) throws java.rmi.RemoteException;

	public GetPurchaseStockOutQtyResponse_GetPurchaseStockOutQtyResponse getPurchaseStockOutQty(
			GetPurchaseStockOutQtyRequest_GetPurchaseStockOutQtyRequest parameters) throws java.rmi.RemoteException;

	public CreateTransferNoteResponse_CreateTransferNoteResponse createTransferNote(
			CreateTransferNoteRequest_CreateTransferNoteRequest parameters) throws java.rmi.RemoteException;

	public GetQtyConversionResponse_GetQtyConversionResponse getQtyConversion(
			GetQtyConversionRequest_GetQtyConversionRequest parameters) throws java.rmi.RemoteException;

	public GetShippingNoticeDataResponse_GetShippingNoticeDataResponse getShippingNoticeData(
			GetShippingNoticeDataRequest_GetShippingNoticeDataRequest parameters) throws java.rmi.RemoteException;

	public GetSalesDocumentResponse_GetSalesDocumentResponse getSalesDocument(
			GetSalesDocumentRequest_GetSalesDocumentRequest parameters) throws java.rmi.RemoteException;

	public GetShippingOrderDataResponse_GetShippingOrderDataResponse getShippingOrderData(
			GetShippingOrderDataRequest_GetShippingOrderDataRequest parameters) throws java.rmi.RemoteException;

	public GetFQCDataResponse_GetFQCDataResponse getFQCData(GetFQCDataRequest_GetFQCDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetWODataResponse_GetWODataResponse getWOData(GetWODataRequest_GetWODataRequest parameters)
			throws java.rmi.RemoteException;

	public GetWOStockQtyResponse_GetWOStockQtyResponse getWOStockQty(
			GetWOStockQtyRequest_GetWOStockQtyRequest parameters) throws java.rmi.RemoteException;

	public CreateWOStockinDataResponse_CreateWOStockinDataResponse createWOStockinData(
			CreateWOStockinDataRequest_CreateWOStockinDataRequest parameters) throws java.rmi.RemoteException;

	public GetWOIssueDataResponse_GetWOIssueDataResponse getWOIssueData(
			GetWOIssueDataRequest_GetWOIssueDataRequest parameters) throws java.rmi.RemoteException;

	public UpdateWOIssueDataResponse_UpdateWOIssueDataResponse updateWOIssueData(
			UpdateWOIssueDataRequest_UpdateWOIssueDataRequest parameters) throws java.rmi.RemoteException;

	public CreateShippingOrderResponse_CreateShippingOrderResponse createShippingOrder(
			CreateShippingOrderRequest_CreateShippingOrderRequest parameters) throws java.rmi.RemoteException;

	public GetReasonCodeResponse_GetReasonCodeResponse getReasonCode(
			GetReasonCodeRequest_GetReasonCodeRequest parameters) throws java.rmi.RemoteException;

	public GetLabelTypeDataResponse_GetLabelTypeDataResponse getLabelTypeData(
			GetLabelTypeDataRequest_GetLabelTypeDataRequest parameters) throws java.rmi.RemoteException;

	public GetCountingLabelDataResponse_GetCountingLabelDataResponse getCountingLabelData(
			GetCountingLabelDataRequest_GetCountingLabelDataRequest parameters) throws java.rmi.RemoteException;

	public UpdateCountingLabelDataResponse_UpdateCountingLabelDataResponse updateCountingLabelData(
			UpdateCountingLabelDataRequest_UpdateCountingLabelDataRequest parameters) throws java.rmi.RemoteException;

	public CreateMISCIssueDataResponse_CreateMISCIssueDataResponse createMISCIssueData(
			CreateMISCIssueDataRequest_CreateMISCIssueDataRequest parameters) throws java.rmi.RemoteException;

	public CheckExecAuthorizationResponse_CheckExecAuthorizationResponse checkExecAuthorization(
			CheckExecAuthorizationRequest_CheckExecAuthorizationRequest parameters) throws java.rmi.RemoteException;

	public CreateStockDataResponse_CreateStockDataResponse createStockData(
			CreateStockDataRequest_CreateStockDataRequest parameters) throws java.rmi.RemoteException;

	public EboGetCustDataResponse_EboGetCustDataResponse eboGetCustData(
			EboGetCustDataRequest_EboGetCustDataRequest parameters) throws java.rmi.RemoteException;

	public EboGetProdDataResponse_EboGetProdDataResponse eboGetProdData(
			EboGetProdDataRequest_EboGetProdDataRequest parameters) throws java.rmi.RemoteException;

	public EboGetOrderDataResponse_EboGetOrderDataResponse eboGetOrderData(
			EboGetOrderDataRequest_EboGetOrderDataRequest parameters) throws java.rmi.RemoteException;

	public RunCommandResponse_RunCommandResponse runCommand(RunCommandRequest_RunCommandRequest parameters)
			throws java.rmi.RemoteException;

	public CheckApsExecutionResponse_CheckApsExecutionResponse checkApsExecution(
			CheckApsExecutionRequest_CheckApsExecutionRequest parameters) throws java.rmi.RemoteException;

	public GetOrganizationListResponse_GetOrganizationListResponse getOrganizationList(
			GetOrganizationListRequest_GetOrganizationListRequest parameters) throws java.rmi.RemoteException;

	public GetUserTokenResponse_GetUserTokenResponse getUserToken(GetUserTokenRequest_GetUserTokenRequest parameters)
			throws java.rmi.RemoteException;

	public CheckUserAuthResponse_CheckUserAuthResponse checkUserAuth(
			CheckUserAuthRequest_CheckUserAuthRequest parameters) throws java.rmi.RemoteException;

	public GetMenuDataResponse_GetMenuDataResponse getMenuData(GetMenuDataRequest_GetMenuDataRequest parameters)
			throws java.rmi.RemoteException;

	public CreateVendorDataResponse_CreateVendorDataResponse createVendorData(
			CreateVendorDataRequest_CreateVendorDataRequest parameters) throws java.rmi.RemoteException;

	public CreateBOMMasterDataResponse_CreateBOMMasterDataResponse createBOMMasterData(
			CreateBOMMasterDataRequest_CreateBOMMasterDataRequest parameters) throws java.rmi.RemoteException;

	public CreateBOMDetailDataResponse_CreateBOMDetailDataResponse createBOMDetailData(
			CreateBOMDetailDataRequest_CreateBOMDetailDataRequest parameters) throws java.rmi.RemoteException;

	public CreateVoucherDataResponse_CreateVoucherDataResponse createVoucherData(
			CreateVoucherDataRequest_CreateVoucherDataRequest parameters) throws java.rmi.RemoteException;

	public GetAccountDataResponse_GetAccountDataResponse getAccountData(
			GetAccountDataRequest_GetAccountDataRequest parameters) throws java.rmi.RemoteException;

	public CreateCustomerDataResponse_CreateCustomerDataResponse createCustomerData(
			CreateCustomerDataRequest_CreateCustomerDataRequest parameters) throws java.rmi.RemoteException;

	public CreateItemMasterDataResponse_CreateItemMasterDataResponse createItemMasterData(
			CreateItemMasterDataRequest_CreateItemMasterDataRequest parameters) throws java.rmi.RemoteException;

	public CreateEmployeeDataResponse_CreateEmployeeDataResponse createEmployeeData(
			CreateEmployeeDataRequest_CreateEmployeeDataRequest parameters) throws java.rmi.RemoteException;

	public CreateAddressDataResponse_CreateAddressDataResponse createAddressData(
			CreateAddressDataRequest_CreateAddressDataRequest parameters) throws java.rmi.RemoteException;

	public TIPTOPGateWayResponse_TIPTOPGateWayResponse TIPTOPGateWay(
			TIPTOPGateWayRequest_TIPTOPGateWayRequest parameters) throws java.rmi.RemoteException;

	public CreateBillingAPResponse_CreateBillingAPResponse createBillingAP(
			CreateBillingAPRequest_CreateBillingAPRequest parameters) throws java.rmi.RemoteException;

	public CreateCustomerOtheraddressDataResponse_CreateCustomerOtheraddressDataResponse createCustomerOtheraddressData(
			CreateCustomerOtheraddressDataRequest_CreateCustomerOtheraddressDataRequest parameters)
			throws java.rmi.RemoteException;

	public CreatePotentialCustomerDataResponse_CreatePotentialCustomerDataResponse createPotentialCustomerData(
			CreatePotentialCustomerDataRequest_CreatePotentialCustomerDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetCustomerContactDataResponse_GetCustomerContactDataResponse getCustomerContactData(
			GetCustomerContactDataRequest_GetCustomerContactDataRequest parameters) throws java.rmi.RemoteException;

	public GetCustomerOtheraddressDataResponse_GetCustomerOtheraddressDataResponse getCustomerOtheraddressData(
			GetCustomerOtheraddressDataRequest_GetCustomerOtheraddressDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetItemStockListResponse_GetItemStockListResponse getItemStockList(
			GetItemStockListRequest_GetItemStockListRequest parameters) throws java.rmi.RemoteException;

	public GetMFGSettingSmaDataResponse_GetMFGSettingSmaDataResponse getMFGSettingSmaData(
			GetMFGSettingSmaDataRequest_GetMFGSettingSmaDataRequest parameters) throws java.rmi.RemoteException;

	public GetPackingMethodDataResponse_GetPackingMethodDataResponse getPackingMethodData(
			GetPackingMethodDataRequest_GetPackingMethodDataRequest parameters) throws java.rmi.RemoteException;

	public GetPotentialCustomerDataResponse_GetPotentialCustomerDataResponse getPotentialCustomerData(
			GetPotentialCustomerDataRequest_GetPotentialCustomerDataRequest parameters) throws java.rmi.RemoteException;

	public GetTableAmendmentDataResponse_GetTableAmendmentDataResponse getTableAmendmentData(
			GetTableAmendmentDataRequest_GetTableAmendmentDataRequest parameters) throws java.rmi.RemoteException;

	public GetTaxTypeDataResponse_GetTaxTypeDataResponse getTaxTypeData(
			GetTaxTypeDataRequest_GetTaxTypeDataRequest parameters) throws java.rmi.RemoteException;

	public GetUnitConversionDataResponse_GetUnitConversionDataResponse getUnitConversionData(
			GetUnitConversionDataRequest_GetUnitConversionDataRequest parameters) throws java.rmi.RemoteException;

	public GetUnitDataResponse_GetUnitDataResponse getUnitData(GetUnitDataRequest_GetUnitDataRequest parameters)
			throws java.rmi.RemoteException;

	public GetReportDataResponse_GetReportDataResponse getReportData(
			GetReportDataRequest_GetReportDataRequest parameters) throws java.rmi.RemoteException;

	public CRMGetCustomerDataResponse_CRMGetCustomerDataResponse CRMGetCustomerData(
			CRMGetCustomerDataRequest_CRMGetCustomerDataRequest parameters) throws java.rmi.RemoteException;

	public CreateCustomerContactDataResponse_CreateCustomerContactDataResponse createCustomerContactData(
			CreateCustomerContactDataRequest_CreateCustomerContactDataRequest parameters)
			throws java.rmi.RemoteException;

	public CreateDepartmentDataResponse_CreateDepartmentDataResponse createDepartmentData(
			CreateDepartmentDataRequest_CreateDepartmentDataRequest parameters) throws java.rmi.RemoteException;

	public GetAccountTypeDataResponse_GetAccountTypeDataResponse getAccountTypeData(
			GetAccountTypeDataRequest_GetAccountTypeDataRequest parameters) throws java.rmi.RemoteException;

	public GetTransactionCategoryResponse_GetTransactionCategoryResponse getTransactionCategory(
			GetTransactionCategoryRequest_GetTransactionCategoryRequest parameters) throws java.rmi.RemoteException;

	public GetVoucherDocumentDataResponse_GetVoucherDocumentDataResponse getVoucherDocumentData(
			GetVoucherDocumentDataRequest_GetVoucherDocumentDataRequest parameters) throws java.rmi.RemoteException;

	public RollbackVoucherDataResponse_RollbackVoucherDataResponse rollbackVoucherData(
			RollbackVoucherDataRequest_RollbackVoucherDataRequest parameters) throws java.rmi.RemoteException;

	public GetCardDetailDataResponse_GetCardDetailDataResponse getCardDetailData(
			GetCardDetailDataRequest_GetCardDetailDataRequest parameters) throws java.rmi.RemoteException;

	public GetOnlineUserResponse_GetOnlineUserResponse getOnlineUser(
			GetOnlineUserRequest_GetOnlineUserRequest parameters) throws java.rmi.RemoteException;

	public GetProdInfoResponse_GetProdInfoResponse getProdInfo(GetProdInfoRequest_GetProdInfoRequest parameters)
			throws java.rmi.RemoteException;

	public GetMemberDataResponse_GetMemberDataResponse getMemberData(
			GetMemberDataRequest_GetMemberDataRequest parameters) throws java.rmi.RemoteException;

	public GetMachineDataResponse_GetMachineDataResponse getMachineData(
			GetMachineDataRequest_GetMachineDataRequest parameters) throws java.rmi.RemoteException;

	public GetProdRoutingDataResponse_GetProdRoutingDataResponse getProdRoutingData(
			GetProdRoutingDataRequest_GetProdRoutingDataRequest parameters) throws java.rmi.RemoteException;

	public GetWorkstationDataResponse_GetWorkstationDataResponse getWorkstationData(
			GetWorkstationDataRequest_GetWorkstationDataRequest parameters) throws java.rmi.RemoteException;

	public CreateRepSubPBOMDataResponse_CreateRepSubPBOMDataResponse createRepSubPBOMData(
			CreateRepSubPBOMDataRequest_CreateRepSubPBOMDataRequest parameters) throws java.rmi.RemoteException;

	public GetBrandDataResponse_GetBrandDataResponse getBrandData(GetBrandDataRequest_GetBrandDataRequest parameters)
			throws java.rmi.RemoteException;

	public CreateItemApprovalDataResponse_CreateItemApprovalDataResponse createItemApprovalData(
			CreateItemApprovalDataRequest_CreateItemApprovalDataRequest parameters) throws java.rmi.RemoteException;

	public GetItemOtherGroupDataResponse_GetItemOtherGroupDataResponse getItemOtherGroupData(
			GetItemOtherGroupDataRequest_GetItemOtherGroupDataRequest parameters) throws java.rmi.RemoteException;

	public CreateSupplierItemDataResponse_CreateSupplierItemDataResponse createSupplierItemData(
			CreateSupplierItemDataRequest_CreateSupplierItemDataRequest parameters) throws java.rmi.RemoteException;

	public CreateWOWorkReportDataResponse_CreateWOWorkReportDataResponse createWOWorkReportData(
			CreateWOWorkReportDataRequest_CreateWOWorkReportDataRequest parameters) throws java.rmi.RemoteException;

	public CreateBOMDataResponse_CreateBOMDataResponse createBOMData(
			CreateBOMDataRequest_CreateBOMDataRequest parameters) throws java.rmi.RemoteException;

	public CreateShippingOrdersWithoutOrdersResponse_CreateShippingOrdersWithoutOrdersResponse createShippingOrdersWithoutOrders(
			CreateShippingOrdersWithoutOrdersRequest_CreateShippingOrdersWithoutOrdersRequest parameters)
			throws java.rmi.RemoteException;

	public GetItemGroupDataResponse_GetItemGroupDataResponse getItemGroupData(
			GetItemGroupDataRequest_GetItemGroupDataRequest parameters) throws java.rmi.RemoteException;

	public GetProdStateResponse_GetProdStateResponse getProdState(GetProdStateRequest_GetProdStateRequest parameters)
			throws java.rmi.RemoteException;

	public GetPaymentTermsDataResponse_GetPaymentTermsDataResponse getPaymentTermsData(
			GetPaymentTermsDataRequest_GetPaymentTermsDataRequest parameters) throws java.rmi.RemoteException;

	public GetSSOKeyResponse_GetSSOKeyResponse getSSOKey(GetSSOKeyRequest_GetSSOKeyRequest parameters)
			throws java.rmi.RemoteException;

	public CreateECNDataResponse_CreateECNDataResponse createECNData(
			CreateECNDataRequest_CreateECNDataRequest parameters) throws java.rmi.RemoteException;

	public CreatePLMBOMDataResponse_CreatePLMBOMDataResponse createPLMBOMData(
			CreatePLMBOMDataRequest_CreatePLMBOMDataRequest parameters) throws java.rmi.RemoteException;

	public GetUserDefOrgResponse_GetUserDefOrgResponse getUserDefOrg(
			GetUserDefOrgRequest_GetUserDefOrgRequest parameters) throws java.rmi.RemoteException;

	public GetMemberCardDataResponse_GetMemberCardDataResponse getMemberCardData(
			GetMemberCardDataRequest_GetMemberCardDataRequest parameters) throws java.rmi.RemoteException;

	public UpdatePaymentInfoResponse_UpdatePaymentInfoResponse updatePaymentInfo(
			UpdatePaymentInfoRequest_UpdatePaymentInfoRequest parameters) throws java.rmi.RemoteException;

	public GetShappingDataResponse_GetShappingDataResponse getShappingData(
			GetShappingDataRequest_GetShappingDataRequest parameters) throws java.rmi.RemoteException;

	public GetCustListResponse_GetCustListResponse getCustList(GetCustListRequest_GetCustListRequest parameters)
			throws java.rmi.RemoteException;

	public GetQuotationDataResponse_GetQuotationDataResponse getQuotationData(
			GetQuotationDataRequest_GetQuotationDataRequest parameters) throws java.rmi.RemoteException;

	public GetSODataResponse_GetSODataResponse getSOData(GetSODataRequest_GetSODataRequest parameters)
			throws java.rmi.RemoteException;

	public GetCouponDataResponse_GetCouponDataResponse getCouponData(
			GetCouponDataRequest_GetCouponDataRequest parameters) throws java.rmi.RemoteException;

	public GetItemListResponse_GetItemListResponse getItemList(GetItemListRequest_GetItemListRequest parameters)
			throws java.rmi.RemoteException;

	public UpdateMemberPointResponse_UpdateMemberPointResponse updateMemberPoint(
			UpdateMemberPointRequest_UpdateMemberPointRequest parameters) throws java.rmi.RemoteException;

	public java.lang.String invokeSrv(java.lang.String request) throws java.rmi.RemoteException;

	public java.lang.String callbackSrv(java.lang.String request) throws java.rmi.RemoteException;

	public java.lang.String syncProd(java.lang.String request) throws java.rmi.RemoteException;

	public java.lang.String invokeMdm(java.lang.String request) throws java.rmi.RemoteException;
}
