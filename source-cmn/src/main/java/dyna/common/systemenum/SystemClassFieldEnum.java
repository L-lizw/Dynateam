/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 系统ClassField枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统ClassField枚举
 * 
 * @author Jiagang
 * 
 */
public enum SystemClassFieldEnum
{
	GUID("GUID$", "ID_SYS_FIELD_GUID", FieldTypeEnum.STRING), //
	MASTERFK("MASTERFK$", "ID_SYS_FIELD_MASTERFK", FieldTypeEnum.STRING), //
	ID("ID$", "ID_SYS_FIELD_ID", FieldTypeEnum.STRING), //
	NAME("NAME$", "ID_SYS_FIELD_NAME", FieldTypeEnum.STRING), //
	ALTERID("ALTERID$", "ID_SYS_FIELD_ALTERID", FieldTypeEnum.STRING), //
	BOGUID("BOGUID$", "ID_SYS_FIELD_BOGUID", FieldTypeEnum.STRING), //
	CLASSGUID("CLASSGUID$", "ID_SYS_FIELD_BIZOBJECT", FieldTypeEnum.STRING), //
	CLASSIFICATION("CLASSIFICATION$", "ID_SYS_FIELD_CLASSIFICATION", FieldTypeEnum.CLASSIFICATION), //
	REVISIONID("REVISIONID$", "ID_SYS_FIELD_REVISION", FieldTypeEnum.STRING), //
	REVISIONIDSEQUENCE("REVISIONIDSEQUENCE$", "ID_SYS_FIELD_RQ", FieldTypeEnum.INTEGER), //
	LATESTREVISION("LATESTREVISION$", "ID_SYS_FIELD_LATESTREVISION", FieldTypeEnum.STRING), //
	ITERATIONID("ITERATIONID$", "ID_SYS_FIELD_ITERATIONID", FieldTypeEnum.INTEGER), //
	ISCHECKOUT("ISCHECKOUT$", "ID_SYS_FIELD_ISCHECKOUT", FieldTypeEnum.BOOLEAN), //
	CHECKOUTUSER("CHECKOUTUSER$", "ID_SYS_FIELD_CHECKOUTUSER", FieldTypeEnum.OBJECT), //
	CHECKOUTTIME("CHECKOUTTIME$", "ID_SYS_FIELD_CHECKOUTTIME", FieldTypeEnum.DATETIME), //
	// 没入库之前代表数据属于哪个库，入库后代表入了哪个库，guid,
	LOCATIONLIB("LOCATIONLIB$", "ID_SYS_FIELD_LOCATIONLIB", FieldTypeEnum.FOLDER), //
	// 入库文件夹guid，入库后可以为空
	COMMITFOLDER("COMMITFOLDER$", "ID_SYS_FIELD_COMMITFOLDER", FieldTypeEnum.FOLDER), //
	STATUS("STATUS$", "ID_SYS_FIELD_STATUS", FieldTypeEnum.STATUS), //
	LCPHASE("LIFECYCLEPHASE$", "ID_SYS_FIELD_LIFECYCLEPHASE", FieldTypeEnum.STRING), //
	RELEASETIME("RELEASETIME$", "ID_SYS_FIELD_RELEASETIME", FieldTypeEnum.DATETIME), //
	OBSOLETETIME("OBSOLETETIME$", "ID_SYS_FIELD_OBSOLETETIME", FieldTypeEnum.DATETIME), //
	OBSOLETEUSER("OBSOLETEUSER$", "ID_SYS_FIELD_OBSOLETEUSER", FieldTypeEnum.OBJECT), //
	OWNERUSER("OWNERUSER$", "ID_SYS_FIELD_OWNERUSER", FieldTypeEnum.OBJECT), //
	OWNERGROUP("OWNERGROUP$", "ID_SYS_FIELD_OWNERGROUP", FieldTypeEnum.OBJECT), //
	CREATETIME("CREATETIME$", "ID_SYS_FIELD_CREATETIME", FieldTypeEnum.DATETIME), //
	CREATEUSER("CREATEUSER$", "ID_SYS_FIELD_CREATEUSER", FieldTypeEnum.OBJECT), //
	UPDATETIME("UPDATETIME$", "ID_SYS_FIELD_UPDATETIME", FieldTypeEnum.DATETIME), //
	UPDATEUSER("UPDATEUSER$", "ID_SYS_FIELD_UPDATEUSER", FieldTypeEnum.OBJECT), //
	FILEGUID("FILEGUID$", "ID_SYS_FIELD_FILE", FieldTypeEnum.STRING), //
	FILENAME("FILENAME$", "ID_SYS_FIELD_FILE", FieldTypeEnum.STRING), //
	FILETYPE("FILETYPE$", "ID_SYS_FIELD_FILE", FieldTypeEnum.STRING), //
	ECFLAG("ECFLAG$", "ID_SYS_FIELD_ECFLAG", FieldTypeEnum.OBJECT), //
	// 20130204-此字段暂无用
	ISEXPORTTOERP("ISEXPORTTOERP$", "ID_SYS_FIELD_ISEXPORTTOERP", FieldTypeEnum.BOOLEAN), //
	UNIQUES("UNIQUES$", "ID_SYS_FIELD_UNIQUE", FieldTypeEnum.STRING), //
	REPEAT("REPEAT$", "ID_SYS_FIELD_REPEAT", FieldTypeEnum.STRING), //
	MD5("MD5$", "ID_SYS_FIELD_MD5", FieldTypeEnum.STRING), //
	// 下一个版本的发布时间
	NEXTREVISIONRLSTIME("NEXTREVISIONRLSTIME$", "ID_SYS_FIELD_NEXTREVISIONRLSTIME", FieldTypeEnum.DATETIME), //
	// 自定义起始版本号
	CUSTSTARTREVIDSEQUENCE("CUSTSTARTREVIDSEQUENCE$", "ID_SYS_FIELD_CUSTSTARTREVIDSEQUENCE", FieldTypeEnum.INTEGER), //

	/**
	 * for structure class
	 * 
	 */
	END2("END2$", "ID_SYS_FIELD_END2", FieldTypeEnum.OBJECT), //
	VIEW("VIEWFK$", "ID_SYS_FIELD_VIEW", FieldTypeEnum.OBJECT), //
	VIEWCLASSGUID("VIEWCLASSGUID$", "ID_SYS_FIELD_VIEW", FieldTypeEnum.OBJECT), //

	CLASSIFICATIONGROUP("CLASSIFICATIONGROUP$", "ID_SYS_FIELD_CLASSIFICATIONGROUP", FieldTypeEnum.STRING) //
	;

	private FieldTypeEnum	type;
	private String			name;
	private String			description;

	private SystemClassFieldEnum(String name, String description, FieldTypeEnum type)
	{
		this.type = type;
		this.name = name;
		this.description = description;
	}

	/*
	 * 当把这些字段放在ui上时，不做必须检查
	 */
	public static List<SystemClassFieldEnum> getNoCheckMandatoryClassFieldList()
	{
		List<SystemClassFieldEnum> list = new ArrayList<SystemClassFieldEnum>();
		list.add(GUID);
		list.add(MASTERFK);
		list.add(BOGUID);
		list.add(CLASSGUID);
		list.add(REVISIONID);
		list.add(REVISIONIDSEQUENCE);
		list.add(LATESTREVISION);
		list.add(ITERATIONID);
		list.add(ISCHECKOUT);
		list.add(CHECKOUTUSER);
		list.add(CHECKOUTTIME);
		list.add(LOCATIONLIB);
		list.add(STATUS);
		list.add(LCPHASE);
		list.add(RELEASETIME);
		list.add(OBSOLETETIME);
		list.add(OBSOLETEUSER);
		list.add(UNIQUES);
		list.add(REPEAT);
		list.add(MD5);
		list.add(CREATEUSER);
		list.add(CREATETIME);
		list.add(UPDATEUSER);
		list.add(UPDATETIME);
		list.add(NEXTREVISIONRLSTIME);

		list.add(VIEW);
		list.add(VIEWCLASSGUID);

		return list;
	}

	public static List<String> getFoundationSystemClassFieldList()
	{
		List<String> fieldList = new ArrayList<String>();
		for (SystemClassFieldEnum fieldEnum : SystemClassFieldEnum.values())
		{
			if (fieldEnum == END2 || fieldEnum == VIEW || fieldEnum == VIEWCLASSGUID || fieldEnum == CLASSIFICATIONGROUP)
			{
				continue;
			}
			fieldList.add(fieldEnum.getName());
		}
		return fieldList;
	}

	public static List<String> getStructureSystemClassFieldList()
	{
		List<String> list = new ArrayList<String>();
		list.add(VIEW.getName());
		list.add(VIEWCLASSGUID.getName());
		list.add(CLASSGUID.getName());
		list.add(END2.getName());
		list.add(CREATEUSER.getName());
		list.add(CREATETIME.getName());
		list.add(UPDATEUSER.getName());
		list.add(UPDATETIME.getName());

		return list;
	}

	public String getName()
	{
		return this.name;
	}

	public String getDescription()
	{
		return this.description;
	}

	public FieldTypeEnum getType()
	{
		return this.type;
	}

	public static SystemClassFieldEnum getByName(String name)
	{
		for (SystemClassFieldEnum e : SystemClassFieldEnum.values())
		{
			if (name.equals(e.getName()))
			{
				return e;
			}
		}
		return null;
	}
}