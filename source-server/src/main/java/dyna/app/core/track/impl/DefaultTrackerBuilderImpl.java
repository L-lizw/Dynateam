/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DefaultTrackerBuilder
 * Wanglei 2011-11-11
 */
package dyna.app.core.track.impl;

import java.lang.reflect.Method;

import dyna.app.core.track.Tracker;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerDescription;
import dyna.app.core.track.TrackerPersistence;
import dyna.app.core.track.TrackerRenderer;
import dyna.app.server.context.ServiceContext;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.security.signature.Signature;

/**
 * @author Wanglei
 *
 */
public class DefaultTrackerBuilderImpl implements TrackerBuilder
{
	private ServiceContext		serviceContext		= null;
	private TrackerRenderer		renderer		= null;
	private TrackerPersistence	persistence			= null;

	public DefaultTrackerBuilderImpl(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.track.TrackerBuilder#make()
	 */
	@Override
	public Tracker make(Signature signature, Method method, Object result, Object[] parameters)
	{
		return new DefaultTrackerImpl(this.serviceContext, signature, method, parameters, result, this.getTrackerRenderer(), this.getTrackerPersistence());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.TrackerBuilder#setResultRendererClass(java.lang.Class)
	 */
	@Override
	public void setTrackerRendererClass(Class<? extends TrackerRenderer> rendererClass, String desc)
	{
		if (this.renderer == null && rendererClass != null && !rendererClass.isInterface())
		{
			try
			{
				this.renderer = rendererClass.newInstance();
			}
			catch (InstantiationException e)
			{
				DynaLogger.error(e);
			}
			catch (IllegalAccessException e)
			{
				DynaLogger.error(e);
			}
		}

		if (this.renderer == null)
		{
			this.renderer = new DefaultTrackerRendererImpl();
		}

		TrackerDescription td = null;
		if (!StringUtils.isNullString(desc))
		{
			td = new TrackerDescriptionFixedImpl(desc);
		}
		else
		{
			td = new DefaultTrackerDescriptionImpl();
		}
		this.renderer.setTrackerDescription(td);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.track.TrackerBuilder#setPersistenceClass(java.lang.Class)
	 */
	@Override
	public void setPersistenceClass(Class<? extends TrackerPersistence> tpClass)
	{
		if (tpClass != null && !tpClass.isInterface())
		{
			try
			{
				this.persistence = tpClass.newInstance();
			}
			catch (InstantiationException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
		}
	}

	private TrackerRenderer getTrackerRenderer()
	{
		return this.renderer;
	}

	private TrackerPersistence getTrackerPersistence()
	{
		return this.persistence;
	}
}
