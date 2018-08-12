package io.heiligeexcept.bstags;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BSVCrawler {

    private YoutubeSearch searcher;
   
    public BSVCrawler() {
        searcher = new YoutubeSearch();
       
        int begin = 0;
        while (begin != -1) {
            begin = lookupPage(begin);
        }
    }
   
    @SuppressWarnings("unchecked")
    private int lookupPage(int i) {
        Utils.freeze(200);
        System.out.println("lookupPage:: i="+i);
       
        String url = "https://beatsaver.com/api/songs/new/"+i;
        JSONObject https = Utils.https(url);
       
        JSONArray songs;
        if (https.getOrDefault("songs", new JSONObject()) instanceof JSONObject) {
            songs = new JSONArray();
            JSONObject jso = (JSONObject) https.getOrDefault("songs", new JSONObject());
            for (Object o : jso.keySet()) {
                JSONObject song = (JSONObject) jso.get(o);
                songs.add(song);
            }
        } else {
            songs = (JSONArray) https.getOrDefault("songs", new JSONArray());
        }
        for (Object o : songs) {
            JSONObject song = (JSONObject) o;
            String name1 = (String) song.getOrDefault("songName", null);
            String name2 = (String) song.getOrDefault("name", null);
            String name = searcher.qualifiesToSearch(name1) ? name1 : searcher.qualifiesToSearch(name2) ? name2 : null;
            if (name != null) {
                String key = (String) song.getOrDefault("key", "?");
                MySQL.get().handleSongs(key, song);
                searcher.queueSearch(key, name);
            }
        }
       
        Utils.dump(url.substring(8)+".json", https);
       
        if (songs.size() == 0 || i > Utils.jsonInt(https, "total", Integer.MAX_VALUE)) return -1;
        return i+songs.size();
    }
   
}