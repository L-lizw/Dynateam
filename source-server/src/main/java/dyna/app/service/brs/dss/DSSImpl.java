/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DSSImpl Distributed Storage Service implementation
 * Wanglei 2010-8-31
 */
package dyna.app.service.brs.dss;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.track.annotation.Tracked;
import dyna.app.server.context.ApplicationServerContext;
import dyna.app.server.context.ServerContextListener;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.BusinessRuleService;
import dyna.app.service.brs.dss.tracked.TRDSSFile;
import dyna.app.service.brs.dss.tracked.TRDSSFileInfoImpl;
import dyna.app.service.brs.dss.tracked.TRFileTransImpl;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.TrackedDesc;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.serv.DSStorage;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.FileType;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.*;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Distributed Storage Service implementation
 * 
 * @author Wanglei
 * 
 */
@Service
public class DSSImpl extends BusinessRuleService implements DSS
{
	private static boolean		initialized			= false;
	@Autowired
	private FileInfoStub		fileInfoStub		;
	@Autowired
	private InstFileStub		instFileStub		;
	@Autowired
	private ProcFileStub		procFileStub		;
	@Autowired
	private TransFileStub		transFileStub		;
	@Autowired
	private StorageStub			storageStub			;
	@Autowired
	private AnyTableFileStub	anyTableFileStub	;

	public static final String	FILE_TYPE_WF		= "C";
	public static final String	FILE_TYPE_ANYTABLE	= "A";
	public static final String	FILE_TYPE_INSTANCE	= "F";
	private String				secondTranId		= null;
	private static boolean		isInit				= false;
	private static boolean		deleteByLogic		= true;

	// private static boolean isInit = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public void init()
	{
		syncInit();
		// timingNotice(this.serviceContext);
		if (isInit)
		{
			return;
		}
		isInit = true;
		if (this.getServiceDefinition().getInitParameter("delete-by-logic") != null)
		{
			deleteByLogic = "true".equalsIgnoreCase(this.getServiceDefinition().getInitParameter("delete-by-logic"));
		}
		this.server.addServerContextListener(new TimeClearServerContextListener(deleteByLogic));
	}

	private static synchronized void syncInit()
	{
		if (initialized)
		{
			return;
		}

		FileInfoStub.init();

		initialized = true;
	}

	/**
	 * @return the anyTableFileStub
	 */
	public AnyTableFileStub getAnyTableFileStub()
	{
		return this.anyTableFileStub;
	}

	/**
	 * @return the storageStub
	 */
	protected StorageStub getStorageStub()
	{
		return this.storageStub;
	}

	/**
	 * @return the fileInfoStub
	 */
	public FileInfoStub getFileInfoStub()
	{
		return this.fileInfoStub;
	}

	/**
	 * @return the instFileStub
	 */
	public InstFileStub getInstFileStub()
	{
		return this.instFileStub;
	}

	/**
	 * @return the procFileStub
	 */
	protected ProcFileStub getProcFileStub()
	{
		return this.procFileStub;
	}

	/**
	 * @return the transFileStub
	 */
	public TransFileStub getTransFileStub()
	{
		return this.transFileStub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.BusinessRuleService#authorize(java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void authorize(Method method, Object... args) throws AuthorizeException
	{
		try
		{
			super.authorize(method, args);
		}
		catch (AuthorizeException e)
		{
			if (this.getSignature() == null)
			{
				throw e;
			}
		}
	}

	protected synchronized FTS getFTS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(FTS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized WFI getWFE() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(WFI.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized ACL getACL() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(ACL.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized EDAP getEDAP() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EDAP.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#attachFile(dyna.common.bean.data.ObjectGuid, java.lang.String)
	 */
	@Tracked(description = TrackedDesc.ATTACH_FILE, renderer = TRDSSFileInfoImpl.class)
	@Override
	public DSSFileInfo attachFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException
	{
		return this.getInstFileStub().attachFile(objectGuid, file, Constants.isSupervisor(true, this), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#batchDownloadFile(java.util.List)
	 */
	@Tracked(description = TrackedDesc.DOWNLOAD_FILE, renderer = TRFileTransImpl.class)
	@Override
	public DSSFileTrans batchDownloadFile(List<String> fileGuidList) throws ServiceRequestException
	{
		return this.getTransFileStub().batchDownloadFile(fileGuidList);
	}

	@Override
	public List<DSSFileTrans> batchlisDownloadFile(List<String> fileGuidList) throws ServiceRequestException
	{
		List<DSSFileTrans> lisFileTrans = new ArrayList<DSSFileTrans>();

		Map<String, List<String>> mapFileGuidList = new HashMap<String, List<String>>();

		for (String fileGuid : fileGuidList)
		{
			DSSFileInfo fileInfo = this.getFile(fileGuid);

			if (fileInfo == null)
			{
				throw new ServiceRequestException("ID_APP_FILE_NOT_EXIST", "file not exist");
			}

			if (mapFileGuidList.get(fileInfo.getStorageId()) != null)
			{
				if (!mapFileGuidList.get(fileInfo.getStorageId()).contains(fileGuid))
				{
					mapFileGuidList.get(fileInfo.getStorageId()).add(fileGuid);
				}
			}
			else
			{
				mapFileGuidList.put(fileInfo.getStorageId(), new ArrayList<String>());
				mapFileGuidList.get(fileInfo.getStorageId()).add(fileGuid);
			}

		}

		Set<String> lisKey = mapFileGuidList.keySet();
		for (String k : lisKey)
		{
			lisFileTrans.add(this.getTransFileStub().batchDownloadFile(mapFileGuidList.get(k)));
		}

		return lisFileTrans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#batchUploadFile(java.lang.String, java.util.List)
	 */
	@Override
	public DSSFileTrans batchUploadFile(List<String> fileGuidList, List<String> filePathList) throws ServiceRequestException
	{
		return this.getTransFileStub().batchUploadFile(fileGuidList, filePathList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#copyFile(dyna.common.bean.data.system.DSSFileInfo,
	 * dyna.common.bean.data.system.DSSFileInfo)
	 */
	@Tracked(description = TrackedDesc.COPY_FILE)
	@Override
	public void copyFile(String destFileGuid, String srcFileGuid) throws ServiceRequestException
	{
		this.getFileInfoStub().copyFile(destFileGuid, srcFileGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#copyFile(dyna.common.bean.data.ObjectGuid, java.lang.String[])
	 */
	@Tracked(description = TrackedDesc.COPY_FILE)
	@Override
	public void copyFile(ObjectGuid destObjectGuid, String... srcFileGuids) throws ServiceRequestException
	{
		this.getFileInfoStub().copyFile(destObjectGuid, Constants.isSupervisor(true, this), srcFileGuids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#copyInstanceFile(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.ObjectGuid)
	 */
	@Tracked(description = TrackedDesc.COPY_FILE)
	@Override
	public void copyFile(ObjectGuid destObjectGuid, ObjectGuid srcObjectGuid) throws ServiceRequestException
	{
		this.getFileInfoStub().copyFile(destObjectGuid, srcObjectGuid, Constants.isSupervisor(true, this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#detachFile(java.lang.String)
	 */
	// @Tracked(description = TrackedDesc.DETTACH_FILE, renderer = TRDSSFile.class)
	@Override
	public void detachFile(String fileGuid) throws ServiceRequestException
	{
		this.getInstFileStub().detachFile(fileGuid, false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#detachFile4Tab(java.lang.String)
	 */
	@Override
	public void detachFile4Tab(String fileGuid) throws ServiceRequestException
	{
		this.getAnyTableFileStub().detachFile4Tab(fileGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#detachAllFile4Tab(java.lang.String, java.lang.String)
	 */
	@Override
	public void detachFile4Tab(String tabName, String fkGuid) throws ServiceRequestException
	{
		this.getAnyTableFileStub().detachFile4Tab(tabName, fkGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#downloadFile(java.lang.String)
	 */
	@Tracked(description = TrackedDesc.DOWNLOAD_FILE, renderer = TRFileTransImpl.class)
	@Override
	public DSSFileTrans downloadFile(String fileGuid) throws ServiceRequestException
	{
		boolean supervisor = Constants.isSupervisor(true, this);
		if (supervisor)
		{
			return this.getTransFileStub().downloadFile(fileGuid, AuthorityEnum.DOWNLOADFILE);
		}
		else
		{
			return this.getTransFileStub().downloadFile(fileGuid, null);
		}
	}

	@Tracked(description = TrackedDesc.DOWNLOAD_FILE, renderer = TRFileTransImpl.class)
	@Override
	public DSSFileTrans downloadFile(String fileGuid, String procGuid) throws ServiceRequestException
	{
		return this.getTransFileStub().downloadFile(fileGuid, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#fileDeleted(java.lang.String)
	 */
	@Override
	public void fileDeleted(String fileTransGuid) throws ServiceRequestException
	{
		this.getTransFileStub().fileDeleted(fileTransGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#fileUploaded(java.lang.String)
	 */
	@Override
	public void fileUploaded(String fileTransGuid, long size, String md5) throws ServiceRequestException
	{
		this.getTransFileStub().fileUploaded(fileTransGuid, size, md5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#getFileTransByUserId(java.lang.String)
	 */
	@Override
	public DSSFileTrans getDSServerUser(String userId) throws ServiceRequestException
	{
		return this.getTransFileStub().getDSServerUser(userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#getFile(java.lang.String)
	 */
	@Override
	public DSSFileInfo getFile(String fileGuid) throws ServiceRequestException
	{
		if (StringUtils.isNullString(fileGuid))
		{
			return null;
		}

		if (fileGuid.startsWith(FILE_TYPE_ANYTABLE))
		{
			return this.getAnyTableFileStub().getFile(fileGuid);
		}
		else if (fileGuid.startsWith(FILE_TYPE_WF))
		{
			return this.getProcFileStub().getFile(fileGuid);
		}
		else
		{
			return this.getFileInfoStub().getFile(fileGuid);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#getFileTrans(java.lang.String)
	 */
	@Override
	public DSSFileTrans getFileTrans(String fileTransGuid) throws ServiceRequestException
	{
		return this.getTransFileStub().getFileTrans(fileTransGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#getFileType(java.lang.String)
	 */
	@Override
	public FileType getFileType(String fileTypeGuidOrIdOrExt) throws ServiceRequestException
	{
		return this.getFileInfoStub().getFileType(fileTypeGuidOrIdOrExt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#getStorage(java.lang.String)
	 */
	@Override
	public DSStorage getStorage(String storageId) throws ServiceRequestException
	{
		return this.getStorageStub().getStorage(storageId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#getStorageForGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public DSStorage getStorageForGroup(String groupGuid, String groupId) throws ServiceRequestException
	{
		return this.getStorageStub().getStorageForGroup(groupGuid, groupId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listFile(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getInstFileStub().listFile(objectGuid, searchCondition);
	}

	@Override
	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, int iterationId, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getInstFileStub().listFile(objectGuid, iterationId, searchCondition, Constants.isSupervisor(true, this), false);
	}
	
	@Override
	public List<DSSFileInfo> listFileByFileType(ObjectGuid objectGuid, int iterationId, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getInstFileStub().listFileByFileType(objectGuid, iterationId, searchCondition);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listFileTrans()
	 */
	@Override
	public List<DSSFileTrans> listFileTrans(SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getTransFileStub().listFileTrans(searchCondition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listFileTransDetail(java.lang.String)
	 */
	@Override
	public List<DSSFileTrans> listFileTransDetail(String fileTransMasterGuid) throws ServiceRequestException
	{
		return this.getTransFileStub().listFileTransDetail(fileTransMasterGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listStorage()
	 */
	@Override
	public List<DSStorage> listStorage() throws ServiceRequestException
	{
		return this.getStorageStub().listStorage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#setAsPrimaryFile(java.lang.String)
	 */
	@Tracked(description = TrackedDesc.SET_PRIMARY_FILE, renderer = TRDSSFile.class)
	@Override
	public void setAsPrimaryFile(String fileGuid) throws ServiceRequestException
	{
		this.getInstFileStub().setAsPrimaryFile(fileGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#uploadFile(java.lang.String)
	 */
	@Override
	public DSSFileTrans uploadFile(String fileGuid, String filePath) throws ServiceRequestException
	{
		return this.getTransFileStub().uploadFile(fileGuid, filePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listProcessFile(java.lang.String)
	 */
	@Override
	public List<DSSFileInfo> listProcessFile(String procRtGuid, String actrtGuid, int startNumber, String createUserGuid) throws ServiceRequestException
	{
		return this.getProcFileStub().listProcessFile(procRtGuid, actrtGuid, startNumber, createUserGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listFile(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DSSFileInfo> listFile(String tabName, String fkGuid) throws ServiceRequestException
	{
		return this.getAnyTableFileStub().listFile(tabName, fkGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#attachFile(java.lang.String, java.lang.String,
	 * dyna.common.bean.data.system.DSSFileInfo)
	 */
	@Tracked(description = TrackedDesc.ATTACH_PRC_FILE, renderer = TRDSSFileInfoImpl.class)
	@Override
	public DSSFileInfo attachFile(String procGuid, String actGuid, int startNumber, DSSFileInfo file) throws ServiceRequestException
	{
		return this.getProcFileStub().attachFile(procGuid, actGuid, startNumber, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#attachFile4Tab(java.lang.String, java.lang.String,
	 * dyna.common.bean.data.system.DSSFileInfo)
	 */
	@Override
	public DSSFileInfo attachFile4Tab(String tabName, String fkGuid, DSSFileInfo file) throws ServiceRequestException
	{
		return this.getAnyTableFileStub().attachFile4Tab(tabName, fkGuid, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#setFileAsType(java.lang.String, java.lang.String)
	 */
	@Tracked(description = TrackedDesc.SET_FILE_TYPE)
	@Override
	public void setAsFileType(String fileGuid, String fileTypeGuidOrIdOrExt) throws ServiceRequestException
	{
		this.getInstFileStub().setAsFileType(fileGuid, fileTypeGuidOrIdOrExt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listFileTypeByExtension(java.lang.String)
	 */
	@Override
	public List<FileType> listFileTypeByExtension(String extension) throws ServiceRequestException
	{
		return this.getFileInfoStub().listFileTypeByExtension(extension);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#downloadPreviewFile(dyna.common.bean.data.ObjectGuid)
	 */
	@Override
	public DSSFileTrans downloadPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		return this.getTransFileStub().downloadPreviewFile(objectGuid, iterationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#listFileType()
	 */
	@Override
	public List<FileType> listFileType() throws ServiceRequestException
	{
		return this.getFileInfoStub().listFileType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#uploadPreviewFile(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.system.DSSFileInfo)
	 */
	@Tracked(description = TrackedDesc.SET_PREVIEW_FILE, renderer = TRDSSFileInfoImpl.class)
	@Override
	public DSSFileTrans uploadPreviewFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException
	{
		return this.getTransFileStub().uploadPreviewFile(objectGuid, file, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#uploadIconFile(dyna.common.bean.data.ObjectGuid,
	 * dyna.common.bean.data.system.DSSFileInfo)
	 */
	@Override
	public DSSFileTrans uploadIconFile(ObjectGuid objectGuid, DSSFileInfo file) throws ServiceRequestException
	{
		return this.getTransFileStub().uploadIconFile(objectGuid, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#downloadIconFile(dyna.common.bean.data.ObjectGuid, int)
	 */
	@Override
	public DSSFileTrans downloadIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		return this.getTransFileStub().downloadIconFile(objectGuid, iterationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#detachIconFile(dyna.common.bean.data.ObjectGuid, int)
	 */
	@Override
	public void detachIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		this.getInstFileStub().detachIconFile(objectGuid, iterationId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.DSS#detachPreviewFile(dyna.common.bean.data.ObjectGuid, int)
	 */
	@Override
	public void detachPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		this.getInstFileStub().detachPreviewFile(objectGuid, iterationId);

	}

	@Override
	public DSSFileTrans downloadFile(String fileGuid, AuthorityEnum authorityEnum) throws ServiceRequestException
	{
		if (authorityEnum == null)
		{
			throw new ServiceRequestException("ID_APP_AUTH_DOWNLOAD_FILE_PARAMER", "permission denied, please check code");
		}

		boolean supervisor = Constants.isSupervisor(true, this);
		if (supervisor)
		{
			return this.getTransFileStub().downloadFile(fileGuid, authorityEnum);
		}
		else
		{
			return this.getTransFileStub().downloadFile(fileGuid, null);
		}
	}

	@Override
	public void detachFile(String fileTransGuid, List<String> fileNameList) throws ServiceRequestException
	{
		this.getInstFileStub().detachFile(fileTransGuid, fileNameList);
	}

	@Override
	public List<DSSFileInfo> listFile(ObjectGuid objectGuid, String procGuid, int iterationId, SearchCondition searchCondition) throws ServiceRequestException
	{
		return this.getInstFileStub().listFile(objectGuid, iterationId, searchCondition, Constants.isSupervisor(false, this), false);
	}

	@Override
	public boolean hasIconFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		return this.getFileInfoStub().hasIconFile(objectGuid, iterationId);
	}

	@Override
	public boolean hasPreviewFile(ObjectGuid objectGuid, int iterationId) throws ServiceRequestException
	{
		return this.getFileInfoStub().hasPreviewFile(objectGuid, iterationId);
	}

	@Override
	public void setLocalFilePath(String fileGuid, String fileClientPath) throws ServiceRequestException
	{
		this.getInstFileStub().setLocalFilePath(fileGuid, fileClientPath);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see dyna.net.service.brs.DSS#downloadFileNoAuthority(java.lang.String)
	// */
	// @Override
	// public DSSFileTrans downloadFileNoAuthority(String fileGuid) throws ServiceRequestException
	// {
	// return this.getTransFileStub().downloadFile(fileGuid, false);
	// }

	@Override
	public boolean checkUploadFiles(String transFileGuid) throws ServiceRequestException
	{
		return this.getTransFileStub().checkUploadFiles(transFileGuid);
	}

	@Override
	public List<String> checkAndDeleteFiles(String transFileGuid, boolean isDelete) throws ServiceRequestException
	{
		return this.getInstFileStub().checkAndDeleteFiles(transFileGuid, isDelete);
	}

	public String getSecondTranId()
	{
		return this.secondTranId;
	}

	@Override
	public String newTransactionId()
	{
		this.secondTranId = StringUtils.generateRandomUID(32);
		return super.newTransactionId();
	}

	@Override
	public void hasDownLoadFileAuthority(String fileGuid) throws ServiceRequestException
	{
		boolean supervisor = Constants.isSupervisor(true, this);
		if (supervisor)
		{
			this.getTransFileStub().hasDownLoadFileAuthority(fileGuid);
		}

	}

}

@Component
class TimeClearServerContextListener implements ServerContextListener
{
	private boolean isDeleteByLogic = true;
	@Autowired
	private ApplicationServerContext serverContext;
	@Autowired
	private ServiceContext serviceContext;

	public TimeClearServerContextListener(boolean isDeleteByLogic)
	{
		this.isDeleteByLogic = isDeleteByLogic;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContextListener#contextInitialized(dyna.app.server.context.ServerContext,
	 * dyna.app.server.context.ServiceContext)
	 */
	@Override
	public void contextInitialized()
	{
		Date firstTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstTime);

		Calendar firstTimeCalendar = Calendar.getInstance();
		firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 24, 0, 0);
		firstTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
		firstTime = firstTimeCalendar.getTime();
		long period = 365 * 24 * 60 * 60 * 1000;
		if (this.isDeleteByLogic)
		{
			period = 8 * 60 * 60 * 1000;
		}

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(new TransFielsScheduledTask(serviceContext), firstTime, period);
	}
}

class TransFielsScheduledTask extends AbstractScheduledTask
{
	private ServiceContext serviceContext = null;

	public TransFielsScheduledTask(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{
		try
		{
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());

			SystemDataService systemDataService = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			c.add(Calendar.YEAR, -1);

			filter.put(SystemObject.CREATE_TIME, c.getTime());
			systemDataService.delete(DSSFileTrans.class, filter, "deleteTranFiles");
		}
		catch (Throwable e)
		{
			DynaLogger.error("run Obsolete:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

		}

	}

}
