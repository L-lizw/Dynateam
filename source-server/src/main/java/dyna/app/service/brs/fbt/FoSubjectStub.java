/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FoSubjectStub
 * wangweixia 2012-9-7
 */
package dyna.app.service.brs.fbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.dto.FileOpenSubject;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

/**
 * 对应FileOpenSubject的相关操作
 * 
 * @author wangweixia
 * 
 */
public class FoSubjectStub extends AbstractServiceStub<FBTSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected FoSubjectStub(ServiceContext context, FBTSImpl service)
	{
		super(context, service);

	}

	/**
	 * @param subjectGuid
	 */
	protected void deleteFileOpenSubject(String subjectGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.delete(FileOpenSubject.class, subjectGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}

	}

	/**
	 * @return
	 */
	protected List<FileOpenSubject> listRootFileOpenSubject() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<FileOpenSubject> fileSubject = sds.listFromCache(FileOpenSubject.class, new FieldValueEqualsFilter<FileOpenSubject>("PARENTGUID", null));
			return fileSubject;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	/**
	 * @return
	 */
	protected List<FileOpenSubject> listALLNodeFileOpenSubject() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			List<FileOpenSubject> fileSubject = sds.listFromCache(FileOpenSubject.class, null);
			return fileSubject;
		}
		catch (DynaDataException e)
		{
			e.printStackTrace();
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	/**
	 * @param fileOpenSubjectGuid
	 * @param isCascade
	 * @return
	 */
	protected List<FileOpenSubject> listSubFileOpenSubject(String fileOpenSubjectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("GUID", fileOpenSubjectGuid);

		try
		{
			List<FileOpenSubject> arrayList = sds.listFromCache(FileOpenSubject.class, null);
			return shortFileOpenSubjectList(arrayList, fileOpenSubjectGuid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e);
		}
	}

	private List<FileOpenSubject> shortFileOpenSubjectList(List<FileOpenSubject> values, String guid)
	{
		if (SetUtils.isNullList(values))
		{
			return null;
		}
		List<FileOpenSubject> results = new ArrayList<FileOpenSubject>();
		for (FileOpenSubject fileOpenSubject : values)
		{
			if (guid.equalsIgnoreCase(fileOpenSubject.getParentGuid()))
			{
				results.add(fileOpenSubject);
			}
		}
		Collections.sort(results, Comparator.comparing(FileOpenSubject::getPosition));
		return results;
	}
}
