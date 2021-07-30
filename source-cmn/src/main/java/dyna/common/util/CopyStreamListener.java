/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CopyStreamListener 传输流监听器
 * Wanglei 2011-9-22
 */
package dyna.common.util;

/**
 * 传输流监听器
 * 
 * @author Wanglei
 * 
 */
public interface CopyStreamListener
{
	/**
	 * This method is not part of the JavaBeans model and is used by the
	 * static methods in the org.apache.commons.io.Util class for efficiency.
	 * It is invoked after a block of bytes to inform the listener of the
	 * transfer.
	 * 
	 * @param totalBytesTransferred
	 *            The total number of bytes transferred
	 *            so far by the copy operation.
	 * @param bytesTransferred
	 *            The number of bytes copied by the most recent
	 *            write.
	 * @param streamSize
	 *            The number of bytes in the stream being copied.
	 *            This may be equal to CopyStreamEvent.UNKNOWN_STREAM_SIZE if
	 *            the size is unknown.
	 */
	public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize);
}
