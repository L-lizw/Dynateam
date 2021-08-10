/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FullNameDecorator
 * Wanglei 2010-7-22
 */
package dyna.app.service.helper.decorate;

import java.math.BigDecimal;

import dyna.app.service.brs.emm.EMMImpl;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ShortObject;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

/**
 * 给数据增加全称<br>
 * FULLNAME$
 * 
 * @author Wanglei
 * 
 */
public class FullNameDecorator implements Decorator
{

	protected <T extends DynaObject> void decorate(String classGuid, T object, EMM emm) throws DecorateException
	{
		try
		{
			ShortObject sObject = (ShortObject) object;

			object.clear("FULLNAME$");
			if (StringUtils.isNullString(sObject.getId()))// has no system fields
			{
				object.put("FULLNAME$", "???");
				return;
			}

			ClassInfo clsInfo = emm.getClassByGuid(classGuid);
			if (clsInfo == null)
			{
				throw new DecorateException("can not find class name matched class guid " + classGuid, null);
			}

			StringBuffer fullName = new StringBuffer();

			String instanceString = clsInfo.getInstanceString();
			if (instanceString == null)
			{
				if (clsInfo.hasInterface(ModelInterfaceEnum.IVersionable))
				{
					fullName.append(StringUtils.convertNULLtoString(sObject.getId()));
					fullName.append("/");
					fullName.append(StringUtils.convertNULLtoString(sObject.getRevisionId()));
					if (sObject.getIterationId() != null)
					{
						fullName.append(".");
						fullName.append(StringUtils.convertNULLtoString(sObject.getIterationId()));
					}
					if (sObject.getName() != null)
					{
						fullName.append("-");
						fullName.append(StringUtils.convertNULLtoString(sObject.getName()));
					}
				}
				else
				{
					fullName.append(sObject.getId());
					if (sObject.getName() != null)
					{
						fullName.append("-");
						fullName.append(StringUtils.convertNULLtoString(sObject.getName()));
					}
				}
				object.put("FULLNAME$", fullName.toString());
			}
			else
			{
				String[] instanceArr = instanceString.split("\\+");
				String renString = "";
				for (String str : instanceArr)
				{
					if (str.contains("\""))
					{
						renString = renString + str.replaceAll("\"", "");
					}
					else
					{
						String titleValue = null;
						if ("CLASSIFICATION".equals(str.toUpperCase()) || "CLASSIFICATION$".equals(str.toUpperCase()))
						{
							Object title = object.get("CLASSIFICATION$");
							if (title == null || "".equals(title))
							{
								continue;
							}
							CodeItemInfo codeItemInfo = emm.getCodeItem((String) title);
							if (codeItemInfo == null || "".equals(title))
							{
								continue;
							}

							titleValue = StringUtils.getMsrTitle(codeItemInfo.getTitle(), ((EMMImpl) emm).getUserSignature().getLanguageEnum().getType());
							if (titleValue == null)
							{
								titleValue = "";
							}

							titleValue = titleValue + "[" + codeItemInfo.getCode() + "]";
						}
						else if ("ID".equals(str.toUpperCase()) || "ID$".equals(str.toUpperCase()))
						{
							Object title = object.get("ID$");
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}
						else if ("ALTERID".equals(str.toUpperCase()) || "ALTERID$".equals(str.toUpperCase()))
						{
							Object title = object.get("ALTERID$");
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}
						else if ("REVISIONID".equals(str.toUpperCase()) || "REVISIONID$".equals(str.toUpperCase()))
						{
							Object title = object.get("REVISIONID$");
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}
						else if ("ITERATIONID".equals(str.toUpperCase()) || "ITERATIONID$".equals(str.toUpperCase()))
						{
							Object title = object.get("ITERATIONID$");
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}
						else if ("FILENAME".equals(str.toUpperCase()) || "FILENAME$".equals(str.toUpperCase()))
						{
							Object title = object.get("FILENAME$");
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}
						else if ("NAME".equals(str.toUpperCase()) || "NAME$".equals(str.toUpperCase()))
						{
							Object title = object.get("NAME$");
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}
						else
						{
							Object title = object.get(str);
							if (title == null || "".equals(title))
							{
								continue;
							}

							if (title instanceof BigDecimal)
							{
								titleValue = title.toString();
							}
							else
							{
								titleValue = (String) title;
							}
						}

						if (StringUtils.isNullString(titleValue))
						{
							continue;
						}

						renString = renString + titleValue;

					}
				}
				while (renString.endsWith("-"))
				{
					renString = renString.substring(0, renString.length() - 1);
				}
				object.put("FULLNAME$", renString);
			}

		}
		catch (ServiceRequestException e)
		{
			throw new DecorateException(e.getMessage(), e.fillInStackTrace());
		}
	}

	public <T extends DynaObject> void decorate(T object, EMM emm) throws DecorateException
	{
		ObjectGuid objectGuid = object.getObjectGuid();
		String classGuid = objectGuid.getClassGuid();
		this.decorate(classGuid, object, emm);
	}

}
