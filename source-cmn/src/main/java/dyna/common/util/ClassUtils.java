package dyna.common.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils
{
	public static Set<Class<?>> listAllClassOfPackage(String packageName) throws IOException
	{
		Map<String, String> classMap = new HashMap<String, String>();
		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
		while (urls != null && urls.hasMoreElements())
		{
			URL url = urls.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol))
			{
				String file = URLDecoder.decode(url.getFile(), "UTF-8");
				File dir = new File(file);
				if (dir.isDirectory())
				{
					parseClassFile(dir, classMap);
				}
				else
				{
					throw new IllegalArgumentException("file must be directory");
				}
			}
			else if ("jar".equals(protocol))
			{
				parseJarFile(url, packageName, classMap);
			}
		}

		Set<Class<?>> set = new HashSet<Class<?>>(classMap.size());
		for (String key : classMap.keySet())
		{
			String className = classMap.get(key);
			try
			{
				set.add(Class.forName(className));
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return set;
	}

	private static void parseClassFile(File dir, Map<String, String> classMap)
	{
		if (dir.isDirectory())
		{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				parseClassFile(file, classMap);
			}
		}
		else if (dir.getName().endsWith(".class"))
		{
			String name = dir.getPath();
			name = name.substring(name.indexOf("dyna")).replace("\\", ".");
			name = name.substring(name.indexOf("dyna")).replace("/", ".");
			addToClassMap(name, classMap);
		}
	}

	private static void parseJarFile(URL url, String packageName, Map<String, String> classMap) throws IOException
	{
		JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements())
		{
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory())
			{
				continue;
			}
			String name = entry.getName().replace("/", ".");
			if (name.endsWith(".class") && name.startsWith(packageName))
			{
				addToClassMap(name, classMap);
			}
		}
	}

	private static void addToClassMap(String name, Map<String, String> classMap)
	{
		if (name.indexOf("$") > 0)
		{
			return;
		}
		if (!classMap.containsKey(name))
		{
			// 去掉.class
			classMap.put(name, name.substring(0, name.length() - 6));
		}
	}
}
