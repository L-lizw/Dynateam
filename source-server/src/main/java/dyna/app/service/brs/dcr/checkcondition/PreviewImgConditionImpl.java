package dyna.app.service.brs.dcr.checkcondition;

import dyna.common.exception.ServiceRequestException;
import dyna.net.service.brs.DSS;

/**
 * 对象是否上传缩略图
 * 
 * @author duanll
 * 
 */
public class PreviewImgConditionImpl extends AbstractFieldCondition
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -579553928394726288L;

	@Override
	public boolean check() throws ServiceRequestException
	{
		DSS dss = this.getServiceInstance(DSS.class);
		return dss.hasPreviewFile(this.getFoundationObject().getObjectGuid(), this.getFoundationObject().getIterationId());
	}
}
