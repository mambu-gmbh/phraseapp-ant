package com.mambu.ant.helper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author aifrim.
 */
public final class FileWriter {

    private FileWriter() {
    }

    public static void writeProperty(String fileNme, String key, String value) {

        try {

            File file = new File(fileNme);

            if(!file.getParentFile().exists()) {

                file.getParentFile().mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(file)) {

                writer.write(key + "=" + value);

            }
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }
}
