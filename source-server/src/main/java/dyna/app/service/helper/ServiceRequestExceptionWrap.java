/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceRequestExptionWrap 服务请求异常包装
 * Wanglei 2011-8-5
 */
package dyna.app.service.helper;

import dyna.app.service.DataAccessService;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.brs.EMM;
import dyna.net.service.das.MSRM;

/**
 * 服务请求异常包装
 * 
 * @author Wanglei
 * 
 */
public class ServiceRequestExceptionWrap
{
	public static ServiceRequestException createByDynaDataException(DataAccessService service, DynaDataException e,
			Object... args)
	{
		Signature signature = service.getSignature();
		if (!(signature instanceof UserSignature))
		{
			return ServiceRequestException.createByDynaDataException(e, args);
		}

		UserSignature us = (UserSignature) signature;
		LanguageEnum lang = us.getLanguageEnum();
		Object[] expArgs = e.getArgs();

		DataExceptionEnum ee = e.getDataExceptionEnum();
		if (ee == DataExceptionEnum.DS_VALUE_TOO_LARGE && expArgs != null && expArgs.length == 4)
		{
			String className = (String) expArgs[0];
			String fieldName = (String) expArgs[1];

			try
			{
				EMM emm = service.getRefService(EMM.class);

				UIField uiField = emm.getUIFieldByName(className, fieldName);
				if (uiField != null)
				{
					expArgs[1] = uiField.getTitle(lang);
					e.setArgs(expArgs);
				}
				else if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[1] = msrm.getMSRString(SystemClassFieldEnum.ID.getDescription(), service
							.getUserSignature().getLanguageEnum().toString());
				}
				else if (SystemClassFieldEnum.ALTERID.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[1] = msrm.getMSRString(SystemClassFieldEnum.ALTERID.getDescription(),
							service.getUserSignature().getLanguageEnum().toString());
				}
				else if (SystemClassFieldEnum.NAME.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[1] = msrm.getMSRString(SystemClassFieldEnum.NAME.getDescription(), service
							.getUserSignature().getLanguageEnum().toString());
				}
				else if (SystemClassFieldEnum.CLASSIFICATION.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[1] = msrm.getMSRString(
							SystemClassFieldEnum.CLASSIFICATION.getDescription(), service
									.getUserSignature().getLanguageEnum().toString());
				}
			}
			catch (Exception e1)
			{
				return ServiceRequestException.createByDynaDataException(e, args);
			}
		}
		else if (ee == DataExceptionEnum.DS_VALUE_TOO_LARGE && expArgs != null && expArgs.length == 3)
		{
			String fieldName = (String) expArgs[0];

			try
			{
				// EMM emm = service.getRefService(EMM.class);

				// UIField uiField = emm.getUIFieldByName(className, fieldName);
				// if (uiField != null)
				// {
				// expArgs[0] = uiField.getTitle(lang);
				// e.setArgs(expArgs);
				// }
				// else
				if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[0] = msrm.getMSRString(SystemClassFieldEnum.ID.getDescription(), service
							.getUserSignature().getLanguageEnum().toString());
				}
				else if (SystemClassFieldEnum.ALTERID.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[0] = msrm.getMSRString(SystemClassFieldEnum.ALTERID.getDescription(),
							service.getUserSignature().getLanguageEnum().toString());
				}
				else if (SystemClassFieldEnum.NAME.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[0] = msrm.getMSRString(SystemClassFieldEnum.NAME.getDescription(), service
							.getUserSignature().getLanguageEnum().toString());
				}
				else if (SystemClassFieldEnum.CLASSIFICATION.getName().equalsIgnoreCase(fieldName))
				{
					MSRM msrm = service.getRefService(MSRM.class);

					expArgs[0] = msrm.getMSRString(
							SystemClassFieldEnum.CLASSIFICATION.getDescription(), service
									.getUserSignature().getLanguageEnum().toString());
				}
			}
			catch (Exception e1)
			{
				return ServiceRequestException.createByDynaDataException(e, args);
			}

		}
		return ServiceRequestException.createByDynaDataException(e, args);
	}

}
