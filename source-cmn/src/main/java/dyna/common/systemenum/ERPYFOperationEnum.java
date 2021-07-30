package dyna.common.systemenum;

import dyna.common.util.StringUtils;

public enum ERPYFOperationEnum {

	CreateItem("createItem", "ItemAndBom", "w"),
	CreateBOM("createBOM", "ItemAndBom", "w"),
	CreateConfigBOM("createConfigBOM", "ItemAndBom", "w"),
	CreateECN("createECN", "ItemAndBom", "w"),
	CreateProcess("createProcess", "ItemAndBom", "w"),//工艺
	CreateRouting("createRouting", "ItemAndBom", "w"),//工艺路线
	CreateLocalR("createLocalR", "ItemAndBom", "w"),
	CreateLocalS("createLocalS", "ItemAndBom", "w"),
	CreateGlobalR("createGlobalR", "ItemAndBom", "w"),
	CreateGlobalS("createGlobalS", "ItemAndBom", "w"),
	CheckConnection("checkConnection", "CheckConnection", "r"),
	;
	
	private String id;
	private String ws;
	private String category;
	private String name;
	private String type ;//w为写类型，r为读类型
	
	private ERPYFOperationEnum(String id, String ws, String type){
		this.id = id;
		this.ws = ws;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}
	
	public String getWs()
	{
		return ws;
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

	public static ERPYFOperationEnum getById(String id) {
		for(ERPYFOperationEnum e : ERPYFOperationEnum.values()){
			if(e.getId().equals(id)){
				return e;
			}
		}
		throw new IllegalArgumentException("no enum const in "+ ERPYFOperationEnum.class.getCanonicalName()+" with id: "+id);
	}
}
