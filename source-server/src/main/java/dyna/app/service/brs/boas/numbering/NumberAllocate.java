package dyna.app.service.brs.boas.numbering;

import java.util.List;

import dyna.app.service.DataAccessService;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.cls.NumberingObjectInfo;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;

public class NumberAllocate
{
	@Deprecated
	public String modelerAllocate(EMM emm, FoundationObject foundationObject, DataAccessService dataAccessService, boolean isIdModelerNumbering, boolean isNameModelerNumbering,
			boolean isAlterIdMoldelerNumbering) throws ServiceRequestException
	{
		String allocateUnique = null;

		String classGuid = foundationObject.getObjectGuid().getClassGuid();
		String className = foundationObject.getObjectGuid().getClassName();

		if (isIdModelerNumbering)
		{
			isIdModelerNumbering = false;
			NumberingModel numberingModel = emm.lookupNumberingModel(classGuid, className, SystemClassFieldEnum.ID.getName());
			if (numberingModel != null)
			{
				if (!SetUtils.isNullList(numberingModel.getNumberingObjectList()))
				{
					isIdModelerNumbering = true;
				}
			}

		}
		if (isNameModelerNumbering)
		{
			isNameModelerNumbering = false;
			NumberingModel numberingModel = emm.lookupNumberingModel(classGuid, className, SystemClassFieldEnum.NAME.getName());
			if (numberingModel != null)
			{
				List<NumberingObjectInfo> numberingObjectInfoList = DataServer.getClassModelService().listChildNumberingObjectInfo(numberingModel.getGuid(), null);
				if (!SetUtils.isNullList(numberingObjectInfoList))
				{
					isNameModelerNumbering = true;
				}
			}
		}
		if (isAlterIdMoldelerNumbering)
		{
			isAlterIdMoldelerNumbering = false;
			NumberingModel numberingModel = emm.lookupNumberingModel(classGuid, className, SystemClassFieldEnum.ALTERID.getName());
			if (numberingModel != null)
			{
				List<NumberingObjectInfo> numberingObjectInfoList = DataServer.getClassModelService().listChildNumberingObjectInfo(numberingModel.getGuid(), null);
				if (!SetUtils.isNullList(numberingObjectInfoList))
				{
					isAlterIdMoldelerNumbering = true;
				}
			}
		}
		ModelerAllocate modelerAllocate = null;
		if (isIdModelerNumbering || isNameModelerNumbering || isAlterIdMoldelerNumbering)
		{

			modelerAllocate = new ModelerAllocate();
			if (isIdModelerNumbering)
			{
				allocateUnique = modelerAllocate.allocateUnique(foundationObject, SystemClassFieldEnum.ID.getName(), true, false, 1, dataAccessService);
				foundationObject.put(SystemClassFieldEnum.ID.getName(), StringUtils.convertNULLtoString(allocateUnique));

			}
			if (isNameModelerNumbering)
			{
				allocateUnique = modelerAllocate.allocateUnique(foundationObject, SystemClassFieldEnum.NAME.getName(), true, false, 1, dataAccessService);
				foundationObject.put(SystemClassFieldEnum.NAME.getName(), StringUtils.convertNULLtoString(allocateUnique));

			}

			if (isAlterIdMoldelerNumbering)
			{
				allocateUnique = modelerAllocate.allocateUnique(foundationObject, SystemClassFieldEnum.ALTERID.getName(), true, false, 1, dataAccessService);
				foundationObject.put(SystemClassFieldEnum.ALTERID.getName(), StringUtils.convertNULLtoString(allocateUnique));

			}
		}

		return allocateUnique;
	}

	public void updateFieldMaxSerial(FoundationObject foundationObject, DataAccessService dataAccessService, String fieldName) throws ServiceRequestException
	{
		try
		{
			String id = foundationObject.getId();
			ClassificationAllocate ac = new ClassificationAllocate();
			if (StringUtils.isNullString(id))
			{
				// ac.startSingleNumberItem(foundationObject, dataAccessService,
				// SystemClassFieldEnum.ID.getName(), true);
				return;
			}

			foundationObject.setId(null);
			String allocateSingleItem = ac.startSingleNumberItem(foundationObject, dataAccessService, fieldName, false);
			String split = "\t";
			if (StringUtils.isNullString(allocateSingleItem))
			{
				return;
			}

			if (id.length() != allocateSingleItem.length())
			{
				return;
			}

			int indexOf = allocateSingleItem.indexOf(split);
			if (indexOf == -1)
			{
				return;
			}

			int lastIndexOf = allocateSingleItem.lastIndexOf(split);
			int length = lastIndexOf - indexOf + 1;
			if (length < 1)
			{
				return;
			}

			String maxValue = id.substring(indexOf, lastIndexOf + 1);
			Integer startValue = 0;
			try
			{
				startValue = Integer.valueOf(maxValue);
			}
			catch (Exception e)
			{
				return;
			}

			allocateSingleItem = allocateSingleItem.replaceAll(split, "");
			String substringLeft = id.substring(0, indexOf);
			String substringright = id.substring(lastIndexOf + 1, id.length());

			id = substringLeft + substringright;
			if (id.equals(allocateSingleItem))
			{
				ac.updateFieldMaxSerial(foundationObject, dataAccessService, fieldName, startValue);
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("update ID MaxSerial error", e);
		}

	}

	@Deprecated
	public void updateIDMaxSerial(FoundationObject foundationObject, DataAccessService dataAccessService) throws ServiceRequestException
	{
		try
		{
			String id = foundationObject.getId();
			ClassificationAllocate ac = new ClassificationAllocate();
			if (StringUtils.isNullString(id))
			{
				// ac.startSingleNumberItem(foundationObject, dataAccessService,
				// SystemClassFieldEnum.ID.getName(), true);
				return;
			}

			foundationObject.setId(null);
			String allocateSingleItem = ac.startSingleNumberItem(foundationObject, dataAccessService, SystemClassFieldEnum.ID.getName(), false);
			String split = "\t";
			if (StringUtils.isNullString(allocateSingleItem))
			{
				return;
			}

			if (id.length() != allocateSingleItem.length())
			{
				return;
			}

			int indexOf = allocateSingleItem.indexOf(split);
			if (indexOf == -1)
			{
				return;
			}

			int lastIndexOf = allocateSingleItem.lastIndexOf(split);
			int length = lastIndexOf - indexOf + 1;
			if (length < 1)
			{
				return;
			}

			String maxValue = id.substring(indexOf, lastIndexOf + 1);
			Integer startValue = 0;
			try
			{
				startValue = Integer.valueOf(maxValue);
			}
			catch (Exception e)
			{
				return;
			}

			allocateSingleItem = allocateSingleItem.replaceAll(split, "");
			String substringLeft = id.substring(0, indexOf);
			String substringright = id.substring(lastIndexOf + 1, id.length());

			id = substringLeft + substringright;
			if (id.equals(allocateSingleItem))
			{
				ac.updateIdSerial(foundationObject, dataAccessService, startValue);
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("update ID MaxSerial error", e);
		}

	}

	public String allocate(FoundationObject foundationObject, DataAccessService dataAccessService, boolean isCreate) throws ServiceRequestException
	{
		ClassificationAllocate ac = new ClassificationAllocate();
		String message = ac.start(foundationObject, dataAccessService, isCreate);
		if (!isCreate)
		{
		}
		else
		{
			// CFM cfm = null;
			EMM emm = null;

			try
			{
				// cfm = dataAccessService.getRefService(CFM.class);
				emm = dataAccessService.getRefService(EMM.class);
			}
			catch (ServiceNotFoundException e)
			{
				throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
			}
			// String classGuid = foundationObject.getObjectGuid().getClassGuid();
			// String className = foundationObject.getObjectGuid().getClassName();

			boolean isIdModelerNumbering = true;
			boolean isNameModelerNumbering = true;
			boolean isAlterIdMoldelerNumbering = true;

			// 分类管理中是否有id,name,alterid编码
			if (!SetUtils.isNullList(ac.getAllFeatureItemList()))
			{
				for (ClassficationFeatureItem item : ac.getAllFeatureItemList())
				{
					if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(item.getFieldName()))
					{
						isIdModelerNumbering = false;
					}
					else if (SystemClassFieldEnum.NAME.getName().equalsIgnoreCase(item.getFieldName()))
					{
						isNameModelerNumbering = false;
					}
					else if (SystemClassFieldEnum.ALTERID.getName().equalsIgnoreCase(item.getFieldName()))
					{
						isAlterIdMoldelerNumbering = false;
					}
				}
			}
			if (!StringUtils.isNullString(foundationObject.getId()))
			{
				isIdModelerNumbering = false;
			}
			ClassInfo instanceClassInfo = emm.getClassByGuid(foundationObject.getObjectGuid().getClassGuid());
			if (isCreate && instanceClassInfo.hasInterface(ModelInterfaceEnum.IItem) && instanceClassInfo.hasInterface(ModelInterfaceEnum.ICAD)
					&& instanceClassInfo.hasInterface(ModelInterfaceEnum.IDWTransfer))
			{
				isAlterIdMoldelerNumbering = false;
			}
			if (isIdModelerNumbering || isNameModelerNumbering || isAlterIdMoldelerNumbering)
			{
				// String modelerAllocate =
				this.modelerAllocate(emm, foundationObject, dataAccessService, isIdModelerNumbering, isNameModelerNumbering, isAlterIdMoldelerNumbering);
			}
		}

		return message;
	}

	public String alterIdAllocate(FoundationObject foundationObject, DataAccessService dataAccessService, boolean isCreate) throws ServiceRequestException
	{
		ClassificationAllocate ac = new ClassificationAllocate();
		String message = ac.start(foundationObject, dataAccessService, true);

		EMM emm = null;

		try
		{
			emm = dataAccessService.getRefService(EMM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
		boolean isAlterIdMoldelerNumbering = true;

		// 分类管理中是否有id,name,alterid编码
		if (!SetUtils.isNullList(ac.getAllFeatureItemList()))
		{
			for (ClassficationFeatureItem item : ac.getAllFeatureItemList())
			{
				if (SystemClassFieldEnum.ALTERID.getName().equalsIgnoreCase(item.getFieldName()))
				{
					isAlterIdMoldelerNumbering = false;
				}
			}
		}

		if (isCreate)
		{
			isAlterIdMoldelerNumbering = false;
		}

		if (isAlterIdMoldelerNumbering)
		{
			this.modelerAllocate(emm, foundationObject, dataAccessService, false, false, isAlterIdMoldelerNumbering);
		}

		return message;
	}

}
