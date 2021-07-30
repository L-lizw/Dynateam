/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Signature
 * 签名
 * Wanglei 2010-3-24
 */
package dyna.net.security.signature;

import java.util.Date;

import dyna.common.Identifiable;


/**
 * 签名
 * 
 * @author Wanglei
 * 
 */
public interface Signature extends Identifiable
{

	public static final String	MODULE_DATA_SERVER		= "MODULE.DATA.SERVER";
	public static final String	MODULE_APP_SERVER		= "MODULE.APP.SERVER";
	public static final String	MODULE_MODELER			= "MODULE.MODELER";
	public static final String	MODULE_DSS				= "MODULE.DSS";
	public static final String	MODULE_CLIENT			= "MODULE.CLIENT";

	public static final String	SYSTEM_INTERNAL_USERID	= "SYSTEM.INTERNAL";
	public static final String	SYSTEM_INTERNAL_SESSION	= "SYSTEM.INTERNAL.SESSION";
	public static final String	SYSTEM_INTERNAL_DSS		= "SYSTEM.INTERNAL.DSS";

	public static final String	SYSTEM_INTERNAL			= "SYSTEM.INTERNAL";
	public static final String	LOCAL_IP				= "127.0.0.1";

	public String getIPAddress();

	public void setIPAddress(String ip);

	public Date getTimeStamp();

	public String getCredential();

	public void setCredential(String credential);
}
