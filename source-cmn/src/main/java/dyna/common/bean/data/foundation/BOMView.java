/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMView
 * caogc 2010-9-17
 */
package dyna.common.bean.data.foundation;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * 
 * @author caogc
 * 
 */
public class BOMView extends FoundationObjectImpl implements FoundationObject
{

	private static final long	serialVersionUID	= -1496953052023292127L;
	private ObjectGuid			end1ObjectGuid		= null;

	public BOMView()
	{
		super();
	}

	public BOMView(FoundationObject sourceObject)
	{
		super();
		this.putAll(((FoundationObjectImpl) sourceObject));
		this.originalMap.putAll(this);
	}

	public ObjectGuid getEnd1ObjectGuid()
	{
		if (this.end1ObjectGuid == null)
		{
			this.end1ObjectGuid = new ObjectGuid((String) this.get(ViewObject.END1CLASSGUID),
					(String) this.get(ViewObject.END1CLASSNAME), (String) this.get(ViewObject.END1),
					(String) this.get(ViewObject.END1MASTER), null);
		}
		return this.end1ObjectGuid;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(SystemClassFieldEnum.NAME.getName());
	}

	public String getStructureClass()
	{
		return (String) this.get(ViewObject.STRUCTURECLASS);
	}

	public String getStructureClassName()
	{
		return (String) this.get(ViewObject.STRUCTURECLASS + "NAME");
	}

	public boolean isPrecise()
	{
		if (this.get(ViewObject.ISPRECISE) == null)
		{
			return false;
		}

		return "Y".equals(this.get(ViewObject.ISPRECISE));
	}

	public void setEnd1ObjectGuid(ObjectGuid end1ObjectGuid)
	{
		if (end1ObjectGuid == null)
		{
			this.remove(ViewObject.END1.toUpperCase());
			this.remove(ViewObject.END1CLASSGUID.toUpperCase());
			this.remove(ViewObject.END1CLASSNAME.toUpperCase());
			this.remove(ViewObject.END1MASTER.toUpperCase());
		}
		else
		{
			this.end1ObjectGuid = end1ObjectGuid;

			this.put(ViewObject.END1.toUpperCase(), end1ObjectGuid.getGuid());
			this.put(ViewObject.END1CLASSGUID.toUpperCase(), end1ObjectGuid.getClassGuid());
			this.put(ViewObject.END1CLASSNAME.toUpperCase(), end1ObjectGuid.getClassName());
			this.put(ViewObject.END1MASTER.toUpperCase(), end1ObjectGuid.getMasterGuid());
		}
	}

	@Override
	public void setName(String name)
	{
		this.put(SystemClassFieldEnum.NAME.getName(), name);
	}

	public void setPrecise(Boolean isPrecise)
	{
		this.put(ViewObject.ISPRECISE, BooleanUtils.getBooleanStringYN(isPrecise));
	}

	public void setStructureClass(String classGuid)
	{
		this.put(ViewObject.STRUCTURECLASS, classGuid);
	}

	public void setStructureClassName(String className)
	{
		this.put(ViewObject.STRUCTURECLASS + "NAME", className);
	}

	public String getTemplateID()
	{
		if (this.get(ViewObject.TEMPLATE_ID) == null)
		{
			return null;
		}
		return (String) this.get(ViewObject.TEMPLATE_ID);
	}

	public void setTemplateID(String end2Type)
	{
		this.put(ViewObject.TEMPLATE_ID, end2Type);
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

	public boolean hasEnd2()
	{
		if ("Y".equals(this.get(ViewObject.HAS_END2)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
