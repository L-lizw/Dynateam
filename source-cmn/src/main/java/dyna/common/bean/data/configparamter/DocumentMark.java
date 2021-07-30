package dyna.common.bean.data.configparamter;

import org.jdom.Document;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

public class DocumentMark extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 1L;
	private Document			document;
	private long				lastModified;
	public static final String	FILENAME			= "configexport.xml";

	public DocumentMark(Document document, long lastModified)
	{
		this.document = document;
		this.lastModified = lastModified;
	}

	/**
	 * 文件是否被改动过
	 * 
	 * @param modified
	 * @return
	 */
	public boolean isChanged(long modified)
	{
		return this.lastModified != modified;
	}

	public Document getDocument()
	{
		return this.document;
	}

	public void setDocument(Document document)
	{
		this.document = document;
	}

	public long getLastModified()
	{
		return this.lastModified;
	}

	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}

}
