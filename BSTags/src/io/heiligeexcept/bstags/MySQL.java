package io.heiligeexcept.bstags;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MySQL {

    private static MySQL instance;
    public static MySQL get() {
        if (instance == null) instance = new MySQL();
        return instance;
    }
   
    private Connection con;
    public Connection con() throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection(Main.args.get("mysql"), Main.args.get("user"), Main.args.get("pass"));
        }
        return con;
    }
   
    public MySQL() {
        if (instance != null)
            throw new IllegalStateException();
        instance = this;
    }
   
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void handleSongs(String songkey, JSONObject jso) {
//        System.out.println("handleSongs:: songkey="+songkey);
        try {
            PreparedStatement prep = con().prepareStatement("REPLACE INTO songs (songkey, name, subName, description, easy, "
                    + "normal, hard, expert, expertplus, bpm, time, time_s, downloadCount, playedCount, upVotes, downVotes, createdAt, linkUrl, "
                    + "coverUrl) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            int i = 1;
            prep.setString(i++, songkey);
            prep.setString(i++, (String) jso.getOrDefault("name", ""));
            prep.setString(i++, (String) jso.getOrDefault("subName", ""));
            prep.setString(i++, (String) jso.getOrDefault("description", ""));
            JSONObject diffs = (JSONObject) jso.getOrDefault("difficulties", new JSONObject());
            prep.setBoolean(i++, diffs.containsKey("Easy"));
            prep.setBoolean(i++, diffs.containsKey("Normal"));
            prep.setBoolean(i++, diffs.containsKey("Hard"));
            prep.setBoolean(i++, diffs.containsKey("Expert"));
            prep.setBoolean(i++, diffs.containsKey("ExpertPlus"));
            prep.setInt(i++, (int) (long) jso.getOrDefault("bpm", 0));
            JSONObject firstDiff = (JSONObject) diffs.getOrDefault("Easy", diffs.getOrDefault("Normal", diffs.getOrDefault("Hard",
                    diffs.getOrDefault("Expert", diffs.getOrDefault("ExpertPlus", new JSONObject())))));
            JSONObject stats = (JSONObject) firstDiff.getOrDefault("stats", new JSONObject());
            prep.setDouble(i++, new Double(""+stats.getOrDefault("time", 0)));
            prep.setDouble(i++, new Double(""+stats.getOrDefault("time", 0)) * 60d / new Double(""+jso.getOrDefault("bpm", 0)));
            prep.setInt(i++, Integer.parseInt(jso.getOrDefault("downloadCount", 0).toString()));
            prep.setInt(i++, Integer.parseInt(jso.getOrDefault("playedCount", 0).toString()));
            prep.setInt(i++, Integer.parseInt(jso.getOrDefault("upVotes", 0).toString()));
            prep.setInt(i++, Integer.parseInt(jso.getOrDefault("downVotes", 0).toString()));
            prep.setString(i++, ((String) ((JSONObject) jso.getOrDefault("createdAt", new JSONObject())).getOrDefault("date", "")));
            prep.setString(i++, (String) jso.getOrDefault("linkUrl", 0));
            prep.setString(i++, (String) jso.getOrDefault("coverUrl", 0));
            prep.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public void handleTags(String songkey, JSONArray tags, JSONArray topicCategories) {
        System.out.println("handleTags:: songkey="+songkey);
        List<Integer> ids = new ArrayList<>();
       
        for (Object o : tags) {
            String s = (String) o;
            String src = Sources.YTTAG.toString();
            try {
                ids.add(insertIgnoreAI_selectId(s, src));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Object o : topicCategories) {
            String s = (String) o;
            String[] ss = s.split("/");
            s = ss[ss.length-1];
            String src = Sources.YTTOPIC.toString();
            try {
                ids.add(insertIgnoreAI_selectId(s, src));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
       
        if (!ids.isEmpty()) {
            String sql = "INSERT INTO songs_tags (song, tag) VALUES ";
            for (int i = 0; i < ids.size(); i++) {
                sql += "(?, ?), ";
            }
            sql = sql.substring(0, sql.length()-2);
            try {
                PreparedStatement stmt = con().prepareStatement(sql);
                int c = 1;
                for (int i = 0; i < ids.size(); i++) {
                    stmt.setString(c++, songkey);
                    stmt.setInt(c++, ids.get(i));
                }
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(sql);
                e.printStackTrace();
            }
        }
    }
   
    private int insertIgnoreAI_selectId(String s, String src) throws SQLException {
        PreparedStatement prep = con().prepareStatement("INSERT INTO tags (name, source) "
                + "SELECT * FROM (SELECT ?, ?) AS tmp "
                + "WHERE NOT EXISTS ( "
                + " SELECT name FROM tags WHERE name=? AND source=? "
                + ") LIMIT 1");
        prep.setString(1, s);
        prep.setString(2, src);
        prep.setString(3, s);
        prep.setString(4, src);
        prep.executeUpdate();
   
        PreparedStatement prep_ = con().prepareStatement("SELECT id FROM tags WHERE name=? AND source=?");
        prep_.setString(1, s);
        prep_.setString(2, src);
        ResultSet rs = prep_.executeQuery();
        rs.next();
        return rs.getInt("id");
    }
   
    // Insert ohne primary key:
    /*
     *
     * INSERT INTO tags (name, source)
     * SELECT * FROM (SELECT "test", "manual2") AS tmp
     * WHERE NOT EXISTS (
     *         SELECT name FROM tags WHERE name = 'test' AND source="manual2"
     * ) LIMIT 1
     *
     *
     */
   
}	