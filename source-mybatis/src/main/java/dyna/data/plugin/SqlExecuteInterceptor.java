package dyna.data.plugin;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.insert.DynamicInsertParamData;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Method;
import java.util.*;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class,Object.class})})
public class SqlExecuteInterceptor implements Interceptor
{
	/**
	 * 	增强
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	@Override public Object intercept(Invocation invocation) throws Throwable
	{
		Method method = invocation.getMethod();
		Class clazz = invocation.getClass();
		Object[] args = invocation.getArgs();

		if (args == null || args.length <= 1)
		{
			return invocation.proceed();
		}
		else if (DynamicSqlParamData.class.isAssignableFrom(args[1].getClass()))
		{
			if (DynamicInsertParamData.class.getName().equals(args[1].getClass().getName()))
			{
				String guid = StringUtils.generateRandomUID(32).toUpperCase();
				DynamicInsertParamData paramData = (DynamicInsertParamData) args[1];
				List<SqlParamData> insertParamList = paramData.getInsertParamList();
				if (!SetUtils.isNullList(insertParamList))
				{
					Optional<SqlParamData> optional = insertParamList.stream().filter(param -> param.getParamName().equalsIgnoreCase("GUID")).findFirst();
					if (optional.isPresent())
					{
						SqlParamData param = optional.get();
						if (!StringUtils.isGuid((String) param.getVal()))
						{
							param.setVal(guid);
						}
						else
						{
							guid = (String) param.getVal();
						}
					}
					else
					{
						insertParamList.add(0, new SqlParamData("GUID", guid, String.class));
					}
				}
				invocation.proceed();
				return guid;
			}
			return invocation.proceed();
		}
		else if (!(args[1] instanceof Map))
		{
			return invocation.proceed();
		}

		Map<String, Object> param = (Map<String, Object>) args[1];
		if (param.get("CURRENTTIME") == null)
		{
			param.put("CURRENTTIME", new Date());
		}
		if ("insert".equals(method.getName()) && (!param.containsKey("GUID") || !StringUtils.isGuid((String) param.get("GUID"))))
		{
			String guid = StringUtils.generateRandomUID(32).toUpperCase();
			param.put("GUID", guid);
			invocation.proceed();
			return guid;
		}
		return invocation.proceed();
	}

	/**
	 * 	生成执行代理
	 */
	@Override public Object plugin(Object target)
	{
		return Plugin.wrap(target, this);
	}

	@Override public void setProperties(Properties properties)
	{

	}
}
