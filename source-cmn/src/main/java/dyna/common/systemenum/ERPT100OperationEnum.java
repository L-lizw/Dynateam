package dyna.common.systemenum;

public enum ERPT100OperationEnum
{
	EntDataGet("EntDataGet", "EntDataGet", "r"), // 取集团代码：将此作为连接测试项

	PLMBOMDataCreate("createBOM", "PLMBOMDataCreate", "w"), // 写入BOM
	
	PLMConfigBOMDataCreate("createConfigBOM", "PLMBOMDataCreate", "w"), // 写入配置BOM

	ItemMasterDataCreate("createItem", "ItemMasterDataCreate", "w"), // 写入Item

	ItemApprovalDataCreate("createMA", "ItemApprovalDataCreate", "w"), // 写入承认书

	CreateLocalSubstitute("createLocalS", "RepSubPBOMDataCreate", "w"), // 局部替代

	CreateLocalReplace("createLocalR", "RepSubPBOMDataCreate", "w"), // 局部取代

	CreateGlobalSubtstitute("createGlobalS", "RepSubPBOMDataCreate", "w"), // 全局替代

	GetSupplyDemandData("getQuantity", "ItemSupplyDemandDataGet", "r"), // 取T100价格成本数量等数据

	GetJobStatus("GetJobStatus", "PLMDataRunStatusGet", "r")// 轮询队列处理结果
	;

	private final String	id;
	private final String	ws;		// T100中真正调用的服务名
	private String			category;	// category字段从xml中读取赋值
	private String			name;		// name字段从xml中读取赋值
	private final String	type;

	private ERPT100OperationEnum(String id, String ws, String type)
	{
		this.id = id;
		this.ws = ws;
		this.type = type;
		this.category = null;
		this.name = null;
	}

	public String getId()
	{
		return id;
	}

	public String getWs()
	{
		return ws;
	}

	public String getCategory()
	{
		if (this.category == null)
		{
			throw new IllegalArgumentException("no category value for " + this.getId() + "." + this.name() + ", you should invoke 'setCategory' to set a value to category");
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

	public static ERPT100OperationEnum getById(String id)
	{
		ERPT100OperationEnum[] all = ERPT100OperationEnum.values();
		for (int i = 0; i < all.length; i++)
		{
			if (all[i].id.equals(id))
			{
				return all[i];
			}
		}
		throw new IllegalArgumentException("No enum const class in " + ERPT100OperationEnum.class.getName() + " with id: " + id);
	}

}
