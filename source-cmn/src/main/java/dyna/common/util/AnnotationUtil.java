package dyna.common.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AnnotationUtil
{
	public static Set<Class<?>> scanAnnotation(String packageName, Class<? extends Annotation> annotationTag)
	{
		Set<Class<?>> classSet = null;
		try
		{
			classSet = ClassUtils.listAllClassOfPackage(packageName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Set<Class<?>> set = new HashSet<Class<?>>();
		if (!SetUtils.isNullSet(classSet))
		{
			for (Iterator<Class<?>> it = classSet.iterator(); it.hasNext();)
			{
				Class<?> clz = it.next();
				if (clz.isAnnotationPresent(annotationTag))
				{
					set.add(clz);
				}
			}
		}
		return set;
	}
}
