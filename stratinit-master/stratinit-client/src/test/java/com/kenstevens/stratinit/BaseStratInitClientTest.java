package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.SpringConfig;
import com.kenstevens.stratinit.client.api.IAudioPlayer;
import com.kenstevens.stratinit.client.api.ICommandList;
import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.event.IEventExecutor;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public abstract class BaseStratInitClientTest {
	@MockBean
	IEventExecutor eventExecutor;
	@MockBean
	IProgressBar progressBar;
	@MockBean
	IStatusReporter statusReporter;
	@MockBean
	ICommandList commandList;
	@MockBean
	IAudioPlayer audioPlayer;

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
