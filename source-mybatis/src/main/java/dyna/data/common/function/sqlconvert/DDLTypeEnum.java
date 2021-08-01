package dyna.data.common.function.sqlconvert;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public enum DDLTypeEnum
{
	// 修改列
	ALTER_COLUMN,
	// 增加列
	ADD_COLUMN,
	// 删除列
	DROP_COLUMN,
	// 增加唯一索引
	ADD_UNIQUE_INDEX,
	// 增加一般索引
	ADD_INDEX,
	// 删除索引
	DROP_INDEX,
	// 增加约束
	ADD_CONSTRAINT,
	// 删除约束
	DROP_CONSTRAINT,
	// 创建表
	CREATE_TABLE,
	// 删除表
	DROP_TABLE
}
