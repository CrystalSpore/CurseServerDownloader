package com.snreloaded;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class NetworkTools {

    /**
     * Note: This project doesn't use GraphQL
     * However, https://curse.nikky.moe/graphql does use
     *      graphql in the background
     * @param slug - Slug component from CurseForge link
     * @return ProjectID - ProjectID needed for CurseForgeAPI
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
        System.out.println(URL);
        WebTarget target = client.target(URL);
        String response = target.request()
                .get(String.class);
        return response;
    }

}


