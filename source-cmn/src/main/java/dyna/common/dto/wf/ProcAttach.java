/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttach 工作流程附件
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ShortObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.wf.ProcAttachMapper;
import dyna.common.util.BooleanUtils;
import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工作流程附件
 * 
 * @author Wanglei
 * 
 */
@EntryMapper(ProcAttachMapper.class)
public class ProcAttach extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= 1L;

	public static final String	PROCRT_GUID				= "PROCRTGUID";
	public static final String	INSTANCE_GUID			= "INSTANCEGUID";
	public static final String	CLASS_GUID				= "CLASSGUID";

	public static final String	ICON32					= "ICON32$";

	// public static final String HAS_BOM = "HASBOM"; // 是否带BOM

	// public static final String ROOT_GUID = "ROOTINSTANCEGUID";
	// public static final String END2_VIEW_GUID = "END2VIEWGUID";
	public static final String	FULL_NAME				= "FULLNAME";
	public static final String	ID						= "ID$";
	public static final String	NAME					= "NAME$";
	public static final String	REVISIONID				= "REVISIONID$";
	public static final String	ITERATIONID				= "ITERATIONID$";

	public static final String	HAS_COMMENT				= "HASCOMMENT";				// 是否有附件意见
	public static final String	INSTANCE_TYPE			= "INSTANCETYPE";			// 1：产品；3：BOM；5：VIEW；
	public static final String	IS_MAIN					= "ISMAIN";					// 1：为主附件；0：为关联附件

	// public static final String IS_OUT_OF_LIFECYCLE_PHASE = "ISOUTOFLIFECYCLEPHASE"; // 1：生命周期不适合流程；0：适合
	// public static final String IS_OUT_OF_WORKFLOW_TYPE = "ISOUTOFWORKFLOWTYPE"; // 1：本身不适用流程；0：适合
	// public static final String IS_CHECKOUT = "ISCHECKOUT"; // 1：检出；0：适合
	// public static final String IS_LOCKED = "ISLOCKED"; // 1：被其他流程锁定；0：适合
	// public static final String IS_UNSELACL = "ISUNSELACL"; // 查看权限
	// public static final String IS_UNWFACL = "ISUNWFACL"; // 工作流权限
	// public static final String IS_REMOVED = "ISREMOVED"; // 是否被删除
	// public static final String IS_LEGALEND1 = "IS_LEGALEND1"; // 关系下的end2的end1是否非法

	public static final String	STATUS					= "STATUS$";
	public static final String	FILENAME				= "FILENAME$";
	public static final String	FILETYPE				= "FILETYPE$";
	public static final String	FILEGUID				= "FILEGUID$";
	public static final String	UPDATETIME				= "UPDATETIME$";
	public static final String	CREATETIME				= "CREATETIME$";
	public static final String	OWNERUSERNAME			= "OWNERUSER$NAME";
	public static final String	OWNERUSER				= "OWNERUSER";
	public static final String	CLASSIFICATION			= "CLASSIFICATION$";

	public static final String	FILE_ICON16				= "FILEICON16$";
	public static final String	FILE_ICON32				= "FILEICON32$";

	public static final String	BOTITLE					= "BOTITLE";
	public static final String	CLASSIFICATIONTITLE		= "CLASSIFICATIONTITLE";

	public static final String	IS_CALCULATED			= "ISCALCULATED";
	public static final String	IS_INVALID				= "ISINVALID";

	public static final String	INVALID_REASONS			= "INVALIDREASONS";

	public static final String	INSTANCELIFCPBACKUP		= "INSTANCELIFCPBACKUP";
	public static final String	INSTANCESTATUSBACKUP	= "INSTANCESTATUSBACKUP";
	public static final String	LIFECYCLEPHASE			= "LIFECYCLEPHASE$";

	public enum InvalidReasonsEnum
	{
		OUT_OF_LIFECYCLE_PHASE("1", "ID_CLIENT_WORKFLOW_EXAMPLE_MISMATCHES_STATUS"), // 生命周期不适合流程
		OUT_OF_WORKFLOW_TYPE("2", "ID_CLIENT_WORKFLOW_EXAMPLE_MISMATCHES"), // 未生效的附件直接走发布的流程
		CHECKOUT("3", "ID_CLIENT_WORKFLOW_EXAMPLE_DETECTION"), // 检出
		LOCKED("4", "ID_CLIENT_WORKFLOW_ISLOCKED"), // 被其他流程锁定
		UNWFACL("5", "ID_CLIENT_WORKFLOW_PERMISSION_DENIED"), // 工作流权限
		UNSELACL("6", "ID_CLIENT_WORKFLOW_ISUNVIEWABLE"); // 查看权限

		private String	type	= null;
		private String	msrId	= null;

		private InvalidReasonsEnum(String type, String msrId)
		{
			this.msrId = msrId;
			this.type = type;
		}

		public String getMsrId()
		{
			return this.msrId;
		}

		public String getType()
		{
			return this.type;
		}

	}

	public void setLifecyclePhase(String lifecyclePhase)
	{
		this.put(LIFECYCLEPHASE, lifecyclePhase);
	}

	public String getLifecyclePhase()
	{
		return (String) this.get(LIFECYCLEPHASE);
	}

	public void setInstanceLifcpBackup(String instanceLifcpBackup)
	{
		this.put(INSTANCELIFCPBACKUP, instanceLifcpBackup);
	}

	public String getInstanceLifcpBackup()
	{
		return (String) this.get(INSTANCELIFCPBACKUP);
	}

	public void setInstanceStatusBackup(String instanceStatusBackup)
	{
		this.put(INSTANCESTATUSBACKUP, instanceStatusBackup);
	}

	public String getInstanceStatusBackup()
	{
		return (String) this.get(INSTANCESTATUSBACKUP);
	}

	public String getFileIcon32()
	{
		return (String) this.get(FILE_ICON32);
	}

	public String getFileIcon16()
	{
		return (String) this.get(FILE_ICON16);
	}

	public String getBoTitle()
	{
		return (String) this.get(BOTITLE);
	}

	public void setBoTitle(String boTitle)
	{
		this.put(BOTITLE, boTitle);
	}

	public String getFileGuid()
	{
		return (String) this.get(FILEGUID);
	}

	public String getFileType()
	{
		return (String) this.get(FILETYPE);
	}

	public String getStatus()
	{
		return (String) this.get(STATUS);
	}

	public String getFileName()
	{
		return (String) this.get(FILENAME);
	}

	public String getIcon32()
	{
		return (String) this.get(ICON32);
	}

	public String getIcon()
	{
		return (String) this.get(ShortObject.BO_ICON);
	}

	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get(UPDATETIME);
	}

	@Override
	public Date getCreateTime()
	{
		return (Date) this.get(CREATETIME);
	}

	public String getOwnerUserName()
	{
		return (String) this.get(OWNERUSERNAME);
	}

	public void setOwnerUserName(String ownerUserName)
	{
		super.put(OWNERUSERNAME, ownerUserName);
	}

	public String getOwnerUser()
	{
		return (String) this.get(OWNERUSER);
	}

	public void setOwnerUser(String ownerUserGuid)
	{
		super.put(OWNERUSER, ownerUserGuid);
	}

	public String getClassification()
	{
		return (String) this.get(CLASSIFICATION);
	}

	public String getClassificationTitle()
	{
		return (String) this.get(CLASSIFICATIONTITLE);
	}

	public void setClassificationTitle(String ClassificationTitle)
	{
		this.put(CLASSIFICATIONTITLE, ClassificationTitle);
	}

	public static enum AttachmentType
	{
		/**
		 * 实例
		 */
		INSTANCE(1),

		/**
		 * BOM
		 */
		BOM(3),

		/**
		 * 关系
		 */
		RELATION(5);

		int ordinary = 1;

		private AttachmentType(int ordinary)
		{
			this.ordinary = ordinary;
		}

		public static AttachmentType getAttachmentType(int ordinary)
		{
			for (AttachmentType type : values())
			{
				if (ordinary == type.ordinary)
				{
					return type;
				}
			}
			return null;
		}
	}

	// public String getEnd2ViewGuid()
	// {
	// return (String) super.get(END2_VIEW_GUID);
	// }
	//
	// public String getRootGuid()
	// {
	// return (String) super.get(ROOT_GUID);
	// }

	public String reLoadFullName()
	{
		String fullName = "";
		String id = this.getId();
		String name = this.getName();
		String revisionId = this.getRevisionId();
		BigDecimal iterationId = this.getIterationId();

		fullName += id;
		if (!StringUtils.isNullString(revisionId))
		{
			fullName += "/" + revisionId + "." + iterationId;
		}
		if (!StringUtils.isNullString(name))
		{
			fullName += "-" + name;
		}
		this.setFullName(fullName);

		return fullName;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub
		super.put(NAME, name);
	}

	public String getFullName()
	{
		return (String) super.get(FULL_NAME);
	}

	public void setFullName(String fullName)
	{
		super.put(FULL_NAME, fullName);
	}

	public String getId()
	{
		return (String) super.get(ID);
	}

	public void setId(String id)
	{
		super.put(ID, id);
	}

	public String getRevisionId()
	{
		return (String) super.get(REVISIONID);
	}

	public void setRevisionId(String revisionId)
	{
		super.put(REVISIONID, revisionId);
	}

	public BigDecimal getIterationId()
	{
		return (BigDecimal) super.get(ITERATIONID);
	}

	public void setIterationId(String iterationId)
	{
		super.put(ITERATIONID, iterationId);
	}

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public String getInstanceGuid()
	{
		return (String) super.get(INSTANCE_GUID);
	}

	public void setInstanceGuid(String guid)
	{
		super.put(INSTANCE_GUID, guid);
	}

	public String getInstanceClassGuid()
	{
		return (String) super.get(CLASS_GUID);
	}

	public void setInstanceClassGuid(String guid)
	{
		super.put(CLASS_GUID, guid);
	}

	// /**
	// * 附件是否带bom
	// *
	// * @return
	// */
	// public boolean hasBom()
	// {
	// Boolean ret = BooleanUtils.getBooleanByYN((String) super.get(HAS_BOM));
	// return ret == null ? false : ret.booleanValue();
	// }

	/**
	 * 附件是否有批注
	 * 
	 * @return
	 */
	public boolean hasComment()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) super.get(HAS_COMMENT));
		return ret == null ? false : ret.booleanValue();
	}

	/**
	 * 附件是否有批注
	 * 
	 * @return
	 */
	public void setComment(boolean isComment)
	{
		this.put(HAS_COMMENT, BooleanUtils.getBooleanStringYN((isComment)));
	}

	/**
	 * 附件类型
	 * 
	 * @return
	 */
	public void setAttachmentType(AttachmentType type)
	{
		if (type == null)
		{
			super.put(INSTANCE_TYPE, AttachmentType.INSTANCE.ordinary + "");
		}
		super.put(INSTANCE_TYPE, type.ordinary + "");
	}

	/**
	 * 附件类型
	 * 
	 * @return
	 */
	public AttachmentType getAttachmentType()
	{
		Integer ord = NumberUtils.getIneger((String) super.get(INSTANCE_TYPE));
		if (ord == null)
		{
			return null;
		}
		return AttachmentType.getAttachmentType(ord);
	}

	/**
	 * 是否已经计算
	 * 
	 * @return
	 */
	public void setCalculated(boolean isCalculated)
	{
		this.put(IS_CALCULATED, BooleanUtils.getBooleanStringYN(isCalculated));
	}

	/**
	 * 是否已经计算
	 * 
	 * @return
	 */
	public boolean isCalculated()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) super.get(IS_CALCULATED));
		return ret == null ? false : ret.booleanValue();
	}

	/**
	 * 是否无效
	 * 
	 * @return
	 */
	public void setInvalid(boolean isInvalid)
	{
		this.put(IS_INVALID, BooleanUtils.getBooleanStringYN(isInvalid));
	}

	/**
	 * 是否无效
	 * 
	 * @return
	 */
	public boolean isInvalid()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) super.get(IS_INVALID));
		return ret == null ? false : ret.booleanValue();
	}

	/**
	 * 是否主附件
	 * 
	 * @return
	 */
	public void setMain(boolean isMain)
	{
		this.put(IS_MAIN, BooleanUtils.getBooleanStringYN(isMain));
	}

	public String getInvalidReasion()
	{
		return (String) super.get(INVALID_REASONS);
	}
	
	public void setInvalidReasion(String invalidReasion)
	{
		super.put(INVALID_REASONS, invalidReasion);
	}

	/**
	 * 是否主附件
	 * 
	 * @return
	 */
	public boolean isMain()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) super.get(IS_MAIN));
		return ret == null ? false : ret.booleanValue();
	}

	// /**
	// * true: 实例状态与流程不匹配
	// *
	// * @return
	// */
	// public boolean isOutPhase()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_OUT_OF_LIFECYCLE_PHASE));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true: 实例与流程不匹配
	// *
	// * @return
	// */
	// public boolean isOutWorkflowType()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_OUT_OF_WORKFLOW_TYPE));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true: 实例被检出
	// *
	// * @return
	// */
	// public boolean isCheckouted()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_CHECKOUT));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true: 实例被其他流程锁定
	// *
	// * @return
	// */
	// public boolean isLocked()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_LOCKED));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true: 实例没有查看权限
	// *
	// * @return
	// */
	// public boolean isUnViewable()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_UNSELACL));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true:流程权限不允许
	// */
	// public boolean isWorkflowUnAuthoizable()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_UNWFACL));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true:被手动删除
	// *
	// * @return
	// */
	// public boolean isRemoved()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_REMOVED));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// /**
	// * true:关系下的end2的end1是非法的，同时导致end2也为非法
	// *
	// * @return
	// */
	// public boolean isLegalEnd1()
	// {
	// Boolean ret = BooleanUtils.getBooleanBy10((String) super.get(IS_LEGALEND1));
	// return ret == null ? false : ret.booleanValue();
	// }

	/**
	 * 取得所有的无效原因
	 * 
	 * @return
	 */
	public List<InvalidReasonsEnum> listInvalidReasons()
	{
		String reasons = (String) super.get(INVALID_REASONS);
		if (StringUtils.isNullString(reasons))
		{
			return null;
		}
		List<InvalidReasonsEnum> reasonsList = new ArrayList<InvalidReasonsEnum>();
		for (InvalidReasonsEnum reason : InvalidReasonsEnum.values())
		{
			if (reasons.contains(reason.getType()))
			{
				reasonsList.add(reason);
			}
		}

		return reasonsList;
	}
}
