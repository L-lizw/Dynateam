/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileTransfer
 * Wanglei 2010-12-3
 */
package dyna.net.syncfile.transfer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import dyna.common.log.DynaLogger;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;
import dyna.net.syncfile.SyncFileServer;

/**
 * 文件传输实例
 * 
 * @author Wanglei
 * 
 */
public class FileTransfer implements Runnable
{

	private SyncFileServer	fileServer	= null;
	private Socket	socket	= null;

	public FileTransfer(SyncFileServer server, Socket socket)
	{
		this.fileServer = server;
		this.socket = socket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		InputStream is = null;
		OutputStream os = null;
		BufferedReader reader = null;
		FileOutputStream fileOutput = null;
		FileInputStream fileInput = null;
		PrintWriter writer = null;

		try
		{
			is = this.socket.getInputStream();
			os = this.socket.getOutputStream();

			reader = new BufferedReader(new InputStreamReader(is));

			String session = reader.readLine();
			long size = 0l;
			String[] ss = StringUtils.splitString(session);
			if (ss != null && ss.length == 2)
			{
				session = ss[0];
				size = Long.valueOf(ss[1]);
			}
			writer = new PrintWriter(new OutputStreamWriter(os));

			TransferHelper helper = this.fileServer.getTransferHelper(session);
			if (helper != null)
			{
				String sendStr = "OK";
				helper.onStart();
				if (!helper.isUpload())
				{
					size = helper.getFile() == null ? 0 : helper.getFile().length();
					sendStr += ";" + size;
				}
				writer.println(sendStr);// acknowledge OK!
				writer.flush();
			}
			else
			{
				writer.println("NO");// acknowledge NO!
				writer.flush();
				return;
			}

			if (helper.isUpload())
			{
				fileOutput = new FileOutputStream(helper.getFile());
				this.doUpload(is, fileOutput, size);
			}
			else
			{
				if (helper.getFile() == null || !helper.getFile().exists())
				{
					return;
				}
				Thread.sleep(2000);
				fileInput = new FileInputStream(helper.getFile());
				this.doDownload(os, fileInput);
			}
			helper.onFinish();
			writer.flush();
			Thread.sleep(5000);
			writer.println("SUC");
			writer.flush();
			Thread.sleep(10000);
			System.out.println("SUC");
		}
		catch (Exception e)
		{
			writer.println("ERR");
			writer.flush();
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fileInput != null)
				{
					fileInput.close();
				}
				if (fileOutput != null)
				{
					fileOutput.close();
				}
				if (os != null)
				{
					os.close();
				}
				if (is != null)
				{
					is.close();
				}
				if (reader != null)
				{
					reader.close();
				}
				if (this.socket != null)
				{
					this.socket.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	private void doUpload(InputStream is, FileOutputStream fileOutput, long size) throws Exception
	{
		byte[] buffer = new byte[FileUtils.BUFFER];
		int readLen = 0;
		long total = 0l;

		DynaLogger.debug("start upload");
		while ((readLen = is.read(buffer, 0, FileUtils.BUFFER)) != -1)
		{
			fileOutput.write(buffer, 0, readLen);
			fileOutput.flush();
			total += readLen;
			if (total == size)
			{
				break;
			}
		}
		// Util.copyStream(is, fileOutput, FileUtils.BUFFER, size, null);
		DynaLogger.debug("finish upload");
	}

	private void doDownload(OutputStream os, FileInputStream fileInput) throws Exception
	{
		byte[] buffer = new byte[FileUtils.BUFFER];
		int readLen = 0;

		DynaLogger.debug("start download");
		while ((readLen = fileInput.read(buffer, 0, FileUtils.BUFFER)) != -1)
		{
			os.write(buffer, 0, readLen);
			os.flush();
			Thread.sleep(10);
		}
		// Util.copyStream(fileInput, os, FileUtils.BUFFER);
		DynaLogger.debug("finish download");
	}

}
