package com.snreloaded;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    public static void main(String[] args) throws IOException {

        if (args.length < 1)
        {
            System.err.println("Not enough arguments. Use this format:");
            System.err.println("\tCurseServerDownloader <CurseForgeLink>");
            return;
        }

        ServerModBlacklist.initBlacklist();

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

        if ( projectID == null || projectID == "" )
        {
            System.err.println("Project ID could not be found. Verify that the CurseForge URL was entered correctly.");
            return;
        }

        String fileID = matcher.group(2);

        if (fileID == null || fileID == "")
        {
            try
            {
                fileID = NetworkTools.getServerFileList(projectID);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            if (fileID == null || fileID == "")
            {
                System.err.println("Was unable to find the fileID. The CurseForge API has no Server File association.");
                return;
            }
        }

        if ( fileID.charAt(0) == '!' )
        {
            String clientURL = fileID.substring(1);
            //System.out.println(clientURL);
            NetworkTools.buildServerFiles(clientURL);
        }
        else {

            String downloadURL = NetworkTools.getCurseDownloadURL(projectID, fileID);

            String[] splitURL = downloadURL.split("/");

            String zipName = "./" + splitURL[splitURL.length - 1];

            downloadURL = downloadURL.replace(" ", "%20");

            NetworkTools.saveFile(downloadURL, zipName);
        }
    }
}
