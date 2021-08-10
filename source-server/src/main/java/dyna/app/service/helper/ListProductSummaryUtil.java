package dyna.app.service.helper;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.sch.Scheduler;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.EMM;
//import org.apache.ftpserver.util.RegularExpr;

import java.util.*;
import java.util.Map.Entry;

public class ListProductSummaryUtil implements Comparator<FoundationObject>
{
	private BOMS							boms						= null;
	private BOAS							boas						= null;
	private EMM								emm							= null;
	private String							assoTemplateName			= null;
	private SearchCondition					bomEnd2SearchCondition;
	private SearchCondition					assoSearchCondition;
	private SearchCondition					assoEnd2SearchCondition;
	private SearchCondition					summarySearchCondition;
	private DataRule						dataRule;
	private Map<String, ObjectGuid>			allInstanceMasterMap		= new HashMap<>();
	private Scheduler						scheduler					= null;
	private Map<String, Object>				allRunTaskMap				= new HashMap<>();
	private Map<String, FoundationObject>	returnMap					= new HashMap<>();
	private List<String>					filterEnd2Class				= new LinkedList<>();
	private List<String>					filterEnd2Classification	= new LinkedList<>();
//	private RegularExpr						keyFielter;

	public ListProductSummaryUtil(BOMS boms, BOAS boas, EMM emm, Scheduler scheduler)
	{
		this.boms = boms;
		this.boas = boas;
		this.emm = emm;
		this.scheduler = scheduler;
	}

	public List<FoundationObject> listProductSummaryObject(ObjectGuid end1, String templateName, SearchCondition searchCondition, DataRule dataRule) throws ServiceRequestException
	{
		this.assoTemplateName = templateName;
		this.summarySearchCondition = searchCondition;
		List<RelationTemplateInfo> assoTempList = this.emm.listRelationTemplateByName(templateName, false);
		if (SetUtils.isNullList(assoTempList))
		{
			return null;
		}
		assoSearchCondition = SearchConditionFactory.createSearchCondition4Class(assoTempList.get(0).getStructureClassName(), null, true);
		assoEnd2SearchCondition = summarySearchCondition;
		ClassInfo firstLevelClassByItem = this.emm.getFirstLevelClassByItem();
		if (firstLevelClassByItem == null)
		{
			return null;
		}
		String itemClassName = firstLevelClassByItem.getName();
		bomEnd2SearchCondition = SearchConditionFactory.createSearchCondition4Class(itemClassName, null, true);
		this.initResultFilter();
		this.dataRule = dataRule;
		this.add(end1);
		while (allRunTaskMap.size() > 0)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			Map<String, Object> tempMap = new HashMap<>(allRunTaskMap);
			for (Entry<String, Object> entry : tempMap.entrySet())
			{
				if (entry.getValue() instanceof ListBOMTask)
				{
					if (((ListBOMTask) entry.getValue()).isFinish())
					{
						allRunTaskMap.remove(entry.getKey());
						List<BOMStructure> end2List = ((ListBOMTask) entry.getValue()).getReturnList();
						if (end2List != null)
						{
							for (BOMStructure end2 : end2List)
							{
								this.add(end2.getEnd2ObjectGuid());
							}
						}
					}
				}
				else
				{
					if (((ListRelationTask) entry.getValue()).isFinish())
					{
						allRunTaskMap.remove(entry.getKey());
						List<StructureObject> end2List = ((ListRelationTask) entry.getValue()).getReturnList();
						if (end2List != null)
						{
							for (StructureObject end2 : end2List)
							{
								this.processsResult(end2.getEnd2UIObject());
							}
						}
					}

				}
			}
		}

		ArrayList<FoundationObject> arrayList = new ArrayList<>(returnMap.values());
		List<Map<String, Boolean>> orderMapList = this.summarySearchCondition.getOrderMapList();
		if (!SetUtils.isNullList(orderMapList))
			;
		{
			Collections.sort(arrayList, this);
		}

		return arrayList;
	}

	@Override
	public int compare(FoundationObject o1, FoundationObject o2)
	{
		if (o1 == null)
		{
			return o2 == null ? 0 : -1;
		}
		if (o2 == null)
		{
			return 1;
		}
		List<Map<String, Boolean>> orderMapList = this.summarySearchCondition.getOrderMapList();
		if (!SetUtils.isNullList(orderMapList))
		{
			for (Map<String, Boolean> sortb : orderMapList)
			{
				if (!sortb.isEmpty())
				{
					String fieldName = sortb.keySet().iterator().next();
					String value1 = (String) o1.get(fieldName);
					String value2 = (String) o1.get(fieldName);
					if (value1 == null)
					{
						if (value2 != null)
						{
							return -1;
						}
					}
					else if (value2 == null)
					{
						return 1;
					}
					else
					{
						int value = value1.compareTo(value2);
						if (value != 0)
						{
							return value;
						}
					}
				}
			}
		}
		return 0;
	}

	private void initResultFilter() throws ServiceRequestException
	{
		if (!StringUtils.isNullString(this.summarySearchCondition.getObjectGuid().getClassName()))
		{
			List<ClassInfo> list = emm.listAllSubClassInfoOnlyLeaf(null, this.summarySearchCondition.getObjectGuid().getClassName());
			if (list != null)
			{
				for (ClassInfo info : list)
				{
					this.filterEnd2Class.add(info.getGuid());
				}
			}
		}
		if (!StringUtils.isNullString(this.summarySearchCondition.getClassification()))
		{
			List<CodeItemInfo> list = emm.listLeafCodeItemInfoByDatail(this.summarySearchCondition.getClassification());
			if (list != null)
			{
				for (CodeItemInfo info : list)
				{
					this.filterEnd2Classification.add(info.getGuid());
				}
			}
		}
		if (!StringUtils.isNullString(this.summarySearchCondition.getSearchValue()))
		{
//			this.keyFielter = new RegularExpr(this.summarySearchCondition.getSearchValue());
		}
	}

	private void processsResult(FoundationObject end2uiObject)
	{
		if (!SetUtils.isNullList(filterEnd2Class) && filterEnd2Class.contains(end2uiObject.getObjectGuid().getClassGuid()) == false)
		{
			return;
		}
		if (!SetUtils.isNullList(filterEnd2Classification) && filterEnd2Classification.contains(end2uiObject.getClassificationGuid()) == false)
		{
			return;
		}
//		if (keyFielter != null)
//		{
//			String id = end2uiObject.getId();
//			String name = end2uiObject.getName();
//			String alterId = end2uiObject.getAlterId();
//			id = (id == null ? "" : id);
//			name = (name == null ? "" : name);
//			alterId = (alterId == null ? "" : alterId);
//			if (keyFielter.isMatch(id) || keyFielter.isMatch(name) || keyFielter.isMatch(alterId))
//			{
//
//			}
//			else
//			{
//				return;
//			}
//		}
		this.returnMap.put(end2uiObject.getObjectGuid().getGuid(), end2uiObject);
	}

	private void add(ObjectGuid og)
	{
		if (!allInstanceMasterMap.containsKey(og.getGuid()))
		{
			allInstanceMasterMap.put(og.getGuid(), og);
			ListBOMTask task = new ListBOMTask(boms, emm, og, bomEnd2SearchCondition, dataRule);
			allRunTaskMap.put(og.getMasterGuid(), task);
			scheduler.addTask(task);
			ListRelationTask task2 = new ListRelationTask(boas, og, assoTemplateName, assoSearchCondition, assoEnd2SearchCondition, dataRule);
			allRunTaskMap.put(og.getGuid(), task2);
			scheduler.addTask(task2);
		}
	}

	class ListBOMTask extends AbstractScheduledTask
	{
		private boolean				finish				= false;
		private BOMS				boms				= null;
		private EMM					emm					= null;
		private ObjectGuid			end1				= null;
		private SearchCondition		end2SearchCondition	= null;
		private DataRule			dataRule			= null;
		private List<BOMStructure>	bomStruList			= new LinkedList<>();

		public ListBOMTask(BOMS boms, EMM emm, ObjectGuid end1, SearchCondition end2SearchCondition, DataRule dataRule)
		{
			this.boms = boms;
			this.emm = emm;
			this.end1 = end1;
			this.end2SearchCondition = end2SearchCondition;
			this.dataRule = dataRule;
		}

		public ObjectGuid getEnd1()
		{
			return end1;
		}

		public List<BOMStructure> getReturnList()
		{
			return bomStruList;
		}

		public boolean isFinish()
		{
			return finish;
		}

		@Override
		public void run()
		{
			try
			{
				List<BOMTemplateInfo> bomtempList = emm.listBOMTemplateByEND1(end1);
				if (bomtempList != null)
				{
					for (BOMTemplateInfo info : bomtempList)
					{
						SearchCondition createSearchCondition4Class = SearchConditionFactory.createSearchCondition4Class(info.getStructureClassName(), null, true);
						List<BOMStructure> tempbomStruList = boms.listBOM(end1, info.getName(), createSearchCondition4Class, end2SearchCondition, dataRule);
						if (tempbomStruList != null)
						{
							bomStruList.addAll(tempbomStruList);
						}
					}
				}
			}
			catch (ServiceRequestException e)
			{
				DynaLogger.error(e.getMessage(), e);
			}
			finally
			{
				finish = true;
			}
		}

	}

	class ListRelationTask extends AbstractScheduledTask
	{
		private boolean					finish				= false;
		private BOAS					boas				= null;
		private String					templateName		= null;
		private ObjectGuid				end1				= null;
		private SearchCondition			searchCondition		= null;
		private SearchCondition			end2SearchCondition	= null;
		private DataRule				dataRule			= null;
		private List<StructureObject>	assoStruList		= null;

		public ListRelationTask(BOAS boas, ObjectGuid end1, String templateName, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
		{
			this.boas = boas;
			this.end1 = end1;
			this.templateName = templateName;
			this.searchCondition = searchCondition;
			this.end2SearchCondition = end2SearchCondition;
			this.dataRule = dataRule;
		}

		public ObjectGuid getEnd1()
		{
			return end1;
		}

		public List<StructureObject> getReturnList()
		{
			return assoStruList;
		}

		public boolean isFinish()
		{
			return finish;
		}

		@Override
		public void run()
		{
			try
			{
				assoStruList = boas.listObjectOfRelation(end1, templateName, searchCondition, end2SearchCondition, dataRule);
			}
			catch (ServiceRequestException e)
			{
				DynaLogger.error(e.getMessage(), e);
			}
			finally
			{
				finish = true;
			}
		}

	}

}
