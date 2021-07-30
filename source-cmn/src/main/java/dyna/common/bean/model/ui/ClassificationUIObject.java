package dyna.common.bean.model.ui;

import java.util.ArrayList;
import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.ui.ClassificationUIField;
import dyna.common.dto.model.ui.ClassificationUIInfo;

public class ClassificationUIObject extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	public static final long			serialVersionUID	= 2051604070981568833L;

	private ClassificationUIInfo		info				= null;

	private List<ClassificationUIField>	uiFieldList			= null;

	public ClassificationUIObject()
	{
	}

	public ClassificationUIObject(ClassificationUIInfo info)
	{
		this.info = info;
	}

	public ClassificationUIInfo getInfo()
	{
		return info;
	}

	public void setInfo(ClassificationUIInfo info)
	{
		this.info = info;
	}

	public String getName()
	{
		return (String) this.get(NAME);
	}

	public String getClassificationGuid()
	{
		return this.info.getClassificationGuid();
	}

	public void setClassificationGuid(String classificationGuid)
	{
		this.info.setClassificationGuid(classificationGuid);
	}

	public String getTitle()
	{
		return this.info.getTitle();
	}

	public void setTitle(String title)
	{
		this.info.setTitle(title);
	}

	public String getUIName()
	{
		return this.info.getName();
	}

	public void setUIName(String uiName)
	{
		this.info.setName(uiName);
	}

	public Boolean isInherited()
	{
		return this.info.isInherited();
	}

	public void setInherited(Boolean isInherited)
	{
		this.info.setInherited(isInherited);
	}

	public int getSequence()
	{
		return this.info.getSequence();
	}

	public void setSequence(Integer sequence)
	{
		this.info.setSequence(sequence);
	}

	public boolean isVisible()
	{
		return this.info.isVisible();
	}

	public void setVisible(boolean isVisible)
	{
		this.info.setVisible(isVisible);
	}

	public String getType()
	{
		return this.info.getType();
	}

	public void setType(String type)
	{
		this.info.setType(type);
	}

	public List<ClassificationUIField> getUiFieldList()
	{
		return uiFieldList;
	}

	public void setUiFieldList(List<ClassificationUIField> uiFieldList)
	{
		this.uiFieldList = uiFieldList;
	}

	public void removeUIField(ClassificationUIField uiField)
	{
		if (this.uiFieldList != null)
		{
			for (ClassificationUIField uiField_ : this.uiFieldList)
			{
				if (uiField_.getGuid().equals(uiField.getGuid()))
				{
					this.uiFieldList.remove(uiField_);
					break;
				}
			}
		}
	}

	public void addUIField(ClassificationUIField uiField)
	{
		if (this.uiFieldList == null)
		{
			this.uiFieldList = new ArrayList<ClassificationUIField>();
		}
		this.uiFieldList.add(uiField);
	}
}
