package dyna.common.util;

/**
 * @author lufeia
 * @date 2016-1-4 下午2:08:21
 * @return
 */
public class CharUtils
{
	/**
	 * 根据Unicode编码判断中文汉字和符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c)
	{
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
		{
			return true;
		}
		return false;
	}
	
	public static String intTOChineseChar(int number)
	{
		String s = "";
		if (number == 1)
		{
			s = "一";
		}
		else if (number == 2)
		{
			s = "二";
		}
		else if (number == 3)
		{
			s = "三";
		}
		else if (number == 4)
		{
			s = "四";
		}
		else if (number == 5)
		{
			s = "五";
		}
		else if (number == 6)
		{
			s = "六";
		}
		return s;
	}

}
