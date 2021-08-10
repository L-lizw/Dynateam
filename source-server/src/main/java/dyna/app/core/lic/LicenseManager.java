/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LicenseManager license管理器
 * Wanglei 2011-4-20
 */
package dyna.app.core.lic;

import dyna.app.core.lic.system.SystemIDChecker;
import dyna.common.log.DynaLogger;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * license管理器
 * 
 * @author Wanglei
 * 
 */
@Component
public class LicenseManager
{
	private static final String	LIC_FILE				= "License.lic";
	private static final String	PROPERTY_FILE			= "License.properties";
	private static final String	LICENSE_HEADER			= "license.";
	private static final String	LICENSE_PRODUCT_LIST	= "license.product";
	private static final String	LICENSE_PRODUCT			= ".product";
	private static final String	LICENSE_MODULE			= ".module";
	private static final String	LICENSE_VERSION			= ".version";
	private static final String	LICENSE_TYPE			= ".type";
	private static final String	LICENSE_KEY				= ".key";

	public static final String	USER_ID_PREFIX			= "UserID:";

	private Properties			prop					= new Properties();
	private List<String>		licenseRawData			= null;
	private String				systemIdentification	= null;
	private List<License>		licenseTable			= new ArrayList<License>();
	private boolean				isVM					= false;

	public LicenseManager()
	{
		this.loadLicenseProperties();
		this.readLicenseDataFile();
	}

	private void loadLicenseProperties()
	{
		FileInputStream stream = null;
		try
		{
			String prefixPath = EnvUtils.getConfRootPath();

			stream = new FileInputStream(prefixPath + "conf/" + PROPERTY_FILE);
			this.prop.load(stream);
		}
		catch (Exception e)
		{
			DynaLogger.error("", e);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					// do nothing
				}
				stream = null;
			}
		}
	}

	private void readLicenseDataFile()
	{
		String prefixPath = EnvUtils.getConfRootPath();

		if (prefixPath == null)
		{
			this.licenseRawData = this.readFile("./" + LIC_FILE);
		}
		else
		{
			this.licenseRawData = this.readFile(prefixPath + "conf/" + LIC_FILE);
		}

	}

	private List<String> readFile(String fileName)
	{
		List<String> retList = new ArrayList<String>();
		BufferedReader buff = null;
		try
		{
			buff = new BufferedReader(new FileReader(fileName));
			String line = null;

			do
			{
				line = buff.readLine();
				if (line == null)
				{
					break;
				}
				retList.add(line);
			}
			while (true);

		}
		catch (IOException e)
		{
			DynaLogger.error(fileName, e);
		}
		finally
		{
			if (buff != null)
			{
				try
				{
					buff.close();
				}
				catch (IOException e1)
				{
					// do nothing
				}
			}
		}

		return retList;
	}

	private void readSystemIdentification(String ip)
	{
		SystemIDChecker idChecker = SystemIDChecker.Utils.getSystemIDChecker();
		this.systemIdentification = idChecker.getSystemIdentification(this.getLicenseRawData());
		this.isVM = idChecker.isVM();
	}

	private synchronized void readLicense()
	{
		if (SetUtils.isNullList(this.licenseTable) == false)
		{
			return;
		}

		DynaLogger.print("Loading license");
		String licNodesStr = "";
		LicenseDecipher decipher = LicenseDecipher.getInstance();
		List<String> licenseRawData = this.getLicenseRawData();
		String systemIdentity = this.getSystemIdentification();
		for (int i = 0; i < licenseRawData.size(); i += 5)
		{

			String type = licenseRawData.get(i);
			String product = licenseRawData.get(i + 1);
			String module = licenseRawData.get(i + 2);
			String version = licenseRawData.get(i + 3);
			String cipher = licenseRawData.get(i + 4);
			License license = decipher.decipher(type, systemIdentity, product, module, version, cipher);
			if (license != null)
			{
				this.licenseTable.add(license);
				licNodesStr += license.getAvailable();
				DynaLogger.print(".");
			}
		}
		DynaLogger.println("[" + this.getProductList() + "|" + licNodesStr + "]");

	}

	public void testLicenseValid(String ip) throws LicenseException
	{
		this.readSystemIdentification(ip);
		if (this.prop == null)
		{
			DynaLogger.error("Invalid license file: License.properties");
			throw new LicenseException("Invalid license file: License.properties");
		}

		if (SetUtils.isNullList(this.licenseRawData) || (this.licenseRawData.size() % 5) != 0)
		{
			DynaLogger.error("Invalid license file: License.lic");
			throw new LicenseException("Invalid license file: license.lic");
		}

		if (StringUtils.isNullString(this.systemIdentification))
		{
			DynaLogger.error("Invalid license");
			throw new LicenseException("Invalid license");
		}

		this.readLicense();
		if (SetUtils.isNullList(this.licenseTable))
		{
			DynaLogger.error("License Expired");
			throw new LicenseException("License Expired");
		}
	}

	public String getSystemIdentification()
	{
		return this.systemIdentification;
	}

	public List<String> getLicenseRawData()
	{
		return this.licenseRawData;
	}

	private String getProperty(String pName)
	{
		return this.prop.getProperty(pName);
	}

	public String getProductList()
	{
		return this.getProperty(LICENSE_PRODUCT_LIST);
	}

	public String getProductCode(String aProduct)
	{
		return this.getProperty(LICENSE_HEADER + aProduct + LICENSE_PRODUCT);
	}

	public String getModuleCode(String aProduct)
	{
		return this.getProperty(LICENSE_HEADER + aProduct + LICENSE_MODULE);
	}

	public String getVersionCode(String aProduct)
	{
		return this.getProperty(LICENSE_HEADER + aProduct + LICENSE_VERSION);
	}

	public String getTypeCode(String aProduct)
	{
		return this.getProperty(LICENSE_HEADER + aProduct + LICENSE_TYPE);
	}

	public String getKeyCode(String aProduct)
	{
		return this.getProperty(LICENSE_HEADER + aProduct + LICENSE_KEY);
	}

	public List<License> getLicenseTable()
	{
		return Collections.unmodifiableList(this.licenseTable);
	}
	
	public boolean isVM()
	{
		return isVM;
	}

	public void createTempLicense(String id, long date)
	{
		LicenseDecipher decipher = LicenseDecipher.getInstance();
		List<String> licenseRawData = this.getLicenseRawData();
		for (int i = 0; i < licenseRawData.size(); i += 5)
		{

			String type = licenseRawData.get(i);
			String product = licenseRawData.get(i + 1);
			String module = licenseRawData.get(i + 2);
			String version = licenseRawData.get(i + 3);
			String cipher = licenseRawData.get(i + 4);
			License license = decipher.decipher(type, id, product, module, version, cipher);
			if (license.getEndDate()>date)
			{
				license= new License(product, module, version, license.getAvailable(), license.getStartDate(), date, license.getEnablePrint(), license.getEnableSave());
			}
			if (license != null)
			{
				this.licenseTable.add(license);
			}
		}
		if (licenseTable.isEmpty())
		{
			DynaLogger.info("Loading Temp Guard license Fail.");
		}
		else
		{
			DynaLogger.info("Loading Temp Guard license Success");
		}
	}

}