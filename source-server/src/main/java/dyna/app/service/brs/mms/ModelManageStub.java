package dyna.app.service.brs.mms;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.configure.ProjectModel;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.IconEntry;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.bean.model.ui.UIIcon;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.cls.NumberingModelInfo;
import dyna.common.dto.model.cls.NumberingObjectInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.ScriptTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
@Component
public class ModelManageStub extends AbstractServiceStub<MMSImpl>
{

	@SuppressWarnings("unchecked")
	protected <T extends SystemObject> T saveModel(T model) throws ServiceRequestException
	{
		DataServer.getSystemDataService().save(model);
		Class<T> clz = (Class<T>) model.getClass();
		return DataServer.getSystemDataService().get(clz, model.getGuid());
	}

	protected ProjectModel getCurrentSyncModel() throws ServiceRequestException
	{
		return DataServer.getSyncModelService().getCurrentSyncModel();
	}

	protected ProjectModel makeModelFile() throws ServiceRequestException
	{
		boolean hasClassificationLicense = serviceContext.getServerContext().getLicenseDaemon().hasLicence(ApplicationTypeEnum.CLS.name());
		return DataServer.getSyncModelService().makeModelFile(hasClassificationLicense);
	}

	protected void editIcon(UIIcon uiIcon) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			IconEntry classIcon = uiIcon.getClassIcon();
			if (classIcon != null)
			{
				ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classIcon.getClassGuid());
				classInfo.setIcon(classIcon.getImagePath());
				classInfo.setIcon32(classIcon.getImagePath32());
				classInfo.setUpdateUserGuid(currentUserGuid);
				sds.save(classInfo);
			}
			List<IconEntry> classActionIcon = uiIcon.getActionIcons();
			if (!SetUtils.isNullList(classActionIcon))
			{
				Map<String, Object> param = new HashMap<String, Object>();
				param.put(SystemObject.UPDATE_USER_GUID, currentUserGuid);
				List<ClassAction> actionList = this.stubService.getEMM().listClassAction(classActionIcon.get(0).getClassGuid());
				for (IconEntry iconEntry : classActionIcon)
				{
					param.put(iconEntry.getActionGuid(), iconEntry.getActionGuid());
					for (ClassAction action : actionList)
					{
						if (action.getGuid().equals(iconEntry.getActionGuid()))
						{
							action.setIcon(iconEntry.getImagePath());
							sds.save(action);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e);
		}

//		DataServer.getTransactionManager().commitTransaction();

	}

	protected void editNumberingModel(String classGuid, List<NumberingModel> numberingModelList, boolean isNumbering) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			UpperKeyMap param = new UpperKeyMap();
			param.put(NumberingModelInfo.CLASSFK, classGuid);
			param.put(NumberingModelInfo.ISNUMBERING, BooleanUtils.getBooleanStringYN(isNumbering));

			List<NumberingModelInfo> needDeleteList = sds.listFromCache(NumberingModelInfo.class, new FieldValueEqualsFilter<>(param));
			if (!SetUtils.isNullList(needDeleteList))
			{
				for (NumberingModelInfo modelInfo : needDeleteList)
				{
					List<NumberingObjectInfo> numberingObjectList = sds.listFromCache(NumberingObjectInfo.class,
							new FieldValueEqualsFilter<>(NumberingObjectInfo.NUMBERRULEFK, modelInfo.getGuid()));
					if (!SetUtils.isNullList(numberingObjectList))
					{
						for (NumberingObjectInfo numberingObject : numberingObjectList)
						{
							sds.deleteFromCache(NumberingObjectInfo.class, new FieldValueEqualsFilter<>(NumberingObjectInfo.PARENTGUID, numberingObject.getGuid()));
							sds.delete(numberingObject);
						}
					}
					sds.delete(modelInfo);
				}
			}

			if (!SetUtils.isNullList(numberingModelList))
			{
				for (NumberingModel numberModel : numberingModelList)
				{
					this.createNumberingModel(classGuid, numberModel);
				}
			}

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

	private void createNumberingModel(String classGuid, NumberingModel numberModel) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			NumberingModelInfo newModelInfo = numberModel.getModelInfo().clone();
			newModelInfo.setGuid(null);
			newModelInfo.setClassGuid(classGuid);
			newModelInfo.setCreateUserGuid(currentUserGuid);
			newModelInfo.setUpdateUserGuid(currentUserGuid);

			sds.save(newModelInfo);

			List<NumberingObject> numberObjectList = numberModel.getNumberingObjectList();
			if (!SetUtils.isNullList(numberObjectList))
			{
				for (NumberingObject numberObject : numberObjectList)
				{
					this.createNumberingObject(numberObject, newModelInfo.getGuid(), null);
				}
			}

		}
		catch (DynaDataException e)
		{
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

	private void createNumberingObject(NumberingObject numberObject, String numberModelguid, String parentGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

			NumberingObjectInfo newNumberObjectInfo = numberObject.getInfo().clone();
			newNumberObjectInfo.setGuid(null);
			newNumberObjectInfo.setParentGuid(parentGuid);
			newNumberObjectInfo.setNumberRuleFK(numberModelguid);
			newNumberObjectInfo.setCreateUserGuid(currentUserGuid);
			newNumberObjectInfo.setUpdateUserGuid(currentUserGuid);

			sds.save(newNumberObjectInfo);

			List<NumberingObject> numberObjectList = numberObject.getChildObject();
			if (!SetUtils.isNullList(numberObjectList))
			{
				for (NumberingObject childNumberObject : numberObjectList)
				{
					this.createNumberingObject(childNumberObject, numberModelguid, newNumberObjectInfo.getGuid());
				}
			}
		}
		catch (DynaDataException e)
		{
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

	protected void editClassAction(String classGuid, List<ClassAction> actionList, boolean isAction) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			UpperKeyMap param = new UpperKeyMap();
			param.put(ClassAction.CLASSFK, classGuid);
			String type = isAction ? ScriptTypeEnum.CLASSACTION.getType() : ScriptTypeEnum.CLASSEVENT.getType();
			param.put(ClassAction.SCRIPTTYPE, type);

			sds.deleteFromCache(ClassAction.class, new FieldValueEqualsFilter<ClassAction>(param));

			int sequence = 0;
			if (!SetUtils.isNullList(actionList))
			{
				for (ClassAction action : actionList)
				{
					ClassAction newAction = action.clone();
					newAction.setGuid(null);
					newAction.setSequence(sequence++);
					newAction.setClassfk(classGuid);
					newAction.setCreateUserGuid(currentUserGuid);
					newAction.setUpdateUserGuid(currentUserGuid);

					sds.save(newAction);

					this.createChildClassAction(classGuid, action.getChildren(), newAction.getGuid());

				}
			}

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

	private void createChildClassAction(String classGuid, List<Script> childList, String parentGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();
			if (!SetUtils.isNullList(childList))
			{
				int childSequence = 0;
				for (Script script : childList)
				{
					ClassAction childAction = (ClassAction) script;
					ClassAction newChild = childAction.clone();
					newChild.setGuid(null);
					// 脚本内容不放入数据库，保存文件名，通过文件名进行加载详细信息
					newChild.setScript(null);
					newChild.setSequence(childSequence++);
					newChild.setClassfk(classGuid);
					newChild.setParentGuid(parentGuid);
					newChild.setCreateUserGuid(currentUserGuid);
					newChild.setUpdateUserGuid(currentUserGuid);

					sds.save(newChild);

					this.createChildClassAction(classGuid, script.getChildren(), newChild.getGuid());
				}
			}
		}
		catch (DynaDataException e)
		{
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
	 * @param filename
	 * @param imageData
	 */
	public void saveIcon(String fileName, byte[] imageData)
	{

		String iconFileName = EnvUtils.getConfRootPath() + "/conf/icon/" + fileName;
		File iconFile = new File(iconFileName);
		try
		{
			if (iconFile.exists())
			{
				iconFile.delete();
				iconFile = new File(iconFileName);
			}
			FileOutputStream out = new FileOutputStream(iconFile);
			out.write(imageData);
			out.flush();
			out.close();
		}
		catch (Exception e)
		{
			DynaLogger.error("write icon file error: " + e.getMessage(), e);
		}
	}
}
