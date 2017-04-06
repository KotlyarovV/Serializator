package com.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by vitaly on 07.04.17.
 */
public class FileWorker {

    public static void write (byte[] bytes) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream("myfile.bin");
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {}
    }

    public static byte[] getBytesFromFile () throws IOException{
        Path path = Paths.get("myfile.bin");
        byte[] data = Files.readAllBytes(path);
        return data;
    }
}
