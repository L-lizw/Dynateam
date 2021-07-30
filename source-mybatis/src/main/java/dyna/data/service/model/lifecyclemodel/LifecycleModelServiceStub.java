/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeXMLWriter
 * WangLHB Jul 6, 2011
 */
package dyna.data.service.model.lifecyclemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.lf.Lifecycle;
import dyna.common.bean.model.lf.LifecyclePhase;
import dyna.common.cache.AbstractCacheInfo;
import dyna.common.cache.CacheRefreshListener;
import dyna.common.cache.DynaCacheObserver;
import dyna.common.cache.DynaObserverMediator;
import dyna.common.dto.model.lf.LifecycleGate;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.model.DataCacheServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;

/**
 * @author WangLHB
 * 
 */
public class LifecycleModelServiceStub extends DataCacheServiceStub<LifecycleModelServiceImpl>
{
	private static final Map<String, Lifecycle>			LIFECYCLE_GUID_MAP			= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, LifecyclePhase>	LIFECYCLE_PHASE_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, LifecycleGate>		LIFECYCLE_GATE_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());

	private static final Map<String, String>			LIFECYCLE_NAME_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());

	private AbstractCacheInfo							cacheInfo					= null;

	protected LifecycleModelServiceStub(DataServerContext context, LifecycleModelServiceImpl service)
	{
		super(context, service);
		this.cacheInfo = new LifecycleModelCacheInfo();
		this.cacheInfo.register();
	}

	@Override
	public void loadModel() throws ServiceRequestException
	{
		List<LifecycleInfo> lifecycleObjectList = DataServer.getSystemDataService().listFromCache(LifecycleInfo.class, null);
		if (!SetUtils.isNullList(lifecycleObjectList))
		{
			lifecycleObjectList.forEach(lifecycleInfo -> {
				try
				{
					makeLifecycleObject(lifecycleInfo);
				}
				catch (ServiceRequestException e)
				{
					e.printStackTrace();
				}
			});
		}
	}

	protected List<Lifecycle> listLifecycle()
	{
		return new ArrayList<>(LIFECYCLE_GUID_MAP.values());
	}

	protected List<LifecycleInfo> listLifecycleInfo()
	{
		return LIFECYCLE_GUID_MAP.values().stream().map(Lifecycle::getInfo).collect(Collectors.toList());
	}

	protected Lifecycle getLifecycle(String lifecycleName)
	{
		String lifecycleGuid = LIFECYCLE_NAME_GUID_MAP.get(lifecycleName);
		if (StringUtils.isGuid(lifecycleGuid))
		{
			return this.getLifecycleByGuid(lifecycleGuid);
		}
		return null;
	}

	protected Lifecycle getLifecycleByGuid(String lifecycleGuid)
	{
		Lifecycle lifecycle = LIFECYCLE_GUID_MAP.get(lifecycleGuid);
		if (lifecycle == null)
		{
			return null;
		}
		return (Lifecycle) lifecycle.clone();
	}

	protected LifecyclePhase getLifecyclePhase(String lifecycleName, String lifecyclePhaseName)
	{
		Lifecycle lifecycleInfo = this.getLifecycle(lifecycleName);
		if (lifecycleInfo != null)
		{
			List<LifecyclePhase> lifecyclePhaseList = lifecycleInfo.getLifecyclePhaseList();
			if (!SetUtils.isNullList(lifecyclePhaseList))
			{
				List<LifecyclePhase> list = lifecyclePhaseList.stream().filter(lifecyclePhaseInfo -> lifecyclePhaseName.equalsIgnoreCase(lifecyclePhaseInfo.getName()))
						.collect(Collectors.toList());
				return SetUtils.isNullList(list) ? null : (LifecyclePhase) list.get(0).clone();
			}
		}
		return null;
	}

	protected LifecyclePhase getLifecyclePhaseByGuid(String lifecyclePhaseGuid) throws ServiceRequestException
	{
		LifecyclePhaseInfo lifecyclePhaseInfo = DataServer.getSystemDataService().get(LifecyclePhaseInfo.class, lifecyclePhaseGuid);
		if (lifecyclePhaseInfo == null)
		{
			return null;
		}
		LifecyclePhaseInfo phaseInfo = (LifecyclePhaseInfo) lifecyclePhaseInfo.clone();
		LifecyclePhase phase = new LifecyclePhase(phaseInfo);
		this.makeLifecyclePhase(phase);
		return phase;
	}

	private void makeLifecycleObject(LifecycleInfo lifecycleInfo) throws ServiceRequestException
	{
		LIFECYCLE_GUID_MAP.put(lifecycleInfo.getGuid(), new Lifecycle(lifecycleInfo));
		LIFECYCLE_NAME_GUID_MAP.put(lifecycleInfo.getName(), lifecycleInfo.getGuid());

		List<LifecyclePhase> phaseObjectList = new ArrayList<LifecyclePhase>();
		List<LifecyclePhaseInfo> phaseInfoList = DataServer.getSystemDataService().listFromCache(LifecyclePhaseInfo.class,
				new FieldValueEqualsFilter<>(LifecyclePhaseInfo.MASTERFK, lifecycleInfo.getGuid()));
		if (!SetUtils.isNullList(phaseInfoList))
		{
			phaseInfoList.forEach(phaseInfo -> {
				try
				{
					LifecyclePhase phase = new LifecyclePhase(phaseInfo);
					LIFECYCLE_PHASE_GUID_MAP.put(phaseInfo.getGuid(), phase);
					this.makeLifecyclePhase(phase);
					phaseObjectList.add(phase);
				}
				catch (ServiceRequestException e)
				{
					e.printStackTrace();
				}
			});
		}
		LIFECYCLE_GUID_MAP.get(lifecycleInfo.getGuid()).setLifecyclePhaseList(phaseObjectList);
	}

	private void makeLifecyclePhase(LifecyclePhase lilecyclePhase) throws ServiceRequestException
	{
		List<LifecycleGate> gateList = DataServer.getSystemDataService().listFromCache(LifecycleGate.class,
				new FieldValueEqualsFilter<>(LifecycleGate.DETAILFK, lilecyclePhase.getGuid()));
		if (gateList != null)
		{
			gateList.forEach(gate -> {
				LIFECYCLE_GATE_GUID_MAP.put(gate.getGuid(), gate);
			});
		}
		lilecyclePhase.setGateList(gateList);
	}

	class LifecycleModelCacheInfo extends AbstractCacheInfo
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5617363176640148854L;

		@Override
		public void register()
		{
			DynaObserverMediator mediator = DynaObserverMediator.getInstance();
			mediator.register(LifecycleInfo.class, new DynaCacheObserver<LifecycleInfo>(this, new CacheRefreshListener<LifecycleInfo>()));
			mediator.register(LifecyclePhaseInfo.class, new DynaCacheObserver<LifecyclePhaseInfo>(this, new CacheRefreshListener<LifecyclePhaseInfo>()));
			mediator.register(LifecycleGate.class, new DynaCacheObserver<LifecycleGate>(this, new CacheRefreshListener<LifecycleGate>()));
		}

		@Override
		public <T extends SystemObject> void addToCache(T data) throws ServiceRequestException
		{
			if (data instanceof LifecycleInfo)
			{
				LifecycleInfo lifecycleInfo = (LifecycleInfo) data;
				LIFECYCLE_GUID_MAP.put(lifecycleInfo.getGuid(), new Lifecycle(lifecycleInfo));
				LIFECYCLE_NAME_GUID_MAP.put(lifecycleInfo.getName(), lifecycleInfo.getGuid());
			}
			else if (data instanceof LifecyclePhaseInfo)
			{
				LifecyclePhaseInfo lifecyclePhaseInfo = (LifecyclePhaseInfo) data;
				LIFECYCLE_PHASE_GUID_MAP.put(lifecyclePhaseInfo.getGuid(), new LifecyclePhase(lifecyclePhaseInfo));
				Lifecycle lifecycle = LIFECYCLE_GUID_MAP.get(lifecyclePhaseInfo.getMasterfk());
				lifecycle.addPhase(LIFECYCLE_PHASE_GUID_MAP.get(lifecyclePhaseInfo.getGuid()));
			}
			else if (data instanceof LifecycleGate)
			{
				LifecycleGate lifecycleGateInfo = (LifecycleGate) data;
				LIFECYCLE_PHASE_GUID_MAP.get(lifecycleGateInfo.getDetailfk()).addGate(lifecycleGateInfo);
			}
		}

		@Override
		public <T extends SystemObject> void removeFromCache(T data) throws ServiceRequestException
		{
			if (data instanceof LifecycleInfo)
			{
				LifecycleInfo lifecycleInfo = (LifecycleInfo) data;
				LIFECYCLE_GUID_MAP.remove(lifecycleInfo.getGuid());
				LIFECYCLE_NAME_GUID_MAP.remove(lifecycleInfo.getName());
			}
			else if (data instanceof LifecyclePhaseInfo)
			{
				LifecyclePhaseInfo lifecyclePhaseInfo = (LifecyclePhaseInfo) data;
				Lifecycle lifecycle = LIFECYCLE_GUID_MAP.get(lifecyclePhaseInfo.getMasterfk());
				if (lifecycle != null)
				{
					lifecycle.removePhase(LIFECYCLE_PHASE_GUID_MAP.get(lifecyclePhaseInfo.getGuid()));
				}
				LIFECYCLE_PHASE_GUID_MAP.remove(lifecyclePhaseInfo.getGuid());
			}
			else if (data instanceof LifecycleGate)
			{
				LifecycleGate lifecycleGateInfo = (LifecycleGate) data;
				LifecyclePhase lifecyclePhase = LIFECYCLE_PHASE_GUID_MAP.get(lifecycleGateInfo.getDetailfk());
				if (lifecyclePhase != null)
				{
					lifecyclePhase.removeGate(lifecycleGateInfo);
				}
			}
		}

		@Override
		public <T extends SystemObject> void updateToCache(T data) throws ServiceRequestException
		{
			if (data instanceof LifecycleInfo)
			{
				LifecycleInfo lifecycleInfo = (LifecycleInfo) data;
				LIFECYCLE_GUID_MAP.get(lifecycleInfo.getGuid()).getInfo().putAll(lifecycleInfo);
			}
			else if (data instanceof LifecyclePhaseInfo)
			{
				LifecyclePhaseInfo lifecyclePhaseInfo = (LifecyclePhaseInfo) data;
				LIFECYCLE_PHASE_GUID_MAP.get(lifecyclePhaseInfo.getGuid()).getInfo().putAll(lifecyclePhaseInfo);
				Lifecycle lifecycle = LIFECYCLE_GUID_MAP.get(lifecyclePhaseInfo.getMasterfk());
				lifecycle.setLifecyclePhaseList(lifecycle.getLifecyclePhaseList());
			}
			else if (data instanceof LifecycleGate)
			{
				LifecycleGate lifecycleGateInfo = (LifecycleGate) data;
				LIFECYCLE_GATE_GUID_MAP.get(lifecycleGateInfo.getGuid()).putAll(lifecycleGateInfo);
			}
		}
	}
}
