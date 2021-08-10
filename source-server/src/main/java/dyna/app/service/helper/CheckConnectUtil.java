package dyna.app.service.helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.sch.Scheduler;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.StructureObject;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;

public class CheckConnectUtil
{
	private EMM								emm						= null;
	private String							sessionId				= null;
	private String							templateName			= null;
	private boolean							isBom					= false;
	private String							checkMasterGuid			= null;
	private boolean							isConnnect				= false;
	private Map<String, ObjectGuid>			allInstanceMasterMap	= new HashMap<>();
	private Scheduler						scheduler;
	private Map<String, CheckConnectTask>	allRunTaskMap			= new HashMap<>();

	public CheckConnectUtil(EMM emm, Scheduler scheduler, String sessionId, String templateName, boolean isBom)
	{
		this.scheduler = scheduler;
		this.emm = emm;
		this.sessionId = sessionId;
		this.templateName = templateName;
		this.isBom = isBom;
	}

	public boolean checkConntc(ObjectGuid og)
	{
		this.checkMasterGuid = "";
		this.add(og);
		this.checkMasterGuid = og.getMasterGuid();
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
			Map<String, CheckConnectTask> tempMap = new HashMap<>(allRunTaskMap);
			for (Entry<String, CheckConnectTask> entry : tempMap.entrySet())
			{
				if (entry.getValue().isFinish())
				{
					allRunTaskMap.remove(entry.getKey());
					List<ObjectGuid> end2List = entry.getValue().getReturnList();
					if (!isConnnect)
					{
						if (end2List != null)
						{
							for (ObjectGuid end2 : end2List)
							{
								this.add(end2);
								if (isConnnect)
								{
									break;
								}
							}
						}
					}
				}
			}
		}
		return isConnnect;
	}

	private void add(ObjectGuid og)
	{
		if (!isConnnect)
		{
			if (checkMasterGuid.equals(og.getMasterGuid()))
			{
				isConnnect = true;
			}
			if (!isConnnect)
			{
				if (!allInstanceMasterMap.containsKey(og.getMasterGuid()))
				{
					allInstanceMasterMap.put(og.getMasterGuid(), og);
					CheckConnectTask task = new CheckConnectTask(emm, sessionId, og, templateName, isBom);
					allRunTaskMap.put(og.getMasterGuid(), task);
					scheduler.addTask(task);
				}
			}
		}
	}
}

class CheckConnectTask extends AbstractScheduledTask
{
	private boolean		finish				= false;
	private EMM			emm					= null;
	private String		templateName		= null;
	private boolean		isBom				= false;
	private ObjectGuid	end1;
	private String		sessionId			= null;
	List<ObjectGuid>	end2ObjectGuidList	= new LinkedList<ObjectGuid>();

	public CheckConnectTask(EMM emm, String sessionId, ObjectGuid end1, String templateName, boolean isBom)
	{
		this.emm = emm;
		this.sessionId = sessionId;
		this.end1 = end1;
		this.templateName = templateName;
		this.isBom = isBom;
	}

	public List<ObjectGuid> getReturnList()
	{
		return end2ObjectGuidList;
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
			String templateId = null;
			String viewClassNameOrGuid = null;
			String struClassGuid = null;
			if (this.isBom)
			{
				BOMTemplateInfo template = this.emm.getBOMTemplateByName(end1, templateName);
				if (template == null)
				{
					return;
				}
				templateId = template.getId();
				struClassGuid = template.getStructureClassGuid();
				viewClassNameOrGuid = template.getViewClassGuid();
			}
			else
			{
				RelationTemplateInfo template = this.emm.getRelationTemplateByName(end1, templateName);
				if (template == null)
				{
					return;
				}
				templateId = template.getId();
				struClassGuid = template.getStructureClassGuid();
				viewClassNameOrGuid = template.getViewClassGuid();
			}
			end1.setIsMaster(true);
			List<StructureObject> list = DataServer.getRelationService().listSimpleStructureOfEnd1(end1, templateId, viewClassNameOrGuid, struClassGuid, sessionId);
			if (list != null)
			{
				for (StructureObject end2 : list)
				{
					end2ObjectGuidList.add(end2.getEnd2ObjectGuid());
				}
			}
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