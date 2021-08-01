package dyna.common.bean.data.checkrule;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.checkrule.ClassConditionDetailDataMapper;
import dyna.common.systemenum.JoinTypeEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@EntryMapper(ClassConditionDetailDataMapper.class)
public class ClassConditionDetailData extends SystemObjectImpl implements SystemObject, Comparable<ClassConditionDetailData>
{
	/**
	 * 
	 */
	private static final long				serialVersionUID	= -199088636887601262L;

	public static final String				MASTERGUID			= "MASTERGUID";

	public static final String				NAME				= "CONDITIONNAME";

	public static final String				PARENTGUID			= "PARENTGUID";

	public static final String				FIELDNAME			= "FIELDNAME";

	public static final String				OPERATORSIGN		= "OPERATOR";

	public static final String				VALUE				= "VALUE";

	public static final String				JOINTYPE			= "JOINTYPE";

	public static final String				SEQUENCE			= "DATASEQ";

	public static final String				ROOT_NAME			= "root";

	private String							parentName			= null;

	private List<ClassConditionDetailData>	detailDataList		= null;

	private String							className			= null;

	private String							conditionDesc		= null;

	public ClassConditionDetailData()
	{
		this.setName(StringUtils.generateRandomUID(31));
		this.setJoinType(JoinTypeEnum.AND);
	}

	@Override
	public int compareTo(ClassConditionDetailData o)
	{
		return Integer.valueOf(getSequence()).compareTo(Integer.valueOf(o.getSequence()));
	}

	public String getMasterGuid()
	{
		return (String) this.get(MASTERGUID);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	public String getParentGuid()
	{
		return this.get(PARENTGUID) == null ? ROOT_NAME : (String) get(PARENTGUID);
	}

	public void setParentGuid(String parentGuid)
	{
		this.put(PARENTGUID, parentGuid);
	}

	public String getFieldName()
	{
		return (String) this.get(FIELDNAME);
	}

	public void setFieldName(String fieldName)
	{
		this.put(FIELDNAME, fieldName);
	}

	public OperateSignEnum getOperator()
	{
		return this.get(OPERATORSIGN) == null ? null : OperateSignEnum.valueOf((String) this.get(OPERATORSIGN));
	}

	public void setOperator(OperateSignEnum operator)
	{
		this.put(OPERATORSIGN, operator.name());
	}

	public String getValue()
	{
		return (String) this.get(VALUE);
	}

	public void setValue(String value)
	{
		this.put(VALUE, value);
	}

	public JoinTypeEnum getJoinType()
	{
		return JoinTypeEnum.typeof((String) this.get(JOINTYPE));
	}

	public void setJoinType(JoinTypeEnum joinType)
	{
		this.put(JOINTYPE, joinType.name());
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

	public List<ClassConditionDetailData> getDetailDataList()
	{
		if (this.detailDataList == null)
		{
			this.detailDataList = new ArrayList<ClassConditionDetailData>();
		}
		return this.detailDataList;
	}

	public void setDetailDataList(List<ClassConditionDetailData> detailDataList)
	{
		this.detailDataList = detailDataList;
	}

	public void addDetail(ClassConditionDetailData detailData)
	{
		if (this.detailDataList == null)
		{
			this.detailDataList = new ArrayList<ClassConditionDetailData>();
		}
		this.detailDataList.add(detailData);
	}

	public String getParentName()
	{
		if (this.parentName == null)
		{
			this.parentName = ClassConditionDetailData.ROOT_NAME;
		}
		return this.parentName;
	}

	public void setParentName(String parentName)
	{
		this.parentName = parentName;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getConditionDesc()
	{
		return conditionDesc;
	}

	public void setConditionDesc(String conditionDesc)
	{
		this.conditionDesc = conditionDesc;
	}

	public boolean isDoCheckRule()
	{
		return StringUtils.isGuid(getFieldName());
	}
}
