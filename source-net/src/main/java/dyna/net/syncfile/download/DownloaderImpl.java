/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DownloaderImpl
 * Wanglei 2010-12-8
 */
package dyna.net.syncfile.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.CopyStreamListener;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;

/**
 * 文件下载实现
 * 
 * @author Wanglei
 * 
 */
public class DownloaderImpl implements Downloader
{

	private static final long	serialVersionUID	= 4360830269707180530L;

	private String				host				= null;
	private int					port				= 0;
	private String				session				= null;

	public DownloaderImpl(String host, int port, String session)
	{
		this.host = host;
		this.port = port;
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.download.Downloader#download(java.io.OutputStream)
	 */
	public void download(OutputStream output) throws ServiceRequestException
	{
		this.download(output, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.syncfile.download.Downloader#download(java.io.OutputStream)
	 */
	@Override
	public void download(OutputStream output, CopyStreamListener listener) throws ServiceRequestException
	{
		Socket s = null;
		InputStream is = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		try
		{
			s = new Socket(this.host, this.port);
			is = s.getInputStream();

			writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.println(this.session);// send session
			writer.flush();

			long size = 0l;
			String ack = readLine(is);
			String[] ss = StringUtils.splitString(ack);
			if (ss != null && ss.length == 2)
			{
				ack = ss[0];
				size = Long.valueOf(ss[1]);
			}

			if (!"OK".equals(ack))
			{
				throw new ServiceRequestException("invalid file transfer session");
			}

			if (size == 0)
			{
				DynaLogger.debug("file not exist");
				return;
			}

			DynaLogger.debug(size);
			byte[] buffer = new byte[FileUtils.BUFFER];
			long total = 0l;
			int readLen = 0;

			while (true)
			{
				if (is.available() > 0)
				{
					if (size - total >= FileUtils.BUFFER)
					{
						readLen = is.read(buffer, 0, FileUtils.BUFFER);
					}
					else
					{
						readLen = is.read(buffer, 0, (int) (size - total));
					}
					if (readLen == -1)
					{
						break;
					}
					output.write(buffer, 0, readLen);
					total += readLen;
					if (listener != null)
					{
						listener.bytesTransferred(total, readLen, size);
					}
					DynaLogger.debug(total);
					if (total == size)
					{
						break;
					}
				}
			}

			// Util.copyStream(is, output, FileUtils.BUFFER, size, listener);
			Thread.sleep(500);

			reader = new BufferedReader(new InputStreamReader(is));
			ack = reader.readLine();
			if (!"SUC".equals(ack))
			{
				DynaLogger.info(ack);
				DynaLogger.info(size);
				throw new Exception("server error occur");
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", "failed to download file", e);
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
				if (is != null)
				{
					is.close();
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
	
    private String readLine(InputStream is) throws IOException
    {
    	boolean eof=false;
    	char[] buffer = new char[8192];
		int i=0;
		while(eof==false)
    	{
    		int b= is.read();
    		if (b==10)
    		{
    			eof=true;
    		}
    		else if (b==13)
    		{
    		}
    		else
    		{
    			buffer[i]=(char) b;
    			i++;
    		}
    	}
    	return String.copyValueOf(buffer, 0, i);
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
