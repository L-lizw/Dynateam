/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EC处理
 * JiangHL 2011-5-10
 */
package dyna.data.service.relation;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.template.BOMTemplate;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.cache.*;
import dyna.common.dto.template.bom.BOMReportTemplate;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.model.DataCacheServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;

/**
 * BOM和关系模板缓存
 *
 * @author JiangHL
 */
public class RelationTemplateStub extends DataCacheServiceStub<RelationServiceImpl>
{
	private static final Map<String, BOMTemplate>			BOMTEMPLATE_GUID_MAP			= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, BOMTemplateEnd2>		BOMTEMPLATE_END2_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, BOMReportTemplate>		BOMTEMPLATE_REPORT_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, List<String>>			BOMTEMPLATE_NAME_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());

	private static final Map<String, RelationTemplate>		RELATIONTEMPLATE_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, RelationTemplateEnd2>	RELATIONTEMPLATE_NED2_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());
	private static final Map<String, List<String>>			RELATIONTEMPLATE_NAME_GUID_MAP	= Collections.synchronizedMap(new HashMap<>());

	private AbstractCacheInfo								cacheInfo						= null;

	protected RelationTemplateStub(DataServerContext context, RelationServiceImpl service)
	{
		super(context, service);
		this.cacheInfo = new TemplateCacheInfo();
		this.cacheInfo.register();
	}

	@Override
	public void loadModel() throws ServiceRequestException
	{
		this.loadBOMTemplate();

		this.loadRelationTemplate();
	}

	private void loadBOMTemplate()
	{
		BOMTEMPLATE_GUID_MAP.clear();
		BOMTEMPLATE_NAME_GUID_MAP.clear();
		BOMTEMPLATE_END2_GUID_MAP.clear();
		BOMTEMPLATE_REPORT_GUID_MAP.clear();
		List<BOMTemplateInfo> bomTemplateList = DataServer.getSystemDataService().listFromCache(BOMTemplateInfo.class, null);
		if (bomTemplateList != null)
		{
			bomTemplateList.forEach(bomTemplateInfo -> {
				BOMTEMPLATE_GUID_MAP.put(bomTemplateInfo.getGuid(), new BOMTemplate(bomTemplateInfo));
				BOMTEMPLATE_NAME_GUID_MAP.computeIfAbsent(bomTemplateInfo.getName(), k -> new ArrayList<>()).add(bomTemplateInfo.getGuid());
				loadBOMTemplate(BOMTEMPLATE_GUID_MAP.get(bomTemplateInfo.getGuid()));
			});
		}
	}

	private void loadRelationTemplate()
	{
		RELATIONTEMPLATE_GUID_MAP.clear();
		RELATIONTEMPLATE_NAME_GUID_MAP.clear();
		RELATIONTEMPLATE_NED2_GUID_MAP.clear();
		List<RelationTemplateInfo> relationTemplateList = DataServer.getSystemDataService().listFromCache(RelationTemplateInfo.class, null);
		if (relationTemplateList != null)
		{
			relationTemplateList.forEach(relationTemplateInfo -> {
				RELATIONTEMPLATE_GUID_MAP.put(relationTemplateInfo.getGuid(), new RelationTemplate(relationTemplateInfo));
				RELATIONTEMPLATE_NAME_GUID_MAP.computeIfAbsent(relationTemplateInfo.getName(), k -> new ArrayList<>()).add(relationTemplateInfo.getGuid());
				loadRelationTemplate(RELATIONTEMPLATE_GUID_MAP.get(relationTemplateInfo.getGuid()));
			});
		}
	}

	protected List<BOMTemplate> listAllBOMTemplate(boolean isContainObsolete)
	{
		List<BOMTemplate> bomTemplateList = new ArrayList<>();
		for (String bomTemplateGuid : BOMTEMPLATE_GUID_MAP.keySet())
		{
			BOMTemplate template = BOMTEMPLATE_GUID_MAP.get(bomTemplateGuid);
			if (!isContainObsolete && !template.isValid())
			{
				continue;
			}

			bomTemplateList.add((BOMTemplate) template.clone());
		}
		return bomTemplateList;
	}

	protected List<RelationTemplate> listAllRelationTemplate(boolean isContainObsolete)
	{
		List<RelationTemplate> relationTemplateList = new ArrayList<>();
		for (String relationTemplateGuid : RELATIONTEMPLATE_GUID_MAP.keySet())
		{
			RelationTemplate template = RELATIONTEMPLATE_GUID_MAP.get(relationTemplateGuid);
			if (!isContainObsolete && !template.isValid())
			{
				continue;
			}

			relationTemplateList.add((RelationTemplate) template.clone());
		}
		return relationTemplateList;
	}

	protected List<BOMTemplate> listBOMTemplateByName(String templateName)
	{
		List<String> templateGuidList = BOMTEMPLATE_NAME_GUID_MAP.get(templateName);
		if (templateGuidList != null)
		{
			List<BOMTemplate> bomTemplateList = new ArrayList<>();
			templateGuidList.forEach(guid -> {
				BOMTemplate template = BOMTEMPLATE_GUID_MAP.get(guid);
				bomTemplateList.add(template);
			});
			return bomTemplateList;
		}
		return null;
	}

	protected List<RelationTemplate> listRelationTemplateByName(String templateName)
	{
		List<String> templateGuidList = RELATIONTEMPLATE_NAME_GUID_MAP.get(templateName);
		if (templateGuidList != null)
		{
			List<RelationTemplate> relationTemplateList = new ArrayList<>();
			templateGuidList.forEach(guid -> {
				RelationTemplate template = RELATIONTEMPLATE_GUID_MAP.get(guid);
				relationTemplateList.add(template);
			});
			return relationTemplateList;
		}
		return null;
	}

	protected void deleteBOMTemplateByName(String templateName)
	{
		List<BOMTemplate> templateList = this.listBOMTemplateByName(templateName);
		if (!SetUtils.isNullList(templateList))
		{
			templateList.forEach(template -> {
				deleteBOMTemplateEnd2(template.getGuid());
				deleteBOMReportTemplate(template.getGuid());
				BOMTEMPLATE_END2_GUID_MAP.remove(template.getGuid());
			});
			DataServer.getSystemDataService().deleteFromCache(BOMTemplateInfo.class, new FieldValueEqualsFilter<>(BOMTemplateInfo.NAME, templateName));
		}
	}

	protected void deleteBOMTemplateEnd2(String templateGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<BOMTemplateEnd2> bomTemplateEnd2List = sds.listFromCache(BOMTemplateEnd2.class, new FieldValueEqualsFilter<>(BOMTemplateEnd2.MASTER_FK, templateGuid));
		if (bomTemplateEnd2List != null)
		{
			bomTemplateEnd2List.forEach(bomTemplateEnd2 -> BOMTEMPLATE_END2_GUID_MAP.remove(bomTemplateEnd2.getGuid()));
		}
		sds.deleteFromCache(BOMTemplateEnd2.class, new FieldValueEqualsFilter<>(BOMTemplateEnd2.MASTER_FK, templateGuid));
	}

	protected void deleteBOMReportTemplate(String templateGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<BOMReportTemplate> bomReportTemplateList = sds.listFromCache(BOMReportTemplate.class, new FieldValueEqualsFilter<>(BOMReportTemplate.BOM_TEMPLATE_GUID, templateGuid));
		if (bomReportTemplateList != null)
		{
			bomReportTemplateList.forEach(bomReportTemplate -> BOMTEMPLATE_REPORT_GUID_MAP.remove(bomReportTemplate.getGuid()));
		}
		sds.deleteFromCache(BOMReportTemplate.class, new FieldValueEqualsFilter<>(BOMReportTemplate.BOM_TEMPLATE_GUID, templateGuid));
	}

	protected void deleteRelationTemplateByName(String templateName)
	{
		List<RelationTemplate> templateList = this.listRelationTemplateByName(templateName);
		if (!SetUtils.isNullList(templateList))
		{
			templateList.forEach(template -> {
				deleteRelationTemplateEnd2(template.getGuid());
				RELATIONTEMPLATE_GUID_MAP.remove(template.getGuid());
			});
			DataServer.getSystemDataService().deleteFromCache(RelationTemplateInfo.class, new FieldValueEqualsFilter<>(RelationTemplateInfo.NAME, templateName));
		}
	}

	protected void deleteRelationTemplateEnd2(String templateGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<RelationTemplateEnd2> relationTemplateEnd2List = sds.listFromCache(RelationTemplateEnd2.class,
				new FieldValueEqualsFilter<>(RelationTemplateEnd2.MASTERFK, templateGuid));
		if (relationTemplateEnd2List != null)
		{
			relationTemplateEnd2List.forEach(relationTemplateEnd2 -> RELATIONTEMPLATE_NED2_GUID_MAP.remove(relationTemplateEnd2.getGuid()));
		}
		sds.deleteFromCache(RelationTemplateEnd2.class, new FieldValueEqualsFilter<>(RelationTemplateEnd2.MASTERFK, templateGuid));
	}

	protected BOMTemplate getBOMTemplate(String templateGuid)
	{
		return BOMTEMPLATE_GUID_MAP.get(templateGuid);
	}

	protected BOMTemplate getBOMTemplateById(String templateId)
	{
		BOMTemplateInfo templateInfo = DataServer.getSystemDataService().findInCache(BOMTemplateInfo.class, new FieldValueEqualsFilter<>(BOMTemplateInfo.ID, templateId));
		return templateInfo == null ? null : BOMTEMPLATE_GUID_MAP.get(templateInfo.getGuid());
	}

	protected RelationTemplate getRelationTemplateById(String templateId)
	{
		RelationTemplateInfo templateInfo = DataServer.getSystemDataService().findInCache(RelationTemplateInfo.class,
				new FieldValueEqualsFilter<>(RelationTemplateInfo.ID, templateId));
		return templateInfo == null ? null : RELATIONTEMPLATE_GUID_MAP.get(templateInfo.getGuid());
	}

	public RelationTemplate getRelationTemplate(String templateGuid)
	{
		return RELATIONTEMPLATE_GUID_MAP.get(templateGuid);
	}

	private void loadBOMTemplate(BOMTemplate bomTemplate)
	{
		List<BOMTemplateEnd2> bomTemplateEnd2List = DataServer.getSystemDataService().listFromCache(BOMTemplateEnd2.class,
				new FieldValueEqualsFilter<>(BOMTemplateEnd2.MASTER_FK, bomTemplate.getGuid()));
		if (bomTemplateEnd2List != null)
		{
			bomTemplateEnd2List.forEach(templateEnd2 -> BOMTEMPLATE_END2_GUID_MAP.put(templateEnd2.getGuid(), templateEnd2));
		}
		bomTemplate.setBOMTemplateEnd2List(bomTemplateEnd2List);

		List<BOMReportTemplate> bomReportTemplateList = DataServer.getSystemDataService().listFromCache(BOMReportTemplate.class,
				new FieldValueEqualsFilter<>(BOMReportTemplate.BOM_TEMPLATE_GUID, bomTemplate.getGuid()));
		if (bomReportTemplateList != null)
		{
			bomReportTemplateList.forEach(reportTemplate -> BOMTEMPLATE_REPORT_GUID_MAP.put(reportTemplate.getGuid(), reportTemplate));
		}
		bomTemplate.setBOMReportTemplateList(bomReportTemplateList);
	}

	private void loadRelationTemplate(RelationTemplate relationTemplate)
	{
		List<RelationTemplateEnd2> relationTemplateEnd2List = DataServer.getSystemDataService().listFromCache(RelationTemplateEnd2.class,
				new FieldValueEqualsFilter<>(RelationTemplateEnd2.MASTERFK, relationTemplate.getGuid()));
		if (relationTemplateEnd2List != null)
		{
			relationTemplateEnd2List.forEach(templateEnd2 -> RELATIONTEMPLATE_NED2_GUID_MAP.put(templateEnd2.getGuid(), templateEnd2));
		}
		relationTemplate.setRelationTemplateEnd2List(relationTemplateEnd2List);
	}

	class TemplateCacheInfo extends AbstractCacheInfo
	{
		/**
		 *
		 */
		private static final long serialVersionUID = 2489402252979827577L;

		@Override
		public void register()
		{
			DynaObserverMediator mediator = DynaObserverMediator.getInstance();
			mediator.register(BOMTemplateInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<>()));
			mediator.register(BOMTemplateEnd2.class, new DynaCacheObserver<>(this, new CacheRefreshListener<>()));
			mediator.register(BOMReportTemplate.class, new DynaCacheObserver<>(this, new CacheRefreshListener<>()));
			mediator.register(RelationTemplateInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<>()));
			mediator.register(RelationTemplateEnd2.class, new DynaCacheObserver<>(this, new CacheRefreshListener<>()));
		}

		public <T extends SystemObject> void refreshCache(T data, String type) throws ServiceRequestException
		{
			if (data instanceof BOMTemplate || data instanceof BOMTemplateEnd2 || data instanceof BOMReportTemplate)
			{
				if (data instanceof BOMTemplate)
				{
					BOMTemplate bomTemplate = (BOMTemplate) data;
					if (CacheConstants.CHANGE_TYPE.INSERT.equals(type))
					{
						BOMTEMPLATE_GUID_MAP.put(bomTemplate.getGuid(), bomTemplate);
						BOMTEMPLATE_NAME_GUID_MAP.computeIfAbsent(bomTemplate.getName(), k -> new ArrayList<>()).add(bomTemplate.getGuid());
					}
					else if (CacheConstants.CHANGE_TYPE.UPDATE.equals(type))
					{
						BOMTEMPLATE_GUID_MAP.get(bomTemplate.getGuid()).putAll(bomTemplate);
					}
					else if (CacheConstants.CHANGE_TYPE.DELETE.equals(type))
					{
						BOMTEMPLATE_GUID_MAP.remove(bomTemplate.getGuid());
						BOMTEMPLATE_NAME_GUID_MAP.remove(bomTemplate.getName());
					}
				}
				else
				{
					BOMTemplate bomTemplate;
					if (data instanceof BOMTemplateEnd2)
					{
						BOMTemplateEnd2 bomTemplateEnd2 = (BOMTemplateEnd2) data;
						String bomTemplateGuid = bomTemplateEnd2.getMasterFK();
						bomTemplate = BOMTEMPLATE_GUID_MAP.get(bomTemplateGuid);
					}
					else
					{
						BOMReportTemplate reportTemplate = (BOMReportTemplate) data;
						String bomTemplateGuid = reportTemplate.getBOMTemplateGUID();
						bomTemplate = BOMTEMPLATE_GUID_MAP.get(bomTemplateGuid);
					}
					if (bomTemplate != null)
					{
						loadBOMTemplate(bomTemplate);
					}
				}
			}
			else if (data instanceof RelationTemplate || data instanceof RelationTemplateEnd2)
			{
				if (data instanceof RelationTemplate)
				{
					RelationTemplate relationTemplate = (RelationTemplate) data;
					if (CacheConstants.CHANGE_TYPE.INSERT.equals(type))
					{
						RELATIONTEMPLATE_GUID_MAP.put(relationTemplate.getGuid(), relationTemplate);
						RELATIONTEMPLATE_NAME_GUID_MAP.computeIfAbsent(relationTemplate.getName(), k -> new ArrayList<>()).add(relationTemplate.getGuid());
					}
					else if (CacheConstants.CHANGE_TYPE.UPDATE.equals(type))
					{
						RELATIONTEMPLATE_GUID_MAP.get(relationTemplate.getGuid()).putAll(relationTemplate);
					}
					else if (CacheConstants.CHANGE_TYPE.DELETE.equals(type))
					{
						RELATIONTEMPLATE_GUID_MAP.remove(relationTemplate.getGuid());
						RELATIONTEMPLATE_NAME_GUID_MAP.remove(relationTemplate.getName());
					}
				}
				else
				{
					RelationTemplateEnd2 relationTemplateEnd2 = (RelationTemplateEnd2) data;
					String relationTemplateGuid = relationTemplateEnd2.getMasterFK();
					RelationTemplate relationTemplate = RELATIONTEMPLATE_GUID_MAP.get(relationTemplateGuid);
					loadRelationTemplate(relationTemplate);
				}
			}
		}

		@Override
		public <T extends SystemObject> void addToCache(T data) throws ServiceRequestException
		{
			if (data instanceof BOMTemplateInfo || data instanceof BOMTemplateEnd2 || data instanceof BOMReportTemplate)
			{
				loadBOMTemplate();
			}
			else if (data instanceof RelationTemplateInfo || data instanceof RelationTemplateEnd2)
			{
				loadRelationTemplate();
			}
		}

		@Override
		public <T extends SystemObject> void removeFromCache(T data) throws ServiceRequestException
		{
			if (data instanceof BOMTemplateInfo || data instanceof BOMTemplateEnd2 || data instanceof BOMReportTemplate)
			{
				loadBOMTemplate();
			}
			else if (data instanceof RelationTemplateInfo || data instanceof RelationTemplateEnd2)
			{
				loadRelationTemplate();
			}
		}

		@Override
		public <T extends SystemObject> void updateToCache(T data) throws ServiceRequestException
		{
			if (data instanceof BOMTemplateInfo || data instanceof BOMTemplateEnd2 || data instanceof BOMReportTemplate)
			{
				loadBOMTemplate();
			}
			else if (data instanceof RelationTemplateInfo || data instanceof RelationTemplateEnd2)
			{
				loadRelationTemplate();
			}
		}
	}
}
