/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ModuleSignature
 * Wanglei 2010-4-15
 */
package dyna.net.security.signature;

import java.io.Serializable;

/**
 * @author Wanglei
 * 
 */
public class ModuleSignature extends AbstractSignature implements Serializable
{
	private static final long	serialVersionUID	= -5121131253646157249L;

	private String				moduleId			= null;

	private boolean				versionValidate		= false;

	public ModuleSignature(String moduleId)
	{
		super();
		this.moduleId = moduleId;
	}

	/**
	 * @return the moduleId
	 */
	public String getModuleId()
	{
		return this.moduleId;
	}

	@Override
	public String toString()
	{
		return "<ModuleSignature> moduleId: [" + this.moduleId + "] " + super.toString();
	}

	/**
	 * @return the versionValidate
	 */
	public boolean isVersionValidate()
	{
		return this.versionValidate;
	}

	/**
	 * @param versionValidate
	 *            the versionValidate to set
	 */
	public void setVersionValidate(boolean versionValidate)
	{
		this.versionValidate = versionValidate;
	}

}
