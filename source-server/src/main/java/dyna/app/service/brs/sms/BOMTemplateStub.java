/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplateStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.sms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.dto.aas.Group;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wanglei
 */
@Component
public class BOMTemplateStub extends AbstractServiceStub<SMSImpl>
{

	protected void deleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.delete(BOMTemplate.class, bomTemplateGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void obsoleteBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		try
		{
			BOMTemplate bomTemplate = this.stubService.getEMM().getBOMTemplate(bomTemplateGuid);
			if (bomTemplate != null)
			{
				bomTemplate.setValid(false);
				SystemDataService sds = DataServer.getSystemDataService();
				sds.save(bomTemplate.getInfo());
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void obsoleteBOMTemplateByName(String bomTemplateName) throws ServiceRequestException
	{
		List<BOMTemplateInfo> templateList = this.stubService.getEMM().listBOMTemplateByName(bomTemplateName, false);
		if (!SetUtils.isNullList(templateList))
		{
			for (BOMTemplateInfo template : templateList)
			{
				this.obsoleteBOMTemplate(template.getGuid());
			}
		}
	}

	protected void reUseBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		try
		{
			BOMTemplate bomTemplate = this.stubService.getEMM().getBOMTemplate(bomTemplateGuid);
			if (bomTemplate != null)
			{
				if (bomTemplate.isValid())
				{
					return;
				}
				bomTemplate.setValid(true);
				SystemDataService sds = DataServer.getSystemDataService();
				sds.save(bomTemplate.getInfo());
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void reUseBOMTemplateByName(String bomTemplateName) throws ServiceRequestException
	{
		List<BOMTemplateInfo> templateList = this.stubService.getEMM().listBOMTemplateByName(bomTemplateName, false);
		if (!SetUtils.isNullList(templateList))
		{
			for (BOMTemplateInfo template : templateList)
			{
				this.reUseBOMTemplate(template.getGuid());
			}
		}
	}

	protected void saveBOMTemplate(BOMTemplate bomTemplate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		BOMTemplate retBOMTemplate;

		// 判断ID和NAME是否包含$,如果包含并抛异常
		Constants.isContain$(bomTemplate);

		try
		{

//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			String bomTemplateGuid = bomTemplate.getGuid();
			String operatorGuid = this.stubService.getOperatorGuid();
			List<BOMTemplateEnd2> bomTemplateEnd2List = bomTemplate.getBOMTemplateEnd2List();
			List<BOMReportTemplate> bomReportTemplateList = bomTemplate.getBOMReportTemplateList();

			// 判断用户是否有管理员的权限
			String groupGuid = this.stubService.getUserSignature().getLoginGroupGuid();
			try
			{
				Group group = this.stubService.getAAS().getGroup(groupGuid);

				if (group == null || !group.isAdminGroup())
				{
					throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only");
				}

			}
			catch (ServiceRequestException e)
			{
				throw new ServiceRequestException("ID_APP_ADMIN_GROUP_TEAM", "accessible for administrative group only", e);
			}

			if (!StringUtils.isGuid(bomTemplate.getBmGuid()))
			{
				bomTemplate.setBmGuid(BOMTemplateInfo.ALL);
			}

			bomTemplate.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (StringUtils.isNullString(bomTemplateGuid))
			{
				bomTemplate.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			List<BOMTemplateInfo> bomTemplateInfoList = DataServer.getRelationService().listBOMTemplateByName(bomTemplate.getName());
			if (bomTemplateInfoList != null)
			{
				bomTemplateInfoList = bomTemplateInfoList.stream().filter(info -> StringUtils.convertNULLtoString(info.getBmGuid()).equals(bomTemplate.getBmGuid())
						&& StringUtils.convertNULLtoString(info.getEnd1BoName()).equals(bomTemplate.getEnd1BoName())).collect(Collectors.toList());
			}

			if (!SetUtils.isNullList(bomTemplateInfoList))
			{
				if (!bomTemplateInfoList.get(0).getGuid().equals(bomTemplateGuid))
				{
					throw new ServiceRequestException("ID_APP_END1_BOM_TEMPLATE_NAME_MUTLI", "end1 bom template name mutli create");
				}

			}

			String ret = sds.save(bomTemplate.getInfo());

			if (StringUtils.isGuid(ret))
			{
				bomTemplate.setGuid(ret);
				bomTemplateGuid = ret;
			}
			BOInfo end1BoInfo;
			if (!BOMTemplateInfo.ALL.equals(bomTemplate.getBmGuid()))
			{
				end1BoInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bomTemplate.getBmGuid(), bomTemplate.getEnd1BoName());
			}
			else
			{
				BMInfo sharedBizModel = this.stubService.getEMM().getSharedBizModel();
				end1BoInfo = this.stubService.getEMM().getBoInfoByNameAndBM(sharedBizModel.getGuid(), bomTemplate.getEnd1BoName());
			}
			bomTemplate.setEnd1BoTitle(end1BoInfo.getTitle());
			bomTemplate.setViewClassName(this.stubService.getEMM().getClassByGuid(bomTemplate.getViewClassGuid()).getName());
			bomTemplate.setStructureClassName(this.stubService.getEMM().getClassByGuid(bomTemplate.getStructureClassGuid()).getName());
			// 保存BOMTemplateEnd2明细
			if (bomTemplateEnd2List != null)
			{
				DataServer.getRelationService().deleteBOMTemplateEnd2(bomTemplateGuid);

				for (BOMTemplateEnd2 bomTemplateEnd2 : bomTemplateEnd2List)
				{
					bomTemplateEnd2.setMasterFK(bomTemplateGuid);
					bomTemplateEnd2.setGuid(null);
					bomTemplateEnd2.put(SystemObject.CREATE_USER_GUID, operatorGuid);

					BOInfo boInfo;
					if (!BOMTemplateInfo.ALL.equals(bomTemplate.getBmGuid()))
					{
						boInfo = this.stubService.getEMM().getBoInfoByNameAndBM(bomTemplate.getBmGuid(), bomTemplateEnd2.getEnd2BoName());
					}
					else
					{
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
						bomTemplateEnd2.setEnd2BoTitle(boInfo.getTitle());
					}

					String save = sds.save(bomTemplateEnd2);
					if (StringUtils.isGuid(save))
					{
						bomTemplateEnd2.setGuid(save);
					}
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

			retBOMTemplate = bomTemplate.getClass().getConstructor().newInstance();
			retBOMTemplate.sync(bomTemplate);
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
	}
}
