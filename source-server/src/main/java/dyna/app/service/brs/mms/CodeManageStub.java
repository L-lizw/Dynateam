package dyna.app.service.brs.mms;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.EMM;
import org.acegisecurity.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeManageStub extends AbstractServiceStub<MMSImpl>
{
	protected CodeManageStub(ServiceContext context, MMSImpl service)
	{
		super(context, service);
	}

	protected List<CodeObjectInfo> copy4CreateCodeObject(List<CodeObjectInfo> sourceCodeObjectInfoList, boolean isCut) throws ServiceRequestException
	{
		List<CodeObjectInfo> resultlist = new ArrayList<CodeObjectInfo>();
		if (!SetUtils.isNullList(sourceCodeObjectInfoList))
		{
			for (CodeObjectInfo codeObjectInfo : sourceCodeObjectInfoList)
			{
				resultlist.add(this.createCodeObject(codeObjectInfo, isCut));

			}
		}
		return resultlist;
	}

	protected CodeObjectInfo createCodeObject(CodeObjectInfo sourceCodeObjectInfo, boolean needDelSource) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserguid = this.stubService.getUserSignature().getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			CodeObjectInfo newCodeObejctInfo = sourceCodeObjectInfo.clone();
			newCodeObejctInfo.setGuid(null);
			newCodeObejctInfo.setCreateUserGuid(currentUserguid);
			newCodeObejctInfo.setUpdateUserGuid(currentUserguid);
			sds.save(newCodeObejctInfo);

			List<CodeItemInfo> firstLevelCodeItemList = this.stubService.getEMM().listSubCodeItemForMaster(sourceCodeObjectInfo.getGuid(), null);
			if (!SetUtils.isNullList(firstLevelCodeItemList))
			{
				for (CodeItemInfo codeItemInfo : firstLevelCodeItemList)
				{
					this.createCodeItem(codeItemInfo, newCodeObejctInfo.getGuid(), null, needDelSource);
				}
			}

			if (needDelSource)
			{
				sds.delete(sourceCodeObjectInfo);
			}

//			DataServer.getTransactionManager().commitTransaction();

			return newCodeObejctInfo;
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

	protected void copy4createCodeItem(List<CodeItemInfo> sourcCodeItemInfoList, String codeObjectguid, String parentCodeItemguid) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(sourcCodeItemInfoList))
		{
			for (CodeItemInfo codeItemInfo : sourcCodeItemInfoList)
			{
				this.createCodeItem(codeItemInfo, codeObjectguid, parentCodeItemguid, false);
			}
		}
	}

	protected void createCodeItem(CodeItemInfo sourcCodeItemInfo, String codeObjectguid, String parentCodeItemguid, boolean needDelSource) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserguid = this.stubService.getUserSignature().getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		try
		{
			CodeItemInfo newCodeItemInfo = sourcCodeItemInfo.clone();
			newCodeItemInfo.setGuid(null);
			newCodeItemInfo.setCodeGuid(codeObjectguid);
			newCodeItemInfo.setParentGuid(parentCodeItemguid);
			newCodeItemInfo.setCreateUserGuid(currentUserguid);
			newCodeItemInfo.setUpdateUserGuid(currentUserguid);
			sds.save(newCodeItemInfo);

			List<CodeItemInfo> subCodeItemInfoList = this.stubService.getEMM().listSubCodeItemForDetail(sourcCodeItemInfo.getGuid());
			if (!SetUtils.isNullList(subCodeItemInfoList))
			{
				for (CodeItemInfo subCodeItemInfo : subCodeItemInfoList)
				{
					this.createCodeItem(subCodeItemInfo, codeObjectguid, newCodeItemInfo.getGuid(), needDelSource);
				}
			}

			if (needDelSource)
			{
				sds.delete(sourcCodeItemInfo);
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

	protected void editCodeObject(CodeObject sourceCodeObject) throws ServiceRequestException
	{
//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

		try
		{
			CodeObjectInfo codeObjectInfo = sourceCodeObject.getInfo();
			Map<String, String> nameGuidMap = new HashMap<String, String>();
			if (codeObjectInfo.getGuid() == null)
			{
				codeObjectInfo.setCreateUserGuid(currentUserGuid);
			}
			else
			{
				List<CodeItemInfo> existsList = sds.listFromCache(CodeItemInfo.class, new FieldValueEqualsFilter<CodeItemInfo>(CodeItemInfo.MASTERGUID, codeObjectInfo.getGuid()));
				if (!SetUtils.isNullList(existsList))
				{
					for (CodeItemInfo codeItemInfo : existsList)
					{
						nameGuidMap.put(codeItemInfo.getName(), codeItemInfo.getGuid());
					}
				}
			}
			codeObjectInfo.setUpdateUserGuid(currentUserGuid);
			sds.save(codeObjectInfo);

			List<CodeItem> codeItemList = sourceCodeObject.getCodeDetailList();
			if (!SetUtils.isNullList(codeItemList))
			{
				int sequence = 0;
				for (CodeItem codeItem : codeItemList)
				{
					codeItem.setGuid(nameGuidMap.get(codeItem.getName()));
					nameGuidMap.remove(codeItem.getName());
					this.editCodeItem(codeItem, codeObjectInfo.getGuid(), null, sequence++, nameGuidMap);
				}
			}

			if (!SetUtils.isNullMap(nameGuidMap))
			{
				for (Map.Entry<String, String> entry : nameGuidMap.entrySet())
				{
					sds.delete(CodeItemInfo.class, entry.getValue());
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

	private void editCodeItem(CodeItem codeItem, String masterGuid, String parentItemGuid, int sequence, Map<String, String> nameGuidMap) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String currentUserGuid = ((UserSignature) this.stubService.getSignature()).getUserGuid();

//		DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

		try
		{
			CodeItemInfo codeItemInfo = codeItem.getInfo().clone();
			codeItemInfo.setSequence(sequence);
			codeItemInfo.setMasterGuid(masterGuid);
			codeItemInfo.setParentGuid(parentItemGuid);
			codeItemInfo.setCreateUserGuid(currentUserGuid);
			codeItemInfo.setUpdateUserGuid(currentUserGuid);
			sds.save(codeItemInfo);

			List<CodeItem> codeItemList = codeItem.getCodeDetailList();
			if (!SetUtils.isNullList(codeItemList))
			{
				int subSequence = 0;
				for (CodeItem subItem : codeItemList)
				{
					subItem.setGuid(nameGuidMap.get(subItem.getName()));
					nameGuidMap.remove(codeItem.getName());
					this.editCodeItem(subItem, masterGuid, codeItemInfo.getGuid(), subSequence++, nameGuidMap);
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

	protected void saveCodeItem(CodeItemInfo codeItemInfo) throws ServiceRequestException
	{
		SystemDataService systemDataService = DataServer.getSystemDataService();
		String ownerUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();
		codeItemInfo.setUpdateUserGuid(ownerUserGuid);
		systemDataService.save(codeItemInfo);
	}

	protected void deleteCodeObject(String codeguid) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			SystemDataService sds = DataServer.getSystemDataService();

			sds.deleteFromCache(CodeItemInfo.class, new FieldValueEqualsFilter<CodeItemInfo>(CodeItemInfo.MASTERGUID, codeguid));

			sds.delete(CodeObjectInfo.class, codeguid);
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

	protected void deleteCodeItem(String codeItemGuid) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			SystemDataService sds = DataServer.getSystemDataService();

			this.deleteChildCodeItem(codeItemGuid);

			sds.delete(CodeItemInfo.class, codeItemGuid);
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

	private void deleteChildCodeItem(String parentItemGuid) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			List<CodeItemInfo> childList = sds.listFromCache(CodeItemInfo.class, new FieldValueEqualsFilter<CodeItemInfo>(CodeItemInfo.PARENTGUID, parentItemGuid));
			if (!SetUtils.isNullList(childList))
			{
				for (CodeItemInfo itemInfo : childList)
				{
					this.deleteChildCodeItem(itemInfo.getGuid());
					sds.delete(itemInfo);
				}
			}
		}
		catch (Exception e)
		{
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

	protected void updateClassificationField(String codeItemGuid, List<ClassField> fieldList) throws ServiceRequestException
	{

	}

	private class SaveCodeTreeScheduledTask extends AbstractScheduledTask
	{
		private ServiceContext	serviceContext	= null;
		private EMM				emm				= null;
		private UserSignature	userSignature;

		private CodeObjectInfo	codeObjectInfo	= null;

		public SaveCodeTreeScheduledTask(ServiceContext serviceContext, CodeObjectInfo codeObjectInfo)
		{
			this.serviceContext = serviceContext;
			this.codeObjectInfo = codeObjectInfo;
		}

		@Override
		public void run()
		{
			DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]SaveCodeTreeScheduledTask , Scheduled Task Start...");
			try
			{
				this.emm = this.serviceContext.allocatService(EMM.class);

				List<CodeItemInfo> codeItemInfoList = this.emm.listAllCodeItemInfoByMaster(this.codeObjectInfo.getGuid(), null);
				if (!SetUtils.isNullList(codeItemInfoList))
				{
					for (CodeItemInfo codeItemInfo : codeItemInfoList)
					{
						if (!StringUtils.isGuid(codeItemInfo.getParentGuid()))
						{
							TreeDataRelation relation = new TreeDataRelation();
							relation.setDataGuid(codeItemInfo.getGuid());
							relation.setSubDataGuid(codeItemInfo.getGuid());
							relation.setDataType(TreeDataRelation.DATATYPE_CODEITEM);
							DataServer.getSystemDataService().delete(TreeDataRelation.class, relation, "deleteBy");
						}
					}

					for (CodeItemInfo codeItemInfo : codeItemInfoList)
					{
						String codeGuid = codeItemInfo.getGuid();
						String parentCodeGuid = codeItemInfo.getParentGuid();
						if (!StringUtils.isGuid(codeItemInfo.getParentGuid()))
						{
							parentCodeGuid = codeGuid;
						}

						TreeDataRelation relation = new TreeDataRelation();
						relation.setDataGuid(parentCodeGuid);
						relation.setSubDataGuid(codeGuid);
						relation.setDataType(TreeDataRelation.DATATYPE_CODEITEM);
						DataServer.getSystemDataService().save(relation);
					}
				}
			}
			catch (Throwable e)
			{
				DynaLogger.error("run send mail:", e);
			}
			finally
			{
				SecurityContextHolder.clearContext();
				if (this.emm != null)
				{
					this.serviceContext.releaseService(this.emm);
				}
			}
			DynaLogger.debug("QueuedTaskScheduler Scheduled [Class]SaveCodeTreeScheduledTask , Scheduled Task End...");
		}
	}
}
