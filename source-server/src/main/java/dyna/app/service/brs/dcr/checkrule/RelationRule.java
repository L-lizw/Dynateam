package dyna.app.service.brs.dcr.checkrule;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.RuleTypeEnum;
import dyna.common.util.SetUtils;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RelationRule extends AbstractRule implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4413609530401536111L;

	private FoundationObject	end1;

	private FoundationObject	end2;

	private BOAS				boas;

	private BOMS				boms;

	@Override
	public boolean check() throws ServiceRequestException
	{
		this.boas = this.getServiceInstance(BOAS.class);
		this.boms = this.getServiceInstance(BOMS.class);

		ClassCondition end1Condition = super.getEnd1Condition();
		end1Condition.setFoundationObject(this.end1);

		if (isCheck(end1Condition, end1))
		{
			if (end1Condition.check())
			{
				if (this.end2 == null)
				{
					List<FoundationObject> end2List = new ArrayList<>();
					if (this.getRuleType() == RuleTypeEnum.BOM)
					{
						try
						{
							List<BOMStructure> bsList = boms.listBOM(end1.getObjectGuid(), getRuleName(), null, null, null);
							if (!SetUtils.isNullList(bsList))
							{
								for (BOMStructure bomStructure : bsList)
								{
									FoundationObject ttEnd2 = boas.getObjectNotCheckAuthor(bomStructure.getEnd2ObjectGuid());
									if (ttEnd2 != null)
									{
										end2List.add(ttEnd2);
									}
								}
							}
						}
						catch (Exception e)
						{
							// TODO: handle exception
							int i = 0;
							e.printStackTrace();
							return false;
						}
					}
					else if (this.getRuleType() == RuleTypeEnum.RELATION)
					{
						try
						{
							List<StructureObject> list = boas.listObjectOfRelation(end1.getObjectGuid(), getRuleName(), null, null, null);
							if (!SetUtils.isNullList(list))
							{
								for (StructureObject structureObject : list)
								{
									FoundationObject ttEnd2 = boas.getObjectNotCheckAuthor(structureObject.getEnd2ObjectGuid());
									if (ttEnd2 != null)
									{
										end2List.add(ttEnd2);
									}
								}
							}
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if (this.getRuleType() == RuleTypeEnum.OBJECTFIELD)
					{
						// should be never happen
					}
					if (!SetUtils.isNullList(end2List))
					{
						for (FoundationObject end2 : end2List)
						{
							boolean result = this.checkEnd2(end2);
							if (!result)
							{
								return false;
							}
						}
					}
					return true;
				}
				else
				{
					return this.checkEnd2(this.end2);
				}
			}
		}
		return true;
	}

	private boolean checkEnd2(FoundationObject end2) throws ServiceRequestException
	{
		List<ClassCondition> end2ConditionList = super.getEnd2ConditionList();
		if (!SetUtils.isNullList(end2ConditionList))
		{
			for (ClassCondition end2Condition : end2ConditionList)
			{
				end2Condition.setFoundationObject(end2);
				if (isCheck(end2Condition, end2))
				{
					if (!end2Condition.check())
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	public void setFoundationObject(FoundationObject end1, FoundationObject end2)
	{
		this.end1 = end1;
		this.end2 = end2;
		if (end1 != null && end1.getObjectGuid() != null)
		{
			this.getDataMap().put(end1.getObjectGuid().getGuid(), end1);
		}
		if (end2 != null && end2.getObjectGuid() != null)
		{
			this.getDataMap().put(end2.getObjectGuid().getGuid(), end2);
		}
	}
}
