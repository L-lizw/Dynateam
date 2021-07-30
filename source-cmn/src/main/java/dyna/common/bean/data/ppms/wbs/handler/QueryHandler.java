package dyna.common.bean.data.ppms.wbs.handler;

import java.util.List;

import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.DeliverableItem;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.systemenum.ppms.DataTypeEnum;

public interface QueryHandler extends Handler
{
	public FoundationObject getRootObject();

	public FoundationObject getObject(String wbsCode);

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getPMFoundationObject(dyna.common.bean.data.ObjectGuid)
	 */
	public FoundationObject getObject(ObjectGuid objectGuid);

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getChildList(dyna.common.bean.data.ObjectGuid)
	 */
	public List<FoundationObject> getChildList(ObjectGuid objectGuid);

	public List<FoundationObject> getAllChildList(ObjectGuid objectGuid);

	/*
	 * 查找所有前置任务
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getDependTaskList(dyna.common.bean.data.ObjectGuid)
	 */
	public List<TaskRelation> getPreTaskList(ObjectGuid objGuid);

	/*
	 * 
	 * 查找所有后置任务
	 * 
	 * @see dyna.common.bean.data.ppm.AbstractWBSContain#getForDependTaskList(dyna.common.bean.data.ObjectGuid)
	 */
	public List<TaskRelation> getPostTaskList(ObjectGuid objGuid);

	public List<RoleMembers> getRoleMemberList(String projectRoleGuid);

	public List<TaskMember> getTaskMemberList(String taskGuid);

	public TaskMember getTaskMemberByUser(String taskGuid, String userGuid);

	public TaskMember getMasterTaskMember(String taskGuid);

	public List<DeliverableItem> getDeliverableItemList(String taskGuid);

	public FoundationObject getParentTask(FoundationObject obj);

	public <T extends DynaObject> List<T> listAllObject(Class<T> clazz, DataTypeEnum typeEnum);

	public List<CheckpointConfig> getCheckPointConfigList();

}
