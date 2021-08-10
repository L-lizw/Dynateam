package dyna.app.service.brs.dcr.checkcondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.EMM;

public class MultiCodeFieldConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -194548853290278502L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		String val = StringUtils.convertNULLtoString(this.getFoundationObject().get(this.getFieldName()));
		String ruleVal = this.getRuleValue();
		if (this.getOperatorSign() == OperateSignEnum.ISNULL)
		{
			return StringUtils.isNullString(val);
		}
		else if (this.getOperatorSign() == OperateSignEnum.NOTNULL)
		{
			return !StringUtils.isNullString(val);
		}
		else
		{
			if (this.getOperatorSign() == OperateSignEnum.EQUALS)
			{
				return this.isSameVal(val, ruleVal);
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTEQUALS)
			{
				return !this.isSameVal(val, ruleVal);
			}
			else if (this.getOperatorSign() == OperateSignEnum.CONTAIN)
			{
				// 规则中选择的code有一个没有包含在值中，为false
				return this.isContain(val, ruleVal);
			}
			else if (this.getOperatorSign() == OperateSignEnum.NOTCONTAIN)
			{
				// 规则中选择的code有一个没有包含在值中，即为true
				return !this.isContain(val, ruleVal);
			}
		}

		return true;
	}

	private boolean isContain(String orig, String dest) throws ServiceRequestException
	{
		List<String> origList = this.transferStrToList(orig);
		List<String> destList = this.transferStrToList(dest);
		for (String s : destList)
		{
			if (!origList.contains(s))
			{
				return false;
			}
		}
		return true;
	}

	private boolean isSameVal(String orig, String dest) throws ServiceRequestException
	{
		List<String> origList = this.transferStrToList(orig);
		List<String> destList = this.transferStrToList(dest);

		return this.isSameList(origList, destList);
	}

	private boolean isSameList(List<String> origList, List<String> destList)
	{
		if (origList.size() != destList.size())
		{
			return false;
		}
		for (String s : origList)
		{
			if (!destList.contains(s))
			{
				return false;
			}
		}
		for (String s : destList)
		{
			if (!origList.contains(s))
			{
				return false;
			}
		}
		return true;
	}

	private List<String> transferStrToList(String val) throws ServiceRequestException
	{
		List<String> tempList = new ArrayList<String>();
		if (!StringUtils.isNullString(val))
		{
			if (val.indexOf(";") != -1)
			{
				String[] tmpArr = val.split(";");
				for (String tmp : tmpArr)
				{
					tempList.add(tmp);
				}
			}
			else
			{
				tempList.add(val);
			}
			Collections.sort(tempList);
		}
		return tempList;
	}

	private String getRuleValue() throws ServiceRequestException
	{
		StringBuffer buffer = new StringBuffer();
		List<String> tmpList = this.transferStrToList(this.getValue());
		if (!SetUtils.isNullList(tmpList))
		{
			ClassField classField = this.getEMM().getFieldByName(this.getFoundationObject().getObjectGuid().getClassName(), this.getFieldName(),true);
			for (String s : tmpList)
			{
				CodeItemInfo codeItemInfo = this.getEMM().getCodeItemByName(classField.getTypeValue(), s);
				if (codeItemInfo != null)
				{
					if (buffer.length() > 0)
					{
						buffer.append(";");
					}
					buffer.append(codeItemInfo.getGuid());
				}
			}
		}
		return buffer.toString();
	}

	public String getValue() throws ServiceRequestException
	{
		Object o = super.getValue();
		if (o == null)
		{
			return StringUtils.EMPTY_STRING;
		}
		return o.toString();
	}

	private EMM getEMM() throws ServiceRequestException
	{
		return this.getServiceInstance(EMM.class);
	}
}
