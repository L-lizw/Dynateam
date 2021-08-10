/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CalendarStub
 * WangLHB May 7, 2012
 */
package dyna.app.service.brs.ppms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.common.bean.data.ppms.indicator.*;
import dyna.common.bean.data.ppms.indicator.chart.IndicatorView;
import dyna.common.bean.data.ppms.indicator.chart.IndicatorViewChartSet;
import dyna.common.bean.data.ppms.indicator.chart.IndicatorViewRow;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ppms.ChartTypeEnum;
import dyna.common.systemenum.ppms.ChartValueTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 * 指标数相关方法
 * 
 * @author duanll
 * 
 */
public class IndicatorConfigStub extends AbstractServiceStub<PPMSImpl>
{
	// 配置文件修改时间
	private static Map<String, Long>		CONFIG_MODIFY_TIME_CACHE	= Collections.synchronizedMap(new HashMap<String, Long>());

	// 指标显示配置
	private static List<IndicatorView>		INDICATOR_SHOW_VIEW_CACHE	= Collections.synchronizedList(new ArrayList<IndicatorView>());

	// 指标
	private static List<DefineIndicator>	INDICATOR_CACHE				= Collections.synchronizedList(new ArrayList<DefineIndicator>());

	private static List<DefineIndicatorVal>	INDICATOR_VAL_CACHE			= Collections.synchronizedList(new ArrayList<DefineIndicatorVal>());

	// 函数
	private static List<FunctionBean>		FUNCTION_CACHE				= Collections.synchronizedList(new ArrayList<FunctionBean>());

	/**
	 * @param context
	 * @param service
	 */
	protected IndicatorConfigStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	protected static void init()
	{
		CONFIG_MODIFY_TIME_CACHE.clear();

		reloadIndicatorShowConfig();
		reloadFunctionConfig();
		reloadIndicatorConfig();
	}

	protected List<IndicatorView> listIndicatorViewSet() throws ServiceRequestException
	{
		// 配置文件发生变更，需要重新读取
		if (isIndicatorShowConfigModified())
		{
			reloadIndicatorShowConfig();
		}
		return INDICATOR_SHOW_VIEW_CACHE;
	}

	protected List<DefineIndicator> listDefinedIndicatorConfig()
	{
		// 配置文件发生变更，需要重新读取
		if (isIndicatorConfigModified() || isFunctionConfigModified())
		{
			reloadIndicatorConfig();
		}
		return INDICATOR_CACHE;
	}

	protected DefineIndicator getDefinedIndicatorConfig(String indicatorId)
	{
		List<DefineIndicator> list = listDefinedIndicatorConfig();
		if (StringUtils.isNullString(indicatorId))
		{
			return null;
		}

		if (!SetUtils.isNullList(list))
		{
			for (DefineIndicator indicator : list)
			{
				if (indicatorId.equals(indicator.getId()))
				{
					return indicator;
				}
			}
		}
		return null;
	}

	protected boolean isAccumulate(String indicatorValId)
	{
		if (StringUtils.isNullString(indicatorValId))
		{
			return true;
		}

		if (!SetUtils.isNullList(INDICATOR_VAL_CACHE))
		{
			for (DefineIndicatorVal val : INDICATOR_VAL_CACHE)
			{
				if (indicatorValId.equals(val.getId()))
				{
					return val.isAccumulate();
				}
			}
		}
		return true;
	}

	private static Map<String, FunctionBean> listFunctions()
	{
		// 配置文件发生变更，需要重新读取
		if (isFunctionConfigModified())
		{
			reloadFunctionConfig();
		}

		Map<String, FunctionBean> map = new HashMap<String, FunctionBean>();
		if (!SetUtils.isNullList(FUNCTION_CACHE))
		{
			for (FunctionBean fun : FUNCTION_CACHE)
			{
				map.put(fun.getName(), fun);
			}
		}
		return map;
	}

	private static void reloadIndicatorShowConfig()
	{
		INDICATOR_SHOW_VIEW_CACHE.clear();

		File configFile = IndicatorConstants.INDICATOR_VIEW_CONFIG_FILE;
		long fileModifyTime = configFile.lastModified();

		ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
		configLoader.setConfigFile(configFile);
		configLoader.load();
		ConfigurableKVElementImpl kv = configLoader.getConfigurable();

		ConfigurableKVElementImpl viewKV = null;
		if (kv != null)
		{
			Iterator<ConfigurableKVElementImpl> viewId = kv.iterator("root.views.view");
			while (viewId.hasNext())
			{
				viewKV = viewId.next();

				IndicatorView view = readIndicatorView(viewKV);

				INDICATOR_SHOW_VIEW_CACHE.add(view);
			}
		}

		CONFIG_MODIFY_TIME_CACHE.put(IndicatorConstants.INDICATOR_VIEW_CONFIG_FILE_NAME, fileModifyTime);
	}

	/**
	 * 是否有子阶view
	 * 
	 * @param viewKV
	 * @return
	 */
	private static boolean hasChildView(ConfigurableKVElementImpl viewKV)
	{
		Iterator<ConfigurableKVElementImpl> viewIt = viewKV.iterator("view");
		while (viewIt.hasNext())
		{
			return true;
		}
		return false;
	}

	/**
	 * 读取view内容
	 * 
	 * @param viewKV
	 * @return
	 */
	private static IndicatorView readIndicatorView(ConfigurableKVElementImpl viewKV)
	{
		IndicatorView view = new IndicatorView();
		view.setName(viewKV.getAttributeValue("name"));
		view.setId(viewKV.getAttributeValue("id"));
		if (StringUtils.isNullString(view.getId()))
		{
			// 编号不能为空
			return null;
		}

		// 有子阶view
		if (hasChildView(viewKV))
		{
			Iterator<ConfigurableKVElementImpl> childViewIt = viewKV.iterator("view");
			while (childViewIt.hasNext())
			{
				ConfigurableKVElementImpl childViewKV = childViewIt.next();
				IndicatorView childView = readIndicatorView(childViewKV);
				view.addChild(childView);
			}
			return view;
		}

		// 没有子阶，添加row
		Iterator<ConfigurableKVElementImpl> rowIt = viewKV.iterator("rows.row");
		ConfigurableKVElementImpl rowKV = null;
		while (rowIt.hasNext())
		{
			rowKV = rowIt.next();

			IndicatorViewRow row = new IndicatorViewRow();
			row.setName(rowKV.getAttributeValue("name"));

			Iterator<ConfigurableKVElementImpl> setIt = rowKV.iterator("indicator");
			ConfigurableKVElementImpl setKV = null;
			while (setIt.hasNext())
			{
				setKV = setIt.next();

				IndicatorViewChartSet chartSet = new IndicatorViewChartSet();
				chartSet.setIndicatorId(setKV.getAttributeValue("id"));
				chartSet.setIndicatorName(setKV.getAttributeValue("name"));

				String chartType = setKV.getAttributeValue("charttype");
				if (!StringUtils.isNullString(chartType))
				{
					chartSet.setChartType(ChartTypeEnum.valueOf(chartType));
				}
				else
				{
					chartSet.setChartType(ChartTypeEnum.LINE);
				}

				String valueType = setKV.getAttributeValue("valuetype");
				if (!StringUtils.isNullString(valueType))
				{
					chartSet.setValueType(ChartValueTypeEnum.valueOf(valueType));
				}
				else
				{
					chartSet.setValueType(ChartValueTypeEnum.NUMBER);
				}

				Map<String, String> styleMap = new HashMap<String, String>();
				chartSet.setStyleMap(styleMap);

				Iterator<ConfigurableKVElementImpl> styleListIt = setKV.iterator("style-list");
				while (styleListIt.hasNext())
				{
					ConfigurableKVElementImpl styleListKV = styleListIt.next();
					Iterator<ConfigurableKVElementImpl> styleIt = styleListKV.iterator("style");
					while (styleIt.hasNext())
					{
						ConfigurableKVElementImpl styleKV = styleIt.next();
						String styleName = styleKV.getAttributeValue("name");
						String style = styleKV.getAttributeValue("value");
						if (!StringUtils.isNullString(styleName) && !StringUtils.isNullString(style))
						{
							styleMap.put(styleName, style);
						}
					}
				}

				row.addChartSet(chartSet);
			}

			view.addRow(row);
		}

		return view;
	}

	private static void reloadFunctionConfig()
	{
		FUNCTION_CACHE.clear();

		File configFile = IndicatorConstants.INDICATOR_FUN_CONFIG_FILE;
		long fileModifyTime = configFile.lastModified();

		ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
		configLoader.setConfigFile(configFile);
		configLoader.load();
		ConfigurableKVElementImpl kv = configLoader.getConfigurable();

		ConfigurableKVElementImpl tempKV = null;
		if (kv != null)
		{
			Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.functions.function");
			while (firstIt.hasNext())
			{
				tempKV = firstIt.next();

				String functionName = tempKV.getAttributeValue("name");
				String functionClass = tempKV.getAttributeValue("class");
				String desc = tempKV.getAttributeValue("description");

				FunctionBean function = new FunctionBean();
				function.setName(functionName);
				function.setDescription(desc);
				function.setClassName(functionClass);

				if (StringUtils.isNullString(functionName) || StringUtils.isNullString(functionClass))
				{
					continue;
				}

				FUNCTION_CACHE.add(function);
			}
		}

		CONFIG_MODIFY_TIME_CACHE.put(IndicatorConstants.INDICATOR_FUN_CONFIG_FILE_NAME, fileModifyTime);
	}

	private static void reloadIndicatorConfig()
	{
		INDICATOR_CACHE.clear();
		INDICATOR_VAL_CACHE.clear();

		File configFile = IndicatorConstants.INDICATOR_CONFIG_FILE;
		long fileModifyTime = configFile.lastModified();

		ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
		configLoader.setConfigFile(configFile);
		configLoader.load();
		ConfigurableKVElementImpl kv = configLoader.getConfigurable();

		INDICATOR_CACHE.addAll(loadDefineIndicator(kv));
		INDICATOR_VAL_CACHE.addAll(loadDefineIndicatorVal(kv));

		CONFIG_MODIFY_TIME_CACHE.put(IndicatorConstants.INDICATOR_CONFIG_FILE_NAME, fileModifyTime);
	}

	private static List<DefineIndicator> loadDefineIndicator(ConfigurableKVElementImpl kv)
	{
		List<DefineIndicator> list = new ArrayList<DefineIndicator>();

		Map<String, FunctionBean> functionMap = listFunctions();
		ConfigurableKVElementImpl tempKV = null;
		if (kv != null)
		{
			Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.indicators.indicator");
			while (firstIt.hasNext())
			{
				tempKV = firstIt.next();
				String id = tempKV.getAttributeValue("id");
				String name = tempKV.getAttributeValue("name");
				String function = tempKV.getAttributeValue("function");

				DefineIndicator indicator = new DefineIndicator();
				indicator.setId(id);
				indicator.setName(name);
				indicator.setFunction(functionMap.get(function));

				setIndicatorDismension(indicator, tempKV);

				setIndicatorParams(indicator, tempKV);

				list.add(indicator);
			}
		}
		return list;
	}

	private static List<DefineIndicatorVal> loadDefineIndicatorVal(ConfigurableKVElementImpl kv)
	{
		List<DefineIndicatorVal> list = new ArrayList<DefineIndicatorVal>();

		ConfigurableKVElementImpl tempKV = null;
		if (kv != null)
		{
			Iterator<ConfigurableKVElementImpl> firstIt = kv.iterator("root.indicator-vals.indicator-val");
			while (firstIt.hasNext())
			{
				tempKV = firstIt.next();
				String id = tempKV.getAttributeValue("id");
				String name = tempKV.getAttributeValue("name");
				String accumulateStr = tempKV.getAttributeValue("accumulate");

				DefineIndicatorVal indicator = new DefineIndicatorVal();
				indicator.setId(id);
				indicator.setName(name);
				indicator.setAccumulate(StringUtils.isNullString(accumulateStr) ? true : !"false".equals(accumulateStr));

				list.add(indicator);
			}
		}
		return list;
	}

	private static void setIndicatorDismension(DefineIndicator indicator, ConfigurableKVElementImpl kv)
	{
		Iterator<ConfigurableKVElementImpl> filtersIt = kv.iterator("dismension-filters");
		if (filtersIt.hasNext())
		{
			ConfigurableKVElementImpl filtersKV = filtersIt.next();
			String dismensionName = filtersKV.getAttributeValue("name");

			List<IndicatorDismensionFilter> filters = new ArrayList<IndicatorDismensionFilter>();
			Iterator<ConfigurableKVElementImpl> filterIt = filtersKV.iterator("filter");
			while (filterIt.hasNext())
			{
				ConfigurableKVElementImpl filterKV = filterIt.next();
				String filterName = filterKV.getAttributeValue("name");
				String filterValue = filterKV.getAttributeValue("value");

				IndicatorDismensionFilter filter = new IndicatorDismensionFilter();
				filter.setFilterName(filterName);
				if (!StringUtils.isNullString(filterValue))
				{
					filter.setFilters(filterValue);
				}
				filters.add(filter);
			}

			IndicatorDismension dismension = new IndicatorDismension();
			dismension.setDismensionName(dismensionName);
			dismension.setFilters(filters);
			indicator.setDismension(dismension);
		}
	}

	private static void setIndicatorParams(DefineIndicator indicator, ConfigurableKVElementImpl kv)
	{
		Iterator<ConfigurableKVElementImpl> paramsIt = kv.iterator("params");
		if (paramsIt.hasNext())
		{
			ConfigurableKVElementImpl paramsKV = paramsIt.next();

			List<Map<String, String>> params = new ArrayList<Map<String, String>>();
			Iterator<ConfigurableKVElementImpl> paramIt = paramsKV.iterator("param");
			while (paramIt.hasNext())
			{
				ConfigurableKVElementImpl paramKV = paramIt.next();
				String paramName = paramKV.getAttributeValue("name");
				String paramValue = paramKV.getAttributeValue("value");

				Map<String, String> tmpMap = new HashMap<String, String>();
				tmpMap.put(paramName, paramValue);
				params.add(tmpMap);
			}

			indicator.setParams(params);
		}
	}

	private static boolean isIndicatorShowConfigModified()
	{
		File configFile = IndicatorConstants.INDICATOR_VIEW_CONFIG_FILE;
		long fileModifyTime = configFile.lastModified();
		if (CONFIG_MODIFY_TIME_CACHE.containsKey(IndicatorConstants.INDICATOR_VIEW_CONFIG_FILE_NAME))
		{
			if (fileModifyTime <= CONFIG_MODIFY_TIME_CACHE.get(IndicatorConstants.INDICATOR_VIEW_CONFIG_FILE_NAME).longValue())
			{
				return false;
			}
		}
		return true;
	}

	private static boolean isFunctionConfigModified()
	{
		File configFile = IndicatorConstants.INDICATOR_FUN_CONFIG_FILE;
		long fileModifyTime = configFile.lastModified();
		if (CONFIG_MODIFY_TIME_CACHE.containsKey(IndicatorConstants.INDICATOR_FUN_CONFIG_FILE_NAME))
		{
			if (fileModifyTime <= CONFIG_MODIFY_TIME_CACHE.get(IndicatorConstants.INDICATOR_FUN_CONFIG_FILE_NAME).longValue())
			{
				return false;
			}
		}
		return true;
	}

	private static boolean isIndicatorConfigModified()
	{
		File configFile = IndicatorConstants.INDICATOR_CONFIG_FILE;
		long fileModifyTime = configFile.lastModified();
		if (CONFIG_MODIFY_TIME_CACHE.containsKey(IndicatorConstants.INDICATOR_CONFIG_FILE_NAME))
		{
			if (fileModifyTime <= CONFIG_MODIFY_TIME_CACHE.get(IndicatorConstants.INDICATOR_CONFIG_FILE_NAME).longValue())
			{
				return false;
			}
		}
		return true;
	}
}
