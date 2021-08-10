/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeTitleDecorator
 * Caogc 2010-9-27
 */
package dyna.app.service.helper.decorate;

import java.util.Set;

import dyna.common.bean.data.DynaObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

/**
 * 给数据增加业务对象信息<br>
 * Code类型字段<br>
 * FIELDNAME$TITLE
 * 
 * @author Wanglei
 * 
 */
public class CodeTitleDecorator implements Decorator
{

	private <T extends DynaObject> void decorate(String codeGuidField, String codeTitleField, String codeNameField, T object, EMM emm) throws DecorateException
	{

		if (emm == null)
		{
			throw new DecorateException("Service EMM is not available.", null);
		}
		String codeGuid = (String) object.get(codeGuidField);
		if (StringUtils.isNullString(codeGuid))
		{
			return;
		}
		String codeTitle = "";
		String codeName = "";
		try
		{
			String[] codeGuids = StringUtils.splitStringWithDelimiter(CodeObjectInfo.MULTI_CODE_DELIMITER_GUID, codeGuid);
			int length = codeGuids.length;
			int i = 0;
			CodeItemInfo codeItemInfo = null;
			for (String tmpCodeGuid : codeGuids)
			{
				i++;
				codeItemInfo = emm.getCodeItem(tmpCodeGuid);
				codeTitle = codeTitle + (codeItemInfo == null ? "" : StringUtils.convertNULLtoString(codeItemInfo.getTitle()))
						+ (i == length ? "" : CodeObjectInfo.MULTI_CODE_DELIMITER);
				codeName = codeName + (codeItemInfo == null ? "" : StringUtils.convertNULLtoString(codeItemInfo.getCode()))
						+ (i == length ? "" : CodeObjectInfo.MULTI_CODE_DELIMITER);
			}
		}
		catch (ServiceRequestException e)
		{
			throw new DecorateException(e.getMessage(), e.fillInStackTrace());
		}
		object.clear(codeTitleField);
		if (StringUtils.isNullString(codeTitle))
		{
			object.put(codeTitleField, null);
		}
		else
		{
			object.put(codeTitleField, codeTitle);
		}
		object.clear(codeNameField);

		if (StringUtils.isNullString(codeName))
		{
			object.clear(codeGuidField);
			object.put(codeNameField, null);
			object.put(codeGuidField, null);
		}
		else
		{
			object.put(codeNameField, codeName);
		}
	}

	public <T extends DynaObject> void decorateWithField(Set<String> fieldNames, T object, EMM emm) throws DecorateException
	{
		if (fieldNames == null || fieldNames.isEmpty())
		{
			return;
		}
		String titleField = null;
		String codeNameField = null;

		if (object != null && object.get("CLASSGUID$") != null)
		{
			try
			{
				ClassInfo classInfo = emm.getClassByGuid((String) object.get("CLASSGUID$"));
				if (classInfo != null)
				{
					String classname = classInfo.getName();
					for (String fieldName : fieldNames)
					{
						try
						{
							ClassField classField = emm.getFieldByName(classname, fieldName, false);
							if (classField != null)
							{
								if (!classField.isSystem())
								{
									titleField = "$TITLE";
									codeNameField = "$NAME";
								}
								else
								{
									titleField = "TITLE";
									codeNameField = "NAME";
								}
								this.decorate(fieldName, fieldName + titleField, fieldName + codeNameField, object, emm);
							}
						}
						catch (ServiceRequestException e)
						{
							// e.printStackTrace();
						}

					}
				}
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
		}
		else if (object != null)
		{
			try
			{
				for (String fieldName : fieldNames)
				{
					String codeItemGuid = (String) object.get(fieldName);
					if (StringUtils.isGuid(codeItemGuid))
					{
						CodeItemInfo codeItem = emm.getCodeItem(codeItemGuid);
						if (codeItem != null)
						{
							this.decorate(fieldName, fieldName + "$TITLE", fieldName + "$NAME", object, emm);
						}
					}
				}
			}
			catch (ServiceRequestException e)
			{
				e.printStackTrace();
			}
		}
	}
}
