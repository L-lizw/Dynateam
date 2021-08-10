/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 常用意见操作分支
 * zhanghj 2011-3-31
 */
package dyna.app.service.brs.wfi.favoritecomment;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.wf.ProcTrackComm;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;

/**
 * 常用意见操作分支
 * 
 * @author zhanghj
 * 
 */
public class FavoriteCommentStub extends AbstractServiceStub<WFIImpl>
{
	private FavoriteCommentDBStub dbStub = null;

	/**
	 * @param context
	 * @param service
	 */
	public FavoriteCommentStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.dbStub = new FavoriteCommentDBStub(context, service);
	}

	public ProcTrackComm saveTrackComm(ProcTrackComm procComm) throws ServiceRequestException
	{
		String guid = null;
		ProcTrackComm procTrackComm = null;
		try
		{
			if (StringUtils.isNullString(procComm.getCreateUserGuid()))
			{
				String operatorGuid = this.stubService.getOperatorGuid();
				procComm.setCreateUserGuid(operatorGuid);
			}
			guid = this.dbStub.saveTrackComm(procComm);

			procTrackComm = this.dbStub.getTrackComm(guid);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}

		return procTrackComm;
	}

	public void deleteTrackComm(String guid) throws ServiceRequestException
	{
		this.dbStub.deleteTrackComm(guid);
	}

	public List<ProcTrackComm> listProcTrackComm() throws ServiceRequestException
	{
		return this.dbStub.listProcTrackComm();
	}
}
