/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoundationObject
 * xiasheng May 7, 2010
 */
package dyna.common.bean.data;

/**
 * @author xiasheng
 * 
 */
public interface StructureObject extends ShortObject, InputObject
{
	public static final String	STRUCTURE_CLASS_NAME			= "StructureObject";
	public static final String	CAD_STRUCTURE_CLASS_NAME		= "CADStructure";
	public static final String	REPLACE_STRUCTURE_CLASS_NAME	= "ReplaceSubstituteStructure";
	public static final String	FIELD_NAME_BOMRELATED			= "BOMRelated";
	public static final String	FIELD_NAME_FILE_PATH			= "FilePath";
	public static final String	FIELD_NAME_QUANTITY				= "Quantity";

	public static final String	END2_PRECISE					= "END2PRECISE";
	public static final String	END2_STRUCTURECLASSNAME			= "END2STRUCTURECLASSNAME";

	public static final String	ISPRIMARY						= "IsPrimary";
	public static final String	END2_UI_OBJECT					= "END2UIOBJECT";
	public static final String	DRIVERTREEITEMKEY				= "@@TREEITEMKEY$$";
	public static final String	STRUCTURE_DOLLAR_PREFIX			= "STRUCTURE$";

	public abstract ObjectGuid getEnd1ObjectGuid();

	public ObjectGuid resetEnd1ObjectGuid();

	public abstract ObjectGuid getEnd2ObjectGuid();

	public ObjectGuid resetEnd2ObjectGuid();

	public abstract ObjectGuid getViewObjectGuid();

	public ObjectGuid resetViewObjectGuid();

	public abstract void setEnd2ObjectGuid(ObjectGuid objectGuid);

	public abstract void setEnd1ObjectGuid(ObjectGuid objectGuid);

	public abstract void setViewObjectGuid(ObjectGuid objectGuid);

	public abstract String getSequence();

	public abstract void setSequence(String sequence);

	public abstract Boolean isEnd2Precise();

	public abstract void setsEnd2Precise(Boolean isPrecise);

	public abstract String getEnd2StructureClassName();

	public abstract void setEnd2StructureClassName(String structureClassName);

	public abstract Boolean isPrimary();

	public abstract void setParmary(Boolean isParmary);

	public abstract FoundationObject getEnd2UIObject();

	public abstract void setEnd2UIObject(FoundationObject fo);

	public void setOperationType(String type);

	public String getOperationType();

}