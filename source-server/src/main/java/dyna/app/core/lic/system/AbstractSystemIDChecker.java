/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractSystemIDChecker
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.system;

import dyna.app.server.GenericServer;
import dyna.common.conf.ConfigurableConnToGuardServerImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.log.DynaLogger;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
public abstract class AbstractSystemIDChecker implements SystemIDChecker
{
	protected String	osName			= null;
	protected String	vMCheckCommand	= null;
	protected boolean	isVM			= false;

	protected AbstractSystemIDChecker(String osName)
	{
		this.osName = osName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.lic.system.SystemIDChecker#getSystemIdentification(java.util.List)
	 */
	@Override
	public String getSystemIdentification(List<String> licenseRawData)
	{
		if (!StringUtils.isNullString(vMCheckCommand))
		{
			this.checkVM();
		}
		String systemID = "";
		if (this.isVM)
		{
			systemID = this.updateGuardServerInfo();
		}
		else
		{
			systemID = this.lookupSystemIdentification(licenseRawData);
		}
		return systemID;
	}

	private String updateGuardServerInfo()
	{
		List<String> ipList=this.lookupSystemIP();
		if (SetUtils.isNullList(ipList)==false)
		{
			for (String ip:ipList)
			{
				String mac=this.updateGuardServerInfo(ip);
				if (StringUtils.isNullString(mac)==false)
				{
					return mac;
				}
			}
		}
		return null;
	}

	public void checkVM()
	{
		File cmdfile = FileUtils.newFileEscape(EnvUtils.getConfRootPath() + this.vMCheckCommand);
		if (cmdfile.exists())
		{
			Process p = null;

			try
			{
				p = Runtime.getRuntime().exec("\""+cmdfile.getPath()+"\"");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = "", currentLine = "";

			try
			{
				while ((currentLine = in.readLine()) != null)
				{
					currentLine = currentLine.trim();
					output += currentLine + "\n";
				}
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();
			}
			if (output.indexOf("Virtual Machine") > -1)
			{
				this.isVM = true;
			}
			else
			{
				this.isVM = false;
			}
		}
		else
		{
			this.isVM = true;
		}
	}

	protected String updateGuardServerInfo(String ip)
	{
		String key = getPrivateKey();
		String mac = getInfoFromGuardServer(ip,key);
		if (StringUtils.isNullString(mac) == false)
		{
			mac = this.decodeGuardServerInfo(mac, key);
		}
		return mac;
	}

	protected String getPrivateKey()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String str = sdf.format(new Date());
		return ("PLM" + str);
	}

	protected String getInfoFromGuardServer(String ip,String key)
	{
		ConfigurableConnToGuardServerImpl conToGsConfig = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4ConnToGS().getConfigurable();
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		try
		{
			socket = new Socket(conToGsConfig.getLookupServiceHost(), conToGsConfig.getLookupServicePort(),(new InetSocketAddress(ip,0)).getAddress(),0);
			socket.setSoTimeout(30000);
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 8192));
			in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), 8192));
			String str = socket.getLocalAddress().getHostAddress();
			if (!StringUtils.isNullString(ip))
			{
				str=ip;
			}
			str = str + ";" + key;
			DynaLogger.debug("Send to Guard:"+str);
			out.write(str.getBytes(), 0, str.length());
			out.flush();
			byte[] strb = new byte[64];
			int len = in.read(strb);
			str = new String(strb, 0, len);
			out.close();
			out = null;
			in.close();
			in = null;
			socket.close();
			socket = null;
			DynaLogger.debug("Recive from Guard:"+str);
			return str;
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				in = null;
			}
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				out = null;

			}
			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				socket = null;

			}
		}
		return null;
	}

	protected String decodeGuardServerInfo(String str, String key)
	{
		Base64 base64 = new Base64();
		byte[] encode = base64.decode(str.getBytes());
		byte[] encode2 = key.getBytes();
		for (int j = 0; j < encode.length; j++)
		{
			for (byte b : encode2)
			{
				encode[j] = (byte) (encode[j] ^ b);
			}
		}
		String rstr = new String(encode);
		if (rstr!=null)
		{
			rstr=rstr.toUpperCase();
		}
		DynaLogger.debug("Decode Guard info:"+rstr);
		if (rstr != null && rstr.length() == 32 && rstr.matches("^[0-9A-F]+$"))
		{
			return rstr = rstr.substring(8, 24);
		}
		else
		{
			return rstr = null;
		}
	}

	public boolean isVM()
	{
		return isVM;
	}

	protected abstract String lookupSystemIdentification(List<String> licenseRawData);
	
	protected  List<String> lookupSystemIP()
	{
		List<String> res = new ArrayList<String>();
		
		Enumeration<NetworkInterface> netInterfaces;
		try
		{
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements())
			{
				NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
				Enumeration<InetAddress> nii = ni.getInetAddresses();
				while (nii.hasMoreElements())
				{
					ip = (InetAddress) nii.nextElement();
					if (ip.getHostAddress().indexOf(":") == -1)
					{
						if ("127.0.0.1".equals(ip.getHostAddress())==false)
						{
							res.add(ip.getHostAddress());
						}
					}
				}
			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();

		}
		return res;

	}

}
