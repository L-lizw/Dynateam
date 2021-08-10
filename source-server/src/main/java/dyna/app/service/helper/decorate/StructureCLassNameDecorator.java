/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: StructureCLassNameDecorator
 * Wanglei 2010-10-13
 */
package dyna.app.service.helper.decorate;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.exception.DecorateException;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.net.service.brs.EMM;

/**
 * @author Wanglei
 * 
 */
public class StructureCLassNameDecorator extends ClassNameDecorator
{

	public <T extends DynaObject> void decorate(T object, EMM emm) throws DecorateException
	{
		super.decorate(object, emm, null);

		String fieldName = ViewObject.END1;
		this.decorate(fieldName, fieldName + "$CLASS", fieldName + "$CLASSNAME", object, emm, null, null);

		fieldName = SystemClassFieldEnum.END2.getName();
		this.decorate(fieldName, fieldName + "CLASSGUID", fieldName + "CLASSNAME", object, emm, null, null);

	}
}
