/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LCSImpl
 * caogc 2010-11-02
 */
package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Life Cycle Service Implement 生命周期服务的实现类
 * 
 * @author caogc
 * 
 */
@Component
public class LCStub extends AbstractServiceStub<EMMImpl>
{

	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecyclePhaseInfoGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getLifecycleModelService().getLifecyclePhaseInfoByGuid(lifecyclePhaseInfoGuid);
	}

	public LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecycleInfoName, String lifecyclePhaseInfoName) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getLifecycleModelService().getLifecyclePhaseInfo(lifecycleInfoName, lifecyclePhaseInfoName);
	}

	protected LifecyclePhaseInfo getFirstLifecyclePhaseInfoByClassName(String className) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getClassByName(className);

		if (classInfo == null)
		{
			throw new ServiceRequestException("ID_APP_NO_FOUND_CALSS", "not found Class: " + className, null, className);
		}
		String lifecycleName = classInfo.getLifecycleName();

		if (lifecycleName == null)
		{
			throw new ServiceRequestException("ID_APP_NO_FOUND_LIFECYCLE", className + ":class not found lifecycleName ", null, className);
		}
		List<LifecyclePhaseInfo> lifecyclePhaseInfoList = this.listLifeCyclePhase(lifecycleName);

		if (SetUtils.isNullList(lifecyclePhaseInfoList))
		{
			throw new ServiceRequestException("ID_APP_NO_FOUND_LIFECYCLE_PHASE", lifecycleName + ":lifecycleName not found lifecyclePhase ", null, lifecycleName);
		}

		return lifecyclePhaseInfoList.get(0);
	}

	protected LifecycleInfo getLifecycleInfoByGuid(String lifecycleInfoGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getLifecycleModelService().getLifecycleInfoByGuid(lifecycleInfoGuid);
	}

	protected List<LifecyclePhaseInfo> listLifecyclePhaseInfo(String lifecycleInfoGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getLifecycleModelService().listLifecyclePhaseInfo(lifecycleInfoGuid);
	}

	public LifecycleInfo getLifecycleInfoByName(String lifecycleName) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getLifecycleModelService().getLifecycleInfo(lifecycleName);
	}

	protected List<LifecycleInfo> listLifeCycleInfo() throws ServiceRequestException
	{
		List<LifecycleInfo> lifecycleInfoList = this.stubService.getDomainServiceStub().getLifecycleModelService().listLifeCycleInfo();
		return lifecycleInfoList;
	}

	protected List<LifecyclePhaseInfo> listLifeCyclePhase(String lifeCycleName) throws ServiceRequestException
	{
		if (lifeCycleName == null)
		{
			return null;
		}

		LifecycleInfo lifecycleInfo = this.getLifecycleInfoByName(lifeCycleName);
		if (lifecycleInfo == null)
		{
			return null;
		}
		return this.listLifecyclePhaseInfo(lifecycleInfo.getGuid());
	}
}
