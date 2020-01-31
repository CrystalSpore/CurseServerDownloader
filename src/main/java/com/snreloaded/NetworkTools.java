package com.snreloaded;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Scanner;

public class NetworkTools {

    /**
     * Note: This project doesn't use GraphQL
     * However, https://curse.nikky.moe/graphql does use
     *      graphql in the background
     * @param slug - Slug component from CurseForge link
     * @return ProjectID - ProjectID needed for CurseForgeAPI
     */
    public static String slugToProjectID(String slug)
    {

        String body = "{\"query\":\"{addons(slug:\\\""+slug+"\\\"){id slug dateCreated latestFiles {gameVersion}}}\"}";
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://curse.nikky.moe/graphql");
        String response = target.request(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .post(Entity.json(body), String.class);
        int indexOfID = response.indexOf("id");
        if (indexOfID == -1)
        {
            return "";
        }
        else
        {
            return response.substring(indexOfID+6, response.indexOf(",", indexOfID));
        }
    }

    /**
     *
     * @param projectID - value identifying which curseforge project we are looking at
     * @param fileID - value identifying the specific file that you wish to download
     * @return
     */
    public static String getCurseDownloadURL(String projectID, String fileID)
    {
        Client client = ClientBuilder.newClient();
        String URL = "https://addons-ecs.forgesvc.net/api/v2/addon/"+projectID+"/file/"+fileID+"/download-url";
        //System.out.println(URL);
        WebTarget target = client.target(URL);
        String response = target.request()
                .get(String.class);
        return response;
    }

    /**
     *
     * @param projectID - modpack project ID
     * @return fileID - serverFileID for download url
     * @throws ParseException
     */
    public static String getFileList(String projectID) throws ParseException {
        Client client = ClientBuilder.newClient();
        String URL = "https://addons-ecs.forgesvc.net/api/v2/addon/"+projectID+"/files";
        //System.out.println(URL);
        WebTarget target = client.target(URL);
        String response = target.request()
                .get(String.class);
        //System.out.println(response);
        JSONArray jsonArray = (JSONArray) (new JSONParser().parse(response));



        System.out.println("Versions: ");
        System.out.println("NOTE! Versions are not in order. Please verify that you are choosing the correct version.");
        for ( int i = 1; i <= jsonArray.size(); i++ )
        {
            JSONObject curJSON = (JSONObject) jsonArray.get(i-1);
            String fileName = ((String) curJSON.get("fileName"));
            String version = fileName.substring(0,fileName.length()-4);
            System.out.println( "\t" + String.format("%2d", i) + ": " + version);
        }
        System.out.println("NOTE! Versions are not in order. Please verify that you are choosing the correct version.");
        System.out.println("What version do you wish to download?");
        Scanner kin = new Scanner(System.in);
        int option = Integer.parseInt(kin.nextLine());
        return ((Long)((JSONObject)jsonArray.get(option-1)).get("serverPackFileId")).toString();
    }

}


