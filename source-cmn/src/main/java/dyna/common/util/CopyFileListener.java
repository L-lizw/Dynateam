/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CopyFileListener 文件复制监听器
 * Wanglei 2011-9-14
 */
package dyna.common.util;

import java.io.File;

/**
 * 文件复制监听器
 * 
 * @author Wanglei
 * 
 */
public interface CopyFileListener
{

	/**
	 * 文件复制监听
	 * 
	 * @param curFile
	 *            当前复制的文件
	 * @param fileTotal
	 *            需要复制的文件数目
	 */
	public void fileCopied(File curFile, long fileTotal);
}
