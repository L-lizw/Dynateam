/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BRMImp
 * wangweixia 2012-7-13
 */
package dyna.app.service.brs.brm;

import dyna.app.service.BusinessRuleService;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.ReplaceConfig;
import dyna.common.dto.ReplaceSearchConf;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ReplaceRangeEnum;
import dyna.common.systemenum.ReplaceStatusEnum;
import dyna.common.systemenum.ReplaceTypeEnum;
import dyna.net.service.brs.*;
import dyna.net.service.das.MSRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * BRM BOM REPLACE MANAGER :bom 取/替代管理
 * 
 * @author wangweixia
 * 
 */
@Service
public class BRMImpl extends BusinessRuleService implements BRM
{
	@Autowired
	private ReplaceQueryStub	replaceQueryStub	;
	@Autowired
	private ReplaceObjectStub	replaceObjectStub	;
	private boolean				replaceControl;			// 取替代管控

	@Override
	public void init()
	{
		super.init();
		replaceControl = "true".equalsIgnoreCase(this.getServiceDefinition().getInitParameter("ReplaceControl"));
	}

	/**
	 * @return the replaceObjectStub
	 */
	public ReplaceQueryStub getReplaceQueryStub()
	{
		return replaceQueryStub;
	}

	/**
	 * @return the replaceObjectStub
	 */
	public ReplaceObjectStub getReplaceObjectStub()
	{
		return replaceObjectStub;
	}

	protected synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized BOMS getBOMS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOMS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized WFI getWFI() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BRM#listReplaceDataBySearch(dyna.common.bean.data.system.ReplaceSearchConf)
	 */
	@Override
	public List<FoundationObject> listReplaceDataBySearch(ReplaceSearchConf replaceSearchConf, int pageNum, int pageSize) throws ServiceRequestException
	{
		return this.getReplaceQueryStub().listReplaceDataBySearch(replaceSearchConf, pageNum, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BRM#listReplaceDataByRang(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.systemenum.ReplaceRangeEnum)
	 */
	@Override
	public List<FoundationObject> listReplaceDataByRang(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ReplaceRangeEnum rang, ReplaceTypeEnum type,
			String bomViewName, String bomKey, boolean isContainInvalidDate) throws ServiceRequestException
	{
		return this.getReplaceQueryStub().listReplaceDataByRang(masterItemObjectGuid, componentItemObjectGuid, rang, type, bomViewName, bomKey, isContainInvalidDate, true);
	}

	@Override
	public List<FoundationObject> listReplaceDataByRang(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, ReplaceRangeEnum rang,
			ReplaceTypeEnum type, String bomViewName, String bomKey, boolean isContainInvalidDate) throws ServiceRequestException
	{
		return this.getReplaceQueryStub().listReplaceDataByRang(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, rang, type, bomViewName, bomKey,
				isContainInvalidDate, true);
	}

	@Override
	public List<FoundationObject> listReplaceDataByRangByStatus(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ReplaceRangeEnum rang, ReplaceTypeEnum type,
			String bomViewName, String bomKey, boolean isContainInvalidDate, ReplaceStatusEnum status) throws ServiceRequestException
	{
		return this.getReplaceQueryStub().listReplaceDataByRang(masterItemObjectGuid, componentItemObjectGuid, rang, type, bomViewName, bomKey, isContainInvalidDate, true);
	}

	@Override
	public FoundationObject createReplaceData(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getReplaceObjectStub().createReplaceData(foundationObject);
	}

	@Override
	public void deleteReplaceData(ObjectGuid ObjectGuid) throws ServiceRequestException
	{
		this.getReplaceObjectStub().deleteReplaceData(ObjectGuid);
	}

	@Override
	public FoundationObject saveReplaceData(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getReplaceObjectStub().saveReplaceData(foundationObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.BRM#createFoundationObject()
	 */
	@Override
	public FoundationObject newFoundationObject(ReplaceRangeEnum range, ReplaceTypeEnum type) throws ServiceRequestException
	{

		return this.getReplaceObjectStub().newFoundationObject(range, type);
	}

	@Override
	public FoundationObject saveObject(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getReplaceObjectStub().saveObject(foundationObject);
	}

	@Override
	public List<FoundationObject> listReferenceItem(ObjectGuid componentItemObjectGuid) throws ServiceRequestException
	{
		return this.getReplaceQueryStub().listReferenceItem(componentItemObjectGuid);
	}

	@Override
	public void exchangeRSNumber(ObjectGuid frontObjectGuid, ObjectGuid laterObjectGuid) throws ServiceRequestException
	{
		this.getReplaceObjectStub().exchangeRSNumber(frontObjectGuid, laterObjectGuid);

	}

	@Override
	public boolean checkCreateReplaceRelation(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getReplaceObjectStub().checkCreateReplaceData(foundationObject);
	}

	@Override
	public boolean checkCreateReplaceRelation(ObjectGuid masterItemObjectGuid, ObjectGuid componentItemObjectGuid, ObjectGuid rsItemObjectGuid, String bomViewName, String bomKey)
			throws ServiceRequestException
	{
		return this.getReplaceObjectStub().checkCreateReplaceData(masterItemObjectGuid, componentItemObjectGuid, rsItemObjectGuid, bomViewName, bomKey);
	}

	@Override
	public void batchDealReplaceApply(String procRtGuid) throws ServiceRequestException
	{
		getReplaceObjectStub().batchDealReplaceApply(procRtGuid);
	}

	@Override
	public List<FoundationObject> listRepalcedByRsItem(ObjectGuid rsItemObjectGuid, ReplaceRangeEnum range, ReplaceTypeEnum type) throws ServiceRequestException
	{
		return this.getReplaceQueryStub().listRepalcedByRsItem(rsItemObjectGuid, range, type, true);
	}

	@Override
	public void deleteReplaceData(ObjectGuid objectGuid, ObjectGuid StructureObjGuid, String bomViewName) throws ServiceRequestException
	{
		this.getReplaceObjectStub().deleteReplaceData(objectGuid, StructureObjGuid, bomViewName);
	}

	@Override
	public FoundationObject createReplace(FoundationObject foundationObject) throws ServiceRequestException
	{
		return this.getReplaceObjectStub().createReplace(foundationObject);
	}

	@Override
	public ReplaceConfig getReplaceConfig() throws ServiceRequestException
	{
		return this.getReplaceObjectStub().getReplaceConfig();
	}

	@Override
	public void updateReplaceConfig(ReplaceConfig replaceConfig) throws ServiceRequestException
	{
		this.getReplaceObjectStub().updateReplaceConfig(replaceConfig);
	}

	@Override
	public boolean isReplaceControl() throws ServiceRequestException
	{
		return replaceControl;
	}

	public void updateBomRsFlag(FoundationObject replaceData) throws ServiceRequestException
	{
		this.getReplaceObjectStub().updateBomRsFlag(replaceData);
	}

	@Override
	public boolean checkReplaceDate(Date effectiveDate, Date invalidDate, boolean checkEffectiveDate, boolean checkExpireDate) throws ServiceRequestException
	{
		return this.getReplaceObjectStub().checkDate(effectiveDate, invalidDate, null, checkEffectiveDate, checkExpireDate);
	}
}
