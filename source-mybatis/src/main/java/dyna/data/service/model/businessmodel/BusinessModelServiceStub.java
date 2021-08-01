package dyna.data.service.model.businessmodel;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.bmbo.BusinessModel;
import dyna.common.bean.model.bmbo.BusinessObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.cache.AbstractCacheInfo;
import dyna.common.cache.CacheRefreshListener;
import dyna.common.cache.DynaCacheObserver;
import dyna.common.cache.DynaObserverMediator;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeBoInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.BusinessModelType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.model.DataCacheServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public class BusinessModelServiceStub extends DataCacheServiceStub<BusinessModelServiceImpl>
{
	private static Map<String, BusinessModel>	MODEL_BM_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());
	private static Map<String, String>			MODEL_BM_NAME_MAP	= Collections.synchronizedMap(new HashMap<>());

	private static Map<String, BusinessObject>	MODEL_BO_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());

	private AbstractCacheInfo					cacheInfo			= null;

	public BusinessModelServiceStub(DataServerContext context, BusinessModelServiceImpl service)
	{
		super(context, service);
		this.cacheInfo = new BusinessModelCacheInfo();
		this.cacheInfo.register();
	}

	@Override
	public void loadModel() throws ServiceRequestException
	{
		this.loadAllBMInfo();

		this.processSharedModel();
	}

	public void checkAndClearBusinessObject() throws ServiceRequestException
	{
		String tranid = StringUtils.generateRandomUID(32);
		try
		{
			cacheInfo.setRemoveErrData(true);
//			DataServer.getTransactionService().startTransaction(tranid);

			List<BOInfo> boList = DataServer.getSystemDataService().listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(BOInfo.PARENTBOGUID, null));
			if (!SetUtils.isNullList(boList))
			{
				for (BOInfo boInfo : boList)
				{
					this.doCheckAndClearContainsChildBO(boInfo);
				}
			}
//			DataServer.getTransactionService().commitTransaction(tranid);

		}
		catch (DynaDataException e)
		{
			cacheInfo.setRemoveErrData(false);
//			DataServer.getTransactionService().rollbackTransaction(tranid);
			throw e;
		}
		catch (Exception e)
		{
			cacheInfo.setRemoveErrData(false);
//			DataServer.getTransactionService().rollbackTransaction(tranid);
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
		cacheInfo.setRemoveErrData(false);
	}

	private void doCheckAndClearContainsChildBO(BOInfo parentBo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (StringUtils.isGuid(parentBo.getClassGuid()))
		{
			ClassInfo classInfo = sds.get(ClassInfo.class, parentBo.getClassGuid());
			if (classInfo == null)
			{
				DynaLogger.info("BO delete: " + parentBo);
				sds.delete(parentBo);
				this.deleteChildBOInfo(parentBo.getBMGuid(), parentBo.getGuid());
				return;
			}
		}

		List<BOInfo> childBoList = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(BOInfo.PARENTBOGUID, parentBo.getGuid()));
		if (!SetUtils.isNullList(childBoList))
		{
			for (BOInfo boInfo : childBoList)
			{
				this.doCheckAndClearContainsChildBO(boInfo);
			}
		}
	}

	private void deleteChildBOInfo(String bmGuid, String parentBOGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			UpperKeyMap param = new UpperKeyMap();
			param.put(BOInfo.BMGUID, bmGuid);
			param.put(BOInfo.PARENTBOGUID, parentBOGuid);

			// 子阶全部删除
			List<BOInfo> childBMBOList = sds.listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(param));
			if (!SetUtils.isNullList(childBMBOList))
			{
				for (BOInfo boInfo : childBMBOList)
				{
					this.deleteChildBOInfo(bmGuid, boInfo.getGuid());
					DynaLogger.info("BO delete: " + boInfo);
					sds.delete(boInfo);
					// 工作流模板业务范围根据boguid记录的同步删除
					sds.deleteFromCache(WorkflowTemplateScopeBoInfo.class, new FieldValueEqualsFilter<>(WorkflowTemplateScopeBoInfo.BOGUID, boInfo.getGuid()));
				}
			}

		}
		catch (DynaDataException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}

	}

	public BusinessModel getBusinessModelByName(String modelName)
	{
		String bmGuid = MODEL_BM_NAME_MAP.get(modelName);
		BusinessModel bmInfo = MODEL_BM_GUID_MAP.get(bmGuid) == null ? null : MODEL_BM_GUID_MAP.get(bmGuid);
		return bmInfo;
	}

	public BusinessModel getBusinessModelByGuid(String modelGuid)
	{
		return MODEL_BM_GUID_MAP.get(modelGuid) == null ? null : MODEL_BM_GUID_MAP.get(modelGuid);
	}

	public BusinessObject getBusinessObjectByName(String bmName, String boName)
	{
		String bmGuid = MODEL_BM_NAME_MAP.get(bmName);
		BusinessModel model = MODEL_BM_GUID_MAP.get(bmGuid);
		return model.findBusinessObject(boName);
	}

	public BusinessObject getBusinessObjectByGuid(String boguid)
	{
		BusinessObject boInfo = MODEL_BO_GUID_MAP.get(boguid) == null ? null : MODEL_BO_GUID_MAP.get(boguid);
		return boInfo;
	}

	public List<BusinessModel> listBusinessModel()
	{
		List<BusinessModel> list = new ArrayList<>();
		MODEL_BM_GUID_MAP.forEach((guid, model) -> list.add(model));
		if (!SetUtils.isNullList(list))
		{
			Collections.sort(list, new Comparator<BusinessModel>()
			{

				@Override
				public int compare(BusinessModel o1, BusinessModel o2)
				{
					return o1.getSequence() - o2.getSequence();
				}
			});
		}
		return list;
	}

	public String getBOClassName(String boGuid)
	{
		for (String bmName : MODEL_BM_NAME_MAP.keySet())
		{
			String bmGuid = MODEL_BM_NAME_MAP.get(bmName);
			if (bmGuid != null)
			{
				if (MODEL_BM_GUID_MAP.get(bmGuid) != null)
				{
					List<BusinessObject> businessObjectList = MODEL_BM_GUID_MAP.get(bmGuid).getBusinessObjectList();
					List<BusinessObject> matchedList = businessObjectList.stream().filter(bo -> bo.getBoInfo().getGuid().equalsIgnoreCase(boGuid)).collect(Collectors.toList());
					return SetUtils.isNullList(matchedList) ? null : matchedList.get(0).getBoInfo().getClassName();
				}
			}
		}
		return null;
	}

	public BusinessModel getSharedBusinessModel()
	{
		return this.getBusinessModelByName(BusinessModelType.SHARED_MODEL.getName());
	}

	public List<BOInfo> listAllBOInfoInShareModel()
	{
		BusinessModel bm = this.getSharedBusinessModel();
		List<BusinessObject> businessObjectList = bm.getBusinessObjectList();
		if (businessObjectList != null)
		{
			List<BOInfo> boInfoList = new ArrayList<>();
			businessObjectList.forEach(bo -> listBOInfo(boInfoList, bo));
			return boInfoList;
		}
		return null;
	}

	public List<BOInfo> listAllBOInfoInBusinessModel(String bmGuid)
	{
		BusinessModel bm = this.getBusinessModelByGuid(bmGuid);
		if (bm == null)
		{
			return null;
		}
		List<BusinessObject> businessObjectList = bm.getBusinessObjectList();
		if (businessObjectList != null)
		{
			List<BOInfo> boInfoList = new ArrayList<>();
			businessObjectList.forEach(bo -> listBOInfo(boInfoList, bo));
			return boInfoList;
		}
		return null;
	}

	private void listBOInfo(List<BOInfo> boInfoList, BusinessObject businessObject)
	{
		boInfoList.add(businessObject.getBoInfo());
		if (businessObject.getChildList() != null)
		{
			businessObject.getChildList().forEach(bo -> listBOInfo(boInfoList, bo));
		}
	}

	private void loadAllBMInfo() throws ServiceRequestException
	{
		List<BMInfo> bmInfoList = DataServer.getSystemDataService().listFromCache(BMInfo.class, null);
		if (!SetUtils.isNullList(bmInfoList))
		{
			for (BMInfo bmInfo : bmInfoList)
			{
				BusinessModel businessModel = new BusinessModel(bmInfo);
				MODEL_BM_GUID_MAP.put(bmInfo.getGuid(), businessModel);
				MODEL_BM_NAME_MAP.put(bmInfo.getName(), bmInfo.getGuid());

				this.loadBOInfo(businessModel);
			}

			if (!SetUtils.isNullMap(MODEL_BO_GUID_MAP))
			{
				for (BusinessObject businessObject : MODEL_BO_GUID_MAP.values())
				{
					if (businessObject.getParentBOGuid() != null)
					{
						BusinessObject parentObject = MODEL_BO_GUID_MAP.get(businessObject.getParentBOGuid());
						businessObject.setParent(parentObject.getName());
						parentObject.addChild(businessObject);
					}
					else
					{
						MODEL_BM_GUID_MAP.get(businessObject.getBMGuid()).addBusinessObject(businessObject);
					}
				}
			}
		}
	}

	private void loadBOInfo(BusinessModel bmModel) throws ServiceRequestException
	{
		List<BOInfo> boInfoList = DataServer.getSystemDataService().listFromCache(BOInfo.class, new FieldValueEqualsFilter<>(BOInfo.BMGUID, bmModel.getGuid()));
		boInfoList.forEach(boInfo -> {

			BusinessObject businessObject = buildBusinessObject(bmModel, boInfo);
			MODEL_BO_GUID_MAP.put(boInfo.getGuid(), businessObject);
		});
	}

	private BusinessObject buildBusinessObject(BusinessModel bmModel, BOInfo boInfo)
	{
		BusinessObject businessObject = new BusinessObject(boInfo);

		String parentBOGuid = boInfo.getParentBOGuid();
		if (parentBOGuid == null)
		{
			parentBOGuid = "root";
		}
		businessObject.getBoInfo().setBmName(bmModel.getName());

		if (StringUtils.isGuid(businessObject.getClassGuid()))
		{
			ClassInfo classInfo = DataServer.getSystemDataService().get(ClassInfo.class, businessObject.getClassGuid());
			if (classInfo != null)
			{
				businessObject.setClassInfo(classInfo);
				businessObject.getBoInfo().setClassName(classInfo == null ? null : classInfo.getName());
				businessObject.getBoInfo().setAbstract(classInfo.isAbstract());
				businessObject.getBoInfo().setCreateTable(classInfo.isCreateTable());
			}
		}
		if (StringUtils.isGuid(businessObject.getClassificationGuid()))
		{
			businessObject.setClassificationItem(DataServer.getSystemDataService().get(CodeItemInfo.class, businessObject.getClassificationGuid()));
		}

		return businessObject;
	}

	/**
	 * 业务对象如果是共享的，需要从共享模型中取得实际的业务对象
	 */
	private void processSharedModel()
	{
		// 取得共享模型
		BusinessModel sharedModel = this.getSharedBusinessModel();

		Map<String, BusinessObject> sharedBusinessObjectMap = new HashMap<>();

		if (sharedModel != null)
		{
			List<BOInfo> shareBOList = this.listAllBOInfoInBusinessModel(sharedModel.getGuid());
			if (!SetUtils.isNullList(shareBOList))
			{
				for (BOInfo info : shareBOList)
				{
					BusinessObject businessObject = MODEL_BO_GUID_MAP.get(info.getGuid());
					if (businessObject != null)
					{
						businessObject.setShared(false);
						sharedBusinessObjectMap.put(businessObject.getName(), businessObject);
					}
				}
			}
		}

		for (BusinessObject businessObject : MODEL_BO_GUID_MAP.values())
		{
			if (!sharedModel.getGuid().equalsIgnoreCase(businessObject.getBMGuid()))
			{
				businessObject.setShared(false);
				if (sharedBusinessObjectMap.containsKey(businessObject.getName()))
				{
					businessObject.setShared(true);
				}
			}
		}
	}

	class BusinessModelCacheInfo extends AbstractCacheInfo
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -1630803643789189094L;

		@Override
		public void register()
		{
			DynaObserverMediator mediator = DynaObserverMediator.getInstance();
			mediator.register(BMInfo.class, new DynaCacheObserver<BMInfo>(this, new CacheRefreshListener<BMInfo>()));
			mediator.register(BOInfo.class, new DynaCacheObserver<BOInfo>(this, new CacheRefreshListener<BOInfo>()));
		}

		@Override
		public <T extends SystemObject> void addToCache(T data) throws ServiceRequestException
		{
			if (data instanceof BMInfo)
			{
				BMInfo bmInfo = (BMInfo) data;
				MODEL_BM_GUID_MAP.put(bmInfo.getGuid(), new BusinessModel(bmInfo));
				MODEL_BM_NAME_MAP.put(bmInfo.getName(), bmInfo.getGuid());
			}
			else if (data instanceof BOInfo)
			{
				BOInfo boInfo = (BOInfo) data;
				BusinessModel bmModel = MODEL_BM_GUID_MAP.get(boInfo.getBMGuid());
				BusinessObject boModle = buildBusinessObject(bmModel, boInfo);
				if (boInfo.getParentBOGuid() == null)
				{
					bmModel.addBusinessObject(boModle);
				}
				else
				{
					BusinessObject parentBO = MODEL_BO_GUID_MAP.get(boInfo.getParentBOGuid());
					boModle.setParent(parentBO.getName());
					parentBO.addChild(boModle);
				}

				MODEL_BO_GUID_MAP.put(boInfo.getGuid(), boModle);
				processSharedModel();
			}
		}

		@Override
		public <T extends SystemObject> void removeFromCache(T data) throws ServiceRequestException
		{
			if (data instanceof BMInfo)
			{
				BMInfo bmInfo = (BMInfo) data;
				MODEL_BM_GUID_MAP.remove(bmInfo.getGuid());
				MODEL_BM_NAME_MAP.remove(bmInfo.getName());
			}
			else if (data instanceof BOInfo)
			{
				BOInfo boInfo = (BOInfo) data;
				MODEL_BO_GUID_MAP.remove(boInfo.getGuid());
				BusinessModel bmModel = MODEL_BM_GUID_MAP.get(boInfo.getBMGuid());
				if (bmModel != null)
				{
					if (boInfo.getParentBOGuid() == null)
					{
						bmModel.removeBusinessObject(boInfo.getName());
					}
					else
					{
						BusinessObject parentBO = MODEL_BO_GUID_MAP.get(boInfo.getParentBOGuid());
						if (parentBO != null)
						{
							parentBO.removeChild(new BusinessObject(boInfo));
						}
					}
				}
				processSharedModel();
			}
		}

		@Override
		public <T extends SystemObject> void updateToCache(T data) throws ServiceRequestException
		{
			if (data instanceof BMInfo)
			{
				BMInfo bmInfo = (BMInfo) data;
				MODEL_BM_GUID_MAP.get(bmInfo.getGuid()).getBmInfo().putAll(bmInfo);
			}
			else if (data instanceof BOInfo)
			{
				BOInfo boInfo = (BOInfo) data;
				ClassInfo classInfo = DataServer.getSystemDataService().get(ClassInfo.class, boInfo.getClassGuid());
				boInfo.setClassName(classInfo == null ? "" : classInfo.getName());
				MODEL_BO_GUID_MAP.get(boInfo.getGuid()).getBoInfo().putAll(boInfo);
				MODEL_BO_GUID_MAP.get(boInfo.getGuid()).sortChild();
				MODEL_BM_GUID_MAP.get(boInfo.getBMGuid()).sortBusinessObject();
			}
		}
	}
}
