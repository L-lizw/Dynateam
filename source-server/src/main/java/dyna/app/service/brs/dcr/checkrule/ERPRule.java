package dyna.app.service.brs.dcr.checkrule;

import dyna.common.bean.data.FoundationObject;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;

import java.io.Serializable;
import java.util.List;

public class ERPRule extends AbstractRule implements Serializable
{
	private static final long	serialVersionUID	= 3835999044169108526L;

	private FoundationObject	foundationObject	= null;

	@Override
	public boolean check() throws ServiceRequestException
	{
		List<ClassCondition> end2ConditionList = super.getEnd2ConditionList();
		if (!SetUtils.isNullList(end2ConditionList))
		{
			for (ICondition end2Condition : end2ConditionList)
			{
				end2Condition.setFoundationObject(this.foundationObject);
				if (!end2Condition.check())
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void setFoundationObject(FoundationObject attach)
	{
		this.foundationObject = attach;
		super.getDataMap().put(this.foundationObject.getObjectGuid().getGuid(), this.foundationObject);
	}
}
