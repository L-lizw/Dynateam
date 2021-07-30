/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UserSignature
 * Wanglei 2010-4-15
 */
package dyna.net.security.signature;

import java.io.Serializable;

import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 *
 */
public class UserSignature extends AbstractSignature implements Serializable
{

	private static final long	serialVersionUID	= -6919242005768889861L;

	private String				userId				= null;
	private String				userName			= null;
	private String				loginGroupId		= null;
	private String				loginGroupName		= null;
	private String				loginRoleId			= null;
	private String				loginRoleName		= null;
	private String				userGuid			= null;
	private String				loginGroupGuid		= null;
	private String				loginRoleGuid		= null;
	private String				loginGroupBMGuid	= null;
	private String				loginGroupBMNAME	= null;
	private String				loginGroupBMTITLE	= null;
	private LanguageEnum		lang				= null;
	private ApplicationTypeEnum	appType				= null;

	public UserSignature(String userId, String userName, String userGuid, //
			String loginGroupId, String groupName, String loginGroupGuid,//
			String loginRoleId, String roleName, String loginRoleGuid, //
			ApplicationTypeEnum appType, LanguageEnum lang,String loginGroupBMGuid,String loginGroupBMNAME)
	{
		super();
		this.userId = userId;
		this.userName = userName;
		this.userGuid = userGuid;

		this.loginGroupId = loginGroupId;
		this.loginGroupName = groupName;
		this.loginGroupGuid = loginGroupGuid;

		this.loginRoleId = loginRoleId;
		this.loginRoleName = roleName;
		this.loginRoleGuid = loginRoleGuid;

		this.appType = appType;
		this.lang = lang;

		this.loginGroupBMGuid = loginGroupBMGuid;
		this.loginGroupBMNAME = loginGroupBMNAME;
	}
	
	public UserSignature(String userId, String userName, String userGuid, //
			String loginGroupId, String groupName, String loginGroupGuid,//
			String loginRoleId, String roleName, String loginRoleGuid, //
			ApplicationTypeEnum appType, LanguageEnum lang,String loginGroupBMGuid,String loginGroupBMNAME,String loginGroupBMTitle)
	{
		super();
		this.userId = userId;
		this.userName = userName;
		this.userGuid = userGuid;

		this.loginGroupId = loginGroupId;
		this.loginGroupName = groupName;
		this.loginGroupGuid = loginGroupGuid;

		this.loginRoleId = loginRoleId;
		this.loginRoleName = roleName;
		this.loginRoleGuid = loginRoleGuid;

		this.appType = appType;
		this.lang = lang;

		this.loginGroupBMGuid = loginGroupBMGuid;
		this.loginGroupBMNAME = loginGroupBMNAME;
		this.loginGroupBMTITLE = loginGroupBMTitle;
	}

	/**
	 * @return the userId
	 */
	public String getUserGuid()
	{
		return this.userGuid;
	}

	public String getUserId()
	{
		return this.userId;
	}

	/**
	 * @return the owningGroupId
	 */
	public String getLoginGroupGuid()
	{
		return this.loginGroupGuid;
	}

	public String getLoginGroupId()
	{
		return this.loginGroupId;
	}

	/**
	 * @return the owningRoleId
	 */
	public String getLoginRoleGuid()
	{
		return this.loginRoleGuid;
	}

	public String getLoginRoleId()
	{
		return this.loginRoleId;
	}

	public LanguageEnum getLanguageEnum()
	{
		return this.lang;
	}

	/**
	 * @return the appType
	 */
	public ApplicationTypeEnum getApplicationType()
	{
		return this.appType;
	}

	/**
	 * @param appType
	 *            the appType to set
	 */
	public void setApplicationType(ApplicationTypeEnum appType)
	{
		this.appType = appType;
	}

	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return this.userName;
	}

	/**
	 * @return the loginGroupName
	 */
	public String getLoginGroupName()
	{
		return this.loginGroupName;
	}

	/**
	 * @return the loginRoleName
	 */
	public String getLoginRoleName()
	{
		return this.loginRoleName;
	}

	@Override
	public String toString()
	{
		return "<UserSignature> user: [" + this.userId + "]" + " group: [" + this.loginGroupId + "]" + " role: ["
				+ this.loginRoleId + "] " + super.toString();
	}

	public String getLoginGroupBMGuid()
	{
		return loginGroupBMGuid;
	}

	public String getLoginGroupBMNAME()
	{
		return loginGroupBMNAME;
	}

	public String getLoginGroupBMTITLE()
	{
		return loginGroupBMTITLE;
	}
	
	public String getLoginGroupBMTITLE(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(getLoginGroupBMTITLE(), lang.getType());
	}
}
