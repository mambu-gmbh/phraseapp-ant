package com.mambu.ant.helper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Helper class used for writing to files in a convenient manner
 *
 * @author aifrim.
 */
public final class FileWriter {

    private FileWriter() {
    }

    /**
     * Write property(key=value pair) to a given file
     */
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
