/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractConfigLoader
 * Wanglei 2010-11-30
 */
package dyna.common.conf.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import dyna.common.Configurable;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public abstract class AbstractConfigLoader<T extends Configurable> implements ConfigLoader<T>
{

	protected ConfigurableKVElementImpl	kvElement	= null;

	protected File						configFile	= null;

	private InputSource					inputSource	= null;

	private final SAXBuilder			builder		= new SAXBuilder();

	private Document					doc			= null;

	public File getConfigFile()
	{
		return this.configFile;
	}

	public void setConfigFile(File file)
	{
		this.configFile = file;
		this.doc = null;
	}
	
	public T load(InputSource inputSource)
	{
		return null;
	}

	public void setConfigInputSource(InputSource inputSource)
	{
		this.inputSource = inputSource;
		this.doc = null;
	}

	private Document getXMLDocument() throws JDOMException, IOException
	{
		if (this.doc == null)
		{
			if (this.inputSource != null)
			{
				this.doc = this.builder.build(inputSource);
			}
			else
			{
				this.doc = this.builder.build(this.configFile);
			}
			this.kvElement = new ConfigurableKVElementImpl();
		}
		return this.doc;
	}

	/**
	 * 按照默认的方式加载xml配置, 使用key(path) - value来保存配置信息
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	protected ConfigurableKVElementImpl loadDefault()
	{

		try
		{
			Element root = this.getXMLDocument().getRootElement();
			List<String> tempKeyList = new ArrayList<String>();
			this.handleElementList(root.getName(), Arrays.asList(root), tempKeyList);
			tempKeyList.clear();
			tempKeyList = null;
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.kvElement.configured();
		return this.kvElement;

	}

	/**
	 * 处理xml中元素的内容
	 * 
	 * @param parentPath
	 *            父元素的路径(包含自身)
	 * @param elList
	 *            元素列表
	 * @param tempKeyList
	 *            临时列表, 用于缓存数据, 处理完成之后自动清空
	 */
	@SuppressWarnings("unchecked")
	private void handleElementList(String parentPath, List<Element> elList, List<String> tempKeyList)
	{
		int index = 0;
		String path = null;
		String newPath = null;
		String name = null;
		List<Element> childList = null;
		List<Attribute> attrList = null;

		// 保存 相同元素 的数量
		int size = elList.size();
		if (size > 1)
		{
			this.kvElement.setSize(parentPath, size);
		}

		for (Element element : elList)
		{

			if (index == 0)
			{
				path = parentPath;
			}
			else
			{
				path = parentPath + ".<" + index + ">";
			}
			index++;

			// 元素的属性
			attrList = element.getAttributes();
			for (Attribute attr : attrList)
			{
				this.kvElement.setAttributeValue(path + "." + attr.getName(), attr.getValue());
			}

			// 元素的内容
			String text = element.getTextTrim();
			if (!StringUtils.isNullString(text))
			{
				this.kvElement.setElementValue(path, text);
			}

			// 元素的子元素
			childList = element.getChildren();

			if (childList.isEmpty())
			{
			}
			else
			{
				for (Element el : childList)
				{
					name = el.getName();
					newPath = path + "." + name;
					if (tempKeyList.contains(newPath))
					{
						continue;
					}
					this.handleElementList(newPath, element.getChildren(name), tempKeyList);
					tempKeyList.add(newPath);
				}
			}
		}

	}

}
