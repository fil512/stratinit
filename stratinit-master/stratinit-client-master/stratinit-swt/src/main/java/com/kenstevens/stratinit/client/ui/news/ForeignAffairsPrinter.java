package com.kenstevens.stratinit.client.ui.news;

import com.kenstevens.stratinit.dto.news.SINewsForeignAffairs;
import com.kenstevens.stratinit.type.RelationType;
import org.eclipse.swt.custom.StyledText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForeignAffairsPrinter extends
		NewsListPrinter<SINewsForeignAffairs> {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEE, MMM d 'at' HH:mm z");

	public ForeignAffairsPrinter(StyledText styledText) {
		super(styledText);
	}

	@Override
	public void print(List<SINewsForeignAffairs> items) {
		if (items.isEmpty()) {
			return;
		}
		printTitle("Foreign Affairs");

		for (SINewsForeignAffairs sifor : items) {
			appendAll(relationChangeDescription(sifor));
		}
	}

	private List<String> relationChangeDescription(
				SINewsForeignAffairs sifor) {
		List<String> actions = new ArrayList<String>();
		RelationType nextType = sifor.newRelation;
		RelationType type = sifor.oldRelation;
		if (nextType.compareTo(type) > 0) {
			improveRelations(actions, nextType, type);
		} else if (nextType.compareTo(type) < 0) {
			degradeRelations(actions, nextType, type);
		}

		String from = sifor.nationName;
		String to = sifor.opponentName;
		Date effective = sifor.effective;
		List<String> retval = new ArrayList<String>();
		for (String action : actions) {
			String line = from + " " + action + " " + to;
			if (effective != null) {
				line += ", effective " + FORMAT.format(effective);
			}
			retval.add(line);
		}
		return retval;
	}

	private void degradeRelations(List<String> actions, RelationType nextType,
			RelationType type) {
		if (type == RelationType.ALLIED) {
			actions.add("breaks its alliance with");
		}
		if (nextType == RelationType.FRIENDLY) {
			actions.add("downgrades relations to friendly with");
		} else if (nextType == RelationType.NEUTRAL) {
			actions.add("closes its borders to");
		} else if (nextType == RelationType.WAR) {
			actions.add("declares war on");
		}
	}

	private void improveRelations(List<String> actions, RelationType nextType,
			RelationType type) {
		if (type == RelationType.WAR) {
			actions.add("is no longer at war with");
		}
		// Improving relations
		if (nextType == RelationType.FRIENDLY) {
			actions.add("opens its borders to");
		} else if (nextType == RelationType.NEUTRAL) {
			actions.add("normalizes relations with");
		}
	}
}
