package dyna.common.bean.data.ppms.indicator.function;

import java.lang.reflect.Constructor;
import java.util.List;

public class FunctionFactory
{
	public static AbstractFunction createFunction(String className, Object data)
	{
		Class<?> jobExecutorClass;
		try
		{
			jobExecutorClass = Class.forName(className);
			Constructor<?> constructor = jobExecutorClass.getConstructor(List.class);
			Object newInstance = constructor.newInstance(data);
			if (newInstance instanceof AbstractFunction)
			{
				return (AbstractFunction) newInstance;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
