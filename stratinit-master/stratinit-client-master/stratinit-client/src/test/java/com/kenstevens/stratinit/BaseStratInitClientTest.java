package com.kenstevens.stratinit;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.StratInitServer;
import com.kenstevens.stratinit.shell.ProgressBarControl;
import com.kenstevens.stratinit.shell.WidgetContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public abstract class BaseStratInitClientTest {
	@MockBean
	protected StratInitServer stratInitServer;
	@MockBean
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
	@Autowired
	protected WidgetContainer widgetContainer;

	@BeforeEach
	public void loadWidgets() {
		widgetContainer.setProgressBarControl(mock(ProgressBarControl.class));
	}

	protected List<SIGame> makeSIGames() {
		SIGame sigame = new SIGame();
		Date now = new Date();
		sigame.id = 1;
		sigame.created = now;
		sigame.name = "test";
		List<SIGame> sigames = new ArrayList<SIGame>();
		sigames.add(sigame);
		return sigames;
	}

	protected void assertResult(Result<?> result) {
		assertTrue(result.isSuccess(), result.toString());
	}
}
