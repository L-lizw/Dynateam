/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LCPhaseDecorator
 * caogc 2010-9-20
 */
package dyna.app.service.helper.decorate;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.DynaObjectImpl;
import dyna.common.bean.data.ShortObject;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.net.service.brs.EMM;

/**
 * 给生命周期增加名称<br>
 * LIFECYCLEPHASE$NAME
 * 
 * @author Wanglei
 * 
 */
public class LCPhaseDecorator implements Decorator
{

	public <T extends DynaObject> void decorate(T object, EMM emm) throws DecorateException
	{
		if (!(object instanceof ShortObject))
		{
			return;
		}

		ShortObject shortObject = (ShortObject) object;
		String phaseGuid = shortObject.getLifecyclePhaseGuid();
		try
		{
			LifecyclePhaseInfo phaseInfo = emm.getLifecyclePhaseInfo(phaseGuid);
			if (phaseInfo == null)
			{
				shortObject.setLifecyclePhaseGuid(null);
				((DynaObjectImpl) shortObject).getOriginalMap().put(SystemClassFieldEnum.LCPHASE.getName().toUpperCase(), null);
				return;
			}
			shortObject.setLifecyclePhase(phaseInfo.getTitle());
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.warn("not lifecycle phase: " + phaseGuid, e);
		}
	}
}
