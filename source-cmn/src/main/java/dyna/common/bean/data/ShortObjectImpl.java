/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CommonObjectImpl
 * Wanglei 2010-9-1
 */
package dyna.common.bean.data;

import dyna.common.systemenum.LocationTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.SystemStatusEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * AssistantObject 的实现
 * 
 * @author Wanglei
 * 
 */
public class ShortObjectImpl extends DynaObjectImpl implements ShortObject
{

	private static final long serialVersionUID = -6938162032869174014L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getAlterId()
	 */
	@Override
	public String getAlterId()
	{
		return (String) this.get(SystemClassFieldEnum.ALTERID.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getCheckedOutUser()
	 */
	@Override
	public String getCheckedOutUser()
	{
		return (String) this.get(SystemClassFieldEnum.CHECKOUTUSER.getName() + "NAME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getCheckedOutUserId()
	 */
	@Override
	public String getCheckedOutUserGuid()
	{
		return (String) this.get(SystemClassFieldEnum.CHECKOUTUSER.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getClassfication()
	 */
	@Override
	public String getClassification()
	{
		return (String) this.get(SystemClassFieldEnum.CLASSIFICATION.getName() + "TITLE");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getClassficationGuid()
	 */
	@Override
	public String getClassificationGuid()
	{
		return (String) this.get(SystemClassFieldEnum.CLASSIFICATION.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getFullName()
	 */
	@Override
	public String getFullName()
	{
		return (String) this.get("FULLNAME$");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.SystemObject#getGuid()
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get("GUID");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getIcon()
	 */
	@Override
	public String getIcon()
	{
		return (String) this.get(BO_ICON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getId()
	 */
	@Override
	public String getId()
	{
		return (String) this.get(SystemClassFieldEnum.ID.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getIterationId()
	 */
	@Override
	public Integer getIterationId()
	{
		Number iid = (Number) this.get(SystemClassFieldEnum.ITERATIONID.getName());
		if (iid == null)
		{
			return null;
		}
		return iid.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getLifecyclePhase()
	 */
	@Override
	public String getLifecyclePhase()
	{
		return (String) this.get(SystemClassFieldEnum.LCPHASE.getName() + "TITLE");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getLifecyclePhaseGuid()
	 */
	@Override
	public String getLifecyclePhaseGuid()
	{
		return (String) this.get(SystemClassFieldEnum.LCPHASE.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getFolderGuid()
	 */
	@Override
	public String getLocation()
	{
		return (String) this.get("LOCATION$");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getFolder()
	 */
	@Override
	public LocationTypeEnum getLocationType()
	{
		return LocationTypeEnum.typeValueOf((String) this.get("LOCATIONTYPE$"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getName()
	 */
	@Override
	public String getName()
	{
		return (String) this.get(SystemClassFieldEnum.NAME.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getOwnerUser()
	 */
	@Override
	public String getOwnerUser()
	{
		return (String) this.get(SystemClassFieldEnum.OWNERUSER.getName() + "NAME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getOwnerUserGuid()
	 */
	@Override
	public String getOwnerUserGuid()
	{
		return (String) this.get(SystemClassFieldEnum.OWNERUSER.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getRevisionId()
	 */
	@Override
	public String getRevisionId()
	{
		return (String) this.get(SystemClassFieldEnum.REVISIONID.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getStatus()
	 */
	@Override
	public SystemStatusEnum getStatus()
	{
		if (this.get(SystemClassFieldEnum.STATUS.getName()) == null)
		{
			return null;
		}
		return SystemStatusEnum.getStatusEnum((String) this.get(SystemClassFieldEnum.STATUS.getName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#hasFile()
	 */
	@Override
	public boolean hasFile()
	{
		if (StringUtils.isGuid(this.getFileGuid()))
		{
			return true;
		}
		return false;
	}

	@Override
	public String getFileName()
	{
		return (String) this.get(SystemClassFieldEnum.FILENAME.getName());
	}

	@Override
	public String getFileGuid()
	{
		return (String) this.get(SystemClassFieldEnum.FILEGUID.getName());
	}

	@Override
	public String getFileType()
	{
		return (String) this.get(SystemClassFieldEnum.FILETYPE.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#isCheckedOut()
	 */
	@Override
	public boolean isCheckOut()
	{
		Boolean b = BooleanUtils.getBooleanByYN((String) this.get(SystemClassFieldEnum.ISCHECKOUT.getName()));
		return b == null ? false : b.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#isObsolete()
	 */
	@Override
	public boolean isObsolete()
	{
		return SystemStatusEnum.OBSOLETE == this.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#isShortcut()
	 */
	@Override
	public boolean isShortcut()
	{
		Boolean value = BooleanUtils.getBooleanByYN((String) this.get(IS_SHORTCUT_KEY));
		return value == null ? false : value.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.SystemObject#removeNoChanged()
	 */
	@Override
	public void removeNoChanged()
	{
		// do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setAlterId(java.lang.String)
	 */
	@Override
	public void setAlterId(String alterId)
	{
		this.put(SystemClassFieldEnum.ALTERID.getName(), alterId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#setClassification(java.lang.String)
	 */
	@Override
	public void setClassification(String clsf)
	{
		this.put(SystemClassFieldEnum.CLASSIFICATION.getName() + "TITLE", clsf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.AssistantObject#setClassificationGuid(java.lang.String)
	 */
	@Override
	public void setClassificationGuid(String classficationGuid)
	{
		this.put(SystemClassFieldEnum.CLASSIFICATION.getName(), classficationGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.SystemObject#setGuid(java.lang.String)
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put("GUID", guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setId(java.lang.String)
	 */
	@Override
	public void setId(String id)
	{
		this.put(SystemClassFieldEnum.ID.getName(), id);

	}

	@Override
	public void setLifecyclePhase(String phaseTitle)
	{
		this.put(SystemClassFieldEnum.LCPHASE.getName() + "TITLE", phaseTitle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#setLifecyclePhaseGuid(java.lang.String)
	 */
	@Override
	public void setLifecyclePhaseGuid(String phaseGuid)
	{
		this.put(SystemClassFieldEnum.LCPHASE.getName(), phaseGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setFolderGuid(java.lang.String)
	 */
	@Override
	public void setLocation(String location)
	{
		this.put("LOCATION$", location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#setFolder(java.lang.String)
	 */
	@Override
	public void setLocationType(LocationTypeEnum locationType)
	{
		this.put("LOCATIONTYPE$", locationType.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		this.put(SystemClassFieldEnum.NAME.getName(), name);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#setOwnerUserGuid(java.lang.String)
	 */
	@Override
	public void setOwnerUserGuid(String ownerUserGuid)
	{
		this.put(SystemClassFieldEnum.OWNERUSER.getName(), ownerUserGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setRevisionId(java.lang.String)
	 */
	@Override
	public void setRevisionId(String revisionId)
	{
		this.put(SystemClassFieldEnum.REVISIONID.getName(), revisionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#setStatusGuid(java.lang.String)
	 */
	@Override
	public void setStatus(SystemStatusEnum systemStatusEnum)
	{
		this.put(SystemClassFieldEnum.STATUS.getName(), systemStatusEnum.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#setShortcut(boolean)
	 */
	@Override
	public void setShortcut(boolean isShortcut)
	{
		this.put(IS_SHORTCUT_KEY, BooleanUtils.getBooleanStringYN(isShortcut));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getFileIcon16()
	 */
	@Override
	public String getFileIcon16()
	{
		return (String) this.get(FILE_ICON16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.ShortObject#getFileIcon32()
	 */
	@Override
	public String getFileIcon32()
	{
		return (String) this.get(FILE_ICON32);
	}

	/**
	 * 取得分类group标题
	 * 
	 * @return
	 */
	@Override
	public String getClassificationGroupTitle()
	{
		return (String) this.get(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName() + "TITLE");
	}

	/**
	 * 取得分类group名
	 * 
	 * @return
	 */
	@Override
	public String getClassificationGroupName()
	{
		return (String) this.get(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName() + "NAME");
	}

	/**
	 * 取得分类group guid
	 * 
	 * @return
	 */
	@Override
	public String getClassificationGroup()
	{
		return (String) this.get(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName());
	}

	/**
	 * 取得分类group标题
	 * 
	 * @return
	 */
	@Override
	public void setClassificationGroupTitle(String classificationGroupTitle)
	{
		this.put(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName() + "TITLE", classificationGroupTitle);
	}

	/**
	 * 取得分类group名
	 * 
	 * @return
	 */
	@Override
	public void setClassificationGroupName(String classificationGroupName)
	{
		this.put(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName() + "NAME", classificationGroupName);
	}

	/**
	 * 取得分类group guid
	 * 
	 * @return
	 */
	@Override
	public void setClassificationGroup(String classificationGroupGuid)
	{
		this.put(SystemClassFieldEnum.CLASSIFICATIONGROUP.getName(), classificationGroupGuid);
	}

	@Override
	public void setUnique(String unique)
	{
		this.put(SystemClassFieldEnum.UNIQUES.getName(), unique);
	}

	@Override
	public String getUnique()
	{
		return (String) this.get(SystemClassFieldEnum.UNIQUES.getName());
	}

	@Override
	public String getClassificationName()
	{
		return (String) this.get(SystemClassFieldEnum.CLASSIFICATION.getName() + "NAME");
	}

	@Override
	public void setClassificationName(String clsfName)
	{
		this.put(SystemClassFieldEnum.CLASSIFICATION.getName() + "NAME", clsfName);

	}

	@Override
	public void setRepeat(String unique)
	{
		this.put(SystemClassFieldEnum.REPEAT.getName(), unique);
	}

	@Override
	public String getRepeat()
	{
		return (String) this.get(SystemClassFieldEnum.REPEAT.getName());
	}

	@Override
	public void setMD5(String md5)
	{
		this.put(SystemClassFieldEnum.MD5.getName(), md5);
	}

	@Override
	public String getMD5()
	{
		return (String) this.get(SystemClassFieldEnum.MD5.getName());
	}

}
