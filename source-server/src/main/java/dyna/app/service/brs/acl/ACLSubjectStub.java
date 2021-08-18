/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLSubjectStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.acl;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.acl.ACLSubject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.acl.AclService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Lizw
 * 
 */
@Component
public class ACLSubjectStub extends AbstractServiceStub<ACLImpl>
{

	protected static void init()
	{
		try
		{
			DataServer.getAclService().loadACLTreeToCache();
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}

	protected List<ACLSubject> listRootACLSubjectByLIB(String libraryGuid) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		Map<String, ACLSubject> subjectTreeMap = aclService.listAllSubjectWithTree();
		ACLSubject root = subjectTreeMap.get(libraryGuid);

		List<ACLSubject> list = new ArrayList<ACLSubject>();
		list.add(root);
		if (!SetUtils.isNullList(root.getChildren()))
		{
			list.addAll(root.getChildren());
		}

		Collections.sort(list, new Comparator<ACLSubject>() {

			@Override
			public int compare(ACLSubject o1, ACLSubject o2)
			{
				return o1.getHierarchy().compareTo(o2.getHierarchy());
			}
		});

		return list;
	}

	protected List<ACLSubject> listSubACLSubject(String aclSubjectGuid, boolean isCascade) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		Map<String, ACLSubject> aclSubjectMap = aclService.listAllSubjectWithTree();
		ACLSubject searched = this.getACLSubjectWithChild(aclSubjectMap, aclSubjectGuid);
		if (searched == null)
		{
			return null;
		}

		List<ACLSubject> subjectList = null;
		if (!isCascade)
		{
			subjectList = searched.getChildren();
		}
		else
		{
			subjectList = new ArrayList<ACLSubject>();
			this.buildAllSubACLSubjectToList(searched, subjectList);
		}

		if (!SetUtils.isNullList(subjectList))
		{
			Collections.sort(subjectList, new Comparator<ACLSubject>() {

				@Override
				public int compare(ACLSubject o1, ACLSubject o2)
				{
					return o1.getHierarchy().compareTo(o2.getHierarchy());
				}
			});
		}

		return subjectList;
	}

	/**
	 * 取得当前对象的所有子项
	 * 
	 * @param subject
	 * @param list
	 */
	private void buildAllSubACLSubjectToList(ACLSubject subject, List<ACLSubject> list)
	{
		List<ACLSubject> subjectList = subject.getChildren();
		if (!SetUtils.isNullList(subjectList))
		{
			for (ACLSubject subject_ : subjectList)
			{
				list.add(subject_);
				this.buildAllSubACLSubjectToList(subject_, list);
			}
		}
	}

	private ACLSubject getACLSubjectWithChild(Map<String, ACLSubject> subjectMap, String aclSubjectGuid)
	{
		SystemDataService sds = DataServer.getSystemDataService();
		ACLSubject searchSubject = sds.get(ACLSubject.class, aclSubjectGuid);
		if (searchSubject == null)
		{
			return null;
		}

		ACLSubject root = subjectMap.get(searchSubject.getLibraryGuid());
		if (root != null)
		{
			return this.getSubject(root, aclSubjectGuid);
		}
		return null;
	}

	private ACLSubject getSubject(ACLSubject subject, String aclSubjectGuid)
	{
		if (subject.getGuid().equals(aclSubjectGuid))
		{
			return subject;
		}
		List<ACLSubject> children = subject.getChildren();
		if (!SetUtils.isNullList(children))
		{
			for (ACLSubject child : children)
			{
				ACLSubject subject_ = this.getSubject(child, aclSubjectGuid);
				if (subject_ != null)
				{
					return subject_;
				}
			}
		}
		return null;
	}

	protected List<ACLSubject> listACLSubject() throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		List<ACLSubject> dataList = new ArrayList<ACLSubject>();
		Map<String, ACLSubject> subjectTreeMap = aclService.listAllSubjectWithTree();
		for (String library : subjectTreeMap.keySet())
		{
			ACLSubject root = subjectTreeMap.get(library);

			List<ACLSubject> list = new ArrayList<ACLSubject>();
			list.add(root);

			this.buildAllSubACLSubjectToList(root, list);

			dataList.addAll(list);
		}

		Collections.sort(dataList, new Comparator<ACLSubject>() {

			@Override
			public int compare(ACLSubject o1, ACLSubject o2)
			{
				if (o1.getLibraryGuid().compareTo(o2.getLibraryGuid()) == 0)
				{
					return o1.getHierarchy().compareTo(o2.getHierarchy());
				}
				return o1.getLibraryGuid().compareTo(o2.getLibraryGuid());
			}
		});

		return dataList;
	}

	protected ACLSubject saveACLSubject(ACLSubject aclSubject) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			if (aclSubject.getCondition() == null)
			{
				throw new ServiceRequestException("ID_APP_ACLSUBJECT_CONDITION_EMPTY", "aclsubject condition is empty");
			}

			if (StringUtils.isNullString(aclSubject.getValueGuid()))
			{
				throw new ServiceRequestException("ID_APP_ACLSUBJECT_VALUE_EMPTY", "aclsubject value is empty");
			}

			String userGuid = this.stubService.getOperatorGuid();

			boolean isCreate = false;
			if (!StringUtils.isGuid(aclSubject.getGuid()))
			{
				isCreate = true;
				aclSubject.put(SystemObject.CREATE_USER_GUID, userGuid);
			}

			aclSubject.put(SystemObject.UPDATE_USER_GUID, userGuid);

			// aclSubject.put(ACLSubject.CONDITION_GUID, aclSubject.getCondition());

			String guid = sds.save(aclSubject);
			if (!isCreate)
			{
				guid = aclSubject.getGuid();
			}

			return this.getACLSubject(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, aclSubject.getName());
		}
	}

	protected ACLSubject getACLSubject(String aclSubjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		ACLSubject subject = sds.get(ACLSubject.class, aclSubjectGuid);
		return subject;
	}

	protected ACLSubject getACLSubjectByName(String libraryGuid, String aclSubjectName) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		UpperKeyMap filterMap = new UpperKeyMap();
		filterMap.put("NAME", aclSubjectName);
		filterMap.put(ACLSubject.LIBRARYGUID, libraryGuid);

		List<ACLSubject> list = sds.listFromCache(ACLSubject.class, new FieldValueEqualsFilter<ACLSubject>(filterMap));
		return SetUtils.isNullList(list) ? null : list.get(0);
	}

	protected boolean deleteACLSubject(String aclSubjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		boolean hasDeleted = false;

		try
		{
			hasDeleted = sds.delete(ACLSubject.class, aclSubjectGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}

		return hasDeleted;
	}

}
