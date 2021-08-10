/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CSAImpl Common Service Access implementation
 * Wanglei 2010-9-6
 */
package dyna.app.service.das;

import dyna.app.service.DataAccessService;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.SignatureFactory;
import dyna.net.service.das.CSA;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Common Service Access implementation
 * 
 * @author Wanglei
 * 
 */
public class CSAImpl extends DataAccessService implements CSA
{
	private static Set<String>	dssRegisterSet	= new HashSet<String>();
	private static Set<String>	smRegisterSet	= new HashSet<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.CSA#registerSFTP()
	 */
	@Override
	public String registerDSS() throws ServiceRequestException
	{
		CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		String clientIdentifier = null;
		if (authentication != null)
		{
			clientIdentifier = (String) authentication.getDetails();
			try
			{
				ModuleSignature signature = (ModuleSignature) cm.authenticate(clientIdentifier);
				if (signature != null && !Signature.MODULE_DSS.equals(signature.getModuleId()))
				{
					throw new ServiceRequestException("[CSA.registerDSS] access denied, only for dss module");
				}
			}
			catch (AuthorizeException e)
			{
				throw ServiceRequestException.createByException("", e);
			}
		}

		synchronized (Signature.SYSTEM_INTERNAL_DSS)
		{
			String dssc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_DSS);
			if (StringUtils.isNullString(dssc))
			{
				dssc = UUID.randomUUID().toString();
				cm.setModuleCredential(Signature.SYSTEM_INTERNAL_DSS, dssc);
				cm.bind(dssc, SignatureFactory.createSignature(Signature.SYSTEM_INTERNAL_DSS));

				if (clientIdentifier == null)
				{
					return dssc;
				}
			}

			if (clientIdentifier != null)
			{
				if (!dssRegisterSet.contains(clientIdentifier))
				{
					dssRegisterSet.add(clientIdentifier);
					return dssc;
				}
			}
			throw new ServiceRequestException("duplicate registDSS.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.CSA#registerSessionManager()
	 */
	@Override
	public String registerSessionManager() throws ServiceRequestException
	{
		CredentialManager cm = this.serviceContext.getServerContext().getCredentialManager();

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		String clientIdentifier = null;
		if (authentication != null)
		{
			clientIdentifier = (String) authentication.getDetails();
			try
			{
				ModuleSignature signature = (ModuleSignature) cm.authenticate(clientIdentifier);
				boolean isValidate = true;
				if (signature != null && !Signature.MODULE_CLIENT.equals(signature.getModuleId())
						&& !Signature.MODULE_DSS.equals(signature.getModuleId()))
				{
					/*try{
						ModelCADTypeEnum.valueOf(signature.getModuleId());
					}
					catch (Exception e)
					{
						isValidate = false;
					}*/

				}
				if(!isValidate){
					throw new ServiceRequestException(
					"[CSA.registerSessionManager] access denied, only for client module");
				}
			}
			catch (AuthorizeException e)
			{
				throw ServiceRequestException.createByException("", e);
			}
		}

		synchronized (Signature.SYSTEM_INTERNAL_SESSION)
		{
			String sessionMgrc = cm.getModuleCredential(Signature.SYSTEM_INTERNAL_SESSION);
			if (StringUtils.isNullString(sessionMgrc))
			{
				sessionMgrc = UUID.randomUUID().toString();
				cm.setModuleCredential(Signature.SYSTEM_INTERNAL_SESSION, sessionMgrc);
				cm.bind(sessionMgrc, SignatureFactory.createSignature(Signature.SYSTEM_INTERNAL_SESSION));

				if (clientIdentifier == null)
				{
					return sessionMgrc;
				}
			}

			if (clientIdentifier != null && !smRegisterSet.contains(clientIdentifier))
			{
				smRegisterSet.add(clientIdentifier);
				return sessionMgrc;
			}
			throw new ServiceRequestException("duplicate registSessionManager.");
		}
	}

}
