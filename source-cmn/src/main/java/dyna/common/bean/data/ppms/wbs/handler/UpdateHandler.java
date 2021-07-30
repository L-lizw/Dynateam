package dyna.common.bean.data.ppms.wbs.handler;

import java.util.List;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.instancedomain.InstanceDomainBean;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.exception.ServiceRequestException;

public interface UpdateHandler extends Handler
{

	public void addTask(FoundationObject parent, FoundationObject foundationObject, int index) throws ServiceRequestException;

	public void deleteTask(FoundationObject foundationObject) throws ServiceRequestException;

	public void upgrade(FoundationObject preObject) throws ServiceRequestException;

	public void degrade(FoundationObject preObject) throws ServiceRequestException;

	public void exchangePosition(FoundationObject preObject, FoundationObject nextObject) throws ServiceRequestException;

	public void setTaskMilestone(FoundationObject object) throws ServiceRequestException;

	public void cancelTaskMilestone(FoundationObject object) throws ServiceRequestException;

	public void removeCheckPointConfig(CheckpointConfig checkPoint) throws ServiceRequestException;

	public void addCheckPointConfig(CheckpointConfig checkPoint) throws ServiceRequestException;

	public void removePreTask(TaskRelation taskRelation) throws ServiceRequestException;

	public void addPreTask(TaskRelation taskRelation) throws ServiceRequestException;

	public void addPreTask(TaskRelation taskRelation, boolean checkLoop) throws ServiceRequestException;

	public void addDeliverableItem(ObjectGuid taskObjectGuid, DeliverableItem item) throws ServiceRequestException;

	public void removeDeliverableItem(DeliverableItem item) throws ServiceRequestException;;

	public void addProjectRole(ProjectRole projectRole) throws ServiceRequestException;

	public void removeProjectRole(ProjectRole projectRole) throws ServiceRequestException;

	public void addRoleMember(ProjectRole projectRole, RoleMembers roleMember) throws ServiceRequestException;

	public void removeRoleMember(RoleMembers roleMember) throws ServiceRequestException;;

	public void addTaskMember(ObjectGuid taskObjectGuid, TaskMember taskMember) throws ServiceRequestException;

	public void removeTaskMember(TaskMember taskMember) throws ServiceRequestException;;

	public void reschedule(FoundationObject task, Object planStartTime1, Object planFinishTime1, double percent, int duration) throws ServiceRequestException;

	public void replacePerformer(RoleMembers oriRoleMemberP, RoleMembers newRoleMemberP) throws ServiceRequestException;

	public void updateTaskExcutor() throws ServiceRequestException;

	public void modifyPreTask(TaskRelation xTaskRelation, FoundationObject end2) throws ServiceRequestException;

	public void modifyPreTaskType(TaskRelation xTaskRelation, String type) throws ServiceRequestException;

	public void modifyPreTaskDelayTime(TaskRelation xTaskRelation, int value) throws ServiceRequestException;

	public void setPrepareFieldValue(FoundationObject fo, String fieldName, Object value) throws ServiceRequestException;

	public InstanceDomainBean copyInstanceDomain(FoundationObject toProject, String classGuid, String copyType, boolean isCopyM, List<CheckpointConfig> checkpointConfigList);

	public void updatePersonInCharge(TaskMember taskMember) throws ServiceRequestException;

	/**
	 * 更改项目执行人
	 * 
	 * @param user
	 * @throws ServiceRequestException
	 */
	public void conversionManager(User user, Group group) throws ServiceRequestException;

}
