package dyna.common.conf;

import java.io.Serializable;

public class SchedulerDefinition extends InitParameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5513672192222809511L;

	private String		id				= null;

	private int		threadCount				= 1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		if (threadCount>0)
		{
			this.threadCount = threadCount;
		}
	}

}
