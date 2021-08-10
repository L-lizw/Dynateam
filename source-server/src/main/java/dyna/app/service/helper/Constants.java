/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 常量类
 * caogc 2010-8-31
 */
package dyna.app.service.helper;

import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.common.dto.aas.Group;
import dyna.common.exception.ServiceRequestException;
import dyna.net.security.signature.Signature;
import dyna.net.service.brs.AAS;

/**
 * @author caogc
 * 
 */
public class Constants
{
	public static final String	FIELD_UHASBOM			= "UHasBOM";

	/**
	 * 是否需要权限判断的标识符,主要在RelationStub中使用
	 */
	public static final boolean	IS_NEED_AUTHORITY		= false;

	/**
	 * 表示SQL排序的静态常量
	 */
	public static final String	ORDER_BY				= "ORDERBY";

	/**
	 * isOwnerOnly为true只允许检出者操作，false允许其他人操作,主要在CheckStub中使用
	 */
	public static final boolean	IS_OWN_ONLY				= true;

	/**
	 * 表示编码规则中YEAR的所取位数为2
	 */
	public static final String	NUMBERING_YEAR_TWO		= "2";

	/**
	 * 表示编码规则中YEAR的所取位数为4
	 */
	public static final String	NUMBERING_YEAR_FOUR		= "4";

	/**
	 * 表示编码规则中MONTH或者DAY的所取位数为1位时前面补0
	 */
	public static final String	NUMBERING_MONTH_DAY_TWO	= "2";

	/**
	 * 判断是否超级管理员 超级管理员不用判断权限
	 * 
	 * @param <T>
	 * @param isCheckAuth
	 * @param service
	 * @return true 要判断权限，false 不用判断权限
	 */
	public static <T extends DataAccessService> boolean isSupervisor(boolean isCheckAuth, T service)
	{
		if (!isCheckAuth)
		{
			return false;
		}

		try
		{
			if (Signature.SYSTEM_INTERNAL_USERID.equals(service.getUserSignature().getUserId()))
			{
				return false;
			}

			Group group = service.getServiceInstance(AAS.class, service.getUserSignature().getCredential()).getGroup(service.getUserSignature().getLoginGroupGuid());

			if (group != null && group.isAdminGroup())
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return true;
		}

		return true;
	}

	/**
	 * 判断ID和NAME是否包含$,如果包含并抛异常
	 * 
	 * @param object
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("unchecked")
	public static void isContain$(Object object) throws ServiceRequestException
	{
		Map<String, Object> map = (Map<String, Object>) object;
		if (map.get("NAME") != null && ((String) map.get("NAME")).contains("$") || map.get("ID") != null && ((String) map.get("ID")).contains("$"))
		{
			throw new ServiceRequestException("ID_APP_ID_NAME_ILLEGAL", "name or id illegal");
		}
	}
}
