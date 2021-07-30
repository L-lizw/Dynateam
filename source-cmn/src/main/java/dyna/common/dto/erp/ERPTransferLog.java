package dyna.common.dto.erp;

import java.math.BigDecimal;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class ERPTransferLog extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -8038359027223031154L;
	public static final String	targetGuid			= "TARGETGUID";
	public static final String	targetClassGuid		= "TARGETCLASSGUID";
	public static final String	iteration			= "ITERATION";
	public static final String	ERPName				= "ERPNAME";
	public static final String	ERPOpertion			= "ERPOPERATION";
	public static final String	category			= "CATEGORY";
	public static final String	ERPFactory			= "ERPFACTORY";

	public String getTargetGuid()
	{
		return (String) this.get(targetGuid);
	}

	public void setTargetGuid(String value)
	{
		this.put(targetGuid, value);
	}

	public String getTargetClassGuid()
	{
		return (String) this.get(targetClassGuid);
	}

	public void setTargetClassGuid(String value)
	{
		this.put(targetClassGuid, value);
	}

	public void setIteration(int value)
	{
		this.put(iteration, new BigDecimal(String.valueOf(value)));
	}

	public int getIteration()
	{
		return this.get(iteration) == null ? 0 : ((Number) this.get(iteration)).intValue();
	}

	public String getERPName()
	{
		return (String) this.get(ERPName);
	}

	public void setERPName(String value)
	{
		this.put(ERPName, value);
	}

	public String getERPOpertion()
	{
		return (String) this.get(ERPOpertion);
	}

	public void setERPOperation(String value)
	{
		this.put(ERPOpertion, value);
	}

	public String getCategory()
	{
		return (String) this.get(category);
	}

	public void setCategory(String value)
	{
		this.put(category, value);
	}

	public String getERPFactory()
	{
		return (String) this.get(ERPFactory);
	}

	public void setERPFactory(String value)
	{
		this.put(ERPFactory, value);
	}

}
