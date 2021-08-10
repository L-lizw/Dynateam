/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SignableAdapter
 * Wanglei 2011-4-18
 */
package dyna.app.core;

import dyna.common.log.DynaLogger;
import dyna.net.security.signature.Signature;

/**
 * @author Wanglei
 *
 */
public class SignableAdapter implements Signable
{
	protected Signature signature = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Signable#clearSignature()
	 */
	@Override
	public void clearSignature()
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("clear signature" + " of [" + this.getClass() + "]");
		}
		this.signature = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Signable#getSignature()
	 */
	@Override
	public Signature getSignature()
	{
		return this.signature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Signable#isSigned()
	 */
	@Override
	public boolean isSigned()
	{
		return (this.signature != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Signable#setSignature(dyna.net.security.signature.Signature)
	 */
	@Override
	public synchronized void setSignature(Signature signature) throws IllegalStateException
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("sign [" + this.getClass() + "] with [" + signature + "]");
		}
		if (signature == this.signature)
		{
			return;
		}

		if (this.signature != null)
		{
			throw new IllegalStateException("Duplicated signature.");
		}
		this.signature = signature;
	}

}
