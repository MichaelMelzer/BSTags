package io.heiligeexcept.bstags;


import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class YoutubeSearch extends Thread {

    private final String API_KEY = Main.args.get("ytkey");
    @SuppressWarnings("unchecked")
    private HashMap<String, String> queue = new JSONObject();
    private JSONArray checked = new JSONArray();
   
    @SuppressWarnings("unchecked")
    public YoutubeSearch() {
        try {
            File qu = new File("queue.json");
            if (qu.exists()) {
                queue = (HashMap<String, String>) new JSONParser().parse(new FileReader(qu));
            }
           
            File ch = new File("checked.json");
            if (ch.exists()) {
                checked = (JSONArray) new JSONParser().parse(new FileReader(ch));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        start();
    }
   
    @Override
    public void run() {
        while (true) {
            Utils.freeze(100);
            if (!queue.isEmpty()) {
                String songkey = queue.keySet().stream().findFirst().get();
                String q = (String) queue.get(songkey);
                queue.remove(songkey);
                saveFiles();
                search(songkey, q);
            } else {
                System.out.println("queue is empty, waiting...");
                Utils.freeze(5000);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void search(String songkey, String q) {
        if (checked.contains(songkey)) return;
       
        System.out.println("search "+songkey);
        try {
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=relevance"
                    + "&q="+ URLEncoder.encode(q, "UTF-8") +"&key="+API_KEY;
            JSONObject https = Utils.https(url);
            JSONArray items = (JSONArray) https.getOrDefault("items", new JSONArray());
            if (!items.isEmpty()) {
                JSONObject item = (JSONObject) items.get(0);
                JSONObject id = (JSONObject) item.getOrDefault("id", new JSONObject());
                JSONObject list = video_list((String) id.getOrDefault("videoId", "?"));
               
                JSONArray items_ = (JSONArray) list.getOrDefault("items", new JSONArray());
                if (!items_.isEmpty()) {
                    JSONObject item_ = (JSONObject) items_.get(0);
                    JSONObject snippet_ = (JSONObject) item_.getOrDefault("snippet", new JSONObject());
                    JSONArray tags = (JSONArray) snippet_.getOrDefault("tags", new JSONArray());
                    JSONObject topicDetails = (JSONObject) item_.getOrDefault("topicDetails", new JSONObject());
                    JSONArray topicCategories = (JSONArray) topicDetails.getOrDefault("topicCategories", new JSONArray());
                    MySQL.get().handleTags(songkey, tags, topicCategories);
                    checked.add(songkey);
                    saveFiles();
                }
            }
           
            Utils.dump(url.substring(8)+".json", https);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
   
    private JSONObject video_list(String id) {
        String url = "https://www.googleapis.com/youtube/v3/videos?part=topicDetails%2Csnippet&id="+ id +"&key="+API_KEY;
        JSONObject json = Utils.https(url);
        Utils.dump(url, json);
        return json;
    }
   
    protected void queueSearch(String songkey, String q) {
        queue.put(songkey, q);
        saveFiles();
    }
   
    protected boolean qualifiesToSearch(String name) {
        if (name == null) return false;
        name = name.trim();
//        name = name.replaceAll("[^A-Za-z ]", "");
        return name.length() >= 10;
    }
   
    protected void saveFiles() {
        try {
            PrintWriter qu = new PrintWriter(new File("queue.json"));
            qu.println(new JSONObject(queue).toJSONString());
            qu.flush();
            qu.close();
           
            PrintWriter ch = new PrintWriter(new File("checked.json"));
            ch.println(checked.toJSONString());
            ch.flush();
            ch.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
}

