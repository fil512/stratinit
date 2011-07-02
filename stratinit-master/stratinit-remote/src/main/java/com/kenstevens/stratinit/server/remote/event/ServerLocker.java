package com.kenstevens.stratinit.server.remote.event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.apache.log4j.Logger;

public class ServerLocker {
	private static final String LOCK_FILENAME = "StratInit.lock";
	private Logger logger = Logger.getLogger(getClass());

	public FileLock getLock() {
		RandomAccessFile file = null;
		FileLock retval = null;
		try {
			file = new RandomAccessFile(LOCK_FILENAME, "rw");
			FileChannel fileChannel = file.getChannel();
			retval = fileChannel.tryLock();
		} catch (FileNotFoundException e) {
			logger.error("Unable to create lockfile "+LOCK_FILENAME, e);
		} catch (IOException e) {
			logger.error("Unable to lock lockfile "+LOCK_FILENAME, e);
		} catch (OverlappingFileLockException e) {
			logger.warn(LOCK_FILENAME+" is already locked.");
		}
		return retval;
	}
	
	public void releaseLock(FileLock fileLock) {
		try {
			fileLock.release();
		} catch (IOException e) {
			logger.error("Unable to release lock on lockfile "+LOCK_FILENAME, e);
		}
	}
}
