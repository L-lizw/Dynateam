/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: StructureBODecorator
 * Wanglei 2010-10-13
 */
package dyna.app.service.helper.decorate;

import dyna.common.bean.data.DynaObject;
import dyna.common.exception.DecorateException;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.net.service.brs.EMM;

/**
 * @author Wanglei
 *
 */
public class StructureBODecorator extends BizObjectDecorator
{

	@Override
	public <T extends DynaObject> void decorate(T object, String bmGuid, EMM emm) throws DecorateException
	{
		String fieldName = SystemClassFieldEnum.END2.getName();
		this.decorate(fieldName + "CLASSGUID", "CLASSIFICATION$", "BOGUID$", "BOTITLE$", object, bmGuid, emm);
	}

}
