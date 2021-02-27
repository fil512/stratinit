package com.kenstevens.stratinit.svc;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.ServerManager;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StratInitAdmin {
    @Autowired
    private ServerManager serverManager;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private RequestFactory requestFactory;

    public synchronized Result<None> shutdown() {
        // TODO SEC block access to this method to admin only
        if (!serverStatus.isRunning()) {
            return new Result<None>("The server is not running.", false);
        }
        serverManager.shutdown();
        return new Result<None>("SERVER SHUTDOWN COMPLETE", true);
    }

    public Result<Integer> postAnnouncement(String subject, String body) {
        return requestFactory.getPostAnnouncementRequest(subject, body).processNoGame();
    }


}
