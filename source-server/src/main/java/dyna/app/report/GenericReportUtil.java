/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericReportDataProvider
 * Daniel 2014-11-4
 */
package dyna.app.report;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.service.brs.emm.EMMImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.Folder;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ReportTypeEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.EnvUtils;
import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;
import net.sf.jasperreports.engine.JRPropertiesMap;

/**
 * @author Daniel
 * 
 */
public class GenericReportUtil
{
	private static final String[]	CHAR_NOT_USED		= { "\\", "|", "/", "?", "*", ":", "<", ">", "\"" };

	private GenericReportParams		params				= null;

	// 生成报表时使用的一些特殊字段，非对象字段，但是有特殊含义
	public static String[]			SPECIAL_FIELD_NAME	= { "FULLNAME$", "BOTITLE$" };

	public GenericReportUtil(GenericReportParams genericParams)
	{
		this.params = genericParams;
	}

	public static String getFolderOfFile(File file)
	{
		String path = file.getPath();
		if (file.isFile())
		{
			path = file.getParent();
		}

		if (!path.endsWith(File.separator))
		{
			path = path + File.separator;
		}

		return path;
	}

	public static File getTemplateFile(String folderPath, String fileName)
	{
		return new File(EnvUtils.getConfRootPath() + folderPath + fileName);
	}

	/**
	 * 取得生成文件的名字
	 * 
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	public static File getFile(String fileName, ReportTypeEnum fileType)
	{
		String fName = StringUtils.convertNULLtoString(fileName);
		if (StringUtils.isNullString(fName))
		{
			fName = DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP);
		}
		else
		{
			fName = fName + "_" + DateFormat.format(new Date(), DateFormat.PTN_TIMESTAMP);
		}

		if (!StringUtils.isNullString(fName))
		{
			for (String canNotUsedChar : CHAR_NOT_USED)
			{
				fName = fName.replace(canNotUsedChar, "_");
			}
		}

		File file = new File(EnvUtils.getRootPath() + "tmp/" + fName + GenericReportUtil.getExportFile(fileType));

		return file;
	}

	/**
	 * 设置导出报表文件后缀
	 * 
	 * @param exportFileType
	 * @return
	 */
	public static String getExportFile(ReportTypeEnum exportFileType)
	{
		if (exportFileType.equals(ReportTypeEnum.PDF))
		{
			return ".pdf";
		}
		else if (exportFileType.equals(ReportTypeEnum.EXCEL))
		{
			return ".xls";
		}
		else if (exportFileType.equals(ReportTypeEnum.CSV))
		{
			return ".csv";
		}
		else if (exportFileType.equals(ReportTypeEnum.HTML))
		{
			return ".html";
		}
		else if (exportFileType.equals(ReportTypeEnum.WORD))
		{
			return ".doc";
		}
		return ".xls";
	}

	public static String reportTitle(String name, LanguageEnum lang, FoundationObject object)
	{
		if (name == null)
		{
			name = StringUtils.getMsrTitle((String) object.get("BOTITLE$"), lang.getType());
		}
		else
		{
			for (String notUsedChar : GenericReportUtil.CHAR_NOT_USED)
			{
				if (name.contains(notUsedChar))
				{
					name = StringUtils.getMsrTitle((String) object.get("BOTITLE$"), lang.getType());
				}
			}
		}

		return name;
	}

	public static String getReportTemplateNameBylang(LanguageEnum lang, String reportTemplateName)
	{

		String newReportTemplateName = "";
		if (lang.equals(LanguageEnum.ZH_CN))
		{
			newReportTemplateName = reportTemplateName.concat("_zh_cn.jrxml");
		}
		else if (lang.equals(LanguageEnum.ZH_TW))
		{
			newReportTemplateName = reportTemplateName.concat("_zh_tw.jrxml");
		}
		else if (lang.equals(LanguageEnum.EN))
		{
			newReportTemplateName = reportTemplateName.concat("_us_en.jrxml");
		}
		return newReportTemplateName;
	}

	public String getFieldValue(Map<String, Object> fo, String classNameOrClfGuid, String fieldName, boolean isClassification, JRPropertiesMap propertiesMap)
			throws ServiceRequestException
	{
		if (this.params == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		fieldName = fieldName.toUpperCase();
		if ("BOTITLE$".equals(fieldName))
		{
			return this.getTitle((String) fo.get(fieldName));
		}

		if ("FULLNAME$".equals(fieldName))
		{
			return (String) fo.get(fieldName);
		}

		String fieldObjectName = null;
		String propertyName = null;
		if (fieldName.endsWith("$NAME") || fieldName.endsWith("$NAME$"))
		{
			if (fo.get(fieldName) != null)
			{
				return (String) fo.get(fieldName);
			}
			else if (fieldName.endsWith("$"))
			{
				return (String) fo.get(fieldName.substring(0, fieldName.length() - 1));
			}
			else
			{
				fieldObjectName = fieldName.substring(0, fieldName.length() - 5);
				propertyName = "NAME";
			}
		}
		else if (fieldName.endsWith("$TITLE"))
		{
			fieldObjectName = fieldName.substring(0, fieldName.length() - 6);
			propertyName = "TITLE";
		}
		else if (fieldName.indexOf("$") != -1)
		{
			fieldObjectName = fieldName.substring(0, fieldName.indexOf("$"));
			propertyName = fieldName.substring(fieldName.indexOf("$") + 1, fieldName.length());
		}
		else
		{
			fieldObjectName = fieldName;
		}

		// if(Arrays.asList(SPECIAL_FIELD_NAME).contains(fieldObjectName))
		// {
		// fieldObjectName = fieldObjectName + "$";
		// }

		Map<String, Object> fieldInfo = null;
		if (isClassification)
		{
			fieldInfo = this.buildClfFieldInfo(classNameOrClfGuid, fieldObjectName);
		}
		else
		{
			String classname = classNameOrClfGuid;
			if (StringUtils.isGuid(classname))
			{
				ClassInfo classInfo = this.params.getEMM().getClassByGuid(classname);
				if (classInfo != null)
				{
					classname = classInfo.getName();
				}
			}
			if (classname == null)
			{
				return null;
			}
			ClassField sfield = null;
			try
			{
				sfield = this.params.getEMM().getFieldByName(classname, fieldObjectName, true);
			}
			catch (Exception e)
			{

			}
			if (sfield != null && sfield.isSystem())
			{
				fieldObjectName = sfield.getName();
			}

			fieldInfo = buildFieldInfo(classNameOrClfGuid, fieldObjectName);
		}

		if (fieldInfo == null || fieldInfo.isEmpty())
		{
			return this.getValOfFieldString(fo.get(fieldName));
		}
		else
		{
			ClassField field = (ClassField) fieldInfo.get("FIELD");
			String pattern = (String) fieldInfo.get("FORMATPATTERN");

			FieldTypeEnum fieldType = field.getType();
			if (FieldTypeEnum.DATE.equals(fieldType))
			{
				if (pattern == null)
				{
					pattern = DateFormat.PTN_YMD;
				}
				return this.getTimeVal(fo, fieldObjectName, pattern);
			}
			if (FieldTypeEnum.DATETIME.equals(fieldType))
			{
				if (pattern == null)
				{
					pattern = DateFormat.PTN_YMDHMS;
				}
				return this.getTimeVal(fo, fieldObjectName, pattern);
			}
			else if (FieldTypeEnum.FLOAT.equals(fieldType) || FieldTypeEnum.INTEGER.equals(fieldType))
			{
				if (propertiesMap!=null)
				{
					pattern = propertiesMap.getProperty("Pattern");
				}
				return null == fo.get(fieldObjectName) ? StringUtils.EMPTY_STRING : this.getBigDecimalVal(fo, fieldObjectName, pattern);
			}
			else if (FieldTypeEnum.CODE.equals(fieldType) || FieldTypeEnum.CODEREF.equals(fieldType) || FieldTypeEnum.CLASSIFICATION.equals(fieldType))
			{
				String val = null;
				if (StringUtils.isNullString(propertyName))
				{
					propertyName = "TITLE";
				}

				// String nameKey = null;
				if (fieldObjectName.endsWith("$"))
				{
					val = null == fo.get(fieldObjectName) ? StringUtils.EMPTY_STRING : (String) fo.get(fieldObjectName + propertyName);
					// nameKey = "NAME";
				}
				else
				{
					val = null == fo.get(fieldObjectName) ? StringUtils.EMPTY_STRING : (String) fo.get(fieldObjectName + "$" + propertyName);
					// nameKey = "$NAME";
				}

				if (!StringUtils.isNullString(val))
				{
					if ("TITLE".equals(propertyName))
					{
						// return this.getTitle(val) + "[" + fo.get(fieldObjectName + nameKey) + "]";
						if (StringUtils.isNullString(this.getTitle(val)))
						{
							return val;
						}
						else
						{
							return this.getTitle(val);
						}
					}
					else
					{
						return val;
					}
				}

				String codeGuid = (String) fo.get(fieldObjectName);
				CodeItemInfo codeItemInfo = this.params.getEMM().getCodeItem(codeGuid);
				if (codeItemInfo == null)
				{
					return StringUtils.EMPTY_STRING;
				}
				else
				{
					if ("TITLE".equals(propertyName))
					{
						// return this.getTitle(codeItemInfo.getTitle()) + "[" + codeItemInfo.getCode() + "]";
						return this.getTitle(val);
					}
					else
					{
						return codeItemInfo.getName();
					}
				}
			}
			else if (FieldTypeEnum.STATUS.equals(fieldType))
			{
				// 状态类型特殊处理
				String status = (String) fo.get(fieldObjectName);
				SystemStatusEnum statusEnmu = SystemStatusEnum.getStatusEnum(status);
				return StringUtils.convertNULLtoString(this.params.getMSRM().getMSRString(statusEnmu.getMsrId(), this.params.getLang().toString()));
			}
			else if (FieldTypeEnum.MULTICODE.equals(fieldType))
			{
				if (fo.get(fieldObjectName) == null)
				{
					return StringUtils.EMPTY_STRING;
				}

				StringBuffer multicode = new StringBuffer();
				// MULTICODE类型的字段，其在数据库中的格式是使用[;]连接的guid，返回值也是用[;]连接
				String[] codeGuids = ((String) fo.get(fieldObjectName)).split(";");
				for (String codeGuid : codeGuids)
				{
					CodeItemInfo codeItemInfo = this.params.getEMM().getCodeItem(codeGuid);
					if (codeItemInfo == null)
					{
						multicode.append(";");
						continue;
					}

					if (multicode.length() != 0)
					{
						multicode.append(";");
					}

					if (propertyName == null || "TITLE".equals(propertyName))
					{
						multicode.append(this.getTitle(codeItemInfo.getTitle()));
					}
					else
					{
						multicode.append(codeItemInfo.getName());
					}
				}
				return multicode.toString();
			}
			else if (FieldTypeEnum.BOOLEAN.equals(fieldType))
			{
				String yes = this.params.getMSRM().getMSRString("ID_CLIENT_DIALOG_YES", this.params.getLang().toString());
				String no = this.params.getMSRM().getMSRString("ID_CLIENT_DIALOG_NO", this.params.getLang().toString());

				if (fo.get(fieldObjectName) == null)
				{
					return no;
				}

				String val = (String) fo.get(fieldObjectName);
				if (StringUtils.isNullString(val))
				{
					return no;
				}
				return val.equals("N") ? no : yes;
			}
			else if (FieldTypeEnum.FOLDER.equals(fieldType))
			{
				if (fo.get(fieldObjectName) == null)
				{
					return StringUtils.EMPTY_STRING;
				}
				String folderGuid = (String) fo.get(fieldObjectName);
				Folder folder = this.params.getEDAP().getFolder(folderGuid);
				if (folder != null)
				{
					return folder.getHierarchy();
				}
				return this.getValOfFieldString(fo.get(fieldObjectName));
			}
			else if (FieldTypeEnum.OBJECT.equals(fieldType))
			{
				if (field != null && field.isSystem())
				{
					// 系统中的OWNERUSER$和OWNERGROUP$的特殊处理
					if (fieldObjectName.endsWith("USER$") || fieldObjectName.endsWith("GROUP$"))
					{
						return StringUtils.convertNULLtoString(fo.get(fieldObjectName + "NAME"));
					}
				}
				String guid = (String) fo.get(fieldObjectName);
				if (!StringUtils.isGuid(guid))
				{
					return StringUtils.convertNULLtoString(fo.get(fieldName));
				}

				// Object中的特殊字段类型（非FoundationObject类型）判断
				ClassInfo classInfo = this.params.getEMM().getClassByName(field.getTypeValue());
				if (classInfo != null)
				{
					if (classInfo.hasInterface(ModelInterfaceEnum.IUser) || classInfo.hasInterface(ModelInterfaceEnum.IGroup)
							|| classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar) || classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
					{
						return this.getSpecialObjectName(classInfo, guid, field);
					}
				}

				// Object字段类型不设置要查询的属性,默认为FullName
				if (StringUtils.isNullString(propertyName) || "NAME".equals(propertyName))
				{
					return StringUtils.convertNULLtoString(fo.get(fieldName + "$FULLNAME"));
				}

				String classguidKey = (fieldObjectName.endsWith("$") ? fieldObjectName + "CLASS" : fieldObjectName + "$CLASS");
				String masterguiddKey = (fieldObjectName.endsWith("$") ? fieldObjectName + "MASTER" : fieldObjectName + "$MASTER");

				// X$CLASS、X$MASTER
				FoundationObjectImpl founObj = this.getObjectByPropertyKey(guid, (String) fo.get(classguidKey), (String) fo.get(masterguiddKey));
				if (founObj == null)
				{
					// X$CLASSGUID、X$MASTERGUID
					founObj = this.getObjectByPropertyKey(guid, (String) fo.get(classguidKey + "GUID"), (String) fo.get(masterguiddKey + "GUID"));
					if (founObj == null)
					{
						if (founObj == null)
						{
							// X$CLASSGUID、X$MASTERFK
							founObj = this.getObjectByPropertyKey(guid, (String) fo.get(classguidKey + "GUID"), (String) fo.get(masterguiddKey + "FK"));
							if (founObj == null)
							{
								// 如果Object类型的字段，没有取到对应的Object对象，则直接从参数对象中取得参数字段的值。
								return StringUtils.convertNULLtoString(fo.get(fieldName));
							}
						}
					}
				}

				if (!founObj.getObjectGuid().hasAuth())
				{
					return "-";
				}

				String val = StringUtils.convertNULLtoString(founObj.get(propertyName));
				if (StringUtils.isNullString(val))
				{
					// 个别情况下，对象存储Object类型的数据时，其上的系统字段属性可能会缺少$，如：NAME
					// 对象中存储的是X$NAME，但NAME是系统字段，从X上查询时，需要使用NAME$
					val = StringUtils.convertNULLtoString(founObj.get(propertyName + "$"));
					if (StringUtils.isNullString(val))
					{
						// 没查询到值，则直接从参数对象返回参数字段对应的值
						return StringUtils.convertNULLtoString(fo.get(fieldName));
					}
					return StringUtils.convertNULLtoString(val);
				}
				else
				{
					return this.getValOfFieldString(founObj.get(propertyName));
				}

			}
			else if (fieldObjectName.startsWith("LIFECYCLEPHASE$"))
			{
				String lifecyclephase = (String) fo.get("LIFECYCLEPHASE$");
				if (StringUtils.isGuid(lifecyclephase))
				{
					LifecyclePhaseInfo lifecyclePhaseInfo = this.params.getEMM().getLifecyclePhaseInfo(lifecyclephase);
					if (lifecyclePhaseInfo == null)
					{
						return this.getValOfFieldString(fo.get(fieldName));
					}
					if ("TITLE".equals(propertyName) || StringUtils.isNullString(propertyName))
					{
						return this.getTitle(lifecyclePhaseInfo.getTitle());
					}
					else
					{
						return this.getValOfFieldString(lifecyclePhaseInfo.getName());
					}
				}
				return this.getValOfFieldString(fo.get(fieldName));
			}
			else
			{
				return this.getValOfFieldString(fo.get(fieldObjectName));
			}
		}
	}

	public String getFieldValue(Map<String, Object> fo, String className, String fieldName, JRPropertiesMap propertiesMap) throws ServiceRequestException
	{
		return this.getFieldValue(fo, className, fieldName, false, propertiesMap);
	}

	public String getFieldValue(FoundationObjectImpl object, String fieldName, JRPropertiesMap propertiesMap) throws ServiceRequestException
	{
		if (object == null)
		{
			return StringUtils.EMPTY_STRING;
		}
		return this.getFieldValue(object, object.getObjectGuid().getClassName(), fieldName, propertiesMap);
	}

	private String getSpecialObjectName(ClassInfo classInfo, String guid, ClassField field) throws ServiceRequestException
	{
		if (classInfo == null || guid == null || field == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		SystemObjectImpl object = null;
		if (classInfo.hasInterface(ModelInterfaceEnum.IGroup))
		{
			object = this.params.getAAS().getGroup(guid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IUser))
		{
			object = this.params.getAAS().getUser(guid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMRole))
		{
			object = this.params.getPPMS().getProjectRole(guid);
		}
		else if (classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			object = this.params.getPPMS().getWorkCalendar(guid);
		}

		return object == null ? StringUtils.EMPTY_STRING : object.getName();
	}

	private String getValOfFieldString(Object obj)
	{
		if (obj == null)
		{
			return StringUtils.EMPTY_STRING;
		}
		if (obj instanceof BigDecimal)
		{
			BigDecimal decimal = (BigDecimal) obj;
			return decimal.toString();
		}
		else
		{
			return obj.toString();
		}
	}

	private String getTimeVal(Map<String, Object> o, String fieldName, String pattern)
	{
		Calendar calendar = Calendar.getInstance();
		Object obj = o.get(fieldName);
		if (obj == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		if (obj instanceof String)
		{
			return o.get(fieldName).toString();
		}
		else if (obj instanceof Timestamp)
		{
			Timestamp time = (Timestamp) o.get(fieldName);
			if (time == null)
			{
				return StringUtils.EMPTY_STRING;
			}
			calendar.setTimeInMillis(time.getTime());
			return DateFormat.format(calendar.getTime(), pattern);
		}
		else if (obj instanceof Date)
		{
			calendar.setTime((Date) obj);
			return DateFormat.format(calendar.getTime(), pattern);
		}
		else
		{
			return o.get(fieldName).toString();
		}
	}

	private String getBigDecimalVal(Map<String, Object> fo, String fieldName, String pattern)
	{
		if (fo.get(fieldName) == null)
		{
			return StringUtils.EMPTY_STRING;
		}

		if (fo.get(fieldName) instanceof BigDecimal)
		{
			BigDecimal decimal = (BigDecimal) fo.get(fieldName);
			if (StringUtils.isNull(pattern) || pattern.indexOf(".") == -1)
			{
				return decimal.toString();
			}
			return decimal.setScale(pattern.substring(pattern.lastIndexOf(".") + 1).length(), RoundingMode.HALF_UP).toString();
		}
		else if (NumberUtils.isNumeric(fo.get(fieldName).toString()))
		{
			BigDecimal decimal = new BigDecimal(fo.get(fieldName).toString());
			if (StringUtils.isNull(pattern) || pattern.indexOf(".") == -1)
			{
				return decimal.toString();
			}
			return decimal.setScale(pattern.substring(pattern.lastIndexOf(".") + 1).length(), RoundingMode.HALF_UP).toString();
		}
		else
		{
			return fo.get(fieldName).toString();
		}
	}

	private Map<String, Object> buildClfFieldInfo(String classificationItemGuid, String fieldName) throws ServiceRequestException
	{
		Map<String, Object> fieldInfoMap = new HashMap<String, Object>();

		if (!StringUtils.isGuid(classificationItemGuid))
		{
			return null;
		}

		ClassField field = null;
		try
		{
			field = ((EMMImpl) this.params.getEMM()).getClassificationStub().getClassificationField(classificationItemGuid, fieldName);
		}
		catch (ServiceRequestException e)
		{
			return null;
		}

		if (field != null)
		{
			fieldInfoMap.put("FIELD", field);
		}
		if (this.params.getCFUIObject() != null)
		{
			UIObjectInfo uiInfo = this.params.getCFUIObject();
			List<UIField> fieldList = this.params.getEMM().listCFUIField(uiInfo.getGuid());
			if (fieldList != null)
			{
				for (UIField uiField : fieldList)
				{
					if (uiField.getName().equalsIgnoreCase(fieldName))
					{
						fieldInfoMap.put("FORMATPATTERN", uiField.getFormat());
						break;
					}
				}
			}
		}

		return fieldInfoMap;
	}

	private Map<String, Object> buildFieldInfo(String className, String fieldName) throws ServiceRequestException
	{
		Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
		ClassInfo classInfo = null;
		try
		{
			classInfo = this.params.getEMM().getClassByName(className);
		}
		catch (ServiceRequestException e)
		{
			return null;
		}

		if (classInfo == null)
		{
			return null;
		}

		ClassField field = null;
		try
		{
			field = this.params.getEMM().getFieldByName(className, fieldName, true);
		}
		catch (ServiceRequestException e)
		{
			return null;
		}

		if (field != null)
		{
			fieldInfoMap.put("FIELD", field);
		}
		if (this.params.getUiObject() != null)
		{
			UIObjectInfo uiObject = this.params.getUiObject();
			List<UIField> fieldList = this.params.getEMM().listUIFieldByUIGuid(uiObject.getGuid());
			if (fieldList != null)
			{
				for (UIField uiField : fieldList)
				{
					if (uiField.getName().equalsIgnoreCase(fieldName))
					{
						fieldInfoMap.put("FORMATPATTERN", uiField.getFormat());
						break;
					}
				}
			}
		}

		return fieldInfoMap;
	}

	private String getTitle(String title)
	{
		if (StringUtils.isNullString(title))
		{
			return StringUtils.EMPTY_STRING;
		}

		return StringUtils.convertNULLtoString(StringUtils.getMsrTitle(title, this.params.getLang().getType()));
	}

	private FoundationObjectImpl getObjectByPropertyKey(String propertyGuid, String propertyClassGuid, String propertyMasterGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(propertyGuid) || !StringUtils.isGuid(propertyClassGuid) || !StringUtils.isGuid(propertyMasterGuid))
		{
			return null;
		}

		ObjectGuid oGuid = new ObjectGuid();
		oGuid.setGuid(propertyGuid);
		oGuid.setClassGuid(propertyClassGuid);
		oGuid.setMasterGuid(propertyMasterGuid);
		try
		{
			return (FoundationObjectImpl) this.params.getBOAS().getObject(oGuid);
		}
		catch (ServiceRequestException e)
		{
			if (!StringUtils.isNullString(e.getMessage()) && e.getMessage().contains("NO READ AUTH"))
			{
				oGuid.setHasAuth(false);
				FoundationObjectImpl fo = (FoundationObjectImpl) this.params.getBOAS().newFoundationObject(propertyClassGuid, null);
				fo.setObjectGuid(oGuid);

				return fo;
			}

			return null;
		}
	}
}