/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplateStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.mms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.bom.BOMReportTemplate;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wanglei
 * 
 */
public class BOMTemplateStub extends AbstractServiceStub<MMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public BOMTemplateStub(ServiceContext context, MMSImpl service)
	{
		super(context, service);
	}

	protected void obsoleteBOMTemplateByName(String bomTemplateName) throws ServiceRequestException
	{
		List<BOMTemplateInfo> templateList = DataServer.getRelationService().listBOMTemplateByName(bomTemplateName);
		if (!SetUtils.isNullList(templateList))
		{
			for (BOMTemplateInfo bomTemplate : templateList)
			{
				bomTemplate.setValid(false);
				this.saveBOMTemplateInfo(bomTemplate);
			}
		}

	}

	protected void obsoleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = DataServer.getRelationService().getBOMTemplateInfo(bomTemplateGuid);
		if (bomTemplate != null)
		{
			bomTemplate.setValid(false);
			this.saveBOMTemplateInfo(bomTemplate);
		}
	}

	private void saveBOMTemplateInfo(BOMTemplateInfo bomTemplate)
	{

	}

	protected void reUseBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = DataServer.getRelationService().getBOMTemplateInfo(bomTemplateGuid);
		if (bomTemplate.isValid())
		{
			return;
		}
		bomTemplate.setValid(true);
		this.saveBOMTemplateInfo(bomTemplate);
	}

	protected void reUseBOMTemplateByName(String bomTemplateName) throws ServiceRequestException
	{
		List<BOMTemplateInfo> templateList = DataServer.getRelationService().listBOMTemplateByName(bomTemplateName);
		if (!SetUtils.isNullList(templateList))
		{
			for (BOMTemplateInfo bomTemplate : templateList)
			{
				if (bomTemplate.isValid())
				{
					continue;
				}
				bomTemplate.setValid(true);
				this.saveBOMTemplateInfo(bomTemplate);
			}
		}
	}

	protected void deleteBOMTemplateByName(String templateName) throws ServiceRequestException
	{
		List<BOMTemplateInfo> templateList = DataServer.getRelationService().listBOMTemplateByName(templateName);
		if (!SetUtils.isNullList(templateList))
		{
			for (BOMTemplateInfo bomTemplate : templateList)
			{
				this.deleteBOMTemplate(bomTemplate.getGuid());
			}
		}
	}

	protected void deleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			BOMTemplate template = this.stubService.getEMM().getBOMTemplate(bomTemplateGuid);
			if (template != null)
			{
				deleteTemplateSub(template);
				sds.delete(template.getInfo());
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	private void deleteTemplateSub(BOMTemplate template)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<BOMTemplateEnd2> bomTemplateEnd2List = template.getBOMTemplateEnd2List();
		List<BOMReportTemplate> bomReportTemplateList = template.getBOMReportTemplateList();
		if (bomTemplateEnd2List != null)
		{
			for (BOMTemplateEnd2 end2 : bomTemplateEnd2List)
			{
				sds.delete(end2);
			}
		}
		if (bomReportTemplateList != null)
		{
			for (BOMReportTemplate end2 : bomReportTemplateList)
			{
				sds.delete(end2);
			}
		}

	}

	protected BOMTemplate saveBOMTemplate(BOMTemplate bomTemplate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(bomTemplate);

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			List<BOMTemplateEnd2> bomTemplateEnd2List = null;
			List<BOMReportTemplate> bomReportTemplateList = null;
			boolean isCreate = false;
			String bomTemplateGuid = bomTemplate.getGuid();
			String operatorGuid = this.stubService.getOperatorGuid();
			bomTemplateEnd2List = bomTemplate.getBOMTemplateEnd2List();
			bomReportTemplateList = bomTemplate.getBOMReportTemplateList();

			if (!StringUtils.isGuid(bomTemplate.getBmGuid()))
			{
				bomTemplate.setBmGuid(BOMTemplateInfo.ALL);
			}

			bomTemplate.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (StringUtils.isNullString(bomTemplateGuid))
			{
				isCreate = true;
				bomTemplate.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}
			List<BOMTemplateInfo> bomTemplateList = DataServer.getRelationService().listBOMTemplateByName(bomTemplate.getName());
			if (bomTemplateList != null)
			{
				bomTemplateList = bomTemplateList.stream().filter(info -> StringUtils.convertNULLtoString(info.getBmGuid()).equals(bomTemplate.getBmGuid())
						&& StringUtils.convertNULLtoString(info.getEnd1BoName()).equals(bomTemplate.getEnd1BoName())).collect(Collectors.toList());
			}
			if (!SetUtils.isNullList(bomTemplateList))
			{
				if (!bomTemplateList.get(0).getGuid().equals(bomTemplateGuid))
				{
					throw new ServiceRequestException("ID_APP_END1_BOM_TEMPLATE_NAME_MUTLI", "end1 bom template name mutli create");
				}

			}
			if (!isCreate)
			{
				deleteTemplateSub(this.stubService.getEMM().getBOMTemplate(bomTemplate.getGuid()));
			}

			String ret = sds.save(bomTemplate.getInfo());

			if (StringUtils.isGuid(ret))
			{
				bomTemplate.setGuid(ret);
				bomTemplateGuid = ret;
			}
			// 保存BOMTemplateEnd2明细
			if (bomTemplateEnd2List != null)
			{
				DataServer.getRelationService().deleteBOMTemplateEnd2(bomTemplateGuid);

				for (BOMTemplateEnd2 bomTemplateEnd2 : bomTemplateEnd2List)
				{
					bomTemplateEnd2.setMasterFK(bomTemplateGuid);
					bomTemplateEnd2.setGuid(null);
					bomTemplateEnd2.put(SystemObject.CREATE_USER_GUID, operatorGuid);

					BOInfo boInfo = null;
					if (!BOMTemplateInfo.ALL.equals(bomTemplate.getBmGuid()))
					{
						boInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bomTemplate.getBmGuid(), bomTemplateEnd2.getEnd2BoName());
					}
					else
					{
						// boInfo = this.stubService.getEMM().getCurrentBoInfoByName(bomTemplateEnd2.getEnd2BoName());
						BMInfo sharedBizModel = this.stubService.getEMM().getSharedBizModel();
						boInfo = this.stubService.getEMM().getBoInfoByNameAndBM(sharedBizModel.getGuid(), bomTemplateEnd2.getEnd2BoName());
					}

					if (boInfo != null)
					{
						ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(boInfo.getClassGuid());
						if (classInfo != null)
						{
							if (!classInfo.hasInterface(ModelInterfaceEnum.IItem))
							{
								throw new ServiceRequestException("ID_APP_END2_INTERFACE_NOT_MATCH", "end2 interface is not match");
							}
						}
					}

					String save = sds.save(bomTemplateEnd2);
					if (StringUtils.isGuid(save))
					{
						bomTemplateEnd2.setGuid(save);
					}
					bomTemplateEnd2.setEnd2BoTitle(boInfo.getTitle());
				}
			}

			// 保存BOMReportTemplate明细
			if (bomReportTemplateList != null)
			{
				DataServer.getRelationService().deleteBOMReportTemplate(bomTemplateGuid);

				for (BOMReportTemplate bomReportTemplate : bomReportTemplateList)
				{
					bomReportTemplate.setBOMTemplateGUID(bomTemplateGuid);
					bomReportTemplate.setGuid(null);
					bomReportTemplate.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					bomReportTemplate.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

					String save = sds.save(bomReportTemplate);
					if (StringUtils.isGuid(save))
					{
						bomReportTemplate.setGuid(save);
					}
				}
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();

			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_UNIQUE)
			{
				e.setDataExceptionEnum(DataExceptionEnum.DS_UNIQUE_ID);
			}

			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}
		return this.stubService.getEMM().getBOMTemplate(bomTemplate.getGuid());
	}

}
