/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UDSImpl
 * Wanglei 2011-9-13
 */
package dyna.app.service.das;

import dyna.app.service.DataAccessService;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.service.das.UDS;
import dyna.net.syncfile.transfer.TransferEnum;

import java.io.File;

/**
 * @author Wanglei
 *
 */
public class UDSImpl extends DataAccessService implements UDS
{

	/* (non-Javadoc)
	 * @see dyna.net.service.das.UDS#shouldUpdate(long)
	 */
	@Override
	public long shouldUpdate(long lastUpdateIndex)
	{
		return shouldUpdate(TransferEnum.DOWNLOAD_UDPKG,lastUpdateIndex);
	}
	
	/* (non-Javadoc)
	 * @see dyna.net.service.das.UDS#shouldUpdate(java.util.Date)
	 */
	@Override
	public long shouldUpdate(TransferEnum transferEnum,long lastUpdateIndex)
	{
		File file=new File(UDS.UDPKG_FOLDER + transferEnum.getServerFileName());
		if (!file.exists())
		{
			DynaLogger.debug("no update package");
			return 0;
		}
		file=new File(UDS.UDPKG_FILE);
		if (!file.exists())
		{
			DynaLogger.debug("no update xml");
			return 0;
		}

		long lastModified = getUpdateIndex(transferEnum);
		if (lastModified > lastUpdateIndex)
		{
			DynaLogger.info("found update package, ready for updating");
			return lastModified;
		}
		return 0;
	}
	
	private long getUpdateIndex(TransferEnum transferEnum)
	{
		ConfigLoaderDefaultImpl xConfigLoaderDefaultImpl=new ConfigLoaderDefaultImpl();
		xConfigLoaderDefaultImpl.load(UDS.UDPKG_FILE);
		String value=xConfigLoaderDefaultImpl.getConfigurable().getElementValue("update."+transferEnum.name());
		if (StringUtils.isNullString(value)==false)
		{
			try
			{
				return Long.parseLong(value);
			}
			catch(Exception e)
			{
				DynaLogger.info("update xml config error");
			}
		}
		return 0;
	}
	
}
