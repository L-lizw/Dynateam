/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ModelInterfaceEnum 模型接口枚举
 * Wanglei 2010-11-16
 */
package dyna.common.systemenum;

/**
 * 模型接口枚举
 *
 * @author Wanglei
 *
 */
public enum ModelInterfaceEnum
{
	IFoundation("ID_SYS_INTERFACE_IFOUNDATION", "IFoundation"),
	/**
	 * 不允许通用搜索此类
	 */
	INonQueryable("ID_SYS_INTERFACE_INONQUERYABLE", "Non Query able "),
	/**
	 * 决定该类的对象是否可以创建文件关联
	 */
	IStorage("ID_SYS_INTERFACE_ISTORAGE", "For DSS"), //
	/**
	 * 决定该类的对象是否为结构
	 */
	IStructureObject("ID_SYS_INTERFACE_ISTRUCTUREOBJECT", "Structure Objects", INonQueryable), //
	/**
	 * 决定该类的对象是否为CAD结构
	 */
	ICADStructureObject("ID_SYS_INTERFACE_ISCADTRUCTUREOBJECT", "CAD Structure Objects", IStructureObject, INonQueryable), //
	/**
	 * 决定该类的对象是否为部分对象
	 */
	IPartialObject("ID_SYS_INTERFACE_IPARTIALOBJECT", "Partial Objects", IFoundation, INonQueryable), //
	/**
	 * 决定该类的对象是否为明细对象
	 */
	IDetailObject("ID_SYS_INTERFACE_IDETAILOBJECT", "Detail Objects", IFoundation, INonQueryable), //
	/**
	 * 决定该类的对象是否为BOM结构
	 */
	IBOMStructure("ID_SYS_INTERFACE_IBOMSTRUCTURE", "BOM Structure", IStructureObject), //
	/**
	 * 决定该类的对象是否为视图
	 */
	IViewObject("ID_SYS_INTERFACE_IVIEWOBJECT", "View Objects", IFoundation, INonQueryable), //
	/**
	 * 决定该类的对象是否为BOM视图
	 */
	IBOMView("ID_SYS_INTERFACE_IBOMVIEW", "BOM View", IFoundation, INonQueryable), //
	/**
	 * 决定该类的对象是否能进行版本控制
	 */
	IVersionable("ID_SYS_INTERFACE_IVERSIONABLE", "Versionable Objects"), //
	/**
	 * 决定该类的对象是否为物料
	 */
	IItem("ID_SYS_INTERFACE_IITEM", "Item Objects", IVersionable, IFoundation), //
	/**
	 * 决定该类的对象是否为产品
	 */
	IProduct("ID_SYS_INTERFACE_IPRODUCT", "Products Objects", IItem), //

	/**
	 * 决定该类的对象是否为cad
	 */
	ICAD("ID_SYS_INTERFACE_ICAD", "CAD Model", IFoundation, IVersionable, IStorage), //

	/**
	 * 决定该类的对象是否为E-CAD
	 */
	IECAD("ID_SYS_INTERFACE_IECAD", "E-CAD Model", ICAD), //

	/**
	 * 决定该类的对象是否为2D的cad
	 */
	ICAD2D("ID_SYS_INTERFACE_ICAD2D", "2D CAD Model", ICAD), //
	/**
	 * 决定该类的对象是否为3D的cad
	 */
	ICAD3D("ID_SYS_INTERFACE_ICAD3D", "3D CAD Model", ICAD), //
	/**
	 * 决定该类的对象是否为用户
	 */
	IUser("ID_SYS_INTERFACE_IUSER", "User"), //
	/**
	 * 决定该类的对象是否为组
	 */
	IGroup("ID_SYS_INTERFACE_IGROUP", "Group"), //
	/**
	 * 决定该类的对象是否为文档
	 */
	IDocument("ID_SYS_INTERFACE_IDOCUMENT", "Document Object", IVersionable, IFoundation, IStorage), //
	/**
	 * 决定该类的对象是否为文档模板
	 */
	IDocumentTemplate("ID_SYS_INTERFACE_IDOCUMENTTEMPLATE", "Document Template Object", IDocument), //

	/**
	 * 决定该类的对象是否为CAD模板
	 */
	ICADTemplate("ID_SYS_INTERFACE_ICADTEMPLATE", "CAD Template", ICAD), //
	/**
	 * 决定该类的对象是否为2D CAD模板
	 */
	ICADTemplate2D("ID_SYS_INTERFACE_ICADTEMPLATE2D", "2D CAD Template", ICADTemplate), //
	/**
	 * 决定该类的对象是否为3D CAD模板
	 */
	ICADTemplate3D("ID_SYS_INTERFACE_ICADTEMPLATE3D", "3D CAD Template", ICADTemplate), //

	/**
	 * 决定该类的对象是否可以DICTIONARY 选择
	 */
	IDictionary("ID_SYS_INTERFACE_IDICTIONARY", "dictionary object"),

	/**
	 * 决定该类的对象是否为电子CAD元件料
	 */
	IComponentMaterial("ID_SYS_INTERFACE_ICOMPONENTMATERIAL", "Component Material", IItem),

	/**
	 * 决定该类的对象是否为PDB板
	 */
	IPCBBoard("ID_SYS_INTERFACE_IPCBBOARD", "PCB Board", IItem),

	/**
	 * 决定该类的对象是否为板材料
	 */
	IBoardMaterial("ID_SYS_INTERFACE_IBoardMaterial", "Board Material", IItem),

	/**
	 * 决定该类的对象是否为项目管理相关类对象
	 */
	IPM("ID_SYS_INTERFACE_IPM", "Project Manager", IFoundation),

	/**
	 * 决定该类的对象是否为项目管理项目对象
	 */
	IPMProject("ID_SYS_INTERFACE_IPMPROJECT", "Project Manager Project", IPM, INonQueryable),

	/**
	 * 决定该类的对象是否为项目任务对象
	 */
	IPMTask("ID_SYS_INTERFACE_IPMTask", "Project Manager Task", IPM, INonQueryable),

	/**
	 * 决定该类的对象是否为工作项
	 */
	IPMWorkItem("ID_SYS_INTERFACE_IPMWORKITEM", "Work Item", IPM, INonQueryable),

	/**
	 * 决定该类的对象是否为项目管理项目模板对象
	 */
	IPMProjectTemplate("ID_SYS_INTERFACE_IPMPROJECTTEMPLATE", "Project Manager Project Template", IPM, IDictionary),

	/**
	 * 决定该类的对象是否为项目管理任务模板对象
	 */
	IPMTaskTemplate("ID_SYS_INTERFACE_IPMTASKTEMPLATE", "Project Manager Task Template", IPM, INonQueryable),

	/**
	 * 决定该类的对象是否为项目管理变更申请对象
	 */
	IPMChangeRequest("ID_SYS_INTERFACE_IPMCHANGEREQUEST", "Project Manager Change Request", IPM, IStorage, INonQueryable),

	/**
	 * 决定该类的对象是否为取替代对象
	 */
	IReplaceSubstitute("ID_SYS_INTERFACE_IREPLACESUBSTITUTE", "Replace Substitute", IFoundation, IVersionable),

	/**
	 * 决定该类的对象是否为项目角色对象
	 */
	IPMRole("ID_SYS_INTERFACE_IPMROLE", "Project Manager Role", IPM),

	/**
	 * 决定该类的对象是否为项目日历对象
	 */
	IPMCalendar("ID_SYS_INTERFACE_IPMCALENDAR", "Project Manager Calendar", IPM),

	IProjectType("ID_SYS_PROJECT_TYPE", "Project Type", IDictionary), //
	IPMChangeTask("ID_SYS_TASK_CHANGE", "PMTask Change", IPM, INonQueryable), //

	IChangeItem("ID_SYS_INTERFACE_IChangeItem", "IChangeItem"), //
	IBatchForEc("ID_SYS_INTERFACE_IBatchForEc", "IBatchForEc"), //
	IIndependenceForEc("ID_SYS_INTERFACE_IIndependenceForEc", "IIndependenceForEc"), //
	INormalForEc("ID_SYS_INTERFACE_INormalForEc", "INormalForEc"), //

	IUpdatedECR("ID_SYS_INTERFACE_IUpdatedECR", "IUpdatedECR", IFoundation), //
	INormalECPContent("ID_SYS_INTERFACE_INormalECPContent", "INormalECPContent", IFoundation), //
	IUpdatedECN("ID_SYS_INTERFACE_IUpdatedECN", "IUpdatedECN", IFoundation), //

	IECPM("ID_SYS_INTERFACE_IECPM", "IECPM", IFoundation), //
	IECOM("ID_SYS_INTERFACE_IECOM", "IECOM", IFoundation), //
	IECI("ID_SYS_INTERFACE_IECI", "IECI", IFoundation), //

	/**
	 * 批量取替代关系接口
	 */
	IBatchRS("ID_SYS_INTERFACE_IBATHREPLACESUBSTITUTE", "Bath Replace Substitute Relation", IFoundation), //

	/**
	 * 批量取替代申请表单接口
	 */
	IRSApplyForm("ID_SYS_INTERFACE_IRSAPPLYFORM", "Replace Substitute Apply Form", IFoundation), //

	/**
	 * 局部取替代申请表单接口
	 */
	IRSApplyFormPartial("ID_SYS_INTERFACE_IRSAPPLYFORMPARTIAL", "Replace Substitute Apply Form For Partial", IRSApplyForm), //

	/**
	 * 全局取替代申请表单接口
	 */
	IRSApplyFormGlobal("ID_SYS_INTERFACE_IRSAPPLYFORMGLOBAL", "Replace Substitute Apply Form For Global", IRSApplyForm), //

	/**
	 * 批量取替代关联关系结构接口
	 */
	IReplaceStructure("ID_SYS_INTERFACE_IREPLACESTRUCTURE", "Batch Replace Structure", IStructureObject), //

	/**
	 * 参数配置接口
	 */
	IManufacturingRule("ID_SYS_INTERFACE_IMANUFACTURINGRULE", "Configure Rule", IFoundation), //

	/**
	 * 产品配置结构接口
	 */
	IProductConfigureStructure("ID_SYS_INTERFACE_IPRODUCTCONFIGURESTRUCTURE", "Product Configure Structure", IStructureObject),

	/**
	 * 订单合同-订单物料结构接口
	 */
	IOrderBOMStructure("ID_SYS_INTERFACE_IORDERBOMSTRUCTURE", "Order BOM Structure", IStructureObject),

	/**
	 * 根据产品配置驱动，生成的新对象需要实现的接口
	 */
	INewProductOfConfigure("ID_SYS_INTERFACE_INEWPRODUCTOFCONFIGURE", "New Product Of Configure", IFoundation),

	/**
	 * 订单内容
	 */
	IOrderContent("ID_SYS_INTERFACE_ORDER_CONTENT", "Order Content", IFoundation),

	/**
	 * 订单合同
	 */
	IOrderContract("ID_SYS_INTERFACE_ORDER_CONTRACT", "Order Contract", IFoundation),
	/**
	 * 产品配置结构接口
	 */
	IOption("ID_SYS_INTERFACE_IPRODUCTCONFIGURESTRUCTURE", "Product Configure Structure"),
	/**
	 * 产品结构关系配置接口
	 */
	IOptionStructure("ID_SYS_INTERFACE_IOptionStructure", "IOP Product Configure Structure"),

	/**
	 * 电气部件接口
	 */
	IEPart("ID_SYS_INTERFACE_IEPART", "Electrical Part"),
	/**
	 * 电气项目接口
	 */
	IEProject("ID_SYS_INTERFACE_IEPROJECT", "Electrical Project"),

	/**
	 * 图纸转换BOM接口
	 */
	IDWTransfer("ID_SYS_INTERFACE_IDWTRANSFER", "transfer draw to bom"),

	/**
	 * 工艺流程管理
	 */
	IProcessFlowManagement("ID_SYS_INTERFACE_IPROCESSFLOW_MANAGEMENT", "Process Flow Management"),

	/**
	 * 工艺流程
	 */
	IProcessFlow("ID_SYS_INTERFACE_IPROCESSFLOW", "Process Flow"),

	/**
	 * 工序
	 */
	IProcess("ID_SYS_INTERFACE_IPROCESS", "Process"),

	/**
	 * 标准工艺流程
	 */
	IStandardProcessFlow("ID_SYS_INTERFACE_ISTANDARDPROCESSFLOW", "Standard Process Flow"),

	/**
	 * 标准工序
	 */
	IStandardProcess("ID_SYS_INTERFACE_ISTANDARDPROCESS", "Standard Process"),

	/**
	 * 计算公式库
	 */
	ICalculationLibrary("ID_SYS_INTERFACE_ICALCULATIONLIBRARY", "Calculation Library"),

	IERPType("ID_SYS_INTERFACE_ERPTYPE", "Default Fields For ERP"),

	IERPTypeE10BOM("ID_SYS_INTERFACE_ERPTYPE_E10BOM", "Default Fields For e10 ERP", IERPType),
	IERPTypeE10Item("ID_SYS_INTERFACE_ERPTYPE_E10ITEM", "Default Fields For e10 ERP", IERPType),
	IERPTypeE10MA("ID_SYS_INTERFACE_ERPTYPE_E10MA", "Default Fields For e10 ERP", IERPType),
	IERPTypeE10Substitute("ID_SYS_INTERFACE_ERPTYPE_E10SUBS", "Default Fields For e10 ERP", IERPType),

	IERPTypeSmartBOM("ID_SYS_INTERFACE_ERPTYPE_SMBOM", "Default Fields For Smart ERP", IERPType),
	IERPTypeSmartItem("ID_SYS_INTERFACE_ERPTYPE_SMITEM", "Default Fields For Smart ERP", IERPType),
	IERPTypeSmartMA("ID_SYS_INTERFACE_ERPTYPE_SMMA", "Default Fields For Smart ERP", IERPType),
	IERPTypeSmartSubstitute("ID_SYS_INTERFACE_ERPTYPE_SMSUBS", "Default Fields For Smart ERP", IERPType),

	IERPTypeT100BOM("ID_SYS_INTERFACE_ERPTYPE_T100BOM", "Default Fields For T100 ERP", IERPType),
	IERPTypeT100Item("ID_SYS_INTERFACE_ERPTYPE_T100ITEM", "Default Fields For T100 ERP", IERPType),
	IERPTypeT100MA("ID_SYS_INTERFACE_ERPTYPE_T100MA", "Default Fields For T100 ERP", IERPType),
	IERPTypeT100Substitute("ID_SYS_INTERFACE_ERPTYPE_T100SUBS", "Default Fields For T100 ERP", IERPType),

	IERPTypeWFBOM("ID_SYS_INTERFACE_ERPTYPE_WFBOM", "Default Fields For WF ERP", IERPType),
	IERPTypeWFItem("ID_SYS_INTERFACE_ERPTYPE_WFITEM", "Default Fields For WF ERP", IERPType),
	IERPTypeWFMA("ID_SYS_INTERFACE_ERPTYPE_WFMA", "Default Fields For WF ERP", IERPType),
	IERPTypeWFSubstitute("ID_SYS_INTERFACE_ERPTYPE_WFSUBS", "Default Fields For WF ERP", IERPType),

	IERPTypeYFBOM("ID_SYS_INTERFACE_ERPTYPE_YFBOM", "Default Fields For YF ERP", IERPType),
	IERPTypeYFItem("ID_SYS_INTERFACE_ERPTYPE_YFITEM", "Default Fields For YF ERP", IERPType),
	IERPTypeYFMA("ID_SYS_INTERFACE_ERPTYPE_YFMA", "Default Fields For YF ERP", IERPType),
	IERPTypeYFSubstitute("ID_SYS_INTERFACE_ERPTYPE_YFSUBS", "Default Fields For YF ERP", IERPType),

	IERPTypeYTBOM("ID_SYS_INTERFACE_ERPTYPE_YTBOM", "Default Fields For YT ERP", IERPType),
	IERPTypeYTItem("ID_SYS_INTERFACE_ERPTYPE_YTITEM", "Default Fields For YT ERP", IERPType),
	IERPTypeYTMA("ID_SYS_INTERFACE_ERPTYPE_YTMA", "Default Fields For YT ERP", IERPType),
	IERPTypeYTSubstitute("ID_SYS_INTERFACE_ERPTYPE_YTSUBS", "Default Fields For YT ERP", IERPType),
	;

	/**
	 * 多语言
	 */
	private String					msrId			= null;
	/**
	 * 描述
	 */
	private String					description		= null;

	/**
	 * 父接口
	 */
	private ModelInterfaceEnum[]	superInterfaces	= null;

	private ModelInterfaceEnum(String msrId, String description, ModelInterfaceEnum... superInterfaces)
	{
		this.msrId = msrId;
		this.description = description;
		this.superInterfaces = superInterfaces;
	}

	public static ModelInterfaceEnum typeof(String interfaceName)
	{
		ModelInterfaceEnum[] interfaceNames = ModelInterfaceEnum.values();
		for (ModelInterfaceEnum interfaceName_ : interfaceNames)
		{
			if (interfaceName_.name().equalsIgnoreCase(interfaceName))
			{
				return interfaceName_;
			}
		}
		return null;
	}

	/**
	 *
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return the superInterfaces
	 */
	public ModelInterfaceEnum[] getSuperInterfaces()
	{
		return this.superInterfaces;
	}
}
