/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: License
 * Wanglei 2011-4-20
 */
package dyna.app.core.lic;

import java.io.PrintStream;

/**
 * 授权证书
 * 
 * @author Wanglei
 * 
 */
public class License {
	private String ProductCode;
	private String ModuleCode;
	private String VersionCode;
	private int    Available;
	private int    InUse;
	private long   stime, etime;
	private boolean EnablePrint, EnableSave;

	public License( String ProductCode, String ModuleCode, String VersionCode,
			int Available, long StartTime, long EndTime, boolean EnablePrint, boolean EnableSave) {
		this.ProductCode = ProductCode;
		this.ModuleCode = ModuleCode;
		this.VersionCode = VersionCode;
		this.Available = Available;
		this.InUse = 0;
		this.stime = StartTime;
		this.etime = EndTime;
		this.EnablePrint = EnablePrint;
		this.EnableSave = EnableSave;
	}


	public synchronized boolean testFindSlot(String ProductCode, String ModuleCode, String VersionCode) {
		if (ProductCode.equals("2000")) {
			VersionCode = this.VersionCode;
		}

		if ( this.ProductCode.equals(ProductCode) && this.ModuleCode.equals(ModuleCode) && this.VersionCode.equals(VersionCode)) {
			return true;
		}

		return false;
	}

	public synchronized boolean isAvailable() {
		long curTime=System.currentTimeMillis();
		if (curTime<stime||curTime>etime)
		{
			return false;
		}
		if (this.InUse < this.Available) {
			return true;
		}

		return false;
	}

	public synchronized boolean increaseUse() {
		long curTime=System.currentTimeMillis();
		if (curTime<stime||curTime>etime)
		{
			return false;
		}
		if (this.InUse < this.Available) {
			this.InUse++;
			return true;
		}

		return false;
	}

	public synchronized void decreaseUse() {
		this.InUse--;

		if (this.InUse < 0) {
			this.InUse = 0;
		}
	}

	public synchronized void clearUse() {
		this.InUse = 0;
	}

	public synchronized long getStartDate() {
		return this.stime;
	}

	public synchronized long getEndDate() {
		return this.etime;
	}

	public synchronized boolean getEnableSave() {
		return this.EnableSave;
	}

	public synchronized boolean getEnablePrint() {
		return this.EnablePrint;
	}

	public synchronized void showStatus(PrintStream out) {
		out.println("Product Code : " + this.ProductCode);
		out.println(" Module Code : " + this.ModuleCode);
		out.println("Version Code : " + this.VersionCode);
		out.println("Enable Print : " + this.EnablePrint);
		out.println(" Enable Save : " + this.EnableSave);
		out.println("   Available : " + this.Available);
		out.println("      In Use : " + this.InUse);
	}

	public synchronized void getStatus(PrintStream out) {
		out.println(this.ProductCode);
		out.println(this.ModuleCode);
		out.println(this.VersionCode);
		out.println(this.EnablePrint);
		out.println(this.EnableSave);
		out.println(this.Available);
		out.println(this.InUse);
	}


	public String getModuleCode() {
		return this.ModuleCode;
	}


	public String getProductCode() {
		return this.ProductCode;
	}


	public String getVersionCode() {
		return this.VersionCode;
	}


	public synchronized int getAvailable() {
		return this.Available;
	}

	public synchronized int getInUse() {
		return this.InUse;
	}

	public synchronized int resetInUse(int inUse)
	{
		if (inUse > this.Available)
		{
			inUse = this.Available;
		}
		return this.InUse = inUse;
	}
	
	public boolean isEnable()
	{
		long curTime=System.currentTimeMillis();
		if (curTime<stime||curTime>etime)
		{
			return false;
		}
		
		return true;

	}

}
