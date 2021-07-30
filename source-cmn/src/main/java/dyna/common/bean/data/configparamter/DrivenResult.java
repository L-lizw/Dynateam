package dyna.common.bean.data.configparamter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class DrivenResult extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private List<StructureObject>	structureObjectList	= new ArrayList<StructureObject>();

	private List<String>			varLogList			= new ArrayList<String>();

	private String					errMsg				= null;

	private Map<String, String>		variableValMap		= new HashMap<String, String>();

	public List<StructureObject> getStructureObjectList()
	{
		return this.structureObjectList == null ? new ArrayList<StructureObject>() : this.structureObjectList;
	}

	public void setStructureObjectList(List<StructureObject> structureObjectList)
	{
		this.structureObjectList = structureObjectList;
	}

	public List<String> getVarLogList()
	{
		return this.varLogList;
	}

	public void setVarLogList(List<String> varLogList)
	{
		this.varLogList = varLogList;
	}

	public String getErrMsg()
	{
		return this.errMsg;
	}

	public void setErrMsg(String errMsg)
	{
		this.errMsg = errMsg;
	}

	public Map<String, String> getVariableValMap()
	{
		return variableValMap;
	}

	public void setVariableValMap(Map<String, String> variableValMap)
	{
		this.variableValMap = variableValMap;
	}
}
