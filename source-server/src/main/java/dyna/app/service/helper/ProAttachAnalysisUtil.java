package dyna.app.service.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.sch.Scheduler;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dto.template.wft.WorkflowTemplateScopeRTInfo;
import dyna.common.dto.wf.ProcAttachSetting;
import dyna.common.dto.wf.WFRelationSet;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.BOMS;
import dyna.net.service.brs.EMM;

public class ProAttachAnalysisUtil
{
	private String								credential		= null;

	private boolean								checkAcl		= false;

	private EMM									emm				= null;

	private BOMS								boms			= null;

	private BOAS								boas			= null;

	private Map<String, AnalysisTask>			allRunTaskMap	= new HashMap<>();

	private Set<String>							distinctSet		= new HashSet<>();

	private List<FoundationObject>				returnList		= new LinkedList<>();

	private Scheduler							scheduler		= null;

	private List<WorkflowTemplateScopeRTInfo>	listScopeRT		= null;

	public ProAttachAnalysisUtil(String credential2, EMM emm2, BOMS boms2, BOAS boas2, Scheduler scheduler2)
	{
		this.credential = credential2;
		this.emm = emm2;
		this.boms = boms2;
		this.boas = boas2;
		this.scheduler = scheduler2;
	}

	public List<FoundationObject> calculateAttach(List<ObjectGuid> firstList, ProcAttachSetting settings, List<WorkflowTemplateScopeRTInfo> listScopeRT, boolean isCheckAcl)
	{
		this.checkAcl = isCheckAcl;

		this.listScopeRT = listScopeRT;

		boolean listBomAll = false;

		boolean listRelationAll = false;

		if (settings != null && settings.getRelationSetList() != null)
		{
			for (WFRelationSet info : settings.getRelationSetList())
			{
				if (info.isBOM())
				{
					listBomAll = (!"1".equals(info.getStrategy()));
				}
				else
				{
					listRelationAll = (!"1".equals(info.getStrategy()));
				}
			}
		}
		for (ObjectGuid og : firstList)
		{
			addTask(og, null, true, true);
		}
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
			Map<String, AnalysisTask> tempMap = new HashMap<>(allRunTaskMap);
			for (Entry<String, AnalysisTask> entry : tempMap.entrySet())
			{
				if (entry.getValue().isFinish())
				{
					allRunTaskMap.remove(entry.getKey());
					List<FoundationObject> end2List = entry.getValue().getBomEnd2List();
					if (end2List != null)
					{
						for (FoundationObject end2 : end2List)
						{
							this.addTask(end2.getObjectGuid(), end2, listBomAll, true);
						}
					}

					end2List = entry.getValue().getAssoEnd2List();
					if (end2List != null)
					{
						for (FoundationObject end2 : end2List)
						{
							this.addTask(end2.getObjectGuid(), end2, listBomAll, listRelationAll);
						}
					}
				}
			}
		}
		return returnList;
	}

	private void addTask(ObjectGuid og, FoundationObject obj, boolean listBom, boolean listRealation)
	{
		if (distinctSet.contains(og.getGuid()) == false)
		{
			distinctSet.add(og.getGuid());
			if (obj != null)
			{
				returnList.add(obj);
			}

			if (listBom || (listRealation && !SetUtils.isNullList(listScopeRT)))
			{
				AnalysisTask task = new AnalysisTask(credential, emm, boms, boas, checkAcl);
				task.setMainObject(og);
				task.setListBom(listBom);
				task.setListRealation(listRealation);
				task.setListScopeRT(listScopeRT);
				allRunTaskMap.put(og.getGuid(), task);
				scheduler.addTask(task);
			}
		}
	}
}

class AnalysisTask extends AbstractScheduledTask
{

	private String								credential		= null;

	private ObjectGuid							mainObjectGuid	= null;

	private boolean								listRealation	= false;

	private boolean								listBom			= false;

	private boolean								checkAcl		= false;

	private boolean								finish			= false;

	private List<WorkflowTemplateScopeRTInfo>	listScopeRT		= null;

	private List<FoundationObject>				bomEnd2List		= new LinkedList<>();

	private List<FoundationObject>				assoEnd2List	= new LinkedList<>();

	private EMM									emm				= null;

	private BOMS								boms			= null;

	private BOAS								boas			= null;

	private Set<String>							distinctSet		= new HashSet<>();

	public AnalysisTask(String credential2, EMM emm2, BOMS boms2, BOAS boas2, boolean checkAcl2)
	{
		this.credential = credential2;
		this.emm = emm2;
		this.boms = boms2;
		this.boas = boas2;
		this.checkAcl = checkAcl2;
	}

	@Override
	public void run()
	{
		if (listBom)
		{
			listbom();
		}
		if (listRealation)
		{
			listReation();
		}
		finish = true;
	}

	private void listReation()
	{
		if (!SetUtils.isNullList(listScopeRT))
		{
			for (WorkflowTemplateScopeRTInfo rt : listScopeRT)
			{
				try
				{
					RelationTemplateInfo relationTemplate = emm.getRelationTemplateById(rt.getTemplateID());
					if (relationTemplate != null)
					{
						relationTemplate = emm.getRelationTemplateByName(mainObjectGuid, relationTemplate.getName());
						if (relationTemplate != null)
						{
							ViewObject viewObject = boas.getRelationByEND1(mainObjectGuid, relationTemplate.getName());
							if (viewObject != null)
							{
								List<StructureObject> assoDataList = DataServer.getRelationService().listObjectOfRelation(viewObject.getObjectGuid(), relationTemplate.getGuid(),
										null, checkAcl, credential, null);
								if (!SetUtils.isNullList(assoDataList))
								{
									for (StructureObject assoObj : assoDataList)
									{
										if (assoObj.getEnd2ObjectGuid().getGuid() != null && !distinctSet.contains(assoObj.getEnd2ObjectGuid().getGuid()))
										{
											distinctSet.add(assoObj.getEnd2ObjectGuid().getGuid());
											assoEnd2List.add((FoundationObject) assoObj.get(ViewObject.PREFIX_END2));
										}
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	private void listbom()
	{
		try
		{
			List<BOMTemplateInfo> bomTempLateList = emm.listBOMTemplateByEND1(mainObjectGuid);
			if (bomTempLateList != null)
			{
				for (BOMTemplateInfo bomTemplate : bomTempLateList)
				{
					BOMView viewObject = boms.getBOMViewByEND1(mainObjectGuid, bomTemplate.getName());
					if (viewObject != null)
					{
						List<BOMStructure> bomList;
						bomList = DataServer.getRelationService().listBOMStructure(viewObject.getObjectGuid(), bomTemplate.getGuid(), null, null, checkAcl, credential);
						if (!SetUtils.isNullList(bomList))
						{
							for (BOMStructure bomstru : bomList)
							{
								FoundationObject obj = (FoundationObject) bomstru.get(ViewObject.PREFIX_END2);
								if (bomstru.getEnd2ObjectGuid().getGuid() != null && !distinctSet.contains(bomstru.getEnd2ObjectGuid().getGuid()))
								{
									distinctSet.add(bomstru.getEnd2ObjectGuid().getGuid());
									bomEnd2List.add(obj);
								}
							}
						}
					}
				}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<FoundationObject> getBomEnd2List()
	{
		return bomEnd2List;
	}

	public List<FoundationObject> getAssoEnd2List()
	{
		return assoEnd2List;
	}

	public void setMainObject(ObjectGuid mainObject)
	{
		this.mainObjectGuid = mainObject;
	}

	public void setListRealation(boolean listRealation)
	{
		this.listRealation = listRealation;
	}

	public void setListBom(boolean listBom)
	{
		this.listBom = listBom;
	}

	public void setListScopeRT(List<WorkflowTemplateScopeRTInfo> listScopeRT)
	{
		this.listScopeRT = listScopeRT;
	}

	public boolean isFinish()
	{
		return finish;
	}

}