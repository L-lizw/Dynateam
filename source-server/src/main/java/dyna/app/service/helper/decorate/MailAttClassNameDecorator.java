/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailAttClassNameDecorator
 * Wanglei 2010-10-26
 */
package dyna.app.service.helper.decorate;

import dyna.common.bean.data.DynaObject;
import dyna.common.dto.MailAttachment;
import dyna.common.exception.DecorateException;
import dyna.net.service.brs.EMM;

/**
 * @author Wanglei
 *
 */
public class MailAttClassNameDecorator extends ClassNameDecorator
{

	public <T extends DynaObject> void decorate(T object, EMM emm) throws DecorateException
	{
		this.decorate(MailAttachment.INSTANCECLASS, MailAttachment.INSTANCECLASSNAME, object, emm);
	}

}
