/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UploaderImpl
 * Wanglei 2010-12-3
 */
package dyna.net.syncfile.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import dyna.common.exception.ServiceRequestException;
import dyna.common.util.CopyStreamListener;
import dyna.common.util.FileUtils;

/**
 * 文件上传器的实现
 * 
 * @author Wanglei
 * 
 */
public class UploaderImpl implements Uploader
{

	private static final long	serialVersionUID	= -7760210938134550684L;
	private String				host				= null;
	private int					port				= 0;
	private String				session				= null;

	public UploaderImpl(String host, int port, String session)
	{
		this.host = host;
		this.port = port;
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see dyna.net.syncfile.upload.Uploader#upload(java.io.InputStream)
	 */
	@Override
	public void upload(InputStream input, long size) throws ServiceRequestException
	{
		this.upload(input, size, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.upload.Uploader#upload(java.io.InputStream)
	 */
	@Override
	public void upload(InputStream input, long size, CopyStreamListener listener) throws ServiceRequestException
	{
		Socket s = null;
		OutputStream os = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		try
		{
			s = new Socket(this.host, this.port);
			os = s.getOutputStream();

			writer = new PrintWriter(new OutputStreamWriter(os));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));

			writer.println(this.session + ";" + size);// send session
			writer.flush();

			String ack = reader.readLine();
			if (!"OK".equals(ack))
			{
				throw new Exception("invalid file transfer session");
			}

			byte[] buffer = new byte[FileUtils.BUFFER];
			long total = 0l;
			int readLen = 0;

			while ((readLen = input.read(buffer, 0, FileUtils.BUFFER)) != -1)
			{
				os.write(buffer, 0, readLen);
				total += readLen;
				if (listener != null)
				{
					listener.bytesTransferred(total, readLen, size);
				}
			}

			// Util.copyStream(input, os, FileUtils.BUFFER, size, listener);
			Thread.sleep(500);
			ack = reader.readLine();
			if (!"SUC".equals(ack))
			{
				throw new Exception("server error occur");
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", "failed to upload file", e);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
				if (reader != null)
				{
					reader.close();
				}
				if (os != null)
				{
					os.close();
				}
				if (s != null)
				{
					s.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public String getSession()
	{
		return session;
	}

}
