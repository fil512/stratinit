package com.kenstevens.stratinit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.shell.ProgressBarControl;
import com.kenstevens.stratinit.shell.WidgetContainer;


@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
public class StratInitClientTest {
	@Autowired protected Mockery context;
	@Autowired protected WidgetContainer widgetContainer;

	private ProgressBarControl progressBarControlMock = new ProgressBarControl() {
		@Override
		public void incrementSelection() {
		}

		@Override
		public void reset() {
		}

		@Override
		public void setMaximum(int value) {
		}
	};

	@Before
	public void loadWidgets() {
		widgetContainer.setProgressBarControl(progressBarControlMock);
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
}
