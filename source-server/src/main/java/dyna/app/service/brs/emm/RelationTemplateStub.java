/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TemplateStub
 * Wanglei 2011-3-31
 */
package dyna.app.service.brs.emm;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.dto.model.bmbo.BMInfo;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.BuiltinRelationNameEnum;
import dyna.common.systemenum.RelationTemplateTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Wanglei
 */
@Component
public class RelationTemplateStub extends AbstractServiceStub<EMMImpl> implements Comparator<RelationTemplateInfo>
{

	public RelationTemplate getRelationTemplate(String relationTemplateGuid) throws ServiceRequestException
	{
		RelationTemplateInfo templateInfo = this.stubService.getDomainServiceStub().getReleationService().getRelationTemplateInfo(relationTemplateGuid);
		if (templateInfo != null)
		{
			RelationTemplate template = new RelationTemplate(templateInfo);
			List<RelationTemplateEnd2> relationTemplateEnd2List = this.stubService.getDomainServiceStub().getReleationService().listRelationTemplateEnd2(relationTemplateGuid);
			template.setRelationTemplateEnd2List(relationTemplateEnd2List);

			this.decorateTemplate(template);

			return template;
		}
		return null;
	}

	protected RelationTemplateInfo getRelationTemplateById(String templateId) throws ServiceRequestException
	{
		RelationTemplateInfo templateInfo = this.stubService.getDomainServiceStub().getReleationService().getRelationTemplateInfoById(templateId);
		return templateInfo;
	}

	/**
	 * 实际应用，不适用于管理页
	 *
	 * @param bmGuid
	 * @param templateName
	 * @param type
	 * @param end1BOGuidList
	 * @param isContainObsolete
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplate(String templateName, String type, String bmGuid, String boGuid, boolean isContainObsolete) throws ServiceRequestException
	{
		List<RelationTemplateInfo> rt = new ArrayList<>();

		List<RelationTemplateInfo> templateList = this.stubService.getDomainServiceStub().getReleationService().listAllRelationTemplateInfo(isContainObsolete);
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

		if (templateList != null && !templateList.isEmpty())
		{
			for (RelationTemplateInfo t : templateList)
			{
				if (!StringUtils.isNull(templateName))
				{
					if (!templateName.equals(t.getName()))
					{
						continue;
					}
				}
				if (!isContainObsolete && !t.isValid())
				{
					continue;
				}
				if (type != null && !type.contains(t.getTemplateType()))
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
		}
		return rt;
	}

	protected List<RelationTemplateInfo> listRelationTemplateByName(String templateName, boolean isContainInValid) throws ServiceRequestException
	{
		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		return this.listRelationTemplate(templateName, null, currentBizModelGuid, null, isContainInValid);
	}

	/**
	 * 获取指定的ObjectGuid对应的所有的关联关系模板
	 *
	 * @param objectGuid
	 * @param boas
	 * @return 关联关系模板列表
	 * @throws ServiceRequestException
	 */
	public List<RelationTemplateInfo> listRelationTemplate(ObjectGuid objectGuid, String type) throws ServiceRequestException
	{
		if (objectGuid == null)
		{
			return null;
		}
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		BOInfo boInfo = null;
		if (objectGuid.getClassGuid() != null)
		{
			boInfo = this.stubService.getCurrentBizObject(objectGuid.getClassGuid());
		}
		else if (!StringUtils.isNullString(objectGuid.getBizObjectGuid()))
		{
			boInfo = this.stubService.getCurrentBizObjectByGuid(objectGuid.getBizObjectGuid());
		}
		List<RelationTemplateInfo> rt = new ArrayList<>();

		if (boInfo != null)
		{
			List<RelationTemplateInfo> listRelationTemplate = this.listRelationTemplate(null, type, currentBizModelGuid, boInfo.getGuid(), false);
			if (!SetUtils.isNullList(listRelationTemplate))
			{
				for (RelationTemplateInfo templateInfo : listRelationTemplate)
				{
					if (this.hasRelationTemplateEnd2(templateInfo))
					{
						rt.add(templateInfo);
					}
				}
			}
		}
		return rt;

	}

	protected List<RelationTemplateInfo> listRelationTemplate4Opposite(ObjectGuid end2ObjectGuid) throws ServiceRequestException
	{
		List<RelationTemplateInfo> relationTemplateList = new ArrayList<RelationTemplateInfo>();
		List<RelationTemplateInfo> end2EnableTemplateList = this.listRelationTemplateByEND2(end2ObjectGuid);
		if (!SetUtils.isNullList(end2EnableTemplateList))
		{
			for (RelationTemplateInfo templateInfo : end2EnableTemplateList)
			{
				RelationTemplate template = new RelationTemplate(templateInfo);
				List<RelationTemplateEnd2> relationTemplateEnd2List = this.stubService.getDomainServiceStub().getReleationService().listRelationTemplateEnd2(templateInfo.getGuid());
				template.setRelationTemplateEnd2List(relationTemplateEnd2List);

				this.decorateTemplate(template);
				if (template.canOpposite())
				{
					relationTemplateList.add(template.getInfo());
				}
			}
		}

		return relationTemplateList;
	}

	protected RelationTemplateInfo getRelationTemplateByName(ObjectGuid objectGuid, String templateName) throws ServiceRequestException
	{
		RelationTemplateInfo relationTemplate = null;

		if (objectGuid == null)
		{
			return null;
		}

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		String currentBizModelGuid = this.stubService.getUserSignature().getLoginGroupBMGuid();
		BOInfo boInfo = this.stubService.getCurrentBizObject(objectGuid.getClassGuid());

		if (boInfo == null)
		{
			return null;
		}

		List<RelationTemplateInfo> listRelationTemplate = this.listRelationTemplate(templateName, null, currentBizModelGuid, boInfo.getGuid(), false);
		if (!SetUtils.isNullList(listRelationTemplate))
		{
			for (RelationTemplateInfo templateInfo : listRelationTemplate)
			{
				if (this.hasRelationTemplateEnd2(templateInfo))
				{
					relationTemplate = templateInfo;
					break;
				}
			}
		}
		return relationTemplate;
	}

	public List<String> listRelationTemplateName(String templateType, boolean isContainObsolete) throws ServiceRequestException
	{
		List<RelationTemplateInfo> relationTemplateList = this.stubService.getDomainServiceStub().getReleationService().listAllRelationTemplateInfo(isContainObsolete);

		List<String> nameList = new ArrayList<>();

		for (RelationTemplateInfo relationTemplate : relationTemplateList)
		{
			if (!nameList.contains(relationTemplate.getName()))
			{
				nameList.add(relationTemplate.getName());
			}
		}
		return nameList;
	}

	protected List<RelationTemplateInfo> listRelationTemplate4Builtin(ObjectGuid end1ObjectGuid) throws ServiceRequestException
	{
		List<RelationTemplateInfo> relationTemplateSequenceList = new ArrayList<>();
		List<RelationTemplateInfo> relationTemplateList = this.listRelationTemplate(end1ObjectGuid, "0");

		if (!SetUtils.isNullList(relationTemplateList))
		{
			RelationTemplateInfo[] arrayTemplate = new RelationTemplateInfo[6];
			for (RelationTemplateInfo relationTemplate : relationTemplateList)
			{
				boolean end2BOHasExist = hasRelationTemplateEnd2(relationTemplate);
				if (BuiltinRelationNameEnum.ITEMCAD3D.toString().equalsIgnoreCase(relationTemplate.getName()))
				{
					arrayTemplate[0] = !end2BOHasExist ? null : relationTemplate;
				}
				else if (BuiltinRelationNameEnum.ITEMCAD2D.toString().equalsIgnoreCase(relationTemplate.getName()))
				{
					arrayTemplate[1] = !end2BOHasExist ? null : relationTemplate;
				}
				else if (BuiltinRelationNameEnum.ITEMECAD.toString().equalsIgnoreCase(relationTemplate.getName()))
				{
					arrayTemplate[2] = !end2BOHasExist ? null : relationTemplate;
				}
				else if (BuiltinRelationNameEnum.MODEL_STRUCTURE.toString().equalsIgnoreCase(relationTemplate.getName()))
				{
					arrayTemplate[3] = !end2BOHasExist ? null : relationTemplate;
				}
				else if (BuiltinRelationNameEnum.MODEL_REFERENCE.toString().equalsIgnoreCase(relationTemplate.getName()))
				{
					arrayTemplate[4] = !end2BOHasExist ? null : relationTemplate;
				}
				else if (BuiltinRelationNameEnum.MODEL_MEMBER.toString().equalsIgnoreCase(relationTemplate.getName()))
				{
					arrayTemplate[5] = !end2BOHasExist ? null : relationTemplate;
				}
			}
			if (arrayTemplate[0] != null)
			{
				relationTemplateSequenceList.add(arrayTemplate[0]);
			}
			if (arrayTemplate[1] != null)
			{
				relationTemplateSequenceList.add(arrayTemplate[1]);
			}
			if (arrayTemplate[2] != null)
			{
				relationTemplateSequenceList.add(arrayTemplate[2]);
			}
			if (arrayTemplate[3] != null)
			{
				relationTemplateSequenceList.add(arrayTemplate[3]);
			}
			if (arrayTemplate[4] != null)
			{
				relationTemplateSequenceList.add(arrayTemplate[4]);
			}
			if (arrayTemplate[5] != null)
			{
				relationTemplateSequenceList.add(arrayTemplate[5]);
			}
		}
		return relationTemplateSequenceList;
	}

	private boolean hasRelationTemplateEnd2(RelationTemplateInfo relationTemplate) throws ServiceRequestException
	{
		List<RelationTemplateEnd2> relationTemplateEnd2List = this.listRelationTemplateEnd2(relationTemplate.getGuid());
		boolean end2BOHasExist = false;
		if (!SetUtils.isNullList(relationTemplateEnd2List))
		{
			for (RelationTemplateEnd2 templateEnd2 : relationTemplateEnd2List)
			{
				String end2BO = templateEnd2.getEnd2BoName();
				BOInfo boInfo = this.stubService.getCurrentBoInfoByName(end2BO, false);
				if (boInfo != null)
				{
					end2BOHasExist = true;
					break;
				}
			}
		}
		return end2BOHasExist;
	}

	/**
	 * @param bmGuid
	 * @param end1BOGuidList
	 *            包含所有子类
	 * @param isContainObsolete
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<RelationTemplateInfo> listEnableRelationTemplate4End1BO(String bmGuid, List<String> end1BOGuidList, boolean isContainObsolete) throws ServiceRequestException
	{
		List<RelationTemplateInfo> rt = new ArrayList<>();

		if ("ALL".equalsIgnoreCase(bmGuid))
		{
			bmGuid = this.stubService.getSharedBizModel().getGuid();
		}

		List<RelationTemplateInfo> templateList = this.stubService.getDomainServiceStub().getReleationService().listAllRelationTemplateInfo(true);
		for (RelationTemplateInfo templateInfo : templateList)
		{
			if (!isContainObsolete && !templateInfo.isValid())
			{
				continue;
			}

			if (!(templateInfo.getStructureModel() == RelationTemplateTypeEnum.TREE || templateInfo.getStructureModel() == RelationTemplateTypeEnum.NORMAL))
			{
				continue;
			}

			List<BOInfo> listAllSubBOInfoContain = this.stubService.listAllSubBOInfoContain(templateInfo.getEnd1BoName(), bmGuid);
			if (!SetUtils.isNullList(listAllSubBOInfoContain))
			{
				for (BOInfo boInfo : listAllSubBOInfoContain)
				{
					if (end1BOGuidList.contains(boInfo.getGuid()))
					{
						if (this.hasRelationTemplateEnd2(templateInfo))
						{
							rt.add(templateInfo);
						}
						break;
					}

				}
			}

		}
		rt.sort(this);
		return rt;
	}

	protected List<RelationTemplateInfo> listRelationTemplateByEND2(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid == null)
		{
			return null;
		}

		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		List<String> boNameList = this.stubService.getBMStub().getSuperSelfBONameList(objectGuid.getClassGuid());

		List<RelationTemplateInfo> relationTemplateList = new ArrayList<>();

		List<RelationTemplateInfo> listRelationTemplate = this.listRelationTemplate(null, null, this.stubService.getUserSignature().getLoginGroupBMGuid(), null, false);
		if (!SetUtils.isNullList(listRelationTemplate))
		{
			for (RelationTemplateInfo t : listRelationTemplate)
			{
				List<RelationTemplateEnd2> templateEnd2List = this.listRelationTemplateEnd2(t.getGuid());
				if (!SetUtils.isNullList(templateEnd2List))
				{
					for (RelationTemplateEnd2 end2 : templateEnd2List)
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

	public List<RelationTemplateEnd2> listRelationTemplateEnd2(String guid)
	{
		return this.stubService.getDomainServiceStub().getReleationService().listRelationTemplateEnd2(guid);
	}

	public RelationTemplateInfo getRelationTemplateInfo(String relationTemplateGuid)
	{
		return this.stubService.getDomainServiceStub().getReleationService().getRelationTemplateInfo(relationTemplateGuid);
	}

	protected Set<String> getEnd2BONameSet(String relationTemplateGuid, boolean isTree) throws ServiceRequestException
	{
		Set<String> end2BoNameList = new HashSet<String>();
		if (isTree)
		{
			RelationTemplateInfo temp = this.stubService.getDomainServiceStub().getReleationService().getRelationTemplateInfo(relationTemplateGuid);
			List<RelationTemplateInfo> tempList = this.listRelationTemplateByName(temp.getName(), false);
			for (RelationTemplateInfo xtemp : tempList)
			{
				List<RelationTemplateEnd2> end2list = this.listRelationTemplateEnd2(xtemp.getGuid());
				if (end2list != null)
				{
					for (RelationTemplateEnd2 end2 : end2list)
					{
						end2BoNameList.add(end2.getEnd2BoName());
					}
				}
			}
		}
		else
		{
			List<RelationTemplateEnd2> end2list = this.listRelationTemplateEnd2(relationTemplateGuid);
			if (end2list != null)
			{
				for (RelationTemplateEnd2 end2 : end2list)
				{
					end2BoNameList.add(end2.getEnd2BoName());
				}
			}
		}
		return end2BoNameList;
	}

	@Override
	public int compare(RelationTemplateInfo o1, RelationTemplateInfo o2)
	{
		if (o1 == null)
		{
			return -1;
		}
		if (o2 == null)
		{
			return -1;
		}

		return o1.getName().compareToIgnoreCase(o2.getName());
	}

	private BOInfo getBoInfo(String boName, String bmGuid) throws ServiceRequestException
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

		return boInfo;
	}

	private void decorateTemplate(RelationTemplate template) throws ServiceRequestException
	{
		BOInfo end1boInfo = this.getBoInfo(template.getEnd1BoName(), template.getBmGuid());

		if (end1boInfo != null)
		{
			template.setEnd1BoTitle(end1boInfo.getTitle());
		}

		try
		{
			ObjectGuid objectGuid = new ObjectGuid(template.getViewClassGuid(), template.getViewClassName(), null, null);
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			template.setViewClassGuid(objectGuid.getClassGuid());
			template.setViewClassName(objectGuid.getClassName());

			objectGuid = new ObjectGuid(template.getStructureClassGuid(), template.getStructureClassName(), null, null);
			ClassStub.decorateObjectGuid(objectGuid, this.stubService);
			template.setStructureClassGuid(objectGuid.getClassGuid());
			template.setStructureClassName(objectGuid.getClassName());

			List<RelationTemplateEnd2> relationTemplateEnd2List = template.getRelationTemplateEnd2List();
			if (!SetUtils.isNullList(relationTemplateEnd2List))
			{
				for (RelationTemplateEnd2 templateEnd2 : relationTemplateEnd2List)
				{
					String end2BoName = templateEnd2.getEnd2BoName();
					BOInfo end2BOInfo = this.getBoInfo(end2BoName, template.getBmGuid());
					if (end2BOInfo != null)
					{
						templateEnd2.setEnd2BoTitle(end2BOInfo.getTitle());
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}
}
