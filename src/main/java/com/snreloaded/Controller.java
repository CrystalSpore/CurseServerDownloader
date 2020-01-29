package com.snreloaded;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    public static void main(String[] args) {
        String url = args[0];

        if (!url.startsWith("https://www.curseforge.com/minecraft/modpacks")) {
            System.err.println("Cannot install as the url was not a Curse modpack url");
            return;
        }

        Pattern pattern = Pattern.compile(
                "https:\\/\\/www\\.curseforge\\.com\\/minecraft\\/modpacks\\/([a-zA-Z0-9-]+)\\/?(?:download|files)?\\/?([0-9]+)?");
        Matcher matcher = pattern.matcher(url);

        if (!matcher.find() || matcher.groupCount() < 2) {
            System.err.println("Cannot install as the url was not a valid Curse modpack url");
            return;
        }

        String packSlug = matcher.group(1);

        String projectID = NikkyGraphQL.slugToProjectID(packSlug);
        System.out.println("Project ID: " + projectID);
    }
}
