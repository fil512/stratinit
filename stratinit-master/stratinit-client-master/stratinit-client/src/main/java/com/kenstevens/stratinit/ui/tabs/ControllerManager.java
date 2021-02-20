package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.shell.TopShell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ControllerManager {
	@Autowired
	Data db;
	@Autowired
	TopShell topShell;

	private final List<TopLevelController> controllers = new ArrayList<TopLevelController>();

	public void add(TopLevelController topLevelController) {
		controllers.add(topLevelController);
	}


	public void setControllers() {
		for (TopLevelController controller : controllers) {
			controller.setControllers();
		}
	}
	
	public void setTitle() {
		String title = ClientConstants.CLIENT_NAME + " "
				+ ClientConstants.CLIENT_VERSION;
		if (db.getSelectedGameId() != -1) {
			title += " Game " + db.getSelectedGameId();
		}
		topShell.setTitle(title);
	}


}
