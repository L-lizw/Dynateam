/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: THUDPKGImpl 客户端更新包下载
 * Wanglei 2011-9-13
 */
package dyna.net.syncfile.transfer;

import java.io.File;

import dyna.net.service.das.UDS;

/**
 * 客户端更新包下载
 * 
 * @author Wanglei
 * 
 */
public class THUDPKGImpl extends AbstractTransferHelper
{
	public THUDPKGImpl(TransferEnum transferEnum)
	{
		super(UDS.UDPKG_FOLDER,transferEnum.getServerFileName() , false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.transfer.AbstractTransferHelper#onStart()
	 */
	@Override
	public void onStart() throws Exception
	{
		this.file =new File(UDS.UDPKG_FOLDER + this.fileName);
	}

}
