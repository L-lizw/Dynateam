package dyna.common.bean.data.checkrule;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class End2CheckRule extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5772892807262237383L;

	public static final String	MASTERGUID			= "MASTERGUID";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	END2CONDITION		= "END2CONDITION";

	private ClassConditionData	end2Condition		= null;

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

	public int getSequence()
	{
		Object object = this.get(SEQUENCE);
		return object == null ? 0 : ((Number) object).intValue();
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, sequence);
	}

	public String getEnd2ConditionGuid()
	{
		return (String) this.get(END2CONDITION);
	}

	public void setEnd2ConditionGuid(String end2ConditionGuid)
	{
		this.put(END2CONDITION, end2ConditionGuid);
	}

	public ClassConditionData getEnd2Condition()
	{
		return end2Condition;
	}

	public void setEnd2Condition(ClassConditionData end2Condition)
	{
		this.end2Condition = end2Condition;
	}

}
