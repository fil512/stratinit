package com.kenstevens.stratinit;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.shell.ProgressBarControl;
import com.kenstevens.stratinit.shell.WidgetContainer;
import org.jmock.Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public abstract class BaseStratInitClientTest {
    @Autowired
    protected Mockery context;
    @Autowired
    protected WidgetContainer widgetContainer;

    private final ProgressBarControl progressBarControlMock = new ProgressBarControl() {
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

	@BeforeEach
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
