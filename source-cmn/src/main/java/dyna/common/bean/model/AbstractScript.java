/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractScript
 * Wanglei 2011-7-13
 */
package dyna.common.bean.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.wf.WorkflowActrtActionInfo;
import dyna.common.dto.model.wf.WorkflowEventInfo;
import dyna.common.systemenum.EventTypeEnum;
import dyna.common.systemenum.MessageIconEnum;
import dyna.common.systemenum.ScriptFileType;
import dyna.common.systemenum.ScriptRunAtEnum;
import dyna.common.systemenum.ScriptTypeEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public abstract class AbstractScript extends SystemObjectImpl implements Script
{
	private static final long	serialVersionUID	= 8848473016271140798L;

	public static final String	ACTIONNAME			= "ACTIONNAME";

	public static final String	PARENTGUID			= "PARENTGUID";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	TITLE				= "TITLE";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	MESSAGEICON			= "MESSAGEICON";

	public static final String	SCRIPTTYPE			= "SCRIPTTYPE";

	public static final String	RUNAT				= "RUNAT";

	public static final String	FIXEDMESSAGE		= "FIXEDMESSAGE";

	public static final String	ISFIXED				= "ISFIXED";

	public static final String	ICONPATH			= "ICONPATH";

	public static final String	UI					= "UI";

	public static final String	SCRIPT				= "SCRIPT";

	public static final String	EVENTTYPE			= "EVENTTYPE";

	public static final String	FEEDBACK			= "FEEDBACK";

	public static final String	FULLNAME			= "FULLNAME";

	public static final String	FILENAME			= "FILENAME";

	private String				parentName			= null;

	private String				className			= null;

	private Script				parent				= null;

	private List<Script>		children			= null;

	private ScriptCallbackEnum	callbackEnum		= null;

	private long				lastModified		= 0L;

	private String[]			options				= null;

	private boolean				isInherited			= false;

	private boolean				isBuiltin			= false;

	private ScriptFileType		scriptType			= ScriptFileType.JAVASCRIPT;

	// 用于编辑
	private boolean				typeChange			= false;

	/**
	 * @param typeChange
	 *            the typeChange to set
	 */
	public void setTypeChange(boolean typeChange)
	{
		this.typeChange = typeChange;
	}

	/**
	 * @return the typeChange
	 */
	public boolean isTypeChange()
	{
		return this.typeChange;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage()
	{
		return (String) this.get(FIXEDMESSAGE);
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message)
	{
		this.put(FIXEDMESSAGE, message);
	}

	@Override
	public MessageIconEnum getMessageIcon()
	{
		return this.get(MESSAGEICON) == null ? null : MessageIconEnum.valueOf((String) this.get(MESSAGEICON));
	}

	public void setMessageIcon(MessageIconEnum messageIcon)
	{
		this.put(MESSAGEICON, messageIcon.name());
	}

	@Override
	public boolean isFixedMessage()
	{
		if (this.get(ISFIXED) == null)
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(ISFIXED));
	}

	public void setFixedMessage(boolean fixed)
	{
		this.put(ISFIXED, BooleanUtils.getBooleanStringYN(fixed));
	}

	@Override
	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}

	public void setSequence(Integer sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	@Override
	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getName()
	 */
	@Override
	public String getName()
	{
		return (String) this.get(ACTIONNAME);
	}

	@Override
	public void setName(String name)
	{
		this.put(ACTIONNAME, name);
	}

	@Override
	public ScriptRunAtEnum getRunAt()
	{
		return this.get(RUNAT) == null ? ScriptRunAtEnum.SERVER : ScriptRunAtEnum.valueOf(((String) this.get(RUNAT)).toUpperCase());
	}

	public void setRunAt(ScriptRunAtEnum runAt)
	{
		this.put(RUNAT, runAt.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getScriptFileName()
	 */
	@Override
	public String getScriptFileName()
	{
		String scriptSubName = this.getScriptSubName();
		if (!StringUtils.isNullString(scriptSubName))
		{
			scriptSubName = "." + scriptSubName;
		}

		if (this.getClass() == ClassAction.class)
		{
			return this.className + "_" + this.getScriptName() + "_Action" + scriptSubName + ".js";
		}
		/*
		 * else if (this.getClass() == ClassEvent.class)
		 * {
		 * return this.className + "_" + this.getScriptName() + "_Event" + scriptSubName + ".js";
		 * }
		 */
		else if (this.getClass() == WorkflowActrtActionInfo.class)
		{
			return this.className + "_" + this.getScriptName() + "_wfAction" + scriptSubName + ".js";
		}
		else if (this.getClass() == WorkflowEventInfo.class)
		{
			return this.className + "_" + this.getScriptName() + "_Event" + scriptSubName + ".js";
		}
		return null;
	}

	public ScriptFileType getType()
	{
		return this.scriptType;
	}

	public void setType(ScriptFileType type)
	{
		this.scriptType = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getParentName()
	 */
	@Override
	public String getParentName()
	{
		return this.parentName;
	}

	public void setParentName(String parentName)
	{
		this.parentName = parentName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getChildren()
	 */
	@Override
	public List<Script> getChildren()
	{
		return this.children;
	}

	public <T extends Script> void setChildren(List<T> children)
	{
		this.children = (List<Script>) children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getScriptResultType()
	 */
	@Override
	public ScriptResultTypeEnum getScriptResultType()
	{
		if (this.get(UI) == null || "DEFAULT".equalsIgnoreCase((String) this.get(UI)))
		{
			return ScriptResultTypeEnum.NONE;
		}
		return ScriptResultTypeEnum.valueOf(((String) this.get(UI)).toUpperCase());
	}

	public void setScriptResultType(ScriptResultTypeEnum type)
	{
		this.put(UI, type.name());
	}

	public String getUI()
	{
		return (String) this.get(UI);
	}

	public void setUI(String ui)
	{
		this.put(UI, ui);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getLastUpdateTime()
	 */
	@Override
	public long getLastModified()
	{
		return this.lastModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#setLastUpdateTime(java.sql.Date)
	 */
	@Override
	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getScriptCallbackEnum()
	 */
	@Override
	public ScriptCallbackEnum getScriptCallbackEnum()
	{
		return this.callbackEnum;
	}

	public void setScriptCallbackEnum(ScriptCallbackEnum callbackEnum)
	{
		this.callbackEnum = callbackEnum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.bean.model.Script#getOptions()
	 */
	@Override
	public String[] getOptions()
	{
		switch (this.getScriptResultType())
		{
		case OK_INFO:
		case OK_WARNNING:
		case OK_ERROR:
			this.options = OK_OPTION;
			break;

		case YES_NO:
			this.options = YES_NO_OPTION;
			break;

		case YES_NO_CANCEL:
			this.options = YES_NO_CANCEL_OPTION;
			break;

		case OK_CANCEL:
			this.options = OK_CANCEL_OPTION;
			break;

		default:
			break;
		}
		return this.options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(String[] options)
	{
		this.options = options;
	}

	@Override
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	public void addScript(Script script)
	{
		if (this.children == null)
		{
			this.children = new ArrayList<Script>();
		}

		this.children.add(script.getSequence(), script);
		((AbstractScript) script).setParent(this);
	}

	public String getParentGuid()
	{
		return (String) this.get(PARENTGUID);
	}

	public void setParentGuid(String parentGuid)
	{
		this.put(PARENTGUID, parentGuid);
	}

	@Override
	public Script getChild(int index)
	{
		if (this.getChildren() != null && this.getChildren().size() > this.getSequence())
		{
			return this.getChildren().get(index);
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getFullName()
	{
		String scriptSubName = this.getScriptSubName();
		if (StringUtils.isNullString(scriptSubName))
		{
			return this.getScriptName();
		}
		else
		{
			return this.getScriptName() + "." + scriptSubName;
		}
	}

	@Override
	public String getSequenceFullName()
	{
		String scriptSubSequence = this.getScriptSubSquence();
		if (StringUtils.isNullString(scriptSubSequence))
		{
			return this.className + "_" + this.getScriptName();
		}
		else
		{
			return this.className + "_" + this.getScriptName() + "." + scriptSubSequence;
		}
	}

	/**
	 * @return the fullName
	 */
	private String getScriptName()
	{
		if (this.getParent() != null)
		{
			return ((AbstractScript) this.getParent()).getScriptName();
		}
		else
		{
			return this.getName();
		}
	}

	/**
	 * @return the fullName
	 */
	private String getScriptSubName()
	{
		if (this.getParent() != null)
		{
			String scriptSubName = ((AbstractScript) this.getParent()).getScriptSubName();
			if (StringUtils.isNullString(scriptSubName))
			{
				return this.getName();
			}
			else
			{
				return scriptSubName + "." + this.getName();
			}
		}
		else
		{
			return "";
		}
	}

	private String getScriptSubSquence()
	{
		if (this.getParent() != null)
		{
			String scriptSubSequence = ((AbstractScript) this.getParent()).getScriptSubSquence();
			if (StringUtils.isNullString(scriptSubSequence))
			{
				return "" + this.getSequence();
			}
			else
			{
				return scriptSubSequence + "." + this.getSequence();
			}
		}
		else
		{
			return "";
		}
	}

	@Override
	public String getIcon()
	{
		return (String) this.get(ICONPATH);
	}

	public void setIcon(String iconPath)
	{
		this.put(ICONPATH, iconPath);
	}

	@Override
	public String getScript()
	{
		return (String) this.get(SCRIPT);
	}

	public void setScript(String script)
	{
		this.put(SCRIPT, script);
	}

	@Override
	public EventTypeEnum getEventType()
	{
		return this.get(EVENTTYPE) == null ? null : EventTypeEnum.typeValueOf(((String) this.get(EVENTTYPE)).toUpperCase());
	}

	public void setEventType(EventTypeEnum eventType)
	{
		this.put(EVENTTYPE, eventType == null ? null : eventType.name().toLowerCase());
	}

	public ScriptTypeEnum getScriptType()
	{
		return this.get(SCRIPTTYPE) == null ? null : ScriptTypeEnum.typeOf((String) this.get(SCRIPTTYPE));
	}

	public void setScriptType(ScriptTypeEnum scriptType)
	{
		this.put(SCRIPTTYPE, scriptType.getType());
	}

	public String getFeedback()
	{
		return (String) this.get(FEEDBACK);
	}

	public void setFeedback(String feedback)
	{
		this.put(FEEDBACK, feedback);
	}

	public void setFullName(String fullName)
	{
		this.put(FULLNAME, fullName);
	}

	public String getFileName()
	{
		return (String) this.get(FILENAME);
	}

	public void setFileName(String fileName)
	{
		this.put(FILENAME, fileName);
	}

	public boolean isBuiltin()
	{
		return isBuiltin;
	}

	public void setBuiltin(boolean isBuiltin)
	{
		this.isBuiltin = isBuiltin;
	}

	public boolean isInherited()
	{
		return isInherited;
	}

	public void setInherited(boolean isInherited)
	{
		this.isInherited = isInherited;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(Script parent)
	{
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public Script getParent()
	{
		return this.parent;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * @return the className
	 */
	public String getClassName()
	{
		return this.className;
	}

	@Override
	public ScriptFileType getScriptFileType()
	{
		return scriptType;
	}

	public void setScriptFileType(ScriptFileType scriptType)
	{
		this.scriptType = scriptType;
	}
}
