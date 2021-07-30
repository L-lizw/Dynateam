package dyna.common.bean.erp;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class ERPSchema extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID		= 8601865612726950837L;
	/**
	 * 是否展开BOM
	 */
	private boolean				expandBOM				= false;
	/**
	 * 是否传取替代(前提是有createItem这个Operation)
	 */
	private boolean				exportRSItem			= false;

	private boolean				expandClassification	= false;
	/**
	 * 是否抛全部数据
	 */
	private boolean				exportAllData			= false;

	private String				schemaName;
	/**
	 * 包含的操作列表
	 */
	private List<String>		operationList;

	private List<String>		contentList;

	public ERPSchema(String schemaName, boolean expandBOM, boolean exportRSItem, List<String> operationList)
	{
		this.schemaName = schemaName;
		this.expandBOM = expandBOM;
		this.exportRSItem = exportRSItem;
		this.operationList = operationList;
	}

	public ERPSchema()
	{
	}

	public List<String> getContentList()
	{
		return contentList;
	}

	public void setContentList(List<String> contentList)
	{
		this.contentList = contentList;
	}

	public boolean isExpandBOM()
	{
		return expandBOM;
	}

	public void setExpandBOM(boolean expandBOM)
	{
		this.expandBOM = expandBOM;
	}

	public boolean isExportRSItem()
	{
		return exportRSItem;
	}

	public void setExportRSItem(boolean exportRSItem)
	{
		this.exportRSItem = exportRSItem;
	}

	public boolean isExpandClassification()
	{
		return this.expandClassification;
	}

	public void setExpandClassification(boolean expandClassification)
	{
		this.expandClassification = expandClassification;
	}

	public boolean isExportAllData()
	{
		return exportAllData;
	}

	public void setExportAllData(boolean exportAllData)
	{
		this.exportAllData = exportAllData;
	}
	/**
	 * 返回Operation的id列列表
	 * 
	 * @return
	 */
	public List<String> getOperationList()
	{
		return operationList;
	}

	public void setOperationList(List<String> operationList)
	{
		this.operationList = operationList;
	}

	public void setName(String name)
	{
		this.schemaName = name;
	}

	public String getName()
	{
		return schemaName;
	}

	@Override
	public String toString()
	{
		return this.schemaName;
	}
}
