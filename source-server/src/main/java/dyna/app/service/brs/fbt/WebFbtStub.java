/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WebFbtStub
 * Qiuxq 2012-12-4
 */
package dyna.app.service.brs.fbt;

import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.FileOpenConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FileOpenToolTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Qiuxq
 * 
 */
@Component
public class WebFbtStub extends AbstractServiceStub<FBTSImpl>
{

	public boolean isFileCompareEnabled() throws ServiceRequestException
	{
		boolean returnValue = (getFileCompareConfig() != null);
		return returnValue;
	}

	public FileOpenConfig getFileCompareConfig() throws ServiceRequestException
	{
		// 比较时是网络版优先,其次取得桌面版第一个
		FileOpenConfig returnValue = null;
		List<FileOpenConfig> list = this.stubService.listFileOpenConfig();
		FileOpenConfig deskFileConfig = null;
		if (SetUtils.isNullList(list) == false)
		{
			for (FileOpenConfig fileconfig : list)
			{
				if (fileconfig.getToolType() == FileOpenToolTypeEnum.DESKTOPVERSION)
				{
					returnValue = fileconfig;
					if (deskFileConfig == null)
					{
						deskFileConfig = returnValue;
					}

				}
				if (fileconfig.getToolType() == FileOpenToolTypeEnum.NETVERSION)
				{
					if (!(StringUtils.isNullString(this.stubService.getWebSerVerInfo()) || StringUtils.isNullString(this.stubService.getWebServerFilePath())))
					{
						returnValue = fileconfig;
						break;
					}
				}
			}
		}
		if (returnValue != null && !returnValue.getToolType().equals(FileOpenToolTypeEnum.NETVERSION))
		{
			return deskFileConfig;
		}
		return returnValue;
	}

	public String getWebFileViewInfo(FoundationObject foundationObject, String guid, DSSFileInfo file) throws ServiceRequestException
	{
		List<DSSFileTrans> list = this.stubService.getFbtStub().listDSSFileTransByConfigGuid(foundationObject.getObjectGuid(), guid, file);
		if (SetUtils.isNullList(list) == false)
		{
			File viewPathfl = new File(this.stubService.getWebServerFilePath() + foundationObject.getObjectGuid().getGuid() + System.getProperty("file.separator"));
			if (!viewPathfl.exists())
			{
				viewPathfl.mkdir();
			}
			for (DSSFileTrans obj : list)
			{
				viewPathfl = new File(this.stubService.getWebServerFilePath() + foundationObject.getObjectGuid().getGuid() + System.getProperty("file.separator")
						+ obj.getFileName());
				if (viewPathfl.exists())
				{
					viewPathfl.delete();
				}
				// TODO duanll DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), obj, viewPathfl.getPath(), FileOperateEnum.DOWNLOAD, true);
			}
			String str = this.stubService.getWebSerVerInfo() + " \"server://" + foundationObject.getObjectGuid().getGuid() + "/" + file.getName() + "\"";
			return str;
		}
		return null;
	}

	public String getWebFileCompareInfo(String guid, ObjectGuid fileObjectGuid1, DSSFileInfo file1, ObjectGuid fileObjectGuid2, DSSFileInfo file2) throws ServiceRequestException
	{
		List<DSSFileTrans> list = this.stubService.getFbtStub().listDSSFileTransByConfigGuid(fileObjectGuid1, guid, file1);
		if (SetUtils.isNullList(list) == false)
		{
			File viewPathfl = new File(this.stubService.getWebServerFilePath() + fileObjectGuid1.getGuid() + System.getProperty("file.separator"));
			if (!viewPathfl.exists())
			{
				viewPathfl.mkdir();
			}
			for (DSSFileTrans obj : list)
			{
				viewPathfl = new File(this.stubService.getWebServerFilePath() + fileObjectGuid1.getGuid() + System.getProperty("file.separator") + obj.getFileName());
				if (viewPathfl.exists())
				{
					viewPathfl.delete();
				}
				// TODO duanll DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), obj, viewPathfl.getPath(), FileOperateEnum.DOWNLOAD, true);
			}
			list = this.stubService.getFbtStub().listDSSFileTransByConfigGuid(fileObjectGuid2, guid, file2);
			if (SetUtils.isNullList(list) == false)
			{
				File viewPathfl1 = new File(this.stubService.getWebServerFilePath() + fileObjectGuid2.getGuid() + System.getProperty("file.separator"));
				if (!viewPathfl1.exists())
				{
					viewPathfl1.mkdir();
				}
				for (DSSFileTrans obj : list)
				{
					viewPathfl = new File(this.stubService.getWebServerFilePath() + fileObjectGuid2.getGuid() + System.getProperty("file.separator") + obj.getFileName());
					if (viewPathfl.exists())
					{
						viewPathfl.delete();
					}
					// TODO duanll DSSFileTransProgress.startAndWaitTransmitSilently(JOptionPane.getRootFrame(), obj, viewPathfl.getPath(), FileOperateEnum.DOWNLOAD, true);
				}
				String str = this.stubService.getWebSerVerInfo() + " \"server://" + fileObjectGuid1.getGuid() + "/" + file1.getName() + "\"" + " \"server://"
						+ fileObjectGuid2.getGuid() + "/" + file2.getName() + "\"";
				return str;
			}
		}
		return null;
	}

}
