package dyna.app.report;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class DataSourceProvider implements JRDataSource
{
	private List<Map<String, Object>>	lista	= new ArrayList<Map<String, Object>>();
	private int							index	= -1;

	public DataSourceProvider(List<Map<String, Object>> lista)
	{
		this.lista = lista;
	}

	public Object getFieldValue(JRField arg0) throws JRException
	{
		// TODO Auto-generated method stub
		return null == this.lista.get(index).get(arg0.getName().replace("#", "$")) ? "" : this.lista.get(index).get(arg0.getName().replace("#", "$")).toString();
	}

	public boolean next() throws JRException
	{
		index++;
		return (index < lista.size());
	}

	public List<Map<String, Object>> getLista()
	{
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista)
	{
		this.lista = lista;
	}
}
