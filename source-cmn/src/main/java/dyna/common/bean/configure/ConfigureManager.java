/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SyncConfigureManager
 * WangLHB May 5, 2011
 */
package dyna.common.bean.configure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

import dyna.common.util.FileUtils;

/**
 * @author WangLHB
 * 
 */
public abstract class ConfigureManager<T>
{
	private File	file	= null;

	private T		model	= null;

	public ConfigureManager()
	{

	}

	public ConfigureManager(String filePath) throws Exception
	{
		this.init(filePath);
	}

	public void init(String filePath) throws Exception
	{
		this.file = FileUtils.newFileEscape(filePath);
		if (!this.file.canWrite())
		{
			this.file.setWritable(true, false);
		}

		if (!this.file.exists())
		{
			FileOutputStream fos = null;
			try
			{
				this.file.createNewFile();

				fos = new FileOutputStream(this.file);
				fos.write(this.createXML().getBytes());
				fos.flush();
				fos.close();
			}
			catch (IOException e)
			{

				throw e;
			}
			finally
			{
				try
				{
					if (fos != null)
					{
						fos.close();
					}
				}
				catch (IOException e)
				{
					throw e;
				}
			}
		}

	}

	/**
	 * 通过XMLModel对象作成 XML文件
	 * 
	 * @param model
	 * @return
	 */
	public void write() throws Exception
	{

		XStream xstream = new XStream();
		xstream.processAnnotations(this.getModel().getClass());

		// Runtime.getRuntime().exec("attrib " + "\"" + this.file.getAbsolutePath() + "\"" + " -H");
		// Thread.sleep(200);
		FileOutputStream fileOutputStream = null;
		Writer writer = null;
		try
		{
			fileOutputStream = new FileOutputStream(this.file);

			writer = new OutputStreamWriter(fileOutputStream, "UTF-8");
			xstream.toXML(this.getModel(), writer);
		}
		catch (FileNotFoundException e)
		{

			throw e;
		}

		catch (UnsupportedEncodingException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
				if (fileOutputStream != null)
				{
					fileOutputStream.close();
				}
			}
			catch (IOException e)
			{
				throw e;
			}
		}
	}

	public void write(String xmlStream) throws Exception
	{
		BufferedWriter output = null;
		try
		{

			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), "UTF-8"));
			output.write(xmlStream);
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (output != null)
				{
					output.flush();
					output.close();
				}
			}
			catch (IOException e)
			{
				throw e;
			}
		}
	}

	public String readFile() throws Exception
	{
		StringBuffer xmlStream = new StringBuffer();
		String buf = null;
		BufferedReader output = null;
		try
		{
			output = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), "UTF-8"));
			while ((buf = output.readLine()) != null)
			{
				xmlStream.append(buf);
			}

			return xmlStream.toString();
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				output.close();
			}
			catch (IOException e)
			{
				throw e;
			}
		}
	}

	public T read() throws Exception
	{
		FileInputStream fileInputStream = new FileInputStream(this.file);
		Reader reader = new InputStreamReader(fileInputStream, "UTF-8");
		this.toModel(reader);
		if (reader != null)
		{
			reader.close();
		}

		if (fileInputStream != null)
		{
			fileInputStream.close();
		}

		return this.getModel();

	}

	/**
	 * 把XML文件流转换为XMLModel对象
	 * 
	 * @param inModel
	 * @param xmlStream
	 * @param rootElement
	 * @return
	 * @throws CADINullException
	 */
	@SuppressWarnings("unchecked")
	private void toModel(Reader fis) throws Exception
	{

		XStream xstream = new XStream();
		xstream.processAnnotations(this.getModel().getClass());

		this.model = (T) xstream.fromXML(fis);

	}

	public T getModel()
	{
		return this.model;
	}

	public void setModel(T model)
	{
		this.model = model;
	}

	/**
	 * 当配置文件不存在时，创建缺省配置文件
	 * 
	 * @return
	 */
	public abstract String createXML();
}
