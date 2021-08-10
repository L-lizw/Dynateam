/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecycleModelServiceImpl
 * Jiagang 2010-10-23
 */
package dyna.data.service.model.lifecyclemodel;

import dyna.common.bean.model.lf.Lifecycle;
import dyna.common.bean.model.lf.LifecyclePhase;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.DataRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 生命周期模型服务服务的实现
 * 
 * @author Jiagang
 * 
 */
@Service
public class LifecycleModelServiceImpl extends DataRuleService implements LifecycleModelService
{
	@Autowired
	private LifecycleModelServiceStub modelStub;

	@Override
	public void init()
	{
		try
		{
			this.getModelStub().loadModel();
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void reloadModel() throws ServiceRequestException
	{
		this.getModelStub().loadModel();
	}

	public LifecycleModelServiceStub getModelStub()
	{
		return this.modelStub;
	}

	@Override
	public Lifecycle getLifecycle(String lifecycleName)
	{
		return this.getModelStub().getLifecycle(lifecycleName);
	}

	@Override
	public Lifecycle getLifecycleByGuid(String lifecycleGuid)
	{
		return this.getModelStub().getLifecycleByGuid(lifecycleGuid);
	}

	@Override
	public LifecyclePhase getLifecyclePhase(String lifecycleName, String lifecyclePhaseName)
	{
		return this.getModelStub().getLifecyclePhase(lifecycleName, lifecyclePhaseName);
	}

	@Override
	public LifecyclePhase getLifecyclePhaseByGuid(String lifecyclePhaseGuid) throws ServiceRequestException
	{
		return this.getModelStub().getLifecyclePhaseByGuid(lifecyclePhaseGuid);
	}

	@Override
	public LifecycleInfo getLifecycleInfo(String lifecycleName)
	{
		Lifecycle lifecycle = this.getLifecycle(lifecycleName);
		if (lifecycle == null)
		{
			return null;
		}
		return lifecycle.getInfo();
	}

	@Override
	public LifecycleInfo getLifecycleInfoByGuid(String lifecycleGuid)
	{
		Lifecycle lifecycle = this.getLifecycleByGuid(lifecycleGuid);
		if (lifecycle == null)
		{
			return null;
		}
		return lifecycle.getInfo();
	}

	@Override
	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecycleName, String lifecyclePhaseName)
	{
		LifecyclePhase lifecyclePhase = this.getLifecyclePhase(lifecycleName, lifecyclePhaseName);
		if (lifecyclePhase == null)
		{
			return null;
		}
		return lifecyclePhase.getInfo();
	}

	@Override
	public LifecyclePhaseInfo getLifecyclePhaseInfoByGuid(String lifecyclePhaseGuid) throws ServiceRequestException
	{
		LifecyclePhase lifecyclePhase = this.getLifecyclePhaseByGuid(lifecyclePhaseGuid);
		if (lifecyclePhase == null)
		{
			return null;
		}
		return lifecyclePhase.getInfo();
	}

	@Override
	public List<LifecyclePhaseInfo> listLifecyclePhaseInfo(String lifecycleGuid)
	{
		return this.getLifecycleByGuid(lifecycleGuid).getLifecyclePhaseList().stream().map(LifecyclePhase::getInfo).collect(Collectors.toList());
	}

	@Override
	public List<LifecycleInfo> listLifeCycleInfo()
	{
		return this.getModelStub().listLifecycleInfo();
	}
}
