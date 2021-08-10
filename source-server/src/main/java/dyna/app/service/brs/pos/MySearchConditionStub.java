/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 自定义查询条件操作分支
 * WangLHB 2013-1-17
 */

package dyna.app.service.brs.pos;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.SaQueryPreference;
import dyna.common.dto.SaQueryPreferenceDetail;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义查询条件操作分支
 * 
 * @author WangLHB
 * 
 */
public class MySearchConditionStub extends AbstractServiceStub<POSImpl>
{

	protected MySearchConditionStub(ServiceContext context, POSImpl service)
	{
		super(context, service);
	}

	protected List<SaQueryPreferenceDetail> saveMySearchCondition(String classGuid, List<SaQueryPreferenceDetail> detailList) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		// String sessionId = this.stubService.getUserSignature().getCredential();

		if (StringUtils.isNullString(classGuid))
		{
			return null;
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			String operatorGuid = this.stubService.getOperatorGuid();
			SaQueryPreference saQueryPreference = this.getSaQueryPreference(classGuid);
			String masterGuid = null;

			if (saQueryPreference == null)
			{
				saQueryPreference = new SaQueryPreference();
				saQueryPreference.put(SystemObject.CREATE_USER_GUID, operatorGuid);
				saQueryPreference.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
				saQueryPreference.setUserGuid(operatorGuid);
				saQueryPreference.setClassfk(classGuid);
				masterGuid = sds.save(saQueryPreference);
			}
			else
			{
				masterGuid = saQueryPreference.getGuid();
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SaQueryPreferenceDetail.MASTERFK, masterGuid);
			sds.delete(SaQueryPreferenceDetail.class, filter, "deleteDetailByMaster");

			if (!SetUtils.isNullList(detailList))
			{
				int i = 0;
				for (SaQueryPreferenceDetail detail : detailList)
				{
					detail.setMasterfk(masterGuid);
					detail.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					detail.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
					detail.setSequence(i++);
					sds.save(detail);
				}
			}
			List<SaQueryPreferenceDetail> listMySearchConditionByMasterGuid = this.listMySearchConditionByMasterGuid(masterGuid);
//			DataServer.getTransactionManager().commitTransaction();
			return listMySearchConditionByMasterGuid;
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			throw ServiceRequestException.createByException("ID_APP_WF_UNKNOW_ERROR", e);
		}
	}

	private SaQueryPreference getSaQueryPreference(String classGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();

		String operatorGuid = this.stubService.getOperatorGuid();
		filter.put(SaQueryPreference.USERGUID, operatorGuid);
		filter.put(SaQueryPreference.CLASSFK, classGuid);
		List<SaQueryPreference> masterList = null;
		try
		{
			masterList = sds.query(SaQueryPreference.class, filter);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		if (!SetUtils.isNullList(masterList))
		{
			return masterList.get(0);
		}
		else
		{
			return null;
		}
	}

	protected List<SaQueryPreferenceDetail> listMySearchCondition(String classGuid) throws ServiceRequestException
	{
		SaQueryPreference saQueryPreference = this.getSaQueryPreference(classGuid);
		if (saQueryPreference != null)
		{
			return this.listMySearchConditionByMasterGuid(saQueryPreference.getGuid());
		}
		else
		{
			return new ArrayList<SaQueryPreferenceDetail>();
		}
	}

	private List<SaQueryPreferenceDetail> listMySearchConditionByMasterGuid(String masterGuid) throws ServiceRequestException
	{

		try
		{

			Map<String, Object> filter = new HashMap<String, Object>();

			SystemDataService sds = DataServer.getSystemDataService();
			filter.put(SaQueryPreferenceDetail.MASTERFK, masterGuid);
			List<SaQueryPreferenceDetail> detailList = sds.query(SaQueryPreferenceDetail.class, filter);
			return detailList;

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
