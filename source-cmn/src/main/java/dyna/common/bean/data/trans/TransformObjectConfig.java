package dyna.common.bean.data.trans;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.trans.TransformObjectConfigMapper;
import dyna.common.util.BooleanUtils;

@EntryMapper(TransformObjectConfigMapper.class)
public class TransformObjectConfig extends SystemObjectImpl
{
	/**
	 * 
	 */
	private static final long	serialVersionUID			= 5739643337834126878L;

	public static String		TRANSFORM_CONFIG_GUID		= "TRANSFORMCONFIGGUID";

	public static String		IS_CHECKIN					= "ISCHECKIN";

	public static String		CHECKIN_SIGNATURE_SOLUTION	= "CHECKINSOLUTION";

	public static String		IS_UPLOADED					= "ISUPLOADED";

	public static String		UPLOADED_SIGNATURE_SOLUTION	= "UPLOADEDSOLUTION";

	public String getTransformConfigGuid()
	{
		return (String) this.get(TRANSFORM_CONFIG_GUID);
	}

	public void setTransformConfigGuid(String transformConfigGuid)
	{
		this.put(TRANSFORM_CONFIG_GUID, transformConfigGuid);
	}

	public String getCheckinSolution()
	{
		return (String) this.get(CHECKIN_SIGNATURE_SOLUTION);
	}

	public void setCheckinSolution(String checkinSolution)
	{
		this.put(CHECKIN_SIGNATURE_SOLUTION, checkinSolution);
	}

	public String getCheckinSolutionName()
	{
		return (String) this.get(CHECKIN_SIGNATURE_SOLUTION + "NAME");
	}

	public void setCheckinSolutionName(String checkinSolution)
	{
		this.put(CHECKIN_SIGNATURE_SOLUTION + "NAME", checkinSolution);
	}

	public String getUploadedSolution()
	{
		return (String) this.get(UPLOADED_SIGNATURE_SOLUTION);
	}

	public void setUploadedSolution(String uploadedSolution)
	{
		this.put(UPLOADED_SIGNATURE_SOLUTION, uploadedSolution);
	}

	public String getUploadedSolutionName()
	{
		return (String) this.get(UPLOADED_SIGNATURE_SOLUTION + "NAME");
	}

	public void setUploadedSolutionName(String uploadedSolution)
	{
		this.put(UPLOADED_SIGNATURE_SOLUTION + "NAME", uploadedSolution);
	}

	public boolean isCheckIn()
	{
		if (this.get(IS_CHECKIN) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(IS_CHECKIN));
	}

	public void setCheckIn(boolean isCheckIn)
	{
		this.put(IS_CHECKIN, BooleanUtils.getBooleanStringYN(isCheckIn));
	}

	public boolean isUploaded()
	{
		if (this.get(IS_UPLOADED) == null)
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(IS_UPLOADED));
	}

	public void setUploaded(boolean isUploaded)
	{
		this.put(IS_UPLOADED, BooleanUtils.getBooleanStringYN(isUploaded));
	}
}
