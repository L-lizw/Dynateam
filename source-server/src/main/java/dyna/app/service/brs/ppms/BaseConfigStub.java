/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理基本配置
 * wanglhb 2013-10-21
 */
package dyna.app.service.brs.ppms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.bean.data.ppms.LaborHourConfig;
import dyna.common.bean.data.ppms.PPMConstans;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.ppms.CodeNameEnum;
import dyna.common.systemenum.ppms.DurationUnitEnum;
import dyna.common.systemenum.ppms.ProjectStatusEnum;
import dyna.common.systemenum.ppms.TaskDependEnum;
import dyna.common.systemenum.ppms.TaskStatusEnum;
import dyna.common.systemenum.ppms.TaskTypeEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author WangLHB
 * 
 */
public class BaseConfigStub extends AbstractServiceStub<PPMSImpl>
{

	protected BaseConfigStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	protected List<FoundationObject> listProjectType() throws ServiceRequestException
	{
		List<ClassInfo> projectTypeClassList = this.stubService.getEMM().listClassByInterface(ModelInterfaceEnum.IProjectType);
		ClassInfo classInfo = projectTypeClassList.get(0);
		if (classInfo != null)
		{
			SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(classInfo.getName(), null, true);
			searchCondition.setPageNum(1);
			searchCondition.setPageSize(SearchCondition.MAX_PAGE_SIZE);
			searchCondition.addOrder(SystemClassFieldEnum.CREATETIME, true);
			this.putAllClassFieldToSearch(classInfo.getName(), searchCondition);
			return ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
		}
		return null;
	}

	/**
	 * 
	 * @param name
	 * @param searchCondition
	 * @throws ServiceRequestException
	 */
	public void putAllClassFieldToSearch(String name, SearchCondition searchCondition) throws ServiceRequestException
	{
		if (!StringUtils.isNullString(name))
		{
			List<ClassField> listField = this.stubService.getEMM().listFieldOfClass(name);
			if (!SetUtils.isNullList(listField) && searchCondition != null)
			{
				for (ClassField field : listField)
				{
					searchCondition.addResultField(field.getName());
				}
			}
		}

	}

	protected FoundationObject saveProjectType(FoundationObject projectType) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		projectType.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		if (!StringUtils.isGuid(projectType.getGuid()))
		{
			projectType.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}
		try
		{
			FoundationObject fo = null;
			if (projectType.getObjectGuid() != null && projectType.getObjectGuid().getGuid() != null)
			{
				fo = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().saveObject(projectType, false, false, false, null, true, false, true);
			}
			else
			{
				fo = ((BOASImpl) this.stubService.getBOAS()).getFSaverStub().createObject(projectType, null, false, false);

			}
			return fo;
		}

		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected FoundationObject getProjectType(String projectTypetGuid, String classGuid) throws ServiceRequestException
	{
		return ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObjectByGuid(new ObjectGuid(classGuid, null, projectTypetGuid, null), false);
	}

	protected void delProjectType(ObjectGuid objectGuid) throws ServiceRequestException
	{
		if (objectGuid != null && objectGuid.getGuid() != null)
		{
			boolean canDelete = true;
			BOInfo pmProjectBoInfo = this.stubService.getProjectStub().getPMProjectBoInfo();
			if (pmProjectBoInfo != null)
			{
				SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(pmProjectBoInfo.getClassName(), null, false);
				searchCondition.addFilter(PPMFoundationObjectUtil.PROJECTTYPE, objectGuid, OperateSignEnum.EQUALS);
				searchCondition.setPageSize(1);
				List<FoundationObject> list = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
				if (!SetUtils.isNullList(list))
				{
					canDelete = false;
				}
			}

			if (canDelete)
			{
				pmProjectBoInfo = this.stubService.getProjectStub().getPMProjectTemplateBoInfo();
				if (pmProjectBoInfo != null)
				{
					SearchCondition searchCondition = SearchConditionFactory.createSearchCondition4Class(pmProjectBoInfo.getClassName(), null, false);
					searchCondition.addFilter(PPMFoundationObjectUtil.PROJECTTYPE, objectGuid, OperateSignEnum.EQUALS);
					searchCondition.setPageSize(1);
					List<FoundationObject> list = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().listObject(searchCondition, false);
					if (!SetUtils.isNullList(list))
					{
						canDelete = false;
					}
				}
			}

			if (!canDelete)
			{
				throw new ServiceRequestException("ID_APP_PM_MILESTONE_NOT_DELETE", "project type is not deleted");
			}

			// 通过项目类型删除关卡设置
			this.deleCheckPointByTypeGuid(objectGuid.getGuid());
			((BOASImpl) this.stubService.getBOAS()).getFoundationStub().deleteFoundationObject(objectGuid.getGuid(), objectGuid.getClassGuid(), false);
		}
	}

	/**
	 * 通过项目类型删除关卡设置
	 * 
	 * @param guid
	 * @throws ServiceRequestException
	 */
	private void deleCheckPointByTypeGuid(String projectTypeGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(projectTypeGuid))
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(CheckpointConfig.TYPEGUID, projectTypeGuid);
			filter.put(CheckpointConfig.TYPE, "1");
			try
			{
				List<CheckpointConfig> checkpointList = this.listCheckpointConfigByMileStoneGuid(projectTypeGuid);
				if (!SetUtils.isNullList(checkpointList))
				{
					sds.delete(CheckpointConfig.class, filter, "delete");
				}
			}
			catch (DynaDataException e)
			{
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
		}

	}

	public ProjectRole getProjectTypeRole(String projectRoleGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, projectRoleGuid);
		try
		{
			List<ProjectRole> projectRoleList = sds.query(ProjectRole.class, filter);
			if (!SetUtils.isNullList(projectRoleList))
			{
				return projectRoleList.get(0);
			}
			return null;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void delProjectRole(String projectRoleGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, projectRoleGuid);
			ProjectRole role = this.getProjectTypeRole(projectRoleGuid);
			if (PMConstans.PROJECT_MANAGER_ROLE.equals(role.getRoleId()) || PMConstans.PROJECT_OBSERVER_ROLE.equals(role.getRoleId()))
			{
				throw new ServiceRequestException("ID_APP_PPMS_BASECONFIGSTUB_DELETEROLE_ERROR", "the default role not allow delete!");
			}
			sds.delete(ProjectRole.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public void copyCheckpointConfigByMileStone(String srcGuid, String trgGuid, String type) throws ServiceRequestException
	{
		List<CheckpointConfig> listCheckpoint = this.stubService.getBaseConfigStub().listCheckpointConfigByMileStoneGuid(srcGuid);
		if (!SetUtils.isNullList(listCheckpoint))
		{
			this.saveCheckpoint("2", trgGuid, listCheckpoint);
		}
	}

	public List<CheckpointConfig> listCheckpointConfigByMileStoneGuid(String mileStoneGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(CheckpointConfig.TYPEGUID, mileStoneGuid);
		try
		{
			List<CheckpointConfig> finalList = new ArrayList<CheckpointConfig>();
			List<CheckpointConfig> classOfList = sds.query(CheckpointConfig.class, filter, "selectClassOfMilestoneCheckpoint");
			if (!SetUtils.isNullList(classOfList))
			{
				Map<String, String> map = new HashMap<String, String>();
				for (CheckpointConfig config : classOfList)
				{
					if (config.get("RELATEDTASKOBJECTCLASS") != null)
					{
						map.put(config.get("RELATEDTASKOBJECTCLASS").toString(), config.get("RELATEDTASKOBJECTCLASS").toString());
					}
					else
					{
						finalList.add(config);
					}
				}
				if (!SetUtils.isNullMap(map))
				{
					for (String c : map.keySet())
					{
						filter.put(CheckpointConfig.RELATEDTASKOBJECT + PPMConstans.CLASS, c);
						filter.put("CLASSGUID", c);
						List<CheckpointConfig> projectCheckpointConfigList = sds.query(CheckpointConfig.class, filter);
						if (!SetUtils.isNullList(projectCheckpointConfigList))
						{
							finalList.addAll(projectCheckpointConfigList);
						}
					}
				}
			}

			Collections.sort(finalList, new Comparator<CheckpointConfig>()
			{

				@Override
				public int compare(CheckpointConfig o1, CheckpointConfig o2)
				{
					if (o1.getSequence().compareTo(o2.getSequence()) == 0)
					{
						return o1.getCreateTime().compareTo(o2.getCreateTime());
					}
					return o1.getSequence().compareTo(o2.getSequence());
				}
			});

			return finalList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	public CheckpointConfig saveCheckpointConfig(CheckpointConfig checkpoint) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		checkpoint.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		if (!StringUtils.isGuid(checkpoint.getGuid()))
		{
			checkpoint.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}

		if (StringUtils.isGuid(checkpoint.getTypeGuid()) && checkpoint.get(CheckpointConfig.SEQUENCE) == null)
		{
			List<CheckpointConfig> listCheckpointConfigByMileStoneGuid = this.listCheckpointConfigByMileStoneGuid(checkpoint.getTypeGuid());
			if (!SetUtils.isNullList(listCheckpointConfigByMileStoneGuid))
			{
				checkpoint.setSequence(listCheckpointConfigByMileStoneGuid.size() + "");
			}
			else
			{
				checkpoint.setSequence("0");
			}
		}

		try
		{
			String guid = sds.save(checkpoint);
			if (StringUtils.isGuid(guid))
			{
				checkpoint.setGuid(guid);
			}
			return checkpoint;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 批量保存里程碑关卡
	 * 
	 * @param guid
	 * @param checkpointList
	 * @throws ServiceRequestException
	 */
	private void saveCheckpoint(String type, String mileStoneGuid, List<CheckpointConfig> checkpointList) throws ServiceRequestException
	{
		try
		{
			int i = 0;
			for (CheckpointConfig checkpoint : checkpointList)
			{
				checkpoint.put(CheckpointConfig.TYPEGUID, mileStoneGuid);
				checkpoint.clear(SystemObject.GUID);
				checkpoint.put(CheckpointConfig.TYPE, type);
				checkpoint.setSequence(i + "");
				this.saveCheckpointConfig(checkpoint);
				i++;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public void delCheckpointConfig(String checkpointGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, checkpointGuid);
			sds.delete(CheckpointConfig.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public LaborHourConfig getWorkTimeConfig() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		try
		{
			List<LaborHourConfig> workTimeConfigList = sds.query(LaborHourConfig.class, filter);
			if (!SetUtils.isNullList(workTimeConfigList))
			{
				return workTimeConfigList.get(0);
			}
			// 数据库中没有，应用层自己封装此对象
			LaborHourConfig config = new LaborHourConfig();
			config.setStandardhour(new Double("8"));
			config.setOverloadhour(new Double("10"));
			config.setUnderloadhour(new Double("6"));
			config.setFreezetime(new Double("5"));
			config.setSaveAsPersonalLog(true);
			config.setModificationAudithour(true);
			return config;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public LaborHourConfig saveWorkTimeConfig(LaborHourConfig laborHour) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		laborHour.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		if (!StringUtils.isGuid(laborHour.getGuid()))
		{
			laborHour.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}
		try
		{
			String guid = sds.save(laborHour);
			if (StringUtils.isGuid(guid))
			{
				laborHour.setGuid(guid);
			}
			return laborHour;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public CodeItemInfo getCodeItemByEnum(@SuppressWarnings("rawtypes") Enum enumType) throws ServiceRequestException
	{
		if (enumType != null)
		{
			String codeName = null;
			String codeItemName = null;
			if (enumType instanceof ProjectStatusEnum)
			{
				codeName = CodeNameEnum.EXECUTESTATUS.getValue();
				codeItemName = ((ProjectStatusEnum) enumType).getValue();
			}
			else if (enumType instanceof TaskStatusEnum)
			{
				codeName = CodeNameEnum.EXECUTESTATUS.getValue();
				codeItemName = ((TaskStatusEnum) enumType).getValue();
			}
			else if (enumType instanceof TaskTypeEnum)
			{
				codeName = CodeNameEnum.TASKTYPE.getValue();
				codeItemName = ((TaskTypeEnum) enumType).getValue();
			}
			else if (enumType instanceof DurationUnitEnum)
			{
				codeName = CodeNameEnum.DURATIONUNIT.getValue();
				codeItemName = ((DurationUnitEnum) enumType).getValue();
			}
			else if (enumType instanceof TaskDependEnum)
			{
				codeName = CodeNameEnum.TASKDEPENDENUM.getValue();
				codeItemName = ((TaskDependEnum) enumType).name();
			}

			CodeItemInfo codeItem = this.stubService.getEMM().getCodeItemByName(codeName, codeItemName);
			if (codeItem == null)
			{
				throw new ServiceRequestException("ID_APP_PM_BUILTIN_CODEITEM_NOT_FOUND", "not found code item :" + codeName + "-" + codeItemName, null, codeName, codeItemName);
			}
			return codeItem;
		}
		return null;
	}

}
