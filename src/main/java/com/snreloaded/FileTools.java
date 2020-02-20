package com.snreloaded;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileTools {

    public static void packZip(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                    try {
                        zs.putNextEntry(zipEntry);
                        Files.copy(path, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                });
        }
    }

    public static void createServerStart(String path, String forgeJarName) throws IOException {
        File script = new File(path+"/ServerStart.sh");
        FileOutputStream writer = new FileOutputStream(script);

        writer.write(("java -Xms1024M -Xmx3072M -jar "+forgeJarName.substring(0,forgeJarName.length()-14)+".jar nogui").getBytes());

        writer.close();
    }

    public static void createServerLoop(String path) throws IOException {
        File script = new File(path+"/ServerLoop.sh");
        FileOutputStream writer = new FileOutputStream(script);

        writer.write("while true;\n".getBytes());
        writer.write("do\n".getBytes());
        writer.write("\t$(dirname \"$0\")/ServerStart.sh\n".getBytes());
        writer.write("echo \"Server has now shutdown.\"\n".getBytes());
        writer.write("echo \"Press C-c to safely shutdown in the next 7 seconds\"\n".getBytes());
        writer.write("echo \"\"\n".getBytes());
        writer.write("echo \"Restarting in...\"\n".getBytes());
        writer.write("for i in 7 6 5 4 3 2 1\n".getBytes());
        writer.write("do".getBytes());
        writer.write("\techo $i \" seconds...\"\n".getBytes());
        writer.write("sleep 1\n".getBytes());
        writer.write("done\n".getBytes());
        writer.write("echo \"Server Restarting!!!\"\n".getBytes());
        writer.write("done".getBytes());

        writer.close();
    }

    public static void runScript(String args, String path)
    {
        Runtime rt = Runtime.getRuntime();
        try {
            final Process p = rt.exec(args, null, new File(path));

            new Thread(new Runnable() {
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;

                    try {
                        while ((line = input.readLine()) != null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            p.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
