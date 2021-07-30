/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SyncFileServer
 * Wanglei 2010-12-3
 */
package dyna.net.syncfile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dyna.common.util.StringUtils;
import dyna.net.syncfile.transfer.FileTransfer;
import dyna.net.syncfile.transfer.TransferHelper;

/**
 * 文件同步服务器
 * 
 * @author Wanglei
 * 
 */
public class SyncFileServer
{

	private int							port			= 5900;
	private boolean			isStarted	= false;
	private ExecutorService				exec			= Executors.newCachedThreadPool();
	private InetAddress  address=null;
	private Map<String, TransferHelper>	transferMap	= Collections
	.synchronizedMap(new HashMap<String, TransferHelper>());

	public SyncFileServer(int port)
	{
		if (port != 0)
		{
			this.port = port;
		}
		try
		{
			address = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public SyncFileServer(int port,InetAddress address)
	{
		if (port != 0)
		{
			this.port = port;
		}
		this.address=address;
	}

	public String getHostAddress()
	{
		return address.getHostAddress();
	}

	public int getPort()
	{
		return this.port;
	}

	public TransferHelper getTransferHelper(String transferSession)
	{
		return this.transferMap.remove(transferSession);
	}

	public String requestSession(TransferHelper helper)
	{
		String session = StringUtils.generateRandomUID(0);
		this.transferMap.put(session, helper);
		return session;
	}

	/**
	 * start server
	 */
	public void start()
	{
		if (this.isStarted)
		{
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					ServerSocket server = new ServerSocket(SyncFileServer.this.port);
					SyncFileServer.this.isStarted = true;
					while (SyncFileServer.this.isStarted)
					{
						Socket socket = server.accept();
						SyncFileServer.this.exec.execute(new FileTransfer(SyncFileServer.this, socket));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * stop server
	 */
	public void stop()
	{
		if (!this.isStarted)
		{
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run()
			{
				SyncFileServer.this.exec.shutdown();
				try
				{
					while (!SyncFileServer.this.exec.isTerminated())
					{
						Thread.sleep(100);
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}
