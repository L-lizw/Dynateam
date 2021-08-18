/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RuleReviseStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.slc;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.ConfigRuleRevise;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ReviseSeriesRuleEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wanglei
 * 
 */
@Component
public class RuleReviseStub extends AbstractServiceStub<SLCImpl>
{
	private static ConfigRuleRevise	configRuleRevise	= null;

	protected ConfigRuleRevise getConfigRuleRevise() throws ServiceRequestException
	{
		if (configRuleRevise != null)
		{
			return configRuleRevise;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		try
		{
			configRuleRevise = sds.queryObject(ConfigRuleRevise.class, filter);

			if (configRuleRevise == null)
			{
				configRuleRevise = new ConfigRuleRevise();
				configRuleRevise.setType(ReviseSeriesRuleEnum.LETTER);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return configRuleRevise;
	}

	protected ConfigRuleRevise saveConfigRuleRevise(ConfigRuleRevise configRuleRevise) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		boolean isCreate = false;
		try
		{
			String configRuleReviseGuid = configRuleRevise.getGuid();

			String operatorGuid = this.stubService.getOperatorGuid();

			configRuleRevise.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (!StringUtils.isGuid(configRuleReviseGuid))
			{
				isCreate = true;
				configRuleRevise.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			String ret = sds.save(configRuleRevise);

			if (isCreate)
			{
				configRuleReviseGuid = ret;
				configRuleRevise.setGuid(configRuleReviseGuid);
			}

			RuleReviseStub.configRuleRevise = configRuleRevise;

			return configRuleRevise;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteConfigRuleRevise(String guid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.delete(ConfigRuleRevise.class, guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
