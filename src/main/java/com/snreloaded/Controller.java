package com.snreloaded;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    public static void main(String[] args) {

        if (args.length < 1)
        {
            System.err.println("Not enough arguments. Use this format:");
            System.err.println("\tCurseServerDownloader <CurseForgeLink>");
            System.err.println("NOTE: You MUST use the link from the download page, not just the project page.\n" +
                    "\tThis defines the file in specific that is requested");
            return;
        }

        String url = args[0];

        if (!url.startsWith("https://www.curseforge.com/minecraft/modpacks")) {
            System.err.println("Cannot install as the url was not a Curse modpack url");
            return;
        }

        Pattern pattern = Pattern.compile("https:\\/\\/www\\.curseforge\\.com\\/minecraft\\/modpacks\\/([a-zA-Z0-9-]+)\\/?(?:download|files)?\\/?([0-9]+)?");
        Matcher matcher = pattern.matcher(url);

        if (!matcher.find() || matcher.groupCount() < 2) {
            System.err.println("Cannot install as the url was not a valid Curse modpack url");
            return;
        }

        String packSlug = matcher.group(1);

        String projectID = NetworkTools.slugToProjectID(packSlug);
        System.out.println("Project ID: " + projectID);

        String downloadURL = NetworkTools.getCurseDownloadURL(projectID, matcher.group(2));
        downloadURL = downloadURL.replace(" ", "%20");
        System.out.println("URL: " + downloadURL);

        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(new URL(downloadURL).openStream());
            FileOutputStream fileOS = new FileOutputStream("./download.zip");
            System.out.println("Attempting to download to: " + new File(".").getCanonicalPath());
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
        }
    }
}
