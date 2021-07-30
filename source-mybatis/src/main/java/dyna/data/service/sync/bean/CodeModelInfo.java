package dyna.data.service.sync.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.systemenum.CodeDisplayEnum;
import dyna.common.systemenum.CodeTypeEnum;

@Root(name = "code-item")
public class CodeModelInfo implements Cloneable, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7296580636666778424L;

	@Attribute(name = "name", required = false) private String name;

	@Attribute(name = "description", required = false) private String description;

	@Attribute(name = "title", required = false) private String title;

	@Attribute(name = "parentname", required = false) private String parentName;

	@Attribute(name = "mastername", required = false) private String masterName;

	@Attribute(name = "code", required = false) private String code;

	@Attribute(name = "sequence", required = false) private String sequence;

	@Attribute(name = "type", required = false) private String type;

	@Attribute(name = "showtype", required = false) private String showType;

	@Attribute(name = "classification", required = false) private String isClassification;

	private List<CodeModelInfo> codeDetailList = null;

	@ElementList(name = "fields", entry = "field", required = false) private List<ClassificationField> fieldList = null;

	public CodeModelInfo()
	{
	}

	public CodeModelInfo(CodeObject codeObjectInfo)
	{
		this.setName(codeObjectInfo.getName());
		this.setDescription(codeObjectInfo.getDescription());
		this.setTitle(codeObjectInfo.getTitle());
		this.setType(codeObjectInfo.getType() == null ? CodeTypeEnum.LIST.name() : codeObjectInfo.getType().name());
		this.setShowType(codeObjectInfo.getShowType() == null ? CodeDisplayEnum.DROPDOWN.name() : codeObjectInfo.getShowType().name());
		this.setMasterName(codeObjectInfo.getName());
		this.setClassification(codeObjectInfo.isClassification());
	}

	public CodeModelInfo(CodeItem codeItemInfo)
	{
		this.setName(codeItemInfo.getName());
		this.setDescription(codeItemInfo.getDescription());
		this.setTitle(codeItemInfo.getTitle());
		this.setCode(codeItemInfo.getCode());
		this.setParentName(codeItemInfo.getParentName());
		this.setMasterName(codeItemInfo.getMasterName());
		this.setSequence(String.valueOf(codeItemInfo.getSequence()));
		this.setClassification(codeItemInfo.isClassification());
		if (this.getParentName() == null)
		{
			this.setParentName(codeItemInfo.getMasterName());
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getParentName()
	{
		return parentName;
	}

	public void setParentName(String parentName)
	{
		this.parentName = parentName;
	}

	public String getMasterName()
	{
		return masterName;
	}

	public void setMasterName(String masterName)
	{
		this.masterName = masterName;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getSequence()
	{
		return sequence;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getShowType()
	{
		return showType;
	}

	public void setShowType(String showType)
	{
		this.showType = showType;
	}

	public List<CodeModelInfo> getCodeDetailList()
	{
		return codeDetailList;
	}

	public void setCodeDetailList(List<CodeModelInfo> codeDetailList)
	{
		this.codeDetailList = codeDetailList;
	}

	public List<ClassificationField> getFieldList()
	{
		return fieldList;
	}

	public void setFieldList(List<ClassificationField> fieldList)
	{
		this.fieldList = fieldList;
	}

	public void setClassification(boolean isClassification)
	{
		this.isClassification = isClassification ? "true" : "false";
	}

	public boolean isClassification()
	{
		return "true".equalsIgnoreCase(this.isClassification);
	}

	/**
	 * 在分类对象中添加子分类对象
	 *
	 * @param child
	 */
	public void addChild(CodeModelInfo child)
	{
		if (this.codeDetailList == null)
		{
			this.codeDetailList = new ArrayList<CodeModelInfo>();
		}
		this.codeDetailList.add(child);
	}
}
