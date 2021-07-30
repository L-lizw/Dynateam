/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOM结构
 * xiasheng 2010-7-13
 */
package dyna.common.bean.data.structure;

import java.math.BigDecimal;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.StructureObjectImpl;
import dyna.common.util.BooleanUtils;

public class BOMStructure extends StructureObjectImpl implements StructureObject
{

	private static final long	serialVersionUID		= -8276862531467452954L;

	public static final String	BOMSTRUCTURE_CLASSNAME	= "BOMStructure";

	public static final String	UOM						= "UOM";

	public static final String	SEQUENCE				= "DATASEQ";

	public static final String	QUANTITY				= "QUANTITY";

	public static final String	ISPRECISE				= "ISPRECISE";

	public static final String	END2VIEWISCHECKOUT		= "END2VIEWISCHECKOUT";

	public static final String	BOM_OPERATION_TYPE		= "BOMOPERATIONTYPE";

	public static final String	REPLACED_EDN2_FULLNAME	= "REPLACEDEND2FULLNAME";

	// 规格
	public static final String	SPEC					= "SPECIFICATION";
	// 是否有取替代
	public static final String	RSFLAG					= "RSFLAG";

	public static final String	ISFROMCAD				= "ISFROMCAD";

	public static final String	BOMKEY					= "BOMKey";

	public static final String	END2_UI_OBJECT			= "END2UIOBJECT";

	public String getSpec()
	{
		String specification = null;
		Object end2Obj = this.get(BOMStructure.END2_UI_OBJECT);
		if (end2Obj != null && end2Obj instanceof FoundationObject)
		{
			FoundationObject end2 = (FoundationObject) end2Obj;
			specification = (String) end2.get(SPEC);
		}
		return specification;
	}

	public BOMStructure()
	{
		// do nothing
	}

	public BOMStructure(String structureClassName)
	{
		this.put("CLASSNAME$", structureClassName);
	}

	public BOMStructure(StructureObject structureObject)
	{
		Object end2Obj = structureObject.get(BOMStructure.END2_UI_OBJECT);
		if (end2Obj != null && end2Obj instanceof FoundationObject)
		{
			FoundationObject end2 = (FoundationObject) end2Obj;
			this.setEnd2UIObject(end2);
			this.setEnd2ObjectGuid(end2.getObjectGuid());
		}

		this.putAll((StructureObjectImpl) structureObject);
		this.originalMap.putAll((StructureObjectImpl) structureObject);
		this.resetEnd1ObjectGuid();
		this.resetEnd2ObjectGuid();
		this.resetObjectGuid();
		this.resetViewObjectGuid();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		BOMStructure result = new BOMStructure();
		result.putAll((Map<String, Object>) super.clone());
		result.originalMap.putAll(this.originalMap);
		result.setObjectGuid(new ObjectGuid(this.getObjectGuid()));

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#getObjectGuid()
	 */
	@Override
	public ObjectGuid getObjectGuid()
	{
		if (this.objectGuid == null)
		{
			this.objectGuid = new ObjectGuid((String) this.get("CLASSGUID$"), (String) this.get("CLASSNAME$"), (String) this.get("GUID$"), null);
		}

		return this.objectGuid;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObjectImpl#setObjectGuid(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void setObjectGuid(ObjectGuid objectGuid)
	{
		this.objectGuid = objectGuid;
		if (objectGuid == null)
		{
			objectGuid = new ObjectGuid();
		}

		this.put("GUID$", objectGuid.getGuid());
		this.put("CLASSNAME$", objectGuid.getClassName());
		this.put("CLASSGUID$", objectGuid.getClassGuid());
	}

	public Double getQuantity()
	{
		Object quantity = this.get("QUANTITY");
		if (quantity == null)
		{
			this.setQuantity(1.0D);
			return 1.0D;
		}
		else if (quantity instanceof String)
		{
			return new BigDecimal((String) quantity).doubleValue();
		}
		else if (quantity instanceof Double)
		{
			return (Double) quantity;
		}

		return ((Number) quantity).doubleValue();
	}

	public void setBOMOperationType(String type)
	{
		this.put(BOM_OPERATION_TYPE, type);
	}

	public void setReplacedEnd2FullName(String name)
	{
		this.put(REPLACED_EDN2_FULLNAME, name);
	}

	public String getReplacedEnd2FullName()
	{
		return (String) this.get(REPLACED_EDN2_FULLNAME);
	}

	public String getBOMOperationType()
	{
		return (String) this.get(BOM_OPERATION_TYPE);
	}

	public String getRemarks()
	{
		return (String) this.get("REMARKS");
	}

	@Override
	public String getSequence()
	{
		return (String) this.get("DATASEQ");
	}

	public String getTitle()
	{
		return (String) this.get("TITLE");
	}

	public String getUOM()
	{
		return (String) this.get("UOM");
	}

	public String getUOMTitle()
	{
		return (String) this.get("UOM$TITLE");
	}

	public String getBOMKey()
	{
		return (String) this.get("BOMKey");
	}

	public Boolean isPrecise()
	{
		if (this.get(ISPRECISE) == null)
		{
			return null;
		}

		return "Y".equals(this.get(ISPRECISE));
	}

	public Boolean isRsFlag()
	{
		if (BooleanUtils.getBooleanByYN((String) this.get(RSFLAG)) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(RSFLAG));
	}

	public void setRsFlag(Boolean flag)
	{
		this.put(RSFLAG, BooleanUtils.getBooleanStringYN(flag));
	}

	public Boolean isFromCAD()
	{
		if (BooleanUtils.getBooleanByYN((String) this.get(ISFROMCAD)) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(ISFROMCAD));
	}

	public void setFromCAD(Boolean flag)
	{
		this.put(ISFROMCAD, BooleanUtils.getBooleanStringYN(flag));
	}

	public void setQuantity(Double quantity)
	{
		if (quantity == null)
		{
			this.put("QUANTITY", BigDecimal.valueOf(1.0D));
		}
		else
		{
			this.put("QUANTITY", BigDecimal.valueOf(quantity));
		}
	}

	public void setRemarks(String remarks)
	{
		this.put("REMARKS", remarks);
	}

	@Override
	public void setSequence(String sequence)
	{
		this.put("DATASEQ", sequence);
	}

	public void setTitle(String title)
	{
		this.put("TITLE", title);
	}

	public void setUOM(String uom)
	{
		this.put("UOM", uom);
	}

	public void setUOMTitle(String uomTitle)
	{
		this.put("UOM$TITLE", uomTitle);
	}

	public void setBOMKey(String bomKey)
	{
		this.put("BOMKey", bomKey);
	}

	public boolean isEnd2ViewCheckOut()
	{
		Boolean isCmt = BooleanUtils.getBooleanByYN((String) this.get(END2VIEWISCHECKOUT));
		return isCmt == null ? false : isCmt;
	}

	public void setEnd2ViewCheckOut(boolean isCheckOut)
	{
		this.put(END2VIEWISCHECKOUT, BooleanUtils.getBooleanStringYN(isCheckOut));
	}

	@Override
	public FoundationObject getEnd2UIObject()
	{
		return (FoundationObject) this.get(END2_UI_OBJECT);
	}

	@Override
	public void setEnd2UIObject(FoundationObject end2UIObject)
	{
		this.put(END2_UI_OBJECT, end2UIObject);
	}

}
