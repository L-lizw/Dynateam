/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BizObjectDecorator
 * Wanglei 2010-9-7
 */
package dyna.app.service.helper.decorate;

import java.util.Set;

import dyna.app.service.brs.emm.ClassStub;
import dyna.common.bean.data.DynaObject;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

/**
 * 给数据增加业务对象信息<br>
 * BOGUID$<BR>
 * BOTITLE$ <BR>
 * 对于object类型的字段, 增加业务对象信息<br>
 * FIELDNAME$BOGUID<br>
 * FIELDNAME$BOTITLE
 * 
 * @author Wanglei
 * 
 */
public class BizObjectDecorator implements Decorator
{

	protected <T extends DynaObject> void decorate(String classGuidField, String classificationGuidField, String bmNameField, String bmTitleField, T object, String bmGuid, EMM emm)
			throws DecorateException
	{

		String classGuid = (String) object.get(classGuidField);
		if (StringUtils.isNullString(classGuid))
		{
			return;
		}

		// String classificationGuid = (String) object.get(classificationGuidField);

		BOInfo bizObject = null;
		try
		{
			bizObject = emm.getBizObject(bmGuid, classGuid, null);
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}

		if (bizObject == null)
		{
			return;
		}

		object.clear(bmNameField);
		object.put(bmNameField, bizObject.getGuid());

		object.clear(bmTitleField);
		object.put(bmTitleField, bizObject.getTitle());

	}

	public <T extends DynaObject> void decorateWithField(Set<String> fieldNames, T object, String bmGuid, EMM emm) throws DecorateException
	{
		if (fieldNames == null || fieldNames.isEmpty())
		{
			return;
		}
		for (String fieldName : fieldNames)
		{
			if (!StringUtils.isNullString(fieldName) && fieldName.contains(ClassStub.FIELD_NAME_SYMBOL))
			{
				continue;
			}
			this.decorate(fieldName + "$CLASS", fieldName + "$CLASSIFICATION", fieldName + "$BOGUID", fieldName + "$BOTITLE", object, bmGuid, emm);
		}
	}

	public <T extends DynaObject> void decorate(T object, String bmGuid, EMM emm) throws DecorateException
	{
		this.decorate("CLASSGUID$", "CLASSIFICATION$", "BOGUID$", "BOTITLE$", object, bmGuid, emm);
	}
}
