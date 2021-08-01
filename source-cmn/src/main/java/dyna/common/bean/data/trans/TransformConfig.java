package dyna.common.bean.data.trans;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.trans.TransformConfigMapper;
import dyna.common.systemenum.trans.TransStorageType;
import dyna.common.util.BooleanUtils;

import java.util.List;

@EntryMapper(TransformConfigMapper.class)
public class TransformConfig extends SystemObjectImpl
{

	public static String				TRANSFORM_CLASS	= "TRANSFORMCLASS";

	// 转换结果类型
	public static String				TRANSFORM_TYPE	= "TRANSFORMTYPE";

	// 文件保存方式
	public static String				RESULT_TYPE		= "RESULTTYPE";

	public static String				ISWORKFLOW		= "ISWORKFLOW";
	public static String				ISOBJECT		= "ISOBJECT";
	public static String				ISMANUAL		= "ISMANUAL";

	public static String				MAPPING_CLASS	= "MAPPINGCLASS";

	public static String				SIGNGUID		= "SIGNGUID";

	private List<TransformWFConfig>		wfConfigList	= null;
	private List<TransformFieldMapping>	fieldConfigList	= null;
	private TransformObjectConfig		objectConfig	= null;
	private TransformManualConfig		manualConfig	= null;

	public static String				IS_DELETE		= "ISDELETE";

	public String getTransformClass()
	{
		return (String) this.get(TRANSFORM_CLASS);
	}

	public String getTransformClassName()
	{
		return (String) this.get(TRANSFORM_CLASS + "NAME");
	}

	public void setTransformClass(String transformClass)
	{
		this.put(TRANSFORM_CLASS, transformClass);
	}

	public String getTransformType()
	{
		return (String) this.get(TRANSFORM_TYPE);
	}

	public void setTransformType(String transformType)
	{
		this.put(TRANSFORM_TYPE, transformType);
	}

	public String getResultType()
	{
		return (String) this.get(RESULT_TYPE);
	}

	public TransStorageType getResultTypeEnum()
	{
		return TransStorageType.typeValueOf((String) this.get(RESULT_TYPE));
	}

	public void setResultType(String tesultType)
	{
		this.put(RESULT_TYPE, tesultType);
	}

	public boolean isWorkflow()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(ISWORKFLOW));
	}

	public void setWorkflow(boolean isWorkflow)
	{
		this.put(ISWORKFLOW, BooleanUtils.getBooleanStringYN(isWorkflow));
	}

	public boolean isManual()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(ISMANUAL));
	}

	public void setManual(boolean isManual)
	{
		this.put(ISMANUAL, BooleanUtils.getBooleanStringYN(isManual));
	}

	public boolean isObject()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(ISOBJECT));
	}

	public void setObject(boolean isObject)
	{
		this.put(ISOBJECT, BooleanUtils.getBooleanStringYN(isObject));
	}

	public String getMappingClass()
	{
		return (String) this.get(MAPPING_CLASS);
	}

	public String getMappingClassName()
	{
		return (String) this.get(MAPPING_CLASS + "NAME");
	}

	public void setMappingClass(String mappingClass)
	{
		this.put(MAPPING_CLASS, mappingClass);
	}

	public void setMappingClassName(String mappingClassName)
	{
		this.put(MAPPING_CLASS + "NAME", mappingClassName);
	}

	public String getSignGuid()
	{
		return (String) this.get(SIGNGUID);
	}

	public void setSignGuid(String signGuid)
	{
		this.put(SIGNGUID, signGuid);
	}

	public List<TransformWFConfig> getWfConfigList()
	{
		return this.wfConfigList;
	}

	public void setWfConfigList(List<TransformWFConfig> wfConfigList)
	{
		this.wfConfigList = wfConfigList;
	}

	public List<TransformFieldMapping> getFieldConfigList()
	{
		return this.fieldConfigList;
	}

	public void setFieldConfigList(List<TransformFieldMapping> fieldConfigList)
	{
		this.fieldConfigList = fieldConfigList;
	}

	public TransformObjectConfig getObjectConfig()
	{
		if (this.objectConfig == null)
		{
			this.objectConfig = new TransformObjectConfig();
		}
		return this.objectConfig;
	}

	public void setObjectConfig(TransformObjectConfig objectConfig)
	{
		this.objectConfig = objectConfig;
	}

	public TransformManualConfig getManualConfig()
	{
		if (this.manualConfig == null)
		{
			this.manualConfig = new TransformManualConfig();
		}

		return this.manualConfig;
	}

	public void setManualConfig(TransformManualConfig manualConfig)
	{
		this.manualConfig = manualConfig;
	}

	public boolean isDelete()
	{
		if (this.get(IS_DELETE) == null)
		{
			return false;
		}
		return (Boolean) this.get(IS_DELETE);
	}

	public void setDelete(boolean isDelete)
	{
		this.put(IS_DELETE, isDelete);
	}
}
