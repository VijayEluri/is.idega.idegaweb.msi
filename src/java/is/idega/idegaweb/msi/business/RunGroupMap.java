/*
 * Created on 21.8.2004
 */
package is.idega.idegaweb.msi.business;

import java.util.Collection;
import java.util.TreeSet;

import com.idega.util.datastructures.MultivaluedHashMap;


/**
 * @author laddi
 */
public class RunGroupMap extends MultivaluedHashMap {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4762273404504621982L;

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		Collection values = getCollection(key);
		if (values == null) {
			values = new TreeSet(new RunResultsComparator());
		}
		values.add(value);
		
		return super.superPut(key, values);
	}
}