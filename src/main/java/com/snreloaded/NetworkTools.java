package com.snreloaded;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
        client.close();
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
     * This method will get the server files download URL from the curseforge api
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
        client.close();
        return response;
    }

    /**
     * This method will get the serverFileID from the curseforge API in order to obtain the
     *      download URL
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
        client.close();
        return ((Long)((JSONObject)jsonArray.get(option-1)).get("serverPackFileId")).toString();
    }

    /**
     * Handles the actual download of the file
     * @param imgURL - URL of the server file
     * @param imgSavePath - path & name of file to save as
     * @return success status
     */
    public static boolean saveFile(String imgURL, String imgSavePath) {

        boolean isSucceed = true;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        URL url = null;
        
        try {
            url = new URL(imgURL);
        }
        catch ( MalformedURLException e )
        {
            isSucceed = false;
        }
        
        if (isSucceed) {
            HttpGet httpGet = new HttpGet(url.toString());
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.11 Safari/537.36");

            try {
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity imageEntity = httpResponse.getEntity();

                if (imageEntity != null) {
                    File outputFile = new File(imgSavePath);
                    BufferedInputStream inputStream = new BufferedInputStream(imageEntity.getContent());
                    URLConnection tempConnect = url.openConnection();
                    int size = tempConnect.getContentLength();
                    FileOutputStream fout = new FileOutputStream(outputFile);
                    byte buffer[] = new byte[524288];

                    // Read from server into buffer.
                    System.out.println("Download:\n\t"+String.format("%10.2f",0.0)+"%");
                    int lastSize = 2;
                    int byteContent;
                    double percent = 0;
                    int ctr = 0;
                    while ((byteContent = inputStream.read(buffer, 0, 524288)) != -1) {
                        fout.write(buffer, 0, byteContent);
                        percent += (((double)byteContent)/size);
                        if ( ctr == 2048 )
                        {
                            String is = String.format("%10.2f", (percent*100));
                            System.out.println("\t"+is+"%");
                            ctr = 0;
                        }
                        ctr++;
                    }
                    System.out.println("\t"+String.format("%10.2f",100.00)+"%");
                    System.out.println("Download is complete!");
                }

            } catch (IOException e) {
                isSucceed = false;
            }
            httpGet.releaseConnection();
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isSucceed;
    }

}


