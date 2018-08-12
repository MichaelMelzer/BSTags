package io.heiligeexcept.bstags;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Utils {

    public static JSONObject https(String url) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            return (JSONObject) new JSONParser().parse(new InputStreamReader(con.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
   
    public static void freeze(long l) {
        try {
            Thread.sleep(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public static void dump(String file, Object data) {
        file = file.replaceAll("[^a-zA-Z0-9\\.\\-\\/\\\\]", "_").replace("/", File.separator);
        if (file.contains("https://www.googleapis.com/youtube/v3")) {
            file = file.split("v3")[1];
            file = file.substring(0, Math.min(150, file.length()));
        }
        File f = new File("dump", file);
        f.getParentFile().mkdirs();
       
        try (PrintWriter pw = new PrintWriter(f, Charset.defaultCharset().name())) {
            pw.println(data);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    @SuppressWarnings("unchecked")
    public static int jsonInt(JSONObject o, String key, int def) {
        try {
            return ((int) (long) o.getOrDefault(key, def));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }
   
}