/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DecoratorFactory
 * Wanglei 2010-9-2
 */
package dyna.app.service.helper.decorate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ShortObject;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.Folder;
import dyna.common.dto.MailAttachment;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

/**
 * 着色器工厂
 * 
 * @author Wanglei
 * 
 */
public class DecoratorFactory
{

	public static ClassNameDecorator			cnd						= new ClassNameDecorator();
	public static ViewClassNameDecorator		vcnd					= new ViewClassNameDecorator();
	public static StructureCLassNameDecorator	scnd					= new StructureCLassNameDecorator();
	public static MailAttClassNameDecorator		mcnd					= new MailAttClassNameDecorator();

	public static FullNameDecorator				fnd						= new FullNameDecorator();
	public static StructureFullNameDecorator	sfnd					= new StructureFullNameDecorator();

	public static LCPhaseDecorator				lcpd					= new LCPhaseDecorator();

	public static BizObjectDecorator			bod						= new BizObjectDecorator();
	public static StructureBODecorator			sbod					= new StructureBODecorator();

	public static CodeTitleDecorator			ctd						= new CodeTitleDecorator();

	public static Set<String>					shortObjectCodeFieldSet	= new HashSet<String>();

	public static ObjectFieldDecorator			ofd						= new ObjectFieldDecorator();

	// public static ExecuteRoleDecorator erd = new ExecuteRoleDecorator();

	static
	{
		shortObjectCodeFieldSet.add(SystemClassFieldEnum.CLASSIFICATION.getName());
	}

	public static void decorateCodeRule(Set<String> objectFieldName, Set<String> codeFieldName, ShortObject object, EMM emm) throws DecorateException
	{
		cnd.decorateWithField(objectFieldName, object, emm);
		ctd.decorateWithField(codeFieldName, object, emm);
	}

	public static void decorateClassificaitonObject(Set<String> fieldNames, FoundationObject object, EMM emm, String bmGuid, Folder folder) throws DecorateException
	{
		cnd.decorateWithField(fieldNames, object, emm);

		if (!StringUtils.isNullString(bmGuid))
		{
			bod.decorateWithField(fieldNames, object, bmGuid, emm);
		}
	}

	public static void decorateFoundationObject(Set<String> fieldNames, List<FoundationObject> objectList, EMM emm, String sessionId) throws DecorateException
	{
		ofd.decorateWithField(fieldNames, objectList, emm, sessionId, false);
	}

	public static void decorateStructureObject(Set<String> fieldNames, List<StructureObject> objectList, EMM emm, String sessionId) throws DecorateException
	{
		ofd.decorateWithField(fieldNames, objectList, emm, sessionId, false);
	}

	public static void decorateBOMStructure(Set<String> fieldNames, List<BOMStructure> objectList, EMM emm, String sessionId) throws DecorateException
	{
		ofd.decorateWithField(fieldNames, objectList, emm, sessionId, false);
	}

	public static void decorateFoundationObject(Set<String> fieldNames, FoundationObject object, EMM emm, String bmGuid, Folder folder) throws DecorateException
	{
		// 主对象的 CLASS$ICON32,CLASS$ICON,CLASSNAME设值
		cnd.decorate(object, emm, folder);
		object.resetObjectGuid();

		// 对象中object类型的字段的 NAME,CLASS$ICON32,CLASS$ICON,CLASSNAME 设值
		cnd.decorateWithField(fieldNames, object, emm);

		if (!StringUtils.isNullString(bmGuid))
		{
			// 主对象BOTITLE,BOGUID 设值
			bod.decorate(object, bmGuid, emm);
			// 对象中object类型的字段的BOTITLE,BOGUID 设值
			bod.decorateWithField(fieldNames, object, bmGuid, emm);
		}
		// 主对象的 name设值
		fnd.decorate(object, emm);
		lcpd.decorate(object, emm);
		// erd.decorate(object, emm);
	}

	public static void decorateFoundationObjectCode(Set<String> fieldNames, FoundationObject object, EMM emm, String bmGuid) throws DecorateException
	{
		ctd.decorateWithField(fieldNames, object, emm);
	}

	public static void decorateMailAttachment(MailAttachment object, EMM emm) throws DecorateException
	{
		mcnd.decorate(object, emm);
	}

	public static void decorateShortObject(ShortObject object, EMM emm, String bmGuid, Folder folder) throws DecorateException
	{
		cnd.decorate(object, emm, folder);
		bod.decorate(object, bmGuid, emm);

		ctd.decorateWithField(shortObjectCodeFieldSet, object, emm);

		fnd.decorate(object, emm);
		lcpd.decorate(object, emm);
	}

	public static void decorateStructureObject(StructureObject object, Set<String> objectFieldNames, Set<String> codeFieldNames, EMM emm, String bmGuid)
			throws DecorateException, ServiceRequestException
	{
		scnd.decorate(object, emm);

		cnd.decorate(object, emm, null);
		object.resetObjectGuid();

		if (objectFieldNames != null)
		{
			// Object's className
			cnd.decorateWithField(objectFieldNames, object, emm);
		}

		if (object instanceof BOMStructure)
		{
			if (codeFieldNames == null)
			{
				codeFieldNames = new HashSet<String>();
			}
			if (!codeFieldNames.contains(BOMStructure.UOM))
			{
				codeFieldNames.add(BOMStructure.UOM);
			}

		}
		if (codeFieldNames != null)
		{
			// Code's Title
			ctd.decorateWithField(codeFieldNames, object, emm);
		}
		sfnd.decorate(object, emm);

		sbod.decorate(object, bmGuid, emm);

		lcpd.decorate(object, emm);
	}

	public static void decorateViewObject(ShortObject object, EMM emm, String bmGuid) throws DecorateException
	{
		vcnd.decorate(object, emm);
		fnd.decorate(object, emm);
		bod.decorate(object, bmGuid, emm);
	}
}
