package dyna.app.service.brs.dcr.checkrule;

import dyna.common.bean.data.FoundationObject;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

import java.io.Serializable;
import java.util.List;

public class WFRule extends AbstractRule implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3835999044169108526L;

	private String				actrtName			= null;

	private FoundationObject	foundationObject	= null;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String actrtName = this.getActrtName();
		if (!StringUtils.isNullString(actrtName))
		{
			if (actrtName.equals(super.getEnd1Condition().getClassName()))
			{
				List<ClassCondition> end2ConditionList = super.getEnd2ConditionList();
				if (!SetUtils.isNullList(end2ConditionList))
				{
					for (ClassCondition end2Condition : end2ConditionList)
					{
						end2Condition.setFoundationObject(this.foundationObject);
						if (isCheck(end2Condition, foundationObject))
						{
							if (!end2Condition.check())
							{
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public void setFoundationObject(String actrtName, FoundationObject attach)
	{
		this.actrtName = actrtName;
		this.foundationObject = attach;
		super.getDataMap().put(this.foundationObject.getObjectGuid().getGuid(), this.foundationObject);
	}

	public String getActrtName()
	{
		return this.actrtName;
	}
}
