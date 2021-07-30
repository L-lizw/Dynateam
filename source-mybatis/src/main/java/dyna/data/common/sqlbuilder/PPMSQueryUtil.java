package dyna.data.common.sqlbuilder;

import dyna.common.SearchCondition;
import dyna.common.dto.TreeDataRelation;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.URIG;
import dyna.common.dto.aas.User;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.systemenum.ppms.OperationStateEnum;
import dyna.common.systemenum.ppms.SearchCategoryEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.WorkItemStateEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.model.codemodel.CodeModelService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.List;

public class PPMSQueryUtil
{
	private static String	PMTYPE_PREFIX_PROJ		= "PROJ_";
	private static String	PMTYPE_PREFIX_TASK		= "TASK_";
	private static String	PMTYPE_PREFIX_WORKLOAD	= "WORKLOAD_";
	private static String	PMTYPE_PREFIX_WORKITEM	= "WORKITEM_";

	public static void changeToPM(String mainTableAlias, SearchCategoryEnum sce, StringBuilder whereClauseSb, String userGuid, String groupGuid, CodeModelService codeService,
			List<SqlParamData> paramList)
	{
		if (sce == null || codeService == null)
		{
			return;
		}

		if (sce.name().startsWith(PMTYPE_PREFIX_PROJ))
		{
			changeToPMProject(mainTableAlias, sce, whereClauseSb, userGuid, groupGuid, codeService);
		}
		else if (sce.name().startsWith(PMTYPE_PREFIX_TASK))
		{
			changeToPMTask(mainTableAlias, sce, whereClauseSb, userGuid, codeService);
		}
		else if (sce.name().startsWith(PMTYPE_PREFIX_WORKLOAD))
		{
			changeToPMWorkload(mainTableAlias, sce, whereClauseSb, userGuid, codeService);
		}
		else if (sce.name().startsWith(PMTYPE_PREFIX_WORKITEM))
		{
			changeToPMWorkItem(mainTableAlias, sce, whereClauseSb, userGuid, codeService);
		}
	}

	// 判断任务创建者，项目经理，执行者或观察员与项目资源的关系（是否包含在项目资源中）
	private static void changeToPMProject(String mainTableAlias, SearchCategoryEnum sce, StringBuilder whereClauseSb, String userGuid, String groupGuid,
			CodeModelService codeService)
	{
		String CODE_MASTER_NAME_PROJECT = "ExecuteStatus";

		whereClauseSb.append(" (");

		// 我创建的项目
		StringBuilder createUserBuffer = new StringBuilder();
		createUserBuffer.append("    " + mainTableAlias + ".createuser = '").append(userGuid + "' ");

		// 我管理的项目
		StringBuffer managerBuffer = new StringBuffer();
		managerBuffer.append("    " + mainTableAlias + ".executor = '").append(userGuid + "' ");

		// 我在项目角色中
		StringBuffer teamMemberBuffer = new StringBuffer();
		teamMemberBuffer.append(" exists(select 1 ");
		teamMemberBuffer.append("   from sa_user         a,");
		teamMemberBuffer.append("        ppm_rolemembers b,");
		teamMemberBuffer.append("        ppm_projectrole c");
		teamMemberBuffer.append("  where a.guid = b.userguid");
		teamMemberBuffer.append("    and b.projectroleguid = c.guid");
		teamMemberBuffer.append("    and c.typeguid = " + mainTableAlias + ".guid");
		teamMemberBuffer.append("    and a.guid = '").append(userGuid + "') ");

		// 我是观察者的项目
		StringBuffer observerBuffer = new StringBuffer();
		observerBuffer.append(" exists(select 1 ");
		observerBuffer.append("   from sa_user         a,");
		observerBuffer.append("        ppm_rolemembers b,");
		observerBuffer.append("        ppm_projectrole c");
		observerBuffer.append("  where a.guid = b.userguid");
		observerBuffer.append("    and b.projectroleguid = c.guid");
		observerBuffer.append("    and c.typeguid = " + mainTableAlias + ".guid");
		observerBuffer.append("    and a.guid = '").append(userGuid + "' ");
		observerBuffer.append("    and c.roleid = '").append(PMConstans.PROJECT_OBSERVER_ROLE + "') ");

		// 责任是是我所在的组的成员
		StringBuffer executorInGroupBuffer = new StringBuffer();
		executorInGroupBuffer.append(" exists (select 1 ");
		executorInGroupBuffer.append("           from ma_treedata_relation r,");
		executorInGroupBuffer.append("                sa_role_group rig,");
		executorInGroupBuffer.append("                sa_grouprole_user gu");
		executorInGroupBuffer.append("          where r.subdataguid = rig.groupguid");
		executorInGroupBuffer.append("            and rig.guid = gu.rolegroupguid");
		executorInGroupBuffer.append("            and r.dataguid = '").append(groupGuid).append("'");
		executorInGroupBuffer.append("            and r.datatype = '").append(TreeDataRelation.DATATYPE_GROUP).append("'");
		executorInGroupBuffer.append("            and gu.userguid = " + mainTableAlias + ".executor)");

		// 项目状态
		// 初始化的项目
		StringBuffer initStatusBuffer = new StringBuffer();
		String iniStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.INI.getValue(), CODE_MASTER_NAME_PROJECT, codeService);
		initStatusBuffer.append(" " + mainTableAlias + ".executestatus = '").append(iniStatus).append("' ");

		// 进行中的项目
		StringBuffer runStatusBuffer = new StringBuffer();
		String runStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.RUN.getValue(), CODE_MASTER_NAME_PROJECT, codeService);
		runStatusBuffer.append("  " + mainTableAlias + ".executestatus = '").append(runStatus).append("' ");

		// 已完成的项目
		StringBuffer copStatusBuffer = new StringBuffer();
		String copStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.COP.getValue(), CODE_MASTER_NAME_PROJECT, codeService);
		copStatusBuffer.append("  " + mainTableAlias + ".executestatus = '").append(copStatus).append("' ");

		// 暂停的项目
		StringBuffer pusStatusBuffer = new StringBuffer();
		String pusStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.PUS.getValue(), CODE_MASTER_NAME_PROJECT, codeService);
		pusStatusBuffer.append("  " + mainTableAlias + ".executestatus = '").append(pusStatus).append("' ");

		// 中止的项目
		StringBuffer sspStatusBuffer = new StringBuffer();
		String sspStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.SSP.getValue(), CODE_MASTER_NAME_PROJECT, codeService);
		sspStatusBuffer.append("  " + mainTableAlias + ".executestatus = '").append(sspStatus).append("' ");

		// 我是创建者，项目团队的成员的项目
		StringBuffer creatorOrTeamMemberBuffer = new StringBuffer();
		creatorOrTeamMemberBuffer.append("(");
		creatorOrTeamMemberBuffer.append(createUserBuffer).append(" or ");
		creatorOrTeamMemberBuffer.append(teamMemberBuffer);
		creatorOrTeamMemberBuffer.append(")");

		if (sce == SearchCategoryEnum.PROJ_DEFAULT)
		{
			whereClauseSb.append(createUserBuffer);
			whereClauseSb.append(" or ");
			whereClauseSb.append(teamMemberBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_ALL_UP_TO_NOW)
		{
			whereClauseSb.append(creatorOrTeamMemberBuffer);
			whereClauseSb.append(" and (");
			whereClauseSb.append(initStatusBuffer).append(" or ");
			whereClauseSb.append(runStatusBuffer).append(" or ");
			whereClauseSb.append(pusStatusBuffer).append(")");
		}
		else if (sce == SearchCategoryEnum.PROJ_INIT)
		{
			whereClauseSb.append(creatorOrTeamMemberBuffer).append(" and ");
			whereClauseSb.append(initStatusBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_IN_PROCESS)
		{
			whereClauseSb.append(creatorOrTeamMemberBuffer).append(" and ");
			whereClauseSb.append(runStatusBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_COMPLETE)
		{
			whereClauseSb.append(creatorOrTeamMemberBuffer).append(" and ");
			whereClauseSb.append(copStatusBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_SUSPEND)
		{
			whereClauseSb.append(creatorOrTeamMemberBuffer).append(" and ");
			whereClauseSb.append(pusStatusBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_STOP)
		{
			whereClauseSb.append(creatorOrTeamMemberBuffer).append(" and ");
			whereClauseSb.append(sspStatusBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_PARTICIPATE)
		{
			whereClauseSb.append(teamMemberBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_CREATE)
		{
			whereClauseSb.append(createUserBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_MANAGE)
		{
			whereClauseSb.append(managerBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_OBSERVE)
		{
			whereClauseSb.append(observerBuffer);
		}
		else if (sce == SearchCategoryEnum.PROJ_GROUP_MANAGER)
		{
			whereClauseSb.append(executorInGroupBuffer);
		}
		whereClauseSb.append(")");
	}

	/**
	 * 判断任务执行者、任务创建者、项目经理与任务资源的关系（是否包含在任务资源中）
	 * 
	 * @param mainTableAlias
	 * @param sce
	 * @param whereClauseSb
	 * @param userGuid
	 * @param codeService
	 */
	private static void changeToPMTask(String mainTableAlias, SearchCategoryEnum sce, StringBuilder whereClauseSb, String userGuid, CodeModelService codeService)
	{
		String CODE_MASTER_NAME_TASK = "ExecuteStatus";

		whereClauseSb.append(" (");
		// 任务资源
		StringBuilder taskSourceBuffer = new StringBuilder();
		taskSourceBuffer.append(" exists (select 1 ");
		taskSourceBuffer.append("           from sa_user          a,");
		taskSourceBuffer.append("                ppm_taskresource b");
		taskSourceBuffer.append("          where a.guid = b.userguid");
		taskSourceBuffer.append("            and b.taskguid = " + mainTableAlias + ".guid");
		taskSourceBuffer.append("            and a.guid = '").append(userGuid + "')");

		// 创建者
		StringBuffer creatorBuffer = new StringBuffer();
		creatorBuffer.append("      " + mainTableAlias + ".createuser = '").append(userGuid + "' ");

		// 执行者
		StringBuffer executorBuffer = new StringBuffer();
		executorBuffer.append("      " + mainTableAlias + ".executor = '").append(userGuid + "' ");

		// 项目经理
		StringBuffer projectManagerBuffer = new StringBuffer();
		projectManagerBuffer.append("      exists (select 1 from PMPROJECT_0 p where " + mainTableAlias + ".ownerproject=p.guid and p.executor = '" + userGuid + "') ");

		// 任务状态
		StringBuffer initBuffer = new StringBuffer();
		String intStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.INI.getValue(), CODE_MASTER_NAME_TASK, codeService);
		initBuffer.append(" " + mainTableAlias + ".executestatus = '").append(intStatus).append("' ");

		StringBuffer runBuffer = new StringBuffer();
		String runStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.RUN.getValue(), CODE_MASTER_NAME_TASK, codeService);
		runBuffer.append(" " + mainTableAlias + ".executestatus = '").append(runStatus).append("' ");

		StringBuffer pusBuffer = new StringBuffer();
		String pusStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.PUS.getValue(), CODE_MASTER_NAME_TASK, codeService);
		pusBuffer.append(" " + mainTableAlias + ".executestatus = '").append(pusStatus).append("' ");

		StringBuffer copBuffer = new StringBuffer();
		String copStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.COP.getValue(), CODE_MASTER_NAME_TASK, codeService);
		copBuffer.append(" " + mainTableAlias + ".executestatus = '").append(copStatus).append("' ");

		StringBuffer sspBuffer = new StringBuffer();
		String sspStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.SSP.getValue(), CODE_MASTER_NAME_TASK, codeService);
		sspBuffer.append(" " + mainTableAlias + ".executestatus = '").append(sspStatus).append("' ");

		// 可启动的任务
		StringBuffer canStartBuffer = new StringBuffer();
		String canStartGuid = DataServicUtil.getGuidByCodeName(OperationStateEnum.START.getValue(), "OperationState", codeService);
		canStartBuffer.append(" " + mainTableAlias + ".operationstate = '").append(canStartGuid).append("' ");

		// 可完成的任务
		StringBuffer canCompleteBuffer = new StringBuffer();
		String canCompleteGuid = DataServicUtil.getGuidByCodeName(OperationStateEnum.COMPLETE.getValue(), "OperationState", codeService);
		canCompleteBuffer.append(" " + mainTableAlias + ".operationstate = '").append(canCompleteGuid).append("' ");

		// 待办任务（可启动的任务+可完成的任务）
		StringBuffer waitToDoBuffer = new StringBuffer();
		waitToDoBuffer.append(" (");
		waitToDoBuffer.append(canStartBuffer).append(" or ");
		waitToDoBuffer.append(canCompleteBuffer);
		waitToDoBuffer.append(")");

		// 审批中的任务
		StringBuffer appBuffer = new StringBuffer();
		String appStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.APP.getValue(), CODE_MASTER_NAME_TASK, codeService);
		appBuffer.append(" " + mainTableAlias + ".executestatus = '").append(appStatus).append("' ");

		if (sce == SearchCategoryEnum.TASK_DEFAULT)
		{
			whereClauseSb.append(taskSourceBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_ALL_UP_TO_NOW)
		{
			whereClauseSb.append(taskSourceBuffer);
			whereClauseSb.append(" and (");
			whereClauseSb.append(initBuffer).append(" or ");
			whereClauseSb.append(runBuffer).append(" or ");
			whereClauseSb.append(pusBuffer).append("or");
			whereClauseSb.append(appBuffer);
			whereClauseSb.append(")");
		}
		else if (sce == SearchCategoryEnum.TASK_PARTICIPATE)
		{
			whereClauseSb.append(taskSourceBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_IN_PROCESS)
		{
			whereClauseSb.append(taskSourceBuffer).append(" and ");
			whereClauseSb.append(runBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_COMPLETE)
		{
			whereClauseSb.append(taskSourceBuffer).append(" and ");
			whereClauseSb.append(copBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_SUSPEND)
		{
			whereClauseSb.append(taskSourceBuffer).append(" and ");
			whereClauseSb.append(pusBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_STOP)
		{
			whereClauseSb.append(taskSourceBuffer).append(" and ");
			whereClauseSb.append(sspBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_NOT_START)
		{
			whereClauseSb.append(taskSourceBuffer).append(" and ");
			whereClauseSb.append(initBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_RESPONSIBLE)
		{
			whereClauseSb.append(executorBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_CAN_START)
		{
			whereClauseSb.append(executorBuffer).append(" and ");
			whereClauseSb.append(canStartBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_CAN_COMPLETE)
		{
			whereClauseSb.append(executorBuffer).append(" and ");
			whereClauseSb.append(canCompleteBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_WAIT_TODO)
		{
			whereClauseSb.append(executorBuffer).append(" and ");
			whereClauseSb.append(waitToDoBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_APP)
		{
			whereClauseSb.append(executorBuffer).append(" and ");
			whereClauseSb.append(appBuffer);
		}
		else if (sce == SearchCategoryEnum.TASK_PROJECT_APP)
		{
			whereClauseSb.append(projectManagerBuffer);
			whereClauseSb.append(" and ");
			whereClauseSb.append(appBuffer);
		}
		whereClauseSb.append(")");
	}

	private static void changeToPMWorkItem(String mainTableAlias, SearchCategoryEnum sce, StringBuilder whereClauseSb, String userGuid, CodeModelService codeService)
	{
		String CODE_MASTER_NAME_WORKITEM = "WorkItemStatus";

		whereClauseSb.append(" (");
		// 工作项资源
		StringBuffer workitemSourceBuffer = new StringBuffer();
		workitemSourceBuffer.append(" exists (select 1 ");
		workitemSourceBuffer.append("           from sa_user          a,");
		workitemSourceBuffer.append("                ppm_taskresource b");
		workitemSourceBuffer.append("          where a.guid = b.userguid");
		workitemSourceBuffer.append("            and b.taskguid = " + mainTableAlias + ".guid");
		workitemSourceBuffer.append("            and a.guid = '").append(userGuid + "')");

		// 创建者
		StringBuffer creatorBuffer = new StringBuffer();
		creatorBuffer.append("      " + mainTableAlias + ".createuser = '").append(userGuid + "' ");

		// 责任人
		StringBuffer executorBuffer = new StringBuffer();
		executorBuffer.append("      " + mainTableAlias + ".executor = '").append(userGuid + "' ");

		// 任务状态
		// 未启动
		StringBuffer initBuffer = new StringBuffer();
		String intStatus = DataServicUtil.getGuidByCodeName(WorkItemStateEnum.NOTSTART.getValue(), CODE_MASTER_NAME_WORKITEM, codeService);
		initBuffer.append(" " + mainTableAlias + ".executestatus = '").append(intStatus).append("' ");

		// 进行中
		StringBuffer runBuffer = new StringBuffer();
		String runStatus = DataServicUtil.getGuidByCodeName(WorkItemStateEnum.INPROGRESS.getValue(), CODE_MASTER_NAME_WORKITEM, codeService);
		runBuffer.append(" " + mainTableAlias + ".executestatus = '").append(runStatus).append("' ");

		// 暂停
		StringBuffer pusBuffer = new StringBuffer();
		String pusStatus = DataServicUtil.getGuidByCodeName(WorkItemStateEnum.PAUSE.getValue(), CODE_MASTER_NAME_WORKITEM, codeService);
		pusBuffer.append(" " + mainTableAlias + ".executestatus = '").append(pusStatus).append("' ");

		// 完成
		StringBuffer copBuffer = new StringBuffer();
		String copStatus = DataServicUtil.getGuidByCodeName(WorkItemStateEnum.COMPLETED.getValue(), CODE_MASTER_NAME_WORKITEM, codeService);
		copBuffer.append(" " + mainTableAlias + ".executestatus = '").append(copStatus).append("' ");

		// 取消
		StringBuffer sspBuffer = new StringBuffer();
		String sspStatus = DataServicUtil.getGuidByCodeName(WorkItemStateEnum.CANCEL.getValue(), CODE_MASTER_NAME_WORKITEM, codeService);
		sspBuffer.append(" " + mainTableAlias + ".executestatus = '").append(sspStatus).append("' ");

		// 非“未指派”之外的所有状态
		StringBuffer notAssignedBuffer = new StringBuffer();
		String notAssignedStatus = DataServicUtil.getGuidByCodeName(WorkItemStateEnum.NOTASSIGNED.getValue(), CODE_MASTER_NAME_WORKITEM, codeService);
		notAssignedBuffer.append(" " + mainTableAlias + ".executestatus <> '").append(notAssignedStatus).append("' ");

		// 待完成（未启动+进行中）
		StringBuffer waitToDoBuffer = new StringBuffer();
		waitToDoBuffer.append(" (");
		waitToDoBuffer.append(initBuffer).append(" or ");
		waitToDoBuffer.append(runBuffer);
		waitToDoBuffer.append(") ");

		if (sce == SearchCategoryEnum.WORKITEM_DEFAULT)
		{
			whereClauseSb.append(workitemSourceBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_ALL_UP_TO_NOW)
		{
			whereClauseSb.append(workitemSourceBuffer);
			whereClauseSb.append(" and (");
			whereClauseSb.append(initBuffer).append(" or ");
			whereClauseSb.append(runBuffer).append(" or ");
			whereClauseSb.append(pusBuffer);
			whereClauseSb.append(")");
		}
		else if (sce == SearchCategoryEnum.WORKITEM_IN_PROCESS)
		{
			whereClauseSb.append(workitemSourceBuffer).append(" and ");
			whereClauseSb.append(runBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_COMPLETE)
		{
			whereClauseSb.append(workitemSourceBuffer).append(" and ");
			whereClauseSb.append(copBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_SUSPEND)
		{
			whereClauseSb.append(workitemSourceBuffer).append(" and ");
			whereClauseSb.append(pusBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_STOP)
		{
			whereClauseSb.append(workitemSourceBuffer).append(" and ");
			whereClauseSb.append(sspBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_NOT_START)
		{
			whereClauseSb.append(workitemSourceBuffer).append(" and ");
			whereClauseSb.append(initBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_RESPONSIBLE)
		{
			whereClauseSb.append(executorBuffer).append(" and ");
			whereClauseSb.append(notAssignedBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_PARTICIPATE)
		{
			whereClauseSb.append(workitemSourceBuffer).append(" and ");
			whereClauseSb.append(notAssignedBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_CREATE)
		{
			whereClauseSb.append(creatorBuffer);
		}
		else if (sce == SearchCategoryEnum.WORKITEM_WAIT_TODO)
		{
			whereClauseSb.append(executorBuffer).append(" and ");
			whereClauseSb.append(waitToDoBuffer);
		}
		whereClauseSb.append(")");
	}

	private static void changeToPMWorkload(String mainTableAlias, SearchCategoryEnum sce, StringBuilder whereClauseSb, String userGuid, CodeModelService codeService)
	{
		String CODE_MASTER_NAME_TASK = "ExecuteStatus";

		// 任务状态
		StringBuffer statusBuffer = new StringBuffer();
		String sspStatus = DataServicUtil.getGuidByCodeName(TaskStatusEnum.SSP.getValue(), CODE_MASTER_NAME_TASK, codeService);
		statusBuffer.append(" " + mainTableAlias + ".executestatus not in( '");
		statusBuffer.append(sspStatus).append("')");

		// 任务资源
		StringBuffer taskSourceBuffer = new StringBuffer();
		taskSourceBuffer.append(" exists (select 1 ");
		taskSourceBuffer.append("           from sa_user          a,");
		taskSourceBuffer.append("                ppm_taskresource b");
		taskSourceBuffer.append("          where a.guid = b.userguid");
		taskSourceBuffer.append("            and b.taskguid = " + mainTableAlias + ".guid");
		taskSourceBuffer.append("            and a.guid = '").append(userGuid + "')");

		whereClauseSb.append("(");
		whereClauseSb.append(taskSourceBuffer);
		if (sce == SearchCategoryEnum.WORKLOAD_PARTICIPATE_TASK)
		{
			whereClauseSb.append(" and ");
			// 已完成和(已删除)
			// 已取消之外的所有状态
			whereClauseSb.append(statusBuffer);
		}
		whereClauseSb.append(")");
	}

	/**
	 * 判断是否项目管理中的特殊查询，包括：参与项目、跟踪项目、参与任务
	 * 
	 * @param sc
	 * @return
	 */
	static boolean isPMQueryEnum(SearchCondition sc)
	{
		if (sc.getSearchCategory() == null)
		{
			return false;
		}

		for (SearchCategoryEnum sce : SearchCategoryEnum.values())
		{
			if (sce == sc.getSearchCategory())
			{
				return true;
			}
		}
		return false;
	}

	static User isSysObserver(String userGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<>(URIG.USER_GUID, userGuid));
		if (!SetUtils.isNullList(urigList))
		{
			for (URIG urig : urigList)
			{
				Group group = sds.get(Group.class, urig.getGroupGuid());
				Role role = sds.get(Role.class, urig.getRoleGuid());
				if (group != null && "receiver".equals(group.getName()) && role != null && "receiver".equals(role.getName()))
				{
					return sds.get(User.class, userGuid);
				}
			}
		}
		return null;
	}

}
