/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：
 *    创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.bean.model;

import java.io.Serializable;
import java.util.List;

import dyna.common.systemenum.EventTypeEnum;
import dyna.common.systemenum.MessageIconEnum;
import dyna.common.systemenum.ScriptFileType;
import dyna.common.systemenum.ScriptRunAtEnum;

public interface Script extends Cloneable, Serializable
{
	public static final String		DEFAULT_TITLE			= "Tip;提示;警示";
	public static final String[]	OK_OPTION				= new String[] { "OK;确定;確定" };
	public static final String[]	YES_NO_OPTION			= new String[] { "YES;是;是", "NO;否;否" };
	public static final String[]	YES_NO_CANCEL_OPTION	= new String[] { "YES;是;是", "NO;否;否", "CANCEL;取消;取消" };
	public static final String[]	OK_CANCEL_OPTION		= new String[] { "OK;确定;確定", "CANCEL;取消;取消" };

	/**
	 * 获取脚本名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 获取脚本全名称, 即脚本名.子脚本0的名称.子脚本0的名称...
	 * 
	 * @return
	 */
	public String getFullName();

	/**
	 * 获取脚本全序列名, 即脚本名.子脚本0的顺序.子脚本0的顺序...
	 * 
	 * @return
	 */
	public String getSequenceFullName();

	/**
	 * 获取脚本内容
	 * 
	 * @return
	 */
	@Deprecated
	public String getScript();

	/**
	 * 获取脚本运行的位置
	 * 
	 * @return
	 */
	public ScriptRunAtEnum getRunAt();

	/**
	 * 获取脚本文件的名称
	 * 
	 * @return
	 */
	public String getScriptFileName();

	/**
	 * 获取脚本的最后更新时间
	 * 
	 * @return
	 */
	public long getLastModified();

	/**
	 * 设置脚本的最后更新时间
	 * 
	 * @param lastModified
	 */
	public void setLastModified(long lastModified);

	/**
	 * 获取脚本类型
	 * 
	 * @return
	 */
	public ScriptFileType getScriptFileType();

	/**
	 * 获取父级脚本结构的名称
	 * 
	 * @return
	 */
	public String getParentName();

	/**
	 * 获取子脚本结构的列表
	 * 
	 * @return
	 */
	public List<Script> getChildren();

	/**
	 * 按顺序获取子脚本
	 * 
	 * @return
	 */
	public Script getChild(int index);

	/**
	 * 获取脚本运行结果类型
	 * 
	 * @return
	 */
	public ScriptResultTypeEnum getScriptResultType();

	/**
	 * 脚本反馈类型
	 * 
	 * @return
	 */
	public ScriptCallbackEnum getScriptCallbackEnum();

	/**
	 * 获取选项信息
	 * 
	 * @return
	 */
	public String[] getOptions();

	/**
	 * 描述
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * 是否使用修正的message
	 * 
	 * @return
	 */
	public boolean isFixedMessage();

	/**
	 * 修正的message
	 * 
	 * @return
	 */
	public String getMessage();

	/**
	 * message icon
	 * 
	 * @return
	 */
	public MessageIconEnum getMessageIcon();

	public int getSequence();

	/**
	 * @return the icon
	 */
	public String getIcon();

	/**
	 * 获取事件类型
	 * 
	 * @return
	 */
	public EventTypeEnum getEventType();
}
