/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FRevisionStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.boas;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author Wanglei
 * 
 */
@Component
public class FRevisionStub extends AbstractServiceStub<BOASImpl>
{
	public String getInitRevisionId(int startRevisionIdSequence) throws ServiceRequestException
	{
		return DataServer.getInstanceService().getFirstRevisionId(startRevisionIdSequence);
	}

	/**
	 * 獲取下一個版本 如：如果參數是ABC,那麼返回值是ABD 如果參數是123,那麼反獲值是124 <br>
	 * 取最大版本号的下一个版本
	 * 
	 * @param revisionId
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getNextRevision(ObjectGuid objectGuid, int custStartRevisionIdSequence) throws ServiceRequestException
	{

		try
		{
			return DataServer.getInstanceService().getNewRevisionId(objectGuid);
		}
		catch (DynaDataException e)
		{
			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_PROCEDURE_20011)
			{
				throw new ServiceRequestException("ID_DS_PROCEDURE_20011", "revision has been all used");
			}
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 获取对象的所有版本
	 * 
	 * @param foundationObject
	 *            FoundationObject对象
	 * @param boas
	 * @param edap
	 * @param emm
	 * @return 对象的历史版本列表
	 * @throws ServiceRequestException
	 * @author caogc
	 */
	protected List<FoundationObject> listObjectRevisionHistoryForMaster(ObjectGuid objectGuid) throws ServiceRequestException
	{
		try
		{
			String sessionId = this.stubService.getSignature().getCredential();

			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(objectGuid.getClassName(), null, false);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);

			// get the bmGuid
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			// get ui model object for this object.
			List<UIObjectInfo> uiObjectList = ((EMMImpl) this.stubService.getEMM()).listUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.FORM, true);

			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			searchCondition.addFilter(SystemClassFieldEnum.MASTERFK, objectGuid.getMasterGuid(), OperateSignEnum.EQUALS);
			searchCondition.addOrder(SystemClassFieldEnum.REVISIONIDSEQUENCE, false);
			searchCondition.setPageSize(100);
			// searchCondition.setLatestOnly(false);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);
			searchCondition.setIncludeOBS(true);
			List<FoundationObject> results = DataServer.getInstanceService().query(searchCondition, Constants.isSupervisor(true, this.stubService), sessionId);

			if (SetUtils.isNullList(results))
			{
				return null;
			}

			EMM emm = this.stubService.getEMM();
			// 装饰检索结果
			for (FoundationObject retObject : results)
			{
				DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), retObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(emm.getCodeFieldNamesInSC(searchCondition), retObject, emm, bmGuid);
			}

			DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), results, this.stubService.getEMM(), sessionId);

			return results;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	/**
	 * 获取对象的所有版本
	 * 
	 * @param foundationObject
	 *            FoundationObject对象
	 * @param boas
	 * @param edap
	 * @param emm
	 * @return 对象的历史版本列表
	 * @throws ServiceRequestException
	 * @author caogc
	 */
	protected List<FoundationObject> listObjectRevisionHistory(ObjectGuid objectGuid) throws ServiceRequestException
	{
		try
		{

			String sessionId = this.stubService.getSignature().getCredential();

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(objectGuid.getClassName(), null, false);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);

			// get the bmGuid
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();

			searchCondition.addFilter(SystemClassFieldEnum.MASTERFK, objectGuid.getMasterGuid(), OperateSignEnum.EQUALS);
			searchCondition.addOrder(SystemClassFieldEnum.REVISIONIDSEQUENCE, false);
			searchCondition.setPageSize(100);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			List<FoundationObject> results = DataServer.getInstanceService().query(searchCondition, Constants.isSupervisor(true, this.stubService), sessionId);

			if (SetUtils.isNullList(results))
			{
				return null;
			}

			EMM emm = this.stubService.getEMM();
			// 装饰检索结果
			for (FoundationObject retObject : results)
			{
				DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), retObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(emm.getCodeFieldNamesInSC(searchCondition), retObject, emm, bmGuid);
			}

			DecoratorFactory.decorateFoundationObject(emm.getObjectFieldNamesInSC(searchCondition), results, this.stubService.getEMM(), sessionId);

			return results;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	protected List<FoundationObject> listObjectRevisionHistoryOnlyWIP(ObjectGuid objectGuid) throws ServiceRequestException
	{

		try
		{
			String sessionId = this.stubService.getSignature().getCredential();

			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(objectGuid.getClassName(), null, false);

			// 根据ObjectGuid中的className或者classGuid一个的值，获取另外一个的值并赋给ObjectGuid
			ClassStub.decorateObjectGuid(searchCondition.getObjectGuid(), this.stubService);

			// get the bmGuid
			String bmGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
			// get ui model object for this object.
			List<UIObjectInfo> uiObjectList = ((EMMImpl) this.stubService.getEMM()).listUIObjectInCurrentBizModel(objectGuid.getClassName(), UITypeEnum.FORM, true);

			if (!SetUtils.isNullList(uiObjectList))
			{
				for (UIObjectInfo uiObject : uiObjectList)
				{
					searchCondition.addResultUIObjectName(uiObject.getName());
				}
			}

			searchCondition.addFilter(SystemClassFieldEnum.MASTERFK, objectGuid.getMasterGuid(), OperateSignEnum.EQUALS);
			searchCondition.addFilter(SystemClassFieldEnum.STATUS, SystemStatusEnum.WIP, OperateSignEnum.EQUALS);
			searchCondition.addOrder(SystemClassFieldEnum.CREATETIME, false);
			searchCondition.setPageSize(100);
			// searchCondition.setLatestOnly(false);
			searchCondition.setSearchRevisionTypeEnum(SearchRevisionTypeEnum.ISHISTORYREVISION);

			List<FoundationObject> results = DataServer.getInstanceService().query(searchCondition, Constants.isSupervisor(true, this.stubService), sessionId);

			if (SetUtils.isNullList(results))
			{
				return null;
			}

			EMM emm = this.stubService.getEMM();
			Set<String> fieldNameList = emm.getObjectFieldNamesInSC(searchCondition);
			// 装饰检索结果
			for (FoundationObject retObject : results)
			{
				DecoratorFactory.decorateFoundationObject(fieldNameList, retObject, emm, bmGuid, null);
				DecoratorFactory.decorateFoundationObjectCode(fieldNameList, retObject, emm, bmGuid);
			}

			DecoratorFactory.decorateFoundationObject(fieldNameList, results, this.stubService.getEMM(), sessionId);

			return results;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
	}

	/**
	 * 获取新建修订版的"建议版本"
	 * 
	 * @param objectGuid
	 * @return FoundationObject建议版本后的对象
	 * @throws ServiceRequestException
	 */
	protected FoundationObject prepareRevision(ObjectGuid objectGuid) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		BigDecimal startRevSequence = new BigDecimal("0");

		String revisionId = this.getNextRevision(objectGuid, startRevSequence.intValue());

		if (StringUtils.isNullString(revisionId))
		{
			throw new ServiceRequestException("ID_APP_NEXT_REVISIONID_NULL", "there is not next revisionid");
		}

		FoundationObject retFoundationObject = this.stubService.getObject(objectGuid);

		retFoundationObject.setRevisionId(revisionId);
		retFoundationObject.clear(SystemClassFieldEnum.CREATETIME.getName());
		retFoundationObject.clear(SystemClassFieldEnum.UPDATETIME.getName());
		retFoundationObject.resetObjectGuid();

		return retFoundationObject;
	}
}
