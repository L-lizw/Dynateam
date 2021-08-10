package dyna.app.service.brs.mms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.ui.*;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ECOperateTypeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIManageStub extends AbstractServiceStub<MMSImpl>
{
	protected UIManageStub(ServiceContext context, MMSImpl service)
	{
		super(context, service);
	}

	protected List<UIObjectInfo> copy4CreateUIObjct(List<UIObjectInfo> sourceUIList, String toclassGuid, boolean isCut) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			List<UIObjectInfo> resultList = new ArrayList<UIObjectInfo>();
			if (!SetUtils.isNullList(sourceUIList))
			{
				// 复制来源类
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(sourceUIList.get(0).getClassGuid());
				// 目的类
				ClassInfo toClassInfo = this.stubService.getEMM().getClassByGuid(toclassGuid);
				if (toClassInfo != null)
				{
					for (UIObjectInfo uiObjectInfo : sourceUIList)
					{
						resultList.add(this.createUIObject(uiObjectInfo, toClassInfo));
						if (isCut)
						{
							this.deleteUIObject(uiObjectInfo.getGuid());
						}
					}
				}
			}

//			DataServer.getTransactionManager().commitTransaction();

			return resultList;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	private UIObjectInfo createUIObject(UIObjectInfo sourceUIObjectInfo, ClassInfo toClassInfo) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
		try
		{
			UIObjectInfo newUIObjectInfo = sourceUIObjectInfo.clone();
			newUIObjectInfo.setGuid(null);
			newUIObjectInfo.setClassGuid(toClassInfo.getGuid());
			newUIObjectInfo.setCreateUserGuid(currentUserGuid);
			newUIObjectInfo.setUpdateUserGuid(currentUserGuid);
			String newUiObjectGuid = sds.save(newUIObjectInfo);

			Map<String, String> canUseFieldNameguidMap = new HashMap<String, String>();
			List<ClassField> allFieldList = this.stubService.getEMM().listFieldOfClass(toClassInfo.getName());
			if (!SetUtils.isNullList(allFieldList))
			{
				for (ClassField classField : allFieldList)
				{
					canUseFieldNameguidMap.put(classField.getName(), classField.getGuid());
				}
			}

			List<UIField> uiFieldList = this.stubService.getEMM().listUIFieldByUIGuid(sourceUIObjectInfo.getGuid());
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField uiField : uiFieldList)
				{
					String fieldName = uiField.getName();
					if (canUseFieldNameguidMap.containsKey(fieldName))
					{
						UIField newUIField = uiField.clone();
						newUIField.setGuid(null);
						newUIField.setUIGuid(newUiObjectGuid);
						newUIField.setFieldGuid(canUseFieldNameguidMap.get(fieldName));
						newUIField.setCreateUserGuid(currentUserGuid);
						newUIField.setUpdateUserGuid(currentUserGuid);
						sds.save(newUIField);
					}

				}
			}
			List<UIAction> uiActionList = this.stubService.getEMM().listUIAction(sourceUIObjectInfo.getGuid());
			if (!SetUtils.isNullList(uiActionList))
			{
				for (UIAction uiAction : uiActionList)
				{
					UIAction newUIAction = uiAction.clone();
					newUIAction.setGuid(null);
					newUIAction.setUIGuid(newUiObjectGuid);
					sds.save(newUIAction);
				}
			}
			return newUIObjectInfo;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	protected List<ClassificationUIInfo> copy4CreateClassificationUIObjct(List<ClassificationUIInfo> sourceUIList, String toCodeItemGuid, boolean isCut)
			throws ServiceRequestException
	{
		List<ClassificationUIInfo> resultList = new ArrayList<ClassificationUIInfo>();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			if (!SetUtils.isNullList(sourceUIList))
			{
				// 复制来源分类子项
				CodeItemInfo code = this.stubService.getEMM().getCodeItem(sourceUIList.get(0).getClassificationGuid());
				// 目的分类子项
				CodeItemInfo toCodeItemInfo = this.stubService.getEMM().getCodeItem(toCodeItemGuid);
				if (toCodeItemInfo != null)
				{
					for (ClassificationUIInfo uiObjectInfo : sourceUIList)
					{
						resultList.add(this.createClassificationUIObject(uiObjectInfo, toCodeItemInfo));
						if (isCut)
						{
							this.deleteClassificationUIObject(uiObjectInfo.getGuid());
						}
					}
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
			return resultList;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	private ClassificationUIInfo createClassificationUIObject(ClassificationUIInfo sourceUIObjectInfo, CodeItemInfo toClassificationItem) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
			ClassificationUIInfo newUIObjectInfo = (ClassificationUIInfo) sourceUIObjectInfo.clone();
			newUIObjectInfo.setGuid(null);
			newUIObjectInfo.setClassificationGuid(toClassificationItem.getGuid());
			newUIObjectInfo.setCreateUserGuid(currentUserGuid);
			newUIObjectInfo.setUpdateUserGuid(currentUserGuid);
			String newUiObjectGuid = sds.save(newUIObjectInfo);

			Map<String, String> canUseFieldNameguidMap = new HashMap<String, String>();
			List<ClassField> allFieldList = this.stubService.getEMM().listClassificationField(toClassificationItem.getGuid());
			if (!SetUtils.isNullList(allFieldList))
			{
				for (ClassField classField : allFieldList)
				{
					canUseFieldNameguidMap.put(classField.getName(), classField.getGuid());
				}
			}

			List<UIField> uiFieldList = this.stubService.getEMM().listCFUIField(sourceUIObjectInfo.getGuid());
			if (!SetUtils.isNullList(uiFieldList))
			{
				for (UIField uiField : uiFieldList)
				{
					String fieldName = uiField.getName();
					if (canUseFieldNameguidMap.containsKey(fieldName))
					{
						ClassificationUIField newUIField = new ClassificationUIField();
						newUIField.putAll(uiField);
						newUIField.setGuid(null);
						newUIField.setUIGuid(newUiObjectGuid);
						newUIField.setFieldGuid(canUseFieldNameguidMap.get(fieldName));
						newUIField.setCreateUserGuid(currentUserGuid);
						newUIField.setUpdateUserGuid(currentUserGuid);
						sds.save(newUIField);
					}

				}
			}

			return newUIObjectInfo;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteClassificationUIField(String cfUIField) throws ServiceRequestException
	{
		DataServer.getSystemDataService().delete(ClassificationUIField.class, cfUIField);
	}

	protected void deleteClassificationUIObject(String uiGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		sds.deleteFromCache(ClassificationUIField.class, new FieldValueEqualsFilter<ClassificationUIField>(ClassificationUIField.CLASSIFICATIONUIFK, uiGuid));
		sds.delete(ClassificationUIInfo.class, uiGuid);
	}

	protected void deleteUIField(String uiFieldGuid) throws ServiceRequestException
	{
		DataServer.getSystemDataService().delete(UIField.class, uiFieldGuid);
	}

	protected void deleteUIObject(String uiGuid) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			sds.deleteFromCache(UIField.class, new FieldValueEqualsFilter<UIField>(UIField.UIGUID, uiGuid));

			sds.deleteFromCache(UIAction.class, new FieldValueEqualsFilter<UIAction>(UIAction.UIGUID, uiGuid));

			sds.delete(UIObjectInfo.class, uiGuid);
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void deleteUIAction(String uiActionGuid) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			DataServer.getSystemDataService().delete(UIAction.class, uiActionGuid);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void editClassificationUI(List<ClassificationUIInfo> cfUIInfoList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			if (!SetUtils.isNullList(cfUIInfoList))
			{
				for (ClassificationUIInfo uiObjectInfo : cfUIInfoList)
				{
					ECOperateTypeEnum operateEnum = (ECOperateTypeEnum) uiObjectInfo.get("OPERATETYPE");
					if (operateEnum == null)
					{
						throw new ServiceRequestException("not found operate for this ClassificationUIInfo,please recheck!");
					}

					String guid = uiObjectInfo.getGuid();

					if (ECOperateTypeEnum.INSERT.equals(operateEnum))
					{
						if (guid != null)
						{
							throw new ServiceRequestException("INSERT operate error! found guid in this UIOjbectInfo:guid: " + guid);
						}
						// 新建的ui根据classificationguid，name查询是否别人已经创建
						UpperKeyMap param = new UpperKeyMap();
						param.put(ClassificationUIInfo.CLASSIFICATIONFK, uiObjectInfo.getClassificationGuid());
						param.put(ClassificationUIInfo.NAME, uiObjectInfo.getName());

						List<ClassificationUIInfo> cacheList = sds.listFromCache(ClassificationUIInfo.class, new FieldValueEqualsFilter<ClassificationUIInfo>(param));
						if (!SetUtils.isNullList(cacheList))
						{
							throw new ServiceRequestException("ID_DS_SAVE_SYSTEMOBJECT_UI_EXISTS", "UIObject has exists,can not reCreate! guid: " + cacheList.get(0).getGuid());
						}
						sds.save(uiObjectInfo);
					}
					else if (ECOperateTypeEnum.MODIFY.equals(operateEnum))
					{
						if (guid == null)
						{
							throw new ServiceRequestException("MODIFY operate error! not found guid in this UIOjbectInfo: " + uiObjectInfo);
						}
						// 建模器多用户登录可能ui已被修改，这将不进行保存
						UpperKeyMap param = new UpperKeyMap();
						param.put(ClassificationUIInfo.GUID, uiObjectInfo.getGuid());
						List<ClassificationUIInfo> cacheList = sds.listFromCache(ClassificationUIInfo.class, new FieldValueEqualsFilter<ClassificationUIInfo>(param));
						ClassificationUIInfo exists = SetUtils.isNullList(cacheList) ? null : cacheList.get(0);
						if (exists == null)
						{
							throw new ServiceRequestException("ID_DS_NO_DATA", "UIObject is not exist, guid='" + guid + "'");
						}
						if (!DateFormat.formatYMDHMS(exists.getUpdateTime()).equals(DateFormat.formatYMDHMS(uiObjectInfo.getUpdateTime())))
						{
							throw new ServiceRequestException("ID_DS_SAVE_SYSTEMOBJECT_PARAM_DATA_LOST", "save failed. guid = " + guid);
						}
						sds.save(uiObjectInfo);
					}
					else if (ECOperateTypeEnum.REPLACE.equals(operateEnum))
					{
						// 此時的guid为父类的uiguid，重载时需将字段复制一份到当前新创建的ui
						List<ClassificationUIInfo> sourceUIList = new ArrayList<ClassificationUIInfo>();
						sourceUIList.add(uiObjectInfo);
						this.copy4CreateClassificationUIObjct(sourceUIList, uiObjectInfo.getClassificationGuid(), false);
					}
					else if (ECOperateTypeEnum.REMOVE.equals(operateEnum))
					{
						this.deleteClassificationUIObject(uiObjectInfo.getGuid());
					}
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 空集合表示ui的全部字段都执行了了删除
	 * 
	 * @param uiInfo
	 * @param uiFieldList
	 * @throws ServiceRequestException
	 */
	protected ClassificationUIInfo editCFUIField(ClassificationUIInfo uiInfo, List<UIField> uiFieldList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			// uifield的编辑保存暂且采用全部替换为新的集合的方式
			if (uiInfo.isInherited())
			{
				uiInfo.setGuid(null);
				uiInfo.setCreateUserGuid(currentUserGuid);
				uiInfo.setUpdateUserGuid(currentUserGuid);
				List<ClassificationUIInfo> hasExistsList = sds.listFromCache(ClassificationUIInfo.class,
						new FieldValueEqualsFilter<>(ClassificationUIInfo.CLASSIFICATIONFK, uiInfo.getClassificationGuid()));
				uiInfo.setSequence(hasExistsList == null ? 0 : hasExistsList.size());
				sds.save(uiInfo);
			}
			else
			{
				sds.deleteFromCache(ClassificationUIField.class, new FieldValueEqualsFilter<ClassificationUIField>(ClassificationUIField.CLASSIFICATIONUIFK, uiInfo.getGuid()));
			}

			if (!SetUtils.isNullList(uiFieldList))
			{
				// 检查当前编辑的ui字段所属ui是否已经被删除或者修改
				ClassificationUIInfo uiObjectInfo = sds.get(ClassificationUIInfo.class, uiInfo.getGuid());
				if (uiObjectInfo == null)
				{
					throw new ServiceRequestException("ID_DS_SAVE_SYSTEMOBJECT_UI_LOST", "owner UIObject not found, UIObject guid: " + uiInfo);
				}
				int sequence = 0;

				for (UIField uiField : uiFieldList)
				{
					ClassificationUIField cfUIField = new ClassificationUIField();
					cfUIField.putAll(uiField);
					cfUIField.setGuid(null);
					cfUIField.setFieldGuid(uiField.getFieldGuid());
					cfUIField.setSequence(sequence++);
					cfUIField.setCreateUserGuid(currentUserGuid);
					cfUIField.setUpdateUserGuid(currentUserGuid);
					cfUIField.setUIGuid(uiInfo.getGuid());
					sds.save(cfUIField);
				}
			}

//			DataServer.getTransactionManager().commitTransaction();

			return uiInfo;
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	/**
	 * 空集合表示ui的全部字段都执行了了删除
	 * 
	 * @param uiInfo
	 * @param uiFieldList
	 * @throws ServiceRequestException
	 */
	protected UIObjectInfo editUIField(UIObjectInfo uiInfo, List<UIField> uiFieldList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			// 继承来的ui编辑字段时，ui需重建
			boolean isInherited = uiInfo.isFieldInherited();
			String uiGuid = uiInfo.getGuid();
			if (isInherited)
			{
				uiInfo.setGuid(null);
				uiInfo.setCreateUserGuid(currentUserGuid);
				uiInfo.setUpdateUserGuid(currentUserGuid);
				List<UIObjectInfo> hasExistsList = sds.listFromCache(UIObjectInfo.class, new FieldValueEqualsFilter<>(UIObjectInfo.CLASSFK, uiInfo.getClassGuid()));
				uiInfo.setSequence(hasExistsList == null ? 0 : hasExistsList.size());
				uiGuid = sds.save(uiInfo);
			}
			else
			{
				sds.deleteFromCache(UIField.class, new FieldValueEqualsFilter<UIField>(UIField.UIGUID, uiInfo.getGuid()));
			}

			if (!SetUtils.isNullList(uiFieldList))
			{
				int sequence = 0;

				// 检查当前编辑的ui字段所属ui是否已经被删除或者修改

				if(!isInherited)
				{
					UIObjectInfo uiObjectInfo = sds.get(UIObjectInfo.class, uiGuid);
					if (uiObjectInfo == null)
					{
						throw new ServiceRequestException("ID_DS_SAVE_SYSTEMOBJECT_UI_LOST", "owner UIObject not found, UIObject guid: " + uiInfo);
					}
				}
				
				for (UIField uiField : uiFieldList)
				{
					uiField.setGuid(null);
					uiField.setSequence(sequence++);
					uiField.setCreateUserGuid(currentUserGuid);
					uiField.setUpdateUserGuid(currentUserGuid);
					uiField.setUIGuid(uiGuid);
					sds.save(uiField);
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
			return uiInfo;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
	}

	protected void editUIObject(List<UIObjectInfo> uiObjectInfoList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{

			if (!SetUtils.isNullList(uiObjectInfoList))
			{
				for (UIObjectInfo uiObjectInfo : uiObjectInfoList)
				{
					ECOperateTypeEnum operateEnum = (ECOperateTypeEnum) uiObjectInfo.get("OPERATETYPE");
					if (operateEnum == null)
					{
						throw new ServiceRequestException("not found operate for this UIObject,please recheck!");
					}

					String guid = uiObjectInfo.getGuid();

					if (ECOperateTypeEnum.INSERT.equals(operateEnum))
					{
						if (guid != null)
						{
							throw new ServiceRequestException("INSERT operate error! found guid in this UIOjbectInfo:guid: " + guid);
						}
						// 新建的ui根据classguid，name查询是否别人已经创建
						UpperKeyMap param = new UpperKeyMap();
						param.put(UIObjectInfo.CLASSFK, uiObjectInfo.getClassGuid());
						param.put(UIObjectInfo.NAME, uiObjectInfo.getName());

						List<UIObjectInfo> cacheList = sds.listFromCache(UIObjectInfo.class, new FieldValueEqualsFilter<UIObjectInfo>(param));
						if (!SetUtils.isNullList(cacheList))
						{
							throw new ServiceRequestException("ID_DS_SAVE_SYSTEMOBJECT_UI_EXISTS", "UIObject has exists,can not reCreate! guid: " + cacheList.get(0).getGuid());
						}
						sds.save(uiObjectInfo);
					}
					else if (ECOperateTypeEnum.MODIFY.equals(operateEnum))
					{
						if (guid == null)
						{
							throw new ServiceRequestException("MODIFY operate error! not found guid in this UIOjbectInfo: " + uiObjectInfo);
						}
						// 建模器多用户登录可能ui已被修改，这将不进行保存
						UpperKeyMap param = new UpperKeyMap();
						param.put(UIObjectInfo.GUID, uiObjectInfo.getGuid());
						List<UIObjectInfo> cacheList = sds.listFromCache(UIObjectInfo.class, new FieldValueEqualsFilter<UIObjectInfo>(param));
						UIObjectInfo exists = SetUtils.isNullList(cacheList) ? null : cacheList.get(0);
						if (exists == null)
						{
							throw new ServiceRequestException("ID_DS_NO_DATA", "UIObject is not exist, guid='" + guid + "'");
						}
						if (!exists.getUpdateTime().equals(uiObjectInfo.getUpdateTime()))
						{
							throw new ServiceRequestException("ID_DS_SAVE_SYSTEMOBJECT_PARAM_DATA_LOST", "save failed. guid = " + guid);
						}
						sds.save(uiObjectInfo);
					}
					else if (ECOperateTypeEnum.REPLACE.equals(operateEnum))
					{
						// 此時的guid为父类的uiguid，重载时需将字段复制一份到当前新创建的ui
						List<UIObjectInfo> sourceUIList = new ArrayList<UIObjectInfo>();
						sourceUIList.add(uiObjectInfo);
						this.copy4CreateUIObjct(sourceUIList, uiObjectInfo.getClassGuid(), false);
					}
					else if (ECOperateTypeEnum.REMOVE.equals(operateEnum))
					{
						this.deleteUIObject(uiObjectInfo.getGuid());
					}
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}

	}

	protected UIObject saveUIObject(UIObject uiObject) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String ret = sds.save(uiObject.getInfo());
		if (StringUtils.isGuid(ret))
		{
			uiObject.setGuid(ret);
		}

		List<UIField> fieldList = uiObject.getFieldList();
		if (fieldList != null)
		{
			fieldList.forEach(uiField -> {
				uiField.setUIGuid(uiObject.getGuid());
				sds.save(uiField);
			});
		}

		List<UIAction> actionList = uiObject.getActionList();
		if (actionList != null)
		{
			actionList.forEach(action -> {
				action.setUIGuid(uiObject.getGuid());
				sds.save(action);
			});
		}

		return DataServer.getSystemDataService().get(UIObject.class, uiObject.getGuid());
	}

	protected UIField saveUIField(UIField uiField) throws ServiceRequestException
	{
		DataServer.getSystemDataService().save(uiField);
		return DataServer.getSystemDataService().get(UIField.class, uiField.getGuid());
	}

	protected UIAction saveUIAction(UIAction uiAction) throws ServiceRequestException
	{
		DataServer.getSystemDataService().save(uiAction);
		return DataServer.getSystemDataService().get(UIAction.class, uiAction.getGuid());
	}
}
