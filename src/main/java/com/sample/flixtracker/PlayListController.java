package com.sample.flixtracker;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class PlayListController implements Initializable {
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private FlowPane mainFlowPane;
    String username = GlobalData.getUserId();
    APIServices serviceObject = new APIServices();

    public void addMovietoPlaylist(Movie movieobj) throws Exception {
        //Add movie to playlist table in DB
        System.out.println("addMovietoPlaylist Entered");
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection myConn = DriverManager.getConnection(url, "root", ""); //Connect to database (Requires JDBC) [Default username:root, pw empty]
        Statement statement= myConn.createStatement();
        JSONObject jsonObject = movieobj.getJsonObject();
        int movieid = jsonObject.getInt("id");
        String query="INSERT INTO `playlist`(`MovieID`, `Username`) VALUES (?,?)";
        PreparedStatement preStat = myConn.prepareStatement(query);
        System.out.println("Query written");
        preStat.setInt(1,movieid);
        preStat.setString(2,username);
        preStat.executeUpdate();
        System.out.println("movie added to playlist db");
        System.out.println(movieid);
    }

    public ResultSet findPlaylistMoviesInDB() throws Exception{
        System.out.println("here you'll find all your saved playlist");
        Class.forName("com.mysql.cj.jdbc.Driver");

        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection connection = DriverManager.getConnection(url, "root", "");

        Statement stm = connection.createStatement();

        String sql = "select * from playlist where Username='" + username + "'";
        return stm.executeQuery(sql);
    }
    public List<Movie> searchPlaylist(ResultSet res) throws Exception{
        //Method to take the IDs of all the playlist movies and run API calls to get the movies & return the movies
        HttpURLConnection connection = null;
        final String mykey = serviceObject.getAPI_KEY();
        boolean adult = true;
        List<Movie> playlistArr = new ArrayList<>();

        while (res.next()) {
            String movieid = res.getString("MovieID");
            String usern = res.getString("Username");
            System.out.println("   "+movieid+" "+usern);
            URL url = new URL("https://api.themoviedb.org/3/movie/" + movieid + "?api_key=" + mykey + "&language=en-US");
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\r");
            }
            reader.close();
            String result = response.toString();
            JSONObject jsonObject = new JSONObject(result);
            System.out.println(jsonObject);
            Movie movie;
            movie = new Movie();
            movie.setJsonObject(jsonObject);
            movie.setName( jsonObject.getString("original_title"));
            movie.setImageSource( "https://image.tmdb.org/t/p/w500"+jsonObject.getString("poster_path"));
            String year = jsonObject.getString("release_date");
            movie.setYear(year.split("-")[0]);
            playlistArr.add(movie);
        }
        return playlistArr;
    }

    public void updatePlaylistMovies(List<Movie> playlistArr){
        //Method to display all the playlist movies
        try{
            for (Movie movie : playlistArr) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("fxmlfiles/movie.fxml"));

                VBox anchorPane = fxmlLoader.load();
                MovieController movieController = fxmlLoader.getController();
                movieController.setData(movie);
                Platform.runLater(()->{
                    mainFlowPane.getChildren().add(anchorPane);
                });

                FlowPane.setMargin(anchorPane, new Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            welcomeUserLabel.setText("Welcome to your Playlist");
            ResultSet res = findPlaylistMoviesInDB();
            List<Movie> playlistArr = searchPlaylist(res);

            mainFlowPane.getChildren().clear();
            new Thread(new Runnable() {
                @Override public void run() {
                    updatePlaylistMovies(playlistArr);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
