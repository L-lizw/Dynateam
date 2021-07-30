/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MessageModel
 * WangLHB Aug 12, 2011
 */
package dyna.common.bean.extra;

import java.io.Serializable;

/**
 * @author WangLHB
 * 
 */
public class Message implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1400140023523897428L;

	private String				key					= null;

	private String				messageId			= null;

	private String				message				= null;

	private boolean				successful			= true;

	private Object[]			data				= null;

	public Message(String key, boolean successful, Object... data)
	{
		this.key = key;
		this.data = data;
		this.successful = successful;
	}

	public Message(String key, boolean successful, String message)
	{
		this.key = key;
		this.message = message;
		this.successful = successful;
	}

	public Message(String key, boolean successful, String message, String messageId, Object... data)
	{
		this(key, successful, message);
		this.messageId = messageId;
		this.setData(data);
	}

	/**
	 * @return the id
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setKey(String id)
	{
		this.key = id;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return the successful
	 */
	public boolean isSuccessful()
	{
		return this.successful;
	}

	/**
	 * @param successful
	 *            the successful to set
	 */
	public void setSuccessful(boolean successful)
	{
		this.successful = successful;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId()
	{
		return this.messageId;
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(String messageId)
	{
		this.messageId = messageId;
	}

	/**
	 * @return the data
	 */
	public Object[] getData()
	{
		return this.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object[] data)
	{
		this.data = data;
	}

}
