/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReportBuilder
 * Wanglei 2011-12-20
 */
package dyna.customization.report;

import java.util.List;
import java.util.Map;

import dyna.app.service.brs.srs.SRSImpl;
import dyna.common.bean.data.ObjectGuid;

/**
 * 报表生成器
 * 
 * @author Wanglei
 * 
 */
public interface ReportBuilder
{
	/**
	 * 设置与报表产生过程需要的参数
	 * 
	 * @param params
	 */
	public void setReportParameters(Object... params);

	/**
	 * {@link #isNotifyCreator()} 返回true时,
	 * 通知报表创建者
	 * 
	 * @param srs
	 *            系统通知服务, 可通过此服务获取系统的其他服务
	 */
	public void notifyCreator(SRSImpl srs);

	/**
	 * 是否通知报表创建者
	 * 
	 * @return
	 */
	public boolean isNotifyCreator();

	/**
	 * 报表创建者
	 * 
	 * @return
	 */
	public String getCreatorId();

	/**
	 * 设置报表创建者
	 * 
	 * @param creatorId
	 */
	public void setCreatorId(String creatorId);

	/**
	 * 返回通知信息的标题
	 * 
	 * @return
	 */
	public String getNotifySubject();

	/**
	 * 返回通知信息的内容
	 * 
	 * @return
	 */
	public String getNotifyMessage();

	/**
	 * 返回通知信息的附件列表
	 * 
	 * @return
	 */
	public List<ObjectGuid> getNotifyAttachmentList();

	/**
	 * 生成报表, 并返回发送给报表申请人系统邮件通知的内容信息
	 * 
	 * @param srs
	 *            简单报表服务, 通过此服务获取相关服务
	 * @return 系统邮件通知的内容信息, 如"XXX报表创建成功"
	 */
	public Map<String, Object> build(SRSImpl srs) throws Exception;

}
