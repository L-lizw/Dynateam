/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Foundation Object bean
 * xiasheng Apr 22, 2010
 */
package dyna.common.bean.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import dyna.common.bean.extra.PromptMessage;
import dyna.common.context.ObjectContext;
import dyna.common.context.ScriptContext;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author xiasheng
 * 
 */
public class FoundationObjectImpl extends ShortObjectImpl implements FoundationObject
{
	private static final long				serialVersionUID	= 7582555978502342956L;
	public static final String				separator			= "#";
	// public static final String ORICLASSIFICATIONITEM = "ORICLASSIFICATIONITEM$";
	private ObjectContext					context				= null;
	private String							privateFolderGuid	= null;
	private String							privateFolderPath	= null;

	private Map<String, FoundationObject>	classificationMap	= null;

	private void mixClasssification(FoundationObject classificationFoundation)
	{
		if (classificationFoundation == null)
		{
			return;
		}

		String classificationName = classificationFoundation.getClassificationGroupName();
		this.clearClasssification(classificationName);

		Set<String> keySet = ((FoundationObjectImpl) classificationFoundation).keySet();
		for (String name : keySet)
		{
			this.put(classificationFoundation.getClassificationGroupName() + separator + name, classificationFoundation.get(name));
		}
	}

	@Override
	public FoundationObject restoreClasssification(String classificationName)
	{
		if (this.classificationMap != null && this.classificationMap.containsKey(classificationName))
		{
			return this.classificationMap.get(classificationName);
		}

		Set<String> keySet = this.keySet();
		FoundationObject result = new FoundationObjectImpl();
		for (String name : keySet)
		{
			if (!StringUtils.isNullString(name) && name.contains(classificationName + separator))
			{
				result.put(name, this.get(classificationName + separator + name));
				this.clear(classificationName + separator + name);
			}
		}
		this.addClassification(result, false);

		return result;
	}

	@Override
	public void clearClasssification(String classificationName)
	{
		if (StringUtils.isNullString(classificationName))
		{
			return;
		}

		if (this.classificationMap != null && this.classificationMap.containsKey(classificationName))
		{
			this.classificationMap.remove(classificationName);
			return;
		}

		Set<String> keySet = this.keySet();
		List<String> clearList = new ArrayList<String>();
		for (String name : keySet)
		{
			if (this.containsKey(classificationName + separator + name))
			{
				clearList.add(classificationName + separator + name);
			}
		}

		for (String name : clearList)
		{
			this.clear(name);
		}
	}

	@Override
	public void mixAllClassification()
	{
		if (this.classificationMap == null)
		{
			return;
		}

		// Collection<FoundationObject> values2 = this.classificationMap.values();
		// for (FoundationObject foundation : values2)
		// {
		// this.addClassification(foundation, true);
		// //this.classificationMap.remove(foundation);
		// }
		List<FoundationObject> fList = new ArrayList<FoundationObject>();
		for (FoundationObject f : this.classificationMap.values())
		{
			fList.add(f);
		}
		for (FoundationObject f : fList)
		{
			this.addClassification(f, true);
		}
		fList.clear();
		this.classificationMap.clear();

	}

	@Override
	public List<FoundationObject> restoreAllClassification(boolean isDecompose)
	{
		if (!isDecompose)
		{
			if (this.classificationMap == null)
			{
				return null;
			}
			return new ArrayList<FoundationObject>(this.classificationMap.values());
		}

		Set<String> keySet = this.keySet();
		FoundationObject foundation = null;
		List<String> remove = new ArrayList<String>();
		for (String name : keySet)
		{
			if (name.contains(separator))
			{
				String[] split = name.split(separator);
				if (split.length == 2)
				{
					if (this.classificationMap != null && this.classificationMap.containsKey(split[0]))
					{
						foundation = this.classificationMap.get(split[0]);
					}
					else
					{
						foundation = new FoundationObjectImpl();
						if (this.classificationMap == null)
						{
							this.classificationMap = new HashMap<String, FoundationObject>();
						}
						this.classificationMap.put(split[0], foundation);
						foundation.setClassificationGroupName(split[0]);
					}
					foundation.put(split[1], this.get(name));
					// this.clear(name);
					remove.add(name);
				}
			}
		}
		for (String name : remove)
		{
			this.clear(name);
		}

		if (this.classificationMap == null)
		{
			return null;
		}
		return new ArrayList<FoundationObject>(this.classificationMap.values());

	}

	@Override
	public void addClassification(FoundationObject classificationFoundation, boolean isMix)
	{
		if (classificationFoundation == null)
		{
			return;
		}

		if (StringUtils.isNullString(classificationFoundation.getClassificationGroupName()))
		{
			return;
		}

		this.clearClasssification(classificationFoundation.getClassificationGroupName());
		if (isMix)
		{
			this.mixClasssification(classificationFoundation);
		}
		else
		{
			if (this.classificationMap == null)
			{
				this.classificationMap = new HashMap<String, FoundationObject>();
			}
			this.classificationMap.put(classificationFoundation.getClassificationGroupName(), classificationFoundation);
		}
	}

	public FoundationObjectImpl()
	{
		super();
	}

	@Override
	public void sync(DynaObject object)
	{
		super.sync(object);
		if (object instanceof FoundationObjectImpl)
		{
			FoundationObjectImpl orignalObj = (FoundationObjectImpl) object;
			if (orignalObj.classificationMap != null)
			{
				this.classificationMap = orignalObj.classificationMap;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		FoundationObjectImpl result = new FoundationObjectImpl();
		result.putAll((Map<String, Object>) super.clone());
		result.originalMap.putAll(this.originalMap);
		result.setObjectGuid(new ObjectGuid(this.getObjectGuid()));
		if (this.classificationMap != null)
		{
			Map<String, FoundationObject> clf = new HashMap<String, FoundationObject>();
			Set<java.util.Map.Entry<String, FoundationObject>> entrySet2 = this.classificationMap.entrySet();
			for (java.util.Map.Entry<String, FoundationObject> it : entrySet2)
			{
				clf.put(it.getKey(), (FoundationObject) it.getValue().clone());
			}

			result.classificationMap = clf;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getCheckedOutTime()
	 */
	@Override
	public Date getCheckedOutTime()
	{
		return (Date) this.get(SystemClassFieldEnum.CHECKOUTTIME.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.DynaObject#getGuid()
	 */
	@Override
	public String getGuid()
	{
		return (String) super.get("GUID$");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getObsoleteTime()
	 */
	@Override
	public Date getObsoleteTime()
	{
		if (this.get(SystemClassFieldEnum.OBSOLETETIME.getName()) == null)
		{
			return null;
		}
		return (Date) this.get(SystemClassFieldEnum.OBSOLETETIME.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getOwnerGroup()
	 */
	@Override
	public String getOwnerGroup()
	{
		return (String) this.get(SystemClassFieldEnum.OWNERGROUP.getName() + "NAME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#getOwnerGroupGuid()
	 */
	@Override
	public String getOwnerGroupGuid()
	{
		return (String) this.get(SystemClassFieldEnum.OWNERGROUP.getName());
	}

	@Override
	public PromptMessage getPromptMessage()
	{
		return (PromptMessage) this.get(FoundationObject.MESSAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getReleaseTime()
	 */
	@Override
	public Date getReleaseTime()
	{
		return (Date) this.get(SystemClassFieldEnum.RELEASETIME.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.SystemObject#setGuid(java.lang.String)
	 */
	@Override
	public void setGuid(String guid)
	{
		super.put("GUID$", guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.classbean.FoundationObject#setOwnerGroupGuid(java.lang.String)
	 */
	@Override
	public void setOwnerGroupGuid(String ownerGroupGuid)
	{
		this.put(SystemClassFieldEnum.OWNERGROUP.getName(), ownerGroupGuid);
	}

	@Override
	public void setPromptMessage(PromptMessage promptMessage)
	{
		this.put(FoundationObject.MESSAGE, promptMessage);
	}

	public synchronized ObjectContext getObjectContext()
	{
		if (this.context == null)
		{
			this.context = new ObjectContext();
		}
		return this.context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.InputObject#getScriptContext()
	 */
	@Override
	public ScriptContext getScriptContext()
	{
		return this.getObjectContext().scriptContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.InputObject#setScriptContext(dyna.common.context.ScriptContext)
	 */
	@Override
	public void setScriptContext(ScriptContext context)
	{
		this.getObjectContext().scriptContext = context;
	}

	@Override
	public String getCheckOutPath()
	{
		// if (this.get(SystemClassFieldEnum.CHECKOUTPATH.getName()) == null)
		// {
		// return null;
		// }
		// return (String) this.get(SystemClassFieldEnum.CHECKOUTPATH.getName());
		return null;
	}

	@Override
	public String getObsoleteUser()
	{
		if (this.get(SystemClassFieldEnum.OBSOLETEUSER.getName() + "NAME") == null)
		{
			return null;
		}
		return (String) this.get(SystemClassFieldEnum.OBSOLETEUSER.getName() + "NAME");
	}

	@Override
	public String getObsoleteUserGuid()
	{
		if (this.get(SystemClassFieldEnum.OBSOLETEUSER.getName()) == null)
		{
			return null;
		}
		return (String) this.get(SystemClassFieldEnum.OBSOLETEUSER.getName());
	}

	@Override
	public boolean isExportToERP()
	{
		Boolean b = BooleanUtils.getBooleanBy10((String) this.get(SystemClassFieldEnum.ISEXPORTTOERP.getName()));
		return b == null ? false : b.booleanValue();
	}

	@Override
	public void setCheckOutPath(String checkoutpath)
	{
		// this.put(SystemClassFieldEnum.CHECKOUTPATH.getName(), checkoutpath);
	}

	@Override
	public void setExportToERP(boolean isExportToERP)
	{
		this.put(SystemClassFieldEnum.ISEXPORTTOERP.getName(), BooleanUtils.getBooleanString10(isExportToERP));
	}

	@Override
	public void setObsoleteUserGuid(String obsoleteUserGuid)
	{
		this.put(SystemClassFieldEnum.OBSOLETEUSER.getName(), obsoleteUserGuid);
	}

	@Override
	public String getLocationlib()
	{
		if (this.get(SystemClassFieldEnum.LOCATIONLIB.getName()) == null)
		{
			return null;
		}
		return (String) this.get(SystemClassFieldEnum.LOCATIONLIB.getName());
	}

	@Override
	public void setLocationlib(String locationlib)
	{
		this.put(SystemClassFieldEnum.LOCATIONLIB.getName(), locationlib);
	}

	@Override
	public String getCommitFolderGuid()
	{
		if (this.get(SystemClassFieldEnum.COMMITFOLDER.getName()) == null)
		{
			return null;
		}
		return (String) this.get(SystemClassFieldEnum.COMMITFOLDER.getName());
	}

	@Override
	public String getCommitPath()
	{
		if (this.get("COMMITPATH") == null)
		{
			return null;
		}
		return (String) this.get("COMMITPATH");
	}

	@Override
	public void setCommitFolderGuid(String commitFolderGuid)
	{
		this.put(SystemClassFieldEnum.COMMITFOLDER.getName(), commitFolderGuid);
	}

	@Override
	public void setCommitPath(String commitPath)
	{
		this.put("COMMITPATH", commitPath);
	}

	@Override
	public String getLocationUser()
	{
		return null;
	}

	@Override
	public void setLocationUser(String locationUser)
	{
	}

	@Override
	public ObjectGuid getECFlag()
	{
		String name = SystemClassFieldEnum.ECFLAG.getName();
		if (!StringUtils.isGuid((String) this.get(name)))
		{
			return null;
		}

		return new ObjectGuid((String) this.get(name + "CLASS"), null, (String) this.get(name), (String) this.get(name + "MASTER"), null);

	}

	@Override
	public void setECFlag(ObjectGuid ecFlag)
	{
		String name = SystemClassFieldEnum.ECFLAG.getName();
		if (ecFlag == null)
		{
			this.put(name, null);
			this.put(name + "CLASS", null);
			this.put(name + "MASTER", null);
		}
		else
		{
			this.put(name, ecFlag.getGuid());
			this.put(name + "CLASS", ecFlag.getClassGuid());
			this.put(name + "MASTER", ecFlag.getMasterGuid());
		}
	}

	@Override
	public boolean isCommited()
	{
		return true;
	}

	@Override
	public boolean isNeedFlowConfirmEffective()
	{
		Boolean isCnfm = BooleanUtils.getBooleanByYN((String) this.get("NEEDFLOWCONFIRMEFFECTIVE"));
		return isCnfm == null ? false : isCnfm;
	}

	@Override
	public void setNeedFlowConfirmEffective(boolean isNeedFlowConfirmEffective)
	{
		this.put("NEEDFLOWCONFIRMEFFECTIVE", BooleanUtils.getBooleanStringYN(isNeedFlowConfirmEffective));
	}

	@Override
	public String getLocationlibName()
	{
		if (this.get(SystemClassFieldEnum.LOCATIONLIB.getName() + "NAME") == null)
		{
			return null;
		}
		return (String) this.get(SystemClassFieldEnum.LOCATIONLIB.getName() + "NAME");
	}

	@Override
	public void setLocationlibName(String locationlibName)
	{
		this.put(SystemClassFieldEnum.LOCATIONLIB.getName() + "NAME", locationlibName);
	}

	@Override
	public String getPrivateFolderGuid()
	{
		return this.privateFolderGuid;
	}

	@Override
	public void setPrivateFolderGuid(String privateFolderGuid)
	{
		this.privateFolderGuid = privateFolderGuid;
	}

	@Override
	public String getPrivateFolderPath()
	{
		return this.privateFolderPath;
	}

	@Override
	public void setPrivateFolderPath(String privateFolderPath)
	{
		this.privateFolderPath = privateFolderPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setGenericItem(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void setGenericItem(ObjectGuid genericItem)
	{
		// String name = SystemClassFieldEnum.GENERICITEM.getName();
		//
		// if (genericItem == null)
		// {
		// this.put(name, "");
		// this.put(name + "CLASS", "");
		// this.put(name + "MASTER", "");
		// }
		// else
		// {
		// this.put(name, genericItem.getGuid());
		// this.put(name + "CLASS", genericItem.getClassGuid());
		// this.put(name + "MASTER", genericItem.getMasterGuid());
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getGenericItem()
	 */
	@Override
	public ObjectGuid getGenericItem()
	{
		// String name = SystemClassFieldEnum.GENERICITEM.getName();
		// if (!StringUtils.isGuid((String) this.get(name)))
		// {
		// return null;
		// }
		//
		// return new ObjectGuid((String) this.get(name + "CLASS"), null, (String) this.get(name), (String)
		// this.get(name
		// + "MASTER"), null);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setConfigInstance(java.lang.String)
	 */
	@Override
	public void setConfigInstance(String configInstance)
	{
		// this.put(SystemClassFieldEnum.CONFIGINSTANCE.getName(), configInstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#getConfigInstance()
	 */
	@Override
	public String getConfigInstance()
	{
		// return (String) this.get(SystemClassFieldEnum.CONFIGINSTANCE.getName());
		return null;
	}

	// @Override
	// public boolean isMasterScript()
	// {
	// Boolean b = BooleanUtils.getBooleanBy01((String) this.get("ISMASTERSCRIPT"));
	// return b == null ? false : b.booleanValue();
	// }
	//
	// @Override
	// public void setMasterScript(boolean isMasterScript)
	// {
	// this.put("ISMASTERSCRIPT", BooleanUtils.getBooleanString01(isMasterScript));
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#isProjectReference()
	 */
	@Override
	public ObjectGuid getProjectReference()
	{
		// String name = SystemClassFieldEnum.PROJECTREFERENCE.getName();
		// if (!StringUtils.isGuid((String) this.get(name)))
		// {
		// return null;
		// }
		//
		// return new ObjectGuid((String) this.get(name + "CLASS"), null, (String) this.get(name), (String)
		// this.get(name
		// + "MASTER"), null);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.data.FoundationObject#setDeliveryPublic(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public void setProjectReference(ObjectGuid projectReference)
	{
		// String name = SystemClassFieldEnum.PROJECTREFERENCE.getName();
		//
		// if (projectReference == null)
		// {
		// this.put(name, "");
		// this.put(name + "CLASS", "");
		// this.put(name + "MASTER", "");
		// }
		// else
		// {
		// this.put(name, projectReference.getGuid());
		// this.put(name + "CLASS", projectReference.getClassGuid());
		// this.put(name + "MASTER", projectReference.getMasterGuid());
		// }
	}

	@Override
	public boolean isLatestRevision()
	{
		String laterst = (String) this.get("LATESTREVISION$");
		if (!StringUtils.isNullString(laterst) && laterst.contains("m"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
