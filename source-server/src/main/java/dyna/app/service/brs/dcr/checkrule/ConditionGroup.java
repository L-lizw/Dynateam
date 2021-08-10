package dyna.app.service.brs.dcr.checkrule;

import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JoinTypeEnum;
import dyna.common.util.SetUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConditionGroup extends AbstractCondition implements Serializable
{
	private static final long	serialVersionUID	= 2428043953559767091L;

	/**
	 * 规则按照顺序，第一个规则没有连接符，其后的规则为连接符+实际规则
	 * 除了第一个规则之外的其他规则，必须是ConditionGroup类型
	 */
	private List<ICondition>	conditionList		= new ArrayList<>();

	private JoinTypeEnum		joinTypeEnum		= null;

	@Override
	public boolean check() throws ServiceRequestException
	{
		if (!SetUtils.isNullList(this.conditionList))
		{
			boolean condResult = true;
			for (int i = 0; i < this.conditionList.size(); i++)
			{
				ICondition cond = this.conditionList.get(i);
				cond.setFoundationObject(this.getFoundationObject());

				if (i == 0)
				{
					condResult = cond.check();
				}
				else if (cond instanceof ConditionGroup)
				{
					ConditionGroup group = (ConditionGroup) cond;
					if (group.getJoinTypeEnum() == null)
					{
						throw new ServiceRequestException("", "rule is not right");
					}

					if (group.getJoinTypeEnum() == JoinTypeEnum.AND)
					{
						condResult = condResult && group.check();
					}
					else
					{
						condResult = condResult || group.check();
					}
				}
				else
				{
					throw new ServiceRequestException("", "rule is not right");
				}
			}
			return condResult;
		}
		return true;
	}

	public List<ICondition> getConditionList()
	{
		return this.conditionList;
	}

	public void setConditionList(List<ICondition> conditionList)
	{
		this.conditionList = conditionList;
	}

	public void addCondition(ICondition condition)
	{
		if (this.conditionList == null)
		{
			this.conditionList = new ArrayList<ICondition>();
		}
		this.conditionList.add(condition);
	}

	public JoinTypeEnum getJoinTypeEnum()
	{
		return this.joinTypeEnum;
	}

	public void setJoinTypeEnum(JoinTypeEnum joinTypeEnum)
	{
		this.joinTypeEnum = joinTypeEnum;
	}
}
