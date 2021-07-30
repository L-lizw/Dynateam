package dyna.common.bean.data.trans;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

public class TransformManualConfig extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long				serialVersionUID		= 6564841658649248759L;

	public static String					TRANSFORM_CONFIG_GUID	= "TRANSFORMCONFIGGUID";

	public static String					IS_WIP					= "ISWIP";
	public static String					WIP_SIGNATURE_SOLUTION	= "WIPSOLUTION";

	public static String					IS_PRE					= "ISPRE";
	public static String					PRE_SIGNATURE_SOLUTION	= "PRESOLUTION";

	public static String					IS_ECP					= "ISECP";
	public static String					ECP_SIGNATURE_SOLUTION	= "ECPSOLUTION";

	public static String					IS_RLS					= "ISRLS";
	public static String					RLS_SIGNATURE_SOLUTION	= "RLSSOLUTION";

	private List<TransformManualPerformer>	wipPerformerList		= new ArrayList<TransformManualPerformer>();
	private List<TransformManualPerformer>	prePerformerList		= new ArrayList<TransformManualPerformer>();
	private List<TransformManualPerformer>	ecpPerformerList		= new ArrayList<TransformManualPerformer>();
	private List<TransformManualPerformer>	rlsPerformerList		= new ArrayList<TransformManualPerformer>();

	private final boolean					isChange				= false;

	public String getTransformConfigGuid()
	{
		return (String) this.get(TRANSFORM_CONFIG_GUID);
	}

	public void setTransformConfigGuid(String transformConfigGuid)
	{
		this.put(TRANSFORM_CONFIG_GUID, transformConfigGuid);
	}

	public boolean isWIP()
	{

		if (this.get(IS_WIP) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_WIP));
	}

	public void setWIP(boolean isWIP)
	{
		this.put(IS_WIP, BooleanUtils.getBooleanStringYN(isWIP));
	}

	public String getWipSolution()
	{
		return (String) this.get(WIP_SIGNATURE_SOLUTION);
	}

	public void setWipSolution(String wipSolution)
	{
		this.put(WIP_SIGNATURE_SOLUTION, wipSolution);
	}

	public String getWipSolutionName()
	{
		return (String) this.get(WIP_SIGNATURE_SOLUTION + "NAME");
	}

	public void setWipSolutionName(String wipSolutionName)
	{
		this.put(WIP_SIGNATURE_SOLUTION + "NAME", wipSolutionName);
	}

	public boolean isECP()
	{

		if (this.get(IS_ECP) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_ECP));
	}

	public void setECP(boolean isECP)
	{
		this.put(IS_ECP, BooleanUtils.getBooleanStringYN(isECP));
	}

	public String getEcpSolution()
	{
		return (String) this.get(ECP_SIGNATURE_SOLUTION);
	}

	public void setEcpSolution(String ecpSolution)
	{
		this.put(ECP_SIGNATURE_SOLUTION, ecpSolution);
	}

	public String getEcpSolutionName()
	{
		return (String) this.get(ECP_SIGNATURE_SOLUTION + "Name");
	}

	public void setEcpSolutionName(String ecpSolutionName)
	{
		this.put(ECP_SIGNATURE_SOLUTION + "Name", ecpSolutionName);
	}

	public boolean isRLS()
	{

		if (this.get(IS_RLS) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_RLS));
	}

	public void setRLS(boolean isRLS)
	{

		this.put(IS_RLS, BooleanUtils.getBooleanStringYN(isRLS));
	}

	public String getRlsSolution()
	{
		return (String) this.get(RLS_SIGNATURE_SOLUTION);
	}

	public void setRlsSolution(String rlsSolution)
	{
		this.put(RLS_SIGNATURE_SOLUTION, rlsSolution);
	}

	public String getRlsSolutionName()
	{
		return (String) this.get(RLS_SIGNATURE_SOLUTION + "Name");
	}

	public void setRlsSolutionName(String rlsSolutionName)
	{
		this.put(RLS_SIGNATURE_SOLUTION + "Name", rlsSolutionName);
	}

	public boolean isPRE()
	{
		if (this.get(IS_PRE) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_PRE));
	}

	public void setPRE(boolean isPRE)
	{
		this.put(IS_PRE, BooleanUtils.getBooleanStringYN(isPRE));
	}

	public String getPreSolution()
	{

		return (String) this.get(PRE_SIGNATURE_SOLUTION);
	}

	public void setPreSolution(String preSolution)
	{
		this.put(PRE_SIGNATURE_SOLUTION, preSolution);
	}

	public String getPreSolutionName()
	{
		return (String) this.get(PRE_SIGNATURE_SOLUTION + "NAME");
	}

	public void setPreSolutionName(String preSolutionName)
	{
		this.put(PRE_SIGNATURE_SOLUTION + "NAME", preSolutionName);
	}

	public List<TransformManualPerformer> getWipPerformerList()
	{
		return this.wipPerformerList;
	}

	public void setWipPerformerList(List<TransformManualPerformer> wipPerformerList)
	{
		this.wipPerformerList = wipPerformerList;
	}

	public List<TransformManualPerformer> getPrePerformerList()
	{
		return this.prePerformerList;
	}

	public void setPrePerformerList(List<TransformManualPerformer> prePerformerList)
	{
		this.prePerformerList = prePerformerList;
	}

	public List<TransformManualPerformer> getEcpPerformerList()
	{
		return this.ecpPerformerList;
	}

	public void setEcpPerformerList(List<TransformManualPerformer> ecpPerformerList)
	{
		this.ecpPerformerList = ecpPerformerList;
	}

	public List<TransformManualPerformer> getRlsPerformerList()
	{
		return this.rlsPerformerList;
	}

	public void setRlsPerformerList(List<TransformManualPerformer> rlsPerformerList)
	{
		this.rlsPerformerList = rlsPerformerList;
	}
}
