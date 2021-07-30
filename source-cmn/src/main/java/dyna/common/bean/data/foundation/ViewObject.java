/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: General Object bean
 * xiasheng 2010-7-14
 */
package dyna.common.bean.data.foundation;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.template.relation.RelationTemplateInfo;
//import dyna.common.bean.data.system.template.relation.RelationTemplate;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.StringUtils;

public class ViewObject extends FoundationObjectImpl implements FoundationObject
{
	private static final long	serialVersionUID	= 4274846508771784110L;

	private ObjectGuid			end1ObjectGuid		= null;

	private static String		END2_TYPE			= "END2TYPE";
	public static String		HAS_END2			= "HASEND2";

	public static final String	END1				= "End1";
	public static final String	ISPRECISE			= "IsPrecise";
	// public static final String VIEWTYPE = "ViewType";
	public static final String	END1CLASSGUID		= "End1$CLASS";
	public static final String	END1CLASSNAME		= "End1$CLASSNAME";
	public static final String	END1MASTER			= "END1$MASTER";
	public static final String	STRUCTURECLASS		= "STRUCTURECLASS";

	public static final String	VIEW_APPOINTED_ID	= "32AA50EA06FA4D4096E454A063BAA2AF";
	public static final String	TEMPLATE_ID			= "TemplateID";
	public static final String	TEMPLATE_TITLE		= "TemplateTitle";
	
	// 当进行结构查询时,可以把end1和end2的字段一起添加到searchcondition中，以此作前缀，例如：E1$NAME
	public static final String  PREFIX_END1         = "E1$_";
	public static final String  PREFIX_END2         = "E2$_";

	public ViewObject()
	{
		super();
	}

	public ViewObject(FoundationObject sourceObject)
	{
		super();
		this.putAll(((FoundationObjectImpl) sourceObject));
		this.originalMap.putAll(this);
	}

	public ObjectGuid getEnd1ObjectGuid()
	{
		if (this.end1ObjectGuid == null)
		{
			this.end1ObjectGuid = new ObjectGuid((String) this.get(END1CLASSGUID), (String) this.get(END1CLASSNAME),
					(String) this.get(END1), (String) this.get(END1MASTER), null);
		}
		return this.end1ObjectGuid;
	}

	public String getEnd2Type()
	{
		return (String) this.get(END2_TYPE);
	}

	public boolean hasEnd2()
	{
		if ("Y".equals(this.get(HAS_END2)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public String getTemplateID()
	{
		if (this.get(TEMPLATE_ID) == null)
		{
			return null;
		}
		return (String) this.get(TEMPLATE_ID);
	}

	public void setTemplateID(String end2Type)
	{
		this.put(TEMPLATE_ID, end2Type);
	}

	public void setEnd1ObjectGuid(ObjectGuid end1ObjectGuid)
	{
		if (end1ObjectGuid == null)
		{
			this.remove(END1.toUpperCase());
			this.remove(END1CLASSGUID.toUpperCase());
			this.remove(END1CLASSNAME.toUpperCase());
			this.remove(END1MASTER.toUpperCase());
		}
		else
		{
			this.end1ObjectGuid = end1ObjectGuid;

			this.put(END1.toUpperCase(), end1ObjectGuid.getGuid());
			this.put(END1CLASSGUID.toUpperCase(), end1ObjectGuid.getClassGuid());
			this.put(END1CLASSNAME.toUpperCase(), end1ObjectGuid.getClassName());
			this.put(END1MASTER.toUpperCase(), end1ObjectGuid.getMasterGuid());
		}
	}

	public void setEnd2Type(String end2Type)
	{
		this.put(END2_TYPE, end2Type);
	}

	@Override
	public void setName(String name)
	{
		this.put(SystemClassFieldEnum.NAME.getName(), name);
	}

	// public void setStructureClass(String classGuid)
	// {
	// this.put(ViewObject.STRUCTURECLASS, classGuid);
	// }
	//
	// public void setStructureClassName(String className)
	// {
	// this.put(ViewObject.STRUCTURECLASS + "NAME", className);
	// }

	// public Integer getMaxQuantity()
	// {
	// BigDecimal mq = (BigDecimal) this.get(ViewObject.MAXQUANTITY);
	// return mq == null ? null : mq.intValue();
	// }

	// public String getStructureClass()
	// {
	// return (String) this.get(ViewObject.STRUCTURECLASS);
	// }
	//
	// public String getStructureClassName()
	// {
	// return (String) this.get(ViewObject.STRUCTURECLASS + "NAME");
	// }

	public RelationTemplateTypeEnum getStructureModel()
	{
		return RelationTemplateTypeEnum.valueOf((String) this.get(RelationTemplateInfo.STRUCTURE_MODEL));
	}

	public void setStructureModel(RelationTemplateTypeEnum structureModel)
	{
		this.put(RelationTemplateInfo.STRUCTURE_MODEL, structureModel.name());
	}

	public String getTemplateTitle()
	{
		if (this.get(ViewObject.TEMPLATE_TITLE) == null)
		{
			return null;
		}
		return (String) this.get(ViewObject.TEMPLATE_TITLE);
	}

	public String getTemplateTitle(LanguageEnum lang)
	{
		String title = this.getTemplateTitle();
		return StringUtils.getMsrTitle(title, lang.getType());
	}

	public void setTemplateTitle(String title)
	{
		this.put(ViewObject.TEMPLATE_TITLE, title);
	}

}
