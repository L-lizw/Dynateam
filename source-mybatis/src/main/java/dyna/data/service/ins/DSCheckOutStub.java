/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 检出的实现
 * JiangHL 2011-5-10
 */
package dyna.data.service.ins;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.Session;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataExceptionSQL;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检出的实现
 * 
 * @author JiangHL
 */
public class DSCheckOutStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	@Autowired
	private FoundationObjectMapper      foundationObjectMapper;
	/**
	 * @param context
	 * @param sd
	 * @throws DynaDataException
	 */
	public DSCheckOutStub(DataServerContext context, InstanceServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 1、bomview和view 直接检出 2、库中的实例 首先根据源实例创建检出的实例，并把检出实例的sourceGuid设置为源实例的guid
	 * 同时设置两条数据的检出状态，并建立相关关联关系
	 * 
	 * @param foundationObject
	 * @param checkOutUser
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	protected FoundationObject checkout(FoundationObject foundationObject, String checkOutUser, boolean isCheckAcl, String sessionId, String fixTranId)
			throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		String className = foundationObject.getObjectGuid().getClassName();
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		boolean isView = classObject.hasInterface(ModelInterfaceEnum.IViewObject);
		boolean isBOMView = classObject.hasInterface(ModelInterfaceEnum.IBOMView);

		// 如果数据已经删除抛出异常 --此方法内部会抛出异常
		this.stubService.getSystemFieldInfo(foundationObject.getObjectGuid().getGuid(), foundationObject.getObjectGuid().getClassGuid(), false, sessionId);
		// 如果是私人数据抛出异常
		if (!isView && !isBOMView && !foundationObject.isCommited())
		{
			throw new DynaDataExceptionAll("CHECKOUT error : private data can't checkout .", null, DataExceptionEnum.DS_CHECKOUT_PRIVATE_DATA, foundationObject.getId());
		}
		// authority check
		if ((ISCHECKACL && isCheckAcl) && !aclService.hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.CHECKOUT, sessionId))
		{
			throw new DynaDataExceptionAll("CHECKOUT error : no authority .", null, DataExceptionEnum.DS_NO_CHECKOUT_AUTH, foundationObject.getId());
		}

		String revisionGuid = foundationObject.getObjectGuid().getGuid();
		Date sysDate = DateFormat.getSysDate();
		String sysTableName = DataServer.getDSCommonService().getTableName(className);
		try
		{
//			DataServer.getTransactionManager().startTransaction(fixTranId);
			// 创建新的实例 同时创建_r _rg表
			this.checkOut(revisionGuid, //
					className, //
					checkOutUser, //
					isView, // 是否自动检出
					foundationObject.getUpdateTime(), //
					Integer.parseInt(classObject.getIterationLimit()), //
					DataServer.getDSCommonService().getSession(sessionId));

			FoundationObject simpleObj = (FoundationObject) this.dynaObjectMapper.lockForCheckout(revisionGuid,sysTableName);

			if (isView || isBOMView)
			{
				foundationObject.put(SystemClassFieldEnum.UPDATEUSER.getName(), checkOutUser);
				foundationObject.put(SystemClassFieldEnum.ITERATIONID.getName(), new BigDecimal(foundationObject.getIterationId() + 1));
				foundationObject.put(SystemClassFieldEnum.ISCHECKOUT.getName(), "Y");
				if (!isView)
				{
					foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), sysDate);
				}
				foundationObject.put(SystemClassFieldEnum.CHECKOUTUSER.getName(), checkOutUser);
				foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), simpleObj.getUpdateTime());
				foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), simpleObj.getCheckedOutTime());
//				DataServer.getTransactionManager().commitTransaction();

				if (isView)
				{
					ViewObject viewObject = new ViewObject();
					viewObject.sync(foundationObject);

					return viewObject;
				}
				else
				{
					BOMView bomView = new BOMView();
					bomView.sync(foundationObject);

					return bomView;
				}
			}

			// create bi_file
			Map<String, Object> filter = new HashMap<>();
			filter.put(DSSFileInfo.REVISION_GUID, revisionGuid);
			filter.put(DSSFileInfo.ITERATION_ID, foundationObject.getIterationId());
			List<DSSFileInfo> fileList = DataServer.getSystemDataService().query(DSSFileInfo.class, filter, "selectForCopy");
			if (fileList != null)
			{
				for (DSSFileInfo info : fileList)
				{
					info.setGuid(null);
					info.setRevision(foundationObject.getObjectGuid().getGuid());
					info.setIterationId(foundationObject.getIterationId() + 1);
					DataServer.getSystemDataService().save(info);
				}
			}
			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("GUID", revisionGuid);
			fileMap.put("TABLENAME", DataServer.getDSCommonService().getTableName(classObject.getName()));
			this.dynaObjectMapper.updatePrimaryFileToFoundation(fileMap);

			Map<String, String> fileInfo = (Map<String, String>) this.dynaObjectMapper.selectFoundationFile(DataServer.getDSCommonService().getTableName(classObject.getName()),revisionGuid);
			if (!SetUtils.isNullMap(fileInfo))
			{
				foundationObject.put(SystemClassFieldEnum.FILEGUID.getName(), fileInfo.get("FILEGUID"));
				foundationObject.put(SystemClassFieldEnum.FILETYPE.getName(), fileInfo.get("FILETYPE"));
				foundationObject.put(SystemClassFieldEnum.FILENAME.getName(), fileInfo.get("FILENAME"));
			}

			fileMap = new HashMap<>();
			fileMap.put("GUID", revisionGuid);
			fileMap.put("TABLENAME", DataServer.getDSCommonService().getRealBaseTableName(classObject.getName()) + "_I");
			this.dynaObjectMapper.updatePrimaryFileToIteration(fileMap);

			foundationObject.put(SystemClassFieldEnum.UPDATEUSER.getName(), checkOutUser);
			foundationObject.put(SystemClassFieldEnum.ITERATIONID.getName(), new BigDecimal(foundationObject.getIterationId() + 1));
			foundationObject.put(SystemClassFieldEnum.ISCHECKOUT.getName(), "Y");
			foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), sysDate);
			foundationObject.put(SystemClassFieldEnum.CHECKOUTUSER.getName(), checkOutUser);
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), simpleObj.getUpdateTime());
			foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), simpleObj.getCheckedOutTime());
			
			FoundationObjectImpl retObject = (FoundationObjectImpl) foundationObject.getClass().getConstructor().newInstance();
			retObject.sync(foundationObject);

//			DataServer.getTransactionManager().commitTransaction();
			return retObject;
		}
		catch (SQLException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw new DynaDataExceptionSQL("checkout error : checkout SQL exception . sessionId = " + sessionId, e, DataExceptionEnum.DS_CHECKOUT, foundationObject.getId());
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("checkout error : checkout exception . sessionId = " + sessionId, e, DataExceptionEnum.DS_CHECKOUT, foundationObject.getId());
		}
	}

	public void checkOut(String foundationGuid, String className, String checkoutUser, boolean isAutoCheckout, Date updatetime, int iterationLimit, Session session)
			throws SQLException, ServiceRequestException
	{
		String tableName = DataServer.getDSCommonService().getTableName(className);

		Map<String, Object> param = new HashMap<>();
		param.put("TABLENAME", tableName);
		param.put("GUID", foundationGuid);
		FoundationObject foundationObject = (FoundationObject) this.dynaObjectMapper.lockForCheckout(foundationGuid,tableName);

		// 在工作流
		String ret = (String) this.dynaObjectMapper.isLockInWF(foundationGuid);
		if (!StringUtils.isNullString(ret) && "Y".equals(ret))
		{
			throw new DynaDataExceptionAll("checkout error : the data is in workFlow .", null, DataExceptionEnum.DS_WF_DATA, foundationGuid);
		}

		// 非wip、ecp状态
		if (foundationObject.getStatus() != SystemStatusEnum.WIP && foundationObject.getStatus() != SystemStatusEnum.ECP)
		{
			throw new DynaDataExceptionAll("checkout error : the data is not in crt、wip、ecp status.", null, DataExceptionEnum.DS_CHECKOUT_STATUS_ERROR, foundationGuid);
		}

		// 被改过（updatetime改变了）
		if (updatetime == null || !DateFormat.formatYMDHMS(foundationObject.getUpdateTime()).equals(DateFormat.formatYMDHMS(updatetime)))
		{
			throw new DynaDataExceptionAll("checkout error : the data is modified.", null, DataExceptionEnum.DS_CHECKOUT_ERROR_DATAMODIFIED, foundationObject.getId());
		}

		// 已经检出
		if (foundationObject.isCheckOut())
		{
			throw new DynaDataExceptionAll("checkout error : the data is already checkouted.", null, DataExceptionEnum.DS_CHECKOUT_ERROR_DATACHECKOUTED, foundationObject.getId());
		}

		// 创建版序
		this.stubService.getIterationStub().createIterationData(foundationGuid, foundationObject.getIterationId(), className, iterationLimit, session);

		// 更新对象
		param.clear();
		param.put("TABLENAME", DataServer.getDSCommonService().getTableName(className));
		param.put("CHECKOUTUSERGUID", session.getUserGuid());
		param.put("ISAUTOCHECKOUT", isAutoCheckout);
		param.put("FOUNDATIONGUID", foundationGuid);
		param.put("UPDATETIME", foundationObject.getUpdateTime());
		int count = this.foundationObjectMapper.setInstanceCheckout(param);
		if (count == 0)
		{
			throw new DynaDataExceptionAll("checkout error : the data is modified.", null, DataExceptionEnum.DS_CHECKOUT_ERROR_DATAMODIFIED, foundationObject.getId());
		}
	}
}
