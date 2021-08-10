/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassNameDecorator
 * Wanglei 2010-7-21
 */
package dyna.app.service.helper.decorate;

import java.util.Set;

import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.ShortObject;
import dyna.common.dto.FileType;
import dyna.common.dto.Folder;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.DSS;
import dyna.net.service.brs.EMM;

/**
 * 给数据增加业务对象信息<br>
 * CLASSNAME$<br>
 * 对于object类型的字段, 增加业务对象信息<br>
 * FIELDNAME$CLASSNAME
 * 
 * @author Wanglei
 * 
 */
public class ClassNameDecorator implements Decorator
{

	private <T extends DynaObject> void clearField(T object, String fieldName)
	{
		object.clear(fieldName + "__" + "ID");
		object.clear(fieldName + "__" + "NAME");
		object.clear(fieldName + "__" + "ALID");
		object.clear(fieldName + "__" + "REID");
		object.clear(fieldName + "__" + "ITID");
		object.clear(fieldName + "__" + "FINA");
		object.clear(fieldName + "__" + "CLFI");

	}

	protected <T extends DynaObject> void decorate(String fieldName, String classGuidField, String classNameField, T object, EMM emm, Folder folder, String extendClassName)
			throws DecorateException
	{

		if (emm == null)
		{
			throw new DecorateException("Service EMM is not available.", null);
		}

		String className = null;
		String classIcon = null;
		String classIcon32 = null;
		ClassInfo classInfo = null;

		try
		{
			if (StringUtils.isNullString(extendClassName))
			{
				String classGuid = (String) object.get(classGuidField);
				if (StringUtils.isNullString(classGuid))
				{
					this.clearField(object, fieldName);
					return;
				}
				classInfo = ((EMMImpl) emm).getClassStub().getClassByGuid(classGuid, false);
				if (classInfo == null)
				{
					this.clearField(object, fieldName);
					return;
				}

				className = classInfo.getName();
				classIcon = classInfo.getIcon();
				classIcon32 = classInfo.getIcon32();

				if (StringUtils.isNullString(className))
				{
					// throw new DecorateException("can not find class name matched class guid " + classGuid, null);
					this.clearField(object, fieldName);
					return;
				}
				object.clear(classNameField);
				object.put(classNameField, className);
			}
			else
			{ // 对igroup,iuser的类进行处理
				classInfo = emm.getClassByName(extendClassName);
				if (classInfo == null)
				{
					this.clearField(object, fieldName);
					return;
				}
				if (object instanceof ShortObject)
				{
					String fieldGuid = (String) ((ShortObject) object).get(fieldName);
					if (StringUtils.isNullString(fieldGuid))
					{
						this.clearField(object, fieldName);
						return;
					}
				}
			}

			boolean isMaster = false;
			boolean isShortCut = false;
			if (object instanceof ShortObject)
			{
				ShortObject shortObject = (ShortObject) object;
				isShortCut = shortObject.isShortcut();
				if (fieldName == null)
				{
					isMaster = shortObject.getObjectGuid().isMaster();
				}
				else
				{
					String tmp = "";
					if (!fieldName.endsWith("$"))
					{
						tmp = fieldName + "$";
					}
					Boolean value = BooleanUtils.getBooleanByYN((String) shortObject.get(tmp + "ISMASTER"));
					isMaster = value == null ? false : value.booleanValue();
				}
			}

			object.clear(fieldName + "__" + "ID");
			object.clear(fieldName + "__" + "NAME");
			object.clear(fieldName + "__" + "ALID");
			object.clear(fieldName + "__" + "REID");
			object.clear(fieldName + "__" + "ITID");
			object.clear(fieldName + "__" + "FINA");
			object.clear(fieldName + "__" + "CLFI");

			if (StringUtils.isNullString(classIcon))
			{
				classIcon = isMaster && isShortCut ? "mastershortcut.png" : isMaster ? "master.png" : isShortCut ? "shortcut.png" : "object.png";
			}
			else
			{
				if (isMaster && isShortCut)
				{
					classIcon = "ms_" + classIcon;
				}
				else if (isMaster)
				{
					classIcon = "m_" + classIcon;
				}
				else if (isShortCut)
				{
					classIcon = "s_" + classIcon;
				}
			}
			if (object instanceof ShortObject)
			{
				String type = ((ShortObject) object).getFileType();
				if (!StringUtils.isNullString(type))
				{
					FileType fileType;
					try
					{
						fileType = ((EMMImpl) emm).getRefService(DSS.class).getFileType(type);
						object.clear(ShortObject.FILE_ICON16);
						object.put(ShortObject.FILE_ICON16, fileType.getIcon16());
						object.clear(ShortObject.FILE_ICON32);
						object.put(ShortObject.FILE_ICON32, fileType.getIcon32());
					}
					catch (ServiceNotFoundException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			throw new DecorateException(e.getMessage(), e.fillInStackTrace());
		}

		if (fieldName == null)
		{
			object.clear(ShortObject.BO_ICON);
			object.put(ShortObject.BO_ICON, classIcon);

			object.clear("ICON32$");
			object.put("ICON32$", classIcon32);
		}
		else
		{
			object.clear(classGuidField + "$ICON");
			object.put(classGuidField + "$ICON", classIcon);

			object.clear(classGuidField + "$ICON32");
			object.put(classGuidField + "$ICON32", classIcon32);
		}

	}

	protected <T extends DynaObject> void decorate(String classGuidField, String classNameField, T object, EMM emm) throws DecorateException
	{
		this.decorate(null, classGuidField, classNameField, object, emm, null, null);
	}

	protected <T extends DynaObject> void decorate(String classGuidField, String classNameField, T object, EMM emm, Folder folder) throws DecorateException
	{
		this.decorate(null, classGuidField, classNameField, object, emm, folder, null);
	}

	public <T extends DynaObject> void decorateWithField(Set<String> fieldNames, T object, EMM emm) throws DecorateException
	{
		if (fieldNames == null || fieldNames.isEmpty())
		{
			return;
		}
		for (String fieldName : fieldNames)
		{
			if (!StringUtils.isNullString(fieldName) && fieldName.contains(ClassStub.FIELD_NAME_SYMBOL))
			{
				String[] field = StringUtils.splitStringWithDelimiterHavEnd(ClassStub.FIELD_NAME_SYMBOL, fieldName);
				this.decorate(field[0], field[0] + "$CLASS", field[0] + "$CLASSNAME", object, emm, null, null);
			}
			else
			{
				String classGuidField = fieldName + "$CLASS";
				String classNameField = fieldName + "$CLASSNAME";
				if (fieldName.endsWith("$"))
				{
					classGuidField = fieldName + "CLASS";
					classNameField = fieldName + "CLASSNAME";
				}
				this.decorate(fieldName, classGuidField, classNameField, object, emm, null, null);
			}
		}
	}

	public <T extends DynaObject> void decorate(T object, EMM emm, Folder folder) throws DecorateException
	{
		this.decorate("CLASSGUID$", "CLASSNAME$", object, emm, folder);
	}
}
