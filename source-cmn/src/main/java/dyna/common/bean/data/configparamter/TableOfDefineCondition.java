package dyna.common.bean.data.configparamter;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 多变量表条件定义
 * 
 * @author wwx
 * 
 */
public class TableOfDefineCondition extends SystemObjectImpl implements SystemObject
{
	private static final long		serialVersionUID	= -2406982283330649529L;

	private static final String	DEFINITIONNAME		= "DEFINITIONNAME";
	private static final String	RELATIONCONDITION	= "RELATIONCONDITION";		// ConditionRelationEnum
	private static final String	DEFINITIONVALUE		= "DEFINITIONVALUE";

	private ConditionMatch match=null;
	
	public String getDefinitionName()
	{
		return (String) this.get(DEFINITIONNAME);
	}

	public void setDefinitionName(String definitionName)
	{
		this.put(DEFINITIONNAME, definitionName);
	}

	public String getRelationCondition()
	{
		return (String) this.get(RELATIONCONDITION);
	}

	public void setRelationCondition(String relationConditionId)
	{
		this.put(RELATIONCONDITION, relationConditionId);
	}

	public String getDefinitionValue()
	{
		return (String) this.get(DEFINITIONVALUE);
	}

	public void setDefinitionValue(String definitionValue)
	{
		this.put(DEFINITIONVALUE, definitionValue);
	}

	public ConditionMatch getConditionMacth() 
	{
		if (match==null)
		{
			if (ConditionRelationEnum.CHOICE.getId().equals(this.getRelationCondition()))
			{
				match=new InConditionMatch();
			}
			else
			{
				match=new CompareConditionMatch(ConditionRelationEnum.getEnumWithid(this.getRelationCondition()));
			}
		}
		return match;
	}
}
