package dyna.common.bean.data.ppms.indicator;

import java.io.File;

import dyna.common.util.EnvUtils;

public class IndicatorConstants
{
	public static final String	INDICATOR_CONFIG_FOLDER			= EnvUtils.getConfRootPath() + "conf";

	public static final String	INDICATOR_CONFIG_FILE_NAME		= "indicatorConf.xml";

	public static final String	INDICATOR_FUN_CONFIG_FILE_NAME	= "indicatorFunctionConf.xml";

	public static final String	INDICATOR_VIEW_CONFIG_FILE_NAME	= "indicatorViewSetConf.xml";

	// 指标
	public static final File	INDICATOR_CONFIG_FILE			= new File(INDICATOR_CONFIG_FOLDER + File.separator + INDICATOR_CONFIG_FILE_NAME);

	// 指标显示
	public static final File	INDICATOR_VIEW_CONFIG_FILE		= new File(INDICATOR_CONFIG_FOLDER + File.separator + INDICATOR_VIEW_CONFIG_FILE_NAME);

	// 指标函数
	public static final File	INDICATOR_FUN_CONFIG_FILE		= new File(INDICATOR_CONFIG_FOLDER + File.separator + INDICATOR_FUN_CONFIG_FILE_NAME);

	// 不设置维度时，默认值，临时用
	public static final String	DISMENSION_WHEN_NULL			= "0CBDE4E87CDB43ABA50369D961594394=@=$=#=";

	// 显示最近12个月的数据
	public static final Integer	LIMIT_NUMBER_OF_MONTH			= 12;

	// 显示最近6个季度的数据
	public static final Integer	LIMIT_NUMBER_OF_QUARTER			= 6;

	// 显示最近3年的数据
	public static final Integer	LIMIT_NUMBER_OF_YEAR			= 3;

	// 显示最近6个半年的数据
	public static final Integer	LIMIT_NUMBER_OF_HALFYEAR		= 6;

	public interface INDICATOR_PARAMS
	{
		public interface FIXED_PARAMS
		{
			public static final String	NUMERATOR	= "numerator";		// 分子
			public static final String	DENOMINATOR	= "denominator";	// 分母
		};
	};
}
