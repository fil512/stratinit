package com.kenstevens.stratinit.client.util;

import com.kenstevens.stratinit.client.main.ClientConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class JsonPersister {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String load(String filename) {
        String retval = "";
        try {
            createDataDirIfNoExists();
            File source = new File(filename);
            if (source.exists()) {
                retval = deserialize(source);
            } else {
                retval = "First time running.  Not loading save file.";
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            retval = "Save file is corrupt.  Not loading Save file.";
        }
        return retval;
    }

    protected abstract String deserialize(File source) throws IOException;

    protected abstract void serialize(File result) throws IOException;

    public void save(String filename) throws IOException {
        createDataDirIfNoExists();
        File result = new File(filename);
        serialize(result);
    }

    private void createDataDirIfNoExists() {
        createDirIfNoExists(System.getProperty("user.home") + "/" + ClientConstants.DATA_DIR);
    }

    private void createDirIfNoExists(String dirName) {
        File dataDir = new File(dirName);
        if (!dataDir.canRead()) {
            dataDir.mkdir();
        }
    }
}
