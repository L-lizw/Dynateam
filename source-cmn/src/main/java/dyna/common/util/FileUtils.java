/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FileUtils
 * Wanglei 2010-10-18
 */
package dyna.common.util;

import dyna.common.log.DynaLogger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

/**
 * @author Wanglei
 * 
 */
public class FileUtils
{
	public static final int		BUFFER	= 8192;
	public static final long	FS_K	= 1024;
	public static final long	FS_M	= FS_K * FS_K;

	/**
	 * 获取文件名后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtention(String fileName)
	{
		if (StringUtils.isNullString(fileName))
		{
			return "";
		}
		int idx = fileName.lastIndexOf('.');
		if (idx == -1)
		{
			return "";
		}
		return fileName.substring(idx + 1);
	}

	public static boolean makeDirectories(final String path, boolean isFile)
	{
		File file = newFileEscape(path);
		if (file.exists())
		{
			return true;
		}

		File dir = null;
		if (isFile)
		{
			dir = file.getParentFile();
			if (dir == null)
			{
				return true;
			}

			if (dir.exists())
			{
				return true;
			}
		}
		else
		{
			dir = file;
		}

		return dir.mkdirs();
	}

	/**
	 * 
	 * @param srcFilePath
	 * @param destFileName
	 * @return <code>null</code> 重命名的文件不存在; <code>false</code> 失败
	 */
	public static Boolean rename(final String srcFilePath, final String destFileName)
	{
		File file = newFileEscape(srcFilePath);
		if (!file.exists())
		{
			return null;
		}

		String temp = destFileName;
		File parent = file.getParentFile();
		if (parent != null)
		{
			temp = parent.getAbsolutePath() + "/" + temp;
		}

		File renameFile = newFileEscape(temp);

		return file.renameTo(renameFile);
	}

	private static long countFileTotalInDirectory(File dir)
	{
		long total = 1;
		if (!dir.isDirectory())
		{
			return total;
		}

		File[] files = dir.listFiles();
		total = files.length;
		for (File file : files)
		{
			total += countFileTotalInDirectory(file);
		}
		return total;
	}

	/**
	 * 文件对文件, 文件对目录, 目录对目录进行拷贝
	 * 
	 * @param srcFileOrDirectory
	 * @param destFileOrDirectory
	 * @throws Exception
	 */
	public static void copyFile(File srcFileOrDirectory, File destFileOrDirectory) throws Exception
	{
		copyFile(srcFileOrDirectory, destFileOrDirectory, true);
	}

	/**
	 * 文件对文件, 文件对目录, 目录对目录进行拷贝
	 * 
	 * @param srcFileOrDirectory
	 * @param destFileOrDirectory
	 * @param withDirectory
	 *            目录对目录拷贝时, 是否同时复制源目录
	 */
	public static void copyFile(File srcFileOrDirectory, File destFileOrDirectory, boolean withDirectory) throws Exception
	{
		copyFile(srcFileOrDirectory, destFileOrDirectory, withDirectory, null);
	}

	public static void copyFile(File srcFileOrDirectory, File destFileOrDirectory, boolean withDirectory, CopyFileListener listener) throws Exception
	{
		// 文件或者文件夹不存在
		if (!srcFileOrDirectory.exists())
		{
			return;
		}

		// 源文件是目录, 目标文件不是目录
		if (srcFileOrDirectory.isDirectory() && destFileOrDirectory.isFile())
		{
			return;
		}

		long fileTotal = countFileTotalInDirectory(srcFileOrDirectory);

		if (srcFileOrDirectory.isDirectory() && !withDirectory)
		{
			if (!destFileOrDirectory.exists())
			{
				destFileOrDirectory.mkdirs();
			}
			for (File file : srcFileOrDirectory.listFiles())
			{
				doCopy(file, destFileOrDirectory, fileTotal, listener);
			}
		}
		else
		{
			doCopy(srcFileOrDirectory, destFileOrDirectory, fileTotal, listener);
		}
	}

	private static void doCopy(File srcFileOrDirectory, File destFileOrDirectory, long fileTotal, CopyFileListener listener) throws Exception
	{
		if (listener != null)
		{
			listener.fileCopied(srcFileOrDirectory, fileTotal);
		}

		if (srcFileOrDirectory.isDirectory())
		{
			doCopyDirectory(srcFileOrDirectory, destFileOrDirectory.getAbsolutePath(), fileTotal, listener);
		}
		else if (destFileOrDirectory.isDirectory())
		{
			doCopyFile(srcFileOrDirectory, newFileEscape(destFileOrDirectory.getAbsolutePath() + "/" + srcFileOrDirectory.getName()));
		}
		else
		{
			File parentFile = destFileOrDirectory.getParentFile();
			if (parentFile != null && !parentFile.exists())
			{
				parentFile.mkdirs();
			}

			doCopyFile(srcFileOrDirectory, destFileOrDirectory);
		}
	}

	private static void doCopyFile(File srcFile, File destFile) throws Exception
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		FileChannel inChannel = null;
		FileChannel outChannel = null;

		try
		{
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);

			inChannel = in.getChannel();
			outChannel = out.getChannel();

			ByteBuffer bb = ByteBuffer.allocate(BUFFER);

			while (inChannel.read(bb) != -1)
			{
				bb.flip();
				outChannel.write(bb);
				bb.clear();
			}
		}
		finally
		{
			if (inChannel != null)
			{
				inChannel.close();
			}
			if (outChannel != null)
			{
				outChannel.close();
			}
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
		}
	}

	private static void doCopyDirectory(File srcDir, String baseDir, long fileTotal, CopyFileListener listener) throws Exception
	{
		String destDirName = baseDir + "/" + srcDir.getName();
		File destFile = newFileEscape(destDirName);
		if (!destFile.exists())
		{
			destFile.mkdirs();
		}

		File[] fileList = srcDir.listFiles();
		for (File file : fileList)
		{
			doCopy(file, destFile, fileTotal, listener);
		}
	}

	/**
	 * 压缩zip文件
	 * 
	 * @param fileOrDirectory
	 *            需要压缩的文件或目录
	 * @param toZipFile
	 *            需要产生目标压缩文件
	 * @throws Exception
	 */
	public static void compress(File fileOrDirectory, File toZipFile) throws Exception
	{
		compress(fileOrDirectory, toZipFile, true);
	}

	/**
	 * 压缩指定文件zip文件
	 * 
	 * @param fileOrDirectory
	 *            需要压缩的文件或目录
	 * @param toZipFile
	 *            需要产生目标压缩文件
	 * @param withDirectory
	 *            是否包含当前文件夹
	 */
	public static void compress(File[] fileOrDirectorys, File toZipFile, boolean withDirectory) throws Exception
	{
		ZipOutputStream out = null;
		if (fileOrDirectorys == null)
		{
			return;
		}
		out = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(toZipFile), new CRC32()));
		try
		{
			for (File fileOrDirectory : fileOrDirectorys)
			{

				if (!fileOrDirectory.exists())
				{
					continue;
				}

				if (fileOrDirectory.isDirectory() && !withDirectory)
				{
					for (File file : fileOrDirectory.listFiles())
					{
						doCompress("", file, out);
					}
				}
				else
				{
					doCompress("", fileOrDirectory, out);
				}

			}
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	/**
	 * 压缩指定文件zip文件
	 * 
	 * @param fileOrDirectory
	 *            需要压缩的文件或目录
	 * @param toZipFile
	 *            需要产生目标压缩文件
	 * @param withDirectory
	 *            是否包含当前文件夹
	 */
	public static void compress(String[] baseDirs, File[] fileOrDirectorys, File toZipFile, boolean withDirectory) throws Exception
	{
		ZipOutputStream out = null;
		if (fileOrDirectorys == null)
		{
			return;
		}
		out = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(toZipFile), new CRC32()));
		try
		{
			for (int i = 0; i < fileOrDirectorys.length; i++)
			{

				File fileOrDirectory = fileOrDirectorys[i];
				if (!fileOrDirectory.exists())
				{
					continue;
				}

				if (fileOrDirectory.isDirectory() && !withDirectory)
				{
					for (File file : fileOrDirectory.listFiles())
					{
						doCompress(baseDirs[i] == null ? "" : baseDirs[i], file, out);
					}
				}
				else
				{
					doCompress(baseDirs[i] == null ? "" : baseDirs[i], fileOrDirectory, out);
				}

			}
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	/**
	 * 压缩zip文件
	 * 
	 * @param fileOrDirectory
	 *            需要压缩的文件或目录
	 * @param toZipFile
	 *            需要产生目标压缩文件
	 * @param withDirectory
	 *            是否包含当前文件夹
	 */
	public static void compress(File fileOrDirectory, File toZipFile, boolean withDirectory) throws Exception
	{
		if (!fileOrDirectory.exists())
		{
			return;
		}

		ZipOutputStream out = null;
		try
		{
			out = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(toZipFile), new CRC32()));

			if (fileOrDirectory.isDirectory() && !withDirectory)
			{
				for (File file : fileOrDirectory.listFiles())
				{
					doCompress("", file, out);
				}
			}
			else
			{
				doCompress("", fileOrDirectory, out);
			}

		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}

	}

	private static void doCompress(String baseDir, File fileOrDirectory, ZipOutputStream out) throws Exception
	{
		if (fileOrDirectory.isDirectory())
		{
			compressDirectory(baseDir + fileOrDirectory.getName() + "/", fileOrDirectory, out);
		}
		else if (fileOrDirectory.isFile())
		{
			compressFile(baseDir, fileOrDirectory, out);
		}
	}

	private static void compressFile(String baseDir, File file, ZipOutputStream zipOutput) throws Exception
	{
		BufferedInputStream bis = null;
		try
		{
			bis = new BufferedInputStream(new FileInputStream(file));
			ZipEntry entry = new ZipEntry(baseDir + file.getName());
			zipOutput.putNextEntry(entry);

			byte[] buf = new byte[BUFFER];
			int readLen = 0;
			while ((readLen = bis.read(buf, 0, BUFFER)) != -1)
			{
				zipOutput.write(buf, 0, readLen);
			}
		}
		finally
		{
			if (bis != null)
			{
				bis.close();
			}
		}
	}

	private static void compressDirectory(String baseDir, File directory, ZipOutputStream zipOutput) throws Exception
	{
		File[] files = directory.listFiles();
		if (files.length == 0)
		{
			// handle empty folder
			zipOutput.putNextEntry(new ZipEntry(baseDir + ""));
			return;
		}
		for (File file : files)
		{
			doCompress(baseDir, file, zipOutput);
		}
	}

	/**
	 * 解压缩zip文件到当前文件夹
	 * 
	 * @param zipFilePath
	 * @param toDirectory
	 * @throws Exception
	 */
	public static void decompress(String zipFilePath, File toDirectory) throws Exception
	{
		decompress(zipFilePath, toDirectory, false);
	}

	/**
	 * 解压缩zip文件
	 * 
	 * @param zipFile
	 *            zip文件路径
	 * @param toDirectory
	 *            解压到的文件夹
	 * @param createFolder
	 *            是否创建新文件夹
	 */
	public static void decompress(String zipFilePath, File toDirectory, boolean createFolder) throws Exception
	{
		decompress(zipFilePath, toDirectory, createFolder, null);
	}

	/**
	 * 解压缩zip文件
	 * 
	 * @param zipFile
	 *            zip文件路径
	 * @param toDirectory
	 *            解压到的文件夹
	 * @param createFolder
	 *            是否创建新文件夹
	 * @param listener
	 *            解压过程监听
	 */
	public static void decompress(String zipFilePath, File toDirectory, boolean createFolder, CopyFileListener listener) throws Exception
	{
		File sourceZipFile = newFileEscape(zipFilePath);
		ZipFile zipFile = new ZipFile(zipFilePath);

		String directory = null;
		if (createFolder)
		{
			directory = toDirectory.getAbsolutePath() + "/" + sourceZipFile.getName() + "/";
		}
		else
		{
			directory = toDirectory.getAbsolutePath() + "/";
		}

		File dirFile = newFileEscape(directory);
		if (!dirFile.exists())
		{
			dirFile.mkdirs();
		}

		long fileTotal = 0l;
		@SuppressWarnings("rawtypes")
		Enumeration enumeration = zipFile.getEntries();
		while (enumeration.hasMoreElements())
		{
			enumeration.nextElement();
			fileTotal++;
		}

		enumeration = zipFile.getEntries();
		ZipEntry entry = null;
		for (; enumeration.hasMoreElements();)
		{
			entry = (ZipEntry) enumeration.nextElement();

			File file = newFileEscape(directory + entry.getName());

			if (listener != null)
			{
				listener.fileCopied(file, fileTotal);
			}

			if (entry.isDirectory())
			{
				file.mkdir();
				continue;
			}

			if (file.getParentFile() != null && !file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}

			OutputStream os = null;
			InputStream is = null;
			try
			{
				os = new BufferedOutputStream(new FileOutputStream(file));
				is = new BufferedInputStream(zipFile.getInputStream(entry));

				byte[] buf = new byte[BUFFER];
				int readLen = 0;
				while ((readLen = is.read(buf, 0, BUFFER)) != -1)
				{
					os.write(buf, 0, readLen);
				}
			}
			finally
			{
				if (os != null)
				{
					os.close();
				}
				if (is != null)
				{
					is.close();
				}
			}
		}
		zipFile.close();
	}

	public static String getMD5(File file)
	{
		String retString = null;
		FileInputStream fis = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER];
			int length = -1;

			while ((length = fis.read(buffer)) != -1)
			{
				md.update(buffer, 0, length);
			}
			retString = EncryptUtils.toHexString(md.digest());
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
				}
			}
		}
		return retString;
	}

	public static String formatFileSize(long fileSize)
	{
		String retStr = "";
		if (fileSize < FS_K)
		{
			retStr = fileSize + " B";
		}
		else if (fileSize < FS_M)
		{
			retStr = fileSize / FS_K + " KB";
		}
		else
		{
			retStr = NumberUtils.format(((float) fileSize) / FS_M, 2) + " MB";
		}
		return retStr;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath)
	{
		File file = newFileEscape(filePath);
		return deleteFile(file);
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file)
	{
		boolean deleteSuccess = true;
		if (file.exists())
		{
			if (file.isDirectory())
			{
				File[] listFiles = file.listFiles();
				if (listFiles != null)
				{
					for (File subFile : listFiles)
					{
						deleteSuccess &= deleteFile(subFile);
					}
				}
			}
			deleteSuccess &= file.delete();
		}
		return deleteSuccess;
	}

	/**
	 * 读文件, 返回文件内容
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String readFromFile(File file) throws Exception
	{
		StringBuffer content = new StringBuffer();

		BufferedReader reader = null;
		try
		{
			String lineSeparator = System.getProperty("line.separator");
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tempStr = null;
			while ((tempStr = reader.readLine()) != null)
			{
				if (content.length() == 0)
				{
					content.append(tempStr);
				}
				else
				{
					content.append(lineSeparator + tempStr);
				}
			}
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}

		return content.toString();
	}

	/**
	 * 写文本文件
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	public static void writeToFile(File file, String content) throws Exception
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.flush();
		}
		finally
		{
			if (bw != null)
			{
				bw.close();
			}
		}
	}

	/**
	 * 新建文件对象, 根据操作系统自动转换文件路径为可识别方式
	 * 
	 * @param pathname
	 * @return
	 */
	public static File newFileEscape(String pathname)
	{
		if (pathname == null)
		{
			return null;
		}

		// if (EnvUtils.getOSType() == EnvUtils.MACOSX)
		// {
		// pathname = decodeURLString(pathname, "UTF-8");
		// }

		return new File(pathname);
	}

	/**
	 * 使用java.net.URLDecoder转换字符串
	 * 
	 * @param str
	 * @param enc
	 *            编码格式
	 * @return
	 */
	public static String decodeURLString(final String str, final String enc)
	{
		if (str == null)
		{
			return str;
		}

		try
		{
			return URLDecoder.decode(str, enc);
		}
		catch (UnsupportedEncodingException e)
		{
			DynaLogger.error(e);
		}

		return str;
	}
}
