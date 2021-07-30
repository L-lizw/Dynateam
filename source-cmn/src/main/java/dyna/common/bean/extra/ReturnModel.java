/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MessageModel
 * WangLHB Aug 12, 2011
 */
package dyna.common.bean.extra;

import java.io.Serializable;
import java.util.List;

/**
 * @author WangLHB
 * 
 */
public class ReturnModel<T> implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1400140023523897428L;

	private List<Message>		messageList			= null;

	private T					instance			= null;

	public ReturnModel(List<Message> messageList, T instance)
	{
		this.setInstance(instance);
		this.setMessageList(messageList);
	}

	/**
	 * @return the messageModelList
	 */
	public List<Message> getMessageList()
	{
		return this.messageList;
	}

	/**
	 * @param messageModelList
	 *            the messageModelList to set
	 */
	public void setMessageList(List<Message> messageList)
	{
		this.messageList = messageList;
	}

	/**
	 * @return the instance
	 */
	public T getInstance()
	{
		return this.instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public void setInstance(T instance)
	{
		this.instance = instance;
	}

}
