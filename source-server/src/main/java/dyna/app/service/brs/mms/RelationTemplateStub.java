/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TemplateStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.mms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
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
public class RelationTemplateStub extends AbstractServiceStub<MMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public RelationTemplateStub(ServiceContext context, MMSImpl service)
	{
		super(context, service);
	}

	protected void obsoleteRelationTemplateByName(String templateName) throws ServiceRequestException
	{
		if (!StringUtils.isNullString(templateName) && templateName.contains("$"))
		{
			throw new ServiceRequestException("ID_APP_TEMPLATE_BUILTIN_NOT_OBSOLETE", "can't obsolete builtin tempalte");
		}

		List<RelationTemplateInfo> templateList = DataServer.getRelationService().listRelationTemplateByName(templateName);
		if (!SetUtils.isNullList(templateList))
		{
			for (RelationTemplateInfo info : templateList)
			{
				this.obsoleteRelationTemplateInfo(info);
			}
		}
	}

	protected void obsoleteRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		this.obsoleteRelationTemplateInfo(DataServer.getRelationService().getRelationTemplateInfo(relationTemplateGuid));
	}

	private void obsoleteRelationTemplateInfo(RelationTemplateInfo relationTemplate) throws ServiceRequestException
	{
		if (relationTemplate != null)
		{
			if ("0".equals(relationTemplate.getTemplateType()))
			{
				throw new ServiceRequestException("ID_APP_TEMPLATE_BUILTIN_NOT_OBSOLETE", "can't obsolete builtin tempalte");
			}

			relationTemplate.setValid(false);
			this.saveRelationTemplateInfo(relationTemplate);
		}
	}

	protected void reUseRelationTemplateByName(String templateName) throws ServiceRequestException
	{
		List<RelationTemplateInfo> templateList = DataServer.getRelationService().listRelationTemplateByName(templateName);
		if (!SetUtils.isNullList(templateList))
		{
			for (RelationTemplateInfo info : templateList)
			{
				this.reUseRelationTemplateInfo(info);
			}
		}
	}

	protected void reUseRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		this.reUseRelationTemplateInfo(DataServer.getRelationService().getRelationTemplateInfo(relationTemplateGuid));
	}

	private void reUseRelationTemplateInfo(RelationTemplateInfo relationTemplate) throws ServiceRequestException
	{
		if (relationTemplate != null)
		{
			if (relationTemplate.isValid())
			{
				return;
			}
			relationTemplate.setValid(true);
			this.saveRelationTemplateInfo(relationTemplate);
		}
	}

	protected void deleteRelationTemplateByName(String templateName) throws ServiceRequestException
	{
		if (!StringUtils.isNullString(templateName) && templateName.contains("$"))
		{
			throw new ServiceRequestException("ID_APP_TEMPLATE_BUILTIN_NOT_DELETE", "can't delete builtin tempalte");
		}
		List<RelationTemplateInfo> templateList = DataServer.getRelationService().listRelationTemplateByName(templateName);
		if (!SetUtils.isNullList(templateList))
		{
			for (RelationTemplateInfo info : templateList)
			{
				this.deleteRelationTemplate(info.getGuid());
			}
		}
	}

	/**
	 * 删除关联关系模板的处理
	 * 
	 * @param relationTemplateGuid
	 *            要删除的关联关系模板对象的Guid
	 * @throws ServiceRequestException
	 * @author caogc
	 */
	protected void deleteRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			RelationTemplate template = this.stubService.getEMM().getRelationTemplate(relationTemplateGuid);
			if ("0".equals(template.getTemplateType()))
			{
				throw new ServiceRequestException("ID_APP_TEMPLATE_BUILTIN_NOT_DELETE", "can't delete builtin tempalte");
			}
			if (template.getRelationTemplateEnd2List() != null)
			{
				for (RelationTemplateEnd2 end2 : template.getRelationTemplateEnd2List())
				{
					sds.delete(end2);
				}
			}
			sds.delete(template.getInfo());
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void saveRelationTemplateInfo(RelationTemplateInfo relationTemplate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.save(relationTemplate);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 更新/新建一个RelationTemplate对象
	 * <String> 该操作需要管理员权限</String>
	 * 
	 * @param relationTemplate
	 *            关联关系模板对象
	 * @return RelationTemplate对象
	 * @throws ServiceRequestException
	 */
	protected RelationTemplate saveRelationTemplate(RelationTemplate relationTemplate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (relationTemplate.getMaxQuantity() < 0)
			{
				throw new ServiceRequestException("ID_APP_INVALID_NUMBER", "positive integer only");
			}

			// RelationTemplate retRelationTemplate = null;
			List<RelationTemplateEnd2> relationTemplateEnd2List = null;
			boolean isCreate = false;
			String relationTemplateGuid = relationTemplate.getGuid();

			String operatorGuid = this.stubService.getOperatorGuid();

			relationTemplateEnd2List = relationTemplate.getRelationTemplateEnd2List();

			if (!StringUtils.isGuid(relationTemplate.getBmGuid()))
			{
				relationTemplate.setBmGuid(BOMTemplateInfo.ALL);
			}

			relationTemplate.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

			if (StringUtils.isNullString(relationTemplateGuid))
			{
				isCreate = true;
				relationTemplate.put(SystemObject.CREATE_USER_GUID, operatorGuid);
			}

			List<RelationTemplateInfo> relationTemplateInfoList = DataServer.getRelationService().listRelationTemplateByName(relationTemplate.getName());
			if (relationTemplateInfoList != null)
			{
				relationTemplateInfoList = relationTemplateInfoList.stream().filter(info -> StringUtils.convertNULLtoString(info.getBmGuid()).equals(relationTemplate.getBmGuid())
						&& StringUtils.convertNULLtoString(info.getEnd1BoName()).equals(relationTemplate.getEnd1BoName())).collect(Collectors.toList());
			}

			if (!SetUtils.isNullList(relationTemplateInfoList))
			{
				if (!relationTemplateInfoList.get(0).getGuid().equals(relationTemplate.getGuid()))
				{
					throw new ServiceRequestException("ID_APP_END1_RELATION_TEMPLATE_NAME_MUTLI", "end1 relation template name mutli create");
				}
			}
			if (isCreate == false)
			{
				deleteRelationTemplateEnd2(relationTemplateGuid);
			}

			String ret = sds.save(relationTemplate.getInfo());

			if (StringUtils.isGuid(ret))
			{
				relationTemplateGuid = ret;
				relationTemplate.setGuid(ret);
			}

			// 保存relationTemplateEnd2明细
			if (relationTemplateEnd2List != null)
			{
				for (RelationTemplateEnd2 relationTemplateEnd2 : relationTemplateEnd2List)
				{
					relationTemplateEnd2.setMasterFK(relationTemplateGuid);
					relationTemplateEnd2.setGuid(null);
					relationTemplateEnd2.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					BOInfo boInfo = null;
					if (!BOMTemplateInfo.ALL.equals(relationTemplate.getBmGuid()))
					{
						boInfo = this.stubService.getEMM().getBoInfoByNameAndBM(relationTemplate.getBmGuid(), relationTemplateEnd2.getEnd2BoName());
					}
					else
					{
						BMInfo sharedBizModel = this.stubService.getEMM().getSharedBizModel();
						boInfo = this.stubService.getEMM().getBoInfoByNameAndBM(sharedBizModel.getGuid(), relationTemplateEnd2.getEnd2BoName());
					}
					if (relationTemplate.getEnd2Interface() != null)
					{

						if (boInfo != null)
						{
							ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(boInfo.getClassGuid());
							if (classInfo != null)
							{
								if (!classInfo.hasInterface(relationTemplate.getEnd2Interface()))
								{
									throw new ServiceRequestException("ID_APP_END2_INTERFACE_NOT_MATCH", "end2 interface is not match");
								}
							}
						}

					}

					String save = sds.save(relationTemplateEnd2);
					if (StringUtils.isGuid(save))
					{
						relationTemplateEnd2.setGuid(save);
					}
					relationTemplateEnd2.setEnd2BoTitle(boInfo.getTitle());
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
		return this.stubService.getEMM().getRelationTemplate(relationTemplate.getGuid());
	}

	private void deleteRelationTemplateEnd2(String relationTemplateGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		RelationTemplate template = this.stubService.getEMM().getRelationTemplate(relationTemplateGuid);
		if (template.getRelationTemplateEnd2List() != null)
		{
			for (RelationTemplateEnd2 end2 : template.getRelationTemplateEnd2List())
			{
				sds.delete(end2);
			}
		}
	}
}
