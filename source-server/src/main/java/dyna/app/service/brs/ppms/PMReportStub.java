/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PMTemplate
 * WangLHB May 9, 2012
 */
package dyna.app.service.brs.ppms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.data.ppms.RptDeliverable;
import dyna.common.bean.data.ppms.RptDeptStat;
import dyna.common.bean.data.ppms.RptManagerStat;
import dyna.common.bean.data.ppms.RptMilestone;
import dyna.common.bean.data.ppms.RptProject;
import dyna.common.bean.data.ppms.RptTask;
import dyna.common.bean.data.ppms.RptTaskDeptStat;
import dyna.common.bean.data.ppms.RptTaskExecStateStat;
import dyna.common.bean.data.ppms.RptTaskExecutorStat;
import dyna.common.bean.data.ppms.RptWorkItem;
import dyna.common.bean.data.ppms.RptWorkItemDeptStat;
import dyna.common.bean.data.ppms.RptWorkItemExecStateStat;
import dyna.common.bean.data.ppms.RptWorkItemExecutorStat;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.RIG;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author fanjq
 * 
 */
public class PMReportStub extends AbstractServiceStub<PPMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected PMReportStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	/**
	 * @param taskObjectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<RptProject> listRptProject(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			DSCommonService ds = DataServer.getDSCommonService();
			ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IProjectType, null);
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			map.put("PROJECTTABLENAME", ds.getTableName(classInfo.getName()));
			map.put("TABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			boolean systemReceiver = this.isSystemReceiver() || this.isAdmin();
			SystemDataService sds = DataServer.getSystemDataService();
			List<RptProject> dataList = null;
			int page = 999;
			int count = 0;
			if (map.get("CURRENTPAGE") != null)
			{
				page = ((Number) map.get("CURRENTPAGE")).intValue();
				if (page < 2)
				{
					Map<String, Object> countCondition = new HashMap<>(map);
					countCondition.remove("ROWSPERPAGE");
					countCondition.remove("CURRENTPAGE");
					if (systemReceiver)
					{
						countCondition.remove("USERGUID");
						dataList = sds.query(RptProject.class, countCondition, "receiverSelectCount");
					}
					else
					{
						countCondition.put("USERGUID", this.stubService.getOperatorGuid());
						dataList = sds.query(RptProject.class, countCondition, "selectCount");
					}
					count = SetUtils.isNullList(dataList) ? 0 : dataList.get(0).getRowCount();
				}
			}
			if (page > 1 || count > 0)
			{
				if (systemReceiver)
				{
					map.remove("USERGUID");
					dataList = sds.query(RptProject.class, map, "receiverSelect");
				}
				else
				{
					map.put("USERGUID", this.stubService.getOperatorGuid());
					dataList = sds.query(RptProject.class, map);
				}
			}
			else
			{
				dataList = null;
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptProject data : dataList)
				{
					data.put("ROWCOUNT$", count);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptDeptStat> listRptDeptStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo classInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			if (classInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();
			map.put("TABLENAME", ds.getTableName(classInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			List<RptDeptStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptDeptStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptDeptStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptDeptStat rptDeptStat : dataList)
				{
					this.decorate(rptDeptStat);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptManagerStat> listRptManagerStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			if (projectClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();
			map.put("CURRENTTIMESTR", new Date());
			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			List<RptManagerStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptManagerStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptManagerStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptManagerStat stat : dataList)
				{
					this.decorate(stat);

					String managerGuid = stat.getManagerGuid();
					User user = this.stubService.getAAS().getUser(managerGuid);
					stat.setManager(user != null ? user.getUserName() : null);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptMilestone> listRptMilestone(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			if (projectClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();
			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			List<RptMilestone> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptMilestone.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptMilestone.class, map);
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptDeliverable> listRptDeliverable(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			if (projectClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();
			map.put("CURRENTTIMESTR", new Date());
			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			List<RptDeliverable> dataList = null;
			String deliveryTableSql = this.getDeliveryTableSql(map);
			if (StringUtils.isNullString(deliveryTableSql))
			{
				return null;
			}
			int page = 999;
			int count = 0;
			boolean systemReceiver = this.isSystemReceiver() || this.isAdmin();
			if (map.get("CURRENTPAGE") != null)
			{
				page = ((Number) map.get("CURRENTPAGE")).intValue();
				if (page < 2)
				{
					Map<String, Object> countCondition = new HashMap<>(map);
					countCondition.remove("ROWSPERPAGE");
					countCondition.remove("CURRENTPAGE");
					if (systemReceiver)
					{
						countCondition.put("DELIVERTABLENAME", deliveryTableSql);
						dataList = sds.query(RptDeliverable.class, countCondition, "receiverSelectCount");
					}
					else
					{
						countCondition.put("USERGUID", this.stubService.getOperatorGuid());
						countCondition.put("DELIVERTABLENAME", deliveryTableSql);
						dataList = sds.query(RptDeliverable.class, countCondition, "selectCount");
					}
					count = SetUtils.isNullList(dataList) ? 0 : dataList.get(0).getRowCount();
				}
			}
			if (page > 1 || count > 0)
			{
				if (systemReceiver)
				{
					map.remove("USERGUID");
					map.put("DELIVERTABLENAME", deliveryTableSql);
					dataList = sds.query(RptDeliverable.class, map, "receiverSelect");
				}
				else
				{
					map.put("USERGUID", this.stubService.getOperatorGuid());
					map.put("DELIVERTABLENAME", deliveryTableSql);
					dataList = sds.query(RptDeliverable.class, map);
				}
			}
			else
			{
				dataList = null;
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptDeliverable data : dataList)
				{
					data.put("ROWCOUNT$", count);
				}
			}
			decodeRptDeliverable(dataList);
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private void decodeRptDeliverable(List<RptDeliverable> dataList) throws ServiceRequestException
	{
		if (dataList != null)
		{
			for (RptDeliverable info : dataList)
			{
				String classGuid = (String) info.get(RptDeliverable.CLASSGUID);
				if (!StringUtils.isNullString(classGuid))
				{
					ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(classGuid);
					if (classInfo != null)
					{
						info.setClassName(classInfo.getName());
					}
					BOInfo boinfo = this.stubService.getEMM().getCurrentBizObject(classGuid);
					if (boinfo != null)
					{
						info.setBOTitle(boinfo.getTitle());
					}
				}
				String classiFItemGuid = (String) info.get(RptDeliverable.CLASSIFICATIONGUID);
				if (!StringUtils.isNullString(classiFItemGuid))
				{
					CodeItemInfo itemInfo = this.stubService.getEMM().getCodeItem(classiFItemGuid);
					info.setClassification(itemInfo.getTitle());
				}
				String userGuid = (String) info.get(RptDeliverable.CREATEUSERGUID);
				if (!StringUtils.isNullString(userGuid))
				{
					User user = this.stubService.getAAS().getUser(userGuid);
					info.setCreateUser(user.getUserName());
				}
			}
		}
	}

	public List<RptTask> listRptTask(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo taskClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMTask, null);
			if (projectClassInfo == null || taskClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("TASKTABLENAME", ds.getTableName(taskClassInfo.getName()));
			map.put("STASKTYPE", this.stubService.getWBSStub().getCodeItemByEnum(TaskTypeEnum.SUMMARY).getGuid());
			map.put("CURRENTTIMESTR", new Date());
			List<RptTask> dataList = null;
			int page = 999;
			int count = 0;
			boolean systemReceiver = this.isSystemReceiver() || this.isAdmin();
			if (map.get("CURRENTPAGE") != null)
			{
				page = ((Number) map.get("CURRENTPAGE")).intValue();
				if (page < 2)
				{
					Map<String, Object> countCondition = new HashMap<>(map);
					countCondition.remove("ROWSPERPAGE");
					countCondition.remove("CURRENTPAGE");
					if (systemReceiver)
					{
						countCondition.remove("USERGUID");
						dataList = sds.query(RptTask.class, countCondition, "receiverSelectCount");
					}
					else
					{
						countCondition.put("USERGUID", this.stubService.getOperatorGuid());
						dataList = sds.query(RptTask.class, countCondition, "selectCount");
					}
					count = SetUtils.isNullList(dataList) ? 0 : dataList.get(0).getRowCount();
				}
			}
			if (page > 1 || count > 0)
			{
				if (systemReceiver)
				{
					map.remove("USERGUID");
					dataList = sds.query(RptTask.class, map, "receiverSelect");
				}
				else
				{
					map.put("USERGUID", this.stubService.getOperatorGuid());
					dataList = sds.query(RptTask.class, map);
				}
			}
			else
			{
				dataList = null;
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptTask data : dataList)
				{
					data.put("ROWCOUNT$", count);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptTaskExecutorStat> listRptTaskExecutorStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo taskClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMTask, null);
			if (projectClassInfo == null || taskClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("TASKTABLENAME", ds.getTableName(taskClassInfo.getName()));
			map.put("STASKTYPE", this.stubService.getWBSStub().getCodeItemByEnum(TaskTypeEnum.SUMMARY).getGuid());
			map.put("CURRENTTIMESTR", new Date());
			List<RptTaskExecutorStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptTaskExecutorStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptTaskExecutorStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptTaskExecutorStat stat : dataList)
				{
					this.decorate(stat);

					String executorGuid = stat.getExecutorGuid();
					User user = this.stubService.getAAS().getUser(executorGuid);
					stat.setExecutor(user != null ? user.getUserName() : null);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptTaskDeptStat> listRptTaskDeptStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo taskClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMTask, null);
			if (projectClassInfo == null || taskClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("TASKTABLENAME", ds.getTableName(taskClassInfo.getName()));
			map.put("STASKTYPE", this.stubService.getWBSStub().getCodeItemByEnum(TaskTypeEnum.SUMMARY).getGuid());
			map.put("CURRENTTIMESTR", new Date());
			List<RptTaskDeptStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptTaskDeptStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptTaskDeptStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptTaskDeptStat stat : dataList)
				{
					this.decorate(stat);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptTaskExecStateStat> listRptTaskExecStateStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo taskClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMTask, null);
			if (projectClassInfo == null || taskClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();
			map.put("CURRENTTIMESTR", new Date());
			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("TASKTABLENAME", ds.getTableName(taskClassInfo.getName()));
			map.put("STASKTYPE", this.stubService.getWBSStub().getCodeItemByEnum(TaskTypeEnum.SUMMARY).getGuid());
			List<RptTaskExecStateStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptTaskExecStateStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptTaskExecStateStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptTaskExecStateStat stat : dataList)
				{
					this.decorate(stat);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private boolean isSystemReceiver() throws ServiceRequestException
	{
		Role role = this.stubService.getAAS().getRoleById("receiver");
		Group group = this.stubService.getAAS().getGroupById("receiver");
		RIG rig = this.stubService.getAAS().getRIGByGroupAndRole(group.getGuid(), role.getGuid());
		if (rig != null && this.stubService.getAAS().isUserInRIG(rig.getGuid(), this.stubService.getUserSignature().getUserGuid()))
		{
			return true;
		}
		return false;
	}

	public List<RptWorkItem> listRptWorkItem(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo workItemClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMWorkItem, null);
			if (projectClassInfo == null || workItemClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("WORKITEMTABLENAME", ds.getTableName(workItemClassInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			List<RptWorkItem> dataList = null;
			boolean systemReceiver = this.isSystemReceiver() || this.isAdmin();
			int page = 999;
			int count = 0;
			if (map.get("CURRENTPAGE") != null)
			{
				page = ((Number) map.get("CURRENTPAGE")).intValue();
				if (page < 2)
				{
					Map<String, Object> countCondition = new HashMap<>(map);
					countCondition.remove("ROWSPERPAGE");
					countCondition.remove("CURRENTPAGE");
					if (systemReceiver)
					{
						countCondition.remove("USERGUID");
						dataList = sds.query(RptWorkItem.class, countCondition, "receiverSelectCount");
					}
					else
					{
						countCondition.put("USERGUID", this.stubService.getOperatorGuid());
						dataList = sds.query(RptWorkItem.class, countCondition, "selectCount");
					}
					count = SetUtils.isNullList(dataList) ? 0 : dataList.get(0).getRowCount();
				}
			}
			if (page > 1 || count > 0)
			{
				if (this.isSystemReceiver() || this.isAdmin())
				{
					map.remove("USERGUID");
					dataList = sds.query(RptWorkItem.class, map, "receiverSelect");
				}
				else
				{
					map.put("USERGUID", this.stubService.getOperatorGuid());
					dataList = sds.query(RptWorkItem.class, map);
				}
			}
			else
			{
				dataList = null;
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptWorkItem data : dataList)
				{
					data.put("ROWCOUNT$", count);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptWorkItemExecStateStat> listRptWorkItemExecStateStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo workItemClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMWorkItem, null);
			if (projectClassInfo == null || workItemClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("WORKITEMTABLENAME", ds.getTableName(workItemClassInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			List<RptWorkItemExecStateStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptWorkItemExecStateStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptWorkItemExecStateStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptWorkItemExecStateStat stat : dataList)
				{
					this.decorate(stat);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptWorkItemDeptStat> listRptWorkItemDeptStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo workItemClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMWorkItem, null);
			if (projectClassInfo == null || workItemClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("WORKITEMTABLENAME", ds.getTableName(workItemClassInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			map.put("STASKTYPE", this.stubService.getWBSStub().getCodeItemByEnum(TaskTypeEnum.SUMMARY).getGuid());
			List<RptWorkItemDeptStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptWorkItemDeptStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptWorkItemDeptStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptWorkItemDeptStat stat : dataList)
				{
					this.decorate(stat);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public List<RptWorkItemExecutorStat> listRptWorkItemExecutorStat(Map<String, Object> map) throws ServiceRequestException
	{
		try
		{
			ClassInfo projectClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMProject, null);
			ClassInfo workItemClassInfo = this.stubService.getEMM().getFirstLevelClassByInterface(ModelInterfaceEnum.IPMWorkItem, null);
			if (projectClassInfo == null || workItemClassInfo == null)
			{
				return null;
			}

			SystemDataService sds = DataServer.getSystemDataService();
			DSCommonService ds = DataServer.getDSCommonService();

			map.put("PROJECTTABLENAME", ds.getTableName(projectClassInfo.getName()));
			map.put("WORKITEMTABLENAME", ds.getTableName(workItemClassInfo.getName()));
			map.put("CURRENTTIMESTR", new Date());
			List<RptWorkItemExecutorStat> dataList = null;
			if (this.isSystemReceiver() || this.isAdmin())
			{
				map.remove("USERGUID");
				dataList = sds.query(RptWorkItemExecutorStat.class, map, "receiverSelect");
			}
			else
			{
				map.put("USERGUID", this.stubService.getOperatorGuid());
				dataList = sds.query(RptWorkItemExecutorStat.class, map);
			}
			if (!SetUtils.isNullList(dataList))
			{
				for (RptWorkItemExecutorStat stat : dataList)
				{
					this.decorate(stat);
					String executor = stat.getExecutorGuid();
					User user = this.stubService.getAAS().getUser(executor);
					stat.setExecutor(user != null ? user.getUserName() : null);
				}
			}
			return dataList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private boolean isAdmin() throws ServiceRequestException
	{
		Role role = this.stubService.getAAS().getRoleById("ADMINISTRATOR");
		Group group = this.stubService.getAAS().getGroupById("ADMINISTRATOR");
		RIG rig = this.stubService.getAAS().getRIGByGroupAndRole(group.getGuid(), role.getGuid());
		if (this.stubService.getAAS().isUserInRIG(rig.getGuid(), this.stubService.getUserSignature().getUserGuid()))
		{
			return true;
		}
		return false;
	}

	private String getDeliveryTableSql(Map<String, Object> map) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		DSCommonService ds = DataServer.getDSCommonService();
		List<RptDeliverable> list = sds.query(RptDeliverable.class, map, "selectClass");

		StringBuffer buffer = new StringBuffer();
		if (!SetUtils.isNullList(list))
		{
			for (RptDeliverable c : list)
			{
				String classGuid = (String) c.get("INSTANCECLASSGUID");
				String tableName = ds.getTableName(classGuid);
				if (buffer.length() > 0)
				{
					buffer.append(" union ");
				}
				buffer.append(" select a.guid, a.md_name, a.md_id, a.classification, a.classguid, a.createuser, a.createtime, a.status from " + tableName + " a ");
			}
		}
		else
		{
			return null;
		}
		return buffer.toString();
	}

	private void decorate(SystemObjectImpl data) throws ServiceRequestException
	{
		String dept = (String) data.get("OWNERGROUP");
		if (StringUtils.isGuid(dept))
		{
			Group group = this.stubService.getAAS().getGroup(dept);
			data.put("DEPT", group == null ? null : group.getName());
		}

		String executor = (String) data.get("EXECUTOR");
		if (StringUtils.isGuid(executor))
		{
			User user = this.stubService.getAAS().getUser(executor);
			data.put("EXECUTOR", user == null ? null : user.getName());
		}

		// rptProject
		if (data.get("COMPLETIONRATE") != null)
		{
			BigDecimal completionrate = (BigDecimal) data.get("COMPLETIONRATE");
			data.put("COMPLETIONRATE", String.valueOf(completionrate.setScale(2, RoundingMode.HALF_UP).doubleValue()));
		}

		if (data.get("SPI") != null)
		{
			BigDecimal spi = (BigDecimal) data.get("SPI");
			data.put("SPI", String.valueOf(spi.setScale(2, RoundingMode.HALF_UP).doubleValue()));
		}

		if (data.get("PLANSTARTTIME") != null)
		{
			Date planStartTime = (Date) data.get("PLANSTARTTIME");
			data.put("PLANSTARTTIME", DateFormat.parse(DateFormat.formatYMD(planStartTime)));
		}

		if (data.get("PLANFINISHTIME") != null)
		{
			Date planFinishTime = (Date) data.get("PLANFINISHTIME");
			data.put("PLANFINISHTIME", DateFormat.parse(DateFormat.formatYMD(planFinishTime)));
		}

		if (data.get("ACTUALSTARTTIME") != null)
		{
			Date actualStartTime = (Date) data.get("ACTUALSTARTTIME");
			data.put("ACTUALSTARTTIME", DateFormat.parse(DateFormat.formatYMD(actualStartTime)));
		}

		if (data.get("ACTUALFINISHTIME") != null)
		{
			Date actualFinishTime = (Date) data.get("ACTUALFINISHTIME");
			data.put("ACTUALFINISHTIME", DateFormat.parse(DateFormat.formatYMD(actualFinishTime)));
		}
		// rptMilestone
		if (data.get("TASKPLANSTARTTIME") != null)
		{
			Date taskPlanstartTime = (Date) data.get("TASKPLANSTARTTIME");
			data.put("TASKPLANSTARTTIME", DateFormat.parse(DateFormat.formatYMD(taskPlanstartTime)));
		}

		if (data.get("TASKPLANFINISHTIME") != null)
		{
			Date taskPlanFinishTime = (Date) data.get("TASKPLANFINISHTIME");
			data.put("TASKPLANFINISHTIME", DateFormat.parse(DateFormat.formatYMD(taskPlanFinishTime)));
		}

		if (data.get("TASKACTUALFINISHTIME") != null)
		{
			Date taskActualFinishTime = (Date) data.get("TASKACTUALFINISHTIME");
			data.put("TASKACTUALFINISHTIME", DateFormat.parse(DateFormat.formatYMD(taskActualFinishTime)));
		}

		data.put("NAME", data.get("MD_NAME"));
		data.put("ID", data.get("MD_ID"));
	}

	class CollectionSort implements Comparator<SystemObjectImpl>
	{
		private List<String>	sortFields	= null;
		private int				index		= 0;

		public CollectionSort(List<String> sortFields)
		{
			this.sortFields = sortFields;
		}

		@Override
		public int compare(SystemObjectImpl o1, SystemObjectImpl o2)
		{
			// TODO Auto-generated method stub
			String value1 = (String) o1.get(sortFields.get(index));
			String value2 = (String) o2.get(sortFields.get(index));
			if (StringUtils.isNullString(value1) && StringUtils.isNullString(value2))
			{
				if (index + 1 < sortFields.size())
				{
					index++;
					return compare(o1, o2);
				}
				else
				{
					return 0;
				}
			}
			else if (StringUtils.isNullString(value1) && !StringUtils.isNullString(value2))
			{
				return -1;
			}
			else if (!StringUtils.isNullString(value1) && StringUtils.isNullString(value2))
			{
				return 1;
			}
			else
			{
				if (value1.compareTo(value2) == 0)
				{
					if (index + 1 < sortFields.size())
					{
						index++;
						return compare(o1, o2);
					}
					else
					{
						return 0;
					}
				}
				else
				{
					return value1.compareTo(value2);
				}
			}
		}

	}
}
