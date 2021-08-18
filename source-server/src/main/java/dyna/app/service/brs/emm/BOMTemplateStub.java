/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplateStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.bom.BOMReportTemplate;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Wanglei
 */
@Component
public class BOMTemplateStub extends AbstractServiceStub<EMMImpl>
{

	protected List<BOMTemplateInfo> listAllBOMTemplate(boolean isContainInValid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getReleationService().listAllBOMTemplateInfo(isContainInValid);
	}

	protected List<String> listAllBOMTemplateName(boolean isContainObsolete) throws ServiceRequestException
	{
		List<String> nameList = new ArrayList<>();
		List<BOMTemplateInfo> templateList = listAllBOMTemplate(isContainObsolete);
		for (BOMTemplateInfo template : templateList)
		{
			if (nameList.contains(template.getName()))
			{
				continue;
			}
			nameList.add(template.getName());
		}
		return nameList;
	}

	protected BOMTemplate getBOMTemplate(String bomTemplateGuid) throws ServiceRequestException
	{
		BOMTemplateInfo templateInfo = this.stubService.getDomainServiceStub().getReleationService().getBOMTemplateInfo(bomTemplateGuid);
		if (templateInfo != null)
		{
			BOMTemplate template = new BOMTemplate(templateInfo);
			template.setBOMTemplateEnd2List(this.stubService.getDomainServiceStub().getReleationService().listBOMTemplateEnd2(templateInfo.getGuid()));
			template.setBOMReportTemplateList(this.stubService.getDomainServiceStub().getReleationService().listBOMReportTemplate(templateInfo.getGuid()));
			this.decorateTemplate(template);
			return template;
		}
		return null;
	}

	protected BOMTemplateInfo getBOMTemplateInfo(String bomTemplateGuid) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getReleationService().getBOMTemplateInfo(bomTemplateGuid);
	}

	public BOMTemplateInfo getBOMTemplateInfoById(String id) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getReleationService().getBOMTemplateInfoById(id);
	}

	protected List<BOMTemplateEnd2> listBOMTemplateEnd2(String templateGuid)
	{
		return this.stubService.getDomainServiceStub().getReleationService().listBOMTemplateEnd2(templateGuid);
	}

	protected List<BOMReportTemplate> listBOMReportTemplate(String templateGuid)
	{
		return this.stubService.getDomainServiceStub().getReleationService().listBOMReportTemplate(templateGuid);
	}

	protected List<BOMTemplateInfo> listBOMTemplateInfoByName(String templateName, boolean isContainInValid) throws ServiceRequestException
	{
		return this.listBOMTemplate(templateName, null, null, isContainInValid);
	}

	protected List<BOMTemplateInfo> listBOMTemplate(String templateName, String bmGuid, String boGuid, boolean isContainObsolete) throws ServiceRequestException
	{
		List<BOMTemplateInfo> bomTemplateList = this.listAllBOMTemplate(isContainObsolete);
		if (SetUtils.isNullList(bomTemplateList))
		{
			return null;
		}

		if (!StringUtils.isNull(templateName))
		{
			bomTemplateList = bomTemplateList.stream().filter(bomTemplate -> bomTemplate.getName().equals(templateName)).collect(Collectors.toList());
		}

		if (SetUtils.isNullList(bomTemplateList))
		{
			return new ArrayList<>();
		}
		Set<String> end1ScopeSet = null;
		if (!StringUtils.isNull(boGuid))
		{
			BOInfo boinfo = this.stubService.getDomainServiceStub().getBusinessModelService().getBOInfoByGuid(boGuid);
			if (boinfo == null)
			{
				return new ArrayList<>();
			}
			end1ScopeSet = this.stubService.getClassStub().getAllSuperClassGuidSet(boinfo.getClassGuid());
		}
		List<BOMTemplateInfo> rt = new ArrayList<>();
		for (BOMTemplateInfo t : bomTemplateList)
		{
			if (!isContainObsolete && !t.isValid())
			{
				continue;
			}
			if (bmGuid != null && !(bmGuid.equalsIgnoreCase(t.getBmGuid()) || "ALL".equalsIgnoreCase(t.getBmGuid())))
			{
				continue;
			}
			if (end1ScopeSet != null)
			{
				BMInfo bmInfo = (bmGuid == null ? this.stubService.getCurrentBizModel() : this.stubService.getBizModel(bmGuid));
				if (bmInfo == null)
				{
					continue;
				}
				if (StringUtils.isNullString(t.getEnd1BoName()))
				{
					continue;
				}
				BOInfo boinfo = this.stubService.getBMStub().getBoInfoByNameAndBM(bmInfo.getGuid(), t.getEnd1BoName(), false);
				if (boinfo == null || end1ScopeSet.contains(boinfo.getClassGuid()) == false)
				{
					continue;
				}
			}
			rt.add(t);
		}
		return rt;
	}

	protected List<BOMTemplateInfo> listAllBOMTemplateInfoByEnd1BO(String boName, boolean isContainInValid) throws ServiceRequestException
	{
		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		BOInfo boInfoByNameAndBM = this.stubService.getBoInfoByNameAndBM(currentBizModelGuid, boName);
		if (boInfoByNameAndBM == null)
		{
			return null;
		}
		return this.listBOMTemplate(null, currentBizModelGuid, boInfoByNameAndBM.getGuid(), isContainInValid);
	}

	protected BOMTemplateInfo getBOMTemplateInfoByName(ObjectGuid objectGuid, String templateName) throws ServiceRequestException
	{
		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		BOInfo boInfo = this.stubService.getBMStub().getBizObject(currentBizModelGuid, objectGuid.getClassGuid(), null);
		if (boInfo == null)
		{
			return null;
		}
		List<BOMTemplateInfo> list = this.listBOMTemplate(null, currentBizModelGuid, boInfo.getGuid(), false);
		return SetUtils.isNullList(list) ? null : list.get(0);
	}

	protected List<BOMTemplateInfo> listBOMTemplateInfoByEND1(ObjectGuid objectGuid, String bmGuid) throws ServiceRequestException
	{
		if (objectGuid == null)
		{
			return null;
		}
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		BOInfo boInfo = this.stubService.getBizObject(bmGuid, objectGuid.getClassGuid(), null);
		if (boInfo == null && objectGuid.getBizObjectGuid() != null)
		{
			if (bmGuid.equals("ALL"))
			{
				bmGuid = this.stubService.getSharedBizModel().getGuid();
			}
			boInfo = this.stubService.getBizObject(bmGuid, objectGuid.getBizObjectGuid());
			if (boInfo == null)
			{
				return null;
			}
		}
		return this.listBOMTemplate(null, bmGuid, boInfo.getGuid(), false);
	}

	protected List<BOMTemplateInfo> listBOMTemplateInfoByEND2(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid == null)
		{
			return null;
		}
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		List<String> boNameList = this.stubService.getBMStub().getSuperSelfBONameList(objectGuid.getClassGuid());

		List<BOMTemplateInfo> relationTemplateList = new ArrayList<>();

		List<BOMTemplateInfo> listRelationTemplate = this.listBOMTemplate(null, this.stubService.getCurrentBizModel().getGuid(), null, false);
		if (!SetUtils.isNullList(listRelationTemplate))
		{
			for (BOMTemplateInfo t : listRelationTemplate)
			{
				List<BOMTemplateEnd2> bomTemplateEnd2List = this.listBOMTemplateEnd2(t.getGuid());
				if (!SetUtils.isNullList(bomTemplateEnd2List))
				{
					for (BOMTemplateEnd2 end2 : bomTemplateEnd2List)
					{
						if (boNameList.contains(end2.getEnd2BoName()))
						{
							relationTemplateList.add(t);
							break;
						}
					}
				}
			}
		}
		return relationTemplateList;
	}

	protected Set<String> getEnd2BONameSet(String relationTemplateGuid, boolean isTree) throws ServiceRequestException
	{
		Set<String> end2BoNameList = new HashSet<String>();
		if (isTree)
		{
			BOMTemplateInfo temp = this.getBOMTemplateInfo(relationTemplateGuid);
			List<BOMTemplateInfo> tempList = this.listBOMTemplateInfoByName(temp.getName(), false);
			for (BOMTemplateInfo xtemp : tempList)
			{
				List<BOMTemplateEnd2> end2list = this.listBOMTemplateEnd2(xtemp.getGuid());
				if (end2list != null)
				{
					for (BOMTemplateEnd2 end2 : end2list)
					{
						end2BoNameList.add(end2.getEnd2BoName());
					}
				}
			}
		}
		else
		{
			List<BOMTemplateEnd2> end2list = this.listBOMTemplateEnd2(relationTemplateGuid);
			if (end2list != null)
			{
				for (BOMTemplateEnd2 end2 : end2list)
				{
					end2BoNameList.add(end2.getEnd2BoName());
				}
			}
		}
		return end2BoNameList;
	}

	public List<String> listBOMReportTemplateName() throws ServiceRequestException
	{
		List<String> rtnFileNameList = new ArrayList<>();
		String filePath = EnvUtils.getConfRootPath() + "conf/comment/report/bomReport";
		File folder = new File(filePath);
		File[] fileArray = folder.listFiles();
		if (fileArray != null && fileArray.length > 0)
		{
			for (File file : fileArray)
			{
				if (file.getName().endsWith(".jrxml"))
				{
					rtnFileNameList.add(file.getName());
				}
			}
		}

		if (rtnFileNameList.size() == 0)
		{
			rtnFileNameList = null;
		}

		return rtnFileNameList;
	}

	private void decorateTemplate(BOMTemplate template)
	{
		ObjectGuid objectGuid = new ObjectGuid(template.getViewClassGuid(), template.getViewClassName(), null, null);
		try
		{
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			template.setViewClassGuid(objectGuid.getClassGuid());
			template.setViewClassName(objectGuid.getClassName());

			objectGuid = new ObjectGuid(template.getStructureClassGuid(), template.getStructureClassName(), null, null);
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			template.setStructureClassGuid(objectGuid.getClassGuid());
			template.setStructureClassName(objectGuid.getClassName());

			if (!StringUtils.isNullString(template.getEnd1BoName()))
			{
				String end1BoTitle = this.getBoTitle(template.getEnd1BoName(), template.getBmGuid());
				if (!StringUtils.isNullString(end1BoTitle))
				{
					template.setEnd1BoTitle(end1BoTitle);
				}
			}

			List<BOMTemplateEnd2> templateEnd2List = template.getBOMTemplateEnd2List();
			if (!SetUtils.isNullList(templateEnd2List))
			{
				templateEnd2List.forEach(templateEnd2 -> {
					if (!StringUtils.isNullString(templateEnd2.getEnd2BoName()))
					{
						String end2BoName = templateEnd2.getEnd2BoName();
						String end2BoTitle = null;
						try
						{
							end2BoTitle = this.getBoTitle(end2BoName, template.getBmGuid());
						}
						catch (ServiceRequestException ignored)
						{
						}
						templateEnd2.put(RelationTemplateEnd2.END2_BO_TITLE, end2BoTitle);
					}
				});
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}

	private String getBoTitle(String boName, String bmGuid) throws ServiceRequestException
	{
		if ("ALL".equalsIgnoreCase(bmGuid))
		{
			BMInfo sharedBizModel = this.stubService.getSharedBizModel();
			if (sharedBizModel != null)
			{
				bmGuid = sharedBizModel.getGuid();
			}
		}

		BOInfo boInfo = null;
		try
		{
			boInfo = this.stubService.getBoInfoByNameAndBM(bmGuid, boName);
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.error(e.getMessage());
		}

		if (boInfo != null && !StringUtils.isNullString(boInfo.getTitle()))
		{
			return boInfo.getTitle();
		}
		return null;
	}
}
