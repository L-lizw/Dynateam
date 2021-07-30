/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SignatureFactory
 * Wanglei 2010-4-16
 */
package dyna.net.security.signature;

import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 *
 */
public class SignatureFactory
{

	public static Signature createSignature(String userId, String userName, String userGuid,//
			String loginGroupId, String groupName, String loginGroupGuid,//
			String loginRoleId, String roleName, String loginRoleGuid, //
			String ip, ApplicationTypeEnum appType, LanguageEnum lang,String loginGroupBMGuid,String loginGroupBMNAME)
	{
		Signature signature = new UserSignature(userId, userName, userGuid,//
				loginGroupId, groupName, loginGroupGuid, //
				loginRoleId, roleName, loginRoleGuid, //
				appType, lang,loginGroupBMGuid,loginGroupBMNAME);
		signature.setIPAddress(StringUtils.getString(ip, Signature.LOCAL_IP));
		return signature;
	}
	
	public static Signature createSignature(String userId, String userName, String userGuid,//
			String loginGroupId, String groupName, String loginGroupGuid,//
			String loginRoleId, String roleName, String loginRoleGuid, //
			String ip, ApplicationTypeEnum appType, LanguageEnum lang,String loginGroupBMGuid,String loginGroupBMNAME,String loginGroupBMTitle)
	{
		Signature signature = new UserSignature(userId, userName, userGuid,//
				loginGroupId, groupName, loginGroupGuid, //
				loginRoleId, roleName, loginRoleGuid, //
				appType, lang,loginGroupBMGuid,loginGroupBMNAME,loginGroupBMTitle);
		signature.setIPAddress(StringUtils.getString(ip, Signature.LOCAL_IP));
		return signature;
	}

	public static Signature createSignature(String moduleID)
	{
		Signature signature = new ModuleSignature(moduleID);
		signature.setIPAddress(Signature.LOCAL_IP);
		return signature;
	}
}
