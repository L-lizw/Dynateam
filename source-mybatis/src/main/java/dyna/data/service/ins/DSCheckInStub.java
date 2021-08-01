/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 检入的实现
 * JiangHL 2011-5-10
 */
package dyna.data.service.ins;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.dto.Session;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.DateFormat;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataExceptionSQL;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 检入的实现
 * 
 * @author JiangHL
 */
public class DSCheckInStub extends DSAbstractServiceStub<InstanceServiceImpl>
{

	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public DSCheckInStub(DataServerContext context, InstanceServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 
	 * @param foundationObject
	 * @param isOwnerOnly
	 * @param sessionId
	 * @param fixTranId
	 * @throws DynaDataException
	 */
	protected FoundationObject checkin(FoundationObject foundationObject, boolean isOwnerOnly, String sessionId, String fixTranId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String sourceGuid = foundationObject.getObjectGuid().getGuid();

		try
		{
			Date currentDate = DateFormat.getSysDate();
			String retrunStr = this.checkIn(sourceGuid, //
					foundationObject.getObjectGuid().getClassGuid(), //
					isOwnerOnly ? session.getUserGuid() : null, //
					session.getUserGuid(), //
					foundationObject.getUpdateTime(), //
					currentDate,
					fixTranId);

			if (!"Y".equals(retrunStr))
			{
				throw new DynaDataExceptionAll("check in failed", null, DataExceptionEnum.DS_CHECKIN_DATA_LOST_ISOWNERONLY, foundationObject.getId());
			}
			
			String sysTableName = DataServer.getDSCommonService().getTableName(foundationObject.getObjectGuid().getClassGuid());
			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", sysTableName);
			param.put("GUID", sourceGuid);
			FoundationObject simpleObj = (FoundationObject) this.dynaObjectMapper.lockForCheckout(sourceGuid, sysTableName);

			foundationObject.put(SystemClassFieldEnum.ISCHECKOUT.getName(), "N");
			foundationObject.put(SystemClassFieldEnum.CHECKOUTUSER.getName(), null);
			foundationObject.put(SystemClassFieldEnum.CHECKOUTUSER.getName()+"NAME", null);
			foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), null);
			foundationObject.put(SystemClassFieldEnum.UPDATEUSER.getName(), session.getUserGuid());
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), currentDate);
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), simpleObj.getUpdateTime());
			foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), simpleObj.getCheckedOutTime());

			FoundationObjectImpl retObject = (FoundationObjectImpl) foundationObject.getClass().getConstructor().newInstance();
			retObject.sync(foundationObject);


			return retObject;
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("checkin() guid=" + sourceGuid, e, DataExceptionEnum.DS_CHECKIN, foundationObject.getId());
		}
	}

	private String checkIn(String foundationGuid, String classGuid, String checkoutUserGuid, String updateUserGuid, Date updateTime, Date currentDate, String fixTranId)
			throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(fixTranId);
			Map<String, Object> checkinMap = new HashMap<>();
			checkinMap.put("table", DataServer.getDSCommonService().getTableName(classGuid));
			checkinMap.put("GUID", foundationGuid);
			checkinMap.put("UPDATEUSER", updateUserGuid);
			checkinMap.put("UPDATETIME", updateTime);
			checkinMap.put("CURRENTTIME", currentDate);
			checkinMap.put("CHECKOUTUSER", checkoutUserGuid);
			int dataCount = this.dynaObjectMapper.checkin(checkinMap);
//			DataServer.getTransactionManager().commitTransaction();
			if (dataCount == 0)
			{
				return "N";
			}
			return "Y";
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw new ServiceRequestException("", null, e);
		}
	}
}
