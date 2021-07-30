/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecycleModelService
 * Jiagang 2010-10-23
 */
package dyna.data.service.model.lifecyclemodel;

import java.util.List;

import dyna.common.bean.model.lf.Lifecycle;
import dyna.common.bean.model.lf.LifecyclePhase;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.data.service.model.ModelService;

/**
 * 生命周期模型服务
 * 
 * @author Jiagang
 * 
 */
public interface LifecycleModelService extends ModelService
{
	void reloadModel() throws ServiceRequestException;

	Lifecycle getLifecycle(String lifecycleName);

	Lifecycle getLifecycleByGuid(String lifecycleGuid);

	LifecyclePhase getLifecyclePhase(String lifecycleName, String lifecyclePhaseName);

	LifecyclePhase getLifecyclePhaseByGuid(String lifecyclePhaseGuid) throws ServiceRequestException;
	
	LifecycleInfo getLifecycleInfo(String lifecycleName);

	LifecycleInfo getLifecycleInfoByGuid(String lifecycleGuid);

	LifecyclePhaseInfo getLifecyclePhaseInfo(String lifecycleName, String lifecyclePhaseName);

	LifecyclePhaseInfo getLifecyclePhaseInfoByGuid(String lifecyclePhaseGuid) throws ServiceRequestException;
	
	List<LifecyclePhaseInfo> listLifecyclePhaseInfo(String lifecycleGuid);

	List<LifecycleInfo> listLifeCycleInfo();

}
