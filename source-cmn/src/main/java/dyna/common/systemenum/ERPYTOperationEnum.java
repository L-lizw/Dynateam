package dyna.common.systemenum;

public enum ERPYTOperationEnum
{
	GetOrganizationList("getOrgans", "GetOrganizationListRequest_GetOrganizationListRequest", "GetOrganizationListResponse_GetOrganizationListResponse", "getOrganizationList", "",
			"r"), // 读营运中心

	GetMFGDocument("getMFG", "GetMFGDocumentRequest_GetMFGDocumentRequest", "GetMFGDocumentResponse_GetMFGDocumentResponse", "getMFGDocument", "", "r"), // 读单据性质

	GetItemGroupData("getItemGroup", "GetItemGroupDataRequest_GetItemGroupDataRequest", "GetItemGroupDataResponse_GetItemGroupDataResponse", "getItemGroupData", "", "r"), // 分群码

	GetSupplierData("getSupplier", "GetSupplierDataRequest_GetSupplierDataRequest", "GetSupplierDataResponse_GetSupplierDataResponse", "getSupplierData", "", "r"), // 取得ERP供應商資料服務

	GetBrandData("getBrand", "GetBrandDataRequest_GetBrandDataRequest", "GetBrandDataResponse_GetBrandDataResponse", "getBrandData", "", "r"), // 取得ERP廠牌資料服務

	GetCurrencyList("getCurrency", "GetCurrencyListRequest_GetCurrencyListRequest", "GetCurrencyListResponse_GetCurrencyListResponse", "getCurrencyList", "", "r"), // 讀取幣別代碼

	GetTableAmendmentData("getTable", "GetTableAmendmentDataRequest_GetTableAmendmentDataRequest", "GetTableAmendmentDataResponse_GetTableAmendmentDataResponse",
			"getTableAmendmentData", "", "r"), // 讀取ERP檔案架構資料

	GetEmployeeList("getEmployee", "GetEmployeeListRequest_GetEmployeeListRequest", "GetEmployeeListResponse_GetEmployeeListResponse", "getEmployeeList", "", "r"), // 讀取員工代號

	GetUnitData("getUnit", "GetUnitDataRequest_GetUnitDataRequest", "GetUnitDataResponse_GetUnitDataResponse", "getUnitData", "", "r"), // 讀取單位資料

	GetPLMTempTableDataStatus("getStatus", "GetPLMTempTableDataStatusRequest_GetPLMTempTableDataStatusRequest",
			"GetPLMTempTableDataStatusResponse_GetPLMTempTableDataStatusResponse", "getPLMTempTableDataStatus", "GetPLMTempTableDataStatus", "r"), // 讀取ERPTempTable資料處理狀態服務

	CreateItemMasterData("createItem", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateItemMasterData", "w"), // 建立正式料件主檔資料

	CreatePLMBOM("createBOM", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreatePLMBOMData", "w"), // 建立PLMBOM

	CreatePLMConfigBOM("createConfigBOM", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreatePLMBOMData", "w"), // 建立PLMBOM

	CreateSupplierItemData("createSupplier", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateSupplierItemData", "w"), // 建立料件/供應商資料

	DeletePLMTempTableData("deleteTemp", "DeletePLMTempTableDataRequest_DeletePLMTempTableDataRequest", "DeletePLMTempTableDataResponse_DeletePLMTempTableDataResponse",
			"deletePLMTempTableData", "DeletePLMTempTableData", "w"), // 刪除ERP

	CreateLocalSubstitute("createLocalS", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateRepSubPBOMData", "w"), // 局部替代

	CreateLocalReplace("createLocalR", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateRepSubPBOMData", "w"), // 局部取代

	CreateGlobalSubtstitute("createGlobalS", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateRepSubPBOMData", "w"), // 全局替代

	CreateGlobalReplace("createGlobalR", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateRepSubPBOMData", "w"), // 全局取代

	CreateItemMA("createMA", "CreatePLMTempTableDataRequest_CreatePLMTempTableDataRequest", "CreatePLMTempTableDataResponse_CreatePLMTempTableDataResponse",
			"createPLMTempTableData", "CreateItemApprovalData", "w"), // 料件承认

	// 读取价格
	GetPrice("getPrice", "GetItemDataRequest_GetItemDataRequest", "GetItemDataResponse_GetItemDataResponse", "getItemData", "GetItemData", "r"),

	// 读取成本
	GetCost("getCost", "GetItemDataRequest_GetItemDataRequest", "GetItemDataResponse_GetItemDataResponse", "getItemData", "GetItemData", "r"),

	// 读取数量
	GetQuantity("getQuantity", "GetItemDataRequest_GetItemDataRequest", "GetItemDataResponse_GetItemDataResponse", "getItemData", "GetItemData", "r");

	private final String	id;
	private final String	request;
	private final String	response;
	private final String	method;
	private final String	ws;			// YT中真正调用的服务名
	private String			category;	// category字段从xml中读取赋值
	private String			name;		// name字段从xml中读取赋值
	private final String	type;

	private ERPYTOperationEnum(String id, String request, String response, String method, String ws, String type)
	{
		this.id = id;
		this.request = request;
		this.response = response;
		this.method = method;
		this.ws = ws;
		this.type = type;
		this.category = null;
		this.name = null;
	}

	public String getId()
	{
		return id;
	}

	public String getRequest()
	{
		return request;
	}

	public String getResponse()
	{
		return response;
	}

	public String getMethod()
	{
		return method;
	}

	public String getWs()
	{
		return ws;
	}

	public String getCategory()
	{
		if (this.category == null)
		{
			throw new IllegalArgumentException("no category value for " + this.getCategory() + "." + this.name() + ", you should invoke 'setCategory' to set a value to category");
		}
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}

	public static ERPYTOperationEnum getById(String id)
	{
		ERPYTOperationEnum[] all = ERPYTOperationEnum.values();
		for (int i = 0; i < all.length; i++)
		{
			if (all[i].id.equals(id))
			{
				return all[i];
			}
		}
		throw new IllegalArgumentException("No enum const class in " + ERPYTOperationEnum.class.getName() + " with id: " + id);
	}
}
