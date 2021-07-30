/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Foundation Object bean
 * xiasheng Apr 22, 2010
 */
package dyna.common.bean.data;

import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.context.ScriptContext;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;

public class StructureObjectImpl extends ShortObjectImpl implements StructureObject
{
	private static final long	serialVersionUID	= -2701181509437162914L;

	private ObjectGuid			viewObjectGuid		= null;

	private ObjectGuid			end1ObjectGuid		= null;

	private ObjectGuid			end2ObjectGuid		= null;

	private ScriptContext		scriptContext		= null;

	public static final String	BOM_OPERATION_TYPE	= "BOMOPERATIONTYPE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#getEnd1ObjectGuid()
	 */
	@Override
	public ObjectGuid getEnd1ObjectGuid()
	{
		if (this.end1ObjectGuid == null)
		{
			String fieldName = ViewObject.END1;
			this.end1ObjectGuid = new ObjectGuid((String) this.get(fieldName + "$CLASS"), // classguid
					(String) this.get(fieldName + "$CLASSNAME"), // classname
					(String) this.get(fieldName), // guid
					(String) this.get(fieldName + "$MASTER"), // master
					null);

		}

		return this.end1ObjectGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#getEnd2ObjectGuid()
	 */
	@Override
	public ObjectGuid getEnd2ObjectGuid()
	{
		if (this.end2ObjectGuid == null)
		{
			String fieldName = SystemClassFieldEnum.END2.getName();
			this.end2ObjectGuid = new ObjectGuid((String) this.get(fieldName + "CLASS"), // classgui
					(String) this.get(fieldName + "CLASSNAME"), // classname
					(String) this.get(fieldName), // guid
					(String) this.get("END2$MASTER"), // masterguid
					this.get("ISMASTER") == null ? false : BooleanUtils.getBooleanByYN((String) this.get("ISMASTER")), // isMaster
					(String) this.get("BOGUID$"), (String) this.get(SystemClassFieldEnum.COMMITFOLDER.getName()) // commitfolderguid
			);
		}

		return this.end2ObjectGuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObjectImpl#getIcon()
	 */
	@Override
	public String getIcon()
	{
		return (String) this.get(SystemClassFieldEnum.END2.getName() + "CLASS$ICON");
	}

	@Override
	public void setGuid(String guid)
	{
		super.put("GUID$", guid);
	}

	@Override
	public String getGuid()
	{
		return (String) super.get("GUID$");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#getViewObjectGuid()
	 */
	@Override
	public ObjectGuid getViewObjectGuid()
	{
		if (this.viewObjectGuid == null)
		{
			this.viewObjectGuid = new ObjectGuid((String) this.get(SystemClassFieldEnum.VIEW.getName() + "CLASSGUID"),
					(String) this.get(SystemClassFieldEnum.VIEW.getName() + "CLASSNAME"), (String) this.get(SystemClassFieldEnum.VIEW.getName()),
					(String) this.get(SystemClassFieldEnum.VIEW.getName() + "MASTERFK"), null);
		}

		return this.viewObjectGuid;
	}

	@Override
	public ObjectGuid resetEnd1ObjectGuid()
	{
		this.end1ObjectGuid = null;
		return this.getEnd1ObjectGuid();
	}

	@Override
	public ObjectGuid resetEnd2ObjectGuid()
	{
		if (this.end2ObjectGuid == null)
		{
			return this.getEnd2ObjectGuid();
		}

		boolean hasAuth = this.end2ObjectGuid.hasAuth();
		this.end2ObjectGuid = null;
		ObjectGuid objGuid = this.getEnd2ObjectGuid();
		objGuid.setHasAuth(hasAuth);
		return objGuid;
	}

	@Override
	public ObjectGuid resetViewObjectGuid()
	{
		this.viewObjectGuid = null;
		return this.getViewObjectGuid();
	}

	@Override
	public void setEnd1ObjectGuid(ObjectGuid objectGuid)
	{
		this.end1ObjectGuid = objectGuid;
	}

	@Override
	public void setEnd2ObjectGuid(ObjectGuid objectGuid)
	{
		this.end2ObjectGuid = objectGuid;

		if (objectGuid == null)
		{
			objectGuid = new ObjectGuid();
		}

		String fieldName = SystemClassFieldEnum.END2.getName();
		this.put(fieldName, objectGuid.getGuid());
		this.put(fieldName + "BOGUID", objectGuid.getBizObjectGuid());
		this.put(fieldName + "CLASSNAME", objectGuid.getClassName());
		this.put(fieldName + "CLASS", objectGuid.getClassGuid());
		this.put(fieldName + "MASTER", objectGuid.getMasterGuid());
	}

	@Override
	public void setViewObjectGuid(ObjectGuid objectGuid)
	{
		this.viewObjectGuid = objectGuid;
	}

	@Override
	public String getSequence()
	{
		return (String) this.get("DATASEQ");
	}

	@Override
	public void setSequence(String sequence)
	{
		this.put("DATASEQ", sequence);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.InputObject#getScriptContext()
	 */
	@Override
	public ScriptContext getScriptContext()
	{
		return this.scriptContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.InputObject#setScriptContext(dyna.common.context.ScriptContext)
	 */
	@Override
	public void setScriptContext(ScriptContext context)
	{
		this.scriptContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#isEnd2Precise()
	 */
	@Override
	public Boolean isEnd2Precise()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(StructureObject.END2_PRECISE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#setsEnd2Precise(java.lang.Boolean)
	 */
	@Override
	public void setsEnd2Precise(Boolean isPrecise)
	{
		this.put(StructureObject.END2_PRECISE, BooleanUtils.getBooleanStringYN(isPrecise));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#getEnd2StructureClassName()
	 */
	@Override
	public String getEnd2StructureClassName()
	{
		return (String) this.get(StructureObject.END2_STRUCTURECLASSNAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#setEnd2StructureClassName(java.lang.String)
	 */
	@Override
	public void setEnd2StructureClassName(String structureClassName)
	{
		this.put(StructureObject.END2_STRUCTURECLASSNAME, structureClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#isPrimary()
	 */
	@Override
	public Boolean isPrimary()
	{
		String object = (String) this.get(ISPRIMARY);
		if (object == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.StructureObject#setParmary(java.lang.Boolean)
	 */
	@Override
	public void setParmary(Boolean isParmary)
	{
		this.put(ISPRIMARY, BooleanUtils.getBooleanStringYN(isParmary));
	}

	@Override
	public FoundationObject getEnd2UIObject()
	{
		return (FoundationObject) (this.get(END2_UI_OBJECT));
	}

	@Override
	public void setEnd2UIObject(FoundationObject end2UIObject)
	{
		this.put(END2_UI_OBJECT, end2UIObject);
	}

	@Override
	public String getOperationType()
	{
		return (String) this.get(BOM_OPERATION_TYPE);
	}

	@Override
	public void setOperationType(String type)
	{
		this.put(BOM_OPERATION_TYPE, type);
	}

}
