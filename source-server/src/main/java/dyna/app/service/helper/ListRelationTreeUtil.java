package dyna.app.service.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.sch.Scheduler;
import dyna.app.service.brs.bom.BOMSImpl;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.dto.DataRule;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.net.service.brs.BOMS;

public class ListRelationTreeUtil
{
	private BOMS							boms					= null;
	private String							templateName			= null;
	SearchCondition							searchCondition;
	SearchCondition							end2SearchCondition;
	DataRule								dataRule;
	private Map<String, ObjectGuid>			allInstanceMasterMap	= new HashMap<>();
	private Scheduler						scheduler;
	private Map<String, ListBOMTask>		allRunTaskMap			= new HashMap<>();
	private Map<String, List<BOMStructure>>	returnMap				= new HashMap<String, List<BOMStructure>>();
	private int								level					= 0;

	public ListRelationTreeUtil(BOMSImpl stubService, Scheduler scheduler)
	{
		this.boms = stubService;
		this.scheduler = scheduler;
	}

	public Map<String, List<BOMStructure>> listBOMForTree(ObjectGuid end1, String templateName, SearchCondition searchCondition, SearchCondition end2SearchCondition,
			DataRule dataRule, int level1)
	{
		this.level = level1;
		this.templateName = templateName;
		this.searchCondition = searchCondition;
		this.end2SearchCondition = end2SearchCondition;
		this.dataRule = dataRule;
		this.add(0, end1);
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
			Map<String, ListBOMTask> tempMap = new HashMap<>(allRunTaskMap);
			for (Entry<String, ListBOMTask> entry : tempMap.entrySet())
			{
				if (entry.getValue().isFinish())
				{
					allRunTaskMap.remove(entry.getKey());
					List<BOMStructure> end2List = entry.getValue().getReturnList();
					returnMap.put(entry.getValue().getEnd1().getGuid(), end2List);
					int level2 = entry.getValue().getLevel() + 1;
					if (level < 0 || level2 < level)
					{
						if (end2List != null)
						{
							for (BOMStructure end2 : end2List)
							{
								this.add(level2, end2.getEnd2ObjectGuid());
							}
						}
					}
				}
			}
		}
		return returnMap;
	}

	private void add(int level3, ObjectGuid og)
	{
		if (!allInstanceMasterMap.containsKey(og.getGuid()))
		{
			allInstanceMasterMap.put(og.getGuid(), og);
			ListBOMTask task = new ListBOMTask(level3, boms, og, templateName, searchCondition, end2SearchCondition, dataRule);
			allRunTaskMap.put(og.getMasterGuid(), task);
			scheduler.addTask(task);
		}
	}

	class ListBOMTask extends AbstractScheduledTask
	{
		private boolean				finish				= false;
		private BOMS				boms				= null;
		private String				templateName		= null;
		private ObjectGuid			end1				= null;
		private SearchCondition		searchCondition		= null;
		private SearchCondition		end2SearchCondition	= null;
		private DataRule			dataRule			= null;
		private List<BOMStructure>	bomStruList			= null;
		private int					level				= 0;

		public ListBOMTask(int level, BOMS boms, ObjectGuid end1, String templateName, SearchCondition searchCondition, SearchCondition end2SearchCondition, DataRule dataRule)
		{
			this.level = level;
			this.boms = boms;
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

		public List<BOMStructure> getReturnList()
		{
			return bomStruList;
		}

		public boolean isFinish()
		{
			return finish;
		}

		public int getLevel()
		{
			return level;
		}

		@Override
		public void run()
		{
			try
			{
				bomStruList = boms.listBOM(end1, templateName, searchCondition, end2SearchCondition, dataRule);
			}
			catch (ServiceRequestException e)
			{
				DynaLogger.debug(e.getMessage(), e);
			}
			finally
			{
				finish = true;
			}
		}

	}
}
