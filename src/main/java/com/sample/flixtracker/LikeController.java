package com.sample.flixtracker;

import javafx.fxml.Initializable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

//Class to handle likes

public class LikeController implements Initializable {

    Map<String, Integer > genreRatings = new HashMap<String, Integer>();

    Map<String, String > genreIdMap = new HashMap<String, String>();

    String username = GlobalData.getUserId();

    public void addLikedMovietoDB(Movie movieobj) throws Exception {
        //Method to add movie to the DB
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection myConn = DriverManager.getConnection(url, "root", ""); //Connect to database (Requires JDBC) [Default username:root, pw empty]
        Statement statement= myConn.createStatement();
        JSONObject jsonObject = movieobj.getJsonObject();
        int movieid = jsonObject.getInt("id");
        String query="INSERT INTO `movie`(`MovieID`, `Username`) VALUES (?,?)";
        PreparedStatement preStat = myConn.prepareStatement(query);
        System.out.println("Query written");
        preStat.setInt(1,movieid);
        preStat.setString(2,username);
        preStat.executeUpdate();
        LikeController likeController = new LikeController();
        likeController.updateRatings(movieobj);
    }

    //WORKING ON BELOW FUNCTION
    public void removeLikedMovieFromDB(Movie movieobj) throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection myConn = DriverManager.getConnection(url, "root", ""); //Connect to database (Requires JDBC) [Default username:root, pw empty]
        Statement statement= myConn.createStatement();
        JSONObject jsonObject = movieobj.getJsonObject();
        int movieid = jsonObject.getInt("id");
        String query="DELETE from `movie` WHERE MovieID=? AND Username=?;";
        PreparedStatement preStat = myConn.prepareStatement(query);
        System.out.println("Data Removed");
        preStat.setInt(1,movieid);
        preStat.setString(2,username);
        preStat.executeUpdate();
        LikeController likeController = new LikeController();
        likeController.updateRatings(movieobj);
    }

    public void updateRatings(Movie movie) throws Exception {
        //Method to update Ratings for each liked movie
        genreRatings.put("Action",0);
        genreRatings.put("Comedy",0);
        genreRatings.put("Drama",0);
        genreRatings.put("Crime",0);
        genreRatings.put("Fantasy",0);
        genreRatings.put("Horror",0);
        genreRatings.put("Mystery",0);
        genreRatings.put("Romance",0);
        genreRatings.put("Thriller",0);

        genreIdMap.put("28","Action");
        genreIdMap.put("35","Comedy");
        genreIdMap.put("18","Drama");
        genreIdMap.put("80","Crime");
        genreIdMap.put("14","Fantasy");
        genreIdMap.put("27","Horror");
        genreIdMap.put("9648","Mystery");
        genreIdMap.put("10749","Romance");
        genreIdMap.put("53","Thriller");

        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection connection = DriverManager.getConnection(url, "root", "");
        Statement stm = connection.createStatement();
        String sql = "select * from genre where Username='" + username + "'";
        ResultSet result = stm.executeQuery(sql);
        System.out.println("Query to select from genre executed");

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



        JSONObject jsonObject = movie.getJsonObject();
        System.out.println(jsonObject);
        String arr = jsonObject.getJSONArray("genre_ids").toString();

        String[] genreString = arr.split(",");
        movie.setGenre("Other");
        for(String s : genreString){
            if(genreIdMap.get(s) != null){
                String genreStr = genreIdMap.get(s);
                System.out.println("Genre: " + s + " " + genreRatings.get(genreStr));
                genreRatings.put(genreStr, genreRatings.get(genreStr) + 5);
                System.out.println("genreRatings updated");
            }
        }
        System.out.println(genreRatings.get("Action"));

        String query="UPDATE genre SET Action=?, Comedy=?, Drama=?, Fantasy=?, Horror=?, Mystery=?, Romance=?, Thriller=? WHERE Username=?";
        System.out.println("Update query written");
        PreparedStatement preStat = connection.prepareStatement(query);
        preStat.setInt(1,genreRatings.get("Action"));
        preStat.setInt(2,genreRatings.get("Comedy"));
        preStat.setInt(3,genreRatings.get("Drama"));
        preStat.setInt(4,genreRatings.get("Fantasy"));
        preStat.setInt(5,genreRatings.get("Horror"));
        preStat.setInt(6,genreRatings.get("Mystery"));
        preStat.setInt(7,genreRatings.get("Romance"));
        preStat.setInt(8,genreRatings.get("Thriller"));
        preStat.setString(9,username);
        System.out.println("DB Change done!");
        preStat.executeUpdate();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        //hashmap initialization
        genreRatings.put("Action",0);
        genreRatings.put("Comedy",0);
        genreRatings.put("Drama",0);
        genreRatings.put("Crime",0);
        genreRatings.put("Fantasy",0);
        genreRatings.put("Horror",0);
        genreRatings.put("Mystery",0);
        genreRatings.put("Romance",0);
        genreRatings.put("Thriller",0);


        genreIdMap.put("28","Action");
        genreIdMap.put("35","Comedy");
        genreIdMap.put("18","Drama");
        genreIdMap.put("80","Crime");
        genreIdMap.put("14","Fantasy");
        genreIdMap.put("27","Horror");
        genreIdMap.put("9648","Mystery");
        genreIdMap.put("10749","Romance");
        genreIdMap.put("53","Thriller");

    }
}


