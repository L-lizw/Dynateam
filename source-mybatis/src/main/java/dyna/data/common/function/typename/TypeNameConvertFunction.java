package dyna.data.common.function.typename;

/**
 * 从数据库中取得表名或者字段名时，不同数据库默认大小写不一致
 * 
 * @author duanll
 *
 */
public interface TypeNameConvertFunction
{
	String getTypeName(String typeName);

	String getSchemaName(String userName);
}
