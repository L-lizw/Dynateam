package dyna.data.service.sync.bean;

import java.io.Serializable;

import dyna.common.systemenum.FieldTypeEnum;

/**
 * 数据库字段模型
 *
 * @author daniel
 */
public class ColumnModel implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4783911141427301095L;

	private String name = null;

	private FieldTypeEnum type = null;

	private String columnType = null;

	private String length = null;

	private String scale = null;

	private boolean nullable = true;

	private String defaultValue = null;

	private String comments = null;

	private boolean isRollback = false;

	private boolean isSystem = false;

	private boolean isBuiltin = false;

	private String tableName = null;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public FieldTypeEnum getType()
	{
		return type;
	}

	public void setType(FieldTypeEnum type)
	{
		this.type = type;
	}

	public String getColumnType()
	{
		return columnType;
	}

	public void setColumnType(String columnType)
	{
		this.columnType = columnType;
	}

	public String getLength()
	{
		return length;
	}

	public void setLength(String length)
	{
		this.length = length;
	}

	public String getScale()
	{
		return scale;
	}

	public void setScale(String scale)
	{
		this.scale = scale;
	}

	public boolean isNullable()
	{
		return nullable;
	}

	public void setNullable(boolean nullable)
	{
		this.nullable = nullable;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public String getComments()
	{
		return comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public boolean isRollback()
	{
		return isRollback;
	}

	public void setRollback(boolean isRollback)
	{
		this.isRollback = isRollback;
	}

	public boolean isSystem()
	{
		return isSystem;
	}

	public void setSystem(boolean isSystem)
	{
		this.isSystem = isSystem;
	}

	public boolean isBuiltin()
	{
		return isBuiltin;
	}

	public void setBuiltin(boolean isBuiltin)
	{
		this.isBuiltin = isBuiltin;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public boolean isVarcharColumn()
	{
		return FieldTypeEnum.CLASSIFICATION == this.getType() || FieldTypeEnum.CODE == this.getType() || FieldTypeEnum.CODEREF == this.getType() || FieldTypeEnum.MULTICODE == this
				.getType() || FieldTypeEnum.OBJECT == this.getType() || FieldTypeEnum.STRING == this.getType() || FieldTypeEnum.FOLDER == this.getType()
				|| FieldTypeEnum.STATUS == this.getType();
	}

	public boolean isBooleanColumn()
	{
		return FieldTypeEnum.BOOLEAN == this.getType();
	}

	public boolean isFixedCharColumn()
	{
		return FieldTypeEnum.STATUS == this.getType();
	}

	public boolean isDateColumn()
	{
		return FieldTypeEnum.DATE == this.getType();
	}

	public boolean isDatetimeColumn()
	{
		return FieldTypeEnum.DATETIME == this.getType();
	}

	public boolean isIntegerColumn()
	{
		return FieldTypeEnum.INTEGER == this.getType();
	}

	public boolean isFloatColumn()
	{
		return FieldTypeEnum.FLOAT == this.getType();
	}
}