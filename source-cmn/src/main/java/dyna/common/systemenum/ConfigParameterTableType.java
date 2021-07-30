package dyna.common.systemenum;

public enum ConfigParameterTableType
{
	G("G", "CONFIGPARAMETERTABLETYPE_TABLE_G"), //
	La("La", "CONFIGPARAMETERTABLETYPE_TABLE_LA"), //
	Lb("Lb", "CONFIGPARAMETERTABLETYPE_TABLE_LB"), //
	L("L", "CONFIGPARAMETERTABLETYPE_TABLE_L"), //
	A("A", "CONFIGPARAMETERTABLETYPE_TABLE_A"), //
	B("B", "CONFIGPARAMETERTABLETYPE_TABLE_B"), //
	C("C", "CONFIGPARAMETERTABLETYPE_TABLE_C"), //
	D("D", "CONFIGPARAMETERTABLETYPE_TABLE_D"), //
	E("E", "CONFIGPARAMETERTABLETYPE_TABLE_E"), //
	R("R", "CONFIGPARAMETERTABLETYPE_TABLE_R"), //
	Q("Q", "CONFIGPARAMETERTABLETYPE_TABLE_Q"), //
	F("F", "CONFIGPARAMETERTABLETYPE_TABLE_F"), //
	P("P", "CONFIGPARAMETERTABLETYPE_TABLE_P"), //
	MAK("MAK", "CONFIGPARAMETERTABLETYPE_TABLE_MAK"), //
	INPT("INPT", "CONFIGPARAMETERTABLETYPE_TABLE_INPT"), //
	A_E("A-E", "CONFIGPARAMETERTABLETYPE_TABLE_A_E"), //
	R_Q("R-Q", "CONFIGPARAMETERTABLETYPE_TABLE_R_Q"),//
	DETAIL("DETAIL", "ID_CLIENT_INSTANCE_CONFIG_MATERIAL_DETAIL_TABLE"), //
	M("M", "CONFIGPARAMETERTABLETYPE_TABLE_M"), //
	RC("RC", "CONFIGPARAMETERTABLETYPE_TABLE_RC"), //
	;
	private String	msrId	= null;
	private String	name	= null;

	private ConfigParameterTableType(String name, String msrId)
	{
		this.name = name;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return msrId;
	}

	public String getName()
	{
		return name;
	}
	
	public static ConfigParameterTableType getTableTypeByName(String tableType)
	{
		for (ConfigParameterTableType confParameterTableType : ConfigParameterTableType.values())
		{
			if (confParameterTableType.getName().equalsIgnoreCase(tableType))
			{
				return confParameterTableType;
			}
		}
		return null;
	}
}
