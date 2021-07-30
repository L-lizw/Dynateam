package dyna.data;

import dyna.common.sqlbuilder.plmdynamic.DynamicSqlParamData;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.insert.DynamicInsertParamData;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SqlExecuteInvocationHandler implements InvocationHandler
{
	// 保留SQLClient对象
	private Object object;

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		try
		{
			if (!(this.object instanceof SqlSessionFactory))
			{
				SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)this.object;
				SqlSession sqlSession = sqlSessionFactory.openSession(true);
				return method.invoke(sqlSession, args);
			}

			if (args == null || args.length <= 1)
			{
				return method.invoke(this.object, args);
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
					method.invoke(this.object, args);
					return guid;
				}
				return method.invoke(this.object, args);
			}
			else if (!(args[1] instanceof Map))
			{
				return method.invoke(this.object, args);
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
				method.invoke(this.object, args);
				return guid;
			}
			return method.invoke(this.object, args);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (InvocationTargetException e)
		{
			Throwable t = e.getTargetException();
			t.printStackTrace();
			throw t;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public void setExecuteClient(Object object)
	{
		this.object = object;
	}
}
