/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 交付物管理
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.Deliverable;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author WangLHB
 *         交付物管理
 */
public class DeliveryStub extends AbstractServiceStub<PPMSImpl>
{

	protected DeliveryStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	private void decodeDeliverableItem(DeliverableItem item)
	{
		try
		{
			User user = this.stubService.getAAS().getUser(item.getCreateUserGuid());
			if (user != null)
			{
				item.put(SystemObject.CREATE_USER_NAME, user.getUserName());
			}
		}
		catch (Exception e)
		{
			item.setClassification(null);
		}
		if (StringUtils.isNullString(item.getClassGuid()) == false)
		{
			try
			{
				String className = this.stubService.getEMM().getClassByGuid(item.getClassGuid()).getName();
				item.setBoTitle(this.stubService.getEMM().getCurrentBoInfoByClassName(className).getTitle());
			}
			catch (Exception e)
			{
				item.setClassGuid(null);
			}
			try
			{
				item.setClsfiTitle(this.stubService.getEMM().getCodeItem(item.getClassification()).getTitle());
			}
			catch (Exception e)
			{
				item.setClassification(null);
			}
		}
	}

	private void decodeDeliverable(Deliverable item) throws ServiceRequestException
	{
		User user = this.stubService.getAAS().getUser((String) item.get("CREATEUSER$"));
		if (user != null)
		{
			item.put("COMMITUSER", user.getUserName());
		}
		FoundationObject objectByGuid = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(item.getInstanceClassGuid(), null, item.getInstanceGuid(), null));
		if (objectByGuid != null)
		{
			item.put("BOTILE", objectByGuid.get("BOTITLE$"));
			item.setName(objectByGuid.getFullName());
			item.put("CREATEUSERNAME", objectByGuid.getCreateUser());
			item.put("UPDATETIME", objectByGuid.getUpdateTime());
			item.put(SystemClassFieldEnum.STATUS.getName(), objectByGuid.getStatus().getId());
			item.put(SystemClassFieldEnum.FILEGUID.getName(), objectByGuid.getFileGuid());
			item.put(SystemClassFieldEnum.FILETYPE.getName(), objectByGuid.getFileName());
			item.put("CLSFITILE", objectByGuid.getClassification());
			item.put("ICON$", objectByGuid.getIcon());
		}
	}

	/**
	 * 取得任务下的所有的交付项
	 *
	 * @throws ServiceRequestException
	 */
	public List<DeliverableItem> listDeliveryItem(String taskGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DeliverableItem.TASKGUID, taskGuid);
			List<DeliverableItem> list = sds.query(DeliverableItem.class, filter);
			if (list != null)
			{
				for (DeliverableItem item : list)
				{
					this.decodeDeliverableItem(item);
				}
			}
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 取得项目下的所有的交付项
	 *
	 * @throws ServiceRequestException
	 */
	public List<DeliverableItem> listDeliveryItemByProject(String projectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(DeliverableItem.PROJECTGUID, projectGuid);
			List<DeliverableItem> list = sds.query(DeliverableItem.class, filter);
			if (list != null)
			{
				for (DeliverableItem item : list)
				{
					this.decodeDeliverableItem(item);
				}
			}
			return list;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 根据任务交付项取得交付物
	 *
	 * @throws ServiceRequestException
	 */
	public List<Deliverable> listDeliveryByItem(String deliveryItemGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<Deliverable> finalList = new ArrayList<Deliverable>();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Deliverable.DELIVERABLEITEMGUID, deliveryItemGuid);
			List<Deliverable> classList = sds.query(Deliverable.class, filter, "selectClassOfDeliverable");
			if (!SetUtils.isNullList(classList))
			{
				for (Deliverable d : classList)
				{
					filter.put(Deliverable.INSTANCECLASSGUID, d.getInstanceClassGuid());
					filter.put("CLASSGUID", d.getInstanceClassGuid());
					List<Deliverable> list = sds.query(Deliverable.class, filter);
					if (!SetUtils.isNullList(list))
					{
						finalList.addAll(list);
					}
				}
			}

			for (Deliverable item : finalList)
			{
				this.decodeDeliverable(item);
			}
			return finalList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<Deliverable> listAllDeliveryByProject(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<Deliverable> finalList = new ArrayList<Deliverable>();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(Deliverable.PROJECTGUID, projectObjectGuid.getGuid());
			List<Deliverable> classList = sds.query(Deliverable.class, filter, "selectClassOfDeliverable");
			if (!SetUtils.isNullList(classList))
			{
				for (Deliverable d : classList)
				{
					filter.put(Deliverable.INSTANCECLASSGUID, d.getInstanceClassGuid());
					filter.put("CLASSGUID", d.getInstanceClassGuid());
					List<Deliverable> list = sds.query(Deliverable.class, filter);
					if (!SetUtils.isNullList(list))
					{
						finalList.addAll(list);
					}
				}
			}

			for (Deliverable item : finalList)
			{
				this.decodeDeliverable(item);
			}
			return finalList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 保存交付项
	 *
	 * @param deliveryItem
	 * @return 交付项
	 * @throws ServiceRequestException
	 */
	public DeliverableItem saveDeliveryItem(DeliverableItem deliveryItem) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String deliveryItemGuid = deliveryItem.getGuid();
			String operatorGuid = this.stubService.getOperatorGuid();
			boolean isCreate = false;
			if (StringUtils.isNullString(deliveryItemGuid))
			{
				isCreate = true;
				deliveryItem.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			String ret = sds.save(deliveryItem);

			if (isCreate)
			{
				deliveryItemGuid = ret;
			}

			DeliverableItem item = this.getDeliveryItem(deliveryItemGuid);
			if (item != null)
			{
				item.setTaskName(deliveryItem.getTaskName());
			}
			return item;
		}
		catch (DynaDataException e)
		{
			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_UNIQUE)
			{
				e.setDataExceptionEnum(DataExceptionEnum.DS_DELIVERABLEITEM_MULTI_ERROR);
			}
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private DeliverableItem getDeliveryItem(String deliveryItemGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, deliveryItemGuid);
			DeliverableItem item = sds.queryObject(DeliverableItem.class, filter);
			if (item != null)
			{
				this.decodeDeliverableItem(item);
			}
			return item;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 提交交付物
	 *
	 * @param delivery
	 * @throws ServiceRequestException
	 */
	public void commitDelivery(Deliverable delivery) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			this.checkDeliveryClassification(delivery);
			String deliveryGuid = delivery.getGuid();
			String operatorGuid = this.stubService.getOperatorGuid();
			boolean isCreate = false;
			if (StringUtils.isNullString(deliveryGuid))
			{
				isCreate = true;
				delivery.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			String ret = sds.save(delivery);

			if (isCreate)
			{
				deliveryGuid = ret;
			}
		}
		catch (DynaDataException e)
		{
			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_UNIQUE)
			{
				e.setDataExceptionEnum(DataExceptionEnum.DS_DELIVERABLE_MULTI_ERROR);
			}
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 判断交付物的分类和交付项设置的分类是否一致
	 *
	 * @param delivery
	 * @throws ServiceRequestException
	 */
	private void checkDeliveryClassification(Deliverable delivery) throws ServiceRequestException
	{
		if (delivery != null && StringUtils.isGuid(delivery.getInstanceGuid()))
		{
			List<String> codeItemGuidList = this.listClassificationOfDeliverableItem(delivery);
			FoundationObject objectByGuid = this.stubService.getBOAS().getObjectByGuid(new ObjectGuid(delivery.getInstanceClassGuid(), null, delivery.getInstanceGuid(), null));
			if (!SetUtils.isNullList(codeItemGuidList) && objectByGuid != null && !codeItemGuidList.contains(objectByGuid.getClassificationGuid()))
			{
				throw new ServiceRequestException("ID_APP_PM_NOT_COMMIT_DELIVERY_CLASSIFICATION", "delivery classification ");
			}
		}
	}

	/**
	 * 取得交付项的所有分类
	 *
	 * @param strucObject
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<String> listClassificationOfDeliverableItem(Deliverable delivery) throws ServiceRequestException
	{
		List<String> codeItemGuidList = null;
		String itemGuid = delivery.getDeliverableItemGuid();
		if (StringUtils.isGuid(itemGuid))
		{
			DeliverableItem deliverableItem = this.getDeliveryItem(itemGuid);
			if (deliverableItem != null && StringUtils.isGuid(deliverableItem.getClassification()))
			{
				codeItemGuidList = new ArrayList<String>();
				codeItemGuidList.add(deliverableItem.getClassification());
				List<CodeItemInfo> codeItemList = this.stubService.getEMM().listAllSubCodeItemInfoByDatail(deliverableItem.getClassification());
				if (!SetUtils.isNullList(codeItemList))
				{
					for (CodeItemInfo info : codeItemList)
					{
						codeItemGuidList.add(info.getGuid());
					}
				}
			}
		}
		return codeItemGuidList;
	}

	/**
	 * 删除交付物
	 *
	 * @param deliveryGuid
	 * @throws ServiceRequestException
	 */
	public void deleteDelivery(String deliveryGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.delete(Deliverable.class, deliveryGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void deleteDeliveryItem(String guid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.delete(DeliverableItem.class, guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public List<Deliverable> listAllDeliveryByTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMTask, null);
			if (classInfo == null)
			{
				return null;
			}

			List<String> allSubTaskGuidList = this.listAllSubTaskGuid(taskObjectGuid.getGuid(), classInfo.getName());
			StringBuilder subTaskGuidBuilder = new StringBuilder();
			for (String subTaskGuid : allSubTaskGuidList)
			{
				if (subTaskGuidBuilder.length() > 0)
				{
					subTaskGuidBuilder.append(",");
				}
				subTaskGuidBuilder.append("'").append(subTaskGuid).append("'");
			}

			DSCommonService ds = DataServer.getDSCommonService();

			List<Deliverable> finalList = new ArrayList<Deliverable>();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("tasktablename", ds.getTableName(classInfo.getName()));
			filter.put(DeliverableItem.TASKGUID, taskObjectGuid.getGuid());
			filter.put("SUBTASKGUIDLIST", subTaskGuidBuilder.toString());
			List<Deliverable> classList = sds.query(Deliverable.class, filter, "selectClassOfTaskDeliverableWithSub");
			if (!SetUtils.isNullList(classList))
			{
				for (Deliverable d : classList)
				{
					filter.put(Deliverable.INSTANCECLASSGUID, d.getInstanceClassGuid());
					filter.put("CLASSGUID", d.getInstanceClassGuid());
					List<Deliverable> list = sds.query(Deliverable.class, filter, "selectTaskDeliverableWithSub");
					if (!SetUtils.isNullList(list))
					{
						finalList.addAll(list);
					}
				}
			}

			for (Deliverable item : finalList)
			{
				this.decodeDeliverable(item);
			}
			return finalList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<String> listAllSubTaskGuid(String taskGuid, String taskClassName) throws ServiceRequestException
	{
		List<String> taskGuidList = new ArrayList<>();
		taskGuidList.add(taskGuid);
		this.listAllSubTaskGuid(taskGuid, taskClassName, taskGuidList);

		return taskGuidList;
	}

	private void listAllSubTaskGuid(String taskGuid, String taskClassName, List<String> taskGuidList) throws ServiceRequestException
	{
		String taskTableName = DataServer.getDSCommonService().getTableName(taskClassName);

		Map<String, Object> params = new HashMap<>();
		params.put("FIELDS", "PARENTTASK");
		params.put("TABLENAME", taskTableName);
		params.put("WHERESQL", "PARENTTASK = '" + taskGuid + "'");

		List<FoundationObject> subTaskList = DataServer.getSystemDataService().query(FoundationObject.class, params, "selectShort");
		if (!SetUtils.isNullList(subTaskList))
		{
			PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
			for (FoundationObject subTask : subTaskList)
			{
				taskGuidList.add(subTask.getObjectGuid().getGuid());
				util.setFoundation(subTask);

				this.listAllSubTaskGuid(subTask.getObjectGuid().getGuid(), taskClassName, taskGuidList);
			}
		}
	}

	protected void copyDeliverableItem(ObjectGuid fromObjectGuid, ObjectGuid toProjectObjectGuid, Map<String, ObjectGuid> newObjGuidMap) throws ServiceRequestException
	{
		// 复制交付项
		List<DeliverableItem> itemList = this.listDeliveryItemByProject(fromObjectGuid.getGuid());

		if (!SetUtils.isNullList(itemList))
		{
			for (DeliverableItem item : itemList)
			{
				item.setProjectGuid(toProjectObjectGuid.getGuid());
				ObjectGuid objectGuid = newObjGuidMap.get(item.getTaskGuid());
				if (objectGuid == null || objectGuid.getGuid() == null)
				{
					continue;
				}

				item.setTaskGuid(objectGuid.getGuid());
				item.setGuid(null);
				this.saveDeliveryItem(item);
			}
		}
	}
}
