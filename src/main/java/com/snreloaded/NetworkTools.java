package com.snreloaded;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class NetworkTools {

    /**
     * Note: This project doesn't use GraphQL
     * However, https://curse.nikky.moe/graphql does use
     *      graphql in the background
     * @param slug - Slug component from CurseForge link
     * @return ProjectID - ProjectID needed for CurseForgeAPI
     */
    public static String slugToProjectID(String slug) throws IOException {
        File csd_cache = new File(System.getProperty("user.home")+"/"+".csd_cache");

        if ( csd_cache.exists() )
        {
            HashMap<String, String> cacheMap = new HashMap<>();
            Scanner fin = new Scanner(new FileInputStream(csd_cache));
            while ( fin.hasNext() )
            {
                String line = fin.nextLine();
                String[] splitLine = line.split(":");
                cacheMap.put(splitLine[0],splitLine[1]);
            }

            if ( cacheMap.containsKey(slug) )
            {
                return cacheMap.get(slug);
            }
        }
        else
        {
            csd_cache.createNewFile();
        }

        String body = "{\"query\":\"{addons(slug:\\\""+slug+"\\\"){id slug dateCreated latestFiles {gameVersion}}}\"}";
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://curse.nikky.moe/graphql");
        String response = target.request(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .post(Entity.json(body), String.class);
        //System.out.println(response);
        int indexOfID = response.indexOf("id");
        client.close();
        //System.out.println(indexOfID);
        if ( indexOfID != -1) {
            String projectID = response.substring(indexOfID + 6, response.indexOf(",", indexOfID));
            try {
                Files.write(Paths.get(csd_cache.getAbsolutePath()), (slug + ":" + projectID + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (indexOfID == -1) {
                return "";
            } else {
                return projectID;
            }
        }
        else
        {
            boolean exitLoop = false;
            for ( int i = 16; i >= 0; i-- )
            {
                System.out.println("Searching Version 1."+i);
                for ( int j = 0; j <= 10; j++ )
                {
                    String version = "1."+i+"."+j;
                    client = ClientBuilder.newClient();
                    target = client.target("https://addons-ecs.forgesvc.net/api/v2/addon/search?categoryId=0&gameId=432&gameVersion="+version+"&index=0&pageSize=255&searchFilter="+slug+"&sectionId=4471&sort=0");
                    response = target.request(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .get(String.class);
                    client.close();
                    if ( response.equals("[]") )
                    {
                        continue;
                    }
                    //System.out.println(response);
                    indexOfID = response.indexOf("id");
                    //System.out.println(indexOfID);
                    if ( indexOfID != -1) {
                        String projectID = response.substring(indexOfID + 4, response.indexOf(",", indexOfID));
                        try {
                            Files.write(Paths.get(csd_cache.getAbsolutePath()), (slug + ":" + projectID + "\n").getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            //exception handling left as an exercise for the reader
                        }
                        if (indexOfID == -1) {
                            return "";
                        } else {
                            return projectID;
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * This method will get the server files download URL from the CurseForge api
     * @param projectID - value identifying which CurseForge project we are looking at
     * @param fileID - value identifying the specific file that you wish to download
     * @return DownloadURL as string
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

    static class FileNameJSONComp implements Comparator<JSONObject> {

        @Override
        public int compare(JSONObject o1, JSONObject o2) {
            String left = (String) o1.get("fileName");
            String right = (String) o2.get("fileName");
            return left.compareTo(right);
        }

    }

    /**
     * This method will get the serverFileID from the curseforge API in order to obtain the
     *      download URL
     * @param projectID - modpack project ID
     * @return fileID - serverFileID for download url
     * @throws ParseException
     */
    public static String getServerFileList(String projectID) throws ParseException {
        Client client = ClientBuilder.newClient();
        String URL = "https://addons-ecs.forgesvc.net/api/v2/addon/" + projectID + "/files";
        //System.out.println(URL);
        WebTarget target = client.target(URL);
        String response = target.request()
                .get(String.class);
        client.close();

        //System.out.println(response);
        JSONArray jsonArray = (JSONArray) (new JSONParser().parse(response));

        ArrayList<JSONObject> sortedJSONarray = new ArrayList<>();
        for ( int i = 0; i < jsonArray.size(); i++ )
        {
            JSONObject curJSON = (JSONObject) jsonArray.get(i);
            sortedJSONarray.add(curJSON);
            System.out.println(curJSON);
            System.out.println(curJSON.get("fileName"));
        }

        sortedJSONarray.sort(new FileNameJSONComp());

        System.out.println(sortedJSONarray);

        System.out.println("Versions: ");
        System.out.println("NOTE! Versions may not be in order. Please verify that you are choosing the correct version.");

        for ( int i = 1; i <= sortedJSONarray.size(); i++ )
        {
            JSONObject curJSON = sortedJSONarray.get(i-1);
            //System.out.println(curJSON.toJSONString());
            String fileName = ((String) curJSON.get("fileName"));
            String version = fileName.substring(0,fileName.length()-4);
            System.out.println( "\t" + String.format("%2d", i) + ": " + version);
        }

        System.out.println("NOTE! Versions may not be in order. Please verify that you are choosing the correct version.");
        System.out.println("What version do you wish to download?");
        Scanner kin = new Scanner(System.in);
        int option = Integer.parseInt(kin.nextLine());

        //System.out.println(( ((JSONObject)jsonArray.get(option-1)).get("serverPackFileId") ));
        if ( ( (sortedJSONarray.get(option-1)).get("serverPackFileId") ) == null)
        {
            return "!" + ( (sortedJSONarray.get(option-1)).get("downloadUrl") );
        }
        return ((Long)(sortedJSONarray.get(option-1)).get("serverPackFileId")).toString();
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
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");

            try {
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity imageEntity = httpResponse.getEntity();

                if (imageEntity != null) {
                    File outputFile = new File(imgSavePath);
                    BufferedInputStream inputStream = new BufferedInputStream(imageEntity.getContent());
                    URLConnection tempConnect = url.openConnection();
                    int size = tempConnect.getContentLength();
                    try (ProgressBar pb = new ProgressBarBuilder()
                                        .setTaskName("Download")
                                        .setInitialMax(size)
                                        .setUpdateIntervalMillis(500)
                                        .setUnit("MiB", 1048576)
                                        .showSpeed()
                                        .build()) {
                        FileOutputStream fout = new FileOutputStream(outputFile);
                        byte buffer[] = new byte[1024];

                        int byteContent;
                        while ((byteContent = inputStream.read(buffer, 0, 1024)) != -1) {
                            fout.write(buffer, 0, byteContent);
                            pb.stepBy(1024);
                        }
                        pb.stepTo(size);
                    }
                }

                httpGet.releaseConnection();
                httpClient.close();
            } catch (IOException e) {
                isSucceed = false;
            }
        }

        return isSucceed;
    }

    public static void buildServerFiles(String clientURL) {
        File tmpDirs = new File("./csd_tmp/mods/");
        if (!tmpDirs.exists())
        {
            if (!tmpDirs.mkdirs())
            {
                return;
            }
        }

        if(!saveFile(clientURL, "./csd_tmp/pack_tmp.zip"))
        {
            return;
        }

        String source = "./csd_tmp/pack_tmp.zip";
        String destination = "./csd_tmp/pack_tmp/";

        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
            return;
        }

        new File("./csd_tmp/pack_tmp.zip").delete();

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject)(new JSONParser().parse(new FileReader("./csd_tmp/pack_tmp/manifest.json")));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        JSONObject minecraftObject = (JSONObject)jsonObject.get("minecraft");
        JSONArray  modloadersArray = (JSONArray)minecraftObject.get("modLoaders");
        JSONObject forgeObject     = ((JSONObject)(modloadersArray.get(0)));

        String minecraftVersion = minecraftObject.get("version").toString();
        String forgeVersion = forgeObject.get("id").toString().substring(6);

        String forgeJarName = "forge-" + minecraftVersion + "-" + forgeVersion + "-installer.jar";

        String minecraftForgeURL = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/" +
                                    minecraftVersion + "-" + forgeVersion + "/"+forgeJarName;

        if (!saveFile(minecraftForgeURL, "./csd_tmp/"+forgeJarName))
        {
            return;
        }

        System.out.println("Installing Forge Server. Hold tight!");
        FileTools.runScript("java -jar " + forgeJarName + " --installServer", "./csd_tmp/");
        System.out.println("Finished installing forge server.");

        System.out.println("Now downloading mods. Please be patient (silencing output to prevent terminal spam)");

        JSONArray filesArray = (JSONArray)jsonObject.get("files");
        for (int i = 0; i < filesArray.size(); i++) {
            JSONObject curObject = (JSONObject)(filesArray.get(i));
            String projectID = curObject.get("projectID").toString();
            String fileID = curObject.get("fileID").toString();

            if ( ServerModBlacklist.contains(projectID) ) //don't include problematic or client side only mods
            {
                continue;
            }

            String downloadURL = NetworkTools.getCurseDownloadURL(projectID, fileID);
            String[] splitURL = downloadURL.split("/");
            String jarName = "./csd_tmp/mods/" + splitURL[splitURL.length - 1];
            downloadURL = downloadURL.replace(" ", "%20");
            saveFile(downloadURL, jarName);
        }

        System.out.println("Finished downloading mods!");

        File from = new File("./csd_tmp/pack_tmp/overrides/");
        File to = new File("./csd_tmp/");
        try
        {
            FileUtils.copyDirectory(from, to);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try {
            FileUtils.deleteDirectory(new File("./csd_tmp/pack_tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileTools.createServerStart("./csd_tmp/", forgeJarName);
            FileTools.createServerLoop("./csd_tmp/");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String[] splitURL = clientURL.split("/");

            String zipName = "./" + splitURL[splitURL.length - 1];

            if ( new File(zipName).exists() )
            {
                new File(zipName).delete();
            }

            String folderToAdd = "./csd_tmp/";

            FileTools.packZip(folderToAdd, zipName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.deleteDirectory(new File ("./csd_tmp/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


