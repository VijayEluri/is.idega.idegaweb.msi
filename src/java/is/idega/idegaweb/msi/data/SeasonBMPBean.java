/*
 * $Id: SeasonBMPBean.java,v 1.1 2007/06/11 12:14:19 palli Exp $
 * Created on May 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.msi.data;

import java.sql.Timestamp;

import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.util.IWTimestamp;

/**
 * Last modified: $Date: 2007/06/11 12:14:19 $ by $Author: palli $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class SeasonBMPBean extends GroupBMPBean implements Season, Group {

	public static final String METADATA_SEASON_BEGIN = "season_begin";

	public static final String METADATA_SEASON_END = "season_end";

	// getters
	public Timestamp getSeasonBeginDate() {
		String date = getMetaData(METADATA_SEASON_BEGIN);
		if (date != null) {
			IWTimestamp stamp = new IWTimestamp(date);
			return stamp.getTimestamp();
		}
		return null;
	}

	public Timestamp getSeasonEndDate() {
		String date = getMetaData(METADATA_SEASON_END);
		if (date != null) {
			IWTimestamp stamp = new IWTimestamp(date);
			return stamp.getTimestamp();
		}
		return null;
	}

	// setters
	public void setSeasonBeginDate(Timestamp date) {
		IWTimestamp stamp = new IWTimestamp(date);
		setMetaData(METADATA_SEASON_BEGIN, stamp.toSQLString(),
				"java.sql.Timestamp");
	}

	public void setSeasonEndDate(Timestamp date) {
		IWTimestamp stamp = new IWTimestamp(date);
		setMetaData(METADATA_SEASON_END, stamp.toSQLString(),
				"java.sql.Timestamp");
	}
}