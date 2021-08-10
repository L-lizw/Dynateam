/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: StructureFullNameDecorator
 * Wanglei 2010-10-14
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
public class StructureFullNameDecorator extends FullNameDecorator
{

	public <T extends DynaObject> void decorate(T object, EMM emm) throws DecorateException
	{
		String classGuid = (String) object.get(SystemClassFieldEnum.END2.getName() + "CLASS");
		this.decorate(classGuid, object, emm);
	}

}
