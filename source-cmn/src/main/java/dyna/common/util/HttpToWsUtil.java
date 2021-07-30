package dyna.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * Http方式调用WebService
 */
public class HttpToWsUtil
{
	protected String						urlWsdl			= "";
	protected String						targetNamespace	= "";
	protected String						method			= "";
	protected String[]						paramArr		= null;
	private Map<String, String>	paramMap		= null;

	public HttpToWsUtil()
	{
		super();
	}

	/**
	 * paramArr-顺序传递参数
	 * 
	 * @param urlWsdl
	 * @param targetNamespace
	 * @param method
	 * @param paramArr
	 */
	public HttpToWsUtil(String urlWsdl, String targetNamespace, String method, String[] paramArr)
	{
		super();
		this.urlWsdl = urlWsdl;
		this.targetNamespace = targetNamespace;
		this.method = method;
		this.paramArr = paramArr;
	}

	/**
	 * paramMap-键值对传递参数
	 * 
	 * @param urlWsdl
	 * @param targetNamespace
	 * @param method
	 * @param paramMap
	 */
	public HttpToWsUtil(String urlWsdl, String targetNamespace, String method, Map<String, String> paramMap)
	{
		super();
		this.urlWsdl = urlWsdl;
		this.targetNamespace = targetNamespace;
		this.method = method;
		this.paramMap = paramMap;
	}

	/**
	 * HttpURLConnection方式 (paramArr-顺序传递参数)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String httpURLConnection() throws Exception
	{
		return this.httpURLConnection(urlWsdl, targetNamespace, method, paramArr);
	}

	/**
	 * HttpURLConnection方式 (paramMap-键值对传递参数)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String httpURLConnection2() throws Exception
	{
		return this.httpURLConnection(urlWsdl, targetNamespace, method, paramMap);
	}

	/**
	 * HttpURLConnection方式 (顺序传递参数)
	 * 
	 * @param urlWsdl
	 * @param targetNamespace
	 * @param method
	 * @param paramArr
	 * @return
	 * @throws Exception
	 */
	public String httpURLConnection(String urlWsdl, String targetNamespace, String method, String[] paramArr) throws Exception
	{
		String params = "";
		if (paramArr != null && paramArr.length > 0)
		{
			for (int i = 0; i < paramArr.length; i++)
			{
				String param = StringUtils.convertNULLtoString(paramArr[i]);
				boolean isXml = false;
				try
				{
					new SAXBuilder().build(new StringReader(param));
					isXml = true;
				}
				catch (JDOMException e)
				{
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				if (isXml)
				{
					param = escapeXmlString(param);
				}
				params = params + "<web:param" + i + ">" + param + "</web:param" + i + ">";
			}
		}
		String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"" + targetNamespace + "\">" + "<soapenv:Header/>"
				+ "<soapenv:Body>" + "<web:" + method + ">" + params + "</web:" + method + ">" + "</soapenv:Body>" + "</soapenv:Envelope>";

		return this.httpURLConnection(urlWsdl, targetNamespace, method, soap);
	}

	/**
	 * HttpURLConnection方式 (键值对传递参数)
	 * 
	 * @param urlWsdl
	 * @param targetNamespace
	 * @param method
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String httpURLConnection(String urlWsdl, String targetNamespace, String method, Map<String, String> paramMap) throws Exception
	{
		String params = "";
		if (!SetUtils.isNullMap(paramMap))
		{
			for (Entry<String, String> entry : paramMap.entrySet())
			{
				String key = entry.getKey();
				String value = StringUtils.convertNULLtoString(entry.getValue());
				boolean isXml = false;
				try
				{
					new SAXBuilder().build(new StringReader(value));
					isXml = true;
				}
				catch (JDOMException e)
				{
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				if (isXml)
				{
					value = escapeXmlString(value);
				}
				params = params + "<web:" + key + ">" + value + "</web:" + key + ">";
			}
		}
		String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"" + targetNamespace + "\">" + "<soapenv:Header/>"
				+ "<soapenv:Body>" + "<web:" + method + ">" + params + "</web:" + method + ">" + "</soapenv:Body>" + "</soapenv:Envelope>";

		return this.httpURLConnection(urlWsdl, targetNamespace, method, soap);
	}

	/**
	 * HttpURLConnection方式
	 * 
	 * @param urlWsdl
	 * @param targetNamespace
	 * @param method
	 * @param soap
	 * @return
	 * @throws Exception
	 */
	protected String httpURLConnection(String urlWsdl, String targetNamespace, String method, String soap) throws Exception
	{
		HttpURLConnection connection = null;
		try
		{
			System.out.println("[httpURLConnection][REQUEST]: " + soap);
			URL url = new URL(urlWsdl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			connection.setRequestProperty("SOAPAction", targetNamespace + method); // .net需要声明SOAPAction
			connection.connect();

			setBytesToOutputStream(connection.getOutputStream(), soap.getBytes());
			System.out.println("[httpURLConnection][CODE]: " + connection.getResponseCode());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED || connection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				String result = "";
				if (connection.getInputStream() != null)
				{
					byte[] b = getBytesFromInputStream(connection.getInputStream());
					result = new String(b);
					System.out.println("[httpURLConnection][RESULT]: " + result);
					result = this.parseResult(result, targetNamespace, method);
				}
				return result;
			}
			else
			{
				String errMsg = "";
				if (connection.getErrorStream() != null)
				{
					byte[] b = getBytesFromInputStream(connection.getErrorStream());
					errMsg = new String(b);
					System.out.println("[httpURLConnection][ERRMSG]: " + errMsg);
				}
				throw new Exception("HttpToWs Exception[" + connection.getResponseCode() + "] " + errMsg);
			}
		}
		finally
		{
			connection.disconnect();
		}
	}

	/**
	 * 解析结果 (解析出错情况重写该方法)
	 * 
	 * @param result
	 * @param targetNamespace
	 * @param method
	 * @return
	 */
	protected String parseResult(String result, String targetNamespace, String method)
	{
		try
		{
			if (!StringUtils.isNullString(result))
			{
				Namespace namespace = Namespace.getNamespace(targetNamespace);
				Element rootElement = new SAXBuilder().build(new StringReader(result)).getRootElement();
				Element bodyElement = rootElement.getChild("Body", Namespace.getNamespace("http://schemas.xmlsoap.org/soap/envelope/"));
				Element responseElement = bodyElement.getChild(method + "Response", namespace);
				Element resultElement = responseElement.getChild(method + "Result", namespace);
				if (resultElement == null)
				{
					resultElement = responseElement.getChild("return", namespace);
				}
				if (resultElement != null)
				{
					result = resultElement.getText();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 从输入流获取数据
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromInputStream(InputStream in) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len;
		while ((len = in.read(b)) != -1)
		{
			baos.write(b, 0, len);
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	/**
	 * 向输出流发送数据
	 * 
	 * @param out
	 * @param bytes
	 * @throws IOException
	 */
	public static void setBytesToOutputStream(OutputStream out, byte[] bytes) throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		byte[] b = new byte[1024];
		int len;
		while ((len = bais.read(b)) != -1)
		{
			out.write(b, 0, len);
		}
		out.flush();
	}

	/**
	 * 转义XML字符串
	 * 
	 * @return
	 */
	public static String escapeXmlString(String value)
	{
		if (value == null)
		{
			return null;
		}

		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
			switch (content[i])
			{
			case 60: // '<'
				result.append("&lt;");
				break;

			case 62: // '>'
				result.append("&gt;");
				break;

			case 38: // '&'
				result.append("&amp;");
				break;

			case 34: // '"'
				result.append("&quot;");
				break;

			case 39: // '\''
				result.append("&#39;");
				break;

			default:
				result.append(content[i]);
				break;
			}

		return result.toString();
	}

	public String getUrlWsdl()
	{
		return urlWsdl;
	}

	public void setUrlWsdl(String urlWsdl)
	{
		this.urlWsdl = urlWsdl;
	}

	public String getTargetNamespace()
	{
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace)
	{
		this.targetNamespace = targetNamespace;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String[] getParamArr()
	{
		return paramArr;
	}

	public void setParamArr(String[] paramArr)
	{
		this.paramArr = paramArr;
	}

	public Map<String, String> getParamMap()
	{
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap)
	{
		this.paramMap = paramMap;
	}
}
