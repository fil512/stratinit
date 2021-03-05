package com.kenstevens.stratinit.client.server.event.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class ServerLocker {
    private static final String LOCK_FILENAME = "StratInit.lock";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public FileLock getLock() {
        RandomAccessFile file = null;
        FileLock retval = null;
        try {
            file = new RandomAccessFile(LOCK_FILENAME, "rw");
            FileChannel fileChannel = file.getChannel();
            retval = fileChannel.tryLock();
        } catch (FileNotFoundException e) {
            logger.error("Unable to create lockfile " + LOCK_FILENAME, e);
        } catch (IOException e) {
            logger.error("Unable to lock lockfile " + LOCK_FILENAME, e);
        } catch (OverlappingFileLockException e) {
            logger.warn(LOCK_FILENAME + " is already locked.");
        }
        return retval;
    }

    public void releaseLock(FileLock fileLock) {
        try {
            fileLock.release();
        } catch (IOException e) {
            logger.error("Unable to release lock on lockfile " + LOCK_FILENAME, e);
        }
    }
}
