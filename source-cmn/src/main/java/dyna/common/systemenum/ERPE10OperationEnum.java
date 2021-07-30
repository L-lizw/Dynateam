/**
 * chega
 * 2013-1-6下午4:03:47
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

public enum ERPE10OperationEnum
{
	CreateItem("createItem", "w", "GROUP_ITEM,IImportDataService,ImportData"),
	CreateBOM("createBOM", "w" , "BOM,IImportDataService,ImportData"),
	CreateConfigBOM("createConfigBOM", "w" , "BOM,IImportDataService,ImportData"),
	CreateECN("createECN", "w", "ECN,IImportDataService,ImportData"),
	CreateProcess("createProcess", "w", ""),//工艺
	CreateRouting("createRouting", "w", ""),//工艺路线
	CreateLocalR("createLocalR", "w", "REPLACE_ITEM,IImportDataService,ImportData"),
	CreateLocalS("createLocalS", "w", "REPLACE_ITEM,IImportDataService,ImportData"),
	CreateGlobalR("createGlobalR", "w", "REPLACE_ITEM,IImportDataService,ImportData"),
	CreateGlobalS("createGlobalS", "w", "REPLACE_ITEM,IImportDataService,ImportData"),
	
	GetItemTableColumn("getItemTableColumn", "r", "GROUP_ITEM,IImportDataService,GetSourceDefine"),
	GetBOMTableColumn("getBOMTableColumn", "r", "BOM,IImportDataService,GetSourceDefine"),
	GetRSTableColumn("getRSTableColumn", "r", "REPLACE_ITEM,IImportDataService,GetSourceDefine"),
	
	getPrice("getPrice","r","GROUP_ITEM,IQueryQtyAndCostService,QueryQtyAndCost"),
	getCost("getCost","r","GROUP_ITEM,IQueryQtyAndCostService,QueryQtyAndCost"),
	getQuantity("getQuantity","r","GROUP_ITEM,IQueryQtyAndCostService,QueryQtyAndCost"),
	
	createCodeitem("createCodeitem","w","FEATURE,IImportDataService,ImportData"),
	createFieldG("createFieldG","w","FEATURE_GROUP,IImportDataService,ImportData");
	
	private String id;
	private String category;//应用目录
	private String name;//显示名称
	private String type ;//w为写类型，r为读类型
	private String ws;//E10提供的服务接口名
	
	private ERPE10OperationEnum(String id, String type, String ws){
		this.id = id;
		this.type = type;
		this.ws = ws;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getWs()
	{
		return this.ws;
	}

	public String getCategory() {
		if(StringUtils.isNullString(this.category)){
			throw new IllegalArgumentException("no category for "+this.getClass()+"."+this.name());
		}
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public static ERPE10OperationEnum getById(String id) {
		for(ERPE10OperationEnum e : ERPE10OperationEnum.values()){
			if(e.getId().equals(id)){
				return e;
			}
		}
		throw new IllegalArgumentException("no enum const in "+ ERPE10OperationEnum.class.getCanonicalName()+" with id: "+id);
	}
}
