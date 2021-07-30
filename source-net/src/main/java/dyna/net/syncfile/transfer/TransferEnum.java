/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TransferEnum
 * Wanglei 2010-12-9
 */
package dyna.net.syncfile.transfer;

/**
 * 文件传输枚举
 * 
 * @author Wanglei
 * 
 */
public enum TransferEnum
{

	/**
	 * 传输建模数据
	 */
	OM("", "", "", "", ""),

	/**
	 * CLASSFICATION传输数据
	 */
	CF("", "", "", "", ""),
	/**
	 * 从数据层传输传输图标文件, 用于应用层从数据层下载模型图标
	 */
	DOWNLOAD_ICON_DS("", "", "", "", ""),

	/**
	 * 从应用层传输图标文件, 用于客户端从应用层下载模型图标
	 */
	DOWNLOAD_ICON_AS("", "", "", "", ""),

	/**
	 * 客户端从应用层下载更新包
	 */
	DOWNLOAD_UDPKG("udpkg", "udpkg.zip", "client-udpkg.zip", "update.dat", "DigiWin PLM.exe"),
	/**
	 * 编码器从应用层下载更新包
	 */
	DOWNLOAD_UDCPKG("udpkg", "udpkg.zip", "nrm-udpkg.zip", "update.dat", "Numbering Rule Management.exe"),

	/**
	 * CAD客户端从应用层下载更新包
	 */
	DOWNLOAD_UDCADPKG("udpkg", "udpkg.zip", "cad-udpkg.zip", "update.dat", ""),

	/**
	 * 从数据层传输传输报表文件
	 */
	DOWNLOAD_REPORT_DS("", "", "", "", ""), 
	DOWNLOAD_SERVER("", "", "", "", "");

	private String	folderName		= null;
	private String	fileName		= null;
	private String	serverFileName	= null;
	private String	dateFile		= null;
	private String	productorName	= null;

	private TransferEnum(String folderName, String fileName, String serverFileName, String dateFile,
			String productorName)
	{
		this.folderName = folderName;
		this.fileName = fileName;
		this.serverFileName = serverFileName;
		this.dateFile = dateFile;
		this.productorName = productorName;
	}

	/**
	 * @return the folderName
	 */
	public String getFolderName()
	{
		return this.folderName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName()
	{
		return this.fileName;
	}

	/**
	 * @return the serverFileName
	 */
	public String getServerFileName()
	{
		return this.serverFileName;
	}

	/**
	 * @return the dateFile
	 */
	public String getDateFile()
	{
		return this.dateFile;
	}

	public static TransferEnum getEnum(String name)
	{
		for (TransferEnum showEnum : TransferEnum.values())
		{
			if (showEnum.name().equals(name))
			{
				return showEnum;
			}
		}
		return null;
	}

	/**
	 * @return the productorName
	 */
	public String getProductorName()
	{
		return this.productorName;
	}

}
