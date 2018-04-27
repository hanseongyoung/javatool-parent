package com.syhan.javatool.share.util.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FolderUtil {
    //
    public static void mkdir(String physicalPath) {
        //
        Path path = Paths.get(physicalPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                // TODO : if fails
                e.printStackTrace();
            }
        }
    }
}
