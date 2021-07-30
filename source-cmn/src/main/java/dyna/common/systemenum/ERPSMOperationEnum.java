package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * @author lufeia
 * @date 2015-4-24 下午05:31:48
 * @return
 */
public enum ERPSMOperationEnum
{
	PLMTOBOMI01("1", "createLocalR", "PLMTOBOMI01", "w"), // 写局部取代
	PLMTOBOMI02("1", "createLocalS", "PLMTOBOMI01", "w"), // 写局部替代
	PLMTOBOMI03("1", "createGlobalR", "PLMTOBOMI01", "w"), // 写全局取代
	PLMTOBOMI04("1", "createGlobalS", "PLMTOBOMI01", "w"), // 写全局替代

	PLMTOBOMI09("8", "createMA", "PLMTOBOMI09", "w"), // 寫入料件承認
	PLMGETDSCMB("10", "", "PLMGETDSCMB", "w"), // 取多公司資料：将此作为连接测试项
	PLMTODGBOM2("13", "createBOM", "PLMTODGBOM", "w"), // 寫入BOM
	PLMTODGBOM3("13", "createConfigBOM", "PLMTODGBOM", "w"), // 寫入BOM
	PLMToDGBOM("13", "createItem", "PLMToDGBOM", "w"), // 写入Item

	GETPRICE("98", "getPrice", "PLMTOPRICECOST", "r"), // 取价格
	GETCOST("98", "getCost", "PLMTOPRICECOST", "r"), // 取成本
	GETQUANTITY("6", "getQuantity", "PLMTOQTY", "r"), // 取数量

	ERPIIQUERYSQLXML("99", "", "ERPIIQUERYSQLXML", "w");// PLM取資料

	private String	No;
	private String	id;
	private String	ws;
	private String	category;
	private String	name;
	private String	type;		// 操作类型，w为向ERP写数据，r为从ERP读取数据

	private ERPSMOperationEnum(String No, String id, String ws, String type)
	{
		this.No = No;
		this.id = id;
		this.ws = ws;
		this.type = type;
	}

	public String getNo()
	{
		return No;
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
			throw new IllegalArgumentException("no category value for " + this.name() + ", you should invoke 'setCategory' to set a value to category");
		}
		return category;
	}

	public void setCategory(String category)
	{
		if (StringUtils.isNullString(category))
		{
			throw new IllegalArgumentException("category can't be null");
		}
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

	public String getType()
	{
		return type;
	}

	public static ERPSMOperationEnum getById(String id)
	{
		for (ERPSMOperationEnum e : ERPSMOperationEnum.values())
		{
			if (id.equals(e.getId()))
			{
				return e;
			}
		}
		throw new IllegalArgumentException("no enum const class in " + ERPSMOperationEnum.class.getCanonicalName() + " with id: " + id);
	}
}