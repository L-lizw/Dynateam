package dyna.app.service.brs.erpi.dataExport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.erpi.ERPIImpl;
import dyna.app.service.brs.erpi.ERPTransferStub;
import dyna.app.service.helper.Constants;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.configparamter.ConfigParameterConstants;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.erp.ERPFieldMapping;
import dyna.common.bean.erp.ERPFieldMappingTypeEnum;
import dyna.common.bean.erp.ERPParameter;
import dyna.common.bean.erp.ERPRestriction;
import dyna.common.bean.erp.ERPRestrictionOperatorEnum;
import dyna.common.dto.DataRule;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dto.erp.ERPTransferLog;
import dyna.common.dto.erp.tmptab.ERPtempData;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ERPE10OperationEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.JobStatus;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ReplaceRangeEnum;
import dyna.common.systemenum.ReplaceTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.systemenum.UITypeEnum;
import dyna.common.util.CharUtils;
import dyna.common.util.DateFormat;
import dyna.common.util.ReplaceSubstituteConstants;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

/**
 * 该类根据配置的字段映射从PLM系统中读取数据并生成XML文档
 * <p>
 * 在生成数据的时候大同小异，每个SingleData表示一个RecordeSet,其中包含1个表头，多个表身等
 * <p>
 * 在生成XML的时候由于每个ERP系统要求的格式差别很大，因此要特别注意。
 * 
 * @author chega
 * 
 * @param <T>
 *            每种ERP集成对应的OperationEnum
 */
public abstract class IntegrateERP<T extends Enum<?>>
{
	/**
	 * Map的初始化大小
	 */
	private final int														initialCapacity				= 150;
	/**
	 * 这里的Item并不专指“物料”，PLM的实例都可以称为Item
	 */
	public static final String												ERP_ITEM					= "item";
	/**
	 * 分类数据
	 */
	public static final String												ERP_CF						= "classification";
	/**
	 * BOM结构
	 */
	public static final String												ERP_BOM						= "bom";
	/**
	 * 取替代，包含3种：局部取代 LocalR & 局部替代 LocalS & 全局替代 GlobalS
	 */
	public static final String												ERP_REPLACESUBSTITUTE		= "replacesubstitute";
	/**
	 * 供应商
	 */
	public static final String												ERP_AVL						= "avl";
	/**
	 * 料件承认
	 */
	public static final String												ERP_MA						= "ma";
	/**
	 * 组织
	 */
	public static final String												ERP_ORGANS					= "organs";
	/**
	 * 料件分群码
	 */
	public static final String												ERP_ITEM_GROUP				= "itemGroup";
	/**
	 * 厂牌
	 */
	public static final String												ERP_BRAND					= "brand";
	/**
	 * 货币
	 */
	public static final String												ERP_CURRENCY				= "currency";
	/**
	 * ERP工号
	 */
	public static final String												ERP_EMPLOYEE				= "erpEmployee";
	/**
	 * 单位
	 */
	public static final String												ERP_UNIT					= "unit";
	/**
	 * 料件供应商
	 */
	public static final String												ERP_ITEM_SUPPLIER			= "itemSupplier";
	/**
	 * 工艺
	 */
	public static final String												ERP_PROCESS					= "process";
	/**
	 * 工艺流程
	 */
	public static final String												ERP_ROUTING					= "routing";
	/**
	 * 分类群组
	 */
	public static final String												ERP_FIELDGROUP				= "fieldgroup";
	/**
	 * 分类字段
	 */
	public static final String												ERP_CODEITEM				= "codeitem";

	/**
	 * 配置文件的位置
	 */
	public String															xmlPath;

	/**
	 * 插件位置
	 */
	public static final String												RESERVED_DESIGNATORS		= "bmt06";
	/**
	 * 当前日期
	 */
	public static final String												RESERVED_DATE				= "${Date}";
	/**
	 * 队列编号
	 */
	public static final String												RESERVED_JOBID				= "${JobId}";
	/**
	 * Datakey(jobId+三位流水码)
	 * 
	 * @see #RESERVED_JOBID
	 */
	public static final String												RESERVED_DATAKEY			= "${DataKey}";
	public static final String												RESERVED_XMLPACKAGENO		= "${XMLPackageNo}";
	public static final String												RESERVED_EMPTY				= "${Empty}";
	/**
	 * 是否以前传输过(如果<b>saveTransferStatus</b>这个参数配置为false则此字段永远返回N
	 */
	public static final String												RESERVED_HASSENT			= "${HasSent}";
	/**
	 * PLM中的UserId,即job的创建者
	 */
	public static final String												RESERVED_USERID				= "${UserId}";
	/**
	 * ERP中的UserId,即配置在xml文件中的ERP用户
	 */
	public static final String												RESERVED_ERP_USERID			= "${ERPUserId}";
	/**
	 * 工厂名
	 */
	public static final String												RESERVED_FACTORY			= "${Factory}";
	/**
	 * 插件位置分隔符
	 */
	public static final String												DELIMITER_DESIGNATOR		= ",";
	protected String														defaultDateFormat			= "yyyy-MM-dd HH:mm:ss";
	/**
	 * 键值是category如 item, 值是多条记录 RecordSet1 RecordSet2...
	 */
	protected Map<String, List<RecordSet>>									dataMap;

	protected List<T>														operationList;
	/**
	 * 内层Map是表和字段集合，中层Map的key是类名，外层Map的key值是catory，如item. <br/>
	 */
	protected Map<String, Map<String, Map<String, List<ERPFieldMapping>>>>	PLMColumnMap;
	/**
	 * 外层Map的键值是category， 内层Map的键值是表的id和name对
	 */
	protected Map<String, Map<String, String>>								tableMap;
	protected ERPTransferStub<T>											stub;
	private ERPFieldComparator												fieldComparator;
	private List<ERPRestriction>											restrictionList;
	private final List<InvalidDataException>								dataErrorList				= new LinkedList<InvalidDataException>();
	private final List<String>												dataWarnList				= new LinkedList<String>();
	/**
	 * 待传输的实例（在记录到erp_transfer_log中用到） <br/>
	 * 传输料件的话存储的是料件的objectguid，传输BOM的话存储的是bomview的objectguid <br/>
	 * 目前只存储item和bom. <br/>
	 * 和dataMap一样，首先将所有要传输的实例存储起来，抛转成功后将tobeExportedGuidList中的相应实例删除。 <br/>
	 * 键值是category
	 */
	protected Map<String, LinkedList<ObjectGuid>>							tobeExportedGuidMap;
	private final Map<String, ERPParameter>									parameterMap				= new HashMap<String, ERPParameter>();
	private List<String>													categoryList;
	protected BOMView														bomView;

	/**
	 * 当前日期
	 */
	protected Date															CURRENT_DATE				= null;
	protected Document														doc;
	/**
	 * 实例数据缓存,缓存中的数据只允许读/删，不允许修改.Job执行完毕后，清空缓存。<br/>
	 * key是ObjectGuid的toString()
	 */
	private Map<String, SystemObject>										itemCache;

	/**
	 * 唯一性。
	 * 用于验证相同一笔数据是否已经传过(这个只是在当前Job范围内验证，不从DB读取历史记录)<br/>
	 * 外层map的key是category, 内层map的key是ObjectGuid的toString， value可以是任何数值，默认1. 使用Map查找更快速，复杂度O(1)。
	 */
	private Map<String, Map<String, Integer>>								uniqueCache;
	/**
	 * 重复出现的实例
	 * 
	 * @see uniqueCache
	 */
	private Map<String, Map<String, Integer>>								duplicatedCache;
	/**
	 * 记录产生的唯一实例ID
	 * key值是category
	 */
	private Map<String, List<String>>										uniqueItemIDMap;
	/**
	 * 记录产生所有重复的实例ID
	 * key值是category
	 */
	private Map<String, List<String>>										duplicatedItemIDMap;

	private ERPIImpl														stubService					= null;
	/**
	 * 内层Map是表和字段集合，外层Map的key值是catory(仅用于分类抛转的2个operation). <br/>
	 */
	protected Map<String, Map<String, List<ERPFieldMapping>>>				PLMCFColumnMap				= null;
	/**
	 * 物料分类信息的集合
	 */
	protected Map<String, Map<String, String>>								itemCFInfoMap				= new LinkedHashMap<String, Map<String, String>>();

	/**
	 * 如果end1之前有过BOM结构，但是后来被删除了，有时需要告诉ERP，也同步删除该BOM结构
	 */
	protected boolean														isPrintEmptyBOMXML			= false;

	/**
	 * ERP字段配置缓存，用于中间表
	 */
	protected ERPFieldMappingContainer										erpFieldMappingContainer	= new ERPFieldMappingContainer();

	public IntegrateERP(ERPTransferStub<T> stub, Document doc)
	{
		this.stub = stub;
		this.doc = doc;
	}

	/**
	 * 初始化各种容器和相关参数，解析xml配置文件。这个方法在每个子类的构造方法中调用
	 * 
	 * @throws Exception
	 */
	protected void init() throws Exception
	{
		this.dataMap = new HashMap<String, List<RecordSet>>(this.initialCapacity);
		this.itemCache = new HashMap<String, SystemObject>(this.initialCapacity);
		this.uniqueCache = new HashMap<String, Map<String, Integer>>(this.initialCapacity);
		this.duplicatedCache = new HashMap<String, Map<String, Integer>>();
		this.uniqueItemIDMap = new HashMap<String, List<String>>(this.initialCapacity);
		this.duplicatedItemIDMap = new HashMap<String, List<String>>();
		this.PLMColumnMap = new HashMap<String, Map<String, Map<String, List<ERPFieldMapping>>>>();
		this.tableMap = new HashMap<String, Map<String, String>>();
		this.tobeExportedGuidMap = new HashMap<String, LinkedList<ObjectGuid>>(this.initialCapacity);
		this.categoryList = this.getAllCategory();
		this.initParameter();
		this.fieldComparator = new ERPFieldComparator();
		this.restrictionList = this.getRestrictionList();
		this.PLMCFColumnMap = new HashMap<String, Map<String, List<ERPFieldMapping>>>();
		this.stubService = stub.getStubService();
		this.operationList = new ArrayList<T>();
		CURRENT_DATE = new Date();
	}

	/**
	 * 
	 * @param end1Obj
	 *            null的话则以当前系统时间生成一个完全虚拟的ObjectGuid,如果end1Obj不为null，则返回end1Obj的ObjectGuid,<br/>
	 *            这个主要用于在生成叶子结点的BOM xml时，由于BOMStructure为空，因此借用end1Object的objectGuid做为在uniqueCache中的key值。 <br/>
	 *            这样的话就会防止生成重复的叶子结点的BOM xml。
	 * @return
	 */
	protected ObjectGuid getMockObjectGuid(FoundationObject end1Obj)
	{
		if (end1Obj == null)
		{
			return new ObjectGuid(String.valueOf(System.currentTimeMillis()), null, String.valueOf(System.currentTimeMillis()), null);
		}
		else
		{
			return end1Obj.getObjectGuid();
		}
	}

	/**
	 * 判断<b>objGuid</b>是否为虚拟出来的ObjectGuid.<br/>
	 * 
	 * @param objGuid
	 * @return
	 */
	protected boolean isMockObjectGuid(ObjectGuid objGuid)
	{
		if (objGuid.getClassGuid().matches("\\d+") && objGuid.getClassName() == null && objGuid.getClassGuid().matches("\\d+") && objGuid.getCommitFolderGuid() == null)
		{
			return true;
		}
		return false;
	}

	/**
	 * 根据字符串解析出一个ObjectGuid
	 * 
	 * @param str
	 * @return
	 */
	protected ObjectGuid parseObjectGuidFromString(String str)
	{
		String[] array = str.split("\\$", -1);
		ObjectGuid g = new ObjectGuid();
		g.setClassGuid(array[0]);
		g.setClassName(array[1]);
		g.setGuid(array[2]);
		g.setMasterGuid(array[3]);
		g.setIsMaster("true".equalsIgnoreCase(array[4]));
		g.setBizObjectGuid(array[5]);
		g.setCommitFolderGuid(array[6]);
		return g;
	}

	/**
	 * 在遇到错误数据时，是否继续解析实例
	 * 
	 * @return
	 */
	protected boolean isTraverseAllInstanceIgnoringError()
	{
		return "true".equalsIgnoreCase(this.getParameter(null).getParamMap().get("traverseAllInstanceIgnoringError"));
	}

	/**
	 * 是否保存传输记录到数据库，如果返回false则${HasSent}为false
	 * 
	 * @return
	 */
	public boolean isSaveTransferStatus()
	{
		return "true".equalsIgnoreCase(this.getParameter(null).getParamMap().get("saveTransferStatus"));
	}

	/**
	 * 是否在xml文件中包含空的BOM，空BOM意味着只有BOM单头信息，而没有BOM单身信息</br>
	 * 目前WF和SM不支持抛空BOM，为防止配置错误，强制返回false
	 * 
	 * @return
	 */
	protected boolean isContinEmptyBOMXML()
	{
		return "true".equalsIgnoreCase(this.getParameter(null).getParamMap().get("containEmptyBOMXML"));
	}

	/**
	 * 这个料件下是否挂载过BOM
	 * 
	 * @param obj
	 * @return
	 * @throws ServiceRequestException
	 */
	protected boolean hasBOM(FoundationObject obj) throws ServiceRequestException
	{
		CodeItemInfo item = null;
		if (obj.get(Constants.FIELD_UHASBOM) != null)
		{
			item = this.stub.getStubService().getEMM().getCodeItem((String) obj.get(Constants.FIELD_UHASBOM));
		}
		return item != null && "D".equalsIgnoreCase(item.getCode());
	}

	/**
	 * 取得BOM单头对应的table name
	 * 
	 * @return
	 */
	protected abstract String getBOMHeaderTableName();

	/**
	 * 取出“任意”一个BOM结构类的名字，这个方法只能用在传空BOM时，由于没有BOM，因此BOMView为null。而在调用getFieldFromMap()时需要传一个类名参数，因此这里 <br/>
	 * 从现在的PLMColumnMap中找，如果找不到则从配置文件中找，找category是bom的第一个class元素的name属性值
	 * 
	 * @return
	 */
	protected String getArbitraryBOMClassName()
	{
		String BOMStructureClassName = null;
		if (this.PLMColumnMap.get(ERP_BOM) == null || this.PLMColumnMap.get(ERP_BOM).isEmpty())
		{
			Element bomElement = this.doc.getRootElement().getChild("category").getChild("bom");
			if (bomElement == null || bomElement.getChild("class") == null)
			{
				throw new IllegalArgumentException("can't find category configuration for bom");
			}
			BOMStructureClassName = bomElement.getChild("class").getAttributeValue("name");
		}
		else
		{
			BOMStructureClassName = this.PLMColumnMap.get(ERP_BOM).keySet().iterator().next();
		}
		if (BOMStructureClassName == null)
		{
			throw new IllegalArgumentException("can't find a class name for BOM configuration");
		}
		return BOMStructureClassName;
	}

	/**
	 * 是否保存中间文件
	 * 
	 * @return
	 */
	public boolean isSaveTempFile()
	{
		return "true".equalsIgnoreCase(this.getParameter(null).getParamMap().get("saveTempFile"));
	}

	/**
	 * 是否产生文件供测试人员对于对比XML完整性
	 * 传料件时产生料件ID;传BOM时产生父件的ID;传取替代时产生元件的ID;传料件承认时产生end1的ID(end1是ApproveItem模板中的父阶对象)
	 */
	public boolean isPrintData4Comparison()
	{
		return "true".equalsIgnoreCase(this.getParameter(null).getParamMap().get("printData4Comparison"));
	}

	/**
	 * 根据category得到应用于这个category的所有restriction
	 * 
	 * @param category
	 * @return
	 */
	private List<ERPRestriction> getRestrictionByCategory(String category)
	{
		List<ERPRestriction> resList = new ArrayList<ERPRestriction>();
		for (ERPRestriction res : this.restrictionList)
		{
			if (res.getCategoryList().contains(category))
			{
				resList.add(res);
			}
		}
		return resList;
	}

	/**
	 * 解析xml配置文件得到所有的restriction
	 * 
	 * @return
	 */
	private List<ERPRestriction> getRestrictionList()
	{
		List<ERPRestriction> list = Collections.emptyList();
		Element parentEle = this.doc.getRootElement().getChild("restrictions");
		if (parentEle == null)
		{
			return Collections.emptyList();
		}
		List<Element> restrictionListEle = parentEle.getChildren("restriction");
		list = new ArrayList<ERPRestriction>();
		Iterator<Element> restriction_ite = restrictionListEle.iterator();
		while (restriction_ite.hasNext())
		{
			Element ele = restriction_ite.next();
			list.add(new ERPRestriction(ele.getAttributeValue("field").trim(), ele.getAttributeValue("value").trim(), ERPRestrictionOperatorEnum.getOperatorBySign(ele
					.getAttributeValue("operator").trim()), ele.getAttributeValue("category").trim(), "true".equalsIgnoreCase(ele.getAttributeValue("skipOnFail"))));
		}
		return list;
	}

	public void getAllMergeDataWithChildren(FoundationObject foundationObject, BOMStructure bomStructure) throws Exception
	{
		this.generateAllData(foundationObject, bomStructure, false, null, 0, null);
		this.afterProcessDataList();
	}

	public void beforeProcessDataList4Merge()
	{
		this.beforeProcessDataList();
	}

	/**
	 * @see #generateAllData(FoundationObject, BOMStructure)
	 */
	public void getAllDataWithChildren(FoundationObject foundationObject, BOMStructure bomStructure) throws Exception
	{
		this.beforeProcessDataList();
		this.generateAllData(foundationObject, bomStructure, false, null, 0, null);
		this.afterProcessDataList();
	}

	/**
	 * 取得所有的数据<br/>
	 * BOM展开时，遵循深度优先 <br/>
	 * 生成所有数据时，要检验实例和字段值的合法性，<b>所有不合法的数据都要且只能抛出InvalidDataException，在该方法中捕获它</b>。 <br/>
	 * 其它异常不捕获。
	 * 
	 * @param end1FoundationObj
	 * @param bomStructure
	 * @param templateName
	 * @param schema
	 * @throws ServiceRequestException
	 * @throws Exception
	 */
	protected void generateAllData(FoundationObject end1FoundationObj, BOMStructure bomStructure, boolean flag, String origGNumber, int level, DataRule dataRule) throws Exception
	{
		String className = end1FoundationObj.getObjectGuid().getClassName();
		List<FoundationObject> end2FoundationObjList = new ArrayList<FoundationObject>();
		List<BOMStructure> BOMStructureList = Collections.emptyList();
		String newOrigGnumber = origGNumber;
		String structureClass = null;
		BOMTemplateInfo bomTemplate = null;

		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(end1FoundationObj.getObjectGuid().getClassGuid());
		if (classInfo.hasInterface(ModelInterfaceEnum.INewProductOfConfigure)
				&& ConfigParameterConstants.CONFIG_PARAMETER_RESULTRELATION_TEMPLATE_NAME.equals(this.stub.templateName))
		{
			String partNumber = (String) end1FoundationObj.get(ConfigParameterConstants.PARTNUMBER);
			if (!StringUtils.isNullString(partNumber))
			{
				newOrigGnumber = partNumber;
			}

			BOMStructureList = this.stub.getStubService().getCPB().listBOM(end1FoundationObj, null, null, origGNumber);
			if (!SetUtils.isNullList(BOMStructureList))
			{
				for (int i = 0; i < BOMStructureList.size(); i++)
				{
					FoundationObject end2FoundationObject = (FoundationObject) this.getObject(BOMStructureList.get(i).getEnd2ObjectGuid());
					end2FoundationObjList.add(end2FoundationObject);
				}
			}
			else if (BOMStructureList == null)
			{
				BOMStructureList = new ArrayList<BOMStructure>();
			}
		}
		else
		{
			// 通过end1和模板名称拿到BOMView对象
			this.bomView = ((BOMSImpl) this.stub.getStubService().getBOMS()).getBomViewStub().getBOMViewByEND1(end1FoundationObj.getObjectGuid(), this.stub.templateName, false);
			if (this.bomView != null)
			{
				bomTemplate = this.stub.getStubService().getEMM().getBOMTemplateById((String) this.bomView.get(ViewObject.TEMPLATE_ID));
				if (bomTemplate != null)
				{
					structureClass = bomTemplate.getStructureClassName();
				}
				List<UIObjectInfo> uiObjectList = null;
				List<UIObjectInfo> uiObjectList1 = this.stub.getStubService().getEMM().listUIObjectInCurrentBizModel(structureClass, UITypeEnum.FORM, true);
				List<UIObjectInfo> uiObjectList2 = this.stub.getStubService().getEMM().listUIObjectInCurrentBizModel(structureClass, UITypeEnum.LIST, true);
				if (uiObjectList1 != null)
				{
					uiObjectList = uiObjectList1;
				}
				if (uiObjectList2 != null)
				{
					if (uiObjectList == null)
					{
						uiObjectList = uiObjectList2;
					}
					else
					{
						uiObjectList.addAll(uiObjectList2);
					}
				}
				SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(structureClass, uiObjectList);

				// 拿到end1下对应的BOMStructure
				BOMStructureList = ((BOMSImpl) this.stub.getStubService().getBOMS()).getBomStub().listBOM(this.bomView.getObjectGuid(), searchCondition, null, null);
				if (BOMStructureList == null)
				{
					BOMStructureList = Collections.emptyList();
				}
				for (int i = 0; i < BOMStructureList.size(); i++)
				{
					FoundationObject end2FoundationObject = (FoundationObject) this.getObject(BOMStructureList.get(i).getEnd2ObjectGuid());
					end2FoundationObjList.add(end2FoundationObject);
				}
			}
		}
		if (SetUtils.isNullList(this.operationList))
		{
			this.operationList = this.getOperationList(false);
		}
		for (int k = 0; k < this.operationList.size(); k++)
		{
			T operation = this.operationList.get(k);
			String category = this.getOperationCategory(operation);
			if (StringUtils.isNullString(category))
			{
				throw new IllegalArgumentException("no category attribute for " + this.operationList.get(k).name() + " in " + this.xmlPath);
			}
			try
			{
				if (ERP_ITEM.equals(category))
				{
					this.getItemData(end1FoundationObj, BOMStructureList, operation);
				}
				else if (ERP_BOM.equals(category))
				{
					this.getBOMData(end1FoundationObj, BOMStructureList, bomStructure, operation);
				}
				else if (ERP_REPLACESUBSTITUTE.equals(category))
				{
					this.getRSData(end1FoundationObj, BOMStructureList, end2FoundationObjList, bomTemplate, operation);
				}
				else if (ERP_MA.equals(category))
				{
					this.getMAData(end1FoundationObj, className, operation);
				}
			}
			catch (InvalidDataException e)
			{
				this.addDataError(e);
				if (!this.isTraverseAllInstanceIgnoringError())
				{
					return;
				}
			}
		}
		// 如果是BOM展开导出，则递归取得BOM树时的所有数据
		if (this.stub.schema.isExpandBOM() && !SetUtils.isNullList(BOMStructureList))
		{
			for (int i = 0; i < end2FoundationObjList.size(); i++)
			{
				level++;
				this.generateAllData(end2FoundationObjList.get(i), BOMStructureList.get(i), flag, newOrigGnumber, level, dataRule);
			}
		}
	}

	/**
	 * 取料件数据（如果Schema为 传单层物料和BOM，则要把当前料件的一级子阶物料也传过去，否则BOM中子件可能在ERP中不存在）
	 * 
	 * @param end1FoundationObj
	 * @param BOMStructureList
	 * @param operation
	 * @throws ServiceRequestException
	 */
	public void getItemData(FoundationObject end1FoundationObj, List<BOMStructure> BOMStructureList, T operation) throws ServiceRequestException
	{
		RecordSet set = this.getEachItemData(end1FoundationObj, operation);
		this.addToDataMap(ERP_ITEM, set);
		if (this.stub.schema.getOperationList().contains(this.getCreateBOMOperationId()) && !this.stub.schema.isExpandBOM() && !SetUtils.isNullList(BOMStructureList))
		{
			if (set != null) // O23181
			{
				for (int i = 0; i < BOMStructureList.size(); i++)
				{
					ClassStub.decorateObjectGuid(BOMStructureList.get(i).getEnd2ObjectGuid(), this.stub.getStubService());
					this.addToDataMap(ERP_ITEM, this.getEachItemData((FoundationObject) this.getObject(BOMStructureList.get(i).getEnd2ObjectGuid()), operation));
				}
			}
		}
	}

	/**
	 * 取BOM数据
	 * 
	 * @param end1Obj
	 * @param BOMStructureList
	 * @param parentBOMStructure
	 * @param operation
	 * @throws ServiceRequestException
	 */
	protected void getBOMData(FoundationObject end1Obj, List<BOMStructure> BOMStructureList, BOMStructure parentBOMStructure, T operation) throws ServiceRequestException
	{
		// add2StampMap(ERP_BOM, end1Obj);
		this.addToDataMap(ERP_BOM, this.getEachBOMData(end1Obj, BOMStructureList, parentBOMStructure, operation));
	}

	/**
	 * 取替代是取当前end1下面end2的取替代数据<br/>
	 * 分为4种，分别为局部替代、局部取代、全局替代、全局取代
	 * 
	 * @param end1FoundationObj
	 * @param bomStructureList
	 * @param end2FoundationObjList
	 * @param bomTemplate
	 * @param operation
	 * @throws ServiceRequestException
	 */
	protected void getRSData(FoundationObject end1FoundationObj, List<BOMStructure> bomStructureList, List<FoundationObject> end2FoundationObjList, BOMTemplateInfo bomTemplate,
			T operation) throws ServiceRequestException
	{
		// 取替代分3类处理
		// 局部替代
		if (this.getOperationId(operation).equals(this.getCreateLocalSOperationId()))
		{
			for (BOMStructure bomStructure : bomStructureList)
			{
				List<FoundationObject> substituteList = this.stub
						.getStubService()
						.getBRM()
						.listReplaceDataByRang(end1FoundationObj.getObjectGuid(), bomStructure.getEnd2ObjectGuid(), ReplaceRangeEnum.PART, ReplaceTypeEnum.TIDAI,
								bomTemplate.getName(), bomStructure.getBOMKey(), true);
				if (substituteList != null && substituteList.size() != 0)
				{
					Set<String> tempSet = new HashSet<String>();
					for (int j = substituteList.size() - 1; j > -1; j--)
					{
						FoundationObject object = substituteList.get(j);
						if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
						{
							String tempKey = object.get(ReplaceSubstituteConstants.MasterItem + "$MASTER") + "-" + object.get(ReplaceSubstituteConstants.ComponentItem + "$MASTER")
									+ "-" + object.get(ReplaceSubstituteConstants.RSItem + "$MASTER");
							if (tempSet.contains(tempKey))
							{
								substituteList.remove(j);
							}
							else
							{
								tempSet.add(tempKey);
							}
						}
					}

					for (int j = 0; j < substituteList.size(); j++)
					{
						FoundationObject object = substituteList.get(j);
						if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
						{
							if (object.get(ReplaceSubstituteConstants.MasterItem) == null)
							{
								object.put(ReplaceSubstituteConstants.MasterItem, end1FoundationObj.getObjectGuid().getGuid());
								object.put(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.ClassGuid, end1FoundationObj.getObjectGuid().getClassGuid());
							}
							ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
							RecordSet eachLocalS = this.getEachLocalS(substituteList.get(j), operation);
							this.addToDataMap(ERP_REPLACESUBSTITUTE, eachLocalS);
						}
					}
				}
			}
		}
		// 局部取代
		else if (this.getOperationId(operation).equals(this.getCreateLocalROperationId()))
		{
			for (BOMStructure bomStructure : bomStructureList)
			{
				List<FoundationObject> substituteList = this.stub
						.getStubService()
						.getBRM()
						.listReplaceDataByRang(end1FoundationObj.getObjectGuid(), bomStructure.getEnd2ObjectGuid(), ReplaceRangeEnum.PART, ReplaceTypeEnum.QUDAI,
								bomTemplate.getName(), bomStructure.getBOMKey(), true);
				if (substituteList != null && substituteList.size() != 0)
				{
					Set<String> tempSet = new HashSet<String>();
					for (int j = substituteList.size() - 1; j > -1; j--)
					{
						FoundationObject object = substituteList.get(j);
						if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
						{
							String tempKey = object.get(ReplaceSubstituteConstants.MasterItem + "$MASTER") + "-" + object.get(ReplaceSubstituteConstants.ComponentItem + "$MASTER")
									+ "-" + object.get(ReplaceSubstituteConstants.RSItem + "$MASTER");
							if (tempSet.contains(tempKey))
							{
								substituteList.remove(j);
							}
							else
							{
								tempSet.add(tempKey);
							}
						}
					}

					for (int j = 0; j < substituteList.size(); j++)
					{
						FoundationObject object = substituteList.get(j);
						if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
						{
							if (object.get(ReplaceSubstituteConstants.MasterItem) == null)
							{
								object.put(ReplaceSubstituteConstants.MasterItem, end1FoundationObj.getObjectGuid().getGuid());
								object.put(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.ClassGuid, end1FoundationObj.getObjectGuid().getClassGuid());
							}
							ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
							RecordSet eachLocalR = this.getEachLocalR(substituteList.get(j), operation);
							this.addToDataMap(ERP_REPLACESUBSTITUTE, eachLocalR);
						}
					}
				}
			}
		}
		// 全局替代
		else if (this.getOperationId(operation).equals(this.getCreateGlobalSOperationId()))
		{
			// for (FoundationObject end2 : end2FoundationObjList)
			// {
			List<FoundationObject> substituteList = this.stub.getStubService().getBRM()
					.listReplaceDataByRang(null, end1FoundationObj.getObjectGuid(), ReplaceRangeEnum.GLOBAL, ReplaceTypeEnum.TIDAI, null, null, true);
			if (substituteList != null && substituteList.size() != 0)
			{
				Set<String> tempSet = new HashSet<String>();
				for (int j = substituteList.size() - 1; j > -1; j--)
				{
					FoundationObject object = substituteList.get(j);
					if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
					{
						String tempKey = object.get(ReplaceSubstituteConstants.MasterItem + "$MASTER") + "-" + object.get(ReplaceSubstituteConstants.ComponentItem + "$MASTER")
								+ "-" + object.get(ReplaceSubstituteConstants.RSItem + "$MASTER");
						if (tempSet.contains(tempKey))
						{
							substituteList.remove(j);
						}
						else
						{
							tempSet.add(tempKey);
						}
					}
				}

				for (int j = 0; j < substituteList.size(); j++)
				{
					FoundationObject object = substituteList.get(j);
					if (object != null && object.get(ReplaceSubstituteConstants.EffectiveDate) != null)
					{
						ClassStub.decorateObjectGuid(substituteList.get(j).getObjectGuid(), this.stub.getStubService());
						RecordSet eachGlobalS = this.getEachGlobalS(substituteList.get(j), operation);
						this.addToDataMap(ERP_REPLACESUBSTITUTE, eachGlobalS);
					}
				}
			}
		}
	}

	/**
	 * 取料件承认数据
	 */
	protected void getMAData(FoundationObject end1FoundationObj, String className, T operation) throws ServiceRequestException
	{
		String maTemplate = this.getMATemplateName();
		List<StructureObject> relationList = this.stub.getStubService().getBOAS().listObjectOfRelation(end1FoundationObj.getObjectGuid(), maTemplate, null, null, null);
		if (relationList != null && relationList.size() != 0)
		{
			// add2StampMap(ERP_MA, end1FoundationObj);
			for (int i = 0; i < relationList.size(); i++)
			{
				ClassStub.decorateObjectGuid(relationList.get(i).getObjectGuid(), this.stub.getStubService());
				this.addToDataMap(ERP_MA, this.getEachMA(relationList.get(i), ERP_MA, operation));
			}
		}
	}

	/**
	 * 解析xml配置文件中的参数配置<br/>
	 * 
	 */
	protected void initParameter()
	{
		if (this.doc.getRootElement().getChild("parameters") != null)
		{
			this.addParameter(null, this.getParameterByElement(this.doc.getRootElement().getChild("parameters")));
		}

		for (String category : this.categoryList)
		{
			Element categoryEle = this.doc.getRootElement().getChild("category").getChild(category);
			if (categoryEle.getChild("parameters") != null)
			{
				this.addParameter(category, this.getParameterByElement(categoryEle.getChild("parameters")));
			}
		}
	}

	/**
	 * 根据DOM中的Element对象取得参数配置
	 * 
	 * @param parametersEle
	 * @return
	 */
	private ERPParameter getParameterByElement(Element parametersEle)
	{
		Iterator<Element> it = parametersEle.getChildren().iterator();
		ERPParameter param = new ERPParameter();
		Element ele = null;
		while (it.hasNext())
		{
			ele = it.next();
			if (ele.getName().equals("param"))
			{
				param.getParamMap().put(ele.getAttributeValue("name"), ele.getAttributeValue("value"));
			}
		}
		return param;
	}

	/**
	 * 取得参数配置, if <b>category</b> is null, means parameters directly contained
	 * in root.parameters node
	 * 
	 * @param category
	 * @return
	 */
	public ERPParameter getParameter(String category)
	{
		return this.parameterMap.get(category);
	}

	/**
	 * if <b>category</b> is null, means <b>param</b> is the root node
	 * parameters
	 * 
	 * @param category
	 * @param param
	 */
	protected void addParameter(String category, ERPParameter param)
	{
		this.parameterMap.put(category, param);
	}

	/**
	 * 解析xml配置文件中所有category，至少要有一个category,否则报错
	 * 
	 * @return
	 */
	protected List<String> getAllCategory()
	{
		Element categoryEle = this.doc.getRootElement().getChild("category");
		if (categoryEle == null)
		{
			throw new IllegalArgumentException("no category config found in: " + this.xmlPath);
		}
		if (categoryEle.getChildren().size() == 0)
		{
			return Collections.emptyList();
		}
		List<String> list = new ArrayList<String>();
		Iterator<Element> it = this.doc.getRootElement().getChild("category").getChildren().iterator();
		while (it.hasNext())
		{
			list.add(it.next().getName());
		}
		return list;
	}

	/**
	 * 生成数据前的操作，一般是清空容器
	 */
	protected void beforeProcessDataList()
	{
		this.dataMap.clear();
		this.operationList.clear();
	}

	/**
	 * 生成数据后的操作，可打印出比对文件供测试人员观察
	 * 
	 * @throws ServiceRequestException
	 */
	protected void afterProcessDataList() throws ServiceRequestException
	{
		this.operationList.clear();
		this.printComparisonFile();
	}

	/**
	 * 将数据异常信息加入到dataErrorList容器中
	 * 
	 * @param e
	 */
	protected void addDataError(InvalidDataException e)
	{
		this.dataErrorList.add(e);
	}

	/**
	 * 存放数据异常信息的容器
	 * 
	 * @return
	 */
	public List<InvalidDataException> getDataErrorList()
	{
		return this.dataErrorList;
	}

	/**
	 * 将警告信息加入到dataWarnList容器中
	 * 
	 * @param str
	 */
	protected void addDataWarn(String str)
	{
		this.dataWarnList.add(str);
	}

	/**
	 * 警告信息容器
	 * 
	 * @return
	 */
	public List<String> getDataWarnList()
	{
		return this.dataWarnList;
	}

	public Map<String, LinkedList<ObjectGuid>> getExportedGuidMap()
	{
		return this.tobeExportedGuidMap;
	}

	/**
	 * 输出数据异常信息
	 * 
	 * @return
	 */
	public String printDataErrorList()
	{
		if (this.dataErrorList.isEmpty())
		{
			return "";
		}
		StringBuilder sb = new StringBuilder("[ERROR]" + ERPTransferStub.LINE_SEPERATOR);
		for (InvalidDataException e : this.dataErrorList)
		{
			sb.append(e.getMessage()).append(ERPTransferStub.LINE_SEPERATOR);
		}
		return sb.toString();
	}

	/**
	 * this method is not used at current stage, because there is no need to show <b>warn</b> info to end user.
	 * 
	 * @return
	 */
	public String printDataWarnList()
	{
		if (this.dataWarnList.isEmpty())
		{
			return "";
		}
		StringBuilder sb = new StringBuilder("[Warning]" + ERPTransferStub.LINE_SEPERATOR);
		for (String e : this.dataWarnList)
		{
			sb.append(e).append(ERPTransferStub.LINE_SEPERATOR);
		}
		return sb.toString();
	}

	/**
	 * 打印对比文件，基本用不到
	 * 
	 * @throws ServiceRequestException
	 */
	public void printComparisonFile() throws ServiceRequestException
	{

		if (this.isPrintData4Comparison())
		{
			Set<String> categorySet = new HashSet<String>();
			Iterator<String> it = this.uniqueCache.keySet().iterator();
			while (it.hasNext())
			{
				String category = it.next();
				categorySet.add(category);
				if (this.uniqueItemIDMap.get(category) == null)
				{
					this.uniqueItemIDMap.put(category, new LinkedList<String>());
				}
				for (String objGuid : this.uniqueCache.get(category).keySet())
				{
					ObjectGuid g = this.parseObjectGuidFromString(objGuid);
					SystemObject fo = this.getObject(g);
					if (ERP_ITEM.equals(category))
					{
						this.uniqueItemIDMap.get(category).add((String) fo.get(SystemClassFieldEnum.ID.getName()));
					}
					else if (ERP_BOM.equals(category))
					{
						ObjectGuid objectGuid = new ObjectGuid();
						objectGuid.setClassGuid((String) fo.get("END1$CLASS"));
						objectGuid.setGuid((String) fo.get("END1"));
						this.uniqueItemIDMap.get(category).add((String) this.getObject(objectGuid).get(SystemClassFieldEnum.ID.getName()));
					}
					else if (ERP_REPLACESUBSTITUTE.equals(category))
					{
						ObjectGuid objectGuid = new ObjectGuid(fo.get("ComponentITEM$CLASS").toString(), null, fo.get("ComponentItem").toString(), null);
						this.uniqueItemIDMap.get(category).add((String) this.getObject(objectGuid).get(SystemClassFieldEnum.ID.getName()));
					}
					else if (ERP_AVL.equals(category))
					{
						this.uniqueItemIDMap.get(category).add((String) fo.get(SystemClassFieldEnum.ID.getName()));
					}
				}
			}

			it = this.duplicatedCache.keySet().iterator();
			while (it.hasNext())
			{
				String category = it.next();
				if (this.duplicatedItemIDMap.get(category) == null)
				{
					this.duplicatedItemIDMap.put(category, new LinkedList<String>());
				}
				for (String objGuid : this.duplicatedCache.get(category).keySet())
				{
					ObjectGuid g = this.parseObjectGuidFromString(objGuid);
					SystemObject fo = this.getObject(g);
					if (ERP_ITEM.equals(category))
					{
						this.duplicatedItemIDMap.get(category).add((String) fo.get(SystemClassFieldEnum.ID.getName()));
					}
					else if (ERP_BOM.equals(category))
					{
						ObjectGuid objectGuid = new ObjectGuid();
						objectGuid.setClassGuid((String) fo.get("END1$CLASS"));
						objectGuid.setGuid((String) fo.get("END1"));
						this.duplicatedItemIDMap.get(category).add((String) this.getObject(objectGuid).get(SystemClassFieldEnum.ID.getName()));
					}
					else if (ERP_REPLACESUBSTITUTE.equals(category))
					{
						ObjectGuid objectGuid = new ObjectGuid(fo.get("ComponentITEM$CLASS").toString(), null, fo.get("ComponentItem").toString(), null);
						this.duplicatedItemIDMap.get(category).add((String) this.getObject(objectGuid).get(SystemClassFieldEnum.ID.getName()));
					}
					else if (ERP_AVL.equals(category))
					{
						this.duplicatedItemIDMap.get(category).add((String) fo.get(SystemClassFieldEnum.ID.getName()));
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			it = categorySet.iterator();
			while (it.hasNext())
			{
				String category = it.next();
				StringBuilder sb_unique = new StringBuilder("unique ID list(count ").append(this.uniqueItemIDMap.get(category).size()).append("):")
						.append(ERPTransferStub.LINE_SEPERATOR);
				for (String s : this.uniqueItemIDMap.get(category))
				{
					sb_unique.append(s).append(ERPTransferStub.LINE_SEPERATOR);
				}
				StringBuilder sb_duplicated = new StringBuilder();
				if (this.duplicatedItemIDMap.get(category) != null)
				{
					sb_duplicated.append("duplicated ID list(count ").append(this.duplicatedItemIDMap.get(category).size()).append("):").append(ERPTransferStub.LINE_SEPERATOR);
					for (String s : this.duplicatedItemIDMap.get(category))
					{
						sb_duplicated.append(s).append(ERPTransferStub.LINE_SEPERATOR);
					}
				}
				sb.append(sb_unique).append(ERPTransferStub.LINE_SEPERATOR).append(sb_duplicated).trimToSize();
				this.stub.saveComparisonFile(this.stub.jobId + "_" + category + ".txt", sb.toString());
				sb.delete(0, sb.length());
			}
		}
	}

	/**
	 * 将数据记录recordset加入到dataMap，如果重复则不插入。<br/>
	 * 插入返回true,不插入返回false
	 * 
	 * @param category
	 * @param recordSet
	 * @return
	 */
	protected boolean addToDataMap(String category, RecordSet recordSet)
	{
		if (recordSet == null || recordSet.isEmpty())
		{
			return false;
		}
		if (this.dataMap.get(category) == null)
		{
			this.dataMap.put(category, new ArrayList<RecordSet>(this.initialCapacity));
		}
		if (recordSet.getObjectGuid() != null)
		{
			if (this.uniqueCache.get(category) == null)
			{
				this.uniqueCache.put(category, new HashMap<String, Integer>(this.initialCapacity));
			}
			if (this.uniqueCache.get(category).containsKey(recordSet.getObjectGuid().toString()))
			{
				if (this.duplicatedCache.get(category) == null)
				{
					this.duplicatedCache.put(category, new HashMap<String, Integer>(this.initialCapacity));
				}
				this.duplicatedCache.get(category).put(recordSet.getObjectGuid().toString(), 1);
				this.addDataWarn("duplicated data: " + recordSet);
				return false;
			}
			this.uniqueCache.get(category).put(recordSet.getObjectGuid().toString(), 1);
		}
		this.dataMap.get(category).add(recordSet);
		return true;
	}

	/**
	 * 将待传的数据的ObjectGuid存储到tobeExportedGuidMap，用于记录到erp_transfer_log表，
	 */
	public void fillExportGuidMap()
	{
		if (!this.isSaveTransferStatus())
		{
			return;
		}
		this.tobeExportedGuidMap.clear();
		Iterator<String> category_it = this.dataMap.keySet().iterator();
		while (category_it.hasNext())
		{
			String category = category_it.next();
			if (this.tobeExportedGuidMap.get(category) == null)
			{
				this.tobeExportedGuidMap.put(category, new LinkedList<ObjectGuid>());
			}
			Iterator<RecordSet> recordset_it = this.dataMap.get(category).iterator();
			while (recordset_it.hasNext())
			{
				RecordSet set = recordset_it.next();
				if (!this.isMockObjectGuid(set.getObjectGuid()))
				{
					this.tobeExportedGuidMap.get(category).add(set.getObjectGuid());
				}
			}
		}
	}

	/**
	 * 检查对象合法性（一般是状态信息）。 <br/>
	 * 如果数据异常且skipOnFail=true表示跳过实例传输，返回true。<br/>
	 * 如果数据异常且skipOnFail=false则报错。<br/>
	 * 这个方法不要放在getObjectMapValue()中调用，因为getObjectMapValue()只是针对一个category某一个table而言， <br/>
	 * 而此方法是针对整个category而言，如果返回true则就不需要遍历table了。
	 * 
	 * @return
	 * @param object
	 *            可以是一个FoundationObject，也可以是一个BOMView
	 * @param structObj
	 * @param category
	 * @throws ServiceRequestException
	 */
	protected boolean checkObjectReturningSkip(SystemObject object, String category, T operation) throws ServiceRequestException
	{
		boolean skip = false;
		if (object == null)
		{
			return skip;
		}
		List<ERPRestriction> resList = this.getRestrictionByCategory(category);
		for (ERPRestriction res : resList)
		{
			ERPFieldMapping field = new ERPFieldMapping();
			field.setPLMField(res.getField());
			String value = this.getFieldValue(object, field, category, operation);
			if (!res.validate(value))
			{
				if (res.isSkipOnFail())
				{// 如果验证失败了跳过该实例的话则继续验证下一个规则（所有规则必须要验证完毕）
					skip = true;
					continue;
				}
				String contents = "";
				if (res.getField().equals("ISCHECKOUT$"))
				{

					contents = this.stub.getStubService().getMSRM().getMSRString("ID_APP_ERP_ISCHECKOUT", this.stub.lang.toString());
					throw new InvalidDataException(this.getFoundationObjectDesp(object) + "：" + contents);
					// throw new InvalidDataException(this.getFoundationObjectDesp(object) + "：" + value +
					// " is invalid for " + res.toString() + ", and not allowed to skip");
				}
				if (res.getField().equals("STATUS$"))
				{
					Element parentEle = this.doc.getRootElement().getChild("restrictions");
					List<Element> restrictionListEle = parentEle.getChildren("restriction");
					Iterator<Element> restriction_ite = restrictionListEle.iterator();
					List<String> valueList = null;
					while (restriction_ite.hasNext())
					{
						Element ele = restriction_ite.next();
						if (ele.getAttributeValue("field").trim().equals("STATUS$"))
						{
							valueList = Arrays.asList(ele.getAttributeValue("value").trim());
							;
						}
					}
					String message = "";
					for (int i = 0; i < valueList.size(); i++)
					{
						String[] values = valueList.get(i).split(",");
						for (int j = 0; j < values.length; j++)
						{
							message = message + this.stub.getStubService().getMSRM().getMSRString(SystemStatusEnum.getStatusEnum(values[j]).getMsrId(), this.stub.lang.toString())
									+ ",";
						}
					}
					contents = this.stub.getStubService().getMSRM().getMSRString("ID_APP_ERP_STATUS", this.stub.lang.toString());
					contents = MessageFormat.format(contents, message.substring(0, message.length() - 1),
							this.stub.getStubService().getMSRM().getMSRString(SystemStatusEnum.getStatusEnum(value).getMsrId(), this.stub.lang.toString()));
					throw new InvalidDataException(this.getFoundationObjectDesp(object) + "：" + contents);
				}
				if (res.getField().equals("${HasSent}"))
				{
					contents = this.stub.getStubService().getMSRM().getMSRString("ID_APP_ERP_HASSENT", this.stub.lang.toString());
					throw new InvalidDataException(this.getFoundationObjectDesp(object) + "：" + contents);
				}
			}
		}
		if (skip)
		{
			// 如果跳过的话，则加入到dataWarnList中
			this.addDataWarn(this.getFoundationObjectDesp(object) + " is skipped");
		}
		return skip;
	}

	/**
	 * 检查数据合法性
	 * 
	 * @param dataValue
	 * @param field
	 * @return
	 */
	protected void checkFieldValue(String dataValue, ERPFieldMapping field, SystemObject object)
	{
		if (dataValue == null)
		{
			dataValue = "";
		}
		boolean isMandatory = field.isMandatory();
		String ERPField = field.getERPField();
		int maxLength = field.getMaxLength();
		if ("".equals(dataValue))
		{
			if (isMandatory)
			{
				throw new InvalidDataException("ERPField: " + ERPField + " is mandatory, but the value is null in " + this.getFoundationObjectDesp(object));
			}
			else
			{
				return;
			}
		}
		if (!field.getDataType().validate(dataValue, field, this.getBooleanCombinationReg()))
		{
			throw new InvalidDataException("dataType validate error. data: " + dataValue + ", type: " + field.getDataType().getType());
		}
		if ((maxLength != 0) && (this.getDataLength(dataValue) > maxLength))
		{
			throw new InvalidDataException("ERPField: " + ERPField + "'s maxLength is " + maxLength + ", but the value is " + dataValue + " in "
					+ this.getFoundationObjectDesp(object));
		}
	}

	/**
	 * 返回对象<b>object</b>的FULLNAME$
	 * 
	 * @param object
	 * @return
	 */
	public String getFoundationObjectDesp(SystemObject object)
	{
		return (String) object.get("FULLNAME$");
	}

	/**
	 * 根据<b>field</b>获得真正取数据的<b>SystemObject</b>
	 * 
	 * @param baseObj
	 * @param field
	 * @return
	 * @throws ServiceRequestException
	 */
	private SystemObject getTargetObject(SystemObject baseObj, JointField field) throws ServiceRequestException
	{
		SystemObject targetObject = baseObj;
		if (field.isEnd1Field())
		{
			targetObject = this.getObject(((StructureObject) baseObj).getEnd1ObjectGuid());
		}
		else if (field.isEnd2Field())
		{
			targetObject = this.getObject(((StructureObject) baseObj).getEnd2ObjectGuid());
		}
		return targetObject;
	}

	/**
	 * 
	 * @param baseObj
	 *            当前取数据的实例（当传结构关系时，baseObj是StructureObject实例，其它情况下则是FoundationObject）
	 * @param fieldList
	 * @param category
	 * @param operation
	 * @return
	 * @throws ServiceRequestException
	 */
	protected Map<String, String> getObjectMapValue(SystemObject baseObj, List<ERPFieldMapping> fieldList, String category, T operation) throws ServiceRequestException
	{
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (fieldList == null)
		{
			return returnMap;
		}
		for (int j = 0; j < fieldList.size(); j++)
		{
			ERPFieldMapping field = fieldList.get(j);
			String dataValue = this.getFieldValue(baseObj, field, category, operation);
			this.checkFieldValue(dataValue, field, baseObj);
			returnMap.put(field.getERPField(), dataValue);
		}
		return returnMap;
	}

	/**
	 * 取实例数据并缓存，下次再次需要时可直接从缓存中取出
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public SystemObject getObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (!this.itemCache.containsKey(objectGuid.toString()))
		{
			ClassInfo classInfo = this.stub.getStubService().getEMM().getClassByGuid(objectGuid.getClassGuid());
			if (classInfo.hasInterface(ModelInterfaceEnum.IStructureObject))
			{
				List<UIObjectInfo> uiObjectList = this.stub.getStubService().getEMM().listALLFormListUIObjectInBizModel(objectGuid.getClassName());
				SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(objectGuid.getClassName(), uiObjectList);
				StructureObject obj = this.stub.getStubService().getBOAS().getStructureObject(objectGuid, searchCondition);
				this.itemCache.put(objectGuid.toString(), obj);
			}
			else if (classInfo.hasInterface(ModelInterfaceEnum.IBOMStructure))
			{
				List<UIObjectInfo> uiObjectList = this.stub.getStubService().getEMM().listALLFormListUIObjectInBizModel(objectGuid.getClassName());
				SearchCondition searchCondition = SearchConditionFactory.createSearchConditionForBOMStructure(objectGuid.getClassName(), uiObjectList);
				BOMStructure obj = this.stub.getStubService().getBOMS().getBOM(objectGuid, searchCondition, null);
				this.itemCache.put(objectGuid.toString(), obj);
			}
			else
			{
				FoundationObject obj = this.getObjectWithoutDecorate(objectGuid);
				this.itemCache.put(objectGuid.toString(), obj);
			}
		}
		else
		{
			// DynaLogger.error("cache hit:" + this.getFoundationObjectDesp(this.itemCache.get(objectGuid.toString())));
		}
		return this.itemCache.get(objectGuid.toString());
	}

	/**
	 * 解析字段字符串，取得对应的值，做格式化处理并返回。
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	protected String getFieldValue(SystemObject baseObj, ERPFieldMapping field, String category, T operation) throws ServiceRequestException
	{
		String resultValue = "";
		if (!StringUtils.isNullString(field.getPLMField()))
		{
			List<JointField> fieldList = JointField.getFiels(field.getPLMField());
			for (JointField jointField : fieldList)
			{
				SystemObject targetObj = this.getTargetObject(baseObj, jointField);

				ClassStub.decorateObjectGuid(targetObj.getObjectGuid(), this.stubService);
				String clazzName = targetObj.getObjectGuid().getClassName();
				Object dataValue = null;
				String fieldName = jointField.getClearFieldName();
				String delegateFieldName = jointField.getDelegateFieldName();
				if (this.isBuildinField(jointField.getFieldName()))
				{
					dataValue = this.getBuildinFieldValue(jointField.getName(), field, category, baseObj, operation);
				}
				else
				{
					ClassField classField = null;
					classField = this.stub.getStubService().getEMM().getFieldByName(clazzName, fieldName, true);
					if (classField == null)
					{
						throw new InvalidDataException("field not found: " + clazzName + "." + fieldName);
					}
					else
					{
						dataValue = this.getValue(fieldName, delegateFieldName, targetObj, classField.getType(), field, operation);
					}
				}
				resultValue += (dataValue == null ? "" : dataValue.toString());
			}
		}
		if (StringUtils.isNullString(resultValue))
		{
			resultValue = field.getDefaultValue();
		}
		if (resultValue.contains("\n"))
		{
			resultValue = resultValue.replace("\n", " ");
		}
		resultValue = this.formatValue(resultValue, field);
		return resultValue;
	}

	/**
	 * O20061/A.5-易拓集成不能抛字段创建者 by haop
	 * 解析系统字段，特殊对象User时的方法
	 * 
	 * @param baseObj
	 * @param field
	 * @return
	 */
	private String getFieldValue2(SystemObject baseObj, ERPFieldMapping field)
	{
		String resultValue = "";
		if (!StringUtils.isNullString(field.getPLMField()))
		{
			List<JointField> fieldList = JointField.getFiels(field.getPLMField());
			for (JointField jointField : fieldList)
			{
				SystemObject targetObj;
				try
				{
					targetObj = this.getTargetObject(baseObj, jointField);
					Object dataValue = null;
					dataValue = targetObj.get(jointField.toString());
					resultValue += (dataValue == null ? "" : dataValue.toString());
				}
				catch (ServiceRequestException e)
				{
					e.printStackTrace();
				}
			}
		}
		if (StringUtils.isNullString(resultValue))
		{
			resultValue = field.getDefaultValue();
		}
		resultValue = this.formatValue(resultValue, field);
		return resultValue;
	}

	/**
	 * 取<b>object</b>的字段<field>fieldName, delegateFieldName</b>的值 <br/>
	 * 这个方法返回的是PLM系统中对应字段的值
	 * 
	 * @param fieldName
	 * @param delegateFieldName
	 * @param object
	 * @param type
	 *            PLM字段类型
	 * @param field映射字段
	 * @return
	 * @throws ServiceRequestException
	 */
	private Object getValue(String fieldName, String delegateFieldName, SystemObject object, FieldTypeEnum type, ERPFieldMapping field, T operation) throws ServiceRequestException
	{
		Object dataValue = "";
		// 如果该字段对应的值为空值，则直接返回""
		if (object.get(fieldName) == null || "".equals(object.get(fieldName)))
		{
			return dataValue;
		}
		if (FieldTypeEnum.BOOLEAN == type)
		{
			dataValue = object.get(fieldName);
		}
		else if (FieldTypeEnum.FLOAT == type)
		{
			dataValue = new BigDecimal(object.get(fieldName).toString());
		}
		else if (FieldTypeEnum.INTEGER == type)
		{
			dataValue = Integer.valueOf(object.get(fieldName).toString());
		}
		else if (FieldTypeEnum.STRING == type)
		{
			dataValue = object.get(fieldName);
			if ("LIFECYCLEPHASE$".equalsIgnoreCase(fieldName) && StringUtils.isGuid((String) dataValue))
			{
				LifecyclePhaseInfo info = this.stub.getStubService().getEMM().getLifecyclePhaseInfo(dataValue.toString());
				if (info == null)
				{
					dataValue = "";
				}
				else
				{
					if (delegateFieldName == null)
					{// 如果没有delegateFieldName则默认取name
						delegateFieldName = "name";
					}
					if ("name".equalsIgnoreCase(delegateFieldName))
					{
						dataValue = info.getName();
					}
					else if ("title".equalsIgnoreCase(delegateFieldName))
					{
						dataValue = StringUtils.getMsrTitle(info.getTitle(), this.stub.lang.getType());
					}
					else
					{
						throw new InvalidDataException("only name or title are allowed for LifecyclePhase.fieldName = " + fieldName);
					}
				}
			}
		}
		else if (FieldTypeEnum.CLASSIFICATION == type)
		{
			CodeItemInfo classification = this.stub.getStubService().getEMM().getCodeItem((String) object.get(fieldName));
			if (classification == null)
			{
				dataValue = "";
			}
			else
			{
				if (delegateFieldName == null)
				{// 如果没有delegateFieldName则默认取code
					delegateFieldName = "code";
				}
				if ("code".equalsIgnoreCase(delegateFieldName))
				{
					dataValue = classification.getCode();
				}
				else if ("title".equalsIgnoreCase(delegateFieldName))
				{
					dataValue = classification.getTitle(this.stub.lang);
				}
				else
				{
					throw new InvalidDataException("only code or title are allowed for Classification. fieldName = " + fieldName);
				}
			}
		}
		else if (FieldTypeEnum.CODE == type || FieldTypeEnum.CODEREF == type)
		{
			String codeGuid = (String) object.get(fieldName);
			if (StringUtils.isGuid(codeGuid))
			{
				dataValue = this.getCodeValue(codeGuid, delegateFieldName, fieldName);
			}
		}
		else if (FieldTypeEnum.MULTICODE == type)
		{
			String codeGuids = (String) object.get(fieldName);
			if (!StringUtils.isNullString(codeGuids))
			{
				List<String> codeGuidList = StringUtils.splitToListByStrs(codeGuids, ";");
				if (!SetUtils.isNullList(codeGuidList))
				{
					String tempValue = "";
					for (String codeGuid : codeGuidList)
					{
						tempValue = tempValue + this.getCodeValue(codeGuid, delegateFieldName, fieldName) + ";";
					}
					if (tempValue.length() > 0 && tempValue.endsWith(";"))
					{
						dataValue = tempValue.substring(0, tempValue.length() - 1);
					}
				}
			}
		}
		else if (FieldTypeEnum.DATE == type)
		{
			dataValue = DateFormat.format((Date) object.get(fieldName), field.getDateFormat());
		}
		else if (FieldTypeEnum.DATETIME == type)
		{
			dataValue = DateFormat.format((Date) object.get(fieldName), field.getDateFormat());
		}
		else if (FieldTypeEnum.OBJECT == type)
		{
			// 字段是Object类型，要注意这个字段也可能是复杂类型
			String guid = (String) object.get(fieldName);
			String classGuid = (String) object.get(fieldName + "$CLASS");
			// O20061/A.5-易拓集成不能抛字段创建者 by haop
			String className = object.getObjectGuid().getClassName();
			ClassField classField = this.stub.getStubService().getEMM().getFieldByName(className, fieldName, true);
			// 用户
			if (classField.getTypeValue().equalsIgnoreCase("IUser"))
			{
				User user = this.stub.getStubService().getAAS().getUser(guid);
				ERPFieldMapping fieldMapping = new ERPFieldMapping();
				if (StringUtils.isNullString(delegateFieldName))
				{// 如果没有delegateFieldName则默认取NAME字段
					delegateFieldName = "NAME";
				}
				fieldMapping.setPLMField(delegateFieldName);
				dataValue = this.getFieldValue2(user, fieldMapping);
			}
			// 用户组
			else if (classField.getTypeValue().equalsIgnoreCase("IGroup"))
			{
				Group group = this.stub.getStubService().getAAS().getGroup(guid);
				ERPFieldMapping fieldMapping = new ERPFieldMapping();
				if (StringUtils.isNullString(delegateFieldName))
				{// 如果没有delegateFieldName则默认取NAME字段
					delegateFieldName = "NAME";
				}
				fieldMapping.setPLMField(delegateFieldName);
				dataValue = this.getFieldValue2(group, fieldMapping);
			}
			// 其他
			else
			{
				ObjectGuid objectGuidFieldObject = new ObjectGuid();
				objectGuidFieldObject.setGuid(guid);
				objectGuidFieldObject.setClassGuid(classGuid);
				FoundationObject foundationObjectByField = (FoundationObject) this.getObject(objectGuidFieldObject);
				if (foundationObjectByField != null)
				{
					if (StringUtils.isNullString(delegateFieldName))
					{// 如果没有delegateFieldName则默认取ID$字段
						delegateFieldName = "ID$";
					}
					// 不能简单的认为delegateFieldName就是这个对象的普通字段，可能delegateFieldName也是这个对象的复杂字段
					ERPFieldMapping fieldMapping = new ERPFieldMapping();
					fieldMapping.setPLMField(delegateFieldName);
					dataValue = this.getFieldValue(foundationObjectByField, fieldMapping, null, operation);
				}
			}
		}
		else
		{// 其它类型
			dataValue = object.get(fieldName);
		}
		return dataValue;
	}

	private FoundationObject getObjectWithoutDecorate(ObjectGuid objectGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectWithoutDecorate(objectGuid);
	}

	/**
	 * 
	 * @param codeGuid
	 * @param delegateFieldName
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getCodeValue(String codeGuid, String delegateFieldName, String fieldName) throws ServiceRequestException
	{
		String resultValue = "";
		if (StringUtils.isGuid(codeGuid))
		{
			CodeItemInfo code = this.stub.getStubService().getEMM().getCodeItem(codeGuid);
			if (code == null)
			{
				resultValue = "";
			}
			else
			{
				if (delegateFieldName == null)
				{// 如果没有delegateFieldName，则默认取code
					delegateFieldName = "code";
				}
				if ("Code".equalsIgnoreCase(delegateFieldName))
				{
					resultValue = code.getCode();
				}
				else if ("title".equalsIgnoreCase(delegateFieldName))
				{
					resultValue = code.getTitle(this.stub.lang);
				}
				else
				{
					throw new InvalidDataException("only code or title are allowed for Code. fieldName = " + fieldName);
				}
			}
		}
		return resultValue;
	}

	/**
	 * 格式化值
	 * 
	 * @param value
	 * @return
	 */
	protected String formatValue(Object value, ERPFieldMapping fieldMapping)
	{
		if (value == null || "".equals(value))
		{
			return "";
		}

		if (fieldMapping.getDataType() == ERPFieldMappingTypeEnum.NUMBER && fieldMapping.getPrecision() >= 0)
		{
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMaximumFractionDigits(fieldMapping.getPrecision());
			decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
			decimalFormat.setGroupingUsed(false);
			return decimalFormat.format(Double.parseDouble(value.toString()));
		}

		if (fieldMapping.getMaxLength() > 0 && this.getDataLength(value.toString()) > fieldMapping.getMaxLength() && fieldMapping.isTruncWhenLengthExceeds())
		{
			return this.ExceedsData(value, fieldMapping.isLengthExceedsOrientationisRight(), fieldMapping.getMaxLength());
		}

		return value.toString();
	}

	/**
	 * boolean类型的所有可能组合正则 <br/>
	 * 两个值之间必须用|连接
	 * 
	 * @return
	 */
	protected String getBooleanCombinationReg()
	{
		return "(?i)[(true|false)(1|0)(T|F)(true|false)]";
	}

	/**
	 * 如果<b>fieldName</b>是内置字段，返回内置字段的值，否则返回null <br/>
	 * 用户在xml文件自定义字段 比 系统内置字段拥有较高优先级(如果自定义字段和内置字段重名，则使用自定义字段)
	 * 
	 * @param fieldName
	 * @return
	 */
	protected String getBuildinFieldValue(String fieldName, ERPFieldMapping field, String category, SystemObject baseObj, T operation)
	{
		String dataValue = null;
		if (this.getParameter(null).getParamMap().get(fieldName) != null)
		{
			dataValue = this.getParameter(null).getParamMap().get(fieldName);
		}
		else
		{
			if (RESERVED_DATE.equalsIgnoreCase(fieldName))
			{
				if (RESERVED_EMPTY.equalsIgnoreCase(field.getDateFormat()))
				{
					return String.valueOf(CURRENT_DATE.getTime());
				}
				else
				{
					dataValue = DateFormat.format(CURRENT_DATE, field.getDateFormat());
				}
			}
			else if (RESERVED_JOBID.equalsIgnoreCase(fieldName))
			{
				dataValue = this.stub.jobId;
			}
			// ${DataKey} 和 ${XMLPackageNo} 在生成数据的时候还没有值，这两个自定义标签只有在开始生成XML传输的时候才能知道值
			// else if (reservedDataKey.equalsIgnoreCase(fieldName))
			// {
			// dataValue = this.stub.getDataKey();
			// }
			// else if (reservedXMLPackageNo.equalsIgnoreCase(fieldName))
			// {
			// dataValue = this.stub.getXMLPackNo();
			// }
			else if (RESERVED_EMPTY.equalsIgnoreCase(fieldName))
			{
				dataValue = "";
			}
			else if (RESERVED_HASSENT.equalsIgnoreCase(fieldName))
			{
				if (!this.isSaveTransferStatus())
				{
					dataValue = "N";
				}
				else
				{
					Map<String, Object> param = new HashMap<String, Object>();
					if (ERP_BOM.equals(category))
					{
						param.put(ERPTransferLog.targetGuid, this.bomView.getGuid());
						param.put("CLASSGUID", this.bomView.getObjectGuid().getClassGuid());
						param.put(ERPTransferLog.iteration, this.bomView.getIterationId());
					}
					else
					{
						param.put(ERPTransferLog.targetGuid, baseObj.getGuid());
						param.put("CLASSGUID", baseObj.getObjectGuid().getClassGuid());
						param.put(ERPTransferLog.iteration, baseObj.get(SystemClassFieldEnum.ITERATIONID.getName()));
					}
					param.put(ERPTransferLog.ERPName, this.stub.ERPType.getProName());
					param.put(ERPTransferLog.ERPOpertion, this.getOperationId(operation));
					param.put(ERPTransferLog.category, category);
					try
					{
						List<ERPTransferLog> logList = this.stub.getStubService().getERPTransferLog(param);
						if (logList.isEmpty())
						{
							dataValue = "N";
						}
						else
						{
							dataValue = "Y";
						}
					}
					catch (ServiceRequestException e)
					{
						throw new InvalidDataException(e.getMessage());
					}
				}
			}
			else if (RESERVED_USERID.equalsIgnoreCase(fieldName))
			{
				return this.stub.userId;
			}
			else if (RESERVED_ERP_USERID.equalsIgnoreCase(fieldName))
			{
				return this.getParameter(null).getParamMap().get("user");
			}
			else if (RESERVED_FACTORY.equalsIgnoreCase(fieldName))
			{
				return this.stub.factory;
			}
			else if (RESERVED_DATAKEY.equalsIgnoreCase(fieldName))
			{
				return this.stub.getDataKey();
			}
			else
			{
				// 如果没有为自定义字段赋值，则直接返回自定义字段名(${DataKey} ${XMLPackageNo} ${Designators}这些字段由于还不能确定值，因此直接返回他们的名字)
				dataValue = fieldName;
			}
		}
		return dataValue == null ? "" : dataValue;
	}

	/**
	 * 判断一个字段是否为自定义字段，自定义字段格式为${XX}, 如${Date}表示当前日期/时间
	 * 
	 * @param fieldName
	 * @return
	 */
	protected boolean isBuildinField(String fieldName)
	{
		return fieldName.matches("^\\$\\{\\w+\\}$");
	}

	/**
	 * 返回当前Operation的Id
	 * 
	 * @return
	 */
	public abstract String getOperationId(T operation);

	/**
	 * 生成数据的时候，当前的Operation
	 * 
	 * @return
	 */
	public abstract String getOperationCrossServiceName(T operation);

	public abstract String getOperationName(T operation);

	/**
	 * 返回Operation的category
	 * 
	 * @return
	 */
	public abstract String getOperationCategory(T operation);

	public String getCreateItemOperationId()
	{
		return "createItem";
	}

	/**
	 * 返回传BOM操作的id
	 * 
	 * @return
	 */
	protected String getCreateBOMOperationId()
	{
		return "createBOM";
	}

	protected String getCreateLocalROperationId()
	{
		return "createLocalR";
	}

	protected String getCreateLocalSOperationId()
	{
		return "createLocalS";
	}

	protected String getCreateGlobalROperationId()
	{
		return "createGlobalR";
	}

	protected String getCreateGlobalSOperationId()
	{
		return "createGlobalS";
	}

	/**
	 * 子类配置中是否存在相同的字段<br/>
	 * 子类中相应PLM字段为空且defaultValue也为空返回true,其它情况返回false
	 * 
	 * @param fieldList
	 * @param parentName
	 * @return
	 */
	protected boolean existSameField(List<ERPFieldMapping> fieldList, String ERPField)
	{
		ERPFieldMapping field = null;
		for (int i = 0; i < fieldList.size(); i++)
		{
			field = fieldList.get(i);
			if (field.getERPField().equals(ERPField))
			{// 在子类中找到相应的字段配置
				return true;
			}
		}
		// 程序运行到这，表明在子类中没找到相应字段配置
		return false;
	}

	/**
	 * 从xml配置文件中解析得到一个category下class的字段映射，注意：递归取得父类的字段映射，同时子类的字段映射覆盖父类的映射。
	 * 
	 * @param usedClassInfo
	 * @param erpClass
	 * @param pdmColumnList
	 * @throws ServiceRequestException
	 */
	private void getFieldListFromXML(ClassInfo PLMClass, String category, Map<String, List<ERPFieldMapping>> fieldMap) throws ServiceRequestException
	{
		// 递归到顶层祖先，退出
		if (PLMClass == null)
		{
			return;
		}

		List<Element> classEleList = this.doc.getRootElement().getChild("category").getChild(category).getChildren("class");
		if (!classEleList.isEmpty())
		{
			Iterator<Element> classIt = classEleList.iterator();
			Element classEle = null;
			while (classIt.hasNext())
			{
				classEle = classIt.next();
				if (classEle.getAttributeValue("name").equals(PLMClass.getName()))
				{
					Iterator<Element> tableIt = classEle.getChildren("table").iterator();
					Element tableEle = null;
					while (tableIt.hasNext())
					{
						tableEle = tableIt.next();
						String tableName = tableEle.getAttributeValue("name");
						Iterator<Element> fieldIt = tableEle.getChildren("mapping").iterator();
						Element mappingEle = null;
						while (fieldIt.hasNext())
						{
							mappingEle = fieldIt.next();
							if (fieldMap.get(tableName) == null)
							{
								fieldMap.put(tableName, new ArrayList<ERPFieldMapping>());
							}
							Map<String, String> paramMap = this.getMapByElement(mappingEle);
							if (!this.existSameField(fieldMap.get(tableName), paramMap.get("ERPField")))
							{

								fieldMap.get(tableName).add(this.getFieldMapping(paramMap));
							}
						}
						mappingEle = null;
						fieldIt = null;
					}
					break;
				}
			}
		}

		if (PLMClass.getSuperclass() == null)
		{
			return;
		}
		// 取自己未配置但父类配置的字段映射
		this.getFieldListFromXML(this.stub.getStubService().getEMM().getClassByName(PLMClass.getSuperclass()), category, fieldMap);
	}

	protected Map<String, String> getMapByElement(Element element)
	{
		Map<String, String> map = new HashMap<String, String>();
		Iterator<Element> it = element.getChildren("param").iterator();
		Element ele = null;
		while (it.hasNext())
		{
			ele = it.next();
			map.put(ele.getAttributeValue("name"), ele.getAttributeValue("value"));
		}
		return map;
	}

	/**
	 * xml字段映射配置转化为ERPFieldMapping
	 * 
	 * @param map
	 * @return
	 */
	protected ERPFieldMapping getFieldMapping(Map<String, String> map)
	{
		ERPFieldMapping mapping = new ERPFieldMapping();
		mapping.setPLMField(StringUtils.convertNULLtoString(map.get("PLMField")));
		mapping.setERPField(StringUtils.convertNULLtoString(map.get("ERPField")));
		mapping.setDefaultValue(StringUtils.convertNULLtoString(map.get("defaultValue")));
		mapping.setDescription(StringUtils.convertNULLtoString(map.get("description")));
		mapping.setKeyColumn("true".equalsIgnoreCase(StringUtils.convertNULLtoString(map.get("isKeyColumn"))));
		mapping.setMandatory("true".equalsIgnoreCase(StringUtils.convertNULLtoString(map.get("isMandatory"))));
		mapping.setShowDefault("true".equalsIgnoreCase(StringUtils.convertNULLtoString(map.get("isShowDefault"))));
		String dateFormat = StringUtils.convertNULLtoString(map.get("dateFormat"));
		if (StringUtils.isNullString(dateFormat))
		{
			mapping.setDateFormat(this.defaultDateFormat);
		}
		else
		{
			mapping.setDateFormat(dateFormat);
		}
		String maxLength = StringUtils.convertNULLtoString(map.get("maxLength"));
		mapping.setMaxLength("".equals(maxLength) ? 0 : Integer.parseInt(maxLength));
		mapping.setTruncWhenLengthExceeds("true".equalsIgnoreCase(map.get("truncWhenLengthExceeds")));
		mapping.setLengthExceedsOrientationisRight("right".equalsIgnoreCase(map.get("LengthExceedsOrientation")));
		String precision = StringUtils.convertNULLtoString(map.get("precision"));
		if (!"".equals(precision))
		{
			mapping.setPrecision(Integer.parseInt(precision));
		}
		String dataType = StringUtils.convertNULLtoString(map.get("dataType"));
		if (!"".equals(dataType))
		{
			mapping.setDataType(ERPFieldMappingTypeEnum.getEnumByType(dataType));
		}
		if (!this.validateERPFieldMapping(mapping))
		{
			throw new IllegalArgumentException("dyan.app.service.brs.erpi.dataExport.IntegrateERP#validateERPFieldMapping error");
		}
		return mapping;
	}

	/**
	 * 校验ERPFieldMapping是否合法 <br/>
	 * 1: ERPField为空则抛异常<br/>
	 * 
	 * @return
	 */
	private boolean validateERPFieldMapping(ERPFieldMapping mapping)
	{
		if ("".equals(mapping.getERPField()))
		{
			throw new IllegalArgumentException("ERPField can't be null");
		}
		return true;
	}

	/**
	 * 从缓存中读取一个category某一类的一个表对应的字段，如果缓存中没有则从xml文件中读取并做缓存。
	 * 
	 * @param category
	 * @param className
	 * @param tableName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPFieldMapping> getFieldFromMap(String category, String className, String tableName) throws ServiceRequestException
	{
		if (this.PLMColumnMap.get(category) == null || this.PLMColumnMap.get(category).get(className) == null
				|| this.PLMColumnMap.get(category).get(className).get(tableName) == null || this.PLMColumnMap.get(category).get(className).get(tableName).isEmpty())
		{
			Map<String, List<ERPFieldMapping>> tableFieldMap = new HashMap<String, List<ERPFieldMapping>>();
			this.getFieldListFromXML(this.stub.getStubService().getEMM().getClassByName(className), category, tableFieldMap);
			if (SetUtils.isNullList(tableFieldMap.get(tableName)))
			{
				throw new IllegalArgumentException("no field mapping for " + category + "." + className + "." + tableName + " in " + this.xmlPath);
			}
			if (this.shuldSortFields())
			{
				Collections.sort(tableFieldMap.get(tableName), this.fieldComparator);
			}
			Map<String, Map<String, List<ERPFieldMapping>>> classFieldMap = new HashMap<String, Map<String, List<ERPFieldMapping>>>();
			classFieldMap.put(className, tableFieldMap);
			this.PLMColumnMap.put(category, classFieldMap);
		}
		return this.PLMColumnMap.get(category).get(className).get(tableName);
	}

	/**
	 * 是否把列名进行排序<br/>
	 * 在YF E10集成中，由于所有列名都是放在一列的，而配置文件中父类和子类的字段顺序并不一定相同，因此要进行排序<br/>
	 * 否则列和值会发生错序
	 */
	protected boolean shuldSortFields()
	{
		return false;
	}

	public Map<String, List<RecordSet>> getDataMap()
	{
		return this.dataMap;
	}

	/**
	 * 取得每一包的数据
	 * 
	 * @param category
	 * @return
	 */
	public List<RecordSet> getHeaderPackageOfData(String category)
	{
		int count = this.getRecordSetSizePerXMLPackage(category);
		if (count > this.getDataMap().get(category).size())
		{
			throw new IllegalStateException("out of data size(dataMap) in category: " + category);
		}
		Iterator<RecordSet> it = this.getDataMap().get(category).iterator();
		int temp = 0;
		List<RecordSet> subList = new ArrayList<RecordSet>();
		while (it.hasNext())
		{
			RecordSet rs = it.next();
			subList.add(rs);
			it.remove();
			temp++;
			if (temp >= count)
			{
				break;
			}
		}
		return subList;
	}

	/**
	 * 返回<b>operation</b>可能产生的xml包数 <br/>
	 * 根据totalRecordSize和pageRecordSize得到pageCount的公式：<br/>
	 * (totalRecordSize + pageRecordSize - 1) / pageRecordSize ，而不是 (totalRecordSize - 1) / pageRecordSize + 1，原因在于：
	 * 如果totalRecordSize=0,则第二个公式得到的结果是1，显然是错误的。
	 * 
	 * @param operation
	 * @return
	 */
	public int getXMLPackageCount(T operation)
	{
		final String category = this.getOperationCategory(operation);
		if (category.equalsIgnoreCase(IntegrateERP.ERP_BOM))
		{
			int BOMBatchSize = this.getBOMBatchSize();
			if (SetUtils.isNullList(this.dataMap.get(category)))
			{
				return -1;
			}
			else if (BOMBatchSize == 0)
			{
				return 1;
			}
			else
			{
				return (this.dataMap.get(category).size() + BOMBatchSize - 1) / BOMBatchSize;
			}
		}
		else
		{
			int batchSize = this.getBatchSize();

			if (SetUtils.isNullList(this.dataMap.get(category)))
			{
				return -1;
			}
			else if (batchSize == 0)
			{
				return 1;
			}
			else
			{
				return (this.dataMap.get(category).size() + batchSize - 1) / batchSize;
			}
		}
	}

	/**
	 * 返回每包xml中数据的笔数<br/>
	 * 同时还要受限于当前还剩下数据的笔数, return value is no more greater than <b>batchSize</b> or <b>BOMBatchSize</b>.
	 * 
	 * @param category
	 * @param remainingCount
	 * @return
	 */
	protected int getRecordSetSizePerXMLPackage(String category)
	{
		final int remainingCount = this.dataMap.get(category).size();
		// -1 means all recordsets are in one package
		int result = -1;
		if (ERP_BOM.equals(category))
		{
			int BOMBatchSize = this.getBOMBatchSize();
			result = BOMBatchSize > 0 ? BOMBatchSize : -1;
		}
		else
		{
			int batchSize = this.getBatchSize();
			result = batchSize > 0 ? batchSize : -1;
		}
		return (result == -1 || result >= remainingCount) ? remainingCount : result;
	}

	protected String getTableNameById(String category, String id)
	{
		return this.getTables(category).get(id);
	}

	/**
	 * 取得category下的tableMap, id和name对
	 * 
	 * @param category
	 * @return
	 */
	public Map<String, String> getTables(String category)
	{
		if (this.tableMap.isEmpty() || this.tableMap.get(category) == null || this.tableMap.get(category).isEmpty())
		{
			Element tableEle = this.doc.getRootElement().getChild("category").getChild(category).getChild("tables");
			if (tableEle == null)
			{
				return null;
			}
			Iterator<Element> tableIt = tableEle.getChildren("table").iterator();
			Element tempEle = null;
			Map<String, String> tableMap = new LinkedHashMap<String, String>();
			while (tableIt.hasNext())
			{
				tempEle = tableIt.next();
				tableMap.put(tempEle.getAttributeValue("id"), tempEle.getAttributeValue("name"));
			}
			tempEle = null;
			tableIt = null;
			this.tableMap.put(category, tableMap);
		}
		return this.tableMap.get(category);
	}

	/**
	 * PLM向ERP传数据的xml，每个ERP接收的格式都不一样，因此要在子类中重写
	 * 
	 * @param operation
	 * @param totalCount
	 * @param index
	 * @return
	 * @throws UnknownHostException
	 */
	public abstract Document getCreateDataXML(T operation, int totalCount, int index) throws Exception;

	/**
	 * 返回<b>Schema</b>下的所有operation
	 * 
	 * @param isMergeByCategory
	 *            是否合并相同category的operation
	 * @return
	 * @throws ServiceRequestException
	 */
	public abstract List<T> getOperationList(boolean isMergeByCategory) throws ServiceRequestException;

	/**
	 * xml配置文件的位置
	 * 
	 * @return
	 */
	public String getXMLPath()
	{
		return this.xmlPath;
	}

	/**
	 * <b>RSItem</b>字段是取替代类中的取替代件字段
	 * 
	 * @param rs
	 * @throws ServiceRequestException
	 */
	protected void addRSEnd2FoundationObject(FoundationObject rs, T operation) throws ServiceRequestException
	{
		// ObjectGuid objectGuid = new ObjectGuid(rs.get("RSITEM$CLASS").toString(), null, rs.get("RSItem").toString(),
		// null);
		ObjectGuid objectGuid = new ObjectGuid((String) rs.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.ClassGuid), null, null,
				(String) rs.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER), true, null);
		FoundationObject obj = (FoundationObject) this.getObject(objectGuid);
		this.addToDataMap(ERP_ITEM, this.getEachItemData(obj, this.getRSItemOpertion()));
	}

	/**
	 * 是否把取替代关系的取替代件传给ERP
	 * 
	 * @return
	 */
	protected boolean shouldAddRSEnd2Item()
	{
		return this.stub.schema.isExportRSItem() && this.stub.schema.getOperationList().contains(this.getCreateItemOperationId());
	}

	/**
	 * 在传取替代件时，从<b>schema</b>中获取createItem
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	private T getRSItemOpertion() throws ServiceRequestException
	{
		for (T operation : this.getOperationList(false))
		{
			if (this.getOperationId(operation).equals(this.getCreateItemOperationId()))
			{
				return operation;
			}
		}
		return null;
	}

	/**
	 * 取得每个物料的数据
	 * 
	 * @param end1Obj
	 * @param category
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	protected RecordSet getEachItemData(FoundationObject end1Obj, T operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(end1Obj, ERP_ITEM, operation))
		{
			return null;
		}
		ClassStub.decorateObjectGuid(end1Obj.getObjectGuid(), this.stubService);
		RecordSet recordSet = new RecordSet(end1Obj.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_ITEM).values().iterator();
		String tableName = null;
		// add2StampMap(ERP_ITEM, end1Obj);
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			// fieldList： 获得一个table下的所有字段匹配
			List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_ITEM, end1Obj.getObjectGuid().getClassName(), tableName);
			TableRecordData tableRecord = new TableRecordData(this.getObjectMapValue(end1Obj, fieldList, ERP_ITEM, operation));
			recordSet.put(tableName, tableRecord);
		}
		return recordSet;
	}

	/**
	 * 取得每一层BOM结构数据
	 * 
	 * @param bomStructureList
	 * @param category
	 * @param className
	 * @return
	 * @throws ServiceRequestException
	 */
	protected RecordSet getEachBOMData(FoundationObject end1Obj, List<BOMStructure> bomStructureList, BOMStructure parentBOMStructure, T operation) throws ServiceRequestException
	{
		if (end1Obj != null && end1Obj.getStatus().equals(SystemStatusEnum.OBSOLETE)) // O23181
		{
			return null;
		}
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

		boolean isConfigBOMExport = ConfigParameterConstants.CONFIG_PARAMETER_RESULTRELATION_TEMPLATE_NAME.equals(this.stub.templateName);
		ObjectGuid objGuid = bomStructureList.isEmpty() || isConfigBOMExport ? this.getMockObjectGuid(end1Obj) : this.bomView.getObjectGuid();
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
					ObjectGuid structureObjectGuid = bomStructureList.get(i).getObjectGuid();
					ClassStub.decorateObjectGuid(structureObjectGuid, this.stubService);
					List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_BOM, structureObjectGuid.getClassName(), tableName);
					tableRecord.add(this.getObjectMapValue(bomStructureList.get(i), fieldList, ERP_BOM, operation));
				}
				recordSet.put(tableName, tableRecord);
			}
		}
		else if (this.hasBOM(end1Obj))
		{
			dealEmptyBom(recordSet, end1Obj, operation);
		}
		return recordSet;
	}

	protected void dealEmptyBom(RecordSet recordSet, FoundationObject end1Obj, T operation) throws ServiceRequestException
	{
		Iterator<String> tableIt = this.getTables(ERP_BOM).values().iterator();
		String className = this.getArbitraryBOMClassName();
		String tableName = null;
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			this.getFieldFromMap(ERP_BOM, className, tableName);
		}

		tableName = this.getBOMHeaderTableName();
		TableRecordData tableRecord = new TableRecordData();
		List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_BOM, this.getArbitraryBOMClassName(), tableName);
		fieldList = this.removeEnd1Prefix(fieldList);
		this.erpFieldMappingContainer.put(tableName, className, fieldList);
		tableRecord.add(this.getObjectMapValue(end1Obj, fieldList, ERP_BOM, operation));
		recordSet.put(tableName, tableRecord);
	}

	/**
	 * 取每一笔 局部替代数据
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	protected RecordSet getEachLocalS(FoundationObject obj, T operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(obj, ERP_REPLACESUBSTITUTE, operation))
		{
			return null;
		}
		if (this.shouldAddRSEnd2Item())
		{
			this.addRSEnd2FoundationObject(obj, operation);
		}
		ClassStub.decorateObjectGuid(obj.getObjectGuid(), this.stubService);
		// add2StampMap(ERP_REPLACESUBSTITUTE, obj);
		RecordSet recordSet = new RecordSet(obj.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
		String className = obj.getObjectGuid().getClassName();
		String tableName = null;
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_REPLACESUBSTITUTE, className, tableName);
			this.erpFieldMappingContainer.put(tableName, className, fieldList);
			TableRecordData tableRecord = new TableRecordData(this.getObjectMapValue(obj, fieldList, ERP_REPLACESUBSTITUTE, operation));
			recordSet.put(tableName, tableRecord);
		}
		String key = StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSType)) + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.Scope));
		recordSet.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER));
		recordSet.setItemKey(StringUtils.generateMd5(key));
		return recordSet;
	}

	/**
	 * 取每一笔全局替代数据
	 * 
	 * @param obj
	 * @param category
	 * @return
	 * @throws ServiceRequestException
	 */
	protected RecordSet getEachGlobalS(FoundationObject obj, T operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(obj, ERP_REPLACESUBSTITUTE, operation))
		{
			return null;
		}
		if (this.shouldAddRSEnd2Item())
		{
			this.addRSEnd2FoundationObject(obj, operation);
		}
		ClassStub.decorateObjectGuid(obj.getObjectGuid(), this.stubService);
		// add2StampMap(ERP_REPLACESUBSTITUTE, obj);
		RecordSet data = new RecordSet(obj.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
		String tableName = null;
		String className = obj.getObjectGuid().getClassName();
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_REPLACESUBSTITUTE, className, tableName);
			this.erpFieldMappingContainer.put(tableName, className, fieldList);
			data.put(tableName, new TableRecordData(this.getObjectMapValue(obj, fieldList, ERP_REPLACESUBSTITUTE, operation)));
		}
		String key = StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSType)) + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.Scope));
		data.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER));
		data.setItemKey(StringUtils.generateMd5(key));
		return data;
	}

	/**
	 * 取每一笔局部取代数据
	 * 
	 * @param obj
	 * @param category
	 * @return
	 * @throws ServiceRequestException
	 */
	protected RecordSet getEachLocalR(FoundationObject obj, T operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(obj, ERP_REPLACESUBSTITUTE, operation))
		{
			return null;
		}
		if (this.shouldAddRSEnd2Item())
		{
			this.addRSEnd2FoundationObject(obj, operation);
		}
		ClassStub.decorateObjectGuid(obj.getObjectGuid(), this.stubService);
		// add2StampMap(ERP_REPLACESUBSTITUTE, obj);
		RecordSet data = new RecordSet(obj.getObjectGuid());
		Iterator<String> tableIt = this.getTables(ERP_REPLACESUBSTITUTE).values().iterator();
		String tableName = null;
		String className = obj.getObjectGuid().getClassName();
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			List<ERPFieldMapping> fieldList = this.getFieldFromMap(ERP_REPLACESUBSTITUTE, className, tableName);
			this.erpFieldMappingContainer.put(tableName, className, fieldList);
			data.put(tableName, new TableRecordData(this.getObjectMapValue(obj, fieldList, ERP_REPLACESUBSTITUTE, operation)));
		}
		String key = StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.MasterItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.ComponentItem + ReplaceSubstituteConstants.MASTER))
				+ StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSType)) + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.Scope));
		data.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(obj.get(ReplaceSubstituteConstants.RSItem + ReplaceSubstituteConstants.MASTER));
		data.setItemKey(StringUtils.generateMd5(key));
		return data;
	}

	/**
	 * 取料件承认数据 <br/>
	 * 系统中料件承认是 Item 和ApprovalSheet类通过一个关联类来实现，因此这里必须要给<b>structObj</b>实例，
	 * 不然从ApprovalSheet实例导航不到Item实例
	 * 
	 * @param structObj
	 *            ApprovalSheet和Item关联类的实例
	 * @param category
	 * @return
	 * @throws ServiceRequestException
	 */
	protected RecordSet getEachMA(StructureObject object, String category, T operation) throws ServiceRequestException
	{
		if (this.checkObjectReturningSkip(object, category, operation))
		{
			return null;
		}
		ClassStub.decorateObjectGuid(object.getObjectGuid(), this.stubService);
		RecordSet data = new RecordSet(object.getObjectGuid());
		Iterator<String> tableIt = this.getTables(category).values().iterator();
		String tableName = null;
		String className = object.getObjectGuid().getClassName();
		while (tableIt.hasNext())
		{
			tableName = tableIt.next();
			List<ERPFieldMapping> fieldList = this.getFieldFromMap(category, className, tableName);
			this.erpFieldMappingContainer.put(tableName, className, fieldList);
			data.put(tableName, new TableRecordData(this.getObjectMapValue(object, fieldList, category, operation)));
		}
		String key = StringUtils.convertNULLtoString(object.getEnd1ObjectGuid().getMasterGuid());
		data.setGroupKey(key);
		key = key + StringUtils.convertNULLtoString(object.getEnd2ObjectGuid().getMasterGuid());
		data.setItemKey(StringUtils.generateMd5(key));
		return data;
	}

	/**
	 * 返回非BOM实例时的批量传输数，如果为0则表示不分包
	 * 
	 * @return
	 */
	public int getBatchSize()
	{
		String batchSizeStr = this.getParameter(null).getParamMap().get("batchSize");
		if (!StringUtils.isNullString(batchSizeStr))
		{
			return Integer.parseInt(batchSizeStr);
		}
		else
		{
			return 0;
		}
	}

	/**
	 * 返回BOM传输时批量传输数，为0表示不分包
	 * 
	 * @return
	 */
	public int getBOMBatchSize()
	{
		String BOMBatchSizeStr = this.getParameter(null).getParamMap().get("BOMBatchSize");
		if (!StringUtils.isNullString(BOMBatchSizeStr))
		{
			return Integer.parseInt(BOMBatchSizeStr);
		}
		else
		{
			return 0;
		}
	}

	public List<String> getCategoryList()
	{
		return this.categoryList;
	}

	/**
	 * 料件承认模板
	 * 
	 * @return
	 */
	protected String getMATemplateName()
	{
		return this.getParameter(null).getParamMap().get("maTemplate");
	}

	/**
	 * 轮询间隔
	 * 
	 * @return
	 */
	public int getOperationPollingTime()
	{
		String value = this.getParameter(null).getParamMap().get("operationPollingTime");
		if (StringUtils.isNullString(value))
		{
			return this.stub.getDefaultOperationPollingTime();
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("operationPollingTime is not a number: " + value);
		}
	}

	/**
	 * 每个Operation最大生成时间
	 * 
	 * @return
	 */
	public int getOperationLiveTime()
	{
		String value = this.getParameter(null).getParamMap().get("operationLiveTime");
		if (StringUtils.isNullString(value))
		{
			return this.stub.getDefaultOperationPollingTime();
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("operationLiveTime is not a number: " + value);
		}
	}

	/**
	 * 释放占用资源，清空容器
	 */
	public void releaseResource()
	{
		this.bomView = null;
		this.categoryList.clear();
		this.dataErrorList.clear();
		this.dataWarnList.clear();
		this.tobeExportedGuidMap.clear();
		this.dataMap.clear();
		this.parameterMap.clear();
		this.PLMColumnMap.clear();
		this.restrictionList.clear();
		this.doc = null;
		this.tableMap.clear();
		this.itemCache.clear();
		this.uniqueCache.clear();
	}

	/**
	 * 将字段配置中带有end1$的转换成end2$
	 * 
	 * @param fieldList
	 * @return
	 */
	protected List<ERPFieldMapping> convertEnd1ToEnd2Field(List<ERPFieldMapping> fieldList)
	{
		List<ERPFieldMapping> newFieldList = new ArrayList<ERPFieldMapping>(fieldList.size());
		ERPFieldMapping field = null;
		for (int i = 0; i < fieldList.size(); i++)
		{
			field = new ERPFieldMapping();
			if (!StringUtils.isNullString(field.getPLMField()) && field.getPLMField().matches("(?i)End2\\$.*"))
			{
				continue;
			}
			String PLMField = fieldList.get(i).getPLMField();
			field.setPLMField(PLMField.replaceAll("(?i)End1\\$", "End2\\$"));
			this.copyOtherFieldValue(field, fieldList.get(i));
			newFieldList.add(field);
		}
		return newFieldList;
	}

	/**
	 * 移除字段配置中end1$前缀
	 * 
	 * @param fieldList
	 * @return
	 */
	protected List<ERPFieldMapping> removeEnd1Prefix(List<ERPFieldMapping> fieldList)
	{
		List<ERPFieldMapping> newFieldList = new ArrayList<ERPFieldMapping>(fieldList.size());
		ERPFieldMapping field = null;
		for (int i = 0; i < fieldList.size(); i++)
		{
			field = new ERPFieldMapping();
			String PLMField = fieldList.get(i).getPLMField().replaceAll("(?i)End1\\$", "");
			if (PLMField.matches("(?i)End2\\$.*"))
			{
				PLMField = "";
			}
			field.setPLMField(PLMField);
			this.copyOtherFieldValue(field, fieldList.get(i));
			newFieldList.add(field);
		}
		return newFieldList;
	}

	/**
	 * 移除end2$前缀
	 * 
	 * @param fieldList
	 * @return
	 */
	protected List<ERPFieldMapping> removeEnd2Prefix(List<ERPFieldMapping> fieldList)
	{
		List<ERPFieldMapping> newFieldList = new ArrayList<ERPFieldMapping>(fieldList.size());
		ERPFieldMapping field = null;
		for (int i = 0; i < fieldList.size(); i++)
		{
			field = new ERPFieldMapping();
			String PLMField = fieldList.get(i).getPLMField().replaceAll("(?i)End2\\$", "");
			if (PLMField.matches("(?i)End1\\$.*"))
			{
				PLMField = "";
			}
			field.setPLMField(PLMField);
			this.copyOtherFieldValue(field, fieldList.get(i));
			newFieldList.add(field);
		}
		return newFieldList;
	}

	private void copyOtherFieldValue(ERPFieldMapping newField, ERPFieldMapping oldField)
	{
		newField.setERPField(oldField.getERPField());
		newField.setDefaultValue(oldField.getDefaultValue());
		newField.setDescription(oldField.getDescription());
		newField.setDataType(oldField.getDataType());
		newField.setDateFormat(oldField.getDateFormat());
		newField.setKeyColumn(oldField.isKeyColumn());
		newField.setMandatory(oldField.isMandatory());
		newField.setMaxLength(oldField.getMaxLength());
		newField.setShowDefault(oldField.isShowDefault());
		newField.setTruncWhenLengthExceeds(oldField.isTruncWhenLengthExceeds());
		newField.setLengthExceedsOrientationisRight(oldField.isLengthExceedsOrientationisRight());
	}

	/**
	 * 读ERP数量成本价格(仅支持取价格成本数量都是同一个服务的，如：E10,Tiptop)
	 * （各ERP方案不同，需在子类重写）
	 * 
	 * @param list
	 * @param factoryId
	 * @param serviceConfig
	 * @param contentList
	 * @return
	 * @throws Exception
	 */
	public List<String> getReturnList(List<FoundationObject> list, List<String> factoryId, ERPServiceConfig serviceConfig, List<String> contentList) throws Exception
	{
		return null;
	}

	public String getReturnList(List<FoundationObject> list, String factory, T operation) throws Exception
	{
		return null;
	}

	/**
	 * @param returnXML
	 * @param contentList
	 * @return
	 * @throws Exception
	 */
	public List<FoundationObject> getReturnList(List<String> returnXML, List<String> contentList) throws Exception
	{
		return null;
	}

	protected String getPriceXML(List<FoundationObject> list, List<String> factoryId, Map<String, List<String>> map) throws Exception
	{
		return null;
	}

	/**
	 * 从缓存中读取一个category某一类的一个表对应的字段，如果缓存中没有则从xml文件中读取并做缓存。
	 * (仅用于抛分类)
	 * 
	 * @param category
	 * @param className
	 * @param tableName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ERPFieldMapping> getFieldFromMap(String category, String tableName) throws ServiceRequestException
	{
		if (this.PLMCFColumnMap.get(category) == null || this.PLMCFColumnMap.get(category).get(tableName) == null || this.PLMCFColumnMap.get(category).get(tableName).isEmpty())
		{
			Map<String, List<ERPFieldMapping>> tableFieldMap = new HashMap<String, List<ERPFieldMapping>>();
			this.getFieldListFromXML(category, tableFieldMap);
			if (SetUtils.isNullList(tableFieldMap.get(tableName)))
			{
				throw new IllegalArgumentException("no field mapping for " + category + "." + tableName + " in " + this.xmlPath);
			}
			if (this.shuldSortFields())
			{
				Collections.sort(tableFieldMap.get(tableName), this.fieldComparator);
			}
			this.PLMCFColumnMap.put(category, tableFieldMap);
		}
		return this.PLMCFColumnMap.get(category).get(tableName);
	}

	/**
	 * 从xml配置文件中解析得到一个category下的字段映射（此方法目前仅抛分类使用）。
	 * 
	 * @param usedClassInfo
	 * @param erpClass
	 * @param pdmColumnList
	 * @throws ServiceRequestException
	 */
	private void getFieldListFromXML(String category, Map<String, List<ERPFieldMapping>> fieldMap) throws ServiceRequestException
	{
		Element categoryElement = this.doc.getRootElement().getChild("category").getChild(category);
		if (categoryElement != null)
		{
			Iterator<Element> tableIt = categoryElement.getChildren("table").iterator();
			Element tableEle = null;
			while (tableIt.hasNext())
			{
				tableEle = tableIt.next();
				String tableName = tableEle.getAttributeValue("name");
				Iterator<Element> fieldIt = tableEle.getChildren("mapping").iterator();
				Element mappingEle = null;
				while (fieldIt.hasNext())
				{
					mappingEle = fieldIt.next();
					if (fieldMap.get(tableName) == null)
					{
						fieldMap.put(tableName, new ArrayList<ERPFieldMapping>());
					}
					Map<String, String> paramMap = this.getMapByElement(mappingEle);
					if (!this.existSameField(fieldMap.get(tableName), paramMap.get("ERPField")))
					{
						fieldMap.get(tableName).add(this.getFieldMapping(paramMap));
					}
				}
				mappingEle = null;
				fieldIt = null;
			}
		}
	}

	// /**
	// *
	// * @param baseObj
	// * 当前取数据的实例（当传结构关系时，baseObj是StructureObject实例，其它情况下则是FoundationObject）
	// * @param fieldList
	// * @param category
	// * @param operation
	// * @return
	// * @throws ServiceRequestException
	// */
	// protected Map<String, String> getObjectMapValue(Object baseObj, List<ERPFieldMapping> fieldList,
	// String category, T operation) throws ServiceRequestException
	// {
	// Map<String, String> returnMap = new LinkedHashMap<String, String>();
	// if (fieldList == null)
	// {
	// return returnMap;
	// }
	// for (int j = 0; j < fieldList.size(); j++)
	// {
	// ERPFieldMapping field = fieldList.get(j);
	// String dataValue = this.getFieldValue(baseObj, field, category, operation);
	// this.checkFieldValue(dataValue, field, baseObj);
	// returnMap.put(field.getERPField(), dataValue);
	// }
	// return returnMap;
	// }

	/**
	 * 仅用于抛分类信息到ERP
	 * 
	 * @param codeGuidList
	 * @throws ServiceRequestException
	 */
	public void getAllCFDataWithChildren(List<String> codeGuidList) throws ServiceRequestException
	{
		this.beforeProcessDataList();
		this.generateAllData(codeGuidList);
	}

	/**
	 * 仅用于抛分类信息到ERP
	 * 
	 * @param codeGuidList
	 * @throws ServiceRequestException
	 */
	protected void generateAllData(List<String> codeGuidList) throws ServiceRequestException
	{
		this.stubService = this.stub.getStubService();
		CodeItemInfo codeItem = null;
		List<CodeItemInfo> codeItemList = new ArrayList<CodeItemInfo>();
		for (String codeguid : codeGuidList)
		{
			codeItem = this.stubService.getEMM().getCodeItem(codeguid);
			if (codeItem != null)
			{
				codeItemList.add(codeItem);
			}
		}
		List<T> operationList = this.getOperationList(false);
		for (int k = 0; k < operationList.size(); k++)
		{
			T operation = operationList.get(k);
			String category = this.getOperationCategory(operation);
			if (StringUtils.isNullString(category))
			{
				throw new IllegalArgumentException("no category attribute for " + operationList.get(k).name() + " in " + this.xmlPath);
			}
			try
			{
				if (category.equals(ERP_CODEITEM))
				{
					this.getCFfiedData(codeItemList, operation);
				}
				else if (category.equals(ERP_FIELDGROUP))
				{
					this.getCFGroupData(codeItemList, operation);
				}
			}
			catch (InvalidDataException e)
			{
				this.addDataError(e);
				if (!this.isTraverseAllInstanceIgnoringError())
				{
					return;
				}
			}
		}
	}

	/**
	 * 获得分类字段数据（继承重写）
	 * 
	 * @param codeItemList
	 * @param operation
	 */
	protected void getCFfiedData(List<CodeItemInfo> codeItemList, T operation) throws ServiceRequestException
	{
	}

	/**
	 * 获得分类群组数据(继承重写)
	 * 
	 * @param codeItemList
	 * @param operation
	 * @throws ServiceRequestException
	 */
	protected void getCFGroupData(List<CodeItemInfo> codeItemList, T operation) throws ServiceRequestException
	{
	}

	/**
	 * e10需要将codeitem重新拼接成唯一nanme
	 * 仅用于抛分类到ERP
	 * 
	 * @param item
	 * @throws ServiceRequestException
	 */
	protected String reNameCode(CodeItemInfo item) throws ServiceRequestException
	{
		CodeItemInfo pCode = item;
		CodeObjectInfo mCode = null;
		StringBuffer nameBuffer = new StringBuffer();
		List<String> nameList = new ArrayList<String>();
		nameList.add(pCode.getName());
		while (pCode.getParentGuid() != null)
		{
			pCode = this.stub.getStubService().getEMM().getCodeItem(pCode.getParentGuid());
			nameList.add(pCode.getName());
		}
		mCode = this.stub.getStubService().getEMM().getCode(pCode.getCodeGuid());
		nameList.add(mCode.getName());
		for (int i = nameList.size() - 1; i > -1; i--)
		{
			nameBuffer.append(nameList.get(i));
		}
		return nameBuffer.toString();
	}

	/**
	 * 获得codeObject
	 * 
	 * @param codeItem
	 * @return
	 * @throws ServiceRequestException
	 */
	protected CodeObjectInfo getCode(CodeItemInfo codeItem) throws ServiceRequestException
	{
		CodeObjectInfo mCode = null;
		if (codeItem != null && codeItem.getCodeGuid() != null)
		{
			mCode = this.stub.getStubService().getEMM().getCode(codeItem.getCodeGuid());
		}
		return mCode;
	}

	public Document getCreateCFDataXML(ERPE10OperationEnum operation, int count, int j)
	{
		return null;
	}

	protected int getERPcnSize()
	{
		if (this.getParameter(null).getParamMap().get("ERPcnSize") != null)
		{
			int cnSize = Integer.valueOf(this.getParameter(null).getParamMap().get("ERPcnSize"));
			if (cnSize > 0)
			{
				return cnSize;
			}
			else
			{
				throw new IllegalArgumentException("the ERPcnSize should larger than 0!");
			}
		}
		return 1;
	}

	protected int getDataLength(String dataValue)
	{
		if (dataValue == null)
		{
			return 0;
		}
		int cnSize = getERPcnSize();
		int size = 0;
		if (cnSize > 1)
		{
			for (char c : dataValue.toCharArray())
			{
				if (CharUtils.isChinese(c))
				{
					size = size + cnSize;
				}
				else
				{
					size = size + 1;
				}
			}
			return size;
		}
		return dataValue.length();
	}

	/**
	 * 
	 * @param value
	 * @param lengthExceedsOrientationisRight
	 *            true:从右向左截取
	 * @param maxLength
	 * @return
	 */
	private String ExceedsData(Object value, boolean lengthExceedsOrientationisRight, int maxLength)
	{
		int cnSize = getERPcnSize();
		int size = 0;
		char[] charArray = value.toString().toCharArray();
		StringBuffer buffer = new StringBuffer();
		if (lengthExceedsOrientationisRight)
		{
			for (int i = charArray.length - 1; i >= 0; i--)
			{
				char c = charArray[i];
				if (CharUtils.isChinese(c))
				{
					size = size + cnSize;
				}
				else
				{
					size = size + 1;
				}
				if (size > maxLength)
				{
					break;
				}
				else
				{
					buffer.insert(0, c);
				}
			}
		}
		else
		{
			for (int i = 0; i < charArray.length; i++)
			{
				char c = charArray[i];
				if (CharUtils.isChinese(c))
				{
					size = size + cnSize;
				}
				else
				{
					size = size + 1;
				}
				if (size > maxLength)
				{
					break;
				}
				else
				{
					buffer.append(c);
				}
			}
		}
		return buffer.toString();
	}

	public String filter(String value)
	{
		if (value == null)
		{
			return null;
		}

		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
		{
			switch (content[i])
			{
			case 60: // '<'
				result.append("&lt;");
				break;

			case 62: // '>'
				result.append("&gt;");
				break;

			case 38: // '&'
				result.append("&amp;");
				break;

			case 34: // '"'
				result.append("&quot;");
				break;

			case 39: // '\''
				result.append("&#39;");
				break;

			default:
				result.append(content[i]);
				break;
			}
		}
		return result.toString();
	}

	/**
	 * 料件承认</br>
	 * 特殊处理（全抛/全不抛）
	 * 
	 * @param stampMap
	 * @param recordSets
	 * @throws ServiceRequestException
	 */
	public List<RecordSet> dealMAData(List<RecordSet> recordSets, boolean isExportAll) throws ServiceRequestException
	{
		List<RecordSet> result = new ArrayList<RecordSet>();
		Map<String, List<RecordSet>> groupedRecords = groupMARecordsByEnd1(recordSets);

		if (!SetUtils.isNullMap(groupedRecords))
		{
			for (String masterguid : groupedRecords.keySet())
			{
				List<RecordSet> recordList = groupedRecords.get(masterguid);
				if (!SetUtils.isNullList(recordList))
				{
					boolean isSend = false;
					for (RecordSet recordSet : recordList)
					{
						String itemkey = recordSet.getItemKey();
						String md5 = StringUtils.generateMd5(recordSet.toString());
						ERPtempData temp = new ERPtempData();
						temp.setITEMGUID(itemkey);
						temp.setSTAMP(md5);
						temp.setJOBGUID(stub.jobGUID);
						temp.setCATEGORY(ERP_MA);
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
							temp = this.getERPTempData(ERP_MA, itemkey);
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
					if (isSend || isExportAll)
					{
						result.addAll(recordList);
					}
				}

			}
		}
		return result;
	}

	/**
	 * 取替代</br>
	 * 因ERP的处理方式，除了T100可以只给异动的某一个取替代对象，其他的PLM需给出主、元件下的整组取替代对象</br>
	 * SM和WF在缓存的时候格式不同，此方法需重写
	 * 
	 * @param stampMap
	 *            取替代对象的masterGuid--stamp
	 * @param recordSets
	 * @throws ServiceRequestException
	 */
	public List<RecordSet> dealReplaceSubstituteData(List<RecordSet> recordSets, boolean isExportAll) throws ServiceRequestException
	{
		List<RecordSet> result = new ArrayList<RecordSet>();
		Map<String, List<RecordSet>> groupedRecords = groupRSRecords(recordSets);
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
						String itemkey = recordSet.getItemKey();
						String md5 = StringUtils.generateMd5(recordSet.toString());
						ERPtempData temp = new ERPtempData();
						temp.setITEMGUID(itemkey);
						temp.setSTAMP(md5);
						temp.setJOBGUID(stub.jobGUID);
						temp.setCATEGORY(ERP_REPLACESUBSTITUTE);
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
							temp = this.getERPTempData(ERP_REPLACESUBSTITUTE, itemkey);
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
					if (isSend || isExportAll)
					{
						result.addAll(recordList);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 将取替代对象按（主件Guid+元件Guid）分组
	 * 
	 * @param recordSets
	 * @return
	 * @throws ServiceRequestException
	 */
	protected Map<String, List<RecordSet>> groupRSRecords(List<RecordSet> recordSets) throws ServiceRequestException
	{
		Map<String, List<RecordSet>> recordMap = new HashMap<String, List<RecordSet>>();
		List<RecordSet> tempList = null;
		if (!SetUtils.isNullList(recordSets))
		{
			for (RecordSet record : recordSets)
			{
				String key = record.getGroupKey();
				tempList = recordMap.get(key);
				if (tempList == null)
				{
					tempList = new ArrayList<RecordSet>();
				}
				tempList.add(record);
				recordMap.put(key, tempList);
			}
		}
		return recordMap;
	}

	/**
	 * 根据承认书将缓存中的料件承认关系分组 </br>
	 * 队列合并可能会出现一个队列中有多个承认书的情况
	 * 
	 * @param recordSets
	 * @return
	 * @throws ServiceRequestException
	 */
	protected Map<String, List<RecordSet>> groupMARecordsByEnd1(List<RecordSet> recordSets) throws ServiceRequestException
	{
		Map<String, List<RecordSet>> recordMap = new HashMap<String, List<RecordSet>>();
		List<RecordSet> tempList = null;
		if (!SetUtils.isNullList(recordSets))
		{
			for (RecordSet record : recordSets)
			{
				// 承认书与料件的关联关系
				// 承认书与料件的关联关系
				String key = record.getGroupKey();
				tempList = recordMap.get(key);
				if (tempList == null)
				{
					tempList = new ArrayList<RecordSet>();
				}
				tempList.add(record);
				recordMap.put(key, tempList);
			}
		}
		return recordMap;
	}

	protected StructureObject getStructureObject(ObjectGuid objectGuid) throws ServiceRequestException
	{
		StructureObject object = null;
		if (objectGuid != null)
		{
			SearchCondition condition = SearchConditionFactory.createSearchConditionForStructure(objectGuid.getClassName());
			object = this.stub.getStubService().getBOAS().getStructureObject(objectGuid, condition);
		}
		return object;
	}

	/**
	 * 处理数据（除料件承认）
	 * 
	 * @param tempDataList
	 * @param recordMap
	 * @param stampMap
	 * @param isBOM
	 * @return
	 * @throws ServiceRequestException
	 */

	private List<RecordSet> dealData(String category, List<RecordSet> recordSets, boolean isExportAll) throws ServiceRequestException
	{
		List<RecordSet> result = new ArrayList<RecordSet>();
		for (RecordSet recordSet : recordSets)
		{
			String key = getSetKeyMguid(recordSet, false);
			String md5 = StringUtils.generateMd5(recordSet.toString());
			ERPtempData temp = new ERPtempData();
			temp.setITEMGUID(key);
			temp.setSTAMP(md5);
			temp.setJOBGUID(stub.jobGUID);
			temp.setCATEGORY(category);
			temp.put("TABLE", stub.tempTableName);
			boolean isSend = false;
			try
			{
				this.stub.getStubService().insertTempData(temp);
				setActtype(recordSet, true);
				isSend = true;
			}
			catch (Exception e)
			{
				setActtype(recordSet, false);
				temp = this.getERPTempData(category, key);
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
						isSend = false;
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
			if (isSend || isExportAll)
			{
				result.add(recordSet);
			}
		}
		return result;
	}

	/**
	 * 比对去重</br>
	 * 若schema中配置强制抛转所有数据，则不执行此方法
	 * 
	 * @throws ServiceRequestException
	 */
	public void compareWithDB(boolean isExportAll) throws ServiceRequestException
	{
		List<T> operationList = getOperationList(true);
		if (!SetUtils.isNullList(operationList))
		{
			for (T operation : operationList)
			{
				String category = getOperationCategory(operation);
				List<RecordSet> comparedData = new ArrayList<RecordSet>();
				List<RecordSet> recordSets = this.dataMap.get(category);
				if (!SetUtils.isNullList(recordSets))
				{
					if (ERP_MA.equals(category))
					{
						comparedData = dealMAData(recordSets, isExportAll);
					}
					else if (ERP_REPLACESUBSTITUTE.equals(category))
					{
						comparedData = dealReplaceSubstituteData(recordSets, isExportAll);
					}
					else
					{
						if (ERP_ITEM.equals(category))
						{
							comparedData = dealData(category, recordSets, isExportAll);
						}
						else
						{
							comparedData = dealData(category, recordSets, isExportAll);
						}
					}
					if (SetUtils.isNullList(comparedData))
					{
						this.dataMap.put(category, null);
					}
					else
					{
						this.dataMap.put(category, comparedData);
					}
				}
			}
		}
	}

	protected void setActtype(RecordSet recordSet, boolean b)
	{

	}

	private String getSetKeyMguid(RecordSet set, boolean isBOM) throws ServiceRequestException
	{
		ObjectGuid oguid = set.getObjectGuid();
		if (!isBOM)
		{
			return oguid.getMasterGuid();
		}
		ClassInfo info = stub.getStubService().getEMM().getClassByGuid(oguid.getClassGuid());
		if (info.hasInterface(ModelInterfaceEnum.IItem))
		{
			return oguid.getMasterGuid();
		}
		else if (info.hasInterface(ModelInterfaceEnum.IBOMView))
		{
			BOMView view = this.stub.getStubService().getBOMS().getBOMView(oguid);
			if (view != null)
			{
				return view.getEnd1ObjectGuid().getMasterGuid();
			}
		}
		return null;
	}

	/**
	 * 获得缓存中每个operation的数据是否为空</br>
	 * 与记录表比对前后各调用一次（为过滤原本没数据与已抛转数据无修改不抛这2种情况）
	 * 
	 * @param isafter
	 *            是否有记录表比对后
	 * @param map
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<String, Boolean> getOperationDataEmptyFlag(boolean isafter, Map<String, Boolean> map) throws ServiceRequestException
	{
		if (SetUtils.isNullMap(map))
		{
			map = new HashMap<String, Boolean>();
		}
		List<T> operationList = this.getOperationList(true);
		for (int k = 0; k < operationList.size(); k++)
		{
			T operation = operationList.get(k);
			String category = this.getOperationCategory(operation);
			if (StringUtils.isNullString(category))
			{
				throw new IllegalArgumentException("no category attribute for " + operationList.get(k).name() + " in " + this.xmlPath);
			}
			if (ERP_ITEM.equals(category))
			{
				if (this.dataMap.get(ERP_ITEM) == null || this.dataMap.get(ERP_ITEM).isEmpty())
				{
					setEmptyFlag(isafter, category, map);
				}
			}
			else if (ERP_BOM.equals(category))
			{
				if (this.dataMap.get(ERP_BOM) == null || this.dataMap.get(ERP_BOM).isEmpty())
				{
					setEmptyFlag(isafter, category, map);
				}
			}
			else if (ERP_REPLACESUBSTITUTE.equals(category))
			{
				if (this.dataMap.get(ERP_REPLACESUBSTITUTE) == null || this.dataMap.get(ERP_REPLACESUBSTITUTE).isEmpty())
				{
					setEmptyFlag(isafter, category, map);
				}
			}
			else if (ERP_MA.equals(category))
			{
				if (this.dataMap.get(ERP_MA) == null || this.dataMap.get(ERP_MA).isEmpty())
				{
					setEmptyFlag(isafter, category, map);
				}
			}
		}
		return map;
	}

	/**
	 * 原本就没数据的为true，比对后过滤的为false,有传输数据的category不缓存
	 * 
	 * @param isafter
	 * @param category
	 * @param map
	 */
	private void setEmptyFlag(boolean isafter, String category, Map<String, Boolean> map)
	{
		if (!isafter)
		{
			map.put(category, true);
		}
		else
		{
			if (map.get(category) == null)
			{
				map.put(category, false);
			}
		}
	}

	protected ERPtempData getERPTempData(String category, String guid) throws ServiceRequestException
	{
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("TABLE", this.stub.tempTableName);
		paraMap.put("CATEGORY", category);
		paraMap.put("ITEMGUID", guid);

		List<ERPtempData> list = stub.getStubService().listERPTempData(paraMap);
		if (SetUtils.isNullList(list))
		{
			return null;
		}
		return list.get(0);

	}
}

/**
 * 组合字段，fieldName & delegateFieldName.
 * fieldName: 当前使用的字段<br/>
 * delegateFieldName: 当fieldName是Object或Code类型时，delegateFieldName表示委托字段，否则delegateFieldName为null。<br/>
 * 
 * <p/>
 * 说明：<br/>
 * +表示两个字段合并<br/>
 * |表示前面的是一个Object或Code或Classification字段，后面的是Object,Code,Classification里的字段， 如果是Code,Classification的话则只允许Code, title两种情况
 * <br/>
 * end1$XX, end2$XX开关表示取end1或end2中的XX字段<br/>
 * 可按上面规则组合出非常复杂的字段表达，如type|code+end1$type|title+end2$name 表示取type字段的code值 加上 end1的type字段的title值 加上
 * end2的name字段值，这里就是三个JointField对象组合
 **/
class JointField
{
	public static final String	MERGE_SIGN		= "+";
	public static final String	DELEGATE_SIGN	= "|";

	private final String		fieldName;
	private final String		delegateFieldName;

	public JointField(String fieldName, String delegateFieldName)
	{
		this.fieldName = fieldName;
		this.delegateFieldName = delegateFieldName;
	}

	public String getFieldName()
	{
		return this.fieldName;
	}

	public String getDelegateFieldName()
	{
		return this.delegateFieldName;
	}

	public boolean isEnd1Field()
	{
		return this.getFieldName().matches("((?i)end1\\$).*");
	}

	public boolean isEnd2Field()
	{
		return this.getFieldName().matches("((?i)end2\\$).*");
	}

	/**
	 * 去除End1$, End2$前缀符后的字段名
	 * 
	 * @return
	 */
	public String getClearFieldName()
	{
		if (this.isEnd1Field() || this.isEnd2Field())
		{
			return this.getFieldName().substring(5);
		}
		return this.getFieldName();
	}

	public String getName()
	{
		if (this.delegateFieldName != null)
		{
			return this.fieldName + DELEGATE_SIGN + this.delegateFieldName;
		}
		else
		{
			return this.fieldName;
		}
	}

	@Override
	public String toString()
	{
		return this.getName();
	}

	public static List<JointField> getFiels(String name)
	{
		List<JointField> fieldList = new ArrayList<JointField>();
		// + |是正则中的特殊字符，要转义
		String[] jointArr = name.split("\\" + MERGE_SIGN);
		for (String f : jointArr)
		{
			String[] sepArr = f.split("\\" + DELEGATE_SIGN);
			// sepArr.length可能大于2，如一个Object类型字段，同时再取这个Object对象的另一个Code类型的字段，配置是这样的：objectField|stock_unit|title
			// 这里把stock_unit|title整体做为一个delegateField
			if (sepArr.length >= 2)
			{
				fieldList.add(new JointField(sepArr[0], f.substring(sepArr[0].length() + 1)));
			}
			else
			{
				fieldList.add(new JointField(sepArr[0], null));
			}
		}
		return fieldList;
	}
}

/**
 * 表数据<br/>
 * 每一个“表”对应的数据。如单头对应一个表，单身对应一个表。一般单头表里的数据只有一条，而单身表里的数据则有多条 <br/>
 * 这个类一般size==1,但是在BOM多单身的时候，就会size>1
 * 
 * @see java.util.LinkedList
 * @see java.util.Map
 * @author chega
 * 
 *         2012-12-27下午2:34:26
 * 
 */
class TableRecordData extends LinkedList<Map<String, String>>
{
	public final static TableRecordData	EMPTY_TABLE_RECORD	= new TableRecordData();

	public TableRecordData(Map<String, String> record)
	{
		super();
		if (record != null && !record.isEmpty())
		{
			this.add(record);
		}
	}

	public TableRecordData()
	{
		this(null);
	}

	@Override
	public boolean add(Map<String, String> e)
	{
		if (e == null || e.isEmpty())
		{
			return false;
		}
		if (!this.exist(e))
		{
			return super.add(e);
		}
		return false;
	}

	/**
	 * 在生成BOM数据的时候，多个BOMStructure对象在循环的时候，每个BOMStructure对象都会生成相同的单头，
	 * 而传输的时候只一个单头即可。因此这里要判断相同的单头是否存在<br/>
	 * 这个方法只是在判断BOM多单身时候可能返回true，其它情况下永远返回false
	 * 
	 * @param e
	 * @return
	 */
	public boolean exist(Map<String, String> e)
	{
		for (Map<String, String> r : this)
		{
			if (e.equals(r))
			{
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * @param compoundField
	 *            是用逗号分隔的多个字段(ERPField)，返回值也用逗号分隔 <br/>
	 * <br/>
	 *            这里只取第一个map
	 * @return
	 */
	public String getCompoundFieldValue(String compoundField)
	{
		String[] fieldArr = compoundField.split("\\s*,\\s*");
		String compoundValue = "";
		for (String field : fieldArr)
		{
			compoundValue += "," + this.get(0).get(field);
		}
		return compoundValue.equals("") ? compoundValue : compoundValue.substring(1);
	}

	/**
	 * 是否存在相同组合值。该方法用于E10中判断primaryKey的唯一性
	 * 
	 * @param compoundField
	 * @return
	 */
	public boolean existDuplicatedValueForFields(String compoundField)
	{
		int size = this.size();
		Set<String> set = new HashSet<String>();
		set.add(this.getCompoundFieldValue(compoundField));
		boolean exist = set.size() == size;
		set.clear();
		return exist;
	}
}

/**
 * 多条“表数据”在一起就是一条完整的记录(RecordSet)。 这是ERP能识别的最小数据单元 ,如一条物料，一个BOM结构，一个取代结构<br/>
 * 一个RecordSet中同tableName的TableRecordData只能有一个
 * 
 * @author chega
 * 
 *         2012-12-27下午2:39:26
 * @see java.util.Map
 * @see TableRecordData
 */
class RecordSet extends HashMap<String, TableRecordData>
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -504707286884283318L;

	/**
	 * 如果是料件则是料件的objectGuid,如果是bomview则是bomview的objectGuid，如果是取替代则取end1的ObjectGuid, 料件承认则是承认书的ObjectGuid
	 */
	private ObjectGuid			objectGuid			= null;

	private String				groupKey			= null;

	private String				itemKey				= null;

	public RecordSet()
	{
	}

	public RecordSet(ObjectGuid obejctGuid)
	{
		this.objectGuid = obejctGuid;
	}

	public static RecordSet emptySet(ObjectGuid objectGuid)
	{
		return new RecordSet(objectGuid);
	}

	public ObjectGuid getObjectGuid()
	{
		return this.objectGuid;
	}

	public String getGroupKey()
	{
		return groupKey;
	}

	public void setGroupKey(String groupKey)
	{
		this.groupKey = groupKey;
	}

	public String getItemKey()
	{
		return itemKey;
	}

	public void setItemKey(String itemKey)
	{
		this.itemKey = itemKey;
	}

	/**
	 * 根据表名取得数据
	 * 
	 * @param tableName
	 * @param stub
	 *            用于取得当前传输时的dataKey和XMLPackageNo
	 * @return
	 * @see TableRecordData
	 */
	public TableRecordData getTableData(String tableName, ERPTransferStub<? extends Enum<?>> stub)
	{
		TableRecordData record = this.get(tableName);
		if (record == null)
		{
			return TableRecordData.EMPTY_TABLE_RECORD;
		}
		return this.replaceBuildinField(record, stub);
	}

	/**
	 * 替换掉 ${DataKey} 和 ${XMLPackageNo}这两个内置字段。
	 * 
	 * @param record
	 * @return
	 */
	private TableRecordData replaceBuildinField(TableRecordData record, ERPTransferStub<? extends Enum<?>> stub)
	{
		if (stub != null && stub.currentOpeIdx != -1 && stub.currentXMLPackIdx != 0)
		{
			for (Map<String, String> map : record)
			{
				Iterator<String> keyIt = map.keySet().iterator();
				String key = null;
				while (keyIt.hasNext())
				{
					key = keyIt.next();
					if (map.get(key).contains(IntegrateERP.RESERVED_DATAKEY))
					{
						map.put(key, map.get(key).replace(IntegrateERP.RESERVED_DATAKEY, stub.getDataKey()));
					}
					if (map.get(key).contains(IntegrateERP.RESERVED_XMLPACKAGENO))
					{
						map.put(key, map.get(key).replace(IntegrateERP.RESERVED_XMLPACKAGENO, stub.getXMLPackNo()));
					}
				}
			}
		}
		return record;
	}

	@Override
	public TableRecordData put(String tableName, TableRecordData tableRecord)
	{
		if (tableRecord.isEmpty())
		{
			return null;
		}
		if (this.get(tableName) != null)
		{
			throw new IllegalArgumentException("can't add TableRecordData with same tableName to RecordSet. tableName: " + tableName);
		}
		return super.put(tableName, tableRecord);
	}
}

class ERPFieldComparator implements Comparator<ERPFieldMapping>
{
	@Override
	public int compare(ERPFieldMapping o1, ERPFieldMapping o2)
	{
		return o1.getERPField().compareTo(o2.getERPField());
	}
}

/**
 * 当数据不合法时抛出此异常，此异常只用于数据不合法的时候。不能用于别的地方
 * 
 * @author chega
 *         2013-5-22
 */
class InvalidDataException extends RuntimeException
{
	InvalidDataException(String msg)
	{
		super(msg);
	}
}

/**
 * 放置ERPFieldMapping的容器<br/>
 * 为创建TableRecord准备ERPFieldMapping<br/>
 * 不允许表名重复
 * 
 * @author zhoux
 * 
 */
class ERPFieldMappingContainer
{
	/**
	 * 表-ERP字段配置容器<br/>
	 */
	private Map<String, List<ERPFieldMapping>>	fieldMappingMap	= new HashMap<String, List<ERPFieldMapping>>();
	/**
	 * 表-类Set容器<br/>
	 * 用于检查ERPFieldMapping是否需要添加到容器，整合同表散布在不同类下的字段配置
	 */
	protected Map<String, Set<String>>			classCheckMap	= new HashMap<String, Set<String>>();
	/**
	 * 表-字段Set容器<br/>
	 * 用于检查ERPFieldMapping是否在表下已存在
	 */
	protected Map<String, Set<String>>			fieldCheckMap	= new HashMap<String, Set<String>>();

	/**
	 * 按表名分组，添加类的ERP字段配置到缓存，用于中间表
	 * 
	 * @param tableName
	 *            表名(不可为空)
	 * @param className
	 *            类名(可为空，同类名只有第一次添加成功)
	 * @param erpFieldMappingList
	 */
	public void put(String tableName, String className, List<ERPFieldMapping> erpFieldMappingList)
	{
		if (StringUtils.isNullString(tableName))
		{
			throw new IllegalArgumentException("tableName can not empty");
		}
		className = StringUtils.convertNULLtoString(className);
		Set<String> classNameSet = this.classCheckMap.get(tableName);
		if (classNameSet == null)
		{
			classNameSet = new HashSet<String>();
			this.classCheckMap.put(tableName, classNameSet);
		}
		if (classNameSet.add(className.toLowerCase()))
		{
			List<ERPFieldMapping> tableFieldList = this.fieldMappingMap.get(tableName);
			if (tableFieldList == null)
			{
				tableFieldList = new ArrayList<ERPFieldMapping>();
				this.fieldMappingMap.put(tableName, tableFieldList);
			}
			if (!SetUtils.isNullList(erpFieldMappingList))
			{
				Set<String> fieldNameSet = this.fieldCheckMap.get(tableName);
				if (fieldNameSet == null)
				{
					fieldNameSet = new HashSet<String>();
					this.fieldCheckMap.put(tableName, fieldNameSet);
				}
				for (ERPFieldMapping erpFieldMapping : erpFieldMappingList)
				{
					if (fieldNameSet.add(erpFieldMapping.getERPField()))
					{
						tableFieldList.add(erpFieldMapping);
					}
				}
			}
		}
	}

	/**
	 * 得到表的字段配置(用于中间表，不能生成数据源时使用)
	 * 
	 * @param tableName
	 * @return
	 */
	public List<ERPFieldMapping> get(String tableName)
	{
		return this.fieldMappingMap.get(tableName);
	}

	/**
	 * 清空容器
	 */
	public void clear()
	{
		this.fieldMappingMap.clear();
		this.classCheckMap.clear();
		this.fieldCheckMap.clear();
	}

}