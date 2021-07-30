package dyna.common.bean.configure.cross;

import java.util.List;

import org.simpleframework.xml.ElementList;

import dyna.common.util.SetUtils;

public class CrossRequestWithParamModel extends AbstractCrossRequestModel
{
	@ElementList(name = "payload", required = false, inline = false)
	private List<ParamModel>	paramList	= null;

	/**
	 * @param paramList
	 *            the paramList to set
	 */
	public void setParamList(List<ParamModel> paramList)
	{
		this.paramList = paramList;
	}

	/**
	 * @return the paramList
	 */
	public List<ParamModel> getParamList()
	{
		return this.paramList;
	}

	@Override
	public String getXMLWithoutCrossInfo()
	{
		return SetUtils.isNullList(getParamList()) || getParamList().get(0) == null ? null : getParamList().get(0).getValue();
	}

}
