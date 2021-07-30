package dyna.data.service.sync;

import dyna.common.util.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;

public class SyncService
{
	protected final SqlSessionFactory sqlClient;
	protected final String            userGuid;

	public SyncService(SqlSessionFactory sqlSessionFactory, String userGuid)
	{
		this.sqlClient = sqlSessionFactory;
		this.userGuid = userGuid;
	}

	public String genericGuid()
	{
		return StringUtils.genericGuid();
	}

	/**
	 * 判断字符串是否只有数字组成
	 *
	 * @param strNum
	 * @return
	 */
	public boolean isDigit(String strNum)
	{
		return strNum.matches("[0-9]{1,}");
	}
}
