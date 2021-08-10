package dyna.app.service.brs.dcr.checkcondition;

import java.util.List;

import dyna.common.dto.DSSFileInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.DSS;

/**
 * 对象是否存在指定附件
 * 
 * @author duanll
 * 
 */
public class FileConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7509261258181802707L;

	private String				fileExtension;

	@Override
	public boolean check() throws ServiceRequestException
	{
		if (StringUtils.isNullString(this.fileExtension))
		{
			return true;
		}

		DSS dss = this.getServiceInstance(DSS.class);
		List<DSSFileInfo> fileList = dss.listFile(this.getFoundationObject().getObjectGuid(), null);
		if (!SetUtils.isNullList(fileList))
		{
			if ("*".equals(this.fileExtension))
			{
				return true;
			}
			for (DSSFileInfo fileInfo : fileList)
			{
				String extensionName = fileInfo.getExtentionName();
				if (this.fileExtension.equalsIgnoreCase(extensionName))
				{
					return true;
				}
			}
		}
		return false;
	}

	public String getFileExtension()
	{
		return this.fileExtension;
	}

	public void setFileExtension(String fileExtension)
	{
		this.fileExtension = fileExtension;
	}
}
