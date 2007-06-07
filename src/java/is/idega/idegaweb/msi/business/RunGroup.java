/*
 * Created on 21.8.2004
 */
package is.idega.idegaweb.msi.business;

import com.idega.util.Counter;


/**
 * @author laddi
 */
public class RunGroup {

	private String groupName;
	private Counter counter;
	
	public RunGroup(String name) {
		this.groupName = name;
	}
	
	/**
	 * @return Returns the counter.
	 */
	public Counter getCounter() {
		return this.counter;
	}
	
	/**
	 * @param counter The counter to set.
	 */
	public void setCounter(Counter counter) {
		this.counter = counter;
	}
	
	/**
	 * @return Returns the groupName.
	 */
	public String getGroupName() {
		return this.groupName;
	}
}