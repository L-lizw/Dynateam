/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JsonUtils
 * wangweixia 2014-2-10
 */
package dyna.common.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

//import org.codehaus.jackson.JsonGenerationException;
//import org.codehaus.jackson.JsonGenerator;
//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.type.TypeReference;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dyna.common.exception.ServiceRequestException;

/**
 * @author wangweixia
 *         Json的工具类
 */
public class JsonUtils
{
	/**
	 * 将Json字符串转换成对象
	 * 
	 * @param <T>
	 * @param jsonStr
	 * @param resultType
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T getObjectByJsonStr(String jsonStr, Class<T> resultType) throws ServiceRequestException
	{
		try
		{
			ObjectMapper objectMap = new ObjectMapper();

			return objectMap.readValue(jsonStr, resultType);
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_GETOBJECTBYJSONSTR_ERROR", e);
		}
	}

	/**
	 * 将对象转换成字符串
	 * 
	 * @param object
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String writeJsonStr(Object object) throws ServiceRequestException
	{
		ObjectMapper objectMap = new ObjectMapper();
		StringWriter writer = null;
		JsonGenerator gen = null;
		try
		{
			writer = new StringWriter();
			gen = objectMap.getJsonFactory().createJsonGenerator(writer);
			objectMap.writeValue(gen, object);
			gen.close();
			String json = writer.toString();
			writer.close();
			return json;
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_WRITEJSONSTR_ERROR", e);
		}

		finally
		{
			if (gen != null)
			{
				try
				{
					gen.close();
				}
				catch (IOException e)
				{
					throw ServiceRequestException.createByException("ID_APP_WRITEJSONSTR_ERROR", e);
				}
				gen = null;
			}
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					throw ServiceRequestException.createByException("ID_APP_WRITEJSONSTR_ERROR", e);
				}
				writer = null;
			}
		}
	}
	

	public static <T> List<T> listObjectByJsonStr(String jsonStr, Class<T> classObject) throws ServiceRequestException
	{
		try
		{
			ObjectMapper objectMap = new ObjectMapper();
			return objectMap.readValue(jsonStr, new TypeReference<List<T>>() {
			});
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_GETOBJECTBYJSONSTR_ERROR", e);
		}
	}

}
