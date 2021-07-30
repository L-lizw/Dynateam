/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeInfo
 * caogc 2010-9-19
 */
package dyna.common.bean.model.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.systemenum.CodeDisplayEnum;
import dyna.common.systemenum.CodeTypeEnum;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.StringUtils;

/**
 * @author caogc
 */
public class CodeObject extends SystemObjectImpl implements SystemObject
{
	private static final long		serialVersionUID			= -740223844971555613L;

	public static final String		MULTI_CODE_DELIMITER		= "\r\n";

	public static final String		MULTI_CODE_DELIMITER_GUID	= ";";

	public static final String		OTHER_GUID					= "################################";

	public static final String		PREFIX_TABLENAME			= "CF_";

	public static final String		SUBFIX_TABLENAME			= "_I";

	private final CodeObjectInfo	info;

	private List<CodeItem>			codeDetailList				= null;

	public CodeObject()
	{
		info = new CodeObjectInfo();
	}

	public CodeObject(CodeObjectInfo info)
	{
		this.info = info;
	}

	public CodeObjectInfo getInfo()
	{
		return info;
	}

	@Override
	public String getGuid()
	{
		return this.info.getGuid();
	}

	@Override
	public void setGuid(String guid)
	{
		this.info.setGuid(guid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CodeObject clone()
	{
		return (CodeObject) super.clone();
	}

	@Override
	public String getName()
	{
		return this.info.getName();
	}

	@Override
	public void setName(String name)
	{
		this.info.setName(name);
	}

	public String getTitle()
	{
		return this.info.getTitle();
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.info.setTitle(title);
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	/**
	 * @return the type
	 */
	public CodeTypeEnum getType()
	{
		return this.info.getType();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(CodeTypeEnum type)
	{
		this.info.setType(type);
	}

	/**
	 * @return the isClassification
	 */
	public boolean isClassification()
	{
		return this.info.isClassification();
	}

	/**
	 * @param isClassification
	 *            the isClassification to set
	 */
	public void setClassification(boolean isClassification)
	{
		this.info.setClassification(isClassification);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "{Name: " + this.getName() + ", Guid: " + this.getGuid() + "}";
	}

	/**
	 * @param showType
	 *            the showType to set
	 */
	public void setShowType(CodeDisplayEnum showType)
	{
		this.info.setShowType(showType);
	}

	/**
	 * @return the showType
	 */
	public CodeDisplayEnum getShowType()
	{
		return this.info.getShowType();
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.info.setDescription(description);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.info.getDescription();
	}

	public String getBaseTableName()
	{
		return this.info.getBaseTableName();
	}

	public void setBaseTableName(String baseTableName)
	{
		this.info.setBaseTableName(baseTableName);
	}

	public boolean isHasFields()
	{
		return this.info.isHasFields();
	}

	public void setHasFields(boolean hasFields)
	{
		this.info.setHasFields(hasFields);
	}

	public void setCodeDetailList(List<CodeItem> codeDetailList)
	{
		this.codeDetailList = codeDetailList;
		if (codeDetailList != null)
		{
			Collections.sort(codeDetailList, new CodeItemSeqCompare());
		}
	}

	public List<CodeItem> getCodeDetailList()
	{
		if (this.codeDetailList == null)
		{
			this.codeDetailList = new ArrayList<CodeItem>();
		}
		return this.codeDetailList;
	}

	public String getRevisionTableName()
	{
		return StringUtils.isNullString(this.getBaseTableName()) ? null : PREFIX_TABLENAME + this.getBaseTableName();
	}

	public String getIterationTableName()
	{
		return StringUtils.isNullString(this.getBaseTableName()) ? null : PREFIX_TABLENAME + this.getBaseTableName() + SUBFIX_TABLENAME;
	}

	public CodeItem getCodeItem(String codeItemName)
	{
		if (codeItemName == null)
		{
			return null;
		}

		List<CodeItem> allCodeItemList = new ArrayList<CodeItem>();
		this.getAllCodeItemList(this.getCodeDetailList(), allCodeItemList);

		for (CodeItem codeItem : allCodeItemList)
		{
			if (codeItemName.equals(codeItem.getName()))
			{
				return codeItem;
			}
		}

		return null;
	}

	private void getAllCodeItemList(List<CodeItem> list, List<CodeItem> allCodeItemList)
	{
		if (list == null || list.size() == 0)
		{
			return;
		}

		allCodeItemList.addAll(list);

		for (CodeItem codeItem : list)
		{
			this.getAllCodeItemList(codeItem.getCodeDetailList(), allCodeItemList);
		}
	}

	/**
	 * 在分类对象中添加子分类对象
	 *
	 * @param child
	 */
	public void addChild(CodeItem child)
	{
		if (this.codeDetailList == null)
		{
			this.codeDetailList = new ArrayList<CodeItem>();
		}
		this.codeDetailList.add(child);
		Collections.sort(codeDetailList, new CodeItemSeqCompare());
	}
}
