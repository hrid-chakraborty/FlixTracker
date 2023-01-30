package com.sample.flixtracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class GenreMapController {
    Map<String, Integer > genreRatings = new HashMap<String, Integer>();

    Map<String, String > genreStringtoIdMap = new HashMap<String, String>();

    public Map<String,String> getStringtoIDMap(){
        //Send map to link genre names and genre ids
        genreStringtoIdMap.put("Action","28");
        genreStringtoIdMap.put("Comedy","35");
        genreStringtoIdMap.put("Drama","18");
        genreStringtoIdMap.put("Crime","80");
        genreStringtoIdMap.put("Fantasy","14");
        genreStringtoIdMap.put("Horror","27");
        genreStringtoIdMap.put("Mystery","9648");
        genreStringtoIdMap.put("Romance","10749");
        genreStringtoIdMap.put("Thriller","53");
        return genreStringtoIdMap;
    }

    public Map<String, Integer> getMap(String username) throws Exception {
        //Method to fetch genre ratings for a user from DB and return it in the form of map
        genreRatings.put("Action",0);
        genreRatings.put("Comedy",0);
        genreRatings.put("Drama",0);
        genreRatings.put("Crime",0);
        genreRatings.put("Fantasy",0);
        genreRatings.put("Horror",0);
        genreRatings.put("Mystery",0);
        genreRatings.put("Romance",0);
        genreRatings.put("Thriller",0);

        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection connection = DriverManager.getConnection(url, "root", "");
        Statement stm = connection.createStatement();
        String sql = "select * from genre where Username='" + username + "'";
        ResultSet result = stm.executeQuery(sql);

        String[] genresArr = {"Action","Comedy","Drama","Fantasy","Horror","Mystery","Romance","Thriller"};
        while(result.next()){
            for(String s:genresArr){
                if(genreRatings.get(s)!=null)
                {
                    genreRatings.put(s,genreRatings.get(s)+result.getInt(s));
                    System.out.println(s);
                }
            }
        }
        return genreRatings;
    }
}
