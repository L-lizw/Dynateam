package dyna.common.dto.erp.tmptab;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.erp.tmptab.ERPtempDataMapper;

@EntryMapper(ERPtempDataMapper.class)
public class ERPtempData extends SystemObjectImpl
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4604675592317433648L;
	private String				ITEMGUID			= "ITEMGUID";
	private String				CATEGORY			= "CATEGORY";
	private String				JOBGUID				= "JOBGUID";
	private String				STAMP				= "STAMP";
	private String				JOBSTATUS			= "JOBSTATUS";
	private String				ISFIRST				= "ISFIRST";

	public String getITEMGUID()
	{
		return (String) this.get(ITEMGUID);
	}

	public void setITEMGUID(String guid)
	{
		this.put(ITEMGUID, guid);
	}

	public String getSTAMP()
	{
		return (String) this.get(STAMP);
	}

	public String getCATEGORY()
	{
		return (String) this.get(CATEGORY);
	}

	public void setCATEGORY(String cATEGORY)
	{
		this.put(CATEGORY, cATEGORY);
	}

	public void setSTAMP(String stamp)
	{
		this.put(STAMP, stamp);
	}

	public String getJOBGUID()
	{
		return (String) this.get(JOBGUID);
	}

	public void setJOBGUID(String jobguid)
	{
		this.put(JOBGUID, jobguid);
	}

	public int getJOBSTATUS()
	{
		if (this.get(JOBSTATUS) == null)
		{
			return -1;
		}
		return ((Number) this.get(JOBSTATUS)).intValue();
	}

	public void setJOBSTATUS(String jOBSTATUS)
	{
		this.put(JOBSTATUS, jOBSTATUS);
	}

	public boolean IsFirst()
	{
		return "Y".equalsIgnoreCase((String) this.get(ISFIRST));
	}

	public void setFirst(String iSFIRST)
	{
		this.put(ISFIRST, iSFIRST);
	}

}
