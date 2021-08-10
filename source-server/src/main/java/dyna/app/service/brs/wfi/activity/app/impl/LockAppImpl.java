/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LockAppImpl
 * Wanglei 2010-11-11
 */
package dyna.app.service.brs.wfi.activity.app.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ProcApp;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author Wanglei
 * 
 */
public class LockAppImpl implements ProcApp
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.wfi.app.ProcApp#invoke(dyna.common.bean.data.system.ActivityRuntime)
	 */
	@Override
	public Object execute(WFIImpl wfeImpl, ActivityRuntime activity) throws ServiceRequestException
	{
		String procRtGuid = activity.getProcessRuntimeGuid();
		wfeImpl.getLockStub().lock(procRtGuid);

		SystemDataService sds = DataServer.getSystemDataService();

		// 锁定时，备份附件状态与生命周期
		List<ProcAttach> listProcAttach = wfeImpl.listProcAttach(procRtGuid);
		if (!SetUtils.isNullList(listProcAttach))
		{
			for (ProcAttach attach : listProcAttach)
			{
				FoundationObject foundationObject = wfeImpl.getBOAS().getObjectByGuid(new ObjectGuid(attach.getInstanceClassGuid(), null, attach.getInstanceGuid(), null));
				if (foundationObject != null)
				{
					attach.setInstanceLifcpBackup(foundationObject.getLifecyclePhaseGuid());
					attach.setInstanceStatusBackup(foundationObject.getStatus().getId());

					Map<String, Object> filter = new HashMap<String, Object>();

					filter.put("PROCRTGUID", procRtGuid);
					filter.put("INSTANCEGUID", attach.getInstanceGuid());
					filter.put("INSTANCELIFCPBACKUP", attach.getInstanceLifcpBackup());
					filter.put("INSTANCESTATUSBACKUP", attach.getInstanceStatusBackup());
					sds.update(ProcAttach.class, filter, "update");
				}
			}
		}

		return null;
	}

}
