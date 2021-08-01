/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MessageRule
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.MessageRuleMapper;
import dyna.common.systemenum.ppms.MessageTypeEnum;
import dyna.common.util.BooleanUtils;

import java.util.List;

/**
 * 消息规则设置
 * 
 * @author wangweixia
 * 
 */
@EntryMapper(MessageRuleMapper.class)
public class MessageRule extends SystemObjectImpl implements SystemObject
{

	private static final long		serialVersionUID		= -9045629491172066006L;
	// 消息类型
	public static final String		MESSAGETYPE				= "MESSAGETYPE";

	// 主题
	public static final String		THEME					= "THEME";

	// 发送给
	public List<MessageTypeEnum>	notifierList			= null;

	// 启用状态
	// public static final String ISENABLE = "ISENABLE";

	// 启用邮件
	public static final String		ISSTARTEMAIL			= "ISSTARTEMAIL";

	// 启用站内消息
	public static final String		ISSTARTINSTATIONINFO	= "ISSTARTINSTATIONINFO";

	// 通知规则
	public static final String		NOTICERULE				= "NOTICERULE";

	public static final String		NAME					= "MESSAGENAME";

	// 空构造有用
	public MessageRule()
	{

	}

	public MessageRule(MessageTypeEnum messageType)
	{
		// setEnable(true);
		setStartEmail(true);
		setStartInstationinfo(true);
		setMessagetype(messageType);
		setNotifierList(MessageTypeEnum.listNotifierByType(messageType.name()));
	}

	/**
	 * @return the notifierList
	 */
	public List<MessageTypeEnum> getNotifierList()
	{
		return notifierList;
	}

	/**
	 * @param notifierList
	 *            the notifierList to set
	 */
	public void setNotifierList(List<MessageTypeEnum> notifierList)
	{
		this.notifierList = notifierList;
	}

	/**
	 * @return the messagetype
	 */
	public MessageTypeEnum getMessagetype()
	{

		if (super.get(MESSAGETYPE) != null)
		{
			return MessageTypeEnum.valueOf((String) super.get(MESSAGETYPE));
		}
		else
		{
			return null;
		}

	}

	public void setMessagetype(MessageTypeEnum type)
	{
		super.put(MESSAGETYPE, type.name());
	}

	/**
	 * @return the theme
	 */
	public String getTheme()
	{
		return (String) super.get(THEME);
	}

	public void setTheme(String theme)
	{
		super.put(THEME, theme);
	}

	/**
	 * @return the isenable
	 */
	// public boolean isEnable()
	// {
	// Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISENABLE));
	// return ret == null ? false : ret.booleanValue();
	// }
	//
	// public void setEnable(boolean enable)
	// {
	// this.put(ISENABLE, BooleanUtils.getBooleanStringYN(enable));
	// }

	/**
	 * @return the isstartemail
	 */
	public boolean isStartEmail()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISSTARTEMAIL));
		return ret == null ? false : ret.booleanValue();
	}

	public void setStartEmail(boolean startEmail)
	{
		this.put(ISSTARTEMAIL, BooleanUtils.getBooleanStringYN(startEmail));
	}

	/**
	 * @return the isstartinstationinfo
	 */
	public boolean isStartInstationinfo()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(ISSTARTINSTATIONINFO));
		return ret == null ? false : ret.booleanValue();
	}

	public void setStartInstationinfo(boolean startInstationinfo)
	{
		this.put(ISSTARTINSTATIONINFO, BooleanUtils.getBooleanStringYN(startInstationinfo));
	}

	/**
	 * @return the noticerule
	 */
	public String getNoticerule()
	{
		return (String) super.get(NOTICERULE);
	}

	public void setNoticerule(String noticeRule)
	{
		super.put(NOTICERULE, noticeRule);
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub
		super.put(NAME, name);
	}
	
	
}
